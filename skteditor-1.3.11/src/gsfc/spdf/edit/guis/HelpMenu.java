/*
 * NOSA HEADER START
 *
 * The contents of this file are subject to the terms of the NASA Open 
 * Source Agreement (NOSA), Version 1.3 only (the "Agreement").  You may 
 * not use this file except in compliance with the Agreement.
 *
 * You can obtain a copy of the agreement at
 *   docs/NASA_Open_Source_Agreement_1.3.txt
 * or 
 *   https://spdf.gsfc.nasa.gov/skteditor/NASA_Open_Source_Agreement_1.3.txt
 *
 * See the Agreement for the specific language governing permissions
 * and limitations under the Agreement.
 *
 * When distributing Covered Code, include this NOSA HEADER in each
 * file and include the Agreement file at 
 * docs/NASA_Open_Source_Agreement_1.3.txt.  If applicable, add the 
 * following below this NOSA HEADER, with the fields enclosed by 
 * brackets "[]" replaced with your own identifying information: 
 * Portions Copyright [yyyy] [name of copyright owner]
 *
 * NOSA HEADER END
 *
 * Copyright (c) 2011-2022 United States Government as represented by 
 * the National Aeronautics and Space Administration. No copyright is 
 * claimed in the United States under Title 17, U.S.Code. All Other 
 * Rights Reserved.
 *
 * $Id: HelpMenu.java,v 1.15 2022/03/24 10:38:32 btharris Exp $
 */
package gsfc.spdf.edit.guis;

import javax.swing.event.*;
import javax.help.*;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;	
	
/**
 */
public class HelpMenu extends JMenu  {
    
    private JMenuItem helpContent    = new JMenuItem("Help Contents",
                              SKTEditor.getIcon("Help16.gif"));
//
// This is an example of how to have a menu item bring up a specific
// section of help in case we want to do this.  "Tips" may not be worth
// a seperate menu item.
//
//    private JMenuItem helpTips = new JMenuItem("Tips",
//                              SKTEditor.getIcon("TipOfTheDay16.gif"));
//
    private JMenuItem aboutSKTEditor = new JMenuItem("About SKTEditor",
                              SKTEditor.getIcon("About16.gif"));
    private JMenuItem helpOn         = new JMenuItem("Help on item",
	                      SKTEditor.getIcon("ContextualHelp16.gif"));
   
    private final static String helpPath = "/help/";
    
	
    public HelpMenu() {

        setText("Help");
        setMnemonic(KeyEvent.VK_H); 
	 
        String helpFile = helpPath + "HelpSet.hs";
                                       // help set filename
        ClassLoader classLoader = getClass().getClassLoader();
                                       // the class loader that is being
                                       //  used.  Note that the
                                       //  ClassLoader.getSystemClassLoader()
                                       //  is not the one to use under Java
                                       //  Web Start.
        URL helpSetUrl = classLoader.getResource(helpFile);
                                       // URL of help set

        if (helpSetUrl == null) {

            //
            // try non-WebStart method
            //
            helpSetUrl = getClass().getResource(helpFile);

            if (helpSetUrl == null) {

                System.err.println("Couldn't find help contents (" +
                                   helpFile + ")");

                return;
            }
        };

        HelpSet mainHS = null;
        
        //	Find the HelpSet file and create the HelpSet object:
        try {

            mainHS = new HelpSet(null, helpSetUrl);
        } 
        catch (HelpSetException ex) {

            System.err.println("Failed to create HelpSet: " + ex.getMessage());
            return;
        };
	    
        add(helpContent);
        helpContent.setMnemonic(KeyEvent.VK_H);
        helpContent.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,
						 ActionEvent.CTRL_MASK));
        helpContent.setEnabled(true);
		
//        add(helpTips);
		
        CSH.setHelpIDString(aboutSKTEditor, "about");

        add(aboutSKTEditor);
        aboutSKTEditor.setMnemonic(KeyEvent.VK_A);
        aboutSKTEditor.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
	   					 ActionEvent.CTRL_MASK));
        aboutSKTEditor.setEnabled(true);

        add(helpOn);

        String javaVersion = System.getProperty("java.version");
        boolean java11 = false;

        if (javaVersion != null && javaVersion.startsWith("1.1")) {

            java11 = true;

            UIManager.put("HelpOnItemCursor", new Cursor(Cursor.HAND_CURSOR));
            //
            // Having this property defined will cause the default 
            //  implementation of Context-Sensitive Help to work under
            //  JDK 1.1
            //
        };

        HelpBroker mainHB = mainHS.createHelpBroker();

        mainHB.enableHelpKey(SKTEditor.frame.getRootPane(), "Intro", null);

        //REGISTER_LISTENERS
	    
        helpContent.addActionListener(
                        new CSH.DisplayHelpFromSource(mainHB));
//        helpTips.addActionListener(
//                        new DisplayHelpId(mainHB, "Tips"));
        aboutSKTEditor.addActionListener(
                        new CSH.DisplayHelpFromSource(mainHB));

        if (java11 && System.getProperty("os.name").equals("Mac OS")) {

            helpOn.addActionListener(new ActionListener() {
        
                public void actionPerformed(ActionEvent e) {

                    JOptionPane.showMessageDialog(
                          SKTEditor.frame.getRootPane(),
                          "Context sensitive help is not available under\n" +
                          "this version of Java (" + 
                          System.getProperty("java.version") + ") on " + 
                          System.getProperty("os.name") +
                          System.getProperty("os.version") + 
                          ".\nIt is expected to work on newer versions\n" +
                          "when they become available.",
                          "Function not available", 
                          JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }
        else {

            helpOn.addActionListener(
                        new CSH.DisplayHelpAfterTracking(mainHB));
        };
    }

    protected static class DisplayHelpId implements ActionListener {

        protected HelpBroker helpBroker = null;
        protected String helpId = null;

        public DisplayHelpId(HelpBroker hb, String id) {

            helpBroker = hb;
            helpId = id;
        }

        public void actionPerformed(ActionEvent e) {

            helpBroker.setCurrentID(helpId);
            helpBroker.setDisplayed(true);
        }
    }
}
