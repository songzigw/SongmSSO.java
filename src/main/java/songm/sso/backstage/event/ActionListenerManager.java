package songm.sso.backstage.event;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import songm.sso.backstage.entity.Result;
import songm.sso.backstage.event.ActionEvent.EventType;

public class ActionListenerManager {

    private final Map<EventType, Set<ActionListener<?>>> listeners;

    public ActionListenerManager() {
        listeners = new EnumMap<EventType, Set<ActionListener<?>>>(EventType.class);
    }

    /**
     * 添加事件监听
     * 
     * @param listener
     */
    public void addListener(EventType eventType, ActionListener<?> listener) {
        Set<ActionListener<?>> lers = listeners.get(eventType);
        if (lers == null) {
            lers = new HashSet<ActionListener<?>>();
            listeners.put(eventType, lers);
        }
        lers.add(listener);
    }

    /**
     * 移除事件监听
     * 
     * @param listener
     */
    public void removeListener(EventType eventType, ActionListener<?> listener) {
        Set<ActionListener<?>> lers = listeners.get(eventType);
        if (lers == null) {
            return;
        }
        lers.remove(listener);
    }

    /**
     * 事件触发
     * 
     * @param type
     * @param res
     * @param sequence
     */
    public <T> void trigger(EventType type, Result<T> res, Long sequence) {
        Set<ActionListener<?>> lers = listeners.get(type);
        if (lers == null) {
            return;
        }

        ActionEvent<T> event = new ActionEvent<T>(type, res, sequence);
        for (ActionListener<?> l : lers) {
            if (l != null) {
                @SuppressWarnings("unchecked")
                ActionListener<T> ler = (ActionListener<T>) l;
                if (ler.getSequence() == null) {
                    ler.actionPerformed(event);
                } else if (ler.getSequence().equals(sequence)) {
                    ler.actionPerformed(event);
                    removeListener(type, ler);
                    break;
                }
            }
        }
    }
}
