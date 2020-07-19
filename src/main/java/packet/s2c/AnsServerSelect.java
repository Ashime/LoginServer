package packet.s2c;
/*
    @project LoginServer
    @author Ashime
    Created on 4/26/2020.

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

import packet.Category;
import packet.Protocol;
import packet.coder.MessageEncoder;
import packet.handlers.GetConnectionInfo;
import utility.Convert;
import utility.Utility;

import java.util.ArrayList;

public class AnsServerSelect implements Category, Protocol
{
    // Size: 36 bytes.
    private static final byte[] unknown =
    {
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00
    };

    private static final int ipSize = 32;
    private static final int portSize = 2;
    private static final byte[] unknown1 = {0x00, 0x00, 0x00};
    private static byte[] port = new byte[portSize];

    public static byte[] createPacket(byte[] input)
    {
        int msgSize = unknown.length + ipSize + portSize + unknown1.length;
        byte[] message = new byte[msgSize];

        ArrayList<String> connectionInfo = GetConnectionInfo.getInfo(input);

        int index = unknown.length;

        char[] temp = connectionInfo.get(0).toCharArray();

        for (char c : temp)
        {
            message[index] = (byte) c;
            index++;
        }

        index = unknown.length + ipSize;

        byte[] temp1 = Convert.hexStringToByteArray(Integer.toHexString(Integer.parseInt(connectionInfo.get(1))));
        port = Utility.flip(temp1, 0, 1);

        System.arraycopy(port, 0, message, index, port.length);

        index += port.length;
        System.arraycopy(unknown1, 0, message, index, unknown1.length);

        return MessageEncoder.createLongPacket(Category.LOGIN, Protocol.S2C_ansSrvSelect, message);
    }
}
