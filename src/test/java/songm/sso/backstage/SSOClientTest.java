package songm.sso.backstage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import songm.sso.backstage.SSOException.ErrorCode;
import songm.sso.backstage.client.SSOClient;
import songm.sso.backstage.entity.Attribute;
import songm.sso.backstage.entity.Backstage;
import songm.sso.backstage.entity.Entity;
import songm.sso.backstage.entity.Session;
import songm.sso.backstage.event.ConnectionListener;
import songm.sso.backstage.event.ResponseListener;

public class SSOClientTest {
    
private ISSOClient ssoClient;

    private static final Logger LOG = LoggerFactory.getLogger(SSOClientTest.class);

    private String key = "zhangsong";
    private String secret = "1234567";
    
    private String host = "127.0.0.1";
    private int port = 9090;

    public SSOClientTest() {
        ssoClient = SSOClient.init(host, port);
        ssoClient.addListener(new ConnectionListener() {
            @Override
            public void onDisconnected(ErrorCode errorCode) {
                System.out.println("===============Disconnected" + errorCode.name());
            }
            
            @Override
            public void onConnecting() {
                System.out.println("===============Connecting");
            }
            
            @Override
            public void onConnected(Backstage backstage) {
                System.out.println("===============Connected" + backstage.getBackId());
                ssoClient.report(null, new ResponseListener<Session>() {
                    @Override
                    public void onSuccess(Session entity) {
                        System.out.println(entity.getSesId());
                        ssoClient.setAttribute(entity.getSesId(),
                                "code", "987654321",
                                new ResponseListener<Attribute>() {
                                    @Override
                                    public void onSuccess(Attribute ent) {
                                        System.out.println("---- " + ent.getSesId());
                                        System.out.println("---- " + ent.getValue());
                                        ssoClient.getAttribute(entity.getSesId(), key,
                                                new ResponseListener<Attribute>() {
                                                    @Override
                                                    public void onSuccess(
                                                            Attribute att) {
                                                        System.out.println(att.getValue());
                                                    }

                                                    @Override
                                                    public void onError(
                                                            ErrorCode errorCode) {
                                                        // TODO Auto-generated method stub
                                                        
                                                    }
                                                
                                                });
                                    }

                                    @Override
                                    public void onError(ErrorCode errorCode) {
                                        
                                    }
                                    
                                });
                    }
                    
                    @Override
                    public void onError(ErrorCode errorCode) {
                        System.out.println(errorCode.name());
                    }
                });
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
    
    public ISSOClient getSSOClient() {
        return ssoClient;
    }

    public static void main(String[] args) throws Exception {
        SSOClientTest test = new SSOClientTest();
        test.start();
    }
}
