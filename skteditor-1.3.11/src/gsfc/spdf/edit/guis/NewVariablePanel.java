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
 * $Id: NewVariablePanel.java,v 1.26 2022/03/24 10:38:32 btharris Exp $
 */
package gsfc.spdf.edit.guis;


import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.*;


import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;


import gsfc.nssdc.cdf.CDF;
import gsfc.nssdc.cdf.CDFException;
import gsfc.nssdc.cdf.Variable;
import gsfc.nssdc.cdf.util.CDFTT2000;
import gsfc.nssdc.cdf.util.Epoch;
import gsfc.nssdc.cdf.util.Epoch16;
  
import gsfc.spdf.gui.LabeledTextFieldPanel;
import gsfc.spdf.gui.LabeledComboBoxPanel;
import gsfc.spdf.gui.LabeledCdfDataTypeCBPanel;
import gsfc.spdf.gui.LabeledSparseRecordTypeCBPanel;
import gsfc.spdf.gui.WholeNumberField;

import gsfc.spdf.cdf.PadValue;
import gsfc.spdf.cdf.SparseRecordType;
import gsfc.spdf.cdf.Value;


/**
 * Panel for displaying and editing the characteristics of a new CDF 
 * variable.
 *
 * @author B. Harris
 * @version $Revision: 1.26 $
 */
public class NewVariablePanel 
    extends JPanel 
    implements ItemListener {

    /**
     * Variable's name field.
     */
    protected LabeledTextFieldPanel name = null;

    /**
     * Variable's data type combo box.
     */
    protected LabeledCdfDataTypeCBPanel type = null;

    /**
     * Variable's "number of elements" field.
     */
    protected LabeledTextFieldPanel elements = null;

    /**
     * Variable's record variance check box.
     */
    protected JCheckBox recordVariance = 
                                new JCheckBox("Record Variance (Time)");

    /**
     * Variable's compression check box.
     */
    protected JCheckBox compression = new JCheckBox("Compress Data");


    /**
     * Varible's sparse record type check box.
     */
    protected LabeledSparseRecordTypeCBPanel sparseRecordType = null;

    /**
     * Variable's pad value field.
     */
    protected LabeledTextFieldPanel padValue = null;

    /**
     * Variable's dimension information panel.
     */
    protected VariableDimensionPanel dimensions = null;


    /**
     * Creates a NewVariablePanel.
     *
     * @param cdf CDF that new variable is to be contained in
     * @param name variable's initial name
     * @param dataType variable's initial data type
     * @param numElements variable's number of elements
     * @param recordVariance variable's record variance
     * @param dimensions variable's dimensionality
     * @param dimensionSizes size of variable's dimensions
     * @param dimensionVariances variances of variable's dimensions
     */
    public NewVariablePanel(CDF cdf, String name, long dataType,
                            long numElements, boolean recordVariance,
                            long dimensions, long[] dimensionSizes, 
                            long[] dimensionVariances) {

        this(cdf);

        if (name.contains("_DEPEND_")) {

//            type.setNumericOnlyTypes();
            type.setNoCharTypes();
        }
        setVariableName(name);
        setDataType(dataType);
        setNumberOfElements((int)numElements);
        setRecordVariance(recordVariance);
        setDimension((int)dimensions);
        setDimensionSizes(dimensionSizes);
        setDimensionVariances(dimensionVariances);
    }


    /**
     * Creates a NewVariablePanel.
     *
     * @param cdf CDF that new variable is to be contained in
     */
    public NewVariablePanel(CDF cdf) {

        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(2,2,2,2);
	
        name = new LabeledTextFieldPanel(
                       "Name", new EmptyBorder(0, 0, 0, 0), 1);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbl.setConstraints(name, gbc);
        add(name);
        

        type = new LabeledCdfDataTypeCBPanel(
                       "Data Type", new EmptyBorder(0, 0, 0, 0));
        type.CdfVersionAdjust(cdf.getVersion());
        type.addItemListener(this);
        type.setInputComponentToolTipText(0,
            "<html><table>" +
            "<tr><td>CDF_INT1</td>" +
                "<td>signed 8-bit integer</td></tr>" +
            "<tr><td>CDF_UINT1</td>" +
                "<td>unsigned 8-bit integer</td></tr>" +
            "<tr><td>CDF_INT2</td>" +
                "<td>signed 16-bit integer</td></tr>" +
            "<tr><td>CDF_UINT2</td>" +
                "<td>unsigned 16-bit integer</td></tr>" +
            "<tr><td>CDF_INT4</td>" +
                "<td>signed 32-bit integer</td></tr>" +
            "<tr><td>CDF_UINT4</td>" +
                "<td>unsigned 32-bit integer</td></tr>" +
            "<tr><td>CDF_UINT8</td>" +
                "<td>unsigned 64-bit integer</td></tr>" +
            "<tr><td>CDF_REAL4</td>" +
                "<td>32-bit floating point</td></tr>" +
            "<tr><td>CDF_REAL8</td>" +
                "<td>64-bit floating point</td></tr>" +
            "<tr><td>CDF_CHAR</td>" +
                "<td>fixed length ASCII string</td></tr>" +
            "<tr><td>CDF_TIME_TT2000</td>" +
                "<td>Terr. Time in ns from 2000AD</td></tr>" +
            "<tr><td>CDF_EPOCH</td>" +
                "<td>time in ms from 0AD</td></tr>" +
            "<tr><td>CDF_EPOCH16</td>" +
                "<td>time in ps from 0AD</td></tr>" +
            "</table></html>");

        gbc.ipadx = -50;
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        gbl.setConstraints(type, gbc);
        add(type);

        WholeNumberField elementsField = new WholeNumberField(0, 8);
        elements = new LabeledTextFieldPanel("Num Chars", 
                           new EmptyBorder(0, 0, 0, 0), 1, 
                           new JTextField[] {elementsField});
        elements.setToolTipText("Number of characters");
        gbc.ipadx = 0;
        gbc.insets = new Insets(2,25,2,2);
	    
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbl.setConstraints(elements, gbc);
        add(elements);
        
        recordVariance.addItemListener(this);
        gbc.insets = new Insets(2,2,2,2);
        gbl.setConstraints(recordVariance, gbc);
        add(recordVariance);

        gbc.insets = new Insets(2,2,2,2);
        gbl.setConstraints(compression, gbc);
        add(compression);

        dimensions = new VariableDimensionPanel();
        dimensions.setMinimumSize(new Dimension(50, 150));
        gbl.setConstraints(dimensions, gbc);
        add(dimensions);

        JPanel sparsePadPanel = new JPanel();
        sparsePadPanel.setLayout(
          new BoxLayout(sparsePadPanel, BoxLayout.X_AXIS));

        sparseRecordType = 
            new LabeledSparseRecordTypeCBPanel(
                    "Sparse Record Type", new EmptyBorder(0, 0, 0, 0));
        sparseRecordType.setMinimumSize(new Dimension(125, 50));
        sparseRecordType.setMaximumSize(new Dimension(125, 50));
        gbl.setConstraints(sparseRecordType, gbc);
        sparsePadPanel.add(sparseRecordType);
        sparseRecordTypeEnabled(false);

        padValue = new LabeledTextFieldPanel("Pad Value", 
                           new EmptyBorder(0, 0, 0, 0), 1);
        padValue.setMinimumSize(new Dimension(180, 50));
        padValue.setMaximumSize(new Dimension(180, 50));
        padValue.setToolTipText("Default value is recommended");
        sparsePadPanel.add(padValue);

        gbl.setConstraints(sparsePadPanel, gbc);
        add(sparsePadPanel);

        setDataType(CDF.CDF_REAL4);
    }


    /**
     * Gets the name of the new variable.
     *
     * @return name of new variable
     */
    public String getVariableName() {

        Object value = name.getInputComponentValue(0);

        if (value instanceof String) {

            return (String)value;
        }

        return null;
    }


    /**
     * Sets the name of the variable.
     *
     * @param name new name of variable
     */
    public void setVariableName(String name) {

        this.name.setInputComponentValue(0, name);
    }


    /**
     * Enables whether or not the variable name field is enabled.
     *
     * @param value true enables the field, false disables it
     */
    public void setVariableNameEnabled(boolean value) {

        name.setEnabled(value);
    }


    /**
     * Gets the data type of the new variable.
     *
     * @return data type of new variable
     */
    public long getDataType() {

        return type.getDataType(0);
    }


    /**
     * Sets the data type of the variable.
     *
     * @param dataType new data type value
     */
    public void setDataType(long dataType) {

        type.setDataType(0, dataType);
    }


    /**
     * Enables whether or not the variable data type field is enabled.
     *
     * @param enabled true enables the field, false disables it
     */
    public void setDataTypeEnabled(boolean enabled) {

        type.setEnabled(enabled);
    }


    /**
     * set the datatype to be epoch or epoch16 only when creating
     * an epoch variable.
     *
     * @param cdfVersion defines the cdf version used (2 or greater)
     */
    public void createEpochVarAdjust(String cdfVersion) {

        type.createEpochVarAdjust(cdfVersion);
    }


    /**
     * Gets the "number of elements" of the variable.
     *
     * @return number of elements of the variable
     */
    public int getNumberOfElements() {

        int numElements = 0;

        String elementsStr = (String)elements.getInputComponentValue(0);

        if (!elementsStr.equals("")) {

            try {

                numElements = Integer.parseInt(elementsStr);
            }
            catch(NumberFormatException e) {

                // shouldn't happen with the WholeNumberField document 
                // model we are using so ignore it and use initial 
                // value if it ever does

                e.printStackTrace();
            };
        };

        return numElements;
    }


    /**
     * Sets the "number of elements" of the variable.
     *
     * @param numberOfElements the "number of elements" characteristic 
     *            of a CDF variable.
     */
    public void setNumberOfElements(int numberOfElements) {

        elements.setInputComponentValue(
            0, Integer.toString(numberOfElements));
    }


    /**
     * Enables whether or not the variable "number of elements" field 
     * is enabled.
     *
     * @param enabled true enables the field, false disables it
     */
    public void setNumberOfElementsEnabled(boolean enabled) {

        elements.setEnabled(enabled);
    }


    /**
     * Gets the record variance characteristic of the variable.
     *
     * @return record variance of the variable
     */
    public long getRecordVariance() {

        return recordVariance.isSelected() ? CDF.VARY : CDF.NOVARY;
    }


    /**
     * Sets the record variance characteristic of the variable.
     * 
     * @param value record variance of variable
     */
    public void setRecordVariance(boolean value) {

        recordVariance.setSelected(value);
    }


    /**
     * Enables whether or not the variable's record variance field is 
     * enabled.
     *
     * @param enabled true enables the field, false disables it
     */
    public void setRecordVarianceEnabled(boolean enabled) {

        recordVariance.setEnabled(enabled);
    }


    /**
     * Gets the compression setting.
     *
     * @return true if compression is selected.  Otherwise, false.
     */
    public boolean getCompression() {

        return compression.isSelected();
    }


    /**
     * Sets the compression setting.
     *
     * @param value boolean value indicating whether the compression
     *            setting is selected.
     */
    public void setCompression(boolean value) {

        compression.setSelected(value);
    }


    /**
     * Sets the default compression setting based upon the data type
     * and record variances values.
     */
    private void setDefaultCompression() {

        if (gsfc.spdf.istp.Variable.isTimeType(getDataType())) {

            setCompression(false);
        }
        else {

            if (recordVariance.isSelected()) {

                setCompression(true);
            }
            else {

                setCompression(false);
            }
        }
    }


    /**
     * Gets the number of dimensions from the panel.
     *
     * @return number of dimensions
     */
    public int getDimension() {

        return dimensions.getDimension();
    }


    /**
     * Sets the number of dimensions.
     *
     * @param dimension the number of dimensions
     */
    public void setDimension(int dimension) {

        dimensions.setDimension(dimension);
    }


    /**
     * Enables whether or not the variable's dimension information
     * (dimension, size of each dimension, and variance of each 
     * dimension) is enabled.
     *
     * @param enabled true enables the dimension information, false 
     *            disables it.
     */
    public void setDimensionInfoEnabled(boolean enabled) {

        dimensions.setEnabled(enabled);
    }


    /**
     * Gets the sizes of all dimensions.
     *
     * @return size of all dimensions (i'th element contains the size 
     *             of the i'th dimension).
     */
    public long[] getDimensionSizes() {

        return dimensions.getDimensionSizes();
    }


    /**
     * Sets the size of all dimensions.
     *
     * @param sizes the size of all dimensions (i'th element contains 
     *            the size of the i'th dimension).
     */
    public void setDimensionSizes(long[] sizes) {

        dimensions.setDimensionSizes(sizes);
    }


    /**
     * Gets the variance value of all dimensions.
     *
     * @return variance value of all dimensions (i'th element contains 
     *             the variance of the i'th dimension).
     */
    public long[] getDimensionVariances() {

        boolean[] booleanVariances = dimensions.getDimensionVariances();
                                       // boolean version of dimension
                                       // variances
        long[] dimVariances = new long[booleanVariances.length];
                                       // long version of dimension
                                       // variances

        for (int i = 0; i < dimVariances.length; i++) {

            dimVariances[i] = 
                booleanVariances[i] ? CDF.VARY : CDF.NOVARY;
        };

        return dimVariances;
    }


    /**
     * Sets the variance of all dimensions.
     *
     * @param variances the variance of all dimensions (i'th element 
     *            contains the variance of the i'th dimension).
     */
    public void setDimensionVariances(long[] variances) {

        boolean[] booleanVariances = new boolean[variances.length];

        for (int i = 0; i < variances.length; i++) {

            booleanVariances[i] = 
                variances[i] == CDF.VARY ? true : false;
        };

        dimensions.setDimensionVariances(booleanVariances);
    }


    /**
     * Gets the sparse record type.
     *
     * @return sparse record type.
     * @see #setSparseRecordType(SparseRecordType)
     */
    public SparseRecordType getSparseRecordType() {

        return sparseRecordType.getSparseRecordType(0);
    }


    /**
     * Sets the sparse record type to the given value.
     *
     * @param value new sparse record type value.
     * @see #getSparseRecordType()
     */
    public void setSparseRecordType(SparseRecordType value) {

        sparseRecordType.setSparseRecordType(0, value);
    }


    /**
     * Enables or disables the sparse record type component.
     *
     * @param value if true, the sparse record type component;
     *            otherwise the component is disabled.
     */
    public void sparseRecordTypeEnabled(boolean value) {

        sparseRecordType.setEnabled(value);
    }


    /**
     * Gets the pad value.
     *
     * @return the pad value.  null if no pad value has been entered.
     * @throws NumberFormatException if the entered pad value is not
     *             a valid format.
     * @see #setPadValue(Object)
     */
    public Object getPadValue() 
        throws NumberFormatException {

        String value = (String)(padValue.getInputComponentValue(0));
                                       // String representation of 
                                       // pad value

        return Value.decode(value, getDataType());
    }


    /**
     * Sets the pad value to the provided value.
     *
     * @param value new pad value.
     */
    public void setPadValue(Object value) {

        padValue.set(Value.toString(value, getDataType()));        

    }


    /**
     * Enables or disables the pad value component.
     *
     * @param value true to enable the component, otherwise false.
     */
    public void padValueEnabled(boolean value) {

        padValue.setEnabled(value);
    }


    /**
     * Sets the default sparse record type setting based upon the 
     * record variance value.
     */
    private void setDefaultSparseRecordType() {

        if (!recordVariance.isSelected()) {

            setSparseRecordType(SparseRecordType.NONE);
        }
    }


    /**
     * Responds to item change events.
     *
     * @param event event indicating that an item has changed.
     */
    public void itemStateChanged(ItemEvent event) {

        Object source = event.getItemSelectable();
                                       // component generating event

        if (source == recordVariance) {

            setDefaultCompression();
            setDefaultSparseRecordType();

            long dataType = getDataType();

            if (recordVariance.isSelected()) {

                sparseRecordTypeEnabled(true);

                if (dataType == CDF.CDF_CHAR || 
                    dataType == CDF.CDF_UCHAR) {

                    setNumberOfElementsEnabled(true);
                }
            }
            else {

                sparseRecordTypeEnabled(false);

                if (dataType == CDF.CDF_CHAR || 
                    dataType == CDF.CDF_UCHAR) {

                    setNumberOfElementsEnabled(false);
                }
            }
        }
        else {

            dataTypeChanged();
        }
    }


    /**
     * Performs actions in response to a data type change event.
     */
    protected void dataTypeChanged() {
        
        if(type.getInputComponentValue(0)!= null){

            long newType = getDataType();

            if (newType == CDF.CDF_CHAR || newType == CDF.CDF_UCHAR) {

                elements.setInputComponentValue(0, "10");

                if (recordVariance.isSelected()) {

                    setNumberOfElementsEnabled(true);
                }
                else {

                    setNumberOfElementsEnabled(false);
                }
            }
            else  {

                elements.setInputComponentValue(0, "1");
                elements.setEnabled(false);
                
/* bh 3/28/19  allow time variables with dim > 0
                boolean epoch =
                    gsfc.spdf.istp.Variable.isTimeType(newType);

                this.setDimension(epoch ? 0 : this.getDimension());
                this.setDimensionInfoEnabled(epoch ? false : true);
*/
               
                setDefaultCompression();

            }
            setPadValue(PadValue.getDefaultPadValue(newType));
            padValue.setInputComponentToolTipText(
                0, getPadValueToolTipText(newType));
        }
    }


    /**
     * Provides the pad value tool tip text for the given data type.
     *
     * @param dataType CDF data type.
     * @return the pad value tool tip text for the given data type.
     */
    private String getPadValueToolTipText(long dataType) {

        if (dataType == CDF.CDF_CHAR || 
            dataType == CDF.CDF_UCHAR) {

            return "<html>A string value</html>";
        }
        else if (dataType == CDF.CDF_EPOCH) {

            return "<html>dd-mmm-yyyy hh:mm:ss.mmm or<br>" +
                   "yyyymmdd.ttttttt or<br>" +
                   "yyyymmddhhmmss or<br>" +
                   "yyyy-mm-ddThh:mm:ss.cccZ or<br>" +
                   "yyyy-mm-ddThh:mm:ss.ccc</html>";
        }
        else if (dataType == CDF.CDF_EPOCH16) {

            return "<html>dd-mmm-yyyy hh:mm:ss.ccc.mmm.nnn.ppp or<br>" +
                   "yyyymmdd.ttttttttttttttt or<br>" +
                   "yyyymmddhhmmss or<br>" +
                   "yyyy-mm-ddThh:mm:ss.ccc.mmm.nnn.pppZ or<br>" +
                   "yyyy-mm-ddThh:mm:ss.cccmmmnnnppp</html>";
        }
        else if (dataType == CDF.CDF_TIME_TT2000) {

            return "<html>yyyy-mm-ddThh:mm:ss.ccccccccc or<br>" +
                   "dd-mmm-yyyy hh:mm:ss.ccccccccc or<br>" +
                   "yyyymmdd.cccccccccc or<br>" +
                   "yyyymmddhhmmss</html>";
        }
        else if (dataType == CDF.CDF_REAL4 ||
                 dataType == CDF.CDF_FLOAT ||
                 dataType == CDF.CDF_REAL8 ||
                 dataType == CDF.CDF_DOUBLE) {

            return "<html>[-+]DecimalDigits[.DecimalDigits] or<br>" +
                   "NaN or<br>" +
                   "[-+]Infinity</html>";
        }
        else {  // integer type of varying lengths

            return "<html>[-+]DecimalDigits or<br>" +
                   "[-+]0xHexDigits or<br>" +
                   "[-+]0xHexDigits or<br>" +
                   "[-+]#HexDigits or<br>" +
                   "[-+]0OctalDigits</html>";
        }
    }
}

