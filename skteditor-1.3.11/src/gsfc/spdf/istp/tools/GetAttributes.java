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
 * $Id: GetAttributes.java,v 1.7 2022/03/24 10:38:41 btharris Exp $
 */
package gsfc.spdf.istp.tools;

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

import gsfc.nssdc.cdf.*;
import gsfc.spdf.util.TextUtils;

/**
 * This is a short java application that will scan the given 
 * CDF for the &quot;TEXT&quot; global attribute
 * and each variables &quot;VAR_NOTES&quot; attribute and will 
 * output a HTML file.<BR><BR>
 *
 * The arguments are the file(s) to scan.
 *
 * @author Phil Williams
 * @version $Revision: 1.7 $
 */
public class GetAttributes {

    public static void main(String [] argv) {
	if (argv.length < 1) {
	    System.err.println("Usage: java GetNotes <pathname> "+
			       "[<arguments>]");
	    System.err.println("\t<pathname> : The file(s) to check.");
	    System.err.println();
	    System.err.println("GetNotes also supports the following "+
			       "Java properties:");
	    System.err.println("  -Dattributes=cdfAttrName1,cdfAttrName2,...");
	    System.err.println("outputStream : Where to send any problems "+
			       "found.  If not present then the stdOut "+
			       "will be used.");
	    System.err.println("errorStream : Where to report errors.  If "+
			       "not present then the stdErr will be used.");
	    System.exit(1);
	} else {
	    Properties props = System.getProperties();

	    // Get the attribute names
	    String [] attrNames = null;
	    String attrs = props.getProperty("attributes");
	    if (attrs == null) {
		System.err.println("Must specify attribute names.");
		System.exit(1);
	    }
	    StringTokenizer st = new StringTokenizer(attrs, ",");
	    int count = st.countTokens(),k = 0;
	    System.out.println(attrs+" st has "+count+" tokens");
	    attrNames = new String [count];
	    while (st.hasMoreTokens()) {
		attrNames[k] = st.nextToken();
		k++;
	    }

	    CDF cdf = null;
	    Variable v;
	    Attribute a;
	    Object data;
	    int numRead, curEntryID;
	    String entry;
	    char [] spaces;
	    int numSpaces;

	    // Process each file
	    for (int i=0;i<argv.length;i++) {
		try {
		    System.err.println("Getting attributes for "+
				       argv[i]+"...");
		    System.out.println("Getting attributes for "+
				       argv[i]+"...");
		    cdf = CDF.open(argv[i], CDFConstants.READONLYon);

		    // Get the logical source if any
		    for (int j=0;j<attrNames.length;j++) {
			try {
			    a = cdf.getAttribute(attrNames[j]);
			    numRead = curEntryID = 0;
			    System.out.println(a.getName()+" entries:");
			    while (numRead < a.getNumEntries()) {
				try {
				    data = a.getEntry(curEntryID).getData();
				    if (a.getScope() == CDFConstants.GLOBAL_SCOPE)
					entry = curEntryID+"";
				    else
					entry = cdf.getVariable(curEntryID).getName();
				    numSpaces = 30-entry.length();
				    if (numSpaces > 0)
					spaces = new char [numSpaces];
				    else
					spaces = new char [1];
				    for (int l=0;l<spaces.length;l++)
					spaces[l] = ' ';
				    System.out.println("\t"+entry+new String(spaces)+"\""+(String)a.getEntry(curEntryID).getData()+"\"");
				    numRead++;
				} catch (CDFException exc2) {
				    // ignore it and try the next one
				}
				curEntryID++;
			    }
			} catch (CDFException exc1) {
			    System.err.println(attrNames[j] +" not found.");
			    // ignore it
			}
		    }
		} catch (CDFException e) {
		    System.err.println("failed for "+argv[i]);
		}
	    }
	}
    }
}	    
