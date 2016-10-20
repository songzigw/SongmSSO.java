package songm.sso.backstage.event;

import songm.sso.backstage.SSOException.ErrorCode;
import songm.sso.backstage.entity.Result;

public class SequenceListener<T> implements ActionListener<T> {

    private Long sequence;
    private ResponseListener<T> response;
    
    public SequenceListener(long sequence) {
        this.sequence = sequence;
        this.response = new ResponseListener<T>();
    }
    
    public ResponseListener<T> getResponse() {
        return response;
    }

    @Override
    public Long getSequence() {
        return this.sequence;
    }

    @Override
    public void actionPerformed(ActionEvent<T> event) {
        Result<T> res = event.getResult();
        if (res.getSucceed()) {
            response.onSuccess(res.getData());
        } else {
            response.onError(ErrorCode.valueOf(res.getErrorCode()));
        }
    }

}
