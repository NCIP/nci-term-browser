<%@ page import="gov.nih.nci.evs.browser.bean.IteratorBean" %>
<%@ page import="gov.nih.nci.evs.browser.bean.IteratorBeanManager" %>
<%@ page import="gov.nih.nci.evs.browser.properties.NCItBrowserProperties" %>

<%

    int prev_page_num = pageNum;
    int next_page_num = prev_page_num + 1;
    
    String istart_str = Integer.toString(istart);
    String iend_str = Integer.toString(iend);
    
    String prev_page_num_str = Integer.toString(pageNum);
    String next_page_num_str = Integer.toString(pageNum+1);
    
    String match_size = Integer.toString(50);
    
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
          <%
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