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
 * $Id: DescriptionPanel.java,v 1.26 2022/03/24 10:38:32 btharris Exp $
 */
package gsfc.spdf.edit.guis;

// Swing Imports
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import javax.swing.table.*;

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
import gsfc.spdf.gui.*;
import gsfc.spdf.util.*;
import gsfc.spdf.table.*;

// Local Imports
import gsfc.spdf.edit.events.*;
import gsfc.spdf.edit.util.SKTUtils;

/**
 * A panel to display a variable's FIELDNAM, CATDESC, VAR_NOTE and DICT_KEY
 * attributes
 */
public class DescriptionPanel
    extends JLabeledPanel
    implements CDFConstants, VariableEventListener,AttributeChangeListener
{
    protected VariablePanel myVP;

    LabeledTextFieldPanel fieldnam;

    JLabeledTA catdesc, varnotes;

    public DescriptionPanel (VariablePanel myVP) {
	super("Description");
	
	GridBagLayout gbl = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();

	setLayout(gbl);
	
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 0.15;
    gbc.ipadx = 40;
    gbc.ipady = 10;
    gbc.insets = new Insets(0,2,1,2);

    fieldnam = new LabeledTextFieldPanel("Expanded Label", 
                                         new EmptyBorder(0, 0, 0, 0), 
                                         1, 30);
    gbc.gridy = 0;
  //  gbc.ipady = 25;
    gbl.setConstraints(fieldnam, gbc);	
	add(fieldnam);

	catdesc = new JLabeledTA("One-Line Description", 3, 30);
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridy = 1;
    gbc.ipady = 5;
    gbl.setConstraints(catdesc, gbc);	
	add(catdesc);

	
   varnotes = new JLabeledTA("Variable Notes", 4, 30);
    gbc.gridy = 2;
//    gbc.ipady = 25;
    gbl.setConstraints(varnotes, gbc);	
	add(varnotes);

 
    }

    public  void reset() {

	fieldnam.reset();
	catdesc.reset();
	varnotes.reset();


	fieldnam.setEnabled(false);
	catdesc.setEnabled(false);
	varnotes.setEnabled(false);
    }
    
     public void attributeChanged(AttributeChangeEvent e) {
        Variable var = e.getVariable();
//        int type = e.getID();
        String  vartype = null;

         try {
            
//            vartype  = ((String)var.getEntryData("VAR_TYPE")).toLowerCase();
             vartype = VarType.get(var);
       
         } catch (CDFException exc) {
                          // Should never happen
          System.err.println("DescriptionPanel.attributeChanged:");
          exc.printStackTrace();      
          return;
        }               
        if (vartype.equals("ignore_data"))
        { 
            fieldnam.setEnabled(false); 
            catdesc.setEnabled(false);
            varnotes.setEnabled(false); 
          
        }
        else {
            
            fieldnam.setEnabled(true); 
            catdesc.setEnabled(true);
            varnotes.setEnabled(true); 
        }
        
    }

    // Set the descPanel
    public  void set(Variable var) {
	String keyword;
	
	try {
	    fieldnam.set(SKTUtils.getVattrEntryData(var, "FIELDNAM"));
	} catch (CDFException e) {
	    myVP.myEditor.setStatus(e.getMessage(),
			       StatusBar.ERROR, true, true);
	    fieldnam.set("");
	}
	
	try {
	    catdesc.set(SKTUtils.getVattrEntryData(var, "CATDESC"));
	} catch (CDFException e) {
	    myVP.myEditor.setStatus(e.getMessage(),
			       StatusBar.ERROR, true, true);
	    catdesc.set("");
	}
	
	try { 
	    varnotes.set(SKTUtils.getVattrEntryData(var, "VAR_NOTES"));
	    varnotes.textArea.setCaretPosition(0);
	} catch (CDFException e) {
	    myVP.myEditor.setStatus(e.getMessage(),
			       StatusBar.ERROR, true, true);
	    varnotes.set("");
	}
	    

     
	fieldnam.setEnabled(true);
	catdesc.setEnabled(true);
	varnotes.setEnabled(true);
    }

    public void save(Variable var) {
	// save the field name
	String fieldname = (String)fieldnam.get();
	if (!fieldname.equals(""))
	    try {
		SKTUtils.putVattrEntry(var, "FIELDNAM", 
				       CDF_CHAR, fieldname);
	    } catch (CDFException e) {
		myVP.myEditor.setStatus(e.getMessage(), StatusBar.ERROR, 
				   true, true);
	    }
	
	// save the catalog description
	String description = (String)catdesc.get();
	if (!description.equals(""))
	    try {
		SKTUtils.putVattrEntry(var, "CATDESC", CDF_CHAR, 
				       description);
	    } catch (CDFException e) {
		myVP.myEditor.setStatus(e.getMessage(), StatusBar.ERROR, 
				   true, true);
	    }
	
	// save the dictionary key

	
	// save the VAR_NOTES
	String notes = (String)varnotes.get();
	
	try {
	    if (!notes.equals(""))
		    SKTUtils.putVattrEntry(var, "VAR_NOTES", CDF_CHAR, 
				       notes);
	    else if (SKTUtils.getVattrEntryData(var, "VAR_NOTES") != null)
	    {
		    var.getMyCDF().getAttribute("VAR_NOTES").
			deleteEntry(var.getID());
	    }
	} catch (CDFException e) {
	    myVP.myEditor.setStatus(e.getMessage(), StatusBar.ERROR, 
				   true, true);
	}
   
    }

    public void performVariableAction(VariableEvent e) {
	// for future use
    }

}
