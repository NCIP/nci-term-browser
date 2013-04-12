/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.utils;

import java.io.*;
import java.util.*;


import org.apache.log4j.*;
import gov.nih.nci.evs.browser.common.*;
/**
 * 
 */

/**
 * @author EVS Team
 * @version 1.0
 *
 *      Modification history Initial implementation kim.ong@ngc.com
 *
 */


public class ValueSetUtils {

	private static Logger _logger = Logger.getLogger(DataUtils.class);
	private static Random rand = new Random();

    public ValueSetUtils() {

	}

    public static void println(PrintWriter out, String text) {
        out.println(text);
    }

    public static Vector<String> parseData(String line) {
		if (line == null) return null;
        String tab = "|";
        return parseData(line, tab);
    }

    public static Vector<String> parseData(String line, String tab) {
		if (line == null) return null;
        Vector data_vec = new Vector();
        StringTokenizer st = new StringTokenizer(line, tab);
        while (st.hasMoreTokens()) {
            String value = st.nextToken();
            if (value.compareTo("null") == 0)
                value = " ";
            data_vec.add(value);
        }
        return data_vec;
    }


	private String generateRandomString() {
		int i = rand.nextInt();
		String t = new Integer(i).toString();
		t = t.replace("-", "n");
		return "_" + t;
	}


    private String generateID(TreeItem node) {
		String node_id = null;
		if (node == null) {
			node_id = "root";
		} else {
			node_id = "N_" + replaceNodeID(node._code);
		}
		return node_id;
	}



      private List<TreeItem> Move_NCIt_to_Top(List<TreeItem> children) {
		  List<TreeItem> new_children = new ArrayList<TreeItem>();

		  for (TreeItem childItem : children) {
			  if (childItem._text.compareTo("NCI Thesaurus") == 0) {
				  new_children.add(childItem);
			  }
		  }

		  for (TreeItem childItem : children) {
			  if (childItem._text.compareTo("NCI Thesaurus") != 0) {
				  new_children.add(childItem);
			  }
		  }
		  return new_children;
	  }




    public void printTree(PrintWriter out, TreeItem root) {
		System.out.println("printTree..");

		if (root == null) {
			System.out.println("(*) printTree aborted -- root is null???");
		}


		//TreeItem root = new TreeItem("<Root>", "Root node");
		for (String association : root._assocToChildMap.keySet()) {
			List<TreeItem> children = root._assocToChildMap.get(association);

			// Collections.sort(children);
			SortUtils.quickSort(children);

			//children = Move_NCIt_to_Top(children);

			for (TreeItem childItem : children) {
				String child_node_id = generateID(childItem);
				printTree(out, childItem, child_node_id, null, "root", 0);
			}
		}
	}


    public void printTree(PrintWriter out, TreeItem ti, String node_id, TreeItem parent, String parent_node_id, int depth) {
        printTreeNode(out, ti, node_id, parent, parent_node_id);
		for (String association : ti._assocToChildMap.keySet()) {
			List<TreeItem> children = ti._assocToChildMap.get(association);
			// Collections.sort(children);
			SortUtils.quickSort(children);
			for (TreeItem childItem : children) {
				String child_node_id = generateID(childItem);
				printTree(out, childItem, child_node_id, ti, node_id, depth+1);
			}
		}
	}



    public void printTree(PrintWriter out, TreeItem ti, String node_id, TreeItem parent, String parent_node_id, int depth, String dictionary) {
        printTreeNode(out, ti, node_id, parent, parent_node_id, dictionary);
		for (String association : ti._assocToChildMap.keySet()) {
			List<TreeItem> children = ti._assocToChildMap.get(association);
			// Collections.sort(children);
			SortUtils.quickSort(children);
			for (TreeItem childItem : children) {
				String child_node_id = generateID(childItem);
				printTree(out, childItem, child_node_id, ti, node_id, depth+1, dictionary);
			}
		}
	}




    private void printTreeNode(PrintWriter out, TreeItem node, String node_id, TreeItem parent, String parent_node_id) {
		if (node == null) return;

		try {
			String image = "[+]";
			boolean expandable = true;
			if (!node._expandable) {
				image = ".";
				expandable = false;
			}

			boolean expanded = expandable;

            String parent_code = null;
            if (parent != null) {
			    parent_code = parent._code;
			}


			String code = node._code;
			boolean isHasMoreNode = false;

			String node_label = node_id;//"N_" + replaceNodeID(code);
		    String node_name = node._text;

		    println(out, "");
		    println(out, "newNodeDetails = \"javascript:onClickTreeNode('" + code + "');\";");
		    println(out, "newNodeData = { label:\"" + node_name + "\", id:\"" + code + "\", href:newNodeDetails };");

		    if (expanded) {
			    println(out, "var " + node_label + " = new YAHOO.widget.TaskNode(newNodeData, " + parent_node_id + ", true);");

		    } else if (isHasMoreNode) {
			    println(out, "var " + node_label + " = new YAHOO.widget.TaskNode(newNodeData, " + parent_node_id + ", false);");
		    } else {
			    println(out, "var " + node_label + " = new YAHOO.widget.TaskNode(newNodeData, " + parent_node_id + ", false);");
		    }

		    if (expandable || isHasMoreNode) {
			    println(out, node_label + ".isLeaf = false;");
			    println(out, node_label + ".ontology_node_child_count = 1;");

		    } else {
				println(out, node_label + ".ontology_node_child_count = 0;");
			    println(out, node_label + ".isLeaf = true;");

		    }

		} catch (Exception ex) {

		}

    }



    private void printTreeNode(PrintWriter out, TreeItem node, String node_id, TreeItem parent, String parent_node_id, String dictionary) {
		if (node == null) return;

		try {
			String image = "[+]";
			boolean expandable = true;
			if (!node._expandable) {
				image = ".";
				expandable = false;
			}

			boolean expanded = expandable;

            String parent_code = null;
            if (parent != null) {
			    parent_code = parent._code;
			}


			String code = node._code;
			boolean isHasMoreNode = false;

			String node_label = node_id;//"N_" + replaceNodeID(code);
		    String node_name = node._text;

		    println(out, "");
		    println(out, "newNodeDetails = \"javascript:onClickTreeNode('" + code + "');\";");
		    println(out, "newNodeData = { label:\"" + node_name + "\", id:\"" + code + "\", href:newNodeDetails };");

		    if (expanded) {
			    println(out, "var " + node_label + " = new YAHOO.widget.TaskNode(newNodeData, " + parent_node_id + ", true);");

		    } else if (isHasMoreNode) {
			    println(out, "var " + node_label + " = new YAHOO.widget.TaskNode(newNodeData, " + parent_node_id + ", false);");
		    } else {
			    println(out, "var " + node_label + " = new YAHOO.widget.TaskNode(newNodeData, " + parent_node_id + ", false);");
		    }

		    if (expandable || isHasMoreNode) {
			    println(out, node_label + ".isLeaf = false;");
			    println(out, node_label + ".ontology_node_child_count = 1;");

		    } else {
				println(out, node_label + ".ontology_node_child_count = 0;");
			    println(out, node_label + ".isLeaf = true;");

		    }

		    if (node_name.compareTo(dictionary) == 0) {
				println(out, node_label + ".checked();");
			}


		} catch (Exception ex) {

		}

    }



//////////////////////////////////////////////////////////////////////
//[#31626] Change value set hierarchy node labels.
//    private static final int  STANDARD_VIEW = 1;
//    private static final int  TERMINOLOGY_VIEW = 2;

    public void printTree(PrintWriter out, TreeItem root, int view, String dictionary) {


System.out.println("(*) ValueSetUtils.printTree dictionary: " + dictionary);



		if (root == null) {
			System.out.println("(*) printTree aborted -- root is null???");
		}

		for (String association : root._assocToChildMap.keySet()) {
			List<TreeItem> children = root._assocToChildMap.get(association);
			SortUtils.quickSort(children);
			for (TreeItem childItem : children) {
				String scheme = childItem._text;
				System.out.println("scheme: " + scheme + "  dictionary: " + dictionary);
				if (scheme.compareTo(dictionary) == 0) {
					String child_node_id = generateID(childItem);
					printTree(out, childItem, child_node_id, null, "root", 0, view);
				}
			}
		}
	}




    public void printTree(PrintWriter out, TreeItem root, int view) {
		if (root == null) {
			System.out.println("(*) printTree aborted -- root is null???");
		}

		//TreeItem root = new TreeItem("<Root>", "Root node");
		for (String association : root._assocToChildMap.keySet()) {
			List<TreeItem> children = root._assocToChildMap.get(association);
			// Collections.sort(children);
			SortUtils.quickSort(children);

			if (view == Constants.TERMINOLOGY_VIEW) {
				children = Move_NCIt_to_Top(children);
			}


			for (TreeItem childItem : children) {
				String child_node_id = generateID(childItem);
				printTree(out, childItem, child_node_id, null, "root", 0, view);
			}
		}
	}


    public void printTree(PrintWriter out, TreeItem ti, String node_id, TreeItem parent, String parent_node_id, int depth, int view) {
        printTreeNode(out, ti, node_id, parent, parent_node_id, view);
		for (String association : ti._assocToChildMap.keySet()) {
			List<TreeItem> children = ti._assocToChildMap.get(association);
			// Collections.sort(children);
			SortUtils.quickSort(children);
			for (TreeItem childItem : children) {
				String child_node_id = generateID(childItem);
				printTree(out, childItem, child_node_id, ti, node_id, depth+1);
			}
		}
	}



    public void printTree(PrintWriter out, TreeItem ti, String node_id, TreeItem parent, String parent_node_id, int depth, int view, String dictionary) {
        printTreeNode(out, ti, node_id, parent, parent_node_id, view);
		for (String association : ti._assocToChildMap.keySet()) {
			List<TreeItem> children = ti._assocToChildMap.get(association);
			// Collections.sort(children);
			SortUtils.quickSort(children);
			for (TreeItem childItem : children) {
				String child_node_id = generateID(childItem);
				printTree(out, childItem, child_node_id, ti, node_id, depth+1);
			}
		}
	}



    private void printTreeNode(PrintWriter out, TreeItem node, String node_id, TreeItem parent, String parent_node_id, int view) {
		if (node == null) return;

		try {
			String image = "[+]";
			boolean expandable = true;
			if (!node._expandable) {
				image = ".";
				expandable = false;
			}

			boolean expanded = expandable;

            String parent_code = null;
            if (parent != null) {
			    parent_code = parent._code;
			}


			String code = node._code;
			boolean isHasMoreNode = false;

			String node_label = node_id;//"N_" + replaceNodeID(code);
		    String node_name = node._text;

		    println(out, "");
		    //KLO testing
		    if (view == Constants.STANDARD_VIEW && parent_node_id.compareTo("root") == 0) {
			    println(out, "newNodeData = { label:\"" + node_name + "\", id:\"" + code + "\"};");

			} else {
				println(out, "newNodeDetails = \"javascript:onClickTreeNode('" + code + "');\";");
			    println(out, "newNodeData = { label:\"" + node_name + "\", id:\"" + code + "\", href:newNodeDetails };");
			}

		    if (expanded) {
			    println(out, "var " + node_label + " = new YAHOO.widget.TaskNode(newNodeData, " + parent_node_id + ", true);");

		    } else if (isHasMoreNode) {
			    println(out, "var " + node_label + " = new YAHOO.widget.TaskNode(newNodeData, " + parent_node_id + ", false);");
		    } else {
			    println(out, "var " + node_label + " = new YAHOO.widget.TaskNode(newNodeData, " + parent_node_id + ", false);");
		    }

		    if (expandable || isHasMoreNode) {
			    println(out, node_label + ".isLeaf = false;");
			    println(out, node_label + ".ontology_node_child_count = 1;");

		    } else {
				println(out, node_label + ".ontology_node_child_count = 0;");
			    println(out, node_label + ".isLeaf = true;");

		    }

		} catch (Exception ex) {

		}

    }


//////////////////////////////////////////////////////////////////////////////////////////////


    private String replaceNodeID(String code) {
		/*
		code = code.replaceAll(":", "cCc");
        code = code.replaceAll("-", "cDc");
        code = code.replaceAll("_", "cUc");
        code = code.replaceAll("/", "cSc");
        code = code.replaceAll(".", "cEc");
		return code;
		*/
		String s = "" + code.hashCode();
		s = s.replace("-", "n");
		return s + generateRandomString();
	}

    public static void main(String[] args) throws Exception {
        //new ValueSetUtils();
    }

}



