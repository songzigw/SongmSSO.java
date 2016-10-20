package songm.sso.backstage.event;

import java.util.EventObject;

import songm.sso.backstage.entity.Result;

public class ActionEvent<T> extends EventObject {

    private static final long serialVersionUID = -6605811937291134933L;

    private Long sequence;

    private Result<T> result;

    public ActionEvent(EventType source, Result<T> result, Long sequece) {
        super(source);
        this.result = result;
        this.sequence = sequece;
    }

    @Override
    public EventType getSource() {
        return (EventType) super.getSource();
    }

    public Result<T> getResult() {
        return result;
    }

    public Long getSequence() {
        return sequence;
    }

    public static enum EventType {
        /** 请求后应答 */
        RESPONSE,
        
        /** 正在连接 */
        CONNECTING,
        /** 连接上了 */
        CONNECTED,
        /** 连接断开 */
        DISCONNECTED,
    }
}
