<%@ page import="gov.nih.nci.evs.browser.utils.DataUtils" %>
<%
  String match_text = gov.nih.nci.evs.browser.utils.HTTPUtils
    .cleanXSS((String) request.getSession().getAttribute("matchText"));
  
  if (match_text == null) match_text = "";
 
%>
<div class="search-form">
  <input CLASS="searchbox-input"
    name="matchText"
    value="<%=match_text%>"
    onFocus="active = true"
    onBlur="active = false"
    onkeypress="return submitEnter('search',event)"
  />
  <h:commandButton
    id="search"
    value="Search"
    action="#{userSessionBean.multipleSearchAction}"
    image="#{facesContext.externalContext.requestContextPath}/images/search.gif"
    alt="Search">
  </h:commandButton>
  <h:outputLink
    value="#{facesContext.externalContext.requestContextPath}/pages/help.jsf#searchhelp">
    <h:graphicImage value="/images/search-help.gif"
    style="border-width:0;" />
  </h:outputLink>
  <%
    String algorithm = (String) request.getSession().getAttribute("algorithm");

    String check_e = "", check_s = "" , check_c ="";
    if (algorithm == null || algorithm.compareTo("exactMatch") == 0)
      check_e = "checked";
    else if (algorithm.compareTo("startsWith") == 0)
      check_s= "checked";
    else
      check_c = "checked";
  %>
  <table border="0" cellspacing="0" cellpadding="0">
    <tr valign="top" align="left">
      <td align="left" class="textbody">
        <input type="radio" name="algorithm" value="exactMatch" alt="Exact Match" <%=check_e%>>Exact Match&nbsp;
        <input type="radio" name="algorithm" value="startsWith" alt="Begins With" <%=check_s%>>Begins With&nbsp;
        <input type="radio" name="algorithm" value="contains" alt="Containts" <%=check_c%>>Contains&nbsp;
        <%
          String searchTarget = (String) request.getSession().getAttribute("searchTarget");
          String check_n = "", check_p = "" , check_r ="";
          if (searchTarget == null || searchTarget.compareTo("names") == 0)
            check_n = "checked";
          else if (searchTarget.compareTo("properties") == 0)
            check_p= "checked";
          else
            check_r = "checked";
        %>
      </td>
    </tr>
    <tr align="left">
      <td height="1px" bgcolor="#2F2F5F"></td>
    </tr>
    <tr valign="top" align="left">
      <td align="left" class="textbody">
        <input type="radio" name="searchTarget" value="names" alt="Names" <%=check_n%>>Name/Code&nbsp;
        <input type="radio" name="searchTarget" value="properties" alt="Properties" <%=check_p%>>Property&nbsp;
        <input type="radio" name="searchTarget" value="relationships" alt="Relationships" <%=check_r%>>Relationship&nbsp;
      </td>
    </tr>
  </table>
</div>
