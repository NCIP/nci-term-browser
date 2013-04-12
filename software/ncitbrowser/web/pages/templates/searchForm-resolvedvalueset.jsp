<%--L
  Copyright Northrop Grumman Information Technology.

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
L--%>

<%@ page import="gov.nih.nci.evs.browser.utils.*" %>
<%

          
          
  String match_text = gov.nih.nci.evs.browser.utils.HTTPUtils
    .cleanXSS((String) request.getSession().getAttribute("matchText_RVS"));

  if (match_text == null) match_text = "";

    String userAgent = request.getHeader("user-agent");
    boolean isIE = userAgent != null && userAgent.toLowerCase().contains("msie");


String uri_str = (String) request.getSession().getAttribute("vsd_uri");
if (uri_str == null) {
    uri_str = HTTPUtils.cleanXSS((String) request.getParameter("vsd_uri"));
}

  String termbrowser_displayed_match_text = HTTPUtils.convertJSPString(match_text);
  String searchform_requestContextPath = request.getContextPath();
  searchform_requestContextPath = searchform_requestContextPath.replace("//ncitbrowser//ncitbrowser", "//ncitbrowser");

    String algorithm = gov.nih.nci.evs.browser.utils.HTTPUtils.cleanXSS((String) request.getSession().getAttribute("algorithm"));

    String check_e = "", check_s = "" , check_c ="";
    if (algorithm == null || algorithm.compareTo("exactMatch") == 0)
      check_e = "checked";
    else if (algorithm.compareTo("startsWith") == 0)
      check_s= "checked";
    else
      check_c = "checked";
      
      
        String searchTarget = (String) request.getSession().getAttribute("searchTarget");
        String check_n = "", check_p = "" , check_cd ="";
        if (searchTarget == null || searchTarget.compareTo("code") == 0)
          check_cd = "checked";
        else if (searchTarget.compareTo("names") == 0)
          check_n = "checked";
        else if (searchTarget.compareTo("properties") == 0)
          check_p= "checked";
  
      
%>
  
  
<h:form id="resolvedValueSetSearchForm" styleClass="search-form">   
    <input CLASS="searchbox-input" id="matchText" name="matchText" value="<%=match_text%>" onFocus="active=true"
        onBlur="active=false"  onkeypress="return submitEnter('resolvedValueSetSearchForm:resolvedvalueset_search',event)" tabindex="1"/>
    <h:commandButton id="resolvedvalueset_search" value="Search" action="#{valueSetBean.resolvedValueSetSearchAction}"
      accesskey="13"
      onclick="javascript:cursor_wait();"
      image="#{form_requestContextPath}/images/search.gif"
      alt="Search Value Set"
      styleClass="searchbox-btn"
      tabindex="2">
    </h:commandButton>
    <h:outputLink value="#{facesContext.externalContext.requestContextPath}/pages/help.jsf#searchhelp" tabindex="3">
      <h:graphicImage value="/images/search-help.gif" style="border-width:0;" styleClass="searchbox-btn" alt="Search Help" />
    </h:outputLink>

  <table border="0" cellspacing="0" cellpadding="0" width="340px">

    <tr valign="top" align="left">
      <td align="left" class="textbody" colspan="2">
        <input type="radio" name="algorithm" id="algorithm1" value="exactMatch" alt="Exact Match" <%=check_e%> tabindex="4"/><label for="algorithm1">Exact Match&nbsp;</label>
        <input type="radio" name="algorithm" id="algorithm2" value="startsWith" alt="Begins With" <%=check_s%> tabindex="4"/><label for="algorithm2">Begins With&nbsp;</label>
        <input type="radio" name="algorithm" id="algorithm3" value="contains" alt="Contains" <%=check_c%> tabindex="4"/><label for="algorithm3">Contains</label>
      </td>
    </tr>  
    
    <tr align="left">
      <td width="263px" height="1px" bgcolor="#2F2F5F"></td>
      <!-- The following lines are needed to make "Advanced Search" link flush right -->
      <% if (isIE) { %>
          <td width="77px"></td>
      <% } else { %>
          <td></td>
      <% } %>
    </tr>

    <tr valign="top" align="left">
      <td align="left" class="textbody" colspan="2">
        <input type="radio" name="searchTarget" id="searchTarget0" value="code" alt="Code" <%=check_cd%> tabindex="5"/><label for="searchTarget0">Code&nbsp;</label>
        <input type="radio" name="searchTarget" id="searchTarget1" value="names" alt="Names" <%=check_n%> tabindex="5"/><label for="searchTarget1">Name&nbsp;</label>
        <input type="radio" name="searchTarget" id="searchTarget2" value="properties" alt="Properties" <%=check_p%> tabindex="5"/><label for="searchTarget2">Property</label>
      </td>
    </tr>
    
    
    <tr><td height="5px;"></td></tr>
    <tr><td colspan="2">
      <table border="0" cellspacing="0" cellpadding="0" width="100%">
        <tr valign="top">

    <input type="hidden" name="referer" id="referer" value="<%=HTTPUtils.getRefererParmEncode(request)%>" />
<%
if (uri_str != null) {
%>
<input type="hidden" name="vsd_uri" id="vsd_uri" value="<%=uri_str%>" />
<%
}
%>
</h:form>

        </tr>
      </table>
    </td></tr>
  </table>


