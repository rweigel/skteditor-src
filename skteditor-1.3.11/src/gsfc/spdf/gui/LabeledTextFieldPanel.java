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
 * $Id: LabeledTextFieldPanel.java,v 1.14 2022/03/24 10:38:36 btharris Exp $
 */

package gsfc.spdf.gui;

import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import gsfc.spdf.gui.AbstractLabeledInputComponent;


/**
 * One or more JTextFields with a single JLabel on a JPanel.
 * 
 * @author B. Harris
 * @version $Revision: 1.14 $
 */
public class LabeledTextFieldPanel 
    extends AbstractLabeledInputComponent {

    /**
     * the JTextFields contained on this component
     */
    private JTextField[] textFields;


    /**
     * the number of JTextField on this component
     */
    private int numFields;


    /**
     * Construct a LabeledTextFieldPanel with the given label and a
     * single JTextField.
     *
     *
     * @param label The label field value for this component
     */
    public LabeledTextFieldPanel(String label) {

        this(label, 1);
    }


    /**
     * Construct a LabeledTextFieldPanel with the given label and border
     * and a single JTextFields.
     *
     * @param label The label field value for this component
     * @param border The border to use for the titled border
     */
    public LabeledTextFieldPanel(String label, Border border) {

        this(label, border, 1);
    }


    /**
     * Construct a LabeledTextFieldPanel with the given label and number
     * of JTextFields.
     *
     * @param label The label field value for this component
     * @param numFields The number of JTextFields to construct on this panel
     */
    public LabeledTextFieldPanel(String label, int numFields) {

        this(label, new EtchedBorder(), numFields);
    }


    /**
     * Construct a LabeledTextFieldPanel with the given label, border,
     * and number of JTextFields.
     *
     * @param label The label field value for this component
     * @param border The border to use for the titled border
     * @param numFields The number of JTextFields to construct on this panel
     */
    public LabeledTextFieldPanel(String label, Border border, int numFields) {

        super(label, border);

        this.numFields = numFields;

        textFields = new JTextField[numFields];
        setLayout(new GridLayout(numFields,1));
        for (int i = 0; i < numFields; i++) {
         
            textFields[i] = new JTextField();
            
            add(textFields[i]);
        };

    }

    /**
     * Construct a LabeledTextFieldPanel with the given label, border,
     * number of JTextFields, and numColumns.
     *
     * @param label The label field value for this component
     * @param border The border to use for the titled border
     * @param numFields The number of JTextFields to construct on this panel
     * @param numColumns The number of columns in each JTextFields 
     */
    public LabeledTextFieldPanel(String label, Border border, int numFields,
                                 int numColumns) {

        super(label, border);

        this.numFields = numFields;

        textFields = new JTextField[numFields];
        setLayout(new GridLayout(numFields,1));
        for (int i = 0; i < numFields; i++) {

            textFields[i] = new JTextField(numColumns);
                      
            add(textFields[i]);
        };
    }


    /**
     * Construct a LabeledTextFieldPanel with the given label, border,
     * number of JTextFields, and numColumns.
     *
     * @param label The label field value for this component
     * @param border The border to use for the titled border
     * @param numFields The number of JTextFields to construct on this panel
     * @param fields The fields to use (should have a length of numFields)
     */
    public LabeledTextFieldPanel(String label, Border border, int numFields,
                                 JTextField[] fields) {

        super(label, border);

        this.numFields = numFields;

        textFields = new JTextField[numFields];
        setLayout(new GridLayout(numFields,1));
        for (int i = 0; i < numFields; i++) {

            textFields[i] = fields[i];
             
           // textFields[i].setMaximumSize(textFields[i].getPreferredSize());
            textFields[i].setAlignmentX(LEFT_ALIGNMENT);
            add(textFields[i]);
        };
    }

    /**
     * @deprecated Use getInputComponentValue()
     */
    public Object get() {

        if (numFields == 1) {

            return getInputComponentValue(0);
        }
        Vector fieldValues = new Vector(numFields);

        for (int i = 0; i < numFields; i++) {

            fieldValues.addElement(getInputComponentValue(i));
        };

        return fieldValues;
    }


    /**
     * @deprecated Use setInputComponentValue()
     */
    public void set(Object value) {

        if (value instanceof Vector) {

            Vector values = (Vector) value;

            for (int i = 0; i < numFields && i < values.size(); i++) {

                setInputComponentValue(i, values.elementAt(i));
            };
        }
        else {  // for backward compatibility

            setInputComponentValue(0, value);
        };
    }


    public void reset() {

        for (int i = 0; i < numFields; i++) {

            textFields[i].setText(null);
        };
    }


    public void addItem(Object value) {

        if (value instanceof Vector) {

            Vector valueVector = (Vector)value;

            for (int i = 0; i < numFields && i < valueVector.size(); i++) {

                textFields[i].setText(valueVector.elementAt(i).toString());
            };
        }
        else {

            textFields[0].setText(value.toString());
        };
    }

    public void setEditable(boolean enabled) {

        for (int i = 0; i < numFields; i++) {

            textFields[i].setEditable(enabled);
        };
    }
 

    /**
     * Adds the specified action listener to receive action events from
     * all textfields.
     *
     * @param l The action listener
     */
    public void addActionListener(ActionListener l) {

        for (int i = 0; i < numFields; i++) {

            textFields[i].addActionListener(l);
        };
    }

    /**
     * Removes the specified action listener so that it no longer receives
     * action events from any of the textfields.
     *
     * @param l The action listener
     */
    public void removeActionListener(ActionListener l) {

        for (int i = 0; i < numFields; i++) {

            textFields[i].removeActionListener(l);
        };
    }

    public int getInputComponentCount() {

        return numFields;
    }

    public Object getInputComponentValue(int index) {
 
        return textFields[index].getText();
    }


    public void setInputComponentValue(int index, Object value) {

        textFields[index].setText("");
//        textFields[index].setText((String)value);
        if (value != null) {

            textFields[index].setText(value instanceof String? 
                    (String)value : ((String[])value)[0]);
        }
        textFields[index].setCaretPosition(0);
    }


    public void setInputComponentToolTipText(int index, String text) {

        textFields[index].setToolTipText(text);
    }

}
