<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ page import="gov.nih.nci.evs.browser.utils.DataUtils"%>

<%
String home = request.getParameter("home");
String context_path = request.getContextPath();

String redirect_url = null;
if (home != null) {
     home = home.trim();
     String home_formal_name = DataUtils.getFormalName(home); 
     if (home_formal_name == null) {
         request.getSession().setAttribute("message", "Vocabulary " + home + " is not supported.");
         redirect_url = context_path + "//pages//message.jsf";
     } else {

	redirect_url = context_path + "//pages//vocabulary.jsf?dictionary=" + home;
     }
} else {
        redirect_url = context_path + "//pages//multiple_search.jsf";
}

String url = response.encodeRedirectURL(redirect_url);
response.sendRedirect(url);

%>     