package Security;

import FileIO.IniFile;
import Interface.Console;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

// @author Ashime

public class SHA512
{
    private String salt = "", saltedPassword = "", hashedPassword = "";
    private static final String hmacKey = IniFile.getHmacKey();
    
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
            for (int i = 0; i < data.length; i++)
            {
                sb.append(Integer.toString((data[i] & 0xff) + 0x100, 16).substring(1));
            }
        }
        catch (NoSuchAlgorithmException | InvalidKeyException ex) {
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
