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
 * Copyright (c) 2011-2024 United States Government as represented by 
 * the National Aeronautics and Space Administration. No copyright is 
 * claimed in the United States under Title 17, U.S.Code. All Other 
 * Rights Reserved.
 *
 * $Id: JLabeledCB.java,v 1.20 2024/10/28 13:01:06 btharris Exp $
 */

//$Id: JLabeledCB.java,v 1.20 2024/10/28 13:01:06 btharris Exp $
package gsfc.spdf.gui;

import java.awt.*;
import java.util.Vector;
import javax.swing.*;

/**
 * A JComboBox with a label.
 *
 * Most methods are wrappers to the same method found on java.swing.JComboBox
 *
 * @author Phil Williams
 * @version $Revision: 1.20 $
 *
 * @see javax.swing.JComboBox
 *
 */
public class JLabeledCB 
    extends AbstractLabeledComponent
{

    /**
     * The JComboBox
     */
    public	JComboBox	comboBox;

    /**
     * If an instance is created using the constructor that accepts 
     * a <code>Vector</code> this is set to the vector.  It is used to 
     * reset the comboBox back to it's original contents.
     */
    public      Vector          defaultItems;

    // Implementation of interface routines

    public Object get() {
	return comboBox.getSelectedItem();
    }

    public void set(Object obj) {
       comboBox.setSelectedItem(obj);
 
	if (comboBox.isEditable()) {
	    JTextField tf = (JTextField)comboBox.getEditor().getEditorComponent();
	    tf.setCaretPosition(0);
	}
    }

    public void reset() {
	comboBox.removeAllItems();
	if (defaultItems != null) {
	    for (int i = 0 ; i < defaultItems.size() ; i++)
		comboBox.addItem( (String)defaultItems.elementAt(i) );
	}

	comboBox.setSelectedIndex(-1);
    }

    public void addItem(Object obj) {
	comboBox.addItem(obj);
    }

    public void setEnabled(boolean enabled) {
	if (enabled) {
	    comboBox.setEnabled(true);
	    label.setForeground(enabledColor);
	} else {
	    comboBox.setEnabled(false);
	    label.setForeground(disabledColor);
	}
    }

    /**
     * Creates a Box containing a JComboBox and a JLabel.
     * The JCombobox is initialized with the specified items. 
     * The position of the label defaults to NORTH. 
     * 
     * @param	str	  	Label of the ComboBox	
     * @param	items	        Items contained in the ComboBox.	
     * @param      editable 	If true, make the combobox editable.
     * @param enabled whether ComboBox is enabled.
     */
    public JLabeledCB (String str, Vector items, boolean editable, 
		       boolean enabled )  {
	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	label = new JLabel(str);

	if (items != null) {
	    // Have to clone the Vector since the CB controls it.
	    defaultItems = (Vector)items.clone();
	    comboBox = new JComboBox( items);
	} else
	    comboBox = new JComboBox();

        comboBox.setAlignmentX(LEFT_ALIGNMENT);
        comboBox.setMinimumSize(new Dimension(100,20));
        comboBox.setEditable(editable);
	if (editable)

	
	enabledColor  = Color.black;
	disabledColor = new Color(142, 142, 142);
	setEnabled(enabled);

        add(label);
        add(Box.createVerticalStrut(3));
        add(comboBox);
    }

    public JLabeledCB (String str, Vector items, boolean editable) {
	this (str, items, editable, true);
    }

    /**
     * Creates a JComboBox with the given label.
     * The position of the label defaults to NORTH. 
     * 
     * @param	str	  	Label of the ComboBox	
     * @param      editable 	If true, make the combobox editable.
     */
    public JLabeledCB (String str, boolean editable)  {
	this(str, null, editable);
    }

    public void setIndex(int index) {
	comboBox.setSelectedIndex(index);
    }

    public void addItemListener(java.awt.event.ItemListener aListener) {
	comboBox.addItemListener(aListener);
    }

    public void removeItemListener(java.awt.event.ItemListener aListener) {
	comboBox.removeItemListener(aListener);
    }

    /**
     * Returns string representations of all the items contained
     * in this combo box.
     *
     * @return A string array containing the string reps of the items.
     */
    public String [] getStringItems() {
	String [] items = null;
	int count = comboBox.getItemCount();
	items = new String [count];
	for (int i=0;i<count;i++)
	    items[i] = comboBox.getItemAt(i).toString();

	return items;
    }

    /**
     * Checks to see if the string is contained in the list.
     * This is only useful for comboBoxes that contain only strings.
     *
     * @param item String value to search for in list.
     * @return True if the item is in the list of items.
     */
    public boolean contains(String item) {
	int count = comboBox.getItemCount();

	for (int i=0;i<count;i++)
	    if (comboBox.getItemAt(i).toString().equals(item))
		return true;
	
	return false;
    }

    public JComboBox getComboBox() {
	return comboBox;
    }

}  // End JLabeledCB
