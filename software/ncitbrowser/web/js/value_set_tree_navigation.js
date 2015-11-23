function doc_keyUp(e) {

//document.getElementById("mytext").focus();

//left arrow  37  
//up arrow  38  
//right arrow  39  
//down arrow  40  

//http://www.telerik.com/help/silverlight/radtreeview-feautres-treeviewitem-expanding-and-collapsing-items.html

    if (e.keyCode == 13) { // subtract
            if (document.activeElement) {
                var id = document.activeElement.id;
	        if (document.getElementById(id) != null) {
		    simulate(document.getElementById(id), "click");
	        }
	    }

    } else if (e.shiftKey && e.keyCode == 189) { // subtract
            if (document.activeElement) {
                var id = document.activeElement.id;
                
                
                var n = id.indexOf("N_");
                if (n == 0) {
                    var img_id = "IMG_" + id;
                    
                    if (document.getElementById(img_id).getAttribute('src') == "/tree/images/minus.gif") {
                        return;
                    }

                    
                    if (document.getElementById(img_id) != null) {
                        simulate(document.getElementById(img_id), "click");
                    }
                } 
            }

    } else if (e.shiftKey && e.keyCode == 187) {
            if (document.activeElement) {
                var id = document.activeElement.id;
               
                var n = id.indexOf("N_");
                if (n == 0) {
                    var img_id = "IMG_" + id;

                    if (document.getElementById(img_id).getAttribute('src') == "/tree/images/plus.gif") {
                        return;
                    }
                    
                    //alert(document.getElementById(img_id).getAttribute('src'));     
                    
                    if (document.getElementById(img_id) != null) {
                        simulate(document.getElementById(img_id), "click");
                    }
                }
            }
            
    // this would test for whichever key is 40 and the ctrl key at the same time
    } else if (e.ctrlKey && e.keyCode == 40) {
            if (document.activeElement) {
                //alert(document.activeElement.id);
                simulate(document.getElementById(document.activeElement.id), "click");
            }

    } else if (e.keyCode == 36) {
            if (document.activeElement) {
                //alert("home key pressed -- id: " + document.activeElement.id);
                //simulate(document.getElementById(document.activeElement.id), "click");
            }

    } else if (e.keyCode == 37) { // left arrow
            if (document.activeElement) {
                var curr_id = document.activeElement.id;
		var n = curr_id.lastIndexOf("_");
		if (n != -1) {
		    var prefix = curr_id.substring(0, n);
		    var node_num = curr_id.substring(n+1, curr_id.length);
		    var prev_node_num = parseInt(node_num) - 1;
 		    var prev_node_num_str = prev_node_num.toString(); 
 		    var prev_sibling = prefix + "_" + prev_node_num_str;
 		    if (document.getElementById(prev_sibling) != null) {
 		        document.getElementById(prev_sibling).focus();
 		    } else if (document.getElementById(prefix) != null) {
                        document.getElementById(prefix).focus();
		    }
                }         
            } 
            
    } else if (e.keyCode == 39) {  // right arrow
            if (document.activeElement) {
                var curr_id = document.activeElement.id;
		var n = curr_id.lastIndexOf("_");
		if (n != -1) {
		    var prefix = curr_id.substring(0, n);
		    var node_num = curr_id.substring(n+1, curr_id.length);
		    var next_node_num = parseInt(node_num) + 1;
 		    var next_node_num_str = next_node_num.toString(); 
 		    var next_sibling = prefix + "_" + next_node_num_str;
 		    if (document.getElementById(next_sibling) != null) {
 		        document.getElementById(next_sibling).focus();
 		    } else if (document.getElementById(prefix) != null) {
 		        var m = prefix.lastIndexOf("_");
 		        var next_prefix = prefix.substring(0, m);
 		        node_num = prefix.substring(m+1, prefix.length);
			next_node_num = parseInt(node_num) + 1;
			next_node_num_str = next_node_num.toString(); 
			var next_parent = next_prefix + "_" + next_node_num_str; 		        
 		        if (document.getElementById(next_parent) != null) {
                            document.getElementById(next_parent).focus();
                        }
		    }
                }       		    
            }             
            
            
    }}
// register the handler
document.addEventListener('keyup', doc_keyUp, false);


 			function updateChildNodeStatus(id) {
 				var prefix = id.concat("_");
 				var child_cnt = 1;
 				child_id = prefix.concat(child_cnt.toString());
 				while (document.getElementById(child_id) != null) {
 					child_cnt++;
 					child_id = prefix.concat(child_cnt.toString());
 				}
 				child_cnt = child_cnt - 1;
 				if (child_cnt == 0) {
 				    return;
 				}

 				child_cnt = 1;
 				child_id = prefix.concat(child_cnt.toString());
 				while (document.getElementById(child_id) != null) {
 					document.getElementById(child_id).checked = document.getElementById(id).checked;
 					updateChildNodeStatus(child_id);
 					child_cnt++;
 					child_id = prefix.concat(child_cnt.toString());
 				}
 			}



 			function uncheckParentNodeStatus(id, status) {
 				var n = id.lastIndexOf("_");
 				if (n != -1) {
 					var parent_id = id.substring(0, n);
 					if (document.getElementById(parent_id) != null) {
 						document.getElementById(parent_id).checked = status;
 						uncheckParentNodeStatus(parent_id, status);
 					}
 				}
 			}


 			function updateParentNodeStatus(id) {
 				var n = id.lastIndexOf("_");
 				if (n != -1) {
 					var parent_id = id.substring(0, n);
 					var prefix = parent_id.concat("_");
 					if (document.getElementById(parent_id) != null) {
 						var child_cnt = 1;
 						child_id = prefix.concat(child_cnt.toString());
 						while (document.getElementById(child_id) != null) {
 							child_cnt++;
 							child_id = prefix.concat(child_cnt.toString());
 						}
 						child_cnt = child_cnt - 1;
 						if (child_cnt == 0) return;
 						child_cnt = 1;
 						child_id = prefix.concat(child_cnt.toString());

 						var has_unchecked_sub = false;
 						var knt = 0;
 						while (document.getElementById(child_id) != null) {
 							if (!document.getElementById(child_id).checked) {
 								has_unchecked_sub = true;
 								//break;
 							} else {
 							    knt++;
 							}
 							child_cnt++;
 							child_id = prefix.concat(child_cnt.toString());
 						}
 						child_cnt = child_cnt - 1;

 						if (has_unchecked_sub == true && document.getElementById(parent_id).checked) {
 						    document.getElementById(parent_id).checked = !document.getElementById(parent_id).checked;
 						} else if (knt == child_cnt) {
 						    child_id = prefix.concat("1");
 						    document.getElementById(parent_id).checked = document.getElementById(child_id).checked;
 						}
 						updateParentNodeStatus(parent_id);
 					}
 				}
 			}

 			function updateCheckbox(id) {
 			    updateChildNodeStatus(id);
 			    updateParentNodeStatus(id);
 			    document.getElementById(parent_id).checked = !document.getElementById(parent_id).checked;
 			}



 			function changeImage(img_id) {
 				 var img_obj = document.getElementById(img_id);
 				 if (img_obj.getAttribute("src").indexOf("minus") != -1) {
 				     var s = img_obj.getAttribute("src");
 				     s = s.replace("minus", "plus");
 				     img_obj.setAttribute("src", s); 					
 					
 				 } else if (img_obj.getAttribute("src").indexOf("plus") != -1) {
 				     var s = img_obj.getAttribute("src");
 				     s = s.replace("plus", "minus");
 				     img_obj.setAttribute("src", s);
 				 }
                        }
                        
                        


 		    function show_hide(div_id) {
 		        var img_id = "IMG_" + div_id.substring(4, div_id.length);
 		        var img_obj = document.getElementById(img_id);
 				if (img_obj.getAttribute("src").indexOf("minus") != -1) {
 					document.getElementById(div_id).style.display = "none";
 				} else if (img_obj.getAttribute("src").indexOf("plus") != -1) {
 					document.getElementById(div_id).style.display = "block";
 				}
 		        changeImage(img_id);
 		    }

 		    function show(div_id) {
 		        var img_id = "IMG_" + div_id.substring(4, div_id.length);
 		        var img_obj = document.getElementById(img_id);
 				if (img_obj.getAttribute("src").indexOf("plus") != -1) {
 					document.getElementById(div_id).style.display = "block";
 					changeImage(img_id);
 				}
 		    }

 		    function hide(div_id) {
 		        var img_id = "IMG_" + div_id.substring(4, div_id.length);
 		        var img_obj = document.getElementById(img_id);
 				if (img_obj.getAttribute("src").indexOf("minus") != -1) {
 					document.getElementById(div_id).style.display = "none";
 					changeImage(img_id);
 				}
 		    }

 			function select_all() {
 			    var id = "N";
 				var prefix = id.concat("_");
 				var child_cnt = 1;
 				child_id = prefix.concat(child_cnt.toString());
 				while (document.getElementById(child_id) != null) {
 				    if (!document.getElementById(child_id).checked) {
 				        document.getElementById(child_id).checked = !document.getElementById(child_id).checked;
                                        updateChildNodeStatus(child_id);
 				    }
 				    child_cnt++;
 				    child_id = prefix.concat(child_cnt.toString());
 				}
 			}

 			function select_none() {
 			    select_all();
 			    var id = "N";
 				var prefix = id.concat("_");
 				var child_cnt = 1;
 				child_id = prefix.concat(child_cnt.toString());
 				while (document.getElementById(child_id) != null) {
 				    if (document.getElementById(child_id).checked) {
 				        document.getElementById(child_id).checked = !document.getElementById(child_id).checked;
                         updateChildNodeStatus(child_id);
 				    }
 					child_cnt++;
 					child_id = prefix.concat(child_cnt.toString());
 				}
 			}

             function get_selection() {
                 var checkedNodes = get_checked_nodes("N");
                 if (checkedNodes.length > 0) {
                     checkedNodes = checkedNodes.substring(1, checkedNodes.length);
                 }
                 alert(checkedNodes);
             }

        		function get_checked_nodes(id) {
                                var checkedNodes = "";
 				var child_checkedNodes = "";

 				if (document.getElementById(id) != null && document.getElementById(id).checked) {
 				    checkedNodes = checkedNodes.concat("|");
 					checkedNodes = checkedNodes.concat(id);
 				}

 				var prefix = id.concat("_");
 				var child_cnt = 1;
 				child_id = prefix.concat(child_cnt.toString());

 				while (document.getElementById(child_id) != null) {
 					child_checkedNodes = get_checked_nodes(child_id);
 					if (child_checkedNodes.length > 0) {
 						checkedNodes = checkedNodes.concat(child_checkedNodes);
 					}
 					child_cnt++;
 					child_id = prefix.concat(child_cnt.toString());
 				}
 				return checkedNodes;
 			}


 			function expand_all() {
 			    var id = "DIV_N";
			    var prefix = id.concat("_");
			    var child_cnt = 1;
			    var child_id = prefix.concat(child_cnt.toString());
			    while (document.getElementById(child_id) != null) {
				show(child_id);
				child_cnt++;
				child_id = prefix.concat(child_cnt.toString());
			    }
			}
			

 			function collapse_all() {
				var divTags = document.getElementsByTagName('div');
				for (var i=0;i<divTags.length;i++) {
				    if (divTags[i].id.indexOf("DIV_N_") == 0) {
					var num = divTags[i].id.substring(6, divTags[i].id.length); 
					if (num.indexOf("_") == -1) {
					    hide(divTags[i].id);
					}
				    }
				}			
 			}

                        function onValueSetNodeClicked(node_id) {
				var url="/ncitbrowser/ajax?action=create_src_vs_tree&vsd_uri=" + node_id;
				window.location.href = url;
				return false ;
			}


                        function getCheckedVocabularies() {
                        alert("getCheckedVocabularies");
                        
			    var inputCheckBox = document.getElementsByTagName("input"); 
			    var checked_vocabularies = "";
			    var knt = 0;
			    for(var i=0; i<inputCheckBox.length; i++){  
			        if (document.getElementById(inputCheckBox[i]).checked) {
			            knt++;
			            if (knt > 1) {
			                checked_vocabularies.append(",");
			            }
			            alert(inputCheckBox[i].id);
			            
			            checked_vocabularies.append(inputCheckBox[i].id);
			        }
			    }
			    alert(checked_vocabularies);
			}

