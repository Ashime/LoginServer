package loginserver;

/*
    @project LoginServer
    @author Ashime
    Created on 07/07/2017.

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
import server.NioServer;
import sql.Connect;
import sql.Query;
import ui.Console;

public class LoginServer
{
    public static void main(String[] args)
    {
        // Setup for console interface. Allows access to display messages on the console.
        Console console = new Console();
        console.initConsole();  // Initializes the interface.

        // Initializes and pulls all the data from Common.ini and LoginServer.ini.
        IniFile ini = new IniFile();
        ini.parseIni();

        Console.displayMessage("MSG", "==================================================================");
        Console.displayMessage("MSG", "                     SUN Online Login Server");
        Console.displayMessage("MSG", "                     Client Version: " + IniFile.getVersion());
        Console.displayMessage("MSG", "==================================================================");

        Connect connect = new Connect();
        connect.testConnection();   // Tests database connection.

        Query query = new Query();
        query.getStoredMacAddresses();

        if (!IniFile.isTrustedDevices())
            Console.displayMessage("WARN", "Trusted devices allowed on a user's account will not be checked!");

        NioServer server = new NioServer();
        server.start();            // Starts up the C2S and S2S logic and business.
    }
}
