package Packet.Handlers;
/*
    @project LoginServer
    @author Ashime
    Created on 4/22/2020.

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

import Packet.Category;
import Packet.Protocol;
import Sql.Query;

import java.util.ArrayList;

public class GetServerList implements Category, Protocol
{
    private static final Query query = new Query();

    private static int serverNum, channelNum;
    private static ArrayList<String> serverNames = new ArrayList<>();
    private static ArrayList<String> serverToChannelRelation = new ArrayList<>();

    public static void getList()
    {
        query.getServerList();
        getSrvChnInfo(query.getQueryResults());
    }

    private static void getSrvChnInfo(ArrayList<String> svrList)
    {
        serverNum = checkSvrCount(svrList);
        serverNames = getServerNames(svrList);
        channelNum = checkChnCount(svrList);
        serverToChannelRelation = getServerToChannelRelation(svrList);
    }

    private static int checkSvrCount(ArrayList<String> svrList)
    {
        int svrNum = 0;

        for (int i = 0; i < svrList.size(); i += 4)
        {
            if (svrNum != 0)
            {
                if (svrNum != Integer.parseInt(svrList.get(i)))
                    svrNum = Integer.parseInt(svrList.get(i));
            }
            else
                svrNum = Integer.parseInt(svrList.get(i));

        }

        return svrNum;
    }

    private static ArrayList<String> getServerNames(ArrayList<String> svrList)
    {
        ArrayList<String> svrName = new ArrayList<>();
        int j = 0;

        for (int i = 1; i < svrList.size(); i += 4)
        {
            if (i == 1)
                svrName.add(svrList.get(i));
            else if (!svrName.get(j).matches(svrList.get(i)))
            {
                svrName.add(svrList.get(i));
                j++;
            }
        }

        return svrName;
    }

    private static int checkChnCount(ArrayList<String> svrList)
    {
        int chnNum = 0;

        for (int i = 2; i < svrList.size(); i += 4)
            chnNum++;

        return chnNum;
    }

    private static ArrayList<String> getServerToChannelRelation(ArrayList<String> svrList)
    {
        ArrayList<String> serversAndChannels = new ArrayList<>();
        int index = 0;

        for (int i = 0; i < svrList.size(); i += 4)
        {
            if (i == 0)
            {
                serversAndChannels.add(svrList.get(i));
                serversAndChannels.add(svrList.get(i + 3));
            }
            else if (serversAndChannels.get(index).matches(svrList.get(i)))
            {
                if (!(serversAndChannels.get(index + 1).matches(svrList.get(i + 3))))
                    serversAndChannels.add(svrList.get(i + 3));
            }
            else if (!(serversAndChannels.get(index).matches(svrList.get(i))))
            {
                serversAndChannels.add(svrList.get(i));
                index = (serversAndChannels.size() - 1);
                serversAndChannels.add(svrList.get(i + 3));
            }
        }

        return serversAndChannels;
    }

    // GETTERS
    public static int getServerNum() {
        return serverNum;
    }

    public static int getChannelNum() {
        return channelNum;
    }

    public static ArrayList<String> getServerNames() {
        return serverNames;
    }

    public static ArrayList<String> getServerToChannelRelation() {
        return serverToChannelRelation;
    }
}
