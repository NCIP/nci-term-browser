<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.Vector"%>
<%@ page import="org.LexGrid.concepts.Entity" %>
<%@ page import="gov.nih.nci.evs.browser.common.Constants" %>
<%@ page import="gov.nih.nci.evs.browser.utils.*" %>
<%@ page import="org.apache.log4j.*" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns:c="http://java.sun.com/jsp/jstl/core">
<head>


  <title>NCI Thesaurus</title>
  
  
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/script.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/search.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/dropdown.js"></script>
</head>
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<%!
  private static Logger _logger = Utils.getJspLogger("single_search_results.jsp");
%>
<f:view>
  <!-- Begin Skip Top Navigation -->
    <a href="#evs-content" class="hideLink" accesskey="1" title="Skip repetitive navigation links">skip navigation links</A>
  <!-- End Skip Top Navigation --> 
  <%@ include file="/pages/templates/header.jsp" %>
  <div class="center-page_960">
    <%@ include file="/pages/templates/sub-header.jsp" %>
    <!-- Main box -->
    <div id="main-area_960">
      <%@ include file="/pages/templates/content-header.jsp" %>
      <!-- Page content -->
      <div class="pagecontent">
        <a name="evs-content" id="evs-content"></a>
        <%
          Vector v = (Vector) request.getAttribute("search_results");
    boolean bool_val;
    if (v != null) {
         _logger.debug("single search results: " + v.size());
    } else {
         _logger.warn("single search results: v is NULL???");
    }

          String matchText = HTTPUtils.cleanXSS((String) request.getSession().getAttribute("matchText"));
          String match_size = HTTPUtils.cleanXSS((String) request.getAttribute("match_size"));
          String page_string = HTTPUtils.cleanXSS((String) request.getAttribute("page_string"));
          Boolean new_search = (Boolean) request.getAttribute("new_search");
          String page_number = HTTPUtils.cleanXSS((String) request.getParameter("page_number"));
          String selectedResultsPerPage = HTTPUtils.cleanXSS((String) request.getAttribute("selectedResultsPerPage"));
          String contains_warning_msg = HTTPUtils.cleanXSS((String) request.getAttribute("contains_warning_msg"));

          if (page_number != null && new_search == Boolean.FALSE)
          {
		    bool_val = JSPUtils.isInteger(page_number);
		    if (!bool_val) {
			 String redirectURL = request.getContextPath() + "/pages/appscan_response.jsf";
			 String error_msg = HTTPUtils.createErrorMsg("page_number", page_number);
			 request.getSession().setAttribute("error_msg", error_msg);
			 response.sendRedirect(redirectURL);
		    } else {
			 page_string = page_number;
		    }          
              
          }
          request.setAttribute("new_search", Boolean.FALSE);
          int page_num = Integer.parseInt(page_string);
          int next_page_num = page_num + 1;
          int prev_page_num = page_num - 1;
          int page_size = 50;
          if (selectedResultsPerPage != null && selectedResultsPerPage.compareTo("") != 0)
          {
              page_size = Integer.parseInt(selectedResultsPerPage);
          }
          int iend = page_num * page_size;
          int istart = iend - page_size;
          if (iend > v.size()) iend = v.size();
          int num_pages = v.size() / page_size;
          if (num_pages * page_size < v.size()) num_pages++;
          String istart_str = Integer.toString(istart+1);
          String iend_str = Integer.toString(iend);
          String prev_page_num_str = Integer.toString(prev_page_num);
          String next_page_num_str = Integer.toString(next_page_num);
        %>
        <table class="datatable_960" border="0" width="100%">
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
              <b>Results <%=istart_str%>-<%=iend_str%> of&nbsp;<%=match_size%> for: <%=matchText%></b>&nbsp;<%=contains_warning_msg%>
            </td>
          </tr>
          <tr>
            <td class="textbody">
              <table class="datatable_960" summary="" cellpadding="3" cellspacing="0" border="0" width="100%">
                <%
                  for (int i=istart; i<iend; i++) {
                    if (i >= 0 && i<v.size()) {
                      Entity c = (Entity) v.elementAt(i);
                      String code = c.getEntityCode();
                      String name = c.getEntityDescription().getContent();

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
                            <a href="<%=request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=Constants.CODING_SCHEME_NAME%>&code=<%=code%>" ><%=DataUtils.encodeTerm(name)%></a>
                          </td>
                        </tr>
                      <%
                    }
                  }
                %>
              </table>
            </td>
          </tr>
        </table>
        <%@ include file="/pages/templates/pagination.jsp" %>
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
