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
 * $Id: FloatNumberField.java,v 1.16 2022/03/24 10:38:36 btharris Exp $
 */

package gsfc.spdf.gui;

import javax.swing.*; 
import javax.swing.text.*; 

import java.text.ParseException;
import java.util.Locale;
import java.text.*;
import java.util.regex.Pattern;

/**
 * A JTextField that only accpets valid floating point numbers
 *
 * @author Phil Williams
 * @version $Revision: 1.16 $
 *
 */
public class FloatNumberField extends NumberField {
    
    static final Pattern pattern = Pattern.compile("(?i)n|na|nan");

    /**
     * Construct a FloatNumberField containing the given value that is
     * the specified width.
     *
     * @param value the default value
     * @param columns the width of the text field in characters.
     */
    public FloatNumberField(float value, int columns) {
	super(columns);
       
       formatter = NumberFormat.getNumberInstance(Locale.US);
   
     formatter.setParseIntegerOnly(false);
     formatter.setMaximumIntegerDigits(5);
     formatter.setMaximumFractionDigits(2);
      
        setText(new Float(value).toString());
    }
    
    /**
     * Creates the Document used by this FloatNumberField.
     */
    public Document createDefaultModel() {
	return new PlainDocument() {
	    public void insertString(int offs, String str, AttributeSet a) 
		throws BadLocationException {
		
		String text = getText(0, getLength());
		String bOff = text.substring(0, offs);
		String aOff = text.substring(offs, text.length());
		StringBuffer testText = new StringBuffer();
		testText.append(bOff);
		testText.append(str);
		testText.append(aOff);

		try   {                
                    if (!str.equalsIgnoreCase("-")&&(pattern.matcher(testText)).matches()!=true)
                       formatter.parse(testText.toString());		
                    super.insertString(offs, str, a);
		} catch (ParseException e) {
		    java.awt.Toolkit.getDefaultToolkit().beep();
		}
	    }
	};
    }
		    
    /**
     * Returns the value of the specified number as an <code>int</code>.
     * This may involve rounding.
     *
     * @return  the numeric value represented by this object after conversion
     *          to type <code>int</code>.
     */
    public  Integer getInteger() {
	try {
	    return new Integer(formatter.parse(getText()).intValue());
	} catch (ParseException e) {
	    return null;
	}
    }

    /**
     * Returns the value of the specified number as a <code>long</code>.
     * This may involve rounding.
     *
     * @return  the numeric value represented by this object after conversion
     *          to type <code>long</code>.
     */
    public  Long getLong() {
	try {
	    return new Long(formatter.parse(getText()).longValue());
	} catch (ParseException e) {
	    return null;
	}
    }

    /**
     * Returns the value of the specified number as a <code>float</code>.
     * This may involve rounding.
     *
     * @return  the numeric value represented by this object after conversion
     *          to type <code>float</code>.
     */
    public  Float getFloat() {

        try {  
            if(getText().equalsIgnoreCase("nan"))
                    
                return new Float(Float.NaN);
                
            return new Float(Float.parseFloat(getText()));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Returns the value of the specified number as a <code>double</code>.
     * This may involve rounding.
     *
     * @return  the numeric value represented by this object after conversion
     *          to type <code>double</code>.
     */
    public  Double getDouble() {
        
        try {
            if(getText().equalsIgnoreCase("nan"))
                    
                return new Double(Double.NaN);
            return new Double(Double.parseDouble(getText()));
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
