<table class="global-nav" border="0" width="100%" cellpadding="0"
  cellspacing="0">
  <tr>
    <td align="left"><a
      href="<%= request.getContextPath() %>/start.jsf">Home</a> | <a
      href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/source_help_info-termbrowser.jsf',
    '_blank','top=100, left=100, height=740, width=780, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
    Sources</a> | <a href="<%= request.getContextPath() %>/pages/help.jsf">Help</a>
    </td>
    <td align="right">
    <%
        Vector visitedConcepts = (Vector) request.getSession().getAttribute("visitedConcepts");
        if (visitedConcepts != null && visitedConcepts.size() > 0) {
          String visitedConceptsStr = DataUtils.getVisitedConceptLink(visitedConcepts);
       %> <%=visitedConceptsStr%> <%
        }
     %>
    </td>
    <td width="7"></td>
  </tr>
</table>