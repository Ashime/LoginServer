package Server.Handlers;

// @author Ashime

import Security.TEA;
import Utility.Convert;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.net.SocketAddress;
import java.util.ArrayList;

/*
GENERAL INFORMATION:
This class is designed to handle all active sessions with currently
connected clients. Output: IP:Port,Key
*/

public class SessionHandler extends ChannelDuplexHandler
{
    private static ArrayList<String> clientSession = new ArrayList<String>();
    private static String user;
    
    @Override
    public void channelActive(ChannelHandlerContext ctx)
    {
        addClient(ctx.channel().remoteAddress());
        ctx.fireChannelActive();
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx)
    {
        String client = getUser(ctx.channel().remoteAddress());
        removeClient(client);
        ctx.close();
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
    {
        byte[] inPacket = (byte[]) msg;
        ctx.fireChannelRead(inPacket);
    }
    
    @Override
     public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
    {
        if(!ctx.channel().isOpen() || !ctx.channel().isActive())
            ctx.fireChannelInactive();
        
        ctx.writeAndFlush(msg);
    }
    
    public void addClient(SocketAddress address)
    {
        user = address.toString() + "," + Convert.byteArrayToHexString(TEA.generateKey());
        clientSession.add(user);
    }
    
    public void removeClient(String user)
    {
        clientSession.remove(user);
    }

    private String getUser(SocketAddress address)
    {
        String user = "";

        for(String temp : clientSession)
        {
            if(temp.contains(address.toString()))
            {
                user = temp;
                break;
            }
        }

        return user;
    }

    public static byte[] getKey(SocketAddress address)
    {
        byte[] key = new byte[4];

        for(String temp: clientSession)
        {
            if(temp.contains(address.toString()))
            {
                String[] userInfo = temp.split(",");
                key = Convert.hexStringToByteArray(userInfo[1]);
            }
        }

        return key;
    }

    public static int getActiveUsers()
    {
        return clientSession.size();
    }
}
