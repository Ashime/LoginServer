package Main;

import FileIO.IniFile;
import Interface.Console;
import Packet.Handlers.GetServerList;
import Packet.S2C.AnsServerList;
import Security.TEA;
import Server.NettyNio;
import Sql.Connect;
import Sql.Query;
import Utility.Convert;

import java.sql.SQLException;

// @author Ashime

/*
GENERAL INFORMATION:
1. Operating Systems Supported
    a. Windows
        This application fully supports Windows 10.

TODO:
1. Upgrading to Java FX.
2. Allow Linux support the following classes:
    a. NettyNio.java (NettyNio NioServer)
        Windows: NioEventLoopGroup
        Linux: EpollEventLoopGroup
*/

public class LoginServer
{    
    public static void main(String[] args)
    {   
        // Setup for console interface. Allows access to display messages on the console.
        Console console = new Console();
        console.initConsole();  // Initializes the interface.
        
        // Initializes and pulls all the data from Common.ini and LoginServer.ini.
        IniFile ini = new IniFile();
        ini.parseIni();
        
        Console.displayMessage("MSG", "==========================================================");
        Console.displayMessage("MSG", "               SUN Online Login Server");
        Console.displayMessage("MSG", "               Client Version: " + ini.getVersion());
        Console.displayMessage("MSG", "==========================================================");
         
        Connect connect = new Connect();
        connect.testConnection();   // Tests database connection.
        
        if(IniFile.isNoLoginAuth())
            Console.displayMessage("WARN", "Login authentication has been disabled!");

//        byte[] temp = new byte[1];
        AnsServerList.createPacket();

        NettyNio server = new NettyNio();
        server.start();            // Starts up the C2S and S2S logic and business.
    }
}
