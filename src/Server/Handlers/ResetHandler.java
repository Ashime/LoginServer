package Server.Handlers;

import Interface.Console;
import Server.NettyNio;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

 // @author Ashime

/*
Information:
        ResetHandler deals with the command '/restart #' when the user presses
        enter on the command line. The number entered after restart is how long
        the delay is before restarting. Delaying the restart can be useful to stop all
        incoming connections for so long. The restart command restarts the
        NioServer and reuses the already existing addresses for binding.
*/

public class ResetHandler implements Runnable
{
    Console console = new Console();
    NettyNio server = new NettyNio();
    
    private int resetTime;

    public ResetHandler(int time) 
    {
        resetTime = time;
    }
    
    @Override
    public void run()
    {  
        try
        {
            server.stop();
            TimeUnit.SECONDS.sleep(resetTime);
           console.displayMessage("INFO", "Server is now restarting!");
            server.start();
        }
        catch (Exception ex) {
            Logger.getLogger(ResetHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
