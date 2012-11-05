package gov.nih.nci.evs.browser.utils;

import java.io.*;
import java.util.*;

import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.json.JSONArray;
import org.json.JSONObject;
import org.lexevs.tree.json.JsonConverter;
import org.lexevs.tree.json.JsonConverterFactory;
import org.lexevs.tree.model.LexEvsTree;
import org.lexevs.tree.model.LexEvsTreeNode;
import org.lexevs.tree.model.LexEvsTreeNode.ExpandableStatus;
import org.lexevs.tree.service.TreeService;
import org.lexevs.tree.service.TreeServiceFactory;

import org.lexevs.tree.dao.iterator.ChildTreeNodeIterator;
import org.apache.log4j.*;

/**
 * <!-- LICENSE_TEXT_START -->
 * Copyright 2008,2009 NGIT. This software was developed in conjunction
 * with the National Cancer Institute, and so to the extent government
 * employees are co-authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the disclaimer of Article 3,
 *      below. Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions and the following
 *      disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *   2. The end-user documentation included with the redistribution,
 *      if any, must include the following acknowledgment:
 *      "This product includes software developed by NGIT and the National
 *      Cancer Institute."   If no such end-user documentation is to be
 *      included, this acknowledgment shall appear in the software itself,
 *      wherever such third-party acknowledgments normally appear.
 *   3. The names "The National Cancer Institute", "NCI" and "NGIT" must
 *      not be used to endorse or promote products derived from this software.
 *   4. This license does not authorize the incorporation of this software
 *      into any third party proprietary programs. This license does not
 *      authorize the recipient to use any trademarks owned by either NCI
 *      or NGIT
 *   5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED
 *      WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *      OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE
 *      DISCLAIMED. IN NO EVENT SHALL THE NATIONAL CANCER INSTITUTE,
 *      NGIT, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT,
 *      INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 *      BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *      LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *      CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *      LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 *      ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *      POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */

/**
 * @author EVS Team
 * @version 1.0
 *
 *      Modification history Initial implementation kim.ong@ngc.com
 *
 */

// Note: Version with the has more (...) nodes feature.

public class ViewInHierarchyUtils {
	private int MAX_CHILDREN = 5;
	private static Logger _logger = Logger.getLogger(DataUtils.class);
	private static Random rand = new Random();


	int has_more_node_knt = 0;


	private String generateRandomString() {
		int i = rand.nextInt();
		//String t = new Integer(i).toString();
		String t = Integer.valueOf(i).toString();
		t = t.replace("-", "n");
		return "_" + t;
	}


    private String generateID(LexEvsTreeNode node) {
		String node_id = null;
		if (node == null) {
			node_id = "root";
		} else {
			node_id = "N_" + replaceNodeID(node.getCode());
		}
		return node_id;
	}


    public ViewInHierarchyUtils() {
		has_more_node_knt = 0;
	}

    private static void println(PrintWriter out, String text) {
        gov.nih.nci.evs.browser.servlet.AjaxServlet.println(out, text);
    }

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

/*
    private String replaceNodeID(String code) {
		code = code.replaceAll(":", "cCc");
        code = code.replaceAll("-", "cDc");
        code = code.replaceAll("_", "cUc");
        //code = code.replaceAll("/", "cSc");
        //code = code.replaceAll(".", "cEc");
		return code;
	}
*/

    private String restoreNodeID(String code) {
		code = code.replaceAll("cCc", ":");
        code = code.replaceAll("cDc", "-");
        code = code.replaceAll("cUc", "_");
        //code = code.replaceAll("cSc", "/");
        //code = code.replaceAll("cEc", ".");
		return code;
	}


    public ViewInHierarchyUtils(String codingScheme, String version, String code) {
		has_more_node_knt = 0;
        try {
			System.setProperty("file.encoding", "UTF8");
			PrintWriter pw = new PrintWriter(System.out, true);
            printTree(pw, codingScheme, version, code);
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void printTree(PrintWriter out, String codingScheme, String version, String code) {
        try {
            TreeService service =
                    TreeServiceFactory.getInstance().getTreeService(
                        RemoteServerUtil.createLexBIGService());

            long start = System.currentTimeMillis();
            CodingSchemeVersionOrTag csvt = null;
            if (version != null && version.length() > 0)
                csvt = Constructors.createCodingSchemeVersionOrTagFromVersion(version);

            String namespace = DataUtils.getNamespaceByCode(codingScheme, version, code);

    //System.out.println("(*************) namespace: " + namespace);

            LexEvsTree tree = service.getTree(codingScheme, csvt, code, namespace);
            List<LexEvsTreeNode> listEvsTreeNode =
                    service.getEvsTreeConverter()
                        .buildEvsTreePathFromRootTree(tree.getCurrentFocus());

            LexEvsTreeNode root = null;
            printTree(out, "", code, root, "root", listEvsTreeNode);
        } catch (Exception e) {
            _logger.error(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    private void printTree(PrintWriter out, String indent, String focus_code, LexEvsTreeNode parent, String parent_node_id, List<LexEvsTreeNode> nodes) {
        for (LexEvsTreeNode node : nodes) {
           char c = ' ';

           String node_id = generateID(node);
           if (node.getExpandableStatus() == LexEvsTreeNode.ExpandableStatus.IS_EXPANDABLE) {
               c = node.getPathToRootChildren() != null ? '-' : '+';
           }
           printTreeNode(out, indent, focus_code, node, node_id, parent, parent_node_id);
           List<LexEvsTreeNode> list_children = node.getPathToRootChildren();
           if (list_children != null) {
                printTree(out, indent + "  ", focus_code, node, node_id, list_children);
           }
        }
    }

    private void printTreeNode(PrintWriter out, String indent, String focus_code, LexEvsTreeNode node, String node_id, LexEvsTreeNode parent, String parent_node_id) {
		if (node == null) return;


		try {
			LexEvsTreeNode.ExpandableStatus node_status = node.getExpandableStatus();
			String image = "[+]";
			boolean expandable = true;
			if (node_status != LexEvsTreeNode.ExpandableStatus.IS_EXPANDABLE) {
				image = ".";
				expandable = false;
			}

			boolean expanded = false;

			if (node_status == LexEvsTreeNode.ExpandableStatus.IS_EXPANDABLE) {

				List<LexEvsTreeNode> list_children = node.getPathToRootChildren();
				if (list_children != null && list_children.size() > 0) {
					expanded = true;
				}
			}

            String parent_code = null;
            if (parent != null) {
			    parent_code = parent.getCode();
			}

/*
            String parent_id = null;
		    if (parent == null) {
			    parent_id = "root";
		    } else {
			    //parent_id = replaceNodeID("N_" + parent.getCode());
			    parent_id = "N_" + replaceNodeID(parent.getCode());
		    }
*/

			String code = node.getCode();
			boolean isHasMoreNode = false;
			if (code.compareTo("...") == 0) {
				isHasMoreNode = true;
				has_more_node_knt++;
				if (parent == null) {
					//code = "root" + "_" + focus_code + "_dot_" + new Integer(has_more_node_knt).toString();
					code = "root" + "_" + focus_code + "_dot_" + Integer.valueOf(has_more_node_knt).toString();
				} else {
				    code = parent.getCode() + "_dot_" + new Integer(has_more_node_knt).toString();
				}
			}

			String node_label = node_id;//"N_" + replaceNodeID(code);
		    String node_name = node.getEntityDescription();
		    String indentStr = indent + "      ";
		    String symbol = getNodeSymbol(node);

		    println(out, "");
            println(out, indentStr + "// " + symbol + " " + node_name + "(" + code + ")");
		    println(out, indentStr + "newNodeDetails = \"javascript:onClickTreeNode('" + code + "');\";");

		    //	[GF#32225] View-In-Hierarchy page fails to render on tree node label containing double quote characters. KLO, 061312
		    if (node_name.indexOf("\"") != -1) {
				node_name = replaceAll(node_name, "\"", "'");
			}

		    println(out, indentStr + "newNodeData = { label:\"" + node_name + "\", id:\"" + code + "\", href:newNodeDetails };");
		    if (expanded) {
			    println(out, indentStr + "var " + node_label + " = new YAHOO.widget.TextNode(newNodeData, " + parent_node_id + ", true);");

		    } else if (isHasMoreNode) {
			    println(out, indentStr + "var " + node_label + " = new YAHOO.widget.TextNode(newNodeData, " + parent_node_id + ", false);");
		    } else {
			    println(out, indentStr + "var " + node_label + " = new YAHOO.widget.TextNode(newNodeData, " + parent_node_id + ", false);");
		    }

		    if (expandable || isHasMoreNode) {
			    println(out, indentStr + node_label + ".isLeaf = false;");
			    println(out, indentStr + node_label + ".ontology_node_child_count = 1;");

			    //if (node.getPathToRootChildren() == null && !isHasMoreNode)
			    if (node.getPathToRootChildren() == null) {
			        println(out, indentStr + node_label + ".setDynamicLoad(loadNodeData);");
				}
		    } else {
				println(out, indentStr + node_label + ".ontology_node_child_count = 0;");
			    println(out, indentStr + node_label + ".isLeaf = true;");
		    }


		    if (focus_code.compareTo(code) == 0) {
			    println(out, indentStr + node_label + ".labelStyle = \"ygtvlabel_highlight\";");
		    }
		} catch (Exception ex) {

		}

    }

    private static String getNodeSymbol(LexEvsTreeNode node) {
        String symbol = "@";
        if (node.getExpandableStatus() == LexEvsTreeNode.ExpandableStatus.IS_EXPANDABLE) {
            symbol = node.getPathToRootChildren() != null ? "-" : "+";
        }
        return symbol;
    }

    public List<LexEvsTreeNode> getChildren(String codingScheme, String version, String parent_code, boolean from_root) {
        // root: input parent_code = "@" or "@@";
        List<LexEvsTreeNode> list = new ArrayList();
        CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
        if (version != null)
            versionOrTag.setVersion(version);
        TreeService treeService =
            TreeServiceFactory.getInstance().getTreeService(
                RemoteServerUtil.createLexBIGService());

        LexEvsTree lexEvsTree = treeService.getTree(codingScheme, versionOrTag, parent_code);
        LexEvsTreeNode parent_node = null;
        if (!from_root) {
            parent_node = lexEvsTree.findNodeInTree(parent_code);
		} else {
			parent_node = lexEvsTree.findNodeInTree("@@");
			if (parent_node == null) {
				parent_node = lexEvsTree.findNodeInTree("@");
			}
		}
		if (parent_node == null) {
			return null;
		}

		LexEvsTreeNode.ExpandableStatus parent_node_status = parent_node.getExpandableStatus();
		if (parent_node_status == LexEvsTreeNode.ExpandableStatus.IS_EXPANDABLE) {
			ChildTreeNodeIterator itr = parent_node.getChildIterator();

			try {
				HashSet hset = new HashSet();
				int lcv = 0;

				while(itr.hasNext()){
					LexEvsTreeNode child = itr.next();
					lcv++;
					if (child != null) {
						String child_code = child.getCode();
						//System.out.println("(" + lcv + ") " + "getChildren child_code " + child_code );
						if (!hset.contains(child_code)) {
							hset.add(child_code);
							list.add(child);
						} else {
							break;
						}
					} else {
						break;
					}
				}
			} catch (Exception ex) {
			    //ex.printStackTrace();
			    _logger.debug("WARNING: ChildTreeNodeIterator exception...");
			}
		}
		return list;
    }

    public HashMap getRemainingSubconcepts(String codingScheme, String version, String focus_code, boolean from_root) {
        HashMap hmap = new HashMap();
        String childNavText = "inverse_is_a";
		long ms = System.currentTimeMillis();

		TreeItem ti = new TreeItem(focus_code, "");
		ti._expandable = false;

        List<LexEvsTreeNode> list = getChildren(codingScheme, version, focus_code, from_root);
        if (list.size() > MAX_CHILDREN) {
			for (int i=MAX_CHILDREN; i<list.size(); i++) {
				LexEvsTreeNode child = (LexEvsTreeNode) list.get(i);
				TreeItem childItem =
					new TreeItem(child.getCode(),
						child.getEntityDescription());

				childItem._expandable = false;
				LexEvsTreeNode.ExpandableStatus child_node_status = child.getExpandableStatus();
				if (child_node_status == LexEvsTreeNode.ExpandableStatus.IS_EXPANDABLE) {
					childItem._expandable = true;
				}
				ti._expandable = true;
				ti.addChild(childNavText, childItem);
			}
		}

        hmap.put(focus_code, ti);
        return hmap;
    }

    public HashMap getSubconcepts(String codingScheme, String version, String focus_code) {
        HashMap hmap = new HashMap();
        String childNavText = "inverse_is_a";
		long ms = System.currentTimeMillis();

		TreeItem ti = new TreeItem(focus_code, "");
		ti._expandable = false;

        List<LexEvsTreeNode> list = getChildren(codingScheme, version, focus_code, false);
        if (list.size() > 0) {
			for (int i=0; i<list.size(); i++) {
				LexEvsTreeNode child = (LexEvsTreeNode) list.get(i);
				TreeItem childItem =
					new TreeItem(child.getCode(),
						child.getEntityDescription());

				childItem._expandable = false;
				LexEvsTreeNode.ExpandableStatus child_node_status = child.getExpandableStatus();
				if (child_node_status == LexEvsTreeNode.ExpandableStatus.IS_EXPANDABLE) {
					childItem._expandable = true;
				}
				ti._expandable = true;
				ti.addChild(childNavText, childItem);
			}
		}

        hmap.put(focus_code, ti);
        return hmap;
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


    public String getFocusCode(String ontology_node_id) {
		if (ontology_node_id == null) return null;
		if (ontology_node_id.indexOf("_dot_") == -1) {
			return ontology_node_id;
		}
		Vector v = parseData(ontology_node_id, "_");
		for (int i=0; i<v.size(); i++) {
			String t = (String) v.elementAt(i);
		}
        if (v.contains("root")) {
			return restoreNodeID((String) v.elementAt(1));
		}
		return restoreNodeID((String) v.elementAt(0));
	}


    public static String replaceAll(String t, String s1, String s2) {
		int n = t.indexOf(s1);
		while (n != -1) {
            if (n > 0) {
				t = t.substring(0, n) + s2 + t.substring(n+s1.length(), t.length());
			} else {
				t = s2 + t.substring(n+s1.length(), t.length());
			}
			n = t.indexOf(s1);

		}

		return t;
	}

    public static void main(String[] args) throws Exception {
        new ViewInHierarchyUtils("NCI_Thesaurus", "11.09d", "C37927"); // Color
    }

}



