package gov.nih.nci.evs.browser.utils;

import java.io.*;
import java.util.*;
import org.LexGrid.LexBIG.DataModel.Collections.*;
import org.LexGrid.LexBIG.DataModel.Collections.ConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.*;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.ConceptReference;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.*;
import org.LexGrid.LexBIG.Extensions.Generic.*;
import org.LexGrid.LexBIG.Extensions.Generic.LexBIGServiceConvenienceMethods.*;
import org.LexGrid.LexBIG.Impl.Extensions.tree.dao.iterator.ChildTreeNodeIterator;
import org.LexGrid.LexBIG.Impl.Extensions.tree.json.JsonConverter;
import org.LexGrid.LexBIG.Impl.Extensions.tree.json.JsonConverterFactory;
import org.LexGrid.LexBIG.Impl.Extensions.tree.model.LexEvsTree;
import org.LexGrid.LexBIG.Impl.Extensions.tree.model.LexEvsTreeNode;
import org.LexGrid.LexBIG.Impl.Extensions.tree.service.TreeService;
import org.LexGrid.LexBIG.Impl.Extensions.tree.service.TreeServiceFactory;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;
import org.LexGrid.LexBIG.LexBIGService.*;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.*;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.caCore.interfaces.LexEVSApplicationService;
import org.LexGrid.codingSchemes.*;
import org.LexGrid.commonTypes.*;
import org.LexGrid.concepts.*;
import org.LexGrid.naming.*;
import org.apache.log4j.*;
import org.json.JSONArray;
import org.json.JSONObject;

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
	public static int MAX_CHILDREN = 5;
	private static Logger _logger = Logger.getLogger(ViewInHierarchyUtils.class);
	private static Random rand = new Random();


	int has_more_node_knt = 0;

	public String rt_1 = null;
	public String rt_2 = null;
	public String rt_3 = null;
	public String rt_4 = null;
	public String rt = null;

    private LexBIGService lbSvc = null;
    private LexBIGServiceConvenienceMethods lbscm = null;

	private String generateRandomString() {
		int i = rand.nextInt();
		//String t = new Integer(i).toString();
		String t = Integer.valueOf(i).toString();
		t = t.replace("-", "n");
		return "_" + t;
	}

    private String replaceNodeID(String code) {
		String s = "" + code.hashCode();
		s = s.replace("-", "n");
		return s + generateRandomString();
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

    public ViewInHierarchyUtils(LexBIGService lbSvc) {
		try {
			this.lbSvc = lbSvc;
			this.lbscm = (LexBIGServiceConvenienceMethods) lbSvc.getGenericExtension("LexBIGServiceConvenienceMethods");
			has_more_node_knt = 0;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

    private static void println(PrintWriter out, String text) {
        out.println(text);
    }

    private String restoreNodeID(String code) {
		code = code.replaceAll("cCc", ":");
        code = code.replaceAll("cDc", "-");
        code = code.replaceAll("cUc", "_");
        //code = code.replaceAll("cSc", "/");
        //code = code.replaceAll("cEc", ".");
		return code;
	}


    public void printTree(String codingScheme, String version, String code) {
		has_more_node_knt = 0;
        printTree(codingScheme, version, code, null);
    }

    public void printTree(String codingScheme, String version, String code, String namespace) {
		has_more_node_knt = 0;
		try {
			System.setProperty("file.encoding", "UTF-8");
			PrintWriter pw  = new PrintWriter(new OutputStreamWriter(System.out, "UTF-8"), true);
			printTree(pw, codingScheme, version, code, namespace);
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    }

    public String getNamespaceByCode(String codingSchemeName, String vers, String code) {
        try {
			if (code == null) {
				return null;
			}
			if (code.indexOf("@") != -1) return null; // anonymous class

            //LexBIGService lbSvc = new RemoteServerUtil().createLexBIGService();
            if (lbSvc == null) {
                return null;
            }
            CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
            if (vers != null) versionOrTag.setVersion(vers);

            ConceptReferenceList crefs = createConceptReferenceList(
                    new String[] { code }, codingSchemeName);

            CodedNodeSet cns = null;
            try {
				try {
					cns = getNodeSet(codingSchemeName, versionOrTag);

				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}

                if (cns == null) {
					return null;
				}

                cns = cns.restrictToCodes(crefs);
 				ResolvedConceptReferenceList matches = null;
				try {
					matches = cns.resolveToList(null, null, null, 1);
				} catch (Exception e) {
					e.printStackTrace();
				}

                if (matches == null) {
                    return null;
                }
                int count = matches.getResolvedConceptReferenceCount();
                // Analyze the result ...
                if (count == 0)
                    return null;
                if (count > 0) {
                    try {
                        ResolvedConceptReference ref = (ResolvedConceptReference) matches
                                .enumerateResolvedConceptReference()
                                .nextElement();

                        return ref.getCodeNamespace();
                    } catch (Exception ex1) {
                        ex1.printStackTrace();
                        return null;
                    }
                }
            } catch (Exception e1) {
                e1.printStackTrace();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }


    public void printTree(PrintWriter out, String codingScheme, String version, String code) {
		printTree(out, codingScheme, version, code, null);
	}


	public LexEvsTreeNode treeItem2LexEvsTreeNode(String codingScheme, String version, TreeItem ti) {
		if (ti == null) return null;
		LexEvsTreeNode node = new LexEvsTreeNode();
		node.setCode(ti._code);
		node.setEntityDescription(ti._text);
		String namespace = ti._ns;

		if (StringUtils.isNullOrBlank(namespace)) {
			namespace = getNamespaceByCode(codingScheme, version, ti._code);
		}

		node.setNamespace(namespace);
	    node.setExpandableStatus(LexEvsTreeNode.ExpandableStatus.IS_EXPANDABLE);
	    return node;
	}

	boolean isNodeInList(List<LexEvsTreeNode> listEvsTreeNode, String code) {
		if (listEvsTreeNode == null) return false;
		for (int k=0; k<listEvsTreeNode.size(); k++) {
			LexEvsTreeNode node = listEvsTreeNode.get(k);
			if (node.getCode().compareTo(code) == 0) return true;
		}
		return false;
	}


	List<LexEvsTreeNode> removeDotNode(List<LexEvsTreeNode> listEvsTreeNode) {
		List<LexEvsTreeNode> list = new ArrayList();
		for (int i=0; i<listEvsTreeNode.size(); i++) {
			LexEvsTreeNode node = listEvsTreeNode.get(i);
			if (node.getCode().compareTo("...") != 0 && node.getEntityDescription().compareTo("...") != 0) {
				list.add(node);
			}
		}
		return list;
	}



	List<LexEvsTreeNode> getRemainingLexEvsTreeNodes(String codingScheme, String version, List<LexEvsTreeNode> listEvsTreeNode) {
		List<LexEvsTreeNode> list = new ArrayList();
		HashMap hmap = getRoots(codingScheme, version);
        TreeItem ti = (TreeItem) hmap.get("<Root>");
		for (String association : ti._assocToChildMap.keySet()) {
			List<TreeItem> children = ti._assocToChildMap.get(association);
			for (int i=0; i<children.size(); i++) {
				TreeItem childItem = (TreeItem) children.get(i);
				if (!isNodeInList(listEvsTreeNode, childItem._code)) {
					LexEvsTreeNode node = treeItem2LexEvsTreeNode(codingScheme, version, childItem);
					list.add(node);
				}
			}
		}
		return list;
	}

//to be modified:
    public void printTree(PrintWriter out, String codingScheme, String version, String code, String namespace) {
        try {
			long ms_0 = System.currentTimeMillis();
			long ms = System.currentTimeMillis();

            TreeService service =
                    TreeServiceFactory.getInstance().getTreeService(lbSvc);
                        //RemoteServerUtil.createLexBIGService());

            rt_1 = "" + (System.currentTimeMillis() - ms);
			ms = System.currentTimeMillis();

            CodingSchemeVersionOrTag csvt = null;
            if (version != null && version.length() > 0)
                csvt = Constructors.createCodingSchemeVersionOrTagFromVersion(version);

            LexEvsTree tree = null;
            /*
            if (StringUtils.isNullOrBlank(namespace)) {
                namespace = getNamespaceByCode(codingScheme, version, code);
			}

            LexEvsTree tree = service.getTree(codingScheme, csvt, code, namespace);
            */
            if (StringUtils.isNullOrBlank(namespace)) {
				tree = service.getTree(codingScheme, csvt, code);
			} else {
				tree = service.getTree(codingScheme, csvt, code, namespace);
			}

            rt_2 = "" + (System.currentTimeMillis() - ms);
            ms = System.currentTimeMillis();

            LexEvsTreeNode focusNode = tree.getCurrentFocus();
            focusNode.setNamespace(namespace);

            List<LexEvsTreeNode> listEvsTreeNode =
                    service.getEvsTreeConverter()
                        .buildEvsTreePathFromRootTree(focusNode);

// 01252016:
            listEvsTreeNode = removeDotNode(listEvsTreeNode);

	        List<LexEvsTreeNode> remainingRootNodes = getRemainingLexEvsTreeNodes(codingScheme, version, listEvsTreeNode);
	        for (int k=0; k<remainingRootNodes.size(); k++) {
				LexEvsTreeNode node = remainingRootNodes.get(k);
    			listEvsTreeNode.add(node);
			}

            rt_3 = "" + (System.currentTimeMillis() - ms);
            ms = System.currentTimeMillis();
            LexEvsTreeNode root = null;

//sort:
            Vector w = new Vector();
	        for (int k=0; k<listEvsTreeNode.size(); k++) {
				LexEvsTreeNode node = (LexEvsTreeNode) listEvsTreeNode.get(k);
    			w.add(node);
			}

            w = SortUtils.quickSort(w);
            listEvsTreeNode = new ArrayList<LexEvsTreeNode>();
	        for (int k=0; k<w.size(); k++) {
				LexEvsTreeNode node = (LexEvsTreeNode) w.get(k);
    			listEvsTreeNode.add(node);
			}

            printTree(out, "", code, root, "root", listEvsTreeNode);

            rt_4 = "" + (System.currentTimeMillis() - ms);

            rt = "" + (System.currentTimeMillis() - ms_0);

        } catch (Exception e) {
			e.printStackTrace();
            _logger.error(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    public Vector getReponseTimes() {
		Vector v = new Vector();
		v.add(rt_1);
		v.add(rt_2);
		v.add(rt_3);
		v.add(rt_4);
		v.add(rt);
		return v;


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

			String node_label = node_id;//"N_" + replaceNodeID(code);
		    String node_name = node.getEntityDescription();
		    String node_ns = node.getNamespace();


			String code = node.getCode();
			boolean isHasMoreNode = false;
			if (code.compareTo("...") == 0) {
				isHasMoreNode = true;
				has_more_node_knt++;

				/*
				if (parent == null) {
					code = "root" + "_" + focus_code + "_dot_" + Integer.valueOf(has_more_node_knt).toString();
				} else {
				    code = parent.getCode() + "_dot_" + Integer.valueOf(has_more_node_knt).toString();
				}
				*/

				if (parent == null) {
					code = "root" + "_" + focus_code + "_dot_" + Integer.valueOf(has_more_node_knt).toString();
				} else {
				    code = parent.getCode() + "_dot_" + focus_code + "_" + Integer.valueOf(has_more_node_knt).toString();
				}
			}


		    String indentStr = indent + "      ";
		    String symbol = getNodeSymbol(node);

		    println(out, "");
            println(out, indentStr + "// " + symbol + " " + node_name + "(" + code + ")");

		    //println(out, indentStr + "newNodeDetails = \"javascript:onClickTreeNode('" + code + "');\";");
		    println(out, indentStr + "newNodeDetails = \"javascript:onClickTreeNode('" + code
		                                                                               + "','"
		                                                                               + node_ns
		                                                                               + "');\";");

		    //	[GF#32225] View-In-Hierarchy page fails to render on tree node label containing double quote characters. KLO, 061312
		    if (node_name.indexOf("\"") != -1) {
				node_name = replaceAll(node_name, "\"", "'");
			}

            //[NCITERM-595] Handle coding schemes with multiple namespaces.
		    //println(out, indentStr + "newNodeData = { label:\"" + node_name + "\", id:\"" + code + "\", href:newNodeDetails };");

		    println(out, indentStr + "newNodeData = { label:\"" + node_name + "\", id:\"" + code + "\", ns:\"" + node_ns + "\", href:newNodeDetails };");


		    if (expanded) {
			    println(out, indentStr + "var " + node_label + " = new YAHOO.widget.TextNode(newNodeData, " + parent_node_id + ", true);");
		    } else {
			    println(out, indentStr + "var " + node_label + " = new YAHOO.widget.TextNode(newNodeData, " + parent_node_id + ", false);");
		    }

		    if (expandable || isHasMoreNode) {
			    println(out, indentStr + node_label + ".isLeaf = false;");
			    println(out, indentStr + node_label + ".ontology_node_child_count = 1;");
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
            ex.printStackTrace();
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
		return getChildren(codingScheme, version, parent_code, null, from_root);
	}


    public List<LexEvsTreeNode> getChildren(String codingScheme, String version, String parent_code, String parent_ns, boolean from_root) {
        List<LexEvsTreeNode> list = new ArrayList();
        try {
			CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
			if (version != null)
				versionOrTag.setVersion(version);
			TreeService treeService =
				TreeServiceFactory.getInstance().getTreeService(lbSvc);//RemoteServerUtil.createLexBIGService());

			LexEvsTree lexEvsTree = null;

			if (!StringUtils.isNullOrBlank(parent_ns)) {
				lexEvsTree = treeService.getTree(codingScheme, versionOrTag, parent_code);
			} else {
				try {
					lexEvsTree = treeService.getTree(codingScheme, versionOrTag, "@");
					if (lexEvsTree == null) {
						lexEvsTree = treeService.getTree(codingScheme, versionOrTag, "@@");
					}
				} catch (Exception ex) {
					System.out.println(	"treeService.getTree failed.");
					return null;
				}
			}

			if (lexEvsTree == null) {
				System.out.println(	"lexEvsTree == null???");
				return null;

			}

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
								String child_ns = child.getNamespace();
								if (StringUtils.isNullOrBlank(parent_ns)) {
									if (!hset.contains(child_code)) {
										hset.add(child_code);
										list.add(child);
									}
								} else {
									if (!StringUtils.isNullOrBlank(child_ns)
									&& parent_ns.compareTo(child_ns) == 0) {
										if (!hset.contains(child_code)) {
											hset.add(child_code);
											list.add(child);
										}
									}
								}
							}
						}


					} catch (Exception ex) {
						//ex.printStackTrace();
						_logger.debug("WARNING: ChildTreeNodeIterator exception...");
					}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return list;
    }

    public HashMap getRemainingSubconcepts(String codingScheme, String version, String parent_code, boolean from_root) {
		return getRemainingSubconcepts(codingScheme, version, parent_code, null, from_root, null);
	}

    public HashMap getRemainingSubconcepts(String codingScheme, String version, String parent_code, boolean from_root, String focus_code) {
		return getRemainingSubconcepts(codingScheme, version, parent_code, null, from_root, focus_code);

	}

    public HashMap getRemainingSubconcepts(String codingScheme, String version, String parent_code, String parent_ns, boolean from_root, String focus_code) {

        HashMap hmap = new HashMap();
        String childNavText = "inverse_is_a";

		TreeItem ti = new TreeItem(parent_code, "", parent_ns, null);
		ti._expandable = false;

        List<LexEvsTreeNode> list = getChildren(codingScheme, version, parent_code, parent_ns, from_root);
        try {
			if (list.size() > MAX_CHILDREN) {
				for (int i=MAX_CHILDREN-1; i<list.size(); i++) {
					LexEvsTreeNode child = (LexEvsTreeNode) list.get(i);

					if (child != null) {
                        boolean include = true;
                        if (focus_code != null) {
							if (focus_code.compareTo(child.getCode()) == 0) {
								include = false;
							}
						}

						if (include) {
							TreeItem childItem =
								new TreeItem(child.getCode(),
									child.getEntityDescription(),
									child.getNamespace(),
									null
									);

							childItem._expandable = false;
							LexEvsTreeNode.ExpandableStatus child_node_status = child.getExpandableStatus();
							if (child_node_status != null && child_node_status == LexEvsTreeNode.ExpandableStatus.IS_EXPANDABLE) {
								childItem._expandable = true;
							}
							ti._expandable = true;
							ti.addChild(childNavText, childItem);
					    }
					}
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

        hmap.put(parent_code, ti);
        return hmap;
    }


    public HashMap getSubconcepts(String codingScheme, String version, String focus_code) {
		return getSubconcepts(codingScheme, version, focus_code, null);
	}


    public HashMap getSubconcepts(String codingScheme, String version, String focus_code, String focus_ns) {
        HashMap hmap = new HashMap();
        List<LexEvsTreeNode> list = null;
        String childNavText = "inverse_is_a";

		TreeItem ti = new TreeItem(focus_code, "", focus_ns, null);
		if (StringUtils.isNullOrBlank(focus_ns)) {
			ti = new TreeItem(focus_code, "");
		}

		ti._expandable = false;
        list = getChildren(codingScheme, version, focus_code, focus_ns, false);

        if (list != null && list.size() > 0) {
			Vector w = new Vector();
			for (int i=0; i<list.size(); i++) {
				LexEvsTreeNode child = (LexEvsTreeNode) list.get(i);
				TreeItem childItem =
					new TreeItem(child.getCode(),
						child.getEntityDescription(), child.getNamespace(), null);
				if (StringUtils.isNullOrBlank(focus_ns)) {
				    childItem = new TreeItem(child.getCode(), child.getEntityDescription());

				}
				childItem._expandable = false;
				LexEvsTreeNode.ExpandableStatus child_node_status = child.getExpandableStatus();
				if (child_node_status == LexEvsTreeNode.ExpandableStatus.IS_EXPANDABLE) {
					childItem._expandable = true;
				}
				w.add(childItem);
			}
			w = SortUtils.quickSort(w);
			for (int i=0; i<w.size(); i++) {
				TreeItem childItem = (TreeItem) w.elementAt(i);
				ti.addChild(childNavText, childItem);
				ti._expandable = true;
			}
		}

        hmap.put(focus_code, ti);
        return hmap;
    }

    public String getFocusCode(String ontology_node_id) {
		if (ontology_node_id == null) return null;
		if (ontology_node_id.indexOf("_dot_") == -1) {
			return ontology_node_id;
		}
		Vector v = StringUtils.parseData(ontology_node_id, "_");
		for (int i=0; i<v.size(); i++) {
			String t = (String) v.elementAt(i);
		}
        if (v.contains("root")) {
			return restoreNodeID((String) v.elementAt(1));
		}
		return restoreNodeID((String) v.elementAt(2));
	}

    public String getParentCode(String ontology_node_id) {
		if (ontology_node_id == null) return null;
		if (ontology_node_id.indexOf("_dot_") == -1) {
			return ontology_node_id;
		}
		Vector v = StringUtils.parseData(ontology_node_id, "_");
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


    //public static CodedNodeSet getNodeSet(LexBIGService lbSvc, String scheme, CodingSchemeVersionOrTag versionOrTag)
    public CodedNodeSet getNodeSet(String scheme, CodingSchemeVersionOrTag versionOrTag)
        throws Exception {
		CodedNodeSet cns = null;
		try {
			cns = lbSvc.getCodingSchemeConcepts(scheme, versionOrTag);
			CodedNodeSet.AnonymousOption restrictToAnonymous = CodedNodeSet.AnonymousOption.NON_ANONYMOUS_ONLY;
			cns = cns.restrictToAnonymous(restrictToAnonymous);
	    } catch (Exception ex) {
			ex.printStackTrace();
		}

		return cns;
	}

    public ConceptReferenceList createConceptReferenceList(
        String[] codes, String codingSchemeName) {
        if (codes == null) {
            return null;
        }
        ConceptReferenceList list = new ConceptReferenceList();
        for (int i = 0; i < codes.length; i++) {
            ConceptReference cr = new ConceptReference();
            cr.setCodingSchemeName(codingSchemeName);
            cr.setConceptCode(codes[i]);
            list.addConceptReference(cr);
        }
        return list;
    }

    public ConceptReferenceList createConceptReferenceList(Vector codes,
        String codingSchemeName) {
        if (codes == null) {
            return null;
        }
        ConceptReferenceList list = new ConceptReferenceList();
        for (int i = 0; i < codes.size(); i++) {
            String code = (String) codes.elementAt(i);
            ConceptReference cr = new ConceptReference();
            cr.setCodingSchemeName(codingSchemeName);
            cr.setConceptCode(code);
            list.addConceptReference(cr);
        }
        return list;
    }

    public String getTree(String codingScheme, CodingSchemeVersionOrTag versionOrTag, String code, String namespace) {
		TreeService treeService =
			TreeServiceFactory.getInstance().getTreeService(lbSvc);

		LexEvsTree tree = null;
		if (StringUtils.isNullOrBlank(namespace)) {
			tree = treeService.getTree(codingScheme, versionOrTag, code);
		} else {
			tree = treeService.getTree(codingScheme, versionOrTag, code, namespace);
		}

		String json =
			treeService.getJsonConverter().buildJsonPathFromRootTree(
				tree.getCurrentFocus());

		return json;
    }


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public HashMap getSubConcepts(String codingScheme, String version, String code, String namespace) {
		if (code.compareTo("@") == 0 || code.compareTo("@@") == 0) {
			return getRoots(codingScheme, version);
		}
		HashMap hmap = new HashMap();
        String childNavText = "inverse_is_a";

		TreeItem ti = new TreeItem(code, "", namespace, null);
		ti._expandable = false;
		Vector w = new Vector();

        List<LexEvsTreeNode> list = new ArrayList();
        try {
			CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
			if (version != null)
				versionOrTag.setVersion(version);
			TreeService treeService =
				TreeServiceFactory.getInstance().getTreeService(lbSvc);//RemoteServerUtil.createLexBIGService());

			LexEvsTree lexEvsTree = treeService.getTree(codingScheme, versionOrTag, code, namespace);
			LexEvsTreeNode parent_node = lexEvsTree.findNodeInTree(code);
			ChildTreeNodeIterator itr = parent_node.getChildIterator();
			try {
				HashSet hset = new HashSet();
				int lcv = 0;

				while(itr.hasNext()){
					LexEvsTreeNode child = itr.next();
					TreeItem childItem =
						new TreeItem(child.getCode(),
							child.getEntityDescription(), child.getNamespace(), null);

					if (StringUtils.isNullOrBlank(namespace)) {
						childItem = new TreeItem(child.getCode(), child.getEntityDescription());
					}
					childItem._expandable = false;
					LexEvsTreeNode.ExpandableStatus child_node_status = child.getExpandableStatus();
					if (child_node_status == LexEvsTreeNode.ExpandableStatus.IS_EXPANDABLE) {
						childItem._expandable = true;
					}
					w.add(childItem);
				}
				w = SortUtils.quickSort(w);
				for (int i=0; i<w.size(); i++) {
					TreeItem childItem = (TreeItem) w.elementAt(i);
					ti.addChild(childNavText, childItem);
					ti._expandable = true;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		hmap.put(code, ti);
		return hmap;
	}

	public HashMap getRoots(String scheme, String version) {
        TreeUtils treeUtils = new TreeUtils(lbSvc);
        String childNavText = "inverse_is_a";
		ResolvedConceptReferenceList rcrl = treeUtils.getHierarchyRoots(scheme, version);
        HashMap hmap = new HashMap();
		TreeItem ti = new TreeItem("<Root>", "", "", null);
		ti._expandable = false;
		Vector w = new Vector();

		for (int i=0; i<rcrl.getResolvedConceptReferenceCount(); i++) {
			ResolvedConceptReference rcr = (ResolvedConceptReference) rcrl.getResolvedConceptReference(i);
			TreeItem childItem =
				new TreeItem(rcr.getCode(),
					rcr.getEntityDescription().getContent(), rcr.getCodeNamespace(), null);
			childItem = new TreeItem(rcr.getCode(), rcr.getEntityDescription().getContent());
			childItem._expandable = true;
			w.add(childItem);
		}
		w = SortUtils.quickSort(w);
		for (int i=0; i<w.size(); i++) {
			TreeItem childItem = (TreeItem) w.elementAt(i);
			ti.addChild(childNavText, childItem);
			ti._expandable = true;
		}
		hmap.put("<Root>", ti);
		return hmap;
	}

    public String getTree(String codingScheme, String version, String code) {
		return getTree(codingScheme, version, code, null);
	}
/*
    public String getTree(String codingScheme, String version, String code, String namespace) {
        try {
			CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
			if (version != null) {
				versionOrTag.setVersion(version);
			}
			return getTree(codingScheme, versionOrTag, code, namespace);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
*/
	public TreeItem searchTree(String scheme, String version, String ns, String code, TreeItem ti) {
		if (code.compareTo("@") == 0 || code.compareTo("@@") == 0) {
			HashMap hmap = getRoots(scheme, version);
			return (TreeItem) hmap.get("<Root>");
		}
		if (ti._code.compareTo(code) == 0) return ti;
		for (String association : ti._assocToChildMap.keySet()) {
			List<TreeItem> children = ti._assocToChildMap.get(association);
			for (int i=0; i<children.size(); i++) {
				TreeItem childItem = (TreeItem) children.get(i);
				if (childItem._ns != null && ns != null) {
					if (childItem._code.compareTo(code) == 0 && childItem._ns.compareTo(ns) == 0) {
						return childItem;
					}
				} else if (childItem._code.compareTo(code) == 0) {
					return childItem;
				}
				return searchTree(scheme, version, ns, code, childItem);
			}
		}
		return null;
	}

	public HashSet getChildItemCodes(TreeItem ti) {
		if (ti == null) return null;
		HashSet hset = new HashSet();
		for (String association : ti._assocToChildMap.keySet()) {
			List<TreeItem> children = ti._assocToChildMap.get(association);
			for (int i=0; i<children.size(); i++) {
				TreeItem childItem = (TreeItem) children.get(i);
				if (childItem._code.compareTo(ti._code) != 0 && !hset.contains(childItem._code)) {
					hset.add(childItem._code);
				}
			}
		}
		return hset;
	}

	public static void dumpHashSet(HashSet hset) {
		if (hset == null) return;
		Iterator it = hset.iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			System.out.println(key);
		}
	}

    public TreeItem removeChildNodes(TreeItem ti, HashSet childCodes) {
		TreeItem root = new TreeItem("<Root>", "Root node");
		root._expandable = false;
        for (String association : ti._assocToChildMap.keySet()) {
            List<TreeItem> children = ti._assocToChildMap.get(association);
            System.out.println("association: " + association);
            System.out.println("children.size(): " + children.size());
            for (int i=0; i<children.size(); i++) {
				TreeItem childItem = (TreeItem) children.get(i);

				System.out.println("\tchildItem._code: " + childItem._code);

                if (!childCodes.contains(childItem._code)) {

					System.out.println("\taddChild: " + childItem._code);
					root.addChild(association, childItem);
					root._expandable = true;
				}
			}
        }
        System.out.println("\treturn root: " + root._code);
        return root;
	}

    public HashMap getRemainingNodes(String scheme, String version, String focus_code, String namespace, String dot_node_code) {
		TreeItem ti = null;
		TreeItem tree_item = null;
		HashMap tree_hmap = null;
		HashMap hmap = null;

		if (StringUtils.isNullOrBlank(namespace)) {
			namespace = getNamespaceByCode(scheme, version, focus_code);
		}

        if (dot_node_code.compareTo("@") == 0 || dot_node_code.compareTo("@@") == 0) {
			String json = getTree(scheme, version, focus_code, namespace);
			System.out.println("\n\n(1) VIH JSON:" + "\n" + json);
			ti = JSON2TreeItem.json2TreeItem(json);
			TreeItem.printTree(ti, 0);
			tree_item = ti;
		} else {
			String json = getTree(scheme, version, focus_code, namespace);
			System.out.println("\n\n(2) VIH JSON:" + "\n" + json);
			ti = JSON2TreeItem.json2TreeItem(json);
			tree_item = searchTree(scheme, version, namespace, dot_node_code, ti);
			TreeItem.printTree(tree_item, 0);
		}

		HashSet hset = getChildItemCodes(tree_item);
		dumpHashSet(hset);

		if (dot_node_code.compareTo("@") == 0 || dot_node_code.compareTo("@@") == 0) {
			hmap = getRoots(scheme, version);
            ti = (TreeItem) hmap.get("<Root>");
	    } else {
            hmap = new TreeUtils(lbSvc).getSubconcepts(scheme, version, dot_node_code, namespace);
            ti = (TreeItem) hmap.get(dot_node_code);
		}

        if (hmap == null) {
			System.out.println("new TreeUtils(lbSvc).getSubconcepts ???");
			return null;
		}

        if (ti == null) {
			System.out.println("ti == null");
			return null;
		}

		TreeItem.printTree(ti, 0);
        TreeItem new_ti = removeChildNodes(ti, hset);
        TreeItem.printTree(new_ti, 0);

        hmap = new HashMap();
        hmap.put("<Root>", new_ti);
        return hmap;
	}


    //public String construct_view_in_hierarchy_tree(String codingScheme, String version, String code, String namespace) {
    public String getTree(String codingScheme, String version, String code, String namespace) {

		CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
		if (version != null) {
			versionOrTag.setVersion(version);
		}

		TreeService treeService =
			TreeServiceFactory.getInstance().getTreeService(lbSvc);

		LexEvsTree tree = null;
		if (StringUtils.isNullOrBlank(namespace)) {
			tree = treeService.getTree(codingScheme, versionOrTag, code);
		} else {
			tree = treeService.getTree(codingScheme, versionOrTag, code, namespace);
		}

		String json =
			treeService.getJsonConverter().buildJsonPathFromRootTree(
				tree.getCurrentFocus());

        //System.out.println(json);
		TreeItem vih_tree = JSON2TreeItem.json2TreeItem(json);
		ViewInHierarchyUtils util = new ViewInHierarchyUtils(lbSvc);

		HashMap hmap = getRoots(codingScheme, version);
		TreeItem vh_tree = (TreeItem) hmap.get("<Root>");

        HashMap vh_hmap = new HashMap();
		TreeItem vh_root = new TreeItem("<Root>", "", "", null);
		vh_root._expandable = false;
		String childNavText = "inverse_is_a";

		for (String association : vh_tree._assocToChildMap.keySet()) {
			List<TreeItem> children = vh_tree._assocToChildMap.get(association);
			for (int i=0; i<children.size(); i++) {
				TreeItem childItem = (TreeItem) children.get(i);
				if (childItem._code.compareTo("@@") != 0 && childItem._code.compareTo("@") != 0) {
					TreeItem node = searchTree(codingScheme, version, childItem._ns, childItem._code, vih_tree);
					if (node == null) {
						vh_root.addChild(childNavText, childItem);
						vh_root._expandable = true;
					} else {
						vh_root.addChild(childNavText, node);
						vh_root._expandable = true;
					}
			    }
			}
		}

		json = JSON2TreeItem.treeItem2Json(vh_root);
		System.out.println("\nNew Tree========================================");
		TreeItem.printTree(vh_root, 0);
		return json;
    }

/*
    public static void main(String[] args) throws Exception {
		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
        ViewInHierarchyUtils vihu = new ViewInHierarchyUtils(lbSvc);
        String scheme = "NCI_Thesaurus";
        String version = "15.12d";
        String namespace = "NCI_Thesaurus";
        String code = "C2693"; //Erlotinib Hydrochloride (Code C2693)
        //code = "C38628";
	    HashMap hmap = vihu.getRoots(scheme, version);
	    TreeItem ti = (TreeItem) hmap.get("<Root>");
	    TreeItem.printTree(ti, 0);
        try {
			hmap = vihu.getSubConcepts(scheme, version, code, namespace);
			if (hmap != null) {
				ti = (TreeItem) hmap.get(code);
				TreeItem.printTree(ti, 0);

				String json = JSON2TreeItem.treeItem2Json(ti);
				System.out.println(json);
		    } else {
				System.out.println("hmap == null???");
			}
		} catch (Exception ex) {
			System.out.println("Exception thrown???");
		}

		String json = vihu.getTree(scheme, version, code, namespace);
		System.out.println("\n\nVIH JSON:" + "\n" + json);

		TreeItem ti = JSON2TreeItem.json2TreeItem(json);
		TreeItem.printTree(ti, 0);

		TreeItem tree_item = vihu.searchTree(scheme, version, namespace, "C1404", ti);
		TreeItem.printTree(tree_item, 0);

		HashSet hset = vihu.getChildItemCodes(tree_item);

        code = "C1404";
        HashMap hmap = new TreeUtils(lbSvc).getSubconcepts(scheme, version, code, namespace);
		ti = (TreeItem) hmap.get(code);
		TreeItem.printTree(ti, 0);

        TreeItem new_ti = vihu.removeChildNodes(ti, hset);
        TreeItem.printTree(new_ti, 0);

        String focus_code = "C20181";//Conceptual Entity (Code C20181)
        String dot_node_code = "@";
        HashMap hmap = vihu.getRemainingNodes(scheme, version, focus_code, namespace, dot_node_code);
		TreeItem ti = (TreeItem) hmap.get("<Root>");
		TreeItem.printTree(ti, 0);

		//Cell Aging (Code C16394)
		//Cellular Process (Code C20480)

        String focus_code = "C16394";//Conceptual Entity (Code C20181)
        String dot_node_code = "C20480";
        HashMap hmap = vihu.getRemainingNodes(scheme, version, focus_code, namespace, dot_node_code);
		TreeItem ti = (TreeItem) hmap.get("<Root>");
		TreeItem.printTree(ti, 0);
        code = "C16394";
        String vih_json = vihu.getTree(scheme, version, code, namespace);
    }
*/
}

