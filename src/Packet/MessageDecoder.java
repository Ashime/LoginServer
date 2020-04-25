package Packet;
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
import Packet.S2C.AnsAuthUser;
import Packet.S2C.AnsServerList;
import Packet.S2C.AnsVerifyPacket;
import Utility.Convert;

public class MessageDecoder implements Category, Protocol
{
    private static byte[] userEncKey = new byte[4];
    private static byte[] outPacket = { };

    private static LoginProtocol login = new LoginProtocol();

    public static byte[] decodeMessage(byte[] inPacket)
    {
        switch (inPacket[1])
        {
            case Protocol.C2S_askVerify:
            {
                outPacket = AnsVerifyPacket.createPacket(inPacket);
                break;
            }
            case Protocol.C2S_askAuthUser:
            {
                outPacket = AnsAuthUser.createPacket(inPacket, userEncKey);
                break;
            }
            case Protocol.C2S_askSrvList:
            {
                outPacket = AnsServerList.createPacket();
                break;
            }
            case 19: {
                Log.formatLog(inPacket, "[C2S] Confirmed Server Selection");
                login.createPacket("GS TRANSFER");
                outPacket = login.getCreatedPacket();
                break;
            }
            default: {
                Log.formatLog(inPacket, "[C2S] Unknown Packet");
                break;
            }
        }

        return outPacket;
    }

    public static void setUserEncKey(byte[] userEncKey) {
        MessageDecoder.userEncKey = userEncKey;
    }
}
