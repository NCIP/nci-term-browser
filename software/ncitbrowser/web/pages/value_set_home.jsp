<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8"%>
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
<%@ page import="org.LexGrid.valueSets.ValueSetDefinition" %>
<%@ page import="org.LexGrid.concepts.Entity" %>

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


<script type="text/javascript">
var tree;
(function() {
    function treeInit() {
        buildRandomTextNodeTree();
    }


    function buildRandomTextNodeTree() {
	//instantiate the tree:
        tree = new YAHOO.widget.TreeView("treecontainer");
        for (var i = 0; i < Math.floor((Math.random()*4) + 3); i++) {
            var tmpNode = new YAHOO.widget.TextNode("label-" + i, tree.getRoot(), false);
            // tmpNode.collapse();
            // tmpNode.expand();
            // buildRandomTextBranch(tmpNode);
            buildLargeBranch(tmpNode);
        }

       // Expand and collapse happen prior to the actual expand/collapse,
       // and can be used to cancel the operation
       tree.subscribe("expand", function(node) {
              YAHOO.log(node.index + " was expanded", "info", "example");
              // return false; // return false to cancel the expand
           });

       tree.subscribe("collapse", function(node) {
              YAHOO.log(node.index + " was collapsed", "info", "example");
           });

       // Trees with TextNodes will fire an event for when the label is clicked:
       tree.subscribe("labelClick", function(node) {
           YAHOO.log(node.index + " label was clicked", "info", "example");
       });
	//The tree is not created in the DOM until this method is called:
       tree.draw();
    }

	//function builds 10 children for the node you pass in:
    function buildLargeBranch(node) {
        if (node.depth < 10) {
            YAHOO.log("buildRandomTextBranch: " + node.index, "info", "example");
            for ( var i = 0; i < 10; i++ ) {
                   var o = {
                     label: node.label + "-" + i,
                     // Tooltip will use the title attribute
                     title: "This is " + node.label + "-" + i,

                     href: "http://www.google.com",
                     target: "_new"

                   };

                   var tmpNode = new YAHOO.widget.TextNode(o, node, false);


            }
        }
    }
    YAHOO.util.Event.onDOMReady(treeInit);
})();
</script>


<%
    String vd_uri = null;
    boolean isValueSet = true;
    String valueSetSearch_requestContextPath = request.getContextPath();
    String selected_ValueSetSearchOption = HTTPUtils.cleanXSS((String) request.getSession().getAttribute("selectValueSetSearchOption")); 
    String vsd_uri = HTTPUtils.cleanXSS((String) request.getParameter("vsd_uri"));

System.out.println("(*) value_set_home.jsp vsd_uri: " + vsd_uri);


    String uri_vsd = null;
    String vsd_name = null;  
    
    String selectedvalueset = null;
    ValueSetDefinition vsd = DataUtils.findValueSetDefinitionByURI(vsd_uri);
    
    if (vsd != null) {
            vsd_name = vsd.getValueSetDefinitionName();  
	    TreeItem ti = DataUtils.getValueSetHierarchy().getSourceValueSetTreeBranch(vsd);
	    HashMap hmap = new HashMap();
	    hmap.put("<Root>", ti);
    } else {
            isValueSet = false;
            Entity entity = DataUtils.getConceptByCode(Constants.TERMINOLOGY_VALUE_SET_NAME, null, vsd_uri); 
            if (entity != null) {
            	vsd_name = "";
            	if (entity.getEntityDescription() != null) {
            	    vsd_name = entity.getEntityDescription().getContent();
            	}
            }
    }

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
    

    
    String match_text = gov.nih.nci.evs.browser.utils.HTTPUtils
        .cleanXSS((String) request.getSession().getAttribute("matchText"));

    if (match_text == null) match_text = "";

    String userAgent = request.getHeader("user-agent");
    boolean isIE = userAgent != null && userAgent.toLowerCase().contains("msie");

    String uri_str = HTTPUtils.cleanXSS((String) request.getParameter("vsd_uri"));
    
    if (uri_str == null) {
        uri_str = (String) request.getSession().getAttribute("vsd_uri");
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
    
    String vsd_description = DataUtils.getValueSetHierarchy().getValueSetDecription(vsd_uri);
    if (vsd_description == null) {
        vsd_description = "DESCRIPTION NOT AVAILABLE";
    }    
    
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



                      <%
                         if (isValueSet) {
                       %>         
        
      <div class="bannerarea_960">

      <div class="banner">
        <a class="vocabularynamebanner" href="/ncitbrowser/pages/value_set_search_results.jsf?vsd_uri=http://ncit:C81222">
	      <div class="vocabularynamebanner">
            <div class="vocabularynameshort" STYLE="font-size: 22px; font-family : Arial">
              <%=DataUtils.encodeTerm(vsd_name)%>
            </div>
          </div>
	    </a>
      </div>

        <div class="search-globalnav_960">        
        
          <!-- Search box -->
          <div class="searchbox-top"><img src="/ncitbrowser/images/searchbox-top.gif" width="352" height="2" alt="SearchBox Top" /></div>
          
<div class="searchbox">

<form id="resolvedValueSetSearchForm" name="resolvedValueSetSearchForm" method="post" action="/ncitbrowser/pages/value_set_search_results.jsf" class="search-form" accept-charset="UTF-8" enctype="application/x-www-form-urlencoded">
<input type="hidden" name="resolvedValueSetSearchForm" value="resolvedValueSetSearchForm" />
   
  <div class="textbody"> 
    <input CLASS="searchbox-input" id="matchText" name="matchText" value="" onFocus="active=true"
        onBlur="active=false"  onkeypress="return submitEnter('resolvedValueSetSearchForm:resolvedvalueset_search',event)" tabindex="1"/>
    <input id="resolvedValueSetSearchForm:resolvedvalueset_search" type="image" src="/ncitbrowser/images/search.gif" name="resolvedValueSetSearchForm:resolvedvalueset_search" accesskey="13" alt="Search Value Set" onclick="javascript:cursor_wait();" tabindex="2" class="searchbox-btn" /><a href="/ncitbrowser/pages/help.jsf#searchhelp" tabindex="3"><img src="/ncitbrowser/images/search-help.gif" alt="Search Help" style="border-width:0;" class="searchbox-btn" /></a>
    

  <table border="0" cellspacing="0" cellpadding="0" width="340px">
    <tr valign="top" align="left">
      <td align="left" class="textbody" colspan="2">
        <input type="radio" name="algorithm" id="contains" value="contains" alt="Contains"  tabindex="4" onclick="onAlgorithmChanged('resolvedValueSetSearchForm');"/><label for="contains">Contains</label>
        <input type="radio" name="algorithm" id="exactMatch" value="exactMatch" alt="Exact Match" checked tabindex="4"/><label for="exactMatch">Exact Match&nbsp;</label>
        <input type="radio" name="algorithm" id="startsWith" value="startsWith" alt="Begins With"  tabindex="4" onclick="onAlgorithmChanged('resolvedValueSetSearchForm');"/><label for="startsWith">Begins With&nbsp;</label>
      </td>
    </tr>
    
    <tr align="left">
      <td width="263px" height="1px" bgcolor="#2F2F5F"></td>
      <!-- The following lines are needed to make "Advanced Search" link flush right -->
      
          <td width="77px"></td>
      
    </tr>
    <tr valign="top" align="left">
      <td align="left" class="textbody" colspan="2">
        <input type="radio" name="searchTarget" id="names" value="names" alt="Names" checked tabindex="5"/><label for="names">Name&nbsp;</label>
        <input type="radio" name="searchTarget" id="codes" value="codes" alt="Code"  tabindex="5" onclick="onCodeButtonPressed('resolvedValueSetSearchForm');" /><label for="codes">Code&nbsp;</label>
        <input type="radio" name="searchTarget" id="properties" value="properties" alt="Properties"  tabindex="5"/><label for="properties">Property</label>
      </td>
    </tr>

    <tr><td height="5px;"></td></tr>
   </table>

    <input type="hidden" name="referer" id="referer" value="<%=HTTPUtils.getRefererParmEncode(request)%>">
    <input type="hidden" name="vsd_uri" id="vsd_uri" value="<%=vsd_uri%>" />

</div>



<input type="hidden" name="javax.faces.ViewState" id="javax.faces.ViewState" value="j_id3:j_id4" />
</form>

</div>

          
          <div class="searchbox-bottom"><img src="/ncitbrowser/images/searchbox-bottom.gif" width="352" height="2" alt="SearchBox Bottom" /></div>
          <!-- end Search box -->
          
          <!-- Global Navigation -->

<table class="global-nav" border="0" width="100%" height="33px" cellpadding="0" cellspacing="0">
  <tr>
    <td align="left" valign="bottom">
      <a href="#" onclick="javascript:window.open('/ncitbrowser/pages/source_help_info-termbrowser.jsf',
        '_blank','top=100, left=100, height=740, width=780, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');" tabindex="13">
        Sources</a> 
       
    </td>
    <td align="right" valign="bottom">
      <a href="/ncitbrowser/pages/help.jsf" tabindex="16">Help</a>
    </td>
    <td width="7"></td>
  </tr>
</table>
          <!-- end Global Navigation -->
          
          
        </div> <!-- search-globalnav_960 -->
        
        
        
      </div> <!-- bannerarea_960 -->
          
          
                      <%
                         } else {
                       %>
                       


<div class="bannerarea_960">
      <div class="banner">
        <a class="vocabularynamebanner" href="/ncitbrowser/pages/value_set_search_results.jsf?vsd_uri=http://ncit:C81222">
	      <div class="vocabularynamebanner">
            <div class="vocabularynameshort" STYLE="font-size: 22px; font-family : Arial">
              <%=DataUtils.encodeTerm(vsd_name)%>
            </div>
          </div>
	    </a>
      </div>
  
  <div class="search-globalnav_960">
    <img src="<%=basePath%>/images/shim.gif"
      width="1" height="80" alt="Shim" border="0"/>
    <%@ include file="/pages/templates/menuBar-termbrowserhome.jsp" %>
  </div>
</div>


                       
                      <%
                         } 
                       %>                       
                       
          



  
      
      
      
      
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
          
            <% if (message != null) { %>
              <tr class="textbodyred"><td>
              <p class="textbodyred">&nbsp;<%=message%></p>
              </td></tr>
            <% } else { %>

<form id="view_form" enctype="application/x-www-form-urlencoded;charset=UTF-8">
<%
     String VSD_view = (String) request.getSession().getAttribute("view");
%>
     <input type="hidden" id="view" name="view" value="<%=VSD_view%>" />
</form>


 <h:form id="valueSetSearchResultsForm" styleClass="search-form" acceptcharset="UTF-8"> 
 
      <input type="hidden" name="valueset" value="<%=vsd_uri%>">&nbsp;</input>
      
            <tr class="textbody"><td>
                <div id="message" class="textbody">
                   <table border="0" width="100%">
                    <tr>
                      <td>
                         <div class="texttitle-blue">Welcome</div>
                      </td>
                      
                      <%
                         if (isValueSet) {
                       %>  
                      
                      <td class="dataCellText" align="right">
                        <h:commandButton id="Values" value="Values" action="#{valueSetBean.downloadValueSetAction}"
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
                      
                      <%
                         } else {
                       %> 
                           <td>&nbsp;</td>
                       <%
                         }
                       %>                       
                      
                    </tr>
                    <tr><td colspan="2">
                      <hr/>
                    </td></tr>
                  </table>  
                </div>
            </td></tr>
            
            <tr><td>
		 <table class="datatable_960" summary="" cellpadding="3" cellspacing="0" border="0" width="100%">
                     <tr><td>
			      <p class="dataCellText">
			      <%=vsd_description%>
			      </p>
		     </td></tr>
		 </table>
          <input type="hidden" name="view" id="view" value="<%=VSD_view%>">
          <input type="hidden" name="view" id="nav_type" value="valuesets">
          <input type="hidden" name="referer" id="referer" value="<%=HTTPUtils.getRefererParmEncode(request)%>">
          </h:form>
           </td></tr>
 <% } %>
        </table>
        <div id="treecontainer" class="ygtv-checkbox"></div>
        </div> <!--  popupContentArea -->
        <div class="textbody">
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


