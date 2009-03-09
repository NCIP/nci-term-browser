<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="java.util.Vector"%>
<%@ page import="org.LexGrid.concepts.Concept" %>
<html>
<head>
<title>NCI Thesaurus Browser Home</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/dhtmlcombo.css" />
<script type="text/javascript" src="<%= request.getContextPath() %>/js/script.js">
</script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/search.js">
</script>
</head>
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" >

<f:view>


<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">


	<tr> 
		<td height="1%"> 	
			<%@ include file="/pages/templates/nciHeader.html" %>
		        <%@ include file="/pages/templates/header.xhtml" %>
		</td> 
        </tr>
	<tr>
		<td VALIGN="TOP">
		     <table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
			  <tr> 
			      <td VALIGN="TOP"> 
				<table cellspacing="0" border=0 cellpadding="0" class="tblbdsers" width="100%" height="100%">
					<tr> 
					   <td width="160" valign="top"  class="tblside"> 
					       <div align="left"> 
						   <%@ include file="/pages/templates/menuBar.xhtml" %>
					       </div>
					   </td>
					</tr>
				</table>
			      </td>
			  </tr>   
		      </table>
                </td>
        </tr>        
       
	<tr>
		<td height="1%">
                      <%@ include file="/pages/templates/quickLink.xhtml" %>
                </td>
        </tr>   
       
       
	<tr>
		<td>



		<TABLE width="650" border="0" align="left" cellpadding="15" cellspacing="0" >
			<tr><td allign="left">
			    <b>Contact Us</b>
			</td></tr>
			
			<tr><td allign="left">
			    <hr></hr>
			</td></tr>

			<tr><td class="standardText"><b>
			    If you would like information immediately, please call:
			    </b></td>
			</tr>
			
			<tr><td><table>
			    <tr><td>&nbsp;</td><td class="standardText"><b>1-800-4-CANCER (1-800-422-6237)</b></td></tr>
			    <tr><td>&nbsp;</td><td class="standardText"><i>9:00 a.m. to 4:30 p.m. local time, Monday through Friday</i></td></tr>
			    <tr><td>&nbsp;</td><td class="standardText">TTY 1-800-332-8615</td></tr>
			</table></td></tr>


			<tr><td class="standardText"><b>
			    Cras lobortis tincidunt nisi. In hac habitasse platea dictumst. In a orci. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Integer sed arcu. Mauris luctus nisi sed dolor. Vestibulum consequat pellentesque eros. Proin malesuada congue massa. Maecenas facilisis leo ut purus. Fusce orci leo, commodo sed, pellentesque eleifend, euismod ac, justo. Morbi convallis varius urna.
			    </b></td>
			</tr>

			<tr><td class="standardText"><b>
			    You must fill in every box below
			    </b></td>
			</tr>
			
			<form method="post" 
			   action="mailto:ncicb@pop.nci.nih.gov" 
			     enctype="text/plain">

			<tr><td class="standardText"><i>
			    Enter the subject of your email
			    </i></td>
			</tr>

			<tr><td class="standardText">
                            <input CLASS="input.formField" size="22" name="subject" onFocus="active = true" onBlur="active = false" onKeyPress="return ifenter(event,this.form)"> 
			</td></tr>			
			
			<tr><td class="standardText"><i>
			    Enter your message.
			    <p>
			    Please include all pertinent details within the contact message box.
			    We do not open attachments to e-mail messages.
			    </p>
			    </i></td>
			</tr>
			
                        <tr><td class="standardText">
			<TEXTAREA Name="message" rows="4" cols="20"></TEXTAREA> 
                        </td></tr>  

			<tr><td class="standardText"><i>
                            E-mail address 
			</i></td></tr>			
			<tr><td class="standardText"><i>
                            &nbsp;&nbsp;For example, jdoe@yahoo.com 
			</i></td></tr>			

			<tr><td class="standardText">
                            <input CLASS="input.formField" size="22" name="emailaddress" onFocus="active = true" onBlur="active = false" onKeyPress="return ifenter(event,this.form)"> 
			</td></tr>			

                        <tr><td class="input.actionButton">
                        <INPUT  type="submit" value="Submit">&nbsp;&nbsp;<INPUT type="reset" value="Clear">
                        </td></tr>
                        
                        </form>

                        <tr><td>
                            <a href="http://www.cancer.gov/policies/page3" ><i>Privacy Policy on E-mail Messages Sent to the NCI Web Site</i></a>
                        </td></tr>
		</TABLE>
	
	
						
		</td>
	</tr>
	<tr>
		<td height="20" width="100%" class="footerMenu">
		     <%@ include file="/pages/templates/applicationFooter.html" %>
		</td>
	</tr>

	<tr>
		<td>
		     <%@ include file="/pages/templates/nciFooter.html" %>
		</td>
	</tr>


</table>


</f:view>

</body>
</html>


