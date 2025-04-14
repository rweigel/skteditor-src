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
 * Copyright (c) 2024 United States Government as represented by 
 * the National Aeronautics and Space Administration. No copyright is 
 * claimed in the United States under Title 17, U.S.Code. All Other 
 * Rights Reserved.
 *
 * $Id: Doi.java,v 1.1 2024/04/22 11:25:47 btharris Exp $
 */
package gsfc.spdf.istp;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStream;
import java.io.IOException;
import javax.net.ssl.SSLHandshakeException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * This class represents a 
 * <a href="https://www.doi.org/">Digital Object Identifier</a> (DOI).
 * 
 * @author B. Harris
 * @version $Revision: 1.1 $
 */
public class Doi {

    /**
     * URL prefix for a DOI landing page.
     */
    public static final String URL_PREFIX = "https://doi.org/";


    /**
     * Gets the canonical DOI value.  That is, the value that is
     * lower case and without any "https://doi.org/" prefix.
     *
     * @param doi DOI value to convert to canonical value.
     * @return canonical DOI value.
     */
    public static String getCanonical(
        String doi) {

        return doi != null ?
               doi.toLowerCase().replaceAll(URL_PREFIX, "") :
               null;
    }


    /**
     * Gets the landing page URL for given a canonical DOI value.
     *
     * @param doi canonical DOI value.
     * @return landing page URL for the given DOI value.
     */
    public static String getLandingPageUrl(
        String doi) {

        return URL_PREFIX + doi;
    }


    /**
     * Determines if the given canonical DOI value is valid.
     *
     * @param doi canonical DOI value.
     * @return true if doi is valid, false otherwise.
     */
    public static boolean isValid(
        String doi) {

        // maybe validate with a regex first
        // maybe throw IllegalArgumentException with details
        // maybe do more than checking that page exists

        try {

            URL url = new URL(getLandingPageUrl(doi));

            HttpURLConnection con =
                    (HttpURLConnection)url.openConnection();

            InputStream stream = url.openStream();

            while (stream.read() != -1);

            stream.close();
        }
        catch (IOException e) {

            return false;
        }
        return true;
    }
}
