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
 * $Id: EntryValuePanel.java,v 1.10 2022/03/24 10:38:32 btharris Exp $
 */
package gsfc.spdf.edit.guis;


import java.awt.*; 
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;

import gsfc.spdf.gui.LabeledTextAreaPanel;
import gsfc.spdf.gui.LabeledDataTablePanel;


/**
 * This class represents a panel component for the display and editing of a 
 * CDF attribute entry value.  The panel employs a cardlayout layout manager
 * to allow the entry value to be displayed as either a text area or a table 
 * of values.
 * 
 * @author B. Harris
 * @version $Revision: 1.10 $
 */
public class EntryValuePanel extends JPanel {

    /**
     * The cardlayout layout manager governing the layout of this panel.
     */
    protected CardLayout layout = new CardLayout();

    /**
     * Cardlayout constraint specifying the card showing the value as a text area.
     */
    protected final static String AREAVALUE = "TextArea value";

    /**
     * Cardlayout constraint specifying the card showing the value as a table.
     */
    protected final static String TABLEVALUE = "Table value";

    /**
     * Indicates whether the value is currently being shown as a table.
     */
    protected boolean tableValueIsShowing;

    /**
     * Component which displays the entry value as a text area.
     */
    protected LabeledTextAreaPanel areaValue;

    /**
     * Component which displays the entry value in a table.
     */
    protected LabeledDataTablePanel tableValue;

    /**
     * Creates an EntryValuePanel.
     */
    public EntryValuePanel() {

        setLayout(layout);
 
        EmptyBorder emptyBorder = new EmptyBorder(0, 0, 0, 0);

        areaValue = new LabeledTextAreaPanel("Value", emptyBorder, 1, 10, 30);
        areaValue.setMultiLineOptionVisible(true);
        areaValue.setLineWrap(true);
        
        tableValue = new LabeledDataTablePanel("Value", emptyBorder);

        add(tableValue, TABLEVALUE);
        add(areaValue, AREAVALUE);

        showValueAsTable();
    }


    /**
     * Sets whether the "multiline text" checkbox option is visible.
     * 
     * @param visible whether to the multiline text checkbox is visible
     */
    public void setMultiLineOptionVisible(boolean visible) {

        areaValue.setMultiLineOptionVisible(visible);
    }


    /**
     * Gets the value of the "multiline text" checkbox.
     * 
     * @return the value of the multiline text checkbox
     */
    public boolean getMultiLineOption() {

        return areaValue.getMultiLineOption();
    }


    /**
     * Sets the value of the "multiline text" checkbox.
     * 
     * @param option the value to set the multiline text checkbox to
     */
    public void setMultiLineOption(boolean option) {

        areaValue.setMultiLineOption(option);
    }


    /**
     * Causes the panel to display the value as a table of values.
     */
    public void showValueAsTable() {

        layout.show(this, TABLEVALUE);
        tableValueIsShowing = true;
    }


    /**
     * Causes the panel to display the value as a text area.
     */
    public void showValueAsArea() {

        layout.show(this, AREAVALUE);
        tableValueIsShowing = false;
    }


    /**
     * Sets the value to be displayed.
     * 
     * @param value the value of the entry to be displayed
     */
    public void setValue(String value) {

        areaValue.setInputComponentValue(0, value);
    }


    /**
     * Sets the value to be displayed.
     * 
     * @param value the value of the entry to be displayed
     */
    public void setValue(String[] value) {

        areaValue.setInputComponentValue(0, value);
    }


    /**
     * Sets the value to be displayed.
     * 
     * @param value the value of the entry to be displayed
     */
    public void setValue(Vector value) {

        areaValue.setInputComponentValue(0, value);
    }


    /**
     * Sets the value to be displayed.
     * 
     * @param value the value of the entry to be displayed
     */
    public void setValue(Object value) {

        tableValue.setInputComponentValue(0, value);
    }


    /**
     * Gets the value of the entry being displayed.
     * 
     * @return the current value of the entry being displayed
     */
    public Object getValue() {

        if (tableValueIsShowing) {

            return tableValue.getInputComponentValue(0);
        }

        return areaValue.getInputComponentValue(0);
    }
}
