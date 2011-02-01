<!-- Thesaurus, banner search area -->

<%
    String vsdUri = (String) request.getSession().getAttribute("selectedvalueset");
    if (vsdUri == null) vsdUri = (String) request.getParameter("selectedvalueset");
    if (vsdUri == null) vsdUri = (String) request.getParameter("uri");
    
    if (vsdUri == null) vsdUri = (String) request.getParameter("vsd_uri");
    if (vsdUri == null) vsdUri = (String) request.getSession().getAttribute("vsd_uri");
    
    
    request.getSession().setAttribute("nav_type", "valuesets");
    
    
%>

<div class="bannerarea">
    <div class="banner">
	    <a class="vocabularynamebanner" href="<%=request.getContextPath()%>/pages/resolve_value_set.jsf?uri=<%=HTTPUtils.cleanXSS(vsdUri)%>">
		<div class="vocabularynamebanner">
		  <div class="vocabularynameshort" STYLE="font-size: <%=HTTPUtils.maxFontSize(vsdUri)%>px; font-family : Arial">
		    <%=HTTPUtils.cleanXSS(vsdUri)%>
		  </div>
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
            <%@ include file="menuBar-termbrowser.jsp" %>
        <!-- end Global Navigation -->
    </div>
    
</div>

<!-- end Thesaurus, banner search area -->
<!-- Quick links bar -->
<%@ include file="quickLink.jsp" %>


