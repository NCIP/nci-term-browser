
package gov.nih.nci.evs.browser.servlet;

import org.json.*;
import gov.nih.nci.evs.browser.utils.*;

import java.io.IOException;

import java.util.Collection;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.List;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import gov.nih.nci.system.applicationservice.EVSApplicationService;


import java.util.Arrays;
import java.util.Iterator;

import org.LexGrid.LexBIG.DataModel.Collections.AssociatedConceptList;
import org.LexGrid.LexBIG.DataModel.Collections.AssociationList;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.AssociatedConcept;
import org.LexGrid.LexBIG.DataModel.Core.Association;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Extensions.Generic.LexBIGServiceConvenienceMethods;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.commonTypes.EntityDescription;
import org.LexGrid.concepts.Concept;
import org.LexGrid.LexBIG.DataModel.Collections.ConceptReferenceList;

import org.LexGrid.LexBIG.DataModel.Core.ConceptReference;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.concepts.Presentation;



public final class AjaxServlet extends HttpServlet {

	protected final Logger logger = Logger.getLogger(this.getClass());

  public static final String ONTOLOGY_ADMINISTRATORS = "ontology_administrators";
  public static final String ONTOLOGY_FILE = "ontology_file";
  public static final String ONTOLOGY_FILE_ID = "ontology_file_id";
  public static final String ONTOLOGY_DISPLAY_NAME = "ontology_display_name";
  public static final String ONTOLOGY_NODE = "ontology_node";
  public static final String ONTOLOGY_NODE_ID = "ontology_node_id";

  public static final String ONTOLOGY_SOURCE = "ontology_source";

  public static final String ONTOLOGY_NODE_NAME = "ontology_node_name";
  public static final String ONTOLOGY_NODE_PARENT_ASSOC = "ontology_node_parent_assoc";
  public static final String ONTOLOGY_NODE_CHILD_COUNT = "ontology_node_child_count";
  public static final String ONTOLOGY_NODE_DEFINITION = "ontology_node_definition";

	/**
   * Validates the Init and Context parameters, configures authentication URL
   *
   * @throws ServletException if the init parameters are invalid or any
   * other problems occur during initialisation
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
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    execute(request, response);
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
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
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



  private String getLink(String code) {
      String t = "<a href=\"javascript:onClickNodeId(" + "'" + code + "'"  + ")\">" + code + "</a>";
      return t;
  }

  public String convertToHyperLink(String s)
  {
	  StringBuffer str = new StringBuffer(s);
	  int fromIndex = 0;
	  int len = str.length();
	  int k = -1;
	  for (int i=0; i<len; i++)
	  {
		  int j = len-(i+1);
		  char c = str.charAt(j);
		  if (c == '(')
		  {
			  k = j;
			  break;
		  }
	  }

	  if (k == -1) return null;

	  //int pos = str.lastIndexOf("(", 0);
	  int pos = k;
	  String s1 = str.substring(0, pos);
	  String s2 = str.substring(pos+1, str.length()-1);
	  String t = s1 + getLink(s2);
	  return t;
  }




  public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    // Determine request by attributes
    String action = request.getParameter("action");//DataConstants.ACTION);
    String node_id = request.getParameter("ontology_node_id");//DataConstants.ONTOLOGY_NODE_ID);
    String ontology_display_name = request.getParameter("ontology_display_name");//DataConstants.ONTOLOGY_DISPLAY_NAME);
   // String ontology_source = request.getParameter(DataConstants.ONTOLOGY_SOURCE);

    if (action.equals("expand_tree")) {

      if (node_id != null && ontology_display_name != null) {
        response.setContentType("text/html");
        response.setHeader("Cache-Control", "no-cache");
        JSONObject json = new JSONObject();

        try {
          SearchUtils util = new SearchUtils();
          Vector subconcept_vec = util.getSubconcepts(ontology_display_name, null, node_id);
          JSONArray nodesArray = new JSONArray();
          for (int i=0; i<subconcept_vec.size(); i++)
          {
			  Concept node = (Concept) subconcept_vec.elementAt(i);
			  JSONObject nodeObject = new JSONObject();

				  nodeObject.put(ONTOLOGY_NODE_ID, node.getId());
				  // to be implemented
				  //nodeObject.put(ONTOLOGY_NODE_NAME, node.getName());

				  //Concept concept = getConceptByCode(ontology_display_name, null, null, node.getConceptCode());
				  //String pt = getPreferredName(concept);

				  String name = node.getEntityDescription().getContent();

				  //nodeObject.put(ONTOLOGY_NODE_NAME, pt);
				  nodeObject.put(ONTOLOGY_NODE_NAME, name);

                  // to be modified
				  Vector sub_vec = util.getSubconcepts(ontology_display_name, null, node.getId());

				  nodeObject.put(ONTOLOGY_NODE_CHILD_COUNT, sub_vec.size());
				  nodesArray.put(nodeObject);

          }
          json.put("nodes", nodesArray);
        }
        catch (Exception e) {
          //LogUtils.log(logger, Level.ERROR, e);
        }
        response.getWriter().write(json.toString());
        return;
      }

    }



    if (action.equals("search_tree")) {//DataConstants.ACTION_SEARCH_TREE)) {

      if (node_id != null && ontology_display_name != null) {
        response.setContentType("text/html");
        response.setHeader("Cache-Control", "no-cache");
        JSONObject json = new JSONObject();

        try {
			//To be implemented



        }
        catch (Exception e) {
          //LogUtils.log(logger, Level.ERROR, e);
        }
        response.getWriter().write(json.toString());
        return;
      }

    }



    if (action.equals("build_tree")) {//DataConstants.ACTION_BUILD_TREE)) {

	  if (ontology_display_name == null) ontology_display_name = "NCI Thesaurus";

      if (ontology_display_name != null) {
        response.setContentType("text/html");
        response.setHeader("Cache-Control", "no-cache");
        JSONObject json = new JSONObject();
        JSONArray nodesArray = new JSONArray();

        List list = null;
        try {
				//Collection collection = searchService.getTopLevelNodes(ontology_display_name, ontology_source);
				String hierarchyID = null;//"hasSubtpe";
				DataUtils util = new DataUtils();
				CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
				list = util.getHierarchyRoots(ontology_display_name, csvt, hierarchyID);

				if (list != null)
				{
					for (int i=0; i<list.size(); i++) {
					  ResolvedConceptReference node = (ResolvedConceptReference) list.get(i);

					  //int childCount = node.getChildrenCount();
					  // to be modified, use cache
					  int childCount = 1;

					  JSONObject nodeObject = new JSONObject();
					  nodeObject.put(ONTOLOGY_NODE_ID, node.getConceptCode());
					  // to be implemented
					  //nodeObject.put(ONTOLOGY_NODE_NAME, node.getName());

					  Concept concept = util.getConceptByCode(ontology_display_name, null, null, node.getConceptCode());

					  String name = concept.getEntityDescription().getContent();

					  //nodeObject.put(ONTOLOGY_NODE_NAME, pt);
					  nodeObject.put(ONTOLOGY_NODE_NAME, name);

					  nodeObject.put(ONTOLOGY_NODE_CHILD_COUNT, childCount);
					  nodesArray.put(nodeObject);
					}
					json.put("root_nodes", nodesArray);
					response.getWriter().write(json.toString());
					return;
			    }

			}
		catch (Exception e) {
		  e.printStackTrace();
		}
	  }
    }

  }



}
