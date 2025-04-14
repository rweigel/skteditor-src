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
 * $Id: CdfDataTypeComboBox.java,v 1.15 2024/10/25 18:37:55 btharris Exp $
 */

package gsfc.spdf.gui;


import javax.swing.JComboBox;

import gsfc.nssdc.cdf.CDF;
import gsfc.nssdc.cdf.CDFException;
import gsfc.nssdc.cdf.util.CDFUtils;



/**
 * This class provides a JComboBox of CDF datatype values.
 */
public class CdfDataTypeComboBox extends JComboBox<Object> {

    /**
     * Class' serialVersionUID.
     */
    private static final long serialVersionUID = 7276185905368374020L;


    /**
     * Constructs a CdfDataTypeComboBox which provides a ComboBox of
     * CDF datatype values.  The ComboBox does not initially offer all
     * CDF datatypes.  For example, redundant types such as CDF_FLOAT 
     * (equivalent to CDF_REAL4) are not initally shown.  However, if
     * an attempt is made to select one of the hidden types
     * (with {@link #setDataType(long)}), the datatype will be added 
     * to the ComboBox.
     *
     */
    public CdfDataTypeComboBox() {

        // Most people do not realize this is signed.  Eliminating 
        // it forces the user to choose INT1 or UINT1.
        // addItem(CDFUtils.getStringDataType(CDF.CDF_BYTE)); 
        addItem(CDFUtils.getStringDataType(CDF.CDF_INT1));
        addItem(CDFUtils.getStringDataType(CDF.CDF_UINT1));
        addItem(CDFUtils.getStringDataType(CDF.CDF_INT2));
        addItem(CDFUtils.getStringDataType(CDF.CDF_UINT2));
        addItem(CDFUtils.getStringDataType(CDF.CDF_INT4));
        addItem(CDFUtils.getStringDataType(CDF.CDF_UINT4));
        addItem(CDFUtils.getStringDataType(CDF.CDF_INT8));
        // addItem(CDFUtils.getStringDataType(CDF.CDF_FLOAT));
        addItem(CDFUtils.getStringDataType(CDF.CDF_REAL4));
        // addItem(CDFUtils.getStringDataType(CDF.CDF_DOUBLE));
        addItem(CDFUtils.getStringDataType(CDF.CDF_REAL8));
        addItem(CDFUtils.getStringDataType(CDF.CDF_CHAR));
        // addItem(CDFUtils.getStringDataType(CDF.CDF_UCHAR));
        addItem(CDFUtils.getStringDataType(CDF.CDF_TIME_TT2000));
        addItem(CDFUtils.getStringDataType(CDF.CDF_EPOCH));
        addItem(CDFUtils.getStringDataType(CDF.CDF_EPOCH16));
             
        // Although we default to less than the full set of CDF
        // data type to make things simpler and to discourage the
        // the use of some data types over others, we'll add any 
        // of the missing datatypes if we encounter them (see 
        // setDataType below).
    }
    

    /**
     * Sets the combobox to not have a CHAR data type (but all others.
     */
    public void setNoCharTypes() {

        removeItem(CDFUtils.getStringDataType(CDF.CDF_CHAR));
    }


    /**
     * Sets the combobox to only have numeric data types (no char
     * and no time datatypes).
     */
    public void setNumericOnlyTypes() {

        removeItem(CDFUtils.getStringDataType(CDF.CDF_CHAR));
        removeItem(CDFUtils.getStringDataType(CDF.CDF_EPOCH));
        removeItem(CDFUtils.getStringDataType(CDF.CDF_EPOCH16));
        removeItem(CDFUtils.getStringDataType(CDF.CDF_TIME_TT2000));
    }


    public void CdfVersionAdjust(String cdfVersion){

        if(cdfVersion != null && cdfVersion.startsWith("2")) {
            
            removeItem(CDFUtils.getStringDataType(CDF.CDF_EPOCH16));
            removeItem(CDFUtils.getStringDataType(CDF.CDF_TIME_TT2000));
            removeItem(CDFUtils.getStringDataType(CDF.CDF_INT8));
        }
    }
    

    /**
     * Sets the ComboBox list to only epoch, epoch16, and time_tt2000
     * when creating an epoch variable for version 3 or above.
     *
     * @param cdfVersion 2 or above
     */    
    public void createEpochVarAdjust (String cdfVersion){
        
        if(cdfVersion != null && !cdfVersion.startsWith("2")){
            
            removeAllItems();
            addItem("CDF_TIME_TT2000");
            addItem("CDF_EPOCH");
            addItem("CDF_EPOCH16");
        }        
    }


    /**
     * Sets the ComboBox value to the given CDF datatype value.  If
     * the given type is not currently displayed in the ComboBox, it
     * will be added and then selected.
     *
     * @param dataType CDF datatype value that ComboBox is to have
     */
    public void setDataType(long dataType) {

        String dataTypeStr = CDFUtils.getStringDataType(dataType);
                                       // string representation of 
                                       // dataType
        if (!isItem(dataTypeStr)) {

            //
            // Although we default to less than the full set of CDF
            // data type to make things simpler and to discourage the
            // the use of some data type, if we encounter one of these
            // other data types, me must support them.
            //
            addItem(dataTypeStr);
        }

        setSelectedItem(dataTypeStr);
    }


    /**
     * Gets the ComboBox's value as a CDF datatype value.
     *
     * @return currently selected ComboBox value as a CDF datatype 
     *             value.
     */
    public long getDataType() {

        String value = (String) getSelectedItem();

        long type = CDFUtils.getDataTypeValue(value);

        if (type != -1) {

            return type;
        }
        else {

            return CDF.CDF_CHAR;
        }
    }


    /**
     * Determines if the given value is a current item of this 
     * ComboBox.
     *
     * @param value value to check for.
     * @return true if the given value is a current item of this
     *             ComboBox.  Otherwise, false.
     */
    private boolean isItem(String value) {

        for (int i = 0; i < getItemCount(); i++) {

            if (getItemAt(i).equals(value)) {

                return true;
            }
        }
        return false;
    }
}

