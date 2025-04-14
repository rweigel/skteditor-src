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
 * $Id: CConversionSpecification.java,v 1.6 2022/08/01 16:03:58 btharris Exp $
 */

package gsfc.spdf.util;


import java.lang.Character;
import java.lang.Integer;
import java.lang.String;
import java.lang.IllegalArgumentException;



/**
 * This class represents a C language style conversion specification
 * as found in the format string of a printf function.  A conversion
 * specification begins with a percent-sign character (%).  After the
 * % character, the following appear in sequence:
 * flags
 * field width
 * precision
 * conversion characters
 * Refer to C library documentation for a more detailed description.
 * 
 * @author B. Harris
 * @version $Revision: 1.6 $
 */
public class CConversionSpecification {


    /**
     * Constructs a CConversionSpecification with the given attributes.
     * 
     * @param type type of conversion
     * @param flags conversion flags
     * @param width field width
     * @param precision field precision
     */
    public CConversionSpecification(char type, String flags, int width, 
                                    int precision) {

        this.type = type;
        this.flags = flags;
        this.width = width;
        this.precision = precision;
    }


    /**
     * Constructs a CConversionSpecification from the given string
     * representation.
     * 
     * @param format a conversion specification from a format string
     * @exception java.lang.IllegalArgumentException thrown if the given 
     *            string does not contain a syntactically correct conversion 
     *            specification
     */
    public CConversionSpecification(String format) 
        throws IllegalArgumentException {

        format = format.trim();

        if (format.length() < 2) {

            throw new IllegalArgumentException("No conversion specification");
        };

        if (format.charAt(0) != '%') {

            throw new IllegalArgumentException("Invalid character '" +
                                               format.charAt(0) + "'");
        };

        int i;

        SEARCH_FOR_FLAGS:
        for (i = 1; i < format.length(); i++) {

            switch (format.charAt(i)) {

            case '+':
            case '-':
            case ' ':
            case '#':
            case '0':

                // a valid flag so keep looking
                break;

            default:

                // not a flag
                break SEARCH_FOR_FLAGS;
            };
        };

        flags = format.substring(1, i);

        type = format.charAt(format.length() - 1);

        switch (type) {

        case 'd':
        case 'i':
        case 'o':
        case 'u':
        case 'x':
        case 'X':
        case 'f':
        case 'e':
        case 'E':
        case 'g':
        case 'G':
        case 'c':
        case 's':

            // valid values, continue
            break;

        default:

            throw new IllegalArgumentException(
                          "Invalid conversion specification '" + type +
                          "'");
        };

        int dotIndex = format.indexOf('.', i);

        if (dotIndex < 0) {

            if (i < format.length() - 1) {

                width = parseNumber(format.substring(i, format.length() - 1));
            };
        }
        else {

            if (i < dotIndex) {

                width = parseNumber(format.substring(i, dotIndex));
            };
            if (dotIndex + 1 < format.length() - 1) {

                precision = parseNumber(format.substring(dotIndex + 1, 
                                                         format.length() - 1));
            };
        };
    }


    /**
     * Parses the given printf format string for a conversion specification.
     * 
     * @param format string containing a conversion specification
     * @return conversion specification found within the given format string
     * @exception java.lang.IllegalArgumentException thrown if the given string
     *            does not contain a syntactically correct conversion 
     *            specification
     */
    public static CConversionSpecification parseDescriptor(String format) 
        throws IllegalArgumentException {

        return new CConversionSpecification(format);
    }


    /**
     * Provides the type value.
     * 
     * @return the type value
     */
    public char getType() {

        return type;
    }


    /**
     * Provides the width value.
     * 
     * @return the width value (0 if none specified)
     */
    public int getWidth() {

        return width;
    }


    /**
     * Provides the precision value.
     * 
     * @return the precision value (0 if none specified)
     */
    public int getPrecision() {

        return precision;
    }


    /**
     * Provides the flags value.
     * 
     * @return the flags value
     */
    public String getFlags() {

        return flags;
    }


    /**
     * conversion type
     */
    protected char type = 'B';

    /**
     * conversion flags
     */
    protected String flags = null;

    /**
     * field width
     */
    protected int width = 0;

    /**
     * field precision
     */
    protected int precision = 0;

    /**
     * Parses the given string for an unsigned number.
     * 
     * @param s string to be parsed for an unsigned number
     * @return the number found in s
     * @exception java.lang.IllegalArgumentException thrown if a valid unsigned
     *            number is not found in s
     */
    protected int parseNumber(String s) 
        throws IllegalArgumentException {

        for (int i = 0; i < s.length(); i++) {

            if (Character.getType(s.charAt(i)) != 
                Character.DECIMAL_DIGIT_NUMBER) {

                throw new IllegalArgumentException("Invalid digit '" + 
                                                   s.charAt(i) + "'");
            };
        };

        return Integer.parseInt(s);
    }


    /**
     * Test harness for this class.
     * 
     * @param args optional parameters containing additional strings to be 
     *        parsed for conversion specifications.  If none are provided, then
     *        just the built in tests are performed
     */
    public static void main(String[] args) {


        String[] tests = {"%d", "%1d", "%.1d", "%1.1d", 
                          "%+d", "%-d", "% d", "%#o", "%0d",
                          "%i", "%o", "%u", "%x", "%X",
                          "%f", "%1f", "%.1f", "%1.1f",
                          "%e", "%E", "%g", "%G", "%c", "%s",
                          "%", "wrong", "%wrong", "%1wrong", "%.d", "%.wrong",
                          "%1.1wrong"};

        System.out.println("Standard tests:");
        for (int i = 0; i < tests.length; i++) {

            doTest(i, tests[i]);
        };

        System.out.println("\nOptional tests:");
        for (int i = 0; i < args.length; i++) {

            doTest(i, args[i]);
        };
    }


    /**
     * Used by the test harness for this class to check the syntax of the
     * given string.
     * 
     * @param id identifies the test that is to be performed
     * @param s the string to perform the test on
     * @return results of the test (true &ge; on error)
     */
    private static boolean doTest(int id, String s) {

        System.out.print("Test[" + id + "]: '" + s + "' results = ");

        try {

            CConversionSpecification.parseDescriptor(s);

            System.out.println("valid");
        }
        catch (IllegalArgumentException e) {

            System.out.println("syntax error");
            System.out.println("    " + e.getMessage());

            return false;
        };

        return true;
    }

}
