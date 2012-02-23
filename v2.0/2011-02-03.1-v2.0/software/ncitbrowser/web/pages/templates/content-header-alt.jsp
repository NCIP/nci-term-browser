<%@ page import="gov.nih.nci.evs.browser.utils.DataUtils"%>
<%@ page import="gov.nih.nci.evs.browser.utils.HTTPUtils" %>
<!-- Thesaurus, banner search area -->
<div class="bannerarea">
<%
        String hdr_dictionary = (String) request.getSession().getAttribute("dictionary");

        if (hdr_dictionary == null || hdr_dictionary.compareTo("NCI Thesaurus") == 0) {
        %>
<div class="banner"><a href="<%=basePath%>"><img
  src="<%=basePath%>/images/thesaurus_browser_logo.jpg" width="383"
  height="117" alt="Thesaurus Browser Logo" border="0" /></a></div>
<%
        } else if (hdr_dictionary != null) {
            request.getSession().setAttribute("dictionary", hdr_dictionary);
            String content_hdr_shortName = DataUtils.getLocalName(hdr_dictionary);

            String content_hdr_formalName = DataUtils.getFormalName(content_hdr_shortName);

            String display_name = DataUtils.getMetadataValue(content_hdr_formalName, "display_name");

            String term_browser_version = DataUtils.getMetadataValue(content_hdr_formalName, "term_browser_version");

            if (display_name == null || display_name.compareTo("null") == 0) {
                display_name = content_hdr_shortName;
            }

            if (term_browser_version == null || term_browser_version.compareTo("null") == 0) {
    term_browser_version = (String) request.getParameter("version");
    if (term_browser_version == null) {
        term_browser_version = (String) request.getAttribute("version");
    }
            }

%>
<a class="vocabularynamebanner" href="<%=request.getContextPath()%>/pages/vocabulary.jsf?dictionary=<%=HTTPUtils.cleanXSS(hdr_dictionary)%>">
  <div class="vocabularynamebanner">
    <div class="vocabularynameshort" STYLE="font-size: <%=HTTPUtils.maxFontSize(display_name)%>px; font-family : Arial">
        <%=HTTPUtils.cleanXSS(display_name)%>
    </div>
    <div class="vocabularynamelong">Version:&nbsp;<%=HTTPUtils.cleanXSS(term_browser_version)%></div>
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
              <a href="<%=request.getContextPath()%>/pages/vocabulary.jsf?dictionary=<%=HTTPUtils.cleanXSS(hdr_dictionary)%>">Simple Search</a>
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

