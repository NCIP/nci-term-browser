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
    String check_code = "";
    String check_name = "";
    String check_src = "";
    
    // to be modified
    String valueset_search_algorithm = null;

    String check__e = "", check__b = "", check__s = "" , check__c ="";
    if (valueset_search_algorithm == null || valueset_search_algorithm.compareTo("exactMatch") == 0)
        check__e = "checked";
    else if (valueset_search_algorithm.compareTo("startsWith") == 0)
        check__s= "checked";
    else if (valueset_search_algorithm.compareTo("DoubleMetaphoneLuceneQuery") == 0)
        check__b= "checked";
    else
        check__c = "checked";
        

    String selectValueSetSearchOption = null;
    selectValueSetSearchOption = (String) request.getParameter("opt");
    
    if (selectValueSetSearchOption == null) {
        selectValueSetSearchOption = (String) request.getSession().getAttribute("selectValueSetSearchOption");
    }
               
    if (selectValueSetSearchOption == null || selectValueSetSearchOption.compareTo("null") == 0) {
        selectValueSetSearchOption = "Code";
    }

    if (selectValueSetSearchOption.compareTo("CodingScheme") == 0)
        check_cs = "checked";
    else if (selectValueSetSearchOption.compareTo("Code") == 0)
        check_code = "checked";
    else if (selectValueSetSearchOption.compareTo("Name") == 0)
        check_name = "checked";        
    else if (selectValueSetSearchOption.compareTo("Source") == 0)
        check_src = "checked";
    
 
 
 
 
String valueset_match_text = "";

%>
<h:form id="valueSetSearchForm" styleClass="search-form">   


<table>
  <tr><td> 

<%
if (selectValueSetSearchOption.compareTo("CodingScheme") == 0) {
%>

	  <input CLASS="searchbox-input"
	    name="matchText"
	    value=""
	    disabled="true"
	    onkeypress="return submitEnter('multiple_search',event)"
	    tabindex="1"/>
<%
} else {
%>

	  <input CLASS="searchbox-input"
	    name="matchText"
	    value="<%=valueset_match_text%>"
	    onFocus="active = true"
	    onBlur="active = false"
	    disabled="false"
	    onkeypress="return submitEnter('multiple_search',event)"
	    tabindex="1"/>
<%
}
%>  
	    
	    
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
  </td></tr>
   
   		  <tr><td>
   		  <table border="0" cellspacing="0" cellpadding="0">
   		     <tr valign="top" align="left"><td align="left" class="textbody">
 				          <input type="radio" id="selectValueSetSearchOption" name="selectValueSetSearchOption" value="Code" <%=check_code%> 
 				              alt="Code" tabindex="1" onclick="javascript:refresh()" >Code&nbsp;
 				          <input type="radio" id="selectValueSetSearchOption" name="selectValueSetSearchOption" value="Name" <%=check_name%> 
 				              alt="Name" tabindex="1" onclick="javascript:refresh()" >Name&nbsp;
 				          <input type="radio" id="selectValueSetSearchOption" name="selectValueSetSearchOption" value="Source" <%=check_src%> 
 				              alt="Source" tabindex="1" onclick="javascript:refresh()" >Source&nbsp;
 				          <input type="radio" id="selectValueSetSearchOption" name="selectValueSetSearchOption" value="CodingScheme" <%=check_cs%> 
                                              alt="Coding Scheme" tabindex="1" onclick="javascript:refresh()" >Terminology
                     </td></tr>



    
    
<%
if (selectValueSetSearchOption.compareToIgnoreCase("Code") == 0 || selectValueSetSearchOption.compareToIgnoreCase("Name") == 0) {                
%>    
		    <tr align="left">
		      <td height="1px" bgcolor="#2F2F5F"></td>
		    </tr>
		    
		    <tr valign="top" align="left"><td align="left" class="textbody">
		      <input type="radio" name="valueset_search_algorithm" value="exactMatch" alt="Exact Match" <%=check__e%> tabindex="3">Exact Match&nbsp;
		      <input type="radio" name="valueset_search_algorithm" value="startsWith" alt="Begins With" <%=check__s%> tabindex="3">Begins With&nbsp;
		      <input type="radio" name="valueset_search_algorithm" value="contains" alt="Contains" <%=check__c%> tabindex="3">Contains
		    </td></tr>
<%
}                
%>		    
		    
		  </table> 
		  </td></tr>

<%
if (selectValueSetSearchOption.compareToIgnoreCase("CodingScheme") == 0) {                
%>                
                <tr>
                
  				<td class="dataCellText">
  				        <h:outputLabel id="codingschemelabel" value="Terminology:" styleClass="textbody">
					<h:selectOneMenu id="selectedOntology" value="#{valueSetBean.selectedOntology}"
					    immediate = "true"
					    valueChangeListener="#{valueSetBean.ontologyChangedEvent}">
									
					     <f:selectItems value="#{valueSetBean.ontologyList}"/>
					</h:selectOneMenu>
					</h:outputLabel>
				</td>                   
                </tr>
<%
}                
%>

                
<!--
                <tr valign="top" align="left">
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
-->


</table>
<!--
</div>
-->
              <input type="hidden" name="referer" id="referer" value="<%=HTTPUtils.getRefererParmEncode(request)%>">

</h:form>   