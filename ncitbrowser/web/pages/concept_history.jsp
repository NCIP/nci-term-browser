<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %> 
<%@ page import="java.util.Vector" %>
<%@ page import="gov.nih.nci.evs.browser.utils.DataUtils" %>
<%@ page import="gov.nih.nci.evs.browser.utils.HistoryUtils" %>
<%@ page import="org.LexGrid.concepts.Concept" %>
<%@ page contentType="text/html;charset=windows-1252"%>

<html>
  <head>
    <title>NCI Thesaurus Hierarchy</title>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />
  </head>

  <body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" ><f:view>

    <%
      String code = (String) request.getParameter("code");
      String dictionary = (String) request.getParameter("dictionary");
      
      String vers = null;
      String ltag = null;
      Concept concept = DataUtils.getConceptByCode(dictionary, vers, ltag, code);
      request.getSession().setAttribute("concept", concept);
      String name = concept.getEntityDescription().getContent();
    %>

    <table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
      <tr> 
        <td height="1%">    
          <%@ include file="/pages/templates/nciHeader.html" %>
          <br/>
        </td> 
      </tr>

      <tr valign="top"> 
        <td>    
          <table summary="" cellpadding="3" cellspacing="0" border="0" width="100%">
            <tr><td align="left">
              <b><%=name%> (Code <%=code%>)</b>
              <hr/><br/>
            </td></tr>

            <tr><td align="left" valign="bottom" class="standardText">
              <b>History</b>
            </td></tr>
                
            <tr><td align="left">
              <%
                Vector headers = HistoryUtils.getTableHeader();
                Vector rows = HistoryUtils.getEditActions(dictionary, vers, ltag, code);
              %>
              <table cellpadding="3" cellspacing="0" border="0" class="dataTable" width="100%">
                <tr>
                  <%
                  for (int i=0; i<headers.size(); ++i) {
                    Object header = headers.elementAt(i); 
                  %>
                    <th class="dataTableHeader" scope="col" align="left"><%=header%></th>
                  <%
                  }
                  %>
                </tr>
                
                <%
                  for (int i=0; i<rows.size(); ++i) {
                    String rowColor = (i%2 == 0) ? "dataRowDark" : "dataRowLight"; 
                %>
                    <tr class="<%=rowColor%>">
                <%
                    String row = (String) rows.elementAt(i);
                    Vector cols = DataUtils.parseData(row, "|");
                    for (int j=0; j<cols.size(); ++j) {
                      Object cell = cols.elementAt(j);
                      String iTag = "", iTagEnd = "";
                      if (j==0 || j==2)
                          { iTag = "<i>"; iTagEnd = "</i>"; }
                %>
                        <td class="dataCellText"><%=iTag%><%=cell%><%=iTagEnd%></td>
                <%
                    }
                %>
                    </tr>
                <%  
                  }
                %>
                
              <table>
            </td></tr>
          </table>         
        </td> 
      </tr>
    </table>
  </f:view></body>
</html>
