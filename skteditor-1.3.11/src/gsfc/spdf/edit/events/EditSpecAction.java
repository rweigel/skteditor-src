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
 * $Id: EditSpecAction.java,v 1.7 2022/03/24 10:38:28 btharris Exp $
 */

//$Id: EditSpecAction.java,v 1.7 2022/03/24 10:38:28 btharris Exp $
package gsfc.spdf.edit.events;

import javax.swing.*;

import java.awt.Cursor;
import java.awt.event.*;
import java.lang.*;
import java.util.Vector;
import java.util.Enumeration;

import gsfc.nssdc.cdf.*;
import gsfc.spdf.istp.*;
import gsfc.spdf.edit.guis.*;

/**
 * For future use.
 *
 * @author Phil Williams
 * @version $Revision: 1.7 $
 */

public class EditSpecAction extends AbstractAction {
    
    private static final String DEFAULT_NAME = "Edit Specs";

    private static final String DEFAULT_ICON_NAME = "Edit16.gif";

    private static Icon DEFAULT_ICON;

    private SKTEditor myEditor;

    public EditSpecAction(SKTEditor myEditor) {
	this(myEditor, DEFAULT_NAME, DEFAULT_ICON);
    }

    public EditSpecAction(SKTEditor myEditor, String name, Icon icon) {
	super(name, icon);
	this.myEditor = myEditor;
    }

    public void putValue(String key, Object value) {
	if (value != null)
	    super.putValue(key, value);
    }

    public void actionPerformed(ActionEvent event) {
	SKTEditor.setWaitCursor();

	JOptionPane.showMessageDialog(SKTEditor.edit, 
				      "This feature is not yet implemented",
				      "SKTEditor: Not yet Implemented",
				      JOptionPane.INFORMATION_MESSAGE);
	SKTEditor.setDefaultCursor();
    }
    
    static {
	String iconPath = SKTEditor.propertyPath +
	    SKTEditor.appProperties.getProperty("icon.path");
	try {
	    DEFAULT_ICON = new ImageIcon(
                                  EditSpecAction.class.getResource(
                                        iconPath + DEFAULT_ICON_NAME));
        }
        catch(NullPointerException e) {
            System.err.println("cannot find icon " + iconPath +
                               DEFAULT_ICON_NAME + " -- continuing without it");
        };
    }

} // EditSpecAction
