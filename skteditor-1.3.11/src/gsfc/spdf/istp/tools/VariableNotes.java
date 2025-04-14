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
 * $Id: VariableNotes.java,v 1.7 2022/03/24 10:38:41 btharris Exp $
 */
package gsfc.spdf.istp.tools;


import gsfc.nssdc.cdf.CDFException;
import gsfc.nssdc.cdf.Variable;


/**
 * This class represents the "notes" obtained from a 
 * <a href="http://cdf.gsfc.nasa.gov/">Common Data Format (CDF)</a>
 * variable.  The "notes" are values which describe a variable and 
 * are displayed as "dataset notes" on 
 * <a href="https://cdaweb.gsfc.nasa.gov/">Coordinated Data Analysis Web</a>.
 *
 * @version $Revision: 1.7 $
 * @author B. Harris
 */
public class VariableNotes {

    /**
     * Variable's name.
     */
    private String name = null;

    /**
     * Value of (CDF) variable ID.
     */
    private long id = -1;

    /**
     * Value of the "VAR_TYPE" attribute.
     */
    private String varType = null;

    /**
     * Value of the "CATDESC" attribute.
     */
    private String catDesc = null;

    /**
     * Value of the "VAR_NOTES" attribute.
     */
    private String notes = null;

    /**
     * Value of the "VARIABLE_PURPOSE" attribute.
     */
    private String purpose = null;


    /**
     * Constructs a VariableNotes object with the given values.  
     *
     * @param name variable name
     * @param id variable identifier.
     * @param varType variable VAR_TYPE value.
     * @param catDesc variable CATDESC value.
     * @param notes variable notes
     * @param purpose VARIABLE_PURPOSE value.
     */
    public VariableNotes(
        String name, 
        long id, 
        String varType,
        String catDesc,
        String notes,
        String purpose) {

        mergeValue(name, id, varType, catDesc, notes, purpose);
    }


    /**
     * Merges the given variable name and notes values with this object's
     * own values.  That is, unless the given values are null; blank; or,
     * in the case of a notes value, a '.'; they replace this object's
     * current values.
     *
     * @param name variable name
     * @param id variable identifier.
     * @param varType variable VAR_TYPE value.
     * @param catDesc variable CATDESC value.
     * @param notes variable notes
     * @param purpose VARIABLE_PURPOSE value.
     */
    public void mergeValue(
        String name, 
        long id, 
        String varType,
        String catDesc,
        String notes,
        String purpose) {

        setName(name);

        if (getId() < 0) {

            // only set with first value because it is from master cdf
            setId(id);
            setVarType(varType);
        }

        if (catDesc != null && !catDesc.trim().equals("") && 
            !catDesc.equals(".") && getCatDesc() == null) {

            setCatDesc(catDesc);
        }

        if (notes != null && !notes.trim().equals("") && 
            !notes.equals(".") && getNotes() == null) {

            setNotes(notes);
        }
        if (purpose != null && !purpose.trim().equals("") && 
            !purpose.equals(".") && getPurpose() == null) {

            setPurpose(purpose);
        }
    }


    /**
     * Gets the variable name.
     *
     * @return the name value
     * @see #setName
     */
    public String getName() {

        return name;
    }


    /**
     * Sets the variable name.
     *
     * @param value new name value
     * @see #getName
     */
    public void setName(String value) {

        name = value;
    }


    /**
     * Gets the id value.
     *
     * @return the id value.
     * @see #setId
     */
    public long getId() {

        return id;
    }


    /**
     * Sets the id value.
     *
     * @param value new id value.
     * @see #setId
     */
    public void setId(long value) {

        id = value;
    }


    /**
     * Gets the "VAR_TYPE" attribute value.
     *
     * @return the varType value
     * @see #setVarType
     */
    public String getVarType() {

        return varType;
    }


    /**
     * Sets the "VAR_TYPE" attribute value.
     *
     * @param value new name value
     * @see #getVarType
     */
    public void setVarType(String value) {

        varType = value;
    }


    /**
     * Gets the "CATDESC" attribute value.
     *
     * @return the catDesc value
     * @see #setCatDesc
     */
    public String getCatDesc() {

        return catDesc;
    }


    /**
     * Sets the "CATDESC" attribute value.
     *
     * @param value new name value
     * @see #getCatDesc
     */
    public void setCatDesc(String value) {

        catDesc = value;
    }


    /**
     * Gets the "VAR_NOTES" attribute value.
     *
     * @return the notes value
     * @see #setNotes
     */
    public String getNotes() {

        return notes;
    }


    /**
     * Sets the "VAR_NOTES" attribute value.
     *
     * @param value new notes value
     * @see #getNotes
     */
    public void setNotes(String value) {

        notes = value;
    }


    /**
     * Gets the "VARIABLE_PURPOSE" attribute value.
     *
     * @return the purpose value.
     * @see #setPurpose
     */
    public String getPurpose() {

        return purpose;
    }


    /**
     * Sets the "VARIABLE_PURPOSE" attribute value.
     *
     * @param value new purpose value.
     * @see #getPurpose
     */
    public void setPurpose(String value) {

        purpose = value;
    }
}
