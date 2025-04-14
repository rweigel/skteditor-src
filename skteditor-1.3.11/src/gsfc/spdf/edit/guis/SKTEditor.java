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
 * $Id: SKTEditor.java,v 1.233 2025/01/23 18:18:16 btharris Exp $
 */
package gsfc.spdf.edit.guis;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.border.*;
import javax.swing.filechooser.*;
import javax.swing.plaf.FileChooserUI;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;
import java.io.*;
import java.applet.*;
import java.net.*;
import java.text.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import gsfc.nssdc.cdf.*;
import gsfc.nssdc.cdf.util.Epoch;
import gsfc.nssdc.cdf.util.Epoch16;
import gsfc.nssdc.cdf.util.CDFUtils;

import gsfc.spdf.cdf.tools.CDFTools;
import gsfc.spdf.cdf.Cdf;

import gsfc.spdf.istp.DefaultVerificationCallback;
import gsfc.spdf.istp.ISTPCompliance;
import gsfc.spdf.istp.ISTPComplianceException;
import gsfc.spdf.istp.Filename;
import gsfc.spdf.istp.FillvalAttribute;
import gsfc.spdf.istp.GlobalAttribute;
import gsfc.spdf.istp.TerrestrialTime2000;
import gsfc.spdf.istp.VerificationResult;
import gsfc.spdf.util.History;
import gsfc.spdf.gui.*;
import gsfc.spdf.io.OutputStreamMulticaster;

import gsfc.spdf.cdf.Cdf;
import gsfc.spdf.edit.events.*;
import gsfc.spdf.edit.filechooser.*;
import gsfc.spdf.edit.util.*;
import javax.swing.plaf.basic.*;

import apple.dts.samplecode.osxadapter.OSXAdapter;

/* begin cdf validation */
import gov.nasa.gsfc.spdf.cdf.validation.CdfValidationException;
import gov.nasa.gsfc.spdf.cdf.validation.Validator;
/* end cdf validation */

/**
 */
public class SKTEditor 
    extends JPanel 
    implements CDFConstants, 
               FileSelectedListener,
               InvocationHandler {

    // This
    public static SKTEditor edit;
    
    // The ISTP object
    public CDF theCDF;
    public File theFile;
    private String filename;
    private String rootfilename;
    private String logicalFilename;
    private String ext;
    private String workingRootFilename;
    private boolean saveAsOnClose = false;

    /**
     * ISTP compliance warnings that are to be suppressed.
     */
    private EnumSet<ISTPCompliance.Warnings> suppressedWarnings = 
        EnumSet.noneOf(ISTPCompliance.Warnings.class);

    /**
     * History of CDF files that have been opened.
     */
    private History cdfHistory = new History("cdf", 10);

    /**
     * User's persistent preferences.
     */
    private Preferences preferences =
        Preferences.userNodeForPackage(SKTEditor.class);
    
    // SKTEditor Properties
    public static Properties istpProps;
    
    // The Frame
    static JFrame frame;
    
    // Current ui
    public String currentUI = "Metal";
    
    // Application Properties
    public static String propertyPath = null;
    static String separator = null;
    public static ApplicationProperties defaultProperties;
    public static ApplicationProperties appProperties;
    public static String chooserCurrentDir = "";
    // JavaDoc URL
//    public static String javaDocPath = "http://spdf";
    
    // A Yes/No Vector
    public final static Vector<String> yesNo = new Vector<>();
    
    // Update the logical_source etc?
    public boolean updateLogicalDescription = false;
    
    // The width and height of the frame
  //  public static int WIDTH = 665;
  //  public static int HEIGHT = 725;
   public static final int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width<1200 ? Toolkit.getDefaultToolkit().getScreenSize().width:1200;
                                                                                          
    public static final int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height<1052 ? Toolkit.getDefaultToolkit().getScreenSize().height:1052;
 
    public final static int INITIAL_WIDTH = 300;
    public final static int INITIAL_HEIGHT = 125;
    
    public final static Dimension hpad5 = new Dimension(5,1);
    public final static Dimension hpad10 = new Dimension(10,1);
    public final static Dimension hpad20 = new Dimension(20,1);
    public final static Dimension hpad25 = new Dimension(25,1);
    public final static Dimension hpad30 = new Dimension(30,1);
    public final static Dimension hpad40 = new Dimension(40,1);
    public final static Dimension hpad80 = new Dimension(80,1);
    
    public final static Dimension vpad5 = new Dimension(1,5);
    public final static Dimension vpad10 = new Dimension(1,10);
    public final static Dimension vpad20 = new Dimension(1,20);
    public final static Dimension vpad25 = new Dimension(1,25);
    public final static Dimension vpad30 = new Dimension(1,30);
    public final static Dimension vpad40 = new Dimension(1,40);
    public final static Dimension vpad80 = new Dimension(1,80);
    
    public final static Insets insets0 = new Insets(0,0,0,0);
    public final static Insets insets2 = new Insets(2,2,2,2);
    public final static Insets insets5 = new Insets(5,5,5,5);
    public final static Insets insets10 = new Insets(10,10,10,10);
    public final static Insets insets15 = new Insets(15,15,15,15);
    public final static Insets insets20 = new Insets(20,20,20,20);
    
    public final static Border emptyBorder0 = new EmptyBorder(0,0,0,0);
    public final static Border emptyBorder2 = new EmptyBorder(2,2,2,2);
    public final static Border emptyBorder5 = new EmptyBorder(5,5,5,5);
    public final static Border emptyBorder10 = new EmptyBorder(10,10,10,10);
    public final static Border emptyBorder15 = new EmptyBorder(15,15,15,15);
    public final static Border emptyBorder20 = new EmptyBorder(20,20,20,20);
    
    public final static Border etchedBorder2 = new CompoundBorder(
    new EtchedBorder(),
    emptyBorder2);
    public final static Border etchedBorder5 = new CompoundBorder(
    new EtchedBorder(),
    emptyBorder5);
    public final static Border etchedBorder10 = new CompoundBorder(
    new EtchedBorder(),
    emptyBorder10);
    
    public final static Border raisedBorder = new BevelBorder(BevelBorder.RAISED);
    public final static Border lightLoweredBorder = new BevelBorder(BevelBorder.LOWERED,
    Color.white, Color.gray);
    public final static Border loweredBorder = new SoftBevelBorder(BevelBorder.LOWERED);
    
    
    public Font defaultFont = new Font("Dialog", Font.PLAIN, 12);
    public Font boldFont = new Font("Dialog", Font.BOLD, 12);
    public Font bigFont = new Font("Dialog", Font.PLAIN, 18);
    public Font bigBoldFont = new Font("Dialog", Font.BOLD, 18);
    public Font reallyBigFont = new Font("Dialog", Font.PLAIN, 18);
    public Font reallyBigBoldFont = new Font("Dialog", Font.BOLD, 24);
    
    // Some images used in the demo
    public ImageIcon spdfLogo;
    public ImageIcon ssdooLogo;
    public ImageIcon gsfcLogo;
    
    // The panels used in the demo
    public JPanel        helpPanel;
    public GlobalPanel   globalPanel;
    private CdfEditor cdfEditor;
/* begin cdf validation */
    private Validator cdfValidator = null;
/* end cdf validation */
    public VariablePanel variablePanel;
    public InfoPanel     logoPanel;
    static StatusPanel     statuspanel;
    
    // Track build progress
    static int totalPanels = 6;
    static int currentProgressValue;
    static JLabel progressLabel = null;
    static JProgressBar progressBar = null;
    
    // Some components
    public JTabbedPane tabbedPane;
    private JFileChooser chooser;
    protected CheckVariableAction checkAction;
    protected EditSpecAction editAction;
    /**
     * Editor exit action.
     */
    protected ExitAction exitAction = null;
    
    private JOptionPane       optionPane;
    private JDialog           optionDialog;
    private  JMenuItem save, saveAs, close;
    private  JMenuItem deleteDataValuesMenuItem;
    private  JMenuItem extractNotesMenuItem;
    private  JMenuItem extractAttributesMenuItem;
    private  JMenuItem cdfEditorMenuItem;
    private  JMenuItem defaultFillMenuItem;
    private  JMenuItem varFillMenuItem;
    private JMenuItem validateFileMenuItem;
    private  JMenu complianceChecksMenu;
    private  JMenu file;
    private  JMenu editMenu;
    private  JMenu helpMenu;
    private  JMenu tools;
    private  JMenu variableMenu;
    private  JMenuBar menuBar;
    // This != null if we are an applet
    private static Dimension screenSize;
    
    java.applet.Applet applet;
    static SKTEditor instance;
    
    public SKTEditor() {
        this(null);
    }
    
    public EnumSet<ISTPCompliance.Warnings> getSuppressedWarnings() {

        return suppressedWarnings.clone();
    }

    private JFileChooser createFileChooser() {

        chooser = new JFileChooser();  // file chooser to use
        
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileHidingEnabled(true);

/*  
    Java > 9 dislikes the following reflection code to retain the
    selected file through a file filter change.  I don't see how to
    avoid the reflection since macOS uses 
    com.apple.laf.AquaFileChooserUI. But is this code even necessary?
    It doesn't seem to be on Linux.

        chooser.addPropertyChangeListener(
            JFileChooser.FILE_FILTER_CHANGED_PROPERTY,
            new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent event) {

                    javax.swing.filechooser.FileFilter newFilter = 
                        (javax.swing.filechooser.FileFilter)
                            event.getNewValue();

                    if (newFilter instanceof FileNameExtensionFilter) {

                        FileNameExtensionFilter newExtFilter = 
                            (FileNameExtensionFilter)newFilter;
                        String[] extensions = 
                            newExtFilter.getExtensions();
                        JFileChooser fileChooser = 
                            (JFileChooser)event.getSource();

                        String currentFilename = null;
                        FileChooserUI fileChooserUI = 
                            fileChooser.getUI();
                        //
                        // OS X uses a com.apple.laf.AquaFileChooserUI 
                        // so we cannot simply down cast fileChooserUI
                        try {

                            Method getFileNameMethod = 
                                fileChooserUI.getClass().
                                    getDeclaredMethod("getFileName");

                            currentFilename = (String)
                                getFileNameMethod.invoke(fileChooserUI);
                        }
                        catch (NoSuchMethodException e) {

                            System.err.println(
                                "fileChooserUI (" +
                                fileChooserUI.getClass().getName() +
                                ") has no getFileName method.");
                        }
                        catch (IllegalAccessException e) {

                            System.err.println(
                                "Not able to call " +
                                "fileChooserUI.getFileName().");
                        }
                        catch (InvocationTargetException e) {

                            System.err.println("Exception (" +
                                e.getMessage() + ") while calling " +
                                "fileChooserUI.getFileName().");
                        }

                        if (currentFilename != null) {

                            int extIndex = 
                                currentFilename.lastIndexOf(".");

                            if (extIndex > 0) {

                                currentFilename =
                                    currentFilename.substring(
                                        0, extIndex + 1);

                                fileChooser.setSelectedFile(
                                    new File(currentFilename + 
                                                 extensions[0]));
                            }
                        }
                    }
                }
            });
*/

        FileNameExtensionFilter cdfFilter =
            new FileNameExtensionFilter("Common Data Format Files", 
                    "cdf");
        FileNameExtensionFilter sktFilter =
            new FileNameExtensionFilter("Skeleton CDF Files", "skt");
        FileNameExtensionFilter noNrvSktFilter =
            new FileNameExtensionFilter("No NRV Skeleton CDF Files", 
                    "nonrvskt");
        FileNameExtensionFilter ncFilter =
            new FileNameExtensionFilter("netCDF Files", "nc");

        ArrayList<String> fileTypes = new ArrayList<String>();
                                       // supported file types
        fileTypes.add("cdf");
        chooser.addChoosableFileFilter(cdfFilter);

        if (CDFTools.skeletonToolsInstalled()) {

            fileTypes.add("skt");
            chooser.addChoosableFileFilter(sktFilter);
            fileTypes.add("nonrvskt");
            chooser.addChoosableFileFilter(noNrvSktFilter);
        }
        if (CDFTools.translationToolsInstalled()) {

            fileTypes.add("nc");
            chooser.addChoosableFileFilter(ncFilter);
        }

        String fileIconPath = propertyPath + 
            appProperties.getProperty("image.path");
                                       // file icon path

        ExampleFileView fileView = new ExampleFileView();
        
        for (String fileType : fileTypes) {

            String iconPath = fileIconPath + 
                appProperties.getProperty("logo." + fileType + "_file");
                                       // full file icon path
            try {
                    
                fileView.putIcon(fileType,
                    new ImageIcon(
                        SKTEditor.class.getResource(iconPath)));
            }
            catch(NullPointerException e) {
                    
                System.err.println("Cannot find icon " + iconPath +
                    " -- continuing without it");
            }
        }
        
        chooser.setFileFilter(cdfFilter);
        chooser.setFileView(fileView);                

        return chooser;
    }

    
    public SKTEditor(java.applet.Applet anApplet) {
        
        super(true); // double buffer
                        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            
            public void run() {
                
                updateStatus(++currentProgressValue,"Loading, please wait..." );
        
                frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));        
                frame.setVisible(true);               
            }
        });
        
        instance = this;
        applet = anApplet;
        edit = this;
        setName("Main SKTEditor Panel");
        setFont(bigFont);
        setLayout(new BorderLayout());
        currentProgressValue = 0;
        checkAction = new CheckVariableAction( this );
        exitAction = new ExitAction(this);
        
        frame.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {                

                exit();
            }
        });
        
        Thread worker = new Thread() {
            
            public void run() {  
                
                yesNo.addElement("Yes");
                yesNo.addElement("No");
                
                System.err.println("Building file chooser...");
                     
                chooser = createFileChooser();
            }                             
        };
                
        worker.start();
        
        // Build a tab pane
 
        System.err.println("Building JTabbedPane...");
        tabbedPane = new JTabbedPane();
        
        // Add the tab to the center
        add(tabbedPane, BorderLayout.CENTER);
        
        
        // Add the Title Page
    //    progressLabel.setText("Loading Title Page...");
        
        System.err.println("Building InfoPanel...");
        logoPanel = new InfoPanel(this);
        tabbedPane.addTab("Information", logoPanel);
        tabbedPane.setSelectedIndex(0);
        updateStatus(++currentProgressValue,"Loading Title Page..." );
        
        // Set the locale globally to English(US) to avoid problems
        //with CDF time encoding
        
        Locale list[] = DateFormat.getAvailableLocales();
        for (int i = 0; i < list.length; i++) 
        {        
            if(list[i].toString().equalsIgnoreCase("en_US"))
            {
                Locale.setDefault(list[i]);
                break;
            }
        }
        if (!Locale.getDefault().toString().equalsIgnoreCase("en_US"))
        {
            System.err.println("!!Could not set the Locale.!!");
        }
                                                                              
        // Global Pane
         updateStatus(++currentProgressValue,"Loading ISTP Global Attribute Panel..." );
      //  progressLabel.setText("Loading ISTP Global Attribute Panel...");
        System.err.println("Building GlobalPanel...");
                
        globalPanel = new GlobalPanel(this);
        tabbedPane.addTab("ISTP Global Attributes", null, globalPanel);
        updateStatus(++currentProgressValue,"Loading Variable Panel..." );
        
        // Variable Pane
       // progressLabel.setText("Loading Variable Panel...");
        System.err.println("Building VariablePanel...");
        variablePanel = new VariablePanel(this);
        tabbedPane.addTab("Variables", null, variablePanel);
        updateStatus(++currentProgressValue,"Loading Message Panel...");
        
        // Message Pane
    //    progressLabel.setText("Loading Message Panel...");
        System.err.println("Building StatusPanel...");
        statuspanel = new StatusPanel(getFrame());
        add(statuspanel, BorderLayout.SOUTH);
        PrintStream ps = statuspanel.getPrintStream();
        if (ps != null)
            System.setOut(ps);
        updateStatus(++currentProgressValue,"Loading Attributes Editor..." );
        
        createTabListener();
        
       
        //
        // Global Attributes Editor
        //
   //     progressLabel.setText("Loading Attributes Editor...");
        System.err.println("Building CDF Editor...");
        cdfEditor = new CdfEditor(frame, "Attribute Editor", true);

        cdfEditor.addWindowListener(new WindowAdapter() {

            public void windowClosed(WindowEvent e) {

                globalPanel.setGlobalAttributes();
                Variable selectedVar = variablePanel.getSelectedVar();
                    
                if (selectedVar != null) {
                    variablePanel.updateVarPanel(selectedVar);
                };
            }

/*  This doesn't work when a showMessageDialog (e.g., "Invalid value...")
    occurs during a close because isShowing returns true.  The above
    windowClosed seems more appropriate

            public void windowDeactivated(WindowEvent e) {
                if (!e.getWindow().isShowing()) {
                    //
                    // when modal window is actually closed (not visible)
                    //
                    globalPanel.setGlobalAttributes();
                    Variable selectedVar = variablePanel.getSelectedVar();
                    
                    if (selectedVar != null) {
                        variablePanel.updateVarPanel(selectedVar);
                    };
                };
            }
*/
        });

/* begin cdf validation */
        try {

            cdfValidator = createCdfValidator();
        }
        catch (CdfValidationException e) {

            System.err.println("Error: " + e.getMessage());
            System.err.println("CDF validation function is disabled.");
        }
/* end cdf validation */

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            
            public void run() {
                
            frame.setJMenuBar(buildMenuBar());
            }
        });                
    }


    JMenuBar buildMenuBar() {
        // MenuBar
        menuBar = new JMenuBar();
        
        JMenuItem mi;
        
        // File Menu
        file = (JMenu) menuBar.add(new JMenu("File"));
       file.setMnemonic('F');
        
        mi = (JMenuItem) file.add(new NewFileAction( this ));
        mi.setToolTipText("Open a new file");
        mi.setMnemonic(KeyEvent.VK_N);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
        Event.CTRL_MASK));
        
        mi = (JMenuItem) file.add(new OpenFileAction( this ));
        mi.setToolTipText("Open an existing file");
        mi.setMnemonic(KeyEvent.VK_O);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
        Event.CTRL_MASK));
        
        mi = file.add(createRecentCdfMenu());

        file.add(new JSeparator());
      
        
        save = (JMenuItem) file.add(new SaveAction( this ));
        save.setToolTipText("Save the file");
        save.setMnemonic(KeyEvent.VK_S);
        save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
        Event.CTRL_MASK));
        save.setEnabled(false);
        
        saveAs = (JMenuItem) file.add(new SaveAsAction( this ));
        saveAs.setToolTipText("Save the file w/ a new name");
        saveAs.setMnemonic('A');
        saveAs.setEnabled(false);
        
        file.add(new JSeparator());
        
        close = (JMenuItem) file.add(new CloseAction( this ));
        close.setToolTipText("Close the file w/o saving");
        close.setMnemonic(KeyEvent.VK_C);
        close.setEnabled(false);
        
mi = (JMenuItem) file.add(exitAction);
//        mi = (JMenuItem) file.add(new ExitAction( this ));
        mi.setToolTipText("Save changes and exit SKTEditor");
        mi.setMnemonic(KeyEvent.VK_X);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
        Event.CTRL_MASK));
        // add the edit menu to the menuBar
        editMenu = new EditMenu(getFrame());
        
        // add the help menu to the menuBar
        helpMenu = (HelpMenu) menuBar.add(new HelpMenu());
        
        tools = new JMenu("Tools");
        tools.setMnemonic('T');
        
        mi = (JMenuItem) tools.add(createComplianceCheckMenu());

/* begin cdf validation */
        if (cdfValidator != null) {

            tools.add(createValidationMenu());
        }
/* end cdf validation */
        
        cdfEditorMenuItem = new JMenuItem("Attributes Editor");
        cdfEditorMenuItem.setToolTipText("Edit global and variable attributes");
        cdfEditorMenuItem.setMnemonic(KeyEvent.VK_E);
        cdfEditorMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                
                try {
                    
                    globalPanel.saveGlobalAttributes();
                }
                catch (CDFException e) {
                    
                    JOptionPane.showMessageDialog(frame,
                    "Error saving global attributes: " +
                    e.getMessage(),
                    "CDF Error",
                    JOptionPane.ERROR_MESSAGE);
                    return;
                };
                variablePanel.saveVariableChanges();
                cdfEditor.setCdf(theCDF);
                cdfEditor.setVisible(true);

            }
        });
        tools.add(cdfEditorMenuItem);
        tools.addSeparator();
        
        defaultFillMenuItem = new JMenuItem("Reset All Fill Values");
        defaultFillMenuItem.setToolTipText("Replace all fill values with default values");
        defaultFillMenuItem.setMnemonic(KeyEvent.VK_R);
        defaultFillMenuItem.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent event) {
                int reply = JOptionPane.showConfirmDialog(frame.getContentPane(),
                "Are you sure that you want to reset \n FILLVAL for all variables. ",
                "Confirm Fillval Reset",
                JOptionPane.YES_NO_OPTION);
                
                if (reply == JOptionPane.YES_OPTION) {
                    try{
                        if (theCDF != null) {
                            for (Enumeration e = theCDF.getVariables().elements() ;
                            e.hasMoreElements() ; ) {
                                gsfc.spdf.istp.Variable v = 
                                        new gsfc.spdf.istp.Variable((Variable)e.nextElement());  
                                v.setDefaultFillval();
                              //  SKTUtils.setFillval((gsfc.spdf.istp.(Variable)e.nextElement());
                            }
                        }
                    }catch (Exception e) {
                        System.out.println(" ");
                        System.out.println("Error resetting fill value, " +
                        " some variables have not been changed");
                        return;
                    };
                    System.out.println(" ");
                    System.out.println("All fill values have been successfully reset.");
                };
            }
        });
        tools.add(defaultFillMenuItem);
        
        deleteDataValuesMenuItem = 
            new JMenuItem("Delete All Data Values");
        deleteDataValuesMenuItem.setToolTipText(
            "Delete all data values from the CDF file");
        deleteDataValuesMenuItem.setMnemonic(KeyEvent.VK_D);
        deleteDataValuesMenuItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {

                    deleteAllDataValues();
                }
            });
        tools.add(deleteDataValuesMenuItem);
        
        extractNotesMenuItem = new JMenuItem("Extract Notes");
        extractNotesMenuItem.setToolTipText("Extract notes from a CDF file");
        extractNotesMenuItem.setMnemonic(KeyEvent.VK_N);
        extractNotesMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                
                extractNotes();
            }
        });
        tools.add(extractNotesMenuItem);
        
        extractAttributesMenuItem = new JMenuItem("Extract Attributes");
        extractAttributesMenuItem.setToolTipText(
        "Extract attributes from a CDF file");
        extractAttributesMenuItem.setMnemonic(KeyEvent.VK_A);
        extractAttributesMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                
                extractAttributes();
            }
        });
        tools.add(extractAttributesMenuItem);
        
        variableMenu = new JMenu("Variables");
        
        variableMenu.setMnemonic('V');
        
        mi = (JMenuItem) variableMenu.add(new NewVariableAction( this ));
        mi.setMnemonic('N');
        mi.setEnabled(false);
        
        mi = (JMenuItem) variableMenu.add(new NewVirtualVariableAction( this ));
        
        mi = (JMenuItem) variableMenu.add(new CopyVariableAction( this ));
        mi.setMnemonic('C');
        mi.setEnabled(false);
        
        mi = (JMenuItem) variableMenu.add(new DeleteVariableAction( this ));
        mi.setMnemonic('D');
        mi.setEnabled(false);
        
        mi = (JMenuItem) variableMenu.add(new RenameVariableAction( this ));
        mi.setMnemonic('R');
        mi.setEnabled(false);
        
        variableMenu.add(new JSeparator());
        
        
        varFillMenuItem = new JMenuItem("Reset Fill Value");
        varFillMenuItem.setToolTipText("Replace this variable fill value with default value");
        varFillMenuItem.setMnemonic(KeyEvent.VK_R);
        varFillMenuItem.addActionListener(new ResetFillvalListener());
                
                variableMenu.add(varFillMenuItem);
            
            mi = (JMenuItem) variableMenu.add(checkAction);
            mi.setMnemonic('k');
            mi.setEnabled(false);
            
            editAction = new EditSpecAction( this );
            mi = (JMenuItem) variableMenu.add(editAction);
            mi.setMnemonic('E');
            mi.setEnabled(false);
            
        if (System.getProperty("os.name").
                toLowerCase().startsWith("mac os x")) {

            registerForMacOsXEvents();
        }
            
        return menuBar;
    }
     

    /**
     * Registers methods of this class to handle OS X specific events.
     */
    public void registerForMacOsXEvents() {

        try {

            Class<?> aboutHandlerClass = 
                Class.forName("java.awt.desktop.AboutHandler");
            Method setAboutMethod = 
                Desktop.class.getDeclaredMethod(
                    "setAboutHandler",
                    new Class<?>[] {aboutHandlerClass});

            Class<?> quitHandlerClass = 
                Class.forName("java.awt.desktop.QuitHandler");
            Method setQuitMethod = 
                Desktop.class.getDeclaredMethod(
                    "setQuitHandler",
                    new Class<?>[] {quitHandlerClass});

            Object handlerProxy = 
                Proxy.newProxyInstance(
                    SKTEditor.class.getClassLoader(),
                    new Class<?>[] {
                        aboutHandlerClass, quitHandlerClass
                    },
                    this);

            Desktop desktop = Desktop.getDesktop();

            setAboutMethod.invoke(desktop, handlerProxy);
            setQuitMethod.invoke(desktop, handlerProxy);

            return;
        }
        catch (ClassNotFoundException e) {

            // Java < 9.  Continue with code below for older Java.
        }
        catch (NoSuchMethodException e) {

            e.printStackTrace();
        }
        catch (IllegalAccessException e) {

            e.printStackTrace();
        }
        catch (InvocationTargetException e) {

            e.printStackTrace();
        }

        //
        // Do the following for Java < 9.  This can be eliminated
        // and the above reflection code rewritten without reflection
        // when Java < 9 is no longer supported.
        //
        try {

            OSXAdapter.setQuitHandler(this, 
                getClass().getDeclaredMethod("macOsXQuit"));
            OSXAdapter.setAboutHandler(this,
                getClass().getDeclaredMethod("macOsXAbout"));
        }
        catch (NoSuchMethodException e) {

            System.err.println("SKTEditor.registerForMacOsXEvents: " +
                "NoSuchMethodException: " + e.getMessage());
        }
    }


    /**
     * Processes a Desktop (Java 9 and later) method invocation for 
     * About and Quit.
     *
     * @param proxy the proxy instance that the method was invoked on.
     * @param method the Method instance corresponding to the interface
     *     method invoked on the proxy instance.
     * @param args an array of objects containing the values of the 
     *     arguments passed in the method invocation on the proxy 
     *     instance, or null if interface method takes no arguments.
     * @return the value to return from the method invocation on the 
     *     proxy instance.
     */
    public Object invoke(
        Object proxy, 
        Method method, 
        Object[] args) {

        if (method.getName().equals("handleAbout")) {

            macOsXAbout();
        }
        else if (method.getName().equals("handleQuitRequestWith")) {

            macOsXQuit();
        }
        else {

            System.err.println("SKTEditor.invoke: was called for " + 
                method.getName());
        }
        return null;
    }


    /**
     * Handles OS X Quit event.
     *
     * @return true.  Never actually returns.
     */
    public boolean macOsXQuit() {

        exit();

        return true;
    }


    /**
     * Handles OS X About event.
     */
    public void macOsXAbout() {

        new gsfc.spdf.gui.AboutDialog(new JFrame()).setVisible(true);
    }


    /**
     * Terminates this application after providing the user with the
     * chance to save the current file.  There is no return from this
     * method.
     */
    public void exit() {

        exitAction.actionPerformed(
            new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
    }


    /**
     * ActionListener for reset-fill-value menu item.
     */
    public class ResetFillvalListener
        implements ActionListener {
            
        public void actionPerformed(ActionEvent event) {

            if (theCDF == null) {

                return;
            }
            Variable var;              // selected variable

            try {

                var = variablePanel.getSelectedVar();

                if (var == null) {
                            
                    JOptionPane.showMessageDialog(SKTEditor.edit,
                        "No variable selected",
                        "SKTEditor: Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                 }
                      
                 gsfc.spdf.istp.Variable v = 
                     new gsfc.spdf.istp.Variable(var); 
                            
                 if(v.getFillval() == null) {
                                
                     if(v.getDataType() == CDF.CDF_UCHAR || 
                        v.getDataType() == CDF.CDF_CHAR) {
                              
                         JOptionPane.showMessageDialog(SKTEditor.edit,
                             "Fill values are not defined for " +
                             "character type variables." );
                     }
                     else {
                                    
                         JOptionPane.showMessageDialog(SKTEditor.edit,
                             "Error resetting fill value",
                             "SKTEditor: Error",
                             JOptionPane.ERROR_MESSAGE);
                     }
                     return;
                 } 
                                
                int reply;             // user's reply to reset fill 
                                       // value dialog
                StringBuffer fill = new StringBuffer(); 
                                       // current fill value
                StringBuffer defaultFill = new StringBuffer();
                                       // default (new) fill value
                long dataType = v.getDataType();
                                       // variable's data type
                                
                if (dataType == CDF.CDF_TIME_TT2000) {

                    fill.append(
                        TerrestrialTime2000.toString(
                            (Long)v.getFillval()));

                    defaultFill.append(
                        TerrestrialTime2000.toString(
                            (Long)v.getDefaultFillval()));
                }
                else if (dataType == CDF.CDF_EPOCH16) { 
                                                                        
                    fill.append(
                        gsfc.spdf.istp.Epoch16.toString(
                            (double[])v.getFillval()));

                    defaultFill.append(
                        gsfc.spdf.istp.Epoch16.toString(
                            (double[])v.getDefaultFillval()));
                }
                else if (dataType == CDF.CDF_EPOCH) {  
                                 
                    fill.append(
                        gsfc.spdf.istp.Epoch8.toString(
                            (Double)v.getFillval()));

                    defaultFill.append(
                        gsfc.spdf.istp.Epoch8.toString(
                            (Double)v.getDefaultFillval()));
                }
                else {

                    fill.append(v.getFillval());
                    defaultFill.append(v.getDefaultFillval());
                }

                reply = JOptionPane.showConfirmDialog(
                            frame.getContentPane(),
                            "Are you sure that you want to reset\n \'" +
                            v.getName() + "\' Fill Value : \'"
                            + fill.toString() + "\'" +
                            "\n to the ISTP FILLVAL:  \'" + 
                            defaultFill.toString() + "\'",
                            "Confirm Fillval Reset",
                            JOptionPane.YES_NO_OPTION);
                            
                if (reply == JOptionPane.YES_OPTION) {

                    v.setDefaultFillval();
                    System.out.println(" ");
                    System.out.println(var + 
                        " Fill Value has been successfully reset.");
                }
            }
            catch (Exception e) {

                System.out.println(" ");
                System.out.println("Error resetting  FILLVAL.");
            }
        }
    }  // end class ResetFillValueListener
                
    
    private JMenu createComplianceCheckMenu() {
        
        complianceChecksMenu = new JMenu("ISTP Compliance Check");
        complianceChecksMenu.setToolTipText("Check ISTP Compliance");
        complianceChecksMenu.setMnemonic(KeyEvent.VK_C);
        
        JMenuItem mi = new JMenuItem("Entire File");
        mi.setToolTipText("Check ISTP compliance of file");
        mi.setMnemonic(KeyEvent.VK_F);
        mi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                setWaitCursor();
                updateLogicalAttributes();
                checkCompliance();
                if(variablePanel.getSelectedVar()!=null) {
                    Variable selectVar = variablePanel.getSelectedVar();
                    variablePanel.topDisplay.save(selectVar);
                    variablePanel.bottomDisplay.save(selectVar);
                    variablePanel.updateVarPanel(selectVar);
                };
                globalPanel.setGlobalAttributes();
                setDefaultCursor();
            }
        });
        
        complianceChecksMenu.add(mi);
        
        mi = new JMenuItem("Global Attributes");
        mi.setToolTipText("Check ISTP global attributes compliance");
        mi.setMnemonic(KeyEvent.VK_G);
        mi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                
                checkGlobalAttributesCompliance();
                globalPanel.setGlobalAttributes();
            }
        });
        
        complianceChecksMenu.add(mi);
        
        complianceChecksMenu.add(new JSeparator());

        mi = new JMenuItem("Suppress Warnings");
        mi.setToolTipText("Suppress compliance warnings");
        mi.setMnemonic(KeyEvent.VK_H);
        mi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {

                suppressedWarnings = 
                    IstpWarningDialog.getSuppressedWarnings(
                        frame, getSuppressedWarnings());
            }
        });
        
        complianceChecksMenu.add(mi);
        
        return complianceChecksMenu;
    }
    

    /**
     * Creates a CDF validator with any default validation criteria
     * file that is found.
     *
     * @return a CDF validator
     * @throws CdfValidationException if a cdf validation exception occurs.
     */
    private Validator createCdfValidator() 
        throws CdfValidationException {

        Validator validator = null;    // CDF validator

        try {

            validator = new Validator();
        }
        catch (NoClassDefFoundError e) {

            // most likely javax.xml.bind.JAXBException was not found 
            // because we are running under JDK 1.5.

            throw new CdfValidationException(e);
        }
        
        URL validationCriteria = Validator.getDefaultCriteria();
                                       // CDF validation criteria
        if (validationCriteria == null) {
        
            validationCriteria = getLastValidationCriteria();
        }
        else {

            // User specified default overrides last value

            setLastValidationCriteria(validationCriteria);
        }
        if (validationCriteria == null) {

            java.util.List<URL> criteriaUrls = 
                getValidationCriteriaUrls();
                                        // urls to included validation
                                        // criteria files
            if (criteriaUrls.size() > 0) {

                validationCriteria = criteriaUrls.get(0);
            }
        }
        if (validationCriteria != null) {

            try {

                validator.setValidationCriteria(validationCriteria);
            }
            catch (CdfValidationException e) {

                System.err.println("Error: " + e.getMessage());
                System.err.println("Failed to set the default CDF " +
                    "validation criteria file to " + 
                    validationCriteria);

                // user will have to manually set the criteria file
            }
        }

        return validator;
    }


    /**
     * Gets URLs to any CDF validation criteria files contained
     * in this application's JAR file in a directory named
     * <code>cdfValidationFiles</code>.
     *
     * @return list of URLs to CDF validation criteria files contained
     *     in this application's JAR file in a directory named
     *     <code>cdfValidationFiles</code>.
     */
    private java.util.List<URL> getValidationCriteriaUrls() {

        java.util.List<URL> criteriaUrls = new ArrayList<URL>();
                                       // criteria URLs that are 
                                       // returned
        URL jarUrl = this.getClass().getResource("SKTEditor.class");
                                       // URL to this app's jar

        if (jarUrl.getProtocol().equals("file")) {

            // Probably being run by Eclipse (or Netbeans) and we will
            // not be able to find the validation criteria files.

            return criteriaUrls;
        }
        int pathIndex = jarUrl.toString().indexOf("!");
                                       // index to begining of path
                                       // within the jar
        String baseUrlString =
            jarUrl.toString().substring(4, pathIndex);
                                       // url without "jar:" (which
                                       // JarInputStream did not like)
                                       // and without "!/..." class path
        URL baseJarUrl = null;         // base jar url without class 
                                       // path
        try {

            baseJarUrl = new URL(baseUrlString);
        }
        catch (MalformedURLException e) {

            System.err.println("SKTEditor.getValidationCriteriaUrls: " +
                "MalformedURLException with " + jarUrl + " : " + 
                e.getMessage());

            return criteriaUrls;
        }

        JarInputStream jarStream = null;
                                       // this app's jar file
        try {

            jarStream = new JarInputStream(baseJarUrl.openStream());
            JarEntry entry = null;     // jar entry

            while((entry = jarStream.getNextJarEntry()) != null) {

                if (entry.getName().contains("/cdfValidationFiles/") &&
                    !entry.isDirectory()) {

                    String criteriaUrl = 
                        "jar:" + baseJarUrl + "!/" + entry.getName();
                                       // full jar url for a criteria
                                       // file
                    criteriaUrls.add(new URL(criteriaUrl));
                }
            }
        }
        catch (IOException e) {

            System.err.println("SKTEditor.getValidationCriteriaUrls: " +
                "IOException while reading " + baseJarUrl + ": " + 
                e.getMessage());
        }
        finally {

            if (jarStream != null) {

                try {

                    jarStream.close();
                }
                catch (IOException e) {

                    // not much more we can do
                }
            }
        }

        if (criteriaUrls.size() < 2) {

            // Oracle Java < 1.6.0_71 (and even OpenJdk 1.7.0_51) do 
            // not find the files in pack200/gzip jars so do not 
            // deploy them.

            System.err.println(
                "SKTEditor.getValidationCriteriaUrls() found " +
                criteriaUrls.size() + " criteria files at ");
            System.err.println("  " + baseJarUrl);
        }

        return criteriaUrls;
    }


    /**
     * Creates a menu of recently accessed CDF files.
     *
     * @return "recently accessed cdf files" menu.
     */
    private JMenu createRecentCdfMenu() {

        RecentFileMenu menu = 
            new RecentFileMenu("Recent File", cdfHistory);
                                       // menu of recently accessed
                                       // cdf files

        menu.addFileSelectedListener(this);

        return menu;
    }


    /**
     * Invoked whenever the user selects a recently accessed CDF file
     * from the menu.
     *
     * @param event event associated with the selection of a recently
     *            accessed CDF file.
     */
    public void fileSelected(FileSelectedEvent event) {

        String selectedFilename = event.getFilename();
                                       // selected file's name
        File selectedFile = new File(selectedFilename);
                                       // the selected file
        if (!selectedFile.exists()) {

            JOptionPane.showMessageDialog(frame,
                selectedFilename + "\n no longer exists.",
                "Non-existent file error",
                JOptionPane.ERROR_MESSAGE);

            try {

                cdfHistory.remove(selectedFilename);
            }
            catch (BackingStoreException e) {

                System.err.println("Failed to remove " + 
                    selectedFilename + " from history.");
                System.err.println("BackingStoreException: " +
                    e.getMessage());
            }
            return;
        }

        if (!saveCurrentFile(true, true)) {

            try {

                closeFile();
            }
            catch (CDFException e) {

                System.err.println("Failed to close file: " + 
                    e.getMessage());
            }
            catch (IOException e) {

                System.err.println("Failed to close file: " + 
                    e.getMessage());
            }
            catch (InterruptedException e) {

                System.err.println("Failed to close file: " + 
                    e.getMessage());
            }
        }

        try {

            openFile(selectedFilename);
        }
        catch (Exception e) {

            JOptionPane.showMessageDialog(frame,
                e.getMessage(),
                "SKTEditor: Exception",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * CDF validation criteria Preference key.
     */
    private static final String CDF_VALIDATION_CRITERIA_PREF =
        "CdfValidationCriteria";


    /**
     * Gets the CDF validation criteria from the user's Preferences.
     *
     * @return CDF validation crieteria from the user's Preferences
     *     or null if the user has none.
     * @see #setLastValidationCriteria(URL)
     */
    private URL getLastValidationCriteria() {

        String lastValidationCriteria =
            preferences.get(CDF_VALIDATION_CRITERIA_PREF, null);

        try {

            return new URL(lastValidationCriteria);
        }
        catch (MalformedURLException e) {

            // not a concern
        }
        catch (NullPointerException e) {

            // not a concern
        }
        return null;
    }

    /**
     * Sets the CDF validation criteria in the user's Preferences.
     *
     * @param value CDF validation crieteria to save in the user's 
     *            Preferences.
     * @see #getLastValidationCriteria()
     */
    private void setLastValidationCriteria(URL value) {

        preferences.put(CDF_VALIDATION_CRITERIA_PREF, value.toString());

    }


    /**
     * Creates the CDF validation menu.
     *
     * @return CDF valiation menu.
     */
    private JMenu createValidationMenu() {
        
        JMenu validationMenu = new JMenu("CDF Validation");
        validationMenu.setToolTipText("Perform CDF Validation");
        validationMenu.setMnemonic(KeyEvent.VK_V);
        
        validateFileMenuItem = new JMenuItem("Validate");
        validateFileMenuItem.setToolTipText(
            "Perform Validation On File");
        validateFileMenuItem.addActionListener(
            new ValidateFileListener());
        validationMenu.add(validateFileMenuItem);

        validationMenu.add(new JSeparator());

        setValidateFileMenuItem();

        validationMenu.add(createSetCriteriaMenu());
        
        return validationMenu;
    }


    /**
     * Creates the "Set Validation Criteria" menu.
     *
     * @return "Set Validation Criteria" menu.
     */
    private JMenu createSetCriteriaMenu() {

        JMenu setCriteriaMenu = 
            new JMenu("Set Validation Criteria");
        setCriteriaMenu.setToolTipText(
            "Sets the validation criteria file");

        for (URL criteriaUrl : getValidationCriteriaUrls()) {

            try {

                String criteriaName =
                    Validator.getCriteriaName(criteriaUrl);

                JMenuItem criteriaMenuItem = 
                    new JMenuItem(criteriaName);
                criteriaMenuItem.setToolTipText(
                    "Sets " + criteriaName + " validation criteria");
                criteriaMenuItem.addActionListener(
                    new SelectValidationCriteriaListener(criteriaUrl));

                setCriteriaMenu.add(criteriaMenuItem);
            }
            catch (CdfValidationException e) {

                System.err.println("CdfValidationException while " +
                    "attempting to create a JMenuItem for " +
                    criteriaUrl + ": " + e.getMessage());
            }
        }

        setCriteriaMenu.add(new JSeparator());

        JMenuItem selectFileMenuItem = new JMenuItem("Select File...");
        selectFileMenuItem.setToolTipText(
            "Sets validation criteria from a file");
        selectFileMenuItem.addActionListener(
            new SetValidationFileListener ());

        setCriteriaMenu.add(selectFileMenuItem);

        return setCriteriaMenu;
    }

    
    /**
     * Listener for a request to set the CDF validation file.
     */
    private class ValidateFileListener 
        implements ActionListener {

        /**
         * Responds to a request to validate a CDF file.
         *
         * @param event action event
         */
        public void actionPerformed(ActionEvent event) {

            setWaitCursor();
            System.out.println("###############################");
            try {

                System.out.println("Validating " + filename +
                    " with " + cdfValidator.getCriteriaName());
                cdfValidator.validate(theCDF);
            }
            catch (CdfValidationException e) {

                System.out.println("Validation failed due to: " + 
                    e.getMessage());
                JOptionPane.showMessageDialog(
                    frame.getContentPane(),
                    "Validation failed due to: " + e.getMessage(),
                    "Validation Failure",
                    JOptionPane.ERROR_MESSAGE);
            }
            catch (CDFException e) {

                System.out.println("Validation failed due to: " + 
                    e.getMessage());
                JOptionPane.showMessageDialog(
                    frame.getContentPane(),
                    "Validation failed due to: " + e.getMessage(),
                    "Validation Failure",
                    JOptionPane.ERROR_MESSAGE);
            }
            System.out.println("###############################");
            setDefaultCursor();
            setStatus(filename + " validation is finished.",
                StatusBar.INFO, true, false);
        }
    }


    /**
     * Listener for a request to set the CDF validation criteria.
     */
    private class SelectValidationCriteriaListener 
        implements ActionListener {

        /**
         * URL of CDF validation criteria.
         */
        private URL validationCriteria = null;


        /**
         * Creates a SelectValidataionCriteriaListener.
         *
         * @param criteria URL of CDF validation criteria to set
         *            when actionPerformed event occurs.
         */
        public SelectValidationCriteriaListener(URL criteria) {

            validationCriteria = criteria;
        }


        /**
         * Responds to a request to set the CDF validation file
         * by displaying a file chooser and responding to the user's
         * choice.
         *
         * @param event action event
         */
        public void actionPerformed(ActionEvent event) {

            try {

                cdfValidator.setValidationCriteria(validationCriteria);
                setLastValidationCriteria(validationCriteria);
                setValidateFileMenuItem();
            }
            catch (CdfValidationException e) {

                System.err.println("CdfValidationException when " +
                    "attempting to set the validation criteria to " +
                    validationCriteria + ": " + e.getMessage());
            }
        }
    }
                

    /**
     * Listener for a request to set the CDF validation file.
     */
    private class SetValidationFileListener 
        implements ActionListener {

        /**
         * Responds to a request to set the CDF validation file
         * by displaying a file chooser and responding to the user's
         * choice.
         *
         * @param event action event
         */
        public void actionPerformed(ActionEvent event) {
                
            JFileChooser criteriaChooser = new JFileChooser();

            FileNameExtensionFilter criteriaFilter =
                new FileNameExtensionFilter("XML Files", "xml");

            criteriaChooser.setFileFilter(criteriaFilter);

            URL lastFile = cdfValidator.getValidationCriteria();
                                       // last validation criteria
                                       // choice
            if (lastFile != null &&
                !lastFile.getProtocol().contains("jar")) {

                criteriaChooser.setCurrentDirectory(
                    new File(lastFile.getPath()));
            }
            boolean retry = true;      // flag indicating whether to
                                       // prompt the user for the
                                       // criteria file again
            while (retry) {

                if (criteriaChooser.showOpenDialog(instance) ==
                    JFileChooser.APPROVE_OPTION) {

                    File criteriaFile =
                        criteriaChooser.getSelectedFile();

                    try {

                        URL newCriteria =
                            new URL("file", null, 
                                    criteriaFile.getCanonicalPath());

                        cdfValidator.setValidationCriteria(newCriteria);
                        setLastValidationCriteria(newCriteria);
                        retry = false;
                    }
                    catch (CdfValidationException e) {

                        JOptionPane.showMessageDialog(
                            frame.getContentPane(),
                            criteriaFile.getPath() + 
                            "\nis not a valid CDF validation " +
                            "criteria file.",
                            "Validation Criteria File Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                    catch (MalformedURLException e) {

                        System.err.println("Choosen criteria file " +
                            "generated a MalformedURLException: " +
                            e.getMessage());
                    }
                    catch (IOException e) {

                        System.err.println("Choosen criteria file " +
                            "generated an IOException: " +
                            e.getMessage());
                    }
                }
                else {

                    retry = false;
                }
            }
            setValidateFileMenuItem();
        }
    }
        

    /**
     * Sets the validateFileMenuItem's text to include the current 
     * name of the validation criteria and enables the menu item
     * if the cdfValidator has a validation file set.  If no 
     * validation file is set in the cdfValidator, then the
     * validateFileMenuItem is disabled.
     */
    private void setValidateFileMenuItem() {
        
        if (cdfValidator.getValidationCriteria() != null) {
 
            validateFileMenuItem.setEnabled(true);
            String criteriaName = cdfValidator.getCriteriaName();
                                       // validation criteria name
            if (criteriaName.length() > 10) {

                criteriaName = criteriaName.substring(0, 9) + "...";
            }
            validateFileMenuItem.setText(
                "Validate using " + criteriaName + " criteria");
        }
        else {

            validateFileMenuItem.setEnabled(false);
        }
    }


    /**
     * Moves a group of variables in the current CDF to a new location.
     *
     * @param names names of the variables to move.
     * @param insertIndex ... (0 &le; insertIndex &le; number of variables)
     * @throws CDFException if a CDFException occurs.
     */
    public void moveVariables(
        java.util.List<String> names,
        int insertIndex)
        throws CDFException {

        if (theCDF == null) {

            return;
        }
        Cdf.moveVariables(theCDF, names, insertIndex);
    }


    public static void main(final String[] args) {
        
        if (!loadNativeCdfLibs()) {
            
            System.err.println(
            "Unable to start due to CDF native library error");
            return;
        }
        
        // Debugging.  Capture all output to stderr to a file as well
        // as the screen.
        try {           
            
            FileOutputStream errFile = null;
                             
            String errFilename = System.getProperty("user.home") +
                                     System.getProperty("file.separator") +
                                     "SKTEditorError.txt";
 
            if (System.getProperty("os.name").equals("Mac OS")) 
                
            errFile = new FileOutputStream("SKTEditorError.txt");
                
            else
                
                errFile = new FileOutputStream(errFilename);               

            OutputStream osm =
                new OutputStreamMulticaster(System.err);
            ((OutputStreamMulticaster)osm).add(errFile);
            
            PrintStream ps = new PrintStream(osm);
            System.setErr(ps);
        } catch (Exception e) {
                    
            System.err.println("Could not setup error logging\n"+
                        "Errors will be sent to the screen only.");
        }
        
        String fileEncoding = System.getProperty("file.encoding");
        if (!fileEncoding.equalsIgnoreCase("UTF-8")) {

            System.err.println("Initial default file.encoding was " + 
                fileEncoding);
            System.err.println("It must be UTF-8 to ensure proper " +
                "functioning with CDF");
            System.err.println("files containing Unicode characters.");
            System.err.println("Please set a UTF-8 locale before " +
                "restarting SKTEditor.");
            System.err.println("For example, LANG=en_US.utf8");
            // If not utf8, swing components like JTable will return
            // corrupt String values which then corrupts the CDF.
            System.exit(1);
        }

        // Get the application properties
        separator = System.getProperty("file.separator");
        
        propertyPath = System.getProperty("spdf.resources");
        if(propertyPath == null) {
            
            propertyPath = "/resources/";
        }
        else {
            
            propertyPath += separator;
        };
        try {
            
            defaultProperties = new ApplicationProperties(propertyPath,
                    "default.properties",
                    "Default Properties");
            appProperties = new ApplicationProperties(defaultProperties);
        } catch (IOException e) {
            
            System.out.println("Error loading Properties: "+e);
            System.exit(1);
        };
                
        Thread worker = new Thread() {
            
            public void run() {           
         
                String programName = defaultProperties.getProperty("program.name");
                String programVersion = defaultProperties.getProperty("program.version");
                String programBuildPlatform =
                defaultProperties.getProperty("program.build.platform");
                String programBuildCompiler =
                defaultProperties.getProperty("program.build.compiler");
                String programBuildDate =
                defaultProperties.getProperty("program.build.date");
                System.err.println("program.name = " + programName);
                System.err.println("program.version = " + programVersion);
                System.err.println("program.build.platform = " + programBuildPlatform);
                System.err.println("program.build.compiler = " + programBuildCompiler);
                System.err.println("program.build.date = " + programBuildDate);
                try {

                    Package helpPkg = 
                        Class.forName("javax.help.HelpSet").getPackage();
                                       // Help package

                    System.err.println("JavaHelp library version: " +
                               helpPkg.getImplementationVersion());
                }
                catch (ClassNotFoundException e) {

                    System.err.println("HelpSet Class not found");
                }
                try {

                    Package validatePkg = Class.forName(
                        "gov.nasa.gsfc.spdf.cdf.validation.Validator").
                            getPackage();
                                       // cdf validation package

                    System.err.println(
                        "CDF Validation library version: " +
                        validatePkg.getImplementationVersion());
                }
                catch (ClassNotFoundException e) {

                    System.err.println(
                        "CDF Validation Class not found");
                }
                System.err.println(
                    "CDF Leap seconds source: " +
                    TerrestrialTime2000.getLeapSecondsSource());
                System.err.println(
                    "Rows in CDF leap seconds table : " +
                    TerrestrialTime2000.getRowsInLeapSecondsTable());

                System.getProperties().list(System.err);
            }                             
        };
                
        worker.start();
        
        frame = new JFrame("SKTEditor");
        
        frame.setDefaultCloseOperation(
            WindowConstants.DO_NOTHING_ON_CLOSE);
        
        JPanel progressPanel = new JPanel() {
            public Insets getInsets() {
                return new Insets(10, 10, 10, 10);
            }
        };
        
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
        frame.getContentPane().add(progressPanel, BorderLayout.CENTER);
        
        Dimension d = new Dimension(400, 20);
        if (args.length == 1) ++SKTEditor.totalPanels;
        SKTEditor.progressLabel = new JLabel();//"Loading, please wait...");
        SKTEditor.progressLabel.setAlignmentX(CENTER_ALIGNMENT);
        SKTEditor.progressLabel.setMaximumSize(d);
        SKTEditor.progressLabel.setPreferredSize(d);
        progressPanel.add(progressLabel);
        progressPanel.add(Box.createRigidArea(new Dimension(1,20)));
        
        SKTEditor.progressBar = new JProgressBar(0, SKTEditor.totalPanels + 2);
        SKTEditor.progressBar.setStringPainted(true);
        SKTEditor.progressLabel.setLabelFor(progressBar);
        SKTEditor.progressBar.setAlignmentX(CENTER_ALIGNMENT);
        progressPanel.add(SKTEditor.progressBar);
                               
        frame.setSize(INITIAL_WIDTH, INITIAL_HEIGHT);
        frame.setLocationRelativeTo(null);
        
        final SKTEditor se = new SKTEditor();
        
        updateStatus(++currentProgressValue,"opening file... " );
                          
        try {
                     
            if (args.length == 0)
      
                se.newFile();
                            
            else
                                   
                se.openFile(args[0]); 
                                                           
//            se.isCdfVersionsOk();
          
        } catch (CDFException exc) {
            
            exc.printStackTrace();
            JOptionPane.showMessageDialog(frame,
            exc.getMessage(),
            "SKTEditor: CDFException",
            JOptionPane.ERROR_MESSAGE);
            
        }  catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame,
                e.getMessage(),
                "SKTEditor: Exception",
                JOptionPane.ERROR_MESSAGE);
        }

        updateStatus(++currentProgressValue,"done " );
                               
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            
            public void run() {
                         
                frame.getContentPane().removeAll();
                frame.getContentPane().setLayout(new BorderLayout());
                frame.getContentPane().add(se, BorderLayout.CENTER);
 
                frame.setSize(new Dimension(screenWidth -screenWidth/4,
                      screenHeight - screenHeight/12));                            

                Dimension windowSize = frame.getSize();
               
                frame.setLocationRelativeTo(null);

                frame.validate();    
            
                frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                
                se.requestDefaultFocus();
                se.setStatus("Ready", StatusBar.INFO, false, true);
            }
        });        
  
    }
    
    static public  void updateStatus(final int i, final String s) {
       
	 Runnable doSetProgressBarValue = new Runnable() {
             
            public void run() {
                    
                progressBar.setValue(i);
                progressLabel.setText(s);
            }
         };
	 SwingUtilities.invokeLater(doSetProgressBarValue);
    }   
    
    public ImageIcon loadImageIcon(String filename, String description) {
        if(applet == null) {
            return new ImageIcon(SKTEditor.class.getResource(filename), description);
        } else {
            URL url;
            try {
                url = new URL(applet.getCodeBase(),filename);
            } catch(MalformedURLException e) {
                System.err.println("Error trying to load image " + filename);
                return null;
            }
            return new ImageIcon(url, description);
        }
    }
    
    public static JPanel buildHorizontalPanel(boolean threeD) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        if(threeD) {
            p.setBorder(loweredBorder);
        }
        return p;
    }
    
    public static SKTEditor sharedInstance() {
        return instance;
    }
    
    public java.applet.Applet getApplet() {
        return applet;
    }
    
    public boolean isApplet() {
        return (applet != null);
    }
    
    public Container getRootComponent() {
        if(isApplet())
            return applet;
        else
            return frame;
    }
    
    public Frame getFrame() {
        if(isApplet()) {
            Container parent;
            for(parent = getApplet(); parent != null &&
            !(parent instanceof Frame) ; parent = parent.getParent());
            if(parent != null)
                return (Frame)parent;
            else
                return null;
        } else
            return frame;
    }
    
    ////////////////////////////////////////////////////////////
    //                                                        //
    //                File Handling Methods                   //
    //                                                        //
    ////////////////////////////////////////////////////////////
    
    public boolean checkCompliance() {

        String displayFilename = filename; // the filename to be used in
        //  display messages
        if (displayFilename == null) {
            
            displayFilename = "new file";
        };
        VerificationResult result = null; // results of ISTP compliance
                                          // check
        try {

            result = ISTPCompliance.check(theCDF, 
                         suppressedWarnings,
                         new DefaultVerificationCallback());
        }
        catch (CDFException e) {

            JOptionPane.showMessageDialog(frame,
                e.getMessage(),
                "SKTEditor: CDFException",
                JOptionPane.ERROR_MESSAGE);

            return false;
        }
        Vector globalErrors = result.getGlobalAttributeErrors();
        Map vectorErrors = result.getVariableErrors();

        System.out.println("###############################");
        System.out.println("Compliance Check for " + displayFilename);
        System.out.println("CDF File Version: " + theCDF.getVersion());
        try {

            System.out.println("File Last Leap Second: " + 
                Cdf.getLeapSecondLastUpdatedAsString(theCDF));
            System.out.println("Majority: " + 
                Cdf.getMajorityAsString(theCDF));
        }
        catch (CDFException e) {}

        if ((globalErrors.size() > 0) ||
            (vectorErrors.size() > 0)) {

            setStatus(displayFilename + " is not ISTP-Compliant.",
            StatusBar.ERROR, true, true);
            
            result.printErrors(System.out);

            return false;
        } 
        else {

            setStatus(displayFilename + " is ISTP-Compliant.",
            StatusBar.INFO, true, false);
        }
        System.out.println("###############################");
        return true;
    }
    
    
    /**
     * Checks the global attributes for ISTP compliance.
     */
    public void checkGlobalAttributesCompliance() {

        setWaitCursor();

        updateLogicalAttributes();
        Vector errors = ISTPCompliance.checkGlobalAttributes(
                            theCDF, suppressedWarnings);
        
        if (errors.size() > 0) {
            
            printGlobalAttributeErrors(errors);
            setStatus("Global Attributes are not ISTP-Compliant",
            StatusBar.ERROR, true, true);
        }
        else {
            
            setStatus("Global Attributes are ISTP-Compliant",
            StatusBar.INFO, true, false);
        };
        
        setDefaultCursor();
    }
    
    
    /**
     * Prints the given global attribute errors.
     *
     * @param errors the errors from a global attributes compliance 
     *            check.
     */
    private void printGlobalAttributeErrors(Vector errors) {
        if (errors.size() > 0) {
            
            System.out.println("Global errors:");
            for (int i=0; i< errors.size(); i++) {
                
                System.out.println("\t" + errors.elementAt(i));
            };
        } else {
            
            System.out.println("All required global attributes present.");
        };
    }
    

    /**
     * After prompting the user for confirmation, deletes all data 
     * values from the current CDF.
     *
     */
    private void deleteAllDataValues() {

        int reply = 
            JOptionPane.showConfirmDialog(frame.getContentPane(),
            "Are you sure that you want to delete \n" +
            "all data values from this CDF?",
            "Confirm delete of all data values",
            JOptionPane.YES_NO_OPTION);
                
        if (reply == JOptionPane.YES_OPTION && theCDF != null) {

                for (Enumeration e = theCDF.getVariables().elements();
                     e.hasMoreElements(); ) {

                    Variable var = (Variable)e.nextElement();  
                                       // a variable
                    try {

                        long records = var.getMaxWrittenRecord();
                                       // number of records
                        if (var.getRecVariance() && records > 0) {
    
                            var.deleteRecords(0, records);
                        }
                    }
                    catch (CDFException ex) {

                        System.out.println("\nError deleting " +
                            var.getName() + " variable data.");
                        System.err.println("Error deleting " +
                            var.getName() + " variable data: " +
                            ex.getMessage());
                    }
                }

            System.out.println(
                "\nAll record-varying data values have been deleted.");
        }
    }

    
    /**
     * Invokes the {@link gsfc.spdf.istp.tools.GetNotes GetNotes()} tool.
     *
     * @see gsfc.spdf.istp.tools.GetNotes
     */
    private void extractNotes() {
        setWaitCursor();                
        setStatus("Extract Notes tool is not yet implemented",
        StatusBar.INFO, true, false);
        
        setDefaultCursor();
    }
    
    
    /**
     * Invokes the {@link gsfc.spdf.istp.tools.GetAttributes GetAttributes()}
     * tool.
     *
     * @see gsfc.spdf.istp.tools.GetAttributes
     */
    private void extractAttributes() {
        setWaitCursor();                
        setStatus("Extract Attributes tool is not yet implemented",
        StatusBar.INFO, true, false);
        
        setDefaultCursor();
    }
    

    public void updateLogicalAttributes() {
        
        try {
            // Save the global attributes.
            globalPanel.saveGlobalAttributes();
            globalPanel.setGlobalAttributes();
            
            String logicalSource = 
                GlobalAttribute.buildLogicalSourceFromComponents(
                    theCDF);

            GlobalAttribute.setLogicalSourceValue(
                theCDF, logicalSource);

            logicalFilename = 
                GlobalAttribute.getRecommendedLogicalFileId(theCDF);
            
            GlobalAttribute.setLogicalFileIdValue(
                theCDF, logicalFilename);
        }
        catch (CDFException e) {
            
            logicalFilename = rootfilename;
            setStatus("Autonaming failed.",
            StatusBar.ERROR, true, true);
        }
    }
    

    private final static String NO_SKT_SUPPORT_MSG =
            
        "<html>The \"CDF skeleton file (.skt)\" could not be saved/opened because<br>" +
        " it requires that the full CDF software distribution package<br>" +
        "be installed on your local machine.<br>" +

        "If you have access to the associated CDF (.cdf) file,  <br>" +
         "please open that instead.<br>" +

        "If not then install the full CDF software " +
        "distribution package,  <br>" +
        "including the toolkit program, and try opening the file again. </html>";

    private final static String NO_NC_SUPPORT_MSG =
            
        "<html>The \"netCDF file (.nc)\" could not be saved/opened because<br>" +
        " it requires that the CDF Data Format Translation Tools<br>" +
        "be installed on your local machine.<br>" +

        "If you have access to the associated CDF (.cdf) file,  <br>" +
         "please open that instead.<br>" +

        "If not then install the CDF Data Translation Tools " +
        "  <br>" +
        "and try opening the file again. </html>";


    public void openFile(String filename)
        throws IOException, CDFException, InterruptedException {

        setWaitCursor();

        System.err.println("opening " + filename);
        
        this.theFile = new File(filename);
        this.filename = filename;
        
        ext = getExtension(filename);
        // Strip off the file extension
        rootfilename = getRootFilename(filename);

        // If there is a workingFile open, close it.
        if (theCDF != null) {

            cdfEditor.saveChanges();
            theCDF.close();
            theCDF = null;
            cdfEditor.setCdf(null);
        }

        String tempFilename = createTempCdf(filename);
                                       // temporary CDF filename
        setDefaultCursor();
        workingRootFilename = getRootFilename(tempFilename);

        if (tempFilename == null) {

            JOptionPane.showMessageDialog(frame,
                "Failed to create a temporary CDF file.",
                "Open file error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {

            theCDF = CDF.open(tempFilename,
                              CDFConstants.READONLYoff);
        }
        catch (CDFException e) {

            if (e.getCurrentStatus() == CDF.NO_SUCH_CDF) {

                JOptionPane.showMessageDialog(frame,
                    "Failed to open the temporary CDF file.",
                    "Open file error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            //
            // Delete working copy of file
            //
            new File(tempFilename).delete();
            throw e;
        }


        if (!wasOpenSuccessful(theCDF)) {

            long status = theCDF.getStatus();
                                       // status from open 
            theCDF.close();
            theCDF = null;
            throw new CDFException(status);
        }
        
        try {

            cdfHistory.add(theFile.getCanonicalPath());
        }
        catch (Exception e) {

            System.err.println("Failed to save filename " + 
                theFile.getName() + " to history backing store: " + 
                e.getMessage());
        }

        verifyFilenaming(theCDF, filename);
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            
            public void run() {
                   
                getFrame().setTitle("SKTEditor: " + theFile.getName());

                checkCompliance();

                globalPanel.resetPanel();
                variablePanel.resetPanel(true);
                enableSaving(false);
                enableVarMenu();
                globalPanel.setGlobalAttributes();
                frame.validate();

                logoPanel.updateFileStats();
            }
        });  
        
    }


    /**
     * Creates a temporary, working CDF file.  If the input file is
     * not a CDF file, this function will attempt to convert it to a
     * CDF file.
     *
     * @param filename full pathname of the input file.
     * @return full pathname of the created temporary CDF file.  null
     *             if the file could not be made.
     * @throws CDFException if a CDFException occurs.
     * @throws IOException if an IOException occurs.
     * @throws InterruptedException if an InterruptedException occurs.
     */
    private String createTempCdf(
        String filename)
        throws CDFException, IOException, InterruptedException {

        String filenameExt = getExtension(filename);
                                       // filename's extension
        String rootFilename = getRootFilename(filename);
                                       // filename without extension
        String tempFilename = createTempFilename(".cdf");
                                       // temporary filename
        String tempRootFilename = getRootFilename(tempFilename);
                                       // temporary filename without
                                       // extension
        if (filenameExt.equals(".cdf")) {

            copyFile(filename, tempFilename);
        }
        else if (filenameExt.equals(".skt") ||
                 filenameExt.equals(".nonrvskt")) {
            
            try {
                
                CDFTools.skeletonCDF(
                    filename, tempRootFilename,
                    new File(tempFilename).exists(),
                    true, false, false, 2, CDFTools.NO_REPORTS, null);
            }
            catch (IOException e) {
                
                if (e.getMessage().indexOf("not found") > -1 ||
                    e.getMessage().indexOf("error=2") > -1) {
                    
                    JOptionPane.showMessageDialog(frame,
                                       NO_SKT_SUPPORT_MSG,
                                       "Open skeleton file error", 
                                       JOptionPane.ERROR_MESSAGE);
                    return null;
                }
                else {
                    
                    throw e;
                }
            }
        }
        else if (filenameExt.equalsIgnoreCase(".nc")) {

            try {
                
                int status = CDFTools.netCdfToCdf(
                                 rootFilename, tempRootFilename);

                if (status != 0) {

/*
System.err.println("****CDFTools.netCdfToCdf(" +
rootFilename + ", " + tempRootFilename + ") status = " + status);
*/

                    return null;
                }
            }
            catch (IOException e) {
                
                if (e.getMessage().indexOf("not found") > -1 ||
                    e.getMessage().indexOf("error=2") > -1) {
                    
                    JOptionPane.showMessageDialog(frame,
                                       NO_NC_SUPPORT_MSG,
                                       "Open netCDF file error", 
                                       JOptionPane.ERROR_MESSAGE);
                    return null;
                }
                else {
                    
                    throw e;
                }
            }
        }
        else {

            return null;
        }

        return tempFilename;
    }
        

    private void verifyFilenaming(CDF cdf, String filename) 
        throws CDFException {

        String dataVersion = Filename.getVersion(filename);
                                       // dataversion from filename
        if (dataVersion != null) {

            GlobalAttribute.setDataVersionValue(cdf, dataVersion);
        }

        String fileNamingConvention = 
            GlobalAttribute.getFileNamingConventionValue(cdf);

        if (fileNamingConvention == null) {

            fileNamingConvention = 
                GlobalAttribute.guessFileNamingConvention(
                    cdf, filename);

            GlobalAttribute.setFileNamingConventionValue(
                cdf, fileNamingConvention);

            System.out.println(
                "Global Attribute \"File_naming_convention\" was not " +
                "found.  It was set to " + fileNamingConvention);
        }
        else {

            String newFileNamingConvention =
                GlobalAttribute.addDateTimeToFileNamingConvention(
                    cdf, filename);

            if (newFileNamingConvention != null &&
                !newFileNamingConvention.equals(fileNamingConvention)) {

                System.out.println(
                    "Global Attribute \"File_naming_convention\" was " +
                    "changed from " + fileNamingConvention + " to " +
                    newFileNamingConvention + ".");

                // Now adjust Logical_source and Logical_file_id to 
                // be consistent
                String oldLogicalSource =
                    GlobalAttribute.getLogicalSourceValue(cdf);
                String logicalSource =
                    GlobalAttribute.buildLogicalSourceFromComponents(
                        cdf);
                if (!logicalSource.equals(oldLogicalSource)) {
                
                    GlobalAttribute.setLogicalSourceValue(
                        cdf, logicalSource);

                    System.out.println(
                        "Global Attribute \"Logical_source\" was " +
                        "changed from " + oldLogicalSource + " to " +
                        logicalSource + " to be consistent with " + 
                        newFileNamingConvention + ".");
                }

                String oldLogicalFileId =
                    GlobalAttribute.getLogicalFileIdValue(cdf);
                String logicalFileId =
                    GlobalAttribute.getRecommendedLogicalFileId(cdf);

                if (!logicalFileId.equals(oldLogicalFileId)) {

                    GlobalAttribute.setLogicalFileIdValue(
                        cdf, logicalFileId);

                    System.out.println(
                        "Global Attribute \"Logical_file_id\" was " +
                        "changed from " + oldLogicalFileId + " to " +
                        logicalFileId + " to be consistent with " + 
                        newFileNamingConvention + ".");
                }
            }
        }
    }
    

    public void newFile() 
        throws CDFException,
            IOException {
        saveAsOnClose = true;
        
        // If there is a workingFile open. close it
        if (theCDF != null) {
            cdfEditor.saveChanges();
            theCDF.close();
            theCDF = null;
            cdfEditor.setCdf(null);
        }
        
        theFile = null;
        filename = null;
        rootfilename = null;
        
        ext = ".cdf";
        
        String newName = createTempFilename(ext);
        workingRootFilename = getRootFilename(newName);
        

        ButtonGroup group = new javax.swing.ButtonGroup();
        JRadioButton cdf3 = new javax.swing.JRadioButton("CDF version 3.x");
        JRadioButton cdf2 = new javax.swing.JRadioButton("CDF version 2.x");
        cdf3.setSelected(true);   
        group.add(cdf3);
        group.add(cdf2);
/*
        String msgString1 = " You can choose to create a CDF file  that can\n" +
                            " be read by CDF version 2.7.2 or earlier. \n" + 
                            " By default the newly created CDF file will not \n"+
                            " be readable by earlier versions.";
        Object[] array = {msgString1, cdf3, cdf2};
        int sel = javax.swing.JOptionPane.showConfirmDialog(null,array,"CDF Version Selection",JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE);  
        try {

            CDF.setFileBackward(cdf2.isSelected()? CDF.BACKWARDFILEon:CDF.BACKWARDFILEoff);
        }
        catch (NoSuchMethodError e) {

            JOptionPane.showMessageDialog(frame,
                "<html>The configured version of CDF is obsolete.<br>" +
                "Please upgrade to a newer version.</html>",
                "Inadequate CDF Version",
                JOptionPane.ERROR_MESSAGE);

            System.exit(0);
        }
*/
        
        ButtonGroup groupFill = new javax.swing.ButtonGroup();
        String msgString1 = " You can choose to set FILLVAL attributes\n" +
                            " to NaN where applicable. \n" + 
                            " By default FILLVAL attributes  are set \n"+
                            " to ISTP compliant values.";
        JRadioButton opt1Fill = 
            new JRadioButton(
                    "Set FILLVAL to ISTP Compliant Default Values");
        JRadioButton opt2Fill = 
            new JRadioButton("Set FILLVAL to NaN where applicable");
        opt1Fill.setSelected(true);   
        groupFill.add(opt1Fill);
        groupFill.add(opt2Fill);
        Object[] message = {msgString1, opt1Fill, opt2Fill};
        int sel = JOptionPane.showConfirmDialog(null, message,
                      "FILLVAL Default Value Selection",
                      JOptionPane.DEFAULT_OPTION, 
                      JOptionPane.QUESTION_MESSAGE);  

        FillvalAttribute.setUseNaNValues(opt2Fill.isSelected() ? 
                                         true : false);
                    
        System.err.println("Creating: " + newName);
        theCDF = CDF.create(newName);
        theFile = new File(newName);
        setChecksum(theCDF);

        if (!isCdfVersionsOk()) {

            theCDF.delete();

            System.exit(0);
        }
        
        theCDF.setMajority(getArrayMajority());
        
        // Set the title to display the filename
        getFrame().setTitle("SKTEditor: New File");
        
        // Initialize the new cdf
        Attribute.create(theCDF, "Project", GLOBAL_SCOPE);
        Attribute.create(theCDF, "Source_name", GLOBAL_SCOPE);
        Attribute.create(theCDF, "Discipline", GLOBAL_SCOPE);
        Attribute.create(theCDF, "Data_type", GLOBAL_SCOPE);
        Attribute.create(theCDF, "Descriptor", GLOBAL_SCOPE);
        
        Attribute fileNamingAttribute =
        Attribute.create(theCDF, "File_naming_convention", GLOBAL_SCOPE);
        Entry.create(fileNamingAttribute, 0, CDF_CHAR, "source_datatype_descriptor");
        
        Attribute dataVersionAttribute =
        Attribute.create(theCDF, "Data_version", GLOBAL_SCOPE);
        Entry.create(dataVersionAttribute, 0, CDF_CHAR, "01");
        Attribute.create(theCDF, "PI_name", GLOBAL_SCOPE);
        Attribute.create(theCDF, "PI_affiliation", GLOBAL_SCOPE);
        Attribute.create(theCDF, "TEXT", GLOBAL_SCOPE);
        Attribute.create(theCDF, "Instrument_type", GLOBAL_SCOPE);
        Attribute.create(theCDF, "Mission_group", GLOBAL_SCOPE);
        Attribute.create(theCDF, "Logical_source", GLOBAL_SCOPE);
        Attribute.create(theCDF, "Logical_file_id", GLOBAL_SCOPE);
        Attribute.create(theCDF, "Logical_source_description", GLOBAL_SCOPE);
        Attribute.create(theCDF, "Time_resolution", GLOBAL_SCOPE);
        Attribute.create(theCDF, "Rules_of_use", GLOBAL_SCOPE);
        Attribute.create(theCDF, "Generated_by", GLOBAL_SCOPE);
        Attribute.create(theCDF, "Generation_date", GLOBAL_SCOPE);
        Attribute.create(theCDF, "Acknowledgement", GLOBAL_SCOPE);
        Attribute.create(theCDF, "MODS", GLOBAL_SCOPE);
        Attribute.create(theCDF, "ADID_ref", GLOBAL_SCOPE);
        Attribute.create(theCDF, "LINK_TEXT", GLOBAL_SCOPE);
        Attribute.create(theCDF, "LINK_TITLE", GLOBAL_SCOPE);
        Attribute.create(theCDF, "HTTP_LINK", GLOBAL_SCOPE);
        Attribute.create(theCDF, "spase_DatasetResourceID", GLOBAL_SCOPE);
        
        // Add all the possible variable attributes.
        Attribute.create(theCDF, "CATDESC", VARIABLE_SCOPE);
        Attribute.create(theCDF, "DEPEND_0", VARIABLE_SCOPE);
        Attribute.create(theCDF, "DEPEND_1", VARIABLE_SCOPE);
        Attribute.create(theCDF, "DEPEND_2", VARIABLE_SCOPE);
        Attribute.create(theCDF, "DEPEND_3", VARIABLE_SCOPE);
        Attribute.create(theCDF, "DICT_KEY", VARIABLE_SCOPE);
        Attribute.create(theCDF, "DISPLAY_TYPE", VARIABLE_SCOPE);
        Attribute.create(theCDF, "FIELDNAM", VARIABLE_SCOPE);
        Attribute.create(theCDF, "FILLVAL", VARIABLE_SCOPE);
        Attribute.create(theCDF, "FORMAT", VARIABLE_SCOPE);
        Attribute.create(theCDF, "LABLAXIS", VARIABLE_SCOPE);
        Attribute.create(theCDF, "LABL_PTR_1", VARIABLE_SCOPE);
        Attribute.create(theCDF, "LABL_PTR_2", VARIABLE_SCOPE);
        Attribute.create(theCDF, "LABL_PTR_3", VARIABLE_SCOPE);
        Attribute.create(theCDF, "UNITS", VARIABLE_SCOPE);
        Attribute.create(theCDF, "UNIT_PTR", VARIABLE_SCOPE);
        Attribute.create(theCDF, "VALIDMIN", VARIABLE_SCOPE);
        Attribute.create(theCDF, "VALIDMAX", VARIABLE_SCOPE);
        Attribute.create(theCDF, "VAR_TYPE", VARIABLE_SCOPE);
        Attribute.create(theCDF, "SCALETYP", VARIABLE_SCOPE);
        Attribute.create(theCDF, "SCAL_PTR", VARIABLE_SCOPE);
        Attribute.create(theCDF, "VAR_NOTES", VARIABLE_SCOPE);
        
        try {

            if (cdf3.isSelected()) {

                new gsfc.spdf.istp.TerrestrialTime2000(theCDF);
            }
            else {

                new gsfc.spdf.istp.Epoch8(theCDF);
            }
        }
        catch (ISTPComplianceException e) {

            // should never happen
            e.printStackTrace();
        }
       
      javax.swing.SwingUtilities.invokeLater(new Runnable() {
            
            public void run() {
                
                // Reset the Panels
                globalPanel.resetPanel();
                globalPanel.setGlobalAttributes();
                //        cdfEditor.setCdf(theCDF);
                variablePanel.resetPanel(true);
                enableVarMenu();
                enableSaving(true);
                frame.validate();
        
                // Update the file stats
                logoPanel.updateFileStats();
            }
        });    
     
    }


    /**
     * Get the user's choice of array majority.
     *
     * @return the user's choice for array majority (
     *     CDFConstants.COLUMN_MAJOR or CDFConstants.ROW_MAJOR).
     */
    private long getArrayMajority() {
        
        ButtonGroup buttonGroup = new ButtonGroup();

        JRadioButton column = new JRadioButton(
            "Column (better for Fortran, IDL, MATLAB environments)");
        JRadioButton row = new JRadioButton(
            "Row (better for C, C++, Python environments)");

        column.setSelected(true);   

        buttonGroup.add(column);
        buttonGroup.add(row);

        Object[] message = {
            "Choose the array majority of the CDF file:", 
            column, row
        };

        int selection = JOptionPane.showConfirmDialog(null, message,
            "Array Majority Selection",
            JOptionPane.DEFAULT_OPTION, 
            JOptionPane.QUESTION_MESSAGE);  

        return column.isSelected() ? 
               CDFConstants.COLUMN_MAJOR : CDFConstants.ROW_MAJOR;
    }


    /**
     * Will perform a save or save as.
     *
     * @param saveAs if true that prompt user for a file name
     * @param closeFile if true then close the file after saving
     * @return true if save successfully completed or if there is no
     *             current file to save.  false if operation fails or 
     *             is cancelled.
     */
    public boolean saveCurrentFile(boolean saveAs, boolean closeFile) {
	
        if (theCDF == null) {

            return true;
        }
	File selectedFile = null;
	String name = null;
	boolean saveIt = true;
        
        if(checkCompliance() != true) {
            
             int result = JOptionPane.showConfirmDialog(frame,
                 (theFile != null && 
                  !theFile.getName().equalsIgnoreCase("New File")? 
                 theFile.getName() : "The unnamed file") +
                 " is not ISTP Compliant.\nDo you want to "+
                 "continue saving ?",
                 "Save Non-Compliant File",
                 JOptionPane.YES_NO_OPTION);

            if (result != JOptionPane.YES_OPTION) {
                           
                return false;
            }                        
        }

	if (saveAs) {
             
	    JFileChooser chooser = getFileChooser();
            chooser.setCurrentDirectory(new File(chooserCurrentDir));
        
	    chooser.rescanCurrentDirectory();

            selectedFile = getFile();

	    String newName = getLogicalFilename();

	    if (newName != null) {

                selectedFile = new File(newName + 
                                        getCurrentFileExtension());
            }

            chooser.setSelectedFile(selectedFile);

            javax.swing.filechooser.FileFilter filters[] =
                chooser.getChoosableFileFilters();

            for (int i = filters.length - 1; i >= 0; i--) {

                if (filters[i].accept(selectedFile)) {

                    chooser.setFileFilter(filters[i]);
                    break;
                }
            }

            boolean invalidFileType;
            do {
                invalidFileType = false;

                int retVal = chooser.showSaveDialog(frame);

                if (retVal == JFileChooser.APPROVE_OPTION) {

                    selectedFile = chooser.getSelectedFile();

                    String fileName = selectedFile.getName();

                    String ext = getExtension(fileName);
                                       // filename extension

                    if (ext == null) {

                        ext = ".cdf";
                        fileName = selectedFile.getPath()+ ext;
                       
                        selectedFile = new File(fileName);
                        chooser.setSelectedFile(selectedFile);
                    }
                    else if (ext.equals(".")) {

                        ext = "cdf";
                        fileName = selectedFile.getPath()+ ext;
                        selectedFile = new File(fileName);
                        chooser.setSelectedFile(selectedFile);
                    }

                    if (validFilename(fileName)) {

                        if (selectedFile.exists()) {

                            int result = JOptionPane.showConfirmDialog(
                                          frame,
					  selectedFile.getName() + 
					  " exists.\nDo you want to "+
					  "overwrite it?",
					  "Overwrite?",
					  JOptionPane.YES_NO_OPTION);

                            if (result != JOptionPane.YES_OPTION) {
                           
                                saveIt = false;
                                closeFile = false;
		            }
                        }; // endif selectedFile.exists()
                    }
                    else {

                        invalidFileType = true;
                    }; 
                } 
                else {
           
                    saveIt = false;
                    closeFile = false;

                    return false;
                }
            }
            while (invalidFileType);

            chooserCurrentDir = chooser.getCurrentDirectory().getPath();
	} 
        else {

	    selectedFile = getFile();
        }
	    
	try {
	    if (saveIt) {
		System.err.println("Saving file "+selectedFile.getPath());
		saveFile(selectedFile.getPath());
	    }

	    if (closeFile) {

		closeFile();
            }
	} 
        catch (CDFException exc) {

            JOptionPane.showMessageDialog(frame,
                "File not saved due to CDF Error: "+
                exc.getCurrentStatus(),
                "SKTEditor: CDFException", JOptionPane.ERROR_MESSAGE);

            return false;
	} 
        catch (IOException ioe) {

            JOptionPane.showMessageDialog(frame,
                "I/O Error.\n"+ ioe.getMessage(),
                "SKTEditor: IOException", JOptionPane.ERROR_MESSAGE);

            return false;
	} 
        catch (InterruptedException ie) {

            ie.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                "Save was interupted, please try again",
                "SKTEditor: InterruptedException",
                JOptionPane.ERROR_MESSAGE);

            return false;
	}
     
        return true;
    }


    /**
     * Provides the extension of the given filename.
     *
     * @param filename name of file.
     * @return filename extension (including '.') or null if the
     *     given filename is null or there is no extension in the 
     *     given filename.
     */
    private static String getExtension(String filename) {

        if (filename == null) {

            return null;
        }
        int extIndex = filename.lastIndexOf('.');
                                       // index of beginning of 
                                       // filename extension
        if (extIndex < 0) {

            return null;
        }

        return filename.substring(extIndex);
    }


    /**
     * Provides the filename (including any path) without the 
     * extension.
     *
     * @param filename name of file.
     * @return filename excluding any extension.
     */
    private static String getRootFilename(String filename) {

        if (filename == null) {

            return null;
        }
        int extIndex = filename.lastIndexOf('.');
                                       // index of beginning of 
                                       // filename extension
        if (extIndex < 0) {

            return filename;
        }

        return filename.substring(0, extIndex);
    }


    /**
     * Checks the validity of the given filename and displays an 
     * information dialog for invalid values.  An extension is not 
     * required but if one is included, it must be "cdf" (or "skt" on 
     * Unix platforms).
     *
     * @param name filename to check
     * @return true if the name if valid, otherwise false
     */
    protected boolean validFilename(String name) {

        if (name == null) {

            return false;
        }

        String ext = getExtension(name).toLowerCase();
                                       // filename extension

        if (name.equalsIgnoreCase(ext)) {

            JOptionPane.showMessageDialog(frame, 
                "Missing filename.  Please enter a valid filename",
                "Invalid Filename", JOptionPane.INFORMATION_MESSAGE);

            return false;
        }
        else if (ext != null) {

            if (ext.equals(".skt") || ext.equals(".nonrvskt") || 
                ext.equals(".nc")) {
            
               return true;
            }
            else if (!ext.equals(".") && !ext.equals(".cdf")) {

                JOptionPane.showMessageDialog(frame, 
                        "Invalid filename extension.",
                        "Invalid Filename",
                        JOptionPane.INFORMATION_MESSAGE);

                return false;
            }
        }

        return true;
    }


    /**
     * Create the name of a file in the default temporary-file 
     * directory with the given extension.  While the the following
     * guarantees were true at the time of this call, they may
     * not be true by the time the caller uses the returned filename.
     * <ol>
     *   <li> The file denoted by the returned abstract pathname did 
     *        not exist before this method was invoked, and
     *   <li> This method will not return the same abstract pathname 
     *        again in the current invocation of the virtual machine.
     * </ol>
     * 
     * @param suffix The suffix string to be used in generating the 
     *            filename; may be null, in which case the suffix 
     *            ".tmp" will be used.
     * @return An abstract pathname of a temporary file.
     * @throws IOException if an I/O exception occurs.
     */
    private static String createTempFilename(String suffix) 
        throws IOException {

        File tempFile = File.createTempFile("skteditor", suffix);

        tempFile.delete();

        // Since the file was deleted, it may not be unique when the
        // caller tries to use it.

        return tempFile.getCanonicalPath();
    }


    public void saveFile(String name)
        throws CDFException, IOException, InterruptedException {

        // Update the hidden global attributes
        updateLogicalAttributes();
        
        // Save the current variable if any
        variablePanel.saveVariableChanges();

        Variable selectedVar = variablePanel.getSelectedVar();
                                       // currently selected variable,
                                       // if any, to restore selection
                                       // after close/re-open
        String selectedVarName = null; // name of selected variable
        if (selectedVar != null) {

            selectedVarName = selectedVar.getName(); 
        }

        if (name != null) {

            try {

                cdfHistory.add(name);
            }
            catch (BackingStoreException e) {

                System.err.println("Failed to save filename " + 
                    name + " to history backing store: " + 
                    e.getMessage());
            }

            filename = name;
            theFile = new File(filename);
            rootfilename = getRootFilename(name);
            
            // Set the title to display the filename
            getFrame().setTitle("SKTEditor: " + theFile.getName());
        }
        
        // commit changes to working file

        ext = getExtension(name);

        theCDF.close(); 

        // Copy changes to the file
        if (ext.equals(".skt") || ext.equals(".nonrvskt")) {

            int sktValues = ext.equals(".skt") ? 
                    CDFTools.NRV_VALUES : CDFTools.NO_VALUES;
                                       // indicates what variable 
                                       // values to include in skt
            try {
                
                CDFTools.skeletonTable(rootfilename, 
                    workingRootFilename, true, false, false, false, 
                    false, false, sktValues, null, 2,
                    CDFTools.NO_REPORTS, null);


                if (ext.equals(".nonrvskt")) {

                    File sktFile = new File(rootfilename + ".skt");
                    File noNrvSktFile = 
                        new File(rootfilename + ".nonrvskt");

                    if (!sktFile.renameTo(noNrvSktFile)) {

                        JOptionPane.showMessageDialog(frame,
                            "An error occurred when attempt to save " +
                            "the nonrvskt file.",
                            "Save as nonrvskt error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            catch (IOException e) {
                
                if (e.getMessage().indexOf("not found") > -1 ||
                    e.getMessage().indexOf("error=2") > -1) {
                    
                    JOptionPane.showMessageDialog(frame,
                                     NO_SKT_SUPPORT_MSG,
                                     "Save as skeleton error", 
                                     JOptionPane.ERROR_MESSAGE);
                    
                    return;
                }
                else {
                    
                    throw e;
                }
            };
        }
        else if (ext.equals(".nc")) {
            
            try {
                
                if (CDFTools.cdfToNetCdf(workingRootFilename, 
                        rootfilename) != 0) {

                    JOptionPane.showMessageDialog(frame,
                        "An error occurred when attempt to save " +
                        "the netcdf file.\nSee the Java console " +
                        "for details.",
                        "Save as netCDF error", 
                        JOptionPane.ERROR_MESSAGE);

                    return;
                }
            }
            catch (IOException e) {
                
                if (e.getMessage().indexOf("not found") > -1 ||
                    e.getMessage().indexOf("error=2") > -1) {
                    
                    JOptionPane.showMessageDialog(frame,
                                     NO_NC_SUPPORT_MSG,
                                     "Save as netCDF error", 
                                     JOptionPane.ERROR_MESSAGE);
                    
                    return;
                }
                else {
                    
                    throw e;
                }
            };
        }
        else {
            
            copyFile(workingRootFilename + ".cdf", 
                     rootfilename + ".cdf");
        };
        
        updateLogicalDescription = false;
        
     //   checkCompliance();
        
        save.setEnabled(true);
        
        saveAsOnClose = false;

        theCDF = CDF.open(workingRootFilename);
        globalPanel.resetPanel();
        variablePanel.resetPanel(true);
        enableSaving(false);
        enableVarMenu();
        globalPanel.setGlobalAttributes();
        if (selectedVarName != null) {

            selectedVar = theCDF.getVariable(selectedVarName);
            if (selectedVar != null) {

                variablePanel.updateVarPanel(selectedVar);
                variablePanel.reselectCurrentVariable();
            }
        }
        
        // Update the file stats
        logoPanel.updateFileStats();
    }
    


    public void closeFile()
    throws
    CDFException,
    IOException,
    InterruptedException {
        //        cdfEditor.saveChanges();
        theCDF.close();
        
        theCDF = null;
        globalPanel.resetPanel();
        cdfEditor.setCdf(null);
        variablePanel.resetPanel(true);
        statuspanel.clearMessages();
        logoPanel.resetPanel();
        disableSaving();
        disableVarMenu();
      
        // Reset the title
        getFrame().setTitle("SKTEditor");
        
        System.err.println("deleting working file");
        new File(workingRootFilename + ".cdf").delete();
        if (ext.equals(".skt")) {

            new File(workingRootFilename + ".skt").delete();
        }
    }
    
    private void copyFile(String source, String destination)
    throws IOException {

        // Create a working file
        File inputFile = new File(source);
        File outputFile = new File(destination);
        
        BufferedInputStream in = new BufferedInputStream(
        new FileInputStream(inputFile));
        BufferedOutputStream out = new BufferedOutputStream(
        new FileOutputStream(outputFile));
        int c;
        
        while ((c = in.read()) != -1)
            out.write(c);
        
        in.close();
        out.close();

        /* when we require Java 1.7, we can replace the above 
           code with the following:
        import java.nio.file.StandardCopyOption;
        import java.nio.file.Files;
        import java.nio.file.Path;
        import java.nio.file.Paths;

        Path in = Paths.get(source);
        Path out = Paths.get(destination);
        Files.copy(in, out, StandardCopyOption.COPY_ATTRIBUTES);
        */
    }
    
    
    ///////////////////////////////////////
    //                                   //
    //         Utility Methods           //
    //                                   //
    ///////////////////////////////////////
    
    public boolean shouldSaveAs() {
        return saveAsOnClose;
    }
    
    public String getLogicalFilename() {
        updateLogicalAttributes();        
        return logicalFilename;
    }
    
    /**
     * Tab Listener
     */
    private void createTabListener() {
        // add listener to know when we've been shown
        ChangeListener changeListener = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JTabbedPane tab = (JTabbedPane) e.getSource();
                int index = tab.getSelectedIndex();
                Component currentPage = tab.getComponentAt(index);
                if (index == 0) {
                    ((InfoPanel)currentPage).updateFileStats();
                    setMenuBar(0);
                }
                if (index == 1)
                    setMenuBar(1);
                if (index ==2)
                    setMenuBar(2);
            }
        };
        tabbedPane.addChangeListener(changeListener);
    }
    
    private void setMenuBar(int index) {
        menuBar.removeAll();
        switch(index) {
            case 2:
                menuBar.add(file);
                menuBar.add(editMenu);
                menuBar.add(tools);
                menuBar.add(variableMenu);
                menuBar.add(helpMenu);
                menuBar.repaint();
                break;
            case 1:
                menuBar.add(file);
                menuBar.add(editMenu);
                menuBar.add(tools);
                menuBar.add(helpMenu);
                menuBar.repaint();
                break;
            case 0:
                menuBar.add(file);
                menuBar.add(helpMenu);
            default:
                menuBar.repaint();
        }
    }
    
    public static void setStatus(String message, int status,
    boolean toStdOut, boolean alert) {
        StringBuffer mb = new StringBuffer();
        mb.append(message);
        if (toStdOut) {
            System.out.println(mb.toString());
            
            if (status != StatusBar.INFO)
                mb.append("  See messages for more details.");
        }
        
        statuspanel.setStatus(mb.toString(), status, alert);
    }
    
    public static void setWaitCursor() {
        instance.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }
    
    public static void setDefaultCursor() {
        instance.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
    
    public JFileChooser getFileChooser() {
        return chooser;
    }
    
    public File getFile() {
        return theFile;
    }


    /**
     * Get the filename extension of the current file.
     *
     * @return filename extension of the current file.  null if
     *     current file cannot be determined.
     */
    public String getCurrentFileExtension() {

        try {

            return getExtension(getFile().getCanonicalPath());
        }
        catch (IOException e) {

            return null;
        }
    }

    
    public void enableSaving(boolean newfile) {
        save.setEnabled(!newfile);
        saveAs.setEnabled(true);
        close.setEnabled(true);
        complianceChecksMenu.setEnabled(true);
        deleteDataValuesMenuItem.setEnabled(true);
        extractNotesMenuItem.setEnabled(true);
        extractAttributesMenuItem.setEnabled(true);
        cdfEditorMenuItem.setEnabled(true);
    }
    
    public void enableVarMenu() {
        Component [] mis = variableMenu.getMenuComponents();
        for (int i = 0; i< mis.length; i++)
            mis[i].setEnabled(true);
    }
    
    public void disableSaving() {
        save.setEnabled(false);
        saveAs.setEnabled(false);
        close.setEnabled(false);
        complianceChecksMenu.setEnabled(false);
        deleteDataValuesMenuItem.setEnabled(false);
        extractNotesMenuItem.setEnabled(false);
        extractAttributesMenuItem.setEnabled(false);
        cdfEditorMenuItem.setEnabled(false);
    }
    
    public void disableVarMenu() {
        Component [] mis = variableMenu.getMenuComponents();
        for (int i = 0; i< mis.length; i++)
            mis[i].setEnabled(false);
    }
    
    public static Dimension getScreenSize() {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }
    
    /**
     * Gets the specified icon using the SKTEditor's standard property values
     * and resource search algorithm.
     *
     * @param imageName the name of the icon image file
     * @return the requested icon or null if it couldn't be found
     */
    public static Icon getIcon(String imageName) {
        Icon icon = null;
        String iconPath = propertyPath +
        appProperties.getProperty("icon.path");
        try {
            
            icon = new ImageIcon(AbstractFileAction.class.getResource(
            iconPath + imageName));
        }
        catch(NullPointerException e) {
            
            // don't care, just return null
        };
        
        return icon;
    }
    
    public static boolean loadNativeCdfLibs() {
        
        //
        // These loadLibrary calls aren't necessary on any platform except
        // Windows Web Start where its absolutely necessary as of Web
        // Start <= 1.2.  Note that they are not necessary on Windows or
        // any other tested platform when the application is run outside
        // of Web Start.  These comments do not imply anything specifying
        // where or how the libraries are located.  That is, outside of
        // Web Start, you'll likely have to specify the locations of the
        // libraries through the a platform specific mechanism (e.g, setting
        // LD_LIBRARY_PATH on Solaris and Linux and setting the PATH
        // environment variable on MS Windows).
        //
        try {
            
            String cdfLib1 = System.getProperty("jnlp.cdfLib1");
            // first CDF native library name
            if (cdfLib1 == null) {

                cdfLib1 = System.getProperty("cdfLib1");// old name
            }
            
            if (cdfLib1 != null) {
                
                System.loadLibrary(cdfLib1);
            }
            
            String cdfLib2 = System.getProperty("jnlp.cdfLib2");
            // second CDF native library name
            if (cdfLib2 == null) {

                cdfLib2 = System.getProperty("cdfLib2");// old name
            }
            
            if (cdfLib2 != null) {
                
                System.loadLibrary(cdfLib2);
            }
        }
        catch(SecurityException e) {
            
            System.err.println("Security violation loading CDF library:" +
                               e.getMessage());
            return false;
        }
        catch(UnsatisfiedLinkError e) {
            
            System.err.println("Unable to load CDF library:" +
                               e.getMessage());
            return false;
        };
        
        return true;
    }


    /**
     * Sets the checksum option on the given CDF to the value specified
     * by the <code>cdf.checksum</code> property.  This method is 
     * designed to compile and run under versions of the CDF library 
     * prior to 3.1 which don't support the checksum option.
     *
     * @param cdf CDF whose checksum option is to be set
     */
    private static void setChecksum(CDF cdf) {

        String checksumOption = System.getProperty("cdf.checksum");
                                       // desired checksum option 
                                       // setting
        if (checksumOption == null) {

            return;
        }
        else if (!checksumOption.equalsIgnoreCase("MD5")) {

            System.err.println("Unrecognized cdf.checksum property " +
                "value " + checksumOption);
            return;
        }

        //
        // When not required to compile with CDF < 3.2, the following
        // reflection code can be replaced with
        // cdf.setChecksum(CDFConstants.MD5_CHECKSUM);
        //
        try {

            Class[] parameters = new Class[] {Long.TYPE};
                                       // parameters to CDF.setChecksum
                                       // method
            Method setChecksumMethod = 
                cdf.getClass().getDeclaredMethod("setChecksum", 
                                                 parameters);
                                       // CDF.setChecksum method (CDF
                                       // version > 3.1).
            Field md5ChecksumField =
                CDFConstants.class.getDeclaredField("MD5_CHECKSUM");
                                       // CDFConstants.MD5_CHECKSUM 
                                       // field
            Long md5ChecksumValue =
                new Long(md5ChecksumField.getLong(CDFConstants.class));
                                       // CDFConstants.MD5_CHECKSUM
                                       // value
            setChecksumMethod.invoke(cdf, 
                new Object[] {md5ChecksumValue});
                                       // theCDF.setChecksumMethod()
                                       // (wrapped) result
        }
        catch (NoSuchMethodException e) {

            // old version of CDF.  Ignore
        }
        catch (NoSuchFieldException e) {

            // this shouldn't happen because NoSuchMethodException
            // would happen first
            System.err.println("NoSuchFieldException: " +
                e.getMessage());
        }
        catch (SecurityException e) {

            System.err.println("SecurityException while reflecting " +
                "on the CDF library" + e.getMessage());
        }
        catch (IllegalAccessException e) {

            System.err.println("IllegalAccessException while " +
                "reflecting on the CDF library" + e.getMessage());
        }
        catch (InvocationTargetException e) {

            System.err.println("InvocationTargetException while " +
                "reflecting on the CDF library" + e.getMessage());
        }
    }


    /**
     * Determines if an immediately prior CDF.open operation was
     * successful.  If the open operation detected a checksum 
     * verification failure, this method displays a dialog asking
     * whether to continue.  This method is designed to compile and 
     * run under versions of the CDF library prior to 3.1 which 
     * don't support the checksum option.
     *
     * @param cdf CDF that was just opened
     * @return true if the open (including checksum verification) was
     *     successful or the user wishes to continue with a checksum
     *     verification failure, false if any other error is detected
     *     or if the user chooses to abort the open due to the checksum
     *     error
     */
    private static boolean wasOpenSuccessful(CDF cdf) {

        long openStatus = cdf.getStatus();
                                       // status of last CDF call

        if (openStatus == CDF.CDF_OK) {

            return true;
        }
        //
        // When not required to compile with CDF < 3.2, the following
        // reflection code can be eliminated
        //
        try {

            Field checksumErrorField =
                CDFConstants.class.getDeclaredField("CHECKSUM_ERROR");
                                       // CDFConstants.CHECKSUM_ERROR 
                                       // field
            long checksumErrorValue = 
                checksumErrorField.getLong(CDFConstants.class);
                                       // CDFConstants.CHECKSUM_ERROR
                                       // value

            if (openStatus == checksumErrorValue) {

                Object[] options = {"Continue", "Abort"};
                                       // dialog options
                int option = JOptionPane.showOptionDialog(null,
                    "The file's checksum indicates that the file is\n" +
                    "corrupt.  Continuing to open this file may result\n" +
                    "in an application failure.  Do you want to continue\n" +
                    "attempting to access this file anyway?",
                    "CDF Checksum Error", JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE, null, options, options[1]);

                if (option == JOptionPane.YES_OPTION) {

                    return true;
                }
                else {

                    return false;
                }

            }
        }
        catch (NoSuchFieldException e) {

            // old version of CDF.  Ignore
        }
        catch (SecurityException e) {

            System.err.println("SecurityException while reflecting " +
                "on the CDF library" + e.getMessage());
        }
        catch (IllegalAccessException e) {

            System.err.println("IllegalAccessException while " +
                "reflecting on the CDF library" + e.getMessage());
        }

        return false;
    }


    /**
     * Checks that the CDF components used by this application are
     * sufficent.  If an issue is discovered, a message dialog is shown.
     *
     * @return true if all CDF components are sufficient.  Otherwise
     *     false.
     */
    private boolean isCdfVersionsOk() {

        boolean versionsOk = true;     // flag indicating whether the
                                       // versions are sufficient
        String libVer = null;          // cdf library version

        try {

            libVer = CDF.getLibraryVersion().trim();
        }
        catch (CDFException e) {

            System.err.println("CDFException while getting library " +
                "version :" + e.getMessage());

            return versionsOk;
        }

        String skeletonTableVer = CDFTools.getSkeletonTableVersion();
                                       // skeletontable tool version
        String skeletonCdfVer = CDFTools.getSkeletonCdfVersion();
                                       // skeletoncdf tool version
        System.err.println("CDF Library Version: " + libVer);
        System.err.println("SkeletonTable Version: " + skeletonTableVer);

/*
        System.err.println("SkeletonTable SHA-256: " + 
            CDFTools.getToolDigestStr("skeletontable", "SHA-256"));
        System.err.println("SkeletonCDF Version: " + skeletonCdfVer);
        System.err.println("SkeletonCDF SHA-256: " + 
            CDFTools.getToolDigestStr("skeletoncdf", "SHA-256"));
*/

        boolean translationToolsInstalled =
            CDFTools.translationToolsInstalled();
        System.err.println("CDF Translation Tools installed: " + 
            translationToolsInstalled);
/*
        if (translationToolsInstalled) {

            System.err.println("netCDF-to-cdf SHA-256: " + 
                CDFTools.getToolDigestStr("netCDF-to-cdf", "SHA-256"));
            System.err.println("cdf-to-netCDF SHA-256: " + 
                CDFTools.getToolDigestStr("cdf-to-netCDF", "SHA-256"));
        }
*/

        if (libVer.startsWith("2.")) {

            JOptionPane.showMessageDialog(frame,
                "<html>A version 2 CDF library has been " +
                "detected in<br>" +
                "your environment. While this editor can create version<br>" +
                "2 CDF files, it can only do so using version 3 or " +
                "higher<br> of the CDF library.  Please upgrade your CDF " +
                "library.<br>  The CDF components and their " +
                "version information is shown<br>below:" +
                "<ul><li>CDF Library: " + libVer + "</li>" +
                    "<li>skeletonTable: " + skeletonTableVer + "</li>" +
                    "<li>skeletonCDF: " + skeletonCdfVer + "</li>" +
                "</ul></html>",
                "CDF Component Configuration Issue",
                JOptionPane.WARNING_MESSAGE);

            return false;
        }

        if (skeletonTableVer == null || skeletonCdfVer == null) {

            // A platform or installation that does not support these
            // tools so no version mismatch.
 
            return versionsOk;
        }

        if (!libVer.equals(skeletonTableVer) ||
            !libVer.equals(skeletonCdfVer)) {

            String msg =
                "<html>Multiple versions of CDF components have been " +
                "detected in<br>" +
                "your environment. This may cause a problem if you " +
                "read<br>" +
                "and/or write skeleton files (.skt).<br>";

            if (isJnlpEnvironment()) {

                msg += "In most cases, upgrading your installation " +
                "of CDF<br>" +
                "(https://cdf.gsfc.nasa.gov/) will " +
                "resolve this issue.<br>";
            }
            else {

                msg += "To avoid this problem, it is recommended " +
                    "that you reconfigure<br>" +
                    "your environment to have consistent CDF " +
                    "components for use<br>" +
                    "by this application.<br>";
            }
            msg += "The CDF components and their " +
                "version information is shown<br>below:" +
                "<ul><li>CDF Library: " + libVer + "</li>" +
                    "<li>skeletonTable: " + skeletonTableVer + "</li>" +
                    "<li>skeletonCDF: " + skeletonCdfVer + "</li>" +
                "</ul>Do you wish to continue anyway?</html>";

            int reply = JOptionPane.showConfirmDialog(frame, msg,
                "CDF Component Configuration Issue",
                JOptionPane.YES_NO_OPTION);

            if (reply != JOptionPane.YES_OPTION) {

                versionsOk = false;
            }
        }

/*  We couldn't agree on the wording of these dialogs or the usefulness
    of this check so leave it commented out.

        CDFTools.Integrity toolsIntegrity = CDFTools.getIntegrity();
                                       // CDF tools integrity
        if (toolsIntegrity.status != CDFTools.Integrity.Status.GOOD) {

            String msg = "<html>" + toolsIntegrity.description;

            if (toolsIntegrity.status == CDFTools.Integrity.Status.BAD) {

                msg += "<br>This may be due to a corrupt or " +
                    "non-standard installation of CDF library and " +
                    "tools.<br> If you know you are using a " +
                    "non-standard CDF installation, you may " +
                    "continue.<br>  Otherwise, you may want to " +
                    "re-install CDF.<br>";
            }
            else {  // Integrity.Status.UNKNOWN

                msg += "<br>This is caused by a build problem with " +
                    "SKTEditor or a Java configuration issue.<br>" +
                    "Please report the problem to " +
                    "gsfc-spdf-support@lists.nasa.gov.<br>";
            }
            msg += "<br>Do you want to continue anyway?</html>";

            int reply = JOptionPane.showConfirmDialog(frame, msg,
                "CDF Tools Integrity Issue",
                JOptionPane.YES_NO_OPTION);

            if (reply != JOptionPane.YES_OPTION) {

                versionsOk = false;
            }
        }
*/

        if (skeletonTableVer.equals("3.2") ||
            skeletonTableVer.equals("3.2.0") ||
            skeletonTableVer.equals("3.2.1") ||
            skeletonTableVer.equals("3.2.2") ||
            skeletonCdfVer.equals("3.2") ||
            skeletonCdfVer.equals("3.2.0") ||
            skeletonCdfVer.equals("3.2.1") ||
            skeletonCdfVer.equals("3.2.2")) {

            JOptionPane.showMessageDialog(frame,
                "<html>You are using a version of CDF skeletontable " +
                "and/or skeletoncdf<br>which can corrupt files. This " +
                "may cause a problem if you<br>read and/or write " +
                "skeleton files (.skt).  To avoid this problem,<br>" +
                "it is recommended that you reconfigure your " +
                "environment<br>to have newer versions of these CDF " +
                "components for use by <br>this application. The CDF " +
                "components and their version<br>information is " +
                "shown below:" +
                "<ul><li>CDF Library: " + libVer + "</li>" +
                    "<li>skeletonTable: " + skeletonTableVer + "</li>" +
                    "<li>skeletonCDF: " + skeletonCdfVer + "</li>" +
                "</ul></html>",
                "CDF Component Configuration Issue",
                JOptionPane.WARNING_MESSAGE);

            versionsOk = false;
        }

        return versionsOk;
    }


    /**
     * Determines whether this application is executing in a JNLP (Web 
     * Start) environment.
     *
     * @return true if executing in a JNLP environment.  Otherwise,
     *             false.
     */
    private static boolean isJnlpEnvironment() {

        try {

            Class clazz = Class.forName("javax.jnlp.BasicService");

            return true;
        }
        catch (ClassNotFoundException e) {

            return false;
        }
    }
}
