package Security;

/*
    @project LoginServer
    @author Ashime
    Created on 12/10/2017.

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

import FileIO.IniFile;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SHA512
{
    private static final String hmacKey = IniFile.getHmacKey();
    private String salt = "", saltedPassword = "", hashedPassword = "";

    public void hashPassword(String Password)
    {
        SecureRandom random = new SecureRandom();
        salt += random.nextLong();   // Generates the salt as a long int.

        // Adds salt and password together.
        saltedPassword = salt + Password;

        runHash();
    }

    public void authHashedPassword(String Password, String Salt)
    {
        // Adds the passed salt and password together.
        saltedPassword = Salt + Password;

        runHash();
    }

    private void runHash()
    {
        StringBuilder sb = new StringBuilder();

        try
        {
            Mac hmacSHA512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec keySpec = new SecretKeySpec(hmacKey.getBytes(), "HmacSHA512");
            hmacSHA512.init(keySpec);

            byte[] data = hmacSHA512.doFinal(saltedPassword.getBytes());

            // Loop through mb and append each bit to the stringbuilder after
            // converting it to Hex.
//            for (int i = 0; i < data.length; i++) {
//                sb.append(Integer.toString((data[i] & 0xff) + 0x100, 16).substring(1));
//            }

            for (byte datum : data)
            {
                sb.append(Integer.toString((datum & 0xff) + 0x100, 16).substring(1));
            }

        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            Logger.getLogger(SHA512.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Store the string builder to hashedPassword.
        hashedPassword = sb.toString();
    }

    /*=============================
     *          Getters
     ==============================*/
    public String getSalt() {
        return salt;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }
}
