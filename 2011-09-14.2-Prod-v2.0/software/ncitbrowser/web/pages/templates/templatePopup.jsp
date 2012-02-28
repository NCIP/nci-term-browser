<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="gov.nih.nci.evs.browser.utils.*" %>

<%
  String basePath = request.getContextPath();
  String ncit_build_info = new DataUtils().getNCITBuildInfo();
  String application_version = new DataUtils().getApplicationVersion();
  String anthill_build_tag_built = new DataUtils().getNCITAnthillBuildTagBuilt();
  String evs_service_url = new DataUtils().getEVSServiceURL();
  String content_title = HTTPUtils.cleanXSS((String) request.getParameter("content_title"));
  String content_page = HTTPUtils.cleanXSS((String) request.getParameter("content_page"));
  String display_app_logo = HTTPUtils.cleanXSS((String) request.getParameter("display_app_logo"));
  boolean is_display_app_logo = display_app_logo != null
    && display_app_logo.equalsIgnoreCase("true");

  JSPUtils.JSPHeaderInfoMore hierarchy_info = new JSPUtils.JSPHeaderInfoMore(request);
  String hierarchy_dictionary = hierarchy_info.dictionary;
  String hierarchy_version = hierarchy_info.version;
  String hierarchy_schema = HTTPUtils.cleanXSS((String) request.getParameter("schema"));
  if (hierarchy_dictionary != null && hierarchy_schema == null)
    hierarchy_schema = hierarchy_dictionary;
  String display_name = hierarchy_info.display_name;
%>
<!--
   Build info: <%=ncit_build_info%>
 Version info: <%=application_version%>
          Tag: <%=anthill_build_tag_built%>
   LexEVS URL: <%=evs_service_url%>
  -->

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns:c="http://java.sun.com/jsp/jstl/core">
  <head>
    <title><%=content_title%></title>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />
    <link rel="shortcut icon" href="<%= request.getContextPath() %>/favicon.ico" type="image/x-icon" />
    <script type="text/javascript" src="<%= request.getContextPath() %>/js/script.js"></script>
  </head>
  <body>
    <div id="popupContainer">
      <!-- nci popup banner -->
      <div class="ncipopupbanner">
        <a href="http://www.cancer.gov" target="_blank"><img src="<%=basePath%>/images/nci-banner-1.gif" width="440" height="39" border="0" alt="National Cancer Institute" /></a>
        <a href="http://www.cancer.gov" target="_blank"><img src="<%=basePath%>/images/spacer.gif" width="48" height="39" border="0" alt="National Cancer Institute" class="print-header" /></a>
      </div>
      <!-- end nci popup banner -->
      <div id="popupMainArea_Elastic">
        <table class="evsLogoBg" cellspacing="0" cellpadding="0" border="0">
        <tr>
          <td valign="top">
            <a href="http://evs.nci.nih.gov/" target="_blank">
              <img src="<%=basePath%>/images/evs-popup-logo.gif" width="213" height="26" alt="EVS: Enterprise Vocabulary Services" title="EVS: Enterprise Vocabulary Services" border="0" />
            </a>
          </td>
          <td valign="top"><div id="closeWindow"><a href="javascript:window.close();"><img src="<%=basePath%>/images/thesaurus_close_icon.gif" width="10" height="10" border="0" alt="Close Window" />&nbsp;CLOSE WINDOW</a></div></td>
        </tr>
        </table>
      </div>
      <% if (is_display_app_logo) { %>
        <div>
          <table background="<%=basePath%>/images/termbrowser_popup_banner-middle.gif" cellspacing="0" cellpadding="0" border="0">
            <tr>
              <td width="1">
                <% if (hierarchy_schema == null) { %>
                  <img src="<%=basePath%>/images/termbrowser_popup_banner-left.gif" alt="NCI Term Browser Banner" title="" border="0" />
                <% } else if (hierarchy_schema.compareTo("NCI Thesaurus") == 0) { %>
                  <img src="<%=basePath%>/images/thesaurus_popup_banner-left.gif" alt="NCI Thesaurus Banner" title="" border="0" />
                <% } else { %>
                  <img src="<%=basePath%>/images/other_popup_banner-left.gif" alt="Other Banner" title="" border="0" />
                  <div class="vocabularynamepopupshort"><%=HTTPUtils.cleanXSS(display_name)%></div>
                <% } %>
              </td>
              <td width="100%"><%-- intentionally left blank --%></td>
              <td width="1">
                <img src="<%=basePath%>/images/termbrowser_popup_banner-right.gif" alt="NCI Thesaurus" title="" border="0" />
              </td>
            </tr>
          </table>
        </div>
      <% } %>
      <jsp:include page="<%=content_page%>" />
    </div>
  </body>
</html>
