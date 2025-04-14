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
 * $Id: VariableSpecificationPanel.java,v 1.14 2022/03/24 10:38:32 btharris Exp $
 */
package gsfc.spdf.edit.guis;


import java.awt.Color;
import java.awt.*;
import java.lang.Long;
import java.lang.Boolean;
import java.lang.StringBuffer;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.*;

import gsfc.nssdc.cdf.Variable;
import gsfc.nssdc.cdf.CDFException;
import gsfc.nssdc.cdf.util.CDFUtils;

import gsfc.spdf.cdf.Value;
import gsfc.spdf.gui.LabeledTextFieldPanel;


public class VariableSpecificationPanel extends JPanel {

    protected LabeledTextFieldPanel name;
    protected LabeledTextFieldPanel type;
    protected LabeledTextFieldPanel variances;
    protected LabeledTextFieldPanel dimensionality;
    protected LabeledTextFieldPanel blocking;
    protected LabeledTextFieldPanel allocated;
    protected LabeledTextFieldPanel sparseness;
    protected LabeledTextFieldPanel compression;
    protected LabeledTextFieldPanel records;
    protected LabeledTextFieldPanel pad;


    public VariableSpecificationPanel() {

        TitledBorder border = new TitledBorder(new EtchedBorder(), 
                                               "Variable Specification");
        border.setTitleColor(Color.black);
        setBorder(border);


        EmptyBorder emptyBorder = new EmptyBorder(0, 0, 0, 0);
                                        // an empty border to replace the
                                        //  default etched border on
                                        //  LabeledComboBoxPanels and
                                        //  LabeledTextFieldPanels

        name = new LabeledTextFieldPanel("Name", emptyBorder, 1, 12);
        name.setEditable(false);
        name.setMinimumSize(name.getPreferredSize());
        
        type = new LabeledTextFieldPanel("Type", emptyBorder, 1,12);
        type.setEditable(false);
        type.setMinimumSize(name.getPreferredSize());
        
        variances = new LabeledTextFieldPanel("Variances", 
                                                   emptyBorder, 1, 12);
        variances.setEditable(false);
        variances.setMinimumSize(name.getPreferredSize());
        
        dimensionality = new LabeledTextFieldPanel("Dimensionality", 
                                                   emptyBorder, 1, 12);
        dimensionality.setEditable(false);
        dimensionality.setMinimumSize(name.getPreferredSize());
                
        blocking = new LabeledTextFieldPanel("Blocking", emptyBorder, 1, 12);
        blocking.setEditable(false);
        blocking.setMinimumSize(name.getPreferredSize());
        
        allocated = new LabeledTextFieldPanel("Allocated", emptyBorder, 1, 12);
        allocated.setEditable(false);
        allocated.setMinimumSize(name.getPreferredSize());
        
        sparseness = new LabeledTextFieldPanel("Sparseness", emptyBorder, 
                                              1, 12);
        sparseness.setEditable(false);
        sparseness.setMinimumSize(name.getPreferredSize());
                                              
        compression = new LabeledTextFieldPanel("Compression", emptyBorder, 
                                              1, 12);
        compression.setEditable(false);
        compression.setMinimumSize(name.getPreferredSize());
        
        records = new LabeledTextFieldPanel("Records", emptyBorder, 1, 12);
        records.setEditable(false);
        records.setMinimumSize(name.getPreferredSize());
        
        pad = new LabeledTextFieldPanel("Pad Value", 
                                                emptyBorder, 1, 12);
        pad.setEditable(false);
        pad.setMinimumSize(name.getPreferredSize());
    
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(gridbag);
             
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.5;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.insets = new Insets(0,5,5,0);
     
        gridbag.setConstraints(name, c);
 
        c.gridx = 1;
        c.insets = new Insets(0,0,5,5);
        gridbag.setConstraints(type, c);
        
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(0,5,5,0);
        gridbag.setConstraints(variances, c);
 
        c.gridx = 1;
        c.insets = new Insets(0,0,5,5);
        gridbag.setConstraints(dimensionality, c);

        c.gridx = 0;
        c.gridy = 2;
        c.insets = new Insets(0,5,5,0);
        gridbag.setConstraints(blocking, c);
 
        c.gridx = 1;
        c.insets = new Insets(0,0,5,5);
        gridbag.setConstraints(allocated, c);
        
        c.gridx = 0;
        c.gridy = 3;
        c.insets = new Insets(0,5,5,0);
        gridbag.setConstraints(sparseness, c);
 
        c.gridx = 1;
        c.insets = new Insets(0,0,5,5);
        gridbag.setConstraints(compression, c);

        c.gridx = 0;
        c.gridy = 4;
        c.insets = new Insets(0,5,5,0);
        gridbag.setConstraints(records, c);
 
        c.gridx = 1;
        c.weighty = 1;
        c.insets = new Insets(0,0,5,5);
        gridbag.setConstraints(pad, c);
       
        
        add(name);
        add(type);
        add(variances);
        add(dimensionality);
        add(blocking);
        add(allocated);
        add(sparseness);
        add(compression);
        add(records);
        add(pad);

        validate();
    }


    public void setVariable(Variable variable) {

        if (variable != null) {

            name.setInputComponentValue(0, variable.getName());
            type.setInputComponentValue(0, 
                                     CDFUtils.getStringDataType(variable));
            boolean recordVarianceValue = variable.getRecVariance();
            long maxRecord = 0;
            long allocatedRecords = 0;
            String compressionValue = "";
            long[] dimSizes = null;
            long numDims = 0l;
            long sparsenessValue = 0;
            long blockingFactorValue = 0;
            long[] dimVariances = null;
            Object padValue = null;
            StringBuffer dimensionalityStr = new StringBuffer();
            StringBuffer variancesStr = new StringBuffer();

            try {

                maxRecord = variable.getMaxWrittenRecord();
                allocatedRecords = variable.getNumAllocatedRecords();
                compressionValue = variable.getCompression();
                numDims = variable.getNumDims();
                dimSizes = variable.getDimSizes();
                sparsenessValue = variable.getSparseRecords();
                blockingFactorValue = variable.getBlockingFactor();
                dimVariances = variable.getDimVariances();
                padValue = variable.getPadValue();
            }
            catch (CDFException e) {

                e.printStackTrace();
            };

            if (recordVarianceValue == true) {

                variancesStr.append("T");
            }
            else {

                variancesStr.append("F");
            };
            if (dimVariances != null) {

                variancesStr.append("/");

                for (int i = 0; i < dimVariances.length; i++) {

                    if (dimVariances[i] == 0) {

                        variancesStr.append("F");
                    }
                    else {

                        variancesStr.append("T");
                    };
                };
            };
            if (numDims == 0) {

                dimensionalityStr.append("0:[]");
            }
            else {

                dimensionalityStr.append(dimSizes.length + ":[");
                for (int i = 0; i < dimSizes.length; i++) {

                    dimensionalityStr.append(Long.toString(dimSizes[i]));
                    if (i < dimSizes.length - 1) {
             
                        dimensionalityStr.append(",");
                    };
                };
                dimensionalityStr.append("]");
            };
            dimensionality.setInputComponentValue(0, 
                                          dimensionalityStr.toString());
            records.setInputComponentValue(0, Long.toString(maxRecord));
            variances.setInputComponentValue(0, variancesStr.toString());
            blocking.setInputComponentValue(0, 
                                         Long.toString(blockingFactorValue));
            allocated.setInputComponentValue(0, 
                                         Long.toString(allocatedRecords));
            sparseness.setInputComponentValue(0, 
                          CDFUtils.getStringSparseRecord(sparsenessValue));
            compression.setInputComponentValue(0, compressionValue);
            if (padValue == null) {

                pad.setInputComponentValue(0, "");
            }
            else {

                if (padValue instanceof String) {

                    pad.setInputComponentValue(0, padValue);
                }
                else {

                    pad.setInputComponentValue(0,
                        Value.toString(padValue, 
                            variable.getDataType()));
                }
            }
        }
        else {

            name.setInputComponentValue(0, "");
            type.setInputComponentValue(0, "");
            dimensionality.setInputComponentValue(0, "");
            records.setInputComponentValue(0, "");
            variances.setInputComponentValue(0, "");
            blocking.setInputComponentValue(0, "");
            allocated.setInputComponentValue(0, "");
            sparseness.setInputComponentValue(0, "");
            compression.setInputComponentValue(0, "");
            pad.setInputComponentValue(0, "");
        };
    }
}
