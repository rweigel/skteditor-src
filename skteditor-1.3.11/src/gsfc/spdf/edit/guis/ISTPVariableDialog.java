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
 * $Id: ISTPVariableDialog.java,v 1.51 2022/08/02 12:16:51 btharris Exp $
 */
package gsfc.spdf.edit.guis;

import gsfc.spdf.edit.util.SKTUtils;
import gsfc.spdf.istp.VirtualVariable;
import gsfc.spdf.istp.ISTPComplianceException;

import gsfc.nssdc.cdf.CDF;
import gsfc.nssdc.cdf.CDFConstants;
import gsfc.nssdc.cdf.CDFException;
import gsfc.nssdc.cdf.Variable;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * ISTPVariableDialog.java
 * 
 * 
 * Created: Tue Mar 30 11:11:01 1999
 * 
 * @author P. Williams
 * @version $Revision: 1.51 $
 */
//public class ISTPVariableDialog extends gsfc.spdf.gui.VariableDialog
public class ISTPVariableDialog 
    implements CDFConstants {
    

    /**
     * Creates an ISTP support data variable after presenting a dialog to the 
     * user to obtain the characteristics of the variable to be created.
     * 
     * @param frame parent frame of dialog
     * @param cdf the CDF in which to create the virtual variable
     * @param createEpoch whether to create an epoch variable.
     * @param var existing variable that the new variable is support data for
     * @param index dimension index of var that the new variable is
     *              support data for
     * @param supportDataType data type.
     * @return new support data variable (may be null if user cancels operation)
     */
    public static Variable createSupportData(JFrame frame, CDF cdf, 
                                             boolean createEpoch, Variable var,
                                             int index,String supportDataType) {

        long [] sizes;               // the new variable's dimension sizes

        if (var.getNumDims() == 0 || createEpoch) {

            sizes = new long [] {1};
        }
        else {

            sizes = new long [] {var.getDimSizes()[index]};
        };

        gsfc.spdf.istp.Variable newVar = null;      
                                       // the new support data variable 
        boolean retryableCreationError = false;
                                     // indicates whether a retryable error
                                     //  occurred during variable creation

        do {
            NewVariableDialog dialog;

            if (createEpoch) {
                                
                dialog = new NewVariableDialog(frame,
                                            "Create New Epoch Variable", 
                                            true, cdf, 
                                            "Epoch",
                                            CDF.CDF_TIME_TT2000, 1, 
                                            true, 0, 
                                            sizes, new long[] {CDF.VARY});
                
                if(cdf.getVersion() != null) {
                    
                    if(cdf.getVersion().startsWith("2")){

                        dialog.setDataTypeEnabled(false);
                    }
                
                    else {
                    
                        dialog.createEpochVarAdjust(cdf.getVersion());
                    }
                }
                dialog.setRecordVarianceEnabled(false);
            }
            else if (supportDataType.indexOf("DELTA_") > -1){
                
                dialog = new NewVariableDialog(frame,
                                            "Create New Delta Variable", 
                                            true, cdf, 
                                            var.getName()
                                            + "_DELTA_VAR",
                                            var.getDataType(),var.getNumElements(),//1,
                                            var.getRecVariance(),var.getNumDims(),
                                            var.getNumDims() >0 ? var.getDimSizes():sizes,
                                            var.getNumDims() >0 ? var.getDimVariances():new long[] {CDF.VARY});                               
                
            }
            else {

                dialog = new NewVariableDialog(frame,
                                            "Create New Support Data Variable", 
                                            true, cdf, 
                                            var.getName()
                                            + "_" + 
                                           supportDataType,
                                            var.getDataType(),1,
                                            //CDF.CDF_REAL8, 1, 
                                            false, 1, 
                                            sizes, new long[] {CDF.VARY});
            };

            dialog.setSize(350, 450);
            dialog.setDimensionInfoEnabled(false);

            dialog.show();

            int choice = dialog.getOption();

            if (choice == JOptionPane.YES_OPTION) {

                try {

                    long compressionType = CDF.NO_COMPRESSION;
                                       // variable's compression type
                    long[] compressionParms = new long[] {};
                                       // variable's compression 
                                       // parameters
                    if (dialog.getCompression()) {

                        compressionType = CDF.GZIP_COMPRESSION;
                        compressionParms =
                            gsfc.spdf.istp.Variable.
                                DEFAULT_GZIP_COMPRESSION_PARMS;
                    }
                    newVar = gsfc.spdf.istp.Variable.getInstance(
                        cdf, dialog.getVariableName(),
                        dialog.getDataType(),
                        dialog.getNumberOfElements(),
                        dialog.getDimension(),
                        dialog.getDimensionSizes(),
                        dialog.getRecordVariance(),
                        dialog.getDimensionVariances(),
                        gsfc.spdf.istp.Variable.SUPPORT_DATA, true,
                        compressionType, compressionParms,
                        dialog.getSparseRecordType()
                    );
                    Object padValue = dialog.getPadValue();
                    if (padValue != null) {

                        newVar.setPadValue(padValue);
                    }

                    if (supportDataType.startsWith("DEPEND") &&
                        !newVar.isRecordVarying() &&
                        newVar.getDataType() != CDF.CDF_CHAR &&
                        newVar.getDataType() != CDF.CDF_UCHAR) {

                        String description = "Comp No " + 
                                             supportDataType.charAt(
                                                 supportDataType.length() - 1);
                                       // DEPEND var description

                        newVar.setAttributeValue("FIELDNAM", description);
                        newVar.setAttributeValue("CATDESC", description);
                        newVar.setAttributeValue("LABLAXIS", description);
                    }
                    retryableCreationError = false;
                }
                catch (ISTPComplianceException e) {

                    retryableCreationError = true;

                    JOptionPane.showMessageDialog(frame, 
                                 "Error creating metadata variable '" +
                                 dialog.getVariableName() + "':\n" +
                                 e.getMessage(),
                                 "Create metadata Variable Error",
                                 JOptionPane.ERROR_MESSAGE);
                }
                catch(CDFException except) {

                    if (except.getCurrentStatus() == CDF.VAR_EXISTS) {

                        // the ISTPComplianceException above should have 
                        //  eliminated this case
                        retryableCreationError = true;
                    }
                    else {

                        except.printStackTrace();
                    };

                    JOptionPane.showMessageDialog(frame, 
                                 "Error creating metadata variable '" +
                                 dialog.getVariableName() + "':\n" +
                                 except.getMessage(),
                                 "Create metadata Variable Error",
                                 JOptionPane.ERROR_MESSAGE);
                };
            }
            else {  // creation cancelled

                retryableCreationError = false;
            };

        } while (retryableCreationError);

	return newVar != null ? newVar.getCdfVariable() : null;
    }


    /**
     * Creates an ISTP virtual variable after presenting a dialog to the user to
     * obtain the characteristics of the variable to be created.
     * 
     * @param frame parent frame of dialog
     * @param cdf the CDF in which to create the virtual variable
     * @param baseVariable an existing variable upon which to base the new 
     *                     virtual variable
     * @return new virtual variable (may be null if user cancels operation)
     */
    public static Variable createVirtual(JFrame frame, CDF cdf,
                                         Variable baseVariable) {

        gsfc.spdf.istp.Variable newVar = null;
                                       // new virtual variable

        NewVirtualVariableDialog dialog = new NewVirtualVariableDialog(frame,
                                             "Create New Virtual Variable",
                                             true, cdf);
        dialog.setSize(350, 650);
        dialog.setBaseVariable(baseVariable);
        dialog.show();

        int choice = dialog.getOption();

        if (choice == JOptionPane.YES_OPTION) {

            try {
                
                //
                // We use "new VirtualVariable()" instead of 
                // "Variable.createVirtualVariable() below because
                // createVirtualVariable does not allow the virtual 
                // variable to have a time varying component that is
                // different from the base variable (something which
                // is required by the THEMIS project.
                //
                long compressionType = CDF.NO_COMPRESSION;
                                       // variable's compression type
                long[] compressionParms = new long[] {};
                                       // variable's compression 
                                       // parameters
                if (dialog.getCompression()) {

                    compressionType = CDF.GZIP_COMPRESSION;
                    compressionParms =
                        gsfc.spdf.istp.Variable.
                            DEFAULT_GZIP_COMPRESSION_PARMS;
                }
                newVar = new VirtualVariable(
                             cdf, dialog.getVariableName(),
                             dialog.getDataType(),
                             dialog.getNumberOfElements(),
                             dialog.getDimension(),
                             dialog.getDimensionSizes(),
                             dialog.getRecordVariance(),
                             dialog.getDimensionVariances(),
                             gsfc.spdf.istp.Variable.DATA,
                             compressionType, compressionParms,
                             dialog.getSparseRecordType(),
                             dialog.getFunctionValue(),
                             dialog.getComponentValues());
Object padValue = dialog.getPadValue();
if (padValue != null) {

    newVar.setPadValue(padValue);
}
            }
            catch (ISTPComplianceException e) {

                JOptionPane.showMessageDialog(frame, 
                                 "Error creating virtual variable '" +
                                 dialog.getVariableName() + "': " +
                                 e.getMessage(),
                                 "Create Virtual Variable Error",
                                 JOptionPane.ERROR_MESSAGE);
            }
            catch(CDFException except) {

                except.printStackTrace();

                JOptionPane.showMessageDialog(frame, 
                                 "Error creating virtual variable '" +
                                 dialog.getVariableName() + "': " +
                                 except.getMessage(),
                                 "Create Virtual Variable Error",
                                 JOptionPane.ERROR_MESSAGE);
            };
        };

	return newVar != null ? newVar.getCdfVariable() : null;
    }
    

    /**
     * Creates an ISTP metadata variable after presenting a dialog to 
     * the user to obtain the characteristics of the variable to be 
     * created.
     * 
     * @param frame parent frame of dialog
     * @param cdf the CDF in which to create the virtual variable
     * @param var existing variable that the new variable is metadata for
     * @param index dimension index of var that the new variable is
     *              metadata for
     * @param metadataType identifies the type of metadata variable to 
     *                     create (e.g., "LABL_1", "FORM")
     * @return new metadata variable (may be null if user cancels operation)
     */
    public static Variable createMetadata(JFrame frame, CDF cdf, 
                                          Variable var, int index,
                                          String metadataType) {

        long [] sizes;               // the new variable's dimension sizes

        if (var.getNumDims() == 0) {

            sizes = new long [] {1};
        }
        else {

            sizes = new long [] {var.getDimSizes()[index]};
        };

        gsfc.spdf.istp.Variable newVar = null;      
                                       // the new metadata variable 
        int numElements = 20;        // the variable's number of elements
        boolean recordVariance = false;
                                     // the variable's record variance
        boolean retryableCreationError = false;
                                     // indicates whether a retryable error
                                     //  occurred during variable creation

        do {
            NewVariableDialog dialog = new NewVariableDialog(frame,
                                            "Create New metadata Variable", 
                                            true, cdf, 
                                            var.getName() + "_" + metadataType,
                                            CDF.CDF_CHAR, numElements, 
                                            recordVariance, 1, 
                                            sizes, new long[] {CDF.VARY});

            dialog.setSize(350, 450);
            dialog.setDataTypeEnabled(false);
            dialog.setDimensionInfoEnabled(false);

            dialog.show();

            int choice = dialog.getOption();

            if (choice == JOptionPane.YES_OPTION) {

                try {

                    long compressionType = CDF.NO_COMPRESSION;
                                       // variable's compression type
                    long[] compressionParms = new long[] {};
                                       // variable's compression 
                                       // parameters
                    if (dialog.getCompression()) {

                        compressionType = CDF.GZIP_COMPRESSION;
                        compressionParms =
                            gsfc.spdf.istp.Variable.
                                DEFAULT_GZIP_COMPRESSION_PARMS;
                    }

                    String newVarName = dialog.getVariableName();
                                       // new variable's name

                    newVar = gsfc.spdf.istp.Variable.getInstance(
                        cdf, newVarName, dialog.getDataType(), 
                        numElements, dialog.getDimension(), 
                        dialog.getDimensionSizes(),
                        dialog.getRecordVariance(),
                        dialog.getDimensionVariances(), 
                        gsfc.spdf.istp.Variable.METADATA,
                        metadataType.equals("SCAL"),
                        compressionType, compressionParms,
dialog.getSparseRecordType());

Object padValue = dialog.getPadValue();
if (padValue != null) {

    newVar.setPadValue(padValue);
}
                    retryableCreationError = false;
                }
                catch (ISTPComplianceException e) {

                    retryableCreationError = true;

                    JOptionPane.showMessageDialog(frame, 
                                 "Error creating metadata variable '" +
                                 dialog.getVariableName() + "':\n" +
                                 e.getMessage(),
                                 "Create metadata Variable Error",
                                 JOptionPane.ERROR_MESSAGE);
                }
                catch(CDFException except) {

                    if (except.getCurrentStatus() == CDF.VAR_EXISTS) {

                        // the ISTPComplianceException above should have 
                        //  eliminated this case
                        retryableCreationError = true;
                    }
                    else {

                        except.printStackTrace();
                    };

                    JOptionPane.showMessageDialog(frame, 
                                 "Error creating metadata variable '" +
                                 dialog.getVariableName() + "':\n" +
                                 except.getMessage(),
                                 "Create metadata Variable Error",
                                 JOptionPane.ERROR_MESSAGE);
                };
            }
            else {  // creation cancelled

                retryableCreationError = false;
            };

        } while (retryableCreationError);

	return newVar != null ? newVar.getCdfVariable() : null;
    }


    /**
     * Creates an ISTP variable after presenting a dialog to the user to
     * obtain the characteristics of the variable to be created.
     * 
     * @param frame parent frame of dialog
     * @param cdf the CDF in which to create the virtual variable
     * @return new variable (may be null if user cancels operation)
     */
    public static Variable create(JFrame frame, CDF cdf) {

        gsfc.spdf.istp.Variable newVar = null;
                                       // new variable

        NewVariableDialog dialog = new NewVariableDialog(frame,
                                            "Create New Variable", 
                                            true, cdf);

        dialog.setSize(350, 450);

        dialog.show();

        int choice = dialog.getOption();

        if (choice == JOptionPane.YES_OPTION) {

            try {

                long compressionType = CDF.NO_COMPRESSION;
                                       // variable's compression type
                long[] compressionParms = new long[] {};
                                       // variable's compression 
                                       // parameters
                if (dialog.getCompression()) {

                    compressionType = CDF.GZIP_COMPRESSION;
                    compressionParms =
                        gsfc.spdf.istp.Variable.
                            DEFAULT_GZIP_COMPRESSION_PARMS;
                }

                newVar = gsfc.spdf.istp.Variable.getInstance(
                             cdf, dialog.getVariableName(),
                             dialog.getDataType(), 
                             dialog.getNumberOfElements(),
                             dialog.getDimension(), 
                             dialog.getDimensionSizes(),
                             dialog.getRecordVariance(),
                             dialog.getDimensionVariances(),
                             gsfc.spdf.istp.Variable.SUPPORT_DATA, true,
                             compressionType, compressionParms,
                             dialog.getSparseRecordType());

                Object padValue = dialog.getPadValue();
                if (padValue != null) {

                    newVar.setPadValue(padValue);
                }
            }
            catch (ISTPComplianceException e) {

                JOptionPane.showMessageDialog(frame, 
                                 "Error creating variable '" +
                                 dialog.getVariableName() + "':\n" +
                                 e.getMessage(),
                                 "Create Variable Error",
                                 JOptionPane.ERROR_MESSAGE);
            }
            catch (CDFException except) {

                if (except.getCurrentStatus() != CDF.VAR_EXISTS) {

                    except.printStackTrace();
                };

                JOptionPane.showMessageDialog(frame, 
                                 "Error creating variable '" +
                                 dialog.getVariableName() + "':\n" +
                                 except.getMessage(),
                                 "Create Variable Error",
                                 JOptionPane.ERROR_MESSAGE);
            }
            catch (OutOfMemoryError e) {

                JOptionPane.showMessageDialog(frame, 
                    "Out of memory error while creating variable '" +
                    dialog.getVariableName() + "'.\n" +
                    "Contact gsfc-spdf-support@lists.nasa.gov\n" +
                    "for instructions on increasing Java's memory.",
                    "Create Variable Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }

	return newVar != null ? newVar.getCdfVariable() : null;
    }

} // ISTPVariableDialog
