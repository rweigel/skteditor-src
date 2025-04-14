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
 * $Id: VirtualVariable.java,v 1.34 2022/03/24 10:38:39 btharris Exp $
 */
package gsfc.spdf.istp;

import java.lang.Integer;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import gsfc.nssdc.cdf.CDF;
import gsfc.nssdc.cdf.CDFException;
import gsfc.nssdc.cdf.Attribute;

import gsfc.spdf.cdf.SparseRecordType;


/**
 * This class represents an ISTP CDF virtual variable.
 * 
 * <p><b>Note:</b> This class is currently incomplete (and therefore
 * abstract).  Some code that is currently scattered through several 
 * other classes needs to be moved here.  Once this is accomplished,
 * it can become contrete.
 *
 * @author B. Harris
 * @version $Revision: 1.34 $
 */
public class VirtualVariable 
    extends Variable {

    /**
     * This class describes attributes of virtual variable FUNCTION 
     * values.
     */
    protected static class FunctionAttributes {

        /**
         * The required dimension of the virtual variable with this
         * FUNCTION attribute value.
         */
        public int dimension;

        /**
         * The number of COMPONENT_x attributes required by a virtual
         * variable with this FUNCTION attribute value.
         */
        public int numberOfComponents;

        /**
         * Creates a FunctionAttributes object with the given attribute
         * values.
         * 
         * @param dimension the dimension required of a virtual 
         *     variable with a given FUNCTION value
         * @param numberOfComponents the number of COMPONENT_x values 
         *     required of a virtual variable with a given FUNCTION 
         *            value
         */
        public FunctionAttributes(
            int dimension, 
            int numberOfComponents) {

            this.dimension = dimension;
            this.numberOfComponents = numberOfComponents;
        }
    }


    /**
     * Creates an ISTP virtual variable of the given CDF variable.  The 
     * caller is responsible for setting the variable's attributes 
     * correctly.
     *
     * @param var a CDF variable
     */
    protected VirtualVariable(gsfc.nssdc.cdf.Variable var) {

        super(var);
    }


    /**
     * Creates an ISTP virtual variable with the given characteristics.
     *
     * @param cdf the CDF inwhich this variable exists
     * @param name name of variable
     * @param dataType CDF data type of variable
     * @param numElements for dataType = CDF_CHAR and CDF_UCHAR this is
     *            the string length, 1 other data types
     * @param numDims dimensionality of this variable
     * @param dimSizes dimension sizes.  An array of length numDims
     *            indicating the size of each dimension
     * @param recVary record variance (VARY or NOVARY)
     * @param dimVarys dimension variance(s).  Each dimension should be
     *            either VARY or NOVARY.
     * @param istpType the ISTP variable type
     * @param compressionType the compression type.
     * @param compressionParms the compression parameters that go with
     *            the specified type.  Maybe null for compression types
     *            that do not have parameters.  If null or zero-length
     *            for a compression type that requires parameters,
     *            default values will be applied.
     * @param sparseRecordType the sparse record type.
     * @param function function to compute virtual values
     * @param components name of components to pass to function
     * @throws CDFException if a CDFException occurs
     * @throws IllegalArgumentException if the given istpType is not 
     *             valid
     * @throws ISTPComplianceException if the given characteristics fail
     *             to be ISTP compliant
     */
    public VirtualVariable(gsfc.nssdc.cdf.CDF cdf, String name,
        long dataType, long numElements, long numDims, long[] dimSizes,
        long recVary, long[] dimVarys, String istpType,
        long compressionType, long[] compressionParms,
        SparseRecordType sparseRecordType,
        String function, String[] components)
        throws CDFException, ISTPComplianceException {
         

        super(cdf, name, dataType, numElements, numDims, dimSizes,
              recVary, dimVarys, 
              dataType == CDF.CDF_TIME_TT2000 ||
              dataType == CDF.CDF_EPOCH || 
              dataType == CDF.CDF_EPOCH16 ? SUPPORT_DATA : istpType, 
              true, compressionType, compressionParms,
              sparseRecordType);

        if(dataType == CDF.CDF_EPOCH){
           
            setEpochAttribute("VALIDMIN", Epoch8.VALIDMIN);
            setEpochAttribute("VALIDMAX", Epoch8.VALIDMAX);
        }
        else if(dataType == CDF.CDF_EPOCH16){
           
            setEpochAttribute("VALIDMIN", Epoch16.VALIDMIN);
            setEpochAttribute("VALIDMAX", Epoch16.VALIDMAX);
        }
        else if(dataType == CDF.CDF_TIME_TT2000){
           
            setEpochAttribute("VALIDMIN", TerrestrialTime2000.VALIDMIN);
            setEpochAttribute("VALIDMAX", TerrestrialTime2000.VALIDMAX);
        }
                                                      
        setVirtualAttributes(function, components);
    }
 

    /**
     * Creates a virtual variable based upon the given base variable.
     *
     * @param baseVariable variable whose characteristics are to be
     *            copied for the new virtual variable
     * @param name name of new VirtualVariable
     * @param function function to compute virtual values
     * @param components name of components to pass to function
     * @return a virtual variable with the specified characteristics
     * @throws CDFException if a CDFException occurs
     */
    public static VirtualVariable createVirtualVariable(
        Variable baseVariable, String name, String function, 
        String[] components) 
        throws CDFException {

        gsfc.nssdc.cdf.Variable baseCdfVar = 
            baseVariable.getCdfVariable();
                                       // copy of baseVariable's 
                                       // underlying CDF variable
        VirtualVariable newVar = 
            new VirtualVariable(baseCdfVar.copy(name));
                                       // new virtual variable base upon
                                       //  given baseVariable

        newVar.setInitialAttributes(); // reset initial attributes with
                                       // new name
        newVar.setVirtualAttributes(function, components);

        return newVar;
    }


    /**
     * Sets the "virtual variable" unique attributes for this variable.
     *
     * @param function function to compute virtual values
     * @param components name of components to pass to function
     * @throws CDFException if a CDFException occurs
     */
    protected void setVirtualAttributes(String function, 
                                        String[] components) 
        throws CDFException {

        try {

            setAttributeValue("VIRTUAL", "TRUE");
            setAttributeValue("FUNCT", function);

            for (int i = 0; i < components.length; i++) {

                setAttributeValue("COMPONENT_" + i, components[i]);
            }
        }
        catch (ISTPIdentifierException e) {

            System.err.println(
                "gsfc.spdf.istp.VirtualVariable.setVirtualAttributes: " +
                "exception: " + e.getMessage());
        }


    }


    /**
     * Sets an attribute entry value.
     *
     * @param name name of attribute
     * @param value new value of attribute entry
     * @throws CDFException if a CDFException occurs
     */
    protected void setEpochAttribute(String name, Double value)
        throws CDFException {

        try {

            Attribute.create(var.getMyCDF(), name, CDF.VARIABLE_SCOPE);
        }
        catch (CDFException e) {

            // ignore it -- probably already exists
        }

        var.putEntry(name, CDF.CDF_EPOCH, value);
    }
    

    /**
     * Sets an attribute entry value.
     *
     * @param name name of attribute
     * @param value new value of attribute entry
     * @throws CDFException if a CDFException occurs
     */
    protected void setEpochAttribute(String name, double[] value)
        throws CDFException {

        try {

            Attribute.create(var.getMyCDF(), name, CDF.VARIABLE_SCOPE);
        }
        catch (CDFException e) {

            // ignore it -- probably already exists
        }

        var.putEntry(name, CDF.CDF_EPOCH16, value);
    }


    /**
     * Sets an attribute entry value.
     *
     * @param name name of attribute
     * @param value new value of attribute entry
     * @throws CDFException if a CDFException occurs
     */
    protected void setEpochAttribute(String name, Long value)
        throws CDFException {

        try {

            Attribute.create(var.getMyCDF(), name, CDF.VARIABLE_SCOPE);
        }
        catch (CDFException e) {

            // ignore it -- probably already exists
        }

        var.putEntry(name, CDF.CDF_TIME_TT2000, value);
    }
    

    /**
     * Contains the names of all valid virtual variable functions and 
     * their corresponding number of required COMPONENT_x values.
     */
    protected final static TreeMap functions = new TreeMap();

    static {

        functions.put("add_51s", new FunctionAttributes(0, 1));
        functions.put("alternate_view", new FunctionAttributes(-1, 1));
        functions.put("apply_qflag", new FunctionAttributes(2, 2));
        functions.put("calc_p", new FunctionAttributes(0, 2));
        functions.put("comp_themis_epoch", new FunctionAttributes(0,2));
        functions.put("comp_themis_epoch16", new FunctionAttributes(0,2));
        functions.put("compute_magnitude", new FunctionAttributes(0, 1));
        functions.put("conv_pos", new FunctionAttributes(1, 1));
        functions.put("conv_pos1", new FunctionAttributes(1, 1));
        functions.put("conv_pos2", new FunctionAttributes(1, 1));
        functions.put("convert_log10", new FunctionAttributes(2, 1));
        functions.put("create_plain_vis", new FunctionAttributes(2, 2));
        functions.put("create_plmap_vis", new FunctionAttributes(2, 4));
        functions.put("create_vis", new FunctionAttributes(2, 3));
        functions.put("crop_image", new FunctionAttributes(2, 3));
        functions.put("flip_image", new FunctionAttributes(2, 1));
        functions.put("height_isis", new FunctionAttributes(0, 1));
        functions.put("region_filt", new FunctionAttributes(0, 2));
        functions.put("wind_plot", new FunctionAttributes(1, 2));
/* 5/6/15 need dimension and # components info. before un-commenting
        functions.put("conv_hungarian", new FunctionAttributes(?, ?));
        functions.put("conv_map_image", new FunctionAttributes(?, ?));
        functions.put("apply_rtn_qflag", new FunctionAttributes(?, ?));
        functions.put("apply_rtn_cadence", new FunctionAttributes(?, ?));
        functions.put("add_1800", new FunctionAttributes(?, ?));
        functions.put("apply_esa_qflag", new FunctionAttributes(?, ?));
        functions.put("apply_fgm_qflag", new FunctionAttributes(?, ?));
        functions.put("error_bar_array", new FunctionAttributes(?, ?));
        functions.put("convert_toev", new FunctionAttributes(?, ?));
        functions.put("convert_ni", new FunctionAttributes(?, ?));
        functions.put("convert_fast_by", new FunctionAttributes(?, ?));
        functions.put("compute_cadence", new FunctionAttributes(?, ?));
        functions.put("extract_array", new FunctionAttributes(?, ?));
        functions.put("expand_wave_data", new FunctionAttributes(?, 3));
        functions.put("make_stack_array", new FunctionAttributes(1, 1));
        functions.put("fix_sparse", new FunctionAttributes(1, 1));
*/
    }


    /**
     * Determines whether the given CDF variable is an ISTP virtual 
     * variable.
     * 
     * @param var variable to evaluate
     * @return true if the given variable is a virtual variable 
     */
    public static boolean isVirtual(gsfc.nssdc.cdf.Variable var) {

        String virtualAttribute = null;// value of VIRTUAL variable 
                                       // attribute

        try {            

            virtualAttribute = 
                ((String)var.getEntryData("VIRTUAL")).trim();
        }
        catch (CDFException e) {

            return false;
        };

        if (virtualAttribute.equalsIgnoreCase("TRUE")) {

            return true;
        }
        else {

            return false;
        }
    }


    /**
     * Determines whether the given function is a valid virtual 
     * variable function.
     * 
     * @param function name of function to evaluate
     * @return true if the given function is a valid virtual variable 
     *     function.
     */
    public static boolean isValidFunction(String function) {

        return functions.containsKey(function.toLowerCase());
    }


    /**
     * Provides the number of COMPONENT_x values required by the given 
     * function.
     * 
     * @param function identifies the virtual variable function
     * @return number of COMPONENT_x values required by the given 
     *     function.
     */
    public static int getNumberOfComponents(String function) {

        if (function == null) {

            return -1;
        }

        FunctionAttributes attributes = 
            (FunctionAttributes)functions.get(function.toLowerCase());

        if (attributes != null) {

            return attributes.numberOfComponents;
        }
        else {

            return -1;
        }
    }



    /**
     * Provides the variable dimension required by the given function.
     * 
     * @param function identifies the virtual variable function
     * @return variable dimension required by the given function (-1 
     *     if any dimension is applicable).
     */
    public static int getVariableDimension(String function) {

        FunctionAttributes attributes = 
            (FunctionAttributes)functions.get(function.toLowerCase());

        if (attributes != null) {

            return attributes.dimension;
        }
        else {

            return -1;
        }
    }


    /**
     * Provides a vector containing the names of all valid virtual 
     * variable functions.
     * 
     * @return vector containing the names of all valid virtual 
     *     variable functions.
     */
    public static Vector getValidFunctions() {

        Vector validFunctions = new Vector();

        Set keys = functions.keySet(); // functions

        for (Iterator i = keys.iterator(); i.hasNext(); ) {

            validFunctions.addElement(i.next());
        };

        return validFunctions;
    }


    /**
     * Provides a vector containing the possible COMPONENT_x CDF 
     * variables from the given CDF.
     * 
     * @param cdf CDF from which possible COMPONENT_x values are to be 
     *     extracted.
     * @return vector containing the possible COMPONENT_x CDF variables
     */
    public static Vector getCandidateComponentVariables(CDF cdf) {

        Vector candidateVars = cdf.getVariables();

        return candidateVars;
    }


    /**
     * Provides a vector containing the possible COMPONENT_x names
     * from the given CDF.
     * 
     * @param cdf CDF from which possible COMPONENT_x values are to be 
     *     extracted.
     * @return vector containing the possible COMPONENT_x CDF variables
     */
    public static Vector getCandidateComponentNames(CDF cdf) {

        Vector candidateVars = getCandidateComponentVariables(cdf);

        Vector candidateNames = new Vector(candidateVars.size());

        for (int i = 0; i < candidateVars.size(); i++) {

            gsfc.nssdc.cdf.Variable var = 
                (gsfc.nssdc.cdf.Variable)candidateVars.elementAt(i);

            candidateNames.addElement(var.getName());
        };

        return candidateNames;
    }
}
