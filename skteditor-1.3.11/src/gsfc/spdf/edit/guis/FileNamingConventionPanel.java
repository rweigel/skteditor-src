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
 * Copyright (c) 2013-2022 United States Government as represented by 
 * the National Aeronautics and Space Administration. No copyright is 
 * claimed in the United States under Title 17, U.S.Code. All Other 
 * Rights Reserved.
 *
 * $Id: FileNamingConventionPanel.java,v 1.3 2022/03/24 10:38:32 btharris Exp $
 */
package gsfc.spdf.edit.guis;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Vector;

import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
// import javax.swing.JPanel;


import gsfc.spdf.gui.AbstractLabeledInputComponent;
import gsfc.spdf.istp.Filename;
import gsfc.spdf.istp.GlobalAttribute;


/**
 * This class implements a component for display and input of the 
 * File_naming_convention global attribute.
 *
 */
public class FileNamingConventionPanel 
    extends AbstractLabeledInputComponent 
    implements ActionListener {

    /**
     * Action listeners.
     */
    private Vector<ActionListener> actionListeners =
        new Vector<ActionListener> ();

    /**
     * ComboBox for source_datatype_description value.
     */
    private JComboBox datatypeDescriptionCb = null;

    /**
     * ComboBox for date value.
     */
    private JComboBox dateCb = null;

    /**
     * ComboBox for time value.
     */
    private JComboBox timeCb = null;


    /**
     * Creates a component for display and input of the 
     * File_naming_convention global attribute value.
     */
    public FileNamingConventionPanel() {

        super("File Naming Convention", new EmptyBorder(0, 0, 0, 0));

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        datatypeDescriptionCb = 
            new JComboBox(GlobalAttribute.getFileNamingSourceOptions());
        datatypeDescriptionCb.addActionListener(this);
        dateCb = new JComboBox(Filename.getDateOptions());
        dateCb.addActionListener(this);
        timeCb = new JComboBox(Filename.getTimeOptions());
        timeCb.addActionListener(this);

        // for OS X
        datatypeDescriptionCb.setPreferredSize(new Dimension(200, 35));
        dateCb.setPreferredSize(new Dimension(100, 35));
        timeCb.setPreferredSize(new Dimension(100, 35));

        add(datatypeDescriptionCb);
        add(dateCb);
        add(timeCb);

    }


    public void reset() {

        datatypeDescriptionCb.setSelectedIndex(0);
        dateCb.setSelectedIndex(0);
        timeCb.setSelectedIndex(0);
    }


    public int getInputComponentCount() {

        return 1;
    }


    public Object getInputComponentValue(int index) {

        return get();
    }


    public void setInputComponentValue(int index, Object value) {

        set(value);
    }


    public void setInputComponentToolTipText(
        int index, String value) {

        datatypeDescriptionCb.setToolTipText(value);
        dateCb.setToolTipText(value);
        timeCb.setToolTipText(value);
    }


    public Object get() {

	return (String)datatypeDescriptionCb.getSelectedItem() + 
               Filename.getDateFormat(
                   dateCb.getSelectedIndex()).toPattern() + 
               Filename.getTimeFormat(
                   timeCb.getSelectedIndex()).toPattern();
    }


    public void set(Object obj) {

        if (obj == null) {

            return;
        }

        String[] components = obj.toString().split("_");
                                       // file naming convention 
                                       // components
        if (components.length == 4) {

            String datatypeDescription = 
                components[0] + "_" + components[1] + "_" +
                components[2] + "_";

	    datatypeDescriptionCb.setSelectedItem(datatypeDescription);

            String dateTime = components[3];
                                       // date and time component
            String dateOption = findFileNamingDateOption(dateTime);
                                       // date display option
            if (dateOption != null) {

                dateCb.setSelectedItem(dateOption);

                try {

                    String time = 
                        dateTime.substring(dateOption.length());
                                       // time portion of date/time
                    String timeOption = 
                        findFileNamingTimeOption(time);
                                       // time display option
                    if (timeOption == null) {

                        timeCb.setSelectedItem("");
                    }
                    else {

                        timeCb.setSelectedItem(timeOption);
                    }
                }
                catch (IndexOutOfBoundsException e) {

                    timeCb.setSelectedItem("");
                }
            }
	}
    }


    /**
     * Get the date display option corresponding to the given
     * File_naming_convention date/time component.
     *
     * @param value File_naming_convention date/time component value.
     * @return display option corresponding to the given date/time
     *     component value or null if none is found.
     */
    private static String findFileNamingDateOption(String value) {

        SimpleDateFormat[] formats = 
            Filename.getDateFormats();

        for (int i = 0; i < formats.length; i++) {

            if (value.startsWith(formats[i].toPattern())) {

                return Filename.getDateOptions()[i];
            }
        }

        return null;
    }


    /**
     * Get the time display option corresponding to the given
     * File_naming_convention time component.
     *
     * @param value File_naming_convention time component value.
     * @return display option corresponding to the given time
     *     component value or null if none is found.
     */
    private static String findFileNamingTimeOption(String value) {
    
        SimpleDateFormat[] formats = 
            Filename.getTimeFormatsParseOrder();

        for (int i = 0; i < formats.length; i++) {

            if (value.startsWith(formats[i].toPattern())) {

                return formats[i].toPattern();
            }
        }

        return null;
    }


    public void addItem(Object obj) {

        set(obj);
    }


    /**
     * Adds an ActionListener.  The ActionListener will receive an
     * ActionEvent when a selection has been made.
     *
     * @param listener the ActionListener that is to be notified.
     */
    public void addActionListener(ActionListener listener) {

        actionListeners.add(listener);
    }


    /**
     * Removes an ActionListener.
     *
     * @param listener the ActionListener to remove.
     */
    public void removeActionListener(ActionListener listener) {

        actionListeners.removeElement(listener);
    }



    /**
     * Invoked when an action occurs.
     *
     * @param e a semantic event which indicates that a 
     *     component-defined action occurred.
     */
    public void actionPerformed(ActionEvent e) {

        Vector<ActionListener> listeners = 
            (Vector<ActionListener>) actionListeners.clone();
                                       // a copy of the listeners

        for (ActionListener listener : listeners) {

            listener.actionPerformed(e);
        }
    }
}
