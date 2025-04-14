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
 * Copyright (c) 2012-2022 United States Government as represented by 
 * the National Aeronautics and Space Administration. No copyright is 
 * claimed in the United States under Title 17, U.S.Code. All Other 
 * Rights Reserved.
 *
 * $Id: Value.java,v 1.4 2022/03/24 10:38:18 btharris Exp $
 */
package gsfc.spdf.cdf;


import java.lang.reflect.Array;

import gsfc.nssdc.cdf.CDF;
import gsfc.nssdc.cdf.CDFException;
import gsfc.nssdc.cdf.util.Epoch;
import gsfc.nssdc.cdf.util.Epoch16;
import gsfc.nssdc.cdf.util.CDFTT2000;


/**
 * This class represents a CDF variable/attribute's value.
 */
public class Value {


    /**
     * Returns a String representation of the given Object's value.
     *
     * @param value Object value.
     * @param dataType CDF data type.
     * @return the String representation for the specified Object value
     *     and CDF data type.  null if no value is defined.
     */
    public static String toString(Object value, long dataType) {

        if (dataType == CDF.CDF_BYTE ||
            dataType == CDF.CDF_INT1) {

            return ((Byte)value).toString();
        }
        else if (dataType == CDF.CDF_CHAR ||
                 dataType == CDF.CDF_UCHAR) {

            return (String)value;
        }
        else if (dataType == CDF.CDF_UINT1 ||
                 dataType == CDF.CDF_INT2) {

            return ((Short)value).toString();
        }
        else if (dataType == CDF.CDF_UINT2 ||
                 dataType == CDF.CDF_INT4) {

            return ((Integer)value).toString();
        }
        else if (dataType == CDF.CDF_UINT4 ||
                 dataType == CDF.CDF_INT8) {

            return ((Long)value).toString();
        }
        else if (dataType == CDF.CDF_REAL4 ||
                 dataType == CDF.CDF_FLOAT) {

            return ((Float)value).toString();
        }
        else if (dataType == CDF.CDF_REAL8 ||
                 dataType == CDF.CDF_DOUBLE) {

            return ((Double)value).toString();
        }
        else if (dataType == CDF.CDF_EPOCH) {

            return Epoch.encode(((Double)value).doubleValue());
        }
        else if (dataType == CDF.CDF_EPOCH16) {

            return Epoch16.encode(value);
        }
        else if (dataType == CDF.CDF_TIME_TT2000) {

            return CDFTT2000.toUTCstring((Long)value);
        }

        return null;
    }


    /**
     * Returns a NaN value for the given CDF data type.
     *
     * @param dataType CDF data type.
     * @return the NaN value for the given CDF data type.  null if 
     *     no NaN value is defined for the given data type.
     */
    public static Object getNaN(long dataType) {

        if (dataType == CDF.CDF_REAL4 ||
            dataType == CDF.CDF_FLOAT) {

            return Float.valueOf(Float.NaN);
        }
        else if (dataType == CDF.CDF_REAL8 ||
                 dataType == CDF.CDF_DOUBLE ||
                 dataType == CDF.CDF_EPOCH) {

            return Double.valueOf(Double.NaN);
        }
        else if (dataType == CDF.CDF_EPOCH16) {

            return new Double[] {
                           Double.valueOf(Double.NaN),
                           Double.valueOf(Double.NaN)};
        }

        return null;
    }


    /**
     * Returns a hexadecimal String representation of the given 
     * Object's value when the dataType is numeric.
     *
     * @param value Object value.
     * @param dataType CDF data type.
     * @return a hexadecimal String representation for the specified 
     *     Object value and CDF data type.  null if no value is defined.
     */
    public static String toHexString(Object value, long dataType) {

        if (dataType == CDF.CDF_BYTE ||
            dataType == CDF.CDF_INT1) {

            return Integer.toHexString(((Byte)value).intValue());
        }
        else if (dataType == CDF.CDF_CHAR ||
                 dataType == CDF.CDF_UCHAR) {

            byte[] byteValues = ((String)value).getBytes();
                                       // byte values
            StringBuilder strBuilder = new StringBuilder();
                                       // String builder
            for (byte byteValue : byteValues) {

                strBuilder.append(Integer.toHexString(byteValue) + " ");
            }
            String strValue = strBuilder.toString();
                                       // String representation of hex 
                                       // value
            return strValue.substring(0, strValue.length() - 1);
        }
        else if (dataType == CDF.CDF_UINT1 ||
                 dataType == CDF.CDF_INT2) {

            return Integer.toHexString(((Short)value).intValue());
        }
        else if (dataType == CDF.CDF_UINT2 ||
                 dataType == CDF.CDF_INT4) {

            return Integer.toHexString(((Integer)value).intValue());
        }
        else if (dataType == CDF.CDF_UINT4 ||
                 dataType == CDF.CDF_INT8 ||
                 dataType == CDF.CDF_TIME_TT2000) {

            return Long.toHexString(((Long)value).longValue());
        }
        else if (dataType == CDF.CDF_REAL4 ||
                 dataType == CDF.CDF_FLOAT) {

            return Float.toHexString(((Float)value).floatValue());
        }
        else if (dataType == CDF.CDF_REAL8 ||
                 dataType == CDF.CDF_DOUBLE ||
                 dataType == CDF.CDF_EPOCH) {

            return Double.toHexString(((Double)value).doubleValue());
        }
        else if (dataType == CDF.CDF_EPOCH16) {

            return Double.toHexString(Array.getDouble(value, 0)) + " " +
                   Double.toHexString(Array.getDouble(value, 1));
        }

        return null;
    }


    /**
     * Decodes the given String value into the corresponding Object
     * value based upon the given CDF data type value.
     *
     * @param value String value to decode.
     * @param dataType CDF data type of value.
     * @return Object value corresponding to the given String value
     *             and CDF data type.
     * @throws NumberFormatException if a format error is encountered 
     *             in the given value.
     */
    public static Object decode(String value, long dataType) 
        throws NumberFormatException {

        if (dataType == CDF.CDF_BYTE ||
            dataType == CDF.CDF_INT1) {

            return Byte.decode(value);
        }
        else if (dataType == CDF.CDF_CHAR ||
                 dataType == CDF.CDF_UCHAR) {

            return value;
        }
        else if (dataType == CDF.CDF_UINT1 ||
                 dataType == CDF.CDF_INT2) {

            return Short.decode(value);
        }
        else if (dataType == CDF.CDF_UINT2 ||
                 dataType == CDF.CDF_INT4) {

            return Integer.decode(value);
        }
        else if (dataType == CDF.CDF_UINT4 ||
                 dataType == CDF.CDF_INT8) {

            return Long.decode(value);
        }
        else if (dataType == CDF.CDF_REAL4 ||
                 dataType == CDF.CDF_FLOAT) {

            return Float.parseFloat(value);
        }
        else if (dataType == CDF.CDF_REAL8 ||
                 dataType == CDF.CDF_DOUBLE) {

            return Double.parseDouble(value);
        }
        else {

            try {

                if (dataType == CDF.CDF_EPOCH) {

                    return parseEpoch(value);
                }
                else if (dataType == CDF.CDF_EPOCH16) {

                    return parseEpoch16(value);
                }
                else if (dataType == CDF.CDF_TIME_TT2000) {

                    return parseTT2000(value);
                }
            }
            catch (CDFException e) {

                throw new NumberFormatException("Invalid Time format");
            }
        }

        return null;
    }


    /**
     * Parses the given string value for a date/time value which is 
     * returned as an Epoch Object.  The recognized formats are any
     * formats recognized by any of the following:
     * <ul>
     *   <li>{@link gsfc.nssdc.cdf.util.Epoch#parse(String)
     *        gsfc.nssdc.cdf.util.Epoch.parse(String)}</li>
     *   <li>{@link gsfc.nssdc.cdf.util.Epoch#parse1(String)
     *        gsfc.nssdc.cdf.util.Epoch.parse1(String)}</li>
     *   <li>{@link gsfc.nssdc.cdf.util.Epoch#parse2(String)
     *        gsfc.nssdc.cdf.util.Epoch.parse2(String)}</li>
     *   <li>{@link gsfc.nssdc.cdf.util.Epoch#parse3(String)
     *        gsfc.nssdc.cdf.util.Epoch.parse3(String)}</li>
     *   <li>{@link gsfc.nssdc.cdf.util.Epoch#parse4(String)
     *        gsfc.nssdc.cdf.util.Epoch.parse4(String)}</li>
     * </ul>
     *
     * @param value the String value to parse.
     * @return Double representation of the given date/time value.
     * @throws CDFException if a format error is encountered 
     *             in the given value.
     */
    public static Double parseEpoch(String value)
        throws CDFException {

        double epochValue = 0.0;       // parsed epoch value

        try {

            epochValue = Epoch.parse(value);
        }
        catch (CDFException e) {

            try {

                epochValue = Epoch.parse1(value);
            }
            catch (CDFException e1) {

                try {

                    epochValue = Epoch.parse2(value);
                }
                catch (CDFException e2) {

                    try {

                        epochValue = Epoch.parse3(value);
                    }
                    catch (CDFException e3) {

                        epochValue = Epoch.parse4(value);
                    }
                }
            }
        }

        return new Double(epochValue);
    }


    /**
     * Parses the given string value for a date/time value which is 
     * returned as an Epoch16 Object.  The recognized formats are any
     * formats recognized by any of the following:
     * <ul>
     *   <li>{@link gsfc.nssdc.cdf.util.Epoch16#parse(String)
     *        gsfc.nssdc.cdf.util.Epoch16.parse(String)}</li>
     *   <li>{@link gsfc.nssdc.cdf.util.Epoch16#parse1(String)
     *        gsfc.nssdc.cdf.util.Epoch16.parse1(String)}</li>
     *   <li>{@link gsfc.nssdc.cdf.util.Epoch16#parse2(String)
     *        gsfc.nssdc.cdf.util.Epoch16.parse2(String)}</li>
     *   <li>{@link gsfc.nssdc.cdf.util.Epoch16#parse3(String)
     *        gsfc.nssdc.cdf.util.Epoch16.parse3(String)}</li>
     *   <li>{@link gsfc.nssdc.cdf.util.Epoch16#parse4(String)
     *        gsfc.nssdc.cdf.util.Epoch16.parse4(String)}</li>
     * </ul>
     *
     * @param value the String value to parse.
     * @return Object representation of the given date/time value.
     * @throws CDFException if a format error is encountered in
     *             the given value.
     */
    public static Object parseEpoch16(String value)
        throws CDFException {

        Object epochValue = null;      // parsed epoch16 value

        try {

            epochValue = Epoch16.parse(value);
        }
        catch (CDFException e) {

            try {

                epochValue = Epoch16.parse1(value);
            }
            catch (CDFException e1) {

                try {

                    epochValue = Epoch16.parse2(value);
                }
                catch (CDFException e2) {

                    try {

                        epochValue = Epoch16.parse3(value);
                    }
                    catch (CDFException e3) {

                        epochValue = Epoch16.parse4(value);
                    }
                }
            }
        }

        return epochValue;
    }


    /**
     * Parses the given string value for a date/time value which is 
     * returned as a TT2000 value (nanoseconds since J2000).  The 
     * recognized formats are any formats recognized by any of the 
     * following:
     * <ul>
     *   <li>{@link gsfc.nssdc.cdf.util.CDFTT2000#parse(String)
     *        gsfc.nssdc.cdf.util.CDFTT2000.parse(String)}</li>
     * </ul>
     *
     * @param value the String value to parse.
     * @return Long representation of the given date/time value.
     * @throws CDFException if a format error is encountered in
     *             the given value.
     */
    public static Long parseTT2000(String value)
        throws CDFException {

        return new Long(CDFTT2000.parse(value));
    }

}
