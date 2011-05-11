<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*"%>
<%@ page import="org.LexGrid.concepts.Entity" %>
<%@ page import="gov.nih.nci.evs.browser.bean.*" %>
<%@ page import="gov.nih.nci.evs.browser.utils.*" %>
<%@ page import="gov.nih.nci.evs.browser.properties.*" %>
<%@ page import="gov.nih.nci.evs.browser.utils.*" %>
<%@ page import="javax.faces.context.FacesContext" %>
<%@ page import="org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference" %>
<%@ page import="org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator" %>
<%@ page import="org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList" %>

<%@ page import="org.apache.log4j.*" %>





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
<body>
  <script type="text/javascript"
    src="<%=request.getContextPath()%>/js/wz_tooltip.js"></script>
  <script type="text/javascript"
    src="<%=request.getContextPath()%>/js/tip_centerwindow.js"></script>
  <script type="text/javascript"
    src="<%=request.getContextPath()%>/js/tip_followscroll.js"></script>


  <%!
    private static Logger _logger = Utils.getJspLogger("value_set_search_results.jsp");
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
        <%@ include file="/pages/templates/content-header-resolvedvalueset.jsp" %>
        
<%

System.out.println("(*******KLO********) resovled_value_set.jsp ...");

int numRemaining = 0;

String valueSetSearch_requestContextPath = request.getContextPath();
	
String message = (String) request.getSession().getAttribute("message");  
request.getSession().removeAttribute("message");  
String vsd_uri = (String) request.getSession().getAttribute("vsd_uri");
String metadata = DataUtils.getValueSetDefinitionMetadata(DataUtils.findValueSetDefinitionByURI(vsd_uri));
Vector u = DataUtils.parseData(metadata);
String name = (String) u.elementAt(0);
String valueset_uri = (String) u.elementAt(1);
String description = (String) u.elementAt(2);
String concept_domain = (String) u.elementAt(3);
String sources = (String) u.elementAt(4);



IteratorBeanManager iteratorBeanManager = (IteratorBeanManager) FacesContext.getCurrentInstance().getExternalContext()
.getSessionMap().get("iteratorBeanManager");

if (iteratorBeanManager == null) {
    iteratorBeanManager = new IteratorBeanManager();
    request.getSession().setAttribute("iteratorBeanManager", iteratorBeanManager);
}

String resolved_vs_key = (String) request.getSession().getAttribute("resolved_vs_key");
IteratorBean iteratorBean = iteratorBeanManager.getIteratorBean(resolved_vs_key);
if (iteratorBean == null) { 
    System.out.println("(*) iteratorBean with key " + resolved_vs_key + " NOT found.");

    ResolvedConceptReferencesIterator itr = (ResolvedConceptReferencesIterator) request.getSession().getAttribute("ResolvedConceptReferencesIterator");
    iteratorBean = new IteratorBean(itr);
    iteratorBean.initialize();
    iteratorBean.setKey(resolved_vs_key);
    request.getSession().setAttribute("resolved_vs_key", resolved_vs_key);
    iteratorBeanManager.addIteratorBean(iteratorBean);
    
    int itr_size = iteratorBean.getSize();
    System.out.println("itr_size: " + itr_size);
    Integer obj = new Integer(itr_size);
    String itr_size_str = obj.toString();
    System.out.println("itr_size_str: " + itr_size_str);
    request.getSession().setAttribute("itr_size_str", itr_size_str);
    
} else {
    System.out.println("(*) iteratorBean with key " + resolved_vs_key + " found.");
    int itr_size = iteratorBean.getSize();
    System.out.println("itr_size: " + itr_size);
    Integer obj = new Integer(itr_size);
    String itr_size_str = obj.toString();
    System.out.println("itr_size_str: " + itr_size_str);
    //request.getSession().setAttribute("itr_size_str", itr_size_str);    
}



String resultsPerPage = (String) request.getParameter("resultsPerPage");
if (resultsPerPage == null) {
    resultsPerPage = (String) request.getSession().getAttribute("resultsPerPage");
    if (resultsPerPage == null) {
        resultsPerPage = "50";
    }
    
}  else {
    request.getSession().setAttribute("resultsPerPage", resultsPerPage);
}


String selectedResultsPerPage = resultsPerPage;

String page_number = HTTPUtils.cleanXSS((String) request.getParameter("page_number"));
int pageNum = 0;

if (page_number != null) {
    pageNum = Integer.parseInt(page_number);
} else {
    pageNum = 1;
}

int page_num = pageNum;

int pageSize = Integer.parseInt(resultsPerPage);
iteratorBean.setPageSize(pageSize);
int size = iteratorBean.getSize();
numRemaining = size;

System.out.println("\npage_num: " + page_num);
System.out.println("size: " + size);
System.out.println("pageSize: " + pageSize);

int num_pages = size / pageSize;
if (num_pages * pageSize < size) num_pages++;

System.out.println("num_pages: " + num_pages + "\n");

int istart = (page_num - 1) * pageSize;
if (istart < 0) istart = 0;


int iend = istart + pageSize - 1;
if (iend > size) iend = size-1;

/*



String page_string = HTTPUtils.cleanXSS((String) request.getParameter("page_number"));
if (page_string == null) page_string = "0";
int page_num = Integer.parseInt(page_string);

System.out.println("(*) initial page_num: " + page_num);

if (page_num <= 0) page_num = 1;

int next_page_num = page_num + 1;
int prev_page_num = page_num - 1;
int page_size = 50;

if (selectedResultsPerPage != null && selectedResultsPerPage.compareTo("") != 0)
{
    page_size = Integer.parseInt(selectedResultsPerPage);
}

System.out.println("(*) page_num: " + page_num);

int iend = page_num * page_size;
int istart = iend - page_size;
iend = iend-1;


System.out.println("(*) istart: " + istart);
System.out.println("(*) iend: " + iend);


//int size = 0;
String match_size = "0";

if (iteratorBean != null) {
size = iteratorBean.getSize();
System.out.println("(*) size: " + size);
match_size = new Integer(size).toString();
}


if (iend > size-1) iend = size-1;
int num_pages = size / page_size;
if (num_pages * page_size < size) num_pages++;
String istart_str = Integer.toString(istart+1);
String iend_str = Integer.toString(iend+1);

System.out.println("(*) istart_str: " + istart_str);
System.out.println("(*) iend_str: " + iend_str);


String prev_page_num_str = Integer.toString(prev_page_num);
String next_page_num_str = Integer.toString(next_page_num);

*/


%>
        <div class="pagecontent">
          <a name="evs-content" id="evs-content"></a>

          <%-- 0 <%@ include file="/pages/templates/navigationTabs.jsp"%> --%>
          
          <div class="tabTableContentContainer">
          
          
 
 <h:form id="valueSetSearchResultsForm" styleClass="search-form"> 
          
          <table border="0" width="100%">
            <tr>
                <td>
                     <table border="0">
                         <tr>
                             <td align="left" class="texttitle-blue">Value Set:&nbsp;<%=vsd_uri%></td>
                             <td align="center">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
  			     <td align="right">
				     <h:commandLink value="Export XML" action="#{valueSetBean.exportToXMLAction}" styleClass="texttitle-blue-small" title="Export VSD in LexGrid XML format"/> |
				     <h:commandLink value="Export CSV" action="#{valueSetBean.exportToCSVAction}" styleClass="texttitle-blue-small" title="Export VSD in CSV format"/>				
			     </td>  
		         </tr>	
		     </table>
	        </td>	      
            </tr>
            
            <% if (message != null) { 
                request.getSession().removeAttribute("message"); 
             %>
             
        <tr class="textbodyred"><td>
      <p class="textbodyred">&nbsp;<%=message%></p>
        </td></tr>
            <% } 
            %>
            
            <tr class="textbody"><td><b>Name</b>: <%=name%></td>
            <tr class="textbody"><td><b>Description</b>: <%=description%></td>
            <tr class="textbody"><td><b>Concept Domain</b>: <%=concept_domain%></td>
            <tr class="textbody"><td><b>Sources</b>: <%=sources%></td>
            
            <tr class="textbody"><td>&nbsp;</td>
            <tr class="textbody"><td><b>Concepts</b>:</td>

            <tr class="textbody"><td>
            
               
              <table class="dataTable" summary="" cellpadding="3" cellspacing="0" border="0" width="100%">
             
                <th class="dataTableHeader" scope="col" align="left">Code</th>
                <th class="dataTableHeader" scope="col" align="left">Name</th>
                <th class="dataTableHeader" scope="col" align="left">Vocabulary</th>
                <th class="dataTableHeader" scope="col" align="left">Namespace</th>

<%
                Vector concept_vec = new Vector();


          
		//ResolvedConceptReferenceList list = new ResolvedConceptReferenceList();
		List list = iteratorBean.getData(istart, iend);
		

                for (int k=0; k<list.size(); k++) {
                      Object obj = list.get(k);
                      ResolvedConceptReference ref = null;
		      if (obj == null) {
			   _logger.warn("rcr == null???");
		      } else {
		         ref = (ResolvedConceptReference) obj;
		      }

		      String entityDescription = "<NOT ASSIGNED>";
		      if (ref.getEntityDescription() != null) {
			 entityDescription = ref.getEntityDescription().getContent();
		      }

		      concept_vec.add(ref.getConceptCode()
			+ "|" + entityDescription
			+ "|" + ref.getCodingSchemeName()
			+ "|" + ref.getCodeNamespace() 
			+ "|" + ref.getCodingSchemeVersion());
				
                }



            for (int i=0; i<concept_vec.size(); i++) {
            
		    String concept_str = (String) concept_vec.elementAt(i);
		    u = DataUtils.parseData(concept_str);
		    String code = (String) u.elementAt(0);
		    String conceptname = (String) u.elementAt(1);
		    String coding_scheme = (String) u.elementAt(2);
		    String namespace = (String) u.elementAt(3);
		    String vsn = (String) u.elementAt(4);

		    if (i % 2 == 0) {
		    %>
		      <tr class="dataRowDark">
		    <%
			} else {
		    %>
		      <tr class="dataRowLight">
		    <%
			}
		    %>    
		      <td class="dataCellText">
		    <%
		      if (code.indexOf("@") == -1) {
		     %> 
			  <a href="<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=coding_scheme%>&version=<%=vsn%>&code=<%=code%>">
			    <%=code%>
			  </a>		      
		      <%
		      } else {
		      %>
		         <%=code%>
		      <%
		      }
		      %>
		      </td>
		      
		      <td class="dataCellText">
			 <%=conceptname%>
		      </td>
		      <td class="dataCellText">
			 <%=coding_scheme%>
		      </td>
		      <td class="dataCellText">
			 <%=namespace%>
		      </td>  

		      </tr>
              
              
             <%
                }
             %>                 
                  
              </table>
           
              <input type="hidden" name="vsd_uri" id="vsd_uri" value="<%=vsd_uri%>">
              <input type="hidden" name="referer" id="referer" value="<%=HTTPUtils.getRefererParmEncode(request)%>">
</h:form>
            
          </td></tr>
        </table>
        </div> <!-- end tabTableContentContainer -->
        <%@ include file="/pages/templates/pagination-resolved-valueset.jsp" %>
        <%@ include file="/pages/templates/nciFooter.jsp" %>
      </div>
      <!-- end Page content -->
    </div>
    <div class="mainbox-bottom"><img src="<%=basePath%>/images/mainbox-bottom.gif" width="745" height="5" alt="Mainbox Bottom" /></div>
    <!-- end Main box -->
  </div>
</f:view>
</body>
</html>
