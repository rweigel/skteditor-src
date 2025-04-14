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
 * Copyright (c) 2024 United States Government as represented by 
 * the National Aeronautics and Space Administration. No copyright is 
 * claimed in the United States under Title 17, U.S.Code. All Other 
 * Rights Reserved.
 *
 * $Id: CdasMetadata.java,v 1.1 2024/10/28 13:11:38 btharris Exp $
 */
package gsfc.spdf.istp.tools;


import java.io.InputStream;
import java.io.IOException;
import java.lang.InterruptedException;
import java.net.URI;
// java 11
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
// end java 11
// java 8
import java.net.HttpURLConnection;
// end java 8
import java.time.Duration;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

// java ee 8
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/* jakarta ee 10
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
*/

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.DatatypeConfigurationException;

import org.w3._1999.xhtml.Html;

import gov.nasa.gsfc.cdaweb.schema.Datasets;
import gov.nasa.gsfc.cdaweb.schema.DatasetDescription;
import gov.nasa.gsfc.cdaweb.schema.ObjectFactory;


/**
 * This class represents 
 * <a href="https://cdaweb.gsfc.nasa.gov/">Coordinated Data Analysis System</a>
 * (CDAS) metadata.  The metadata is obtained from the 
 * <a href="https://cdaweb.gsfc.nasa.gov/WebServices">CDAS web services</a>.
 *
 * @author B. Harris
 * @version $Revision: 1.1 $
 */
public class CdasMetadata {

    /**
     * Client of CDAS RESTful Web services.
     */
/* java 11
    private HttpClient client = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(30))
        .build();
end java 11 */

    /**
     * URL for cdas dataset descriptions.
     */
    private URI cdasDatasetsUri = URI.create("https://cdaweb.gsfc.nasa.gov/WS/cdasr/1/dataviews/sp_phys/datasets");

    /**
     * JAXB context for CDAS XML data.
     */
    private JAXBContext cdasJaxbContext =
        JAXBContext.newInstance("gov.nasa.gsfc.cdaweb.schema");

    /**
     * JAXB context for XHTML data.
     */
    private JAXBContext xhtmlJaxbContext =
        JAXBContext.newInstance("org.w3._1999.xhtml");
 
    /**
     * A CDAS object factory.
     */
    private ObjectFactory cdasFactory = new ObjectFactory();

    /**
     * Map of CDAS dataset identifiers.  The key is the CDAS dataset
     * identifier and the value is the corresponding DOI identifier
     * for the dataset.
     */
    private Map<String, String> idMap = new HashMap<>();


    /**
     * HTTP User-Agent value to use.
     */
    private static final String USER_AGENT = "GetNotes";


    /**
     * Create a CdasMetadata object.
     *
     * @throws JAXBException if one occurs.
     */
    public CdasMetadata()
        throws JAXBException {

        refreshMetadata();
    }


    /**
     * Refreshes the CDAS metadata cache.
     */
    public synchronized void refreshMetadata() {

/* java 11
        HttpRequest request = HttpRequest.newBuilder()
            .uri(cdasDatasetsUri)
            .header("User-Agent", USER_AGENT)
            .header("Accept", "application/xml")
            .build();                  // dataset description request
*/

        try {

// java 11    HttpResponse<InputStream> response =
// java 11        client.send(request, BodyHandlers.ofInputStream());

            HttpURLConnection con = (HttpURLConnection)
                cdasDatasetsUri.toURL().openConnection();
                                       // dataset description http connection
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept", "application/xml");
            con.setConnectTimeout(10000);
            con.setReadTimeout(30000);
            int status = con.getResponseCode();

            Unmarshaller unmarshaller = 
                cdasJaxbContext.createUnmarshaller();
                                       // cdasws unmarshaller

            Datasets datasets = (Datasets)
// java 11        unmarshaller.unmarshal(response.body());
                unmarshaller.unmarshal(con.getInputStream());
                                       // all cdas datasets
            
            List<DatasetDescription> descriptions =
                datasets.getDatasetDescription();
                                       // all cdas dataset descriptions
            idMap.clear();
            for (DatasetDescription description : descriptions) {

                idMap.put(description.getId(), description.getDoi());
            }
        }
// java 11 catch (IOException | InterruptedException | JAXBException e) {
        catch (IOException | JAXBException e) {

            System.err.println("Getting CDAS metadata failed: " +
                e.getMessage());
        }
    } 


    /**
     * Get the DOI associated with the given CDAS dataset identifier.
     *
     * @param cdasId CDAS dataset identifier.
     * @return DOI identifier associated with the given CDAS dataset 
     *     identifier.  null if there is none.
     */
    public synchronized String getDoi(
        String cdasId) {

        return idMap.get(cdasId);
    }


    /**
     * Class tester.
     *
     * @param argv command-line arguments
     * @throws Exception if an Exception occurs.
     */
    public static void main(
        String [] argv) 
        throws Exception {

        CdasMetadata metadata = new CdasMetadata();

        System.out.println(metadata.getDoi("AC_H2_MFI"));
    }
}
