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
 * $Id: Variable.java,v 1.69 2024/04/22 11:25:47 btharris Exp $
 */
package gsfc.spdf.istp;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.reflect.Array;

import gsfc.nssdc.cdf.Attribute;
import gsfc.nssdc.cdf.CDF;
import gsfc.nssdc.cdf.CDFConstants;
import gsfc.nssdc.cdf.CDFData;
import gsfc.nssdc.cdf.CDFException;
import gsfc.nssdc.cdf.Entry;

import gsfc.spdf.cdf.PadValue;
import gsfc.spdf.cdf.SparseRecordType;


/**
 * This class represents an International Solar-Terrestrial Physics 
 * (ISTP) specialized CDF variable.  ISTP variables have specific 
 * attributes and conform to the 
 * <a href="http://spdf.gsfc.nasa.gov/sp_use_of_cdf.html">
 * ISTP guidelines for CDF</a>.
 * 
 * @author B. Harris
 * @version $Revision: 1.69 $
 */
public class Variable {

    /**
     * The CDF variable associated with this ISTP CDF variable.
     */
    protected gsfc.nssdc.cdf.Variable var = null;


    //
    // The following are ISTP variable type (varible attribute VAR_TYPE)
    // values.
    //
    /**
     * Integer or real numbers that are plottable.
     */
    public static final String DATA = "data";

    /**
     * Integer or real "attached" variables.
     */
    public static final String SUPPORT_DATA = "support_data";

    /**
     * Labels or character variables.
     */
    public static final String METADATA = "metadata";

    /**
     * Placeholder variables.
     */
    public static final String IGNORE_DATA = "ignore_data";


    /**
     * The names of variable compression algorithms.  The names are
     * ordered such that their index in the List corresponds to their
     * location in the COMPRESSION_TYPE array.
     */
    public static final List<String> COMPRESSION_ALGORITHM_NAMES;

    static {

        COMPRESSION_ALGORITHM_NAMES = new ArrayList<String>();

        COMPRESSION_ALGORITHM_NAMES.add("No compression");
        COMPRESSION_ALGORITHM_NAMES.add("Run-length encoding");
        COMPRESSION_ALGORITHM_NAMES.add("Huffman");
        COMPRESSION_ALGORITHM_NAMES.add("Adaptive Huffman");
        COMPRESSION_ALGORITHM_NAMES.add("gzip");
    }


    /**
     * The contant values of the CDF compression types.  The values
     * are ordered such that their index in the array corresponds to
     * the name of the algorithm in the COMPRESSION_ALGORITHM_NAMES
     * List.
     */
    public static final long[] COMPRESSION_TYPES = new long[] {
        CDF.NO_COMPRESSION, 
        CDF.RLE_COMPRESSION,
        CDF.HUFF_COMPRESSION,
        CDF.AHUFF_COMPRESSION,
        CDF.GZIP_COMPRESSION
    };


    /**
     * Default compression parameters (only applicable to gzip).
     */
    public static final long[] DEFAULT_GZIP_COMPRESSION_PARMS =
        new long[] {6};


    /**
     * Gets the name of the compression algorithm corresponding to
     * the specified CDF compression type value.
     *
     * @param type CDF compression type constant value.
     * @return the name of the compression algorithm corresponding to
     *             the specified CDF compression type value.  null if
     *             specified type value is invalid.
     * @see #getCompressionType(String)
     */
    public static String getCompressionName(long type) {

        for (int i = 0; i < COMPRESSION_TYPES.length; i++) {

            if (COMPRESSION_TYPES[i] == type) {

                return COMPRESSION_ALGORITHM_NAMES.get(i);
            }
        }

        return null;
    }


    /**
     * Gets the compression type constant corresponding to the 
     * specified compression name value.
     *
     * @param name compression name value.
     * @return the compression type constant value corresponding to
     *             the specified compression name value.
     * @see #getCompressionName(long)
     */
    public static long getCompressionType(String name) {

        int index = COMPRESSION_ALGORITHM_NAMES.indexOf(name);

        if (index == -1) {

            return CDF.NO_COMPRESSION;
        }
        else {

            return COMPRESSION_TYPES[index];
        }
    }


    /**
     * Creates an ISTP variable of the given CDF variable.  The caller
     * is responsible for setting the variable's attributes correctly.
     *
     * <p><b>Note:</b>This method is public to to aid in the 
     * introduction of this gsfc.spdf.istp.Variable class hiararchy 
     * into an existing codebase.  The method is expected to become 
     * protected or eliminated in the future.
     *
     * @param var a CDF variable
     */
    public Variable(gsfc.nssdc.cdf.Variable var) {

        this.var = var;
    }


    /**
     * Creates a variable with the given characteristics.
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
     * @param linearScale indicates whether the variable has a linear 
     *            scale
     * @param compressionType the compression type.
     * @param compressionParms the compression parameters that go with 
     *            the specified type.  Maybe null for compression types
     *            that do not have parameters.  If null or zero-length
     *            for a compression type that requires parameters,
     *            default values will be applied.
     * @param sparseRecordType the sparse record type.
     * @return the new Variable.
     * @throws CDFException if a CDFException occurs
     * @throws ISTPComplianceException if the given characteristics fail
     *             to be ISTP compliant
     */
    public static Variable getInstance(
        final gsfc.nssdc.cdf.CDF cdf, 
        final String name, final long dataType, long numElements, 
        final long numDims, final long[] dimSizes, final long recVary, 
        final long[] dimVarys, final String istpType, 
        final boolean linearScale, 
        final long compressionType, final long[] compressionParms,
        final SparseRecordType sparseRecordType) 
        throws CDFException, ISTPComplianceException {

        checkName(cdf, name);

        if (dataType == CDF.CDF_EPOCH) {

            return new Epoch8(cdf, name, numDims, dimSizes, recVary);
        }
        else if (dataType == CDF.CDF_EPOCH16) {

            return new Epoch16(cdf, name, numDims, dimSizes, recVary);
        }
        else if (dataType == CDF.CDF_TIME_TT2000) {

            return new TerrestrialTime2000(cdf, name, numDims, dimSizes, 
                           recVary);
        }
        else {

            if (istpType.equals(METADATA)) {

                if (dataType != CDF.CDF_CHAR) {

                    throw new ISTPComplianceException(
                        "Illegal data type for variable " + name + 
                        ". VAR_TYPE = " + istpType + 
                        " must have a data type of CDF_CHAR");
                }

                numElements = Math.max(numElements,
                                       getDefaultMetadataValueLength(
                                           name, linearScale));
            }

            return new Variable(cdf, name, dataType, numElements, 
                                numDims, dimSizes, recVary, dimVarys, 
                                istpType, linearScale,
                                compressionType, compressionParms,
                                sparseRecordType);
        }
    }


    /**
     * Creates a variable with the given characteristics.
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
     * @param linearScale indicates whether the variable has a linear 
     *            scale
     * @param compressionType the compression type.
     * @param compressionParms the compression parameters that go with 
     *            the specified type.  Maybe null for compression types
     *            that do not have parameters.  If null or zero-length
     *            for a compression type that requires parameters,
     *            default values will be applied.
     * @param sparseRecordType the sparse record type.
     * @throws CDFException if a CDFException occurs.
     * @throws ISTPComplianceException if the given characteristics fail
     *             to be ISTP compliant.
     */
    protected Variable(
        gsfc.nssdc.cdf.CDF cdf, String name, long dataType,
        long numElements, long numDims, long[] dimSizes,
        long recVary, long[] dimVarys, String istpType,
        boolean linearScale, 
        long compressionType, long[] compressionParms,
        SparseRecordType sparseRecordType) 
        throws CDFException, ISTPComplianceException {

        checkName(cdf, name);

        var = gsfc.nssdc.cdf.Variable.create(
                  cdf, name, dataType, numElements, numDims, 
                  dimSizes, recVary, dimVarys);

        try {

            setCompression(compressionType, compressionParms);
            setSparseRecordType(sparseRecordType);

            setInitialAttributes();
        
            setType(isTimeType(dataType) ? SUPPORT_DATA : istpType);

            if (dataType != CDF.CDF_CHAR && 
                dataType != CDF.CDF_UCHAR && linearScale) {

                setAttributeValue("SCALETYP", "linear");
            }

            // subclasses should set other appropriate ISTP variable 
            // attribute values

            setDefaultValues(linearScale);
            setDefaultValidMin();
            setDefaultValidMax();
        }
        catch (OutOfMemoryError e) {

            var.delete();
            throw e;
        }
    }


    /**
     * Checks the given variable name (case-insensitive comparison) 
     * against the names of existing variables in the given CDF and
     * against other ISTP variable naming restrictions.  The specified
     * name should not be that of an existing variable.  If the 
     * specified name is that of an existing variable, use
     * {@link #checkName(gsfc.nssdc.cdf.CDF, gsfc.nssdc.cdf.Variable)}
     * instead of this method.
     *
     * @param cdf CDF file to check the variable name against
     * @param name the name of a variable to check
     * @throws ISTPIdentifierException if the given name already 
     *             exists in the given CDF or violates other ISTP 
     *             variable naming restrictions
     * @see #checkName(gsfc.nssdc.cdf.CDF, gsfc.nssdc.cdf.Variable)
     */
    public static void checkName(gsfc.nssdc.cdf.CDF cdf, String name)
        throws ISTPIdentifierException {

        @SuppressWarnings("rawtypes")
        Vector variables = cdf.getVariables();
                                       // all varibles in cdf

        for (int i = 0; i < variables.size(); i++) {

            gsfc.nssdc.cdf.Variable var = 
                (gsfc.nssdc.cdf.Variable)variables.elementAt(i);
                                       // i'th variable

            if (var.getName().equalsIgnoreCase(name)) {

                throw new ISTPIdentifierException(
                    "Illegal variable name.  Variable '" + name + 
                    "' already exists in CDF as '" + var.getName() + 
                    "'.  Variable names must be distinct in more than case.",
                    name, var.getName());
            }
        }

        try {

            checkIdlName(name);
        }
        catch (ISTPIdentifierException e) {

            throw new ISTPIdentifierException(
                "Illegal variable name.  " + e.getMessage(), name);
        }
    }


    /**
     * Checks that the given variable does not violation any ISTP 
     * variable naming restrictions.
     *
     * @param cdf CDF file to check the variable against
     * @param var variable to check
     * @throws ISTPIdentifierException if the given name already 
     *             exists in the given CDF or violates other ISTP 
     *             variable naming restrictions
     */
    public static void checkName(gsfc.nssdc.cdf.CDF cdf, 
                                 gsfc.nssdc.cdf.Variable var)
        throws ISTPIdentifierException {

        @SuppressWarnings("rawtypes")
        Vector variables = cdf.getVariables();
                                       // all variables in cdf

        for (int i = 0; i < variables.size(); i++) {

            gsfc.nssdc.cdf.Variable var2 = 
                (gsfc.nssdc.cdf.Variable)variables.elementAt(i);
                                       // i'th variable

            if (var.getID() != var2.getID() &&
                var.getName().equalsIgnoreCase(var2.getName())) {

                throw new ISTPIdentifierException(
                    "Illegal variable name.  Variable " + 
                    var.getName() + " already exists in CDF as " + 
                    var2.getName() + ".", var.getName(), 
                    var2.getName());
            }
        }

        try {

            checkIdlName(var.getName());
        }
        catch (ISTPIdentifierException e) {

            throw new ISTPIdentifierException(
                "Illegal variable name.  " + e.getMessage(),
                var.getName(), null);
        }
    }


    /**
     * Checks that the given variable can be renamed to the given
     * name without violating any ISTP variable naming restrictions.
     *
     * @param var variable that is to be renamed
     * @param name new name for variable
     * @throws ISTPIdentifierException if the given name already 
     *             exists in the given CDF or violates other ISTP 
     *             variable naming restrictions
     */
    public static void checkName(gsfc.nssdc.cdf.Variable var, 
                                 String name)
        throws ISTPIdentifierException {

        @SuppressWarnings("rawtypes")
        Vector variables = var.getMyCDF().getVariables();
                                       // all variables in the CDF
                                       //  containing var

        for (int i = 0; i < variables.size(); i++) {

            gsfc.nssdc.cdf.Variable var2 = 
                (gsfc.nssdc.cdf.Variable)variables.elementAt(i);
                                       // i'th variable

            if (var.getID() != var2.getID() &&
                name.equalsIgnoreCase(var2.getName())) {

                throw new ISTPIdentifierException(
                    "Illegal variable name.  Variable " + 
                    name + " already exists in CDF as " + 
                    var2.getName() + ".", name, var2.getName());
            }
        }

        try {

            checkIdlName(name);
        }
        catch (ISTPIdentifierException e) {

            throw new ISTPIdentifierException(
                "Illegal variable name.  " + e.getMessage(),
                name, null);
        }
    }



    /**
     * Checks the given name for IDL identifier name restrictions.
     *
     * @param name name to check
     * @throws ISTPIdentifierException if the given name violates
     *             IDL identifier naming restrictions
     */
    public static void checkIdlName(String name) 
        throws ISTPIdentifierException {

        String error = Idl.validateIdentifier(name);
                                       // error message

        if (error != null) {

            throw new ISTPIdentifierException(
                "Invalid ISTP Identifier: " + error, name);
        }
    }


    /**
     * Determines if the given variable is the target of any DEPEND_0
     * variable attribute.  That is, does the name of the given variable
     * appear as the value of a DEPEND_0 attribute on any variable.
     *
     * @param cdf the CDF to search
     * @param var the variable to test
     * @return true if the name of the given variable is found in the
     *     DEPEND_0 attribute of any variable, otherwise false
     */
    public static boolean isDepend0Target(gsfc.nssdc.cdf.CDF cdf, 
                                          gsfc.nssdc.cdf.Variable var) {

        String varName = var.getName();// variable's name
        Attribute depend0Attribute = null;
                                       // DEPEND_0 variable attribute

        try {

            depend0Attribute = cdf.getAttribute("DEPEND_0");
        }
        catch (CDFException e) {

            return false;
        }

        try {

            Vector depend0Entries = depend0Attribute.getEntries();
                                       // all DEPEND_0 entries (values)

            for (int i = 0; i < depend0Entries.size(); i++) {

                Entry depend0Entry = (Entry)depend0Entries.elementAt(i);
                                       // i'th DEPEND_0 entry

                try {

                    if (varName.equalsIgnoreCase(
                                 (String)depend0Entry.getData())) {
    
                        return true;
                    }
                }
                catch (ClassCastException e) {

                    // entry data is not a String so its clearly not equal 
                    // to varName so continue looking
                }
            }
        }
        catch (CDFException e) {

            // not found
        }

        return false;
    }


    /**
     * Creates a virtual variable based upon this variable.
     *
     * @param name name of new variable
     * @param function function to compute virtual values
     * @param components name of components to pass to function
     * @return the new VirtualVariable
     * @throws CDFException if a CDFException occurs
     */
    public VirtualVariable createVirtualVariable(
        String name, String function, String[] components) 
        throws CDFException {

        return VirtualVariable.createVirtualVariable(this, name, 
                   function, components);
    }


    /**
     * Creates a DELTA_PLUS_VAR variable for this variable.  If
     * one already exists, it is replaced by the newly created
     * one.  The variable that is created is a record varying
     * support_data variable of the same dimensionality and CDF 
     * data-type as this variable.  If different characteristics 
     * are desired, use {@link #getInstance} and 
     * {@link #setDeltaPlusVar(Variable)} instead of this method.
     *
     * @param name the name of the new variable.  If null, a name
     *            is created by concatonating the name of this
     *            variable with " Delta Plus"
     * @return new DELTA_PLUS_VAR variable
     * @throws CDFException if a CDFException occurs
     * @throws ISTPComplianceException if the given characteristics fail
     *             to be ISTP compliant
     */
    public Variable createDeltaPlusVar(String name)
        throws CDFException, ISTPComplianceException {

        if (name == null) {

            name = var.getName() + "_Delta_Plus";
        }

        Variable deltaPlusVar =
            getInstance(var.getMyCDF(), name, var.getDataType(),
                        var.getNumElements(), var.getNumDims(),
                        var.getDimSizes(), CDF.VARY,
                        var.getDimVariances(), SUPPORT_DATA,
                        true, CDF.NO_COMPRESSION, new long [] {},
                        SparseRecordType.NONE);

        setDeltaPlusVar(deltaPlusVar);

        return deltaPlusVar;
    }


    /**
     * Creates a DELTA_MINUS_VAR variable for this variable.  If
     * one already exists, it is replaced by the newly created
     * one.  The variable that is created is a non-record varying
     * support_data variable of the same dimensionality and CDF 
     * data-type as this variable.  If different characteristics 
     * are desired, use {@link #getInstance} and 
     * {@link #setDeltaPlusVar(Variable)} instead of this method.
     *
     * @param name the name of the new variable.  If null, a name
     *            is created by concatonating the name of this
     *            variable with " Delta Minus"
     * @return new DELTA_MINU_VAR variable
     * @throws CDFException if a CDFException occurs
     * @throws ISTPComplianceException if the given characteristics fail
     *             to be ISTP compliant
     */
    public Variable createDeltaMinusVar(String name)
        throws CDFException, ISTPComplianceException {

        if (name == null) {

            name = var.getName() + "_Delta_Minus";
        }

        Variable deltaMinusVar =
            getInstance(var.getMyCDF(), name, var.getDataType(),
                        var.getNumElements(), var.getNumDims(),
                        var.getDimSizes(), CDF.VARY,
                        var.getDimVariances(), SUPPORT_DATA,
                        true, CDF.NO_COMPRESSION, new long [] {},
                        SparseRecordType.NONE);

        setDeltaMinusVar(deltaMinusVar);

        return deltaMinusVar;
    }


    /**
     * Gets the DELTA_PLUS_VAR variable.
     *
     * @return DELTA_PLUS_VAR variable
     * @see #setDeltaPlusVar
     */
    public Variable getDeltaPlusVar() {

        String[] deltaVarName = getCharAttributeValue("DELTA_PLUS_VAR");
        if (deltaVarName != null && deltaVarName.length > 0) {

            return getVariable(deltaVarName[0]);
        }
        else {

            return null;
        }
//        return getVariable(getCharAttributeValue("DELTA_PLUS_VAR"));
    }


    /**
     * Sets the DELTA_PLUS_VAR variable.
     *
     * @param var new DELTA_PLUS_VAR variable
     * @throws CDFException if a CDFException occurs
     * @see #getDeltaPlusVar
     */
    public void setDeltaPlusVar(Variable var) 
        throws CDFException {

        try {

            setAttributeValue("DELTA_PLUS_VAR", var.getName());
        }
        catch (ISTPIdentifierException e) {

            System.err.println(
                "gsfc.spdf.istp.Variable.setDeltaPlusVar: " +
                "exception: " + e.getMessage());
        }
    }


    /**
     * Sets the DELTA_PLUS_VAR variable.
     *
     * @param var new DELTA_PLUS_VAR variable
     * @deprecated As of SKTEditor 1.2, use 
     * {@link #setDeltaPlusVar(Variable)}
     * @throws CDFException if a CDFException occurs
     * @see #getDeltaPlusVar()
     */
    @Deprecated
    public void setDeltaPlusVar(gsfc.nssdc.cdf.Variable var) 
        throws CDFException {

        try {
             
            setAttributeValue("DELTA_PLUS_VAR", var.getName());
        }
        catch (ISTPIdentifierException e) {

            System.err.println(
                "gsfc.spdf.istp.Variable.setDeltaPlusVar: " +
                "exception: " + e.getMessage());
        }
    }


    /**
     * Gets the DELTA_MINUS_VAR variable.
     *
     * @return DELTA_MINUS_VAR variable
     * @see #setDeltaMinusVar
     */
    public Variable getDeltaMinusVar() {

        String[] deltaVarName = getCharAttributeValue("DELTA_MINUS_VAR");
        if (deltaVarName != null && deltaVarName.length > 0) {

            return getVariable(deltaVarName[0]);
        }
        else {

            return null;
        }
//        return getVariable(getCharAttributeValue("DELTA_MINUS_VAR"));
    }


    /**
     * Sets the DELTA_MINUS_VAR variable.
     *
     * @param var new DELTA_MINUS_VAR variable
     * @throws CDFException if a CDFException occurs
     * @see #getDeltaMinusVar
     */
    public void setDeltaMinusVar(Variable var) 
        throws CDFException {

        try {
            
            setAttributeValue("DELTA_MINUS_VAR", var.getName());

        }
        catch (ISTPIdentifierException e) {

            System.err.println(
                "gsfc.spdf.istp.Variable.setDeltaMinusVar: " +
                "exception: " + e.getMessage());
        }
    }


    /**
     * Sets the DELTA_MINUS_VAR variable.
     *
     * @param var new DELTA_MINUS_VAR variable
     * @deprecated As of SKTEditor 1.2, use 
     * {@link #setDeltaMinusVar(Variable)}
     * @throws CDFException if a CDFException occurs
     * @see #getDeltaMinusVar()
     */
    @Deprecated
    public void setDeltaMinusVar(gsfc.nssdc.cdf.Variable var) 
        throws CDFException {

        try {
            
            setAttributeValue("DELTA_MINUS_VAR", var.getName());
        }
        catch (ISTPIdentifierException e) {

            System.err.println(
                "gsfc.spdf.istp.Variable.setDeltaMinusVar: " +
                "exception: " + e.getMessage());
        }
    }


    /**
     * Gets the variable with the specified name from the CDF that this
     * variable is contained in.
     *
     * @param name name of variable to get
     * @return specified variable or null if not found
     */
    public Variable getVariable(String name) {

        if (name == null || var == null) {

            return null;
        }
        try {

            gsfc.nssdc.cdf.Variable getVar =
                var.getMyCDF().getVariable(name);
                                       // specified CDF variable
            return new Variable(getVar);
        }
        catch (CDFException e) {

            return null;
        }
    }


    /**
     * Renames this variable to the given new name.  Also, any 
     * references to this variable in the attributes of other variables
     * are changed to refer to this variable by its new name.
     *
     * @param newName new name
     * @return this renamed variable
     * @throws CDFException if a CDFException occurs
     * @throws ISTPComplianceException if the given name already exists
     *             in the given CDF or violates other ISTP variable
     *             naming restrictions
     */
    public Variable rename(String newName) 
        throws CDFException, ISTPComplianceException {

        checkName(var, newName);

        String oldName = var.getName();// existing name of variable

        var.rename(newName);

//        setDefaultFieldnamCatdescLablaxis();

        //
        // Change any references to the oldName in all ISTP pointer
        // variable attributes.
        //
        @SuppressWarnings("rawtypes")
        Vector variables = var.getMyCDF().getVariables();
                                       // all variables
        Iterator varIter = variables.iterator();
                                       // variable iterator
        while (varIter.hasNext()) {

            gsfc.nssdc.cdf.Variable variable =
                (gsfc.nssdc.cdf.Variable)varIter.next();
                                       // a specific variable

            if (variable.getID() != var.getID()) {

                Vector variableAttributes = variable.getAttributes();
                                       // this variable's attributes
                Iterator attrIter = variableAttributes.iterator();
                                       // attribute iterator
                while (attrIter.hasNext()) {

                    Attribute variableAttribute =
                        (Attribute)attrIter.next();
                                       // a specific variable attribute

                    if (VariableAttribute.isIstpPointer(
                            variableAttribute)) {

                        Entry entry =
                            variableAttribute.getEntry(variable);
                                       // variable attribute's entry

                        if (entry != null) {

                            Object entryData = entry.getData();
                                       // the entry's value

                            if (entryData instanceof String) {

                                String entryStrValue = (String)entryData;
                                       // entry's string value

                                if (entryStrValue.equals(oldName)) {

                                    entry.putData(CDF.CDF_CHAR, newName);
                                }
                            }
                        }
                    }
                }
            }
        }

        return this;
    }


    /**
     * Creates a copy of this variable with the given name.  The new
     * variable has the same characteristics (i.e., data type, 
     * dimensionality, ...) and all attribute values except FIELDNAM and
     * CATDESC (FIELDNAM == CATDESC == new name) but not a copy of the 
     * actual data values.
     *
     * @param name name of new variable
     * @return a copy of this variable
     * @throws CDFException if a CDFException occurs
     * @see #duplicate
     */
    public Variable copy(String name) 
        throws CDFException {

        Variable copy = new Variable(var.copy(name));
                                       // copy of this variable

//        copy.setDefaultFieldnamCatdescLablaxis();

        return copy;
    }


    /**
     * Sets the values of this variable's FIELDNAM, CATDESC and LABLAXIS
     * attributes to the value of its name.
     *
     * @throws CDFException if a CDFException occurs
     */
    public void setDefaultFieldnamCatdescLablaxis() 
        throws CDFException {

        setDefaultFieldnam();
        setDefaultCatdesc();
        setDefaultLablaxis();
    }


    /**
     * Sets the value of this variable's FIELDNAM attribute to the value
     * of its name.
     *
     * @throws CDFException if a CDFException occurs
     */
    public void setDefaultFieldnam()
        throws CDFException {

        setFieldnam(var.getName());
    }


    /**
     * Gets the value of this variable's FIELDNAM attribute.
     *
     * @return value of FIELDNAM attribute.
     * @throws CDFException if a CDFException occurs
     * @see #setFieldnam
     */
    public String[] getFieldnam()
        throws CDFException {

        return getCharAttributeValue("FIELDNAM");
    }


    /**
     * Sets the value of this variable's FIELDNAM attribute to the 
     * specified value.
     *
     * @param value new value for FIELDNAM attribute.
     * @throws CDFException if a CDFException occurs
     * @see #getFieldnam
     */
    public void setFieldnam(String value)
        throws CDFException {

        try {

            setAttributeValue("FIELDNAM", value);
        }
        catch (ISTPIdentifierException e) {

            System.err.println(
                "gsfc.spdf.istp.Variable.setFieldnam: " +
                "exception: " + e.getMessage());
        }
    }


    /**
     * Sets the value of this variable's CATDESC attribute to the value
     * of its name.
     *
     * @throws CDFException if a CDFException occurs
     */
    public void setDefaultCatdesc()
        throws CDFException {

        setCatdesc(var.getName());
    }


    /**
     * Gets the value of this variable's CATDESC attribute.
     *
     * @return value of CATDESC attribute.
     * @throws CDFException if a CDFException occurs
     * @see #setCatdesc
     */
    public String[] getCatdesc()
        throws CDFException {

        return getCharAttributeValue("CATDESC");
    }


    /**
     * Sets the value of this variable's CATDESC attribute to the 
     * specified value.
     *
     * @param value new value for CATDESC attribute.
     * @throws CDFException if a CDFException occurs
     * @see #getCatdesc
     */
    public void setCatdesc(String value)
        throws CDFException {

        try {

            setAttributeValue("CATDESC", value);
        }
        catch (ISTPIdentifierException e) {

            System.err.println(
                "gsfc.spdf.istp.Variable.setCatdesc: " +
                "exception: " + e.getMessage());
        }
    }


    /**
     * Sets the value of this variable's LABLAXIS attribute to the value
     * of its name.
     *
     * @throws CDFException if a CDFException occurs
     */
    public void setDefaultLablaxis()
        throws CDFException {

        setLablaxis(var.getName());
    }


    /**
     * Gets the value of this variable's LABLAXIS attribute.
     *
     * @return value of LABLAXIS attribute.
     * @throws CDFException if a CDFException occurs
     * @see #setLablaxis
     */
    public String[] getLablaxis()
        throws CDFException {

        return getCharAttributeValue("LABLAXIS");
    }


    /**
     * Sets the value of this variable's LABLAXIS attribute to the 
     * specified value.
     *
     * @param value new value for LABLAXIS attribute.
     * @throws CDFException if a CDFException occurs
     * @see #getLablaxis
     */
    public void setLablaxis(String value)
        throws CDFException {

        try {

            setAttributeValue("LABLAXIS", value);
        }
        catch (ISTPIdentifierException e) {

            System.err.println(
                "gsfc.spdf.istp.Variable.setLablaxis: " +
                "exception: " + e.getMessage());
        }
    }


    /**
     * Determines whether LABL_PTR_i attributes are allowed for this
     * variable.
     *
     * @return true if LABL_PTR_i attributes are allowed for this
     *     variable.  false if LABL_PTR_i attributes are not allowed.
     */
    public boolean lablPtrIsAllowed() {

        String displayType = null;     // variable's DISPLAY_TYPE
        try {

            DisplayType fullDisplayType = getDisplayType();

            if (fullDisplayType != null) {

                displayType = fullDisplayType.getType();
            }
            else {

                return true;
            }
        }
        catch (ISTPComplianceException e) {

            return true;
        }

        long numDims = getNumDims();   // variable's dimensionality

        return !((displayType.equals(DisplayType.STACK_PLOT_TYPE) && 
                  numDims == 1) || 
                 (displayType.equals(DisplayType.TIME_SERIES_TYPE) && 
                  numDims == 0) ||
                 (displayType.equals(DisplayType.SPECTROGRAM_TYPE) && 
                  numDims == 1) ||
                 (displayType.equals(DisplayType.IMAGE_TYPE)) ||
                 (displayType.equals(DisplayType.PLASMAGRAM_TYPE)));
    }


    /**
     * Gets this variable's LABL_PTR_i attribute variable.
     *
     * @param i LABL_PTR index/identifier
     * @return LABL_PTR_i attribute target variable.
     * @throws CDFException if a CDFException occurs
     * @see #setLablPtrVariable
     */
    public Variable getLablPtrVariable(int i)
        throws CDFException {

        Variable targetVariable = null;// variable pointed to by
                                       // LABL_PTR_i attribute

        String targetVarName = (String)
            getAttributeValue("LABL_PTR_" + i);

        if (targetVarName != null) {

            try {

                targetVariable = 
                    new Variable(
                        var.getMyCDF().getVariable(targetVarName));
            }
            catch (CDFException e) {

                // probably does not exist
            }
        }

        return targetVariable;
    }


    /**
     * Sets the value of this variable's LABL_PTR_i attribute to the 
     * specified variable.
     *
     * @param i LABL_PTR index/identifier
     * @param var variable for LABL_PTR_i attribute.
     * @throws CDFException if a CDFException occurs
     * @see #getLablPtrVariable
     */
    public void setLablPtrVariable(int i, Variable var)
        throws CDFException {

        try {

            setAttributeValue("LABL_PTR_" + i, var.getName());
        }
        catch (ISTPIdentifierException e) {

            System.err.println(
                "gsfc.spdf.istp.Variable.setLablPtr: " +
                "exception: " + e.getMessage());
        }
    }


    /**
     * Gets this variable's LABL_PTR_i attribute target value.
     *
     * @param i LABL_PTR index/identifier
     * @return LABL_PTR_i attribute target value.
     * @throws CDFException if a CDFException occurs
     * @see #setLablPtrTargetValue
     */
    public String getLablPtrTargetValue(int i)
        throws CDFException {

        Variable targetVariable = getLablPtrVariable(i);
                                       // variable pointed to by
                                       // LABL_PTR_i attribute
        if (targetVariable != null) {

            return (String)
                (targetVariable.getRecordObject(0L).getData());
        }

        return null;
    }


    /**
     * Sets the value of this variable's LABL_PTR_i attribute target
     * value.
     *
     * @param i LABL_PTR index/identifier
     * @param value new value for LABL_PTR_i attribute target.
     * @throws CDFException if a CDFException occurs
     * @see #getLablPtrTargetValue
     */
    public void setLablPtrTargetValue(int i, String value)
        throws CDFException {

        Variable targetVariable = getLablPtrVariable(i);
                                       // variable pointed to by
                                       // LABL_PTR_i attribute
        if (targetVariable != null) {

            targetVariable.putRecord(value);
        }
    }


    /**
     * Gets this variable's label values.  In the case of LABLAXIS,
     * the Object returned is the String value (or null if no value
     * is found).  In the case of LABL_PTR_i, the Object[i - 1]
     * returned is the pointer Variable containing the label values 
     * (or null if the Variable is not found).
     *
     * @return array of this variable's label values.
     */
    public Object[] getLabels() {

        int numLabels = (int)getNumDims();
                                       // number of labels
        if (numLabels == 0) numLabels = 1;

        Object[] labels = new Object[numLabels];
                                       // labels to return
        DisplayType displayType = null;
                                       // variable's display type
        try {

            displayType = getDisplayType();
        }
        catch (ISTPComplianceException e) {

            // ignore it
        }

        try {

            if (displayType != null && lablPtrIsAllowed()) {

                labels[0] = getLablPtrVariable(1);
                // Return the "cdf" variable until callers of this
                // method can be changed to work with istp variable
                if (labels[0] != null) {

                    labels[0] = ((Variable)labels[0]).getCdfVariable();
                }
            }
            if (labels[0] == null) {

                labels[0] = getLablaxis();
            }
        }
        catch (CDFException e) {

            // ignore it
        }


        for (int i = 2; i <= numLabels; i++) {

            try {

                labels[i - 1] = getLablPtrVariable(i);
                // Return the "cdf" variable until callers of this
                // method can be changed to work with istp variable
                if (labels[i - 1] != null) {

                    labels[i - 1] = ((Variable)labels[i - 1]).
                                        getCdfVariable();
                }
            }
            catch (CDFException e) {

                // ignore it
            }
        }

        return labels;
    }


    /**
     * Creates a duplicate of this variable with the given name.  The 
     * new variable has the same characteristics (i.e., data type, 
     * dimensionality, ...) and all attribute values except FIELDNAM 
     * and CATDESC (FIELDNAM == CATDESC == new name).  The new variable
     * also has duplicates of all data records.
     *
     * @param name name of new variable
     * @return a copy of this variable
     * @throws CDFException if a CDFException occurs
     * @see #copy
     */
    public Variable duplicate(String name) 
        throws CDFException {

        Variable copy = new Variable(var.duplicate(name));
                                       // copy of this variable

//        copy.setDefaultFieldnamCatdescLablaxis();

        return copy;
    }


    /**
     * Conformation callback class whose 
     * {@link DeleteReferenceAttributeConfirmer#confirmAttributeDelete}
     * method is called for every variable attribute of another 
     * variable that contains the name of the variable that is about 
     * to be deleted.
     */
    public static interface DeleteReferenceAttributeConfirmer {

        /**
         * Method that is called for every variable attribute of
         * another variable that contains the name of the variable
         * that is about to be deleted.
         *
         * @param variable the variable which has an attribute
         *            containing the name of the variable that is
         *            about to be deleted
         * @param attribute the attribute containing the name of the
         *            variable that is about to be deleted
         * @return true if the given attribute is also to be deleted.
         *             false if the attribute is to be retained.
         */
        public boolean confirmAttributeDelete(Variable variable,
                                              Attribute attribute);
    }


    /**
     * Default conformation callback class which always responds
     * that the attribute should be deleted.
     */
    public static class DefaultDeleteReferenceAttributeConfirmer 
        implements DeleteReferenceAttributeConfirmer {

        /**
         * Detault implementation which always return a value
         * indicating that the attribute should be deleted.
         *
         * @param variable the variable which has an attribute
         *            containing the name of the variable that is
         *            about to be deleted
         * @param attribute the attribute containing the name of the
         *            variable that is about to be deleted
         * @return true always.
         */
        public boolean confirmAttributeDelete(Variable variable,
                                              Attribute attribute) {

            return true;
        }
    }


    /**
     * Deletes this variable.  Also, calls the given
     * referenceAttributeConfirmer.confirmAttributeDelete for every
     * variable attribute of another variable which contains the
     * name of this variable.  Any attempt to perform subsequent 
     * operations on this object will fail.
     *
     * @param referenceAttributeConfirmer object whose 
     *            confirmAttributeDelete method is to be called for
     *            every attribute of another variable which is found
     *            to contain the name of this variable to confirm
     *            whether that attribute should also be deleted
     * @throws CDFException if a CDFException occurs
     */
    public void delete(DeleteReferenceAttributeConfirmer 
                       referenceAttributeConfirmer)
        throws CDFException {

        String name = var.getName();   // name of this variable

        //
        // search all variables for attributes refering to this variable
        //
        @SuppressWarnings("rawtypes")
        Vector variables = var.getMyCDF().getVariables();
                                       // all variables
        Iterator varIter = variables.iterator();
                                       // variable iterator
        while (varIter.hasNext()) {

            gsfc.nssdc.cdf.Variable variable = 
                (gsfc.nssdc.cdf.Variable)varIter.next();
                                       // a specific variable

            if (variable.getID() != var.getID()) {

                Vector variableAttributes = variable.getAttributes();
                                       // this variable's attributes
                Iterator attrIter = variableAttributes.iterator();
                                       // attribute iterator
                while (attrIter.hasNext()) {

                    Attribute variableAttribute = 
                        (Attribute)attrIter.next();
                                       // a specific variable attribute

                    Entry entry = 
                            variableAttribute.getEntry(variable);
                                       // variable attribute's entry

                    if (entry != null) {

                        Object entryData = entry.getData();
                                       // the entry's value

                        if (entryData instanceof String &&
                            ((String)entryData).equals(name)) {

                            if (VariableAttribute.isIstpPointer(
                                    variableAttribute)) {

                                // we do not want ISTP pointers pointing
                                // to non-existent variables
                                //
                                entry.delete();
                            }
                            else {
                                // we will allow non-ISTP pointers to
                                // point to non-existent variables if 
                                // the user wants it
                                //
                                if (referenceAttributeConfirmer.
                                    confirmAttributeDelete(
                                        new Variable(variable),
                                        variableAttribute)) {

                                    entry.delete();
                                }
                            } // if not istp pointer
                        } // entryData == name
                    } // entry != null
                } // for attributes
            } // not variable that is to be deleted
        } // for variables

        var.delete();
        var = null;
    }


    /**
     * Sets initial attribute values.
     *
     * @throws CDFException if a CDFException occurs
     */
    public void setInitialAttributes()
        throws CDFException {

        setDefaultFieldnamCatdescLablaxis();

        setDefaultFillval();
        setDefaultFormat();
    }


    /**
     * Sets default initial values.
     *
     * @param linearScale indicates whether the variable has a linear 
     *            scale
     * @throws CDFException if a CDFException occurs
     * @see #getDefaultValues
     */
    public void setDefaultValues(boolean linearScale)
        throws CDFException {

        Object defaultValues = getDefaultValues(linearScale);
                                       // default values

        if (defaultValues != null) {

            putRecord(defaultValues);
        }
    }


    /**
     * Gets the default values, if any, for this variable.
     *
     * @param linearScale indicates whether the variable has a linear 
     *            scale
     * @return default values or null if none are defined
     * @see #setDefaultValues
     */
    public Object getDefaultValues(boolean linearScale) {

        if (var.getRecVariance()) {

            // no default values for record varying variables

            return null; 
        }

        String[] varType = getType();    // ISTP VAR_TYPE

        if (varType.length > 0) {

            if (varType[0].equalsIgnoreCase(SUPPORT_DATA)) {

                return getDefaultSupportDataValues();
            }
            else if (varType[0].equalsIgnoreCase(METADATA)) {

                return getDefaultMetadataValues(linearScale);
            }
        }

        return null;
    }


    /**
     * Gets the default values of a support_data variable.
     *
     * @return default values
     */
    public Object getDefaultSupportDataValues() {

        long cdfType = getDataType();
                                       // CDF data type
        long dimension = getNumDims();
                                       // dimension of variable
//System.err.println("getDefaultSupportDataValues: cdfType = " +
//    cdfType + ", dimension = " + dimension);

        if (dimension == 0) {

            if (cdfType == CDF.CDF_UINT1 || 
                cdfType == CDF.CDF_INT2) {

                return Short.valueOf((short)1);
            }
            else if (cdfType == CDF.CDF_UINT2 ||
                     cdfType == CDF.CDF_INT4) {

                return Integer.valueOf(1);
            }
            else if (cdfType == CDF.CDF_UINT4) {

                return Long.valueOf(1);
            }
            else if (cdfType == CDF.CDF_FLOAT ||
                     cdfType == CDF.CDF_REAL4) {

                return Float.valueOf(1.0f);
            }
            else if (cdfType == CDF.CDF_DOUBLE ||
                     cdfType == CDF.CDF_REAL8) {

                return Double.valueOf(1.0);
            }
            else if (cdfType == CDF.CDF_EPOCH) {

                return Epoch8.getDefaultValue();
            }
            else if (cdfType == CDF.CDF_EPOCH16) {

                // impossible case.  epoch16 is double[2]
                // (that is, dimension == 1).
                return Epoch16.getDefaultValue();
            }
            else if (cdfType == CDF.CDF_TIME_TT2000) {

                return TerrestrialTime2000.getDefaultValue();
            }
        }
        else if (dimension == 1 && cdfType == CDF.CDF_EPOCH16) {

            return Epoch16.getDefaultValue();
        }
        else if (dimension == 1) {

            long[] dimSizes = var.getDimSizes();
                                       // dimension sizes
            int size = (int)dimSizes[0];
                                       // size of this dimension
            Object values = null;      // default values

            if (cdfType == CDF.CDF_UINT1 || 
                cdfType == CDF.CDF_INT2) {

                values = Array.newInstance(Short.TYPE, size);

                for (int i = 0; i < size; i++) {

                    Array.setShort(values, i, (short)(i + 1));
                }
            }
            else if (cdfType == CDF.CDF_UINT2 ||
                     cdfType == CDF.CDF_INT4) {

                values = Array.newInstance(Integer.TYPE, size);

                for (int i = 0; i < size; i++) {

                    Array.setInt(values, i, i + 1);
                }
            }
            else if (cdfType == CDF.CDF_UINT4) {

                values = Array.newInstance(Long.TYPE, size);

                for (int i = 0; i < size; i++) {

                    Array.setLong(values, i, i + 1);
                }
            }
            else if (cdfType == CDF.CDF_FLOAT ||
                     cdfType == CDF.CDF_REAL4) {

                values = Array.newInstance(Float.TYPE, size);

                for (int i = 0; i < size; i++) {

                    Array.setFloat(values, i, (float)(i + 1));
                }
            }
            else if (cdfType == CDF.CDF_DOUBLE ||
                     cdfType == CDF.CDF_REAL8) {

                values = Array.newInstance(Double.TYPE, size);

                for (int i = 0; i < size; i++) {

                    Array.setDouble(values, i, (double)(i + 1));
                }
            }
            else if (cdfType == CDF.CDF_EPOCH) {

                values = Array.newInstance(Double.TYPE, size);

                for (int i = 0; i < size; i++) {

                    Array.set(values, i, Epoch8.getDefaultValue());
                }
            }
/*
            else if (cdfType == CDF.CDF_EPOCH16) {

                int[] dimensions = new int[] {size, 2};
                                       // dimensions of values
                values = Array.newInstance(Double.TYPE, dimensions);

                for (int i = 0; i < size; i++) {

                    Array.set(values, i, Epoch16.getDefaultValue());
                }
            }
*/
            else if (cdfType == CDF.CDF_TIME_TT2000) {

                values = Array.newInstance(Long.TYPE, size);

                for (int i = 0; i < size; i++) {

                    Array.set(values, i, 
                              TerrestrialTime2000.getDefaultValue());
                }
            }

            return values;
        }
        else if (dimension == 2 && cdfType == CDF.CDF_EPOCH16) {

            long[] dimSizes = var.getDimSizes();
                                       // dimension sizes
            int size = (int)dimSizes[0];
                                       // size of this dimension
            Object values = null;      // default values
            int[] dimensions = new int[] {size, 2};
                                       // dimensions of values
            values = Array.newInstance(Double.TYPE, dimensions);

            for (int i = 0; i < size; i++) {

                Array.set(values, i, Epoch16.getDefaultValue());
            }
            return values;
        }

        return null;
    }


    /**
     * Provides the length of the values that
     * {@link #getDefaultMetadataValues(boolean)} will return.
     *
     * @param varName name of metadata variable
     * @param linearScale indicates whether the variable has a linear 
     *            scale
     * @return length of default values
     * @see #getDefaultMetadataValues
     */
    public static int getDefaultMetadataValueLength(
        String varName, boolean linearScale) {

        if (linearScale) {

            return "Linear".length();
        }
        else {

            return varName.length() + "Comp xx ".length();
        }
    }


    /**
     * Gets the default values of a metadata variable.
     *
     * @param linearScale indicates whether the variable has a linear 
     *            scale
     * @return default values
     */
    public Object getDefaultMetadataValues(boolean linearScale) {

        if (getNumDims() == 1) {

            long[] sizes = var.getDimSizes();
                                       // dimension sizes
            String[] values = new String[(int)sizes[0]];
                                       // default values
            for (int i = 0; i < values.length; i++) {

                //
                // Changes to the following will likely require changes
                // to getDefaultMetadataValueLength()
                //
                if (linearScale) {

                    values[i] = "linear";
                }
                else {

                    values[i] = "Comp " + (i + 1) + " " + var.getName();
                }
            }

            return values;
        }
        else {

            return null;   // don't know what these should be
        }

    }


    /**
     * Sets default VALIDMIN attribute value(s) equal to the variable's
     * non-record varying data values.  This method should only be
     * called after the variable's default values have been set.
     *
     * @throws CDFException if a CDFException occurs
     * @see #setDefaultValues
     */
    private void setDefaultValidMin() 
        throws CDFException {

        Object data = null;            // data value

        try {

            CDFData record = getRecordObject(0L);
                                       // first record of values
            if (record != null) {

                // This condition will result in virtual record data
                // (containing pad values) being used for the default
                // validmin value.  To not use the pad value for the
                // validmin, add
                //     || var.getMyCDF().getStatus() == 
                //     CDF.VIRTUAL_RECORD_DATA 
                // to the above if condition.

                data = record.getData();
            }
        }
        catch (CDFException e) {

            if (e.getCurrentStatus() == CDF.NO_SUCH_RECORD ) {

                data = getDefaultValues(true);
            }
            else {

                throw e;
            }
        }

        if (data != null) {

            long cdfType = getDataType();
                                       // CDF datatype
            if (cdfType == CDF.CDF_EPOCH16) {

                setAttributeValue("VALIDMIN", cdfType, data);
            }
            else if (cdfType != CDF.CDF_CHAR && 
                     cdfType != CDF.CDF_UCHAR) {

                long numDims = getNumDims();
                                       // dimensionality
                if (numDims == 1) {

                    long dimSize = getDimSizes()[0];
                                       // size of array
                    if (dimSize > 3) {

                        // For large array of values, set the VALIDMIN
                        // to the single value that is the minimum of
                        // all the array values.

                        setAttributeValue("VALIDMIN", cdfType, 
                            getMin(data));
                    }
                    else {

                        setAttributeValue("VALIDMIN", cdfType, 
                            createPrimativeArray((int)dimSize, 
                                getMin(data)));
                    }
                }
                else {

                    setAttributeValue("VALIDMIN", cdfType, 
                        getMin(data));
                }
            }
        }
    }


    /**
     * Creates an array of the given length whose elements are primative
     * type and value of the given value.
     *
     * @param length length of array to create
     * @param elementValue value to initialize all elements to
     * @return an array of the given length whose elements are primative
     *             type and value of the given value.
     */
    private static Object createPrimativeArray(
        int length, 
        Object elementValue) {

        Object array = null;

        if (elementValue instanceof Boolean) {

            array = Array.newInstance(Boolean.TYPE, length);

            for (int i = 0; i < length; i++) {

                Array.setBoolean(array, i, 
                    ((Boolean)elementValue).booleanValue());
            }
        }
        else if (elementValue instanceof Byte) {

            array = Array.newInstance(Byte.TYPE, length);

            for (int i = 0; i < length; i++) {

                Array.setByte(array, i, 
                    ((Byte)elementValue).byteValue());
            }
        }
        else if (elementValue instanceof Double) {

            array = Array.newInstance(Double.TYPE, length);

            for (int i = 0; i < length; i++) {

                Array.setDouble(array, i, 
                    ((Double)elementValue).doubleValue());
            }
        }
        else if (elementValue instanceof Float) {

            array = Array.newInstance(Float.TYPE, length);

            for (int i = 0; i < length; i++) {

                Array.setFloat(array, i, 
                    ((Float)elementValue).floatValue());
            }
        }
        else if (elementValue instanceof Integer) {

            array = Array.newInstance(Integer.TYPE, length);

            for (int i = 0; i < length; i++) {

                Array.setInt(array, i, 
                    ((Integer)elementValue).intValue());
            }
        }
        else if (elementValue instanceof Long) {

            array = Array.newInstance(Long.TYPE, length);

            for (int i = 0; i < length; i++) {

                Array.setLong(array, i, 
                    ((Long)elementValue).longValue());
            }
        }
        else if (elementValue instanceof Short) {

            array = Array.newInstance(Short.TYPE, length);

            for (int i = 0; i < length; i++) {

                Array.setShort(array, i, 
                    ((Short)elementValue).shortValue());
            }
        }

        return array;
    }


    /**
     * Gets the minimum value contained in the given data.  If the
     * given object is an array, the minimum value of all the elements
     * of the array is returned.
     *
     * @param data data to search for minimum value
     * @return minimum value from within data.
     */
    private static Object getMin(Object data) {

        Class dataClass = data.getClass();
                                       // data's Class

        if (dataClass.isArray()) {

            Object min = null;         // current minimum value

            for (int i = 0; i < Array.getLength(data); i++) {

                Object element = Array.get(data, i);
                                       // i'th element of data

                min = min(min, getMin(element));
            }

            return min;
        }
        else {

            return data;
        }
    }


    /**
     * Returns the smaller of the two values.  The objects must be of
     * the same class and must implement {@code Comparable<T>}.
     *
     * @param v1 first value
     * @param v2 second value
     * @return the smaller of v1 and v2.
     */
    private static Object min(Object v1, Object v2) {

        if (v1 == null) {

            return v2;
        }

        if (v2 == null) {

            return v1;
        }

        Class cl1 = v1.getClass();         // v1's class
        Class cl2 = v2.getClass();         // v2's class

        if (!cl1.equals(cl2)) {

            throw new IllegalArgumentException(cl1.getName() + " != " +
                      cl2.getName());
        }

        try {

            return ((Comparable)v1).compareTo(v2) < 0 ? v1 : v2;
        }
        catch (ClassCastException e) {

            throw new IllegalArgumentException(cl1.getName() + " or " +
                      cl2.getName() + " is not Comparable");
        }
    }


    /**
     * Gets the maximum value contained in the given data.  If the
     * given object is an array, the maximum value of all the elements
     * of the array is returned.
     *
     * @param data data to search for maximum value
     * @return maximum value from within data.
     */
    private static Object getMax(Object data) {

        Class dataClass = data.getClass();
                                       // data's Class

        if (dataClass.isArray()) {

            Object max = null;         // current maximum value

            for (int i = 0; i < Array.getLength(data); i++) {

                Object element = Array.get(data, i);
                                       // i'th element of data

                max = max(max, getMin(element));
            }

            return max;
        }
        else {

            return data;
        }
    }


    /**
     * Returns the larger of the two values.  The objects must be of
     * the same class and must implement {@code Comparable<T>}.
     *
     * @param v1 first value
     * @param v2 second value
     * @return the larger of v1 and v2.
     */
    private static Object max(Object v1, Object v2) {

        if (v1 == null) {

            return v2;
        }

        if (v2 == null) {

            return v1;
        }

        Class cl1 = v1.getClass();         // v1's class
        Class cl2 = v2.getClass();         // v2's class

        if (!cl1.equals(cl2)) {

            throw new IllegalArgumentException(cl1.getName() + " != " +
                      cl2.getName());
        }

        try {

            return ((Comparable)v1).compareTo(v2) > 0 ? v1 : v2;
        }
        catch (ClassCastException e) {

            throw new IllegalArgumentException(cl1.getName() + " or " +
                      cl2.getName() + " is not Comparable");
        }
    }


    /**
     * Sets default VALIDMAX attribute value(s) equal to the variable's
     * non-record varying data values.  This method should only be
     * called after the variable's default values have been set.
     *
     * @throws CDFException if a CDFException occurs
     * @see #setDefaultValues
     */
    public void setDefaultValidMax() 
        throws CDFException {

        Object data = null;            // data value

        try {
            CDFData record = getRecordObject(0L);
                                       // first record of values
            if (record != null) {

                data = record.getData();
            }
        }
        catch (CDFException e) {

            if (e.getCurrentStatus() == CDF.NO_SUCH_RECORD ) {

                data = getDefaultValues(true);
            }
            else {

                throw e;
            }
        }

        if (data != null) {

            long cdfType = getDataType();
                                       // CDF datatype
            if (cdfType != CDF.CDF_CHAR && cdfType != CDF.CDF_UCHAR) {

                long numDims = getNumDims();
                                       // dimensionality
                if (numDims == 1) {

                    long dimSize = getDimSizes()[0];
                                       // size of array
                    if (dimSize > 3) {

                        // For large array of values, set the VALIDMIN
                        // to the single value that is the minimum of
                        // all the array values.

                        setAttributeValue("VALIDMAX", cdfType, 
                            getMax(data));
                    }
                    else {

                        setAttributeValue("VALIDMAX", cdfType, 
                            createPrimativeArray((int)dimSize,
                                getMax(data)));
                    }
                }
                else {

                    setAttributeValue("VALIDMAX", cdfType, 
                        getMax(data));
                }
            }
        }
    }


    /**
     * Gets the ISTP variable type.
     *
     * @return ISTP variable type
     * @see #setType
     */
    public String[] getType() {

        return getCharAttributeValue("VAR_TYPE");
    }


    /**
     * Sets the ISTP variable type.
     *
     * @param value new type value
     * @see #getType
     * @throws CDFException if a CDFException occurs
     * @throws ISTPComplianceException if the given value is not valid
     */
    public void setType(String value)
        throws CDFException, ISTPComplianceException {

        if (isValidType(value)) {

            setAttributeValue("VAR_TYPE", value);
        }
        else {

            throw new ISTPComplianceException(value +
                " is not a valid ISTP variable type");
        }
    }


    /**
     * Gets default VAR_TYPE value.
     *
     * @return default VAR_TYPE value.
     */
    public String[] getDefaultType() {

System.err.println("getDefaultType: " + var.getName());

         String varType = var.getRecVariance() ? DATA : SUPPORT_DATA;
                                       // default VAR_TYPE value
System.err.println("getDefaultType: varType = " + varType);
         return new String[] {varType};
    }

    /**
     * Sets a default VAR_TYPE value.
     *
     * @return default VAR_TYPE value that was set.
     * @throws CDFException if a CDFException occurs.
     */
    public String[] setDefaultType() 
        throws CDFException {

System.err.println("setDefaultType: ");
        String[] defaultValue = getDefaultType();

        try {

System.err.println("setDefaultType: setType");
            setType(defaultValue[0]);
System.err.println("setDefaultType: Type set");
        }
        catch (ISTPComplianceException e) {

System.err.println("setDefaultType: e = " + e.getMessage());
            // getDefaultType() returned an invalid value!
            return null;
        }

        return defaultValue;
    }


    /**
     * Determines if the given value is a valid ISTP variable type.
     *
     * @param value VAR_TYPE value to test for validity
     * @return true if the given value is a valid VAR_TYPE value, 
     *         false otherwise
     */
    public static boolean isValidType(String value) {


        return (value.equalsIgnoreCase(DATA) || 
                value.equalsIgnoreCase(SUPPORT_DATA) ||
                value.equalsIgnoreCase(METADATA) || 
                value.equalsIgnoreCase(IGNORE_DATA));
    }


    /**
     * Gets the ISTP variable DISPLAY_TYPE value.
     *
     * @return ISTP variable DISPLAY_TYPE value.
     * @see #setDisplayTypeValue
     */
    public String[] getDisplayTypeValue() {

        return getCharAttributeValue("DISPLAY_TYPE");
    }


    /**
     * Sets the ISTP variable DISPLAY_TYPE value.
     *
     * @param value new DISPLAY_TYPE value.
     * @see #getDisplayTypeValue
     * @throws CDFException if a CDFException occurs
     * @throws ISTPComplianceException if the given value is not valid
     */
    public void setDisplayTypeValue(String value)
        throws CDFException, ISTPComplianceException {

        if (DisplayType.isValidDisplayType(value)) {

            setAttributeValue("DISPLAY_TYPE", value);
        }
        else {

            throw new ISTPComplianceException(value +
                " is not a valid ISTP variable display type");
        }
    }


    /**
     * Gets the ISTP variable DISPLAY_TYPE value.
     *
     * @return ISTP variable DISPLAY_TYPE value or null if none.
     * @see #setDisplayTypeValue
     * @throws ISTPComplianceException if the DISPLAY_TYPE value is 
     *     not valid.
     */
    public DisplayType getDisplayType() 
        throws ISTPComplianceException {

        DisplayType displayType = null;// result display type value
        String[] rawDisplayType = getCharAttributeValue("DISPLAY_TYPE");
                                       // raw display type value
        if (rawDisplayType.length > 0) {

            try {

                displayType =
                    DisplayType.parseDisplayType(rawDisplayType[0]);
            }
            catch (IllegalArgumentException e) {

                throw new ISTPComplianceException(rawDisplayType[0] +
                    " is not a valid ISTP variable display type");
            }
        }
        return displayType;
    }


    /**
     * Provides the valid DisplayType values for this variable based 
     * upon the dimensionality in order of decreasing use cases.  That 
     * is, element 0 is used more often than element 1.
     *
     * @return valid DisplayType values for this variable based upon
     *     dimensionality in order of decreasing use cases.
     */
    public String[] getValidDisplayTypeValues() {

        return DisplayType.getValidDisplayTypeValues(
                   (int)getNumDims());
    }


    /**
     * Provides the default DisplayType value for this variable based 
     * upon the dimensionality.
     *
     * @return default DisplayType value for this variable based upon
     *     dimensionality.
     * @see #setDefaultDisplayTypeValue()
     */
    public String getDefaultDisplayTypeValue() {

        return getValidDisplayTypeValues()[0];
    }


    /**
     * Sets the DisplayType value for this variable to a default value
     * based upon the dimensionality.
     *
     * @throws CDFException if an error occurs setting the value.
     * @see #getDefaultDisplayTypeValue()
     */
    public void setDefaultDisplayTypeValue() 
        throws CDFException {

        try {

            setDisplayTypeValue(getValidDisplayTypeValues()[0]);
        }
        catch (ArrayIndexOutOfBoundsException e) {

            // there are no default display types
        }
        catch (ISTPComplianceException e) {

            // getValidDisplayTypeValues will not return a value
            // that is not ISTP compliant.
        }
    }


    /**
     * Provides the valid DisplayType Title values for this variable 
     * based upon the dimensionality in order of decreasing use cases.
     * That is, element 0 is used more often than element 1.
     *
     * @return valid DisplayType Title values for this variable based 
     *     upon dimensionality in order of decreasing use cases.
     */
    public String[] getValidDisplayTypeTitles() {

        return DisplayType.getValidDisplayTypeTitles(
                   (int)getNumDims());
    }


    /**
     * Provides the valid DisplayType values for this variable based 
     * upon the dimensionality in order of decreasing use cases.  That 
     * is, element 0 is used more often than element 1.
     *
     * @return valid DisplayType values for this variable based upon
     *     dimensionality in order of decreasing use cases.
     */
    public DisplayType[] getValidDisplayTypes() {

        return DisplayType.getValidDisplayTypes(
                   (int)getNumDims());
    }


    /**
     * Gets the underlying CDF variable.  
     *
     * @return the underlying CDF variable
     * @deprecated As of SKTEditor 1.2, there is no replacement.  This
     *     method was introduced to aid in the introduction of this 
     *     gsfc.spdf.istp.Variable class hiararchy.
     */
    @Deprecated
    public gsfc.nssdc.cdf.Variable getCdfVariable() {

        return var;
    }


    /**
     * Gets the name of this variable.
     *
     * @return variable's name
     */
    public String getName() {

        return var.getName();
    }


    /**
     * Gets the CDF data type of this variable.
     *
     * @return CDF data type of this variable.
     */
    public long getDataType() {

        return var.getDataType();
    }


    /**
     * Gets the number of dimensions for this variable.
     *
     * @return number of dimensions for this variable.
     */
    public long getNumDims() {

        return var.getNumDims();
    }


    /**
     * Gets the dimension sizes for this variable.
     *
     * @return the dimension sizes for this variable.
     */
    public long[] getDimSizes() {

        return var.getDimSizes();
    }


    /**
     * Gets the dimension variances for this variable.
     *
     * @return the dimension variances for this variable.
     */
    public long[] getDimVariances() {

        return var.getDimVariances();
    }


    /**
     * Indicates whether this variable is record varying.
     *
     * @return true if record varying, false if not
     */
    public boolean isRecordVarying() {

        return var.getRecVariance();
    }


    /**
     * Indicates whether this variable is a tensor variable.  Note that 
     * the ISTP guidelines do not specify how tensor variables are
     * identified so this function may not return a definitive answer.
     *
     * @return true if this variable represents a tensor object.  
     *     Otherwise, false.
     */
    public boolean isTensor() {

        long numDims = getNumDims();
        if (numDims < 1 || numDims > 3) {

System.out.println("**** " + getName() + " is NOT tensor because numDims = " + 
    numDims);
            return false;
        }
        long[] dimSizes = getDimSizes();
        for (int i = 0; i < numDims; i++) {

            if (dimSizes[i] > 3) {

System.out.println("**** " + getName() + " is NOT tensor because dimSizes[" +
    i + " = " + dimSizes[i]);
                return false;
            }
        }
        String[] dict_key = getCharAttributeValue("DICT_KEY");
                                       // DICT_KEY value
        if (dict_key != null && 
            dict_key[0].toLowerCase().contains("vector")) {

System.out.println("*** " + getName() + " is tensor");
            return true;
        }

        String[] catdesc = getCharAttributeValue("CATDESC");
                                       // CATDESC value
        if (catdesc != null && 
            catdesc[0].toLowerCase().contains("tensor")) {

System.out.println("*** " + getName() + " is tensor");
            return true;
        }

/*
        try {

            if (getAttribute("TENSOR_ORDER") != null ||
                getAttribute("TENSOR_FRAME") != null) {

System.out.println("*** " + getName() + " is tensor");
                return true;
            }
        }
        catch (CDFException e) {

            return false;
        }
*/

        return false;
    }


    /**
     * Gets the attributes associated with this variable.
     *
     * @return Vector containing the attributes associated with this 
     * variable.
     */
    public Vector getAttributes() {

        return var.getAttributes();
    }


    /**
     * Get the specified attribute for this variable.  Note that the
     * name of the returned attribute may differ in case from the 
     * specified name since ISTP variable attribute names are case
     * insensitive.
     *
     * @param name name of attribute to get
     * @return specified variable attribute or null if it does not exist
     * @throws CDFException if a CDFException occurs
     */
    public Attribute getAttribute(String name) 
        throws CDFException {

        try {

            Attribute attribute = var.getMyCDF().getAttribute(name);
                                       // requested attribute

            if (attribute.getScope() == CDF.VARIABLE_SCOPE) {

                return attribute;
            }
            else {

                return null;
            }
        }
        catch (CDFException e) {

            if (e.getCurrentStatus() == CDF.NO_SUCH_ATTR) {

                Vector attributes = var.getMyCDF().getAttributes();
                                       // all attributes

                for (int i = 0; i < attributes.size(); i++) {

                    Attribute attribute = 
                        (Attribute)attributes.elementAt(i);
                                       // i'th attribute
                    if (name.equalsIgnoreCase(attribute.getName()) &&
                        attribute.getScope() == CDF.VARIABLE_SCOPE) {

                        return attribute;
                    }
                }
                return null;
            }
            else {

                throw e;
            }
        }
    }


    /**
     * Creates the specified variable attribute in accordance with ISTP
     * guidelines.
     *
     * @param name name of new attribute.
     * @return new attribute.
     * @throws CDFException if a CDFException occurs.
     * @throws ISTPIdentifierException if an attribute already exists 
     *     with the specified name (case insensitive comparison) or the 
     *     specified name violates ISTP guidelines
     */
    public Attribute createAttribute(String name)
        throws CDFException, ISTPIdentifierException {

        gsfc.spdf.istp.Attribute attribute = 
            gsfc.spdf.istp.Attribute.getIgnoreCase(
                var.getMyCDF(), name);
                                       // existing attribute with the
                                       // same (ignoring case) name
        if (attribute != null) {

            throw new ISTPIdentifierException(
                    "Illegal attribute name.  Attribute " + 
                    name + " already exists in CDF as " + 
                    attribute.getName() + ".", name, 
                    attribute.getName());
        }

        checkIdlName(name);

        return Attribute.create(var.getMyCDF(), name, 
                                CDF.VARIABLE_SCOPE);
    }


    /**
     * Get the specified attribute entry for this variable.  Note that 
     * the name of the returned entry may belong to an attribute whose 
     * name differ in case from the specified name since ISTP variable 
     * attribute names are case insensitive.
     *
     * @param name name of attribute entry to get
     * @return specified variable attribute entry or null if it does 
     *     not exist
     * @throws CDFException if a CDFException occurs
     */
    public Entry getAttributeEntry(String name) 
        throws CDFException {

        Attribute attribute = getAttribute(name);
                                       // specified attribute

        if (attribute != null) {

            try {

                return attribute.getEntry(var);
            }
            catch (CDFException e) {

                if (e.getCurrentStatus() == CDF.NO_SUCH_ENTRY) {

                    return null;
                }
                else {

                    throw e;
                }
            }
        }
        else {

            return null;
        }
    }


    /**
     * Sets an attribute entry value.  If the specified attribute does
     * not exist, it is created.  If the specified value is null, the
     * attribute is deleted.
     *
     * @param name name of attribute
     * @param value new value of attribute entry
     * @throws CDFException if a CDFException occurs
     * @throws ISTPIdentifierException if the specified attribute
     *             does not exist and cannot be created because the
     *             specified name does not comply with ISTP identifier
     *             restrictions
     * @see #getAttributeValue
     * @see #getCharAttributeValue
     */
    public void setAttributeValue(String name, String value)
        throws CDFException, ISTPIdentifierException {

        Attribute attribute = getAttribute(name);    
                                       // the attribute to set

        if (attribute == null) {

            attribute = createAttribute(name);
        }

        if (value != null && value.length() > 0) {

            var.putEntry(attribute.getName(), CDF.CDF_CHAR, value);
        }
        else {

            try {

                attribute.deleteEntry(var);
            }
            catch (CDFException e) {

                if (e.getCurrentStatus() != CDF.NO_SUCH_ENTRY) {

                    throw e;
                }
            }
        }
    }


    /**
     * Gets the value of the specified attribute.  This method should 
     * only be used when the type of the attribute is known to be CHAR. 
     * If called for an attribute that is not of type CHAR, null will 
     * be returned.
     *
     * @param name attribute name
     * @return value of attribute.  null if the specified attribute
     *             does not exist or is not of type CHAR
     * @see #setAttributeValue(String, String)
     */
    public String[] getCharAttributeValue(String name) {

//        try {

            Object value = getAttributeValue(name);

            if (value == null) {

//                return new String[] {};
                return null;
            }
            if (value instanceof String) {

                return new String[] {(String)value};
            }
            else if (value instanceof String[]) {

                return (String[])value;
            }
//        }
//        catch (ClassCastException e) {
//
//            return new String[] {};
//        }
        return null;
    }


    /**
     * Gets the value of the specified attribute from the specified
     * variable.  
     *
     * @param name attribute name
     * @return value of attribute.  null if the specified attribute
     *             does not exist 
     * @see #setAttributeValue
     */
    public Object getAttributeValue(String name) {

        try {

            return var.getEntryData(name);
        }
        catch (CDFException e) {

            return null;
        }
    }


    /**
     * Sets the specified attribute's value.
     * 
     * @param name name of attribute
     * @param cdfType CDF data type of attribute
     * @param value new value
     * @throws CDFException if an CDFException occurs
     * @see #getAttributeValue
     */
    public void setAttributeValue(String name, long cdfType, 
                                  Object value)
        throws CDFException {

        try {

            Attribute.create(var.getMyCDF(), name, CDF.VARIABLE_SCOPE);
        }
        catch (CDFException e) {

            // ignore it -- probably already exists
        }

        if (value != null) {

            var.putEntry(name, cdfType, value);
        }
    }


    /**
     * Gets a map containing this variables attribute entries.  The
     * map's key value is the name of the attribute and the map's value
     * is the CDF Entry.
     *
     * @return map containing this variable's attribute entries.
     * @throws CDFException if an error occurs getting the attribute
     *             entries.
     * @see #setAttributeEntries
     */
    public Map<String, Entry> getAttributeEntries() 
        throws CDFException {

        HashMap<String, Entry> entries = new HashMap<String, Entry>();
                                       // this variable's attribute 
                                       // entries
        Vector varAttrs = var.getAttributes();
                                       // this variable's attributes
        for (Enumeration e = varAttrs.elements(); 
             e.hasMoreElements(); ) {

            Attribute attr = (Attribute)e.nextElement();
                                       // one of this variable's 
                                       // attributes
            entries.put(attr.getName(), attr.getEntry(var));
        }

        return entries;
    }


    /**
     * Sets the given CDF attribute entry values for this variable.
     * The map's key value is the name of the attribute and the map's
     * value is the CDF Entry to set.
     *
     * @param entries map containing the CDF Attribute Entry value to
     *                set.
     * @throws CDFException if an error occurs setting the attribute
     *             entries.
     * @see #getAttributeEntries
     */
    public void setAttributeEntries(Map<String, Entry> entries) 
        throws CDFException {

        for (Map.Entry<String, Entry> mapEntry : entries.entrySet()) {

            Entry entry = mapEntry.getValue();

            var.putEntry(mapEntry.getKey(), entry.getDataType(), 
                         entry.getData());
        }
    }


    /**
     * Creates a copy of this variable with the same name, same
     * Attribute Entry values, and the specified number of elements.  
     * Note the following:<ul>
     *   <li>In order to have the same name, the original CDF 
     *       variable is deleted.  Any external references to the 
     *       original CDF variable should be discarded.</li>
     *   <li>Only the associated CDF Attribute Entries are copied
     *       to the new variable.  The data is not copied.</li>
     * </ul>
     *
     * @param numElements CDF number of elements for new variable.
     * @return a copy of this variable with the specified number of
     *             elements.
     * @throws CDFException if an error occurs setting the attribute
     *             entries.
     */
    public Variable copy(long numElements) 
        throws CDFException {

        CDF cdf = var.getMyCDF();      // cdf containing the variable
        String varName = var.getName();// name of variable
        long datatype = var.getDataType();
                                       // cdf datatype
        long numDims = var.getNumDims();
                                       // number of dimensions
        long[] dimSizes = var.getDimSizes();
                                       // dimension sizes
        boolean recVary = var.getRecVariance();
                                       // record variance
        long[] dimVarys = var.getDimVariances();
                                       // dimension variances

        Map<String, Entry> entries = getAttributeEntries();
                                       // associated Attribute Entries
        var.delete();

        var = gsfc.nssdc.cdf.Variable.create(
                  cdf, varName, datatype, numElements, numDims, 
                  dimSizes, (recVary ? CDF.VARY : CDF.NOVARY),
                  dimVarys);

        setAttributeEntries(entries);

        return this;
    }


    /**
     * Gets the value of the VALIDMIN attribute as an array of <code>
     * Number</code> values.  If the attribute value is not an array, an
     * array of length 1 is returned.  A null value is returned if the
     * VALIDMIN attribute does not exist or if the attribute's value 
     * is not one of: <code>Byte, Double, Float, Integer, Long, Short
     * </code>, or a corresponding primative type.
     *
     * @return array of VALIDMIN Number values.
     */
    public Number[] getValidMinNumber() {

        return getAttributeNumber("VALIDMIN");
    }


    /**
     * Gets the value of the VALIDMAX attribute as an array of <code>
     * Number</code> values.  If the attribute value is not an array, an
     * array of length 1 is returned.  A null value is returned if the
     * VALIDMAX attribute does not exist or if the attribute's value 
     * is not one of: <code>Byte, Double, Float, Integer, Long, Short
     * </code>, or a corresponding primative type.
     *
     * @return array of VALIDMAX Number values.
     */
    public Number[] getValidMaxNumber() {

        return getAttributeNumber("VALIDMAX");
    }


    /**
     * Gets the value of the specified attribute as an array of <code>
     * Number</code> values.  If the attribute value is not an array, an
     * array of length 1 is returned.  A null value is returned if the
     * specified attribute does not exist or if the attribute's value 
     * is not one of: <code>Byte, Double, Float, Integer, Long, Short
     * </code>, or a corresponding primative type.
     *
     * @param name name of attribute.
     * @return array of Number values.
     */
    public Number[] getAttributeNumber(String name) {

        Object value = getAttributeValue(name);
                                       // attribute value
        if (value == null) {

            return null;
        }

        Class cl = value.getClass();   // class of value

        if (cl.isArray()) {

            int length = Array.getLength(value);
                                       // length of value array
            Number[] result = new Number[length];
                                       // result array
            Class type = cl.getComponentType();
                                       // type of value elements

            for (int i = 0; i < length; i++) {

                if (type == Byte.class || type == Double.class ||
                    type == Float.class || type == Integer.class ||
                    type == Long.class || type == Short.class) {

                    result[i] = (Number)Array.get(value, i);
                }
                else if (type.isPrimitive()) {

                    if (type == Byte.TYPE) {

                        result[i] = (Number)(Array.getByte(value, i));
                    }
                    else if (type == Double.TYPE) {

                        result[i] = (Number)(Array.getDouble(value, i));
                    }
                    else if (type == Float.TYPE) {

                        result[i] = (Number)(Array.getFloat(value, i));
                    }
                    else if (type == Integer.TYPE) {

                        result[i] = (Number)(Array.getInt(value, i));
                    }
                    else if (type == Long.TYPE) {

                        result[i] = (Number)(Array.getLong(value, i));
                    }
                    else if (type == Short.TYPE) {

                        result[i] = (Number)(Array.getShort(value, i));
                    }
                }
                else {

                    return null;
                }
            }

            return result;
        }
        else {

            if (value instanceof Number) {

                return new Number[] {(Number)value};
            }
        }

        return null;
    }


    /**
     * Gets the value of the FORM_PTR attribute.
     *
     * @return value of FORM_PTR attribute or null if it does not exist.
     */
    public String getFormPtr() {

        Object formPtr = getAttributeValue("FORM_PTR");

        if (formPtr == null) {

            return null;
        }
        else if (formPtr instanceof String) {

            return (String)formPtr;
        }
        else if (formPtr instanceof String[]) {

            return ((String[])formPtr)[0];
        }
        return null;
//        return (String)getAttributeValue("FORM_PTR");
    }


    /**
     * Gets the output format values (from FORMAT or FORM_PTR attribute
     * values).  When the FORMAT attribute is present, this method is
     * functionally equivalent to {@link #getFormat()}.
     *
     * @return output format values
     * @throws ISTPComplianceException if an ISTP compliance issue is
     *     detected.
     */
    public String[] getFormats() 
        throws ISTPComplianceException {

        String[] formats = new String[1];  
                                       // created as an array to 
                                       // accomodate the more 
                                       // complicated case but 
                                       // dimensioned for the simple 
                                       // case.  The dimension will be 
                                       // increased later if we 
                                       // actually have the more 
                                       // complicated case.
        String formPtr = null;         // FORM_PTR attribute value == 
                                       // name of variable holding 
                                       // FORMAT values

Object formatObj = getAttributeValue("FORMAT");
if (formatObj instanceof String) {

    formats[0] = (String)formatObj;
}
else if (formatObj instanceof String[]) {

    formats = (String[])formatObj;
}
//        formats[0] = (String)getAttributeValue("FORMAT");
                                       // attribute value
        if (formats[0] == null) {

            formPtr = getFormPtr();

            if (formPtr == null) {

                return null;
            }

            Variable formPtrVar = getVariable(formPtr);

            if (formPtrVar == null) {

                throw new ISTPComplianceException(
                    "FORM_PTR set to non-existent variable '" +
                    formPtr + "'");
            }

            if (formPtrVar.getDataType() != CDFConstants.CDF_CHAR) {

                throw new ISTPComplianceException(
                    "FORM_PTR variable '" + formPtr +
                                  "' is not of type CDF_CHAR");
            }

            CDFData formatData = null; // actual FORMAT values

            try {

                formatData = formPtrVar.getRecordObject(0);
            }
            catch (CDFException e) {

                throw new ISTPComplianceException(
                    "FORM_PTR data is missing.  (" + 
                    e.getMessage() + ")");
            }

            int nDims = formatData.getnDims(); 
                                       // dimension of formatData

            switch (nDims) {

            case 0:

                formats[0] = (String)formatData.getData();
                break;

            case 1:

                formats = (String[])formatData.getData();
                break;

            default:

                throw new ISTPComplianceException(
                    "Invalid dimension '" + nDims +
                    "' for FORM_PTR variable '" + formPtr + "'");
            }
        }

        return formats;
    }


    /**
     * Get a single record of data from this variable.
     *
     * @param record the record number to retrieve data from.
     * @return requested data.
     * @throws CDFException if a problem getting the record.
     */
    public CDFData getRecordObject(long record)
        throws CDFException {

        return var.getRecordObject(record);
    }


    /**
     * Gets the number of records physically written (not allocated) for
     * this variable.
     *
     * @return the number of records physically written
     * @throws CDFException if a problem occurs getting the number of 
     *             records written physically
     */
    public long getNumWrittenRecords()
        throws CDFException {

        return var.getNumWrittenRecords();
    }


    /**
     * Adds a single record to a record-varying variable.
     *
     * @param record record number where this data is to be put
     * @param data the data to be added
     * @return CDFData object containing the user specified data
     * @throws CDFException if there is a problem writing the data
     */
    public CDFData putRecord(long record, Object data) 
        throws CDFException {

        return var.putRecord(data);
    }


    /**
     * Adds a single record to a non-record-varying variable.
     *
     * @param data the data to be added
     * @return CDFData object containing the user specified data
     * @throws CDFException if there is a problem writing the data
     */
    public CDFData putRecord(Object data) 
        throws CDFException {

        return var.putRecord(data);
    }


    /**
     * Gets the default FILLVAL attribute value for this variable.
     *
     * @return default FILLVAL attribute value
     * @see #setDefaultFillval
     */
    public Object getDefaultFillval() {

        return FillvalAttribute.getStandardValue(var.getDataType());
    }


    /**
     * Sets a default value for the FILLVAL attribute if a default value
     * is defined for this type of variable.
     *
     * @throws CDFException if CDFException occurs
     * @see #getDefaultFillval
     */
    public void setDefaultFillval()
        throws CDFException {

        Object defaultFillval = getDefaultFillval();
                                       // default FILLVAL attribute 
                                       //  value
        if (defaultFillval != null) {

            try {

                setFillval(defaultFillval);
            }
            catch (ISTPComplianceException e) {

                //
                // getDefaultFillval() should give us the correct value
                //
                System.err.println(
                    "gsfc.spdf.istp.Variable.setDefaultFillval: " +
                    "ISTPComplianceException: " + e.getMessage());
            }
        }
    }


    /**
     * Sets an appropriate default FORTRAN format descriptor for this
     * variable based upon the variable's CDF data type.
     *
     * @throws CDFException if CDFException occurs
     * @see #getDefaultFormat
     */
    public void setDefaultFormat() 
        throws CDFException {

        if (var.getDataType() != CDF.CDF_EPOCH &&
            var.getDataType() != CDF.CDF_EPOCH16) {

            setFormat(getDefaultFormat());
        }
    }


    /**
     * Gets the value of the FILLVAL attribute.
     *
     * @return value of FILLVAL attribute
     * @throws CDFException if CDFException occurs
     * @see #setFillval
     */
    public Object getFillval()
        throws CDFException {

        return getAttributeValue("FILLVAL");
    }


    /**
     * Sets the value of the FILLVAL attribute.
     *
     * @param value new FILLVAL attribute value
     * @throws CDFException if CDFException occurs
     * @see #getFillval
     * @throws ISTPComplianceException if the given value is not of a
     *             compatiable type for this variable datatype
     */
    public void setFillval(Object value) 
        throws CDFException, ISTPComplianceException {

        if (FillvalAttribute.isValid(getDataType(), value)) {

            setAttributeValue("FILLVAL", var.getDataType(), value);
        }
        else {

            throw new ISTPComplianceException(
                "Invalid FILLVAL value of type " +
                value.getClass().getName() + " for variable " +
                var.getName() + " (datatype = " +
                gsfc.nssdc.cdf.util.CDFUtils.getStringDataType(var) + 
                ")");
        }
    }


    /**
     * Determines whether the current set FILLVAL value is valid.
     *
     * @return true if the FILLVAL attribute is currently set to a valid
     *             value.  Otherwise false.
     */
    public boolean isFillvalValid() {

        try {

            return FillvalAttribute.isValid(getDataType(), 
                                            getFillval());
        }
        catch (CDFException e) {

            System.err.println(
                "Variable.isFillvalValid: CDFException: " +
                e.getMessage());
            return false;
        }
    }


    /**
     * Gets the FORMAT attribute value.
     *
     * @return FORMAT attribute value
     * @throws CDFException if CDFException occurs
     * @see #getFormats
     * @see #setFormat
     */
    public String[] getFormat()
        throws CDFException {

        return getCharAttributeValue("FORMAT");
    }


    /**
     * Sets the FORMAT attribute value to the given value.
     *
     * @param value new FORMAT attribute value
     * @throws CDFException if CDFException occurs
     * @see #getFormat
     */
    public void setFormat(String value)
        throws CDFException {

        try {

            setAttributeValue("FORMAT", value);
        }
        catch (ISTPIdentifierException e) {

            System.err.println(
                "gsfc.spdf.istp.Variable.setFormat: " +
                "exception: " + e.getMessage());
        }
    }


    /**
     * Provides an appropriate default FORTRAN format descriptor for the
     * given variable.  A null value is returned for EPOCH and EPOCH16
     * type variables.
     * 
     * @return default FORTRAN format descriptor
     * @see #setDefaultFormat
     */
    public String getDefaultFormat() {

        long type = var.getDataType(); // variable's data type

        if (type == CDF.CDF_INT8) {

            return "I19";
        }
        else if (type == CDF.CDF_INT4 || type == CDF.CDF_UINT4) {

            return "I8";
        }
        else if (type == CDF.CDF_INT2 || type == CDF.CDF_UINT2) {

            return "I5";
        }
        else if (type == CDF.CDF_INT1 || type == CDF.CDF_UINT1 ||
                 type == CDF.CDF_BYTE) {

            return "I3";
        }
        else if (type == CDF.CDF_FLOAT || type == CDF.CDF_REAL4 ||
                 type == CDF.CDF_DOUBLE || type == CDF.CDF_REAL8) {

            return "E12.2";
        }
        else if (type == CDF.CDF_CHAR || type == CDF.CDF_UCHAR) {

            long length = var.getNumElements();
                                      // number of characters

            return "A" + (length + 1);
        }
        else {

            return null;
        }
    }


    /**
     * Gets the UNITS attribute value.
     *
     * @return UNITS attribute value
     * @throws CDFException if CDFException occurs
     * @see #setUnitsValue
     */
    public String[] getUnitsValue()
        throws CDFException {

        return getCharAttributeValue("UNITS");
    }


    /**
     * Sets the UNITS attribute value to the given value.
     *
     * @param value new UNITS attribute value
     * @throws CDFException if CDFException occurs
     * @see #getUnitsValue
     */
    public void setUnitsValue(String value)
        throws CDFException {

        try {

            setAttributeValue("UNITS", value);
        }
        catch (ISTPIdentifierException e) {

            System.err.println(
                "gsfc.spdf.istp.Variable.setUnits: " +
                "exception: " + e.getMessage());
        }
    }


    /**
     * Gets this variable's UNIT_PTR attribute variable.
     *
     * @return UNIT_PTR attribute target variable.
     * @throws CDFException if a CDFException occurs
     * @see #setUnitPtrVariable
     */
    public Variable getUnitPtrVariable()
        throws CDFException {

        Variable targetVariable = null;// variable pointed to by
                                       // UNIT_PTR attribute

        String targetVarName = (String)
            getAttributeValue("UNIT_PTR");

        if (targetVarName != null) {

            try {

                targetVariable = 
                    new Variable(
                        var.getMyCDF().getVariable(targetVarName));
            }
            catch (CDFException e) {

                // probably does not exist
            }
        }

        return targetVariable;
    }


    /**
     * Sets the value of this variable's UNIT_PTR attribute to the 
     * specified variable.
     *
     * @param var variable for UNIT_PTR attribute.
     * @throws CDFException if a CDFException occurs
     * @see #getUnitPtrVariable
     */
    public void setUnitPtrVariable(Variable var)
        throws CDFException {

        try {

            setAttributeValue("UNIT_PTR", var.getName());
        }
        catch (ISTPIdentifierException e) {

            System.err.println(
                "gsfc.spdf.istp.Variable.setUnitPtrVariable: " +
                "exception: " + e.getMessage());
        }
    }


    /**
     * Gets the compression type of this variable.
     *
     * @return the compression type of this variable.
     * @see #setCompression(long, long[])
     */
    public long getCompressionType() {

        return var.getCompressionType();
    }


    /**
     * Sets the compression type and parameters for this variable.
     * Note that it is not possible to set the compression of a
     * non-record-varying variable after creation because a default
     * value is written during creation.
     *
     * @param type the compression type.
     * @param parms the compression parameters that go with the 
     *            specified type.  Maybe null for compression types
     *            that do not have parameters.  If null or zero-length
     *            for a compression type that requires parameters,
     *            default values will be applied.
     * @throws CDFException if a problem occurs setting the compression
     *             type and parameters.
     * @see #getCompressionType()
     * @see #getCompressionParms()
     */
    public void setCompression(long type, long[] parms) 
        throws CDFException {

        if (type == CDF.GZIP_COMPRESSION) {

            if (parms == null || parms.length == 0) {

                parms = DEFAULT_GZIP_COMPRESSION_PARMS;
            }
        }
        else {

            parms = new long[] {0};
        }
        var.setCompression(type, parms);
    }


    /**
     * Sets the compression type and parameters for this variable.
     * Note that it is not possible to set the compression of a
     * non-record-varying variable after creation because a default
     * value is written during creation.
     *
     * @param name the compression type.
     * @param parms the compression parameters that go with the 
     *            specified type.  This value is ignored for all
     *            compression types except gzip.
     * @throws CDFException if a problem occurs setting the compression
     *             type and parameters.
     * @see #getCompressionName()
     * @see #getCompressionParms()
     */
    public void setCompression(String name, long[] parms) 
        throws CDFException {

        long type = getCompressionType(name);

        setCompression(type, parms);
    }


    /**
     * Gets the compression parameters for this variable.  This is 
     * only applicable for the GZIP compression method. 
     *
     * @return the compression parameters of this variable.
     * @see #setCompression(long, long[])
     */
    public long[] getCompressionParms() {

        return var.getCompressionParms();
    }


    /**
     * Gets the compression type of this variable by name.
     *
     * @return the compression type name of this variable.
     * @see #setCompression(String, long[])
     */
    public String getCompressionName() {

        return getCompressionName(getCompressionType());
    }


    /**
     * Returns whether this variable's data type is one of TT2000,
     * EPOCH, or EPOCH16.
     *
     * @return true if this variable's data type is either TT2000,
     *             EPOCH, or EPOCH16.  Otherwise false.
     */
    public boolean isTimeType() {

        return isTimeType(getDataType());
    }


    /**
     * Returns whether the given CDF data type is one of TT2000,
     * EPOCH, or EPOCH16.
     *
     * @param cdfType CDF data type value to test.
     * @return true if the data type is either TT2000,
     *             EPOCH, or EPOCH16.  Otherwise false.
     */
    public static boolean isTimeType(long cdfType) {

        return cdfType == CDF.CDF_TIME_TT2000 ||
               cdfType == CDF.CDF_EPOCH ||
               cdfType == CDF.CDF_EPOCH16;
    }


    /**
     * Sets the blocking factor for this variable.
     *
     * @param value the new blocking factor for this variable.  A value
     *            of zero indicates that the default blocking factor
     *            should be used.
     * @see #getBlockingFactor()
     * @throws CDFException if a problem occurs setting the blocking
     *             factor.
     */
    public void setBlockingFactor(long value) 
        throws CDFException {

        var.setBlockingFactor(value);
    }


    /**
     * Gets the blocking factor for this variable.
     *
     * @return blocking factor for this variable.
     * @see #setBlockingFactor(long)
     * @throws CDFException if a problem occurs setting the blocking
     *             factor.
     */
    public long getBlockingFactor()
        throws CDFException {

        return var.getBlockingFactor();
    }


    /**
     * Sets the sparse record type for this variable.
     *
     * @param value the new sparse record type for this variable.
     * @see #getSparseRecordType()
     * @throws CDFException if a problem occurs setting the sparse
     *             record type.
     */
    public void setSparseRecordType(SparseRecordType value) 
        throws CDFException {

        var.setSparseRecords(value.value());
    }


    /**
     * Gets the sparse record type of this variable.
     *
     * @return sparse record type of this variable.
     * @see #setSparseRecordType(SparseRecordType)
     * @throws CDFException if a problem occurs setting the sparse
     *             record type.
     */
    public SparseRecordType getSparseRecordType()
        throws CDFException {

        return SparseRecordType.fromValue(var.getSparseRecords());
    }


    /**
     * Sets the pad value for this variable.
     *
     * @param value the new pad value for this variable.
     * @see #getPadValue()
     * @throws CDFException if a problem occurs setting the pad value.
     */
    public void setPadValue(Object value) 
        throws CDFException {

        var.setPadValue(value);
    }


    /**
     * Gets the pad value of this variable.
     *
     * @return pad value of this variable.  null if no pad value has 
     *             been set.
     * @see #setPadValue(Object)
     * @throws CDFException if a problem occurs setting the pad value.
     */
    public Object getPadValue()
        throws CDFException {

        return var.getPadValue();
    }


    /**
     * Sets the default pad value for this variable based upon its
     * data type.
     *
     * @see #getDefaultPadValue()
     * @throws CDFException if a problem occurs setting the pad value.
     */
    public void setDefaultPadValue() 
        throws CDFException {

        Object defaultPadValue = getDefaultPadValue();
                                       // default pad value
        if (defaultPadValue != null) {

            setPadValue(defaultPadValue);
        }
    }


    /**
     * Gets the default pad value for this variable based upon its
     * data type.
     *
     * @return default pad value for this variable.  null if no default
     *     pad value is defined.
     * @see #setDefaultPadValue()
     */
    public Object getDefaultPadValue() {

        return PadValue.getDefaultPadValue(getDataType());
    }



/*
// ISTPCompliance.EntryControl replacement
public void verifyAttributeTypesMatch(VerifyAttributeHandler h) {

    ArrayList<Entry> entries = getAttributeEntries();

    for (Entry entry: entries) {

        if (!typeMatches(entry)) {

            if (h.correctTypeMismatch(this, entry)) {

                correctEntryTypeMismatch(entry);
            }
        }
    }
}

public static interface VerifyAttributeHandler {

    public boolean correctTypeMismatch(Variable var, Entry entry);

    public void typeMismatchCorrectionError(Variable var, Entry entry);
}

public static class DefaultVerifyAttributeHandler
    implements VerifyAttributeHandler {

    public boolean correctTypeMismatch(Variable var, Entry entry) {

        return true;
    }

    public void typeMismatchCorrectionError(Variable var, Entry entry) {

    }
}

*/


// ISTPCompliance.dataTypeEqual replacement
public boolean typeMatches(Entry entry) {

    long varDataType = var.getDataType();
    long entryDataType = entry.getDataType();

    if (varDataType == entryDataType) {

        return true;
    }
    else if ((varDataType == CDF.CDF_FLOAT && 
              entryDataType == CDF.CDF_REAL4) ||
             (varDataType == CDF.CDF_REAL4 && 
              entryDataType == CDF.CDF_FLOAT)) {

        return true;
    }
    else if ((varDataType == CDF.CDF_DOUBLE && 
              entryDataType == CDF.CDF_REAL8) ||
             (varDataType == CDF.CDF_REAL8 && 
              entryDataType == CDF.CDF_DOUBLE) ||
             (varDataType == CDF.CDF_EPOCH && 
              entryDataType == CDF.CDF_REAL8) ||
             (varDataType == CDF.CDF_EPOCH && 
              entryDataType == CDF.CDF_DOUBLE) ||
             (varDataType == CDF.CDF_EPOCH16 && 
              entryDataType == CDF.CDF_REAL8) ||
             (varDataType == CDF.CDF_EPOCH16 &&
              entryDataType == CDF.CDF_DOUBLE)) {

        return true;
    }
    else {

        return false;
    }
}

}
