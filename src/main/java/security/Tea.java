package security;

import server.handlers.SessionHandler;
import utility.Utility;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 @author CwaniX
 Ragezone link: http://forum.ragezone.com/members/2000270863.html
 GitHub link: https://github.com/CwaniX/OpenSUN-Emu

 @author Ashime
 Changed on 04/21/2020
 1. Made all methods static.
 2. Added an additional method for generating keys.

 Changed on 07/15/2020
 1. Merged Cwanix's ByteUtils class with my Utility class. Changed ByteUtils calls to Utility.
    NOTE: All credit was properly noted in Utility.java.

 Changed on 07/18/2020
 1. Updated the passwordEncode and passwordDecode methods. The passwordEncode method now adds
 a unique salt after the password and places the password inside the passMask byte array. All
 result byte arrays and the passMask array has been changed to 24 bytes. This convention comes
 from the client because the client sends the encrypted password in the size of 24 bytes.

 NOTES:
 1. The size of the key and password has to be managed elsewhere.
        a. password (max: 12 bytes)
        b. key (max: 4 bytes)
*/

public class Tea
{
    public static byte[] passwordEncode(String passInput, byte[] keyInput)
    {
        byte[] passMask = new byte[24];
        byte[] key = new byte[4];
        byte[] result = new byte[24];

        try
        {
            // The max number is 23 because there is one separator byte between password and filler.
            byte[] filler = new byte[23 - passInput.getBytes().length];
            // Securely randomize the bytes to create a unique salt.
            SecureRandom.getInstance("SHA1PRNG").nextBytes(filler);

            // Copy the passInput to passMask.
            System.arraycopy(passInput.getBytes(), 0, passMask, 0, passInput.getBytes().length);
            // Copy the filler to the end of passMask.
            System.arraycopy(filler, 0, passMask, passInput.getBytes().length + 1, filler.length);
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        int keyValue = Utility.byteArrayToInt(keyInput);

        key[0] = (byte) keyValue;
        key[1] = (byte) (keyValue + 1);
        key[2] = (byte) (keyValue + 2);
        key[3] = (byte) (keyValue + 3);

        byte[] enc1 = encode(Arrays.copyOfRange(passMask, 0, 8), key);
        byte[] enc2 = encode(Arrays.copyOfRange(passMask, 8, 16), key);
        byte[] enc3 = encode(Arrays.copyOfRange(passMask, 16, 23), key);

        Utility.strncpy(enc1, result, 0);
        Utility.strncpy(enc2, result, 8);
        Utility.strncpy(enc3, result, 16);

        return result;
    }

    public static byte[] passwordDecode(byte[] passInput, byte[] keyInput)
    {
        byte[] result = new byte[24];
        int keyValue = Utility.byteArrayToInt(keyInput);

        byte[] key = new byte[4];
        key[0] = (byte) keyValue;
        key[1] = (byte) (keyValue + 1);
        key[2] = (byte) (keyValue + 2);
        key[3] = (byte) (keyValue + 3);

        byte[] dec1 = decode(passInput, key);
        byte[] dec2 = decode(Arrays.copyOfRange(passInput, 8, 16), key);
        byte[] dec3 = decode(Arrays.copyOfRange(passInput, 16, 23), key);

        Utility.strncpy(dec1, result, 0);
        Utility.strncpy(dec2, result, 8);
        Utility.strncpy(dec3, result, 16);

        return Utility.cutTail(result);
    }

    public static byte[] encode(byte[] src, byte[] key)
    {
        int v0 = Utility.byteArrayToInt(Arrays.copyOfRange(src, 0, 4));
        int v1 = Utility.byteArrayToInt(Arrays.copyOfRange(src, 4, 8));
        int delta = 0x9e3779b9;
        int sum = 0;

        for (int i = 0; i < 32; i++)
        {
            sum += delta;
            v0 += ((v1 << 4) + key[0]) ^ (v1 + sum) ^ ((v1 >>> 5) + key[1]);
            v1 += ((v0 << 4) + key[2]) ^ (v0 + sum) ^ ((v0 >>> 5) + key[3]);
        }

        return Utility.intToByteArray(v0, v1);
    }

    public static byte[] decode(byte[] src, byte[] key)
    {
        int v0 = Utility.byteArrayToInt(Arrays.copyOfRange(src, 0, 4));
        int v1 = Utility.byteArrayToInt(Arrays.copyOfRange(src, 4, 8));
        int delta = 0x9e3779b9;
        int sum = 0xc6ef3720;

        for (int i = 0; i < 32; i++)
        {
            v1 -= ((v0 << 4) + key[2]) ^ (v0 + sum) ^ ((v0 >>> 5) + key[3]);
            v0 -= ((v1 << 4) + key[0]) ^ (v1 + sum) ^ ((v1 >>> 5) + key[1]);
            sum -= delta;
        }

        return Utility.intToByteArray(v0, v1);

    }

    /*
        @author Ashime
        Generates a secure 4 byte key.
     */
    public static byte[] generateKey()
    {
        byte[] key = new byte[4];

        try {
            SecureRandom.getInstance("SHA1PRNG").nextBytes(key);
            key[0] = 0x00;

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(SessionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        return key;
    }
}
