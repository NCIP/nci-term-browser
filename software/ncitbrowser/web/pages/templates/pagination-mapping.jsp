<%@ page import="gov.nih.nci.evs.browser.bean.IteratorBean" %>
<%@ page import="gov.nih.nci.evs.browser.bean.IteratorBeanManager" %>
<%@ page import="gov.nih.nci.evs.browser.properties.NCItBrowserProperties" %>

<%

    int prev_page_num = pageNum;
    int next_page_num = prev_page_num + 1;
    
    String istart_str = Integer.toString(istart);
    String iend_str = Integer.toString(iend);
    int numRemaining = iend+1;
    
    
System.out.println("(1) pagination-mapping.jsp numRemaining: " + numRemaining);    
if (iterator != null) {    
    
    try {
        numRemaining = iterator.numberRemaining();
    } catch (Exception ex) {
        ex.printStackTrace();
    }
    
}
    
System.out.println("(2) pagination-mapping.jsp numRemaining: " + numRemaining);    
    
    
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
        <h:selectOneMenu
          id="id" value="#{userSessionBean.selectedResultsPerPage}"
          valueChangeListener="#{userSessionBean.resultsPerPageChanged}" immediate="true" onchange="submit()"> 
          <f:selectItems value="#{userSessionBean.resultsPerPageList}"/>
        </h:selectOneMenu>
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