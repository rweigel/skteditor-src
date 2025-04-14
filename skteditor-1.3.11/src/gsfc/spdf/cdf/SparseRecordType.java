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
 * $Id: SparseRecordType.java,v 1.2 2022/03/24 10:38:18 btharris Exp $
 */
package gsfc.spdf.cdf;


import gsfc.nssdc.cdf.CDF;


/**
 * This class represents the CDF sparse record types.
 */
public enum SparseRecordType {

    /**
     * The variable doesn't have sparse records.
     */
    NONE(CDF.NO_SPARSERECORDS),

    /**
     * Missing records are returned as pad values.
     */
    PAD(CDF.PAD_SPARSERECORDS),

    /**
     * Missing records are returned as the previous record's value.
     */
    PREVIOUS(CDF.PREV_SPARSERECORDS);

    /**
     * Long value.
     */
    private final long value;


    /**
     * Constructs the specified sparse record type value.
     *
     * @param value sparse record type value.
     */
    private SparseRecordType(long value) {

        this.value = value;
    }


    /**
     * Provides the long value corresponding to this sparse record type.
     *
     * @return long value corresponding to this sparse record type.
     */
    public long value() {

        return value;
    }


    /**
     * Provides the SparseRecordType corresponding to the given long
     * value.
     *
     * @param value long value corresponding to a SparseRecordType.
     * @return SparseRecordType corresponding to the given value.
     * @throws IllegalArgumentException if the given value does not
     *             correspond to any SparseRecordType.
     */
    public static SparseRecordType fromValue(long value) {

        for (SparseRecordType type : SparseRecordType.values()) {

            if (type.value() == value) {

                return type;
            }
        }

        throw new IllegalArgumentException(Long.toString(value));
    }


    /**
     * Provides a string representation of this value.
     *
     * @return a string representation of this value.
     */
    public String toString() {

        switch (this) {

        case NONE:

            return "None";

        case PAD:

            return "Pad";

        case PREVIOUS:

            return "Previous";

        default:

            return "Undefined SparseRecordType " + value;
        }
    }
}
