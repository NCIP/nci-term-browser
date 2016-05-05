<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8"%>
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
String checked_vocabularies = HTTPUtils.cleanXSS((String) request.getSession().getAttribute("checked_vocabularies"));
boolean bool_val;
Vector selected_vocabularies = null;

String tooltip_str = "No value set is selected." + "</br>";
String selected_vocabularies_link = "";
if (checked_vocabularies != null) {
    tooltip_str = checked_vocabularies + "</br>";
    selected_vocabularies = StringUtils.parseData(checked_vocabularies, ",");
    Vector selected_vocabularies_names = DataUtils.uri2CodingSchemeName(selected_vocabularies);
    selected_vocabularies_link = JSPUtils.getPopUpWindow(selected_vocabularies_names, "Selected Value Sets");
}

//String partial_checked_vocabularies = HTTPUtils.cleanXSS((String) request.getParameter("partial_checked_vocabularies"));

String resultsPerPage = HTTPUtils.cleanXSS((String) request.getParameter("resultsPerPage"));
if (resultsPerPage == null) {
    resultsPerPage = (String) request.getSession().getAttribute("resultsPerPage");
    
    if (resultsPerPage == null) {
        resultsPerPage = "50";
    }  else {
    
	    bool_val = JSPUtils.isInteger(resultsPerPage);
	    if (!bool_val) {
		 String redirectURL = request.getContextPath() + "/pages/appscan_response.jsf";
		 String error_msg = HTTPUtils.createErrorMsg("resultsPerPage", resultsPerPage);
		 request.getSession().setAttribute("error_msg", error_msg);
		 response.sendRedirect(redirectURL);
	    } else {
	         resultsPerPage = "50";
	    }
    
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
int size = 0;
if (iteratorBean != null) {
    iteratorBean.getSize(); 
}
    
//List list = null;
List list = new ArrayList();
int num_pages = size / pageSize;
if (num_pages * pageSize < size) num_pages++;

String page_number = HTTPUtils.cleanXSS((String) request.getParameter("page_number"));

if (!DataUtils.isNull(page_number)) {

	    bool_val = JSPUtils.isInteger(page_number);
	    if (!bool_val) {
		 String redirectURL = request.getContextPath() + "/pages/appscan_response.jsf";
		 String error_msg = HTTPUtils.createErrorMsg("page_number", page_number);
		 request.getSession().setAttribute("error_msg", error_msg);
		 response.sendRedirect(redirectURL);
	    } else {
	         pageNum = Integer.parseInt(page_number);
	    }
    
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

if (iteratorBean != null) {
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
}


num_pages = size / pageSize;
if (num_pages * pageSize < size) num_pages++;

int istart_plus_pageSize = istart+pageSize;


String istart_str = Integer.toString(istart+1);    
String iend_str = Integer.valueOf(iend).toString();

if (iteratorBean != null) {
	if (iend >= istart+pageSize-1) {
	    iend = istart+pageSize-1;
	    list = iteratorBean.getData(istart, iend);
	    iend_str = Integer.valueOf(iend+1).toString();
	}
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
    if (message == null) {
        message = (String) request.getSession().getAttribute("error_msg");
    }
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
    if (valueset_search_algorithm == null || valueset_search_algorithm.compareTo("contains") == 0)
        check__c = "checked";
    else if (valueset_search_algorithm.compareTo("startsWith") == 0)
        check__s= "checked";
    else 
        check__e = "checked";
        
    String selectValueSetSearchOption = null;
    selectValueSetSearchOption = HTTPUtils.cleanXSS((String) request.getParameter("opt"));
    
    if (selectValueSetSearchOption == null) {
        selectValueSetSearchOption = (String) request.getSession().getAttribute("selectValueSetSearchOption");
    }
               
     

    String uri_vsd = null;
    String vsd_name = "null";

    String match_text = gov.nih.nci.evs.browser.utils.HTTPUtils
        .cleanXSS((String) request.getSession().getAttribute("matchText"));

    if (match_text == null) match_text = "";

    String userAgent = request.getHeader("user-agent");
    boolean isIE = userAgent != null && userAgent.toLowerCase().contains("msie");
    
    if (vsd_uri == null) {
        vsd_uri = (String) request.getSession().getAttribute("vsd_uri");
    }

    request.setAttribute("globalNavHeight", "37"); 
      
    String searchTarget = selectValueSetSearchOption;
        
        String check_n = "";
        String check_p = "";
        check_cd ="";
        
    if (DataUtils.isNullOrBlank(searchTarget)) {
        check_n = "checked";
    } else if (searchTarget.compareTo("Code") == 0) {
        check_cd = "checked";
    } else if (searchTarget.compareTo("Name") == 0) {
        check_n = "checked";
    } 
    /*else if (searchTarget.compareTo("properties") == 0) {
        check_p = "checked";
    }*/        

  
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
          <div class="searchbox-top"><img src="/ncitbrowser/images/searchbox-top.gif" width="352" height="2" alt="SearchBox Top" /></div>
          <div class="searchbox">

<!--
<h:form id="valueSetSearchForm" styleClass="search-form" acceptcharset="UTF-8">      
-->

<form id="valueSetSearchForm" name="valueSetSearchForm" method="post" action="<%= request.getContextPath() %>/ajax?action=search_value_set" class="search-form-main-area" enctype="application/x-www-form-urlencoded;charset=UTF-8">

<input type="hidden" name="valueSetSearchForm" value="valueSetSearchForm" />
<input type="hidden" name="view" value="1" />
            <input type="hidden" id="partial_checked_vocabularies" name="partial_checked_vocabularies" value="" />
            <input type="hidden" id="value_set_home" name="value_set_home" value="true" />
            <input type="hidden" name="vsd_uri" value="null" />
<table border="0" cellspacing="0" cellpadding="0" style="margin: 2px" >
  <tr valign="top" align="left">
    <td align="left" class="textbody">

                  <input CLASS="searchbox-input-2"
                    name="matchText"
                    value="<%=matchText%>"
                    onFocus="active = true"
                    onBlur="active = false"
                    onkeypress="return submitEnter('valueSetSearchForm:valueset_search',event)"
                    tabindex="1"/>


                <input id="valueSetSearchForm:valueset_search" type="image" src="/ncitbrowser/images/search.gif" name="valueSetSearchForm:valueset_search" alt="Search value sets containing matched concepts" onclick="javascript:getCheckedVocabularies();" tabindex="2" class="searchbox-btn" /><a href="/ncitbrowser/pages/help.jsf#searchhelp" tabindex="3"><img src="/ncitbrowser/images/search-help.gif" alt="Search Help" style="border-width:0;" class="searchbox-btn" /></a>


    </td>
  </tr>

  <tr valign="top" align="left">
    <td>
      <table border="0" cellspacing="0" cellpadding="0" style="margin: 0px">

        <tr valign="top" align="left">
        <td align="left" class="textbody">
                     <input type="radio" name="valueset_search_algorithm" value="contains"   <%=check__c%> alt="Contains" checked tabindex="3"  onclick="onVSAlgorithmChanged();">Contains
                     <input type="radio" name="valueset_search_algorithm" value="exactMatch" <%=check__e%> alt="Exact Match"  tabindex="3">Exact Match&nbsp;
                     <input type="radio" name="valueset_search_algorithm" value="startsWith" <%=check__s%> alt="Begins With"  tabindex="3"  onclick="onVSAlgorithmChanged();">Begins With&nbsp;
        </td>  
        </tr>

        <tr align="left">
            <td height="1px" bgcolor="#2F2F5F" align="left"></td>
        </tr>
        <tr valign="top" align="left">
          <td align="left" class="textbody">
                <input type="radio" id="selectValueSetSearchOption" name="selectValueSetSearchOption" value="Name" <%=check_n%>  alt="Name" checked tabindex="4"  >Name&nbsp;
                <input type="radio" id="selectValueSetSearchOption" name="selectValueSetSearchOption" value="Code" <%=check_cd%> alt="Code" tabindex="4" onclick="onVSCodeButtonPressed();">Code&nbsp;
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
                <input type="hidden" id="nav_type" name="nav_type" value="valuesets" />
                <input type="hidden" id="view" name="view" value="source" />

		<input type="hidden" name="checked_vocabularies" id="checked_vocabularies" value="<%=checked_vocabularies%>">
		<input type="hidden" name="referer" id="referer" value="<%=HTTPUtils.getRefererParmEncode(request)%>">
 
                
</h:form>

          </div> <!-- searchbox -->

          <div class="searchbox-bottom"><img src="/ncitbrowser/images/searchbox-bottom.gif" width="352" height="2" alt="SearchBox Bottom" /></div>
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
<div>
          <a name="evs-content" id="evs-content"></a>
         
            <% if (!DataUtils.isNullOrBlank(message)) {
		      if (message.compareToIgnoreCase("No match found.") == 0) {
		      %>
		      <p class="textbodyred">&nbsp;No match found in &nbsp;

<%=selected_vocabularies_link%>

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


    String vsd_description = DataUtils.getValueSetHierarchy().getValueSetDecription(vsd_uri);
    if (vsd_description == null) {
	vsd_description = "DESCRIPTION NOT AVAILABLE";
    }
	    

HashMap hmap = new HashMap();
HashSet hset = new HashSet();

for (int i=0; i<list.size(); i++) {
      ResolvedConceptReference rcr = (ResolvedConceptReference) list.get(i);
      
      name = (String) rcr.getCodingSchemeName();
      
      uri = (String) rcr.getCodingSchemeURI();
      entity_name = "";
      if (rcr.getEntityDescription() != null) {
          entity_name = rcr.getEntityDescription().getContent();
      }
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
      
      String cs_name_and_version = vocabulary_name + " (" +  entity_cs_version + ")";

        if (i % 2 == 0) {
          %> <tr class="dataRowDark"> <%
        } else {
          %> <tr class="dataRowLight"> <%
        }
        %>      
	      
		      <td class="dataCellText">
                        <a href="<%=request.getContextPath() %>/ajax?action=create_src_vs_tree&vsd_uri=<%=uri%>"><%=DataUtils.encodeTerm(name)%></a>
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
			     <a href="<%=request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=entity_cs_nm%>&ns=<%=entity_cs_nm%>&code=<%=entity_code%>&key=<%=itr_key%>&b=1&n=<%=page_number%>&vse=1" ><%=DataUtils.encodeTerm(entity_name)%></a>
                         <%
                         } else {
                         %>
			     <a href="<%=request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=entity_cs_nm%>&version=<%=entity_cs_version%>&ns=<%=entity_cs_nm%>&code=<%=entity_code%>&key=<%=itr_key%>&b=1&n=<%=page_number%>&vse=1" ><%=DataUtils.encodeTerm(entity_name)%></a>
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
          <input type="hidden" name="checked_vocabularies" id="checked_vocabularies" value="<%=checked_vocabularies%>">
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
  <div class="mainbox-bottom"><img src="<%=basePath%>/images/mainbox-bottom.gif" width="945" height="5" alt="Mainbox Bottom" /></div>
</f:view>
</body>
</html>


