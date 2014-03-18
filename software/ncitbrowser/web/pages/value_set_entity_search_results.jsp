<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="java.util.Vector"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.HashSet"%>
<%@ page import="org.LexGrid.concepts.Entity" %>
<%@ page import="gov.nih.nci.evs.browser.common.Constants" %>
<%@ page import="gov.nih.nci.evs.browser.utils.*" %>
<%@ page import="gov.nih.nci.evs.browser.bean.IteratorBean" %>
<%@ page import="org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference" %>
<%@ page import="javax.faces.context.FacesContext" %>
<%@ page import="org.apache.log4j.*" %>
<%@ page import="gov.nih.nci.evs.browser.utils.*" %>
<%@ page import="org.lexgrid.valuesets.LexEVSValueSetDefinitionServices" %>
<%@ page import="org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator" %>

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
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
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
  
  <script type="text/javascript">
    
    function onCodeButtonPressed(formname) {
          var algorithmObj = document.forms[formname].algorithm;
	  for (var j=0; j<algorithmObj.length; j++) {
		  algorithm = algorithmObj[j].value;
		  if (algorithm == "exactMatch") {
			 algorithmObj[j].checked = true;
			 break;
		  }
	  }
    }

    function getSearchTarget(formname) {
          var searchTargetObj = document.forms[formname].searchTarget;
	  for (var j=0; j<searchTargetObj.length; j++) {
	      if (searchTargetObj[j].checked == true) {
	          return searchTargetObj[j].value;
	      }
	  }
    }


    function onAlgorithmChanged(formname) {
          var target = getSearchTarget(formname);
          if (target != "codes") return;
          var targetObj = document.forms[formname].searchTarget;
          targetObj[0].checked = true;
    }         
  
  </script>
  
<%!
  private static Logger _logger = Utils.getJspLogger("value_set_entity_search_results.jsp");
%>
    

<%
String VSD_view = (String) request.getSession().getAttribute("view");
String valueSetSearch_requestContextPath = request.getContextPath();
String selected_ValueSetSearchOption = HTTPUtils.cleanXSS((String) request.getSession().getAttribute("selectValueSetSearchOption")); 
//String checked_vocabularies = HTTPUtils.cleanXSS((String) request.getParameter("checked_vocabularies"));

String checked_vocabularies = HTTPUtils.cleanXSS((String) request.getSession().getAttribute("checked_vocabularies"));
Vector selected_vocabularies = null;


String tooltip_str = "No value set is selected." + "</br>";
String selected_vocabularies_link = "";
if (checked_vocabularies != null) {
    tooltip_str = checked_vocabularies + "</br>";
    selected_vocabularies = DataUtils.parseData(checked_vocabularies, ",");
    
    selected_vocabularies_link = JSPUtils.getPopUpWindow(selected_vocabularies, "Selected Value Sets");
}


//String partial_checked_vocabularies = HTTPUtils.cleanXSS((String) request.getParameter("partial_checked_vocabularies"));


String resultsPerPage = HTTPUtils.cleanXSS((String) request.getParameter("resultsPerPage"));
if (resultsPerPage == null) {
    resultsPerPage = (String) request.getSession().getAttribute("resultsPerPage");
    if (resultsPerPage == null) {
        resultsPerPage = "50";
    }
    
}  else {
    request.getSession().setAttribute("resultsPerPage", resultsPerPage);
}

String selectedResultsPerPage = resultsPerPage;
String itr_key = null;
IteratorBean iteratorBean = (IteratorBean) request.getSession().getAttribute("value_set_entity_search_results");
if (iteratorBean != null) {
    itr_key = iteratorBean.getKey();
}

//====================================================================================================
String matchText = HTTPUtils.cleanXSS((String) request.getSession().getAttribute("matchText"));
int pageNum = 0; 
int pageSize = Integer.parseInt( resultsPerPage );
int size = iteratorBean.getSize();    
List list = null;
int num_pages = size / pageSize;
if (num_pages * pageSize < size) num_pages++;

String page_number = HTTPUtils.cleanXSS((String) request.getParameter("page_number"));

if (!DataUtils.isNull(page_number)) {
    pageNum = Integer.parseInt(page_number);
} else {
    pageNum = 0;
    
    page_number = "0";
}

int istart = pageNum * pageSize;
int page_num = pageNum;
if (page_num == 0) {
    page_num++;
} else {
    istart = (pageNum-1) * pageSize;
}
int iend = istart + pageSize - 1;
try {
   list = iteratorBean.getData(istart, iend);
   int prev_size = size;
   size = iteratorBean.getSize();

   if (size != prev_size) {
	if (iend > size) {
	    iend = size;
	}
       list = iteratorBean.getData(istart, size);
       
   } else {

	if (iend > size) {
	    iend = size;
	}

   }
} catch (Exception ex) {
   //System.out.println("ERROR: bean.getData throws exception??? istart: " + istart + " iend: " + iend);
}


num_pages = size / pageSize;
if (num_pages * pageSize < size) num_pages++;

int istart_plus_pageSize = istart+pageSize;


String istart_str = Integer.toString(istart+1);    
String iend_str = Integer.valueOf(iend).toString();

if (iend >= istart+pageSize-1) {
    iend = istart+pageSize-1;
    list = iteratorBean.getData(istart, iend);
    iend_str = Integer.valueOf(iend+1).toString();
}

String match_size = Integer.valueOf(size).toString();
    

int next_page_num = page_num + 1;
int prev_page_num = page_num - 1;
String prev_page_num_str = Integer.toString(prev_page_num);
String next_page_num_str = Integer.toString(next_page_num);

//====================================================================================================
String vsd_uri = HTTPUtils.cleanXSS((String) request.getParameter("vsd_uri")); 
    
    String searchform_requestContextPath = request.getContextPath();
    searchform_requestContextPath = searchform_requestContextPath.replace("//ncitbrowser//ncitbrowser", "//ncitbrowser");

    request.getSession().setAttribute("navigation_type", "valuesets");

    String message = (String) request.getSession().getAttribute("message");
    request.getSession().removeAttribute("message");

   
    String t = null;
    
    String selected_cs = "";
    String selected_cd = null;

    String check_cs = "";
    String check_cd = "";
    String check_code = "";
    String check_name = "";
    String check_src = "";
    
    // to be modified
    String valueset_search_algorithm = HTTPUtils.cleanXSS((String) request.getSession().getAttribute("valueset_search_algorithm")); ;

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
               
    if (DataUtils.isNullOrBlank(selectValueSetSearchOption)) {
        selectValueSetSearchOption = "Name";
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
    if (DataUtils.isNull(valueset_match_text)) {
        valueset_match_text = (String) request.getSession().getAttribute("matchText");
    }
    if (DataUtils.isNull(valueset_match_text)) {
        valueset_match_text = "";
    }    
    

String uri_vsd = null;
String vsd_name = "null";
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
     <!-- Thesaurus, banner search area -->
      <div class="bannerarea_960">
      
      
    <a href="<%=basePath%>/start.jsf" style="text-decoration: none;">
      <div class="vocabularynamebanner_tb">
        <span class="vocabularynamelong_tb"><%=JSPUtils.getApplicationVersionDisplay()%></span>
      </div>
    </a>


        <div class="search-globalnav_960">
          <!-- Search box -->
          <div class="searchbox-top"><img src="<%=basePath%>/images/searchbox-top.gif" width="352" height="2" alt="SearchBox Top" /></div>
          

<%

    String match_text = gov.nih.nci.evs.browser.utils.HTTPUtils
        .cleanXSS((String) request.getSession().getAttribute("matchText"));

    if (match_text == null) match_text = "";

    String userAgent = request.getHeader("user-agent");
    boolean isIE = userAgent != null && userAgent.toLowerCase().contains("msie");

    //String vsd_uri = HTTPUtils.cleanXSS((String) request.getParameter("vsd_uri"));
    
    if (vsd_uri == null) {
        vsd_uri = (String) request.getSession().getAttribute("vsd_uri");
    }


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
        
    if (DataUtils.isNullOrBlank(searchTarget)) {
        check_n = "checked";
    } else if (searchTarget.compareTo("codes") == 0) {
        check_cd = "checked";
    } else if (searchTarget.compareTo("names") == 0) {
        check_n = "checked";
    } else if (searchTarget.compareTo("properties") == 0) {
        check_p = "checked";
    }        

  
%>


          <div class="searchbox">
          
<h:form id="valueSetSearchForm" styleClass="search-form-main-area_960" acceptcharset="UTF-8">      
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
                  alt="Search value sets containing matched concepts"
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
                     <input type="radio" id="valueset_search_algorithm" name="valueset_search_algorithm" value="contains" alt="Contains" <%=check__c%> tabindex="3" onclick="onVSAlgorithmChanged();">Contains
                     <input type="radio" id="valueset_search_algorithm" name="valueset_search_algorithm" value="exactMatch" alt="Exact Match" <%=check__e%> tabindex="3">Exact Match&nbsp;
                     <input type="radio" id="valueset_search_algorithm" name="valueset_search_algorithm" value="startsWith" alt="Begins With" <%=check__s%> tabindex="3" onclick="onVSAlgorithmChanged();">Begins With&nbsp;
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
          
                <input type="radio" id="selectValueSetSearchOption" name="selectValueSetSearchOption" value="Name" <%=check_name%> 
                  alt="Name" tabindex="1" >Name&nbsp;
          
          
                <input type="radio" id="selectValueSetSearchOption" name="selectValueSetSearchOption" value="Code" <%=check_code%> 
                  alt="Code" tabindex="1" onclick="javascript:onVSCodeButtonPressed()" >Code
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>                 
                <input type="hidden" name="referer" id="referer" value="<%=HTTPUtils.getRefererParmEncode(request)%>">
                <input type="hidden" id="nav_type" name="nav_type" value="valuesets" />
                <input type="hidden" id="view" name="view" value="source" />
<input type="hidden" name="checked_vocabularies" id="checked_vocabularies" value="<%=checked_vocabularies%>">
                
              <%-- </div> <!-- textbody --> --%>
</h:form> 

          </div> <!-- searchbox -->
          
          <div class="searchbox-bottom"><img src="<%=basePath%>/images/searchbox-bottom.gif" width="352" height="2" alt="SearchBox Bottom" /></div>
          <!-- end Search box -->
          <!-- Global Navigation -->
          <%@ include file="/pages/templates/menuBar-termbrowserhome.jsp" %>
          <!-- end Global Navigation -->
        </div> <!-- search-globalnav_960 -->
      </div> <!-- bannerarea_960 -->
      
      <!-- end Thesaurus, banner search area -->
      <!-- Quick links bar -->
      <%@ include file="/pages/templates/quickLink.jsp" %>
      <!-- end Quick links bar -->

      <!-- Page content -->
      <div class="pagecontent">
        <div id="contentArea">
          <a name="evs-content" id="evs-content"></a>

          <%-- 0 <%@ include file="/pages/templates/navigationTabs.jsp"%> --%>
          
            <% if (!DataUtils.isNullOrBlank(message)) {
		      if (message.compareToIgnoreCase("No match found.") == 0) {
		      %>
		      <p class="textbodyred">&nbsp;No match found in &nbsp;

<%=selected_vocabularies_link%>
<!--
	<a href="#" onmouseover="Tip('<%=tooltip_str%>')" onmouseout="UnTip()">selected value sets</a>
-->

	              </p>.
		      <%
		      } else {
		      %>
		          <p class="textbodyred">&nbsp;<%=message%><p>
		      <%
		      }
		      %>
              
            <% }  else { %>         

          
          <table>
           



 <h:form id="valueSetSearchResultsForm" styleClass="search-form" acceptcharset="UTF-8">            
            <tr class="textbody"><td>
                <div id="message" class="textbody">
                
                   <table width="900px">
                   
			  <tr><td>
			    <table>
			      <tr>
				<td class="texttitle-blue">Result for:</td>
				<td class="texttitle-gray"><%=matchText%></td>
			      </tr>
			    </table>
			  </td></tr>
			  
			  <tr>
			    <td><hr></td>
			  </tr>
			  
			  <tr>
			    <td>
			      <b>Results <%=istart_str%>-<%=iend_str%> of&nbsp;<%=match_size%> for: <%=matchText%>&nbsp;from</b>&nbsp;

<%=selected_vocabularies_link%>
<!--
<a href="#" onmouseover="Tip('<%=tooltip_str%>')" onmouseout="UnTip()">selected value sets</a>.
-->
			    </td>
			  </tr>                   
                   </table>  
                </div>
 
    <input type="hidden" name="valueset" value="<%=vsd_uri%>">&nbsp;</input>

 
    <table class="datatable_960" summary="" cellpadding="3" cellspacing="0" border="0" width="100%">
       <th class="dataTableHeader" scope="col" align="left">Value Set</th>
        <th class="dataTableHeader" scope="col" align="left">Vocabulary</th>
        <!--
        <th class="dataTableHeader" scope="col" align="left">Version</th>
        -->
        <th class="dataTableHeader" scope="col" align="left">Name</th>
        <th class="dataTableHeader" scope="col" align="left">Code</th>

<%
      String name = null;
      String uri = null;
      String label = null;
      String cd = null;
      String sources = null;
      String supportedsources = null;
      
      String entity_name = null;
      String entity_code = null;
      String entity_cs = null;
      String entity_cs_version = null;


    String vsd_description = ValueSetHierarchy.getValueSetDecription(vsd_uri);
    if (vsd_description == null) {
	vsd_description = "DESCRIPTION NOT AVAILABLE";
    }
	    

HashMap hmap = new HashMap();
HashSet hset = new HashSet();

for (int i=0; i<list.size(); i++) {
      ResolvedConceptReference rcr = (ResolvedConceptReference) list.get(i);
      
      name = (String) rcr.getCodingSchemeName();
      
      uri = (String) rcr.getCodingSchemeURI();
      entity_name = (String) rcr.getEntityDescription().getContent();
      entity_code = (String) rcr.getCode();
      entity_cs = (String) rcr.getCodeNamespace();
      
      String vocabulary_name = (String) DataUtils.getFormalName(entity_cs);
      
      entity_cs_version = null;//(String) rcr.getCodingSchemeVersion(); 
      
      if (hmap.containsKey(name + "|" + entity_cs)) {
           entity_cs_version = (String) hmap.get(name + "|" + entity_cs);
      } else {
           entity_cs_version = DataUtils.findVersionOfCodingSchemeUsedInValueSetResolution(name, entity_cs);
	   if (DataUtils.isNull(entity_cs_version)) {
	      entity_cs_version = "";
	   }           
           hmap.put(name + "|" + entity_cs, entity_cs_version);
      }
      /*
      if (DataUtils.isNull(entity_cs_version)) {
           entity_cs_version = "";
      }
      */
      
      String cs_name_and_version = vocabulary_name + " (" +  entity_cs_version + ")";

        if (i % 2 == 0) {
          %> <tr class="dataRowDark"> <%
        } else {
          %> <tr class="dataRowLight"> <%
        }
        %>      
	      
		      <td class="dataCellText">
                        <a href="<%=request.getContextPath() %>/ajax?action=create_src_vs_tree&vsd_uri=<%=uri%>"><%=name%></a>
		      </td>
		      
		      <!--
		      <td class="dataCellText">
			 <%=entity_cs%>
		      </td>  
		      
		      <td class="dataCellText">
			 <%=entity_cs_version%>
		      </td>  
		      -->

		      <td class="dataCellText">
			 <%=cs_name_and_version%>
		      </td>  
		      
		      <td class="dataCellText">
		         <%
		         String entity_cs_nm = DataUtils.getCSName(entity_cs);
			 if (DataUtils.isNull(entity_cs_version) || entity_cs_version.compareTo("") == 0) {
			     //String entity_cs_nm = DataUtils.getCSName(entity_cs);
			 %>
			     <a href="<%=request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=entity_cs_nm%>&code=<%=entity_code%>&key=<%=itr_key%>&b=1&n=<%=page_number%>&vse=1" ><%=entity_name%></a>
                         <%
                         } else {
                         %>
			     <a href="<%=request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=entity_cs_nm%>&version=<%=entity_cs_version%>&code=<%=entity_code%>&key=<%=itr_key%>&b=1&n=<%=page_number%>&vse=1" ><%=entity_name%></a>
                         <%
                         }
                         %>
		      </td>  
		      
		      <td class="dataCellText">
			 <%=entity_code%>
		      </td>  
		  </tr>
              
<%
}
%>                 
        </table>

          <input type="hidden" name="vsd_uri" id="vsd_uri" value="<%=vsd_uri%>">
          <input type="hidden" name="view" id="view" value="<%=VSD_view%>">
          <input type="hidden" name="view" id="nav_type" value="valuesets">
          
          <input type="hidden" name="referer" id="referer" value="<%=HTTPUtils.getRefererParmEncode(request)%>">
          
</h:form>

<% } %>

          </td></tr>
           
        </table>


<form id="view_form" enctype="application/x-www-form-urlencoded;charset=UTF-8">
     <input type="hidden" id="view" name="view" value="<%=VSD_view%>" />
</form>

          
        </div> <!--  popupContentArea -->
        <div class="textbody">
        
        <% if (DataUtils.isNullOrBlank(message)) {

%>

        
          <%@ include file="/pages/templates/pagination-valuesetentity-results.jsp" %>
          
          
        <%}%>
        
          <%@ include file="/pages/templates/nciFooter.jsp" %>
        </div>
      </div> <!-- pagecontent -->
    </div> <!-- main-area_960 -->
    <!-- end Main box -->
  </div> <!-- center-page_960 -->
  <div class="mainbox-bottom"><img src="<%=basePath%>/images/mainbox-bottom.gif" width="941" height="5" alt="Mainbox Bottom" /></div>
</f:view>
</body>
</html>


