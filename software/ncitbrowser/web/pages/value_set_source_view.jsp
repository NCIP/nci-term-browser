<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="java.util.Vector"%>
<%@ page import="org.LexGrid.concepts.Entity" %>
<%@ page import="gov.nih.nci.evs.browser.common.Constants" %>
<%@ page import="gov.nih.nci.evs.browser.utils.*" %>
<%@ page import="gov.nih.nci.evs.browser.bean.IteratorBean" %>
<%@ page import="org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference" %>
<%@ page import="javax.faces.context.FacesContext" %>
<%@ page import="org.apache.log4j.*" %>
<%@ page import="gov.nih.nci.evs.browser.utils.*" %>
<%@ page import="org.lexgrid.valuesets.LexEVSValueSetDefinitionServices" %>

<!-- Note: This tree is used in Value Sets tab. -->

<!--
<script type="text/javascript" src="<%= request.getContextPath() %>/js/yui/yahoo-min.js" ></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/yui/event-min.js" ></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/yui/dom-min.js" ></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/yui/animation-min.js" ></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/yui/container-min.js" ></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/yui/connection-min.js" ></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/yui/autocomplete-min.js" ></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/yui/treeview-min.js" ></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/dropdown.js"></script>
-->


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns:c="http://java.sun.com/jsp/jstl/core">
<head>
  <title>NCI Thesaurus</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  
  
  
<style type="text/css">
/*margin and padding on body element
  can introduce errors in determining
  element position and are not recommended;
  we turn them off as a foundation for YUI
  CSS treatments. */
body {
	margin:0;
	padding:0;
}
</style>

<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.9.0/build/fonts/fonts-min.css" />
<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.9.0/build/treeview/assets/skins/sam/treeview.css" />

<script type="text/javascript" src="http://yui.yahooapis.com/2.9.0/build/yahoo-dom-event/yahoo-dom-event.js"></script>
<script type="text/javascript" src="http://yui.yahooapis.com/2.9.0/build/treeview/treeview-min.js"></script>


<!-- Dependency -->
<script src="http://yui.yahooapis.com/2.9.0/build/yahoo/yahoo-min.js"></script>

<!-- Source file -->
<!--
	If you require only basic HTTP transaction support, use the
	connection_core.js file.
-->
<script src="http://yui.yahooapis.com/2.9.0/build/connection/connection_core-min.js"></script>
 
<!--
	Use the full connection.js if you require the following features:
	- Form serialization.
	- File Upload using the iframe transport.
	- Cross-domain(XDR) transactions.
-->
<script src="http://yui.yahooapis.com/2.9.0/build/connection/connection-min.js"></script>



<!--begin custom header content for this example-->
<!--Additional custom style rules for this example:-->
<style type="text/css">


.ygtvcheck0 { background: url(<%= request.getContextPath() %>/images/yui/treeview/check0.gif) 0 0 no-repeat; width:16px; height:20px; float:left; cursor:pointer; }
.ygtvcheck1 { background: url(<%= request.getContextPath() %>/images/yui/treeview/check1.gif) 0 0 no-repeat; width:16px; height:20px; float:left; cursor:pointer; }
.ygtvcheck2 { background: url(<%= request.getContextPath() %>/images/yui/treeview/check2.gif) 0 0 no-repeat; width:16px; height:20px; float:left; cursor:pointer; }


.ygtv-edit-TaskNode  {	width: 190px;}
.ygtv-edit-TaskNode .ygtvcancel, .ygtv-edit-TextNode .ygtvok  {	border:none;}
.ygtv-edit-TaskNode .ygtv-button-container { float: right;}
.ygtv-edit-TaskNode .ygtv-input  input{	width: 140px;}
.whitebg {
	background-color:white;
}
</style>  
  
  
  
  
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />
  <link rel="shortcut icon" href="<%= request.getContextPath() %>/favicon.ico" type="image/x-icon" />
  
<!--  
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/yui/fonts.css" />
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/yui/grids.css" />
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/yui/code.css" />
  <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/yui/tree.css" />
-->  
  
  
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/script.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/tasknode.js"></script>


  <script type="text/javascript">
  
    function refresh() {
      
      var selectValueSetSearchOptionObj = document.forms["valueSetSearchForm"].selectValueSetSearchOption;
      
      for (var i=0; i<selectValueSetSearchOptionObj.length; i++) {
        if (selectValueSetSearchOptionObj[i].checked) {
            selectValueSetSearchOption = selectValueSetSearchOptionObj[i].value;
        }
      }
      
    
      window.location.href="/ncitbrowser/pages/value_set_source_view.jsf?refresh=1"
          + "&nav_type=valuesets" + "&opt="+ selectValueSetSearchOption;

    }
  </script>
  
  <script language="JavaScript">

    var tree;
    var nodeIndex;
    var nodes = [];

    function load(url,target) {
      if (target != '')
        target.window.location.href = url;
      else
        window.location.href = url;
    }

    function init() {
       //initTree();
    }

	//handler for expanding all nodes
	YAHOO.util.Event.on("expand_all", "click", function(e) {
	     expandEntireTree();
	
	     //	tree.expandAll();
		//YAHOO.util.Event.preventDefault(e);
	});
	
	//handler for collapsing all nodes
	YAHOO.util.Event.on("collapse_all", "click", function(e) {
		tree.collapseAll();
		//YAHOO.util.Event.preventDefault(e);
	});
 
	//handler for checking all nodes
	YAHOO.util.Event.on("check_all", "click", function(e) {
		check_all();
		//YAHOO.util.Event.preventDefault(e);
	});
	
	//handler for unchecking all nodes
	YAHOO.util.Event.on("uncheck_all", "click", function(e) {
		uncheck_all();
		//YAHOO.util.Event.preventDefault(e);
	});
 
 
 
	YAHOO.util.Event.on("getchecked", "click", function(e) {
               //alert("Checked nodes: " + YAHOO.lang.dump(getCheckedNodes()), "info", "example");
		//YAHOO.util.Event.preventDefault(e);
	       	
	});
	
	
	
	
	
    function addTreeNode(rootNode, nodeInfo) {
    
      //alert("clicking on " + nodeInfo.ontology_node_id);
      
      var newNodeDetails = "javascript:onClickTreeNode('" + nodeInfo.ontology_node_id + "');";

      if (nodeInfo.ontology_node_id.indexOf("TVS_") >= 0) {
          newNodeData = { label:nodeInfo.ontology_node_name, id:nodeInfo.ontology_node_id };
      } else {
          newNodeData = { label:nodeInfo.ontology_node_name, id:nodeInfo.ontology_node_id, href:newNodeDetails };
      }  
      
      var newNode = new YAHOO.widget.TaskNode(newNodeData, rootNode, false);
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
              //showEmptyRoot();
            }
            else {
              for (var i=0; i < respObj.root_nodes.length; i++) {
                var nodeInfo = respObj.root_nodes[i];
                var expand = false;
                //addTreeNode(root, nodeInfo, expand);
                
                addTreeNode(root, nodeInfo);
              }
            }

            tree.draw();
          }
        }
      }

      var handleBuildTreeFailure = function(o) {
        alert('responseFailure: ' + o.statusText);
      }

      var buildTreeCallback =
      {
        success:handleBuildTreeSuccess,
        failure:handleBuildTreeFailure
      };

      if (ontology_display_name!='') {
        var ontology_source = null;
        var ontology_version = document.forms["pg_form"].ontology_version.value;
        var request = YAHOO.util.Connect.asyncRequest('GET','<%= request.getContextPath() %>/ajax?action=build_src_vs_tree&ontology_node_id=' +ontology_node_id+'&ontology_display_name='+ontology_display_name+'&version='+ontology_version+'&ontology_source='+ontology_source,buildTreeCallback);
      }
    }

    function resetTree(ontology_node_id, ontology_display_name) {

      var handleResetTreeSuccess = function(o) {
        var respTxt = o.responseText;
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
            var ontRoot = new YAHOO.widget.TaskNode(rootNodeData, root, expand);

            if ( typeof(respObj.child_nodes) != "undefined") {
              for (var i=0; i < respObj.child_nodes.length; i++) {
                var nodeInfo = respObj.child_nodes[i];
                addTreeNode(ontRoot, nodeInfo);
              }
            }
            tree.draw();
          }
        }
      }

      var handleResetTreeFailure = function(o) {
        alert('responseFailure: ' + o.statusText);
      }

      var resetTreeCallback =
      {
        success:handleResetTreeSuccess,
        failure:handleResetTreeFailure
      };
      if (ontology_node_id!= '') {
        var ontology_source = null;
        var ontology_version = document.forms["pg_form"].ontology_version.value;
        var request = YAHOO.util.Connect.asyncRequest('GET','<%= request.getContextPath() %>/ajax?action=reset_vs_tree&ontology_node_id=' +ontology_node_id+'&ontology_display_name='+ontology_display_name + '&version='+ ontology_version +'&ontology_source='+ontology_source,resetTreeCallback);
      }
    }

    function onClickTreeNode(ontology_node_id) {
        //alert("onClickTreeNode " + ontology_node_id);
        window.location = '<%= request.getContextPath() %>/pages/value_set_treenode_redirect.jsf?ontology_node_id=' + ontology_node_id;
    }
    
    
    function onClickViewEntireOntology(ontology_display_name) {
      var ontology_display_name = document.pg_form.ontology_display_name.value;
      tree = new YAHOO.widget.TreeView("treecontainer");
      tree.draw();
      buildTree('', ontology_display_name);
    }

    function initTree() {
    
        tree = new YAHOO.widget.TreeView("treecontainer");
	tree.setNodesProperty('propagateHighlightUp',true);
	tree.setNodesProperty('propagateHighlightDown',true);
	//YEED: Commented the following line.  Instead of opening the folder 
	//  when the user selected the label of the value set root node, the 
	//  following error message is displayed:
    //    * HTTP Status 404 - /ncitbrowser/pages/null
	//YEED: tree.subscribe('clickEvent',tree.onEventToggleHighlight);
	tree.subscribe('keydown',tree._onKeyDownEvent);
		
	
	//http://www.cambiaresearch.com/articles/15/javascript-char-codes-key-codes
	

		    tree.subscribe("expand", function(node) {
			//alert(node.data.myNodeId + " was expanded");
			// return false; // return false to cancel the expand

			//alert("Expanding " + node.label );
			//node.setDynamicLoad(loadNodeData);
			
			//YAHOO.util.UserAction.click(document.body, );
			
			YAHOO.util.UserAction.keydown(document.body, { keyCode: 39 });

		    });
		    


		    tree.subscribe("collapse", function(node) {
			//alert("Collapsing " + node.label );
			
			YAHOO.util.UserAction.keydown(document.body, { keyCode: 109 });
		    });

		    // By default, trees with TextNodes will fire an event for when the label is clicked:
		    tree.subscribe("checkClick", function(node) {
			//alert(node.data.myNodeId + " label was checked");
		    });
 

	
	tree.render();
  
		//YAHOO.util.Event.on('logHilit','click',function() {
		//	var hiLit = tree1.getNodesByProperty('highlightState',1);
		//	if (YAHOO.lang.isNull(hiLit)) { 
		//		YAHOO.log("None selected");
		//	} else {
		//		var labels = [];
		//		for (var i = 0; i < hiLit.length; i++) {
		//			labels.push(hiLit[i].label);
		//		}
		//		YAHOO.log("Highlighted nodes:\n" + labels.join("\n"), "info", "example");
		//	}
		//});      
      
      
      var ontology_node_id = document.forms["pg_form"].ontology_node_id.value;
      var ontology_display_name = document.forms["pg_form"].ontology_display_name.value;

      if (ontology_node_id == null || ontology_node_id == "null")
      {
          buildTree(ontology_node_id, ontology_display_name);
      }
      else
      {
          searchTree(ontology_node_id, ontology_display_name);
      }
    }


    function onCheckClick(node) {
        YAHOO.log(node.label + " check was clicked, new state: " + node.checkState, "info", "example");
    }
 
    function check_all() {
        var topNodes = tree.getRoot().children;
        for(var i=0; i<topNodes.length; ++i) {
            topNodes[i].check();
        }
    }
 
    function uncheck_all() {
        var topNodes = tree.getRoot().children;
        for(var i=0; i<topNodes.length; ++i) {
            topNodes[i].uncheck();
        }
    }



    function expand_all() {
        //alert("expand_all");
        var ontology_display_name = document.forms["pg_form"].ontology_display_name.value;
        onClickViewEntireOntology(ontology_display_name);
    }
    
 
   // Gets the labels of all of the fully checked nodes
   // Could be updated to only return checked leaf nodes by evaluating
   // the children collection first.
    function getCheckedNodes(nodes) {
        nodes = nodes || tree.getRoot().children;
        checkedNodes = [];
        for(var i=0, l=nodes.length; i<l; i=i+1) {
            var n = nodes[i];
            if (n.checkState > 0) { // if we were interested in the nodes that have some but not all children checked
            //if (n.checkState === 2) {
                checkedNodes.push(n.label); // just using label for simplicity

		    if (n.hasChildren()) {
			checkedNodes = checkedNodes.concat(getCheckedNodes(n.children));
		    }                
                
            }
        }
  		
       var checked_vocabularies = document.forms["valueSetSearchForm"].checked_vocabularies;
       checked_vocabularies.value = checkedNodes;

       return checkedNodes;
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
            var newNode = new YAHOO.widget.TaskNode(newNodeData, node, false);
            if (respObj.nodes[i].ontology_node_child_count > 0) {
              newNode.setDynamicLoad(loadNodeData);
            }
          }
        }
        tree.draw();
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
      var cObj = YAHOO.util.Connect.asyncRequest('GET','<%= request.getContextPath() %>/ajax?action=expand_src_vs_tree&ontology_node_id=' +id+'&ontology_display_name='+ontology_display_name+'&version='+ontology_version,callback);
    }


    function searchTree(ontology_node_id, ontology_display_name) {

        var handleBuildTreeSuccess = function(o) {

        var respTxt = o.responseText;
        var respObj = eval('(' + respTxt + ')');
        if ( typeof(respObj) != "undefined") {
        
          if ( typeof(respObj.dummy_root_nodes) != "undefined") {
              showNodeNotFound(ontology_node_id);
          }
            
          else if ( typeof(respObj.root_nodes) != "undefined") {
            var root = tree.getRoot();
            if (respObj.root_nodes.length == 0) {
              //showEmptyRoot();
            }
            else {
              showPartialHierarchy();
              showConstructingTreeStatus();

              for (var i=0; i < respObj.root_nodes.length; i++) {
                var nodeInfo = respObj.root_nodes[i];
                //var expand = false;
                addTreeBranch(ontology_node_id, root, nodeInfo);
              }
            }
          }
        }
      }

      var handleBuildTreeFailure = function(o) {
        alert('responseFailure: ' + o.statusText);
      }

      var buildTreeCallback =
      {
        success:handleBuildTreeSuccess,
        failure:handleBuildTreeFailure
      };

      if (ontology_display_name!='') {
        var ontology_source = null;//document.pg_form.ontology_source.value;
        var ontology_version = document.forms["pg_form"].ontology_version.value;
        var request = YAHOO.util.Connect.asyncRequest('GET','<%= request.getContextPath() %>/ajax?action=search_vs_tree&ontology_node_id=' +ontology_node_id+'&ontology_display_name='+ontology_display_name+'&version='+ontology_version+'&ontology_source='+ontology_source,buildTreeCallback);

      }
    }



    function expandEntireTree() {
        tree = new YAHOO.widget.TreeView("treecontainer");
        //tree.draw();
        
        var ontology_display_name = document.forms["pg_form"].ontology_display_name.value;
        var ontology_node_id = document.forms["pg_form"].ontology_node_id.value;
        
        var handleBuildTreeSuccess = function(o) {

        var respTxt = o.responseText;
        var respObj = eval('(' + respTxt + ')');
        if ( typeof(respObj) != "undefined") {
        
             if ( typeof(respObj.root_nodes) != "undefined") {
             
                    //alert(respObj.root_nodes.length);
            
                    var root = tree.getRoot();  
		    if (respObj.root_nodes.length == 0) {
		      //showEmptyRoot();
		    } else {


	              

		      for (var i=0; i < respObj.root_nodes.length; i++) {
			 var nodeInfo = respObj.root_nodes[i];
	                 //alert("calling addTreeBranch ");

			 addTreeBranch(ontology_node_id, root, nodeInfo);
		      }
		    }
              }
        }
      }

      var handleBuildTreeFailure = function(o) {
        alert('responseFailure: ' + o.statusText);
      }

      var buildTreeCallback =
      {
        success:handleBuildTreeSuccess,
        failure:handleBuildTreeFailure
      };

      if (ontology_display_name!='') {
        var ontology_source = null;
        var ontology_version = document.forms["pg_form"].ontology_version.value;
        var request = YAHOO.util.Connect.asyncRequest('GET','<%= request.getContextPath() %>/ajax?action=expand_entire_vs_tree&ontology_node_id=' +ontology_node_id+'&ontology_display_name='+ontology_display_name+'&version='+ontology_version+'&ontology_source='+ontology_source,buildTreeCallback);

      }
    }
    
    
    

    function addTreeBranch(ontology_node_id, rootNode, nodeInfo) {
      var newNodeDetails = "javascript:onClickTreeNode('" + nodeInfo.ontology_node_id + "');";
      
      var newNodeData;
      if (ontology_node_id.indexOf("TVS_") >= 0) {
          newNodeData = { label:nodeInfo.ontology_node_name, id:nodeInfo.ontology_node_id };
      } else {
          newNodeData = { label:nodeInfo.ontology_node_name, id:nodeInfo.ontology_node_id, href:newNodeDetails };
      }        
      
      var expand = false;
      var childNodes = nodeInfo.children_nodes;

      if (childNodes.length > 0) {
          expand = true;
      }
      var newNode = new YAHOO.widget.TaskNode(newNodeData, rootNode, expand);
      if (nodeInfo.ontology_node_id == ontology_node_id) {
          newNode.labelStyle = "ygtvlabel_highlight";
      }

      if (nodeInfo.ontology_node_id == ontology_node_id) {
         newNode.isLeaf = true;
         if (nodeInfo.ontology_node_child_count > 0) {
             newNode.isLeaf = false;
             newNode.setDynamicLoad(loadNodeData);
         } else {
             tree.draw();
         }

      } else {
          if (nodeInfo.ontology_node_id != ontology_node_id) {
          if (nodeInfo.ontology_node_child_count == 0 && nodeInfo.ontology_node_id != ontology_node_id) {
        newNode.isLeaf = true;
          } else if (childNodes.length == 0) {
        newNode.setDynamicLoad(loadNodeData);
          }
        }
      }

      tree.draw();
      for (var i=0; i < childNodes.length; i++) {
         var childnodeInfo = childNodes[i];
         addTreeBranch(ontology_node_id, newNode, childnodeInfo);
      }
    }
    YAHOO.util.Event.addListener(window, "load", init);
    
    YAHOO.util.Event.onDOMReady(initTree);
  

  </script>
  
</head>





<body onLoad="document.forms.valueSetSearchForm.matchText.focus();">
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/wz_tooltip.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/tip_centerwindow.js"></script>
  <script type="text/javascript" src="<%= request.getContextPath() %>/js/tip_followscroll.js"></script>
<%!
  private static Logger _logger = Utils.getJspLogger("value_set_source_view.jsp");
%>

<%
    String message = (String) request.getSession().getAttribute("message");
    request.getSession().removeAttribute("message");

    String searchform_requestContextPath = request.getContextPath();
    searchform_requestContextPath = searchform_requestContextPath.replace("//ncitbrowser//ncitbrowser", "//ncitbrowser");

    String selected_cs = "";
    String selected_cd = null;

    String check_cs = "";
    String check_cd = "";
    String check_code = "";
    String check_name = "";
    String check_src = "";
    
    String valueset_search_algorithm = null;
    valueset_search_algorithm = (String) request.getSession().getAttribute("valueset_search_algorithm");
    if (valueset_search_algorithm == null) valueset_search_algorithm = "";
    

    String check__e = "", check__b = "", check__s = "" , check__c ="";
    if (valueset_search_algorithm.compareTo("") == 0 || valueset_search_algorithm.compareTo("exactMatch") == 0)
        check__e = "checked";
    else if (valueset_search_algorithm.compareTo("startsWith") == 0)
        check__s= "checked";
    else if (valueset_search_algorithm.compareTo("contains") == 0)
        check__c = "checked";
        

    String selectValueSetSearchOption = null;

    selectValueSetSearchOption = HTTPUtils.cleanXSS((String) request.getParameter("opt"));
    
    if (selectValueSetSearchOption == null) {
        selectValueSetSearchOption = (String) request.getSession().getAttribute("selectValueSetSearchOption");
    }
               
    if (selectValueSetSearchOption == null || selectValueSetSearchOption.compareTo("null") == 0) {
        selectValueSetSearchOption = "Code";
    }

    if (selectValueSetSearchOption.compareTo("CodingScheme") == 0)
        check_cs = "checked";
    else if (selectValueSetSearchOption.compareTo("Code") == 0)
        check_code = "checked";
    else if (selectValueSetSearchOption.compareTo("Name") == 0)
        check_name = "checked";        
    else if (selectValueSetSearchOption.compareTo("Source") == 0)
        check_src = "checked";
    
    String valueset_match_text = (String) request.getSession().getAttribute("matchText_VSD");
    if (valueset_match_text == null) valueset_match_text = "";
    if (valueset_match_text != null && valueset_match_text.compareTo("null") == 0) valueset_match_text = "";
%>

<f:view>
  <!-- Begin Skip Top Navigation -->
  <a href="#evs-content" class="hideLink" accesskey="1" title="Skip repetitive navigation links">skip navigation links</A>
  <!-- End Skip Top Navigation --> 
  <%@ include file="/pages/templates/header.jsp" %>
  <div class="center-page">
    <%@ include file="/pages/templates/sub-header.jsp" %>
    <!-- Main box -->
    <div id="main-area">
    
      <!-- Thesaurus, banner search area -->
      <div class="bannerarea">
	    <a href="<%=basePath%>/start.jsf" style="text-decoration: none;">
	      <div class="vocabularynamebanner_tb">
	        <span class="vocabularynamelong_tb"><%=JSPUtils.getApplicationVersionDisplay()%></span>
	      </div>
	    </a>
        <div class="search-globalnav">
          <!-- Search box -->
          <div class="searchbox-top"><img src="<%=basePath%>/images/searchbox-top.gif" width="352" height="2" alt="SearchBox Top" /></div>
          <div class="searchbox">

<h:form id="valueSetSearchForm" styleClass="search-form-main-area">  


            <input type="hidden" id="checked_vocabularies" name="checked_vocabularies" value="" />


              <%-- <div class="textbody"> --%>
<table border="0" cellspacing="0" cellpadding="0" style="margin: 2px" >
  <tr valign="top" align="left">
    <td align="left" class="textbody">  
                <% if (selectValueSetSearchOption.compareTo("CodingScheme") == 0) { %>
                  <input CLASS="searchbox-input-2"
                    name="matchText"
                    value=""
                    disabled="disabled" 
                    onkeypress="return submitEnter('valueSetSearchForm:valueset_search',event)"
                    tabindex="1"/>
                <% } else { %>
                  <input CLASS="searchbox-input-2"
                    name="matchText"
                    value="<%=valueset_match_text%>"
                    onFocus="active = true"
                    onBlur="active = false"
                    onkeypress="return submitEnter('valueSetSearchForm:valueset_search',event)"
                    tabindex="1"/>
                <% } %>  
                
                <h:commandButton id="valueset_search" value="Search" action="#{valueSetBean.valueSetSearchAction}"
                  onclick="javascript:getCheckedNodes();"
                  image="#{valueSetSearch_requestContextPath}/images/search.gif"
                    styleClass="searchbox-btn"
                  alt="Search"
                  tabindex="2">
                </h:commandButton>
                
                <h:outputLink
                  value="#{facesContext.externalContext.requestContextPath}/pages/help.jsf#searchhelp"
                  tabindex="3">
                  <h:graphicImage value="/images/search-help.gif" styleClass="searchbox-btn" alt="Search Help"
                    style="border-width:0;"/>
                </h:outputLink> 
    </td>
  </tr>
  
  <tr valign="top" align="left">
    <td>
      <table border="0" cellspacing="0" cellpadding="0" style="margin: 0px">
    
        <tr valign="top" align="left">
        <td align="left" class="textbody">  
                     <input type="radio" name="valueset_search_algorithm" value="exactMatch" alt="Exact Match" <%=check__e%> tabindex="3">Exact Match&nbsp;
                     <input type="radio" name="valueset_search_algorithm" value="startsWith" alt="Begins With" <%=check__s%> tabindex="3">Begins With&nbsp;
                     <input type="radio" name="valueset_search_algorithm" value="contains" alt="Contains" <%=check__c%> tabindex="3">Contains
        </td>
        </tr>
        <%
                     request.setAttribute("globalNavHeight", "37"); 
        %>
        <tr align="left">
            <td height="1px" bgcolor="#2F2F5F" align="left"></td>
        </tr>
        <tr valign="top" align="left">
          <td align="left" class="textbody">     
                <input type="radio" id="selectValueSetSearchOption" name="selectValueSetSearchOption" value="Code" <%=check_code%> 
                  alt="Code" tabindex="1" >Code&nbsp;
                <input type="radio" id="selectValueSetSearchOption" name="selectValueSetSearchOption" value="Name" <%=check_name%> 
                  alt="Name" tabindex="1" >Name
          </td>
        </tr>  
      </table>
    </td>
  </tr>
</table>                 
                <input type="hidden" name="referer" id="referer" value="<%=HTTPUtils.getRefererParmEncode(request)%>">
                <input type="hidden" id="nav_type" name="nav_type" value="valuesets" />
                <input type="hidden" id="view" name="view" value="source" />
              <%-- </div> <!-- textbody --> --%>
</h:form>               
          </div> <!-- searchbox -->
          
          <div class="searchbox-bottom"><img src="<%=basePath%>/images/searchbox-bottom.gif" width="352" height="2" alt="SearchBox Bottom" /></div>
          <!-- end Search box -->
          <!-- Global Navigation -->
          <%@ include file="/pages/templates/menuBar-termbrowserhome.jsp" %>
          <!-- end Global Navigation -->
              
        </div> <!-- search-globalnav -->
      </div> <!-- bannerarea -->
      
      <!-- end Thesaurus, banner search area -->
      <!-- Quick links bar -->
      <%@ include file="/pages/templates/quickLink.jsp" %>
      <!-- end Quick links bar -->
      
      <!-- Page content -->
      <div class="pagecontent">
          
<p class="textbody">
View value sets organized by standards category or source terminology. 
Standards categories group the value sets supporting them; all other labels lead to the home pages of actual value sets or source terminologies. 
Search or browse a value set from its home page, or search all value sets at once from this page (very slow) to find which ones contain a particular code or term.
</p> 

        <div id="popupContentArea">
          <a name="evs-content" id="evs-content"></a>
          
          <table width="580px" cellpadding="3" cellspacing="0" border="0">

          
            <% if (message != null) { request.getSession().removeAttribute("message"); %>
              <tr class="textbodyred"><td>
                <p class="textbodyred">&nbsp;<%=message%></p>
              </td>
              <td>&nbsp;</td>
             </tr>
            <% } %>
                       
            <tr class="textbody">
              <td class="textbody" align="left">
                <% String view = "source"; %>
                Standards View
                &nbsp;| 
                <a href="<%=request.getContextPath() %>/pages/value_set_terminology_view.jsf?view=terminology">Terminology View</a>
              </td>
            
              <td align="right">
               <font size="1" color="red" align="right">
                 <a href="javascript:printPage()"><img src="<%= request.getContextPath() %>/images/printer.bmp" border="0" alt="Send to Printer"><i>Send to Printer</i></a>
               </font>
              </td>
            </tr>
          </table>
        
          <hr/>



<style> 
#expandcontractdiv {border:1px solid #336600; background-color:#FFFFCC; margin:0 0 .5em 0; padding:0.2em;}
#treecontainer { background: #fff }
</style>

          
<div id="expandcontractdiv">
	<a id="expand_all" href="#">Expand all</a>
	<a id="collapse_all" href="#">Collapse all</a>
	<a id="check_all" href="#">Check all</a>
	<a id="uncheck_all" href="#">Uncheck all</a>
</div>



          <!-- Tree content -->
          
          <div id="treecontainer" class="ygtv-checkbox"></div>
          
          <form id="pg_form">
            <%
              String ontology_node_id = HTTPUtils.cleanXSS((String) request.getParameter("code"));
              String schema = HTTPUtils.cleanXSS((String) request.getParameter("schema"));
              String ontology_version = HTTPUtils.cleanXSS((String) request.getParameter("version"));
              String ontology_display_name = HTTPUtils.cleanXSS((String) request.getParameter("schema"));
              if (ontology_display_name == null) {
                  ontology_display_name = HTTPUtils.cleanXSS((String) request.getParameter("dictionary"));
              }
          
            %>
            <input type="hidden" id="ontology_node_id" name="ontology_node_id" value="<%=HTTPUtils.cleanXSS(ontology_node_id)%>" />
            <input type="hidden" id="ontology_display_name" name="ontology_display_name" value="<%=HTTPUtils.cleanXSS(ontology_display_name)%>" />
            <input type="hidden" id="schema" name="schema" value="<%=HTTPUtils.cleanXSS(schema)%>" />
            <input type="hidden" id="ontology_version" name="ontology_version" value="<%=HTTPUtils.cleanXSS(ontology_version)%>" />
            <input type="hidden" id="view" name="view" value="source" />
          </form>
          
          
        </div> <!-- popupContentArea -->
        

<div class="textbody">
<%@ include file="/pages/templates/nciFooter.jsp"%>
</div>       

        
      </div> <!-- pagecontent -->
    </div> <!--  main-area -->
    <div class="mainbox-bottom"><img src="<%=basePath%>/images/mainbox-bottom.gif" width="745" height="5" alt="Mainbox Bottom" /></div>

  </div> <!-- center-page -->
</f:view>
</body>
</html>
