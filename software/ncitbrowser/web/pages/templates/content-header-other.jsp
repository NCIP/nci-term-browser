<%@ page import="gov.nih.nci.evs.browser.utils.DataUtils"%>
<%@ page import="gov.nih.nci.evs.browser.utils.HTTPUtils" %>
<%@ page import="gov.nih.nci.evs.browser.utils.JSPUtils" %>
<!-- Thesaurus, banner search area -->
<div class="bannerarea">
<%
  JSPUtils.JSPHeaderInfo info = new JSPUtils.JSPHeaderInfo(request);
  if (info.dictionary == null || info.dictionary.compareTo("NCI Thesaurus") == 0) {
  %>
    <div class="banner">
      <a href="<%=basePath%>"><img
        src="<%=basePath%>/images/thesaurus_browser_logo.jpg" width="383"
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
          <div class="vocabularynamelong">Version:&nbsp;<%=HTTPUtils.cleanXSS(info.term_browser_version)%>
          </div>
       </div>
    </a>
  <%
  }
%>
        
<div class="search-globalnav"><!-- Search box -->
<div class="searchbox-top"><img
  src="<%=basePath%>/images/searchbox-top.gif" width="352" height="2"
  alt="SearchBox Top" /></div>
<div class="searchbox"><%@ include file="searchForm.jsp"%></div>
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