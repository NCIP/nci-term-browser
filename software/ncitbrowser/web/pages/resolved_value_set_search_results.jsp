<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*"%>
<%@ page import="org.LexGrid.concepts.Entity" %>
<%@ page import="gov.nih.nci.evs.browser.bean.*" %>
<%@ page import="gov.nih.nci.evs.browser.utils.*" %>
<%@ page import="gov.nih.nci.evs.browser.properties.*" %>
<%@ page import="gov.nih.nci.evs.browser.utils.*" %>
<%@ page import="javax.faces.context.FacesContext" %>
<%@ page import="org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference" %>
<%@ page import="org.apache.log4j.*" %>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns:c="http://java.sun.com/jsp/jstl/core">
<head>
  <title>NCI Term Browser - Value Set Search</title>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
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
  

  <%!
    private static Logger _logger = Utils.getJspLogger("resolved_value_set_search_results.jsp");
    private static String _ncimUrl = NCItBrowserProperties.getNCIM_URL();
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
        <%@ include file="/pages/templates/content-header-resolvedvalueset.jsp" %>
        
<%
HashMap name_hmap = new HashMap();
String valueSetSearch_requestContextPath = request.getContextPath();
String message = (String) request.getSession().getAttribute("message");  
request.getSession().removeAttribute("message");  

String vsd_uri = (String) request.getSession().getAttribute("selectedvalueset");
if (vsd_uri == null) {
vsd_uri = (String) request.getSession().getAttribute("vsd_uri");
}

Vector coding_scheme_ref_vec = DataUtils.getCodingSchemesInValueSetDefinition(vsd_uri);
String checked = "";
boolean bool_val;

%>
        <div class="pagecontent">
          <a name="evs-content" id="evs-content"></a>
          
          <%-- 0 <%@ include file="/pages/templates/navigationTabs.jsp"%> --%>
          
<%


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
        
        request.getSession().removeAttribute("dictionary");
        
        HashMap hmap = DataUtils.getNamespaceId2CodingSchemeFormalNameMapping();


          IteratorBeanManager iteratorBeanManager = (IteratorBeanManager) FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap().get("iteratorBeanManager");

String key = (String) request.getSession().getAttribute("key");

          IteratorBean iteratorBean = iteratorBeanManager.getIteratorBean(key);
                    

        String matchText = HTTPUtils.cleanXSS((String) request.getSession().getAttribute("matchText_RVS"));
        if (matchText == null) matchText = "";
        if (matchText != null && matchText.compareTo("null") == 0) matchText = "";
        
        String page_string = HTTPUtils.cleanXSS((String) request.getSession().getAttribute("page_string"));
        Boolean new_search = (Boolean) request.getSession().getAttribute("new_search");

        String page_number = HTTPUtils.cleanXSS((String) request.getParameter("page_number"));
        //String selectedResultsPerPage = HTTPUtils.cleanXSS((String) request.getSession().getAttribute("selectedResultsPerPage"));
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
	  }
	  

      boolean no_match = false;
 
     
      %>
        <table width="900px">
<%
	      if (!DataUtils.isNull(message)) {
		    if (message.startsWith("No match found")) {
			no_match = true;
		    } 
%>
			<tr class="textbodyred"><td>
		      <p class="textbodyred">&nbsp;<%=message%></p>
			</td></tr>
	      <%}%>	
			
			
			
<% 
if (!no_match) {           
%>        
        
          <tr>
            <td>
              <table>
                <tr>
                  <td class="texttitle-blue">Value Set Search Result for:</td>
                  <td class="texttitle-gray"><%=match_text%></td>
                </tr>
              </table>
            </td>
          </tr>
          <tr>
            <td><hr></td>
          </tr>
          <tr>
            <td class="dataTableHeader">
            <%
              if (contains_warning_msg != null) {
             %>
              <b>Results <%=istart_str%>-<%=iend_str%> of&nbsp;<%=match_size%> for: <%=match_text%></b>&nbsp;<%=contains_warning_msg%>
             <%
              } else {
              %>
              Results <%=istart_str%>-<%=iend_str%> of&nbsp;<%=match_size%> for: <%=match_text%></b>
              <%
              }

              
              %>
            </td>
          </tr>

          <tr>
            <td class="textbody">
              <table class="datatable_960" summary="" cellpadding="3" cellspacing="0" border="0" width="100%">
                <th class="dataTableHeader" scope="col" align="left">Concept</th>
                <th class="dataTableHeader" scope="col" align="left">Vocabulary</th>
                <%
                  list = iteratorBean.getData(istart, iend);

    boolean timeout = iteratorBean.getTimeout();
    message = iteratorBean.getMessage();

    if (!DataUtils.isNull(message)) {
      %>
      <p class="textbodyred"><%=message%></p>
      <%
	    message = null;
	    iteratorBean.setMessage(message);

    } else if (timeout) {
      %>
      <p class="textbodyred">WARNING: System times out. Please advance fewer pages at one time.</p>
      <%
    } else {

    for (int i=0; i<list.size(); i++) {
        ResolvedConceptReference rcr = (ResolvedConceptReference) list.get(i);
        String code = null;
        String name = "";
        if (rcr != null) {
            code = rcr.getConceptCode();
        }

       
        if (rcr != null && rcr.getEntityDescription() != null) {
            name = rcr.getEntityDescription().getContent();
        }
        
        if (rcr != null) {
        
        
        String cs_version = rcr.getCodingSchemeVersion();

        String vocabulary_name = (String) DataUtils.getFormalName(rcr.getCodingSchemeName());
        if (vocabulary_name == null) {
            vocabulary_name = (String) hmap.get(rcr.getCodingSchemeName());
        }

        String short_vocabulary_name = null;
        if (name_hmap.containsKey(vocabulary_name)) {
           short_vocabulary_name = (String) name_hmap.get(vocabulary_name);
        } else {
            short_vocabulary_name = DataUtils.getMetadataValue(vocabulary_name, "display_name");
        if (short_vocabulary_name == null || short_vocabulary_name.compareTo("null") == 0) {
            short_vocabulary_name = DataUtils.getLocalName(vocabulary_name);
        }
            name_hmap.put(vocabulary_name, short_vocabulary_name);
        }

            if (code == null || code.indexOf("@") != -1) {
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
              <td class="dataCellText">
                 <%=short_vocabulary_name%>
              </td>
            </tr>
            <%
            } else {

            String con_status = DataUtils.getConceptStatus(vocabulary_name, null, null, code);

            if (con_status != null) {
          	con_status = con_status.replaceAll("_", " ");
            }

            //String vocabulary_name_encoded = null;
            String vocabulary_nm = null;
            if (vocabulary_name != null) {
                //vocabulary_name_encoded = vocabulary_name.replace(" ", "%20");
                vocabulary_nm = DataUtils.getCSName(vocabulary_name);
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
          <%
          if (con_status == null) {
          %>

          <td class="dataCellText" scope="row">
          <%
          if (DataUtils.isNCIT(vocabulary_name)) {
          %>
               <a href="<%=request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=vocabulary_nm%>&version=<%=cs_version%>&code=<%=code%>" ><%=DataUtils.encodeTerm(name)%></a>
          <%
          } else if (vocabulary_name.compareToIgnoreCase("NCI MetaThesaurus") == 0) {
               String meta_url = _ncimUrl + "/ConceptReport.jsp?dictionary=NCI%20MetaThesaurus" + "&version=" + cs_version + "&code=" + code;
          %>
               <a href="javascript:openQuickLinkSite('<%=meta_url%>')"><%=DataUtils.encodeTerm(name)%></a>
          <%
          } else {
              if (code.indexOf("@") != -1) {
              
          %>    
                   <%=code%>
          <%         
                   
              } else {
           %>
                <a href="<%=request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=vocabulary_nm%>&version=<%=cs_version%>&code=<%=code%>" ><%=DataUtils.encodeTerm(name)%></a>
           <%
              
              }

          }
          %>
          </td>
          <td class="dataCellText">
            <%=short_vocabulary_name%>
          </td>


          <%
          } else {
          %>

          <td class="dataCellText" scope="row">
          <%
          if (DataUtils.isNCIT(vocabulary_name)) {
          %>
               <a href="<%=request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=vocabulary_nm%>&version=<%=cs_version%>&code=<%=code%>" ><%=DataUtils.encodeTerm(name)%></a>&nbsp;(<%=con_status%>)
          <%
          } else if (vocabulary_name.compareToIgnoreCase("NCI MetaThesaurus") == 0) {
               String meta_url = _ncimUrl + "/ConceptReport.jsp?dictionary=NCI%20MetaThesaurus" + "&version=" + cs_version + "&code=" + code;
          %>
               <a href="javascript:openQuickLinkSite('<%=meta_url%>')"><%=DataUtils.encodeTerm(name)%></a>&nbsp;(<%=con_status%>)
          <%
          } else {
               if (code.indexOf("@") != -1) {
               
           %>    
                    <%=code%>
           <%         
                    
               } else {
            %>
               <a href="<%=request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=vocabulary_nm%>&version=<%=cs_version%>&code=<%=code%>" ><%=DataUtils.encodeTerm(name)%></a>&nbsp;(<%=con_status%>)
            <%
               
               }
 
          }
         
          %>
          </td>
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
                  }
                %>
              </table>
            </td>
          </tr>
          
          
 <%
 }
 %>
          
          
          
        </table>
<% 


    
if(message == null || message.compareTo("null") == 0) {



%>        
        <%@ include file="/pages/templates/pagination-valueset-results.jsp" %>
<%       
}
%>
       
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
