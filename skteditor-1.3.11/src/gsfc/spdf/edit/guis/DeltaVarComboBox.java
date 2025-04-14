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
 * $Id: DeltaVarComboBox.java,v 1.7 2022/03/24 10:38:32 btharris Exp $
 */
package gsfc.spdf.edit.guis;

import java.awt.Dimension;
import java.util.Vector;

import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.Box;


import gsfc.spdf.edit.events.VariableEventListener;
import gsfc.spdf.gui.AbstractLabeledInputComponent;
import gsfc.spdf.istp.Variable;


public class DeltaVarComboBox 
    extends AbstractLabeledInputComponent {

    protected AttributeComboBox comboBox = null;

    private String deltaVar = null;


    public DeltaVarComboBox(String deltaVar) {

        super(deltaVar, new EmptyBorder(0, 0, 0, 0));

        this.deltaVar = "DELTA_" + deltaVar.toUpperCase() + "_VAR";

	comboBox = new AttributeComboBox(this.deltaVar);
        comboBox.setAlignmentX(LEFT_ALIGNMENT);
        comboBox.setMinimumSize(new Dimension(100,15));

        add(comboBox);
    }


    public void reset() {

	comboBox.removeAllItems();
	setEnabled(false);
    }


    public void setVariableList(Vector list) {

	comboBox.setVariableList(list);
    }


    public void setVariable(Variable var) {

	comboBox.setVariable(var.getCdfVariable());
    }


    public Variable getVariable() {

        gsfc.nssdc.cdf.Variable cdfVar = 
            (gsfc.nssdc.cdf.Variable)comboBox.getSelectedItem();

        return cdfVar == null ? null : new Variable(cdfVar);
    }


    public void addVariableEventListener(VariableEventListener l) {

        comboBox.addVariableEventListener(l);
    }


    public void removeVariableEventListener(VariableEventListener l) {

        comboBox.removeVariableEventListener(l);
    }


    public int getInputComponentCount() {

        return 1;
    }


    public Object getInputComponentValue(int index) {

        return get();
    }


    public void setInputComponentValue(int index, Object value) {

        set(value);
    }


    public void setInputComponentToolTipText(int index, String text) {

        comboBox.setToolTipText(text);
    }


    public Object get() {

	return comboBox.getSelectedItem();
    }


    public void set(Object obj) {

        if (obj != null) {

	    comboBox.setSelectedItem(obj);
        }
        else {

	    comboBox.setSelectedIndex(-1);
        }
	if (comboBox.isEditable()) {
	    ((JTextField)comboBox.getEditor().getEditorComponent()).
		setCaretPosition(0);
	}
    }
	

    public void addItem(Object obj) {

	comboBox.addItem(obj);
    }


    public void setEnabled(boolean enabled) {

        comboBox.setEnabled(enabled);
    }


    public boolean containsVar(gsfc.nssdc.cdf.Variable item) {

        int count = comboBox.getItemCount();

        for (int i=0;i<count;i++) {

            if (comboBox.getItemAt(i) == item) {

                return true;
            }
        }

        return false;
    }

}
