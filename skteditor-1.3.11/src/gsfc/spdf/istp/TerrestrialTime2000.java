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
 * $Id: TerrestrialTime2000.java,v 1.11 2022/08/01 16:04:38 btharris Exp $
 */
package gsfc.spdf.istp;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import java.lang.reflect.Array;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

import gsfc.spdf.cdf.Value;
import gsfc.spdf.cdf.LeapSecondsSource;

import gsfc.nssdc.cdf.Attribute;
import gsfc.nssdc.cdf.CDF;
import gsfc.nssdc.cdf.CDFException;
import gsfc.nssdc.cdf.util.CDFTT2000;


/**
 * This class represents an ISTP Terrestrial Time with a fixed time 
 * base of J2000 variable.
 * 
 * @author B. Harris
 * @version $Revision: 1.11 $
 */
public class TerrestrialTime2000
    extends Epoch {


    /**
     * ISTP VALIDMIN value.
     */
    public final static Long VALIDMIN;

    /**
     * ISTP VALIDMAX value.
     */
    public final static Long VALIDMAX;

    /**
     * ISTP FILLVAL value.
     */
    public final static Long FILLVAL = new Long(Long.MIN_VALUE);

    /**
     * The CDF minimum epoch value.
     */
    public final static Long MIN;

    /**
     * gsfc.nssdc.cdf.CDFTT2000.toUTCstring() format string value of 
     * the CDF minimum epoch value.
     */
    public final static String MIN_STRING;

    /**
     * The CDF maximum epoch value.
     */
    public final static Long MAX;

    /**
     * gsfc.nssdc.cdf.CDFTT2000.toUTCstring() format string value of 
     * the CDF maximum epoch value.
     */
    public final static String MAX_STRING;


    static {

        long validMin = 0;             // temporary valid min value
        long validMax = 0;             // temporary valid max value
        long min = 0;                  // temporary min value
        long max = 0;                  // temporary max value

        try {

            validMin =
                gsfc.nssdc.cdf.util.CDFTT2000.fromUTCparts(
                    1990, 1, 1, 0, 0, 0, 0);
            validMax =
                gsfc.nssdc.cdf.util.CDFTT2000.fromUTCparts(
                    2029, 12, 31, 23, 59, 59, 999);
            min = gsfc.nssdc.cdf.util.CDFTT2000.fromUTCparts(
                    1708, 1, 1, 0, 0, 0, 0);
            max = gsfc.nssdc.cdf.util.CDFTT2000.fromUTCparts(
                    2291, 12, 31, 23, 59, 59, 998);
        }
        catch (CDFException e) {

            System.err.println(
                "gsfc.spdf.istp.TerrestrialTime2000: CDFException " +
                e.getMessage() + " while creating VALIDMIN/MAX");
        }

        VALIDMIN = Long.valueOf(validMin);
        VALIDMAX = Long.valueOf(validMax);
        MIN = Long.valueOf(min);
        MAX = Long.valueOf(max);
        MIN_STRING = gsfc.nssdc.cdf.util.CDFTT2000.toUTCstring(MIN, 0);
        MAX_STRING = gsfc.nssdc.cdf.util.CDFTT2000.toUTCstring(MAX, 0);
    }


    /**
     * Determines whether the given value is outside the range of valid
     * CDF epoch values.  Note that the range of valid ISTP epoch values
     * is smaller.
     *
     * @param value value to check
     * @return true if the given value is not a valid epoch value
     */
    public static boolean isNotAnEpoch(Long value) {

        return value.compareTo(MIN) < 0 || value.compareTo(MAX) > 0;
    }


    /**
     * Determines whether the given value is equal to the ISTP FILLVAL.
     *
     * @param value value to be tested
     * @return true if the given value is equal to FILLVAL, otherwise
     *     false
     */
    public static boolean isFillval(Long value) {

        return value.compareTo(FILLVAL) == 0;
    }


    /**
     * Returns the String representation of the given TT2000 value.  
     * This method is similar to the 
     * <code>gsfc.nssdc.cdf.util.Epoch.encode</code> method except 
     * in its representation of "standard ISTP FILLVAL" and 
     * "not an epoch" values.
     *
     * @param value value to convert
     * @return the String representation of the given value
     * @see #toString(Object)
     */
    public static String toString(Long value) {

        if (isFillval(value)) {

            return FILLVAL_STRING + " (" + value.toString() + ")";
        }
        else if (isNotAnEpoch(value)) {

            return value.toString();
        }
        else {

            return gsfc.nssdc.cdf.util.CDFTT2000.toUTCstring(value, 0);
        }
    }


    /**
     * Returns the String representation of the given TT2000 value(s).  
     * This method is similar to the 
     * <code>gsfc.nssdc.cdf.util.Epoch.encode</code> method except 
     * in its representation of "standard ISTP FILLVAL" and 
     * "not an epoch" values.
     *
     * @param values values to convert.  May be an array of Long or a
     *     single Long value.
     * @return the String[] representation of the given value.
     * @see #toString(Long)
     */
    public static String[] toString(
        Object values) {

        String[] strValues = null;     // resulting values

        if (values.getClass().isArray()) {

            strValues = new String[Array.getLength(values)];

            for (int i = 0; i < strValues.length; i++) {

                strValues[i] = toString(Array.getLong(values, i));
            }
        }
        else {

            strValues = new String[1];
            strValues[0] = toString((Long)values);
        }

        return strValues;
    }


    /**
     * Returns the Long representation of the given epoch value.  
     * This method is similar to the 
     * {@link gsfc.nssdc.cdf.util.CDFTT2000#fromUTCstring(String)} 
     * method except it return a <code>Long</code> instead of a 
     * <code>long</code> value and handles ISTP FILLVAL values.
     *
     * @param value value to convert
     * @return the Long representation of the given value
     * @throws CDFException if one or more of the date/time fields
     *             is out of the valid range.
     */
    public static Long fromString(String value) 
        throws CDFException {

        if (value.startsWith(FILLVAL_STRING) ||
            value.equalsIgnoreCase(NaE_STRING)) {

            return FILLVAL;
        }
        else if (value.length() == 0) {

            // An alpha version of CDF 3.3.2 required this case for 
            // consistency with gsfc.nssdc.cdf.util.Epoch.parse.
            // But CDF 3.3.2 is suppose to be fixed to throw an
            // exception itself.  So this may be deleted for the
            // final 3.3.2.

            throw new CDFException("One or more of the date/time " +
                "fields is out of valid range.");
        }
        else {

            return Value.parseTT2000(value);
        }
    }


    /**
     * Creates an ISTP TerrestrialTime2000 variable for the given CDF 
     * variable.
     *
     * @param var a CDF variable with the characteristics of an ISTP 
     *            epoch variable
     */
    protected TerrestrialTime2000(gsfc.nssdc.cdf.Variable var) {

        super(var);
    }


    /**
     * Create a new instance of an ISTP TerrestrialTime2000 variable 
     * in the given CDF file.  The new instance has the necessary 
     * ISTP type, dimensions, attributes, and attribute values.
     *
     * @param cdf the CDF file in which the TerrestrialTime2000 
     *            variable is to be created
     * @throws CDFException if a CDFException occurs
     * @throws ISTPComplianceException if the given characteristics fail
     *             to be ISTP compliant
     */
    public TerrestrialTime2000(CDF cdf) 
        throws CDFException, ISTPComplianceException {

        this(cdf, "Epoch");
    }
    

    /**
     * Create a new instance of an ISTP TerrestrialTime2000 variable 
     * in the given CDF file.  The new instance has the necessary ISTP 
     * type, dimensions, attributes, and attribute values.
     *
     * @param cdf the CDF file in which the TerrestrialTime2000 
     *            variable is to be created
     * @param name variable name
     * @throws CDFException if a CDFException occurs
     * @throws ISTPComplianceException if the given characteristics fail
     *             to be ISTP compliant
     */
    public TerrestrialTime2000(CDF cdf, String name ) 
        throws CDFException, ISTPComplianceException {

        this(cdf, name, 0, new long[] {1}, CDF.VARY);
    }


    /**
     * Create a new instance of an ISTP TerrestrialTime2000 variable 
     * in the given CDF file.  The new instance has the necessary ISTP 
     * type, dimensions, attributes, and attribute values.
     *
     * @param cdf the CDF file in which the TerrestrialTime2000 
     *            variable is to be created
     * @param name variable name
     * @param numDims dimensionality of this variable
     * @param dimSizes dimension sizes.  An array of length numDims
     *            indicating the size of each dimension
     * @param recVary record variance (VARY or NOVARY)
     * @throws CDFException if a CDFException occurs
     * @throws ISTPComplianceException if the given characteristics fail
     *             to be ISTP compliant
     */
    public TerrestrialTime2000(CDF cdf, String name, long numDims,
               long[] dimSizes, long recVary ) 
        throws CDFException, ISTPComplianceException {

        super(cdf, name, CDF.CDF_TIME_TT2000, numDims, dimSizes,
              recVary, new long[] {recVary});
        
        setAttributeValue("VALIDMIN", VALIDMIN);
        setAttributeValue("VALIDMAX", VALIDMAX);

        setUnitsValue();

        setTimeBaseValue();
        setAttributeValue("TIME_SCALE", "Terrestrial Time");
        setAttributeValue("REFERENCE_POSITION", "Rotating Earth Geoid");
        setAttributeValue("ABSOLUTE_ERROR", CDF.CDF_TIME_TT2000, null);
        setAttributeValue("RELATIVE_ERROR", CDF.CDF_TIME_TT2000, null);
    }


    /**
     * Sets the UNITS attribute's value.
     */
    public void setUnitsValue() 
        throws CDFException {

        setUnitsValue("ns");
    }


    /**
     * Sets the TIME_BASE attribute's value.
     */
    public void setTimeBaseValue() 
        throws CDFException {

        try {

            setAttributeValue("TIME_BASE", "J2000");
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
    protected void setAttributeValue(String name, Long value)
        throws CDFException {

        try {

            Attribute.create(var.getMyCDF(), name, CDF.VARIABLE_SCOPE);
        }
        catch (CDFException e) {

            // ignore it -- probably already exists
        }

        var.putEntry(name, CDF.CDF_TIME_TT2000, value);
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

        long time =
            gsfc.nssdc.cdf.util.CDFTT2000.fromUTCparts(
                year, month, day, hour, minute, second, msec);
                                       // epoch value

        var.putRecord(record, Long.valueOf(time));

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

        Calendar calendar = 
            GregorianCalendar.getInstance(UTC_TIME_ZONE);
                                       // calendar to intepret Date
        calendar.setTime(value);

        set(record, calendar.get(Calendar.YEAR), 
            calendar.get(Calendar.MONTH), 
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.HOUR),
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

        Long time = (Long)var.getRecord(record);
                                       // Long representation of CDF 
                                       // TT2000
        long[] components = 
            gsfc.nssdc.cdf.util.CDFTT2000.toUTCparts(time.longValue());
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
        calendar.set(Calendar.MILLISECOND, (int)components[6]);

        return calendar.getTime();
    }


    /**
     * Gets the default value.
     *
     * @return the default value
     */
    public static Object getDefaultValue() {

        return VALIDMIN;
    }


    /**
     * This method returns the source of leap seconds information
     *
     * @return LeapSecondsSource.
     */
    public static LeapSecondsSource getLeapSecondsSource() {

        return gsfc.spdf.cdf.TerrestrialTime2000.getLeapSecondsSource();
    }


    /**
     * This method returns the leap seconds table.
     *
     * @return The table contents of the leap seconds.
     */
    public static double[][] getLeapSecondsTable() {

        return gsfc.spdf.cdf.TerrestrialTime2000.getLeapSecondsTable();
    }


    /**
     * This method returns the number of entries in the leap 
     * seconds table.
     *
     * @return The entry count in the leap seconds table.
     */
    public static int getRowsInLeapSecondsTable() {

        return gsfc.spdf.cdf.TerrestrialTime2000.
                   getRowsInLeapSecondsTable();
    }
    

    /**
     * This method returns the last UTC date that a leap second was 
     * added in the leap second table used in the class. This can be 
     * used to check whether the table is up-to-date. 
     * 
     * @return the last UTC date that a leap second was added in the
     *    leap second table.
     */
    public static long[] getLastDateInLeapSecondsTable() {

        return CDFTT2000.CDFgetLastDateinLeapSecondsTable();
    }


    /**
     * This method returns the last UTC date (as a String) that a 
     * leap second was added in the leap second table used in the 
     * class. This can be used to check whether the table is 
     * up-to-date. 
     * 
     * @return the last UTC date that a leap second was added in the
     *    leap second table.
     */
    public static String getLastDateInLeapSecondsTableAsString() {

        long[] libLastDateInLeapSecondsTable = 
            getLastDateInLeapSecondsTable();

        try {

            ByteArrayOutputStream libLastLeapSecondDate =
                new ByteArrayOutputStream();
                                       // CDF library's last leap
                                       // second date
            PrintStream libLastLeapSecondDateStrm =
                new PrintStream(libLastLeapSecondDate, true, 
                                "US-ASCII");
                                       // PrintStream for formating
                                       // library's last leap second
                                       // date
            libLastLeapSecondDateStrm.format(
                "%04d-%02d-%02d",
                libLastDateInLeapSecondsTable[0],
                libLastDateInLeapSecondsTable[1],
                libLastDateInLeapSecondsTable[2]);

            return libLastLeapSecondDate.toString("US-ASCII");
        }
        catch (UnsupportedEncodingException e) {

            // US-ASCII is required so this cannot happen
            return e.getMessage();
        }
    }

}
