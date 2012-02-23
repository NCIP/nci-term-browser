<%@ page import="gov.nih.nci.evs.browser.utils.*" %>
<%@ page import="org.lexgrid.valuesets.LexEVSValueSetDefinitionServices" %>


  <script type="text/javascript">
  
    function refresh() {
      
      var selectValueSetSearchOptionObj = document.forms["valueSetSearchForm"].selectValueSetSearchOption;
      
      for (var i=0; i<selectValueSetSearchOptionObj.length; i++) {
        if (selectValueSetSearchOptionObj[i].checked) {
            selectValueSetSearchOption = selectValueSetSearchOptionObj[i].value;
        }
      }
      
    
      window.location.href="/ncitbrowser/pages/value_set_search.jsf?refresh=1"
          + "&opt="+ selectValueSetSearchOption;

    }
  </script>
  
  
  
<%

  String _searchform_requestContextPath = request.getContextPath();
  _searchform_requestContextPath = _searchform_requestContextPath.replace("//ncitbrowser//ncitbrowser", "//ncitbrowser");

    String _selected_cs = "";
    String _selected_cd = null;

    String _check_cs = "";
    String _check_cd = "";
    String _check_code = "";
    String _check_name = "";
    String _check_src = "";
    
    String _valueset_search_algorithm = null;
    _valueset_search_algorithm = (String) request.getSession().getAttribute("valueset_search_algorithm");
    if (_valueset_search_algorithm == null) _valueset_search_algorithm = "";
    System.out.println("JSP _valueset_search_algorithm " + _valueset_search_algorithm);   
    

    String _check__e = "", _check__b = "", _check__s = "" , _check__c ="";
    if (_valueset_search_algorithm == null || _valueset_search_algorithm.compareTo("exactMatch") == 0)
        _check__e = "checked";
    else if (_valueset_search_algorithm.compareTo("startsWith") == 0)
        _check__s= "checked";
    else if (_valueset_search_algorithm.compareTo("DoubleMetaphoneLuceneQuery") == 0)
        _check__b= "checked";
    else
        _check__c = "checked";
        

    String _selectValueSetSearchOption = null;
    _selectValueSetSearchOption = HTTPUtils.cleanXSS((String) request.getParameter("opt"));
    
    if (_selectValueSetSearchOption == null) {
        _selectValueSetSearchOption = (String) request.getSession().getAttribute("selectValueSetSearchOption");
    }
               
    if (_selectValueSetSearchOption == null || _selectValueSetSearchOption.compareTo("null") == 0) {
        _selectValueSetSearchOption = "Code";
    }

    if (_selectValueSetSearchOption.compareTo("CodingScheme") == 0)
        _check_cs = "checked";
    else if (_selectValueSetSearchOption.compareTo("Code") == 0)
        _check_code = "checked";
    else if (_selectValueSetSearchOption.compareTo("Name") == 0)
        _check_name = "checked";        
    else if (_selectValueSetSearchOption.compareTo("Source") == 0)
        _check_src = "checked";
    
 
 
 
 
String _valueset_match_text = null;
_valueset_match_text = (String) request.getSession().getAttribute("matchText_VSD");
if (_valueset_match_text == null) _valueset_match_text = "";

%>
<h:form id="valueSetSearchForm" styleClass="search-form">   

<input type="hidden" id="nav_type" name="nav_type" value="valuesets" />

<table>
  <tr><td> 

<%
if (_selectValueSetSearchOption.compareTo("CodingScheme") == 0) {
%>
	  <input CLASS="searchbox-input-2"
	    name="matchText"
	    value=""
	    onkeypress="return submitEnter('valueSetSearchForm:valueset_search',event)"
	    tabindex="1"/>
<%
} else {
%>

	  <input CLASS="searchbox-input-2"
	    name="matchText"
	    value="<%=_valueset_match_text%>"
	    onFocus="active = true"
	    onBlur="active = false"
	    onkeypress="return submitEnter('valueSetSearchForm:valueset_search',event)"
	    tabindex="1"/>
<%
}
%>  
	    
	    
	    <h:commandButton id="valueset_search" value="Search" action="#{valueSetBean.valueSetSearchAction}"
	      onclick="javascript:cursor_wait();"
	      image="#{valueSetSearch_requestContextPath}/images/search.gif"
          styleClass="searchbox-btn"
	      alt="Search"
	      tabindex="2">
	    </h:commandButton>

	    <h:outputLink
	    value="#{facesContext.externalContext.requestContextPath}/pages/help.jsf#searchhelp"
	    tabindex="3">
	    <h:graphicImage value="/images/search-help.gif" styleClass="searchbox-btn" alt="Search Help"
	    style="border-width:0;"/>
	  </h:outputLink> 
  </td></tr>
   
   		  <tr><td>
   		  <table border="0" cellspacing="0" cellpadding="0">

		    <tr valign="top" align="left"><td align="left" class="textbody">
		      <input type="radio" name="valueset_search_algorithm" value="exactMatch" alt="Exact Match" <%=_check__e%> tabindex="3">Exact Match&nbsp;
		      <input type="radio" name="valueset_search_algorithm" value="startsWith" alt="Begins With" <%=_check__s%> tabindex="3">Begins With&nbsp;
		      <input type="radio" name="valueset_search_algorithm" value="contains" alt="Contains" <%=_check__c%> tabindex="3">Contains
		    </td></tr>
		    

 
		    <tr align="left">
		      <td height="1px" bgcolor="#2F2F5F"></td>
		    </tr>
		    
   		  
   		     <tr valign="top" align="left"><td align="left" class="textbody">
 				          <input type="radio" id="selectValueSetSearchOption" name="selectValueSetSearchOption" value="Code" <%=_check_code%> 
 				              alt="Code" tabindex="1" onclick="javascript:refresh()" >Code&nbsp;
 				          <input type="radio" id="selectValueSetSearchOption" name="selectValueSetSearchOption" value="Name" <%=_check_name%> 
 				              alt="Name" tabindex="1" onclick="javascript:refresh()" >Name&nbsp;
 				          <input type="radio" id="selectValueSetSearchOption" name="selectValueSetSearchOption" value="Source" <%=_check_src%> 
 				              alt="Source" tabindex="1" onclick="javascript:refresh()" >Source&nbsp;
 				          <input type="radio" id="selectValueSetSearchOption" name="selectValueSetSearchOption" value="CodingScheme" <%=_check_cs%> 
                                              alt="Coding Scheme" tabindex="1" onclick="javascript:refresh()" >Terminology
                     </td></tr>
		    
		    
		  </table> 
		  </td></tr>

<%
if (_selectValueSetSearchOption.compareToIgnoreCase("CodingScheme") == 0) {                
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


</table>

              <input type="hidden" name="referer" id="referer" value="<%=HTTPUtils.getRefererParmEncode(request)%>">

</h:form>   