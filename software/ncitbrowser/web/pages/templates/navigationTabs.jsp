<%@ page import="gov.nih.nci.evs.browser.utils.HistoryUtils" %>
<%@ page import="gov.nih.nci.evs.browser.utils.DataUtils"%>

<table cellspacing="0" cellpadding="0" border="0">
  <tr>
    <%
      String jsp_page_name = "multiple_search.jsf";
      String mapping_jsp_page_name = "mapping_search.jsf";
      String nav_type = (String) gov.nih.nci.evs.browser.utils.HTTPUtils.cleanXSS((String) request.getParameter("nav_type"));
      if (nav_type == null) {
          nav_type = (String) request.getSession().getAttribute("nav_type");
          if (nav_type != null) request.getSession().removeAttribute("nav_type");
      }
      if (nav_type == null) nav_type = "terminologies";
     %>
      <td width="105" height="21">
        <% if (nav_type == null) { %>
          <img name="tab_terms"
            src="<%=request.getContextPath() %>/images/tab_tp_clicked.gif"
            width="105" height="21" border="0" alt="Terminologies"
            title="Terminologies" />
        <% } else if (nav_type.compareTo("terminologies") == 0) { %>
          <img name="tab_terms"
            src="<%=request.getContextPath() %>/images/tab_terms_clicked.gif"
            width="105" height="21" border="0" alt="Terminologies" 
            title="Terminologies" />
        <% } else if (nav_type.compareTo("terminologies") != 0) { %>
          <a href="<%=request.getContextPath() %>/pages/<%=jsp_page_name%>?&nav_type=terminologies">
            <img name="tab_terms"
              src="<%=request.getContextPath() %>/images/tab_terms.gif"
              width="105" height="21" border="0" alt="Terminologies" 
              title="Terminologies" />
          </a>
        <% } %>
      </td>

      <td width="88" height="21">
        <% if (nav_type == null ||
            (nav_type != null && nav_type.compareTo("valuesets") != 0)) { %>
          <a href="<%=request.getContextPath() %>/pages/value_set_search.jsf">
            <img name="tab_valuesets"
              src="<%=request.getContextPath() %>/images/tab_valuesets.gif"
              width="88" height="21" border="0" alt="Value Sets" 
              title="ValueSets" />
          </a>
        <% } else { %>
          <img name="tab_valuesets"
            src="<%=request.getContextPath() %>/images/tab_valuesets_clicked.gif"
            width="88" height="21" border="0" alt="Value Sets" 
            title="ValueSets" />
        <% } %>
     </td>
     
      <td width="88" height="21">
        <% if (nav_type == null ||
            (nav_type != null && nav_type.compareTo("mappings") != 0)) { %>
          <a href="<%=request.getContextPath() %>/pages/<%=mapping_jsp_page_name%>?nav_type=mappings">
            <img name="tab_map"
              src="<%=request.getContextPath() %>/images/tab_map.gif"
              width="88" height="21" border="0" alt="Mappings" 
              title="Mappings" />
          </a>
        <% } else { %>
          <img name="tab_map"
            src="<%=request.getContextPath() %>/images/tab_map_clicked.gif"
            width="88" height="21" border="0" alt="Mappings" 
            title="Mappings" />
        <% } %>
      </td>


  </tr>
</table>