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
import Sql.Query;
import Utility.Convert;
import Utility.Utility;

import java.util.Arrays;

public class AuthUser
{
    private static final Query query = new Query();

    public static boolean authUserAndPassword(byte[] input, byte[] key, String ipAddress)
    {
        byte[] username = Arrays.copyOfRange(input, 6, 56);
        byte[] shortenUsername = Arrays.copyOfRange(username, 0, Utility.indexOf(username, "0"));
        byte[] password = Arrays.copyOfRange(input, 57, 81);
        byte[] dPassword = TEA.passwordDecode(password, key);

        if (IniFile.isTrustedDevices())
        {
            query.getStoredTrustedDevices(Convert.byteArrayToUTF8String(shortenUsername));

            for (String temp : Query.getTrustedDevices())
            {
                if (temp.matches(ipAddress))
                    return query.AuthUser(Convert.byteArrayToUTF8String(shortenUsername), Convert.byteArrayToUTF8String(dPassword));
            }

            return false;
        }
        else
            return query.AuthUser(Convert.byteArrayToUTF8String(shortenUsername), Convert.byteArrayToUTF8String(dPassword));
    }
}
