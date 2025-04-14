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
 * $Id: NewVirtualVariableDialog.java,v 1.10 2022/03/24 10:38:32 btharris Exp $
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
import gsfc.spdf.istp.VirtualVariable;


/**
 * Dialog for specifying the characteristics of a new ISTP virtual CDF 
 * variable.
 * 
 * @author B. Harris
 * @version $Revision: 1.10 $
 */
public class NewVirtualVariableDialog extends NewVariableDialog {
    
     /**
     * Initial witdth of the dialog window
     */
    public final static int WIDTH = 350;
    
     /**
     * Initial height of the dialog window
     */
    public final static int HEIGHT = 550;


    /**
     * Creates a NewVirtualVariableDialog.
     * 
     * @param owner frame from which the dialog is displayed
     * @param title string to display in the dialog's title bar
     * @param modal true for modal dialog, false for one that allows other 
     *              windows to be active at the same time
     * @param cdf CDF in which to create the new variable
     */
    public NewVirtualVariableDialog(Frame owner, String title,
                                    boolean modal, CDF cdf) {

        super(owner, title, modal, new NewVirtualVariablePanel(cdf), cdf);

        setSize(WIDTH, HEIGHT);
        Dimension  screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width/2 - WIDTH/2,screenSize.height/2 - HEIGHT/2);

//        setLocationRelativeTo(null);

    }


    /**
     * Sets the variable upon which the new variable is based (inherits certain
     * attribute values and dimensionality characteristics)
     * 
     * @param baseVar base variable
     */
    public void setBaseVariable(Variable baseVar) {

        ((NewVirtualVariablePanel)newVariablePanel).setBaseVariable(baseVar);
    }


public boolean getCompression() {

    return newVariablePanel.getCompression();
}

public void setCompression(boolean value) {

    newVariablePanel.setCompression(value);
}

public SparseRecordType getSparseRecordType() {

    return newVariablePanel.getSparseRecordType();
}

public void setSparseRecordType(SparseRecordType value) {

    newVariablePanel.setSparseRecordType(value);
}


    /**
     * Gets the FUNCTION attribute value for the new variable.
     * 
     * @return FUNCTION attribute value for new variable
     */
    public String getFunctionValue() {

        return ((NewVirtualVariablePanel)newVariablePanel).getFunctionValue();
    }


    /**
     * Gets the number of COMPONENT_x values.
     * 
     * @return number of COMPONENT_x values.
     */
    public int getComponentValueCount() {

        return ((NewVirtualVariablePanel)newVariablePanel).
                                            getComponentValueCount();
    }


    /**
     * Gets the specified COMPONENT_x attribute value for the new variable.
     * 
     * @param index specifies which COMPONENT_x value to get where x = index
     * @return COMPONENT_x value of new variable where x = index
     */
    public String getComponentValue(int index) {

        return ((NewVirtualVariablePanel)newVariablePanel).
                                            getComponentValue(index);
    }


    /**
     * Gets all the COMPONENT_x attribute values for the new variable.
     * 
     * @return COMPONENT_x values of new variable
     */
    public String[] getComponentValues() {

        return ((NewVirtualVariablePanel)newVariablePanel).
                                            getComponentValues();
    }


    /**
     * Indicates whether the function value is valid.
     * 
     * @return indication of validity of function value
     */
    public String functionIsValid() {

        String function = getFunctionValue();

        if (function == null) {

            return "Missing function value";
        };

        function = function.trim();

        if (function.length() == 0) {

            return "Missing function value";
        };

        return null;
    }


    /**
     * Indicates whether the component values are valid.
     * 
     * @return indication of validity of all component values
     */
    public String componentsAreValid() {

        int requiredComponents = VirtualVariable.getNumberOfComponents(
                                                       getFunctionValue());

        if (requiredComponents < 1) {

            //
            // User must have entered a function we don't know about so we
            // can't provoide anymore validation
            //
            return null;
        };

        int numComponents = getComponentValueCount();

        for (int i = 0; i < numComponents; i++) {

            String componentValue = getComponentValue(i);

            if (componentValue == null) {

                return "Missing COMPONENT_" + i + " value";
            };

            componentValue = componentValue.trim();

            if (componentValue.length() == 0) {

                return "Missing COMPONENT_" + i + " value";
            };
        };

        return null;
    }


    /**
     * Indicates whether the all the variable characteristic values (name,
     * number of elements, etc.) on this panel are valid.
     *
     * @return null if all values are valid or a string containing a message
     *         describing an invalid condition
     */
    public String valuesAreValid() {

        String error = super.valuesAreValid();

        if (error != null) {

            return error;
        };

        error = functionIsValid();

        if (error != null) {

            return error;
        };

        error = componentsAreValid();

        if (error != null) {

            return error;
        };

        return error;
    }
}

