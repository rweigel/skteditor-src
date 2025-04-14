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
 * $Id: LimitedPlainDocument.java,v 1.4 2022/03/24 10:38:36 btharris Exp $
 */

// $Id: LimitedPlainDocument.java,v 1.4 2022/03/24 10:38:36 btharris Exp $
package gsfc.spdf.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;

/**
 * The ever present extension to PlainDocument to limit the number of 
 * charaters in a text field.  Checkout the Java tutorial for more info
 */
public class LimitedPlainDocument extends PlainDocument {
    
    LimitedPlainDocument(int columns) {
	super();
	putProperty("columns", new Integer(columns));
    }

    public void insertString(int offs, String str, AttributeSet a)
    throws BadLocationException {
       // check if the new length (length of document +
       // length of string which will be inserted into the document)
       // exceeds the maximum number of characters
       if (str != null && 
	   getLength() + str.length() > ((Integer)getProperty("columns")).intValue()) {
           throw new BadLocationException("", offs);
       }
       else
           super.insertString(offs, str, a);
    }
}
