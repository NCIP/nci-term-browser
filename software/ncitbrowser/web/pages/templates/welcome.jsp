<%--L
  Copyright Northrop Grumman Information Technology.

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
L--%>

<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ page import="gov.nih.nci.evs.browser.utils.DataUtils" %>
<%@ page import="gov.nih.nci.evs.browser.utils.MetadataUtils" %>

<%
  String nci_meta_url = new DataUtils().getNCImURL();
  String vocablary_version = DataUtils.getVersion();
  String vocablary_version_value = DataUtils.getVersion();
  if (vocablary_version_value == null) vocablary_version_value = "";
%>
<div id="message" class="textbody">
  <table border="0" width="700px"><tr>
    <td><div class="texttitle-blue">Welcome</div></td>
    <td><div class="texttitle-blue-rightJust">Version: <%= vocablary_version_value %></div></td>
  </tr></table>
  <hr/>

<%
Vector from_vec = new Vector();
Vector to_vec = new Vector();
from_vec.add("ncim_url");
to_vec.add(nci_meta_url);

String html_compatable_description_value = DataUtils.getMetadataValue(Constants.CODING_SCHEME_NAME, "html_compatable_description");

String contextPath = request.getContextPath();
if (html_compatable_description_value == null) {
    html_compatable_description_value = "WARNING: html_compatable_description metadata element not found.";
} else {
    html_compatable_description_value = DataUtils.replaceContextPath(html_compatable_description_value, contextPath);
    html_compatable_description_value = DataUtils.replaceInnerEvalExpressions(html_compatable_description_value, from_vec, to_vec);
}

String _version = request.getParameter("version");
System.out.println("(welcome.jsp: ) vocabulary_version: " + _version);
if (vocabulary_version != null) {
	request.setAttribute("version", _version);
}


String version_value = DataUtils.getMetadataValue(Constants.CODING_SCHEME_NAME, _version, "version");
String source_url_value = DataUtils.getMetadataValue(Constants.CODING_SCHEME_NAME, _version, "source_url");
String download_url_value = DataUtils.getMetadataValue(Constants.CODING_SCHEME_NAME, _version, "download_url");
String copyright_statement_value = DataUtils.getMetadataValue(Constants.CODING_SCHEME_NAME, _version, "copyright");
String cabig_vkc_index_url_value = DataUtils.getMetadataValue(Constants.CODING_SCHEME_NAME, _version, "cabig_vkc_index_url");

String license_statement_value = null;
String license_display_value = DataUtils.getMetadataValue(Constants.CODING_SCHEME_NAME, _version, "license_display");
if (license_display_value != null && (license_display_value.compareTo("show") == 0 || license_display_value.compareTo("accept") == 0)) {
    license_statement_value = DataUtils.getMetadataValue(Constants.CODING_SCHEME_NAME, _version, "license_statement");
}

%>
  <table border="0">
    <tr>
      <td class="textbody">
        <%=html_compatable_description_value%>
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


      <td valign="top">
        <table border="0">
          <tr valign="top">
            <td width="10px"></td>
            <td>
              <a href="http://evs.nci.nih.gov/" target="_blank" alt="EVS">
                <img src="<%=basePath%>/images/EVSTile.gif"
                  width="77" height="38px" alt="EVS" border="0"/>
              </a>
            </td>
            <td width="3px"></td>
            <td class="textbody" valign="top">
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
              Comprehensive database of 3,600,000 terms from 76 terminologies.
            </td>
          </tr>
          <tr valign="top">
            <td width="10px"></td>
            <td>
              <a href="<%= request.getContextPath() %>/start.jsf" alt="NCI Term Browser">
                <img src="<%=basePath%>/images/EVSTermsBrowserTile.gif"
                  width="77" height="38px" alt="Bio Portal" border="0"/>
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