/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.utils;

import java.util.*;
import javax.servlet.http.*;
import org.apache.log4j.*;

/**
 * 
 */

/**
 * @author EVS Team
 * @version 1.0
 * 
 *          Modification history Initial implementation kim.ong@ngc.com
 * 
 */

public class VisitedConceptUtils {
    private static Logger _logger = Logger.getLogger(VisitedConceptUtils.class);

    private static class VisitedConcept {
        public String scheme = "";
        public String version = "";
        public String code = "";
        public String name = "";
        public String value = "";

        public VisitedConcept(String scheme, String version, String code,
            String name) {
            this.scheme = scheme;
            this.version = version;
            this.code = code;
            this.name = name;
            this.value = name + " (" + scheme + " " + version + ")";
        }
        
        public String getValue() {
            return value;
        }

        public String toString() {
            return value;
        }

        public boolean exists(Vector<VisitedConcept> concepts) {
            Iterator<VisitedConcept> iterator = concepts.iterator();
            while (iterator.hasNext()) {
                VisitedConcept visitedConcept = iterator.next();
                if (visitedConcept.getValue().equals(value))
                    return true;
            }
            return false;
        }
    }

    public static void add(HttpServletRequest request, String dictionary,
        String version, String code, String name) {
        @SuppressWarnings("unchecked")
        Vector<VisitedConcept> visitedConcepts =
            (Vector<VisitedConcept>) request.getSession().getAttribute(
                "visitedConcepts");
        if (visitedConcepts == null)
            visitedConcepts = new Vector<VisitedConcept>();

        String localCodingSchemeName = DataUtils.getLocalName(dictionary);
        VisitedConcept visitedConcept =
            new VisitedConcept(localCodingSchemeName, version, code, name);

        if (visitedConcept.exists(visitedConcepts))
            return;

        visitedConcepts.add(visitedConcept);
        _logger.debug("add: " + visitedConcept);
        request.getSession().removeAttribute("visitedConcepts");
        request.getSession().setAttribute("visitedConcepts", visitedConcepts);
    }

    private static String getLink(Vector<VisitedConcept> visitedConcepts) {
        StringBuffer strbuf = new StringBuffer();
        String line = "<A href=\"#\" onmouseover=\"Tip('";
        strbuf.append(line);
        strbuf.append("<ul>");
        for (int i = 0; i < visitedConcepts.size(); i++) {
            int j = visitedConcepts.size() - i - 1;
            VisitedConcept visitedConcept = visitedConcepts.elementAt(j);
            String scheme = visitedConcept.scheme;
            String formalName = DataUtils.getFormalName(scheme);
            // String localName = DataUtils.getLocalName(scheme);
            String version = visitedConcept.version;
            String display_name =
                DataUtils.getMetadataValue(formalName, version, "display_name");
            if (display_name == null) {
                _logger.warn("Missing \"display_name\" metadata for: " + formalName);
                _logger.warn("  * Defaulting to display_name value to: " + formalName);
                display_name = formalName;
            }
            String code = visitedConcept.code;
            String name = visitedConcept.name;
            name = DataUtils.htmlEntityEncode(name);
            strbuf.append("<li>");

            String versionParameter = "", versionParameterDisplay = "";
            if (version != null && version.length() > 0) {
                versionParameter = "&version=" + version;
                String term_browser_version = DataUtils.getMetadataValue(
                    formalName, version, "term_browser_version");
                if (term_browser_version == null) {
                    _logger.warn("Missing \"term_browser_version\" metadata for: " + formalName);
                    _logger.warn("  * Defaulting to \"term_browser_version\" value to: " + version);
                    term_browser_version = version;
                }
                versionParameterDisplay = term_browser_version;
            }

            line =
                "<a href=\\'/ncitbrowser/ConceptReport.jsp?dictionary="
                    + formalName + versionParameter + "&code=" + code + "\\'>"
                    + name + " &#40;" + display_name + " "
                    + versionParameterDisplay + "&#41;" + "</a><br>";
            strbuf.append(line);
            strbuf.append("</li>");
        }
        strbuf.append("</ul>");
        line = "',";
        strbuf.append(line);

        line =
            "WIDTH, 300, TITLE, 'Visited Concepts', SHADOW, true, "
                + "FADEIN, 300, FADEOUT, 300, STICKY, 1, CLOSEBTN, true, "
                + "CLICKCLOSE, true)\"";
        strbuf.append(line);

        line = " onmouseout=UnTip() ";
        strbuf.append(line);
        line = ">Visited Concepts</A>";
        strbuf.append(line);

        return strbuf.toString();
    }

    public static String getDisplayLink(HttpServletRequest request, 
        Boolean[] displaySeparator) {
        //Note: Boolean[] displaySeparator is a hack to return the
        //  state of the display separate.
        @SuppressWarnings("unchecked")
        Vector<VisitedConcept> visitedConcepts =
            (Vector<VisitedConcept>) request.getSession().getAttribute(
                "visitedConcepts");
        if (visitedConcepts == null || visitedConcepts.size() <= 0)
            return "";

        String value = getLink(visitedConcepts);
        String pipe = JSPUtils.getPipeSeparator(displaySeparator);
        if (pipe.length() > 0)
            value = pipe + " " + value;
        return value;
    }
    
    public static String getDisplayLink(HttpServletRequest request, 
        boolean displaySeparator) {
        Boolean[] isPipeDisplayed = new Boolean[] { new Boolean(displaySeparator) };
        return getDisplayLink(request, isPipeDisplayed);
    }
    
    public static String getDisplayLink(HttpServletRequest request) {
        return getDisplayLink(request, false);
    }
}
