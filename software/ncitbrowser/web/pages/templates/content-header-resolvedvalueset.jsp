<!-- Thesaurus, banner search area -->
<%@ page import="org.LexGrid.valueSets.ValueSetDefinition" %>
<%@ page import="gov.nih.nci.evs.browser.utils.*" %>

<%

    String vsdUri = null;
    if (vsdUri == null) vsdUri = HTTPUtils.cleanXSS((String) request.getParameter("uri"));
    if (vsdUri == null) vsdUri = HTTPUtils.cleanXSS((String) request.getParameter("vsd_uri"));
    if (vsdUri == null) vsdUri = (String) request.getSession().getAttribute("vsd_uri");
    
String vsd_name = null;  

ValueSetDefinition selected_vsd = (ValueSetDefinition) request.getSession().getAttribute("selected_vsd");

if (vsdUri != null && vsdUri.compareTo("null") != 0 && vsdUri.indexOf("|") != -1) {

    Vector w = StringUtils.parseData(vsdUri);
    
    for (int k=0; k<w.size(); k++) {
       String t = (String) w.elementAt(k);
    }    
    
    vsdUri = (String) w.elementAt(1);
    vsd_name = (String) w.elementAt(0);
    request.getSession().setAttribute("vsd_uri", vsdUri); 

} else if (selected_vsd != null) {

    vsd_name = selected_vsd.getValueSetDefinitionName();
    vsdUri = selected_vsd.getValueSetDefinitionURI();
    request.getSession().setAttribute("vsd_uri", vsdUri); 

    
} else if (vsdUri != null) {
    String metadata = DataUtils.getValueSetDefinitionMetadata(vsdUri);
    
    Vector metadata_vec = StringUtils.parseData(metadata);

    vsd_name = (String) metadata_vec.elementAt(0);
    request.getSession().setAttribute("vsd_uri", vsdUri); 
}
 

  
%>

<div class="bannerarea_960">
    <div class="banner">
	    <a class="vocabularynamebanner" href="<%=request.getContextPath()%>/ajax?action=create_src_vs_tree&vsd_uri=<%=HTTPUtils.cleanXSS(vsdUri)%>">
     
	<div class="vocabularynamebanner">
	
<%
if (vsd_name == null) vsd_name = "Not specified";
if (vsd_name.length() < HTTPUtils.ABS_MAX_STR_LEN) {
%>
	
		  <div class="vocabularynameshort" STYLE="font-size: <%=HTTPUtils.maxFontSize(vsd_name)%>px; font-family : Arial">
		    <%=HTTPUtils.cleanXSS(vsd_name)%>
		  </div>
<%		  
} else {


%>


		  <div class="vocabularynameshort" STYLE="font-size: <%=HTTPUtils.maxFontSize(vsd_name)%>px; font-family : Arial;">
		    <%=HTTPUtils.cleanXSS(vsd_name)%>
		  </div>

<%
}
%>
		  
		  
		  
	</div>
  
	    </a>
    

    </div>
	
    <div class="search-globalnav_960">
        <!-- Search box -->
        <div class="searchbox-top"><img src="<%=basePath%>/images/searchbox-top.gif" width="352" height="2" alt="SearchBox Top" /></div>
        
        
        
        <div class="searchbox"><%@ include file="/pages/templates/searchForm-downloadedvalueset.jsp" %></div>
        
        
        
        
        <div class="searchbox-bottom"><img src="<%=basePath%>/images/searchbox-bottom.gif" width="352" height="2" alt="SearchBox Bottom" /></div>
        <!-- end Search box -->
        <!-- Global Navigation -->
            <%@ include file="menuBar-termbrowserhome.jsp" %>
        <!-- end Global Navigation -->
    </div>
    
</div>

<!-- end Thesaurus, banner search area -->
<!-- Quick links bar -->
<%@ include file="quickLink.jsp" %>


