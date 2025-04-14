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
 * $Id: NewVirtualVariableAction.java,v 1.11 2022/03/24 10:38:28 btharris Exp $
 */

//$Id: NewVirtualVariableAction.java,v 1.11 2022/03/24 10:38:28 btharris Exp $
package gsfc.spdf.edit.events;


import java.awt.Cursor;
import java.awt.event.*;
import java.lang.*;
import java.util.Vector;
import java.util.Enumeration;

import javax.swing.*;
import javax.swing.border.EtchedBorder;

import gsfc.nssdc.cdf.*;
import gsfc.spdf.edit.guis.*;
import gsfc.spdf.edit.events.VariableEvent;
import gsfc.spdf.edit.events.VariableEventListener;

/**
 * Create a new virtual variable.
 *
 * @author B. Harris
 * @version $Revision: 1.11 $
 */
public class NewVirtualVariableAction extends AbstractAction {
    
    private static final String DEFAULT_NAME = "New Virtual";

    private static final String DEFAULT_ICON_NAME = "New16.gif";

    private static Icon DEFAULT_ICON;

    private SKTEditor myEditor;

    public NewVirtualVariableAction(SKTEditor myEditor) {
	this(myEditor, DEFAULT_NAME, DEFAULT_ICON);
    }

    public NewVirtualVariableAction(SKTEditor myEditor, String name, 
                                    Icon icon) {
	super(name, icon);
	this.myEditor = myEditor;
    }

    public void putValue(String key, Object value) {
	if (value != null)
	    super.putValue(key, value);
    }

    public void actionPerformed(ActionEvent event) {
	SKTEditor.setWaitCursor();

	if (myEditor.theCDF == null) {

	    JOptionPane.showMessageDialog(myEditor.sharedInstance(), 
					  "You must open a CDF before "+
					  "creating variables.",
					  "Create Variable Error",
					  JOptionPane.ERROR_MESSAGE);
	} 
        else {
 
            JList baseVarList = new JList(myEditor.theCDF.getVariables());
            JScrollPane listScrollPane = new JScrollPane(baseVarList);
            baseVarList.setSelectedValue(
                                myEditor.variablePanel.getSelectedVar(), true);

            String[] buttons = {"Select", "No Base", "Cancel"};

            JOptionPane optionPane = new JOptionPane(
                    new Object[] {"Select a base variable", listScrollPane},
                    JOptionPane.QUESTION_MESSAGE, JOptionPane.DEFAULT_OPTION,
                    null, buttons, buttons[0]);

            JDialog dialog = optionPane.createDialog(myEditor.sharedInstance(),
                                                     "Select Base Variable");
            dialog.show();

            String choice = (String)optionPane.getValue();

            Variable baseVarChoice = (Variable)baseVarList.getSelectedValue();

            if (choice == null || choice.equals(buttons[2])) {  // cancel

                // do nothing
            }
            else {  

                if (choice.equals(buttons[1])) {  // don't use a base variable

                    baseVarChoice = null;
                };

                Variable newVar = ISTPVariableDialog.createVirtual(
                                             (JFrame)myEditor.getFrame(), 
                                             myEditor.theCDF, baseVarChoice);

                if (newVar != null) {

                    myEditor.variablePanel.addToListOfVariables(newVar);
                    myEditor.variablePanel.updateVarPanel(newVar);
                    myEditor.variablePanel.reselectCurrentVariable();
                };
            };
        };
        SKTEditor.setDefaultCursor();
    }

    static {

	String iconPath = SKTEditor.propertyPath +
	    SKTEditor.appProperties.getProperty("icon.path");
        try {
	    DEFAULT_ICON = new ImageIcon(
                                  NewVirtualVariableAction.class.getResource(
                                       iconPath + DEFAULT_ICON_NAME));
        }
        catch(NullPointerException e) {
            System.err.println("cannot find icon " + iconPath +
                               DEFAULT_ICON_NAME + " -- continuing without it");
        };
    }

} // NewVirtualVariableAction
