<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="gov.nih.nci.evs.browser.utils.*" %>

<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="java.util.Vector"%>
<%@ page import="org.LexGrid.concepts.Entity" %>
<%@ page import="gov.nih.nci.evs.browser.utils.DataUtils" %>
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
  LexEVSUtils.CSchemes unacceptedLicensesCS = 
    (LexEVSUtils.CSchemes) request.getAttribute("unacceptedLicensesCS");
  LicenseUtils.WebPageHelper helper = new LicenseUtils.WebPageHelper(unacceptedLicensesCS);
  
  String scheme = (String) request.getAttribute("scheme");
  String version = (String) request.getAttribute("version");
  String ontology_list_str = (String) request.getAttribute("ontology_list_str");
  String matchText = (String) request.getSession().getAttribute("matchText");
  if (matchText == null)
    matchText = "";
  String license_page__match_text = HTTPUtils.convertJSPString(matchText);
  String searchTarget = (String) request.getAttribute("searchTarget");
  String matchAlgorithm = (String) request.getAttribute("algorithm");
  
  
  String delimitedValues = "";
  boolean rendered = false;
  String fontColor = "class=\"textbodyred\"";
  if (unacceptedLicensesCS != null) {
      delimitedValues = unacceptedLicensesCS.getDelimitedValues();
      rendered = true;
      fontColor = "";
  } 
  
  if (scheme != null) scheme = scheme.replaceAll("%20", " ");
%>
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<f:view>
  <!-- Begin Skip Top Navigation -->
    <a href="#evs-content" class="hideLink" accesskey="1" title="Skip repetitive navigation links">skip navigation links</A>
  <!-- End Skip Top Navigation --> 
  <%@ include file="/pages/templates/header.jsp" %>
  <div class="center-page">
    <%@ include file="/pages/templates/sub-header.jsp" %>
    <!-- Main box -->
    <div id="main-area">
      <!-- Thesaurus, banner search area -->
      <div class="bannerarea">
	    <a href="<%=basePath%>/start.jsf" style="text-decoration: none;">
	      <div class="vocabularynamebanner_tb">
	        <span class="vocabularynamelong_tb"><%=JSPUtils.getApplicationVersionDisplay()%></span>
	      </div>
	    </a>
      </div>
      <!-- end Thesaurus, banner search area -->
      <!-- Quick links bar -->
      <%@ include file="/pages/templates/quickLink.jsp" %>
      <!-- end Quick links bar -->
      <!-- Page content -->
      <div class="pagecontent">
        <a name="evs-content" id="evs-content"></a>
        <p <%=fontColor%>><%= helper.getReviewAndAcceptMessage() %></p>
        <% if (rendered) { %>
          <textarea cols="87" rows="15" readonly align="left"><%= helper.getLicenseMessages(87) %></textarea>
        <% } %>
        <p><%= helper.getButtonMessage() %></p>
          <h:form>
            <% if (rendered) { %>
              <h:commandButton
                id="search"
                value="Search"
                action="#{userSessionBean.multipleSearchAction}"
                image="/images/accept.gif"
                alt="Search">
              </h:commandButton>
              &nbsp;&nbsp;
            <% } %>
            <a href="<%= request.getContextPath() %>/start.jsf">
              <img src="<%= request.getContextPath() %>/images/cancel.gif" border="0" alt="Cancel"/>
            </a>

            <input type="hidden" id="matchText" name="matchText" value="<%=license_page__match_text%>" />
            <input type="hidden" id="algorithm" name="algorithm" value="<%=matchAlgorithm%>" />
            <input type="hidden" id="ontology_list_str" name="ontology_list_str" value="<%=ontology_list_str%>" />
            <input type="hidden" id="scheme" name="scheme" value="<%=scheme%>" />
            <input type="hidden" id="version" name="version" value="<%=version%>" />            
            <input type="hidden" id="acceptedLicenses" name="acceptedLicenses" value="<%=delimitedValues%>" />            
            <input type="hidden" id="searchTarget" name="searchTarget" value="<%=searchTarget%>" />

          </h:form>
        <%@ include file="/pages/templates/nciFooter.jsp" %>
      </div>
      <!-- end Page content -->
    </div>
    <div class="mainbox-bottom"><img src="<%=basePath%>/images/mainbox-bottom.gif" width="745" height="5" alt="Mainbox Bottom" /></div>
    <!-- end Main box -->
  </div>
</f:view>
</body>
</html>