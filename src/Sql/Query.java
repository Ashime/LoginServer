package Sql;

import FileIO.IniFile;
import Interface.Console;
import Security.SHA512;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

// @author Ashime

/*
TODO:
1. Implement all database queries.
2. Commenting
*/

public class Query
{
    private SHA512 sha = new SHA512();
    private final Connect connect = new Connect();
    
    CallableStatement statement = null;
    ResultSet result = null;
    
    // Index[0]: Salt, Index[1]: Password
    private ArrayList<String> list = new ArrayList<String>();
    private ArrayList<String> queryResults = new ArrayList();
    private int timeOut = IniFile.getTimeOut();
    
    
    public void CreateUser(String Username, String Password, String Email, String IP)
    {
        try
        {
            sha.hashPassword(Password);
            
            connect.openConnection();
            
            statement = connect.getConnection().prepareCall("{? = call SP_CreateUser(?, ?, ?, ?, ?)}");
            statement.setEscapeProcessing(true);
            statement.setQueryTimeout(timeOut);
            
            statement.registerOutParameter(1, java.sql.Types.INTEGER);
            
            statement.setNString(2, Username);
            statement.setNString(3, sha.getHashedPassword());
            statement.setNString(4, sha.getSalt());
            statement.setNString(5, Email);
            statement.setNString(6, IP);
            
            statement.execute();
            
            if(statement.getInt(1) == 0)
                Console.displayMessage("INFO", "Creating user (" + Username + ") failed!");
            
            else if(statement.getInt(1) == 1)
                Console.displayMessage("INFO", "Creating user (" + Username + ") was successful!");
            
            statement.close();
            connect.closeConnection();
            
        } catch (SQLException ex) {
            Logger.getLogger(Query.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean AuthUser(String Username, String Password)
    {
        int accountStatus = 0;
        
        try
        {
            String salt = getSalt(Username);
            sha.authHashedPassword(Password, salt);
            
            connect.openConnection();
            
            statement = connect.getConnection().prepareCall("{? = call SP_AuthUser(?, ?)}");
            statement.setEscapeProcessing(true);
            statement.setQueryTimeout(timeOut);

            statement.registerOutParameter(1, Types.INTEGER);
            statement.setNString(2, Username);
            statement.setNString(3, sha.getHashedPassword());

            statement.execute();

            accountStatus = statement.getInt(1);
            
            statement.close();
            connect.closeConnection();
            
        } catch (SQLException ex) {
            Logger.getLogger(Query.class.getName()).log(Level.SEVERE, null, ex);
        }

        switch(accountStatus)
        {
            case 0:
            {
                Console.displayMessage("WARN", "Authenticating user (" + Username + ") failed!");
                return false;
            }
            case 1:
            {
                Console.displayMessage("INFO", "Authenticating user (" + Username + ") was successful!");
                return true;
            }
            case 2:
            {
                Console.displayMessage("WARN", "Authenticating user (" + Username + ") failed due to account being locked!");
                return false;
            }
            case 3:
            {
                Console.displayMessage("WARN", "The account of user (" + Username + ") has been checked for deletion!");
                return false;
            }
            case 4:
            {
                Console.displayMessage("WARN", "The account of user (" + Username + ") has a temporary ban!");
                return false;
            }
            case 5:
            {
                Console.displayMessage("WARN", "The account of user (" + Username + ") has a permanent ban!");
                return false;
            }
            default:
            {
                Console.displayMessage("ERR", "Authenticating user returned an abnormal account status.");
                return false;
            }       
        } 
    }
    
    private String getSalt(String Username)
    {
        String salt = null;
        
        try
        {
            connect.openConnection();
            
            statement = connect.getConnection().prepareCall("{call SP_GetSalt(?)}");
            statement.setEscapeProcessing(true);
            statement.setQueryTimeout(timeOut);
            
            statement.setNString(1, Username);
            
            result = statement.executeQuery();

            if(result.next())
                salt = result.getNString(1);

            statement.close();
            connect.closeConnection(); 
            
        } catch (SQLException ex) {
            Logger.getLogger(Query.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return salt;
    }
    
    /* When server protocols come into affect, this will have to be broken down into
     * more steps. Step 1: User sends username and email for verification, once
     * verified, then server sends packet back to application for user to reset
     * password.  Step 2: User sends new password with username, server will 
     * retrieve old salt and password to verify that the new password is not same
     * as the old password. Step 3: If all steps are successful then send a packet
     * back to the application that it was successful AKA password reset completed. */
    public void ResetPassword(String Username, String Password, String Email) throws SQLException
    {
        int status = VerifyAccount(Username, Email);
        
        if(status == 1)
        {
            GetOldPassword(Username);
            sha.authHashedPassword(Password, list.get(0));
            
            if((list.get(1).matches(sha.getHashedPassword())))
                System.out.println("The new password that user (" + Username + ") is trying to reset with equals with an older password.");
//            else if((list.get(1).matches(sha.getHashedPassword())) == false)
            else
            {
                sha.hashPassword(Password);
                
                connect.openConnection();
                
                statement = connect.getConnection().prepareCall("{? = call SP_ResetPassword(?, ?, ?)}");
                statement.setEscapeProcessing(true);
                statement.setQueryTimeout(timeOut);
                
                statement.registerOutParameter(1, java.sql.Types.INTEGER);
                statement.setNString(2, Username);
                statement.setNString(3, sha.getHashedPassword());
                statement.setNString(4, sha.getSalt());
                
                statement.execute();
                
                if(statement.getInt(1) == 1)
                    System.out.println("Resetting password for user (" + Username + ") was successful!");
                
                statement.close();
                connect.closeConnection();
            }  
        }
        // TODO: When the packet system works, then send packet back to user to notify them that the user does not exist.
        else if(status == 0)
        {
            Console.displayMessage("WARN", "The user (" + Username + ") does not exist in the database!");
        }
    }
    
    private int VerifyAccount(String Username, String Email) throws SQLException
    {
        int status = 0;
        
        connect.openConnection();

        statement = connect.getConnection().prepareCall("{? = call SP_VerifyAccount(?, ?)}");
        statement.setEscapeProcessing(true);
        statement.setQueryTimeout(timeOut);

        statement.registerOutParameter(1, java.sql.Types.INTEGER);
        statement.setNString(2, Username);
        statement.setNString(3, Email);

        statement.execute();

        if(statement.getInt(1) == 0)
            ; // Leave status equal to zero
        else if(statement.getInt(1) == 1)
            status = 1;

        statement.close();
        connect.closeConnection();
        
        return status;
    }

    private void GetOldPassword(String Username) throws SQLException
    {
        connect.openConnection();
        
        statement = connect.getConnection().prepareCall("{call SP_GetOldPassword(?)}");
        statement.setEscapeProcessing(true);
        statement.setQueryTimeout(timeOut);

        statement.setNString(1, Username);

        result = statement.executeQuery();

        while(result.next())
            list.add(result.getNString(1));
        
        statement.close();
        connect.closeConnection();
    }
    
    public void GetCharacters(String Username) throws SQLException
    {
        connect.openConnection();
        
        statement = connect.getConnection().prepareCall("{call SP_SelectCharacters(?)}");
        statement.setEscapeProcessing(true);
        statement.setQueryTimeout(timeOut);
        
        statement.setNString(1, Username);
        
        result = statement.executeQuery();
        
        while(result.next())
            list.add(result.getNString(1));
        
        statement.close();
        connect.closeConnection();
    }

    public int FindUser(String Username)
    {
        int foundUser = 0;

        connect.openConnection();

        try
        {
            statement = connect.getConnection().prepareCall("{? = call SP_FindUser(?)}");
            statement.setEscapeProcessing(true);
            statement.setQueryTimeout(timeOut);

            statement.registerOutParameter(1, Types.INTEGER);
            statement.setNString(2, Username);

            statement.execute();

            foundUser = statement.getInt(1);

            statement.close();
            connect.closeConnection();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return foundUser;
    }

    public void getServerList()
    {
        connect.openConnection();

        try
        {
            statement = connect.getConnection().prepareCall("{call SP_GetServerList()}");
            statement.setEscapeProcessing(true);
            statement.setQueryTimeout(timeOut);

            statement.executeQuery();

            result = statement.getResultSet();

            // result.next() will jump to the next row.
            while(result.next())
            {
                // Cycles through the columns - one column at a time.
                for(int j = 1; j <= result.getMetaData().getColumnCount(); j++)
                    queryResults.add(result.getString(j));
            }

            statement.close();
            connect.closeConnection();

        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public ArrayList<String> getQueryResults() {
        return queryResults;
    }
}
