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
import Packet.Handlers.AuthUser;
import Packet.Protocol;
import Utility.Utility;

public class AnsAuthUser implements Category, Protocol
{
    public static byte[] createPacket(byte[] input, byte[] encKey)
    {
        byte message;

        if(AuthUser.authUserAndPassword(input, encKey))
            message = 0x00;
        else
            message = 0x01;

        return Utility.createShortPacket(Category.LOGIN, Protocol.S2C_ansAuthUser, message);
    }
}
