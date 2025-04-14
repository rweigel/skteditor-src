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
 * $Id: LabeledComponentTablePanel.java,v 1.6 2022/03/24 10:38:32 btharris Exp $
 */
package gsfc.spdf.edit.guis;

import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;

import gsfc.spdf.gui.LabeledDataTablePanel;


/**
 * Panel containing a label and a table of editable COMPONENT_x values.
 * 
 * @author B. Harris
 * @version $Revision: 1.6 $
 */
public class LabeledComponentTablePanel extends LabeledDataTablePanel {

    /**
     * Combo box with a data model containing the choices for COMPONENT_x values.
     * The combo box is used to create the component table cell editor.
     */
    protected JComboBox varComboBox = null;

    /**
     * Creates a LabeledComponentTablePanel.
     */
    public LabeledComponentTablePanel() {

        super("Components", new EmptyBorder(0, 0, 0, 0), 1);

        varComboBox = new JComboBox();
    }


    /**
     * Sets the COMPONENT_x values available for selection.
     * 
     * @param choices values that are to appear a choices for COMPONENT_x values
     */
    public void setChoices(Vector choices) {

        varComboBox.setModel(new DefaultComboBoxModel(choices));

        dataTables[0].getColumnModel().getColumn(0).setCellEditor(
                                         new DefaultCellEditor(varComboBox));
    }

}

