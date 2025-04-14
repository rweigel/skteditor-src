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
 * $Id: AttributeChangeEvent.java,v 1.3 2022/03/24 10:38:28 btharris Exp $
 */

//$Id: AttributeChangeEvent.java,v 1.3 2022/03/24 10:38:28 btharris Exp $

package gsfc.spdf.edit.events;

import java.awt.AWTEvent;
import java.awt.Event;

import gsfc.nssdc.cdf.Variable;

/**
 * Event to handle a change in the VAR_TYPE or DISPLAY_TYPE.
 *
 * These are fired by the plot info panel if a change in either of these
 * attributes occurs.
 */
public class AttributeChangeEvent 
    extends AWTEvent 
{

    public static final int VAR_TYPE_CHANGE = 
	RESERVED_ID_MAX + 10;

    public static final int DISPLAY_TYPE_CHANGE = 
	RESERVED_ID_MAX + 20;


    /**
     * The variable on which the change occured
     */
    private Variable _var;
    
    /**
     * Get the value of _var.
     * @return Value of _var.
     */
    public Variable getVariable() {return _var;}
    
    public AttributeChangeEvent(Object source, Variable var, int id) {
	super(source, id);
	this._var = var;
    }
}
