/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.servlet.container;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.net.URL;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

/**
 * Graphical user interface for the Servlet container.
 * This class may move to another package.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:anthony.goubard@orange-ftgroup.com">Anthony Goubard</a>
 *
 * @since XINS 2.1
 */
public class ConsoleGUI {

   private JPanel consolePanel;

   private JTextPane console;

   private JMenuBar consoleMenuBar;

   private Style[] logStyles;
   
   private int logLevel = 0;

   /**
    * Constructs a new <code>ConsoleGUI</code>.
    */
   public ConsoleGUI() {
      // TODO pass the main frame as arg for the menu bar and sub dialogs
      initUI();
      initData();
   }
   
   protected void initUI() {
      consolePanel = new JPanel();
      console = new JTextPane();
      console.setPreferredSize(new Dimension(700, 400));
      consolePanel.setLayout(new BorderLayout(5,5));
      consolePanel.add(new JScrollPane(console), BorderLayout.CENTER);
      
      consoleMenuBar = new JMenuBar();
      
      // Add the actions
      JMenu consoleMenu = new JMenu("Console");
      consoleMenu.setMnemonic('c');
      Action clearAction = new AbstractAction("Clear") {
         public void actionPerformed(ActionEvent ae) {
            console.setText("");
         }
      };
      consoleMenu.add(clearAction);
      consoleMenu.addSeparator();
      Action exitAction = new AbstractAction("Exit") {
         public void actionPerformed(ActionEvent ae) {
            System.exit(0);
         }
      };
      exitAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK));
      consoleMenu.add(exitAction);
      
      JMenu logLevelMenu = new JMenu("Log level");
      logLevelMenu.setMnemonic('l');
      logLevelMenu.add(new ChangeLogLevel(0, "Debug"));
      logLevelMenu.add(new ChangeLogLevel(1, "Info"));
      logLevelMenu.add(new ChangeLogLevel(2, "Notice"));
      logLevelMenu.add(new ChangeLogLevel(3, "Warning"));
      logLevelMenu.add(new ChangeLogLevel(4, "Error"));
      logLevelMenu.add(new ChangeLogLevel(5, "Fatal"));
      JMenu helpMenu = new JMenu("Help");
      helpMenu.setMnemonic('h');
      Action aboutAction = new AbstractAction("About") {
         public void actionPerformed(ActionEvent ae) {
            Object[] aboutMessage = { "XINS", "http://www.xins.org/" };

            JOptionPane optionPane = new JOptionPane();
            optionPane.setMessage(aboutMessage);
            optionPane.setMessageType(JOptionPane.INFORMATION_MESSAGE);
            JDialog dialog = optionPane.createDialog(null, "About");
            dialog.setVisible(true);
         }
      };
      helpMenu.add(aboutAction);
      consoleMenuBar.add(consoleMenu);
      consoleMenuBar.add(logLevelMenu);
      consoleMenuBar.add(helpMenu);
      
      // Initialize the styles
      Style debug = console.addStyle("Debug", null);
      StyleConstants.setForeground(debug, Color.GRAY);
      Style info = console.addStyle("Info", null);
      StyleConstants.setForeground(info, Color.BLACK);
      Style notice = console.addStyle("Notice", null);
      StyleConstants.setForeground(notice, Color.BLUE);
      Style warning = console.addStyle("Warning", null);
      StyleConstants.setForeground(warning, Color.ORANGE);
      Style error = console.addStyle("Error", null);
      StyleConstants.setForeground(error, Color.RED);
      Style fatal = console.addStyle("Fatal", null);
      StyleConstants.setForeground(fatal, Color.RED);
      StyleConstants.setBackground(fatal, Color.LIGHT_GRAY);
      logStyles = new Style[] {debug, info, notice, warning, error, fatal};
   }

   protected void initData() {
      try {
         // Set up System.out
         PipedInputStream piOut = new PipedInputStream();
         PipedOutputStream poOut = new PipedOutputStream(piOut);
         System.setOut(new PrintStream(poOut, true));

         // Set up System.err
         PipedInputStream piErr = new PipedInputStream();
         PipedOutputStream poErr = new PipedOutputStream(piErr);
         System.setErr(new PrintStream(poErr, true));
         // Create reader threads
         new ReaderThread(piOut).start();
         new ReaderThread(piErr).start();
      } catch (IOException ioe) {
      }
   }

   public static JFrame create() {
      JFrame application = new JFrame();
      application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      URL iconLocation = ConsoleGUI.class.getResource("/org/xins/common/servlet/container/xins.gif");
      if (iconLocation != null) {
         application.setIconImage(new ImageIcon(iconLocation).getImage());
      }
      
      ConsoleGUI console = new ConsoleGUI();
      application.setJMenuBar(console.getMenuBar());
      application.getContentPane().add(console.getMainPanel());
      application.pack();
      
      // Center the JFrame
      Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
      Dimension appDim = application.getSize();
      application.setLocation((screenDim.width - appDim.width) / 2,(screenDim.height - appDim.height) / 2);
      return application;
   }
   
   public JPanel getMainPanel() {
      return consolePanel;
   }
   
   public JMenuBar getMenuBar() {
      return consoleMenuBar;
   }

   protected int getLogLevel(String text) {
      String textToSearch = text;
      if (text.length() > 80) {
         textToSearch = text.substring(0, 80);
      }
      if (textToSearch.indexOf("DEBUG") != -1) {
         return 0;
      } else if (textToSearch.indexOf("INFO") != -1) {
         return 1;
      } else if (textToSearch.indexOf("NOTICE") != -1) {
         return 2;
      } else if (textToSearch.indexOf("WARN") != -1) {
         return 3;
      } else if (textToSearch.indexOf("ERROR") != -1) {
         return 4;
      } else if (textToSearch.indexOf("FATAL") != -1) {
         return 5;
      } else {
         return -1;
      }
   }

   class ReaderThread extends Thread {
      BufferedReader br;
      
      ReaderThread(PipedInputStream pi) {
         br = new BufferedReader(new InputStreamReader(pi));
      }
      
      public void run() {
         try {
            while (true) {
               final String text = br.readLine();
               SwingUtilities.invokeLater(new Runnable() {
                  public void run() {
                     try {
                        int consoleLength = console.getDocument().getLength();
                        int messageLogLevel = getLogLevel(text);
                        if (messageLogLevel < logLevel) {
                           return;
                        }
                        Style style = logStyles[messageLogLevel];
                        console.getDocument().insertString(consoleLength, text + "\n", style);

                        // Make sure the last line is always visible
                        consoleLength = console.getDocument().getLength();
                        console.setCaretPosition(consoleLength);

                        // Keep the text area down to a certain character size
                        int idealSize = 10000;
                        int maxExcess = 5000;
                        int excess = consoleLength - idealSize;
                        if (excess >= maxExcess) {
                           console.getDocument().remove(0, excess);
                        }
                     } catch (BadLocationException e) {
                     }
                  }
               });
            }
         } catch (IOException e) {
         }
      }
   }

   class ChangeLogLevel extends AbstractAction {
      
      private int _newLogLevel;
      
      ChangeLogLevel(int newLogLevel, String level) {
         super(level);
         _newLogLevel = newLogLevel;
      }
      public void actionPerformed(ActionEvent ae) {
         logLevel = _newLogLevel;
      }
   }
}
