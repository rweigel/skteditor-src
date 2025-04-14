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
 * $Id: TerrestrialTime2000.java,v 1.3 2022/03/24 10:38:18 btharris Exp $
 */
package gsfc.spdf.cdf;

import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;


import gsfc.nssdc.cdf.Attribute;
import gsfc.nssdc.cdf.CDF;
import gsfc.nssdc.cdf.CDFException;
import gsfc.nssdc.cdf.util.CDFTT2000;


/**
 * This class represents a CDF Terrestrial Time with a fixed time 
 * base of J2000 variable.
 * 
 * @author B. Harris
 * @version $Revision: 1.3 $
 */
public class TerrestrialTime2000 {


    /**
     * Determines if the given CDF contains a variable of type TT2000.
     *
     * @param cdf the CDF to search.
     * @return true if the given cdf contain a variable of type TT2000.
     *             Otherwise, false.
     */
    public static boolean hasOne(CDF cdf) {

        return getAll(cdf).size() > 0;
    }


    /**
     * Returns a list of all TT2000 type variables in the given CDF.
     *
     * @param cdf the CDF to search.
     * @return list containg all TT2000 type variables in the given CDF.
     */
    public static List<gsfc.nssdc.cdf.Variable> getAll(CDF cdf) {

        ArrayList<gsfc.nssdc.cdf.Variable> tt2000Vars =
            new ArrayList<gsfc.nssdc.cdf.Variable> ();
                                       // all the TT2000 variables
        Vector vars = cdf.getVariables();
                                       // all the variables in the CDF

        for (int i = 0; i < vars.size(); i++) {

            gsfc.nssdc.cdf.Variable var =
                (gsfc.nssdc.cdf.Variable)vars.elementAt(i);
                                       // a specific variable

            if (var.getDataType() == CDF.CDF_TIME_TT2000) {

                tt2000Vars.add(var);
            }
        }

        return tt2000Vars;
    }


    /**
     * This method returns the source of leap seconds information.
     *
     * @return LeapSecondsSource.
     */
    public static LeapSecondsSource getLeapSecondsSource() {

        return LeapSecondsSource.fromValue(
                   CDFTT2000.CDFgetLeapSecondsTableStatus());
    }


    /**
     * This method returns the leap seconds table.
     *
     * @return The table contents of the leap seconds.
     */
    public static double[][] getLeapSecondsTable() {

        return CDFTT2000.CDFgetLeapSecondsTable();
    }


    /**
     * This method returns the number of entries in the leap 
     * seconds table.
     *
     * @return The entry count in the leap seconds table.
     */
    public static int getRowsInLeapSecondsTable() {

        return CDFTT2000.CDFgetRowsinLeapSecondsTable();
    }
    
}
