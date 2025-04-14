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
 * $Id: ValueUncertaintyPanel.java,v 1.10 2022/08/02 12:09:53 btharris Exp $
 */
package gsfc.spdf.edit.guis;


import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Vector;

import gsfc.nssdc.cdf.CDFException;

import gsfc.spdf.istp.Variable;

import gsfc.spdf.gui.JLabeledPanel;
import gsfc.spdf.gui.LabeledComboBoxPanel;
import gsfc.spdf.edit.events.*;


/**
 * A panel to display the variable's DELTA_PLUS_VAR and DELTA_MINUS_VAR
 * attribute values.
 */
public class ValueUncertaintyPanel
    extends JLabeledPanel implements  AttributeChangeListener{

    /**
     * Reference to variable panel to get the current list of variables.
     */
    private VariablePanel varPanel;

    /**
     * DELTA_PLUS_VAR ComboBox.
     */
    private DeltaVarComboBox plusVar;

    /**
     * DELTA_MINUS_VAR ComboBox.
     */
    private DeltaVarComboBox minusVar;


    public ValueUncertaintyPanel(VariablePanel varPanel) {

        super("Value Uncertainty", new GridBagLayout());

        this.varPanel = varPanel;

        GridBagLayout gbl = (GridBagLayout)getLayout();
                                       // our layout manager
        GridBagConstraints gbc = new GridBagConstraints();
                                       // layout constraints

        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.1;
        gbc.ipadx = 40;
        gbc.ipady = 15;
        gbc.insets = new Insets(0,2,0,2);

        plusVar = new DeltaVarComboBox("Plus");
        plusVar.addVariableEventListener(varPanel);
        gbl.setConstraints(plusVar, gbc);
        add(plusVar);

        minusVar = new DeltaVarComboBox("Minus");
        minusVar.addVariableEventListener(varPanel);
        gbc.gridx = 1;
        gbl.setConstraints(minusVar, gbc);
        add(minusVar);
        plusVar.setEnabled(false);
        minusVar.setEnabled(false);
    }


    public void reset() {

        plusVar.setEnabled(false);
        plusVar.reset();
        minusVar.setEnabled(false);
        minusVar.reset();
    }


    public void set(gsfc.nssdc.cdf.Variable var) {

        Variable istpVar = new Variable(var);
                                       // ISTP version of var
        
        if(istpVar.getType()[0].equalsIgnoreCase(istpVar.DATA) ||
           istpVar.getType()[0].equalsIgnoreCase(istpVar.SUPPORT_DATA)){ 
            
            plusVar.setVariable(istpVar);
            minusVar.setVariable(istpVar);

            Variable deltaPlusVar = istpVar.getDeltaPlusVar();
                                       // DELTA_PLUS_VAR variable
        //    Vector currentVars = 
        //        (Vector)varPanel.getControls().get("variableList");
                                       // current list of all variables
            
            Vector currentVars = 
                    varPanel.getVariableList();

            plusVar.setVariableList(currentVars);
            minusVar.setVariableList(currentVars);
            
            plusVar.setEnabled(true);
            minusVar.setEnabled(true); 
            
            if (deltaPlusVar != null) {
                              
               plusVar.set(deltaPlusVar.getCdfVariable());
            }
            else {

               plusVar.set(null);
            }

            Variable deltaMinusVar = istpVar.getDeltaMinusVar();
                                       // DELTA_MINUS_VAR variable
            if (deltaMinusVar != null) {

                minusVar.set(deltaMinusVar.getCdfVariable());
            }
            else {

                minusVar.set(null);
            }            
        }
        else {
            
            plusVar.setEnabled(false);
            minusVar.setEnabled(false);            
        }
    }

    public void save(gsfc.nssdc.cdf.Variable var) {
        

        Variable istpVar = new Variable(var);
                                       // ISTP version of var
        
        Object plusVarSelection = plusVar.get();
                                       // plusVar ComboBox selection

        if (plusVarSelection != null) {
            
            try {

                istpVar.setDeltaPlusVar(
                    (gsfc.nssdc.cdf.Variable)plusVarSelection);
            }
            catch (CDFException e) {

                System.err.println(
                    "ValueUncertaintyPanel.save: CDFException while " +
                    "saving DELTA_PLUS_VAR: " + e.getMessage());
            }
        }

        Object minusVarSelection = minusVar.get();
                                       // minusVar ComboBox selection

        if (minusVarSelection != null) {

            try {

                istpVar.setDeltaMinusVar(
                    (gsfc.nssdc.cdf.Variable)minusVarSelection);
            }
            catch (CDFException e) {

                System.err.println(
                    "ValueUncertaintyPanel.save: CDFException while " +
                    "saving DELTA_MINUS_VAR: " + e.getMessage());
            }
        }
    }
 
    /**
     * Since Delta_plus and Delta_minus are usually references to the 
     * same variable, creating a new variable in one will automatically
     * add it to both list and set it in both combo-box.
     *
     * @param var CDF variable.
     */
    public void varCreated(Object var){
        
      if (this.plusVar.containsVar((gsfc.nssdc.cdf.Variable) var)) {
          
          this.minusVar.addItem(var);
          this.minusVar.set(var);
      }
      
     else  {
         
          this.plusVar.addItem(var);
          this.plusVar.set(var);
     }
      
  } 
    

    /**
     * Invoked when an AttributeChangeEvent occurs.
     * 
     * @param event attribute change event
     */
    public void attributeChanged(AttributeChangeEvent event) {
       gsfc.nssdc.cdf.Variable var = event.getVariable();
                                       // the variable whose attribute 
                                       //  has changed
        int type = event.getID();      // the type of attribute change 
                                       //  that has occurred

        // Save the changes before doing anything else.
        
        save(var);
       

        // reset the panel
         //  reset();

        if (type == AttributeChangeEvent.VAR_TYPE_CHANGE) {

	    System.err.println("DependPanel: Got VAR_TYPE_CHANGE.");

            handleVarTypeChanged(var);
            


        }

    }	    


    /**
     * Handles an event associated with a VAR_TYPE change.
     *
     * @param var the variable whose type has changed
     */
    private void handleVarTypeChanged(gsfc.nssdc.cdf.Variable var) {

        String varType = null;         // VAR_TYPE value
        try{

            varType = VarType.get(var);
	}
        catch (CDFException e) {

            // Should never happen
            System.err.println(
                "ValueUncertaintyPanel could not find a variable type");
            return;
        }
    
         if (varType.equals("data")||varType.equals("support_data") ) {

            handleDataVarTypeChanged(var);
        }
         else {
            
            this.minusVar.setEnabled(false);
            this.plusVar.setEnabled(false);
         }             
    }


    /**
     * Handles an event associated with a VAR_TYPE change to "data".
     *
     * @param var the variable whose type has changed
     */
    private void handleDataVarTypeChanged(gsfc.nssdc.cdf.Variable var) {

        // enable depend comboboxes depending upon dimension
        //
        set(var);
    }
 

}


