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
 * $Id: GeneralInputComponent.java,v 1.4 2022/03/24 10:38:36 btharris Exp $
 */

// $Id: GeneralInputComponent.java,v 1.4 2022/03/24 10:38:36 btharris Exp $
package gsfc.spdf.gui;

/**
 * General input component interface.  Implementation of this interface allows
 * many input components (e.g., JTextField, JComboBox, etc.) to be processed
 * genericly.
 * 
 * @author B. Harris
 * @version $Revision: 1.4 $
 */
public interface GeneralInputComponent
{

    /**
     * Provides the number of input components contained within
     * this component.
     * 
     * @return The number of input components contained within this component
     */
    public abstract int getInputComponentCount();

    /**
     * Provides the current value of the specified input component.
     * 
     * @param index Zero valued index identifying the input component whose 
     *              value is to be returned
     * @return The value of the specified input component
     */
    public abstract Object getInputComponentValue(int index);
    
    /**
     * Sets the value of the specified input component.
     * 
     * @param index Zero valued index identifying the input component whose
     *              value is to be set
     * @param value The value to set the specified input component to
     */
    public abstract void setInputComponentValue(int index, Object value);

}

    
