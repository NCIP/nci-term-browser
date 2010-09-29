package gov.nih.nci.evs.browser.formatter;

import gov.nih.nci.evs.browser.utils.*;

import java.io.*;
import java.util.*;

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
