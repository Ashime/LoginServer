package Interface;

import FileIO.Log;
import Server.Handlers.SessionHandler;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

// @author Ashime

/* 
TODO:
1. Commenting
    Apply more comments to the displayMessage method.
*/

public class Console
{
    private static JFrame frame = new JFrame();
    private static JScrollPane scrollPane = new JScrollPane();
    private static JTextPane textPane = new JTextPane();    // Keep this static, otherwise other classes/methods cannot update the JTextPane accordingly.
    private static JTextField cmdLine = new JTextField();
    
    // Initializing the console interface.
    public void initConsole()
    {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   // Sets the frame's closing to default.
        frame.setTitle("SUN Online Login Server");     // Application title.
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage("Icon.png"));       // Set application icon.
       
        textPane.setEditable(false);            // Cannot be edited.
        textPane.setBackground(Color.BLACK);    // Sets textArea background to black.
        textPane.setFont(new Font("Lucida Console", 1, 12));
  
        scrollPane = new JScrollPane(textPane); // Initializes scrollPane with textArea inside.
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);   // Set scrollbar to always vertical.
        frame.add(scrollPane, BorderLayout.CENTER);  // Adds the scrollPane to the frame.
        
        cmdLine.setFont(new Font("Lucida Console", 1, 12)); // Font style and size of command line.
        cmdLine.addActionListener(new onCommand()); // Attach listener to the command line bar.
        frame.add(cmdLine, BorderLayout.SOUTH);  // Attach command line bar to the bottom of the frame.

        frame.setPreferredSize(new Dimension(800, 250));

        frame.pack();   // Packs the frame.
        frame.setVisible(true); // Sets the frame's visibility to true.
    }
    
    // Display any messages to the console interface.
    public static void displayMessage(String style, String message)
    {  
        // Default Messaging Style. Prints the text in white.
        StyledDocument standard = textPane.getStyledDocument();
        Style standardStyle = textPane.addStyle("Default Message", null);
        StyleConstants.setForeground(standardStyle, Color.WHITE);
        
        // Information Messaging Style. Prints the text in green.
        StyledDocument info = textPane.getStyledDocument();
        Style infoStyle = textPane.addStyle("INFO Message", null);
        StyleConstants.setForeground(infoStyle, Color.GREEN);
        
        // Warning Messaging Style. Prints the text in yellow.
        StyledDocument warning = textPane.getStyledDocument();
        Style warningStyle = textPane.addStyle("WARNING Message", null);
        StyleConstants.setForeground(warningStyle, Color.YELLOW);
        
        // Error Messaging Style. Prints the text in red.
        StyledDocument error = textPane.getStyledDocument();
        Style errorStyle = textPane.addStyle("ERROR Message", null);
        StyleConstants.setForeground(errorStyle, Color.RED);
        
        // Command Messaging Style. Prints the text in red.
        StyledDocument command = textPane.getStyledDocument();
        Style commandStyle = textPane.addStyle("ERROR Message", null);
        StyleConstants.setForeground(commandStyle, Color.CYAN);
        
        // Setup to take the string and add new line indentation after it.
        String newMessage = "[" + Log.formatDate() + "]" + "[" + Log.formatTime() + "]: " + message + "\n";

        try
        {
            switch (style)
            {
                case "MSG":
                {
                    standard.insertString(standard.getLength(), "[MSG]", standardStyle);
                    standard.insertString(standard.getLength(), newMessage, standardStyle);

                    String currentMessage = " [MSG]" + newMessage;

                    Log.logInfo("m", currentMessage);
                    break;
                }
                // Info Messaging Style. Prints [INFO]: + string.
                case "INFO":
                {
                    info.insertString(info.getLength(), "[INFO]", infoStyle);
                    standard.insertString(standard.getLength(), newMessage, standardStyle);

                    String currentMessage = "[INFO]" + newMessage;

                    Log.logInfo("m", currentMessage);
                    break;
                }
                // Warning Messaging Style. Prints [WARNING]: + string.
                case "WARN":
                {
                    warning.insertString(warning.getLength(), "[WARN]", warningStyle);
                    standard.insertString(standard.getLength(), newMessage, standardStyle);

                    String currentMessage = "[WARN]" + newMessage;

                    Log.logInfo("m", currentMessage);
                    break;  
                }
                // Error Messaging Style. Prints [ERROR]: + string.
                case "ERR":
                {
                    error.insertString(error.getLength(), "[ERR]", errorStyle);
                    standard.insertString(standard.getLength(), newMessage, standardStyle);

                    String currentMessage = " [ERR]" + newMessage;

                    Log.logInfo("m", currentMessage);
                    break;
                }
                case "CMD":
                {
                    command.insertString(command.getLength(), "[CMD]", commandStyle);
                    standard.insertString(standard.getLength(), newMessage, standardStyle);
                    
                    String currentMessage = "[CMD]" + newMessage;
                    
                    Log.logInfo("m", currentMessage);
                    break;
                }
                default:
                   System.out.println("[ERR]: Wrong output. Please check passing variable.");
                   break;  
            }
        } catch (BadLocationException ex) {
            Logger.getLogger(Console.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static class onCommand implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent ae)
        {
            String command = cmdLine.getText();
            cmdLine.setText("");
            
            if(command.matches("/help"))
            {
                displayMessage("CMD", "----------------------------------------------------------");
                displayMessage("CMD", "List of commands:");
                displayMessage("CMD", "/activity        -  Displays stats of server activity.");
                displayMessage("CMD", "/font #          -  Changes the font size.");
                displayMessage("CMD", "/restart #       -  Restarts the NioServer.");
                displayMessage("CMD", "----------------------------------------------------------");
            }
            else if(command.matches("/activity"))
            {
                displayMessage("CMD", "Active Connections: " + SessionHandler.getActiveUsers());
            }
            else if(command.contains("/font"))
            {
                 String size = "";
                int fontSize;
                char[] cmd = command.toCharArray();
                
                for(int i = 0; i < cmd.length; i++)
                {
                    if(Character.isDigit(cmd[i]))
                       size += cmd[i] ;
                }
                
                if(size.matches("") || size.matches("0"))
                    size+= 12;
                
                fontSize = Integer.parseInt(size);
                
                textPane.setFont(new Font("Lucida Console", 1, fontSize));
                cmdLine.setFont(new Font("Lucida Console", 1, fontSize));
            }
            else if(command.contains("/restart"))
            {
                String time = "";
                int resetTime;
                char[] cmd = command.toCharArray();
                
                for(int i = 0; i < cmd.length; i++)
                {
                    if(Character.isDigit(cmd[i]))
                       time += cmd[i] ;
                }
                
                if(time.matches(""))
                    time += 0;
                
                resetTime = Integer.parseInt(time);
//                ResetHandler reset = new ResetHandler(resetTime);
//                Thread t = new Thread(reset);
//                t.start();
            }
            else if(command.isEmpty() || !command.isEmpty())
            {
                displayMessage("CMD", "Unknown command. Use /help for the command list.");
            }
        }
    }
}