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
 * Copyright (c) 2012-2022 United States Government as represented by 
 * the National Aeronautics and Space Administration. No copyright is 
 * claimed in the United States under Title 17, U.S.Code. All Other 
 * Rights Reserved.
 *
 * $Id: package-info.java,v 1.3 2022/03/24 10:38:39 btharris Exp $
 */

/**
 * Provides <a href="https://spdf.gsfc.nasa.gov/">Space Physics Data 
 * Facility</a> (SPDF) International Solar-Terrestrial Physics (ISTP)
 * specialized <a href="https://spdf.gsfc.nasa.gov/sp_use_of_cdf.html">
 * CDF</a> classes.  The classes assist in creating ISTP compliant
 * CDF files.
 * <P>
 * ISTPCompliance provides the main methods for determining the
 * compliance of CDFs and the variables that they contain.  The
 * subpackage gsfc.spdf.istp.tools provides several classes that 
 * contain a main method (i.e. executable) that demonstrate using the 
 * ISTPCompliance class.
 * </P>
 * <P>
 * ISTPComplianceException is used by ISPTCompliance to signal when a
 * CDF is non compliant or conatains non-compliant variables.
 * </P>
 *
 * @since 1.0
 */
package gsfc.spdf.istp;

