<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="java.util.Vector"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="org.LexGrid.concepts.Entity" %>
<%@ page import="gov.nih.nci.evs.browser.common.Constants" %>
<%@ page import="gov.nih.nci.evs.browser.utils.HTTPUtils" %>

<%@ page import="gov.nih.nci.evs.browser.utils.*" %>
<%@ page import="gov.nih.nci.evs.browser.bean.IteratorBean" %>
<%@ page import="javax.faces.context.FacesContext" %>
<%@ page import="org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference" %>
<%@ page import="org.apache.log4j.*" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns:c="http://java.sun.com/jsp/jstl/core">
  <head>
    <title>NCI Term Browser</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />
    <script type="text/javascript" src="<%= request.getContextPath() %>/js/script.js"></script>
    <script type="text/javascript" src="<%= request.getContextPath() %>/js/search.js"></script>
    <script type="text/javascript" src="<%= request.getContextPath() %>/js/dropdown.js"></script>
  </head>
  <body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
    <script type="text/javascript" src="<%= request.getContextPath() %>/js/wz_tooltip.js"></script>
    <script type="text/javascript" src="<%= request.getContextPath() %>/js/tip_centerwindow.js"></script>
    <script type="text/javascript" src="<%= request.getContextPath() %>/js/tip_followscroll.js"></script>


  <script language="JavaScript">
    
     
    function onCodeButtonPressed(formname) {
          var algorithmObj = document.forms["searchTerm"].algorithm;
	  for (var j=0; j<algorithmObj.length; j++) {
		  algorithm = algorithmObj[j].value;
		  if (algorithm == "exactMatch") {
			 algorithmObj[j].checked = true;
			 document.forms["searchTerm"].getElementById("exactMatch").checked = true;
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
          var targetObj = document.forms["searchTerm"].searchTarget;
          targetObj[0].checked = true;
    }     
     
     
     
  </script>
  
  
  <%!
    private static Logger _logger = Utils.getJspLogger("multiple_search_results.jsp");
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
 <%
boolean reindex_required = false;
String ontologiesToSearchOnStr = HTTPUtils.cleanXSS((String) request.getSession().getAttribute("ontologiesToSearchOnStr"));

String requestContextPath = request.getContextPath();
requestContextPath = requestContextPath.replace("//ncitbrowser//ncitbrowser", "//ncitbrowser");

long ms = System.currentTimeMillis(), delay = 0;

String match_text = gov.nih.nci.evs.browser.utils.HTTPUtils
      .cleanXSS((String) request.getSession().getAttribute("matchText"));

match_text = HTTPUtils.convertJSPString(match_text);
request.getSession().setAttribute("matchText", match_text);

     if (match_text == null) match_text = "";

     String algorithm = (String) request.getSession().getAttribute("algorithm");

     String check_e = "", check_s = "" , check_c ="";
     if (algorithm == null || algorithm.compareTo("exactMatch") == 0)
       check_e = "checked";
     else if (algorithm.compareTo("startsWith") == 0)
       check_s= "checked";
     else
       check_c = "checked";

    String searchTarget = (String) request.getSession().getAttribute("searchTarget");
    String check_n = "", check_cd ="", check_p = "" , check_r ="";
    if (searchTarget == null || searchTarget.compareTo("names") == 0)
      check_n = "checked";
    else if (searchTarget.compareTo("codes") == 0)
      check_cd= "checked";      
    else if (searchTarget.compareTo("properties") == 0)
      check_p= "checked";
    else
      check_r = "checked";
%>
      <!-- Thesaurus, banner search area -->
      <h:form styleClass="search-form-main-area_960" id="searchTerm" acceptcharset="UTF-8">
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
              <div class="search-form">
              <input CLASS="searchbox-input"
                name="matchText"
                type="text"
                value="<%=match_text%>"
                onFocus="active = true"
                onBlur="active = false"
                onkeypress="return submitEnter('searchTerm:search',event);"
              />&nbsp;
              <h:commandButton
                id="search"
                value="Search"
                action="#{userSessionBean.multipleSearchAction}"
                image="#{requestContextPath}/images/search.gif"
                alt="Search">
              </h:commandButton>
              <h:outputLink
                value="#{facesContext.externalContext.requestContextPath}/pages/help.jsf#searchhelp">
                <h:graphicImage value="/images/search-help.gif" alt="Search Help"
                style="border-width:0;" />
              </h:outputLink>
              <table border="0" cellspacing="0" cellpadding="0">
                <tr valign="top" align="left">
                  <td align="left" class="textbody">

        <input type="radio" id="exactMatch" name="algorithm" value="exactMatch" alt="Exact Match" <%=check_e%> tabindex="4">Exact Match&nbsp;
        <input type="radio" id="startsWith" name="algorithm" value="startsWith" alt="Begins With" <%=check_s%> tabindex="4" onclick="onAlgorithmChanged('searchTerm');">Begins With&nbsp;
        <input type="radio" id="contains"   name="algorithm" value="contains"   alt="Contains"    <%=check_c%> tabindex="4" onclick="onAlgorithmChanged('searchTerm');">Contains&nbsp;


                  </td>
                </tr>
                <tr align="left">
                  <td height="1px" bgcolor="#2F2F5F"></td>
                </tr>
                <tr valign="top" align="left">
                  <td align="left" class="textbody">
                  
        <input type="radio" id="names" name="searchTarget" value="names" alt="Names" <%=check_n%> tabindex="5">Name&nbsp;
        <input type="radio" id="codes" name="searchTarget" value="codes" alt="Codes" <%=check_cd%> tabindex="5" onclick="onCodeButtonPressed('searchTerm');">Code&nbsp;
        <input type="radio" id="properties" name="searchTarget" value="properties" alt="Properties" <%=check_p%> tabindex="5">Property&nbsp;
        <input type="radio" id="relationships" name="searchTarget" value="relationships" alt="Relationships" <%=check_r%> tabindex="5">Relationship&nbsp;


<input type="hidden" name="selected_vocabularies" id="selected_vocabularies" value="<%=ontologiesToSearchOnStr%>">
                  
                  </td>
                </tr>
              </table>
              </div> <!--  end search-form -->
            </div> <!-- end searchbox -->
          <div class="searchbox-bottom"><img src="<%=basePath%>/images/searchbox-bottom.gif" width="352" height="2" alt="SearchBox Bottom" /></div>
          <!-- end Search box -->
          <!-- Global Navigation -->
          <%@ include file="/pages/templates/menuBar-termbrowserhome.jsp" %>
          <!-- end Global Navigation -->
      </div> <!-- end search-globalnav_960 -->
    </div> <!-- end bannerarea_960 -->
    </h:form>
    <!-- end Thesaurus, banner search area -->
    <!-- Quick links bar -->
    <%@ include file="/pages/templates/quickLink.jsp" %>
    <!-- end Quick links bar -->
    <!-- Page content -->
    <div class="pagecontent">
      <a name="evs-content" id="evs-content"></a>
      
      
      <%


String resultsPerPage = HTTPUtils.cleanXSS((String) request.getParameter("resultsPerPage"));
if (resultsPerPage == null) {
    resultsPerPage = (String) request.getSession().getAttribute("resultsPerPage");
    if (resultsPerPage == null) {
        resultsPerPage = "50";
    }
    
}  else {
    request.getSession().setAttribute("resultsPerPage", resultsPerPage);
}

String contains_warning_msg = HTTPUtils.cleanXSS((String) request.getSession().getAttribute("contains_warning_msg"));
request.getSession().removeAttribute("contains_warning_msg");

String selectedResultsPerPage = resultsPerPage;
request.getSession().removeAttribute("dictionary");
HashMap hmap = DataUtils.getNamespaceId2CodingSchemeFormalNameMapping();

IteratorBean iteratorBean = (IteratorBean) FacesContext.getCurrentInstance().getExternalContext()
      .getSessionMap().get("iteratorBean");


if(iteratorBean != null) {
	request.getSession().setAttribute("multiple_search_results", iteratorBean);
} 


String itr_key = null;
itr_key = HTTPUtils.cleanXSS((String) request.getParameter("key"));
if (itr_key == null) {
    itr_key = iteratorBean.getKey();
} else {
    iteratorBean = (IteratorBean) request.getSession().getAttribute("multiple_search_results");
}


//====================================================================================================
String matchText = HTTPUtils.cleanXSS((String) request.getSession().getAttribute("matchText"));
int pageNum = 0; 
int pageSize = Integer.parseInt( resultsPerPage );
int size = iteratorBean.getSize();    
List list = null;
int num_pages = size / pageSize;
if (num_pages * pageSize < size) num_pages++;

String page_number = HTTPUtils.cleanXSS((String) request.getParameter("page_number"));

if (!DataUtils.isNull(page_number)) {
    pageNum = Integer.parseInt(page_number);
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
    

int next_page_num = page_num + 1;
int prev_page_num = page_num - 1;
String prev_page_num_str = Integer.toString(prev_page_num);
String next_page_num_str = Integer.toString(next_page_num);


%>
      
      
      
        <table width="900px">
          <tr>
            <td>
              <table>
                <tr>
                  <td class="texttitle-blue">Result for:</td>
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
              if (contains_warning_msg != null && size > 0) {
             %>
              <b>Results <%=istart_str%>-<%=iend_str%> of&nbsp;<%=match_size%> for: <%=match_text%></b>&nbsp;<%=contains_warning_msg%>
             <%
              } else if (size > 0) {
              %>
              Results <%=istart_str%>-<%=iend_str%> of&nbsp;<%=match_size%> for: <%=match_text%></b>
              <%
              }
              String tooltip_str = JSPUtils.getSelectedVocabularyTooltip(request);
              HashMap name_hmap = new HashMap();
              %>
              from <a href="#" onmouseover="Tip('<%=tooltip_str%>')" onmouseout="UnTip()">selected vocabularies</a>.
            </td>
          </tr>

          <tr>
            <td class="textbody">
              <table class="datatable_960" summary="" cellpadding="3" cellspacing="0" border="0" width="100%">
<%

if (size > 0) {

%>
                <th class="dataTableHeader" scope="col" align="left">Concept</th>
                <th class="dataTableHeader" scope="col" align="left">Vocabulary</th>
<%
}   
%>
                
                <%
  

    boolean timeout = iteratorBean.getTimeout();
    String message = iteratorBean.getMessage();

    if (message != null) {
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


HashMap concept_status_hmap = DataUtils.getPropertyValuesInBatch(list, "Concept_Status");


    for (int i=0; i<list.size(); i++) {
   
        ResolvedConceptReference rcr = (ResolvedConceptReference) list.get(i);
        String code = rcr.getConceptCode();
        String name = "";
        String version = null;
        String ns = null;
        
        if (rcr != null) {
            if (rcr.getConceptCode() == null) {
                reindex_required = true;
            }
            if (rcr.getEntityDescription() == null) {
            
	         Entity entity = SearchUtils.getConceptByCode(rcr.getCodeNamespace(),
		   null, null, rcr.getConceptCode());
		 if (entity != null && entity.getEntityDescription() != null) { 
	         	name = entity.getEntityDescription().getContent(); 
	         } else {
	                name = rcr.getConceptCode();
	         }
                 //name = rcr.getConceptCode();
                 reindex_required = true;
            }  else {
                 name = rcr.getEntityDescription().getContent();
            }
        }
       
        if (rcr != null && code != null) {
		version = rcr.getCodingSchemeVersion();
		ns = rcr.getCodeNamespace(); 
		
		String vocabulary_name = (String) DataUtils.getFormalName(rcr.getCodingSchemeName());
		if (vocabulary_name == null) {
		    vocabulary_name = (String) hmap.get(rcr.getCodingSchemeName());
		}

		String short_vocabulary_name = null;
		if (name_hmap.containsKey(vocabulary_name)) {
	      		short_vocabulary_name = (String) name_hmap.get(vocabulary_name);
		} else {
	      		//short_vocabulary_name = DataUtils.getMetadataValue(vocabulary_name, version, "display_name");
	      	        short_vocabulary_name = DataUtils.getCSName(vocabulary_name);
	      	        if (short_vocabulary_name == null || short_vocabulary_name.compareTo("null") == 0) {
		  		short_vocabulary_name = DataUtils.getLocalName(vocabulary_name);
	      		}
	      		name_hmap.put(vocabulary_name, short_vocabulary_name);
        	}
      
        
		String version_parameter = "";
		if (version != null && version.length() > 0) {
                	version_parameter = "&version=" + version;
                }
                
		String version_parameter_display = DataUtils.getMetadataValue(vocabulary_name, version, "term_browser_version");
        	
		if (version_parameter_display != null && version_parameter_display.length() > 0)
		    version_parameter_display = " (" + version_parameter_display + ")";
		else {
		    version_parameter_display = "";
		}

           if (code.indexOf("@") != -1 && name.compareTo("") == 0) {

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
		      <td class="dataCellText">
			 <%=short_vocabulary_name%><%=version_parameter_display%>
		      </td>
		    </tr>
		    
           <%

           } else if (code.indexOf("@") != -1 && name.compareTo("") != 0) {

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
			 <%=name%>
		      </td>
		      <td class="dataCellText">
			 <%=short_vocabulary_name%><%=version_parameter_display%>
		      </td>
		    </tr>
		    
            <%
            } else {

            //String con_status = DataUtils.getConceptStatus(vocabulary_name, version, null, code);
            String con_status = null;
            if (concept_status_hmap != null) {
                 con_status = (String) concept_status_hmap.get(rcr.getCodingSchemeName() + "$" + rcr.getCodingSchemeVersion()
                              + "$" + code);
            }
            
            if (con_status != null) {
                con_status = con_status.replaceAll("_", " ");
            }

            String vocabulary_name_encoded = null;
            String short_name = (String) name_hmap.get(vocabulary_name);
            if (short_name != null) {
                 vocabulary_name_encoded = DataUtils.getCSName(short_name);//short_name.replace(" ", "%20");
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
          if (vocabulary_name.compareToIgnoreCase("NCI Thesaurus") == 0) {
          %>
               <a href="<%=request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=vocabulary_name_encoded%><%=version_parameter%>&code=<%=code%>&ns=<%=ns%>&key=<%=itr_key%>&m=1&b=1&n=<%=page_number%>"><%=name%></a>
          <%
          } else if (vocabulary_name.compareToIgnoreCase("NCI MetaThesaurus") == 0) {
               String meta_url = _ncimUrl + "/ConceptReport.jsp?dictionary=NCI%20MetaThesaurus&code=" + code;
          %>
               <a href="javascript:openQuickLinkSite('<%=meta_url%>')"><%=name%></a>
          <%
          } else {
          %>
               <a href="<%=request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=vocabulary_name_encoded%><%=version_parameter%>&code=<%=code%>&ns=<%=ns%>&key=<%=itr_key%>&m=1&b=1&n=<%=page_number%>" ><%=name%></a>
          <%
          }
          %>
          </td>
          <td class="dataCellText">
            <%=short_vocabulary_name%><%=version_parameter_display%>
          </td>


          <%
          } else {
          %>

          <td class="dataCellText" scope="row">
          <%
          if (vocabulary_name.compareToIgnoreCase("NCI Thesaurus") == 0) {
          %>
               <a href="<%=request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=vocabulary_name_encoded%><%=version_parameter%>&code=<%=code%>&ns=<%=ns%>&key=<%=itr_key%>&m=1&b=1&n=<%=page_number%>" ><%=name%></a>&nbsp;(<%=con_status%>)
          <%
          } else if (vocabulary_name.compareToIgnoreCase("NCI MetaThesaurus") == 0) {
               String meta_url = _ncimUrl + "/ConceptReport.jsp?dictionary=NCI%20MetaThesaurus&code=" + code;
          %>
               <a href="javascript:openQuickLinkSite('<%=meta_url%>')"><%=name%></a>&nbsp;(<%=con_status%>)
          <%
          } else {
          %>
               <a href="<%=request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=vocabulary_name_encoded%><%=version_parameter%>&code=<%=code%>&ns=<%=ns%>&key=<%=itr_key%>&m=1&b=1&n=<%=page_number%>" ><%=name%></a>&nbsp;(<%=con_status%>)
          <%
          }
          %>
          </td>
          <td class="dataCellText">
            <%=short_vocabulary_name%><%=version_parameter_display%>
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
        </table>

<%
    if (reindex_required) {
%>
      <p class="textbodyred">WARNING: Lucene index may have not been setup properly.</p>
<%
    } 
%>

    
<%        
        if (size > 0) {
%>        
        	<%@ include file="/pages/templates/pagination-termbrowser.jsp" %>
<%        
        } else if (message != null) {
%>        
            <p class="textbodyred"><%=message%></p>
<%        
        } else {
%>        
            <p class="textbodyred">No match found.</p>
<%          
        }
%>        
  
  
  
  
        <%@ include file="/pages/templates/nciFooter.jsp" %>
      </div> <!-- end Page content -->
    </div> <!-- end main-area_960 -->    
    <div class="mainbox-bottom"><img src="<%=basePath%>/images/mainbox-bottom.gif" width="941" height="5" alt="Mainbox Bottom" /></div>
    <!-- end Main box -->
  </div> <!-- end center-page_960 -->

<%

delay = System.currentTimeMillis() - ms;
_logger.debug("Total page rendering delay (millisec.): " + delay);

%>
</f:view>
<br/>
</body>
</html>
