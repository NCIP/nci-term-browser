<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=windows-1252"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns:c="http://java.sun.com/jsp/jstl/core">
<head>
  <title>NCI Thesaurus</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />
  <link rel="shortcut icon" href="<%= request.getContextPath() %>/favicon.ico" type="image/x-icon" />
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/script.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/search.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/dropdown.js"></script>
</head>
<body onLoad="document.forms.searchTerm.matchText.focus();">
<script type="text/javascript" src="<%= request.getContextPath() %>/js/wz_tooltip.js"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/tip_centerwindow.js"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/tip_followscroll.js"></script>
<f:view>
  <!-- Begin Skip Top Navigation -->
     <a href="#evs-content" class="hideLink" accesskey="1" title="Skip repetitive navigation links">skip navigation links</A>
  <!-- End Skip Top Navigation --> 
  <%
    String contactUsUrl = request.getContextPath() + "/pages/contact_us.jsf";
    String subsetsUrl = request.getContextPath() + "/pages/subset.jsf";
    String arrowImage = request.getContextPath() + "/images/up_arrow.jpg";
    String tab = "&nbsp;&nbsp;&nbsp;&nbsp;";
    String tab2 = tab + tab;
    String dyeePrevHelpUrl = request.getContextPath() + "/pages/help_orig.jsf";
  %>
  <%@ include file="/pages/templates/header.jsp" %>
  <div class="center-page">
    <%@ include file="/pages/templates/sub-header.jsp" %>
    <!-- Main box -->
    <div id="main-area">
    <%@ include file="/pages/templates/content-header-no-searchbox.jsp" %>
      <!-- Page content -->
      <div class="pagecontent">
        <a name="evs-content" id="evs-content"></a>
<%-- DYEE(Begin) --%>        
        <div class="texttitle-blue">Note(s):</div>
        <ul>
          <li>This help page is still being updated.</li>
          <li>To see previous version, select the following link:
            <ul>
              <li><a href="<%=dyeePrevHelpUrl%>">Previous Help</a></li>
            </ul>
          </li>
        </ul>
        <div class="texttitle-blue">Reminder(s):</div>
        <ul>
          <li>Verify links in the "Help" section all works.</li>
          <li>Verify "see below" links in Advanced Search.</li>
          <li>Verify "see below" links in Concept Details.</li>
        </ul>
        <hr/>
<%-- DYEE(End) --%>

        <!-- ======================================= -->
        <!--                 HELP CONTENT            -->
        <!-- ======================================= -->
        <div class="texttitle-blue">Help</div>
       
        <p class="textbody">
          <a href="#introduction">Introduction</a><br/>
          <a href="#homePage">NCI Term Browser Home Page</a><br/>
          <br/>
          <%=tab%> <a href="#sources">Sources</a><br/>
          <%=tab%> <a href="#searchBox">Using the Search Box</a><br/>
          <%=tab%> <a href="#searchResults">Search Results</a><br/>
          <%=tab%> <a href="#searchingOther">Searching Other/Multiple Versions of a Terminology</a><br/>
          <%=tab%> <a href="#advancedSearch">Advanced Search</a><br/>
          <%=tab%> <a href="#conceptDetails">Concept Details</a><br/>
          <%=tab%> <a href="#additionalLinks">Additional Links for Individual Terminologies</a><br/>
          <%=tab%> <a href="#cartAndExport">Cart and Export Functionality</a><br/>
          <a href="#valueSetsTab">Value Sets Tab</a><br/>
          <%=tab%> <a href="#valueSetsSearchBox">Using the Search Box</a><br/>
          <%=tab%> <a href="#valueSetConceptDetails">Concept Details</a><br/>
          <a href="#mappingsTab">Mappings Tab</a><br/>
          <%=tab%> <a href="#mappingSets">Mapping Sets</a><br/>
          <%=tab%> <a href="#mappingSearchBox">Using the Search Box</a><br/>
          <a href="#additionalInformation">Additional Information</a><br/>
        </p>

        <%-- -------------------------------------------------------------- --%>        
        <div class="textbody">
          <br/>
          <table width="720px" cellpadding="0" cellspacing="0" border="0">
            <tr>
              <td><b><a name="introduction">Introduction</a></b></td>
              <td align="right">
                <a href="#"><img src="<%=arrowImage%>" 
                  width="16" height="16" border="0" alt="top" /></a>
              </td>
            </tr>
          </table>
          <p>
            <b>NCI Term Browser</b> provides a consistent, user-friendly 
            tool to browse, search and retrieve all of the biomedical 
            terminologies hosted by National Cancer Institute Enterprise 
            Vocabulary Services (NCI EVS), including both NCI Thesaurus 
            (NCIt) and the NCI Metathesaurus (NCIm), which itself 
            includes more than 70 terminologies.
          </p>
          <p>
            This new version of the <b>NCI Term Browser</b> adds new 
            functionalities, including retrieving and querying Value 
            Sets and Mappings, performing Advanced Searches, using a 
            Cart and Data Export, and searching in multiple versions 
            of a particular terminology.  All of these new functionalities 
            are discussed below.
          </p>
          <p>
            <b>Get in touch:</b> To get help or to offer suggestions, use 
            the browser's <a href="<%= contactUsUrl %>">Contact Us</a> page. 
          </p>
          <p>
            This help file provides basic information about how to 
            use the NCI Term Browser effectively, as well as links 
            to additional information elsewhere. The following 
            typeface font conventions are used for describing search 
            and the browser interface:
          </p>
          <table class="textbody" border="0" cellspacing="0" cellpadding="0">
            <tr>
              <td><%=tab2%></td><td><li><b>Bold:</b></li></td>
              <td><%=tab%></td><td>Browser links, buttons, page tabs, and drop-down boxes.</td>
            </tr>
            <tr>
              <td><%=tab2%></td><td><li><font face="courier">Fixed Width:</font></li></td>
              <td><%=tab%></td><td>Search strings.</td>
            </tr>
            <tr>
              <td><%=tab2%></td><td><li><i>Italics:</i></li></td>
              <td><%=tab%></td><td>Concept terms.</td>
            </tr>
          </table>
        </div>

        <%-- -------------------------------------------------------------- --%>        
        <div class="textbody">
          <br/>
          <table width="720px" cellpadding="0" cellspacing="0" border="0">
            <tr>
              <td><a name="homePage"><b>NCI Term Browser Home Page</b></a></td>
              <td align="right">
                <a href="#"><img src="<%=arrowImage%>" 
                  width="16" height="16" border="0" alt="top" /></a>
              </td>
            </tr>
          </table>
          <p>
            In the upper left of the NCI Term Browser home page are 
            3 tabs: the <b>Terminologies tab</b> (the default tab), 
            the <b>Value Sets tab</b>, and the <b>Mappings tab</b>:
          </p>
          <ul>
            <li>The <b>Terminologies</b> tab allows browsing and 
              searching within each of the NCI hosted terminologies, 
              or within multiple terminologies.</li>
            <li>The <b>Value Sets</b> tab allows browsing and searching 
              within all value sets (i.e., the CDISC SEND Terminology, 
              the FDA SPL Terminology, etc.) available on the NCI server.</li>
            <li>The <b>Mappings</b> tab allows browsing and searching 
              within a mapping set such as the PDQ to NCIt Mapping: 
              National Cancer Institute Thesaurus (201010).</li>
          </ul>
          <p>
            These 3 tabs will appear at the top of every Term Browser 
            page you are on, and the tab that is highlighted indicates 
            which section of the Term Browser you are in.  The 
            <b>NCI Term Browser</b> icon above the tabs always leads 
            back to the main Terminologies home page.
          </p>
          <p>
            The NCI Term Browser home page contains a <b>Search Box</b>
            in the upper right hand corner. This Search Box has different 
            options, and will bring back different results, depending on 
            which tab you are on in the Term Browser.
          </p>
          <p>
            Several links appear below the <b>Search Box</b>.  Most 
            appear only in certain tabs or other settings, but these 
            appear on all tabs:
          </p>
          <ul>
            <li><b>Sources</b> always appears at the left, and gives 
              information about all terminologies including the 
              descriptions appearing on each individual terminology's 
              home page.</li>
            <li><b>Visited Concepts</b> will appear under the Search 
              box once you start visiting individual concept pages. 
              It shows the concept name, terminology, and version for
              each concept visited, with the most recent concepts at 
              the top.</li>
            <li><b>Help</b> always appears at the far right, linking 
              to this Help page.</li>
          </ul>
        </div>

        <%-- -------------------------------------------------------------- --%>        
        <div class="textbody">
          <br/>
          <table width="720px" cellpadding="0" cellspacing="0" border="0">
            <tr>
              <td><a name="terminologies"><b>Terminologies Tab</b></a></td>
              <td align="right">
                <a href="#"><img src="<%=arrowImage%>" 
                  width="16" height="16" border="0" alt="top" /></a>
              </td>
            </tr>
          </table>
          <p>
            These terminologies normally represent each specific 
            meaning, such as melanoma, lung, or chemotherapy, by a 
            distinct concept with a unique, permanent code. Each 
            concept normally provides additional information such 
            as a preferred name, other terms and codes, definitions, 
            and relationships with other concepts. Concepts are 
            normally organized in parent-child hierarchies from 
            very broad top concepts down to the most specific 
            subcategories. All browsing and searching in this 
            browser reflects this concept-based view of terminology. 
            For information on other EVS browsers, file formats, 
            and computer application access, see the 
            <a href="http://evs.nci.nih.gov/" target="_blank">EVS web site.</a>
          </p>
        </div>

        <%-- -------------------------------------------------------------- --%>        
        <div class="textbody">
          <br/>
          <table width="720px" cellpadding="0" cellspacing="0" border="0">
            <tr>
              <td><a name="sources"><b>Sources</b></a></td>
              <td align="right">
                <a href="#"><img src="<%=arrowImage%>" 
                  width="16" height="16" border="0" alt="top" /></a>
              </td>
            </tr>
          </table>
          <p>
            The <b>Terminologies</b> tab of the NCI Term Browser 
            home page lists the terminologies that are available: 
            NCI Metathesaurus, NCI Thesaurus, and the rest in 
            alphabetical order. Click on a terminology name to 
            go to its home page, or click on a terminology's 
            check box to include it in a search from the 
            <b>Terminologies</b> tab of the NCI Term Browser 
            home page. Both above and below the terminologies
            list on the <b>Terminologies</b> tab are four buttons: 
          </p>
          <ul>
            <li><b>Select All</b> button: includes all terminologies
              for searching, leaving their check boxes checked.</li>
            <li><b>All but NCIm</b> button: includes all terminologies
              except NCI Metathesaurus (NCIm); this is to aid a quick 
              search since the time to retrieve search results may be 
              affected when a search within the NCIm is included.</li>
            <li><b>Clear</b> button: unselects all terminologies and 
              clears their check boxes.</li>
            <li><b>Search</b> button: starts a search in the same way
              as the Search button in the search box (see below).</li>
          </ul>
          <p>
            The <b>Sources</b> link at the top of the NCI Term Browser
            home page gives information about all terminologies, 
            including the terminology descriptions appearing 
            correspondingly on each individual terminology's home page. 
          </p>
        </div>

        <%-- -------------------------------------------------------------- --%>        
        <div class="textbody">
          <br/>
          <table width="720px" cellpadding="0" cellspacing="0" border="0">
            <tr>
              <td><a name="searchBox"><b>Using the Search Box</b></a></td>
              <td align="right">
                <a href="#"><img src="<%=arrowImage%>" 
                  width="16" height="16" border="0" alt="top" /></a>
              </td>
            </tr>
          </table>
          <p>
            The <b>Search Box</b>, in the upper right corner of the 
            Term Browser home page and of most Term Browser pages, 
            lets you enter all or part of what you are looking for 
            and how you want to search for it. Some details: 
          </p>

          <ul>
            <li><b>Text Box</b>: Enter the exact string of characters 
                you want to search for.
              <ul>
                <li>Search is not case sensitive (e.g., <font face="courier">aids</font>
                    will match <i>aids</i>, <i>Aids</i>, and <i>AIDS</i>).</li>
                <li>There are no wildcard characters. All characters are 
                    matched literally (e.g., searching for <b>Begins With</b>
                    <font face="courier">NAT2*</font> will match <i>NAT2*5 Allele</i>
                     but not <i>NAT2 Gene</i>).</li>
                <li>Do not use quotes - they will be searched for 
                   literally, as characters to be matched.</li>
                <li>Searching for multiple words does not search on each
                   word separately. To match, all words have to be found 
                   in the same order you provided. For example, if you do
                   a <b>Contains</b> search on <font face="courier">Melanoma Corneal</font>,
                   no results will be returned, but if you search on
                   <font face="courier">Corneal Melanoma</font> you get
                   the detail page for <i>Corneal Melanoma</i>.</li>
              </ul>
            </li>
            <li><b>Match method radio buttons</b> select how your 
                search string will be matched.
              <ul>
                <li><b>Exact Match</b> button is the default: Only terms 
                    or codes that are identical will match.</li>
                <li><b>Begins With</b> button can be selected to find all 
                    terms or codes that start with the words or characters 
                    you enter.</li>
                <li><b>Contains</b> button will search for what you enter 
                    anywhere within a term or code (e.g., 
                    <font face="courier">carcinoma</font> will match 
                    <i>adenocarcinoma)</i>.</li>
                <li>Concept Codes will only match if they exactly match 
                    what you enter, even if you select <b>Begins With</b>
                    or <b>Contains</b> buttons.</li>
              </ul>
            </li>
            <li><b>Match target radio buttons</b> select what category 
                of concept information is searched 
              <ul>
                <li><b>Name/Code</b> button is the default: Search text 
                    is matched to a concept's preferred name, synonyms, 
                    acronyms, or codes. Unless stated otherwise, all 
                    search examples in this Help page use the default 
                    name/code search.</li>
                <li><b>Property</b> button will match to other direct 
                    property attributes of a concept, such as 
                    definitions.</li>
                <li><b>Relationship</b> button will return concepts 
                    that have relationships to concepts that match by 
                    name/code (e.g., an exact relationship search on 
                    <font face="courier">toe</font> does not return 
                    the concept <i>toe</i>, but does return <i>toenail</i> 
                    and other related concepts).</li>
              </ul>
            </li>
            <li><b>Search</b> button starts a search.</li>
            <li>The <b>"?"</b> button brings you to this Search section 
                of the Help file.</li>
          </ul>

          <p>
            If you are in the NCI Term Browser home page, all 
            currently selected terminologies on the <b>Terminologies</b> 
            tab will be searched. When you are in the pages of a 
            particular terminology, only that terminology will be 
            searched. The banner to the left of the Search box tells 
            you which terminology environment you are in.
          </p>
        </div>

        <%-- -------------------------------------------------------------- --%>        
        <div class="textbody">
          <br/>
          <table width="720px" cellpadding="0" cellspacing="0" border="0">
            <tr>
              <td><a name="searchResults"><b>Search Results</b></a></td>
              <td align="right">
                <a href="#"><img src="<%=arrowImage%>" 
                  width="16" height="16" border="0" alt="top" /></a>
              </td>
            </tr>
          </table>
          <p>
            When a terminologies search is run, <b>Search Results</b>
            are displayed by concept preferred name. If you are searching 
            multiple terminologies, a right-hand column displays the 
            corresponding name  of the terminology for each matching 
            concept. If there is only one match, the concept details 
            page is shown directly without first listing results, unless 
            the matching concept is in the NCI Metathesaurus (because 
            this links to the separate NCIm Browser). Some details: 
          </p>
          <ul>
            <li>All matching concepts are returned.</li>
            <li>Results are listed from best match to weakest. For 
              example, a <b>Contains</b> search on <font face="courier">Bone</font>
              returns <i>Bone</i> at the top, followed by concepts 
              with two word matches (e.g., <i>Flat Bone</i>), followed 
              by concepts whose terms have more non-<i>Bone</i> content.</li> 
            <li>When a terminology search is run, it looks for matches 
              not only in the preferred name of concepts but also among 
              the synonyms of the preferred name as listed in the concept 
              details page.  For this reason, the match will often be 
              to synonyms or codes only visible on the concept details 
              page (e.g., searching <b>Begins With</b> 
              <font face="courier">melanoma</font> will show <i>MIA</i>
              in the results list because that concept contains a 
              synonym of <i>melanoma inhibitory activity</i>.)</li> 
            <li>If there are too many to show on one page, you can page 
              through the results, with a default of 50 per page. To 
              change the default number, use the <b>Show results per page</b> 
              drop-down menu at the bottom of the results page.</li>
            <li>If a concept is retired, this will be indicated by the
              phrase "(Retired Concept") next to the name of the concept
              in the Results listing.</li>
            <li>Click on the preferred name to see a concept's details.</li>
          </ul>
        </div>

        <%-- -------------------------------------------------------------- --%>        
        <div class="textbody">
          <br/>
          <table width="720px" cellpadding="0" cellspacing="0" border="0">
            <tr>
              <td><a name="searchingOther"><b>Searching Other/Multiple Versions of a Terminology</b></a></td>
              <td align="right">
                <a href="#"><img src="<%=arrowImage%>" 
                  width="16" height="16" border="0" alt="top" /></a>
              </td>
            </tr>
          </table>
          <p>
            It is possible in some instances to search non-production 
            or multiple versions of a particular terminology from the 
            <b>Terminologies</b> tab of the Term Browser. 
          </p>
          <ul>
            <li>The terminologies for which this is possible will have a 
              link to the right of them that says <b>show other versions</b>. 
              Click on that link to see the non-production versions of 
              the terminology that are available.</li>  
            <li>These versions, which will appear directly underneath 
              the production version, will have a check box to the left; 
              click on that check box and the non-production version(s) 
              will be included in the search.</li>
          </ul>
          <p>
            Some details about searching other or multiple versions of a
            particular terminology from the <b>Terminologies</b> tab:
          </p>
          <ul>
            <li>The <b>Select All</b> button will not select non-production 
              versions of a terminology unless the <b>show other versions</b>
              link has already been clicked and the non-production version(s) 
              are visible.</li>
            <li>However, the <b>Clear</b> button will unselect the check 
              box of a non-production version of a terminology even if 
              it is hidden from view.</li>
            <li>The <b>Search Results</b> will also indicate in parentheses 
              under <b>Vocabulary</b> the terminology version the result 
              came from.</li>
          </ul>
        </div>          

        <%-- -------------------------------------------------------------- --%>        
        <div class="textbody">
          <br/>
          <table width="720px" cellpadding="0" cellspacing="0" border="0">
            <tr>
              <td><a name="advancedSearch"><b>Advanced Search</b></a></td>
              <td align="right">
                <a href="#"><img src="<%=arrowImage%>" 
                  width="16" height="16" border="0" alt="top" /></a>
              </td>
            </tr>
          </table>
          <p>
            The <b>Advanced Search</b> link, which appears under the 
            Search Box on terminology source home pages, Concept Details 
            pages (<a href="#conceptDetails">see below</a>) or on mapping 
            set home pages (<a href="#mappingSets">see below</a>), leads 
            to a separate page. The options and selections are similar 
            to those described for the <b>Search Box</b>, with the following 
            key differences:
          </p>
          <ul>
            <li><b>Code</b> and <b>Name</b> are searched separately.</li>
            <li><b>Source</b>: Some terminologies include content from 
              more than one contributing source.  You can choose to 
              limit search to one contributing source, or stay with 
              the default of all, using the pull down menu.</li>
            <li><b>Property</b> search offers a drop-down list 
              of all specific properties associated with the home 
              terminology source, so that they can be selected and 
              searched individually.</li>
            <li><b>Relationship</b> search offers a drop-down list 
              of all specific relationships associated with the home 
              terminology source, so that they can be selected and 
              searched individually.</li> 
          </ul>
        </div>

        <%-- -------------------------------------------------------------- --%>        
        <div class="textbody">
          <br/>
          <table width="720px" cellpadding="0" cellspacing="0" border="0">
            <tr>
              <td><a name="conceptDetails"><b>Concept Details</b></a></td>
              <td align="right">
                <a href="#"><img src="<%=arrowImage%>" 
                  width="16" height="16" border="0" alt="top" /></a>
              </td>
            </tr>
          </table>
          <p>
            Detailed information on a selected concept is grouped 
            and shown on several related pages: 
          </p>
          <ul>
            <li>
              Tabbed information gives the concept's meaning, labels, 
                  and direct relationships:
              <ul>
                <li><b>Terms &amp; Properties</b>: Gives definitions, 
                  synonyms, abbreviations, codes, and other information.</li>
                <li><b>Synonym Details</b>: Shows each term or abbreviation 
                  with the corresponding term type, source, and code 
                  (for sources that have them).</li>
                <li><b>Relationships</b>: Shows how other concepts are 
                  directly related to this concept as parents, children, 
                  or in other ways.  Note: For some sources, the browser 
                  adds inverse relationships not asserted bidirectionally 
                  in the source data file; this is often useful for 
                  navigational purposes, but might be confusing, and 
                  will be evaluated for adjustments in future releases.</li>
                <li><b>Mappings</b>: Gives the mapping relationships, 
                  if any, to other vocabularies.</li>
                <li><b>View All</b>: Combines all of the concept details 
                  on a single page.</li>
              </ul>
            </li>
            <li><b>View in Hierarchy</b>: Click the link to see where 
              the concept is found within the terminology hierarchy. 
              Concepts are often found in several different places. 
              The focus concept will be bold, underlined, and 
              colored red.</li>
            <li>A <b>Suggest changes</b> link appears in the upper 
              right of all concept details pages of sources for 
              which NCI can handle such requests. It goes to a 
              special suggestion page with source and concept 
              code filled in.</li>
            <li>For information about placing concepts into the 
              <b>Cart</b> (using the <b>Add to Cart</b> link found 
              on the Concept Details pages) in order to <b>Export</b>
              concept information to a file, please 
              <a href="#cartAndExport">see below</a>.</li>
          </ul>
        </div>

        <%-- -------------------------------------------------------------- --%>        
        <div class="textbody">
          <br/>
          <table width="720px" cellpadding="0" cellspacing="0" border="0">
            <tr>
              <td><a name="additionalLinks"><b>Additional Links for Individual Terminologies</b></a></td>
              <td align="right">
                <a href="#"><img src="<%=arrowImage%>" 
                  width="16" height="16" border="0" alt="top" /></a>
              </td>
            </tr>
          </table>
          <ul>
            <li>
              Click on the <b>Hierarchy link</b>, at the top of pages 
              for sources that support it, to bring up a separate 
              window showing the full terminology hierarchy. Some details:
              <ul>
                <li>At first, only the top level nodes of the hierarchy 
                  are shown.</li>
                <li>At each level, concepts are listed alphabetically by 
                  concept preferred name.</li>
                <li>Browse through the levels by clicking on the + next 
                  to each concept.</li>
                <li>Click on the concept name itself to see the concept's 
                  details in the main browser window.</li>
              </ul>
            </li>
            <li>Click on the <b>Maps</b> link to bring up a list of 
              the mapping data sets of the terminology, which can 
              then be searched. Terminologies that do not have mappings 
              will not have the <b>Maps</b> link (see the information 
              on the <b>Mappings</b> tab below).</li>
            <li>Click on the <b>Value Sets</b> link at the top of the 
              page to bring up a separate window showing a hierarchy 
              of the value sets of the terminology.  Clicking on the 
              name of a value set will bring up that value set's home 
              page.  Terminologies that do not have value sets will 
              not have the <b>Value Sets</b> link (see the information 
              on the <b>Value Sets</b> tab below).</li>
          </ul>      
        </div>

        <%-- -------------------------------------------------------------- --%>        
        <div class="textbody">
          <br/>
          <table width="720px" cellpadding="0" cellspacing="0" border="0">
            <tr>
              <td><a name="cartAndExport"><b>Cart and Export Functionality</b></a></td>
              <td align="right">
                <a href="#"><img src="<%=arrowImage%>" 
                  width="16" height="16" border="0" alt="top" /></a>
              </td>
            </tr>
          </table>
          <p>
            The <b>Cart</b> and <b>Export</b> functionality is available 
            from within the detailed <b>Concept Page</b> of any concept 
            that is selected for viewing. This is true no matter whether 
            the concept was selected from the <b>Terminologies</b> tab, 
            the <b>Value Sets</b> tab, or the <b>Mappings</b> tab of the 
            Term Browser. The purpose of the <b>Cart</b> and <b>Export</b> 
            functionality is to allow the user to collect and save one 
            or more concepts so that they can be exported to a file.
            The following instructions apply to the <b>Cart</b> and 
            <b>Export</b> functionality.
          </p>
          <ul>
            <li>In order to place a concept whose details you are 
              viewing into the <b>Cart</b>, click on the <b>Add to Cart</b>
              link on the right hand side of the concept details (note
              that the <b>Add to Cart</b> link appears on all of the 
              tabbed pages: <b>Terms &amp; Properties</b>, <b>Relationships</b>, 
              <b>Synonym Details</b>, <b>Mappings</b>, and <b>View All</b>).</li>
            <li>Once you have placed a concept into the <b>Cart</b>, a 
              link called <b>Cart</b> will appear underneath the Search 
              Box at the top of the page, next to <b>Visited Concepts</b>; 
              from this point forward, this link will remain there 
              regardless of the Term Browser page you are on (provided 
              that the <b>Cart</b> is not emptied; if the <b>Cart</b> 
              is emptied, the link will disappear).</li>
            <li>
               If you click on the <b>Cart</b> link, you will be taken 
               to the <b>Cart</b> page:
               <ul>
                 <li>The number next to the word <b>Cart</b> on the 
                   left indicates the number of concepts you have 
                   placed in the cart.</li>
                 <li>The concepts you have placed in the cart are 
                   listed by concept <b>Name</b> (on the left) and 
                   concept <b>Vocabulary</b> (on the right).</li>
                 <li>If you want to <b>remove</b> a particular concept 
                   from the <b>Cart</b>, check the box on the left of 
                   the concept's name and then click <b>Remove</b>. 
                   This will also work for removing several selected 
                   concepts at once.</li>
               </ul>
            </li>
            <li>
              Exporting of the concepts you have accumulated on the 
              <b>Cart</b> page can be done in two formats:
              <ul>
                <li><b>Export XML</b> will export cart contents in 
                  XML format.</li>
                <li><b>Export CSV</b> (Comma Separated Values) will 
                  generate a list of cart contents in CSV format 
                  readable from Excel.</li>
                <li>If a concept is available in more than one version 
                  of a terminology, the user is allowed to choose 
                  between the default production version of the 
                  terminology and other available versions.</li>
              </ul>
            </li>
          </ul>
        </div>
        
        <%-- -------------------------------------------------------------- --%>        
        <div class="textbody">
          <br/>
          <table width="720px" cellpadding="0" cellspacing="0" border="0">
            <tr>
              <td><a name="valueSetsTab"><b>Value Sets Tab</b></a></td>
              <td align="right">
                <a href="#"><img src="<%=arrowImage%>" 
                  width="16" height="16" border="0" alt="top" /></a>
              </td>
            </tr>
          </table>
          <p>
            The <b>Value Sets</b> tab of the NCI Term Browser home 
            page lists the value sets, which are flat lists of member 
            concepts. We hope to extend what can be offered in the 
            future, but this currently offers the basic information. 
            Value sets are available in two different views:
          </p>
          <ul>
            <li>
              <b>Standards View</b> is the default view, and shows 
                the available value sets listed by the authority 
                responsible for their content.
              <ul> 
                <li>Authorities, and value sets for each, are listed 
                  alphabetically.  Some value sets are defined by 
                  combining component value sets, displayed as child 
                  entries.</li>
                <li>Browse through the levels by clicking on the + 
                  next to entries having lower levels.</li>
                <li>If you select one of the top-level authority 
                  entries, such as CDISC or FDA, you will go to 
                  a concept for that authority in the Terminology 
                  Value Set terminology, a small new NCI terminology 
                  used to help organize value set information.</li>
              </ul>
            </li>
          </ul>
          <ul>
            <li>
              <b>Terminology View</b> shows available value sets 
                listed by the terminologies their values come from.
              <ul> 
                <li>Browse through the levels by clicking on the + 
                  next to each terminology.</li>
                <li>If you select one of the top-level terminologies, 
                  you will go to the home page for that terminology.</li>
              </ul>
            </li>
          </ul>
          <p>
            Select a value set name, in either view, to go to that 
            value set's home page.  The home page gives a brief 
            description, and three buttons for viewing it:
          </p>
          <ul>
            <li>
              <a name="valueBullet"><b>Values</b></a>
              lists all concepts contained in this 
                value set, using the production version of each 
                participating terminology.  The listing shows:
              <ul> 
                <li>Name</li>
                <li>Description</li>
                <li>Concept Domain</li>
                <li>Sources</li>
                <li>
                  Concepts
                  <ul>
                    <li>The first 50 concepts of the value set are 
                      listed. Use the <b>Show &lt;n&gt; results per page</b>
                      drop-down menu at the bottom of the page to 
                      display up to 500 concepts at once.</li>
                    <li>Each concept listed shows its <b>code</b>, 
                      <b>name</b>, <b>vocabulary</b> and <b>namespace</b>.</li>
                    <li>Select the <b>Code</b> link (e.g., C81209) 
                      to display the concept details. See the 
                      <a href="#terminologies">Terminologies Tab</a>
                      help section for information on the concept 
                      details display.</li>
                  </ul>
                </li>
              </ul>
              <br/>There are two options for exporting all of the 
                values from the displayed set:<br/><br/>
              <ul>
                <li><b>Export XML</b> will open or save the value set 
                  in a tagged XML format.</li>
                <li><b>Export CSV</b> (Comma Separated Values) will 
                  open or save the value set in a delimited text format.</li>
              </ul>
              <br/>
            </li>
            <li>
              <b>Versions</b> allows you to select particular versions 
                of each terminology participating in this value set. 
                For example, selecting the CDISC ADaM Terminology and 
                then the Versions button will show the currently available 
                versions of NCI Thesaurus. The version information includes:
              <ul>
                <li>Coding Scheme</li>
                <li>Version</li>
                <li>Tag</li>
              </ul>
              <br/>The <b>Continue</b> button uses the selected version(s) 
                to select the values contained in the set. See help section 
                on <a href="#valueBullet">Values</a> above for information 
                on the details contained there.
              <br/><br/>
            </li>
            <li>
              <b>XML Definition</b> opens or saves the logical definition 
              of the value set in LexGrid XML format. This definition 
              contains the criteria used to find the values in the 
              value set, using concept codes, logical hierarchies, 
              or other criteria sometimes combining multiple parts. 
            </li>
          </ul>
        </div>

<hr/> <%-- DYEE Begin --%>
<hr/> <%-- DYEE Middle --%>
<hr/> <%-- DYEE End --%>

        <p class="textbody">
          <table width="720px" cellpadding="0" cellspacing="0" border="0"><tr>
            <td><h2><A NAME="viewhierarchy">View Hierarchy</A></h2></td>
            <td align="right"><a href="#"><img src="<%= request.getContextPath() %>/images/up_arrow.jpg" width="16" height="16" border="0" alt="top" /></a></td>
          </tr></table>
          Click on the <b>View Hierarchy</b> link, at the top of
          pages for sources that support it, to bring up a separate
          window showing the full source hierarchy. Some details:
          <ul>
            <li>At first, only the top level nodes of the hierarchy
              are shown.</li>
            <li>At each level, concepts are listed alphabetically
              by concept preferred name.</li>
            <li>Browse through the levels by clicking on the + next
              to each concept.</li>
            <li>Click on the concept name itself to see the concept's
              details in the main browser window.</li>
          </ul>
        </p>

        <p class="textbody">
          <table width="720px" cellpadding="0" cellspacing="0" border="0"><tr>
            <td><h2><A NAME="otherlinks">Other Links</A></h2></td>
            <td align="right"><a href="#"><img src="<%= request.getContextPath() %>/images/up_arrow.jpg" width="16" height="16" border="0" alt="top" /></a></td>
          </tr></table>
          Several other browser links appear in the upper parts of
            pages to which they apply:
          <ul>
            <li><b>Visited Concepts</b> will appear under the Search box
              once you start visiting individual concept pages.  It
              shows the concept name and source for each concept
              visited, with most recent concepts at the top.</li>
            <li><b>Term Suggestion</b> appears on the initial pages of
              sources for which NCI can handle such requests, and
              goes to a special suggestion page.  This link is lost
              when <b>Visited Concepts</b> start, but is still available
              on the <b>Quick Links</b> pull-down menu.</li>
            <li><b>Suggest changes</b> (to this concept) link appears in the
              upper right of all concept details pages of sources
              for which NCI can handle such requests.  It goes to
              a special suggestion page with source and concept code
              filled in.</li>
            <li><b>Subsets</b> on NCI Thesaurus (NCIt) pages links to a
              special page to read about and link to
              <a href="<%= subsetsUrl %>">NCI Thesaurus Subsets</a>
              The text on this page provides additional
              information. </li>
          </ul>
        </p>

        <p class="textbody">
          <table width="720px" cellpadding="0" cellspacing="0" border="0"><tr>
            <td><h2><A NAME="knownissues">Known Issues</A></h2></td>
            <td align="right"><a href="#"><img src="<%= request.getContextPath() %>/images/up_arrow.jpg" width="16" height="16" border="0" alt="top" /></a></td>
          </tr></table>
          This browser addresses many of the issues identified in the
          NCIt Browser on which it is based. We are still working on
          remaining issues of data, functionality and documentation,
          which include the following:
          <ul>
            <li><b>Data</b>: Some source data are not being properly loaded
              into the LexBIG database, or are not being correctly
              processed for presentation and search.  We are addressing
              such issues through ongoing changes in data file formats
              and loading, and expect significant improvements to be
              visible even before the next browser release.</li>
            <li><b>Functionality</b>: The default scoring for search
              matches will continue to be improved, especially in
              harmonizing the currently very uneven scoring of matches
              from multiple sources. The often large performance penalty
              for searching NCIm together with other sources also needs
              to be fixed.  Some user-settable options should be part
              of a forthcoming Advanced Search page. Performance in
              some features, such as the display of concept placement
              in source hierarchies, still needs major improvement. Single
              character Contains searches or Begins With are not supported
              by the browser at this time but will be addressed in a future
              release.</li>
            <li><b>Documentation</b>: Online and standalone documentation
              are still under development.</li>
            <li>For the latest updates of known issues,
              <a href="https://wiki.nci.nih.gov/display/EVS/NCI+Term+Browser+2.0+Release+Notes" target="_blank"> see NCI Term Browser 2.0 Release Notes</a>.</li>
          </ul>
          Please report any bugs or suggestions using the browser's
          <a href="<%= contactUsUrl %>">Contact Us</a> page.
        </p>

        <p class="textbody">
          <table width="720px" cellpadding="0" cellspacing="0" border="0"><tr>
            <td><h2><A NAME="additionalinfo">Additional Information</A></h2></td>
            <td align="right"><a href="#"><img src="<%= request.getContextPath() %>/images/up_arrow.jpg" width="16" height="16" border="0" alt="top" /></a></td>
          </tr></table>
          Additional information about EVS can be found on the
          <a href="http://evs.nci.nih.gov/" target="_blank">EVS Web</a> and
          <a href="https://wiki.nci.nih.gov/display/EVS/EVS+Wiki" target="_blank">EVS Wiki sites</a>.
          Several journal articles describe some aspects in greater detail; these are listed in the
          <a href="http://evs.nci.nih.gov/aboutEVS" target="_blank">About EVS</a>
          page on the EVS Web site.
        </p>
        <br>
        <%@ include file="/pages/templates/nciFooter.jsp" %>
      </div> <!-- end pagecontent -->
    </div> <!-- end main-area -->
    <div class="mainbox-bottom"><img src="<%=basePath%>/images/mainbox-bottom.gif" width="745" height="5" alt="Mainbox Bottom" /></div>
    <!-- end Main box -->
  </div> <!-- end center-page -->
</f:view>
</body>
</html>