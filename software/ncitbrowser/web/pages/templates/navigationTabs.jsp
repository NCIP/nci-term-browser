<%@ page import="gov.nih.nci.evs.browser.utils.*" %>

<table cellspacing="0" cellpadding="0" border="0">
  <tr>
    <%
      String jsp_page_name = "multiple_search.jsf";
      String mapping_jsp_page_name = "mapping_search.jsf";
      String nav_type = (String) HTTPUtils.cleanXSS(request.getParameter("nav_type"));
      if (nav_type == null) {
        nav_type = (String) request.getSession().getAttribute("nav_type");
        if (nav_type != null) request.getSession().removeAttribute("nav_type");
      }
      if (nav_type == null) nav_type = "terminologies";

      String imagesPath = request.getContextPath() + "/images/";
      String pagesPath = request.getContextPath() + "/pages/";
      String tab_terms_image = nav_type.equals("terminologies")
        ? "tab_terms_clicked.gif" : "tab_terms.gif";
      tab_terms_image = imagesPath + tab_terms_image;
      String tab_terms_link = pagesPath + jsp_page_name + "?&nav_type=terminologies";   
      
      String tab_valuesets_image = nav_type.equals("valuesets")
        ? "tab_valuesets_clicked.gif" : "tab_valuesets.gif";
      tab_valuesets_image = imagesPath + tab_valuesets_image;
      String tab_valuesets_link = pagesPath + "value_set_search.jsf";
    
      String tab_mappings_image = nav_type.equals("mappings")
        ? "tab_map_clicked.gif" : "tab_map.gif";
      tab_mappings_image = imagesPath + tab_mappings_image;
      String tab_mappings_link = pagesPath + mapping_jsp_page_name + "?nav_type=mappings";

    %>
    <td>
      <a href="<%=tab_terms_link%>">
        <img name="tab_terms" src="<%=tab_terms_image%>" border="0"
          alt="Terminologies" title="Terminologies" /></a>
    </td>

    <td>
      <a href="<%=tab_valuesets_link%>">
        <img name="tab_valuesets" src="<%=tab_valuesets_image%>"
          border="0" alt="Value Sets" title="ValueSets" /></a>
    </td>
   
    <td>
      <a href="<%=tab_mappings_link%>">
        <img name="tab_map" src="<%=tab_mappings_image%>"
          border="0" alt="Mappings" title="Mappings" /></a>
    </td>
  </tr>
</table>
