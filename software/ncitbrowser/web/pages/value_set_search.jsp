<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*"%>
<%@ page import="org.LexGrid.concepts.Entity" %>
<%@ page import="gov.nih.nci.evs.browser.bean.*" %>
<%@ page import="gov.nih.nci.evs.browser.utils.*" %>
<%@ page import="gov.nih.nci.evs.browser.properties.*" %>
<%@ page import="gov.nih.nci.evs.browser.utils.*" %>
<%@ page import="javax.faces.context.FacesContext" %>
<%@ page import="org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference" %>
<%@ page import="org.apache.log4j.*" %>

<%@ page import="org.lexgrid.valuesets.LexEVSValueSetDefinitionServices" %>


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
<body>
  <script type="text/javascript"
    src="<%=request.getContextPath()%>/js/wz_tooltip.js"></script>
  <script type="text/javascript"
    src="<%=request.getContextPath()%>/js/tip_centerwindow.js"></script>
  <script type="text/javascript"
    src="<%=request.getContextPath()%>/js/tip_followscroll.js"></script>
  <script type="text/javascript">
  
    function refresh() {
      
      var selectValueSetSearchOptionObj = document.forms["valueSetSearchForm"].selectValueSetSearchOption;
      for (var i=0; i<selectValueSetSearchOptionObj.length; i++) {
        if (selectValueSetSearchOptionObj[i].checked) {
            selectValueSetSearchOption = selectValueSetSearchOptionObj[i].value;
        }
      }

      window.location.href="/ncitbrowser/pages/value_set_search.jsf?refresh=1"
          + "&opt="+ selectValueSetSearchOption;

    }
  </script>
  <%!
    private static Logger _logger = Utils.getJspLogger("value_set_search.jsp");
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
      <%@ include file="/pages/templates/content-header-valueset.jsp" %> 
<%

  String valueSetSearch_requestContextPath = request.getContextPath();
  valueSetSearch_requestContextPath = valueSetSearch_requestContextPath.replace("//ncitbrowser//ncitbrowser", "//ncitbrowser");

String conceptDomain = (String) request.getSession().getAttribute("conceptDomain");
System.out.println("subset_editor conceptDomain: " + conceptDomain);
if (conceptDomain == null) conceptDomain = "";

    String message = (String) request.getSession().getAttribute("message");
    request.getSession().removeAttribute("message");
    String t = null;

%>
        <div class="pagecontent">
          <a name="evs-content" id="evs-content"></a>
          
          <%@ include file="/pages/templates/navigationTabs.jsp"%>
          <div class="tabTableContentContainer">
          
            <span class="textbody">&nbsp;All value sets available on the server are listed below. 
            You may search value sets by code, name, source, or coding scheme. Click on the Help (i.e., the question mark) icon above for instructions on
            how to perform each type of search.
            <br/>
            (WARNING: <b>Code</b> and <b>Name</b> searches require resolving all value sets on the server; it can be slow.)
            <br/><br/>
            </span>
            
          
          <table>
          

            <% if (message != null) { 
                request.getSession().removeAttribute("message");
            %>
		<tr class="textbodyred"><td>
	            <p class="textbodyred">&nbsp;<%=message%></p>
		</td></tr>
            <% 
            } 
            %>

         <tr class="textbody"><td>
            
              <table class="dataTable" summary="" cellpadding="3" cellspacing="0" border="0" width="100%">

		<th class="dataTableHeader" scope="col" align="left">Name</th>
		<th class="dataTableHeader" scope="col" align="left">URI</th>
                <th class="dataTableHeader" scope="col" align="left">Description</th>

<%
		Vector vsd_vec = (Vector) request.getSession().getAttribute("vsddata");
		if (vsd_vec == null) {
		     vsd_vec = DataUtils.getValueSetDefinitionMetadata();
		     request.getSession().setAttribute("vsddata", vsd_vec);
		}

                for (int i=0; i<vsd_vec.size(); i++) {
            
		    String vsd_str = (String) vsd_vec.elementAt(i);
		    Vector u = DataUtils.parseData(vsd_str);
		    
		    String name = (String) u.elementAt(0);
		    String uri = (String) u.elementAt(1);
		    String description = (String) u.elementAt(2);
		    //String domain = (String) u.elementAt(3);
		    //String src_str = (String) u.elementAt(4);

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
                         <a href="<%=request.getContextPath() %>/pages/value_set_search_results.jsf?uri=<%=uri%>"><%=name%></a>
		      </td>
		      <td class="dataCellText">
                         <a href="<%=request.getContextPath() %>/pages/value_set_search_results.jsf?uri=<%=uri%>"><%=uri%></a>
		      </td>		      
		      
		      <td class="dataCellText">
			 <%=description%>
		      </td>

		      </tr>
              
              
             <%
                }
             %>                 
                  
              </table>
            
          </td>
          </tr>
        </table>
        </div> <!-- end tabTableContentContainer -->
        
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
