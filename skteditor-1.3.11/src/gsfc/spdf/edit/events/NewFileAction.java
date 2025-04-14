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
 * $Id: NewFileAction.java,v 1.9 2022/03/24 10:38:28 btharris Exp $
 */


//$Id: NewFileAction.java,v 1.9 2022/03/24 10:38:28 btharris Exp $
package gsfc.spdf.edit.events;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.event.*;

import gsfc.spdf.edit.guis.*;

/**
 * Create a new File.
 *
 * @author Phil Williams
 * @version $Revision: 1.9 $
 */
public class NewFileAction extends AbstractFileAction {
    
    private static final String DEFAULT_NAME = "New";

    private static final String DEFAULT_ICON_NAME = "New16.gif";

    private static Icon DEFAULT_ICON;

    public NewFileAction(SKTEditor myEditor) {
	this(myEditor, DEFAULT_NAME, DEFAULT_ICON);
    }

    public NewFileAction(SKTEditor myEditor, String name, Icon icon) {
	super(name, icon);
	this.myEditor = myEditor;
    }

    public void putValue(String key, Object value) {
	if (value != null)
	    super.putValue(key, value);
    }

    public void actionPerformed(ActionEvent event) {
	boolean doIt = true;

	SKTEditor.setWaitCursor();

	try {
	    if (myEditor.theCDF != null) {
		String name = null;
		if (myEditor.getFile() == null)
		    name = "New File";
		else
		    name = myEditor.getFile().getName();
		int result =
		    JOptionPane.
		    showConfirmDialog(SKTEditor.edit, 
				      "Do you want to save "+name+
				      "\nbefore opening a new file?",
				      "Save "+name,
				      JOptionPane.YES_NO_CANCEL_OPTION);
		if (result == JOptionPane.YES_OPTION)
		       performSaveAction(myEditor.shouldSaveAs(), true);
		else if (result == JOptionPane.NO_OPTION)
			    performCloseAction();
		else doIt = false;
	    }
	    
	    if (doIt)
		myEditor.newFile();
	} catch (gsfc.nssdc.cdf.CDFException exc) {
	    exc.printStackTrace();
	    JOptionPane.showMessageDialog(myEditor.sharedInstance(),
					  exc.getMessage(),
					  "SKTEditor: CDFException",
					  JOptionPane.ERROR_MESSAGE);
	}  catch (Exception e) {
	    e.printStackTrace();
	    JOptionPane.showMessageDialog(myEditor.sharedInstance(),
					  e.getMessage(),
					  "SKTEditor: Exception",
					  JOptionPane.ERROR_MESSAGE);
	}
	
	SKTEditor.setDefaultCursor();
    }

    static {
	String iconPath = SKTEditor.propertyPath +
	    SKTEditor.appProperties.getProperty("icon.path");
        try {
	    DEFAULT_ICON = new ImageIcon(
                                  NewFileAction.class.getResource(
                                        iconPath + DEFAULT_ICON_NAME));
        }
        catch(NullPointerException e) {
            System.err.println("cannot find icon " + iconPath +
                               DEFAULT_ICON_NAME + " -- continuing without it");
        };
    }
    
} // NewFileAction
