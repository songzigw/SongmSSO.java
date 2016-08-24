package songm.sso.backstage.event;

import java.util.EventObject;

public class ActionEvent extends EventObject {

    private static final long serialVersionUID = -6605811937291134933L;

    private Long sequence;

    private Object data;

    public ActionEvent(EventType source, Object data) {
        super(source);
        this.data = data;
    }

    @Override
    public EventType getSource() {
        return (EventType) super.getSource();
    }

    public Object getData() {
        return data;
    }

    public Long getSequence() {
        return sequence;
    }

    public static enum EventType {
        /** 正在连接 */
        CONNECTING,
        /** 连接上了 */
        CONNECTED,
        /** 连接断开 */
        DISCONNECTED,
    }
}
