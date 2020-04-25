package Sql;

import FileIO.IniFile;
import Interface.Console;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

// @author Ashime

/*
GENERAL INFORMATION:
1. Microsoft JDBC Driver
    This is being used to connect, communicate, and etc. with MSSQL databases.
    Please see ../AuthServer/libs/ReadMe.txt for further information.
    >> Download Link: https://www.microsoft.com/en-us/download/details.aspx?id=55539

TODO:
1. Commenting
*/

public class Connect
{   
    private static Connection connection;          
    private static String sqlPath;      // Connect pathway.
    
    IniFile ini = new IniFile();
    Console console = new Console();
    
    
    public void testConnection()
    {   
        
        try
        {
            sqlPath = "jdbc:sqlserver://" + ini.getSqlAddr() + ":" + ini.getSqlPort() + ";" + "Database=" + ini.getSqlDBN() + ";" + "Username=" + ini.getSqlUser() + ";" + "Password=" + ini.getSqlPass();
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection((sqlPath));

            console.displayMessage("INFO", "SQL test connection was successful!");
        }
        catch (ClassNotFoundException ex)
        {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
            console.displayMessage("ERR", "Corresponding JDBC Driver cannot be found or is corrupted!");

        }
        catch (SQLException ex)
        {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
            console.displayMessage("ERR", "Cannot connect to SQL database. Check LoginServer.ini or SQL database configuration.");
        }
        finally
        {
            if(connection != null)
            {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
            console.displayMessage("ERR", "Cannot connect to SQL database. Check LoginServer.ini or SQL database configuration.");
        }
    }
    
    public void closeConnection()
    {
        if(connection != null)
        {
            try {
                connection.close();
            } catch (SQLException ex) {
                Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    // Getter
    public Connection getConnection()
    {
        return connection;
    }
}
