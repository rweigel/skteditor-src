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
 * $Id: CDFSpecPanel.java,v 1.10 2022/03/24 10:38:32 btharris Exp $
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
import gsfc.nssdc.cdf.util.CDFUtils;

// SPDF imports
import gsfc.spdf.cdf.Value;
import gsfc.spdf.istp.Variable;
import gsfc.spdf.gui.*;
import gsfc.spdf.util.*;
import gsfc.spdf.table.*;

// Local Imports
import gsfc.spdf.edit.events.*;
import gsfc.spdf.edit.util.SKTUtils;

/**
 * A panel to display a variable's CDF specifications
 */
public class CDFSpecPanel
    extends JLabeledPanel
    implements CDFConstants //, ItemListener
{
    private VariablePanel myVP;
    //private SpecChangeListener scl;

    JLabeledTF
	name,
	datatype,
	recvary,
	ndim,
        sparseRecordType,
        compression,
        padValue;

    /**
     * Variable whose specification is currently being displayed.
     */
    private Variable istpVar = null;


    public CDFSpecPanel(VariablePanel myVP) {
	super("CDF Specifications", BoxLayout.Y_AXIS);
	this.myVP = myVP;

        JPanel rows[] = new JPanel[] {
            new JPanel(), 
            new JPanel()
        };
        for (JPanel row : rows) {

            row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
            add(row);
        }
        //
	// We do not need this name field because the name is 
        // highlighted to the left when selected.
        //
	name     = new JLabeledTF("Name", 10);
	name.textField.setEditable(false);
	name.textField.setOpaque(false);
	name.textField.setBorder(new EmptyBorder(0,0,0,0));

	datatype = new JLabeledTF("Data Type", 12); 
	datatype.textField.setEditable(false);
	datatype.textField.setOpaque(false);
	datatype.textField.setBorder(new EmptyBorder(0,0,0,0));

	recvary  = new JLabeledTF("Time Varying", 4);
	recvary.textField.setEditable(false);
	recvary.textField.setOpaque(false);
	recvary.textField.setBorder(new EmptyBorder(0,0,0,0));

	ndim     = new JLabeledTF("Dimensions", 10);
	ndim.textField.setEditable(false);
	ndim.textField.setOpaque(false);
	ndim.textField.setBorder(new EmptyBorder(0,0,0,0));

	compression     = new JLabeledTF("Compression", 19);
	compression.textField.setEditable(false);
	compression.textField.setOpaque(false);
	compression.textField.setBorder(new EmptyBorder(0,0,0,0));

	sparseRecordType     = new JLabeledTF("Sparse Recd", 9);
	sparseRecordType.textField.setEditable(false);
	sparseRecordType.textField.setOpaque(false);
	sparseRecordType.textField.setBorder(new EmptyBorder(0,0,0,0));

	padValue     = new JLabeledTF("Pad Value", 9);
	padValue.textField.setEditable(false);
	padValue.textField.setOpaque(false);
	padValue.textField.setBorder(new EmptyBorder(0,0,0,0));

	rows[0].add(name); rows[0].add(Box.createRigidArea(myVP.myEditor.hpad10));
	rows[0].add(datatype); rows[0].add(Box.createRigidArea(myVP.myEditor.hpad10));
	rows[0].add(recvary); rows[0].add(Box.createRigidArea(myVP.myEditor.hpad10));
	rows[0].add(ndim); rows[0].add(Box.createRigidArea(myVP.myEditor.hpad10));
        rows[0].add(compression); rows[0].add(Box.createRigidArea(myVP.myEditor.hpad10));
        rows[1].add(Box.createRigidArea(myVP.myEditor.hpad80));
        rows[1].add(Box.createRigidArea(myVP.myEditor.hpad40));
        rows[1].add(sparseRecordType); rows[1].add(Box.createRigidArea(myVP.myEditor.hpad10));
        rows[1].add(padValue); rows[1].add(Box.createRigidArea(myVP.myEditor.hpad10));

	// Sizes subpanel
	/*
	sizePanel = new JLabeledPanel("Sizes", BoxLayout.X_AXIS);
	sizes = new WholeNumberField [] { new WholeNumberField(0, 4),
					  new WholeNumberField(0, 4),
					  new WholeNumberField(0, 4)};
	sizePanel.add(sizes[0]);
	sizePanel.add(sizes[1]);
	sizePanel.add(sizes[2]);
	sizePanel.setEnabled(false);
	add(sizePanel);
	*/
    }
	
    ////////////////////////////////
    //                            //
    //            reset           //
    //                            //
    ////////////////////////////////

    public  void reset() {
	name.set("");
	datatype.set("");
	recvary.set("");
	ndim.set("");
        compression.set("");
        sparseRecordType.set("");
        padValue.set("");

	/*
	for (int i=0;i<3;i++)
	    sizes[i].setText("0");
	*/
    }

    ////////////////////////////////
    //                            //
    //             set            //
    //                            //
    ////////////////////////////////

    public  void set(gsfc.nssdc.cdf.Variable var) {

        istpVar = new Variable(var);   // ISTP version of var

	name.set(var.getName());
	datatype.set(CDFUtils.getStringDataType(var) + "/" +
                     var.getNumElements());
	recvary.set(""+var.getRecVariance());

	long numDims = var.getNumDims();
	StringBuffer sNumDims = new StringBuffer();
	sNumDims.append(numDims + ":");

	sNumDims.append("[");
	if (numDims != 0) {
	    long [] dimSizes = var.getDimSizes();
	    for (int i = 0; i < (int)numDims; i++) {
		//		sizes[i].setText(""+dimSizes[i]);
		sNumDims.append(dimSizes[i]+"");
		if (i!=(int)numDims-1) sNumDims.append(",");
	    }
	}
	sNumDims.append("]");
	ndim.set(sNumDims.toString());

        setCompression(istpVar);

        try {

            sparseRecordType.set(istpVar.getSparseRecordType().toString());
        }
        catch (CDFException e) {

            System.err.println(
                "CDFSpecPanel.set: CDFException getting the " +
                "sparse record type: " + e.getMessage());
            sparseRecordType.set("unknown");
        }

        Object padObj = null;

        try {

            padObj = istpVar.getPadValue();
        }
        catch (CDFException e) {

            System.err.println(
                "CDFSpecPanel.set: CDFException getting the " +
                "pad value: " + e.getMessage());
        }

        if (padObj != null) {

            if (padObj instanceof String) {

                padValue.set("'" + padObj + "'");
            }
            else {

                padValue.set(
                    Value.toString(padObj, var.getDataType()));
            }
        }
        else {

            padValue.set("");
        }
    }


    /**
     * Sets the compression display value of the given variable.
     *
     * @param var variable whose compression value is to be displayed.
     */
    private void setCompression(Variable var) {

        String compStr = var.getCompressionName();

        if (var.getCompressionType() == CDF.GZIP_COMPRESSION) {

            compStr += "." + var.getCompressionParms()[0];
        }
        compression.set(compStr);
    }


    /**
     * Invoked when the compression ComboBox has been selected or
     * deselected by the user.
     *
     * @param e state change event.
     */
/*
    public void itemStateChanged(ItemEvent e) {

        if (e.getStateChange() == ItemEvent.SELECTED) {

            if (istpVar != null) {

                String compressionName = (String)compression.get();
                                       // selected compression
                try {

                    istpVar.setCompression(compressionName, null);
                }
                catch (CDFException ex) {

                    System.err.println(
                        "CDFSpecPanel.itemStateChanged: " +
                        "CDFException: " + ex.getMessage());
                    System.err.println(
                        "CDFSpecPanel.itemStateChanged: " +
                        "compressionName = " + compressionName);
                }
            }
        }
    }
*/
}
