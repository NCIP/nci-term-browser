package gov.nih.nci.evs.browser.utils;

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

/**
 * @author EVS Team
 * @version 1.0
 *
 *          Modification history Initial implementation kim.ong@ngc.com
 *
 */

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

    private static String getVisitedConceptLink(Vector<String> concept_vec) {
        StringBuffer strbuf = new StringBuffer();
        String line = "<A href=\"#\" onmouseover=\"Tip('";
        strbuf.append(line);
        strbuf.append("<ul>");
        for (int i = 0; i < concept_vec.size(); i++) {
            int j = concept_vec.size() - i - 1;
            String concept_data = (String) concept_vec.elementAt(j);
            Vector<String> w = DataUtils.parseData(concept_data);
            String scheme = (String) w.elementAt(0);
            String formalName = DataUtils.getFormalName(scheme);
            // String localName = DataUtils.getLocalName(scheme);
            String vocabulary_name =
                DataUtils.getMetadataValue(formalName, "display_name");
            String code = (String) w.elementAt(1);
            String name = (String) w.elementAt(2);
            name = DataUtils.htmlEntityEncode(name);
            strbuf.append("<li>");
            line =
                "<a href=\\'/ncitbrowser/ConceptReport.jsp?dictionary="
                    + formalName + "&code=" + code + "\\'>" + name + " &#40;"
                    + vocabulary_name + "&#41;" + "</a><br>";
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

    public static String getDisplayLink(HttpServletRequest request) {
        @SuppressWarnings("unchecked")
        Vector<String> visitedConcepts =
            (Vector<String>) request.getSession().getAttribute(
                "visitedConcepts");
        if (visitedConcepts == null || visitedConcepts.size() <= 0)
            return "";

        String value = getVisitedConceptLink(visitedConcepts);
        return value;
    }
}
