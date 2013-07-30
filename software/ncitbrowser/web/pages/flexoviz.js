// actions for interacting with the graph

// gets the Flex application
function getApp() {
	if (navigator.appName.indexOf ("Microsoft") != -1) {
		app = window["FlexoViz"];
		if (app == null) {
			app = window["BasicFlexoViz"];
		}
	} else {
		app = document["FlexoViz"];
		if (app == null) {
			app = document["BasicFlexoViz"];
		}
	}
	if (app == null) {
		app = document.getElementById("FlexoViz");
		if (app == null) {
			app = document.getElementById("BasicFlexoViz");
		}
	}
	if (app == null) {
		alert("Could not get Flash object, JavaScript/Flex communication failed.");
	}
	return app;
}

/** 
 * Selects on the node with the given id
 * @param id the id of the node to focus on
 */
function selectNodeByID(id) {
	var app = getApp();
	if (app && app.selectNodeByID) {
		app.selectNodeByID(id);
	} else if (app != null) {
		alert("Couldn't get the Flex selectNodeByID() function");
	}
}

/** 
 * Focusses on the node with the given id
 * @param id the id of the node to focus on
 * @param option the graph view setting (optional), one of "Neighborhood", "Hierarchy To Root", "Parents", or "Children"
 *		defaults to "Neighborhood"
 */
function searchByID(id, option) {
	var app = getApp();
	if (app && app.searchByID) {
		app.searchByID(id, option);
	} else if (app != null) {
		alert("Couldn't get the Flex searchByID() function");
	}
}

/** 
 * Performs a search on the given text and shows
 * the appropriate graph - either the neighborhood, or the hierarchy to root
 * @param searchText the text to search for
 * @param option the graph view setting (optional), one of "Neighborhood", "Hierarchy To Root", "Parents", or "Children"
 *		defaults to "Neighborhood"
 */
function searchByName(searchText, option) {
	var app = getApp();
	if (app && app.searchByName) {
		app.searchByName(searchText, option);
	} else if (app != null) {
		alert("Couldn't get the Flex searchByName() function");
	}
}

/** 
 * Returns the id of the selected node.  If no nodes are selected then null is returned.
 */
function getSelectedNodeID() {
	var selID = null;
	var app = getApp();
	if (app && app.getSelectedNodeID) {
		selID = app.getSelectedNodeID();
	} else if (app != null) {
		alert("Couldn't get the Flex getSelectedNodeID() function");
	}
	return selID;
}

/** 
 * Returns the name of the selected node.  If no nodes are selected then null is returned.
 */
function getSelectedNodeName() {
	var selName = null;
	var app = getApp();
	if (app && app.getSelectedNodeName) {
		selName = app.getSelectedNodeName();
	} else if (app != null) {
		alert("Couldn't get the Flex getSelectedNodeName() function");
	}
	return selName;
}

/** 
 * Returns an array of the ids of the selected nodes.  If no nodes are selected then an empty
 * array is returned.  
 */
function getSelectedNodeIDs() {
	var selIDs = new Array();
	var app = getApp();
	if (app && app.getSelectedNodeIDs) {
		selIDs = app.getSelectedNodeIDs();
	} else if (app != null) {
		alert("Couldn't get the Flex getSelectedNodeIDs() function");
	}
	return selIDs;
}

/** 
 * Returns an array of the names of the selected nodes.  If no nodes are selected then an empty
 * array is returned.  
 */
function getSelectedNodeNames() {
	var selNames = new Array();
	var app = getApp();
	if (app && app.getSelectedNodeNames) {
		selNames = app.getSelectedNodeNames();
	} else if (app != null) {
		alert("Couldn't get the Flex getSelectedNodeNames() function");
	}
	return selNames;
}

/**
 * The basic version of FlexViz calls this function when a node is double clicked.
 */
/*function flexVizNodeClicked(id, currentID) {
	alert("Node selected in flex: " + id + ", current id: " + currentID);
}*/

/**
 * Both versions of FlexViz call this function when the application has finished loading.
 * It passes in the id of the swf (this is the value of the "name" parameter).
 */ 
function flexVizLoaded(swfID) {
	//alert("FlexViz loaded: " + swfID);
	/*
	var app = getApp();
	if (app && app.addNodeMouseOverListener) {
		app.addNodeMouseOverListener("testJSHandler");
	} else if (app != null) {
		alert("Couldn't get the Flex addNodeMouseOverListener() function");
	}
	*/
}
/*
function testJSHandler(nodeID, nodeName, ontID, mx, my, swfID) {
	alert("JS: " + nodeID + ", " + nodeName+ ", ontID=" + ontID + ", swfID=" + swfID + 
		", [" + mx + "x" + my + "]");
}
*/
