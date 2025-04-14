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
 * $Id: CdfVariableDimensionTableModel.java,v 1.11 2022/03/24 10:38:32 btharris Exp $
 */
package gsfc.spdf.edit.guis;


import java.util.Vector;
import java.lang.Boolean;
import java.lang.Integer;

import javax.swing.table.AbstractTableModel;



/**
 * This class implements an AbstractTableModel containing CDF variable
 * dimension information.
 * 
 * @author B. Harris
 * @version $Revision: 1.11 $
 */
public class CdfVariableDimensionTableModel extends AbstractTableModel {


    /**
     * number of dimensions contained in this mode.
     */
    protected Vector dimension = new Vector();

    /**
     * contains the size of each dimension (indexed by dimension)
     */
    protected Vector sizes = new Vector();

    /**
     * variance of each dimension (indexed by dimension)
     */
    protected Vector variances = new Vector();

    /**
     * indicates whether the overall (all columns) table is editable
     */
    protected boolean editable = true;

    /**
     * names of columns
     */
    private final static String[] columnNames = {
        "Dimension", " Size", "Variance"
    };


    /**
     * Creates a table model containing CDF variable dimension 
     * information.
     */
    public CdfVariableDimensionTableModel() {

    }

    /**
     * Creates a table model containing CDF variable dimension 
     * information for a variable of the given dimension.
     * 
     * @param dimension number of dimensions
     */
    public CdfVariableDimensionTableModel(int dimension) {

        setDimension(dimension);
    }

    /**
     * Sets the number of dimensions to be represented in the model.
     * 
     * @param dimension number of dimensions to be represented
     */
    public void setDimension(int dimension) {

        int oldDimension = this.dimension.size();

        this.dimension = new Vector(dimension);
        sizes = new Vector(dimension);
        variances = new Vector(dimension);

        if (oldDimension > 0) {

            fireTableRowsDeleted(0, oldDimension - 1);
        };

        for (int i = 0; i < dimension; i++) {

            this.dimension.addElement(new Integer(i));
            sizes.addElement(new Integer(2));
            variances.addElement(Boolean.valueOf(true));
        };

        if (dimension > 0) {

            fireTableRowsInserted(0, dimension - 1);
        };
    }


    /**
     * Gets the number of dimensions represented in the model.
     * 
     * @return number of dimensions represented in the model
     */
    public int getDimension() {

        return dimension.size();
    }


    /**
     * Gets the size of the specified dimension.
     * 
     * @param dimension specifies the dimension whose size is to be retured
     * @return size (number of elements) of the specified dimension
     */
    public int getSize(int dimension) {

        return ((Integer)sizes.elementAt(dimension)).intValue();
    }

 
    /**
     * Sets the size of the specified dimension.
     * 
     * @param dimension specifies the dimension
     * @param size size (number of elements) of the specified dimension
     */
    public void setSize(int dimension, int size) {

        setValueAt(Integer.toString(size), dimension, 1);
    }


    /**
     * Gets the variance of the specified dimension.
     * 
     * @param dimension dimension whose variance is to be returned
     * @return variance of the specified dimension
     */
    public boolean getVariance(int dimension) {

        return ((Boolean)variances.elementAt(dimension)).booleanValue();
    }


    /**
     * Sets the variance of the specified dimension.
     * 
     * @param dimension dimension whose variance is to be set
     * @param value variance value
     */
    public void setVariance(int dimension, boolean value) {

        setValueAt(Boolean.valueOf(value), dimension, 2);
    }

    /**
     * Sets whether the overall table model is editable or not.  This method
     * exists to overcome the problem that a disabled JTable is still editable
     * on some platforms.
     * 
     * @param editable indicates whether the contents of this model is editable
     */
    public void setEditable(boolean editable) {

        this.editable = editable;
    }


    public int getColumnCount() {

        return columnNames.length;
    }

    public int getRowCount() {

        return sizes.size();
    }

    public String getColumnName(int col) {

        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {

        switch (col) {

        case 0:

            return dimension.elementAt(row);

        case 1:

            return sizes.elementAt(row);

        case 2:

            return variances.elementAt(row);
        };

        return null;
    }


    public Class getColumnClass(int col) {

        return getValueAt(0, col).getClass();
    }


    public boolean isCellEditable(int row, int col) {

        if (!editable) {

            return false;
        };

        if (col > 0) {

            return true;
        }
        return false;
 
    }


    public void setValueAt(Object value, int row, int col) {

        if (sizes.isEmpty()) {

            return;
        };

        switch (col) {

        case 1:

            if (value instanceof String) {  // Java version < 1.3

                sizes.setElementAt(new Integer((String)value), row);
            }
            else {

                sizes.setElementAt(value, row);
                
            };
            break;
        
        case 2:

            variances.setElementAt(value, row);
            break;
        };

        fireTableCellUpdated(row, col);
    }
}
