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
 * $Id: PlotInfoPanel.java,v 1.39 2022/03/24 10:38:32 btharris Exp $
 */
package gsfc.spdf.edit.guis;

// Swing Imports
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import javax.swing.table.*;
import javax.swing.event.EventListenerList;

// Java imports
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.lang.reflect.*;

// CDF Imports
import gsfc.nssdc.cdf.*;
import gsfc.nssdc.cdf.util.*;

// SPDF imports
import gsfc.spdf.istp.DisplayType;
import gsfc.spdf.istp.ISTPCompliance;
import gsfc.spdf.gui.*;
import gsfc.spdf.util.*;
import gsfc.spdf.table.*;

// Local Imports
import gsfc.spdf.edit.events.*;
import gsfc.spdf.edit.util.SKTUtils;

import gsfc.spdf.istp.ISTPComplianceException;

/**
 * A panel to display a variable's VAR_TYPE and DISPLAY_TYPE
 *
 * These two attributes cause determine all other required attributes.  
 * If these items change an event is fired.  Use addAttributeChangeListener
 * to listen for these events.
 */
public class PlotInfoPanel
    extends JLabeledPanel
    implements CDFConstants, ItemListener, AttributeChangeListener
{
    protected VariablePanel myVP;
    private EventListenerList listenerList = new EventListenerList();
    private AttributeChangeEvent ace = null;

    private JLabeledCB cbdt, cbvt;
    private JLabeledTA tfpi;

    public PlotInfoPanel(VariablePanel myVP) {
	super("Plot Information");
	this.myVP = myVP;
	
	GridBagLayout gbl = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();
	setLayout(gbl);
	
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weighty = 0.1;
    gbc.ipadx = 40;  
    gbc.weightx = 1.0;
    gbc.insets = new Insets(0,2,0,2);
    
        cbvt = new JLabeledCB("Variable Type", 
			      myVP.myEditor.appProperties.
			      getPropertyVector("variable.types"),
			      false);
	cbvt.comboBox.addItemListener( this );
        cbvt.comboBox.setToolTipText(
            "<html><table>" +
            "<tr><td>Data</td><td>suitable for ploting/listings</td></tr>" +
            "<tr><td>Support Data</td>" +
                "<td>attach to Data variables, depends, quality flags</td></tr>" +
            "<tr><td>Metadata</td>" +
                "<td>labels and other text attributes</td></tr>" +
            "<tr><td>Ignore Data</td><td>place holder or bad data</td></tr>" +
            "</table></html>");
        gbl.setConstraints(cbvt, gbc);	
        add(cbvt);

	cbdt = new JLabeledCB("Display Type", 
                              null,
//                              DisplayType.getDisplayText(),
			      false);
        gbc.gridx = 1;
        cbdt.comboBox.addItemListener( this );
        gbl.setConstraints(cbdt, gbc);	
        add(cbdt);
	

        tfpi = new JLabeledTA("Display Arguments",5,30);
        tfpi.setToolTipText("options to Display Type");
        gbc.weighty = 0.2;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbl.setConstraints(tfpi, gbc);	
        add(tfpi); 

	addAttributeChangeListener( this );
    }

    public void reset() {
	cbvt.comboBox.removeItemListener( this );
	cbdt.comboBox.removeItemListener( this );
	cbvt.reset();
	cbvt.setEnabled(false);
	cbdt.reset();
	cbdt.setEnabled(false);
	cbvt.comboBox.addItemListener( this );
	cbdt.comboBox.addItemListener( this );
	tfpi.reset();
	tfpi.setEnabled(false);
    }

    public void set(Variable var) {

	try {
//	    String raw = (String)SKTUtils.getVattrEntryData(var, "VAR_TYPE");
            String raw = VarType.get(var);

	    String vt;
	   if (raw != null)
		vt = TextUtils.toTitleCase(TextUtils.replaceChar(raw, "_"));
	    else {
		long datatype = var.getDataType();
		boolean variance = var.getRecVariance();
		if (((datatype == CDF_EPOCH)||(datatype == CDF_EPOCH16)||
                     (datatype == CDF_TIME_TT2000)) && variance &&
		    (var.getNumDims() == 0))
		    vt = "Support Data";
		else if (!variance) {
		    if ((datatype == CDF_CHAR) || (datatype == CDF_UCHAR))
			vt = "Metadata";
		    else 
			vt = "Support Data";
		} else
		    vt = "Data";
	    }
	    
            if (!vt.equalsIgnoreCase("data") &&
           !vt.equalsIgnoreCase("support data") &&
            !vt.equalsIgnoreCase("metadata") &&
            !vt.equalsIgnoreCase("ignore data"))
            {
           
               cbvt.addItem(vt);
            }
	    cbvt.set(vt);
	    cbvt.setEnabled(true);
	    saveVarType(var);
	} catch (CDFException vtexc) {
	    vtexc.printStackTrace();
	}
    }
    

    private void setDisplayType(Variable var) {

        gsfc.spdf.istp.Variable istpVar = 
            new gsfc.spdf.istp.Variable(var);
                                       // ISTP version of var
        String varType = istpVar.getType()[0];
                                       // ISTP variable type

	if (varType.equals("data")) {

            cbdt.setEnabled(true);
            tfpi.setEnabled(true);

            String[] rawDisplayType = istpVar.getDisplayTypeValue();
                                       // complete display type value
            if (rawDisplayType == null || rawDisplayType.length == 0) {

                try {

                    istpVar.setDefaultDisplayTypeValue();
                    rawDisplayType = istpVar.getDisplayTypeValue();

                    System.err.println(
                        "Created DISPLAY_TYPE attribute and set its " +
                        "initial value to '" + rawDisplayType[0] + "'");
                }
                catch (CDFException e) {

                    myVP.myEditor.setStatus("Could not create " +
                    "DISPLAY_TYPE",
                    StatusBar.ERROR, true, true);
                    return;
                }
            }

            setDisplayTypeItems(istpVar);

            String displayType = 
                DisplayType.getDisplayTypeValue(rawDisplayType[0]);
                                       // just the DisplayType portion 
                                       // of the complete value
            String displayTypeText = 
                DisplayType.getDisplayTypeText(displayType);
                                       // the textual variation to 
                                       // display
            if (displayTypeText == null) {

                // An unrecognized displayType value.  Add the value 
                // to the ComboBox so it will not get lost (changed 
                // to time_series) when the user selects the variable.

                displayTypeText = displayType;
                cbdt.addItem(displayTypeText);
            }

            cbdt.set(displayTypeText);

            String displayArgument = 
                DisplayType.getArgumentsValue(rawDisplayType[0]);
                                       // the arguments portion of the
                                       // complete display type value

            if (displayArgument != null) {

                System.err.println("Found display args of '" + 
                    displayArgument + "' for variable '" +
                                   var.getName() + "'");
                tfpi.set(displayArgument);
                tfpi.setEnabled(true);
            }
            else {

                System.err.println(
                    "No display args found for variable '" +
                                   var.getName() + "'");
                tfpi.set("");
            }
        } 
        else {                      // Other VAR_TYPEs have no display

            if(!varType.equals("ignore_data"))
                
                cbdt.comboBox.setSelectedIndex(-1);
            cbdt.setEnabled(false);
            if(!varType.equals("ignore_data"))
            
                tfpi.reset();
            tfpi.setEnabled(false);
        }
    }


    /**
     * Sets the DisplayType ComboBox items to be appropriate for the
     * given variable.
     *
     * @param var variable for which DisplayType ComboBox items are
     *            to be set.
     */
    private void setDisplayTypeItems(gsfc.spdf.istp.Variable var) {

        String[] validDisplayTypeTitles = 
            var.getValidDisplayTypeTitles();
                                       // valid DisplayType titles
        cbdt.reset();

        for (int i = 0; i < validDisplayTypeTitles.length; i++) {

            cbdt.addItem(validDisplayTypeTitles[i]);
        }
    }


    public void save(Variable var) {
	String vartype = (String)cbvt.get();
	saveVarType(var);
	if (vartype.equals("Data")) {
	    saveDisplayType(var);
	}
    }    

    private void saveVarType(Variable var) {
	try {

	    SKTUtils.putVattrEntry(var, "VAR_TYPE", CDF_CHAR, TextUtils.
				   replaceSpaces((String)cbvt.get(), '_').
				   toLowerCase());
	} catch (CDFException e) {
	    myVP.myEditor.setStatus(e.getMessage(), StatusBar.ERROR, 
			       true, true);
	}
    }

    private void saveDisplayType(Variable var) {

	String info = (String)tfpi.get(); 
	String plotinfo = (!info.equals("") ? ">"+info : "");

	// DISP_HANDLING
	String raw = (String)cbdt.get();
	System.err.println("Saving DISPLAY_TYPE: args = "+plotinfo+
			   "\n\traw = "+raw +"   " + var.getName());
	if (raw != null) {
	    String displaytype = TextUtils.
		replaceSpaces(raw, '_').toLowerCase();
	    try {
		SKTUtils.putVattrEntry(var, "DISPLAY_TYPE", CDF_CHAR, 
				       displaytype+plotinfo);
                
                
	    } catch (CDFException e) {
		myVP.myEditor.setStatus(e.getMessage(), StatusBar.ERROR, 
					true, true);
	    }
	} else {
	    try {
		Attribute a = var.getMyCDF().getAttribute("DISPLAY_TYPE");
		a.deleteEntry(var.getID());
	    } catch (CDFException e) {
		//ignore it
	    }
	}
    }

    /**
     * Save the attribute and notify all listeners that either the
     * VAR_TYPE or DISPLAY_TYPE has changed.
     */
    public void itemStateChanged(ItemEvent e) {
	Object source = e.getSource();
//	int id = e.getID();
	Variable var = myVP.getSelectedVar();
	int actionID;


	System.err.print("PlotInfoPanel:\t Firing AttributeChangeEvent: ");
	if (var != null) {
	    if (source == cbdt.comboBox 
                /* && e.getStateChange() == ItemEvent.DESELECTED*/) {
		System.err.println("DISPLAY_TYPE_CHANGE");
		saveDisplayType(var);
		actionID = AttributeChangeEvent.DISPLAY_TYPE_CHANGE;
	    } else if (source == cbvt.comboBox) {
		System.err.println("VAR_TYPE_CHANGE");
		saveVarType(var);
		actionID = AttributeChangeEvent.VAR_TYPE_CHANGE;
	    } else {
		actionID = -1;
		System.err.println("not fired");
	    }
	    
	    // If a valid action occured, fire an attribute change event
	    if (actionID != -1)
		fireAttributeChanged(new AttributeChangeEvent(source, 
							      var, 
							      actionID));
	}
    }
    
    public void addAttributeChangeListener(AttributeChangeListener l) {
	listenerList.add(AttributeChangeListener.class, l);
    }

    public void removeAttributeChangeListener(AttributeChangeListener l) {
	listenerList.remove(AttributeChangeListener.class, l);
    }

    protected void fireAttributeChanged(AttributeChangeEvent e) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for ( int i = listeners.length-2; i>=0; i-=2 ) {
            if ( listeners[i]==AttributeChangeListener.class ) {
                // Lazily create the event:
                // if (changeEvent == null)
                // changeEvent = new ChangeEvent(this);
                ((AttributeChangeListener)listeners[i+1]).attributeChanged(e);
            }
        }
    }   
	
    public void attributeChanged(AttributeChangeEvent e) {
	Variable var = e.getVariable();
	int type = e.getID();

	if (type == AttributeChangeEvent.VAR_TYPE_CHANGE) {
	    setDisplayType(var);
	}
    }


}
