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
 * Copyright (c) 2014-2022 United States Government as represented by 
 * the National Aeronautics and Space Administration. No copyright is 
 * claimed in the United States under Title 17, U.S.Code. All Other 
 * Rights Reserved.
 *
 * $Id: DataType.java,v 1.5 2022/08/01 16:58:25 btharris Exp $
 */
package gsfc.spdf.cdf;


import gsfc.nssdc.cdf.CDF;
import gsfc.nssdc.cdf.util.CDFUtils;


/**
 * This class represents a CDF variable/attribute entry's data-type.
 */
public class DataType {


    /**
     * Gets the CDF datatype corresponding to the given Java object.
     *
     * @param o object whose CDF datatype is to be determined.
     * @return CDF datatype corresponding to the Java datatype of the
     *     given object.
     * @throws IllegalArgumentException if the given object has no
     *     corresponding CDF datatype.
     */
    public static long getDataType(Object o) 
        throws IllegalArgumentException {

        if (o instanceof Boolean) {

            return CDF.CDF_BYTE;
        }
        else if (o instanceof Byte) {

            return CDF.CDF_BYTE;
        }
        else if (o instanceof Double) {

            return CDF.CDF_DOUBLE;
        }
        else if (o instanceof Float) {

            return CDF.CDF_FLOAT;
        }
        else if (o instanceof Integer) {

            return CDF.CDF_INT4;
        }
        else if (o instanceof Long) {

            return CDF.CDF_INT8;
        }
        else if (o instanceof Short) {

            return CDF.CDF_INT2;
        }
        else if (o instanceof String) {

            return CDF.CDF_CHAR;
        }
        else {

            throw new IllegalArgumentException(
                "Unknow CDF datatype for " + o.getClass().getName());
        }
    }


    /**
     * Gets the string representation of the given CDF data type.
     *
     * @param dataType CDF data type.
     * @return the String representation of dataType.  "UNKNOWN" is 
     *     returned if invalid cdfDataType is given.
     * @see #getFromString(String) getFromString.
     */
    public static String getString(long dataType) {

        return CDFUtils.getStringDataType(dataType);
    }


    /**
     * Gets the long representation of the given string represenation 
     * of a CDF data type.  This is the reverse of 
     * @see #getString(long) getString.
     *
     * @param dataType string represenation of a CDF data type.
     * @return long represenation of dataType.  -1 is returned if 
     *     invalid dataType value is given.
     * @see #getString(long) getString.
     */
    public static long getFromString(String dataType) {

        return CDFUtils.getDataTypeValue(dataType);
    }


    /**
     * Indicates whether the given CDF data type is numeric.
     *
     * @param dataType CDF data type to test.
     * @return true if the given type is numeric.  Otherwise, false.
     */
    public static boolean isNumeric(
        long dataType) {

        return dataType == CDF.CDF_INT2 ||
               dataType == CDF.CDF_UINT2 ||
               dataType == CDF.CDF_INT4 ||
               dataType == CDF.CDF_UINT4 ||
               dataType == CDF.CDF_INT8 ||
               dataType == CDF.CDF_FLOAT ||
               dataType == CDF.CDF_REAL4 ||
               dataType == CDF.CDF_DOUBLE ||
               dataType == CDF.CDF_REAL8 ||
               dataType == CDF.CDF_INT1 ||
               dataType == CDF.CDF_UINT1 ||
               dataType == CDF.CDF_BYTE;
    }


    /**
     * Indicates whether the given CDF data type is character.
     *
     * @param dataType CDF data type to test.
     * @return true if the given type is character.  Otherwise, false.
     */
    public static boolean isCharacter(
        long dataType) {

        return dataType == CDF.CDF_CHAR ||
               dataType == CDF.CDF_UCHAR;
    }
}
