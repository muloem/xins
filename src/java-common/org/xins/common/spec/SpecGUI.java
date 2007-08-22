/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.spec;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import org.xins.common.io.IOReader;
import org.xins.common.xml.Element;
import org.xins.common.xml.ElementParser;

/**
 * Graphical user interface that allows to browse the specification of an API
 * and execute the functions of this API.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:anthony.goubard@orange-ftgroup.com">Anthony Goubard</a>
 *
 * @since XINS 2.1
 */
public class SpecGUI {

   private JPanel specPanel;

   private JMenuBar specMenuBar;

   private JTextField jtfEnvironment;

   private JTextField jtfQuery;

   private JTextPane xmlViewer;

   /**
    * Constructs a new <code>ConsoleGUI</code>.
    */
   public SpecGUI() {
      JFrame mainFrame = new JFrame();
      initUI(mainFrame);
      initData();
      mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      mainFrame.setVisible(true);
   }

   /**
    * Constructs a new <code>SpecGUI</code>.
    * 
    * @param mainFrame
    *    the main frame or <code>null</code> if no frame is available.
    */
   public SpecGUI(JFrame mainFrame) {
      initUI(mainFrame);
      initData();
   }

   /**
    * Creates the user interface.
    * This method also creates the actions available in the menu.
    * 
    * @param mainFrame
    *    the main frame or <code>null</code> if no frame is available.
    */
   protected void initUI(JFrame mainFrame) {
      specPanel = new JPanel();
      xmlViewer = new JTextPane();
      xmlViewer.setPreferredSize(new Dimension(500, 400));
      specPanel.setLayout(new BorderLayout(5,5));
      
      JPanel queryPanel = new JPanel();
      jtfEnvironment = new JTextField("http://localhost:8080/?_function=_xins-std");
      jtfQuery = new JTextField();
      queryPanel.setLayout(new GridLayout(2,1));
      queryPanel.add(jtfEnvironment);
      queryPanel.add(jtfQuery);
      specPanel.add(queryPanel, BorderLayout.NORTH);
      specPanel.add(new JScrollPane(xmlViewer), BorderLayout.CENTER);
      
      specMenuBar = new JMenuBar();

      // Add the actions
      JMenu consoleMenu = new JMenu("File");
      consoleMenu.setMnemonic('f');
      Action clearAction = new AbstractAction("Clear") {
         public void actionPerformed(ActionEvent ae) {
            xmlViewer.setText("");
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

      JMenu metaFunctionsMenu = new JMenu("Meta functions");
      metaFunctionsMenu.setMnemonic('m');
      metaFunctionsMenu.add(new QueryMetaFunction("GetVersion"));
      metaFunctionsMenu.add(new QueryMetaFunction("GetSettings"));
      metaFunctionsMenu.add(new QueryMetaFunction("GetStatistics"));
      metaFunctionsMenu.add(new QueryMetaFunction("NoOp"));
      metaFunctionsMenu.add(new QueryMetaFunction("ReloadProperties"));
      metaFunctionsMenu.add(new QueryMetaFunction("CheckSettings"));
      metaFunctionsMenu.add(new QueryMetaFunction("ResetStatistics"));
      metaFunctionsMenu.add(new QueryMetaFunction("WSDL"));
      metaFunctionsMenu.add(new QueryMetaFunction("SMD"));
      metaFunctionsMenu.add(new QueryMetaFunction("GetFunctionList"));
      metaFunctionsMenu.add(new QueryMetaFunction("DisableAPI"));
      metaFunctionsMenu.add(new QueryMetaFunction("EnableAPI"));
      JMenu specMenu = new JMenu("Specifications");
      specMenu.setMnemonic('s');
      try {
         ElementParser parser = new ElementParser();
         Element webapp = parser.parse(getClass().getResourceAsStream("/WEB-INF/web.xml"));
         String apiClassName = null;
         Iterator itParams = webapp.getUniqueChildElement("servlet").getChildElements("init-param").iterator();
         while (itParams.hasNext()) {
            Element initParam = (Element) itParams.next();
            String paramName = initParam.getUniqueChildElement("param-name").getText();
            if (paramName.equals("org.xins.api.class")) {
               apiClassName = initParam.getUniqueChildElement("param-value").getText();
            }
         }
         Class apiClass = Class.forName(apiClassName);
         APISpec specs = new APISpec(apiClass, getClass().getResource("/WEB-INF/specs/").toString());
         jtfEnvironment.setText("http://localhost:8080/" + specs.getName() + "/?_function=_xins-std");
         Map functions = specs.getFunctions();
         Iterator itFunctions = functions.keySet().iterator();
         while (itFunctions.hasNext()) {
            String nextFunction = (String) itFunctions.next();
            specMenu.add(new ViewSpecAction(nextFunction + ".fnc"));
         }

      } catch (Exception ex) {
         ex.printStackTrace();
      }

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
      specMenuBar.add(consoleMenu);
      specMenuBar.add(metaFunctionsMenu);
      specMenuBar.add(specMenu);
      specMenuBar.add(helpMenu);

      if (mainFrame != null) {
         mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         URL iconLocation = SpecGUI.class.getResource("/org/xins/common/servlet/container/xins.gif");
         if (iconLocation != null) {
            mainFrame.setIconImage(new ImageIcon(iconLocation).getImage());
         }
         mainFrame.setJMenuBar(getMenuBar());
         mainFrame.getContentPane().add(getMainPanel());
         mainFrame.pack();

         // Center the JFrame
         Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
         Dimension appDim = mainFrame.getSize();
         mainFrame.setLocation((screenDim.width - appDim.width) / 2,(screenDim.height - appDim.height) / 2);
      }
   }

   protected void initData() {
   }

   public JPanel getMainPanel() {
      return specPanel;
   }
   
   public JMenuBar getMenuBar() {
      return specMenuBar;
   }

   class QueryMetaFunction extends AbstractAction {

      QueryMetaFunction(String metaFunction) {
         super(metaFunction);
      }

      public void actionPerformed(ActionEvent ae) {
         String query = jtfEnvironment.getText() + "&_function=_" + getValue(Action.NAME);
         jtfQuery.setText(query);
         xmlViewer.setText("");
         try {
            URL urlQuery = new URL(query);
            BufferedReader in = new BufferedReader(new InputStreamReader(urlQuery.openStream()));

            String inputLine;

            while ((inputLine = in.readLine()) != null) {
               xmlViewer.getDocument().insertString(xmlViewer.getDocument().getLength(), inputLine + "\n", null);
            }
            in.close();
         } catch (BadLocationException ble) {
         } catch (IOException ioe) {
            ioe.printStackTrace();
         }
      }
   }

   class ViewSpecAction extends AbstractAction {

      ViewSpecAction(String specFile) {
         super(specFile);
      }

      public void actionPerformed(ActionEvent ae) {
         try {
            String specContent = IOReader.readFully(getClass().getResourceAsStream("/WEB-INF/specs/" + getValue(Action.NAME)));
            xmlViewer.setText(specContent);
         } catch (IOException ioe) {
            ioe.printStackTrace();
         }
      }
   }
}
