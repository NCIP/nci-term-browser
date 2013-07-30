<%@ page import="gov.nih.nci.evs.browser.utils.DataUtils"%>
<%@ page import="gov.nih.nci.evs.browser.utils.HTTPUtils" %>
<!-- Thesaurus, banner search area -->
<div class="bannerarea">
<%
  JSPUtils.JSPHeaderInfoMore info = new JSPUtils.JSPHeaderInfoMore(request);
  if (info.dictionary == null || info.dictionary.compareTo("NCI Thesaurus") == 0) {
  %>
    <div class="banner">
      <a href="<%=basePath%>"><img src="<%=basePath%>/images/thesaurus_browser_logo.jpg" width="383"
        height="117" alt="Thesaurus Browser Logo" border="0" />
      </a>
    </div>
  <%
  } else if (info.dictionary != null) {
  %>
    <a class="vocabularynamebanner" href="<%=request.getContextPath()%>/pages/vocabulary.jsf?dictionary=<%=HTTPUtils.cleanXSS(info.dictionary)%>">
      <div class="vocabularynamebanner">
        <div class="vocabularynameshort" STYLE="font-size: <%=HTTPUtils.maxFontSize(info.display_name)%>px; font-family : Arial">
            <%=HTTPUtils.cleanXSS(info.display_name)%>
        </div>
        
  
<%              
String content_header_alt_dictionary = HTTPUtils.cleanXSS(info.dictionary);
String content_header_alt_version = HTTPUtils.cleanXSS(info.version);

String release_date = DataUtils.getVersionReleaseDate(content_header_alt_dictionary, content_header_alt_version);
boolean display_release_date = true;
if (release_date == null || release_date.compareTo("") == 0) {
    display_release_date = false;
}
if (display_release_date) {
%>
    <div class="vocabularynamelong">Version: <%=HTTPUtils.cleanXSS(info.term_browser_version)%> (Release date: <%=release_date%>)</div>
<%
} else {
%>
    <div class="vocabularynamelong">Version:&nbsp;<%=HTTPUtils.cleanXSS(info.term_browser_version)%></div>
<%
}
%> 

      </div>
    </a>
  <%
  }
%>
  
<div class="search-globalnav"><!-- Search box -->
<div class="searchbox-top"><img
  src="<%=basePath%>/images/searchbox-top.gif" width="352" height="2"
  alt="SearchBox Top" /></div>


    <div class="searchbox">
        <table border="0" height="90px" width="100%" class="global-nav">
          <tr>
            <td valign="middle" align="center">
              <a href="<%=request.getContextPath()%>/pages/vocabulary.jsf?dictionary=<%=HTTPUtils.cleanXSS(info.dictionary)%>">Simple Search</a>
            </td>
          </tr>
        </table>
    </div>



<div class="searchbox-bottom"><img
  src="<%=basePath%>/images/searchbox-bottom.gif" width="352" height="2"
  alt="SearchBox Bottom" /></div>




<!-- end Search box --> <!-- Global Navigation --> <%@ include
  file="menuBar.jsp"%> <!-- end Global Navigation -->
</div>
</div>
<!-- end Thesaurus, banner search area -->
<!-- Quick links bar -->
<%@ include file="quickLink.jsp"%>
<!-- end Quick links bar -->

