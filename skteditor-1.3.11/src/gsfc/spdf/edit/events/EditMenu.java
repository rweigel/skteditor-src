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
 * $Id: EditMenu.java,v 1.7 2022/03/24 10:38:28 btharris Exp $
 */

// $id$
package gsfc.spdf.edit.events;

import javax.swing.*;   
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.JComboBox;

import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import gsfc.spdf.edit.guis.*;
	
	
/**
 * A top-level Edit menu to allow the cut, copy, paste, and select  
 operations.
 * 
 * @author R. Chimiak
 * @version $Revision: 1.7 $
 * @since SKTEditor version 0.2
 */
public class EditMenu extends JMenu  {
	
	/**
	 * Provides the mean to remove the currently selected text 
	 * from a text component and copy it to the system clipboard.
	 */
	private JMenuItem JMenuItemCut       = new JMenuItem("Cut",
                                                             addIcon("Cut16.gif"));

	/**
	 * Provides the mean to copy the currently selected text to the clipboard from a 
	 * text component.  If there is no selection the menu item is grayed out.
	 */
	private JMenuItem JMenuItemCopy      = new JMenuItem("Copy",
                                                             addIcon("Copy16.gif"));

	/**
	 * Provides the mean to copy the current selection from the system clipboard
	 * and inserts it ahead of the current caret position.  If the system clipboard is empty,
	 * the paste menu item is grayed out.
	 */
	private JMenuItem JMenuItemPaste     = new JMenuItem("Paste",
                                                             addIcon("Paste16.gif"));

	/**
	 * Provides a mean to select the text in a text component.
	 */
	private JMenuItem JMenuItemSelectAll = new JMenuItem("SelectAll");
                                                    //  addIcon("SelectAll16.gif"));

	/**
	 * Contains all of the components associated with 
	 * the SKTEditor application
	 */
	private Window  frame;

	/**
	 * Represents the system clipboard. is used in the cut, copy, and paste
	 * operations.
	 */
	  private static final Clipboard CLIPBOARD;
    
    static {
        Clipboard clipboard;
        try {
            clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        } catch (SecurityException e) {
            // Don't have access to the clipboard, create a new one
            clipboard = new Clipboard("Sandboxed Clipboard");
        }
        CLIPBOARD = clipboard;
    }
    
    private static Clipboard getClipboard() {
        return CLIPBOARD;
    }

	
	
	/**
	 * Construct the Edit top-level menu and a popup menu with
	 * the cut, copy, paste, and select all menu items.  The edit menu 
	 * registers as a menu listener and the menu items as action listeners.
	 * @param windowFrame contain all of the components associated with this application.
	 */
	public EditMenu(Window windowFrame)
	{	   
	    frame= windowFrame; 
	    JComboBox box;
	    setText("Edit");
	    setMnemonic(KeyEvent.VK_E); 
	 
		add(JMenuItemCut);
	    JMenuItemCut.setMnemonic(KeyEvent.VK_T);
	    JMenuItemCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
						 ActionEvent.CTRL_MASK));
		JMenuItemCut.setEnabled(true);
		
	    add(JMenuItemCopy);
	    JMenuItemCopy.setMnemonic(KeyEvent.VK_C);
	    JMenuItemCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
						 ActionEvent.CTRL_MASK));
		JMenuItemCopy.setEnabled(true);
       
	    add(JMenuItemPaste);
	    JMenuItemPaste.setMnemonic(KeyEvent.VK_P);
	    JMenuItemPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
						 ActionEvent.CTRL_MASK));
		JMenuItemPaste.setEnabled(true);
        
	    add(new JSeparator());

	    add(JMenuItemSelectAll);
	    JMenuItemSelectAll.setMnemonic(KeyEvent.VK_S);
	    JMenuItemSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
	   					 ActionEvent.CTRL_MASK));
	   					 
	    JMenuItemSelectAll.setEnabled(true);
        
	class SymAction implements java.awt.event.ActionListener
	{
		public void actionPerformed(java.awt.event.ActionEvent event)
		{
	        Object object = event.getSource();
		   Component comp = KeyboardFocusManager.getCurrentKeyboardFocusManager().
                    getPermanentFocusOwner();
	            
		    if(comp instanceof JTextComponent)
		    {   	    
		        if (object == JMenuItemCut){
		            ((JTextComponent)comp).cut();}
		        else if (object == JMenuItemCopy)
		            ((JTextComponent)comp).copy();
		        else if (object == JMenuItemPaste)
		            ((JTextComponent)comp).paste();
		        else if (object == JMenuItemSelectAll)
                    ((JTextComponent)comp).selectAll();
		    }	        
	    }
	};
	class SymMenu implements MenuListener
	{
	    public void menuSelected(MenuEvent event)
	    {
		    Component comp = frame.getFocusOwner();
	
		    if(comp instanceof JTextComponent)
		    {         
		        String theText = ((JTextComponent)comp).getText();
		                
		        if (theText.length() == 0)
		            JMenuItemSelectAll.setEnabled(false);
		        else
		            JMenuItemSelectAll.setEnabled(true);
		              
		        if (((JTextComponent)comp).getSelectedText()!= null)
		        {
		            JMenuItemCut.setEnabled(true);
		            JMenuItemCopy.setEnabled(true);
		        }
		        else
		        {
		             JMenuItemCut.setEnabled(false);
		             JMenuItemCopy.setEnabled(false);
		   		};
	            Transferable contents = getClipboard().getContents(null);

	            if(contents != null && 
                       contents.getTransferDataFlavors() != null &&
                       // ^^^^^^ this check is necessary for Java < 1.2 
                       //   due to JDK bug 4139552 
                       contents.isDataFlavorSupported(
                                      DataFlavor.plainTextFlavor))
	        	                
	 		        JMenuItemPaste.setEnabled(true);
	            else
		            JMenuItemPaste.setEnabled(false);
	 
	        }
	   		else
		    {
		        JMenuItemCut.setEnabled(false);
		        JMenuItemCopy.setEnabled(false);
		   		JMenuItemPaste.setEnabled(false);
		        JMenuItemSelectAll.setEnabled(false);
		    };
		            
        }

	    public void menuDeselected(MenuEvent event)
	    {
		        JMenuItemCut.setEnabled(true);
		        JMenuItemCopy.setEnabled(true);
		   		JMenuItemPaste.setEnabled(true);
		        JMenuItemSelectAll.setEnabled(true);
	    } 
	    public void menuCanceled(MenuEvent event)
	    {
	    } 
    };
    
	    //{{REGISTER_LISTENERS
	    
	    SymMenu lSymMenu = new SymMenu();
	    addMenuListener(lSymMenu);
	   
		SymAction lSymAction = new SymAction();
	    JMenuItemCut.addActionListener(lSymAction);
	    JMenuItemCopy.addActionListener(lSymAction);
	    JMenuItemPaste.addActionListener(lSymAction);
	    JMenuItemSelectAll.addActionListener(lSymAction);
	    
	
    }
		
   
     /**
      * Provides the icon associated with a particular menu item
      * 
      * @param iconName A name reference to a particular icon
      * @return The requested Icon image
      */
     private Icon addIcon(String iconName)
     {
	 String iconPath = SKTEditor.propertyPath +
	    SKTEditor.appProperties.getProperty("icon.path");
	    
        Icon icon=null;
        
        try {
	    icon = new ImageIcon(
                                  EditMenu.class.getResource(
                              iconPath + iconName));
        }
        catch(NullPointerException e) {
            System.err.println("cannot find icon " + iconPath +
                               iconName + " -- continuing without it");
        };
        return icon;
        
     } 
}
