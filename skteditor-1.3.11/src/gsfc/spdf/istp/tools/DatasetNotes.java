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
 * $Id: DatasetNotes.java,v 1.12 2024/04/22 11:25:48 btharris Exp $
 */
package gsfc.spdf.istp.tools;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import gsfc.nssdc.cdf.CDF;
import gsfc.nssdc.cdf.CDFException;
import gsfc.nssdc.cdf.Variable;


/**
 * This class represents the "notes" for a dataset obtained from a 
 * <a href="http://cdf.gsfc.nasa.gov/">Common Data Format (CDF)</a>
 * file.  The "notes" are values which describe the dataset and 
 * are displayed as "dataset notes" on 
 * <a href="https://cdaweb.gsfc.nasa.gov/">Coordinated Data Analysis Web</a>.
 *
 * @version $Revision: 1.12 $
 * @author B. Harris
 */
public class DatasetNotes {

    /**
     * Name of dataset (i.e., Logical_source value).
     */
    private String name = null;

    /**
     * Global attribute notes.
     */
    private GlobalNotes globalNotes = null;

    /**
     * Variable attribute notes.  The variable's name is the key to the
     * map and the variable's VariableNotes is the stored value.
     */
    private TreeMap<String, VariableNotes> variableNotes = null;


    /**
     * Constructs a DatasetNotes object with the given name and attributes
     * from the given CDF file.
     *
     * @param name dataset name
     * @param cdf file from which to obtain initial attribute values from
     * @throws CDFException if an exception is encountered while accessing
     *                      the given cdf
     */
    public DatasetNotes(String name, CDF cdf) 
        throws CDFException {

        this.name = name;

        setValue(cdf);
    }


    /**
     * Gets the dataset's name.
     *
     * @return the name value
     * @see #setName
     */
    public String getName() {

        return name;
    }


    /**
     * Sets the dataset's name.
     *
     * @param value new name value
     * @see #getName
     */
    public void setName(String value) {

        name = value;
    }


    /**
     * Gets the globalNotes value.
     *
     * @return the globalNotes value
     * @see #setGlobalNotes
     */
    public GlobalNotes getGlobalNotes() {

        return (GlobalNotes)globalNotes.clone();
    }


    /**
     * Sets the globalNotes value.
     *
     * @param value new globalNotes value
     * @see #getGlobalNotes
     */
    public void setGlobalNotes(GlobalNotes value) {

        globalNotes = value;
    }


    /**
     * Gets the variableNotes.
     *
     * @return variableNotes value.
     * @see #setVariableNotes
     */
    public TreeMap<String, VariableNotes> getVariableNotes() {

        return new TreeMap<String, VariableNotes>(variableNotes);
    }


    /**
     * Gets the variableNotes sorted in ascending variable identifier
     * order.
     *
     * @return variableNotes sorted in ascending variable identifier
     *     order.
     */
     public ArrayList<VariableNotes> getVariableNotesInIdOrder() {

         ArrayList<VariableNotes> varNoteValues = 
             new ArrayList<>(variableNotes.values());
                                       // variable notes

         Collections.sort(varNoteValues, 
             new Comparator<VariableNotes>() {
                 @Override
                 public int compare(VariableNotes v1, VariableNotes v2) {

                     return (int)(v1.getId() - v2.getId());
                 }
             }
         );
         return varNoteValues;
     }


    /**
     * Sets the variableNotes.
     *
     * @param value new variableNotes value
     * @see #getVariableNotes
     */
    public void setVariableNotes(TreeMap<String, VariableNotes> value) {

        variableNotes = value;
    }


    /**
     * Set this objects values based upon the values contained in the
     * given CDF file.
     *
     * @param cdf CDF file to read dataset note values from
     * @throws CDFException if an exception occurs accessing the given
     *              file
     */
    public void setValue(CDF cdf)
        throws CDFException {

        globalNotes = new GlobalNotes();

        variableNotes = new TreeMap<String, VariableNotes>();

        mergeValue(cdf);

    }


    /**
     * Set this objects values based upon the values contained in the
     * given CDF file.  Any non-null attribute values from the CDF file 
     * replace any existing values.  Any existing values that are not 
     * contained in the CDF file retain their values.  
     *
     * @param cdf CDF file to read dataset note values from
     * @throws CDFException if an exception occurs accessing the given
     *              file
     */
    public void mergeValue(CDF cdf) 
        throws CDFException {

        globalNotes.mergeValue(cdf);

        for (Iterator varIter = cdf.getVariables().iterator();
             varIter.hasNext(); ) {

            Variable var = (Variable)varIter.next();
                                       // a variable
            long id = var.getID();     // variable's id
            String name = var.getName();
                                       // variable's name
            String catDesc = name;     // variable's CATDESC attribute
            String varType = "";       // variable's VAR_TYPE attribute

            try {

                Object vartype = var.getEntryData("VAR_TYPE");

                if (vartype instanceof String) {

                    varType = (String)vartype;
                }
                else if (vartype instanceof String[]) {

                    varType = ((String[])vartype)[0];
                }
            }
            catch (CDFException e) {

                // continue with initial value
            }
            try {

                Object catdesc = var.getEntryData("CATDESC");

                if (catdesc instanceof String) {

                    catDesc = (String)catdesc;
                }
                else if (catdesc instanceof String[]) {

                    catDesc = ((String[])catdesc)[0];
                }
            }
            catch (CDFException e) {

                // just use the variable's name
            }

            String notes = "";         // variable's notes

            try {

                Object varNotes = var.getEntryData("VAR_NOTES");

                if (varNotes instanceof String) {

                    notes = (String)varNotes;
                }
                else if (varNotes instanceof String[]) {

                    notes = ((String[])varNotes)[0];
                }
            }
            catch (CDFException e) {

                // Ignore exception.  And, consequently with 
                // notes == null, we will ignore ths variable.
                // This condition is why many variables are missing
                // in the resulting output.
            }
            String purpose = null;     // variable's VARIABLE_PURPOSE attr.
            try {

                Object purposeEntry = var.getEntryData("VARIABLE_PURPOSE");

                if (purposeEntry instanceof String) {

                    purpose = (String)purposeEntry;
                }
                else if (purposeEntry instanceof String[]) {

                    purpose = ((String[])purposeEntry)[0];
                }
            }
            catch (CDFException e) {

                // Ignore exception.  
            }

            VariableNotes varNotes = 
                (VariableNotes)variableNotes.get(name);
                                       // this variable's notes
            if (varNotes == null) {

                varNotes = new VariableNotes(name, id, varType,
                                             catDesc, notes, purpose);

                variableNotes.put(name, varNotes);
            }
            else {

                varNotes.mergeValue(name, id, varType,
                                    catDesc, notes, purpose);
            }
        }
    }


    /**
     * Gets this dataset's globalNotes description value.
     *
     * @return globalNotes description
     */
    public String getDescription() {

        return globalNotes.getDescription();
    }


    /**
     * Gets this dataset's globalNotes text value.
     *
     * @return globalNotes text
     */
    public List<String> getText() {

        return globalNotes.getText();
    }


    /**
     * Gets this dataset's globalNotes text supplement value.
     *
     * @return globalNotes text supplement
     */
    public List<String> getTextSupplement() {

        return globalNotes.getTextSupplement();
    }


    /**
     * Gets this dataset's globalNotes text supplement1 value.
     *
     * @return globalNotes text supplement1
     */
    public List<String> getTextSupplement1() {

        return globalNotes.getTextSupplement1();
    }


    /**
     * Gets this dataset's globalNotes mods value.
     *
     * @return globalNotes mods
     */
    public List<String> getMods() {

        return globalNotes.getMods();
    }


    /**
     * Gets this dataset's globalNotes caveats value.
     *
     * @return globalNotes caveats
     */
    public List<String> getCaveats() {

        return globalNotes.getCaveats();
    }


    /**
     * Gets this dataset's globalNotes spase_DatasetResourceID value.
     *
     * @return globalNotes spase_DatasetResourceID value.
     */
    public String getSpaseDatasetResourceID() {

        return globalNotes.getSpaseDatasetResourceID();
    }

    /**
     * Gets this dataset's globalNotes DOI values.
     *
     * @return globalNotes DOI values.
     */
    public List<String> getDois() {

        return globalNotes.getDois();
    }

}

