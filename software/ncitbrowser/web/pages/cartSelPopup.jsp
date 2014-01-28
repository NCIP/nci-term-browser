<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=windows-1252"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns:c="http://java.sun.com/jsp/jstl/core">
<head>
  <title>NCI Term Browser</title>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />
</head>
<body>
<f:view>
<div class="pagecontent">   
   <h:form id="popupForm" acceptcharset="UTF-8">
      <table border="0">
         <tr>
            <td align="left"><b>Select export versions</b></td>
         </tr>
         <tr>
            <td align="left">
               <h:selectManyListbox value="#{CartActionBean.selectedVersionItems}" size="10" style="width:250px;">  
                   <f:selectItems value="#{CartActionBean.selectVersionItems}" />  
               </h:selectManyListbox>
            </td>
         </tr>
         <tr> 
            <td align="left">
               <h:outputLink value="javascript:checkIfDone();">
                  <h:graphicImage value="../images/exportxml.gif" alt="Export XML" title="Export XML" style="border: none" />
               </h:outputLink>                                 
               &#xA0;    
               <h:outputLink value="javascript:window.close();">
                  <h:graphicImage value="../images/close.gif" alt="Close window" title="Close window" style="border: none" />
               </h:outputLink>
               <h:commandButton style="display: none; visibility: hidden;"
                  id="startExport" action="#{CartActionBean.exportCartXML}">
               </h:commandButton>                              
            </td>   
         </tr> 
      </table>           
   </h:form>   
</div>
<script language="javascript" type="text/javascript">
   function checkIfDone() {
	   document.getElementById('popupForm:startExport').click();
	   alert('Export started. Download page should appear shortly.');
	   setTimeout ('window.close()',1200); 	        
   }
</script>
</f:view>
</body>
</html>
