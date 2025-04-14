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
 * $Id: RecentFileMenu.java,v 1.4 2024/10/25 18:37:55 btharris Exp $
 */
package gsfc.spdf.gui;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.Vector;
import java.util.prefs.BackingStoreException;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import gsfc.spdf.util.History;


/**
 * This class provides a menu whose items are the names of recently 
 * accessed files.
 *
 * @author B. Harris
 */
public class RecentFileMenu 
    extends JMenu 
    implements MenuListener, ActionListener {

    /**
     * History of recently accessed files.
     */
    private History fileHistory = null;


    /**
     * The listeners that are to be notified when a file is selected.
     */
    private Vector<FileSelectedListener> fileSelectedListeners =
        new Vector<>();


    /**
     * Creates a JMenu of recently access files.
     *
     * @param s the text for the menu label.
     * @param fileHistory history of recently accessed files.
     */
    public RecentFileMenu(String s, History fileHistory) {

        super(s);
        this.fileHistory = fileHistory;
        addMenuListener(this);
    }


    /**
     * Registers the given listener to receive FileSelectedEvent 
     * events.
     * 
     * @param listener the listener whose fileSelected method will be 
     *            envoked when an event occurs.
     */
    public void addFileSelectedListener(
        FileSelectedListener listener) {

        fileSelectedListeners.add(listener);
    }


    /**
     * Unregisters the given listener.
     *
     * @param listener the listener that is to be removed.
     */
    public void removeFileSelectedListener(
        FileSelectedListener listener) {

        fileSelectedListeners.removeElement(listener);
    }


    /**
     * Invoked when menu is canceled.
     *
     * @param e menu event.
     */
    public void menuCanceled(MenuEvent e) {}


    /**
     * Invoked when menu is deselected.
     *
     * @param e menu event.
     */
    public void menuDeselected(MenuEvent e) {}


    /**
     * Invoked when menu is selected.  Populates the menu with recently
     * accessed filename menu items.
     *
     * @param e menu event.
     */
    public void menuSelected(MenuEvent e) {

        removeAll();

        try {

            ArrayList/*<String>*/ files = fileHistory.get();
                                       // file history

            if (files.size() == 0) {

                addNoFilesItem();
            }
            else {

                for (int i = 0; i < files.size(); i++) {

                    JMenuItem fileMenuItem = 
                        new JMenuItem((String)files.get(i));
                                       // menu item for this file
                    fileMenuItem.addActionListener(this);
                    add(fileMenuItem);
                }
            }
        }
        catch (BackingStoreException ex) {

            addNoFilesItem();
        }
    }


    /**
     * Invoked when a file menu item is selected.
     *
     * @param e event associated with menu item being selected.
     */
    public void actionPerformed(ActionEvent e) {

        Vector listeners = (Vector)fileSelectedListeners.clone();
                                       // a copy of the listeners

        JMenuItem fileItem = (JMenuItem)e.getSource();
                                       // file item selected
        FileSelectedEvent event = 
            new FileSelectedEvent(fileItem, fileItem.getText());
                                       // event corresponding to 
                                       // selection
        for (int i = 0; i < listeners.size(); i++) {

            FileSelectedListener listener = 
                (FileSelectedListener)listeners.get(i);
                                       // a listener
            listener.fileSelected(event);
        }
    }


    /**
     * Adds an initial, disabled, "No Files" menu item.
     */
    private void addNoFilesItem() {

        JMenuItem noFilesItem = new JMenuItem("No Files");
                                       // "No Files" menu item
        noFilesItem.setEnabled(false);
        add(noFilesItem);
    }
}
