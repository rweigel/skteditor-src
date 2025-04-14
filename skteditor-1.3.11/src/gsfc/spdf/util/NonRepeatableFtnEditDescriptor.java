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
 * $Id: NonRepeatableFtnEditDescriptor.java,v 1.7 2022/08/01 18:58:04 btharris Exp $
 */

package gsfc.spdf.util;


import java.lang.Character;
import java.lang.Integer;
import java.lang.String;
import java.lang.IllegalArgumentException;



/**
 * This class represents a non-repeatable Fortran edit descriptor 
 * of a format specification.  
 *
 * Nonrepeatable edit descriptors are of the form:<pre>
 *    'h1 h2 ... hn'
 *    nHh1h2 ... hn
 *    Tc
 *    TLc
 *    TRc
 *    nX
 *    /
 *    :
 *    S
 *    SP
 *    SS
 *    kP
 *    BN
 *    BZ
 *
 * where:
 *
 *   apostrophe, H, T, TL, TR, X, slash, colon, S, SP, SS, P, 
 *     BN, and BZ indicate the manner of editing 
 *   h is one of the characters capable of representation by 
 *     the processor 
 *   n and c are nonzero, unsigned, integer constants 
 *   k is an optionally signed integer constant 
 * </pre>
 * @author B. Harris
 * @version $Revision: 1.7 $
 */
public class NonRepeatableFtnEditDescriptor extends FtnEditDescriptor {


    /**
     * Constructs a NonRepeatableFtnEditDescriptor with the given attributes.
     * 
     * @param type specifies the type of editing 
     * @param length the length of this descriptor including any integer 
     *               constansts (e.g., the length of "TL123" is 5);
     * @param position the position value for editing types Tc, TLc, TRc, nX
     * @param scale the scale value for the editing type kP
     */
    NonRepeatableFtnEditDescriptor(String type, int length, int position, 
                                   int scale) {

        this.type = type;
        this.length = length;
        this.position = position;
        this.scale = scale;
    }


    /**
     * Constructs a NonRepeatableFtnEditDescriptor with the given type.
     * 
     * @param type specifies the type of editing (should be one of /, :,
     *             S, SP, SS, BN, BZ)
     */
    NonRepeatableFtnEditDescriptor(String type) {

        this.type = type;
        length = type.length();
    }


    /**
     * Constructs a "positioning" NonRepeatableFtnEditDescriptor with the 
     * given position value.
     * 
     * @param type specifies the type of editing (should be one of Tc, TLc,
     *             TRc, or nX)
     * @param length the length of this descriptor including any integer 
     *               constansts (e.g., the length of "TL123" is 5);
     * @param position the position value for editing types Tc, TLc, TRc, nX
     */
    NonRepeatableFtnEditDescriptor(String type, int length, int position) {

        this.type = type;
        this.length = length;
        this.position = position;
    }


    /**
     * Constructs a 'kP' NonRepeatableFtnEditDescriptor with the given
     * scale value.
     * 
     * @param scale the scale value for the editing type kP
     * @param length the length of this descriptor including any integer 
     *               constansts (e.g., the length of "1P" is 2);
     */
    NonRepeatableFtnEditDescriptor(int scale, int length) {

        this.type = "P";
        this.length = length;
        this.scale = scale;
    }


    /**
     * parses the given string for a NonRepeatableFtnEditDescriptor 
     * 
     * @param format an edit descriptor from a format specification
     * @return NonRepeatableFtnEditDescriptor representation of given format
     *     String.
     * @exception java.lang.IllegalArgumentException thrown if the given 
     *            string does not contain a syntactically correct edit 
     *            descriptor
     */
    public static NonRepeatableFtnEditDescriptor 
                           parseNonRepeatableFtnEditDescriptor(String format) 
        throws IllegalArgumentException {

        if (format.length() == 0) {

            throw new IllegalArgumentException("No value");
        };

        int numLen = numberLength(format);

        if (numLen > 0) {  // i.e., there's a leading number

            // look for kP or nX or nH

            int num = Integer.parseInt(format.substring(0, numLen));

            if (numLen < format.length()) {

                char ch = Character.toUpperCase(format.charAt(numLen));

                switch (ch) {

                case 'P':

                    return new NonRepeatableFtnEditDescriptor("P", 
                                                  numLen + 1, num);
                case 'H':
                    
                    if (num < 1) {

                        throw new IllegalArgumentException("Constant '" +
                                       num + "' must be positive in '" +
                                       format + "'");
                    };

                    if (numLen + 1 + num > format.length()) {

                        throw new IllegalArgumentException("Less than " +
                                       num + " characters in H field '" +
                                       format + "'");
                    };

                    return new NonRepeatableFtnEditDescriptor(
                                    format.substring(numLen, numLen + 1 + num),
                                    numLen + 1 + num, num);

                case 'X':

                    if (num < 1) {

                        throw new IllegalArgumentException("Constant '" +
                                       num + "' must be positive in '" +
                                       format + "'");
                    };

                    return new NonRepeatableFtnEditDescriptor("X", 
                                                  numLen + 1, num);
                default:

                    throw new IllegalArgumentException(
                                           "Invalid character '" + ch + 
                                           "' in edit descriptor '" +
                                           format + "'");
                }
            }
            else {

                throw new IllegalArgumentException("Invalid edit descriptor '" +
                                                   format + "'");
            }
        }
        else {  // no leading number

            char ch = Character.toUpperCase(format.charAt(0));

            if (Character.isLetter(ch)) {

                switch (ch) {

                case 'T':
 
                    if (format.length() == 1) {

                        throw new IllegalArgumentException(
                                           "Invalid edit descriptor '" +
                                           format + "'");
                    }
                    else {

                        ch = Character.toUpperCase(format.charAt(1));

                        int num = 0;

                        if (Character.isDigit(ch)) {

                            numLen = numberLength(format.substring(1));
                            num = Integer.parseInt(format.substring(1, 
                                                                1 + numLen));

                            return new NonRepeatableFtnEditDescriptor("T",
                                                     1 + numLen, num);
                        }
                        else if (ch == 'L' || ch == 'R') {

                            if (format.length() > 2 &&
                                (numLen = numberLength(format.substring(2))) > 0) {

                                num = Integer.parseInt(format.substring(2, 
                                                                 2 + numLen));

                                return new NonRepeatableFtnEditDescriptor(
                                                 format.substring(0, 2),
                                                     2 + numLen, num);
                            }
                            else {

                                throw new IllegalArgumentException(
                               "Missing position value in edit descriptor '" +
                               format + "'");
                            }
                        }
                        else {

                            throw new IllegalArgumentException(
                                           "Invalid edit descriptor '" +
                                           format + "'");
                        }
                    }

                case 'S':

                    if (format.length() == 1) {

                        return new NonRepeatableFtnEditDescriptor("S");
                    }
                    else {

                        ch = Character.toUpperCase(format.charAt(1));

                        switch (ch) {

                        case 'P':
                        case 'S':

                            return new NonRepeatableFtnEditDescriptor(
                                                   format.substring(0, 2));
                        default:

                            return new NonRepeatableFtnEditDescriptor("S");
                        }
                    }

                case 'B':

                    if (format.length() > 1) {

                        ch = Character.toUpperCase(format.charAt(1));

                        switch (ch) {

                        case 'N':
                        case 'Z':

                            return new NonRepeatableFtnEditDescriptor(
                                                   format.substring(0, 2));

                        default:

                            throw new IllegalArgumentException(
                                           "Invalid edit descriptor '" +
                                           format + "'");
                        }
                    }
                    else {

                        throw new IllegalArgumentException(
                                           "Invalid edit descriptor '" +
                                           format + "'");
                    }

                default:

                    throw new IllegalArgumentException("Invalid edit descriptor '" +
                                                   format + "'");
                }
            }
            else if (ch == '\'') {

                if (format.length() == 1) {

                    throw new IllegalArgumentException(
                                           "Invalid edit descriptor '" +
                                           format + "'");
                }
                else {

                    int i = format.indexOf('\'', 1);

                    if (i > -1) {

                        return new NonRepeatableFtnEditDescriptor(
                                          format.substring(0, i + 1));
                    }
                    else {

                        throw new IllegalArgumentException(
                                  "Closing ' missing from edit descriptor '" +
                                  format + "'");
                    }
                }
            }
            else if (ch == '/') {
 
                return new NonRepeatableFtnEditDescriptor("/");
            }
            else if (ch == ':') {

                return new NonRepeatableFtnEditDescriptor(":");

            };
        };

        throw new IllegalArgumentException("Invalid edit descriptor '" +
                                           format + "'");
    }


    /**
     * provides the type value
     * 
     * @return the type value
     */
    public String getType() {

        return type;
    }


    /**
     * provides the position value
     * 
     * @return the position value
     */
    public int getPosition() {

        return position;
    }


    /**
     * provides the scale value
     * 
     * @return the scale value
     */
    public int getScale() {

        return scale;
    }


    /**
     * value specifying the field's type
     */
    protected String type;


    /**
     * value specifying the position value
     */
    protected int position = 0;


    /**
     * value specifying the scale value
     */
    protected int scale = 1;


    /**
     * Test harness for this class.
     * 
     * @param args optional parameters containing additional strings to be 
     * parsed for edit descriptors.  If none are provided, then just the built-
     * in tests are performed.
     */
    public static void main(String[] args) {

        String[] tests = {"'h1 h2 ... hn'", "5H12345", "T1", "TL1", "TR1",
                          "1X", "/", ":", "S", "SP", "SS", "1P", "-2P", 
                          "BN", "BZ", "", "wrong", "'h1", "Twrong", 
                          "TLwrong", "TRwrong", "SI3", "Bwrong"};

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
     * Used by the test harness for this class to check the syntax of the given
     * string.
     * 
     * @param id identifies the test that is to be performed
     * @param s the string to perform a syntax check on
     * @return the results of the test (true &ge; no error)
     */
    private static boolean doTest(int id, String s) {

        System.out.print("Test[" + id + "]: '" + s + "' results = ");

        try {

            parseNonRepeatableFtnEditDescriptor(s);

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
