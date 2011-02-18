<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="java.util.Vector"%>
<%@ page import="java.util.List"%>
<%@ page import="org.LexGrid.concepts.Entity"%>
<%@ page import="gov.nih.nci.evs.browser.utils.DataUtils"%>
<%@ page import="gov.nih.nci.evs.browser.utils.HTTPUtils" %>
<%@ page import="gov.nih.nci.evs.browser.utils.MetadataUtils"%>

<%@ page
  import="gov.nih.nci.evs.browser.properties.NCItBrowserProperties"%>
<%@ page import="gov.nih.nci.evs.browser.bean.MetadataElement"%>
<%@ page import="gov.nih.nci.evs.browser.bean.LicenseBean"%>
<%
  String ncit_build_info = new DataUtils().getNCITBuildInfo();
  String application_version = new DataUtils().getApplicationVersion();
  String anthill_build_tag_built = new DataUtils().getNCITAnthillBuildTagBuilt();
  String evs_service_url = new DataUtils().getEVSServiceURL();
%>
<!--
   Build info: <%=ncit_build_info%>
 Version info: <%=application_version%>
          Tag: <%=anthill_build_tag_built%>
   LexEVS URL: <%=evs_service_url%>
  -->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns:c="http://java.sun.com/jsp/jstl/core">
  <head>
    <title>NCI Term Browser</title>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
    <link rel="stylesheet" type="text/css"
      href="<%=request.getContextPath()%>/css/styleSheet.css" />
    <script type="text/javascript"
      src="<%=request.getContextPath()%>/js/script.js"></script>
    <script type="text/javascript"
      src="<%=request.getContextPath()%>/js/search.js"></script>
    <script type="text/javascript"
      src="<%=request.getContextPath()%>/js/dropdown.js"></script>
  </head>
  <body onLoad="document.forms.searchTerm.matchText.focus();">
    <script type="text/javascript"
      src="<%=request.getContextPath()%>/js/wz_tooltip.js"></script>
    <script type="text/javascript"
      src="<%=request.getContextPath()%>/js/tip_centerwindow.js"></script>
    <script type="text/javascript"
      src="<%=request.getContextPath()%>/js/tip_followscroll.js"></script>
    <f:view>
    <!-- Begin Skip Top Navigation -->
      <a href="#evs-content" class="hideLink" accesskey="1" title="Skip repetitive navigation links">skip navigation links</A>
    <!-- End Skip Top Navigation -->     
      <%


String vocabulary_version = request.getParameter("version");
//System.out.println("(***** vocabulary.jsp) vocabulary_version: " + vocabulary_version);
if (vocabulary_version != null) {
  request.setAttribute("version", vocabulary_version);
}



        String menubar_scheme = null;
        String menubar_scheme0 = null;
        String menubar_version = null;
        String download_site = null;
        String voc_description = null;
        String voc_version = null;
        Vector v = null;

        /* ------------------------ */

        String scheme = (String) request.getParameter("dictionary");
        String vocabulary_home_str = (String) request.getParameter("home");
        String shortName = null;
//KLO, testing
if (scheme != null) {
        scheme = DataUtils.getFormalName(scheme);
        scheme = DataUtils.searchFormalName(scheme);
        shortName = DataUtils.getLocalName(scheme);
}

        String dictionary = null;
        if (scheme == null) {
            scheme = (String) request.getSession().getAttribute("scheme");
        }

 boolean tree_access_allowed = true;
 if (DataUtils._vocabulariesWithoutTreeAccessHashSet.contains(scheme)) {
     tree_access_allowed = false;
 }
 boolean vocabulary_isMapping = DataUtils.isMapping(scheme, null);
 //System.out.println("vocabulary_isMapping: " + vocabulary_isMapping);

        String version = (String) request.getParameter("version");
        if (version == null) {
            version = (String) request.getSession().getAttribute("version");
        }

        String term_browser_version = DataUtils.getMetadataValue(scheme, version, "term_browser_version");


        if (term_browser_version == null || term_browser_version.compareTo("null") == 0) term_browser_version = version;//"N/A";

        String display_name = DataUtils.getMetadataValue(scheme, version, "display_name");

        if (display_name == null || display_name.compareTo("null") == 0) display_name = shortName;


        if (scheme != null && scheme == null) {
          if (version != null) {
            dictionary = scheme + " (version" + version + ")";
            version = version.replaceAll("%20", " ");
          }
        }


        request.getSession().setAttribute("dictionary", scheme);

        menubar_scheme = scheme;
        //KLO tesing
        if (menubar_scheme != null) {
            menubar_scheme = DataUtils.getFormalName(menubar_scheme);
        }

        menubar_version = version;
        menubar_scheme0 = menubar_scheme;

        boolean isLicensed = LicenseBean.isLicensed(scheme, version);
        LicenseBean licenseBean = (LicenseBean) request.getSession()
            .getAttribute("licenseBean");
        if (licenseBean == null) {
          licenseBean = new LicenseBean();
          request.getSession().setAttribute("licenseBean",
              licenseBean);
        }
        boolean accepted = licenseBean.licenseAgreementAccepted(scheme);

        /* ------------------------ */

        v = MetadataUtils.getMetadataNameValuePairs(scheme, version, null);
        Vector u1 = MetadataUtils.getMetadataValues(v, 
            "html_compatable_description");
        voc_description = scheme;
        if (u1 != null && u1.size() > 0) {
          voc_description = (String) u1.elementAt(0);
          if (voc_description == null
              || voc_description.compareTo("") == 0
              || voc_description.compareTo("null") == 0) {
            voc_description = "";
          }
        } else {
          u1 = MetadataUtils.getMetadataValues(v, "description");
          if (u1 != null && u1.size() > 0) {
            voc_description = (String) u1.elementAt(0);
            if (voc_description == null
                || voc_description.compareTo("") == 0
                || voc_description.compareTo("null") == 0) {
              voc_description = "";
            }
          }
        }
        Vector u2 = MetadataUtils.getMetadataValues(v, "version");
        voc_version = "";
        if (u2 != null && u2.size() > 0) {
          voc_version = (String) u2.elementAt(0);
        }
        if (voc_version.compareTo("") == 0)
          voc_version = version;

        Vector u3 = MetadataUtils.getMetadataValues(v,
            "download_url");
        if (u3 != null && u3.size() > 0) {
          download_site = (String) u3.elementAt(0);
        }

        if (menubar_scheme != null) {
          menubar_scheme = menubar_scheme.replaceAll(" ", "%20");
        }
        if (menubar_version != null) {
          menubar_version = menubar_version
              .replaceAll(" ", "%20");
        }

      %>
      <%@ include file="/pages/templates/header.jsp" %>
      <div class="center-page">
        <%@ include file="/pages/templates/sub-header.jsp"%> <!-- Main box -->
        <div id="main-area">
        <%
          if (isLicensed && !accepted) {
            String licenseStmt = LicenseBean.resolveCodingSchemeCopyright(scheme, version);
        %>
            <!-- Thesaurus, banner search area -->
            <div class="bannerarea">
              <div class="banner"><a href="<%=basePath%>">
                <img src="<%=basePath%>/images/evs_termsbrowser_logo.gif" width="383" height="117" border="0"/>
              </div>
            </div>
            <!-- end Thesaurus, banner search area -->
            <!-- Quick links bar -->
            <%@ include file="/pages/templates/quickLink.jsp" %>
            <!-- end Quick links bar -->
            <div class="pagecontent">
            <a name="evs-content" id="evs-content"></a>
        <p>
          To access <b><%=HTTPUtils.cleanXSS(display_name)%></b>, please review and accept the copyright/license statement below:
        </p>

              <textarea cols="87" rows="15" readonly align="left"><%=licenseStmt%></textarea>
              <p>If and only if you agree to these terms and conditions, click the
              Accept button to proceed.</p>
              <p>
              <h:form>
                <h:commandButton id="accept" value="Accept"
                action="#{userSessionBean.acceptLicenseAgreement}"
                image="/images/accept.gif"
                alt="Accept">
              </h:commandButton> &nbsp;&nbsp; <img
                src="<%=request.getContextPath()%>/images/cancel.gif" name="cancel"
                alt="reset" onClick="history.back()" />
                <input type="hidden" id="dictionary" name="dictionary" value="<%=HTTPUtils.cleanXSS(scheme)%>" />
                <input type="hidden" id="version" name="version" value="<%=HTTPUtils.cleanXSS(version)%>" /></h:form>
              </p>
              <%@ include file="/pages/templates/nciFooter.html" %>
            </div>
        <% } else {
              if (scheme != null) {
                request.setAttribute("scheme", scheme);
              }
              if (version != null) {
                request.setAttribute("version", version);
              }
              if (dictionary != null) {
                request.setAttribute("dictionary", dictionary);
              }
        %>
              <!-- Thesaurus, banner search area -->

              <div class="bannerarea">
<%

//System.out.println("vocabulary.jsp basePath " + basePath);

if ((dictionary != null && dictionary.compareTo("NCI Thesaurus") == 0) ||
    (scheme != null && scheme.compareTo("NCI Thesaurus") == 0)
    ) {
%>
    <div class="banner"><a href="<%=basePath%>"><img src="<%=basePath%>/images/thesaurus_browser_logo.jpg" width="383" height="117" alt="Thesaurus Browser Logo" border="0"/></a></div>
<%
} else {
%>

                <% if (menubar_version == null) { %>
                  <a class="vocabularynamebanner" href="<%=request.getContextPath()%>/pages/vocabulary.jsf?dictionary=<%=HTTPUtils.cleanXSS(menubar_scheme)%>">
                <% } else { %>
                  <a class="vocabularynamebanner" href="<%=request.getContextPath()%>/pages/vocabulary.jsf?dictionary=<%=HTTPUtils.cleanXSS(menubar_scheme)%>&version=<%=HTTPUtils.cleanXSS(menubar_version)%>">
                <% } %>
                    <div class="vocabularynamebanner">
                      <div class="vocabularynameshort" STYLE="font-size: <%=HTTPUtils.maxFontSize(display_name)%>px; font-family : Arial">
                          <%=HTTPUtils.cleanXSS(display_name)%>
                      </div>
                      <div class="vocabularynamelong">Version: <%=HTTPUtils.cleanXSS(term_browser_version)%></div>
                    </div>
                  </a>
<%
}
%>

                <div class="search-globalnav">
                  <!-- Search box -->
                  <div class="searchbox-top"><img
                    src="<%=basePath%>/images/searchbox-top.gif" width="352" height="2"
                    alt="SearchBox Top" /></div>
                  <div class="searchbox"><%@ include
                    file="/pages/templates/searchForm.jsp"%></div>
                  <div class="searchbox-bottom"><img
                    src="<%=basePath%>/images/searchbox-bottom.gif" width="352"
                    height="2" alt="SearchBox Bottom" /></div>
                  <!-- end Search box -->
                  <table class="global-nav" border="0" width="100%" cellpadding="0" cellspacing="0">
                    <tr>
                      <td>
                        <% if (menubar_version == null) { %>
                              <a href="<%=request.getContextPath()%>/pages/vocabulary.jsf?dictionary=<%=HTTPUtils.cleanXSS(menubar_scheme)%>" tabindex="10">Home</a>
                        <% } else { %>
                              <a href="<%=request.getContextPath()%>/pages/vocabulary.jsf?dictionary=<%=HTTPUtils.cleanXSS(menubar_scheme)%>&version=<%=HTTPUtils.cleanXSS(menubar_version)%>" tabindex="10">Home</a>
                        <% }
                           if (download_site != null) {
                        %>    | <a href="#" onclick="javascript:window.open('<%=download_site%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');" tabindex="17">
                              Download </a>
                        <% }
                        %>

         <%
      if (vocabulary_isMapping) {
      %>

      | <a href="<%=request.getContextPath() %>/pages/mapping.jsf?dictionary=<%=HTTPUtils.cleanXSS(menubar_scheme)%>&version=<%=menubar_version%>">
        View Mapping
      </a>

      <%
      }
      else if (tree_access_allowed) {
         %>
                        | <a href="#" onclick="javascript:window.open('<%=request.getContextPath()%>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(menubar_scheme)%>&version=<%=HTTPUtils.cleanXSS(menubar_version)%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');" tabindex="12">
                        View Hierarchy </a>
   	<c:choose>	
		<c:when test="${sessionScope.CartActionBean.count>0}">
			| <a href="<%= request.getContextPath() %>/pages/cart.jsf" tabindex="14">Cart</a>
	    </c:when>
    </c:choose> 
   <% }
       %>

                        <% if (menubar_scheme0 != null && menubar_scheme0.compareTo("NCI Thesaurus") == 0) { %>
                              | <a href="<%=request.getContextPath()%>/pages/subset.jsf" tabindex="15">Subsets</a>
                        <% } %> | <a href="<%=request.getContextPath()%>/pages/help.jsf" tabindex="16">Help</a>
                      </td>
                      <td align="right">
                        <%= VisitedConceptUtils.getDisplayLink(request) %>
                      </td>
                      <td width="7"></td>
                    </tr>
                  </table>
                  <!-- end Global Navigation -->
                </div>
              </div>
              <!-- Quick links bar -->
              <%@ include file="/pages/templates/quickLink.jsp" %>
              <!-- end Quick links bar -->
              <div class="pagecontent">
                <%@ include file="/pages/templates/welcome-other.jsp"%>
                <%@ include file="/pages/templates/nciFooter.html" %>
              </div>
        <% } %>
        </div><!-- end main-area -->
      </div><!-- end center-page -->
      <div class="mainbox-bottom"><img src="<%=basePath%>/images/mainbox-bottom.gif" width="745" height="5" alt="Mainbox Bottom" /></div>
    </f:view>
    <br/>
  </body>
</html>