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
 * $Id: Epoch16.java,v 1.27 2022/08/01 16:11:37 btharris Exp $
 */
package gsfc.spdf.istp;

import java.lang.reflect.Array;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

import gsfc.nssdc.cdf.Attribute;
import gsfc.nssdc.cdf.CDF;
import gsfc.nssdc.cdf.CDFException;
import gsfc.nssdc.cdf.Variable;



/**
 * This class represents an ISTP Epoch16 variable.
 * 
 * @author rchimiak
 * @version $Revision: 1.27 $
 */
public class Epoch16 
    extends Epoch {


    /**
     * ISTP VALIDMIN value.
     */
    final static double[] VALIDMIN;

    /**
     * ISTP VALIDMAX value.
     */
    final static double[] VALIDMAX;

    /**
     * ISTP FILLVAL value.
     */
    final static double[] FILLVAL = 
        new double[] {-1.0E31, -1.0E31};

    /**
     * The CDF minimum epoch value.
     */
    final static double[] MIN;

    /**
     * gsfc.nssdc.cdf.epoch.encode() format string value of the CDF 
     * minimum epoch value.
     */
    final static String MIN_STRING;

    /**
     * The CDF maximum epoch value.
     */
    final static double[] MAX;

    /**
     * gsfc.nssdc.cdf.epoch.encode() format string value of the CDF 
     * maximum epoch value.
     */
    public final static String MAX_STRING;


    static {

        double[] validMin = new double[2];           
                                       // temporary valid min value
        double[] validMax = new double[2];           
                                       // temporary valid max value
        double[] min = new double[2];  // temporary min value
        double[] max = new double[2];  // temporary max value

        try {
            
            gsfc.nssdc.cdf.util.Epoch16.compute(1990, 1, 1, 0, 0,
                                      0, 0, 0, 0, 0, validMin);
            
            gsfc.nssdc.cdf.util.Epoch16.compute(2029, 12, 31, 23, 59,
                                     59, 999, 999, 999, 999, validMax);

            gsfc.nssdc.cdf.util.Epoch16.compute(0000, 1, 1, 0, 0,
                                     0, 0, 0, 0, 0, min);

            gsfc.nssdc.cdf.util.Epoch16.compute(9999, 12, 31, 23, 59,
                                     59, 999, 999, 999, 998, max);

        }
        catch (CDFException e) {

            System.err.println("gsfc.spdf.istp.Epoch16: CDFException " +
                e.getMessage() + " while creating VALIDMIN/MAX");
        }

        VALIDMIN = validMin;
        VALIDMAX = validMax;
        MIN = min;
        MAX = max;
        MIN_STRING = gsfc.nssdc.cdf.util.Epoch16.encode(MIN);
        MAX_STRING = gsfc.nssdc.cdf.util.Epoch16.encode(MAX);
    }


    /**
     * Determines whether the given value is outside the range of valid
     * CDF epoch values.  Note that the range of valid ISTP epoch values
     * is smaller.
     *
     * @param value value to check
     * @return true if the given value is not a valid epoch value
     */
    public static boolean isNotAnEpoch(double[] value) {

        return value[0] < MIN[0] || value[1] < MIN[1] || 
               value[0] > MAX[0] || 
               (value[0] == MAX[0] && value[1] > MAX[1]);
    }


    /**
     * Determines whether the given value is equal to the ISTP FILLVAL.
     *
     * @param value value to be tested
     * @return true if the given value is equal to FILLVAL, otherwise
     *     false
     */
    public static boolean isFillval(double[] value) {

        return value[0] == FILLVAL[0] && value[1] == FILLVAL[1];
    }


    /**
     * Returns the String representation of the given epoch16 value.  This
     * method is similar to the <code>gsfc.nssdc.cdf.util.Epoch16.encode
     * </code> method except in its representation of 
     * "standard ISTP FILLVAL" and "not an epoch" values.
     *
     * @param value value to convert
     * @return the String representation of the given value
     * @see #toString(Object)
     */
    public static String toString(double[] value) {

        if (value[0] == FILLVAL[0] && value[1] == FILLVAL[1]) {

            return FILLVAL_STRING + " (" + Double.toString(value[0]) +
                   ", " + Double.toString(value[1]) + ")";
        }
        else if (isNotAnEpoch(value)) {

            return "(" + Double.toString(value[0]) +
                   ", " + Double.toString(value[1]) + ")";
        }
        else {

            return gsfc.nssdc.cdf.util.Epoch16.encode(value);
        }
    }


    /**
     * Returns the String representation of the given epoch16 value(s).  
     * This method is similar to the 
     * <code>gsfc.nssdc.cdf.util.Epoch16.encode</code> method except 
     * in its representation of "standard ISTP FILLVAL" and 
     * "not an epoch" values.
     *
     * @param values values to convert. May be an array of double[n][2]
     *     or a single double[2].
     * @return the String[] representation of the given value
     * @see #toString(double[])
     */
    public static String[] toString(
        Object values) {

        String[] strValues = null;     // resulting values

        Object firstElement = Array.get(values, 0);
                                       // first element of values array

        if (firstElement.getClass().isArray()) {

            strValues = new String[Array.getLength(values)];

            for (int i = 0; i < strValues.length; i++) {

                strValues[i] = toString((double[])Array.get(values, i));
            }
        }
        else {

            strValues = new String[1];
            strValues[0] = toString((double[])values);
        }

        return strValues;
    }


    /**
     * Parses the given data/time string and returns the corresponding
     * epoch value.  The recognized formats are the following:
     * <ul>
     *   <li><code>FILLVAL_STRING</code></li>
     *   <li><code>NaE_STRING</code></li>
     *   <li>{@link gsfc.spdf.cdf.Value#parseEpoch16(String)
     *        gsfc.spdf.cdf.Value.parseEpoch16(String)}</li>
     * </ul>
     *
     * @param value date/time string value to be parsed.
     * @return corresponding epoch value.
     * @throws CDFException if the given value cannot be parsed.
     */
    public static double[] fromString(String value)
        throws CDFException {

        if (value.startsWith(FILLVAL_STRING) ||
            value.equalsIgnoreCase(NaE_STRING)) {

            return new double[] {FILLVAL[0], FILLVAL[1]};
        }
        else {

            return (double[])gsfc.spdf.cdf.Value.parseEpoch16(value);
        }
    }


    /**
     * Creates an ISTP Epoch variable for the given CDF variable.
     *
     * @param var a CDF variable with the characteristics of an ISTP 
     *            epoch variable
     */
    protected Epoch16(gsfc.nssdc.cdf.Variable var) {

        super(var);
    }


    /**
     * Create a new instance of an ISTP Epoch variable in the given
     * CDF file.  The new instance has the necessary ISTP type, 
     * dimensions, attributes, and attribute values.
     *
     * @param cdf the CDF file in which the Epoch variable is to be
     *            created
     * @throws CDFException if a CDFException occurs
     * @throws ISTPComplianceException if the given characteristics fail
     *             to be ISTP compliant
     */
    public Epoch16(CDF cdf)
        throws CDFException, ISTPComplianceException {

        this(cdf, "Epoch16");
    }


    /**
     * Create a new instance of an ISTP Epoch16 variable in the given
     * CDF file.  The new instance has the necessary ISTP type, 
     * dimensions, attributes, and attribute values.
     *
     * @param cdf the CDF file in which the Epoch16 variable is to be 
     *            created
     * @param name variable name
     * @throws CDFException if a CDFException occurs
     * @throws ISTPComplianceException if the given characteristics fail
     *             to be ISTP compliant
     */
    public Epoch16(CDF cdf, String name ) 
        throws CDFException, ISTPComplianceException {

        this(cdf, name, 0, new long[] {1}, CDF.VARY);
    }
    

    /**
     * Create a new instance of an ISTP Epoch16 variable in the given
     * CDF file.  The new instance has the necessary ISTP type, 
     * dimensions, attributes, and attribute values.
     *
     * @param cdf the CDF file in which the Epoch16 variable is to be 
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
    public Epoch16(CDF cdf, String name, long numDims, long[] dimSizes,
               long recVary ) 
        throws CDFException, ISTPComplianceException {

        super(cdf, name, CDF.CDF_EPOCH16, numDims, dimSizes,
              recVary, new long[] {recVary});
        
        setAttributeValue("VALIDMIN", VALIDMIN);
        setAttributeValue("VALIDMAX", VALIDMAX);

        setUnitsValue();

        setTimeBaseValue();
        setAttributeValue("TIME_SCALE", (String)null);
        setAttributeValue("REFERENCE_POSITION", (String)null);
        setAttributeValue("ABSOLUTE_ERROR", CDF.CDF_EPOCH16, null);
        setAttributeValue("RELATIVE_ERROR", CDF.CDF_EPOCH16, null);
    }


    /**
     * Sets the UNITS attribute's value.
     *
     * @throws CDFException if an error occurs setting the attribute.
     */
    public void setUnitsValue() 
        throws CDFException {

        setUnitsValue("ps");
    }


    /**
     * Sets the TIME_BASE attribute's value.
     *
     * @throws CDFException if an error occurs setting the attribute.
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
    protected void setAttributeValue(String name, double[] value)
        throws CDFException {

        try {

            Attribute.create(var.getMyCDF(), name, CDF.VARIABLE_SCOPE);
        }
        catch (CDFException e) {

            // ignore it -- probably already exists
        }

        var.putEntry(name, CDF.CDF_EPOCH16, value);
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
     * @param msec milliseconds
     * @param usec microseconds
     * @param nsec nanoseconds
     * @param psec picoseconds
     * @throws CDFException if a CDFException occurs
     */
    public void set(long record, long year, long month, long day,
                    long hour, long minute, long second, long msec,
                    long usec, long nsec, long psec)
        throws CDFException {

        double[] value = new double[2];// new value

        gsfc.nssdc.cdf.util.Epoch16.compute(
            year, month, day, hour, minute, second, msec, usec, nsec,
            psec, value);
                                       // epoch value

        var.putRecord(record, value);
    }


    /**
     * Gets the default value.
     *
     * @return the default value
     */
    public static Object getDefaultValue() {

        double[] value = new double[VALIDMIN.length];
                                       // the default value

        for (int i = 0; i < VALIDMIN.length; i++) {

            value[i] = VALIDMIN[i];
        }

        return value;
    }


    /**
     * Gets the Date value at the specified record.  Note that a Date 
     * value is less precise than Epoch16.
     *
     * @param record identifies a specific record
     * @return epoch value from the specfied record
     * @throws CDFException if a CDFException occurs
     */
    public Date getDate(long record)
        throws CDFException {

        Object time = var.getRecord(record);
                                       // value from specified record
        long[] components =
            gsfc.nssdc.cdf.util.Epoch16.breakdown(time);
                                       // component values

        Calendar calendar = 
            GregorianCalendar.getInstance(UTC_TIME_ZONE);
                                       // calendar to create a Date

        calendar.set(Calendar.YEAR, (int)components[0]);
        calendar.set(Calendar.MONTH, (int)components[1] - 1);
        calendar.set(Calendar.DAY_OF_MONTH, (int)components[2]);
        calendar.set(Calendar.HOUR_OF_DAY, (int)components[3]);
        calendar.set(Calendar.MINUTE, (int)components[4]);
        calendar.set(Calendar.SECOND, (int)components[5]);

        if (components[9] > 500) {

            components[8]++;
        }
        if (components[8] > 500) {

            components[7]++;
        }
        if (components[7] > 500) {

            components[6]++;
        }
        calendar.set(Calendar.MILLISECOND, (int)components[6]);

        return calendar.getTime();
    }

}
