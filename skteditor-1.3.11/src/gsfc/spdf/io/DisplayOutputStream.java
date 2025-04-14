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
 * $Id: DisplayOutputStream.java,v 1.8 2022/08/02 11:15:48 btharris Exp $
 */

package gsfc.spdf.io;

/**
 * An OutputStream for text areas.
 *
 * Use this to redirect the standard output or standard area to a 
 * text component.  The component may either be AWT component or a 
 * swing component.
 *
 * Example:
 * <PRE>
 *      JTextArea ta = new JTextArea();
 *	OutputStream out = new DisplayOutputStream();
 *	((DisplayOutputStream)out).setDisplayComponent(ta, "append");
 *	PrintStream print = new PrintStream(out);
 *      System.setOut(print);
 * </PRE>
 * 
 * @author Phil Williams, QSS Group Inc
 * @version $Revision: 1.8 $
 *
 *  1999, NASA/Goddard Space Flight Center
 *  This software may be used, copied, or redistributed as long as it is not
 *  sold or incorporated in any product meant for profit.  This copyright 
 *  notice must be reproduced on each copy made.  This routine is provided 
 *  as is without any express or implied warranties whatsoever.
 *
 */
public class DisplayOutputStream extends java.io.OutputStream {
    private java.awt.Component       _target;
    private java.lang.reflect.Method _method;
    private Object                   _args[] = new Object[1];
    private StringBuffer             _buffer;

    private static String lineSep = System.getProperty("line.separator");

    /**
     * Sets the display component for this OutputStream.
     *
     * @param target the component
     * @param methodName the method the <code>target</code> uses 
     *       to display text
     *
     * @return the component (redundant, will be removed in future release)
     * @throws java.io.IOException if it is not possible to write to the given
     *       <code>target</code> using the specified <code>methodName</code>
     */
    public java.awt.Component setDisplayComponent(java.awt.Component target,
						  String methodName)
        throws java.io.IOException {
        if(_target == null) {
            _target = target;
            Class type[] = new Class[1];
            type[0] = "s".getClass();
            try {
                _method = _target.getClass().getMethod(methodName, type);
            } catch(Exception e) {
                throw new java.io.IOException("Cannot write to " + _target +
					      " using " + methodName + "(String s)");
            }
            if(_buffer != null) {
                write(_buffer.toString());
                _buffer = null;
            }
        }
        return target;
    }

    /**
     * OutputStream implementation
     *
     * @see java.io.OutputStream
     */
    public final void write(int b)
        throws java.io.IOException {
	write("" + (char)b);
    }

    /**
     * OutputStream implementation
     *
     * @see java.io.OutputStream
     */
    public final void write(byte b[])
        throws java.io.IOException {
	write(b, 0, b.length);
    }

    /**
     * OutputStream implementation
     *
     * @see java.io.OutputStream
     */
    public final void write(byte b[], int off, int len)
        throws java.io.IOException {
        StringBuffer buffer = new StringBuffer(len);
        for(int i = off; i < len; i++) {
            buffer.append((char)b[i]);
        }
        write(buffer.toString());
    }

    /**
     * OutputStream implementation
     *
     * @param s String to write.
     * @throws java.io.IOException if an I/O exception occurs.
     * @see java.io.OutputStream
     */
    public void write(String s)
        throws java.io.IOException {
        if(_target == null) {
            if(_buffer == null) {
                _buffer = new StringBuffer();
            }
            _buffer.append(s);
        } else {
            if(_method == null) {
                throw new java.io.IOException("Cannot write " + s + " to " 
                                              + _target);
            } else {
                try {

                    // 
                    // Find any platform specific I/O line.separators and
                    //  replace them with the platform independent line
                    //  separator ("\n") for text components.  On MacOS (and
                    //  maybe other non Unix, non MS Windows platforms) the
                    //  line.separator does not contain a "\n" so the text
                    //  won't look right on a text component unless we fix 
                    //  it here.
                    //

                    for(int i = 0, j = s.indexOf(lineSep); i < s.length();) {
    
                        if(j > -1) {	// found an I/O line.separator

                            _args[0] = s.substring(i, j) + "\n";
                            i = j + lineSep.length();
                            j = s.substring(i).indexOf(lineSep);
                        }
                        else {	// no more line.separator's

                            _args[0] = s.substring(i);
                            i = s.length();	
                        };

                        _method.invoke(_target, _args);
                    };

                } catch(Exception e) {
                    throw new java.io.IOException("Cannot write " + s
						  + " to " + _target + 
						  " using " + _method.getName());
                }
            }
        }
    }
}
