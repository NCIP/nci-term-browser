package gov.nih.nci.evs.browser.utils;

import gov.nih.nci.evs.browser.common.*;
import gov.nih.nci.evs.browser.properties.*;

import java.io.*;
import java.util.*;
import net.sf.ehcache.*;
import org.LexGrid.LexBIG.DataModel.Collections.*;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.*;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.*;
import org.LexGrid.LexBIG.Extensions.Generic.*;
import org.LexGrid.LexBIG.Extensions.Generic.LexBIGServiceConvenienceMethods.*;
import org.LexGrid.LexBIG.Impl.Extensions.tree.json.JsonConverter;
import org.LexGrid.LexBIG.Impl.Extensions.tree.json.JsonConverterFactory;
import org.LexGrid.LexBIG.Impl.Extensions.tree.model.*;
import org.LexGrid.LexBIG.Impl.Extensions.tree.service.*;
import org.LexGrid.LexBIG.LexBIGService.*;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.*;
import org.LexGrid.codingSchemes.*;
import org.LexGrid.commonTypes.*;
import org.LexGrid.concepts.*;
import org.LexGrid.naming.*;
import org.apache.log4j.*;
import org.json.*;

public class HierarchyUtils {
    LexBIGService lbSvc = null;
    LexBIGServiceConvenienceMethods lbscm = null;
    TreeUtils treeUtils = null;
    RelationshipUtils relUtils = null;
    ViewInHierarchyUtils vihUtils = null;
    CodingSchemeDataUtils csdu = null;
    TreeService treeService = null;

    private static String DEFAULT_HIERARCHY_ID = "is_a";
    boolean APPLY_NULL_NS = false;

    public HierarchyUtils(LexBIGService lbSvc) {
        this.lbSvc = lbSvc;
        treeUtils = new TreeUtils(lbSvc);
        relUtils = new RelationshipUtils(lbSvc);
        vihUtils = new ViewInHierarchyUtils(lbSvc);
        ViewInHierarchyUtils vihUtils = new ViewInHierarchyUtils(lbSvc);
        csdu = new CodingSchemeDataUtils(lbSvc);
		treeService = TreeServiceFactory.getInstance().getTreeService(lbSvc);

        try {
			this.lbscm = (LexBIGServiceConvenienceMethods) lbSvc.getGenericExtension("LexBIGServiceConvenienceMethods");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
    }

    protected CodingScheme getCodingScheme(String codingScheme,
        CodingSchemeVersionOrTag versionOrTag) throws LBException {
        CodingScheme cs = null;
        try {
            cs = lbSvc.resolveCodingScheme(codingScheme, versionOrTag);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cs;
    }

    public SupportedHierarchy[] getSupportedHierarchies(
        String codingScheme, CodingSchemeVersionOrTag versionOrTag)
            throws LBException {

        CodingScheme cs = null;
        try {
            cs = getCodingScheme(codingScheme, versionOrTag);
        } catch (Exception ex) {

        }
        if (cs == null) {
            throw new LBResourceUnavailableException(
                "Coding scheme not found -- " + codingScheme);
        }
        Mappings mappings = cs.getMappings();
        return mappings.getSupportedHierarchy();
    }

    public String[] getHierarchyIDs(String codingScheme, String version) {
		CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
		if (version != null) {
			versionOrTag.setVersion(version);
		}
		try {
			return getHierarchyIDs(codingScheme, versionOrTag);
		} catch (Exception ex) {
			return null;
		}
	}

    public String getHierarchyID(String scheme, String version) {
		CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
		if (version != null) {
	        versionOrTag.setVersion(version);
		}
		try {
			String[] ids = getHierarchyIDs(scheme, versionOrTag);
			if (ids != null && ids.length == 1) {
				return ids[0];
			}
			return selectHierarchyId(ids, DEFAULT_HIERARCHY_ID);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}


    public String[] getHierarchyIDs(
		String codingScheme,
        CodingSchemeVersionOrTag versionOrTag) throws LBException {

        String[] hier = null;
        Set<String> ids = new HashSet<String>();
        SupportedHierarchy[] sh = null;
        try {
            sh = getSupportedHierarchies(codingScheme, versionOrTag);
            if (sh != null) {
                for (int i = 0; i < sh.length; i++) {
                    ids.add(sh[i].getLocalId());
                }
                hier = ids.toArray(new String[ids.size()]);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return hier;
    }

    public String selectHierarchyId(String[] ids, String id) {
		if (ids == null || ids.length == 0) return null;
		if (ids.length == 1) {
			return ids[0];
		} else {
			if (Arrays.asList(ids).contains(id)) {
				return id;
			} else {
				return ids[0];
			}
		}
	}

    public String getRootJSONString(String codingScheme, String version) {
		long ms = System.currentTimeMillis();
		ResolvedConceptReferenceList rcrl = getHierarchyRoots(codingScheme, version);
            System.out.println("getHierarchyRoots run time (milliseconds): "
                + (System.currentTimeMillis() - ms));

		TreeItem root = new TreeItem("Root", "<Root>");
        Vector w = new Vector();
        for (int i=0; i<rcrl.getResolvedConceptReferenceCount(); i++) {
			ResolvedConceptReference rcr = rcrl.getResolvedConceptReference(i);
			w.add(rcr);
		}
		w = SortUtils.quickSort(w);
		try {
			for (int i=0; i<w.size(); i++) {
				ResolvedConceptReference rcr = (ResolvedConceptReference) w.elementAt(i);
				String cs_name = rcr.getCodingSchemeName();
				String cs_version = rcr.getCodingSchemeVersion();
				String cs_ns = rcr.getCodeNamespace();
				String code = rcr.getConceptCode();
				String name = rcr.getEntityDescription().getContent();
				boolean is_expandable = false;
				try {
					is_expandable = isExpandable(treeService, cs_name, cs_version, code, cs_ns);
					//System.out.println(cs_name + "|" + cs_version + "|" + code + "|" + cs_ns);
				} catch (Exception ex) {
                    System.out.println("WARNING: isExpandable throws exceptions.");
                    if (APPLY_NULL_NS) {
						is_expandable = isExpandable(treeService, cs_name, cs_version, code, null);
					}
				}
				TreeItem child = new TreeItem(code, name, cs_ns, null);
				child._expandable = is_expandable;
				root.addChild("has_child", child);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		String json = JSON2TreeItem.treeItem2Json(root);
		System.out.println("treeItem2Json run time (milliseconds): "
			+ (System.currentTimeMillis() - ms));
		return json;
	}

	public boolean isExpandable(TreeService treeService, String scheme, String version, String code, String namespace) {
		CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
		if (version != null) {
			versionOrTag.setVersion(version);
		}
    	LexEvsTreeNode node = null;
		try {
			node = treeService.getSubConcepts(scheme, versionOrTag, code, namespace);
		} catch (Exception ex) {
			//System.out.println("treeService.getSubConcepts throws exception???");
			ex.printStackTrace();
			return false;
		}

        if (node != null && node.getExpandableStatus().toString().compareTo("IS_EXPANDABLE") == 0) {
			return true;
		}
        return false;
	}

    public ResolvedConceptReferenceList getHierarchyRoots(
        String codingScheme, String version) {
		ResolvedConceptReferenceList list = treeUtils.getHierarchyRoots(codingScheme, version);
		return list;
	}

    public String getRemainingSubconceptJSONString(String codingScheme, String version, String parent_code, String parent_ns, String focus_code, boolean from_root) {
	    if (parent_ns == null || parent_ns.compareTo("null") == 0 || parent_ns.compareTo("undefined") == 0) {
		     parent_ns = new ConceptDetails(lbSvc).getNamespaceByCode(codingScheme, version, parent_code);
	    }
		long ms = System.currentTimeMillis();
        boolean useNamespace = false;
        if (parent_ns == null || parent_ns.compareTo("null") == 0) {
			parent_ns = new ConceptDetails(lbSvc).getNamespaceByCode(codingScheme, version, parent_code);
			if (parent_ns != null) {
				useNamespace = true;
			}
		}
		ms = System.currentTimeMillis();
		HashMap hmap = null;
		if (from_root) {
			hmap = vihUtils.getRemainingNodes(codingScheme, version, focus_code, parent_ns, "@");
		} else {
			hmap = vihUtils.getRemainingNodes(codingScheme, version, focus_code, parent_ns, parent_code);
		}

		TreeItem root = (TreeItem) hmap.get("<Root>");
		String json = JSON2TreeItem.treeItem2Json(root);
		return json;
	}

    public JSONObject treeItem2JSONObject(TreeItem ti) {
		JSONObject json_object = new JSONObject();
		try {
			json_object.put(Constants.ONTOLOGY_NODE_NAME, ti._text);
			json_object.put(Constants.ONTOLOGY_NODE_ID, ti._code);
			json_object.put(Constants.ONTOLOGY_NODE_NS, ti._ns);
			json_object.put(Constants.ASSOCIATION, ti._auis);
			if (ti._expandable) {
				json_object.put(Constants.ONTOLOGY_NODE_CHILD_COUNT, "1");
			} else {
				json_object.put(Constants.ONTOLOGY_NODE_CHILD_COUNT, "0");
			}
			JSONArray list = new JSONArray();
			for (String association : ti._assocToChildMap.keySet()) {
				List<TreeItem> children = ti._assocToChildMap.get(association);
				SortUtils.quickSort(children);
				for (int i=0; i<children.size(); i++) {
					TreeItem childItem = (TreeItem) children.get(i);
					JSONObject child_obj = treeItem2JSONObject(childItem);
					list.put(child_obj);
				}
			}
			json_object.put(Constants.CHILDREN_NODES, list);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return json_object;
	}

    public JSONArray treeItemList2JSONArray(List list) {
		JSONArray nodesArray = new JSONArray();
		for (int i=0; i<list.size(); i++) {
			TreeItem ti = (TreeItem) list.get(i);
			JSONObject obj = (JSONObject) treeItem2JSONObject(ti);
			nodesArray.put(obj);
		}
		return nodesArray;
	}

    public JSONArray getSubconceptJSONArray(String codingScheme, String version, String code, String ns) {
        String key = codingScheme + "$" + version + "$" + code + "$" + ns;
		long ms = System.currentTimeMillis();
        boolean useNamespace = false;
        if (ns == null) {
			ns = new ConceptDetails(lbSvc).getNamespaceByCode(codingScheme, version, code);
			if (ns != null) {
				useNamespace = true;
			}
		}
		TreeItem root = new TreeItem("Root", "<Root>");
		if (code.compareTo("<Root>") == 0) {
			return null;
		}
        List options = relUtils.createOptionList(false, true, false, false, false, false);
        HashMap relMap = relUtils.getRelationshipHashMap(codingScheme, version, code, ns, useNamespace, options);
        List list = (ArrayList) relMap.get("type_subconcept");
        Vector w = new Vector();
        for (int i=0; i<list.size(); i++) {
			String t = (String) list.get(i);
			w.add(t);
		}
		w = SortUtils.quickSort(w);
		List treeItem_list = new ArrayList();
        for (int i=0; i<w.size(); i++) {
		    String t = (String) w.elementAt(i);
			Vector u = StringUtils.parseData(t);
			String child_name = (String) u.elementAt(0);
			String child_code = (String) u.elementAt(1);
			String child_ns = (String) u.elementAt(2);
			boolean is_expandable = false;
			try {
				is_expandable = isExpandable(treeService, codingScheme, version, child_code, child_ns);
			} catch (Exception ex) {
				System.out.println("WARNING: isExpandable throws exceptions.");
				if (APPLY_NULL_NS) {
					is_expandable = isExpandable(treeService, codingScheme, version, child_code, null);
				}
			}
			TreeItem child = new TreeItem(child_code, child_name, child_ns, null);
			child._expandable = is_expandable;
			treeItem_list.add(child);
		}
		JSONArray nodeArray = treeItemList2JSONArray(treeItem_list);
		return nodeArray;
	}

    public JSONArray getSubconcepts(String scheme, String version, String code, String ns, boolean fromCache) {
        String parent_code = null;
		String focus_code = null;
        HashMap map = null;
        JSONArray nodeArray = null;
        ViewInHierarchyUtils util = new ViewInHierarchyUtils(lbSvc);
        // getRemainingSubconcepts
		if (code.indexOf("_dot_") != -1) {
			parent_code = util.getParentCode(code);
			focus_code = util.getFocusCode(code);
			boolean from_root = false;
			if (code.indexOf("root_") != -1) {
				from_root = true;
			}

			String json = getRemainingSubconceptJSONString(scheme, version, parent_code, ns, focus_code, from_root);
			try {
				nodeArray = new JSONArray(json);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return nodeArray;
		}
        //String key = scheme + "$" + version + "$" + code + "$" + ns;
        if (nodeArray == null) {
            try {
			    nodeArray = getSubconceptJSONArray(scheme, version, code, ns);
		    } catch (Exception ex) {
				ex.printStackTrace();
			}
        }
        return nodeArray;
    }

    public List getNamespacesOfCode(String scheme, String version, String code) {
		List list = null;
		CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
		if (version != null) {
			versionOrTag.setVersion(version);
		}
		try {
			list = lbscm.getDistinctNamespacesOfCode(scheme, versionOrTag, code);
			return list;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

    public boolean testHierarchy(PrintWriter pw, String scheme, String version, String code) {
		if (version == null) {
			version = csdu.getVocabularyVersionByTag(scheme, Constants.PRODUCTION);
		}

		CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
		if (version != null) {
			versionOrTag.setVersion(version);
		}

		pw.println("scheme: " + scheme);
		pw.println("version: " + version);
		pw.println("code: " + code);
		// View Hierarchy
		String hier_id = getHierarchyID(scheme, version);
		pw.println("hierarchy id: " + hier_id);

		ResolvedConceptReferenceList roots = getHierarchyRoots(scheme, version);
		if (roots == null) return false;
		Vector v = new Vector();
		for (int i=0; i<roots.getResolvedConceptReferenceCount(); i++) {
			ResolvedConceptReference rcr = roots.getResolvedConceptReference(i);
			v.add(rcr.getEntityDescription().getContent() + " (" + rcr.getConceptCode() + ")");
		}
		v = SortUtils.quickSort(v);
		for (int i=0; i<v.size(); i++) {
			String line = (String) v.elementAt(i);
			int j = i+1;
			pw.println("\t(" + j + ") " + line);
		}

		String rootJSONString = getRootJSONString(scheme, version);
		if (rootJSONString == null) return false;
		pw.println("\nrootJSONString: ");
		pw.println(rootJSONString);

		String ns = null;
        List ns_list = getNamespacesOfCode(scheme, version, code);
        if (ns_list == null) {
			pw.println("ns_list == null??? " + code);
		}
		LexEvsTreeNode node = null;
        pw.println("Number of namespaces: " + ns_list.size());
        for (int i=0; i<ns_list.size(); i++) {
		    ns = (String) ns_list.get(i);
		    pw.println("\tnamespace: " + ns);
			try {
				node = treeService.getSubConcepts(scheme, versionOrTag, code, ns);
			} catch (Exception ex) {
				pw.println("treeService.getSubConcepts throws exception???");
				return false;
			}

			JSONArray nodeArray = getSubconceptJSONArray(scheme, version, code, ns);
			if (nodeArray == null) {
				pw.println("getSubconceptJSONArray returns null??? " + code);
				return false;
			}
			try {
				pw.println("subconceptJSONArray: " + nodeArray.getString(0));
			} catch (Exception ex) {
				ex.printStackTrace();
			}

            // View in Hierarchy
            String vih_json = null;
            try {
			    vih_json = vihUtils.getTree(scheme, version, code, ns);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (vih_json == null) {
				pw.println("vihUtils.getTree returns null??? " + code + " namespace: " + ns);
				return false;
			} else {
				pw.println("\nVIH JSON: ");
				pw.println(vih_json);
			}
		}
    	return true;
	}

	public Vector getChildrenCodes(LexEvsTreeNode node) {
	   List<LexEvsTreeNode> list_children = node.getPathToRootChildren();
	   if (list_children == null) return null;
	   Vector v = new Vector();
	   for (int i=0; i<list_children.size(); i++) {
		   LexEvsTreeNode childNode = list_children.get(i);
		   v.add(childNode.getCode());
	   }
	   return v;
	}


    public Vector getChildrenCodes(String scheme, String version, String code, String namespace) {
		CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
		if (version != null) {
			versionOrTag.setVersion(version);
		}
    	LexEvsTreeNode node = null;
		try {
			node = treeService.getSubConcepts(scheme, versionOrTag, code, namespace);
			return getChildrenCodes(node);

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

/*
    public static void main(String[] args) {
		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
		HierarchyUtils test = new HierarchyUtils(lbSvc);

		String scheme = "NCI_Thesaurus";
		String version = null;
		version = "OWL2Asserted NS";
		String code = "C43431"; // Activity
		String rootJSON = null;

		String outputfile = "test_hier_" + code + ".txt";
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(outputfile, "UTF-8");
			pw.println("Service URL: " + RemoteServerUtil.getServiceURL());
			long ms1 = System.currentTimeMillis();
			scheme = "NCI_Thesaurus";
			version = "OWL2Asserted NS";
			//version = null;
			code = "C43431"; // Activity
            pw.println("\n==========================================================");
			boolean bool = test.testHierarchy(pw, scheme, version, code);
			if (bool) {
				pw.println("\nResult: Success.");
			} else {
				pw.println("\nResult: Failed.");
			}
			System.out.println("Run time (milliseconds): " + (System.currentTimeMillis() - ms1));
		} catch (Exception ex) {

		} finally {
			try {
				pw.close();
				System.out.println("Output file " + outputfile + " generated.");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
    }
*/
}
