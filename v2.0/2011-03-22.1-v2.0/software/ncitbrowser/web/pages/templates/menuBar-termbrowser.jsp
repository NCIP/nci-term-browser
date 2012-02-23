<table class="global-nav" border="0" width="100%" height="33px" cellpadding="0"
  cellspacing="0">
  <tr>
    <td align="left" valign="bottom"><a
      href="<%= request.getContextPath() %>/start.jsf" tabindex="10">Home</a> | <a
      href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/source_help_info-termbrowser.jsf',
    '_blank','top=100, left=100, height=740, width=780, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');" tabindex="13">
    Sources</a> 
   	<c:choose>	
		<c:when test="${sessionScope.CartActionBean.count>0}">
			| <a href="<%= request.getContextPath() %>/pages/cart.jsf" tabindex="14">Cart</a>
	    </c:when>
    </c:choose> 
     | <a href="<%= request.getContextPath() %>/pages/help.jsf" tabindex="16">Help</a>
    </td>
    <td align="right" valign="bottom">
      <%= VisitedConceptUtils.getDisplayLink(request) %>
    </td>
    <td width="7"></td>
  </tr>
</table>