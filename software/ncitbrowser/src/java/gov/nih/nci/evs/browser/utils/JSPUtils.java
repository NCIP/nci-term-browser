/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.utils;

import gov.nih.nci.evs.browser.common.*;

import java.util.*;

import javax.servlet.http.*;

import org.apache.log4j.*;

/**
 * 
 */

public class JSPUtils {
    private static Logger _logger = Logger.getLogger(JSPUtils.class);
    private static final String DEFAULT_DICTIONARY = Constants.NCI_THESAURUS;

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

            String dictionary = request.getParameter("dictionary");
            String version = request.getParameter("version");
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
            dictionary = request.getParameter("dictionary");
            version = request.getParameter("version");


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
        String value = "";
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
            value = value + displayName + "<br/>";
        }
        return value;
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

        String vsd_view = (String) request.getParameter("view");
        if (vsd_view != null) return "valuesets";

        String vsd_uri = (String) request.getParameter("vsd_uri");
        String dictionary = info.dictionary;
        String version = info.version;

        String nav_type = (String) request.getParameter("nav_type");
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
}
