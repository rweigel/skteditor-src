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
 * $Id: FileSelectedEvent.java,v 1.3 2022/03/24 10:38:36 btharris Exp $
 */
package gsfc.spdf.gui;


import java.util.EventObject;


/**
 * This class represent the event associated with a file menu item being
 * selected.
 *
 * @author B. Harris
 */
public class FileSelectedEvent 
    extends EventObject { 

    /**
     * Name of file selected.
     */
    private String filename = null;


    /**
     * Creates a FileSelectedEvent.
     *
     * @param source the source of this event.
     * @param filename name of file selected.
     */
    public FileSelectedEvent(Object source, String filename) {

        super(source);

        this.filename = filename;
    }


    /**
     * Gets the name of the file that was selected.
     *
     * @return name of the file that was selected.
     * @see #setFilename
     */
    public String getFilename() {

        return filename;
    }


    /**
     * Set the name of the file that was selected.
     *
     * @param value name of the file that was selected.
     * @see #getFilename
     */
    public void setFilename(String value) {

        filename = value;
    }
}
