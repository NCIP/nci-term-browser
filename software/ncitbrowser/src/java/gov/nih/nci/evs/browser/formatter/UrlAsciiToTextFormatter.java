/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.formatter;

import java.io.*;
import java.net.*;

import org.apache.log4j.*;

/**
 * 
 */

/**
 * @author EVS Team (David Yee)
 * @version 1.0
 */

public class UrlAsciiToTextFormatter extends AsciiToTextFormatter implements
        FormatterConstant {
    private static Logger _logger = Logger
        .getLogger(UrlAsciiToTextFormatter.class);

    protected Boolean convert2(String textfile, String toExt, String delimiter)
            throws Exception {
        int i = textfile.lastIndexOf("/");
        String outfile = textfile.substring(i + 1);
        i = outfile.lastIndexOf(".");
        outfile = outfile.substring(0, i) + "." + toExt;
        outfile = outfile.replaceAll("%20", " ");
        return convert(textfile, delimiter, outfile);
    }

    protected BufferedReader getBufferReader(String fileUrl) throws Exception {
        URL url = new URL(fileUrl);
        _logger.debug("fileUrl: " + fileUrl);
        InputStream is = url.openStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        return br;
    }

    public static void generate(String textfile, int[] ncitCodeColumns)
            throws Exception {
        if (textfile == null)
            return;

        String delimiter = "\t";
        UrlAsciiToTextFormatter formatter = new UrlAsciiToTextFormatter();
        formatter.convert(textfile, delimiter);
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
