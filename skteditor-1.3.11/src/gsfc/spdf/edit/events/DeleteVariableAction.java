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
 * $Id: DeleteVariableAction.java,v 1.11 2022/03/24 10:38:28 btharris Exp $
 */

//$Id: DeleteVariableAction.java,v 1.11 2022/03/24 10:38:28 btharris Exp $
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
 * Delete the currently selected variable.
 *
 * Note:  If the variable is metadata or support_data, then a check is
 * performed to determine if the variable is in use, if so, then the variable
 * will not be removed and the user will be notified where the variable is in
 * use.  This link will have to be removed before the variable can be deleted.
 *
 * @author Phil Williams
 * @version $Revision: 1.11 $
 */

public class DeleteVariableAction extends AbstractAction {
    
    private static final String DEFAULT_NAME = "Delete";

    private static final String DEFAULT_ICON_NAME = "Delete16.gif";

    private static Icon DEFAULT_ICON;

    private SKTEditor myEditor;

    public DeleteVariableAction(SKTEditor myEditor) {
	this(myEditor, DEFAULT_NAME, DEFAULT_ICON);
    }

    public DeleteVariableAction(SKTEditor myEditor, String name, Icon icon) {
	super(name, icon);
	this.myEditor = myEditor;
    }

    public void putValue(String key, Object value) {
	if (value != null)
	    super.putValue(key, value);
    }

    public void actionPerformed(ActionEvent event) {
	SKTEditor.setWaitCursor();
	Object vObj = myEditor.variablePanel.getSelectedVar();
	if (vObj == null)
	    JOptionPane.showMessageDialog(SKTEditor.edit, 
					  "No variable selected",
					  "SKTEditor: Error",
					  JOptionPane.ERROR_MESSAGE);
	else {
	    int result = JOptionPane.
		showConfirmDialog(SKTEditor.edit, 
				  "Are you sure you want to delete "+
				  vObj.toString());
	    if (result == JOptionPane.YES_OPTION) {
		SKTEditor.setWaitCursor();
		try {
		    myEditor.variablePanel.deleteSelectedVar(
                        new DeleteReferenceAttributeConfirmer());
		} catch (ISTPComplianceException ie) {
		    JOptionPane.showMessageDialog(SKTEditor.edit, 
						  "ISTP Error:\n"+
						  ie.getMessage(),
						  "SKTEditor: CDFException",
						  JOptionPane.ERROR_MESSAGE);
		} catch (CDFException exc) {
		    JOptionPane.showMessageDialog(SKTEditor.edit, 
						  "CDF Error:\n"+
						  exc.getMessage(),
						  "SKTEditor: CDFException",
						  JOptionPane.ERROR_MESSAGE);
		}
	    }
	}
	SKTEditor.setDefaultCursor();
    }
    

    /**
     * Confirmation callback class that confirms whether the user wants
     * variable attributes that reference the variable that is about to be
     * also deleted.
     */
    private static class DeleteReferenceAttributeConfirmer
        implements gsfc.spdf.istp.Variable.DeleteReferenceAttributeConfirmer {

        /**
         * Flag indicating whether the user want all subsequent variable
         * attributes deleted without further conformation.
         */
        private boolean deleteAll = false;


        /**
         * Obtains the user's confirmation of whether the given variable's
         * attribute should be deleted.
         *
         * @param variable the variable which has an attribute
         *            containing the name of the variable that is
         *            about to be deleted
         * @param attribute the attribute containing the name of the
         *            variable that is about to be deleted
         * @return true if the given attribute is also to be deleted.
         *             false if the attribute is to be retained.
         */
        public boolean confirmAttributeDelete(
            gsfc.spdf.istp.Variable variable, 
            gsfc.nssdc.cdf.Attribute attribute) {

            if (deleteAll) {

                return true;
            }
            else {

                String[] options = new String[] {
                    "Yes", "No", "Yes to all"
                };                     // dialog options

                int reply = JOptionPane.showOptionDialog(SKTEditor.edit, 
                    "Do you want to delete the '" + attribute.getName() +
                    "' attribute\nof variable '" + variable.getName() +
                    "' which appears to reference\n" +
                    "the variable that is about to be deleted?",
                    "Select an Option",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);
                                       // user's reply
                switch (reply) {

                case JOptionPane.YES_OPTION:

                    return true;

                case JOptionPane.NO_OPTION:
                case JOptionPane.CLOSED_OPTION:

                    return false;

                default:  // yes to all

                    deleteAll = true;
                    return true;
                }
            }

        }
    }


    static {
	String iconPath = SKTEditor.propertyPath +
	    SKTEditor.appProperties.getProperty("icon.path");
        try {
	    DEFAULT_ICON = new ImageIcon(
                                   DeleteVariableAction.class.getResource(
                                               iconPath + DEFAULT_ICON_NAME));
        }
        catch(NullPointerException e) {
            System.err.println("cannot find icon " + iconPath +
                               DEFAULT_ICON_NAME + " -- continuing without it");
        };
    }

} // DeleteVariableAction
