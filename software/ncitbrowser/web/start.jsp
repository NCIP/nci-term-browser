<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<%
String home = request.getParameter("home");
String context_path = request.getContextPath();

String redirect_url = null;
if (home != null) {
	redirect_url = context_path + "//pages//vocabulary.jsf?dictionary=" + home;
} else {
        redirect_url = context_path + "//pages//multiple_search.jsf";
}

String url = response.encodeRedirectURL(redirect_url);
response.sendRedirect(url);

%>     