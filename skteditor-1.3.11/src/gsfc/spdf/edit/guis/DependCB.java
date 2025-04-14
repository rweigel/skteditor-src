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
 * $Id: DependCB.java,v 1.14 2022/03/24 10:38:32 btharris Exp $
 */
package gsfc.spdf.edit.guis;

import gsfc.spdf.util.TextUtils;
import gsfc.spdf.gui.*;
import gsfc.nssdc.cdf.*;
import java.util.Vector;
import java.awt.*;
import javax.swing.*;

public class DependCB 
    extends AbstractLabeledComponent
    implements CDFConstants, GenericComponentInterface
{

    protected AttributeComboBox comboBox;
    private String depend;

    public DependCB(String str, boolean enabled) {
	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	label = new JLabel(str);
	depend = TextUtils.replaceSpaces(str, '_').toUpperCase();

	comboBox = new AttributeComboBox(depend);
        comboBox.setAlignmentX(LEFT_ALIGNMENT);
        comboBox.setMinimumSize(new Dimension(100,15));

	enabledColor  = Color.black;
	disabledColor = new Color(142, 142, 142);
	setEnabled(enabled);
	
       // label.setFont(new Font("Dialog",1,11));

        add(label);
        add(Box.createVerticalStrut(3));
        add(comboBox);
    }

    public void reset() {
	System.err.println("DependCB.reset: "+depend);
	comboBox.removeAllItems();
	setEnabled(false);
    }

    public void setVariableList(Vector list) {
	System.err.println("DependCB.setVariableList: "+depend);
	comboBox.setVariableList(list);
    }

    public void setVariable(Variable var) {
	System.err.println("DependCB.setVariable: "+depend+" to "+var);
	comboBox.setVariable(var);
    }

    // Implementation of interface routines
    public Object get() {
	return comboBox.getSelectedItem();
    }

    public void set(Object obj) {
	String objString = (obj == null ? "null" : obj.toString());
	System.err.println("DependCB.set: "+depend+" to "+objString);
	comboBox.setSelectedItem(obj);
	if (comboBox.isEditable()) {
	    ((JTextField)comboBox.getEditor().getEditorComponent()).
		setCaretPosition(0);
	}
    }

    public void addItem(Object obj) {
	comboBox.addItem(obj);
    }

    /**
     *
     */
    public void setEnabled(boolean enabled) {
	if (enabled) {
	    comboBox.setEnabled(true);
	    label.setForeground(enabledColor);
	} else {
	    comboBox.setEnabled(false);
	    label.setForeground(disabledColor);
	}
    }

    public boolean containsVar(Variable item) {
	int count = comboBox.getItemCount();

	for (int i=0;i<count;i++)
	    if (comboBox.getItemAt(i) == item)
		return true;
	
	return false;
    }


}
