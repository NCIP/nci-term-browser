<%@ page import="java.util.*"%>
<%@ page import="gov.nih.nci.evs.browser.formatter.*"%>
<%@ page import="gov.nih.nci.evs.browser.properties.*"%>

<%!
  private static final String POPUP_ARGS = 
      "'_blank', 'top=100, left=100, height=740, width=680, status=no," +
      " menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no," +
      " directories=no'";
%>

<%
  String page2 = request.getContextPath() + "/pages/display_standard_report.jsf";
  String ftpUrl = NCItBrowserProperties.getStandardFtpReportUrl();
  Vector<StandardFtpReportInfo> list =
      NCItBrowserProperties.getStandardFtpReportInfoList();
%>

The following reports are found in the Terminology Subset Reports
<a href="#" onclick="javascript:window.open('<%=ftpUrl%>', <%=POPUP_ARGS%>);">download</a>
page:
<ul>
  <% 
    Iterator<StandardFtpReportInfo> iterator = list.iterator();
    while (iterator.hasNext()) {
      StandardFtpReportInfo info = iterator.next();
  %>
      <li><a href="#" onclick="javascript:window.open('<%=page2%>?report=<%=info.getName()%>', <%=POPUP_ARGS%>);"><%=info.getName()%></a></li>
  <%
    }
  %>
</ul>
