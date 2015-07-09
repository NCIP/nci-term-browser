<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="org.LexGrid.concepts.*"%>
<%@ page import="gov.nih.nci.evs.browser.utils.*"%>
<%@ page import="gov.nih.nci.evs.browser.properties.*"%>
<%@ page import="gov.nih.nci.evs.browser.bean.*"%>

<%@ page import="org.LexGrid.LexBIG.LexBIGService.LexBIGService"%>

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
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
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
JSPUtils.JSPHeaderInfoMore info = new JSPUtils.JSPHeaderInfoMore(request);
String vocabulary_version = info.version;
LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
MetadataUtils metadataUtils = new MetadataUtils(lbSvc);

        String menubar_scheme = null;
        String menubar_scheme0 = null;
        String menubar_version = null;
        String download_site = null;
        String voc_description = null;
        String voc_version = null;
        Vector v = null;

        /* ------------------------ */

        String scheme = info.dictionary;
        
        
        
        String vocabulary_home_str = HTTPUtils.cleanXSS((String) request.getParameter("home"));
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


  boolean hasValueSet = DataUtils.getValueSetHierarchy().hasValueSet(scheme);
  boolean hasMapping = DataUtils.hasMapping(scheme);


request.getSession().removeAttribute("n");
request.getSession().removeAttribute("b");
request.getSession().removeAttribute("m");


 boolean tree_access_allowed = true;
 if (DataUtils.get_vocabulariesWithoutTreeAccessHashSet().contains(scheme)) {
     tree_access_allowed = false;
 }
 boolean vocabulary_isMapping = DataUtils.isMapping(scheme, null);
 
        String version = info.version;
        String term_browser_version = info.term_browser_version;
        String display_name = info.display_name;

        //if (scheme != null && scheme == null) {
        if (scheme != null) {
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

        /* ------------------------ */

        v = DataUtils.getMetadataNameValuePairs(scheme, version, null);
        Vector u1 = metadataUtils.getMetadataValues(v, 
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
          u1 = metadataUtils.getMetadataValues(v, "description");
          if (u1 != null && u1.size() > 0) {
            voc_description = (String) u1.elementAt(0);
            if (voc_description == null
                || voc_description.compareTo("") == 0
                || voc_description.compareTo("null") == 0) {
              voc_description = "";
            }
          }
        }
        Vector u2 = metadataUtils.getMetadataValues(v, "version");
        voc_version = "";
        if (u2 != null && u2.size() > 0) {
          voc_version = (String) u2.elementAt(0);
        }
        if (voc_version.compareTo("") == 0)
          voc_version = version;

        Vector u3 = metadataUtils.getMetadataValues(v,
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
      <div class="center-page_960">
        <%@ include file="/pages/templates/sub-header.jsp"%> <!-- Main box -->
        <div id="main-area_960">
        <%
       
        
          if (LicenseUtils.isLicensedAndNotAccepted(request, scheme, version)) {
            LicenseUtils.WebPageHelper helper = new LicenseUtils.WebPageHelper(scheme, version);
        %>
            <!-- Thesaurus, banner search area -->
            <div class="bannerarea_960">
			  <a href="<%=basePath%>/start.jsf" style="text-decoration: none;">
			    <div class="vocabularynamebanner_tb">
			      <span class="vocabularynamelong_tb"><%=JSPUtils.getApplicationVersionDisplay()%></span>
			    </div>
			  </a>
            </div>
            <!-- end Thesaurus, banner search area -->
            <!-- Quick links bar -->
            <%@ include file="/pages/templates/quickLink.jsp" %>
            <!-- end Quick links bar -->
            <div class="pagecontent">
              <a name="evs-content" id="evs-content"></a>
              <p><%= helper.getReviewAndAcceptMessage() %></p>
              <textarea cols="87" rows="15" readonly align="left"><%= helper.getLicenseMessages(87) %></textarea>
              <p><%= helper.getButtonMessage() %></p>
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
              <%@ include file="/pages/templates/nciFooter.jsp" %>
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

              <div class="bannerarea_960">
<%
if (DataUtils.isNCIT(dictionary) || DataUtils.isNCIT(scheme)) {    
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
                      
<%
String release_date = DataUtils.getVersionReleaseDate(scheme, version);
boolean display_release_date = true;
if (release_date == null || release_date.compareTo("") == 0) {
    display_release_date = false;
}
if (display_release_date) {
%>
    <div class="vocabularynamelong">Version: <%=HTTPUtils.cleanXSS(term_browser_version)%> (Release date: <%=release_date%>)</div>
<%
} else {
%>
    <div class="vocabularynamelong">Version: <%=HTTPUtils.cleanXSS(term_browser_version)%></div>
<%
}
%>                    
                      
                    </div>
                  </a>
<%
}
%>

                <div class="search-globalnav_960">
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
                         <% Boolean[] isPipeDisplayed = new Boolean[] { Boolean.FALSE }; %>
                         <% if (vocabulary_isMapping) { %>
                              <%= JSPUtils.getPipeSeparator(isPipeDisplayed) %>
                              <a href="<%=request.getContextPath() %>/pages/mapping.jsf?dictionary=<%=HTTPUtils.cleanXSS(menubar_scheme)%>&version=<%=menubar_version%>">
                                Mapping
                              </a>
                         <% } else if (tree_access_allowed) { %>
                              <%= JSPUtils.getPipeSeparator(isPipeDisplayed) %>
                              <a href="#" onclick="javascript:window.open('<%=request.getContextPath()%>/pages/hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(menubar_scheme)%>&version=<%=HTTPUtils.cleanXSS(menubar_version)%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');" tabindex="12">
                                Hierarchy </a>
                         <% } %>       
                                
                                
      <% if (hasValueSet) { %>
        <%= JSPUtils.getPipeSeparator(isPipeDisplayed) %>
        <!--
        <a href="<%= request.getContextPath() %>/pages/value_set_hierarchy.jsf?dictionary=<%=HTTPUtils.cleanXSS(menubar_scheme)%>&version=<%=HTTPUtils.cleanXSS(menubar_version)%>" tabindex="15">Value Sets</a>
        -->
        <a href="<%= request.getContextPath() %>/ajax?action=create_cs_vs_tree&dictionary=<%=HTTPUtils.cleanXSS(menubar_scheme)%>&version=<%=HTTPUtils.cleanXSS(menubar_version)%>" tabindex="15">Value Sets</a>


      <% } %>
      
      <% if (hasMapping) { %>
          <%= JSPUtils.getPipeSeparator(isPipeDisplayed) %>
          <a href="<%= request.getContextPath() %>/pages/cs_mappings.jsf?dictionary=<%=HTTPUtils.cleanXSS(menubar_scheme)%>&version=<%=HTTPUtils.cleanXSS(menubar_version)%>" tabindex="15">Maps</a>      
      <% } %>                                
                                

                         
                         <c:choose>   
                           <c:when test="${sessionScope.CartActionBean.count>0}">
                             <%= JSPUtils.getPipeSeparator(isPipeDisplayed) %>
                             <a href="<%= request.getContextPath() %>/pages/cart.jsf" tabindex="14">Cart</a>
                           </c:when>
                         </c:choose> 

                         <%= VisitedConceptUtils.getDisplayLink(request, isPipeDisplayed) %>
                      </td>
                      <td align="right">
                        <a href="<%=request.getContextPath()%>/pages/help.jsf" tabindex="16">Help</a>
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
                <%@ include file="/pages/templates/nciFooter.jsp" %>
              </div>
        <% } %>
        </div><!-- end main-area_960 -->
      </div><!-- end center-page_960 -->
      <div class="mainbox-bottom"><img src="<%=basePath%>/images/mainbox-bottom.gif" width="945" height="5" alt="Mainbox Bottom" /></div>
    </f:view>
    <br/>
  </body>
</html>
