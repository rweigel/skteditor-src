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
 * Copyright (c) 2012-2023 United States Government as represented by 
 * the National Aeronautics and Space Administration. No copyright is 
 * claimed in the United States under Title 17, U.S.Code. All Other 
 * Rights Reserved.
 *
 * $Id: Filename.java,v 1.11 2023/03/24 12:40:20 btharris Exp $
 */
package gsfc.spdf.istp;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * This class represents the name of an ISTP CDF file.
 * 
 * @author B. Harris
 * @version $Revision: 1.11 $
 */
public class Filename {

    /**
     * Regular expression pattern matching CDAWeb CDF filenames with a
     * capture groups for the following portions of the filename:
     * <ol>
     *   <li>dataset name</li>
     *   <li>source name</li>
     *   <li>data type</li>
     *   <li>description</li>
     *   <li>date</li>
     *   <li>version</li>
     *   <li>extension</li>
     * </ol>
     */
    private final static Pattern FILENAME_PATTERN =
        Pattern.compile(
            "(?:.*/)?" +                   // optional path
            "(([\\w\\-]+?)_" +             // source name
            "([\\w\\-]+?)[-_]" +           // data type
            "([\\w\\-]+?))_" +             // descriptor
            "(\\d+(t\\d+)?)" +             // date
            "(?:-(?:\\d+(?:t\\d+)?))?" +   // optional end date
            "(?:_\\w+)?" +                 // map01-99 in ibex filenames
            "_v(\\d+(?:(\\.\\d+)|([a-z])|(-\\d{1,2}){2})*)" +     // version
            "(?:[-_]r?\\d+)?" +            // optional, extended version
            "\\.([cC][dD][fF]|[sS][kK][tT])");
                                           // extension

    /**
     * Regular expression pattern to match de_vs_eics data filenames
     * which don't contain the dataset name like all other dataset
     * data filenames do.
     */
    private final static Pattern DE_VS_EICS_PATTERN =
        Pattern.compile("(?:.*/)?(\\d+)_eics_de_96s_v(\\d+)\\.cdf");


    /**
     * Default file naming components.
     */
    private final static String[] DEFAULT_COMPONENTS =
        new String[] {
            "source",
            "datatype",
            "descriptor",
            "yyyyMMdd",
            "",
    };


    /**
     * <code>java.text.SimpleDateFormat</code> for the default file
     * naming date/time value.
     */
    private final static SimpleDateFormat
        DEFAULT_DATE_TIME_FORMAT;

    /**
     * File naming date formats.  The values are
     * <code>java.text.SimpleDateFormat</code> patterns.  Values are
     * in decreasing order of preference.
     */
    private final static SimpleDateFormat[] 
        DATE_FORMATS;

    /**
     * File naming time formats.  The values are
     * <code>java.text.SimpleDateFormat</code> patterns.  The elements
     * are in decreasing order of preference.
     */
    private final static SimpleDateFormat[] 
        TIME_FORMATS;

    /**
     * File naming time formats in parse preference order.  The values
     * are <code>java.text.SimpleDateFormat</code> patterns.
     */
    private final static SimpleDateFormat[]
        TIME_FORMATS_PARSE_ORDER;


    static {

        DEFAULT_DATE_TIME_FORMAT =
            new SimpleDateFormat(
                    DEFAULT_COMPONENTS[3] +
                    DEFAULT_COMPONENTS[4]);

        DEFAULT_DATE_TIME_FORMAT.setTimeZone(   
            Epoch.UTC_TIME_ZONE);

        DATE_FORMATS = new SimpleDateFormat[] {
            new SimpleDateFormat("yyyyMMdd"),
            new SimpleDateFormat("yyyyDDD")
        };
        DATE_FORMATS[0].setTimeZone(Epoch.UTC_TIME_ZONE);
        DATE_FORMATS[1].setTimeZone(Epoch.UTC_TIME_ZONE);

        TIME_FORMATS = new SimpleDateFormat[] {
            new SimpleDateFormat(""),
            new SimpleDateFormat("HHmmss"),
            new SimpleDateFormat("HHmm"),
            new SimpleDateFormat("HH"),
            new SimpleDateFormat("'t'HHmmss"),
            new SimpleDateFormat("'t'HHmm"),
            new SimpleDateFormat("'t'HH")
        };
        TIME_FORMATS[0].setTimeZone(Epoch.UTC_TIME_ZONE);
        TIME_FORMATS[1].setTimeZone(Epoch.UTC_TIME_ZONE);
        TIME_FORMATS[2].setTimeZone(Epoch.UTC_TIME_ZONE);
        TIME_FORMATS[3].setTimeZone(Epoch.UTC_TIME_ZONE);
        TIME_FORMATS[4].setTimeZone(Epoch.UTC_TIME_ZONE);
        TIME_FORMATS[5].setTimeZone(Epoch.UTC_TIME_ZONE);
        TIME_FORMATS[6].setTimeZone(Epoch.UTC_TIME_ZONE);

        TIME_FORMATS_PARSE_ORDER = new SimpleDateFormat[] {
            TIME_FORMATS[4],
            TIME_FORMATS[5],
            TIME_FORMATS[6],
            TIME_FORMATS[1],
            TIME_FORMATS[2],
            TIME_FORMATS[3],
            TIME_FORMATS[0]
        };

    }


    /**
     * Determines if the given filename is valid with respect to ISTP
     * file naming conventions.
     *
     * @param filename name of file to evaluate.
     * @return true if filename is valid with respect to ISTP file
     *             naming conventions.  Otherwise, false.
     */
    public static boolean isValid(String filename) {

        return FILENAME_PATTERN.matcher(filename).matches();
    }


    /**
     * Determines if the given filename is acceptable with respect to
     * CDAWeb file naming conventions.  Note that CDAWeb may accept
     * some file names which are not strictly valid with respect to
     * ISTP naming conventions.
     *
     * @param filename name of file to evaluate.
     * @return true if filename is valid with respect to ISTP file
     *             naming conventions.  Otherwise, false.
     * @see #isValid
     */
    public static boolean isAcceptable(String filename) {

        return FILENAME_PATTERN.matcher(filename).matches() ||
               DE_VS_EICS_PATTERN.matcher(filename).matches();
    }


    /**
     * Gets the dataset name from the given CDF filename.
     *
     * @param filename name of CDF file.
     * @return dataset name portion of given filename or null if the
     *                 filename does not conform to the ISTP CDF file
     *                 naming convention.
     */
    public static String getDataset(String filename) {

        String dataset = null;         // the dataset name value that is
                                       // to be returned

        Matcher matcher = FILENAME_PATTERN.matcher(filename);
                                       // filename pattern matcher
        if (matcher.matches()) {

            dataset = matcher.group(1).toUpperCase();
        }
        else if (DE_VS_EICS_PATTERN.matcher(filename).matches()) {

            dataset = "DE_VS_EICS";
        }

        return dataset;
    }


    /**
     * Gets the source name from the given CDF filename.
     *
     * @param filename name of CDF file.
     * @return source name portion of given filename or null if the
     *                 filename does not conform to the ISTP CDF file
     *                 naming convention.
     */
    public static String getSourceName(String filename) {

        String source = null;          // the source name value that is
                                       // to be returned

        Matcher matcher = FILENAME_PATTERN.matcher(filename);
                                       // filename pattern matcher
        if (matcher.matches()) {

            source = matcher.group(2).toUpperCase();
        }
        else if (DE_VS_EICS_PATTERN.matcher(filename).matches()) {

            source = "DE";
        }

        return source;
    }


    /**
     * Gets the data type from the given CDF filename.
     *
     * @param filename name of CDF file.
     * @return data type portion of given filename or null if the
     *                 filename does not conform to the ISTP CDF file
     *                 naming convention.
     */
    public static String getDatatype(String filename) {

        String datatype = null;        // the datatype name value that 
                                       // is to be returned

        Matcher matcher = FILENAME_PATTERN.matcher(filename);
                                       // filename pattern matcher
        if (matcher.matches()) {

            datatype = matcher.group(3).toUpperCase();
        }
        else if (DE_VS_EICS_PATTERN.matcher(filename).matches()) {

            datatype = "VS";
        }

        return datatype;
    }


    /**
     * Gets the descriptor from the given CDF filename.
     *
     * @param filename name of CDF file.
     * @return descriptor portion of given filename or null if the
     *                 filename does not conform to the ISTP CDF file
     *                 naming convention.
     */
    public static String getDescriptor(String filename) {

        String descriptor = null;      // the descriptor value that is
                                       // to be returned

        Matcher matcher = FILENAME_PATTERN.matcher(filename);
                                       // filename pattern matcher
        if (matcher.matches()) {

            descriptor = matcher.group(4).toUpperCase();
        }
        else if (DE_VS_EICS_PATTERN.matcher(filename).matches()) {

            descriptor = "EICS";
        }

        return descriptor;
    }


    /**
     * Gets the date from the given CDF filename.
     *
     * @param filename name of CDF file.
     * @return date portion of given filename or null if the
     *                 filename does not conform to the ISTP CDF file
     *                 naming convention.
     */
    public static String getDate(String filename) {

        String date = null;            // the date value that is
                                       // to be returned

        Matcher matcher = FILENAME_PATTERN.matcher(filename);
                                       // filename pattern matcher
        if (matcher.matches()) {

            date = matcher.group(5);
        }
        else if (DE_VS_EICS_PATTERN.matcher(filename).matches()) {

            date = matcher.group(1);
        }

        return date;
    }


    /**
     * Gets the version from the given CDF filename.
     *
     * @param filename name of CDF file.
     * @return version portion of given filename or null if the
     *                 filename does not conform to the ISTP CDF file
     *                 naming convention.
     */
    public static String getVersion(String filename) {

        String version = null;         // the version value that is
                                       // to be returned

        Matcher matcher = FILENAME_PATTERN.matcher(filename);
                                       // filename pattern matcher
        if (matcher.matches()) {

            version = matcher.group(7);
        }
        else if (DE_VS_EICS_PATTERN.matcher(filename).matches()) {

            version = matcher.group(2);
        }

        return version;
    }


    /**
     * Gets the default file naming components.
     *
     * @return the default file naming components.
     */
    public static String[] getDefaultNamingComponents() {

        return DEFAULT_COMPONENTS;
    }


    /**
     * Gets the default file naming date/time components.
     *
     * @return the default file naming date/time components.
     */
    public static SimpleDateFormat getDefaultNamingDateTimeFormat() {

        return DEFAULT_DATE_TIME_FORMAT;
    }


    /**
     * Gets the default file naming convention.
     *
     * @return the default file naming convention.
     */
    public static String getDefaultNamingConvention() {

        return DEFAULT_COMPONENTS[0] + "_" +
                   DEFAULT_COMPONENTS[1] + "_" +
                   DEFAULT_COMPONENTS[2] + "_" +
                   DEFAULT_COMPONENTS[3] + 
                   DEFAULT_COMPONENTS[4];
    }


    /**
     * Gets the valid file naming date formats.  The values are
     * <code>java.text.SimpleDateFormat</code> patterns.  Values are
     * in decreasing order of preference.
     *
     * @return an array containing valid file naming date formats.
     *     The values are <code>java.text.SimpleDateFormat</code>
     *     patterns.  Values are returned in decreasing order of
     *     preference.
     */
    public static SimpleDateFormat[] getDateFormats() {

        return DATE_FORMATS;
    }


    /**
     * Gets the specified file naming date format.
     *
     * @param index index of format being requested.
     * @return the specified file naming date format.
     */
    public static SimpleDateFormat getDateFormat(int index) {

        return DATE_FORMATS[index];
    }


    /**
     * Gets the valid file naming date options.  A file naming date
     * option is a slightly more human friendly version of the date
     * format patterns returned by {@link #getDateFormats}.
     * Element zero contains the recommended default value.
     *
     * @return an array containing valid file naming date options.
     *     Element zero contains the recommended default value.
     */
    public static String[] getDateOptions() {

        String[] options = new String[DATE_FORMATS.length];

        for (int i = 0; i < options.length; i++) {

            options[i] = DATE_FORMATS[i].toPattern();
        }
        return options;
    }


    /**
     * Gets the valid file naming time formats.  The values are
     * <code>java.text.SimpleDateFormat</code> patterns.  The elements
     * are returned in decreasing parse order.
     *
     * @return an array containing valid file naming time formats.
     *     The values are <code>java.text.SimpleDateFormat</code>
     *     patterns.  The elements are returned in decreasing order of
     *     preference.
     */
    public static SimpleDateFormat[]
        getTimeFormatsParseOrder() {

        return TIME_FORMATS_PARSE_ORDER;
    }


    /**
     * Gets the valid file naming time formats.  The values are
     * <code>java.text.SimpleDateFormat</code> patterns.  The elements
     * are returned in decreasing order of preference.
     *
     * @return an array containing valid file naming time formats.
     *     The values are <code>java.text.SimpleDateFormat</code>
     *     patterns.  The elements are returned in decreasing order of
     *     preference.
     */
    public static SimpleDateFormat[] getTimeFormats() {

        return TIME_FORMATS;
    }


    /**
     * Gets the specified file naming time format.
     *
     * @param index index of format being requested.
     * @return the specified file naming time format.
     */
    public static SimpleDateFormat getTimeFormat(int index) {

        return TIME_FORMATS[index];
    }


    /**
     * Gets the valid file naming time options.  A file naming time
     * option is a slightly more human friendly version of the time
     * format patterns returned by {@link #getTimeFormats}.
     * Element zero contains the recommended default value.
     *
     * @return an array containing valid file naming time options.
     *     Element zero contains the recommended default value.
     */
    public static String[] getTimeOptions() {

        String[] options = new String[TIME_FORMATS.length];
                                       // result option values

        for (int i = 0; i < options.length; i++) {

            options[i] = TIME_FORMATS[i].
                             toPattern().replaceAll("'", "");
        }
        return options;
    }



    /**
     * Class tester.
     *
     * @param args filenames to test for compliance.
     * @throws Exception if an Exception is encountered.
     */
    public static void main(String[] args) 
        throws Exception {

        System.out.println("Using FILENAME_PATTERN = " + FILENAME_PATTERN);

        for (String filename : args) {

            Matcher matcher = FILENAME_PATTERN.matcher(filename);
                                       // filename pattern matcher

            if (matcher.matches()) {

                System.out.println("\n" + filename + " matched.");

                System.out.println("    Groups:");
                for (int i = 1; i <= matcher.groupCount(); i++) {

                    System.out.println("    " + i + ": " + 
                        matcher.group(i));
                }
                System.out.println("    Dataset: " +
                    getDataset(filename));
                System.out.println("    SourceName: " +
                    getSourceName(filename));
                System.out.println("    Datatype: " +
                    getDatatype(filename));
                System.out.println("    Descriptor: " +
                    getDescriptor(filename));
                System.out.println("    Date: " +
                    getDate(filename));
                System.out.println("    Version: " +
                    getVersion(filename));
            }
            else {

                System.out.println(filename + 
                    " is NOT an ISTP compliant name.");
            }
        }
    }
}
