<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="java.util.Vector"%>
<%@ page import="org.LexGrid.concepts.Entity" %>
<%@ page import="gov.nih.nci.evs.browser.common.Constants" %>
<%@ page import="gov.nih.nci.evs.browser.utils.*" %>
<%@ page import="gov.nih.nci.evs.browser.bean.IteratorBean" %>
<%@ page import="org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference" %>
<%@ page import="javax.faces.context.FacesContext" %>
<%@ page import="org.apache.log4j.*" %>
<%@ page import="gov.nih.nci.evs.browser.utils.*" %>
<%@ page import="org.lexgrid.valuesets.LexEVSValueSetDefinitionServices" %>

<script type="text/javascript" src="<%= request.getContextPath() %>/js/yui/yahoo-min.js" ></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/yui/event-min.js" ></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/yui/dom-min.js" ></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/yui/animation-min.js" ></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/yui/container-min.js" ></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/yui/connection-min.js" ></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/yui/autocomplete-min.js" ></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/yui/treeview-min.js" ></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/dropdown.js"></script>

<% String vsBasePath = request.getContextPath(); %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns:c="http://java.sun.com/jsp/jstl/core">
<head>
  <title>NCI Term Browser - Value Set Search</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />
  <link rel="shortcut icon" href="<%= request.getContextPath() %>/favicon.ico" type="image/x-icon" />
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/yui/fonts.css" />
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/yui/grids.css" />
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/yui/code.css" />
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/yui/tree.css" />
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/script.js"></script>
  <script type="text/javascript">
    function refresh() {      
      var selectValueSetSearchOptionObj = document.forms["valueSetSearchForm"].selectValueSetSearchOption;
      for (var i=0; i<selectValueSetSearchOptionObj.length; i++) {
        if (selectValueSetSearchOptionObj[i].checked) {
            selectValueSetSearchOption = selectValueSetSearchOptionObj[i].value;
        }
      }
      
      var view = document.forms["view_form"].view.value;
     
      if (view == "source") {
          window.location.href="/ncitbrowser/pages/value_set_source_view.jsf?refresh=1"
              + "&nav_type=valuesets" + "&opt="+ selectValueSetSearchOption;
      
      } else {
          window.location.href="/ncitbrowser/pages/value_set_terminology_view.jsf?refresh=1"
              + "&nav_type=valuesets" + "&opt="+ selectValueSetSearchOption;
      }
    }
  </script>
</head>
<body onLoad="document.forms.valueSetSearchForm.matchText.focus();">
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/wz_tooltip.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/tip_centerwindow.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/tip_followscroll.js"></script>
<%!
  private static Logger _logger = Utils.getJspLogger("value_set_search_results.jsp");
%>

    
    
<form id="view_form">
<%
     String VSD_view = (String) request.getSession().getAttribute("view");
%>
     <input type="hidden" id="view" name="view" value="<%=VSD_view%>" />
</form>
<%


String valueSetSearch_requestContextPath = request.getContextPath();
String selected_ValueSetSearchOption = HTTPUtils.cleanXSS((String) request.getSession().getAttribute("selectValueSetSearchOption")); 
Vector vsd_vec = null;
String vsd_uri = (String) request.getParameter("vsd_uri"); 

String selectedvalueset = null;
if (vsd_uri != null && vsd_uri.compareTo("null") != 0) { 
    String vsd_metadata = DataUtils.getValueSetDefinitionMetadata(DataUtils.findValueSetDefinitionByURI(vsd_uri));
    vsd_vec = new Vector();
    vsd_vec.add(vsd_metadata);
    
} else {
    vsd_vec = (Vector) request.getSession().getAttribute("matched_vsds");
    if (vsd_vec != null && vsd_vec.size() == 1) {
	vsd_uri = (String) vsd_vec.elementAt(0);
	
	Vector temp_vec = DataUtils.parseData(vsd_uri);
	selectedvalueset = (String) temp_vec.elementAt(1);
    }
}   

  
    
    
    String searchform_requestContextPath = request.getContextPath();
    searchform_requestContextPath = searchform_requestContextPath.replace("//ncitbrowser//ncitbrowser", "//ncitbrowser");

    request.getSession().setAttribute("navigation_type", "valuesets");

    String message = (String) request.getSession().getAttribute("message");
    request.getSession().removeAttribute("message");
    
if (message == null) {
    if (vsd_vec == null) {
        message = "WARNING: Session lost. Please click on the Value Sets tab to start a new session.";
    }
}

    
    String t = null;
    
    String selected_cs = "";
    String selected_cd = null;

    String check_cs = "";
    String check_cd = "";
    String check_code = "";
    String check_name = "";
    String check_src = "";
    
    // to be modified
    String valueset_search_algorithm = null;

    String check__e = "", check__b = "", check__s = "" , check__c ="";
    if (valueset_search_algorithm == null || valueset_search_algorithm.compareTo("exactMatch") == 0)
        check__e = "checked";
    else if (valueset_search_algorithm.compareTo("startsWith") == 0)
        check__s= "checked";
    else if (valueset_search_algorithm.compareTo("DoubleMetaphoneLuceneQuery") == 0)
        check__b= "checked";
    else
        check__c = "checked";
        
    String selectValueSetSearchOption = null;
    selectValueSetSearchOption = HTTPUtils.cleanXSS((String) request.getParameter("opt"));
    
    if (selectValueSetSearchOption == null) {
        selectValueSetSearchOption = (String) request.getSession().getAttribute("selectValueSetSearchOption");
    }
               
    if (selectValueSetSearchOption == null || selectValueSetSearchOption.compareTo("null") == 0) {
        selectValueSetSearchOption = "Code";
    }

    if (selectValueSetSearchOption.compareTo("CodingScheme") == 0)
        check_cs = "checked";
    else if (selectValueSetSearchOption.compareTo("Code") == 0)
        check_code = "checked";
    else if (selectValueSetSearchOption.compareTo("Name") == 0)
        check_name = "checked";        
    else if (selectValueSetSearchOption.compareTo("Source") == 0)
        check_src = "checked";
    String valueset_match_text = (String) request.getSession().getAttribute("matchText_VSD");
    if (valueset_match_text == null) valueset_match_text = "";
    if (valueset_match_text != null && valueset_match_text.compareTo("null") == 0) valueset_match_text = "";
    
  
    
    
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
   
   

   
   
      <!-- Thesaurus, banner search area -->
      <div class="bannerarea">
      
<%
String uri_vsd = null;
String vsd_name = "null";

if ((vsd_vec != null && vsd_vec.size() > 1) || (vsd_vec == null)) {


if (vsd_vec == null) {

}

%>

      
    <a href="<%=basePath%>/start.jsf" style="text-decoration: none;">
      <div class="vocabularynamebanner_tb">
        <span class="vocabularynamelong_tb"><%=JSPUtils.getApplicationVersionDisplay()%></span>
      </div>
    </a>


<%
} else if (vsd_vec != null && vsd_vec.size() > 0) {
    vsd_uri = (String) vsd_vec.elementAt(0);
    if (vsd_uri.indexOf("|") == -1) {
        uri_vsd = vsd_uri;
    } else {
       Vector temp_vec = DataUtils.parseData(vsd_uri);
        vsd_name = (String) temp_vec.elementAt(0);
        uri_vsd = (String) temp_vec.elementAt(1);
    } 

    System.out.println("JSP vsd_name: " + vsd_name);
    if (vsd_name == null || vsd_name.compareTo("null") == 0) {
    %>
	  <a href="<%=basePath%>/start.jsf" style="text-decoration: none;">
	    <div class="vocabularynamebanner_tb">
	      <span class="vocabularynamelong_tb"><%=JSPUtils.getApplicationVersionDisplay()%></span>
	    </div>
	  </a>
    <%
    } else {
    %>
      <div class="banner">
        <a class="vocabularynamebanner" href="<%=request.getContextPath()%>/pages/value_set_search_results.jsf?vsd_uri=<%=HTTPUtils.cleanXSS(uri_vsd)%>">
	      <div class="vocabularynamebanner">
            <div class="vocabularynameshort" STYLE="font-size: <%=HTTPUtils.maxFontSize(vsd_name)%>px; font-family : Arial">
              <%=HTTPUtils.cleanXSS(vsd_name)%>
            </div>
          </div>
	    </a>
      </div>
    <%
    }
    %>
<%
} else {
%>

    <a href="<%=basePath%>/start.jsf" style="text-decoration: none;">
      <div class="vocabularynamebanner_tb">
        <span class="vocabularynamelong_tb"><%=JSPUtils.getApplicationVersionDisplay()%></span>
      </div>
    </a>
  
  
<%
}
%>


        <div class="search-globalnav">
          <!-- Search box -->
          <div class="searchbox-top"><img src="<%=basePath%>/images/searchbox-top.gif" width="352" height="2" alt="SearchBox Top" /></div>
          

<%
if (vsd_vec != null && vsd_vec.size() == 1) {


    String match_text = gov.nih.nci.evs.browser.utils.HTTPUtils
        .cleanXSS((String) request.getSession().getAttribute("matchText"));

    if (match_text == null) match_text = "";

    String userAgent = request.getHeader("user-agent");
    boolean isIE = userAgent != null && userAgent.toLowerCase().contains("msie");


    String uri_str = HTTPUtils.cleanXSS((String) request.getParameter("vsd_uri"));
    
    if (uri_str == null) {
        uri_str = (String) request.getSession().getAttribute("vsd_uri");
    }

    System.out.println("searchFom resolvedvaluset.jsp uri_str: " + uri_str);


    String algorithm = gov.nih.nci.evs.browser.utils.HTTPUtils.cleanXSS((String) request.getSession().getAttribute("algorithm"));

    String check_e = "", check_s = "" , check_c ="";
    if (algorithm == null || algorithm.compareTo("exactMatch") == 0)
      check_e = "checked";
    else if (algorithm.compareTo("startsWith") == 0)
      check_s= "checked";
    else
      check_c = "checked";
      
      
        String searchTarget = (String) request.getSession().getAttribute("searchTarget");
        String check_n = "";
        String check_p = "";
        check_cd ="";
        if (searchTarget == null || searchTarget.compareTo("code") == 0)
          check_cd = "checked";
        else if (searchTarget.compareTo("names") == 0)
          check_n = "checked";
        else if (searchTarget.compareTo("properties") == 0)
          check_p= "checked";
  
%>


<div class="searchbox">

<h:form id="resolvedValueSetSearchForm" styleClass="search-form">   
  <div class="textbody"> 
    <input CLASS="searchbox-input" id="matchText" name="matchText" value="<%=match_text%>" onFocus="active=true"
        onBlur="active=false"  onkeypress="return submitEnter('resolvedValueSetSearchForm:resolvedvalueset_search',event)" tabindex="1"/>
    <h:commandButton id="resolvedvalueset_search" value="Search" action="#{valueSetBean.resolvedValueSetSearchAction}"
      accesskey="13"
      onclick="javascript:cursor_wait();"
      image="#{valueSetSearch_requestContextPath}/images/search.gif"
      alt="Search Value Set"
      styleClass="searchbox-btn"
      tabindex="2">
    </h:commandButton>
    <h:outputLink value="#{facesContext.externalContext.requestContextPath}/pages/help.jsf#searchhelp" tabindex="3">
      <h:graphicImage value="/images/search-help.gif" style="border-width:0;" styleClass="searchbox-btn" alt="Search Help" />
    </h:outputLink>

  <table border="0" cellspacing="0" cellpadding="0" width="340px">
    <tr valign="top" align="left">
      <td align="left" class="textbody" colspan="2">
        <input type="radio" name="algorithm" id="algorithm1" value="exactMatch" alt="Exact Match" <%=check_e%> tabindex="4"/><label for="algorithm1">Exact Match&nbsp;</label>
        <input type="radio" name="algorithm" id="algorithm2" value="startsWith" alt="Begins With" <%=check_s%> tabindex="4"/><label for="algorithm2">Begins With&nbsp;</label>
        <input type="radio" name="algorithm" id="algorithm3" value="contains" alt="Contains" <%=check_c%> tabindex="4"/><label for="algorithm3">Contains</label>
      </td>
    </tr>
    
    <tr align="left">
      <td width="263px" height="1px" bgcolor="#2F2F5F"></td>
      <!-- The following lines are needed to make "Advanced Search" link flush right -->
      <% if (isIE) { %>
          <td width="77px"></td>
      <% } else { %>
          <td></td>
      <% } %>
    </tr>
    <tr valign="top" align="left">
      <td align="left" class="textbody" colspan="2">
        <input type="radio" name="searchTarget" id="searchTarget0" value="code" alt="Code" <%=check_cd%> tabindex="5"/><label for="searchTarget0">Code&nbsp;</label>
        <input type="radio" name="searchTarget" id="searchTarget1" value="names" alt="Names" <%=check_n%> tabindex="5"/><label for="searchTarget1">Name&nbsp;</label>
        <input type="radio" name="searchTarget" id="searchTarget2" value="properties" alt="Properties" <%=check_p%> tabindex="5"/><label for="searchTarget2">Property</label>
      </td>
    </tr>

    <tr><td height="5px;"></td></tr>
   </table>


    <input type="hidden" name="referer" id="referer" value="<%=HTTPUtils.getRefererParmEncode(request)%>" />
<%
if (uri_str != null) {
%>
<input type="hidden" name="vsd_uri" id="vsd_uri" value="<%=uri_str%>" />
<%
}
%>

</div>



</h:form>

</div>


<%
} else {
%>
          
          <div class="searchbox">
          
<h:form id="valueSetSearchForm" styleClass="search-form-main-area">      
              <%-- <div class="textbody"> --%>
<table border="0" cellspacing="0" cellpadding="0" style="margin: 2px" >
  <tr valign="top" align="left">
    <td align="left" class="textbody">  
                <% if (selectValueSetSearchOption.compareTo("CodingScheme") == 0) { %>
                  <input CLASS="searchbox-input-2"
                    name="matchText"
                    value=""
                    disabled="disabled" 
                    onkeypress="return submitEnter('valueSetSearchForm:valueset_search',event)"
                    tabindex="1"/>
                <% } else { %>
                  <input CLASS="searchbox-input-2"
                    name="matchText"
                    value="<%=valueset_match_text%>"
                    onFocus="active = true"
                    onBlur="active = false"
                    onkeypress="return submitEnter('valueSetSearchForm:valueset_search',event)"
                    tabindex="1"/>
                <% } %>  
                
                <h:commandButton id="valueset_search" value="Search" action="#{valueSetBean.valueSetSearchAction}"
                  onclick="javascript:cursor_wait();"
                  image="#{valueSetSearch_requestContextPath}/images/search.gif"
                    styleClass="searchbox-btn"
                  alt="Search"
                  tabindex="2">
                </h:commandButton>
                
                <h:outputLink
                  value="#{facesContext.externalContext.requestContextPath}/pages/help.jsf#searchhelp"
                  tabindex="3">
                  <h:graphicImage value="/images/search-help.gif" styleClass="searchbox-btn" alt="Search Help"
                    style="border-width:0;"/>
                </h:outputLink> 
    </td>
  </tr>
  
  <tr valign="top" align="left">
    <td>
      <table border="0" cellspacing="0" cellpadding="0" style="margin: 0px">
    
        <tr valign="top" align="left">
        <td align="left" class="textbody">  
                     <input type="radio" name="valueset_search_algorithm" value="exactMatch" alt="Exact Match" <%=check__e%> tabindex="3">Exact Match&nbsp;
                     <input type="radio" name="valueset_search_algorithm" value="startsWith" alt="Begins With" <%=check__s%> tabindex="3">Begins With&nbsp;
                     <input type="radio" name="valueset_search_algorithm" value="contains" alt="Contains" <%=check__c%> tabindex="3">Contains
        </td>
        </tr>
        <%
                     request.setAttribute("globalNavHeight", "37"); 
        %>
        <tr align="left">
            <td height="1px" bgcolor="#2F2F5F" align="left"></td>
        </tr>
        <tr valign="top" align="left">
          <td align="left" class="textbody">     
                <input type="radio" id="selectValueSetSearchOption" name="selectValueSetSearchOption" value="Code" <%=check_code%> 
                  alt="Code" tabindex="1" onclick="javascript:refresh()" >Code&nbsp;
                <input type="radio" id="selectValueSetSearchOption" name="selectValueSetSearchOption" value="Name" <%=check_name%> 
                  alt="Name" tabindex="1" onclick="javascript:refresh()" >Name
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>                 
                <input type="hidden" name="referer" id="referer" value="<%=HTTPUtils.getRefererParmEncode(request)%>">
                <input type="hidden" id="nav_type" name="nav_type" value="valuesets" />
                <input type="hidden" id="view" name="view" value="source" />
              <%-- </div> <!-- textbody --> --%>
</h:form> 

          </div> <!-- searchbox -->
          
 <%
} 
%>         
          
          
          <div class="searchbox-bottom"><img src="<%=basePath%>/images/searchbox-bottom.gif" width="352" height="2" alt="SearchBox Bottom" /></div>
          <!-- end Search box -->
          <!-- Global Navigation -->
          <%@ include file="/pages/templates/menuBar-termbrowserhome.jsp" %>
          <!-- end Global Navigation -->
        </div> <!-- search-globalnav -->
      </div> <!-- bannerarea -->
      
      <!-- end Thesaurus, banner search area -->
      <!-- Quick links bar -->
      <%@ include file="/pages/templates/quickLink.jsp" %>
      <!-- end Quick links bar -->

      <!-- Page content -->
      <div class="pagecontent">
        <div id="contentArea">
          <a name="evs-content" id="evs-content"></a>


          <%-- 0 <%@ include file="/pages/templates/navigationTabs.jsp"%> --%>
          
          <table border="0">
            <% if (vsd_vec != null && vsd_vec.size() > 1) { %>     
                <tr>
                  <td class="texttitle-blue">Matched Value Sets:</td>
                </tr>
            <% } %>
            <% if (message != null) { %>
              <tr class="textbodyred"><td>
              <p class="textbodyred">&nbsp;<%=message%></p>
              </td></tr>
            <% } else { %>

 <h:form id="valueSetSearchResultsForm" styleClass="search-form">            
            <tr class="textbody"><td>
              <% if (vsd_vec != null && vsd_vec.size() == 1) { %>
                <div id="message" class="textbody">
                   <table border="0" width="700px">
                    <tr>
                      <td>
                         <div class="texttitle-blue">Welcome</div>
                      </td>
                      
                      <td class="dataCellText" align="right">
                        <h:commandButton id="Values" value="Values" action="#{valueSetBean.resolveValueSetAction}"
                          onclick="javascript:cursor_wait();"
                          image="#{valueSetSearch_requestContextPath}/images/values.gif"
                          alt="Values"
                          tabindex="3">
                        </h:commandButton>                  
                        &nbsp;
                        <h:commandButton id="versions" value="versions" action="#{valueSetBean.selectCSVersionAction}"
                          onclick="javascript:cursor_wait();"
                          image="#{valueSetSearch_requestContextPath}/images/versions.gif"
                          alt="Versions"
                          tabindex="2">
                        </h:commandButton>
                        &nbsp;
                        <h:commandButton id="xmldefinition" value="xmldefinition" action="#{valueSetBean.exportVSDToXMLAction}"
                          onclick="javascript:cursor_wait();"
                          image="#{valueSetSearch_requestContextPath}/images/xmldefinitions.gif"
                          alt="XML Definition"
                          tabindex="2">
                        </h:commandButton>                      
                      </td>
                    </tr>
                    <tr><td colspan="2">
                      <hr/>
                    </td></tr>
                  </table>  
                </div>
              <% } %> 
 
 <%
 if (vsd_uri != null) {
  
      if (vsd_uri.indexOf("|") != -1) {
  	Vector w = (Vector) DataUtils.parseData(vsd_uri);
  	vsd_uri = (String) w.elementAt(1);
      }

 %>
 
    <input type="hidden" name="valueset" value="<%=vsd_uri%>">&nbsp;</input>
    
<%
 }
 %>

 
              <table class="datatableValueSet" summary="" cellpadding="3" cellspacing="0" border="0" width="100%">

<%
if (vsd_vec != null && vsd_vec.size() > 1) {
%> 
		<th class="dataTableHeader" scope="col" align="left">&nbsp;</th>
        <th class="dataTableHeader" scope="col" align="left">Name</th>
        <!--
        <th class="dataTableHeader" scope="col" align="left">URI</th>
        <th class="dataTableHeader" scope="col" align="left">Description</th>
        -->
        <th class="dataTableHeader" scope="col" align="left">Concept Domain</th>
        <th class="dataTableHeader" scope="col" align="left">Sources</th>
<%
}
%>		
		


<%
  if (vsd_vec != null) {
    for (int i=0; i<vsd_vec.size(); i++) {
      String vsd_str = (String) vsd_vec.elementAt(i);
            
      Vector u = DataUtils.parseData(vsd_str);
      String name = (String) u.elementAt(0);
      String uri = (String) u.elementAt(1);
      String label = (String) u.elementAt(2);
      String cd = (String) u.elementAt(3);
      String sources = (String) u.elementAt(4);
      String supportedsources = (String) u.elementAt(5);

    
      if (vsd_vec.size() > 1) {
        if (i % 2 == 0) {
          %> <tr class="dataRowDark"> <%
        } else {
          %> <tr class="dataRowLight"> <%
        }
      } else {
        %> <tr> <%
      } 
      if (vsd_vec != null && vsd_vec.size() > 1) {
%>	
        <td scope="row">
          <% if (i == 0) { %>
            <input type=radio name="valueset" value="<%=uri%>" checked >&nbsp;</input>
          <% } else { %>
            <input type=radio name="valueset" value="<%=uri%>">&nbsp;</input>
          <% } %>
        </td>
<%    } %>

<%
if (vsd_vec != null && vsd_vec.size() == 1) {
    String vsd_metadata_str = (String) vsd_vec.elementAt(0);
  
    
    
    Vector w = DataUtils.parseData(vsd_metadata_str);
    vsd_uri = (String) w.elementAt(1);

    
    String vsd_description = ValueSetHierarchy.getValueSetDecription(vsd_uri);
    if (vsd_description == null) {
        vsd_description = "DESCRIPTION NOT AVAILABLE";
    }
    
    
    
    
%>
		      <td class="dataCellText">
		      <p>
			 <b><%=name%></b>
		      </p>
		      
		      <p class="dataCellText">
		      
		      <%=vsd_description%>

		      
		      </p>
		      
		      </td>
		      

		      
		      
<%		
} else {
%>		      
		      
		      <td class="dataCellText">
                         <a href="<%=request.getContextPath() %>/pages/value_set_search_results.jsf?vsd_uri=<%=uri%>"><%=name%></a>
		      </td>
		      

		      <td class="dataCellText">
			 <%=cd%>
		      </td>
		      <td class="dataCellText">
			 <%=supportedsources%>
		      </td>  

<%		
} 
%>

		      </tr>
              
              
             <%
                }
             }
             %>                 
              </table>
 
 
 
 
 <%
 if (vsd_vec != null && vsd_vec.size() > 1) {
%>
 
                   <tr><td class="dataCellText">
                     <h:commandButton id="Values" value="Values" action="#{valueSetBean.resolveValueSetAction}"
                       onclick="javascript:cursor_wait();"
                       image="#{valueSetSearch_requestContextPath}/images/values.gif"
                       alt="Values"
                       tabindex="3">
                     </h:commandButton>                  
                   &nbsp;
                     <h:commandButton id="versions" value="versions" action="#{valueSetBean.selectCSVersionAction}"
                       onclick="javascript:cursor_wait();"
                       image="#{valueSetSearch_requestContextPath}/images/versions.gif"
                       alt="Versions"
                       tabindex="2">
                     </h:commandButton>
                   &nbsp;
                     <h:commandButton id="xmldefinition" value="xmldefinition" action="#{valueSetBean.exportVSDToXMLAction}"
                       onclick="javascript:cursor_wait();"
                       image="#{valueSetSearch_requestContextPath}/images/xmldefinitions.gif"
                       alt="XML Definition"
                       tabindex="2">
                     </h:commandButton>
                  </td></tr>


<%
}
%>


<%		
if (vsd_vec != null && vsd_vec.size() == 1) {

if (vsd_uri.indexOf("|") != -1) {
	Vector w = (Vector) DataUtils.parseData(vsd_uri);
	vsd_uri = (String) w.elementAt(1);
}


%>		
    <input type="hidden" name="vsd_uri" id="vsd_uri" value="<%=vsd_uri%>">
    
<%
} else {
%>
    <input type="hidden" name="multiplematches" id="multiplematches" value="true">
<%
}
%>

          <input type="hidden" name="view" id="view" value="<%=VSD_view%>">
          <input type="hidden" name="view" id="nav_type" value="valuesets">
          
              <input type="hidden" name="referer" id="referer" value="<%=HTTPUtils.getRefererParmEncode(request)%>">
</h:form>
            
          </td></tr>
          
          
 <% } %>
          
          
        </table>
          
        </div> <!--  popupContentArea -->
        <!--
        <div class="popupContentAreaWithoutBorder">
        -->
        <div class="textbody">
          <%@ include file="/pages/templates/nciFooter.jsp" %>
        </div>
      </div> <!-- pagecontent -->
    </div> <!-- main-area -->
    <!-- end Main box -->
  </div> <!-- center-page -->
  <div class="mainbox-bottom"><img src="<%=basePath%>/images/mainbox-bottom.gif" width="745" height="5" alt="Mainbox Bottom" /></div>
</f:view>
</body>
</html>


