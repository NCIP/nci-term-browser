<%--L
  Copyright Northrop Grumman Information Technology.

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
L--%>

<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="java.util.Vector"%>
<%@ page import="gov.nih.nci.evs.browser.utils.*" %>
<%@ page import="gov.nih.nci.evs.browser.common.*" %>

<%
  String basePath = request.getContextPath();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
  <head>
    <title>Sources</title>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />
    <link rel="shortcut icon" href="<%= request.getContextPath() %>/favicon.ico" type="image/x-icon" />
    <script type="text/javascript" src="<%= request.getContextPath() %>/js/script.js"></script>
  </head>
  <body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" >
  <f:view>
  <div id="popupContainer">
      <!-- nci popup banner -->
      <div class="ncipopupbanner">
        <a href="http://www.cancer.gov" target="_blank" alt="National Cancer Institute"><img src="<%=basePath%>/images/nci-banner-1.gif" width="440" height="39" border="0" alt="National Cancer Institute" /></a>
        <a href="http://www.cancer.gov" target="_blank" alt="National Cancer Institute"><img src="<%=basePath%>/images/spacer.gif" width="48" height="39" border="0" alt="National Cancer Institute" class="print-header" /></a>
      </div>
      <!-- end nci popup banner -->
      <div id="popupMainArea">
        <table class="evsLogoBg" cellspacing="3" cellpadding="0" border="0" width="570px">
        <tr>
          <td valign="top">
            <a href="http://evs.nci.nih.gov/" target="_blank" alt="Enterprise Vocabulary Services">
              <img src="<%=basePath%>/images/evs-popup-logo.gif" width="213" height="26" alt="EVS: Enterprise Vocabulary Services" title="EVS: Enterprise Vocabulary Services" border="0" />
            </a>
          </td>
          <td valign="top"><div id="closeWindow"><a href="javascript:window.close();"><img src="<%=basePath%>/images/thesaurus_close_icon.gif" width="10" height="10" border="0" alt="Close Window" />&nbsp;CLOSE WINDOW</a></div></td>
        </tr>
        </table>
        <div><img src="<%=basePath%>/images/termbrowser_popup_banner.gif" width="612" height="56" alt="NCI Thesaurus" title="" border="0" /></div>
        <div id="popupContentArea">
          <!-- Term Type content -->
          <table width="580px" cellpadding="3" cellspacing="0" border="0">
            <tr class="pageTitle">
              <td align="left">
                <b>Sources</b>
              </td>
              <td align="right">
                <font size="1" color="red" align="right">
                  <a href="javascript:printPage()"><img src="<%= request.getContextPath() %>/images/printer.bmp" border="0" alt="Send to Printer" ><i>Send to Printer</i></a>
                </font>
              </td>
            </tr>
          </table>
          <hr/>
          <table width="580px" cellpadding="3" cellspacing="0" border="0">
            <tr class="textbody"><td align="left">

<p>
The NCI Term Browser provides access to the terminology sources listed below, some of which are proprietary and included, by permission, for non-commercial use only. 
</p>
<ul>
<li>
<b>ICD-10</b> & <b>ICD-O-3</b>: The World Health Organization allows use of ICD-10 and ICD-O-3 in NCI Enterprise Vocabulary Services, 
but requires licensing for other purposes 
(see
		    <a href="http://www.who.int/classifications/icd/en/" target="_blank"
		      alt="ICD10 License">http://www.who.int/classifications/icd/en/</a>
). 
</li>
<li>
<b>MedDRA</b>: The Medical Dictionary for Regulatory Activities (MedDRA) terminology is licensed for NCI work and may be viewed on NCI browsers. 
All other uses are prohibited, unless covered by separate subscription to MedDRA from the MedDRA MSSO 
(see 
		    <a href="http://www.meddramsso.com" target="_blank"
		      alt="MedDRA License">http://www.meddramsso.com</a>
)
or contact at http://mssohelp@ngc.com , 877.258.8280, or 12011 Sunset Hills Road Reston Virginia, 20190-3285. 
</li>
<li>
<b>NCIm</b> is built using the National Library of Medicine's UMLS Metathesaurus, 
use of which is restricted under the UMLS license agreement 
(see 
		    <a href="http://www.nlm.nih.gov/research/umls/license.html" target="_blank"
		      alt="UMLS Metathesaurus License">http://www.nlm.nih.gov/research/umls/license.html</a>
). 
</li>
<li>
<b>SNOMED CT</b>: The International Health Terminology Standards Development Organisation (IHTSDO) allows use of SNOMED CT in NCI Enterprise Vocabulary Services, 
but requires licensing for other purposes 
(see 
		    <a href="http://www.ihtsdo.org/join-us/use-snomed-ct-licenses/" target="_blank"
		      alt="SNOMED CT License">http://www.ihtsdo.org/join-us/use-snomed-ct-licenses/</a>
). 
</li>
<li>
<b>UMLS Semantic Network</b>: The National Library of Medicine makes the UMLS Semantic Network freely available without charge on request, 
subject to the conditions specified at  
		    <a href="http://semanticnetwork.nlm.nih.gov/TermsAndConditions/" target="_blank"
		      alt="UMLS Metathesaurus Terms and Conditions">http://semanticnetwork.nlm.nih.gov/TermsAndConditions/</a>
. 
</li>
</ul>


            </td></tr>
          </table>
          <br/>
          <table width="580px" cellpadding="3" cellspacing="0" border="0">
            <%
              String propertyName = "html_compatable_description";
              Vector from_vec = new Vector();
              Vector to_vec = new Vector();
              from_vec.add("ncim_url"); to_vec.add(new DataUtils().getNCImURL());
              Vector abbr_vec = (Vector) request.getSession().getAttribute("source_descriptions");
              if (abbr_vec == null) {
                  abbr_vec = new MetadataUtils().getSupportedVocabularyMetadataValues(propertyName);
                  request.getSession().setAttribute("source_descriptions", abbr_vec);
              }              
              for (int n=0; n<abbr_vec.size(); n++) {
                 String t = (String) abbr_vec.elementAt(n);
                 Vector w = DataUtils.parseData(t, "|");
                 String abbr = (String) w.elementAt(0);
                 String def = (String) w.elementAt(1);
                 def = DataUtils.replaceInnerEvalExpressions(def, from_vec, to_vec);
                 String rowColor = (n%2 == 0) ? "dataRowDark" : "dataRowLight";
            %>
              <tr class="<%=rowColor%>">
                <td><%=abbr%></td>
                <td>&nbsp;</td>
                <td><%=def%></td>
              </tr>
            <%
              }
            %>
          </table>
          <br/>

        </div>
        <!-- End of Term Type content -->
      </div>
  </div>
  </f:view>
  </body>
</html>