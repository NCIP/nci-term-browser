<%--L
  Copyright Northrop Grumman Information Technology.

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
L--%>

<%@ page import="java.util.*"%>
<%@ page import="gov.nih.nci.evs.browser.formatter.*"%>
<%@ page import="gov.nih.nci.evs.browser.properties.*"%>
<%@ page import="gov.nih.nci.evs.browser.utils.*"%>

<%!
  private static String ncitUrl = NCItBrowserProperties.getNCIT_URL(); 
%>

<%
  String report = HTTPUtils.cleanXSS((String) request.getParameter("report"));
  String value = "Warning: " + HTTPUtils.cleanXSS(report) + " is an invalid report.";

  try {
    Vector<StandardFtpReportInfo> list =
      NCItBrowserProperties.getStandardFtpReportInfoList();
    StandardFtpReportInfo selectedReport = StandardFtpReportInfo.getByName(
      list, report);
    if (selectedReport != null) {
      String url = selectedReport.getUrl();
      int[] cols = Utils.toInts(selectedReport.getNcitColumns());
        value = UrlAsciiToHtmlFormatter.generate(url, cols, ncitUrl);
    }
  } catch (Exception e) {
    value = e.getClass().getSimpleName() + ": " + e.getMessage();      
  }
%>

<%=value%>
