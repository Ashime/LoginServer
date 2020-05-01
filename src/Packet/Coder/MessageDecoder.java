package Packet.Coder;
/*
    @project LoginServer
    @author Ashime
    Created on 4/20/2020.

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

import FileIO.Log;
import Packet.Category;
import Packet.Protocol;
import Packet.S2C.AnsAuthUser;
import Packet.S2C.AnsServerList;
import Packet.S2C.AnsServerSelect;
import Packet.S2C.AnsVerifyPacket;

public class MessageDecoder implements Category, Protocol
{
    private static byte[] userEncKey = new byte[4];
    private static String userIpAddress;
    private static byte[] outPacket = {};

    public static byte[] decodeMessage(byte[] inPacket)
    {
        switch (inPacket[1])
        {
            case Protocol.C2S_askVerify:
            {
                Log.formatLog(inPacket, "[C2S] askVerify");
                outPacket = AnsVerifyPacket.createPacket(inPacket);
                Log.formatLog(outPacket, "[S2C] ansVerify");
                break;
            }
            case Protocol.C2S_askAuthUser:
            {
                Log.formatLog(inPacket, "[C2S] askAuthUser");
                outPacket = AnsAuthUser.createPacket(inPacket, userEncKey, userIpAddress);
                Log.formatLog(outPacket, "[S2C] ansAuthUser");
                break;
            }
            case Protocol.C2S_askSrvList:
            {
                Log.formatLog(inPacket, "[C2S] askServerList");
                outPacket = AnsServerList.createPacket();
                Log.formatLog(outPacket, "[S2C] ansServerList");
                break;
            }
            case Protocol.C2S_askSrvSelect:
            {
                Log.formatLog(inPacket, "[C2S] askServerSelect");
                outPacket = AnsServerSelect.createPacket(inPacket);
                Log.formatLog(outPacket, "[S2C] ansServerSelect");
                break;
            }
            default:
            {
                Log.formatLog(inPacket, "[C2S] Unknown Packet");
                break;
            }
        }

        return outPacket;
    }


    // SETTERS
    public static void setUserEncKey(byte[] userEncKey) {
        MessageDecoder.userEncKey = userEncKey;
    }

    public static void setUserIpAddress(String userIpAddress) {
        MessageDecoder.userIpAddress = userIpAddress;
    }
}
