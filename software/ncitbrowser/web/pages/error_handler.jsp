<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="java.util.*"%>
<%@ page import="org.LexGrid.concepts.*" %>
<%@ page import="gov.nih.nci.evs.browser.utils.*" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns:c="http://java.sun.com/jsp/jstl/core">
<head>
  <title>NCI Term Browser</title>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />
  <link rel="shortcut icon" href="<%= request.getContextPath() %>/favicon.ico" type="image/x-icon" />
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/script.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/search.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/dropdown.js"></script>
</head>
<%
  // Determine which sub app the message or error came from in order to
  //   display the correct banner.
  JSPUtils.JSPHeaderInfoMore info = new JSPUtils.JSPHeaderInfoMore(request);
  String dictionary = info.dictionary;
  String display_name = null;
  final int TB=0, NCIT=1, NCIO=2;
  int subApp = TB;
  String version = info.version;
  
  if (dictionary != null) {
    display_name = info.display_name;
    version = info.term_browser_version;
    if (display_name == null) subApp = TB;
    else if (dictionary == "NCI Thesaurus" || dictionary == "NCI_Thesaurus")
      subApp = NCIT;
    else
      subApp = NCIO;
  }
%>
<body>
<!-- dictionary = <%=HTTPUtils.cleanXSS(dictionary)%> -->
<f:view>
  <!-- Begin Skip Top Navigation -->
    <a href="#evs-content" class="hideLink" accesskey="1" title="Skip repetitive navigation links">skip navigation links</A>
  <!-- End Skip Top Navigation --> 
  <%@ include file="/pages/templates/header.jsp" %>
  <div class="center-page_960">
    <%@ include file="/pages/templates/sub-header.jsp" %>
    <!-- Main box -->
    <div id="main-area_960">
      <div class="bannerarea_960">
        <% if (subApp == TB) {%>
		  <a href="<%=basePath%>/start.jsf" style="text-decoration: none;">
		    <div class="vocabularynamebanner_tb">
		      <span class="vocabularynamelong_tb"><%=JSPUtils.getApplicationVersionDisplay()%></span>
		    </div>
		  </a>
        <% } else if (subApp == NCIO) { %>
          <a class="vocabularynamebanner" href="<%=request.getContextPath()%>/pages/vocabulary.jsf?dictionary=<%=HTTPUtils.cleanXSS(dictionary)%>">
            <div class="vocabularynamebanner">
              <div class="vocabularynameshort" STYLE="font-size: <%=HTTPUtils.maxFontSize(dictionary)%>px; font-family : Arial">
                  <%=HTTPUtils.cleanXSS(dictionary)%>
              </div>

<%              
String error_handler_dictionary = HTTPUtils.cleanXSS(dictionary);
String error_handler_version = HTTPUtils.cleanXSS(version);

String release_date = DataUtils.getVersionReleaseDate(error_handler_dictionary, error_handler_version);
boolean display_release_date = true;
if (release_date == null || release_date.compareTo("") == 0) {
    display_release_date = false;
}
if (display_release_date) {
%>
    <div class="vocabularynamelong">Version: <%=error_handler_version%> (Release date: <%=release_date%>)</div>
<%
} else {
%>
    <div class="vocabularynamelong">Version: <%=error_handler_version%></div>
<%
}
%>   
               
            </div>
          </a>
        <% } else { %>
          <div class="banner"><a href="<%=basePath%>"><img src="<%=basePath%>/images/thesaurus_browser_logo.jpg" width="383" height="117" alt="Thesaurus Browser Logo" border="0"/></a></div>
        <% } %>
      </div> <!-- end bannerarea_960 -->
      <!-- end Thesaurus, banner search area -->
      <!-- Quick links bar -->
      <%@ include file="/pages/templates/quickLink.jsp" %>
      <!-- end Quick links bar -->
      <!-- Page content -->
      <div class="pagecontent">
        <a name="evs-content" id="evs-content"></a>
        <%
          String message = (String) request.getSession().getAttribute(Constants.ERROR_MESSAGE);
        %>
        <b><%=HTTPUtils.cleanXSS(message)%></b>
        <%@ include file="/pages/templates/nciFooter.jsp" %>
      </div>
      <!-- end Page content -->
    </div>
    <div class="mainbox-bottom"><img src="<%=basePath%>/images/mainbox-bottom.gif" width="945" height="5" alt="Mainbox Bottom" /></div>
    <!-- end Main box -->
  </div>
</f:view>
</body>
</html>

