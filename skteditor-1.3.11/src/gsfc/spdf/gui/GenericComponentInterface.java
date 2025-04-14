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
 * $Id: GenericComponentInterface.java,v 1.8 2022/08/02 12:29:29 btharris Exp $
 */

// $Id: GenericComponentInterface.java,v 1.8 2022/08/02 12:29:29 btharris Exp $

package gsfc.spdf.gui;

/**
 * Used to put a generic interface on any extended Swing/AWT component.
 * 
 * @author Phil Williams
 * @version $Revision: 1.8 $
 * @see gsfc.spdf.gui.GeneralInputComponent
 * @deprecated This interface isn't generic enough to be useful.  Use 
 * gsfc.spdf.gui.GeneralInputComponent instead.
 */
public interface GenericComponentInterface {

    /**
     * A wrapper to get information back from a component
     * 
     * @return information about component.
     * @deprecated
     */
    public abstract Object get();
    
    /**
     * A wrapper to set a component's information
     * 
     * @param obj component information to set.
     * @deprecated
     */
    public abstract void set(Object obj);

    /**
     * Reset the component to a default state
     * 
     * @deprecated
     */
    public abstract void reset();
}
