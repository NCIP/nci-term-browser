<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="java.util.Vector"%>
<%@ page import="java.util.List"%>
<%@ page import="org.LexGrid.concepts.Concept"%>
<%@ page import="gov.nih.nci.evs.browser.utils.DataUtils"%>
<%@ page import="gov.nih.nci.evs.browser.utils.MetadataUtils"%>
<%@ page
  import="gov.nih.nci.evs.browser.properties.NCItBrowserProperties"%>
<%@ page import="gov.nih.nci.evs.browser.bean.MetadataElement"%>
<%@ page import="gov.nih.nci.evs.browser.bean.LicenseBean"%>
<%
  String ncit_build_info = new DataUtils().getNCITBuildInfo();
%>
<!-- Build info: <%=ncit_build_info%> -->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
  <head>
    <title>NCI Thesaurus</title>
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
  <body>
    <script type="text/javascript"
      src="<%=request.getContextPath()%>/js/wz_tooltip.js"></script>
    <script type="text/javascript"
      src="<%=request.getContextPath()%>/js/tip_centerwindow.js"></script>
    <script type="text/javascript"
      src="<%=request.getContextPath()%>/js/tip_followscroll.js"></script>
    <f:view>
      <%
        String menubar_scheme = null;
        String menubar_scheme0 = null;
        String menubar_version = null;
        String download_site = null;
        String voc_description = null;
        String voc_version = null;
        Vector v = null;
        Vector metadata_names = new Vector();
        List metadataElementList = NCItBrowserProperties
            .getMetadataElementList();
        for (int i = 0; i < metadataElementList.size(); i++) {
          MetadataElement ele = (MetadataElement) metadataElementList
              .get(i);
          metadata_names.add(ele.getName());
        }
         
        /* ------------------------ */
        
        String scheme = (String) request.getParameter("dictionary");
        String shortName = scheme;
        String dictionary = null;
        if (scheme == null) {
          scheme = (String) request.getAttribute("scheme");
        }
        String version = (String) request.getParameter("version");
        if (version == null) {
          version = (String) request.getAttribute("version");
        }
        if (scheme != null) {
          shortName = new DataUtils().getLocalName(scheme);
        }
        if (scheme != null && scheme == null) {
          if (version != null) {
            dictionary = scheme + " (version" + version + ")";
            version = version.replaceAll("%20", " ");
          }
        }  
        request.getSession().setAttribute("dictionary", scheme);  
        menubar_scheme = scheme;
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
        
        v = MetadataUtils.getMetadataNameValuePairs(scheme,
                version, null);
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
                <img src="<%=basePath%>/images/evs_termsbrowser_logo.gif" width="383" height="97" border="0"/></a>
              </div>
            </div>
            <!-- end Thesaurus, banner search area -->
            <!-- Quick links bar -->
            <%@ include file="/pages/templates/quickLink.jsp" %>
            <!-- end Quick links bar -->  
            <div class="pagecontent">
              <p>Please review the following License/Copyright statement for <b><%=scheme%></b>.
              </p>
              <textarea cols="87" rows="15" readonly align="left"><%=licenseStmt%></textarea>
              <p>If and only if you agree to these terms/conditions, click the
              Accept button to proceed.</p>
              <p>
              <form><h:commandButton id="accept" value="Accept"
                action="#{userSessionBean.acceptLicenseAgreement}"
                image="#{facesContext.externalContext.requestContextPath}/images/accept.gif"
                alt="Accept">
              </h:commandButton> &nbsp;&nbsp; <img
                src="<%=request.getContextPath()%>/images/cancel.gif" name="cancel"
                alt="reset" onClick="history.back()" /> <input type="hidden"
                id="dictionary" name="dictionary" value="<%=scheme%>" /> <input
                type="hidden" id="version" name="version" value="<%=version%>" /></form>
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
                <div class="vocabularynamebanner">
                  <div class="vocabularynameshort"><%=shortName%></div>
                  <div class="vocabularynamelong"><%=scheme%></div>
                </div>              
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
                              <a href="<%=request.getContextPath()%>/pages/vocabulary_home.jsf?dictionary=<%=dictionary%>&scheme=<%=menubar_scheme%>">Home</a>
                        <% } else { %>
                              <a href="<%=request.getContextPath()%>/pages/vocabulary_home.jsf?dictionary=<%=dictionary%>&scheme=<%=menubar_scheme%>&version=<%=menubar_version%>">Home</a>
                        <% }
                           if (download_site != null) {
                        %>    | <a href="#" onclick="javascript:window.open('<%=download_site%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
                              Download </a>
                        <% } %>
                        | <a href="#" onclick="javascript:window.open('<%=request.getContextPath()%>/pages/hierarchy.jsf?dictionary=<%=menubar_scheme%>&version=<%=menubar_version%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
                        View Hierarchy </a>
                        <% if (menubar_scheme0.compareTo("NCI Thesaurus") == 0) { %>
                              | <a href="<%=request.getContextPath()%>/pages/subset.jsf">Subsets</a>
                        <% } %> | <a href="<%=request.getContextPath()%>/pages/help.jsf">Help</a>
                      </td>
                      <td align="right">                                
                        <%
                           Vector visitedConcepts = (Vector) request.getSession().getAttribute("visitedConcepts");
                           if (visitedConcepts != null && visitedConcepts.size() > 0) {
                             String visitedConceptsStr = DataUtils.getVisitedConceptLink(visitedConcepts);
                        %>
                             <%=visitedConceptsStr%>
                        <% } %>
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
                <% if (v == null || v.size() == 0) { %>
                    <i>Metadata not found.</i>
                <% } else { %>
                    <i>&nbsp;</i>
                    <table class="dataTable">
                    <%
                       int n1 = 0;
                       for (int i = 0; i < v.size(); i++) {
                           String s = (String) v.get(i);
                           Vector ret_vec = DataUtils.parseData(s, "|");
                           String meta_prop_name = (String) ret_vec.elementAt(0);
                           if (!metadata_names.contains(meta_prop_name)
                                   && meta_prop_name
                                       .compareTo("html_compatable_description") != 0) {
                               String meta_prop_value = (String) ret_vec
                               .elementAt(1);
                               if (meta_prop_value.startsWith("ftp:")
                                       || meta_prop_value.startsWith("http:")) {
                                     meta_prop_value = DataUtils
                                         .getDownloadLink(meta_prop_value);
                               }                               
                               if (n1 % 2 == 0) {
                            	   %><tr class="dataRowDark"><%                            	   
                               } else {
                                 %><tr class="dataRowLight"><%                            	   
                               }
                               n1++;
                               %>                               
                                     <td><%=meta_prop_name%></td>
                                     <td><%=meta_prop_value%></td>
                                   </tr>  
                               <%                               
                           }
                        }  
            	      %>
                    </table>
                <% } %>         
                <%@ include file="/pages/templates/nciFooter.html" %>
              </div> 
        <% } %>
        </div><!-- end main-area -->
        <div class="mainbox-bottom"><img src="<%=basePath%>/images/mainbox-bottom.gif" width="745" height="5" alt="Mainbox Bottom" /></div>
      </div><!-- end center-page -->
    </f:view>
    <br/>
  </body>
</html>