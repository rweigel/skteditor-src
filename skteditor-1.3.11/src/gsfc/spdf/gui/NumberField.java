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
 * $Id: NumberField.java,v 1.6 2022/03/24 10:38:36 btharris Exp $
 */

//$Id: NumberField.java,v 1.6 2022/03/24 10:38:36 btharris Exp $
package gsfc.spdf.gui;

import javax.swing.text.Document;
import java.text.NumberFormat;
import javax.swing.JTextField;

/**
 * Provides similiar methods as java.lang.Number within the context of 
 * a JTextField.
 *
 * @author Phil Williams
 * @version $Revision: 1.6 $
 */
public abstract class NumberField extends JTextField {
    
    public NumberFormat formatter;
  

    public NumberField(int columns) { super(columns); }

    /**
     * Returns the value of the specified number as an 
     * <code>java.lang.Integer</code>. This may involve rounding.
     *
     * @return  the numeric value represented by this object after conversion
     *          to type <code>java.lang.Integer</code>.
     */
    public abstract Integer getInteger();

    /**
     * Returns the value of the specified number as a 
     * <code>java.lang.Long</code>. This may involve rounding.
     *
     * @return  the numeric value represented by this object after conversion
     *          to type <code>java.lang.Long</code>.
     */
    public abstract Long getLong();

    /**
     * Returns the value of the specified number as a 
     * <code>java.lang.Float</code>. This may involve rounding.
     *
     * @return  the numeric value represented by this object after conversion
     *          to type <code>float</code>.
     */
    public abstract Float getFloat();

    /**
     * Returns the value of the specified number as a
     * <code>java.lang.Double</code>. This may involve rounding.
     *
     * @return  the numeric value represented by this object after conversion
     *          to type <code>double</code>.
     */
    public abstract Double getDouble();

    /**
     * Returns the value of the specified number as a
     * <code>java.lang.Byte</code>. This may involve rounding or truncation.
     *
     * @return  the numeric value represented by this object after conversion
     *          to type <code>java.lang.Byte</code>.
     */
    public Byte getByte() {
	return new Byte(getInteger().byteValue());
    }

    /**
     * Returns the value of the specified number as a
     * <code>java.lang.Short</code>. This may involve rounding or truncation.
     *
     * @return  the numeric value represented by this object after conversion
     *          to type <code>java.lang.Short</code>.
     */
    public Short getShort() {
	return new Short(getInteger().shortValue());
    }

    /**
     * Returns the value of the specified number as a <code>int</code>.
     * This may involve rounding or truncation.
     *
     * @return  the numeric value represented by this object after conversion
     *          to type <code>int</code>.
     */
    public int    intValue()    { return getInteger().intValue(); }

    /**
     * Returns the value of the specified number as a <code>long</code>.
     * This may involve rounding or truncation.
     *
     * @return  the numeric value represented by this object after conversion
     *          to type <code>long</code>.
     */
    public long   longValue()   { return getLong().longValue(); }

    /**
     * Returns the value of the specified number as a <code>float</code>.
     * This may involve rounding or truncation.
     *
     * @return  the numeric value represented by this object after conversion
     *          to type <code>float</code>.
     */
    public float  floatValue()  { return getFloat().floatValue(); }

    /**
     * Returns the value of the specified number as a <code>double</code>.
     *
     * @return  the numeric value represented by this object after conversion
     *          to type <code>double</code>.
     */
    public double doubleValue() { return getDouble().doubleValue(); }

    /**
     * Returns the value of the specified number as a <code>byte</code>.
     * This may involve rounding or truncation.
     *
     * @return  the numeric value represented by this object after conversion
     *          to type <code>byte</code>.
     */
    public byte   byteValue()   { return getInteger().byteValue(); }

    /**
     * Returns the value of the specified number as a <code>short</code>.
     * This may involve rounding or truncation.
     *
     * @return  the numeric value represented by this object after conversion
     *          to type <code>short</code>.
     */
    public short  shortValue()  { return getInteger().shortValue(); }


    public abstract Document createDefaultModel();
} // NumberField
