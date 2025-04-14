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
 * $Id: LabeledTextAreaPanel.java,v 1.13 2022/03/24 10:38:36 btharris Exp $
 */
package gsfc.spdf.gui;


import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.StringTokenizer;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import gsfc.spdf.gui.AbstractLabeledInputComponent;


/**
 * One or more JTextAreas with a single JLabel on a JPanel.
 * 
 * @author B. Harris
 * @version $Revision: 1.13 $
 */
public class LabeledTextAreaPanel 
    extends AbstractLabeledInputComponent {

    /**
     * checkbox indicating whether a text areas is to be considered multiple
     * lines or a single line
     */
    private JCheckBox multiline;


    /**
     * the JTextAreas contained on this component
     */
    private JTextArea[] textAreas;


    /**
     * the number of JTextArea on this component
     */
    private int numFields;


    /**
     * Construct a LabeledTextAreaPanel with the given label and a
     * single JTextArea.
     *
     * A BoxLayout (Y_AXIS) is set as the default LayoutManager.  The
     * border is an EtchedBorder.
     *
     * @param label The label field value for this component
     */
    public LabeledTextAreaPanel(String label) {

        this(label, 1);
    }


    /**
     * Construct a LabeledTextAreaPanel with the given label and border
     * and a single JTextAreas.
     *
     * A BoxLayout (Y_AXIS) is set as the default LayoutManager.  
     *
     * @param label The label field value for this component
     * @param border The border to use for the titled border
     */
    public LabeledTextAreaPanel(String label, Border border) {

        this(label, border, 1);
    }


    /**
     * Construct a LabeledTextAreaPanel with the given label and number
     * of JTextAreas.
     *
     * A BoxLayout (Y_AXIS) is set as the default LayoutManager.  The
     * border is an EtchedBorder.
     *
     * @param label The label field value for this component
     * @param numFields The number of JTextAreas to construct on this panel
     */
    public LabeledTextAreaPanel(String label, int numFields) {

        this(label, new EtchedBorder(), numFields);
    }


    /**
     * Construct a LabeledTextAreaPanel with the given label, border,
     * and number of JTextAreas.
     *
     * A BoxLayout (Y_AXIS) is set as the default LayoutManager.  
     *
     * @param label The label field value for this component
     * @param border The border to use for the titled border
     * @param numFields The number of JTextAreas to construct on this panel
     */
    public LabeledTextAreaPanel(String label, Border border, int numFields) {

        super(label, border);

        multiline = new JCheckBox("preserve lines", false);
        multiline.setVisible(false);

        this.numFields = numFields;

        textAreas = new JTextArea[numFields];

        for (int i = 0; i < numFields; i++) {

            textAreas[i] = new JTextArea();
            JScrollPane scrollPane = new JScrollPane(textAreas[i]);
            add(scrollPane);
        };

        add(multiline);
    }

    /**
     * Construct a LabeledTextAreaPanel with the given label, border,
     * number of JTextAreas, and numColumns.
     *
     * A BoxLayout (Y_AXIS) is set as the default LayoutManager.  
     *
     * @param label The label field value for this component
     * @param border The border to use for the titled border
     * @param numFields The number of JTextAreas to construct on this panel
     * @param numRows The number of rows in each JTextAreas 
     * @param numColumns The number of columns in each JTextAreas 
     */
    public LabeledTextAreaPanel(String label, Border border, int numFields,
                                int numRows, int numColumns) {

        super(label, border);

        multiline = new JCheckBox("preserve lines", false);
        multiline.setVisible(false);

        this.numFields = numFields;

        textAreas = new JTextArea[numFields];

        for (int i = 0; i < numFields; i++) {

            textAreas[i] = new JTextArea(numRows, numColumns);
            JScrollPane scrollPane = new JScrollPane(textAreas[i]);
            add(scrollPane);
        };

        add(multiline);
    }


    /**
     * Sets whether the "multiline text" checkbox option is visible.
     * 
     * @param visible indicates whether the multiline text checkbox is visible
     */
    public void setMultiLineOptionVisible(boolean visible) {

        multiline.setVisible(visible);
    }


    /**
     * Sets the value of the "multiline text" checkbox value.
     * 
     * @param option the value to set the multiline text checkbox to
     */
    public void setMultiLineOption(boolean option) {

        multiline.setSelected(option);
    }


    /**
     * Gets the current value of the "multiline text" checkbox.
     * 
     * @return the value of the multiline text checkbox
     */
    public boolean getMultiLineOption() {

        return multiline.isSelected();
    }


    /**
     * Sets the line wrap option of the text area.
     * 
     * @param wrap line wrap value
     */
    public void setLineWrap(boolean wrap) {

        for (int i = 0; i < numFields; i++) {

            textAreas[i].setLineWrap(wrap);
        };
    }


    /**
     * Gets the line wrap value of the text area.
     * 
     * @return text area line wrap value
     */
    public boolean getLineWrap() {

        return textAreas[0].getLineWrap();
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

            textAreas[i].setText(null);
        };
    }


    public void addItem(Object value) {

        if (value instanceof Vector) {

            Vector valueVector = (Vector)value;

            for (int i = 0; i < numFields && i < valueVector.size(); i++) {

                textAreas[i].setText(valueVector.elementAt(i).toString());
            };
        }
        else {

            textAreas[0].setText(value.toString());
        };
    }


    public void setEditable(boolean enabled) {

        for (int i = 0; i < numFields; i++) {

            textAreas[i].setEditable(enabled);
        };
    }
 

    public int getInputComponentCount() {

        return numFields;
    }


    public Object getInputComponentValue(int index) {
 
        JTextArea textArea = textAreas[index];

        if (getMultiLineOption()) {

            return textAreaAsLines(textArea);
        }
        else {

            return textAreaAsALine(textArea);
        }
    }


    public void setInputComponentValue(int index, Object value) {

        textAreas[index].setText("");

        if (value instanceof Vector) {

            textAreas[index].setText(createTextFromLines((Vector)value));
        }
        else if (value instanceof String[]) {

            textAreas[index].setText(createTextFromLines((String[])value));
        }
        else {

            textAreas[index].setText((String)value);
        };

        textAreas[index].setCaretPosition(0);
        
    }


    public void setInputComponentToolTipText(int index, String text) {

        textAreas[index].setToolTipText(text);
    }


    /**
     * Provides the text from the given text area as a vector of lines.
     * 
     * @param textArea the text area whose text is to be retrieved
     * @return vector whose elements are the lines of text from the given text
     * area
     */
    protected Vector textAreaAsLines(JTextArea textArea) {

        String text = textArea.getText();

        Vector lines = new Vector();

        for (int i = 0; i < textArea.getLineCount(); i++) {

            try {

                String line = 
                    text.substring(textArea.getLineStartOffset(i),
                                   textArea.getLineEndOffset(i));
                                        // a line of text

                if (line.endsWith("\n")) {

                    //
                    // Remove newline that was added for display 
                    // in text area.
                    //
                    line = line.substring(0, line.length() - 1);
                }
                if (line.length() > 0) {

                    lines.addElement(line);
                }
            }
            catch (BadLocationException e) {

                e.printStackTrace();
            };
        };

        return lines;
    }


    /**
     * Provides the text from the given text area as a single line (all
     * lines are concatentated into a single string value)
     * 
     * @param textArea the text area whose text is to be retrieved
     * @return all the text from the given text area as a single value (no line
     * delimiters)
     */
    protected String textAreaAsALine(JTextArea textArea) {

        String text = textArea.getText();

        StringBuffer strBuf = new StringBuffer();

        for (int i = 0; i < textArea.getLineCount(); i++) {

            try {
                
                strBuf.append(
                    text.substring(textArea.getLineStartOffset(i),
                    textArea.getLineEndOffset(i)));                  
                   
            }
            catch (BadLocationException e) {

                e.printStackTrace();
            };
        };

        return strBuf.toString();
    }


    /**
     * Creates a string value containing line delimited text from the
     * given vector of strings.  Each element of the given vector 
     * represents one line of text (may or may not be line delimited at
     * entry).
     * 
     * @param lines vector in which each element represents a line of text 
     *              (may or may not be terminated by a line delimiter character)
     * @return a string containing the line delimited text created from the 
     *         given lines of text
     */
    protected String createTextFromLines(Vector lines) {

        StringBuffer strBuf = new StringBuffer();
        String line;

        for (int i = 0; i < lines.size(); i++) {

            line = (String)lines.elementAt(i);

            if (line.endsWith("\n")) {

                strBuf.append(line);
            }
            else {

                strBuf.append(line + "\n");
            };
        };

        return strBuf.toString();
    }


    /**
     * Creates a string value containing line delimited text from the
     * given array of strings.  Each element of the given array 
     * represents one line of text (may or may not be line delimited).
     * 
     * @param lines array in which each element represents a line of text 
     *     (may or may not be terminated by a line delimiter character).
     * @return a string containing the line delimited text created from the 
     *     given lines of text.
     */
    protected String createTextFromLines(String[] lines) {

        StringBuilder strBuf = new StringBuilder();

        for (String line : lines) {

            if (line.endsWith("\n")) {

                strBuf.append(line);
            }
            else {

                strBuf.append(line + "\n");
            };
        };

        return strBuf.toString();
    }

}

