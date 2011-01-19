<%@ page import="gov.nih.nci.evs.browser.utils.DataUtils" %>
<%@ page import="gov.nih.nci.evs.browser.utils.HTTPUtils" %>
<%@ page import="org.lexgrid.valuesets.LexEVSValueSetDefinitionServices" %>

<%

  String searchform_requestContextPath = request.getContextPath();
  searchform_requestContextPath = searchform_requestContextPath.replace("//ncitbrowser//ncitbrowser", "//ncitbrowser");

    request.getSession().setAttribute("nav_type", "valuesets");

    String selected_cs = "";
    String selected_cd = null;

    String check_cs = "";
    String check_cd = "";

    String selectValueSetSearchOption = null;
    selectValueSetSearchOption = (String) request.getParameter("opt");
               
    if (selectValueSetSearchOption == null || selectValueSetSearchOption.compareTo("null") == 0) {
        selectValueSetSearchOption = "CodingScheme";
    }

    if (selectValueSetSearchOption.compareTo("CodingScheme") == 0)
        check_cs = "checked";
    else if (selectValueSetSearchOption.compareTo("ConceptDomain") == 0)
        check_cd = "checked";
    else check_cs = "checked";
    
    
%>
<h:form id="valueSetSearchForm" styleClass="search-form">   





<table>

                <tr valign="top" align="left">
                <td align="left" class="textbody">
                
			<table>
			<tr>
			<td align="left" class="textbody">

 				          <input type="radio" id="selectValueSetSearchOption" name="selectValueSetSearchOption" value="CodingScheme" <%=check_cs%> 
                                          alt="Coding Scheme" tabindex="1">Vocabulary&nbsp;
					  <input type="radio" id="selectValueSetSearchOption" name="selectValueSetSearchOption" value="ConceptDomain" <%=check_cd%> 
					  alt="Concept Domain" tabindex="2">Concept Domain&nbsp;
					  
			</td>
			<td align="right">
					    <h:commandButton id="adv_search" value="Search" action="#{valueSetBean.valueSetSearchAction}"
					      onclick="javascript:cursor_wait();"
					      image="#{valueSetSearch_requestContextPath}/images/search.gif"
					      alt="Search"
					      tabindex="2">
					    </h:commandButton>

					  <h:outputLink
					    value="#{facesContext.externalContext.requestContextPath}/pages/help.jsf#searchhelp"
					    tabindex="3">
					    <h:graphicImage value="/images/search-help.gif" styleClass="searchbox-btn"
					    style="border-width:0;"/>
					  </h:outputLink> 
			</td>
			</tr>
			</table>		  
                </td>
                </tr>
                
                <tr>
                
  				<td class="dataCellText">
  				        <h:outputLabel id="codingschemelabel" value="Vocabulary:" styleClass="textbody">
					<h:selectOneMenu id="selectedOntology" value="#{valueSetBean.selectedOntology}"
					    immediate = "true"
					    valueChangeListener="#{valueSetBean.ontologyChangedEvent}">
									
					     <f:selectItems value="#{valueSetBean.ontologyList}"/>
					</h:selectOneMenu>
					</h:outputLabel>
				</td>                   
                </tr>

                <tr>
                        
 				<td class="dataCellText">
 				        <h:outputLabel id="selectConceptDomainLabel" value="Domain:" styleClass="textbody">
					<h:selectOneMenu id="selectedConceptDomain" value="#{valueSetBean.selectedConceptDomain}"
					    immediate = "true"
					    valueChangeListener="#{valueSetBean.conceptDomainChangedEvent}">
					
					     <f:selectItems value="#{valueSetBean.conceptDomainList}"/>
					</h:selectOneMenu>
					</h:outputLabel>  
				</td>                       
                         
                                      
                </tr>
           
                  
</table>
<!--
</div>
-->
              <input type="hidden" name="referer" id="referer" value="<%=HTTPUtils.getRefererParmEncode(request)%>">

</h:form>   