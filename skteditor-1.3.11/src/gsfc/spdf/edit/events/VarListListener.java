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
 * $Id: VarListListener.java,v 1.8 2022/03/24 10:38:28 btharris Exp $
 */

// $Id: VarListListener.java,v 1.8 2022/03/24 10:38:28 btharris Exp $

package gsfc.spdf.edit.events;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.border.*;

import java.awt.Cursor;
import java.awt.event.*;
import java.lang.*;

import gsfc.nssdc.cdf.*;

import gsfc.spdf.edit.guis.*;

/**
 * Listen for selections made in the variable list and update the 
 * VariablePanel.
 *
 * @author Phil Williams
 * @version $Revision: 1.8 $
 */
public class VarListListener implements ListSelectionListener {

    private VariablePanel myPanel;

    public VarListListener(VariablePanel myPanel) {
	super();
	this.myPanel = myPanel;
    }

    public void valueChanged(ListSelectionEvent e) {
	JList jl = (JList)e.getSource();
	if (!e.getValueIsAdjusting() && (jl.getSelectedIndex() >= 0)) {
	    jl.removeListSelectionListener(this);
	    SKTEditor.sharedInstance().getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    
	    Variable v = null;
	    boolean result;
	    Variable oldVar = myPanel.getSelectedVar();
	    String varName = 
		jl.getSelectedValue().toString();

	    try {
		myPanel.saveVariableChanges();
		v = myPanel.myEditor.theCDF.getVariable(varName);
		myPanel.updateVarPanel(v);
	    } catch (Exception exc) {
		exc.printStackTrace();
		JOptionPane.showMessageDialog(myPanel.myEditor.sharedInstance(), 
					      exc.getMessage(),
					      "SKTEditor: Error",
					      JOptionPane.ERROR_MESSAGE);
		jl.setSelectedValue(oldVar.toString(), true);
	    } 
	    jl.addListSelectionListener(this);
	}
	SKTEditor.sharedInstance().getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
}
