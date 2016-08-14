package songm.sso.backstage.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import songm.sso.backstage.entity.Protocol;

@ChannelHandler.Sharable
public class ProtocolCodec extends MessageToMessageCodec<ByteBuf, Protocol> {

    private static final Logger LOG = LoggerFactory.getLogger(ProtocolCodec.class);

    @Override
    protected void encode(ChannelHandlerContext ctx,
            Protocol pro, List<Object> list) throws Exception {
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
        if (pro.getBody() != null) {
            byteBuf.writeInt(Protocol.HEADER_LENGTH + pro.getBody().length);
            byteBuf.writeShort(Protocol.HEADER_LENGTH);
            byteBuf.writeShort(Protocol.VERSION);
            byteBuf.writeInt(pro.getOperation());
            byteBuf.writeLong(pro.getSequence());
            byteBuf.writeBytes(pro.getBody());
        } else {
            byteBuf.writeInt(Protocol.HEADER_LENGTH);
            byteBuf.writeShort(Protocol.HEADER_LENGTH);
            byteBuf.writeShort(Protocol.VERSION);
            byteBuf.writeInt(pro.getOperation());
            byteBuf.writeLong(pro.getSequence());
        }

        list.add(byteBuf);

        LOG.debug("encode: {}", pro);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx,
            ByteBuf byteBuf, List<Object> list) throws Exception {
        Protocol pro = new Protocol();
        pro.setPacketLen(byteBuf.readInt());
        pro.setHeaderLen(byteBuf.readShort());
        pro.setVersion(byteBuf.readShort());
        pro.setOperation(byteBuf.readInt());
        pro.setSequence(byteBuf.readLong());
        if (pro.getPacketLen() > pro.getHeaderLen()) {
            byte[] bytes = new byte[pro.getPacketLen() - pro.getHeaderLen()];
            byteBuf.readBytes(bytes);
            pro.setBody(bytes);
        }

        list.add(pro);

        LOG.debug("decode: {}", pro);
    }
}
