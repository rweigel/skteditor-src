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
 * $Id: ISTPIdentifierException.java,v 1.4 2022/03/24 10:38:39 btharris Exp $
 */
package gsfc.spdf.istp;

/**
 * Thrown when an attempt is made to have an ISTP identifier with a name 
 * which violates an ISTP identifier naming restriction.
 *
 * @author B. Harris
 */
public class ISTPIdentifierException 
    extends ISTPComplianceException {

    /**
     * Name of the identifier causing this exception.
     */
    private String identifierName = null;

    /**
     * Name of the identifier which conflicts with the identifier named
     * identifierName.  null if the exception is not due to another 
     * identifier.
     */
    private String conflictingName = null;


    /**
     * Constructs an ISTPIdentifierException.
     *
     * @param message message describing the exception
     * @param identifierName name of the identifier causing this exception
     */
    public ISTPIdentifierException(String message, String identifierName) {

        this(message, identifierName, null);
    }


    /**
     * Constructs an ISTPIdentifierException.
     *
     * @param message message describing the exception
     * @param identifierName name of the identifier causing this exception
     * @param conflictingName name of the identifier which conflicts with
     *            the identifier named identifierName.  null if the exception
     *            is not due to another identifier.
     */
    public ISTPIdentifierException(String message, String identifierName,
                                   String conflictingName) {

	super(message);

        this.identifierName = identifierName;
        this.conflictingName = conflictingName;
    }


    /**
     * Gets the name of the identifier that caused the exception.
     *
     * @return name of the identifier that caused the exception
     */
    public String getIdentifierName() {

        return identifierName;
    }


    /**
     * Gets the name of the identifier that conflicted with the identifier
     * that caused the exception.
     *
     * @return name of the identifier that conflicted with the identifier
     *     that caused the exception.  null if the exception was not caused
     *     by a conflict with another identifier.
     */
    public String getConflictingName() {

        return conflictingName;
    }
}
