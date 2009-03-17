<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %> 
<%@ page contentType="text/html;charset=windows-1252"%>

<%@ page import="java.util.Vector"%>
<%@ page import="org.LexGrid.concepts.Concept" %>

<script type="text/javascript" src="<%= request.getContextPath() %>/js/yui/yahoo-min.js" ></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/yui/event-min.js" ></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/yui/dom-min.js" ></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/yui/animation-min.js" ></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/yui/container-min.js" ></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/yui/connection-min.js" ></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/yui/autocomplete-min.js" ></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/yui/treeview-min.js" ></script>


<html>
<head>
<title>NCI Thesaurus Hierarchy</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />

<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/yui/fonts.css" />
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/yui/grids.css" />
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/yui/code.css" />
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/yui/tree.css" />
		
<script language="JavaScript">

	var tree;
	var nodeIndex;
	var rootDescDiv;
	var emptyRootDiv;
	var treeStatusDiv;
	var nodes = [];


   	function load(url,target){
		if (target != '') 
			target.window.location.href = url; 
		else 
			window.location.href = url; 
	}


	function init() {
		
		rootDescDiv = new YAHOO.widget.Module("rootDesc", {visible:false} );
		resetRootDesc();

		emptyRootDiv = new YAHOO.widget.Module("emptyRoot", {visible:true} );
		resetEmptyRoot();	

		treeStatusDiv = new YAHOO.widget.Module("treeStatus", {visible:true} );
		resetTreeStatus();

		//initRootDesc();
	        initTree();
	}
	
	

	function addTreeNode(rootNode, nodeInfo) {
		var newNodeDetails = "javascript:onClickTreeNode('" + nodeInfo.ontology_node_id + "');";
		var newNodeData = { label:nodeInfo.ontology_node_name, id:nodeInfo.ontology_node_id, href:newNodeDetails };
		var newNode = new YAHOO.widget.TextNode(newNodeData, rootNode, false);
		if (nodeInfo.ontology_node_child_count > 0) {
			newNode.setDynamicLoad(loadNodeData);
		}		
	}

	function buildTree(ontology_node_id, ontology_display_name) {

		var handleBuildTreeSuccess = function(o) {
			var respTxt = o.responseText;
			var respObj = eval('(' + respTxt + ')');
			if ( typeof(respObj) != "undefined") {
				if ( typeof(respObj.root_nodes) != "undefined") {
					var root = tree.getRoot();
					if (respObj.root_nodes.length == 0) {
						showEmptyRoot();
					}
					else {
						for (var i=0; i < respObj.root_nodes.length; i++) {
							var nodeInfo = respObj.root_nodes[i];
							var expand = false;
							addTreeNode(root, nodeInfo, expand);
						}					
					}
					
					tree.draw();
				}
			}
			resetTreeStatus();
		}

		var handleBuildTreeFailure = function(o) {
			resetTreeStatus();
			resetEmptyRoot();
			alert('responseFailure: ' +	o.statusText);
		}
		
		var buildTreeCallback =
		{
			success:handleBuildTreeSuccess,
			failure:handleBuildTreeFailure
		};

		if (ontology_display_name!='') {
			resetEmptyRoot();

			showTreeLoadingStatus();
			var ontology_source = null;//document.pg_form.ontology_source.value;
			var request = YAHOO.util.Connect.asyncRequest('GET','<%= request.getContextPath() %>/ajax?action=build_tree&ontology_node_id=' +ontology_node_id+'&ontology_display_name='+ontology_display_name+'&ontology_source='+ontology_source,buildTreeCallback);

		}
	}

	function resetTree(ontology_node_id, ontology_display_name) {

		var handleResetTreeSuccess = function(o) {
			var respTxt = o.responseText;
//			alert(respTxt);
			var respObj = eval('(' + respTxt + ')');
			if ( typeof(respObj) != "undefined") {
				if ( typeof(respObj.root_node) != "undefined") {
					var root = tree.getRoot();			
					var nodeDetails = "javascript:onClickTreeNode('" + respObj.root_node.ontology_node_id + "');";
					var rootNodeData = { label:respObj.root_node.ontology_node_name, id:respObj.root_node.ontology_node_id, href:nodeDetails };
					var expand = false;
					if (respObj.root_node.ontology_node_child_count > 0) {
						expand = true;
					}
					var ontRoot = new YAHOO.widget.TextNode(rootNodeData, root, expand);
					
					if ( typeof(respObj.child_nodes) != "undefined") {
						for (var i=0; i < respObj.child_nodes.length; i++) {
							var nodeInfo = respObj.child_nodes[i];
							addTreeNode(ontRoot, nodeInfo);
						}
					}					
					tree.draw();
					setRootDesc(respObj.root_node.ontology_node_name, ontology_display_name);					
				}
			}
			resetTreeStatus();
		}

		var handleResetTreeFailure = function(o) {
			resetTreeStatus();
			alert('responseFailure: ' +	o.statusText);
		}
		
		var resetTreeCallback =
		{
			success:handleResetTreeSuccess,
			failure:handleResetTreeFailure
		};
		if (ontology_node_id!= '') {
			showTreeLoadingStatus();
			var ontology_source = null;//document.pg_form.ontology_source.value;
			var request = YAHOO.util.Connect.asyncRequest('GET','<%= request.getContextPath() %>/ajax?action=reset_tree&ontology_node_id=' +ontology_node_id+'&ontology_display_name='+ontology_display_name+'&ontology_source='+ontology_source,resetTreeCallback);
		}
	}

	function onClickTreeNode(ontology_node_id) {
		//document.pg_form.ontology_node_id.value = ontology_node_id;
		//var ontology_display_name = document.pg_form.ontology_display_name.value;
		//var graph_type = document.pg_form.graph_type.options[document.pg_form.graph_type.selectedIndex].value;		
		//setNodeDetails(ontology_node_id, ontology_display_name);
		//buildGraph(ontology_node_id, ontology_display_name, graph_type);

		load('<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=NCI%20Thesaurus&code=' + ontology_node_id,top.opener);

	}

	function onClickViewEntireOntology(ontology_display_name) {
//		document.pg_form.ontology_node_id.value = '';
		var ontology_display_name = "NCI Thesaurus";//document.pg_form.ontology_display_name.value;
		tree = new YAHOO.widget.TreeView("treecontainer");
		tree.draw();
		resetRootDesc();
		buildTree('', ontology_display_name);
	}

//	function initTree() {
	
//		tree = new YAHOO.widget.TreeView("treecontainer");

//		var ontology_node_id = null;
//		var ontology_display_name = "NCI Thesaurus";
		
//		buildTree(ontology_node_id, ontology_display_name);
//	}



	function initTree() {
	
		tree = new YAHOO.widget.TreeView("treecontainer");
		var ontology_node_id = document.forms["pg_form"].ontology_node_id.value;

//alert("ontology_node_id " + ontology_node_id);

		//var ontology_display_name = document.pg_form.ontology_display_name.value;
		
		//var ontology_node_id = null;
		var ontology_display_name = "NCI Thesaurus";
		
		if (ontology_node_id == null || ontology_node_id == "null")
		{
		
//alert("buildTree " );		
			buildTree(ontology_node_id, ontology_display_name);
		}
		else
		{
		
//alert("searchTree " + ontology_node_id);
                        searchTree(ontology_node_id, ontology_display_name);
		}
	}
	
	
	function initRootDesc() {
		rootDescDiv.setBody('');
		initRootDesc.show();
		rootDescDiv.render();
	}
	
	function resetRootDesc() {
		rootDescDiv.hide();
		rootDescDiv.setBody('');
		rootDescDiv.render();
	}

	function resetEmptyRoot() {
		emptyRootDiv.hide();
		emptyRootDiv.setBody('');
		emptyRootDiv.render();
	}	

	function resetTreeStatus() {
		treeStatusDiv.hide();
		treeStatusDiv.setBody('');
		treeStatusDiv.render();
	}

	function showEmptyRoot() {
		emptyRootDiv.setBody("<span class='instruction_text'>No root nodes available.</span>");
		emptyRootDiv.show();
		emptyRootDiv.render();
	}	
	
	function showTreeLoadingStatus() {
		treeStatusDiv.setBody("<img src='<%= request.getContextPath() %>/images/loading.gif'/> <span class='instruction_text'>Building tree ...</span>");
		treeStatusDiv.show();
		treeStatusDiv.render();
	}

	function showSearchingTreeStatus() {
		treeStatusDiv.setBody("<img src='<%= request.getContextPath() %>/images/loading.gif'/> <span class='instruction_text'>Searching tree... Please wait.</span>");
		treeStatusDiv.show();
		treeStatusDiv.render();
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
//					var name = respObj.nodes[i].ontology_node_name + " (" + respObj.nodes[i].ontology_node_parent_assoc + ")";
					var name = respObj.nodes[i].ontology_node_name;
					var nodeDetails = "javascript:onClickTreeNode('" + respObj.nodes[i].ontology_node_id + "');";
					var newNodeData = { label:name, id:respObj.nodes[i].ontology_node_id, href:nodeDetails };
					var newNode = new YAHOO.widget.TextNode(newNodeData, node, false);
					if (respObj.nodes[i].ontology_node_child_count > 0) {
						newNode.setDynamicLoad(loadNodeData);
					}
				}
			}
			tree.draw();
			fnLoadComplete();	
		}
		
		var responseFailure = function(o){
			alert('responseFailure: ' +	o.statusText);
		}
		
		var callback =
		{
			success:responseSuccess,
			failure:responseFailure
		};
		
		var ontology_display_name = "NCI Thesaurus";
		var cObj = YAHOO.util.Connect.asyncRequest('GET','<%= request.getContextPath() %>/ajax?action=expand_tree&ontology_node_id=' +id+'&ontology_display_name='+ontology_display_name,callback);
	}

	function setRootDesc(rootNodeName, ontology_display_name) {
		var newDesc = "<span class='instruction_text'>Root set to <b>" + rootNodeName + "</b></span>";
		rootDescDiv.setBody(newDesc);
		var footer = "<a onClick='javascript:onClickViewEntireOntology();' href='#' class='link_text'>view full ontology}</a>";
		rootDescDiv.setFooter(footer);
		rootDescDiv.show();
		rootDescDiv.render();
	}	
	

	function searchTree(ontology_node_id, ontology_display_name) {

		var handleBuildTreeSuccess = function(o) {
	
			var respTxt = o.responseText;
			var respObj = eval('(' + respTxt + ')');
			if ( typeof(respObj) != "undefined") {
				if ( typeof(respObj.root_nodes) != "undefined") {
					var root = tree.getRoot();
					if (respObj.root_nodes.length == 0) {
						showEmptyRoot();
					}
					else {
				
						for (var i=0; i < respObj.root_nodes.length; i++) {
							var nodeInfo = respObj.root_nodes[i];
							//var expand = false;
							addTreeBranch(root, nodeInfo);
							//tree.draw();
						}					
					}
					//tree.draw();
				}
			}
			resetTreeStatus();
		}

		var handleBuildTreeFailure = function(o) {
			resetTreeStatus();
			resetEmptyRoot();
			alert('responseFailure: ' +	o.statusText);
		}
		
		var buildTreeCallback =
		{
			success:handleBuildTreeSuccess,
			failure:handleBuildTreeFailure
		};

		if (ontology_display_name!='') {
			resetEmptyRoot();

			//showTreeLoadingStatus();
			showSearchingTreeStatus();
			var ontology_source = null;//document.pg_form.ontology_source.value;
			var request = YAHOO.util.Connect.asyncRequest('GET','<%= request.getContextPath() %>/ajax?action=search_tree&ontology_node_id=' +ontology_node_id+'&ontology_display_name='+ontology_display_name+'&ontology_source='+ontology_source,buildTreeCallback);

		}
	}
	
	function addTreeBranch(rootNode, nodeInfo) {
		var newNodeDetails = "javascript:onClickTreeNode('" + nodeInfo.ontology_node_id + "');";
		var newNodeData = { label:nodeInfo.ontology_node_name, id:nodeInfo.ontology_node_id, href:newNodeDetails };
		
		var expand = false;
		var childNodes = nodeInfo.children_nodes;
	
		if (childNodes.length > 0) {
			expand = true;
		}
		var newNode = new YAHOO.widget.TextNode(newNodeData, rootNode, expand);
		if (nodeInfo.ontology_node_child_count > 0) {
		     newNode.setDynamicLoad(loadNodeData);
		}

		//if (ontology_node_id == nodeInfo.ontology_node_id)
		//{
		//     var el = newNode.getLabelEl()
	        //     el.style.backgroundColor = "#c5dbfc";
		//}
		
		tree.draw();
		for (var i=0; i < childNodes.length; i++) {
			var childnodeInfo = childNodes[i];
			addTreeBranch(newNode, childnodeInfo);
		}			
	}	
	YAHOO.util.Event.addListener(window, "load", init);

</script>


</head>
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" >

<f:view>


<form id="pg_form">
<%
String ontology_node_id = (String) request.getParameter("code");
%>
<input type="hidden" id="ontology_node_id" name="ontology_node_id" value="<%=ontology_node_id%>" />

<%
String ontology_display_name = (String) request.getParameter("dictionary");
%>
<input type="hidden" id="ontology_display_name" name="ontology_display_name" value="<%=ontology_display_name%>" />
</form>



<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
	<tr> 
		<td height="1%"> 	
			<%@ include file="/pages/templates/nciHeader.html" %>
		</td> 
        </tr>

	<tr> 
		<td> 	
			   <table summary="" cellpadding="3" cellspacing="0" border="0" class="dataTable" width="100%">

				<tr><td allign="left">
				    <b>NCI Thesaurus Hierarchy</b>
				</td></tr>

				<tr><td allign="left">
				    <hr></hr>
				</td></tr>


				<tr><td class="standardText">
				
<div id="rootDesc">
	<div id="bd">
	</div>
	<div id="ft">
	</div>
</div>
<div id="treeStatus">
	<div id="bd">
	</div>
</div>
<div id="emptyRoot">
	<div id="bd">
	</div>
</div>

<!--
<div id="treecontainer" style="overflow:auto;width:350px;height:700px;"></div>
-->

<div id="treecontainer" style="overflow:auto;width:750px;height:700px;"></div>

				</td></tr>
			
           
			   </table>			
			
		</td> 
        </tr>


</table>


</f:view>

</body>
</html>