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
 * $Id: DataTable.java,v 1.10 2022/03/24 10:38:36 btharris Exp $
 */

//$Id: DataTable.java,v 1.10 2022/03/24 10:38:36 btharris Exp $
package gsfc.spdf.gui;

import javax.swing.JTable;
import javax.swing.table.TableModel;
import javax.swing.DefaultCellEditor;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Component;
import java.text.NumberFormat;
import javax.swing.SwingConstants;
import gsfc.spdf.table.*;
import java.awt.Color;

/**
 * A Table which accepts arrays of primative or string values.
 *
 * <BR><BR>The DefaultDataTable provided in the JFC does not support arrays
 * of primatives.  The user must ensure that all primative values are
 * wrapped in the corresponding <code>java.lang.Number</code> class. This 
 * class takes care of this problem.
 *
 *
 * @see gsfc.spdf.table.DefaultDataTableModel
 *
 * @author Phil Williams
 * @version $Revision: 1.10 $
 *
 */
public class DataTable extends JTable {

    
    // Constructors

    /**
     * Construct a default DataTable.
     */
    public DataTable() {
   
	this(new DefaultDataTableModel());
        this.setShowGrid(true);
    }

    /**
     * Construct a DataTable using the given model.
     *
     * @param model A DefaultDataTableModel
     */
    public DataTable(DefaultDataTableModel model) {
	super(model);
	setUpFloat();
	setUpDouble();
	setUpByteEditor();
	setUpShortEditor();
	setUpIntegerEditor();
	setUpLongEditor();
    }

    /**
     * Construct a DataTable containing the given data.
     *
     * @param data the table data (must be an array).
     */
    public DataTable(Object data) {
	this(new DefaultDataTableModel(data));
    }

    /**
     * Construct a DataTable containing the given data using the specified
     * majority.
     *
     * @param data the table data (must be an array).
     * @param majority either DefaultDataTableModel.ROW_MAJOR or
     *      DefaultDataTableModel.COLUMN_MAJOR
     */
    public DataTable(Object data, int majority) {
	this(new DefaultDataTableModel(data, majority));
    }

    // Private methods to set up the editors for the Number objects

    private void setUpFloat() {
        //Set up the editor for the float cells.
        final FloatNumberField floatField = new FloatNumberField(0, 5);
        floatField.setHorizontalAlignment(FloatNumberField.RIGHT);

        DefaultCellEditor floatEditor = 
            new DefaultCellEditor(floatField) {
                //Override DefaultCellEditor's getCellEditorValue method
                //to return an Float, not a String:
                public Object getCellEditorValue() {
                    return floatField.getFloat();
                }
            };
       
        this.setDefaultEditor(Float.class, floatEditor);
        
        this.setDefaultRenderer(Float.class, new DefaultTableCellRenderer() {
            
             public Component getTableCellRendererComponent(
                        JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
            {
    
                Component renderer = super.getTableCellRendererComponent(
                                    table, value, isSelected, hasFocus, row, column);
                            
                if(value != null && value instanceof Float){
                                    
                    setHorizontalAlignment( SwingConstants.RIGHT );
                    FloatNumberField f = new FloatNumberField(((Float)value).floatValue(),column);         
                    value = f.getFloat(); 
                }
                return renderer;
            }
        });
                  
    }

    private void setUpDouble() {
        //Set up the editor for the double cells.
        final FloatNumberField floatField = new FloatNumberField(0, 5);
        floatField.setHorizontalAlignment(FloatNumberField.RIGHT);

        DefaultCellEditor doubleEditor = 
            new DefaultCellEditor(floatField) {
                //Override DefaultCellEditor's getCellEditorValue method
                //to return an Float, not a String:
                public Object getCellEditorValue() {
                    return floatField.getDouble();
                }
            };
            this.setDefaultEditor(Double.class, doubleEditor);
            
            this.setDefaultRenderer(Double.class, new DefaultTableCellRenderer() {
            
             public Component getTableCellRendererComponent(
                        JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
            {
    
                Component renderer = super.getTableCellRendererComponent(
                                    table, value, isSelected, hasFocus, row, column);
                            
                if(value != null && value instanceof Double){
                                    
                    setHorizontalAlignment( SwingConstants.RIGHT );
                    FloatNumberField f = new FloatNumberField(((Double)value).floatValue(),column);         
                    value = f.getDouble(); 
                }
                return renderer;
            }
        });        
    }

    private void setUpByteEditor() {
        //Set up the editor for the byte cells.
        final WholeNumberField wholeField = new WholeNumberField(0, 5);
        wholeField.setHorizontalAlignment(WholeNumberField.RIGHT);

        DefaultCellEditor byteEditor = 
            new DefaultCellEditor(wholeField) {
                //Override DefaultCellEditor's getCellEditorValue method
                //to return an Whole, not a String:
                public Object getCellEditorValue() {
                    return wholeField.getByte();
                }
            };
        this.setDefaultEditor(Byte.class, byteEditor);
    }

    private void setUpShortEditor() {
        //Set up the editor for the short cells.
        final WholeNumberField wholeField = new WholeNumberField(0, 5);
        wholeField.setHorizontalAlignment(WholeNumberField.RIGHT);

        DefaultCellEditor shortEditor = 
            new DefaultCellEditor(wholeField) {
                //Override DefaultCellEditor's getCellEditorValue method
                //to return an Whole, not a String:
                public Object getCellEditorValue() {
                    return wholeField.getShort();
                }
            };
        this.setDefaultEditor(Short.class, shortEditor);
    }

    private void setUpIntegerEditor() {
        //Set up the editor for the integer cells.
        final WholeNumberField wholeField = new WholeNumberField(0, 5);
        wholeField.setHorizontalAlignment(WholeNumberField.RIGHT);

        DefaultCellEditor integerEditor = 
            new DefaultCellEditor(wholeField) {
                //Override DefaultCellEditor's getCellEditorValue method
                //to return an Whole, not a String:
                public Object getCellEditorValue() {
                    return wholeField.getInteger();
                }
            };
        this.setDefaultEditor(Integer.class, integerEditor);
    }

    private void setUpLongEditor() {
        //Set up the editor for the long cells.
        final WholeNumberField wholeField = new WholeNumberField(0, 5);
        wholeField.setHorizontalAlignment(WholeNumberField.RIGHT);

        DefaultCellEditor longEditor = 
            new DefaultCellEditor(wholeField) {
                //Override DefaultCellEditor's getCellEditorValue method
                //to return an Whole, not a String:
                public Object getCellEditorValue() {
                    return wholeField.getLong();
                }
            };
        this.setDefaultEditor(Long.class, longEditor);
    }
    



} // DataTable
