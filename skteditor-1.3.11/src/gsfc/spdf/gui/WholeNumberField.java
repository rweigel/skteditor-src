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
 * $Id: WholeNumberField.java,v 1.9 2022/03/24 10:38:36 btharris Exp $
 */

package gsfc.spdf.gui;

import javax.swing.*; 
import javax.swing.text.*; 

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * This is an extension from the Java tutorial that can be found at
 * <a href="http://java.sun.com">JavaSoft</a>
 */
public class WholeNumberField extends NumberField {

    
    private boolean parseNegative = true;
    
    /**
       * Get the value of parseNegative.
       * @return Value of parseNegative.
       */
    public boolean isParseNegative() {return parseNegative;}
    
    /**
       * Set the value of parseNegative.
       * @param v  Value to assign to parseNegative.
       */
    public void setParseNegative(boolean  v) {this.parseNegative = v;}
    
    public WholeNumberField(int value, int columns) {
	super(columns);
        formatter = NumberFormat.getNumberInstance(Locale.US);
        formatter.setParseIntegerOnly(true);
	setText(new Long(value).toString());
    }

    public Document createDefaultModel() {
	return new PlainDocument() {
	    public void insertString(int offs, String str, AttributeSet a) 
		throws BadLocationException {
		
		char[] text   = getText(0, getEndPosition().getOffset()).
		    toCharArray();
		char[] source = str.toCharArray();
		char[] result = new char[source.length];
		int j = 0;
		
		for (int i = 0; i < result.length; i++) {
		    if (Character.isDigit(source[i]) || 
			(isParseNegative() && 
			 (offs == 0) && (source[i] == '-')))
			result[j++] = source[i];
		    else {
			java.awt.Toolkit.getDefaultToolkit().beep();
		    }
		}
		super.insertString(offs, new String(result, 0, j), a);
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
        if (System.getProperty("os.name").equals("Mac OS")) 
        {
            try {
                return new Float(formatter.parse(getText()).floatValue());
            } catch (ParseException e) {
                return null;
            }
        }
        else
        {   
            try {
                return new Float(Float.parseFloat(getText()));
            } catch (NumberFormatException e) {
                return null;
            }
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
        
        if (System.getProperty("os.name").equals("Mac OS")) 
        {
            try {
                return new Double(formatter.parse(getText()).doubleValue());
            } catch (ParseException e) {
                return null;
            }
        }
        else
        {
            try {
                return new Double(Double.parseDouble(getText()));
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }

}
