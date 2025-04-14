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
 * $Id: FunctionComponentPanel.java,v 1.17 2022/08/02 12:22:56 btharris Exp $
 */
package gsfc.spdf.edit.guis;

import java.awt.Color;
import java.awt.LayoutManager;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.*;

import gsfc.nssdc.cdf.*;

import gsfc.spdf.gui.LabeledComboBoxPanel;
import gsfc.spdf.gui.LabeledDataTablePanel;
import gsfc.spdf.istp.VirtualVariable;


/**
 * Panel to display and support editing of the FUNCTION and COMPONENT_x
 * attributes of a virtual variable.
 * 
 * @author B. Harris
 * @version $Revision: 1.17 $
 */
public class FunctionComponentPanel 
    extends JPanel 
    implements ItemListener {

    /**
     * Panel's title border
     */
    protected TitledBorder titledBorder;

    /**
     * Panel's layout manager.
     */
    protected LayoutManager layoutManager;

    /**
     * Panel's border.
     */
    protected Border border;

    /**
     * Panel's label.
     */
    protected String label = "Virtual Variable";

    /**
     * FUNCTION combobox.
     */
    protected LabeledComboBoxPanel function;

    /**
     * COMPONENT_x tables.
     */
    protected LabeledComponentTablePanel components;

    /**
     * the color to use when this component is enabled
     */
    protected Color enabledColor = Color.black;

    /**
     * the color to use when this component is disabled
     */
    protected Color disabledColor = new Color(142, 142, 142);


    /**
     * The CDF from which candidate COMPONENT_x values are obtained.
     */
    protected CDF cdf;


    /**
     * Creates a panel containing a virtual variable's FUNCTION and
     * COMPONENT_x values.
     *
     * @param title panel's title.
     * @param border panel's border.
     * @param cdf CDF containing the virtual variable.
     */
    public FunctionComponentPanel(String title, Border border, CDF cdf) {

        label = title;
        this.cdf = cdf;

        layoutManager = new BoxLayout(this, BoxLayout.Y_AXIS);

        titledBorder = new TitledBorder(border, label);

        titledBorder.setTitleColor(enabledColor);
        setLayout(layoutManager);
        setBorder(titledBorder);

        function = new LabeledComboBoxPanel("Function", 
                                        new EmptyBorder(0, 0, 0, 0),
                                        VirtualVariable.getValidFunctions(),
                                        true);
        function.addItemListener(this);
        function.setAlignmentX(LEFT_ALIGNMENT);
        add(function);

        components = new LabeledComponentTablePanel();
        components.setAlignmentX(LEFT_ALIGNMENT);
        add(components);
    }


    /**
     * Gets the CDF variable's FUNCTION attribute value from the panel.
     * 
     * @return the FUNCTION attribute value
     */
    public String getFunctionValue() {

        return (String)function.getInputComponentValue(0);
    }


    /**
     * Sets the CDF variable's FUNCTION attribute value on the panel.
     * 
     * @param value FUNCTION attribute value
     */
    public void setFunctionValue(String value) {

        function.setInputComponentValue(0, value);

        resizeComponents(value);
    }


    /**
     * Gets a count of the number of COMPONENT_x attribute values.
     * 
     * @return the number of COMPONENT_x values
     */
    public int getComponentValueCount() {

        Object value = components.getInputComponentValue(0);

        if (value instanceof String[]) {

            String[] values = (String[])value;
            return values.length;
        }
        return 1;
    }


    /**
     * Gets the specified COMPONENT_x attribute value.
     * 
     * @param index identifes the COMPONENT_x whose value is to be returned
     * @return the value of the specified COMPONENT_x value
     */
    public String getComponentValue(int index) {

        Object value = components.getInputComponentValue(0);

        if (value instanceof String[]) {

            String[] values = (String[])value;
            return values[index];
        }
        return (String)value;
    
    }


    /**
     * Sets the specified COMPONENT_x's value.
     * 
     * @param index identifies the COMPONENT_x whose value is to be set
     * @param value value to set COMPONENT_x's value to
     */
    public void setComponentValue(int index, String value) {

        Object oldValue = components.getInputComponentValue(0);

        if (oldValue instanceof String[]) {

            String[] values = (String[])oldValue;
            values[index] = value;
            components.setInputComponentValue(0, values);
        }
        else {

            components.setInputComponentValue(0, value);
            components.setChoices(
                           VirtualVariable.getCandidateComponentNames(cdf));
        };

    }


    /**
     * Gets all the COMPONENT_x attribute values.
     * 
     * @return the COMPONENT_x values
     */
    public String[] getComponentValues() {

        Object values = components.getInputComponentValue(0);
                                       // component values
        if (values instanceof String[]) {

            return (String[])values;
        }
        return new String[] {(String)values};
    }


    /**
     * Resizes the number of COMPONENT_x combo boxes based upon the given 
     * function value.
     * 
     * @param functionAttribute the FUNCTION value
     * @return the number of COMPONENT_x values
     */
    protected int resizeComponents(String functionAttribute) {

        int numComponents = VirtualVariable.getNumberOfComponents(
                                                   functionAttribute);

        try {

            components.createEmptyTable(0, 
                                        numComponents > 0 ? numComponents : 15, 
                                        1, Class.forName("java.lang.String"));
        }
        catch(ClassNotFoundException e) {

            e.printStackTrace();
        };

        components.setChoices(VirtualVariable.getCandidateComponentNames(cdf));

        return numComponents;
    }


    /**
     * Responds to a function change.
     *
     * @param newValue new FUNCTION attribute value
     */
    protected void functionChanged(String newValue) {

        resizeComponents(newValue);
    }


    /**
     * Responds to a function change.
     *
     * @param event event indicating that the function combobox value has 
     *              changed
     */
    public void itemStateChanged(ItemEvent event) {

        String functionValue = (String)event.getItem();

        functionChanged(functionValue);
    }
}
