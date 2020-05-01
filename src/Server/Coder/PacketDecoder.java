package Server.Coder;

/*
    @project LoginServer
    @author Ashime
    Created on 05/22/2019.

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

import Utility.Convert;
import Utility.Utility;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;


public class PacketDecoder extends ChannelInboundHandlerAdapter
{
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

        if (msg.length == size)
            // Passes the message down the pipeline.
            ctx.fireChannelRead(msg);
        else
            // Drops the packet.
            ctx.fireChannelReadComplete();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
    {
        byte[] inPacket = (byte[]) msg;
        decodePacket(ctx, inPacket);
    }
}
