<%@ page import="gov.nih.nci.evs.browser.utils.DataUtils"%>
<%@ page import="gov.nih.nci.evs.browser.utils.HTTPUtils" %>
<%@ page import="gov.nih.nci.evs.browser.utils.JSPUtils" %>
<!-- Thesaurus, banner search area -->
<div class="bannerarea">
<%
  JSPUtils.JSPHeaderInfoMore info3 = new JSPUtils.JSPHeaderInfoMore(request);
  if (JSPUtils.isNull(info3.dictionary)) {
      %>
      <div class="banner">
        <a href="<%=basePath%>/start.jsf"><img
          src="<%=basePath%>/images/evs_termsbrowser_logo.gif" width="383"
          height="117" alt="Term Browser Logo" border="0" />
        </a>
      </div>
    <%
  } else if (info3.dictionary.compareTo("NCI Thesaurus") == 0) {
  %>
    <div class="banner">
      <a href="<%=basePath%>"><img
        src="<%=basePath%>/images/thesaurus_browser_logo.jpg" width="383"
        height="117" alt="Thesaurus Browser Logo" border="0" />
      </a>
    </div>
  <%
  } else {
  %>
    <a class="vocabularynamebanner" href="<%=request.getContextPath()%>/pages/vocabulary.jsf?dictionary=<%=HTTPUtils.cleanXSS(info3.dictionary)%>">
      <div class="vocabularynamebanner">
          <div class="vocabularynameshort" STYLE="font-size: <%=HTTPUtils.maxFontSize(info3.display_name)%>px; font-family : Arial">
            <%=HTTPUtils.cleanXSS(info3.display_name)%>
          </div>
          <div class="vocabularynamelong">Version:&nbsp;<%=HTTPUtils.cleanXSS(info3.term_browser_version)%>
          </div>
       </div>
    </a>
  <%
  }
%>

<% if (! JSPUtils.isNull(info3.dictionary)) { %>
  <div class="search-globalnav"><!-- Search box -->
    <div class="searchbox-top"><img
      src="<%=basePath%>/images/searchbox-top.gif" width="352" height="2"
      alt="SearchBox Top" /></div>
    <div class="searchbox"><%@ include file="searchForm.jsp"%></div>
    <div class="searchbox-bottom"><img
      src="<%=basePath%>/images/searchbox-bottom.gif" width="352" height="2"
      alt="SearchBox Bottom" />
    </div>
    <!-- end Search box --> <!-- Global Navigation --> 
    <%@ include file="menuBar.jsp"%> 
    <!-- end Global Navigation -->
  </div>
<% } %>
</div>
<!-- end Thesaurus, banner search area -->
<!-- Quick links bar -->
<%@ include file="quickLink.jsp"%>
<!-- end Quick links bar -->