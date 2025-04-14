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
 * $Id: GlobalPanel.java,v 1.65 2022/08/02 12:20:13 btharris Exp $
 */
package gsfc.spdf.edit.guis;

import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.Box;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.help.CSH;

import gsfc.nssdc.cdf.Attribute;
import gsfc.nssdc.cdf.CDF;
import gsfc.nssdc.cdf.CDFConstants;
import gsfc.nssdc.cdf.CDFException;
import gsfc.nssdc.cdf.Entry;

import gsfc.spdf.gui.GeneralInputComponent;
import gsfc.spdf.gui.JLabeledPanel;
import gsfc.spdf.gui.LabeledTextAreaPanel;
import gsfc.spdf.gui.LabeledComboBoxPanel;
import gsfc.spdf.gui.LabeledTextFieldPanel;
import gsfc.spdf.gui.AbstractLabeledInputComponent;
import gsfc.spdf.gui.StatusBar;
import gsfc.spdf.istp.ISTPCompliance;
import gsfc.spdf.cdf.Value;


/**
 * Implements the Global Attributes panel of the SKTEditor.
 * 
 * @author B. Harris
 * @version $Revision: 1.65 $
 */
public class GlobalPanel extends JPanel 
    implements ItemListener, CDFConstants {
    
    /**
     * The editor this panel belongs to.
     */
    public SKTEditor editor;
    
    /**
     * The GUI components representing the Recommended Global attributes.
     */
    private Hashtable recommendedControls;

    /**
     * The GUI components representing the Required Global attributes.
     */
    private Hashtable requiredControls;
    


    /**
     * Constructs the Global Attributes panel.
     * 
     * @param editor The editor this panel belongs to
     */
    public GlobalPanel(SKTEditor editor) 
    {
        super(true);
        this.editor = editor;
        int reqLong, recLong;
        JLabel label;
        JTextField field;

        requiredControls    = new Hashtable();
        recommendedControls = new Hashtable();

        setBorder(new CompoundBorder(editor.loweredBorder, 
				     editor.emptyBorder2));
        setLayout(new GridLayout());
        add(reqPanel());
        add(recPanel());

        resetPanel();
    }


    /**
     * Constructs the Required Global Attributes panel.
     * 
     * @return the required global attributes panel
     */
    private JPanel reqPanel() 
    {
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel p = new JLabeledPanel("Required", gbl);

        LabeledTextAreaPanel ltap;      // used to refer to each of the
                                        //  LabeledTextAreaPanels created
                                        //  during their construction and
                                        //  initialization
        LabeledComboBoxPanel lcbp;      // used to refer to each of the
                                        //  LabeledComboBoxPanels created
                                        //  during their construction and
                                        //  initialization
        LabeledTextFieldPanel ltfp;     // used to refer to each of the
                                        //  LabeledTextFieldPanels created
                                        //  during their construction and
                                        //  initialization
        EmptyBorder emptyBorder = new EmptyBorder(0, 0, 0, 0);
                                        // an empty border to replace the
                                        //  default etched border on
                                        //  LabeledComboBoxPanels and
                                        //  LabeledTextFieldPanels
        int size = 24;
        int y = 0;
        gbc.weighty = 0.1;
        gbc.gridx = 0;
        gbc.ipady = 5;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0,5,0,5);

        //
        // Project
        //
        lcbp = new LabeledComboBoxPanel("Project", emptyBorder,
                            editor.appProperties.getPropertyVector(
                                                        "gAttr.projects"),
                            true);

        CSH.setHelpIDString(lcbp, "gAttrProject");

        gbc.gridy = y++;
        gbl.setConstraints(lcbp, gbc);
        p.add(lcbp);
        requiredControls.put("Project", lcbp);
        //
        // Source Name
        //
        lcbp = new LabeledComboBoxPanel("Source / Spacecraft Name", emptyBorder,
                            editor.appProperties.getPropertyVector(
                                                        "gAttr.source_names"),
                            true);
        CSH.setHelpIDString(lcbp, "gAttrSourceName");
        lcbp.addItemListener(this);
        lcbp.addItemListener( new ItemListener() {
      
        public void itemStateChanged(ItemEvent event) {
            
            if (event.getStateChange() == ItemEvent.SELECTED) {
                
                new LogicalSourceEltsSyntaxCheck(event, "Source",
                        "RBSP-A>Radiation Belt Storm Probe A");
                  
            }; // endif ItemEvent.SELECTED
        }} );

        gbc.gridy = y++;
        gbl.setConstraints(lcbp, gbc);
        p.add(lcbp);
        requiredControls.put("Source_name", lcbp);

        //
        // Descriptor
        //
        lcbp = new LabeledComboBoxPanel("Descriptor / Instrument Name", emptyBorder,            
	                    editor.appProperties.getPropertyVector(
                                                        "gAttr.descriptors"),
                            true);
        CSH.setHelpIDString(lcbp, "gAttrDescriptors");
        lcbp.addItemListener(this);
        lcbp.addItemListener( new ItemListener() {
      
        public void itemStateChanged(ItemEvent event) {
            
            if (event.getStateChange() == ItemEvent.SELECTED
                    && !((String)event.getItem()).equals(" ")) {
                
                new LogicalSourceEltsSyntaxCheck(event , "Descriptor", "SPA>Synchronous Orbit Particle Analyzer");
                  
            }; // endif ItemEvent.SELECTED
        }} );
        
        gbc.gridy = y++;      
        gbl.setConstraints(lcbp, gbc);
        p.add(lcbp);
        requiredControls.put("Descriptor", lcbp);
	
        //
        // Data Type
        //
        lcbp = new LabeledComboBoxPanel("Data Type", emptyBorder,
                                    editor.appProperties.getPropertyVector(
                                                         "gAttr.data_types"),
                                    true);
        CSH.setHelpIDString(lcbp, "gAttrDataType");
        lcbp.addItemListener(this);
        lcbp.addItemListener( new ItemListener() {
      
        public void itemStateChanged(ItemEvent event) {
            
            if (event.getStateChange() == ItemEvent.SELECTED) {
                
                new LogicalSourceEltsSyntaxCheck(event, "Data Type", "AT>Attitude");
                  
            }; // endif ItemEvent.SELECTED
        }} );

        gbc.gridy = y++;      
        gbl.setConstraints(lcbp, gbc);
        p.add(lcbp);
        requiredControls.put("Data_type", lcbp);
               
        
        //
        // File Naming Convention
        //
        FileNamingConventionPanel fileNamingConventionPanel = 
            new FileNamingConventionPanel();
        CSH.setHelpIDString(
            fileNamingConventionPanel, "gAttrFileNamingConvention");
        fileNamingConventionPanel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                editor.updateLogicalDescription = true;
            }
        });
        gbc.gridy = y++;      
        gbl.setConstraints(fileNamingConventionPanel, gbc);
        p.add(fileNamingConventionPanel);
        requiredControls.put(
            "File_naming_convention", fileNamingConventionPanel);

        
        JPanel piPanel = new JPanel(new GridLayout(1, 2, 2, 2));

        
        //
        // PI Name
        //
        
        gbc.ipady = 15;
        ltfp = new LabeledTextFieldPanel("PI Name", emptyBorder, 1, 25);
        CSH.setHelpIDString(ltfp, "gAttrPiName");
        piPanel.add(ltfp);
        requiredControls.put("PI_name", ltfp); 
        //
        // PI Affiliation
        //
     //   lcbp = new LabeledComboBoxPanel("PI Affiliation", emptyBorder,
     //                               editor.appProperties.getPropertyVector(
     //                                                  "gAttr.piAffiliations"),
     //                               true);
        
        ltfp = new LabeledTextFieldPanel("PI Affiliation", emptyBorder, 1, 25);
        CSH.setHelpIDString(ltfp, "gAttrPiAffiliation");
        piPanel.add(ltfp);
        requiredControls.put("PI_affiliation", ltfp);

        gbc.gridy = y++;
        gbl.setConstraints(piPanel, gbc);
        p.add(piPanel);        
        
        //
        // Discipline 
        //
     
        gbc.ipady = 5;
        lcbp = new LabeledComboBoxPanel("Discipline", emptyBorder, 2, 
                                    editor.appProperties.getPropertyVector(
                                                         "gAttr.disciplines"),
                                    false);
        CSH.setHelpIDString(lcbp, "gAttrDiscipline");
        gbc.gridy = y++;
        gbl.setConstraints(lcbp, gbc);
        p.add(lcbp);
        requiredControls.put("Discipline", lcbp);
        

        JPanel firstPanel = new JPanel(new GridLayout(2, 1));
        
        firstPanel.setMinimumSize(new Dimension(150, 50));

        //
        // Mission Groups
        //
        lcbp = new LabeledComboBoxPanel("Mission Group", emptyBorder, 1,
                            editor.appProperties.getPropertyVector(
                                                     "gAttr.missionGroups"),
                            true);
        CSH.setHelpIDString(lcbp, "gAttrMissionGroup");
        firstPanel.add(lcbp);
        requiredControls.put("Mission_group", lcbp);
	
        //
        // Data Version
        //
        lcbp = new LabeledComboBoxPanel("Data Version", emptyBorder,
                                    editor.appProperties.getPropertyVector(
                                                 "gAttr.dataVersions"),
                                    true);
        CSH.setHelpIDString(lcbp, "gAttrDataVersion");
 
        firstPanel.add(lcbp);
        requiredControls.put("Data_version", lcbp);
        //
        // Instrument type
        //
        lcbp = new LabeledComboBoxPanel("Instrument Types", emptyBorder, 3,
                                    editor.appProperties.getPropertyVector(
                                                 "gAttr.instrumentTypes"),
                                    true);
        CSH.setHelpIDString(lcbp, "gAttrInstrumentTypes");
        lcbp.setMinimumSize(new Dimension(150, 100));
        
        requiredControls.put("Instrument_type", lcbp);
        
        Box gridPanel = Box.createHorizontalBox();
        gridPanel.add(firstPanel);
        gridPanel.add(lcbp);
        
        gbc.gridy = y++;
        gbl.setConstraints(gridPanel, gbc);
        p.add(gridPanel);

        //
        // Logical Source Description
        //      
        gbc.gridy = y++;
        gbc.ipady = 20;
        gbc.fill = GridBagConstraints.BOTH;

        ltap = new LabeledTextAreaPanel("Logical Source / Short Dataset Description",
                                        emptyBorder, 1, 2, size);
        ltap.setLineWrap(true);
        CSH.setHelpIDString(ltap, "gAttrLogicalSourceDescription");
        gbl.setConstraints(ltap, gbc);
        p.add(ltap);
        
        requiredControls.put("Logical_source_description", ltap);

        //
        // Descriptive text
        //
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = y++;
        gbc.ipady = 100;
        gbc.weighty = 0.2;
        
        ltap = new LabeledTextAreaPanel("Extended Dataset Descriptive Text", emptyBorder,
                                        1, 5, size);
        ltap.setLineWrap(true);
        ltap.setMultiLineOption(true);
        CSH.setHelpIDString(ltap, "gAttrDescriptiveText");
        gbl.setConstraints(ltap, gbc);
        p.add(ltap);
        requiredControls.put("TEXT", ltap);

	
        p.setPreferredSize(new Dimension(editor.WIDTH/2 - 15, 
        				 editor.HEIGHT/2 - 40));

        return p;
    }

    /**
     * Constructs the Recommended Global Attributes panel.
     * 
     * @return the recommended global attributes panel
     */
    private JPanel recPanel() {
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel p = new JLabeledPanel("Recommended", gbl);
	
        LabeledTextAreaPanel ltap;      // used to refer to each of the
                                        //  LabeledTextAreaPanels created
                                        //  during their construction and
                                        //  initialization
        LabeledComboBoxPanel lcbp;      // used to refer to each of the
                                        //  LabeledComboBoxPanels created
                                        //  during their construction and
                                        //  initialization
        LabeledTextFieldPanel ltfp;     // used to refer to each of the
                                        //  LabeledTextFieldPanels created
                                        //  during their construction and
                                        //  initialization
        EmptyBorder emptyBorder = new EmptyBorder(0, 0, 0, 0);
                                        // an empty border to replace the
                                        //  default etched border on
                                        //  LabeledComboBoxPanels and
                                        //  LabeledTextFieldPanels
        int size = 24;
	
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.weighty = 0.1;
        gbc.ipady = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0,5,0,5);
        
        
        //
        // Acknowledgement
        //
        lcbp = new LabeledComboBoxPanel("Acknowledgement", emptyBorder,
                                    editor.appProperties.getPropertyVector(
                                                      "gAttr.acknowledgements"),
                                    true);
        CSH.setHelpIDString(lcbp, "gAttrAcknowledgement");
        gbc.gridy = 0;
        gbl.setConstraints(lcbp, gbc);
        p.add(lcbp);
        recommendedControls.put("Acknowledgement", lcbp);
	
        //
        // Rules of use
        //
        lcbp = new LabeledComboBoxPanel("Rules of Use", emptyBorder,
                                    editor.appProperties.getPropertyVector(
                                                      "gAttr.rulesOfUse"),
                                    true);
        gbc.gridy = 1;
        gbl.setConstraints(lcbp, gbc);
        p.add(lcbp);
        recommendedControls.put("Rules_of_use", lcbp);
        CSH.setHelpIDString(lcbp, "gAttrRulesOfUse");
	
        JPanel gridPanel = new JPanel(new GridLayout(2, 2,2,2));
      //  gbc.ipady =10;
        
/*
        //
        // ADID Ref 
        //
        lcbp = new LabeledComboBoxPanel("ADID Ref", emptyBorder,
                                    editor.appProperties.getPropertyVector(
                                                         "gAttr.adidRefs"),
                                    true);
        CSH.setHelpIDString(lcbp, "gAttrAdidRefs");

        gridPanel.add(lcbp);
        recommendedControls.put("ADID_ref", lcbp);
*/
        //
        // Generated by
        //
        ltfp = new LabeledTextFieldPanel("SPASE ID", emptyBorder, 1, 25);
        gridPanel.add(ltfp);
        recommendedControls.put("spase_DatasetResourceID", ltfp);
        CSH.setHelpIDString(ltfp, "gAttrSpaseId");
	

        //
        // Time Resolution
        //
        lcbp = new LabeledComboBoxPanel("Time Resolution", emptyBorder,
                                    editor.appProperties.getPropertyVector(
                                                     "gAttr.timeResolutions"),
                                    true);
        CSH.setHelpIDString(lcbp, "gAttrTimeResolution");
        gridPanel.add(lcbp);
        recommendedControls.put("Time_resolution", lcbp);
	
        //
        // Generated by
        //
        ltfp = new LabeledTextFieldPanel("Generated by", emptyBorder, 1, 25);
        gridPanel.add(ltfp);
        recommendedControls.put("Generated_by", ltfp);
        CSH.setHelpIDString(ltfp, "gAttrGeneratedBy");
	
        //
        // Generation date
        //
        lcbp = new LabeledComboBoxPanel("Generation Date", emptyBorder,
                                    editor.appProperties.getPropertyVector(
                                                     "gAttr.generationDates"),
                                    true);
        CSH.setHelpIDString(lcbp, "gAttrGenerationDate");
        gridPanel.add(lcbp);
        recommendedControls.put("Generation_date", lcbp);

        gbc.gridy = 2;
        
        gbl.setConstraints(gridPanel, gbc);
        p.add(gridPanel);

        //
        // Link Text
        //
      //  gbc.ipady =15;
        
        ltfp = new LabeledTextFieldPanel(
                                "Link Text (describing on-line data)", 
                                emptyBorder, 3, 50);
        CSH.setHelpIDString(ltfp, "gAttrLinkText");
 
        gbc.gridy= 3;
        gbl.setConstraints(ltfp, gbc);
        p.add(ltfp);
        recommendedControls.put("LINK_TEXT", ltfp);
	

        //
        // Link Title
        //
       
        ltfp = new LabeledTextFieldPanel("Link Title", emptyBorder, 3, 50);
        CSH.setHelpIDString(ltfp, "gAttrLinkTitle");
        gbc.gridy = 4;
        gbl.setConstraints(ltfp, gbc);
        p.add(ltfp);
        recommendedControls.put("LINK_TITLE", ltfp);
	
        //
        // HTTP Link 
        //
        ltfp = new LabeledTextFieldPanel("HTTP Link", emptyBorder, 3, 50);
        CSH.setHelpIDString(ltfp, "gAttrHttpLink");
        gbc.gridy = 5;
        gbl.setConstraints(ltfp, gbc);
        p.add(ltfp);
        recommendedControls.put("HTTP_LINK", ltfp);
	
        //
        // MODS
        //
        gbc.weighty = 0.2;
        gbc.ipady =90;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.BOTH;

        ltap = new LabeledTextAreaPanel("Modification History", emptyBorder,
                                         1, 5, size);
        ltap.setLineWrap(true);
        ltap.setMultiLineOption(true);
        CSH.setHelpIDString(ltap, "gAttrModificationHistory");
        gbl.setConstraints(ltap, gbc);
        p.add(ltap);
        recommendedControls.put("MODS", ltap);

        p.setPreferredSize(new Dimension(editor.WIDTH/2 - 15, 
					 editor.HEIGHT/2 - 40));

        return p;
    }
    /**
     * Saves the Global Attribute values from the panel into the CDF file.
     * 
     * @exception CDFException thrown if an exception occurs accessing the CDF
     */

    ////////////////////////////////
    //                            //
    //       Utility Methods      //
    //                            //
    ////////////////////////////////

    public void saveGlobalAttributes() 
        throws CDFException
    {
        String key;
        Object widget;

        for (Enumeration e = requiredControls.keys(); e.hasMoreElements();) {

            key = (String)e.nextElement();
            widget = requiredControls.get(key);

            saveGlobalAttribute(key, widget);
        }
	
        for (Enumeration e = recommendedControls.keys(); 
             e.hasMoreElements();) {

            key = (String)e.nextElement();
            widget = recommendedControls.get(key);

            saveGlobalAttribute(key, widget);
        }
    }

    /**
     * Sets the Global Attributes values on the panel from the values in 
     * the CDF file.
     */
    public void setGlobalAttributes() {

        String key;
        Object widget;

        for (Enumeration e = requiredControls.keys(); e.hasMoreElements();) {

            key = (String)e.nextElement();
            widget = requiredControls.get(key);
  
            setGlobalAttribute(key, widget);
        }

        for (Enumeration e = recommendedControls.keys(); e.hasMoreElements();) {

            key = (String)e.nextElement();
            widget = recommendedControls.get(key);

            setGlobalAttribute(key, widget);
        }
    }


    /**
     * Saves the Global Attribute value from the given widgent and 
     * identified by the given key into the CDF file.
     * 
     * @param key name of global attribute to save
     * @param widget component representing the attribute
     * @exception CDFException thrown if an exception occurs accessing 
     *     the CDF
     */
    private void saveGlobalAttribute(String key, Object widget) 
        throws CDFException {

        if (widget instanceof GeneralInputComponent) {

            copyComponentValuesToCdf(key, (GeneralInputComponent)widget);
        }
        else {

            System.err.println(
                    "GlobalPanel.saveGlobalAttributes: Bad widget for " + key);
        }
    }

    /**
     * Sets the value of the Global Attribute identified by the given 
     * key in the given widget to the value obtained from the CDF file.
     * 
     * @param key name of the global attribute
     * @param widget component displaying the attribute on the panel
     */
    private void setGlobalAttribute(String key, Object widget) {

        if (widget instanceof GeneralInputComponent) {

            copyCdfValuesToComponent(key, (GeneralInputComponent)widget);
        }
        else {

            System.err.println(
                    "GlobalPanel.setGlobalAttributes: Bad widget for " + key);
        }
    }

	
    /**
     * Copies the identified CDF attribute values to the given 
     * component.
     * 
     * @param key name of CDF attribute
     * @param gic general interface component to receive the CDF 
     *            attribute values
     */
    private void copyCdfValuesToComponent(String key, 
                                          GeneralInputComponent gic) {
        Attribute a;

        try {

            a = editor.theCDF.getAttribute(key);
            long numEntries = a.getNumEntries();
            int numInputComponents = gic.getInputComponentCount();

            for (int i = 0; i < numEntries && i < numInputComponents; i++) {
                
                StringBuffer data = new StringBuffer();
                
                if(gic instanceof LabeledTextAreaPanel){
                                      
                    for(int j= 0; j < numEntries; j++){
                    
                        data.append(a.getEntry(j).getData().toString());
                   
                        if(j < numEntries -1){
                        
                            data.append("\n");
                        }
                    }
                }
                else {

                    Entry e = a.getEntry(i);
                    data.append(Value.toString(e.getData(), e.getDataType()));
                }
               
                gic.setInputComponentValue(i, data.toString());
                System.err.println("Setting "+key+" to " + data.toString());
            };

        } 
        catch (CDFException exc) {

            if (exc.getCurrentStatus() == NO_SUCH_ATTR) {

        	System.err.println("Attribute \""+key+"\" not found. "+
        			   "It will be added upon saving.");
        	try {
        	    Attribute.create(editor.theCDF, key, 
        			     GLOBAL_SCOPE);

        	} catch (CDFException exc2) {
        	     editor.setStatus("Attribute \""+key+
        			       "\" unable to be added.", 
        			       StatusBar.ERROR, false, true);
        	}
            }
        }
    }
    

    /**
     * Copies the values from the given component into the identified 
     * CDF attribute.
     * 
     * @param key name of CDF attribute
     * @param gic general interface component whose values are to be 
     *            copied to the CDF
     * @exception CDFException thrown if an exception occurs accessing 
     *            the CDF file
     */
    private void copyComponentValuesToCdf(String key, 
                                          GeneralInputComponent gic)
	throws CDFException {

        int numComponents = gic.getInputComponentCount();

        Attribute a = editor.theCDF.getAttribute(key);

        long dataType = CDF.CDF_CHAR;  // original datatype of
                                       // attribute entry
        try {

            dataType = a.getEntry(0).getDataType();
        }
        catch (CDFException e) {

            // default CHAR is good
        }

        //
        // delete all possible gEntries that we may have new values for
        //
        for (int i = 0; i < numComponents; i++) {

            try {

                a.getEntry(i).delete();
            }
            catch(CDFException e) {

                // if it doesn't exist then we don't need to delete it
            };
        };

        //
        // set the new values
        //
        for (int i = 0, entry = 0; i < numComponents; i++) {

            Object compValue = gic.getInputComponentValue(i);

            if (compValue != null) {

                Vector<String> values = null;

                if (compValue instanceof String) {

                    values = new Vector<String>(1);
                    values.add((String)compValue);
                }
                else {

                    values = (Vector<String>)compValue;
                }

                for (int j = 0; j < values.size(); j++) {

                    String value = values.elementAt(j);

                    if (value != null && !value.equals("") && 
                        !value.equals(" ")) {

                        System.err.print("Saving " + key + ", entry ");
                        System.err.print(entry);
                        System.err.println(" = " + value);

                        Object objValue = Value.decode(value, dataType);
                                       // object representation of value
                        Entry.create(a, entry++, dataType, objValue);
//        	Entry.create(a, entry++, CDF_CHAR, value);
                    }
                }

                // We need to clear the component value for the case
                // where the i'th component value is saved in the
                // i - j entry so values don't remain in the GUI
                // component that do not correspond to entries in the 
                // CDF.
                gic.setInputComponentValue(i, null);
            }
        }
    }


    public void resetPanel() {
        AbstractLabeledInputComponent widget;
        String key;

        for (Enumeration keys = recommendedControls.keys(); 
             keys.hasMoreElements(); ) {
            key = (String)(keys.nextElement());
            widget = (AbstractLabeledInputComponent)recommendedControls.get(key);
            widget.reset();
        }
        for (Enumeration keys = requiredControls.keys(); 
             keys.hasMoreElements(); ) {
            key = (String)(keys.nextElement());
            Object temp = requiredControls.get(key);
            if (temp instanceof AbstractLabeledInputComponent){
                            
        	((AbstractLabeledInputComponent)temp).reset();
               
                
            }  
        }
    }

    /**
     * Listen for changes to Source_name, Descriptor, or Data type.
     * If any of these change then let SKTEditor know that the logical
     * global attributes will need to be updated.
     */
    public void itemStateChanged(ItemEvent event) {
 
        editor.updateLogicalDescription = true;
    }
   
    private static class LogicalSourceEltsSyntaxCheck  {
        
        public LogicalSourceEltsSyntaxCheck(ItemEvent event, String type, String example) {
              
            String source = (String)event.getItem();
                                          // source name value
                                
            if (source.length() == 0 ){
                
                return;
            };
                                 
            String invalidSourceCause = ISTPCompliance.checkSource(source);
                                          // the reason source is invalid

           if (invalidSourceCause != null) {
                
               
                
                if (invalidSourceCause.equalsIgnoreCase("replace character")){
                    
                    String newSource = source.replace('/','_');
                    System.out.println(" ");                                       
                    System.out.println("   "+ source + " was changed to "+ newSource +" ;");
                     ((JComboBox)event.getSource()).setSelectedItem(newSource);
                     
                     return;
                
                }

                    //
                    // On at least the platforms listed below (there may be
                    // others), this method can be called during a menu 
                    // selection event and displaying the dialog will cause
                    // an exception.  To avoid the problem, delay showing the
                    // dialog until the menu event completes.
                    // Affected platforms:
                    //   Java 1.1.8 on MacOS 8.6
                    //   Java 1.2 on Solaris 8
                    //   Java 1.3 on Solaris 8
                    // Note that the problem does not occur on Java 1.3 on MS
                    // Windows 2000.
                    //
                SwingUtilities.invokeLater(
                                new ErrorDialog(invalidSourceCause, type, example));

                ((JComboBox)event.getSource()).setSelectedIndex(-1);

            };
        }; 
       
        /**
         * A runnable class (which therefore can be invoked later) which
         * displays an error dialog.
         *
         * @author B. Harris
         */
        private static class ErrorDialog implements Runnable {

            /**
             * Additional text to be included in text of dialog.
             */
            private String errMsg;
            
            /**
             * Global attribute type generating this error message.
             */ 
            private String source;
            
            /**
             * Example of syntax required for this particular global attribute.
             */ 
            private String example;

            /**
             * Construct an ErrorDialog.
             * 
             * @param errMsg Additional text to be included in text of dialog
             * @param source global attribute type generating this error 
             *     message.
             * @param example example of syntax required for this particular
             *     global attribute.
             */
            public ErrorDialog(String errMsg, String source, String example) {

                this.errMsg = errMsg;
                this.source = source;
                this.example = example;
            }

            /**
             * Called to have the dialog displayed.
             */
            public void run() {

                JOptionPane.showMessageDialog(null,
                        "Invalid " + source + " Name: " + errMsg +
                        "\n\nThe value should be of the form\n" +
                        "    shortname>longname\n" +
                        "where 'shortname' consists only of alphanumeric\n" +
                        "characters and '-' (no blanks or other special\n" +
                        "characters).  The 'longname' may contain blanks\n" +
                        "and non-alphanumeric characters.  For example,\n" +
                        "    " + example + "\n" +
                        "is a valid value.", "Invalid Source Name", 
                        JOptionPane.ERROR_MESSAGE);
            }
        } // end class ErrorDialog
    } // end class SourceChangedListener

}
