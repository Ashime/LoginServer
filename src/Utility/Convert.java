package Utility;

// @author Ashime

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

 
public class Convert
{
    /**
     * @param input - The byte[] that will be converted.
     * @return - Returns a string of hex values.
     */
    public static String byteArrayToHexString(byte[] input) 
    {
        StringBuilder sb = new StringBuilder(input.length * 2);
        
        for(byte b: input)
            sb.append(String.format("%02x", b));
        
        return sb.toString();
    }

    public static byte[] hexStringToByteArray(String input)
    {
        int len = input.length();
        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2)
        {
            data[i / 2] = (byte) ((Character.digit(input.charAt(i), 16) << 4)
                    + Character.digit(input.charAt(i+1), 16));
        }

        return data;
    }
    
    /**
     * @param input - Byte[] that will be converted.
     * @return - Returns a string in the format of UTF-8.
     */
    public static String byteArrayToUTF8String(byte[] input)
    {
        String output = "";
        
        try
        {
            output = new String(input, "UTF-8");
        }
        catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Convert.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return output;
    }
    
    /**
     * @param input - Byte[] that will be converted.
     * @return - Returns an integer value.
     */
    public static int byteArrayToInt(byte[] input)
    {
        String hex = byteArrayToHexString(input);
        int output = Integer.parseInt(hex, 16);
        
        return output;
    }
    
    /**
     * @param input - 2 byte integer value.
     * @return - Returns a byte[].
     */
    public static byte[] intToByteArray(int input)
    { 
        byte[] output = new byte[2];

        output[0] = (byte) (input >> 8);
        output[1] = (byte) (input);
        
        return output;
    }

    public static String stringToHexString(String input)
    {
        StringBuilder buf = new StringBuilder(input.length());

        for (char ch: input.toCharArray())
        {
            if (buf.length() > 0)
                buf.append(' ');
            buf.append(String.format("%02x", (int) ch));
        }

        return buf.toString();
    }

    public static byte intToByte(int input)
    {
        return Byte.parseByte(Integer.toHexString(input));
    }
}
