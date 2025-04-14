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
 * $Id: VariableEvent.java,v 1.6 2022/03/24 10:38:28 btharris Exp $
 */

//$Id: VariableEvent.java,v 1.6 2022/03/24 10:38:28 btharris Exp $

package gsfc.spdf.edit.events;

import java.awt.AWTEvent;
import java.awt.Event;

import gsfc.nssdc.cdf.Variable;

/**
 * Event to handle a changes to variables.
 *
 * Currently only used by AttributeComboBox to handle variable creation 
 * events. However, the hooks are present to handle various variable events.
 *
 * @see gsfc.spdf.edit.guis.AttributeComboBox
 *
 * @author Phil Williams
 * @version $Revision: 1.6 $
 */
public class VariableEvent 
    extends AWTEvent 
{

    public static final int CREATED = 
	RESERVED_ID_MAX + 10;

    public static final int DELETED = 
	RESERVED_ID_MAX + 20;

    public static final int NAME_CHANGE = 
	RESERVED_ID_MAX + 30;

    public static final int DATATYPE_CHANGE = 
	RESERVED_ID_MAX + 40;

    public static final int NDIM_CHANGE = 
	RESERVED_ID_MAX + 50;

    public static final int NELEMENTS_CHANGE = 
	RESERVED_ID_MAX + 60;

    public static final int DIMSIZE_CHANGE = 
	RESERVED_ID_MAX + 70;

    public static final int RECVARY_CHANGE = 
	RESERVED_ID_MAX + 80;

    public static final int DIMVARY_CHANGE = 
	RESERVED_ID_MAX + 90;


    /**
     * The variable on which the change occured
     */
    private Variable _var;
    
   private boolean deltaSource; 
    
    /**
     * Get the value of _var.
     * @return Value of _var.
     */
    public Variable getVariable() {return _var;}
    
         
    public VariableEvent(Object source, Variable var, int id, String attribute) {
	super(source, id);
	this._var = var;
        deltaSource = attribute.indexOf("DELTA") > -1 ? true:false;
    }
    
   public boolean isDeltaSource() {
           
       return deltaSource;
   }
                   
}
