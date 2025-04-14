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
 * Copyright (c) 2011-2024 United States Government as represented by 
 * the National Aeronautics and Space Administration. No copyright is 
 * claimed in the United States under Title 17, U.S.Code. All Other 
 * Rights Reserved.
 *
 * $Id: VariablesVerificationResult.java,v 1.7 2024/10/28 13:03:24 btharris Exp $
 */
package gsfc.spdf.istp;

import java.util.Map;
import java.util.Vector;

/**
 * This class represents the results of verifying ISTP compliance of a
 * CDF's variables.
 *
 * @author B. Harris
 * @version $Revision: 1.7 $
 */

public class VariablesVerificationResult {

    /**
     * Variable error messages.
     */
    private Map<String, Vector<String>>  errors;

    /**
     * Status of ISTP compliance verification operation.  The possible
     * values are those of {@link VerificationResult#status}.
     */
    private int status = VerificationResult.COMPLETE;


    /**
     * Creates a VariableVerificationResult object with the given values.
     *
     * @param errors variable error messages
     * @param status verification completion status
     */
    public VariablesVerificationResult(
        Map<String, Vector<String>> errors, 
        int status) {

        this.errors = errors;
        this.status = status;
    }


    /**
     * Gets the error messages.
     *
     * @return a map containing the variable error messages
     */
    public Map<String, Vector<String>> getErrors() {

        return errors;
    }


    /**
     * Sets the error messages.
     *
     * @param value a hashtable containing the variable error messages
     * @see #getErrors
     */
/*
    public void setErrors(Hashtable value) {

        errors = value;
    }
*/


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
}

