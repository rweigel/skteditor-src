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
 * $Id: PadValue.java,v 1.2 2022/03/24 10:38:18 btharris Exp $
 */
package gsfc.spdf.cdf;


import gsfc.nssdc.cdf.CDF;


/**
 * This class represents the CDF variable's pad value.
 */
public class PadValue {

    /**
     * Gets the default pad value for the given CDF data type.
     *
     * @param dataType CDF data type.
     * @return default pad value for the specified data type.  null if 
     *     no default pad value is defined.
     */
    public static Object getDefaultPadValue(long dataType) {

        if (dataType == CDF.CDF_BYTE) {

            return Byte.valueOf(CDF.DEFAULT_BYTE_PADVALUE);
        }
        else if (dataType == CDF.CDF_CHAR) {

            return String.valueOf(CDF.DEFAULT_CHAR_PADVALUE);
        }
        else if (dataType == CDF.CDF_INT1) {

            return Byte.valueOf(CDF.DEFAULT_INT1_PADVALUE);
        }
        else if (dataType == CDF.CDF_UCHAR) {

            return String.valueOf(CDF.DEFAULT_UCHAR_PADVALUE);
        }
        else if (dataType == CDF.CDF_UINT1) {

            return Short.valueOf(CDF.DEFAULT_UINT1_PADVALUE);
        }
        else if (dataType == CDF.CDF_INT2) {

            return Short.valueOf(CDF.DEFAULT_INT2_PADVALUE);
        }
        else if (dataType == CDF.CDF_UINT2) {

            return Integer.valueOf(CDF.DEFAULT_UINT2_PADVALUE);
        }
        else if (dataType == CDF.CDF_INT4) {

            return Integer.valueOf(CDF.DEFAULT_INT4_PADVALUE);
        }
        else if (dataType == CDF.CDF_UINT4) {

            return Long.valueOf(CDF.DEFAULT_UINT4_PADVALUE);
        }
        else if (dataType == CDF.CDF_INT8) {

            return Long.valueOf(CDF.DEFAULT_INT8_PADVALUE);
        }
        else if (dataType == CDF.CDF_REAL4) {

            return Float.valueOf(CDF.DEFAULT_REAL4_PADVALUE);
        }
        else if (dataType == CDF.CDF_FLOAT) {

            return Float.valueOf(CDF.DEFAULT_FLOAT_PADVALUE);
        }
        else if (dataType == CDF.CDF_REAL8) {

            return Double.valueOf(CDF.DEFAULT_REAL8_PADVALUE);
        }
        else if (dataType == CDF.CDF_DOUBLE) {

            return Double.valueOf(CDF.DEFAULT_DOUBLE_PADVALUE);
        }
        else if (dataType == CDF.CDF_EPOCH) {

            return Double.valueOf(CDF.DEFAULT_EPOCH_PADVALUE);
        }
        else if (dataType == CDF.CDF_EPOCH16) {

            return new double[] {
                CDF.DEFAULT_EPOCH16_PADVALUE,
                CDF.DEFAULT_EPOCH16_PADVALUE
            };
        }
        else if (dataType == CDF.CDF_TIME_TT2000) {

            return Long.valueOf(CDF.DEFAULT_TT2000_PADVALUE);
        }

        return null;
    }

}
