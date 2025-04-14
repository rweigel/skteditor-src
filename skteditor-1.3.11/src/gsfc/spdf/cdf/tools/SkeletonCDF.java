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
 * $Id: SkeletonCDF.java,v 1.4 2022/03/24 10:38:25 btharris Exp $
 */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*; 
import java.awt.datatransfer.*;

/** SkeletonCDF produces a CDF from a skeleton table. A skeleton table is a 
    text file which is read by the SkeletonCDF program to build a skeleton CDF.
 */

public class SkeletonCDF extends JDialog 
			  implements ActionListener, ItemListener, 
				     MouseListener, FocusListener, Runnable {

/** The dialog for user interface. */
    JDialog dialog = new JDialog();

/** The panel for text fields and selectable buttons which goes to dialog's
    north portion. */
    JPanel panel1 = new JPanel(new GridBagLayout());
/** The panel containing all checkboxes goes to dialog's center portion. */
    JPanel panel2 = new JPanel(new GridBagLayout());
/** The panel containing panel31 and text area goes to dialog's south portion. */
    JPanel panel3 = new JPanel(new BorderLayout());
/** The panel containing enter/help/quit buttons. */
    JPanel panel31 = new JPanel(new GridLayout(1, 3, 50, 20));
    JPanel tpanel1 = new JPanel(new BorderLayout());
    JPanel tpanel2 = new JPanel(new BorderLayout());

/** "Use FILLVAL" checkbox. */
    JCheckBox ufc = new JCheckBox("Use FILLVAL");
/** "Report Info's" checkbox. */
    JCheckBox ric = new JCheckBox("Report Info's");
/** "Report Warnings" checkbox. */
    JCheckBox rwc = new JCheckBox("Report Warnings", true);
/** "Report Errors" checkbox. */
    JCheckBox rec = new JCheckBox("Report Errors", true);
/** "-0.0 to 0.0" checkbox. */
    JCheckBox zzc = new JCheckBox("-0.0 to 0.0");
/** "Display Statistics" checkbox. */
    JCheckBox dsc = new JCheckBox("Display Statistics");
/** "Delete Existing" checkbox. */
    JCheckBox dec = new JCheckBox("Delete Existing");
/** "Log Progress" checkbox. */
    JCheckBox lpc = new JCheckBox("Log Progress");
/** "About" checkbox. */
    JCheckBox abc = new JCheckBox("About");
/** "Run it Batch" checkbox. */
    JCheckBox bac = new JCheckBox("Run it Batch", false);

/** A combo box for zMode. */
    JComboBox zmode = new JComboBox();

/** "Run SkeletonCDF" button. */
    JButton entrb = new JButton("Run SkeletonCDF");
/** "Help" button. */
    JButton helpb = new JButton("Help");
/** "Quit" button. */
    JButton quitb = new JButton("Quit");
/** "Select1" button for selecting the source skeleton table file. */
    JButton slt1 = new JButton("Select1");
/** "Select2" button for selecting the destination CDF file. */
    JButton slt2 = new JButton("Select2");

/** Text area for information/results from a command execution. */
    JTextArea info = new JTextArea(20,40);

/** Text field for entering optional cache sizes. */
    JTextField cachesizes = new JTextField("",20);
/** Text field for entering the optional destination CDF file name. */
    JTextField cdf = new JTextField("", 40);
/** Text field for entering the mandatory source skeleton table file name. */
    JTextField sklt = new JTextField("", 40);

/** A label of "Destination CDF:" for the text field cdf. */
    JLabel CDF = new JLabel("CDF: ");
/** A label of "Destination CDF:" for the text field sklt. */
    JLabel SKLT = new JLabel("Skeleton: ");
/** A label of "Cache Sizes:" for the text field cachesizes. */
    JLabel CASS = new JLabel("Cache Sizes: ");
/** A label of "zMode:" for the combobox zmode. */
    JLabel ZMODE = new JLabel("zMode: ");

/** Scroll pane for the text area. */
    JScrollPane scroller;

/** Hold valid extensions for the CDF files for the file chooser. */
    String[] cdfs = {"cdf", "CDF"};
/** Hold valid extensions for the skeleton table files for the file chooser. */
    String[] sklts = {"skt", "SKT"};
/** A file filter for the CDF files for the file chooser. */
    SimpleFileFilter filter1 = new SimpleFileFilter(cdfs,
                                            " CDF files (*.cdf, *.CDF)");
/** A file filter for the skeleton table files for the file chooser. */
    SimpleFileFilter filter2 = new SimpleFileFilter(sklts,
                                            " skeleton tables (*.skt, * .SKT)");

/** The command string. */
    String command = "skeletoncdf ";
    String commandX;
/** Environment variable for the current directory. */
    String currentdir = System.getProperty("user.dir");
/** Environment variable for CDF_BASE. */
    String cdfbase = System.getProperty("CDF_BASE");
/** The file separator. */
    String separator = System.getProperty("file.separator");
/** Valid zModes for combobox zmode. */
    String[] zmodex = {"0", "1", "2"};
    String struf = " ";
    String strri = "";
    String strrw = "w";
    String strre = "e";
    String strds = " ";
    String strde = " ";
    String strlp = " ";
    String strzz = " ";
    String strzm = "";
    String strba = " ";
    String report, Zmode, caches, temp;
    String cdfspec = "";
    String skltspec = "";
    Runtime runtime = Runtime.getRuntime();
    Process process = null;
    int hlp = 0;
    int iba = 0;
    boolean running = true;

/** The text field is currently getting the focus. */
    JTextField focused = null;
/** The text field previously was focused. */
    JTextField prevfocused = null;
/** Menu bar. */
    JMenuBar menuBar = new JMenuBar();
/** A menu: File. */
    JMenu menuFile = new JMenu("File");
/** A menu: Edit. */
    JMenu menuEdit = new JMenu("Edit");
/** A menu: Help. */
    JMenu menuHelp = new JMenu("Help");
/** An Edit menu item: Copy. */
    JMenuItem menuEditCopy = new JMenuItem("Copy");
/** An Edit menu item: Cut. */
    JMenuItem menuEditCut = new JMenuItem("Cut");
/** An Edit menu item: Paste. */
    JMenuItem menuEditPaste = new JMenuItem("Paste");
/** A File menu item: Exit. */
    JMenuItem menuFileExit = new JMenuItem("Exit");
/** A Help menu item: Help. */
    JMenuItem menuHelpHelp = new JMenuItem("Help");
    int istart = -1;
    int iend = -1;

    Dimension hpad5 = new Dimension(5,1);
    Dimension hpad10 = new Dimension(10,1);
    Dimension hpad20 = new Dimension(20,1);
    Dimension hpad25 = new Dimension(25,1);
    Dimension hpad30 = new Dimension(30,1);
    Dimension hpad40 = new Dimension(40,1);
    Dimension hpad80 = new Dimension(80,1);
    Dimension vpad5 = new Dimension(1,5);
    Dimension vpad10 = new Dimension(1,10);
    Dimension vpad20 = new Dimension(1,20);
    Dimension vpad25 = new Dimension(1,25);
    Dimension vpad30 = new Dimension(1,30);
    Dimension vpad40 = new Dimension(1,40);
    Dimension vpad80 = new Dimension(1,80);

    Font  defaultFont = new Font("Dialog", Font.PLAIN, 12);
    Font  boldFont = new Font("Dialog", Font.BOLD, 12);
    Font  bigFont = new Font("Dialog", Font.PLAIN, 18);
    Font  bigboldFont = new Font("Dialog", Font.BOLD, 18);
    static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    static int INITIAL_WIDTH = 600;
    static int INITIAL_HEIGHT = 400;
    static boolean defaultModal = false;
    static String  defaultfname = "";
    static File    defaultfile  = null;
    boolean modal;
    int frommain = 0;

    SkeletonCDF(JFrame parentFrame) {
        this(parentFrame, null, defaultModal);
    }

    SkeletonCDF(JFrame parentFrame, String pgm) {
        this(parentFrame, pgm, defaultModal);
    }

    SkeletonCDF(JFrame parentFrame, String pgm, boolean modal) {
        super(parentFrame, modal);
        if (pgm != null) command = pgm;
        else if (cdfbase != null) {
            command = cdfbase + separator + "bin" + separator + "skeletoncdf ";
        }
        dialog = this;
        modal = defaultModal;
    }
/*
    SkeletonCDF(JFrame parentFrame) {
        this(parentFrame, defaultModal, defaultfname, defaultfile);
    }


    SkeletonCDF(JFrame parentFrame, boolean modal) {
        this(parentFrame, modal, defaultfname, defaultfile);
    }


    SkeletonCDF(JFrame parentFrame, boolean modal, String fname) {
        this(parentFrame, modal, fname, defaultfile);
    }

    SkeletonCDF(JFrame parentFrame, boolean modal, String fname, File afile)
{
        super(parentFrame, modal);
        dialog = this;
        this.modal = modal;
        if (fname != null) cdf.setText(fname);
    }
*/

/** Set up the GUI stuff for user selection.
 */

    private void buildGUI() {

        WindowListener l = new WindowAdapter() {
           public void windowClosing(WindowEvent e) {
              if (frommain == 0) {
		if (modal) dialog.dispose();
		else dialog.setVisible(false);
              } else {System.exit(0);}
	   }
        };
        dialog.addWindowListener(l);
        dialog.setTitle("Enter   Parameters/qualifiers  for  SkeletonCDF");
        dialog.setSize(INITIAL_WIDTH, INITIAL_HEIGHT);
        dialog.setLocation(screenSize.width/2 - INITIAL_WIDTH/2,
                          screenSize.height/2 - INITIAL_HEIGHT/2);
	
        // set the menu bar
        dialog.setJMenuBar(menuBar);

        // add the file menu
        menuBar.add(menuFile);

        // add the menu items to file
        menuFile.add(menuFileExit);
        menuFileExit.addActionListener(this);

        // add the edit menu
        menuBar.add(menuEdit);

        // add the menu items to edit
        menuEdit.add(menuEditCopy);
        menuEdit.add(menuEditCut);
        menuEdit.add(menuEditPaste);
        menuEditCopy.addActionListener(this);
        menuEditCut.addActionListener(this);
        menuEditPaste.addActionListener(this);

        // add the help menu
        menuBar.add(menuHelp);

        // add the menu items to help
        menuHelp.add(menuHelpHelp);
        menuHelpHelp.addActionListener(this);

        // Panel 1

        makeJButton(panel1, Box.createRigidArea(vpad5), 0, 0, 1, 1, 0.0, 0.0);
        makeJButton(panel1, SKLT, 0, 1, 1, 1, 0.0, 0.0);
        makeJButton(panel1, sklt, 1, 1, 1, 1, 0.0, 0.0);
        makeJButton(panel1, slt1, 2, 1, 1, 1, 0.0, 0.0);
	makeJButton(panel1, CDF,  0, 2, 1, 1, 0.0, 0.0);
        makeJButton(panel1, cdf,  1, 2, 1, 1, 0.0, 0.0);
        makeJButton(panel1, slt2, 2, 2, 1, 1, 0.0, 0.0);
	makeJButton(panel1, Box.createRigidArea(vpad10), 0, 3, 1, 1, 0.0, 0.0);
        cdf.addActionListener(this);
        cdf.addMouseListener(this);
        cdf.addFocusListener(this);
        sklt.addActionListener(this);
        sklt.addMouseListener(this);
        sklt.addFocusListener(this);
        slt1.addActionListener(this);
        slt2.addActionListener(this);
	CDF.setFont(boldFont);
	cdf.setFont(boldFont);
	SKLT.setFont(boldFont);
  	sklt.setFont(boldFont);
	dialog.getContentPane().add(panel1,BorderLayout.NORTH);
	
        // Panel 2

        // Initialize the zMode choice.
	zmode.setEditable(false);
        for (int i=0; i<zmodex.length; i++) {
            zmode.addItem(zmodex[i]);
        }

	makeJButton(panel2, dec, 0, 0, 1, 1, 0.0, 0.0);
        makeJButton(panel2, ric, 1, 0, 1, 1, 0.0, 0.0);
        makeJButton(panel2, ufc, 2, 0, 1, 1, 0.0, 0.0);
        makeJButton(panel2, lpc, 0, 1, 1, 1, 0.0, 0.0);
        makeJButton(panel2, rwc, 1, 1, 1, 1, 0.0, 0.0);
        makeJButton(panel2, dsc, 2, 1, 1, 1, 0.0, 0.0);
        makeJButton(panel2, zzc, 0, 2, 1, 1, 0.0, 0.0);
        makeJButton(panel2, rec, 1, 2, 1, 1, 0.0, 0.0);
        makeJButton(panel2, abc, 2, 2, 1, 1, 0.0, 0.0);
        tpanel1.add(ZMODE,BorderLayout.WEST); 
	tpanel1.add(zmode, BorderLayout.CENTER);
        tpanel1.add(Box.createRigidArea(hpad20), BorderLayout.EAST);
	makeJButton(panel2, tpanel1, 0, 3, 1, 1, 0.0, 0.0);
        makeJButton(panel2, bac, 1, 3, 1, 1, 0.0, 0.0);
	makeJButton(panel2, Box.createRigidArea(vpad10), 0, 4, 1, 1, 0.0, 0.0);
        makeJButton(panel2, CASS, 0, 5, 1, 1, 0.0, 0.0);
        makeJButton(panel2, cachesizes, 1, 5, 2, 1, 0.0, 0.0);
        makeJButton(panel2, Box.createRigidArea(vpad20), 0, 6, 1, 1, 0.0, 0.0);
	ZMODE.setFont(boldFont);
  	zmode.setFont(boldFont);
	CASS.setFont(boldFont);
	cachesizes.setFont(boldFont);

        ric.addItemListener(this);
        zmode.addItemListener(this);
        dec.addItemListener(this);
        rwc.addItemListener(this);
        lpc.addItemListener(this);
        rec.addItemListener(this);
        dsc.addItemListener(this);
        ufc.addItemListener(this);
        zzc.addItemListener(this);
	abc.addItemListener(this);
	bac.addItemListener(this);
        cachesizes.addActionListener(this);
        cachesizes.addMouseListener(this);
        cachesizes.addFocusListener(this);

        dialog.getContentPane().add(panel2,BorderLayout.CENTER);

        // Panel 3 (buttons and text area)

        panel31.add(entrb);
        panel31.add(helpb);
        panel31.add(quitb);
  	panel3.add(panel31, BorderLayout.NORTH);
        panel3.add(Box.createRigidArea(vpad30),BorderLayout.CENTER);

        // Listen for events on buttons
        entrb.addActionListener(this);
        helpb.addActionListener(this);
        quitb.addActionListener(this);

	// Text Area
  	info.setEditable(false);
  	scroller = new JScrollPane() {
		public Dimension getPreferredSize() {
		   return new Dimension(80, 160);
		}
		public float getAlignmentX() {
		   return LEFT_ALIGNMENT;
		}
	};
	scroller.getViewport().add(info);
	panel3.add(scroller, BorderLayout.SOUTH);
	dialog.getContentPane().add(panel3, BorderLayout.SOUTH);
  	info.setVisible(true);

    }

/** Implement the run method for Runabout interface.
 */

    public void run() {
	buildGUI();
	dialog.pack();
	dialog.setVisible(true);

    }

/** Reenter from the main toolkit driver. Reset the dialog to visible.
 */

    public void reEnter() {
        dialog.setVisible(true);
        info.setText("");
	cdf.setText("");
	sklt.setText("");
	cachesizes.setText("");
    }

/** Action for buttons or menu items selection
 */

    public void actionPerformed(ActionEvent evt) {
	Object obj = evt.getSource();
        info.setVisible(true);
	info.setText("");
	if (obj == quitb || obj == menuFileExit) { // for "Quit" or "Exit" 
             if (frommain == 0) {
		if (modal) dialog.dispose();
		else dialog.setVisible(false);
             } else {System.exit(0);}
	} else if (obj == slt1 || obj == slt2) { // for "Select 1|2" button
             if (obj == slt2) { // for "Select2" button for CDF
                doFileChooser("Select/enter a cdf", cdf, 1, filter1);
             } else { // for "Select1" button for skeleton table
                doFileChooser("Select/enter a skeleton table", sklt, 2, filter2);
             }
	} else if (obj == helpb || obj == menuHelpHelp) { // for "Help" 
		commandX = command + "-java";
		hlp = 1;
		runit(commandX);
		hlp = 0;
	} else if (obj == entrb) { // for "Enter" button
		temp = strri + strrw + strre;
		if (temp.length() == 0) report = " ";
		else if (temp.length() == 3) report = "-report \"i,w,e\" ";
		else if (temp.length() == 1) {
		   if (strri.length() == 1) report = "-report \"i\" ";
		   if (strrw.length() == 1) report = "-report \"w\" ";
		   if (strre.length() == 1) report = "-report \"e\" ";
		} else {
		   if (strri.length() == 0) report = "-report \"w,e\" ";
		   if (strrw.length() == 0) report = "-report \"i,e\" ";
		   if (strre.length() == 0) report = "-report \"i,w\" ";
		}
		strzm = (String) zmode.getSelectedItem();
		Zmode = "-zmode " + strzm + " ";
		caches = cachesizes.getText();
		if (caches.equals("")) caches = " ";
		else {
		   if (caches.startsWith("\"")) caches = "-cache " + 
							 caches + " ";
		   else caches = "-cache \"" + caches + "\" ";
		}
		cdfspec = cdf.getText(); 
		skltspec = sklt.getText(); 
		if (cdfspec.equals("")) cdfspec = " ";
		else cdfspec = "-cdf " + cdfspec + " ";
		commandX = command  + strzz + struf + strlp +
			   strds + strde + report + Zmode + 
			   caches + cdfspec + skltspec + strba;
		runit(commandX);
	} else if (obj == cdf) { // "CDF" text field
		cdfspec = cdf.getText();
	} else if (obj == sklt) { // "Skeleton" text field
                skltspec = sklt.getText();
	} else if (obj == cachesizes) { // "CacheSizes" text field
		caches = cachesizes.getText();
	} else if (obj == menuEditCopy) {
                if (istart == -1) {
		    info.setText("Where to copy from?");
                    Toolkit.getDefaultToolkit().beep();
                } else {
                    focused.setSelectionStart(istart);
                    focused.setSelectionEnd(iend);
                    focused.copy();
                }
        } else if (obj == menuEditCut) {
                if (istart == -1) {
		    info.setText("What to cut?");
                    Toolkit.getDefaultToolkit().beep();
                } else {
                    focused.setSelectionStart(istart);
                    focused.setSelectionEnd(iend);
                    focused.cut();
                }
        } else if (obj == menuEditPaste) {
                if (focused == null) {
		    info.setText("Where to paste to?");
                    Toolkit.getDefaultToolkit().beep();
                } else {
                  if (istart != -1) {
                    focused.setSelectionStart(istart);
                    focused.setSelectionEnd(iend);
                  }
                  String temp;
                  try {
                    temp = (String) Toolkit.getDefaultToolkit().
                                    getSystemClipboard().getContents(this).
                                    getTransferData(DataFlavor.stringFlavor);
                    if (temp == null) { // no clipboard text
                        info.setText("No current selection on clipboard");
                        Toolkit.getDefaultToolkit().beep();
                    } else if (temp.indexOf("\n") != -1) { // multiple lines
                        info.setText("Multiple lines selection on clipboard - not allowed:");
                        info.append("\n\n");
                        info.append(temp);
                        Toolkit.getDefaultToolkit().beep();
                    } else { focused.paste();}
                  } catch (Exception e) {}
                }
        }
    }

/** Action for checkboxs and combo boxs selection
 */

    public void itemStateChanged(ItemEvent evt) {
	Object obj = evt.getSource();
	if (obj == zmode) { // "zMode" choice
		if (evt.getStateChange() ==  ItemEvent.SELECTED) {
		  strzm = (String) zmode.getSelectedItem();
		}
	} else if (obj == dec) { // "Delete Existing" checkbox
		if (dec.isSelected()) 
	          strde = "-delete ";
		else
		  strde = "-nodelete ";
	} else if (obj == abc) { // "About" checkbox
                if (abc.isSelected()) {
		  commandX = command + " -about";
                  runit(commandX);
                  abc.setSelected(false);
		}
	} else if (obj == ric) { // "Report Info's" checkbox
                if (ric.isSelected()) 
		  strri = "i";
	        else 
		  strri = "";
        } else if (obj == rwc) { // "Report Warnings" checkbox 
                if (rwc.isSelected())
                  strrw = "w";
                else
                  strrw = "";
        } else if (obj == rec) { // "Report Errors" checkbox
                if (rec.isSelected())
                  strre = "e";
                else
                  strre = "";
        } else if (obj == ufc) { // "Use FILLVAL" checkbox
                if (ufc.isSelected())
                  struf = "-fillval ";
                else
                  struf = "-nofillval ";
        } else if (obj == lpc) { // "Log Progress" checkbox
                if (lpc.isSelected())
                  strlp = "-log ";
                else
                  strlp = "-nolog ";
	} else if (obj == zzc) { // "-0.0 to 0.0" checkbox
                if (zzc.isSelected())
                  strzz = "-neg2posfp0 ";
                else
                  strzz = "-noneg2posfp0 ";
	} else if (obj == dsc) { // "Display Statistics" checkbox
                if (dsc.isSelected())
                  strds = "-statistics ";
                else
                  strds = "-nostatistics ";
	} else if (obj == bac) { // "Run it Batch" checkbox
                if (bac.isSelected()) {
		  strba = "&";
                  iba = 1;
                } else {
		  strba = " ";
                  iba = 0;
	        }
        }
    }

    public void focusGained(FocusEvent e) {
        focused = (JTextField)e.getSource();
        if (prevfocused != null) {
            prevfocused.setText(prevfocused.getText());
            if (iend != -1)
                if (iend < focused.getText().length())
                             prevfocused.setCaretPosition(iend);
                else focused.setCaretPosition(focused.getText().length());
        }
    }

    public void focusLost(FocusEvent e) {
        if (istart != -1)     {
            focused.setSelectionStart(istart);
            focused.setSelectionEnd(iend);
            prevfocused = focused;
          }
        }
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {
        if (focused != null) {
          String selectedText = focused.getSelectedText();
          if (selectedText!=null) {
            istart = focused.getSelectionStart();
            iend = focused.getSelectionEnd();
          } else {
            istart = -1;
            iend = -1;
          }
        }
    }

/** Set up gridbaglayout.
 */

    private void makeJButton(Container cont, Object arg,
            int x, int y, int w, int h, double weightx, double weighty) {
        GridBagLayout gbl = (GridBagLayout)cont.getLayout();
        GridBagConstraints c = new GridBagConstraints();
        Component comp;

        c.fill = GridBagConstraints.BOTH;
        c.gridx = x;
        c.gridy = y;
        c.gridwidth = w;
        c.gridheight = h;
        c.weightx = weightx;
        c.weighty = weighty;
        if (arg instanceof String) {
            comp = new JButton((String)arg);
        } else {
            comp = (Component)arg;
        }
        cont.add(comp);
        gbl.setConstraints(comp, c);
    }

/** Execute a command as a separate process for the user selection and returns
    the results in the text area.
 */
    public void runit(String command) {
	try {
		if (System.getProperty("os.name").equalsIgnoreCase("VMS"))
		   command = command.replace('-', '/');
                process = runtime.exec(command);
                if (iba == 1 && hlp == 0) { // can't be batch if just for help
                        info.setText("Program is submitted...\n");
                        return;
                }
                if (hlp == 0) { // skip if just for help info
                        info.setText("");
                        info.append("Program is running...\n");
                }
                process.waitFor();
                BufferedReader inStream = new BufferedReader(new       
                                InputStreamReader(process.getInputStream()));
		info.read(inStream, null);
		if (process.exitValue() == 1) Toolkit.getDefaultToolkit().beep();
                else {
		  if (hlp == 0 && !abc.isSelected()) {
                        String myString;
			String extension, osname;
                        info.append("Done!\n\n");
			osname = System.getProperty("os.name");
			extension = ".CDF";
			if (osname.indexOf("UNIX") != -1) extension = ".cdf";
			if (osname.indexOf("unix") != -1) extension = ".cdf";
                        if (!cdfspec.equals(" ")) {
				myString = cdf.getText().trim() + extension;
				info.append("CDF:"+myString+"   is created.");
			}
		  }
                } 
	} catch (Exception e) {
                info.setText("error executing: "+e);
                Toolkit.getDefaultToolkit().beep();
	}

    }

/** Initiate the file chooser and strip the extension for the selected file
    if necessary
 */

    private void doFileChooser (String title, JTextField textField, int theone, SimpleFileFilter filter) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(currentdir));
        fileChooser.setDialogTitle(title);
        fileChooser.addChoosableFileFilter(filter1);
	fileChooser.addChoosableFileFilter(filter1);
        if (filter != null) fileChooser.setFileFilter(filter);
        int returnValue = fileChooser.showDialog(dialog, "OK");
        if (returnValue == JFileChooser.APPROVE_OPTION) {
          File selectedFile = fileChooser.getSelectedFile();
          if (selectedFile != null) {
             int ind;
             String myfile = selectedFile.getAbsolutePath();
             if (theone == 1) { // for .cdf file
                ind = myfile.lastIndexOf(".cdf");
                if (ind != -1) myfile = myfile.substring(0, ind);
                ind = myfile.lastIndexOf(".CDF");
                if (ind != -1) myfile = myfile.substring(0, ind);
             } else { // for .skt file
                ind = myfile.lastIndexOf(".skt");
                if (ind != -1) myfile = myfile.substring(0, ind);
                ind = myfile.lastIndexOf(".SKT");
                if (ind != -1) myfile = myfile.substring(0, ind);
             }
             textField.setText(myfile);
          }
        } else {
//        JOptionPane.showMessageDialog(dialog, "No file chosen");
        }
    }

/** This class can run as an application by its own.
 */

    static public void main(String[] args) {

        JFrame myframe = new JFrame();
        SkeletonCDF thispgm = new SkeletonCDF(myframe);
        thispgm.frommain = 1;
        thispgm.buildGUI();
        thispgm.pack();
        thispgm.setVisible(true);
    }

}

