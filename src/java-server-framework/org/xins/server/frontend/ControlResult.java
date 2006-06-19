/*
 * $Id$
 *
 * Copyright 2003-2006 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server.frontend;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.xins.common.xml.ElementBuilder;
import org.xins.common.spec.FunctionSpec;
import org.xins.common.spec.InvalidSpecificationException;

import org.xins.server.API;
import org.xins.server.FunctionResult;

/**
 * Result for the Control command.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
class ControlResult extends FunctionResult {

   /**
    * Creates a new Control result.
    */
   ControlResult(API api, SessionManager sessionManager, Map redirectionMap) {

      // The versions
      param("xins-common", org.xins.common.Library.getVersion());
      param("xins-server", org.xins.server.Library.getVersion());

      // The commands
      try {
         Map functions = api.getAPISpecification().getFunctions();
         Iterator itFunctions = functions.entrySet().iterator();
         while (itFunctions.hasNext()) {
            Map.Entry nextFunction = (Map.Entry) itFunctions.next();
            FunctionSpec functionSpec = (FunctionSpec) nextFunction.getValue();
            ElementBuilder builder = new ElementBuilder("command");
            builder.setAttribute("name", (String) nextFunction.getKey());
            builder.setAttribute("description", functionSpec.getDescription());
            add(builder.createElement());
         }
      } catch (InvalidSpecificationException isex) {
         isex.printStackTrace();
      }
      Iterator itVirtualFunctions = redirectionMap.keySet().iterator();
      while (itVirtualFunctions.hasNext()) {
         String nextFunction = (String) itVirtualFunctions.next();
         ElementBuilder builder = new ElementBuilder("command");
         builder.setAttribute("name", nextFunction);
         add(builder.createElement());
      }

      // The sessions
      ElementBuilder builder = new ElementBuilder("sessionproperties");
      Map sessionProperties = sessionManager.getProperties();
      Iterator itSessions = sessionProperties.entrySet().iterator();
      while (itSessions.hasNext()) {
         Map.Entry nextSession = (Map.Entry) itSessions.next();
         String nextKey = (String) nextSession.getKey();
         Object nextValue = nextSession.getValue();
         ElementBuilder builder2 = new ElementBuilder("property");
         builder2.setAttribute("name", nextKey);
         builder2.setText(nextValue.toString());
         builder.addChild(builder2.createElement());
      }
   }
}