/*
 * Copyright (c) 2016, zhangsong <songm.cn>.
 *
 */

package songm.sso.backstage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 
 * 客户端
 * 
 * @author zhangsong
 * @since 0.1, 2016-8-3
 * @version 0.1
 * 
 */
public class Client extends Thread {

    // 应用账号信息
    private Account account;

    private String serviceIP;
    private int servicePost;

    private Socket soc;
    // 客户端接收服务亲数据流
    private BufferedReader br;
    // 客户端发送数据到服务器
    private PrintStream ps;

    // 侦听服务器消息状态标识
    private boolean running;

    public Client(Account account, String ip, int post) {
        this.account = account;
        this.serviceIP = ip;
        this.servicePost = post;
    }

    public boolean isRunning() {
        return running;
    }

    public void connect() throws UnknownHostException, IOException {
        soc = new Socket(serviceIP, servicePost);
        ps = new PrintStream(soc.getOutputStream());
        ps.println(account);
        ps.flush();
        br = new BufferedReader(new InputStreamReader(soc.getInputStream()));

        // 启动线程
        this.start();
    }

    public void disconnect() {
        ps.println("exit");
        ps.flush();
        try {
            // 关闭套接字
            soc.close();
        } catch (IOException e) {
        } finally {
            soc = null;
        }
        this.running = false;
    }

    public void sendMessage() {
        ps.println("abc");
        ps.flush();
    }

    public void run() {
        this.running = true;
        String msg = null;
        try {
            while (running) {
                // 读取从服务器传来的信息
                msg = br.readLine();
                System.out.println("receive msg: " + msg);
                // 如果从服务器传来的信息为空则断开此次连接
                if (msg == null) {
                    this.disconnect();
                    return;
                }
            }
        } catch (IOException e) {
            this.disconnect();
        }
    }

    public static void main(String[] args) {

    }
}

class Account {
    private String key;
    private String secret;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}