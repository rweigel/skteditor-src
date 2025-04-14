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
 * $Id: MessageDialog.java,v 1.7 2022/08/02 11:41:48 btharris Exp $
 */

package gsfc.spdf.gui;

import gsfc.spdf.gui.MessageArea;
import javax.swing.*;
import java.awt.*;
import javax.swing.text.Document;
import javax.swing.border.*;

/**
 * A Dialog that provides a MessageArea.  
 *
 * <BR><BR>You can redirect
 * System.out and/or System.err in the following manner:<br>
 *  <pre>
 *     MessageDialog mp = new MessageDialog();
 *     System.setOut(mp.getPrintStream());
 *     System.setErr(mp.getPrintStream());
 *  </pre>
 *
 * <BR><BR>Note:  This should extend JDialog but there is a bug with JDialog
 * not firing component events properly.  Since this is usually a non-modal
 * dialog, there is no problem.  However, this does prevent creating a 
 * message dialog as modal.
 *
 * @author Phil Williams
 * @version $Revision: 1.7 $
 *
 * @see MessageArea
 *
 */
public class MessageDialog extends javax.swing.JFrame
    implements java.awt.event.ActionListener
{

    private MessageArea _area;
    private JButton     _clear;
    private JButton     _close;
    private JScrollPane _scroll;

    /**
     * Create a MessageDialog
     *
     * @param frame the parent frame to which the dialog belongs
     * @param modal true if the dialog should be modal.
     * @throws java.io.IOException if an I/O exception occurs.
     */
    public MessageDialog(Frame frame, boolean modal)
	throws java.io.IOException
    {
	super("Messages");
	setTitle("Messages");

	addWindowListener
	    ( new java.awt.event.WindowAdapter() {
		public void windowClosing(java.awt.event.WindowEvent e) {
		    setVisible(false);
		}
	    }
	      );

        setLocationRelativeTo(frame);

	JPanel p = new JPanel();
	p.setLayout(new BorderLayout());
	getContentPane().add(p);

	_area = new MessageArea(30, 40);
	_scroll = new JScrollPane(_area);
	_scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	_scroll.setMinimumSize(_scroll.getPreferredSize());
	p.add(_scroll, BorderLayout.CENTER);
	

	JPanel bp = new JPanel();
	bp.setBorder(new EtchedBorder());
	bp.setLayout(new BoxLayout(bp, BoxLayout.X_AXIS));

	_clear = new JButton("Clear");
	_clear.addActionListener( this );
	bp.add(_clear);

	bp.add(Box.createVerticalStrut(5));

	_close = new JButton("Close");
	_close.addActionListener( this );
	bp.add(_close);
	    
	p.add(bp, BorderLayout.SOUTH);

	setResizable(true);
	setSize(600, 300);
	setVisible(false);
    }
    
    /**
     * Get the PrintStream of the MessageArea
     *
     * @return MessageArea.getPrintStream
     */
    public java.io.PrintStream getPrintStream() {
	return _area.getPrintStream();
    }
    
    /**
     * Get the Document of the MessageArea
     *
     * @return MessageArea.getDocument
     */
    public Document getDocument() {
	return _area.getDocument();
    }

    /**
     * Set the message area's document
     *
     * @param doc the new document model
     */
    public void setDocument(Document doc) {
	_area.setDocument(doc);
    }

    /**
     * Handle clear and close events.
     */
    public void actionPerformed(java.awt.event.ActionEvent evt) {
	// The only action is to clear the messageArea
	Object source = evt.getSource();
	if (source == _clear)
	    _area.clear();
	else if (source == _close) {
	    setVisible(false);
	}
    }

    /**
     * Clear the MessageArea
     */
    public void clear() {
	_area.clear();
    }

} // MessageDialog
