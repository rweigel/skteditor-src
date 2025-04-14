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
 * $Id: CBandTablePanel.java,v 1.46 2024/10/28 12:59:53 btharris Exp $
 */
package gsfc.spdf.edit.guis;

// Swing Imports
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import javax.swing.table.*;
import javax.swing.event.ChangeEvent;

// Java imports
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

// CDF Imports
import gsfc.nssdc.cdf.*;
import gsfc.nssdc.cdf.util.*;
import gsfc.spdf.gui.*;
import gsfc.spdf.table.*;

// Local Imports
import gsfc.spdf.edit.util.*;
import gsfc.spdf.edit.events.*;

/**
 * This is the compound widgets for displaying _PTR attributes for labels and
 * Formatting.
 */
public class CBandTablePanel 
    extends AbstractLabeledComponent
    implements ItemListener, CDFConstants, VariableEventListener
{

    public  AttributeComboBox comboBox;
    private DataTable        table;
    private JScrollPane      sp;
    private Vector           defaultItems = null;
    private String           attr;
    private VariablePanel    myVP;
    private JLabel           thisLabel; 
    private boolean          deselectedNewVariable = false;      
    /**
     * Create a panel with a AttributeComboBox and a DataTable for
     * displaying _PTR attributes in ISTP compliant CDFs
     *
     * @param myVP the VariablePanel to which this belongs.
     * @param label How to label this.
     * @param attr Which attribute name this displays
     */
    public CBandTablePanel(VariablePanel myVP, String label, String attr) {
	this(myVP, label, attr, true, null);
    }

    /**
     * Create a panel with a AttributeComboBox and a DataTable for
     * displaying _PTR attributes in ISTP compliant CDFs
     *
     * @param myVP the VariablePanel to which this belongs.
     * @param label How to label this.
     * @param attr Which attribute name this displays
     * @param enabled Default state
     */
    public CBandTablePanel(VariablePanel myVP,
			   String label, String attr, boolean enabled)
    {
	this(myVP, label, attr, true, null);
    }

    /**
     * Create a panel with a AttributeComboBox and a DataTable for
     * displaying _PTR attributes in ISTP compliant CDFs
     *
     * @param myVP the VariablePanel to which this belongs.
     * @param label How to label this.
     * @param attr Which attribute name this displays
     * @param enabled Default state
     * @param defaultItems Items other than variables or New Variable to display
     *     in the combo box.
     */
    public CBandTablePanel(VariablePanel myVP,
			   String label, String attr, boolean enabled,
			   Vector defaultItems)
    {
	this.attr              = attr;
	this.myVP              = myVP;
	this.defaultItems      = defaultItems;
	comboBox               = new AttributeComboBox(attr, defaultItems,this);
	table                  = new DataTable();
	thisLabel              = new JLabel(label);
	

	table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	ListSelectionModel rowSM = table.getSelectionModel();
	rowSM.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	((DefaultDataTableModel)table.getModel()).
	    setMajority(DefaultDataTableModel.COLUMN_MAJOR);
	sp = new JScrollPane(table);

	
	comboBox.addItemListener( this );
	comboBox.addVariableEventListener( myVP );
	comboBox.addVariableEventListener( this );
	enabledColor  = Color.black;
	disabledColor = new Color(142, 142, 142);
	setEnabled(enabled);

	
	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	thisLabel.setAlignmentX(LEFT_ALIGNMENT);
        comboBox.setAlignmentX(LEFT_ALIGNMENT); 
        comboBox.setMinimumSize(new Dimension(100,20));
        Font font = comboBox.getFont();
        comboBox.setFont(new Font(font.getName(),Font.PLAIN, font.getSize()));
        sp.setAlignmentX(LEFT_ALIGNMENT);
        sp.setMinimumSize(new Dimension(100,50));
	add(thisLabel);
        add(Box.createVerticalStrut(3));
        add(comboBox);
        add(sp);
    }
    // AbstractLabeledComponent implementation
    public void addItem(Object item) {
	comboBox.removeItemListener( this );
	comboBox.addItem(item);
	comboBox.addItemListener( this );
    }

    /**
     * Reset the comboBox and table adding the default "New Variable" choice
     */
    public void reset() {
	reset(false);
    }

    /**
     * Reset the comboBox and table.
     *
     * @param isEmpty If true, do not add default "New Variable" choice.
     */
    public void reset(boolean isEmpty) {
	comboBox.removeItemListener( this );
	comboBox.removeVariableEventListener( this );

	if (comboBox.getItemCount() > 0)
	    comboBox.removeAllItems(isEmpty);
        
	table.setVisible(false);
	((DefaultDataTableModel)table.getModel()).setData(null);
	comboBox.setSelectedIndex(-1);
	comboBox.addVariableEventListener( this );
	comboBox.addItemListener( this );
    }

    public void setEnabled(boolean enabled) {
	if (enabled) {
	    thisLabel.setForeground(enabledColor);
	    comboBox.setEnabled(true);
	    table.setEnabled(true);
	    table.setVisible(true);
	    this.repaint();
	    revalidate();
	
	} else {
	    thisLabel.setForeground(disabledColor);
	    comboBox.setEnabled(false);
	    table.setEnabled(false);
	    table.setVisible(false);
	    this.repaint();
	}
    }
    
    // Set the AttributeComboBox's variable
    public void setVariable(Variable var) {
	comboBox.setVariable(var);
    }

    public void setVariableList(Vector list) {
	comboBox.setVariableList(list);
    }

	
    /**
     * Set the comboBox to <code>obj</code>.
     */
    public void set(Object obj) {
	//        comboBox.removeItemListener( this );

	comboBox.setSelectedItem(obj);
	if (comboBox.isEditable()) {
	    ((JTextField)comboBox.getEditor().getEditorComponent()).
		setCaretPosition(0);
	}	
    }

    public Object get() {
	return comboBox.getSelectedItem();
    }

    public DefaultDataTableModel getDataTableModel() {
	return ((DefaultDataTableModel)table.getModel());
    }

    public DataTable getDataTable() {
	return table;
    }

    /**
     * Update the table with the ptrVar data
     *
     * @param obj CDF variable whose data is to be added to table.
     */
    public void updateTable(Object obj) 
    {
//	long [] sizes;
	Object data;
        Variable var = null;
	
	int numDefaults = 0;
	if (defaultItems != null)
	    numDefaults = defaultItems.size();

	if (obj != null)
        {
	    // ignore New Variable selection and any defaults
	    if (comboBox.getSelectedIndex() > numDefaults)
            {
		if (!(obj instanceof Variable))
                {
		   try {
			CDF cdf = myVP.getSelectedVar().getMyCDF();
			var = cdf.getVariable(obj.toString());
		   } catch (CDFException e) {
			// Variable not found.  Can't happen
			System.err.println("CBandTablePanel: "+e);
		   }
		}
                else
                {
                    var = (Variable)obj;
                };
                if (var != null)
                {
/*
		    if (var.getNumDims() > 0) sizes = var.getDimSizes();
		    else sizes = new long [] {1};
*/
		    
		    try {
                        data = SKTUtils.stripBlanks(var.getRecord(0));
                        
			((DefaultDataTableModel)table.getModel()).setData(data);  
			table.sizeColumnsToFit(true);
			table.setVisible(true);
		    } catch (Exception e) {
			myVP.myEditor.setStatus("Error getting data for "+
						var,
						StatusBar.ERROR, true, true);
			System.err.println(e);
			e.printStackTrace();
		    }
		} else {
		    table.setVisible(false);
		    ((DefaultDataTableModel)table.getModel()).setData(null);
		}
	    } else {
		table.setVisible(false);
		((DefaultDataTableModel)table.getModel()).setData(null);
	    }
	} else {
	    table.setVisible(false);
	    ((DefaultDataTableModel)table.getModel()).setData(null);
	}
    }

    /**
     * Checks to see if the string is contained in the comboBox list.
     * This is only useful for comboBoxes that contain only strings.
     *
     * @param item value to search list for.
     * @return True if the item is in the list of items.
     */
    public boolean contains(String item) {
	int count = comboBox.getItemCount();

	for (int i=0;i<count;i++)
	    if (comboBox.getItemAt(i).toString().equals(item))
		return true;
	
	return false;
    }
    //save the work that was done on the table before
    //allowing another selection.
    
    public void saveData() {
        if (table.isEditing()) 
                     table.getCellEditor().stopCellEditing();
            myVP.getTopDisplay().getAxisInfo().save(myVP.getSelectedVar());
    }
    
    /**
     * Handle a new Variable event on the combo box
     */
    public void performVariableAction(VariableEvent event) {
	Variable var  = event.getVariable();
//	long [] dimSizes = var.getDimSizes();
	if (event.getID() == VariableEvent.CREATED) {
	    // build an empty table ready to accept values for _PTR

	    Class tableClass = null;
	    switch ((int)var.getDataType()) {
	    case (int)CDF_CHAR:
	    case (int)CDF_UCHAR:
	    case (int)CDF_TIME_TT2000: 
	    case (int)CDF_EPOCH: 
            case (int)CDF_EPOCH16:   
		try {tableClass = Class.forName("java.lang.String");}
		catch (ClassNotFoundException e) {}
		break;
	    case (int)CDF_BYTE: 
	    case (int)CDF_INT1:   tableClass = Byte.TYPE;    break;
	    case (int)CDF_INT2:
	    case (int)CDF_UINT1:  tableClass = Short.TYPE;   break;
	    case (int)CDF_INT4:
	    case (int)CDF_UINT2:  tableClass = Integer.TYPE; break;
            case (int)CDF_INT8:
	    case (int)CDF_UINT4:  tableClass = Long.TYPE;    break;
	    case (int)CDF_REAL4:
	    case (int)CDF_FLOAT:  tableClass = Float.TYPE;   break;
	    case (int)CDF_REAL8:
	    case (int)CDF_DOUBLE: tableClass = Double.TYPE;  break;
	    }

	//	((DefaultDataTableModel)table.getModel()).createEmptyTable((int)dimSizes[0], 1,
	//			 tableClass);
	//	table.sizeColumnsToFit(true);
            updateTable(var);
	}
   }

    /**
     * Handle events on the combo box
     */
     
    public void itemStateChanged(ItemEvent event) 
   {
           //  myVP.myEditor.setWaitCursor();
   	  
        if (event.getStateChange() == ItemEvent.SELECTED)
        {
            if (!event.getItem().toString().equalsIgnoreCase("New Variable"))                           
	    {
                updateTable(event.getItem());  
             }
        }
    } 
}

	
