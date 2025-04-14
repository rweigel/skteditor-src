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
 * Copyright (c) 2011-2025 United States Government as represented by 
 * the National Aeronautics and Space Administration. No copyright is 
 * claimed in the United States under Title 17, U.S.Code. All Other 
 * Rights Reserved.
 *
 * $Id: VerificationResult.java,v 1.6 2025/01/23 18:18:18 btharris Exp $
 */
package gsfc.spdf.istp;

import java.io.PrintStream;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


/**
 * This class represents the results of verifying a CDF's ISTP 
 * compliance.
 *
 * @author B. Harris
 * @version $Revision: 1.6 $
 */
public class VerificationResult {

    /**
     * Global attribute error messages.
     */
    private Vector<String> globalAttributeErrors = new Vector<>();

    /**
     * Variable error messages.
     */
    private Map<String, Vector<String>> variableErrors = new HashMap<>();

    /**
     * Status of ISTP compliance verification operation.
     */
    private int status = COMPLETE;

    /**
     * ISTP compliance verification status indicating that the operation
     * was completed (with or without errors).
     */
    public static final int COMPLETE = 0;

    /**
     * ISTP compliance verification statis indicating that the operation
     * was aborted.  Presently, the verification operation will always
     * complete unless instructed to abort by the
     * {@link VerificationCallback}.
     */
    public static final int ABORTED = 1;


    /**
     * Creates an empty VerificationResult.
     */
    public VerificationResult() {}


    /**
     * Creates a VerificationResult with the given values.
     *
     * @param globalAttributeErrors global attribute error messages
     * @param variableErrors variable error messages
     * @param status verification completion status
     */
    public VerificationResult(
        Vector<String> globalAttributeErrors, 
        Map<String, Vector<String>> variableErrors, 
        int status) {
        
        this.globalAttributeErrors = globalAttributeErrors;
        this.variableErrors = variableErrors;
        this.status = status;
    }


    /**
     * Gets the global attribute error messages.
     *
     * @return a vector containing the error messages concerning global
     *     attributes
     * @see #setGlobalAttributeErrors
     */
    public Vector<String> getGlobalAttributeErrors() {

        return (Vector<String>)globalAttributeErrors.clone();
    }


    /**
     * Sets the global attribute error messages.
     *
     * @param value a vector containing the error messages concerning 
     *            global attributes
     * @see #getGlobalAttributeErrors
     */
    public void setGlobalAttributeErrors(
        Vector<String> value) {

        globalAttributeErrors = value;
    }


    /**
     * Gets the variable attribute error messages.
     *
     * @return a hashtable containing the error messages concerning 
     *     variables attributes.  The table's key value is the 
     *     variable's name and the associated value is a Vector 
     *     containing the actual error messages.
     * @see #setVariableErrors
     */
    public Map<String, Vector<String>> getVariableErrors() {

        return variableErrors;
    }


    /**
     * Sets the variable attribute error messages.
     *
     * @param value a hashtable containing the error messages 
     *            concerning variables.  The table's key value is the 
     *            variable's name and the associated value is a Vector 
     *            containing the actual error messages.
     * @see #getVariableErrors
     */
    public void setVariableErrors(
        Map<String, Vector<String>> value) {

        variableErrors = value;
    }


    /**
     * Gets the verification completion status.
     *
     * @return the verification completion status
     * @see #setStatus
     */
    public int getStatus() {

        return status;
    }


    /**
     * Sets the verification completion status.
     *
     * @param value the new verification completion status
     * @see #getStatus
     */
    public void setStatus(int value) {

        status = value;
    }


    /**
     * Indicates whether any global attribute errors exist.
     *
     * @return true if global attribute errors exists.  Otherwise, false.
     */
    public boolean hasGlobalAttributeErrors() {

        return globalAttributeErrors.size() > 0;
    }


    /**
     * Indicates whether any variable errors exist.
     *
     * @return true if variables errors exists.  Otherwise, false.
     */
    public boolean hasVariableErrors() {

        return variableErrors.size() > 0;
    }


    /**
     * Prints the global attribute errors to the given PrintStream.
     *
     * @param out output destination.
     */
    public void printGlobalAttributeErrors(
        PrintStream out) {

        if (globalAttributeErrors.size() > 0) {

            out.println("Global errors:");
            for (String error : globalAttributeErrors) {

                out.println("\t" + error);
            }
        } else {

            out.println("All required global attributes present.");
        };
    }


    /**
     * Prints the variable errors to the given PrintStream.
     *
     * @param out output destination.
     */
    public void printVariableErrors(
        PrintStream out) {

        if (variableErrors.size() > 0) {

            out.println("The following variables are not ISTP-compliant:");
            for (Map.Entry<String, Vector<String>> errorEntry : 
                 variableErrors.entrySet()) {

                out.println("\t" + errorEntry.getKey());

                for (String error : errorEntry.getValue()) {

                    out.println("\t\t" + error);
                }
            }
        }
    }


    /**
     * Prints the global attribute and variable errors to the given 
     * PrintStream.
     *
     * @param out output destination.
     */
    public void printErrors(
        PrintStream out) {

        printGlobalAttributeErrors(out);
        printVariableErrors(out);
    }
}
