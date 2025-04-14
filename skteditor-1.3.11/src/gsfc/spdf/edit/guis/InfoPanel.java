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
 * $Id: InfoPanel.java,v 1.34 2022/03/24 10:38:32 btharris Exp $
 */
package gsfc.spdf.edit.guis;

import javax.swing.*;
import javax.swing.border.*;
import javax.help.CSH;

import java.awt.*;
/*
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
*/

import gsfc.spdf.cdf.Cdf;
import gsfc.spdf.istp.TerrestrialTime2000;
import gsfc.spdf.gui.*;
import gsfc.nssdc.cdf.*;



public class InfoPanel 
    extends JPanel
    implements CDFConstants {

    private SKTEditor myEditor;
    
    // The logos
    private ImageIcon spdfLogo;
    private ImageIcon gsfcLogo;
    private ImageIcon ssdooLogo;

    // the file information
    private JTextField filename;
    private JTextField cdfLibVersion;
    private JTextField cdfVersion;
    private JTextField libLastLeapSecond;
    private JTextField fileLastLeapSecond;
    private JTextField encoding;
    private JTextField majority;
    private JTextField numVars;
    private JTextField numAttrs;
    private JTextField compression;
    private JTextField compressionPct;

    /**
     * Checksum option field.
     */
    private JTextField checksum;


    public InfoPanel(SKTEditor myEditor) {
	super(true);
	this.myEditor = myEditor;
        getAccessibleContext().setAccessibleName("Information Panel");
	GridBagLayout gbl = new GridBagLayout();
	GridBagConstraints gbc = new GridBagConstraints();

	setBorder(new CompoundBorder(myEditor.loweredBorder, 
				     myEditor.emptyBorder2));
	setLayout(gbl);
	setBackground(Color.white);
	setPreferredSize(new Dimension(myEditor.WIDTH, myEditor.HEIGHT));
	
	// The logos
        String iconPath = null;

/*
        try {
            iconPath = SKTEditor.propertyPath +
                       myEditor.appProperties.getProperty("image.path") +
                       myEditor.appProperties.getProperty("logo.gsfc");

	    gsfcLogo = new ImageIcon(InfoPanel.class.getResource(iconPath),
			             "gsfc");
        }
        catch(NullPointerException e) {

            System.err.println("cannot find icon " + iconPath +
                               " -- continuing without it");
        };
        try {
            iconPath = SKTEditor.propertyPath+
                       myEditor.appProperties.getProperty("image.path")+
                       myEditor.appProperties.getProperty("logo.ssdoo");
	    ssdooLogo = new ImageIcon(InfoPanel.class.getResource(iconPath),
			  "ssdoo");
        }
        catch(NullPointerException e) {

            System.err.println("cannot find icon " + iconPath +
                               " -- continuing without it");
        };
*/
        try {
            iconPath = SKTEditor.propertyPath+
                       myEditor.appProperties.getProperty("image.path")+
                       myEditor.appProperties.getProperty("logo.spdf");
	    spdfLogo = new ImageIcon(InfoPanel.class.getResource(iconPath),
			  "spdf");
            spdfLogo.setDescription("Space Physics Data Facility logo");
        }
        catch(NullPointerException e) {

            System.err.println("cannot find icon " + iconPath +
                               " -- continuing without it");
        };

	String sktInfo = "For information on the SKTEditor see: "+
	    "https://spdf.gsfc.nasa.gov/skteditor";
        
	String istpInfo = "For information on the ISTP Guidelines see: "+
	    "https://spdf.gsfc.nasa.gov/sp_use_of_cdf.html";

	// Layout the panel
	gbc.insets = myEditor.insets5;
	gbc.anchor = GridBagConstraints.NORTHWEST;
	gbc.gridheight = 1;
        gbc.weightx = 1.0;
	gbc.weighty = 0.2;
        gbc.ipady =10;

//	JLabel label = new JLabel(ssdooLogo);
//	gbl.setConstraints(label, gbc);
//        add(label);

        JLabel label = new JLabel(spdfLogo);
	
	gbc.gridwidth = GridBagConstraints.REMAINDER;
	gbl.setConstraints(label, gbc);
	add(label);
	
	gbc.insets = new Insets(20,5,0,0);
        
 
	label = new JLabel(sktInfo);
	gbl.setConstraints(label, gbc);
	add(label);
       gbc.insets = new Insets(0,5,0,0);		

/*
JButton istpInfoButton = new JButton(istpInfo);
istpInfoButton.addActionListener(new ActionListener() {
    void actionPerformed(ActionEvent e) {

        if (Desktop.isDesktopSupported()) {

            Desktop.getDesktop().browse(SPDF_USE_OF_CDF_URL);
        }
    }
});
gbl.setConstraints(istpInfoButton, gbc)
add(istpInfoButton);
*/

	label = new JLabel(istpInfo);
	gbl.setConstraints(label, gbc);
	add(label);
        

        String programVersion = "Program version: " + 
            SKTEditor.defaultProperties.getProperty("program.version");
             //   substring(1).replace('_', '.');
        String programBuildDate = "Program build date: " + 
            SKTEditor.defaultProperties.getProperty("program.build.date");
        String programBuildPlatform = 
            SKTEditor.defaultProperties.getProperty("program.build.platform");
        String programBuildCompiler = 
            SKTEditor.defaultProperties.getProperty("program.build.compiler");
        String programBuildPlatformTxt = "Program build platform: " + 
                                         programBuildPlatform;
        String programBuildCompilerTxt = "Program build compiler: " + 
                                         programBuildCompiler;

        label = new JLabel(programVersion);
        gbl.setConstraints(label, gbc);
        add(label);
        CSH.setHelpIDString(label, "ProgramVersion"); 

        label = new JLabel(programBuildDate);
        gbl.setConstraints(label, gbc);
        add(label);
        CSH.setHelpIDString(label, "ProgramBuildDate"); 

        if (programBuildPlatform != null) {

            label = new JLabel(programBuildPlatformTxt);
            gbl.setConstraints(label, gbc);
            add(label);
            CSH.setHelpIDString(label, "ProgramBuildPlatform"); 
        }

        if (programBuildCompiler != null) {

            label = new JLabel(programBuildCompilerTxt);
            gbl.setConstraints(label, gbc);
            add(label);
            CSH.setHelpIDString(label, "ProgramBuildCompiler");
        }
        
	JPanel stats = FileInfoPanel();
	gbc.weighty = 1;
	
	gbc.insets = new Insets(20,5,0,0);
	gbl.setConstraints(stats, gbc);
	add(stats);
    }

    private JPanel FileInfoPanel() {
	GridBagLayout gbl = new GridBagLayout();
	GridBagConstraints gbc = new GridBagConstraints();
	JPanel p = new JLabeledPanel("File Statistics", gbl);
	p.setBackground(Color.white);
	int size = 40;
	gbc.weighty = 1.0;

	JLabel label;
	
	gbc.insets = myEditor.insets5;
        gbc.anchor = GridBagConstraints.NORTHWEST;
    
	label = new JLabel("File Name");
	gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
	gbl.setConstraints(label, gbc);
	p.add(label);
        CSH.setHelpIDString(label, "FileName"); 

	filename = new JTextField(size);
        label.setLabelFor(filename);
	filename.setEditable(false);
	filename.setOpaque(false);
	filename.setBorder(new EmptyBorder(0,0,0,0));
        filename.setMinimumSize(new Dimension(200,10));
        gbc.gridx = 1;
        gbc.gridy = 0;
	gbl.setConstraints(filename, gbc);
	p.add(filename);
        CSH.setHelpIDString(filename, "FileName"); 
	
	label = new JLabel("CDF Library Version");	
        gbc.gridx = 0;
        gbc.gridy = 1;
    
	gbl.setConstraints(label, gbc);
	p.add(label);
        CSH.setHelpIDString(label, "CdfLibVersion"); 

	cdfLibVersion = new JTextField(size);
        label.setLabelFor(cdfLibVersion);
	cdfLibVersion.setEditable(false);
	cdfLibVersion.setOpaque(false);
	cdfLibVersion.setBorder(new EmptyBorder(0,0,0,0));
        cdfLibVersion.setMinimumSize(new Dimension(200,10));
        gbc.gridx = 1;
        gbc.gridy = 1;
	gbl.setConstraints(cdfLibVersion, gbc);
	p.add(cdfLibVersion);
        CSH.setHelpIDString(cdfLibVersion, "CdfLibVersion"); 

	label = new JLabel("CDF File Version");	
        gbc.gridx = 0;
        gbc.gridy = 2;
    
	gbl.setConstraints(label, gbc);
	p.add(label);
        CSH.setHelpIDString(label, "CdfVersion"); 

	cdfVersion = new JTextField(size);
        label.setLabelFor(cdfVersion);
	cdfVersion.setEditable(false);
	cdfVersion.setOpaque(false);
	cdfVersion.setBorder(new EmptyBorder(0,0,0,0));
        cdfVersion.setMinimumSize(new Dimension(200,10));
        gbc.gridx = 1;
        gbc.gridy = 2;
	gbl.setConstraints(cdfVersion, gbc);
	p.add(cdfVersion);
        CSH.setHelpIDString(cdfVersion, "CdfVersion"); 
	
	label = new JLabel("Library Last Leap Second");
        gbc.gridx = 0;
        gbc.gridy = 3;
	gbl.setConstraints(label, gbc);
	p.add(label);
        CSH.setHelpIDString(label, "LibraryLastLeapSecond"); 

	libLastLeapSecond = new JTextField(size);
        label.setLabelFor(libLastLeapSecond);
	libLastLeapSecond.setEditable(false);
	libLastLeapSecond.setOpaque(false);
	libLastLeapSecond.setBorder(new EmptyBorder(0,0,0,0));
        libLastLeapSecond.setMinimumSize(new Dimension(200,10));
        gbc.gridx = 1;
        gbc.gridy = 3;
	gbl.setConstraints(libLastLeapSecond, gbc);
	p.add(libLastLeapSecond);
        CSH.setHelpIDString(libLastLeapSecond, 
            "LibraryLastLeapSecond"); 
	
	label = new JLabel("File Last Leap Second");
        gbc.gridx = 0;
        gbc.gridy = 4;
	gbl.setConstraints(label, gbc);
	p.add(label);
        CSH.setHelpIDString(label, "FileLastLeapSecond"); 

	fileLastLeapSecond = new JTextField(size);
        label.setLabelFor(fileLastLeapSecond);
	fileLastLeapSecond.setEditable(false);
	fileLastLeapSecond.setOpaque(false);
	fileLastLeapSecond.setBorder(new EmptyBorder(0,0,0,0));
        fileLastLeapSecond.setMinimumSize(new Dimension(200,10));
        gbc.gridx = 1;
        gbc.gridy = 4;
	gbl.setConstraints(fileLastLeapSecond, gbc);
	p.add(fileLastLeapSecond);
        CSH.setHelpIDString(fileLastLeapSecond, "FileLastLeapSecond"); 
	
	label = new JLabel("CDF Encoding");
        gbc.gridx = 0;
        gbc.gridy = 5;
	gbl.setConstraints(label, gbc);
	p.add(label);
        CSH.setHelpIDString(label, "CdfEncoding"); 

	encoding = new JTextField(size);
        label.setLabelFor(encoding);
	encoding.setEditable(false);
	encoding.setOpaque(false);
	encoding.setBorder(new EmptyBorder(0,0,0,0));
        encoding.setMinimumSize(new Dimension(200,10));
        gbc.gridx = 1;
        gbc.gridy = 5;
	gbl.setConstraints(encoding, gbc);
	p.add(encoding);
        CSH.setHelpIDString(encoding, "CdfEncoding"); 
	
	label = new JLabel("CDF Majority");
        gbc.gridx = 0;
        gbc.gridy = 6;
	gbl.setConstraints(label, gbc);
	p.add(label);
        CSH.setHelpIDString(label, "CdfMajority"); 

	majority = new JTextField(size);
        label.setLabelFor(majority);
	majority.setEditable(false);
	majority.setOpaque(false);
	majority.setBorder(new EmptyBorder(0,0,0,0));
        majority.setMinimumSize(new Dimension(200,10));
        gbc.gridx = 1;
        gbc.gridy = 6;
	gbl.setConstraints(majority, gbc);
	p.add(majority);
        CSH.setHelpIDString(majority, "CdfMajority"); 
	
	label = new JLabel("Number of Variables");
        gbc.gridx = 0;
        gbc.gridy = 7;
	gbl.setConstraints(label, gbc);
	p.add(label);
        CSH.setHelpIDString(label, "NumberOfVariables"); 

	numVars = new JTextField(size);
        label.setLabelFor(numVars);
	numVars.setEditable(false);
	numVars.setOpaque(false);
	numVars.setBorder(new EmptyBorder(0,0,0,0));
        numVars.setMinimumSize(new Dimension(200,10));
        gbc.gridx = 1;
        gbc.gridy = 7;
	gbl.setConstraints(numVars, gbc);
	p.add(numVars);
        CSH.setHelpIDString(numVars, "NumberOfVariables"); 
	
	label = new JLabel("Number of Attributes");
        gbc.gridx = 0;
        gbc.gridy = 8;
	gbl.setConstraints(label, gbc);
	p.add(label);
        CSH.setHelpIDString(label, "NumberOfAttributes"); 

	numAttrs = new JTextField(size);
        label.setLabelFor(numAttrs);
	numAttrs.setEditable(false);
	numAttrs.setOpaque(false);
	numAttrs.setBorder(new EmptyBorder(0,0,0,0));
        numAttrs.setMinimumSize(new Dimension(200,10));
        gbc.gridx = 1;
        gbc.gridy = 8;
	gbl.setConstraints(numAttrs, gbc);
	p.add(numAttrs);
        CSH.setHelpIDString(numAttrs, "NumberOfAttributes"); 
	
	label = new JLabel("Compression");
        gbc.gridx = 0;
        gbc.gridy = 9;
	gbl.setConstraints(label, gbc);
	p.add(label);
        CSH.setHelpIDString(label, "Compression"); 

	compression = new JTextField(size);
        label.setLabelFor(compression);
	compression.setEditable(false);
	compression.setOpaque(false);
	compression.setBorder(new EmptyBorder(0,0,0,0));
        compression.setMinimumSize(new Dimension(300,13));
        gbc.gridx = 1;
        gbc.gridy = 9;
	gbl.setConstraints(compression, gbc);
	p.add(compression);
        CSH.setHelpIDString(compression, "Compression"); 

	label = new JLabel("Compression Percent");
        gbc.gridx = 0;
        gbc.gridy = 10;
	gbl.setConstraints(label, gbc); 
	p.add(label);
        CSH.setHelpIDString(label, "CompressionPercent"); 
	
	compressionPct = new JTextField(size);
        label.setLabelFor(compressionPct);
	compressionPct.setEditable(false);
	compressionPct.setOpaque(false);
	compressionPct.setBorder(new EmptyBorder(0,0,0,0));
        compressionPct.setMinimumSize(new Dimension(200,10));
        gbc.gridx = 1;
        gbc.gridy = 10;
	gbl.setConstraints(compressionPct, gbc);
	p.add(compressionPct);
        CSH.setHelpIDString(compressionPct, "CompressionPercent"); 

        label = new JLabel("Checksum");
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbl.setConstraints(label, gbc); 
        p.add(label);
        CSH.setHelpIDString(label, "Checksum"); 
	
        checksum = new JTextField(size);
        label.setLabelFor(checksum);
        checksum.setEditable(false);
        checksum.setOpaque(false);
        checksum.setBorder(new EmptyBorder(0,0,0,0));
        checksum.setMinimumSize(new Dimension(200,10));
        gbc.gridx = 1;
        gbc.gridy = 11;
        gbl.setConstraints(checksum, gbc);
        p.add(checksum);
        CSH.setHelpIDString(checksum, "Checksum"); 

	return p;
    }	
    public void updateFileStats() {
	if (myEditor.theCDF != null) {
	    filename.setText((myEditor.theFile == null ? 
			      "New File" : myEditor.theFile.getName()));
            try {

	        cdfLibVersion.setText(
                    myEditor.theCDF.getLibraryVersion());
            }
            catch (CDFException e) {

                System.err.println("CDF.getLibraryVersion() failed: " +
                    e.getMessage());
	        cdfLibVersion.setText("unknown");
            }
	    cdfVersion.setText(""+myEditor.theCDF.getVersion());

/*
            long[] libLastDateInLeapSecondsTable =
                TerrestrialTime2000.getLastDateInLeapSecondsTable();

            ByteArrayOutputStream libLastLeapSecondDate =
                new ByteArrayOutputStream();
                                       // CDF library's last leap
                                       // second date
            PrintStream libLastLeapSecondDateStrm =
                new PrintStream(libLastLeapSecondDate);
                                       // PrintStream for formating
                                       // library's last leap second
                                       // date
            libLastLeapSecondDateStrm.format(
                "%04d-%02d-%02d", 
                libLastDateInLeapSecondsTable[0],
                libLastDateInLeapSecondsTable[1],
                libLastDateInLeapSecondsTable[2]);

            libLastLeapSecond.setText(
                libLastLeapSecondDate.toString());
*/
            libLastLeapSecond.setText(
                TerrestrialTime2000.getLastDateInLeapSecondsTableAsString());

            String leapSecondLastUpdated = null;
            try {

                leapSecondLastUpdated =
                    Cdf.getLeapSecondLastUpdatedAsString(
                            myEditor.theCDF);
            }
            catch(CDFException e) {

                leapSecondLastUpdated = "error";

                System.err.println("InfoPanel.updateFileStats: " +
                    "Cdf.getLeapSecondLastUpdatedAsString " +
                    "CDFException: " + e.getMessage());
            }

            fileLastLeapSecond.setText(leapSecondLastUpdated);

            encoding.setText(Cdf.getEncodingAsString(myEditor.theCDF));
	    
	    majority.setText(Cdf.getMajorityAsString(myEditor.theCDF));

	    numVars.setText(""+myEditor.theCDF.getNumVars());
	    
	    StringBuffer attrString = new StringBuffer();
	    attrString.append(myEditor.theCDF.getNumAttrs());
	    attrString.append(" Attributes (");
	    attrString.append(myEditor.theCDF.getNumGattrs());
	    attrString.append("G/");
	    attrString.append(myEditor.theCDF.getNumVattrs());
	    attrString.append("V)");
	    numAttrs.setText(attrString.toString());

            String compressionValue = "";
            try {
                compressionValue = myEditor.theCDF.getCompression();
            }
            catch (CDFException e) {

                e.printStackTrace();
            };
            compression.setText(compressionValue);

            compressionPct.setText(
                Long.toString(myEditor.theCDF.getCompressionPct()));

            checksum.setText(Cdf.getChecksumAsString(myEditor.theCDF));

	}
    }

    public void resetPanel() {
	filename.setText("");
	cdfVersion.setText("");
        libLastLeapSecond.setText("");
	fileLastLeapSecond.setText("");
	encoding.setText("");
	majority.setText("");
	numVars.setText("");
	numAttrs.setText("");
        compression.setText("");
        compressionPct.setText("");
        checksum.setText("");
    }


} // InfoPanel
