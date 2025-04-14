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
 * $Id: GlobalNotes.java,v 1.16 2024/04/22 11:25:48 btharris Exp $
 */
package gsfc.spdf.istp.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gsfc.spdf.istp.Doi;

import gsfc.nssdc.cdf.CDF;
import gsfc.nssdc.cdf.CDFException;
import gsfc.nssdc.cdf.Entry;


/**
 * This class represents the "notes" obtained from a 
 * <a href="http://cdf.gsfc.nasa.gov/">Common Data Format (CDF)</a>
 * file's global attributes.  The "notes" are values which describe a
 * dataset and are displayed as "dataset notes" on 
 * <a href="https://cdaweb.gsfc.nasa.gov/">Coordinated Data Analysis Web</a>.
 *
 * @version $Revision: 1.16 $
 * @author B. Harris
 */
public class GlobalNotes 
    implements Cloneable {

    /**
     * Value of the "Logical_source" global attribute.
     */
    private String logicalSource = null;

    /**
     * Value of the "Logical_source_description" global attribute.
     */
    private String logicalSourceDescription = null;

    /**
     * Value of the "spase_DatasetResourceID" global attribute.
     */
    private String spaseDatasetResourceID = null;

    /**
     * Value of the "DOI" global attribute.
     */
    private List<String> dois = new ArrayList<>();

    /**
     * Value of the "PI_name" global attribute.
     */
    private String piName = null;

    /**
     * Value of the "PI_affiliation" global attribute.
     */
    private String piAffiliation = null;

    /**
     * Value of the "TEXT" global attribute.  This is a list of Strings.
     */
    private List<String> text = new ArrayList<>();

    /**
     * Value of the "TEXT_supplement" global attribute.  This is a list 
     * of Strings.
     */
    private List<String> textSupplement = new ArrayList<>();

    /**
     * Value of the "TEXT_supplement1" global attribute.  This is a list 
     * of Strings.
     */
    private List<String> textSupplement1 = new ArrayList<>();

    /**
     * Value of the "MODS" global attribute.  This is a list 
     * of Strings.
     */
    private List<String> mods = new ArrayList<>();

    /**
     * Value of the "Caveats" global attribute.  This is a list 
     * of Strings.
     */
    private List<String> caveats = new ArrayList<>();

    /**
     * Regular expression pattern matching CDAWeb CDF filenames with a
     * capture group for the data's date portion of the filename.
     */
    private final static Pattern filenamePattern =
                Pattern.compile("(?:.*/)?\\w+_\\w+_\\w+_(\\d+)_v\\d+\\.cdf");


    /**
     * Constructs a GlobalNotes object with null values.  Use the 
     * #setValues(CDF) method to initialize this object from values in
     * a CDF file.
     */
    public GlobalNotes() {

        // setValues does the real initialization
    }


    /**
     * Determines whether the give filename conforms to the naming
     * convention for master CDF files.
     *
     * @param filename name of a file
     * @return true if the given filename conforms to the naming convention
     *              for master CDF files.  Otherwise, false.
     */
    public static boolean isMasterCdf(String filename) {

        Matcher matcher = filenamePattern.matcher(filename);
                                       // filename pattern matcher

        if (matcher.matches() && matcher.group(1).equals("00000000")) {

            return true;
        }

        return false;
    }


    /**
     * Gets the canonical form of the given DOI values.
     *
     * @param dois DOI values.
     * @return canonical representation of the give DOI values.
     */
    private static List<String> getCanonicalDois(
        List<String> dois) {

        List<String> canonicalDois = new ArrayList<>();

        for (String doi : dois) {

            String[] extraDois = doi.split("\\s*,\\s*");

            if (extraDois != null && extraDois.length > 0) {

                for (int j = 0; j < extraDois.length; j++) {

                    canonicalDois.add(Doi.getCanonical(extraDois[j]));
                }
            }
        }

        return canonicalDois;
    }


    /**
     * Initializes this object from values in the given CDF file.  For
     * any attributes that already have a value when this method is
     * called, the value from the CDF file replaces the current value
     * unless the CDF file's value is null or blank.
     *
     * @param cdf an open CDF file from which to read the global 
     *            attribute values to initialize this object
     */
    public void mergeValue(CDF cdf) {

        String value = null;           // an attribute's value
        String filename = null;        // given cdf's filename

        try {

            filename = cdf.getName();

            value = (String)(cdf.getAttribute("Logical_source").
                             getEntry(0).getData());
        }
        catch (CDFException e) {

            if (isMasterCdf(filename)){

                System.err.println(
                    "Unable to get 'Logical_source' value from " +
                    filename + ": " + e.getMessage());
                System.err.println(
                    "Please correct this problem or the links " +
                    "to the information from CDAWeb will not " +
                    "function properly.");
            }
        }

        if (value != null && !value.trim().equals("")) {

            setLogicalSource(value);
        }

        value = getGlobalAttributeEntry(cdf, "Logical_source_description");

        if (value != null && !value.trim().equals("")) {

            setLogicalSourceDescription(value);
        }

        value = getGlobalAttributeEntry(cdf, "spase_DatasetResourceID");

        if (value != null && getSpaseDatasetResourceID() == null) {

            setSpaseDatasetResourceID(value);
        }

        // The following was written before the name of the DOI global
        // attribute was approved so it will need to be changed if the
        // final name is something other than DOI.
        List<String>dois = getGlobalAttributeEntries(cdf, "DOI");

        setDois(getCanonicalDois(dois));

        value = getGlobalAttributeEntry(cdf, "PI_name");

        if (value != null && !value.trim().equals("")) {

            setPiName(value);
        }

        value = getGlobalAttributeEntry(cdf, "PI_affiliation");

        if (value != null && !value.trim().equals("")) {

            setPiAffiliation(value);
        }

        List<String> arrayValue = null;   // an attribute's value

        arrayValue = getText();

        if (arrayValue.size() == 0) {

            arrayValue = getGlobalAttributeEntries(cdf, "TEXT");

            if (arrayValue.size() > 0) {

                setText(arrayValue);
            }
            else {

                setText(getGlobalAttributeEntries(cdf, "Text"));
            }
        }
        else if (arrayValue.size() == 1) {

            // single value from master

            value = (String)arrayValue.get(0);

            if (value != null && value.trim().equals("")) {

                arrayValue = getGlobalAttributeEntries(cdf, "TEXT");

                if (arrayValue.size() > 0) {

                    setText(arrayValue);
                }
                else {

                    setText(getGlobalAttributeEntries(cdf, "Text"));
                }
            }
        }
        // else use master cdf value and ignore value from data cdf

        arrayValue = getGlobalAttributeEntries(cdf, "TEXT_supplement");

        if (arrayValue.size() != 0) {

            setTextSupplement(arrayValue);
        }

        arrayValue = getGlobalAttributeEntries(cdf, "TEXT_supplement_1");

        if (arrayValue.size() != 0) {

            setTextSupplement(arrayValue);
        }

        arrayValue = getGlobalAttributeEntries(cdf, "MODS");

        if (arrayValue.size() != 0) {

            setMods(arrayValue);
        }

        arrayValue = getGlobalAttributeEntries(cdf, "Caveats");

        if (arrayValue.size() != 0) {

            setCaveats(arrayValue);
        }
    }


    /**
     * Gets the "Logical_source" global attribute value.
     *
     * @return the logicalSource value
     * @see #setLogicalSource
     */
    public String getLogicalSource() {

        return logicalSource;
    }


    /**
     * Sets the "Logical_source" global attribute value.
     *
     * @param value new logicalSource value
     * @see #getLogicalSource
     */
    public void setLogicalSource(String value) {

        logicalSource = value;
    }


    /**
     * Gets the "Logical_source_description" global attribute value.
     *
     * @return the logicalSourceDescription value
     * @see #setLogicalSourceDescription
     */
    public String getLogicalSourceDescription() {

        return logicalSourceDescription;
    }


    /**
     * Sets the "Logical_source_description" global attribute value.
     *
     * @param value new logicalSourceDescription value
     * @see #getLogicalSourceDescription
     */
    public void setLogicalSourceDescription(String value) {

        logicalSourceDescription = value;
    }


    /**
     * Gets the "spase_DatasetResourceID" global attribute value.
     *
     * @return the spaseDatasetResourceID value
     * @see #setSpaseDatasetResourceID
     */
    public String getSpaseDatasetResourceID() {

        return spaseDatasetResourceID;
    }


    /**
     * Sets the "spase_DatasetResourceID" global attribute value.
     *
     * @param value new spaseDatasetResourceID value
     * @see #getSpaseDatasetResourceID
     */
    public void setSpaseDatasetResourceID(String value) {

        spaseDatasetResourceID = value;
    }


    /**
     * Gets the "DOI" global attribute values.
     *
     * @return the DOI values
     * @see #setDois
     */
    public List<String> getDois() {

        return dois;
    }


    /**
     * Sets the "DOI" global attribute values.
     *
     * @param value new DOI values
     * @see #getDois
     */
    public void setDois(List<String> value) {

        dois = value;
    }


    /**
     * Gets the "PI_name" global attribute value.
     *
     * @return the piName value
     * @see #setPiName
     */
    public String getPiName() {

        return piName;
    }


    /**
     * Sets the "PI_name" global attribute value.
     *
     * @param value new piName value
     * @see #getPiName
     */
    public void setPiName(String value) {

        piName = value;
    }


    /**
     * Gets the "PI_affiliation" global attribute value.
     *
     * @return the piAffiliation value
     * @see #setPiAffiliation
     */
    public String getPiAffiliation() {

        return piAffiliation;
    }


    /**
     * Sets the "PI_affiliation" global attribute value.
     *
     * @param value new piAffiliation value
     * @see #getPiAffiliation
     */
    public void setPiAffiliation(String value) {

        piAffiliation = value;
    }


    /**
     * Gets the "TEXT" global attribute value.  This is a list of Strings.
     *
     * @return the text value
     * @see #setText
     */
    public List<String> getText() {

        return text;
    }


    /**
     * Sets the "TEXT" global attribute value.
     *
     * @param value new text value
     * @see #getText
     */
    public void setText(List<String> value) {

        text = value;
    }


    /**
     * Gets the "TEXT_supplement" global attribute value.  This is a list 
     * of Strings.
     *
     * @return the testSupplement value
     * @see #setTextSupplement
     */
    public List<String> getTextSupplement() {

        return textSupplement;
    }


    /**
     * Sets the "TEXT_supplement" global attribute value.
     *
     * @param value new textSupplement value
     * @see #getTextSupplement
     */
    public void setTextSupplement(List<String> value) {

        textSupplement = value;
    }


    /**
     * Gets the "TEXT_supplement1" global attribute value.  This is a list 
     * of Strings.
     *
     * @return the testSupplement1 value
     * @see #setTextSupplement1
     */
    public List<String> getTextSupplement1() {

        return textSupplement1;
    }


    /**
     * Sets the "TEXT_supplement1" global attribute value.
     *
     * @param value new textSupplement1 value
     * @see #getTextSupplement1
     */
    public void setTextSupplement1(List<String> value) {

        textSupplement1 = value;
    }


    /**
     * Gets the "MODS" global attribute value.  This is a list 
     * of Strings.
     *
     * @return the mods value
     * @see #setMods
     */
    public List<String> getMods() {

        return mods;
    }


    /**
     * Sets the "MODS" global attribute value.
     *
     * @param value new mods value
     * @see #getMods
     */
    public void setMods(List<String> value) {

        mods = value;
    }


    /**
     * Gets the "Caveats" global attribute value.  This is a list 
     * of Strings.
     *
     * @return the caveats value
     * @see #setCaveats
     */
    public List<String> getCaveats() {

        return caveats;
    }


    /**
     * Sets the "Caveats" global attribute value.
     *
     * @param value new caveats value
     * @see #getCaveats
     */
    public void setCaveats(List<String> value) {

        caveats = value;
    }



    /**
     * Gets the first entry value of the identified attribute from the
     * given CDF file.  The entry's value must be a String.
     *
     * @param cdf the CDF file to get the value from
     * @param attribute the name of the attribute whose first entry
     *                  value is to be gotten
     * @return the first entry value of attribute
     */
    private static String getGlobalAttributeEntry(CDF cdf, 
                                                  String attribute) {

        try {

            return (String)(cdf.getAttribute(attribute).
                             getEntry(0).getData());
        }
        catch (CDFException e) {

            // ignore it

            return null;
        }
    }


    /**
     * Gets the entry values of the identified attribute from the
     * given CDF file.  The entry's values must be Strings.
     *
     * @param cdf the CDF file to get the value from
     * @param attribute the name of the attribute whose entry
     *                  values are to be gotten
     * @return a list containing the entry values of attribute
     */
    private static List<String> getGlobalAttributeEntries(CDF cdf, 
                                                       String attribute) {

        List<String> results = new ArrayList<>();
                                       // specified Attribute's entries
                                       //  that are to be returned
        Vector entries = null;         // the specified Attribute's entries

        try {

            entries = cdf.getAttribute(attribute).getEntries();
                                       // the specified Attribute's entries
        }
        catch (CDFException e) {

            return results;
        }

        for (Iterator i = entries.iterator(); i.hasNext(); ) {

            Entry entry = (Entry)i.next();

            if (entry != null) {

                String data = null;
                try {

                    data = (String)(entry.getData());
                }
                catch (CDFException e) {

                }

                if (data != null && !data.trim().equals("")) {

                    results.add(data);
                }
            }
        }

        return results;
    }


    /**
     * Get the description value that consists of the concatenation of
     * the logicalSourceDescription, piName, and piAffiliation.
     *
     * @return description value
     */
    public String getDescription() {

        StringBuffer description = new StringBuffer();
                                       // description buffer

        String logicalSourceDescription = getLogicalSourceDescription();
                                       // logical source description

        if (logicalSourceDescription != null) {

            description.append(logicalSourceDescription);
        }
        else {

            description.append("No description");
        }

        String piName = getPiName();   // PI name

        if (piName != null) {

            description.append(" - ").append(piName);

            String piAffiliation = getPiAffiliation();
                                       // PI affiliation

            if (piAffiliation != null) {

                description.append(" (").append(piAffiliation).append(")");
            }
        }

        return description.toString();
    }


    /**
     * Creates and returns a copy of this object.
     *
     * @return a clone of this instance.
     * @see java.lang.Object#clone
     */
    public Object clone() {

        try {

            GlobalNotes gn = (GlobalNotes)super.clone();
                                       // global notes clone

            gn.text = new ArrayList<>(text);
            gn.textSupplement = new ArrayList<>(textSupplement);
            gn.textSupplement1 = new ArrayList<>(textSupplement1);
            gn.mods = new ArrayList<>(mods);
            gn.caveats = new ArrayList<>(caveats);

            return gn;
        }
        catch (CloneNotSupportedException e) {

            return null;
        }
    }


}
