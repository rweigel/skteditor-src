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
 * $Id: JActionButton.java,v 1.6 2022/03/24 10:38:36 btharris Exp $
 */

package gsfc.spdf.gui;

import java.beans.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
/**
 * An Action aware button.
 *
 * The button will configure itself to the Action set using
 * the setAction method.
 *
 * The class may only be configured for one Action at
 * a time. If a second Action is set using the setAction
 * method, the first Action will be removed. However, other
 * ActionListeners may be added as normal, using the
 * addActionListener method.
 *
 * Adapted from JFC Unleashed by Mike Foley
 **/
public class JActionButton extends JButton 
    implements PropertyChangeListener {

    /**
     * The Action this button is configured for.
     **/
    private Action action;
    
    /**
     * Bound property names.
     **/
    public static final String ACTION_PROPERTY = "action";
    
    public static final int ICON_ONLY = 1;

    public static final int TEXT_ONLY = 2;

    public static final int TEXT_AND_ICON = 3;

    /**
     * AButton, default constructor.
     * Creates a button with no set text or icon. 
     **/
    public JActionButton() {
        super();
    }
    
    /**
     * AButton, constructor
     * Creates a button from the given Action. 
     *
     * @see #setAction
     * @param action The Action to configure this button to.
     **/
    public JActionButton( Action action ) {
        this( action, TEXT_AND_ICON );
    }
    
    /**
     * AButton, constructor
     * Creates a button from the given Action. 
     *
     * @see #setAction
     * @param action The Action to configure this button to.
     * @param displayFlag indicates whether icon, text or both 
     *     should be displayed
     **/
    public JActionButton( Action action, int displayFlag ) {
	super();
	setAction( action, displayFlag );
    }

    /**
     * Configure the button for the given Action.
     * If the button was already configured for
     * an Action, remove that Action from being
     * a listener to this button.
     * The Action is a bound property.
     *
     * @param action The Action to configure this button to.
     * @param displayFlag indicates whether icon, text or both 
     *     should be displayed
     **/
    public void setAction( Action action, int displayFlag ) {
        
        Action oldAction = this.action;
        if( oldAction != null ) {
            //
            // Remove the bind between the button and the
            // old action.
            //
            oldAction.removePropertyChangeListener( this );
            removeActionListener( oldAction );
        }
        
        this.action = action;
        
        if( action != null ) {
            
            //
            // Update our appearance to that of the new action.
            //
	    setAppearance(displayFlag);
        
            setEnabled( action.isEnabled() );
        
            //
            // Bind ourself to the new Action.
            //
            action.addPropertyChangeListener( this );
            addActionListener( action );
    
  
        } else {
            
            //
            // null Action, set the button's view to empty
            setText( "" );
            setIcon( null );
        } // else
        
        //
        // The Action is a bound property.
        //
        firePropertyChange( ACTION_PROPERTY, oldAction, this.action );
        
    } // setAction
    

    /**
     * propertyChange, from PropertyChangeListener.
     * Only handle changes from the Action we are configured for.
     *
     * @param event The property change event causing this method call.
     **/
    public void propertyChange( PropertyChangeEvent event ) {
        if( event.getSource() == action ) {
            
            //
            // Get the name of the changed property, and
            // update ourself accordinly.
            //
            String propertyName = event.getPropertyName();
            
            if( propertyName.equals( Action.NAME ) ) {
                setText( ( String )event.getNewValue() );
            } else if( propertyName.equals(Action.SMALL_ICON)) {
                setIcon( ( Icon )event.getNewValue() );
                invalidate();
            } else if( propertyName.equals( "enabled" ) ) {
                Boolean enabled = ( Boolean )event.getNewValue();
                setEnabled( enabled.booleanValue() );
            } 
            
            //
            // Update our display.
            //
            repaint();
        }
        
    } // propertyChange

    /**
     * Display either the action's icon, text or both.
     *
     * @param displayFlag indicates what to display on the button
     */
    public void setAppearance(int displayFlag) {
	switch ( displayFlag ) {
	case TEXT_ONLY:
            setText( ( String )action.getValue( Action.NAME ) );
            setIcon( null );
	    break;
	case ICON_ONLY:
            setText( "" );
            setIcon( ( Icon )action.getValue( Action.SMALL_ICON ) );
	    break;
	default:
            setText( ( String )action.getValue( Action.NAME ) );
            setIcon( ( Icon )action.getValue( Action.SMALL_ICON ) );
            //
            // Set the text below the Icon.
            //
            setHorizontalTextPosition( JButton.CENTER );
            setVerticalTextPosition( JButton.BOTTOM );
           setBorder(new SoftBevelBorder(0));
	    break;
	}
    }
} // Abutton
