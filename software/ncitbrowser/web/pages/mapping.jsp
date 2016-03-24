<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.io.*" %>
<%@ page import="java.util.*"%>
<%@ page import="java.util.Vector"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.List"%>
<%@ page import="java.net.URLEncoder"%>

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

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns:c="http://java.sun.com/jsp/jstl/core">
<head>
  <title>NCI Term Browser - Mapping</title>
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
  private static Logger _logger = Utils.getJspLogger("mapping.jsp");
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



HashMap display_name_hmap = new HashMap();

ResolvedConceptReferencesIterator iterator = null;
JSPUtils.JSPHeaderInfo info = new JSPUtils.JSPHeaderInfo(request);
String mapping_dictionary = info.dictionary;
String mapping_version = info.version;

String mapping_schema = HTTPUtils.cleanXSS((String) request.getParameter("schema"));
if (mapping_dictionary != null && mapping_schema == null)
  mapping_schema = mapping_dictionary;
if (mapping_schema != null) {
  request.getSession().setAttribute("dictionary", mapping_schema);
}


if (mapping_dictionary != null) {
  request.getSession().setAttribute("dictionary", mapping_dictionary);
}


_logger.debug("mapping.jsp dictionary: " + mapping_dictionary);
_logger.debug("mapping.jsp version: " + mapping_version);


if (mapping_version != null) {
    request.getSession().setAttribute("version", mapping_version);
}

if (DataUtils.isNCIT(mapping_dictionary)) {
%>

      <%@ include file="/pages/templates/content-header.jsp" %>
<%
} else {
%>
      <%@ include file="/pages/templates/content-header-other.jsp" %>
<%
}
%>
      <!-- Page content -->
      <div class="pagecontent">
	    <a name="evs-content" id="evs-content"></a>

<%
String base_path = request.getContextPath();
boolean bool_val;
HashMap scheme2MappingIteratorBeanMap = null;
Object scheme2MappingIteratorBean = request.getSession().getAttribute("scheme2MappingIteratorBeanMap");

if (scheme2MappingIteratorBean != null) {
    scheme2MappingIteratorBeanMap = (HashMap) scheme2MappingIteratorBean;
} else {
    scheme2MappingIteratorBeanMap = new HashMap();
    request.getSession().setAttribute("scheme2MappingIteratorBeanMap", scheme2MappingIteratorBeanMap);
}


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
	    MappingIteratorBean mapping_bean = (MappingIteratorBean) scheme2MappingIteratorBeanMap.get(mapping_schema);
	    if (mapping_bean != null) {
		mapping_bean.setPageSize(Integer.parseInt(resultsPerPage));
		scheme2MappingIteratorBeanMap.put(mapping_schema, mapping_bean);
	    }
    }
}





int numRemaining = 0;

int sortBy = MappingData.COL_SOURCE_CODE;

int prevSortBy = MappingData.COL_SOURCE_CODE;

String sortByStr = HTTPUtils.cleanXSS((String) request.getParameter("sortBy"));


    

if (sortByStr != null  && sortByStr.compareTo("null") != 0 ) {


    bool_val = JSPUtils.isInteger(sortByStr);
    if (!bool_val) {
         String redirectURL = request.getContextPath() + "/pages/appscan_response.jsf";
         String error_msg = HTTPUtils.createErrorMsg("sortByStr", sortByStr);
         request.getSession().setAttribute("error_msg", error_msg);
	 response.sendRedirect(redirectURL);
    } else {
         sortBy = Integer.parseInt(sortByStr);
    }
}


String prevSortByStr = (String) request.getSession().getAttribute("sortBy");
if (prevSortByStr != null && prevSortByStr.compareTo("null") != 0) {

    bool_val = JSPUtils.isInteger(prevSortByStr);
    if (!bool_val) {
         String redirectURL = request.getContextPath() + "/pages/appscan_response.jsf";
         String error_msg = HTTPUtils.createErrorMsg("prevSortByStr", prevSortByStr);
         request.getSession().setAttribute("error_msg", error_msg);
	 response.sendRedirect(redirectURL);
    } else {
         prevSortBy = Integer.parseInt(prevSortByStr);
    }
    
    
}

if (sortByStr == null) {
    request.getSession().setAttribute("sortBy", "1");
} else {
    request.getSession().setAttribute("sortBy", sortByStr);
}


MappingIteratorBean bean = (MappingIteratorBean) scheme2MappingIteratorBeanMap.get(mapping_schema);
if (bean == null) {
    //bean = new MappingIteratorBean();
    // initialization
    iterator = DataUtils.getMappingDataIterator(mapping_schema, mapping_version, sortBy);
    if (iterator != null) {

        try {
            numRemaining = iterator.numberRemaining();
           
            bean = new MappingIteratorBean(iterator);
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
	    
    }
    
    scheme2MappingIteratorBeanMap.put(mapping_schema, bean);
    
} else { //if (prevSortByStr != null && sortBy != prevSortBy) {
    bean = (MappingIteratorBean) scheme2MappingIteratorBeanMap.get(mapping_schema);
    iterator = DataUtils.getMappingDataIterator(mapping_schema, mapping_version, sortBy);
    if (iterator != null) {

        try {
            bean.setIterator(iterator);
            numRemaining = iterator.numberRemaining();
	    //bean.initialize();
	    scheme2MappingIteratorBeanMap.put(mapping_schema, bean);            
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}





if (resultsPerPage != null && resultsPerPage.compareTo("null") != 0) {
    bean.setPageSize(Integer.parseInt(resultsPerPage));
}


int pageSize = bean.getPageSize();

String selectedResultsPerPage = Integer.valueOf(pageSize).toString();


String page_number = HTTPUtils.cleanXSS((String) request.getParameter("page_number"));
int pageNum = 0;
if (page_number != null && page_number.compareTo("null") != 0) {


    bool_val = JSPUtils.isInteger(page_number);
    if (!bool_val) {
         String redirectURL = request.getContextPath() + "/pages/appscan_response.jsf";
         String error_msg = HTTPUtils.createErrorMsg("page_number", page_number);
         request.getSession().setAttribute("error_msg", error_msg);
	 response.sendRedirect(redirectURL);
    } else {
         pageNum = Integer.parseInt(page_number);
    }
}

int page_num = pageNum;

bean.setPageSize(pageSize);
int size = bean.getSize();


int num_pages = size / pageSize;
if (num_pages * pageSize < size) num_pages++;


int istart = (page_num - 1) * pageSize;
if (istart < 0) istart = 0;


int iend = istart + pageSize - 1;
if (iend > size) iend = size-1;



List list = null;
try {
   list = bean.getData(istart, iend);
} catch (Exception ex) {
   //System.out.println("ERROR: bean.getData throws exception??? istart: " + istart + " iend: " + iend);
}

String mapping_selectedPageSize = (String) request.getSession().getAttribute("selectedPageSize");


int prev_page_num = pageNum - 1;
int next_page_num = pageNum + 1;
    
    
String istart_str = Integer.toString(istart+1);
String iend_str = Integer.toString(iend+1);
    
String dictionary_map = (String) request.getSession().getAttribute("dictionary");

//scheme2MappingIteratorBeanMap = (HashMap) request.getSession().getAttribute("scheme2MappingIteratorBeanMap");
//bean = (MappingIteratorBean) scheme2MappingIteratorBeanMap.get(dictionary_map);

numRemaining = bean.getSize();


String match_size = Integer.toString(numRemaining);

String prev_page_num_str = Integer.toString(prev_page_num);
String next_page_num_str = Integer.toString(next_page_num);








boolean show_rank_column = true;
String map_rank_applicable = DataUtils.getMetadataValue(mapping_schema, mapping_version, "map_rank_applicable");
if (map_rank_applicable != null && map_rank_applicable.compareTo("false") == 0) {
    show_rank_column = false;
}
%>

                              <table class="textbody" border="0" width="100%">
                                 <tr >
                                    <td align="left">&nbsp;&nbsp;</td>
                                    <td align="center">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
      <td align="right">                               
      <a href="<%=request.getContextPath() %>/ajax?action=export_mapping&dictionary=<%=HTTPUtils.cleanXSS(mapping_schema)%>&version=<%=mapping_version%>" title="Export mapping to CSV">
      Export CSV
      </a>  
      </td>

                                 </tr>
                              </table>
                              

          <table class="datatable_960" border="0" width="100%">

          <th class="dataTableHeader" width="100px" scope="col" align="left">Source</th>


          <th class="dataTableHeader" width="100px" scope="col" align="left">
              <%
              if (sortBy == MappingData.COL_SOURCE_CODE) {
              %>
                 Source Code
              <%
              } else {
                  String s = Integer.valueOf(MappingData.COL_SOURCE_CODE).toString();
              %>

                <a href="<%=request.getContextPath() %>/pages/mapping.jsf?dictionary=<%=HTTPUtils.cleanXSS(mapping_schema)%>&version=<%=mapping_version%>&sortBy=<%=s%>">
                   Source Code
                </a>

              <%
              }
              %>
          </th>

          <th class="dataTableHeader" scope="col" align="left">
              <%
              if (sortBy == MappingData.COL_SOURCE_NAME) {
              %>
                 Source Name
              <%
              } else {
                  String s = Integer.valueOf(MappingData.COL_SOURCE_NAME).toString();
              %>

                <a href="<%=request.getContextPath() %>/pages/mapping.jsf?nav_type=mappings&dictionary=<%=HTTPUtils.cleanXSS(mapping_schema)%>&version=<%=mapping_version%>&sortBy=<%=s%>">
                   Source Name
                </a>

              <%
              }
              %>
          </th>


          <th class="dataTableHeader" width="30px" scope="col" align="left">
              <%
              if (sortBy == MappingData.COL_REL) {
              %>
                 REL
              <%
              } else {
                  String s = Integer.valueOf(MappingData.COL_REL).toString();
              %>

                <a href="<%=request.getContextPath() %>/pages/mapping.jsf?nav_type=mappings&dictionary=<%=HTTPUtils.cleanXSS(mapping_schema)%>&version=<%=mapping_version%>&sortBy=<%=s%>">
                   REL
                </a>

              <%
              }
              %>
          </th>

<%
if (show_rank_column) {
%>
          <th class="dataTableHeader" width="35px" scope="col" align="left">
              <%
              if (sortBy == MappingData.COL_SCORE) {
              %>
                 Map Rank
                 
        <a href="#" onclick="javascript:window.open('<%=request.getContextPath() %>/pages/rank_help_info.jsf',
    '_blank','top=100, left=100, height=740, width=780, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
    <img src="<%= request.getContextPath() %>/images/help.gif" alt="Rank Definitions" title="Rank Definitions" border="0">
        </a>                 
                 
                 
              <%
              } else {
                  String s = Integer.valueOf(MappingData.COL_SCORE).toString();
              %>

                <a href="<%=request.getContextPath() %>/pages/mapping.jsf?nav_type=mappings&dictionary=<%=HTTPUtils.cleanXSS(mapping_schema)%>&version=<%=mapping_version%>&sortBy=<%=s%>">
                   Map Rank
                </a>


        <a href="#" onclick="javascript:window.open('<%=request.getContextPath() %>/pages/rank_help_info.jsf',
    '_blank','top=100, left=100, height=740, width=780, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
    <img src="<%= request.getContextPath() %>/images/help.gif" alt="Rank Definitions" title="Rank Definitions" border="0">
        </a>
        
              <%
              }
              %>
              
              
          </th>

<%
}
%>


          <th class="dataTableHeader" width="100px" scope="col" align="left">Target</th>

          <th class="dataTableHeader" width="100px" scope="col" align="left">
              <%
              if (sortBy == MappingData.COL_TARGET_CODE) {
              %>
                 Target Code
              <%
              } else {
                  String s = Integer.valueOf(MappingData.COL_TARGET_CODE).toString();
              %>

                <a href="<%=request.getContextPath() %>/pages/mapping.jsf?nav_type=mappings&dictionary=<%=HTTPUtils.cleanXSS(mapping_schema)%>&version=<%=mapping_version%>&sortBy=<%=s%>">
                   Target Code
                </a>

              <%
              }
              %>
          </th>

          <th class="dataTableHeader" scope="col" align="left">
              <%
              if (sortBy == MappingData.COL_TARGET_NAME) {
              %>
                 Target Name
              <%
              } else {
                  String s = Integer.valueOf(MappingData.COL_TARGET_NAME).toString();
              %>

                <a href="<%=request.getContextPath() %>/pages/mapping.jsf?nav_type=mappings&dictionary=<%=HTTPUtils.cleanXSS(mapping_schema)%>&version=<%=mapping_version%>&sortBy=<%=s%>">
                   Target Name
                </a>

              <%
              }
              %>
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
    //System.out.println("list == null???");
 } else {

                for (int lcv=0; lcv<list.size(); lcv++) {
                    mappingData = (MappingData) list.get(lcv);
        source_code = mappingData.getSourceCode();
        source_name = mappingData.getSourceName();
        source_namespace = mappingData.getSourceCodeNamespace();
        
        if (display_name_hmap.containsKey(source_namespace)) {
            source_namespace = (String) display_name_hmap.get(source_namespace);
        } else {
            String mapping_short_name = DataUtils.getMappingDisplayName(mapping_dictionary, source_namespace);
            display_name_hmap.put(source_namespace, mapping_short_name);
            source_namespace = mapping_short_name;
        }


        rel = mappingData.getRel();
        score = Integer.valueOf(mappingData.getScore()).toString();
        target_code = mappingData.getTargetCode();
        target_name = mappingData.getTargetName();
        target_namespace = mappingData.getTargetCodeNamespace();
        
        if (display_name_hmap.containsKey(target_namespace)) {
            target_namespace = (String) display_name_hmap.get(target_namespace);
        } else {
            String mapping_short_name = DataUtils.getMappingDisplayName(mapping_dictionary, target_namespace);
            display_name_hmap.put(target_namespace, mapping_short_name);
            target_namespace = mapping_short_name;
        }       

        source_scheme = DataUtils.getCSName(mappingData.getSourceCodingScheme());
        source_version = mappingData.getSourceCodingSchemeVersion();
        target_scheme = DataUtils.getCSName(mappingData.getTargetCodingScheme());
        target_version = mappingData.getTargetCodingSchemeVersion();

            %>

<tr>
                    <td class="datacoldark" scope="row"><%=source_namespace%></td>
        <td class="datacoldark">
<a href="<%=request.getContextPath()%>/ConceptReport.jsp?dictionary=<%=source_scheme%>&version=<%=source_version%>&ns=<%=source_scheme%>&code=<%=URLEncoder.encode(source_code,"UTF-8")%>">
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

<a href="<%=request.getContextPath()%>/ConceptReport.jsp?dictionary=<%=target_scheme%>&version=<%=target_version%>&ns=<%=target_scheme%>&code=<%=URLEncoder.encode(target_code,"UTF-8")%>">
      <%=target_code%>
</a>

                    </td>
        <td class="datacoldark"><%=DataUtils.encodeTerm(target_name)%></td>

</tr>

               <%
               }
}
               %>


          </table>

        <%@ include file="/pages/templates/pagination-mapping.jsp" %>
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

