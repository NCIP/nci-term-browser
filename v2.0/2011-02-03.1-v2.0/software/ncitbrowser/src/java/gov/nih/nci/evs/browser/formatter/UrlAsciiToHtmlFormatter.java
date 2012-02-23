package gov.nih.nci.evs.browser.formatter;

import java.io.*;
import java.net.*;

import org.apache.log4j.*;

/**
 * <!-- LICENSE_TEXT_START -->
 * Copyright 2008,2009 NGIT. This software was developed in conjunction
 * with the National Cancer Institute, and so to the extent government
 * employees are co-authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the disclaimer of Article 3,
 *      below. Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions and the following
 *      disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *   2. The end-user documentation included with the redistribution,
 *      if any, must include the following acknowledgment:
 *      "This product includes software developed by NGIT and the National
 *      Cancer Institute."   If no such end-user documentation is to be
 *      included, this acknowledgment shall appear in the software itself,
 *      wherever such third-party acknowledgments normally appear.
 *   3. The names "The National Cancer Institute", "NCI" and "NGIT" must
 *      not be used to endorse or promote products derived from this software.
 *   4. This license does not authorize the incorporation of this software
 *      into any third party proprietary programs. This license does not
 *      authorize the recipient to use any trademarks owned by either NCI
 *      or NGIT
 *   5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED
 *      WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *      OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE
 *      DISCLAIMED. IN NO EVENT SHALL THE NATIONAL CANCER INSTITUTE,
 *      NGIT, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT,
 *      INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 *      BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *      LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *      CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *      LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 *      ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *      POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */

/**
 * @author EVS Team (David Yee)
 * @version 1.0
 */

public class UrlAsciiToHtmlFormatter extends AsciiToHtmlFormatter implements
        FormatterConstant {
    private static Logger _logger = Logger
        .getLogger(UrlAsciiToHtmlFormatter.class);
    private String _value = "";

    protected Boolean convert2(String textfile, String toExt, String delimiter)
            throws Exception {
        int i = textfile.lastIndexOf("/");
        String outfile = textfile.substring(i + 1);
        i = outfile.lastIndexOf(".");
        outfile = outfile.substring(0, i) + "." + toExt;
        outfile = outfile.replaceAll("%20", " ");
        return convert(textfile, delimiter, outfile);
    }

    public Boolean convert(String textfile, String delimiter, String outfile)
            throws Exception {
        TabFormatterStringBuffer out = new TabFormatterStringBuffer(outfile);
        printHeader(out);
        printContent(out, textfile, delimiter);
        printFooter(out);
        _value = out.toString();
        return Boolean.TRUE;
    }

    protected BufferedReader getBufferReader(String fileUrl) throws Exception {
        URL url = new URL(fileUrl);
        _logger.debug("fileUrl: " + fileUrl);
        InputStream is = url.openStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        return br;
    }

    protected String getReportName(String filename) {
        String reportName = filename.replace(".htm", "");
        return reportName;
    }

    public String getValue() {
        return _value;
    }

    public static String generate(String textfile, int[] ncitCodeColumns)
            throws Exception {
        if (textfile == null)
            return "";
        
        String delimiter = "\t";
        UrlAsciiToHtmlFormatter formatter = new UrlAsciiToHtmlFormatter();
        formatter.setNcitUrl("http://ncit.nci.nih.gov/ncitbrowser/");
        formatter.setNcitCodeColumns(ncitCodeColumns);
        formatter
            .setDisplayNCItCodeUrl(DisplayNCItCodeUrl.SeparateSingleWindow);
        formatter.convert(textfile, delimiter);
        return formatter.getValue();
    }

    public static void main(String[] args) {
        try {
            generate(CDISC_SDTM_REPORT_URL, CDISC_SDTM_NCIT_COLUMNS);
            // test(dir + "CDISC_Subset_REPORT__10.06e.txt", new int[] { 1, 3
            // });
            generate(CDRH_REPORT_URL, CDRH_NCIT_COLUMNS);
            generate(FDA_SPL_REPORT_URL, FDA_SPL_NCIT_COLUMNS);
            generate(FDA_UNII_REPORT_URL, FDA_UNII_NCIT_COLUMNS);
            // test(dir +
            // "Individual_Case_Safety_(ICS)_Subset_REPORT__10.06e.txt",
            // new int[] { 1, 3 });
            // test(dir +
            // "Structured_Product_Labeling_(SPL)_REPORT__10.06e.txt",
            // new int[] { 1, 3 });
            _logger.debug("Done");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
