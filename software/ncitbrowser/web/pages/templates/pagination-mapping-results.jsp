<%@ page import="gov.nih.nci.evs.browser.bean.IteratorBean" %>
<%@ page import="gov.nih.nci.evs.browser.bean.IteratorBeanManager" %>
<%@ page import="gov.nih.nci.evs.browser.properties.NCItBrowserProperties" %>

<%

    int prev_page_num = pageNum;
    int next_page_num = prev_page_num + 1;
    
//System.out.println("(*) pagination: pageNum " + pageNum);
//System.out.println("(*) pagination: prev_page_num " + prev_page_num);
//System.out.println("(*) pagination: next_page_num " + next_page_num);

    

String dictionary_map = (String) request.getSession().getAttribute("dictionary");
bean = (MappingIteratorBean) request.getSession().getAttribute("mapping_search_results");

if (bean != null) {

    numRemaining = bean.getSize();

    
    String istart_str = Integer.toString(istart+1);
    String iend_str = Integer.toString(iend);
    
    String match_size = Integer.toString(numRemaining);
    
    if (istart == 0) {
    	iend_str = Integer.toString(iend+1);
    }
    

if (iend >= numRemaining) {
   iend_str = match_size;
}
    
    String prev_page_num_str = Integer.toString(pageNum-1);
    String next_page_num_str = Integer.toString(pageNum+1);


//System.out.println("(*) pagination: prev_page_num_str " + prev_page_num_str);
//System.out.println("(*) pagination: next_page_num_str " + next_page_num_str);

    
%>




<FORM NAME="paginationForm" METHOD="POST" action="<%=request.getContextPath() %>/pages/mapping_search_results.jsf?nav_type=mappings" >
  <table>
    <tr>
      <td class="textbody" align=left>
         <b>Results <%=istart_str%>-<%=iend_str%> of&nbsp;<%=match_size%></b>
      </td>
      <td>
         &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      </td>
      <!-- ---------------------------------------------------------------------- -->
      
      <td class="textbody" align=right>

       <%
          if (page_num > 1) {
        %>
        &nbsp;
        <i>
          <a href="<%=request.getContextPath() %>/pages/mapping_search_results.jsf?dictionary=<%=mapping_scheme%>&page_number=<%=prev_page_num_str%>">Prev</a>
          
        </i>&nbsp;
        <%
          }
          if (num_pages > 1) {
          
          
          int sliding_window_start = 1;
          int sliding_window_end = num_pages;
          int sliding_window_half_width = NCItBrowserProperties.getSlidingWindowHalfWidth();
          
          sliding_window_start = page_num - sliding_window_half_width;
          if (sliding_window_start < 1) sliding_window_start = 1;
          
          sliding_window_end = sliding_window_start + sliding_window_half_width * 2 - 1;
          if (sliding_window_end > num_pages) sliding_window_end = num_pages;

//System.out.println("num_pages: " + num_pages);
//System.out.println("page_num: " + page_num);


		 for (int idx=sliding_window_start; idx<=sliding_window_end; idx++) { 
		    String idx_str = Integer.toString(idx);
		    if (page_num != idx) {
		      %>
		        <a href="<%=request.getContextPath() %>/pages/mapping_search_results.jsf?dictionary=<%=mapping_scheme%>&page_number=<%=idx_str%>"><%=idx_str%></a>
		
			&nbsp;
		      <%
		    } else {
		      %>
			<%=idx_str%>&nbsp;
		      <%
		    }
		  }
          }
          
          
//System.out.println("(*) next_page_num: " + next_page_num);
//System.out.println("(*) num_pages: " + num_pages);
          
          
          if (next_page_num <= num_pages) {
        %>
          &nbsp;
          <i>
            <a href="<%=request.getContextPath() %>/pages/mapping_search_results.jsf?dictionary=<%=mapping_scheme%>&page_number=<%=next_page_num_str%>">Next</a>

          </i>
        <%
          }
        %>
      </td>

    </tr>
    <tr>
      <td class="textbody" align=left>
        Show
        
  <select name=resultsPerPage size=1 onChange="paginationForm.submit();">
  <%
  
	//System.out.println("(*) mapping pagination  selectedResultsPerPage: " + selectedResultsPerPage); 
  
  
  
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

<%
}
%>
