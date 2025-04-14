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
 * $Id: ApplicationProperties.java,v 1.7 2022/03/24 10:38:34 btharris Exp $
 */


package gsfc.spdf.edit.util;


import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.io.InputStream;
import java.io.IOException;

public class ApplicationProperties 
    extends Properties 
{
    private String path;
    private String filename;
    private String description;
    private InputStream in;

    public ApplicationProperties(Properties props) {
	super(props);
    }

    public ApplicationProperties(
        String path, 
        String filename, 
        String description) 
        throws IOException {

	super();

	this.path = path;
	this.filename = filename;
	this.description = description;

	in = ApplicationProperties.class.getResourceAsStream(path+filename);
	load(in);
    }

    @Deprecated
    public Vector getPropertyVector(String key) {
/*
	String propList = getProperty(key);
	StringTokenizer st = new StringTokenizer(propList, ":");
	Vector retVal = new Vector();
	int i = 0;
	while (st.hasMoreTokens())
	    retVal.addElement(st.nextToken());

	return retVal;
*/
        return new Vector(getPropertyValues(key));
    }


    public ArrayList<String> getPropertyValues(
        String key) {

	String propList = getProperty(key);
	StringTokenizer st = new StringTokenizer(propList, ":");
	ArrayList<String> values = new ArrayList<String>();

	while (st.hasMoreTokens()) {

	    values.add(st.nextToken());
        }

	return values;
    }

}
