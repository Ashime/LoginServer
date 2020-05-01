package Packet;
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

public interface Protocol
{
    byte S2C_ansReady       = 0x00;
    byte C2S_askVerify      = 0x01;
    byte S2C_ansVerify      = 0x02;
    byte C2S_askAuthUser    = 0x03;
    byte S2C_ansAuthUser    = 0x0E;
    byte C2S_askSrvList     = 0x0F;
    byte S2C_ansSrvList_Srv = 0x11;
    byte S2C_ansSrvList_Chn = 0x12;
    byte C2S_askSrvSelect   = 0x13;
    byte S2C_ansSrvSelect   = 0x1A;
}
