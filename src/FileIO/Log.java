package FileIO;

import Packet.LoginProtocol;
import Utility.Convert;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

// @author Ashime

// TODO:
// 1. Commenting

public class Log
{
    private static String fileName;
        
    public static void logInfo(String type, String message)
    {
        checkDirectory();
        
        switch(type)
        {
            case "m":
            {
                fileName = "Log/Console/Login Server/LoginServer " + formatDate() +  ".txt";
                write(fileName, message);
                break;
            }
            case "p":
            {
                fileName = "Log/Packet/Login Server/LoginPackets " + formatDate() + ".txt";
                write(fileName, message);
                break;
            }
        }
    }
    
    private static void write(String fileName, String message)
    {
        File file = new File(fileName);
        BufferedWriter write = null;
        FileWriter fileStream;
        
        // If file is not in the specified location then create a new one.
        if(!file.exists() || !file.isDirectory())
        {
                try
                {
                    fileStream = new FileWriter(file, true);
                    write = new BufferedWriter(fileStream);
                    write.write(message);
                    write.newLine();
                    
                } catch (IOException ex) {
                    Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
                } finally
                {
                        if(write != null)
                    {
                        try {
                            write.close();
                        } catch (IOException ex) {
                            Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
        }
        else
        {
                try (PrintWriter writer = new PrintWriter(file, "UTF-16")) {
                    writer.println(message);
                    writer.close();
                } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                    Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
    }
    
    private static void checkDirectory()
    {
        File consoleDirectory = new File("Log/Console/Login Server");
        File packetDirectory = new File("Log/Packet/Login Server");
        
        if(!consoleDirectory.exists())
            consoleDirectory.mkdirs();
        else if(!packetDirectory.exists())
            packetDirectory.mkdirs();
    }
    
    public static String formatDate()
    {
        return new SimpleDateFormat(IniFile.getDateFormat()).format(new GregorianCalendar().getTime());
    }
    
    public static String formatTime()
    {
        return new SimpleDateFormat(IniFile.getTimeFormat()).format(new GregorianCalendar().getTime());
    }

    public static void formatLog(byte[] inPacket, String packetType)
    {
        try
        {
            logString(packetType);

            String data = new String(inPacket, "UTF-8");
            logString("Unicode: ");
            logString(data);

            logString("HEX: ");
            logByte(inPacket);
            logString("");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(LoginProtocol.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void logString(String msg)
    {
        logInfo("p", msg);
    }

    private static void logByte(byte[] msg)
    {
        String message = Convert.byteArrayToHexString(msg);
        logInfo("p", message);
    }
}
