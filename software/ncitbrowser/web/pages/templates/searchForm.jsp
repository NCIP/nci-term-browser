<%--L
  Copyright Northrop Grumman Information Technology.

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
L--%>

<%@ page import="gov.nih.nci.evs.browser.properties.*"%>
<%@ page import="gov.nih.nci.evs.browser.utils.*"%>
<%@ page import="gov.nih.nci.evs.browser.bean.*"%>
<%@ page import="org.apache.log4j.*"%>
<script type="text/javascript">
	function cursor_wait() {
		document.body.style.cursor = 'wait';
	}
	function disableAnchor() {
		var obj1 = document.getElementById("a_tpTab");
		if (obj1 != null)
			obj1.removeAttribute('href');

		var obj2 = document.getElementById("a_relTab");
		if (obj2 != null)
			obj2.removeAttribute('href');

		var obj3 = document.getElementById("a_synTab");
		if (obj3 != null)
			obj3.removeAttribute('href');

		var obj4 = document.getElementById("a_srcTab");
		if (obj4 != null)
			obj4.removeAttribute('href');

		var obj5 = document.getElementById("a_allTab");
		if (obj5 != null)
			obj5.removeAttribute('href');

		var obj6 = document.getElementById("a_hierBut");
		if (obj6 != null)
			obj6.removeAttribute('href');
	}
</script>
<%
	Logger logger = Utils.getJspLogger("searchForm.jsp");
	String form_requestContextPath = request.getContextPath();
	form_requestContextPath = form_requestContextPath.replace(
			"//ncitbrowser//ncitbrowser", "//ncitbrowser");
	String userAgent = request.getHeader("user-agent");
	boolean isIE = userAgent != null
			&& userAgent.toLowerCase().contains("msie");
	String match_text = (String) request.getSession().getAttribute(
			"matchText");
	if (match_text == null || match_text.compareTo("null") == 0)
		match_text = "";
	JSPUtils.JSPHeaderInfo vocab_info = new JSPUtils.JSPHeaderInfo(
			request);
	String vocab_name = vocab_info.dictionary;
	vocab_name = DataUtils.getCodingSchemeName(vocab_name);
	String srchform_version = vocab_info.version;

	boolean is_a_mapping = DataUtils.isMapping(vocab_name,
			srchform_version);

	logger.debug(Utils.SEPARATOR);
	logger.debug("searchForm.jsp vocab_name: " + vocab_name);
	logger.debug("searchForm.jsp version: " + srchform_version);

	String displayed_match_text = HTTPUtils
			.convertJSPString(match_text);
	String algorithm = gov.nih.nci.evs.browser.utils.HTTPUtils
			.cleanXSS((String) request.getSession().getAttribute(
					"algorithm"));

	String check_e = "", check_b = "", check_s = "", check_c = "";
	if (algorithm == null || algorithm.compareTo("exactMatch") == 0)
		check_e = "checked";
	else if (algorithm.compareTo("startsWith") == 0)
		check_s = "checked";
	else if (algorithm.compareTo("DoubleMetaphoneLuceneQuery") == 0)
		check_b = "checked";
	else
		check_c = "checked";

	String searchTarget = (String) request.getSession().getAttribute(
			"searchTarget");
	String check_n = "", check_p = "", check_r = "";
	if (searchTarget == null || searchTarget.compareTo("names") == 0)
		check_n = "checked";
	else if (searchTarget.compareTo("properties") == 0)
		check_p = "checked";
	else
		check_r = "checked";
%>
<h:form id="searchTerm" styleClass="search-form" onsubmit="javascript:disableAnchor();">
   <label for="matchText" />
   <input CLASS="searchbox-input" id="matchText" name="matchText"
      value="<%=HTTPUtils.cleanXSS(displayed_match_text)%>" onFocus="active=true"
      onBlur="active=false"
      onkeypress="return submitEnter('searchTerm:search', event)"
      tabindex="1" />
   <h:commandButton id="search" value="Search"
      action="#{userSessionBean.searchAction}" accesskey="13"
      onclick="javascript:cursor_wait();"
      image="#{form_requestContextPath}/images/search.gif" alt="Search"
      styleClass="searchbox-btn" tabindex="2">
   </h:commandButton>
   <h:outputLink
      value="#{facesContext.externalContext.requestContextPath}/pages/help.jsf#searchhelp"
      tabindex="3">
      <h:graphicImage value="/images/search-help.gif" alt="Search Help"
         style="border-width:0;" styleClass="searchbox-btn" />
   </h:outputLink>
   <table border="0" cellspacing="0" cellpadding="0" width="340px">
      <tr valign="top" align="left">
         <td align="left" class="textbody" colspan="2">
            <input type="radio" name="algorithm" id="algorithm1"
               value="exactMatch" alt="Exact Match" <%=HTTPUtils.cleanXSS(check_e)%>
               tabindex="4" />
            <label for="algorithm1">Exact Match&nbsp;</label>
            <input type="radio" name="algorithm" id="algorithm2" value="startsWith" alt="Begins With"
               <%=HTTPUtils.cleanXSS(check_s)%> tabindex="4" />
            <label for="algorithm2">Begins With&nbsp;</label>
            <input type="radio" name="algorithm" id="algorithm3" value="contains" alt="Contains" <%=HTTPUtils.cleanXSS(check_c)%>
               tabindex="4" />
            <label for="algorithm3">Contains</label>
         </td>
      </tr>
      <tr align="left">
        <td width="263px" height="1px" bgcolor="#2F2F5F"></td>
        <td width="77px"></td>
      </tr>
      <tr valign="top" align="left">
         <td align="left" class="textbody" colspan="2">
            <input type="radio" name="searchTarget" id="searchTarget1"
               value="names" alt="Names" <%=HTTPUtils.cleanXSS(check_n)%> tabindex="5" />
            <label for="searchTarget1">Name/Code&nbsp;</label>   
            <input type="radio" name="searchTarget" id="searchTarget2"
               value="properties" alt="Properties" <%=HTTPUtils.cleanXSS(check_p)%>
               tabindex="5" />
            <label for="searchTarget2">Property&nbsp;</label>   
            <input type="radio" name="searchTarget" id="searchTarget3"
               value="relationships" alt="Relationships" <%=HTTPUtils.cleanXSS(check_r)%>
               tabindex="5" />
            <label for="searchTarget3">Relationship</label>               
         </td>
      </tr>
      <tr>
         <td height="5px;"></td>
      </tr>
      <tr>
         <td colspan="2">
            <%
               Boolean hideAdvancedSearchLink = (Boolean) request.getAttribute("hideAdvancedSearchLink");
               if (!is_a_mapping && (hideAdvancedSearchLink == null || !hideAdvancedSearchLink)) {
            %>
            <table border="0" cellspacing="0" cellpadding="0" width="100%">
               <tr valign="top">
                  <td height="5px;"></td>
                  <td valign="middle" align="right">
                     <a class="global-nav"
                        href="<%=request.getContextPath()%>/pages/advanced_search.jsf?dictionary=<%=HTTPUtils.cleanXSS(vocab_name)%>&version=<%=HTTPUtils.cleanXSS(srchform_version)%>"
                        tabindex="6"> Advanced Search </a>
                  </td>
               </tr>
            </table>
            <%
               }
            %>            
         </td>
      </tr>
   </table>
   <input type="hidden" name="referer" id="referer" value="<%=HTTPUtils.getRefererParmEncode(request)%>" />   
   <%
      if (vocab_name != null) {
   %>
   <input type="hidden" id="vocabulary" name="vocabulary" value="<%=HTTPUtils.cleanXSS(vocab_name)%>" />
   <input type="hidden" id="dictionary" name="dictionary" value="<%=HTTPUtils.cleanXSS(vocab_name)%>" />
   <input type="hidden" id="scheme" name="scheme" value="<%=HTTPUtils.cleanXSS(vocab_name)%>" />
   <%
      }
      if (srchform_version != null) {
   %>
   <input type="hidden" id="version" name="version" value="<%=HTTPUtils.cleanXSS(srchform_version)%>" />
   <%
      }
   %>   
</h:form>