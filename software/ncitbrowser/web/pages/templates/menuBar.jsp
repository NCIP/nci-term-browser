<%@ page import="gov.nih.nci.evs.browser.common.*"%>
<%@ page import="gov.nih.nci.evs.browser.utils.*"%>
<%
  JSPUtils.JSPHeaderInfo menuBar_info = new JSPUtils.JSPHeaderInfo(request);
  String dictionaryName0 = null;
  String dictionaryName = menuBar_info.dictionary;
  if (dictionaryName == null) dictionaryName = (String) request.getSession().getAttribute("dictionary");
  if (dictionaryName == null) dictionaryName = Constants.CODING_SCHEME_NAME;

  dictionaryName0 = dictionaryName;
  dictionaryName = dictionaryName.replaceAll(" ", "%20");

  dictionaryName0 = DataUtils.replaceAll(dictionaryName0, "&#40;", "(");
  dictionaryName0 = DataUtils.replaceAll(dictionaryName0, "&#41;", ")");

  String menubar_dictionary = DataUtils.getCodingSchemeName( dictionaryName0 );
  String menubar_version = DataUtils.getCodingSchemeVersion( dictionaryName0 );


 boolean menubar_isMapping = DataUtils.isMapping(menubar_dictionary, null);


if (menubar_version == null) {
    menubar_version = menuBar_info.version;
}
//System.out.println("menuBar.jsp menubar_version: " + menubar_version);


  String hdr_dictionary0 = menuBar_info.dictionary;
  if (hdr_dictionary0 == null) hdr_dictionary0 = ""; // Set to empty string

 boolean tree_access_allowed = true;
 if (DataUtils._vocabulariesWithoutTreeAccessHashSet.contains(hdr_dictionary0)) {
     tree_access_allowed = false;
 }


%>
<table class="global-nav" border="0" width="100%" cellpadding="0"
  cellspacing="0">
  <tr>
    <td align="left">
    <%



         if (hdr_dictionary0.compareTo(Constants.CODING_SCHEME_NAME) == 0) {
      %> <a href="<%= request.getContextPath()%>" tabindex="10">Home</a> <%
         } else {
            if (menubar_version == null) {
      %> <a
      href="<%= request.getContextPath() %>/pages/vocabulary.jsf?dictionary=<%=HTTPUtils.cleanXSS(hdr_dictionary0)%>" tabindex="10">Home</a>
    <%
            } else {
      %> <a
      href="<%= request.getContextPath() %>/pages/vocabulary.jsf?dictionary=<%=HTTPUtils.cleanXSS(menubar_dictionary)%>&version=<%=HTTPUtils.cleanXSS(menubar_version)%>" tabindex="10">Home</a>
    <%
            }
         }
      %>
      <%

      if (menubar_isMapping) {
      %>
      | <a href="<%=request.getContextPath() %>/pages/mapping.jsf?dictionary=<%=HTTPUtils.cleanXSS(menubar_dictionary)%>&version=<%=menubar_version%>" tabindex="11">
      View Mapping
      </a>

      <%
      }

      else if (tree_access_allowed) {
      %>

      | <a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(hdr_dictionary0)%>&version=<%=menubar_version%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');" tabindex="12">
      View Hierarchy 
      </a>
      <%
      }
      %>
	<c:choose>	
		<c:when test="${sessionScope.CartActionBean.count>0}">
		| <a href="<%= request.getContextPath() %>/pages/cart.jsf" tabindex="14">Cart</a>
	    </c:when>
    </c:choose>    
    <%
      if (hdr_dictionary0 != null && hdr_dictionary0.compareTo(Constants.CODING_SCHEME_NAME) == 0) {
    %> 
   
      | <a href="<%= request.getContextPath() %>/pages/subset.jsf" tabindex="15">Subsets</a>

 
      | <a href="<%= request.getContextPath() %>/pages/value_set_hierarcy.jsf" tabindex="15">Value Sets</a>
      
    <%
      }
      %> | <a href="<%= request.getContextPath() %>/pages/help.jsf" tabindex="16">Help</a>
    </td>
    <td align="right">
      <%=VisitedConceptUtils.getDisplayLink(request)%>
    </td>
    <td width="7"></td>
  </tr>
</table>