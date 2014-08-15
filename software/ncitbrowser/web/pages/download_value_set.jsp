<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="org.LexGrid.concepts.Entity"%>
<%@ page import="gov.nih.nci.evs.browser.bean.*"%>
<%@ page import="gov.nih.nci.evs.browser.utils.*"%>
<%@ page import="gov.nih.nci.evs.browser.properties.*"%>
<%@ page import="gov.nih.nci.evs.browser.utils.*"%>
<%@ page import="javax.faces.context.FacesContext"%>
<%@ page import="org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference"%>
<%@ page import="org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator"%>
<%@ page import="org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList"%>
<%@ page import="org.apache.log4j.*"%>


<%@ page import="org.LexGrid.valueSets.ValueSetDefinition" %>
<%@ page import="gov.nih.nci.evs.browser.utils.*" %>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns:c="http://java.sun.com/jsp/jstl/core">
<head>
<title>NCI Thesaurus</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/styleSheet.css" />
<link rel="shortcut icon" href="<%=request.getContextPath()%>/favicon.ico" type="image/x-icon" />
<script type="text/javascript" src="<%=request.getContextPath()%>/js/script.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/search.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/dropdown.js"></script>
</head>

<style>
.outer {position:relative}
.inner {
  overflow-x:scroll;
  overflow-y:scroll;
  width:900px;
  margin-left:5px;
  height:500px;
}
</style>


<style type="text/css">
    body, .mt {padding:0px;font-family: Tahoma, sans-serif;font-size: 0.9em;}
    body {margin:5px;}
    p {margin:30px 0px 30px 0px;}
    table.mt {border-width: 1px;border-spacing:0px ;border-style: solid;border-color: #cfcfcf;border-collapse: collapse;background-color: transparent;}
    table.mt th {border-width: 1px;padding: 1px;border-style: solid;border-color: #cfcfcf;white-space: nowrap; background-color: #afafaf;text-align:left;}
    table.mt td {border-width: 1px;padding: 1px;border-style: solid;border-color: #cfcfcf;text-align: left;vertical-align:middle;}
    .frc {background: #efefef;}
</style>
        
        

<body>
   <script type="text/javascript" src="<%=request.getContextPath()%>/js/wz_tooltip.js"></script>
   <script type="text/javascript" src="<%=request.getContextPath()%>/js/tip_centerwindow.js"></script>
   <script type="text/javascript" src="<%=request.getContextPath()%>/js/tip_followscroll.js"></script>
   
   <script type="text/javascript">
         (function(){var l=false,e=[];this.ge$=function(a){return document.getElementById(a)};this.scrollHeader=function(a){if(!l){a=a?a:window.event;a=a.target?a.target:a.srcElement;if(a.nodeType==3)a=a.parentNode;var b=a.id.replace(":scroller",""),g=ge$(b+":scroller:fx");a=ge$(b+":scroller");g.style.left=0-a.scrollLeft+"px";if(b=ge$(b+"_CFB")){g=parseInt(b.getAttribute("dmt"));b.style.marginTop=0-(a.scrollTop+g)+"px"}}};this.fxheader=function(){if(!l){l=true;for(var a=0;a<e.length;a++){var b=ge$(e[a].tid), g=e[a].swidth+"";if(g.indexOf("%")>=0){var i=ge$(e[a].tid+":scroller:fx");i.style.width="0px";g=parseInt(g);g=(document.body.offsetWidth?document.body.offsetWidth:window.innerWidth)*g/100;i.style.width="9999px"}b.style.width=parseInt(g-18)+"px";i=ge$(e[a].tid+":scroller:fx");i.style.marginLeft="0px";i.style.display="";var d=i.childNodes,c;for(c=0;c<d.length;c++)i.removeChild(d[c]);var f=b.cloneNode(false);f.id=e[a].tid+"__cN";f.style.width=document.all?b.offsetWidth+"px":"auto";f.style.marginTop= "0px";f.style.marginLeft="0px";d=document.createElement("thead");d.style.padding="0px";d.style.margin="0px";for(c=0;c<e[a].noOfRows;c++){var h=b.rows[c].cloneNode(true);d.appendChild(h)}f.appendChild(d);i.appendChild(f);var k;if(e[a].noOfCols>0){k=f.cloneNode(true);k.id=e[a].tid+"_CFH"}for(c=d=0;c<e[a].noOfRows;c++){h=f.rows[c].cells;var m,n=b.rows[c].cells,j;if(k){m=k.rows[c].cells;for(j=0;j<h.length;j++)h[j].style.width=m[j].style.width=n[j].offsetWidth-3+"px"}else for(j=0;j<h.length;j++)h[j].style.width= n[j].offsetWidth-3+"px";d+=b.rows[c].offsetHeight}b.style.marginTop="-"+d+"px";f=e[a].sheight;if(b.offsetHeight<f)f=b.offsetHeight+18;h=0;if(e[a].noOfCols>0){for(c=0;c<e[a].noOfCols;c++)h+=b.rows[0].cells[c].offsetWidth;b.style.marginLeft="-"+h+"px";b.style.display="block";i.style.marginLeft="-"+h+"px";ge$(e[a].tid+":scroller:fxcol").style.width=h+"px";c=ge$(e[a].tid+":scroller:fxCH");i=ge$(e[a].tid+":scroller:fxCB");c.innerHTML="";i.innerHTML="";c.appendChild(k);c.style.height=d+"px";i.style.height= f-d+"px";b=b.cloneNode(true);b.id=e[a].tid+"_CFB";b.style.marginLeft="0px";b.setAttribute("dmt",d);i.appendChild(b)}g=parseInt(g)-h+"px";ge$(e[a].tid+":scroller").style.height=f-d+"px";ge$(e[a].tid+":scroller").style.width=g;ge$(e[a].tid+":scroller:fx:OuterDiv").style.height=d+"px";ge$(e[a].tid+":scroller:fx:OuterDiv").style.width=g}window.onresize=fxheader;l=false}};this.fxheaderInit=function(a,b,g,i){var d={},c=ge$(a);d.tid=a;d.sheight=b;d.swidth=c.width;if(!d.swidth||d.swidth.length==0){d.swidth= c.style.width;if(!d.swidth)d.swidth="100%";if(d.swidth.indexOf("%")==-1)d.swidth=parseInt(d.swidth)}d.noOfRows=g;d.noOfCols=i;if(!ge$(a+":scroller")){var f=ge$(a);b=f.parentNode;g=f.nextSibling;c=document.createElement("div");c.id=a+":scroller";c.style.cssText="height:auto;overflow-x:auto;overflow-y:auto;width:auto;";c.onscroll=scrollHeader;c.appendChild(f);f=document.createElement("div");f.id=a+":scroller:fx:OuterDiv";f.style.cssText="position:relative;width:auto;overflow:hidden;overflow-x:hidden;padding:0px;margin:0px;"; f.innerHTML='<div id="'+a+':scroller:fx" style="text-align:left;position:relative;width:9999px;padding:0px;margin-left:0px;"><font size="3" color="red">Please wait while loading the table..</font></div>';var h=null;if(i>0){h=document.createElement("div");h.id=a+":scroller:fxcol";h.style.cssText="width:0px;height:auto;display:block;float:left;overflow:hidden;";h.innerHTML="<div id='"+a+":scroller:fxCH' style='width:100%;overflow:hidden;'>&nbsp;</div><div id='"+a+":scroller:fxCB' style='width:100%;overflow:hidden;'>&nbsp;</div>"}if(g){h&& b.insertBefore(h,g);b.insertBefore(f,g);b.insertBefore(c,g)}else{h&&b.appendChild(h);b.appendChild(f);b.appendChild(c)}}e[e.length]=d}})();
   </script>

   <script type="text/javascript">
   
    function refresh() {
      var vsd_uri = document.forms["paginationForm"].vsd_uri;
      var nav_type = document.forms["paginationForm"].nav_type;
      var view = document.forms["paginationForm"].view;
      var resultsPerPage = document.forms["paginationForm"].getElementById('resultsPerPage').value;
      
      window.location.href="/ncitbrowser/pages/download_value_set.jsf?"
          + "vsd_uri="+ vsd_uri
          + "&nav_type="+ nav_type
          + "&resultsPerPage="+ resultsPerPage
          + "&view="+ view;
    }

    function onCodeButtonPressed(formname) {
	  var algorithmObj = document.forms[formname].valueset_search_algorithm;
	  for (var j=0; j<algorithmObj.length; j++) {
		  algorithm = algorithmObj[j].value;
		  if (algorithm == "exactMatch") {
			 algorithmObj[j].checked = true;
		  }
	  }
    }

    function getSearchTarget(formname) {
          var searchTargetObj = document.forms[formname].selectValueSetSearchOption;
	  for (var j=0; j<searchTargetObj.length; j++) {
	      if (searchTargetObj[j].checked == true) {
	         return searchTargetObj[j].value;
	      }
	  }
    }

    function onAlgorithmChanged(formname) {
      var curr_target = getSearchTarget(formname);
      if (curr_target != "Code") return;

          var searchTargetObj = document.forms[formname].selectValueSetSearchOption;
	  for (var j=0; j<searchTargetObj.length; j++) {
		  target = searchTargetObj[j].value;
		  if (target == "Code") {
			  searchTargetObj[0].checked = true;
			  return;
		  }
	  }
    }	
    
       
  
  </script>   
   
  
   
   <%!private static Logger _logger = Utils.getJspLogger("value_set_search_results.jsp");%>
   <f:view>
      <!-- Begin Skip Top Navigation -->
      <a href="#evs-content" class="hideLink" accesskey="1" title="Skip repetitive navigation links">skip navigation links</a>
      <!-- End Skip Top Navigation -->
      <%@ include file="/pages/templates/header.jsp"%>
      <div class="center-page_960">
         <%@ include file="/pages/templates/sub-header.jsp"%>
         <!-- Main box -->
         <div id="main-area_960">
         
         
         


<%
String message = (String) request.getSession().getAttribute("message");
request.getSession().removeAttribute("message");
System.out.println(message);   

String name = null;
String valueset_uri = null;
String description = null;
String concept_domain = null;
String sources = null;
String supportedsources = null;
int num_pages = 0;
String selectedResultsPerPage = "50";
ResolvedValueSetIteratorHolder rvsi = null;
int page_num = 0;
int istart = 0;
int iend = 0;
String resultsPerPage = null;
String page_number = null;
int pageSize = 50;
int size = 0;

    String vsd_uri = HTTPUtils.cleanXSS((String) request.getParameter("vsd_uri"));
    if (DataUtils.isNullOrBlank(vsd_uri)) {
	vsd_uri = (String) request.getSession().getAttribute("vsd_uri");
    }   
    
    
            		String metadata = DataUtils
            				.getValueSetDefinitionMetadata(DataUtils
            						.findValueSetDefinitionByURI(vsd_uri));
            		Vector u = DataUtils.parseData(metadata);
            		name = (String) u.elementAt(0);
            		valueset_uri = (String) u.elementAt(1);
            		description = (String) u.elementAt(2);
            		concept_domain = (String) u.elementAt(3);
            		sources = (String) u.elementAt(4);
            		supportedsources = (String) u.elementAt(5);    
    
    
    
    
    
    request.getSession().setAttribute("vsd_uri", vsd_uri); 
    String vsd_name = DataUtils.getValueSetName(vsd_uri);





%>

<div class="bannerarea_960">
    <div class="banner">
	    <a class="vocabularynamebanner" href="<%=request.getContextPath()%>/ajax?action=create_src_vs_tree&vsd_uri=<%=vsd_uri%>">
     
	<div class="vocabularynamebanner">
	
<%
if (vsd_name == null) vsd_name = "Not specified";
if (vsd_name.length() < HTTPUtils.ABS_MAX_STR_LEN) {
%>
	
		  <div class="vocabularynameshort" STYLE="font-size: <%=HTTPUtils.maxFontSize(vsd_name)%>px; font-family : Arial">
		    <%=HTTPUtils.cleanXSS(vsd_name)%>
		  </div>
<%		  
} else {


%>


		  <div class="vocabularynameshort" STYLE="font-size: <%=HTTPUtils.maxFontSize(vsd_name)%>px; font-family : Arial;">
		    <%=HTTPUtils.cleanXSS(vsd_name)%>
		  </div>

<%
}
%>
		  
		  
		  
	</div>
  
	    </a>
    

    </div>
	
    <div class="search-globalnav_960">
        <!-- Search box -->
        <div class="searchbox-top"><img src="<%=basePath%>/images/searchbox-top.gif" width="352" height="2" alt="SearchBox Top" /></div>
        <div class="searchbox">
        

<%


  String checked_valuesets = (String) request.getSession().getAttribute("checked_vocabularies");
  String match_text = gov.nih.nci.evs.browser.utils.HTTPUtils
    .cleanXSS((String) request.getSession().getAttribute("matchText_RVS"));

  if (match_text == null) match_text = "";
  

    String userAgent = request.getHeader("user-agent");
    boolean isIE = userAgent != null && userAgent.toLowerCase().contains("msie");


if (!DataUtils.isNullOrBlank(vsd_uri)) {
    System.out.println("set checked_valuesets = " + vsd_uri);
    checked_valuesets = vsd_uri;
}

  String termbrowser_displayed_match_text = HTTPUtils.convertJSPString(match_text);
  String searchform_requestContextPath = request.getContextPath();
  searchform_requestContextPath = searchform_requestContextPath.replace("//ncitbrowser//ncitbrowser", "//ncitbrowser");

    String algorithm = HTTPUtils.cleanXSS((String) request.getSession().getAttribute("valueset_search_algorithm"));
    String check_e = "", check_s = "" , check_c ="";

    if (algorithm == null || algorithm.compareTo("contains") == 0)
      check_c = "checked";
    else if (algorithm.compareTo("startsWith") == 0)
      check_s= "checked";
    else
      check_e = "checked";
      
      
        String searchTarget = (String) request.getSession().getAttribute("searchTarget");
        String check_n = "", check_p = "" , check_cd ="";
        

    if (DataUtils.isNullOrBlank(searchTarget)) {
        check_n = "checked";
    } else if (searchTarget.compareTo("Code") == 0) {
        check_cd = "checked";
    } else if (searchTarget.compareTo("Name") == 0) {
        check_n = "checked";
    } else if (searchTarget.compareTo("properties") == 0) {
          check_p= "checked";
    }
     
%>
<!--  
<form id="resolvedValueSetSearchForm" name="resolvedValueSetSearchForm" method="post" action="/ncitbrowser/ajax?action=search_value_set" class="search-form" accept-charset="UTF-8" enctype="application/x-www-form-urlencoded">
-->
<form id="resolvedValueSetSearchForm" name="resolvedValueSetSearchForm" method="post" action="/ncitbrowser/ajax?action=search_downloaded_value_set" class="search-form" accept-charset="UTF-8" enctype="application/x-www-form-urlencoded">

<input type="hidden" name="resolvedValueSetSearchForm" value="resolvedValueSetSearchForm" />


    <input CLASS="searchbox-input" id="matchText" name="matchText" value="<%=termbrowser_displayed_match_text%>" onFocus="active=true"
        onBlur="active=false"  onkeypress="return submitEnter('resolvedValueSetSearchForm:resolvedvalueset_search',event)" tabindex="1"/>

    <input id="resolvedValueSetSearchForm:resolvedvalueset_search" type="image" src="/ncitbrowser/images/search.gif" name="resolvedValueSetSearchForm:resolvedvalueset_search" accesskey="13" alt="Search concepts in value set" onclick="javascript:cursor_wait();" tabindex="2" class="searchbox-btn" /><a href="/ncitbrowser/pages/help.jsf#searchhelp" tabindex="3"><img src="/ncitbrowser/images/search-help.gif" alt="Search Help" style="border-width:0;" class="searchbox-btn" /></a>



  <table border="0" cellspacing="0" cellpadding="0" width="340px">

    <tr valign="top" align="left">
      <td align="left" class="textbody" colspan="2">
        <input type="radio" name="valueset_search_algorithm" id="contains" value="contains" alt="Contains" <%=check_c%> tabindex="4" onclick="onAlgorithmChanged('resolvedValueSetSearchForm');"/><label for="contains">Contains</label>
        <input type="radio" name="valueset_search_algorithm" id="exactMatch" value="exactMatch" alt="Exact Match" <%=check_e%> tabindex="4"/><label for="exactMatch">Exact Match&nbsp;</label>
        <input type="radio" name="valueset_search_algorithm" id="startsWith" value="startsWith" alt="Begins With" <%=check_s%> tabindex="4" onclick="onAlgorithmChanged('resolvedValueSetSearchForm');"/><label for="startsWith">Begins With&nbsp;</label>
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
        <input type="radio" name="selectValueSetSearchOption" id="selectValueSetSearchOption" value="Name" alt="Name" <%=check_n%> tabindex="5"/><label for="names">Name&nbsp;</label>
        <input type="radio" name="selectValueSetSearchOption" id="selectValueSetSearchOption" value="Code" alt="Code" <%=check_cd%> tabindex="5" onclick="onCodeButtonPressed('resolvedValueSetSearchForm');" /><label for="codes">Code&nbsp;</label>
      </td>
    </tr>
  
    
    <tr><td height="5px;"></td></tr>
    <tr><td colspan="2">
      <table border="0" cellspacing="0" cellpadding="0" width="100%">
        <tr valign="top">

    <input type="hidden" name="referer" id="referer" value="<%=HTTPUtils.getRefererParmEncode(request)%>" />
<%
if (vsd_uri != null) {
%>
<input type="hidden" name="vsd_uri" id="vsd_uri" value="<%=vsd_uri%>" />
<%
}
%>
<%
if (!DataUtils.isNullOrBlank(checked_valuesets)) {
%>
    <input type="hidden" name="checked_vocabularies" id="checked_vocabularies" value="<%=checked_valuesets%>" />
<%
}
%> 

      <input type="hidden" id="nav_type" name="nav_type" value="valuesets" />
      <input type="hidden" id="view" name="view\ value="source" />
      <input type="hidden" id="vsd_uri" name="vsd_uri" value="<%=vsd_uri%>" />  

</form>
        </tr>
      </table>
    </td></tr>
  </table>

</div>
        
        
        <div class="searchbox-bottom"><img src="<%=basePath%>/images/searchbox-bottom.gif" width="352" height="2" alt="SearchBox Bottom" /></div>
        <!-- end Search box -->
        <!-- Global Navigation -->
            <%@ include file="/pages/templates/menuBar-termbrowserhome.jsp" %>
        <!-- end Global Navigation -->
    </div>
    
</div>

<!-- end Thesaurus, banner search area -->
<!-- Quick links bar -->
<%@ include file="/pages/templates/quickLink.jsp" %>
            
            
            <%
            
Vector matched_concept_codes = (Vector) request.getSession().getAttribute("matched_concept_codes");
request.getSession().removeAttribute("matched_concept_codes");            
            
            		int numRemaining = 0;
            		String valueSetSearch_requestContextPath = request.getContextPath();

String table_content = "";
StringBuffer table_content_buf = new StringBuffer();

rvsi = (ResolvedValueSetIteratorHolder) request.getSession().getAttribute("rvsi");
if (rvsi != null) {

			table_content_buf.append(rvsi.getOpenTableTag("rvs_table"));
			List list = rvsi.getResolvedValueSetList();

/*

            		resultsPerPage =  HTTPUtils.cleanXSS((String) request.getParameter("resultsPerPage"));
            		if (resultsPerPage == null) {
            			resultsPerPage = (String) request.getSession()
            					.getAttribute("resultsPerPage");
            			if (resultsPerPage == null) {
            				resultsPerPage = "50";
            			}

            		} else {
            			request.getSession().setAttribute("resultsPerPage", resultsPerPage);
            		}

            		selectedResultsPerPage = resultsPerPage;

            		page_number = HTTPUtils.cleanXSS((String) request.getParameter("page_number"));
            		int pageNum = 0;

            		if (page_number != null) {
            			pageNum = Integer.parseInt(page_number);
            		} else {
            			pageNum = 1;
            		}

            		page_num = pageNum;

            		pageSize = Integer.parseInt(resultsPerPage);
            		
           		size = list.size() - 1;
            		numRemaining = size;

            		num_pages = size / pageSize;
            		if (num_pages * pageSize < size)
            			num_pages++;


            		istart = (page_num - 1) * pageSize;
            		if (istart < 0)
            			istart = 0;

            		iend = istart + pageSize - 1;
            		if (iend > size) {
            			iend = size;
            		}
            		
            		if (istart > 0) {
			    String first_line = (String) list.get(0);
			    first_line = first_line.replaceAll("td", "th");
			    table_content_buf.append(first_line);
            		}
            		
			for (int k=istart; k<iend+1; k++) {
			    String line = (String) list.get(k);
			    table_content_buf.append(line);
			}
			
*/



boolean filter = false;
if (matched_concept_codes != null && matched_concept_codes.size() > 0) {
    filter = true;
}

			    String first_line = (String) list.get(0);
			    first_line = first_line.replaceAll("td", "th");
			    table_content_buf.append(first_line);
			    
			    for (int k=1; k<list.size(); k++) {
				    String line = (String) list.get(k);
				    boolean include = true;
				    if (filter) {
				        include = false;
				        for (int m=0; m<matched_concept_codes.size(); m++) {
				            String matched_concept_code = (String) matched_concept_codes.elementAt(m);
				            if (line.indexOf(matched_concept_code) != -1) {
				                include = true;
				                break;
				            }
				        }
				    }
				    if (include) table_content_buf.append(line);
			    }
			
			
			table_content_buf.append(rvsi.getCloseTableTag());
			table_content = table_content_buf.toString();
}
            		
            %>
            
            <div class="pagecontent"> 
            
               <a name="evs-content" id="evs-content"></a>               
                  <div class="tabTableContentContainer">
                  <h:form id="valueSetSearchResultsForm" styleClass="search-form" acceptcharset="UTF-8">
                     <%
                     	if (!DataUtils.isNull(message)) {
                     %>
                     <p class="textbodyred">
                        &nbsp;<%=message%></p>
                     <%
                        } else {
                     %>
                     <table border="0">
                        <tr>
                           <td>
                              <table border="0" width="95%">
                                 <tr>
                                    <td align="left" class="texttitle-blue">Value Set:&nbsp;<%=vsd_uri%></td>
                                    <td align="center">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
                                    <td align="right">
                                    
                                       <h:commandLink
                                          value="Export Excel"
                                          action="#{valueSetBean.exportToExcelAction}"
                                          styleClass="texttitle-blue-small"
                                          title="Export VSD in MS Excel format" />

<a title="Download Plugin Microsoft Excel Viewer" href="http://www.microsoft.com/downloads/details.aspx?FamilyID=1cd6acf9-ce06-4e1c-8dcf-f33f669dbc3a&amp;DisplayLang=en" target="_blank"><img
     src="http://evs.nci.nih.gov/link_xls.gif" width="16"
     height="16" border="0"
alt="Download Plugin Microsoft Excel Viewer" /></a>
    
                                       | <h:commandLink
                                          value="Export CSV"
                                          action="#{valueSetBean.exportToCSVAction}"
                                          styleClass="texttitle-blue-small"
                                          title="Export VSD in CSV format" />
                                    </td>
                                 </tr>
                              </table>
                           </td>
                        </tr>
                        <tr class="textbodyred">
                           <td></td>
                        </tr>
                        <tr class="textbody">
                           <td><b>Name</b>: <%=DataUtils.encodeTerm(name)%></td>
                        </tr>
                        <tr class="textbody">
                           <td><b>Description</b>: <%=description%></td>
                        </tr>
                        <tr class="textbody">
                           <td><b>Concept Domain</b>: <%=concept_domain%></td>
                        </tr>
                        <tr class="textbody">
                           <td><b>Sources</b>: <%=supportedsources%></td>
                        </tr>
                        <tr class="textbody">
                           <td>&nbsp;</td>
                        </tr>
                        <tr class="textbody">
<%                        
if (matched_concept_codes == null) {                       
%>                        
                           <td><b>Concepts</b>:</td>
<%
} else {
%>
<td>
<table border="0" width="95%">
<tr class="textbody">
<td align=left><b>Concepts</b>:</td>
<td align=right>
<a href="/ncitbrowser/ajax?action=download&vsd_uri=<%=vsd_uri%>">
<img src="/ncitbrowser/images/released_file.gif" alt="Value Set Released Files (FTP Server)" border="0" tabindex="2"></a>
</td>
</tr>
</table>
</td>
<%
}     
%>    
                           
                        </tr>
                        <tr class="textbody">
                           <td>


            <div style="float:left;width:360px;">
            <!--
                        <table id="rvs_table" width="900" class="mt">
             -->
  
<!-- Insert table here -->

<%=table_content%>
  
 </div>

                 
                     </table>
                     <%
                     	}
                     %>
                     <input type="hidden" name="vsd_uri" id="vsd_uri" value="<%=vsd_uri%>">
                     <input type="hidden" name="from_download" id="from_download" value="true">
                     <input type="hidden" name="referer" id="referer" value="<%=HTTPUtils.getRefererParmEncode(request)%>">
                     </h:form>
                     
               </div> <!-- end tabTableContentContainer -->
               
               
<%
boolean show_pagination = false;
if (rvsi != null && show_pagination) {

	int prev_page_num = page_num-1;
	int next_page_num = page_num+1;
	String istart_str = Integer.toString(istart);
	String iend_str = Integer.toString(iend+1);

	String match_size = Integer.toString(numRemaining);

	String prev_page_num_str = Integer.toString(prev_page_num);
	String next_page_num_str = Integer.toString(next_page_num);                 

%>
               
               
               
 <FORM NAME="paginationForm" METHOD="POST" action="<%=request.getContextPath() %>/ajax" >
 
  <table>
    <tr>
      <td class="textbody" align=left>
    
      <b>Results <%=istart_str%>-<%=iend_str%> of&nbsp;<%=match_size%></b>
        
      </td>
      <td>
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      </td>
      <td class="textbody" align=right>
        <%
          if (page_num > 1) {
        %>
        &nbsp;
        <i>
          <a href="<%=request.getContextPath() %>/pages/download_value_set.jsf?nav_type=valuesets&page_number=<%=prev_page_num_str%>">Prev</a>
        </i>&nbsp;
        <%
          }
          if (num_pages > 1) {
          
          
          int sliding_window_start = 1;
          int sliding_window_end = num_pages;
          int sliding_window_half_width = NCItBrowserProperties.getSlidingWindowHalfWidth();
          
          sliding_window_start = page_num - sliding_window_half_width;
          if (sliding_window_start < 1) sliding_window_start = 1;
          
          sliding_window_end = sliding_window_start + sliding_window_half_width * 2 - 1;
          if (sliding_window_end > num_pages) sliding_window_end = num_pages;
       
          
		 for (int idx=sliding_window_start; idx<=sliding_window_end; idx++) { 
		    String idx_str = Integer.toString(idx);
		    if (page_num != idx) {
		      %>
			<a href="<%=request.getContextPath() %>/pages/download_value_set.jsf?nav_type=valuesets&page_number=<%=idx_str%>"><%=idx_str%></a>
			&nbsp;
		      <%
		    } else {
		      %>
			<%=idx_str%>&nbsp;
		      <%
		    }
		  }
          }
          
          if (num_pages > 1 && next_page_num <= num_pages) {
        %>
          &nbsp;
          <i>
            <a href="<%=request.getContextPath() %>/pages/download_value_set.jsf?nav_type=valuesets&page_number=<%=next_page_num_str%>">Next</a>
          </i>
        <%
          }
        %>
          </td>
    </tr>
    <tr>
      <td class="textbody" align=left>
        Show

  <select id=resultsPerPage name=resultsPerPage size=1 onChange="paginationForm.submit();">
  
  <%
    List resultsPerPageValues = UserSessionBean.getResultsPerPageValues();
    for (int i=0; i<resultsPerPageValues.size(); i++) {
        String resultsPerPageValue = (String) resultsPerPageValues.get(i);
        
        if (selectedResultsPerPage.compareTo(resultsPerPageValue) == 0) {
  %>      
        <option value="<%=resultsPerPageValue%>" selected><%=resultsPerPageValue%></option>
  <%        
        
        } else {
  %>      
        <option value="<%=resultsPerPageValue%>"><%=resultsPerPageValue%></option>
  <%        
        }

  }
  %>
  </select>
 
        &nbsp;results per page
        
      </td>
      <td>
        &nbsp;&nbsp;
      </td>
      <td>
        &nbsp;
      </td>
    </tr>
  </table>
  
  <input type="hidden" id="nav_type" name="nav_type" value="valuesets" />
  <input type="hidden" id="view" name="view" value="source" /> 
  <input type="hidden" id="refresh" name="refresh" value="true" />  
  <input type="hidden" id="action" name="action" value="values" /> 
  <input type="hidden" id="vsd_uri" name="vsd_uri" value="<%=vsd_uri%>" />  
  
</form>              
               

<%
}
%>
               <%@ include file="/pages/templates/nciFooter.jsp"%>
            </div><!-- end Page content -->
         </div><!-- end Main box -->         
      </div>      
      <div class="mainbox-bottom"><img src="<%=basePath%>/images/mainbox-bottom.gif" width="945" height="5" alt="Mainbox Bottom" /></div>
   </f:view>


        <script type="text/javascript">
            fxheaderInit('rvs_table',300,1,0);
            fxheader();
        </script>
        

</body>
</html>
