<%@ include file="/pages/templates/search_hierarchy_header.html"%>

    function searchTree(ontology_node_id, ontology_display_name) {

        var handleBuildTreeSuccess = function(o) {
      	var tsTotalStart = getTimeStamp();

        var respTxt = o.responseText;
        var tsEvalStart = getTimeStamp();
        var respObj = eval('(' + respTxt + ')');
        var tsEvalTotal = getTimeStamp() - tsEvalStart;
        if ( typeof(respObj) != "undefined") {
        
          if ( typeof(respObj.dummy_root_nodes) != "undefined") {
              showNodeNotFound(ontology_node_id);
          }
            
          else if ( typeof(respObj.root_nodes) != "undefined") {
            var root = tree.getRoot();
            if (respObj.root_nodes.length == 0) {
              showEmptyRoot();
            }
            else {
              showPartialHierarchy("");
              showConstructingTreeStatus();

              for (var i=0; i < respObj.root_nodes.length; i++) {
                var nodeInfo = respObj.root_nodes[i];
                //var expand = false;
                addTreeBranch(ontology_node_id, root, nodeInfo);
              }
            }
          }
        }
        resetTreeStatus();
        var tsTotal = getTimeStamp() - tsTotalStart;
      }
       
<%@ include file="/pages/templates/search_hierarchy_footer.html"%>       
