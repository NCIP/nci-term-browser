<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="java.util.Vector"%>
<%@ page import="org.LexGrid.concepts.Concept" %>
<html>
<head>
<title>NCI Thesaurus Browser Home</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />
<script type="text/javascript" src="<%= request.getContextPath() %>/js/script.js">
</script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/search.js">
</script>
</head>
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" >

<f:view>

<table valign="top" width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td height="1%" VALIGN="TOP">
            <%@ include file="/pages/templates/nciHeader.html" %>
            <%@ include file="/pages/templates/header.xhtml" %>
        </td>
    </tr>
    <tr>
        <td height="20px">
            <%@ include file="/pages/templates/quickLink.xhtml" %>
        </td>
    </tr>
       
       
	<tr>
		<td>


		<!-- ========================================================================= -->
		<!-- 					  		 HELP CONTENT					   -->
		<!-- ========================================================================= -->
		
		<TABLE width="650" border="0" align="left" cellpadding="15" cellspacing="0" >
			<tr><td allign="left">
			    <b>Help</b>
			</td></tr>
			
			<tr><td allign="left">
			    <hr></hr>
			</td></tr>

			<tr><td allign="left">
			    <b>Help Tool</b>
			</td></tr>
			
			<tr><td class="standardText"><i>
			</i></td></tr>

			<tr><td class="standardText"><i>
To be implemented.			
                        </i></td></tr>

			
			<tr><td allign="left">
			    <hr></hr>
			</td></tr>



			<tr><td allign="left">
			    <b>Search Help</b>
			</td></tr>
			
			<tr><td class="standardText"><i>
To be implemented.			
	
			</i></td></tr>


		</TABLE>
	
						
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


