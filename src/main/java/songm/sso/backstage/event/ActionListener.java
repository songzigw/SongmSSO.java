package songm.sso.backstage.event;

import java.util.EventListener;

public interface ActionListener<T> extends EventListener {

    public Long getSequence();

    public void actionPerformed(ActionEvent<T> event);
}
