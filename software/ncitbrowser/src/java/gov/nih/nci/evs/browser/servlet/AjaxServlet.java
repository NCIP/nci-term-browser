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
import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.log4j.*;

import gov.nih.nci.evs.browser.properties.*;
import static gov.nih.nci.evs.browser.common.Constants.*;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;


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

public final class AjaxServlet extends HttpServlet {
    private static Logger _logger = Logger.getLogger(AjaxServlet.class);
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
        String action = request.getParameter("action");// DataConstants.ACTION);
        String node_id = request.getParameter("ontology_node_id");// DataConstants.ONTOLOGY_NODE_ID);
        String ontology_display_name =
            request.getParameter("ontology_display_name");// DataConstants.ONTOLOGY_DISPLAY_NAME);

        String ontology_version = request.getParameter("version");
        if (ontology_version == null) {
			ontology_version = DataUtils.getVocabularyVersionByTag(ontology_display_name, "PRODUCTION");
		}

        long ms = System.currentTimeMillis();

        if (action.equals("expand_tree")) {
            if (node_id != null && ontology_display_name != null) {
                response.setContentType("text/html");
                response.setHeader("Cache-Control", "no-cache");
                JSONObject json = new JSONObject();
                JSONArray nodesArray = null;
                try {

                    // for HL7 (temporary fix)
                    ontology_display_name =
                        DataUtils.searchFormalName(ontology_display_name);

                    nodesArray =
                        CacheController.getInstance().getSubconcepts(
                            ontology_display_name, ontology_version, node_id);
                    if (nodesArray != null) {
                        json.put("nodes", nodesArray);
                    }

                } catch (Exception e) {
                }
                response.getWriter().write(json.toString());
                _logger.debug("Run time (milliseconds): "
                    + (System.currentTimeMillis() - ms));
            }
        }

        /*
         * else if (action.equals("search_tree")) {
         *
         *
         * if (node_id != null && ontology_display_name != null) {
         * response.setContentType("text/html");
         * response.setHeader("Cache-Control", "no-cache"); JSONObject json =
         * new JSONObject(); try { // testing // JSONArray rootsArray = //
         * CacheController.getInstance().getPathsToRoots(ontology_display_name,
         * // null, node_id, true);
         *
         * String max_tree_level_str = null; int maxLevel = -1; try {
         * max_tree_level_str = NCItBrowserProperties .getInstance()
         * .getProperty( NCItBrowserProperties.MAXIMUM_TREE_LEVEL); maxLevel =
         * Integer.parseInt(max_tree_level_str);
         *
         * } catch (Exception ex) {
         *
         * }
         *
         * JSONArray rootsArray = CacheController.getInstance()
         * .getPathsToRoots(ontology_display_name, null, node_id, true,
         * maxLevel);
         *
         * if (rootsArray.length() == 0) { rootsArray =
         * CacheController.getInstance() .getRootConcepts(ontology_display_name,
         * null);
         *
         * boolean is_root = isRoot(rootsArray, node_id); if (!is_root) {
         * //rootsArray = null; json.put("dummy_root_nodes", rootsArray);
         * response.getWriter().write(json.toString());
         * response.getWriter().flush();
         *
         * _logger.debug("Run time (milliseconds): " +
         * (System.currentTimeMillis() - ms)); return; } }
         * json.put("root_nodes", rootsArray); } catch (Exception e) {
         * e.printStackTrace(); }
         *
         * response.getWriter().write(json.toString());
         * response.getWriter().flush();
         *
         * _logger.debug("Run time (milliseconds): " +
         * (System.currentTimeMillis() - ms)); return; } }
         */
        if (action.equals("search_tree")) {

            if (node_id != null && ontology_display_name != null) {
                response.setContentType("text/html");
                response.setHeader("Cache-Control", "no-cache");
                JSONObject json = new JSONObject();
                try {
                    // testing
                    // JSONArray rootsArray =
                    // CacheController.getInstance().getPathsToRoots(ontology_display_name,
                    // null, node_id, true);

                    String max_tree_level_str = null;
                    int maxLevel = -1;
                    try {
                        max_tree_level_str =
                            NCItBrowserProperties.getInstance().getProperty(
                                NCItBrowserProperties.MAXIMUM_TREE_LEVEL);
                        maxLevel = Integer.parseInt(max_tree_level_str);

                    } catch (Exception ex) {

                    }
                    CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
                    if (ontology_version != null) versionOrTag.setVersion(ontology_version);

                    String jsonString =
                        CacheController.getInstance().getTree(
                            ontology_display_name, versionOrTag, node_id);
                    JSONArray rootsArray = new JSONArray(jsonString);

                    json.put("root_nodes", rootsArray);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                response.getWriter().write(json.toString());
                response.getWriter().flush();

                _logger.debug("Run time (milliseconds): "
                    + (System.currentTimeMillis() - ms));
                return;
            }
        }

        else if (action.equals("build_tree")) {
            if (ontology_display_name == null)
                ontology_display_name = CODING_SCHEME_NAME;

            response.setContentType("text/html");
            response.setHeader("Cache-Control", "no-cache");
            JSONObject json = new JSONObject();
            JSONArray nodesArray = null;// new JSONArray();
            try {
                nodesArray =
                    CacheController.getInstance().getRootConcepts(
                        ontology_display_name, null);
                if (nodesArray != null) {
                    json.put("root_nodes", nodesArray);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            response.getWriter().write(json.toString());
            // response.getWriter().flush();

            _logger.debug("Run time (milliseconds): "
                + (System.currentTimeMillis() - ms));
            return;
        }
    }

    private boolean isRoot(JSONArray rootsArray, String code) {
        for (int i = 0; i < rootsArray.length(); i++) {
            String node_id = null;
            try {
                JSONObject node = rootsArray.getJSONObject(i);
                node_id = (String) node.get(CacheController.ONTOLOGY_NODE_ID);
                if (node_id.compareTo(code) == 0)
                    return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
