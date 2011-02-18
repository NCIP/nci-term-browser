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
<%@ page import="gov.nih.nci.evs.browser.utils.DataUtils" %>
<%@ page import="gov.nih.nci.evs.browser.utils.HTTPUtils" %>
<%@ page import="gov.nih.nci.evs.browser.properties.PropertyFileParser" %>
<%@ page import="gov.nih.nci.evs.browser.properties.NCItBrowserProperties" %>
<%@ page import="gov.nih.nci.evs.browser.bean.DisplayItem" %>
<%@ page import="gov.nih.nci.evs.browser.bean.*" %>
<%@ page import="gov.nih.nci.evs.browser.utils.*" %>
<%@ page import="org.LexGrid.concepts.Entity" %>
<%@ page import="org.LexGrid.concepts.Presentation" %>
<%@ page import="org.LexGrid.commonTypes.Source" %>
<%@ page import="org.LexGrid.commonTypes.EntityDescription" %>
<%@ page import="org.LexGrid.commonTypes.Property" %>
<%@ page import="org.LexGrid.commonTypes.PropertyQualifier" %>
<%@ page import="org.LexGrid.concepts.Presentation" %>
<%@ page import="org.LexGrid.commonTypes.Source" %>
<%@ page import="org.LexGrid.commonTypes.EntityDescription" %>
<%@ page import="org.LexGrid.commonTypes.Property" %>
<%@ page import="org.LexGrid.commonTypes.PropertyQualifier" %>
<%@ page import="gov.nih.nci.evs.browser.common.Constants" %>
<%@ page import="org.apache.log4j.*" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
  <title>NCI Term Browser</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/styleSheet.css" />
  <script type="text/javascript" src="<%=request.getContextPath()%>/js/script.js"></script>
  <script type="text/javascript" src="<%=request.getContextPath()%>/js/search.js"></script>
  <script type="text/javascript" src="<%=request.getContextPath()%>/js/dropdown.js"></script>
</head>
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
  <script type="text/javascript" src="<%=request.getContextPath()%>/js/wz_tooltip.js"></script>
  <script type="text/javascript" src="<%=request.getContextPath()%>/js/tip_centerwindow.js"></script>
  <script type="text/javascript" src="<%=request.getContextPath()%>/js/tip_followscroll.js"></script>

  <%!
    private static Logger _logger = Utils.getJspLogger("concept_details_other_term.jsp");
  %>
  <f:view>
    <!-- Begin Skip Top Navigation -->
      <a href="#evs-content" class="hideLink" accesskey="1" title="Skip repetitive navigation links">skip navigation links</A>
    <!-- End Skip Top Navigation -->   
    <%@ include file="/pages/templates/header.jsp" %>
    <div class="center-page">
      <%@ include file="/pages/templates/sub-header.jsp" %>
      <!-- Main box -->
      <div id="main-area">
       <%
        String dictionary = (String) request.getAttribute("dictionary");

          if (dictionary == null) {
            dictionary = (String) request.getParameter("dictionary");
            dictionary = DataUtils.getCodingSchemeName(dictionary);
          }

 boolean cdo_isMapping = DataUtils.isMapping(dictionary, null);
 boolean tree_access_allowed = true;
 if (DataUtils._vocabulariesWithoutTreeAccessHashSet.contains(hdr_dictionary0)) {
     tree_access_allowed = false;
 }

          String shortName = DataUtils.getLocalName(dictionary);
          String term_suggestion_application_url = new DataUtils()
              .getTermSuggestionURL();

       %>
          <!-- Thesaurus, banner search area -->
          <div class="bannerarea">

       <%
        if (dictionary != null
              && dictionary.compareTo("NCI Thesaurus") == 0) {
       %>
                  <div><img src="<%=basePath%>/images/thesaurus_popup_banner.gif" width="612" height="56" alt="NCI Thesaurus" title="" border="0" /></div>
       <%
        } else {
       %>


 <%
            String version = (String) request.getParameter("version");
                if (version == null) {
                  version = (String) request.getAttribute("version");
                }

                String term_browser_version = DataUtils.getMetadataValue(
                    dictionary, version, "term_browser_version");
                if (term_browser_version == null
                    || term_browser_version.compareTo("null") == 0)
                  term_browser_version = version;
                String display_name = DataUtils.getMetadataValue(
                    dictionary, version, "display_name");
                if (display_name == null
                    || display_name.compareTo("null") == 0)
                  display_name = shortName;
          %>
    <a class="vocabularynamebanner" href="<%=request.getContextPath()%>/pages/vocabulary.jsf?dictionary=<%=HTTPUtils.cleanXSS(dictionary)%>">
      <div class="vocabularynamebanner">
        <div class="vocabularynameshort" STYLE="font-size: <%=HTTPUtils.maxFontSize(display_name)%>px; font-family : Arial">
            <%=HTTPUtils.cleanXSS(display_name)%>
        </div>
        <div class="vocabularynamelong">Version: <%=HTTPUtils.cleanXSS(term_browser_version)%></div>
      </div>
    </a>
        <%
          }
        %>
          <div class="search-globalnav">
              <!-- Search box -->
              <div class="searchbox-top"><img src="<%=basePath%>/images/searchbox-top.gif" width="352" height="2" alt="SearchBox Top" /></div>
              <div class="searchbox"><%@ include file="/pages/templates/searchForm.jsp" %></div>
              <div class="searchbox-bottom"><img src="<%=basePath%>/images/searchbox-bottom.gif" width="352" height="2" alt="SearchBox Bottom" /></div>
              <!-- end Search box -->
              <!-- Global Navigation -->
              <table class="global-nav" border="0" width="100%" cellpadding="0" cellspacing="0">
                <tr>
                  <td>

                    <a href="<%=request.getContextPath()%>/pages/vocabulary.jsf?dictionary=<%=HTTPUtils.cleanXSS(dictionary)%>">Home</a>

      <%
      if (cdo_isMapping) {
      %>

      <a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/mapping.jsf?dictionary=<%=HTTPUtils.cleanXSS(dictionary)%>&version=<%=version%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      </a>

      <%
      } else if (tree_access_allowed) {
      %>

                    | <a href="#" onclick="javascript:window.open('<%=request.getContextPath()%>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(dictionary)%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
                    View Hierarchy </a>
      <%
      }
      %>


                    | <a href="<%=request.getContextPath()%>/pages/help.jsf">Help</a>
                  </td>



                  <td align="right">
                    <%= VisitedConceptUtils.getDisplayLink(request) %>
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
        <!-- Page content -->
        <div class="pagecontent">
          <a name="evs-content" id="evs-content"></a>
          <%
            String code = null;
              String type = null;

              String singleton = (String) request.getSession().getAttribute("singleton");
              if (singleton != null && singleton.compareTo("true") == 0) {
                code = (String) request.getSession().getAttribute("code");
              } else {
                code = (String) request.getSession().getAttribute("code");
                type = (String) request.getParameter("type");
              }
              if (dictionary == null) {
                dictionary = Constants.CODING_SCHEME_NAME;
              }
              if (type == null) {
                type = "properties";
              } else if (type.compareTo("properties") != 0
                  && type.compareTo("relationship") != 0
                  && type.compareTo("synonym") != 0
                  && type.compareTo("all") != 0) {
                type = "properties";
              }

              String name = "";
              Entity c = null;

              String vers = null;
              String ltag = null;

              c = DataUtils.getConceptByCode(dictionary, vers, ltag, code);

              if (c != null) {
                request.getSession().setAttribute("concept", c);
                request.getSession().setAttribute("code", code);
                name = c.getEntityDescription().getContent();
                //request.getSession().removeAttribute("RelationshipHashMap");
              } else {
                request.getSession().setAttribute("dictionary", dictionary);
                name = "ERROR: Invalid code - " + code + ".";
              }

              String tg_dictionary = DataUtils.replaceAll(dictionary, " ",
                  "%20");
              if (c != null) {
                request.getSession().setAttribute("dictionary", dictionary);
                request.getSession().setAttribute("type", type);
                request.getSession().setAttribute("singleton", "false");

                String active_code = (String) request.getSession()
                    .getAttribute("active_code");
                if (active_code == null) {
                  request.getSession().setAttribute("active_code", code);
                } else {
                  if (active_code.compareTo(code) != 0) {
                    request.getSession().removeAttribute(
                        "RelationshipHashMap");
                    request.getSession().setAttribute("active_code",
                        code);
                  }
                }
          %>
          <table border="0" width="700px">
            <tr>
              <td class="texttitle-blue"><%=HTTPUtils.cleanXSS(name)%> (Code <%=HTTPUtils.cleanXSS(code)%>)</td>
              <%
                    VisitedConceptUtils.add(request, dictionary, code, name);
                    if (term_suggestion_application_url != null
                        && term_suggestion_application_url.compareTo("") != 0) {
              %>
              <td align="right" valign="bottom" class="texttitle-blue-rightJust" nowrap>
                 <a href="<%=term_suggestion_application_url%>?dictionary=<%=HTTPUtils.cleanXSS(tg_dictionary)%>&code=<%=HTTPUtils.cleanXSS(code)%>" target="_blank" alt="Term Suggestion">Suggest changes to this concept</a>
              </td>
              <%
                }
              %>

            </tr>
          </table>
          <hr>
          <%@ include file="/pages/templates/typeLinks.jsp" %>
          <div class="tabTableContentContainer">
              <%@ include file="/pages/templates/property.jsp" %>
              <%@ include file="/pages/templates/relationship.jsp" %>
              <%@ include file="/pages/templates/synonym.jsp" %>
          </div>
              <%
                } else {
              %>
          <div class="textbody">
              <%=HTTPUtils.cleanXSS(name)%>
          </div>
           <%
            }
           %>
           <%@ include file="/pages/templates/nciFooter.html" %>
          </div>
        </div>
        <!-- end Page content -->
      </div>
      <div class="mainbox-bottom"><img src="<%=basePath%>/images/mainbox-bottom.gif" width="745" height="5" alt="Mainbox Bottom" /></div>
      <!-- end Main box -->
    </div>
  </f:view>
</body>
</html>