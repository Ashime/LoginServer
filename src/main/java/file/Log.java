package file;

/*
    @project LoginServer
    @author Ashime
    Created on 07/12/2017.

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
import ui.Console;
import utility.Convert;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Log
{
    private static String fileName;

    public static void log(String type, String message)
    {
        checkDirectory();

        switch (type)
        {
            case "MSG": // message
            {
                fileName = IniFile.getLogFolder() + "/Console/Login Server/LoginServer " + formatDate() + ".txt";
                write(fileName, message);
                break;
            }
            case "PKT": // packet
            {
                fileName = IniFile.getLogFolder() + "/Packet/Login Server/LoginPackets " + formatDate() + ".txt";
                write(fileName, message);
                break;
            }
            default:
            {
//                Console.displayMessage("WARN", "Unknown message type: " + type);
                fileName = IniFile.getLogFolder() + "/Error/Login Server/Login Server " + formatDate() + ".txt";
                write(fileName, Log.class.getName() + ": " + "Unknown message type " + type);
                break;
            }
        }
    }

    public static void logInfo(String message)
    {
        checkDirectory();
        fileName = IniFile.getLogFolder() + "/Console/Login Server/Login Server " + formatDate() + ".txt";

        // Example: [mm/dd/yyyy][hh:mm:ss]Message
        write(fileName, "[" + formatDate() + "]" + "[" + formatTime() + "]" + message);
    }

    public static void logPacket(String type, byte[] packet)
    {
        checkDirectory();
        fileName = IniFile.getLogFolder() + "/Packet/Login Server/Login Server " + formatDate() + ".txt";

        // Example: [mm/dd/yyyy][hh:mm:ss][S2C] AnsReadyPacket
        // Example: [mm/dd/yyyy][hh:mm:ss][UNI]
        // Example: [mm/dd/yyyy][hh:mm:ss][HEX] 06003300001aad38
        String data = new String(packet, StandardCharsets.UTF_8);
        logString("Unicode: ");
        logString(data);

        logString("HEX: ");
//        String message = Convert.byteArrayToHexString(msg);
        logString("");
    }

    public static void logError(String message, String inClass, String inMethod)
    {
        checkDirectory();
        fileName = IniFile.getLogFolder() + "/Error/Login Server/Login Server " + formatDate() + ".txt";

        // Example: [mm/dd/yyyy][hh:mm:ss] Error: custom message here. CLASS: class name METHOD: method name
        write(fileName, "[" + formatDate() + "]" + "[" + formatTime() + "] " + " Error: " + message +
                " CLASS: " + inClass + " METHOD: " + inMethod);

        Console.displayMessage("ERR", "An error has occurred, please check error logs in " + fileName);
    }

    private static void write(String fileName, String message)
    {
        File file = new File(fileName);
        BufferedWriter write = null;
        FileWriter fileStream;

        // If file is not in the specified location then create a new one.
        if (!file.exists() || !file.isDirectory())
        {
            try
            {
                fileStream = new FileWriter(file, true);
                write = new BufferedWriter(fileStream);
                write.write(message);
                write.newLine();

            } catch (IOException ex) {
                Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
            }
            finally
            {
                if (write != null)
                {
                    try { write.close(); }
                    catch (IOException ex) {
                        Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        else
        {
            try
            {
                PrintWriter writer = new PrintWriter(file, "UTF-8");
                writer.println(message);
                writer.close();
            } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static void checkDirectory()
    {
        File consoleDirectory = new File(IniFile.getLogFolder() + "/Console/Login Server");
        File packetDirectory = new File(IniFile.getLogFolder() + "/Packet/Login Server");
        File errorDirectory = new File(IniFile.getLogFolder() + "/Error/Login Server");

        if (!consoleDirectory.exists())
            consoleDirectory.mkdirs();
        else if (!packetDirectory.exists())
            packetDirectory.mkdirs();
        else if (!errorDirectory.exists())
            errorDirectory.mkdirs();
    }

    public static String formatDate() {
        return new SimpleDateFormat(IniFile.getDateFormat()).format(new GregorianCalendar().getTime());
    }

    public static String formatTime() {
        return new SimpleDateFormat(IniFile.getTimeFormat()).format(new GregorianCalendar().getTime());
    }

    public static void formatLog(byte[] inPacket, String packetType)
    {
        logString(packetType);

        String data = new String(inPacket, StandardCharsets.UTF_8);
        logString("Unicode: ");
        logString(data);

        logString("HEX: ");
        logByte(inPacket);
        logString("");
    }
    private static void logString(String msg) {
        log("PKT", msg);
    }

    private static void logByte(byte[] msg) {
        String message = Convert.byteArrayToHexString(msg);
        log("PKT", message);
    }
}
