<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ page import="gov.nih.nci.evs.browser.utils.*" %>

<%
  String nci_meta_url = new DataUtils().getNCImURL();
  JSPUtils.JSPHeaderInfo info4 = new JSPUtils.JSPHeaderInfo(request);
  String vocablary_version_value = info4.version;
  boolean isMapping = DataUtils.isMapping(scheme, null);


%>
<div id="message" class="textbody">
  <table border="0" width="700px"><tr>
    <td><div class="texttitle-blue">Welcome</div></td>

    <!-- <td><div class="texttitle-blue-rightJust">Version: <%=HTTPUtils.cleanXSS(vocablary_version_value) %></div></td> -->
<%
    if (isMapping) {
         String requestContextPath = request.getContextPath();

%>
      <td align="right">
      <a href="<%=request.getContextPath() %>/pages/mapping.jsf?dictionary=<%=HTTPUtils.cleanXSS(scheme)%>&version=<%=vocablary_version_value%>">
      <img src="<%=basePath%>/images/ViewMapping.gif" alt="View Mapping" style="border-style:none;"/>
      </a>
      &nbsp;
      <a href="<%=request.getContextPath() %>/ajax?action=export_mapping&dictionary=<%=HTTPUtils.cleanXSS(scheme)%>&version=<%=vocablary_version_value%>">
      <img src="<%=basePath%>/images/exportcsv.gif" alt="Export Mapping" style="border-style:none;"/>
      </a> 
      </td>

<%
    }
%>


  </tr></table>
  <hr/>

<%


String _version = HTTPUtils.cleanXSS((String) request.getParameter("version"));
//System.out.println("(*****welcome-other.jsp) vocabulary_version: " + _version);
if (vocabulary_version != null) {
  request.setAttribute("version", _version);
}



String html_compatable_description_value = DataUtils.getMetadataValue(scheme, _version, "html_compatable_description");
String version_value = DataUtils.getMetadataValue(scheme, _version, "term_browser_version");
if (version_value == null)
    version_value = DataUtils.getMetadataValue(scheme, _version, "version");
String source_url_value = DataUtils.getMetadataValue(scheme, _version, "source_url");
String download_url_value = DataUtils.getMetadataValue(scheme, _version, "download_url");
String copyright_statement_value = DataUtils.getMetadataValue(scheme, _version, "copyright");
String cabig_vkc_index_url_value = DataUtils.getMetadataValue(scheme, _version, "cabig_vkc_index_url");

String license_statement_value = null;
String license_display_value = DataUtils.getMetadataValue(scheme, _version, "license_display");
if (license_display_value != null && (license_display_value.compareTo("show") == 0 || license_display_value.compareTo("accept") == 0)) {
    license_statement_value = DataUtils.getMetadataValue(scheme, _version, "license_statement");
}




%>
  <table border="0">
    <tr>
      <td class="textbody" width="388px" valign="top" align="left">
      <%
	if (DataUtils.isNullOrBlank(scheme)) {
      %>
            <p class="textbodyred">&nbsp;<b><%=Constants.UNIDENTIFIABLE_VOCABULARY%></b></p>
      <%      
	} else if (html_compatable_description_value == null) {
      %>
            <%=HTTPUtils.cleanXSS(scheme)%>
        <%
        } else {
        %>
            <%=html_compatable_description_value%>
        <%
        }
        %>

        <%
        if (source_url_value != null) {
        %>
            <p>
            Source Home Page:
              <a href="<%=source_url_value%>" target="_blank"><%=source_url_value%></a>
            </p>
        <%
        }

        if (download_url_value != null) {
        %>
            <p>
            Download:
              <a href="<%=download_url_value%>" target="_blank"><%=download_url_value%></a>
            </p>
        <%
        }
        
        if (cabig_vkc_index_url_value != null) {
        %>
            <p>
            caBIG VKC Link:
              <a href="<%=cabig_vkc_index_url_value%>" target="_blank"><%=cabig_vkc_index_url_value%></a>
            </p>
        <%
        }

        if (license_statement_value != null) {
        %>
            <p>
            <%=license_statement_value%>
            </p>
        <%
        }
        %>

      </td>


      <td valign="top" align="right">
        <table border="0">
          <tr valign="top">
            <td width="10px"></td>
            <td width="77px">
              <a href="http://evs.nci.nih.gov/" target="_blank" alt="EVS">
                <img src="<%=basePath%>/images/EVSTile.gif"
                  width="77px" height="38px" alt="EVS" border="0"/>
              </a>
            </td>
            <td width="3px"></td>
            <td class="textbody" align="left" valign="top" width="210px">
              <a href="http://evs.nci.nih.gov/" target="_blank" alt="EVS">
                NCI Enterprise Vocabulary Services</a>:
              Terminology resources and services for NCI and the biomedical community.
            </td>
          </tr>
          <tr valign="top">
            <td width="10px"></td>
            <td>
              <a href="<%=nci_meta_url%>" target="_blank" alt="NCIm">
                <img src="<%=basePath%>/images/NCImTile.gif"
                  width="77" height="38px" alt="NCIm" border="0"/>
              </a>
            </td>
            <td width="3px"></td>
            <td class="textbody" valign="top">
              <a href="<%=nci_meta_url%>" target="_blank" alt="NCIm">
                NCI Metathesaurus</a>:
              Comprehensive database of 4,000,000 terms from 75 terminologies.
            </td>
          </tr>
          <tr valign="top">
            <td width="10px"></td>
            <td>
              <a href="<%= request.getContextPath() %>/start.jsf" alt="NCI Term Browser">
                <img src="<%=basePath%>/images/EVSTermsBrowserTile.gif"
                  width="77" height="38px" alt="NCI Term Browser" border="0"/>
              </a>
            </td>
            <td width="3px"></td>
            <td class="textbody" valign="top">
              <a href="<%= request.getContextPath() %>/start.jsf" alt="NCI Term Browser">
                NCI Term Browser</a>:
              NCI and other terminologies in an integrated environment.
            </td>
          </tr>
          <tr valign="top">
            <td width="10px"></td>
            <td>
              <a href="http://www.cancer.gov/cancertopics/terminologyresources/"
                  target="_blank" alt="NCI Terminology Resources">
                <img src="<%=basePath%>/images/Cancer_govTile.gif"
                  alt="NCI Terminology Resources" border="0"/>
              </a>
            </td>
            <td width="3px"></td>
            <td class="textbody" valign="top">
              <a href="http://www.cancer.gov/cancertopics/terminologyresources/"
                  target="_blank" alt="NCI Terminology Resources">
                NCI Terminology Resources</a>:
              More information on NCI dictionaries and resources.
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</div>
