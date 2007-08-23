/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.spec;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import org.xins.common.types.Type;

/**
 * Graphical user interface that allows to browse the specification of an API
 * and execute the functions of this API.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:anthony.goubard@orange-ftgroup.com">Anthony Goubard</a>
 *
 * @since XINS 2.1
 */
public class TestFormPanel extends JPanel {

   private APISpec apiSpec;
   
   private String functionName;

   private java.util.List parameterComponents;

   private ActionListener submitListener;

   /**
    * Constructs a new <code>SpecGUI</code>.
    * 
    * @param apiSpec
    *    the specification of the API.
    * 
    * @param functionName
    *    the specification of the API.
    */
   public TestFormPanel(APISpec apiSpec, String functionName, ActionListener submitListener) {
      this.apiSpec = apiSpec;
      this.functionName = functionName;
      this.submitListener = submitListener;
      try {
         initUI();
      } catch (Exception ex) {
         ex.printStackTrace();
      }
      initData();
   }

   /**
    * Creates the user interface.
    */
   protected void initUI() throws Exception {
      FunctionSpec functionSpec = apiSpec.getFunction(functionName);
      setLayout(new BorderLayout(5,5));
      JLabel jlFunctionName = new JLabel(functionName) {
         public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            Color background = getBackground();
            
            Paint oldPaint = g2.getPaint();
            GradientPaint gradient = new GradientPaint(0.0f, 0.0f, background, TestFormPanel.this.getWidth() + 0.1f, getHeight() + 0.1f, background.brighter());
            g2.setPaint(gradient);
            g2.fill(new Rectangle(TestFormPanel.this.getWidth(), getHeight()));
            g2.setPaint(oldPaint);
            super.paint(g);
         }
      };
      jlFunctionName.setOpaque(false);
      jlFunctionName.setFont(jlFunctionName.getFont().deriveFont(20.0f));
      jlFunctionName.setToolTipText(functionSpec.getDescription());
      add(jlFunctionName, BorderLayout.NORTH);
      
      Map inputParameters = functionSpec.getInputParameters();
      boolean hasInputDataSection = functionSpec.getInputDataSectionElements().size() > 0;
      parameterComponents = new ArrayList();
      JPanel paramNamesPanel = new JPanel();
      JPanel paramValuesPanel = new JPanel();
      paramNamesPanel.setLayout(new BoxLayout(paramNamesPanel, BoxLayout.Y_AXIS));
      paramValuesPanel.setLayout(new BoxLayout(paramValuesPanel, BoxLayout.Y_AXIS));
      Iterator itInputParameters = inputParameters.values().iterator();
      while (itInputParameters.hasNext()) {
         ParameterSpec inputSpec = (ParameterSpec) itInputParameters.next();
         JLabel jlInput = new JLabel(inputSpec.getName() + ":");
         jlInput.setToolTipText(inputSpec.getDescription());
         paramNamesPanel.add(jlInput);
         Type inputType = inputSpec.getType();
         JTextField inputField = new JTextField(20);
         inputField.setToolTipText(inputType.getName());
         inputField.putClientProperty("PARAM_NAME", inputSpec.getName());
         paramValuesPanel.add(inputField);
         parameterComponents.add(inputField);
      }
      if (hasInputDataSection) {
         JLabel jlInput = new JLabel("Data section:");
         paramNamesPanel.add(jlInput);
         JTextArea inputField = new JTextArea(8,40);
         inputField.putClientProperty("PARAM_NAME", "_data");
         paramValuesPanel.add(inputField);
         parameterComponents.add(inputField);
      }
      add(paramNamesPanel, BorderLayout.WEST);
      add(paramValuesPanel, BorderLayout.CENTER);
      
      JPanel submitPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
      JButton jbSubmit = new JButton("Submit");
      jbSubmit.addActionListener(submitListener);
      submitPanel.add(jbSubmit);
      add(submitPanel, BorderLayout.SOUTH);
   }

   protected void initData() {
   }

   /**
    * Gets the list of parameters in a URL form.
    * 
    * @return
    *    the list of the parameters as it should be send to the URL 
    *    (starting with an '&') or an empty String if no parameter is set.
    */
   public String getParameters() {
      String result = "";
      Iterator itParameters = parameterComponents.iterator();
      while (itParameters.hasNext()) {
         JComponent inputComponent = (JComponent) itParameters.next();
         String paramName = (String) inputComponent.getClientProperty("PARAM_NAME");
         String paramValue = "";
         if (inputComponent instanceof JTextComponent) {
            paramValue = ((JTextComponent) inputComponent).getText();
         }
         if (!"".equals(paramValue)) {
            result += "&" + paramName + "=" + paramValue;
         }
      }
      return result;
   }
}
