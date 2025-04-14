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
 * $Id: CheckFileAction.java,v 1.6 2022/03/24 10:38:28 btharris Exp $
 */


//$Id: CheckFileAction.java,v 1.6 2022/03/24 10:38:28 btharris Exp $
package gsfc.spdf.edit.events;

import javax.swing.*;
import javax.swing.event.*;
import java.io.File;

import java.awt.event.*;

import gsfc.spdf.edit.guis.*;
//import gsfc.spdf.gui.StatusBar;

/**
 * Implements the File/Check ISTP compliance action.
 * 
 * @author B. Harris
 * @version $Revision: 1.6 $
 */
public class CheckFileAction extends AbstractFileAction {
    
    /**
     * default name of this action if one isn't specified
     */
    private static final String DEFAULT_NAME = "Check";

    /**
     * default icon name if one isn't specified
     */
    private static final String DEFAULT_ICON_NAME = "Check.gif";

    /**
     * the default icon for this action
     */
    private static Icon DEFAULT_ICON;

    /**
     * Constructs a CheckFileAction for the given editor.  The name and
     * icon used are DEFAULT_NAME and DEFAULT_ICON_NAME.
     * 
     * @param editor the editor this menu item belongs to
     */
    public CheckFileAction(SKTEditor editor) 
    {
        this(editor, DEFAULT_NAME, DEFAULT_ICON);
    }

    /**
     * Construct a CheckFileAction for the given editor with the given
     * name and icon.
     * 
     * @param editor the editor this menu item belongs to
     * @param name the name of this action
     * @param icon the file name of the icon to represent this action
     */
    public CheckFileAction(SKTEditor editor, String name, Icon icon) 
    {
        super(name, icon);
        this.myEditor = editor;
    }


    /**
     * Perform the File/Check ISTP compliance action.
     * 
     * @param event event initiating this action
     */
    public void actionPerformed(ActionEvent event) 
    {
        SKTEditor.setWaitCursor();

        myEditor.checkCompliance();

        SKTEditor.setDefaultCursor();
    }

    static {

        DEFAULT_ICON = getIcon(DEFAULT_ICON_NAME);

        if (DEFAULT_ICON == null) {
            
            System.err.println("cannot find icon " + DEFAULT_ICON_NAME +
                               " -- continuing without it");
        };
    }
    
} // CheckFileAction
