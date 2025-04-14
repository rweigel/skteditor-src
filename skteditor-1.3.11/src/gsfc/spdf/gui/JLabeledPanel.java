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
 * $Id: JLabeledPanel.java,v 1.6 2022/03/24 10:38:36 btharris Exp $
 */

// $Id: JLabeledPanel.java,v 1.6 2022/03/24 10:38:36 btharris Exp $
package gsfc.spdf.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * A JPanel with a label. 
 *
 * @author  Phil Williams
 * @version $Revision: 1.6 $
 */
public class JLabeledPanel extends JPanel {

    private TitledBorder tb;
    private LayoutManager lm;
    private Border primaryBorder;
    private Border secondaryBorder;
    private String label;
    private String label2;
    private Color enabledColor = Color.black;
    private Color disabledColor = new Color(142, 142, 142);

    /**
     * Construct a JLabeledPanel with the given label.
     *
     * A BoxLayout (Y_AXIS) is set as the default LayoutManager.  The 
     * primary border is an EtchedBorder.
     *
     * @param label The primary label
     */
    public JLabeledPanel(String label) {
	this.label = label;
	setAlignmentX(SwingConstants.WEST);

	lm            = new BoxLayout(this, BoxLayout.Y_AXIS);
	primaryBorder = new EtchedBorder();
	tb            = new TitledBorder(primaryBorder, label);
	tb.setTitleColor(enabledColor);
	setLayout(lm);
	setBorder(tb);
    }

    /**
     * Construct a JLabeledPanel with the given label.
     *
     * The primary border is an EtchedBorder.
     *
     * @param label The primary label
     * @param lm A LayoutManager to apply to the panel.
     */
    public JLabeledPanel(String label, LayoutManager lm) {
	this.label = label;
	this.lm = lm;

	primaryBorder = new EtchedBorder();
	tb            = new TitledBorder(primaryBorder, label);
	tb.setTitleColor(enabledColor);
	setLayout(lm);
	setBorder(tb);
    }

    /**
     * Construct a JLabeledPanel with the given label.
     *
     * A BoxLayout (Y_AXIS) is set as the default LayoutManager.
     *
     * @param label The primary label
     * @param primaryBorder an alternate border to use for the TitledBorder
     */
    public JLabeledPanel(String label, Border primaryBorder) {
	this.label = label;
	this.primaryBorder = primaryBorder;

	lm            = new BoxLayout(this, BoxLayout.Y_AXIS);
	tb            = new TitledBorder(primaryBorder, label);
	tb.setTitleColor(enabledColor);

	setLayout(lm);
	setBorder(tb);
    }
    
    /**
     * Construct a JLabeledPanel with the given label.
     *
     * @param label The primary label
     * @param lm A LayoutManager to apply to the panel.
     * @param primaryBorder an alternate border to use for the TitledBorder
     */
    public JLabeledPanel(String label, 
			 Border primaryBorder, LayoutManager lm) {
	this.label = label;
	this.primaryBorder = primaryBorder;
	this.lm = lm;

	tb            = new TitledBorder(primaryBorder, label);
	tb.setTitleColor(enabledColor);

	setLayout(lm);
	setBorder(tb);
    }

    /**
     * Construct a JLabeledPanel with the given label.
     *
     * A BoxLayout with the specified axis is set as the LayoutManager.  The 
     * primary border is an EtchedBorder.
     *
     * @param label The primary label
     * @param axis BoxLayout.Y_AXIS or BoxLayout.X_AXIS
     */
    public JLabeledPanel(String label, int axis) {
	this.label = label;

	lm            = new BoxLayout(this, axis);
	primaryBorder = new EtchedBorder();
	tb            = new TitledBorder(primaryBorder, label);
	tb.setTitleColor(enabledColor);

	setLayout(lm);
	setBorder(tb);
    }

    /**
     * Construct a JLabeledPanel with the given label.
     *
     * A BoxLayout with the specified axis is set as the LayoutManager.
     *
     * @param label The primary label
     * @param axis BoxLayout.Y_AXIS or BoxLayout.X_AXIS
     * @param primaryBorder an alternate border to use for the TitledBorder
     */
    public JLabeledPanel(String label, int axis, Border primaryBorder) {
	this.label = label;
	this.primaryBorder = primaryBorder;

	lm            = new BoxLayout(this, axis);
	tb            = new TitledBorder(primaryBorder, label);
	tb.setTitleColor(enabledColor);

	setLayout(lm);
	setBorder(tb);
    }

    /**
     * Enables or disables all the components contained in this panel
     *
     * @param enabled the new state of this panel.
     */
    public void setEnabled(boolean enabled) {
	super.setEnabled(enabled);
	int nComponents = getComponentCount();
	for (int i=0; i< nComponents; i++)
	    getComponent(i).setEnabled(enabled);

	if (enabled)
	    tb.setTitleColor(enabledColor);
	else
	    tb.setTitleColor(disabledColor);
    }
	    
    /**
     * Changes the color of the title.
     *
     * @param c the new title color
     */
    public void setTitleColor(Color c) {
	tb.setTitleColor(c);
    }

}

    
