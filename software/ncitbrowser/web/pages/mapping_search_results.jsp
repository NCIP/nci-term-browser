<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.io.*" %>
<%@ page import="java.util.*"%>
<%@ page import="java.util.Vector"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.List"%>

<%@ page import="org.LexGrid.concepts.Entity" %>
<%@ page import="gov.nih.nci.evs.browser.common.Constants" %>
<%@ page import="gov.nih.nci.evs.browser.utils.DataUtils" %>
<%@ page import="gov.nih.nci.evs.browser.utils.HTTPUtils" %>

<%@ page import="gov.nih.nci.evs.browser.bean.MappingIteratorBean" %>
<%@ page import="gov.nih.nci.evs.browser.bean.MappingData" %>
<%@ page import="org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference" %>
<%@ page import="org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator" %>

<%@ page import="gov.nih.nci.evs.browser.bean.*" %>
<%@ page import="gov.nih.nci.evs.browser.utils.*" %>
<%@ page import="gov.nih.nci.evs.browser.properties.*" %>
<%@ page import="javax.faces.context.FacesContext" %>
<%@ page import="org.apache.log4j.*" %>


<%@ page import="org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.Mapping.SearchContext" %>




<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns:c="http://java.sun.com/jsp/jstl/core">
<head>
  <title>NCI Term Browser - Mapping Search</title>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />
  <link rel="shortcut icon" href="<%= request.getContextPath() %>/favicon.ico" type="image/x-icon" />
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/script.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/search.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/dropdown.js"></script>
</head>
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/wz_tooltip.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/tip_centerwindow.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/tip_followscroll.js"></script>
<%!
  private static Logger _logger = Utils.getJspLogger("mapping-search_results.jsp");
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
<%

String base_Path = request.getContextPath();
HashMap display_name_hmap = new HashMap();


HashMap scheme2MappingIteratorBeanMap = null;
ResolvedConceptReferencesIterator iterator = null;
MappingIteratorBean bean = null;

JSPUtils.JSPHeaderInfo info = new JSPUtils.JSPHeaderInfo(request);
String mapping_scheme = info.dictionary;
String mapping_dictionary = info.dictionary;
String mapping_version = info.version;

_logger.debug("mapping_search_results.jsp dictionary: " + mapping_dictionary);
_logger.debug("mapping_search_results.jsp version: " + mapping_version);

 String mapping_display_name =
                DataUtils
                    .getMetadataValue(mapping_dictionary, mapping_version, "display_name");
                    
 String mapping_term_browser_version =
                DataUtils.getMetadataValue(mapping_dictionary, mapping_version,
                    "term_browser_version");

 JSPUtils.JSPHeaderInfoMore info3 = new JSPUtils.JSPHeaderInfoMore(request); 
 String nciturl = request.getContextPath() + "/pages/home.jsf" + "?version=" + info3.version;

 String release_date = DataUtils.getVersionReleaseDate(mapping_dictionary, mapping_version);
 boolean display_release_date = true;
 if (release_date == null || release_date.compareTo("") == 0) {
     display_release_date = false;
 }
 
          

%>

 
 <div class="bannerarea_960">
 
<% 
   if (info3.dictionary.compareTo("NCI Thesaurus") == 0) {
%>
 	 <a href="<%=nciturl%>" style="text-decoration: none;">
 	      <div class="vocabularynamebanner_ncit">
 
 <%	      
 	 String content_header_other_dictionary = HTTPUtils.cleanXSS(info3.dictionary);
 	 String content_header_other_version = HTTPUtils.cleanXSS(info3.version);
 
 	 release_date = DataUtils.getVersionReleaseDate(content_header_other_dictionary, content_header_other_version);
 	 display_release_date = true;
 	 if (release_date == null || release_date.compareTo("") == 0) {
 	     display_release_date = false;
 	 }
 	 if (display_release_date) {
 %>	 
 	 
 	     <span class="vocabularynamelong_ncit">Version: <%=HTTPUtils.cleanXSS(info3.term_browser_version)%> (Release date: <%=release_date%>)</span>
 <%
 	 } else {
 %>	 
 	     <span class="vocabularynamelong_ncit">Version:&nbsp;<%=HTTPUtils.cleanXSS(info3.term_browser_version)%></span>
 <%
 	 }
 %>	      
 		 
 	     </div>
 	 </a>
 	 
<% 
} else {
%>
 
     <a class="vocabularynamebanner" href="<%=request.getContextPath()%>/pages/vocabulary.jsf?dictionary=<%=HTTPUtils.cleanXSS(mapping_dictionary)%>">
       <div class="vocabularynamebanner">
           <div class="vocabularynameshort" STYLE="font-size: <%=HTTPUtils.maxFontSize(mapping_display_name)%>px; font-family : Arial">
             <%=HTTPUtils.cleanXSS(mapping_display_name)%>
           </div>

<%
	 if (display_release_date) {
	 %>
	     <div class="vocabularynamelong">Version: <%=HTTPUtils.cleanXSS(mapping_term_browser_version)%> (Release date: <%=release_date%>)</div>
	 <%
	 } else {
	 %>
	     <div class="vocabularynamelong">Version:&nbsp;<%=HTTPUtils.cleanXSS(mapping_term_browser_version)%> </div>
	 <%
	 }
%>	 
	 
  
     </div>
     
   </a>  

<%              
}
%> 

         
	 <div class="search-globalnav_960"><!-- Search box -->
		 <div class="searchbox-top"><img
		   src="<%=base_Path%>/images/searchbox-top.gif" width="352" height="2"
		   alt="SearchBox Top" /></div>
	 	<div class="searchbox"><%@ include file="/pages/templates/searchForm.jsp"%></div>
	 
		 <div class="searchbox-bottom"><img
		   src="<%=base_Path%>/images/searchbox-bottom.gif" width="352" height="2"
		   alt="SearchBox Bottom" /></div>
		 <!-- end Search box --> 
		 <!-- Global Navigation -->
		 <% request.setAttribute("globalNavHeight", "13"); %> 
		 <%@ include file="/pages/templates/menuBar-termbrowser.jsp"%> 
		 <!-- end Global Navigation -->
	 </div>
 </div>
 
 
 
 
 <!-- end Thesaurus, banner search area -->
 <!-- Quick links bar -->
 <%@ include file="/pages/templates/quickLink.jsp"%>
<!-- end Quick links bar -->



      
      
      <!-- Page content -->
      <div class="pagecontent">
	    <a name="evs-content" id="evs-content"></a>


                              <table class="textbody" border="0" width="100%">
                                 <tr >
                                    <td align="left">&nbsp;&nbsp;</td>
                                    <td align="center">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
      <td align="right">                               
      <a href="/ncitbrowser/ajax?action=export_mapping_search&dictionary=<%=mapping_dictionary%>&version=<%=mapping_version%>"  title="Export mapping search results to CSV">
      Export CSV
      </a>  
      </td>
                                 </tr>
                              </table>



<%
String mapping_results_msg = (String) request.getSession().getAttribute("message");
boolean bool_val;
if (mapping_results_msg != null) {
    request.getSession().removeAttribute("message");
    
    
      %>
      <p class="textbodyred"><%=mapping_results_msg%></p>
      <%
      
} else {    


	String resultsPerPage = HTTPUtils.cleanXSS((String) request.getParameter("resultsPerPage"));
	if (resultsPerPage == null) {
	    resultsPerPage = (String) request.getSession().getAttribute("resultsPerPage");
	    if (resultsPerPage == null) {
		resultsPerPage = "50";
	    }

	}  else {
	

		    bool_val = JSPUtils.isInteger(resultsPerPage);
		    if (!bool_val) {
			 String redirectURL = request.getContextPath() + "/pages/appscan_response.jsf";
			 String error_msg = HTTPUtils.createErrorMsg("resultsPerPage", resultsPerPage);
			 request.getSession().setAttribute("error_msg", error_msg);
			 response.sendRedirect(redirectURL);
		    } else {
			 request.getSession().setAttribute("resultsPerPage", resultsPerPage);
		    }   	
	    
	}






String selectedResultsPerPage = resultsPerPage;

String base_path = request.getContextPath();
int numRemaining = 0;


String key = (String) request.getAttribute("key");
if (key == null) {
    key = HTTPUtils.cleanXSS((String) request.getParameter("key"));
}

//IteratorBeanManager iteratorBeanManager = (IteratorBeanManager) FacesContext.getCurrentInstance().getExternalContext()
//.getSessionMap().get("iteratorBeanManager");

MappingIteratorBean iteratorBean = (MappingIteratorBean) request.getSession().getAttribute("mapping_search_results");


//====================================================================================================
String matchText = HTTPUtils.cleanXSS((String) request.getSession().getAttribute("matchText"));
int pageNum = 0; 
int pageSize = Integer.parseInt( resultsPerPage );
int size = iteratorBean.getSize();    
List list = null;
int num_pages = size / pageSize;
if (num_pages * pageSize < size) num_pages++;

String page_number_2 = HTTPUtils.cleanXSS((String) request.getParameter("page_number"));

if (page_number_2 != null) {
    pageNum = Integer.parseInt(page_number_2);
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
       //list = iteratorBean.getData(istart, size);
       
   } else {

	if (iend > size) {
	    iend = size;
	}

   }
 
   
   list = iteratorBean.getData(istart, iend);
   
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


if (iend+1 > size) {
    iend = size;
    iend_str = Integer.valueOf(iend).toString();
}


String match_size = Integer.valueOf(size).toString();
    

int next_page_num = page_num + 1;
int prev_page_num = page_num - 1;
String prev_page_num_str = Integer.toString(prev_page_num);
String next_page_num_str = Integer.toString(next_page_num);

//====================================================================================================

boolean show_rank_column = true;
String map_rank_applicable = DataUtils.getMetadataValue(mapping_scheme, mapping_version, "map_rank_applicable");
if (map_rank_applicable != null && map_rank_applicable.compareTo("false") == 0) {
    show_rank_column = false;
}

%>
          <table class="datatable_960">

          <th class="dataTableHeader" width="100px" scope="col" align="left">Source</th>


          <th class="dataTableHeader" width="100px" scope="col" align="left">
                 Source Code
          </th>

          <th class="dataTableHeader" scope="col" align="left">
                 Source Name
          </th>

          <th class="dataTableHeader" width="30px" scope="col" align="left">
                 REL
          </th>

<%
if (show_rank_column) {
%>
          <th class="dataTableHeader" width="35px" scope="col" align="left">
                 Map Rank
          </th>
<%
}
%>

          <th class="dataTableHeader" width="100px" scope="col" align="left">Target</th>

          <th class="dataTableHeader" width="100px" scope="col" align="left">
                 Target Code
          </th>

          <th class="dataTableHeader" scope="col" align="left">
                 Target Name
          </th>


            <%
                String source_scheme = null;//"NCI_Thesaurus";
                String source_version = null;// "10.06e";
                String source_namespace = null;
                String target_scheme = null;// "ICD_9_CM";
                String target_version = null;// "2010";

                String source_code = null;
                String source_name = null;
                String rel = null;
                String score = null;
                String target_code = null;
                String target_name = null;
                String target_namespace = null;
                MappingData mappingData = null;


 if (list == null) {
     //System.out.println("MAPPING RESULT PAGE list == null???");
 } else {

                
 int upper_bound = list.size();
 if (upper_bound > pageSize) {
     upper_bound = pageSize;
     
 }
               
            
                
  for (int lcv=0; lcv<list.size(); lcv++) {
                    mappingData = (MappingData) list.get(lcv);
        source_code = mappingData.getSourceCode();
        source_name = mappingData.getSourceName();
        source_namespace = mappingData.getSourceCodeNamespace();

        if (display_name_hmap.containsKey(source_namespace)) {
            source_namespace = (String) display_name_hmap.get(source_namespace);
        } else {
            String short_name = DataUtils.getMappingDisplayName(mapping_dictionary, source_namespace);
            display_name_hmap.put(source_namespace, short_name);
            source_namespace = short_name;
        }

        rel = mappingData.getRel();
        score = Integer.valueOf(mappingData.getScore()).toString();
        target_code = mappingData.getTargetCode();
        target_name = mappingData.getTargetName();
        target_namespace = mappingData.getTargetCodeNamespace();

        if (display_name_hmap.containsKey(target_namespace)) {
            target_namespace = (String) display_name_hmap.get(target_namespace);
        } else {
            String short_name = DataUtils.getMappingDisplayName(mapping_dictionary, target_namespace);
            display_name_hmap.put(target_namespace, short_name);
            target_namespace = short_name;
        }  
        
        source_scheme = mappingData.getSourceCodingScheme();
        source_version = mappingData.getSourceCodingSchemeVersion();
        target_scheme = mappingData.getTargetCodingScheme();
        target_version = mappingData.getTargetCodingSchemeVersion();
       
                
source_scheme = DataUtils.getCSName(source_scheme);        
target_scheme = DataUtils.getCSName(target_scheme);        

            %>

<tr>
                    <td class="datacoldark" scope="row"><%=source_namespace%></td>
        <td class="datacoldark">
<a href="#"
      onclick="javascript:window.location='<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=source_scheme%>&version=<%=source_version%>&code=<%=source_code%>&b=0&m=0'">
      <%=source_code%>
</a>


        </td>
        <td class="datacoldark"><%=DataUtils.encodeTerm(source_name)%></td>


        <td class="textbody"><%=rel%></td>
        
<%
if (show_rank_column) {
%>        
        <td class="textbody"><%=score%></td>
<%
}
%>


        <td class="datacoldark"><%=target_namespace%></td>
        <td class="datacoldark">

<a href="#"
      onclick="javascript:window.location='<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=target_scheme%>&version=<%=target_version%>&code=<%=target_code%>&b=0&m=0'">
      <%=target_code%>
</a>


                    </td>
        <td class="datacoldark"><%=DataUtils.encodeTerm(target_name)%></td>

</tr>





               <%
               }
%>               
               
          </table>

        <%@ include file="/pages/templates/pagination-mapping-results.jsp" %>
      
                 
<%               
}
               %>
      
        
        <%@ include file="/pages/templates/nciFooter.jsp" %>
      </div>
      <!-- end Page content -->
    </div>
    <div class="mainbox-bottom"><img src="<%=base_Path%>/images/mainbox-bottom.gif" width="945" height="5" alt="Mainbox Bottom" /></div>
    <!-- end Main box -->
  </div>
</f:view>
</body>
</html>

