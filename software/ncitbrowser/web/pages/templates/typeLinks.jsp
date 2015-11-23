<%@ page import="gov.nih.nci.evs.browser.utils.*" %>

<table width="900px" cellspacing="0" cellpadding="0" border="0" class="tabTable">
  <tr>
    <%
 JSPUtils.JSPHeaderInfo typeLinks_info = new JSPUtils.JSPHeaderInfo(request);
 String scheme = typeLinks_info.dictionary;
 //scheme = DataUtils.getFormalName(scheme);
 scheme = DataUtils.getCSName(scheme);

 String tab_version = typeLinks_info.version;
 boolean typeLink_isMapping = DataUtils.isMapping(scheme, null);


String _vse = HTTPUtils.cleanXSS((String) request.getParameter("vse")); 
String _search_key = HTTPUtils.cleanXSS((String) request.getParameter("key"));


String _page_number = HTTPUtils.cleanXSS((String) request.getParameter("page_number"));
String _ns = HTTPUtils.cleanXSS((String) request.getParameter("ns"));


if (DataUtils.isNull(_page_number)) {
    _page_number = "0";
}

 
      String jsp_page_name = "concept_details.jsf";
      String id = null;
      id = (String) request.getSession().getAttribute("code");

      String data_type = HTTPUtils.cleanXSS((String) request.getParameter("data_type"));
      if (data_type == null) {
          data_type = (String) request.getSession().getAttribute("type");
      }
      if (data_type == null) data_type = "properties";
      scheme = scheme.replaceAll(" ", "%20");
     %>
     <td width="134" height="21">
     <%
        if (data_type == null) {
          %>
            <img name="tpTab"
              src="<%=request.getContextPath() %>/images/tab_tp_clicked.gif"
              width="134" height="21" border="0" alt="Terms &amp; Properties"
              title="Terms &amp; Properties" />
          <%
        } else if (data_type.compareTo("properties") == 0) {
          %>
            <img name="tpTab"
              src="<%=request.getContextPath() %>/images/tab_tp_clicked.gif"
              width="134" height="21" border="0" alt="Terms &amp; Properties"
              title="Terms &amp; Properties" />
          <%
        } else if (data_type.compareTo("properties") != 0) {
          %>
            <a href="<%=request.getContextPath() %>/pages/<%=jsp_page_name%>?dictionary=<%=scheme%>&version=<%=tab_version%>&code=<%=id%>&ns=<%=_ns%>&type=properties&key=<%=_search_key%>&b=1&n=<%=_page_number%>&vse=<%=_vse%>"">
              <img name="tpTab"
                src="<%=request.getContextPath() %>/images/tab_tp.gif"
                width="134" height="21" border="0" alt="Terms &amp; Properties"
                title="Terms &amp; Properties" />
            </a>
          <%
        }
          %>
      </td>
      
        <td width="119" height="21">
          <%
            if (data_type == null ||
              (data_type != null && data_type.compareTo("synonym") != 0)) {
          %>
          <a href="<%=request.getContextPath() %>/pages/<%=jsp_page_name%>?dictionary=<%=scheme%>&version=<%=tab_version%>&code=<%=id%>&ns=<%=_ns%>&type=synonym&key=<%=_search_key%>&b=1&n=<%=_page_number%>&vse=<%=_vse%>"">
            <img name="sdTab"
              src="<%=request.getContextPath() %>/images/tab_sd.gif"
              width="119" height="21" border="0" alt="Synonym Details"
              title="Synonym Details" />
          </a>
          <%
            } else {
          %>
            <img name="sdTab"
              src="<%=request.getContextPath() %>/images/tab_sd_clicked.gif"
              width="119" height="21" border="0" alt="Synonym Details"
              title="Synonym Details" />
          <%
            }
          %>
          </td>      
      
      
      
      <td width="102" height="21">
        <%
          if (data_type == null ||
            (data_type != null && data_type.compareTo("relationship") != 0)) {
        %>
        <a href="<%=request.getContextPath() %>/pages/<%=jsp_page_name%>?dictionary=<%=scheme%>&version=<%=tab_version%>&code=<%=id%>&ns=<%=_ns%>&type=relationship&key=<%=_search_key%>&b=1&n=<%=_page_number%>&vse=<%=_vse%>"">
          <img name="relTab"
            src="<%=request.getContextPath() %>/images/tab_rel.gif"
            width="102" height="21" border="0" alt="Relationships"
            title="Relationships" />
        </a>
        <%
          } else {
        %>
          <img name="relTab"
            src="<%=request.getContextPath() %>/images/tab_rel_clicked.gif"
            width="102" height="21" border="0" alt="Relationships"
            title="Relationships" />
        <%
          }
        %>
        </td>
        
        

          
<%          
if (!typeLink_isMapping) {          
%>          
      <td width="85" height="21">
        <%
          if (data_type == null ||
            (data_type != null && data_type.compareTo("mapping") != 0)) {
        %>
        <a href="<%=request.getContextPath() %>/pages/<%=jsp_page_name%>?dictionary=<%=scheme%>&version=<%=tab_version%>&code=<%=id%>&ns=<%=_ns%>&type=mapping&key=<%=_search_key%>&b=1&n=<%=_page_number%>&vse=<%=_vse%>"">
          <img name="mapTab"
            src="<%=request.getContextPath() %>/images/tab_map.gif"
            width="90" height="21" border="0" alt="Mapping"
            title="Mappings" />
        </a>
        <%
          } else {
        %>
          <img name="mapTab"
            src="<%=request.getContextPath() %>/images/tab_map_clicked.gif"
            width="85" height="21" border="0" alt="Mapping"
            title="Mappings" />
        <%
          }
        %>
        </td>          
<%                   
}          
%>          
          
          
          <td width="71" height="21">
          <%
            if (data_type == null ||
              (data_type != null && data_type.compareTo("all") != 0)) {
          %>
            <a href="<%=request.getContextPath() %>/pages/<%=jsp_page_name%>?dictionary=<%=scheme%>&version=<%=tab_version%>&code=<%=id%>&ns=<%=_ns%>&type=all&key=<%=_search_key%>&b=1&n=<%=_page_number%>&vse=<%=_vse%>"">
              <img name="vaTab"
                src="<%=request.getContextPath() %>/images/tab_va.gif"
                width="71" height="21" border="0" alt="View All"
                title="View All" />
            </a>
          <%
            } else {
          %>
            <img name="vaTab"
              src="<%=request.getContextPath() %>/images/tab_va_clicked.gif"
              width="71" height="21" border="0" alt="View All"
              title="View All" />
          <%
          }
          %>
    </td>
    <td align="right" valign="top">
      &nbsp;
    </td>
  </tr>
</table>
