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
 * Copyright (c) 2022 United States Government as represented by
 * the National Aeronautics and Space Administration. No copyright is
 * claimed in the United States under Title 17, U.S.Code. All Other
 * Rights Reserved.
 *
 * $Id: VariableListTransferHandler.java,v 1.1 2022/08/25 13:11:14 btharris Exp $
 */
package gsfc.spdf.edit.guis;


import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.TransferHandler;

import gsfc.nssdc.cdf.CDFException;


/**
 * This class provides a TransferHandler to facilitate drag and drop of
 * variables on the VariablePanel.
 *
 * @author B. Harris
 */
public class VariableListTransferHandler
    extends TransferHandler {

    /**
     * SKTEditor.
     */
    private SKTEditor editor;

    /**
     * List of variables.
     */
    private JList list;

    private int dropIndex = -1;
    private int oldIndex = -1;

    /**
     * Creates a VariableListTransferHandler to facilitate drag and drop
     * of variables on the given variableList.
     *
     * @param editor SKTEditor containing the variableList.
     * @param variableList the JList containing CDF variables names.
     */
    public VariableListTransferHandler(
        SKTEditor editor,
        JList variableList) {

        this.editor = editor;
        list = variableList;
    }

    @Override
    public boolean canImport(
        TransferHandler.TransferSupport info) {

        return info.isDataFlavorSupported(DataFlavor.stringFlavor);
    }
 
    @Override
    public boolean importData(
        TransferHandler.TransferSupport info) {

        if (!info.isDrop()) {

            return false;
        }
                 
        if (!info.isDataFlavorSupported(DataFlavor.stringFlavor)) {

            return false;
        }
 
        JList.DropLocation dl = (JList.DropLocation)info.getDropLocation();

        DefaultListModel<String> listModel = 
            (DefaultListModel<String>)list.getModel();

        dropIndex = dl.getIndex();
        boolean insert = dl.isInsert();
        // Get the strings that are being dropped.
        Transferable transferable = info.getTransferable();
        String[] selectedValues = getStrings(transferable);

        String varName;
        if (selectedValues != null && selectedValues.length > 0) {

            varName = selectedValues[0];
        }
        else {

            return false;
        }
        try {

            String dropValue = null;
            if (dropIndex == listModel.size()) {

                dropValue = listModel.lastElement();
            }
            else {

                dropValue = listModel.get(dropIndex);
            }

            oldIndex = listModel.indexOf(selectedValues[0]);

            editor.moveVariables(Arrays.asList(selectedValues), dropIndex); 

            for (int i = selectedValues.length - 1; i >= 0; i--) {

                listModel.add(dropIndex, selectedValues[i]);
            }
        }
        catch (CDFException e) {

            System.err.println("VariableListTransferHandler.importData: " +
                "moveVariable failed with CDFException: " + e.getMessage());
            return false;
        }

        return true;
    }


    @Override
    protected void exportDone(
        JComponent c, Transferable data, int action) {

        String[] selectedValues = getStrings(data);

        if (action == MOVE) {

            JList source = (JList)c;
            DefaultListModel listModel = (DefaultListModel)source.getModel();

            if (oldIndex > dropIndex) {

                for (int i = 0; i < selectedValues.length; i++) {

                    listModel.remove(listModel.lastIndexOf(selectedValues[i]));
                }
            }
            else {

                for (int i = 0; i < selectedValues.length; i++) {

                    listModel.remove(listModel.indexOf(selectedValues[i]));
                }
            }

        }
    }


    @Override
    public int getSourceActions(
        JComponent component) {

        return MOVE;
    }
             
    @Override
    protected Transferable createTransferable(
        JComponent component) {

        return getStringSelection((JList)component);
    }


    /**
     * Creates a StringSelection containing all the selected values from
     * the given JList.  The individual values are separated by a newline
     * character.
     *
     * @param list list from which to obtain selected values.
     * @return a StringSelection containing all the selected values from
     *     list.
     */
    private StringSelection getStringSelection(
        JList list) {

        List<String> values = list.getSelectedValuesList();

        StringBuilder buff = new StringBuilder();

        for (int i = 0; i < values.size(); i++) {

            String value = values.get(i);
            buff.append(value == null ? "" : value);
            if (i != values.size() - 1) {

                buff.append("\n");
            }
        }
        return new StringSelection(buff.toString());
    }


    /**
     * Splits the String values in the given Transferable separated by
     * newline characters.
     *
     * @param transferable the Transferable whose String data is to be
     *     split.
     * @return String values obtained from the given Transferable.
     */
    private String[] getStrings(
        Transferable transferable) {

        try {

            String data = (String)transferable.getTransferData(
                               DataFlavor.stringFlavor);

            return data.split("[\\n]");
        } 
        catch (Exception e) { 

            return new String[] {};
        }
    }
}
