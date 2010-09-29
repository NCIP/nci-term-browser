<%@ page import="gov.nih.nci.evs.browser.formatter.*"%>

<%
  String report = request.getParameter("report");
  
  String url = null;
  int[] cols = null;
  
  if (report == null) {
      ;
  } else if (report.equals("CDISC_SDTM")) {
      url = FormatterConstant.CDISC_SDTM_REPORT_URL;
      cols = FormatterConstant.CDISC_SDTM_NCIT_COLUMNS;
  } else if (report.equals("CDRH")) {
      url = FormatterConstant.CDRH_REPORT_URL;
      cols = FormatterConstant.CDRH_NCIT_COLUMNS;
  } else if (report.equals("FDA_SPL")) {
      url = FormatterConstant.FDA_SPL_REPORT_URL;
      cols = FormatterConstant.FDA_SPL_NCIT_COLUMNS;
  } else if (report.equals("FDA_UNII")) {
      url = FormatterConstant.FDA_UNII_REPORT_URL;
      cols = FormatterConstant.FDA_UNII_NCIT_COLUMNS;
  }
  
  String value = "Warning: " + report + " is an invalid report.";
  if (url != null)
      value = UrlAsciiToHtmlFormatter.generate(url, cols);
%>
<%=value%>
