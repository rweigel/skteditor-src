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
 * $Id: NewVariableAction.java,v 1.14 2022/03/24 10:38:28 btharris Exp $
 */

//$Id: NewVariableAction.java,v 1.14 2022/03/24 10:38:28 btharris Exp $
package gsfc.spdf.edit.events;

import javax.swing.*;

import java.awt.Cursor;
import java.awt.event.*;
import java.lang.*;
import java.util.Vector;
import java.util.Enumeration;

import gsfc.nssdc.cdf.*;
import gsfc.spdf.edit.guis.*;
import gsfc.spdf.edit.events.VariableEvent;
import gsfc.spdf.edit.events.VariableEventListener;

/**
 * Create a new variable.
 *
 * @author Phil Williams 
 * @version $Revision: 1.14 $
 */
public class NewVariableAction extends AbstractAction {
    
    private static final String DEFAULT_NAME = "New";

    private static final String DEFAULT_ICON_NAME = "New16.gif";

    private static Icon DEFAULT_ICON;

    private SKTEditor myEditor;

    public NewVariableAction(SKTEditor myEditor) {
	this(myEditor, DEFAULT_NAME, DEFAULT_ICON);
    }

    public NewVariableAction(SKTEditor myEditor, String name, Icon icon) {
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
					  "Must open a CDF before "+
					  "creating variables.",
					  "SKTEditor: Error",
					  JOptionPane.ERROR_MESSAGE);
	} else {
	    Variable var = 
		ISTPVariableDialog.create((JFrame)myEditor.getFrame(),
					      myEditor.theCDF);
	    if (var != null) {
		try {

		    myEditor.variablePanel.saveVariableChanges();
		} 
                finally {
		    myEditor.variablePanel.addToListOfVariables(var);
		    myEditor.variablePanel.updateVarPanel(var);
		    myEditor.variablePanel.reselectCurrentVariable();
		}
	    }
	}
	SKTEditor.setDefaultCursor();
    }

    static {
	String iconPath = SKTEditor.propertyPath +
	    SKTEditor.appProperties.getProperty("icon.path");
        try {
	    DEFAULT_ICON = new ImageIcon(
                                  NewVariableAction.class.getResource(
                                       iconPath + DEFAULT_ICON_NAME));
        }
        catch(NullPointerException e) {
            System.err.println("cannot find icon " + iconPath +
                               DEFAULT_ICON_NAME + " -- continuing without it");
        };
    }

} // NewVariableAction
