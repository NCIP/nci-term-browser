<%@ page import="java.util.*"%>
<%@ page import="gov.nih.nci.evs.browser.formatter.*"%>
<%@ page import="gov.nih.nci.evs.browser.properties.*"%>
<%@ page import="gov.nih.nci.evs.browser.utils.*"%>

<%
  String report = request.getParameter("report");
  String value = "Warning: " + report + " is an invalid report.";

  try {
    Vector<StandardFtpReportInfo> list =
      NCItBrowserProperties.getStandardFtpReportInfoList();
    StandardFtpReportInfo selectedReport = StandardFtpReportInfo.getByName(
      list, report);
    if (selectedReport != null) {
      String url = selectedReport.getUrl();
      int[] cols = Utils.toInts(selectedReport.getNcitColumns());
        value = UrlAsciiToHtmlFormatter.generate(url, cols);
    }
  } catch (Exception e) {
    value = e.getClass().getSimpleName() + ": " + e.getMessage();      
  }
%>

<%=value%>
