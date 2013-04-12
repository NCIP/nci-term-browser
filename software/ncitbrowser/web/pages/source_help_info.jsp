<%--L
  Copyright Northrop Grumman Information Technology.

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
L--%>

<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="java.util.Vector"%>
<%@ page import="gov.nih.nci.evs.browser.utils.*" %>
<%@ page import="gov.nih.nci.evs.browser.common.*" %>

<%
  String basePath = request.getContextPath();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
  <head>
    <title>Sources</title>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />
    <link rel="shortcut icon" href="<%= request.getContextPath() %>/favicon.ico" type="image/x-icon" />
    <script type="text/javascript" src="<%= request.getContextPath() %>/js/script.js"></script>
  </head>
  <body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" >
  <f:view>
  <div id="popupContainer">
      <!-- nci popup banner -->
      <div class="ncipopupbanner">
        <a href="http://www.cancer.gov" target="_blank" alt="National Cancer Institute"><img src="<%=basePath%>/images/nci-banner-1.gif" width="440" height="39" border="0" alt="National Cancer Institute" /></a>
        <a href="http://www.cancer.gov" target="_blank" alt="National Cancer Institute"><img src="<%=basePath%>/images/spacer.gif" width="48" height="39" border="0" alt="National Cancer Institute" class="print-header" /></a>
      </div>
      <!-- end nci popup banner -->
      <div id="popupMainArea">
        <table class="evsLogoBg" cellspacing="3" cellpadding="0" border="0" width="570px">
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
          String dictionary = request.getParameter("dictionary");
          String schema = request.getParameter("schema");
          if (dictionary != null && schema == null)
            schema = dictionary;
          
          String display_name = DataUtils.getMetadataValue(schema, "display_name");
          if (display_name == null || display_name.compareTo("null") == 0) {
            display_name = DataUtils.getLocalName(schema); 
          }
          
          if (schema.compareTo("NCI Thesaurus") == 0) {
        %>
            <div><img src="<%=basePath%>/images/thesaurus_popup_banner.gif"
              width="612" height="56" alt="NCI Thesaurus" title="" border="0" /></div>
        <% } else { %>
            <div>
              <img src="<%=basePath%>/images/other_popup_banner.gif"
                width="612" height="56" alt="NCI Thesaurus" title="" border="0" />
              <div class="vocabularynamepopupshort"><%=display_name%></div>
            </div>
        <%
          }
        %>        

        <div id="popupContentArea">

          <%
            String codingScheme = dictionary;
            String header = DataUtils.getMetadataValue(
                codingScheme, "source_header");
            String footer = DataUtils.getMetadataValue(
                codingScheme, "source_footer");
          %>

          <!-- Term Type content -->
          <table width="580px" cellpadding="3" cellspacing="0" border="0">
            <tr class="pageTitle">
              <td align="left">
                <b>Sources</b>
              </td>
              <td align="right">
                <font size="1" color="red" align="right">
                  <a href="javascript:printPage()"><img src="<%= request.getContextPath() %>/images/printer.bmp" border="0" alt="Send to Printer" ><i>Send to Printer</i></a>
                </font>
              </td>
            </tr>
          </table>
          <hr/>
          
          <% if (header != null) { %>
            <%=header%>
          <% } %>
          <br/>
          <table width="580px" cellpadding="3" cellspacing="0" border="0">
            <%
              Vector names = DataUtils.getMetadataValues(
                  codingScheme, "source_name");
              Vector descriptions = DataUtils.getMetadataValues(
                  codingScheme, "source_description");
              if (names != null && descriptions != null) {
                for (int n=0; n<names.size(); n++) {
                  String name = (String) names.elementAt(n);
                  String description = (String) descriptions.elementAt(n);
                  String rowColor = (n%2 == 0) ? "dataRowDark" : "dataRowLight";
              %>
                  <tr class="<%=rowColor%>">
                    <td><%=name%></td>
                    <td><%=description%></td>
                  </tr>
              <%
                }
              }
            %>
          </table>
          <br/>
          <% if (footer != null) { %>
            <%=footer%>
          <% } %>

        </div>
        <!-- End of Term Type content -->
      </div>
  </div>
  </f:view>
  </body>
</html>