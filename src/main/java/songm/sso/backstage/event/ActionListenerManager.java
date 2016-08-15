package songm.sso.backstage.event;

import java.util.Collection;
import java.util.HashSet;

public class ActionListenerManager {

    private Collection<ActionListener> listeners;

    public ActionListenerManager() {
        listeners = new HashSet<ActionListener>();
    }

    /**
     * 添加事件监听
     * 
     * @param listener
     */
    public void addListener(ActionListener listener) {
        if (listeners == null) {
            listeners = new HashSet<ActionListener>();
        }
        listeners.add(listener);
    }

    /**
     * 移除事件监听
     * 
     * @param listener
     */
    public void removeListener(ActionListener listener) {
        if (listeners == null)
            return;
        listeners.remove(listener);
    }

    public void trigger(ActionEvent event) {
        if (listeners == null)
            return;
    }
    
}
