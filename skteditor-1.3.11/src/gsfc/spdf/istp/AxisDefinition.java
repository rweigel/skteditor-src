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
 * $Id: AxisDefinition.java,v 1.8 2022/08/01 16:29:09 btharris Exp $
 */
package gsfc.spdf.istp;

import java.lang.String;
import java.lang.IllegalArgumentException;
import java.lang.IndexOutOfBoundsException;
import java.util.StringTokenizer;


/**
 * This class represents an ISTP DISPLAY_TYPE argument axis definition.
 * 
 * @author B. Harris
 * @version $Revision: 1.8 $
 */

public class AxisDefinition 
    extends AbstractDisplayTypeParameter {

    /**
     * Creates an AxisDefinition based upon the given information.  Axis
     * definitions are of the form axis=variable(index1, ...) where:
     *   axis = x, y, or z
     *   variable is the name of a variable
     *   index1 identifies the element in an array or matrix (may also be
     *          a value of '*')
     * 
     * @param axis the left-hand-side, or axis value of an axis definition
     * @param rhs the right-hand-side of an axis definion
     * @exception java.lang.IllegalArgumentException thrown if the given 
     *            values don't represent a valid axis definition
     */
    public AxisDefinition(String axis, String rhs)
        throws IllegalArgumentException {

        this.axis = axis;

        if (axis.length() != 1) {

            throw new IllegalArgumentException("invalid axis identifier '" +
                                               axis + "'");
        }
        else {

            switch (axis.charAt(0)) {

            case 'x':
            case 'y':
            case 'z':

                // looks good, continue
                break;

            default:

                throw new IllegalArgumentException("invalid axis '" + axis +
                                                   "'");
            };
        };

        parseVariable(rhs);
    }


    /**
     * Provides the axis value.
     * 
     * @return the axis value
     */
    public String getAxis() {

        return axis;
    }


    /**
     * Provides the variable name value.
     * 
     * @return the variable name value
     */
    public String getVariable() {

        return variable;
    }


    /**
     * Provides the number of indices specified for this definition.
     * 
     * @return the number of indices specified for this definition
     */
    public int getNumIndices() {

        return numIndices;
    }


    /**
     * Provides the index's value.
     * 
     * @param i identifies the index whose value is to be returned (zero based)
     * @return the value of the specified index (-1 if '*' was specfied)
     * @exception java.lang.IndexOutOfBoundsException thrown if i &lt; 0 or 
     *            i &gt; numIndices()
     */
    public int getIndex(int i) 
        throws IndexOutOfBoundsException {

        if (i < 0 || i > numIndices) {

            throw new IndexOutOfBoundsException(i + 
                                                " is out of valid range: 0 - " +
                                                (numIndices - 1));
        };

        return index[i];
    }

    /**
     * An axis value (i.e., 'x', 'y', 'z').
     */
    protected String axis = "";

    /**
     * Variable name.
     */
    protected String variable = null;

    /**
     * Number of indices specified.
     */
    protected int numIndices = 0;

    /**
     * Specified index values.
     */
    protected int index[] = new int[2];


    /**
     * Parses the given string for a variable value.
     * 
     * @param value string containing a variable
     * @throws IllegalArgumentException thrown if the given string 
     *             doesn't contain a valid variable
     */
    protected void parseVariable(String value)
        throws IllegalArgumentException {

        StringTokenizer tokenizer = new StringTokenizer(value, "(,)", true);
        String delimiter = null;
        String token = null;

        switch (tokenizer.countTokens()) {

        case 0:

            throw new IllegalArgumentException("no variable specified");

        case 1:

            variable = value;
            return;
            // break;

        case 4:  // variable ( index )

            variable = tokenizer.nextToken();
            delimiter = tokenizer.nextToken();

            if (!delimiter.equals("(")) {

                throw new IllegalArgumentException("invalid delimiter '" +
                                                   delimiter + "'");
            };

            token = tokenizer.nextToken();

            index[0] = parseNumber(token);

            numIndices = 1;

            delimiter = tokenizer.nextToken();

            if (!delimiter.equals(")")) {

                throw new IllegalArgumentException("invalid delimiter '" +
                                                   delimiter + "'");
            };
            break;

        case 6:  // variable ( index1 , index2 )

            variable = tokenizer.nextToken();
            delimiter = tokenizer.nextToken();

            if (!delimiter.equals("(")) {

                throw new IllegalArgumentException("invalid delimiter '" +
                                                   delimiter + "'");
            };

            numIndices = 2;
            token = tokenizer.nextToken();

            if (token.equals("*")) {

                index[0] = -1;
            }
            else {

                index[0] = parseNumber(token);
            };

            delimiter = tokenizer.nextToken();

            if (!delimiter.equals(",")) {

                throw new IllegalArgumentException("invalid delimiter '" +
                                                   delimiter + "'");
            };

            token = tokenizer.nextToken();

            if (token.equals("*")) {

                index[1] = -1;
            }
            else {

                index[1] = parseNumber(token);
            };

            delimiter = tokenizer.nextToken();

            if (!delimiter.equals(")")) {

                throw new IllegalArgumentException("invalid delimiter '" +
                                                   delimiter + "'");
            };
            break;
        
        default:

            throw new IllegalArgumentException("invalid syntax '" + value +
                                               "'");
        };
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
            "", "x", "x=", "a=b", "x=wrong(", "x=wrong(a)", "x=wrong(1",
            "x=wrong(1,"};

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

        AxisDefinition ad = null;

        System.out.print("Test[" + id + "]: '" + s + "' results = ");

        try {

            StringTokenizer tokenizer = new StringTokenizer(s.trim(), "=");

            if (tokenizer.countTokens() != 2) {

                throw new IllegalArgumentException("'" + s +
                                      "' is not of the form 'x=variable'");
            };

            ad = new AxisDefinition(tokenizer.nextToken(), 
                                    tokenizer.nextToken());

            System.out.println("valid");
            System.out.println("    " + ad.getAxis() + " = " + 
                               ad.getVariable());
        }
        catch (IllegalArgumentException e) {

            System.out.println("syntax error");
            System.out.println("    " + e.getMessage());

            return false;
        };

        return true;
    }

}
