package gov.nih.nci.evs.browser.utils;

import java.io.*;
import java.util.*;

import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.json.JSONArray;
import org.json.JSONObject;

/*
import org.lexevs.tree.json.JsonConverter;
import org.lexevs.tree.json.JsonConverterFactory;
import org.lexevs.tree.model.LexEvsTree;
import org.lexevs.tree.model.LexEvsTreeNode;
import org.lexevs.tree.model.LexEvsTreeNode.ExpandableStatus;
import org.lexevs.tree.service.TreeService;
import org.lexevs.tree.service.TreeServiceFactory;
import org.lexevs.tree.dao.iterator.ChildTreeNodeIterator;
*/

import org.LexGrid.LexBIG.Impl.Extensions.tree.json.JsonConverter;
import org.LexGrid.LexBIG.Impl.Extensions.tree.json.JsonConverterFactory;
import org.LexGrid.LexBIG.Impl.Extensions.tree.service.TreeService;
import org.LexGrid.LexBIG.Impl.Extensions.tree.service.TreeServiceFactory;
import org.LexGrid.LexBIG.Impl.Extensions.tree.dao.iterator.ChildTreeNodeIterator;
import org.LexGrid.LexBIG.Impl.Extensions.tree.model.LexEvsTreeNode;
import org.LexGrid.LexBIG.Impl.Extensions.tree.model.LexEvsTree;




import org.apache.log4j.*;

import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.caCore.interfaces.LexEVSApplicationService;

import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Collections.ConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.ConceptReference;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;



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
		this.lbSvc = lbSvc;
		has_more_node_knt = 0;
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

            rt_3 = "" + (System.currentTimeMillis() - ms);
            ms = System.currentTimeMillis();
            LexEvsTreeNode root = null;
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
        // root: input parent_code = "@" or "@@";
        List<LexEvsTreeNode> list = new ArrayList();
        try {
			CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
			if (version != null)
				versionOrTag.setVersion(version);
			TreeService treeService =
				TreeServiceFactory.getInstance().getTreeService(lbSvc);//RemoteServerUtil.createLexBIGService());

			LexEvsTree lexEvsTree = null;
			if (StringUtils.isNullOrBlank(parent_ns)) {
				lexEvsTree = treeService.getTree(codingScheme, versionOrTag, parent_code);
			} else {
				lexEvsTree = treeService.getTree(codingScheme, versionOrTag, parent_code, parent_ns);
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
				for (int i=MAX_CHILDREN; i<list.size(); i++) {
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
        String childNavText = "inverse_is_a";

		TreeItem ti = new TreeItem(focus_code, "", focus_ns, null);
		if (StringUtils.isNullOrBlank(focus_ns)) {
			ti = new TreeItem(focus_code, "");
		}

		ti._expandable = false;
        List<LexEvsTreeNode> list = getChildren(codingScheme, version, focus_code, focus_ns, false);

        if (list.size() > 0) {
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
				//ti.addChild(childNavText, childItem);
				//ti._expandable = true;
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


/*
	if (parent == null) {
		code = "root" + "_" + focus_code + "_dot_" + Integer.valueOf(has_more_node_knt).toString();
	} else {
		code = parent.getCode() + "_dot_" + focus_code + "_" + Integer.valueOf(has_more_node_knt).toString();
	}
*/

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

    public String getTree(String codingScheme,
        CodingSchemeVersionOrTag versionOrTag, String code, String namespace) {
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


    public static void main(String[] args) throws Exception {
		LexBIGService lbSvc = LexBIGServiceImpl.defaultInstance();
        ViewInHierarchyUtils vihu = new ViewInHierarchyUtils(lbSvc);
        vihu.printTree("npo", "TestForMultiNamespace", "NPO_1607", "npo");
    }
}

