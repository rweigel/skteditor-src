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
 * Copyright (c) 2018-2022 United States Government as represented by 
 * the National Aeronautics and Space Administration. No copyright is 
 * claimed in the United States under Title 17, U.S.Code. All Other 
 * Rights Reserved.
 *
 * $Id: VarType.java,v 1.3 2022/08/02 12:09:53 btharris Exp $
 */
package gsfc.spdf.edit.guis;


import gsfc.nssdc.cdf.Attribute;
import gsfc.nssdc.cdf.CDF;
import gsfc.nssdc.cdf.CDFException;
import gsfc.nssdc.cdf.Variable;


/**
 * This class represents an International Solar-Terrestrial Physics
 * (ISTP) VAR_TYPE variable attribute.  See
 * <a href="http://spdf.gsfc.nasa.gov/sp_use_of_cdf.html">
 * ISTP guidelines for CDF</a>.  This class is a specialized 
 * representation for existing edit.guis classes.  It may be better for
 * new code to use
 * {@link gsfc.spdf.istp.Variable#getType()}.
 *
 * @author B. Harris
 * @version $Revision: 1.3 $
 */
public class VarType {

    /**
     * At present, this class only exists for the static method below.
     */
    private VarType () {}


    /**
     * Gets the lower-case value of the first VAR_TYPE attribute entry
     * of the specified Variable.  If the Attribute does not exist, it
     * is create (with no Entry).  If you need the actual 
     * (case-sensitive) value(s), 
     * {@link gsfc.spdf.istp.Variable#getType()} may be a better choice.
     *
     * @param var Variable's whose VAR_TYPE attribute entry value is to
     *     be returned.
     * @return the lower-case value of the first VAR_TYPE attribute entry
     *     of the specified Variable.  null if it does not exist.
     * @throws CDFException if a CDF error occurs.
     */
    public static String get(
        Variable var)
        throws CDFException {

        String varType = null;

        Object varTypeObj = null;

        try {

            varTypeObj = var.getEntryData("VAR_TYPE");
        }
        catch (CDFException e) {

            if (e.getCurrentStatus() == CDF.NO_SUCH_ATTR) {

                // This side effect is here because existing code
                // expects it.
                Attribute.create(var.getMyCDF(), "VAR_TYPE", 
                    CDF.VARIABLE_SCOPE);

                varTypeObj = setDefaultValue(var);
            }
            else if (e.getCurrentStatus() == CDF.NO_SUCH_ENTRY) {

                varTypeObj = setDefaultValue(var);
            }
            else {

                throw e;
            }
        } 

        if (varTypeObj == null) {

            return null;
        }

        if (varTypeObj instanceof String) {

            varType = (String)varTypeObj;
        }
        else if (varTypeObj instanceof String[]) {

            varType = ((String[])varTypeObj)[0];
        }
        else {

            System.err.println(
                "VarType.get: varTypeObj is " +
                varTypeObj.getClass().getName());

            return null;
        }
        return varType.toLowerCase();
    }


    /**
     * Sets a default value for the given variable's VAR_TYPE attribute.
     *
     * @param var variable whose VAR_TYPE attribute is to be set.
     * @return new, default VAR_TYPE attribute value.
     * @throws CDFException if a CDF exception occurs.
     */
    public static String[] setDefaultValue(
        Variable var)
        throws CDFException {
        
        gsfc.spdf.istp.Variable istpVar = new gsfc.spdf.istp.Variable(var);
                                       // istp version of var
        String[] defaultValue = istpVar.setDefaultType();
                                       // default VAR_TYPE value
        System.err.println("Setting " + var.getName() + 
            "'s missing VAR_TYPE value to " + defaultValue[0] + ".");

        return defaultValue;
    }

}
