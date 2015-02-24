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

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns:c="http://java.sun.com/jsp/jstl/core">
<head>

<%
JSPUtils.JSPHeaderInfo info = new JSPUtils.JSPHeaderInfo(request);
//KLO, 012714
String search_results_dictionary = DataUtils.getCSName(info.dictionary);
%>
  <title><%=search_results_dictionary%></title>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />
  <link rel="shortcut icon" href="<%= request.getContextPath() %>/favicon.ico" type="image/x-icon" />
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/script.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/search.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/dropdown.js"></script>
</head>
<body onLoad="document.forms.searchTerm.matchText.focus();">
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/wz_tooltip.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/tip_centerwindow.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/tip_followscroll.js"></script>
<%!
  private static Logger _logger = Utils.getJspLogger("search_results.jsp");
%>
<f:view>
  <!-- Begin Skip Top Navigation -->
    <a href="#evs-content" class="hideLink" accesskey="1" title="Skip repetitive navigation links">skip navigation links</A>
  <!-- End Skip Top Navigation --> 

<%  
request.getSession().setAttribute("no_back_to_search_results_link", "true");  
%>

  <%@ include file="/pages/templates/header.jsp" %>
  <div class="center-page_960">
    <%@ include file="/pages/templates/sub-header.jsp" %>
    <!-- Main box -->
    <div id="main-area_960">

<%

boolean isMapping = DataUtils.isMapping(search_results_dictionary, null);
boolean isExtension = DataUtils.isExtension(search_results_dictionary, null);
String search_results_version = info.version;

HashMap hmap = DataUtils.getNamespaceId2CodingSchemeFormalNameMapping();
HashMap name_hmap = new HashMap();
String vocabulary_name = null;
String short_vocabulary_name = null;
String coding_scheme_version = null;
String ns = null;

String key = (String) request.getSession().getAttribute("key");
if (key == null) {
    key = HTTPUtils.cleanXSS((String) request.getParameter("key"));
}

_logger.debug("search_results.jsp dictionary: " + search_results_dictionary);
_logger.debug("search_results.jsp version: " + search_results_version);

if (search_results_version != null) {
    request.setAttribute("version", search_results_version);
}

if (search_results_dictionary == null || search_results_dictionary.compareTo("NCI Thesaurus") == 0) {
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

key = (String) request.getSession().getAttribute("key");
boolean bool_val;

String resultsPerPage = HTTPUtils.cleanXSS((String) request.getParameter("resultsPerPage"));
if (resultsPerPage == null) {
    resultsPerPage = (String) request.getSession().getAttribute("resultsPerPage");
    if (resultsPerPage == null) {
       resultsPerPage = "50";
    }
} else {



		    bool_val = JSPUtils.isInteger(resultsPerPage);
		    if (!bool_val) {
			 String redirectURL = request.getContextPath() + "/pages/appscan_response.jsf";
			 String error_msg = HTTPUtils.createErrorMsg("resultsPerPage", resultsPerPage);
			 request.getSession().setAttribute("error_msg", error_msg);
			 response.sendRedirect(redirectURL);
		    } else {
			 resultsPerPage = "50";
		    }  



    request.getSession().setAttribute("resultsPerPage", resultsPerPage);
}

String selectedResultsPerPage = resultsPerPage;

_logger.debug("search_result.jsp " + key);
request.setAttribute("key", key);

          IteratorBeanManager iteratorBeanManager = (IteratorBeanManager) FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap().get("iteratorBeanManager");

          IteratorBean iteratorBean = iteratorBeanManager.getIteratorBean(key);
         

    if (iteratorBean == null){
      _logger.warn("iteratorBean NOT FOUND???" + key);
    }
String matchText = HTTPUtils.cleanXSS((String) request.getSession().getAttribute("matchText"));
 
int pageNum = 0; 
int pageSize = Integer.parseInt( resultsPerPage );
int size = iteratorBean.getSize();    
List list = null;
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
    
}

int istart = pageNum * pageSize;
int page_num = pageNum;
if (page_num == 0) {
    page_num++;
    
} else {
    istart = (pageNum-1) * pageSize;
}

int iend = istart + pageSize - 1;
//if (page_num == 1 && iend > size) {
//    iend = size - 1;
//}

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
   ex.printStackTrace();
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
   

          String contains_warning_msg = HTTPUtils.cleanXSS((String) request.getSession().getAttribute("contains_warning_msg"));
          request.getSession().removeAttribute("contains_warning_msg");
          
          int next_page_num = page_num + 1;
          int prev_page_num = page_num - 1;
          String prev_page_num_str = Integer.toString(prev_page_num);
          String next_page_num_str = Integer.toString(next_page_num);


/*

          String matchText = HTTPUtils.cleanXSS((String) request.getSession().getAttribute("matchText"));
          //String match_size = HTTPUtils.cleanXSS((String) request.getSession().getAttribute("match_size"));
          String page_string = HTTPUtils.cleanXSS((String) request.getSession().getAttribute("page_string"));
          Boolean new_search = (Boolean) request.getSession().getAttribute("new_search");
          String page_number = HTTPUtils.cleanXSS((String) request.getParameter("page_number"));
          //String selectedResultsPerPage = HTTPUtils.cleanXSS((String) request.getSession().getAttribute("selectedResultsPerPage"));
          String contains_warning_msg = HTTPUtils.cleanXSS((String) request.getSession().getAttribute("contains_warning_msg"));

          if (page_number != null)
          {
          
		    bool_val = JSPUtils.isInteger(page_number);
		    if (!bool_val) {
			 String redirectURL = request.getContextPath() + "/pages/appscan_response.jsf";
			 String error_msg = HTTPUtils.createErrorMsg("page_number", page_number);
			 request.getSession().setAttribute("error_msg", error_msg);
			 response.sendRedirect(redirectURL);
		    } 

          }
          
          if (page_number != null && new_search == Boolean.FALSE)
          {
               page_string = page_number;
          }
          request.getSession().setAttribute("new_search", Boolean.FALSE);
          
          int page_num = Integer.parseInt(page_string);
          int next_page_num = page_num + 1;
          int prev_page_num = page_num - 1;
          int page_size = 50;

          if (selectedResultsPerPage != null && selectedResultsPerPage.compareTo("") != 0)
          {
              page_size = Integer.parseInt(selectedResultsPerPage);
          }

          int iend = page_num * page_size;
          int istart = iend - page_size;
          iend = iend-1;
          int size = 0;
          String match_size = "0";

          if (iteratorBean != null) {
	      size = iteratorBean.getSize();
	      match_size = Integer.valueOf(size).toString();
	      
          }

          if (iend > size-1) iend = size-1;
          int num_pages = size / page_size;
          if (num_pages * page_size < size) num_pages++;
          String istart_str = Integer.toString(istart+1);
          String iend_str = Integer.toString(iend+1);
          String prev_page_num_str = Integer.toString(prev_page_num);
          String next_page_num_str = Integer.toString(next_page_num);

	  int numberRemaining_before = iteratorBean.getSize();
	  List list = iteratorBean.getData(istart, iend);
	  int numberRemaining_after = iteratorBean.getSize();
 
  	  if (numberRemaining_before != numberRemaining_after) {
		iend_str = Integer.valueOf(numberRemaining_after).toString();
		match_size = Integer.valueOf(numberRemaining_after).toString();
		size = iteratorBean.getSize();
		iend = page_num * page_size;
		istart = iend - page_size;
		iend = iend-1;

                if (iend > size-1) iend = size-1;
                num_pages = size / page_size;
                if (num_pages * page_size < size) num_pages++;
                istart_str = Integer.toString(istart+1);
                iend_str = Integer.toString(iend+1);
                prev_page_num_str = Integer.toString(prev_page_num);
                next_page_num_str = Integer.toString(next_page_num);
	        list = iteratorBean.getData(istart, iend);         
	  }

	  
int expected_count = (iend - istart) + 1;
int actual_count = list.size();
if (expected_count != actual_count) {
    if (actual_count < expected_count) {
        int upper_bound = istart + actual_count;
        
	iend_str = Integer.valueOf(upper_bound).toString();
	match_size = Integer.valueOf(upper_bound).toString();
        
    }
}

*/


    String message = null;
    if (list.size() == 0) {
        message = "No match found.";    
    }

    
    boolean timeout = iteratorBean.getTimeout();
    if (timeout) {
      %>
      <p class="textbodyred">WARNING: System times out. Please advance fewer pages at one time.</p>
      <%
    } else if (message != null) {
    %>
      <p class="textbodyred"><%=message%></p>
    <%
    } else {

        %>

        <table class="datatable_960" border="0" width="100%">
          <tr>
            <table>
              <tr>
                <td class="texttitle-blue">Result for:</td>
                <td class="texttitle-gray"><%=matchText%></td>
              </tr>
            </table>
          </tr>
          <tr>
            <td><hr></td>
          </tr>
          <tr>
            <td>
               <%
               if (contains_warning_msg != null) {
               %>
                  <b>Results <%=istart_str%>-<%=iend_str%> of&nbsp;<%=match_size%> for: <%=matchText%></b>&nbsp;<%=contains_warning_msg%>
               <%
               } else {
               %>
                  <b>Results <%=istart_str%>-<%=iend_str%> of&nbsp;<%=match_size%> for: <%=matchText%></b>
               <%
               }
               %>
            </td>
          </tr>
          <tr>
            <td class="textbody">
              <table class="datatable_960" summary="" cellpadding="3" cellspacing="0" border="0" width="100%">

<%
if (isMapping || isExtension) {
%>
                <th class="dataTableHeader" scope="col" align="left">Concept</th>
                <th class="dataTableHeader" scope="col" align="left">Vocabulary</th>
<%
}
%>

                <%

/*
                  Vector code_vec = new Vector();
                  for (int k=0; k<list.size(); k++) {
                      ResolvedConceptReference rcr = (ResolvedConceptReference) list.get(k);
                      if (rcr != null) {
                          code_vec.add(rcr.getConceptCode());
                      } else {
                          code_vec.add(null);
                      }
                  }

//to be modified:
                  Vector status_vec = DataUtils.getConceptStatusByConceptCodes(search_results_dictionary, search_results_version, null, code_vec);
*/                  


HashMap concept_status_hmap = DataUtils.getPropertyValuesInBatch(list, "Concept_Status");
                  
                  
                  int i = -1;

                  for (int k=0; k<list.size(); k++) {
                      Object obj = list.get(k);
                      ResolvedConceptReference rcr = null;

                      if (obj != null) {
                      
			      rcr = (ResolvedConceptReference) obj;
			      String code = rcr.getConceptCode();
			      ns = rcr.getCodeNamespace(); 
                    
                      
                      	coding_scheme_version = rcr.getCodingSchemeVersion();
			if (isMapping || isExtension) {

			    //vocabulary_name = (String) DataUtils.getFormalName(rcr.getCodingSchemeName());
			    vocabulary_name = (String) DataUtils.getFormalName(rcr.getCodeNamespace());
			    if (vocabulary_name == null) {
			      vocabulary_name = (String) hmap.get(rcr.getCodingSchemeName());
			    }

			    short_vocabulary_name = null;
			    if (name_hmap.containsKey(vocabulary_name)) {
			      short_vocabulary_name = (String) name_hmap.get(vocabulary_name);
			    } else {
			      short_vocabulary_name = DataUtils.getMetadataValue(vocabulary_name, coding_scheme_version, "display_name");
			      if (short_vocabulary_name == null || short_vocabulary_name.compareTo("null") == 0) {
				short_vocabulary_name = DataUtils.getLocalName(vocabulary_name);
			      }
			      name_hmap.put(vocabulary_name, short_vocabulary_name);
			    }
			}


                      String name = "";
                      if (rcr.getEntityDescription() != null) {
                          name = rcr.getEntityDescription().getContent();
                      } else {
			      Entity entity = SearchUtils.getConceptByCode(rcr.getCodeNamespace(), null, null, rcr.getConceptCode());
			      if (entity != null && entity.getEntityDescription() != null) {
			      	  name = entity.getEntityDescription().getContent();
			      } 
                      }
                      
                      
                      
                      if (code == null) {
                          i++;
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
          <td class="dataCellText" scope="row">
             <%=DataUtils.encodeTerm(name)%>
          </td>
        </tr>
          <%
                      }

                      else if (code != null && code.indexOf("@") != -1 && name.compareTo("") == 0) {
                          i++;
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
          <td class="dataCellText" scope="row">
             <%=code%>
          </td>
        </tr>
          <%
                      }
                      
                      else if (code != null) { // && code.indexOf("@") == -1) {
                          i++;
                          
                        if (name.compareTo("") == 0) {
                             name = "Not available";
                        }
			String con_status = null;
			if (concept_status_hmap != null) {
			    con_status = (String) concept_status_hmap.get(rcr.getCodingSchemeName() + "$" + rcr.getCodingSchemeVersion() + "$" + code);
			}
			if (con_status != null) {
			    con_status = con_status.replaceAll("_", " ");
			}
              

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

          <td class="dataCellText" scope="row">
          <%

          if (con_status == null) {
          %>
             <a href="<%=request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=search_results_dictionary%>&version=<%=search_results_version%>&code=<%=code%>&ns=<%=ns%>&key=<%=key%>&b=1&n=<%=page_number%>" ><%=DataUtils.encodeTerm(name)%></a>
          <%
          } else {
          %>
             <a href="<%=request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=search_results_dictionary%>&version=<%=search_results_version%>&code=<%=code%>&ns=<%=ns%>&key=<%=key%>&b=1&n=<%=page_number%>" ><%=DataUtils.encodeTerm(name)%></a>&nbsp;(<%=con_status%>)
          <%
          }
          %>
          </td>

<%
if (isMapping || isExtension) {
%>
            <td class="dataCellText">
           <%=short_vocabulary_name%>
            </td>
<%
}
%>

        </tr>
            <%
                        }
                     }
                  }

                %>
              </table>
            </td>
          </tr>
        </table>

               <%
               }
               %>


        <%@ include file="/pages/templates/pagination.jsp" %>
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
