<%
  String application_version_display = HTTPUtils.cleanXSS(new DataUtils().getApplicationVersionDisplay());
%>

<div class="bannerarea">
  <div class="banner">
    <a href="<%=basePath%>/start.jsf">
      <img src="<%=basePath%>/images/evs_termsbrowser_logo.gif"
        width="383" height="117" alt="Thesaurus Browser Logo" border="0"/>
    </a>
    <div class="vocabularynamelong_tb"><%=application_version_display%></div>
  </div>
  <div class="search-globalnav">
    <img src="<%=basePath%>/images/shim.gif"
      width="1" height="80" alt="Shim" border="0"/>
    <%@ include file="menuBar-termbrowserhome.jsp" %>
  </div>
</div>
<%@ include file="quickLink.jsp" %>
