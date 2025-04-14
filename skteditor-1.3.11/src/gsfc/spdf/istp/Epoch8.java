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
 * $Id: Epoch8.java,v 1.25 2022/08/01 16:11:37 btharris Exp $
 */
package gsfc.spdf.istp;

import java.lang.reflect.Array;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

import gsfc.nssdc.cdf.Attribute;
import gsfc.nssdc.cdf.CDF;
import gsfc.nssdc.cdf.CDFException;


/**
 * This class represents an ISTP Epoch variable.
 * 
 * @author B. Harris
 * @version $Revision: 1.25 $
 */
public class Epoch8
    extends Epoch {


    /**
     * ISTP VALIDMIN value.
     */
    public final static Double VALIDMIN;

    /**
     * ISTP VALIDMAX value.
     */
    public final static Double VALIDMAX;

    /**
     * ISTP FILLVAL value.
     */
    public final static Double FILLVAL = new Double(-1.0E31);

    /**
     * The CDF minimum epoch value.
     */
    public final static Double MIN;

    /**
     * gsfc.nssdc.cdf.epoch.encode() format string value of the CDF 
     * minimum epoch value.
     */
    public final static String MIN_STRING;

    /**
     * The CDF maximum epoch value.
     */
    public final static Double MAX;

    /**
     * gsfc.nssdc.cdf.epoch.encode() format string value of the CDF 
     * maximum epoch value.
     */
    public final static String MAX_STRING;


    static {

        double validMin = 0;           // temporary valid min value
        double validMax = 0;           // temporary valid max value
        double min = 0;                // temporary min value
        double max = 0;                // temporary max value

        try {

            validMin =
                gsfc.nssdc.cdf.util.Epoch.compute(1990, 1, 1, 0, 0, 0, 0);
            validMax =
                gsfc.nssdc.cdf.util.Epoch.compute(2029, 12, 31, 23, 59, 
                                                  59, 999);
            min = gsfc.nssdc.cdf.util.Epoch.compute(0, 1, 1, 0, 0, 0, 0);
            max = gsfc.nssdc.cdf.util.Epoch.compute(9999, 12, 31, 23, 59, 
                                                    59, 998);
        }
        catch (CDFException e) {

            System.err.println("gsfc.spdf.istp.Epoch: CDFException " +
                e.getMessage() + " while creating VALIDMIN/MAX");
        }

        VALIDMIN = new Double(validMin);
        VALIDMAX = new Double(validMax);
        MIN = new Double(min);
        MAX = new Double(max);
        MIN_STRING = gsfc.nssdc.cdf.util.Epoch.encode(MIN.doubleValue());
        MAX_STRING = gsfc.nssdc.cdf.util.Epoch.encode(MAX.doubleValue());
    }


    /**
     * Determines whether the given value is outside the range of valid
     * CDF epoch values.  Note that the range of valid ISTP epoch values
     * is smaller.
     *
     * @param value value to check
     * @return true if the given value is not a valid epoch value
     */
    public static boolean isNotAnEpoch(Double value) {

        return value.compareTo(MIN) < 0 || value.compareTo(MAX) > 0;
    }


    /**
     * Determines whether the given value is equal to the ISTP FILLVAL.
     *
     * @param value value to be tested
     * @return true if the given value is equal to FILLVAL, otherwise
     *     false
     */
    public static boolean isFillval(Double value) {

        return value.compareTo(FILLVAL) == 0;
    }


    /**
     * Returns the String representation of the given epoch value.  This 
     * method is similar to the <code>gsfc.nssdc.cdf.util.Epoch.encode
     * </code> method except in its representation of 
     * "standard ISTP FILLVAL" and "not an epoch" values.
     *
     * @param value value to convert
     * @return the String representation of the given value
     * @see #toString(Object)
     */
    public static String toString(Double value) {

        if (isFillval(value)) {

            return FILLVAL_STRING + " (" + value.toString() + ")";
        }
        else if (isNotAnEpoch(value)) {

            return value.toString();
        }
        else {

            return gsfc.nssdc.cdf.util.Epoch.encode(value.doubleValue());
        }
    }


    /**
     * Returns the String representation of the given epoch value(s).  This 
     * method is similar to the <code>gsfc.nssdc.cdf.util.Epoch.encode
     * </code> method except in its representation of 
     * "standard ISTP FILLVAL" and "not an epoch" values.
     *
     * @param values values to convert.  May be an array of Double or a
     *     single Double value.
     * @return the String[] representation of the given value.
     * @see #toString(Double)
     */
    public static String[] toString(
        Object values) {

        String[] strValues = null;     // resulting values

        if (values.getClass().isArray()) {

            strValues = new String[Array.getLength(values)];

            for (int i = 0; i < strValues.length; i++) {

                strValues[i] = toString(Array.getDouble(values, i));
            }   
        }   
        else {

            strValues = new String[1];
            strValues[0] = toString((Double)values);
        }   

        return strValues;
    }


    /**
     * Parses the given data/time string and returns the corresponding
     * epoch value.  The recognized formats are as follows:
     * <ul>
     *   <li><code>FILLVAL_STRING</code></li>
     *   <li><code>NaE_STRING</code></li>
     *   <li>{@link gsfc.spdf.cdf.Value#parseEpoch(String)
     *        gsfc.spdf.cdf.Value.parseEpoch(String)}</li>
     * </ul>
     *
     * @param value date/time string value to be parsed.
     * @return corresponding epoch value.
     * @throws CDFException if the given value cannot be parsed.
     */
    public static Double fromString(String value) 
        throws CDFException {

        if (value.startsWith(FILLVAL_STRING) ||
            value.equalsIgnoreCase(NaE_STRING)) {

            return FILLVAL;
        }
        else {

            return gsfc.spdf.cdf.Value.parseEpoch(value);
        }
    }


    /**
     * Creates an ISTP Epoch variable for the given CDF variable.
     *
     * @param var a CDF variable with the characteristics of an ISTP epoch
     *            variable
     */
    protected Epoch8(gsfc.nssdc.cdf.Variable var) {

        super(var);
    }


    /**
     * Create a new instance of an ISTP Epoch variable in the given
     * CDF file.  The new instance has the necessary ISTP type, dimensions,
     * attributes, and attribute values.
     *
     * @param cdf the CDF file in which the Epoch variable is to be 
     *            created
     * @throws CDFException if a CDFException occurs
     * @throws ISTPComplianceException if the given characteristics fail
     *             to be ISTP compliant
     */
    public Epoch8(CDF cdf) 
        throws CDFException, ISTPComplianceException {

        this(cdf, "Epoch");
    }
    

    /**
     * Create a new instance of an ISTP Epoch variable in the given
     * CDF file.  The new instance has the necessary ISTP type, dimensions,
     * attributes, and attribute values.
     *
     * @param cdf the CDF file in which the Epoch variable is to be 
     *            created
     * @param name variable name
     * @throws CDFException if a CDFException occurs
     * @throws ISTPComplianceException if the given characteristics fail
     *             to be ISTP compliant
     */
    public Epoch8(CDF cdf, String name ) 
        throws CDFException, ISTPComplianceException {

        this(cdf, name, 0, new long[] {1}, CDF.VARY);
    }


    /**
     * Create a new instance of an ISTP Epoch variable in the given
     * CDF file.  The new instance has the necessary ISTP type, dimensions,
     * attributes, and attribute values.
     *
     * @param cdf the CDF file in which the Epoch variable is to be 
     *            created
     * @param name variable name
     * @param numDims dimensionality of this variable
     * @param dimSizes dimension sizes.  An array of length numDims 
     *            indicating the size of each dimension
     * @param recVary record variance (VARY or NOVARY)
     * @throws CDFException if a CDFException occurs
     * @throws ISTPComplianceException if the given characteristics fail
     *             to be ISTP compliant
     */
    public Epoch8(CDF cdf, String name, long numDims, long[] dimSizes,
        long recVary ) 
        throws CDFException, ISTPComplianceException {

        super(cdf, name, CDF.CDF_EPOCH, numDims, dimSizes,
              recVary, new long[] {recVary});
        
        setAttributeValue("VALIDMIN", VALIDMIN);
        setAttributeValue("VALIDMAX", VALIDMAX);

        setUnitsValue();

        setTimeBaseValue();
        setAttributeValue("TIME_SCALE", (String)null);
        setAttributeValue("REFERENCE_POSITION", (String)null);
        setAttributeValue("ABSOLUTE_ERROR", CDF.CDF_EPOCH, null);
        setAttributeValue("RELATIVE_ERROR", CDF.CDF_EPOCH, null);
    }


    /**
     * Sets the UNITS attribute's value.
     */
    public void setUnitsValue() 
        throws CDFException {

        setUnitsValue("ms");
    }


    /**
     * Sets the TIME_BASE attribute's value.
     */
    public void setTimeBaseValue() 
        throws CDFException {

        try {

            setAttributeValue("TIME_BASE", "0 AD");
        }
        catch (ISTPIdentifierException e) {

            // should not happen because the idenifier is good
            e.printStackTrace();
        }
    }


    /**
     * Sets an attribute entry value.
     *
     * @param name name of attribute
     * @param value new value of attribute entry
     * @throws CDFException if a CDFException occurs
     */
    protected void setAttributeValue(String name, Double value)
        throws CDFException {

        try {

            Attribute.create(var.getMyCDF(), name, CDF.VARIABLE_SCOPE);
        }
        catch (CDFException e) {

            // ignore it -- probably already exists
        }

        var.putEntry(name, CDF.CDF_EPOCH, value);
    }


    /**
     * Sets this variable's value.
     *
     * @param record identifies the specific record
     * @param year year
     * @param month month
     * @param day day
     * @param hour hour
     * @param minute minute
     * @param second second
     * @param msec millisecond
     * @throws CDFException if a CDFException occurs
     */
    public void set(long record, long year, long month, long day, 
                    long hour, long minute, long second, long msec)
        throws CDFException {

        double epoch =
            gsfc.nssdc.cdf.util.Epoch.compute(year, month, day, hour, 
                                              minute, second, msec);
                                       // epoch value

        var.putRecord(record, new Double(epoch));

    }


    /**
     * Sets this variable's value in the specified record.
     *
     * @param record identifies the specific record
     * @param value new value
     * @throws CDFException if a CDFException occurs
     */
    public void set(long record, Date value) 
        throws CDFException {

        Calendar calendar = GregorianCalendar.getInstance(UTC_TIME_ZONE);
                                       // calendar to intepret Date
        calendar.setTime(value);

        set(record, calendar.get(Calendar.YEAR), 
            calendar.get(Calendar.MONTH), 
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            calendar.get(Calendar.SECOND),
            calendar.get(Calendar.MILLISECOND));
    }


    /**
     * Gets the Date value at the specified record.
     *
     * @param record identifies a specific record
     * @return epoch value from the specfied record
     * @throws CDFException if a CDFException occurs
     */
    public Date getDate(long record) 
        throws CDFException {

        Double time = (Double)var.getRecord(record);
                                       // Double representation of CDF 
                                       //  epoch
        long[] components = 
            gsfc.nssdc.cdf.util.Epoch.breakdown(time.doubleValue());
                                       // component values

        Calendar calendar = GregorianCalendar.getInstance(UTC_TIME_ZONE);
                                       // calendar to create a Date

        calendar.set(Calendar.YEAR, (int)components[0]);
        calendar.set(Calendar.MONTH, (int)components[1] - 1);
        calendar.set(Calendar.DAY_OF_MONTH, (int)components[2]);
        calendar.set(Calendar.HOUR_OF_DAY, (int)components[3]);
        calendar.set(Calendar.MINUTE, (int)components[4]);
        calendar.set(Calendar.SECOND, (int)components[5]);
        calendar.set(Calendar.MILLISECOND, (int)components[6]);

        return calendar.getTime();
    }


    /**
     * Gets the default value.
     *
     * @return the default value
     */
    public static Object getDefaultValue() {

        return new Double(VALIDMIN.doubleValue());
    }

}
