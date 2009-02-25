
package gov.nih.nci.evs.browser.servlet;

import org.json.*;
import gov.nih.nci.evs.browser.utils.*;

import java.io.IOException;

import java.util.Collection;
import java.util.ArrayList;

import java.util.HashMap;


import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
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

import javax.servlet.RequestDispatcher;


public final class DispatchServlet extends HttpServlet {


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


/*
	<option value="Quick Links" selected>Quick Links</option>
	<option value="NCI Terminology Browser">NCI Terminology Browser</option>
	<option value="NCI MetaThesaurus">NCI MetaThesaurus</option>
	<option value="EVS Home">EVS Home</option>
	<option value="NCI Terminology Resources">NCI Terminology Resources</option>

	<option value="http://nciterms.nci.nih.gov">NCI Terminology Browser</option>
	<option value="http://ncimeta.nci.nih.gov/MetaServlet/">NCI MetaThesaurus</option>
	<option value="http://ncicb.nci.nih.gov/NCICB/infrastructure/cacore_overview/vocabulary">EVS Home</option>
	<option value="http://www.cancer.gov/cancertopics/terminologyresources">NCI Terminology Resources</option>


*/


    public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		  //String selectedQuickLink = (String) request.getSession().getAttribute("selectedQuickLink");
String selectedQuickLink = (String) request.getParameter("quickLink");

System.out.println("====================== selectedQuickLink: " + selectedQuickLink);

		  //HttpSession session = request.getSession(false);
		  //if (session != null) {
			  String targetURL = null;//"http://nciterms.nci.nih.gov/";
			  if (selectedQuickLink.compareTo("NCI Terminology Browser") == 0) {
				  targetURL = "http://nciterms.nci.nih.gov/";
			  }
			  else if (selectedQuickLink.compareTo("NCI MetaThesaurus Browser") == 0) {
				  targetURL = "http://ncimeta.nci.nih.gov/MetaServlet/";
			  }
			  else if (selectedQuickLink.compareTo("EVS Home") == 0) {
				  targetURL = "http://ncicb.nci.nih.gov/NCICB/infrastructure/cacore_overview/vocabulary";
			  }
			  else if (selectedQuickLink.compareTo("NCI Terminology Resources") == 0) {
				  targetURL = "http://www.cancer.gov/cancertopics/terminologyresources";
			  }

			  response.sendRedirect(response.encodeRedirectURL(targetURL));


			  //RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(targetURL);
			  //dispatcher.forward(request, response);
		  //}
	}
}
