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
 * $Id: MessageArea.java,v 1.9 2022/08/02 11:41:48 btharris Exp $
 */

// $Id: MessageArea.java,v 1.9 2022/08/02 11:41:48 btharris Exp $

package gsfc.spdf.gui;

import gsfc.spdf.io.DisplayOutputStream;
import java.io.PrintStream;
import java.io.OutputStream;

/**
 * A JTextArea that wraps a DisplayOutputStream.  
 *
 * <BR><BR>You can redirect System.out and/or System.err in the following
 * manner:<br>
 *  <pre>
 *     MessageArea ma = new MessageArea();
 *     System.setOut(ma.getPrintStream());
 *     System.setErr(ma.getPrintStream());
 *  </pre>
 *
 * @author Phil Williams
 * @version $Revision: 1.9 $
 *
 * @see java.io.PrintStream
 * @see java.io.OutputStream
 */
public class MessageArea extends javax.swing.JTextArea {
    
    private java.io.OutputStream _out;
    private java.io.PrintStream  _print;

    /**
     * Contruct a MessageArea of the specified size
     *
     * @param rows the height of the message area
     * @param columns the width of the message area
     * @throws java.io.IOException if an I/O exception occurs.
     */
    public MessageArea(int rows, int columns) throws java.io.IOException {
	super(rows, columns);
	this._out = new DisplayOutputStream();
	((DisplayOutputStream)_out).setDisplayComponent(this, "append");
	_print = new PrintStream(_out);
	setEditable(false);
    }
    
    /**
     * Get a PrintStream to use for redirection of the System.err or System.out
     *
     * @return this MessageArea's PrintStream
     */
    public PrintStream getPrintStream() {
	return _print;
    }
    
    /**
     * Blank the message area
     */
    public void clear() {
	setText("");
    }
} // MessageArea
