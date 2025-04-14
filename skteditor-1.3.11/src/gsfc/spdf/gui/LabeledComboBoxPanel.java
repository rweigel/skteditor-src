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
 * $Id: LabeledComboBoxPanel.java,v 1.9 2022/03/24 10:38:36 btharris Exp $
 */

package gsfc.spdf.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import gsfc.spdf.gui.AbstractLabeledInputComponent;

/**
 * One or more JComboBoxes with a single JLabel on a JPanel.
 *
 * @author B. Harris
 * @version $Revision: 1.9 $
 */
public class LabeledComboBoxPanel 
    extends AbstractLabeledInputComponent 
    implements ActionListener
{

    /**
     * Indicates whether the combo boxes are editable
     */
    private boolean editable = false;


    /**
     * The number of JComboBoxes on this component
     */
    private int numFields;


    /**
     * The JComboBoxes on this component
     */
    private JComboBox[] comboBoxes;


    /**
     * The initial value of selection items for the combo boxes
     */
    private Vector initialSelectionItems;


    /**
     * Construct a LabeledComboBoxPanel with the given label and a single
     * combo box.  The combo box has the given selectionItems and editability.
     *
     * A BoxLayout (Y_AXIS) is set as the default layout manager.  The border
     * is an EtchedBorder.
     *
     * @param label  The label field for this component
     * @param selectionItems  The initial value of the selection items for
     *                        the combo box
     * @param editable  Indicates whether the combo box is to be editable
     */
    public LabeledComboBoxPanel(String label, 
                                Vector selectionItems, boolean editable) {

        this(label, 1, selectionItems, editable);
    }


    /**
     * Construct a LabeledComboBoxPanel with the given label and the
     * specified number of combo boxes.  The combo boxes have the given 
     * selectionItems and editability.
     *
     * A BoxLayout (Y_AXIS) is set as the default layout manager.  The border
     * is an EtchedBorder.
     *
     * @param label  The label field for this component
     * @param numFields  The number of combo boxes on this panel
     * @param selectionItems  The initial value of the selection items for
     *                        the combo boxes
     * @param editable  Indicates whether the combo boxes are to be editable
     */
    public LabeledComboBoxPanel(String label, int numFields,
                                Vector selectionItems, boolean editable) {

        this(label, new EtchedBorder(), numFields, selectionItems, editable);
    }


    /**
     * Construct a LabeledComboBoxPanel with the given label, border, and 
     * a single combo box.  The combo box has the given selectionItems and 
     * editability.
     *
     * A BoxLayout (Y_AXIS) is set as the default layout manager.  
     *
     * @param label  The label field for this component
     * @param border  The border to use of the titled border
     * @param selectionItems  The initial value of the selection items for
     *                        the combo boxes
     * @param editable  Indicates whether the combo boxes are to be editable
     */
    public LabeledComboBoxPanel(String label, Border border, 
                                Vector selectionItems, boolean editable) {
        this(label, border, 1, selectionItems, editable);
    }


    /**
     * Construct a LabeledComboBoxPanel with the given label, border, and the
     * specified number of combo boxes.  The combo boxes have the given 
     * selectionItems and editability.
     *
     * A BoxLayout (Y_AXIS) is set as the default layout manager.  
     *
     * @param label  The label field for this component
     * @param border  The border to use of the titled border
     * @param numFields  The number of combo boxes on this panel
     * @param selectionItems  The initial value of the selection items for
     *                        the combo boxes
     * @param editable  Indicates whether the combo boxes are to be editable
     */
    public LabeledComboBoxPanel(String label, Border border, int numFields,
                                Vector selectionItems, boolean editable) {

        super(label, border);

        this.numFields = numFields;
        this.editable = editable;

        if (numFields > 0) {

            createComboBoxes(numFields, selectionItems);
        };
    }


    /**
     * Creates the combo boxes for this panel.
     * 
     * @param numFields number of combo boxes to create
     * @param selectionItems initial selection values for the combo boxes
     */
    protected void createComboBoxes(int numFields, Vector selectionItems) {

        this.numFields = numFields;
        initialSelectionItems = (Vector)selectionItems.clone();

        comboBoxes = new JComboBox[numFields];

        for (int i = 0; i < numFields; i++) {

            comboBoxes[i] = new JComboBox(selectionItems);
            comboBoxes[i].setEditable(editable);

            if (comboBoxes[i].isEditable()) {

                comboBoxes[i].addActionListener(this);
            }
            add(comboBoxes[i]);
        };
    }


    /**
     * Deletes all combo boxes from this panel.
     */
    protected void deleteComboBoxes() {

        for (int i = numFields - 1; i >= 0; i--) {

            remove(comboBoxes[i]);
        };

        comboBoxes = null;
        numFields = 0;
    }


    /**
     * Redefines (number and initial selection values) the combo boxes on this 
     * panel.
     * 
     * @param numFields number of combo boxes to have on this panel
     * @param selectionItems initial selection values for the combo boxes
     */
    public void redefine(int numFields, Vector selectionItems) {

        deleteComboBoxes();

        createComboBoxes(numFields, selectionItems);

        revalidate();
    }


    /**
     * @deprecated Use getInputComponentCount()
     */
    public Object get() {

        if (numFields == 1) {  // for backward compatibility

            return getInputComponentValue(0);
        }
            Vector fieldValues = new Vector(numFields);

            for (int i = 0; i < numFields; i++) {

                fieldValues.addElement(getInputComponentValue(i));
            };

            return fieldValues;
    }


    /**
     * @deprecated Use getInputComponentCount()
     */
    public void set(Object value) {

        if (value instanceof Vector) {

            Vector values = (Vector) value;

            for (int i = 0; i < values.size(); i++) {

                setInputComponentValue(i, values.elementAt(i));
            };
        }
        else {

            setInputComponentValue(0, value);
        };
    }


    public void reset() {

        for (int i = 0; i < numFields; i++) {

            comboBoxes[i].removeAllItems();

            for (int j = 0; j < initialSelectionItems.size(); j++) {

                comboBoxes[i].addItem(initialSelectionItems.elementAt(j));
            };

            comboBoxes[i].setSelectedIndex(-1);
        };
    }


    public void addItem(Object value) {

        if (value instanceof Vector) {

            Vector valueVector = (Vector)value;

            for (int i = 0; i < numFields && i < valueVector.size(); i++) {

                Vector values = (Vector)valueVector.elementAt(i);

                for (int j = 0; j < values.size(); j++) {

                    comboBoxes[i].addItem(values.elementAt(j));
                };
            };
        }
        else {

            comboBoxes[0].addItem(value);
        };
    }


    /**
     * Adds an ItemListener.  A listener will receive an event when the
     * selected item of any of the combo boxes changes.
     *
     * @param l the ItemListener that is to be notified
     */
    public void addItemListener(ItemListener l) {

        for (int i = 0; i < numFields; i++) {

            comboBoxes[i].addItemListener(l);
        };
    }


    /**
     * Removes an ItemListener.  
     *
     * @param l the ItemListener to remove
     */
    public void removeItemListener(ItemListener l) {

        for (int i = 0; i < numFields; i++) {

            comboBoxes[i].removeItemListener(l);
        };
    }


    /**
     * Adds the specified action listener to receive action events from
     * all combo boxes.
     * 
     * @param l action listener
     */
    public void addActionListener(ActionListener l) {

        for (int i = 0; i < numFields; i++) {

            comboBoxes[i].addActionListener(l);
        };
    }

    /**
     * Removes the specified action listener so that it no longer receives
     * action events from any of the combo boxes.
     * 
     * @param l action listener
     */
    public void removeActionListener(ActionListener l) {

        for (int i = 0; i < numFields; i++) {

            comboBoxes[i].removeActionListener(l);
        };
    }
 
    public int getInputComponentCount() {

        return numFields;
    }


    public Object getInputComponentValue(int index) {

        return comboBoxes[index].getSelectedItem();
    }


    public void setInputComponentValue(int index, Object value) {

        comboBoxes[index].setSelectedItem(value);
            
        if (comboBoxes[index].isEditable()) {

            ((JTextField)(comboBoxes[index].getEditor().getEditorComponent())).
                            setCaretPosition(0);
        };
    }


    public void setInputComponentToolTipText(int index, String text) {

        comboBoxes[index].setToolTipText(text);
    }


    /**
     * Responds to an ActionEvent from an editable JComboBox.
     *
     * @param event ActionEvent.
     */
    public void actionPerformed(ActionEvent event) {

        JComboBox comboBox = (JComboBox)event.getSource();
                                       // combobox which generated the event
        if (event.getActionCommand().equals("comboBoxEdited")) {

            String newItem = (String)comboBox.getEditor().getItem();
            if (!containsItem(comboBox, newItem)) {

                comboBox.addItem(newItem);
                comboBox.setSelectedItem(newItem);
            }
        }
    }


    /**
     * Determines if the given JComboBox contains the given item.
     *
     * @param comboBox comboBox to check for the given item.
     * @param item item to look for.
     * @return true if comboBox has the given item.  Otherwise false.
     */
    private boolean containsItem(JComboBox comboBox, Object item) {

        return items(comboBox).contains(item);
    }


    /**
     * Provides a set of the item on the given JComboBox.
     *
     * @param comboBox comboBox to get items from.
     * @return a Set of the items from comboBox.
     */
    private Set<Object> items (JComboBox comboBox) {

        HashSet<Object> items = new HashSet<Object>(comboBox.getItemCount());
                                       // Set of all items from the comboBox
        for (int i = 0; i < comboBox.getItemCount(); i++) {

            items.add(comboBox.getItemAt(i));
        }

        return items;
    }
}
