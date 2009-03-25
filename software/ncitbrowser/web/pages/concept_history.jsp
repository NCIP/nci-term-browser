<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %> 
<%@ page import="gov.nih.nci.evs.browser.utils.DataUtils" %>
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

            <tr><td align="left">
              <b>Sample History (UNDER CONSTRUCTION)</b>
            </td></tr>
                
            <tr><td align="left">
              <table summary="Enter summary of data here" cellpadding="3" cellspacing="0" border="0" class="dataTable" width="100%">
                <tr>
                  <th class="dataTableHeader" scope="col" align="left">Edit Action</th>
                  <th class="dataTableHeader" scope="col" align="left">Date</th>
                  <th class="dataTableHeader" scope="col" align="left">Reference Concept</th>
                </tr>
                
                <%
                  String actions[] = new String[] { "create", "modify", "modify", "modify" };
                  String dates[] = new String[] { "2003-08-12", "2003-09-23", "2003-10-31", "2003-12-25" };
                  String references[] = new String[] { "none", "none", "none", "none" };
                  for (int i=0; i<actions.length; ++i) {
                    String action = actions[i];
                    String date = dates[i];
                    String reference = references[i];
                    if (i % 2 == 0) {
                %>
                      <tr class="dataRowDark">
                <%    
                    } else {
                %>
                      <tr class="dataRowLight"> 
                <% 
                    }
                %>
                        <td class="dataCellText"><%=action%></td>
                        <td class="dataCellText"><%=date%></td>
                        <td class="dataCellText"><%=reference%></td>
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
