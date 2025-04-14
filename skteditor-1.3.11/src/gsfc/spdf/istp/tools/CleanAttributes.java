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
 * $Id: CleanAttributes.java,v 1.8 2024/10/28 12:49:26 btharris Exp $
 */
package gsfc.spdf.istp.tools;

import java.io.*;
import java.util.*;
import gsfc.nssdc.cdf.*;

import gsfc.spdf.istp.*;

/**
 * This class is an implmentation demonstrating the use of the ISTPCompliance
 * class.<BR><BR>
 *
 * It can be used to "Clean" the global and variable attribute names to 
 * comply with the ISTP guidelines.  This will fix the case of the attribute 
 * names only.  Missing or misspelled attributes will not be cleaned.<BR><BR>
 *
 * Together with <code>CDFCheck</code> this class can assist in bringing a 
 * given CDF into ISTP-compliance.<BR><BR>
 *
 * <DL>
 * <DT><STRONG>Usage:</STRONG><BR></DT>
 * <DD>From the command line:
 * <PRE>
 *   % java gsfc.spdf.istp.tools.CleanAttributes &lt;filename&gt;
 * </PRE>
 * Filename may be a single file name or simply a &quot;.&quot; that will 
 * cause all the CDF files in a given directory to be cleaned.<BR><BR>
 * 
 * Messages as to the cleaning that was performed will be printed to stdout.<BR>
 * Error messages and status will be printed to stderr.<BR><BR>
 *
 * This aids in capturing messages to a file.
 * </DL>
 *
 * @author Phil Williams
 * @version $Revision: 1.8 $
 * @see gsfc.spdf.istp.ISTPCompliance
 * @see gsfc.spdf.istp.tools.CDFCheck
 */
public final class CleanAttributes {

    public static void main(String [] argv) {
	if (argv.length < 1) {
	    System.err.println("Usage: java CleanAttributes <pathname>");
	    System.err.println("\t<pathname> : The files(s) to clean.");
	    System.exit(1);
	} else {
	    CDF cdf = null;
	    Vector changes = null;
	    
	    for (int i=0;i<argv.length;i++) { 
		try {
		    System.err.print("Cleaning file: "+argv[i]+"...");
		    cdf = CDF.open(argv[i], CDFConstants.READONLYoff);
		    changes = ISTPCompliance.cleanAttributeNames(cdf);
		    System.err.println("done.");
		    cdf.close();
		    if (changes.size() > 0) {
			System.out.println("Changes made to "+argv[i]+":");
			for (int j = 0; j < changes.size(); j++)
			    System.out.println(changes.elementAt(j));
		    }
		} catch (CDFException exc) {
		    System.err.println("failed.");
		    System.err.println("Unable to clean "+argv[i]+
				       " due to:  "+exc.getMessage());
		}
	    }
	}
    }
}
