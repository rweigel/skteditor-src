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
 * $Id: AbstractDataTableModel.java,v 1.7 2022/08/01 18:48:48 btharris Exp $
 */


//$Id: AbstractDataTableModel.java,v 1.7 2022/08/01 18:48:48 btharris Exp $
package gsfc.spdf.table;

import javax.swing.table.AbstractTableModel;

/**
 * Provides the framework and helper methods for displaying arrays of
 * primatives in a table.
 *
 * @author Phil Williams
 * @version $Revision: 1.7 $
 */
public abstract class AbstractDataTableModel extends AbstractTableModel {

    /**
     * The type of the array.
     *
     * Either Z, C, B, S, I, J, F, D or L
     */
    protected char _type;

    // Abstract methods

    /**
     * Set the data in this model
     *
     * @param data model's data.
     */
    public abstract void setData(Object data);

    /**
     * Get the data object out of this model.
     *
     * @return model's data.
     */
    public abstract Object getData();


    ////////////////////////////////////////////////////
    //                                                //
    //             Private Methods                    //
    //                                                //
    ////////////////////////////////////////////////////

    /**
     * Create a new 1D array of the correct type
     *
     * @param length number of elements in array.
     * @return new array.
     */
    protected Object newArray(int length) {
	switch(_type) {
	case 'B':
	    return new byte[length];
	case 'S':
	    return new short[length];
	case 'I':
	    return new int[length];
	case 'J':
	    return new long[length];
	case 'F':
	    return new float[length];
	case 'D':
	    return new double[length];
	case 'Z':
	    return new boolean[length];
	case 'L':
	    return new String[length];
	default:
	    return new String[length];
	}
    }

    /**
     * Create a new data object of the correct type
     *
     * @param x size of first dimension.
     * @param y size of second dimension.
     * @return new data object of given size.
     */
    protected Object newDataObject(int x, int y) {
	switch(_type) {
	case 'B':
	    return new byte[x][y];
	case 'S':
	    return new short[x][y];
	case 'I':
	    return new int[x][y];
	case 'J':
	    return new long[x][y];
	case 'F':
            return new float[x][y];
	case 'D':
	    return new double[x][y];
	case 'Z':
	    return new boolean[x][y];
	case 'L':
	    return new String[x][y];
	default:
	    return new String[x][y];
	}
    }
    
} // AbtractDataTableModel
