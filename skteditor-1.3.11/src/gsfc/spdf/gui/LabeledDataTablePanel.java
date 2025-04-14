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
 * $Id: LabeledDataTablePanel.java,v 1.17 2022/03/24 10:38:36 btharris Exp $
 */

package gsfc.spdf.gui;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;

import gsfc.spdf.gui.AbstractLabeledInputComponent;
import gsfc.spdf.table.DefaultDataTableModel;


/**
 * One or more DataTables with a single JLabel on a JPanel.
 * 
 * @author B. Harris
 * @version $Revision: 1.17 $
 */
public class LabeledDataTablePanel 
    extends AbstractLabeledInputComponent {


    /**
     * the DataTables contained on this component
     */
    protected DataTable[] dataTables;


    /**
     * the number of DataTable on this component
     */
    protected int numFields;


    /**
     * Construct a LabeledDataTablePanel with the given label and a
     * single DataTable.
     *
     * A BoxLayout (Y_AXIS) is set as the default LayoutManager.  The
     * border is an EtchedBorder.
     *
     * @param label The label field value for this component
     */
    public LabeledDataTablePanel(String label) {

        this(label, 1);
    }


    /**
     * Construct a LabeledDataTablePanel with the given label and border
     * and a single DataTables.
     *
     * A BoxLayout (Y_AXIS) is set as the default LayoutManager.  
     *
     * @param label The label field value for this component
     * @param border The border to use for the titled border
     */
    public LabeledDataTablePanel(String label, Border border) {

        this(label, border, 1);
    }


    /**
     * Construct a LabeledDataTablePanel with the given label and number
     * of DataTables.
     *
     * A BoxLayout (Y_AXIS) is set as the default LayoutManager.  The
     * border is an EtchedBorder.
     *
     * @param label The label field value for this component
     * @param numFields The number of DataTables to construct on this panel
     */
    public LabeledDataTablePanel(String label, int numFields) {

        this(label, new EtchedBorder(), numFields);
    }


    /**
     * Construct a LabeledDataTablePanel with the given label, border,
     * and number of DataTables.
     *
     * A BoxLayout (Y_AXIS) is set as the default LayoutManager.  
     *
     * @param label The label field value for this component
     * @param border The border to use for the titled border
     * @param numFields The number of DataTables to construct on this panel
     */
    public LabeledDataTablePanel(String label, Border border, int numFields) {

        super(label, border);
         
        BoxLayout layoutManager = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(layoutManager);
        this.numFields = numFields;

        dataTables = new DataTable[numFields];

        for (int i = 0; i < numFields; i++) {

            dataTables[i] = new DataTable();
            dataTables[i].setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            DefaultDataTableModel model = (DefaultDataTableModel)
                                              dataTables[i].getModel();
            model.setMajority(DefaultDataTableModel.COLUMN_MAJOR);
            dataTables[i].setTableHeader(null);
            JScrollPane scrollPane = new JScrollPane(dataTables[i]);

            JButton fillButton = new JButton("Fill all with selected value");
            fillButton.addActionListener(new FillActionListener(dataTables[i]));
            fillButton.setAlignmentX(Component.RIGHT_ALIGNMENT);

            add(fillButton);
            add(Box.createRigidArea(new Dimension(0, 5)));
            add(scrollPane);
        };
     
    }


    /**
     * @deprecated Use getInputComponentValue()
     */
    public Object get() {

        if (numFields == 1) {

            return getInputComponentValue(0);
        }

            Vector fieldValues = new Vector(numFields);

            for (int i = 0; i < numFields; i++) {

                fieldValues.addElement(getInputComponentValue(i));
            };

            return fieldValues;
    }


    /**
     * @deprecated Use setInputComponentValue()
     */
    public void set(Object value) {

        if (value instanceof Vector) {

            Vector values = (Vector) value;

            for (int i = 0; i < numFields && i < values.size(); i++) {

                setInputComponentValue(i, values.elementAt(i));
            };
        }
        else {  // for backward compatibility

            setInputComponentValue(0, value);
        };
    }


    public void reset() {

        for (int i = 0; i < numFields; i++) {

            ((DefaultDataTableModel)dataTables[i].getModel()).setData(null);
        };
    }
    
    public void setTablesEnabled(boolean enabled){
        
        for(int i = 0; i < dataTables.length; i++){
            
            dataTables[i].setEnabled(enabled);
        }
        
        setEnabled(enabled);                
    }


    public void addItem(Object value) {

        DefaultDataTableModel dataModel = null;

        if (value instanceof Vector) {

            Vector valueVector = (Vector)value;

            for (int i = 0; i < numFields && i < valueVector.size(); i++) {

                dataModel = (DefaultDataTableModel)dataTables[i].getModel();
                dataModel.setData(valueVector.elementAt(i));
            };
        }
        else {

            dataModel = (DefaultDataTableModel)dataTables[0].getModel();
            dataModel.setData(value);
        };
    }


/*
    public void setEditable(boolean enabled) {

        for (int i = 0; i < numFields; i++) {

//            dataTables[i].setEditable(enabled);
        };
    }
*/
 


    public int getInputComponentCount() {

        return numFields;
    }


    public Object getInputComponentValue(int index) {
 
        if (dataTables[index].isEditing()) {

            dataTables[index].getCellEditor().stopCellEditing();
        };

        return ((DefaultDataTableModel)dataTables[index].getModel()).getData();
    }


    public void setInputComponentValue(int index, Object value) {

        ((DefaultDataTableModel)dataTables[index].getModel()).setData(value);
        dataTables[index].sizeColumnsToFit(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }


    public void setInputComponentToolTipText(int index, String text) {

        dataTables[index].setToolTipText(text);
    }


    public void createEmptyTable(int index, int rows, int cols, 
                                 Class dataClass) {

        ((DefaultDataTableModel)dataTables[index].getModel()).createEmptyTable(
                                              rows, cols, dataClass);
        dataTables[index].sizeColumnsToFit(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }


    public void setNumRows(int index, int newSize) {

        ((DefaultDataTableModel)dataTables[index].getModel()).setNumRows(
                                                                   newSize);
        dataTables[index].sizeColumnsToFit(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }


    public int getEditingColumn(int index) {

        return dataTables[index].getEditingColumn();
    }


    public int getEditingRow(int index) {

        return dataTables[index].getEditingRow();
    }


    public TableCellEditor getCellEditor(int index, int row, int col) {

        return dataTables[index].getCellEditor(row, col);
    }

 
    public void setVisible(int index, boolean value) {

        dataTables[index].setVisible(value);
    }


    /**
     * "Fill" button action listener.
     */
    private static class FillActionListener
        implements ActionListener {

        /**
         * The table whose values are to be filled with the selected 
         * value.
         */
        private DataTable table = null;


        /**
         * Creates a FillActionListener for the given table.
         *
         * @param table the table whose values are to be filled
         */
        public FillActionListener(DataTable table) {

            this.table = table;
        }


        /**
         * Invoked when the "Fill" button is selected.  Gets the value
         * from the selected row and fills all the other values in the
         * column to that of the selected row.
         *
         * @param event action event
         */
        public void actionPerformed(ActionEvent event) {

            if (table.getRowCount() == 1) {

                table.setRowSelectionInterval(0, 0);
            }

            int row = table.getSelectedRow();
                                       // selected row

            if (row < 0) {

                JOptionPane.showMessageDialog(null, 
                    "You must select the value that is to be filled\n"+
                    "into all other cells.",
                    "Missing value selection", JOptionPane.ERROR_MESSAGE);
            }
            else {

                if (table.isEditing()) {

                    TableCellEditor editor = table.getCellEditor(row, 0);
                                       // cell editor
                    editor.stopCellEditing();
                }
                Object selectedValue = table.getValueAt(row, 0);
                                       // value from selected row
                int rows = table.getRowCount();
                                       // number of rows in table

                for (int i = 0; i < rows; i++) {

                    table.setValueAt(selectedValue, i, 0);
                    //
                    // I don't know why the row has to be selected for the
                    // new value to be shown.  There might be a better way
                    // of doing this.
                    //
                    table.setRowSelectionInterval(i, i);
                }
                table.setRowSelectionInterval(row, row);
            }
        }
    }
}
