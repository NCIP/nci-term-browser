<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.Vector"%>
<%@ page import="java.util.HashSet"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Set"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="gov.nih.nci.evs.browser.utils.DataUtils"%>
<%@ page import="gov.nih.nci.evs.browser.utils.HTTPUtils" %>
<%@ page import="gov.nih.nci.evs.browser.properties.PropertyFileParser"%>
<%@ page import="gov.nih.nci.evs.browser.properties.NCItBrowserProperties"%>
<%@ page import="gov.nih.nci.evs.browser.bean.DisplayItem"%>
<%@ page import="gov.nih.nci.evs.browser.bean.*"%>
<%@ page import="gov.nih.nci.evs.browser.utils.*"%>
<%@ page import="org.LexGrid.concepts.Entity"%>
<%@ page import="org.LexGrid.concepts.Presentation"%>
<%@ page import="org.LexGrid.commonTypes.Source"%>
<%@ page import="org.LexGrid.commonTypes.EntityDescription"%>
<%@ page import="org.LexGrid.commonTypes.Property"%>
<%@ page import="org.LexGrid.commonTypes.PropertyQualifier"%>
<%@ page import="org.LexGrid.concepts.Presentation"%>
<%@ page import="org.LexGrid.commonTypes.Source"%>
<%@ page import="org.LexGrid.commonTypes.EntityDescription"%>
<%@ page import="org.LexGrid.commonTypes.Property"%>
<%@ page import="org.LexGrid.commonTypes.PropertyQualifier"%>
<%@ page import="gov.nih.nci.evs.browser.common.Constants"%>
<%@ page import="org.LexGrid.LexBIG.Extensions.Generic.LexBIGServiceConvenienceMethods"%>
<%@ page import="org.LexGrid.LexBIG.Extensions.Generic.MappingExtension"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns:c="http://java.sun.com/jsp/jstl/core">
<head>
  <%
  String dictionary = null;
  String version = null, deprecatedVersion = null;
  dictionary = (String) request.getParameter("dictionary");
  version = (String) request.getParameter("version");

  if (dictionary != null) {
    dictionary = DataUtils.replaceAll(dictionary, "&#40;", "(");
    dictionary = DataUtils.replaceAll(dictionary, "&#41;", ")");
    dictionary = DataUtils.getCodingSchemeName(dictionary);
    request.getSession().setAttribute("dictionary", dictionary);
  } else {
    dictionary = (String) request.getSession().getAttribute("dictionary");
  }

  if (version == null) {
    version = (String) request.getAttribute("version");
  }

  //System.out.println("concept_details.jsp version: " + version);  
  request.setAttribute("version", version);

  if (dictionary.compareTo("NCI Thesaurus") == 0) {
  %>
    <title>NCI Thesaurus</title>
  <%
  } else {
  %>
    <title>NCI Term Browser</title>
  <%
  }
  %>

<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" type="text/css"
  href="<%=request.getContextPath()%>/css/styleSheet.css" />
<script type="text/javascript"
  src="<%=request.getContextPath()%>/js/script.js"></script>
<script type="text/javascript"
  src="<%=request.getContextPath()%>/js/search.js"></script>
<script type="text/javascript"
  src="<%=request.getContextPath()%>/js/dropdown.js"></script>
</head>
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<script type="text/javascript"
  src="<%=request.getContextPath()%>/js/wz_tooltip.js"></script>
<script type="text/javascript"
  src="<%=request.getContextPath()%>/js/tip_centerwindow.js"></script>
<script type="text/javascript"
  src="<%=request.getContextPath()%>/js/tip_followscroll.js"></script>

<f:view>
  <!-- Begin Skip Top Navigation -->
    <a href="#evs-content" class="hideLink" accesskey="1" title="Skip repetitive navigation links">skip navigation links</A>
  <!-- End Skip Top Navigation --> 
  <%@ include file="/pages/templates/header.jsp"%>
  <div class="center-page"><%@ include
    file="/pages/templates/sub-header.jsp"%> <!-- Main box -->
  <div id="main-area">
  <%
              String code = null;
              String type = null;
              String singleton = (String) request.getAttribute("singleton");

              if (singleton != null && singleton.compareTo("true") == 0) {
                if (dictionary != null
                    && dictionary
                        .compareTo(Constants.CODING_SCHEME_NAME) != 0) {
                  dictionary = DataUtils.getCodingSchemeName(dictionary);
                }
              }

              code = (String) request.getParameter("code");
              if (code == null) {
                Entity con = (Entity) request.getSession().getAttribute(
                    "concept");
                    
                if (con != null) {
                  code = con.getEntityCode();
                  request.getSession().setAttribute("code", code);

                } else {
                  code = (String) request.getSession().getAttribute("code");
                 
                }
                if (code == null) {
                  System.out
                      .println("WARNING: concept_details.jsp code: "
                          + code);
                }
              }

              String active_code = (String) request.getSession()
                  .getAttribute("active_code");
              if (active_code == null) {
                request.getSession().setAttribute("active_code", code);
              } else {
                if (active_code.compareTo(code) != 0) {
                  request.getSession().removeAttribute(
                      "RelationshipHashMap");
                  request.getSession().setAttribute("active_code", code);
                }
              }

              Boolean new_search = null;
              Object new_search_obj = request.getSession().getAttribute(
                  "new_search");
              if (new_search_obj != null) {
                new_search = (Boolean) new_search_obj;
                if (new_search.equals(Boolean.TRUE)) {
                  type = "properties";
                  request.getSession().setAttribute("new_search",
                      Boolean.FALSE);
                  code = (String) request.getSession().getAttribute(
                      "code");
                }
              }

              if (type == null) {
                type = (String) request.getParameter("type");

                if (type == null) {
                  type = "properties";
                } else if (type.compareTo("properties") != 0
                    && type.compareTo("relationship") != 0
                    && type.compareTo("synonym") != 0
                    && type.compareTo("mapping") != 0
                    && type.compareTo("all") != 0) {
                  type = "properties";
                }
              }

              String cd_dictionary = DataUtils.getFormalName(dictionary);
              String term_suggestion_application_url = DataUtils.getMetadataValue(cd_dictionary, "term_suggestion_application_url");

              String name = "";
              Entity c = null;

              String ltag = null;

              c = DataUtils.getConceptByCode(dictionary, version, ltag, code);
              if (c == null) {
                  deprecatedVersion = version;
                  version = DataUtils.getVocabularyVersionByTag(dictionary, "PRODUCTION");
                  c = DataUtils.getConceptByCode(dictionary, version, ltag, code);
              }
              
              if (c != null) {
                request.getSession().setAttribute("concept", c);
                request.getSession().setAttribute("code", code);
                name = c.getEntityDescription().getContent();
              } else {
                //name = "The server encountered an internal error that prevented it from fulfilling this request.";
                name = "ERROR: Invalid code - " + code + ".";
              }

              if (dictionary.compareTo("NCI Thesaurus") == 0
                  || dictionary.compareTo("NCI_Thesaurus") == 0) {
          %> <%@ include file="/pages/templates/content-header.jsp"%>
  <%
          } else {
              request.getSession().setAttribute("dictionary", dictionary);
        %> <%@ include file="/pages/templates/content-header-other.jsp"%>
  <%
          }
            String tg_dictionary_0 = dictionary;
            
            String tg_dictionary = DataUtils.replaceAll(dictionary, " ",
                "%20");
            if (c != null) {
              request.getSession().setAttribute("type", type);
              request.getSession().setAttribute("singleton", "false");
        %> <!-- Page content -->
  <div class="pagecontent">
  <a name="evs-content" id="evs-content"></a>
  <table border="0" width="700px">
    <tr>
      <td class="texttitle-blue"><%=HTTPUtils.cleanXSS(name)%> (Code <%=HTTPUtils.cleanXSS(code)%>)</td>

      <%
            Vector visitedConcepts = (Vector) request.getSession()
                    .getAttribute("visitedConcepts");
                if (visitedConcepts == null) {
                  visitedConcepts = new Vector();
                }

                String localCodingSchemeName = DataUtils
                    .getLocalName(tg_dictionary_0);
                   
                    
                String visitedConceptStr = localCodingSchemeName + "|"
                    + code + "|" + name;
                if (!visitedConcepts.contains(visitedConceptStr)) {
                  visitedConcepts.add(visitedConceptStr);
                  request.getSession().removeAttribute("visitedConcepts");
                  request.getSession().setAttribute("visitedConcepts",
                      visitedConcepts);
                }

                if (term_suggestion_application_url != null
                    && term_suggestion_application_url.compareTo("") != 0) {
          %>
                  <td align="right" valign="bottom" class="texttitle-blue-rightJust" nowrap>
                    <a href="<%=term_suggestion_application_url%>?dictionary=<%=HTTPUtils.cleanXSS(cd_dictionary)%>&code=<%=HTTPUtils.cleanXSS(code)%>"
                    target="_blank" alt="Term Suggestion">Suggest changes to this concept</a>
                  </td>
          <%
                }
          %>
                <!-- <td align="right" valign="bottom" class="textbodysmall" nowrap><%= version %></td> -->
    </tr>
    <% if (deprecatedVersion != null) { %>
      <tr>
        <td class="textbodysmall">
          <font color="#A90101">Warning:</font>
          Version <%=deprecatedVersion%> of this vocabulary is not accessible.
          Displaying version <%=version%> of this concept instead.
        </td>
      </tr>
    <% } %>
  </table>

  <hr>


  <%
  request.getSession().setAttribute("concept", c);
  request.getSession().setAttribute("code", code);
  request.setAttribute("version", version);    
            
      
%> <%@ include file="/pages/templates/typeLinks.jsp"%>
  <div class="tabTableContentContainer"><%@ include
    file="/pages/templates/property.jsp"%> <%@ include
    file="/pages/templates/relationship.jsp"%>
  <%@ include file="/pages/templates/synonym.jsp"%>
  <%@ include file="/pages/templates/mappings.jsp"%>
  </div>
  <%
            } else {
          %>
  <div class="textbody"><%=HTTPUtils.cleanXSS(name)%></div>
  <%
      }
    %> <%@ include file="/pages/templates/nciFooter.html"%>
  </div>
  </div>
  <!-- end Page content --></div>
  <div class="mainbox-bottom"><img
    src="<%=basePath%>/images/mainbox-bottom.gif" width="745" height="5"
    alt="Mainbox Bottom" /></div>
  <!-- end Main box -->
  </div>
  <br />
</f:view>
</body>
</html>