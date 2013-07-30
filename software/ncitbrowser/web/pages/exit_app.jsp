<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="java.util.Vector"%>
<%@ page import="gov.nih.nci.evs.browser.utils.*" %>
<%@ page import="gov.nih.nci.evs.browser.common.*" %>

<%
  String basePath = request.getContextPath();
  String ncbo_id = HTTPUtils.cleanXSS((String) request.getParameter("ncbo_id"));
  String code = HTTPUtils.cleanXSS((String) request.getParameter("code"));
  
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
  <head>
    <title>Sources</title>
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
          String dictionary = HTTPUtils.cleanXSS((String) request.getParameter("dictionary"));
          String schema = HTTPUtils.cleanXSS((String) request.getParameter("schema"));
          if (dictionary != null && schema == null)
            schema = dictionary;
          
          if (schema == null) {
             schema = "NCI Thesaurus";
          }
          
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
          <a name="evs-content" id="evs-content"></a>
          <%
            String codingScheme = dictionary;
            String header = DataUtils.getMetadataValue(
                codingScheme, "source_header");
            String footer = DataUtils.getMetadataValue(
                codingScheme, "source_footer");
          %>


          <!-- Term Type content -->
          <table width="580px" cellpadding="3" cellspacing="0" border="0">
             <tr><td class="pageTitle">
                <b>You are now leaving the term browser website.</b>
            </td></tr>
            
            <tr><td class="textbody">
Links to any non-Federal organizations are provided solely as a service to our users. 
These links do not constitute an endorsement of these organizations or their programs by the NCI or the Federal Government, 
and none should be inferred. Any reference to a commercial product, process, service, or company is not an endorsement 
or recommendation by the U.S. government, the Department of Health and Human Services, NCI or any of its components. 
The NCI is not responsible for the content of the individual organization Web pages found at these links.
             </td></tr>

<tr><td>&nbsp;</td></tr>

		<tr><td>


<a href="http://bioportal.bioontology.org/flex/BasicFlexoViz?ontology=<%=ncbo_id%>&nodeid=<%=code%>">
<img src="<%=basePath%>/images/continue.gif" alt="Continue" border="0">
</a>
		    
		&nbsp;
		
<a href="javascript:window.close();">
<img src="<%=basePath%>/images/cancel.gif" alt="Cancel" border="0">
</a>

		</td></tr>
</table>

        </div>
        <!-- End of Term Type content -->
     
        
      </div>
  </div>
  </f:view>
  </body>
</html>