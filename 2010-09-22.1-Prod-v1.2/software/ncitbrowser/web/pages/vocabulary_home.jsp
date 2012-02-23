<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="java.util.Vector"%>
<%@ page import="java.util.List"%>
<%@ page import="org.LexGrid.concepts.Concept" %>
<%@ page import="gov.nih.nci.evs.browser.utils.DataUtils" %>
<%@ page import="gov.nih.nci.evs.browser.utils.HTTPUtils" %>
<%@ page import="gov.nih.nci.evs.browser.utils.MetadataUtils" %>
<%@ page import="gov.nih.nci.evs.browser.properties.NCItBrowserProperties" %>
<%@ page import="gov.nih.nci.evs.browser.bean.MetadataElement" %>
<%
  String ncit_build_info = new DataUtils().getNCITBuildInfo();
  String application_version = new DataUtils().getApplicationVersion();
  String anthill_build_tag_built = new DataUtils().getNCITAnthillBuildTagBuilt();
  String evs_service_url = new DataUtils().getEVSServiceURL();
%>
<!--
   Build info: <%=ncit_build_info%>
 Version info: <%=application_version%>
          Tag: <%=anthill_build_tag_built%>
   LexEVS URL: <%=evs_service_url%>                             
  -->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
  <title>NCI Term Browser</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/script.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/search.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/dropdown.js"></script>
</head>
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<f:view>
  <%
    String menubar_scheme = null;
    String menubar_scheme0 = null;
    String menubar_version = null;
    String download_site = null;
    String voc_description = null;
    String voc_version = null;
    Vector v = null;
    Vector metadata_names = new Vector();
    List metadataElementList = NCItBrowserProperties
        .getMetadataElementList();
    for (int i = 0; i < metadataElementList.size(); i++) {
      MetadataElement ele = (MetadataElement) metadataElementList
      .get(i);
      metadata_names.add(ele.getName());
    }
  %>
  <%@ include file="/pages/templates/header.jsp" %>
  <div class="center-page">
    <%@ include file="/pages/templates/sub-header.jsp" %>
    <!-- Main box -->
    <div id="main-area">
  <%
    String dictionary = (String) request.getParameter("dictionary");
    
    String scheme = (String) request.getParameter("scheme");
    String shortName = "Vocabulary";
    if (scheme == null) {
      scheme = (String) request.getAttribute("scheme");
    } else {
      shortName = DataUtils.getMetadataValue(scheme, "display_name");
      if (shortName == null) shortName = "Vocabulary";
    }
    String version = (String) request.getParameter("version");
    if (version == null) {
      version = (String) request.getAttribute("version");
    }
    String term_browser_version = DataUtils.getMetadataValue(scheme, "term_browser_version");
    if (term_browser_version == null) term_browser_version = "N/A";

    if (dictionary != null && scheme == null) {
      scheme = dictionary;
      if (version != null) {
        dictionary = dictionary + " (version" + version + ")";
        version = version.replaceAll("%20", " ");
      }
    }
    if (dictionary != null)
      dictionary = dictionary.replaceAll("%20", " ");
    if (scheme != null)
      scheme = scheme.replaceAll("%20", " ");
    menubar_scheme = scheme;
    menubar_version = version;
    menubar_scheme0 = menubar_scheme;

    if (scheme != null) {
      scheme = scheme.replaceAll("%20", " ");
      request.setAttribute("scheme", scheme);
    }
    if (version != null) {
      version = version.replaceAll("%20", " ");
      request.setAttribute("version", version);
    }
       
    
    if (dictionary != null) {
        dictionary = DataUtils.getFormalName(dictionary);    
        dictionary = dictionary.replaceAll("%20", " ");
        request.setAttribute("dictionary", dictionary);
    }
  %>
  <!-- Thesaurus, banner search area -->
  <div class="bannerarea">
    <% if (menubar_version == null) { %>
      <a class="vocabularynamebanner" href="<%=request.getContextPath()%>/pages/vocabulary_home.jsf?dictionary=<%=HTTPUtils.cleanXSS(dictionary)%>&scheme=<%=HTTPUtils.cleanXSS(menubar_scheme)%>">
    <% } else { %>
      <a class="vocabularynamebanner" href="<%=request.getContextPath()%>/pages/vocabulary_home.jsf?dictionary=<%=HTTPUtils.cleanXSS(dictionary)%>&scheme=<%=HTTPUtils.cleanXSS(menubar_scheme)%>&version=<%=HTTPUtils.cleanXSS(menubar_version)%>">
    <% } %>
        <div class="vocabularynamebanner">
          <div class="vocabularynameshort"><%=HTTPUtils.cleanXSS(shortName)%></div>
          <div class="vocabularynamelong">Version: <%=HTTPUtils.cleanXSS(term_browser_version)%></div>
        </div>
      </a>
      <div class="search-globalnav">
      <!-- Search box -->
      <div class="searchbox-top"><img src="<%=basePath%>/images/searchbox-top.gif" width="352" height="2" alt="SearchBox Top" /></div>
      <div class="searchbox"><%@ include file="/pages/templates/searchForm.jsp" %></div>
      <div class="searchbox-bottom"><img src="<%=basePath%>/images/searchbox-bottom.gif" width="352" height="2" alt="SearchBox Bottom" /></div>
      <!-- end Search box -->
      <!-- Global Navigation -->
    <%
      v = MetadataUtils.getMetadataNameValuePairs(scheme, version, null);
      Vector u1 = MetadataUtils.getMetadataValues(v, "description");
      voc_description = scheme;
      if (u1 != null && u1.size() > 0) {
        voc_description = (String) u1.elementAt(0);
        if (voc_description == null
        || voc_description.compareTo("") == 0
        || voc_description.compareTo("null") == 0) {
          voc_description = "";
        }
      }
      Vector u2 = MetadataUtils.getMetadataValues(v, "version");
      voc_version = version;
      if (u2 != null && u2.size() > 0) {
        voc_version = (String) u2.elementAt(0);
      }
      Vector u3 = MetadataUtils.getMetadataValues(v, "download_url");
      if (u3 != null && u3.size() > 0) {
        download_site = (String) u3.elementAt(0);
      }
      if (menubar_scheme != null) {
        menubar_scheme = menubar_scheme.replaceAll(" ", "%20");
      }
      if (menubar_version != null) {
        menubar_version = menubar_version.replaceAll(" ", "%20");
      }
    %>
    <table class="global-nav" border="0" width="100%" cellpadding="0" cellspacing="0">
      <tr>
        <td>
          <% if (menubar_version == null) { %>
          <a href="<%= request.getContextPath() %>/pages/vocabulary_home.jsf?dictionary=<%=HTTPUtils.cleanXSS(dictionary)%>&scheme=<%=HTTPUtils.cleanXSS(menubar_scheme)%>">Home</a>
          <% } else { %>
          <a href="<%= request.getContextPath() %>/pages/vocabulary_home.jsf?dictionary=<%=HTTPUtils.cleanXSS(dictionary)%>&scheme=<%=HTTPUtils.cleanXSS(menubar_scheme)%>&version=<%=HTTPUtils.cleanXSS(menubar_version)%>">Home</a>
          <% }
          if (download_site != null) {
           %>
          | <a href="#" onclick="javascript:window.open('<%=download_site%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
              Download
            </a>
          <% } %>
          | <a href="#" onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(menubar_scheme)%>&version=<%=HTTPUtils.cleanXSS(menubar_version)%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
              View Hierarchy
            </a>
          <% if (menubar_scheme0.compareTo("NCI Thesaurus") == 0) { %>
          | <a href="<%= request.getContextPath() %>/pages/subset.jsf">Subsets</a>
          <% } %>
          | <a href="<%= request.getContextPath() %>/pages/help.jsf">Help</a>
        </td>
        <td align="right">
          <%
             Vector visitedConcepts = (Vector) request.getSession().getAttribute("visitedConcepts");
             if (visitedConcepts != null && visitedConcepts.size() > 0) {
               String visitedConceptsStr = DataUtils.getVisitedConceptLink(visitedConcepts);
          %>
               <%=visitedConceptsStr%>
          <% } %>
        </td>
        <td width="7"></td>
      </tr>
    </table>
    <!-- end Global Navigation -->
  </div>
</div>
<!-- end Thesaurus, banner search area -->

<!-- Quick links bar -->
<%@ include file="/pages/templates/quickLink.jsp" %>
<!-- end Quick links bar -->
<div class="pagecontent">
      <%@ include file="/pages/templates/welcome-other.jsp" %>
      <%@ include file="/pages/templates/nciFooter.html" %>
      <!-- end Page content -->
    </div>
    <!-- end Main box -->
  </div>
  <div class="mainbox-bottom"><img src="<%=basePath%>/images/mainbox-bottom.gif" width="745" height="5" alt="Mainbox Bottom" /></div>
</f:view>
<br/>
</body>
</html>