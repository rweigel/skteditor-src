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
 * $Id: AxisInfoPanel.java,v 1.55 2022/08/02 13:01:42 btharris Exp $
 */
package gsfc.spdf.edit.guis;

// Swing Imports
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import javax.swing.table.*;
import javax.swing.event.ChangeEvent;

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
 * A panel to display a variable's Labels, scale, format and units
 */
public class AxisInfoPanel
extends JLabeledPanel
implements AttributeChangeListener, CDFConstants
{
    private VariablePanel myVP;
    private CBandTablePanel labelPanels[] = new CBandTablePanel[6];
    private CBandTablePanel scale;
    private CBandTablePanel format;
    private CBandTablePanel units;
    
    /**
     * A string for use in type comparisons.
     */
    private static final String EMPTY_STRING = "";
    
    
    public AxisInfoPanel(VariablePanel myVP) {
        super("Axis Information");
        this.myVP = myVP;
        
        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.weightx = 0.33;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0,1,1,1);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.BOTH;
        
        for (int i = 0; i < labelPanels.length; i++) {

            switch (i % 3) {
            case 0:
                gbc.gridwidth = 1;
                break;
            case 1:
                gbc.gridwidth = GridBagConstraints.RELATIVE;
                break;
            default:
                gbc.gridwidth = GridBagConstraints.REMAINDER;
                break;
            }
            labelPanels[i] = 
                new CBandTablePanel(myVP, "Label " + (i + 1), 
                        "LABL_PTR_" + (i + 1));
            gbl.setConstraints(labelPanels[i], gbc);
            add(labelPanels[i]);
        }
        
        Vector items = new Vector();
        items.addElement("linear");
        items.addElement("log");
        gbc.gridwidth = 1;
        scale = new CBandTablePanel(myVP, "Scale Type", "SCAL_PTR", false, items);
        gbl.setConstraints(scale, gbc);
        add(scale);
        
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        format = new CBandTablePanel(myVP, "Format", "FORM_PTR");
        gbl.setConstraints(format, gbc);
        add(format);
        
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        units = new CBandTablePanel(myVP, "Units", "UNIT_PTR");
        gbl.setConstraints(units, gbc);
        add(units);
        
    }
    
    public void reset() {
        
        for (int i = 0; i < labelPanels.length; i++) {

            labelPanels[i].reset();
            labelPanels[i].setEnabled(false); 
            labelPanels[i].comboBox.setEditable(i == 0 ? true : false);
        }
        
        scale.reset();
        scale.setEnabled(false);  scale.comboBox.setEditable(false);
        format.reset();
        format.setEnabled(false); format.comboBox.setEditable(true);
        units.reset();
        units.setEnabled(false);  units.comboBox.setEditable(true);
    }
    
    private void set(Variable var) {
        setLabels(var);
        setFormatUnits(var);
    }
    
    private void setLabels(Variable var) {

        gsfc.spdf.istp.Variable istpVar = 
            new gsfc.spdf.istp.Variable(var);
                                       // istp representation of var
        boolean getAxis = false,  // Allow lablaxis to be entered
        noPTR = true;        // Do not allow labl_ptr_1 to be entered
        
        int numDims = (int)var.getNumDims();
        
        try {
            String  vartype  = VarType.get(var);
                        
            if (vartype.equals("data") ||
            vartype.equals("support_data")) {
                
                String displaytype = null;
                
                try {

                    displaytype = ((String)var.getEntryData("DISPLAY_TYPE"));
                } 
                catch (CDFException exc) {

                    // ignore it
                }
                catch (ClassCastException e) {

                    // not a good value so continue with null
                }
                
                Object [] labels = {null, null, null};
                
                getAxis = (numDims == 0) || (numDims == 1);
                if (displaytype != null) {
                    
                    noPTR = !istpVar.lablPtrIsAllowed();
                }
                
                // The currently available variables
                Vector currentList = var.getMyCDF().getVariables();
 
                
                try {

                    try {
  
                        labels = istpVar.getLabels();
                        
                    } catch (Exception e) {
                    }

                    for (int i = 1; i < numDims; i++) {

                        labelPanels[i].setEnabled(true);
                        labelPanels[i].setVariable(var);
                        labelPanels[i].setVariableList(currentList);
                        if (i < labels.length) {

                            labelPanels[i].set(labels[i]);
                        }
                        else {
                            labelPanels[i].set(labels[0]);
                        }
                    }
                    switch (numDims) {
                        case 6:
                        case 5:
                        case 4:
                        case 3:
                        case 2:
                        case 1:
                            if (noPTR) {
                                labelPanels[0].reset(true);

                            } else {
                                labelPanels[0].setVariable(var);
                               labelPanels[0].setVariableList(currentList);
                            }
                        case 0:
                            if(numDims==0) labelPanels[0].comboBox.removeAllItems(true);
                            labelPanels[0].setEnabled(true);             
                            labelPanels[0].comboBox.setEditable(vartype.equals("data") && !noPTR ?
                                                        false: true);                       
                            if (labels[0] == null ||(!currentList.contains(labels[0] ) ||
                            (currentList.contains(labels[0]) &&
                            VarType.get((Variable)labels[0]).equals("metadata"))))        
                                labelPanels[0].set(labels[0]);
                            
                            break;
                        default:

                            break;
                   }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Error setting labels: "+e);
                }
           }
        } catch (CDFException vtexc) {
            // should only happen if VAR_TYPE is not defined, which is never
            vtexc.printStackTrace();
        }
    }
    
    /**
     * Set the format, units and scale fields
     *
     * @param var CDF variable whose attributes are to be set.
     */
    private void setFormatUnits(Variable var) {
        long numDims  = var.getNumDims();
        
        try {
            String  vartype  = VarType.get(var);
            if(vartype.equals("ignore_data"))
                
                return;
            
            if (vartype.equals("data") ||
            vartype.equals("support_data")) {
                if (numDims == 1) {
                    // The currently available variables
                    Vector currentList = var.getMyCDF().getVariables();
                    format.setVariable(var);
                    format.setVariableList(currentList);
                    units.setVariable(var);
                    units.setVariableList(currentList);
                    scale.setVariable(var);
                    scale.setVariableList(currentList);
                }
                else {
                    units.comboBox.removeAllItems(true);
                    format.comboBox.removeAllItems(true);
                    scale.comboBox.removeAllItems(true);
                }
                units.setEnabled(true);
                format.setEnabled(true);
                scale.setEnabled(true);
                
                // set the format
                try {
                    format.
                    set(var.getMyCDF().
                    getVariable((String)SKTUtils.
                    getVattrEntryData(var, "FORM_PTR")));
                } catch (CDFException e) {
                    try {
                        format.
                        set((String)SKTUtils.
                        getVattrEntryData(var, "FORMAT"));
                    } catch (CDFException e1) {
                        format.set(null);
                    }
                    catch (ClassCastException e1) {

                        format.set(null);
                    }
                }
                catch (ClassCastException e) {

                    format.set(null);
                }
                
                // set the units
                try {
                    units.
                    set(var.getMyCDF().
                    getVariable((String)SKTUtils.
                    getVattrEntryData(var, "UNIT_PTR")));
                } catch (CDFException e2) {
                    try {
                        units.
                        set((String)SKTUtils.
                        getVattrEntryData(var, "UNITS"));
                    } catch (CDFException e4) {
                        units.set(null);
                    }
                    catch (ClassCastException e4) {

                        units.set(null);
                    }
                }
                catch (ClassCastException e) {

                    units.set(null);
                }
                
                // set the scale
                try {
                    scale.
                    set(var.getMyCDF().
                    getVariable((String)SKTUtils.
                    getVattrEntryData(var, "SCAL_PTR")));
                    System.err.println("Set SCAL_PTR.");
                } catch (CDFException e2) {
                    try {
                        scale.
                        set((String)SKTUtils.
                        getVattrEntryData(var, "SCALETYP"));
                    } catch (CDFException e4) {
                        // ignore it
                    }
                    catch (ClassCastException e4) {

                    }
                }
                catch (ClassCastException e2) {

                }
            } else {   // metadata needs the format turned on and the default set
                format.reset(true);
                format.setEnabled(true);
                String sFormat = null;
                try {
//                    sFormat = (String)SKTUtils.getVattrEntryData(var, "FORMAT");
                    Object formatObj = 
                        SKTUtils.getVattrEntryData(var, "FORMAT");
                    if (formatObj != null) {

                        if (formatObj instanceof String) {

                            sFormat = (String)formatObj;
                        }
                        else if (formatObj instanceof String[]) {

                            sFormat =  ((String[])formatObj)[0];
                        }
                    }

                    if (sFormat == null) {

                        // Treat a null value the same as the attribute not
                        //  existing

                        throw new CDFException("null FORMAT value");
                    };
                    format.set(sFormat);
                } 
                catch (CDFException e5) {

                    sFormat = "a" + (var.getNumElements() + 1);
                    format.set(sFormat);

                    System.out.println(" ");
                    System.out.println("FORMAT attribute or its value was " +
                                       "missing for variable " + var.getName() +
                                       ".  It has been set to " + sFormat);
                }
            }
        } catch (CDFException vtexc) {
            // should only happen if VAR_TYPE is not defined, which is never
            vtexc.printStackTrace();
        }
    } 
    
    /**
     * Save the current setting to the given variable
     *
     * @param var CDF variable to save settings of.
     */
    public void save(Variable var) {

        for (int i = 0; i < labelPanels.length; i++) {

            saveLabel(i, var);
        }
        saveScale( var);
        saveFormat( var);
        saveUnit( var);
    }
                                            
    
    private void saveLabel(int i, Variable var) {

        save(labelPanels[i], 
            i == 0 ? "LABLAXIS" : EMPTY_STRING, 
            "LABL_PTR_" + (i + 1), var);
    }
    
    private void saveScale(Variable var)
    {
        save(scale, "SCALETYP", "SCAL_PTR",var);
    }
    
    private void saveFormat(Variable var)
    {
        save(format, "FORMAT", "FORM_PTR",var);
    }
    
    private void saveUnit(Variable var)
    {
        save(units, "UNITS", "UNIT_PTR",var);
    }
    
    private void save(
        CBandTablePanel comboTableElement, 
        String attrString,
        String attrPtr, 
        Variable var) {

        Variable axisPTR=null;
        Object  axisData=null;
        long numElements=1L;

        if (!(comboTableElement.get() instanceof Variable)) {

            if (comboTableElement.get() != null) {

                try {
                     
                    SKTUtils.putVattrEntry(var, attrString, CDF_CHAR,
                                           comboTableElement.get());
//                                           (String)comboTableElement.get());
                } 
                catch (CDFException e) {

                    e.printStackTrace();

                    JOptionPane.showMessageDialog(null, "Error saving " + 
                         var.getName() + "'s " + attrString + " attribute.\n" +
                         "Please report the problem to software maintenance.", 
                         "Software Error", JOptionPane.ERROR_MESSAGE);

                }
            }    
        } 
        else {

            if (comboTableElement.get() != null) {

                try {

                    axisPTR = (Variable)comboTableElement.get();
                
                    //notify table that editing is complete
                    if (comboTableElement.getDataTable().isEditing())
                        comboTableElement.getDataTable().getCellEditor().stopCellEditing();
                
                    axisData = comboTableElement.getDataTableModel().getData();
                    
                    if(axisData == null ) return;
                
                        SKTUtils.verifyArray(axisData,axisPTR.getDataType());

                    numElements = SKTUtils.getMaxNumElements(axisData);               
      
                    if (numElements > axisPTR.getNumElements()) {

                        gsfc.spdf.istp.Variable istpAxisPtr =
                            new gsfc.spdf.istp.Variable(axisPTR);
                                       // istp representation of axisPTR
                        gsfc.spdf.istp.Variable newAxisPtr =
                            istpAxisPtr.copy(numElements);
                                       // axisPtr with larger numElements
                        axisPTR = newAxisPtr.getCdfVariable();
                    } 
                
                    axisPTR.putRecord(0, axisData);
                    comboTableElement.updateTable(axisPTR);
               
                    try {

                        SKTUtils.putVattrEntry(var, attrPtr,
                            CDF_CHAR, axisPTR.getName());
                    } catch (CDFException e) {
                        myVP.myEditor.setStatus(e.getMessage(),
                        StatusBar.ERROR,
                        true, true);
                    }
                } catch (CDFException cdfe) {
                    System.err.println("Should not happen: "+cdfe.getMessage());
                }
            }               
        }
    }
    
        
    public void attributeChanged(AttributeChangeEvent e) {
        Variable var = e.getVariable();
        int type = e.getID();
        String  vartype = null;
        // Save the changes before doing anything else.
        save(var);        
                   
         try {
            
             vartype  = VarType.get(var);
       
         } catch (CDFException exc) {
                          // Should never happen
          System.err.println("AxisInfoPanel.attributeChanged:");
          exc.printStackTrace();      
          return;
        }               
        if (vartype.equals("ignore_data")) { 

            for (int i = 0; i < labelPanels.length; i++) {

                labelPanels[i].setEnabled(false); 
            }
            scale.setEnabled(false);
            format.setEnabled(false);
            units.setEnabled(false);             
        }
        else {
            
            // reset the panel
            reset();
        }
        
        // Format and units only depend on VAR_TYPE
        if (type == AttributeChangeEvent.VAR_TYPE_CHANGE) {
            System.err.println("AxisInfoPanel: Got VAR_TYPE_CHANGE.");
            set(var);
        }
        else if (type == AttributeChangeEvent.DISPLAY_TYPE_CHANGE) {
            System.err.println("AxisInfoPanel: Got DISPLAY_TYPE_CHANGE.");

                if (!vartype.equals("metadata")) {
                    set(var);
                }

        }
    }
}

