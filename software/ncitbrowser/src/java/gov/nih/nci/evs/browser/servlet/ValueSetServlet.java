/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.servlet;

import org.json.*;
import gov.nih.nci.evs.browser.utils.*;

import java.io.*;
import java.net.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.log4j.*;

import gov.nih.nci.evs.browser.properties.*;
import static gov.nih.nci.evs.browser.common.Constants.*;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.lexgrid.valuesets.LexEVSValueSetDefinitionServices;


/**
 * 
 */

/**
 * @author EVS Team
 * @version 1.0
 *
 * Modification history
 *     Initial implementation kim.ong@ngc.com
 *
 */

public final class ValueSetServlet extends HttpServlet {
    private static Logger _logger = Logger.getLogger(ValueSetServlet.class);
    /**
     * local constants
     */
    private static final long serialVersionUID = 1L;

    /**
     * Validates the Init and Context parameters, configures authentication URL
     *
     * @throws ServletException if the init parameters are invalid or any other
     *         problems occur during initialisation
     */
    public void init() throws ServletException {

    }

    /**
     * Route the user to the execute method
     *
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        execute(request, response);
    }

    /**
     * Route the user to the execute method
     *
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a Servlet exception occurs
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        execute(request, response);
    }

    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     *
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     */

    public void execute(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        // Determine request by attributes
        String uri = HTTPUtils.cleanXSS(request.getParameter("valueset"));

        long ms = System.currentTimeMillis();

        LexEVSValueSetDefinitionServices vsd_service = RemoteServerUtil.getLexEVSValueSetDefinitionServices();

        String s = "";
        String valueSetDefinitionRevisionId = null;
        try {
			URI valueSetDefinitionURI = new URI(uri);

			StringBuffer sb = new StringBuffer();
			sb.append("<html>\n");
			sb.append("\n");
			sb.append("<head>\n");
			sb.append("<title>" + uri + "</title>\n");
			sb.append("</head>\n");
			sb.append("\n");
			sb.append("<body>\n");
			sb.append("\n");
	        StringBuffer buf = vsd_service.exportValueSetDefinition(valueSetDefinitionURI, valueSetDefinitionRevisionId);
	        sb.append(buf.toString());
            response.getWriter().write(sb.toString());

            sb = new StringBuffer();
            sb.append("</body></html>\n");

            response.getWriter().write(sb.toString());

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		_logger.debug("Run time (milliseconds): "
			+ (System.currentTimeMillis() - ms));

    }
}

