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
 * $Id: VariableAttribute.java,v 1.5 2024/10/25 18:37:57 btharris Exp $
 */
package gsfc.spdf.istp;

import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gsfc.nssdc.cdf.CDF;
import gsfc.nssdc.cdf.CDFData;
import gsfc.nssdc.cdf.CDFException;
import gsfc.nssdc.cdf.Entry;


/**
 * This class represents an International Solar-Terrestrial Physics 
 * (ISTP) specialized CDF varaible attribute.  ISTP varaible attributes 
 * have specific names and conform to the 
 * <a href="http://spdf.gsfc.nasa.gov/sp_use_of_cdf.html">
 * ISTP guidelines for CDF</a>.
 * 
 * @author B. Harris
 * @version $Revision: 1.5 $
 */
public class VariableAttribute 
    extends gsfc.spdf.istp.Attribute {

    /**
     * Regular exporession patterns matching the names of ISTP "pointer"
     * variable attributes.  That is, a pattern which matches the name
     * of a variable attribute whose value is the name of another 
     * variable.
     */
    public final static HashSet <Pattern> PTR_ATTRIBUTE_PATTERNS =
        new HashSet<>();

    static {
        PTR_ATTRIBUTE_PATTERNS.add(
            Pattern.compile("^DEPEND_\\d$"));
        PTR_ATTRIBUTE_PATTERNS.add(
            Pattern.compile("^FORM_PTR$"));
        PTR_ATTRIBUTE_PATTERNS.add(
            Pattern.compile("^LABL_PTR_\\d$"));
        PTR_ATTRIBUTE_PATTERNS.add(
            Pattern.compile("^UNIT_PTR$"));
        PTR_ATTRIBUTE_PATTERNS.add(
            Pattern.compile("^SCAL_PTR$"));
        PTR_ATTRIBUTE_PATTERNS.add(
            Pattern.compile("^DELTA_PLUS_VAR$"));
        PTR_ATTRIBUTE_PATTERNS.add(
            Pattern.compile("^DELTA_MINUS_VAR$"));
        PTR_ATTRIBUTE_PATTERNS.add(
            Pattern.compile("^V_PARENT$"));
        PTR_ATTRIBUTE_PATTERNS.add(
            Pattern.compile("^COMPONENT_\\d$"));
    }


    /**
     * Creates an ISTP variable attribute from the given CDF variable
     * attribute.
     *
     * @param attr CDF variable attribute
     */
    public VariableAttribute(gsfc.nssdc.cdf.Attribute attr) {

        super(attr);
    }


    /**
     * Determines if the given CDF variable attribute is a standard
     * ISTP variable attribute.
     *
     * @param attr attribute to test
     * @return true if attr is an ISTP standard pointer.  Otherwise
     *             false
     */
    public static boolean isIstpPointer(
        gsfc.nssdc.cdf.Attribute attr) {

        String attrName = attr.getName();
                                       // attribute's name
        Iterator ptrPatternIter = PTR_ATTRIBUTE_PATTERNS.iterator();
                                       // iterator for pointer attribute
                                       // patterns
        while (ptrPatternIter.hasNext()) {

            Pattern ptrPattern = (Pattern)ptrPatternIter.next();
                                       // a pointer pattern

            if (ptrPattern.matcher(attrName).matches()) {

                return true;
            }
        }

        return false;
    } 

}
