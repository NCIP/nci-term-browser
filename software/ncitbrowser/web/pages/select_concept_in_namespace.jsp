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

<%@ page import="org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator" %>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns:c="http://java.sun.com/jsp/jstl/core">



<%
String sel_dictionary = HTTPUtils.cleanXSS((String) request.getParameter("dictionary"));
%>

<head>
  <title><%=sel_dictionary%></title>
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


  <%@ include file="/pages/templates/header.jsp" %>
  <div class="center-page_960">
    <%@ include file="/pages/templates/sub-header.jsp" %>
    <!-- Main box -->
    <div id="main-area_960">

<%
// forwardPage = jsfPage + "?&dictionary=" + dictionary + version_parameter + "&code=" + code + "&ns=" + ns;
String sel_version = HTTPUtils.cleanXSS((String) request.getParameter("version"));
String sel_code = HTTPUtils.cleanXSS((String) request.getParameter("code"));

String vocabulary_name = sel_dictionary;
String sel_dictionary_nm = DataUtils.getCSName(sel_dictionary);
String short_vocabulary_name = sel_dictionary;
String coding_scheme_version = sel_version;
String ns = null;
String name = null;
String code = null;

 
ResolvedConceptReferencesIterator iterator = (ResolvedConceptReferencesIterator) request.getSession().getAttribute("concepts_in_namespaces");
List list = null;
if (iterator != null) {
     list = new ArrayList();
     while (iterator.hasNext()) {
         ResolvedConceptReference rcr = (ResolvedConceptReference) iterator.next();
         list.add(rcr);
     }
     request.getSession().removeAttribute("concepts_in_namespaces");
}

if (coding_scheme_version != null) {
    request.setAttribute("version", coding_scheme_version);
}

if (DataUtils.isNCIT(vocabulary_name)) {
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
    String message = null;
    if (list.size() == 0) {
        message = "Concept with code " + sel_code + " not found in " + vocabulary_name;    
    }
    if (message != null) {
    %>
      <p class="textbodyred"><%=message%></p>
    <%
    } else {
    %>
        <table width="900px">
          <tr>
            <table>
              <tr>
                <td class="texttitle-blue">Multiple concepts found in <%=sel_dictionary%> with code:</td>
                <td class="texttitle-gray"><%=sel_code%></td>
              </tr>
            </table>
          </tr>
          <tr>
            <td><hr></td>
          </tr>
          
          <tr>
            <td class="textbody">
            
              <table class="dataTable" summary="" cellpadding="3" cellspacing="0" border="0" width="100%">
                <%

HashMap concept_status_hmap = DataUtils.getPropertyValuesInBatch(list, "Concept_Status");
                 
                  int i = -1;
                  for (int k=0; k<list.size(); k++) {
                      Object obj = list.get(k);
                      ResolvedConceptReference rcr = null;
                      if (obj != null) {
			      rcr = (ResolvedConceptReference) obj;
			      code = rcr.getConceptCode();
			      ns = rcr.getCodeNamespace(); 
                     	      coding_scheme_version = rcr.getCodingSchemeVersion();

			      name = "No description available.";
			      if (rcr.getEntityDescription() != null) {
				  name = rcr.getEntityDescription().getContent();
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
             <a href="<%=request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=sel_dictionary_nm%>&version=<%=sel_version%>&ns=<%=ns%>&code=<%=code%>" ><%=DataUtils.encodeTerm(name)%></a>&nbsp;(Namespace: <%=ns%>)
          <%
          } else {
          %>
             <a href="<%=request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=sel_dictionary_nm%>&version=<%=sel_version%>&ns=<%=ns%>&code=<%=code%>" ><%=DataUtils.encodeTerm(name)%></a>&nbsp;(Namespace: <%=ns%>)&nbsp;(<%=con_status%>)
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



       <%@ include file="/pages/templates/nciFooter.jsp" %>
      </div>
      <!-- end Page content -->
    </div>
    <div class="mainbox-bottom"><img src="<%=request.getContextPath()%>/images/mainbox-bottom.gif" width="945" height="5" alt="Mainbox Bottom" /></div>
    <!-- end Main box -->
  </div>
</f:view>
</body>
</html>
