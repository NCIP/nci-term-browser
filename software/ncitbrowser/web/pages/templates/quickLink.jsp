<%--L
  Copyright Northrop Grumman Information Technology.

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
L--%>

<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Vector"%>
<%@ page import="java.util.HashSet"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Set"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="gov.nih.nci.evs.browser.utils.DataUtils" %>
<%@ page import="gov.nih.nci.evs.browser.properties.PropertyFileParser" %>
<%@ page import="gov.nih.nci.evs.browser.properties.NCItBrowserProperties" %>
<%@ page import="gov.nih.nci.evs.browser.bean.DisplayItem" %>
<%@ page import="gov.nih.nci.evs.browser.bean.*" %>
<%@ page import="gov.nih.nci.evs.browser.utils.*" %>
<%@ page import="org.LexGrid.concepts.Concept" %>
<%@ page import="org.LexGrid.concepts.Presentation" %>
<%@ page import="org.LexGrid.commonTypes.Source" %>
<%@ page import="org.LexGrid.commonTypes.EntityDescription" %>
<%@ page import="org.LexGrid.commonTypes.Property" %>
<%@ page import="org.LexGrid.commonTypes.PropertyQualifier" %>
<%@ page import="org.LexGrid.concepts.Presentation" %>
<%@ page import="org.LexGrid.commonTypes.Source" %>
<%@ page import="org.LexGrid.commonTypes.EntityDescription" %>
<%@ page import="org.LexGrid.commonTypes.Property" %>
<%@ page import="org.LexGrid.commonTypes.PropertyQualifier" %>
<%@ page import="gov.nih.nci.evs.browser.common.Constants" %>
<%@ page import="javax.faces.model.SelectItem" %>
<%
    String ncim_url = new DataUtils().getNCImURL();
    String quicklink_dictionary = (String) request.getSession().getAttribute("dictionary");
    quicklink_dictionary = DataUtils.getFormalName(quicklink_dictionary);
    String term_suggestion_application_url2 = "";
    String dictionary_encoded2 = "";
    if (quicklink_dictionary != null) {
        term_suggestion_application_url2 = DataUtils.getMetadataValue(quicklink_dictionary, "term_suggestion_application_url");    
        dictionary_encoded2 = DataUtils.replaceAll(quicklink_dictionary, " ", "%20");
    }
%>


<div class="bluebar">
  <div id="quicklinksholder">
      <ul id="quicklinks"
        onmouseover="document.quicklinksimg.src='<%=basePath%>/images/quicklinks-active.gif';"
        onmouseout="document.quicklinksimg.src='<%=basePath%>/images/quicklinks-inactive.gif';">
        <li>
          <a href="#"><img src="<%=basePath%>/images/quicklinks-inactive.gif" width="162"
            height="18" border="0" name="quicklinksimg" alt="Quick Links" />
          </a>
          <ul>
            <li><a href="http://evs.nci.nih.gov/" target="_blank"
              alt="Enterprise Vocabulary Services">EVS Home</a></li>
            <li><a href="<%=ncim_url%>" target="_blank"
              alt="NCI Metathesaurus">NCI Metathesaurus Browser</a></li>

            <%
            if (quicklink_dictionary == null || quicklink_dictionary.compareTo("NCI Thesaurus") != 0) {
            %>

            <li><a href="<%= request.getContextPath() %>/index.jsf"
              alt="NCI Thesaurus Browser">NCI Thesaurus Browser</a></li>

            <%
            }
            %>


            <li><a href="<%= request.getContextPath() %>/start.jsf"
              alt="NCI Term Browser">NCI Term Browser</a></li>
            <li><a href="http://www.cancer.gov/cancertopics/terminologyresources" target="_blank"
              alt="NCI Terminology Resources">NCI Terminology Resources</a></li>
            <% if (term_suggestion_application_url2 != null && term_suggestion_application_url2.length() > 0) { %>
              <li><a href="<%=term_suggestion_application_url2%>?dictionary=<%=dictionary_encoded2%>" target="_blank" alt="Term Suggestion">Term Suggestion</a></li>
            <% } %>

          </ul>
        </li>
      </ul>
  </div>
</div>