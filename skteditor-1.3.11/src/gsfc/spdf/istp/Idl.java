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
 * $Id: Idl.java,v 1.5 2024/10/25 18:37:57 btharris Exp $
 */
package gsfc.spdf.istp;

import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * This class contains limited knowledge about the syntax of the
 * <a href="http://www.ittvis.com/idl/">Interactive Data Lanaguage 
 * (IDL)</a>.  Some ISTP guidlelines were defined to allow easy analysis
 * of ISTP data by IDL.
 * 
 * @author B. Harris
 * @version $Revision: 1.5 $
 */
public class Idl {

    /**
     * Pattern to match Interactive Data Language (IDL) variable
     * names that must begin with a letter.
     */
    private static final Pattern IDL_VAR_NAME_PATTERN =
        Pattern.compile("[a-zA-Z].*");

    /**
     * Pattern to match and capture any Interactive Data Language (IDL)
     * "special characters" as defined in the IDL Reference Guide.
     */
    private static final Pattern IDL_SPECIAL_CHAR_PATTERN = 
        Pattern.compile(".*(\\\\|/|\\.|%|!|@|#|\\^|&|\\*|\\(|\\)|-|" +
                        "\\+|=|`|~|\\||\\?|<|>| ).*");

    /**
     * Interactive Data Language (IDL) keywords for restricting ISTP
     * variable names.
     */
    public static final TreeSet<String> IDL_KEYWORDS = new TreeSet<>();

    static {
        IDL_KEYWORDS.add("AND");
        IDL_KEYWORDS.add("BEGIN");
        IDL_KEYWORDS.add("BREAK");
        IDL_KEYWORDS.add("CASE");
        IDL_KEYWORDS.add("COMMON");
        IDL_KEYWORDS.add("COMPILE_OPT");
        IDL_KEYWORDS.add("CONTINUE");
        IDL_KEYWORDS.add("DO");
        IDL_KEYWORDS.add("ELSE");
        IDL_KEYWORDS.add("END");
        IDL_KEYWORDS.add("ENDCASE");
        IDL_KEYWORDS.add("ENDELSE");
        IDL_KEYWORDS.add("ENDFOR");
        IDL_KEYWORDS.add("ENDIF");
        IDL_KEYWORDS.add("ENDREP");
        IDL_KEYWORDS.add("ENDSWITCH");
        IDL_KEYWORDS.add("ENDWHILE");
        IDL_KEYWORDS.add("EQ");
        IDL_KEYWORDS.add("FOR");
        IDL_KEYWORDS.add("FORWARD_FUNCTION");
        IDL_KEYWORDS.add("FUNCTION");
        IDL_KEYWORDS.add("GE");
        IDL_KEYWORDS.add("GOTO");
        IDL_KEYWORDS.add("GT");
        IDL_KEYWORDS.add("IF");
        IDL_KEYWORDS.add("INHERITS");
        IDL_KEYWORDS.add("LE");
        IDL_KEYWORDS.add("LT");
        IDL_KEYWORDS.add("MOD");
        IDL_KEYWORDS.add("NE");
        IDL_KEYWORDS.add("NOT");
        IDL_KEYWORDS.add("OF");
        IDL_KEYWORDS.add("ON_IOERROR");
        IDL_KEYWORDS.add("OR");
        IDL_KEYWORDS.add("PRO");
        IDL_KEYWORDS.add("REPEAT");
        IDL_KEYWORDS.add("SWITCH");
        IDL_KEYWORDS.add("THEN");
        IDL_KEYWORDS.add("UNTIL");
        IDL_KEYWORDS.add("WHILE");
        IDL_KEYWORDS.add("XOR");
    }


    /**
     * Checks the given value for IDL identifier restrictions.
     *
     * @param value value to check
     * @return null if the given value is a valid IDL identifier.  
     *     Otherwise a string describing the invalid characteristic
     *     of the given value is return.
     */
    public static String validateIdentifier(String value) {

        if (value == null || value.trim().length() == 0) {

            return "An identifer must be at least one character.";
        }

        if (IDL_KEYWORDS.contains(value.toUpperCase())) {

            return "'" + value + "' is an IDL keyword and therefore " +
                   "not valid a valid identifer.";
        }

        if (!IDL_VAR_NAME_PATTERN.matcher(value).matches()) {

            return "Identifers must begin with a letter.";
        }

        Matcher specCharMatcher = 
            IDL_SPECIAL_CHAR_PATTERN.matcher(value);
                                        // IDL special character pattern
                                        //  matcher

        if (specCharMatcher.matches()) {

            String specChar = specCharMatcher.group(1);
                                        // the IDL special character
            return "'" + specChar + "' is an IDL special character " +
                   "and not valid in an identifer.";
        }

        return null;
    }

}
