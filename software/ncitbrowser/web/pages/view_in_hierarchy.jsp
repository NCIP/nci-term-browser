<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ page contentType="text/html;charset=windows-1252"%>

<%@ page import="java.util.Vector"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>

<%@ page import="org.LexGrid.concepts.Entity" %>
<%@ page import="gov.nih.nci.evs.browser.common.Constants" %>
<%@ page import="gov.nih.nci.evs.browser.utils.*" %>

<%

String basePath = request.getContextPath();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
  <head>
  <title>Vocabulary Hierarchy</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />
  <link rel="shortcut icon" href="<%= request.getContextPath() %>/favicon.ico" type="image/x-icon" />
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/yui/fonts.css" />
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/yui/grids.css" />
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/yui/code.css" />
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/yui/tree.css" />
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/script.js"></script>

  <script language="JavaScript">


function Toggle(node)
{



	// Unfold the branch if it isn't visible
	if (node.nextSibling.style.display == 'none')
	{
		// Change the image (if there is an image)
		if (node.children.length > 0)
		{
			if (node.children.item(0).tagName == "IMG")
			{
				node.children.item(0).src = "lm.gif";
			}
		}

		node.nextSibling.style.display = '';
	}
	// Collapse the branch if it IS visible
	else
	{
		// Change the image (if there is an image)
		if (node.children.length > 0)
		{
			if (node.children.item(0).tagName == "IMG")
			{
				node.children.item(0).src = "lp.gif";
			}
		}

		node.nextSibling.style.display = 'none';
	}

}






function replace_content(node_id) {

	var x = "";
	var node_id_0 = node_id;

	if (node_id.indexOf("_") == -1) {
	    node_id = node_id + "_";

		x = x + "<div id=\"" + node_id + "\">";
		//x = x + "<br/>"
		x = x + "<IMG src=\"<%= request.getContextPath() %>/images/minus.gif\" onClick=\"replace_content(\'" + node_id + "\')\"/>&nbsp;" + node_id_0;
		x = x + "<table>";
		x = x + "<tr><td>";
		x = x + "&nbsp;&nbsp;child concept #1";
		x = x + "</td></tr>";

		x = x + "<tr><td>";
		x = x + "&nbsp;&nbsp;child concept #2";
		x = x + "</td></tr>";

		x = x + "<tr><td>";
		x = x + "&nbsp;&nbsp;child concept #3";
		x = x + "</td></tr>";
                x = x + "</table>";
		x = x + "</div>";

		document.getElementById(node_id_0).innerHTML=x;

	} else {

	    var k = node_id.lastIndexOf("_");
	    node_id = node_id.substring(0, k);

		x = x + "<div id=\"" + node_id + "\">";
		//x = x + "<br/>";
		x = x + "<IMG src=\"<%= request.getContextPath() %>/images/plus.gif\" onClick=\"replace_content(\'" + node_id + "\')\"/>&nbsp;" + node_id;
		x = x + "</div>";

        document.getElementById(node_id).innerHTML=x;

	}


}



    function loadNodeData(node, fnLoadComplete) {
      var id = node.data.id;

      var responseSuccess = function(o)
      {
        var path;
        var dirs;
        var files;
        var respTxt = o.responseText;
        var respObj = eval('(' + respTxt + ')');
        var fileNum = 0;
        var categoryNum = 0;
        if ( typeof(respObj.nodes) != "undefined") {
          for (var i=0; i < respObj.nodes.length; i++) {
            var name = respObj.nodes[i].ontology_node_name;
            var nodeDetails = "javascript:onClickTreeNode('" + respObj.nodes[i].ontology_node_id + "');";
            var newNodeData = { label:name, id:respObj.nodes[i].ontology_node_id, href:nodeDetails };
            var newNode = new YAHOO.widget.TextNode(newNodeData, node, false);
            if (respObj.nodes[i].ontology_node_child_count > 0) {
              //newNode.setDynamicLoad(loadNodeData);
            }
          }
        }
        //tree.draw();
        fnLoadComplete();
      }

      var responseFailure = function(o){
        alert('responseFailure: ' + o.statusText);
      }

      var callback =
      {
        success:responseSuccess,
        failure:responseFailure
      };

      var ontology_display_name = document.forms["pg_form"].ontology_display_name.value;
      var ontology_version = document.forms["pg_form"].ontology_version.value;
      var cObj = YAHOO.util.Connect.asyncRequest('GET','<%= request.getContextPath() %>/ajax?action=expand_tree&ontology_node_id=' +id+'&ontology_display_name='+ontology_display_name+'&version='+ontology_version,callback);
    }



</script>
</head>

  <f:view>
    <!-- Begin Skip Top Navigation -->
      <a href="#evs-content" class="hideLink" accesskey="1" title="Skip repetitive navigation links">skip navigation links</A>
    <!-- End Skip Top Navigation --> 
    <div id="popupContainer">
      <!-- nci popup banner -->
      <div class="ncipopupbanner">
        <a href="http://www.cancer.gov" target="_blank" alt="National Cancer Institute"><img src="<%=basePath%>/images/nci-banner-1.gif" width="440" height="39" border="0" alt="National Cancer Institute" /></a>
        <a href="http://www.cancer.gov" target="_blank" alt="National Cancer Institute"><img src="<%=basePath%>/images/spacer.gif" width="48" height="39" border="0" alt="National Cancer Institute" class="print-header" /></a>
      </div>
      <!-- end nci popup banner -->
      <div id="popupMainArea">
        <a name="evs-content" id="evs-content"></a>
        <table class="evsLogoBg" cellspacing="0" cellpadding="0" border="0">
        <tr>
          <td valign="top">
            <a href="http://evs.nci.nih.gov/" target="_blank" alt="Enterprise Vocabulary Services">
              <img src="<%=basePath%>/images/evs-popup-logo.gif" width="213" height="26" alt="EVS: Enterprise Vocabulary Services" title="EVS: Enterprise Vocabulary Services" border="0" />
            </a>
          </td>
          <td valign="top"><div id="closeWindow"><a href="javascript:window.close();"><img src="<%=basePath%>/images/thesaurus_close_icon.gif" width="10" height="10" border="0" alt="Close Window" />&nbsp;CLOSE WINDOW</a></div></td>
        </tr>
        </table>
        


<%
JSPUtils.JSPHeaderInfoMore info = new JSPUtils.JSPHeaderInfoMore(request);
String hierarchy_dictionary = info.dictionary;
String hierarchy_version = info.version;

//System.out.println("hierarchy.jsp hierarchy_version: " + hierarchy_version);

String hierarchy_schema = HTTPUtils.cleanXSS((String) request.getParameter("schema"));
if (hierarchy_dictionary != null && hierarchy_schema == null) hierarchy_schema = hierarchy_dictionary;
hierarchy_schema = DataUtils.getFormalName(hierarchy_schema);

String localName = DataUtils.getLocalName(hierarchy_schema);
String formalName = DataUtils.getFormalName(localName);
String term_browser_version = DataUtils.getMetadataValue(formalName, hierarchy_version, "term_browser_version");
String display_name = DataUtils.getMetadataValue(formalName, hierarchy_version, "display_name");


if (display_name == null || display_name.compareTo("null") == 0) {
   display_name = DataUtils.getLocalName(hierarchy_schema); 
}


String release_date = DataUtils.getVersionReleaseDate(hierarchy_schema, hierarchy_version);
boolean display_release_date = true;
if (release_date == null || release_date.compareTo("") == 0) {
	display_release_date = false;
}
 
	 
	 

if (hierarchy_schema.compareTo("NCI Thesaurus") == 0) {
%>
    <div>
      <img src="<%=basePath%>/images/thesaurus_popup_banner.gif" width="612" height="56" alt="NCI Thesaurus" title="" border="0" />
      
<%      
	 if (display_release_date) {
%>	 
             <span class="texttitle-blue-rightjust-2"><%=HTTPUtils.cleanXSS(term_browser_version)%> (Release date: <%=release_date%>)</span>
<%
	 } else {
%>	 
	     <span class="texttitle-blue-rightjust-2"><%=HTTPUtils.cleanXSS(term_browser_version)%></span>
<%
	 }      
%>      

    </div>
<%
} else {
     String hierarchy_shortName = DataUtils.getLocalName(hierarchy_schema);
%>
    <div>
      <img src="<%=basePath%>/images/other_popup_banner.gif" width="612" height="56" alt="NCI Thesaurus" title="" border="0" />
      <div class="vocabularynamepopupshort"><%=HTTPUtils.cleanXSS(display_name)%>
      
<%      
	 if (display_release_date) {
%>	 
             <span class="texttitle-blue-rightjust"><%=HTTPUtils.cleanXSS(term_browser_version)%> (Release date: <%=release_date%>)</span>
<%
	 } else {
%>	 
	     <span class="texttitle-blue-rightjust"><%=HTTPUtils.cleanXSS(term_browser_version)%></span>
<%
	 }      
%>         

      </div>
    </div>
<%
}
%>

        <div id="popupContentArea">
          <table width="580px" cellpadding="3" cellspacing="0" border="0">
            <tr class="textbody">
              <td class="pageTitle" align="left">
                <%=HTTPUtils.cleanXSS(display_name)%> Hierarchy
              </td>
              <td class="pageTitle" align="right">
                <font size="1" color="red" align="right">
                  <a href="javascript:printPage()"><img src="<%= request.getContextPath() %>/images/printer.bmp" border="0" alt="Send to Printer"><i>Send to Printer</i></a>
                </font>
              </td>
            </tr>
          </table>
          <!-- Tree content -->
          <form id="pg_form">
          

<table border=0>

            <%
              String ontology_node_id = HTTPUtils.cleanXSS((String) request.getParameter("code"));

String schema = HTTPUtils.cleanXSS((String) request.getParameter("schema"));
String ontology_version = HTTPUtils.cleanXSS((String) request.getParameter("version"));



String ontology_display_name = HTTPUtils.cleanXSS((String) request.getParameter("schema"));
if (ontology_display_name == null) {
    ontology_display_name = HTTPUtils.cleanXSS((String) request.getParameter("dictionary"));
}


                ViewInHierarchyUtil util = new ViewInHierarchyUtil();
		List tree_data = util.getTree(ontology_display_name, ontology_version, ontology_node_id, 1);
		
System.out.println("***** (JSP): tree_data lines = " + 	tree_data.size());	
		
		
		String html_line = new ViewInHierarchyUtil().toHTML(basePath, tree_data);
		
%>    
          <%=html_line%>





<tr><td><div id="C20189"><img src="/ncitbrowser/images/minus.gif" onClick="replace_content('C20189')"/>&nbsp;Property or Attribute</div></td></tr><tr><td><div id="C25447">&nbsp;&nbsp;&nbsp;&nbsp;<img src="/ncitbrowser/images/minus.gif" onClick="replace_content('C25447')"/>&nbsp;Characteristic</div></td></tr><tr><td><div id="C25377">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="/ncitbrowser/images/minus.gif" onClick="replace_content('C25377')"/>&nbsp;Appearance</div></td></tr><tr><td><div id="C37927">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="/ncitbrowser/images/minus.gif" onClick="replace_content('C37927')"/>&nbsp;Color</div></td></tr><tr><td><div id="C48323">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Black</div></td></tr><tr><td><div id="C48333">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Blue</div></td></tr><tr><td><div id="C48332">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Brown</div></td></tr><tr><td><div id="C48324">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Gray</div></td></tr><tr><td><div id="C48329">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Green</div></td></tr><tr><td><div id="C48331">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Orange</div></td></tr><tr><td><div id="C48328">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Pink</div></td></tr><tr><td><div id="C48327">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Purple</div></td></tr><tr><td><div id="C48326">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Red</div></td></tr><tr><td><div id="C96298">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Tan</div></td></tr><tr><td><div id="C48334">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Turquoise</div></td></tr><tr><td><div id="C48325">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;White</div></td></tr><tr><td><div id="C48330">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Yellow</div></td></tr><tr><td><div id="C90392">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Hair Coat Color</div></td></tr><tr><td><div id="C94578">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Stained Appearance</div></td></tr><tr><td><div id="C53251">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="/ncitbrowser/images/plus.gif" onClick="replace_content('C53251')"/>&nbsp;Adverse Event Characteristics</div></td></tr><tr><td><div id="C61468">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Aspect</div></td></tr><tr><td><div id="C73486">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Aspect Ratio</div></td></tr><tr><td><div id="C52089">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Big-O Notation</div></td></tr><tr><td><div id="C70753">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Brightness</div></td></tr><tr><td><div id="C25443">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="/ncitbrowser/images/plus.gif" onClick="replace_content('C25443')"/>&nbsp;Capacity</div></td></tr><tr><td><div id="C41185">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="/ncitbrowser/images/plus.gif" onClick="replace_content('C41185')"/>&nbsp;Concentration</div></td></tr><tr><td><div id="C70749">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Contour</div></td></tr><tr><td><div id="C70754">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Contrast</div></td></tr><tr><td><div id="C45781">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="/ncitbrowser/images/plus.gif" onClick="replace_content('C45781')"/>&nbsp;Density</div></td></tr><tr><td><div id="C25333">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Depth</div></td></tr><tr><td><div id="C42676">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="/ncitbrowser/images/plus.gif" onClick="replace_content('C42676')"/>&nbsp;Dimensionality</div></td></tr><tr><td><div id="C54215">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Directionality</div></td></tr><tr><td><div id="C62263">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="/ncitbrowser/images/plus.gif" onClick="replace_content('C62263')"/>&nbsp;Diversity</div></td></tr><tr><td><div id="C25489">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Dryness</div></td></tr><tr><td><div id="C73619">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="/ncitbrowser/images/plus.gif" onClick="replace_content('C73619')"/>&nbsp;Feature</div></td></tr><tr><td><div id="C73792">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Flip Angle</div></td></tr><tr><td><div id="C75445">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Gait</div></td></tr><tr><td><div id="C88214">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Generation</div></td></tr><tr><td><div id="C25347">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="/ncitbrowser/images/plus.gif" onClick="replace_content('C25347')"/>&nbsp;Height</div></td></tr><tr><td><div id="C70589">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Abundance</div></td></tr><tr><td><div id="C49145">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Accuracy</div></td></tr><tr><td><div id="C64358">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Alert</div></td></tr><tr><td><div id="C49033">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Alignment</div></td></tr><tr><td><div id="C25414">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Aliquot</div></td></tr><tr><td><div id="C70429">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Ambiguity</div></td></tr><tr><td><div id="C67505">&nbsp;&nbsp;&nbsp;&nbsp;<img src="/ncitbrowser/images/plus.gif" onClick="replace_content('C67505')"/>&nbsp;Angle</div></td></tr><tr><td><div id="C25244">&nbsp;&nbsp;&nbsp;&nbsp;<img src="/ncitbrowser/images/plus.gif" onClick="replace_content('C25244')"/>&nbsp;Area</div></td></tr><tr><td><div id="C61532">&nbsp;&nbsp;&nbsp;&nbsp;<img src="/ncitbrowser/images/plus.gif" onClick="replace_content('C61532')"/>&nbsp;Arrangement</div></td></tr><tr><td><div id="C28025">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Asymmetry</div></td></tr><tr><td><div id="C89327">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Be</div></td></tr><tr><td><div id="C93270">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Biological Profile</div></td></tr><tr><td><div id="C37919">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Blur</div></td></tr><tr><td><div id="C85883">&nbsp;&nbsp;&nbsp;&nbsp;<img src="/ncitbrowser/images/plus.gif" onClick="replace_content('C85883')"/>&nbsp;Burden</div></td></tr><tr><td><div id="C28135">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Caloric Intake</div></td></tr><tr><td><div id="C74979">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Capability</div></td></tr><tr><td><div id="C54199">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Cardinality</div></td></tr><tr><td><div id="C71490">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Case Sensitive</div></td></tr><tr><td><div id="C86056">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Causing</div></td></tr><tr><td><div id="C17895">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Cell Lineage</div></td></tr><tr><td><div id="C12913"><img src="/ncitbrowser/images/plus.gif" onClick="replace_content('C12913')"/>&nbsp;Abnormal Cell</div></td></tr><tr><td><div id="C43431"><img src="/ncitbrowser/images/plus.gif" onClick="replace_content('C43431')"/>&nbsp;Activity</div></td></tr><tr><td><div id="C12219"><img src="/ncitbrowser/images/plus.gif" onClick="replace_content('C12219')"/>&nbsp;Anatomic Structure, System, or Substance</div></td></tr><tr><td><div id="C20633"><img src="/ncitbrowser/images/plus.gif" onClick="replace_content('C20633')"/>&nbsp;Biochemical Pathway</div></td></tr><tr><td><div id="C17828"><img src="/ncitbrowser/images/plus.gif" onClick="replace_content('C17828')"/>&nbsp;Biological Process</div></td></tr><tr><td><div id="C12218"><img src="/ncitbrowser/images/plus.gif" onClick="replace_content('C12218')"/>&nbsp;Chemotherapy Regimen or Agent Combination</div></td></tr><tr><td><div id="C20181"><img src="/ncitbrowser/images/plus.gif" onClick="replace_content('C20181')"/>&nbsp;Conceptual Entity</div></td></tr><tr><td><div id="C20047"><img src="/ncitbrowser/images/plus.gif" onClick="replace_content('C20047')"/>&nbsp;Diagnostic or Prognostic Factor</div></td></tr><tr><td><div id="C7057"><img src="/ncitbrowser/images/plus.gif" onClick="replace_content('C7057')"/>&nbsp;Disease, Disorder or Finding</div></td></tr><tr><td><div id="C1908"><img src="/ncitbrowser/images/plus.gif" onClick="replace_content('C1908')"/>&nbsp;Drug, Food, Chemical or Biomedical Material</div></td></tr><tr><td><div id="C22188"><img src="/ncitbrowser/images/plus.gif" onClick="replace_content('C22188')"/>&nbsp;Experimental Organism Anatomical Concept</div></td></tr><tr><td><div id="C22187"><img src="/ncitbrowser/images/plus.gif" onClick="replace_content('C22187')"/>&nbsp;Experimental Organism Diagnosis</div></td></tr><tr><td><div id="C16612"><img src="/ncitbrowser/images/plus.gif" onClick="replace_content('C16612')"/>&nbsp;Gene</div></td></tr><tr><td><div id="C26548"><img src="/ncitbrowser/images/plus.gif" onClick="replace_content('C26548')"/>&nbsp;Gene Product</div></td></tr><tr><td><div id="C97325"><img src="/ncitbrowser/images/plus.gif" onClick="replace_content('C97325')"/>&nbsp;Manufactured Object</div></td></tr><tr><td><div id="C3910"><img src="/ncitbrowser/images/plus.gif" onClick="replace_content('C3910')"/>&nbsp;Molecular Abnormality</div></td></tr><tr><td><div id="C28389"><img src="/ncitbrowser/images/plus.gif" onClick="replace_content('C28389')"/>&nbsp;NCI Administrative Concept</div></td></tr><tr><td><div id="C14250"><img src="/ncitbrowser/images/plus.gif" onClick="replace_content('C14250')"/>&nbsp;Organism</div></td></tr><tr><td><div id="C28428"><img src="/ncitbrowser/images/plus.gif" onClick="replace_content('C28428')"/>&nbsp;Retired Concept</div></td></tr>




</table>

            <input type="hidden" id="ontology_node_id" name="ontology_node_id" value="<%=HTTPUtils.cleanXSS(ontology_node_id)%>" />
            <input type="hidden" id="ontology_display_name" name="ontology_display_name" value="<%=HTTPUtils.cleanXSS(ontology_display_name)%>" />
            <input type="hidden" id="schema" name="schema" value="<%=HTTPUtils.cleanXSS(schema)%>" />
            <input type="hidden" id="ontology_version" name="ontology_version" value="<%=HTTPUtils.cleanXSS(ontology_version)%>" />

          </form>
          <!-- End of Tree control content -->
        </div>
      </div>
    </div>
  </f:view>
</body>
</html>