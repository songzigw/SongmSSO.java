package songm.sso.backstage.event;

import java.util.EventListener;

public abstract class ClientListener implements EventListener {

    protected abstract void onConnecting();

    protected abstract void onConnected();

    protected abstract void onDisconnected();

}
