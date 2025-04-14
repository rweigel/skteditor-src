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
 * $Id: StatusBar.java,v 1.10 2022/03/24 10:38:36 btharris Exp $
 */

// $Id: StatusBar.java,v 1.10 2022/03/24 10:38:36 btharris Exp $

package gsfc.spdf.gui;

import java.lang.String;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * A compound widget that provides a status line for applications and a 
 * MessageDialog to cache messages sent to the StatusBar.
 *
 * @author Phil Williams
 * @version $Revision: 1.10 $
 *
 * @see MessageDialog
 */
public class StatusBar 
    extends JPanel
    implements ActionListener
{
    
    private JLabel _theStatus;

    /**
     * Key for informational messages
     */
    public static final int INFO    = 0;

    /**
     * Key for warning messages
     */
    public static final int WARNING = 1;

    /**
     * Key for error messages
     */
    public static final int ERROR   = 2;

    private static final Color errorForeground = Color.white;
    private static final Color errorBackground = Color.red;
    private static final Color warnForeground  = Color.black;
    private static final Color warnBackground  = Color.yellow;
    private static final Color infoForeground  = Color.white;
    private static final Color infoBackground  = Color.blue;

    private Timer resetTimer;

    
    protected String defaultMessage;
    
    /**
     * Get the value of defaultMessage.
     *
     * @return Value of defaultMessage.
     */
    public String getDefaultMessage() {return defaultMessage;}
    
    /**
     * Set the value of defaultMessage.
     * @param v  Value to assign to defaultMessage.
     */
    public void setDefaultMessage(String  v) {this.defaultMessage = v;}
    
    
    private int delay;
    
    /**
     * Get the value of delay. 
     *
     * To set the StatusBar back to the default message.
     * @return Value of delay.
     */
    public int getDelay() {return delay;}
    
    /**
     * Set the value of delay.
     * @param v  Value to assign to delay.
     */
    public void setDelay(int  v) {this.delay = v;}
    
    /**
     * Instatiate a new StatusBar with the default delay and message.
     * 
     * The default delay is 10 seconds and the default message is "Ready"
     */
    public StatusBar() {
	this("Ready", 10);
    }
    
    /**
     * Instatiate a new StatusBar with the default message.
     * 
     * The default message is "Ready"
     *
     * @param delay the delay in seconds to return back to the default message.
     */
    public StatusBar(int delay) {
	this("Ready", delay);
    }
    
    /**
     * Instatiate a new StatusBar with the default delay.
     * 
     * The default delay is 10 seconds
     *
     * @param message the default message to set after delay seconds.
     */
    public StatusBar(String message) {
	this(message, 10);
    }

    /**
     * Instatiate a new status bar.
     *
     * @param defaultMessage The default message, typically "Ready"
     * @param delay The number of seconds until the default message reappears
     */
    public StatusBar(String defaultMessage, int delay) {
	this.delay = delay;
	this.defaultMessage = defaultMessage;

	setBorder(new javax.swing.border.EtchedBorder());
	setLayout(new BorderLayout());
	_theStatus = new JLabel(" ");
	_theStatus.setAlignmentX(LEFT_ALIGNMENT);
	_theStatus.setFont(new Font("SansSerif", Font.BOLD, 14));
	add(_theStatus, BorderLayout.CENTER);

	resetTimer = new Timer(delay*1000, this);
	resetTimer.setRepeats(false);
	resetTimer.setCoalesce(true);
    }
    
    /**
     * Listener handler for timer.  Do not override.
     */
    public void actionPerformed(ActionEvent e) {
	setStatus(defaultMessage, INFO, false);
    }
    /**
     * Set the status message.
     *
     * @param message the Status message
     * @param status can be one of StatusBar.ERROR, StatusBar.WARNING or
     *        StatusBar.INFO.  The color of the message depends on 
     *        the status, red for errors, orange for warnings and blue
     *        for information.
     * @param alert if true then ring the bell
     */
    public void setStatus(String message, int status, boolean alert) {
	switch (status) {
	case ERROR:
	    _theStatus.setForeground(errorForeground);
	    setBackground(errorBackground);
	    break;
	case INFO:
	    _theStatus.setForeground(infoForeground);
	    setBackground(infoBackground);
	    break;
	case WARNING:
	    _theStatus.setForeground(warnForeground);
	    setBackground(warnBackground);
	    break;
	default:
	    _theStatus.setForeground(infoForeground);
	    setBackground(infoBackground);
	    break;
	}

	_theStatus.setText(message);
	if (alert)
	    Toolkit.getDefaultToolkit().beep();

	resetTimer.start();
    }
} // StatusBar
