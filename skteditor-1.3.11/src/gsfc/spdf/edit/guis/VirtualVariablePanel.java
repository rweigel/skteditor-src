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
 * $Id: VirtualVariablePanel.java,v 1.10 2022/03/24 10:38:32 btharris Exp $
 */
package gsfc.spdf.edit.guis;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.*;

import gsfc.nssdc.cdf.*;

import gsfc.spdf.gui.LabeledComboBoxPanel;
import gsfc.spdf.istp.VirtualVariable;


/**
 * Panel to display and support editing of virtual variable unique attributes.
 * 
 * @author B. Harris
 * @version $Revision: 1.10 $
 */
public class VirtualVariablePanel extends FunctionComponentPanel {

    /**
     * The virtual variable that is currently being displayed.
     */
    protected Variable currentVar = null;


    /**
     * Creates a virtual variable panel.
     */
    public VirtualVariablePanel() {

        super("Virtual Variable", new EtchedBorder(), null);
        //
        // hide this component until set() has been so cdf is set before
        //  the user can change the function
        //
        setVisible(false);       
        setMinimumSize(new Dimension(50, 100));
    }


    /**
     * Sets the variable to be displayed.  If the given variable is not a 
     * virtual variable, this panel becomes invisible.
     * 
     * @param var the virtual variable to be displayed
     */
    public void set(Variable var) {

        currentVar = var;
        cdf = var.getMyCDF();

        if (VirtualVariable.isVirtual(var)) {

            setVisible(true);
        }
        else {

            setVisible(false);
            return;
        };

        String functionAttribute = null;     // FUNCTION attribute value
        try {

            functionAttribute = ((String)var.getEntryData("FUNCT")).
                                trim().toLowerCase();
        }
        catch (CDFException e) {

            return;
        }
        finally {

            setFunctionValue(functionAttribute);
        };

        setComponents(var, functionAttribute);
    }

    /**
     * Displayes the COMPONENT_x values based upon the given variable and 
     * function value.
     * 
     * @param var identifies the virtual variable
     * @param functionAttribute the FUNCTION value of the given variable
     */
    protected void setComponents(Variable var, String functionAttribute) {

        int numComponents = resizeComponents(functionAttribute);

        if (numComponents > 0) {

            String[] values = new String[numComponents];

            for (int i = 0; i < numComponents; i++) {

                try {

                    values[i] = ((String)var.getEntryData("COMPONENT_" + i)).
                                                                        trim();
                }
                catch (CDFException e) {

                    values[i] = "";
                };
            };

            if(numComponents == 1) {

                components.setInputComponentValue(0, values[0]);
            }
            else {

                components.setInputComponentValue(0, values);
            };
        };

        components.setChoices(VirtualVariable.getCandidateComponentNames(
                                                            var.getMyCDF()));
    }

    /**
     * Saves the current virtual variable attribute values from the GUI 
     * components into the given CDF virtual variable.
     * 
     * @param var the variable to save the current FUNCTION and COMPONENT_x 
     *            values in
     */
    public void save(Variable var) {

        if (!VirtualVariable.isVirtual(var)) {

            return;
        };

        try {

            try {
                Attribute fctAttr = var.getMyCDF().getAttribute("FUNCT");
                fctAttr.getEntry(var).delete();
            }
            catch (CDFException e) {

                if (e.getCurrentStatus() == CDF.NO_SUCH_ATTR) {

                    Attribute.create(var.getMyCDF(), "FUNCT",
                                     CDF.VARIABLE_SCOPE);
                }
                else if (e.getCurrentStatus() != CDF.NO_SUCH_ENTRY) {

                    String errMsg = "Error deleting " + var.getName() +
                                    "'s FUNCTION attribute entry";
                    System.err.println(errMsg);
                    e.printStackTrace();

                    JOptionPane.showMessageDialog(this, errMsg,
                                    "Error", JOptionPane.ERROR_MESSAGE);
                };
            };

            String value = getFunctionValue();

            if (value != null && value.length() > 0) {

                var.putEntry("FUNCT", CDF.CDF_CHAR, value);
            };
        }
        catch (CDFException e) {

            String errMsg = "Error saving " + var.getName() + 
                            "'s FUNCTION attribute entry value of\n" +
                            function.getInputComponentValue(0) +
                            ".\nError:" + e.getMessage();

            System.err.println(errMsg);
            e.printStackTrace();

            JOptionPane.showMessageDialog(this, errMsg,
                            "Error", JOptionPane.ERROR_MESSAGE);
        };

        int numComponents = getComponentValueCount();

        for (int i = 0; i < numComponents; i++) {

            String attrName = "COMPONENT_" + i;

            try {

                try {
                    Attribute compAttr = var.getMyCDF().getAttribute(attrName);
                    compAttr.getEntry(var).delete();
                }
                catch (CDFException e) {

                    if (e.getCurrentStatus() == CDF.NO_SUCH_ATTR) {

                        Attribute.create(var.getMyCDF(), attrName,
                                         CDF.VARIABLE_SCOPE);
                    }
                    else if (e.getCurrentStatus() != CDF.NO_SUCH_ENTRY) {

                        String errMsg = "Error deleting " + var.getName() +
                                        "'s " + attrName + " attribute entry";

                        System.err.println(errMsg);
                        e.printStackTrace();

                        JOptionPane.showMessageDialog(this, errMsg,
                                    "Error", JOptionPane.ERROR_MESSAGE);
                    };
                };

                String value = getComponentValue(i);

                if (value != null && value.length() > 0) {

                    var.putEntry(attrName, CDF.CDF_CHAR, getComponentValue(i));
                };
            }
            catch (CDFException e) {

                String errMsg = "Error saving " + var.getName() + 
                                "'s " + attrName + 
                                " attribute entry value of\n" +
                                getComponentValue(i) +
                                ".\nError:" + e.getMessage();

                System.err.println(errMsg);
                e.printStackTrace();

                JOptionPane.showMessageDialog(this, errMsg,
                            "Error", JOptionPane.ERROR_MESSAGE);
            };
        };
    }


    /**
     * Responds to a function change.
     *
     * @param newValue new FUNCTION value
     */
    public void functionChanged(String newValue) {

        setComponents(currentVar, newValue);
    }
}
