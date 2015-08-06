package gov.nih.nci.evs.browser.utils;

import gov.nih.nci.evs.browser.bean.*;
import gov.nih.nci.evs.browser.common.*;
import gov.nih.nci.evs.browser.properties.NCItBrowserProperties;

import java.util.*;

import javax.servlet.http.*;

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

public class JSPUtils {
    private static Logger _logger = Logger.getLogger(JSPUtils.class);
    private static final String DEFAULT_DICTIONARY = Constants.NCI_THESAURUS;

    private static String _ncitUrl = NCItBrowserProperties.getNCIT_URL();
    private static final String NCIT_URL =
        _ncitUrl + "/ConceptReport.jsp?dictionary=NCI%20Thesaurus&";


    public static boolean isNull(String text) {
        return text == null || text.equalsIgnoreCase("null");
    }

    public static class JSPHeaderInfo {
        public String dictionary;
        public String version;
        public String version_deprecated;
        protected boolean debugAll = false;
        protected boolean debug = false;

        public void setVersion(String ver) {
			version = ver;
		}

        private void debugDV(String msg, String dictionary, String version) {
            if (debug)
                _logger.debug(msg + "version=" + version +
                    ", dictionary=" + dictionary);
        }

        private void debugAllVersions(HttpServletRequest request) {
            String prefix = "ALL: ";
            if (debug)
                _logger.debug(Utils.SEPARATOR_DASHES);

            String dictionary =  HTTPUtils.cleanXSS((String) request.getParameter("dictionary"));
            String version = HTTPUtils.cleanXSS((String) request.getParameter("version"));
            debugDV(prefix + "Request Parameters: ", dictionary, version);

            dictionary = (String) request.getAttribute("dictionary");
            version = (String) request.getAttribute("version");
            debugDV(prefix + "Request Attributes: ", dictionary, version);

            dictionary = (String) request.getSession().getAttribute("dictionary");
            version = (String) request.getSession().getAttribute("version");
            debugDV(prefix + "Session Attributes: ", dictionary, version);
        }

        public JSPHeaderInfo(HttpServletRequest request) {
            if (debugAll)
                debugAllVersions(request);
            if (debug)
                _logger.debug(Utils.SEPARATOR);
            dictionary = HTTPUtils.cleanXSS((String) request.getParameter("dictionary"));

            dictionary = DataUtils.getCSName(dictionary);
            version = HTTPUtils.cleanXSS((String) request.getParameter("version"));

			 //KLO testing AppScan fix:
			if (dictionary != null) {
				dictionary = HTTPUtils.cleanXSS(dictionary);
			}
			if (version != null) {
				version = HTTPUtils.cleanXSS(version);
			}

            debugDV("Request Parameters: ", dictionary, version);

            if (isNull(dictionary) && isNull(version)) {
                dictionary = (String) request.getAttribute("dictionary");
                version = (String) request.getAttribute("version");
                debugDV("Request Attributes: ", dictionary, version);
            }

            if (isNull(dictionary) && isNull(version)) {
                dictionary = (String) request.getSession().getAttribute("dictionary");
                version = (String) request.getSession().getAttribute("version");
                debugDV("Session Attributes: ", dictionary, version);
            }

            boolean isDictionaryNull = isNull(dictionary);
            boolean isVersionNull = isNull(version);
            if (isDictionaryNull && ! isVersionNull &&
                    DataUtils.isCodingSchemeLoaded(DEFAULT_DICTIONARY, version)) {
                dictionary = DEFAULT_DICTIONARY;
                debugDV("Defaulting to: ", dictionary, version);


            } else if (! isDictionaryNull && isVersionNull) {
                version =
                    DataUtils.getVocabularyVersionByTag(dictionary,
                        "PRODUCTION");
                if (version == null) {
                	String formalName = DataUtils.getFormalName(dictionary);
                	version = DataUtils.getVocabularyVersionByTag(formalName, "PRODUCTION");
                }
                debugDV("Defaulting to: ", dictionary, version);
            } else if (! isDictionaryNull && ! isVersionNull &&
                    ! DataUtils.isCodingSchemeLoaded(dictionary, version)) {
                version_deprecated = version;
                version =
                    DataUtils.getVocabularyVersionByTag(dictionary,
                        "PRODUCTION");
                if (debug) {
                    _logger.debug(Utils.SEPARATOR);
                    _logger.debug("dictionary: " + dictionary);
                    _logger.debug("  * version: " + version);
                    if (version_deprecated != null)
                        _logger.debug("  * version_deprecated: " + version_deprecated);
                    else _logger.debug("  * Note: Version was not specified.  Defaulting to producion.");
                }
            }
            request.getSession().setAttribute("dictionary", dictionary);
            request.getSession().setAttribute("version", version);
        }
    }

    public static class JSPHeaderInfoMore extends JSPHeaderInfo {
        public String display_name;
        public String term_browser_version;

        public JSPHeaderInfoMore(HttpServletRequest request) {
            super(request);
            String formalName = DataUtils.getFormalName(dictionary);
            display_name =
                DataUtils
                    .getMetadataValue(formalName, version, "display_name");
            if (isNull(display_name)) {
            	display_name = DataUtils.getMetadataValue(formalName, "display_name");
            }

            term_browser_version =
                DataUtils.getMetadataValue(formalName, version,
                    "term_browser_version");
            if (isNull(term_browser_version))
                term_browser_version = version;
            if (isNull(version)) {
            	version = DataUtils.getVocabularyVersionByTag(formalName, null); //DataUtils.getVersion(formalName);
            	request.getSession().setAttribute("version", version);
            }
        }


        public String toString() {
			StringBuffer buf = new StringBuffer();
			buf.append("dictionary: ").append(dictionary).append("\n");
			buf.append("\tversion: ").append(version).append("\n");
			buf.append("\tdisplay_name: ").append(display_name).append("\n");
			buf.append("\tterm_browser_version: ").append(term_browser_version);
			return buf.toString();
		}
    }

    public static String getSelectedVocabularyTooltip(HttpServletRequest request) {
        String ontologiesToSearchOn =
            (String) request.getSession().getAttribute("ontologiesToSearchOn");
        if (ontologiesToSearchOn == null)
            return "";

        @SuppressWarnings("unchecked")
        Vector<OntologyInfo> display_name_vec =
            (Vector<OntologyInfo>) request.getSession().getAttribute(
                "display_name_vec");

        Vector<String> ontologies_to_search_on =
            DataUtils.parseData(ontologiesToSearchOn);

        //String value = "";
        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < ontologies_to_search_on.size(); i++) {
            String s = ontologies_to_search_on.elementAt(i);
            String csName = DataUtils.key2CodingSchemeName(s);
            String csVersion = DataUtils.key2CodingSchemeVersion(s);
            String term_browser_version =
                DataUtils.getMetadataValue(csName, csVersion, "term_browser_version");
            String displayName = "";

            if (term_browser_version == null)
                term_browser_version = csVersion;
            for (int j = 0; j < display_name_vec.size(); j++) {
                OntologyInfo info = display_name_vec.elementAt(j);
                String label = info.getLabel();
                if (label.compareTo(s) == 0) {
                    displayName = info.getDisplayName();
                    break;
                }
            }
            displayName += " (" + term_browser_version + ")";
            //value = value + displayName + "<br/>";
            buf.append(displayName + "<br/>");
        }
        return buf.toString();
        //return value;
    }

    public static String getPipeSeparator(Boolean[] display) {
        boolean isDisplayed = display[0].booleanValue();
        if (isDisplayed)
            return "|";
        display[0] = Boolean.TRUE;
        return "";
    }

    public static String getNavType(HttpServletRequest request) {
        boolean debug = false;
		String navigation_type = (String) request.getSession().getAttribute("navigation_type");
		if (navigation_type != null) {
			request.getSession().removeAttribute("navigation_type");
			return navigation_type;
		}

        JSPUtils.JSPHeaderInfo info = new JSPUtils.JSPHeaderInfo(request);

        String vsd_view = HTTPUtils.cleanXSS((String) request.getParameter("view"));
        if (vsd_view != null) return "valuesets";

        String vsd_uri = HTTPUtils.cleanXSS((String) request.getParameter("vsd_uri"));
        String dictionary = info.dictionary;
        String version = info.version;

        String nav_type = HTTPUtils.cleanXSS((String) request.getParameter("nav_type"));
        if (debug) {
            _logger.debug(Utils.SEPARATOR);
            _logger.debug("nav_type (Parameter): " + nav_type);
        }

        nav_type = DataUtils.getNavigationTabType(
            dictionary, version, vsd_uri, nav_type);
        if (debug)
            _logger.debug("nav_type (getNavigationTabType): " + nav_type);

        if (nav_type == null) {
            nav_type = (String) request.getSession().getAttribute("nav_type");
            if (debug)
                _logger.debug("nav_type (Session): " + nav_type);
        }
        if (nav_type == null) {
            nav_type = "terminologies";
            if (debug)
                _logger.debug("nav_type (Default): " + nav_type);
        }

        if (!Arrays.asList(Constants.NAV_TYPE_VALUES).contains(nav_type)) {
            nav_type = "terminologies";
            if (debug)
                _logger.debug("nav_type (Default): " + nav_type);
        }

        request.getSession().setAttribute("nav_type", nav_type);
        return nav_type;
    }

    public static int parseInt(String text, int defaultValue) {
        if (isNull(text))
            return defaultValue;
        try {
            int value = Integer.parseInt(text);
            return value;
        } catch (Exception e) {
            _logger.error(e.getMessage());
            return defaultValue;
        }
    }

    public static String getConceptUrl(HttpServletRequest request,
    	String dictionary, String version, String concept_id) {
    	  String requestURL = request.getRequestURL().toString();
    	  return getConceptUrl(requestURL, dictionary, version, concept_id);
    }

    public static String getConceptUrl(String requestURL,
        String dictionary, String version, String concept_id) {
          int idx = requestURL.indexOf("pages");
          requestURL = requestURL.substring(0, idx);
          String encoded_dictionary = dictionary.replace(" ", "%20");

          String encoded_concept_id = concept_id;
          encoded_concept_id = encoded_concept_id.replaceAll(":", "%3A");

          String url = requestURL;
          url += "ConceptReport.jsp";
          url += "?dictionary=" + encoded_dictionary;
          if (version != null && version.length() > 0)
              url += "&version=" + version;
          url +="&code=" + encoded_concept_id;
          return url;
    }

    private static String _applicationVersionDisplay = null;
    public static String getApplicationVersionDisplay() {
        if (_applicationVersionDisplay == null)
            _applicationVersionDisplay =
                HTTPUtils.cleanXSS(new DataUtils().getApplicationVersionDisplay());
                //HTTPUtils.cleanXSS(new ConceptDetails().getApplicationVersionDisplay());
        return _applicationVersionDisplay;
    }

    public static String getPopUpWindow(Vector<String> schemes, String label) {
		if (schemes.size() > 200) {
			return getPopUpWindowAlt(schemes, label);
		}
        StringBuffer strbuf = new StringBuffer();
        String line = "<A href=\"#\" onmouseover=\"Tip('";
        strbuf.append(line);
        strbuf.append("<ul>");
        for (int i = 0; i < schemes.size(); i++) {
            String scheme = schemes.elementAt(i);
            strbuf.append("<li>");
            line = scheme;
            strbuf.append(line);
            strbuf.append("</li>");
        }
        strbuf.append("</ul>");
        line = "',";
        strbuf.append(line);

        line =
            "WIDTH, 300, TITLE, '" + label + "', SHADOW, true, "
                + "FADEIN, 300, FADEOUT, 300, STICKY, 1, CLOSEBTN, true, "
                + "CLICKCLOSE, true)\"";
        strbuf.append(line);

        line = " onmouseout=UnTip() ";
        strbuf.append(line);
        line = ">" + label + "</A>";
        strbuf.append(line);

        return strbuf.toString();
    }

    public static String getPopUpWindowAlt(Vector<String> schemes, String label) {
        StringBuffer strbuf = new StringBuffer();
        String line = "<a href=\"#\" class=\"tooltip\">";
        strbuf.append(line);
        strbuf.append(label);
        strbuf.append("<span>");
        strbuf.append("<center><strong>");
        strbuf.append(label);
        strbuf.append("</strong></center>");
        strbuf.append("<ul>");
        for (int i = 0; i < schemes.size(); i++) {
            String scheme = schemes.elementAt(i);
            strbuf.append("<li>");
            line = scheme;
            strbuf.append(line);
            strbuf.append("</li>");
        }
        strbuf.append("</ul>");
        strbuf.append("</span>");
        strbuf.append("</a>");
        return strbuf.toString();
	}

    public static String getBookmarkUrl(HttpServletRequest request,
    	String dictionary, String version, String concept_id, String ns) {
    	  String requestURL = request.getRequestURL().toString();
    	  return getBookmarkUrl(requestURL, dictionary, version, concept_id, ns);
    }


    public static String getBookmarkUrl(String requestURL, String dictionary, String version, String concept_id, String ns) {
          int idx = requestURL.indexOf("pages");
          requestURL = requestURL.substring(0, idx);
          String encoded_dictionary = dictionary.replace(" ", "%20");

          String encoded_concept_id = concept_id;
          encoded_concept_id = encoded_concept_id.replaceAll(":", "%3A");

          String url = requestURL;
          url += "ConceptReport.jsp";
          url += "?dictionary=" + encoded_dictionary;
          if (version != null && version.length() > 0) {
              url += "&version=" + version;
		  }

          if (ns != null) {
			  url +="&ns=" + ns;
		  }
          url +="&code=" + encoded_concept_id;
          return url;
    }

	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch(NumberFormatException e) {
			return false;
		}
		return true;
	}

    public static Vector<String> findHyperlinks(String t, String target) {
        Vector<String> v = new Vector<String>();
        boolean found = false;
        // "aspx?"
        String t1 = t;
        String t2 = t;
        String doubleQuote = "\"";
        String t6 = null;
        String replacedWith = null;
        String t5 = null;

        for (int i = 0; i < t.length() - target.length(); i++) {
            String substr = t.substring(i, i + target.length());
            if (substr.compareTo(target) == 0) {
                found = true;
                t1 = t.substring(0, i);
                int k1 = i;
                while (k1 < t.length() - 1) {
                    k1++;
                    String c = t.substring(k1, k1 + 1);
                    if (c.compareTo(doubleQuote) == 0) {
                        t1 = t.substring(0, k1);
                        break;
                    }
                }
                int k2 = i;
                while (k2 > 0) {
                    k2--;
                    String c = t.substring(k2, k2 + 1);
                    if (c.compareTo(doubleQuote) == 0) {
                        t2 = t.substring(0, k2);
                        break;
                    }
                }

                String t3 = t.substring(k2, k1 + 1);
                String t4 = t.substring(k2 + 1, k1);
                v.add(t4);
            }
        }
        return v;
    }

    public static String replaceHyperlinks(String s, String target,
        String hyperlinktext) {
        Vector<String> v = findHyperlinks(s, target);
        //String t3 = "";

        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < v.size(); i++) {
            String str = (String) v.elementAt(i);
            String s1 = str;
            int n1 = s.indexOf(s1);
            String t1 = s.substring(0, n1);
            String t2 = s.substring(n1 + str.length(), s.length());
            s = t2;

            String link = null;

            int index = s.indexOf(hyperlinktext);
            if (index != -1) {
                String text = s.substring(0, index + hyperlinktext.length());
                s = s.substring(index + hyperlinktext.length(), s.length());
                text = text.trim();
                if (text.charAt(0) == '\"') {
                    text = text.substring(1, text.length());
                    text = text.trim();
                }
                if (hyperlinktext.compareTo("NCI Thesaurus") == 0) {
                    str = replaceBrowserURL(str);
                }
                link = createHyperLink(str, text);
            }

            String s2 = link;
            t2 = t1 + s2;
            //t3 = t3 + t2;
            buf.append(t2);
        }
        //t3 = t3 + s;
        buf.append(s);
        String t3 = buf.toString();

        t3 = t3.replaceAll("\"<a", "<a");
        t3 = t3.replaceAll("</a>\"", "</a>");

        return t3;
    }

    public static String createHyperLink(String url, String text) {
        /*
         * String s = "<a href=\"javascript:window.open('" + url +
         * "', 'window id', 'status,scrollbars,resizable,width=800,height=500')\", alt='"
         * + text + "'>" + text + "</a>"; return s;
         */

        String s =
            "<a href=\"" + url + "\" target=\"_blank\" alt=\"" + text + "\">"
                + text + "</a>";
        return s;

    }

    public static String reformatPDQDefinition(String s) {
        String target = "aspx?";
        String t = replaceHyperlinks(s, target, "clinical trials");
        s = t;
        target = "jsp?";
        t = replaceHyperlinks(s, target, "NCI Thesaurus");
        return t;
    }

    public static String replaceBrowserURL(String url) {
        int n = url.indexOf("code=");
        if (n != -1) {
            String t = url.substring(n, url.length());
            return NCIT_URL + t;
        }
        return url;
    }


    public static String replaceInnerEvalExpressions(String s, Vector from_vec, Vector to_vec) {
        String openExp = "<%=";
        String closeExp = "%>";
        //String t = "";

        StringBuffer buf = new StringBuffer();

        int idx = s.indexOf(openExp);
        if (idx == -1)
            return s;

        while (idx != -1) {
            String lhs = s.substring(0, idx);
            //t = t + lhs;

            buf.append(lhs);

            String res = s.substring(idx + 3, s.length());
            int idx2 = s.indexOf(closeExp);

            String expression = s.substring(idx, idx2 + 2);

            String expressionValue = s.substring(idx + 3, idx2);

            for (int i = 0; i < from_vec.size(); i++) {
                String from = (String) from_vec.elementAt(i);
                String to = (String) to_vec.elementAt(i);
                if (expressionValue.compareTo(from) == 0) {
                    expression = to;
                    break;
                }
            }
            buf.append(expression);
            String rhs = s.substring(idx2 + 2, s.length());

            s = rhs;
            idx = s.indexOf(openExp);
        }
        String t = buf.toString();
        t = t + s;
        return t;
    }

    public static String replaceContextPath(String s, String contextPath) {
        if (s == null || contextPath == null)
            return s;
        String openExp = "<%=";
        String closeExp = "%>";
        //String t = "";
        StringBuffer buf = new StringBuffer();

        int idx = s.indexOf(openExp);
        if (idx == -1)
            return s;

        while (idx != -1) {
            String lhs = s.substring(0, idx);
            //t = t + lhs;
            buf.append(lhs);

            String res = s.substring(idx + 3, s.length());
            int idx2 = s.indexOf(closeExp);

            String expression = s.substring(idx, idx2 + 2);
            String expressionValue = s.substring(idx + 3, idx2);

            if (expression.indexOf("request.getContextPath()") != -1) {
                expression = contextPath;
            }
            buf.append(expression);

            String rhs = s.substring(idx2 + 2, s.length());

            s = rhs;
            idx = s.indexOf(openExp);
        }
        String t = buf.toString();
        t = t + s;
        return t;
    }

    public static String getDownloadLink(String url) {
        String t =
            "<a href=\"" + url + "\" target=\"_blank\" alt=\"Download Site\">"
                + url + "</a>";
        return t;
    }


    public static void main(String argv[]) {

        String def =
            "PDQ Definition: A recombinant, chimeric monoclonal antibody directed against the epidermal growth factor (EGFR) with antineoplastic activity. Cetuximab binds to the extracellular domain of the EGFR, thereby preventing the activation and subsequent dimerization of the receptor; the decrease in receptor activation and dimerization may result in an inhibition in signal transduction and anti-proliferative effects. This agent may inhibit EGFR-dependent primary tumor growth and metastasis. EGFR is overexpressed on the cell surfaces of various solid tumors."
                + " Check for \"http://www.cancer.gov/Search/ClinicalTrialsLink.aspx?id=42384&idtype=1\" active clinical trials or \"http://www.cancer.gov/Search/ClinicalTrialsLink.aspx?id=42384&idtype=1&closed=1\" closed clinical trials using this agent."
                + "(\"http://nciterms.nci.nih.gov:80/NCIBrowser/ConceptReport.jsp?dictionary=NCI_Thesaurus&code=C1723\" NCI Thesaurus)\";";
        JSPUtils test = new JSPUtils();

        String t = test.reformatPDQDefinition(def);
        _logger.info(t);
    }
}
