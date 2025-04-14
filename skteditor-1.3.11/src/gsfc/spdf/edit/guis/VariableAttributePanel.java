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
 * $Id: VariableAttributePanel.java,v 1.13 2022/03/24 10:38:32 btharris Exp $
 */
package gsfc.spdf.edit.guis;


import javax.swing.JPanel;
import javax.swing.BoxLayout;

import gsfc.nssdc.cdf.Entry;
import gsfc.nssdc.cdf.Variable;

import java.awt.*;

public class VariableAttributePanel extends JPanel {

    protected VariableSpecificationPanel variableSpecificationPanel =
                                             new VariableSpecificationPanel();
    protected AttributePanel attributePanel = new AttributePanel();


    public VariableAttributePanel()  {

        attributePanel.setMultiLineOptionVisible(false);

        GridBagLayout gridbag = new GridBagLayout();
        setLayout(gridbag);
        GridBagConstraints c = new GridBagConstraints();
       
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 0;
        
        gridbag.setConstraints(variableSpecificationPanel, c);
     
        c.gridy =1;
        c.weighty = 1;
        
        gridbag.setConstraints(attributePanel, c);

        add(variableSpecificationPanel);
    
        add(attributePanel);
    }


    public void setVariable(Variable variable) 
    {
        variableSpecificationPanel.setVariable(variable);
    }


    public void setEntry(String attributeName, Entry entry) 
    {
        attributePanel.setEntry(attributeName, entry);
    }

    public long getAttributeDatatype()
    {
        return attributePanel.getDataType();
    }

    public Object getAttributeValue()
    {
        return attributePanel.getValue();
    }
}
