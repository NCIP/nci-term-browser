<!-- EVS Logo -->
<div>
  <img src="<%=basePath%>/images/evs-logo.gif"
       width="745" height="26" border="0"
       usemap="#external-evs" />
  <map id="external-evs" name="external-evs">
    <area shape="rect" coords="0,0,226,26"
      href="http://evs.nci.nih.gov/" target="_blank"
      alt="Enterprise Vocabulary Services" />
    <area shape="rect" coords="600,0,745,26"
      href="<%= request.getContextPath() %>/start.jsf" target="_self"
      alt="NCI Term Browser" />
  </map>
</div>
<div class="mainbox-top"><img src="<%=basePath%>/images/mainbox-top.gif" width="745" height="5" alt=""/></div>
<!-- end EVS Logo -->