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
 * $Id: FillvalAttribute.java,v 1.14 2022/03/24 10:38:39 btharris Exp $
 */
package gsfc.spdf.istp;


import gsfc.nssdc.cdf.CDF;


/**
 * This class represents the ISTP FILLVAL variable attribute.
 *
 * @author B. Harris
 * @version $Revision: 1.14 $
 */
public class FillvalAttribute {

    /**
     * Standard value for a CDF_INT1 and CDF_BYTE variable.
     */
    public static final Byte INT1_VALUE = Byte.valueOf((byte)-128);

    /**
     * Standard value for a CDF_UINT1 variable.
     */
    public static final Short UINT1_VALUE = Short.valueOf((short)255);

    /**
     * Standard value for a CDF_INT2 variable.
     */
    public static final Short INT2_VALUE = Short.valueOf((short)-32768);

    /**
     * Standard value for a CDF_UINT2 variable.
     */
    public static final Integer UINT2_VALUE = Integer.valueOf(65535);

    /**
     * Standard value for a CDF_INT4 variable.
     */
    public static final Integer INT4_VALUE = Integer.valueOf(-2147483648);

    /**
     * Standard value for a CDF_UINT4 variable.
     */
    public static final Long UINT4_VALUE = Long.valueOf(4294967295L);

    /**
     * Standard value for a CDF_INT8 variable.
     */
    public static final Long INT8_VALUE = Long.valueOf(Long.MIN_VALUE);

    /**
     * Traditional (before NaN values) standard value for a CDF_REAL4 
     * and CDF_FLOAT variable.
     */
    public static final Float REAL4_VALUE0 = Float.valueOf(-1.0E31F);

    /**
     * Standard NaN value for a CDF_REAL4 and CDF_FLOAT variable.  
     * Note: be careful checking for equality of this value since <code>
     * Float.NaN != Float.NaN</code>.  See <code>
     * java.lang.Float.equals()</code> for more details.
     */
    public static final Float REAL4_VALUE1 = Float.valueOf(Float.NaN);

    /**
     * Traditional (before NaN values) standard value for a CDF_REAL8 
     * and CDF_DOUBLE variable.
     */
    public static final Double REAL8_VALUE0 = Double.valueOf(-1.0E31);

    /**
     * Standard NaN value for a CDF_REAL8 and CDF_DOUBLE variable.  
     * Note: be careful checking for equality of this value since <code>
     * Double.NaN != Double.NaN</code>.  See <code>
     * java.lang.Double.equals()</code> for more details.
     */
    public static final Double REAL8_VALUE1 = Double.valueOf(Double.NaN);

    /**
     * Standard value for a CDF_EPOCH variable.
     */
    public static final Double EPOCH_VALUE = Epoch8.FILLVAL;

    /**
     * Standard value for a CDF_EPOCH16 variable.
     */
    static final double[] EPOCH16_VALUE = Epoch16.FILLVAL;

    /**
     * Standard value for a CDF_TIME_TT2000 variable.
     */
    public static final Long TERRESTRIAL_TIME_2000_VALUE = 
        TerrestrialTime2000.FILLVAL;

    /**
     * Flag indicating whether to use traditional ISTP FILLVAL values or
     * the newer NaN values.
     */
    private static boolean useNaNValues = false;



    /**
     * Gets the value of the flag indicating whether NaN values are to 
     * be used instead of traditional ISTP FILLVAL values.
     *
     * @return true if NaN values are to be used or false if only
     *             traditional FILLVAL values are to be used
     * @see #setUseNaNValues
     */
    public static boolean getUseNaNValues() {

        return useNaNValues;
    }


    /**
     * Sets the value of the flag indicating whether NaN values are to 
     * be used instead of traditional ISTP FILLVAL values.
     *
     * @param value true if NaN values are to be used or false if only
     *             traditional FILLVAL values are to be used
     * @see #getUseNaNValues
     */
    public static void setUseNaNValues(boolean value) {

        useNaNValues = value;
    }


    /**
     * Gets the FILLVAL attribute value for the specified CDF data type.
     * There is no FILLVAL value defined for CDF_CHAR and CDF_UCHAR data
     * types so null is returned for those cases.
     *
     * @param cdfType CDF data type
     * @return FILLVAL attribute value or null if no FILLVAL value is
     *             defined for the given data type.
     */
    public static Object getStandardValue(long cdfType) {

        if (cdfType == CDF.CDF_BYTE || cdfType == CDF.CDF_INT1) {

            return INT1_VALUE;
        }
        else if (cdfType == CDF.CDF_UINT1) {

            return UINT1_VALUE;
        }
        else if (cdfType == CDF.CDF_INT2) {

            return INT2_VALUE;
        }
        else if (cdfType == CDF.CDF_UINT2) {

            return UINT2_VALUE;
        }
        else if (cdfType == CDF.CDF_INT4) {

            return INT4_VALUE;
        }
        else if (cdfType == CDF.CDF_UINT4) {

            return UINT4_VALUE;
        }
        else if (cdfType == CDF.CDF_INT8) {

            return INT8_VALUE;
        }
        else if (cdfType == CDF.CDF_FLOAT || 
                 cdfType == CDF.CDF_REAL4) {

            if (useNaNValues) {

                return REAL4_VALUE1;
            }
            else {

                return REAL4_VALUE0;
            }
        }
        else if (cdfType == CDF.CDF_DOUBLE || 
                 cdfType == CDF.CDF_REAL8) {

            if (useNaNValues) {

                return REAL8_VALUE1;
            }
            else {

                return REAL8_VALUE0;
            }
        }
        else if (cdfType == CDF.CDF_TIME_TT2000) {

            return TERRESTRIAL_TIME_2000_VALUE;
        }
        else if (cdfType == CDF.CDF_EPOCH) {

            return EPOCH_VALUE;
        }
        else if (cdfType == CDF.CDF_EPOCH16) {

            double[] epoch16Value = new double[EPOCH16_VALUE.length];
                                       // copy of EPOCH16_VALUE

            for (int i = 0; i < epoch16Value.length; i++) {

                epoch16Value[i] = EPOCH16_VALUE[i];
            }
            return epoch16Value;
        }
        else {

            return null;
        }
    }


    /**
     * Returns a string representation of the given value.
     *
     * @param cdfType CDF data type
     * @param value the value to be converted
     * @return a string representation of the given value.
     */
    public static String toString(long cdfType, Object value) {

        if (value instanceof Byte) {

            return ((Byte)value).toString();
        }
        else if (value instanceof Short) {

            return ((Short)value).toString();
        }
        else if (value instanceof Integer) {

            return ((Integer)value).toString();
        }
        else if (value instanceof Long) {

            if (cdfType == CDF.CDF_TIME_TT2000) {

                return TerrestrialTime2000.toString((Long)value);
            }
            else {

                return ((Long)value).toString();
            }
        }
        else if (value instanceof Float) {

            return ((Float)value).toString();
        }
        else if (value instanceof Double) {

            if (cdfType == CDF.CDF_EPOCH) {

                return Epoch8.toString((Double)value);
            }
            else {

                return ((Double)value).toString();
            }
        }
        else if (value instanceof double[] &&
                 cdfType == CDF.CDF_EPOCH16) {

            return Epoch16.toString((double[])value);
        }
        else {

            return null;
        }
    }


    /**
     * Determines whether the given CDF datatype and value are valid
     * for a FILLVAL attribute value.
     *
     * @param cdfType CDF datatype
     * @param value value to verify
     * @return true if the given value is the correct value for the
     *             specified datatype.  Otherwise false.
     */
    public static boolean isValid(long cdfType, Object value) {

        if (value instanceof Number) {

            Number numValue = (Number)value;
                                       // value recast as a Number

            if ((cdfType == CDF.CDF_BYTE || cdfType == CDF.CDF_INT1) &&
                numValue.equals(INT1_VALUE)) {

                return true;
            }
            else if (cdfType == CDF.CDF_UINT1 && 
                     numValue.equals(UINT1_VALUE)) {

                return true;
            }
            else if (cdfType == CDF.CDF_INT2 &&
                     numValue.equals(INT2_VALUE)) {

                return true;
            }
            else if (cdfType == CDF.CDF_UINT2 &&
                     numValue.equals(UINT2_VALUE)) {

                return true;
            }
            else if (cdfType == CDF.CDF_INT4 &&
                     numValue.equals(INT4_VALUE)) {

                return true;
            }
            else if (cdfType == CDF.CDF_UINT4 &&
                     numValue.equals(UINT4_VALUE)) {

                return true;
            }
            else if (cdfType == CDF.CDF_INT8 &&
                     numValue.equals(INT8_VALUE)) {

                return true;
            }
            else if (cdfType == CDF.CDF_FLOAT || 
                     cdfType == CDF.CDF_REAL4) {

                if (useNaNValues && numValue.equals(REAL4_VALUE1)) {

                    return true;
                }
                else if (!useNaNValues && 
                         numValue.equals(REAL4_VALUE0)) {

                    return true;
                }
                else {

                    return false;
                }
            }
            else if (cdfType == CDF.CDF_DOUBLE || 
                     cdfType == CDF.CDF_REAL8) {

                if (useNaNValues && numValue.equals(REAL8_VALUE1)) {

                    return true;
                }
                else if (!useNaNValues && numValue.equals(REAL8_VALUE0)){

                    return true;
                }
                else {

                    return false;
                }
            }
            else if (cdfType == CDF.CDF_EPOCH &&
                     numValue.equals(EPOCH_VALUE)) {

                return true;
            }
            else if (cdfType == CDF.CDF_TIME_TT2000 &&
                     numValue.equals(TERRESTRIAL_TIME_2000_VALUE)) {

                return true;
            }
            else {

                return false;
            }
        }
        else if (value instanceof double[]) {

            double[] doubleValue = (double[])value;
                                       // value recast as a double[]

            if (cdfType == CDF.CDF_EPOCH16 && 
                doubleValue.length == EPOCH16_VALUE.length) {

                for (int i = 0; i < EPOCH16_VALUE.length; i++) {
            
                    if (doubleValue[i] != EPOCH16_VALUE[i]) {

                        return false;
                    }
                }

                return true;
            }
            else if (cdfType == CDF.CDF_REAL8 || 
                     cdfType == CDF.CDF_FLOAT) {

                for (int i = 0; i < doubleValue.length; i++) {

                    Double doubleObject = new Double(doubleValue[i]);
                                       // object representation of 
                                       //  double[i] to let 
                                       //  Double.equals() deal with 
                                       //  the special case of NaN 
                                       //  values
                                          
                    if ((useNaNValues && 
//                         !doubleObject.equals(REAL8_VALUE1)) ||
                         !doubleObject.isNaN()) ||
                        (!useNaNValues && 
                         !doubleObject.equals(REAL8_VALUE0))) {

                        return false;
                    }
                }

                return true;
            }
            else {

                return false;
            }
        }
        else if (value instanceof String || value instanceof String[]) {

            //
            // FILLVAL values are not appliciable to String values so
            // anything is alright.
            //
            return true;
        }
        else {

            return false;
        }
    }
}
