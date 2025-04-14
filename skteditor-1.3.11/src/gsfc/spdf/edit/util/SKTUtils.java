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
 * $Id: SKTUtils.java,v 1.40 2022/08/02 11:58:20 btharris Exp $
 */

package gsfc.spdf.edit.util;

import gsfc.nssdc.cdf.*;
import gsfc.nssdc.cdf.util.CDFUtils;
import gsfc.spdf.istp.FillvalAttribute;
import gsfc.spdf.istp.ISTPCompliance;
import gsfc.spdf.istp.ISTPComplianceException;
import gsfc.spdf.edit.guis.SKTEditor;
import gsfc.spdf.gui.StatusBar;

import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.lang.reflect.Array;

import javax.swing.JOptionPane;


/**
 * SKTUtils.java
 *
 *
 * Created: Wed Dec 30 08:30:25 1998
 *
 * @author Phil Williams
 * @version $Revision: 1.40 $
 */
public class SKTUtils implements CDFConstants {  
    
    public static Variable createMetadata(CDF    cdf,
					  String name,
					  String fieldname,
					  String catdesc,
					  long   numElements,
					  long   size) 
	throws CDFException
    {
	Variable v = Variable.create(cdf, name, CDF_CHAR,
				     numElements, 
				     1, new long [] {size},
				     NOVARY, new long [] {VARY});
	v.putEntry("VAR_TYPE", CDF_CHAR, "metadata");
	v.putEntry("DICT_KEY", CDF_CHAR, "label");
	v.putEntry("FIELDNAM", CDF_CHAR, fieldname);
	v.putEntry("CATDESC",  CDF_CHAR, catdesc);

	return v;

    }

    public static Variable createSupportData(CDF      cdf,
					     String   name,
					     String   fieldname,
					     String   catdesc,
					     String   dictkey,
					     Object   label1,
					     Variable label2,
					     String   units,
					     Variable unitPTR,
					     String   format,
					     Variable formPTR,
					     long     dataType,
					     long     numDims,
					     long     numElements,
					     long     recVary,
					     long []  dimVary,
					     long []  dimSizes) 
	throws CDFException, ISTPComplianceException 
    {
	if ((dataType == CDF_CHAR) ||
	    (dataType == CDF_UCHAR)) {
	    throw new ISTPComplianceException("Character SupportData not "+
					      "supported.");
	}

	if (numDims > 2) {
	    throw new ISTPComplianceException("SupportData must be < 3D.");
	}

	if ((numDims == 1) && ((unitPTR == null) && (units == null))) {
	    throw new ISTPComplianceException("Must specify a UNITS or "+
					      "UNIT_PTR.");
	}

	if ((numDims > 1) && (units == null)) {
	    throw new ISTPComplianceException("Must specify a UNITS.");
	}

	if ((format == null) && (numDims > 1)) {
	    throw new ISTPComplianceException("Must specify FORMAT.");
	}

	if ((numDims == 1) && ((formPTR == null) && (format == null))) {
	    throw new ISTPComplianceException("Must specify a FORMAT"+
					      " or FORM_PTR.");
	}
	
	Variable v = Variable.create(cdf, name, dataType,
				     numElements, numDims, dimSizes,
				     recVary, dimVary);

	v.putEntry("VAR_TYPE", CDF_CHAR, "support_data");
	v.putEntry("FIELDNAM", CDF_CHAR, fieldname);
	v.putEntry("CATDESC",  CDF_CHAR, catdesc);
	v.putEntry("DICT_KEY", CDF_CHAR, dictkey);
	if (units != null)   v.putEntry("UNITS",    CDF_CHAR, units);
	if (unitPTR != null) v.putEntry("UNIT_PTR", CDF_CHAR, unitPTR.getName());
	if (format != null)  v.putEntry("FORMAT",   CDF_CHAR, format);
	if (formPTR != null) v.putEntry("FORM_PTR", CDF_CHAR, formPTR.getName());
	setLabels(v, new Object [] {label1, label2});
	setFillval(v);

	return v;
    }
    
    public static Variable createData(CDF      cdf,
				      String   name,
				      String   displayType,
				      String   fieldname,
				      String   catdesc,
				      String   dictkey,
				      String   depend0,
				      String   depend1,
				      String   depend2,
				      String   depend3,
				      Object   label1,
				      Variable label2,
				      Variable label3,
				      String   units,
				      Variable unitPTR,
				      String   format,
				      Variable formPTR,
				      Object   validmin,
				      Object   validmax,
				      long     dataType,
				      long     numDims,
				      long     numElements,
				      long     recVary,
				      long []  dimVary,
				      long []  dimSizes)
	throws CDFException, ISTPComplianceException 
    {
	if ((dataType == CDF_CHAR) || (dataType == CDF_UCHAR))
	    throw new ISTPComplianceException("Data can not be "+
					      "CDF_CHAR or CDF_UCHAR.");

	// DISP_HANDLING
	if ((displayType.indexOf("time_series") >= 0) && (numDims > 1))
	    throw new ISTPComplianceException("time_series data must be"+
					      " 1D or smaller.");

	if ((displayType.indexOf("image") >= 0) && (numDims < 2))
	    throw new ISTPComplianceException("image data must be "+
					      "2D or larger.");

	if ((displayType.indexOf("spectrogram") >= 0) &&
	    ((numDims < 1) || (numDims > 2)))
	    throw new ISTPComplianceException("spectrogram data must "+
					      "be 1D or 2D.");

	Variable v = Variable.create(cdf, name, dataType,
				     numElements, numDims, dimSizes,
				     recVary, dimVary);

	v.putEntry("VAR_TYPE",     CDF_CHAR, "data");
	v.putEntry("DISPLAY_TYPE", CDF_CHAR, displayType);
	v.putEntry("FIELDNAM",     CDF_CHAR, fieldname);
	v.putEntry("CATDESC",      CDF_CHAR, catdesc);
	v.putEntry("DICT_KEY",     CDF_CHAR, dictkey);
	if (units != null)   v.putEntry("UNITS",    CDF_CHAR, units);
	if (unitPTR != null) v.putEntry("UNIT_PTR", CDF_CHAR, unitPTR.getName());
	if (format != null)  v.putEntry("FORMAT",   CDF_CHAR, format);
	if (formPTR != null) v.putEntry("FORM_PTR", CDF_CHAR, formPTR.getName());
	setLabels(v, new Object [] {label1, label2, label3});
	switch ((int)numDims) {
	case 3:
	    v.putEntry("DEPEND_3", CDF_CHAR, depend3);
	case 2:
	    v.putEntry("DEPEND_2", CDF_CHAR, depend2);
	case 1:
	    v.putEntry("DEPEND_1", CDF_CHAR, depend1);
	case 0:
	    v.putEntry("DEPEND_0", CDF_CHAR, depend0);
	}
	setFillval(v);

	return v;
    }
    
    
    /**
     * Sets the FILLVAL attribute for the variable v
     * to the default value for the data type
     *
     * @param v CDF variable.
     */
    public static void setFillval(
        Variable v) {

	try {
	    // If the attribute doesn't exist, then create it
            
  
	    if (v.getMyCDF().getAttributeID("FILLVAL") == -1)
		Attribute.create(v.getMyCDF(),
				 "FILLVAL",
				 VARIABLE_SCOPE);
	    
            Object defaultFillval = 
                FillvalAttribute.getStandardValue(v.getDataType());
                                       // default FILLVAL value for this 
                                       //  type of variable
            if (defaultFillval != null) {

                v.putEntry("FILLVAL", v.getDataType(), defaultFillval);
            }
	} catch (CDFException e) {
	    SKTEditor.setStatus("Setting FILLVAL failed.", StatusBar.ERROR,
				true, true);
	}
    }
    
    /**
     * Set the label attributes for the given variable
     *
     * @param v CDF variable.
     * @param labels label values.
     * @throws ISTPComplianceException if the result is not ISTP compliant.
     * @throws CDFException of a CDF exception occurs.
     */
    public static void setLabels(Variable v, Object [] labels)
	throws ISTPComplianceException, CDFException
    {
	String  varType = (String)v.getEntryData("VAR_TYPE");
	String  display = (String)v.getEntryData("DISPLAY_TYPE");
	long    numDims = v.getNumDims();
	boolean putAxis;

	// DISP_HANDLING
	putAxis = ((numDims == 0) ||
		   display.equals("image") ||
		   (display.equals("time_series") && (numDims == 0)) ||
		   (display.equals("spectrogram") && (numDims == 1)) ||
		   (display.equals("support_data") && 
		    (((numDims == 1) && (labels[0] instanceof String)) ||
		     ((numDims > 1) && (labels.length == 1))))
		   );

	if (putAxis)
	    v.putEntry("LABLAXIS", CDF_CHAR, labels[0]);
	else {
	    // Check to make sure that the objects are the 
	    // correct type and size
	    switch ((int)numDims) {
	    case 3:
		v.putEntry("LABL_PTR_3", CDF_CHAR, 
			   ((Variable)labels[2]).getName());
	    case 2:
		v.putEntry("LABL_PTR_2", CDF_CHAR, 
			   ((Variable)labels[1]).getName());
	    case 1:
		v.putEntry("LABL_PTR_1", CDF_CHAR, 
			   ((Variable)labels[0]).getName());
		break;
	    default:
	    }
	}
    }


    /**
      * A string for use in type comparisions.
      */
    private static final String A_STRING = "";

    /**
     * Provides the maximum number of CDF elements contained in the given 
     * data object.  As of CDF 3.8.1, CDF_CHAR type values are UTF-8 
     * encoded so the number of element (bytes) may be larger than the
     * number of code points.
     *
     * @param data object (possibly an array) whose maximum number of 
     *             elements is to be determined
     * @return maximum number of elements contained in data
     */
    public static long getMaxNumElements(Object data) {

        if (data == null) {

            return 1;
        };

        Class componentType = data.getClass().getComponentType();  
                                            // data's component type
        long maxNumElements = 1;            // result that we will return

        try {

            if (componentType != null && 
                componentType.isInstance(A_STRING)) {

                String[] stringArrayData = (String[])data;
                                            // array representation of data

                for (int i = 0; i < stringArrayData.length; i++) {

                    if (stringArrayData[i].length() > maxNumElements) {

                        maxNumElements = 
                            stringArrayData[i].getBytes("UTF-8").length;
                    }
                }
            }
            else if (data instanceof java.lang.String) {

                return ((String)data).getBytes("UTF-8").length;
            }
        }
        catch (UnsupportedEncodingException e) {

            // utf-8 should always be supported
            System.err.println("SKTUtils.getMaxNumElements: " +
                "UnsupportedEncodingException: " + e.getMessage());
        }

        return maxNumElements;
    }

    /**
     * Check each element of a data object (could be an array or a scalar
     * to ensure that it is not null. 
     *
     * @param data object to verify.
     * @param dataType CDF datatype of object.
     */
    public static void verifyArray(
        Object data,
        long dataType) {

     if(data.getClass().isArray()) {

	    for (int i=0;i<Array.getLength(data);i++)
	    {
	        if (Array.get(data,i) == null)
	        {
	            switch ((int)dataType)
	            {
	                case (int)CDF_CHAR:
	                case (int)CDF_UCHAR:               
				        Array.set(data,i, " "); break;
		        
	                case (int)CDF_BYTE: 
	                case (int)CDF_INT1: 
				        Array.set(data,i,new Byte((byte)0)); break;
		       
	                case (int)CDF_INT2:
	                case (int)CDF_UINT1: 
				        Array.set(data,i,new Short((short)0)); break;
	            
	                case (int)CDF_INT4:
	                case (int)CDF_UINT2:  
				        Array.set(data,i, new Integer(0)); break;
	            
	                case (int)CDF_INT8:  
	                case (int)CDF_UINT4:  
				        Array.set(data,i, new Long(0L)); break;
	   
	                case (int)CDF_REAL4:
	                case (int)CDF_FLOAT: 
				        Array.set(data,i, new Float((float)0.0)); break;
	    
	                case (int)CDF_TIME_TT2000:
                            Array.set(data,i, new Long(0L));
                            break;
	                case (int)CDF_REAL8:
	                case (int)CDF_DOUBLE:
	                case (int)CDF_EPOCH:
				        Array.set(data,i, new Double((double)0.0)); break;
                        case (int)CDF_EPOCH16:                            
                            double[] ep16PadData = new double[2];             
                            try{
                                double ep_status  = gsfc.nssdc.cdf.util.Epoch16.compute(0000, 1, 01, 00, 00,
                                              00, 000, 000, 000, 000, ep16PadData); 
                            }catch(CDFException e) {}            
			    Array.set(data,i, ep16PadData);                             
                            break;		    
                        default:
	                    break;
			    }
			}
	    }
	    return;
	}
 }

 
    public static Object stripBlanks( Object data) {

        if (data == null) {

            return data;
        };
        

        Class componentType = data.getClass().getComponentType();  
                                            // data's component type
                                            // result that we will return

         if (componentType != null && 
            componentType.isInstance(A_STRING))
         {                
            for (int i=0;i<Array.getLength(data);i++)
            {
                String s = Array.get(data,i).toString().trim();
       
                Array.set(data,i,s.replaceAll("\\b\\s{2,}\\b", " "));
            }
            return data;
            
 
        }
        else if (data instanceof java.lang.String) {            

             return ((String)data).replaceAll("\\b\\s{2,}\\b", " ").trim();
        };
        return data;
    }

  
  public  static Object replaceData(long dataType) {
    
    switch ((int)dataType)
    {
        case (int)CDF_CHAR:
	    case (int)CDF_UCHAR:               
		    return " ";
		    
	    case (int)CDF_BYTE: 
	    case (int)CDF_INT1: 
	        return new Byte((byte)0);
		       
	    case (int)CDF_INT2:
	    case (int)CDF_UINT1: 
		    return new Short((short)0);		       
	            
	    case (int)CDF_INT4:
	    case (int)CDF_UINT2:  
		    return	new Integer(0); 	       
	            
	    case (int)CDF_INT8:  
	    case (int)CDF_UINT4:  
		    return	new Long(0L); 		      
	   
	    case (int)CDF_REAL4:
	    case (int)CDF_FLOAT: 
		    return 	 new Float((float)0.0); 	        
	    
	    case (int)CDF_TIME_TT2000:
		    return new Long(0L);
	    case (int)CDF_REAL8:
	    case (int)CDF_DOUBLE:
	    case (int)CDF_EPOCH:
		    return 	new Double((double)0.0);	       
	    case (int)CDF_EPOCH16:
                double[] ep16PadData = new double[2];             
                try{
                    double ep_status  = gsfc.nssdc.cdf.util.Epoch16.compute(0000, 1, 01, 00, 00,
                                              00, 000, 000, 000, 000, ep16PadData); 
                }catch(CDFException e) {}            
                return ep16PadData;                             
	    		    
	    default:
	        return null;
	    }
    }


    /**
     * Check each element of a string array to ensure that it
     * is not null
     *
     * @param strArray array of Strings to verify.
     */
    public static void verifyStringArray(String [] strArray) {
	for (int i=0;i<strArray.length;i++)
	    if (strArray[i] == null)
		strArray[i] = " ";
    }

    /**
     * Put a entry for the given attribute on the given variable.
     * If the attribute is not present, then first create the attribute.
     *
     * @param var target variable.
     * @param attrName attribute's name.
     * @param datatype attribute's data type.
     * @param data attribute's new value.
     * @throws CDFException of a CDF exception occurs.
     */
    public static void putVattrEntry(
        Variable var,
        String attrName,
        long datatype,
        Object data) 
        throws CDFException {

        try {

            Object existingData = null;
                                       // existing entry value
            try {

                existingData = var.getEntryData(attrName);
            }
            catch (CDFException e) {

                // continuing with existingData == null is fine
            }
            if (existingData != null) {

                Class existingDataClass = existingData.getClass();
                                       // existingData's Class

                if (existingDataClass.isArray() && data != null &&
                    !data.getClass().isArray()) {

                    Array.set(existingData, 0, data);
                    var.putEntry(attrName, datatype, existingData);
                }
                else if (!existingDataClass.isArray() && data != null &&
                         data.getClass().isArray()) {

                    var.putEntry(attrName, datatype, Array.get(data, 0));
                }
                else {

                    var.putEntry(attrName, datatype, data);
                }
            }
            else {

                var.putEntry(attrName, datatype, data);
            }
	} 
        catch (CDFException e) {

	    if (e.getCurrentStatus() == NO_SUCH_ATTR) {

		Attribute.create(var.getMyCDF(), attrName, 
				 VARIABLE_SCOPE);
		var.putEntry(attrName, datatype, data);
	    }
	}
    }
				     

    public static Object getVattrEntryData(Variable var, String attrName) 
	throws CDFException
    {
	try {
	    return var.getEntryData(attrName);
	} catch (CDFException e) {
	    if (e.getCurrentStatus() == NO_SUCH_ATTR) {
		Attribute.create(var.getMyCDF(), attrName, VARIABLE_SCOPE);
		return null;
	    } else return null;
	}
    }

} // SKTUtils
