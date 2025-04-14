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
 * Copyright (c) 2011-2023 United States Government as represented by 
 * the National Aeronautics and Space Administration. No copyright is 
 * claimed in the United States under Title 17, U.S.Code. All Other 
 * Rights Reserved.
 *
 * $Id: CDFTools.java,v 1.37 2024/10/28 12:58:05 btharris Exp $
 */

package gsfc.spdf.cdf.tools;

import java.lang.*;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.bind.DatatypeConverter;

import javax.xml.namespace.NamespaceContext;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.XMLConstants;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import gsfc.nssdc.cdf.*;
import gsfc.nssdc.cdf.util.*;


/**
 * This class is a wrapper for the "CDF Tools."
 * 
 * Notes:
 * - The class does not currently provide an interface to all tools
 * - This class exists in this package because those maintaining CDF (i.e., the 
 *   gsfc.nssdc withdrew the class from the gsfc.nssdc.cdf.util and 
 *   gsfc.nssdc.cdf.tools packages.  This occurred in April 2000 after the issue
 *   of supporting the class on non-Unix platforms was raised.
 * - All though it is highly discouraged, the class' behavior can be influence 
 *   through the follow system properties:
 *   cdf.base = root directory for the native CDF installation
 *   cdf.bin = bin directory for the native CDF installation
 *   If neither of these is set, the native CDF tools must be in the user's 
 *   path.  These are not required facilitate the packaging of applications 
 *   in "executable jar" files and to avoid embedding knowledge of CDF 
 *   installation details in this class.
 * 
 * @author Phil Williams
 * @version $Id: CDFTools.java,v 1.37 2024/10/28 12:58:05 btharris Exp $
 */

public class CDFTools implements CDFConstants {
    

    /**
     * User's home directory.
     */
    private static String userHome = null;

    /**
     * Platform's file separator.
     */
    private static String separator = null;

    /**
     * Directory path to the native CDF Tools.
     */
    private static String cmdBase   = null;

    /**
     * Platform's LD_LIBRARY_PATH name.
     */
    private static String ldLibraryPathName = null;

    /**
     * LD_LIBRARY_PATH value.
     */
    private static String ldLibraryPathValue = null;

    /**
     * Runtime Operating System name.
     */
    private static String osName = null;

    /**
     * Name of skeleton table utility.
     */
    private static String skeletonTable = "skeletontable";

    /**
     * Name of skeleton CDF utility.
     */
    private static String skeletonCdf = "skeletoncdf";

    /**
     * Name of netCDF-to-cdf translation utility.
     */
    private static String netCdfToCdf = "netCDF-to-cdf";

    /**
     * Name of cdf-to-netCDF translation utility.
     */
    private static String cdfToNetCdf = "cdf-to-netCDF";

    /**
     * Names of the cdf utilities/tools.
     */
    private static String[] tools = {
        skeletonTable, skeletonCdf, netCdfToCdf, cdfToNetCdf
    };

    /**
     * Flag indicating whether debug information is to generated.
     */
    private static boolean debug = false;

    /**
     * Name of file containing hash values of tools.
     */
private static String hashesFile = null;

    /**
     * Message digest algorithm to use in verifying the tools.
     */
private static String messageDigestAlgorithm = "SHA-256";


    static {

        userHome = System.getProperty("user.home");
        separator = System.getProperty("file.separator");

        String cdfBase = System.getProperty("cdf.base");
        if (cdfBase == null) {

            cdfBase = System.getProperty("jnlp.cdf.base");
        }
        if (cdfBase != null) {

            cdfBase = findCdfBase(Paths.get(cdfBase));
        }
        String cdfBin = System.getProperty("cdf.bin");
        if (cdfBin == null) {

            cdfBin = System.getProperty("jnlp.cdf.bin");
        }
        String cdfLib = System.getProperty("cdf.lib");
        if (cdfLib == null) {

            cdfLib = System.getProperty("jnlp.cdf.lib");
        }
        String javaVersion = System.getProperty("java.vm.version");
                                       // version of Java being used

        if (cdfBase == null && javaVersion.compareTo("1.5") >= 0) {

            cdfBase = System.getenv("CDF_BASE");
        }

        if (cdfBin == null && javaVersion.compareTo("1.5") >= 0) {

            cdfBin = System.getenv("CDF_BIN");
        }

        if (cdfLib == null && javaVersion.compareTo("1.5") >= 0) {

            cdfLib = System.getenv("CDF_LIB");
        }

        if (cdfBase != null && selectPath(cdfBase) != null) {

            cdfBase = selectPath(cdfBase);

            cmdBase = cdfBase + separator + "bin" + separator;
        }
        else if (cdfBin != null) {

            cmdBase = cdfBin + separator;
        }
        else {

            //
            // Hopefully the CDF Tools are in the user's PATH
            //
            cmdBase = "";
        }
            
        osName = System.getProperty("os.name");

        if (osName.toLowerCase().startsWith("windows")) {

            ldLibraryPathName = "PATH";
        }
        else if (osName.toLowerCase().startsWith("mac os x")) {

            ldLibraryPathName = "DYLD_LIBRARY_PATH";
        }
        else {

            ldLibraryPathName = "LD_LIBRARY_PATH";
        }

        ldLibraryPathValue = System.getenv(ldLibraryPathName);

        if (cdfLib != null) {

            if (ldLibraryPathValue != null) {

                if (!ldLibraryPathValue.contains(cdfLib)) {

                    ldLibraryPathValue = cdfLib + ":" + 
                        ldLibraryPathValue;
                }
            }
            else {

                ldLibraryPathValue = cdfLib;
            }
        }
        else if (cdfBase != null) {

            if (ldLibraryPathValue != null) {

                if (!ldLibraryPathValue.contains(cdfBase)) {

                    ldLibraryPathValue = cdfBase + "/lib:" + 
                        ldLibraryPathValue;
                }
            }
            else {

                ldLibraryPathValue = cdfBase + "/lib";
            }
        }

        String cdfDebug = System.getProperty("skteditor.cdf.debug");

        if (cdfDebug != null && !cdfDebug.equalsIgnoreCase("false")) {

            debug = true;
        }


String resourcesPath = System.getProperty("spdf.resources");
if (resourcesPath == null) {

    resourcesPath = "/resources/";
}
else {

    resourcesPath += separator;
}
hashesFile = resourcesPath + "CdfUtilityHashes.xml";
    }


    /**
     * Attempts to find the base path of the most recent version CDF
     * installation given a path to start with.
     *
     * @param path path to start search from.
     * @return the base path of the most recent version CDF 
     *     installation or null if not found.
     */
    private static String findCdfBase(
        Path path) {

        List<Path> paths = new ArrayList<>();

        try {

            paths = findByFileName(path, "bin");
        }   
        catch (IOException e) {

            try {

                paths = findByFileName(
                            Paths.get(userHome + path), "bin");
            }   
            catch (IOException e0) {

                // continue with empty result
            }   
        }   
        Collections.sort(paths);  // latest version last

        if (paths.size() > 0) {

            return paths.get(paths.size() -1).getParent().toString();
        }
        else {

            return null;
        }
    }


    /**
     * Attempts to find the specified file searching from the given path.
     *
     * @param path path from which to start search.
     * @param fileName name of file/directory to search for
     * @return List of Paths found.
     * @throws IOException if an I/O exception occurs.
     */
    public static List<Path> findByFileName(
        Path path,
        String fileName)
    throws IOException {

        List<Path> result;
        try (Stream<Path> pathStream =
                 Files.find(path, Integer.MAX_VALUE,
                     (p, basicFileAttributes) ->
                         p.getFileName().toString().equalsIgnoreCase(fileName))
        ) {
            result = pathStream.collect(Collectors.toList());
        }
        return result;
    }


    /**
     * Selects the first readable path from a list of paths.
     *
     * @param paths list of paths.
     * @return the first readable path from the given list.  null if
     *     no readable path is found.
     */
    private static String selectPath(
        String paths) {

        if (paths == null) {

            return null;
        }
        String pathSeparator = System.getProperty("path.separator");
        String userHome = System.getProperty("user.home");

        for (String path : paths.split(pathSeparator)) {

            if (path.startsWith("~")) {

                path = path.replaceFirst("~", userHome);
            }
            File pathFile = new File(path);

            if (pathFile.canRead()) {

                return path;
            }
        }
        return null;
    }


    public static final int NO_VALUES     = 0;
    public static final int NRV_VALUES    = 1;
    public static final int RV_VALUES     = 2;
    public static final int ALL_VALUES    = 3;
    public static final int NAMED_VALUES   = 4;

    public static final int NO_REPORTS    = 0;
    public static final int REPORT_ERRORS = 1;
    public static final int REPORT_WARNINGS = 2;
    public static final int REPORT_INFORMATION = 4;


    /**
     * Gets the version of the skeletontable tool.
     *
     * @return version of skeletontool
     */
    public static String getSkeletonTableVersion() {

        return getToolVersion(skeletonTable);
    }


    /**
     * Gets the version of the skeletoncdf tool.
     *
     * @return version of skeletoncdf
     */
    public static String getSkeletonCdfVersion() {

        return getToolVersion(skeletonCdf);
    }


    /**
     * Regex pattern to capture the version number from the "-about"
     * option to a CDF tool.
     */
    private static final Pattern VERSION_PATTERN =
        Pattern.compile(".* V(\\d+(?:[\\._]\\w+)*) .*");


    /**
     * Gets the version of the specified tool.
     *
     * @param name name of tool
     * @return version of specified tool or null if it cannot be
     *     determined
     */
    public static String getToolVersion(String name) {

        String version = null;         // tool's version
        BufferedReader output = null;  // tool output reader

        try {

            List<String> cmdList = new ArrayList<>();
                                       // command argument list
            cmdList.add(cmdBase + name);
            cmdList.add("-about");

            ProcessBuilder pb = new ProcessBuilder(cmdList);
                                       // tool ProcessBuilder

            setCdfLibraryPath(pb);

            Process process = pb.redirectErrorStream(true).start();
                                       // tool process
            int status = process.waitFor();
                                       // process exit status

            output =
                new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line = output.readLine();
                                       // line of output from process
            if (line != null) {

                Matcher verMatcher = VERSION_PATTERN.matcher(line);
                                       // version matcher
                if (verMatcher.matches() && 
                    verMatcher.groupCount() == 1) {

                    version = verMatcher.group(1);
                }
                else {

                    System.err.println(
                        "Unrecognized version returned by " +
                        name + " : " + line);
                }
            }
            else {

                System.err.println("Error getting version of " + name);
                System.err.println("Status = " + status);

                BufferedReader error = null;
                                       // tool error reader
                try {

                    error = new BufferedReader(
                                new InputStreamReader(
                                    process.getErrorStream()));

                    while ((line = error.readLine()) != null) {

                        System.err.println(line);
                    }
                }
                finally {

                    error.close();
                }
            }
        }
        catch (IOException e) {

            System.err.println(
                "IOException while getting version of " + 
                name + " : " + e.getMessage());
        }
        catch (InterruptedException e) {

            System.err.println(
                "InterruptedException while getting version " +
                "of " + name + " : " + e.getMessage());
        }
        finally {

            if (output != null) {

                try {

                    output.close();
                }
                catch (IOException e) {

                }
            }
        }

        return version;
    }


    /**
     * This class represents an expression of the integrity of
     * the CDF utilities.
     */
    public static class Integrity {

        /**
         * A descripton of the integrity of the CDF utility programs.
         */
        public enum Status {
            GOOD, UNKNOWN, BAD
        }

        /**
         * Overall integrity assesment of the integrity of the
         * CDF utility programs.
         */
        public Status status = Status.BAD;

        /**
         * When status != GOOD, a textual description of integrity
         * issues.  The string is an HTML fragment.  That is, it
         * may contain embedded tags such as br, ul, and li but it
         * does not have leading and trailing html elements.
         */
        public String description = "";


        /**
         * Creates an Integrity object with the given values.
         *
         * @param status initial status value.
         * @param description initial description value.
         */
        public Integrity(Status status, String description) {

            this.status = status;
            this.description = description;
        }
    }


    /**
     * Determines the integrity of the CDF utility programs.  The
     * integrity is determined by computing the a cryptographic hash
     * value for each program and comparing it to the known valid
     * value.
     *
     * @return indication of the integrity of the CDF utility programs.
     */
    public static Integrity getIntegrity() {

        InputStream hashesStream = 
            CDFTools.class.getResourceAsStream(hashesFile);
                                       // InputStream containg valid
                                       // hash value document.
        if (hashesStream == null) {

            return new Integrity(Integrity.Status.UNKNOWN,
                           "Unable to verify the integrity of the " +
                           "CDF tools because the<br>" +
                           hashesFile + " file was not found.<br>");
        }

        Document hashes = null;        // document containg valid hash 
                                       // values.
        try {

            DocumentBuilderFactory dbf = 
                DocumentBuilderFactory.newInstance();
                                       // document builder factory

           // There is no XXE concern since the document is from a
           // trusted source.
           // dbf.setXIncludeAware(false);
           // dbf.setExpandEntityReferences(false);

            DocumentBuilder db = dbf.newDocumentBuilder();
                                       // document builder

            hashes = db.parse(hashesStream);
        }
        catch(ParserConfigurationException e) {

            return new Integrity(Integrity.Status.UNKNOWN,
                           "Unable to verify the integrity of the " +
                           "CDF tools because of<br> an XML parser " +
                           "configuration error: <br>" +
                           e.getMessage() + "<br>");
        }
        catch(SAXException e) {

            return new Integrity(Integrity.Status.UNKNOWN,
                           "Unable to verify the integrity of the " +
                           "CDF tools because of<br> an XML parsing" +
                           "error: <br>" + e.getMessage() + "<br>");
        }
        catch(IOException e) {

            return new Integrity(Integrity.Status.UNKNOWN,
                           "Unable to verify the integrity of the " +
                           "CDF tools because of<br> an XML parsing" +
                           "error: <br>" + e.getMessage() + "<br>");
        }

        Integrity integrity = new Integrity(Integrity.Status.GOOD, "");
                                       // initial Integrity value
        String version = getToolVersion(tools[0]);
                                       // tool version
        XPath xPath = XPathFactory.newInstance().newXPath();
                                       // xpath used to search hash
                                       // value document
//    xPath.setNamespaceContext(new CdfUtilityHashNamespaceContext ());

        for (String tool : tools) {

            String toolHashExpression = 
                "//CdfUtilityHash[@Name='" + tool + "']" +
                "[@Version='" + version + "']" +
                "[starts-with(@OsName, '" + osName + "')]" +
                "[@MessageDigestAlgorithm='" + 
                messageDigestAlgorithm + "']";
                                       // xpath expression to find
                                       // cdf tool's hash value
            try {

System.err.println("*** evaluating " + toolHashExpression);

                NodeList nodes = (NodeList) 
                    xPath.evaluate(toolHashExpression, hashes, 
                        XPathConstants.NODESET);
                                       // nodes containing valid
                                       // hash value for this tool
                if (nodes.getLength() == 0 && 
                    !osName.startsWith("Windows") &&
                    !osName.startsWith("Mac OS")) {

                    // Is there a better way to deal with non-windows
                    // and non-osx platforms for which cdf tool binaries
                    // are not distributed (so there is no hash values)?

                    toolHashExpression = 
                        "//CdfUtilityHash[@Name='" + tool + "']" +
                        "[@Version='" + version + "']" +
                        "[@OsName='*']" +
                        "[@MessageDigestAlgorithm='" + 
                        messageDigestAlgorithm + "']";
System.err.println("*** evaluating " + toolHashExpression);
                    nodes = (NodeList) 
                        xPath.evaluate(toolHashExpression, hashes, 
                            XPathConstants.NODESET);
                }
                if (nodes.getLength() == 0) {

                    integrity.status = Integrity.Status.UNKNOWN;

                    integrity.description +=
                        "Unable to verify the integrity of the " + 
                        tool + " CDF tool because<br>no " + 
                        messageDigestAlgorithm +
                        " value was found for version " + version + 
                        " on " + osName + ".<br>";
                }
                else {

                    boolean hashValuesMatch = false;
                                       // flag indicating whether
                                       // the hash values match
                    String toolHash = 
                        getToolDigestStr(tool, messageDigestAlgorithm);
                                       // tool's actual hash value

                    for (int i = 0; i < nodes.getLength(); i++) {
    
                        String hashValue = 
                            nodes.item(i).getTextContent();
                                        // valid tool's hash value

                        if (hashValue.equalsIgnoreCase(toolHash) ||
                            hashValue.length() == 0) {

                            hashValuesMatch = true;
                            break;
                        }
                    }

                    if (!hashValuesMatch) {

                        integrity.status = Integrity.Status.BAD;

                        integrity.description +=
                            "Your " + tool + " CDF tool does not have " +
                            "a " + messageDigestAlgorithm + 
                            " signature matching any known values.<br>";
/*
                            tool + "'s " + messageDigestAlgorithm +
                            " value of<br>" + toolHash + 
                            "<br>does not match any of<br>";

                        for (int i = 0; i < nodes.getLength(); i++) {

                            integrity.description +=
                                nodes.item(i).getTextContent() + "<br>";
                        }
*/
                    }
                }
            }
            catch (XPathExpressionException e) {

                System.err.println("verifyToolsIntegrity: " +
                    toolHashExpression + " caused exception: " +
                    e.getMessage());
            }
        }

        return integrity;
    }


    /**
     * Gets the message digest of the specified tool.
     *
     * @param name name of tool.
     * @param digest message digest algorithm.
     * @return message digest of specified tool or null if it cannot be
     *     determined.
     */
    public static byte[] getToolDigest(
        String name, 
        MessageDigest digest) {

        try {

            DigestInputStream dStrm = 
                new DigestInputStream(
                        new FileInputStream(cmdBase + name), digest);

            byte[] buf = new byte[8192];

            while(dStrm.read(buf, 0, buf.length) != -1);
        }
        catch (FileNotFoundException e) {

            return new byte[] {};
        }
        catch (IOException e) {

            return new byte[] {};
        }

        return digest.digest();
    }
        

    /**
     * Gets the message digest of the tool.
     *
     * @param name name of tool.
     * @param algorithm name of MessageDigest algorithm.
     * @return the message digest of tool.
     */
    public static byte[] getToolDigest(
        String name, String algorithm) {

        if (osName.toLowerCase().startsWith("windows")) {

            name += ".exe";
        }
        try {

            MessageDigest md = MessageDigest.getInstance(algorithm);
                                       // message digest
            return CDFTools.getToolDigest(name, md);
        }
        catch (NoSuchAlgorithmException e) {

            return new byte[] {};
        }
    }


    /**
     * Gets the String representation of message digest of the tool.
     *
     * @param name name of tool.
     * @param algorithm name of MessageDigest algorithm.
     * @return the String representation of the message digest of the
     *     tool.
     */
    public static String getToolDigestStr(
        String name, String algorithm) {

        try {

            return DatatypeConverter.printHexBinary(
                       getToolDigest(name, algorithm));
        }
        catch (IllegalArgumentException e) {

            return "";
        }
    }



    /**
     * skeletonTable produces a skeleton table from a CDF.  A
     * skeleton table is a text file which can be read by the
     * SkeletonCDF program to build a skeleton CDF.
     *
     * @param cdfName The pathname of the CDF from which the skeleton table
     *                will be created.  Do not enter an extension.<BR><BR>
     *
     * @param skeletonName is the pathname of the skeleton table
     *            to be created.  (Do not enter an extension because ".skt"
     *            is appended automatically).  If <B>null</B> is specified, the
     *            skeleton table is named <i>cdfName</i>.skt in the current
     *            directory<BR><BR>
     *
     * @param values Specifies which variable values are to
     *           be put in the skeleton table.  It may be one of
     *           the following...<BR><BR>
     *           <DL>
     *              <DT>CDFTools.NO_VALUES
     *                 <DD>Ignore all NRV data values.
     *              <DT>CDFTools.NRV_VALUES
     *                 <DD>Put NRV data values in the skeleton table.
     *              <DT>CDFTools.RV_VALUES
     *                 <DD>Put RV variable values in the skeleton table.
     *               <DT>CDFTools.ALL_VALUES
     *                 <DD>Put all variable values in the skeleton table.
     *               <DT>CDFTools.NAMED_VALUES
     *                  <DD>Put named variables values in the skeleton table.
     *                      This requires that valueList be non-null
     *           </DL><BR><BR>
     *
     * @param valueList the named variables to list values.<BR><BR>
     *
     * @param log   Specifies whether or not messages are displayed as the
     *             program executes.<BR><BR>
     *
     * @param zMode Specifies which zMode should be used.  May be one
     *            of the following...<BR><BR>
     *
     *               <DL>
     *               <DT>0
     *                 <DD>Indicates that zMode is disabled.
     *               <DT>1
     *                  <DD>Indicates that zMode/1 should be used (the
     *                       dimension variances of rVariables will be
     *                       preserved).
     *               <DT>2
     *                  <DD>Indicates that zMode/2 should be used (the
     *                       dimensions of rVariables having a variance
     *                       of NOVARY (false) are hidden.
     *               </DL><BR><BR>
     *
     * @param format Specifies whether or not the FORMAT attribute is used
     *            when writing variable values (if the FORMAT attribute
     *            exists and an entry exists for the variable).<BR><BR>
     *
     * @param neg2posfp0
     *            Specifies whether or not -0.0 is converted to 0.0 by the CDF
     *            library when read from a CDF.  -0.0 is an illegal floating
     *            point value on VAXes and DEC Alphas running OpenVMS.<BR><BR>
     *
     * @param reportType Specifies the types of return status codes from the CDF
     *            library which should be reported/displayed.  <code>report</code>
     *            is a bit mask made up from the following
     *            CDFTools.NO_REPORTS, CDFTools.REPORT_ERRORS, 
     *            CDFTools.REPORT_WARNINGS and CDFTools.REPORT_INFORMATION<BR><BR>
     *
     * @param cacheSize The number of 512-byte buffers to be used for the CDF's
     *            dotCDF file, staging file, and compression scratch file.
     *            If this qualifier is absent, default cache sizes chosen by
     *            the CDF library are used.  The cache sizes are specified
     *            with a comma-separated list of <i>number</i><i>type</i> pairs
     *            where <i>number</i> is the number of cache buffers and 
     *            <i>type</i> is the
     *            type of file.  The file <i>type</i>'s are as follows: `d' for
     *            the dotCDF file, `s' for the staging file, and `c' for the
     *            compression scratch file.  For example, `200d,100s'
     *            specifies 200 cache buffers for the dotCDF file and 100
     *            cache buffers for the staging file.  The dotCDF file cache
     *            size can also be specified without the `d' <i>type</i> for
     *            compatibility with older CDF releases (eg. `200,100s').
     *            Note that not all of the file types must be specified.
     *            Those not specified will receive a default cache size.<BR><BR>
     *
     * @param statistics Specifies whether or not caching statistics
     *            are displayed at the end of each CDF.<BR><BR>
     *
     * @param screen Specifies whether or not the skeleton table is displayed
     *            on the terminal screen (written to the "standard output").
     *            If not, the skeleton table is written to a file.<BR><BR>
     *
     * @param page If the skeleton table is being displayed on the terminal
     *            screen, specifies whether or not the output is displayed
     *            one page (screen) at a time.<BR><BR>
     *
     * @exception java.io.IOException if there is a problem opening the 
     *    source file or creating the destination file
     * @exception InterruptedException if file opening/creating was 
     *    interrupted by the user.
     */
    public static void skeletonTable(String skeletonName,
				     String cdfName,
				     boolean log,
				     boolean format,
				     boolean neg2posfp0,
				     boolean statistics,
				     boolean screen,
				     boolean page,
				     int values,
				     String [] valueList,
				     int zMode,
				     int reportType,
				     String cacheSize)
	throws java.io.IOException, InterruptedException
    {

        List<String> cmdList = new ArrayList<>();
                                       // command argument list
        cmdList.add(cmdBase + skeletonTable);

	if (skeletonName != null) {

            cmdList.add("-skeleton");
            cmdList.add(skeletonName);
	}

	switch (values) {
	case NO_VALUES:
	    cmdList.add("-values");
	    cmdList.add("none");
	    break;
	case NRV_VALUES:
	    cmdList.add("-values");
	    cmdList.add("nrv");
	    break;
	case RV_VALUES:
	    cmdList.add("-values");
	    cmdList.add("rv");
	    break;
	case ALL_VALUES:
	    cmdList.add("-values");
	    cmdList.add("all");
	    break;
	case NAMED_VALUES: {

                cmdList.add("-values");
                StringBuffer valueBuf = new StringBuffer();
                                       // buffer inwhich to construct 
                                       // comma separated values
                for (int i=0; i < valueList.length; i++) {

                    valueBuf.append(valueList[i]+",");
                }

                cmdList.add(
                    valueBuf.substring(0, valueBuf.length() - 1));
            }
	    break;
	}

        if (log) {

            cmdList.add("-log");
        }
        else {

            cmdList.add("-nolog");
        }
	    
	cmdList.add("-zmode");
        cmdList.add(zMode + "");

        if (format) {

            cmdList.add("-format");
        }
        else {

            cmdList.add("-noformat");
        }

        if (neg2posfp0) {

            cmdList.add("-neg2posfp0");
        }
        else {

            cmdList.add("-noneg2posfp0");
        }

        if (reportType != 0) {

	    cmdList.add("-report");

            StringBuffer reportBuf = new StringBuffer();
                                       // buffer inwhich to build
                                       // report options
            if ((reportType & REPORT_ERRORS) != 0) {

                reportBuf.append("errors, ");
            }
            if ((reportType & REPORT_WARNINGS) != 0) {

                reportBuf.append("warnings, ");
            }
            if ((reportType & REPORT_INFORMATION) != 0) {

                reportBuf.append("informationals, ");
            }

            cmdList.add(
                reportBuf.substring(0, reportBuf.length() - 2));
	}

        if (cacheSize != null) {

            cmdList.add("-cache");
            cmdList.add(cacheSize + "");
	}

        if (statistics) {

            cmdList.add("-statistics");
        }
        else {

            cmdList.add("-nostatistics");
        }

        if (screen) {

            cmdList.add("-screen");
        }
        else {

            cmdList.add("-noscreen");
        }

        if (page) {

            cmdList.add("-page");
        }
        else {

            cmdList.add("-nopage");
        }
	
        cmdList.add(cdfName);

        ProcessBuilder pb = new ProcessBuilder(cmdList);
                                       // skeletontable ProcessBuilder
        setCdfLibraryPath(pb);

        debug(pb);

        Process p = pb.redirectErrorStream(true).start();
                                       // skeletontable Process

	BufferedReader inStream = null;
        try {

            inStream = new BufferedReader(
                           new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = inStream.readLine()) != null) {

                System.err.println("skeletontable: " + line);
	    }
        }
        finally {

            if (inStream != null) {

                inStream.close();
            }
        }
	int retcode = p.waitFor();

    }


    /**
     * Regex pattern to find a leap second table error in the output
     * from the skeletontable CDF tool.
     */
    private static final Pattern BAD_LS_PATTERN =
        Pattern.compile(".*BADDATE_LEAPSECOND_UPDATED.*");


    /**
     * skeletonTable produces a skeleton table from a CDF.  A
     * skeleton table is a text file which can be read by the
     * SkeletonCDF program to build a skeleton CDF.
     *
     * @param cdfName The pathname of the CDF from which the skeleton table
     *                will be created.  Do not enter an extension.
     * @param skeletonName is the pathname of the skeleton table
     *            to be created.  (Do not enter an extension because ".skt"
     *            is appended automatically).  If <code>null</code> is 
     *            specified, the skeleton table is named cdfName.skt in the 
     *            current directory.
     * @param delete specifies whether or not the CDF should be deleted
     *               if it already exists.
     * @param log   Specifies whether or not messages are displayed as the
     *             program executes.
     * @param zMode Specifies which zMode should be used.  May be one
     *            of the following:
     *
     *               <DL>
     *               <DT>0
     *                 <DD>Indicates that zMode is disabled.
     *               <DT>1
     *                  <DD>Indicates that zMode/1 should be used (the
     *                       dimension variances of rVariables will be
     *                       preserved).
     *               <DT>2
     *                  <DD>Indicates that zMode/2 should be used (the
     *                       dimensions of rVariables having a variance
     *                       of NOVARY (false) are hidden.
     *               </DL>
     *
     * @param neg2posfp0
     *            Specifies whether or not -0.0 is converted to 0.0 by the CDF
     *            library when read from a CDF.  -0.0 is an illegal floating
     *            point value on VAXes and DEC Alphas running OpenVMS.
     * @param reportType Specifies the types of return status codes from the CDF
     *            library which should be reported/displayed.  <code>report</code>
     *            is a bit mask made up from the following
     *            CDFTools.NO_REPORTS, CDFTools.REPORT_ERRORS, 
     *            CDFTools.REPORT_WARNINGS and CDFTools.REPORT_INFORMATION
     * @param cacheSize The number of 512-byte buffers to be used for the CDF's
     *            dotCDF file, staging file, and compression scratch file.
     *            If this qualifier is absent, default cache sizes chosen by
     *            the CDF library are used.  The cache sizes are specified
     *            with a comma-separated list of <i>number</i><i>type</i> 
     *            pairs where <i>number</i> is the number of cache buffers
     *            and <i>type</i> is the type of file.  The file 
     *            <i>type</i>'s are as follows: `d' for
     *            the dotCDF file, `s' for the staging file, and `c' for the
     *            compression scratch file.  For example, `200d,100s'
     *            specifies 200 cache buffers for the dotCDF file and 100
     *            cache buffers for the staging file.  The dotCDF file cache
     *            size can also be specified without the `d' <i>type</i> for
     *            compatibility with older CDF releases (eg. `200,100s').
     *            Note that not all of the file types must be specified.
     *            Those not specified will receive a default cache size.
     * @param statistics Specifies whether or not caching statistics
     *            are displayed at the end of each CDF.<BR><BR>
     * @throws CDFException if a CDF error occurs.
     * @throws IOException if there is a problem opening the 
     *    source file or creating the destination file
     * @throws InterruptedException if file opening/creating was 
     *    interrupted by the user.
     */
    public static void skeletonCDF(String skeletonName,
				   String cdfName,
				   boolean delete,
				   boolean log,
				   boolean neg2posfp0,
				   boolean statistics,
				   int zMode,
				   int reportType,
				   String cacheSize)
	throws CDFException, IOException, InterruptedException {

        List<String> cmdList = new ArrayList<>();
                                       // command argument list
	cmdList.add(cmdBase + skeletonCdf);
	
        if (cdfName != null) {

	    cmdList.add("-cdf");
	    cmdList.add(cdfName);
	}

        if (delete) {

            cmdList.add("-delete");
        }
        else {

            cmdList.add("-nodelete");
        }

        if (log) {

            cmdList.add("-log");
        }
        else {

            cmdList.add("-nolog");
        }
	    
	    
	cmdList.add("-zmode");
        cmdList.add(zMode + "");

        if (neg2posfp0) {

            cmdList.add("-neg2posfp0");
        }
        else {

            cmdList.add("-noneg2posfp0");
        }

        if (reportType != 0) {

	    cmdList.add("-report");

            StringBuffer reportBuf = new StringBuffer();
                                       // buffer inwhich to build
                                       // report options
            if ((reportType & REPORT_ERRORS) != 0) {

                reportBuf.append("errors, ");
            }
            if ((reportType & REPORT_WARNINGS) != 0) {

                reportBuf.append("warnings, ");
            }
            if ((reportType & REPORT_INFORMATION) != 0) {

                reportBuf.append("informationals, ");
            }

            cmdList.add(
                reportBuf.substring(0, reportBuf.length() - 2));
	}

        if (cacheSize != null) {

            cmdList.add("-cache");
            cmdList.add(cacheSize + "");
	}

        if (statistics) {

            cmdList.add("-statistics");
        }
        else {

            cmdList.add("-nostatistics");
        }

	cmdList.add(skeletonName);

        ProcessBuilder pb = new ProcessBuilder(cmdList);
                                       // skeletoncdf ProcessBuilder
        setCdfLibraryPath(pb);

        debug(pb);

        Process p = pb.redirectErrorStream(true).start();
                                       // skeletoncdf Process

	BufferedReader inStream = null;
        try {

            inStream = new BufferedReader(
                           new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = inStream.readLine()) != null) {

                System.err.println("skeletoncdf: " + line);

                if (BAD_LS_PATTERN.matcher(line).matches()) {

                    throw new CDFException(
                                  CDF.TT2000_USED_OUTDATED_TABLE);
                }
	    }
        }
        finally {

            if (inStream != null) {

                inStream.close();
            }
        }

	int returnCode = p.waitFor();
    }


    /**
     * Determines if the CDF/netCDF Translation Tools are installed.
     *
     * @return true if the CDF/netCDF translation tools are found.
     *             Otherwise, false.
     */
    public static boolean translationToolsInstalled() {

        return utilityIsInstalled(netCdfToCdf) &&
                   utilityIsInstalled(cdfToNetCdf);
    }


    /**
     * Determines if the CDF skeleton table Tools are installed.
     *
     * @return true if the CDF skeleton table tools are found.
     *             Otherwise, false.
     */
    public static boolean skeletonToolsInstalled() {

        // Note: we cannot use utilityIsInstalled() with these tool
        // because if they are started with no arguments they will
        // display the first page of help and then wait for input.
        //
        String sktTableVersion = getSkeletonTableVersion();
                                       // version of skeletontable
        String sktCdfVersion = getSkeletonCdfVersion();
                                       // version of skeletoncdf

        return sktTableVersion != null && sktTableVersion.length() > 0 
            && sktCdfVersion != null && sktCdfVersion.length() > 0;
    }


    /**
     * Regex pattern to capture the output filename from the CDF
     * translation tools.
     */
    private static final Pattern OUTPUT_FILENAME_PATTERN =
        Pattern.compile(".*Output file name: *(.*)");


    /**
     * Produces a netCDF file from a CDF file.
     *
     * @param cdfFilename The pathname of the CDF (without extension)
     *            from which the netCDF file will be created.  
     * @param netCdfFilename The pathname of the netCDF (without 
     *            extension) that will be created.  
     * @return cdf-to-netCDF exit code.
     * @throws java.io.IOException if there is a problem opening the 
     *    source file or creating the destination file.
     * @throws InterruptedException if file opening/creating was 
     *    interrupted by the user.
     */
    public static int cdfToNetCdf(
        String cdfFilename, 
        String netCdfFilename)
	throws IOException, InterruptedException {

        File netCdfFile = new File(netCdfFilename + ".nc");

        List<String> cmdList = new ArrayList<>();
                                       // command argument list
        cmdList.add(cmdBase + "cdf-to-netCDF");

// At one time, the mapping file was required but now it is not.
// If it exists, use it in case we are calling an older version that
// requires it.  This can be eliminated when we don't have to work
// with the old version.
File mappingFile = new File(cmdBase + "cdf_to_netcdf_mapping.dat");
if (mappingFile.canRead()) {
        cmdList.add("-map");
        cmdList.add(mappingFile.getCanonicalPath());
}
        cmdList.add("-o");
        cmdList.add(netCdfFilename + ".nc");
        cmdList.add("-nc4");
//        cmdList.add("-nc4classic");
        cmdList.add("-epoch");
        cmdList.add(cdfFilename + ".cdf");

        ProcessBuilder pb = new ProcessBuilder(cmdList);

        setCdfLibraryPath(pb);

        debug(pb);

	Process p = pb.redirectErrorStream(true).start();

        String outputFilename = null;
	BufferedReader inStream = null;
        try {

            inStream = new BufferedReader(
                           new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = inStream.readLine()) != null) {

                Matcher outFileMatcher = 
                    OUTPUT_FILENAME_PATTERN.matcher(line);
                                       // output filename matcher
                if (outFileMatcher.matches() && 
                    outFileMatcher.groupCount() == 1) {

                    outputFilename = outFileMatcher.group(1);
                }
                System.err.println("cdf-to-netCDF: " + line);
	    }
        }
        finally {

            if (inStream != null) {

                inStream.close();
            }
        }

	int exitCode = p.waitFor();

        if (exitCode == 0) {

            if (outputFilename == null) {

                exitCode = -1;
            }
        }
        return exitCode;
    }


    /**
     * Produces a CDF file from a netCDF file.
     *
     * @param netCdfFilename The pathname of the netCDF (without 
     *            extension) from which the CDF file will be created.  
     * @param cdfFilename The pathname of the CDF (without 
     *            extension) that will be created.  
     * @return netCDF-to-cdf exit code.
     * @throws java.io.IOException if there is a problem opening the 
     *    source file or creating the destination file.
     * @throws InterruptedException if file opening/creating was 
     *    interrupted by the user.
     */
    public static int netCdfToCdf(
        String netCdfFilename, String cdfFilename)
	throws IOException, InterruptedException {

        File netCdfFile = new File(netCdfFilename + ".nc");
        if (!netCdfFile.exists()) {

            netCdfFile = new File(netCdfFilename + ".NC");
        }
        File cdfFile = new File(cdfFilename + ".cdf");

        List<String> cmdList = new ArrayList<>();
                                       // command argument list
        cmdList.add(cmdBase + "netCDF-to-cdf");

// At one time, the mapping file was required but now it is not.
// If it exists, use it in case we are calling an older version that
// requires it.  This can be eliminated when we don't have to work
// with the old version.
File mappingFile = new File(cmdBase + "netcdf_to_cdf_mapping.dat");
if (mappingFile.canRead()) {
        cmdList.add("-map");
        cmdList.add(mappingFile.getCanonicalPath());
}
        cmdList.add("-o");
        cmdList.add(cdfFilename);
        cmdList.add("-istpepoch");
        cmdList.add(netCdfFile.getCanonicalPath());

        ProcessBuilder pb = new ProcessBuilder(cmdList);
                                       // process builder

        setCdfLibraryPath(pb);

        debug(pb);

	Process p = pb.redirectErrorStream(true).start();

        String outputFilename = null;
	BufferedReader inStream = null;
        try {

            inStream = new BufferedReader(
                           new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = inStream.readLine()) != null) {

                Matcher outFileMatcher = 
                    OUTPUT_FILENAME_PATTERN.matcher(line);
                                       // output filename matcher
                if (outFileMatcher.matches() && 
                    outFileMatcher.groupCount() == 1) {

                    outputFilename = outFileMatcher.group(1);
                }
                System.err.println("netCDF-to-cdf: " + line);
	    }
        }
        finally {

            if (inStream != null) {

                inStream.close();
            }
        }

	int exitCode = p.waitFor();

        if (exitCode == 0) {

            if (outputFilename == null) {

                exitCode = -1;
            }
        }
        return exitCode;
    }


    /**
     * Sets the CDF dynamic library path if necessary.
     *
     * @param pb ProcessBuilder whose environment is to be modified.
     * @return true if the path was modified.  Otherwise, false.
     */
    private static boolean setCdfLibraryPath(
        ProcessBuilder pb) {

        Map<String, String> env = pb.environment();
                                       // process' environment
        if (ldLibraryPathValue != null) {

            String envLdLibraryPathValue = env.get(ldLibraryPathName);
                                       // environment's ldLibraryPath
                                       // value
            if (envLdLibraryPathValue != null &&
                !envLdLibraryPathValue.contains(ldLibraryPathValue)) {

                env.put(ldLibraryPathName, 
                    ldLibraryPathValue + ":" + envLdLibraryPathValue);
            }
            else {

                env.put(ldLibraryPathName, ldLibraryPathValue);
            }
            return true;
        }
        return false;
    }


    /**
     * Prints debug information about the given ProcessBuilder to
     * System.err.
     *
     * @param pb ProcessBuilder
     */
    private static void debug(ProcessBuilder pb) {

        if (!debug) {

            return;
        }
        Map<String, String> env = pb.environment();
                                       // process' environment
        System.err.print("Running ");
        for (String cmd : pb.command()) {
            System.err.println("    " + cmd);
        }
        System.err.println("  With environment ");
        for (Map.Entry<String, String> envEntry: env.entrySet()) {

            String key = envEntry.getKey();

            if (key.contains("PATH") || key.contains("LIB")) {

                System.err.println("    " + envEntry.getKey() + ": " + 
                    envEntry.getValue());
            }
        }
    }


    /**
     * Checks whether the given utility program is installed.
     *
     * @param name name of utility program to check.
     * @return true if utility exists.  Otherwise, false.
     */
    public static boolean utilityIsInstalled(
        String name) {

        // Attempts to execute the utility with no arguments.  If it 
        // executes (and produces the help text), if must have been 
        // installed and we either have the full path, it was found 
        // somewhere on our path, or an alias has been correctly 
        // defined.
       
        List<String> cmdList = new ArrayList<>();
                                       // command argument list
        cmdList.add(cmdBase + name);

        ProcessBuilder pb = new ProcessBuilder(cmdList);

        setCdfLibraryPath(pb);

        debug(pb);

        try {

            Process p = pb.redirectErrorStream(true).start();

            String outputFilename = null;
            BufferedReader inStream = null;
            try {

                inStream = new BufferedReader(
                               new InputStreamReader(
                                   p.getInputStream()));
                String line;
                while ((line = inStream.readLine()) != null) {

                    // we could look for the help output but why bother
	        }
            }
            finally {

                if (inStream != null) {

                    inStream.close();
                }
            }

            int exitCode = p.waitFor();

            if (exitCode == 0) {

                return true;
            }
        }
        catch (IOException e) {

            return false;
        }
        catch (InterruptedException e) {

            return false;
        }

        return false;
    }


}


/*

class CdfUtilityHashNamespaceContext
    implements NamespaceContext {

    public static final String CDF_UTILITY_HASH_NS =
        "http://spdf.gsfc.nasa.gov/schema/cdf/hash";

    private static final ArrayList<String> prefixes = 
        new ArrayList<String>();

    static {

        prefixes.add(XMLConstants.DEFAULT_NS_PREFIX);
    }

    public String getNamespaceURI(String prefix) {

        if (prefix == null) {

            throw new IllegalArgumentException(
                          "null namespace prefix.");
        }
        else if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {

            return CDF_UTILITY_HASH_NS;
        }
        else {

            return XMLConstants.NULL_NS_URI;
        }
    }

    public String getPrefix(String namespaceURI) {

        if (namespaceURI.equals(CDF_UTILITY_HASH_NS)) {

            return XMLConstants.DEFAULT_NS_PREFIX;
        }
        else {

            return null;
        }
    }

    public Iterator getPrefixes(String namespaceURI) {

        return prefixes.iterator();
    }
}

*/
