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
 * Copyright (c) 2011-2024 United States Government as represented by 
 * the National Aeronautics and Space Administration. No copyright is 
 * claimed in the United States under Title 17, U.S.Code. All Other 
 * Rights Reserved.
 *
 * $Id: AttributePanel.java,v 1.38 2024/01/22 13:04:14 btharris Exp $
 */
package gsfc.spdf.edit.guis;


import java.awt.Color;
import java.lang.Boolean;
import java.lang.Long;
import java.lang.StringIndexOutOfBoundsException;
import java.lang.reflect.Array;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.*;

import java.awt.*; 
import java.awt.event.*; 

import gsfc.nssdc.cdf.*;
import gsfc.nssdc.cdf.util.CDFUtils;
import gsfc.nssdc.cdf.util.Epoch;
import gsfc.nssdc.cdf.util.Epoch16;

import gsfc.spdf.istp.TerrestrialTime2000;
import gsfc.spdf.istp.Epoch8;
import gsfc.spdf.cdf.Value;
import gsfc.spdf.gui.*;



/**
 * This class represents a panel component for the display and editing of
 * a CDF attribute.
 * 
 * @author B. Harris
 * @version $Revison$
 */
public class AttributePanel extends JPanel 
    implements ItemListener {

    /**
     * The attribute's name.
     */
    protected LabeledTextFieldPanel name;

    /**
     * The attribute's CDF data type.
     */
    protected LabeledCdfDataTypeCBPanel type;

    /**
     * The panel which displays the attribute's value.
     */
    protected EntryValuePanel valuePanel = new EntryValuePanel();


    /**
     * Creates an AttributePanel.
     */
    public AttributePanel() {

        TitledBorder border = new TitledBorder(new EtchedBorder(), 
                                               "Attribute Entry");
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
        
        type = new LabeledCdfDataTypeCBPanel("Type", emptyBorder);
        type.setMinimumSize(type.getPreferredSize());
        type.addItemListener(this);
    
        valuePanel.setMinimumSize(valuePanel.getPreferredSize());


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
        c.gridy = 0;
        c.insets = new Insets(0,0,5,5);
        gridbag.setConstraints(type, c);
        
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        c.weighty = 1;
        c.anchor = GridBagConstraints.NORTH;
        c.insets = new Insets(0,0,5,5);
        gridbag.setConstraints(valuePanel, c);

        
        add(name);
        add(type);
        add(valuePanel);

    }


    /**
     * Sets the visibility of the "multiline text" option on the value panel.
     * 
     * @param visible whether the multiline text option on the value panel is visible
     */
    public void setMultiLineOptionVisible(boolean visible) {

        valuePanel.setMultiLineOptionVisible(visible);
    }
                                                            

    /**
     * Sets the value of the "multiline text" option to the value panel.
     * 
     * @param option value to set the multiline text option on the value panel to
     */
    public void setMultiLineOption(boolean option) {

        valuePanel.setMultiLineOption(option);
    }


    /**
     * Gets the value of the "multiline text" option on the value panel.
     * 
     * @return the value of the multiline text option on the value panel
     */
    public boolean getMultiLineOption() {

        return valuePanel.getMultiLineOption();
    }
                                                            

    /**
     * Sets the attribute entry value that is to be displayed.
     * 
     * @param attributeName attribute name
     * @param entries attribute's entries
     */
    public void setEntry(String attributeName, Vector entries) {

        if (attributeName != null) {

            int numActualEntries = getNumActualEntries(entries);

System.err.println("AttributePanel.setEntry: numActualEntries = " +
                   numActualEntries);

            switch (numActualEntries) {

            case 0:

                // ???
System.err.println("AttributePanel.setEntry: time to decide what to do for this case");
                break;
      
            case 1:

                setEntry(attributeName, getActualEntryAt(entries, 0));
                break;

            default:

                if (allEntriesAreChar(entries)) {
                
                    name.setInputComponentValue(0, attributeName);
                    type.setInputComponentValue(0, "CDF_CHAR");

                    Vector lines = new Vector();

                    for (int i = 0; i < entries.size(); i++) {

                        Entry entry = (Entry)entries.elementAt(i);

                        if (entry != null) {

                            try {

                                lines.addElement(
                                    (String)entry.getData());
                            }
                            catch (CDFException e) {

                            }
                        }
                    }
                    valuePanel.setValue(lines);
                    valuePanel.setMultiLineOption(true);
                    valuePanel.showValueAsArea();
                }
                else {

                    setEntry(attributeName, getActualEntryAt(entries, 0));
                }
                break;
            };
        }
        else {

            name.setInputComponentValue(0, "");
            type.setInputComponentValue(0, "");
            valuePanel.setValue("");
        };
    }


    /**
     * Sets the attribute entry that is to be displayed.
     * 
     * @param attributeName attribute name
     * @param entry entry to be displayed
     */
    public void setEntry(String attributeName, Entry entry) {

        if (attributeName != null) {

            name.setInputComponentValue(0, attributeName);

            if (entry != null) {

                type.setInputComponentValue(0, 
                                     CDFUtils.getStringDataType(entry));
 
                Object data = null;
                try {

                    data = entry.getData();
                }
                catch (CDFException e) {

                }
                String value = null;

                if (data != null) {

                    if (entry.getDataType() == CDF.CDF_TIME_TT2000) {

                        data = TerrestrialTime2000.toString((Long)data);
                    }
                    else if (entry.getDataType() == CDF.CDF_EPOCH) {

                        data = gsfc.spdf.istp.Epoch8.toString((Double)data);
                    }
                    else if (entry.getDataType() == CDF.CDF_EPOCH16) {

                        data = gsfc.spdf.istp.Epoch16.toString((double[])data);
                    }
                }
                
                if (data instanceof String) {

                    valuePanel.setValue((String)data);
                }
                else if (data instanceof String[]) {

                    valuePanel.setValue((String[])data);
                }
                else {

                    valuePanel.setValue(data);
                };

                if (entry.getDataType() == CDF.CDF_CHAR ||
                    entry.getDataType() == CDF.CDF_UCHAR ||
                    entry.getDataType() == CDF.CDF_EPOCH ||
                    entry.getDataType() == CDF.CDF_EPOCH16 ||
                    entry.getDataType() == CDF.CDF_TIME_TT2000){

                    valuePanel.showValueAsArea();
                }
                else {

                    valuePanel.showValueAsTable();
                };
            }
            else {

                type.setInputComponentValue(0, "CDF_CHAR");
                valuePanel.setValue("");
            };
        }
        else {

            name.setInputComponentValue(0, "");
            type.setInputComponentValue(0, "");
            valuePanel.setValue("");
        };
    }


    /**
     * Gets the CDF data type of the attribute.
     * 
     * @return the CDF data type of the attribute
     */
    public long getDataType() {

        return type.getDataType(0);
    }

    /**
     * Gets the value of the attribute entry.
     * 
     * @return the value of the attribute entry
     */
    public Object getValue() {

        Object value = valuePanel.getValue();

        if (value == null || value instanceof Vector) {

            return value;
        };

        String className = value.getClass().getName();

        if (className.charAt(0) == '[') {    // i.e., an array

            return value;
        }
        else if (className.equals("java.lang.String")) {

            String strValue = (String)value;

            if (strValue.length() == 0) {

                value = null;
            };
        
            long dataType = getDataType();
        
            if (strValue.equals("NaN")) {

                value = Value.getNaN(dataType);
            }
            else {

                try {

                    if (dataType == CDF.CDF_TIME_TT2000) {

                        value = TerrestrialTime2000.fromString(strValue);
                    }
                    else if (dataType == CDF.CDF_EPOCH) {

                        value = Epoch8.fromString(strValue);
                    }
                    else if (dataType == CDF.CDF_EPOCH16) {

                        value = gsfc.spdf.istp.Epoch16.fromString(strValue);
                    };
                }
                catch (CDFException | StringIndexOutOfBoundsException |
                       NumberFormatException e) {

                    value = null;
                }
            }
        };

        return value;
    }


    /**
     * Gets the number of non-null entry values in the given Vector.
     * 
     * @param entries vector of attribute entries
     * @return number of non-null entry values
     */
    protected int getNumActualEntries(Vector entries) {

        int actualEntries = 0;

        for (int i = 0; i < entries.size(); i++) {

            if (entries.elementAt(i) != null) {

                actualEntries++;
            };
        };

        return actualEntries;
    }


    /**
     * Gets the entry at the given index of the given vector.  The index value is with
     * respect to non-null valued elements in the given vector of entries.  e.g., if the
     * first element in null and the second non-null, an index value of 0 would retrieve
     * the second element.
     * 
     * @param entries vector of entries to retrieve the specified entry from
     * @param i index entry to get (null valued elements don't count)
     * @return the specified entry
     */
    protected Entry getActualEntryAt(Vector entries, int i) {

        int actualEntry = 0;

        for (int j = 0; j < entries.size(); j++) {

            if (entries.elementAt(j) != null) {

                if (actualEntry == i) {

                    return (Entry)entries.elementAt(j);
                };

                actualEntry++;
            };
        };

        return null;
    }


    /**
     * Determines whether all of the given entries are of CDF dataType CHAR.
     * 
     * @param entries attribute entries to examine
     * @return true if all the given entries are of CDF dataType CHAR
     */
    protected boolean allEntriesAreChar(Vector entries) {

        for (int i = 0; i < entries.size(); i++) {

            Entry entry = (Entry)entries.elementAt(i);

            if (entry != null && ((entry.getDataType() != CDF.CDF_CHAR) &&
                                  (entry.getDataType() != CDF.CDF_UCHAR)) ){

                return false;
            };
        };
        return true;
    }


    /**
     * Responds to a dataType change.
     * 
     * @param event event indicating that the CDF dataType combobox value has changed
     */
    public void itemStateChanged(ItemEvent event) {

        if (((String)event.getItem()).equals("CDF_CHAR")) {

            valuePanel.showValueAsArea();
        }
        else {

            Object value = getValue();

            if (value != null && value instanceof Vector) {

                int reply = JOptionPane.showConfirmDialog(this,
                                "Changing the data type may result in " +
                                "loosing some\ninformation.  Do you want " +
                                "to continue?",
                                "Data Type Change Confirmation",
                                JOptionPane.OK_CANCEL_OPTION);

                if (reply != JOptionPane.YES_OPTION) {

                    type.setInputComponentValue(0, "CDF_CHAR");
                    return;
                };
            };
            valuePanel.showValueAsTable();
        };
    }
}
