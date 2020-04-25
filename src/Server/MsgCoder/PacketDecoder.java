package Server.MsgCoder;

// @author Ashime

import Utility.Utility;
import Utility.Convert;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;


public class PacketDecoder extends ChannelInboundHandlerAdapter
{
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
    {
        byte[] inPacket = (byte[]) msg;
        decodePacket(ctx, inPacket);
    }
    
    private static void decodePacket(ChannelHandlerContext ctx, byte[] msg)
    {
        byte[] bSize;
        int size;
        
        // Flip the size bytes around.
        msg = Utility.flip(msg, 0, 1);
        
        // Split the size header off.
        bSize = Utility.split(msg, 0, 2);
        
        // Convert size from byte array into int.
        size = Convert.byteArrayToInt(bSize);
        
        // Split message from size header
        msg = Utility.split(msg, 2, msg.length);
       
        if(msg.length == size)
            // Passes the message down the pipeline.
            ctx.fireChannelRead(msg);  
        else
            // Drops the packet.
            ctx.fireChannelReadComplete();
    }
}
