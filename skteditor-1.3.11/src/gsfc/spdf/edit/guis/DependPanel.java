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
 * $Id: DependPanel.java,v 1.32 2022/08/02 12:25:35 btharris Exp $
 */
package gsfc.spdf.edit.guis;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import javax.swing.table.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.lang.reflect.*;

import gsfc.nssdc.cdf.*;
import gsfc.nssdc.cdf.util.*;

import gsfc.spdf.gui.*;
import gsfc.spdf.util.*;
import gsfc.spdf.table.*;

import gsfc.spdf.edit.events.*;
import gsfc.spdf.edit.util.SKTUtils;

/**
 * A panel to display a variable's DEPEND_x attributes
 */
public class DependPanel
    extends JLabeledPanel
    implements CDFConstants, AttributeChangeListener {

    private VariablePanel myVP;
    private DependCB[] dependCb;

    public DependPanel(VariablePanel myVP) {
	this(myVP, "Depends");
    }

    public DependPanel(VariablePanel myVP, String title) {
	// Depends panel
	super(title);
	
	this.myVP = myVP;

/* */
       addComboBoxes(7);
/* */
    }

    private void addComboBoxes(int number) {

        dependCb = new DependCB[number];

        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        JPanel panel = new JPanel(gbl);
        setMinimumSize(new Dimension(250, 500));

        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.1;
        gbc.ipady = 15;
        gbc.ipadx = 40;
        gbc.insets = new Insets(0,2,0,2);
    
        for (int i = 0; i < dependCb.length; i++) {

            dependCb[i] = new DependCB("Depend " + i, false);
            dependCb[i].comboBox.addVariableEventListener( myVP );
            gbc.gridy = i;
            gbl.setConstraints(dependCb[i], gbc);	

            panel.add(dependCb[i]);
        }

        JScrollPane scrollPane =
            new JScrollPane(panel, 
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane, BorderLayout.CENTER);
    }
    
    public void reset() {

/* */
        for (int i = 0; i < dependCb.length; i++) {

	    dependCb[i].reset();
            dependCb[i].setEnabled(false);
        }
/* */
/*
dependCb = new DependCB[0];
removeAll();
*/
    }
    
    /**
     * Set the depends based on the current CDF specs.
     *
     * @param var CDF Variable.
     */
    public void set(Variable var) {
        int numDims  = (int)var.getNumDims();
        long datatype = var.getDataType();
        boolean recvary  = var.getRecVariance();
// addComboBoxes(numDims + (recvary ? 1 : 0));
        Variable depend;

        Vector currentList =
        //  (Vector)myVP.getControls().get("variableList");
        myVP.getVariableList();
        System.err.println("DependPanel.set: nDims = "+numDims+
			   " revary = "+recvary+
			   " datatype = "+CDFUtils.getStringDataType(datatype));

        if(recvary) {

            for (int i = 1; i <= numDims && i < dependCb.length; i++) {

                dependCb[i].setEnabled(true);
                dependCb[i].setVariable(var);
                dependCb[i].setVariableList(currentList);
                try {

                    depend = var.getMyCDF().getVariable((String)
                                 SKTUtils.getVattrEntryData(
                                     var, "DEPEND_" + i));
                    dependCb[i].set(dependCb[i].containsVar(depend) ? 
                                    depend : null);
                } 
                catch (CDFException e) {

                    dependCb[i].comboBox.setSelectedIndex(-1);
                }
            }
            if (numDims >= 0) {

/* we now want to allow time type depends
                if (datatype != CDFConstants.CDF_EPOCH && 
                    datatype != CDFConstants.CDF_EPOCH16 &&
                    datatype != CDFConstants.CDF_TIME_TT2000) {
*/

                    dependCb[0].setEnabled(true);
                    dependCb[0].setVariable(var);
                    dependCb[0].setVariableList(currentList);

                    try {
                    		    
                        depend = var.getMyCDF().getVariable((String)
                                     SKTUtils.getVattrEntryData(
                                         var, "DEPEND_0"));
		    
                        dependCb[0].set(
                            dependCb[0].containsVar(depend) ? 
                            depend : null);
                    }
                    catch (CDFException e) {

                        dependCb[0].comboBox.setSelectedIndex(-1);
                    }
                    catch (ClassCastException e) {

                        dependCb[0].comboBox.setSelectedIndex(-1);
                    }
/*
                }
                else {
		
                    dependCb[0].comboBox.setSelectedIndex(-1);
                    dependCb[0].setEnabled(false);
                }
*/
            }
        }
    }

    /**
     * Save the current depend_x to the given variable
     *
     * @param var CDF Variable.
     */
    public void save(Variable var) {

	int numDims = (int)var.getNumDims();

        for (int i = 0; i <= numDims && i < dependCb.length; i++) {

	    if (dependCb[i].comboBox.getSelectedIndex() != -1) {

		try {

		    SKTUtils.putVattrEntry(var, "DEPEND_" + i, CDF_CHAR,
                        ((Variable)dependCb[i].get()).getName());
		}
                catch (CDFException e) {

		    myVP.myEditor.setStatus(e.getMessage(), 
                        StatusBar.ERROR, true, true);
		}
            }
        }
    }
    
    
    /**
     * Invoked when an AttributeChangeEvent occurs.
     * 
     * @param event attribute change event
     */
    public void attributeChanged(AttributeChangeEvent event) {

        Variable var = event.getVariable();
                                       // the variable whose attribute 
                                       //  has changed
        int type = event.getID();      // the type of attribute change 
                                       //  that has occurred

        // Save the changes before doing anything else.
        save(var);
        
        String varType = null;         // VAR_TYPE value

        // reset the panel
        try{

            varType = VarType.get(var);

            if (!varType.equals("ignore_data")) 
            
                reset();
	}
        catch (CDFException e) {

            // Should never happen
            System.err.println(
                "DependPanel could not find a variable type");
            return;
        }
    	

        if (type == AttributeChangeEvent.VAR_TYPE_CHANGE) {

	    System.err.println("DependPanel: Got VAR_TYPE_CHANGE.");

            handleVarTypeChanged(var, varType);
        }
        else if (type == AttributeChangeEvent.DISPLAY_TYPE_CHANGE) {

            System.err.println("DependPanel: Got DISPLAY_TYPE_CHANGE.");

            handleDisplayTypeChanged(var);
	}
    }	    


    /**
     * Handles an event associated with a VAR_TYPE change.
     *
     * @param var the variable whose type has changed
     * @param varType new variable type value.
     */
    private void handleVarTypeChanged(Variable var, String varType) {
    
	if (varType.equals("support_data")) {

            handleDataVarTypeChanged(var);
        } 
        else if (varType.equals("data")) {

            handleDataVarTypeChanged(var);
        }
        else if(varType.equals("ignore_data")) {

            for (int i = 0; i < dependCb.length; i++) {

                dependCb[i].setEnabled(false);
            }
        }
/* */
        else {
            
            dependCb[0].comboBox.setSelectedIndex(-1);
            dependCb[0].setEnabled(false);
        }
/* */
    }


    /**
     * Handles an event associated with a VAR_TYPE change to "data".
     *
     * @param var the variable whose type has changed
     */
    private void handleDataVarTypeChanged(Variable var) {

        // enable depend comboboxes depending upon dimension
        //
        set(var);
    }


    /**
     * Handles an event associated with a DISPLAY_TYPE change.
     *
     * @param var the variable whose type has changed
     */
    private void handleDisplayTypeChanged(Variable var) {

        try {

            String varType = VarType.get(var);
                                       // VAR_TYPE attribute value

            if (!varType.equalsIgnoreCase("metadata")) {

                set(var);
            }
        }
        catch (CDFException exc) {

            // Should never happen
            System.err.println(
                "DependPanel.attributeChanged: should never happen");
            exc.printStackTrace();
        }
    }

}
