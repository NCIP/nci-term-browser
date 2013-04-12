<%--L
  Copyright Northrop Grumman Information Technology.

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
L--%>

<%@ page import="gov.nih.nci.evs.browser.utils.HistoryUtils" %>
<%@ page import="gov.nih.nci.evs.browser.utils.DataUtils"%>

<table width="700px" cellspacing="0" cellpadding="0" border="0" class="tabTable">
  <tr>
    <%
 String scheme = (String) request.getSession().getAttribute("dictionary");
 scheme = DataUtils.getFormalName(scheme);

 String tab_version = (String) request.getAttribute("version");
 if (tab_version == null ) {
     tab_version = DataUtils.getVocabularyVersionByTag(scheme, "PRODUCTION");
 }
 System.out.println("typeLink.jsp tab_version: " + version);

 boolean tree_access = true;
 if (DataUtils._vocabulariesWithoutTreeAccessHashSet.contains(scheme)) {
     tree_access = false;
 }
 
      String jsp_page_name = "concept_details.jsf";
      String id = null;
      id = (String) request.getSession().getAttribute("code");

      String data_type = (String) gov.nih.nci.evs.browser.utils.HTTPUtils.cleanXSS((String) request.getParameter("data_type"));
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
            <a href="<%=request.getContextPath() %>/pages/<%=jsp_page_name%>?dictionary=<%=scheme%>&version=<%=tab_version%>&code=<%=id%>&type=properties">
              <img name="tpTab"
                src="<%=request.getContextPath() %>/images/tab_tp.gif"
                width="134" height="21" border="0" alt="Terms &amp; Properties"
                title="Terms &amp; Properties" />
            </a>
          <%
        }
          %>
      </td>
      <td width="102" height="21">
        <%
          if (data_type == null ||
            (data_type != null && data_type.compareTo("relationship") != 0)) {
        %>
        <a href="<%=request.getContextPath() %>/pages/<%=jsp_page_name%>?dictionary=<%=scheme%>&version=<%=tab_version%>&code=<%=id%>&type=relationship">
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
        <td width="119" height="21">
          <%
            if (data_type == null ||
              (data_type != null && data_type.compareTo("synonym") != 0)) {
          %>
          <a href="<%=request.getContextPath() %>/pages/<%=jsp_page_name%>?dictionary=<%=scheme%>&version=<%=tab_version%>&code=<%=id%>&type=synonym">
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
          <td width="71" height="21">
          <%
            if (data_type == null ||
              (data_type != null && data_type.compareTo("all") != 0)) {
          %>
            <a href="<%=request.getContextPath() %>/pages/<%=jsp_page_name%>?dictionary=<%=scheme%>&version=<%=tab_version%>&code=<%=id%>&type=all">
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
         <%
          if (tree_access) {
         %>       
    
        <input onClick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=scheme%>&version=<%=tab_version%>&code=<%=id%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');"
                  type="button"
                 class="redButton"
                 value="View in Hierarchy"
        />
        
	 <% } 
	 %>    
	 
        <%
        String link_scheme = scheme.replaceAll("%20", " ");
        if (HistoryUtils.isHistoryServiceAvailable(link_scheme)) {
        %>
        &nbsp;&nbsp;
        <input onClick="javascript:window.open('<%=request.getContextPath() %>/pages/concept_history.jsf?dictionary=<%=scheme%>&version=<%=tab_version%>&code=<%=id%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');"
                  type="button"
                 class="redButton"
                 value="View History"
        />
        <%
        }
        %>
    </td>
  </tr>
</table>