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
 * $Id: CDFCheck.java,v 1.18 2025/01/23 18:18:19 btharris Exp $
 */
package gsfc.spdf.istp.tools;

import java.io.*;
import java.util.*;
import gsfc.nssdc.cdf.*;
import gsfc.nssdc.cdf.util.*;
import gsfc.spdf.cdf.Cdf;
import gsfc.spdf.istp.*;

/**
 * This class is an implmentation demonstrating the use of the 
 * ISTPCompliance class.
 *
 * It can be used to "Check" the CDF to ensure that it complies with 
 * the ISTP guidelines.  If errors are found with attribute naming 
 * you may try to use <code>CleanAttributes</code> to fix some common 
 * attribute naming mistakes.
 *
 * @author Phil Williams
 * @version $Revision: 1.18 $
 * @see gsfc.spdf.istp.ISTPCompliance
 * @see gsfc.spdf.istp.tools.CleanAttributes
 */
public class CDFCheck {


    /**
     * Gets the set of ISTPCompliance.Warnings that are to be 
     * suppressed from the given properties.
     *
     * @param props system properties.
     * @return set of ISTPCompliance.Warnings that are to be 
     *     suppressed.
     */
    private static EnumSet<ISTPCompliance.Warnings> 
        getSuppressedWarnings(
            Properties props) 
        throws IllegalArgumentException {

        EnumSet<ISTPCompliance.Warnings> warnings =
                EnumSet.noneOf(ISTPCompliance.Warnings.class);
                                       // warnings that are to be 
                                       // suppressed
        String suppress = props.getProperty("suppress");
                                       // suppress property
        if (suppress == null) {

            return warnings;
        }

        StringTokenizer st = new StringTokenizer(suppress, ",");
        while (st.hasMoreTokens()) {

            warnings.add(ISTPCompliance.Warnings.valueOf(
                             st.nextToken().toUpperCase()));
        }

        return warnings;
    }


   /**
    * CDF ISTP compliance checking tool.
    * <DL>
    * <DT><STRONG>Usage:</STRONG><BR></DT>
    * <DD>From the command line:
    * <PRE>
    *   % java gsfc.spdf.istp.tools.CDFCheck &lt;filename&gt;
    * </PRE>
    * Filename may be a single file name or simply a &quot;.&quot; that will 
    * cause all the CDF files in a given directory to be checked.<BR><BR>
    * 
    * Messages will be printed to stdout.<BR>
    * Error messages and status will be printed to stderr.<BR><BR>
    * </DL>
    * 
    * @param argv program arguments (name of files to check)
    */
    public static void main(String [] argv) {
	if (argv.length < 1) {
	    System.err.println("Usage: java [-Dproperty=value] " +
                "gsfc.spdf.istp.tools.CDFCheck <filenames>");
	    System.err.println("\t<filenames> : The files to check.");
	    System.err.println();
	    System.err.println("CDFCheck also supports the following "+
			       "properties: (Use with the -D option)");
	    System.err.println("checks  : A comma separated list of the "+
			       "checks to perform.  The following\n" +
                               "    values are accepted:");
	    System.err.println("\tglobal    : Check that all the required "+
			       "global attributes are present.");
	    System.err.println("\tvariables : Check the DICT_KEY varaible "+
			       "attribute for each variable.");
	    System.err.println("    If not present then both checks are "+
			       "performed.");
	    System.err.println("options : A comma separated list of the "+
			       "options.  The following values "+
			       "\n    are accepted:");
	    System.err.println("\tallowNaN  : option indicating that NaN "+
			       "should be allowed for FILLVAL\n\t    values.");
	    System.err.println("outputStream : Where to send any problems "+
			       "found.  If not present then the\n\tstdOut "+
			       "will be used.");
	    System.err.println("errorStream : Where to report errors.  If "+
			       "not present then the stdErr will\n\tbe used.");
	    System.err.println("suppress : A comma separated list of the "+
			       "warnings that are to be\n" +
                               "\tsuppressed.  The following values "+
			       "are accepted:");
            for (ISTPCompliance.Warnings warning : 
                 ISTPCompliance.Warnings.values()) {

                System.err.println("\t\t" + warning);
            }
	    System.exit(1);
	} else {
	    String anchor, text, scanDir, title, 
		outFilename, errFilename, sCheck, nextCheck, v;
	    Map varErrors = null;
	    Vector errors, globalErrors = null;
	    int nPassed = 0, nFailed = 0, checkMask = 0;
	    
	    Properties props = System.getProperties();
	    outFilename = props.getProperty("outputStream");
	    errFilename = props.getProperty("errorStream");

	    if (outFilename != null) {
		try {
		    FileOutputStream out = new FileOutputStream(outFilename);
		    PrintStream outPrintStream = new PrintStream(out);
		    System.setOut(outPrintStream);
		} catch (IOException e) {
		    System.err.println("Failed to initialize "+outFilename+
				       " for error output.  Using stdOut.");
		}
	    }
	    if (errFilename != null) {
		try {
		    FileOutputStream err = new FileOutputStream(errFilename);
		    PrintStream errPrintStream = new PrintStream(err);
		    System.setErr(errPrintStream);
		} catch (IOException e) {
		    System.err.println("Failed to initialize "+errFilename+
				       " for error output.  Using stdErr.");
		}
	    }

	    boolean checkGlobal = false, checkVariable = false;
	    sCheck      = props.getProperty("checks");
	    // Build the checkmask
	    if (sCheck == null) {
		checkGlobal = true;
		checkVariable = true;
	    } else {
		StringTokenizer st = new StringTokenizer(sCheck, ",");
		while (st.hasMoreTokens()) {
		    nextCheck = st.nextToken();
		    if (nextCheck.equalsIgnoreCase("all")) {
			checkGlobal = true;
			checkVariable = true;
		    }
		    if (nextCheck.equalsIgnoreCase("global"))
			checkGlobal = true;
		    if (nextCheck.equalsIgnoreCase("variables"))
			checkVariable = true;
		}
	    }

            String options = props.getProperty("options");
            if (options != null && 
                options.equalsIgnoreCase("allowNaN")) {

                FillvalAttribute.setUseNaNValues(true);
            }

            EnumSet<ISTPCompliance.Warnings> warnings = null;
                                       // warnings that are to be 
                                       // suppressed
            try {

                warnings = getSuppressedWarnings(props);
            }
            catch (IllegalArgumentException e) {

                System.err.println("Invalid suppress value: " +
                    e.getMessage());
                return;
            }

	    // Setup the title for info output
	    int sepPos = 
		argv[0].lastIndexOf(props.getProperty("file.separator"));
	    if (sepPos >= 0)
		scanDir = argv[0].substring(0, sepPos);
	    else
		scanDir = props.getProperty("user.dir");
	    if (argv.length > 1)
		title = "Check for CDF files in "+scanDir;
	    else
		title = "Check for "+argv[0];
	    System.out.println(title);

	    // Info to the user
	    System.out.println("Checking "+argv.length+" file(s).");

	    for (int i=0;i<argv.length;i++) {

                CDF cdf = null;        // the CDF to check
		try {
		    // Get the filename
		    anchor = argv[i].substring(sepPos+1);

		    // More info to the user
		    System.out.println("Checking "+anchor+"..."+
				       nPassed+" "+nFailed);

                    cdf = CDF.open(argv[i], CDF.READONLYon);

                    System.out.println("CDF File Version: " + 
                        cdf.getVersion());
                    System.out.println("File Last Leap Second: " +
                        Cdf.getLeapSecondLastUpdatedAsString(cdf));
                    System.out.println("Majority: " +
                        Cdf.getMajorityAsString(cdf));

		    
                    VerificationResult result = 
                        new VerificationResult();

		    // Perform the check
		    if (checkGlobal && checkVariable) {

                        result =
                            ISTPCompliance.check(cdf, 
                                warnings,
                                new DefaultVerificationCallback());
                                       // result of compliance 
                                       // verification
			globalErrors = result.getGlobalAttributeErrors();
			varErrors    = result.getVariableErrors();
		    }
                    else if (checkGlobal) {

                        globalErrors = 
                            ISTPCompliance.checkGlobalAttributes(
                                cdf, warnings);
                        result.setGlobalAttributeErrors(globalErrors);
                    }
		    else if (checkVariable) {

			VariablesVerificationResult varResult = 
                            ISTPCompliance.checkVariables(cdf, 
                                warnings,
                                new DefaultVerificationCallback());

                        varErrors = varResult.getErrors();
                        result.setVariableErrors(varResult.getErrors());
                    }
                    boolean failed = false;
                    if (result.hasGlobalAttributeErrors()) {

                        failed = true;
                    }
                    else if (checkGlobal) {

			System.out.println(" PASSED global attribute check.");
                    }
                    if (result.hasVariableErrors()) {

                        failed = true;
                    }
                    else if (checkVariable) {

			System.out.println(" PASSED variable checks.");
                    }
                    result.printErrors(System.out);
		    
		    if (failed)
			nFailed++;
		    else {
			nPassed++;
		    }
		} catch (CDFException e1) {
		    System.out.println(" FAILED due to "+e1);
                    e1.printStackTrace();
		} 
                finally {

                    if (cdf != null) {

                        try {

                            cdf.close();
                        }
                        catch (CDFException e) {

                            e.printStackTrace();
                        }
                    }
                }
	    }

	    if (argv.length > 1)
		System.out.println("nPassed = "+nPassed+" nFailed = "+nFailed);
	}
    }
}
	    
	    
