package Server;

import FileIO.IniFile;
import Interface.Console;
import Server.MsgCoder.PacketDecoder;
import Server.Handlers.PacketHandler;
import Server.Handlers.SessionHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ipfilter.UniqueIpFilter;
import io.netty.handler.timeout.IdleStateHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

// @author Ashime

/*
GENERAL INFORMATION:
1. NettyNio.io
    This 3rd library is being used to improve performance by using less resources,
    and better throughput to lower latency.
    >> Website Link:        https://netty.io/
    >> Documentation Link:  https://netty.io/wiki/index.html
    >> Download Link:       https://netty.io/downloads.html

TODO:
1. When client does RST packets, the server produces errors. It is either due to child options or
the PacketHandler needs a method to handle the exception.
2. Commenting
*/

public class NettyNio
{
    private static IniFile ini;
    Console console = new Console();
    
    /*==============================
                 Client
    ===============================*/
    private  static EventLoopGroup clientAcceptor;
    private static EventLoopGroup clientWorker;
    private static ServerBootstrap clientBoot;
    private static ChannelFuture clientChannel;
    
    private final int clientAcceptionThreads = ini.getClientAcceptingThreads();
    private final int clientWorkingThreads = ini.getClientWorkingThreads(); 
    
    private String connectionIP = ini.getConnectionIP();
    private  int connectionPort = ini.getConnectionPort();
    
    private final boolean uniqueIpFilter = ini.isUniqueIpFilter();
    private final int ping = ini.getPing();
    private final int disconnect = ini.getDisconnect();
    
    /*=============================
                Server
    ==============================*/
    private static EventLoopGroup serverAcceptor;
    private static EventLoopGroup serverWorker;
    private static ServerBootstrap serverBoot;
    private static ChannelFuture serverChannel;
    
    private int serverAcceptionThreads = ini.getServerAcceptingThreads();
    private int serverWorkingThreads = ini.getServerWorkingThreads();
    
    private final String serverIP = ini.getServerIP();
    private final  int serverPort = ini.getServerPort();
    
    
    public void start()
    {   
        try
        {
            /*==================================================================
                                    CLIENT TO SERVER
            ===================================================================*/
            clientAcceptor = new NioEventLoopGroup(clientAcceptionThreads);
            clientWorker = new NioEventLoopGroup(clientWorkingThreads);

            clientBoot = new ServerBootstrap();
            clientBoot.group(clientAcceptor, clientWorker).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer()
            {
                @Override
                protected void initChannel(Channel ch) throws Exception
                {
                    // TODO: IMPORTANT: Need some type of firewall or ip checker to block clients from connecting.
                    
                    // UniqueIpFilter only allows one IP per channel, so a client cannot connect more than once.
                    if(uniqueIpFilter) { ch.pipeline().addLast(new UniqueIpFilter()); }
                    
                    // Inactivity Handler. Ping the client every # seconds of inactivity and DC client after # seconds Check the Common.ini.
                    ch.pipeline().addLast("idleStateHandler", new IdleStateHandler(disconnect, ping, 0));
                    
                    // Encoder/Decoder for converting ByteBuf into byte[] and vise versa.
                    ch.pipeline().addLast("byteDecoder", new ByteArrayDecoder());
                    ch.pipeline().addLast("byteEncoder", new ByteArrayEncoder());

                    // Session Handler
                    ch.pipeline().addLast("sessionHandler", new SessionHandler());

                    // PacketDecoder checks, split, and passes packets down.
                    ch.pipeline().addLast("packetDecoder", new PacketDecoder());
                    // PacketEncoder checks, adds, and pushes packets up.
//                    ch.pipeline().addLast("packetEncoder", new PacketEncoder());
                    
                    // Packet Handler will check and pass the packets their corresponding classes.
                    ch.pipeline().addLast("packetHandler", new PacketHandler());
                }
            }).childOption(ChannelOption.TCP_NODELAY, true).childOption(ChannelOption.AUTO_READ, true);
            
            clientChannel = clientBoot.bind(connectionIP, connectionPort).sync();
            console.displayMessage("INFO", "Client connection address - " + connectionIP + ":" + connectionPort);
            
            /*==================================================================
                                    SERVER TO SERVER
            ===================================================================*/
            serverAcceptor = new NioEventLoopGroup(serverAcceptionThreads);
            serverWorker = new NioEventLoopGroup(serverWorkingThreads);
            
            serverBoot = new ServerBootstrap();
            serverBoot.group(serverAcceptor, serverWorker).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer() {
                @Override
                protected void initChannel(Channel ch) throws Exception
                {
                    // TODO: Need to add ip filter to only accept ip connects. Basically a WhiteList.
                    //ch.pipeline().addFirst("serverFirewall", new RuleBasedIpFilter(new ServerFirewall()));
                    
                    // Not given a choice on whether to enable it. For security reasons, the Server To Server address will only
                    // accept one connection per IP.
                    ch.pipeline().addLast(new UniqueIpFilter());
                    
                    // Frame Decoder to break incoming continuous packets up by frames so further decoders can separate
                    // each message accordingly.
                    ch.pipeline().addLast("frameDecoder", new DelimiterBasedFrameDecoder(1024, Delimiters.nulDelimiter()));
                    
                    // Server to Server will be doing message to message packets.
                    ch.pipeline().addLast("stringDecoder", new StringDecoder());
                    ch.pipeline().addLast("stringEncoder", new StringEncoder());
                    
                    // TODO: Add Handler.
                }
            });
            
            serverChannel = serverBoot.bind(serverIP, serverPort).sync();
            console.displayMessage("INFO", "Server connection address - " + serverIP + ":" + serverPort);
            //console.displayMessage(1, "Server is waiting for connections...");
        } catch (InterruptedException ex) {
            Logger.getLogger(NettyNio.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void stop()
    {
        if(clientChannel != null)
            clientChannel.channel().closeFuture();
        
        if(serverChannel != null)
            serverChannel.channel().closeFuture();
        
        if(clientAcceptor != null)
            clientAcceptor.shutdownGracefully();
       
        if(serverAcceptor != null)
            serverAcceptor.shutdownGracefully();
        
        if(clientWorker != null)
            clientWorker.shutdownGracefully();
        
        if(serverWorker != null)
            serverWorker.shutdownGracefully();
        
        console.displayMessage("INFO", "Server has successfully shutdown!");
    }
}
