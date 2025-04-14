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
 * $Id: CoordinateSystem.java,v 1.7 2022/03/24 10:38:39 btharris Exp $
 */
package gsfc.spdf.istp;


/**
 * This class represents coordinate system DISPLAY_TYPE parameter value.  Its 
 * implementation follows the Java idiom for a type-safe enum.
 *
 * @author B. Harris
 * @version $Revision: 1.7 $
 */
public class CoordinateSystem
    extends AbstractDisplayTypeParameter {
    
    /**
     * Provides a String value for this enum value.
     *
     * @return a String value for this enum
     */
    public String toString() {
        
        return name;
    }
    
    /**
     * Parses the given string for a coordinate system name and returns 
     * the corresponding enum value.
     *
     * @param name abbreviation name of a coordinate system
     * @return a CoordinateSystem value corresponding to the given 
     *     coordinate system name
     * @throws IllegalArgumentException thrown if the given name is not 
     *             a recognized name of a coordinate system
     */
    public static CoordinateSystem parseCoordinateSystem(String name)
        throws IllegalArgumentException {
        
        if (name.equals(GCI.toString())) {
            
            return GCI;
        }
        else if (name.equals(GSE.toString())) {
            
            return GSE;
        }
        else if (name.equals(GSM.toString())) {
            
            return GSM;
        }
        else if (name.equals(TOD.toString())) {
            
            return TOD;
        }
        else if (name.equals(J2000.toString())) {
            
            return J2000;
        }
        else if (name.equals(GEO.toString())) {
            
            return GEO;
        }
        else if (name.equals(GM.toString())) {
            
            return GM;
        }
        else if (name.equals(SM.toString())) {
            
            return SM;
        }
        else if (name.equals(HEC.toString())) {
            
            return HEC;
        }
        else {
            
            throw new IllegalArgumentException("invalid coordinate system '" +
            name + "'");
        }
    }
    
    
    /**
     * GC coordinate system.
     */
    public static final CoordinateSystem GCI = new CoordinateSystem("gci");
    
    /**
     * Solar-Ecliptic (SE) or (GSE) coordinate system.
     */
    public static final CoordinateSystem GSE = new CoordinateSystem("gse");
    
    /**
     * Solar Magnetospheric (GSM) coordinate system.<pre>
     *   X GSM axis is coincide wit the direction to the sun
     *   XZ GSM plane contains the dipole axis
     *   Y GSM supplements the right three, toward dusk</pre>
     */
    public static final CoordinateSystem GSM = new CoordinateSystem("gsm");
    
    /**
     * TOD coordinate system.
     */
    public static final CoordinateSystem TOD = new CoordinateSystem("tod");
    
    /**
     * J2000 coordinate system.
     */
    public static final CoordinateSystem J2000 = new CoordinateSystem("j2000");
    
    /**
     * Geographic (GEO) coordinate system.<pre>
     *   Z GEO axis is coincide with the Earth's rotation axis
     *   X GEO axis belong to the Greenwich meridian
     *   Y GEO axis supplements the right three</pre>
     */
    public static final CoordinateSystem GEO = new CoordinateSystem("geo");
    
    /**
     * GM coordinate system.
     */
    public static final CoordinateSystem GM = new CoordinateSystem("gm");
    
    /**
     * Solar Magnetic (SM) coordinate system.
     */
    public static final CoordinateSystem SM = new CoordinateSystem("sm");
    
    /**
     * HEC coordinate system.
     */
    public static final CoordinateSystem HEC = new CoordinateSystem("hec");
    
    
    /**
     * A name for this enum value.
     */
    private final String name;
    
    /**
     * Constructs a CoordinateSystem object for the given value.
     *
     * @param name the abreviation name of the coordinate system
     */
    private CoordinateSystem(String name) {
        
        this.name = name;
    }
}
