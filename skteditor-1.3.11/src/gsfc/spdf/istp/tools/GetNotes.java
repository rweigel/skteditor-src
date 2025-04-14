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
 * Copyright (c) 2011-2024 United States Government as represented by 
 * the National Aeronautics and Space Administration. No copyright is 
 * claimed in the United States under Title 17, U.S.Code. All Other 
 * Rights Reserved.
 *
 * $Id: GetNotes.java,v 1.44 2024/11/04 18:03:07 btharris Exp $
 */
package gsfc.spdf.istp.tools;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.List;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;

import gsfc.nssdc.cdf.CDF;
import gsfc.nssdc.cdf.CDFConstants;
import gsfc.nssdc.cdf.CDFException;

import gsfc.spdf.istp.Doi;
import gsfc.spdf.istp.Filename;
import gsfc.spdf.util.TextUtils;


/**
 * This class represents an application that extracts certain global and
 * variable attribute information from the specified 
 * <a href="http://cdf.gsfc.nasa.gov/">Common Data Format (CDF)</a> 
 * files and produces an HTML representation of the information.
 */
public class GetNotes {

    /**
     * A map containing the {@link DatasetNotes} for each dataset.  The 
     * key to the map is the dataset's name.
     */
    private TreeMap<String, DatasetNotes> datasetNotes = new TreeMap<>();

    /**
     * Flag indicating that the output should be XHTML instead of HTML.
     */
    private boolean outputXhtml = false;

    /**
     * Flag indicating that the output should be standalone HTML 
     * instead of fragmented HTML that depends upon include files.
     */
    private boolean outputHtml = false;

    /**
     * CDAS metadata.
     */
    private CdasMetadata cdasMetadata = new CdasMetadata();

    /**
     * Constructs a GetNotes object.
     *
     * @throws JAXBException if a JAXBException occurs.
     */
    public GetNotes() 
        throws JAXBException {}


    /**
     * Gets the value of the outputXhtml attribute.
     *
     * @return value of outputXhtml attribute
     * @see #setOutputXhtml
     */
    public boolean getOutputXhtml() {

        return outputXhtml;
    }


    /**
     * Sets the value of the outputXhtml attribute.
     *
     * @param value new outputXhtml value
     * @see #getOutputXhtml
     */
    public void setOutputXhtml(boolean value) {

        outputXhtml = value;
    }


    /**
     * Gets the value of the outputHtml attribute.
     *
     * @return value of outputHtml attribute
     * @see #setOutputHtml
     */
    public boolean getOutputHtml() {

        return outputHtml;
    }


    /**
     * Sets the value of the outputHtml attribute.
     *
     * @param value new outputHtml value
     * @see #getOutputHtml
     */
    public void setOutputHtml(boolean value) {

        outputHtml = value;
    }


    /**
     * Reads the notes (attribute values) from the specifed files and
     * stores them in the datasetNotes map.
     *
     * @param files name of CDF files to read notes from
     */
    public void readNotes(String[] files) {

/*
        // tried the following with CDF 3.7.1 for speed but it was
        // not very helpful
        try {

            CDF.setValidate(CDFConstants.VALIDATEFILEoff);
        }
        catch (CDFException e) {

            System.err.println("Error turning CDF validation off.");
            System.err.println(e.getMessage());
            System.err.println("Ignoring error.");
        }
*/

        for (int i = 0; i < files.length; i++) {

            String filename = files[i];// the name of the file that is
                                       // currently being read

            String dataset = Filename.getDataset(filename);
                                       // dataset's name
            if (dataset == null) {

                System.err.println(
                    "Unrecognized filename pattern in file named '" +
                    filename + "'.");
                System.err.println("Skipping file.");
            }
            else {

                CDF cdf = null;        // CDF file

                try {

                    cdf = CDF.open(filename, CDFConstants.READONLYon);
                    // tried the following for speed but not very helpful
                    // cdf = CDF.open(filename, CDFConstants.READONLYoff);

                    DatasetNotes dNotes = null;
                                       // this dataset's notes

                    dNotes = (DatasetNotes)datasetNotes.get(dataset);

                    if (dNotes == null) {

                        dNotes = new DatasetNotes(dataset, cdf);

                        datasetNotes.put(dataset, dNotes);
                    }
                    else {

                        dNotes.mergeValue(cdf);
                    }
                }
                catch (CDFException e) {

                    System.err.println("Error accessing " + filename + ".");
                    System.err.println(e.getMessage());
                    System.err.println("Skipping file.");
                }
                finally {

                    if (cdf != null) {

                        try {

                            cdf.close();
                        }
                        catch (CDFException e) {

                            // at this point, I don't care
                        }
                    }
                }
            } // endif dataset != null
        } // endfor each file
    }


    /**
     * Writes an HTML representation of all the notes in the datasetNotes
     * map to a file with a name derived from the given filename.
     *
     * @param filename begining portion of output filename
     * @throws IOException if an IOException occurs writing the file
     */
    public void writeAllNotes(String filename) 
        throws IOException {

        writeNotes(datasetNotes, null, filename);
    }


    /**
     * Writes an HTML representation of the notes in the given 
     * DatasetNotes map to a file with a name derived from the given 
     * filename.
     *
     * @param dNotes map containing the DatasetNotes to write.
     * @param subsetId subset identifier.  null if not a subset.
     * @param filename begining portion of output filename.
     * @throws IOException if an IOException occurs writing the file
     */
    public void writeNotes(
        SortedMap dNotes, String subsetId, String filename) 
        throws IOException {

        PrintWriter out = null;        // PrintWriter used to output 
                                       // notes

        if (filename.equals("-")) {

            out = new PrintWriter(System.out);
        }
        else {

            String extension = outputXhtml ? ".xhtml" : ".html";   
                                       // filename's extension

            out = new PrintWriter(filename + extension, "UTF-8");
        }

        String title = "CDAWeb Served Heliophysics Datasets";
                                       // title of page
        if (subsetId != null) {

            title = title + subsetId;
        }

        PrintHeader(out, title);

//        out.println("<a name=\"top\"></a>");
        out.println("<a id=\"top\"></a>");

        title = title.replaceAll("CDAWeb",
            "<a href=\"/\">CDAWeb</a>");

        PrintToc(dNotes, title, out);

        PrintNotes(dNotes, out);

        PrintFooter(out);

        out.close();
    }


    /**
     * Writes separate HTML pages for each subset of the notes in 
     * datasetNotes.  Each subsets is defined by the set of datasets
     * with names begining with each letter of the alphabet.  That is,
     * each dataset with a name beginning with 'A' belong to one subset
     * and therefore appear on one HTML page.  Each dataset with a name
     * beginning with 'B' belong to another subset and so on.  The HTML
     * page filename is constructed by concatenating the subset letter
     * to the end of the given filename value.
     *
     * @param filename begining portion of output filename
     * @throws IOException if an IOException occurs writing the file
     */
    public void writeAllSubNotes(String filename)
        throws IOException {

        for (char subset = 'A'; subset <= 'Z'; subset++) {

            SortedMap subDatasetNotes = 
                datasetNotes.subMap(subset + "", 
                                    (char)(subset + 1) + "");
                                       // subset of datasetNotes whose 
                                       // name begins with the subset 
                                       // character

            if (subDatasetNotes.size() > 0) {

                writeNotes(subDatasetNotes, 
                    " Beginning with '" + subset + "'", 
                    filename + subset);
            }
        } // endfor each subset
    }


    /**
     * Prints the header portion of an HTML page.
     *
     * @param out PrintWriter to use for printing
     * @param title title of HTML page
     */
    private void PrintHeader(PrintWriter out, String title) {

        if (outputXhtml || outputHtml) {

            String metaEnd = null;     // characters to end a <meta> 
                                       // element
            if (outputXhtml) {

                metaEnd = "/>";

                out.println(
                    "<?xml version=\"1.0\" encoding=\"utf-8\"?>");
                out.println("<!DOCTYPE html");
                out.println(
                    "PUBLIC \"-//W3C/DTD XHTML 1.0 Transitional//EN\"");
                out.println("\"http://www.w3.org/TR/xhtml1/DTD/" +
                        "xhtml1-transitional.dtd\">");
                out.println(
                    "<html xmlns=\"http://www.w3.org/1999/xhtml\" " +
                        "xml:lang=\"en\" lang=\"en\">");
            }
            else {

                metaEnd = ">";

                out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD " +
                    "HTML 4.01 Transitional//EN\" " +
                    "\"http://www.w3c.org/TR/1999/" +
                    "REC-html401-19991224/loose.dtd\">");
                out.println("<html lang=\"en\">");
            }

            out.println("<head>");
            out.println("  <title>" + title + "</title>");
            out.println("  <meta http-equiv=\"Content-Type\" " +
                "content=\"text/html\" " + metaEnd);
            out.println("  <meta name=\"orgcode\" content=\"672\" " + 
                metaEnd);
            out.println("  <meta name=\"description\" " +
                "content=\"Information about CDAWeb Heliophysics datasets.\" " +
                metaEnd);
            out.println(
                "  <meta name=\"keywords\" content=\"science, data, " +
                "CDAWeb, coordinated data analysis, multi-mission, " +
                "multi-instrument, space physics\" " + metaEnd);
            out.println(
                "  <meta name=\"rno\" content=\"Robert.M.Candey.1\" " +
                metaEnd);
            out.println("  <meta name=\"content-owner\" " +
                "content=\"Robert.M.Candey.1\" " + metaEnd);
            out.println("  <meta name=\"webmaster\" " +
                "content=\"Tamara.J.Kovalick.1\" " + metaEnd);
            out.println("  <link rel=\"icon\" href=\"nasaicon.png\" " +
                "type=\"image/png\" " + metaEnd);
            out.println(
                "  <link rel=\"SHORTCUT ICON\" href=\"favicon.ico\" " + 
                metaEnd);
            out.println("</head>");
            out.println("<body>");
        }
        else {

            out.println(
                "<!--#include virtual=\"../cdawheader_cgi.html\"-->");
        }
        out.println("<script>");
        out.println("    function clipCitation(doi) {");
        out.println("        document.body.style.cursor = 'wait';");
        out.println("        let url = \"https://citation.crosscite.org/format?doi=\" + doi + \"&style=bibtex&lang=en-US\";");
        out.println("        const text = new ClipboardItem({"); 
        out.println("                \"text/plain\": fetch(url)");
        out.println("                .then((response) => {document.body.style.cursor = 'default'; return response.text()})");
        out.println("                .then((text) => new Blob([text], { type: \"text/plain\" }))");
        out.println("        });");
        out.println("        navigator.clipboard.write([text]);");
        out.println("        text.getType(\"text/plain\")");
        out.println("            .then((value) => value.text())");
        out.println("            .then((value) => alert(value + \"\\nCopied to clipboard\"));");
        out.println("    }");
	out.println("    const monthName=new Array(\"January\",\"February\",\"March\",\"April\",\"May\",\"June\",\"July\",\"August\",\"September\",\"October\",\"November\",\"December\");");
	out.println("    let currentDate = new Date();");
	out.println("    let currentDateStr = currentDate.getFullYear() + \"-\" + monthName[currentDate.getMonth()] + \"-\" + currentDate.getDate();");
        out.println("</script>");
    }


    /**
     * Prints the table-of-contents portion of a dataset notes HTML 
     * page.
     *
     * @param datasets map containing the datasets that are to be 
     *            included in the table of contents
     * @param title title of HTML page
     * @param out PrintWriter to use for printing
     */
    private void PrintToc(SortedMap datasets, String title, 
                          PrintWriter out) {

        out.println("<h2>" + title + "</h2>");
        out.println("<dl>");

        for (Iterator i = datasets.entrySet().iterator(); i.hasNext(); ) {

            Map.Entry datasetEntry = (Map.Entry)i.next();
                                       // dataset entry
            String dataset = (String)datasetEntry.getKey();
                                       // a dataset name
            DatasetNotes dNotes = (DatasetNotes)datasetEntry.getValue();
                                       // notes about dataset
            String description = 
                TextUtils.htmlify(dNotes.getDescription());
                                       // XHTML version of description

            out.println("<dd><a href=\"#" + dataset + "\">" + dataset +
                        "</a>: " + description + "</dd>");
        }

        out.println("</dl>");
    }


    /**
     * Returns a URL to an HTML rendering of the specified SPASE resource.
     *
     * @param resourceId SPASE resource identifier.
     * @return URL to an HTML rendering of the given SPASE resource.
     */
    private static String getSpaseHtmlUrl(
        String resourceId) {

        StringBuilder url = new StringBuilder();
                                       // result URL
        if (resourceId != null && resourceId.length() > 7) {

            url.append(
                "https://hpde.io/")
               .append(resourceId.substring(8));
            /* old url
            url.append(
                "http://www.spase-group.org/registry/render?f=yes&id=")
               .append(resourceId);
            */
        }

        return url.toString();
    }


    /**
     * Return an HTML A element for the DOI landing page of the given 
     * DOI value.
     *
     * @param doi DOI value.
     * @return an HTML A element for the given DOI value.
     */
    private static String getDoiHtmlA(
        String doi) {

        return "<a href=\"" + Doi.getLandingPageUrl(doi) + 
                    "\" target=\"_blank\">doi:" + doi + "</a>";
    }


    /**
     * Return an HTML A element for the CiteAs page of the given 
     * DOI value.
     *
     * @param doi DOI value.
     * @return an HTML A element for the given DOI value.
     */
    private static String getCiteAsHtmlA(
        String doi) {

        String citeAsUrl = "https://citeas.org/cite/" + doi;

        return "<a href=\"" + citeAsUrl + "\" target=\"_blank\">CiteAs</a>";
    }


    /**
     * Return an HTML A element for the BibTeX citation for the given
     * DOI value.
     *
     * @param doi DOI value.
     * @return an HTML A element for the given DOI value.
     */
    private static String getBibTeXHtmlA(
        String doi) {

        String bibtexUrl = "https://citation.crosscite.org/format?doi=/" + 
            doi + "&style=bibtex&lang=en-US";

        return "<a href=\"" + bibtexUrl + "\" target=\"_blank\">Citation in BibTeX format</a>";
    }


    /**
     * Citation date formatter.
     */
    static final DateTimeFormatter CITATION_DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-LLLL-dd");


    /**
     * Prints the notes portion of a dataset notes HTML page.
     *
     * @param datasets map containing the datasets that are to be 
     *            included in the page
     * @param out PrintWriter to use for printing
     */
    private void PrintNotes(SortedMap datasets, PrintWriter out) {

//        LocalDate date = LocalDate.now();
//        String dateStr = date.format(CITATION_DATE_FORMATTER);

        for (Iterator i = datasets.entrySet().iterator(); i.hasNext(); ) {

            Map.Entry datasetEntry = (Map.Entry)i.next();
                                       // dataset entry
            String dataset = (String)datasetEntry.getKey();
                                       // a dataset name
            DatasetNotes dNotes = (DatasetNotes)datasetEntry.getValue();
                                       // notes about dataset
            String spaseResourceId = dNotes.getSpaseDatasetResourceID();
                                       // dataset's SPASE resourceID
            List<String> dois = dNotes.getDois();
                                       // dataset's digital object 
                                       // identifier
            out.println("<hr />");
            out.println("<dl>");
            out.println("<dt><a id=\"" + dataset + "\"></a>");
            out.print("<b>" + dataset + "</b>");

            if (dois.size() > 0) {

                ListIterator<String> iter = dois.listIterator();

                String doi = iter.next();
                out.print(" " + getDoiHtmlA(doi));
//                          " " + getBibTeXHtmlA(doi));
//                          " (" + getCiteAsHtmlA(doi) + ")");
                out.print(" <button title=\"Copy BibTeX citation to clipboard\" onclick=\"clipCitation('" + doi + "')\">Copy BibTeX Citation</button>");
                while (iter.hasNext()) {

                    doi = iter.next();
                    out.print(", " + getDoiHtmlA(doi));
//                          " " + getBibTeXHtmlA(doi));
//                          " (" + getCiteAsHtmlA(doi) + ")");
                    out.print(" <button title=\"Copy BibTeX citation to clipboard\" onclick=\"clipCitation('" + doi + "')\">Copy BibTeX Citation</button>");
                }
//                out.print("<br>Proper citations should include the \"Accessed on date\" in the form " + dateStr + ".");
                out.print("<br>Proper citations should include the \"Accessed on date\" in the form ");
                out.print("<script>document.write(currentDateStr)</script>.");
            }
            else if (cdasMetadata.getDoi(dataset) != null) {

                String doi = cdasMetadata.getDoi(dataset);

                out.print(" " + getDoiHtmlA(doi));
                out.print(" <button title=\"Copy BibTeX citation to clipboard\" onclick=\"clipCitation('" + doi + "')\">Copy BibTeX Citation</button>");
            }
            else if (spaseResourceId != null && 
                     !spaseResourceId.trim().equals("")) {

                out.print(" (<a href=\"" + 
                    getSpaseHtmlUrl(spaseResourceId) +
                    "\" target=\"_blank\">" + spaseResourceId + "</a>)");
            }
            out.println("</dt>");

            PrintGlobalNotes(dNotes, out);

            out.println("</dl>");
            out.println("<pre> </pre>");

            PrintVariableNotes(dNotes, out);

            out.println("<a href=\"/cgi-bin/eval2.cgi?dataset=" + dataset + "&index=sp_phys\">Dataset in CDAWeb</a>");

            // Add link to client code examples
            out.println("<br/>");
            out.println(
                "<a href=\"/WS/cdasr/1/dataviews/sp_phys/datasets/" +
                dataset + "/clientLibraryExample/\" target=\"_blank\">" +
                "Data Access Code Examples</a> written in " +
                "<img align=\"middle\" width=\"75px\" height=\"auto\" " +
                "src=\"https://www.python.org/static/community_logos/python-logo-generic.svg\" " +
                "alt=\"Python\"></img> and " +
                "IDL&reg;.");
/*
                "<a href=\"https://www.nv5geospatialsoftware.com/Products/IDL\" " +
                "target=\"_blank\">IDL&reg;</a>.");
*/
            // End link to client code examples

            out.println("<br/>");
            out.println("<a href=\"#top\">Back to top</a>");
        }
    }


    /**
     * Prints the global attribute notes portion of a dataset notes 
     * HTML page.
     *
     * @param dNotes map containing the datasets that are to be included
     *               in the page
     * @param out PrintWriter to use for printing
     */
    private void PrintGlobalNotes(DatasetNotes dNotes,
                                  PrintWriter out) {

        List<String> value = null;        // global attribute value

        out.println("<dd><b>Description</b><pre>");

        value = dNotes.getText();

        if (value.size() > 0) {

            PrintText(value, out);
            PrintText(dNotes.getTextSupplement(), out);
            PrintText(dNotes.getTextSupplement1(), out);
        }
        else {

            out.println("No TEXT global attribute value.");
        }
        out.println("</pre></dd>");

        value = dNotes.getMods();

        if (value.size() > 0) {

            out.println("<dd><b>Modification History</b><pre>");
            PrintText(value, out);
            out.println("</pre></dd>");
        }

        value = dNotes.getCaveats();

        if (value.size() > 0) {

            out.println("<dd><b>Caveats</b><pre>");
            PrintText(value, out);
            out.println("</pre></dd>");
        }
    }


    /**
     * Prints the given array of text as fixed length lines in a 
     * representation that is suitable for a preformated portion of
     * an HTML document.  The text is broken on word boundaries and
     * transformed for use in an HTML document.  The transformations
     * performed include changing characters such as {@code '<'} to 
     * {@code "&lt;"} and changing URL strings to {@code <a href>} 
     * elements.
     *
     * @param text the text that is to be printed
     * @param out PrintWriter to use for printing
     */
    private void PrintText(List<String> text, PrintWriter out) {

        for (int i = 0; i < text.size(); i++) {

            PrintText( (String)text.get(i), out);
        }
    }


    /**
     * Prints the given string of text as fixed length lines in a 
     * representation that is suitable for a preformated portion of
     * an HTML document.  The text is broken on word boundaries and
     * transformed for use in an HTML document.  The transformations
     * performed include changing characters such as {@code '<'} to 
     * {@code "&lt;"} and changing URL strings to {@code <a href>}
     * elements.
     *
     * @param text the text that is to be printed
     * @param out PrintWriter to use for printing
     */
    private void PrintText(String text, PrintWriter out) {

        if (text == null) {

            return;
        }

        String[] lines = TextUtils.splitBetweenWords(text, 80);
                                       // 80 character lines

        for (int i = 0; i < lines.length; i++) {

            out.println(TextUtils.hotlinkify(
                                    TextUtils.htmlify(lines[i]), "_TOP"));
        }
    }


    /**
     * Prints the variable attribute notes portion of a dataset notes 
     * HTML page.
     *
     * @param dNotes map containing the datasets that are to be included
     *               in the page
     * @param out PrintWriter to use for printing
     */
    private void PrintVariableNotes(DatasetNotes dNotes,
                                    PrintWriter out) {

        List<VariableNotes> varNotesList = 
            dNotes.getVariableNotesInIdOrder();
                                       // variable notes 
        Iterator varIter = varNotesList.iterator();
                                       // variables iterator

        if (varIter.hasNext()) {

            out.println("<ul><li style=\"list-style: none\">" +
//                        "<b>Data Variable Descriptions</b><pre></pre><dl>");
                        "<b>Data Variable Descriptions</b><pre></pre><ul>");
        }

        while (varIter.hasNext()) {

            VariableNotes varNotes = (VariableNotes)varIter.next();

            if (varNotes.getVarType().equalsIgnoreCase("data")) {

                PrintVariableNotes(varNotes, out);
            }
        }

        if (varNotesList.size() > 0) {  // there were variables printed

//            out.println("</dl></li></ul>");
            out.println("</ul></li></ul>");
        }
    }


    /**
     * Prints an individual variable's attribute notes portion of a 
     * dataset notes HTML page.
     *
     * @param varNotes the variable notes that are to be printed
     * @param out PrintWriter to use for printing
     */
    private void PrintVariableNotes(VariableNotes varNotes,
                                    PrintWriter out) {

        String name = TextUtils.htmlify(varNotes.getName());
                                       // XHTML version of variable name
        String catDesc = TextUtils.htmlify(varNotes.getCatDesc());
                                       // XHTML version of variable CATDESC
        String purpose = varNotes.getPurpose();
                                       // variable's purpose
        if (purpose != null) {

            purpose = " (" + purpose + ")";
        }
        else {
            purpose = "";
        }
        out.println("<dt><b>" + catDesc + " [" + name + purpose + "]</b></dt><dd><pre>");
//        out.println("<li><b>" + catDesc + " [" + name + purpose + "]:</b>");

        PrintText(varNotes.getNotes(), out);

        out.println("</pre></dt>");
//        out.println("</li>");
    }


    /**
     * Prints the footer portion of an HTML page.
     *
     * @param out PrintWriter to use for printing
     */
    private void PrintFooter(PrintWriter out) {

        if (outputXhtml || outputHtml) {

            out.println("</body>");
            out.println("</html>");
        }
        else {
            out.println(
                "<!--#include virtual=\"../cdawfooter.html\"-->");
        }
    }


    /**
     * The main method used to invoke this application.
     *
     * @param args command line arguments
     * @throws IOException if an IOException occurs
     * @throws JAXBException if a JAXBException occurs
     */
    public static void main(String[] args) 
        throws IOException, JAXBException {

        String outputFilename = "Notes";   
                                       // name of output file
        boolean noSubNotes = false;    // flag indicating that the sub
                                       // notes should not be created
        boolean outputXhtml = false;   // flag indicating that the 
                                       // output should be XHTML
        boolean outputHtml = false;    // output should be (standalone)
                                       // HTML
        int i = 0;                     // argv index
        for (; i < args.length && args[i].startsWith("-"); i++) {

            if (args[i].equals("-o")) {

                if (++i < args.length) {

                    outputFilename = args[i];
                }
                else {

                    exitArgsError("missing output-filename");
                }
            }
            else if (args[i].equals("-n")) {

                noSubNotes = true;
            }
            else if (args[i].equals("-x")) {

                outputXhtml = true;
            }
            else if (args[i].equals("-h")) {

                outputHtml = true;
            }
            else {

                exitArgsError("invalid option: " + args[i]);
            }
        }

        int numCdfs = 0;               // number of CDF files to process
        String[] cdfFiles = null;      // names of CDF files to process

        if (i == 0) {

            numCdfs = args.length;
            cdfFiles = args;
        }
        else { // args[i] was not an option or beyond end

            numCdfs = args.length - i;

            cdfFiles = new String[numCdfs];
            System.arraycopy(args, i, cdfFiles, 0, numCdfs);
        }

        if (numCdfs == 0) {

            exitArgsError("no CDF files given");
        }

        GetNotes getNotes = new GetNotes();

        getNotes.setOutputXhtml(outputXhtml);
        getNotes.setOutputHtml(outputHtml);
        getNotes.readNotes(cdfFiles);

        getNotes.writeAllNotes(outputFilename);

        if (!noSubNotes) {

            getNotes.writeAllSubNotes(outputFilename);
        }
    }


    /**
     * Terminates this application after printing the given message about
     * an "args" error and a message describing the application's
     * command line argument syntax.
     *
     * @param message text of a message describing a problem with the
     *                command-line arguments given to this application
     */
    public static void exitArgsError(String message) {

        System.err.println("Error: " + message);
        System.err.println(
            "Usage: GetNotes [-o output-filename] [-n] [-x] CDF-files");
        System.err.println("Where:");
        System.err.println(
            "    output-filename is the name of the file with the notes");
        System.err.println(
            "    -n specifies not to create the sub-notes files");
        System.err.println(
            "    -x specifies that the output is to be XHTML instead " +
            "of HTML");
        System.err.println(
            "    -h specifies that the output is to be standalone " +
            "HTML instead of HTML that depends upon include files");
        System.err.println(
            "    CDF-files are the names of the CDF files to extract the");
        System.err.println(
            "    notes from.  If multiple files are given for the same");
        System.err.println(
            "    dataset, the non-null, non-blank attribute values from");
        System.err.println(
            "    the first file given will have preference.");
        System.exit(1);
    }
}
