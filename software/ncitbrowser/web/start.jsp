<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ page import="gov.nih.nci.evs.browser.utils.DataUtils"%>
<%@ page import="gov.nih.nci.evs.browser.properties.NCItBrowserProperties" %>
<%@ page import="gov.nih.nci.evs.browser.common.Constants" %>

<%
String home = request.getParameter("home");
String context_path = request.getContextPath();
String ncimurl = NCItBrowserProperties.getNCIM_URL();
String redirect_url = null;
if (home != null) {
     home = home.trim();
     home = home.replaceAll("%20", " ");
     String home_formal_name = DataUtils.getFormalName(home); 
     if (home_formal_name == null) {
         request.getSession().setAttribute("unsupported_vocabulary_message", "WARNING: Vocabulary " + home + " is not supported.");
         redirect_url = context_path + "/pages/multiple_search.jsf";
     } else {
        if (home_formal_name.compareTo(Constants.NCI_THESAURUS) == 0) {
            redirect_url = context_path + "/index.jsf";
        } else if (home_formal_name.compareTo(Constants.NCI_METATHESAURUS) == 0) {
            redirect_url = ncimurl;//context_path + "/start.jsf";            
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