<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=windows-1252"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns:c="http://java.sun.com/jsp/jstl/core">
<head>
  <title>NCI Thesaurus</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />
</head>
<body>
<f:view>
<div class="pagecontent">   
   <h:form>
      <table border="0">
         <tr>
            <td align="left"><b>Select export versions</b></td>
         </tr>
         <tr>
            <td align="left">
               <h:selectManyListbox size="10" style="width:250px;">  
                   <f:selectItems value="#{CartActionBean.selectVersionItems}" />  
               </h:selectManyListbox>
            </td>
         </tr>
         <tr> 
            <td align="left">
               <h:commandLink action="#{CartActionBean.exportCartXML}" styleClass="texttitle-blue-small">
                  <h:graphicImage value="../images/exportxml.gif" alt="Export XML" title="Export cart contents in LexGrid XML format" style="border: none" />               
               </h:commandLink>
               &#xA0;    
               <h:outputLink value="javascript:window.close();">
                  <h:graphicImage value="../images/close.gif" alt="Close window" title="Close window" style="border: none" />
               </h:outputLink>               
            </td>   
         </tr> 
      </table>           
   </h:form>   
</div>
</f:view>
</body>
</html>