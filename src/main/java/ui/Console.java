package ui;

/*
    @project LoginServer
    @author Ashime
    Created on 07/07/2017.

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

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;

// TODO: Commenting

public class Console
{
    private static final JFrame frame = new JFrame();
    private static final JTextPane textPane = new JTextPane();    // Keep this static, otherwise other classes/methods cannot update the JTextPane accordingly.
    private static final JTextField cmdLine = new JTextField();
    private static JScrollPane scrollPane = new JScrollPane();

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
        cmdLine.addActionListener(new CMD()); // Attach listener to the command line bar.
        frame.add(cmdLine, BorderLayout.SOUTH);  // Attach command line bar to the bottom of the frame.

        frame.setPreferredSize(new Dimension(800, 250));

        frame.pack();   // Packs the frame.
        frame.setVisible(true); // Sets the frame's visibility to true.
    }

    // Display any messages to the console interface.
    public static void displayMessage(String style, String message)
    {
        String[] headers = {"[MSG]: ", "[INFO]: ", "[WARN]: ", "[ERR]: ", "[CMD]: "};
        String timestamp = "[" + Log.formatDate() + "]" + "[" + Log.formatTime() + "]";

        StyledDocument styledDocument = textPane.getStyledDocument();

        // Default Messaging Style. Prints the text in white.
        Style standard = textPane.addStyle("Default Message", null);
        StyleConstants.setForeground(standard, Color.WHITE);

        // Information Messaging Style. Prints the text in green.
        Style info = textPane.addStyle("INFO Message", null);
        StyleConstants.setForeground(info, Color.GREEN);

        // Warning Messaging Style. Prints the text in yellow.
        Style warning = textPane.addStyle("WARNING Message", null);
        StyleConstants.setForeground(warning, Color.YELLOW);

        // Error Messaging Style. Prints the text in red.
        Style error = textPane.addStyle("ERROR Message", null);
        StyleConstants.setForeground(error, Color.RED);

        // Command Messaging Style. Prints the text in cyan.
        Style command = textPane.addStyle("CMD Message", null);
        StyleConstants.setForeground(command, Color.CYAN);

        try
        {
            switch (style)
            {
                case "MSG":
                {
                    styledDocument.insertString(styledDocument.getLength(), timestamp, standard);
                    styledDocument.insertString(styledDocument.getLength(), headers[0], standard);
                    styledDocument.insertString(styledDocument.getLength(), message + "\n", standard);

                    Log.logInfo(headers[0] + message);
                    break;
                }
                case "INFO":
                {
                    styledDocument.insertString(styledDocument.getLength(), timestamp, standard);
                    styledDocument.insertString(styledDocument.getLength(), headers[1], info);
                    styledDocument.insertString(styledDocument.getLength(), message + "\n", standard);

                    String newMessage = headers[1] + message;
                    Log.logInfo(newMessage);
                    break;
                }
                case "WARN":
                {
                    styledDocument.insertString(styledDocument.getLength(), timestamp, standard);
                    styledDocument.insertString(styledDocument.getLength(), headers[2], warning);
                    styledDocument.insertString(styledDocument.getLength(), message + "\n", standard);

                    String newMessage = headers[2] + message;
                    Log.logInfo(newMessage);
                    break;
                }
                case "ERR":
                {
                    styledDocument.insertString(styledDocument.getLength(), timestamp, standard);
                    styledDocument.insertString(styledDocument.getLength(), headers[3], error);
                    styledDocument.insertString(styledDocument.getLength(), message + "\n", standard);

                    String newMessage = headers[3] + message;
                    Log.logInfo(newMessage);
                    break;
                }
                case "CMD":
                {
                    styledDocument.insertString(styledDocument.getLength(), timestamp, standard);
                    styledDocument.insertString(styledDocument.getLength(), headers[4], command);
                    styledDocument.insertString(styledDocument.getLength(), message + "\n", standard);

                    String newMessage = headers[4] + message;
                    Log.logInfo(newMessage);
                    break;
                }
                default:
                {
                    Log.logError("Wrong output. Please check passing variable.", Console.class.getName(), "displayMessage");
                    break;
                }
            }
        }
        catch (BadLocationException ex) {
            Log.logError(ex.getMessage(), Console.class.getName(), "displayMessage");
        }
    }

    // ---------------- GETTERS ----------------
    public static JTextField getCmdLine() {
        return cmdLine;
    }
    public static JTextPane getTextPane() {
        return textPane;
    }
}