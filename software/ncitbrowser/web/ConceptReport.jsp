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

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
 <body>
   <%
   String dictionary =  HTTPUtils.cleanXSS((String) request.getParameter("dictionary"));
   
Boolean cs_available = DataUtils.isCodingSchemeAvailable(dictionary);
if (cs_available == null || !cs_available.equals(Boolean.TRUE)) {
    String error_msg = "WARNING: " + dictionary + Constants.CODING_SCHEME_NOT_AVAILABLE;
    request.getSession().setAttribute("error_msg", error_msg);
    String redirectURL = request.getContextPath() + "/pages/coding_scheme_unavailable.jsf";
    response.sendRedirect(redirectURL);
    
}  else { 
   
   String code =  HTTPUtils.cleanXSS((String) request.getParameter("code"));
   String version =  HTTPUtils.cleanXSS((String) request.getParameter("version"));
   String version_parameter = "";
   if (version != null && ! version.equalsIgnoreCase("null"))
       version_parameter = "&version=" + version;
   String match_text = (String) request.getSession().getAttribute("matchText");
  
   String jsfPage = "/pages/concept_details.jsf";
  
   boolean bool_val = LicenseUtils.isLicensedAndNotAccepted(request, dictionary, null);
         
   if (LicenseUtils.isLicensedAndNotAccepted(request, dictionary, null)) {
       jsfPage = "/pages/accept_license.jsf";
   }
   String forwardPage = jsfPage + "?&dictionary=" + dictionary + version_parameter + "&code=" + code;
   %>
   <jsp:forward page="<%=forwardPage%>" />
   
   <% 
} 
%>
  
   
 </body>
</html>