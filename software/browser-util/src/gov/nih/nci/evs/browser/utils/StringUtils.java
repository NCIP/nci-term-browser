package gov.nih.nci.evs.browser.utils;


import java.io.*;
import java.net.URI;

import java.text.*;
import java.util.*;

import org.apache.commons.lang3.StringEscapeUtils;

public class StringUtils {

    public static Vector<String> parseData(String line) {
		if (line == null) return null;
        String tab = "|";
        return parseData(line, tab);
    }

    public static Vector<String> parseData(String line, String tab) {
		if (line == null) return null;
        Vector data_vec = new Vector();
        StringTokenizer st = new StringTokenizer(line, tab);
        while (st.hasMoreTokens()) {
            String value = st.nextToken();
            if (value.compareTo("null") == 0)
                value = "";
            data_vec.add(value);
        }
        return data_vec;
    }

    public static boolean isAlphanumeric(String str) {
        for (int i=0; i<str.length(); i++) {
            char c = str.charAt(i);
            if (!Character.isDigit(c) && !Character.isLetter(c))
                return false;
        }
        return true;
    }

	public static boolean isInteger( String input )
	{
	   if (input == null) return false;
	   try {
		  Integer.parseInt( input );
		  return true;
	   } catch( Exception e) {
		  return false;
	   }
	}


    public static boolean isNull(String value) {
		if (value == null || value.compareToIgnoreCase("null") == 0 || value.compareToIgnoreCase("undefined") == 0) return true;
		return false;
	}

    public static boolean isNullOrBlank(String value) {
		if (value == null || value.compareToIgnoreCase("null") == 0 || value.compareTo("") == 0 || value.compareTo("undefined") == 0) return true;
		return false;
	}

    public static boolean isNullOrEmpty(Vector v) {
		if (v == null || v.size() == 0) return true;
		return false;
	}

    public static String replaceAll(String s, String t1, String t2) {
		if (s == null) return s;
        s = s.replaceAll(t1, t2);
        return s;
    }

	public static String getStringComponent(String bar_delimed_str, int index) {
		if (bar_delimed_str == null) return null;
		Vector u = StringUtils.parseData(bar_delimed_str);
		if (index >= u.size()) return null;
		return (String) u.elementAt(index);
	}

	public static String getStringComponent(String bar_delimed_str, String delim, int index) {
		if (bar_delimed_str == null) return null;
		Vector u = StringUtils.parseData(bar_delimed_str, delim);
		if (index >= u.size()) return null;
		return (String) u.elementAt(index);
	}

	public static void dumpVector(String label, Vector v) {
		System.out.println(label + ":");
		for (int i=0; i<v.size(); i++) {
			String t = (String) v.elementAt(i);
			int j=i+1;
			System.out.println("\t(" + j + ") " + t);
		}
	}

    public static String convertToCommaSeparatedValue(Vector v) {
        if (v == null)
            return null;
        String s = "";
        if (v.size() == 0)
            return s;

        StringBuffer buf = new StringBuffer();
        buf.append((String) v.elementAt(0));
        for (int i = 1; i < v.size(); i++) {
            String next = (String) v.elementAt(i);
            buf.append("; " + next);
        }
        return buf.toString();
    }

    public static String int2String(Integer int_obj) {
        if (int_obj == null) {
            return null;
        }
        String retstr = Integer.toString(int_obj);
        return retstr;
    }

    public static String encodeTerm(String s) {
		if (s == null) return null;
		if (StringUtils.isAlphanumeric(s)) return s;

        StringBuilder buf = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
			if (Character.isLetterOrDigit(c)) {
                buf.append(c);
            } else {
                buf.append("&#").append((int) c).append(";");
            }
        }
        return buf.toString();
    }


// Reference: http://www.walterzorn.de/en/tooltip_old/tooltip_e.htm
// (whilespace after &lt; is intentional)
    public static String encode_term(String s) {
		if (s == null) return null;
		if (StringUtils.isAlphanumeric(s)) return s;
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == 60) {
				buf.append("&lt; ");
			} else if (c == 62) {
				buf.append("&gt;");
			} else if (c == 38) {
				buf.append("&amp;");
			} else if (c == 32) {
				buf.append("&#32;");
			} else {
				buf.append(c);
			}
        }
        String t = buf.toString();
        return t;
    }



    public static String decode_term(String t) {
	    t = t.replaceAll("&amp;", "&");
	    t = t.replaceAll("&lt;", "<");
	    t = t.replaceAll("&gt;", ">");
	    t = t.replaceAll("&quot;", "\"");
	    t = t.replaceAll("&apos;", "'");
	    return t;
    }



    /*
     * To convert a string to the URL-encoded form suitable for transmission as
     * a query string (or, generally speaking, as part of a URL), use the escape
     * function. This function works as follows: digits, Latin letters and the
     * characters + - * / . _ @ remain unchanged; all other characters in the
     * original string are replaced by escape-sequences %XX, where XX is the
     * ASCII code of the original character. Example: escape("It's me!") //
     * result: It%27s%20me%21
     */

    public static String htmlEntityEncode(String s) {
        StringBuilder buf = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0'
                && c <= '9') {
                buf.append(c);
            } else {
                buf.append("&#").append((int) c).append(";");
            }
        }
        return buf.toString();
    }


    public static String escape(String s) {
		if (s == null) return null;
		if (StringUtils.isAlphanumeric(s)) return s;

        StringBuilder buf = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\') {
				buf.append("\\").append("\\");
            } else {
                buf.append(c);
            }
        }
        return buf.toString();
	}

   public static String removeWhiteSpaces(String str) {
	   if (str == null) return null;
	   StringBuffer buf = new StringBuffer();
	   for (int i=0; i<str.length(); i++) {
		   char c = str.charAt(i);
		   if (c != ' ') {
			   buf.append(c);
		   }
	   }
	   return buf.toString();
   }

   public static String getFieldValue(String line, int index) {
		if (line == null) return null;
        Vector u = parseData(line);
        if (u.size() <= index) return null;
        return (String) u.elementAt(index);
   }

   public static String getFieldValue(String line, String delim, int index) {
		if (line == null) return null;
        Vector u = parseData(line, delim);
        if (u.size() <= index) return null;
        return (String) u.elementAt(index);
   }

	public static boolean isNumeric(String str) {
		try {
			double d = Double.parseDouble(str);
		} catch(NumberFormatException nfe) {
			return false;
		}
		return true;
	}

    public static boolean isSmallIrrationalNumber(String t) {
	    if (t == null) return false;
	    String s = t.toLowerCase();
	    if (!s.startsWith("e-")) return false;
	    s = s.substring(1, s.length());
	    return isNumeric(s);
    }

    public static String escapeHtml(String str) {
		return StringEscapeUtils.escapeHtml4(str);
	}

	public static String getToday() {
		return getToday("MM-dd-yyyy");
	}

	public static String getToday(String format) {
		java.util.Date date = Calendar.getInstance().getTime();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}
}