<%@ page import="gov.nih.nci.evs.browser.utils.*" %>



<table cellspacing="0" cellpadding="0" border="0">
  <tr>
    <%
      String nav_type_in_session = (String) request.getSession().getAttribute("nav_type");
    
      String imagesPath = request.getContextPath() + "/images/";
      String pagesPath = request.getContextPath() + "/pages/";
      String term_jsp_page_name = "multiple_search.jsf";
      String valueset_jsp_page_name = "value_set_source_view.jsf";
      String mapping_jsp_page_name = "mapping_search.jsf";
      
      //String nav_tab_nav_type = JSPUtils.getNavType(request);
      JSPUtils.JSPHeaderInfo nav_tab_info = new JSPUtils.JSPHeaderInfo(request);
      String nav_tab_vsd_uri = (String) request.getParameter("vsd_uri");
      String nav_tab_dictionary = nav_tab_info.dictionary;
      String nav_tab_version = nav_tab_info.version;
      String nav_tab_nav_type = (String) request.getParameter("nav_type");
      
      System.out.println("nav_tab_dictionary: " + nav_tab_dictionary);
      System.out.println("nav_tab_version: " + nav_tab_version);
      System.out.println("nav_tab_vsd_uri: " + nav_tab_vsd_uri);
      String nav_type = DataUtils.getNavigationTabType(nav_tab_dictionary, nav_tab_version, nav_tab_vsd_uri, nav_tab_nav_type);
      
      if (nav_type == null) {
          if (nav_type_in_session != null) nav_type = nav_type_in_session;
      }
      request.getSession().setAttribute("nav_type", nav_type);
      
      String tab_terms_image = nav_type.equalsIgnoreCase("terminologies")
        ? "tab_terms_clicked.gif" : "tab_terms.gif";
      tab_terms_image = imagesPath + tab_terms_image;
      String tab_terms_link = pagesPath + term_jsp_page_name + "?nav_type=terminologies";   
      
      String tab_valuesets_image = nav_type.equalsIgnoreCase("valuesets")
        ? "tab_valuesets_clicked.gif" : "tab_valuesets.gif";
      tab_valuesets_image = imagesPath + tab_valuesets_image;
      String tab_valuesets_link = pagesPath + valueset_jsp_page_name + "?nav_type=valuesets";
    
      String tab_mappings_image = nav_type.equalsIgnoreCase("mappings")
        ? "tab_map_clicked.gif" : "tab_map.gif";
      tab_mappings_image = imagesPath + tab_mappings_image;
      String tab_mappings_link = pagesPath + mapping_jsp_page_name + "?nav_type=mappings";
    %>

    <%-- 
      Note: Slight gap appears between the tab images and the logo when
        * (For Firefox): </a> is on a separate line,
        * (For Internet Explorer): </td> is on a separate line
    --%>
    <td width="5"></td>
    <td><a href="<%=tab_terms_link%>">
      <img name="tab_terms" src="<%=tab_terms_image%>"
        border="0" alt="Terminologies" title="Terminologies" /></a></td>
    <td><a href="<%=tab_valuesets_link%>">
      <img name="tab_valuesets" src="<%=tab_valuesets_image%>"
        border="0" alt="Value Sets" title="ValueSets" /></a></td>
    <td><a href="<%=tab_mappings_link%>">
      <img name="tab_map" src="<%=tab_mappings_image%>"
        border="0" alt="Mappings" title="Mappings" /></a></td>
  </tr>
</table>
