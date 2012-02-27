<!-- Thesaurus, banner search area -->

<div class="bannerarea">
    <div class="banner">
      <a href="<%=basePath%>/start.jsf"><img src="<%=basePath%>/images/evs_termsbrowser_logo.gif" width="383" height="117" alt="Thesaurus Browser Logo" border="0"/></a>
      <div class="vocabularynamelong_tb"><%=JSPUtils.getApplicationVersionDisplay()%></div>
    </div>
    <div class="search-globalnav">
        <!-- Search box -->
        <div class="searchbox-top"><img src="<%=basePath%>/images/searchbox-top.gif" width="352" height="2" alt="SearchBox Top" /></div>
       
        <div class="searchbox"><%@ include file="/pages/templates/searchForm-termbrowser.jsp" %></div>
        
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
<!-- end Quick links bar -->