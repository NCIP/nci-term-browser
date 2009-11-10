<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="java.util.Vector"%>
<%@ page import="org.LexGrid.concepts.Concept" %>
<%@ page import="gov.nih.nci.evs.browser.utils.DataUtils" %>
<%@ page import="gov.nih.nci.evs.browser.common.Constants" %>
<%
  String ncit_build_info = new DataUtils().getNCITBuildInfo();
  String term_suggestion_application_url = new DataUtils().getTermSuggestionURL();
%>
<!-- Build info: <%=ncit_build_info%> -->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
  <title>NCI Thesaurus</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />
  <link rel="shortcut icon" href="<%= request.getContextPath() %>/favicon.ico" type="image/x-icon" />
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/script.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/search.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/dropdown.js"></script>
</head>
<body>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/wz_tooltip.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/tip_centerwindow.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/tip_followscroll.js"></script>


<%
    request.getSession().removeAttribute("dictionary");
%>


<f:view>
  <%@ include file="/pages/templates/header.jsp" %>
  <div class="center-page">
    <%@ include file="/pages/templates/sub-header.jsp" %>
    <!-- Main box -->
    <div id="main-area">
      <form name="searchTerm" method="post" class="search-form-main-area">

        <input type="hidden" name="initial_search" value="true" />

        <%@ include file="/pages/templates/content-header-termbrowser.jsp" %>
        <!-- Page content -->
        <div class="pagecontent">
          <div class="tabTableContentContainer">
                 <%
                 String warning_msg = (String) request.getSession().getAttribute("warning");
                 if (warning_msg != null) {
                 %>
                    <p class="textbodyred">&nbsp;<%=warning_msg%></p>
                 <%
                 }
                 request.getSession().removeAttribute("warning");
                 %>
<%
            String hide_ontology_list = (String) request.getSession().getAttribute("hide_ontology_list");
            request.getSession().removeAttribute("hide_ontology_list");
            if (hide_ontology_list == null || hide_ontology_list.compareTo("false") == 0) {
 %>
            <p class="textbody">&nbsp;Select NCI hosted terminologies to search, or click on a source name to go to its browser home page.</p>
            <table class="termstable" border="0">
              <tr>
                <%
                  List ontology_list = DataUtils.getOntologyList();
                  if (ontology_list == null)
                    System.out.println("??????????? ontology_list == null");
                  int num_vocabularies = ontology_list.size();
                  String ontologiesToSearchOn = (String) request.getSession().getAttribute("ontologiesToSearchOn");

                %>
                  <td class="textbody">
                    <table border="0" cellpadding="0" cellspacing="0">
                      <%
                      for (int i = 0; i < ontology_list.size(); i++) {
                        SelectItem item = (SelectItem) ontology_list.get(i);
                        String value = (String) item.getValue();
                        String label = (String) item.getLabel();
                        String label2 = "|" + label + "|";

                        String scheme = DataUtils.key2CodingSchemeName(value);
                        String version = DataUtils.key2CodingSchemeVersion(value);
                        String http_label = null;
                        String http_scheme = null;
                        String http_version = null;

                        if (label != null)
                          http_label = label.replaceAll(" ", "%20");
                        if (scheme != null)
                          http_scheme = scheme.replaceAll(" ", "%20");
                        if (version != null)
                          http_version = version.replaceAll(" ", "%20");
                        %>
                        <tr>
                          <td width="25px"></td>
                          <td>
                        <%
                        if (scheme.compareTo("NCI Thesaurus") == 0) {
                           if (ontologiesToSearchOn != null
                              && ontologiesToSearchOn.indexOf(label2) != -1) {
                           %>
                              <input type="checkbox" name="ontology_list" value="<%=label%>" checked />
                           <%
                           } else {
                            %>
                              <input type="checkbox" name="ontology_list" value="<%=label%>" /> <%
                           }
                           %>
                              <a href="<%= request.getContextPath() %>"><%=label%></a>
                           <%
                        } else if (scheme.compareToIgnoreCase("NCI Metathesaurus") == 0) {
                              String ncimurl = NCItBrowserProperties.getNCIM_URL();
                            if (ontologiesToSearchOn != null
                               && ontologiesToSearchOn.indexOf(label2) != -1) {
                           %>
                              <input type="checkbox" name="ontology_list"
                                      value="<%=label%>" checked />
                           <%
                            } else {
                            %>
                              <input type="checkbox" name="ontology_list" value="<%=label%>" />
                           <%
                            }
                            %>
                              <a href="<%=ncimurl%>" target="_blank"><%=label%>
                                <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=label%>" />
                              </a>
                           <%
                        } else {
                           if (ontologiesToSearchOn != null
                              && ontologiesToSearchOn.indexOf(label2) != -1) {
                            %>
                              <input type="checkbox" name="ontology_list" value="<%=label%>" checked />
                           <%
                           } else {
                            %>
                              <input type="checkbox" name="ontology_list" value="<%=label%>" />
                           <%
                           }
                            %>
                              <a href="<%= request.getContextPath() %>/pages/vocabulary.jsf?dictionary=<%=http_scheme%>&version=<%=http_version%>">
                                <%=label%>
                              </a>
                           <%
                        }
                      %>                          
                        </td>
                      </tr>
                     <%
                      }
                     %>
                    </table>
                  </td>
                </tr>
                <tr><td height="20"></td></tr>
                <tr>
                  <td><img
                    src="<%= request.getContextPath() %>/images/selectAll.gif"
                    name="selectAll" alt="selectAll"
                    onClick="checkAll(document.searchTerm.ontology_list)" />
                  &nbsp;&nbsp; <img
                    src="<%= request.getContextPath() %>/images/clear.gif"
                    name="reset" alt="reset"
                    onClick="uncheckAll(document.searchTerm.ontology_list)" />
                  &nbsp;&nbsp; <h:commandButton id="search" value="Search"
                    action="#{userSessionBean.multipleSearchAction}"
                    image="#{facesContext.externalContext.requestContextPath}/images/search.gif"
                    alt="Search">
                  </h:commandButton></td>
                </tr>
            </table>
<%
}
%>
          </div> <!-- end tabTableContentContainer -->
          <%@ include file="/pages/templates/nciFooter.html"%>
        </div> <!-- end Page content -->
      </form>
    </div> <!-- end main-area -->
    <div class="mainbox-bottom"><img src="<%=basePath%>/images/mainbox-bottom.gif" width="745" height="5" alt="Mainbox Bottom" /></div>
    <!-- end Main box -->
  </div>
</f:view>

<%
    request.getSession().removeAttribute("dictionary");

%>

</body>
</html>