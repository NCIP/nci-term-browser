<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="java.util.Vector"%>
<%@ page import="org.LexGrid.concepts.Entity" %>
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
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns:c="http://java.sun.com/jsp/jstl/core">
<head>
  <title>NCI Thesaurus - Subsets</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />
  <link rel="shortcut icon" href="<%= request.getContextPath() %>/favicon.ico" type="image/x-icon" />
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/script.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/search.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/dropdown.js"></script>
</head>
<body onLoad="document.forms.searchTerm.matchText.focus();">
<f:view>
  <!-- Begin Skip Top Navigation -->
    <a href="#evs-content" class="hideLink" accesskey="1" title="Skip repetitive navigation links">skip navigation links</A>
  <!-- End Skip Top Navigation --> 
  <%@ include file="/pages/templates/header.jsp" %>
  <div class="center-page">
    <%@ include file="/pages/templates/sub-header.jsp" %>
    <!-- Main box -->
    <div id="main-area">
      <%@ include file="/pages/templates/content-header.jsp" %>
      <!-- Page content -->
      <div class="pagecontent">
        <a name="evs-content" id="evs-content"></a>
        <div class="texttitle-blue">NCI Thesaurus Subsets</div>
        <hr>
        <p>
          There are over 100 specially defined subsets in NCI Thesaurus. Each has its own NCIt concept,
          defining the nature of the subset, and these are hierarchically organized under the parent concept
          <a href="<%=request.getContextPath()%>/ConceptReport.jsp?dictionary=NCI%20Thesaurus&code=C54443">
          Terminology Subset (C54443)</a>. These subset concepts are linked to subset-member concepts through
          over 20,000 association relationships. Many of these subsets are now also available as
          <a href="<%=request.getContextPath()%>/pages/value_set_source_view.jsf?nav_type=valuesets">Value Sets</a>.
        </p>
        <p>
          Most of these subsets are federal and international coding standards maintained in active collaboration with our FDA, CDISC, and other
          partners. They are currently distributed largely as tab-delimited data and Excel spreadsheet files accessible
          via FTP. Below are four important subset collections currently presented on these Cancer.gov Web pages:
        </p>
        <table width="670px" border="0">
          <tr>
            <td>
              <a href="http://www.cancer.gov/cancertopics/terminologyresources/CDISC" target="_blank"
                  alt="Clinical Data Interchange Standards Consortium (CDISC) Terminology">
                <img src="<%=basePath%>/images/CDISC_Logo.jpg" border="0"
                  alt="Clinical Data Interchange Standards Consortium (CDISC) Terminology"/>
              </a>
            </td>
            <td class="textbody" valign="top">
              <a href="http://www.cancer.gov/cancertopics/terminologyresources/CDISC" target="_blank"
                  alt="Clinical Data Interchange Standards Consortium (CDISC) Terminology">
                Clinical Data Interchange Standards Consortium Terminology:
              </a>
              CDISC is an international, non-profit organization that develops and supports global data standards for medical research.
              CDISC is working actively with EVS to develop and support controlled terminology in several areas, notably CDISC's Study
              Data Tabulation Model (SDTM).
              <br/><br/>
            </td>
          </tr>
          <tr>
            <td>
              <a href="http://www.cancer.gov/cancertopics/terminologyresources/FDA" target="_blank"
                  alt="U.S. Food and Drug Administration Terminology">
                <img src="<%=basePath%>/images/FDA_Logo.jpg" border="0"
                  alt="U.S. Food and Drug Administration Terminology"/>
              </a>
            </td>
            <td class="textbody" valign="top">
              <a href="http://www.cancer.gov/cancertopics/terminologyresources/FDA" target="_blank"
                  alt="U.S. Food and Drug Administration Terminology">
                U.S. Food and Drug Administration Terminology:
              </a>
              The FDA is working with EVS to develop and support controlled
              terminology in several areas. Some 15,000 terms are stored in NCI Thesaurus (NCIt).
              <br/><br/>
            </td>
          </tr>
          <tr>
            <td>
              <a href="http://www.cancer.gov/cancertopics/terminologyresources/FMT" target="_blank"
                  alt="Federal Medication Terminologies">
                <img src="<%=basePath%>/images/FMT_Logo.jpg" border="0"
                  alt="Federal Medication Terminologies"/>
              </a>
            </td>
            <td class="textbody" valign="top">
              <a href="http://www.cancer.gov/cancertopics/terminologyresources/FMT" target="_blank"
                  alt="Federal Medication Terminologies">
                Federal Medication Terminologies:
              </a>
              The Federal Medication (FedMed) interagency collaboration is organizing
              an agreed set of standard, comprehensive, freely and easily accessible Federal Medication Terminologies (FMT)
              to improve the exchange and public availability of medication information. FedMed is a joint effort of eight Federal partner
              agencies.
              <br/><br/>
            </td>
          </tr>
          <tr>
            <td>
              <a href="http://www.cancer.gov/cancertopics/cancerlibrary/terminologyresources/page7" target="_blank"
                  alt="National Council for Prescription Drug Programs">
                <img src="<%=basePath%>/images/NCPDP_logo.jpg" border="0"
                  alt="National Council for Prescription Drug Programs"/>
              </a>
            </td>
            <td class="textbody" valign="top">
              <a href="http://www.cancer.gov/cancertopics/cancerlibrary/terminologyresources/page7" target="_blank"
                  alt="Federal Medication Terminologies">
                National Council for Prescription Drug Programs Terminology:
              </a>
              The NCPDP is a not-for-profit ANSI-Accredited Standards Development Organization
              representing virtually every sector of the pharmacy services industry. NCPDP is
              working with NCI to provide terminology sets that can be easily used for transfer
              of data related to medications, supplies, and services within the healthcare system.
            </td>
          </tr>
        </table>
        <p>
          EVS is developing extended methods to create, browse, download, and have direct computer API access to these and other NCIt subsets.
          Click
          <a href="<%= request.getContextPath() %>/pages/list_standard_reports.jsf">here</a>
          to see the new download page for Terminology Subset Reports.
        </p>
        <%@ include file="/pages/templates/nciFooter.jsp" %>
      </div>
      <!-- end Page content -->
    </div>
    <div class="mainbox-bottom"><img src="<%=basePath%>/images/mainbox-bottom.gif" width="745" height="5" alt="Mainbox Bottom" /></div>
    <!-- end Main box -->
  </div>
</f:view>
</body>
</html>