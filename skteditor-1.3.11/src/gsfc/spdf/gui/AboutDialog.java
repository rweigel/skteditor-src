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
 * Copyright (c) 2003-2022 United States Government as represented by the 
 * National Aeronautics and Space Administration. No copyright is claimed 
 * in the United States under Title 17, U.S.Code. All Other Rights Reserved.
 *
 * $Id: AboutDialog.java,v 1.3 2022/03/24 10:38:36 btharris Exp $ 
 *
 * Created on November 7, 2006, 1:53 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gsfc.spdf.gui;

/**
 *
 * @author rachimiak
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import gsfc.spdf.edit.guis.SKTEditor;


/**
 * The AboutDialog.
 */
public class AboutDialog extends JDialog {
	public AboutDialog(JFrame parent) {
		super(parent, true);		 
		setResizable(false);
		getContentPane().setLayout(new GridBagLayout());
		setSize(330, 138);
		setTitle("About");
		// setLocationRelativeTo is only available in JDK 1.4
		try {
			setLocationRelativeTo(parent);
		} catch (NoSuchMethodError e) {
			Dimension paneSize=this.getSize();
                        Dimension screenSize= this.getToolkit().getScreenSize();
                        this.setLocation((screenSize.width-paneSize.width)/2, (screenSize.height-paneSize.height)/2);
     
		}

		JButton close= new JButton("Close");
		close.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			}
		);
		getRootPane().setDefaultButton(close);
                JLabel label1= new JLabel("SKTEditor");
		label1.setFont(new Font("dialog", Font.PLAIN, 36));
                
		String line = null;
            
                try{

                   line = SKTEditor.defaultProperties.getProperty("program.version") + ", " + SKTEditor.defaultProperties.getProperty("program.build.date");
          
                
                    }catch (Exception e) {

                       e.printStackTrace();
                        return;
                    }
       
        
    
                JLabel label2 = new JLabel(line);


		 label2.setFont(new Font("dialog", Font.PLAIN, 14));
		

		GridBagConstraints constraintsLabel1= new GridBagConstraints();
		constraintsLabel1.gridx = 3; constraintsLabel1.gridy = 0;
		constraintsLabel1.gridwidth = 1; constraintsLabel1.gridheight = 1;
		constraintsLabel1.anchor = GridBagConstraints.CENTER;
		getContentPane().add(label1, constraintsLabel1);

		GridBagConstraints constraintsLabel2= new GridBagConstraints();
		constraintsLabel2.gridx = 2; constraintsLabel2.gridy = 1;
		constraintsLabel2.gridwidth = 2; constraintsLabel2.gridheight = 1;
		constraintsLabel2.anchor = GridBagConstraints.CENTER;
		getContentPane().add(label2, constraintsLabel2);

		GridBagConstraints constraintsButton1= new GridBagConstraints();
		constraintsButton1.gridx = 2; constraintsButton1.gridy = 2;
		constraintsButton1.gridwidth = 2; constraintsButton1.gridheight = 1;
		constraintsButton1.anchor = GridBagConstraints.CENTER;
		constraintsButton1.insets= new Insets(8, 0, 8, 0);
		getContentPane().add(close, constraintsButton1);

		addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					dispose();
				}
			}
		);
	}

}    

