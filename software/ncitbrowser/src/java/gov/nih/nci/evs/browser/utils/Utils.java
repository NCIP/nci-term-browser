/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.utils;

import java.text.*;
import java.util.*;

import org.apache.log4j.*;

/**
 * 
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
}