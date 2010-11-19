<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=windows-1252"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns:c="http://java.sun.com/jsp/jstl/core">
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
<f:view>
	<h:form>
	  <%
	    String contactUsUrl = request.getContextPath() + "/pages/contact_us.jsf";
	    String subsetsUrl = request.getContextPath() + "/pages/subset.jsf";
	  %>
	  <%@ include file="/pages/templates/header.jsp" %>
	  <div class="center-page">
	    <%@ include file="/pages/templates/sub-header.jsp" %>
	    <!-- Main box -->
	    <div id="main-area">
	   <%@ include file="/pages/templates/content-header-no-searchbox.jsp" %>
	      <!-- Page content -->
	      <div class="pagecontent">
	      	<table border="0" width="708px">
	      		<tr>
	      			<td>
						<table border="0">
						  <tr>
						    <td class="texttitle-blue">Cart</td>	
						    <td class="texttitle-gray">(<h:outputText value="#{CartActionBean.count}"/>)</td>
						  </tr>
						</table>
					</td>
					<td class="texttitle-blue-rightjust-large" align="right">
						<h:commandLink onclick="history.go(-1);return false;" value="Back"/>
					</td>
				</tr>      
			</table>	
			<hr/>
			<table class="dataTable" summary="" cellpadding="3" cellspacing="0" border="0" width="100%">
		        <tr>
		          <th class="dataTableHeader" scope="col" align="left">Term</th>
		          <th class="dataTableHeader" scope="col" align="left">Code</th>
				  <th class="dataTableHeader" scope="col" align="left">Source</th>
				</tr>  
			    <c:forEach var="item" begin="0" items="${sessionScope.CartActionBean.concepts}" varStatus="status">	        
					<c:choose>
						<c:when test="${status.index % 2 == 0}">
							<tr class="dataRowDark">
						</c:when>
						<c:when test="${status.index % 2 != 0}">	
							<tr class="dataRowLight">		        	        
				        </c:when>
				    </c:choose>   
				    	<td>${item.name}</td> 
			            <td>${item.code}</td>
			            <td>${item.dictionary}</td>
			        </tr>
			    </c:forEach>
			</table>
	        <br/>
	        <%@ include file="/pages/templates/nciFooter.html" %>
	      </div> <!-- end pagecontent -->
	    </div> <!-- end main-area -->
	    <div class="mainbox-bottom"><img src="<%=basePath%>/images/mainbox-bottom.gif" width="745" height="5" alt="Mainbox Bottom" /></div>
	    <!-- end Main box -->
	  </div> <!-- end center-page -->
	</h:form>	  
</f:view>
</body>
</html>