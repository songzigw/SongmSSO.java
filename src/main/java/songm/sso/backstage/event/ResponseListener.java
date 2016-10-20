package songm.sso.backstage.event;

import java.util.EventListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import songm.sso.backstage.SSOException;
import songm.sso.backstage.SSOException.ErrorCode;

/**
 * 响应事件监听器
 *
 * @author zhangsong
 * @since 0.1, 2016-8-2
 * @version 0.1
 *
 */
public class ResponseListener<T> implements EventListener {

    private static final Logger LOG = LoggerFactory.getLogger(ResponseListener.class);

    private boolean flag;
    private Boolean succeed;
    private T data;
    private ErrorCode errCode;

    public void onSuccess(T data) {
        this.succeed = true;
        this.data = data;
        this.flag = true;
    }

    public void onError(ErrorCode errCode) {
        this.succeed = false;
        this.errCode = errCode;
        this.flag = true;
    }

    public T handle() throws SSOException {
        long execute = System.currentTimeMillis();
        while (true) {
            LOG.info("Response listener flag: {}", this.flag);
            if (this.flag) break;
            if (System.currentTimeMillis() - execute > 10000)
                throw new SSOException(ErrorCode.TIMEOUT, "timeout");
        }

        if (!this.succeed) {
            throw new SSOException(errCode, errCode.name());
        }
        return this.data;
    }
    
}
