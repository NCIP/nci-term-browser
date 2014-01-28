<%@ page import="java.util.*"%>
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

  String menubar_dictionary = HTTPUtils.cleanXSS(DataUtils.getCodingSchemeName( dictionaryName0 ));
  String menubar_version = HTTPUtils.cleanXSS(DataUtils.getCodingSchemeVersion( dictionaryName0 ));
  
  String menubar_formalname = null;
  if (menubar_dictionary != null) {
      menubar_formalname = DataUtils.getFormalName(menubar_dictionary);
  }
  boolean showMenuItems = true;
  if (menubar_dictionary == null || menubar_formalname == null) {
      showMenuItems = false;
  }
  if (menubar_version != null && !DataUtils.validateCodingSchemeVersion(menubar_formalname, menubar_version)) {
      showMenuItems = false;
  }  

  boolean menubar_isMapping = DataUtils.isMapping(menubar_dictionary, null);
  //Vector mapping_scheme_vec = DataUtils.getMappingCodingSchemes(menubar_dictionary);
  
  boolean hasValueSet = ValueSetHierarchy.hasValueSet(menubar_dictionary);
  boolean hasMapping = DataUtils.hasMapping(menubar_dictionary);

  if (menubar_version == null) menubar_version = menuBar_info.version; // HTTPUtils.cleanXSS already performed in JSPUtils.JSPHeaderInfo

  String hdr_dictionary0 = menuBar_info.dictionary;

  if (hdr_dictionary0 == null) {
      hdr_dictionary0 = ""; // Set to empty string
  }
  
  //11202103,, KLO
  //hdr_dictionary0 = DataUtils.uri2CodingSchemeName(hdr_dictionary0);
  hdr_dictionary0 = DataUtils.getCSName(hdr_dictionary0);
  
  hdr_dictionary0 = HTTPUtils.cleanXSS(hdr_dictionary0);

  boolean tree_access_allowed = true;
  if (DataUtils.get_vocabulariesWithoutTreeAccessHashSet().contains(hdr_dictionary0)) {
      tree_access_allowed = false;
  }

  Boolean[] isPipeDisplayed = new Boolean[] { Boolean.FALSE };
  String adjustedHeight = "";
  Boolean hideAdvancedSearchLink2 = (Boolean) request.getAttribute("hideAdvancedSearchLink");
  if (hideAdvancedSearchLink2 != null && hideAdvancedSearchLink2)
      adjustedHeight = "height=\"42\"";
      
     
%>
<table class="global-nav" border="0" width="100%" height="15px" <%=adjustedHeight%> cellpadding="0" cellspacing="0">
  <tr valign="bottom">
    <td align="left">
      <% if (menubar_isMapping) { %>
        <%= JSPUtils.getPipeSeparator(isPipeDisplayed) %>
        <a href="<%=request.getContextPath() %>/pages/mapping.jsf?dictionary=<%=menubar_dictionary%>&version=<%=menubar_version%>" tabindex="11">
          Mapping</a>
      <% } else if (tree_access_allowed) { %>
        <% if (showMenuItems) { 
		  if (DataUtils.isNull(hdr_dictionary0)) {
		       hdr_dictionary0 = "NCI_Thesaurus";
		  }        
        %>
          <%= JSPUtils.getPipeSeparator(isPipeDisplayed) %>
          <a href="#" onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=hdr_dictionary0%>&version=<%=menubar_version%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');" tabindex="12">
            Hierarchy</a>
        <% } %>
        
      <% } %>

      <% if (hasValueSet) { %>
        <%= JSPUtils.getPipeSeparator(isPipeDisplayed) %>

        <a href="<%= request.getContextPath() %>/ajax?action=create_cs_vs_tree&dictionary=<%=menubar_dictionary%>&version=<%=menubar_version%>" tabindex="15">Value Sets</a>
      
      
      <% } %>
      
      <% if (hasMapping) { %>
          <%= JSPUtils.getPipeSeparator(isPipeDisplayed) %>
          <a href="<%= request.getContextPath() %>/pages/cs_mappings.jsf?dictionary=<%=menubar_dictionary%>&version=<%=menubar_version%>" tabindex="15">Maps</a>      
      <% } %>
      
 
      <c:choose>
        <c:when test="${sessionScope.CartActionBean.count>0}">
          <%= JSPUtils.getPipeSeparator(isPipeDisplayed) %>
          <a href="<%= request.getContextPath() %>/pages/cart.jsf" tabindex="14">Cart</a>
        </c:when>
      </c:choose>    

      <%=VisitedConceptUtils.getDisplayLink(request, isPipeDisplayed)%>
    </td>
    
    
    
    <td align="right">
      <a href="<%= request.getContextPath() %>/pages/help.jsf" tabindex="16">Help</a>
    </td>
    <td width="7"></td>
  </tr>
</table>
