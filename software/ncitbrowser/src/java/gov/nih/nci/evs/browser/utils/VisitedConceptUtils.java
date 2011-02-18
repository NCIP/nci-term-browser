package gov.nih.nci.evs.browser.utils;

import java.util.*;
import javax.servlet.http.*;
import org.apache.log4j.*;

public class VisitedConceptUtils {
    private static Logger _logger = Logger.getLogger(VisitedConceptUtils.class);

    public static void add(HttpServletRequest request, String dictionary,
        String code, String name) {
        @SuppressWarnings("unchecked")
        Vector<String> visitedConcepts =
            (Vector<String>) request.getSession().getAttribute(
                "visitedConcepts");
        if (visitedConcepts == null)
            visitedConcepts = new Vector<String>();

        String localCodingSchemeName = DataUtils.getLocalName(dictionary);
        String value = localCodingSchemeName + "|" + code + "|" + name;
        if (visitedConcepts.contains(value))
            return;

        visitedConcepts.add(value);
        _logger.debug("add: " + value);
        request.getSession().removeAttribute("visitedConcepts");
        request.getSession().setAttribute("visitedConcepts", visitedConcepts);
    }

    public static String getDisplayLink(HttpServletRequest request) {
        @SuppressWarnings("unchecked")
        Vector<String> visitedConcepts =
            (Vector<String>) request.getSession().getAttribute(
                "visitedConcepts");
        if (visitedConcepts == null || visitedConcepts.size() <= 0)
            return "";

        String value = DataUtils.getVisitedConceptLink(visitedConcepts);
        return value;
    }
}
