/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.formatter;

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

public abstract class FileFormatterBase {


    protected static final Logger _logger = Logger.getLogger(FileFormatterBase.class);

    public abstract Boolean convert(String textfile, String delimiter)
            throws Exception;

    protected Boolean convert2(String textfile, String toExt, String delimiter)
            throws Exception {
        int i = textfile.lastIndexOf(".");
        String outfile = textfile.substring(0, i) + "." + toExt;
        return convert(textfile, delimiter, outfile);
    }

    protected abstract Boolean convert(String infile, String delimiter,
        String outfile) throws Exception;

    protected BufferedReader getBufferReader(String filename) throws Exception {
        File file = new File(filename);
        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);
        BufferedReader br = new BufferedReader(new InputStreamReader(bis, "UTF8"));
        return br;
    }

    protected Vector<String> parseData(String line, String tab) {
        Vector<String> data_vec = new Vector<String>();
        // Note(GF20743): Delimiters are returned as tokens.
        // First value could be a tab.
        StringTokenizer st = new StringTokenizer(line, tab, true);
        boolean lastWasDelim = true;
        while (st.hasMoreTokens()) {
            String value = st.nextToken();
            if (value.equals(tab)) {
                if (lastWasDelim) {
                    data_vec.add("");
                }
                lastWasDelim = true;
            } else {
                data_vec.add(value);
                lastWasDelim = false;
            }
        }
        return data_vec;
    }

    protected Vector<String> getColumnHeadings(String textfile, String delimiter)
            throws Exception {
        BufferedReader br = getBufferReader(textfile);
        String line = br.readLine();
        Vector<String> v = null;
        if (line != null) {
        	v = parseData(line, delimiter);
		}
        br.close();
        return v;
    }

    protected Vector<Integer> getColumnMaxChars(String textfile,
        String delimiter) throws Exception {
        Vector<Integer> maxChars = new Vector<Integer>();
        BufferedReader br = getBufferReader(textfile);

        // Header line
        String line = br.readLine();
        Vector<String> v = null;
        if (line != null) {
			v = parseData(line, delimiter);
			for (int i = 0; i < v.size(); i++)
				//maxChars.add(new Integer(0));
				maxChars.add(Integer.valueOf(0));

			while (true) {
				line = br.readLine();
				if (line == null)
					break;
				if (line.length() <= 0)
					continue;
				v = parseData(line, delimiter);
				for (int k = 0; k < v.size(); k++) {
					String s = v.elementAt(k);
					int numChar = s.length();
					int maxChar = maxChars.elementAt(k);
					if (maxChar < numChar) {
						//maxChars.setElementAt(new Integer(numChar), k);
						maxChars.setElementAt(Integer.valueOf(numChar), k);
					}
				}
			}
	    }

        br.close();
        return maxChars;
    }

    protected Boolean[] findWrappedColumns(String textfile, String delimiter,
        int maxLength) throws Exception {
        Boolean[] a = null;
        BufferedReader br = getBufferReader(textfile);

        int rownum = 0;
        while (true) {
            String line = br.readLine();
            if (line == null)
                break;
            if (line.length() <= 0)
                continue;
            Vector<String> v = parseData(line, delimiter);
            if (rownum == 0) {
                a = new Boolean[v.size()];
                for (int i = 0; i < v.size(); i++) {
                    a[i] = Boolean.FALSE;
                }
            } else {
                for (int i = 0; i < v.size(); i++) {
                    String s = (String) v.elementAt(i);
                    if (s.length() > maxLength && a[i].equals(Boolean.FALSE)) {
                        a[i] = Boolean.TRUE;
                    }
                }
            }
            rownum++;
        }
        br.close();
        return a;
    }

    protected int getHeightInPoints(Vector<String> v, boolean adjustHeight,
        int maxCellWidth) {
        int num_lines = 1;
        if (!adjustHeight)
            return num_lines;

        for (int i = 0; i < v.size(); i++) {
            String s = (String) v.elementAt(i);
            int len = s.length();
            int lines = len / maxCellWidth;
            if (lines > num_lines)
                num_lines = lines;
        }
        return num_lines;
    }

    protected int findColumnIndicator(Vector<String> headings, String label) {
        for (int i = 0; i < headings.size(); i++) {
            String heading = headings.elementAt(i);
            if (heading.contains(label))
                return i;
        }
        return -1;
    }

    protected int getMaxTokenLength(String heading) {
        if (heading == null || heading.length() == 0)
            return 0;
        int max = 0;
        String delimiter = " ";
        Vector<String> v = parseData(heading, delimiter);
        for (int k = 0; k < v.size(); k++) {
            String s = (String) v.elementAt(k);
            int len = s.length();
            if (len > max)
                max = len;
        }
        return max;
    }

    // -------------------------------------------------------------------------
    protected static final String DEFAULT_URL = "http://ncit.nci.nih.gov/ncitbrowser/";
    protected String _ncitUrl = DEFAULT_URL;
    protected Vector<Integer> _ncitCodeColumns = new Vector<Integer>();

    public void clearNcitCodeColumns() {
        _ncitCodeColumns.clear();
    }

    public void addNcitCodeColumn(int column) {
        if (!_ncitCodeColumns.contains(column))
            _ncitCodeColumns.add(column);
    }

    public void setNcitCodeColumns(int[] columns) {
        clearNcitCodeColumns();
        if (columns == null)
            return;
        for (int i = 0; i < columns.length; ++i)
            addNcitCodeColumn(columns[i]);
    }

    public void setNcitUrl(String url) {
        if (url == null || url.trim().length() <= 0) {
            _logger.warn("URL is not set.  Using default NCIt URL: "
                + DEFAULT_URL);
            url = DEFAULT_URL;
        }

        if (url.charAt(url.length() - 1) != '/')
            url += "/";
        _ncitUrl = url;
    }

    protected String getNCItCodeUrl(String code) {
        String ncitCodeUrl =
            _ncitUrl + "ConceptReport.jsp?dictionary=NCI%20Thesaurus"
                + "&code=" + code;
        return ncitCodeUrl;
    }
}
