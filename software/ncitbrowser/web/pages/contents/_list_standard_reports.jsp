<%@ page import="gov.nih.nci.evs.browser.formatter.*"%>

<%
  String page2 = request.getContextPath() + "/pages/display_standard_report.jsf";
%>

The following reports are found in the Terminology Subset Reports
<a href="<%=FormatterConstant.FTP_URL%>">download</a> page:
<ul>
  <li><a href="<%= page2 %>?report=CDISC_SDTM">CDISC SDTM Terminology</a></li>
  <li><a href="<%= page2 %>?report=CDRH">FDA CDRH NCIt Subsets</a></li>
  <li><a href="<%= page2 %>?report=FDA_SPL">FDA SPL Country Codes</a></li>
  <li><a href="<%= page2 %>?report=FDA_UNII">FDA UNII NCIt Subsets</a></li>
</ul>
