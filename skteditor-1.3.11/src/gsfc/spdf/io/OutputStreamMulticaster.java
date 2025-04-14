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
 * $Id: OutputStreamMulticaster.java,v 1.5 2022/08/02 11:15:48 btharris Exp $
 */

//$Id: OutputStreamMulticaster.java,v 1.5 2022/08/02 11:15:48 btharris Exp $
package gsfc.spdf.io;

import java.io.OutputStream;
import java.io.IOException;

/**
 * This class allows you to send output to multiple locations.
 *
 * For instance, you can use this class to redirect System.err to the
 * standard error and a file in this manner.
 *
 * <PRE>
 *	try {
 *	    FileOutputStream errFile =
 *		new FileOutputStream("SKTEditorError.txt");
 *	    OutputStream osm = 
 *		new OutputStreamMulticaster(System.err);
 *	    ((OutputStreamMulticaster)osm).add(errFile);
 *
 *	    PrintStream ps = new PrintStream(osm);
 *	    System.setErr(ps);
 *	} catch (FileNotFoundException e) {
 *	    System.err.println("Could not setup error logging\n"+
 *			       "Errors will be sent to the screen only.");
 *	}
 * </PRE>
 *
 * @author Phil Williams
 * @version $Revision: 1.5 $
 *
 *  1999, NASA/Goddard Space Flight Center
 *  This software may be used, copied, or redistributed as long as it is not
 *  sold or incorporated in any product meant for profit.  This copyright 
 *  notice must be reproduced on each copy made.  This routine is provided 
 *  as is without any express or implied warranties whatsoever.
 *
 */
public class OutputStreamMulticaster extends OutputStream {
    protected OutputStream [] outputStreamList = null;

    /**
     * Add an output stream to the list
     * 
     * @param a the OutputStream
     */ 
    public void add(OutputStream a) {
	if (outputStreamList == null) {
	    outputStreamList = new OutputStream [] {a};
	} else {
	    int i = outputStreamList.length;
	    OutputStream [] tmp = new OutputStream [i+1];
	    System.arraycopy(outputStreamList, 0, tmp, 0, i);
	    tmp[i] = a;
	    outputStreamList = tmp;
	}
    }

    /**
     * Remove an output stream to the list
     * 
     * @param a the OutputStream
     */ 
    public synchronized void remove(OutputStream a) {
	if (a == null)
	    throw new IllegalArgumentException("OutputStream is null");

	//Is a on the list?
	int index = -1;
	for (int i = outputStreamList.length-2; i>=0; i--) {
	    if (outputStreamList[i]==a) {
		index = i;
		break;
	    }
	}
	
	// If so,  remove it
	if (index != -1) {
	    OutputStream[] tmp = new OutputStream [outputStreamList.length-1];
	    // Copy the list up to index
	    System.arraycopy(outputStreamList, 0, tmp, 0, index);
	    // Copy from two past the index, up to
	    // the end of tmp (which is two elements
	    // shorter than the old list)
	    if (index < tmp.length)
		System.arraycopy(outputStreamList, index+1, tmp, index, 
				 tmp.length - index);
	    // set the listener array to the new array or null
	    outputStreamList = (tmp.length == 0) ? null : tmp;
	}
    }

    public void write(byte b[]) throws IOException {
	for (int i = 0; i < outputStreamList.length; i++)
	    outputStreamList[i].write(b);
    }

    public void write(byte b[], int off, int len) throws IOException {
	for (int i = 0; i < outputStreamList.length; i++)
	    outputStreamList[i].write(b, off, len);
    }

    public void write(int b) throws IOException {
	for (int i = 0; i < outputStreamList.length; i++)
	    outputStreamList[i].write(b);
    }

    public void flush() throws IOException {
	for (int i = 0; i < outputStreamList.length; i++)
	    outputStreamList[i].flush();
    }

    public void close() throws IOException {
	for (int i = 0; i < outputStreamList.length; i++)
	    outputStreamList[i].close();
    }

    public OutputStreamMulticaster(OutputStream a) {
	add(a);
    }

}
