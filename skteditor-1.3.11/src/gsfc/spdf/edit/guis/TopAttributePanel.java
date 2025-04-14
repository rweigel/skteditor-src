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
 * $Id: TopAttributePanel.java,v 1.11 2022/03/24 10:38:32 btharris Exp $
 */
package gsfc.spdf.edit.guis;

// Swing Imports
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import javax.swing.table.*;

// Java imports
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.lang.reflect.*;

// CDF Imports
import gsfc.nssdc.cdf.*;
import gsfc.nssdc.cdf.util.*;

// SPDF imports
import gsfc.spdf.gui.*;
import gsfc.spdf.util.*;
import gsfc.spdf.table.*;

// Local Imports
import gsfc.spdf.edit.events.*;
import gsfc.spdf.edit.util.SKTUtils;

/**
 * A panel to display a variable's labels, scale, format and units
 */
public class TopAttributePanel
    extends JPanel
    implements CDFConstants
{
    protected VariablePanel myVP;

    protected AxisInfoPanel axisInfo;    // Labels, scale, format, units
    protected DescriptionPanel description;
    protected ValueUncertaintyPanel uncertainty;

    public TopAttributePanel(VariablePanel myVP) {
	super(true);
	this.myVP = myVP;

    
	axisInfo = new AxisInfoPanel( this.myVP );
	description = new DescriptionPanel( this.myVP );
        uncertainty = new ValueUncertaintyPanel(this.myVP);

	GridBagConstraints gbc;
	GridBagLayout gbl;

	// Set up the main vip panel
	gbl = new GridBagLayout();
	gbc = new GridBagConstraints();
	setLayout(gbl);

 
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = myVP.myEditor.insets2;	
	gbc.anchor = GridBagConstraints.NORTHWEST;
 	gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx=.4;
        gbc.weighty=.4;
    
	//gbc.gridwidth = 1;
	gbl.setConstraints(description, gbc);
	add(description);
        
        gbc.ipady = 5;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbl.setConstraints(uncertainty, gbc);
        add(uncertainty);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx=.4;
        gbc.weighty=.4;
        gbc.weighty=.6;
        gbc.ipadx = 150;
	gbc.gridwidth = GridBagConstraints.REMAINDER;
	gbl.setConstraints(axisInfo, gbc);
	add(axisInfo);
	}
	
    public void reset() {
	axisInfo.reset();
	description.reset();
        uncertainty.reset();
    }

    public void set(Variable var) {
	description.set(var);
      //  uncertainty.set(var);
    }

    public void save(Variable var) {
	axisInfo.save(var);
	description.save(var);
        uncertainty.save(var);
    }
    

    public AxisInfoPanel getAxisInfo() {

        return axisInfo;
    }
    
    public ValueUncertaintyPanel getUncertainty() {

        return uncertainty;
    }  
    
   public DescriptionPanel getDescription() {

        return description;
    }    
}



