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
 * $Id: AbstractLabeledComponent.java,v 1.10 2022/08/02 12:51:29 btharris Exp $
 */

// $Id: AbstractLabeledComponent.java,v 1.10 2022/08/02 12:51:29 btharris Exp $
package gsfc.spdf.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;

import javax.swing.border.*;

/**
 * Provides the default implementations for all labeled components.
 * 
 * @author Phil Williams
 * @author QSS Group Inc
 * @version $Revision: 1.10 $
 * @see gsfc.spdf.gui.AbstractLabeledInputComponent
 * @deprecated This class isn't general enough to be useful.  Use gsfc.spdf.gui.AbtractLabeledInputComponent
 * instead.
 */
public abstract class AbstractLabeledComponent extends JPanel 
    implements GenericComponentInterface
{

    /**
     * The primary label
     */
    public JLabel label;

    /**
     * The secondary label, if desired.
     */
    public JLabel label2;

    /**
     * Color to use for the label when the component is enabled.
     */
    protected Color enabledColor;

    /**
     * Color to use for the label when the component is disabled.
     */
    protected Color disabledColor;

    /**
     * Should wrap this components set method.
     *
     * @param obj item to add.
     */
    public abstract void   addItem(Object obj);
    
    /**
     * Wraps this components reset method.
     */
    public void removeAllItems() {
	reset();
    }

    /**
     * Changes the enabled status of the component.  Should also take care
     * of changing the label color if desired.
     *
     * @param enabled the enabled status.
     */
    public abstract void setEnabled(boolean enabled);

    /**
     * Specifies the color to use when this component is enabled.
     *
     * @param c the color.
     */
    public void setEnabledColor(Color c) {
	this.enabledColor = c;
    }

    /**
     * What color is being used when this component is enabled.
     *
     * @return the enabledColor
     */
    public Color getEnabledColor() {
	return this.enabledColor;
    }

    /**
     * Specifies the color to use when this component is disabled.
     *
     * @param c the color.
     */
    public void setDisabledColor(Color c) {
	this.disabledColor = c;
    }

    /**
     * What color is being used when this component is disabled.
     *
     * @return the disabledColor
     */
    public Color getDisabledColor() {
	return this.disabledColor;
    }

}
