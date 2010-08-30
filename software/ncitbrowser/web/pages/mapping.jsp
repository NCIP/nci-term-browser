<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ page contentType="text/html;charset=windows-1252"%>

<%@ page import="java.util.Vector"%>
<%@ page import="java.util.HashMap"%>

<%@ page import="org.LexGrid.concepts.Entity" %>
<%@ page import="gov.nih.nci.evs.browser.common.Constants" %>
<%@ page import="gov.nih.nci.evs.browser.utils.DataUtils" %>
<%@ page import="gov.nih.nci.evs.browser.utils.HTTPUtils" %>

<%@ page import="gov.nih.nci.evs.browser.bean.MappingIteratorBean" %>

<script type="text/javascript" src="<%= request.getContextPath() %>/js/yui/yahoo-min.js" ></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/yui/event-min.js" ></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/yui/dom-min.js" ></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/yui/animation-min.js" ></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/yui/container-min.js" ></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/yui/connection-min.js" ></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/yui/autocomplete-min.js" ></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/yui/treeview-min.js" ></script>
<%
  String basePath = request.getContextPath();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
  <head>
  <title>Mapping Vocabulary</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />
  <link rel="shortcut icon" href="<%= request.getContextPath() %>/favicon.ico" type="image/x-icon" />
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/yui/fonts.css" />
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/yui/grids.css" />
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/yui/code.css" />
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/yui/tree.css" />
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/script.js"></script>

  <script language="JavaScript">

  </script>
 
  
</head>
<body>
  <f:view>

    <div id="popupContainer">
      <!-- nci popup banner -->
      <div class="ncipopupbanner">
        <a href="http://www.cancer.gov" target="_blank" alt="National Cancer Institute"><img src="<%=basePath%>/images/nci-banner-1.gif" width="440" height="39" border="0" alt="National Cancer Institute" /></a>
        <a href="http://www.cancer.gov" target="_blank" alt="National Cancer Institute"><img src="<%=basePath%>/images/spacer.gif" width="48" height="39" border="0" alt="National Cancer Institute" class="print-header" /></a>
      </div>
      <!-- end nci popup banner -->
      <div id="popupMainArea">
        <table class="evsLogoBg" cellspacing="0" cellpadding="0" border="0">
        <tr>
          <td valign="top">
            <a href="http://evs.nci.nih.gov/" target="_blank" alt="Enterprise Vocabulary Services">
              <img src="<%=basePath%>/images/evs-popup-logo.gif" width="213" height="26" alt="EVS: Enterprise Vocabulary Services" title="EVS: Enterprise Vocabulary Services" border="0" />
            </a>
          </td>
          <td valign="top"><div id="closeWindow"><a href="javascript:window.close();"><img src="<%=basePath%>/images/thesaurus_close_icon.gif" width="10" height="10" border="0" alt="Close Window" />&nbsp;CLOSE WINDOW</a></div></td>
        </tr>
        </table>

<%

Object scheme2MappingIteratorBean = request.getSession().getAttribute("scheme2MappingIteratorBeanMap");
HashMap scheme2MappingIteratorBeanMap = null;
if (scheme2MappingIteratorBean != null) {
    scheme2MappingIteratorBeanMap = (HashMap) scheme2MappingIteratorBean;
} else {
    scheme2MappingIteratorBeanMap = new HashMap();
    request.getSession().setAttribute("scheme2MappingIteratorBeanMap", scheme2MappingIteratorBeanMap);
}

String mapping_dictionary = request.getParameter("dictionary");
String mapping_version = request.getParameter("version");

String mapping_schema = request.getParameter("schema");
if (mapping_dictionary != null && mapping_schema == null) mapping_schema = mapping_dictionary;

MappingIteratorBean bean = (MappingIteratorBean) scheme2MappingIteratorBeanMap.get(mapping_schema);
if (bean == null) {
    bean = new MappingIteratorBean();
    // initialization
    scheme2MappingIteratorBeanMap.put(mapping_schema, bean);
}

String term_browser_version = DataUtils.getMetadataValue(mapping_schema, mapping_version, "term_browser_version");
String display_name = DataUtils.getMetadataValue(mapping_schema, mapping_version, "display_name");


if (display_name == null || display_name.compareTo("null") == 0) {
   display_name = DataUtils.getLocalName(mapping_schema); 
}

if (mapping_schema.compareTo("NCI Thesaurus") == 0) {
%>
    <div><img src="<%=basePath%>/images/thesaurus_popup_banner.gif" width="612" height="56" alt="NCI Thesaurus" title="" border="0" /></div>
<%
} else {
     String mapping_shortName = DataUtils.getLocalName(mapping_schema);
%>
    <div>
      <img src="<%=basePath%>/images/other_popup_banner.gif" width="612" height="56" alt="NCI Thesaurus" title="" border="0" />
      <div class="vocabularynamepopupshort"><%=HTTPUtils.cleanXSS(display_name)%></div>
    </div>
<%
}
%>
        <div id="popupContentArea">
          <table width="580px" cellpadding="3" cellspacing="0" border="0">
            <th>Source Code</th>
            <th>Source Name</th>
            <th>REL</th>
            <th>Map Rank</th>
            <th>Target Code</th>
            <th>Target Name</th>
            <tr>
            <%
                String source_scheme = "NCI_Thesaurus";
                String source_version = "10.06e";
                
                String target_scheme = "ICD_9_CM";
                String target_version = "2010";

                
                String source_code = "C25765";
                String source_name = "Secondary Acute Myeloid Leukemia";
                String rel = "RN";
                String score = "6";
                String target_code = "205";
                String target_name = "Myeloid leukemia, acute";
            %>
		    <td>
<a href="#"
      onclick="javascript:window.open('<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=source_scheme%>&code=<%=source_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <%=source_code%>
</a> 

<a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(source_scheme)%>&version=<%=source_version%>&code=<%=source_code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=source_code%>" />
</a> 
		    
		    </td>
		    <td><%=source_name%></td>
		    <td><%=rel%></td>
		    <td><%=score%></td>
		    <td>
		    
<a href="#"
      onclick="javascript:window.open('<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=target_scheme%>&code=<%=target_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <%=target_code%>
</a> 

<a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(target_scheme)%>&version=<%=target_version%>&code=<%=target_code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=target_code%>" />
</a> 		    
                    </td>
		    <td><%=target_name%></td>
</tr> 


<tr>
		    <%
		    source_code = "C26913";
		    source_name = "Verruca Plantaris";
		    rel = "SY";
		    score = "6";
		    target_code = "78.12";
		    target_name = "Plantar wart";
		    %>
		    <td>

<a href="#"
      onclick="javascript:window.open('<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=source_scheme%>&code=<%=source_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <%=source_code%>
</a>

<a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(source_scheme)%>&version=<%=source_version%>&code=<%=source_code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=source_code%>" />
</a>

		    </td>
		    <td><%=source_name%></td>
		    <td><%=rel%></td>
		    <td><%=score%></td>
		    <td>

<a href="#"
      onclick="javascript:window.open('<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=target_scheme%>&code=<%=target_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
     <%=target_code%>
</a>

<a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(target_scheme)%>&version=<%=target_version%>&code=<%=target_code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=target_code%>" />
</a>
                    </td>
		    <td><%=target_name%></td>
</tr>
<tr>
		    <%
		    source_code = "C26919";
		    source_name = "Lymphosarcoma";
		    rel = "SY";
		    score = "6";
		    target_code = "200.1";
		    target_name = "Lymphosarcoma";
		    %>
		    <td>

<a href="#"
      onclick="javascript:window.open('<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=source_scheme%>&code=<%=source_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <%=source_code%>
</a>

<a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(source_scheme)%>&version=<%=source_version%>&code=<%=source_code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=source_code%>" />
</a>

		    </td>
		    <td><%=source_name%></td>
		    <td><%=rel%></td>
		    <td><%=score%></td>
		    <td>

<a href="#"
      onclick="javascript:window.open('<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=target_scheme%>&code=<%=target_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
     <%=target_code%>
</a>

<a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(target_scheme)%>&version=<%=target_version%>&code=<%=target_code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=target_code%>" />
</a>
                    </td>
		    <td><%=target_name%></td>
</tr>
<tr>
		    <%
		    source_code = "C26959";
		    source_name = "Reticulosarcoma Involving Spleen";
		    rel = "SY";
		    score = "6";
		    target_code = "200.07";
		    target_name = "Reticulosarcoma involving spleen";
		    %>
		    <td>

<a href="#"
      onclick="javascript:window.open('<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=source_scheme%>&code=<%=source_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <%=source_code%>
</a>

<a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(source_scheme)%>&version=<%=source_version%>&code=<%=source_code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=source_code%>" />
</a>

		    </td>
		    <td><%=source_name%></td>
		    <td><%=rel%></td>
		    <td><%=score%></td>
		    <td>

<a href="#"
      onclick="javascript:window.open('<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=target_scheme%>&code=<%=target_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
     <%=target_code%>
</a>

<a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(target_scheme)%>&version=<%=target_version%>&code=<%=target_code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=target_code%>" />
</a>
                    </td>
		    <td><%=target_name%></td>
</tr>
<tr>
		    <%
		    source_code = "C26960";
		    source_name = "Lymphosarcoma Involving Spleen";
		    rel = "SY";
		    score = "6";
		    target_code = "200.17";
		    target_name = "Lymphosarcoma involving spleen";
		    %>
		    <td>

<a href="#"
      onclick="javascript:window.open('<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=source_scheme%>&code=<%=source_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <%=source_code%>
</a>

<a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(source_scheme)%>&version=<%=source_version%>&code=<%=source_code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=source_code%>" />
</a>

		    </td>
		    <td><%=source_name%></td>
		    <td><%=rel%></td>
		    <td><%=score%></td>
		    <td>

<a href="#"
      onclick="javascript:window.open('<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=target_scheme%>&code=<%=target_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
     <%=target_code%>
</a>

<a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(target_scheme)%>&version=<%=target_version%>&code=<%=target_code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=target_code%>" />
</a>
                    </td>
		    <td><%=target_name%></td>
</tr>
<tr>
		    <%
		    source_code = "C26961";
		    source_name = "Hodgkin's Paragranuloma Involving Spleen";
		    rel = "SY";
		    score = "6";
		    target_code = "201.07";
		    target_name = "Hodgkin's paragranuloma involving spleen";
		    %>
		    <td>

<a href="#"
      onclick="javascript:window.open('<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=source_scheme%>&code=<%=source_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <%=source_code%>
</a>

<a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(source_scheme)%>&version=<%=source_version%>&code=<%=source_code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=source_code%>" />
</a>

		    </td>
		    <td><%=source_name%></td>
		    <td><%=rel%></td>
		    <td><%=score%></td>
		    <td>

<a href="#"
      onclick="javascript:window.open('<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=target_scheme%>&code=<%=target_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
     <%=target_code%>
</a>

<a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(target_scheme)%>&version=<%=target_version%>&code=<%=target_code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=target_code%>" />
</a>
                    </td>
		    <td><%=target_name%></td>
</tr>
<tr>
		    <%
		    source_code = "C27031";
		    source_name = "Pancreatic Endocrine Tumor";
		    rel = "RB";
		    score = "6";
		    target_code = "157.4";
		    target_name = "Malignant neoplasm of islets of Langerhans";
		    %>
		    <td>

<a href="#"
      onclick="javascript:window.open('<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=source_scheme%>&code=<%=source_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <%=source_code%>
</a>

<a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(source_scheme)%>&version=<%=source_version%>&code=<%=source_code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=source_code%>" />
</a>

		    </td>
		    <td><%=source_name%></td>
		    <td><%=rel%></td>
		    <td><%=score%></td>
		    <td>

<a href="#"
      onclick="javascript:window.open('<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=target_scheme%>&code=<%=target_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
     <%=target_code%>
</a>

<a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(target_scheme)%>&version=<%=target_version%>&code=<%=target_code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=target_code%>" />
</a>
                    </td>
		    <td><%=target_name%></td>
</tr>
<tr>
		    <%
		    source_code = "C27087";
		    source_name = "Verruca Vulgaris";
		    rel = "SY";
		    score = "6";
		    target_code = "78.1";
		    target_name = "Viral warts";
		    %>
		    <td>

<a href="#"
      onclick="javascript:window.open('<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=source_scheme%>&code=<%=source_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <%=source_code%>
</a>

<a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(source_scheme)%>&version=<%=source_version%>&code=<%=source_code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=source_code%>" />
</a>

		    </td>
		    <td><%=source_name%></td>
		    <td><%=rel%></td>
		    <td><%=score%></td>
		    <td>

<a href="#"
      onclick="javascript:window.open('<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=target_scheme%>&code=<%=target_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
     <%=target_code%>
</a>

<a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(target_scheme)%>&version=<%=target_version%>&code=<%=target_code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=target_code%>" />
</a>
                    </td>
		    <td><%=target_name%></td>
</tr>
<tr>
		    <%
		    source_code = "C27093";
		    source_name = "Stage 0 Squamous Cell Carcinoma";
		    rel = "RB";
		    score = "6";
		    target_code = "231.1";
		    target_name = "Carcinoma in situ of trachea";
		    %>
		    <td>

<a href="#"
      onclick="javascript:window.open('<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=source_scheme%>&code=<%=source_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <%=source_code%>
</a>

<a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(source_scheme)%>&version=<%=source_version%>&code=<%=source_code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=source_code%>" />
</a>

		    </td>
		    <td><%=source_name%></td>
		    <td><%=rel%></td>
		    <td><%=score%></td>
		    <td>

<a href="#"
      onclick="javascript:window.open('<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=target_scheme%>&code=<%=target_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
     <%=target_code%>
</a>

<a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(target_scheme)%>&version=<%=target_version%>&code=<%=target_code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=target_code%>" />
</a>
                    </td>
		    <td><%=target_name%></td>
</tr>
<tr>
		    <%
		    source_code = "C27093";
		    source_name = "Stage 0 Squamous Cell Carcinoma";
		    rel = "RB";
		    score = "6";
		    target_code = "233.5";
		    target_name = "Carcinoma in situ of penis";
		    %>
		    <td>

<a href="#"
      onclick="javascript:window.open('<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=source_scheme%>&code=<%=source_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <%=source_code%>
</a>

<a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(source_scheme)%>&version=<%=source_version%>&code=<%=source_code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=source_code%>" />
</a>

		    </td>
		    <td><%=source_name%></td>
		    <td><%=rel%></td>
		    <td><%=score%></td>
		    <td>

<a href="#"
      onclick="javascript:window.open('<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=target_scheme%>&code=<%=target_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
     <%=target_code%>
</a>

<a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(target_scheme)%>&version=<%=target_version%>&code=<%=target_code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=target_code%>" />
</a>
                    </td>
		    <td><%=target_name%></td>
</tr>
<tr>
		    <%
		    source_code = "C27209";
		    source_name = "Radiation-Related Malignant Neoplasm";
		    rel = "RN";
		    score = "6";
		    target_code = "199";
		    target_name = "Malignant neoplasm without specification of site";
		    %>
		    <td>

<a href="#"
      onclick="javascript:window.open('<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=source_scheme%>&code=<%=source_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <%=source_code%>
</a>

<a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(source_scheme)%>&version=<%=source_version%>&code=<%=source_code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=source_code%>" />
</a>

		    </td>
		    <td><%=source_name%></td>
		    <td><%=rel%></td>
		    <td><%=score%></td>
		    <td>

<a href="#"
      onclick="javascript:window.open('<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=target_scheme%>&code=<%=target_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
     <%=target_code%>
</a>

<a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(target_scheme)%>&version=<%=target_version%>&code=<%=target_code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=target_code%>" />
</a>
                    </td>
		    <td><%=target_name%></td>
</tr>
<tr>
		    <%
		    source_code = "C27236";
		    source_name = "Tobacco-Related Carcinoma";
		    rel = "RN";
		    score = "6";
		    target_code = "305.1";
		    target_name = "Tobacco use disorder";
		    %>
		    <td>

<a href="#"
      onclick="javascript:window.open('<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=source_scheme%>&code=<%=source_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <%=source_code%>
</a>

<a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(source_scheme)%>&version=<%=source_version%>&code=<%=source_code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=source_code%>" />
</a>

		    </td>
		    <td><%=source_name%></td>
		    <td><%=rel%></td>
		    <td><%=score%></td>
		    <td>

<a href="#"
      onclick="javascript:window.open('<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=target_scheme%>&code=<%=target_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
     <%=target_code%>
</a>

<a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(target_scheme)%>&version=<%=target_version%>&code=<%=target_code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=target_code%>" />
</a>
                    </td>
		    <td><%=target_name%></td>
</tr>
<tr>
		    <%
		    source_code = "C27246";
		    source_name = "Uterine Corpus Choriocarcinoma";
		    rel = "RN";
		    score = "6";
		    target_code = "182";
		    target_name = "Malignant neoplasm of body of uterus";
		    %>
		    <td>

<a href="#"
      onclick="javascript:window.open('<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=source_scheme%>&code=<%=source_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <%=source_code%>
</a>

<a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(source_scheme)%>&version=<%=source_version%>&code=<%=source_code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=source_code%>" />
</a>

		    </td>
		    <td><%=source_name%></td>
		    <td><%=rel%></td>
		    <td><%=score%></td>
		    <td>

<a href="#"
      onclick="javascript:window.open('<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=target_scheme%>&code=<%=target_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
     <%=target_code%>
</a>

<a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(target_scheme)%>&version=<%=target_version%>&code=<%=target_code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=target_code%>" />
</a>
                    </td>
		    <td><%=target_name%></td>
</tr>
<tr>
		    <%
		    source_code = "C27250";
		    source_name = "Uterine Corpus Adenomatoid Tumor";
		    rel = "RN";
		    score = "6";
		    target_code = "219.1";
		    target_name = "Benign neoplasm of corpus uteri";
		    %>
		    <td>

<a href="#"
      onclick="javascript:window.open('<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=source_scheme%>&code=<%=source_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <%=source_code%>
</a>

<a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(source_scheme)%>&version=<%=source_version%>&code=<%=source_code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=source_code%>" />
</a>

		    </td>
		    <td><%=source_name%></td>
		    <td><%=rel%></td>
		    <td><%=score%></td>
		    <td>

<a href="#"
      onclick="javascript:window.open('<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=target_scheme%>&code=<%=target_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
     <%=target_code%>
</a>

<a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(target_scheme)%>&version=<%=target_version%>&code=<%=target_code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=target_code%>" />
</a>
                    </td>
		    <td><%=target_name%></td>
</tr>
<tr>
		    <%
		    source_code = "C27262";
		    source_name = "Myelodysplastic/Myeloproliferative Neoplasm";
		    rel = "RB";
		    score = "6";
		    target_code = "205.2";
		    target_name = "Myeloid leukemia, subacute";
		    %>
		    <td>

<a href="#"
      onclick="javascript:window.open('<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=source_scheme%>&code=<%=source_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <%=source_code%>
</a>

<a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(source_scheme)%>&version=<%=source_version%>&code=<%=source_code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=source_code%>" />
</a>

		    </td>
		    <td><%=source_name%></td>
		    <td><%=rel%></td>
		    <td><%=score%></td>
		    <td>

<a href="#"
      onclick="javascript:window.open('<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=target_scheme%>&code=<%=target_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
     <%=target_code%>
</a>

<a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(target_scheme)%>&version=<%=target_version%>&code=<%=target_code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=target_code%>" />
</a>
                    </td>
		    <td><%=target_name%></td>
</tr>
<tr>
		    <%
		    source_code = "C27280";
		    source_name = "Secondary Myelodysplastic Syndrome";
		    rel = "RN";
		    score = "6";
		    target_code = "238.75";
		    target_name = "Myelodysplastic syndrome, unspecified";
		    %>
		    <td>

<a href="#"
      onclick="javascript:window.open('<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=source_scheme%>&code=<%=source_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <%=source_code%>
</a>

<a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(source_scheme)%>&version=<%=source_version%>&code=<%=source_code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=source_code%>" />
</a>

		    </td>
		    <td><%=source_name%></td>
		    <td><%=rel%></td>
		    <td><%=score%></td>
		    <td>

<a href="#"
      onclick="javascript:window.open('<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=target_scheme%>&code=<%=target_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
     <%=target_code%>
</a>

<a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(target_scheme)%>&version=<%=target_version%>&code=<%=target_code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=target_code%>" />
</a>
                    </td>
		    <td><%=target_name%></td>
</tr>
<tr>
		    <%
		    source_code = "C27358";
		    source_name = "Recurrent Hematologic Malignancy";
		    rel = "RB";
		    score = "6";
		    target_code = "204.12";
		    target_name = "Lymphoid leukemia, chronic in relapse";
		    %>
		    <td>

<a href="#"
      onclick="javascript:window.open('<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=source_scheme%>&code=<%=source_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <%=source_code%>
</a>

<a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(source_scheme)%>&version=<%=source_version%>&code=<%=source_code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=source_code%>" />
</a>

		    </td>
		    <td><%=source_name%></td>
		    <td><%=rel%></td>
		    <td><%=score%></td>
		    <td>

<a href="#"
      onclick="javascript:window.open('<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=target_scheme%>&code=<%=target_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
     <%=target_code%>
</a>

<a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(target_scheme)%>&version=<%=target_version%>&code=<%=target_code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=target_code%>" />
</a>
                    </td>
		    <td><%=target_name%></td>
</tr>
<tr>
		    <%
		    source_code = "C27359";
		    source_name = "Unresectable Malignant Neoplasm";
		    rel = "RN";
		    score = "6";
		    target_code = "199";
		    target_name = "Malignant neoplasm without specification of site";
		    %>
		    <td>

<a href="#"
      onclick="javascript:window.open('<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=source_scheme%>&code=<%=source_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <%=source_code%>
</a>

<a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(source_scheme)%>&version=<%=source_version%>&code=<%=source_code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=source_code%>" />
</a>

		    </td>
		    <td><%=source_name%></td>
		    <td><%=rel%></td>
		    <td><%=score%></td>
		    <td>

<a href="#"
      onclick="javascript:window.open('<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=target_scheme%>&code=<%=target_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
     <%=target_code%>
</a>

<a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(target_scheme)%>&version=<%=target_version%>&code=<%=target_code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=target_code%>" />
</a>
                    </td>
		    <td><%=target_name%></td>
</tr>
<tr>
		    <%
		    source_code = "C27367";
		    source_name = "Adult Anaplastic Large Cell Lymphoma";
		    rel = "RN";
		    score = "6";
		    target_code = "200.6";
		    target_name = "Anaplastic large cell lymphoma";
		    %>
		    <td>

<a href="#"
      onclick="javascript:window.open('<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=source_scheme%>&code=<%=source_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <%=source_code%>
</a>

<a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(source_scheme)%>&version=<%=source_version%>&code=<%=source_code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=source_code%>" />
</a>

		    </td>
		    <td><%=source_name%></td>
		    <td><%=rel%></td>
		    <td><%=score%></td>
		    <td>

<a href="#"
      onclick="javascript:window.open('<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=target_scheme%>&code=<%=target_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
     <%=target_code%>
</a>

<a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(target_scheme)%>&version=<%=target_version%>&code=<%=target_code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=target_code%>" />
</a>
                    </td>
		    <td><%=target_name%></td>
</tr>



          </table>

          <%@ include file="/pages/templates/pagination-mapping.jsp" %>
          
        </div>
      </div>
    </div>
  </f:view>
</body>
</html>

