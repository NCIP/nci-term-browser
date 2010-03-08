<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="java.util.Vector"%>
<%@ page import="org.LexGrid.concepts.Concept" %>
<%@ page import="gov.nih.nci.evs.browser.common.Constants" %>
<%@ page import="gov.nih.nci.evs.browser.utils.HTTPUtils" %>
<%@ page import="gov.nih.nci.evs.browser.utils.DataUtils" %>
<%@ page import="gov.nih.nci.evs.browser.bean.IteratorBean" %>
<%@ page import="org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference" %>
<%@ page import="javax.faces.context.FacesContext" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
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

<f:view>
  <%@ include file="/pages/templates/header.jsp" %>
  <div class="center-page">
    <%@ include file="/pages/templates/sub-header.jsp" %>
    <!-- Main box -->
    <div id="main-area">

<%
String search_results_dictionary = (String) request.getSession().getAttribute("dictionary");

if (search_results_dictionary.compareTo("NCI Thesaurus") == 0) {
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
        <%

          //HashMap hmap = DataUtils.getNamespaceId2CodingSchemeFormalNameMapping();

/*
          IteratorBean iteratorBean = (IteratorBean) FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap().get("iteratorBean");
*/
          
          String key = (String) request.getSession().getAttribute("key");
          IteratorBeanManager iteratorBeanManager = (IteratorBeanManager) FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap().get("iteratorBeanManager");
                
          IteratorBean iteratorBean = iteratorBeanManager.getIteratorBean(key);
          
	  if (iteratorBean == null){
	    System.out.println("iteratorBean NOT FOUND???" + key);  
	  }

          String matchText = HTTPUtils.cleanXSS((String) request.getSession().getAttribute("matchText"));
          //String match_size = HTTPUtils.cleanXSS((String) request.getSession().getAttribute("match_size"));
          String page_string = HTTPUtils.cleanXSS((String) request.getSession().getAttribute("page_string"));
          Boolean new_search = (Boolean) request.getSession().getAttribute("new_search");
          String page_number = HTTPUtils.cleanXSS((String) request.getParameter("page_number"));
          String selectedResultsPerPage = HTTPUtils.cleanXSS((String) request.getSession().getAttribute("selectedResultsPerPage"));
          String contains_warning_msg = HTTPUtils.cleanXSS((String) request.getSession().getAttribute("contains_warning_msg"));

          if (page_number != null && new_search == Boolean.FALSE)
          {
              page_string = page_number;
          }
          
          request.getSession().setAttribute("new_search", Boolean.FALSE);
          int page_num = Integer.parseInt(page_string);
          int next_page_num = page_num + 1;
          int prev_page_num = page_num - 1;
          int page_size = 50;
          
          if (selectedResultsPerPage != null)
          {
              page_size = Integer.parseInt(selectedResultsPerPage);
          }
          
          int iend = page_num * page_size;
          int istart = iend - page_size;
          iend = iend-1;
          int size = iteratorBean.getSize();
          
          String match_size = new Integer(size).toString();

          
          if (iend > size-1) iend = size-1;
          int num_pages = size / page_size;
          if (num_pages * page_size < size) num_pages++;
          String istart_str = Integer.toString(istart+1);
          String iend_str = Integer.toString(iend+1);
          String prev_page_num_str = Integer.toString(prev_page_num);
          String next_page_num_str = Integer.toString(next_page_num);

	  List list = iteratorBean.getData(istart, iend);
	  boolean timeout = iteratorBean.getTimeout();
	  if (timeout) {
		  %>
		  <p class="textbodyred">WARNING: System times out. Please advance fewer pages at one time.</p>
		  <%
	  } else {
                  
        %>

        <table width="700px">
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
              <table class="dataTable" summary="" cellpadding="3" cellspacing="0" border="0" width="100%">
                <%
                  
                  Vector code_vec = new Vector();
                  for (int k=0; k<list.size(); k++) {
                      ResolvedConceptReference rcr = (ResolvedConceptReference) list.get(k);
                      if (rcr != null) {
                      	  code_vec.add(rcr.getConceptCode());
                      } else {
                          code_vec.add(null);
                      }
                  }


                  //Vector status_vec = DataUtils.getStatusByConceptCodes(search_results_dictionary, null, null, code_vec);
                  Vector status_vec = DataUtils.getConceptStatusByConceptCodes(search_results_dictionary, null, null, code_vec);
                  int i = -1;
                 
                  for (int k=0; k<list.size(); k++) {
                      Object obj = list.get(k);
                      ResolvedConceptReference rcr = null;

if (obj == null) {
   System.out.println("rcr == null???????????????????");
} else {
   rcr = (ResolvedConceptReference) obj;
}
                      
                      if (rcr != null) {
                      String code = rcr.getConceptCode();
                      
                      String name = "null";
                      if (rcr.getEntityDescription() != null) {
                          name = rcr.getEntityDescription().getContent();
                      }
                      if (code == null || code.indexOf("@") != -1) {
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
				  <td class="dataCellText">
				     <%=name%>
				  </td>
				</tr>
		      <%	
                      }
                      
                      else if (code != null && code.indexOf("@") == -1) {
                          i++;
            		  String con_status = null;
			  if (status_vec != null && status_vec.elementAt(i) != null) {
			     con_status = (String) status_vec.elementAt(i);
			     con_status = con_status.replaceAll("_", " ");
			  }

                          String vocabulary_name = search_results_dictionary;//(String) hmap.get(rcr.getCodingSchemeName());

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
				  if (con_status == null) {
				  %>
				     <a href="<%=request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=vocabulary_name%>&code=<%=code%>" ><%=name%></a>
				  <%
				  } else {
				  %>
				     <a href="<%=request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=vocabulary_name%>&code=<%=code%>" ><%=name%></a>&nbsp;(<%=con_status%>)
				  <%
				  }
				  %>
				  </td>
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