<%@ page import="gov.nih.nci.evs.browser.utils.*" %>
<%@ page import="gov.nih.nci.evs.browser.common.*" %>

<table cellspacing="0" cellpadding="0" border="0">
  <tr>
    <%
    
      String imagesPath = request.getContextPath() + "/images/";
      String pagesPath = request.getContextPath() + "/pages/";
      String term_jsp_page_name = "multiple_search.jsf";
      String valueset_jsp_page_name = "value_set_source_view.jsf";
      String mapping_jsp_page_name = "mapping_search.jsf";
      String nav_type = JSPUtils.getNavType(request);

      if (nav_type == null) {
	      String vs_nav_type = (String) request.getSession().getAttribute("vs_nav_type");
	      if (vs_nav_type != null) {
		  nav_type = vs_nav_type;
		  request.getSession().removeAttribute("vs_nav_type");
	      } 
      }
      
      if (nav_type == null) {
          nav_type = "terminologies";
      }
 
request.getSession().setAttribute("nav_type", nav_type);

      
      String tab_terms_image = nav_type.equalsIgnoreCase("terminologies")
        ? "tab_terms_clicked.gif" : "tab_terms.gif";
      tab_terms_image = imagesPath + tab_terms_image;
      String tab_terms_link = pagesPath + term_jsp_page_name + "?nav_type=terminologies";
      
      
      String tab_valuesets_image = nav_type.equalsIgnoreCase("valuesets")
        ? "tab_valuesets_clicked.gif" : "tab_valuesets.gif";
        tab_valuesets_image = imagesPath + tab_valuesets_image;

	String tab_valueset_link = null;
	String mode = null;

	mode = (String) request.getSession().getAttribute("mode");

	if (mode == null) {
	    //mode = Constants.MODE_EXPAND;
	    mode = Constants.MODE_COLLAPSE;
	}

	tab_valueset_link = request.getContextPath() + "/ajax2?action=create_src_vs_tree&nav_type=valuesets";      
	if (mode.compareToIgnoreCase(Constants.MODE_EXPAND) == 0) {
	      tab_valueset_link = request.getContextPath() + "/ajax?action=create_src_vs_tree&nav_type=valuesets&mode=1";
	} else if (mode.compareToIgnoreCase(Constants.MODE_COLLAPSE) == 0) {
	      tab_valueset_link = request.getContextPath() + "/ajax?action=create_src_vs_tree&nav_type=valuesets&mode=2";
	} 
	request.getSession().setAttribute("mode", mode);

      String tab_mappings_image = nav_type.equalsIgnoreCase("mappings")
        ? "tab_map_clicked.gif" : "tab_map.gif";
      tab_mappings_image = imagesPath + tab_mappings_image;
      String tab_mappings_link = pagesPath + mapping_jsp_page_name + "?nav_type=mappings&b=0&m=0";
    %>

    <%-- 
      Note: Slight gap appears between the tab images and the logo when
        * (For Firefox): </a> is on a separate line,
        * (For Internet Explorer): </td> is on a separate line
    --%>
    
    <td width="5"></td>
    <td><a href="<%=HTTPUtils.cleanXSS(tab_terms_link)%>">
      <img name="tab_terms" src="<%=tab_terms_image%>"
        border="0" alt="Terminologies" title="Terminologies" /></a></td>
        
    <% 
    Boolean value_set_tab_available = DataUtils.VALUE_SET_TAB_AVAILABLE;
    if (value_set_tab_available != null && value_set_tab_available.equals(Boolean.TRUE)) {
    %>
    
    <td><a href="<%=HTTPUtils.cleanXSS(tab_valueset_link)%>">
      <img name="tab_valuesets" src="<%=tab_valuesets_image%>"
        border="0" alt="Value Sets" title="ValueSets" /></a></td>
    <%    
    }
    %>
        
    <td><a href="<%=HTTPUtils.cleanXSS(tab_mappings_link)%>">
      <img name="tab_map" src="<%=tab_mappings_image%>"
        border="0" alt="Mappings" title="Mappings" /></a></td>
  </tr>
</table>
