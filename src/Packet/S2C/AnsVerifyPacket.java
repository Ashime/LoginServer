package Packet.S2C;
/*
    @project LoginServer
    @author Ashime
    Created on 4/21/2020.

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

import Packet.Category;
import Packet.Handlers.VerifyIpProtocol;
import Packet.Protocol;
import Utility.Utility;

public class AnsVerifyPacket implements Category, Protocol
{
    public static byte[] createPacket(byte[] inPacket)
    {
        byte message;

        if(VerifyIpProtocol.verify(inPacket))
            message = 0x00;
        else
            message = 0x001;

        return Utility.createShortPacket(Category.LOGIN, Protocol.S2C_ansVerify, message);
    }
}
