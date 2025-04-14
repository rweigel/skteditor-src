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
 * $Id: AttributeComboBox.java,v 1.39 2022/08/02 13:11:17 btharris Exp $
 */
package gsfc.spdf.edit.guis;

import gsfc.spdf.edit.events.VariableEvent;
import gsfc.spdf.edit.events.VariableEventListener;
import gsfc.spdf.edit.util.SKTUtils;

import gsfc.nssdc.cdf.*;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Vector;
import java.awt.*;
import javax.swing.MutableComboBoxModel;
import javax.swing.ComboBoxModel;
import javax.swing.AbstractListModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;

public class AttributeComboBox extends javax.swing.JComboBox {
    
    /**
     * The attribute this box represents
     */
    protected String                 _attr;

    /**
     * The model
     */
    private AttributeComboBoxModel _acm;

    /**
     * The variable to use for comparision
     */
    private Variable               _var;

    /**
     * The newly created variable when "New Variable" is selected.
     */
    private Variable               _newVar;
    
    /**
     * The variable panel.
     */
    private CBandTablePanel           myParent;
    
    /**
     * The CDF to which these variables belong
     */
    private CDF                    _cdf;

    /**
     * The frame to which this box belongs
     */
    private JFrame                 _frame;

    /**
     * Is this a metadata attribute or support data (i.e. xxx_PTR, or DEPEND_X)
     */
    private boolean                getMetadata;

    /**
     * Is the attribute DEPEND_0
     */
    private boolean                checkForEpoch;
    
   /**
     * Is the attribute DELTA_PLUS or DELTA_MINUS
     */
    private boolean                deltaVar;
    /**
     * was the new var added to the list?
     */
    private boolean                newVarAdded;

    /**
     * was the new var created?
     */
    private boolean                varCreated;
    

  /**
     * A list of items to use as a default list.
     *
     * Typically, these items are not variables and should not include the
     * "New Variable" item since this is added by default.
     */
    private Vector _defaultItems = null;

    /**
     * Create an AttributeComboBox for the given attribute.
     *
     * @param attribute The attribute name that this will display
     */
    public AttributeComboBox(String attribute) {
	this(attribute, null,null);
	
    }
    public AttributeComboBox(String attribute, CBandTablePanel myParent) {
	this(attribute, null, myParent);

    }
    
    /**
     * Create an AttributeComboBox for the given attribute and default items.
     *
     * @param attribute The attribute name that this will display
     * @param defaultItems A Vector of String that contain default items to 
     *   display in the combo box. Note that "New Variable" does not need to be
     *   explicitly listed in this list as the combo box handles this item.
     * @param myParent parent Panel.
     */
    public AttributeComboBox(String attribute, Vector defaultItems,CBandTablePanel myParent) {
	super();
    setMinimumSize(new Dimension(100,15));
	
	this._attr = attribute;
	this._acm = new AttributeComboBoxModel();
	setModel(_acm);
    this.myParent = myParent;
    
	if (defaultItems != null)
	    this._defaultItems = defaultItems;
    }
    
    public void setVariableList(Vector list) {
	_acm.setVariableList(list);
    }

    public void setVariable(Variable var) {
	_acm.setVariable(var);
    }

    /**
     * Remove all the elements from the list of choices and then add the default
     * "New Variable" selection.
     */
    public void removeAllItems() {
	_acm.reset();
    }

    /**
     * Remove all the elements from the list of choices. 
     *
     * @param isEmpty If true, do not add default "New Variable" choice.
     */
    public void removeAllItems(boolean isEmpty) {
	_acm.reset(isEmpty);
    }

    protected void fireVariableEvent(VariableEvent e) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
	for ( int i = listeners.length-2; i>=0; i-=2 ) {
	    if ( listeners[i]==VariableEventListener.class ) {
		((VariableEventListener)listeners[i+1]).
		    performVariableAction(e);
	    }
	}
	// reset varCreated
	varCreated = false;
    }

    public void addVariableEventListener(VariableEventListener l) {
	listenerList.add(VariableEventListener.class, l);
    }

    public void removeVariableEventListener(VariableEventListener l) {
	listenerList.remove(VariableEventListener.class, l);
    }

    /**
     * If a new variable was selected then create a NewVariableEvent
     * and fire that off the VariableEventListeners
     */
    protected void fireItemStateChanged(ItemEvent e) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();

	// if a new var was created, construct a NewVarEvent and fire it
	if (varCreated) {
	    fireVariableEvent(new VariableEvent(this,
						_newVar, 
                                               VariableEvent.CREATED, _attr));                                         
            
	} else {
	    // Process the listeners last to first, notifying
	    // those that are interested in this event
	    for ( int i = listeners.length-2; i>=0; i-=2 ) {
		if ( listeners[i]==ItemListener.class ) {
		    ((ItemListener)listeners[i+1]).itemStateChanged(e);
		}
	    }
	}
    }   
	
    ////////////////////////////////////////////////
    //                                            //
    //          Inner Model Class                 //
    //                                            //
    ////////////////////////////////////////////////

    /**
     *  This model can be used to display a list of possible variables that
     *  match the conditions for use as a DEPEND or XXX_PTR attribute
     *
     *  @author Phil Williams
     *
     *  1999, NASA/Goddard Space Flight Center
     *  This software may be used, copied, or redistributed as long as it is not
     *  sold and this copyright notice is reproduced on each copy made.  This
     *  routine is provided as is without any express or implied warranties
     *  whatsoever.
     */
    private class AttributeComboBoxModel
	extends AbstractListModel
	implements MutableComboBoxModel, Serializable, CDFConstants
    {
	
	/**
	 * A Vector that contains all Variables.  All elements in an
	 * AttributeComboBoxModel must be CDF Variables.
	 *
	 * The only exceptions are the first element which is always 
	 * "New Variable" and if the combo box is editable and the
	 * selectedIndex is -1
	 */
	private Vector validList = new Vector(); 
	private Vector list = null;
	
	public void setVariableList(Vector list) {
	    // Reset the validList
	    reset();
	    
	    this.list = list;
	    
	    System.err.println("ACB.setVariableList: adding elements to list");
	    for (int i=0;i<list.size();i++) {
		addElement(list.elementAt(i));
	    }
	}
	
	/**
	 * Reset the list of possible selections and add the default
	 * "New Variable" selection.
	 */
	public void reset() {
	    reset(false);
	}
	
	/**
	 * Reset the list of possible selections without adding the 
	 * default "New Variable" selection.
         *
         * @param isEmpty whether to add the "New Variable" selection.  If
         *     false, add "New Variable" selection.  Otherwise, do not add
         *     "New Variable" selection.
	 */
	public void reset(boolean isEmpty) {
	    validList.setSize(0);

	    if (!isEmpty)                
		validList.addElement("New Variable");
	    if (_defaultItems != null) {
		for (int i = 0; i < _defaultItems.size(); i++) 
		    validList.addElement(_defaultItems.elementAt(i).toString());
	    }
           setSelectedItem(null);
	}	    
	private long [] dimSizes;
	
	public void setVariable(Variable v) {
	    _var = v;
	    _cdf = v.getMyCDF();
	    this.dimSizes = v.getDimSizes();
	    
	    // Reset the validList
	    System.err.println("ACB.setVariable: calling reset");
	    reset();
	    
	    System.err.println("ACB.setVariable: adding elements to list");
	    if (list != null) 
		for (int i=0;i<list.size();i++)
		    addElement(list.elementAt(i));
	    
	}
	
	private Object selectedVariable;
	
	private int testIndex;
	
	////////////////////////////////////////////////
	//                                            //
	//                Constructor                 //
	//                                            //
	////////////////////////////////////////////////
	
	public AttributeComboBoxModel() {
	    if (_attr.indexOf("_PTR") >= 0)
		getMetadata = true;
	    else
		getMetadata = false;
	    
	    if (_attr.indexOf("DEPEND_0") == 0) {
		checkForEpoch = true;
	    } else {
		checkForEpoch = false;
	    }
            
 	    if (_attr.indexOf("DELTA") == 0) {
		deltaVar = true;
	    } else {
		deltaVar = false;
	    }           
	    
	    try {
		testIndex = 
		    Integer.decode(_attr.substring(_attr.length() - 1)).
		    intValue()-1;
	    } catch (NumberFormatException nfe) {
		testIndex = 0;
	    }
	    
	}
	
	////////////////////////////////////////////////
	//                                            //
	//  javax.swing.ComboBoxModel implementation  //
	//                                            //
	////////////////////////////////////////////////
	
	public void setSelectedItem(Object anObject) {
	    varCreated = false;
	    _newVar    = null;

	    int numDefaults = 0;
	    if (_defaultItems != null)
		numDefaults = _defaultItems.size();

	    if (anObject != null) {
	        
		if(myParent !=null)    
	           AttributeComboBox.this.myParent.saveData();

                if (anObject instanceof String[]) {

                   // A multi-valued cdf variable attribute entry value.  
                   // Just use the first value.

                   anObject = Array.get(anObject, 0);
                }

		// if the item == "New Variable" then open a new var dialog box
		if (anObject.toString().equals("New Variable")) {
                    Object selected = this.getSelectedItem();
		    System.err.println("AttributeComboBox: creating new "+_attr);
		    _newVar = create();
		    if (_newVar != null) {
			varCreated = true;
			addElement(_newVar);
			anObject = _newVar;
		    } else {
			selectedVariable = selected;
			fireContentsChanged(this, -1, -1);
			return;
		    }
		} else if (_defaultItems != null) { // is it a default item

		    if (_defaultItems.contains(anObject))
			selectedVariable = anObject;
		} 

		if (AttributeComboBox.this.isEditable()) {
		    // in this case the object could be a string.
		    // if so, check to see if there is a named variable and set
		    // the object to the variable
		    if ((_var != null) && (validList.size() > (1+numDefaults))) {
			Variable aVariable = null;                                            
			try {
			    aVariable = _var.getMyCDF().
				getVariable(anObject.toString());                                                        
			    if (validList.contains(aVariable))
				selectedVariable = aVariable;                            
			    else
				selectedVariable = anObject;
			} catch (CDFException e) {
			    selectedVariable = anObject;
			}
		    } else
			selectedVariable = anObject;
		    } else if (validList.contains(anObject))
			selectedVariable = anObject;
		} else
		    selectedVariable = null;
            
             
	  fireContentsChanged(this, -1, -1);
	}

	public Object getSelectedItem() {
	    return selectedVariable;
	}
	
	////////////////////////////////////////////////
	//		                 	      //
	//   javax.swing.ListModel implementation     //
	//					      //
	////////////////////////////////////////////////
	
	public int getSize() {
	    return validList.size();
	}
	
	public Object getElementAt(int index) {
	    if ( (index >= 0) && (index < validList.size()) )
		return validList.elementAt(index);
	    else
		return null;
	}
	
	////////////////////////////////////////////////
	//	                                      //
	// javax.swing.MutableComboBox implementation //
	//					      //
	////////////////////////////////////////////////
	
	public void addElement(Object anObject) {
	    Variable testVar = null;
	    
	    if (anObject instanceof Variable) {

                testVar = (Variable)anObject;
            }
	    else {
		// All objects must resolve to a valid variable name
		long id = _cdf.getVariableID(anObject.toString());
		if (id != -1) {
		    try {
			testVar = _cdf.getVariable(anObject.toString());
		    } catch (CDFException exc) {
			// Ignore errors (Should never happen)
			System.err.println("AttributeComboBoxModel error: "+
					   exc.getMessage());
		    }
		}
	    }
	    
	    if (testVar != null) {

		if (addIt(testVar)) {
		    validList.addElement(testVar);
   
		    fireIntervalAdded(this, validList.size()-1,
				      validList.size()-1);
		}
	    }
	}
	
	public void insertElementAt(Object anObject, int index) {
	    Variable testVar = null;
	    if (anObject instanceof Variable)
		testVar = (Variable)anObject;
	    else {
		long id = _cdf.getVariableID(anObject.toString());
		if (id != -1) {
		    try {
			testVar = _cdf.getVariable(anObject.toString());
		    } catch (CDFException e) {
			System.err.println("AttributeComboBoxModel error: "+
					   e.getMessage());
		    }
		}
	    }
	    
	    if (testVar != null) {
		if (addIt(testVar)) {
		    validList.insertElementAt(anObject, index);
		    fireIntervalAdded(this, index, index);
		}
	    }
	}
	
	public void removeElementAt(int index) {
	    if ( getElementAt( index ) == selectedVariable ) {
		if ( index == 0 ) {
		    System.err.println("ACB.removeElementAt:  "+
				       getElementAt( index + 1 ) );
		    setSelectedItem( getSize() == 1 ? null : 
				     getElementAt( index + 1 ) );
		}
		else {
		    System.err.println("ACB.removeElementAt:  "+
				       getElementAt( index - 1 ) );
		    setSelectedItem( getElementAt( index - 1 ) );
		}
	    }
	    
	    validList.removeElementAt(index);
	    
	    fireIntervalRemoved(this, index, index);
	}
	
	public void removeElement(Object anObject) {
	    int index = validList.indexOf(anObject);
	    if ( index != -1 ) {
		removeElementAt(index);
	    }
	}
	
	// Private APIs
	/**
	 * Should the testVar be added to the validList???
         *
         * @param testVar CDF Variable to potentially add to validList.
         * @return whether testVar was added to the validList.
	 */
	private boolean addIt(Variable testVar) {
	    String testType;

 	    newVarAdded = !testVar.getName().equals(_var.getName());
	    if (newVarAdded) {
		try {

                      testType = VarType.get(testVar);

		    if (getMetadata)
			// Building a list of _PTRs
			newVarAdded = (testType.toLowerCase().equals("metadata") &&
				       ((testVar.getNumDims() == 1) &&
					(testVar.getDimSizes()[0] == dimSizes[testIndex])));
                    else if(deltaVar){
                        // building a list for DELTA_PLUS_VAR and DELTA_MINUS_VAR attributes
                     
                        newVarAdded = true;   
                        
                        gsfc.spdf.istp.Variable var = new gsfc.spdf.istp.Variable(_var);
                       
                        gsfc.spdf.istp.Variable plus = var.getDeltaPlusVar();
                        gsfc.spdf.istp.Variable minus = var.getDeltaMinusVar();
                        
                        if (plus != null && plus.getCdfVariable()== testVar ||
                                minus != null && minus.getCdfVariable()== testVar )
                      
                            return newVarAdded;
                       
                                            
                        try {
                        
                            if(!((String)testVar.getEntryData("UNITS")).
                                                equalsIgnoreCase((String)_var.getEntryData("UNITS")) ||
                                                testType.toLowerCase().equalsIgnoreCase("ignore_data")){
                                
                                    newVarAdded = false;
                            }
                        }catch(CDFException e){
                                // no units defined
                        }
                        if(testType.toLowerCase().equalsIgnoreCase("ignore_data")){
                            
                            newVarAdded = false;
                        }
                        
                        
                        if(testVar.getNumDims() != _var.getNumDims()){
                            
                            newVarAdded = false;
                        }
                        else {
                            
                            for (int i = 0; i < testVar.getNumDims(); i++){
                                
                                if(testVar.getDimSizes()[i] !=_var.getDimSizes()[i]) {
                                    
                                    newVarAdded = false;
                                    break;
                                }
                            }
                        }
                       
                        newVarAdded &=  (testVar.getDataType() == _var.getDataType() ||  
                                                (_var.getDataType()== CDF.CDF_EPOCH && testVar.getDataType()== CDF.CDF_REAL8));                        
                   }
                    
                   
		    else {	      
			// Building a list for DEPEND_s
			newVarAdded = testType.equalsIgnoreCase("data") ||
                                      testType.equalsIgnoreCase("support_data")||
                                      testType.equalsIgnoreCase("metadata");
			if (checkForEpoch) {

			    newVarAdded &= ((testVar.getNumDims() == 0) &&
				    ((testVar.getDataType() == CDF_EPOCH)||
                                     (testVar.getDataType() == CDF_EPOCH16) ||
                                     (testVar.getDataType() == CDF_TIME_TT2000)));
			}
			else {

			   String displaytype = ((String)_var.getEntryData("DISPLAY_TYPE")).
				toLowerCase();
			    if ((displaytype.indexOf("map") >= 0)&& dimSizes.length>1)
				newVarAdded &= ((testVar.getNumDims() == 2) &&
						((testVar.getDimSizes()[0] == dimSizes[0]) &&
						 (testVar.getDimSizes()[1] == dimSizes[1])));

			    else
				newVarAdded &= ((testVar.getNumDims() == 1) &&
						(testVar.getDimSizes()[0] == dimSizes[testIndex]));
			}
		    }
		
		    return newVarAdded;
		    
		} catch (CDFException e) {

                    // probably "No such entry for specified attribute."

            try{

                if (VarType.get(_var).equals("support_data")) {

                    return ((testVar.getNumDims() == 1) &&
                            dimSizes != null &&
                            (testVar.getDimSizes()[0] == 
                                 dimSizes[testIndex]));
                }
                else {

                    return false;
                }
            }
            catch(CDFException ex) {
                return false;
            }
		}
	    } 

	    return newVarAdded;
	}
	
	private Variable create() {

		if (getMetadata){

                    //
                    // remove _PTR from attribute name to form metadataType
                    //
                    int _ptrIndex = _attr.indexOf("_PTR");
                    String metadataType = null;

                    try {
                        if (_ptrIndex > 0 && _ptrIndex < _attr.length()) {

                            metadataType = _attr.substring(0, _ptrIndex);

                            if (_ptrIndex + 4 < _attr.length()) {

                                metadataType += _attr.substring(_ptrIndex + 4);
                            };
                        };
                    }
                    catch (IndexOutOfBoundsException e) {

                        System.err.println(this.getClass().getName() + 
                                  ": IndexOutOfBoundsException for " + _attr);
                    };

		    return ISTPVariableDialog.createMetadata(_frame, 
                                                             _cdf, 
                                                             _var, 
                                                             testIndex,
                                                             metadataType);
		}
		else {
                    
                    String supportDataType = _attr;
                                        
		    return ISTPVariableDialog.createSupportData(_frame, 
								_cdf, 
								checkForEpoch,
								_var,
								testIndex,
                                                                supportDataType);
                    
                }
	}
    }
    
    
} // AttributeComboBox
