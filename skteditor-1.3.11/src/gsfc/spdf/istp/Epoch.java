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
 * $Id: Epoch.java,v 1.35 2022/03/24 10:38:39 btharris Exp $
 */
package gsfc.spdf.istp;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.Vector;

import gsfc.nssdc.cdf.Attribute;
import gsfc.nssdc.cdf.CDF;
import gsfc.nssdc.cdf.CDFException;

import gsfc.spdf.cdf.SparseRecordType;


/**
 * This class represents an ISTP Epoch variable.
 * 
 * @author B. Harris
 * @version $Revision: 1.35 $
 */
public abstract class Epoch 
    extends Variable {

    /**
     * String value for an NaE value.
     */
    public final static String NaE_STRING = "Not an Epoch";

    /**
     * String value of FILLVAL value.
     */
    public final static String FILLVAL_STRING = "ISTP FILLVAL";


    /**
     * A UTC time zone.
     */
    public static final SimpleTimeZone 
        UTC_TIME_ZONE = new SimpleTimeZone(0, "UTC");

    /**
     * Creates an ISTP Epoch variable for the given CDF variable.
     *
     * @param var a CDF variable with the characteristics of an ISTP 
     *            epoch variable
     */
    protected Epoch(gsfc.nssdc.cdf.Variable var) {

        super(var);
    }


    /**
     * Gets an ISTP Epoch variable for the given CDF variable.
     *
     * @param var a CDF variable with the characteristics of an ISTP 
     *            epoch variable
     * @return an istp.Epoch variable for the given CDF variable
     * @throws IllegalArgumentException if the given variable does not
     *             qualify as an ISTP epoch variable
     */
    public static Epoch getInstance(gsfc.nssdc.cdf.Variable var) 
        throws IllegalArgumentException {

        if (!isEpoch(var)) {

            throw new IllegalArgumentException(var.getName() +
                " is not an ISTP epoch variable");
        }

        long type = var.getDataType();

        if (type == CDF.CDF_EPOCH) {

            return new Epoch8(var);
        }
        else if (type == CDF.CDF_EPOCH16) {

            return new Epoch16(var);
        }
        else {

            return new TerrestrialTime2000(var);
        }
    }


    /**
     * Create a new instance of an ISTP Epoch variable in the given
     * CDF file.  The new instance has the necessary ISTP type, 
     * dimensions, attributes, and attribute values.
     *
     * @param cdf the CDF in which this variable exists
     * @param name name of variable
     * @param dataType CDF data type of variable
     * @param numDims dimensionality of this variable
     * @param dimSizes dimension sizes.  An array of length numDims
     *            indicating the size of each dimension
     * @param recVary record variance (VARY or NOVARY)
     * @param dimVarys dimension variance(s).  Each dimension should be
     *            either VARY or NOVARY.
     * @throws CDFException if a CDFException occurs
     * @throws ISTPComplianceException if the given characteristics fail
     *             to be ISTP compliant
     */
    protected Epoch(gsfc.nssdc.cdf.CDF cdf, String name, long dataType,
                    long numDims, long[] dimSizes, long recVary, 
                    long[] dimVarys)
        throws CDFException, ISTPComplianceException {

        super(cdf, name, dataType, 1, numDims, dimSizes,
              recVary, dimVarys, SUPPORT_DATA, true, CDF.NO_COMPRESSION,
              new long[] {}, SparseRecordType.NONE);
    }


    /**
     * Sets initial attribute values.
     *
     * @throws CDFException if a CDFException occurs
     */
    public void setInitialAttributes() 
        throws CDFException {

        super.setInitialAttributes();

        try {

            setAttributeValue("CATDESC",  "Default time");

            setAttributeValue("SCALETYP",  "linear");
            setAttributeValue("MONOTON",  "INCREASE");

            setAttributeValue("LEAP_SECONDS_INCLUDED", null);
            setAttributeValue("RESOLUTION", null);
            setAttributeValue("Bin_location", CDF.CDF_FLOAT, null);
        }
        catch (ISTPIdentifierException e) {

            System.err.println(
                "gsfc.spdf.istp.Epoch.setInitialAttributes: " +
                "exception: " + e.getMessage());
        }

    }


    /**
     * Gets the "ISTP epoch" variable from the specified CDF.  
     *
     * @param cdf the CDF file to search
     * @return the "ISTP Epoch" variable or null if none was found
     */
    public static Epoch getEpoch(CDF cdf) {

        Vector vars = cdf.getVariables();
                                       // all the variables in the CDF

        for (int i = 0; i < vars.size(); i++) {

            gsfc.nssdc.cdf.Variable var = 
                (gsfc.nssdc.cdf.Variable)vars.elementAt(i);
                                       // a specific variable

            if (isEpoch(var)) {

                return getInstance(var);
            }
        }

        return null;
    }


    /**
     * Determines whether the given variable meets the criteria of an
     * "ISTP epoch" variable.  The criteria is as follows:
     * <ul>
     *   <li>Its CDF data type is EPOCH, EPOCH16, or TIME_TT2000
     *   <li>It has a dimensionality of 0
     *   <li>It is one of the following:
     *     <ul>
     *       <li>It is a record varying variable
     *       <li>It is a virtual variable
     *     </ul>
     * </ul>
     * @param var the variable to check
     * @return true if var is an "ISTP epoch" variable.  Otherwise 
     *             false.
     */
    public static boolean isEpoch(gsfc.nssdc.cdf.Variable var) {

        long type = var.getDataType(); // variable's data type

        if ((type == CDF.CDF_EPOCH || type == CDF.CDF_EPOCH16 ||
             type == CDF.CDF_TIME_TT2000) && var.getNumDims() == 0) {
            
            if (var.getRecVariance()) {

                if (!var.getName().equalsIgnoreCase("range_epoch")) {

                    return true;
                }
            }
            else if (VirtualVariable.isVirtual(var)) { 

                return true;
            }
        }

        return false;
    }


    /**
     * Gets the "THEMIS range epoch" variable from the specified CDF.  
     * The THEMIS project has used the range-epoch instead of the
     * traditional ISTP epoch.  One should not assume that the 
     * object returned by this function will function the same as
     * a traditional ISTP epoch object.
     *
     * @param cdf the CDF file to search
     * @return the "THEMIS range Epoch" variable or null if none was 
     *             found.
     */
    public static Variable getRangeEpoch(CDF cdf) {

        Vector vars = cdf.getVariables();
                                       // all the variables in the CDF

        for (int i = 0; i < vars.size(); i++) {

            gsfc.nssdc.cdf.Variable var = 
                (gsfc.nssdc.cdf.Variable)vars.elementAt(i);
                                       // a specific variable

            if (isRangeEpoch(var)) {

                return new Variable(var);
            }
        }

        return null;
    }


    /**
     * Determines whether the given variable meets the criteria of an
     * "THEMIS range epoch" variable.  The criteria is as follows:
     * <ul>
     *   <li>Its CDF data type is EPOCH
     *   <li>It has a dimensionality of 0
     *   <li>It is a record varying variable
     *   <li>It has a name of "range_epoch"
     * </ul>
     * @param var the variable to check
     * @return true if var is an "THEMIS range epoch" variable.  
     *             Otherwise false.
     */
    public static boolean isRangeEpoch(gsfc.nssdc.cdf.Variable var) {

        long type = var.getDataType(); // variable's data type

        if (type == CDF.CDF_EPOCH && var.getNumDims() == 0) {
            
            if (var.getRecVariance() && 
                var.getName().equalsIgnoreCase("range_epoch")) {

                return true;
            }
        }

        return false;
    }


    /**
     * Gets the earliest (record 0) Epoch variable value as a Date from
     * the given cdf.  If the cdf contains a THEMIS range epoch instead
     * of the usual ISTP Epoch variable, then the earlies range epoch
     * value is returned.
     *
     * @param cdf the CDF file to search.
     * @return earliest (record 0) Epoch value as a Date or null if
     *     no value is found.
     * @throws CDFException if a CDFException occurs.
     */
    public static Date getEarliestEpochDate(CDF cdf) 
        throws CDFException {

        Epoch epoch = null;            // Epoch variable
        Variable rangeEpochVar = getRangeEpoch(cdf);
                                       // THEMIS' range epoch variable
        if (rangeEpochVar != null) {

            if (rangeEpochVar.getDataType() == CDF.CDF_EPOCH) {

                epoch = new Epoch8(rangeEpochVar.getCdfVariable());
            }
            else if (rangeEpochVar.getDataType() == CDF.CDF_EPOCH16) {

                epoch = new Epoch16(rangeEpochVar.getCdfVariable());
            }
            else if (rangeEpochVar.getDataType() == 
                     CDF.CDF_TIME_TT2000) {

                epoch = new TerrestrialTime2000(
                                rangeEpochVar.getCdfVariable());
            }
            else {

                return null;
            }
        }
        else {

            epoch = getEpoch(cdf);
        }

        if (epoch != null && epoch.getNumWrittenRecords() > 0) {

            return epoch.getDate(0L);
        }

        return null;
    }
        

    /**
     * Sets the UNITS attribute value based upon the data type.
     *
     * @throws CDFException if a CDF error occurs when attempting to
     *     set the attribute.
     */
    public void setUnitsValue() 
        throws CDFException {

        long dataType = getDataType(); // CDF data type

        if (dataType == CDF.CDF_TIME_TT2000) {

            ((TerrestrialTime2000)this).setUnitsValue();
        }
        else if (dataType == CDF.CDF_EPOCH) {

            ((Epoch8)this).setUnitsValue();
        }
        else { // EPOCH16

            ((Epoch16)this).setUnitsValue();
        }
    }


    /**
     * Gets the TIME_BASE attribute value.
     *
     * @return TIME_BASE attribute value or null if it does not exist.
     */
    public String getTimeBaseValue() {

        return (String)getAttributeValue("TIME_BASE");
    }



    /**
     * Sets the TIME_BASE attribute value based upon the data type.
     *
     * @throws CDFException if a CDF error occurs when attempting to
     *     set the attribute.
     */
    public void setTimeBaseValue() 
        throws CDFException {

        long dataType = getDataType(); // CDF data type

        if (dataType == CDF.CDF_TIME_TT2000) {

            ((TerrestrialTime2000)this).setTimeBaseValue();
        }
        else if (dataType == CDF.CDF_EPOCH) {

            ((Epoch8)this).setTimeBaseValue();
        }
        else { // EPOCH16

            ((Epoch16)this).setTimeBaseValue();
        }
    }


    /**
     * Gets the Date value at the specified record.
     *
     * @param record identifies a specific record
     * @return value from the specfied record as a Date.
     * @throws CDFException if a CDFException occurs.
     */
    public abstract Date getDate(long record) throws CDFException;

}
