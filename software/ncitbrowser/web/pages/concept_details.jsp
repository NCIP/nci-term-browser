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
<%@ page import="gov.nih.nci.evs.browser.utils.ConceptDetails"%>
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
<%@ page import="org.LexGrid.LexBIG.LexBIGService.LexBIGService"%>


<%@ page import="gov.nih.nci.evs.browser.properties.*"%>
<%@ page import="gov.nih.nci.evs.browser.utils.*"%>
<%@ page import="gov.nih.nci.evs.browser.common.*"%>

<%@ page contentType="text/html;charset=UTF-8"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns:c="http://java.sun.com/jsp/jstl/core">
<head>
<%
JSPUtils.JSPHeaderInfo prop_info = new JSPUtils.JSPHeaderInfo(request);
String prop_dictionary = prop_info.dictionary;

if (prop_dictionary != null) {
	prop_dictionary = StringUtils.replaceAll(prop_dictionary, "&#40;", "(");
	prop_dictionary = StringUtils.replaceAll(prop_dictionary, "&#41;", ")");
}


String prop_version = prop_info.version;

LexBIGService lbs = RemoteServerUtil.createLexBIGService();
DataUtils dataUtils = new DataUtils();
String cs_name = dataUtils.getCSName(prop_dictionary);
PropertyData propertyData = new PropertyData(lbs, cs_name, prop_version); 
ConceptDetails conceptDetails = propertyData.getConceptDetails();
HistoryUtils historyUtils = propertyData.getHistoryUtils();


List namespace_list = null;

        response.setContentType("text/html;charset=utf-8");

	//JSPUtils.JSPHeaderInfo info = new JSPUtils.JSPHeaderInfo(request);
	
	String dictionary = prop_dictionary;

String short_name = cs_name; 	
	
	if (dictionary != null) {
		//dictionary = StringUtils.replaceAll(dictionary, "&#40;", "(");
		//dictionary = StringUtils.replaceAll(dictionary, "&#41;", ")");
		dictionary = dataUtils.getCSName(dictionary);
		
		Boolean cs_available = DataUtils.isCodingSchemeAvailable(dictionary);
		if (cs_available == null || !cs_available.equals(Boolean.TRUE)) {
		    String error_msg = "WARNING: " + dictionary + Constants.CODING_SCHEME_NOT_AVAILABLE;
		    request.getSession().setAttribute("error_msg", error_msg);
		    String redirectURL = request.getContextPath() + "/pages/coding_scheme_unavailable.jsf";
		    response.sendRedirect(redirectURL);

		}  		
	}
	String deprecatedVersion = prop_info.version_deprecated;
	String version = prop_info.version;
	
	//AppScan KLO 051512
	if (version == null) {
	    version = conceptDetails.getVocabularyVersionByTag(dictionary, "PRODUCTION");
	}
	
	request.setAttribute("version", version);
	// AppScan
	if (DataUtils.isNCIT(dictionary)) {
%>
<title>NCI Thesaurus</title>
<%
	} else {
%>
<title><%=dictionary%></title>
<%
	}
%>


<%
        boolean view_graph_link = false;
        String ncbo_id = null;
        String is_virtual = "true";
        String ncbo_widget_info = NCItBrowserProperties.getNCBO_WIDGET_INFO();
        boolean view_graph = dataUtils.visualizationWidgetSupported(dictionary);

%>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/styleSheet.css" />
<script type="text/javascript" src="<%=request.getContextPath()%>/js/script.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/search.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/dropdown.js"></script>
</head>
<body>
   <script type="text/javascript" src="<%=request.getContextPath()%>/js/wz_tooltip.js"></script>
   <script type="text/javascript" src="<%=request.getContextPath()%>/js/tip_centerwindow.js"></script>
   <script type="text/javascript" src="<%=request.getContextPath()%>/js/tip_followscroll.js"></script>
   
   <script type="text/javascript">
	var newwindow;
	function popup_window(url)
	{
		newwindow=window.open(
		url, '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');
		if (window.focus) {
		    newwindow.focus();
		}
	}
   </script>
  
   
   <f:view>
              
      <!-- Begin Skip Top Navigation -->
      <a href="#evs-content" class="hideLink" accesskey="1" title="Skip repetitive navigation links">skip navigation links</A>
      <!-- End Skip Top Navigation -->
      
      
      
      <%@ include file="/pages/templates/header.jsp"%>
      <div class="center-page_960">         
         <%@ include file="/pages/templates/sub-header.jsp"%>
         <!-- Main box -->
         <div id="main-area_960">         
            <%
                String code = null;
                String ns = null;
		String type = null;
		Entity c = null;

		String singleton = (String) request.getAttribute("singleton");

		boolean code_from_cart_action = false;

		code = (String) request.getAttribute("code_from_cart_action");

		if (code == null) {
		    code = HTTPUtils.cleanXSS((String) request.getParameter("code"));

		} else {
		    request.removeAttribute("code_from_cart_action");
		    code_from_cart_action = true;
		}
		ns = HTTPUtils.cleanXSS((String) request.getParameter("ns"));
		

		if (StringUtils.isNullOrBlank(code)) {

		} else {

			 Vector u2 = StringUtils.parseData(code, ",");
			 if (u2.size() == 2) {
			     code = (String)u2.elementAt(0);
			     ns = (String)u2.elementAt(1);
			 }
		}
		//KLO 
		code = HTTPUtils.cleanXSS(code);

		if (code == null) {
			Entity con = (Entity) request.getSession().getAttribute("concept");
			if (con != null) {
				code = con.getEntityCode();
				ns = con.getEntityCodeNamespace();


			} else {
				code = (String) request.getSession().getAttribute("code");
				ns = (String) request.getSession().getAttribute("ns");
			}
		}

		request.getSession().setAttribute("code", code);	
		request.getSession().setAttribute("ns", ns);


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

		String cd_dictionary = dataUtils.getFormalName(dictionary);
		String term_suggestion_application_url = dataUtils
				.getMetadataValue(cd_dictionary,"term_suggestion_application_url");
		String name = "";
		String ltag = null;

		if (JSPUtils.isNull(dictionary)) {
			name = "Error: Invalid dictionary - " + dictionary + ".";
		} else if (JSPUtils.isNull(version)) {
			name = "Error: Invalid version - " + version + ".";
		} else {
            		namespace_list = conceptDetails.getDistinctNamespacesOfCode(
            				dictionary, version, code);		
		
			if (StringUtils.isNullOrBlank(ns) || namespace_list.size() == 1) {
			    c = conceptDetails.getConceptByCode(dictionary, version, code);
			} else {
			    c = conceptDetails.getConceptByCode(dictionary, version, code, ns, true);
			}

			if (c != null) {
				request.getSession().setAttribute("concept", c);
				request.getSession().setAttribute("code", code);
				request.getSession().setAttribute("ns", ns);
				name = "";
				if (c.getEntityDescription() != null) {
				    name = c.getEntityDescription().getContent();
				}
			} else {
				//name = "The server encountered an internal error that prevented it from fulfilling this request.";
				name = "ERROR: Invalid code - " + code + ".";
			}
		}

            		
               if (DataUtils.isNCIT(dictionary)) {
               %>
               <%@ include file="/pages/templates/content-header-other.jsp"%>
               <%
               	} else {
               			request.getSession().setAttribute("dictionary", dictionary);
               %>
               <%@ include file="/pages/templates/content-header-other.jsp"%>
               <%
               	}

            				
            		String tg_dictionary_0 = dictionary;
            		String tg_dictionary = StringUtils.replaceAll(dictionary, " ", "%20");
            		if (c != null) {
            			request.getSession().setAttribute("type", type);
            			request.getSession().setAttribute("singleton", "false");
            %>
            <!-- Page content -->
            <div class="pagecontentLittlePadding"> 
            
            
                  <h:form style="margin:0px 0px 0px 0px;" acceptcharset="UTF-8"> 
                  
                  
                  <table border="0" width="920px" style="margin:0px 0px 0px 0px;">
                     <tr class="global-nav"> 
                        <td width="25%"></td>                       
                        <td align="right" width="75%">
                           <%
                           	Boolean[] isPipeDisplayed = new Boolean[] { Boolean.FALSE };
                           	boolean tree_access2 = !dataUtils.get_vocabulariesWithoutTreeAccessHashSet().contains(dictionary);
                    		boolean typeLink_isMapping2 = dataUtils.isMapping(dictionary, null);
                           	if (tree_access2 && !typeLink_isMapping2) {
                           	
                           	
                           	
                           %>
      
 <%
 if (DataUtils.isNullOrBlank(ns)) {
 %>
       
                            <a href="#" onClick="javascript:window.open('<%=request.getContextPath()%>/ajax?action=search_hierarchy&ontology_node_id=<%=code%>&ontology_display_name=<%=short_name%>&version=<%=version%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">

 
 <%
 } else {
 %>
       
                            <a href="#" onClick="javascript:window.open('<%=request.getContextPath()%>/ajax?action=search_hierarchy&ontology_node_id=<%=code%>&ontology_node_ns=<%=ns%>&ontology_display_name=<%=short_name%>&version=<%=version%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">

 
 <%
 }
 %>
      
                               View in Hierarchy</a>
                           <%=JSPUtils.getPipeSeparator(isPipeDisplayed)%>
             <%
                      }
                      
                      
                      boolean historyAccess = historyUtils.isHistoryServiceAvailable(dictionary);
                      if (historyAccess) {
             %>
                          <%=JSPUtils.getPipeSeparator(isPipeDisplayed)%>
                           <a href="#" onClick="javascript:window.open('<%=request.getContextPath()%>/pages/concept_history.jsf?dictionary=<%=dictionary%>&version=<%=version%>&code=<%=code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
                              View History</a> <%
 	                   }
             %>


<%


if (view_graph) { 
    String ncbo_widget_page = "ncbo_widget";
%>
                          <%=JSPUtils.getPipeSeparator(isPipeDisplayed)%>
	<a href="#" onclick="javascript:popup_window('<%=request.getContextPath()%>/pages/<%=ncbo_widget_page%>.jsf?dictionary=<%=dictionary%>&code=<%=code%>', '_blank','top=100, left=100, height=740, width=680, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');" tabindex="12"
	 title="This link displays a graph that recapitulates some information in the Relationships tab in a visual format.">
	View Graph</a>  
<%
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
                              target="_blank" alt="Term Suggestion">Suggest Changes</a> 
             <%
 	                  }
             %>
                        </td>
                     </tr>
                  </table>
                  
 
             <input type="hidden" id="cart_dictionary" name="cart_dictionary" value="<%=HTTPUtils.cleanXSS(dictionary)%>" />
             <input type="hidden" id="cart_version" name="cart_version" value="<%=HTTPUtils.cleanXSS(version)%>" />
             <input type="hidden" id="cart_code" name="cart_code" value="<%=HTTPUtils.cleanXSS(code)%>" />
             
<%
String b = HTTPUtils.cleanXSS((String) request.getParameter("b"));
String n = HTTPUtils.cleanXSS((String) request.getParameter("n"));
String m = HTTPUtils.cleanXSS((String) request.getParameter("m"));
String vse = HTTPUtils.cleanXSS((String) request.getParameter("vse"));


// Floating Point Value Denial of Service threats fix:
        if (!StringUtils.isNull(b) && b.compareTo("0") != 0) {
            b = "1";
        }
        
        if (!StringUtils.isNull(n) && !StringUtils.isInteger(n)) {
            n = "1";
        }

        if (!StringUtils.isNull(m) && m.compareTo("0") != 0) {
            m = "1";
        }
        
        

String key = HTTPUtils.cleanXSS((String) request.getParameter("key"));

if (!StringUtils.isNull(vse)) {
%>
    <input type="hidden" id="vse" name="vse" value="<%=vse%>" />
<%
}


if (!StringUtils.isNull(b)) {  
    if (StringUtils.isNull(n)) {
        n = "1";
    }
    
    request.getSession().setAttribute("b", b);
    request.getSession().setAttribute("n", n);
    request.getSession().setAttribute("key", key);
    
    
%>
             <input type="hidden" id="b" name="b" value="<%=b%>" />
             <input type="hidden" id="n" name="n" value="<%=n%>" />
             <input type="hidden" id="key" name="key" value="<%=key%>" />
             
<%  
    if (!StringUtils.isNull(m)) {
        request.getSession().setAttribute("m", m);
    %>
        <input type="hidden" id="m" name="m" value="<%=m%>" />
    <%
    }


}
%>
                  </h:form>  
 
               <a name="evs-content" id="evs-content"></a>
               <table border="0" cellpadding="0" cellspacing="0" width="100%">
                  <tr>
                  <%
                  if (namespace_list != null && namespace_list.size() > 1) {
                  %>
                     <td class="texttitle-blue"><%=HTTPUtils.cleanXSS(name)%> (Code <%=HTTPUtils.cleanXSS(code)%>; &nbsp;Namespace <%=ns%>)</td>
                  <%   
                  } else {
                  %>
                     <td class="texttitle-blue"><%=HTTPUtils.cleanXSS(name)%> (Code <%=HTTPUtils.cleanXSS(code)%>)</td>
                  <%
                  }
                  %>
                     
                     
                     <td class="textbodyred">
                  <%
                  	if (namespace_list != null && namespace_list.size() > 1) {
                  		String count_str = Integer.valueOf(namespace_list.size()).toString();
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
		request.getSession().setAttribute("ns", ns);
		request.setAttribute("version", version);
               %>
               <%@ include file="/pages/templates/typeLinks.jsp"%>
               <div class="tabTableContentContainer">
                  <%
                  	if (type != null && type.compareTo("all") == 0) {
              				boolean isMappingCD = dataUtils.isMapping(dictionary,version);
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
         </div> <!--  End main-area_960 -->
         <div class="mainbox-bottom"><img src="<%=basePath%>/images/mainbox-bottom.gif" width="945" height="5" alt="Mainbox Bottom" /></div>
      </div> <!-- End center-page_960 -->
      
   </f:view>
</body>
</html>
