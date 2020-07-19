package server.handlers;

/*
    @project LoginServer
    @author Ashime
    Created on 6/22/2019.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

import file.Log;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import packet.Category;
import packet.Protocol;
import packet.coder.MessageDecoder;
import packet.s2c.AnsReadyPacket;
import ui.Console;
import utility.Utility;

// TODO: Commenting

public class PacketHandler extends ChannelDuplexHandler
{
    private static byte[] outPacket;

    @Override
    public void channelActive(ChannelHandlerContext ctx)
    {
        AnsReadyPacket ansReadyPacket = new AnsReadyPacket();
        outPacket = ansReadyPacket.createPacket(SessionHandler.getKey(ctx.channel().remoteAddress()));
        Log.formatLog(outPacket, "[S2C] AnsReady Packet");
        ctx.write(outPacket);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
    {
        byte[] inPacket = (byte[]) msg;

        if (inPacket[0] == Category.LOGIN)
        {
            // If protocol matches for authenticating user, then pass user's key to MessageDecoder.
            if (inPacket[1] == Protocol.C2S_askAuthUser)
            {
                MessageDecoder.setUserEncKey(SessionHandler.getKey(ctx.channel().remoteAddress()));
                MessageDecoder.setUserIpAddress(Utility.getIp(ctx.channel().remoteAddress().toString()));
            }

            outPacket = MessageDecoder.decodeMessage(inPacket);
            ctx.write(outPacket);
        }
        else
        {
            Log.formatLog(inPacket, "Unknown category!");
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
