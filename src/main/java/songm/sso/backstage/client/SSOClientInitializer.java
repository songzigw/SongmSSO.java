/*
 * Copyright (c) 2016, zhangsong <songm.cn>.
 *
 */

package songm.sso.backstage.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * Tcp管道初始化
 *
 * @author zhangsong
 * @since 0.1, 2016-8-9
 * @version 0.1
 * 
 */
public class SSOClientInitializer extends ChannelInitializer<SocketChannel> {

    private ProtocolCodec protocolCodec;
    private SSOClientHandler clientHandler;

    public SSOClientInitializer() {
        protocolCodec = new ProtocolCodec();
        clientHandler = new SSOClientHandler();
    }
    
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(protocolCodec);
        ch.pipeline().addLast(clientHandler);
    }

}
