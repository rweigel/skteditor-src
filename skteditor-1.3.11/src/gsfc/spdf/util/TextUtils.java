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
 * $Id: TextUtils.java,v 1.14 2024/10/25 18:38:37 btharris Exp $
 */


/**
 * TextUtils.java
 *
 *
 * Created: Fri Jan 22 09:52:00 1999
 *
 * @author Phil Williams
 * @version $Revision: 1.14 $
 */
package gsfc.spdf.util;

import java.lang.StringBuffer;
import java.lang.String;
import java.util.StringTokenizer;
import java.util.Vector;

public class TextUtils  {

    /**
     * Replace spaces with theChar
     *
     * @param theString the string on which to operate
     * @param theChar   the character to replace the spaces
     * @return the string with spaces replaced with theChar
     */
    public static String replaceSpaces(String theString, char theChar) {
	StringBuffer unString = new StringBuffer();
	for (StringTokenizer st = new StringTokenizer(theString, " ");
	     st.hasMoreTokens(); ) {
	    unString.append(st.nextToken());
	    unString.append(theChar);
	}
	// Chop off the last underscore
        if (unString.length() > 0) {

            unString.setLength(unString.length() - 1);
        }
	return unString.toString();
    }

    /**
     * replace theChar with spaces
     *
     * @param theString the string on which to operate
     * @param theChar   the character to replace with spaces
     * @return the string with theChar replaced with spaces
     */
    public static String replaceChar(String theString, String theChar) {
	StringBuffer words = new StringBuffer();
	String cw;
	StringTokenizer st = new StringTokenizer(theString, theChar);
	if (st.hasMoreTokens()) {
	    do {
		words.append(st.nextToken());
		words.append(" ");
	    } while (st.hasMoreTokens());
	    words.setLength(words.length()-1);
	} else
	    words.append(theString);
	return words.toString();
    }

    /**
     * Capitalize the first character of each word in the string
     *
     * @param theString string to modify.
     * @return the String with title case
     */
    public static String toTitleCase(String theString) {
	StringTokenizer st = new StringTokenizer(theString, " ");
	StringBuffer    tc = new StringBuffer();
	String curWord;
	while (st.hasMoreTokens()) {
	    curWord = st.nextToken().toLowerCase();
	    tc.append(String.valueOf(Character.toTitleCase(curWord.charAt(0))).concat(curWord.substring(1)));
	    tc.append(" ");
	}

	// Chop off the last space
	tc.setLength(tc.length()-1);

	return tc.toString();
    }
    
    /**
     * Replaces all "&gt;" with "&amp;&gt;", "&lt;" with "&amp;&lt;"
     * and all "&amp;" with "<CODE>&amp;</CODE>"
     *
     * @param text text to modify.
     * @return resulting modified String.
     */
    public static String htmlify(String text) {

        if (text == null) {

            return null;
        }

        String[] targets = {"&", "<", ">", "\"", "\'"};
        String[] replacements = {"&amp;", "&lt;", "&gt;", "&#34", 
            "&#39"};

        String htmlText = text;
        for (int i = 0; i < targets.length; i++) {

            htmlText = htmlText.replaceAll(targets[i], replacements[i]);
        }
        return htmlText;
    }
    
    /**
     * Finds all links in text and creates a properly formed HTML link
     *
     * @param text text to modify.
     * @param target link target.
     * @return resulting modified String.
     */
    public static String hotlinkify(String text, String target) {
	int httpIdx, from = 0;
	String link;
	String period;
	StringBuffer sb = new StringBuffer();
	StringBuffer hotlink = new StringBuffer();
	while ((httpIdx = text.indexOf("http", from)) > 0) {
	    try {
		link = text.substring(httpIdx, text.indexOf(" ", httpIdx));
	    } catch (java.lang.StringIndexOutOfBoundsException e) {
		// String ends with a link, so just go to the end
		link = text.substring(httpIdx);
	    }
	    
	    if (link.lastIndexOf(".") == link.length()-1) {
		link = link.substring(0, link.length()-1);
		period = ".";
	    } else
		period = "";
	    hotlink.append("<a href=\""+link+"\"");
	    if (target != null)
		hotlink.append(" target=\""+target+"\"");
	    hotlink.append(">"+period);
	    hotlink.append(link);
	    hotlink.append("</a>");
	    sb.append(text.substring(0, httpIdx));
	    sb.append(hotlink.toString());
	    sb.append(text.substring(httpIdx+link.length()));
	    sb.append(" ");
	    text = sb.toString();
	    from = httpIdx + hotlink.length();
	    sb.setLength(0);
	    hotlink.setLength(0);
	}

	return text;
    }

    /**
     * Splits the given string at blank characters with no resulting string
     * being longer than the given maxLen.
     *
     * @param s the string that is to be split
     * @param maxLen the maximum number of characters that may be in any of
     *               the resulting strings
     * @return array of substrings of the given string
     */
    public static String[] splitBetweenWords(String s, int maxLen) {

        Vector<String> lines = new Vector<>();

        for (int i = 0; i < s.length(); ) {

            if (i + maxLen >= s.length()) {

                lines.addElement(s.substring(i));
                i = s.length();
            }
            else {

                int nextBreak = s.lastIndexOf(' ', i + maxLen);

                if (nextBreak > 0) {

                    if (nextBreak < i) {

                        lines.addElement(s.substring(i, i + maxLen));
                        i += maxLen;
                    }
                    else {

                        lines.addElement(s.substring(i, nextBreak));
                        i = nextBreak + 1;
                    };
                }
                else {

                    lines.addElement(s.substring(i, i + maxLen));
                    i += maxLen;
                };
            };
        };

        String[] lineArray = new String[lines.size()];

        for (int i = 0; i < lines.size(); i++) {

            lineArray[i] = (String)lines.elementAt(i);
        };

        return lineArray;
    }


    /**
     * A test harness for this class.  Currently it only tests 
     * splitBetweenWords().
     *
     * @param args command line arguments.  Not currently used.
     */
    public static void main(String[] args) {

        String[] tests = {"a b c d e", "abcde", "abc", "  a    b     ", 
                          "a", "a bcdef"};

        for (int i = 0; i < tests.length; i++) {

            String[] lines = splitBetweenWords(tests[i], 3);

            System.out.println("Results of spliting '" + tests[i] + "' are:");

            for (int j = 0; j < lines.length; j++) {

                System.out.println("  '" + lines[j] + "'");
            };
            System.out.println();
        };
    }


} // TextUtils
