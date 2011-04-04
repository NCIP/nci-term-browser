<%@ page import="gov.nih.nci.evs.browser.common.*"%>
<%@ page import="gov.nih.nci.evs.browser.utils.*"%>

<%

  JSPUtils.JSPHeaderInfo mbo_info = new JSPUtils.JSPHeaderInfo(request);
  String dictionaryName0 = null;
  String dictionaryName = mbo_info.dictionary;
  if (dictionaryName == null) dictionaryName = (String) request.getSession().getAttribute("dictionary");
  if (dictionaryName == null) dictionaryName = Constants.CODING_SCHEME_NAME;

  dictionaryName0 = dictionaryName;
  dictionaryName = dictionaryName.replaceAll(" ", "%20");

  dictionaryName0 = DataUtils.replaceAll(dictionaryName0, "&#40;", "(");
  dictionaryName0 = DataUtils.replaceAll(dictionaryName0, "&#41;", ")");

  String mbo_dictionary = DataUtils.getCodingSchemeName( dictionaryName0 );
  String mbo_version = DataUtils.getCodingSchemeVersion( dictionaryName0 );
  
  
%>

<table class="global-nav" border="0" width="100%" height="33px" cellpadding="0" cellspacing="0">
  <tr>
    <td align="left" valign="bottom">
      <a href="#" onclick="javascript:window.open('<%=request.getContextPath() %>/pages/source_help_info-termbrowser.jsf',
        '_blank','top=100, left=100, height=740, width=780, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');" tabindex="13">
        Sources</a> 
   
      <c:choose>	
        <c:when test="${sessionScope.CartActionBean.count>0}">
          | <a href="<%= request.getContextPath() %>/pages/cart.jsf" tabindex="14">Cart</a>
        </c:when>
      </c:choose> 
      <%= VisitedConceptUtils.getDisplayLink(request, true) %> 
    </td>
    <td align="right" valign="bottom">
      <a href="<%= request.getContextPath() %>/pages/help.jsf" tabindex="16">Help</a>
    </td>
    <td width="7"></td>
  </tr>
</table>