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
 * Copyright (c) 2012-2025 United States Government as represented by 
 * the National Aeronautics and Space Administration. No copyright is 
 * claimed in the United States under Title 17, U.S.Code. All Other 
 * Rights Reserved.
 *
 * $Id: LabeledSparseRecordTypeCBPanel.java,v 1.3 2025/01/16 13:17:40 btharris Exp $
 */
package gsfc.spdf.gui;


import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.*;

import gsfc.spdf.cdf.SparseRecordType;
import gsfc.spdf.gui.AbstractLabeledInputComponent;


/**
 * One or more SparseRecordTypeComboBoxes with a single JLabel on a JPanel.
 *
 * @author B. Harris
 * @version $Revision: 1.3 $
 */
public class LabeledSparseRecordTypeCBPanel 
    extends AbstractLabeledInputComponent {

    /**
     * Indicates whether the combo boxes are editable
     */
    private boolean editable = false;


    /**
     * The number of JComboBoxes on this component
     */
    private int numFields;


    /**
     * The SparseRecordTypeComboBoxes on this component
     */
    private SparseRecordTypeComboBox[] comboBoxes;
   
   
 
    /**
     * Construct a LabeledSparseRecordTypeCBPanel with the given label and a 
     * single combo box.  
     *
     * A BoxLayout (Y_AXIS) is set as the default layout manager.  The border
     * is an EtchedBorder.
     *
     * @param label  The label field for this component
     */
    public LabeledSparseRecordTypeCBPanel(String label) {

        this(label, 1);
    }


    /**
     * Construct a LabeledSparseRecordTypeCBPanel with the given label and the
     * specified number of combo boxes.  
     *
     * A BoxLayout (Y_AXIS) is set as the default layout manager.  The border
     * is an EtchedBorder.
     *
     * @param label  The label field for this component
     * @param numFields  The number of combo boxes on this panel
     */
    public LabeledSparseRecordTypeCBPanel(String label, int numFields) {
        
        this(label, new EtchedBorder(), numFields);
    }


    /**
     * Construct a LabeledSparseRecordTypeCBPanel with the given label, 
     * border, and a single combo box.  
     *
     * A BoxLayout (Y_AXIS) is set as the default layout manager.  
     *
     * @param label  The label field for this component
     * @param border  The border to use of the titled border
     */
    public LabeledSparseRecordTypeCBPanel(String label, Border border) {

        this(label, border, 1);
    }


    /**
     * Construct a LabeledSparseRecordTypeCBPanel with the given label, border, 
     * and the specified number of combo boxes.  
     *
     * A BoxLayout (Y_AXIS) is set as the default layout manager.  
     *
     * @param label  The label field for this component
     * @param border  The border to use of the titled border
     * @param numFields  The number of combo boxes on this panel
     */
    public LabeledSparseRecordTypeCBPanel(String label,  Border border, 
                                     int numFields) {

        super(label, border);

        this.numFields = numFields;

        comboBoxes = new SparseRecordTypeComboBox[numFields];

        for (int i = 0; i < numFields; i++) {

            comboBoxes[i] = new SparseRecordTypeComboBox();
            add(comboBoxes[i]);
        };

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

            comboBoxes[i].setSelectedIndex(-1);
        };
    }
    
 
    public void addItem(Object value) {

        if (value instanceof Vector) {

            Vector valueVector = (Vector)value;

            for (int i = 0; i < numFields && i < valueVector.size(); i++) {

                Vector values = (Vector)valueVector.elementAt(i);

                for (int j = 0; j < values.size(); j++) {

                    comboBoxes[i].addItem((SparseRecordType)values.elementAt(j));
                };
            };
        }
        else {

            comboBoxes[0].addItem((SparseRecordType)value);
        }
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
        }
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
     * @param l The action listener
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
     * @param l The action listener
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


    public SparseRecordType getSparseRecordType(int index) {

        return comboBoxes[index].getSparseRecordType();
    }


    public void setSparseRecordType(int index, SparseRecordType value) {

        comboBoxes[index].setSparseRecordType(value);
    }

}
