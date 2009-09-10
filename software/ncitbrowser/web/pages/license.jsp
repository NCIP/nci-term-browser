<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="java.util.Vector"%>
<%@ page import="org.LexGrid.concepts.Concept" %>
<%@ page import="gov.nih.nci.evs.browser.utils.DataUtils" %>
<%@ page import="gov.nih.nci.evs.browser.utils.MetadataUtils" %>
<%@ page import="gov.nih.nci.evs.browser.properties.NCItBrowserProperties" %>
<%@ page import="gov.nih.nci.evs.browser.bean.MetadataElement" %>
<%@ page import="gov.nih.nci.evs.browser.bean.LicenseBean" %>


<%
  String ncit_build_info = new DataUtils().getNCITBuildInfo();
%>
<!-- Build info: <%=ncit_build_info%> -->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
  <title>NCI Thesaurus</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/script.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/search.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/dropdown.js"></script>
</head>
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">

<f:view>

  <%@ include file="/pages/templates/header.xhtml" %>
  <div class="center-page">
    <%@ include file="/pages/templates/sub-header.xhtml" %>
    <!-- Main box -->
    

    <div id="main-area">
    
<%
            String scheme = (String) request.getAttribute("scheme");
            String version = (String) request.getAttribute("version");
            String ontology_list_str = (String) request.getAttribute("ontology_list_str");
	    String matchText = (String) request.getAttribute("matchText");
	    String matchAlgorithm = (String) request.getAttribute("algorithm");
            if (scheme != null) scheme = scheme.replaceAll("%20", " ");

String licenseStmt = LicenseBean.resolveCodingSchemeCopyright(scheme, version);

%>
<P>
Please review the following License/Copyright statement for <%=scheme%>.  
</p>
<P>
<%=licenseStmt%>  
</p>
<P>
If and only if you agree to these terms/conditions, click the Accept button to proceed.   
</p>
<P>
<%

String scheme0 = scheme;
String version0 = version;

            if (scheme != null) {
                scheme = scheme.replaceAll(" ", "%20");
            }
            if (version != null) {
                version = version.replaceAll(" ", "%20");
            }

%>


<form>

		  <h:commandButton
		    id="search"
		    value="Search"
		    action="#{userSessionBean.multipleSearchAction}"
		    image="#{facesContext.externalContext.requestContextPath}/images/accept.gif"
		    alt="Search">
		  </h:commandButton>
		  
&nbsp;&nbsp;
<img src="<%= request.getContextPath() %>/images/cancel.gif" name="cancel" alt="reset" onClick="history.back()" />

<input type="hidden" id="matchText" name="matchText" value="<%=matchText%>" />
<input type="hidden" id="algorithm" name="algorithm" value="<%=matchAlgorithm%>" />
<input type="hidden" id="ontology_list_str" name="ontology_list_str" value="<%=ontology_list_str%>" />
<input type="hidden" id="scheme" name="scheme" value="<%=scheme%>" />
<input type="hidden" id="version" name="version" value="<%=version%>" />

</form>


      
      <%@ include file="/pages/templates/nciFooter.html" %>

      <!-- end Page content -->
    </div>
    <div class="mainbox-bottom"><img src="<%=basePath%>/images/mainbox-bottom.gif" width="745" height="5" alt="Mainbox Bottom" /></div>
    <!-- end Main box -->
  </div>
</f:view>

</body>
</html>