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
 * Copyright (c) 2015-2022 United States Government as represented by 
 * the National Aeronautics and Space Administration. No copyright is 
 * claimed in the United States under Title 17, U.S.Code. All Other 
 * Rights Reserved.
 *
 * $Id: LeapSecondsSource.java,v 1.2 2022/03/24 10:38:18 btharris Exp $
 */
package gsfc.spdf.cdf;


import gsfc.nssdc.cdf.CDF;


/**
 * This class represents the source of CDF leap seconds information.
 */
public enum LeapSecondsSource {

    /**
     * Leap seconds information is from a file.
     */
    FILE(1),

    /**
     * Leap seconds information is from the code itself (a hard-coded
     * table within the code).
     */
    CODE(0);


    /**
     * Int value.
     */
    private final int value;


    /**
     * Constructs the specified leap seconds source value.
     *
     * @param value leap seconds source value.
     */
    private LeapSecondsSource(int value) {

        this.value = value;
    }


    /**
     * Provides the int value corresponding to this leap seconds source.
     *
     * @return int value corresponding to this leap seconds source.
     */
    public int value() {

        return value;
    }


    /**
     * Provides the LeapSecondsSource corresponding to the given int
     * value.
     *
     * @param value int value corresponding to a LeapSecondsSource.
     * @return LeapSecondsSource corresponding to the given value.
     * @throws IllegalArgumentException if the given value does not
     *             correspond to any LeapSecondsSource.
     */
    public static LeapSecondsSource fromValue(int value) {

        for (LeapSecondsSource type : LeapSecondsSource.values()) {

            if (type.value() == value) {

                return type;
            }
        }

        throw new IllegalArgumentException(Integer.toString(value));
    }


    /**
     * Provides a string representation of this value.
     *
     * @return a string representation of this value.
     */
    public String toString() {

        switch (this) {

        case FILE:

            return "File";

        case CODE:

            return "Code";

        default:

            return "Undefined LeapSecondsSource " + value;
        }
    }
}
