<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="java.util.Vector"%>
<%@ page import="org.LexGrid.concepts.Concept" %>
<%@ page import="gov.nih.nci.evs.browser.utils.DataUtils" %>
<%
  String terminology_subset_download_url = new DataUtils().getTerminologySubsetDownloadURL();
  String term_subset_link = null;
  if ( (terminology_subset_download_url == null) ||
    (terminology_subset_download_url.compareToIgnoreCase("null") == 0) ) {
    term_subset_link = "href=\"#\" onclick=\"javascript:alert('Not available yet.')\";";
  } else {
    term_subset_link = "href=\"" + terminology_subset_download_url +
      "\" target=\"_blank\" alt=\"Terminology Subset Download\"";
  }
  String term_suggestion_application_url = new DataUtils().getTermSuggestionURL();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
  <title>NCI Thesaurus</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />
  <link rel="shortcut icon" href="<%= request.getContextPath() %>/favicon.ico" type="image/x-icon" />
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/script.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/search.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/dropdown.js"></script>
</head>
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<f:view>
  <%@ include file="/pages/templates/header.xhtml" %>
  <div class="center-page">
    <%@ include file="/pages/templates/sub-header.xhtml" %>
    <!-- Main box -->
    <div id="main-area">
      <%@ include file="/pages/templates/content-header.xhtml" %>
      <!-- Page content -->
      <div class="pagecontent">
        <div class="texttitle-blue">NCI Thesaurus Subsets</div>
        <hr>
        <p>
          <i>
            There are over 100 specially defined subsets in NCI Thresaurus. Each has its own NCIt concept,
            defining the nature of the subset, and these are hierarchically organized under the parent concept
            <a href="<%=request.getContextPath()%>/ConceptReport.jsp?dictionary=NCI%20Thesaurus&code=C54443">
            Terminology Subset (C54443)</a>. These subset concepts are linked to subset-member concepts through
            some 10,000 association relationships.
          </i>
        </p>
        <p>
          <i>
            Most of these subsets are federal and internatinal coding standards maintained in active collaboration with our FDA, CDISC, and other
            partners. They are currently distributd largely as tab-delimited data and Excel spreadsheet files accessible
            via FTP. Below are the three most important subset collections, curently presented on these Cancer.gov Web pages:
          </i>
        </p>
        <table width="100%" border="0">
          <tr>
            <td>
              <a href="http://www.cancer.gov/cancertopics/terminologyresources/CDISC" target="_blank"
                  alt="Clinical Data Interchange Standards Consortium (CDISC) Terminology">
                <img src="<%=basePath%>/images/CDISC_Logo.jpg" border="0"
                  alt="Clinical Data Interchange Standards Consortium (CDISC) Terminology"/>
              </a>
            </td>
            <td>
              <a href="http://www.cancer.gov/cancertopics/terminologyresources/FDA" target="_blank"
                  alt="U.S. Food ad Drug Administration Terminology">
                <img src="<%=basePath%>/images/FDA_Logo.jpg" border="0"
                  alt="U.S. Food ad Drug Administration Terminology"/>
              </a>
            </td>
            <td>
              <a href="http://www.cancer.gov/cancertopics/terminologyresources/FMT" target="_blank"
                  alt="Federal Medication Terminologies">
                <img src="<%=basePath%>/images/FMT_Logo.jpg" border="0"
                  alt="Federal Medication Terminologies"/>
              </a>
            </td>
          </tr>
          <tr>
            <td class="textbody" valign="top">
              <i>
                <a href="http://www.cancer.gov/cancertopics/terminologyresources/CDISC" target="_blank"
                    alt="Clinical Data Interchange Standards Consortium (CDISC) Terminology">
                  Clinical Data Interchange Standards Consortium Terminology:
                </a>
                CDISC is an international, non-profit organization hat develops and supports global data standards for medical research.
                CDISC is working actively with EVS to develop and support controlled terminology in several aras, notably CDISC's Study
                Data Tabulation Model (SDTM).
              </i>
            </td>
            <td class="textbody" valign="top">
              <i>
                <a href="http://www.cancer.gov/cancertopics/terminologyresources/FDA" target="_blank"
                    alt="U.S. Food ad Drug Administration Terminology">
                  U.S. Food ad Drug Administration Terminology:
                </a>
                The FDA is working with EVS to develop and supporyt controlled
                terminology in several areas. More than 10,000 FDA terms and codes are stored in NCI Thesaurus (NCIt).
              </i>
            </td>
            <td class="textbody" valign="top">
              <i>
                <a href="http://www.cancer.gov/cancertopics/terminologyresources/FMT" target="_blank"
                    alt="Federal Medication Terminologies">
                  Federal Medication Terminologies:
                </a>
                The Federal Medication (FedMed) interagency collaboration is organizing
                an agreed set of standard, comprehensive, freely and easily accessible Federal Medication Terminologies (FMT)
                to improve the exchange and public availability of medication information. FedMed is a joint effort of these Federal partner
                agencies.
              </i>
            </td>
          </tr>
        </table>
        <p>
          <i>
            EVS is developing extended methods to create, browse, download, and have direct computer API access to these and other NCIt subsets.
            Click
            <a <%=term_subset_link%>>here</a>
            to see the new download page for Terminology Subset Reports (still under development).
          </i>
        </p>
        <%@ include file="/pages/templates/nciFooter.html" %>
      </div>
      <!-- end Page content -->
    </div>
    <div class="mainbox-bottom"><img src="<%=basePath%>/images/mainbox-bottom.gif" width="745" height="5" alt="Mainbox Bottom" /></div>
    <!-- end Main box -->
  </div>
</f:view>
</body>
</html>