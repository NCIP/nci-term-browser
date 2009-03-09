<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %> 
<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="java.util.Vector"%>
<%@ page import="org.LexGrid.concepts.Concept" %>
<html>
<head>
<title>NCI Thesaurus Browser Home</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />
<script type="text/javascript" src="<%= request.getContextPath() %>/js/script.js">
</script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/search.js">
</script>
</head>
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" >

<f:view>

<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
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
        
	<tr>
		<td valign="top">
		

		<table width="650" border="0" align="left" cellpadding="15" cellspacing="0" >
		        <tr><td>&nbsp;&nbsp;</td><td>&nbsp;&nbsp;</td></tr>
			<tr><td>&nbsp;&nbsp;</td><td>
			     <td class="standardText" align="left" ><i>

				<%
				String message = (String) request.getSession().getAttribute("message");
				%>

				<%=message%>			     
                            </i></td>
			</tr>
	        </table>
		
			
	        </td>
	</tr>
	<tr>
	        <td>
		     <%@ include file="/pages/templates/applicationFooter.html" %>
		</td>
	</tr>

	<tr>
		<td valign="top">
		     <%@ include file="/pages/templates/nciFooter.html" %>
		</td>
	</tr>


</table>



</f:view>

</body>
</html>