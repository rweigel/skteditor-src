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
 * $Id: JLabeledDataTable.java,v 1.9 2022/08/02 11:41:48 btharris Exp $
 */

// $Id: JLabeledDataTable.java,v 1.9 2022/08/02 11:41:48 btharris Exp $

package gsfc.spdf.gui;

// Swing Imports
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import javax.swing.table.*;

import gsfc.spdf.gui.*;
import gsfc.spdf.table.*;

/**
 * A DataTable wrapped in a JScrollPane contained in  a JLabeledPanel.
 *
 * @author Phil Williams
 * @version $Revision: 1.9 $
 *
 * @see gsfc.spdf.gui.DataTable
 * @see gsfc.spdf.table.DefaultDataTableModel
 *
 */
public class JLabeledDataTable extends JLabeledPanel {
    private JScrollPane sp;
    private DataTable table;
    
    /**
     * Construct a JLabeledDataTable labeled with the label.
     *
     * @param label the label.
     */
    public JLabeledDataTable(String label) {
	super(label);
	table = new DataTable();
	table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	ListSelectionModel rowSM = table.getSelectionModel();
	rowSM.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	sp = new JScrollPane(table);
	add(sp);
    }

    /**
     * Wraps DataTable.setVisible.
     *
     * @see javax.swing.JTable#setVisible
     */
    public void setVisible(boolean v) {
	table.setVisible(v);
    }

    /**
     * Wraps DataTable.setModel.
     *
     * @param tm table model.
     * @see javax.swing.JTable#setModel
     */
    public void setModel(DefaultDataTableModel tm) {
	table.setModel(tm);
	sp.revalidate();
	sp.repaint();
    }

    /**
     * Wraps DataTable.getModel.
     *
     * @return table model.
     * @see javax.swing.JTable#getModel
     */
    public DefaultDataTableModel getModel() {
	return (DefaultDataTableModel)table.getModel();
    }

    /**
     * Get the scroll pane containing the table
     *
     * @return scroll pane.
     */
    public JScrollPane getScrollPane() {
	return sp;
    }

    /**
     * Get the table containing the table
     *
     * @return data table.
     */
    public DataTable getDataTable() {
	return table;
    }

    public void setEnabled(boolean enable) {
	super.setEnabled(enable);
	table.setEnabled(enable);
    }
    
} 
