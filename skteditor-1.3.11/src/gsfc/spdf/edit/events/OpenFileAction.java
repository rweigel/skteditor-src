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
 * $Id: OpenFileAction.java,v 1.19 2022/03/24 10:38:28 btharris Exp $
 */

package gsfc.spdf.edit.events;

import javax.swing.*;
import javax.swing.event.*;
import java.io.File;

import java.awt.event.*;

import gsfc.nssdc.cdf.CDF;

import gsfc.spdf.edit.guis.*;
import gsfc.spdf.gui.StatusBar;
import gsfc.spdf.istp.TerrestrialTime2000;


/**
 * Open an existing CDF.
 *
 * @author Phil Williams 
 * @version $Revision: 1.19 $
 */
public class OpenFileAction extends AbstractFileAction {
    
    private static final String DEFAULT_NAME = "Open";

    private static final String DEFAULT_ICON_NAME = "Open16.gif";

    private static Icon DEFAULT_ICON;

    public OpenFileAction(SKTEditor myEditor) {
	this(myEditor, DEFAULT_NAME, DEFAULT_ICON);
    }

    public OpenFileAction(SKTEditor myEditor, String name, Icon icon) {
	super(name, icon);
	this.myEditor = myEditor;
    }

    public void putValue(String key, Object value) {
	if (value != null)
	    super.putValue(key, value);
    }

    public void actionPerformed(ActionEvent event) {
	//boolean doIt = true;
          doIt = true;
	SKTEditor.setWaitCursor();

	if (myEditor.theCDF != null) {
	    int result =
		JOptionPane.
		showConfirmDialog(SKTEditor.edit, 
				  "Do you want to save changes\n" +
                                  "before opening a new file?",
				  "Save Changes?",
				  JOptionPane.YES_NO_CANCEL_OPTION);
	    if (result == JOptionPane.YES_OPTION)
		performSaveAction(myEditor.shouldSaveAs(), true);
	    else if (result == JOptionPane.NO_OPTION)
		performCloseAction();
	    else
		doIt = false;
	}
	
	if (doIt) {
	    chooser = myEditor.getFileChooser();
            chooser.setCurrentDirectory(new File(SKTEditor.chooserCurrentDir));
        
	    chooser.rescanCurrentDirectory();
	    int retVal = 
		chooser.showOpenDialog(myEditor.sharedInstance());
	    if (retVal == JFileChooser.APPROVE_OPTION) {
		
		File theFile = chooser.getSelectedFile();

		try {
                    SKTEditor.chooserCurrentDir = 
                        chooser.getCurrentDirectory().getPath();

		    myEditor.setStatus("Opening "+theFile.getPath(), 
                                       StatusBar.INFO, false, false);
		    myEditor.openFile(theFile.getPath());
		    
		}
                catch (gsfc.nssdc.cdf.CDFException e) {

                    myEditor.theCDF = null;

                    if (e.getCurrentStatus() == 
                        CDF.TT2000_USED_OUTDATED_TABLE) {

                        StringBuilder errMsg = new StringBuilder();
                                       // error message
                        errMsg.append("<html>CDF Open Error:<br>");
                        errMsg.append("The file cannot be opened for " +
                            "one of the following reasons:<ol>" +
                            "<li>It contains TT2000 data that was<br>" +
                            "made with an out-of-date leap second " +
                            "table.</li>" +
                            "<li>This application's CDF library is " +
                            "using an<br>" +
                            "out-of-date leap second table. The date " +
                            "of the last<br> leap second known to " +
                            "this application is ");
                        errMsg.append(
                            TerrestrialTime2000.
                                getLastDateInLeapSecondsTableAsString());
                        errMsg.append(".</li></ol>" +
                            "Use one of the following remedies to " +
                            "resolve the issue:<ol>" +
                            "<li>Use the cdfconvert utility with the " +
                            "-adjusttt2000 option<br>" +
                            "to correct the CDF file.</li>" +
                            "<li>Use a newer version of the CDF " +
                            "library or a newer<br>" +
                            "leap second table.</li>" +
                            "</ol></html>");

                        JOptionPane.showMessageDialog(
                            myEditor.sharedInstance(), 
                            errMsg.toString(),
                            "SKTEditor: CDFException",
                            JOptionPane.ERROR_MESSAGE);
                    }
                    else if (e.getCurrentStatus() != 
                             CDF.CHECKSUM_ERROR) {

		        e.printStackTrace();

                        JOptionPane.showMessageDialog(
                            myEditor.sharedInstance(), 
                            "CDF Error:\n" + e.getMessage(),
                            "SKTEditor: CDFException",
                            JOptionPane.ERROR_MESSAGE);
                    }
		}
                catch (Exception e) {

		    e.printStackTrace();

		    JOptionPane.showMessageDialog(
                        myEditor.sharedInstance(), 
                        e.getMessage(),
                        "SKTEditor: Exception",
                        JOptionPane.ERROR_MESSAGE);

		    myEditor.theCDF = null;
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
                                  OpenFileAction.class.getResource(
                                      iconPath + DEFAULT_ICON_NAME));
        }
        catch(NullPointerException e) {
            System.err.println("cannot find icon " + iconPath +
                               DEFAULT_ICON_NAME + " -- continuing without it");
        };
    }
    
} // OpenFileAction
