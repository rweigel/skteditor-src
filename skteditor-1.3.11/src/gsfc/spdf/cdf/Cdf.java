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
 * Copyright (c) 2015-2024 United States Government as represented by 
 * the National Aeronautics and Space Administration. No copyright is 
 * claimed in the United States under Title 17, U.S.Code. All Other 
 * Rights Reserved.
 *
 * $Id: Cdf.java,v 1.5 2024/10/25 18:08:04 btharris Exp $
 */
package gsfc.spdf.cdf;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.io.PrintStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import gsfc.nssdc.cdf.CDF;
import gsfc.nssdc.cdf.CDFConstants;
import gsfc.nssdc.cdf.CDFException;
import gsfc.nssdc.cdf.Variable;


/**
 * This class represents a CDF file.
 */
public class Cdf {


    /**
     * Print to System.err the names and IDs of all variables in the 
     * specified CDF.
     *
     * @param cdf file whose variables are to be printed.
     * @param strm stream to print to.
     * @param label label to print before the variables.
     */
    private static void printVars(
        CDF cdf,
        PrintStream strm,
        String label) {

        Vector v = cdf.getVariables();
        System.err.println("printVars: " + label);
        for (int i = 0; i < v.size(); i++) {
            Variable variable = (Variable)v.get(i);
            strm.println("  " + i + " " + variable.getName() + " (" + 
                variable.getID() + ")");
        }            
    }        
    

    /**
     * Moves a group of variables in the given CDF to a new location.
     *
     * @param cdf file whose variables are to be moved.
     * @param names names of the variables to move.
     * @param insertIndex index of the variable position after which 
     *     the variables are to be inserted
     *     (0 &le; insertIndex &le; number of variables).
     * @throws CDFException if a CDFException occurs.
     */   
    public static void moveVariables(
        CDF cdf,
        java.util.List<String> names,
        int insertIndex)
        throws CDFException {

        if (cdf == null) {

            return;
        }        
        Vector vars = cdf.getVariables();
        int originalSize = vars.size();
        ArrayList<String> varNames = 
            new ArrayList<>(vars.size());// names of the cdf
                                       // variables in a list that
                                       // supports insertion
        for (int i = 0; i < vars.size(); i++) {

            Variable var = (Variable)vars.get(i);
            varNames.add(var.getName());
        }        
//System.err.println("*** moveVariable: original varNames:");
//for (String name : varNames) System.err.println("  " + name);

        int originalIndex = varNames.indexOf(names.get(0));
//System.err.println("*** moveVariable: originalIndex = " + originalIndex);

        varNames.removeAll(names);

//System.err.println("*** moveVariable: varNames after removeAll:");
//for (String name : varNames) System.err.println("  " + name);

        if (insertIndex > originalIndex) {

            insertIndex -= names.size();
        }        
//System.err.println("*** moveVariable: insertIndex = " + insertIndex);
        if (insertIndex < 0) { 

            return;
        }        
        varNames.addAll(insertIndex, names);

//System.err.println("*** moveVariable: varNames after addAll:");
//for (String name : varNames) System.err.println("  " + name);

        // duplicate variables in the desired order with a temporary
        // name (at end of variables)
        for (String varName : varNames) {

            Variable var = cdf.getVariable(varName);
//System.err.println("*** duplicating " + var.getName() + " (" + var.getID() + ")");
            Variable dupVar = var.duplicate(varName + "*");
        }
//printVars("*** after duplication");
        // delete the original variables
        vars = cdf.getVariables();
        for (int i = 0; i < originalSize; i++) {

            Variable var = (Variable)vars.get(0);
//System.err.println("*** deleting " + var.getName() + " (" + var.getID() + ")");
            var.delete();
        }
//printVars("*** after deletions");
        // finally rename duplicated variables to desired (original) names
        vars = cdf.getVariables();
        for (int i = 0; i < vars.size(); i++) {

            Variable var = (Variable)vars.get(i);
//System.err.println("*** about to rename " + var.getName() +
//" to " + varNames.get(i));
            var.rename(varNames.get(i));
        }
//printVars("*** after renames");
    }


    /**
     * Returns a String representation of the given CDF's encoding.
     *
     * @param cdf file whose encoding is to be returned.
     * @return the String representation for the specified CDF's 
     *     encoding.
     */
    public static String getEncodingAsString(CDF cdf) {

        switch ((int)cdf.getEncoding()) {
        case (int)CDFConstants.NETWORK_ENCODING:
            return "Network";
        case (int)CDFConstants.SUN_ENCODING:
            return "Sun";
        case (int)CDFConstants.VAX_ENCODING:
            return "VAX";
        case (int)CDFConstants.DECSTATION_ENCODING:
            return "DECStation";
        case (int)CDFConstants.SGi_ENCODING:
            return "Silicon Graphics";
        case (int)CDFConstants.IBMPC_ENCODING:
            return "IEEE Little Endian (IBM PC)";
        case (int)CDFConstants.IBMRS_ENCODING:
            return "IBM RISC";
        case (int)CDFConstants.HOST_ENCODING:
            return "Host";
        case (int)CDFConstants.MAC_ENCODING:
            return "Macintosh";
        case (int)CDFConstants.HP_ENCODING:
            return "Hewlett-Packard";
        case (int)CDFConstants.NeXT_ENCODING:
            return "NeXT";
        case (int)CDFConstants.ALPHAOSF1_ENCODING:
            return "Alpha OSF1";
        case (int)CDFConstants.ALPHAVMSd_ENCODING:
            return "Alpha VMSd";
        case (int)CDFConstants.ALPHAVMSg_ENCODING:
            return "Alpha VMSg";
        case (int)CDFConstants.ALPHAVMSi_ENCODING:
            return "Alpha VMSi";
        default:
            return "Unknown";
        }
    }


    /**
     * Returns a String representation of the given CDF's last known
     * leap second date.  This method is designed to compile and run
     * under versions of the CDF library prior to 3.6 which don't
     * support the getLeapSecondLastUpdated method.
     *
     * @param cdf file whose last known leap second date is to be 
     *            returned.
     * @return the String representation for the specified CDF's 
     *     last known leap second date.
     * @throws CDFException if there was a problem getting the data 
     *     or other vital infomation from the CDF file.
     */
    public static String getLeapSecondLastUpdatedAsString(CDF cdf) 
        throws CDFException {

        String lastLeapSecondStr = "unknown";
                                       // String representation of
                                       // CDF's last leap second
        //
        // When not required to compile with CDF < 3.6, the following
        // reflection code can be replaced by 
        //     long lastUpdated = cdf.getLeapSecondLastUpdated();
        try {

            Class getLeapSecondLastUpdatedArgs[] = new Class[] {};
                                       // getLeapSecondLastUpdated
                                       // arguments (none)
            Method getLeapSecondLastUpdatedMethod =
                cdf.getClass().getDeclaredMethod(
                    "getLeapSecondLastUpdated", 
                    getLeapSecondLastUpdatedArgs);
                                       // CDF.getLeapSecondLastUpdated
                                       // method (CDF version > 3.6)
            Long leapSecondLastUpdatedResult = (Long)
                getLeapSecondLastUpdatedMethod.invoke(
                    cdf, new Object[] {});

            long lastUpdated = leapSecondLastUpdatedResult.longValue();

            if (lastUpdated == -1) {

                lastLeapSecondStr = "unknown";
            }
            else if (lastUpdated == 0) {

                lastLeapSecondStr = "infinite";
            }
            else {

                lastLeapSecondStr = lastUpdated + "";

                if (lastLeapSecondStr.length() == 8) {

                    lastLeapSecondStr = 
                       lastLeapSecondStr.substring(0, 4) + "-" +
                       lastLeapSecondStr.substring(4, 6) + "-" +
                       lastLeapSecondStr.substring(6, 8);
                }
                else {
    
                    lastLeapSecondStr = "error";
                }
            }
        }
        catch (NoSuchMethodException e) {

            // old version of CDF < 3.6.  Use default value.
        }
        catch (SecurityException e) {

            System.err.println("SecurityException while reflecting " +
                "on the CDF library" + e.getMessage());
            // Use default value.
        }
        catch (IllegalAccessException e) {

            System.err.println("IllegalAccessException while " +
                "reflecting on the CDF library" + e.getMessage());
            // Use default value.
        }
        catch (InvocationTargetException e) {

            System.err.println("InvocationTargetException while " +
                "reflecting on the CDF library" + e.getMessage());
            // Use default value.
        }

        return lastLeapSecondStr;
    }


    /**
     * Returns a String representation of the given CDF's 
     * multidimensional array memory layout.
     *
     * @param cdf file whose multidimensional array memory layout 
     *            is to be returned.
     * @return the String representation for the specified CDF's 
     *     multidimensional array memory layout.
     */
    public static String getMajorityAsString(CDF cdf) {

        return cdf.getMajority() == CDFConstants.ROW_MAJOR ? 
                   "Row" : "Column";
    }


    /**
     * Provides a String value describing the given CDF's checksum
     * option setting.  This method is designed to compile and run
     * under versions of the CDF library prior to 3.1 which don't 
     * support the checksum option.
     *
     * @param cdf CDF whose option setting is to be returned
     * @return the given CDF's checksum option or "Not Applicable"
     *     if the CDF is a version that doesn't support the checksum
     *     option
     */
    public static String getChecksumAsString(CDF cdf) {

        String checksumValue = "Not Applicable";
                                       // default return value
        //
        // When not required to compile with CDF < 3.2, the following
        // reflection code can be replaced with
        // long option = cdf.getChecksum();
        //
        try {

            Class getChecksumArgs[] = new Class[] {};
                                       // getChecksum arguments (none)
            Method getChecksumMethod = 
                cdf.getClass().getDeclaredMethod("getChecksum", 
                                                 getChecksumArgs);
                                       // CDF.getChecksum method (CDF
                                       // version > 3.1).
            Field noneChecksumField =
                CDFConstants.class.getDeclaredField("NONE_CHECKSUM");
                                       // CDFConstants.NONE_CHECKSUM 
                                       // field
            Field md5ChecksumField =
                CDFConstants.class.getDeclaredField("MD5_CHECKSUM");
                                       // CDFConstants.MD5_CHECKSUM 
                                       // field
            Long checksumResult = (Long)
                getChecksumMethod.invoke(cdf, new Object[] {});
                                       // theCDF.getChecksumMethod()
                                       // (wrapped) result
            long noneChecksumValue = 
                noneChecksumField.getLong(CDFConstants.class);
                                       // CDFConstants.NONE_CHECKSUM 
                                       // value
            long md5ChecksumValue = 
                md5ChecksumField.getLong(CDFConstants.class);
                                       // CDFConstants.MD5_CHECKSUM 
                                       // value

            if (checksumResult.longValue() == noneChecksumValue) {

                checksumValue = "None";
            }
            else if (checksumResult.longValue() == md5ChecksumValue) {

                checksumValue = "MD5";
            }
        }
        catch (NoSuchMethodException e) {

            // old version of CDF.  Use default NA value.
        }
        catch (NoSuchFieldException e) {

            // this shouldn't happen because NoSuchMethodException
            // would happen first
            System.err.println("NoSuchFieldException: " + 
                e.getMessage());
            // Use default NA value.
        }
        catch (SecurityException e) {

            System.err.println("SecurityException while reflecting " +
                "on the CDF library" + e.getMessage());
            // Use default NA value.
        }
        catch (IllegalAccessException e) {

            System.err.println("IllegalAccessException while " +
                "reflecting on the CDF library" + e.getMessage());
            // Use default NA value.
        }
        catch (InvocationTargetException e) {

            System.err.println("InvocationTargetException while " +
                "reflecting on the CDF library" + e.getMessage());
            // Use default NA value.
        }

        return checksumValue;
    }


    /**
     * Produces a List of time (EPOCH, EPOCH16, or TT2000) variables
     * contained in the given CDF.
     *
     * @param cdf the CDF to search.
     * @return a List of time (EPOCH, EPOCH16, or TT2000) variables
     *     contained in the given CDF.
     */
    public static List<gsfc.nssdc.cdf.Variable> getAllTimeVariables(
        CDF cdf) {

        ArrayList<gsfc.nssdc.cdf.Variable> timeVars =
            new ArrayList<gsfc.nssdc.cdf.Variable> ();
                                       // all the TT2000 variables
        Vector vars = cdf.getVariables();
                                       // all the variables in the CDF

        for (int i = 0; i < vars.size(); i++) {

            gsfc.nssdc.cdf.Variable var =
                (gsfc.nssdc.cdf.Variable)vars.elementAt(i);
                                       // a specific variable
            long dataType = var.getDataType();
                                       // variable's data type
            if (dataType == CDF.CDF_EPOCH ||
                dataType == CDF.CDF_EPOCH16 || 
                dataType == CDF.CDF_TIME_TT2000) {

                timeVars.add(var);
            }
        }

        return timeVars;

    }

}
