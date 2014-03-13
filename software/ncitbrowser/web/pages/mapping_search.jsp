<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="java.util.Collections"%>
<%@ page import="java.util.Vector"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="org.LexGrid.concepts.Entity" %>
<%@ page import="gov.nih.nci.evs.browser.utils.*" %>
<%@ page import="gov.nih.nci.evs.browser.common.Constants" %>
<%@ page import="org.apache.log4j.*" %>
<%!
  private static final String CABIG_APPROVED_MSG = "caBIG approved";
  private static Logger _logger = Utils.getJspLogger("multiple_search.jsp");

  private static String getCabigIndicator(boolean display, String basePath) {
    if (! display)
        return "";
    
    // Added shim.gif image next to the asterisk indicator so we can be
    //   508 compliant.  This associates the alternate text from the shim
    //   to the asterisk.
    String cabig_msg = "<img src=\"" + basePath + "/images/shim.gif\""
      // + " width=\"1\" height=\"1\""
      + " alt=\"" + CABIG_APPROVED_MSG + "\"" + ">";
    return " <b>*</b> " + cabig_msg;
  }
%>
<%
  int mapping_cs_knt = 0;

  String mapping_scheme_and_version = (String) request.getSession().getAttribute("scheme_and_version");
  if (mapping_scheme_and_version == null) {
      mapping_scheme_and_version = "";
  }

  String ncit_build_info = new DataUtils().getNCITBuildInfo();
  String application_version = new DataUtils().getApplicationVersion();
  String anthill_build_tag_built = new DataUtils().getNCITAnthillBuildTagBuilt();
  String evs_service_url = new DataUtils().getEVSServiceURL();
  
  String requestContextPath = request.getContextPath();
  requestContextPath = requestContextPath.replace("//ncitbrowser//ncitbrowser", "//ncitbrowser");
  boolean display_cabig_approval_indicator_note = false;
  Integer curr_sort_category = null;
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns:c="http://java.sun.com/jsp/jstl/core">
<head>
  <title>NCI Term Browser</title>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />
  <link rel="shortcut icon" href="<%= request.getContextPath() %>/favicon.ico" type="image/x-icon" />
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/script.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/search.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/dropdown.js"></script>
</head>
<!--
<body onload="checkVisited();">
-->
<body onLoad="document.forms.searchTerm.matchText.focus();">
<!--
   Build info: <%=ncit_build_info%>
 Version info: <%=application_version%>
          Tag: <%=anthill_build_tag_built%>
   LexEVS URL: <%=evs_service_url%>
  -->
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/wz_tooltip.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/tip_centerwindow.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/tip_followscroll.js"></script>
  <script language="JavaScript">
     function checkVisited() {
       var test = '<%= request.getSession().getAttribute("visited") %>';
       if (test == "" || test == "null")
         checkAllButOne(document.searchTerm.ontology_list, 'Metathesaurus');
     }
     
     
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
<%
    request.getSession().removeAttribute("dictionary");
    
Vector display_name_vec = (Vector) request.getSession().getAttribute("display_name_vec");

String ontologiesToSearchOn = (String) request.getSession().getAttribute("ontologiesToSearchOnStr");
if (ontologiesToSearchOn == null) {
    ontologiesToSearchOn = DataUtils.getDefaultOntologiesToSearchOnStr();
    request.getSession().setAttribute("ontologiesToSearchOnStr", ontologiesToSearchOn);
}



if (display_name_vec == null) {

  display_name_vec = new Vector();
  List ontology_list = DataUtils.getOntologyList();

  for (int i = 0; i < ontology_list.size(); i++) {
    SelectItem item = (SelectItem) ontology_list.get(i);
    String value = (String) item.getValue();
    String label = (String) item.getLabel();

    String scheme = DataUtils.key2CodingSchemeName(value);
    String version = DataUtils.key2CodingSchemeVersion(value);

    String display_name = DataUtils.getMetadataValue(scheme, version, "display_name");
    if (DataUtils.isNull(display_name)) {
	//if (display_name == null || display_name.compareTo("null") == 0)
	display_name = DataUtils.getLocalName(scheme);
    }

    String sort_category = DataUtils.getMetadataValue(
	scheme, version, "vocabulary_sort_category");

    String short_scheme_name = DataUtils.uri2CodingSchemeName(scheme);
   //OntologyInfo info = new OntologyInfo(scheme, display_name, version, label, sort_category);
    OntologyInfo info = new OntologyInfo(short_scheme_name, display_name, version, label, sort_category);
    display_name_vec.add(info);

  }


  for (int i = 0; i < display_name_vec.size(); i++) { 
     OntologyInfo info = (OntologyInfo) display_name_vec.elementAt(i);
     if (!DataUtils.isNull(info.getTag()) && info.getTag().compareToIgnoreCase("PRODUCTION") == 0) {

	Vector w = DataUtils.getNonProductionOntologies(display_name_vec, info.getCodingScheme());
	if (w.size() > 0) {
		info.setHasMultipleVersions(true);

	}
     }
  }


  for (int k = 0; k < display_name_vec.size(); k++) { 
     OntologyInfo info = (OntologyInfo) display_name_vec.elementAt(k);

     if (ontologiesToSearchOn.indexOf(info.getLabel()) != -1) {
	 info.setSelected(true);
     }		     
  }

  for (int k = 0; k < display_name_vec.size(); k++) { 
     OntologyInfo info = (OntologyInfo) display_name_vec.elementAt(k);
     if (!info.isProduction()) {
	  info.setSelected(false);
     }		     
  }

  Collections.sort(display_name_vec, new OntologyInfo.ComparatorImpl());
}

request.getSession().setAttribute("display_name_vec", display_name_vec);
display_name_vec = DataUtils.sortOntologyInfo(display_name_vec);





String warning_msg = (String) request.getSession().getAttribute("warning");
    
%>
<f:view>
  <!-- Begin Skip Top Navigation -->
    <a href="#evs-content" class="hideLink" accesskey="1" title="Skip repetitive navigation links">skip navigation links</A>
  <!-- End Skip Top Navigation --> 
  <%@ include file="/pages/templates/header.jsp" %>
  
  
  <div class="center-page_960">
  
<h:form id="mappingSearch" acceptcharset="UTF-8">
    
    <%@ include file="/pages/templates/sub-header.jsp" %>
    <!-- Main box -->
    <div id="main-area_960">
        <input type="hidden" name="initial_search" value="true" />


 
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
        
         <div class="searchbox">
         
         
         
<%@ page import="gov.nih.nci.evs.browser.utils.HTTPUtils" %>
<%

  request.getSession().removeAttribute("dictionary");
  request.getSession().removeAttribute("version");


  String match_text = gov.nih.nci.evs.browser.utils.HTTPUtils
    .cleanXSS((String) request.getSession().getAttribute("matchText"));

  if (match_text == null) match_text = "";

  String termbrowser_displayed_match_text = HTTPUtils.convertJSPString(match_text);
  String searchform_requestContextPath = request.getContextPath();
  searchform_requestContextPath = searchform_requestContextPath.replace("//ncitbrowser//ncitbrowser", "//ncitbrowser");


%>
<div class="search-form">
  <input CLASS="searchbox-input"
    name="matchText"
    value="<%=termbrowser_displayed_match_text%>"
    onFocus="active = true"
    onBlur="active = false"
    onkeypress="return submitEnter('mappingSearch:mapping_search',event)"
    tabindex="1"/>
  <h:commandButton
    id="mapping_search"
    value="mapping_search"
    action="#{userSessionBean.searchAction}"
    image="#{searchform_requestContextPath}/images/search.gif"
    alt="Search selected mapping"
    styleClass="searchbox-btn"
    tabindex="2">
  </h:commandButton>
  <h:outputLink
    value="#{facesContext.externalContext.requestContextPath}/pages/help.jsf#searchhelp"
    tabindex="3">
    <h:graphicImage value="/images/search-help.gif" styleClass="searchbox-btn" alt="Search Help"
    style="border-width:0;"/>
  </h:outputLink>
  
  
  <%
//String algorithm = (String) request.getSession().getAttribute("algorithm");
String algorithm = gov.nih.nci.evs.browser.utils.HTTPUtils.cleanXSS((String) request.getSession().getAttribute("algorithm"));

    String check_e = "", check_s = "" , check_c ="";
    /*
    if (algorithm == null || algorithm.compareTo("exactMatch") == 0)
      check_e = "checked";
    else if (algorithm.compareTo("startsWith") == 0)
      check_s= "checked";
    else
      check_c = "checked";
    */

    if (algorithm == null || algorithm.compareTo("contains") == 0)
      check_c = "checked";
    else if (algorithm.compareTo("startsWith") == 0)
      check_s= "checked";
    else
      check_e = "checked";
      
    
  %>
  <table border="0" cellspacing="0" cellpadding="0">
    <tr valign="top" align="left">
      <td align="left" class="textbody">
        <input type="radio" id="contains" name="algorithm" value="contains" alt="Contains" <%=check_c%> tabindex="4" onclick="onAlgorithmChanged('mappingSearch');">Contains&nbsp;
        <input type="radio" id="exactMatch" name="algorithm" value="exactMatch" alt="Exact Match" <%=check_e%> tabindex="4">Exact Match&nbsp;
        <input type="radio" id="startsWith" name="algorithm" value="startsWith" alt="Begins With" <%=check_s%> tabindex="4" onclick="onAlgorithmChanged('mappingSearch');">Begins With&nbsp;
        <%
          String searchTarget = (String) request.getSession().getAttribute("searchTarget");
          String check_n = "", check_cd = "", check_p = "" , check_r ="";
          if (searchTarget == null || searchTarget.compareTo("names") == 0)
            check_n = "checked";
          else if (searchTarget.compareTo("codes") == 0)
            check_cd= "checked";            
          else if (searchTarget.compareTo("properties") == 0)
            check_p= "checked";
          else
            check_r = "checked";
        %>
      </td>
    </tr>
    <tr align="left">
      <td height="1px" bgcolor="#2F2F5F"></td>
    </tr>
    <tr valign="top" align="left">
      <td align="left" class="textbody">
        <input type="radio" id="names" name="searchTarget" value="names" alt="Names" <%=check_n%> tabindex="5">Name&nbsp;
        <input type="radio" id="codes" name="searchTarget" value="codes" alt="Codes" <%=check_cd%> tabindex="5" onclick="onCodeButtonPressed('mappingSearch');">Code&nbsp;
        <input type="radio" id="properties" name="searchTarget" value="properties" alt="Properties" <%=check_p%> tabindex="5">Property&nbsp;
        <input type="radio" id="relationships" name="searchTarget" value="relationships" alt="Relationships" <%=check_r%> tabindex="5">Relationship&nbsp;
      </td>
    </tr>
  </table>
</div>
         
         
             
             
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
<!-- end Quick links bar -->


        
        
        
        
        <!-- Page content -->
        <div class="pagecontent">
          <a name="evs-content" id="evs-content"></a>
          
          <%-- 1 <%@ include file="/pages/templates/navigationTabs.jsp"%> --%>
          
          
          <div class="tabTableContentContainer">


<%
String mapping_error_msg = (String) request.getSession().getAttribute("message");
if (mapping_error_msg != null) {
    request.getSession().removeAttribute("message");
    
      %>
      <p class="textbodyred"><%=mapping_error_msg%></p>
      <%
      
} 
%>



            
 
             <%
             if (warning_msg != null) {
             %>
                <p class="textbodyred">&nbsp;<%=warning_msg%></p>
             <%
             }
    

             
             request.getSession().removeAttribute("warning");
             String hide_ontology_list = (String) request.getSession().getAttribute("hide_ontology_list");
             request.getSession().removeAttribute("hide_ontology_list");
if (hide_ontology_list == null || hide_ontology_list.compareTo("false") == 0) {
%>



           
            <span class="textbody">
Select a mapping data set to search here, or click on a mapping data set name to go to its home page and browse
or search from there.  It is not yet possible to search more than one mapping data set at a time.
           <br/><br/>
            </span>
            
            
            <table class="termstable_960" border="0">
  
               
                <tr><td class="textbody">Mappings:</td></tr>
                
                <tr>
                  <td class="textbody">
                    <table border="0" cellpadding="0" cellspacing="0">
                      <%
                      
                      for (int i = 0; i < display_name_vec.size(); i++) {
                     
                        OntologyInfo info = (OntologyInfo) display_name_vec.elementAt(i);
                       
                        String display_name = info.getDisplayName();
                        String label = info.getLabel();
                        String scheme = info.getCodingScheme();
                        String version = info.getVersion();
                        
                        boolean isMapping = DataUtils.isMapping(scheme, version);
                        
                        if (isMapping) {
                                mapping_cs_knt++;
                       
				//String http_label = null;
				String http_scheme = null;
				String http_version = null;
				
				boolean checked = false;
				

				String full_name = DataUtils.getMetadataValue(scheme, version, "full_name");
				if (full_name == null || full_name.compareTo("null") == 0) 
				    full_name = scheme;
				String term_browser_version = DataUtils.getMetadataValue(scheme, version, "term_browser_version");
				if (term_browser_version == null || term_browser_version.compareTo("null") == 0) {
				    term_browser_version = version;
				}     
				String display_label = display_name + ":&nbsp;" + full_name + "&nbsp;(" + term_browser_version + ")";

				//if (label != null)
				// http_label = label.replaceAll(" ", "%20");
				if (scheme != null)
				  http_scheme = scheme.replaceAll(" ", "%20");
				if (version != null)
				  http_version = version.replaceAll(" ", "%20");
				%>
				<tr>
				  <td width="25px"></td>
				  
				  <td>
<%
String scheme_and_version_str = scheme + "$" + version;
String checkedStr = "";
if (mapping_scheme_and_version.compareTo("") == 0 && mapping_cs_knt == 1) {
     checkedStr = "checked";
} else if (mapping_scheme_and_version.compareTo(scheme_and_version_str) == 0) {
     checkedStr = "checked";
}
%>

    <input type="radio" name="scheme_and_version" value="<%=scheme%>$<%=version%>" <%=checkedStr%> tabinex="1" />




				    <a href="<%= request.getContextPath() %>/pages/vocabulary.jsf?dictionary=<%=http_scheme%>&version=<%=http_version%>">
				      <%=display_label%>
				    </a>
				  
				  </td>

                               </tr>
			     <%
			      }
			   }
			 %>
                    </table>
                  </td>
                </tr> 
           
<%           
    if (mapping_cs_knt == 0) {
%>  
        <tr><td class="textbody">&nbsp;&nbsp;&nbsp;&nbsp;No mapping data is available.</td></tr>
<%  
    }
%>    
          
           
            </table>

<p></p>

<%           
    if (mapping_cs_knt > 0) {
%>  

          <table class="termstable_960" border="0">
                <tr>
                  <td>

                  <h:commandButton id="Search" value="Search"
                    action="#{userSessionBean.searchAction}"
                    image="#{requestContextPath}/images/search.gif"
                    alt="Search selected mapping">
                  </h:commandButton>
                  
                  </td>
                </tr>
          </table>

<%  
    }
%>             
<%
}
%>

          </div> <!-- end tabTableContentContainer -->
          <%@ include file="/pages/templates/nciFooter.jsp"%>
        </div> <!-- end Page content -->
    </div> <!-- end main-area_960 -->
    <div class="mainbox-bottom"><img src="<%=basePath%>/images/mainbox-bottom.gif" width="941" height="5" alt="Mainbox Bottom" /></div>
    <input type="hidden" id="single_mapping_search" name="single_mapping_search" value="true">



</h:form>

  </div> <!-- end center-page_960 -->
  <br>
</f:view>
<%
    request.getSession().removeAttribute("dictionary");
    request.getSession().putValue("visited","true");
%>
<br/>
</body>
</html>
