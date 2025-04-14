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
 * $Id: StatusPanel.java,v 1.4 2022/03/24 10:38:32 btharris Exp $
 */
package gsfc.spdf.edit.guis;

import gsfc.spdf.gui.MessageDialog;
import gsfc.spdf.gui.StatusBar;

import java.lang.String;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.border.*;

public class StatusPanel extends JPanel
    implements ActionListener
{
    
    private StatusBar          _theStatus;
    private JButton            _clear;
    private JButton            _message;
    private MessageDialog      _md = null;

    public StatusPanel(Frame frame) {
	setBorder(new javax.swing.border.EtchedBorder());
	setLayout(new BorderLayout());

	try {
	    _md = new MessageDialog(frame, false);

	    _md.addComponentListener
		( new ComponentAdapter() {
		    public void componentHidden(ComponentEvent e) {
			_message.setText("Show Messages");
		    }
		    
		    public void componentShown(ComponentEvent e) {
			_message.setText("Hide Messages");
		    }
		}
		  );

	} catch (java.io.IOException e) {}

	_theStatus = new StatusBar();
	add(_theStatus, BorderLayout.CENTER);
	
	JPanel p = new JPanel();
	p.setBorder(new EtchedBorder());
	p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
	if (_md != null) {
	    _message = new JButton("Show Messages");
	    _message.addActionListener(this);
	    p.add(Box.createVerticalStrut(5));
	    p.add(_message);
	    add(p, BorderLayout.EAST);
	}
    }

    /**
     * Set the status message.
     *
     * @param message the Status message
     * @param status can be one of StatusPanel.ERROR, StatusPanel.WARNING or
     *        StatusPanel.INFO.  The color of the message depends on 
     *        the status, red for errors, orange for warnings and blue
     *        for information.
     * @param alert if true then ring the bell
     */
    public void setStatus(String message, int status, boolean alert) {
	_theStatus.setStatus(message, status, alert);
    }

    public void actionPerformed(ActionEvent event) {
	Object source = event.getSource();

	if (source == _message) {
	    _md.setVisible(!_md.isVisible());
	}
    }

    public void clearMessages() {
	if (_md != null)
	    _md.clear();
    }

    public Document getMessageDocument() {
	return _md.getDocument();
    }

    public void setMessageDocument(Document doc) {
	_md.setDocument(doc);
    }
    
    public java.io.PrintStream getPrintStream() {
	return _md.getPrintStream();
    }

} // StatusPanel
