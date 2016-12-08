<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.Vector"%>
<%@ page import="gov.nih.nci.evs.browser.utils.*" %>
<%@ page import="gov.nih.nci.evs.browser.common.*" %>

<%
  String basePath = request.getContextPath();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
  <head>
    <title>Term Type Help Informaton</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />
    <link rel="shortcut icon" href="<%= request.getContextPath() %>/favicon.ico" type="image/x-icon" />
    <script type="text/javascript" src="<%= request.getContextPath() %>/js/script.js"></script>
  </head>
  <body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" >
  <f:view>
  <!-- Begin Skip Top Navigation -->
    <a href="#evs-content" class="hideLink" accesskey="1" title="Skip repetitive navigation links">skip navigation links</A>
  <!-- End Skip Top Navigation --> 

  <div id="popupContainer">
      <!-- nci popup banner -->
      <div class="ncipopupbanner">
        <a href="http://www.cancer.gov" target="_blank" alt="National Cancer Institute"><img src="<%=basePath%>/images/banner-red.png" width="680" height="39" border="0" alt="National Cancer Institute" /></a>
        <a href="http://www.cancer.gov" target="_blank" alt="National Cancer Institute"><img src="<%=basePath%>/images/spacer.gif" width="60" height="39" border="0" alt="National Cancer Institute" class="print-header" /></a>
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
          String dictionary = HTTPUtils.cleanXSS((String) request.getParameter("dictionary"));
          String schema = HTTPUtils.cleanXSS((String) request.getParameter("schema"));
          if (dictionary != null && schema == null)
            schema = dictionary;
          
          String display_name = DataUtils.getMetadataValue(schema, "display_name");
          if (display_name == null || display_name.compareTo("null") == 0) {
            display_name = DataUtils.getLocalName(schema); 
          }
          
          if (DataUtils.isNCIT(schema)) {
        %>
            <div><img src="<%=basePath%>/images/thesaurus_popup_banner.gif"
              width="612" height="56" alt="NCI Thesaurus" title="" border="0" /></div>
        <% } else { %>
            <div>
              <img src="<%=basePath%>/images/other_popup_banner.gif"
                width="612" height="56" alt="NCI Thesaurus" title="" border="0" />
              <div class="vocabularynamepopupshort"><%=DataUtils.encodeTerm(display_name)%></div>
            </div>
        <%
          }
        %>        
        
        <div id="popupContentArea">
          <a name="evs-content" id="evs-content"></a>
          <%
            String codingScheme = dictionary;
            String header = DataUtils.getMetadataValue(
                codingScheme, "term_type_header");
            String footer = DataUtils.getMetadataValue(
                codingScheme, "term_type_footer");
          %>

          <!-- Term Type content -->
          <table width="580px" cellpadding="3" cellspacing="0" border="0">
            <tr class="pageTitle">
              <td align="left">
                <b>Term Types</b>
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
            <%=header%><br/>
          <% } %>
          <br/>
          <table width="580px" cellpadding="3" cellspacing="0" border="0">
            <tr class="dataRowDark">
              <th scope="col" align="left">Name</th>
              <th scope="col" align="left">Description</th>
            </tr>          
            <%
              Vector names = DataUtils.getMetadataValues(
                  codingScheme, "term_type_code");
              Vector descriptions = DataUtils.getMetadataValues(
                  codingScheme, "term_type_meaning");
              if (names != null && descriptions != null) {
                for (int n=0; n<names.size(); n++) {
                  String name = (String) names.elementAt(n);
                  String description = (String) descriptions.elementAt(n);
                  String rowColor = (n%2 == 1) ? "dataRowDark" : "dataRowLight";
              %>
                  <tr class="<%=rowColor%>">
                    <td scope="row"><%=name%></td>
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
