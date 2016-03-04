<%@ page import="gov.nih.nci.evs.browser.utils.*" %>
<%
  String checked_valuesets = (String) request.getSession().getAttribute("checked_vocabularies");
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

    String algorithm = HTTPUtils.cleanXSS((String) request.getSession().getAttribute("valueset_search_algorithm"));
    String check_e = "", check_s = "" , check_c ="";

    if (algorithm == null || algorithm.compareTo("contains") == 0)
      check_c = "checked";
    else if (algorithm.compareTo("startsWith") == 0)
      check_s= "checked";
    else
      check_e = "checked";
      
      
        String searchTarget = (String) request.getSession().getAttribute("searchTarget");
        String check_n = "", check_p = "" , check_cd ="";
        

    if (DataUtils.isNullOrBlank(searchTarget)) {
        check_n = "checked";
    } else if (searchTarget.compareTo("codes") == 0) {
        check_cd = "checked";
    } else if (searchTarget.compareTo("names") == 0) {
        check_n = "checked";
    } else if (searchTarget.compareTo("properties") == 0) {
        check_p= "checked";
    }
  
      
%>
  

<form id="resolvedValueSetSearchForm" name="resolvedValueSetSearchForm" method="post" action="/ncitbrowser/ajax?action=search_downloaded_value_set" class="search-form" accept-charset="UTF-8" enctype="application/x-www-form-urlencoded">

<input type="hidden" name="resolvedValueSetSearchForm" value="resolvedValueSetSearchForm" />


    <input CLASS="searchbox-input" id="matchText" name="matchText" value="<%=termbrowser_displayed_match_text%>" onFocus="active=true"
        onBlur="active=false"  onkeypress="return submitEnter('resolvedValueSetSearchForm:resolvedvalueset_search',event)" tabindex="1"/>

    <input id="resolvedValueSetSearchForm:resolvedvalueset_search" type="image" src="/ncitbrowser/images/search.gif" name="resolvedValueSetSearchForm:resolvedvalueset_search" accesskey="13" alt="Search concepts in value set" onclick="javascript:cursor_wait();" tabindex="2" class="searchbox-btn" /><a href="/ncitbrowser/pages/help.jsf#searchhelp" tabindex="3"><img src="/ncitbrowser/images/search-help.gif" alt="Search Help" style="border-width:0;" class="searchbox-btn" /></a>



  <table border="0" cellspacing="0" cellpadding="0" width="340px">

    <tr valign="top" align="left">
      <td align="left" class="textbody" colspan="2">
        <input type="radio" name="valueset_search_algorithm" id="contains" value="contains" alt="Contains" <%=check_c%> tabindex="4" onclick="onAlgorithmChanged('resolvedValueSetSearchForm');"/><label for="contains">Contains</label>
        <input type="radio" name="valueset_search_algorithm" id="exactMatch" value="exactMatch" alt="Exact Match" <%=check_e%> tabindex="4"/><label for="exactMatch">Exact Match&nbsp;</label>
        <input type="radio" name="valueset_search_algorithm" id="startsWith" value="startsWith" alt="Begins With" <%=check_s%> tabindex="4" onclick="onAlgorithmChanged('resolvedValueSetSearchForm');"/><label for="startsWith">Begins With&nbsp;</label>
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
        <input type="radio" name="selectValueSetSearchOption" id="selectValueSetSearchOption" value="Name" alt="Name" <%=check_n%> tabindex="5"/><label for="names">Name&nbsp;</label>
        <input type="radio" name="selectValueSetSearchOption" id="selectValueSetSearchOption" value="Code" alt="Code" <%=check_cd%> tabindex="5" onclick="onCodeButtonPressed('resolvedValueSetSearchForm');" /><label for="codes">Code&nbsp;</label>
      </td>
    </tr>
  
    
    <tr><td height="5px;"></td></tr>
    <tr><td colspan="2">
      <table border="0" cellspacing="0" cellpadding="0" width="100%">
        <tr valign="top">

    <input type="hidden" name="referer" id="referer" value="<%=HTTPUtils.getRefererParmEncode(request)%>" />
    
<%
    String valuesetdef_uri = HTTPUtils.cleanXSS((String) request.getParameter("vsd_uri"));
    if (DataUtils.isNullOrBlank(valuesetdef_uri)) {
	valuesetdef_uri = (String) request.getSession().getAttribute("valuesetdef_uri");
    }  
if (valuesetdef_uri != null) {
%>
<input type="hidden" id="vsd_uri" name="vsd_uri" value="<%=valuesetdef_uri%>" />  
<%
}
%>
<%
if (!DataUtils.isNullOrBlank(checked_valuesets)) {
%>
    <input type="hidden" name="checked_vocabularies" id="checked_vocabularies" value="<%=checked_valuesets%>" />
<%
}
%> 

      <input type="hidden" id="nav_type" name="nav_type" value="valuesets" />
      <input type="hidden" id="view" name="view\ value="source" />
      <input type="hidden" id="vsd_uri" name="vsd_uri" value="<%=valuesetdef_uri%>" />  

</form>

        </tr>
      </table>
    </td></tr>
  </table>


