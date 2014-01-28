<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
  <%@ include file="/pages/templates/header.jsp" %>
  <div class="center-page_960">
    <%@ include file="/pages/templates/sub-header.jsp" %>
    <!-- Main box -->
    <div id="main-area_960">

        <%
        //String msg_dictionary = gov.nih.nci.evs.browser.utils.HTTPUtils.cleanXSS((String) request.getSession().getAttribute("dictionary"));
        String msg_dictionary = (String) request.getSession().getAttribute("dictionary");

        if (msg_dictionary == null) {
        %>
          <%@ include file="/pages/templates/content-header-termbrowser.jsp" %>
        <%
        } else if (msg_dictionary.compareTo("NCI Thesaurus") == 0 || msg_dictionary.compareTo("NCI%20Thesaurus") == 0) {
        %>
          <%@ include file="/pages/templates/content-header.jsp" %>
        <%
        } else {
                request.getSession().setAttribute("dictionary", msg_dictionary);
        %>
                <%@ include file="/pages/templates/content-header-other.jsp" %>
        <%
        }
        %>




      <!-- Page content -->
      <div class="pagecontent">
        <a name="evs-content" id="evs-content"></a>
        <%
          String message = (String) request.getSession().getAttribute("message");
          request.getSession().removeAttribute("message");
        %>
        
        <p class="textbodyred">&nbsp;<%=message%></p>

        
        <%@ include file="/pages/templates/nciFooter.jsp" %>
      </div>
      <!-- end Page content -->
    </div>
    <div class="mainbox-bottom"><img src="<%=basePath%>/images/mainbox-bottom.gif" width="941" height="5" alt="Mainbox Bottom" /></div>
    <!-- end Main box -->
  </div>
</f:view>
</body>
</html>
