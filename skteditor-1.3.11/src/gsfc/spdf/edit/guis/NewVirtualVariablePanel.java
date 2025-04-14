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
 * $Id: NewVirtualVariablePanel.java,v 1.6 2022/03/24 10:38:32 btharris Exp $
 */
package gsfc.spdf.edit.guis;


import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Dimension;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.*;


import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;


import gsfc.nssdc.cdf.CDF;
import gsfc.nssdc.cdf.Variable;
  
import gsfc.spdf.gui.LabeledTextFieldPanel;
import gsfc.spdf.gui.LabeledComboBoxPanel;
import gsfc.spdf.gui.LabeledCdfDataTypeCBPanel;
import gsfc.spdf.gui.WholeNumberField;


/**
 * Panel for displaying and editing the characteristics of a new ISTP
 * virtual CDF variable.
 *
 * @author B. Harris
 * @version $Revision: 1.6 $
 */
public class NewVirtualVariablePanel extends NewVariablePanel {

    /**
     * Virtual variable's function and component information panel.
     */
    protected FunctionComponentPanel functionAndComponents = null;


    /**
     * Creates a NewVirtualVariablePanel.
     *
     * @param cdf CDF that new variable is to be contained in
     */
    public NewVirtualVariablePanel(CDF cdf) {

        super(cdf);

        GridBagLayout gbl = (GridBagLayout)getLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        functionAndComponents = new FunctionComponentPanel(
                                                 "Virtual Variable", 
                                                 new EtchedBorder(), cdf);

        functionAndComponents.setMinimumSize(new Dimension(50, 175));
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbl.setConstraints(functionAndComponents, gbc);
        add(functionAndComponents);
        
        setFunctionValue("alternate_view");
    }



    /**
     * Gets the CDF variable's FUNCTION attribute value from the panel.
     *
     * @return the FUNCTION attribute value
     */
    public String getFunctionValue() {

        return functionAndComponents.getFunctionValue();
    }


    /**
     * Sets the CDF variable's FUNCTION attribute value on the panel.
     *
     * @param value FUNCTION attribute value
     */
    public void setFunctionValue(String value) {

        functionAndComponents.setFunctionValue(value);
    }


   /**
     * Gets a count of the number of COMPONENT_x attribute values.
     *
     * @return the number of COMPONENT_x values
     */
    public int getComponentValueCount() {

        return functionAndComponents.getComponentValueCount();
    }


    /**
     * Gets the specified COMPONENT_x attribute value.
     *
     * @param index identifes the COMPONENT_x whose value is to be returned
     * @return the value of the specified COMPONENT_x value
     */
    public String getComponentValue(int index) {

        return functionAndComponents.getComponentValue(index);
    }


    /**
     * Sets the specified COMPONENT_x's value.
     *
     * @param index identifies the COMPONENT_x whose value is to be set
     * @param value value to set COMPONENT_x's value to
     */
    public void setComponentValue(int index, String value) {

        functionAndComponents.setComponentValue(index, value);
    }


    /**
     * Gets all the COMPONENT_x attribute values.
     *
     * @return the COMPONENT_x values
     */
    public String[] getComponentValues() {

        return functionAndComponents.getComponentValues();
    }


    /**
     * Sets the base (i.e., the variable upon which a virtual variable is
     * based) variable.
     *
     * @param var base variable
     */
    public void setBaseVariable(Variable var) {

        setFunctionValue("alternate_view");

        if (var != null) {

            setDataType(var.getDataType());
            setNumberOfElements((int)var.getNumElements());
            setRecordVariance(var.getRecVariance());
            setDimension((int)var.getNumDims());

            if (var.getNumDims() > 0) {

                setDimensionSizes(var.getDimSizes());
            };

            setComponentValue(0, var.getName());
        };
    }
}

