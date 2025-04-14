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
 * $Id: AbstractFileAction.java,v 1.28 2022/03/24 10:38:28 btharris Exp $
 */

//$Id: AbstractFileAction.java,v 1.28 2022/03/24 10:38:28 btharris Exp $
/**
 * FileAction.java
 *
 *
 * Created: Thu Apr 15 12:40:41 1999
 *
 * @author 
 * @version $Revision: 1.28 $
 */
package gsfc.spdf.edit.events;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.event.*;
import java.lang.*;
import java.io.*;
import java.util.*;

import gsfc.nssdc.cdf.*;

//import gsfc.spdf.gui.StatusBar;
import gsfc.spdf.istp.*;
import gsfc.spdf.edit.guis.*;
import gsfc.spdf.edit.filechooser.*;

/**
 * Superclass for all actions under the File menu.
 *
 * Provides some methods that are common to all actions under the file menu.
 *
 * @see CloseAction
 * @see ExitAction
 * @see NewFileAction
 * @see OpenFileAction
 * @see SaveAction
 * @see SaveAsAction
 * @see CheckFileAction
 *
 * @author Phil Williams
 * @version $Revision: 1.28 $
 */
public abstract class AbstractFileAction extends AbstractAction {
    
    protected SKTEditor myEditor;
    protected JFileChooser chooser; 
    public boolean doIt = true;
    
    protected AbstractFileAction(String name, Icon icon) {
	super(name, icon);
       
    }


    /**
     * Gets the specified icon using the SKTEditor's standard property values 
     * and resource search algorithm.
     * 
     * @param imageName the name of the icon image file
     * @return the requested icon or null if it couldn't be found
     */
    public static Icon getIcon(String imageName) {

        return SKTEditor.getIcon(imageName);
    }


    /**
     * Close a file that is opened.
     *
     * Currently, the SKTEditor can only have a single file opened at a time.
     * This will need to be changed if and when SKTEditor can handle multiple
     * files.
     */
    public void performCloseAction() {
	StringBuffer msg = new StringBuffer();
	
	try {
	    myEditor.closeFile();
	} catch (CDFException exc) {
	    exc.printStackTrace();
	    msg.append("CDF Error:\n");
	    msg.append(exc.getMessage());
	    JOptionPane.showMessageDialog(myEditor.sharedInstance(), 
					  msg.toString(),
					  "SKTEditor: CDFException",
					  JOptionPane.ERROR_MESSAGE);
	} catch (Exception e) {
	    e.printStackTrace();
	    JOptionPane.showMessageDialog(myEditor.sharedInstance(), 
					  e.getMessage(),
					  "SKTEditor: Exception",
					  JOptionPane.ERROR_MESSAGE);
	}
    }

    /**
     * Will perform a save or save as.
     *
     * @param saveAs if true that prompt user for a file name
     * @param closeFile if true then close the file after saving
     * @return true if save successfully completed.  false if operation
     *              fails or is cancelled.
     */
    public boolean performSaveAction(boolean saveAs, boolean closeFile) {

        return doIt = myEditor.saveCurrentFile(saveAs, closeFile);
    }

} // FileAction
