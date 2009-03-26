<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %> 
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="gov.nih.nci.evs.browser.utils.DataUtils" %>
<%@ page import="gov.nih.nci.evs.browser.utils.HistoryUtils" %>
<%@ page import="org.LexGrid.concepts.Concept" %>
<%@ page import="org.LexGrid.LexBIG.DataModel.Collections.NCIChangeEventList" %>
<%@ page import="org.LexGrid.LexBIG.DataModel.NCIHistory.NCIChangeEvent" %>
<%@ page import="org.LexGrid.LexBIG.DataModel.NCIHistory.types.ChangeType" %>
<%@ page contentType="text/html;charset=windows-1252"%>

<html>
  <head>
    <title>NCI Thesaurus Hierarchy</title>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />
  </head>

  <body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" ><f:view>

    <%
      DateFormat dataFormatter = new SimpleDateFormat("yyyy-MM-dd");
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

            <tr><td align="left">
              <b>History</b>
            </td></tr>
                
            <tr><td align="left">
              <table summary="Enter summary of data here" cellpadding="3" cellspacing="0" border="0" class="dataTable" width="100%">
                <tr>
                  <th class="dataTableHeader" scope="col" align="left">Edit Action</th>
                  <th class="dataTableHeader" scope="col" align="left">Date</th>
                  <th class="dataTableHeader" scope="col" align="left">Reference Concept</th>
                </tr>
                
                <%
                  HistoryUtils hUtils = new HistoryUtils();
                  NCIChangeEventList list = hUtils.getEditActions(code);
                  Enumeration enumeration = list.enumerateEntry();
                  int i=0;
                  while (enumeration.hasMoreElements()) {
                    NCIChangeEvent event = (NCIChangeEvent) enumeration.nextElement();
                    ChangeType type = event.getEditaction();
                    Date date = event.getEditDate();
                    String refCode = event.getReferencecode();
                    String refName = event.getReferencename();
                    String refInfo = "none";
                    if (refCode != null && refCode.length() > 0 && ! refCode.equalsIgnoreCase("null")) {
                      Concept refConcept = DataUtils.getConceptByCode(dictionary, vers, ltag, refCode);
                      refName = refConcept.getEntityDescription().getContent();
                      refInfo = refName + " (Code " + refCode + ")";
                    }
                    if (i++ % 2 == 0) {
                %>
                      <tr class="dataRowDark">
                <%    
                    } else {
                %>
                      <tr class="dataRowLight"> 
                <% 
                    }
                %>
                        <td class="dataCellText"><%=type%></td>
                        <td class="dataCellText"><%=dataFormatter.format(date)%></td>
                        <td class="dataCellText"><%=refInfo%></td>
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
