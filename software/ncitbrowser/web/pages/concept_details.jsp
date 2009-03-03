<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %> 

<%@ page import="java.util.List" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Vector"%>
<%@ page import="java.util.HashSet"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.Set"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="gov.nih.nci.evs.browser.utils.DataUtils" %>
<%@ page import="gov.nih.nci.evs.browser.properties.PropertyFileParser" %>
<%@ page import="gov.nih.nci.evs.browser.properties.NCItBrowserProperties" %>
<%@ page import="gov.nih.nci.evs.browser.bean.DisplayItem" %>
<%@ page import="gov.nih.nci.evs.browser.bean.*" %>
<%@ page import="gov.nih.nci.evs.browser.utils.*" %>
<%@ page import="org.LexGrid.concepts.Concept" %>
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

<html>
<head>
<title>NCI Thesaurus Browser Home</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/dhtmlcombo.css" />

<script type="text/javascript" src="<%= request.getContextPath() %>/js/script.js">
</script>

<script type="text/javascript" src="<%= request.getContextPath() %>/js/search.js">
</script>

<script type="text/javascript" src="<%= request.getContextPath() %>/js/dhtmlcombo.js">
</script>

</head>
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" >

<f:view>

<table width="100%" border="0" cellspacing="0" cellpadding="0">

	<tr> 
		<td height="1%" VALIGN="TOP"> 	
			<%@ include file="/pages/templates/nciHeader.html" %>
		        <%@ include file="/pages/templates/header.xhtml" %>
		</td> 
        </tr>

	<tr>
		<td height="1%">
                      <%@ include file="/pages/templates/quickLink.xhtml" %>
                </td>
        </tr>     
       
       

<% 
    String dictionary = (String) request.getParameter("dictionary");
    String code = (String) request.getParameter("code");
    String type = (String) request.getParameter("type");
    
    if (type == null)
    {
        type = "properties";
    }
    request.getSession().setAttribute("dictionary", dictionary);
    request.getSession().setAttribute("code", code);
    request.getSession().setAttribute("type", type);
    
    String vers = null;
    String ltag = null;
    Concept c = DataUtils.getConceptByCode(dictionary, vers, ltag, code);
    
    request.getSession().setAttribute("concept", c);
    
    String name = c.getEntityDescription().getContent();
    
%>

	   <tr>
	      <td>&nbsp;</td>
	   </tr>

	   <tr>   
	      <td class="standardText2">
		  <b><%=name%></b>
	      </td>
	   </tr>
			   
	
	<tr>
		<td height="1%" >
		     <%@ include file="/pages/templates/typeLinks.xhtml" %>
		</td>
	</tr>

	<tr>
		<td height="1%" >
		     <%@ include file="/pages/templates/property.xhtml" %>
		</td>
	</tr>


	
	<tr>
		<td height="20" width="100%" class="footerMenu">
		     <%@ include file="/pages/templates/applicationFooter.html" %>
		</td>
	</tr>

	<tr>
		<td>
		     <%@ include file="/pages/templates/nciFooter.html" %>
		</td>
	</tr>


</table>


</f:view>

</body>
</html>