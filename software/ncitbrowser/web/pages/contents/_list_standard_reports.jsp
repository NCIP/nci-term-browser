<%@ page import="gov.nih.nci.evs.browser.formatter.*"%>

<%!
  private static final String POPUP_ARGS = 
      "'_blank', 'top=100, left=100, height=740, width=680, status=no," +
      " menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no," +
      " directories=no'";
%>

<%
  String page2 = request.getContextPath() + "/pages/display_standard_report.jsf";
%>

The following reports are found in the Terminology Subset Reports
<a href="<%=FormatterConstant.FTP_URL%>">download</a> page:
<ul>
  <li><a href="#" onclick="javascript:window.open('<%=page2%>?report=CDISC_SDTM', <%=POPUP_ARGS%>);">CDISC SDTM Terminology</a></li>
  <li><a href="#" onclick="javascript:window.open('<%=page2%>?report=CDRH',       <%=POPUP_ARGS%>);">FDA CDRH NCIt Subsets</a></li>
  <li><a href="#" onclick="javascript:window.open('<%=page2%>?report=FDA_SPL',    <%=POPUP_ARGS%>);">FDA SPL Country Codes</a></li>
  <li><a href="#" onclick="javascript:window.open('<%=page2%>?report=FDA_UNII',   <%=POPUP_ARGS%>);">FDA UNII NCIt Subsets</a></li>
</ul>
