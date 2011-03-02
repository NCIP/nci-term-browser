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
  String ncit_build_info = new DataUtils().getNCITBuildInfo();
  String application_version = new DataUtils().getApplicationVersion();
  String anthill_build_tag_built = new DataUtils().getNCITAnthillBuildTagBuilt();
  String evs_service_url = new DataUtils().getEVSServiceURL();
  
  String requestContextPath = request.getContextPath();
  requestContextPath = requestContextPath.replace("//ncitbrowser//ncitbrowser", "//ncitbrowser");
  boolean display_cabig_approval_indicator_note = false;
  Integer curr_sort_category = null;
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
  <title>NCI Term Browser</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
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
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/wz_tooltip.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/tip_centerwindow.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/tip_followscroll.js"></script>
  <script language="JavaScript">
     function checkVisited() {
       var test = '<%= request.getSession().getAttribute("visited") %>';
       if (test == "" || test == "null")
         checkAllButOne(document.searchTerm.ontology_list, 'Metathesaurus');
     }
  </script>
<%
    request.getSession().removeAttribute("dictionary");
    
    String navigation_type = (String) request.getParameter("nav_type");
    if (navigation_type == null) {
        navigation_type = "terminologies";
    }
    request.getSession().setAttribute("nav_type", navigation_type);

 

HashMap display_name_hmap = null;
Vector display_name_vec = null;
display_name_hmap = (HashMap) request.getSession().getAttribute("display_name_hmap");
display_name_vec = (Vector) request.getSession().getAttribute("display_name_vec");
String warning_msg = (String) request.getSession().getAttribute("warning");
String ontologiesToSearchOn = (String) request.getSession().getAttribute("defaultOntologiesToSearchOnStr");
if (ontologiesToSearchOn == null) {
  ontologiesToSearchOn = DataUtils.getDefaultOntologiesToSearchOnStr();
}
if (warning_msg != null && warning_msg.compareTo(Constants.ERROR_NO_VOCABULARY_SELECTED) == 0) {
   ontologiesToSearchOn = "|";
}
String unsupported_vocabulary_message = (String) request.getSession().getAttribute("unsupported_vocabulary_message");

    
%>
<f:view>
  <!-- Begin Skip Top Navigation -->
    <a href="#evs-content" class="hideLink" accesskey="1" title="Skip repetitive navigation links">skip navigation links</A>
  <!-- End Skip Top Navigation --> 
  <%@ include file="/pages/templates/header.jsp" %>
  <div class="center-page">
  
    <h:form id="searchTerm" styleClass="search-form" >
    
    <%@ include file="/pages/templates/sub-header.jsp" %>
    <!-- Main box -->
    <div id="main-area">
        <input type="hidden" name="initial_search" value="true" />

        <%@ include file="/pages/templates/content-header-termbrowser.jsp" %>
        
        <!-- Page content -->
        <div class="pagecontent">
          <a name="evs-content" id="evs-content"></a>
          
          <%@ include file="/pages/templates/navigationTabs.jsp"%>
          
          
          <div class="tabTableContentContainer">



          <table class="termstable" border="0">
                <tr>
                  <td><img
                    src="<%= request.getContextPath() %>/images/selectAll.gif"
                    name="selectAll" alt="selectAll"
                    onClick="checkAll(document.searchTerm.ontology_list)" />

<%
if (navigation_type == null || navigation_type.compareTo("terminologies") == 0) {
%>

                  &nbsp;&nbsp; <img
                    src="<%= request.getContextPath() %>/images/AllbutNCIm.gif"
                    name="reset" alt="selectAllButNCIm"
                    onClick="checkAllButOne(document.searchTerm.ontology_list, 'Metathesaurus')" />

<%
 }
 %>
                  &nbsp;&nbsp; <img
                    src="<%= request.getContextPath() %>/images/clear.gif"
                    name="reset" alt="reset"
                    onClick="uncheckAll(document.searchTerm.ontology_list)" />

                  &nbsp;&nbsp; <h:commandButton id="multiple_search" value="Search"
                    action="#{userSessionBean.multipleSearchAction}"
                    image="#{requestContextPath}/images/search.gif"
                    alt="Search">
                  </h:commandButton></td>
                </tr>
          </table>

            
 
             <%
             if (warning_msg != null) {
             %>
                <p class="textbodyred">&nbsp;<%=warning_msg%></p>
             <%
             }
    
	     if (unsupported_vocabulary_message != null && unsupported_vocabulary_message.compareTo("null") != 0) {
	        request.getSession().removeAttribute("unsupported_vocabulary_message"); 
             %>
                <p class="textbodyred">&nbsp;<%=unsupported_vocabulary_message%></p>
             <%
	     }             
             
             request.getSession().removeAttribute("warning");
             String hide_ontology_list = (String) request.getSession().getAttribute("hide_ontology_list");
             request.getSession().removeAttribute("hide_ontology_list");
             if (hide_ontology_list == null || hide_ontology_list.compareTo("false") == 0) {
             %>
             
             
<%
if (navigation_type == null || navigation_type.compareTo("terminologies") == 0) {
%>           
            <span class="textbody">&nbsp;Select NCI hosted terminologies to search, or click on a source name to go to its browser home page.
            <br/>
            &nbsp;(WARNING: <b>Select All</b> searches with thousands of hits may be slow; try NCI Metathesaurus separately.)
            <br/><br/>
            </span>
<%
} 
%>



            
            
            <table class="termstable" border="0">

<%
if (navigation_type == null || navigation_type.compareTo("terminologies") == 0) {
%>

              <tr>
              <%
                List ontology_list = DataUtils.getOntologyList();
                if (ontology_list == null) 
                    _logger.warn("??????????? ontology_list == null");
                int num_vocabularies = ontology_list.size();


                if (display_name_hmap == null || display_name_vec == null) {
                        display_name_hmap = new HashMap();
                        display_name_vec = new Vector();

                  for (int i = 0; i < ontology_list.size(); i++) {
                    SelectItem item = (SelectItem) ontology_list.get(i);
                    String value = (String) item.getValue();
                    String label = (String) item.getLabel();
                    
                    String scheme = DataUtils.key2CodingSchemeName(value);
                    String version = DataUtils.key2CodingSchemeVersion(value);
                    
                    String display_name = DataUtils.getMetadataValue(scheme, version, "display_name");
                    
                    if (display_name == null || display_name.compareTo("null") == 0)
                        display_name = DataUtils.getLocalName(scheme);
                    String sort_category = DataUtils.getMetadataValue(
                        scheme, version, "vocabulary_sort_category");
                    
                    display_name_hmap.put(display_name+"$"+version, value);
                    display_name_vec.add(new OntologyInfo(display_name+"$"+version, scheme, sort_category));
                  }
                  
                  Collections.sort(display_name_vec, new OntologyInfo.ComparatorImpl());
                  request.getSession().setAttribute("display_name_hmap", display_name_hmap);
                  request.getSession().setAttribute("display_name_vec", display_name_vec);
                }
                %>
                  <td class="textbody">
                    <table border="0" cellpadding="0" cellspacing="0">
                      <%
                     
                      for (int i = 0; i < display_name_vec.size(); i++) {
                        OntologyInfo info = (OntologyInfo) display_name_vec.elementAt(i);
                        int sort_category = info.getSortCategory();
                        String display_name_version = info.getDisplayNameVersion();
                        
                        int n = display_name_version.indexOf("$");
                        String display_name = display_name_version.substring(0, n);
                        String value = (String)  display_name_hmap.get(display_name_version);
                        String label = (String)  display_name_hmap.get(display_name_version);
                       
                        String label2 = "|" + label + "|";
                        String scheme = DataUtils.key2CodingSchemeName(value);
                        String version = DataUtils.key2CodingSchemeVersion(value);
 
                        boolean isMapping = DataUtils.isMapping(scheme, version);
                        if (!isMapping) {
 
				String http_label = null;
				String http_scheme = null;
				String http_version = null;

        String status = DataUtils.getMetadataValue(
            scheme, version, "cabig_approval_status");
        boolean display_status = status != null && 
          status.trim().length() > 0;
        String cabig_approval_indicator = getCabigIndicator(display_status, basePath);
        display_cabig_approval_indicator_note |= display_status;
        
				if (label != null)
				  http_label = label.replaceAll(" ", "%20");
				if (scheme != null)
				  http_scheme = scheme.replaceAll(" ", "%20");
				if (version != null)
				  http_version = version.replaceAll(" ", "%20");
				%>

        <% if (curr_sort_category != null && sort_category != curr_sort_category.intValue()) { %>
          <tr>
            <td width="25px"></td>
            <td><img src="<%=basePath%>/images/shim.gif" width="1" height="7" alt="Shim" /></td>
          </tr>
        <% } curr_sort_category = new Integer(sort_category); %>
        
				<tr>
				  <td width="25px"></td>
				  
				  
				  <td>
				<%
				boolean checked = ontologiesToSearchOn != null
				    && ontologiesToSearchOn.indexOf(label2) != -1;
				String checkedStr = checked ? "checked" : "";
				%>
				   
				   <input type="checkbox" name="ontology_list" value="<%=label%>" <%=checkedStr%> />
				   
				   
				<%

				String full_name = DataUtils.getMetadataValue(scheme, version, "full_name");
				if (full_name == null || full_name.compareTo("null") == 0) 
				    full_name = scheme;
				String term_browser_version = DataUtils.getMetadataValue(scheme, version, "term_browser_version");
				if (term_browser_version == null || term_browser_version.compareTo("null") == 0) {
				    term_browser_version = version;
				}     
				String display_label = display_name + ":&nbsp;" + full_name + "&nbsp;(" + term_browser_version + ")";

				if (scheme.compareTo("NCI Thesaurus") == 0) {
				    String nciturl = NCItBrowserProperties.getNCIT_URL();
				    nciturl = nciturl + "?version=" + version;
				  %>
				    <a href="<%=nciturl%>"><%=display_label%></a><%=cabig_approval_indicator%>
				  <%
				} else if (scheme.compareToIgnoreCase("NCI Metathesaurus") == 0) {
				    String ncimurl = NCItBrowserProperties.getNCIM_URL();
				  %>
				    <a href="<%=ncimurl%>" target="_blank"><%=display_label%>
				      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=display_label%>" />
				    </a><%=cabig_approval_indicator%>
				  <%
				} else {
				  %>
				    <a href="<%= request.getContextPath() %>/pages/vocabulary.jsf?dictionary=<%=http_scheme%>&version=<%=http_version%>">
				      <%=display_label%>
				    </a><%=cabig_approval_indicator%>
				  <%
				}
                        }
                      %>
                        </td>
                      </tr>
                     <%
                      }
                     %>
                     <% if (display_cabig_approval_indicator_note) { %>
                       <tr>
                         <td width="25px"></td>
                         <td><img src="<%=basePath%>/images/shim.gif" width="1" height="7" alt="Shim" /></td>
                       </tr>                     
                       <tr>
                         <td width="25px"></td>
                         <td class="termstable">
                           <img src="<%=basePath%>/images/shim.gif" width="20" height="1" alt="Shim" />
                           <b class="textbody">*</b> <%=CABIG_APPROVED_MSG%>.
                         </td>
                       </tr>
                     <% } %>
                    </table>
                  </td>
                </tr>




                <tr><td height="20"></td></tr>
                
                <tr>
                  <td><img
                    src="<%= request.getContextPath() %>/images/selectAll.gif"
                    name="selectAll" alt="selectAll"
                    onClick="checkAll(document.searchTerm.ontology_list)" />

                  &nbsp;&nbsp; <img
                    src="<%= request.getContextPath() %>/images/AllbutNCIm.gif"
                    name="reset" alt="selectAllButNCIm"
                    onClick="checkAllButOne(document.searchTerm.ontology_list, 'Metathesaurus')" />

                  &nbsp;&nbsp; <img
                    src="<%= request.getContextPath() %>/images/clear.gif"
                    name="reset" alt="reset"
                    onClick="uncheckAll(document.searchTerm.ontology_list)" />

                  &nbsp;&nbsp; <h:commandButton id="multiplesearch" value="Search"
                    action="#{userSessionBean.multipleSearchAction}"
                    image="#{requestContextPath}/images/search.gif"
                    alt="Search">
                  </h:commandButton></td>
                   <%
                   if (warning_msg != null) {
                      request.getSession().removeAttribute("ontologiesToSearchOn");
                   }
                  %>
                </tr>
                
 <%
 }
 %>
  
  
<%
if (navigation_type != null && (navigation_type.compareTo("mappings") == 0)) {

System.out.println("mappings tab clicked...");

%> 
                
                <tr><td class="textbody">Mappings:</td></tr>
                
                <tr>
                  <td class="textbody">
                    <table border="0" cellpadding="0" cellspacing="0">
                      <%
                     
                      for (int i = 0; i < display_name_vec.size(); i++) {
                        OntologyInfo info = (OntologyInfo) display_name_vec.elementAt(i);
                        String display_name_version = info.getDisplayNameVersion();
                        
                        int n = display_name_version.indexOf("$");
                        String display_name = display_name_version.substring(0, n);
                        String value = (String)  display_name_hmap.get(display_name_version);
                        String label = (String)  display_name_hmap.get(display_name_version);
                       
                        String label2 = "|" + label + "|";
                        String scheme = DataUtils.key2CodingSchemeName(value);
                        String version = DataUtils.key2CodingSchemeVersion(value);
                        
                        boolean isMapping = DataUtils.isMapping(scheme, version);
                        if (isMapping) {
                       
				String http_label = null;
				String http_scheme = null;
				String http_version = null;
				
				boolean checked = ontologiesToSearchOn != null
				    && ontologiesToSearchOn.indexOf(label2) != -1;
				String checkedStr = checked ? "checked" : "";

				String full_name = DataUtils.getMetadataValue(scheme, version, "full_name");
				if (full_name == null || full_name.compareTo("null") == 0) 
				    full_name = scheme;
				String term_browser_version = DataUtils.getMetadataValue(scheme, version, "term_browser_version");
				if (term_browser_version == null || term_browser_version.compareTo("null") == 0) {
				    term_browser_version = version;
				}     
				String display_label = display_name + ":&nbsp;" + full_name + "&nbsp;(" + term_browser_version + ")";
		

				if (label != null)
				  http_label = label.replaceAll(" ", "%20");
				if (scheme != null)
				  http_scheme = scheme.replaceAll(" ", "%20");
				if (version != null)
				  http_version = version.replaceAll(" ", "%20");
				%>
				<tr>
				  <td width="25px"></td>
				  
				  <td>

				     <input type="checkbox" name="ontology_list" value="<%=label%>" <%=checkedStr%> tabinex="1" />

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
                
                
                
                 <tr><td height="20"></td></tr>
                
                <tr>
                  <td><img
                    src="<%= request.getContextPath() %>/images/selectAll.gif"
                    name="selectAll" alt="selectAll"
                    onClick="checkAll(document.searchTerm.ontology_list)" />

                  &nbsp;&nbsp; <img
                    src="<%= request.getContextPath() %>/images/clear.gif"
                    name="reset" alt="reset"
                    onClick="uncheckAll(document.searchTerm.ontology_list)" />

                  &nbsp;&nbsp; <h:commandButton id="multiplesearch" value="Search"
                    action="#{userSessionBean.multipleSearchAction}"
                    image="#{requestContextPath}/images/search.gif"
                    alt="Search">
                  </h:commandButton></td>
                   <%
                   if (warning_msg != null) {
                      request.getSession().removeAttribute("ontologiesToSearchOn");
                   }
                  %>
                </tr>               
                
 <%
 }
 %>                
                
            </table>
<%
}
%>
          </div> <!-- end tabTableContentContainer -->
          <%@ include file="/pages/templates/nciFooter.jsp"%>
        </div> <!-- end Page content -->
    </div> <!-- end main-area -->
    <div class="mainbox-bottom"><img src="<%=basePath%>/images/mainbox-bottom.gif" width="745" height="5" alt="Mainbox Bottom" /></div>

    <input type="hidden" id="nav_type" name="nav_type" value="<%=navigation_type%>">



</h:form>

  </div> <!-- end center-page -->
  <br>
</f:view>
<%
    request.getSession().removeAttribute("dictionary");
    request.getSession().removeAttribute("message");
    request.getSession().removeAttribute("warning");
    request.getSession().removeAttribute("ontologiesToSearchOn");
    request.getSession().putValue("visited","true");
    
    request.getSession().removeAttribute("nav_type");
%>
<br/>
</body>
</html>