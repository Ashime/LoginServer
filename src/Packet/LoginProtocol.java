package Packet;

import FileIO.IniFile;
import FileIO.Log;
import Security.TEA;
import Sql.Query;
import Utility.Convert;

import java.util.Arrays;

// @author Ashime
// Notice: All packets are hard coded. Packet recognition system needs to be created.

public class LoginProtocol 
{
    IniFile ini = new IniFile();
    Log log = new Log();
    TEA tea = new TEA();
    Query query = new Query();
    Convert convert = new Convert();
    
    private byte[] createdPacket = {};
    private String ipWithNoDots, protocolWithNoDots;
    private byte[] dPassword = {};

    byte[] key = {00, 00, 36, 72}; // Need to dynamically generate key based on user.

    public LoginProtocol()
    {
        ipWithNoDots = ini.getConnectionIP().replaceAll(".", "");
        protocolWithNoDots = ini.getProtocol().replaceAll(".", "");
    }
   
   /* Packet Header:
      - Byte 1-2 tells the size of the remaining packet from byte 3 and on.
      - Byte 3 is server id/category
      - Byte 4 is typically message id/protocol
   */
    
    // 1. Recieve active connection from client.
    // 2. Send hello packet (Size: 72).
    public byte[] readyPacket =
    //{05, 00, 51, 00, 00, 36, 72};
    {05, 00, 51, 00, 00, 36, 72};
    
    // 3. Client sends packet with IP.
                // Format: Size, ServerID, MessageID, Protocol#, IP#, End Byte
                    // Example: 25 00 33 01 030406 3132372e302e302e3100000000000000000000000000000000000000000000 00
    
    // 4. Send accept packet (Size: 5).
        // Format: Size, ServerID, MessageID, Flag (00 - true, 01 - false)
    //private byte[] acceptPacket = {03, 00, 51, 02, 00};
    private byte[] acceptPacket = {03, 00, 51, 02, 00};
    private byte[] declinePacket = {03, 00, 51, 02, 01};
    
    // Size: 83 bytes
    // 5. Client will send packet with Username and Password (encrypted).
            // Username Length: 1-50 bytes      Password Length: 1-12 bytes
            // 51 00 33 03 00 00 00 00, [Username], 00, [Encrypted Password]
            // Size, ServerID, MessageID, [UserID?], ~
            // Username can be byte 9-59. Password can be byte 11-83 or 61-83.
    
    // 6. Send packet back with login status. Size: 71.
        // Format: Size, ServerID, MessageID, Flag, 00, ...
        // (byte 7 - 70 are message response) e.g "Incorrect Password"
            // Flag on byte 5 are 00 (correct login) or 01 (incorrect login)
    private byte[] correctLogin =
    {03, 00, 51, 14, 00};  
    // incorrect password packet.
    private byte[] incorrectLogin =
    {03, 00, 51, 14, 01};
        
    // 7. Client will send a confirmation packet.
        // Size, ServerID, (MessageID /Flag)?
    
    // 8. Send server list packet. Size: 83.
        // Server Name: Global (71, 108, 111, 98, 97, 108), Channel Name: Channel 1 (67, 104, 97, 110, 110, 101, 108, 32, 49)
        // Format: Size, ServerID, MessageID, Flag, (filler packet - Server Name), ServerID, MessageID + 1, (filler packet - Channel Name), Flag
    // One server and one channel
	
	/* Size (2 bytes)
	   Category (1 byte)
	   Protocol (1 byte)
	   # of Servers	(1 byte)
	   ServerName (33 bytes)
	   unknown (1 byte)
	   unknown (1 byte)
	   unknown (1 byte)
	   Size (2 bytes)
	   Category (1 byte)
	   Protocol +1 (1 byte)
	   # of Channels (1 byte)
	   ChannelName (33 bytes)
	   unknown (1 byte)
	   Channel# (1 byte)
	   unknown (1 byte)
	   */
    // One server and one channel
    private byte[] serverListPacket =
    {
         39, 00, 51, 17, 01, 71, 108, 111, 98, 97,
        108, 00, 00, 00, 00, 00,  00,  00, 00, 00,
         00, 00, 00, 00, 00, 00,  00,  00, 00, 00,
         00, 00, 00, 00, 00, 00,  00,  00, 01, 00,
         01, 40, 00, 51, 18, 01,  67, 104, 97,110,
        110,101,108, 32, 49, 00,  00,  00, 00, 00,
         49, 50, 00, 00, 00, 00,  00,  00, 00, 00,
         00, 00, 00, 00, 00, 00,  00,  00, 00, 01,
         01, 01, 01
    };

    /*
        Size (2 bytes)
        Category (1 byte - 17/0x11)
        Protocol (1 byte)
        # of Servers (1 byte)
        Server Name (32 bytes)
        unknown (1 byte)
        unknown (1 byte)
        unknown (1 byte)
        Size (2 bytes)
        Category (1 byte)
        Protocol (1 byte - 18/0x12)
        # of Channels (1 byte)
        Channel Name (32 bytes)
        Unknown (1 byte)
        Channel # (1 byte)
        Unknown (1 byte)
        Channel Name (32 bytes)
        Unknown (1 byte)
        Unknown (1 byte)
        Unknown (1 byte)
        Channel # (1 byte)
        Unknown (1 byte)
        Unknown (1 byte)
     */
    // One server and two channels
    private byte[] serverListPacket1 =
    {
         39, 00, 51, 17, 01, 71, 108, 111, 98, 97,
        108, 00, 00, 00, 00, 00,  00,  00, 00, 00,
         00, 00, 00, 00, 00, 00,  00,  00, 00, 00,
         00, 00, 00, 00, 00, 00,  00,  00, 01, 01,
         01, 77, 00, 51, 18, 02,  67, 104, 97,110,
        110,101,108, 32, 49, 00,  00,  00, 00, 00,
         00, 00, 00, 00, 00, 00,  00,  00, 00, 00,
         00, 00, 00, 00, 00, 00,  00,  00, 01, 01,
         01, 01, 00, 67,104, 97, 110, 110,101,108,
         32, 50, 00, 00, 00, 00,  00,  00, 00, 00,
         00, 00, 00, 00, 00, 00,  00,  00, 00, 00,
         00, 00, 00, 00, 00, 00,  01,  02, 01, 01
    };

    /*
        Size (2 bytes)
        Category (1 byte - 51/0x33)
        Protocol (1 byte - 17/0x11)
        # of Servers (1 byte - 01)
        Server Name (32 bytes)
        unknown (1 byte)
        unknown (1 byte)
        unknown (1 byte)
        Size (2 bytes)
        Unknown (1 byte)
        Category (1 byte - 51/0x33)
        Protocol (1 byte - 18/0x12)
        # of Channels (1 byte)
        Channel Name (32 bytes)
        Unknown (1 byte)
        Channel # (1 byte)
        Unknown (1 byte)
        Channel Name (32 bytes)
        Unknown (1 byte)
        Channel # (1 byte)
        Unknown (1 byte)
        Unknown (1 byte)
        Channel Name (32 bytes)
        Unknown (1 byte)
        Channel # (1 byte)
        Unknown (1 byte)
        Unknown (1 byte)
	   */
    // One server and three channels.
    private byte[] serverListPacket2 =
    {
            39, 00, 51, 17, 01, 71, 108, 111, 98, 97,
            108, 00, 00, 00, 00, 00,  00,  00, 00, 00,
            00, 00, 00, 00, 00, 00,  00,  00, 00, 00,
            00, 00, 00, 00, 00, 00,  00,  00, 01, 01,
            01, 112, 00, 51, 18, 03,  67, 104, 97,110,
            110,101,108, 32, 49, 00,  00,  00, 00, 00,
            00, 00, 00, 00, 00, 00,  00,  00, 00, 00,
            00, 00, 00, 00, 00, 00,  00,  00, 01, 01,
            01, 01, 00, 67,104, 97, 110, 110,101,108,
            32, 50, 00, 00, 00, 00,  00,  00, 00, 00,
            00, 00, 00, 00, 00, 00,  00,  00, 00, 00,
            00, 00, 00, 00, 00, 00,  01,  02, 01, 00,
            67,104, 97, 110,110, 101,108, 32, 51, 00,
            00, 00, 00, 00, 00, 00, 00, 00, 00, 00,
            00, 00, 00, 00, 00, 00, 00, 00, 00, 00,
            00, 00, 00, 01, 03, 01, 01
    };



    // Two servers, one channel on 1st server,
    // and two channels on 2nd server.
    private byte[] serverListPacket3 =
    {
        74, 00, 51, 17, 02, 49, 49, 49, 49, 49,
        49, 00, 00, 00, 00, 00, 00, 00, 00, 00,
        00, 00, 00, 00, 00, 00, 00, 00, 00, 00,
        00, 00, 00, 00, 00, 00, 00, 00, 01, 00,
        00, 50, 50, 50, 50, 50, 50, 00, 00, 00,
        00, 00, 00, 00, 00, 00, 00, 00, 00, 00,
        00, 00, 00, 00, 00, 00, 00, 00, 00, 00,
        00, 00, 00, 00, 02, 00,111, 00, 51, 18,
        03, 49, 49, 49, 49, 49, 49, 00, 00, 00,
        00, 00, 00, 00, 00, 00, 00, 00, 00, 00,
        00, 00, 00, 00, 00, 00, 00, 00, 00, 00,
        00, 00, 00, 00, 01, 01, 10, 00, 50, 50,
        50, 50, 50, 50, 00, 00, 00, 00, 00, 00,
        00, 00, 00, 00, 00, 00, 00, 00, 00, 00,
        00, 00, 00, 00, 00, 00, 00, 00, 00, 00,
        00, 02, 01, 10, 00, 50, 50, 50, 50, 50,
        50, 00, 00, 00, 00, 00, 00, 00, 00, 00,
        00, 00, 00, 00, 00, 00, 00, 00, 00, 00,
        00, 00, 00, 00, 00, 00, 00, 00, 02, 02,
        10
    };

    // PacketID, 00, ServerID, MessageID, Flag1, Server Name, 00, Flag2, 00, Flag3
    // 00, 00, ServerID, MessageID +1, Flag4, Channel Name, 00, Flag4, Flag3, Flag2, Flag1
    
    // 9. Client will send a server selection packet.
        // Format: PacketID, 00, ServerID, MessageID, Server#, Channel#
            // Example: 04 00 33 13 01 01
    
    // 10. Send Game Server IP to client. Size: 86 Port: 34, 78 (HEX for 20002 is 4e22
    private byte[] gsTransferPacket =
    {
        84, 00, 51, 26, 00, 30, 85, 77, 24, 48, 72, 96, 120, 00, 00, 00,
        00, 00, 8, 32, 56, 80, 104, 00, 00, 00, 00, 00, 00, 10, 40, 64,
        88, 112, 00, 00, 00, 00, 00, 00, 49, 50, 55, 46, 48, 46, 48, 46,
        49, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00,
        00, 00, 00, 00, 00, 00, 00, 00, 34, 78, 00, 00, 00, 55, 110, 54,
        103, 55, 55, 110, 107, 00
    };
    
    // IP: 127.0.0.1 (49, 50, 55, 46, 48, 46, 48, 46, 49) Port: 20002 (7834 - flipped)
    private byte[] gsTransferPacket1 =
    {
        84, 00, 51, 26, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00,
        00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00,
        00, 00, 00, 00, 00, 00, 00, 00, 49, 50, 55, 46, 48, 46, 48, 46,
        49, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00,
        00, 00, 00, 00, 00, 00, 00, 00, 34, 78, 00, 00, 00, 00, 00, 00,
        00, 00, 00, 00, 00, 00
    };
    
    public void createPacket(String type)
    {
        switch(type)
        {
            case "GS TRANSFER":
            {
                Log.formatLog(gsTransferPacket1, "[S2C] Transfer to Game Server");
                createdPacket = gsTransferPacket1;
                break;
            }  
            default: 
            {
                Log.formatLog(acceptPacket, "[S2C] Unknown Packet");
                createdPacket = acceptPacket;
            }
        }
    }
    
    public void decodeMessage(byte[] inPacket)
    {
        switch(inPacket[1]) // Protocol
        {
            case 15:
            {
                Log.formatLog(inPacket, "[C2S] Confirmed Login");
                createPacket("SERVER LIST");
                break;
            }
            case 19:
            {                     
                Log.formatLog(inPacket, "[C2S] Confirmed Server Selection");
                createPacket("GS TRANSFER");
                break;
            }
            default:
            {
                Log.formatLog(inPacket, "[C2S] Unknown Packet");
                break;
            }
        }
    }

    // Getter
    public byte[] getCreatedPacket() {
        return createdPacket;
    }
}