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
 * $Id: AbstractLabeledInputComponent.java,v 1.9 2022/03/24 10:38:36 btharris Exp $
 */

package gsfc.spdf.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * One or more swing input components with a label. 
 * 
 * @author B. Harris
 * @version $Revision: 1.9 $
 */
public abstract class AbstractLabeledInputComponent 

    extends AbstractLabeledComponent // to become JPanel when 
                                     //  AbstractLabeledComponent is eliminated
    implements GeneralInputComponent {

    /**
     * the TitledBorder for this component
     */
    private TitledBorder titledBorder;

    /**
     * the LayoutManager for this component
     */
    private LayoutManager layoutManager;

    /**
     * the border for this component
     */
    private Border border;

    /**
     * the label for this component
     */
    private String label;

    /**
     * the color to use when this component is enabled
     */
    private Color enabledColor = Color.black;

    /**
     * the color to use when this component is disabled
     */
    private Color disabledColor = new Color(142, 142, 142);

    /**
     * Construct a AbstractLabeledInputComponent with the given label.
     * 
     * A BoxLayout (Y_AXIS) is set as the default LayoutManager.  The 
     * border is an EtchedBorder.
     * 
     * @param label The label field value for this component
     */
    public AbstractLabeledInputComponent(String label) {

        this.label = label;
        setAlignmentX(SwingConstants.WEST);

        layoutManager = new BoxLayout(this, BoxLayout.Y_AXIS);

        border = new EtchedBorder();
        titledBorder  = new TitledBorder(border, label);

        titledBorder.setTitleColor(enabledColor);
        setLayout(layoutManager);
        setBorder(titledBorder);
    }

    /**
     * Construct a AbstractLabeledInputComponent with the given label and layout
     * manager.
     * 
     * The border is an EtchedBorder.
     * 
     * @param label The label field value for this component
     * @param layoutManager A LayoutManager to apply to this component
     */
    public AbstractLabeledInputComponent(String label, 
                                         LayoutManager layoutManager) {

        this(label, new EtchedBorder(), layoutManager);
    }

    /**
     * Construct a AbstractLabeledInputComponent with the given label and border.
     * 
     * A BoxLayout (Y_AXIS) is set as the default LayoutManager.
     * 
     * @param label The label field value of this component
     * @param border The border to use for the TitledBorder
     */
    public AbstractLabeledInputComponent(String label, Border border) {

        this.label = label;
        this.border = border;

        layoutManager = new BoxLayout(this, BoxLayout.Y_AXIS);
        titledBorder = new TitledBorder(border, label);
        titledBorder.setTitleColor(enabledColor);

        setLayout(layoutManager);
        setBorder(titledBorder);
    }
    
    /**
     * Construct a AbstractLabeledInputComponent with the given label, border,
     * and layout manager.
     * 
     * @param label The label field value for this component
     * @param layoutManager A LayoutManager to apply to this component
     * @param border The border to use for the TitledBorder
     */
    public AbstractLabeledInputComponent(String label, Border border, 
                                         LayoutManager layoutManager) {

        this.label = label;
        this.border = border;
        this.layoutManager = layoutManager;

        titledBorder = new TitledBorder(border, label);
        titledBorder.setTitleColor(enabledColor);

        setLayout(layoutManager);
        setBorder(titledBorder);
    }

    /**
     * Construct a AbstractLabeledInputComponent with the given label.
     * 
     * A BoxLayout with the specified axis is set as the LayoutManager.  The 
     * border is an EtchedBorder.
     * 
     * @param label The label field value for this component
     * @param axis BoxLayout.Y_AXIS or BoxLayout.X_AXIS
     */
    public AbstractLabeledInputComponent(String label, int axis) {

        this.label = label;

        layoutManager  = new BoxLayout(this, axis);
        border = new EtchedBorder();
        titledBorder = new TitledBorder(border, label);
        titledBorder.setTitleColor(enabledColor);

        setLayout(layoutManager);
        setBorder(titledBorder);
    }

    /**
     * Construct a AbstractLabeledInputComponent with the given label and border.
     * 
     * A BoxLayout with the specified axis is set as the LayoutManager.
     * 
     * @param label The label field value for this component
     * @param axis BoxLayout.Y_AXIS or BoxLayout.X_AXIS
     * @param border The border to use for the TitledBorder
     */
    public AbstractLabeledInputComponent(String label, int axis, 
                                         Border border) {

        this.label = label;
        this.border = border;

        layoutManager = new BoxLayout(this, axis);
        titledBorder  = new TitledBorder(border, label);
        titledBorder.setTitleColor(enabledColor);

        setLayout(layoutManager);
        setBorder(titledBorder);
    }

    /**
     * Enables or disables all the components contained in this panel
     * 
     * @param enabled The new state of this panel.
     */
    public void setEnabled(boolean enabled) {

        int nComponents = getComponentCount();

        for (int i=0; i< nComponents; i++) {

            getComponent(i).setEnabled(enabled);
        };

        if (enabled) {

            titledBorder.setTitleColor(enabledColor);
        }
        else {

            titledBorder.setTitleColor(disabledColor);
        };

        repaint();
    }
	    
    /**
     * Changes the color of the title.
     * 
     * @param c The new title color
     */
    public void setTitleColor(Color c) {

        titledBorder.setTitleColor(c);
    }

    public abstract int getInputComponentCount();

    public abstract Object getInputComponentValue(int index);
    
    public abstract void setInputComponentValue(
        int index, Object value);

    public abstract void setInputComponentToolTipText(
        int index, String value);
}

    
