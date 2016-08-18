package songm.sso.backstage.event;

import java.util.EventListener;

public abstract class ClientListener implements EventListener {

    public abstract void onConnecting();

    public abstract void onConnected();

    public abstract void onDisconnected();

}
