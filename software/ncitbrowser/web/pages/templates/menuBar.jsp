<%@ page import="gov.nih.nci.evs.browser.utils.HTTPUtils"%>
<%@ page import="gov.nih.nci.evs.browser.common.Constants"%>
<%
  String dictionaryName0 = null;
  String dictionaryName = gov.nih.nci.evs.browser.utils.HTTPUtils.cleanXSS((String) request.getParameter("dictionary"));
  if (dictionaryName == null) dictionaryName = Constants.CODING_SCHEME_NAME;

  dictionaryName0 = dictionaryName;
  dictionaryName = dictionaryName.replaceAll(" ", "%20");

  dictionaryName0 = DataUtils.replaceAll(dictionaryName0, "&#40;", "(");
  dictionaryName0 = DataUtils.replaceAll(dictionaryName0, "&#41;", ")");
  String menubar_dictionary = DataUtils.getCodingSchemeName( dictionaryName0 );
  String menubar_version = DataUtils.getCodingSchemeVersion( dictionaryName0 );
    
%>
<table class="global-nav" border="0" width="100%" cellpadding="0"
  cellspacing="0">
  <tr>
    <td align="left">
    <%
         if (menubar_dictionary != null && menubar_dictionary.compareTo(Constants.CODING_SCHEME_NAME) == 0) {
      %> <a href="<%= request.getContextPath() %>">Home</a> <%
         } else {
            if (menubar_version == null) {
      %> <a
      href="<%= request.getContextPath() %>/pages/vocabulary.jsf?dictionary=<%=menubar_dictionary%>">Home</a>
    <%
            } else {
      %> <a
      href="<%= request.getContextPath() %>/pages/vocabulary.jsf?dictionary=<%=menubar_dictionary%>&version=<%=menubar_version%>">Home</a>
    <%
            }
         }
      %> | <a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=menubar_dictionary%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
    View Hierarchy </a> <%
      if (menubar_dictionary != null && menubar_dictionary.compareTo(Constants.CODING_SCHEME_NAME) == 0) {
      %> | <a href="<%= request.getContextPath() %>/pages/subset.jsf">Subsets</a>
    <%
      }
      %> | <a href="<%= request.getContextPath() %>/pages/help.jsf">Help</a>
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