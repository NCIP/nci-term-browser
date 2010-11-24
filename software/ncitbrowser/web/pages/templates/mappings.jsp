
<%
  if (type.compareTo("mapping") == 0 || type.compareTo("all") == 0) {

	Entity concept_curr = (Entity) request.getSession().getAttribute("concept");
	String scheme_curr = (String) request.getSession().getAttribute("dictionary");
	if (scheme_curr == null) {
		scheme_curr = (String) request.getParameter("dictionary");
	}

	String version_curr = (String) request.getSession().getAttribute("version");
	String code_curr = (String) request.getSession().getAttribute("code");  
        Vector mapping_uri_version_vec = DataUtils.getMappingCodingSchemesEntityParticipatesIn(code_curr, null);

 %>
	<table border="0" width="708px">
		<tr>
			<td class="textsubtitle-blue" align="left">Mapping relationships</td>
			<td align="right" class="texttitle-blue-rightJust">	
				<h:form>			
					<h:commandLink action="#{CartActionBean.addToCart}" value="Add to Cart">				
						<f:setPropertyActionListener target="#{CartActionBean.codename}" value="concept" />
					</h:commandLink>
				</h:form>				
			</td>
		</tr>
	</table>       
<%         
        if (mapping_uri_version_vec == null || mapping_uri_version_vec.size() == 0) {
%>        
            <b>Mapping Relationships:</b> <i>(none)</i>
<%        
        } else {
%>       

		    <b>Mapping Relationships:</b>
		<p>    
		<br/>
		<table class="dataTable">
		<%

			for(int lcv=0; lcv<mapping_uri_version_vec.size(); lcv++) {
			     String mapping_uri_version = (String) mapping_uri_version_vec.elementAt(lcv);
			     Vector ret_vec = DataUtils.parseData(mapping_uri_version, "|");
			     String mapping_cs_uri = (String) ret_vec.elementAt(0);
			     String mapping_cs_version = (String) ret_vec.elementAt(1);

			     String mapping_cs_name = DataUtils.uri2CodingSchemeName(mapping_cs_uri);

		%>		
				   <tr>
					   <td>
					   <%=mapping_cs_uri%>&nbsp;(<%=mapping_cs_version%>)&nbsp;
				<a href="<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=mapping_cs_name%>&code=<%=code_curr%>&type=relationship">
				   <i class="textbodyred">View Mapping</i>
				</a>
					   </td>
				   </tr>
		<%   

			} 

		%>	

		</table>
		</p>

		<%
          }

    }
%>



