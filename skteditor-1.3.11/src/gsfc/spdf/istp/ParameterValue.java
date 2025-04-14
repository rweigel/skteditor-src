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
 * $Id: ParameterValue.java,v 1.7 2022/08/01 16:04:38 btharris Exp $
 */
package gsfc.spdf.istp;


/**
 * This class represents a DISPLAY_TYPE parameter value that consists of a 
 * single value.  For example, the 90000 in the DISPLAY_TYPE value 
 * "topside_ionogram&gt;max_panel_time&gt;90000".
 * 
 * @author B. Harris
 * @version $Revision: 1.7 $
 */
public class ParameterValue 
    extends AbstractDisplayTypeParameter {

    /**
     * Constructs a ParameterValue from the given value.
     * 
     * @param value the parameter's value
     */
    public ParameterValue(String value) {

        this.value = value;
    }


    /**
     * Provides the parameter's value.
     * 
     * @return the parameter's value
     */
    public String getValue() {

        return value;
    }


    /**
     * Provides the String representation of this object's value.
     * 
     * @return String representation of object's value
     */
    public String toString() {

        return value;
    }


    /**
     * Parameter's value.
     */
    protected String value;
}
