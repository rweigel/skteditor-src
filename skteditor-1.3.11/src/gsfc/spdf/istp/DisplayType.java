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
 * $Id: DisplayType.java,v 1.23 2024/09/04 16:45:04 btharris Exp $
 */
package gsfc.spdf.istp;


import java.lang.Character;
import java.lang.Integer;
import java.lang.String;
import java.lang.IllegalArgumentException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;



/**
 * This class represents an ISTP DISPLAY_TYPE variable attribute.  
 *
 * See examples in test driver {@link #main(String[])}.
 * 
 * @author B. Harris
 * @version $Revision: 1.23 $
 */
public class DisplayType 
    implements Cloneable {

    /**
     * Display type.
     */
    private String type = null;

    /**
     * Keywords.
     */
    private Vector<String> keywords = new Vector<>();

    /**
     * Parameters.
     */
    private Vector<AbstractDisplayTypeParameter> parameters = 
        new Vector<>();


    @Override
    @SuppressWarnings("unchecked")
    public Object clone() {

        try {

            DisplayType dt = (DisplayType)super.clone();

            dt.keywords = (Vector<String>)dt.keywords.clone();
            dt.parameters = 
                (Vector<AbstractDisplayTypeParameter>)dt.parameters.clone();

            return dt;
        }
        catch (CloneNotSupportedException e) {

            return null;
        }
    }


    /**
     * Constructs a DisplayType from the given string representation.
     * 
     * @param value string representation of DisplayType
     * @exception java.lang.IllegalArgumentException thrown if the 
     *            given string does not contain a syntactically correct 
     *            DISPLAY_TYPE value
     */
    public DisplayType(String value) 
        throws IllegalArgumentException {

        StringTokenizer tokenizer = 
            new StringTokenizer(value, ">", true);
                                       // tokenizer for parsing the 
                                       // given value into DISPLAY_TYPE
                                       // components
        if (tokenizer.hasMoreTokens()) {

            type = tokenizer.nextToken();
        }
        else {

            throw new IllegalArgumentException("missing display type");
        };

        if (!isValidDisplayType(type)) {

            throw new IllegalArgumentException(
                          "invalid display type '" + type + "'");
        };

        while (tokenizer.countTokens() > 3) {

            tokenizer.nextToken();  // the ">" token

            String keyword = tokenizer.nextToken();
                                    // a keyword value from the given 
                                    // string

            if (!isValidKeyword(keyword)) {

                throw new IllegalArgumentException("invalid keyword '" +
                                                   keyword + "'");
            };

            keywords.addElement(keyword);
        };

        String token = null;        // the next token obtained from the
                                    // given string
        Vector<String> paramTokens = new Vector<>();
                                    // contains all the parameter values
                                    // (as strings) obtained from the 
                                    // given string        
        switch (tokenizer.countTokens()) {

        case 0:

            return;

        case 2:

            tokenizer.nextToken();  // the ">" token

            token = tokenizer.nextToken(",");

            if (token.indexOf('(') > -1 && token.indexOf(')') < 0) {

                if (tokenizer.countTokens() < 2) {
       
                    throw new IllegalArgumentException(
                                    "incomplete axis definition");
                };
                tokenizer.nextToken();  // the "," token
                paramTokens.addElement(token + "," + 
                    tokenizer.nextToken());
            }
            else {

                paramTokens.addElement(token);
            };
            break;
        
        default:

            throw new IllegalArgumentException(
                              "missing keyword or axis definition");
        };
            
        while (tokenizer.countTokens() > 1) {

            tokenizer.nextToken();  // the "," token
            token = tokenizer.nextToken();

            if (token.indexOf('(') > -1 && token.indexOf(')') < 0) {

                if (tokenizer.countTokens() < 2) {
       
                    throw new IllegalArgumentException(
                                    "incomplete axis definition");
                };
                tokenizer.nextToken();  // the "," token
                paramTokens.addElement(token + "," + 
                    tokenizer.nextToken());
            }
            else {

                paramTokens.addElement(token);
            };
        };

        if (tokenizer.hasMoreTokens()) {

            throw new IllegalArgumentException(
                          "missing axis definition");
        };

        for (int i = 0; i < paramTokens.size(); i++) {

            parameters.addElement(
                AbstractDisplayTypeParameter.parseParameter(
                    (String)paramTokens.elementAt(i)));
        };
    }


    /**
     * Parses the given string for a DISPLAY_TYPE value.
     * 
     * @param s string containing a DISPLAY_TYPE value
     * @return DisplayType representation of the given value
     * @throws java.lang.IllegalArgumentException thrown if the 
     *             given string does not contain a syntactically 
     *             correct DISPLAY_TYPE value
     */
    public static DisplayType parseDisplayType(String s) 
        throws IllegalArgumentException {

        return new DisplayType(s);
    }


    /**
     * Gets the DISPLAY_TYPE value from the given string.  That is
     * the string before the first '&gt;' character.
     *
     * @param s the string to parse.
     * @return the DISPLAY_TYPE value or null if it was not found.
     */
    public static String getDisplayTypeValue(String s) {

        if (s == null) {

            return null;
        }
        int i = s.indexOf('>');        // index of first >

        if (i > -1) {

            return s.substring(0, i);
        }
        return s;
    }


    /**
     * Gets the arguments value from the given string.  That is
     * the string after the first '&gt;' character.
     *
     * @param s the string to parse.
     * @return the arguments value or null if it was not found.
     */
    public static String getArgumentsValue(String s) {

        if (s == null) {

            return null;
        }
        int i = s.indexOf('>');        // index of first >

        if (i > -1) {

            return s.substring(i + 1);
        }
        return null;
    }


    /**
     * Creates a DisplayType value from a Logical_source value and 
     * variable name.
     *
     * @param source Logical_source value.
     * @param varName variable name.
     * @return DisplayType value associated with the given 
     *             Logical_source value and variable name or null 
     *             if the given source does not correspond to a 
     *             DisplayType value.
     */
    public static String createValueFromLogicalSourceVarName(
        String source, String varName) {

        if (source != null && source.length() > 9) {

            String sourceKey = source.substring(3, 9);

            if (sourceKey.equals("OR_DEF") || 
                sourceKey.equals("OR_PRE")) {

                StringTokenizer st = 
                    new StringTokenizer(varName, "_");

                int parts = st.countTokens();
                Vector<String> varParts = new Vector<>(parts);

                do {
                    varParts.addElement(st.nextToken());

                } while (st.hasMoreTokens());

                if (parts > 1 && ((String)varParts.elementAt(parts-2)).
                                            equalsIgnoreCase("pos")) {

                    return "orbit>coord=" +
                               ((String)varParts.lastElement()).
                                   toLowerCase();
                }
            }
        }

        return null;
    }


    /**
     * Returns a string representation of this DisplayType.
     *
     * @return a string representation of this DisplayType.
     */
    public String toString() {

        StringBuffer buf = new StringBuffer(getType());
                                       // buffer in which to construct
                                       // string representation
        String arguments = getArguments();
                                       // arguments value
        if (arguments.length() > 0) {

            buf.append(">").append(arguments);
        }

        return buf.toString();
    }


    /**
     * Returns a string representation of this DisplayType arguments.
     *
     * @return a string representation of this DisplayType arguments.
     */
    public String getArguments() {

        StringBuffer buf = new StringBuffer();
                                       // buffer in which to construct
                                       // string representation
        List keywords = getKeywords(); // keyword values

        if (keywords.size() > 0) {

            buf.append(keywords.get(0));
        }
        for (int i = 1; i < keywords.size(); i++) {

            buf.append(">").append(keywords.get(i));
        }
        List params = getParameters(); // parameter values

        if (params.size() > 0) {

            buf.append(">").append(params.get(0));
        }
        for (int i = 1; i < params.size(); i++) {

            buf.append(",").append(params.get(i));
        }

        return buf.toString();
    }



    /**
     * Provides the type value.
     * 
     * @return the type value
     */
    public String getType() {

        return type;
    }


    /**
     * Provides the keyword values.
     * 
     * @return the keyword values
     */
    public List getKeywords() {

        return keywords;
    }


    /**
     * Provides the parameters value.
     * 
     * @return the parameters value 
     */
    public List getParameters() {

        return parameters;
    }


    /**
     * Tests whether the given keyword is a valid keyword.
     * 
     * @param keyword value to be tested
     * @return whether the given keyword is valid
     */
    public boolean isValidKeyword(String keyword) {

        return isValidKeyword(type, keyword);
    }


    /**
     * Tests whether the given DISPLAY_TYPE value is valid.
     * 
     * @param displayType the value to be tested
     * @return whether the given value is valid
     */
    public static boolean isValidDisplayType(String displayType) {

        return validDisplayTypes.containsKey(displayType);
    }


    /**
     * Provides the textual value (which is suitable for presentation 
     * to a user) of this DISPLAY_TYPE value.
     * 
     * @return the text value associated with this DISPLAY_TYPE 
     *         value.
     */
    public String getDisplayTypeText() {

        return getDisplayTypeText(getType());
    }


    /**
     * Provides the textual value (which is suitable for presentation 
     * to a user) of the given DISPLAY_TYPE value.
     * 
     * @param displayType DISPLAY_TYPE value
     * @return the text value associated with the given DISPLAY_TYPE 
     *         value.
     */
    public static String getDisplayTypeText(String displayType) {

        if (displayType == null) {

            return null;
        }

        DisplayTypeAttributes attributes = (DisplayTypeAttributes)
            validDisplayTypes.get(displayType.toLowerCase());

        if (attributes != null) {

            return attributes.getTitle();
        }

        return null;
    }


    /**
     * Provides all valid DISPLAY_TYPE values.
     * 
     * @return all valid DISPLAY_TYPE values
     */
    public static Enumeration getDisplayTypes() {

        return validDisplayTypes.keys();
    }


    /**
     * Provides the valid DisplayType values for a variable of the 
     * given dimensionality in order of decreasing use cases.  That is,
     * element 0 is used more often than element 1.
     *
     * @param dimension variable dimension.
     * @return valid DisplayType values for a variable of the given
     *     dimensionality in order of decreasing use cases.
     */
    public static String[] getValidDisplayTypeValues(int dimension) {

        switch (dimension) {

        case 0:

            return new String[] {
                       TIME_SERIES_TYPE, 
                       TIME_TEXT_TYPE, 
                       NO_PLOT_TYPE};

        case 1:

            return new String[] {
                       SPECTROGRAM_TYPE,
                       STACK_PLOT_TYPE,
                       ORBIT_TYPE,
                       TOPSIDE_IONOGRAM_TYPE,
                       TIME_SERIES_TYPE,
                       TIME_TEXT_TYPE, 
                       NO_PLOT_TYPE};
        case 2:

            return new String[] {
                       IMAGE_TYPE,
                       FLUX_IMAGE_TYPE,
                       FUV_IMAGE_TYPE,
                       MAP_IMAGE_TYPE,
                       MAPPED_TYPE,
                       PLASMAGRAM_TYPE,
                       RADAR_VECTOR_TYPE,
                       SKYMAP_TYPE,
                       WIND_PLOT_TYPE,
                       MOVIE_TYPE,
                       FLUX_MOVIE_TYPE,
                       FUV_MOVIE_TYPE,
                       MAP_MOVIE_TYPE,
                       PLASMA_MOVIE_TYPE,
                       SKYMAP_MOVIE_TYPE,
                       SPECTROGRAM_TYPE,
                       STACK_PLOT_TYPE,
                       TIME_SERIES_TYPE,
                       NO_PLOT_TYPE};

        case 3:

            return new String[] {
                       IMAGE_TYPE,
                       FLUX_IMAGE_TYPE,
                       MAP_IMAGE_TYPE,
                       SPECTROGRAM_TYPE,
                       TIME_SERIES_TYPE,
                       NO_PLOT_TYPE};

        case 4:
        case 5:
        case 6:

            return new String[] {
                       DEFAULT_DIMENSION_TYPE};

        default:

            return new String[] {};
        }
    }


    /**
     * Provides the valid DisplayType Title values for a variable of 
     * the given dimensionality in order of decreasing use cases.  That
     * is, element 0 is used more often than element 1.
     *
     * @param dimension variable dimension.
     * @return valid DisplayType Title values for a variable of the 
     *     given dimensionality in order of decreasing use cases.
     */
    public static String[] getValidDisplayTypeTitles(int dimension) {

        String[] validDisplayTypeValues = 
            getValidDisplayTypeValues(dimension);
                                       // valid display type values
        String[] validTitles = 
            new String[validDisplayTypeValues.length];
                                       // valid display type titles

        for (int i = 0; i < validTitles.length; i++) {

            validTitles[i] = 
                getDisplayTypeText(validDisplayTypeValues[i]);
        }

        return validTitles;
    }


    /**
     * Provides the valid DisplayType values for a variable of the 
     * given dimensionality in order of decreasing use cases.  That is,
     * element 0 is used more often than element 1.
     *
     * @param dimension variable dimension.
     * @return valid DisplayType values for a variable of the given
     *     dimensionality in order of decreasing use cases.
     */
    public static DisplayType[] getValidDisplayTypes(int dimension) {

        String[] validDisplayTypeValues = 
            getValidDisplayTypeValues(dimension);
                                       // valid display type values
        DisplayType[] displayTypes = 
            new DisplayType[validDisplayTypeValues.length];
                                       // valid display types

        for (int i = 0; i < displayTypes.length; i++) {

            displayTypes[i] = (DisplayType)
                ((DisplayTypeAttributes)validDisplayTypes.get(
                                  validDisplayTypeValues[i])).clone();
        }

        return displayTypes;
    }

        
    /**
     * Provides the textual values for all possible DISPLAY_TYPE values.
     * 
     * @return textual values for all DISPLAY_TYPE values
     */
    public static Vector<String> getDisplayText() {

        Vector<String> text = new Vector<>();    // text that is to be returned

        for (Enumeration attributes = validDisplayTypes.elements();
             attributes.hasMoreElements(); ) {

            text.addElement(((DisplayTypeAttributes)
                     attributes.nextElement()).getTitle());
        };

        return text;
    }


    /**
     * Tests whether the given keyword is valid in the context of the 
     * given DISPLAY_TYPE value.
     * 
     * @param type DISPLAY_TYPE value
     * @param keyword keyword value that is to be tested
     * @return whether the given keyword is valid within the context of 
     *     the given DISPLAY_TYPE value.
     */
    public static boolean isValidKeyword(String type, String keyword) {

        DisplayTypeAttributes attributes = 
            (DisplayTypeAttributes)validDisplayTypes.get(type);

        if (attributes == null) {

            return false;
        };

        return attributes.isValidKeyword(keyword);
    }


    /**
     * This class contains attributes associated with a given 
     * DISPLAY_TYPE value.
     * 
     * @author B. Harris
     * @version $Revsion$
     */
    protected static class DisplayTypeAttributes
    implements Cloneable {

        /**
         * Constructs a DisplayTypeAttributes object with the given 
         * title.
         *
         * @param title a textual value used for presentation to the 
         *            user of a display type value.
         */
        public DisplayTypeAttributes(String title) {

            this.title = title;
        }


        /**
         * Constructs a DisplayTypeAttributes object with the given 
         * title and valid keywords.
         *
         * @param title a textual value used for presentation to the 
         *            user of a display type value.
         * @param validKeywords valid display types keywords
         */
        public DisplayTypeAttributes(
            String title, String[] validKeywords) {

            this.title = title;

            for (int i = 0; i < validKeywords.length; i++) {

                this.validKeywords.add(validKeywords[i]);
            };
        }


        /**
         * Provides the textual title for a DISPLAY_TYPE value.
         * 
         * @return the textual title
         */
        public String getTitle() {

            return title;
        }


        /**
         * Provides all the valid keyword values for a particular 
         * DISPLAY_TYPE value.
         * 
         * @return all valid keywords
         */
        public Iterator getValidKeywords() {

            return validKeywords.iterator();
        }


        /**
         * Indicates whether the given keyword value is a valid value.
         * 
         * @param keyword the keyword value whose validity is to be 
         *            tested
         * @return whether the given keyword value is valid
         */
        public boolean isValidKeyword(String keyword) {

            return validKeywords.contains(keyword);
        }


        /**
         * A textual value used for presentation to the user of a 
         * display type value.  e.g., "time_series" is the actual 
         * display type value stored in a CDF variable attribute but 
         * "Time Series" is the title displayed to the user on a GUI.
         */
        protected String title;


        /**
         * Set of valid keywords.  
         */
        private HashSet<String> validKeywords = new HashSet<>();

/* why is this missing?  not implemented yet?
        private HashSet<String> validParameters = new HashSet<String>();
*/
    
        @Override
        @SuppressWarnings("unchecked")
        public Object clone() {

            try {

                DisplayTypeAttributes dta = 
                    (DisplayTypeAttributes)super.clone();

                dta.validKeywords = 
                    (HashSet<String>)dta.validKeywords.clone();

                return dta;
            }
            catch (CloneNotSupportedException e) {

                return null;
            }
        }
    }


    public final static String BAR_CHART_TYPE = "bar_chart";
    public final static String FLUX_IMAGE_TYPE = "flux_image";
    public final static String FLUX_MOVIE_TYPE = "flux_movie";
    public final static String FUV_IMAGE_TYPE = "fuv_image";
    public final static String FUV_MOVIE_TYPE = "fuv_movie";
    public final static String LABEL_TYPE = "label";
    public final static String LINE_TYPE = "line";
    public final static String LIST_TYPE = "list";
/* changed to NO_PLOT on 3/27/14
    public final static String NOT_PLOTTABLE_TYPE = "not_plottable";
*/
    public final static String NO_PLOT_TYPE = "no_plot";
    public final static String TIME_TEXT_TYPE = "time_text";
    public final static String WIND_MOVIE_TYPE = "wind_movie";
    public final static String WIND_PLOT_TYPE = "wind_plot";
    public final static String XY_PLOT_TYPE = "xy_plot";
    public final static String PLASMA_MOVIE_TYPE = "plasma_movie";
    public final static String PLASMAGRAM_TYPE = "plasmagram";
    public final static String TIME_SERIES_TYPE = "time_series";
    public final static String SPECTROGRAM_TYPE = "spectrogram";
    public final static String ORBIT_TYPE = "orbit";
    public final static String TOPSIDE_IONOGRAM_TYPE = 
                                   "topside_ionogram";
    public final static String RADAR_VECTOR_TYPE = "radar_vector";
    public final static String STACK_PLOT_TYPE = "stack_plot";
    public final static String MAP_IMAGE_TYPE = "map_image";
    public final static String IMAGE_TYPE = "image";
    public final static String MAP_MOVIE_TYPE = "map_movie";
    public final static String MOVIE_TYPE = "movie";
    public final static String MAPPED_TYPE = "mapped";
    public final static String SKYMAP_TYPE = "skymap";
    public final static String SKYMAP_MOVIE_TYPE = "skymap_movie";

public final static String DEFAULT_DIMENSION_TYPE = "default_dimension";

    /**
     * Valid display types and their associated textual value.
     */
    protected final static Hashtable<String, DisplayTypeAttributes>
        validDisplayTypes = new Hashtable<>();

    static {
        validDisplayTypes.put(DEFAULT_DIMENSION_TYPE, 
            new DisplayTypeAttributes("Default Dimension"));
        validDisplayTypes.put(BAR_CHART_TYPE, 
                               new DisplayTypeAttributes("Bar Chart"));
        validDisplayTypes.put(FLUX_IMAGE_TYPE, 
                               new DisplayTypeAttributes("Flux Image"));
        validDisplayTypes.put(FLUX_MOVIE_TYPE, 
                               new DisplayTypeAttributes("Flux Movie"));
        validDisplayTypes.put(FUV_IMAGE_TYPE, 
                               new DisplayTypeAttributes("Fuv Image"));
        validDisplayTypes.put(FUV_MOVIE_TYPE, 
                               new DisplayTypeAttributes("Fuv Movie"));
        validDisplayTypes.put(LABEL_TYPE, 
                               new DisplayTypeAttributes("Label"));
        validDisplayTypes.put(LINE_TYPE, 
                               new DisplayTypeAttributes("Line"));
        validDisplayTypes.put(LIST_TYPE, 
                               new DisplayTypeAttributes("List"));
        validDisplayTypes.put(NO_PLOT_TYPE, 
                       new DisplayTypeAttributes("No Plot"));
        validDisplayTypes.put(TIME_TEXT_TYPE, 
                               new DisplayTypeAttributes("Time Text"));
        validDisplayTypes.put(WIND_MOVIE_TYPE, 
                               new DisplayTypeAttributes("Wind Movie"));
        validDisplayTypes.put(WIND_PLOT_TYPE, 
                               new DisplayTypeAttributes("Wind Plot"));
        validDisplayTypes.put(XY_PLOT_TYPE, 
                               new DisplayTypeAttributes("XY Plot"));
        validDisplayTypes.put(PLASMA_MOVIE_TYPE, 
                             new DisplayTypeAttributes("Plasma Movie"));
        validDisplayTypes.put(PLASMAGRAM_TYPE, 
            new DisplayTypeAttributes("Plasmagram",
                    new String[] {"thumbsize", "symsize", "labl"}));
        validDisplayTypes.put(TIME_SERIES_TYPE, 
//            new DisplayTypeAttributes("Time Series"));
            new DisplayTypeAttributes("Time Series",
                    new String [] {"scatter"}));
        validDisplayTypes.put(SPECTROGRAM_TYPE, 
                              new DisplayTypeAttributes("Spectrogram"));
        validDisplayTypes.put(ORBIT_TYPE, 
            new DisplayTypeAttributes("Orbit"));
// valid parameters = new String[] {"coord"}
        validDisplayTypes.put(TOPSIDE_IONOGRAM_TYPE, 
            new DisplayTypeAttributes("Topside Ionogram",
                    new String[] {"max_panel_time"}));
        validDisplayTypes.put(RADAR_VECTOR_TYPE, 
                             new DisplayTypeAttributes("Radar Vector"));
        validDisplayTypes.put(STACK_PLOT_TYPE, 
            new DisplayTypeAttributes("Stack Plot",
                    new String[] {"nobar"}));

        String[] mapKeywords = {"CENTERPOLE", "Sun_Vctr", "TERMINATOR",
                                "FIXED_IMAGE", "MLT_IMAGE", "SUN"};

        validDisplayTypes.put(MAP_IMAGE_TYPE, 
                              new DisplayTypeAttributes("Map Image", 
                                                        mapKeywords));
        validDisplayTypes.put(IMAGE_TYPE, 
                              new DisplayTypeAttributes("Image",
                                          new String[] {"thumbsize"}));
        validDisplayTypes.put(MAP_MOVIE_TYPE, 
                              new DisplayTypeAttributes("Map Movie",
                                                        mapKeywords));
        validDisplayTypes.put(MOVIE_TYPE, 
                              new DisplayTypeAttributes("Movie"));
        validDisplayTypes.put(MAPPED_TYPE, 
                              new DisplayTypeAttributes("Mapped"));
        validDisplayTypes.put(SKYMAP_TYPE, 
            new DisplayTypeAttributes("Skymap"));
// valid parameters = new String[] {"labl"}
        validDisplayTypes.put(SKYMAP_MOVIE_TYPE, 
            new DisplayTypeAttributes("Skymap Movie"));
// valid parameters = new String[] {"labl"}
     }



    /**
     * Test harness for this class.
     * 
     * @param args optional parameters containing additional strings to
     *            be parsed for conversion specifications.  If none are
     *            provided, then just the built in tests are performed.
     */
    public static void main(String[] args) {


        String[] tests = {
            "time_series", "time_series>y=variable(2)",
            "stack_plot", "stack_plot>y=Helium,z=He_Energy",
            "stack_plot>y=Proton_DIntn_Engy(1),y=Proton_DIntn_Engy(3),y=Proton_DIntn_Engy(5)",
            "spectrogram", "spectrogram>y=Chan_Freq,z=Intensity",
            "spectrogram>y=angle,z=Sigma_He_1VQ(1,*),z=Sigma_He_1VQ(3,*)",
            "spectrogram>y=engery,z=Sigma_H(*,1)",
            "image", 
            "map_image>CENTERPOLE>Sun_Vctr>x=Geo_Lat,y=Geo_Lon,z=image",
            "movie",
            "map_movie>FIXED_IMAGE>TERMINATOR>x=GEOD_LAT,y=GEOD_LON",
            "orbit>coord=gsm",
            "topside_ionogram>max_panel_time>90000",
            "wrong",
            "map_movie>", "map_image>wrong>x=Geo_Lat", "map_image>x=wrong,",
            "topside_ionogram>wrong>90000",
            "skymap>labl=variable,labl=variable"};

        System.out.println("\nStandard tests:");
        for (int i = 0; i < tests.length; i++) {

            doTest(i, tests[i]);
        };

        System.out.println("\nOptional tests:");
        for (int i = 0; i < args.length; i++) {

            doTest(i, args[i]);
        };
        System.out.println();
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

        DisplayType dt = null;

        System.out.print("Test[" + id + "]: '" + s + "' results = ");

        try {

            dt = DisplayType.parseDisplayType(s);

            System.out.println("valid");
            System.out.println("    type = " + dt.getType());
            System.out.println("    keywords:");

            List keywords = dt.getKeywords();

            for (int i = 0; i < keywords.size(); i++) {

                System.out.println("        " + keywords.get(i));
            };

            System.out.println("    parameters:");

            List parameters = dt.getParameters();

            for (int i = 0; i < parameters.size(); i++) {

                AbstractDisplayTypeParameter param = 
                        (AbstractDisplayTypeParameter)parameters.get(i);

                if (param instanceof CoordinateSystem) {

                    CoordinateSystem cs = (CoordinateSystem)param;

                    System.out.println("        Coordinate System = " +
                                       cs);
                }
                else if (param instanceof ParameterValue) {

                    ParameterValue value = (ParameterValue)param;

                    System.out.println("        value = " + value.getValue());
                }
                else if (param instanceof AxisDefinition) {

                    AxisDefinition ad = (AxisDefinition)param;
                    System.out.println("        " + ad.getAxis() + " = " +
                                       ad.getVariable());
                }
                else {

                    System.out.println("        invalid parameter type of '" +
                                       param.getClass().getName() + "'");
                };
            };
        }
        catch (IllegalArgumentException e) {

            System.out.println("syntax error");
            System.out.println("    " + e.getMessage());

            return false;
        };

        return true;
    }

}
