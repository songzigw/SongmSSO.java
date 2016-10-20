package songm.sso.backstage.event;

public abstract class AbstractListener<T> implements ActionListener<T> {

    @Override
    public Long getSequence() {
        return null;
    }

}
