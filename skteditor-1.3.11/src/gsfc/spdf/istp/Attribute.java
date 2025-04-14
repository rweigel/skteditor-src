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
 * $Id: Attribute.java,v 1.6 2022/03/24 10:38:39 btharris Exp $
 */
package gsfc.spdf.istp;

import java.util.Vector;

import gsfc.nssdc.cdf.CDF;
import gsfc.nssdc.cdf.CDFData;
import gsfc.nssdc.cdf.CDFException;
import gsfc.nssdc.cdf.Entry;


/**
 * This class represents an International Solar-Terrestrial Physics 
 * (ISTP) specialized CDF attribute.  ISTP attributes have specific 
 * names and conform to the 
 * <a href="http://spdf.gsfc.nasa.gov/sp_use_of_cdf.html">
 * ISTP guidelines for CDF</a>.
 * 
 * @author B. Harris
 * @version $Revision: 1.6 $
 */
public class Attribute {

    /**
     * The CDF Attribute associated with this ISTP CDF Attribute.
     */
    protected gsfc.nssdc.cdf.Attribute attr = null;


    /**
     * Creates an ISTP attribute of the given CDF attribute.  The caller
     * is responsible for setting the variable's attributes correctly.
     *
     * <p><b>Note:</b>This method is public to to aid in the 
     * introduction of this gsfc.spdf.istp.Attriubute class hiararchy 
     * into an existing codebase.  The method is expected to become 
     * protected or eliminated in the future.
     *
     * @param attr a CDF attribute
     */
    public Attribute(gsfc.nssdc.cdf.Attribute attr) {

        this.attr = attr;
    }


    /**
     * Get any global or variable attribute from the given CDF with the
     * specified name.
     *
     * @param cdf the CDF to get the attribute from.
     * @param name name of attribute to get.
     * @return specified variable attribute or null if it does not 
     *     exist.
     * @throws CDFException if a CDFException occurs.
     */
    public static Attribute get(CDF cdf, String name) 
        throws CDFException {

        try {

            gsfc.nssdc.cdf.Attribute attribute = cdf.getAttribute(name);
                                       // specified attribute

            if (attribute != null) {

                return new Attribute(attribute);
            }
        }
        catch (CDFException e) {

            if (e.getCurrentStatus() != CDF.NO_SUCH_ATTR) {

                throw e;
            }
        }

        return null;
    }


    /**
     * Get any global or variable attribute from the given CDF with the
     * specified name.  Note that the name of the returned attribute may
     * differ in case from the specified name since ISTP variable 
     * attribute names are case insensitive.  Also, to be consistent
     * with gsfc.nssdc.cdf classes, the name of the returned attribute
     * may have trailing white space characters.
     *
     * @param cdf the CDF to get the attribute from.
     * @param name name of attribute to get.
     * @return specified variable attribute or null if it does not 
     *     exist.
     */
    public static Attribute getIgnoreCase(CDF cdf, String name) {

        Vector attributes = cdf.getAttributes();
                                       // all attributes

        for (int i = 0; i < attributes.size(); i++) {

            gsfc.nssdc.cdf.Attribute attribute = 
                (gsfc.nssdc.cdf.Attribute)attributes.elementAt(i);
                                       // i'th attribute

            if (name.equalsIgnoreCase(attribute.getName().trim())) {

                return new Attribute(attribute);
            }
        }

        return null;
    }


    /**
     * Gets the name of this attribute.
     *
     * @return name of this attribute.
     */
    public String getName() {

        return attr.getName();
    }

}
