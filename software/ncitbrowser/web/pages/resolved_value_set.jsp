<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="org.LexGrid.concepts.Entity"%>
<%@ page import="gov.nih.nci.evs.browser.bean.*"%>
<%@ page import="gov.nih.nci.evs.browser.utils.*"%>
<%@ page import="gov.nih.nci.evs.browser.properties.*"%>
<%@ page import="gov.nih.nci.evs.browser.utils.*"%>
<%@ page import="javax.faces.context.FacesContext"%>
<%@ page import="org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference"%>
<%@ page import="org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator"%>
<%@ pageimport="org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList"%>
<%@ page import="org.apache.log4j.*"%>

<%@ page import="org.LexGrid.LexBIG.LexBIGService.LexBIGService"%>
<%@ page import="org.lexgrid.resolvedvalueset.LexEVSResolvedValueSetService"%>
<%@ page import="org.lexgrid.valuesets.LexEVSValueSetDefinitionServices"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns:c="http://java.sun.com/jsp/jstl/core">
<head>
<title>NCI Thesaurus</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/styleSheet.css" />
<link rel="shortcut icon" href="<%=request.getContextPath()%>/favicon.ico" type="image/x-icon" />
<script type="text/javascript" src="<%=request.getContextPath()%>/js/script.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/search.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/dropdown.js"></script>
</head>
<body>
   <script type="text/javascript" src="<%=request.getContextPath()%>/js/wz_tooltip.js"></script>
   <script type="text/javascript" src="<%=request.getContextPath()%>/js/tip_centerwindow.js"></script>
   <script type="text/javascript" src="<%=request.getContextPath()%>/js/tip_followscroll.js"></script>
   
   
   
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
   
   
   
   
   <%!private static Logger _logger = Utils.getJspLogger("value_set_search_results.jsp");%>
   <f:view>
      <!-- Begin Skip Top Navigation -->
      <a href="#evs-content" class="hideLink" accesskey="1" title="Skip repetitive navigation links">skip navigation links</a>
      <!-- End Skip Top Navigation -->
      <%@ include file="/pages/templates/header.jsp"%>
      <div class="center-page_960">
         <%@ include file="/pages/templates/sub-header.jsp"%>
         <!-- Main box -->
         <div id="main-area_960">
            <%@ include file="/pages/templates/content-header-resolvedvalueset.jsp"%>
            <%
                        String version_selection = (String) request.getSession().getAttribute("version_selection");
                        request.getSession().removeAttribute("version_selection");
                        
                        boolean bool_val;
            		int numRemaining = 0;
            		String valueSetSearch_requestContextPath = request
            				.getContextPath();

            		String message = (String) request.getSession().getAttribute("message");
            		request.getSession().removeAttribute("message");
            		String vsd_uri = (String) request.getSession().getAttribute("vsd_uri");
            		String metadata = DataUtils
            				.getValueSetDefinitionMetadata(DataUtils
            						.findValueSetDefinitionByURI(vsd_uri));
            		Vector u = StringUtils.parseData(metadata);
            		String name = (String) u.elementAt(0);
            		String valueset_uri = (String) u.elementAt(1);
            		String description = (String) u.elementAt(2);
            		String concept_domain = (String) u.elementAt(3);
            		String sources = (String) u.elementAt(4);
            		String supportedsources = (String) u.elementAt(5);
            		String supportedsource = null;
            		
            		String defaultCodingScheme = (String) u.elementAt(6);
            		

            		IteratorBeanManager iteratorBeanManager = (IteratorBeanManager) FacesContext
            				.getCurrentInstance().getExternalContext()
            				.getSessionMap().get("iteratorBeanManager");

            		if (iteratorBeanManager == null) {
            			iteratorBeanManager = new IteratorBeanManager();
            			request.getSession().setAttribute("iteratorBeanManager",
            					iteratorBeanManager);
            		}

            		String resolved_vs_key = (String) request.getSession()
            				.getAttribute("resolved_vs_key");
            		IteratorBean iteratorBean = iteratorBeanManager
            				.getIteratorBean(resolved_vs_key);
            				
           		
            		if (iteratorBean == null) {
           		
            			ResolvedConceptReferencesIterator itr = (ResolvedConceptReferencesIterator) request
            					.getSession().getAttribute(
            							"ResolvedConceptReferencesIterator");
            			iteratorBean = new IteratorBean(itr);
            			
            			iteratorBean.initialize();
            			iteratorBean.setKey(resolved_vs_key);
            			request.getSession().setAttribute("resolved_vs_key",
            					resolved_vs_key);
            			iteratorBeanManager.addIteratorBean(iteratorBean);
            			int itr_size = iteratorBean.getSize();
         			
           			Integer obj = Integer.valueOf(itr_size);
            			String itr_size_str = obj.toString();
           			request.getSession().setAttribute("itr_size_str",
            					itr_size_str);

            		} else {
           			int itr_size = iteratorBean.getSize();
            			Integer obj = Integer.valueOf(itr_size);
            			String itr_size_str = obj.toString();
            		}

            		String resultsPerPage =  HTTPUtils.cleanXSS((String) request.getParameter("resultsPerPage"));
            		if (resultsPerPage == null) {
            			resultsPerPage = (String) request.getSession()
            					.getAttribute("resultsPerPage");
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
					 request.getSession().setAttribute("resultsPerPage", resultsPerPage);
				    }              		
 
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

            		int num_pages = size / pageSize;
            		if (num_pages * pageSize < size)
            			num_pages++;


            		int istart = (page_num - 1) * pageSize;
            		if (istart < 0)
            			istart = 0;

            		int iend = istart + pageSize - 1;
            		if (iend > size)
            			iend = size - 1;
            %>
            
            <!--
            <div class="pagecontent" style="width:590px;">
            -->
            <div class="pagecontent"> 
            
               <a name="evs-content" id="evs-content"></a>               
                  <div class="tabTableContentContainer">
                  <h:form id="valueSetSearchResultsForm" styleClass="search-form" acceptcharset="UTF-8">
                     <%
                     	if (!DataUtils.isNull(message)) {
                     %>
                     <p class="textbodyred">
                        &nbsp;<%=message%></p>
                     <%
                        } else {
                     %>
                     <table border="0">
                        <tr>
                           <td>
                              <table border="0" width="900" >
                                 <tr>
                                    <td align="left" class="texttitle-blue">Value Set:&nbsp;<%=vsd_uri%></td>
                                    <td align="right">
                                       <h:commandLink
                                          value="Export XML"
                                          action="#{valueSetBean.exportToXMLAction}"
                                          styleClass="texttitle-blue-small"
                                          title="Export VSD in LexGrid XML format" />
                                       | <h:commandLink
                                          value="Export CSV"
                                          action="#{valueSetBean.exportToCSVAction}"
                                          styleClass="texttitle-blue-small"
                                          title="Export VSD in CSV format" />
                                    </td>
                                 </tr>
                              </table>
                           </td>
                        </tr>
                        <tr class="textbodyred">
                           <td></td>
                        </tr>
                        <tr class="textbody">
                           <td><b>Name</b>: <%=DataUtils.encodeTerm(name)%></td>
                        </tr>
                        <tr class="textbody">
                           <td><b>Description</b>: <%=description%></td>
                        </tr>
                        <tr class="textbody">
                           <td><b>Concept Domain</b>: <%=concept_domain%></td>
                        </tr>
                        <tr class="textbody">
                           <td><b>Sources</b>: <%=supportedsources%></td>
                        </tr>
                        <tr class="textbody">
                           <td>&nbsp;</td>
                        </tr>
                        <tr class="textbody">
                           <td><b>Concepts</b>:</td>
                        </tr>
                        <tr class="textbody">
                           <td>
                        <%   
                        boolean reformat = true;
                        boolean use_new_format = true;
                        String rvs_tbl = null;
                        if (supportedsources != null) {
                            Vector w = gov.nih.nci.evs.browser.utils.StringUtils.parseData(supportedsources, "$");
                            supportedsource = (String) w.elementAt(0);
                        }
                        if (supportedsource == null || supportedsource.compareTo("null") == 0) {
                            reformat = false;
                        }
                        if (reformat) {
                                Vector codes = new Vector();
				List list = iteratorBean.getData(istart, iend);
				for (int k = 0; k < list.size(); k++) {
					Object obj = list.get(k);
					ResolvedConceptReference ref = (ResolvedConceptReference) obj;
					codes.add(ref.getConceptCode());
				}  

				LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
				LexEVSValueSetDefinitionServices vsd_service = RemoteServerUtil.getLexEVSValueSetDefinitionServices();
				ValueSetFormatter formatter = new ValueSetFormatter(lbSvc, vsd_service);
				Vector fields = formatter.getDefaultFields();
				//public String generate(String vsd_uri, String version, String source, Vector fields, Vector codes, int maxReturn) {
				rvs_tbl = formatter.generate(defaultCodingScheme, null, supportedsource, fields, codes, codes.size());
			} else if (use_new_format) {
                                Vector codes = new Vector();
				List list = iteratorBean.getData(istart, iend);
				for (int k = 0; k < list.size(); k++) {
					Object obj = list.get(k);
					ResolvedConceptReference ref = (ResolvedConceptReference) obj;
					codes.add(ref.getConceptCode());
				}  

				LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
				LexEVSValueSetDefinitionServices vsd_service = RemoteServerUtil.getLexEVSValueSetDefinitionServices();
				ValueSetFormatter formatter = new ValueSetFormatter(lbSvc, vsd_service);
				Vector fields = formatter.getDefaultFields(false);
				//public String generate(String vsd_uri, String version, String source, Vector fields, Vector codes, int maxReturn) {
				rvs_tbl = formatter.generate(defaultCodingScheme, null, supportedsource, fields, codes, codes.size());			
			}
			if (rvs_tbl != null) {
			%>	
			   <%=rvs_tbl%>
			<%   
			} else {
                        %>
                              <table class="datatable_960" summary="Data Table" cellpadding="3" cellspacing="0" border="0" width="100%">
                                 <th class="dataTableHeader" scope="col" align="left">Code</th>
                                 <th class="dataTableHeader" scope="col" align="left">Name</th>
                                 <th class="dataTableHeader" scope="col" align="left">Vocabulary</th>
                                 <th class="dataTableHeader" scope="col" align="left">Namespace</th>
                                 <%
                                 	Vector concept_vec = new Vector();
                                 				List list = iteratorBean.getData(istart, iend);
                                 				for (int k = 0; k < list.size(); k++) {
                                 					Object obj = list.get(k);
                                 					ResolvedConceptReference ref = null;
                                 					if (obj == null) {
                                 						_logger.warn("rcr == null???");
                                 					} else {
                                 						ref = (ResolvedConceptReference) obj;
                                 					}

                                 					String entityDescription = "<NOT ASSIGNED>";
                                 					if (ref.getEntityDescription() != null) {
                                 						entityDescription = ref.getEntityDescription()
                                 								.getContent();
                                 					}

                                 					String t = ref.getConceptCode() + "|"
                                 							+ entityDescription + "|"
                                 							+ ref.getCodingSchemeName() + "|"
                                 							+ ref.getCodeNamespace() + "|"
                                 							+ ref.getCodingSchemeVersion();
                            							
                                 					concept_vec.add(t);                                 							
 
                                 				}
                                 				for (int i = 0; i < concept_vec.size(); i++) {
                                 					String concept_str = (String) concept_vec
                                 							.elementAt(i);
                                 					u = StringUtils.parseData(concept_str);
                                 					String code = (String) u.elementAt(0);
                                 					String conceptname = (String) u.elementAt(1);
                                 					String coding_scheme = (String) u.elementAt(2);
                                 					String namespace = (String) u.elementAt(3);
                                 					
                                 					String vsn = (String) u.elementAt(4);
                                 					String coding_scheme_nm = namespace;
                                 					vsn = DataUtils.getProductionVersion(namespace);



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
                                       	if (code.indexOf("@") == -1) {
                                            namespace = DataUtils.getCSName(namespace);
                                       %> 
                                           <a href="<%=request.getContextPath()%>/ConceptReport.jsp?dictionary=<%=namespace%>&version=<%=vsn%>&ns=<%=namespace%>&code=<%=code%>"><%=code%></a>
                                       <%
                                        	} else {
                                       %> <%=code%> <%
                                        	}
                                       %>
                                    </td>
                                    <td class="dataCellText"><%=DataUtils.encodeTerm(conceptname)%></td>
                                    <td class="dataCellText"><%=coding_scheme%></td>
                                    <td class="dataCellText"><%=namespace%></td>
                                 <%
                                 	}
                                 %>
                                 </tr>
                              </table>

			     <%
				}
			     %>

                           </td>
                        </tr>
                     </table>
                     <%
                     	}
                     %>
                     <input type="hidden" name="vsd_uri" id="vsd_uri" value="<%=vsd_uri%>">
                     <input type="hidden" name="referer" id="referer" value="<%=HTTPUtils.getRefererParmEncode(request)%>">
<%                     
if (version_selection != null)
{
%>
<input type="hidden" name="version_selection" id="version_selection" value="true">
<%
}
%>
                 
                     
                     </h:form>
               </div> <!-- end tabTableContentContainer -->      
               <%
                 if (DataUtils.isNull(message)) {
               %>
               <%@ include file="/pages/templates/pagination-resolved-valueset.jsp"%>
               <%
                  }
               %>
               <%@ include file="/pages/templates/nciFooter.jsp"%>
            </div><!-- end Page content -->
         </div><!-- end Main box -->         
      </div>      
      <div class="mainbox-bottom"><img src="<%=basePath%>/images/mainbox-bottom.gif" width="945" height="5" alt="Mainbox Bottom" /></div>
   </f:view>
</body>
</html>
