<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="java.util.Vector"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="org.LexGrid.concepts.Concept" %>
<%@ page import="gov.nih.nci.evs.browser.common.Constants" %>
<%@ page import="gov.nih.nci.evs.browser.utils.HTTPUtils" %>

<%@ page import="gov.nih.nci.evs.browser.utils.DataUtils" %>
<%@ page import="gov.nih.nci.evs.browser.bean.IteratorBean" %>
<%@ page import="javax.faces.context.FacesContext" %>
<%@ page import="org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
  <title>NCI Thesaurus</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/script.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/search.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/dropdown.js"></script>

</head>
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/wz_tooltip.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/tip_centerwindow.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/tip_followscroll.js"></script>

<f:view>

  <%@ include file="/pages/templates/header.xhtml" %>
  <div class="center-page">
    <%@ include file="/pages/templates/sub-header.xhtml" %>
    <!-- Main box -->
    <div id="main-area">
      <form name="searchTerm" method="post" class="search-form-main-area">
          <%@ include file="/pages/templates/content-header-termbrowser.xhtml" %>
      </form>
      <!-- Page content -->
      <div class="pagecontent">
        <%
          HashMap hmap = DataUtils.getNamespaceId2CodingSchemeFormalNameMapping();

          IteratorBean iteratorBean = (IteratorBean) FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap().get("iteratorBean");

          String matchText = HTTPUtils.cleanXSS((String) request.getSession().getAttribute("matchText"));
          String match_size = HTTPUtils.cleanXSS((String) request.getSession().getAttribute("match_size"));
          String page_string = HTTPUtils.cleanXSS((String) request.getSession().getAttribute("page_string"));
          Boolean new_search = (Boolean) request.getSession().getAttribute("new_search");
          String page_number = HTTPUtils.cleanXSS((String) request.getParameter("page_number"));
          String selectedResultsPerPage = HTTPUtils.cleanXSS((String) request.getSession().getAttribute("selectedResultsPerPage"));
          String contains_warning_msg = HTTPUtils.cleanXSS((String) request.getSession().getAttribute("contains_warning_msg"));

          if (page_number != null && new_search == Boolean.FALSE)
          {
              page_string = page_number;
          }
          request.getSession().setAttribute("new_search", Boolean.FALSE);
          int page_num = Integer.parseInt(page_string);
          int next_page_num = page_num + 1;
          int prev_page_num = page_num - 1;
          int page_size = 50;
          if (selectedResultsPerPage != null)
          {
              page_size = Integer.parseInt(selectedResultsPerPage);
          }
          int iend = page_num * page_size;
          int istart = iend - page_size;
          int size = iteratorBean.getSize();

          if (iend > size) iend = size;
          int num_pages = size / page_size;
          if (num_pages * page_size < size) num_pages++;
          String istart_str = Integer.toString(istart+1);
          String iend_str = Integer.toString(iend);
          String prev_page_num_str = Integer.toString(prev_page_num);
          String next_page_num_str = Integer.toString(next_page_num);
        %>
        <table width="700px">
          <tr>
            <table>
              <tr>
                <td class="texttitle-blue">Result for:</td>
                <td class="texttitle-gray"><%=matchText%></td>
              </tr>
            </table>
          </tr>
          <tr>
            <td><hr></td>
          </tr>
          <tr>
            <td>
            <%
              if (contains_warning_msg != null) {
             %> 
              <b>Results <%=istart_str%>-<%=iend_str%> of&nbsp;<%=match_size%> for: <%=matchText%></b>&nbsp;<%=contains_warning_msg%>
             <% 
              } else {
              %>
              <b>Results <%=istart_str%>-<%=iend_str%> of&nbsp;<%=match_size%> for: <%=matchText%></b>
              <%
              }
              %>

<%              
String ontologiesToSearchOnStr = (String) request.getSession().getAttribute("ontologiesToSearchOn");
String tooltip_str = "";
if (ontologiesToSearchOnStr != null) {
	Vector ontologies_to_search_on = DataUtils.parseData(ontologiesToSearchOnStr);
	for (int k=0; k<ontologies_to_search_on.size(); k++) {
		String s = (String) ontologies_to_search_on.elementAt(k);
		tooltip_str = tooltip_str + s + "; ";
	}
}
%>
from <a onmouseover="Tip('<%=tooltip_str%>')" onmouseout="UnTip()">selected vocabularies</a>. 
            </td>
          </tr>
          <tr>
            <td class="textbody">
              <table class="dataTable" summary="" cellpadding="3" cellspacing="0" border="0" width="100%">

          <th class="dataTableHeader" scope="col" align="left">Concept</th>
          <th class="dataTableHeader" scope="col" align="left">Vocabulary</th>

                <%
                  List list = iteratorBean.getData(istart, iend);
                  for (int i=0; i<list.size(); i++) {
                      ResolvedConceptReference rcr = (ResolvedConceptReference) list.get(i);

                      String code = rcr.getConceptCode();
                      String name = rcr.getEntityDescription().getContent();

                      String vocabulary_name = (String) hmap.get(rcr.getCodingSchemeName());

                      String vocabulary_name_encoded = null;
                      if (vocabulary_name != null) vocabulary_name_encoded = vocabulary_name.replace(" ", "%20");

                      if (i % 2 == 0) {
                        %>
                          <tr class="dataRowDark">
                        <%
                      } else {
                        %>
                          <tr class="dataRowLight">
                        <%
                      }
                      %>
                          <td class="dataCellText">
                          <%
                          if (vocabulary_name.compareToIgnoreCase("NCI Thesaurus") == 0) {
                          %>
                               <a href="<%=request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=vocabulary_name_encoded%>&code=<%=code%>" ><%=name%></a>
                          <%
                          } else if (vocabulary_name.compareToIgnoreCase("NCI MetaThesaurus") == 0) {
                               String meta_url = "http://ncim.nci.nih.gov/ncimbrowser/ConceptReport.jsp?dictionary=NCI%20MetaThesaurus&code=" + code;
                          %>
                               <a href="javascript:openQuickLinkSite('<%=meta_url%>')"><%=name%></a>
                               <!--
                               &nbsp;<img src='<%=basePath%>/images/newWindow.gif'/>
                               -->

                          <%
                          } else {
                          %>
                               <a href="<%=request.getContextPath() %>/pages/concept_details2.jsf?dictionary=<%=vocabulary_name_encoded%>&code=<%=code%>" ><%=name%></a>
                          <%
                          }
                          %>
                          </td>
                          <td class="dataCellText">
                            <%=vocabulary_name%>
                          </td>

                        </tr>
                      <%
                  }
                %>
              </table>
            </td>
          </tr>
        </table>
        <%@ include file="/pages/templates/pagination.xhtml" %>
        <%@ include file="/pages/templates/nciFooter.html" %>
      </div>
      <!-- end Page content -->
    </div>
    <div class="mainbox-bottom"><img src="images/mainbox-bottom.gif" width="745" height="5" alt="Mainbox Bottom" /></div>
    <!-- end Main box -->
  </div>
</f:view>
</body>
</html>