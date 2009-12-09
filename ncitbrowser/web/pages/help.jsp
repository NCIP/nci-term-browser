<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ page contentType="text/html;charset=windows-1252"%>
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
  <%
    String contactUsUrl = request.getContextPath() + "/pages/contact_us.jsf";
    String subsetsUrl = request.getContextPath() + "/pages/subset.jsf";
  %>
  <%@ include file="/pages/templates/header.jsp" %>
  <div class="center-page">
    <%@ include file="/pages/templates/sub-header.jsp" %>
    <!-- Main box -->
    <div id="main-area">
    
<%          
String help_dictionary = (String) request.getSession().getAttribute("dictionary");
if (help_dictionary == null) {
%>
   <%@ include file="/pages/templates/content-header-termbrowser.jsp" %>
<%   
}
else if (help_dictionary.compareTo("NCI Thesaurus") == 0) {
%>
   <%@ include file="/pages/templates/content-header.jsp" %>
<%
} else {
%>
   <%@ include file="/pages/templates/content-header-other.jsp" %>
<%   
}
%>

      <!-- Page content -->
      <div class="pagecontent">
        <!-- ======================================= -->
        <!--                 HELP CONTENT            -->
        <!-- ======================================= -->
        <div class="texttitle-blue">Help</div>
        <p class="textbody">
          <A HREF="#introduction">Introduction</A><br>
          <A HREF="#sources">Sources</A><br>
          <A HREF="#searchhelp">Search</A><br>
          <A HREF="#conceptdetails">Concept Details</A><br>
          <A HREF="#viewhierarchy">View Hierarchy</A><br>
          <A HREF="#subsets">Subsets</A><br>
          <A HREF="#knownissues">Known Issues</A><br>
          <A HREF="#additionalinfo">Additional Information</A>
        </p>
        <p class="textbody">
          <table width="100%" cellpadding="0" cellspacing="0" border="0"><tr>
            <td><h2><A NAME="introduction">Introduction</A></h2></td>
            <td align="right"><a href="#"><img src="<%= request.getContextPath() %>/images/up_arrow.jpg" width="16" height="16" border="0" alt="top" /></td>
          </tr></table>
          <b>NCI Term Browser</b> provides a consistent, user-friendly tool to 
          browse and search all of the biomedical terminologies hosted 
          by NCI EVS, including both NCI Thesaurus (NCIt) and the NCI 
          Metathesaurus (NCIm), which itself includes more than 70 
          terminologies,  These terminologies normally represent each
          specific meaning, such as melanoma, lung, orchemotherapy, by
          a distinct <i>concept</i> with a unique, permanent <i>code</i>. Each concept 
          normally provides additional information such as a preferred 
          name, other terms and codes, definitions, and relationships 
          with other concepts. Concepts are normally organized in 
          parent-child hierarchies from very broad top concepts down 
          to the most specific subcategories.  All browsing and searching 
          in this browser reflects this concept-based view of terminology. 
          For information on other EVS browsers, file formats, and computer 
          application access, see the 
          <a href="http://evs.nci.nih.gov/" target="_blank" alt="EVS">EVS web site</a>. 
        </p>
        <p class="textbody">
          <b>Get in touch</b> to get help or offer suggestions using the 
          browser's <a href="<%= contactUsUrl %>">Contact Us</a> page. 
        </p>
        <p class="textbody">
          This help file provides basic information about how to use 
          the NCI Term Browser effectively as well as links to additional 
          information elsewhere. The following typeface font conventions 
          are used for describing search and the browser interface: 
          
          <ul>
            <li><b>Bold</b>: Browser links, buttons, page tabs, and drop-down boxes. 
            <li><font face="courier">Fixed Width</font>: Search strings. 
            <li><i>Italics</i>: Concept terms. 
          </ul>
        </p>

        <p class="textbody">
          <table width="100%" cellpadding="0" cellspacing="0" border="0"><tr>
            <td><h2><A NAME="sources">Sources</A></h2></td>
            <td align="right"><a href="#"><img src="<%= request.getContextPath() %>/images/up_arrow.jpg" width="16" height="16" border="0" alt="top" /></td>
          </tr></table>
            The NCI Term Browser home page lists alphabetically the 
            terminology sources that are available.  Click on a source 
            name to go to its home page, or click on a source's check 
            box to include it in a search from the NCI Term Browser 
            home page.  Below the sources list are three buttons:
            
            <ul>
              <li><b>Select All</b> includes all sources for searching, leaving their check boxes checked.</li>
              <li><b>Clear</b> unselects all sources, unchecking their check boxes.</li>
              <li><b>Search</b> starts a search in the same way as the Search button in the search box.</li>
            </ul>
            
            The <b>Sources</b> link at the top of the NCI Term Browser home page 
            gives information about all sources, including the source 
            descriptions appearing on each source's home page. 
        </p>

        <p class="textbody">
          <table width="100%" cellpadding="0" cellspacing="0" border="0"><tr>
            <td><h2><A NAME="searchhelp">Search</A></h2></td>
            <td align="right"><a href="#"><img src="<%= request.getContextPath() %>/images/up_arrow.jpg" width="16" height="16" border="0" alt="top" /></td>
          </tr></table>

          <b>The Search box</b>, in the upper right corner of most browser 
          pages, lets you enter all or part of what you are looking 
          for and how you want to search for it. Some details: 
          
          <ul>
            <li><b>Text Box</b>: Enter the exact string of characters you want to search for.
              <ul>
                <li>Search is not case sensitive (e.g., <font face="courier">aids</font> will match
                  <i>aids</i>, <i>Aids</i>, and <i>AIDS</i>).</li>
                <li>There are no wildcard characters. All characters
                  are matched literally (e.g., searching for <b>Begins With</b>
                  <font face="courier">NAT2*</font> will match <i>NAT2*5 Allele</i> but not <i>NAT2 Gene</i>).</li>
                <li>Do not use quotes - they will be searched for literally,
                  as characters to be matched.</li>
                <li>Searching for multiple words does not search on each
                  word separately. To match, all words have to be found in
                  the same order you provided. For example, if you do a
                  <b>Contains</b> search on <font face="courier">Melanoma Corneal</font> no results will be
                  returned, but if you search on <font face="courier">Corneal Melanoma</font> you get
                  the detail page for <i>Corneal Melanoma</i>.</li>
              </ul>
            </li>
            <li><b>Match method radio buttons</b> select how your search string
                will be matched.
              <ul>
                <li><b>Exact Match</b> is the default: Only terms or codes that
                  are identical will match.</li>
                <li><b>Begins With</b> can be selected to find all terms or codes
                  that start with the words or characters you enter.</li>
                <li><b>Contains</b> will search for what you enter anywhere
                  within a term or code (e.g., <font face="courier">carcinoma</font> will match
                  <i>adenocarcinoma</i>).</li>
                <li>Concept Codes will only match if they exactly match 
                  what you enter, even if you select <b>Begins With</b> or
                  <b>Contains</b>.</li>
              </ul>
            </li>
            <li><b>Match target radio buttons</b> select what category of
                concept information is searched
              <ul>
                <li><b>Name/Code</b> is the default: Search text is matched to
                  a concept's preferred name, synonyms, acronyms, or 
                  codes. Unless stated otherwise, all search examples 
                  in this Help page use the default name/code search.</li>
                <li><b>Property</b> will match to other direct property 
                  attributes of a concept, such as definitions.</li>
                <li><b>Relationship</b> will return concepts that have 
                  relationships to concepts that match by name/code
                  (e.g., an exact relationship search on <font face="courier">toe</font> does
                  not return the concept <i>toe</i>, but does return <i>toenail</i>
                  and other related concepts).</li>
              </ul>
            </li>
            <li><b>Search</b> button starts a search.</li>
            <li><b>"?"</b> button takes you to this Search section of the Help file.</li>
          </ul>
        </p>
        <p class="textbody">
          If you are in the main NCI Term Browser pages, all currently
          selected sources will be searched.  If you are in the pages
          of a particular source, only that source will be searched.
          The banner to the left of the Search box tells you which
          source environment you are in.
        </p>
        <p class="textbody">
          <b>Search results</b> are displayed by concept preferred name. 
          If you are searching multiple sources, a right-hand column
          displays the source of each matching concept.  If there is
          only one match, the concept details page is shown directly
          without first listing results, unless the matching concept
          is in the NCI Metathesaurus (because this links to the
          separate NCIm Browser). Some details: 
          <ul>
            <li>All matching concepts are returned.</li>
            <li>Results are listed from best match to weakest. For
              example, a <b>Contains</b> search on <font face="courier">Bone</font> returns <i>Bone</i> at
              the top, followed by concepts with two word matches
              (e.g., <i>Flat Bone</i>), followed by concepts whose terms
              have more non-<i>Bone</i> content. Ranking of results from
              multiple sources is still uneven, so that, e.g., 
              exact matches from one source may appear after some
              weaker matches from other sources; we are working
              to fix this problem.</li>
            <li>The match will often be to synonyms or codes only
              visible on the concept details page (e.g., searching
              <b>Begins With</b> <font face="courier">melanoma</font> will show <i>Corneal Melanoma</i> in
              the results list because that concept contains a 
              synonym of <i>Melanoma of the Cornea</i>.) A future release
              will show these matches in the results window.</li>
            <li>If there are too many to show on one page, you can
              page through the results with a default of 50 per page.
              To change the default number, use the <b>Show results per
              page</b> drop-down menu at the bottom of the results page.</li>
            <li>Concepts whose status is unusual (e.g., retired
              or obsolete) show their status in parentheses in
              the results listing.</li>
            <li>Click on the preferred name to see a concept's
              details.</li>
          </ul>
        </p>

        <p class="textbody">
          <table width="100%" cellpadding="0" cellspacing="0" border="0"><tr>
            <td><h2><A NAME="conceptdetails">Concept Details</A></h2></td>
            <td align="right"><a href="#"><img src="<%= request.getContextPath() %>/images/up_arrow.jpg" width="16" height="16" border="0" alt="top" /></td>
          </tr></table>

          Detailed information on the selected concept is grouped and shown on several related pages:
          <ul>
            <li>Tabbed information gives the concept’s meaning, labels, and direct relationships:</li>
            <ul>
              <li><b>Terms & Properties:</b> Gives definitions, synonyms, abbreviations, codes, and other information.</li>
              <li><b>Relationships:</b> Shows how other concepts are directly related to this concept as  parents, children, or in other ways.</li>
              <li><b>Synonym Details:</b> For each term or abbreviation, shows its term type, source, and code (for outside sources that have them).</li>
              <li><b>View All:</b> Combines all of the above information on a single page.</li>
            </ul>
            <li><b>View in Hierarchy:</b> Click the button to see where the concept is found within the NCI Thesaurus hierarchy. Concepts are often found in several different places. The focused concept will be bold, underlined, and colored red.</li>
            <li><b>View History:</b> Click the button to view a history of edit actions on this concept, including dates and reference concepts involved in actions that split or merge concepts.</li>
          </ul>
        </p>

        <p class="textbody">
          <table width="100%" cellpadding="0" cellspacing="0" border="0"><tr>
            <td><h2><A NAME="viewhierarchy">View Hierarchy</A></h2></td>
            <td align="right"><a href="#"><img src="<%= request.getContextPath() %>/images/up_arrow.jpg" width="16" height="16" border="0" alt="top" /></td>
          </tr></table>

          Click on the <b>View Hierarchy</b> link at the top of the page to bring up a separate window showing the NCI Thesaurus hierarchy.  Some details:
          <ul>
            <li>At first, only the top level nodes of the hierarchy are shown.</li>
            <li>At each level, concepts are listed alphabetically by concept preferred name.</li>
            <li>Browse through the levels by clicking on the + next to each concept.</li>
            <li>Click on the concept name itself to see the concept’s details in the main browser window.</li>
          </ul>
        </p>

        <p class="textbody">
          <table width="100%" cellpadding="0" cellspacing="0" border="0"><tr>
            <td><h2><A NAME="subsets">Subsets</A></h2></td>
            <td align="right"><a href="#"><img src="<%= request.getContextPath() %>/images/up_arrow.jpg" width="16" height="16" border="0" alt="top" /></td>
          </tr></table>

          Click on the <a href="<%= subsetsUrl %>">Subsets</a> link at the top of the page to read about and link to NCI Thesaurus Subsets. The text on this page provides additional information.
        </p>

        <p class="textbody">
          <table width="100%" cellpadding="0" cellspacing="0" border="0"><tr>
            <td><h2><A NAME="knownissues">Known Issues</A></h2></td>
            <td align="right"><a href="#"><img src="<%= request.getContextPath() %>/images/up_arrow.jpg" width="16" height="16" border="0" alt="top" /></td>
          </tr></table>

          Most known issues are limits to functionality and documentation that we are working to address for release 2.  These include the following:
          <ul>
            <li>Some searches returning thousands of results hit internal limits in the LexEVS 4.2/LexBIG 2.3
              server. The browser detects this failure and falls back to a safe search that will return only
              some of the matching concepts (only full-word matches, normally the best), warning the user about
              the problem. This problem should be fixed with the LexEVS 5.0 release in June 2009.</li>
            <li>The default scoring for search matches will continue to be improved, and some user-settable options should be part of the forthcoming Advanced Search page.</li>
            <li>Two characters - "&" and "<" - fail normal searching because they are stored and processed in special
              ways.  An ampersand ("&") can mostly be found using its encoded "&amp;amp;" or double-encoded "&amp;amp;amp;" forms;
              less-than ("<") by using "&amp;lt;" or "&amp;amp;lt;". This problem should be fixed with the LexEVS 5.0 release in June 2009.</li>
            <li>Online and standalone documentation are still under development.</li>
            <li>For the latest updates of known issues, <a href="https://wiki.nci.nih.gov/display/EVS/NCI+Thesaurus+Browser+1.0.1+Release+Notes" target="_blank" alt="NCI Thesaurus Browser 1.0 Release Notes"> see NCI Thesaurus Browser 1.0.1 Release Notes</a>.
          </ul>
          Please report any bugs or suggestions using the browser’s <a href="<%= contactUsUrl %>">Contact Us</a> page.<br>
          Suggestions to add a new concept or make changes to an existing concept can also be made using the <b>Term Suggestion</b> link below the Search box or the <b>Suggest changes to this concept</b> link in the upper right of all concept details pages.
        </p>

        <p class="textbody">
          <table width="100%" cellpadding="0" cellspacing="0" border="0"><tr>
            <td><h2><A NAME="additionalinfo">Additional Information</A></h2></td>
            <td align="right"><a href="#"><img src="<%= request.getContextPath() %>/images/up_arrow.jpg" width="16" height="16" border="0" alt="top" /></td>
          </tr></table>

          Additional information about NCIt and EVS can be found on the <a href="http://evs.nci.nih.gov/" target="_blank" alt="EVS">EVS Web</a> and <a href="https://wiki.nci.nih.gov/display/EVS/EVS+Wiki" target="_blank" alt="EVS Wiki sites">EVS Wiki sites</a>.
          </br>Several journal articles describe NCIt in greater detail; these are listed in the <a href="http://evs.nci.nih.gov/aboutEVS" target="_blank" alt="About EVS">About EVS</a> page on the EVS Web site.
        </p>
        <br>
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