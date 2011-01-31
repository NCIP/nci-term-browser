<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=windows-1252"%>
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

<%@ page import="org.lexgrid.valuesets.LexEVSValueSetDefinitionServices" %>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns:c="http://java.sun.com/jsp/jstl/core">
<head>
  <title>NCI Thesaurus</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
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
  <script type="text/javascript">
  
    function refresh() {
      
      var selectValueSetSearchOptionObj = document.forms["valueSetSearchForm"].selectValueSetSearchOption;
      for (var i=0; i<selectValueSetSearchOptionObj.length; i++) {
        if (selectValueSetSearchOptionObj[i].checked) {
            selectValueSetSearchOption = selectValueSetSearchOptionObj[i].value;
        }
      }

      window.location.href="/ncitbrowser/pages/value_set_search.jsf?refresh=1"
          + "&opt="+ selectValueSetSearchOption;

    }
  </script>
  <%!
    private static Logger _logger = Utils.getJspLogger("value_set_search.jsp");
  %>
  <f:view>
    <!-- Begin Skip Top Navigation -->
      <a href="#evs-content" class="hideLink" accesskey="1" title="Skip repetitive navigation links">skip navigation links</A>
    <!-- End Skip Top Navigation -->  
    <%@ include file="/pages/templates/header.jsp" %>
    <div class="center-page">
      <%@ include file="/pages/templates/sub-header.jsp" %>
      <!-- Main box -->
      <div id="main-area">
      <%@ include file="/pages/templates/content-header-valueset.jsp" %> 
<%

  String valueSetSearch_requestContextPath = request.getContextPath();
  valueSetSearch_requestContextPath = valueSetSearch_requestContextPath.replace("//ncitbrowser//ncitbrowser", "//ncitbrowser");

String name = null;
String uri = null;
String description = null;
String domain = null;
String src_str = null;


String conceptDomain = (String) request.getSession().getAttribute("conceptDomain");
System.out.println("value_set_search.jsp conceptDomain: " + conceptDomain);
if (conceptDomain == null) conceptDomain = "";

    String message = (String) request.getSession().getAttribute("message");
    request.getSession().removeAttribute("message");
    String t = null;

%>
        <div class="pagecontent">
          <a name="evs-content" id="evs-content"></a>
          
          <%@ include file="/pages/templates/navigationTabs.jsp"%>
          <div class="tabTableContentContainer">
          
            <span class="textbody">&nbsp;All value sets available on the server are listed below. 
            You may search value sets by code, name, source, or coding scheme. Click on the Help (i.e., the question mark) icon above for instructions on
            how to perform each type of search.
            <br/><br/><br/>
            </span>
          
          <table>
          
<tr class="textbody">
<td align="left">

<%
Vector vsd_vec = (Vector) request.getSession().getAttribute("vsddata");
if (vsd_vec == null) {
     vsd_vec = DataUtils.getValueSetDefinitionMetadata();
     request.getSession().setAttribute("vsddata", vsd_vec);
}
		
String view = (String) request.getParameter("view");
if (view == null) {
    view = "URI";
}
%>

<%
if (view.compareToIgnoreCase("URI") == 0) {
%>
    URI View
<%    
} else {
%>
    <a href="<%=request.getContextPath() %>/pages/value_set_search.jsf?view=uri">URI View</a>
<%
} 
%>
&nbsp; | 
<%
if (view.compareToIgnoreCase("terminology") == 0) {
%>
    Terminology View
<%
} else {
%>
    <a href="<%=request.getContextPath() %>/pages/value_set_search.jsf?view=terminology">Terminology View</a>
<%
} 
%>
&nbsp; | 
<%
if (view.compareToIgnoreCase("source") == 0) {
%>
    Bibliographic Resource View
<%
} else {
%>
    <a href="<%=request.getContextPath() %>/pages/value_set_search.jsf?view=source">Bibliographic Resource View</a>
<%
} 
%>


</td></tr>      
			     

            <% if (message != null) { 
                request.getSession().removeAttribute("message");
            %>
		<tr class="textbodyred"><td>
	            <p class="textbodyred">&nbsp;<%=message%></p>
		</td></tr>
            <% 
            } 
            %>

         <tr class="textbody"><td>
            
 <%           
 if (view.compareToIgnoreCase("URI") == 0) {           
 %>          
            
              <table class="dataTable" summary="" cellpadding="3" cellspacing="0" border="0" width="100%">

		<th class="dataTableHeader" scope="col" align="left">Name</th>
		<th class="dataTableHeader" scope="col" align="left">URI</th>
                <th class="dataTableHeader" scope="col" align="left">Description</th>

<%


                for (int i=0; i<vsd_vec.size(); i++) {
            
		    String vsd_str = (String) vsd_vec.elementAt(i);
		    Vector u = DataUtils.parseData(vsd_str);
		    
		    name = (String) u.elementAt(0);
		    uri = (String) u.elementAt(1);
		    description = (String) u.elementAt(2);
		    //domain = (String) u.elementAt(3);
		    //src_str = (String) u.elementAt(4);

		    if (i % 2 == 0) {
		    %>
		      <tr class="dataRowDark">
		    <%
			} else {
		    %>
		      <tr class="dataRowLight">
		    <%
			}
		    %>    

				
		      <td class="dataCellText">
                         <a href="<%=request.getContextPath() %>/pages/value_set_search_results.jsf?nav_type=valuesets&&uri=<%=uri%>"><%=name%></a>
		      </td>
		      <td class="dataCellText">
                         <a href="<%=request.getContextPath() %>/pages/value_set_search_results.jsf?nav_type=valuesets&&uri=<%=uri%>"><%=uri%></a>
		      </td>		      
		      
		      <td class="dataCellText">
			 <%=description%>
		      </td>

		      </tr>
              
              
             <%
                }
             %>                 
                  
              </table>
            
<%           
 } else if (view.compareToIgnoreCase("terminology") == 0) { 
     HashMap csURN2ValueSetMetadataHashMap = DataUtils.getCodingSchemeURN2ValueSetMetadataHashMap(vsd_vec); 
     Iterator it = csURN2ValueSetMetadataHashMap.keySet().iterator();
     
     
%>          
             
               <table class="dataTable" summary="" cellpadding="3" cellspacing="0" border="0" width="100%">
                <th class="dataTableHeader" scope="col" align="left">Terminology</th>
 		<th class="dataTableHeader" scope="col" align="left">Name</th>
 		<th class="dataTableHeader" scope="col" align="left">URI</th>
                <th class="dataTableHeader" scope="col" align="left">Description</th>
 
 <%
 
 
        while (it.hasNext()) {
                String cs = (String) it.next();
                
 System.out.println("CS: " + cs);               
                
 		Vector vsd_vector = (Vector) csURN2ValueSetMetadataHashMap.get(cs);
                 
                 for (int i=0; i<vsd_vector.size(); i++) {
             
 		    String vsd_string = (String) vsd_vector.elementAt(i);
 		    Vector u = DataUtils.parseData(vsd_string);
 		    
 		    name = (String) u.elementAt(0);
 		    uri = (String) u.elementAt(1);
 		    description = (String) u.elementAt(2);
 
 		    if (i % 2 == 0) {
 		    %>
 		      <tr class="dataRowDark">
 		    <%
 			} else {
 		    %>
 		      <tr class="dataRowLight">
 		    <%
 			}
 		    %>    
 
  		      <td class="dataCellText">
  			 <%=cs%>
 		      </td>
 				
 		      <td class="dataCellText">
                          <a href="<%=request.getContextPath() %>/pages/value_set_search_results.jsf?nav_type=valuesets&&uri=<%=uri%>"><%=name%></a>
 		      </td>
 		      <td class="dataCellText">
                          <a href="<%=request.getContextPath() %>/pages/value_set_search_results.jsf?nav_type=valuesets&&uri=<%=uri%>"><%=uri%></a>
 		      </td>		      
 		      
 		      <td class="dataCellText">
 			 <%=description%>
 		      </td>
 
 		      </tr>
               
               
              <%
                 }
         }
              %>                 
                   
               </table>
             
 <%           
  } else if (view.compareToIgnoreCase("source") == 0) { 
     HashMap src2ValueSetMetadataHashMap = DataUtils.getSource2ValueSetMetadataHashMap(vsd_vec); 
     Iterator it = src2ValueSetMetadataHashMap.keySet().iterator();
     
     
%>          
             
               <table class="dataTable" summary="" cellpadding="3" cellspacing="0" border="0" width="100%">
                <th class="dataTableHeader" scope="col" align="left">Resource</th>
 		<th class="dataTableHeader" scope="col" align="left">Name</th>
 		<th class="dataTableHeader" scope="col" align="left">URI</th>
                <th class="dataTableHeader" scope="col" align="left">Description</th>
 
 <%
 
 
        while (it.hasNext()) {
                String resource = (String) it.next();
               
 		Vector vsd_vector = (Vector) src2ValueSetMetadataHashMap.get(resource);
 
                 for (int i=0; i<vsd_vector.size(); i++) {
             
 		    String vsd_string = (String) vsd_vector.elementAt(i);
 		    Vector u = DataUtils.parseData(vsd_string);
 		    
 		    name = (String) u.elementAt(0);
 		    uri = (String) u.elementAt(1);
 		    description = (String) u.elementAt(2);
 
 		    if (i % 2 == 0) {
 		    %>
 		      <tr class="dataRowDark">
 		    <%
 			} else {
 		    %>
 		      <tr class="dataRowLight">
 		    <%
 			}
 		    %>    
 
  		      <td class="dataCellText">
  			 <%=resource%>
 		      </td>
 				
 		      <td class="dataCellText">
                          <a href="<%=request.getContextPath() %>/pages/value_set_search_results.jsf?nav_type=valuesets&&uri=<%=uri%>"><%=name%></a>
 		      </td>
 		      <td class="dataCellText">
                          <a href="<%=request.getContextPath() %>/pages/value_set_search_results.jsf?nav_type=valuesets&&uri=<%=uri%>"><%=uri%></a>
 		      </td>		      
 		      
 		      <td class="dataCellText">
 			 <%=description%>
 		      </td>
 
 		      </tr>
               
               
              <%
                 }
         }
              %>                 
                   
               </table>
             
 <%           
  }          
 %>   
 
            
            
          </td>
          </tr>
        </table>
        </div> <!-- end tabTableContentContainer -->
        
        <%@ include file="/pages/templates/nciFooter.jsp" %>
      </div>
      <!-- end Page content -->
    </div>
    <div class="mainbox-bottom"><img src="<%=basePath%>/images/mainbox-bottom.gif" width="745" height="5" alt="Mainbox Bottom" /></div>
    <!-- end Main box -->
  </div>
</f:view>
</body>
</html>
