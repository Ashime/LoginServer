package Packet.Handlers;
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

import FileIO.IniFile;
import Security.TEA;
import Utility.Utility;
import Utility.Convert;
import Sql.Query;

import java.util.Arrays;

public class AuthUser
{
    private static Query query = new Query();

    public static boolean authUserAndPassword(byte[] input, byte[] key)
    {
        byte[] username = Arrays.copyOfRange(input, 6, 56);
        byte[] shortenUsername = Arrays.copyOfRange(username, 0, Utility.indexOf(username, "0"));

        if(IniFile.isNoLoginAuth())
        {
            if(query.FindUser(Convert.byteArrayToUTF8String(shortenUsername)) == 1)
                return false;
            else
                return true;
        }
        else
        {
            byte[] password = Arrays.copyOfRange(input, 57, 81);
            byte[] dPassword = TEA.passwordDecode(password, key);
            System.out.println("dPassword: " + Convert.byteArrayToHexString(dPassword));

            if (query.AuthUser(Convert.byteArrayToUTF8String(shortenUsername), Convert.byteArrayToUTF8String(dPassword)))
                return true;
            else
                return false;
        }
    }
}
