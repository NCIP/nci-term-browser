<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*"%>
<%@ page import="org.LexGrid.concepts.Entity" %>
<%@ page import="gov.nih.nci.evs.browser.bean.*" %>
<%@ page import="gov.nih.nci.evs.browser.utils.*" %>
<%@ page import="gov.nih.nci.evs.browser.properties.*" %>
<%@ page import="gov.nih.nci.evs.browser.utils.*" %>
<%@ page import="javax.faces.context.FacesContext" %>
<%@ page import="org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference" %>
<%@ page import="org.apache.log4j.*" %>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns:c="http://java.sun.com/jsp/jstl/core">
<head>
  <title>NCI Term Browser</title>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />
  <link rel="shortcut icon" href="<%= request.getContextPath() %>/favicon.ico" type="image/x-icon" />
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/script.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/search.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/dropdown.js"></script>
</head>
<body>
  <script type="text/javascript"
    src="<%=request.getContextPath()%>/js/wz_tooltip.js"></script>
  <script type="text/javascript"
    src="<%=request.getContextPath()%>/js/tip_centerwindow.js"></script>
  <script type="text/javascript"
    src="<%=request.getContextPath()%>/js/tip_followscroll.js"></script>


  <%!
    private static Logger _logger = Utils.getJspLogger("cartVersionSelection.jsp");
  %>
  <f:view>
    <!-- Begin Skip Top Navigation -->
      <a href="#evs-content" class="hideLink" accesskey="1" title="Skip repetitive navigation links">skip navigation links</A>
    <!-- End Skip Top Navigation -->  
    <%@ include file="/pages/templates/header.jsp" %>
    <div class="center-page_960">
      <%@ include file="/pages/templates/sub-header.jsp" %>
      <!-- Main box -->
      <div id="main-area_960">
        <%@ include file="/pages/templates/content-header-no-searchbox.jsp" %>
        
<%

String message = (String) request.getSession().getAttribute("message");  
request.getSession().removeAttribute("message");  

String export_format = (String) request.getSession().getAttribute("format"); 
if (export_format == null) export_format = "XML";

Vector coding_scheme_ref_vec = (Vector) request.getSession().getAttribute("cart_coding_scheme_ref_vec");

String checked = "";

String prev_cs_urn = "";

%>
        <div class="pagecontent">
          <a name="evs-content" id="evs-content"></a>
          <%-- 0 <%@ include file="/pages/templates/navigationTabs.jsp"%> --%>
          <div class="tabTableContentContainer">
                    
          <table>
            <% if (message != null)  { 
                request.getSession().removeAttribute("message");
            %>
            
        <tr class="textbodyred"><td>
      <p class="textbodyred">&nbsp;<%=message%></p>
        </td></tr>
            <% } %>

            <tr class="textbody"><td>

 <h:form id="resolveValueSetForm" styleClass="search-form" acceptcharset="UTF-8">            
               
              <table class="dataTable" summary="" cellpadding="3" cellspacing="0" border="0">
                <tr>                
                   <th class="dataTableHeader" scope="col" align="left" width="25px">&nbsp;</th>
                   <th class="dataTableHeader" scope="col" align="left">Coding Scheme</th>
                   <th class="dataTableHeader" scope="col" align="left">Version</th>
                   <th class="dataTableHeader" scope="col" align="left">Tag</th>
                </tr>
<%
if (coding_scheme_ref_vec != null) {
int k = -1;

            for (int i=0; i<coding_scheme_ref_vec.size(); i++) {
            
		    String coding_scheme_ref_str = (String) coding_scheme_ref_vec.elementAt(i);
int lcv = i+1;		    
	    
		    
		    String coding_scheme_name_version = coding_scheme_ref_str;
		    
		    Vector u = StringUtils.parseData(coding_scheme_ref_str);
		    String cs_name = (String) u.elementAt(0);
		    String displayed_cs_name = DataUtils.uri2CodingSchemeName(cs_name);
		    
		    //cs_name = DataUtils.uri2CodingSchemeName(cs_name); 
		    
		    
		    String cs_version = (String) u.elementAt(1);
		    
		    String cs_tag = DataUtils.getVocabularyVersionTag(cs_name, cs_version);
		    if (cs_tag == null) cs_tag = "";
		    
		    if (cs_name.compareTo(prev_cs_urn) != 0) {
		       k++;
		       prev_cs_urn = cs_name;
		    }
		    
		    if (coding_scheme_ref_vec.size() == 1) {
		        checked = "checked";
		    } else if (cs_tag.compareToIgnoreCase("PRODUCTION") == 0) {
		        checked = "checked";
		    }
	    
        
		    if (k % 2 == 0) {
		    %>
		      <tr class="dataRowDark">
		    <%
			} else {
		    %>
		      <tr class="dataRowLight">
		    <%
			}
		    %>    



		<td scope="row">
<input type="radio" name="<%=cs_name%>" value="<%=cs_version%>" <%=checked%> tabinex="1" />
		</td>


	
		      <td class="dataCellText">
			 <%=displayed_cs_name%>
		      </td>
		      <td class="dataCellText">
			 <%=cs_version%>
		      </td>
		      <td class="dataCellText">
			 <%=cs_tag%>
		      </td>		      

        
		      </tr>
              
             <%
                }
} else {
%>
<tr><td>
<p class="textbodyred">&nbsp;WARNING: Unable to retrieve coding scheme reference data from the server.</p>
</td></tr>
<%
}
             %>                 


              </table>

<%
if (export_format.compareTo("XML") == 0) {
%>
                  <tr><td>
                    <h:commandButton id="continue_resolve" value="continue_resolve" action="#{CartActionBean.exportCartXML}"
                      onclick="javascript:cursor_wait();"
                      image="#{valueSetSearch_requestContextPath}/images/continue.gif"
                      alt="Resolve"
                      tabindex="2">
                    </h:commandButton>
                  </td></tr>
 <%             
 } else {
 %>
 
                   <tr><td>
                     <h:commandButton id="continue_resolve" value="continue_resolve" action="#{CartActionBean.exportCartCSV}"
                       onclick="javascript:cursor_wait();"
                       image="#{valueSetSearch_requestContextPath}/images/continue.gif"
                       alt="Resolve"
                       tabindex="2">
                     </h:commandButton>
                  </td></tr>
 
 <%
 }
 %>             
              
              <input type="hidden" name="format" value="<%=export_format%>">
              <input type="hidden" name="referer" id="referer" value="<%=HTTPUtils.getRefererParmEncode(request)%>">
</h:form>
            
          </td></tr>
        </table>
        </div> <!-- end tabTableContentContainer -->
        <%@ include file="/pages/templates/nciFooter.jsp" %>
      </div>
      <!-- end Page content -->
    </div>
    <div class="mainbox-bottom"><img src="<%=basePath%>/images/mainbox-bottom.gif" width="945" height="5" alt="Mainbox Bottom" /></div>
    <!-- end Main box -->
  </div>
</f:view>
</body>
</html>
