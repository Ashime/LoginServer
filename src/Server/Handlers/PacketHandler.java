package Server.Handlers;

import FileIO.IniFile;
import Interface.Console;
import Packet.Category;
import Packet.MessageDecoder;
import Packet.Protocol;
import Packet.S2C.AnsReadyPacket;
import Utility.Convert;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

// @author Ashime
// TODO: Commenting

public class PacketHandler extends ChannelDuplexHandler
{
    private ByteBuf buffer;
    private final int bufferSize = IniFile.getBufferSize();

    private static byte[] outPacket;
    
    @Override
    public void channelActive(ChannelHandlerContext ctx)
    {
        buffer = ctx.alloc().buffer(bufferSize);

        AnsReadyPacket ansReadyPacket = new AnsReadyPacket();
        outPacket = ansReadyPacket.createPacket(SessionHandler.getKey(ctx.channel().remoteAddress()));
        ctx.write(outPacket);

    }   
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
    {
        byte[] inPacket = (byte[]) msg;

        if(inPacket[0] == Category.LOGIN)
        {
            // If protocol matches for authenticating user, then pass user's key to MessageDecoder.
            if(inPacket[1] == Protocol.C2S_askAuthUser)
                MessageDecoder.setUserEncKey(SessionHandler.getKey(ctx.channel().remoteAddress()));

            outPacket = MessageDecoder.decodeMessage(inPacket);
            System.out.println("PH - OutPacket: " + Convert.byteArrayToHexString(outPacket));
            ctx.write(outPacket);
        }
        else
        {
            System.out.println("Unknown category!");
            System.out.println(Convert.byteArrayToHexString(inPacket));
            ctx.fireChannelReadComplete();
        }
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    {
        Console.displayMessage("ERR", cause.toString());
        cause.printStackTrace();

        ctx.flush();
        ctx.close();
    }
}
