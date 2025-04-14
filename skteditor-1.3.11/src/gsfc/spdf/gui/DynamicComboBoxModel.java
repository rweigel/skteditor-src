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
 * $Id: DynamicComboBoxModel.java,v 1.12 2024/10/28 12:52:49 btharris Exp $
 */

// $Id: DynamicComboBoxModel.java,v 1.12 2024/10/28 12:52:49 btharris Exp $
package gsfc.spdf.gui;

import java.util.Vector;
import java.text.Collator;
import java.text.CollationKey;

import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

import javax.swing.MutableComboBoxModel;
import javax.swing.ComboBoxModel;
import javax.swing.AbstractListModel;

/**
 * A combo box model that adds items to an editable combo box as the user
 * enters new values.
 *
 *  <BR><BR>When an item is entered into a JComboBox that is using this model, 
 *  if the item is not already a member of the list, it will be added
 *  to the list in the appropriate position.  The position is determined
 *  by the ordering that is used.  The three supported ordering modes are
 *  <code>DynmaicComboModel.TOP</code>, <code>DynmaicComboModel.BOTTOM</code> 
 *  (which is the default) and <code>DynmaicComboModel.ALPHABETIC</code>.
 *
 *  <BR><BR>A dynamic combo box may be created by the following:
 *  <PRE>
 *     cbModel = new DynamicComboBoxModel(sList,
 *                                        DynamicComboBoxModel.BOTTOM);
 *     combo = new JComboBox(cbModel1);
 *     combo.setEditable(true);
 *  </PRE>
 *  <BR><BR>
 *
 *  @author Phil Williams
 *
 */
public class DynamicComboBoxModel
    extends AbstractListModel
    implements MutableComboBoxModel, Serializable
{
    Vector objects;
    Object selectedObject;

    /**
     * Add the entry and alphabetize
     */
    public static final int ALPHABETIC = 1;

    /**
     * Add new entries to the top of the list
     */
    public static final int TOP = 2;

    /**
     * Add new entries to the bottom of the list. <B>(default)</B>
     */
    public static final int BOTTOM = 3;

    /**
     * Is this indeed dynamic
     */
    protected boolean dynamic;

    /**
     * Where to add new items to the list
     */
    protected int order;

    /**
     * Constructs an empty DynamicComboBoxModel object.
     */
    public DynamicComboBoxModel() {
        objects = new Vector();
	order = BOTTOM;
    }

    /**
     * Constructs a DynamicComboBoxModel object initialized with
     * an array of objects.
     *
     * @param items  an array of Object objects
     */
    public DynamicComboBoxModel(final Object items[]) {
	this(items, BOTTOM);
    }

    /**
     * Constructs a DynamicComboBoxModel object initialized with
     * an array of objects using the ordering specified.
     *
     * @param items  an array of Object objects
     * @param order  Either <code>DynmaicComboModel.TOP</code>, 
     *               <code>DynmaicComboModel.BOTTOM</code> or
     *               <code>DynmaicComboModel.ALPHABETIC</code>
     */
    public DynamicComboBoxModel(final Object items[], int order) {
	this.order = order;
	Vector v = new Vector();
        objects = new Vector();
	objects.ensureCapacity( items.length );
	
        int i,c;
	for ( i=0,c=items.length;i<c;i++ )
	    v.addElement(items[i]);
	
	if (order == DynamicComboBoxModel.ALPHABETIC)
	    objects = alphabetize(v);
	else
	    objects = v;
	
	if ( getSize() > 0 ) {
	    selectedObject = getElementAt( 0 );
	}
    }
    
    /**
     * Constructs a DynamicComboBoxModel object initialized with
     * a vector.
     *
     * @param v  a Vector object
     */
    public DynamicComboBoxModel(Vector v) {
	this(v, BOTTOM);
    }

    /**
     * Constructs a DynamicComboBoxModel object initialized with
     * a vector.
     *
     * @param v  a Vector object
     * @param order  Either <code>DynmaicComboModel.TOP</code>, 
     *               <code>DynmaicComboModel.BOTTOM</code> or
     *               <code>DynmaicComboModel.ALPHABETIC</code>
     */
    public DynamicComboBoxModel(Vector v, int order) {
	this.order = order;
	if (order == ALPHABETIC)
	    objects = alphabetize(v);
	else
	    objects = v;
	
	if ( getSize() > 0 ) {
	    selectedObject = getElementAt( 0 );
	}
    }
    
    /**
     * Use a bubble sort to alphabetize the list of items.
     *
     * @param v list of items.
     * @return alphabetized list of items.
     */
    private Vector alphabetize(Vector v) {
	Vector vObjects = new Vector();
	int count = v.size();
	Collator collator = Collator.getInstance();
	CollationKey [] keys = new CollationKey[count];
	CollationKey tempKey;
	
	// Fill the keys
	for (int i = 0 ; i < count ; i++)
	    keys[i] = collator.getCollationKey(v.elementAt(i).toString());
	
	// Bubble sort the list
	for (int i = count-1 ; i > 0 ; i--) {
	    for (int j = 0 ; j < i ; j++) {
		if (keys[j].compareTo(keys[j+1]) > 0) {
		    tempKey   = keys[j];
		    keys[j]   = keys[j+1];
		    keys[j+1] = tempKey;
		}
	    }
	}
	
	for (int i=0;i<count;i++)
	    vObjects.addElement(keys[i].getSourceString());
	return vObjects;
    }
    
    /**
     * Get the ordering.
     *
     * @return the ordering of this model.
     */
    public int getOrder() {
	return order;
    }
    
    /**
     * Set the ordering
     *
     * @param order  Either <code>DynmaicComboModel.TOP</code>, 
     *               <code>DynmaicComboModel.BOTTOM</code> or
     *               <code>DynmaicComboModel.ALPHABETIC</code>
     */
    public void setOrder(int order) {
	this.order = order;
    }
    
    /**
     *implements javax.swing.ComboBoxModel
     */
    public void setSelectedItem(Object anObject) {
	if (anObject != null)
	    if (!objects.contains(anObject)) 
		addElement(anObject);
        selectedObject = anObject;
	fireContentsChanged(this, -1, -1);
    }
    
    /**
     * implements javax.swing.ComboBoxModel
     */
    public Object getSelectedItem() {
        return selectedObject;
    }
    
    /** 
     * implements javax.swing.ListModel
     */
    public int getSize() {
        return objects.size();
    }
    
    /**
     * implements javax.swing.ListModel
     */
    public Object getElementAt(int index) {
        if ( index >= 0 && index < objects.size() )
	    return objects.elementAt(index);
	else
	    return null;
    }
    
    /**
     * Returns the index-position of the specified object in the list.
     *
     * @param anObject the object
     * @return an int representing the index position, where 0 is
     *         the first position
     */
    public int getIndexOf(Object anObject) {
        return objects.indexOf(anObject);
    }
    
    /**
     * implements javax.swing.MutableComboBoxModel
     */
    public void addElement(Object anObject) {
	String sElement;
	int nElements = objects.size(), i =0;

	switch (order) {
	case DynamicComboBoxModel.BOTTOM:
	    objects.addElement(anObject);
	    fireIntervalAdded(this, objects.size()-1, objects.size()-1);
	    break;
	case DynamicComboBoxModel.TOP:
	    objects.insertElementAt(anObject, 0);
	    fireIntervalAdded(this, 0, 0);
	    break;
	case DynamicComboBoxModel.ALPHABETIC:
	    if (nElements != 0) {
		//		for (int i=0; i<objects.size(); i++) {
		while (i < nElements) {
		    sElement = objects.elementAt(i).toString();
		    if (sElement.compareTo(anObject.toString()) > 0) {
			insertElementAt(anObject,i);
			fireIntervalAdded(this, i, i);
			break;
		    }
		    i += 1;
		}
		
		// Check to make sure we don't have to add it to the end
		if (i == nElements) {
		    insertElementAt(anObject,i);
		    fireIntervalAdded(this, i, i);
		}
	    } else {
		objects.addElement(anObject);
		fireIntervalAdded(this, objects.size()-1, objects.size()-1);
	    }
	    break;
	default:
	    throw new IllegalArgumentException("Bad ordering");
	}
	if (objects.size() == 1 && selectedObject == null && anObject != null)
	    setSelectedItem( anObject );
    }
    
    /**
     * implements javax.swing.MutableComboBoxModel
     */
    public void insertElementAt(Object anObject,int index) {
        objects.insertElementAt(anObject,index);
	fireIntervalAdded(this, index, index);
    }
    
    /**
     * implements javax.swing.MutableComboBoxModel
     */
    public void removeElementAt(int index) {
        if ( getElementAt( index ) == selectedObject ) {
	    if ( index == 0 ) {
		setSelectedItem( getSize() == 1 ? null : getElementAt( index +
								       1 ) );
	    }
	    else {
		setSelectedItem( getElementAt( index - 1 ) );
	    }
	}
	
	objects.removeElementAt(index);
	
	fireIntervalRemoved(this, index, index);
    }
    
    /**
     * implements javax.swing.MutableComboBoxModel
     */
    public void removeElement(Object anObject) {
        int index = objects.indexOf(anObject);
	if ( index != -1 ) {
	    removeElementAt(index);
	}
    }
    
    /**
     * Empties the list.
     */
    public void removeAllElements() {
        int firstIndex = 0;
	int lastIndex = objects.size()-1;
	objects.removeAllElements();
	selectedObject = null;
	fireIntervalRemoved(this, firstIndex, lastIndex);
    }
}
