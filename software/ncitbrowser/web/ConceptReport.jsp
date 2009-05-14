<html>
 <body>
   <%
     try {
   %>
       <jsp:forward page="/pages/concept_details.jsf" />
   <%
     } catch(Exception e) {
       String msg = "Internal Error: " + e.getMessage();
       request.getSession().setAttribute("message", msg);     
   %>
       <jsp:forward page="/pages/message.jsf" />
   <%
     } 
   %>
 </body>
</html>
