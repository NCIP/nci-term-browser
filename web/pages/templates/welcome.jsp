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
String html_compatable_description_value = DataUtils.getMetadataValue(Constants.CODING_SCHEME_NAME, "html_compatable_description");
String version_value = DataUtils.getMetadataValue(Constants.CODING_SCHEME_NAME, "version");
String download_url_value = DataUtils.getMetadataValue(Constants.CODING_SCHEME_NAME, "source_url");
if (download_url_value == null) download_url_value = DataUtils.getMetadataValue(Constants.CODING_SCHEME_NAME, "download_url");
String license_statement_value = DataUtils.getMetadataValue(Constants.CODING_SCHEME_NAME, "license_statement");
%>
  <table border="0">
    <tr>
      <td class="textbody">
      <%
        if (html_compatable_description_value == null) {
      %>  

		NCI Thesaurus (NCIt) provides reference terminology for many NCI and
		other systems. It covers vocabulary for clinical care, translational
		and basic research, and public information and administrative activities.
		<br><br>
		<b>NCIt features:</b>
		<ul>
		  <li>Stable, unique codes for biomedical concepts;
		  <li>Preferred terms, synonyms, definitions, research codes,
		    external source codes, and other information;
		  <li>Links to
		    <a href="<%=nci_meta_url%>" target="_blank"
		      alt="NCI Metathesaurus">NCI Metathesaurus</a>
		    and other information sources;
		  <li>Over 200,000 cross-links between concepts, providing formal
		    logic-based definition of many concepts;
		  <li>Extensive content integrated from NCI and other partners, much
		    available as separate NCIt
		    <a href="<%= request.getContextPath() %>/pages/subset.jsf">subsets</a>
		  <li>Updated frequently by a team of subject matter experts.
		</ul>
		NCIt is a widely recognized standard for biomedical coding and reference,
		used by a broad variety of public and private partners both nationally
		and internationally.
        <%
        } else {
        %>
            <%=html_compatable_description_value%>
        <%    
        }
        %>
        
        <%
        if (download_url_value != null) {
        %>
            <p>
            Source URL: 
              <a href="<%=download_url_value%>" target="_blank"><%=download_url_value%></a>
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
              NCI and other terminologies in an integrated ontology environment.
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