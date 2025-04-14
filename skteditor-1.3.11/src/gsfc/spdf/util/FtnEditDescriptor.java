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
 * $Id: FtnEditDescriptor.java,v 1.8 2022/08/01 18:58:04 btharris Exp $
 */

package gsfc.spdf.util;


import java.lang.Character;
import java.lang.Integer;
import java.lang.String;
import java.lang.IllegalArgumentException;



/**
 * This class represents a Fortran edit descriptor of a format
 * specification.  A format specification may be of the following
 * forms:<pre>
 *   [r] ed
 *       ned
 *   [r] fs
 *
 * where:
 *    ed is a repeatable edit descriptor 
 *    ned is a nonrepeatable edit descriptor 
 *    r is a nonzero, unsigned, integer constant called a repeat specification 
 * </pre>
 * @author B. Harris
 * @version $Revision: 1.8 $
 */
public class FtnEditDescriptor {


    /**
     * Parses the given string for a FtnEditDescriptor.
     * 
     * @param format an edit descriptor from a format specification
     * @return FtnEditDescriptor representation of given format String.
     * @exception java.lang.IllegalArgumentException thrown if the given 
     *            string does not contain a syntactically correct edit 
     *            descriptor
     */
    public static FtnEditDescriptor parseFtnEditDescriptor(String format) 
        throws IllegalArgumentException {

        if (format.length() == 0) {

            throw new IllegalArgumentException("No value");
        };

        char ch = Character.toUpperCase(format.charAt(0));

        if (ch == '-') {

            return NonRepeatableFtnEditDescriptor.
                            parseNonRepeatableFtnEditDescriptor(format);
        }
        else if (Character.isDigit(ch)) {

            int i;

            for (i = 1; i < format.length(); i++) {

                ch = Character.toUpperCase(format.charAt(i));

                if (!Character.isDigit(ch)) {

                    if (ch == 'P') {

                        return NonRepeatableFtnEditDescriptor.
                            parseNonRepeatableFtnEditDescriptor(format);
                    }
                    else {

                        return RepeatableFtnEditDescriptor.
                            parseRepeatableFtnEditDescriptor(format);
                    }
                };
            }; // end for

            throw new IllegalArgumentException(
                          "Invalid edit descriptor '" + format + "'");
        }
        else if (ch == 'I' || ch == 'Z' || ch == 'O' || ch == 'B' ||
                 ch == 'F' || ch == 'E' || ch == 'D' || ch == 'G' || 
                 ch == 'L' || ch == 'A') {

            return RepeatableFtnEditDescriptor.
                            parseRepeatableFtnEditDescriptor(format);
        }
        else {

            return NonRepeatableFtnEditDescriptor.
                            parseNonRepeatableFtnEditDescriptor(format);
        }
    }


    /**
     * Provides the length (number of characters) of the edit 
     * descriptor.
     * 
     * @return the length
     */
    public int getLength() {

        return length;
    }


    /**
     * Value specifying the descriptors length.
     */
    protected int length = 0;


    /**
     * Parses the given string for an unsigned number.
     * 
     * @param s string to be parsed for an unsigned number
     * @return the number found in s
     * @exception java.lang.IllegalArgumentException thrown if a valid 
     *            unsigned number is not found in s
     */
    protected static int parseNumber(String s) 
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
     * Provides the number of leading characters in the given string that
     * could represent a decimal number (a leading '-' is accepted but
     * '+' is not).
     * 
     * @param s string to be examined for an decimal number
     * @return the number of leading characters found in s that could
     *         represent a valid decimal number
     */
    protected static int numberLength(String s) {

        int i = 0;

        if (s.length() > 0 && s.charAt(0) == '-') {

            i++;
        };

        for (; i < s.length() && Character.isDigit(s.charAt(i)); i++) {

        };

        return i;
    }


    /**
     * Test harness for this class.
     * 
     * @param args optional parameters containing additional strings to be 
     * parsed for edit descriptors.  If none are provided, then just the built-
     * in tests are performed.
     */
    public static void main(String[] args) {

        String[] emptyArgs = new String[0];

        RepeatableFtnEditDescriptor.main(emptyArgs);
        NonRepeatableFtnEditDescriptor.main(emptyArgs);

        String[] tests = {"1PE10.3", "2p1e8.5"};

        System.out.println("Combination tests:");
        for (int i = 0; i < tests.length; i++) {

            doTest(i, tests[i]);
        };

        System.out.println("\nOptional tests:");
        for (int i = 0; i < args.length; i++) {

            doTest(i, args[i]);
        };

    }

    
    /**
     * Used by the test harness for this class to check the syntax of the given
     * string.
     * 
     * @param id identifies the test that is to be performed
     * @param s the string to perform a syntax check on
     * @return the results of the test (true &ge; no error)
     */
    private static boolean doTest(int id, String s) {

        FtnEditDescriptor ed = null;

        System.out.print("Test[" + id + "]: '" + s + "' results = ");

        try {

            for (int i = 0; i < s.length(); ) {

                ed = parseFtnEditDescriptor(s.substring(i));

                System.out.print("'" + s.substring(i, i + ed.getLength()) +
                             "' valid ");
                i += ed.getLength();
            };
            System.out.println();
        }
        catch (IllegalArgumentException e) {

            System.out.println("syntax error");
            System.out.println("    " + e.getMessage());

            return false;
        };

        return true;
    }
}
