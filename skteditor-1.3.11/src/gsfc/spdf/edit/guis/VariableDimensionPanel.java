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
 * $Id: VariableDimensionPanel.java,v 1.19 2022/03/24 10:38:32 btharris Exp $
 */
package gsfc.spdf.edit.guis;


import java.awt.Color;
import java.awt.FlowLayout;
import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.border.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import gsfc.nssdc.cdf.CDFConstants;


/**
 * Panel to display and support editing of CDF variable dimension 
 * characteristics.
 * 
 * @author B. Harris
 * @version $Revision: 1.19 $
 */
public class VariableDimensionPanel extends JPanel 
    implements ChangeListener {

    /**
     * Spinner for the number of dimensions.
     */
    private JSpinner dimensionsSpinner = null;

    /**
     * Table model containing the variable's dimension information 
     * (sizes and variances).
     */
    protected CdfVariableDimensionTableModel tableModel = 
                                   new CdfVariableDimensionTableModel();

    /**
     * Table containing the variable's dimension information (sizes and 
     * variances).
     */
    protected JTable dimensionTable = new JTable(tableModel);


    protected final static Color enabledColor = Color.black;

    protected final static Color disabledColor = 
        new Color(142, 142, 142);

    protected TitledBorder border;

    /**
     * Creates a VariableDimensionPanel.
     */
    public VariableDimensionPanel() {

        border = new TitledBorder(new EtchedBorder(), 
                                  "Variable Dimensions");
        border.setTitleColor(enabledColor);
        setBorder(border);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        EmptyBorder emptyBorder = new EmptyBorder(0, 0, 0, 0);
                                        // an empty border to replace 
                                        // the default etched border on
                                        // LabeledComboBoxPanels and
                                        // LabeledTextFieldPanels

        JPanel dimensionsPanel = 
            new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel dimensionsLabel = new JLabel("Dimensions");
        dimensionsPanel.add(dimensionsLabel);
        SpinnerNumberModel dimensionsNumberModel =
            new SpinnerNumberModel(
/*
                    0, 0, CDFConstants.CDF_MAX_DIMS, 1);
CDF may support more but we only support dimension 6.
*/
                    0, 0, 6, 1);
        dimensionsSpinner = 
            new JSpinner(dimensionsNumberModel);
        dimensionsSpinner.addChangeListener(this);
        dimensionsSpinner.setToolTipText(
            "Variable dimensions excluding record-varying (time) " +
            "dimension");
        dimensionsPanel.add(dimensionsSpinner);
        add(dimensionsPanel);

        JScrollPane scrollPane = new JScrollPane(dimensionTable);
      
        add(scrollPane);
    }


    /**
     * Gets the number of dimensions from the panel.
     * 
     * @return number of dimensions
     */
    public int getDimension() {

        return ((Number)dimensionsSpinner.getValue()).intValue();
    }


    /**
     * Sets the number of dimensions.
     * 
     * @param dimension the number of dimensions
     */
    public void setDimension(int dimension) {

        dimensionsSpinner.setValue(dimension);
    }


    /**
     * Gets the sizes of all dimensions.
     * 
     * @return size of all dimensions (i'th element contains the size 
     *             of the i'th dimension).
     */
    public long[] getDimensionSizes() {

        long[] sizes;

        if (tableModel.getRowCount() == 0) {

            //
            // Although sizes = null may make more sense, it seems as
            // if the CDF API prefers a non-null value to ignore.
            //
            sizes = new long [] {1};
        }
        else {

            stopCellEditing();

            sizes = new long[tableModel.getRowCount()];

            for (int i = 0; i < sizes.length; i++) {

                sizes[i] = tableModel.getSize(i);
            };
        };

        return sizes;
    }


    /**
     * Sets the size of all dimensions.
     * 
     * @param sizes the size of all dimensions (i'th element contains the 
     *              size of the i'th dimension)
     */
    public void setDimensionSizes(long[] sizes) {

        for (int i = 0; i < sizes.length; i++) {

            tableModel.setSize(i, (int)sizes[i]);
        };
    }


    /**
     * Gets the variance value of all dimensions.
     * 
     * @return variance value of all dimensions (i'th element contains the 
     *         variance of the i'th dimension)
     */
    public boolean[] getDimensionVariances() {

        stopCellEditing();

        boolean[] variances = new boolean[tableModel.getRowCount()];

        for (int i = 0; i < variances.length; i++) {

            variances[i] = tableModel.getVariance(i);
        };

        return variances;
    }


    /**
     * Sets the variance of all dimensions.
     * 
     * @param variances the variance of all dimensions (i'th element contains 
     *                  the variance of the i'th dimension)
     */
    public void setDimensionVariances(boolean[] variances) {

        for (int i = 0; i < variances.length; i++) {

            tableModel.setVariance(i, variances[i]);
        };
    }


    /**
     * Enables whether or not this panel is enabled.
     *
     * @param enabled true enables this panel, false disables it
     */
    public void setEnabled(boolean enabled) {

        border.setTitleColor(enabled ? enabledColor : disabledColor);
        setDimensionEnabled(enabled);
        setTableEnabled(enabled);
    }


    /**
     * Enables whether or not the dimension field is enabled.
     *
     * @param enabled true enables the dimension field, false disables it
     */
    public void setDimensionEnabled(boolean enabled) {

        dimensionsSpinner.setEnabled(enabled);
    }


    /**
     * Enables whether or not the dimension sizes and variance table is 
     * enabled.
     *
     * @param enabled true enables the table, false disables it
     */
    public void setTableEnabled(boolean enabled) {

        dimensionTable.setEnabled(enabled);
        tableModel.setEditable(enabled);  // for Mac OS 8.x
    }


    /**
     * Stops any editing of cell values in the dimensionTable and to 
     * accept any partially entered value.
     */
    protected void stopCellEditing() {

        TableCellEditor editor = dimensionTable.getCellEditor();
       
        if (editor != null) {

            editor.stopCellEditing();
        };
    }


    /**
     * Called when the dimensionsSpinner value changes.
     *
     * @param e value change event.
     */
    public void stateChanged(ChangeEvent e) {

        updateDimension();
    }


    /**
     * Updates the table model's dimension based upon the dimension 
     * field's value.
     */
    protected void updateDimension() {
        
        stopCellEditing();

        tableModel.setDimension(getDimension());
    }
}
