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
 * $Id: ValuePanel.java,v 1.49 2022/08/02 12:12:00 btharris Exp $
 */
package gsfc.spdf.edit.guis;

// Swing Imports
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import javax.swing.table.*;
import javax.swing.event.ChangeEvent;

// Java imports
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.lang.reflect.*;

// CDF Imports
import gsfc.nssdc.cdf.*;
import gsfc.nssdc.cdf.util.*;

// SPDF imports

import gsfc.spdf.gui.*;
import gsfc.spdf.util.*;
import gsfc.spdf.table.*;
import gsfc.spdf.istp.TerrestrialTime2000;

// Local Imports
import gsfc.spdf.edit.events.*;
import gsfc.spdf.edit.util.SKTUtils;

/**
 * A panel to display a variable's values and VALIDMIN/VALIDMAX
 */
public class ValuePanel
    extends JPanel
    implements AttributeChangeListener, CDFConstants, VariableEventListener
{
    protected VariablePanel myVP;
    private LabeledDataTablePanel values, min, max;

    public ValuePanel(VariablePanel myVP) {
	super(true);
	this.myVP = myVP;

	GridBagLayout gbl = new GridBagLayout();
	GridBagConstraints gbc = new GridBagConstraints();
	setLayout(gbl);
	
	gbc.weightx = 1.0;
	gbc.weighty = 1.0;
	gbc.fill = GridBagConstraints.BOTH;
	gbc.anchor = GridBagConstraints.CENTER;

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridheight = 2;
	values = new LabeledDataTablePanel("Values");
	gbl.setConstraints(values, gbc);
	add(values);

    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.gridheight = 1;
	gbc.gridwidth = GridBagConstraints.RELATIVE;
	min = new LabeledDataTablePanel("Valid Min");
	gbl.setConstraints(min, gbc);
	add(min);

    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.gridheight = 1;
	gbc.gridwidth = GridBagConstraints.REMAINDER;
	max = new LabeledDataTablePanel("Valid Max");
	gbl.setConstraints(max, gbc);
	add(max);
    }

    public void reset() {
	values.setVisible(false);
    values.setInputComponentValue(0, null);
	min.setVisible(false);
    min.setInputComponentValue(0, null);
	max.setVisible(false);
    max.setInputComponentValue(0, null);
    }

    public void set(Variable var) {
	setValues(var);
	setMinMax(var);
    }

    /** 
     * Set the value table (useful only for metadata)
     *
     * @param var CDF variable.
     */
    public void setValues(Variable var) {
	Object data = null;
	long [] sizes;
	boolean variance = var.getRecVariance();
	long nDims = var.getNumDims();
	long datatype = var.getDataType();

	// Only display NRV 1D or scalar variable's values
	if (!var.getRecVariance() && (var.getNumDims() <= 1)) {

	    System.err.println("ValuePanel:\tsetting values");
	    if (var.getNumDims() > 0 && var.getDimSizes() != null) {

                sizes = var.getDimSizes();
            }
	    else {

                sizes = new long [] {1};
            }
	    try {
		data = var.getRecord(0);
                
                if (datatype == CDF_TIME_TT2000) {

                    data = TerrestrialTime2000.toString(data);
                }
                else if (datatype == CDF_EPOCH) {

                    data = gsfc.spdf.istp.Epoch8.toString(data);
                }
                else if (datatype == CDF_EPOCH16) {

                    data = gsfc.spdf.istp.Epoch16.toString(data);
                }
                 
                if (data != null)   
                    values.setInputComponentValue(0, 
                            SKTUtils.stripBlanks(data));           
		    values.setVisible(true);				
            }
            catch (CDFException e) {

                values.createEmptyTable(0, (int)sizes[0], 1, 
                    getTableClass(datatype));
            
                values.setVisible(true);
                System.err.println("ValuePanel:\tbuilt empty table");
	    }
	}
    }


    /**
     * Gets the Java Class that we use to represent the given CDF
     * datatype in a table.
     *
     * @param datatype CDF datatype
     * @return Java Class used to represent the given CDF datatype
     *             in a table.
     */
    private Class getTableClass(long datatype) {

        switch ((int)datatype) {

        case (int)CDF_BYTE: 
        case (int)CDF_INT1:

            return Byte.TYPE;
        case (int)CDF_INT2:
        case (int)CDF_UINT1:

            return Short.TYPE;
        case (int)CDF_INT4:
        case (int)CDF_UINT2:

            return Integer.TYPE;
        case (int)CDF_INT8:
        case (int)CDF_UINT4:

            return Long.TYPE;
        case (int)CDF_REAL4:
        case (int)CDF_FLOAT:

            return Float.TYPE;
        case (int)CDF_REAL8:
        case (int)CDF_DOUBLE:

            return Double.TYPE;
        default:

            return String.class;
        }
    }


    private Object convertCdfTimeToString(
        Object value, long datatype) {

        if (datatype == CDF_TIME_TT2000) {

            return CDFTT2000.toUTCstring(((Long)value).longValue());
        }
        else if (datatype == CDF_EPOCH) {

            return Epoch.encode(((Double)value).doubleValue());
        }
        else if (datatype == CDF_EPOCH16) {

            return Epoch16.encode(value);
        }

        return value;
    }


    /**
     * Set the min and max tables
     *
     * @param var CDF variable.
     */
    private  void setMinMax(Variable var) {

	Object minData = null;
        Object maxData = null;
	boolean buildMaxTable = true, buildMinTable = true;
	long datatype = var.getDataType();
	// Only need to set min & max if not character variable
	if ((datatype != CDF_CHAR) && (datatype != CDF_UCHAR)) {

	    // Get the size of the tables

            gsfc.spdf.istp.Variable istpVar = 
                new gsfc.spdf.istp.Variable(var);
                                       // ISTP representation of var
            Number[] validMinNumber = istpVar.getValidMinNumber();
                                       // VALIDMIN value(s)
            int tableSize = 1;         // required size of table
            if (validMinNumber != null && datatype != CDF_EPOCH16) {

               tableSize = validMinNumber.length;
            }
	    
	    // Get the table class
	    Class tableClass = getTableClass(datatype);

	    // See if there are attributes defined
	    try {

		minData = var.getEntryData("VALIDMIN");

                minData = convertCdfTimeToString(minData, datatype);


                if (!minData.getClass().isArray()&& tableSize >1 && minData instanceof Number){

                    min.setInputComponentValue(0,getExtremeArray((int)datatype,tableSize, minData));                        
                }
                    
                else
                    min.setInputComponentValue(0, minData);
                
		buildMinTable = false;
               
       
	    } catch (Exception e) {
		    myVP.myEditor.setStatus("No VALIDMIN for "+
					var.toString(),
					StatusBar.ERROR, true, true);
	    }
	    try {

		maxData = var.getEntryData("VALIDMAX");

                maxData = convertCdfTimeToString(maxData, datatype);
                                      
                if (!maxData.getClass().isArray()&& tableSize >1 && maxData instanceof Number){
                        
                    max.setInputComponentValue(0,getExtremeArray((int)datatype,tableSize, maxData));                        
                }
                    
                else
                    max.setInputComponentValue(0, maxData);
                
		buildMaxTable = false;
               
	    } catch (Exception e) {
		    myVP.myEditor.setStatus("No VALIDMAX for "+
					var.toString(),
					StatusBar.ERROR, true, true);
	    }
	    if (buildMaxTable) {
		    System.err.println("Building empty maxTable.");
            max.createEmptyTable(0, tableSize, 1, tableClass);
	    } else 
            max.setNumRows(0, tableSize);
	    if (buildMinTable) {
		    System.err.println("Building empty minTable.");
            min.createEmptyTable(0, tableSize, 1, tableClass);
	    } else 
            min.setNumRows(0, tableSize);
	    max.setVisible(true);
	    min.setVisible(true);
	} else {

        max.setInputComponentValue(0, null);
	    max.setVisible(false);
        min.setInputComponentValue(0, null);
	    min.setVisible(false);
	}
    }
    
    private Object getExtremeArray(int type, int size, Object value){
        
        switch (type){
                            
            case (int)CDF_BYTE: 
	    case (int)CDF_INT1:    
                              
                byte[] dataB = new byte[size];
                for(int i = 0; i < size; i++)
                                   
                    dataB[i] = (((Byte)value).byteValue());
                                                   
                return dataB;                  
                             
                           
            case (int)CDF_INT2:
	    case (int)CDF_UINT1:   
                    
                short[] dataS = new short[size];
                for(int i = 0; i < size; i++)
                                   
                    dataS[i] = (((Short)value).shortValue());
                return dataS;
	       
            case (int)CDF_INT4:
	    case (int)CDF_UINT2:   
                
                int[] dataI = new int[size];                              
                for(int i = 0; i < size; i++)
                                   
                    dataI[i] = (((Integer)value).intValue());
               return dataI;
	       
            case (int)CDF_INT8:    
            case (int)CDF_UINT4:    
               
                long[] dataL = new long[size];
                for(int i = 0; i < size; i++)
                                   
                    dataL[i] = (((Long)value).longValue());
                return dataL;
           
            case (int)CDF_REAL4:	        
            case (int)CDF_FLOAT:  
                                    
                float[] dataF = new float[size];
                for(int i = 0; i < size; i++)
                                   
                    dataF[i] = (((Float)value).floatValue());
                return dataF;

            case (int)CDF_REAL8:	        
            case (int)CDF_DOUBLE:       
            
                double[] dataD = new double[size];                              
                for(int i = 0; i < size; i++)
                                   
                    dataD[i] = (((Double)value).doubleValue());
                return dataD;
 
         }
        return null;
        
    }

    private void buildTables(Variable var) {
	boolean buildMinMax = true;
	boolean buildValues = true;
	try {
//	    String vartype = ((String)var.getEntryData("VAR_TYPE")).
//		toLowerCase();
	    String vartype = VarType.get(var);

	    if (vartype.equalsIgnoreCase("metadata"))
		buildMinMax = false;
	    if (!var.getRecVariance() && (var.getNumDims() <= 1))
		    buildValues = false;
	    // Get the size of the tables
	    int tableSize, valueSize;
	    long numDims = var.getNumDims();
	    long datatype = var.getDataType();
	    if (numDims == 1) {
	        tableSize = (int)var.getDimSizes()[0];
                if (tableSize > 3) {
                
		    tableSize = 1;
                }
	    } else
		tableSize = 1;
		
            Class tableClass = getTableClass(datatype);
	    
	    if (buildMinMax) {
            max.createEmptyTable(0, tableSize, 1, tableClass);
            min.createEmptyTable(0, tableSize, 1, tableClass);
		    max.setVisible(true);
		    min.setVisible(true);
	    }
	    if (buildValues) {
            values.createEmptyTable(0, tableSize, 1, tableClass);
		    values.setVisible(true);
	    }
	} catch (CDFException vtexc) {
	    // should never happend since var_type should always be defined
	    System.err.println("ValuePanel.buildTables: should never happen");
	    vtexc.printStackTrace();
	}
    }

  private void saveValues(Variable var) {

	Object data = null;
	CDF cdf = var.getMyCDF();
	Attribute curAttr;
	Vector varAttrs = cdf.getVariableAttributes();
	Hashtable curVarAttrs = new Hashtable();
	long numElements, datatype, numDims;
	long [] lsizes, dimVarys;
	boolean recVary;
	numDims = var.getNumDims();
	recVary = var.getRecVariance();

	try {

	    String vartype = VarType.get(var);

            // If this is a NRV then get the data
            if (((numDims==0)||(numDims==1))&& (!recVary)){

                data = values.getInputComponentValue(0);
                                                
                if (data != null) {
                                                        
                    if (var.getDataType() == CDF_TIME_TT2000) {

                        try {

                            data = new Long(CDFTT2000.fromUTCstring(
                                                (String)data));
                        }
                        catch (CDFException e) {

                            String errMsg = e.getMessage() != null ?
                                                e.getMessage() :
                                                "Invalid epoch value";

                            myVP.myEditor.setStatus(
                                errMsg, StatusBar.ERROR, true, true);
                        }
                        catch (NoSuchElementException nsee) {

                            myVP.myEditor.setStatus(
                                "Missing epoch component", 
                                StatusBar.ERROR, true, true);
                        }
                    }
                    else if (var.getDataType() == CDF_EPOCH) {

			    double epoch;
                            try {

                                epoch = Epoch.parse((String)data);
                                data = new Double(epoch);
                            } 
                            catch (CDFException e) {

                                String errMsg = e.getMessage() != null ?
                                                e.getMessage() :
                                                "Invalid epoch value";

                                myVP.myEditor.setStatus(errMsg,
						    StatusBar.ERROR, 
						    true, true);
                            }
                            catch (NoSuchElementException nsee) {

                                myVP.myEditor.setStatus("Missing epoch component", 
						    StatusBar.ERROR, 
						    true, true);
                            };
                        
                    }
                    else if (var.getDataType() == CDF_EPOCH16 ) {
                                             			     
                            try {
                                                     
                                data = (double[])Epoch16.parse((String)data);			        
                            } 
                            catch (CDFException e) {

                                String errMsg = e.getMessage() != null ?
                                                e.getMessage() :
                                                "Invalid epoch16 value";

                                myVP.myEditor.setStatus(errMsg,
						    StatusBar.ERROR, 
						    true, true);
                            }
                            catch (NoSuchElementException nsee) {
	
                            };
                      
                    }
		                        
                }                
                
                if (vartype.equalsIgnoreCase("metadata") && data != null &&
                    data.getClass().isArray() &&
                    data.getClass().getComponentType().isInstance("")) {

                    // the last condition above is necessary because even
                    //  though metadata should always be of type String, the
                    //  editor doesn't currently enforce it

                    SKTUtils.verifyStringArray((String [])data);
                    
                   numElements = SKTUtils.getMaxNumElements(data);
                   SKTUtils.stripBlanks(data);
                   
                   if (SKTUtils.getMaxNumElements(data)< numElements) {
                       
                         JOptionPane.showMessageDialog(null,    
                                "WARNING\n"+
                                "The variable \""+ var.getName()+
                                "\" values extra blanks have been removed."+
                                "\nSaving this file will result in a permanent change.");              
                    }; 
                };

                numElements = SKTUtils.getMaxNumElements(data);
            } 
            else {
                numElements = 1;	    
            };
 
            // Need to recreate the data
            if (numElements > var.getNumElements()) {
                            
                gsfc.spdf.istp.Variable istpVar = 
                    new gsfc.spdf.istp.Variable(var);
                                       // istp representation of var
                gsfc.spdf.istp.Variable newVar = 
                    istpVar.copy(numElements);
                                       // copy of istpVar with 
                                       // specified numElements
                var = newVar.getCdfVariable();
                myVP.updateVarPanel(var);
	    }
 
	    // Save the data if there is any
	    if (data != null) {

		    var.putRecord(0, data);
	    }
	} catch (CDFException vtexc) {
	    System.err.println("ValuePanel.saveValues: "+
			       "Should never happen: ("+var+"): "+
			       vtexc.getMessage());
	}
        catch (Exception e) {

            e.printStackTrace();
        };
    }


    /**
     * Converts the given String representation of time into the 
     * specified timeType.  If timeType is not a time datatype, the
     * original object is returned unchanged.
     *
     * @param value String representation of time to convert.
     * @param timeType target CDF time datatype.
     * @return CDF time representation of the given string value.  If
     *     timeType is not a time datatype, the original object is
     *     returned.
     */
    private Object convertStringToTime(Object value, long timeType) {

        if (timeType == CDF_TIME_TT2000) {
                            
            try {

                long epoch = CDFTT2000.fromUTCstring((String)value);

                return new Long(epoch);
            } 
            catch (CDFException e) {

                String errMsg = e.getMessage() != null ?
                                    e.getMessage() :
                                    "Invalid epoch value";

                myVP.myEditor.setStatus(errMsg, StatusBar.ERROR, 
                    true, true);
            }
            catch (NoSuchElementException nsee) {

                myVP.myEditor.setStatus("Missing epoch component", 
                    StatusBar.ERROR, true, true);
            }
        }
        else if (timeType == CDF_EPOCH) {
                            
            try {

                double epoch = Epoch.parse((String)value);

                return new Double(epoch);
            } 
            catch (CDFException e) {

                String errMsg = e.getMessage() != null ?
                                    e.getMessage() :
                                    "Invalid epoch value";

                myVP.myEditor.setStatus(errMsg, StatusBar.ERROR, 
                    true, true);
            }
            catch (NoSuchElementException nsee) {

                myVP.myEditor.setStatus("Missing epoch component", 
                    StatusBar.ERROR, true, true);
            }
        }
        else if (timeType == CDF_EPOCH16 ) {
                        
            try {
                                                     
                return (double[])Epoch16.parse((String)value);
            } 
            catch (CDFException e) {

                String errMsg = e.getMessage() != null ?
                                    e.getMessage() :
                                    "Invalid epoch16 value";

                myVP.myEditor.setStatus(errMsg, StatusBar.ERROR, 
                    true, true);
            }
            catch (NoSuchElementException nsee) {
	
            }
        }

        return value;
    }

		    
    private void saveMinMax(Variable var) {
	long datatype = var.getDataType();
	Object minData, maxData;

	try {
//	    String vartype = ((String)var.getEntryData("VAR_TYPE")).
//		toLowerCase();
	    String vartype = VarType.get(var);

	    if (!vartype.equals("metadata")) {
                
                minData = min.getInputComponentValue(0);
                maxData = max.getInputComponentValue(0);

                if (minData != null) {
                                                        
                    if(!minData.getClass().isArray()) {

                        minData = 
                            convertStringToTime(minData, datatype);
                    }
		    
                    try {
			    
                        SKTUtils.putVattrEntry(var, "VALIDMIN", 
					       datatype, minData);
                    } catch (CDFException e) {
			    myVP.myEditor.setStatus(e.getMessage(), 
						StatusBar.ERROR, 
						true, true);
                    }
                }
		
                if (maxData != null) {
                                                        
                    if(!maxData.getClass().isArray()) {

                        maxData =
                            convertStringToTime(maxData, datatype);
                    }
                    		    
		    try {
			SKTUtils.putVattrEntry(var, "VALIDMAX", 
					       datatype, maxData);
		    } catch (CDFException e) {
			    myVP.myEditor.setStatus(e.getMessage(), 
						StatusBar.ERROR, 
						true, true);
		    }
		}
	}
    } catch (CDFException vtexc) {
	    // should never happend since var_type should always be defined
        vtexc.printStackTrace();
    }
    }

    public void save(Variable var) {
	    saveValues(var);
	    saveMinMax(var);
    }


    public void attributeChanged(AttributeChangeEvent e) {
	Variable var = e.getVariable();
	int type = e.getID();
	
	save(var);

	if (type == AttributeChangeEvent.VAR_TYPE_CHANGE) {
	        setValues(var);
	        setMinMax(var);
    
            
 	try {
//	    String vartype = ((String)var.getEntryData("VAR_TYPE")).
//		toLowerCase();
	    String vartype = VarType.get(var);

	    if (vartype.equals("ignore_data")) {
                
                values.setTablesEnabled(false); 
                min.setTablesEnabled(false);
                max.setTablesEnabled(false);                   
            } 
            else {
                
                values.setTablesEnabled(true); 
                min.setTablesEnabled(true);
                max.setTablesEnabled(true);                   
            }              
        }

        catch (CDFException ex) {}
                               
	}
    }

    public void performVariableAction(VariableEvent e) {
	Variable var = e.getVariable();
	int type = e.getID();
	
	switch (type) {
	    case VariableEvent.CREATED:
	        buildTables(var);
	        break;
	    case VariableEvent.DELETED:
	        reset();
	        break;
	    case VariableEvent.NAME_CHANGE:
	    case VariableEvent.DATATYPE_CHANGE:
	    case VariableEvent.NDIM_CHANGE:
	    case VariableEvent.NELEMENTS_CHANGE:
	    case VariableEvent.DIMSIZE_CHANGE:
	    case VariableEvent.RECVARY_CHANGE:
	    case VariableEvent.DIMVARY_CHANGE:
	    default:
	        break;
	}
    }
    
}

