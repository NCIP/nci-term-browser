<%@ page import="gov.nih.nci.evs.browser.utils.*" %>
<!-- Thesaurus, banner search area -->
<%
  JSPUtils.JSPHeaderInfoMore info4 = new JSPUtils.JSPHeaderInfoMore(request);
  String nciturl = request.getContextPath() + "/pages/home.jsf" + "?version=" + info4.version;
%>
<div class="bannerarea">
    <a href="<%=nciturl%>" style="text-decoration: none;">
      <div class="vocabularynamebanner_ncit">
      
      

<%              
String content_header_dictionary = HTTPUtils.cleanXSS(info4.dictionary);
String content_header_version = HTTPUtils.cleanXSS(info4.version);

String release_date = DataUtils.getVersionReleaseDate(content_header_dictionary, content_header_version);
boolean display_release_date = true;
if (release_date == null || release_date.compareTo("") == 0) {
    display_release_date = false;
}
if (display_release_date) {
%>
    <span class="vocabularynamelong">Version: <%=HTTPUtils.cleanXSS(info4.term_browser_version)%> (Release date: <%=release_date%>)</span>
<%
} else {
%>
    <span class="vocabularynamelong_ncit">Version:&nbsp;<%=HTTPUtils.cleanXSS(info4.term_browser_version)%></span>
<%
}
%> 
        
         
      </div>
    </a> 
    
    <div class="search-globalnav">
        <!-- Search box -->
        <div class="searchbox-top"><img src="<%=basePath%>/images/searchbox-top.gif" width="352" height="2" alt="SearchBox Top" /></div>
        <div class="searchbox"><%@ include file="searchForm.jsp" %></div>
        <div class="searchbox-bottom"><img src="<%=basePath%>/images/searchbox-bottom.gif" width="352" height="2" alt="SearchBox Bottom" /></div>
        <!-- end Search box -->
        <!-- Global Navigation -->
            <%@ include file="menuBar.jsp" %>
        <!-- end Global Navigation -->
    </div>
</div>
<!-- end Thesaurus, banner search area -->
<!-- Quick links bar -->
<%@ include file="quickLink.jsp" %>
<!-- end Quick links bar -->