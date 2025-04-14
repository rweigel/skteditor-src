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
 * $Id: RepeatableFtnEditDescriptor.java,v 1.8 2022/08/01 18:58:04 btharris Exp $
 */

package gsfc.spdf.util;


import java.lang.Character;
import java.lang.Integer;
import java.lang.String;
import java.lang.IllegalArgumentException;



/**
 * This class represents a repeatable Fortran edit descriptor of 
 * a format specification.  Repeatable edit descriptors are of 
 * the form:<pre>
 *   [r]Iw
 *   [r]Iw.m
 *   [r]Zw
 *   [r]Zw.m
 *   [r]Ow
 *   [r]Ow.m
 *   [r]Fw.d
 *   [r]Ew.d
 *   [r]Ew.dEe
 *   [r]Gw.d
 *   [r]Gw.dEe
 *   [r]Lw
 *   [r]A
 *   [r]Aw
 * where:
 *   r is an optional nonzero, unsigned, integer constant called 
 *     a repeat specification
 *   I, Z, O, F, E, D, G, L, and A indicate the manner of editing
 *   w and e are nonzero, unsigned, integer constants
 *   d and m are unsigned integer constants
 * </pre>
 * @author B. Harris
 * @version $Revision: 1.8 $
 */
public class RepeatableFtnEditDescriptor extends FtnEditDescriptor {


    /**
     * Constructs a RepeatableFtnEditDescriptor with the given 
     * attributes.
     * 
     * @param repeat specifies the repeat count
     * @param type specifies the data type the variable this 
     *            descriptor is 
     * associated with
     * @param width the width, in number of characters, of the field
     * @param minWidth the minimum width, in characters, of the field
     * @param precision the precision, or number of digits to the right
     *            of the decimal point, of the field
     * @param expPrecision the precision of the exponent
     * @param length length, in characters, of this edit descriptor
     */
    RepeatableFtnEditDescriptor(int repeat, char type, int width, 
                                int minWidth, int precision, 
                                int expPrecision, int length) {

        this.repeat = repeat;
        this.type = type;
        this.width = width;
        this.minWidth = minWidth;
        this.precision = precision;
        this.expPrecision = expPrecision;
        this.length = length;
    }


    /**
     * Parses the given string for a RepeatableFtnEditDescriptor.
     * 
     * @param format an edit descriptor from a format specification
     * @return RepeatableFtnEditDescriptor representation of given format
     *     String.
     * @throws java.lang.IllegalArgumentException thrown if the given 
     *            string does not contain a syntactically correct edit 
     *            descriptor
     */
    public static RepeatableFtnEditDescriptor 
        parseRepeatableFtnEditDescriptor(String format) 
        throws IllegalArgumentException {
    
        int repeat = 1;
        char type = 'B';
        int width = 0;
        int minWidth = 0;
        int precision = 0;
        int expPrecision = 0;
        int length = 0;

        if (format.length() == 0) {

            throw new IllegalArgumentException("No value");
        };

        int i;

        for (i = 0; i < format.length() && 
                    Character.isDigit(format.charAt(i)); i++) {

        };

        if (i > 0) {

            repeat = Integer.parseInt(format.substring(0, i));
        };

        type = parseType(format.substring(i, i + 1));

        i++;

        if (format.length() == i) {

            length = format.length();

            return new RepeatableFtnEditDescriptor(repeat, type, width, 
                                                   minWidth, precision, 
                                                   expPrecision, length);
        };

        int dotIndex = format.indexOf('.', i);

        if (dotIndex < 0) {

            dotIndex = format.length();
        };

        width = parseNumber(format.substring(i, dotIndex));

        if (dotIndex == format.length()) {

            length = format.length();

            return new RepeatableFtnEditDescriptor(repeat, type, width, 
                                                   minWidth, precision, 
                                                   expPrecision, length);
        };

        int eIndex = -1;

        if (type == 'E' || type == 'G') {

            eIndex = format.indexOf('E', dotIndex);
        };

        if (eIndex < 0) {

            eIndex = format.length();
        };

        precision = parseNumber(format.substring(dotIndex + 1, eIndex));

        if (type == 'I' || type == 'Z' || type == 'O' || type == 'B') {

            minWidth = precision;
        };

        if (eIndex == format.length()) {

            length = format.length();

            return new RepeatableFtnEditDescriptor(repeat, type, width, 
                                                   minWidth, precision, 
                                                   expPrecision, length);
        };

        expPrecision = parseNumber(format.substring(eIndex + 1));

        length = format.length();
    
        return new RepeatableFtnEditDescriptor(repeat, type, width, 
                                               minWidth, precision, 
                                               expPrecision, length);
    }


    /**
     * Provides the repeat count value.
     * 
     * @return the repeat count value
     */
    public int getRepeat() {

        return repeat;
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
     * @return the width value (0 if none specified and its not 
     *     required [type == 'A'])
     */
    public int getWidth() {

        return width;
    }


    /**
     * Provides the minimum width value.
     * 
     * @return the minimum width value (0 if not specified and not 
     *     required).
     */
    public int getMinWidth() {

        return minWidth;
    }


    /**
     * Provides the precision value.
     * 
     * @return the precision value (0 if not applicable)
     */
    public int getPrecision() {

        return precision;
    }


    /**
     * Provides the exponent's precision value.
     * 
     * @return the exponent's precision value (0 if not specified)
     */
    public int getExpPrecision() {

        return expPrecision;
    }


    /**
     * A constant holding the largest negative finite value of type
     * Double.
     */
    private static Double DOUBLE_MIN_VALUE = 
        new Double(-Double.MAX_VALUE);

    /**
     * A constant holding the largest positive finite value of type
     * Double.
     */
    private static Double DOUBLE_MAX_VALUE = 
        new Double(Double.MAX_VALUE);


    /**
     * Produces a warning about the specified width of this edit
     * descriptor if it is not sufficient to display any value.
     *
     * @return a warning message about the specified width of this 
     *     edit descriptor if it is not sufficient to display any 
     *     value.
     */
    public String widthWarning() {

        return widthWarning(DOUBLE_MIN_VALUE, DOUBLE_MAX_VALUE);
    }


    /**
     * Produces a warning about the specified width of this edit
     * descriptor if it is not sufficient to display any value within
     * the given values.
     *
     * @param minValue minimum limit of values to check.
     * @param maxValue maximum limit of values to check.
     * @return a warning message about the specified width of this 
     *     edit descriptor if it is not sufficient to display any 
     *     value.
     */
    public String widthWarning(Number minValue, Number maxValue) {

        if (width == 0 || minValue == null || maxValue == null) {

            return null;
        }

        int radix;                     // radix to use in string 
                                       // representation
        switch (type) {

            case 'Z':

                radix = 16;
                break;

            case 'O':

                radix = 8;
                break;

            case 'B':

                radix = 2;
                break;

            default:

                radix = 10;
                break;
         }
        int maxIntWidth = 
            Math.max(
                Integer.toString(minValue.intValue(), radix).length(),
                Integer.toString(maxValue.intValue(), radix).length());
                                       // maximum width (characters)
                                       // required for the string
                                       // representation of the integer
                                       // portion of minValue and 
                                       // maxValue
        switch (type) {

        case 'I':
        case 'Z':
        case 'O':
        case 'B':

            if (minWidth > width) {

                return "Minimum width " + minWidth + 
                    " should be less than width " + width + ".";
            }
            if (width < maxIntWidth) {

                return "Width of " + type + width + 
                    " FORMAT may be insufficient for values." +
                    "  Adjust FORMAT or VALIDMIN/VALIDMAX value " +
                    "to resolve.";
            }
            break;

        case 'F':

            //                        v= len of '.'
            if (width < maxIntWidth + 1 + precision) {

                return "Width of F" + width + 
                    " FORMAT may be insufficient for values." +
                    "  Adjust FORMAT or VALIDMIN/VALIDMAX value " +
                    "to resolve.";
            }
            break;

        case 'E':

            //          v= len of '-.'  v= len 'E-'
            if (width < 2 + precision + 2 + expPrecision) {

                return "Width of E" + width + 
                    " FORMAT may be insufficient for values." +
                    "  Adjust FORMAT or VALIDMIN/VALIDMAX value " +
                    "to resolve.";
            }
            int maxExpWidth = 
                Math.max(
                    ftnExponentDigits(minValue),
                    ftnExponentDigits(maxValue));
                                       // maximum width (characters)
                                       // required for the exponent
                                       // of the value in "fortran"
                                       // normalized scientific
                                       // notation
            if (expPrecision > 0 && expPrecision < maxExpWidth) {

                return "Exponent precision of " + expPrecision + 
                    " FORMAT may be insufficient for values." +
                    "  Adjust FORMAT or VALIDMIN/VALIDMAX value " +
                    "to resolve.";
            }
            break;

        default:

            // nothing for other types
            break;
        }

        return null;
    }


    /**
     * Determines the number of digits required to express the given
     * value in Fortran exponental form (0.xEy).
     *
     * @param value value that is to be evaluated.
     * @return the number of digits required to express the given
     *     value in Fortran exponential form.
     */
    private static int ftnExponentDigits(Number value) {

        double doubleValue = value.doubleValue();
                                       // double representation of 
                                       // value

        if (Double.isInfinite(doubleValue)) {

            return 9;  // length of -infinity
        }
        if (Double.isNaN(doubleValue)) {

            return 3;  // length of NaN
        }
        // no Math.log10() in Java 1.4, so

        double temp = Math.abs(value.doubleValue());
                                       // temporary computation value
        int exp = 1;                   // exponent value

        while (temp >= 1.0) {

            exp++;
            temp /= 10.0;
        }

        int expDigit = 1;              // number of digit for exponent
                                       // value
        while (exp > 10) {

            expDigit++;
            exp /= 10;
        }

        return expDigit;
    }

    /**
     * Value specifying the descriptor's repeat count.
     */
    protected int repeat = 1;

    /**
     * Value specifying the field's type.
     */
    protected char type = 'C';

    /**
     * Value specifying the field's width.
     */
    protected int width = 0;

    /**
     * Value specifying the fields minimum width (0 if not specified or
     * not applicable).
     */
    protected int minWidth = 0;

    /**
     * Value specifying the fields precision (0 if not applicable).
     */
    protected int precision = 0;

    /**
     * Value specifying the exponents precision (0 if none provide or 
     * isn't applicable).
     */
    protected int expPrecision = 0;


    /**
     * Parses the given string for the type value
     * 
     * @param s the string containing a type value
     * @return the type value
     * @throws java.lang.IllegalArgumentException thrown if a valid 
     *     type is not found in s
     */
    protected static char parseType(String s) 
        throws IllegalArgumentException {

        char type = Character.toUpperCase(s.charAt(0));

        if (type != 'I' && type != 'Z' && type != 'O' && type != 'B' &&
            type != 'F' && type != 'E' && type != 'D' && type != 'G' &&
            type != 'L' && type != 'A') {

            throw new IllegalArgumentException(
                          "Invalid edit descriptor '" + 
                          s.charAt(0) + "'");
        };

        return type;
    }


    /**
     * Test harness for this class.
     * 
     * @param args optional parameters containing additional strings to
     *            be parsed for edit descriptors.  If none are 
     *            provided, then just the built-in tests are performed.
     */
    public static void main(String[] args) {

        Number[] testNums = {
            new Double("1"), new Double("123"), 
            new Double("123456789"), new Double("1234567890")
        };

        System.out.println("ftnExponentDigits tests:");
        for (int i = 0; i < testNums.length; i++) {

            System.out.println("ftnExponentDigits(" +
                testNums[i].doubleValue() + ") = " + 
                ftnExponentDigits(testNums[i]));
        }

        String[] tests = {"I1", "I1.2", "Z2", "O3.2", "B50",
                          "F1.1", "E1.1", "E1.1E1",
                          "D1.1", "G1.1", "G1.1E1", "L1", "A", "A1",
                          "", "wrong", "Iwrong", "I1wrong", "I1.wrong",
                          "E1.1wrong", "E1.1Ewrong"};

        System.out.println("Standard tests:");
        for (int i = 0; i < tests.length; i++) {

            doTest(i, DOUBLE_MIN_VALUE, DOUBLE_MAX_VALUE, tests[i]);
        };

        if (args.length > 0) {

            if (args.length < 3) {

                System.out.println("ERROR: missing arguments");
                System.out.println(
                    "USAGE: RepeatableFtnEditDescriptor validMin " +
                    "validMax edit-descriptors...");
                return;
            }
            Double validMin = new Double(args[0]);
            Double validMax = new Double(args[1]);

            System.out.println("\nOptional tests:");
            for (int i = 2; i < args.length; i++) {

                doTest(i, validMin, validMax, args[i]);
            }

        }
    }

    
    /**
     * Used by the test harness for this class to check the syntax of 
     * the given string.
     * 
     * @param id identifies the test that is to be performed
     * @param s the string to perform a syntax check on
     * @param validMin minimum valid value
     * @param validMax maximum valid value
     * @return the results of the test (true &ge; no error)
     */
    private static boolean doTest(
        int id, Number validMin, Number validMax, String s) {

        RepeatableFtnEditDescriptor ed = null;

        System.out.print("Test[" + id + "]: '" + s + "' results = ");

        try {

            ed = parseRepeatableFtnEditDescriptor(s);

            System.out.println("valid.  ");

            String warning = ed.widthWarning(validMin, validMax);

            if (warning != null) {

                System.out.println("    " + warning);
            }
        }
        catch (IllegalArgumentException e) {

            System.out.println("syntax error");
            System.out.println("    " + e.getMessage());

            return false;
        };

        return true;
    }
}
