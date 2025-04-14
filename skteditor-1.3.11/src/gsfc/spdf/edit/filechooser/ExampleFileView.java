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
 * $Id: ExampleFileView.java,v 1.12 2024/10/28 10:32:47 btharris Exp $
 */


/*
 * @(#)ExampleFileView.java	1.6 98/06/29
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package gsfc.spdf.edit.filechooser;

import javax.swing.*;
import javax.swing.filechooser.*;

import java.io.File;
import java.util.Hashtable;

/**
 * A convenience implementation of the FileView interface that
 * manages name, icon, traversable, and file type information.
 *
 * This this implemention will work well with file systems that use
 * "dot" extensions to indicate file type. For example: "picture.gif"
 * as a gif image.
 *
 * If the java.io.File ever contains some of this information, such as
 * file type, icon, and hidden file inforation, this implementation may
 * become obsolete. At minimum, it should be rewritten at that time to
 * use any new type information provided by java.io.File
 *
 * Example:
 *    JFileChooser chooser = new JFileChooser();
 *    fileView = new ExampleFileView();
 *    fileView.putIcon("jpg", new ImageIcon("images/jpgIcon.jpg"));
 *    fileView.putIcon("gif", new ImageIcon("images/gifIcon.gif"));
 *    chooser.setFileView(fileView);
 *
 * @version 1.6 06/29/98
 * @author Jeff Dinkins
 */
public class ExampleFileView extends FileView {
    private Hashtable<String, Icon> icons = new Hashtable<>(5);
    private Hashtable fileDescriptions = new Hashtable(5);
    private Hashtable<String, String> typeDescriptions = new Hashtable<>(5);

    /**
     * The name of the file.  Do nothing special here. Let
     * the system file view handle this.
     *
     * @see #getName
     */
    public String getName(File f) {
	return null;

    }

    /**
     * Adds a human readable description of the file.
     *
     * @param f file.
     * @param fileDescription description to add.
     */
    public void putDescription(File f, String fileDescription) {
 	fileDescriptions.put(fileDescription, f);
    }

    /**
     * A human readable description of the file.
     *
     * @see FileView#getDescription
     */
    public String getDescription(File f) {

        /* 10/28/24, B. Harris: This does not seem correct to me.
           f is not the key in the hashtable but the value.  But I'm
           not familar with this code so I am not changing it now. */

	return (String) fileDescriptions.get(f);
    }

    /**
     * Adds a human readable type description for files. Based on "dot"
     * extension strings, e.g: ".gif". Case is ignored.
     *
     * @param extension file extension.
     * @param typeDescription description.
     */
    public void putTypeDescription(String extension, String typeDescription) {
                           
	typeDescriptions.put(typeDescription, extension);
    }

    /**
     * Adds a human readable type description for files of the type of
     * the passed in file. Based on "dot" extension strings, e.g: ".gif".
     * Case is ignored.
     *
     * @param f file.
     * @param typeDescription description to add.
     */
    public void putTypeDescription(File f, String typeDescription) {
	putTypeDescription(getExtension(f), typeDescription);
    }

    /**
     * A human readable description of the type of the file.
     *
     * @param f file.
     * @return file's description.
     * @see FileView#getTypeDescription
     */
    public String getTypeDescription(File f) {
   	return (String) typeDescriptions.get(getExtension(f));
    }

    /**
     * Conveinience method that returnsa the "dot" extension for the
     * given file.
     *
     * @param f file.
     * @return file's extension.
     */
    public String getExtension(File f) {
	String name = f.getName();
	if(name != null) {
	    int extensionIndex = name.lastIndexOf('.');
	    if(extensionIndex < 0) {
		return null;
	    }
	    return name.substring(extensionIndex+1).toLowerCase();
	}
	return null;
    }

    /**
     * Adds an icon based on the file type "dot" extension
     * string, e.g: ".gif". Case is ignored.
     *
     * @param extension file extension.
     * @param icon file icon.
     */
    public void putIcon(String extension, Icon icon) {
	icons.put(extension, icon);
    }

    /**
     * Icon that reperesents this file. Default implementation returns
     * null. You might want to override this to return something more
     * interesting.
     *
     * @param f file.
     * @see FileView#getIcon
     */
    public Icon getIcon(File f) {
	Icon icon = null;
	String extension = getExtension(f);
        if(extension != null) {
	    icon = (Icon) icons.get(extension);
	}
	return icon;
    }

    /**
     * Whether the file is hidden or not. This implementation returns
     * true if the filename starts with a "."
     *
     * @param f file.
     * @return true if file is hidden.  Otherwise false.
     * @see #isHidden
     */
    public Boolean isHidden(File f) {
	String name = f.getName();
	if(name != null && !name.equals("") && name.charAt(0) == '.') {
	    return Boolean.TRUE;
	} else {
	    return Boolean.FALSE;
	}
    }

    /**
     * Whether the directory is traversable or not. Generic implementation
     * returns true for all directories.
     *
     * You might want to subtype ExampleFileView to do somethimg more interesting,
     * such as recognize compound documents directories; in such a case you might
     * return a special icon for the diretory that makes it look like a regular
     * document, and return false for isTraversable to not allow users to
     * descend into the directory.
     *
     * @param f file.
     * @return true if the directory is traversable.  Otherwise false.
     * @see FileView#isTraversable
     */
    public Boolean isTraversable(File f) {
	if(f.isDirectory()) {
             return Boolean.TRUE;
	} else {
	    return Boolean.FALSE;
	}
    }

}
