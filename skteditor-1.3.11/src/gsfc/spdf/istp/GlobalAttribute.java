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
 * $Id: GlobalAttribute.java,v 1.16 2023/06/14 16:44:04 btharris Exp $
 */
package gsfc.spdf.istp;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gsfc.nssdc.cdf.Attribute;
import gsfc.nssdc.cdf.CDF;
import gsfc.nssdc.cdf.CDFConstants;
import gsfc.nssdc.cdf.CDFException;
import gsfc.nssdc.cdf.Entry;
import gsfc.nssdc.cdf.util.CDFUtils;

import gsfc.spdf.cdf.DataType;

import gsfc.spdf.istp.Epoch;


/**
 * This class represents an International Solar-Terrestrial Physics
 * (ISTP) specialized CDF global attribute.  ISTP global attributes have
 * specific characteristics and conform to the
 * <a href="http://spdf.gsfc.nasa.gov/sp_use_of_cdf.html">
 * ISTP guidelines for CDF</a>.
 *
 * @author B. Harris
 * @version $Revision: 1.16 $
 */
public class GlobalAttribute {

    //
    // Note:  This class is incomplete.  The names of all the standard 
    // required and optional attributes should be moved here from other 
    // places in the SKTEditor code.  Additional methods also need to
    // be defined to replace fragments of code found thoughout the
    // SKTEditor.
    //

    /**
     * Get the specified global attribute from the given CDF.  Note 
     * that the name of the returned attribute may differ in case from 
     * the specified name since ISTP variable attribute names are case
     * insensitive.  Also, to be consistent with gsfc.nssdc.cdf classes,
     * the returned attribute may have trailing white space that is not
     * present in the specified name.
     *
     * @param cdf the CDF to get the attribute from.
     * @param name name of attribute to get.
     * @return specified variable attribute or null if it does not exist
     */
    public static Attribute get(CDF cdf, String name) {

        Vector attributes = cdf.getAttributes();
                                       // all attributes

        for (int i = 0; i < attributes.size(); i++) {

            Attribute attribute = (Attribute)attributes.elementAt(i);
                                       // i'th attribute

            if (name.equalsIgnoreCase(attribute.getName().trim()) &&
                attribute.getScope() == CDF.GLOBAL_SCOPE) {

                return attribute;
            }
        }

        return null;
    }


    /**
     * Gets the first entry of the specified global attribute.
     *
     * @param cdf the CDF to get the attribute from.
     * @param name name of attribute to get.
     * @return the first entry of the specified global attribute.  null
     *     if the attribute or entry does not exist.
     * @throws CDFException if a CDFException is encountered.
     */
    public static Entry getEntry(CDF cdf, String name) 
        throws CDFException {

        try {

            return getEntry(cdf, name, 0L);
        }
        catch (CDFException e) {

            if (e.getCurrentStatus() != CDF.NO_SUCH_ENTRY) {

                throw e;
            }
        }

        return null;
    }


    /**
     * Gets the specified entry of the specified global attribute.
     *
     * @param cdf the CDF to get the attribute from.
     * @param name name of attribute to get.
     * @param id entry identifier.
     * @return the specified entry of the specified global attribute.  
     *     null if the attribute or entry does not exist.
     * @throws CDFException if a CDFException is encountered.
     */
    public static Entry getEntry(CDF cdf, String name, long id) 
        throws CDFException {

        Attribute attribute = get(cdf, name);
                                       // specified attribute

        if (attribute != null) {

            try {

                return attribute.getEntry(id);
            }
            catch (CDFException e) {

                if (e.getCurrentStatus() != CDF.NO_SUCH_ENTRY) {

                    throw e;
                }
            }
        }

        return null;
    }


    /**
     * Gets the all the entries of the specified global attribute.
     *
     * @param cdf the CDF to get the attribute from.
     * @param name name of attribute to get.
     * @return the all the entries of the specified global attribute.  
     *     null if the attribute or entry does not exist.
     * @throws CDFException if a CDFException is encountered.
     */
    public static List<Entry> getEntries(CDF cdf, String name) 
        throws CDFException {

        List<Entry> entries = new ArrayList<Entry>();
                                       // requested entries
        Attribute attribute = get(cdf, name);
                                       // specified attribute
        if (attribute != null) {

            for (long i = 0; i < attribute.getNumEntries(); i++) {

                Entry entry = null;    // i'th entry

                try {

                    entry = attribute.getEntry(i);
            
                }
                catch (CDFException e) {

                    if (e.getCurrentStatus() != CDF.NO_SUCH_ENTRY) {

                        throw e;
                    }
                }
                entries.add(entry);
            }
        }

        return entries;
    }


    /**
     * Gets the data value of the first entry of the specified 
     * global attribute.
     *
     * @param cdf the CDF to get the attribute from.
     * @param name name of attribute to get.
     * @return the data value of the first entry of the specified 
     *     global attribute.  null if the attribute or entry does 
     *     not exist.
     * @throws CDFException if a CDFException is encountered.
     */
    public static Object getEntryValue(CDF cdf, String name)
        throws CDFException {

        return getEntryValue(cdf, name, 0L);
    }


    /**
     * Gets the data value of the specified entry of the specified 
     * global attribute.
     *
     * @param cdf the CDF to get the attribute from.
     * @param name name of attribute to get.
     * @param id entry identifier.
     * @return the data value of the specified entry of the specified 
     *     global attribute.  null if the attribute or entry does 
     *     not exist.
     * @throws CDFException if a CDFException is encountered.
     */
    public static Object getEntryValue(CDF cdf, String name, long id) 
        throws CDFException {

        Entry entry = getEntry(cdf, name, id);

        if (entry != null) {

            return entry.getData();
        }

        return null;
    }


    /**
     * Sets the data value of the first entry of the specified 
     * global attribute.
     *
     * @param cdf the CDF to get the attribute from.
     * @param name name of attribute to get.
     * @param value new value.
     * @throws CDFException if a CDFException is encountered.
     */
    public static void setEntryValue(CDF cdf, String name, 
        Object value) 
        throws CDFException {

        Entry entry = getEntry(cdf, name);
                                       // entry
        if (entry != null) {

            entry.putData(DataType.getDataType(value), value);
        }
        else {

            Attribute attribute = get(cdf, name);
                                       // attribute
            Entry.create(attribute, 0L, 
                DataType.getDataType(value), value);
        }
    }

    /**
     * Sets the data value of the specified entry of the specified 
     * global attribute.
     *
     * @param cdf the CDF to get the attribute from.
     * @param name name of attribute to get.
     * @param id entry identifier.
     * @param value new value.
     * @throws CDFException if a CDFException is encountered.
     */
    public static void setEntryValue(CDF cdf, String name, long id,
        Object value) 
        throws CDFException {

        Entry entry = getEntry(cdf, name, id);
                                       // entry
        if (entry != null) {

            entry.putData(DataType.getDataType(value), value);
        }
        else {

            Attribute attribute = get(cdf, name);
                                       // attribute
            Entry.create(attribute, id, 
                DataType.getDataType(value), value);
        }
    }


    /**
     * Determines if the first entry of the specified global attribute
     * is of type CDF_CHAR.
     *
     * @param cdf the CDF to get the attribute from.
     * @param name name of attribute to get.
     * @return true if the first entry of the specified global attribute
     *     is of type CDF_CHAR.  Otherwise, false.
     * @throws CDFException if a CDFException is encountered.
     */
    public static boolean entryIsChar(CDF cdf, String name) 
        throws CDFException {

        Entry entry = getEntry(cdf, name);
                                       // first entry of specifed
                                       // attribute
        if (entry != null) {

            return entry.getDataType() == CDF.CDF_CHAR;
        }

        return false;
    }


    /**
     * Creates the specified global attribute in accordance with ISTP
     * guidelines.
     *
     * @param cdf the CDF in which to create the attribute
     * @param name name of new attribute
     * @return new attribute
     * @throws CDFException if a CDFException occurs.
     * @throws ISTPIdentifierException if an attribute already exists 
     *     with the specified name (case insensitive comparison) or the
     *     specified name violates ISTP guidelines
     */
    public static Attribute create(CDF cdf, String name) 
        throws CDFException, ISTPIdentifierException {

        gsfc.spdf.istp.Attribute attribute = 
            gsfc.spdf.istp.Attribute.getIgnoreCase(cdf, name);
                                       // variable or global variable
                                       //  with specified name

        if (attribute != null) {

            throw new ISTPIdentifierException(
                    "Illegal attribute name.  Attribute " +
                    name + " already exists in CDF as " +
                    attribute.getName() + ".", name,
                    attribute.getName());
        }

        Variable.checkIdlName(name);

        return Attribute.create(cdf, name, CDF.GLOBAL_SCOPE);
    }
    

    /**
     * Gets the Logical_source global attribute value from the given
     * cdf.
     *
     * @param cdf CDF from which to get the Logical_source value.
     * @return Logical_source global attribute value or null if no 
     *             value exists.
     * @throws CDFException if a CDFException occurs.
     */
    public static String getLogicalSourceValue(CDF cdf)
        throws CDFException {

        Entry logicalSourceEntry = getEntry(cdf, "Logical_source");
                                        // Logical_source entry
        if (logicalSourceEntry != null) {

            return logicalSourceEntry.getData().toString();
        }
        else {

            return null;
        }
    }


    /**
     * Sets the Logical_source global attribute value in the given
     * cdf.  If the attribute does not exist, it is created.
     *
     * @param cdf CDF in which to set the Logical_source value.
     * @param value new value of attribute.
     * @throws CDFException if a CDFException occurs.
     */
    public static void setLogicalSourceValue(CDF cdf, String value)
        throws CDFException {

        Attribute logicalSourceAttr = get(cdf, "Logical_source");
                                        // Logical_source attribute
        if (logicalSourceAttr == null) {

            logicalSourceAttr =
                Attribute.create(cdf, "Logical_source", 
                    CDFConstants.GLOBAL_SCOPE);
        }

        Entry.create(logicalSourceAttr, 0, CDFConstants.CDF_CHAR, 
            value);
    }


    /**
     * Gets the Logical_file_id global attribute value from the given
     * cdf.
     *
     * @param cdf CDF from which to get the Logical_file_id value.
     * @return Logical_file_id global attribute value or null if no 
     *             value exists.
     * @throws CDFException if a CDFException occurs.
     */
    public static String getLogicalFileIdValue(CDF cdf)
        throws CDFException {

        Entry logicalFileIdEntry = getEntry(cdf, "Logical_file_id");
                                        // Logical_file_id entry
        if (logicalFileIdEntry != null) {

            return logicalFileIdEntry.getData().toString();
        }
        else {

            return null;
        }
    }


    /**
     * Sets the Logical_file_id global attribute value in the given
     * cdf.  If the attribute does not exist, it is created.
     *
     * @param cdf CDF in which to set the Logical_file_id value.
     * @param value new value of attribute.
     * @throws CDFException if a CDFException occurs.
     */
    public static void setLogicalFileIdValue(CDF cdf, String value)
        throws CDFException {

        Attribute logicalFileIdAttr = get(cdf, "Logical_file_id");
                                        // Logical_source attribute
        if (logicalFileIdAttr == null) {

            logicalFileIdAttr = 
                Attribute.create(cdf, "Logical_file_id", 
                    CDFConstants.GLOBAL_SCOPE);
        }

        Entry.create(logicalFileIdAttr, 0, CDFConstants.CDF_CHAR, 
            value);
    }


    /**
     * Gets the Source_name global attribute value from the given
     * cdf.
     *
     * @param cdf CDF from which to get the Source_name value.
     * @return Source_name global attribute value or null if no 
     *             value exists.
     * @throws CDFException if a CDFException occurs.
     */
    public static String getSourceNameValue(CDF cdf)
        throws CDFException {

        Entry sourceNameEntry = getEntry(cdf, "Source_name");
                                        // Source_name entry
        if (sourceNameEntry != null) {

            return sourceNameEntry.getData().toString();
        }
        else {

            return null;
        }
    }


    /**
     * Map containing the long and short Source_name values for the 
     * cases where the short value is not exactly the value before 
     * the '&gt;' * character.  The map key is the entire Source_name 
     * value and the map value is the short value.
     */
    private final static HashMap<String, String> SOURCE_NAME_MAP;

    static {

        SOURCE_NAME_MAP = new HashMap<String, String>();

        SOURCE_NAME_MAP.put("CANOPUS>Canadian Auroral Network Open Program Unified Study", "CN");
        SOURCE_NAME_MAP.put("DARN>Dual Auroral Radar Network", "DN");
        SOURCE_NAME_MAP.put("FAST>FAST Auroral Snapshot Explorer", "FA");
        SOURCE_NAME_MAP.put("GOES_6>Geostationary Operational Environmental Satellite 6", "G6");
        SOURCE_NAME_MAP.put("GOES_7>Geostationary Operational Environmental Satellite 7", "G7");
        SOURCE_NAME_MAP.put("GOES_8>Geostationary Operational Environmental Satellite 8", "G8");
        SOURCE_NAME_MAP.put("GOES_9>Geostationary Operational Environmental Satellite 9", "G9");
        SOURCE_NAME_MAP.put("GEOTAIL>Geomagnetic Tail", "GE");
        SOURCE_NAME_MAP.put("ISIS-1>International Satellite for Ionosphere Studies 1", "I1");
        SOURCE_NAME_MAP.put("ISIS-2>International Satellite for Ionosphere Studies 2", "I2");
        SOURCE_NAME_MAP.put("IMP-8>Interplanetary Monitoring Platform 8", "I8");
        SOURCE_NAME_MAP.put("INTERBALL-AURORAL>Interball Auroral Probe", "IA");
        SOURCE_NAME_MAP.put("INTERBALL-TAIL>Interball Tail Probe", "IT");
        SOURCE_NAME_MAP.put("INTERBALL-GROUND>Ground Based Data", "IG");
        SOURCE_NAME_MAP.put("IMAGE>Imager for Magnetopause to Aurora Global Exploration", "IM");
        SOURCE_NAME_MAP.put("LANL1990_095>Los Alamos National Laboratory 1990", "L0");
        SOURCE_NAME_MAP.put("LANL1991_080>Los Alamos National Laboratory 1989", "L1");
        SOURCE_NAME_MAP.put("LANL1994_084>Los Alamos National Laboratory 1994", "L4");
        SOURCE_NAME_MAP.put("LANL1997A>Los Alamos National Laboratory 1997", "L7");
        SOURCE_NAME_MAP.put("LANL1989_046>Los Alamos National Laboratory 1989", "l9");
        //SOURCE_NAME_MAP.put("POLAR>Polar Plasma Laboratory", "PO");
        SOURCE_NAME_MAP.put("SESAME>Satellite Experiments Simultaneous with Antarctic Measurements", "SE");
        SOURCE_NAME_MAP.put("STELAB>Solar-Terrestrial Environment Laboratory, Nagoya U.", "SL");
        SOURCE_NAME_MAP.put("SONDRESTROM>Greenland Incoherent-Scatter Radar", "SN");
        SOURCE_NAME_MAP.put("SOHO>Solar Heliospheric Observatory", "SO");
        SOURCE_NAME_MAP.put("SAMPEX>Solar Anomalous Magnetospheric Particle Explorer", "SX");
        SOURCE_NAME_MAP.put("ULYSSES>Ulysses", "UY");
        SOURCE_NAME_MAP.put("VI>Viking (Swedish satellite with auroral imager)", "VI");
        SOURCE_NAME_MAP.put("WIND>Wind Interplanetary Plasma Laboratory", "WI");

    }


    /**
     * Gets the "short" Source_name attribute value.
     *
     * @param cdf CDF from which to get the Source_name value.
     * @return "short" Source_name global attribute value or null if no 
     *             value exists.
     * @throws CDFException if a CDFException occurs.
     */
    public static String getShortSourceName(CDF cdf)
        throws CDFException {

        String shortSourceName = null; // short source name result

        String sourceName = getSourceNameValue(cdf);
                                       // full Source_name value
        if (sourceName != null) {

            shortSourceName = SOURCE_NAME_MAP.get(sourceName);

            if (shortSourceName == null) {

                shortSourceName = getShortPart(sourceName);
            }
        }

        return shortSourceName;
    }


    /**
     * Gets the "short" part (the value before the '&gt;' character) of 
     * the given global attribute value.
     *
     * Note that the short part provided by this function is not always
     * the "short" Source_name (@see #getShortSourceName(CDF)).
     *
     * @param value a global attribute value whose short part is to be
     *     returned.
     * @return the short part of the given value.  If no '&gt;' character 
     *     is contained in the given value, the original value is 
     *     returned.
     */
    public static String getShortPart(String value) {

        String[] parts = value.split(">"); 
                                       // sub-parts of value
        return parts.length > 0 ? parts[0] : "";
    }


    /**
     * Gets the Descriptor global attribute value from the given
     * cdf.
     *
     * @param cdf CDF from which to get the Descriptor value.
     * @return Descriptor global attribute value or null if no 
     *             value exists.
     * @throws CDFException if a CDFException occurs.
     */
    public static String getDescriptorValue(CDF cdf)
        throws CDFException {

        Entry descriptorEntry = getEntry(cdf, "Descriptor");
                                        // Descriptor entry
        if (descriptorEntry != null) {

            return descriptorEntry.getData().toString();
        }
        else {

            return null;
        }
    }


    /**
     * Gets the "short" Descriptor attribute value.
     *
     * @param cdf CDF from which to get the Descriptor value.
     * @return "short" Descriptor global attribute value or null if no 
     *             value exists.
     * @throws CDFException if a CDFException occurs.
     */
    public static String getShortDescriptor(CDF cdf)
        throws CDFException {

        String shortDescriptor = null; // short Descriptor result

        String descriptor = getDescriptorValue(cdf);
                                       // full Descriptor value
        if (descriptor != null) {

            shortDescriptor = getShortPart(descriptor);
        }

        return shortDescriptor;
    }


    /**
     * Gets the Data_type global attribute value from the given
     * cdf.
     *
     * @param cdf CDF from which to get the Data_type value.
     * @return Data_type global attribute value or null if no 
     *             value exists.
     * @throws CDFException if a CDFException occurs.
     */
    public static String getDataTypeValue(CDF cdf)
        throws CDFException {

        Entry dataTypeEntry = getEntry(cdf, "Data_type");
                                        // Data_type entry
        if (dataTypeEntry != null) {

            return dataTypeEntry.getData().toString();
        }
        else {

            return null;
        }
    }


    /**
     * Gets the "short" DataType attribute value.
     *
     * @param cdf CDF from which to get the DataType value.
     * @return "short" DataType global attribute value or null if no 
     *             value exists.
     * @throws CDFException if a CDFException occurs.
     */
    public static String getShortDataType(CDF cdf)
        throws CDFException {

        String shortDataType = null;   // short Data_type result

        String descriptor = getDataTypeValue(cdf);
                                       // full DataType value
        if (descriptor != null) {

            shortDataType = getShortPart(descriptor);
        }

        return shortDataType;
    }


    /**
     * Gets the File_naming_convention global attribute value from the 
     * given cdf.
     *
     * @param cdf CDF from which to get the File_naming_convention 
     *            value.
     * @return File_naming_convention global attribute value or null 
     *             if no value exists.
     * @throws CDFException if a CDFException occurs.
     */
    public static String getFileNamingConventionValue(CDF cdf)
        throws CDFException {

        Entry fileNamingConventionEntry = 
            getEntry(cdf, "File_naming_convention");
                                        // File_naming_convention 
                                        // entry
        if (fileNamingConventionEntry != null) {

            return fileNamingConventionEntry.getData().toString();
        }
        else {

            return null;
        }
    }


    /**
     * Sets the File_naming_convention global attribute value in the 
     * given cdf to the specified value.
     *
     * @param cdf CDF in which to set the File_naming_convention 
     *     value.
     * @param value new value for the attribute.
     * @throws CDFException if a CDFException occurs.
     */
    public static void setFileNamingConventionValue(
        CDF cdf, 
        String value)
        throws CDFException {

        Attribute fileNamingConventionAttr = 
            get(cdf, "File_naming_convention");
                                        // File_naming_convention 
                                        // attribute
        if (fileNamingConventionAttr == null) {

            try {

                fileNamingConventionAttr = 
                    create(cdf, "File_naming_convention");
            }
            catch (ISTPIdentifierException e) {

                // should not happen with File_naming_convention
            }
            Entry.create(fileNamingConventionAttr, 0, 
                CDFConstants.CDF_CHAR, value);
        }
        else {

            if (!fileNamingConventionAttr.getName().equals(
                    "File_naming_convention")) {

                // name probably has trailing white space

                fileNamingConventionAttr.rename("File_naming_convention");
            }
            try {

                fileNamingConventionAttr.getEntry(0).putData(
                    CDFConstants.CDF_CHAR, value);
            }
            catch (CDFException e) {

                if (e.getCurrentStatus() == CDF.NO_SUCH_ENTRY) {

                    Entry.create(fileNamingConventionAttr, 0,
                        CDFConstants.CDF_CHAR, value);
                }
                else {

                    throw e;
                }
            }
        }
    }


    /**
     * Gets the components of the File_naming_convention global
     * attribute value from the given cdf.  The array of components
     * returned are as follows:
     * <ul>
     *   <li>[0] source</li>
     *   <li>[1] datatype or descriptor</li>
     *   <li>[2] descriptor or datatype</li>
     *   <li>[3] "" or <code>java.text.SimpleDateFormat</code> date
     *       pattern</li>
     *   <li>[4] "" or <code>java.text.SimpleDateFormat</code> time
     *       pattern</li>
     * </ul>
     *
     * @param cdf CDF from which to get the File_naming_convention 
     *     value.
     * @return array containing the components of the 
     *     File_naming_convention global attribute value or a zero 
     *     length array if no valid File_naming_convention attribute 
     *     exists.
     * @throws CDFException if a CDFException occurs.
     */
    public static String[] getFileNamingComponents(CDF cdf)
        throws CDFException {

        String fileNaming = getFileNamingConventionValue(cdf);
                                       // File_naming_convention value
        if (fileNaming != null) {

            String[] components = fileNaming.split("_");
                                       // components of 
                                       // File_naming_convention value
            String dateComponent = ""; // date component
            String timeComponent = ""; // time component

            if (components.length < 3) {

                return new String[] {};
            }
            if (components.length > 3) { 

                // have a date/time component

                if (components[3].length() > 6) {

                    if (components[3].startsWith(
                            Filename.getDateFormat(0).toPattern())) {

                        dateComponent = 
                            Filename.getDateFormat(0).toPattern();
                        timeComponent = 
                            components[3].substring(
                                dateComponent.length());
                    }
                    else if (components[3].startsWith(
                             Filename.getDateFormat(1).toPattern())) {

                        dateComponent = 
                            Filename.getDateFormat(1).toPattern();
                        timeComponent = 
                            components[3].substring(
                                dateComponent.length());
                    }
                }
            }

            return new String[] {
                components[0], components[1], components[2], 
                dateComponent, timeComponent
            };
        }
        else {

            return new String[] {};
        }
    }


    /**
     * Gets the Data_version global attribute entry from the given cdf.
     *
     * @param cdf CDF from which to get the Data_version entry.
     * @return Data_version global attribute entry or null 
     *             if no entry exists.
     * @throws CDFException if a CDFException occurs.
     */
    public static Entry getDataVersionEntry(CDF cdf) 
        throws CDFException {

        return getEntry(cdf, "Data_version");
    }


    /**
     * Gets the Data_version global attribute value from the 
     * given cdf.
     *
     * @param cdf CDF from which to get the Data_version value.
     * @return Data_version global attribute value or null 
     *             if no value exists.
     * @throws CDFException if a CDFException occurs.
     */
    public static Object getDataVersionValue(CDF cdf)
        throws CDFException {

        Entry dataVersionEntry = getEntry(cdf, "Data_version");
                                        // Data_version entry
        if (dataVersionEntry != null) {

            return dataVersionEntry.getData();
        }
        else {

            return null;
        }
    }


    /**
     * Sets the Data_version global attribute value in the given
     * cdf.  If the attribute does not exist, it is created.
     *
     * @param cdf CDF in which to set the Logical_source value.
     * @param value new value of attribute.
     * @throws CDFException if a CDFException occurs.
     */
    public static void setDataVersionValue(CDF cdf, Object value)
        throws CDFException {

        Attribute dataVersionAttr = get(cdf, "Data_version");
                                        // Data_version attribute
        if (dataVersionAttr == null) {

            dataVersionAttr = Attribute.create(cdf, "Data_version", 
                CDFConstants.GLOBAL_SCOPE);
        }

        Entry dataVersionEntry = getDataVersionEntry(cdf);
                                        // Data_version entry
        if (dataVersionEntry != null) {

            dataVersionEntry.delete();
        }

        Entry.create(dataVersionAttr, 0, 
            DataType.getDataType(value), value);
    }


    /**
     * Constructs the Logical_source value from the Source_name,
     * Descriptor, Data_type, and File_naming_convention values.
     *
     * @param cdf CDF from which to get the component value.
     * @return Logical_source value.
     * @throws CDFException if a CDFException occurs.
    */
    public static String buildLogicalSourceFromComponents(CDF cdf) 
        throws CDFException {

        String shortSourceName = getShortSourceName(cdf);
                                       // short Source_name attribute 
                                       // value
        String shortDescriptor = getShortDescriptor(cdf);
                                       // short Descriptor attribute
                                       // value
        String shortDataType = getShortDataType(cdf);
                                       // short Data_type attribute
                                       // value
        String[] fileNamingComponents = getFileNamingComponents(cdf);
                                       // File_naming_convention
                                       // attribute component values

        if (fileNamingComponents == null ||
            fileNamingComponents.length == 0) {

            fileNamingComponents = 
                Filename.getDefaultNamingComponents();
        }

        StringBuilder logicalSource = new StringBuilder();
                                         // resulting logical source
                                         // value

        logicalSource.append(shortSourceName).append("_");

        if (fileNamingComponents[1].equals("datatype")) {

            logicalSource.append(shortDataType).
                append("_").append(shortDescriptor);
        }
        else {

            logicalSource.append(shortDescriptor).
                append("_").append(shortDataType);
        }

        return logicalSource.toString().toLowerCase();
    }


    /**
     * Regex pattern to identify a string consisting of a single digit.
     */
    private static final Pattern SINGLE_DIGIT_PATTERN =
        Pattern.compile("\\d");

    /**
     * Gets the recommended Logical_file_id value based upon the
     * global attribute and epoch values in the given cdf.
     *
     * @param cdf the CDF file whose recommended Logical_file_id value
     *            is to be determined.
     * @return the recommended Logical_file_id value.
     * @throws CDFException if a CDFException occurs.
     */
    public static String getRecommendedLogicalFileId(CDF cdf) 
        throws CDFException {

        String logicalSource = buildLogicalSourceFromComponents(cdf);
                                       // logical source value

        Calendar date = new GregorianCalendar(Epoch.UTC_TIME_ZONE);
                                       // date of data in file
        date.set(1, 0, 1, 0, 0, 0);

        try {

            Date earliestEpoch = Epoch.getEarliestEpochDate(cdf);
                                       // earliest epoch value
            if (earliestEpoch != null) {

                Calendar earliestEpochCal = 
                    new GregorianCalendar(Epoch.UTC_TIME_ZONE);
                                       // Calendar version of 
                                       // earliestEpoch
                earliestEpochCal.setTime(earliestEpoch);

                if (earliestEpochCal.get(Calendar.YEAR) != 1) {

                    date.setTime(earliestEpoch);
                }
            }
        }
        catch (CDFException cdfException) {

            // use initial default value
        }

        SimpleDateFormat dateTimeFormat =
            getFileNamingDateTimeFormat(cdf);
                                       // date/time format from cdf
        String dateStr = dateTimeFormat.format(date.getTime());
                                       // string representation of
                                       // earliest date value
        if (date.get(Calendar.YEAR) == 1) {

            // a master cdf

            dateStr = dateStr.replaceAll("\\d", "0");
        }

        String dataVersion = null;     // data version value

        Object dataVersionObj = getDataVersionValue(cdf);

        if (dataVersionObj != null) {

            dataVersion = dataVersionObj.toString();

            if (SINGLE_DIGIT_PATTERN.matcher(dataVersion).matches()) {

                dataVersion = "0" + dataVersion;
            }
        }
        else {

            dataVersion = "01";
        }

        return logicalSource + "_" + dateStr + "_v" + dataVersion;
    }


    /**
     * Gets the source/datatype/descriptor file naming options.
     *
     * @return array containing the source/datatype/descriptor file 
     *     naming options.  Element zero contains the recommended 
     *     default value.
     */
    public static String[] getFileNamingSourceOptions() {

        return new String[] {
            "source_datatype_descriptor_",
            "source_descriptor_datatype_"
        };
    }


    /**
     * Gets a <code>java.text.SimpleDateFormat</code> object for the
     * format of data/time values corresponding to the format
     * specified by the File_naming_convention attribute in the given
     * cdf.
     *
     * @param cdf the cdf to query for the File_naming_convention.
     * @return a <code>java.text.SimpleDateFormat</code> object for the
     *     format of data/time values corresponding to the format
     *     specified by the File_naming_convention attribute in the 
     *     given cdf.
     */
    public static SimpleDateFormat getFileNamingDateTimeFormat(
        CDF cdf) {

        String[] components = null;    // the file naming components
                                       // from the cdf
        try {

            components = getFileNamingComponents(cdf);
        }
        catch (CDFException e) {};

        if (components == null || components.length < 5) {

            return Filename.getDefaultNamingDateTimeFormat();
        }
        String pattern = "";           // date/time format pattern

        pattern = components[3] + components[4];

        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
                                       // the date format from the file
        dateFormat.setTimeZone(Epoch.UTC_TIME_ZONE);

        return dateFormat;
    }


    /**
     * Adds the date and time formats to the File_naming_convention 
     * attribute's value if those formats are not already present.
     * The formats which are added are deduced from the given filename.
     * Prior to September 2013, the File_naming_convention value did
     * not have the date and time formats (even though filenames
     * typically did include at least the date).  This function 
     * provides a means for updating old values to new ones that 
     * include the data and time formats.
     *
     * @param cdf the cdf from which to get/set the 
     *     File_naming_convention attribute value.
     * @param filename a filename from which to deduce the date and
     *     formats from.
     * @return the new value of the File_naming_convention attribute
     *     or null if the value was not changed.
     * @throws CDFException if a CDFException occurs.
     */
    public static String addDateTimeToFileNamingConvention(
        CDF cdf,
        String filename) 
        throws CDFException {

        String[] components = getFileNamingComponents(cdf);
                                       // file naming convention 
                                       // components

        if (components == null || components.length < 5) {

            return null;
        }

        if (components[3].length() == 0) {

            // guess date/time components

            SimpleDateFormat[] dateTimeFormats =
                getDateTimeFormatsFromFilename(filename);
                                        // the date and time formats
                                        // used in the given filename
            String fileNamingConvention = 
                components[0] + "_" + components[1] + "_" +
                components[2];          // new File_naming_convention 
                                        // value
            if (dateTimeFormats[0].toPattern().length() > 0) {

                fileNamingConvention += "_" +
                    dateTimeFormats[0].toPattern() + 
                    dateTimeFormats[1].toPattern();
            }

            setFileNamingConventionValue(cdf, fileNamingConvention);

            return fileNamingConvention;
        }

        return null;
    }


    /**
     * Attempts to determine the date and time formats used in the
     * given filename.  The returned array of 
     * <code>java.text.SimpleDateFormat</code> values are as describe
     * below:
     * <ul>
     *   <li>[0] date format or a format of "" if no date was found</li>
     *   <li>[1] time format or a format of "" if no time was found</li>
     * </ul>
     *
     * @param filename filename to examine for date and time values.
     * @return array of <code>java.text.SimpleDateFormat</code> values
     *     as described above.
     */
    public static SimpleDateFormat[] getDateTimeFormatsFromFilename(
        String filename) {

        SimpleDateFormat[] formats = new SimpleDateFormat[] {
            new SimpleDateFormat(""),
            new SimpleDateFormat("")
        };                             // resulting date and time 
                                       // formats
        formats[0].setTimeZone(Epoch.UTC_TIME_ZONE);
        formats[1].setTimeZone(Epoch.UTC_TIME_ZONE);

        String[] components = filename.split("_");
                                       // components of given filename
        if (components.length > 3) {

            ParsePosition position = new ParsePosition(0);
                                       // parse position within filename
            for (SimpleDateFormat dateFormat : 
                 Filename.getDateFormats()) {

                Date date = dateFormat.parse(components[3], position);
                                       // found date value
                if (date != null) {

                    formats[0] = dateFormat;
                    // I do not understand why position is not what the
                    // documentation states it will be (JDK 1.6).
                    position.setIndex(dateFormat.toPattern().length());
                    break;
                }
            }
            if (position.getIndex() > 0 && 
                position.getIndex() < components[3].length()) {

                for (SimpleDateFormat timeFormat :
                     Filename.getTimeFormatsParseOrder()) {

                    Date time = 
                        timeFormat.parse(components[3], position);
                                       // found time value
                    if (time != null) {

                        formats[1] = timeFormat;
                        break;
                    }
                }
            }
        }

        return formats;
    }


    /**
     * Guesses what the File_naming_convention should be based upon
     * global attribute values in the given CDF and the given 
     * filename.
     *
     * @param cdf cdf whose attribute values are used to guess the
     *     value of File_naming_convention.
     * @param filename name of file that is used to guess the
     *     value of File_naming_convention.
     * @return guess of File_naming_convention.
     * @throws CDFException if a CDFException occurs.
     */
    public static String guessFileNamingConvention(
        CDF cdf,
        String filename) 
        throws CDFException {

        String[] fileComponents = filename.split("_");
                                       // filename components
        String[] components = new String[5];
                                       // guessed component values

        String descriptor = getShortDescriptor(cdf);
        String datatype = getShortDataType(cdf);

        components[0] = "source_";
        components[1] = "datatype_";
        components[2] = "descriptor_";

        if (fileComponents.length > 1 &&
            fileComponents[1].equalsIgnoreCase(descriptor)) {

            components[1] = "descriptor_";
            components[2] = "datatype_";
        }

        SimpleDateFormat[] formats = 
            getDateTimeFormatsFromFilename(filename);

        components[3] = formats[0].toPattern();
        components[4] = formats[1].toPattern();

        if (components[3].equals("")) {

            components[3] = Filename.getDefaultNamingComponents()[3];
        }

        return components[0] + components[1] + components[2] +
               components[3] + components[4];
    }

}
