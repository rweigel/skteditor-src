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
 * $Id: AbstractDisplayTypeParameter.java,v 1.7 2022/08/01 16:17:57 btharris Exp $
 */
package gsfc.spdf.istp;

import java.lang.String;
import java.lang.IllegalArgumentException;
import java.lang.IndexOutOfBoundsException;
import java.util.StringTokenizer;


/**
 * This class represents an abstract ISTP DISPLAY_TYPE argument parameter.
 * 
 * @author B. Harris
 * @version $Revision: 1.7 $
 */

public abstract class AbstractDisplayTypeParameter {

    /**
     * Creates a concreate DISPLAY_TYPE parameter based upon the given string 
     * value (i.e., this is a "virtual constructor").
     * 
     * @param value string representation of a DISPLAY_TYPE parameter
     * @return the DISPLAY_TYPE parameter corresponding to the given string
     * @exception java.lang.IllegalArgumentException thrown if the given 
     *            value doesn't contain a valid parameter
     */
    public static AbstractDisplayTypeParameter parseParameter(String value)
        throws IllegalArgumentException {

        AbstractDisplayTypeParameter parameter = null;
                                      // the concreate parameter that is
                                      //  constructed and returned

        StringTokenizer tokenizer = new StringTokenizer(value.trim(), "=");
                                      // tokenizer for parsing the given
                                      //  value

        switch (tokenizer.countTokens()) {

        case 0:

            throw new IllegalArgumentException("missing parameter value");

        case 1:

            return new ParameterValue(tokenizer.nextToken());

        case 2:

            String lhsToken = tokenizer.nextToken();

            if (lhsToken.equals("x") || lhsToken.equals("y") ||
                lhsToken.equals("z")) {

                return new AxisDefinition(lhsToken, tokenizer.nextToken());
            }
            else if (lhsToken.equals("coord")) {

                return CoordinateSystem.parseCoordinateSystem(
                                                   tokenizer.nextToken());
            };
            // intentionally no break

        default:

            throw new IllegalArgumentException("invalid parameter '" +
                                               value + "'");
        }
    }


    /**
     * Parses the given string for an unsigned number.
     * 
     * @param s string to be parsed for an unsigned number
     * @return the number found in s
     * @exception java.lang.IllegalArgumentException thrown if a valid unsigned
     *            number is not found in s
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
     * Test harness for this class.
     * 
     * @param args optional parameters containing additional strings to be 
     *        parsed for conversion specifications.  If none are provided, then
     *        just the built in tests are performed
     */
    public static void main(String[] args) {


        String[] tests = {
            "x=Geo_Lat", "y=Geo_Lon", "z=image",
            "y=Proton_DIntn_Engy(1)", "z=Sigma_He_1VQ(1,*)", "z=Sigma_H(*,1)",
            "coord=gsm", "90000", 
            "", "x", "x=", "a=b", "x=wrong(", "x=wrong(a)", "x=wrong(1",
            "x=wrong(1,", "coord=wrong"};

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
     * @return results of the test (true on error)
     */
    private static boolean doTest(int id, String s) {

        AbstractDisplayTypeParameter param = null;

        System.out.print("Test[" + id + "]: '" + s + "' results = ");

        try {

            param = AbstractDisplayTypeParameter.parseParameter(s);

            System.out.println("valid");
            System.out.println("    " + param.getClass().getName());
        }
        catch (IllegalArgumentException e) {

            System.out.println("syntax error");
            System.out.println("    " + e.getMessage());

            return false;
        };

        return true;
    }

}
