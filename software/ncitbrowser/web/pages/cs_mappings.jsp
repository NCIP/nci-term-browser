<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=windows-1252"%>
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
  <title>NCI Thesaurus</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
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
  private static Logger _logger = Utils.getJspLogger("cs_mappings.jsp");
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
JSPUtils.JSPHeaderInfo info = new JSPUtils.JSPHeaderInfo(request);
String cs_mappings_dictionary = info.dictionary;

System.out.println("********* cs_mappings.jsp cs_mappings_dictionary: " + cs_mappings_dictionary);


boolean isMapping = DataUtils.isMapping(cs_mappings_dictionary, null);

//System.out.println("isMapping: " + isMapping);

boolean isExtension = DataUtils.isExtension(cs_mappings_dictionary, null);


//System.out.println("isExtension: " + isExtension);


String cs_mappings_version = info.version;

System.out.println("********* cs_mappings.jsp cs_mappings_version: " + cs_mappings_version);


HashMap hmap = DataUtils.getNamespaceId2CodingSchemeFormalNameMapping();
HashMap name_hmap = new HashMap();
String vocabulary_name = null;
String short_vocabulary_name = null;
String coding_scheme_version = null;

String mapping_scheme_and_version = (String) request.getSession().getAttribute("scheme_and_version");
if (mapping_scheme_and_version == null) {
mapping_scheme_and_version = "";
}
  

String key = (String) request.getAttribute("key");
System.out.println("search results.jsp key: " + key);
if (key == null) {
    key = HTTPUtils.cleanXSS((String) request.getParameter("key"));
}

if (cs_mappings_version != null) {
    request.setAttribute("version", cs_mappings_version);
}

if (cs_mappings_dictionary == null || cs_mappings_dictionary.compareTo("NCI Thesaurus") == 0) {
%>

      <%@ include file="/pages/templates/content-header.jsp" %>
<%
} else {
%>
      <%@ include file="/pages/templates/content-header-other.jsp" %>
<%
}


Vector mapping_schemes = DataUtils.getMappingCodingSchemes(cs_mappings_dictionary);

%>
 
<h:form id="mappingSearch" styleClass="search-form" > 
      
        <!-- Page content -->
        <div class="pagecontent">
          <a name="evs-content" id="evs-content"></a>
          
          
          <div class="tabTableContentContainer">

	<%
	String mapping_error_msg = (String) request.getSession().getAttribute("message");
	if (mapping_error_msg != null) {
	    request.getSession().removeAttribute("message");

	      %>
	      <p class="textbodyred"><%=mapping_error_msg%></p>
	      <%

	} 

	String mapping_warning_msg = (String) request.getSession().getAttribute("warning");
	if (mapping_warning_msg != null) {
	    request.getSession().removeAttribute("warning");

	      %>
	      <p class="textbodyred"><%=mapping_warning_msg%></p>
	      <%
	} 
		
             
             String hide_ontology_list = (String) request.getSession().getAttribute("hide_ontology_list");
             request.getSession().removeAttribute("hide_ontology_list");
if (hide_ontology_list == null || hide_ontology_list.compareTo("false") == 0) {
%>
             
           
            <span class="textbody">&nbsp;Select exactly one mapping data sets to perform search, or click on a specific mapping data set name to go to its home page
            and perform search there.
            <br/><br/>
            </span>
            
            
            <table class="termstable" border="0">
  
               
                <tr><td class="textbody">Mappings:</td></tr>
                
                <tr>
                  <td class="textbody">
                    <table border="0" cellpadding="0" cellspacing="0">
                      <%
                      int mapping_cs_knt = 0;
                      for (int i = 0; i < mapping_schemes.size(); i++) {
                        mapping_cs_knt++;
                        String label = (String) mapping_schemes.elementAt(i);
 
		        String scheme = DataUtils.getCodingSchemeName( label );
		        String version = DataUtils.getCodingSchemeVersion( label );

                       
				String http_label = null;
				String http_scheme = null;
				String http_version = null;
				
				boolean checked = false;
				

				String full_name = DataUtils.getMetadataValue(scheme, version, "full_name");
				String display_name = DataUtils.getMetadataValue(scheme, version, "display_name");
				
				if (full_name == null || full_name.compareTo("null") == 0) 
				    full_name = scheme;
				String term_browser_version = DataUtils.getMetadataValue(scheme, version, "term_browser_version");
				if (term_browser_version == null || term_browser_version.compareTo("null") == 0) {
				    term_browser_version = version;
				}     
				String display_label = display_name + ":&nbsp;" + full_name + "&nbsp;(" + term_browser_version + ")";

				if (label != null)
				  http_label = label.replaceAll(" ", "%20");
				if (scheme != null)
				  http_scheme = scheme.replaceAll(" ", "%20");
				if (version != null)
				  http_version = version.replaceAll(" ", "%20");
				%>
				<tr>
				  <td width="25px"></td>
				  
				  <td>
<%
String scheme_and_version_str = scheme + "$" + version;
String checkedStr = "";
if (mapping_scheme_and_version.compareTo("") == 0 && mapping_cs_knt == 1) {
     checkedStr = "checked";
} else if (mapping_scheme_and_version.compareTo(scheme_and_version_str) == 0) {
     checkedStr = "checked";
}
%>

    <input type="radio" name="scheme_and_version" value="<%=scheme%>$<%=version%>" <%=checkedStr%> tabinex="1" />




				    <a href="<%= request.getContextPath() %>/pages/vocabulary.jsf?dictionary=<%=http_scheme%>&version=<%=http_version%>">
				      <%=display_label%>
				    </a>
				  
				  </td>

                               </tr>
			     <%

			   }
			 %>
                    </table>
                  </td>
                </tr> 
           
            </table>

<p></p>

          <table class="termstable" border="0">
                <tr>
                  <td>

                  <h:commandButton id="Search" value="Search"
                    action="#{userSessionBean.searchAction}"
                    image="#{requestContextPath}/images/search.gif"
                    alt="Search">
                  </h:commandButton>
                  
                  </td>
                </tr>
          </table>
          
<%
}
%>

          </div> <!-- end tabTableContentContainer -->
          <%@ include file="/pages/templates/nciFooter.jsp"%>
        </div> <!-- end Page content -->      
      
</h:form>      
      
      
    </div>
    <div class="mainbox-bottom"><img src="images/mainbox-bottom.gif" width="745" height="5" alt="Mainbox Bottom" /></div>
    <!-- end Main box -->
  </div>
</f:view>
</body>
</html>