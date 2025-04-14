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
 * $Id: LabeledTable.java,v 1.6 2022/03/24 10:38:32 btharris Exp $
 */
package gsfc.spdf.edit.guis;

// Swing Imports
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import javax.swing.table.*;

import gsfc.spdf.gui.*;

/**
 * <STRONG>Obsolete</STRONG>.  Use gsfc.spdf.gui.JLabeledDataTable instead.
 */ 
public class LabeledTable extends JLabeledPanel {
    public JScrollPane sp;
    public DataTable table;
    public TableModel dm;
    
    public LabeledTable(String label, TableModel dm) {
	super(label);
	this.dm = dm;
	table = new DataTable();
	table.setModel(dm);
	ListSelectionModel rowSM = table.getSelectionModel();
	rowSM.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	
	sp = new JScrollPane(table);
	add(sp);
    }
} 
