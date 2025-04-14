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
 * Copyright (c) 2012-2024 United States Government as represented by 
 * the National Aeronautics and Space Administration. No copyright is 
 * claimed in the United States under Title 17, U.S.Code. All Other 
 * Rights Reserved.
 *
 * $Id: SparseRecordTypeComboBox.java,v 1.3 2024/10/25 18:37:55 btharris Exp $
 */

package gsfc.spdf.gui;


import javax.swing.JComboBox;

import gsfc.spdf.cdf.SparseRecordType;



/**
 * This class provides a JComboBox of sparse record type values.
 */
public class SparseRecordTypeComboBox 
    extends JComboBox<SparseRecordType> {

    /**
     * Class' serialVersionUID.
     */
    private static final long serialVersionUID = 7276185905368374020L;


    /**
     * Constructs a SparseRecordTypeComboBox.
     *
     */
    public SparseRecordTypeComboBox() {

        addItem(SparseRecordType.NONE);
        addItem(SparseRecordType.PAD);
        addItem(SparseRecordType.PREVIOUS);
    }
    

    /**
     * Sets the ComboBox value to the given sparse record type value.
     *
     * @param value sparse record type value that ComboBox is to have
     * @see #getSparseRecordType()
     */
    public void setSparseRecordType(SparseRecordType value) {

        setSelectedItem(value);
    }


    /**
     * Gets the ComboBox's value as a sparse record type value.
     *
     * @return currently selected ComboBox value as a sparse record 
     *             type value.
     * @see #setSparseRecordType(SparseRecordType)
     */
    public SparseRecordType getSparseRecordType() {

        return (SparseRecordType)getSelectedItem();
    }

}

