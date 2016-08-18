package songm.sso.backstage.event;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import songm.sso.backstage.event.ActionEvent.EventType;

public class ActionListenerManager {

    private final Map<EventType, Set<ActionListener>> listeners;

    public ActionListenerManager() {
        listeners = new EnumMap<EventType, Set<ActionListener>>(EventType.class);
    }

    /**
     * 添加事件监听
     * 
     * @param listener
     */
    public void addListener(EventType eventType, ActionListener listener) {
        Set<ActionListener> lers = listeners.get(eventType);
        if (lers == null) {
            lers = new HashSet<ActionListener>();
        }
        lers.add(listener);
    }

    /**
     * 移除事件监听
     * 
     * @param listener
     */
    public void removeListener(EventType eventType, ActionListener listener) {
        Set<ActionListener> lers = listeners.get(eventType);
        if (lers == null) {
            lers = new HashSet<ActionListener>();
        }
        lers.remove(listener);
    }

    /**
     * 事件触发
     * 
     * @param event
     */
    public void trigger(ActionEvent event) {
        EventType type = event.getSource();
        Set<ActionListener> lers = listeners.get(type);
        if (lers == null) {
            return;
        }

        for (ActionListener ler : lers) {
            if (ler != null) {
                ler.actionPerformed(event);
            }
        }
    }
}