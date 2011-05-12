<!-- Thesaurus, banner search area -->
<%@ page import="org.LexGrid.valueSets.ValueSetDefinition" %>


<%

    String vsdUri = null;
    if (vsdUri == null) vsdUri = (String) request.getParameter("uri");
    if (vsdUri == null) vsdUri = (String) request.getParameter("vsd_uri");
    if (vsdUri == null) vsdUri = (String) request.getSession().getAttribute("vsd_uri");
    
String vsd_name = null;  
System.out.println("content-header-resolvedvalueset.jsp vsdUri: " + vsdUri);


ValueSetDefinition selected_vsd = (ValueSetDefinition) request.getSession().getAttribute("selected_vsd");

if (vsdUri != null && vsdUri.compareTo("null") != 0 && vsdUri.indexOf("|") != -1) {

System.out.println("(1) vsdUri: " + vsdUri);

    Vector w = DataUtils.parseData(vsdUri);
    
    for (int k=0; k<w.size(); k++) {
       String t = (String) w.elementAt(k);
       System.out.println("(" + k + ") " + t);
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
    
System.out.println("(2) metadata: " + metadata);    
    
    Vector metadata_vec = DataUtils.parseData(metadata);
    
    
    for (int k=0; k<metadata_vec.size(); k++) {
       String t = (String) metadata_vec.elementAt(k);
       System.out.println("(" + k + ") " + t);
    }
    vsd_name = (String) metadata_vec.elementAt(0);
    request.getSession().setAttribute("vsd_uri", vsdUri); 
}
 
System.out.println("vsd_name: " + vsd_name);
System.out.println("vsdUri: " + vsdUri);

  
%>

<div class="bannerarea">
    <div class="banner">
	    <a class="vocabularynamebanner" href="<%=request.getContextPath()%>/pages/value_set_search_results.jsf?vsd_uri=<%=HTTPUtils.cleanXSS(vsdUri)%>">
      
	<div class="vocabularynamebanner">
	
<%
if (vsd_name.length() < HTTPUtils.ABS_MAX_STR_LEN) {
%>
	
		  <div class="vocabularynameshort" STYLE="font-size: <%=HTTPUtils.maxFontSize(vsd_name)%>px; font-family : Arial">
		    <%=HTTPUtils.cleanXSS(vsd_name)%>
		  </div>
<%		  
} else {

System.out.println("Using small font.");
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
	
    <div class="search-globalnav">
        <!-- Search box -->
        <div class="searchbox-top"><img src="<%=basePath%>/images/searchbox-top.gif" width="352" height="2" alt="SearchBox Top" /></div>
        <div class="searchbox"><%@ include file="/pages/templates/searchForm-resolvedvalueset.jsp" %></div>
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


