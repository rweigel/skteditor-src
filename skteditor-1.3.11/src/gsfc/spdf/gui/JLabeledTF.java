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
 * $Id: JLabeledTF.java,v 1.14 2022/08/02 11:41:48 btharris Exp $
 */

// $Id: JLabeledTF.java,v 1.14 2022/08/02 11:41:48 btharris Exp $
package gsfc.spdf.gui;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;

/**
 * A JTextArea with a label.
 *
 * @author Phil Williams
 * @version $Revision: 1.14 $
 */
public class JLabeledTF 
    extends AbstractLabeledComponent 
    implements ActionListener
{

    /**
     * The text field
     */
    public      JTextField      textField;

    /**
     * The text field's Document model
     */
    public      Document        document;

    // Implementation of Interface Routines
    public Object get() {
	return textField.getText();
    }

    public void set(Object str) {
	textField.setText("");
	textField.setText((String)str);
	textField.setCaretPosition(0);
    }

    public void reset() {
	textField.setText(null);
    }
    
    public void addItem(Object obj) {
	textField.setText(obj.toString());
    }

    public void setEnabled(boolean enabled) {
	if (enabled) {
	    textField.setEnabled(true);
	    label.setForeground(enabledColor);
	} else {
	    textField.setEnabled(false);
	    label.setForeground(disabledColor);
	}
    }

    public void selectAll() {
	textField.selectAll();
    }
    
    public void addActionListener(ActionListener l) {
	textField.addActionListener(l); 
    }

    public void removeActionListener(ActionListener l) {
	textField.removeActionListener(l); 
    }

    // Constructors

    /**
     * Create a panel with a label and a text field.
     *
     * @param str the label text
     * @param columns the number of columns in the text field
     */
    public JLabeledTF (String str, int columns)  {
	this(str, columns, new PlainDocument(), true);
    }

    /**
     * Create a panel with a label and a text field.
     *
     * @param str the label text
     * @param columns the number of columns in the text field
     * @param document the text storage to use.
     */
    public JLabeledTF(String str, int columns, Document document) {
	this(str, columns, document, true);
    }
    
    /**
     * Create a panel with a label and a text field.
     *
     * @param str the label text
     * @param columns the number of columns in the text field
     * @param document the text field's document model
     * @param enabled the initial state of this component
     */
    public JLabeledTF (String str, int columns, 
		       Document document, boolean enabled)  {
	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

	this.document = document;

        label = new JLabel(str);
        textField = new JTextField(columns);
	textField.setDocument(document);
        textField.setAlignmentX(LEFT_ALIGNMENT);
        textField.setMinimumSize(new Dimension(100,15));

	enabledColor  = Color.black;
	disabledColor = new Color(142, 142, 142);
	setEnabled(enabled);
	
      //  label.setFont(new Font("Dialog",1,11));

        add(label);
        add(Box.createVerticalStrut(3));
        add(textField);
    }

    /**
     * Create a panel with a label and a text field.
     *
     * @param str the label text
     * @param columns the number of columns in the text field
     * @param enabled the initial state of this component
     * @param altEdit enabled an alternate editing method
     */
    public JLabeledTF (String str, int columns, 
		       boolean enabled, boolean altEdit)  {
	this.document = new PlainDocument();
	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

	JPanel jp = new JPanel();
	jp.setLayout(new BoxLayout( jp, BoxLayout.X_AXIS ));
	jp.setAlignmentY(TOP_ALIGNMENT);

        label = new JLabel(str);

	JButton edit = new JButton("...");
	edit.addActionListener( this );

        textField = new JTextField(columns);
	textField.setDocument(document);
        textField.setAlignmentX(LEFT_ALIGNMENT);

	jp.add(textField);
	jp.add(edit);

        add(label);
        add(Box.createVerticalStrut(3));
        add(jp);

	enabledColor  = Color.black;
	disabledColor = new Color(142, 142, 142);
	setEnabled(enabled);
    }

    public JLabeledTF (String str, JTextField textField, boolean enabled)  {
	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        label = new JLabel(str);
        this.textField = textField;

	enabledColor  = Color.black;
	disabledColor = new Color(142, 142, 142);
	setEnabled(enabled);

        add(label);
        add(Box.createVerticalStrut(3));
        add(textField);
    }

    /**
     * Sets the maximum length of text in the textfield.
     * This method is used if you the column number is different than
     * the maximum number of characters.
     *
     * @param maximum maximum length of text field.
     */
    public void setMaxLength(int maximum) {

	textField.setDocument(new LimitedPlainDocument(maximum));
    }

    public void setLabel(String sLabel) {
	label.setText(sLabel);
	label.revalidate();
	if (label.isVisible()) 
	    label.repaint();
    }

    public void actionPerformed(ActionEvent event) {
	String message = "Enter "+label.getText()+" value:";
	String oldValue = textField.getText();
	JOptionPane editor = new JOptionPane(message,
					     JOptionPane.QUESTION_MESSAGE,
					     JOptionPane.OK_CANCEL_OPTION);
	editor.setWantsInput( true );
	editor.setInputValue(oldValue);
	editor.setMessage(message);
	JDialog ed = editor.createDialog(null, "Edit Value");
	ed.show();
	String newValue = (String)editor.getInputValue();
	if (newValue != null) {
	    textField.setText(newValue);
	    textField.setCaretPosition(0);
	}
    }
	
}  // End JLabeledTF

