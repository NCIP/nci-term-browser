<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*"%>
<%@ page import="org.LexGrid.concepts.Entity" %>
<%@ page import="gov.nih.nci.evs.browser.bean.*" %>
<%@ page import="gov.nih.nci.evs.browser.utils.*" %>
<%@ page import="gov.nih.nci.evs.browser.properties.*" %>
<%@ page import="gov.nih.nci.evs.browser.utils.*" %>
<%@ page import="javax.faces.context.FacesContext" %>
<%@ page import="org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference" %>
<%@ page import="org.apache.log4j.*" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns:c="http://java.sun.com/jsp/jstl/core">
<head>
  <title>NCI Thesaurus</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />
  <link rel="shortcut icon" href="<%= request.getContextPath() %>/favicon.ico" type="image/x-icon" />
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/script.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/search.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/dropdown.js"></script>
</head>
<body onLoad="document.forms.advancedSearchForm.matchText.focus();">
  <script type="text/javascript"
    src="<%=request.getContextPath()%>/js/wz_tooltip.js"></script>
  <script type="text/javascript"
    src="<%=request.getContextPath()%>/js/tip_centerwindow.js"></script>
  <script type="text/javascript"
    src="<%=request.getContextPath()%>/js/tip_followscroll.js"></script>
  <script type="text/javascript">
    function refresh() {

      var dictionary = document.forms["advancedSearchForm"].dictionary.value;

      var text = escape(document.forms["advancedSearchForm"].matchText.value);
      
      algorithm = "exactMatch";
      var algorithmObj = document.forms["advancedSearchForm"].adv_search_algorithm;
      for (var i=0; i<algorithmObj.length; i++) {
        if (algorithmObj[i].checked) {
          algorithm = algorithmObj[i].value;
        }
      }
      var adv_search_source = document.forms["advancedSearchForm"].adv_search_source.value;

      var selectSearchOption = "";
      var selectSearchOptionObj = document.forms["advancedSearchForm"].selectSearchOption;
      for (var i=0; i<selectSearchOptionObj.length; i++) {
        if (selectSearchOptionObj[i].checked) {
          selectSearchOption = selectSearchOptionObj[i].value;
        }
      }

      var rel_search_association = document.forms["advancedSearchForm"].rel_search_association.value;
      //var rel_search_rela = document.forms["advancedSearchForm"].rel_search_rela.value;
      var selectProperty = document.forms["advancedSearchForm"].selectProperty.value;
      var _version = document.forms["advancedSearchForm"].version.value;

      window.location.href="/ncitbrowser/pages/advanced_search.jsf?refresh=1"
          + "&opt="+ selectSearchOption
          + "&text="+ text
          + "&algorithm="+ algorithm
          + "&sab="+ adv_search_source
          + "&prop="+ selectProperty
          + "&rel="+ rel_search_association
          //+ "&rela="+ rel_search_rela
          + "&dictionary="+ dictionary
          + "&version="+ _version;
    }
  </script>
  <%!
    private static Logger _logger = Utils.getJspLogger("advanced_search.jsp");
  %>
  <f:view>
    <!-- Begin Skip Top Navigation -->
      <a href="#evs-content" class="hideLink" accesskey="1" title="Skip repetitive navigation links">skip navigation links</A>
    <!-- End Skip Top Navigation -->  
    <%@ include file="/pages/templates/header.jsp" %>
    <div class="center-page">
      <%@ include file="/pages/templates/sub-header.jsp" %>
      <!-- Main box -->
      <div id="main-area">
      
        <%-- Simple Search Link Version: <%@ include file="/pages/templates/content-header-alt.jsp" %> --%>
        
        <% request.setAttribute("hideAdvancedSearchLink", true); %>
        
        <%@ include file="/pages/templates/content-header-other.jsp" %>
        
        
<%

  String advSearch_requestContextPath = request.getContextPath();
  advSearch_requestContextPath = advSearch_requestContextPath.replace("//ncitbrowser//ncitbrowser", "//ncitbrowser");

    JSPUtils.JSPHeaderInfo info2 = new JSPUtils.JSPHeaderInfo(request);
    String adv_search_vocabulary = info2.dictionary;
    String adv_search_version = info2.version;
 
 System.out.println("advanced_search.jsp adv_search_vocabulary: " + adv_search_vocabulary);
 System.out.println("advanced_search.jsp adv_search_version: " + adv_search_version);
     
    
    String refresh = (String) request.getParameter("refresh");
    boolean refresh_page = false;
    if (refresh != null) {
        refresh_page = true;
    }

    String adv_search_algorithm = null;
    String search_string = "";
    String selectProperty = null;
    String rel_search_association = null;
    String rel_search_rela = null;
    String adv_search_source = null;
    String adv_search_type = null;

    String t = null;
    String selectSearchOption = null;

    if (refresh_page) {
        // Note: Called when the user selects "Search By" fields.
        selectSearchOption = (String) request.getParameter("opt");
        search_string = (String) request.getParameter("text");
        adv_search_algorithm = (String) request.getParameter("algorithm");
        adv_search_source = (String) request.getParameter("sab");
        rel_search_association = (String) request.getParameter("rel");
        rel_search_rela = (String) request.getParameter("rela");
        selectProperty = (String) request.getParameter("prop");
        
        adv_search_type = selectSearchOption;


    } else {
        selectSearchOption = (String) request.getSession().getAttribute("selectSearchOption");
        search_string = (String) request.getSession().getAttribute("matchText");
    }

    if (selectSearchOption == null || selectSearchOption.compareTo("null") == 0) {
        selectSearchOption = "Property";
    }

    SearchStatusBean bean = null;
    String message = (String) request.getAttribute("message");
    if (message != null) {
        request.removeAttribute("message");
    }


    if (!refresh_page || message != null) {
        /*
        // Note: Called when search contains no match.
        Object bean_obj = FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("searchStatusBean");
        if (bean_obj == null) {
            //bean_obj = request.getAttribute("searchStatusBean");
            
            bean_obj = request.getSession().getAttribute("searchStatusBean");
        }
        */
        
        Object bean_obj = request.getSession().getAttribute("searchStatusBean");

        if (bean_obj == null) {
            bean = new SearchStatusBean(adv_search_vocabulary);
            FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("searchStatusBean", bean);
            
            System.out.println("bean_obj == null???");
            adv_search_algorithm = bean.getAlgorithm();
            System.out.println("adv_search_algorithm " + adv_search_algorithm);

        } else {
        
            System.out.println("bean_obj != null???");
        
            bean = (SearchStatusBean) bean_obj;
            adv_search_algorithm = bean.getAlgorithm();
            
            System.out.println("adv_search_algorithm " + adv_search_algorithm);
            
            adv_search_source = bean.getSelectedSource();
            selectProperty = bean.getSelectedProperty();
            search_string = bean.getMatchText();
            rel_search_association = bean.getSelectedAssociation();
            rel_search_rela = bean.getSelectedRELA();
            
            //KLO
            adv_search_type = bean.getSearchType();
            selectSearchOption = adv_search_type;
            
            selectSearchOption = bean.getSelectedSearchOption();
            

            _logger.debug("advanced_search.jsp adv_search_algorithm: " + adv_search_algorithm);
            _logger.debug("advanced_search.jsp adv_search_source: " + adv_search_source);
            _logger.debug("advanced_search.jsp selectProperty: " + selectProperty);
            _logger.debug("advanced_search.jsp search_string: " + search_string);
            _logger.debug("advanced_search.jsp rel_search_association: " + rel_search_association);
            _logger.debug("advanced_search.jsp rel_search_rela: " + rel_search_rela);
            FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("searchStatusBean", bean);
        }
    }


    if (rel_search_association == null) rel_search_association = "ALL";
    if (rel_search_rela == null) rel_search_rela = " ";
    if (selectProperty == null) selectProperty = "ALL";
    if (adv_search_source == null) adv_search_source = "ALL";
    if (search_string == null) search_string = "";
    if (adv_search_algorithm == null) adv_search_algorithm = "exactMatch";


    String check__e = "", check__b = "", check__s = "" , check__c ="";
    if (adv_search_algorithm == null || adv_search_algorithm.compareTo("exactMatch") == 0)
        check__e = "checked";
    else if (adv_search_algorithm.compareTo("startsWith") == 0)
        check__s= "checked";
    else if (adv_search_algorithm.compareTo("DoubleMetaphoneLuceneQuery") == 0)
        check__b= "checked";
    else
        check__c = "checked";

    String check_n2 = "", check_c2 = "", check_p2 = "" , check_r2 ="";

    if (selectSearchOption == null || selectSearchOption.compareTo("Name") == 0)
      check_n2 = "checked";
    else if (selectSearchOption.compareTo("Code") == 0)
        check_c2 = "checked";
    else if (selectSearchOption.compareTo("Property") == 0)
      check_p2 = "checked";
    else if (selectSearchOption.compareTo("Relationship") == 0)
      check_r2 = "checked";
      

%>
        <div class="pagecontent">
          <a name="evs-content" id="evs-content"></a>
          <table>
            <tr>
            <td class="texttitle-blue">Advanced Search</td>
            </tr>

            <% if (message != null) { %>
        <tr class="textbodyred"><td>
      <p class="textbodyred">&nbsp;<%=message%></p>
        </td></tr>
            <% } %>

            <tr class="textbody"><td>
            
<!--            
               <FORM NAME="advancedSearchForm" METHOD="POST" CLASS="search-form" >
-->
 <h:form id="advancedSearchForm" styleClass="search-form">            
               
               
                <table>
                  <tr><td>
                    <input CLASS="searchbox-input" name="matchText" value="<%=HTTPUtils.cleanXSS(search_string)%>" 
                           onkeypress="return submitEnter('advancedSearchForm:adv_search',event)" tabindex="1">
                    <h:commandButton id="adv_search" value="Search" action="#{userSessionBean.advancedSearchAction}"
                      onclick="javascript:cursor_wait();"
                      image="#{advSearch_requestContextPath}/images/search.gif"
                      alt="Search"
                      tabindex="2">
                    </h:commandButton>
                  </td></tr>
                  <tr><td>
                     <table border="0" cellspacing="0" cellpadding="0">
                    <tr valign="top" align="left"><td align="left" class="textbody">
                      <input type="radio" name="adv_search_algorithm" value="exactMatch" alt="Exact Match" <%=check__e%> tabindex="3">Exact Match&nbsp;
                      <input type="radio" name="adv_search_algorithm" value="startsWith" alt="Begins With" <%=check__s%> tabindex="3">Begins With&nbsp;
                      <input type="radio" name="adv_search_algorithm" value="contains" alt="Contains" <%=check__c%> tabindex="3">Contains
                    </td></tr>
                  </table>
                </td></tr>

                <tr><td>
                  <h:outputLabel id="rel_search_source_Label" value="Source" styleClass="textbody">
                    <select id="adv_search_source" name="adv_search_source" size="1" tabindex="4">
                    <%
                      Vector src_vec = OntologyBean.getSupportedSources(adv_search_vocabulary, adv_search_version);
                      t = "ALL";
                      if (adv_search_source == null) adv_search_source = "ALL";
                        if (t.compareTo(adv_search_source) == 0) {
                    %>
                          <option value="<%=t%>" selected><%=t%></option>
                    <%
                        } else {
                    %>
                          <option value="<%=t%>"><%=t%></option>
                    <%
                       }


                       if (src_vec != null) {
			    for (int i=0; i<src_vec.size(); i++) {
				 t = (String) src_vec.elementAt(i);
				 if (t.compareTo(adv_search_source) == 0) {
			    %>
				   <option value="<%=t%>" selected><%=t%></option>
			    <%
				 } else {
			    %>
				   <option value="<%=t%>"><%=t%></option>
			    <%
				 }
			    }
                       }
                    %>
                    </select>
                  </h:outputLabel>
                </td></tr>

                <tr><td>
                  &nbsp;&nbsp;
                </td></tr>

                <tr valign="top" align="left"><td align="left" class="textbody">
                Concepts with this value in:
                </td></tr>

                <tr valign="top" align="left"><td align="left" class="textbody">
                  <input type="radio" id="selectSearchOption" name="selectSearchOption" value="Code" alt="Code" <%=check_c2%> onclick="javascript:refresh()" tabindex="5">Code&nbsp;
                  <input type="radio" id="selectSearchOption" name="selectSearchOption" value="Name" alt="Name" <%=check_n2%> onclick="javascript:refresh()" tabindex="5">Name&nbsp;
                  <input type="radio" id="selectSearchOption" name="selectSearchOption" value="Property" alt="Property" <%=check_p2%> onclick="javascript:refresh()" tabindex="5">Property&nbsp;
                  <input type="radio" id="selectSearchOption" name="selectSearchOption" value="Relationship" alt="Relationship" <%=check_r2%> onclick="javascript:refresh()" tabindex="5">Relationship
                </td></tr>

                <tr><td>
                  <table>
                  <% if (selectSearchOption.equals("Property")) { %>
                    <input type="hidden" name="rel_search_association" id="rel_search_association" value="<%=HTTPUtils.cleanXSS(rel_search_association)%>">
                    <input type="hidden" name="rel_search_rela" id="rel_search_rela" value="<%=HTTPUtils.cleanXSS(rel_search_rela)%>">
                    <tr>
                      <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
                      <td>
                        <h:outputLabel id="selectPropertyLabel" value="Property" styleClass="textbody">
                          <select id="selectProperty" name="selectProperty" size="1" tabindex="6">
                          <%
                            t = "ALL";
                            if (t.compareTo(selectProperty) == 0) {
                          %>
                              <option value="<%=t%>" selected><%=t%></option>
                          <%} else {%>
                              <option value="<%=t%>"><%=t%></option>
                          <%}%>

                          <%
                            Vector property_vec = OntologyBean.getSupportedPropertyNames(adv_search_vocabulary, adv_search_version);
                            for (int i=0; i<property_vec.size(); i++) {
                              t = (String) property_vec.elementAt(i);
                              if (t.compareTo(selectProperty) == 0) {
                          %>
                                <option value="<%=t%>" selected><%=t%></option>
                          <%  } else { %>
                                <option value="<%=t%>"><%=t%></option>
                          <%
                              }
                            }
                          %>
                          </select>
                        </h:outputLabel>
                      </td>
                    </tr>

                  <% } else if (selectSearchOption.equals("Relationship")) { %>
                    <input type="hidden" name="selectProperty" id="selectProperty" value="<%=HTTPUtils.cleanXSS(selectProperty)%>">
                    <tr>
                      <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
                      <td>
                        <h:outputLabel id="rel_search_associationLabel" value="Relationship" styleClass="textbody">
                          <select id="rel_search_association" name="rel_search_association" size="1">
                          <%
                            t = "ALL";
                            if (t.compareTo(rel_search_association) == 0) {
                          %>
                              <option value="<%=t%>" selected><%=t%></option>
                          <%} else {%>
                              <option value="<%=t%>"><%=t%></option>
                          <%} %>

                          <%
                          
System.out.println("adv_search_vocabulary: " + adv_search_vocabulary);
System.out.println("adv_search_version: " + adv_search_version);
//KLO, 010611                          
//Vector association_vec = OntologyBean.getSupportedAssociationNames(adv_search_vocabulary, adv_search_version);
                            
                            Vector association_vec = OntologyBean.getSupportedAssociationNamesAndIDs(adv_search_vocabulary, adv_search_version);
                             
                            for (int i=0; i<association_vec.size(); i++) {
                              t = (String) association_vec.elementAt(i);
                              
                              Vector name_and_id_vec = DataUtils.parseData(t);
                              String association_name = (String) name_and_id_vec.elementAt(0);
                              String association_id = (String) name_and_id_vec.elementAt(1);
                                                            
                              if (association_id.compareTo(rel_search_association) == 0) {
                          %>
                                <option value="<%=association_id%>" selected><%=association_name%></option>
                          <%  } else { %>
                                <option value="<%=association_id%>"><%=association_name%></option>
                          <%
                              }
                            }
                            
                          %>
                          </select>
                        </h:outputLabel>
                      </td>
                    </tr>
                  <% } else { %>
                    <input type="hidden" name="selectProperty" id="selectProperty" value="<%=HTTPUtils.cleanXSS(selectProperty)%>">
                    <input type="hidden" name="rel_search_association" id="rel_search_association" value="<%=HTTPUtils.cleanXSS(rel_search_association)%>">
                    <input type="hidden" name="rel_search_rela" id="rel_search_rela" value="<%=HTTPUtils.cleanXSS(rel_search_rela)%>">

                  <% }%>

                  </table>
                </td></tr>

              </table>
              <input type="hidden" name="referer" id="referer" value="<%=HTTPUtils.getRefererParmEncode(request)%>">
              <input type="hidden" name="dictionary" id="dictionary" value="<%=HTTPUtils.cleanXSS(adv_search_vocabulary)%>">
              <input type="hidden" name="version" id="version" value="<%=HTTPUtils.cleanXSS(adv_search_version)%>">

              <input type="hidden" name="adv_search_type" id="adv_search_type" value="<%=HTTPUtils.cleanXSS(adv_search_type)%>" />
<!--
            </form>
 -->           
            </h:form>
            
          </td></tr>
        </table>
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
