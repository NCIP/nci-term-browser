<%@ page import="gov.nih.nci.evs.browser.bean.IteratorBean" %>
<%@ page import="gov.nih.nci.evs.browser.bean.IteratorBeanManager" %>
<%@ page import="gov.nih.nci.evs.browser.properties.NCItBrowserProperties" %>

<%

    int prev_page_num = pageNum;
    int next_page_num = prev_page_num + 1;
    
    String istart_str = Integer.toString(istart+1);
    String iend_str = Integer.toString(iend+1);
  

String dictionary_map = (String) request.getSession().getAttribute("dictionary");
System.out.println("(*) dictionary_map " + dictionary_map);
bean = (MappingIteratorBean) scheme2MappingIteratorBeanMap.get(dictionary_map);
numRemaining = bean.getSize();

System.out.println("(2) pagination-mapping.jsp iterator.getSize(): " + numRemaining);    
   
    
    String match_size = Integer.toString(numRemaining);
    
    String prev_page_num_str = Integer.toString(pageNum);
    String next_page_num_str = Integer.toString(pageNum+1);
    
    
System.out.println("prev_page_num_str: " + prev_page_num_str);
System.out.println("next_page_num_str: " + next_page_num_str);
    
%>

<FORM NAME="paginationForm" METHOD="POST" action="<%=request.getContextPath() %>/pages/mapping.jsf?" >
  <table>
    <tr>
      <td class="textbody" align=left>
         <b>Results <%=istart_str%>-<%=iend_str%> of&nbsp;<%=match_size%></b>
      </td>
      <td>
         &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      </td>
      <td class="textbody" align=right>
          <%
             if (prev_page_num > 0) {
          %>   
          <a href="<%=request.getContextPath() %>/pages/mapping.jsf?dictionary=<%=mapping_schema%>&page_number=<%=prev_page_num_str%>">Prev</a>
          &nbsp;
          <%
             }
          %>
        
<%
                 int maxPageNumber = 5;
                 if (prev_page_num > maxPageNumber) maxPageNumber = prev_page_num;

System.out.println("maxPageNumber: " + maxPageNumber);


		 for (int idx=1; idx<=maxPageNumber; idx++) { 
		    String idx_str = Integer.toString(idx);
		    
		    if (prev_page_num != idx) {
		        if (prev_page_num == 0 && idx == 1) {
		        %>
			    <%=idx_str%>&nbsp;
		        <%      
		        } else {
		        %>
			    <a href="<%=request.getContextPath() %>/pages/mapping.jsf?dictionary=<%=mapping_schema%>&page_number=<%=idx_str%>"><%=idx_str%></a>
			    &nbsp;
		        <%
		        }
		    } else {
		      %>
			<%=idx_str%>&nbsp;
		      <%
		    }
		 }
%>		  
        
        
          &nbsp;&nbsp;
          <a href="<%=request.getContextPath() %>/pages/mapping.jsf?dictionary=<%=mapping_schema%>&page_number=<%=next_page_num_str%>">Next</a>
      </td>
    </tr>
    <tr>
      <td class="textbody" align=left>
        Show
        
  <select name=resultsPerPage size=1 onChange="paginationForm.submit();">
  <%
    List resultsPerPageValues = UserSessionBean.getResultsPerPageValues();
    for (int i=0; i<resultsPerPageValues.size(); i++) {
        String resultsPerPageValue = (String) resultsPerPageValues.get(i);
        
        if (selectedResultsPerPage.compareTo(resultsPerPageValue) == 0) {
  %>      
        <option value="<%=resultsPerPageValue%>" selected><%=resultsPerPageValue%></option>
  <%        
        
        } else {
  %>      
        <option value="<%=resultsPerPageValue%>"><%=resultsPerPageValue%></option>
  <%        
        }

  }
  %>
  </select>
  <!--
        
        <h:selectOneMenu
          id="id" value="#{userSessionBean.selectedResultsPerPage}"
          valueChangeListener="#{userSessionBean.resultsPerPageChanged}" immediate="true" onchange="submit()"> 
          <f:selectItems value="#{userSessionBean.resultsPerPageList}"/>
        </h:selectOneMenu>
        
  -->        
        &nbsp;results per page
      </td>
      <td>
        &nbsp;&nbsp;
      </td>
      <td>
        &nbsp;
      </td>
    </tr>
  </table>
</form>