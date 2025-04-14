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
 * $Id: ISTPCompliance.java,v 1.171 2025/03/11 13:13:05 btharris Exp $
 */
package gsfc.spdf.istp;

import gsfc.nssdc.cdf.*;
import gsfc.nssdc.cdf.Attribute;
import gsfc.nssdc.cdf.util.CDFUtils;

import gsfc.spdf.cdf.tools.CDFTools;
import gsfc.spdf.cdf.DataType;
import gsfc.spdf.util.TextUtils;
import gsfc.spdf.util.FtnEditDescriptor;
import gsfc.spdf.util.RepeatableFtnEditDescriptor;
import gsfc.spdf.util.CConversionSpecification;
import gsfc.spdf.edit.util.SKTUtils;
import java.util.*;
import java.util.regex.Pattern;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;


/**
 * This class is a group of utility methods that are useful to test 
 * a given CDF for ISTPCompliance.
 */
public class ISTPCompliance 
    implements CDFConstants {


    /**
     * Types of compliance warnings that may be suppressed.
     */
    public enum Warnings {
        ALL,
        NON_STANDARD_FILLVAL,
        VALIDMIN_GE_VALIDMAX,
        MISSING_SPASE_ID
    }


    /**
     * A Hashtable that contains correctly cased required and 
     * recommended global attributes names. The <code>keys</code> 
     * are the attribute names in lower case and the <code>values</code> 
     * are the attribute names properly cased.
     * 
     * @see #cleanAttributeNames
     */
    public final static Hashtable<String, String> ATTR_NAMES;

    /**
     * A Hashtable that contains the long source names and 
     * short source names that are to be used to build the 
     * Logical_source_id. The <code>keys</code> 
     * are the short names <code>values</code> 
     * are the long names properly cased.
     */
//    public final static Hashtable SOURCE_NAME_MAPPINGS;
   
    static {
	ATTR_NAMES = new Hashtable<>();
	ATTR_NAMES.put("project",            "Project");
	ATTR_NAMES.put("source_name",        "Source_name");
	ATTR_NAMES.put("discipline",         "Discipline");
	ATTR_NAMES.put("descriptor",         "Descriptor");
	ATTR_NAMES.put("data_version",       "Data_version");
	ATTR_NAMES.put("data_type",          "Data_type");
	ATTR_NAMES.put("pi_name",            "PI_name");
	ATTR_NAMES.put("pi_affiliation",     "PI_affiliation");
	ATTR_NAMES.put("title",              "TITLE");
	ATTR_NAMES.put("text",               "TEXT");
	ATTR_NAMES.put("instrument_type",    "Instrument_type");
	ATTR_NAMES.put("mission_group",      "Mission_group");
	ATTR_NAMES.put("logical_source",     "Logical_source");
	ATTR_NAMES.put("logical_file_id",    "Logical_file_id");
	ATTR_NAMES.put("logical_source_description", 
			      "Logical_source_description");
	ATTR_NAMES.put("time_resolution",    "Time_resolution");
	ATTR_NAMES.put("rules_of_use",       "Rules_of_use");
	ATTR_NAMES.put("generated_by",       "Generated_by");
	ATTR_NAMES.put("generation_data",    "Generation_date");
	ATTR_NAMES.put("acknowledgement",    "Acknowledgement");
	ATTR_NAMES.put("mods",               "MODS");
//	ATTR_NAMES.put("adid_ref",           "ADID_ref");
	ATTR_NAMES.put("spase_datasetresourceid", "spase_DatasetResourceID");
	ATTR_NAMES.put("link_text",          "LINK_TEXT");
	ATTR_NAMES.put("link_title",         "LINK_TITLE");
	ATTR_NAMES.put("http_link",          "HTTP_LINK");

	// Variable Attribute names
	ATTR_NAMES.put("catdesc",      "CATDESC");
	ATTR_NAMES.put("depend_0",     "DEPEND_0");
	ATTR_NAMES.put("depend_1",     "DEPEND_1");
	ATTR_NAMES.put("depend_2",     "DEPEND_2");
	ATTR_NAMES.put("depend_3",     "DEPEND_3");
	ATTR_NAMES.put("display_type", "DISPLAY_TYPE");
	ATTR_NAMES.put("fieldnam",     "FIELDNAM");
	ATTR_NAMES.put("fillval",      "FILLVAL");
	ATTR_NAMES.put("format",       "FORMAT");
	ATTR_NAMES.put("form_ptr",     "FORM_PTR");	
	ATTR_NAMES.put("lablaxis",     "LABLAXIS");
	ATTR_NAMES.put("labl_ptr_1",   "LABL_PTR_1");
	ATTR_NAMES.put("labl_ptr_2",   "LABL_PTR_2");
	ATTR_NAMES.put("labl_ptr_3",   "LABL_PTR_3");
	ATTR_NAMES.put("units",        "UNITS");
	ATTR_NAMES.put("unit_ptr",     "UNIT_PTR");
	ATTR_NAMES.put("validmin",     "VALIDMIN");
	ATTR_NAMES.put("validmax",     "VALIDMAX");
	ATTR_NAMES.put("var_type",     "VAR_TYPE");
	ATTR_NAMES.put("scaletyp",     "SCALETYP");
	ATTR_NAMES.put("scal_ptr",     "SCAL_PTR");
	ATTR_NAMES.put("var_notes",    "VAR_NOTES");
	
/*
	SOURCE_NAME_MAPPINGS = new Hashtable();
	SOURCE_NAME_MAPPINGS.put("CANOPUS>Canadian Auroral Network Open Program Unified Study", "CN");
	SOURCE_NAME_MAPPINGS.put("DE>Dynamics Explorer", "DE");
	SOURCE_NAME_MAPPINGS.put("DARN>Dual Auroral Radar Network", "DN");
	SOURCE_NAME_MAPPINGS.put("EQ>Equator-S", "EQ");
	SOURCE_NAME_MAPPINGS.put("FAST>FAST Auroral Snapshot Explorer", "FA");
	SOURCE_NAME_MAPPINGS.put("GOES_6>Geostationary Operational Environmental Satellite 6", "G6");
	SOURCE_NAME_MAPPINGS.put("GOES_7>Geostationary Operational Environmental Satellite 7", "G7");
	SOURCE_NAME_MAPPINGS.put("GOES_8>Geostationary Operational Environmental Satellite 8", "G8");
	SOURCE_NAME_MAPPINGS.put("GOES_9>Geostationary Operational Environmental Satellite 9", "G9");
	SOURCE_NAME_MAPPINGS.put("GEOTAIL>Geomagnetic Tail", "GE");
	SOURCE_NAME_MAPPINGS.put("ISIS-1>International Satellite for Ionosphere Studies 1", "I1");
	SOURCE_NAME_MAPPINGS.put("ISIS-2>International Satellite for Ionosphere Studies 2", "I2");
	SOURCE_NAME_MAPPINGS.put("IMP-8>Interplanetary Monitoring Platform 8", "I8");
	SOURCE_NAME_MAPPINGS.put("INTERBALL-AURORAL>Interball Auroral Probe", "IA");
	SOURCE_NAME_MAPPINGS.put("INTERBALL-TAIL>Interball Tail Probe", "IT");
	SOURCE_NAME_MAPPINGS.put("INTERBALL-GROUND>Ground Based Data", "IG");
	SOURCE_NAME_MAPPINGS.put("IMAGE>Imager for Magnetopause to Aurora Global Exploration", "IM");
	SOURCE_NAME_MAPPINGS.put("LANL1990_095>Los Alamos National Laboratory 1990", "L0");
	SOURCE_NAME_MAPPINGS.put("LANL1991_080>Los Alamos National Laboratory 1989", "L1");
	SOURCE_NAME_MAPPINGS.put("LANL1994_084>Los Alamos National Laboratory 1994", "L4");
	SOURCE_NAME_MAPPINGS.put("LANL1997A>Los Alamos National Laboratory 1997", "L7");
	SOURCE_NAME_MAPPINGS.put("LANL1989_046>Los Alamos National Laboratory 1989", "l9");
	//SOURCE_NAME_MAPPINGS.put("POLAR>Polar Plasma Laboratory", "PO");
	SOURCE_NAME_MAPPINGS.put("SESAME>Satellite Experiments Simultaneous with Antarctic Measurements", "SE");
	SOURCE_NAME_MAPPINGS.put("STELAB>Solar-Terrestrial Environment Laboratory, Nagoya U.", "SL");
	SOURCE_NAME_MAPPINGS.put("SONDRESTROM>Greenland Incoherent-Scatter Radar", "SN");
	SOURCE_NAME_MAPPINGS.put("SOHO>Solar Heliospheric Observatory", "SO");
	SOURCE_NAME_MAPPINGS.put("SAMPEX>Solar Anomalous Magnetospheric Particle Explorer", "SX");
	SOURCE_NAME_MAPPINGS.put("ULYSSES>Ulysses", "UY");
	SOURCE_NAME_MAPPINGS.put("VI>Viking (Swedish satellite with auroral imager)", "VI");
	SOURCE_NAME_MAPPINGS.put("WIND>Wind Interplanetary Plasma Laboratory", "WI");
*/

    }
    

    /**
     * Infer the DISPLAY_TYPE of the given variable from its other 
     * characteristics.
     *
     * @param var the variable whose DISPLAY_TYPE is to be deduced
     * @return the infered DISPLAY_TYPE value of the given variable
     */
    public static String inferDisplayType(gsfc.nssdc.cdf.Variable var) {

        String displayType = null;

        long numDims = var.getNumDims();
		    
        switch ((int)numDims) {

        case 0:

            long dataType = var.getDataType();
                                       // variable's data type
            if (dataType == CDF.CDF_CHAR || dataType == CDF.CDF_UCHAR) {

                displayType = DisplayType.NO_PLOT_TYPE;
            }
            else {

                displayType = DisplayType.TIME_SERIES_TYPE;
            }
            break;

        case 1:

            String source = null;
            try {

                source = GlobalAttribute.getLogicalSourceValue(
                             var.getMyCDF());
            } 
            catch (CDFException e) {
                try {

                    source = GlobalAttribute.getLogicalFileIdValue(
                                 var.getMyCDF());
                } 
                catch (CDFException e2) {
                }
            }

            displayType = 
                DisplayType.createValueFromLogicalSourceVarName(
                    source, var.getName());

            if (displayType == null) {

                displayType = DisplayType.TIME_SERIES_TYPE;
            }
            break;

        case 2:

            displayType = DisplayType.IMAGE_TYPE;
            break;

        default:

            displayType = DisplayType.SPECTROGRAM_TYPE;
            break;
        }

        return displayType;
    }

    /**
     * Create and initialize a DISPLAY_TYPE attribute entry for the 
     * given variable.  The initial value is infered from its other 
     * characteristics.
     *
     * @param var the variable whose DISPLAY_TYPE is to be created.
     * @return the initial value that was set for the DISPLAY_TYPE
     *     attribute entry of the given variable.
     */
    public static String createDisplayTypeEntry(
        gsfc.nssdc.cdf.Variable var) {

        String displayType = null;

        Attribute displayTypeAttr = null;

        try {

            displayTypeAttr = Attribute.create(var.getMyCDF(), 
                                  "DISPLAY_TYPE", VARIABLE_SCOPE);
        }
        catch (CDFException e) {

            if (e.getCurrentStatus() != ATTR_EXISTS) {

                e.printStackTrace();
                return displayType;
            }
        }

        displayType = inferDisplayType(var);

        try {

            if (displayTypeAttr == null) {

                displayTypeAttr = var.getMyCDF().getAttribute(
                                      "DISPLAY_TYPE");
            }

            var.putEntry(displayTypeAttr, CDF_CHAR, displayType);
        }
        catch (CDFException e) {

            e.printStackTrace();
        }
		    
        return displayType;
    }


    /**
     * Check the given CDF.
     *
     * @param cdf the CDF file to check.
     * @param suppressedWarnings the warnings that are to be suppressed.
     * @param callback the object to report verification issues to.
     * @return A Vector.  The first element contains the global
     *         error messages and the second contains a Hashtable
     *         of variable error messages. The keys are
     *         the variable name and the values are vectors.
     * @throws CDFException if a CDFException occurs.
     */
    public static VerificationResult check(
        CDF cdf, 
        EnumSet<Warnings> suppressedWarnings,
        VerificationCallback callback)
        throws CDFException {

        Map<String, Vector<String>> varErrors;
        Vector<String> globalErrors = new Vector<>();
                                       // errors of a file-global nature

        globalErrors.addAll(checkFileCharacteristics(cdf));
        globalErrors.addAll(cleanAttributeNames(cdf));
        globalErrors.addAll(checkDataVersionAttribute(cdf));
        globalErrors.addAll(checkFunctionAttributes(cdf));
	globalErrors.addAll(
            checkGlobalAttributes(cdf, suppressedWarnings));

        Vector<String> epochErrors = checkEpoch(cdf);
                                       // epoch errors

        VariablesVerificationResult varResult = 
            checkVariables(cdf, suppressedWarnings, callback);
                                       // result of variable check
        varErrors = varResult.getErrors();

        if (varErrors != null && epochErrors.size() > 0) {

            Epoch epoch = Epoch.getEpoch(cdf);
            String epochName = epoch.getName();

            Vector<String> epochVarErrors = varErrors.get(epochName);

            if (epochVarErrors != null) {

                epochVarErrors.addAll(epochErrors);
            }
            else {

                varErrors.put(epochName, epochErrors);
            }
        }

        return new VerificationResult(globalErrors, varErrors,
                                      varResult.getStatus());
    }


    /**
     * Check that a correctly defined Epoch variable exists.
     *
     * @param cdf CDF file to check
     * @return vector containing any error messages
     * @throws CDFException if a CDF error occurs.
     */
    public static Vector<String> checkEpoch(CDF cdf) 
        throws CDFException {
        
        Vector<String> errors = new Vector<>();  // error messages
        Epoch epoch = Epoch.getEpoch(cdf);
                                       // the "ISTP epoch" variable
        if (epoch == null) {

            errors.add("Missing a required \"ISTP epoch\" variable.");
            return errors;
        }

        long compression = epoch.getCompressionType();
                                       // compression option
        if (compression != CDF.NO_COMPRESSION) {

            errors.add(epoch.getName() + 
                " has compression option set to " + 
                epoch.getCompressionName(compression) + 
                ". ISTP Epoch variables should not be compressed.");
        }

        String[] unitsAttribute = epoch.getUnitsValue();
                                       // UNITS attribute value
        if (unitsAttribute == null || unitsAttribute.length == 0) {

            errors.add("ISTP epoch variable '" + epoch.getName() + 
                "' is missing the UNITS attribute.");

            if (cdf.confirmReadOnlyMode() == CDF.READONLYoff) {

                epoch.setUnitsValue();

                errors.add("The UNITS attribute has been added.");
            }
        }

        String timeBaseAttribute = epoch.getTimeBaseValue();
                                       // TIME_BASE attribute value.
                                       // This attribute is used in
                                       // netCDF/CDF translations.
        if (timeBaseAttribute == null) {

            errors.add("ISTP epoch variable '" + epoch.getName() + 
                "' is missing the TIME_BASE attribute.");

            if (cdf.confirmReadOnlyMode() == CDF.READONLYoff) {

                epoch.setTimeBaseValue();

                errors.add("The TIME_BASE attribute has been added.");
            }
        }

        return errors;
    }


    /**
     * Check the given CDF according.
     *
     * @param filename A valid cdf or skt filename
     * @param suppressedWarnings the warnings that are to be suppressed.
     * @param callback the object to report verification issues to
     * @return A Vector.  The first element contains the global
     *         error messages and the second contains a Hashtable
     *         of variable error messages. The keys are
     *         the variable name and the values are vectors.
     * @throws CDFException if a CDF exception occurs.
     * @throws IOException if an I/O exception occurs.
     * @throws InterruptedException if an interrupted exception occurs.
     */
    public static VerificationResult check(
        String filename, 
        EnumSet<Warnings> suppressedWarnings,
        VerificationCallback callback)
        throws CDFException, IOException, InterruptedException {

	StringBuffer sb = new StringBuffer();
	CDF myCDF = null;
	sb.append(filename);
	boolean cleanup = false;
	if (filename.endsWith(".skt")) {
	    cleanup = true;
	    sb.setLength(sb.length()-4);
	    CDFTools.skeletonCDF(sb.toString(),
				 sb.toString(),
				 new File(filename).exists(),
				 false, false, false, 2,
				 CDFTools.NO_REPORTS, null);
	    myCDF = CDF.open(sb.toString()+".cdf", READONLYon);
	} else {
	    myCDF = CDF.open(filename, READONLYon);
	}
        VerificationResult result = check(myCDF, suppressedWarnings, callback);

	if (cleanup)
	    myCDF.delete();
	else
	    myCDF.close();

        return result;
    }


    /**
     * Regular expression pattern to match strings containing only
     * ASCII characters.
     */
    private static final Pattern ASCII_STRING = 
        Pattern.compile("\\p{ASCII}*?");


    /**
     * Determines if the given String value contains only ASCII 
     * characters.
     *
     * @param value string to check for ASCII characters.
     * @return true if the given string contains only ASCII characters.
     *         false if any non-ASCII characters exist in the string.
     */
    public static boolean isAscii(String value) {

        return ASCII_STRING.matcher(value).matches();
    }


    /**
     * Regular expression pattern to match strings containing only
     * printable ASCII characters.
     */
    private static final Pattern PRINTABLE_STRING = 
        Pattern.compile("\\p{Print}*?");


    /**
     * Determines if the given String value contains only printable
     * ASCII characters.
     *
     * @param value string to check for printable ASCII characters.
     * @return true if the given string contains only printable ASCII 
     *     characters.  false if any non-printable ASCII characters 
     *     exist in the string.
     */
    public static boolean isPrintable(String value) {

        return PRINTABLE_STRING.matcher(value).matches();
    }


    /**
     * Perform the global attribute checks on the <code>CDF</code>.
     *
     * @param cdf The CDF file to check for compliance
     * @param suppressedWarnings the warnings that are to be suppressed.
     * @return A Vector containing all the errors associated with 
     *         the specified test.  If no errors then this is empty.
     */
    public static Vector<String> checkGlobalAttributes(
        CDF cdf,
        EnumSet<Warnings> suppressedWarnings) {

	Vector<String> tempMessages;
	Vector<String> messages = new Vector<String>();
	
	tempMessages = checkThatGlobalAttributeValuesAreAscii(cdf);
	if (tempMessages.size() > 0)
	    for (int i=0;i< tempMessages.size(); i++) 
		messages.addElement(tempMessages.elementAt(i));
	
	// logical id check
	tempMessages = logicalIDCheck(cdf);
	if (tempMessages.size() > 0)
	    for (int i=0;i< tempMessages.size(); i++) 
		messages.addElement(tempMessages.elementAt(i));
	
	// logical_source check
	tempMessages = logicalSourceCheck(cdf);
	if (tempMessages.size() > 0)
	    for (int i=0;i< tempMessages.size(); i++) 
		messages.addElement(tempMessages.elementAt(i));

	// required global check
	tempMessages = checkRequired(cdf);
	if (tempMessages.size() > 0)
	    for (int i=0;i< tempMessages.size(); i++) 
		messages.addElement(tempMessages.elementAt(i));

        //
        // check recommended global attributes
        //
        try {

            tempMessages = checkRecommendedAttributes(
                               cdf, suppressedWarnings);

            if (tempMessages.size() > 0) {

                for (int i=0;i< tempMessages.size(); i++) {

                    messages.addElement(tempMessages.elementAt(i));
                }
            }
        }
        catch (CDFException e) {

            e.printStackTrace();
        }

	return messages;
    }


    /**
     * Checks that all global attribute entry string values only
     * contain ASCII characters.
     *
     * @param cdf cdf to check.
     * @return Vector containing any error messages.
     */
    public static Vector<String> checkThatGlobalAttributeValuesAreAscii(
        CDF cdf) {

        Vector<String> messages = new Vector<>();// error messages
	
        Vector globalAttributes = cdf.getGlobalAttributes();
                                       // all global attributes
        for (int i = 0; i < globalAttributes.size(); i++) {

            Attribute gAttr = (Attribute)globalAttributes.get(i);
                                       // a global attribute
            try {

                Vector entries = gAttr.getEntries();
                                       // attribute entries
                for (int j = 0; j < entries.size(); j++) {

                    Entry entry = (Entry)entries.get(j);
                                       // attribute entry
                    if (entry != null) {

                        Object value = entry.getData();
                                       // entry value
                        long dataType = entry.getDataType();
                                       // entry's data type
                        if (value != null && value instanceof String) {

                            String strValue = (String)value;

                            if (!isAscii(strValue)) {

                                messages.addElement(
                                    "Global attribute " + gAttr.getName() + 
                                    " contains non-ASCII characters in " +
                                    "entry " + entry.getName() + ".");
                                messages.addElement(
                                    "    The non-ASCII characters " +
                                    "are encoded as Unicode " +
                                    "characters in the following " +
                                    "representation of the " +
                                    "offending entry.");
                                messages.addElement(
                                    "    " + toAscii(strValue));
                                messages.addElement(
                                    "    Unicode characters require CDF " +
                                    "3.8.1 or higher.");
                                messages.addElement(
                                    "    Non-ASCII characters may not " +
                                    "be supported by some software.");
                            }
                        }
                        else if (dataType != CDF.CDF_CHAR) {

                            messages.addElement(
                                "Global attribute " + gAttr.getName() + 
                                " is of type " + 
                                DataType.getString(dataType) + ".");
                            messages.addElement(
                                "    Datatypes other than CDF_CHAR " +
                                "may be problematic.");
                        }
                    }
                }
            }
            catch (CDFException e) {

                // no entries to check
            }
        }

        return messages;
    }


    /**
     * Converts any non-ascii characters in the given string to their
     * Unicode character representation in the returned String.
     * 
     * @param value String to be converted.
     * @return given String with any non-ascii characters represented 
     *     by their Unicode value.
     */
    public static String toAscii(String value) {

        StringBuilder asciiStr = new StringBuilder();
                                       // resulting String
        for (int i = 0; i < value.length(); i++) {

            int codePoint = value.codePointAt(i);

            if (codePoint > 0x7f) {

                asciiStr.append(String.format("{U+%04X}", codePoint));

                if (!Character.isBmpCodePoint(codePoint)) {
                    i++;
                }
            }
            else {

                asciiStr.appendCodePoint(codePoint);
            }
        }

        return asciiStr.toString();
    }


    public static Vector<String> checkGlobalAttributes(
        String filename,
        EnumSet<Warnings> suppressedWarnings) 
    	throws CDFException, java.io.IOException, 
               java.lang.InterruptedException {

	StringBuffer sb = new StringBuffer();
	CDF myCDF = null;
	sb.append(filename);
	boolean cleanup = false;
	if (filename.endsWith(".skt")) {
	    cleanup = true;
	    sb.setLength(sb.length()-4);
	    CDFTools.skeletonCDF(sb.toString(),
				 sb.toString(),
				 new File(filename).exists(),
				 false, false, false, 2,
				 CDFTools.NO_REPORTS, null);
	    myCDF = CDF.open(sb.toString()+".cdf", READONLYon);
	} else {
	    myCDF = CDF.open(filename, READONLYon);
	}

	Vector<String> rv = 
            checkGlobalAttributes(myCDF, suppressedWarnings);

	if (cleanup)
	    myCDF.delete();
        else
	    myCDF.close();

	return rv;
    }


    /**
     * Perform the variable checks on all
     * the variables contained in the <code>CDF</code>.
     *
     * @param cdf The CDF file to check for compliance
     * @param suppressedWarnings the warnings that are to be suppressed.
     * @param callback the object to report verification issues to.
     * @return A Hashtable for which the keys are variables that
     *         had errors and the value is a Vector containing all
     *         the errors associated with the specified test.
     */
    public static VariablesVerificationResult checkVariables(
        CDF cdf, 
        EnumSet<Warnings> suppressedWarnings,
        VerificationCallback callback) {

        Map<String, Vector<String>> varMessages = new LinkedHashMap<>();
        Vector variables = cdf.getVariables();
                        
        Vector varClone = (Vector)variables.clone();

	for (int i = 0; i <variables.size(); i++) {

            gsfc.nssdc.cdf.Variable var = 
                (gsfc.nssdc.cdf.Variable)varClone.elementAt(i);
                                       // i'th variable            
            VariableVerificationResult result = null;
                                       // verification result
            Vector<String> messages = new Vector<>();
                                       // error messages
            
/* 11/8/13, B. Harris
   I think the following code creates a problem (corrupting label values)
   by deleting a CDF variable that other parts of the code continues to
   use.  At this time, I do not have a fix.  But leaving the extra blanks
   in the values is better than corrupting them (and having code use a
   CDF variable that has been deleted).

            boolean labelTrimmed = false;

            try{
                       
                if((var.getNumDims() == 0 || var.getNumDims() == 1) &&
                   (!var.getRecVariance()) &&
                   var.getDataType() == CDF_CHAR &&
                   var.getNumWrittenRecords() > 0){ 
                     
                    Object data = SKTUtils.stripBlanks(var.getRecord(0));
                       
                    if(SKTUtils.getMaxNumElements(data)< var.getNumElements()){
                            
                            Hashtable curVarAttrs = new Hashtable(); 
                            
                            for (Enumeration e = cdf.getVariableAttributes().elements(); 
                                    e.hasMoreElements();) {
                                
                                try{
                            
                                    Attribute curAttr = (Attribute)e.nextElement();
                            
                                    curVarAttrs.put(curAttr.getName(),
				    curAttr.getEntry(var).getData());
                        
                                }catch (CDFException ex) {}                                            
                            }
		
                             // delete the existing one and recreate
                            String varname = var.getName();
                            long dataType = var.getDataType();
                            long numElements = SKTUtils.getMaxNumElements(data);
                            long numDims = var.getNumDims();
                            long[] dimSizes = var.getDimSizes();
                            if (dimSizes == null) {

                                dimSizes = new long[0];
                            }
                            long recVariance = 
                                var.getRecVariance() ? VARY : NOVARY;
                            long[] dimVariances = var.getDimVariances();
                            if (dimVariances == null) {

                                dimVariances = new long[0];
                            }
                   
                            var.delete();      
    
                            var = gsfc.nssdc.cdf.Variable.create(cdf, varname, 
                                    dataType, numElements, numDims, dimSizes, 
                                    recVariance, dimVariances);                                      
                            // put the attributes on the new variable
                            String key;
                            for (Enumeration e = curVarAttrs.keys();
                                e.hasMoreElements();) {
                
                                key = (String)e.nextElement();
                
                                var.putEntry(key, CDF_CHAR, 
				    curVarAttrs.get(key).toString());
                            }	   
                            // Save the data if there is any
                            if (data != null) {
		    
                                var.putRecord(0, data);
                            }
                
                            labelTrimmed = true;
                  
                        }
                    } 
        
            }
            catch (CDFException cdfex) {

                // This entire try block is covering too much code
                // but I don't have time to re-write all this code.
                // Logging the exception is better than ignoring it
                // as before.

                System.err.println(
                    "ISTPCompliance.checkVariables: CDFException: " +
                    cdfex.getMessage());
            }
                   
           if(labelTrimmed)messages.addElement("Values trailing blanks were trimmed, and " + var.getName() + " relocated to botton of list");           
*/
            
            result = checkVariable(var, suppressedWarnings, callback);

            messages.addAll(result.getErrors());
            
            if (result.getStatus() == VerificationResult.ABORTED) {

                varMessages.put(var.toString(), messages);

                return new VariablesVerificationResult(
                    varMessages, VerificationResult.ABORTED);
            }

	    if (messages.size() > 0) 
		varMessages.put(var.toString(), messages);

	}
 
        return new VariablesVerificationResult(varMessages, 
                       VerificationResult.COMPLETE);
    }


    /**
     * Checks that all variable attribute entry string values only
     * contain ASCII characters.
     *
     * @param var the variable whose attribute are to be check.
     * @param callback the object to report verification issues to.
     * @return result of check.
     */
    public static VariableVerificationResult 
        checkThatAttributeValuesAreAscii(
        Variable var, VerificationCallback callback) {

        Vector<String> messages = new Vector<>();// error messages
	
        Vector varAttributes = var.getAttributes();
                                       // this variable's attributes
        for (int i = 0; i < varAttributes.size(); i++) {

            Attribute vAttr = (Attribute)varAttributes.get(i);
                                       // a variable attribute
            Entry entry = null;        // attribute entry
            try {

                entry = vAttr.getEntry(var.getCdfVariable());
                Object value = entry.getData();
                                       // entry value
                if (value != null && value instanceof String) {

                    String strValue = (String)value;
                                       // String representation of value
                    if (!isAscii(strValue)) {

                        String msg =
                            vAttr.getName() + " attribute contains " +
                            "non-ASCII characters." +
                            "\n        The non-ASCII characters " +
                            "are encoded as Unicode characters in " +
                            "the following representation of the " +
                            "offending value." +
                            "\n        " + toAscii(strValue) +
                            "\n        Unicode characters require CDF " +
                            "3.8.1 or higher. " +
                            "\n        Non-ASCII characters may not be " +
                            "supported by some software.";
                                       // error message
                        int cbStatus =
                            callback.handleNonAsciiAttributeValueError(
                                var, vAttr, entry);
                                        // callback status
                        switch (cbStatus) {

                        case VerificationCallback.CORRECTED:

                            // just continue
                            break;

                        case VerificationCallback.ABORT:

                            messages.addElement(msg);

                            return new VariableVerificationResult(
                                       messages,
                                       VerificationResult.ABORTED);

                        default:

                            // report uncorrected error and continue
                            messages.addElement(msg);
                            break;
                        }
                    }
                }
            }
            catch (CDFException e) {

                // if no Entry then there is nothing to check
            }
        }

        return new VariableVerificationResult(
                       messages, VerificationResult.COMPLETE);
    }



    public static VariablesVerificationResult checkVariables(
        String filename, 
        EnumSet<Warnings> suppressedWarnings,
        VerificationCallback callback) 
        throws CDFException, IOException, InterruptedException {

	StringBuffer sb = new StringBuffer();
	CDF myCDF = null;
	sb.append(filename);
	boolean cleanup = false;
	if (filename.endsWith(".skt")) {
	    cleanup = true;
	    sb.setLength(sb.length()-4);
	    CDFTools.skeletonCDF(sb.toString(),
				 sb.toString(),
				 new File(filename).exists(),
				 false, false, false, 2,
				 CDFTools.NO_REPORTS, null);
	    myCDF = CDF.open(sb.toString()+".cdf", READONLYon);
	} else {
	    myCDF = CDF.open(filename, READONLYon);
	}

	VariablesVerificationResult result = 
            checkVariables(myCDF, suppressedWarnings, callback);

	if (cleanup)
	    myCDF.delete();
	else
	    myCDF.close();

	return result;
    }


    public static Vector<String> checkVariable(
        gsfc.nssdc.cdf.Variable var,
        EnumSet<ISTPCompliance.Warnings> suppressedWarnings) {

        VariableVerificationResult result =
             checkVariable(var, 
                 suppressedWarnings,
                 new DefaultVerificationCallback());
                                       // result of variable verification
        return result.getErrors();
    }


    /**
     * Gets the Attribute Entry value of the specified Variable Attribute
     * as a String value.  If the specified Variable Attribute is not of
     * type CDF_CHAR, a zero-length String array is returned.
     *
     * @param var Variable whose Attribute value is being requested.
     * @param name name of Attribute to get.
     * @return String array containing the value of the specified Variable
     *     Entry.  A zero-length array is returned if the Attribute Entry
     *     is not of type CDF_CHAR.
     * @throws CDFException if a CDFException occurs.
     */
    private static String[] getStringAttribute(
        gsfc.nssdc.cdf.Variable var,
        String name)
        throws CDFException {

        Object entryObj = var.getEntryData(name);

        if (entryObj instanceof String) {

            return new String[] {(String)entryObj};
        }
        else if (entryObj instanceof String[]) {

            return (String[])entryObj;
        }
        else {

            return new String[] {};
        }
    }

    /**
     * Checks that the given Variable Attribute Entry value has less
     * than two values.  
     * 
     * @param var Variable whose Attribute Entry is to be checked.
     * @param attributeName name of Attribute to be checked.
     * @param attributeValue Attribute value to be checked.
     * @param callback the object to report verification issues to.
     * @return results of check.
     */
    private static VariableVerificationResult checkAttributeCardinality(
        Variable var, 
        String attributeName,
        Object attributeValue,
        VerificationCallback callback) {

        Vector<String> messages = new Vector<String>();
                                       // error messages
        try {

            if (attributeValue != null &&
                Array.getLength(attributeValue) > 1) {

                String msg = attributeName + " has multiple values " +
                                      "when it should be a single value.";
                                      // error message
                int cbStatus = callback.handleMultiValueAttribute(
                                   var, attributeName, attributeValue);
                                      // callback status
                switch (cbStatus) {

                case VerificationCallback.CORRECTED:

                    // just continue
                    break;

                case VerificationCallback.ABORT:

                    messages.addElement(msg);

                    return new VariableVerificationResult(
                                   messages, VerificationResult.ABORTED);

                default:

                    // report uncorrected error and continue
                    messages.addElement(msg);
                    break;
                }
            }
        }
        catch (IllegalArgumentException e) {

            // if not an array, then cardinality is fine
        }

        return new VariableVerificationResult(messages,
                       VerificationResult.COMPLETE);
    }


    /**
     * Check the variable <code>var</code> for ISTP compliance. 
     *
     * The following items are checked
     * <OL>
     *   <LI>Existence of all required variable attributes
     *   <LI>Dependancy (if needed).
     *   <LI>Labelling if needed.
     * </OL>
     *
     * @param var The variable to test.
     * @param suppressedWarnings the warnings that are to be suppressed.
     * @param callback the object to report verification issues to.
     * @return A Vector containing all the errors associated with the
     *         specified test.  If no errors then this is empty.
     */
    public static VariableVerificationResult checkVariable(
        gsfc.nssdc.cdf.Variable var, 
        EnumSet<Warnings> suppressedWarnings,
        VerificationCallback callback) {

	Vector<String> tempMessages;
	Vector<String> messages = new Vector<>();
	CDF cdf = var.getMyCDF();
	boolean recVary = false, ignore = true;
	long dataType = CDF_BYTE, nDims = 0;

        try {

            Variable.checkName(cdf, var);
        }
        catch (ISTPIdentifierException e) {

            String conflictName = e.getConflictingName();
                                      // name of conflicting variable
            gsfc.spdf.istp.Variable conflictVar = null;
                                      // conflicting variable
            if (conflictName != null) {

                try {

                    conflictVar =
                        new gsfc.spdf.istp.Variable(
                                cdf.getVariable(conflictName));
                }
                catch (CDFException ex) {

                    System.err.println(
                        "ISTPCompliance.checkVariables: failed to " +
                        "get conflicting variable '" + conflictName + 
                        "': " + ex.getMessage());
                }
            }

            int cbStatus = callback.handleVariableNameError(
                    new gsfc.spdf.istp.Variable(var), conflictVar);
                                        // callback status
            switch (cbStatus) {

            case VerificationCallback.CORRECTED:

                // just continue
                break;

            case VerificationCallback.ABORT:

                messages.addElement(e.getMessage());

                return new VariableVerificationResult(
                                   messages,
                                   VerificationResult.ABORTED);

            default:

                // report uncorrected error and continue
                messages.addElement(e.getMessage());
                break;
            }
        }

        Variable istpVar = new Variable(var);
                                       // ISTP representation of var

        VariableVerificationResult dimSizesResult =
            checkDimSizes(istpVar, callback);

        messages.addAll(dimSizesResult.getErrors());

        if (dimSizesResult.getStatus() == VerificationResult.ABORTED) {

            return new VariableVerificationResult(
                           messages, VerificationResult.ABORTED);
        }
	    
        VariableVerificationResult result =
            checkThatAttributeValuesAreAscii(istpVar, callback);

        messages.addAll(result.getErrors());

        if (result.getStatus() == VerificationResult.ABORTED) {

            return new VariableVerificationResult(
                           messages, VerificationResult.ABORTED);
        }
	    
        VariableVerificationResult dupAttrResult =
            checkDupAttributeNames(var, callback);
                                       // result of callback

        messages.addAll(dupAttrResult.getErrors());

        if (dupAttrResult.getStatus() == VerificationResult.ABORTED) {

            return new VariableVerificationResult(messages,
                                         VerificationResult.ABORTED);
        }

        VariableVerificationResult unitsResult =
            checkUnits(istpVar, callback);
                                       // result of callback

        messages.addAll(unitsResult.getErrors());

        if (unitsResult.getStatus() == VerificationResult.ABORTED) {

            return new VariableVerificationResult(messages,
                                         VerificationResult.ABORTED);
        }

        VariableVerificationResult deltaVarResult =
            checkDeltaVar(istpVar, callback);
                                       // result of callback

        messages.addAll(deltaVarResult.getErrors());

        if (deltaVarResult.getStatus() == VerificationResult.ABORTED) {

            return new VariableVerificationResult(messages,
                                         VerificationResult.ABORTED);
        }

	// Check to make sure that the var has the required attributes.
	try {

            String[] varTypeArray = getStringAttribute(var, "VAR_TYPE");

            VariableVerificationResult varTypeCardResult =
                checkAttributeCardinality(istpVar, "VAR_TYPE", 
                    varTypeArray, callback);

            messages.addAll(varTypeCardResult.getErrors());

            if (varTypeCardResult.getStatus() == 
                VerificationResult.ABORTED) {

            return new VariableVerificationResult(messages,
                                         VerificationResult.ABORTED);
            }
            String vartype = varTypeArray[0];
	    recVary        = var.getRecVariance();
	    dataType       = var.getDataType();
	    nDims          = var.getNumDims();
            String name = var.getName();       
                                       // variable's name

            if (name.equals("Epoch") && 
                (dataType == CDF_TIME_TT2000 || 
                 dataType == CDF_EPOCH || dataType == CDF_EPOCH16) &&
                nDims != 0) {

                messages.addElement("Dimension error: has dimension " + 
                                    nDims + " and it should be 0");
/*
                messages.addElement("Deleting Epoch variable");
                var.delete();
                messages.addElement("Re-creating Epoch variable");
                Epoch epoch = new Epoch(cdf);

                return messages;
*/
            }

            if (!gsfc.spdf.istp.Variable.isValidType(vartype)) {

                messages.addElement("VAR_TYPE value '" + vartype +
                    "' is not a valid value.");
            }
                
            if (vartype.equalsIgnoreCase("data")) {

                tempMessages = checkDisplayType(var);

                for (int i = 0; i < tempMessages.size(); i++) {

                    messages.addElement(tempMessages.elementAt(i));
                }
            }

	    // VAR_TYPE must be present to check depends

	    // these do not need DEPEND attributes
	    ignore = (vartype.equalsIgnoreCase("metadata") ||
		      ((dataType == CDF_TIME_TT2000) && (recVary) && (nDims == 0)) ||
		      ((dataType == CDF_EPOCH) && (recVary) && (nDims == 0)) ||
                      ((dataType == CDF_EPOCH16) && (recVary) && (nDims == 0)) ||
		   //   ((vartype.equalsIgnoreCase("support_data"))) ||
                      (vartype.equalsIgnoreCase("ignore_data"))
		       );
                                      
		tempMessages = extraDependCheck(var, ignore);
		if (tempMessages.size() > 0)
		    for (int i=0;i< tempMessages.size(); i++) 
			messages.addElement(tempMessages.elementAt(i));

        if (!ignore && recVary ) {
		tempMessages = dependCheck(var);
		if (tempMessages.size() > 0)
		    for (int i=0;i< tempMessages.size(); i++) 
			messages.addElement(tempMessages.elementAt(i));
	    }

	    // VAR_TYPE must be present to check labels
	    // Only check non metadata variables
	    ignore = vartype.equalsIgnoreCase("metadata")||
                     vartype.equalsIgnoreCase("ignore_data");

	    if (!ignore) {
		tempMessages = labelCheck(var);
		if (tempMessages.size() > 0)
		    for (int i=0;i< tempMessages.size(); i++) 
			messages.addElement(tempMessages.elementAt(i));

		tempMessages = minmaxCheck(var, suppressedWarnings);
		if (tempMessages.size() > 0)
		    for (int i=0;i<tempMessages.size(); i++)
			messages.addElement(tempMessages.elementAt(i));
	    }

            tempMessages = checkVirtual(var);

            for (int i = 0; i < tempMessages.size(); i++) {

                messages.addElement(tempMessages.elementAt(i));
            }
  
            ignore = vartype.equalsIgnoreCase("metadata")||
                     vartype.equalsIgnoreCase("ignore_data") ||
                     (vartype.equalsIgnoreCase("support_data")&& 
                     (!recVary) &&
                    (var.getDataType()!= CDF.CDF_TIME_TT2000)&&
                    (var.getDataType()!= CDF.CDF_EPOCH)&&
                    (var.getDataType()!= CDF.CDF_EPOCH16));
            if (!ignore)
            {
                tempMessages = fillvalCheck(var);
                for (int i=0;i< tempMessages.size(); i++) 
                    messages.addElement(tempMessages.elementAt(i));

                if (!(suppressedWarnings.contains(
                          Warnings.NON_STANDARD_FILLVAL) ||
                      suppressedWarnings.contains(Warnings.ALL))) {

                    tempMessages = fillvalStandardCheck(var);
                    messages.addAll(tempMessages);
                }
            }	        

           if (!var.getRecVariance() && (var.getNumDims() <= 1)) {
                
                try {
                    
                    Object data = var.getRecord(0);
                    Object validmin = var.getEntryData("VALIDMIN"); 
                    Object validmax = var.getEntryData("VALIDMAX"); 
                                        
                    if(data != null && validmin != null && validmax != null &&
                        dataType != CDF.CDF_CHAR && 
                        dataType != CDF_TIME_TT2000 &&
                        dataType != CDF_EPOCH &&
                        dataType != CDF_EPOCH16 
                     ){
                                                                 
                        if(data.getClass().isArray()){
                          
                            Number value = valuesOutsideBounds(data,validmin,validmax);
                            
                            if(value != null)
                                                                 
                                 messages.addElement("data value: " +value +" is outside the valid range of values.");
                                                                                                    
                        }
                    }                             
                } catch (CDFException e) {}
           }                        
	} 
        catch (CDFException e) {

            if (e.getCurrentStatus() == CDFConstants.NO_SUCH_ENTRY) {

                messages.addElement("VAR_TYPE is missing.  (" + 
                    e.getMessage() + ")");

                try {

                    String[] defaultVarType = istpVar.setDefaultType();
                    messages.addElement("Setting VAR_TYPE value to " + 
                        defaultVarType[0] + ".");
                }
                catch (CDFException e1) {

                    System.err.println(e1.getMessage() + 
                        " while attempting to set a default VAR_TYPE " +
                        "value on " + var.getName());
                }
            }
            else {

                messages.addElement("CDFException accessing VAR_TYPE.  " 
                    + e.getMessage());
                messages.addElement("This error should be corrected " +
                    "with other CDF tools before continuing.");
               
                return new VariableVerificationResult(messages,
                                         VerificationResult.ABORTED);
            }
	}

	try {
	    var.getEntryData("FIELDNAM");
	} catch (CDFException e) {
	    messages.addElement("FIELDNAM is missing.  (" + e.getMessage() + ")");
	}

	try {
	    var.getEntryData("CATDESC");
	} catch (CDFException e) {
	    messages.addElement("CATDESC is missing.  (" + e.getMessage() + ")");
	}
	

        if (dataType != CDF_TIME_TT2000 && dataType != CDF_EPOCH && 
            dataType !=CDF_EPOCH16) {

            tempMessages = checkFormat(var);

            for (int i=0; i < tempMessages.size(); i++) {

                messages.addElement(tempMessages.elementAt(i));
            }
        }
                
        return new VariableVerificationResult( messages,
                                   VerificationResult.COMPLETE);
    }

    //////////////////////////////////////////////////////////////
    //                                                          //
    //                     Variable Tests                       //
    //                                                          //
    //////////////////////////////////////////////////////////////


    /**
     * Checks that the variable's dimension sizes are sane.
     *
     * @param var the variable whose dimension sizes are to be check.
     * @param callback the object to report verification issues to.
     * @return result of check.
     */
    public static VariableVerificationResult checkDimSizes(
        Variable var, VerificationCallback callback) {

        Vector<String> messages = new Vector<>();// error messages

        long numDims = var.getNumDims();
                                       // number of dimensions
        if (numDims > 0) {

            long[] dimSizes = var.getDimSizes();
                                       // dimension sizes
            if (dimSizes == null) {

                String msg =
                    var.getName() + " has a dimension of " + numDims +
                    " but the sizes of the dimensions is missing.";
                                       // error message
                int cbStatus =
                    callback.handleMissingDimSizeError(
                        var, numDims);
                                        // callback status
                switch (cbStatus) {

                case VerificationCallback.CORRECTED:

                    // just continue
                    break;

                case VerificationCallback.ABORT:

                    messages.addElement(msg);

                    return new VariableVerificationResult(
                               messages,
                               VerificationResult.ABORTED);

                default:

                    // report uncorrected error and continue
                    messages.addElement(msg);
                            break;
                }
            }
            else {

                long[] dimVariances = var.getDimVariances();

                for (int i = 0; i < dimSizes.length; i++) {

                    if (dimSizes[i] < 2) {

                        String msg =
                            var.getName() + "'s " + i + 
                            " dimension has a size of " + dimSizes[i] +
                            ".  Sizes < 2 are likely to be a problem.";
                                       // error message
                        int cbStatus =
                            callback.handleBadDimSizeError(
                                var, i, dimSizes[i]);
                                        // callback status
                        switch (cbStatus) {

                        case VerificationCallback.CORRECTED:

                            // just continue
                            break;

                        case VerificationCallback.ABORT:

                            messages.addElement(msg);

                            return new VariableVerificationResult(
                                       messages,
                                       VerificationResult.ABORTED);

                        default:

                            // report uncorrected error and continue
                            messages.addElement(msg);
                            break;
                        }
                    }
                    else {   // dimSizes[i] > 1

                        if (dimVariances[i] != CDF.VARY) {

                            String msg =
                                var.getName() + "'s " + i + 
                                " dimension has a size of " + dimSizes[i] +
                                " and dimension variance of " +
                                (dimVariances[i] == CDF.VARY ? "VARY" : 
                                "NOVARY") + ".";

                            int cbStatus =
                                callback.handleDimVarianceError(
                                    var, i, dimSizes[i], dimVariances[i]);
                                        // callback status
                            switch (cbStatus) {

                            case VerificationCallback.CORRECTED:

                                // just continue
                                break;

                            case VerificationCallback.ABORT:

                                messages.addElement(msg);

                                return new VariableVerificationResult(
                                           messages,
                                           VerificationResult.ABORTED);

                            default:

                                // report uncorrected error and continue
                                messages.addElement(msg);
                                break;
                            }
                        }
                    }  // if dimSizes[i]
                }  // for i
            }
        }

        return new VariableVerificationResult(
                       messages, VerificationResult.COMPLETE);
    }

  
    /**
     * Checks the given variable for duplicately named (in a case-blind
     * sense) attributes.
     *
     * @param var the variable to check
     * @param callback interface to call if a duplicate is detected.
     * @return descriptions of any duplicately named attributes
     */
    private static VariableVerificationResult checkDupAttributeNames(
        gsfc.nssdc.cdf.Variable var, 
        VerificationCallback callback) {

        Vector<String> messages = new Vector<>();
                                       // error messages
        Vector attributes = var.getAttributes();
                                       // variable's attributes

        for (int i = 0; i < attributes.size(); i++) {

            Attribute attribute = (Attribute)attributes.elementAt(i);
                                       // i'th attribute

            for (int j = 0; j < attributes.size(); j++) {

                Attribute attribute2 = 
                    (Attribute)attributes.elementAt(j);
                                       // j'th attribute

                if (attribute.getID() != attribute2.getID() &&
                    attribute.getName().equalsIgnoreCase(
                                            attribute2.getName())) {

                    String msg =
                        "Attributes named " + attribute.getName() +
                        " and " + attribute2.getName() + 
                        " have names that differ only in case.";
                                        // error message
                    int cbStatus = 
                        callback.handleDuplicateAttributeNameError(
                                               attribute, attribute2);
                                        // callback status
                    switch (cbStatus) {

                    case VerificationCallback.CORRECTED:

                        // just continue
                        break;

                    case VerificationCallback.ABORT:

                        messages.addElement(msg);

                        return new VariableVerificationResult(
                                   messages,
                                   VerificationResult.ABORTED);

                    default:

                        // report uncorrected error and continue
                        messages.addElement(msg);
                        break;
                    }

/*
                    messages.addElement(
                        "Attributes named " + attribute.getName() +
                        " and " + attribute2.getName() + 
                        " have names that differ only in case.");
*/
                }
            }

            String error = Idl.validateIdentifier(attribute.getName());
                                       // error message

            if (error != null) {

                String msg = "Variable '" + var.getName() + 
                    "' has an invalid attribute. " + error;
                                        // error message

                int cbStatus = callback.handleAttributeNameError(
                                                   attribute, error);
                                        // callback status
                switch (cbStatus) {

                case VerificationCallback.CORRECTED:

                    // just continue
                    break;

                case VerificationCallback.ABORT:

                    messages.addElement(msg);

                    return new VariableVerificationResult(
                                       messages,
                                       VerificationResult.ABORTED);

                default:

                    // report uncorrected error and continue
                    messages.addElement(msg);
                    break;
                }
/*
                messages.addElement(
                    "Variable '" + var.getName() + 
                    "' has an invalid attribute. " + error);
*/
            }

        }

        return new VariableVerificationResult(messages, 
                                              VerificationResult.COMPLETE);
    }
    

    /**
     * Checks the given variable for UNITS or UNIT_PTR attributes.
     *
     * @param var the variable to check
     * @param callback the object to report verification issues to.
     * @return descriptions of problems with UNITS or UNIT_PTR attributes.
     */
    private static VariableVerificationResult checkUnits(
        Variable var, 
        VerificationCallback callback) {

        Vector<String> messages = new Vector<>();
                                       // error messages
        String[] varType = var.getType();  
                                       // ISTP VAR_TYPE
        if (varType != null && varType.length > 0 &&
            !varType[0].equalsIgnoreCase(Variable.DATA) &&
            !varType[0].equalsIgnoreCase(Variable.SUPPORT_DATA)) {

            return new VariableVerificationResult(messages,
                                         VerificationResult.COMPLETE);
        }

        String[] units = new String[] {};
                                       // variable's UNITS value
        try {

            units = var.getUnitsValue();
        }
        catch (CDFException e) {

            // continue with null value
        }

        VariableVerificationResult varTypeCardResult =
            checkAttributeCardinality(var, "UNITS", units, 
                callback);

        messages.addAll(varTypeCardResult.getErrors());

        if (varTypeCardResult.getStatus() == VerificationResult.ABORTED) {

            return new VariableVerificationResult(messages,
                                         VerificationResult.ABORTED);
        }

        String[] unitPtr = var.getCharAttributeValue("UNIT_PTR");

        varTypeCardResult =
            checkAttributeCardinality(var, "UNIT_PTR", unitPtr, 
                callback);

        messages.addAll(varTypeCardResult.getErrors());

        if (varTypeCardResult.getStatus() == VerificationResult.ABORTED) {

            return new VariableVerificationResult(messages,
                                         VerificationResult.ABORTED);
        }

        if (unitPtr == null || unitPtr.length == 0) {

            if (units == null || units.length == 0) {

                String msg =
                    "UNITS (and UNIT_PTR) attribute is missing.";
                                        // error message
                int cbStatus = 
                    callback.handleMissingUnits(var);
                                        // callback status
                switch (cbStatus) {

                case VerificationCallback.CORRECTED:

                    // just continue
                    break;

                case VerificationCallback.ABORT:

                    messages.addElement(msg);

                    return new VariableVerificationResult(
                               messages,
                               VerificationResult.ABORTED);

                default:

                    // report uncorrected error and continue
                    messages.addElement(msg);
                    break;
                }
            }
        }
        else {

            if (units != null && units.length > 0) {

                messages.addElement("Variable has both a UNITS and " +
                    "UNIT_PTR attributes.");
            }

            Variable unitPtrVar = var.getVariable(unitPtr[0]);

            if (unitPtrVar == null) {

                String msg =
                      "UNIT_PTR value (" + unitPtr[0] + ") does not " +
                      "point to an existing variable.";
                                      // error message
                int cbStatus = callback.handleBadPointerTarget(
                                   var, "UNIT_PTR", unitPtr[0]);
                                      // callback status
                switch (cbStatus) {

                case VerificationCallback.CORRECTED:

                    // just continue
                    break;

                case VerificationCallback.ABORT:

                    messages.addElement(msg);

                    return new VariableVerificationResult(
                               messages,
                               VerificationResult.ABORTED);

                default:

                    // report uncorrected error and continue
                    messages.addElement(msg);
                    break;
                }
            }
        }

        return new VariableVerificationResult(messages, 
                                              VerificationResult.COMPLETE);
    }


    /**
     * Checks the given variable's DETLA_PLUS/MINUS_VAR attributes.
     *
     * @param var the variable to check.
     * @param callback the object to report verification issues to.
     * @return descriptions of problems with DETLA_PLUS/MINUS_VAR 
     *     attributes.
     */
    private static VariableVerificationResult checkDeltaVar(
        Variable var, 
        VerificationCallback callback) {

        Vector<String> messages = new Vector<>();
                                       // error messages
        for (String plusMinus : new String[] {"PLUS", "MINUS"}) {

            String deltaName = "DELTA_" + plusMinus + "_VAR";

            try {

                Entry deltaVarEntry = var.getAttributeEntry(deltaName);

                if (deltaVarEntry != null) {

                    long deltaVarEntryType = deltaVarEntry.getDataType();

                    if (deltaVarEntryType == CDF.CDF_CHAR) {

                        String deltaVar = null;
                                       // DELTA_*_VAR value (name)
                        Object deltaVarObj = deltaVarEntry.getData();

                        VariableVerificationResult deltaVarCardResult =
                            checkAttributeCardinality(var, deltaName, 
                                deltaVarObj, callback);

                        messages.addAll(deltaVarCardResult.getErrors());

                        if (deltaVarCardResult.getStatus() == 
                            VerificationResult.ABORTED) {

                            return new VariableVerificationResult(messages,
                                         VerificationResult.ABORTED);
                        }
                        if (deltaVarObj instanceof String) {

                            deltaVar = (String)deltaVarObj;
                        }
                        else if (deltaVarObj instanceof String[]) {

                            deltaVar = ((String[])deltaVarObj)[0];
                        }

                        Variable target = var.getVariable(deltaVar);

                        if (target == null) {

                            String msg = deltaName + " does not point " +
                                "to an actual CDF variable.";
                                        // error message
                            int cbStatus = 
                                callback.handleBadPointerTarget(
                                    var, deltaName, deltaVar);
                                        // callback status
                            switch (cbStatus) {

                            case VerificationCallback.CORRECTED:

                                // just continue
                                break;

                            case VerificationCallback.ABORT:

                                messages.addElement(msg);

                                return new VariableVerificationResult(
                                       messages,
                                       VerificationResult.ABORTED);

                            default:

                                // report uncorrected error and continue
                                messages.addElement(msg);
                                break;
                            }
                        }
                    }
                    else {

                        String msg = deltaName + " has a data type of " +
                            CDFUtils.getStringDataType(deltaVarEntryType) +
                            ".  It should be CDF_CHAR.";
                                        // error message
                        int cbStatus = 
                            callback.handleBadEntryDataType(
                                deltaVarEntry,
                                CDF.CDF_CHAR);
                                        // callback status
                        switch (cbStatus) {

                        case VerificationCallback.CORRECTED:

                            // just continue
                            break;

                        case VerificationCallback.ABORT:

                            messages.addElement(msg);

                            return new VariableVerificationResult(
                                   messages,
                                   VerificationResult.ABORTED);

                        default:

                            // report uncorrected error and continue
                            messages.addElement(msg);
                            break;
                        }
                    }
                }
            }
            catch (CDFException e) {

                e.printStackTrace(System.err);
            }

        }

        return new VariableVerificationResult(messages, 
                                              VerificationResult.COMPLETE);
    }



     /**
     * This will test to recognize DEPEND_x attributes that should 
     * not be present and will point out if such attributes are 
     * pointing to non-existent variables.
     *
     * @param v The variable to be checked.
     * @param ignore special case variables where no depends will be 
     *     check further.
     * @return A Vector of all the error messages for this variable.
     *
     */
    private static Vector<String> extraDependCheck(
        gsfc.nssdc.cdf.Variable v, boolean ignore) {
	
        Vector<String> failures = new Vector<>();
        CDF cdf = v.getMyCDF();       	             

        if (ignore || !v.getRecVariance()) {

            try {

                String dependVarName;
                Object depend0 = v.getEntryData("DEPEND_0");

                if (depend0 instanceof String) {

                    dependVarName = (String)depend0;
                }
                else {

                    failures.addElement(
                        "DEPEND_0 is not of type CHAR and should be.");

                    dependVarName = depend0.toString();

                    if (v.getMyCDF().confirmReadOnlyMode() ==
                        CDF.READONLYoff) {

                        v.putEntry("DEPEND_0", CDF_CHAR, dependVarName);
                        failures.addElement(
                            "DEPEND_0 was converted to CHAR.");
                    }
                }
                
                try {

                    cdf.getVariable(dependVarName);
                }
                catch (CDFException e) {

                    failures.addElement(
                        "DEPEND_0 (" + dependVarName + 
                        ") is not pointing to an existing variable.");
                }
            }
            catch (CDFException e) {

                // no DEPEND_0 attribute like we expect
            }
        }

        for (int i = (int)v.getNumDims(); i < 9; i++) {

            try {

                String dependVarName = (String)
                    v.getEntryData("DEPEND_" + (i + 1));

                failures.addElement("DEPEND_" + (i + 1) + 
                    " should not be present.");

                try {

                    cdf.getVariable(dependVarName);
                }
                catch (CDFException e) {

                    failures.addElement("DEPEND_" + (i + 1) + " (" + 
                        dependVarName + 
                        ") is not pointing to an existing variable."); 
                }
            }
            catch (CDFException e) {

                // no DEPEND_i attribute like we expect
            }
        }

/*
        int checkLevel = ignore ||!v.getRecVariance()? -1 : (int)v.getNumDims();
           		    
	switch (checkLevel) {
                
            case -1:
                               
                try {

                    String dependVarName = (String)v.getEntryData("DEPEND_0");

                    try{
                       
                        cdf.getVariable(dependVarName);
                    } catch(CDFException cdfexc0){
                      
                        failures.addElement("DEPEND_0 is pointing to a non-variable");
                   }
               }
                catch(CDFException cdfexc1){
                    // success
                }
           
            case 0:
                    
                try {
 
		   String dependVarName = (String)v.getEntryData("DEPEND_1");
                    failures.addElement("DEPEND_1 should not be present");
                   try{
                    
                        cdf.getVariable(dependVarName);
                    } catch(CDFException cdfexc0){
                      
                        failures.removeElement(failures.lastElement());
                       failures.addElement("DEPEND_1 should not be present and is pointing to a non-variable");                 
                    }                
                } catch(CDFException cdfexc1){
                    // success
                }
                
             case 1:       
                try {
		    
                    String dependVarName = (String)v.getEntryData("DEPEND_2");
                    failures.addElement("DEPEND_2 should not be present");
                
                    try{
                    
                        cdf.getVariable(dependVarName);
                    } catch(CDFException cdfexc0){
                      
                        failures.removeElement(failures.lastElement());
                        failures.addElement("DEPEND_2 should not be present and is pointing to a non-variable");  
                    }
                }
                catch(CDFException cdfexc1){
                    // success
                }
            case 2:   
                
                try {
		                        
                    String dependVarName = (String)v.getEntryData("DEPEND_3");
                    failures.addElement("DEPEND_3 should not be present");
                    try{
                    
                        cdf.getVariable(dependVarName);
                    } catch(CDFException cdfexc0){
                     
                        failures.removeElement(failures.lastElement());                     
                        failures.addElement("DEPEND_3 should not be present and is pointing to a non-variable");  
                    }
                }                              
                catch(CDFException cdfexc1){
                    // success
                }
        }           
*/
        return failures;
    }
            
                 
                
   
    /**
     * This will test to make sure that all the DEPEND_x attributes
     * for each variable point to a variable that is N.R.V. and is 1D with 
     * the appropriate size. The following assumptions are tested.<BR>
     *
     * @param v The variable to be checked.
     * @return A Vector of all the error messages for this varibale.
     *
     * <OL TYPE="I" START="I">
     * <LI> DEPEND_0
     * <OL TYPE="1" START="1">
     *   <LI>Needed only for record varying variables
     *   <LI>Must be CDF_EPOCH data type
     *   <LI>Must be record varying
     *   <LI>Must be 0 dimensional
     * </OL>
     * <LI> DEPEND_1, _2 and _3
     * <OL TYPE="1" START="1">
     *   <LI>Dimensional variables must the appropriate DEPEND attributes.  
     *       A 1D variable must have DEPEND_1.
     *       A 2D var must have DEPEND_1 and _2, etc.
     *   <LI>Must be one dimensional
     *   <LI>Must match the appropriate size of the variable
     * </OL>
     * <LI>The following variables do not need any DEPEND
     *     variables and are therefore ignored
     * <OL TYPE="1" START="1">
     *   <LI>Any metadata variable
     *   <LI>A variable that is CDF_EPOCH, record varying and 0 dimensional.
     *       These variables always describe time and therefore do not need
     *       any DEPEND attributes.
     *   <LI>A NRV support_data variable.
     * </OL>
     * </OL>
     */
    private static Vector<String> dependCheck(gsfc.nssdc.cdf.Variable v) {
	Vector<String> failures = new Vector<>();
	gsfc.nssdc.cdf.Variable dependVar;
	long nDims;
	long [] dimSizes;
	String vartype, dependVarName;
	//boolean recVary;
	CDF cdf = v.getMyCDF();

	try {
            vartype = getStringAttribute(v, "VAR_TYPE")[0];
	    nDims = v.getNumDims();
	    
	    if (nDims > 0) {

                dimSizes = v.getDimSizes();
            }
	    else {

                dimSizes = new long [] {0};
            }

/*
Variable istpVar = new Variable(v);
                                       // ISTP representation of var
if (!istpVar.isTensor()) {
*/
            for (int dimension = (int)nDims; dimension > 0; dimension--) {

                failures.addAll(checkDepend(v, dimension));
            }
/*
}
*/

            // now check DEPEND_0
            try {
                dependVarName = getStringAttribute(v, "DEPEND_0")[0];
                try {
                    dependVar = cdf.getVariable(dependVarName);
                    if (dependVar.getNumDims() != 0) {

                        failures.addElement(
                                    "DEPEND_0 is not 0 dimensional.");
                    }
                    long dependVarDataType = dependVar.getDataType();
                    if (dependVarDataType != CDF_TIME_TT2000 &&
                        dependVarDataType != CDF_EPOCH &&
                        dependVarDataType != CDF_EPOCH16) {

                        failures.addElement(
                            "DEPEND_0 (" + dependVarName + ") is " +
                            "not a time type (CDF_EPOCH, " +
                            "CDF_EPOCH16, or CDF_TIME_TT2000).  " +
                            "It is " +
                            gsfc.nssdc.cdf.util.CDFUtils.
                                getStringDataType(dependVarDataType) + 
                            ".");
                    }
                }
                catch (CDFException cdfexc0) {
                    failures.addElement("DEPEND_0 variable is missing");
                }
            }
            catch (CDFException cdfexc) {
                failures.addElement("DEPEND_0 attribute is missing.");
            }
	}
        catch (CDFException cdfexc) {
            failures.addElement("dependency check failed: "+
				cdfexc.getMessage());
	}
	return failures;
    }
    

    /**
     * Check the dependent variable of the i'th dimension of the given
     * variable.
     *
     * @param var variable to check dependent variable of.
     * @param i dimension to check.
     * @return List of error messages.
     */
    private static List<String> checkDepend(
        gsfc.nssdc.cdf.Variable var,
        int i) {

        ArrayList<String> errors = new ArrayList<String>();
                                       // error messages
        long[] dimSizes = var.getDimSizes();
                                       // var's dimension sizes
        CDF cdf = var.getMyCDF();      // cdf we are checking
        String dependName = "DEPEND_" + i;
                                       // name of attribute containing
                                       // the name of the i'th dependent
                                       // variable
        try {

            String dependVarName = (String)var.getEntryData(dependName);
                                       // dependent variable's name
            try {

                gsfc.nssdc.cdf.Variable dependVar = 
                    cdf.getVariable(dependVarName);

                if (dependVar.getNumDims() == 0) {

                    errors.add(dependName + " has dimension 0.  " +
                        "It should be 1.");
                    return errors;
                }
                if (dependVar.getNumDims() != 1) {

                    errors.add(dependName + " has dimension " +
                        dependVar.getNumDims() + ".  It should be 1.");
                }

                if(dependVar.getDimSizes() == null) {

                    errors.add(dependName + 
                        " dimension sizes are not set.");
                }
                else if (dependVar.getDimSizes()[0] != dimSizes[i - 1]) {

                    errors.add(dependName + " is wrong size (" +
                        dependVar.getDimSizes()[0] + ").  It should be " +
                        dimSizes[i - 1] + ".");

                }

                String[] additionalCheckErrors =
                    dependAdditionalCheck(var,dependVar);

                if (additionalCheckErrors != null) {

                    for (String error : additionalCheckErrors) {

                        errors.add(error);
                    }
                }                        
                if (DataType.isCharacter(dependVar.getDataType())) {

                    errors.add(dependName + " is a character type.");
                }
            }
            catch (CDFException cdfexc0) {

                errors.add(dependName + " variable (" + dependVarName +
                    ") is missing.");
            }
        }
        catch (CDFException cdfexc) {

            try {

                String varType = getStringAttribute(var, "VAR_TYPE")[0];
                                       // var's VAR_TYPE value
                if (varType.toLowerCase().equals("data")) {

                    String displayType = "";
                                       // var's DISPLAY_TYPE value
                    String[] displayTypeArray = 
                        getStringAttribute(var, "DISPLAY_TYPE");
                    if (displayTypeArray != null && 
                        displayTypeArray.length > 0) {

                        displayType = displayTypeArray[0];
                    }
                    // only allow missing DEPEND_1 and DEPEND_2 if ndims < 3
                    // and all dimSizes < 4
                    if (i > 2 || 
                        (i > dimSizes.length || dimSizes[i - 1] > 3 ||
                         !displayType.equalsIgnoreCase("time_series"))) {

                        // not a simple, 1/2D (coordinate) vector timeseries

                        errors.add(dependName + " attribute is missing.");
                    }
                }
            }
            catch (CDFException e) {

                System.err.println("ISTPCompliance.checkDepend: " +
                    "exception getting VAR_TYPE: " + e.getMessage());
            }
        }

        return errors;
    }


    private static String[] dependAdditionalCheck(
        gsfc.nssdc.cdf.Variable var,
        gsfc.nssdc.cdf.Variable dependVar){
        
        Vector<String> failures = new Vector<>();       
            
        if(dependVar.getRecVariance()){

            try{
                    
                String vardep0 = ((String)var.getEntryData("DEPEND_0"));
                    
                try{
                    
                    String dep0 = ((String)dependVar.getEntryData("DEPEND_0"));
                        
                    if(dep0.equalsIgnoreCase( vardep0)!= true ) 
                            
                       failures.addElement(dependVar.getName() + " DEPEND_0 attribute does not match "+ var.getName() + " DEPEND_0 attribute"); 
                        
                }catch(CDFException cdfexc0){
                        
                    failures.addElement(dependVar.getName() + " DEPEND_0 attribute is missing."); 
                }
// do we want the following?
                try {

                    long numDepend0Records = 
                        var.getMyCDF().getVariable(vardep0).
                            getNumWrittenRecords();

                    if (numDepend0Records > 1 &&
                        dependVar.getNumWrittenRecords() == 1 &&
                        dependVar.getSparseRecords() != 
                            CDFConstants.PREV_SPARSERECORDS) {

                        failures.addElement(dependVar.getName() +
                            " is record varying with a single record " +
                            "which is not designated previous-sparse.");
                    }
                }
                catch (CDFException e) {

                    System.err.println("dependAdditionalCheck: " +
                        "getNumWrittenRecords() or getSparseRecords() " +
                        "failed for " + dependVar.getName() + 
                        ": " + e.getMessage());
                }
// end do we want the following?
            }
            catch(CDFException cdfexc1){
                    
                // The variable does not have a valid depend 0.  That 
                // will be picked up and reported later in the original 
                // depend check.
            }
        }
// do we want the following?
        else {   // NRV depend

            try {

                if (dependVar.getNumWrittenRecords() != 1) {

                    failures.addElement(dependVar.getName() +
                        " (a non-record varying variable) has no " +
                        "value (record).");
                }
            }
            catch(CDFException e) {

                System.err.println("dependAdditionalCheck: " +
                    "getNumWrittenRecords() failed for " + 
                    dependVar.getName() + ": " + e.getMessage());
            }
        }
// end do we want the following?
      
        /*    if(((String)dependVar.getEntryData("VAR_TYPE")).equalsIgnoreCase("data")  &&
                dependVar.getNumDims() == 1 &&
                (((String)dependVar.getEntryData("DEPEND_1")).
                equalsIgnoreCase(((String)var.getEntryData("DEPEND_1")))!= true))
                
                failures.addElement(var.getName() + " DEPEND_1 attribute  does not match "+ dependVar.getName() + " DEPEND_1 attribute"); 
        
                    
        try { 
          
            String vardep1 = ((String)var.getEntryData("DEPEND_1"));
          
            try {
              
                String depType = ((String)dependVar.getEntryData("VAR_TYPE"));
      
                if(depType.equalsIgnoreCase("data") && dependVar.getNumDims() == 1){
              
                    try {
                  
                        String dep1 = ((String)dependVar.getEntryData("DEPEND_1"));
                  
                     //   if(dep1.equalsIgnoreCase(vardep1) != true)
                      
                        //    failures.addElement(var.getName() + " DEPEND_1 attribute  does not match "+ dependVar.getName() + " DEPEND_1 attribute");
                                    
                    }catch (CDFException cdfexc2){
                  
                        failures.addElement(dependVar.getName() + " DEPEND_1 attribute is missing.");
                    }
                }
            }catch(CDFException cdfexc3){
             
                failures.addElement(dependVar.getName() + " var type is not defined ");
                 
            }
                                     
        }catch(CDFException cdfexc4){
             
             //the variable does not have a Depend_1 that we could compare the depend variable
             // Depend_1 to.  That will be handled in the original depend check.
                    
                    //failures.addElement(dependVar.getName() + " var type is not defined ");
        }  */
       
        return failures.isEmpty() ? null : (String[])failures.toArray(new String[failures.size()]);
    
    }

    /**
     * This test will test all <B>data</B> variables to ensure that the
     * labeling is setup properly. The following describes the test.
     *
     * @param v The variable to be checked.
     * @return A Vector of all the error messages for this varibale.
     *
     * <OL TYPE="1" START="0">
     * <LI> Only variables with a VAR_TYPE = <B>data</B> and a 
     *      DISPLAY_TYPE of image,  _series or spectrogram are
     *      checked.  More DISPLAY_TYPES will be added as the rules for
     *      these becomes clear.
     * <LI> Variables with DISPLAY_TYPE image should never use a
     *      LABL_PTR attribute
     * <LI> Time series variables should only use a LABL_PTR attribute if
     *      it is not 0 dimensional
     * <LI> Spectrogram should only use LABL_PTR attributes if it is not 
     *      0 dimensional
     * <LI> A LABL_PTR should point to a 1D, NRV variable with a size 
     *      equal to the corresponding dimensional size of the variable.
     * <LI> A LABL_PTR must point to a metadata variable.
     * </OL>
     */
    public static Vector<String> labelCheck(gsfc.nssdc.cdf.Variable v) {

        Variable istpVar = new Variable(v);
                                       // istp representation of the 
                                       // variable
	Vector<String> failures = new Vector<>();
	Vector<String> fail1, fail2, fail3;
	CDF cdf = v.getMyCDF();
	gsfc.nssdc.cdf.Variable lv;
	long nDims, dataType;
	long [] dimSizes;
	String vartype, label;
	boolean recVary, ignore, ignore2, 
	    noPTR            = false, 
            noPTRStackPlot   = false,
	    noPTRTimeSeries  = false, 
	    noPTRSpectrogram = false, 
	    noPTRImage       = false;

	try {
//vartype = istpVar.getType();
            vartype = getStringAttribute(v, "VAR_TYPE")[0].toLowerCase();
	    nDims = v.getNumDims();
	    
	    if (vartype.equals("support_data")) {
		// Check for labelaxis???
		try {
		    v.getEntryData("LABLAXIS");
		}
                catch (CDFException e0) {

                    for (int dimension = (int)nDims; 
                         dimension > 0; dimension--) {

                        failures.addAll(
                            checkPTRAttribute("LABL_PTR_" + dimension,
                                cdf, v));
                    }
		}
	    } 
            else if (vartype.equals("data")) {

                String displayTypeStr; // complete DISPLAY_TYPE
                DisplayType displayType = null;  
                                       // object representation of 
                                       //  DISPLAY_TYPE

		try {

//displayType = istpVar.getDisplayType();

		    displayTypeStr = ((String)v.getEntryData("DISPLAY_TYPE")).toLowerCase();
                    displayType = new DisplayType(displayTypeStr);
		} 
                catch (CDFException e) {

                    // Ignore this.  A missing DISPLAY_TYPE will be 
                    // reported by checkDisplayType().

                    return failures;
                }
                catch (IllegalArgumentException e) {

                    // Ignore this.  A missing DISPLAY_TYPE will be 
                    // reported by checkDisplayType().

                    return failures;
                }
 		// Check for labelaxis???

                noPTR = !istpVar.lablPtrIsAllowed();

 		if (noPTR) {
                    try {
 			v.getEntryData("LABLAXIS");
                    } 
                    catch (CDFException e0) {

                        failures.addElement(
                                "LABLAXIS is missing for variable with " +
                                "DISPLAY_TYPE '" + displayType.getType() + 
                                "' and dimension " + nDims + ".");

                        try {

                            v.getEntryData("LABL_PTR_1");
                            failures.addElement(
                                    "LABL_PTR_1 is present but unnecessary " +
                                    "and will be ignored.");
                        } 
                        catch (CDFException e1) {

                            // there shouldn't be a LABL_PTR_1 attribute
                            //  so this isn't an error
                        }
                    }
                } 
                else {
                    
			switch ((int)nDims) {
			case 3:
			    fail3 = checkPTRAttribute("LABL_PTR_3", cdf, v);
			    for (int i=0;i<fail3.size();i++)
				failures.addElement(fail3.elementAt(i));
			case 2:
			    fail2 = checkPTRAttribute("LABL_PTR_2", cdf, v);
			    for (int i=0;i<fail2.size();i++)
				failures.addElement(fail2.elementAt(i));
			case 1:
			    fail1 = checkPTRAttribute("LABL_PTR_1", cdf, v);
			    for (int i=0;i<fail1.size();i++)
				failures.addElement(fail1.elementAt(i));
			    break;
			default:
			}
		}
	    }
	} catch (CDFException e4) {
	    failures.addElement("VAR_TYPE is missing.  (" + e4.getMessage() + ")");
	}
	return failures;
    }

    /**
     * Used to check for _PTR attributes.
     *
     * @param attr name of attribute to check.
     * @param cdf CDF to check.
     * @param v Variable whose attribute is to be checked.
     * @return Vector containing any error messages generated by check.
     */
     private static Vector<String> checkPTRAttribute(
        String attr, 
        CDF cdf, 
        gsfc.nssdc.cdf.Variable v) {

	Vector<String> failures = new Vector<>();

        String displayType = null;
        try {

            displayType = (String)v.getEntryData("DISPLAY_TYPE");
        }
        catch (Exception e) {

            // just continue with null value if DISPLAY_TYPE is missing 
            // or bad type
        }

	gsfc.nssdc.cdf.Variable av;
	long nDims, dataType;
	long [] dimSizes;
	boolean recVary, ignore, ignore2, 
	    noPTR            = false, 
	    noPTRTimeSeries  = false, 
	    noPTRSpectrogram = false, 
	    noPTRImage       = false;

	int PTRnum = 
	    Character.getNumericValue(attr.charAt(attr.length()-1)) - 1;
	if (PTRnum < 0) PTRnum = 0;
	String attrData;
	nDims = v.getNumDims();
	if (nDims > 0) dimSizes = v.getDimSizes();
	else dimSizes = new long [] {0};
//	recVary = v.getRecVariance();
//	dataType = v.getDataType();

	
	try { //labl_ptr_1 is missing
	    attrData = (String)v.getEntryData(attr);
	    try { //labl_ptr_1 var is missing
		av = cdf.getVariable(attrData);

                if (av.getID() == v.getID()) {

                    failures.addElement(attr + 
                        " should not point to variable '" + v.getName() + 
                        "' itself");
                }

                long labelNumDims = av.getNumDims();

		if (labelNumDims != 1){

		    failures.addElement(attr + " is not 1 dimensional");
                }
                else if (av.getDimSizes()[0] != dimSizes[PTRnum]){
                  
                        failures.addElement(attr + " variable (" + 
                            av.getName() + ") is the wrong size (" + 
                            av.getDimSizes()[0] + ").  It should be " + 
                            dimSizes[PTRnum] + ".");
                }

                long labelDataType = av.getDataType();

                if (labelDataType != CDF.CDF_CHAR) {

                    failures.addElement(attr + " is of type " + 
                        CDFUtils.getStringDataType(labelDataType) +
                        " and should be " + 
                        CDFUtils.getStringDataType(CDF.CDF_CHAR) + ".");
                }
                
		if (av.getRecVariance()) {

		    failures.addElement(attr +
                        " can not be record varying.");
                }
                if (labelNumDims == 1 && 
                    labelDataType == CDF.CDF_CHAR) {

                    String[] labelValues = (String[])av.getRecord(0);

                    for (int i = 0; i < labelValues.length; i++) {

                        if (!isPrintable(labelValues[i])) {

                            failures.addElement(attr + "'s " + i + 
                                "th value is not printable ASCII " +
                                "characters.  That may not be " +
                                "supported by some software.");
                        }
                    }
                }
		if (!((String)av.getEntryData("VAR_TYPE")).equalsIgnoreCase("metadata"))
		    failures.addElement(attr+" does not point to a metadata variable.");
	    } catch (CDFException e1) {

                if (displayType == null ||
                    !displayType.equalsIgnoreCase(DisplayType.NO_PLOT_TYPE)) {

                    failures.addElement(attr + " variable (" + attrData + 
                        ") is missing.");
                }
	    }
	} catch (CDFException e2) {

            if (displayType == null ||
                !displayType.equalsIgnoreCase(DisplayType.NO_PLOT_TYPE)) {

                failures.addElement(attr+" attribute is missing.  (" + 
                    e2.getMessage() + ")");
            }
	}

	return failures;
    }

    
    /**
     * Checks the validity of the FORMAT or FORM_PTR attribute of the 
     * given variable.
     *
     * @param var variable whose FORMAT or FORM_PTR attribute is to be 
     *            validated.
     * @return vector of error messages.
     */
    public static Vector<String> checkFormat(gsfc.nssdc.cdf.Variable var) {

        Vector<String> errors = new Vector<>();  
                                       // vector to accumulate error
                                       //  messages in
        Variable istpVar = new Variable(var);
                                       // ISTP version of var
        String[] formats = null;
                                       // variable's output format
                                       // values
        try {

            formats = istpVar.getFormats();
        }
        catch (ISTPComplianceException e) {

            errors.addElement(e.getMessage());
            return errors;
        }

        if (formats == null) {

            errors.addElement(
                "FORMAT and FORM_PTR attribute value is missing.");

            return errors;
        }
        String formPtr = istpVar.getFormPtr();
                                       // FORM_PTR attribute value
        Number[] validMin = istpVar.getValidMinNumber();
                                       // valid minimum values
        Number[] validMax = istpVar.getValidMaxNumber();
                                       // valid minimum values

/*
        if (validMin != null && 
            formats.length != validMin.length) {

            errors.addElement(
                "FORMAT size (" + formats.length + 
                ") does not equal VALIDMIN size (" + 
                validMin.length + ").");
        }
        if (validMax != null && 
            formats.length != validMax.length) {

            errors.addElement(
                "FORMAT size (" + formats.length + 
                ") does not equal VALIDMAX size (" + 
                validMax.length + ").");
        }
*/

        for (int i = 0; i < formats.length; i++) {

            Vector<String> tempErrors = new Vector<>();  
                                       // temporary collection of error
                                       //  messages that ultimately get
                                       //  copied to errors

            if (formats[i].length() == 0) {

                tempErrors.addElement("value is missing.");
            }
            else if (formats[i].charAt(0) == '%') {

                tempErrors = checkCConversionSpecification(
                                 istpVar.getDataType(), formats[i]);
            }
            else {

                tempErrors = 
                    checkFortranEditDescriptor(
                        istpVar.getDataType(), 
                        validMin != null && i < validMin.length ? 
                            validMin[i] : null,
                        validMax != null && i < validMax.length ? 
                            validMax[i] : null,
                        formats[i]);
            }

            for (int j = 0; j < tempErrors.size(); j++) {

                if (formPtr == null) {

                    errors.addElement(tempErrors.elementAt(j));
                }
                else {

                    errors.addElement(formPtr + " (FORM_PTR) value [" +
                        i + "]: " + tempErrors.elementAt(j));
                }
            }
        }

        return errors;
    }


    /**
     * Adds an appropriate FORMAT attribute value to the given variable.
     *
     * @param var variable whose FORMAT or FORM_PTR attribute is to be 
     *            added.
     * @return message describing success or failure
     */
    public static String addFormat(gsfc.nssdc.cdf.Variable var) {

        CDF cdf = var.getMyCDF();

        String varType;

        try {

            varType = (String)var.getEntryData("VAR_TYPE");
        }
        catch (CDFException e) {

            return "Couldn't add FORMAT attribute to " + var.getName() +
                   " because VAR_TYPE couldn't be determined. (" +
                   e.getMessage() + ")";
        }

        Attribute format;

        if (varType.equalsIgnoreCase("metadata")) {

            try {

                format = cdf.getAttribute("FORMAT");
            }
            catch (CDFException e) {

                try {

                    format = Attribute.create(cdf, "FORMAT", 
                                 CDF.VARIABLE_SCOPE);
                }
                catch (CDFException e1) {

                    return "Couldn't add FORMAT attribute to " + 
                           var.getName() + " because " +
                           e1.getMessage();
                }
            }
        }
       else {

            /*return "Don't know how to add FORMAT attribute to " +
                   var.getName() + " of type " + varType;*/
            return null;
        }

        String formatValue = "a";

        try {

            formatValue += (var.getNumElements() + 1);

            var.putEntry(format, CDF.CDF_CHAR, formatValue);
        }
        catch (CDFException e) {

            return "Couldn't add FORMAT value of " + formatValue + " to " + 
                   var.getName() + " because " + e.getMessage();
        }

        return "Added FORMAT attribute with value '" + formatValue +
               "' to " + var.getName();
    }


    /**
     * Checks the validity of the Fortran edit descriptor contained in 
     * the given format string with respect to the given variable 
     * datatype, validMin, and validMax values.
     *
     * @param varType variable's data type.
     * @param validMin variable's valid minimum value.
     * @param validMax variable's valid maximum value.
     * @param format contains the Fortran edit descriptor to be check
     * @return vector of error messages
     */
    public static Vector<String> checkFortranEditDescriptor(
        long varType, Number validMin, Number validMax, String format) {

        Vector<String> errors = new Vector<>();  
                                       // accumulated set of error 
                                       // messages

        FtnEditDescriptor editDescriptor = null;
                                       // the edit descriptor 
                                       // constructed from the given 
                                       // format string
        RepeatableFtnEditDescriptor repeatableEditDescriptor = null;
                                       // the repeatable edit descriptor
                                       // constructed from the given 
                                       // format string
        try {

            for (int i = 0; i < format.length(); ) {

                editDescriptor = 
                    FtnEditDescriptor.parseFtnEditDescriptor(
                        format.substring(i));

                i += editDescriptor.getLength();

                if (editDescriptor instanceof 
                    RepeatableFtnEditDescriptor) {

                    repeatableEditDescriptor = 
                        (RepeatableFtnEditDescriptor)editDescriptor;
                    break;
                }
            }
        }
        catch (IllegalArgumentException e) {

            errors.addElement("FORMAT syntax error: " + e.getMessage());
   
            return errors;
        }

        if (repeatableEditDescriptor == null) {

            errors.addElement("'" + format + 
                              "' is not an acceptable edit descriptor");
        }
        else {

            if (!isValidDescriptorForType(
                     repeatableEditDescriptor, varType)) {

                errors.addElement("FORMAT '" + format + 
                    "' is invalid for a " +
                    CDFUtils.getStringDataType(varType) + 
                    " type variable");
            }
            else {

                String widthWarning =
                    repeatableEditDescriptor.widthWarning(
                        validMin, validMax);
                                       // descriptor width warning

                if (widthWarning != null) {

                    errors.addElement(widthWarning);
                }
            }
        }

        return errors;
    }


    /**
     * Checks the validity of the C language printf conversion 
     * specification contained in the given format string with respect 
     * to the given variable datatype.
     *
     * @param varType variable data type
     * @param format contains the conversion specification to be check
     * @return vector of error messages
     */
    public static Vector<String> checkCConversionSpecification(
        long varType,
        String format) {

        Vector<String> errors = new Vector<>();  
                                       // accumulated set of error 
                                       // messages
        CConversionSpecification convSpec = null;
                                       // the conversion specification 
                                       // constructed from the given 
                                       // format string
        try {

            convSpec = new CConversionSpecification(format);
        }
        catch (IllegalArgumentException e) {

            errors.addElement("FORMAT syntax error: " + e.getMessage());
   
            return errors;
        }

        if (!isValidDescriptorForType(convSpec, varType)) {

            errors.addElement("FORMAT '" + format + 
                "' is invalid for a " +
                CDFUtils.getStringDataType(varType) + " type variable");
        }

        return errors;
    }


    /**
     * Provides an indication of whether the given edit descriptor is valid
     * for the given datatype.
     *
     * @param ed Fortran edit descriptor
     * @param varType CDF datatype
     * @return true if valid
     */
    public static boolean isValidDescriptorForType(
                                             RepeatableFtnEditDescriptor ed, 
                                             long varType) {

        if (varType == CDF.CDF_INT1 || varType == CDF.CDF_UINT1 ||
            varType == CDF.CDF_INT2 || varType == CDF.CDF_UINT2 ||
            varType == CDF.CDF_INT4 || varType == CDF.CDF_UINT4 ||
            varType == CDF.CDF_INT8 || varType == CDF.CDF_BYTE) {

            if (ed.getType() != 'I' && ed.getType() != 'Z' &&
                ed.getType() != 'O' && ed.getType() != 'B') {

                return false;
            }
        }
        else if (varType == CDF.CDF_FLOAT || varType == CDF.CDF_REAL4 ||
                 varType == CDF.CDF_DOUBLE || varType == CDF.CDF_REAL8) {

            if (ed.getType() != 'F' && ed.getType() != 'E' &&
                ed.getType() != 'D' && ed.getType() != 'G') {

                return false;
            }
        }
        else if (varType == CDF.CDF_CHAR || varType == CDF.CDF_UCHAR) {

            if (ed.getType() != 'A') {

                return false;
            }
        
        }

        return true;
    }


    /**
     * Provides an indication of whether the given conversion specification
     * is valid for the given datatype.
     *
     * @param cs printf conversion specification
     * @param varType CDF datatype
     * @return true if valid
     */
    public static boolean isValidDescriptorForType(CConversionSpecification cs, 
                                                   long varType) {

        //
        // Maybe unsigned output representation (i.e., o, u, x, X) should
        //  be required for unsigned types (i.e., CDF_UINT?) but this probably
        //  isn't necessary right now.
        //
        if (varType == CDF.CDF_INT1 || varType == CDF.CDF_UINT1 ||
            varType == CDF.CDF_INT2 || varType == CDF.CDF_UINT2 ||
            varType == CDF.CDF_INT4 || varType == CDF.CDF_UINT4 ||
            varType == CDF.CDF_INT8 || varType == CDF.CDF_BYTE) {

            if (cs.getType() != 'd' && cs.getType() != 'i' &&
                cs.getType() != 'o' && cs.getType() != 'u' &&
                cs.getType() != 'x' && cs.getType() != 'X') {

                return false;
            }
        }
        else if (varType == CDF.CDF_FLOAT || varType == CDF.CDF_REAL4 ||
                 varType == CDF.CDF_DOUBLE || varType == CDF.CDF_REAL8) {

            if (cs.getType() != 'f' && 
                cs.getType() != 'e' && cs.getType() != 'E' &&
                cs.getType() != 'g' && cs.getType() != 'G') {

                return false;
            }
        }
        else if (varType == CDF.CDF_CHAR || varType == CDF.CDF_UCHAR) {

            if (cs.getType() != 'c' && cs.getType() != 's') {

                return false;
            }
        
        }

        return true;
    }


    /**
     * Checks the validity of the DISPLAY_TYPE attribute of the given
     * variable.
     * 
     * @param var the variable to check.
     * @return a vector containing a description of any errors found 
     *     (an empty vector if no errors)
     */
    private static Vector<String> checkDisplayType(
        gsfc.nssdc.cdf.Variable var) {

	Vector<String> errors = new Vector<>();  
                                       // contains descriptions of the 
                                       // errors found with the given 
                                       // variable
        String displayTypeStr = null;  // string representation of 
                                       //  DISPLAY_TYPE value
        try {
            
            String trimmedDisplayType = 
                ((String)var.getEntryData("DISPLAY_TYPE")).trim();

            displayTypeStr = trimmedDisplayType.toLowerCase();

            if (!trimmedDisplayType.equals(displayTypeStr)) {

                errors.addElement("DISPLAY_TYPE attribute value '" +
                     trimmedDisplayType + "' is not all lower case.");

                if (var.getMyCDF().confirmReadOnlyMode() ==
                    CDF.READONLYoff) {

                    try {

                        var.putEntry("DISPLAY_TYPE", CDF_CHAR, 
                                     displayTypeStr);
                        errors.addElement("DISPLAY_TYPE attribute " +
                            "value changed to '" + displayTypeStr + 
                            "'.");
                    }
                    catch (CDFException e) {

                        errors.addElement("Failed to change " +
                            "DISPLAY_TYPE attribute value: " + 
                            e.getMessage());
                    }
                }
            }
        }
        catch (CDFException e) {

            errors.addElement("Missing DISPLAY_TYPE attribute.  (" + 
                e.getMessage() + ")");

            try {

                if (var.getMyCDF().confirmReadOnlyMode() ==
                    CDF.READONLYoff) {

                    displayTypeStr = createDisplayTypeEntry(var);

                    if (displayTypeStr != null) {

                        errors.addElement(
                            "Created DISPLAY_TYPE attribute " +
                            "and set its initial value to '" +
                            displayTypeStr + "'");
                    }
                    else {

                        errors.addElement(
                            "Error creating DISPLAY_TYPE");
                    }
                }
            }
            catch (CDFException e1) {

                // how can confirmReadOnlyMode() fail?
                e1.printStackTrace();
            }

            return errors;
        }

        DisplayType displayType = null;  // object representation of 
                                         //  DISPLAY_TYPE
        try {

            displayType = new DisplayType(displayTypeStr);
        }
        catch (IllegalArgumentException e) {

            errors.addElement("DISPLAY_TYPE error: " + e.getMessage());
            return errors;
        }

        List parameters = displayType.getParameters();
                                          // the parameter values of 
                                          //  DISPLAY_TYPE

        for (int i = 0; i < parameters.size(); i++) {

            AbstractDisplayTypeParameter param = 
                    (AbstractDisplayTypeParameter)parameters.get(i);
                                          // a single parameter value

            if (param instanceof AxisDefinition) {

                Vector<String> tempErrors = 
                    checkAxisDefinition(var.getMyCDF(),
                                        (AxisDefinition)param);

                for (int j = 0; j < tempErrors.size(); j++) {

                    errors.addElement(tempErrors.elementAt(j));
                }
            }
            else if (param instanceof CoordinateSystem) {

                if (!displayType.getType().equals("orbit")) {

                    errors.addElement(
                            "'coord' is not valid with a DISPLAY_TYPE of '" + 
                            displayType.getType() + "'");
                }
            }
            else if (param instanceof ParameterValue) {

/*
                ParameterValue paramValue = (ParameterValue)param;

                if (!displayType.getType().equals("topside_ionogram") &&
                    !displayType.getType().equals("image")) {

                    errors.addElement("'" + paramValue.getValue() +
                            "' is not valid with a DISPLAY_TYPE of '" + 
                            displayType.getType() + "'");
                }
*/
            }
            else {

                errors.addElement("internal software error in ISTPCompliance");
                errors.addElement("unknown parameter type '" +
                                    param.getClass().getName() + "'");
                errors.addElement("report this to software maintenance");
            }
        }
      
	return errors;
    }


    /**
     * Checks the validity of the given DISPLAY_TYPE axis definition.
     * 
     * @param cdf the CDF containing the axis definition
     * @param axisDef the axis definition to be checked
     * @return vector containing error messages of any errors found in the 
     *         given definition (vector is empty if definition is completely 
     *         valid)
     */
    protected static Vector<String> checkAxisDefinition(
        CDF cdf, 
        AxisDefinition axisDef) {

        Vector<String> errors = new Vector<>();

        gsfc.nssdc.cdf.Variable axisVar = null;

        try {

            axisVar = getVariableIgnoreCase(cdf, axisDef.getVariable());
            if (axisVar == null) {

                throw new CDFException(axisDef.getVariable() + 
                                       " variable not found");
            }
//            axisVar = cdf.getVariable(axisDef.getVariable());

            long numDims = axisVar.getNumDims();

            if (axisDef.getNumIndices() > numDims) {

                errors.addElement("DISPLAY_TYPE argument variable '" +
                                  axisDef.getVariable() + 
                                  "' index error: dimension of " +
                                  axisDef.getNumIndices() + 
                                  " is > variable's dimension of " +
                                  numDims);
                return errors;
            }

            long[] dimSizes = axisVar.getDimSizes();

            for (int j = 0; j < axisDef.getNumIndices(); j++) {

                if (axisDef.getIndex(j) > dimSizes[j]) {

                    errors.addElement("DISPLAY_TYPE argument variable '" +
                                      axisDef.getVariable() + 
                                      "' index error: index " + j +
                                      "'s value of " +
                                      axisDef.getIndex(j) + 
                            " is > allowed by variable's dimension size of " +
                                      dimSizes[j]);
                }
            }
        }
        catch (CDFException e) {

            errors.addElement("DISPLAY_TYPE argument variable '" + 
                              axisDef.getVariable() + "' not found");
        }

        return errors;
    }


    /**
     * Finds the specified variable in the given CDF ignoring the variable
     * name's case (CDF variable names are normally case sensitive).
     * 
     * @param cdf the CDF to look in
     * @param name the name of the variable to find (ignoring case)
     * @return the variable (null if not found)
     */
    protected static gsfc.nssdc.cdf.Variable getVariableIgnoreCase(
        CDF cdf, String name) {

        Vector allVars = cdf.getVariables();

        for (int i = 0; i < allVars.size(); i++) {

            gsfc.nssdc.cdf.Variable var = 
                (gsfc.nssdc.cdf.Variable)allVars.elementAt(i);

            if (var.getName().equalsIgnoreCase(name)) {

                return var;
            }
        }

        return null;
    }


    /**
     * If the given variable is a "virtual variable", then checks the 
     * validity of virtual variable unique attributes.  Does nothing if
     * the given variable is not virtual.
     * 
     * @param var the variable to check
     * @return a vector containing a description of any errors found (an empty
     * vector if no errors)
     */
    private static Vector<String> checkVirtual(
        gsfc.nssdc.cdf.Variable var) {

	Vector<String> errors = new Vector<>();   
                                        // contains descriptions of the 
                                        // errors found with the given 
                                        // variable
        String virtualAttribute = null; // VIRTUAL attribute value
        try {

            virtualAttribute = ((String)var.getEntryData("VIRTUAL")).trim();
        }
        catch (CDFException e) {

            return errors;
        }

        if (!virtualAttribute.equalsIgnoreCase("TRUE")) {

            return errors;
        }

        String function = null;         // FUNCTION attribute value
        try {

            function = ((String)var.getEntryData("FUNCT")).trim();
        }
        catch (CDFException e) {

            errors.addElement(
                        "FUNCTION attribute missing for virtual variable.  (" + e.getMessage() + ")");
            return errors;
        }

        if (function == null) {

            errors.addElement("null virtual variable FUNCTION");

            return errors;
        }

        String[] components = null;    // COMPONENT_i values

        int numComponents = VirtualVariable.getNumberOfComponents(function);
                                       // number of COMPONENT values

        if (numComponents >= 0) {

            components = new String[numComponents];
        }
        else {

            errors.addElement("unrecognized virtual variable FUNCTION '" + 
                              function + "'");
            return errors;
        }

        for (int i = 0; i < components.length; i++) {

            try {

                components[i] = 
                        ((String)var.getEntryData("COMPONENT_" + i)).trim();
            }
            catch (CDFException e) {

                errors.addElement("COMPONENT_" + i + 
                              " attribute missing for virtual variable.  (" + e.getMessage() + ")");
                return errors;
            }
        
            if (getVariableIgnoreCase(var.getMyCDF(), components[i]) == null) {

                errors.addElement("COMPONENT_" + i + " value of '" + 
                                  components[i] +
                                  "' is not a valid variable name");
            }
        }

        int reqDimension = VirtualVariable.getVariableDimension(function);

        if (reqDimension >= 0) {

            long varDimension = var.getNumDims();

            if (reqDimension != varDimension) {

                errors.addElement("dimension '" + varDimension +
                              "' of virtual variable does not match the '" + 
                              function + 
                              "' FUNCTION's required dimension of '" + 
                              reqDimension + "'");
            }
        }

        return errors;
    }

    
    /**
     * returns a default value for a specific data type.
     * 
     * @param type name of CDF datatype.
     * @return default value for the specified datatype.
     * @since version 0.3
     */
    private static Object getDefaultValue(String type) 
    {
            
        if (type.equals("CDF_BYTE")) 
            return DEFAULT_BYTE_PADVALUE;
            
        else if (type.equals("CDF_INT1")) 
            return DEFAULT_INT1_PADVALUE;
            
        else if (type.equals("CDF_UINT1")) 
            return DEFAULT_UINT1_PADVALUE;
            
        else if (type.equals("CDF_INT2"))
            return DEFAULT_INT2_PADVALUE;
            
        else if (type.equals("CDF_UINT2")) 
            return DEFAULT_UINT2_PADVALUE;
            
        else if (type.equals("CDF_INT4")) 
            return DEFAULT_INT4_PADVALUE;
            
        else if (type.equals("CDF_UINT4")) 
            return DEFAULT_UINT4_PADVALUE;
            
        else if (type.equals("CDF_INT8")) 
            return DEFAULT_INT8_PADVALUE;

        else if (type.equals("CDF_FLOAT"))
            return DEFAULT_FLOAT_PADVALUE;
            
        else if (type.equals("CDF_REAL4")) 
            return DEFAULT_REAL4_PADVALUE;
            
        else if (type.equals("CDF_DOUBLE")) 
            return DEFAULT_REAL8_PADVALUE;
            
        else if (type.equals("CDF_REAL8")) 
            return DEFAULT_DOUBLE_PADVALUE;
            
        else if (type.equals("CDF_CHAR")) 
            return DEFAULT_CHAR_PADVALUE;
            
        else if (type.equals("CDF_UCHAR")) 
            return DEFAULT_UCHAR_PADVALUE;
            
        else if (type.equals("CDF_TIME_TT2000")) 
            return DEFAULT_TT2000_PADVALUE;
                
        else if (type.equals("CDF_EPOCH")) 
            return DEFAULT_EPOCH_PADVALUE;
                
       else if (type.equals("CDF_EPOCH16")){
            
            double[] ep16PadData = new double[2]; 
            
            try{
                gsfc.nssdc.cdf.util.Epoch16.compute(0000, 1, 01, 00, 00,
                    00, 000, 000, 000, 000, ep16PadData); 

                return ep16PadData;
            }
            catch(CDFException e) {
                
                return null;	   
            }             
       }
            
        else 
            return null; 
    }
    
    private static Object getConvertedValue(
        String type,Object data,String attrName) { 

        try{
        if (data instanceof Number)
        {
            if (type.equals("CDF_BYTE"))
                return ((Number)data).byteValue();
            
            else if (type.equals("CDF_INT1"))
                return ((Number)data).byteValue();
            
            else if (type.equals("CDF_UINT1")) 
                return ((Number)data).shortValue();
            
            else if (type.equals("CDF_INT2"))
                return ((Number)data).shortValue();
            
            else if (type.equals("CDF_UINT2"))
                return ((Number)data).intValue();
            
            else if (type.equals("CDF_INT4"))
                return ((Number)data).intValue();
            
            else if (type.equals("CDF_UINT4")) 
                return ((Number)data).longValue();
            
            else if (type.equals("CDF_INT8"))
                return ((Number)data).longValue();
            
            else if (type.equals("CDF_FLOAT"))
                return ((Number)data).floatValue();
            
            else if (type.equals("CDF_REAL4"))
                return ((Number)data).floatValue();
            
            else if (type.equals("CDF_DOUBLE")){
                
                String s = ((Number)data).toString();
                return  Double.valueOf(s);   
            } 
              
            
            else if (type.equals("CDF_REAL8")){
                
                String s = ((Number)data).toString();
                return  Double.valueOf(s);    
          }
            
                
            else if (type.equals("CDF_CHAR"))
                return data.toString();
            
            else if (type.equals("CDF_UCHAR")) 
                return data.toString();
            
            else if (type.equals("CDF_TIME_TT2000")) {

                return ((Number)data).longValue();
            }
            else if (type.equals("CDF_EPOCH")){
               
                String s = ((Number)data).toString();
                return  Double.valueOf(s);                  
           }
                   
  
        }
        else if (data instanceof String)
        {
            if (type.equals("CDF_BYTE"))
                return Byte.parseByte(data.toString());
            
            else if (type.equals("CDF_INT1"))
                return Byte.parseByte(data.toString());
            
            else if (type.equals("CDF_UINT1")) 
                return Short.parseShort(data.toString());
            
            else if (type.equals("CDF_INT2"))
                return Short.parseShort(data.toString());
            
            else if (type.equals("CDF_UINT2"))
                return Integer.parseInt(data.toString());
            
            else if (type.equals("CDF_INT4"))
                return Integer.parseInt(data.toString());
            
            else if (type.equals("CDF_UINT4")) 
                return Long.parseLong(data.toString());
            
            else if (type.equals("CDF_INT8")) 
                return Long.parseLong(data.toString());
            
            else if (type.equals("CDF_FLOAT"))
                return Float.valueOf(data.toString());
          
            else if (type.equals("CDF_REAL4"))
                return Float.valueOf(data.toString());
            
            else if (type.equals("CDF_DOUBLE")) 
                return Double.valueOf(data.toString());
            
            else if (type.equals("CDF_REAL8"))
                return Double.valueOf(data.toString());
                
            else if (type.equals("CDF_CHAR"))
                return data.toString();
            
            else if (type.equals("CDF_UCHAR")) 
                return data.toString();
            
            else if (type.equals("CDF_TIME_TT2000")) {

                return TerrestrialTime2000.fromString(data.toString());
            }
            else if (type.equals("CDF_EPOCH"))
                return new Double(gsfc.nssdc.cdf.util.Epoch.parse(data.toString()));

            else if (type.equals("CDF_EPOCH16"))
                return gsfc.nssdc.cdf.util.Epoch16.parse(data.toString());
        }
        return getDefaultValue(type); 
        }
        catch (CDFException e){
            if (attrName.startsWith("FILLVAL"))
                return getFillvalue(type);
            else
                return getDefaultValue(type);
        }
        catch (NumberFormatException e){
            if (attrName.startsWith("FILLVAL"))
                return getFillvalue(type);
            else
                return getDefaultValue(type);
           
        }
    }


  
        private static Object getConvertedArrayValues(String type,Object data,String attrName) 
    {
        Object newdata = null;
        try{
            if (type.equals("CDF_BYTE")||type.equals("CDF_INT1"))
            {
                newdata = Array.newInstance(Byte.TYPE, Array.getLength(data));
                for (int i=0;i<Array.getLength(data);i++)
				   Array.setByte(newdata,i, Array.getByte(data,i));
                return newdata;
            }
            else if (type.equals("CDF_UINT1")||type.equals("CDF_INT2"))
            {
                newdata = Array.newInstance(Short.TYPE, Array.getLength(data));
                for (int i=0;i<Array.getLength(data);i++)
				   Array.setShort(newdata,i, Array.getShort(data,i));
                return newdata;
            }
            else if (type.equals("CDF_UINT2")||type.equals("CDF_INT4"))
            {
                  newdata = Array.newInstance(Integer.TYPE, Array.getLength(data));
                for (int i=0;i<Array.getLength(data);i++)
				   Array.setInt(newdata,i, Array.getInt(data,i));
                return newdata; 
            }
            else if (type.equals("CDF_UINT4")||type.equals("CDF_INT8"))
            {
                newdata = Array.newInstance(Long.TYPE, Array.getLength(data));
                for (int i=0;i<Array.getLength(data);i++)
				   Array.setLong(newdata,i, Array.getLong(data,i));
                return newdata; 
            }
            else if (type.equals("CDF_FLOAT")||type.equals("CDF_REAL4"))
            {
                newdata = Array.newInstance(Float.TYPE, Array.getLength(data));
                for (int i=0;i<Array.getLength(data);i++)
				   Array.setFloat(newdata,i, Array.getFloat(data,i));
                return newdata; 
            }
            else if (type.equals("CDF_TIME_TT2000")) {

                newdata = Array.newInstance(Long.TYPE, Array.getLength(data));
                for (int i=0;i<Array.getLength(data);i++) {

                    Array.setLong(newdata,i, Array.getLong(data,i));
                }
                return newdata; 
            }  
            else if (type.equals("CDF_DOUBLE")
                        ||type.equals("CDF_REAL8")||type.equals("CDF_EPOCH"))
            {
                newdata = Array.newInstance(Double.TYPE, Array.getLength(data));
                for (int i=0;i<Array.getLength(data);i++)
				   Array.setDouble(newdata,i, Array.getDouble(data,i));
                return newdata; 
            }  
             else if (type.equals("CDF_EPOCH16"))
            {
                
                newdata = Array.newInstance(double[].class, Array.getLength(data));
                for (int i=0;i<Array.getLength(data);i++)
				   Array.set(newdata,i, Array.get(data,i));
                return newdata; 
            }    
            else if (type.equals("CDF_CHAR")||type.equals("CDF_UCHAR"))
            {
                newdata = Array.newInstance(Character.TYPE, Array.getLength(data));
                for (int i=0;i<Array.getLength(data);i++)
				   Array.set(newdata,i, (Array.get(data,i)).toString());
                return newdata; 
            }
            return data; 
        }
        catch (Exception e)
        {
            if (getDefaultValue(type) instanceof Byte)
            {
                newdata = Array.newInstance(Byte.TYPE, Array.getLength(data));
                for (int i=0;i<Array.getLength(data);i++)
                    Array.setByte(newdata,i, ((Byte)getDefaultValue(type)).byteValue());
                return newdata;
            }
            if (getDefaultValue(type) instanceof Double)
            {
                newdata = Array.newInstance(Double.TYPE, Array.getLength(data));
                for (int i=0;i<Array.getLength(data);i++)
                    Array.setDouble(newdata,i, ((Double)getDefaultValue(type)).doubleValue());
                return newdata;
            }
            if (getDefaultValue(type) instanceof Float)
            {
                newdata = Array.newInstance(Float.TYPE, Array.getLength(data));
                for (int i=0;i<Array.getLength(data);i++)
                    Array.setFloat(newdata,i, ((Float)getDefaultValue(type)).floatValue());
                return newdata;
            }
            if (getDefaultValue(type) instanceof Integer)
            {
                newdata = Array.newInstance(Integer.TYPE, Array.getLength(data));
                for (int i=0;i<Array.getLength(data);i++)
                    Array.setInt(newdata,i, ((Integer)getDefaultValue(type)).intValue());
                return newdata;
            }
            if (getDefaultValue(type) instanceof Long)
            {
                newdata = Array.newInstance(Long.TYPE, Array.getLength(data));
                for (int i=0;i<Array.getLength(data);i++)
                    Array.setLong(newdata,i, ((Long)getDefaultValue(type)).longValue());
                return newdata;
            }
            if (getDefaultValue(type) instanceof Short)
            {
                newdata = Array.newInstance(Short.TYPE, Array.getLength(data));
                for (int i=0;i<Array.getLength(data);i++)
                    Array.setShort(newdata,i, ((Short)getDefaultValue(type)).shortValue());
                return newdata;
            }
            if (getDefaultValue(type) instanceof Character)
            {
                newdata = Array.newInstance(Character.TYPE, Array.getLength(data));
                for (int i=0;i<Array.getLength(data);i++)
                    Array.set(newdata,i, (String)(getDefaultValue(type)));
                return newdata;
            }
            if (getDefaultValue(type) instanceof double[])
            {
                newdata = Array.newInstance(double[].class, Array.getLength(data));
                for (int i=0;i<Array.getLength(data);i++)
                    Array.set(newdata,i, (double[])(getDefaultValue(type)));
                return newdata;
            }
            else
                return data;
        }
    }
    

   /**
    * Sets the FILLVAL attribute for the variable v
    * to the default value for the data type.
    * 
    * @param type name of CDF datatype.
    * @return default fill value for the specified datatype.
    * @since version 0.3
    */
    public static Object getFillvalue(String type) {

        return FillvalAttribute.getStandardValue(
                                    CDFUtils.getDataTypeValue(type));
    }


    public static boolean dataTypeEqual(long varDataType, long entryDataType)
    {
        if (varDataType == entryDataType)
            return true;
            
        else if ((varDataType==CDF.CDF_FLOAT && entryDataType == CDF.CDF_REAL4)||
                 (varDataType==CDF.CDF_REAL4 && entryDataType == CDF.CDF_FLOAT))
            return true;
            
        else if ((varDataType==CDF.CDF_DOUBLE && entryDataType == CDF.CDF_REAL8)||
                 (varDataType==CDF.CDF_REAL8 && entryDataType == CDF.CDF_DOUBLE))//||
                 //(varDataType==CDF.CDF_EPOCH && entryDataType == CDF.CDF_REAL8)||
               //  (varDataType==CDF.CDF_EPOCH && entryDataType == CDF.CDF_DOUBLE))
            return true;
            
        else  
            return false;
    }

  
    /**
     * Checks that the data type for validmin, validmax, and fillval
     * entries are corrolated with the variable data type.  If not, 
     * will change the respective entry data type and convert the 
     * data value when possible
     * 
     * @param var CDF Variable.
     * @param failures Vector containing any error messages generated by 
     *     check.
     * @param attrName variable attribute name.
     * @since version 0.3
     */
    private static void EntryControl(
        gsfc.nssdc.cdf.Variable var,
        Vector<String> failures,
        String attrName) {

        Attribute attr;
        Entry     entry;
     	    
        try {
            long cdfMode = var.getMyCDF().confirmReadOnlyMode();
            attr = var.getMyCDF().getAttribute(attrName);
	    try { 
                entry = attr.getEntry(var.getID());
                long entryDataType = entry.getDataType();
                long id=entry.getID();
                Object data = entry.getData();
                Object value = null;
                
                                                         
                if (!dataTypeEqual(var.getDataType(),entryDataType)) {

                    if(data.getClass().isArray())
                        value = getConvertedArrayValues(
                                    CDFUtils.getStringDataType(var),
                                    data, attrName);
                    else
                        value = getConvertedValue(
                                    CDFUtils.getStringDataType(var),
                                    data, attrName);
		                                      
                    if (value != null && cdfMode == CDF.READONLYoff) {
                    
                        entry.delete();
                                              		                
                        Entry.create(attr, id, var.getDataType(),value);
                        		           
                        failures.addElement(attrName+" data type '"+
		            CDFUtils.getStringDataType(entryDataType)+
		            "' did not match variable data type '"
		            +CDFUtils.getStringDataType(var) + "'.");
                        failures.addElement(attrName+
                            " data type has been changed to '" 
		            +CDFUtils.getStringDataType(var)+"'.");
                    }
                    else {

                        failures.addElement(attrName + " data type '" +
		            CDFUtils.getStringDataType(entryDataType) +
		            "' does not match variable data type '" +
		            CDFUtils.getStringDataType(var)+"'.");
                        //failures.addElement("The automatic conversion " +
                        //   "could not be done.");
                    }
                 }
	    } catch (CDFException e) { 
		failures.addElement(attrName+" entry is missing.  (" + e.getMessage() + ")");
	    }
	} catch (CDFException e) {
	    failures.addElement(attrName+" attribute is missing.  (" + e.getMessage() + ")");
	}
    }
    
    //compare validmin and validmax to enforce values between those extreme
    
    private static Number valuesOutsideBounds(Object data, Object validmin, Object validmax){
              
        int len = Array.getLength(data);
            
        if(validmin.getClass().isArray()&& 
                    validmax.getClass().isArray()&&
                    Array.getLength(validmin) == len &&
                    Array.getLength(validmin) == len){
                
            for (int i=0; i<len; i++){
                    
                if (Array.get(validmin,i)instanceof Number &&
                             Array.get(validmin,i)instanceof Number &&
                             Array.get(data,i) instanceof Number){
                            
                    Number dat = (Number)Array.get(data,i);
                    Number min = (Number)Array.get(validmin,i);
                    Number max = (Number)Array.get(validmax,i);
                            
                    if (dat != null && min != null && max != null) {
                                                      
                        if(dat.doubleValue() > max.doubleValue()||
                                    dat.doubleValue() < min.doubleValue()) 
                            
                            return dat;                                             
                    }
                }
            }
        }
                            
       else if (validmin instanceof Number && validmax instanceof Number){
               
            for (int i=0; i<len; i++){
                
                Object dat = Array.get(data,i);
                   
                if(dat != null && dat instanceof Number){
                                         
                    if(((Number)validmin).doubleValue() > ((Number)dat).doubleValue() ||
                                ((Number)validmax).doubleValue()< ((Number)dat).doubleValue())
                                
                        return (Number)dat;
                }
            }
       }                            
        return null;	    
    }
    
    
    private static void checkUnrepresentableValues(
        Number[] values, 
        String identifier, 
        Vector<String> errors) {

        if (values != null) {

            for (int i = 0; i < values.length; i++) {

                if (Double.isInfinite(values[i].doubleValue())) {

                    if (values.length == 1) {

                        errors.addElement(
                            identifier + " is infinite.  This is " +
                            "not recommended and may cause a " +
                            "problem for some software.");
                    }
                    else {

                        errors.addElement(
                            "The " + i + "th " + identifier + 
                            "value is infinite.  This is not " +
                            "recommended and may cause a problem " +
                            "for some software.");
                    }
                }
                else if (Double.isNaN(values[i].doubleValue())) {

                    if (values.length == 1) {

                        errors.addElement(
                            identifier + " is NaN.  This is " +
                            "not recommended and may cause a " +
                            "problem for some software.");
                    }
                    else {

                        errors.addElement(
                            "The " + i + "th " + identifier + 
                            "value is NaN.  This is not " +
                            "recommended and may cause a problem " +
                            "for some software.");
                    }
                }
            }
        }
    }


    private static void minMaxValidity(
        gsfc.spdf.istp.Variable var, 
        Vector<String> errors) {

        Number[] validMin = var.getValidMinNumber();
        Number[] validMax = var.getValidMaxNumber();

        checkUnrepresentableValues(validMin, "VALIDMIN", errors);
        checkUnrepresentableValues(validMax, "VALIDMAX", errors);

        if (validMin != null && validMax != null) {

            if (var.getDataType() == CDF_EPOCH16) {

                if (validMin[0].doubleValue() >=
                    validMax[0].doubleValue()) {

                    errors.addElement("VALIDMIN >= VALIDMAX");
                }
            }
            else {

                for (int i = 0; i < validMin.length; i++) {

                    if (validMin[i].doubleValue() >=
                        validMax[i].doubleValue()) {

                        errors.addElement("VALIDMIN >= VALIDMAX");
                    }
                }
            }
        }
    }


    private static Vector<String> minmaxCheck(
        gsfc.nssdc.cdf.Variable var,
        EnumSet<ISTPCompliance.Warnings> suppressedWarnings) {

	Vector<String> failures = new Vector<>();
  
        long dataType = var.getDataType();
                                       // var's cdf data type
        if (dataType != CDF.CDF_CHAR && dataType != CDF.CDF_UCHAR) {

            EntryControl(var,failures,"VALIDMIN");        
            EntryControl(var,failures,"VALIDMAX");

            if (!(suppressedWarnings.contains(
                      Warnings.VALIDMIN_GE_VALIDMAX) ||
                  suppressedWarnings.contains(Warnings.ALL))) {

                minMaxValidity(new gsfc.spdf.istp.Variable(var), 
                    failures);
            }
        }
        
        return failures;
    }


    private static Vector<String> fillvalCheck(
        gsfc.nssdc.cdf.Variable var) {
	Vector<String> failures = new Vector<>();
  
        long dataType = var.getDataType();
                                       // var's cdf data type
        if (dataType != CDF.CDF_CHAR && dataType != CDF.CDF_UCHAR) {

            EntryControl(var,failures,"FILLVAL");
        }
        
	return failures;
    }
    
    private static Vector<String> fillvalStandardCheck(
        gsfc.nssdc.cdf.Variable var){
	
        Vector<String> failures = new Vector<>();
                
        try {
        
            Entry entry = (var.getMyCDF().getAttribute("FILLVAL")).getEntry(var.getID());

            if(entry != null)
            { 
                

       // if(!CompareToStandard(CDFUtils.getStringDataType(var),entry.getData()))
                if(!FillvalAttribute.isValid(var.getDataType(), entry.getData())){
                    
                    gsfc.spdf.istp.Variable variable = 
                        new gsfc.spdf.istp.Variable(var);
                                       // ISTP version of var
                    String fill = 
                        FillvalAttribute.toString(variable.getDataType(),
                                                  variable.getFillval());
                                       // string representation of var's
                                       //  FILLVAL attribute value

                    failures.addElement("FILLVAL value of '" + fill +
                        "' is non-standard." );

                    String defaultFill = 
                        FillvalAttribute.toString(variable.getDataType(),
                                                  variable.getDefaultFillval());
                                       // string representation of var's
                                       //  standard FILLVAL value

                    failures.addElement("    The recommended value is '" +
                        defaultFill + "'.");
                }
            }
	} catch (CDFException e) {}
    return failures; 
    }


    //////////////////////////////////////////////////////////////
    //                                                          //
    //                     Global Tests                         //
    //                                                          //
    //////////////////////////////////////////////////////////////

    /**
     * Checks that the global attribute Logical_source is formed properly 
     * and the needed global attributes are present.
     *
     * @param cdf The CDF file to check for compliance
     * @return A vector containing the error messages
     */
    public static Vector<String> logicalSourceCheck(CDF cdf) {
	Vector<String> failures = new Vector<>();

        String logicalSource = null;   // Logical_source attribute value
        try {

            GlobalAttribute.getLogicalSourceValue(cdf);
        }
        catch (CDFException e) {};

        if (logicalSource == null) {

            // The error has already been reported and we should have 
            // never been called.
            return failures;
        }

        String correctLogicalSource = null;
        try {

            correctLogicalSource = 
                GlobalAttribute.buildLogicalSourceFromComponents(cdf);
        }
        catch (CDFException e) {};

        if (!logicalSource.equals(correctLogicalSource)) {

            failures.addElement("Logical_source should be \""+
                correctLogicalSource + "\". It is \""+
                logicalSource+"\"");         
        }

	return failures;
    }


    /**
     * Checks that the Logical_file_id gloabl attribute is formed
     * properly and that the needed global attributes are
     * present.
     *
     * @param cdf The CDF file to check for compliance
     * @return A vector containing error messages
     *
     * <B>Note:</B> At present this will only work on masters but 
     *  will be extended soon to work on all CDFs.  Also, all ISTP
     *  CDFs do not conform to this standard.
     */
    public static Vector<String> logicalIDCheck(CDF cdf) {
	String fileID = null, 
	    logicalSource = null;
	Vector<String> failures = new Vector<>();
        

	try {
	    fileID = (String)cdf.getAttribute("Logical_file_id").
		getEntry(0).getData();
	    try {
		logicalSource = (String)cdf.getAttribute("Logical_source").
		    getEntry(0).getData();
	    } catch (CDFException e2) {
		failures.addElement("Logical_source attribute is missing.  (" + e2.getMessage() + ")");
	    }

            Object dataVersion = 
                GlobalAttribute.getDataVersionValue(cdf);
                                       // Data_version global attribute
                                       // value
            if (dataVersion == null) {

                failures.addElement(
                    "Data_version attribute is missing.");
            }
            else {

                Entry dataVersionEntry = 
                    GlobalAttribute.getDataVersionEntry(cdf);
                                       // Data_version global attribute
                                       // entry
                if (dataVersionEntry.getDataType() != CDF.CDF_CHAR) {

                    failures.addElement(
                        "Data_version attribute is of type " +
                        DataType.getString(
                            dataVersionEntry.getDataType()) +
                        ".  CDF_CHAR is recommended.");
                }
            }

	    if (failures.size() > 0)
		return failures;

            String recommendedLogicalFileId = 
                GlobalAttribute.getRecommendedLogicalFileId(cdf);

            if (!fileID.equalsIgnoreCase(recommendedLogicalFileId)) {

		failures.addElement("Logical_file_id should be '" +
                    recommendedLogicalFileId + "'.  It is '" + fileID + "'.");
            }
	} catch (CDFException e2) {
	    failures.addElement("Logical_file_id attribute is missing.  (" + e2.getMessage() + ")");
	}

	return failures;
    }

    /**
     * Check <code>cdf</code> for all the required ISTP global and variable attributes.
     * @param cdf The CDF file to check for compliance
     * @return A vector containing any error statements
     */
    public static Vector<String> checkRequired(CDF cdf) {
	Attribute a;
	Vector<String> errors = new Vector<>();


	try { 
	    a = cdf.getAttribute("Project"); 
          
	    if (!hasEntries(a) )
		errors.addElement("Project has no entries.");
	} catch (CDFException e2) {
	    errors.addElement("Project global attribute is missing.  (" + e2.getMessage() + ")");
	}

	try { 
	    a = cdf.getAttribute("Source_name"); 
	    if (!hasEntries(a) )
		errors.addElement("Source_name has no entries.");
	} catch (CDFException e2) {
	    errors.addElement("Source_name global attribute is missing.  (" + e2.getMessage() + ")");
	}

	try { 
	    a = cdf.getAttribute("Descriptor"); 
	    if (!hasEntries(a) )
		errors.addElement("Descriptor has no entries.");
	} catch (CDFException e2) {
	    errors.addElement("Descriptor global attribute is missing.  (" + e2.getMessage() + ")");
	}

	try { 
	    a = cdf.getAttribute("Data_type"); 
	   if (!hasEntries(a) )
		errors.addElement("Data_type has no entries.");
	} catch (CDFException e2) {
	    errors.addElement("Data_type global attribute is missing.  (" + e2.getMessage() + ")");
	}

	try { 
	    a = cdf.getAttribute("PI_name"); 
	   if (!hasEntries(a) )
		errors.addElement("PI_name has no entries.");
	} catch (CDFException e2) {
	    errors.addElement("PI_name global attribute is missing.  (" + e2.getMessage() + ")");
	}

	try { 
	    a = cdf.getAttribute("PI_affiliation"); 
	  if (!hasEntries(a) )
		errors.addElement("PI_affiliation has no entries.");
	} catch (CDFException e2) {
	    errors.addElement("PI_affiliation global attribute is missing.  (" + e2.getMessage() + ")");
	}

	try { 
	    a = cdf.getAttribute("TEXT"); 
	    if (!hasEntries(a) )
		errors.addElement("TEXT has no entries.");
	} catch (CDFException e2) {
	    errors.addElement("TEXT global attribute is missing.  (" + e2.getMessage() + ")");
	}

	try { 
	    a = cdf.getAttribute("Discipline"); 
	   if (!hasEntries(a) )
		errors.addElement("Discipline has no entries.");
	} catch (CDFException e2) {
	    errors.addElement("Discipline global attribute is missing.  (" + e2.getMessage() + ")");
	}

        try { 

 	   a = cdf.getAttribute("Mission_group"); 

 	 if (!hasEntries(a) ){

               errors.addElement("Mission_group has no entries.");
           }
        } 
        catch (CDFException e2) {

            errors.addElement("Mission_group global attribute is missing.  (" + e2.getMessage() + ")");
        }

        try { 

 	   a = cdf.getAttribute("Instrument_type"); 

 	  if (!hasEntries(a) ){

               errors.addElement("Instrument_type has no entries.");
           }
        } 
        catch (CDFException e2) {

            errors.addElement("Instrument_type global attribute is missing.  (" + e2.getMessage() + ")");
        }

        try { 

 	   a = cdf.getAttribute("Logical_source_description"); 

 	   if (!hasEntries(a) ) {

               errors.addElement("Logical_source_description has no entries.");
           }
        } 
        catch (CDFException e2) {

            errors.addElement("Logical_source_description global attribute is missing.  (" + e2.getMessage() + ")");
        }

    //logical_source attribute already checked in logicalSourceCheck

	try { 
	    a = cdf.getAttribute("Logical_file_id"); 
	   if (!hasEntries(a) )
		errors.addElement("Logical_file_id has no entries.");
	} catch (CDFException e2) {
	    errors.addElement("Logical_file_id global attribute is missing.  (" + e2.getMessage() + ")");
	}
    
	return errors;
    }
    
    
    private static boolean hasEntries(Attribute attribute){
        
        if (attribute.getNumEntries() == 0) {

            return false;
        }
       
        try {

            Vector entries = attribute.getEntries();
       
            for (Enumeration e = entries.elements(); 
                                e.hasMoreElements();) {
                               
                Entry entry = (Entry)e.nextElement();
           
                try {
               
                    Object data = entry.getData();
           
                    if (!data.toString().trim().equalsIgnoreCase(""))
               
                        return true;
                
                }catch(Exception cdfex){
               
                    return false;
                }
            }
        }
        catch (CDFException e) {

            // must not have any entries
        }
        return false;
    }


    /**
     * Checks the SPASE gobal attributes.
     *
     * @param cdf CDF to check.
     * @param suppressedWarnings the warnings that are to be suppressed.
     * @param errors Vector in which error messages are returned.
     */
    private static void checkSpaseAttribute(
        CDF cdf,
        EnumSet<Warnings> suppressedWarnings,
        Vector<String> errors) {

        if (suppressedWarnings.contains(Warnings.MISSING_SPASE_ID) ||
            suppressedWarnings.contains(Warnings.ALL)) {

            return;
        }
        try {

            Attribute spaseResourceIdAttr =
                cdf.getAttribute("spase_DatasetResourceID");
            Vector spaseResourceIdEntries =
                spaseResourceIdAttr.getEntries();

            if (spaseResourceIdEntries.isEmpty()) {

                errors.addElement(
                    "spase_DatasetResourceID has no entry (value).");
            }
            else if (spaseResourceIdEntries.size() > 1) {

                errors.addElement(
                    "spase_DatasetResourceID has more than one " +
                    "entry (value).");
            }
            else {

                Entry value = (Entry)spaseResourceIdEntries.get(0);

                if (value == null) {

                    errors.addElement(
                        "spase_DatasetResourceID has no entry (value).");
                }
                else if (value.getDataType() == CDF.CDF_CHAR) {

                    String strValue = (String)value.getData();

                    if (!strValue.startsWith("spase://")) {

                        errors.addElement(
                        "spase_DatasetResourceID's value (" +
                        strValue + ") is not a valid SPASE " +
                        "resourceID.");
                    }
                }
                else {

                    errors.addElement(
                        "spase_DatasetResourceID entry is of type " +
                        CDFUtils.getStringDataType(value) +
                        ".  It should be CDF_CHAR.");
                }
            }
        }
        catch (CDFException e) {

            if (e.getCurrentStatus() == CDF.NO_SUCH_ATTR) {

                errors.addElement("spase_DatasetResourceID is missing.");
            }
            else {

                System.err.println("CDFException while accessing " +
                    "spase_DatasetResourceID: " + e.getMessage());
            }
        }
    }


    /**
     * Performs a check of the recommended ISTP global attributes in the
     * given <code>cdf</code>.
     * @param cdf The CDF file to check for compliance
     * @param suppressedWarnings the warnings that are to be suppressed.
     * @return A vector containing any error statements
     * @throws CDFException if a CDFException occurs.
     */
    public static Vector<String> checkRecommendedAttributes(
        CDF cdf,
        EnumSet<Warnings> suppressedWarnings)
        throws CDFException {

        Vector<String> errors = new Vector<>();// error statements to be 
                                        // returned
        // checkSpaseAttribute(cdf, suppressedWarnings, errors);

        Attribute linkTextAttr = null;  // Link_text attribute
        Vector linkTextEntries = null;  // Link_text entries
        Attribute linkTitleAttr = null; // Link_title attribute
        Vector linkTitleEntries = null; // Link_title entries
        Attribute httpLinkAttr = null;  // HTTP_link attribute
        Vector httpLinkEntries = null;  // HTTP_link entries

        try {

            linkTextAttr = cdf.getAttribute("LINK_TEXT");
            linkTextEntries = linkTextAttr.getEntries();

            linkTitleAttr = cdf.getAttribute("LINK_TITLE");
            linkTitleEntries = linkTitleAttr.getEntries();

            httpLinkAttr = cdf.getAttribute("HTTP_LINK"); 
            httpLinkEntries = httpLinkAttr.getEntries();
        }
        catch (CDFException e) {

            // these are just recommended and not required so do not
            // complain
            return errors;
        }

        for (int i = 0; 
             i < linkTitleEntries.size() && i < httpLinkEntries.size(); 
             i++) {

            Entry linkTitleEntry = (Entry)linkTitleEntries.elementAt(i);

            Entry httpLinkEntry = (Entry)httpLinkEntries.elementAt(i);

            if (linkTitleEntry != null && httpLinkEntry == null) {

                errors.addElement("LINK_TITLE " + i + 
                                " without corresponding HTTP_LINK entry");
            }
            else if (linkTitleEntry == null && httpLinkEntry != null) {

                errors.addElement("HTTP_LINK " + i + 
                                " without corresponding LINK_TITLE entry");
            }
        }

        for (int i = linkTitleEntries.size(); 
             i < httpLinkEntries.size(); i++) {

            if (httpLinkEntries.elementAt(i) != null) {

                errors.addElement("HTTP_LINK " + i +
                                " without corresponding LINK_TITLE entry");
            }
        }

        for (int i = httpLinkEntries.size();
             i < linkTitleEntries.size(); i++) {

            if (linkTitleEntries.elementAt(i) != null) {

                errors.addElement("LINK_TITLE " + i + 
                                " without corresponding HTTP_LINK entry");
            }
        }

        for (int i = 0; i < httpLinkEntries.size(); i++) {

            Entry httpLinkEntry = (Entry)httpLinkEntries.elementAt(i);

            if (httpLinkEntry != null) {

                String urlStr = (String)httpLinkEntry.getData();

                try {
                    //
                    // attempt to construct a URL from the entered value
                    //  to get the URL class to check the syntax 
                    //  
                    URL url = new URL(urlStr);
                }
                catch (MalformedURLException e) {

                    errors.addElement("Malformed HTTP Link URL " + i + 
                                          " " + e.getMessage());
                }
            }
        }

        return errors;
    }

	    
    /**
     * Checks the validity of the given source name value.
     *
     * @param source The Logical_source_name global attribute
     *
     * @return null if the value is valid.  Otherwise a string describing
     *         why the value is invalid is returned.
     *
     */
    static public String checkSource(String source) {

//        if (!SOURCE_NAME_MAPPINGS.containsKey(source)) {

            int i = 0; 
            boolean replace = false;

            for (char ch;
                 i < source.length() && (ch = source.charAt(i)) != '>'
                                     && (ch = source.charAt(i)) != '_'; 
                 i++) {

                if (!Character.isLetterOrDigit(ch)) {
                    
                    if(ch == '/'){
                        
                       replace = true;
                    }
                    else if (ch != '-') {

                        return "invalid character '" + ch + "'";
                    }
                }
            }

            if (i == 0) {

                return "missing value";
            }
            
            if (replace == true)
            {
                
                return "replace character";
            }
//        }

        return null;
    }


    public static List<String> checkFileCharacteristics(CDF cdf) {

        List<String> msgs = new ArrayList<String>();

        String version = cdf.getVersion();

        if (version.compareTo("3.6") < 0) {

            msgs.add("Warning: CDF file version is " + version);
            msgs.add("Version 3.6 or higher is required for files " +
                "containing TT2000 type values.");
        }

        if (cdf.getMajority() != CDF.COLUMN_MAJOR) {

            msgs.add("Warning: CDF is set for row major array " +
                "variables and column major is recommended.");
        }

        return msgs;
    }


    //////////////////////////////////////////////////////////////
    //                                                          //
    //                 Cleaning Methods                         //
    //                                                          //
    //////////////////////////////////////////////////////////////


    /**
     * Cleans the CDF file to ensure that the all attribute names are 
     * properly formatted.  If the given CDF is writable, any improper
     * attribute names will be changed.
     *
     * @param cdf The cdf to clean
     *
     * @return A Vector containing a String description of each 
     *    of the changes made, if any.
     */
    public static Vector<String> cleanAttributeNames(CDF cdf) {
	Attribute a;
	String aName, newName, tName, fName;
	Vector<String> changes = new Vector<>();
	for (Enumeration e = cdf.getAttributes().elements(); 
	     e.hasMoreElements();) {
	    a = (Attribute)e.nextElement();
	    // CDAWeb Attribute names can not have spaces 
	    aName = a.getName();
 	    fName = aName.trim();
	    tName = TextUtils.replaceSpaces(fName, '_');
	    if (!ATTR_NAMES.contains(aName))
		if (ATTR_NAMES.containsKey(tName.toLowerCase())) {

		    newName = (String)ATTR_NAMES.get(tName.
						     toLowerCase());

                    changes.addElement("Attribute '" + aName + 
                        "' should be named '" + newName + "'.");

                    try {

                        if (cdf.confirmReadOnlyMode() == 
                            CDF.READONLYoff) {

                            a.rename(newName);
                            changes.addElement(
                                "Renamed '" + aName + "' to '" + 
                                newName + "'.");
		        }
                    }
                    catch (CDFException exc) {

                        System.err.println("Could not rename " +
                            a + " due to " + exc);
                    }
		}
	}
	return changes;
    }


    public static Vector<String> checkDataVersionAttribute(CDF cdf) {

        String dataVersion=null;
        Vector<String> changes = new Vector<>();

        try {

            if(cdf.getAttribute("Data_version").getNumEntries() == 0) {

                Entry.create(cdf.getAttribute("Data_version"), 0, CDF_CHAR,"1");
                changes.addElement(
                    "Data_version entry was missing, it has been added as 1.");
            }   
        }
        catch (CDFException e1) {

        }
        return changes;
    }


    /**
     * Checks that the ISTP FUNCTion attribute is named FUNCT and not
     * FUNCTION (a previously valid name that was deprecated when the
     * IDL programming language made it a keyword).  If the old name
     * is found, it is renamed to the new value.
     *
     * @param cdf the CDF in which to check for the function 
     *            attribute's name
     * @return a List of Strings describing any issues concerning the
     *         function attribute's name
     */
    private static List<String> checkFunctionAttributes(CDF cdf) {

        List<String> errors = new ArrayList<>();
                                       // Strings describing any changes
                                       // that were made
        Vector attributes = cdf.getVariableAttributes();
                                       // all variable attributes in
                                       // the given cdf

        for (int i = 0; i < attributes.size(); i++) {

            Attribute attribute = (Attribute)(attributes.elementAt(i));
                                       // the i'th variable attribute
            String name = attribute.getName();
                                       // name of i'th variable 
                                       // attribute

            if (name.equalsIgnoreCase("function")) {

                StringBuffer errMsg = new StringBuffer("Variable(s) ");
                                       // buffer in which to assemble
                                       // a message concerning this
                                       // function attribute
                Vector vars = cdf.getVariables();
                                       // all the variables in the
                                       // given cdf

                for (int j = 0; j < vars.size(); j++) {

                    gsfc.nssdc.cdf.Variable var = 
                        (gsfc.nssdc.cdf.Variable)(vars.elementAt(j));
                                       // the j'th variable

                    try {

                        Entry entry = attribute.getEntry(var);
                                       // the function attribute entry
                                       // for this variable
                        if (j > 0) {

                            errMsg.append(", ");
                        }
                        errMsg.append(var.getName());
                    }
                    catch (CDFException e) {

                        // many not found exceptions are expected
                    }
                }

                errMsg.append(" have an attribute named " + name + ".");

                errors.add(errMsg.toString());
                errors.add(
                    "That name conflicts with the IDL keyword " +
                    "FUNCTION so the attribute will be renamed to " +
                    "FUNCT.");

                try {

                    attribute.rename("FUNCT");
                }
                catch (CDFException e) {

                    errors.add("Error renaming attribute " + name + 
                               ": " + e.getMessage());
                }
            }
        }

        return errors;
    }

}
