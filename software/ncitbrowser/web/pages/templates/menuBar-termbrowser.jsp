<%--L
  Copyright Northrop Grumman Information Technology.

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
L--%>

<%@ page import="gov.nih.nci.evs.browser.common.*"%>
<%@ page import="gov.nih.nci.evs.browser.utils.*"%>

<%

  JSPUtils.JSPHeaderInfo menubar_tb_info = new JSPUtils.JSPHeaderInfo(request);
  String _dictionaryName0 = null;
  String _dictionaryName = menubar_tb_info.dictionary;
  if (_dictionaryName == null) _dictionaryName = (String) request.getSession().getAttribute("dictionary");
  if (_dictionaryName == null) _dictionaryName = Constants.CODING_SCHEME_NAME;

  _dictionaryName0 = _dictionaryName;
  _dictionaryName = _dictionaryName.replaceAll(" ", "%20");

  _dictionaryName0 = DataUtils.replaceAll(_dictionaryName0, "&#40;", "(");
  _dictionaryName0 = DataUtils.replaceAll(_dictionaryName0, "&#41;", ")");

  String _menubar_tb_dictionary = DataUtils.getCodingSchemeName( _dictionaryName0 );
  String _menubar_tb_version = DataUtils.getCodingSchemeVersion( _dictionaryName0 );

  boolean hasValueSet = ValueSetHierarchy.hasValueSet(_menubar_tb_dictionary);
  boolean hasMapping = DataUtils.hasMapping(_menubar_tb_dictionary);
  
  int globalNavHeight = JSPUtils.parseInt(
      (String) request.getAttribute("globalNavHeight"), 33);
%>

<table class="global-nav" border="0" width="100%" height="<%=globalNavHeight%>px" cellpadding="0" cellspacing="0">
  <tr>
    <td align="left" valign="bottom">
      <a href="#" onclick="javascript:window.open('<%=request.getContextPath() %>/pages/source_help_info-termbrowser.jsf',
        '_blank','top=100, left=100, height=740, width=780, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');" tabindex="13">
        Sources</a> 
   
   

      <% if (hasValueSet) { %>
        |
        <!--
        <a href="<%= request.getContextPath() %>/pages/value_set_hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(_dictionaryName)%>&version=<%=HTTPUtils.cleanXSS(_menubar_tb_version)%>" tabindex="15">Value Sets</a>
        -->
        
        <a href="<%= request.getContextPath() %>/ajax?action=create_cs_vs_tree&dictionary=<%=HTTPUtils.cleanXSS(_dictionaryName)%>&version=<%=HTTPUtils.cleanXSS(_menubar_tb_version)%>" tabindex="15">Value Sets</a>
      
      <% } %>
      
      <% if (hasMapping) { %>
        |
          <a href="<%= request.getContextPath() %>/pages/cs_mappings.jsf?dictionary=<%=HTTPUtils.cleanXSS(_dictionaryName)%>&version=<%=HTTPUtils.cleanXSS(_menubar_tb_version)%>" tabindex="15">Maps</a>      
      <% } %>     
         
   
   
   
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