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
 * $Id: BottomAttributePanel.java,v 1.12 2022/03/24 10:38:32 btharris Exp $
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
public class BottomAttributePanel
    extends JPanel
    implements CDFConstants {

    protected VariablePanel myVP;

    protected PlotInfoPanel plotInfo;    // VAR_TYPE and DISPLAY_TYPE
    protected DependPanel   depends;
    protected ValuePanel values;
    protected VirtualVariablePanel virtualVariable;

    public BottomAttributePanel(VariablePanel myVP) {
	super(true);
	this.myVP = myVP;

    
	plotInfo = new PlotInfoPanel( this.myVP );
	depends  = new DependPanel( this.myVP );
	values   = new ValuePanel( this.myVP );
        virtualVariable = new VirtualVariablePanel();

	// axis and depends need to listen for plotInfo events
	plotInfo.addAttributeChangeListener(myVP.getTopDisplay().getAxisInfo());
	plotInfo.addAttributeChangeListener( values );
	plotInfo.addAttributeChangeListener( depends );
        plotInfo.addAttributeChangeListener( myVP.getTopDisplay().getUncertainty() );
        plotInfo.addAttributeChangeListener( myVP.getTopDisplay().getDescription());

	GridBagConstraints gbc;
	GridBagLayout gbl;

	// Set up the main vip panel
	gbl = new GridBagLayout();
	gbc = new GridBagConstraints();
	setLayout(gbl);

        gbc.insets = myVP.myEditor.insets2;	
	gbc.anchor = GridBagConstraints.NORTHWEST;
 	gbc.fill = GridBagConstraints.BOTH;
 	
        gbc.gridx = 0;
	gbc.gridwidth = 1;
        gbc.weightx=.1;
        gbc.weighty=.7;
        gbc.ipadx=-170;
	gbl.setConstraints(plotInfo, gbc);
	add(plotInfo);
	
        gbc.gridx = 1;
        gbc.ipadx=-70;
	gbl.setConstraints(depends, gbc);
	add(depends);
	
        gbc.gridx = 2;
        gbc.weightx=.7;
        gbl.setConstraints(virtualVariable, gbc);
        add(virtualVariable);
    
        gbc.gridx = 3;
        gbc.gridwidth = 2;
	gbl.setConstraints(values, gbc);
	add(values);
    }
	
    public void reset() {
	plotInfo.reset();
	depends.reset();
	values.reset();
        virtualVariable.setVisible(false);
    }

    public void set(Variable var) {
	// Setting the plotInfo will cause an AttributeChangeEvent to
	// be fired and axisInfo and depends will be set accordingly
	plotInfo.set(var);
        virtualVariable.set(var);
    }

    public void save(Variable var) {
	plotInfo.save(var);
	depends.save(var);
	values.save(var);
        virtualVariable.save(var);
    }
}

