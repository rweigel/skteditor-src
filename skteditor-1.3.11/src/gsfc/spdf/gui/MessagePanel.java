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
 * $Id: MessagePanel.java,v 1.6 2022/03/24 10:38:36 btharris Exp $
 */

// $Id: MessagePanel.java,v 1.6 2022/03/24 10:38:36 btharris Exp $
package gsfc.spdf.gui;

import gsfc.spdf.gui.MessageArea;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import javax.swing.text.Document;

/**
 * A panel containing a MessageArea.
 *
 * <BR><BR>You can redirect System.out and/or System.err in the 
 * following manner:<br>
 *  <pre>
 *     MessagePanel mp = new MessagePanel();
 *     System.setOut(mp.getPrintStream());
 *     System.setErr(mp.getPrintStream());
 *  </pre>
 *
 * @author Phil Williams
 * @version $Revision: 1.6 $
 *
 * @see MessageArea
 */
public class MessagePanel extends javax.swing.JPanel
    implements java.awt.event.ActionListener
{
    private MessageArea _area;
    private JButton     _button;
    private JScrollPane _scroll;

    public MessagePanel(int rows, int columns) 
	throws java.io.IOException
    {
	super(true);
	_area = new MessageArea(rows, columns);
	_scroll = new JScrollPane(_area);
	_scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	_scroll.setMinimumSize(_scroll.getPreferredSize());
	setLayout(new BorderLayout());
	add(_scroll, BorderLayout.CENTER);
	
	_button = new JButton("Clear");
	_button.addActionListener( this );
	add(_button, BorderLayout.SOUTH);

	validate();
    }
    
    public java.io.PrintStream getPrintStream() {
	return _area.getPrintStream();
    }
    
    public void actionPerformed(java.awt.event.ActionEvent evt) {
	// The only action is to clear the messageArea
	_area.clear();
    }

    public void clear() {
	_area.clear();
    }

    public Document getDocument() {
	return _area.getDocument();
    }

    public void setDocument(Document doc) {
	_area.setDocument(doc);
    }

} // MessagePanel
