<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns:c="http://java.sun.com/jsp/jstl/core">
<head>
  <title>NCI Term Browser - Value Set Source View</title>
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
<script type="text/javascript" src="/ncitbrowser/js/yui/treeview-min.js" ></script>


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


.ygtvcheck0 { background: url(/ncitbrowser/images/yui/treeview/check0.gif) 0 0 no-repeat; width:16px; height:20px; float:left; cursor:pointer; }
.ygtvcheck1 { background: url(/ncitbrowser/images/yui/treeview/check1.gif) 0 0 no-repeat; width:16px; height:20px; float:left; cursor:pointer; }
.ygtvcheck2 { background: url(/ncitbrowser/images/yui/treeview/check2.gif) 0 0 no-repeat; width:16px; height:20px; float:left; cursor:pointer; }


.ygtv-edit-TaskNode  {  width: 190px;}
.ygtv-edit-TaskNode .ygtvcancel, .ygtv-edit-TextNode .ygtvok  { border:none;}
.ygtv-edit-TaskNode .ygtv-button-container { float: right;}
.ygtv-edit-TaskNode .ygtv-input  input{ width: 140px;}
.whitebg {
    background-color:white;
}
</style>

  <link rel="stylesheet" type="text/css" href="/ncitbrowser/css/styleSheet.css" />
  <link rel="shortcut icon" href="/ncitbrowser/favicon.ico" type="image/x-icon" />

  <script type="text/javascript" src="/ncitbrowser/js/script.js"></script>
  <script type="text/javascript" src="/ncitbrowser/js/tasknode.js"></script>

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
         //expandEntireTree();

         tree.expandAll();
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
        var request = YAHOO.util.Connect.asyncRequest('GET','/ncitbrowser/ajax?action=build_src_vs_tree&ontology_node_id=' +ontology_node_id+'&ontology_display_name='+ontology_display_name+'&version='+ontology_version+'&ontology_source='+ontology_source,buildTreeCallback);
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
        var request = YAHOO.util.Connect.asyncRequest('GET','/ncitbrowser/ajax?action=reset_vs_tree&ontology_node_id=' +ontology_node_id+'&ontology_display_name='+ontology_display_name + '&version='+ ontology_version +'&ontology_source='+ontology_source,resetTreeCallback);
      }
    }

    function onClickTreeNode(ontology_node_id) {
        //alert("onClickTreeNode " + ontology_node_id);
        window.location = '/ncitbrowser/pages/value_set_treenode_redirect.jsf?ontology_node_id=' + ontology_node_id;
    }


    function onClickViewEntireOntology(ontology_display_name) {
      var ontology_display_name = document.pg_form.ontology_display_name.value;
      tree = new YAHOO.widget.TreeView("treecontainer");
      tree.draw();
    }

    function initTree() {

        tree = new YAHOO.widget.TreeView("treecontainer");
    tree.setNodesProperty('propagateHighlightUp',true);
    tree.setNodesProperty('propagateHighlightDown',true);
    tree.subscribe('keydown',tree._onKeyDownEvent);




            tree.subscribe("expand", function(node) {

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


            var root = tree.getRoot();

newNodeData = { label:"Clinical Data Interchange Standards Consortium Terminology", id:"TVS_CDISC"};
var N_1487619242_n573977265 = new YAHOO.widget.TaskNode(newNodeData, root, true);
N_1487619242_n573977265.isLeaf = false;
N_1487619242_n573977265.ontology_node_child_count = 1;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C81222');";
newNodeData = { label:"CDISC ADaM Terminology", id:"http://ncit:C81222", href:newNodeDetails };
var N_346975166_n1142351184 = new YAHOO.widget.TaskNode(newNodeData, N_1487619242_n573977265, true);
N_346975166_n1142351184.isLeaf = false;
N_346975166_n1142351184.ontology_node_child_count = 1;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C81223');";
newNodeData = { label:"CDISC ADaM Date Imputation Flag Terminology", id:"http://ncit:C81223", href:newNodeDetails };
var N_346975167_2133656227 = new YAHOO.widget.TaskNode(newNodeData, N_346975166_n1142351184, false);
N_346975167_2133656227.ontology_node_child_count = 0;
N_346975167_2133656227.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C81224');";
newNodeData = { label:"CDISC ADaM Derivation Type Terminology", id:"http://ncit:C81224", href:newNodeDetails };
var N_346975168_n1588776588 = new YAHOO.widget.TaskNode(newNodeData, N_346975166_n1142351184, false);
N_346975168_n1588776588.ontology_node_child_count = 0;
N_346975168_n1588776588.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C81225');";
newNodeData = { label:"CDISC ADaM Parameter Type Terminology", id:"http://ncit:C81225", href:newNodeDetails };
var N_346975169_n1262985552 = new YAHOO.widget.TaskNode(newNodeData, N_346975166_n1142351184, false);
N_346975169_n1262985552.ontology_node_child_count = 0;
N_346975169_n1262985552.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C81226');";
newNodeData = { label:"CDISC ADaM Time Imputation Flag Terminology", id:"http://ncit:C81226", href:newNodeDetails };
var N_346975170_1338853688 = new YAHOO.widget.TaskNode(newNodeData, N_346975166_n1142351184, false);
N_346975170_1338853688.ontology_node_child_count = 0;
N_346975170_1338853688.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C77527');";
newNodeData = { label:"CDISC CDASH Terminology", id:"http://ncit:C77527", href:newNodeDetails };
var N_346233279_n2074676292 = new YAHOO.widget.TaskNode(newNodeData, N_1487619242_n573977265, true);
N_346233279_n2074676292.isLeaf = false;
N_346233279_n2074676292.ontology_node_child_count = 1;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C78418');";
newNodeData = { label:"CDISC CDASH Concomitant Medication Dose Form Terminology", id:"http://ncit:C78418", href:newNodeDetails };
var N_346262079_n468939714 = new YAHOO.widget.TaskNode(newNodeData, N_346233279_n2074676292, false);
N_346262079_n468939714.ontology_node_child_count = 0;
N_346262079_n468939714.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C78417');";
newNodeData = { label:"CDISC CDASH Concomitant Medication Dose Units Terminology", id:"http://ncit:C78417", href:newNodeDetails };
var N_346262078_1696739268 = new YAHOO.widget.TaskNode(newNodeData, N_346233279_n2074676292, false);
N_346262078_1696739268.ontology_node_child_count = 0;
N_346262078_1696739268.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C78419');";
newNodeData = { label:"CDISC CDASH Concomitant Medication Dosing Frequency per Interval Terminology", id:"http://ncit:C78419", href:newNodeDetails };
var N_346262080_n211132192 = new YAHOO.widget.TaskNode(newNodeData, N_346233279_n2074676292, false);
N_346262080_n211132192.ontology_node_child_count = 0;
N_346262080_n211132192.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C78420');";
newNodeData = { label:"CDISC CDASH Concomitant Medication Route of Administration Terminology", id:"http://ncit:C78420", href:newNodeDetails };
var N_346262102_n1333807278 = new YAHOO.widget.TaskNode(newNodeData, N_346233279_n2074676292, false);
N_346262102_n1333807278.ontology_node_child_count = 0;
N_346262102_n1333807278.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C78422');";
newNodeData = { label:"CDISC CDASH ECG Original Units Terminology", id:"http://ncit:C78422", href:newNodeDetails };
var N_346262104_843131583 = new YAHOO.widget.TaskNode(newNodeData, N_346233279_n2074676292, false);
N_346262104_843131583.ontology_node_child_count = 0;
N_346262104_843131583.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C78426');";
newNodeData = { label:"CDISC CDASH Exposure Dose Form Terminology", id:"http://ncit:C78426", href:newNodeDetails };
var N_346262108_n304569395 = new YAHOO.widget.TaskNode(newNodeData, N_346233279_n2074676292, false);
N_346262108_n304569395.ontology_node_child_count = 0;
N_346262108_n304569395.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C78424');";
newNodeData = { label:"CDISC CDASH Exposure Dosing Frequency per Interval Terminology", id:"http://ncit:C78424", href:newNodeDetails };
var N_346262106_581665481 = new YAHOO.widget.TaskNode(newNodeData, N_346233279_n2074676292, false);
N_346262106_581665481.ontology_node_child_count = 0;
N_346262106_581665481.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C78425');";
newNodeData = { label:"CDISC CDASH Exposure Route of Administration Terminology", id:"http://ncit:C78425", href:newNodeDetails };
var N_346262107_1425874631 = new YAHOO.widget.TaskNode(newNodeData, N_346233279_n2074676292, false);
N_346262107_1425874631.ontology_node_child_count = 0;
N_346262107_1425874631.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C83004');";
newNodeData = { label:"CDISC CDASH Substance Usage Never Current Former Terminology", id:"http://ncit:C83004", href:newNodeDetails };
var N_347032766_91510002 = new YAHOO.widget.TaskNode(newNodeData, N_346233279_n2074676292, false);
N_347032766_91510002.ontology_node_child_count = 0;
N_347032766_91510002.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C78428');";
newNodeData = { label:"CDISC CDASH Total Volume Administration Unit Terminology", id:"http://ncit:C78428", href:newNodeDetails };
var N_346262110_1002177286 = new YAHOO.widget.TaskNode(newNodeData, N_346233279_n2074676292, false);
N_346262110_1002177286.ontology_node_child_count = 0;
N_346262110_1002177286.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C78423');";
newNodeData = { label:"CDISC CDASH Units for Exposure Terminology", id:"http://ncit:C78423", href:newNodeDetails };
var N_346262105_n721915690 = new YAHOO.widget.TaskNode(newNodeData, N_346233279_n2074676292, false);
N_346262105_n721915690.ontology_node_child_count = 0;
N_346262105_n721915690.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C78430');";
newNodeData = { label:"CDISC CDASH Units for Planned Exposure Terminology", id:"http://ncit:C78430", href:newNodeDetails };
var N_346262133_n1310028440 = new YAHOO.widget.TaskNode(newNodeData, N_346233279_n2074676292, false);
N_346262133_n1310028440.ontology_node_child_count = 0;
N_346262133_n1310028440.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C78427');";
newNodeData = { label:"CDISC CDASH Unit for Duration of Treatment Interruption Terminology", id:"http://ncit:C78427", href:newNodeDetails };
var N_346262109_183304862 = new YAHOO.widget.TaskNode(newNodeData, N_346233279_n2074676292, false);
N_346262109_183304862.ontology_node_child_count = 0;
N_346262109_183304862.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C78421');";
newNodeData = { label:"CDISC CDASH Unit of Drug Dispensed or Returned Terminology", id:"http://ncit:C78421", href:newNodeDetails };
var N_346262103_n1696560429 = new YAHOO.widget.TaskNode(newNodeData, N_346233279_n2074676292, false);
N_346262103_n1696560429.ontology_node_child_count = 0;
N_346262103_n1696560429.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C78429');";
newNodeData = { label:"CDISC CDASH Unit of Measure for Flow Rate Terminology", id:"http://ncit:C78429", href:newNodeDetails };
var N_346262111_n25416745 = new YAHOO.widget.TaskNode(newNodeData, N_346233279_n2074676292, false);
N_346262111_n25416745.ontology_node_child_count = 0;
N_346262111_n25416745.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C78431');";
newNodeData = { label:"CDISC CDASH Vital Signs Position of Subject Terminology", id:"http://ncit:C78431", href:newNodeDetails };
var N_346262134_1954457025 = new YAHOO.widget.TaskNode(newNodeData, N_346233279_n2074676292, false);
N_346262134_1954457025.ontology_node_child_count = 0;
N_346262134_1954457025.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C67497');";
newNodeData = { label:"CDISC Glossary Terminology", id:"http://ncit:C67497", href:newNodeDetails };
var N_345309014_n1990975453 = new YAHOO.widget.TaskNode(newNodeData, N_1487619242_n573977265, false);
N_345309014_n1990975453.ontology_node_child_count = 0;
N_345309014_n1990975453.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C82590');";
newNodeData = { label:"CDISC Observation Class Terminology", id:"http://ncit:C82590", href:newNodeDetails };
var N_347008055_n268972743 = new YAHOO.widget.TaskNode(newNodeData, N_1487619242_n573977265, false);
N_347008055_n268972743.ontology_node_child_count = 0;
N_347008055_n268972743.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C66830');";
newNodeData = { label:"CDISC SDTM Terminology", id:"http://ncit:C66830", href:newNodeDetails };
var N_345282874_1152700394 = new YAHOO.widget.TaskNode(newNodeData, N_1487619242_n573977265, true);
N_345282874_1152700394.isLeaf = false;
N_345282874_1152700394.ontology_node_child_count = 1;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C66767');";
newNodeData = { label:"CDISC SDTM Action Taken with Study Treatment Terminology", id:"http://ncit:C66767", href:newNodeDetails };
var N_345282013_n1132595156 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_345282013_n1132595156.ontology_node_child_count = 0;
N_345282013_n1132595156.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C66768');";
newNodeData = { label:"CDISC SDTM Adverse Event Outcome Terminology", id:"http://ncit:C66768", href:newNodeDetails };
var N_345282014_64084854 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_345282014_64084854.ontology_node_child_count = 0;
N_345282014_64084854.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C66780');";
newNodeData = { label:"CDISC SDTM Age Group Terminology", id:"http://ncit:C66780", href:newNodeDetails };
var N_345282068_n156494961 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_345282068_n156494961.ontology_node_child_count = 0;
N_345282068_n156494961.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C74456');";
newNodeData = { label:"CDISC SDTM Anatomical Location Terminology", id:"http://ncit:C74456", href:newNodeDetails };
var N_346143037_n1141222627 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_346143037_n1141222627.ontology_node_child_count = 0;
N_346143037_n1141222627.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C71148');";
newNodeData = { label:"CDISC SDTM Body Position Terminology", id:"http://ncit:C71148", href:newNodeDetails };
var N_346050752_n720271701 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_346050752_n720271701.ontology_node_child_count = 0;
N_346050752_n720271701.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C74558');";
newNodeData = { label:"CDISC SDTM Category For Disposition Event Terminology", id:"http://ncit:C74558", href:newNodeDetails };
var N_346144000_121912681 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_346144000_121912681.ontology_node_child_count = 0;
N_346144000_121912681.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C66797');";
newNodeData = { label:"CDISC SDTM Category for Inclusion And Or Exclusion Terminology", id:"http://ncit:C66797", href:newNodeDetails };
var N_345282106_n230508427 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_345282106_n230508427.ontology_node_child_count = 0;
N_345282106_n230508427.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C66784');";
newNodeData = { label:"CDISC SDTM Common Terminology Criteria for Adverse Event Grade Terminology", id:"http://ncit:C66784", href:newNodeDetails };
var N_345282072_2120223320 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_345282072_2120223320.ontology_node_child_count = 0;
N_345282072_2120223320.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C87162');";
newNodeData = { label:"CDISC SDTM Common Terminology Criteria for Adverse Event Grade Terminology Version 4.0", id:"http://ncit:C87162", href:newNodeDetails };
var N_347153075_1247883767 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_347153075_1247883767.ontology_node_child_count = 0;
N_347153075_1247883767.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C66785');";
newNodeData = { label:"CDISC SDTM Control Type Terminology", id:"http://ncit:C66785", href:newNodeDetails };
var N_345282073_24089391 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_345282073_24089391.ontology_node_child_count = 0;
N_345282073_24089391.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C66787');";
newNodeData = { label:"CDISC SDTM Diagnosis Group Terminology", id:"http://ncit:C66787", href:newNodeDetails };
var N_345282075_n1984582395 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_345282075_n1984582395.ontology_node_child_count = 0;
N_345282075_n1984582395.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C66788');";
newNodeData = { label:"CDISC SDTM Dictionary Name Terminology", id:"http://ncit:C66788", href:newNodeDetails };
var N_345282076_875501689 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_345282076_875501689.ontology_node_child_count = 0;
N_345282076_875501689.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C78732');";
newNodeData = { label:"CDISC SDTM Drug Accountability Test Terminology by Code", id:"http://ncit:C78732", href:newNodeDetails };
var N_346265018_n1296079912 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_346265018_n1296079912.ontology_node_child_count = 0;
N_346265018_n1296079912.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C78731');";
newNodeData = { label:"CDISC SDTM Drug Accountability Test Terminology by Name", id:"http://ncit:C78731", href:newNodeDetails };
var N_346265017_17656863 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_346265017_17656863.ontology_node_child_count = 0;
N_346265017_17656863.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C71150');";
newNodeData = { label:"CDISC SDTM ECG Finding Terminology", id:"http://ncit:C71150", href:newNodeDetails };
var N_346050775_n289170104 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_346050775_n289170104.ontology_node_child_count = 0;
N_346050775_n289170104.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C71151');";
newNodeData = { label:"CDISC SDTM ECG Test Method Terminology", id:"http://ncit:C71151", href:newNodeDetails };
var N_346050776_1769352128 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_346050776_1769352128.ontology_node_child_count = 0;
N_346050776_1769352128.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C71152');";
newNodeData = { label:"CDISC SDTM ECG Test Name Terminology", id:"http://ncit:C71152", href:newNodeDetails };
var N_346050777_n1858827593 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_346050777_n1858827593.ontology_node_child_count = 0;
N_346050777_n1858827593.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C71153');";
newNodeData = { label:"CDISC SDTM ECG Test Terminology by Code", id:"http://ncit:C71153", href:newNodeDetails };
var N_346050778_n1062785096 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_346050778_n1062785096.ontology_node_child_count = 0;
N_346050778_n1062785096.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C66790');";
newNodeData = { label:"CDISC SDTM Ethnic Group Terminology", id:"http://ncit:C66790", href:newNodeDetails };
var N_345282099_n1552324628 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_345282099_n1552324628.ontology_node_child_count = 0;
N_345282099_n1552324628.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C78735');";
newNodeData = { label:"CDISC SDTM Evaluator Terminology", id:"http://ncit:C78735", href:newNodeDetails };
var N_346265021_n1660080786 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_346265021_n1660080786.ontology_node_child_count = 0;
N_346265021_n1660080786.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C74560');";
newNodeData = { label:"CDISC SDTM Fitzpatrick Skin Classification Terminology", id:"http://ncit:C74560", href:newNodeDetails };
var N_346144023_755907185 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_346144023_755907185.ontology_node_child_count = 0;
N_346144023_755907185.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C76348');";
newNodeData = { label:"CDISC SDTM Marital Status Terminology", id:"http://ncit:C76348", href:newNodeDetails };
var N_346201629_561352676 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_346201629_561352676.ontology_node_child_count = 0;
N_346201629_561352676.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C85492');";
newNodeData = { label:"CDISC SDTM Method Terminology", id:"http://ncit:C85492", href:newNodeDetails };
var N_347096469_n626074317 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_347096469_n626074317.ontology_node_child_count = 0;
N_347096469_n626074317.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C85495');";
newNodeData = { label:"CDISC SDTM Microbiology Susceptibility Testing Result Category Terminology", id:"http://ncit:C85495", href:newNodeDetails };
var N_347096472_171034536 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_347096472_171034536.ontology_node_child_count = 0;
N_347096472_171034536.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C85491');";
newNodeData = { label:"CDISC SDTM Microorganism Terminology", id:"http://ncit:C85491", href:newNodeDetails };
var N_347096468_805242477 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_347096468_805242477.ontology_node_child_count = 0;
N_347096468_805242477.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C78738');";
newNodeData = { label:"CDISC SDTM Never/Current/Former Classification Terminology", id:"http://ncit:C78738", href:newNodeDetails };
var N_346265024_2135841665 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_346265024_2135841665.ontology_node_child_count = 0;
N_346265024_2135841665.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C85493');";
newNodeData = { label:"CDISC SDTM Pharmacokinetic Parameter Terminology", id:"http://ncit:C85493", href:newNodeDetails };
var N_347096470_1763440941 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_347096470_1763440941.ontology_node_child_count = 0;
N_347096470_1763440941.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C85839');";
newNodeData = { label:"CDISC SDTM Pharmacokinetic Parameter Terminology by Code", id:"http://ncit:C85839", href:newNodeDetails };
var N_347100134_550233504 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_347100134_550233504.ontology_node_child_count = 0;
N_347100134_550233504.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C85494');";
newNodeData = { label:"CDISC SDTM Pharmacokinetic Parameter Unit of Measure Terminology", id:"http://ncit:C85494", href:newNodeDetails };
var N_347096471_n1080465168 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_347096471_n1080465168.ontology_node_child_count = 0;
N_347096471_n1080465168.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C95121');";
newNodeData = { label:"CDISC SDTM Physical Properties Test Code Terminology", id:"http://ncit:C95121", href:newNodeDetails };
var N_348016889_255751452 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_348016889_255751452.ontology_node_child_count = 0;
N_348016889_255751452.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C95120');";
newNodeData = { label:"CDISC SDTM Physical Properties Test Name Terminology", id:"http://ncit:C95120", href:newNodeDetails };
var N_348016888_n716501720 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_348016888_n716501720.ontology_node_child_count = 0;
N_348016888_n716501720.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C74457');";
newNodeData = { label:"CDISC SDTM Race Terminology", id:"http://ncit:C74457", href:newNodeDetails };
var N_346143038_n1346924043 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_346143038_n1346924043.ontology_node_child_count = 0;
N_346143038_n1346924043.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C66727');";
newNodeData = { label:"CDISC SDTM Reason for Non-Completion Terminology", id:"http://ncit:C66727", href:newNodeDetails };
var N_345281889_n1732162641 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_345281889_n1732162641.ontology_node_child_count = 0;
N_345281889_n1732162641.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C78736');";
newNodeData = { label:"CDISC SDTM Reference Range Indicator", id:"http://ncit:C78736", href:newNodeDetails };
var N_346265022_n804916737 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_346265022_n804916737.ontology_node_child_count = 0;
N_346265022_n804916737.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C78737');";
newNodeData = { label:"CDISC SDTM Relationship Type Terminology", id:"http://ncit:C78737", href:newNodeDetails };
var N_346265023_1342147687 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_346265023_1342147687.ontology_node_child_count = 0;
N_346265023_1342147687.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C66728');";
newNodeData = { label:"CDISC SDTM Relation to Reference Period Terminology", id:"http://ncit:C66728", href:newNodeDetails };
var N_345281890_n765208742 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_345281890_n765208742.ontology_node_child_count = 0;
N_345281890_n765208742.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C66769');";
newNodeData = { label:"CDISC SDTM Severity Intensity Scale for Adverse Event Terminology", id:"http://ncit:C66769", href:newNodeDetails };
var N_345282015_1230308773 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_345282015_1230308773.ontology_node_child_count = 0;
N_345282015_1230308773.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C66733');";
newNodeData = { label:"CDISC SDTM Size Terminology", id:"http://ncit:C66733", href:newNodeDetails };
var N_345281916_n1031232147 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_345281916_n1031232147.ontology_node_child_count = 0;
N_345281916_n1031232147.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C74561');";
newNodeData = { label:"CDISC SDTM Skin Type Terminology", id:"http://ncit:C74561", href:newNodeDetails };
var N_346144024_871395347 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_346144024_871395347.ontology_node_child_count = 0;
N_346144024_871395347.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C78733');";
newNodeData = { label:"CDISC SDTM Specimen Condition Terminology", id:"http://ncit:C78733", href:newNodeDetails };
var N_346265019_n2048096394 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_346265019_n2048096394.ontology_node_child_count = 0;
N_346265019_n2048096394.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C78734');";
newNodeData = { label:"CDISC SDTM Specimen Type Terminology", id:"http://ncit:C78734", href:newNodeDetails };
var N_346265020_n418913753 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_346265020_n418913753.ontology_node_child_count = 0;
N_346265020_n418913753.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C74559');";
newNodeData = { label:"CDISC SDTM Subject Characteristic Code Terminology", id:"http://ncit:C74559", href:newNodeDetails };
var N_346144001_n123364309 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_346144001_n123364309.ontology_node_child_count = 0;
N_346144001_n123364309.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C66734');";
newNodeData = { label:"CDISC SDTM Submission Domain Abbreviation Terminology", id:"http://ncit:C66734", href:newNodeDetails };
var N_345281917_1177577070 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_345281917_1177577070.ontology_node_child_count = 0;
N_345281917_1177577070.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C66783');";
newNodeData = { label:"CDISC SDTM System Organ Class Terminology", id:"http://ncit:C66783", href:newNodeDetails };
var N_345282071_1271860235 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_345282071_1271860235.ontology_node_child_count = 0;
N_345282071_1271860235.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C66735');";
newNodeData = { label:"CDISC SDTM Trial Blinding Schema Terminology", id:"http://ncit:C66735", href:newNodeDetails };
var N_345281918_n814381772 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_345281918_n814381772.ontology_node_child_count = 0;
N_345281918_n814381772.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C66736');";
newNodeData = { label:"CDISC SDTM Trial Indication Type Terminology", id:"http://ncit:C66736", href:newNodeDetails };
var N_345281919_520899650 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_345281919_520899650.ontology_node_child_count = 0;
N_345281919_520899650.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C66737');";
newNodeData = { label:"CDISC SDTM Trial Phase Terminology", id:"http://ncit:C66737", href:newNodeDetails };
var N_345281920_n275955378 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_345281920_n275955378.ontology_node_child_count = 0;
N_345281920_n275955378.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C66738');";
newNodeData = { label:"CDISC SDTM Trial Summary Parameter Terminology by Code", id:"http://ncit:C66738", href:newNodeDetails };
var N_345281921_n1078417831 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_345281921_n1078417831.ontology_node_child_count = 0;
N_345281921_n1078417831.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C67152');";
newNodeData = { label:"CDISC SDTM Trial Summary Parameter Terminology by Name", id:"http://ncit:C67152", href:newNodeDetails };
var N_345306002_n434716399 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_345306002_n434716399.ontology_node_child_count = 0;
N_345306002_n434716399.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C66739');";
newNodeData = { label:"CDISC SDTM Trial Type Terminology", id:"http://ncit:C66739", href:newNodeDetails };
var N_345281922_315522307 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_345281922_315522307.ontology_node_child_count = 0;
N_345281922_315522307.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C66770');";
newNodeData = { label:"CDISC SDTM Unit for Vital Sign Result Terminology", id:"http://ncit:C66770", href:newNodeDetails };
var N_345282037_1574131656 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_345282037_1574131656.ontology_node_child_count = 0;
N_345282037_1574131656.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C71620');";
newNodeData = { label:"CDISC SDTM Unit of Measure Terminology", id:"http://ncit:C71620", href:newNodeDetails };
var N_346055487_1961803972 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_346055487_1961803972.ontology_node_child_count = 0;
N_346055487_1961803972.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C76351');";
newNodeData = { label:"CDISC Skin Classification Terminology", id:"http://ncit:C76351", href:newNodeDetails };
var N_346201653_238399765 = new YAHOO.widget.TaskNode(newNodeData, N_345282874_1152700394, false);
N_346201653_238399765.ontology_node_child_count = 0;
N_346201653_238399765.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C77526');";
newNodeData = { label:"CDISC SEND Terminology", id:"http://ncit:C77526", href:newNodeDetails };
var N_346233278_n1311102133 = new YAHOO.widget.TaskNode(newNodeData, N_1487619242_n573977265, true);
N_346233278_n1311102133.isLeaf = false;
N_346233278_n1311102133.ontology_node_child_count = 1;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C66781');";
newNodeData = { label:"CDISC SDTM Age Unit Terminology", id:"http://ncit:C66781", href:newNodeDetails };
var N_345282069_830000938 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_345282069_830000938.ontology_node_child_count = 0;
N_345282069_830000938.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C66786');";
newNodeData = { label:"CDISC SDTM Country Terminology", id:"http://ncit:C66786", href:newNodeDetails };
var N_345282074_n1531431244 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_345282074_n1531431244.ontology_node_child_count = 0;
N_345282074_n1531431244.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C71113');";
newNodeData = { label:"CDISC SDTM Frequency Terminology", id:"http://ncit:C71113", href:newNodeDetails };
var N_346050654_42479041 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_346050654_42479041.ontology_node_child_count = 0;
N_346050654_42479041.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C65047');";
newNodeData = { label:"CDISC SDTM Laboratory Test Terminology by Code", id:"http://ncit:C65047", href:newNodeDetails };
var N_345245433_n965284344 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_345245433_n965284344.ontology_node_child_count = 0;
N_345245433_n965284344.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C67154');";
newNodeData = { label:"CDISC SDTM Laboratory Test Terminology by Name", id:"http://ncit:C67154", href:newNodeDetails };
var N_345306004_n1946157265 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_345306004_n1946157265.ontology_node_child_count = 0;
N_345306004_n1946157265.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C66789');";
newNodeData = { label:"CDISC SDTM Not Done Terminology", id:"http://ncit:C66789", href:newNodeDetails };
var N_345282077_741914300 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_345282077_741914300.ontology_node_child_count = 0;
N_345282077_741914300.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C66726');";
newNodeData = { label:"CDISC SDTM Pharmaceutical Dosage Form Terminology", id:"http://ncit:C66726", href:newNodeDetails };
var N_345281888_922021395 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_345281888_922021395.ontology_node_child_count = 0;
N_345281888_922021395.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C66729');";
newNodeData = { label:"CDISC SDTM Route of Administration Terminology", id:"http://ncit:C66729", href:newNodeDetails };
var N_345281891_n711549189 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_345281891_n711549189.ontology_node_child_count = 0;
N_345281891_n711549189.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C66731');";
newNodeData = { label:"CDISC SDTM Sex of Individual Terminology", id:"http://ncit:C66731", href:newNodeDetails };
var N_345281914_n325098131 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_345281914_n325098131.ontology_node_child_count = 0;
N_345281914_n325098131.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C66732');";
newNodeData = { label:"CDISC SDTM Sex of Study Group Terminology", id:"http://ncit:C66732", href:newNodeDetails };
var N_345281915_1805492989 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_345281915_1805492989.ontology_node_child_count = 0;
N_345281915_1805492989.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C66741');";
newNodeData = { label:"CDISC SDTM Vital Sign Terminology by Code", id:"http://ncit:C66741", href:newNodeDetails };
var N_345281945_n998708709 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_345281945_n998708709.ontology_node_child_count = 0;
N_345281945_n998708709.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C67153');";
newNodeData = { label:"CDISC SDTM Vital Sign Terminology by Name", id:"http://ncit:C67153", href:newNodeDetails };
var N_345306003_n561951809 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_345306003_n561951809.ontology_node_child_count = 0;
N_345306003_n561951809.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C66742');";
newNodeData = { label:"CDISC SDTM Yes No Unknown or Not Applicable Response Terminology", id:"http://ncit:C66742", href:newNodeDetails };
var N_345281946_n290399215 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_345281946_n290399215.ontology_node_child_count = 0;
N_345281946_n290399215.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C77529');";
newNodeData = { label:"CDISC SEND Biospecimens Terminology", id:"http://ncit:C77529", href:newNodeDetails };
var N_346233281_n863104052 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_346233281_n863104052.ontology_node_child_count = 0;
N_346233281_n863104052.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C88026');";
newNodeData = { label:"CDISC SEND Body System Terminology", id:"http://ncit:C88026", href:newNodeDetails };
var N_347181785_n261631282 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_347181785_n261631282.ontology_node_child_count = 0;
N_347181785_n261631282.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C89960');";
newNodeData = { label:"CDISC SEND Body Weight Gain Test Code Terminology", id:"http://ncit:C89960", href:newNodeDetails };
var N_347220343_744534793 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_347220343_744534793.ontology_node_child_count = 0;
N_347220343_744534793.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C89959');";
newNodeData = { label:"CDISC SEND Body Weight Gain Test Name Terminology", id:"http://ncit:C89959", href:newNodeDetails };
var N_347220321_1196492751 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_347220321_1196492751.ontology_node_child_count = 0;
N_347220321_1196492751.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C89962');";
newNodeData = { label:"CDISC SEND Body Weight Test Code Terminology", id:"http://ncit:C89962", href:newNodeDetails };
var N_347220345_n255467374 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_347220345_n255467374.ontology_node_child_count = 0;
N_347220345_n255467374.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C89961');";
newNodeData = { label:"CDISC SEND Body Weight Test Name Terminology", id:"http://ncit:C89961", href:newNodeDetails };
var N_347220344_949728025 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_347220344_949728025.ontology_node_child_count = 0;
N_347220344_949728025.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C89963');";
newNodeData = { label:"CDISC SEND Category for Clinical Observation Terminology", id:"http://ncit:C89963", href:newNodeDetails };
var N_347220346_665834032 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_347220346_665834032.ontology_node_child_count = 0;
N_347220346_665834032.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C90018');";
newNodeData = { label:"CDISC SEND Consciousness State Terminology", id:"http://ncit:C90018", href:newNodeDetails };
var N_347866949_414396691 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_347866949_414396691.ontology_node_child_count = 0;
N_347866949_414396691.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C89964');";
newNodeData = { label:"CDISC SEND Death Diagnosis Object of Measurement Terminology", id:"http://ncit:C89964", href:newNodeDetails };
var N_347220347_n706882828 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_347220347_n706882828.ontology_node_child_count = 0;
N_347220347_n706882828.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C89966');";
newNodeData = { label:"CDISC SEND Death Diagnosis Test Code Terminology", id:"http://ncit:C89966", href:newNodeDetails };
var N_347220349_n892006855 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_347220349_n892006855.ontology_node_child_count = 0;
N_347220349_n892006855.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C89965');";
newNodeData = { label:"CDISC SEND Death Diagnosis Test Name Terminology", id:"http://ncit:C89965", href:newNodeDetails };
var N_347220348_1389840112 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_347220348_1389840112.ontology_node_child_count = 0;
N_347220348_1389840112.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C90012');";
newNodeData = { label:"CDISC SEND Electrocardiogram Category Terminology", id:"http://ncit:C90012", href:newNodeDetails };
var N_347866943_423427196 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_347866943_423427196.ontology_node_child_count = 0;
N_347866943_423427196.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C90013');";
newNodeData = { label:"CDISC SEND Electrocardiogram Lead Terminology", id:"http://ncit:C90013", href:newNodeDetails };
var N_347866944_113338419 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_347866944_113338419.ontology_node_child_count = 0;
N_347866944_113338419.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C89970');";
newNodeData = { label:"CDISC SEND Food and Water Consumption Test Code Terminology", id:"http://ncit:C89970", href:newNodeDetails };
var N_347220374_1607339849 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_347220374_1607339849.ontology_node_child_count = 0;
N_347220374_1607339849.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C89969');";
newNodeData = { label:"CDISC SEND Food and Water Consumption Test Name Terminology", id:"http://ncit:C89969", href:newNodeDetails };
var N_347220352_42436970 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_347220352_42436970.ontology_node_child_count = 0;
N_347220352_42436970.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C77808');";
newNodeData = { label:"CDISC SEND Laboratory Animal Species Terminology", id:"http://ncit:C77808", href:newNodeDetails };
var N_346236101_364951178 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_346236101_364951178.ontology_node_child_count = 0;
N_346236101_364951178.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C77530');";
newNodeData = { label:"CDISC SEND Laboratory Animal Strain Terminology", id:"http://ncit:C77530", href:newNodeDetails };
var N_346233303_1349829844 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_346233303_1349829844.ontology_node_child_count = 0;
N_346233303_1349829844.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C89972');";
newNodeData = { label:"CDISC SEND Macroscopic Findings Test Code Terminology", id:"http://ncit:C89972", href:newNodeDetails };
var N_347220376_n543646180 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_347220376_n543646180.ontology_node_child_count = 0;
N_347220376_n543646180.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C89971');";
newNodeData = { label:"CDISC SEND Macroscopic Findings Test Name Terminology", id:"http://ncit:C89971", href:newNodeDetails };
var N_347220375_n336706962 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_347220375_n336706962.ontology_node_child_count = 0;
N_347220375_n336706962.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C89975');";
newNodeData = { label:"CDISC SEND Method of Termination Terminology", id:"http://ncit:C89975", href:newNodeDetails };
var N_347220379_n396089707 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_347220379_n396089707.ontology_node_child_count = 0;
N_347220379_n396089707.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C89974');";
newNodeData = { label:"CDISC SEND Microscopic Findings Test Code Terminology", id:"http://ncit:C89974", href:newNodeDetails };
var N_347220378_1423163011 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_347220378_1423163011.ontology_node_child_count = 0;
N_347220378_1423163011.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C89973');";
newNodeData = { label:"CDISC SEND Microscopic Findings Test Name Terminology", id:"http://ncit:C89973", href:newNodeDetails };
var N_347220377_1443392844 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_347220377_1443392844.ontology_node_child_count = 0;
N_347220377_1443392844.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C90017');";
newNodeData = { label:"CDISC SEND Microscopic Histopathology Result Category Terminology", id:"http://ncit:C90017", href:newNodeDetails };
var N_347866948_n789298691 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_347866948_n789298691.ontology_node_child_count = 0;
N_347866948_n789298691.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C89977');";
newNodeData = { label:"CDISC SEND Organ Measurement Test Code Terminology", id:"http://ncit:C89977", href:newNodeDetails };
var N_347220381_875371776 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_347220381_875371776.ontology_node_child_count = 0;
N_347220381_875371776.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C89976');";
newNodeData = { label:"CDISC SEND Organ Measurement Test Name Terminology", id:"http://ncit:C89976", href:newNodeDetails };
var N_347220380_n585293772 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_347220380_n585293772.ontology_node_child_count = 0;
N_347220380_n585293772.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C77528');";
newNodeData = { label:"CDISC SEND Pre-Clinical Units of Measure Terminology", id:"http://ncit:C77528", href:newNodeDetails };
var N_346233280_1080513939 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_346233280_1080513939.ontology_node_child_count = 0;
N_346233280_1080513939.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C90000');";
newNodeData = { label:"CDISC SEND Severity Terminology", id:"http://ncit:C90000", href:newNodeDetails };
var N_347866910_n1450845687 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_347866910_n1450845687.ontology_node_child_count = 0;
N_347866910_n1450845687.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C90001');";
newNodeData = { label:"CDISC SEND Specimen Spatial Organization Terminology", id:"http://ncit:C90001", href:newNodeDetails };
var N_347866911_n1626591057 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_347866911_n1626591057.ontology_node_child_count = 0;
N_347866911_n1626591057.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C89968');";
newNodeData = { label:"CDISC SEND Standardized Disposition Term Terminology", id:"http://ncit:C89968", href:newNodeDetails };
var N_347220351_1974176906 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_347220351_1974176906.ontology_node_child_count = 0;
N_347220351_1974176906.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C90002');";
newNodeData = { label:"CDISC SEND Study Category Terminology", id:"http://ncit:C90002", href:newNodeDetails };
var N_347866912_1656984153 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_347866912_1656984153.ontology_node_child_count = 0;
N_347866912_1656984153.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C89967');";
newNodeData = { label:"CDISC SEND Study Design Terminology", id:"http://ncit:C89967", href:newNodeDetails };
var N_347220350_n255876738 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_347220350_n255876738.ontology_node_child_count = 0;
N_347220350_n255876738.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C90003');";
newNodeData = { label:"CDISC SEND Study Type Terminology", id:"http://ncit:C90003", href:newNodeDetails };
var N_347866913_617701301 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_347866913_617701301.ontology_node_child_count = 0;
N_347866913_617701301.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C89981');";
newNodeData = { label:"CDISC SEND Subject Characteristic Test Code Terminology", id:"http://ncit:C89981", href:newNodeDetails };
var N_347220406_n965762436 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_347220406_n965762436.ontology_node_child_count = 0;
N_347220406_n965762436.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C89980');";
newNodeData = { label:"CDISC SEND Subject Characteristic Test Name Terminology", id:"http://ncit:C89980", href:newNodeDetails };
var N_347220405_806138765 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_347220405_806138765.ontology_node_child_count = 0;
N_347220405_806138765.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C90009');";
newNodeData = { label:"CDISC SEND Trial Summary Parameter Code Terminology", id:"http://ncit:C90009", href:newNodeDetails };
var N_347866919_n1040850043 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_347866919_n1040850043.ontology_node_child_count = 0;
N_347866919_n1040850043.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C90007');";
newNodeData = { label:"CDISC SEND Trial Summary Parameter Terminology", id:"http://ncit:C90007", href:newNodeDetails };
var N_347866917_n1409448110 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_347866917_n1409448110.ontology_node_child_count = 0;
N_347866917_n1409448110.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C90004');";
newNodeData = { label:"CDISC SEND Tumor Findings Histopathology Result Category Terminology", id:"http://ncit:C90004", href:newNodeDetails };
var N_347866914_1322600501 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_347866914_1322600501.ontology_node_child_count = 0;
N_347866914_1322600501.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C88025');";
newNodeData = { label:"CDISC SEND Tumor Findings Results Terminology", id:"http://ncit:C88025", href:newNodeDetails };
var N_347181784_n1909630793 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_347181784_n1909630793.ontology_node_child_count = 0;
N_347181784_n1909630793.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C90006');";
newNodeData = { label:"CDISC SEND Tumor Findings Test Code Terminology", id:"http://ncit:C90006", href:newNodeDetails };
var N_347866916_1143815942 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_347866916_1143815942.ontology_node_child_count = 0;
N_347866916_1143815942.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C90005');";
newNodeData = { label:"CDISC SEND Tumor Findings Test Name Terminology", id:"http://ncit:C90005", href:newNodeDetails };
var N_347866915_n1390483684 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_347866915_n1390483684.ontology_node_child_count = 0;
N_347866915_n1390483684.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C89982');";
newNodeData = { label:"CDISC SEND Version Terminology", id:"http://ncit:C89982", href:newNodeDetails };
var N_347220407_n772259780 = new YAHOO.widget.TaskNode(newNodeData, N_346233278_n1311102133, false);
N_347220407_n772259780.ontology_node_child_count = 0;
N_347220407_n772259780.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C83187');";
newNodeData = { label:"CDISC Variable Terminology", id:"http://ncit:C83187", href:newNodeDetails };
var N_347033978_n610465458 = new YAHOO.widget.TaskNode(newNodeData, N_1487619242_n573977265, false);
N_347033978_n610465458.ontology_node_child_count = 0;
N_347033978_n610465458.isLeaf = true;

newNodeData = { label:"DICOM Terminology", id:"TVS_DICOM"};
var N_1488685838_1459567215 = new YAHOO.widget.TaskNode(newNodeData, root, true);
N_1488685838_1459567215.isLeaf = false;
N_1488685838_1459567215.ontology_node_child_count = 1;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C69186');";
newNodeData = { label:"DICOM Terminology", id:"http://ncit:C69186", href:newNodeDetails };
var N_345365681_1570120571 = new YAHOO.widget.TaskNode(newNodeData, N_1488685838_1459567215, false);
N_345365681_1570120571.ontology_node_child_count = 0;
N_345365681_1570120571.isLeaf = true;

newNodeData = { label:"FDA Terminology", id:"TVS_FDA"};
var N_n217443307_1739222229 = new YAHOO.widget.TaskNode(newNodeData, root, true);
N_n217443307_1739222229.isLeaf = false;
N_n217443307_1739222229.ontology_node_child_count = 1;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C63923');";
newNodeData = { label:"FDA Established Names and Unique Ingredient Identifier Codes Terminology", id:"http://ncit:C63923", href:newNodeDetails };
var N_345194434_n1480147386 = new YAHOO.widget.TaskNode(newNodeData, N_n217443307_1739222229, false);
N_345194434_n1480147386.ontology_node_child_count = 0;
N_345194434_n1480147386.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54447');";
newNodeData = { label:"FDA Individual Case Safety Report Terminology", id:"http://ncit:C54447", href:newNodeDetails };
var N_344295965_1649266612 = new YAHOO.widget.TaskNode(newNodeData, N_n217443307_1739222229, true);
N_344295965_1649266612.isLeaf = false;
N_344295965_1649266612.ontology_node_child_count = 1;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54583');";
newNodeData = { label:"Adverse Event Outcome ICSR Terminology", id:"http://ncit:C54583", href:newNodeDetails };
var N_344297046_n91980967 = new YAHOO.widget.TaskNode(newNodeData, N_344295965_1649266612, false);
N_344297046_n91980967.ontology_node_child_count = 0;
N_344297046_n91980967.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54595');";
newNodeData = { label:"Device Usage ICSR Terminology", id:"http://ncit:C54595", href:newNodeDetails };
var N_344297079_34629030 = new YAHOO.widget.TaskNode(newNodeData, N_344295965_1649266612, false);
N_344297079_34629030.ontology_node_child_count = 0;
N_344297079_34629030.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C94849');";
newNodeData = { label:"Dose Denominator Qualifier ICSR Terminology", id:"http://ncit:C94849", href:newNodeDetails };
var N_347993895_1884680429 = new YAHOO.widget.TaskNode(newNodeData, N_344295965_1649266612, false);
N_347993895_1884680429.ontology_node_child_count = 0;
N_347993895_1884680429.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54590');";
newNodeData = { label:"Location Of Event Occurrence ICSR Terminology", id:"http://ncit:C54590", href:newNodeDetails };
var N_344297074_n1361759082 = new YAHOO.widget.TaskNode(newNodeData, N_344295965_1649266612, false);
N_344297074_n1361759082.ontology_node_child_count = 0;
N_344297074_n1361759082.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C88088');";
newNodeData = { label:"Observation ICSR Terminology", id:"http://ncit:C88088", href:newNodeDetails };
var N_347181973_259728666 = new YAHOO.widget.TaskNode(newNodeData, N_344295965_1649266612, false);
N_347181973_259728666.ontology_node_child_count = 0;
N_347181973_259728666.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54585');";
newNodeData = { label:"Occupation ICSR Terminology", id:"http://ncit:C54585", href:newNodeDetails };
var N_344297048_1556038556 = new YAHOO.widget.TaskNode(newNodeData, N_344295965_1649266612, false);
N_344297048_1556038556.ontology_node_child_count = 0;
N_344297048_1556038556.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54584');";
newNodeData = { label:"Operator of Medical Device ICSR Terminology", id:"http://ncit:C54584", href:newNodeDetails };
var N_344297047_n465222921 = new YAHOO.widget.TaskNode(newNodeData, N_344295965_1649266612, false);
N_344297047_n465222921.ontology_node_child_count = 0;
N_344297047_n465222921.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54593');";
newNodeData = { label:"Reason For Non-Evaluation ICSR Terminology", id:"http://ncit:C54593", href:newNodeDetails };
var N_344297077_1759208230 = new YAHOO.widget.TaskNode(newNodeData, N_344295965_1649266612, false);
N_344297077_1759208230.ontology_node_child_count = 0;
N_344297077_1759208230.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54587');";
newNodeData = { label:"Report Source ICSR Terminology", id:"http://ncit:C54587", href:newNodeDetails };
var N_344297050_n1584462134 = new YAHOO.widget.TaskNode(newNodeData, N_344295965_1649266612, false);
N_344297050_n1584462134.ontology_node_child_count = 0;
N_344297050_n1584462134.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54580');";
newNodeData = { label:"Type of Event ICSR Terminology", id:"http://ncit:C54580", href:newNodeDetails };
var N_344297043_n208615665 = new YAHOO.widget.TaskNode(newNodeData, N_344295965_1649266612, false);
N_344297043_n208615665.ontology_node_child_count = 0;
N_344297043_n208615665.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54592');";
newNodeData = { label:"Type Of Follow-Up ICSR Terminology", id:"http://ncit:C54592", href:newNodeDetails };
var N_344297076_n140045353 = new YAHOO.widget.TaskNode(newNodeData, N_344295965_1649266612, false);
N_344297076_n140045353.ontology_node_child_count = 0;
N_344297076_n140045353.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54598');";
newNodeData = { label:"Type Of Manufacturer ICSR Terminology", id:"http://ncit:C54598", href:newNodeDetails };
var N_344297082_n1003411828 = new YAHOO.widget.TaskNode(newNodeData, N_344295965_1649266612, false);
N_344297082_n1003411828.ontology_node_child_count = 0;
N_344297082_n1003411828.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54594');";
newNodeData = { label:"Type Of Remedial Action ICSR Terminology", id:"http://ncit:C54594", href:newNodeDetails };
var N_344297078_838024597 = new YAHOO.widget.TaskNode(newNodeData, N_344295965_1649266612, false);
N_344297078_838024597.ontology_node_child_count = 0;
N_344297078_838024597.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54591');";
newNodeData = { label:"Type of Reportable Event ICSR Terminology", id:"http://ncit:C54591", href:newNodeDetails };
var N_344297075_1796291619 = new YAHOO.widget.TaskNode(newNodeData, N_344295965_1649266612, false);
N_344297075_1796291619.ontology_node_child_count = 0;
N_344297075_1796291619.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54596');";
newNodeData = { label:"Type Of Reporter ICSR Terminology", id:"http://ncit:C54596", href:newNodeDetails };
var N_344297080_1506907983 = new YAHOO.widget.TaskNode(newNodeData, N_344295965_1649266612, false);
N_344297080_1506907983.ontology_node_child_count = 0;
N_344297080_1506907983.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54588');";
newNodeData = { label:"Type Of Report ICSR Terminology", id:"http://ncit:C54588", href:newNodeDetails };
var N_344297051_296792068 = new YAHOO.widget.TaskNode(newNodeData, N_344295965_1649266612, false);
N_344297051_296792068.ontology_node_child_count = 0;
N_344297051_296792068.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C62596');";
newNodeData = { label:"FDA Medical Device Terminology", id:"http://ncit:C62596", href:newNodeDetails };
var N_345161019_1987061883 = new YAHOO.widget.TaskNode(newNodeData, N_n217443307_1739222229, true);
N_345161019_1987061883.isLeaf = false;
N_345161019_1987061883.ontology_node_child_count = 1;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54577');";
newNodeData = { label:"FDA Medical Device Component Or Accessory Terminology", id:"http://ncit:C54577", href:newNodeDetails };
var N_344297019_79479154 = new YAHOO.widget.TaskNode(newNodeData, N_345161019_1987061883, false);
N_344297019_79479154.ontology_node_child_count = 0;
N_344297019_79479154.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C91802');";
newNodeData = { label:"FDA Medical Device Evaluation Conclusions Terminology", id:"http://ncit:C91802", href:newNodeDetails };
var N_347904391_1478312266 = new YAHOO.widget.TaskNode(newNodeData, N_345161019_1987061883, false);
N_347904391_1478312266.ontology_node_child_count = 0;
N_347904391_1478312266.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C91800');";
newNodeData = { label:"FDA Medical Device Evaluation Methods Terminology", id:"http://ncit:C91800", href:newNodeDetails };
var N_347904389_n1633552343 = new YAHOO.widget.TaskNode(newNodeData, N_345161019_1987061883, false);
N_347904389_n1633552343.ontology_node_child_count = 0;
N_347904389_n1633552343.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C91801');";
newNodeData = { label:"FDA Medical Device Evaluation Results Terminology", id:"http://ncit:C91801", href:newNodeDetails };
var N_347904390_1228139075 = new YAHOO.widget.TaskNode(newNodeData, N_345161019_1987061883, false);
N_347904390_1228139075.ontology_node_child_count = 0;
N_347904390_1228139075.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54451');";
newNodeData = { label:"FDA Medical Device Problem Codes Terminology", id:"http://ncit:C54451", href:newNodeDetails };
var N_344295990_1954010343 = new YAHOO.widget.TaskNode(newNodeData, N_345161019_1987061883, false);
N_344295990_1954010343.ontology_node_child_count = 0;
N_344295990_1954010343.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54450');";
newNodeData = { label:"FDA Patient Problem Codes Terminology", id:"http://ncit:C54450", href:newNodeDetails };
var N_344295989_n1426220311 = new YAHOO.widget.TaskNode(newNodeData, N_345161019_1987061883, false);
N_344295989_n1426220311.ontology_node_child_count = 0;
N_344295989_n1426220311.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54452');";
newNodeData = { label:"FDA SPL Terminology", id:"http://ncit:C54452", href:newNodeDetails };
var N_344295991_506294390 = new YAHOO.widget.TaskNode(newNodeData, N_n217443307_1739222229, true);
N_344295991_506294390.isLeaf = false;
N_344295991_506294390.ontology_node_child_count = 1;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C73600');";
newNodeData = { label:"FDA SPL Business Operation Terminology", id:"http://ncit:C73600", href:newNodeDetails };
var N_346115007_n1210804560 = new YAHOO.widget.TaskNode(newNodeData, N_344295991_506294390, false);
N_346115007_n1210804560.ontology_node_child_count = 0;
N_346115007_n1210804560.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54453');";
newNodeData = { label:"FDA SPL Color Terminology", id:"http://ncit:C54453", href:newNodeDetails };
var N_344295992_n49722644 = new YAHOO.widget.TaskNode(newNodeData, N_344295991_506294390, false);
N_344295992_n49722644.ontology_node_child_count = 0;
N_344295992_n49722644.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54459');";
newNodeData = { label:"FDA SPL DEA Schedule Terminology", id:"http://ncit:C54459", href:newNodeDetails };
var N_344295998_925488347 = new YAHOO.widget.TaskNode(newNodeData, N_344295991_506294390, false);
N_344295998_925488347.ontology_node_child_count = 0;
N_344295998_925488347.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C87299');";
newNodeData = { label:"FDA SPL Document Type Terminology", id:"http://ncit:C87299", href:newNodeDetails };
var N_347154136_411683080 = new YAHOO.widget.TaskNode(newNodeData, N_344295991_506294390, false);
N_347154136_411683080.ontology_node_child_count = 0;
N_347154136_411683080.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54455');";
newNodeData = { label:"FDA SPL Drug Route of Administration Terminology", id:"http://ncit:C54455", href:newNodeDetails };
var N_344295994_n1037336846 = new YAHOO.widget.TaskNode(newNodeData, N_344295991_506294390, false);
N_344295994_n1037336846.ontology_node_child_count = 0;
N_344295994_n1037336846.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C73339');";
newNodeData = { label:"FDA SPL Flavor Terminology", id:"http://ncit:C73339", href:newNodeDetails };
var N_346112226_n853929017 = new YAHOO.widget.TaskNode(newNodeData, N_344295991_506294390, false);
N_346112226_n853929017.ontology_node_child_count = 0;
N_346112226_n853929017.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54599');";
newNodeData = { label:"FDA SPL Limitation of Use Terminology", id:"http://ncit:C54599", href:newNodeDetails };
var N_344297083_571190001 = new YAHOO.widget.TaskNode(newNodeData, N_344295991_506294390, false);
N_344297083_571190001.ontology_node_child_count = 0;
N_344297083_571190001.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C73582');";
newNodeData = { label:"FDA SPL Marketing Category Terminology", id:"http://ncit:C73582", href:newNodeDetails };
var N_346114296_1662635688 = new YAHOO.widget.TaskNode(newNodeData, N_344295991_506294390, false);
N_346114296_1662635688.ontology_node_child_count = 0;
N_346114296_1662635688.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54619');";
newNodeData = { label:"FDA SPL Medical Product Intent of Use Terminology", id:"http://ncit:C54619", href:newNodeDetails };
var N_344297796_2016694623 = new YAHOO.widget.TaskNode(newNodeData, N_344295991_506294390, false);
N_344297796_2016694623.ontology_node_child_count = 0;
N_344297796_2016694623.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54457');";
newNodeData = { label:"FDA SPL Package Type Terminology", id:"http://ncit:C54457", href:newNodeDetails };
var N_344295996_2141287233 = new YAHOO.widget.TaskNode(newNodeData, N_344295991_506294390, false);
N_344295996_2141287233.ontology_node_child_count = 0;
N_344295996_2141287233.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54456');";
newNodeData = { label:"FDA SPL Pharmaceutical Dosage Form Terminology", id:"http://ncit:C54456", href:newNodeDetails };
var N_344295995_1023438064 = new YAHOO.widget.TaskNode(newNodeData, N_344295991_506294390, false);
N_344295995_1023438064.ontology_node_child_count = 0;
N_344295995_1023438064.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54600');";
newNodeData = { label:"FDA SPL Pharmacokinetic Effect Consequences Terminology", id:"http://ncit:C54600", href:newNodeDetails };
var N_344297756_880518409 = new YAHOO.widget.TaskNode(newNodeData, N_344295991_506294390, false);
N_344297756_880518409.ontology_node_child_count = 0;
N_344297756_880518409.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54458');";
newNodeData = { label:"FDA SPL Potency Terminology", id:"http://ncit:C54458", href:newNodeDetails };
var N_344295997_n510525342 = new YAHOO.widget.TaskNode(newNodeData, N_344295991_506294390, false);
N_344295997_n510525342.ontology_node_child_count = 0;
N_344295997_n510525342.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54454');";
newNodeData = { label:"FDA SPL Shape Terminology", id:"http://ncit:C54454", href:newNodeDetails };
var N_344295993_n1746235768 = new YAHOO.widget.TaskNode(newNodeData, N_344295991_506294390, false);
N_344295993_n1746235768.ontology_node_child_count = 0;
N_344295993_n1746235768.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54618');";
newNodeData = { label:"FDA SPL Type of Drug Interaction Terminology", id:"http://ncit:C54618", href:newNodeDetails };
var N_344297795_1465991337 = new YAHOO.widget.TaskNode(newNodeData, N_344295991_506294390, false);
N_344297795_1465991337.ontology_node_child_count = 0;
N_344297795_1465991337.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C92951');";
newNodeData = { label:"FDA SPL Unit of Measure Terminology", id:"http://ncit:C92951", href:newNodeDetails };
var N_347935297_n1076174515 = new YAHOO.widget.TaskNode(newNodeData, N_344295991_506294390, false);
N_347935297_n1076174515.ontology_node_child_count = 0;
N_347935297_n1076174515.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C87300');";
newNodeData = { label:"FDA SPL Unit of Presentation Terminology", id:"http://ncit:C87300", href:newNodeDetails };
var N_347154809_n855202637 = new YAHOO.widget.TaskNode(newNodeData, N_344295991_506294390, false);
N_347154809_n855202637.ontology_node_child_count = 0;
N_347154809_n855202637.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ndfrt:MoA');";
newNodeData = { label:"NDFRT Mechanism of Action ", id:"http://ndfrt:MoA", href:newNodeDetails };
var N_542640421_751465558 = new YAHOO.widget.TaskNode(newNodeData, N_344295991_506294390, false);
N_542640421_751465558.ontology_node_child_count = 0;
N_542640421_751465558.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ndfrt:PE');";
newNodeData = { label:"NDFRT Physiologic Effects", id:"http://ndfrt:PE", href:newNodeDetails };
var N_433146575_1761931591 = new YAHOO.widget.TaskNode(newNodeData, N_344295991_506294390, false);
N_433146575_1761931591.ontology_node_child_count = 0;
N_433146575_1761931591.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ndfrt:SC');";
newNodeData = { label:"NDFRT Structural Class", id:"http://ndfrt:SC", href:newNodeDetails };
var N_433146666_n404010803 = new YAHOO.widget.TaskNode(newNodeData, N_344295991_506294390, false);
N_433146666_n404010803.ontology_node_child_count = 0;
N_433146666_n404010803.isLeaf = true;

newNodeData = { label:"Individual Case Safety Report Terminology", id:"TVS_ICSR"};
var N_1849281127_558550728 = new YAHOO.widget.TaskNode(newNodeData, root, true);
N_1849281127_558550728.isLeaf = false;
N_1849281127_558550728.ontology_node_child_count = 1;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54447');";
newNodeData = { label:"FDA Individual Case Safety Report Terminology", id:"http://ncit:C54447", href:newNodeDetails };
var N_344295965_1986790225 = new YAHOO.widget.TaskNode(newNodeData, N_1849281127_558550728, true);
N_344295965_1986790225.isLeaf = false;
N_344295965_1986790225.ontology_node_child_count = 1;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54583');";
newNodeData = { label:"Adverse Event Outcome ICSR Terminology", id:"http://ncit:C54583", href:newNodeDetails };
var N_344297046_1061085159 = new YAHOO.widget.TaskNode(newNodeData, N_344295965_1986790225, false);
N_344297046_1061085159.ontology_node_child_count = 0;
N_344297046_1061085159.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54595');";
newNodeData = { label:"Device Usage ICSR Terminology", id:"http://ncit:C54595", href:newNodeDetails };
var N_344297079_336214041 = new YAHOO.widget.TaskNode(newNodeData, N_344295965_1986790225, false);
N_344297079_336214041.ontology_node_child_count = 0;
N_344297079_336214041.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C94849');";
newNodeData = { label:"Dose Denominator Qualifier ICSR Terminology", id:"http://ncit:C94849", href:newNodeDetails };
var N_347993895_243530634 = new YAHOO.widget.TaskNode(newNodeData, N_344295965_1986790225, false);
N_347993895_243530634.ontology_node_child_count = 0;
N_347993895_243530634.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54590');";
newNodeData = { label:"Location Of Event Occurrence ICSR Terminology", id:"http://ncit:C54590", href:newNodeDetails };
var N_344297074_n125218123 = new YAHOO.widget.TaskNode(newNodeData, N_344295965_1986790225, false);
N_344297074_n125218123.ontology_node_child_count = 0;
N_344297074_n125218123.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C88088');";
newNodeData = { label:"Observation ICSR Terminology", id:"http://ncit:C88088", href:newNodeDetails };
var N_347181973_n1165760147 = new YAHOO.widget.TaskNode(newNodeData, N_344295965_1986790225, false);
N_347181973_n1165760147.ontology_node_child_count = 0;
N_347181973_n1165760147.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54585');";
newNodeData = { label:"Occupation ICSR Terminology", id:"http://ncit:C54585", href:newNodeDetails };
var N_344297048_1298200125 = new YAHOO.widget.TaskNode(newNodeData, N_344295965_1986790225, false);
N_344297048_1298200125.ontology_node_child_count = 0;
N_344297048_1298200125.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54584');";
newNodeData = { label:"Operator of Medical Device ICSR Terminology", id:"http://ncit:C54584", href:newNodeDetails };
var N_344297047_123805772 = new YAHOO.widget.TaskNode(newNodeData, N_344295965_1986790225, false);
N_344297047_123805772.ontology_node_child_count = 0;
N_344297047_123805772.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54593');";
newNodeData = { label:"Reason For Non-Evaluation ICSR Terminology", id:"http://ncit:C54593", href:newNodeDetails };
var N_344297077_n700415697 = new YAHOO.widget.TaskNode(newNodeData, N_344295965_1986790225, false);
N_344297077_n700415697.ontology_node_child_count = 0;
N_344297077_n700415697.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54587');";
newNodeData = { label:"Report Source ICSR Terminology", id:"http://ncit:C54587", href:newNodeDetails };
var N_344297050_605509013 = new YAHOO.widget.TaskNode(newNodeData, N_344295965_1986790225, false);
N_344297050_605509013.ontology_node_child_count = 0;
N_344297050_605509013.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54580');";
newNodeData = { label:"Type of Event ICSR Terminology", id:"http://ncit:C54580", href:newNodeDetails };
var N_344297043_n691898298 = new YAHOO.widget.TaskNode(newNodeData, N_344295965_1986790225, false);
N_344297043_n691898298.ontology_node_child_count = 0;
N_344297043_n691898298.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54592');";
newNodeData = { label:"Type Of Follow-Up ICSR Terminology", id:"http://ncit:C54592", href:newNodeDetails };
var N_344297076_1574928888 = new YAHOO.widget.TaskNode(newNodeData, N_344295965_1986790225, false);
N_344297076_1574928888.ontology_node_child_count = 0;
N_344297076_1574928888.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54598');";
newNodeData = { label:"Type Of Manufacturer ICSR Terminology", id:"http://ncit:C54598", href:newNodeDetails };
var N_344297082_n416543220 = new YAHOO.widget.TaskNode(newNodeData, N_344295965_1986790225, false);
N_344297082_n416543220.ontology_node_child_count = 0;
N_344297082_n416543220.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54594');";
newNodeData = { label:"Type Of Remedial Action ICSR Terminology", id:"http://ncit:C54594", href:newNodeDetails };
var N_344297078_1856971592 = new YAHOO.widget.TaskNode(newNodeData, N_344295965_1986790225, false);
N_344297078_1856971592.ontology_node_child_count = 0;
N_344297078_1856971592.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54591');";
newNodeData = { label:"Type of Reportable Event ICSR Terminology", id:"http://ncit:C54591", href:newNodeDetails };
var N_344297075_n2069669628 = new YAHOO.widget.TaskNode(newNodeData, N_344295965_1986790225, false);
N_344297075_n2069669628.ontology_node_child_count = 0;
N_344297075_n2069669628.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54596');";
newNodeData = { label:"Type Of Reporter ICSR Terminology", id:"http://ncit:C54596", href:newNodeDetails };
var N_344297080_n517387381 = new YAHOO.widget.TaskNode(newNodeData, N_344295965_1986790225, false);
N_344297080_n517387381.ontology_node_child_count = 0;
N_344297080_n517387381.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C54588');";
newNodeData = { label:"Type Of Report ICSR Terminology", id:"http://ncit:C54588", href:newNodeDetails };
var N_344297051_893722770 = new YAHOO.widget.TaskNode(newNodeData, N_344295965_1986790225, false);
N_344297051_893722770.ontology_node_child_count = 0;
N_344297051_893722770.isLeaf = true;

newNodeData = { label:"NCI Terminology", id:"TVS_NCI"};
var N_n217435642_n1541388752 = new YAHOO.widget.TaskNode(newNodeData, root, true);
N_n217435642_n1541388752.isLeaf = false;
N_n217435642_n1541388752.ontology_node_child_count = 1;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:Neoplasm');";
newNodeData = { label:"NCIt Neoplasm Tree", id:"http://ncit:Neoplasm", href:newNodeDetails };
var N_n751234993_1261635856 = new YAHOO.widget.TaskNode(newNodeData, N_n217435642_n1541388752, false);
N_n751234993_1261635856.ontology_node_child_count = 0;
N_n751234993_1261635856.isLeaf = true;

newNodeData = { label:"NCPDP Terminology", id:"TVS_NCPDP"};
var N_1497754457_n1155360830 = new YAHOO.widget.TaskNode(newNodeData, root, true);
N_1497754457_n1155360830.isLeaf = false;
N_1497754457_n1155360830.ontology_node_child_count = 1;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C89415');";
newNodeData = { label:"National Council for Prescription Drug Programs Terminology", id:"http://ncit:C89415", href:newNodeDetails };
var N_347215388_311976366 = new YAHOO.widget.TaskNode(newNodeData, N_1497754457_n1155360830, true);
N_347215388_311976366.isLeaf = false;
N_347215388_311976366.ontology_node_child_count = 1;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C89507');";
newNodeData = { label:"NCPDP DEA Schedule Terminology", id:"http://ncit:C89507", href:newNodeDetails };
var N_347216320_2048436622 = new YAHOO.widget.TaskNode(newNodeData, N_347215388_311976366, false);
N_347216320_2048436622.ontology_node_child_count = 0;
N_347216320_2048436622.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C91101');";
newNodeData = { label:"NCPDP Measurement Unit Code Terminology", id:"http://ncit:C91101", href:newNodeDetails };
var N_347897663_596212496 = new YAHOO.widget.TaskNode(newNodeData, N_347215388_311976366, false);
N_347897663_596212496.ontology_node_child_count = 0;
N_347897663_596212496.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C89510');";
newNodeData = { label:"NCPDP Quantity Unit of Measure Terminology", id:"http://ncit:C89510", href:newNodeDetails };
var N_347216344_n732896747 = new YAHOO.widget.TaskNode(newNodeData, N_347215388_311976366, false);
N_347216344_n732896747.ontology_node_child_count = 0;
N_347216344_n732896747.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C89508');";
newNodeData = { label:"NCPDP Strength Form Terminology", id:"http://ncit:C89508", href:newNodeDetails };
var N_347216321_1262242766 = new YAHOO.widget.TaskNode(newNodeData, N_347215388_311976366, false);
N_347216321_1262242766.ontology_node_child_count = 0;
N_347216321_1262242766.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C89509');";
newNodeData = { label:"NCPDP Strength Unit of Measure Terminology", id:"http://ncit:C89509", href:newNodeDetails };
var N_347216322_1106644042 = new YAHOO.widget.TaskNode(newNodeData, N_347215388_311976366, false);
N_347216322_1106644042.ontology_node_child_count = 0;
N_347216322_1106644042.isLeaf = true;

newNodeData = { label:"NDFRT Terminology", id:"TVS_NDFRT"};
var N_1497775076_1850527801 = new YAHOO.widget.TaskNode(newNodeData, root, true);
N_1497775076_1850527801.isLeaf = false;
N_1497775076_1850527801.ontology_node_child_count = 1;

newNodeDetails = "javascript:onClickTreeNode('http://ndfrt:MoA');";
newNodeData = { label:"NDFRT Mechanism of Action ", id:"http://ndfrt:MoA", href:newNodeDetails };
var N_542640421_789844708 = new YAHOO.widget.TaskNode(newNodeData, N_1497775076_1850527801, false);
N_542640421_789844708.ontology_node_child_count = 0;
N_542640421_789844708.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ndfrt:PE');";
newNodeData = { label:"NDFRT Physiologic Effects", id:"http://ndfrt:PE", href:newNodeDetails };
var N_433146575_n1786637040 = new YAHOO.widget.TaskNode(newNodeData, N_1497775076_1850527801, false);
N_433146575_n1786637040.ontology_node_child_count = 0;
N_433146575_n1786637040.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ndfrt:SC');";
newNodeData = { label:"NDFRT Structural Class", id:"http://ndfrt:SC", href:newNodeDetails };
var N_433146666_n1322907061 = new YAHOO.widget.TaskNode(newNodeData, N_1497775076_1850527801, false);
N_433146666_n1322907061.ontology_node_child_count = 0;
N_433146666_n1322907061.isLeaf = true;

newNodeData = { label:"NICHD Pediatric Terminology", id:"TVS_NICHD"};
var N_1497920822_n26818283 = new YAHOO.widget.TaskNode(newNodeData, root, true);
N_1497920822_n26818283.isLeaf = false;
N_1497920822_n26818283.ontology_node_child_count = 1;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C90259');";
newNodeData = { label:"National Institute of Child Health and Human Development Pediatric Terminology", id:"http://ncit:C90259", href:newNodeDetails };
var N_347868996_1726454512 = new YAHOO.widget.TaskNode(newNodeData, N_1497920822_n26818283, true);
N_347868996_1726454512.isLeaf = false;
N_347868996_1726454512.ontology_node_child_count = 1;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C96388');";
newNodeData = { label:"NICHD Childhood Immunization Terminology", id:"http://ncit:C96388", href:newNodeDetails };
var N_348048795_642291612 = new YAHOO.widget.TaskNode(newNodeData, N_347868996_1726454512, false);
N_348048795_642291612.ontology_node_child_count = 0;
N_348048795_642291612.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C97150');";
newNodeData = { label:"NICHD Neurological Development Terminology", id:"http://ncit:C97150", href:newNodeDetails };
var N_348076563_2037180169 = new YAHOO.widget.TaskNode(newNodeData, N_347868996_1726454512, false);
N_348076563_2037180169.ontology_node_child_count = 0;
N_348076563_2037180169.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C89506');";
newNodeData = { label:"NICHD Newborn Screening Terminology", id:"http://ncit:C89506", href:newNodeDetails };
var N_347216319_n1583743354 = new YAHOO.widget.TaskNode(newNodeData, N_347868996_1726454512, false);
N_347216319_n1583743354.ontology_node_child_count = 0;
N_347216319_n1583743354.isLeaf = true;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C92712');";
newNodeData = { label:"NICHD Pregnancy and Childbirth Terminology", id:"http://ncit:C92712", href:newNodeDetails };
var N_347933252_n254455284 = new YAHOO.widget.TaskNode(newNodeData, N_347868996_1726454512, false);
N_347933252_n254455284.ontology_node_child_count = 0;
N_347933252_n254455284.isLeaf = true;

newNodeData = { label:"UCUM Terminology", id:"TVS_UCUM"};
var N_1849638676_n1142236083 = new YAHOO.widget.TaskNode(newNodeData, root, true);
N_1849638676_n1142236083.isLeaf = false;
N_1849638676_n1142236083.ontology_node_child_count = 1;

newNodeDetails = "javascript:onClickTreeNode('http://ncit:C67567');";
newNodeData = { label:"Unified Code for Units of Measure Terminology", id:"http://ncit:C67567", href:newNodeDetails };
var N_345309882_1211493727 = new YAHOO.widget.TaskNode(newNodeData, N_1849638676_n1142236083, false);
N_345309882_1211493727.ontology_node_child_count = 0;
N_345309882_1211493727.isLeaf = true;

         tree.collapseAll();
      tree.draw();
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
            //if (n.checkState == 2) {
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
      var cObj = YAHOO.util.Connect.asyncRequest('GET','/ncitbrowser/ajax?action=expand_src_vs_tree&ontology_node_id=' +id+'&ontology_display_name='+ontology_display_name+'&version='+ontology_version,callback);
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
        var request = YAHOO.util.Connect.asyncRequest('GET','/ncitbrowser/ajax?action=search_vs_tree&ontology_node_id=' +ontology_node_id+'&ontology_display_name='+ontology_display_name+'&version='+ontology_version+'&ontology_source='+ontology_source,buildTreeCallback);

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
        var request = YAHOO.util.Connect.asyncRequest('GET','/ncitbrowser/ajax?action=expand_entire_vs_tree&ontology_node_id=' +ontology_node_id+'&ontology_display_name='+ontology_display_name+'&version='+ontology_version+'&ontology_source='+ontology_source,buildTreeCallback);

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
  <script type="text/javascript" src="/ncitbrowser/js/wz_tooltip.js"></script>
  <script type="text/javascript" src="/ncitbrowser/js/tip_centerwindow.js"></script>
  <script type="text/javascript" src="/ncitbrowser/js/tip_followscroll.js"></script>





  <!-- Begin Skip Top Navigation -->
  <a href="#evs-content" class="hideLink" accesskey="1" title="Skip repetitive navigation links">skip navigation links</A>
  <!-- End Skip Top Navigation -->

<!-- nci banner -->
<div class="ncibanner">
  <a href="http://www.cancer.gov" target="_blank">
    <img src="/ncitbrowser/images/logotype.gif"
      width="440" height="39" border="0"
      alt="National Cancer Institute"/>
  </a>
  <a href="http://www.cancer.gov" target="_blank">
    <img src="/ncitbrowser/images/spacer.gif"
      width="48" height="39" border="0"
      alt="National Cancer Institute" class="print-header"/>
  </a>
  <a href="http://www.nih.gov" target="_blank" >
    <img src="/ncitbrowser/images/tagline_nologo.gif"
      width="173" height="39" border="0"
      alt="U.S. National Institutes of Health"/>
  </a>
  <a href="http://www.cancer.gov" target="_blank">
    <img src="/ncitbrowser/images/cancer-gov.gif"
      width="99" height="39" border="0"
      alt="www.cancer.gov"/>
  </a>
</div>
<!-- end nci banner -->

  <div class="center-page">
    <!-- EVS Logo -->
<div>
  <img src="/ncitbrowser/images/evs-logo-swapped.gif" alt="EVS Logo"
       width="745" height="26" border="0"
       usemap="#external-evs" />
  <map id="external-evs" name="external-evs">
    <area shape="rect" coords="0,0,140,26"
      href="/ncitbrowser/start.jsf" target="_self"
      alt="NCI Term Browser" />
    <area shape="rect" coords="520,0,745,26"
      href="http://evs.nci.nih.gov/" target="_blank"
      alt="Enterprise Vocabulary Services" />
  </map>
</div>


<table cellspacing="0" cellpadding="0" border="0">
  <tr>
    <td width="5"></td>
    <td><a href="/ncitbrowser/pages/multiple_search.jsf?nav_type=terminologies">
      <img name="tab_terms" src="/ncitbrowser/images/tab_terms.gif"
        border="0" alt="Terminologies" title="Terminologies" /></a></td>
    <td><a href="/ncitbrowser/ajax?action=create_src_vs_tree">
      <img name="tab_valuesets" src="/ncitbrowser/images/tab_valuesets_clicked.gif"
        border="0" alt="Value Sets" title="ValueSets" /></a></td>
    <td><a href="/ncitbrowser/pages/mapping_search.jsf?nav_type=mappings">
      <img name="tab_map" src="/ncitbrowser/images/tab_map.gif"
        border="0" alt="Mappings" title="Mappings" /></a></td>
  </tr>
</table>

<div class="mainbox-top"><img src="/ncitbrowser/images/mainbox-top.gif" width="745" height="5" alt=""/></div>
<!-- end EVS Logo -->
    <!-- Main box -->
    <div id="main-area">

      <!-- Thesaurus, banner search area -->
      <div class="bannerarea">
        <a href="/ncitbrowser/start.jsf" style="text-decoration: none;">
          <div class="vocabularynamebanner_tb">
            <span class="vocabularynamelong_tb">Version 2.1 (using LexEVS 6.0)</span>
          </div>
        </a>
        <div class="search-globalnav">
          <!-- Search box -->
          <div class="searchbox-top"><img src="/ncitbrowser/images/searchbox-top.gif" width="352" height="2" alt="SearchBox Top" /></div>
          <div class="searchbox">


<form id="valueSetSearchForm" name="valueSetSearchForm" method="post" action="/ncitbrowser/ajax?action=search_value_set" class="search-form-main-area" enctype="application/x-www-form-urlencoded">
<input type="hidden" name="valueSetSearchForm" value="valueSetSearchForm" />
<input type="hidden" name="view" value="1" />



            <input type="hidden" id="checked_vocabularies" name="checked_vocabularies" value="" />



<table border="0" cellspacing="0" cellpadding="0" style="margin: 2px" >
  <tr valign="top" align="left">
    <td align="left" class="textbody">

                  <input CLASS="searchbox-input-2"
                    name="matchText"
                    value=""
                    onFocus="active = true"
                    onBlur="active = false"
                    onkeypress="return submitEnter('valueSetSearchForm:valueset_search',event)"
                    tabindex="1"/>


                <input id="valueSetSearchForm:valueset_search" type="image" src="/ncitbrowser/images/search.gif" name="valueSetSearchForm:valueset_search" alt="Search" onclick="javascript:getCheckedNodes();" tabindex="2" class="searchbox-btn" /><a href="/ncitbrowser/pages/help.jsf#searchhelp" tabindex="3"><img src="/ncitbrowser/images/search-help.gif" alt="Search Help" style="border-width:0;" class="searchbox-btn" /></a>


    </td>
  </tr>

  <tr valign="top" align="left">
    <td>
      <table border="0" cellspacing="0" cellpadding="0" style="margin: 0px">

        <tr valign="top" align="left">
        <td align="left" class="textbody">
                     <input type="radio" name="valueset_search_algorithm" value="exactMatch" alt="Exact Match" checked tabindex="3">Exact Match&nbsp;
                     <input type="radio" name="valueset_search_algorithm" value="startsWith" alt="Begins With"  tabindex="3">Begins With&nbsp;
                     <input type="radio" name="valueset_search_algorithm" value="contains" alt="Contains"  tabindex="3">Contains
        </td>
        </tr>

        <tr align="left">
            <td height="1px" bgcolor="#2F2F5F" align="left"></td>
        </tr>
        <tr valign="top" align="left">
          <td align="left" class="textbody">
                <input type="radio" id="selectValueSetSearchOption" name="selectValueSetSearchOption" value="Code"  alt="Code" tabindex="1" >Code&nbsp;
                <input type="radio" id="selectValueSetSearchOption" name="selectValueSetSearchOption" value="Name" checked alt="Name" tabindex="1" >Name
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
                <input type="hidden" name="referer" id="referer" value="http%3A%2F%2Flocalhost%3A8080%2Fncitbrowser%2Fpages%2Fresolved_value_set_search_results.jsf">
                <input type="hidden" id="nav_type" name="nav_type" value="valuesets" />
                <input type="hidden" id="view" name="view" value="source" />

<input type="hidden" name="javax.faces.ViewState" id="javax.faces.ViewState" value="j_id22:j_id23" />
</form>
          </div> <!-- searchbox -->

          <div class="searchbox-bottom"><img src="/ncitbrowser/images/searchbox-bottom.gif" width="352" height="2" alt="SearchBox Bottom" /></div>
          <!-- end Search box -->
          <!-- Global Navigation -->

<table class="global-nav" border="0" width="100%" height="37px" cellpadding="0" cellspacing="0">
  <tr>
    <td align="left" valign="bottom">
      <a href="#" onclick="javascript:window.open('/ncitbrowser/pages/source_help_info-termbrowser.jsf',
        '_blank','top=100, left=100, height=740, width=780, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');" tabindex="13">
        Sources</a>

 

      
 

    </td>
    <td align="right" valign="bottom">
      <a href="
/ncitbrowser/pages/help.jsf" tabindex="16">Help</a>

    </td>

    <td width="7"></td>

  </tr>

</table>
          <!-- end Global Navigation -->

        </div> <!-- search-globalnav -->
      </div> <!-- bannerarea -->

      <!-- end Thesaurus, banner search area -->
      <!-- Quick links bar -->

<div class="bluebar">
  <table border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td><div class="quicklink-status">&nbsp;</div></td>
    <td>

  <div id="quicklinksholder">
      <ul id="quicklinks"
        onmouseover="document.quicklinksimg.src='/ncitbrowser/images/quicklinks-active.gif';"
        onmouseout="document.quicklinksimg.src='/ncitbrowser/images/quicklinks-inactive.gif';">
        <li>
          <a href="#" tabindex="-1"><img src="/ncitbrowser/images/quicklinks-inactive.gif" width="162"
            height="18" border="0" name="quicklinksimg" alt="Quick Links" />
          </a>
          <ul>
            <li><a href="http://evs.nci.nih.gov/" tabindex="-1" target="_blank"
              alt="Enterprise Vocabulary Services">EVS Home</a></li>
            <li><a href="http://localhost/ncimbrowserncimbrowser" tabindex="-1" target="_blank"
              alt="NCI Metathesaurus">NCI Metathesaurus Browser</a></li>

            <li><a href="/ncitbrowser/start.jsf" tabindex="-1"
              alt="NCI Term Browser">NCI Term Browser</a></li>
            <li><a href="http://www.cancer.gov/cancertopics/terminologyresources" tabindex="-1" target="_blank"
              alt="NCI Terminology Resources">NCI Terminology Resources</a></li>

              <li><a href="http://ncitermform.nci.nih.gov/ncitermform/?dictionary=NCI%20Thesaurus" tabindex="-1" target="_blank" alt="Term Suggestion">Term Suggestion</a></li>


          </ul>
        </li>
      </ul>
  </div>

      </td>
    </tr>
  </table>

</div>
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




            <tr class="textbody">
              <td class="textbody" align="left">

                Standards View
                &nbsp;|
                <a href="/ncitbrowser/ajax?action=create_cs_vs_tree">Terminology View</a>
              </td>

              <td align="right">
               <font size="1" color="red" align="right">
                 <a href="javascript:printPage()"><img src="/ncitbrowser/images/printer.bmp" border="0" alt="Send to Printer"><i>Send to Printer</i></a>
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

            <input type="hidden" id="ontology_node_id" name="ontology_node_id" value="null" />
            <input type="hidden" id="ontology_display_name" name="ontology_display_name" value="null" />
            <input type="hidden" id="schema" name="schema" value="null" />
            <input type="hidden" id="ontology_version" name="ontology_version" value="null" />
            <input type="hidden" id="view" name="view" value="source" />
          </form>


        </div> <!-- popupContentArea -->


<div class="textbody">
<!-- footer -->
<div class="footer" style="width:720px">
  <ul>
    <li><a href="http://www.cancer.gov" target="_blank" alt="National Cancer Institute">NCI Home</a> |</li>
    <li><a href="/ncitbrowser/pages/contact_us.jsf">Contact Us</a> |</li>
    <li><a href="http://www.cancer.gov/policies" target="_blank" alt="National Cancer Institute Policies">Policies</a> |</li>
    <li><a href="http://www.cancer.gov/policies/page3" target="_blank" alt="National Cancer Institute Accessibility">Accessibility</a> |</li>
    <li><a href="http://www.cancer.gov/policies/page6" target="_blank" alt="National Cancer Institute FOIA">FOIA</a></li>
  </ul>
  <p>
    A Service of the National Cancer Institute<br />
    <img src="/ncitbrowser/images/external-footer-logos.gif"
      alt="External Footer Logos" width="238" height="34" border="0"
      usemap="#external-footer" />
  </p>
  <map id="external-footer" name="external-footer">
    <area shape="rect" coords="0,0,46,34"
      href="http://www.cancer.gov" target="_blank"
      alt="National Cancer Institute" />
    <area shape="rect" coords="55,1,99,32"
      href="http://www.hhs.gov/" target="_blank"
      alt="U.S. Health &amp; Human Services" />
    <area shape="rect" coords="103,1,147,31"
      href="http://www.nih.gov/" target="_blank"
      alt="National Institutes of Health" />
    <area shape="rect" coords="148,1,235,33"
      href="http://www.usa.gov/" target="_blank"
      alt="USA.gov" />
  </map>
</div>
<!-- end footer -->
</div>


      </div> <!-- pagecontent -->
    </div> <!--  main-area -->
    <div class="mainbox-bottom"><img src="/ncitbrowser/images/mainbox-bottom.gif" width="745" height="5" alt="Mainbox Bottom" /></div>

  </div> <!-- center-page -->

</body>
</html>

