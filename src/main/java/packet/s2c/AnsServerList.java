package packet.s2c;
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

import packet.Category;
import packet.Protocol;
import packet.coder.MessageEncoder;
import packet.handlers.GetServerList;
import utility.Utility;

import java.util.ArrayList;

public class AnsServerList implements Category, Protocol
{
    private static final int svrNumSize = 1;
    private static final int svrNameSize = 32;
    private static final byte unknown_svr = 0x00;

    private static final int chnNumSize = 1;
    private static final int chnNameSize = 33;
    private static final byte separator = 0x00;
    private static final byte terminator = 0x01;

    private static final int tailInfoSize = 3;

    /**
     * @return Returns a byte array packet with the server and channel info in one.
     */
    public static byte[] createPacket()
    {
        // Calls on GetServerList class to pull server list info from database.
        GetServerList.getList();

        byte[] tempMsg1 = assembleServerList(GetServerList.getServerNum(), GetServerList.getServerNames());
        byte[] tempMsg2 = assembleChannelList(GetServerList.getChannelNum(), GetServerList.getServerToChannelRelation());

        byte[] message = new byte[tempMsg1.length + tempMsg2.length];

        // Copying tempMsg1 at index 0 to max length into message starting at index 0.
        System.arraycopy(tempMsg1, 0, message, 0, tempMsg1.length);
        // Copying tempMsg2 at index 0 to max length into message starting at tempMsg1 max length.
        System.arraycopy(tempMsg2, 0, message, tempMsg1.length, tempMsg2.length);

        return message;
    }

    /**
     * @param svrNum   An integer number of all servers.
     * @param svrNames ArrayList of all server names.
     * @return A byte array of the server portion of the ansServerList packet with packet header attached.
     */
    private static byte[] assembleServerList(int svrNum, ArrayList<String> svrNames)
    {
        byte[] output;
        // Getting size for output.
        int messageSize = 0;

        // Add the size of svrNum, which is 1 byte.
        messageSize += svrNumSize;

        // Cycle through the amount of severs there are.
        for (int i = 1; i <= svrNum; i++)
        {
            // Add the size of the default name size (32 bytes).
            messageSize += svrNameSize;
            // Add the size of the default tail size (3 bytes).
            messageSize += tailInfoSize;

            // If it's not the last server to add, then add size of separator (1 byte).
            if (i != svrNum)
                messageSize += 1;
        }

        output = new byte[messageSize];

        // Add the byte data for svrNum.
        output[0] = (byte) svrNum;
        // temp output array index. Set to 1 because of svrNum at index 0.
        int outputIndex = 1;

        // Cycle through the svrName arraylist to build the SvrList packet.
        for (int j = 1; j <= svrNames.size(); j++)
        {
            // Push svrName to char array (temp).
            char[] temp = svrNames.get(j - 1).toCharArray();

            // Cycle through temp and convert each char into a byte.
            for (int k = 0; k < temp.length; k++)
            {
                output[outputIndex] = (byte) temp[k];
                outputIndex += 1;
            }

            // Need to reset outputIndex and set it to svrNameSize (32). Otherwise it will be temp.length + svrNameSize.
            outputIndex += (svrNameSize - temp.length);

            // Assemble the tail.
            output[outputIndex] = unknown_svr;
            output[outputIndex + 1] = (byte) (j);
            output[outputIndex + 2] = unknown_svr;

            // If there is more than one server, then do outputIndex + 4 (tail = 3 and 1 = separator).
            if (j != svrNum)
                outputIndex += 4;
        }

        // Build packet header and return packet.
        return MessageEncoder.createLongPacket(Category.LOGIN, Protocol.S2C_ansSrvList_Srv, output);
    }

    /**
     * @param chnNum         An integer number of all channels.
     * @param chnSvrRelation ArrayList of all channels with their relation to what servers.
     * @return A byte array of the channel portion of the ansServerList packet with packet header attached.
     */
    private static byte[] assembleChannelList(int chnNum, ArrayList<String> chnSvrRelation)
    {
        byte[] output;
        // Getting size for output.
        int messageSize = 0;

        // Add the size of chnNum, which is 1 byte.
        messageSize += chnNumSize;

        // Cycle through the amount of channels there are.
        for (int i = 1; i <= chnNum; i++) {
            // Add the size of the default name size (33 bytes).
            messageSize += chnNameSize;
            // Add the size of the default tail size (3 bytes).
            messageSize += tailInfoSize;

            // If it's not the last channel to add, then add size of separator (1 byte).
            if (i != chnNum)
                messageSize += 1;
        }

        output = new byte[messageSize];

        // Add the byte data for chnNum.
        output[0] = (byte) chnNum;

        // output array index.
        int outputIndex = 1;
        // chnIndex to help keep track of the channel number per server.
        int chnIndex = 1;
        int prevSvrNum = Integer.parseInt(chnSvrRelation.get(0));

        // Cycle through the chnSvrRelation arraylist to build the channel portion of the SvrList packet.
        for (int j = 0; j < chnSvrRelation.size(); j++) {
            // Checks if the current value in chnSvrRelation is a number and doesn't match the index 0 value.
            // Means that current chnSvrRelation value is a new server index.
            if (Utility.isStringInt(chnSvrRelation.get(j)) && !chnSvrRelation.get(j).matches(chnSvrRelation.get(1))) {
                // Change the prevSvrNum to new svrNum.
                prevSvrNum = Integer.parseInt(chnSvrRelation.get(j));
                // Reset the chnIndex due to server change.
                chnIndex = 1;
            }
            // Checks to see if current chnSvrRelation is not a number, therefore there is more channels.
            else if (!Utility.isStringInt(chnSvrRelation.get(j))) {
                // Push channel name to char array (temp).
                char[] temp = chnSvrRelation.get(j).toCharArray();

                // Cycle through temp and convert each char into a byte.
                for (int k = 0; k < temp.length; k++) {
                    output[outputIndex] = (byte) temp[k];
                    outputIndex += 1;
                }

                // Need to rest outputIndex and set it to chnNameSize (33). Otherwise it will be
                // temp.length + chnNameSize.
                outputIndex += ((chnNameSize - temp.length));

                // Assemble the tail.
                output[outputIndex] = (byte) prevSvrNum;
                output[outputIndex + 1] = (byte) chnIndex;
                output[outputIndex + 2] = terminator;

                // If there is more than one channel, then do outputIndex + 4 (tail = 3 and 1 = separator)
                // and adjust chnIndex.
                if (j != chnNum) {
                    outputIndex += 4;
                    chnIndex += 1;
                }
            }
        }
        // Build packet header and return packet.
        return MessageEncoder.createLongPacket(Category.LOGIN, Protocol.S2C_ansSrvList_Chn, output);
    }
}