package sql;

/*
    @project LoginServer
    @author Ashime
    Created on 7/12/2017.

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

import file.Log;
import file.io.IniFile;
import ui.Console;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/*
GENERAL INFORMATION:
1. Microsoft JDBC Driver
    This is being used to connect, communicate, and etc. with MSSQL databases.
    Please see ../AuthServer/libs/ReadMe.txt for further information.
    >> Download Link: https://www.microsoft.com/en-us/download/details.aspx?id=55539

TODO: Commenting
*/

public class Connect
{
    private static Connection connection;
    private static String sqlPath;      // Connect pathway.

    public void testConnection()
    {

        try
        {
            if(IniFile.isMySql())
            {
                sqlPath = "jdbc:mysql://" + IniFile.getSqlAddr() + ":" + IniFile.getSqlPort() + "/" + IniFile.getSqlDBN() + "?" + "user=" + IniFile.getSqlUser() + "&" + "password=" + IniFile.getSqlPass();
                connection = DriverManager.getConnection((sqlPath));
            }
            else if(IniFile.isPostgreSQL()) // Needs to be tested.
            {
                sqlPath = "jdbc:postgresql://" + IniFile.getSqlAddr() + ":" + IniFile.getSqlPort() + ";" + "Database=" + IniFile.getSqlDBN() + ";" + "Username=" + IniFile.getSqlUser() + ";" + "Password=" + IniFile.getSqlPass();
                connection = DriverManager.getConnection((sqlPath));
            }
            else if(IniFile.isMsSQLServer())
            {
                sqlPath = "jdbc:sqlserver://" + IniFile.getSqlAddr() + ":" + IniFile.getSqlPort() + ";" + "databaseName=" + IniFile.getSqlDBN() + ";" + "user=" + IniFile.getSqlUser() + ";" + "password=" + IniFile.getSqlPass();
                connection = DriverManager.getConnection((sqlPath));
            }

            Console.displayMessage("INFO", "SQL test connection was successful!");
        }
        catch (SQLException throwables)
        {
//            throwables.printStackTrace();
           Log.logError( throwables.getMessage(), Connect.class.getName(), "testConnection");
                //Console.displayMessage("ERR", ex.getMessage());
        }
        finally
        {
            if (connection != null)
            {
                try
                {
                    connection.close();
                }
                catch (SQLException ex)
                {
//                    Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
//                    Console.displayMessage("ERR", ex.getMessage());
                    Log.logError( ex.getMessage(), Connect.class.getName(), "testConnection");
                }
            }
        }
    }

    public void openConnection()
    {
        try
        {
            connection = DriverManager.getConnection((sqlPath));
        }
        catch (SQLException ex)
        {
//            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
//            Console.displayMessage("ERR", ex.getMessage());
            Log.logError(ex.getMessage(), Connect.class.getName(), "openConnection");
        }
    }

    public void closeConnection()
    {
        if (connection != null)
        {
            try
            {
                connection.close();
            }
            catch (SQLException ex) {
//                Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
//                Console.displayMessage("ERR", ex.getMessage());
                Log.logError(ex.getMessage(), Connect.class.getName(), "closeConnection");
            }
        }
    }

    // Getter
    public Connection getConnection() {
        return connection;
    }
}
