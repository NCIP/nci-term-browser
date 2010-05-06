<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ page import="gov.nih.nci.evs.browser.utils.HTTPUtils" %>

<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="java.util.Vector"%>
<%@ page import="org.LexGrid.concepts.Concept" %>
<%@ page import="gov.nih.nci.evs.browser.utils.DataUtils" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
  <title>NCI Term Browser</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />
  <link rel="shortcut icon" href="<%= request.getContextPath() %>/favicon.ico" type="image/x-icon" />
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/script.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/search.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/dropdown.js"></script>
</head>
<%
  String scheme = (String) request.getAttribute("scheme");
  String version = (String) request.getAttribute("version");
  String ontology_list_str = (String) request.getAttribute("ontology_list_str");
  String matchText = (String) request.getSession().getAttribute("matchText");
  
    if (matchText == null) matchText = "";
    String license_page__match_text = HTTPUtils.convertJSPString(matchText); 
  
  
  String searchTarget = (String) request.getAttribute("searchTarget");
  String matchAlgorithm = (String) request.getAttribute("algorithm");
  String licenseStmt = LicenseBean.resolveCodingSchemeCopyright(scheme, version);

  if (scheme != null) scheme = scheme.replaceAll("%20", " ");
%>
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<f:view>
  <%@ include file="/pages/templates/header.jsp" %>
  <div class="center-page">
    <%@ include file="/pages/templates/sub-header.jsp" %>
    <!-- Main box -->
    <div id="main-area">
      <!-- Thesaurus, banner search area -->
      <div class="bannerarea">
        <div class="banner"><a href="<%=basePath%>/start.jsf"><img src="<%=basePath%>/images/evs_termsbrowser_logo.gif" width="383" height="97" border="0"/></a></div>
      </div>
      <!-- end Thesaurus, banner search area -->
      <!-- Quick links bar -->
      <%@ include file="/pages/templates/quickLink.jsp" %>
      <!-- end Quick links bar -->
      <!-- Page content -->
      <div class="pagecontent">
        <p>
        <%
          String display_name = DataUtils.getMetadataValue(scheme, "display_name");
        %>  
          To access <b><%=display_name%></b>, please review and accept the copyright/license statement below:
        </p>
        <textarea cols="87" rows="15" readonly align="left"><%=licenseStmt%></textarea>
        <p>
          If and only if you agree to these terms and conditions, click the Accept button to proceed.
        </p>
        <%
          String scheme0 = scheme;
          String version0 = version;
          if (scheme != null) {
            scheme = scheme.replaceAll(" ", "%20");
          }
          if (version != null) {
            version = version.replaceAll(" ", "%20");
          }
         %>
          <form>
            <h:commandButton
              id="search"
              value="Search"
              action="#{userSessionBean.multipleSearchAction}"
              image="#{facesContext.externalContext.requestContextPath}/images/accept.gif"
              alt="Search">
            </h:commandButton>
            &nbsp;&nbsp;
            <a href="<%= request.getContextPath() %>/start.jsf">
              <img src="<%= request.getContextPath() %>/images/cancel.gif" border="0" alt="Cancel"/>
            </a>

            <input type="hidden" id="matchText" name="matchText" value="<%=license_page__match_text%>" />
            <input type="hidden" id="algorithm" name="algorithm" value="<%=matchAlgorithm%>" />
            <input type="hidden" id="ontology_list_str" name="ontology_list_str" value="<%=ontology_list_str%>" />
            <input type="hidden" id="scheme" name="scheme" value="<%=scheme0%>" />
            <input type="hidden" id="version" name="version" value="<%=version0%>" />
            <input type="hidden" id="searchTarget" name="searchTarget" value="<%=searchTarget%>" />
           
          </form>
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