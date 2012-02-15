
<script type="text/javascript" src="/ncitbrowser/js/yui/yahoo-min.js" ></script>
<script type="text/javascript" src="/ncitbrowser/js/yui/event-min.js" ></script>
<script type="text/javascript" src="/ncitbrowser/js/yui/dom-min.js" ></script>
<script type="text/javascript" src="/ncitbrowser/js/yui/animation-min.js" ></script>
<script type="text/javascript" src="/ncitbrowser/js/yui/container-min.js" ></script>
<script type="text/javascript" src="/ncitbrowser/js/yui/connection-min.js" ></script>
<script type="text/javascript" src="/ncitbrowser/js/yui/treeview-min.js" ></script>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
  <head>
  <title>Vocabulary Hierarchy</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <link rel="stylesheet" type="text/css" href="/ncitbrowser/css/styleSheet.css" />
  <link rel="shortcut icon" href="/ncitbrowser/favicon.ico" type="image/x-icon" />
  <link rel="stylesheet" type="text/css" href="/ncitbrowser/css/yui/fonts.css" />
  <link rel="stylesheet" type="text/css" href="/ncitbrowser/css/yui/grids.css" />
  <link rel="stylesheet" type="text/css" href="/ncitbrowser/css/yui/code.css" />
  <link rel="stylesheet" type="text/css" href="/ncitbrowser/css/yui/tree.css" />
  <script type="text/javascript" src="/ncitbrowser/js/script.js"></script>

  <script language="JavaScript">

    var tree;
    var nodeIndex;
    var rootDescDiv;
    var emptyRootDiv;
    var treeStatusDiv;
    var nodes = [];

    function load(url,target) {
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
        alert('responseFailure: ' + o.statusText);
      }

      var buildTreeCallback =
      {
        success:handleBuildTreeSuccess,
        failure:handleBuildTreeFailure
      };

      if (ontology_display_name!='') {
        resetEmptyRoot();

        showTreeLoadingStatus();
        var ontology_source = null;
        var ontology_version = document.forms["pg_form"].ontology_version.value;
        var request = YAHOO.util.Connect.asyncRequest('GET','/ncitbrowser/ajax?action=build_tree&ontology_node_id=' +ontology_node_id+'&ontology_display_name='+ontology_display_name+'&version='+ontology_version+'&ontology_source='+ontology_source,buildTreeCallback);
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
        alert('responseFailure: ' + o.statusText);
      }

      var resetTreeCallback =
      {
        success:handleResetTreeSuccess,
        failure:handleResetTreeFailure
      };
      if (ontology_node_id!= '') {
        showTreeLoadingStatus();
        var ontology_source = null;
        var ontology_version = document.forms["pg_form"].ontology_version.value;
        var request = YAHOO.util.Connect.asyncRequest('GET','/ncitbrowser/ajax?action=reset_tree&ontology_node_id=' +ontology_node_id+'&ontology_display_name='+ontology_display_name + '&version='+ ontology_version +'&ontology_source='+ontology_source,resetTreeCallback);
      }
    }

    function onClickTreeNode(ontology_node_id) {
      var ontology_display_name = document.forms["pg_form"].ontology_display_name.value;
      var ontology_version = document.forms["pg_form"].ontology_version.value;
      load('/ncitbrowser/ConceptReport.jsp?dictionary='+ ontology_display_name + '&version='+ ontology_version  + '&code=' + ontology_node_id,top.opener);
    }

    function onClickViewEntireOntology(ontology_display_name) {
      var ontology_display_name = document.pg_form.ontology_display_name.value;
      tree = new YAHOO.widget.TreeView("treecontainer");
      tree.draw();
      resetRootDesc();
      buildTree('', ontology_display_name);
    }

    function initTree() {

      tree = new YAHOO.widget.TreeView("treecontainer");
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

    function showNodeNotFound(node_id) {
      //emptyRootDiv.setBody("<span class='instruction_text'>Concept with code " + node_id + " not found in the hierarchy.</span>");
      emptyRootDiv.setBody("<span class='instruction_text'>Concept not part of the parent-child hierarchy in this source; check other relationships.</span>");
      emptyRootDiv.show();
      emptyRootDiv.render();
    }
    
    function showPartialHierarchy() {
      rootDescDiv.setBody("<span class='instruction_text'>(Note: This tree only shows partial hierarchy.)</span>");
      rootDescDiv.show();
      rootDescDiv.render();
    }

    function showTreeLoadingStatus() {
      treeStatusDiv.setBody("<img src='/ncitbrowser/images/loading.gif'/> <span class='instruction_text'>Building tree ...</span>");
      treeStatusDiv.show();
      treeStatusDiv.render();
    }

    function showTreeDrawingStatus() {
      treeStatusDiv.setBody("<img src='/ncitbrowser/images/loading.gif'/> <span class='instruction_text'>Drawing tree ...</span>");
      treeStatusDiv.show();
      treeStatusDiv.render();
    }

    function showSearchingTreeStatus() {
      treeStatusDiv.setBody("<img src='/ncitbrowser/images/loading.gif'/> <span class='instruction_text'>Searching tree... Please wait.</span>");
      treeStatusDiv.show();
      treeStatusDiv.render();
    }

    function showConstructingTreeStatus() {
      treeStatusDiv.setBody("<img src='/ncitbrowser/images/loading.gif'/> <span class='instruction_text'>Constructing tree... Please wait.</span>");
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
        alert('responseFailure: ' + o.statusText);
      }

      var callback =
      {
        success:responseSuccess,
        failure:responseFailure
      };

      var ontology_display_name = document.forms["pg_form"].ontology_display_name.value;
      var ontology_version = document.forms["pg_form"].ontology_version.value;
      var cObj = YAHOO.util.Connect.asyncRequest('GET','/ncitbrowser/ajax?action=expand_tree&ontology_node_id=' +id+'&ontology_display_name='+ontology_display_name+'&version='+ontology_version,callback);
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

      var root = tree.getRoot();
      var newNodeDetails = "";
      var newNodeData = "";

      newNodeDetails = "javascript:onClickTreeNode('C12219');";
      newNodeData = { label:"Anatomic Structure, System, or Substance", id:"C12219", href:newNodeDetails };
      var N_C12219 = new YAHOO.widget.TextNode(newNodeData, root, true);
      N_C12219.isLeaf = false;
      N_C12219.setDynamicLoad(loadNodeData);

      newNodeDetails = "javascript:onClickTreeNode('C32221');";
      newNodeData = { label:"Body Part", id:"C32221", href:newNodeDetails };
      var N_C32221 = new YAHOO.widget.TextNode(newNodeData, N_C12219, true);
      N_C32221.isLeaf = false;
      N_C32221.setDynamicLoad(loadNodeData);

      newNodeDetails = "javascript:onClickTreeNode('C38630');";
      newNodeData = { label:"Extremity Part", id:"C38630", href:newNodeDetails };
      var N_C38630 = new YAHOO.widget.TextNode(newNodeData, N_C32221, true);
      N_C38630.isLeaf = false;
      N_C38630.setDynamicLoad(loadNodeData);

      newNodeDetails = "javascript:onClickTreeNode('C40186');";
      newNodeData = { label:"Digit", id:"C40186", href:newNodeDetails };
      var N_C40186 = new YAHOO.widget.TextNode(newNodeData, N_C38630, true);
      N_C40186.isLeaf = false;
      N_C40186.setDynamicLoad(loadNodeData);

      newNodeDetails = "javascript:onClickTreeNode('C32608');";
      newNodeData = { label:"Finger", id:"C32608", href:newNodeDetails };
      var N_C32608 = new YAHOO.widget.TextNode(newNodeData, N_C40186, true);
      N_C32608.isLeaf = false;
      N_C32608.setDynamicLoad(loadNodeData);
      N_C32608.labelStyle = "ygtvlabel_highlight";

      newNodeDetails = "javascript:onClickTreeNode('C38628');";
      newNodeData = { label:"Upper Extremity Part", id:"C38628", href:newNodeDetails };
      var N_C38628 = new YAHOO.widget.TextNode(newNodeData, N_C38630, true);
      N_C38628.isLeaf = false;
      N_C38628.setDynamicLoad(loadNodeData);

      newNodeDetails = "javascript:onClickTreeNode('C32608');";
      newNodeData = { label:"Finger", id:"C32608", href:newNodeDetails };
      var N_C32608 = new YAHOO.widget.TextNode(newNodeData, N_C38628, true);
      N_C32608.isLeaf = false;
      N_C32608.setDynamicLoad(loadNodeData);
      N_C32608.labelStyle = "ygtvlabel_highlight";

      newNodeDetails = "javascript:onClickTreeNode('C12913');";
      newNodeData = { label:"Abnormal Cell", id:"C12913", href:newNodeDetails };
      var N_C12913 = new YAHOO.widget.TextNode(newNodeData, root, false);
      N_C12913.isLeaf = false;
      N_C12913.setDynamicLoad(loadNodeData);

      newNodeDetails = "javascript:onClickTreeNode('C43431');";
      newNodeData = { label:"Activity", id:"C43431", href:newNodeDetails };
      var N_C43431 = new YAHOO.widget.TextNode(newNodeData, root, false);
      N_C43431.isLeaf = false;
      N_C43431.setDynamicLoad(loadNodeData);

      newNodeDetails = "javascript:onClickTreeNode('C20633');";
      newNodeData = { label:"Biochemical Pathway", id:"C20633", href:newNodeDetails };
      var N_C20633 = new YAHOO.widget.TextNode(newNodeData, root, false);
      N_C20633.isLeaf = false;
      N_C20633.setDynamicLoad(loadNodeData);

      newNodeDetails = "javascript:onClickTreeNode('C17828');";
      newNodeData = { label:"Biological Process", id:"C17828", href:newNodeDetails };
      var N_C17828 = new YAHOO.widget.TextNode(newNodeData, root, false);
      N_C17828.isLeaf = false;
      N_C17828.setDynamicLoad(loadNodeData);

      newNodeDetails = "javascript:onClickTreeNode('C12218');";
      newNodeData = { label:"Chemotherapy Regimen or Agent Combination", id:"C12218", href:newNodeDetails };
      var N_C12218 = new YAHOO.widget.TextNode(newNodeData, root, false);
      N_C12218.isLeaf = false;
      N_C12218.setDynamicLoad(loadNodeData);

      newNodeDetails = "javascript:onClickTreeNode('C20181');";
      newNodeData = { label:"Conceptual Entity", id:"C20181", href:newNodeDetails };
      var N_C20181 = new YAHOO.widget.TextNode(newNodeData, root, false);
      N_C20181.isLeaf = false;
      N_C20181.setDynamicLoad(loadNodeData);

      newNodeDetails = "javascript:onClickTreeNode('C20047');";
      newNodeData = { label:"Diagnostic or Prognostic Factor", id:"C20047", href:newNodeDetails };
      var N_C20047 = new YAHOO.widget.TextNode(newNodeData, root, false);
      N_C20047.isLeaf = false;
      N_C20047.setDynamicLoad(loadNodeData);

      newNodeDetails = "javascript:onClickTreeNode('C7057');";
      newNodeData = { label:"Disease, Disorder or Finding", id:"C7057", href:newNodeDetails };
      var N_C7057 = new YAHOO.widget.TextNode(newNodeData, root, false);
      N_C7057.isLeaf = false;
      N_C7057.setDynamicLoad(loadNodeData);

      newNodeDetails = "javascript:onClickTreeNode('C1908');";
      newNodeData = { label:"Drug, Food, Chemical or Biomedical Material", id:"C1908", href:newNodeDetails };
      var N_C1908 = new YAHOO.widget.TextNode(newNodeData, root, false);
      N_C1908.isLeaf = false;
      N_C1908.setDynamicLoad(loadNodeData);

      newNodeDetails = "javascript:onClickTreeNode('C22188');";
      newNodeData = { label:"Experimental Organism Anatomical Concept", id:"C22188", href:newNodeDetails };
      var N_C22188 = new YAHOO.widget.TextNode(newNodeData, root, false);
      N_C22188.isLeaf = false;
      N_C22188.setDynamicLoad(loadNodeData);

      newNodeDetails = "javascript:onClickTreeNode('C22187');";
      newNodeData = { label:"Experimental Organism Diagnosis", id:"C22187", href:newNodeDetails };
      var N_C22187 = new YAHOO.widget.TextNode(newNodeData, root, false);
      N_C22187.isLeaf = false;
      N_C22187.setDynamicLoad(loadNodeData);

      newNodeDetails = "javascript:onClickTreeNode('C16612');";
      newNodeData = { label:"Gene", id:"C16612", href:newNodeDetails };
      var N_C16612 = new YAHOO.widget.TextNode(newNodeData, root, false);
      N_C16612.isLeaf = false;
      N_C16612.setDynamicLoad(loadNodeData);

      newNodeDetails = "javascript:onClickTreeNode('C26548');";
      newNodeData = { label:"Gene Product", id:"C26548", href:newNodeDetails };
      var N_C26548 = new YAHOO.widget.TextNode(newNodeData, root, false);
      N_C26548.isLeaf = false;
      N_C26548.setDynamicLoad(loadNodeData);

      newNodeDetails = "javascript:onClickTreeNode('C97325');";
      newNodeData = { label:"Manufactured Object", id:"C97325", href:newNodeDetails };
      var N_C97325 = new YAHOO.widget.TextNode(newNodeData, root, false);
      N_C97325.isLeaf = false;
      N_C97325.setDynamicLoad(loadNodeData);

      newNodeDetails = "javascript:onClickTreeNode('C3910');";
      newNodeData = { label:"Molecular Abnormality", id:"C3910", href:newNodeDetails };
      var N_C3910 = new YAHOO.widget.TextNode(newNodeData, root, false);
      N_C3910.isLeaf = false;
      N_C3910.setDynamicLoad(loadNodeData);

      newNodeDetails = "javascript:onClickTreeNode('C28389');";
      newNodeData = { label:"NCI Administrative Concept", id:"C28389", href:newNodeDetails };
      var N_C28389 = new YAHOO.widget.TextNode(newNodeData, root, false);
      N_C28389.isLeaf = false;
      N_C28389.setDynamicLoad(loadNodeData);

      newNodeDetails = "javascript:onClickTreeNode('C14250');";
      newNodeData = { label:"Organism", id:"C14250", href:newNodeDetails };
      var N_C14250 = new YAHOO.widget.TextNode(newNodeData, root, false);
      N_C14250.isLeaf = false;
      N_C14250.setDynamicLoad(loadNodeData);

      newNodeDetails = "javascript:onClickTreeNode('C20189');";
      newNodeData = { label:"Property or Attribute", id:"C20189", href:newNodeDetails };
      var N_C20189 = new YAHOO.widget.TextNode(newNodeData, root, false);
      N_C20189.isLeaf = false;
      N_C20189.setDynamicLoad(loadNodeData);

      newNodeDetails = "javascript:onClickTreeNode('C28428');";
      newNodeData = { label:"Retired Concept", id:"C28428", href:newNodeDetails };
      var N_C28428 = new YAHOO.widget.TextNode(newNodeData, root, false);
      N_C28428.isLeaf = false;
      N_C28428.setDynamicLoad(loadNodeData);
             tree.draw();
    }


    function addTreeBranch(ontology_node_id, rootNode, nodeInfo) {
      var newNodeDetails = "javascript:onClickTreeNode('" + nodeInfo.ontology_node_id + "');";
      var newNodeData = { label:nodeInfo.ontology_node_name, id:nodeInfo.ontology_node_id, href:newNodeDetails };

      var expand = false;
      var childNodes = nodeInfo.children_nodes;

      if (childNodes.length > 0) {
          expand = true;
      }
      var newNode = new YAHOO.widget.TextNode(newNodeData, rootNode, expand);
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

  </script>
</head>
<body>
  
    <!-- Begin Skip Top Navigation -->
      <a href="#evs-content" class="hideLink" accesskey="1" title="Skip repetitive navigation links">skip navigation links</A>
    <!-- End Skip Top Navigation --> 
    <div id="popupContainer">
      <!-- nci popup banner -->
      <div class="ncipopupbanner">
        <a href="http://www.cancer.gov" target="_blank" alt="National Cancer Institute"><img src="/ncitbrowser/images/nci-banner-1.gif" width="440" height="39" border="0" alt="National Cancer Institute" /></a>
        <a href="http://www.cancer.gov" target="_blank" alt="National Cancer Institute"><img src="/ncitbrowser/images/spacer.gif" width="48" height="39" border="0" alt="National Cancer Institute" class="print-header" /></a>
      </div>
      <!-- end nci popup banner -->
      <div id="popupMainArea">
        <a name="evs-content" id="evs-content"></a>
        <table class="evsLogoBg" cellspacing="0" cellpadding="0" border="0">
        <tr>
          <td valign="top">
            <a href="http://evs.nci.nih.gov/" target="_blank" alt="Enterprise Vocabulary Services">
              <img src="/ncitbrowser/images/evs-popup-logo.gif" width="213" height="26" alt="EVS: Enterprise Vocabulary Services" title="EVS: Enterprise Vocabulary Services" border="0" />
            </a>
          </td>
          <td valign="top"><div id="closeWindow"><a href="javascript:window.close();"><img src="/ncitbrowser/images/thesaurus_close_icon.gif" width="10" height="10" border="0" alt="Close Window" />&nbsp;CLOSE WINDOW</a></div></td>
        </tr>
        </table>


    <div>
      <img src="/ncitbrowser/images/thesaurus_popup_banner.gif" width="612" height="56" alt="NCI Thesaurus" title="" border="0" />
      
     
             <span class="texttitle-blue-rightjust-2">11.09d (Release date: 2011-09-26-08:00)</span>
      

    </div>

        <div id="popupContentArea">
          <table width="580px" cellpadding="3" cellspacing="0" border="0">
            <tr class="textbody">
              <td class="pageTitle" align="left">
                NCI Thesaurus Hierarchy
              </td>
              <td class="pageTitle" align="right">
                <font size="1" color="red" align="right">
                  <a href="javascript:printPage()"><img src="/ncitbrowser/images/printer.bmp" border="0" alt="Send to Printer"><i>Send to Printer</i></a>
                </font>
              </td>
            </tr>
          </table>
          <!-- Tree content -->
          <div id="rootDesc">
            <div id="bd"></div>
            <div id="ft"></div>
          </div>
          <div id="treeStatus">
            <div id="bd"></div>
          </div>
          <div id="emptyRoot">
            <div id="bd"></div>
          </div>
          <div id="treecontainer"></div>

          <form id="pg_form">
            
            <input type="hidden" id="ontology_node_id" name="ontology_node_id" value="C32608" />
            <input type="hidden" id="ontology_display_name" name="ontology_display_name" value="NCI Thesaurus" />
            <input type="hidden" id="ontology_version" name="ontology_version" value="11.09d" />

          </form>
          <!-- End of Tree control content -->
        </div>
      </div>
    </div>
  
</body>
</html>
