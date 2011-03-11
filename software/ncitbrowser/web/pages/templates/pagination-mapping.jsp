<%@ page import="gov.nih.nci.evs.browser.bean.IteratorBean" %>
<%@ page import="gov.nih.nci.evs.browser.bean.IteratorBeanManager" %>
<%@ page import="gov.nih.nci.evs.browser.properties.NCItBrowserProperties" %>

<%

System.out.println("pagination-mapping.jsp pageNum: " + pageNum);

    int prev_page_num = pageNum - 1;
    int next_page_num = pageNum + 1;
    
    
    
String istart_str = Integer.toString(istart+1);
String iend_str = Integer.toString(iend+1);
  
  
System.out.println("pagination-mapping.jsp istart_str: " + istart_str);
System.out.println("pagination-mapping.jsp iend_str: " + iend_str);

  
String dictionary_map = (String) request.getSession().getAttribute("dictionary");
System.out.println("(*) dictionary_map " + dictionary_map);


bean = (MappingIteratorBean) request.getSession().getAttribute("mapping_search_results");

if (bean == null) {
        scheme2MappingIteratorBeanMap = (HashMap) request.getSession().getAttribute("scheme2MappingIteratorBeanMap");
	bean = (MappingIteratorBean) scheme2MappingIteratorBeanMap.get(dictionary_map);
}


numRemaining = bean.getSize();

System.out.println("(2) pagination-mapping.jsp iterator.getSize(): " + numRemaining);    
   
    
    String match_size = Integer.toString(numRemaining);
    
    String prev_page_num_str = Integer.toString(prev_page_num);
    String next_page_num_str = Integer.toString(next_page_num);
    

    
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
      <!-- ---------------------------------------------------------------------- -->
      
      <td class="textbody" align=right>

       <%
          if (page_num > 1) {
        %>
        &nbsp;
        <i>
          <a href="<%=request.getContextPath() %>/pages/mapping.jsf?dictionary=<%=mapping_schema%>&page_number=<%=prev_page_num_str%>">Prev</a>
          
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

System.out.println("num_pages: " + num_pages);
System.out.println("page_num: " + page_num);


		 for (int idx=sliding_window_start; idx<=sliding_window_end; idx++) { 
		    String idx_str = Integer.toString(idx);
		    
		    if (idx == 1 && page_num == idx) {
		    %>
		        <%=idx_str%>&nbsp;
		    <%    
		    } else if (page_num != idx) {
		      %>
		        <a href="<%=request.getContextPath() %>/pages/mapping.jsf?dictionary=<%=mapping_schema%>&page_number=<%=idx_str%>"><%=idx_str%></a>
		
			&nbsp;
		      <%
		    } else {
		      %>
			<%=idx_str%>&nbsp;
		      <%
		    }
		  }
          }
          
          if (next_page_num < num_pages) {
        %>
          &nbsp;
          <i>
            <a href="<%=request.getContextPath() %>/pages/mapping.jsf?dictionary=<%=mapping_schema%>&page_number=<%=next_page_num_str%>">Next</a>

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