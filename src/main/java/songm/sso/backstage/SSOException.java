package songm.sso.backstage;

public class SSOException extends Exception {

    private static final long serialVersionUID = 5118981894942473582L;

    public SSOException(String message) {
        super(message);
    }

    public SSOException(String message, Throwable cause) {
        super(message, cause);
    }

}
