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
 * $Id: VerificationCallback.java,v 1.11 2022/03/24 10:38:39 btharris Exp $
 */
package gsfc.spdf.istp;


import gsfc.nssdc.cdf.Attribute;
import gsfc.nssdc.cdf.Entry;
import gsfc.spdf.istp.Variable;


/**
 * Defines the callback interface used to report violations during an
 * ISTP compliance verification operation.  <b>Note:</b> this interface
 * is currently incomplete.  Many additional violation conditions are
 * expected to be added in the future.  Once support for all violations
 * has been added, the accumulation of error messages in
 * {@link VerificationResult} can be eliminated since the callback
 * mechanism can support both immediate reporting of violations and an
 * accumulated report.
 *
 * @author B. Harris
 * @version $Revision: 1.11 $
 */
public interface VerificationCallback {

    /**
     * Callback status value indicating that the callback function took
     * corrective action and the verification operation can continue.
     */
    public static final int CORRECTED = 0;

    /**
     * Callback status value indicating that the callback function did
     * not correct the problem but the verification operation can 
     * continue.
     */
    public static final int NOT_CORRECTED = 1;

    /**
     * Callback status value indicating that the verification operation
     * should be aborted.
     */
    public static final int ABORT = 2;


    /**
     * Method that is called when 
     * {@link Variable#checkName(gsfc.nssdc.cdf.CDF, 
     * gsfc.nssdc.cdf.Variable)} fails.
     *
     * @param var the variable whose name failed the compliance test
     * @param conflictVar the variable whose name is in conflict with
     *            var.  null if name error is not caused by another
     *            variable.
     * @return CORRECTED, NOT_CORRECTED, or ABORT status to indicate
     *     the action taken and whether to proceed with the 
     *     verification operation
     */
    public int handleVariableNameError(Variable var, 
                                       Variable conflictVar);


    /**
     * Method that is called when the names of two Attributes are found
     * to differ only in case.
     *
     * @param attr a conflicting attribute 
     * @param conflictAttr the other conflicting attribute 
     * @return CORRECTED, NOT_CORRECTED, or ABORT status to indicate
     *     the action taken and whether to proceed with the 
     *     verification operation
     */
    public int handleDuplicateAttributeNameError(Attribute attr, 
                                                 Attribute conflictAttr);

    /**
     * Method that is called when the name of an Attribute is found
     * to be invalid.
     *
     * @param attr the affending attribute 
     * @param cause description of problem
     * @return CORRECTED, NOT_CORRECTED, or ABORT status to indicate
     *     the action taken and whether to proceed with the 
     *     verification operation
     */
    public int handleAttributeNameError(Attribute attr, String cause);


    /**
     * Method that is called when a Variable Attribute value is found
     * that contains non-ASCII characters.
     *
     * @param var the affending variable.
     * @param attr the affending attribute.
     * @param entry the affending entry.
     * @return CORRECTED, NOT_CORRECTED, or ABORT status to indicate
     *     the action taken and whether to proceed with the
     *     verification operation
     */
    public int handleNonAsciiAttributeValueError(
        Variable var, Attribute attr, Entry entry);

    /**
     * Method that is called when a Variable is missing dimension size
     * information.
     *
     * @param var the affending variable.
     * @param dimensions the number of dimensions.
     * @return CORRECTED, NOT_CORRECTED, or ABORT status to indicate
     *     the action taken and whether to proceed with the
     *     verification operation
     */
    public int handleMissingDimSizeError(
        Variable var, long dimensions);

    /**
     * Method that is called when a Variable has a bad dimension size.
     *
     * @param var the affending variable.
     * @param dimension dimension having the bad size.
     * @param dimensionSize size of dimension.
     * @return CORRECTED, NOT_CORRECTED, or ABORT status to indicate
     *     the action taken and whether to proceed with the
     *     verification operation
     */
    public int handleBadDimSizeError(
        Variable var, int dimension, long dimensionSize);

    /**
     * Method that is called when a Variable has a dimension size
     * greater than 1 and a dimension variance of NOVARY.
     *
     * @param var the affending variable.
     * @param dimension dimension having the bad size.
     * @param dimensionSize size of dimension.
     * @param dimensionVariance variance of dimension.
     * @return CORRECTED, NOT_CORRECTED, or ABORT status to indicate
     *     the action taken and whether to proceed with the
     *     verification operation
     */
    public int handleDimVarianceError(
        Variable var, int dimension, long dimensionSize, 
        long dimensionVariance);

    /**
     * Method that is called when a Variable is missing its UNITS
     * attribute.
     *
     * @param var the variable that is missing a UNITS attribute.
     * @return CORRECTED, NOT_CORRECTED, or ABORT status to indicate
     *     the action taken and whether to proceed with the
     *     verification operation
     */
    public int handleMissingUnits(
        Variable var);

    /**
     * Method that is called when a Variable has a bad pointer attribute
     * value.  That is, when the pointer does not point to an actual variable.
     *
     * @param var the variable with the bad pointer attribute value.
     * @param name name of attribute.
     * @param value value of attribute.
     * @return CORRECTED, NOT_CORRECTED, or ABORT status to indicate
     *     the action taken and whether to proceed with the
     *     verification operation
     */
    public int handleBadPointerTarget(
        Variable var,
        String name,
        String value);


    /**
     * Method that is called when an Entry has the wrong data type.
     *
     * @param entry the Entry with the wrong data type.
     * @param requiredType the required data type.
     * @return CORRECTED, NOT_CORRECTED, or ABORT status to indicate
     *     the action taken and whether to proceed with the
     *     verification operation.
     */
    public int handleBadEntryDataType(
        Entry entry,
        long requiredType);


    /**
     * Method that is called when a Variable has multiple Attribute
     * values.
     *
     * @param variable the Variable with multiple Attribute values.
     * @param attributeName name of Attribute with multiple values.
     * @param value the attribute's values.
     * @return CORRECTED, NOT_CORRECTED, or ABORT status to indicate
     *     the action taken and whether to proceed with the
     *     verification operation.
     */
    public int handleMultiValueAttribute(
        Variable variable,
        String attributeName,
        Object value);

}
