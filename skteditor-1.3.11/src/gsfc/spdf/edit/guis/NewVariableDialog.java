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
 * $Id: NewVariableDialog.java,v 1.18 2022/03/24 10:38:32 btharris Exp $
 */
package gsfc.spdf.edit.guis;


import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.lang.Character;

import javax.swing.*;
import javax.swing.border.*;

import gsfc.nssdc.cdf.CDF;
import gsfc.nssdc.cdf.Variable;

import gsfc.spdf.cdf.SparseRecordType;
import gsfc.spdf.istp.ISTPComplianceException;
import gsfc.spdf.istp.VirtualVariable;


/**
 * Dialog for specifying the characteristics of a new CDF variable.
 * 
 * @author B. Harris
 * @version $Revision: 1.18 $
 */
public class NewVariableDialog extends JDialog 
    implements PropertyChangeListener {

    /**
     * Dialog buttons.
     */
    protected String[] buttons =  {"Create", "Cancel"};

    /**
     * Dialog's option pane.
     */
    protected JOptionPane optionPane = null;

    /**
     * Panel containing characteristics of new variable.
     */
    protected NewVariablePanel newVariablePanel = null;

    /**
     * The CDF in which to create the new variable.
     */
    private CDF cdf = null;

    /**
     * Initial witdth of the dialog window
     */
    public final static int WIDTH = 350;
    
    /**
     * Initial height of the dialog window
     */
    public final static int HEIGHT = 450;
    
    
    /**
     * Creates a NewVariableDialog.
     * 
     * @param owner frame from which the dialog is displayed
     * @param title string to display in the dialog's title bar
     * @param modal true for modal dialog, false for one that allows other 
     *              windows to be active at the same time
     * @param cdf CDF in which to create the new variable
     */
    public NewVariableDialog(Frame owner, String title,
                             boolean modal, CDF cdf) {

        this(owner, title, modal, new NewVariablePanel(cdf), cdf);
    }


    /**
     * Creates a NewVariableDialog.
     * 
     * @param owner frame from which the dialog is displayed
     * @param title string to display in the dialog's title bar
     * @param modal true for modal dialog, false for one that allows other 
     *              windows to be active at the same time
     * @param cdf CDF in which to create the new variable
     * @param varName variable's initial name
     * @param dataType variable's initial data type
     * @param numElements variable's number of elements
     * @param recordVariance variable's record variance
     * @param dimensions variable's dimensionality
     * @param dimensionSizes size of variable's dimensions
     * @param dimensionVariances variances of variable's dimensions
     */
    public NewVariableDialog(Frame owner, String title,
                             boolean modal, CDF cdf, String varName,
                             long dataType, long numElements, 
                             boolean recordVariance, long dimensions, 
                             long[] dimensionSizes, long[] dimensionVariances) {

        this(owner, title, modal, 
             new NewVariablePanel(cdf, varName, dataType, numElements, 
                                  recordVariance, dimensions, dimensionSizes, 
                                  dimensionVariances), cdf);
    }


    /**
     * Creates a NewVariableDialog.
     * 
     * @param owner frame from which the dialog is displayed
     * @param title string to display in the dialog's title bar
     * @param modal true for modal dialog, false for one that allows other 
     *              windows to be active at the same time
     * @param varPanel panel containing the variable's characteristics
     * @param cdf CDF in which to create the new variable
     */
    public NewVariableDialog(Frame owner, String title,
                             boolean modal, NewVariablePanel varPanel,
                             CDF cdf) {

        super(owner, title, modal);
        this.cdf = cdf;

//        setSize(WIDTH, HEIGHT);
//        Dimension  screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//        setLocation(screenSize.width/2 - WIDTH/2,screenSize.height/2 - HEIGHT/2);
setLocationRelativeTo(null);


        newVariablePanel = varPanel;

        optionPane = new JOptionPane(newVariablePanel,
                                     JOptionPane.DEFAULT_OPTION, 
                                     JOptionPane.PLAIN_MESSAGE,
                                     null, buttons, buttons[0]);
              
        optionPane.addPropertyChangeListener(this);

        Container contentPane = getContentPane();
        
        optionPane.setLayout(new BorderLayout());
        optionPane.setBorder(new EmptyBorder(10,10,10,10));
        optionPane.add(newVariablePanel, BorderLayout.CENTER);
        optionPane.add(optionPane.getComponent(1),BorderLayout.SOUTH);
        contentPane.add(optionPane);
        pack();

    }



    /**
     * Gets the option selected on the dialog.
     * 
     * @return the option selected on the dialog
     */
    public int getOption() {

        Object value = optionPane.getValue();

        if (value instanceof String) {

            String strValue = (String)value;

            if (strValue.equals(buttons[0])) {

                return JOptionPane.YES_OPTION;
            }
            else if (strValue.equals(buttons[1])) {

                return JOptionPane.CANCEL_OPTION;
            };
        };

        return JOptionPane.CLOSED_OPTION;
    }


    /**
     * Gets the name of the new variable.
     * 
     * @return name of new variable
     */
    public String getVariableName() {

        return newVariablePanel.getVariableName();
    }


    /**
     * Enables whether or not the variable name field is enabled.
     *
     * @param value true enables the field, false disables it
     */
    public void setVariableNameEnabled(boolean value) {

        newVariablePanel.setVariableNameEnabled(value);
    }
    
    


    /**
     * Gets the data type of the new variable.
     * 
     * @return data type of new variable
     */
    public long getDataType() {

        return newVariablePanel.getDataType();
    }


    /**
     * Enables whether or not the variable data type field is enabled.
     *
     * @param enabled true enables the field, false disables it
     */
    public void setDataTypeEnabled(boolean enabled) {

        newVariablePanel.setDataTypeEnabled(enabled);
    }

     /**
     * Set the datatype combobox to allow for epoch or epoch16 to 
     * create epoch variables.
     *
     * @param cdfVersion defines the cdf version being used (2 or higher)
     */
    public void createEpochVarAdjust(String cdfVersion) {

        newVariablePanel.createEpochVarAdjust(cdfVersion);
    }


    /**
     * Gets the number of elements for the new variable.
     * 
     * @return number of elements for the new variable
     */
    public int getNumberOfElements() {

        return newVariablePanel.getNumberOfElements();
    }


    /**
     * Enables whether or not the variable "number of elements" field is
     * enabled.
     *
     * @param enabled true enables the field, false disables it
     */
    public void setNumberOfElementsEnabled(boolean enabled) {

        newVariablePanel.setNumberOfElementsEnabled(enabled);
    }


    /**
     * Gets the record variance characteristic of the new variable.
     * 
     * @return record variance of new variable
     * @see #setRecordVarianceEnabled(boolean)
     */
    public long getRecordVariance() {

        return newVariablePanel.getRecordVariance();
    }


    /**
     * Enables whether or not the variable's record variance field is
     * enabled.
     *
     * @param enabled true enables the field, false disables it
     * @see #getRecordVariance()
     */
    public void setRecordVarianceEnabled(boolean enabled) {

        newVariablePanel.setRecordVarianceEnabled(enabled);
    }


    /**
     * Gets the compression setting.
     *
     * @return true if compression is enabled.  Otherwise, false.
     * @see #setCompression(boolean)
     */
    public boolean getCompression() {

        return newVariablePanel.getCompression();
    }


    /**
     * Enables or disables compression.
     *
     * @param value whether or not to enable compression.
     * @see #getCompression()
     */
    public void setCompression(boolean value) {

        newVariablePanel.setCompression(value);
    }


    /**
     * Gets the sparse record type.
     *
     * @return sparse record type.
     * @see #setSparseRecordType(SparseRecordType)
     */
    public SparseRecordType getSparseRecordType() {

        return newVariablePanel.getSparseRecordType();
    }


    /**
     * Sets the sparse record type.
     *
     * @param value new sparse record type.
     * @see #getSparseRecordType()
     */
    public void setSparseRecordType(SparseRecordType value) {

        newVariablePanel.setSparseRecordType(value);
    }


    /**
     * Gets the pad value.
     *
     * @return pad value.
     * @see #setPadValue(Object)
     */
    public Object getPadValue() {

        try {

            return newVariablePanel.getPadValue();
        }
        catch (NumberFormatException e) {

            // this should not happen since validation was done in
            // valuesAreValid()

            return null;
        }
    }


    /**
     * Sets the pad value.
     *
     * @param value new pad value.
     * @see #getPadValue()
     */
    public void setPadValue(Object value) {

        newVariablePanel.setPadValue(value);
    }


    /**
     * Indicates whether the pad value is valid.
     * 
     * @return indication of validity of pad value.
     */
    public String padValueIsValid() {

        try {

            newVariablePanel.getPadValue();
        }
        catch (NumberFormatException e) {

            return "Invalid Pad Value: " + e.getMessage();
        }

        return null;
    }


    /**
     * Gets the number of dimensions of the new variable.
     * 
     * @return number of dimensions of new variable
     */
    public int getDimension() {

        return newVariablePanel.getDimension();
    }


    /**
     * Enables whether or not the variable's dimensions field is
     * enabled.
     *
     * @param enabled true enables the field, false disables it
     */
    public void setDimensionInfoEnabled(boolean enabled) {

        newVariablePanel.setDimensionInfoEnabled(enabled);
    }


    /**
     * Gets the size of each dimension of the new variable.
     * 
     * @return size of all dimensions of the new variable
     */
    public long[] getDimensionSizes() {

        return newVariablePanel.getDimensionSizes();
    }


    /**
     * Gets the variance of each dimension of the new variable.
     * 
     * @return variance of all dimensions of new variable
     */
    public long[] getDimensionVariances() {

        return newVariablePanel.getDimensionVariances();
    }


    /**
     * Indicates whether the variable name is a valid CDF variable name.
     * 
     * @return indication of validity of variable name
     */
    public String nameIsValid() {

        try {

            gsfc.spdf.istp.Variable.checkName(cdf, getVariableName());
        }
        catch (ISTPComplianceException e) {

            return e.getMessage();
        }

        return null;
    }


    /**
     * Indication of whether the number of elements is value with 
     * respect to the variable's data type.
     * 
     * @return indication of validity of number of elements value
     */
    public String numberOfElementsIsValid() {

        if (getNumberOfElements() < 1) {

            return "Number of elements must be greater than zero.";
        }

        return null;
    }


    /**
     * Indicates whether the all the dimension size values are valid.
     * 
     * @return indication of whether all dimension size values are valid
     */
    public String dimensionSizesAreValid() {

        long[] dimSizes = getDimensionSizes();

        if (getDimension() > 0 && dimSizes != null) {

            for (int i = 0; i < dimSizes.length; i++) {

                if (dimSizes[i] < 1) {

                    return "Dimension " + i + 
                           " size must be greater than zero.";
                }
            }
        }

        return null;
    }


    /**
     * Responds to a property change event.
     * 
     * @param event event describing the property change
     */
    public void propertyChange(PropertyChangeEvent event) {

        String propertyName = event.getPropertyName();

        if (propertyName.equals(JOptionPane.VALUE_PROPERTY)) {

            String value = (String)event.getNewValue();

            if (value.equals(buttons[0])) {   // i.e., Create button

                createButtonSelected();

            }
            else if (value.equals(buttons[1])) {// i.e., Cancel button

                cancelButtonSelected();
            };
        };
    }


    /**
     * Responds to a create button selection event.
     */
    protected void createButtonSelected() {

        String error = valuesAreValid();

        if (error != null) {

            JOptionPane.showMessageDialog(this, error, "Invalid Value", 
                                          JOptionPane.ERROR_MESSAGE);

            optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
            return;
        };

        dispose();
    }


    /**
     * Responds to a cancel button selection event.
     */
    protected void cancelButtonSelected() {

        dispose();
    }


    /**
     * Indicates whether the all the variable characteristic values 
     * (name, number of elements, etc.) on this panel are valid.
     * 
     * @return null if all values are valid or a string containing a 
     *             message describing an invalid condition
     */
    public String valuesAreValid() {

        String error = nameIsValid();

        if (error != null) {

            return error;
        }
    
        error = numberOfElementsIsValid();

        if (error != null) {

            return error;
        }

        error = dimensionSizesAreValid();

        if (error != null) {

            return error;
        }

        error = padValueIsValid();

        if (error != null) {

            return error;
        }

        return error;
    }

}

