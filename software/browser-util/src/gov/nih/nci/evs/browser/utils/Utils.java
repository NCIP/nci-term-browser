package gov.nih.nci.evs.browser.utils;

import java.io.*;
import java.text.*;
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

public class Utils {
    private static Logger _logger = Logger.getLogger(Utils.class);
    public static final String SEPARATOR =
        "----------------------------------------"
            + "----------------------------------------";
    public static final String SEPARATOR_DASHES =
        "========================================"
            + "========================================";
    private static DecimalFormat _doubleFormatter = new DecimalFormat("0.00");

    public static Logger getJspLogger(String fileName) {
        String name = fileName;
        int i = fileName.lastIndexOf('.');
        if (i > 0)
            name = fileName.substring(0, i) + "_" + fileName.substring(i + 1);
        return Logger.getLogger("gov.nih.nci.evs.browser.jsp." + name);
    }

    public static class StopWatch {
        private long _startMS = 0;

        public StopWatch() {
            start();
        }

        public void start() {
            _startMS = System.currentTimeMillis();
        }

        public long duration() {
            return System.currentTimeMillis() - _startMS;
        }

        public String getResult() {
            long time = duration();
            double timeSec = time / 1000.0;
            double timeMin = timeSec / 60.0;

            return "Run time: " + time + " ms, "
                + _doubleFormatter.format(timeSec) + " sec, "
                + _doubleFormatter.format(timeMin) + " min";
        }
    }

    public static String[] toStrings(String value, String delimiter,
        boolean includeDelimiter, boolean trim) {
        StringTokenizer tokenizer =
            new StringTokenizer(value, delimiter, includeDelimiter);
        ArrayList<String> list = new ArrayList<String>();
        while (tokenizer.hasMoreElements()) {
            String s = tokenizer.nextToken();
            if (trim)
                s = s.trim();
            if (s.length() > 0)
                list.add(s);
        }
        return list.toArray(new String[list.size()]);
    }

    public static String[] toStrings(String value, String delimiter,
        boolean includeDelimiter) {
        return toStrings(value, delimiter, includeDelimiter, true);
    }

    public static int[] toInts(List<Integer> list) {
        int[] values = new int[list.size()];
        int i=0;

        Iterator<Integer> iterator = list.iterator();
        while (iterator.hasNext()) {
            Integer value = iterator.next();
            values[i++] = value;
        }
        return values;
    }

    public static void debug(String msg, String[] list) {
        if (msg != null && msg.length() > 0)
            _logger.debug(msg);
        if (list == null)
            return;
        for (int i = 0; i < list.length; ++i) {
            _logger.debug("  " + (i + 1) + ") " + list[i]);
        }
    }


    public static void dumpHashMap(String label, HashMap hmap) {
		System.out.println("\n" + label + ":");
		Iterator it = hmap.keySet().iterator();
		while (it.hasNext()) {
			String nv = (String) it.next();
			System.out.println("\n");

			Vector v = (Vector) hmap.get(nv);
			for (int i=0; i<v.size(); i++) {
				String q = (String) v.elementAt(i);
				System.out.println(nv + " --> " + q);
			}
		}
		System.out.println("\n");
	}

    public static void dumpVector(String label, Vector v) {
		System.out.println("\n" + label + ":");
		if (v.size() == 0) {
			System.out.println("\tNone");
			return;
		}
        for (int i=0; i<v.size(); i++) {
			String t = (String) v.elementAt(i);
			int j = i+1;
			System.out.println("\t(" + j + ") " + t);
		}
		System.out.println("\n");
	}

    public static void dumpArrayList(String label, ArrayList list) {
		System.out.println("\n" + label + ":");
		if (list.size() == 0) {
			System.out.println("\tNone");
			return;
		}
        for (int i=0; i<list.size(); i++) {
			String t = (String) list.get(i);
			int j = i+1;
			System.out.println("\t(" + j + ") " + t);
		}
		System.out.println("\n");
	}

    public static void dumpList(String label, List list) {
		System.out.println("\n" + label + ":");
		if (list.size() == 0) {
			System.out.println("\tNone");
			return;
		}
        for (int i=0; i<list.size(); i++) {
			String t = (String) list.get(i);
			int j = i+1;
			System.out.println("\t(" + j + ") " + t);
		}
		System.out.println("\n");
	}

    public static void debugHashMap(String preNote, HashMap<String, String> hashMap,
    		String postNote) {
        if (preNote != null && preNote.length() > 0)
            _logger.debug(preNote);
        if (hashMap != null) {
	        Set<String> keys = (Set<String>) hashMap.keySet();
	        List<String> sortedKeys = asSortedList(keys);

	        Iterator<?> iterator = sortedKeys.iterator();
	        int i = 0;
	        while (iterator.hasNext()) {
	        	String key = (String) iterator.next();
	        	String value = (String) hashMap.get(key);
	        	 _logger.debug(i + ": " + key + " = " + value);
	        	++i;
	        }
        }
        if (postNote != null && postNote.length() > 0)
            _logger.debug(postNote);
    }

	public static <T extends Comparable<? super T>> List<T> asSortedList(
			Collection<T> c) {
		List<T> list = new ArrayList<T>(c);
		java.util.Collections.sort(list);
		return list;
	}

    public static String toHtml(String text) {
        text = text.replaceAll("\n", "<br/>");
        text = text.replaceAll("  ", "&nbsp;&nbsp;");
        return text;
    }

    public static String fill(String text, Character fillText, int maxChar) {
        StringBuffer buffer = new StringBuffer(text);
        for (int i = text.length(); i < maxChar; ++i)
            buffer.append(fillText);
        return buffer.toString();
    }

    public static Vector<String> unique(Vector<String> list) {
        Vector<String> newList = new Vector<String>();
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            String text = iterator.next();
            if (! newList.contains(text))
                newList.add(text);
        }
        return newList;
    }

    // -------------------------------------------------------------------------
    private static String debugJSONLine(int indentLevel, int i, String str) {
        if (str.length() <= 0)
            return "";
        for (int j=0; j<indentLevel; ++j)
            str = "  " + str;

        String index = Integer.toString(i);
        for (int j=index.length(); j<3; ++j)
        	index = " " + index;

        String line = index + ": ";
        // line += indentLevel + " ";
        line += str;
        _logger.debug(line);

        return ""; // Initializes the string after printing.
    }

    // -------------------------------------------------------------------------
    public static void debugJSONString(String text) {
        String delimiter = "{},[]";
        StringTokenizer tokenizer = new StringTokenizer(text, delimiter, true);
        int i = 0;
        String str = "";
        int indentLevel = -1;
        String prevToken = "";
        String token = "";
        while (tokenizer.hasMoreTokens()) {
            prevToken = token;
            token = tokenizer.nextToken();
            //_logger.debug("token: " + token);
            str += token;

            if (prevToken.equals("[") && ! token.equals("]")) {
                str = debugJSONLine(indentLevel, i++, str);
                continue;
            }

            if (token.equals("[")) {
                ++indentLevel;
                continue;
            } else if (token.equals("]")) {
                --indentLevel;
            }

            if (! str.endsWith("},") && ! str.endsWith("}],"))
                continue;
            str = debugJSONLine(indentLevel, i++, str);
        }
        str = debugJSONLine(indentLevel, i++, str);
    }

	 public static void saveToFile(String outputfile, String t) {
		 Vector v = new Vector();
		 v.add(t);
		 saveToFile(outputfile, v);
	 }

	 public static void saveToFile(String outputfile, Vector v) {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(outputfile, "UTF-8");
			if (v != null && v.size() > 0) {
				for (int i=0; i<v.size(); i++) {
					String t = (String) v.elementAt(i);
					pw.println(t);
				}
		    }
		} catch (Exception ex) {

		} finally {
			try {
				pw.close();
				System.out.println("Output file " + outputfile + " generated.");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	 }

}