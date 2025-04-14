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
 * $Id: VariablePanel.java,v 1.81 2022/08/25 13:11:14 btharris Exp $
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
import javax.swing.event.ListDataListener;

// CDF Imports
import gsfc.nssdc.cdf.*;
import gsfc.nssdc.cdf.util.*;

// SPDF imports
import gsfc.spdf.istp.ISTPCompliance;
import gsfc.spdf.istp.ISTPComplianceException;
import gsfc.spdf.gui.*;
import gsfc.spdf.util.*;
import gsfc.spdf.table.*;

// Local Imports
import gsfc.spdf.edit.events.*;
import gsfc.spdf.edit.util.SKTUtils;

/**
 *
 * This class defines all the methods to build and control the variable
 * panel for the SKTEditor.  The setXXX methods will set the various
 * subpanels for the selectedVar. If the selectedVar is simply a string
 * (which implies a new variable) then the widgets are updated from the
 * appropriate info. The getXXX methods will query the widgets in a given
 * subpanel and place the values in the currentAttributes Hashtable.  The 
 * keys for this hashtable correpsond to the keys for the controls Hashtable.
 *
 */
public class VariablePanel 
  extends JPanel
    implements CDFConstants, VariableEventListener
{
    
    // The Frame
    public  SKTEditor myEditor;
    
    //private JTabbedPane infoTabPane;
    private Variable             selectedVar = null;

    // Listen for changes in the list
    private VarListListener    vll;
    
    // These are the controls for the generic attribute editor
    private JPanel genericAttributePanel;

    /**
     * Holds the widgets and other info that reference VariableAttributes
     */
 
    private Hashtable currentAttributes = new Hashtable(); // ISTP Attributes
    private Hashtable currentSpecs      = new Hashtable(); // CDF Specs

    // Subpanels
    protected CDFSpecPanel          cdfspecs;
    protected TopAttributePanel      topDisplay;
    protected BottomAttributePanel   bottomDisplay;
    protected DescriptionPanel      description;
    protected ValuePanel            values;
    
        
private JList listOfVariables = null; 
//private JDnDList listOfVariables = null; 
    //
    // Constructor
    //
    public VariablePanel(SKTEditor myEditor) {

	this.myEditor = myEditor;
        listOfVariables = new JList(new DefaultListModel());
//        listOfVariables = new JDnDList(new DnDListModel(), myEditor);
        JScrollPane variableScrollPane = new JScrollPane(listOfVariables);
	vll = new VarListListener(this);
	setBorder(new CompoundBorder(myEditor.loweredBorder, 
				     myEditor.emptyBorder5));

	// Instatiate the subpanels
	cdfspecs = new CDFSpecPanel( this );
	topDisplay  = new TopAttributePanel( this );
	bottomDisplay = new BottomAttributePanel( this );

/*
        listOfVariables.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION);
        listOfVariables.setSelectionMode(
            ListSelectionModel.SINGLE_INTERVAL_SELECTION);
*/
        listOfVariables.setSelectionMode(
            ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listOfVariables.setDragEnabled(true);
        listOfVariables.setDropMode(DropMode.INSERT);
        listOfVariables.setTransferHandler(
            new VariableListTransferHandler(myEditor, listOfVariables));
    
	listOfVariables.addListSelectionListener( vll );

	JPanel topRightPanel = new JPanel();
	topRightPanel.setLayout(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(Box.createRigidArea(new Dimension(1,65)));
        buttonPanel.add(new JActionButton(myEditor.checkAction));
        topRightPanel.add(buttonPanel, BorderLayout.EAST);
	topRightPanel.add(cdfspecs,BorderLayout.CENTER);
	
	GridBagConstraints gbc;
	GridBagLayout gbl;

	gbl = new GridBagLayout();
	gbc = new GridBagConstraints();
        setLayout(gbl);

	gbc.anchor = GridBagConstraints.NORTHWEST;
	gbc.fill = GridBagConstraints.BOTH;
	
        gbc.gridx =0;
        gbc.gridy =0;
        gbc.gridheight=2;
        gbc.ipadx = -50;
	gbc.weightx = 0.2;
	gbc.weighty = 0.9;
        gbl.setConstraints(variableScrollPane, gbc);
	variableScrollPane.setMinimumSize(new Dimension(100,75));
	
        gbc.gridx =1;
        gbc.gridy = 0;
        gbc.gridheight=1;
        gbc.ipadx = 0;
        gbc.ipady = -50;
	gbc.weightx = 0.9;
	gbc.weighty = 0.1;
        gbl.setConstraints(topRightPanel, gbc);
//	topRightPanel.setMinimumSize(new Dimension(200,75));
        topRightPanel.setMinimumSize(new Dimension(200,90));
	
        gbc.gridy = 1;
        gbc.ipady = 0;
	gbc.weighty = 0.6;
	gbl.setConstraints(topDisplay, gbc);
	topDisplay.setMinimumSize(new Dimension(200,75));
	
        gbc.gridx =0;
        gbc.gridy = 2;
        gbc.gridwidth=2;   
        gbc.ipadx = 0;
        gbc.ipady = -150;
	gbl.setConstraints(bottomDisplay, gbc);
	bottomDisplay.setMinimumSize(new Dimension(200,75));
	
	add(variableScrollPane);
	add(topRightPanel);
	add(topDisplay);
	add(bottomDisplay);
	
	setPreferredSize(new Dimension(myEditor.WIDTH, myEditor.HEIGHT));

	resetPanel(false);
    }

    //---------------------------------------------------------
    //
    //                    Event Handler Methods                   
    //
    //---------------------------------------------------------
    public void performVariableAction(VariableEvent e) {
	int type = e.getID();

	switch (type) {
	case VariableEvent.CREATED:
	    addToListOfVariables(e.getVariable());
            if(e.isDeltaSource()) { 
                
                this.topDisplay.getUncertainty(). varCreated(e.getVariable());
            }                                
	    reselectCurrentVariable();
	    break;
	case VariableEvent.DELETED:
	    break;
	default:
	    break;
	}
    }

    //---------------------------------------------------------
    //
    //                    Controller Methods                   
    //
    //---------------------------------------------------------

    /**
     * Update panel for existing variables
     *
     * @param var CDF variable.
     */
    public  void updateVarPanel(Variable var) {
                  
// String sVar = (selectedVar == null ? "null" : selectedVar.toString());
// System.err.println("====== VariablePanel:  oldVar = "+sVar+
// " newVar = "+var.toString()+" ======");
	selectedVar = var;

 	resetPanel(false);

	cdfspecs.set(selectedVar);  // Set the widgets to the current specs
	topDisplay.set(selectedVar);   // Set the display attributes
	bottomDisplay.set(selectedVar);   // Set the display attributes
                        
        Attribute attr;
	Entry     entry;
	CDF       cdf =	selectedVar.getMyCDF();
        
        gsfc.spdf.istp.Variable selectedVariable = new gsfc.spdf.istp.Variable(selectedVar);
	
        
       try {
                      
//	    String vartype = ((String)selectedVar.getEntryData("VAR_TYPE")).
//		toLowerCase();
	    String vartype = VarType.get(selectedVar);
            	    
       //if (!vartype.equals("metadata")) {
            try {
                
                attr = cdf.getAttribute("FILLVAL");
		try {

                    entry = attr.getEntry(selectedVar.getID());
                    if ( !(ISTPCompliance.dataTypeEqual(selectedVar.getDataType(), entry.getDataType())))
                        
			selectedVariable.setDefaultFillval(); 
		    } catch (CDFException e) {
			
                        selectedVariable.setDefaultFillval(); 
		    }
		} catch (CDFException e) {
		   
                    try {
			
                        Attribute.create(cdf, "FILLVAL", VARIABLE_SCOPE);
			selectedVariable.setDefaultFillval(); 
		    
                    } catch (CDFException e2) {
			
                        myEditor.setStatus("Error creating FILLVAL "+
					   "attribute",
					   StatusBar.ERROR, true, true);
			e2.printStackTrace();
		    }
		}
	  //  }
	} catch (CDFException e0) {
	    System.err.println("VariablePanel.updateVarPanel:"+
			       "\tVAR_TYPE error");
	    e0.printStackTrace();
	}
    }


    /**
     * provides a software wrapper to select a variable.  These changes are not
     * listened for.
     */
    public void reselectCurrentVariable() {
	
       listOfVariables.setSelectedValue(selectedVar.toString(), true);
    }
	
    public  void resetPanel(boolean resetList) {
        
	cdfspecs.reset();
	topDisplay.reset();
	bottomDisplay.reset();
        //was added due to a problem using Windows and JDK 1.3
	requestFocus();
        
	if (resetList) {
	    updateListOfVariables();

	}
    }
    
 

    // Update the Variable List to include all the variables in the ISTP file
    private  void updateListOfVariables() {
        
//        DnDListModel model = (DnDListModel)listOfVariables.getModel();
        DefaultListModel<String> model = (DefaultListModel)listOfVariables.getModel();
         
/*
        while(!model.isEmpty()) {
            
            model.remove((String)model.getElementAt(0));
        }
*/
        model.removeAllElements();
        
        
	if (myEditor.theCDF != null){
	    
            for (Enumeration e = myEditor.theCDF.getVariables().elements() ; 
		 e.hasMoreElements() ; )
                        
//                model.add(e.nextElement().toString());
                model.addElement(e.nextElement().toString());
        
        }
        selectedVar = null;
        listOfVariables.clearSelection();

    }
    
    public Vector getVariableList() {
        
//        return new Vector(((DnDListModel)listOfVariables.getModel()).items);
        DefaultListModel model = 
            (DefaultListModel)listOfVariables.getModel();
        Vector<String> vars = new Vector();
        for (Enumeration<String> names = model.elements(); 
             names.hasMoreElements();) {

            vars.addElement(names.nextElement());
        }
        return vars;
    } 

    // Add the variable to the list
    public void addToListOfVariables(Object var) {

//        ((DnDListModel)listOfVariables.getModel()).add(var.toString());
        ((DefaultListModel)listOfVariables.getModel()).addElement(var.toString());

    }

    //-----------------------------------------------------------
    //
    //                        Utility Methods
    //
    //-----------------------------------------------------------

    /**
     * Saves the current variable to the CDF in memory 
     * Called from VarListListener and VarMenuListener.
     */
    public void saveVariableChanges() {

	// This is brute force until I can determine if there is 
	// a way to get only what has changed

	if (selectedVar != null) {             // is something selected?
	    topDisplay.save(selectedVar);
	    bottomDisplay.save(selectedVar);
	    checkCompliance();
 	}
    }

    public void checkCompliance() {

        if(selectedVar == null) return;
        
        try {
            
//            String vartype = (String)selectedVar.getEntryData("VAR_TYPE");
            String vartype = VarType.get(selectedVar);

            if(vartype.equalsIgnoreCase("ignore_data"))
                
                return;
            
        } catch (CDFException e) {
            
            // not an ignore_data type.  Will be handled 
            // during the checkVariable call following this.
	}

	// Check the variable for compliance and report any errors 
        // to user
	Vector msgs = ISTPCompliance.checkVariable(
                          selectedVar, 
                          myEditor.getSuppressedWarnings());
	StringBuffer info = new StringBuffer();
	info.append(selectedVar + " is ");
	if (msgs.size() > 0) {
	    info.append("not ISTP-Compliant.");
	    myEditor.setStatus(info.toString(),
			       StatusBar.WARNING, true, true);
	    for (int i = 0; i<msgs.size(); i++)
		System.out.println("\t"+msgs.elementAt(i));
	} else {
	    info.append(" ISTP-Compliant.");
	    myEditor.setStatus(info.toString(),
			       StatusBar.INFO, true, false);
	}
     
    }
	
    /**
     * Check to see if support data or metadata is used in other
     * variable attributes and change the name there as well.
     *
     * @param newName new name of selected variable.
     * @throws CDFException if a CDF exception occurs.
     * @throws ISTPComplianceException if an ISTP compliance exception 
     *     occurs.
     */
    public void renameSelectedVar(String newName)
	throws CDFException, ISTPComplianceException
    {
	
        try {
	    
            String oldName = selectedVar.getName();
                                       // original name of selected variable
            gsfc.spdf.istp.Variable istpSelectedVar =
                new gsfc.spdf.istp.Variable(selectedVar);
                                       // ISTP version of selecte variable
            istpSelectedVar.rename(newName);
         //   myEditor.variablePanel.updateVarPanel(istpSelectedVar.getCdfVariable());

/*
            int idx =((DnDListModel)listOfVariables.getModel()).indexOf(oldName);

            ((DnDListModel)listOfVariables.getModel()).remove(oldName);

             ((DnDListModel)listOfVariables.getModel()).add(idx,selectedVar.toString());
*/
            DefaultListModel model = 
                (DefaultListModel)listOfVariables.getModel();

            int idx = model.indexOf(oldName);
            model.remove(idx);
            model.add(idx,selectedVar.toString());

            listOfVariables.setSelectedValue(newName, true);

	} catch (CDFException e) {
	    System.err.println("VariablePanel.renameSelectedVar: "+e);
	}
    }

    /**
     * Check to see if support data or metadata is used in other
     * variable attributes and do not allow deletion of variable.
     *
     * @param referenceAttributeConfirmer interface to call to confirm
     *     deletion.
     * @throws CDFException if a CDF exception occurs.
     * @throws ISTPComplianceException if an ISTP compliance exception 
     *     occurs.
     */
    public void deleteSelectedVar(
        gsfc.spdf.istp.Variable.DeleteReferenceAttributeConfirmer
        referenceAttributeConfirmer)
	throws CDFException, ISTPComplianceException
    {

        try {

            String name = selectedVar.getName();
                                       // name of selected variable
            gsfc.spdf.istp.Variable istpSelectedVar = 
                new gsfc.spdf.istp.Variable(selectedVar);
                                       // ISTP version of selected variable
                        
            istpSelectedVar.delete(referenceAttributeConfirmer);  
          
                      	    
//           ((DnDListModel)listOfVariables.getModel()).remove(name);
           ((DefaultListModel)listOfVariables.getModel()).removeElement(name);

            listOfVariables.setSelectedIndex(-1);
            listOfVariables.clearSelection();
	    resetPanel(false);
 
	    selectedVar = null;
	} catch (CDFException e) {
	    System.err.println("VariablePanel.deleteSelectedVar: "+e);
	}
    }

    public Variable getSelectedVar() {
	return selectedVar;
    }

 

    public Object getAttribute(String name) {
	return currentAttributes.get(name);
    }

    private void putAttribute(String name, Object value) {
	currentAttributes.put(name, value);
    }

    public TopAttributePanel getTopDisplay() {
	return topDisplay;
    }
    public BottomAttributePanel getBottomDisplay() {
	return bottomDisplay;
    }

}
