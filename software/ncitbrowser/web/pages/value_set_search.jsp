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


    String t = null;
    String selected_uri = null;
    String selected_cs = "";
    String selected_cd = null;

    String check_uri = null;
    String check_cs = "";
    String check_cd = null;
    
    String message = (String) request.getSession().getAttribute("message");;

    String selectValueSetSearchOption = null;
    selectValueSetSearchOption = (String) request.getParameter("opt");
               
    if (selectValueSetSearchOption == null || selectValueSetSearchOption.compareTo("null") == 0) {
        selectValueSetSearchOption = "URI";
    }

    String check__uri = "", check__cs = "", check__cd = "";
    if (selectValueSetSearchOption == null || selectValueSetSearchOption.compareTo("URI") == 0)
      check_uri = "checked";
    else if (selectValueSetSearchOption.compareTo("CodingScheme") == 0)
        check_cs = "checked";
    else if (selectValueSetSearchOption.compareTo("ConceptDomain") == 0)
      check_cd = "checked";
    else check_uri = "checked";
           

%>
        <div class="pagecontent">
          <a name="evs-content" id="evs-content"></a>
          
          <%@ include file="/pages/templates/navigationTabs.jsp"%>
          <div class="tabTableContentContainer">
          
          <table>
            <tr>
            <td class="textbody">Value Sets</td>
            </tr>

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
            

            
          </td></tr>
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
