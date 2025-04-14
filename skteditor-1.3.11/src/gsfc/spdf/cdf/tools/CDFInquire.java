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
 * $Id: CDFInquire.java,v 1.4 2022/03/24 10:38:25 btharris Exp $
 */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

/** The CDFInquire tool program which can be run either from the CDFTools 
    driver or as an application, will provide the version of the CDF 
    distribution being used and the default toolkit qualifiers.
 */ 

public class CDFInquire extends JDialog 
			 implements ActionListener, ItemListener, Runnable {
/** dialog for user interface. */
    JDialog dialog = new JDialog();

/** The panel for selectable buttons which goes to dialog's north portion. */
    JPanel panel1 = new JPanel(new GridBagLayout());
/** The panel for run/help/quit buttons. */
    JPanel panel21 = new JPanel(new GridLayout(1, 3, 50, 20));
/** The panel containing panel21 goes to dialog's center portion. */
    JPanel panel2 = new JPanel(new BorderLayout());
/** The panel for text area which goes to dialog's south portion. */
    JPanel panel3 = new JPanel(new BorderLayout());

/** "Id" checkbox. */
    JCheckBox idc = new JCheckBox("Id",true);
/** "Page Output" checkbox. */
    JCheckBox pgc = new JCheckBox("Page Output");
/** "About" checkbox. */
    JCheckBox abc = new JCheckBox("About");

/** "Run CDFInquire" button. */
    JButton entrb = new JButton("Run CDFInquire");
/** "Help" button. */
    JButton helpb = new JButton("Help");
/** "Quit" button. */
    JButton quitb = new JButton("Quit");

/** Text area for information. */
    JTextArea info = new JTextArea(20, 40);

/** Scroll pane for the text area. */
    JScrollPane scroller;

/** command string. */
    String command = "cdfinquire ";
    String commandX;
/** Environment variable for CDF_BASE. */
    String cdfbase = System.getProperty("CDF_BASE");
/** The file separator. */
    String separator = System.getProperty("file.separator");
/** A platform-dependent Runtime object. */
    Runtime runtime = Runtime.getRuntime();
/** A separate process to run the command. */ 
    Process process = null;
    int hlp;

/** Menu bar. */
    JMenuBar menuBar = new JMenuBar();
/** A menu: File. */
    JMenu menuFile = new JMenu("File");
/** A menu: Help. */
    JMenu menuHelp = new JMenu("Help");
/** A File menu item: Exit. */
    JMenuItem menuFileExit = new JMenuItem("Exit");
/** A Help menu item: Help. */
    JMenuItem menuHelpHelp = new JMenuItem("Help");

    Dimension hpad5 = new Dimension(5,1);
    Dimension hpad10 = new Dimension(10,1);
    Dimension hpad20 = new Dimension(20,1);
    Dimension hpad25 = new Dimension(25,1);
    Dimension hpad30 = new Dimension(30,1);
    Dimension hpad40 = new Dimension(40,1);
    Dimension hpad50 = new Dimension(50,1);
    Dimension vpad5 = new Dimension(1,5);
    Dimension vpad10 = new Dimension(1,10);
    Dimension vpad20 = new Dimension(1,20);
    Dimension vpad25 = new Dimension(1,25);
    Dimension vpad30 = new Dimension(1,30);
    Dimension vpad40 = new Dimension(1,40);
    Dimension vpad50 = new Dimension(1,50);

    Font  defaultFont = new Font("Dialog", Font.PLAIN, 12);
    Font  boldFont = new Font("Dialog", Font.BOLD, 12);
    Font  bigFont = new Font("Dialog", Font.PLAIN, 18);
    Font  bigboldFont = new Font("Dialog", Font.BOLD, 18);
    static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    static int INITIAL_WIDTH = 600;
    static int INITIAL_HEIGHT = 400;

/** The default dialog modal (is set to false). */
    static boolean defaultModal = false;
/** The current dialog modal. */
    boolean modal;
    int frommain = 0;

    CDFInquire(JFrame parentFrame) {
        this(parentFrame, null, defaultModal);
    }

    CDFInquire(JFrame parentFrame, String pgm) {
        this(parentFrame, pgm, defaultModal);
    }

    CDFInquire(JFrame parentFrame, String pgm, boolean modal) {
        super(parentFrame, modal);
        if (pgm != null) command = pgm;
        else if (cdfbase != null) {
            command = cdfbase + separator + "bin" + separator + "cdfinquire ";
        }
        dialog = this;
        this.modal = modal;
    }

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
        dialog.setTitle("Enter   Parameters/qualifiers  for  CDFinquire");
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

        // add the help menu
        menuBar.add(menuHelp);

        // add the menu items to help
        menuHelp.add(menuHelpHelp);
        menuHelpHelp.addActionListener(this);

        // Panel 1
	makeJButton(panel1, Box.createRigidArea(vpad10), 0, 0, 1, 1, 0.0, 0.0);
	makeJButton(panel1, Box.createRigidArea(hpad50), 0, 1, 1, 1, 0.0, 0.0);
	makeJButton(panel1, idc, 1, 1, 1, 1, 0.0, 0.0);
	makeJButton(panel1, Box.createRigidArea(hpad50), 2, 1, 1, 1, 0.0, 0.0);
        makeJButton(panel1, pgc, 3, 1, 1, 1, 0.0, 0.0);
	makeJButton(panel1, Box.createRigidArea(hpad50), 4, 1, 1, 1, 0.0, 0.0);
        makeJButton(panel1, abc, 5, 1, 1, 1, 0.0, 0.0);
	makeJButton(panel1, Box.createRigidArea(hpad50), 6, 1, 1, 1, 0.0, 0.0);
	makeJButton(panel1, Box.createRigidArea(vpad20), 0, 2, 1, 1, 0.0, 0.0);

	idc.addItemListener(this);
	pgc.addItemListener(this);
        abc.addItemListener(this);
	dialog.getContentPane().add(panel1,BorderLayout.NORTH);
	

        // Panel 2
	
        panel21.add(entrb);
        panel21.add(helpb);
        panel21.add(quitb);
        panel2.add(panel21, BorderLayout.NORTH);
        panel2.add(Box.createRigidArea(vpad20),BorderLayout.SOUTH);
	dialog.getContentPane().add(panel2,BorderLayout.CENTER);

        // Listen for events on buttons
        entrb.addActionListener(this);
        helpb.addActionListener(this);
        quitb.addActionListener(this);

	// Text Area
	info.setEditable(false);
        scroller = new JScrollPane() {
                public Dimension getPreferredSize() {
                   return new Dimension(200, 260);
                }
                public float getAlignmentX() {
                   return LEFT_ALIGNMENT;
                }
        };

	scroller.getViewport().add(info);
        panel3.add(scroller, BorderLayout.CENTER);
        info.setVisible(true);
        dialog.getContentPane().add(panel3, BorderLayout.SOUTH);

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
    }


/** Action for buttons or menu items selection
 */

    public void actionPerformed(ActionEvent evt) {
	Object obj = evt.getSource();
        if (obj == quitb || obj == menuFileExit) { // for "Quit" button
              if (frommain == 0) { 
                if (modal) dialog.dispose();
                else dialog.setVisible(false);
              } else {System.exit(0);}
	} else if (obj == entrb) { // for "Enter" button
		commandX = command + " -id " + "-nopage ";
		runit(commandX);
	} else { // for "Help" button
                commandX = command + " -java"; 
		runit(commandX);
	}
    }

/** Action for checkboxes selection
 */
    public void itemStateChanged(ItemEvent evt) {
	Object obj = evt.getSource();
	if (obj == abc) { // "About" checkbox
	  if (abc.isSelected()) {
		commandX = command + " -about";
		runit(commandX);
                abc.setSelected(false);
	  }
	} else if (obj == idc) { // "Id" checkbox
		if (!idc.isSelected()) {
			info.setText("Id is always needed.\n");
                        Toolkit.getDefaultToolkit().beep();
			idc.setSelected(true);
		}
	} else { // "Page Output" checkbox
		if (pgc.isSelected()) {
			info.setText("Page output is not allowed in Java.");
			pgc.setSelected(false);
			Toolkit.getDefaultToolkit().beep();
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
    private void runit(String command) {
        try {
		if (System.getProperty("os.name").equalsIgnoreCase("VMS"))
		  command = command.replace('-', '/');
                process = runtime.exec(command);
                if (hlp == 0) { // skip if just for help info
                        info.setText("Program is running...\n");
                }
                process.waitFor();
                BufferedReader inStream = new BufferedReader(new
                                InputStreamReader(process.getInputStream()));
                info.read(inStream, null);
                if (process.exitValue() == 1) Toolkit.getDefaultToolkit().beep();
                if (hlp == 1) hlp = 0;
        } catch (Exception e) {
              info.setText("error executing: "+e);
              Toolkit.getDefaultToolkit().beep();
        }
    }
/** This class can run as an application by its own.
 */
    static public void main(String[] args) {

        JFrame myframe = new JFrame();
        CDFInquire thispgm = new CDFInquire(myframe);
        thispgm.frommain = 1;
        thispgm.buildGUI();
        thispgm.pack();
        thispgm.setVisible(true);
    }

}
