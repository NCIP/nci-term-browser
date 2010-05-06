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
<%@ page import="org.LexGrid.concepts.Concept" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
 <body>
   <%
   String dictionary = (String) request.getParameter("dictionary");
   String code = (String) request.getParameter("code");
   String match_text = (String) request.getSession().getAttribute("matchText");
   
   LicenseBean licenseBean = (LicenseBean) request.getSession().getAttribute("licenseBean");
   if (licenseBean == null) {
       licenseBean = new LicenseBean();
       request.getSession().setAttribute("licenseBean", licenseBean);
   }
  
   if (LicenseBean.isLicensed(dictionary, null) && !licenseBean.licenseAgreementAccepted(dictionary)) {
   %>  
       <jsp:forward page="/pages/accept_license.jsf?dictionary=<%=dictionary%>&code=<%=code%>" />
   <%
   } else {
   %> 
       <jsp:forward page="/pages/concept_details.jsf?dictionary=<%=dictionary%>&code=<%=code%>" />
   <%
   }
   %>   
 </body>
</html>