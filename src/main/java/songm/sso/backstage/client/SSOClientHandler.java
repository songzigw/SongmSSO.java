package songm.sso.backstage.client;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import songm.sso.backstage.JsonUtils;
import songm.sso.backstage.entity.Backstage;
import songm.sso.backstage.entity.Protocol;
import songm.sso.backstage.event.ActionEvent;
import songm.sso.backstage.event.ActionListenerManager;

@ChannelHandler.Sharable
public class SSOClientHandler extends SimpleChannelInboundHandler<Protocol> {

    private static final Logger LOG = LoggerFactory
            .getLogger(SSOClientHandler.class);

    private ActionListenerManager listenerManager;

    public SSOClientHandler(ActionListenerManager listenerManager) {
        this.listenerManager = listenerManager;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Protocol pro)
            throws Exception {
        LOG.debug("messageReceived: {}", pro);
        int op = pro.getOperation();
        if (op == 1) {
            Backstage back = JsonUtils.fromJson(pro.getBody(), Backstage.class);
            ActionEvent event = new ActionEvent(
                    ActionEvent.EventType.CONNECTED, back);
            listenerManager.trigger(event);
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        LOG.debug("handlerRemoved: {}", ctx);
        ActionEvent event = new ActionEvent(ActionEvent.EventType.DISCONNECTED,
                null);
        listenerManager.trigger(event);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        LOG.error("exceptionCaught", cause);
    }
}
