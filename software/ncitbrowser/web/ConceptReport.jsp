<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Vector"%>
<%@ page import="java.util.HashSet"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Set"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="gov.nih.nci.evs.browser.bean.*" %>
<%@ page import="gov.nih.nci.evs.browser.utils.*" %>
<%@ page import="org.LexGrid.concepts.Entity" %>
<%@ page import="gov.nih.nci.evs.browser.common.*"%>
<%@ page import="gov.nih.nci.evs.browser.utils.RemoteServerUtil" %>
<%@ page import="org.LexGrid.LexBIG.LexBIGService.LexBIGService"%>
<%@ page import="org.LexGrid.LexBIG.Utility.Iterators.*"%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
 <body>
   <%
   String dictionary =  HTTPUtils.cleanXSS((String) request.getParameter("dictionary"));
   if (dictionary == null) {
       dictionary = "NCI_Thesaurus";
   }
   
Boolean cs_available = DataUtils.isCodingSchemeAvailable(dictionary);
if (cs_available == null || !cs_available.equals(Boolean.TRUE)) {
    String error_msg = "WARNING: " + dictionary + Constants.CODING_SCHEME_NOT_AVAILABLE;
    request.getSession().setAttribute("error_msg", error_msg);
    String redirectURL = request.getContextPath() + "/pages/coding_scheme_unavailable.jsf";
    response.sendRedirect(redirectURL);
    
}  else { 
   
   String code =  HTTPUtils.cleanXSS((String) request.getParameter("code"));
   String version =  HTTPUtils.cleanXSS((String) request.getParameter("version"));
   String ns =  HTTPUtils.cleanXSS((String) request.getParameter("ns"));
   
   String version_parameter = "";
   LexBIGService lexBIGService = RemoteServerUtil.createLexBIGService();
   ConceptDetails conceptDetails = new ConceptDetails(lexBIGService);
   if (version != null && ! version.equalsIgnoreCase("null")) {
       version_parameter = "&version=" + version;
   } else {
	version = conceptDetails.getVocabularyVersionByTag(dictionary, "PRODUCTION");
        version_parameter = "&version=" + version;
   }
   String ns_parameter = "";
   String jsfPage = "/pages/concept_details.jsf";
   boolean multiple_namespaces = false;
   if (ns != null && ! ns.equalsIgnoreCase("null")) {
       ns_parameter = "&ns=" + ns;
   } else {
       List namespaces = conceptDetails.getDistinctNamespacesOfCode(dictionary, version, code);
       if (namespaces.size() == 1) {
           ns = (String) namespaces.get(0);
           ns_parameter = "&ns=" + ns;
       } else if (namespaces.size() > 1) {
           Vector schemes = new Vector();
           Vector versions = new Vector();
           schemes.add(dictionary);
           versions.add(version);
           String matchText = code;
           String source = null;
           String matchAlgorithm = "exactMatch";
           boolean ranking = false;
           int maxToReturn = -1;
	   ResolvedConceptReferencesIteratorWrapper wrapper = new CodeSearchUtils(lexBIGService).searchByCode(
						schemes, versions, matchText,
						source, matchAlgorithm, ranking, maxToReturn, false);
	   ResolvedConceptReferencesIterator iterator = wrapper.getIterator();
	   request.getSession().setAttribute("concepts_in_namespaces", iterator);
 	   jsfPage = "/pages/select_concept_in_namespace.jsf";	
 	   multiple_namespaces = true;
       }
   }
   String match_text = (String) request.getSession().getAttribute("matchText");
   boolean bool_val = LicenseUtils.isLicensedAndNotAccepted(request, dictionary, null);
   if (LicenseUtils.isLicensedAndNotAccepted(request, dictionary, null)) {
       jsfPage = "/pages/accept_license.jsf";
   } 
   String forwardPage = jsfPage + "?&dictionary=" + dictionary + version_parameter;
   if (!multiple_namespaces) {
       forwardPage = forwardPage + ns_parameter;
   }
   forwardPage = forwardPage + "&code=" + code;
   System.out.println("forwardPage: " + forwardPage);
   %>
   <jsp:forward page="<%=forwardPage%>" />
   <% 
} 
%>
  
   
 </body>
</html>