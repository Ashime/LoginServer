package Server.MsgCoder;

// @author Ashime

import Utility.Utility;
import Utility.Convert;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;

 
public class PacketEncoder extends MessageToMessageEncoder <byte[]>
{
    @Override
    protected void encode(ChannelHandlerContext ctx, byte[] msg, List<Object> out)
    {
        int msgSize;

        byte[] outPacket;
        byte[] sizeHeader;
        
        // Get size of the message.
        msgSize = msg.length;
        
        // Convert size from int into byte array.
        sizeHeader = Convert.intToByteArray(msgSize);

        // create a destination array that is the size of the two arrays
        outPacket = new byte[sizeHeader.length + msg.length];

        // copy sizeHeader at position 0 into outPacket at position 0 up to the length of sizeHeader.
        System.arraycopy(sizeHeader, 0, outPacket, 0, sizeHeader.length);

        // copy msg at position 0 into outPacket at position after sizerHeader up to the length of msg.
        System.arraycopy(msg, 0, outPacket, sizeHeader.length, msg.length);

        // Flip the first two bytes.
        outPacket = Utility.flip(outPacket, 0, 1);

        // Wrap byte[] in ByteBuffer and send outPacket into the out pool.
        out.add(Unpooled.wrappedBuffer(outPacket));
    }
}
