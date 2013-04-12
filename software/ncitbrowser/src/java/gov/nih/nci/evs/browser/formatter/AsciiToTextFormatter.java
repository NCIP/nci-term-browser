/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.formatter;

import gov.nih.nci.evs.browser.utils.*;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;

/**
 * 
 */

/**
 * @author EVS Team (David Yee)
 * @version 1.0
 */

public class AsciiToTextFormatter extends FileFormatterBase implements
        FormatterConstant {
    private static Logger _logger = Logger
        .getLogger(AsciiToTextFormatter.class);

    public Boolean convert(String textfile, String delimiter) throws Exception {
        return convert2(textfile, "edt", delimiter);
    }

    public Boolean convert(String textfile, String delimiter, String outfile)
            throws Exception {
        BufferedReader br = getBufferReader(textfile);
        TabFormatterFileOutputStream out =
            new TabFormatterFileOutputStream(outfile);
        Vector<String> headings = getColumnHeadings(textfile, delimiter);

        int row = 0;
        while (true) {
            String line = br.readLine();
            if (line == null)
                break;
            if (line.length() <= 0)
                continue;

            if (row > 0) {
                Vector<String> v = parseData(line, delimiter);
                out.writeln(Utils.SEPARATOR);
                for (int col = 0; col < v.size(); col++) {
                    out.writeln("[" + row + "," + col + "] "
                        + headings.get(col) + ": " + v.get(col));
                }
                out.writeln();
            }
            row++;
        }
        br.close();
        out.close();
        return Boolean.TRUE;
    }

    // -- Test Program- --------------------------------------------------------
    public static void test(String textfile, int[] ncitCodeColumns) {
        try {
            String delimiter = "\t";
            AsciiToTextFormatter formatter = new AsciiToTextFormatter();
            formatter.convert(textfile, delimiter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        test(CDISC_SDTM_FILE, CDISC_SDTM_NCIT_COLUMNS);
        test(CDISC_SUBSET_FILE, CDISC_SUBSET_NCIT_COLUMNS);
        test(CDRH_SUBSET_FILE, CDRH_NCIT_COLUMNS);
        test(FDA_SPL_FILE, FDA_SPL_NCIT_COLUMNS);
        test(FDA_UNII_FILE, FDA_UNII_NCIT_COLUMNS);
        test(ICS_SUBSET_FILE, ICS_SUBSET_NCIT_COLUMNS);
        test(SPL_FILE, SPL_NCIT_COLUMNS);
        _logger.debug("Done");
    }
}
