package gov.nih.nci.evs.browser.servlet;

/**
  * <!-- LICENSE_TEXT_START -->
* Copyright 2008,2009 NGIT. This software was developed in conjunction with the National Cancer Institute,
* and so to the extent government employees are co-authors, any rights in such works shall be subject to Title 17 of the United States Code, section 105.
* Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
* 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the disclaimer of Article 3, below. Redistributions
* in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other
* materials provided with the distribution.
* 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
* "This product includes software developed by NGIT and the National Cancer Institute."
* If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself,
* wherever such third-party acknowledgments normally appear.
* 3. The names "The National Cancer Institute", "NCI" and "NGIT" must not be used to endorse or promote products derived from this software.
* 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize
* the recipient to use any trademarks owned by either NCI or NGIT
* 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
* MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE NATIONAL CANCER INSTITUTE,
* NGIT, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
* PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
* WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
  * <!-- LICENSE_TEXT_END -->
  */

/**
  * @author EVS Team
  * @version 1.0
  *
  * Modification history
  *     Initial implementation kim.ong@ngc.com
  *
 */

import org.json.*;
import gov.nih.nci.evs.browser.utils.*;

import gov.nih.nci.evs.browser.utils.TreeUtils.TreeItem;
import gov.nih.nci.evs.browser.utils.CacheManager;
import gov.nih.nci.evs.browser.utils.CacheController;

import java.io.IOException;

import java.util.Collection;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.Vector;
import java.util.Set;
import java.util.Collections;
import java.util.Map;


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

	long ms = System.currentTimeMillis();

    if (action.equals("expand_tree")) {
        if (node_id != null && ontology_display_name != null) {
			response.setContentType("text/html");
			response.setHeader("Cache-Control", "no-cache");
			JSONObject json = new JSONObject();
			JSONArray nodesArray = null;
			try {
				//HashMap hmap = CacheManager.getInstance().getSubconcepts(ontology_display_name, null, node_id);
				HashMap hmap = CacheController.getInstance().getSubconcepts(ontology_display_name, null, node_id);
				nodesArray = new JSONArray();
				Set keyset = hmap.keySet();
				Object[] objs = keyset.toArray();
				String code = (String) objs[0];
				TreeItem ti = (TreeItem) hmap.get(code);
				for (String association : ti.assocToChildMap.keySet()) {
					List<TreeItem> children = ti.assocToChildMap.get(association);
					Collections.sort(children);
					for (TreeItem childItem : children) {
						//printTree(childItem, focusCode, depth + 1);
						JSONObject nodeObject = new JSONObject();
						nodeObject.put(ONTOLOGY_NODE_ID, childItem.code);
						nodeObject.put(ONTOLOGY_NODE_NAME, childItem.text);
						int knt = 0;
						if (childItem.expandable)
						{
							knt = 1;
						}
						nodeObject.put(ONTOLOGY_NODE_CHILD_COUNT, knt);
						nodesArray.put(nodeObject);
					}
				}

				//nodesArray = CacheManager.getInstance().getSubconcepts(ontology_display_name, null, node_id);
				if (nodesArray != null)
				{
					json.put("nodes", nodesArray);
				}

			} catch (Exception e) {
			}
			response.getWriter().write(json.toString());
			System.out.println("Run time (milliseconds): " + (System.currentTimeMillis() - ms) );
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


/*
    if (action.equals("build_tree")) {//DataConstants.ACTION_BUILD_TREE)) {
	    if (ontology_display_name == null) ontology_display_name = "NCI Thesaurus";
        response.setContentType("text/html");
        response.setHeader("Cache-Control", "no-cache");
        JSONObject json = new JSONObject();
        JSONArray nodesArray = null;
        try {
			nodesArray = CacheManager.getInstance().getRootConcepts(ontology_display_name, null);
			if (nodesArray != null)
			{
				json.put("root_nodes", nodesArray);
			}
	    } catch (Exception e) {
		  e.printStackTrace();
		}

		response.getWriter().write(json.toString());
		System.out.println("Run time (milliseconds): " + (System.currentTimeMillis() - ms) );
        return;
      }
*/

    if (action.equals("build_tree")) {
	    if (ontology_display_name == null) ontology_display_name = "NCI Thesaurus";
        response.setContentType("text/html");
        response.setHeader("Cache-Control", "no-cache");
        JSONObject json = new JSONObject();
        JSONArray nodesArray = null;//new JSONArray();
        try {
			//List list = CacheManager.getInstance().getRootConcepts(ontology_display_name, null);
			List list = CacheController.getInstance().getRootConcepts(ontology_display_name, null);

			if (list != null)
			{
				nodesArray = new JSONArray();
				for (int i=0; i<list.size(); i++) {
				  ResolvedConceptReference node = (ResolvedConceptReference) list.get(i);
				  Concept concept = node.getReferencedEntry();
				  int childCount = 1; // assumption

				  JSONObject nodeObject = new JSONObject();
				  nodeObject.put(ONTOLOGY_NODE_ID, node.getConceptCode());

				  //String pt = getPreferredName(concept);

				  String name = concept.getEntityDescription().getContent();
				  nodeObject.put(ONTOLOGY_NODE_NAME, name);
				  nodeObject.put(ONTOLOGY_NODE_CHILD_COUNT, childCount);
				  nodesArray.put(nodeObject);
				}
			}
			if (nodesArray != null)
			{
				json.put("root_nodes", nodesArray);
			}
	    } catch (Exception e) {
		  e.printStackTrace();
		}

		response.getWriter().write(json.toString());
		System.out.println("Run time (milliseconds): " + (System.currentTimeMillis() - ms) );
        return;

      }
   }

}
