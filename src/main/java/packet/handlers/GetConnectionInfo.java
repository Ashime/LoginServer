package packet.handlers;
/*
    @project LoginServer
    @author Ashime
    Created on 4/26/2020.

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

import sql.Query;

import java.util.ArrayList;

public class GetConnectionInfo
{
    private static final Query query = new Query();

    public static ArrayList<String> getInfo(byte[] input)
    {
        int size = input.length;
        query.getGameServerInfo((int) input[size - 2], (int) input[size - 1]);
        return query.getQueryResults();
    }
}
