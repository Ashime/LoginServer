package utility;
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

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class Utility
{

    // -----------------------------------------------------------------------------------------------------------------
    //                                          @author CwaniX
    //                  Ragezone link: http://forum.ragezone.com/members/2000270863.html
    //                    GitHub link: https://github.com/CwaniX/OpenSUN-Emu
    //                    Description: These functions comes from his BytesUtils.java. These
    //                                 functions are used for the Tea.java he created. I only
    //                                 merged his file with mine.
    // -----------------------------------------------------------------------------------------------------------------
    //                                      START OF TEA FUNCTIONS
    // -----------------------------------------------------------------------------------------------------------------

    public static void strncpy(byte[] input, byte[] output, int start)
    {
        for (int i = 0; i < input.length; i++)
        {
            output[i + start] = input[i];
        }
    }

    public static int byteArrayToInt(byte[] input)
    {
        return ByteBuffer.wrap(input).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    public static byte[] intToByteArray(int... input)
    {
        ByteBuffer buffer = ByteBuffer.allocate(input.length * 4).order(ByteOrder.LITTLE_ENDIAN);

        for (int i : input)
        {
            buffer.putInt(i);
        }

        return buffer.array();
    }

    public static String byteArrayToHexString(byte[] input)
    {
        StringBuilder sb = new StringBuilder();

        for (byte b : input) {
            sb.append(String.format("%02x ", b));
        }

        return sb.toString();
    }

    public static byte[] cutTail(byte[] input)
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int counter = 0;

        while (counter < input.length)
        {
            if (input[counter] != 0x00)
            {
                out.write(input[counter]);
                counter++;
            }
            else {
                break;
            }
        }

        return out.toByteArray();
    }

    // -----------------------------------------------------------------------------------------------------------------
    //                                        END OF TEA FUNCTIONS
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * @param input - The byte[] that you wish to get the index of a certain value.
     * @param value - The value that you want the index of.
     * @return - Returns the index of the value.
     */
    public static int indexOf(byte[] input, String value)
    {
        String inString = Convert.byteArrayToHexString(input);
        String transString = inString.replaceAll(" ", "");

        if (!transString.contains(value))
            return transString.length();
        else
            return (transString.indexOf(value) / 2);
    }

    /**
     * @param input  - The byte[] that you wish to split.
     * @param index1 - Starting index.
     * @param index2 - Ending index.
     * @return - Returns a byte[] that was split from the input.
     */
    public static byte[] split(byte[] input, int index1, int index2)
    {
        return Arrays.copyOfRange(input, index1, index2);
    }

    /**
     * @param input  - The byte[] that you wish to flip some bytes in.
     * @param index1 - The index of the byte you wish to flip.
     * @param index2 - The index of the second byte you wish to flip.
     * @return - Returns a byte[] of the input with flipped bytes.
     */
    public static byte[] flip(byte[] input, int index1, int index2)
    {
        byte[] output = Arrays.copyOfRange(input, 0, input.length);

        byte b1 = input[index1];
        byte b2 = input[index2];

        output[index1] = b2;
        output[index2] = b1;

        return output;
    }

    public static boolean isStringInt(String s)
    {
        try
        {
            Integer.parseInt(s);
            return true;
        }
        catch (NumberFormatException ex) {
            return false;
        }
    }

    public static String getIp(String unfilteredIp)
    {
        String ipPort = unfilteredIp.replace("/", "");
        String[] splitIpPort = ipPort.split(":");
        return splitIpPort[0];
    }
}
