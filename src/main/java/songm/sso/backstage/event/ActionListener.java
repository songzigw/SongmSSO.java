package songm.sso.backstage.event;

import java.util.EventListener;

public interface ActionListener extends EventListener {

    public Long getSequence();

    public void actionPerformed(ActionEvent event);
}
