<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=windows-1252"%>

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
  <title>NCI Thesaurus</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
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
  <div class="center-page">
    <%@ include file="/pages/templates/sub-header.jsp" %>
    <!-- Main box -->
    <div id="main-area">
<%

HashMap scheme2MappingIteratorBeanMap = null;
ResolvedConceptReferencesIterator iterator = null;
String mapping_dictionary = request.getParameter("dictionary");
String mapping_version = request.getParameter("version");
MappingIteratorBean bean = null;


if (mapping_dictionary == null) {
mapping_dictionary = (String) request.getSession().getAttribute("dictionary");
}



String mapping_schema = request.getParameter("schema");

if (mapping_dictionary != null && mapping_schema == null) mapping_schema = mapping_dictionary;
if (mapping_schema != null) {
  request.getSession().setAttribute("dictionary", mapping_schema);
}


_logger.debug("mapping.jsp dictionary: " + mapping_dictionary);
_logger.debug("mapping.jsp version: " + mapping_version);


System.out.println("(*) mapping_search_results.jsp dictionary: " + mapping_dictionary);
System.out.println("(*) mapping_search_results.jsp version: " + mapping_version);



if (mapping_version != null) {
    request.setAttribute("version", mapping_version);
}


%>
      <%@ include file="/pages/templates/content-header-other.jsp" %>
      <!-- Page content -->
      <div class="pagecontent">
	    <a name="evs-content" id="evs-content"></a>

<%
String resultsPerPage = request.getParameter("resultsPerPage");
if (resultsPerPage == null) {
    resultsPerPage = "50";
}

String selectedResultsPerPage = resultsPerPage;


  String base_path = request.getContextPath();
int numRemaining = 0;

int sortBy = MappingData.COL_SOURCE_CODE;
int prevSortBy = MappingData.COL_SOURCE_CODE;

String sortByStr = request.getParameter("sortBy");
if (sortByStr != null) {
    sortBy = Integer.parseInt(sortByStr);
}

String prevSortByStr = (String) request.getSession().getAttribute("sortBy");
if (prevSortByStr != null) {
    prevSortBy = Integer.parseInt(prevSortByStr);
}

if (sortByStr == null) {
    request.getSession().setAttribute("sortBy", "1");
} else {
    request.getSession().setAttribute("sortBy", sortByStr);
}


String key = (String) request.getAttribute("key");
System.out.println("(++++++++++++++++++++) search results.jsp key: " + key);
if (key == null) {
    key = HTTPUtils.cleanXSS((String) request.getParameter("key"));
}


IteratorBeanManager iteratorBeanManager = (IteratorBeanManager) FacesContext.getCurrentInstance().getExternalContext()
.getSessionMap().get("iteratorBeanManager");


IteratorBean iteratorBean = iteratorBeanManager.getIteratorBean(key);

if (iteratorBean == null){
   _logger.warn("iteratorBean NOT FOUND???" + key);
   System.out.println("iteratorBean NOT FOUND???" + key);
} else {
System.out.println("(++++++++++++++++++++) iteratorBean with key found: " + key);
}

iterator = iteratorBean.getIterator();

System.out.println("(*) Calling getRestrictedMappingDataIterator ...mapping_schema "  + mapping_schema);
System.out.println("(*) Calling getRestrictedMappingDataIterator ...mapping_version "  + mapping_version);


//iterator = DataUtils.getRestrictedMappingDataIterator(mapping_schema, mapping_version, null, iterator);

/*
if (searchTarget.compareTo("relationships") == 0) {
	iterator = DataUtils.getRestrictedMappingDataIterator(scheme, version, null, iterator, SearchContext.TARGET_CODES);
} else {
	iterator = DataUtils.getRestrictedMappingDataIterator(scheme, version, null, iterator);
}
*/
iterator = DataUtils.getRestrictedMappingDataIterator(mapping_schema, mapping_version, null, iterator, SearchContext.TARGET_CODES);

if (iterator != null) {

        try {
            numRemaining = iterator.numberRemaining();
            
            System.out.println("numRemaining: " + numRemaining);
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }


  int numberPages = numRemaining / 50 + 1;
  
  
  bean = new MappingIteratorBean(
    iterator,
    numRemaining, // number remaining
    0,    // istart
    50,   // iend,
    numRemaining, // size,
    0,    // pageNumber,
    numberPages);   // numberPages
   
  bean.initialize(
    iterator,
    numRemaining, // number remaining
    0,    // istart
    50,   // iend,
    numRemaining, // size,
    0,    // pageNumber,
    numberPages);   // numberPages
    
    
    request.getSession().setAttribute("mapping_search_results", bean);

} else {
    System.out.println("(*) MappingIteratorBean is null??? ...");
}


String page_number = HTTPUtils.cleanXSS((String) request.getParameter("page_number"));
int pageNum = 0;
if (page_number != null) {
    pageNum = Integer.parseInt(page_number);
}

int page_num = pageNum;
if (page_num == 0) page_num++;


int pageSize = Integer.parseInt(selectedResultsPerPage);

int size = 0;
List list = null;

if (bean != null) {
size = bean.getNumberRemaining();
}

System.out.println("\npage_num: " + page_num);
System.out.println("size: " + size);
System.out.println("pageSize: " + pageSize);

int num_pages = size / pageSize;
if (num_pages * pageSize < size) num_pages++;

System.out.println("num_pages: " + num_pages + "\n");

int istart = pageNum * pageSize;
int iend = istart + pageSize - 1;


System.out.println("calling bean.getData ...");
try {
   list = bean.getData(istart, iend);
} catch (Exception ex) {
   System.out.println("ERROR: bean.getData throws exception??? istart: " + istart + " iend: " + iend);
}
System.out.println("exiting bean.getData ...");

boolean show_rank_column = true;
String map_rank_applicable = DataUtils.getMetadataValue(mapping_schema, mapping_version, "map_rank_applicable");
if (map_rank_applicable != null && map_rank_applicable.compareTo("false") == 0) {
    show_rank_column = false;
}


%>


<table>
<tr><td>
<p class="textbodyred">(Note: This page is under construction.)</p>
</td>
</tr>
</table>



          <table width="580px" cellpadding="3" cellspacing="0" border="0">

          <th class="dataTableHeader" scope="col" align="left">Source</th>


          <th class="dataTableHeader" scope="col" align="left">
                 Source Code
          </th>

          <th class="dataTableHeader" scope="col" align="left">
                 Source Name
          </th>

          <th class="dataTableHeader" scope="col" align="left">
                 REL
          </th>

<%
if (show_rank_column) {
%>
          <th class="dataTableHeader" scope="col" align="left">
                 Map Rank
          </th>
<%
}
%>

          <th class="dataTableHeader" scope="col" align="left">Target</th>

          <th class="dataTableHeader" scope="col" align="left">
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
  System.out.println("list == null???");
 } else {

                for (int lcv=0; lcv<list.size(); lcv++) {
                    mappingData = (MappingData) list.get(lcv);
        source_code = mappingData.getSourceCode();
        source_name = mappingData.getSourceName();
        source_namespace = mappingData.getSourceCodeNamespace();

        // To be modified through metadata
        if (source_namespace.compareTo("NCI_Thesaurus") == 0) source_namespace = "NCIt";

        rel = mappingData.getRel();
        score = new Integer(mappingData.getScore()).toString();
        target_code = mappingData.getTargetCode();
        target_name = mappingData.getTargetName();
        target_namespace = mappingData.getTargetCodeNamespace();

        // To be modified through metadata
        if (target_namespace.compareTo("NCI_Thesaurus") == 0) target_namespace = "NCIt";

        source_scheme = mappingData.getSourceCodingScheme();
        source_version = mappingData.getSourceCodingSchemeVersion();
        target_scheme = mappingData.getTargetCodingScheme();
        target_version = mappingData.getTargetCodingSchemeVersion();

            %>

<tr>
                    <td class="datacoldark"><%=source_namespace%></td>
        <td class="datacoldark">
<a href="#"
      onclick="javascript:window.open('<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=source_scheme%>&version=<%=source_version%>&code=<%=source_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <%=source_code%>
</a>

<a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(source_scheme)%>&version=<%=source_version%>&code=<%=source_code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=source_code%>" />
</a>

        </td>
        <td class="datacoldark"><%=source_name%></td>


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
      onclick="javascript:window.open('<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=target_scheme%>&version=<%=target_version%>&code=<%=target_code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <%=target_code%>
</a>

<a href="#"
      onclick="javascript:window.open('<%=request.getContextPath() %>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(target_scheme)%>&version=<%=target_version%>&code=<%=target_code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=target_code%>" />
</a>
                    </td>
        <td class="datacoldark"><%=target_name%></td>

</tr>

               <%
               }
}
               %>


          </table>

        <%@ include file="/pages/templates/pagination-mapping-results.jsp" %>
        
        
        <%@ include file="/pages/templates/nciFooter.html" %>
      </div>
      <!-- end Page content -->
    </div>
    <div class="mainbox-bottom"><img src="images/mainbox-bottom.gif" width="745" height="5" alt="Mainbox Bottom" /></div>
    <!-- end Main box -->
  </div>
</f:view>
</body>
</html>

