<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ page import="gov.nih.nci.evs.browser.utils.DataUtils"%>

<%
String home = request.getParameter("home");
String context_path = request.getContextPath();

String redirect_url = null;
if (home != null) {
     home = home.trim();
     home = home.replaceAll("%20", " ");
     String home_formal_name = DataUtils.getFormalName(home); 
     if (home_formal_name == null) {
         request.getSession().setAttribute("message", "Vocabulary " + home + " is not supported.");
         redirect_url = context_path + "/pages/message.jsf";
     } else {
        if (home_formal_name.compareTo("NCI Thesaurus") == 0) {
            redirect_url = context_path + "/index.jsf";
        } else if (home_formal_name.compareTo("NCI Metathesaurus") == 0) {
            request.getSession().setAttribute("redirect_ncim_message", "Plesase click on the NCI Metathesaurus hyperlink below to access NCI Metathesaurus."); 
            redirect_url = context_path + "/start.jsf";            
            
        } else {
        	home = home.replaceAll(" ", "%20");
		redirect_url = context_path + "/pages/vocabulary.jsf?dictionary=" + home;
	}
     }
} else {
        redirect_url = context_path + "/pages/multiple_search.jsf";
}

String url = response.encodeRedirectURL(redirect_url);
response.sendRedirect(url);

%>     