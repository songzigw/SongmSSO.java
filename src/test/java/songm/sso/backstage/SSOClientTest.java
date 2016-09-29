package songm.sso.backstage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import songm.sso.backstage.SSOException.ErrorCode;
import songm.sso.backstage.client.SSOClientImpl;
import songm.sso.backstage.entity.Backstage;
import songm.sso.backstage.entity.Session;
import songm.sso.backstage.event.ConnectionListener;
import songm.sso.backstage.event.ResponseListener;

public class SSOClientTest {
    
    private static final Logger LOG = LoggerFactory.getLogger(SSOClientTest.class);

    private String key = "zhangsong";
    private String secret = "1234567";
    
    private String host = "127.0.0.1";
    private int port = 9090;

    private SSOClient ssoClient;
    public SSOClientTest() {
        ssoClient = SSOClientImpl.init(host, port);
        init();
    }
    
    public void init() {
        ssoClient.addListener(new ConnectionListener() {
            @Override
            public void onDisconnected(ErrorCode errorCode) {
                System.out.println("===============Disconnected: " + errorCode.name());
            }
            
            @Override
            public void onConnecting() {
                System.out.println("===============Connecting");
            }
            
            @Override
            public void onConnected(Backstage backstage) {
                System.out.println("===============Connected: " + backstage.getBackId());
                ssoClient.report(null, new ResponseListener<Session>() {
                    @Override
                    public void onSuccess(Session entity) {
                        System.out.println("===============Success: " + entity.getSesId());
                    }
                    
                    @Override
                    public void onError(ErrorCode errorCode) {
                        System.out.println("===============Error: " + errorCode.name());
                    }
                });
                System.out.println("===============Report end");
            }
        });
    }
    
    public void start() {
        try {
            ssoClient.connect(key, secret);
        } catch (SSOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public void stop() {
        ssoClient.disconnect();
    }
    
    public SSOClient getSSOClient() {
        return ssoClient;
    }

    public static void main(String[] args) throws Exception {
        SSOClientTest test = new SSOClientTest();
        test.start();
    }
}
