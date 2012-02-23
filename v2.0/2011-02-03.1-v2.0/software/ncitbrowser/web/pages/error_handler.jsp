<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="java.util.Vector"%>
<%@ page import="org.LexGrid.concepts.Entity" %>
<%@ page import="gov.nih.nci.evs.browser.utils.DataUtils" %>
<%@ page import="gov.nih.nci.evs.browser.utils.HTTPUtils" %>
<%@ page import="gov.nih.nci.evs.browser.common.Constants" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns:c="http://java.sun.com/jsp/jstl/core">
<head>
  <title>NCI Thesaurus</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />
  <link rel="shortcut icon" href="<%= request.getContextPath() %>/favicon.ico" type="image/x-icon" />
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/script.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/search.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/dropdown.js"></script>
</head>
<%
  // Determine which sub app the message or error came from in order to
  //   display the correct banner.

  String dictionary = (String) request.getSession().getAttribute("dictionary");
  String display_name = null;
  final int TB=0, NCIT=1, NCIO=2;
  int subApp = TB;
  String version = null;

  if (dictionary != null) {
    display_name = DataUtils.getFormalName(dictionary);
    version = DataUtils.getMetadataValue(display_name, "term_browser_version");
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
  <div class="center-page">
    <%@ include file="/pages/templates/sub-header.jsp" %>
    <!-- Main box -->
    <div id="main-area">
      <div class="bannerarea">
        <% if (subApp == TB) {%>
          <div class="banner"><a href="<%=basePath%>/start.jsf"><img src="<%=basePath%>/images/evs_termsbrowser_logo.gif" width="383" height="117" alt="NCI Term Browser" border="0"/></a></div>
        <% } else if (subApp == NCIO) { %>
          <a class="vocabularynamebanner" href="<%=request.getContextPath()%>/pages/vocabulary.jsf?dictionary=<%=HTTPUtils.cleanXSS(dictionary)%>">
            <div class="vocabularynamebanner">
              <div class="vocabularynameshort" STYLE="font-size: <%=HTTPUtils.maxFontSize(dictionary)%>px; font-family : Arial">
                  <%=HTTPUtils.cleanXSS(dictionary)%>
              </div>
               <div class="vocabularynamelong">Version: <%=HTTPUtils.cleanXSS(version)%></div>
            </div>
          </a>
        <% } else { %>
          <div class="banner"><a href="<%=basePath%>"><img src="<%=basePath%>/images/thesaurus_browser_logo.jpg" width="383" height="117" alt="Thesaurus Browser Logo" border="0"/></a></div>
        <% } %>
      </div> <!-- end bannerarea -->
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
        <%@ include file="/pages/templates/nciFooter.html" %>
      </div>
      <!-- end Page content -->
    </div>
    <div class="mainbox-bottom"><img src="<%=basePath%>/images/mainbox-bottom.gif" width="745" height="5" alt="Mainbox Bottom" /></div>
    <!-- end Main box -->
  </div>
</f:view>
</body>
</html>

