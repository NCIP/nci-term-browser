<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="java.util.Vector"%>
<%@ page import="org.LexGrid.concepts.Concept" %>
<html>
<head>
<title>NCI Thesaurus Browser Home</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />
<script type="text/javascript" src="<%= request.getContextPath() %>/js/script.js">
</script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/search.js">
</script>
</head>
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" >

<f:view>

<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
	<tr> 
		<td height="1%" VALIGN="TOP"> 	
			<%@ include file="/pages/templates/nciHeader.html" %>
		        <%@ include file="/pages/templates/header.xhtml" %>
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
			    <b>NCI Thesaurus Subsets</b>
			</td></tr>
			
			<tr><td allign="left">
			    <hr></hr>
			</td></tr>

			<tr><td class="standardText"><i>
			    There are roughly 100 specially defined subsets in NCI Thresaurus. Each has its own NCIt concept,
			    defining the nature of the subset, and these are hierarchically organized under the parent concept:
			    C54443 Terminology Subset. These subset concepts are linked to subset-member concepts through 
			    some 10,000 association relationships.
			</i></td></tr>

			<tr><td class="standardText"><i>
			    Most of these subsets are federal and internatinal coding standards maintained in active collaboration with our FDA, CDISC, and other
			    partners. They are currently distributd largely as tab-delimited data and Excel spreadsheet files accessible
			    via FTP. Below are the three most important subset collections, curently presented on these Cancer.gov Web pages:
			</i></td></tr>

                        <tr><td>
                            <TABLE width="650" border="0" align="left" cellpadding="15" cellspacing="0" >
 
 
				<tr>
				    <td class="standardText"><i>
				    <a href="#" onclick="javascript:window.open('http://www.cancer.gov/cancertopics/terminologyresources/CDISC', '_blank','top=100, left=100, height=740, width=780, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
				    Clinical Data Interchange Standards Consortium (CDISC) Termiology:</a>
				    
				    CDISC is an international, non-profit organization hat develops and supports global data standards for medical research.
				    CDISC is working actively with EVS to develop and support controlled terminology in several aras, notably CDISC's Study 
				    Data Tabulation Model (SDTM).
				    </i></td>
				
				    <td class="standardText"><i>
				    <a href="#" onclick="javascript:window.open('http://www.cancer.gov/cancertopics/terminologyresources/FDA', '_blank','top=100, left=100, height=740, width=780, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
				    U.S. Food ad Drug Administration Termiology:</a>
				    
				    The FDA is working with EVS to develop and supporyt controlled
				    terminology in several areas. More than 10,000 FDA terms and codes are stored in NCI Thesaurus (NCIt).
				    </i></td>

				    <td class="standardText"><i>
				    <a href="#" onclick="javascript:window.open('http://www.cancer.gov/cancertopics/terminologyresources/FMT', '_blank','top=100, left=100, height=740, width=780, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
				    Federal Medication Terminologies:</a>
				    
				    The Federal Medication (FedMed) interagency collaboration is organizing
				    an agreed set of standard, comprehensive, freely and easily accessible Federal Medication Terminologies (FMT)
				    to improve the exchange and public availability of medication information. FedMed is a joint effort of these Federal partner
				    agencies.
				    </i></td>
				
				</tr>
                        
                            </table> 
                        </td></tr>


			<tr><td class="standardText"><i>
			    EVS is developing extended method to create, browse, download, and have direct computer API access to these and other NCIt subsets.
			</i></td></tr>


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


