<%--L
  Copyright Northrop Grumman Information Technology.

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
L--%>

<div class="bannerarea">
  <a href="<%=basePath%>/start.jsf" style="text-decoration: none;">
    <div class="vocabularynamebanner_tb">
      <span class="vocabularynamelong_tb"><%=JSPUtils.getApplicationVersionDisplay()%></span>
    </div>
  </a>
  <div class="search-globalnav">
    <img src="<%=basePath%>/images/shim.gif"
      width="1" height="80" alt="Shim" border="0"/>
    <%@ include file="menuBar-termbrowserhome.jsp" %>
  </div>
</div>
<%@ include file="quickLink.jsp" %>
