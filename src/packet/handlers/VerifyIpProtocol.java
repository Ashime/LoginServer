package packet.handlers;

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

import file.io.IniFile;
import utility.Convert;

public class VerifyIpProtocol
{
    public static boolean verify(byte[] input)
    {
        String hexPacket = Convert.byteArrayToHexString(input);
        String serverIP = IniFile.getConnectionIP().replaceAll(".", "");
        String clientProtocol = IniFile.getProtocol().replaceAll(".", "");

        return hexPacket.contains(clientProtocol) && hexPacket.contains(serverIP);
    }
}
