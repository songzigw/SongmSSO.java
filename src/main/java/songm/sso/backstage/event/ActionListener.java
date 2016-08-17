package songm.sso.backstage.event;

import java.util.EventListener;

public interface ActionListener extends EventListener {

    public void setSequence(long sequece);

    public Long getSequece();

    public void actionPerformed(ActionEvent event);
}
