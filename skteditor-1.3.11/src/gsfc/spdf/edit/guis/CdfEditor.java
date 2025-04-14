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
 * $Id: CdfEditor.java,v 1.52 2022/03/24 10:38:32 btharris Exp $
 */
package gsfc.spdf.edit.guis;


import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.tree.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeExpansionEvent;

import gsfc.spdf.edit.events.*;
import gsfc.spdf.istp.ISTPIdentifierException;
import gsfc.spdf.istp.GlobalAttribute;
import gsfc.nssdc.cdf.*;


public class CdfEditor 
    extends JDialog {
       
    protected CDF cdf = null;
    protected boolean deleteInProgress = false;

    protected Container contentPane = null;
    protected DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
    protected DefaultMutableTreeNode globalAttributesNode = null;
    protected DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
    protected JTree attributeTree = new JTree(treeModel);

    protected CardLayout detailsLayout = new CardLayout();
    protected JPanel detailsPanel = new JPanel(detailsLayout);
    
     /**
     * Initial witdth of the dialog window
     */
    public final static int WIDTH = 500;
    
     /**
     * Initial height of the dialog window
     */
    public final static int HEIGHT = 550;

    //
    // detailsPanel card layouts
    //
    final static String BLANK_PANEL = "Blank Panel";
    final static String VARIABLE_PANEL = "Variable Panel";
    final static String ATTRIBUTE_PANEL = "Attribute Panel";

    protected final static JPanel blankPanel = new JPanel();
    protected VariableAttributePanel variableAttributePanel =
                                              new VariableAttributePanel();
    protected AttributePanel attributePanel = new AttributePanel();

    protected static class AttributeTreeNode 
        extends DefaultMutableTreeNode {

        public AttributeTreeNode(Attribute attribute) {

            this(attribute, false);
        }


        public AttributeTreeNode(Attribute attribute, 
                                 boolean saveMultiLineAsMultiEntry) {
            super(attribute);
            this.saveMultiLineAsMultiEntry = saveMultiLineAsMultiEntry;
        }


        public boolean getSaveMultiLineAsMultiEntry() {

            return saveMultiLineAsMultiEntry;
        }


        public void setSaveMultiLineAsMultiEntry(boolean value) {

            saveMultiLineAsMultiEntry = value;
        }


        public Attribute getAttribute() {

            return (Attribute)getUserObject();
        }


        private boolean saveMultiLineAsMultiEntry;
    }

    private static class NewAttributeDialog 
        extends JOptionPane{ 
                                
        private JDialog dlg; 
        private JComboBox inputfield = new JComboBox();
        
        public NewAttributeDialog(Component parent, String title, 
                                  boolean global)
        { 
            Vector attributeNames = null;
            setOptionType(JOptionPane.OK_CANCEL_OPTION);
            setMessage(null);
            inputfield.setEditable(true);
            if (global)
            {
                 attributeNames = SKTEditor.appProperties.getPropertyVector(
                                                         "gAttr.standard");
             }
            else
            {
                 attributeNames = SKTEditor.appProperties.getPropertyVector(
                                                         "vAttr.standard");
            }
            inputfield.add(new JTextField(),0);

            for (int i = 0; i < attributeNames.size(); i++)
            {
                inputfield.addItem(attributeNames.elementAt(i));
            };
            
            JPanel p = new JPanel();
            p.setLayout(new GridLayout(2,1));
	        JLabel label= new JLabel("Attribute Name");
	        label.setForeground(Color.black);
            
            p.add(label);
            inputfield.setAlignmentX(LEFT_ALIGNMENT);
            p.add(inputfield);
            inputfield.setSelectedIndex(-1);
 
            add (p,0);

            dlg = createDialog(null,title); 
            Dimension dims = dlg.getPreferredSize();
            dims.height += 100;
            dlg.setSize(dims);
         } 
       
        public String showDialog()
        { 
            dlg.setVisible(true);
          
            Integer value = (Integer)getValue();
            if((value !=null) && (value.intValue() == JOptionPane.OK_OPTION)&&
                    (inputfield.getSelectedItem()!=null))
                return inputfield.getSelectedItem().toString();
            else
                return null;
        }
    }

    private static class CopyAttributeDialog 
        extends JOptionPane { 
                                
        private JDialog dlg; 

        private JTextField attrName = new JTextField(15);
        private JTextArea attrValue = new JTextArea(1, 15);
        private JList varsList = new JList();
        private JTextField targetAttrName = new JTextField(15);

        public static class Target {

            public String attributeName;
            public List<Variable> variables = 
                new ArrayList<Variable>();
        }

        public CopyAttributeDialog(
            Component parent, 
            String title, 
            Attribute attr,
            Entry entry,
            Vector vars) {

            JPanel sourcePanel = new JPanel();
            sourcePanel.setLayout(
                new BoxLayout(sourcePanel, BoxLayout.Y_AXIS));
            sourcePanel.setBorder(
                new TitledBorder(new EtchedBorder(), "Source"));

            JLabel attrNameLabel = 
                new JLabel("Attribute Name");
            attrNameLabel.setLabelFor(attrName);

            JLabel attrValueLabel = 
                new JLabel("Attribute Value");
            attrValueLabel.setLabelFor(attrValue);

            JScrollPane attrValueScrollPane = 
                new JScrollPane(attrValue);

            sourcePanel.add(attrNameLabel);
            sourcePanel.add(attrName);
            sourcePanel.add(attrValueLabel);
            sourcePanel.add(attrValueScrollPane);

            JPanel targetPanel = new JPanel();
            targetPanel.setLayout(
                new BoxLayout(targetPanel, BoxLayout.Y_AXIS));
            targetPanel.setBorder(
                new TitledBorder(new EtchedBorder(), "Target"));

            JLabel varsLabel = 
                new JLabel("Variable(s)");
            varsLabel.setLabelFor(varsList);

            JLabel targetAttrNameLabel = 
                new JLabel("Attribute Name");
            targetAttrNameLabel.setLabelFor(targetAttrName);

            JScrollPane varsListScrollPane = 
                new JScrollPane(varsList);

            targetPanel.add(varsLabel);
            targetPanel.add(varsListScrollPane);
            targetPanel.add(targetAttrNameLabel);
            targetPanel.add(targetAttrName);


            setOptionType(JOptionPane.OK_CANCEL_OPTION);
            setMessage(null);
            
            JPanel p = new JPanel();
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

            p.add(sourcePanel);
            p.add(targetPanel);
            add (p,0);

            attrName.setEditable(false);
            attrName.setText(attr.getName());
            attrValue.setEditable(false);
            try {

                attrValue.setText(entry.getData().toString());
            }
            catch (CDFException e) {

                System.err.println("CDFException: " + e.getMessage());
            }
            varsList.setListData(vars);
            targetAttrName.setText(attr.getName());

            dlg = createDialog(null,title); 
        } 
       
        public Target showDialog() { 

            dlg.setVisible(true);
          
            Integer value = (Integer)getValue();

            if((value != null) && 
               (value.intValue() == JOptionPane.OK_OPTION)) {

                Target target = new Target();

                target.attributeName = targetAttrName.getText();

                for (Object var : varsList.getSelectedValues()) {

                    target.variables.add((Variable)var);
                }

                return target;
            }
            else {

                return null;
            }
        }
    }

    public CdfEditor(Frame owner, String title, boolean modal) {

        super(owner, title, modal);
        setSize(WIDTH, HEIGHT);
        Dimension  screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//        setLocation(screenSize.width/2 - WIDTH/2,screenSize.height/2 - HEIGHT/2);
        setLocationRelativeTo(owner);

    //    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        addWindowListener(
            new WindowAdapter() {

                public void windowClosing(WindowEvent e) {
                                   
                    try {

                        saveChanges();
                    }
                    catch (CDFException ex) {

                        ex.printStackTrace();

                        JOptionPane.showMessageDialog(contentPane,
                                "Error saving most recent changes:" + 
                                ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    };
                    //CdfEditor.this.setVisible(false);
                    dispose();
                }
            }
        );
                
        contentPane = getContentPane();

        setJMenuBar(createMenuBar());

        contentPane.setLayout(new BorderLayout());

        detailsPanel.add(blankPanel, BLANK_PANEL);
        detailsPanel.add(variableAttributePanel, VARIABLE_PANEL);
        detailsPanel.add(attributePanel, ATTRIBUTE_PANEL);
//        detailsPanel.add(textAttributePanel, TEXT_ATTRIBUTE_PANEL);
        detailsLayout.show(detailsPanel, BLANK_PANEL);

//        attributeTree.setShowsRootHandles(false);
//        attributeTree.setRootVisible(false);
        attributeTree.putClientProperty("JTree.lineStyle", "Angled");
        attributeTree.getSelectionModel().setSelectionMode(
                      TreeSelectionModel.SINGLE_TREE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(attributeTree);

        contentPane.add(scrollPane, BorderLayout.WEST);
        contentPane.add(detailsPanel, BorderLayout.CENTER);

        contentPane.validate();

        attributeTree.addTreeSelectionListener(
                                           new CustomTreeSelectionListener());
        attributeTree.addTreeExpansionListener(new TreeExpansionListener() {

            public void treeExpanded(TreeExpansionEvent e) {

//                contentPane.validate();  // this isn't working right
            }

            public void treeCollapsed(TreeExpansionEvent e) {

//                contentPane.validate();  // this isn't working right
            }
        });
    }


    public void saveChanges() 
        throws CDFException {

        saveSelection(getSelection());
    }


    public void setCdf(CDF cdf) {

        this.cdf = cdf;

        //
        // It's easier and faster to create and set a new tree model 
        // (containing just a root node) than it is to traverse the entire
        // tree deleting all but the root node.
        //
        rootNode = new DefaultMutableTreeNode();
        treeModel = new DefaultTreeModel(rootNode);
        attributeTree.setModel(treeModel);

        if (cdf != null) {

            createGlobalAttributeNodes(cdf);
            createVariableNodes(cdf);
        };

        attributeTree.expandRow(0);
        detailsLayout.show(detailsPanel, BLANK_PANEL);
    }

   
    protected void createGlobalAttributeNodes(CDF cdf) {

        globalAttributesNode = 
                        new DefaultMutableTreeNode("Global Attributes");
        treeModel.insertNodeInto(globalAttributesNode, rootNode, 0);

        Vector globalAttributes = cdf.getGlobalAttributes();

        for (int i = 0; i < globalAttributes.size(); i++) {

            Attribute globalAttribute = 
                              (Attribute)globalAttributes.elementAt(i);

            AttributeTreeNode attributeNode = 
                        new AttributeTreeNode(globalAttribute, true);
            treeModel.insertNodeInto(attributeNode, globalAttributesNode, i);

            try {

                Vector entries = globalAttribute.getEntries();

                if (entries.size() > 1) {

                    int k = 0;

                    for (int j = 0; j < entries.size(); j++) {

                        Entry entry = (Entry)entries.elementAt(j);
                 
                        if (entry != null) {

                            DefaultMutableTreeNode entryNode =
                                new DefaultMutableTreeNode(
                                        new Long(entry.getID()));

                            treeModel.insertNodeInto(
//                                entryNode, attributeNode, j);
                                entryNode, attributeNode, k++);
                        }
                    }
                } // end if (entries.size() > 1)
            }
            catch (CDFException e) {

            }
        };
    }

    protected void createVariableNodes(CDF cdf) {

        Vector variables = cdf.getVariables();
        Vector varAttributes = cdf.getVariableAttributes();

        for (int i = 0; i < variables.size(); i++) {

            Variable variable = (Variable)variables.elementAt(i);
            //
            // get attributes and make nodes of them
            //

            DefaultMutableTreeNode varNode = 
                        new DefaultMutableTreeNode(variable);
            treeModel.insertNodeInto(varNode, rootNode, i + 1);

            for (int j = 0; j < varAttributes.size(); j++) {

                Attribute varAttribute = 
                                    (Attribute)varAttributes.elementAt(j);
                try {

                    Entry entry = varAttribute.getEntry(variable);

                    //
                    // probably also want to put the entry in the attrNode
                    //
//                    DefaultMutableTreeNode attrNode =
//                            new DefaultMutableTreeNode(varAttribute);
                    AttributeTreeNode attrNode =
                            new AttributeTreeNode(varAttribute);
                    treeModel.insertNodeInto(attrNode, varNode,
                                                 varNode.getChildCount());
                }
                catch(CDFException e) {
                    //
                    // ignore it, if there's no entry, there's no attribute
                    //  to add to the tree
                    //
                };

            };
        };
    }


    /**
     * Searches the treeModel for the DefaultMutableTreeNode 
     * associated with the given variable.
     *
     * @param var Variable whose TreeNode is to be returned.
     * @return the requested DefaultMutableTreeNode or null if not 
     *     found.
     */
    private DefaultMutableTreeNode getTreeNode(
        Variable var) {

        for (int i = 0; i < treeModel.getChildCount(rootNode); i++) {

            DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                treeModel.getChild(rootNode, i);

            Object item = node.getUserObject();

            if (item instanceof Variable) {

                Variable varNode = (Variable)item;

                if (var.getName().equals(varNode.getName())) {

                    return node;
                }
            }
        }

        return null;
    }


    /**
     * Searches the treeModel for the AttributeTreeNode associated 
     * with the given Attribute under the given variable node.
     *
     * @param varNode variable TreeNode where search is to begin.
     * @param attr the Attribute whose TreeNode is to be returned.
     * @return the requested AttributeTreeNode or null if not found.
     */
    private AttributeTreeNode getTreeNode(
        DefaultMutableTreeNode varNode,
        Attribute attr) {

        for (int i = 0; i < treeModel.getChildCount(varNode); i++) {

            AttributeTreeNode attrNode = (AttributeTreeNode)
                treeModel.getChild(varNode, i);

            Attribute nodeAttr = attrNode.getAttribute();

            if (attr.getName().equals(nodeAttr.getName())) {

                return attrNode;
            }
        }

        return null;
    }


    /**
     * Adds an AttributeTreeNode to the treeModel for the given
     * variable and attribute if none currently exists.
     *
     * @param var the variable associated with the attribute.
     * @param attr the attribute to add to the tree.
     * @return true if a node was added to the tree.  false if the
     *     node already existed.
     */
    private boolean addAttributeToTree(
        Variable var,
        Attribute attr) {

        DefaultMutableTreeNode varNode = getTreeNode(var);

        if (varNode != null) {

            AttributeTreeNode attrNode = getTreeNode(varNode, attr);

            if (attrNode == null) {

                attrNode = new AttributeTreeNode(attr);

                treeModel.insertNodeInto(attrNode, varNode,
                    varNode.getChildCount());

                return true;
            }
        }

        return false;
    }



    protected class Selection {

        private TreePath path = null;
        private Variable variable = null;
        private Attribute attribute = null;
        private Vector entries = null;
        private AttributeTreeNode attrNode = null;
        private boolean saveMultiLineAsMultiEntry = false;


        Selection(TreePath path)
            throws CDFException {

            if (path == null || deleteInProgress) {

                return;
            };

//System.err.println("Selection: path = " + path);

            int pathCount = path.getPathCount();

            if (pathCount == 1) { // selected root node only
            
                return;
            };

            this.path = path;

            DefaultMutableTreeNode level1Node = (DefaultMutableTreeNode)
                                                  path.getPathComponent(1);

            Object level1Item = level1Node.getUserObject();

            if (level1Item instanceof Variable) {

//System.err.println("Selection: instanceof Variable");

                variable = (Variable)level1Item;

                if (pathCount == 3) { // selected variable and attribute
                
                    attribute = (Attribute)((DefaultMutableTreeNode)
                                                  path.getPathComponent(2)).
                                                 getUserObject();

                    entries = new Vector();
                    try {
                        entries.addElement(attribute.getEntry(variable));
                    }catch (CDFException e){
                        if (e.getCurrentStatus() != CDF.NO_SUCH_ENTRY) {

                             throw e;
                        }
                        
                    }
                };
            }
            else { // global attributes

//System.err.println("Selection: global attribute, path = " + path + 
//                   ", pathCount = " + pathCount);

                if (pathCount > 2) { // selected an attribute or entry
                
                    attribute = (Attribute)((DefaultMutableTreeNode)
                                                  path.getPathComponent(2)).
                                                 getUserObject();

//                    attrNode = (AttributeTreeNode) path.getPathComponent(2);
//                    attribute = attrNode.getAttribute();
//                    saveMultiLineAsMultiEntry = 
//                                   attrNode.getSaveMultiLineAsMultiEntry();

                    if (pathCount == 4) { // selected a specific entry
                    
                        Long entryId = (Long)((DefaultMutableTreeNode)
                                                  path.getPathComponent(3)).
                                                 getUserObject();

                        entries = new Vector(1);
//System.err.println("Selection: adding entry with id " + entryId + 
//                   " to vector");
                        entries.addElement(attribute.getEntry(
                                                         entryId.longValue()));
                    }
                    else { // didn't selecte a specific entry
                      
//System.err.println("Selection: didn't select a specific entry");

                        entries = (Vector)attribute.getEntries().clone();
                    };
                };
            };
        }


        public TreePath getPath() {

            return path;
        }

        public Variable getVariable() {

            return variable;
        }

        public Attribute getAttribute() {

            return attribute;
        }

        public boolean getSaveMultiLineAsMultiEntry() {

            return saveMultiLineAsMultiEntry;
        }

        public void setSaveMultiLineAsMultiEntry(boolean value) {

            if (attrNode != null) {

                attrNode.setSaveMultiLineAsMultiEntry(value);
            };
        }

        public Vector getEntries() {

            return entries;
        }

        public int getNumActualEntries() {

            int actualEntries = 0;

            for (int i = 0; i < entries.size(); i++) {

                if (entries.elementAt(i) != null) {

                    actualEntries++;
                };
            };

            return actualEntries;
        }

        public Entry getActualEntryAt(int i) {

            int actualEntry = 0;

            for (int j = 0; j < entries.size(); j++) {

                if (entries.elementAt(j) != null) {

                    if (actualEntry == i) {

                        return (Entry)entries.elementAt(j);
                    };

                    actualEntry++;
                };
            };

            return null;
        }

        public void setActualEntryAt(int i, Entry entry) {

            int actualEntry = 0;

            for (int j = 0; j < entries.size(); j++) {

                if (entries.elementAt(j) != null) {

                    if (actualEntry == i) {

                        entries.setElementAt(entry, j);
                    };

                    actualEntry++;
                };
            };
        }
        
        public boolean allEntriesAreChar() {

            if (entries != null) {

                for (int i = 0; i < entries.size(); i++) {

                    Entry entry = (Entry)entries.elementAt(i);

                    if (entry != null && entry.getDataType() != CDF.CDF_CHAR) {

                        return false;
                    };
                };
                return true;
            }
  //          else {

                return true;
  //          };
        }


        public boolean variableIsSelected() {

            return variable != null;
        }


        public boolean globalAttributeIsSelected() {

            return (variable == null && 
                    path != null && path.getPathCount() > 1);
        }
    }


    protected Selection getSelection()
        throws CDFException {

        return new Selection(attributeTree.getSelectionPath());
    }


    protected class CustomTreeSelectionListener 
        implements TreeSelectionListener {

        public void valueChanged(TreeSelectionEvent event) {

            if (cdf == null) {

                return;
            };

            try {

                saveSelection(
                    new Selection(event.getOldLeadSelectionPath()));

                displaySelection(
                    new Selection(event.getNewLeadSelectionPath()));
            }
            catch (CDFException e) {

                e.printStackTrace();

                JOptionPane.showMessageDialog(contentPane,
                            "Error getting selection:" + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
            };
        }
    }


    public boolean saveSelection(Selection selection) 
        throws CDFException {

//System.err.println("saving old selection, deleteInProgress = " + 
//                   deleteInProgress);

        if (selection.getPath() == null || deleteInProgress) {

            return true;
        };

        if (selection.variableIsSelected()) {

            if (selection.getAttribute() != null) {  

                Object newValue = 
                    variableAttributePanel.getAttributeValue();

                if (newValue == null) {

                    JOptionPane.showMessageDialog(contentPane,
                        "Invalid value entered for '" + 
                        selection.getVariable().getName() + "/" +
                        selection.getAttribute().getName() + "'",
                        "Invalid Value", JOptionPane.ERROR_MESSAGE);

                    return false;
                };

                    ((Entry)selection.getEntries().elementAt(0)).delete();
                selection.getVariable().putEntry(selection.getAttribute(),
                               variableAttributePanel.getAttributeDatatype(),
                               newValue);
            };
        }
        else { 

            return saveGlobalAttributeEntries(selection);
        };

        return true;
    }


    protected boolean saveGlobalAttributeEntries(Selection selection) 
        throws CDFException {

        if (selection.getAttribute() != null) {

            selection.setSaveMultiLineAsMultiEntry(
                attributePanel.getMultiLineOption());

            if (selection.getAttribute().getNumEntries() == 0) {

                return false;
            };

            int actualSelectedEntries = selection.getNumActualEntries();

            Object newValue = attributePanel.getValue();

            if (newValue == null) {

                JOptionPane.showMessageDialog(contentPane,
                    "Invalid value entered for '" + 
                    selection.getAttribute().getName() + "'",
                    "Invalid Value", JOptionPane.ERROR_MESSAGE);

                attributePanel.setEntry(
                    selection.getAttribute().getName(), 
                    selection.getAttribute().getEntries());

                return false;
            }
            else if (newValue instanceof Vector) {

                Vector values = (Vector)newValue;

//System.err.println(values.size() + "new values to save");
                saveEntryValues(selection, values);
            }
            else {

//System.err.println("a single newValue with actualEntries = " + 
//                   actualSelectedEntries);

                if (actualSelectedEntries == 1) {

                    //
                    // replace value of the single selected entry
                    //

                    Entry replacementEntry = 
                              replaceEntryValue(
                                  selection.getAttribute(),
                                  selection.getActualEntryAt(0),
                                  attributePanel.getDataType(),
                                  newValue);

                    selection.setActualEntryAt(0, replacementEntry);
                }
                else {

                    //
                    // Replace all entries by a single entry with the 
                    // given value.
                    //
                    Vector valueVector = new Vector(1);

                    valueVector.addElement(newValue);

                    saveEntryValues(selection, valueVector);
                };
            };

        };  

        return true;
    }


    protected Entry replaceEntryValue(Attribute attribute, Entry entry, 
                                      long dataType, Object value) 
        throws CDFException {

        long entryId = entry.getID();

        entry.delete();

        return Entry.create(attribute, entryId, dataType, value);
    }


    protected void saveEntryValues(Selection selection, Vector values)
        throws CDFException {

        int newId = 0;

        int actualSelectedEntries = selection.getNumActualEntries();

        if (actualSelectedEntries == 1 &&
            selection.getAttribute().getNumEntries() > 1) {

            Attribute selectedAttribute = selection.getAttribute();
            Entry selectedEntry = selection.getActualEntryAt(0);

            if (values.size() == 1) {

                replaceEntryValue(selectedAttribute, selectedEntry, 
                                  CDF.CDF_CHAR, values.elementAt(0));
                return;
            };

            replaceEntryValue(selectedAttribute, selectedEntry, CDF.CDF_CHAR, 
                              values.elementAt(values.size() - 1));

            for (int i = values.size() - 2; i >= 0; i--) {

                insertGlobalAttributeEntry(selection, CDF.CDF_CHAR, 
                                           values.elementAt(i));
            };
            return;
        }
        else {

            deleteAllGlobalAttributeEntries(selection);
        };

//System.err.println("saveEntryValues: creating new entries");

        Attribute attribute = selection.getAttribute();
        DefaultMutableTreeNode attributeNode = (DefaultMutableTreeNode)
                                       selection.getPath().getPathComponent(2);
        
//System.err.println("saveEntryValues: attributeNode.getChildCount = "
//+ attributeNode.getChildCount());

        for (int i = 0, id = 0; i < values.size(); i++) {

            String value = (String)values.elementAt(i);

//System.err.println("values[" + i + "] = '" + value + "'");

            if (!value.equals("")) {

//System.err.println("saveEntryValues: creating entry " + id);

                Entry.create(attribute, id, CDF.CDF_CHAR, value);
      
                switch (id) {

                case 0:

                    //
                    // first entry is hidden unless there are more
                    //
                    break;

                case 1:

                    //
                    // create missing first entry node and fall through
                    //  to create this entry's node
                    //
//System.err.println("saveEntryValues: creating first entry node");
                    DefaultMutableTreeNode entryNode = new 
                                        DefaultMutableTreeNode(new Long(0));
                    treeModel.insertNodeInto(entryNode, attributeNode, 
                                             attributeNode.getChildCount());
                    // intentionally no break

                default:
                    
//System.err.println("saveEntryValues: creating " + id + " entry node");
                    entryNode = new DefaultMutableTreeNode(new Long(id));
                    treeModel.insertNodeInto(entryNode, attributeNode, 
                                             attributeNode.getChildCount());
                    break;
                };

                id++;
            };
        };
//System.err.println("saveEntryValues: finished");
    }


    protected void deleteAllGlobalAttributeEntries(Selection selection)
        throws CDFException {

//System.err.println("deleteAllGlobalAttributeEntries: deleting tree nodes and entries");

        DefaultMutableTreeNode attributeNode = (DefaultMutableTreeNode)
                                       selection.getPath().getPathComponent(2);

        Attribute attribute = selection.getAttribute();

        while (treeModel.getChildCount(attributeNode) > 0) {

            DefaultMutableTreeNode entryNode = (DefaultMutableTreeNode)
                                      treeModel.getChild(attributeNode, 0);

            Long entryId = (Long)entryNode.getUserObject();

//System.err.println("deleteAllGlobalAtttributeEntries: about to delete entry " +
//                   entryId);

            try {

                deleteInProgress = true;

                treeModel.removeNodeFromParent(entryNode);

                attribute.getEntry(entryId.longValue()).delete();
            }
            finally {

                deleteInProgress = false;
            };
        };

//System.err.println("deleteAllGlobalAttributeEntries: finished");
    }


    protected void deleteGlobalAttributeEntry(Selection selection)
        throws CDFException {

//System.err.println("deleteGlobalAttributeEntry: begin ");

        DefaultMutableTreeNode attributeNode = (DefaultMutableTreeNode)
                                       selection.getPath().getPathComponent(2);

        Attribute attribute = selection.getAttribute();

        int deleteEntryIndex = 0;

        if (selection.getPath().getPathCount() > 3) {
        
            DefaultMutableTreeNode selectedEntryNode = (DefaultMutableTreeNode)
                                      selection.getPath().getPathComponent(3);

            deleteEntryIndex = treeModel.getIndexOfChild(attributeNode,
                                                         selectedEntryNode);
        };

        if (attributeNode.getChildCount() > deleteEntryIndex) {

            DefaultMutableTreeNode entryNode = (DefaultMutableTreeNode)
                         treeModel.getChild(attributeNode, deleteEntryIndex);

            Long entryId = (Long)entryNode.getUserObject();

//System.err.println("deleteGlobalAtttributeEntry: about to delete entry " +
//                   entryId);

            try {

                deleteInProgress = true;
                attribute.getEntry(entryId.longValue()).delete();

                treeModel.removeNodeFromParent(entryNode);
            }
            finally {

                deleteInProgress = false;
            };
        };

//System.err.println("deleteGlobalAttributeEntry: finished");
    }


    protected void insertGlobalAttributeEntry(Selection selection,
                                              long datatype, Object value) 
        throws CDFException {

        DefaultMutableTreeNode attributeNode = (DefaultMutableTreeNode)
                                      selection.getPath().getPathComponent(2);
        int insertEntryIndex = 0;

        if (selection.getPath().getPathCount() > 3) {
        
            DefaultMutableTreeNode selectedEntryNode = (DefaultMutableTreeNode)
                                      selection.getPath().getPathComponent(3);

            insertEntryIndex = treeModel.getIndexOfChild(attributeNode,
                                                         selectedEntryNode);
        };

//System.err.println("insertGlobalAttributeEntry: insertEntryIndex = " + 
//   insertEntryIndex);

        Attribute attribute = selection.getAttribute();

        int totalEntries = treeModel.getChildCount(attributeNode);

        for (int i = totalEntries - 1; i >= insertEntryIndex; i--) {

//System.err.println("insertGlobalAttributeEntry: i = " + i);

            DefaultMutableTreeNode entryNode = (DefaultMutableTreeNode)
                                       treeModel.getChild(attributeNode, i);

            long id = ((Long)entryNode.getUserObject()).longValue();

//System.err.println("insertGlobalAttributeEntry: id = " + id);

            Entry entry = attribute.getEntry(id);

            long newId = id + 1;

            Entry newEntry = Entry.create(attribute, newId, 
                                          entry.getDataType(),
                                          entry.getData());

            if (i == totalEntries - 1) {

//System.err.println("insertGlobalAttributeEntry: creating newEntryNode, id = " 
//  + newId);
                DefaultMutableTreeNode newEntryNode = 
                              new DefaultMutableTreeNode(new Long(newId));

                treeModel.insertNodeInto(newEntryNode, attributeNode, i + 1);
            };
        };

//System.err.println("insertGlobalAttributeEntry: creating new entry, id = " 
//   + insertEntryIndex);
        Entry.create(attribute, insertEntryIndex, datatype, value);
    }


    public void displaySelection(Selection selection) {

        if (selection.getPath() == null) {

            detailsLayout.show(detailsPanel, BLANK_PANEL);
            return;
        };

        if (selection.variableIsSelected()) {

            detailsLayout.show(detailsPanel, VARIABLE_PANEL);

            if (selection.getAttribute() != null) {  
                


                variableAttributePanel.setEntry(
                                    selection.getAttribute().getName(), 
                                    (Entry)selection.getEntries().elementAt(0));

            }
            else {  // selected variable but not attribute
            
                variableAttributePanel.setEntry(null, null);
            };
            variableAttributePanel.setVariable(selection.getVariable());
        }
        else {  // global attributes
        
            displayGlobalAttributeSelection(selection);
        };
        //
        // This wouldn't be necessary if things were getting correctly 
        // re-layed out after a tree expansion and collapse
        //
        contentPane.validate();
    }


    protected void displayGlobalAttributeSelection(Selection selection) {

        if (selection.getAttribute() != null) {

            if (selection.getEntries() != null) {

                attributePanel.setMultiLineOption(
                                   selection.getSaveMultiLineAsMultiEntry());

                int actualEntries = selection.getNumActualEntries();

                switch (actualEntries) {

                case 0:

                    detailsLayout.show(detailsPanel, BLANK_PANEL);
/*
                    attributePanel.setEntry(
                                     selection.getAttribute().getName(), 
                                     (Entry)null);
                    detailsLayout.show(detailsPanel, ATTRIBUTE_PANEL);
*/
                    break;

                case 1:

//System.err.println("displayGlobalAttributeSelection: displaying a single entry");
                    attributePanel.setEntry(
                                     selection.getAttribute().getName(), 
                                     selection.getActualEntryAt(0));
                    detailsLayout.show(detailsPanel, ATTRIBUTE_PANEL);
                    break;

                default:

//System.err.println("displayGlobalAttributeSelection: " + actualEntries
//                   + " entries");

                    if (selection.allEntriesAreChar()) {

//System.err.println("displayGlobalAttributeSelection: all CHAR");
                        attributePanel.setEntry(
                                         selection.getAttribute().getName(), 
                                         selection.getEntries());
                    }
                    else {

//System.err.println("displayGlobalAttributeSelection: not all CHAR");
                        attributePanel.setEntry(
                                         selection.getAttribute().getName(), 
                                         selection.getActualEntryAt(0));
                    };
                    detailsLayout.show(detailsPanel, ATTRIBUTE_PANEL);
                    break;
                };

            }
            else { // attribute but no entries
            
                attributePanel.setEntry(selection.getAttribute().getName(), 
                                        (Entry)null);
                detailsLayout.show(detailsPanel, ATTRIBUTE_PANEL);
            };
        }
        else // no attribute or entry selected
        {  

            detailsLayout.show(detailsPanel, BLANK_PANEL);
        };  
    }


    private class AddAttributeActionListener 
        implements ActionListener {

        public void actionPerformed(ActionEvent event) {

            Selection selection = null;

            try {

                selection = getSelection();
            }
            catch (CDFException e) {

                e.printStackTrace();

                JOptionPane.showMessageDialog(contentPane,
                            "Error getting selection:" + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
            };

            if (selection == null || 
                (!selection.variableIsSelected() && 
                 !selection.globalAttributeIsSelected())) {

                JOptionPane.showMessageDialog(contentPane,
                            "You must select 'Global Attributes' or a variable",
                            "Missing Selection", JOptionPane.ERROR_MESSAGE);
                return;
            };

            NewAttributeDialog newAttributeDialog = null;

            Variable selectedVar = selection.getVariable();
                                       // selected variable
            if (selectedVar != null) {

                newAttributeDialog = new NewAttributeDialog(contentPane,
                                  "Enter the name of the new attribute",false);
            }
            else {

               newAttributeDialog = new NewAttributeDialog(contentPane,
                                   "Enter the name of the new attribute",true);
            };
            
            String attributeName = newAttributeDialog.showDialog(); 
            
            if (attributeName == null) {

                return;
            };

            TreePath path = selection.getPath();

            while (path.getPathCount() > 2) {

                path = path.getParentPath();
            };

            try {

                Attribute newAttribute = null;

                if (selection.variableIsSelected()) {

                    gsfc.spdf.istp.Variable istpSelectedVar =
                        new gsfc.spdf.istp.Variable(selectedVar);
                                       // ISTP version of selectedVar

                    if (istpSelectedVar.getAttributeEntry(attributeName)
                        != null) {

                        Attribute existingAttribute = 
                            istpSelectedVar.getAttribute(attributeName);
                                       // existing attribute

                        JOptionPane.showMessageDialog(contentPane,
                           "Attribute '" + attributeName + "' already " +
                           "exists as '" + existingAttribute.getName() +
                           "'.",
                           "Invalid Name", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    try {

                        istpSelectedVar.setAttributeValue(attributeName, " ");
                    }
                    catch (ISTPIdentifierException e) {

                        JOptionPane.showMessageDialog(contentPane,
                           "Attribute '" + attributeName + "' name error.\n" +
                            e.getMessage(),
                           "Invalid Name", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    newAttribute =
                        istpSelectedVar.getAttribute(attributeName);
                }
                else { // global attributes
                
                    try {

                        newAttribute = 
                            GlobalAttribute.create(cdf, attributeName);
                    }
                    catch (ISTPIdentifierException e) {

                        JOptionPane.showMessageDialog(contentPane,
                           "Attribute '" + attributeName + "' name error.\n" +
                            e.getMessage(),
                           "Invalid Name", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                };  

                DefaultMutableTreeNode level1Node = (DefaultMutableTreeNode)
                                    selection.getPath().getPathComponent(1);
                DefaultMutableTreeNode attributeNode = 
                                    new DefaultMutableTreeNode(newAttribute);
                treeModel.insertNodeInto(attributeNode, level1Node, 
                                             level1Node.getChildCount());
                attributeTree.setSelectionPath(path.pathByAddingChild( 
                                                               attributeNode));
                attributeTree.scrollPathToVisible(path.pathByAddingChild( 
                                                               attributeNode));

                if (!attributeName.equals(newAttribute.getName())) {

                    JOptionPane.showMessageDialog(contentPane,
                        "The new attribute's name is '" + 
                        newAttribute.getName() +
                        "'\nto match the case of an existing attribute.",
                        "Name Notification", JOptionPane.INFORMATION_MESSAGE);
                }
             }
             catch (CDFException e) {

                JOptionPane.showMessageDialog(contentPane,
                        "Error adding attribute " + attributeName + ": " + 
                        e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
             };
        }
    }
  

    private class AddEntryActionListener 
        implements ActionListener {

        public void actionPerformed(ActionEvent event) {

            Selection selection = null;

//System.err.println("begining AddEntryActionListener");
            try {

//System.err.println("getting selection");
                selection = getSelection();
//System.err.println("calling saveSelection");
                saveSelection(selection);
//System.err.println("returned from saveSelection");
            }
            catch (CDFException e) {

                e.printStackTrace();

                JOptionPane.showMessageDialog(contentPane,
                            "Error getting selection:" + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                return;
            };

//System.err.println("finished getting and saving the selection");

            if (selection == null || selection.getPath() == null) {

                JOptionPane.showMessageDialog(contentPane,
                    "You must select a global attributes to add the entry to",
                    "Missing Selection", JOptionPane.ERROR_MESSAGE);
                return;
            };

            if (selection.variableIsSelected()) {

                JOptionPane.showMessageDialog(contentPane,
                        "You may only create additional entries for\n" +
                        "global attriubes",
                        "Missing Selection", JOptionPane.ERROR_MESSAGE);
                return;
            }
            else if (selection.globalAttributeIsSelected()) { 

                addGlobalAttributeEntry(selection);
            }
            else {

                JOptionPane.showMessageDialog(contentPane,
                    "You must select a global attributes to add the entry to",
                    "Missing Selection", JOptionPane.ERROR_MESSAGE);
                return;
            };
        }
    

        protected void addGlobalAttributeEntry(Selection selection) {

//System.err.println("adding a global attribute");

            DefaultMutableTreeNode entryNode = 
                                createGlobalAttributeEntry(selection);

            if (entryNode == null) {

                return;
            };

            switch ((int)selection.getAttribute().getNumEntries()) {

            case 0:
            case 1:

                detailsLayout.show(detailsPanel, ATTRIBUTE_PANEL);

                break;

            default:

                TreePath path = selection.getPath();

                while (path.getPathCount() > 3) {

                    path = path.getParentPath();
                };

//System.err.println("setting path to new entry" + 
//                path.pathByAddingChild(entryNode));

                attributeTree.setSelectionPath(
                                      path.pathByAddingChild(entryNode));
                attributeTree.scrollPathToVisible(
                                      path.pathByAddingChild(entryNode));

//System.err.println("path set to new entry");
/* */ 
                break;
            };
        }
    }  


    protected DefaultMutableTreeNode createGlobalAttributeEntry(
                                                Selection selection) {

//System.err.println("creating a global attribute");
        long newEntryId = 0;
        long existingEntryId = 0;
        DefaultMutableTreeNode entryNode = null;
         
        Attribute selectedAttribute = selection.getAttribute();

        if (selectedAttribute == null) {

            JOptionPane.showMessageDialog(contentPane,
                                "You must select a global attribute",
                                "Missing selection", 
                                JOptionPane.ERROR_MESSAGE);
            return entryNode;
        };

        if (selection.getEntries() != null && 
            selection.getEntries().size() > 0) {

            try {

                Vector entries = selection.getAttribute().getEntries();
                Entry entry = (Entry)
                    entries.elementAt(entries.size() - 1);

                if (entry != null) {

                    existingEntryId = entry.getID();

                    newEntryId = existingEntryId + 1;
                }
            }
            catch (CDFException e) {

            }
        }

        try {

            for (;;newEntryId++) { // look for the next available ID
            
                selectedAttribute.getEntry(newEntryId);
            }
        }
        catch (CDFException e) {

            if (e.getCurrentStatus() != CDF.NO_SUCH_ENTRY) {

                JOptionPane.showMessageDialog(contentPane,
                                "Error adding entry: " + 
                                e.getMessage(), "Error", 
                                JOptionPane.ERROR_MESSAGE);
                return null;
            };
            //
            // found an unused ID so keep going
            //
//System.err.println("newEntryId = " + newEntryId + " is available");
        };

        try {

//System.err.println("creating new global attribute entry with id " +
//           newEntryId);

            Entry newEntry = Entry.create(selection.getAttribute(), 
                                    newEntryId, CDF.CDF_CHAR, " ");
            attributePanel.setEntry(selection.getAttribute().getName(), 
                                    newEntry);

//            textAttributePanel.setEntry(
//                                    selection.getAttribute().getName(), 
//                                    (int)newEntryId, newEntry);

            DefaultMutableTreeNode level2Node = 
                        (DefaultMutableTreeNode)
                               selection.getPath().getPathComponent(2);

            switch ((int)selection.getAttribute().getNumEntries()) {

            case 1:
                //
                // don't update the tree since we don't show a single entry
                //  in the tree but make the new entry visible on the 
                //   details panel
                //
                detailsLayout.show(detailsPanel, ATTRIBUTE_PANEL);

                break;

            case 2:

                //
                // add previously hidden single entry to tree
                //

//System.err.println("adding 0 entry node, existingEntryId = " + 
//                   existingEntryId);

                entryNode = new DefaultMutableTreeNode(
                                            new Long(existingEntryId));
                treeModel.insertNodeInto(entryNode, level2Node, 
                                         level2Node.getChildCount());
                // break intentionally missing

            default:

//System.err.println("adding " + newEntryId + " entry node");
                entryNode = new DefaultMutableTreeNode(
                                                 new Long(newEntryId));
                treeModel.insertNodeInto(entryNode, level2Node, 
                                         level2Node.getChildCount());

//                textAttributePanel.setEntries(
//                                 selection.getAttribute().getName(), 
//                                 selection.getAttribute().getEntries());
                break;
            };
        }
        catch (CDFException e) {

            JOptionPane.showMessageDialog(contentPane,
                                "Error adding entry: " + 
                                e.getMessage(), "Error", 
                                JOptionPane.ERROR_MESSAGE);
            return null;
        };

        return entryNode;
    }

/*
    private class AddTextEntriesActionListener 
        implements ActionListener {

        public void actionPerformed(ActionEvent event) {

            Selection selection = null;

//System.err.println("begining AddTextEntriesActionListener");
            try {

//System.err.println("getting selection");
                selection = getSelection();
//System.err.println("calling saveSelection");
                saveSelection(selection);
//System.err.println("returned from saveSelection");
            }
            catch (CDFException e) {

                e.printStackTrace();

                JOptionPane.showMessageDialog(contentPane,
                            "Error getting selection:" + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                return;
            };

            if (selection == null || selection.getPath() == null) {

                JOptionPane.showMessageDialog(contentPane,
                    "You must select a global attributes to add the entries to",
                    "Missing Selection", JOptionPane.ERROR_MESSAGE);
                return;
            };

            if (selection.variableIsSelected()) {

                JOptionPane.showMessageDialog(contentPane,
                        "You may only create additional entries for\n" +
                        "global attriubes",
                        "Missing Selection", JOptionPane.ERROR_MESSAGE);
                return;
            }
            else if (selection.globalAttributeIsSelected()) { 

                addGlobalTextEntries(selection);
            }
            else {

                JOptionPane.showMessageDialog(contentPane,
                    "You must select a global attributes to add the entries to",
                    "Missing Selection", JOptionPane.ERROR_MESSAGE);
                return;
            };
        };


        protected void addGlobalTextEntries(Selection selection) {

            if (selection.allEntriesAreChar()) {

                if (selection.getEntries().size() == 0) {

                    createGlobalAttributeEntry(selection);
                    createGlobalAttributeEntry(selection);

                }
                else if (selection.getEntries().size() == 1) {

                    createGlobalAttributeEntry(selection);
                };

//                detailsLayout.show(detailsPanel, 
//                                   TEXT_ATTRIBUTE_PANEL);
                detailsLayout.show(detailsPanel, 
                                   ATTRIBUTE_PANEL);
            }
            else {
                JOptionPane.showMessageDialog(contentPane,
                        "The selected attribute has non CDF_CHAR type entries.",
                        "Wrong Type", JOptionPane.ERROR_MESSAGE);
                return;
            };
        };
    };
*/


    private class DeleteAttributeActionListener 
        implements ActionListener {

        public void actionPerformed(ActionEvent event) {

            Selection selection = null;

            try {

                selection = getSelection();
            }
            catch (CDFException e) {

                e.printStackTrace();

                JOptionPane.showMessageDialog(contentPane,
                            "Error getting selection:" + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
            };

            if (selection == null) {

                JOptionPane.showMessageDialog(contentPane,
                        "You must select the attribute that is to be deleted",
                        "Missing Selection", JOptionPane.ERROR_MESSAGE);
                return;
            };

            TreePath path = selection.getPath();

            if (path == null) {

                JOptionPane.showMessageDialog(contentPane,
                        "You must select the attribute that is to be deleted",
                        "Missing Selection", JOptionPane.ERROR_MESSAGE);
                return;
            };

            while (path.getPathCount() > 2) {

                path = path.getParentPath();
            };

            if (selection.getAttribute() != null) {  

                Variable variable = selection.getVariable();

                if (variable == null) {  // a global attribute

                    int reply = JOptionPane.showConfirmDialog(contentPane,
                            "Delete '" + selection.attribute.getName() +
                            "' attribute and \n all its entries from the CDF?",
                            "Delete Confirmation",
                            JOptionPane.OK_CANCEL_OPTION);

                    if (reply == JOptionPane.YES_OPTION) {

                        try {

                            deleteInProgress = true;
                            selection.getAttribute().delete();
                            DefaultMutableTreeNode level2Node = 
                                                     (DefaultMutableTreeNode)
                                           selection.path.getPathComponent(2);

                            while (level2Node.getChildCount() > 0) {

                                level2Node.remove(level2Node.getChildCount() - 1);
                            };
                            treeModel.removeNodeFromParent(level2Node);
                            attributeTree.setSelectionPath(path);
                            attributeTree.scrollPathToVisible(path);
                        }
                        catch (CDFException e) {

                            JOptionPane.showMessageDialog(contentPane,
                                    "Error deleting attribute " + 
                                    selection.attribute.getName() + ": " + 
                                    e.getMessage(),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        finally {

                            deleteInProgress = false;
                        };
                    }; // endif reply == YES
                }
                else {  // a "variable attribute"

                    String msg =
                        "Note that this delete operation only " +
                        "deletes the attribute entry\nassociated " +
                        "with the selected variable.  It will not " +
                        "delete the\nattribute itself from the CDF.  " +
                        "Variable attributes cannot be\ndeleted " +
                        "by this application.  You may use another " +
                        "tool (e.g., cdfedit\nthat is included with " +
                        "CDF) to actually delete a variable " +
                        "attribute.\n\n" +
                        "Delete variable-attribute entry '" +
                        variable.getName() + "-" +
                        selection.attribute.getName() + "'?";
                                       // dialog message

                    int reply = JOptionPane.showConfirmDialog(
                        contentPane, msg, "Delete Confirmation",
                        JOptionPane.OK_CANCEL_OPTION);
                                       // confirmation dialog reply

                    if (reply == JOptionPane.YES_OPTION) {

                        try {

                            deleteInProgress = true;
                            selection.getAttribute().getEntry(variable).delete();
                            DefaultMutableTreeNode level2Node = 
                                          (DefaultMutableTreeNode)
                                           selection.path.getPathComponent(2);
                            treeModel.removeNodeFromParent(level2Node);
                            attributeTree.setSelectionPath(path);
                            attributeTree.scrollPathToVisible(path);
                        }
                        catch (CDFException e) {

                            JOptionPane.showMessageDialog(contentPane,
                                    "Error deleting attribute " + 
                                    selection.attribute.getName() + ": " + 
                                    e.getMessage(),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        finally {
                        
                            deleteInProgress = false;
                        };
                    }; // endif reply == YES
                };
            }
            else { // selected variable but not attribute

                JOptionPane.showMessageDialog(contentPane,
                        "You must select the attribute that is to be deleted",
                        "Missing Selection", JOptionPane.ERROR_MESSAGE);
                return;
            };
        }
    }

  
    private class DeleteEntryActionListener 
        implements ActionListener {

        public void actionPerformed(ActionEvent event) {

            Selection selection = null;

            try {

                selection = getSelection();
            }
            catch (CDFException e) {

                e.printStackTrace();

                JOptionPane.showMessageDialog(contentPane,
                            "Error getting selection:" + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
            };

            if (selection == null) {

                JOptionPane.showMessageDialog(contentPane,
                        "You must select the entry that is to be deleted",
                        "Missing Selection", JOptionPane.ERROR_MESSAGE);
                return;
            };

            TreePath path = selection.getPath();

            if (path == null) {

                JOptionPane.showMessageDialog(contentPane,
                        "You must select the entry that is to be deleted",
                        "Missing Selection", JOptionPane.ERROR_MESSAGE);
                return;
            };

            while (path.getPathCount() > 3) {

                path = path.getParentPath();
            };

            if (selection.getVariable() == null && 
                selection.getAttribute() != null) {

//System.err.println("deleting a global attribute entry");
                DefaultMutableTreeNode attributeNode = (DefaultMutableTreeNode)
                                        selection.getPath().getPathComponent(2);
                DefaultMutableTreeNode entryNode = null;

                if (selection.getPath().getPathCount() == 4) {

//System.err.println("a specific entry was selected");
                    entryNode = (DefaultMutableTreeNode)
                                       selection.getPath().getPathComponent(3);
                };
                Entry entry = null;

                if (selection.getEntries() != null && 
                    selection.getEntries().size() == 1) {

//System.err.println("got the specific entry ");
                    entry = (Entry)selection.getEntries().elementAt(0);
selection.getEntries().removeElementAt(0);
                }
                else if (selection.getAttribute().getNumEntries() == 1) {

                    try {

                        Vector entries = 
                            selection.getAttribute().getEntries();

                        for (int i = 0; i < entries.size(); i++) {

                            entry = (Entry)entries.elementAt(i);

                            if (entry != null) {

                                break;
                            }
                        }
                    }
                    catch (CDFException e) {

                        // nothing to do
                    }
                }
                else {

                    JOptionPane.showMessageDialog(contentPane,
                        "You must select the entry that is to be deleted",
                        "Missing Selection", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int reply = JOptionPane.showConfirmDialog(contentPane,
                        "Delete entry " + entry.getID() + " of attribute '" + 
                        selection.attribute.getName() +
                        "' from the CDF?",
                        "Delete Confirmation",
                        JOptionPane.OK_CANCEL_OPTION);

                if (reply == JOptionPane.YES_OPTION) {

                    try {

                        deleteInProgress = true;
//System.err.println("deleting entry with ID = " + entry.getID());
                        entry.delete();
selection.getAttribute().getEntries().removeElement(entry);
                    }
                    catch (CDFException e) {

                        JOptionPane.showMessageDialog(contentPane,
                                "Error deleting entry " + entry.getID() +
                                " of attribute " + 
                                selection.attribute.getName() + ": " + 
                                e.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    };
                
                    if (entryNode != null) {

//System.err.println("removing " + entryNode + " from tree");
                        treeModel.removeNodeFromParent(entryNode);
                    };

                    if (attributeNode.getChildCount() == 1) {

                        DefaultMutableTreeNode lastChildNode = 
                                    (DefaultMutableTreeNode)
                                            attributeNode.getChildAt(0);

                        Entry hiddenEntry = null;
try{
                        hiddenEntry = selection.getAttribute().getEntry(
                            ((Long)lastChildNode.getUserObject()).longValue());
}
catch(Exception e) {
e.printStackTrace();
}

//System.err.println("lastChildNode...entry = " + hiddenEntry);

//System.err.println("removing last child " + lastChildNode + " from tree");
                        treeModel.removeNodeFromParent(lastChildNode);


                        attributePanel.setEntry(
                                          selection.getAttribute().getName(),
                                          hiddenEntry);

                        detailsLayout.show(detailsPanel, ATTRIBUTE_PANEL);
                    };
//                    detailsLayout.show(detailsPanel, BLANK_PANEL);
//                    attributeTree.setSelectionPath(path.getParentPath());
//                    attributeTree.scrollPathToVisible(path.getParentPath());
                    deleteInProgress = false;
                }; // endif reply == YES
            }
            else {

                JOptionPane.showMessageDialog(contentPane,
                        "You must select the entry that is to be deleted",
                        "Missing Selection", JOptionPane.ERROR_MESSAGE);
                return;
            };
        }
    }


    private class CopyAttributeActionListener 
        implements ActionListener {

        public void actionPerformed(
            ActionEvent event) {

            Selection selection = null;

            try {

                selection = getSelection();
            }
            catch (CDFException e) {

                e.printStackTrace();

                JOptionPane.showMessageDialog(contentPane,
                            "Error getting selection:" + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
            };

            if (selection == null) {

                JOptionPane.showMessageDialog(contentPane,
                    "You must select the attribute that is to be copied",
                    "Missing Selection", JOptionPane.ERROR_MESSAGE);
                return;
            };

            TreePath path = selection.getPath();

            if (path == null) {

                JOptionPane.showMessageDialog(contentPane,
                    "You must select the attribute that is to be copied",
                    "Missing Selection", JOptionPane.ERROR_MESSAGE);
                return;
            };

            while (path.getPathCount() > 3) {

                path = path.getParentPath();
            };

            Variable selectedVar = selection.getVariable();
            Attribute selectedAttr = selection.getAttribute();
            Vector selectedEntries = selection.getEntries();

            if (selectedEntries.size() != 1) {

                JOptionPane.showMessageDialog(contentPane,
                    "You must select the attribute entry that is " +
                    "to be copied",
                    "Missing Selection", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Entry selectedEntry = (Entry)selectedEntries.get(0);

            CopyAttributeDialog copyDialog = 
                new CopyAttributeDialog (
                        contentPane, "Copy Attribute Dialog",
                        selectedAttr, 
                        selectedEntry,
                        cdf.getVariables());

            CopyAttributeDialog.Target target = 
                copyDialog.showDialog();

            if (target != null) {

                try {

                    copyValue(selectedAttr, selectedEntry, target);
                }
                catch (CDFException e) {

                    JOptionPane.showMessageDialog(contentPane,
                        "CDF error while attempting to copy " +
                        "attribute value:\n" + e.getMessage(),
                        "CDF Error", JOptionPane.ERROR_MESSAGE);

                }
            }
        }

        private void copyValue(
            Attribute attr,
            Entry entry, 
            CopyAttributeDialog.Target target) 
        throws CDFException {

            CDF cdf = attr.getMyCDF();
            long entryType = entry.getDataType();
            Object entryData = entry.getData();
            Attribute targetAttr = null;
            try {

                targetAttr = cdf.getAttribute(target.attributeName);
            }
            catch (CDFException e) {

                if (e.getCurrentStatus() == CDF.NO_SUCH_ATTR) {

                    targetAttr = 
                        Attribute.create(cdf, target.attributeName,
                            CDF.VARIABLE_SCOPE);
                }
                else {

                    throw e;
                }
            }

            boolean overWriteAll = false;

            for (Variable var : target.variables) {

                Entry targetEntry = null;
                try {

                    targetEntry = targetAttr.getEntry(var);

                    Object targetEntryData = targetEntry.getData();

                    if (!overWriteAll && targetEntryData != null) {

                        // display confirmation dialog
                        String msg = "Variable " + var.getName() +
                            "'s " + targetAttr.getName() + 
                            " attribute has a value of\n'" +
                            targetEntryData + 
                            "'.\nDo you want to over write it?";

                        String[] options = {
                            "Yes", "No", "Yes to all"
                        };

                        int reply = JOptionPane.showOptionDialog(
                            contentPane, msg, "Delete Confirmation",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null, options, options[0]);

                        switch (reply) {

                        case 1:

                            break;
                        case 2:

                            overWriteAll = true;
                            // fall through
                        case 0:

                            targetEntry.putData(entryType, entryData);
                            break;
                        }
                    }
                }
                catch (CDFException e) {

                    if (e.getCurrentStatus() == CDF.NO_SUCH_ENTRY) {

                        targetEntry = 
                            Entry.create(targetAttr, var.getID(),
                                entryType, entryData);

                        addAttributeToTree(var, targetAttr);
                    }
                    else {

                        throw e;
                    }
                }
            }
        }
        
    }


    private JMenuBar createMenuBar() {

        JMenuBar menuBar = new JMenuBar();

        menuBar.add(createWindowMenu());
        menuBar.add(createEditMenu());

//        menuBar.add(createHelpMenu());

        return menuBar;
    }

    private JMenu createWindowMenu() {

        JMenu windowMenu = new JMenu("Window");

        windowMenu.setMnemonic('W');
        JMenuItem closeMenuItem = new JMenuItem("Close");
        closeMenuItem.setToolTipText("Close window");
        closeMenuItem.setMnemonic(KeyEvent.VK_C);
        closeMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {

                try {

                    saveChanges();
                }
                catch (CDFException e) {

                    e.printStackTrace();

                    JOptionPane.showMessageDialog(contentPane,
                            "Error saving most recent changes:" + 
                            e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                };
                //CdfEditor.this.setVisible(false);
                dispose();
            }
        });
        windowMenu.add(closeMenuItem);

        return windowMenu;
    }

    private JMenu createEditMenu() {

        JMenu editMenu = new EditMenu(this);

	editMenu.add(new JSeparator());

        JMenu addNewMenu = new JMenu("Add new");
        addNewMenu.setMnemonic('A');
  
        JMenuItem addAttributeMenuItem = new JMenuItem("Attribute");
        addAttributeMenuItem.setMnemonic('t');
        addAttributeMenuItem.setToolTipText(
                                   "Add new global or variable attribute");
        addAttributeMenuItem.addActionListener(
                                 new AddAttributeActionListener());
        addNewMenu.add(addAttributeMenuItem);

        JMenuItem addEntryMenuItem = new JMenuItem("Global Attribute Entry");
        addEntryMenuItem.setMnemonic('E');
        addEntryMenuItem.setToolTipText("Add new global attribute entry");
        addEntryMenuItem.addActionListener(new AddEntryActionListener());
        addNewMenu.add(addEntryMenuItem);

/*
        JMenuItem addTextEntryMenuItem = new JMenuItem(
                                           "Global Text Attribute Entries");
//        addTextEntryMenuItem.setMnemonic('E');
        addTextEntryMenuItem.setToolTipText(
                                    "Add new global text attribute entries");
        addTextEntryMenuItem.addActionListener(
                                     new AddTextEntriesActionListener());
        addNewMenu.add(addTextEntryMenuItem);
*/

        editMenu.add(addNewMenu);


        JMenu deleteMenu = new JMenu("Delete");
        deleteMenu.setMnemonic('D');

        JMenuItem deleteAttributeMenuItem = new JMenuItem("Attribute");
        deleteAttributeMenuItem.setMnemonic('A');
        deleteAttributeMenuItem.setToolTipText(
                                    "Delete global or variable attribute");
        deleteAttributeMenuItem.addActionListener(
                                   new DeleteAttributeActionListener());
        deleteMenu.add(deleteAttributeMenuItem);

        JMenuItem deleteEntryMenuItem = new JMenuItem("Global Attribute Entry");
        deleteEntryMenuItem.setMnemonic('E');
        deleteEntryMenuItem.setToolTipText("Delete global attribute entry");
        deleteEntryMenuItem.addActionListener(
                                   new DeleteEntryActionListener());
// deleteEntryMenuItem.setEnabled(false);
        deleteMenu.add(deleteEntryMenuItem);
        editMenu.add(deleteMenu);

/* ??? */
	editMenu.add(new JSeparator());

        JMenuItem copyAttributeMenuItem = 
            new JMenuItem("Copy attribute value...");
//        copyAttributeMenuItem.setMnemonic('t');
        copyAttributeMenuItem.setToolTipText(
            "Copy attribute value to");

        copyAttributeMenuItem.addActionListener(
            new CopyAttributeActionListener());
        editMenu.add(copyAttributeMenuItem);
/* ??? */
        return editMenu;
    }

    private JMenu createHelpMenu() {

        JMenu helpMenu = new JMenu("Help");

        helpMenu.setMnemonic('H');
        helpMenu.add("Overview");
        helpMenu.add("Table of Contents");
        helpMenu.add("Tasks");
        helpMenu.add("Reference");

        return helpMenu;
    }

}
