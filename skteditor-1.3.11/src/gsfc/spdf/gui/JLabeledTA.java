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
 * $Id: JLabeledTA.java,v 1.17 2022/03/24 10:38:36 btharris Exp $
 */

// $Id: JLabeledTA.java,v 1.17 2022/03/24 10:38:36 btharris Exp $
package gsfc.spdf.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.BadLocationException;

/**
 * A JTextArea with a label.
 *
 * @author Phil Williams
 * @version $Revision: 1.17 $
 */
public class JLabeledTA 
    extends AbstractLabeledComponent 
{

    private     JScrollPane	scrollPane;
    public      JTextArea       textArea;

  
    // Implementation of Interface Routines
    public Object get() {

	return textArea.getText();
    }

    public void set(Object str) {
	
        textArea.setText("");
//	textArea.setText((String)str);
        if (str != null) {

            textArea.setText(
                str instanceof String ? (String)str : ((String[])str)[0]);
        }
    }

    public void setLineWrap(boolean wrap){
    textArea.setLineWrap(wrap);
    }
   
    public void setWrapStyleWord(boolean wrapStyleWord){
    textArea.setWrapStyleWord(wrapStyleWord);
    }
    
    public void reset() {

	textArea.setText(null);    
    }
    
    public void addItem(Object obj) {
	textArea.append(obj.toString());
    }
       
       
    public void setEnabled(boolean enabled) {
        if(enabled)
        { 
            textArea.setEnabled(true); 
            label.setForeground(enabledColor); 
	} else { 
            textArea.setEnabled(false);  
            label.setForeground(disabledColor);               
 	}
    }   

    /**
     * Creates a Box containing a JLabel and a JTextArea that line wraps. 
     *
     * @param	str	 Text of the label.
     * @param	rows	 Number of rows in the JTextArea
     * @param	columns  Number of columns in the JTextArea
     *
     */
    public JLabeledTA(String str, int rows, int columns) {
	this(str, rows, columns, true);
     }

    /**
     * Creates a Box containing a JLabel and a JTextArea that line wraps. 
     *
     * @param	str	 Text of the label.
     * @param	rows	 Number of rows in the JTextArea
     * @param	columns  Number of columns in the JTextArea
     * @param   enabled  The initial state of the text area
     */
    public JLabeledTA (String str, int rows, int columns, boolean enabled)  {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        label = new JLabel(str);
       
        textArea = new JTextArea("",rows, columns);
	textArea.setLineWrap(true);
	textArea.setWrapStyleWord(true);
        label.setAlignmentX(LEFT_ALIGNMENT);
        textArea.setAlignmentX(LEFT_ALIGNMENT);
	enabledColor  = Color.black;
	disabledColor = new Color(142, 142, 142);
	setEnabled(enabled);
        add(label);
        add(Box.createVerticalStrut(2));
    
	scrollPane = new JScrollPane(textArea);	
        scrollPane.setAlignmentX(LEFT_ALIGNMENT);
	scrollPane.setMinimumSize(scrollPane.getPreferredSize());
        add(scrollPane);
        
     }
 
}  // End JLabeledTA
