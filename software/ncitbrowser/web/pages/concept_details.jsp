<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.Vector"%>
<%@ page import="java.util.HashSet"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Set"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="gov.nih.nci.evs.browser.utils.DataUtils"%>
<%@ page import="gov.nih.nci.evs.browser.utils.HTTPUtils"%>
<%@ page import="gov.nih.nci.evs.browser.properties.PropertyFileParser"%>
<%@ page import="gov.nih.nci.evs.browser.properties.NCItBrowserProperties"%>
<%@ page import="gov.nih.nci.evs.browser.bean.DisplayItem"%>
<%@ page import="gov.nih.nci.evs.browser.bean.*"%>
<%@ page import="gov.nih.nci.evs.browser.utils.*"%>
<%@ page import="org.LexGrid.concepts.Entity"%>
<%@ page import="org.LexGrid.concepts.Presentation"%>
<%@ page import="org.LexGrid.commonTypes.Source"%>
<%@ page import="org.LexGrid.commonTypes.EntityDescription"%>
<%@ page import="org.LexGrid.commonTypes.Property"%>
<%@ page import="org.LexGrid.commonTypes.PropertyQualifier"%>
<%@ page import="org.LexGrid.concepts.Presentation"%>
<%@ page import="org.LexGrid.commonTypes.Source"%>
<%@ page import="org.LexGrid.commonTypes.EntityDescription"%>
<%@ page import="org.LexGrid.commonTypes.Property"%>
<%@ page import="org.LexGrid.commonTypes.PropertyQualifier"%>
<%@ page import="gov.nih.nci.evs.browser.common.Constants"%>
<%@ page import="org.LexGrid.LexBIG.Extensions.Generic.LexBIGServiceConvenienceMethods"%>
<%@ page import="org.LexGrid.LexBIG.Extensions.Generic.MappingExtension"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns:c="http://java.sun.com/jsp/jstl/core">
<head>
<%
	JSPUtils.JSPHeaderInfo info = new JSPUtils.JSPHeaderInfo(request);
	String dictionary = info.dictionary;
	if (dictionary != null) {
		dictionary = DataUtils.replaceAll(dictionary, "&#40;", "(");
		dictionary = DataUtils.replaceAll(dictionary, "&#41;", ")");
		dictionary = DataUtils.getCodingSchemeName(dictionary);
	}
	String deprecatedVersion = info.version_deprecated;
	String version = info.version;
	System.out.println("concept_details.jsp version: " + version);
	request.setAttribute("version", version);
	if (dictionary.compareTo("NCI Thesaurus") == 0) {
%>
<title>NCI Thesaurus</title>
<%
	} else {
%>
<title>NCI Term Browser</title>
<%
	}
%>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/styleSheet.css" />
<script type="text/javascript" src="<%=request.getContextPath()%>/js/script.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/search.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/dropdown.js"></script>
</head>
<body>
   <script type="text/javascript" src="<%=request.getContextPath()%>/js/wz_tooltip.js"></script>
   <script type="text/javascript" src="<%=request.getContextPath()%>/js/tip_centerwindow.js"></script>
   <script type="text/javascript" src="<%=request.getContextPath()%>/js/tip_followscroll.js"></script>
   <f:view>
              
      <!-- Begin Skip Top Navigation -->
      <a href="#evs-content" class="hideLink" accesskey="1" title="Skip repetitive navigation links">skip navigation links</A>
      <!-- End Skip Top Navigation -->
      <%@ include file="/pages/templates/header.jsp"%>
      <div class="center-page">         
         <%@ include file="/pages/templates/sub-header.jsp"%>
         <!-- Main box -->
         <div id="main-area">         
            <%
                  String code = null;
            		String type = null;
            		String singleton = (String) request.getAttribute("singleton");
            		if (singleton != null && singleton.compareTo("true") == 0) {
            			if (dictionary != null
            					&& dictionary
            							.compareTo(Constants.CODING_SCHEME_NAME) != 0) {
            				dictionary = DataUtils.getCodingSchemeName(dictionary);
            			}
            		}
            		
            		boolean code_from_cart_action = false;
            		
            		code = (String) request.getAttribute("code_from_cart_action");
            		 
            		 
            		if (code == null) {
           		    code = HTTPUtils.cleanXSS((String) request.getParameter("code"));
           		} else {
           		    request.removeAttribute("code_from_cart_action");
           		    code_from_cart_action = true;
           		}
           		
            		
            		//KLO 
            		code = HTTPUtils.cleanXSS(code);
            		
            		if (code == null) {
            			Entity con = (Entity) request.getSession().getAttribute("concept");
            			if (con != null) {
            				code = con.getEntityCode();
            				request.getSession().setAttribute("code", code);
            			} else {
            				code = (String) request.getSession().getAttribute("code");
            			}
            			if (code == null) {
            				System.out.println("WARNING: concept_details.jsp code: "	+ code);
            			}
            		}
           		
            		
            		String active_code = (String) request.getSession().getAttribute("active_code");
            		
            		
            		if (active_code == null) {
            			request.getSession().setAttribute("active_code", code);
            		} else {
            			if (active_code.compareTo(code) != 0) {
            				request.getSession().removeAttribute(
            						"RelationshipHashMap");
            				request.getSession().setAttribute("active_code", code);
            			}
            		}
            		
            		Boolean new_search = null;
            		Object new_search_obj = request.getSession().getAttribute("new_search");
                  
            		if (new_search_obj != null) {
            			new_search = (Boolean) new_search_obj;
            			if (new_search.equals(Boolean.TRUE)) {
            			    type = "properties";
            			    request.getSession().setAttribute("new_search",Boolean.FALSE);
            			    String codeFromParameter = code;
            			    code = (String) request.getSession().getAttribute("code");
                                    if (code == null) {
                                        code = codeFromParameter;
                                    }
            		        }
            		}
            		
           		
            		if (type == null) {
            			type = HTTPUtils.cleanXSS((String) request.getParameter("type"));
                                if (type == null) type = (String) request.getAttribute("type");
            			if (type == null) {
            				type = "properties";
            			} else if (type.compareTo("properties") != 0
            					&& type.compareTo("relationship") != 0
            					&& type.compareTo("synonym") != 0
            					&& type.compareTo("mapping") != 0
            					&& type.compareTo("all") != 0) {
            				type = "properties";
            			}
            		}
                  
            		String cd_dictionary = DataUtils.getFormalName(dictionary);
            		String term_suggestion_application_url = DataUtils
            				.getMetadataValue(cd_dictionary,"term_suggestion_application_url");
            		String name = "";
            		Entity c = null;
            		String ltag = null;

            		if (JSPUtils.isNull(dictionary)) {
            			name = "Error: Invalid dictionary - " + dictionary + ".";
            		} else if (JSPUtils.isNull(version)) {
            			name = "Error: Invalid version - " + version + ".";
            		} else {
           			c = DataUtils.getConceptByCode(dictionary, version, ltag, code);
           			
           			//c = SearchUtils.getConceptByCode(dictionary, version, ltag, code);
           			
            			if (c != null) {
            				request.getSession().setAttribute("concept", c);
            				request.getSession().setAttribute("code", code);
            				name = c.getEntityDescription().getContent();
            			} else {
            				//name = "The server encountered an internal error that prevented it from fulfilling this request.";
            				name = "ERROR: Invalid code - " + code + ".";
            			}
            		}
          		
            		
            		if (dictionary.compareTo("NCI Thesaurus") == 0
            				|| dictionary.compareTo("NCI_Thesaurus") == 0) {
               %>
               <%@ include file="/pages/templates/content-header-other.jsp"%>
               <%
               	} else {
               			request.getSession().setAttribute("dictionary", dictionary);
               %>
               <%@ include file="/pages/templates/content-header-other.jsp"%>
               <%
               	}
            		List namespace_list = DataUtils.getDistinctNamespacesOfCode(
            				dictionary, version, code);
            		String tg_dictionary_0 = dictionary;
            		String tg_dictionary = DataUtils.replaceAll(dictionary, " ", "%20");
            		if (c != null) {
            			request.getSession().setAttribute("type", type);
            			request.getSession().setAttribute("singleton", "false");
            %>
            <!-- Page content -->
            <div class="pagecontentLittlePadding">      
                  <h:form style="margin:0px 0px 0px 0px;">          
                  <table border="0" width="720px" style="margin:0px 0px 0px 0px;">
                     <tr class="global-nav"> 
                        <td width="25%"></td>                       
                        <td align="right" width="75%">
                           <%
                           	Boolean[] isPipeDisplayed = new Boolean[] { Boolean.FALSE };
                           	boolean tree_access2 = !DataUtils._vocabulariesWithoutTreeAccessHashSet.contains(dictionary);
                    				boolean typeLink_isMapping2 = DataUtils.isMapping(dictionary, null);
                           	if (tree_access2 && !typeLink_isMapping2) {
                           %>
                           
                           
                           
                           
                           <%=JSPUtils.getPipeSeparator(isPipeDisplayed)%>
                           <% boolean debugVIH = false; if (debugVIH) {  //DYEE_DEBUG %>
                             <a href="#" onClick="javascript:window.open('<%=request.getContextPath()%>/pages/viewInHierarchy.jsf?dictionary=<%=dictionary%>&version=<%=version%>&code=<%=code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
                                DHtml</a>
                             <a href="#" onClick="javascript:window.open('<%=request.getContextPath()%>/pages/viewInHierarchy.jsf?dictionary=<%=dictionary%>&version=<%=version%>&code=<%=code%>&type=hierarchy');">
                                (tab)</a>
                             <%=JSPUtils.getPipeSeparator(isPipeDisplayed)%>
                             <a href="#" onClick="javascript:window.open('<%=request.getContextPath()%>/pages/hierarchy.jsf?dictionary=<%=dictionary%>&version=<%=version%>&code=<%=code%>&type=hierarchy');">
                                VIH tab</a>
                             <%=JSPUtils.getPipeSeparator(isPipeDisplayed)%>
                           <% } %>
                           
                           <!--
                           <a href="#" onClick="javascript:window.open('<%=request.getContextPath()%>/pages/search_hierarchy.jsf?dictionary=<%=dictionary%>&version=<%=version%>&code=<%=code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
                           -->
                           <a href="#" onClick="javascript:window.open('<%=request.getContextPath()%>/ajax?action=search_hierarchy&ontology_node_id=<%=code%>&ontology_display_name=<%=dictionary%>&version=<%=version%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
                              View in Hierarchy</a>
                           <!--   
                           <a href="#" onClick="javascript:window.open('<%=request.getContextPath()%>/pages/view_in_hierarchy.jsf?dictionary=<%=dictionary%>&version=<%=version%>&code=<%=code%>&type=hierarchy', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
                              Test VIH</a>                        
                           -->   
             <%
                      }
                      boolean historyAccess = HistoryUtils.isHistoryServiceAvailable(dictionary);
                      if (historyAccess) {
             %>
                          <%=JSPUtils.getPipeSeparator(isPipeDisplayed)%>
                           <a href="#" onClick="javascript:window.open('<%=request.getContextPath()%>/pages/concept_history.jsf?dictionary=<%=dictionary%>&version=<%=version%>&code=<%=code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
                              View History</a> <%
 	                   }
             %>
                     <%=JSPUtils.getPipeSeparator(isPipeDisplayed)%>
                     
                    
                     <h:commandLink action="#{CartActionBean.addToCart}" value="Add to Cart">
                        <f:setPropertyActionListener target="#{CartActionBean.entity}" value="concept" />
                        <f:setPropertyActionListener target="#{CartActionBean.codingScheme}" value="dictionary" />
                        <f:setPropertyActionListener target="#{CartActionBean.version}" value="version" />
                     </h:commandLink>
                   
         		     <c:choose>
         			      <c:when test="${sessionScope.CartActionBean.count>0}">
         			         (<h:outputText value="#{CartActionBean.count}"/>)
         			      </c:when>
         		     </c:choose>                     
             <%
 	                  if (term_suggestion_application_url != null && term_suggestion_application_url
 								.compareTo("") != 0) {
             %>
                           <%=JSPUtils.getPipeSeparator(isPipeDisplayed)%>
                           <a href="<%=term_suggestion_application_url%>?dictionary=<%=HTTPUtils.cleanXSS(cd_dictionary)%>&code=<%=HTTPUtils.cleanXSS(code)%>"
                              target="_blank" alt="Term Suggestion">Suggest Changes</a> <%
 	                  }
             %>
                        </td>
                     </tr>
                  </table>
                  
 
             <input type="hidden" id="cart_dictionary" name="cart_dictionary" value="<%=HTTPUtils.cleanXSS(dictionary)%>" />
             <input type="hidden" id="cart_version" name="cart_version" value="<%=HTTPUtils.cleanXSS(version)%>" />
             <input type="hidden" id="cart_code" name="cart_code" value="<%=HTTPUtils.cleanXSS(code)%>" />
 
                  
                  </h:form>               
               <a name="evs-content" id="evs-content"></a>
               <table border="0" cellpadding="0" cellspacing="0" width="700px">
                  <tr>
                     <td class="texttitle-blue"><%=HTTPUtils.cleanXSS(name)%> (Code <%=HTTPUtils.cleanXSS(code)%>)</td>
                     <td class="textbodyred">
                  <%
                  	if (namespace_list != null && namespace_list.size() > 1) {
                  		String count_str = new Integer(namespace_list.size()).toString();
                  		count_str = "(Note: Code " + code + " is found in " + count_str + " different namespaces.)";
                  %>
                     <%=count_str%>
                  <%
                  	}
                  %>
                     </td>
                  </tr>
                  <%
                  VisitedConceptUtils.add(request, tg_dictionary_0, version, code, name);
                  	if (deprecatedVersion != null) {
                  %>
                  <tr>
                     <td class="textbodysmall" colspan="2">
                        <%
                        if (deprecatedVersion.compareTo(version) == 0) {
                        %>
                        <font color="#A90101">Warning:</font> Requested version 
                        is not accessible. Displaying version <%=version%>
                        of this concept instead.
                        
                        <%
                        } else {
                        %>
                     
                        <font color="#A90101">Warning:</font> Version <%=deprecatedVersion%>
                        of this vocabulary is not accessible. Displaying version <%=version%>
                        of this concept instead.
                        
                        <%
                        }
                        %>
                        
                        
                     </td>
                  </tr>
                  <%
                  	}
                  %>
               </table>              
               <hr>
               <%
               	request.getSession().setAttribute("concept", c);
               			request.getSession().setAttribute("code", code);
               			request.setAttribute("version", version);
               %>
               <%@ include file="/pages/templates/typeLinks.jsp"%>
               <div class="tabTableContentContainer">
                  <%
                  	if (type != null && type.compareTo("all") == 0) {
              				boolean isMappingCD = DataUtils.isMapping(dictionary,version);
                  %>
                  <h1 class="textsubtitle-blue">Table of Contents</h1>
                  <ul>
                     <li><a href="#properties">Terms &amp; Properties</a></li>
                     <li><a href="#synonyms">Synonym Details</a></li>
                     <li><a href="#relationships">Relationships</a></li>
                     <%
                     	if (!isMappingCD) {
                     %>
                     <li><a href="#mappings">Mapping Details</a></li>
                     <%
                     	}
                     %>
                  </ul>
                  <br>
                  <%
                  	}
                  %>
                  <%@ include file="/pages/templates/property.jsp"%>
                  <%@ include file="/pages/templates/synonym.jsp"%>
                  <%@ include file="/pages/templates/relationship.jsp"%>
                  <%@ include file="/pages/templates/mappings.jsp"%>
               </div>
               <%
               	} else {
               %>
               <div class="textbody"><%=HTTPUtils.cleanXSS(name)%></div>
               <%
               	}
               %>
               <%@ include file="/pages/templates/nciFooter.jsp"%>
           
            </div> <!--  End pagecontentLittlePadding -->         
         </div> <!--  End main-area -->
         <div class="mainbox-bottom"><img src="<%=basePath%>/images/mainbox-bottom.gif" width="745" height="5" alt="Mainbox Bottom" /></div>
      </div> <!-- End center-page -->
      
   </f:view>
</body>
</html>