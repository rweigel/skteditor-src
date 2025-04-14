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
 * Copyright (c) 2017-2022 United States Government as represented by 
 * the National Aeronautics and Space Administration. No copyright is 
 * claimed in the United States under Title 17, U.S.Code. All Other 
 * Rights Reserved.
 *
 * $Id: IstpWarningDialog.java,v 1.3 2022/03/24 10:38:32 btharris Exp $
 */

package gsfc.spdf.edit.guis;


import java.awt.Component;

import java.util.EnumSet;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

import gsfc.spdf.istp.ISTPCompliance;


/**
 * A Dialog that offers the user a choice of the ISTP compliance
 * warnings that they want to suppress.
 *
 * @author B. Harris
 */
public class IstpWarningDialog {


    /**
     * Displays a dialog to obtain the user's selection of message 
     * warnings that are to be suppressed.
     *
     * @param component parent component for dialog.
     * @param suppressedWarnings inital set of suppressed warnings.
     * @return user's selection of suppressed warnings.
     */
    public static EnumSet<ISTPCompliance.Warnings> 
        getSuppressedWarnings(
            Component component,
            EnumSet<ISTPCompliance.Warnings> suppressedWarnings) {


        WarningCheckBox[] warningCkBoxes = {
            new WarningCheckBox("FILLVAL value is non-standard...",
                ISTPCompliance.Warnings.NON_STANDARD_FILLVAL),
            new WarningCheckBox("spase_DatasetResourceID is missing.",
                ISTPCompliance.Warnings.MISSING_SPASE_ID),
/*
            new WarningCheckBox("VALIDMIN >= VALIDMAX",
                ISTPCompliance.Warnings.VALIDMIN_GE_VALIDMAX)
*/
        };                             // checkboxes for each warning
                                       // that can be suppressed

        setCkBoxWarnings(warningCkBoxes, suppressedWarnings);

        Object[] msg = new Object[1 + warningCkBoxes.length];
                                       // warning selection dialog
                                       // message
        msg[0] = "Choose one or more warnings that are to be\n" +
                 " suppressed in the Messages window:";

        for (int i = 0; i < warningCkBoxes.length; i++) {

            msg[i + 1] = warningCkBoxes[i];
        }

        int selection = JOptionPane.showConfirmDialog(
                            component, msg,
                            "Messages Warning Selection",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
                                       // user's selection

        if (selection == JOptionPane.CANCEL_OPTION) {

            return suppressedWarnings;
        }

        return getSuppressedWarnings(warningCkBoxes);
    }


    /**
     * Set the given warningCkBoxes according to the given
     * suppressedWarnings.
     *
     * @param warningCkBoxes WarningCheckBoxes to be set.
     * @param suppressedWarnings warning that are to be suppressed.
     */
    private static void setCkBoxWarnings(
        WarningCheckBox[] warningCkBoxes,
        EnumSet<ISTPCompliance.Warnings> suppressedWarnings) {

        for (WarningCheckBox checkBox : warningCkBoxes) {

            ISTPCompliance.Warnings warning = checkBox.getWarning();

            checkBox.setSelected(suppressedWarnings.contains(warning));
        }
    }


    /**
     * Gets the set of ISTPCompliance.Warnings that are to be 
     * suppressed as represented by the given WarningCheckBoxes.
     *
     * @param warningCkBoxes WarningCheckBoxes representing the
     *     ISTPCompliance.Warnings that are to be suppressed.
     * @return set of ISTPCompliance.Warnings that are to be 
     *     suppressed.
     */
    private static EnumSet<ISTPCompliance.Warnings> 
        getSuppressedWarnings(
            WarningCheckBox[] warningCkBoxes) {

        EnumSet<ISTPCompliance.Warnings> suppressedWarnings =
            EnumSet.noneOf(ISTPCompliance.Warnings.class);

        for (WarningCheckBox checkBox : warningCkBoxes) {

            if (checkBox.isSelected()) {

                suppressedWarnings.add(checkBox.getWarning());
            }
        }

        return suppressedWarnings;
    }
} 



/**
 * A JCheckBox with an associated ISTPCompliance.Warnings.
 */
class WarningCheckBox
    extends JCheckBox {

    /**
     * The message warning Pattern associated with this checkbox.
     */
    private ISTPCompliance.Warnings warning = null;


    /**
     * Creates a WarningCheckBox with the given text and 
     * ISTPCompliance.Warnings.
     *
     * @param text text that is displayed next to this checkbox.
     * @param warning the ISTPCompliance.Warnings associated with this
     *     checkbox.
     */
    public WarningCheckBox(
        String text,
        ISTPCompliance.Warnings warning) {

        super(text);
        this.warning = warning;
    }


    /**
     * Gets the ISTPCompliance.Warnings represented by this checkbox.
     *
     * @return the ISTPCompliance.Warnings represented by this 
     *     checkbox.
     */
    public ISTPCompliance.Warnings getWarning() {

        return warning;
    }
}
