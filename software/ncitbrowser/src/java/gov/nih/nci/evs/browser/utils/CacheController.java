package gov.nih.nci.evs.browser.utils;

import java.util.*;

import net.sf.ehcache.*;

import org.LexGrid.LexBIG.DataModel.Core.*;
import org.LexGrid.commonTypes.*;
import org.LexGrid.concepts.*;

import org.json.*;

/*
import org.lexevs.tree.model.*;
import org.lexevs.tree.service.*;
*/
import org.LexGrid.LexBIG.Impl.Extensions.tree.json.JsonConverter;
import org.LexGrid.LexBIG.Impl.Extensions.tree.json.JsonConverterFactory;
import org.LexGrid.LexBIG.Impl.Extensions.tree.model.*;
import org.LexGrid.LexBIG.Impl.Extensions.tree.service.*;

import gov.nih.nci.evs.browser.properties.*;
import gov.nih.nci.evs.browser.common.*;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.apache.log4j.*;

import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;


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
 * Modification history
 *     Initial implementation kim.ong@ngc.com
 *
 */

public class CacheController {
    private static Logger _logger = Logger.getLogger(CacheController.class);
    public static final String ONTOLOGY_ADMINISTRATORS = "ontology_administrators";
    public static final String ONTOLOGY_FILE = "ontology_file";
    public static final String ONTOLOGY_FILE_ID = "ontology_file_id";
    public static final String ONTOLOGY_DISPLAY_NAME = "ontology_display_name";
    public static final String ONTOLOGY_NODE = "ontology_node";
    public static final String ONTOLOGY_NODE_ID = "ontology_node_id";
    public static final String ONTOLOGY_NODE_SCHEME = "ontology_node_scheme";
    public static final String ONTOLOGY_SOURCE = "ontology_source";
    public static final String ONTOLOGY_NODE_NAME = "ontology_node_name";
    public static final String ONTOLOGY_NODE_PARENT_ASSOC = "ontology_node_parent_assoc";
    public static final String ONTOLOGY_NODE_CHILD_COUNT = "ontology_node_child_count";
    public static final String ONTOLOGY_NODE_DEFINITION = "ontology_node_definition";
    public static final String CHILDREN_NODES = "children_nodes";

    public static final String ONTOLOGY_NODE_NS = "ontology_node_ns";

    private static CacheController _instance = null;
    private static CacheManager _cacheManager = null;
    private static Cache _cache = null;


    static {
		String cacheName = "treeCache";
		_cacheManager = getCacheManager();
        if (!_cacheManager.cacheExists(cacheName)) {
            _cacheManager.addCache(cacheName);
			_logger.debug("cache added");
        }
        _cache = _cacheManager.getCache(cacheName);
    }


    public CacheController(String cacheName) {
        if (!_cacheManager.cacheExists(cacheName)) {
            _cacheManager.addCache(cacheName);
        }

        _logger.debug("cache added");
        _cache = _cacheManager.getCache(cacheName);
    }


    public static CacheController getInstance() {
        synchronized (CacheController.class) {
            if (_instance == null) {
                _instance = new CacheController("treeCache");
            }
        }
        return _instance;
    }
/*
    public static CacheController getInstance() {
		return new CacheController();
	}
*/


    private static CacheManager getCacheManager() {
        if (_cacheManager != null)
            return _cacheManager;
        try {
            NCItBrowserProperties properties =
                NCItBrowserProperties.getInstance();
            String ehcache_xml_pathname =
                properties
                    .getProperty(NCItBrowserProperties.EHCACHE_XML_PATHNAME);
            _cacheManager = new CacheManager(ehcache_xml_pathname);
            return _cacheManager;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }





    public String[] getCacheNames() {
        return getCacheManager().getCacheNames();
    }

    public void clear() {
        _cache.removeAll();
        // cache.flush();
    }

    public boolean containsKey(Object key) {
        return _cache.isKeyInCache(key);
    }

    public boolean containsValue(Object value) {
        return _cache.isValueInCache(value);
    }

    public boolean isEmpty() {
        return _cache.getSize() > 0;
    }

    public JSONArray getSubconcepts(String scheme, String version, String code) {
        return getSubconcepts(scheme, version, code, null, true);
    }

    public JSONArray getSubconcepts(String scheme, String version, String code, String ns) {
        return getSubconcepts(scheme, version, code, ns, true);
    }

    public String getRemainingSubconceptJSONString(String codingScheme, String version, String parent_code, String parent_ns, String focus_code, boolean from_root) {
        LexBIGService lb_svc = RemoteServerUtil.createLexBIGService();
	    if (parent_ns == null || parent_ns.compareTo("null") == 0 || parent_ns.compareTo("undefined") == 0) {
		     parent_ns = new ConceptDetails(lb_svc).getNamespaceByCode(codingScheme, version, parent_code);
	    }
		long ms = System.currentTimeMillis();
		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
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
			hmap = new ViewInHierarchyUtils(lbSvc).getRemainingNodes(codingScheme, version, focus_code, parent_ns, "@");
		} else {
			hmap = new ViewInHierarchyUtils(lbSvc).getRemainingNodes(codingScheme, version, focus_code, parent_ns, parent_code);
		}

		TreeItem root = (TreeItem) hmap.get("<Root>");
		//TreeItem.printTree(root, 0);
		String json = JSON2TreeItem.treeItem2Json(root);
        //System.out.println("getRemainingSubconceptJSONString run time (milliseconds): "
        //        + (System.currentTimeMillis() - ms));
		return json;
	}


/*
    public String getRemainingSubconceptJSONString(String codingScheme, String version, String parent_code, String parent_ns, String focus_code) {
		long ms = System.currentTimeMillis();
		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();

        boolean useNamespace = false;
        if (parent_ns == null) {
			parent_ns = new ConceptDetails(lbSvc).getNamespaceByCode(codingScheme, version, parent_code);
			//System.out.println("NS: " + ns);
			if (parent_ns != null) {
				useNamespace = true;
			}
		}

		TreeService treeService =
			TreeServiceFactory.getInstance().getTreeService(lbSvc);


		TreeItem root = new TreeItem("Root", "<Root>");
		if (parent_code.compareTo("<Root>") == 0) {
			return null;
		}

        boolean from_root = false;
        List<LexEvsTreeNode> list = new ViewInHierarchyUtils(lbSvc).getChildren(codingScheme, version, parent_code, parent_ns, from_root);
        try {
			if (list.size() > ViewInHierarchyUtils.MAX_CHILDREN) {
				for (int i=ViewInHierarchyUtils.MAX_CHILDREN; i<list.size(); i++) {
					LexEvsTreeNode child = (LexEvsTreeNode) list.get(i);
					if (child != null) {
                        boolean include = true;
                        if (focus_code != null) {
							if (focus_code.compareTo(child.getCode()) == 0) {
								include = false;
							}
						}

						if (include) {

							String name = "<UNSPECIFIED>";
							if (child.getEntityDescription() != null) {
								name = child.getEntityDescription();
							}

							TreeItem childItem =
								new TreeItem(child.getCode(),
									name,
									child.getNamespace(),
									null
									);

							childItem._expandable = false;
							LexEvsTreeNode.ExpandableStatus child_node_status = child.getExpandableStatus();
							if (child_node_status != null && child_node_status == LexEvsTreeNode.ExpandableStatus.IS_EXPANDABLE) {
								childItem._expandable = true;
							}
							root.addChild("has_child", childItem);
							root._expandable = true;
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		ms = System.currentTimeMillis();
		String json = JSON2TreeItem.treeItem2Json(root);
		//json = "{\"nodes\":" + json + "}";
		return json;
	}
*/

    public JSONArray getSubconcepts(String scheme, String version, String code, String ns, boolean fromCache) {
        if (scheme == null) {
            scheme = Constants.CODING_SCHEME_NAME;
			String retval = DataUtils.getCodingSchemeName(scheme);
			if (retval != null) {
				scheme = retval;
				version = DataUtils.key2CodingSchemeVersion(scheme);
			}
		}

        String parent_code = null;
		String focus_code = null;
        HashMap map = null;
        JSONArray nodeArray = null;
        LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
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

            /* 090215
			map = util.getRemainingSubconcepts(scheme, version, parent_code, ns, from_root, focus_code);
			nodeArray = hashMap2JSONArray(map);
			*/

			return nodeArray;
		}

        String key = scheme + "$" + version + "$" + code + "$" + ns;
        if (fromCache) {
            Element element = _cache.get(key);
            if (element != null) {
				if (element.getValue() instanceof JSONArray) {
                	nodeArray = (JSONArray) element.getValue();
				}
            }
        }
        //KLO, 090215
        if (nodeArray == null) {
            _logger.debug("Not in cache -- calling getSubconcepts " + scheme + " (code: " + code + ")");
            //map = new TreeUtils(lbSvc).getSubconcepts(scheme, version, code, ns);
            try {
				//nodeArray = new JSONArray(getSubconceptJSONString(scheme, version, code, ns));

			    nodeArray = getSubconceptJSONArray(scheme, version, code, ns);

				if (fromCache) {
					try {
						Element element = new Element(key, nodeArray);
						_cache.put(element);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
		    } catch (Exception ex) {
				ex.printStackTrace();
			}
        } else {
            _logger.debug("Retrieved from cache.");
        }
        return nodeArray;
    }


    public JSONArray getSubValueSets(String scheme, String version, String code) {
        return getSubValueSets(scheme, version, code, true);
    }

// get sub value sets in scheme
    public JSONArray getSubValueSets(String scheme, String version, String code, boolean fromCache) {
        if (scheme == null)
            scheme = Constants.CODING_SCHEME_NAME;

        String retval = DataUtils.getCodingSchemeName(scheme);
        if (retval != null) {
            scheme = retval;
        }

        HashMap map = null;
        String key = scheme + "$" + version + "subvsd$" + code;
        JSONArray nodeArray = null;
        if (fromCache) {
            Element element = _cache.get(key);
            if (element != null) {
                nodeArray = (JSONArray) element.getValue();
            }
        }
        if (nodeArray == null) {
            _logger.debug("Not in cache -- calling getSubValueSets ");

            map = DataUtils.getValueSetHierarchy().getSubValueSets(scheme, code);
            nodeArray = hashMap2JSONArray(map);

            if (nodeArray != null && fromCache) {
                try {
                    Element element = new Element(key, nodeArray);
                    _cache.put(element);
                } catch (Exception ex) {

                }
            }
        } else {
            _logger.debug("Retrieved from cache.");
        }
        return nodeArray;
    }

    public JSONArray getSubValueSets(String code) {
        HashMap map = null;
        String key = "sub_vsd_uri_$" + code;
        boolean fromCache = true;
        JSONArray nodeArray = null;
        if (fromCache) {
            Element element = _cache.get(key);
            if (element != null) {
                nodeArray = (JSONArray) element.getValue();
            }
        }
        if (nodeArray == null) {
            _logger.debug("Not in cache -- calling getSubValueSets ");
            map = DataUtils.getValueSetHierarchy().getSubValueSets(code);
            nodeArray = hashMap2JSONArray(map);

            if (nodeArray != null && fromCache) {
				try {
					Element element = new Element(key, nodeArray);
					_cache.put(element);
				} catch (Exception ex) {

				}
		    }
        } else {
            _logger.debug("Retrieved from cache.");
        }
        return nodeArray;
    }



    public JSONArray getSubValueSets(String code, boolean fromCache) {
        HashMap map = null;
        String key = "sub_vsd$" + code;
        JSONArray nodeArray = null;
        if (fromCache) {
            Element element = _cache.get(key);
            if (element != null) {
                nodeArray = (JSONArray) element.getValue();
            }
        }
        if (nodeArray == null) {
            _logger.debug("Not in cache -- calling getSubValueSets ");
            map = DataUtils.getValueSetHierarchy().getSubValueSets(null, code);
            nodeArray = hashMap2JSONArray(map);

            if (nodeArray != null && fromCache) {
                try {
                    Element element = new Element(key, nodeArray);
                    _cache.put(element);
                } catch (Exception ex) {

                }
            }
        } else {
            _logger.debug("Retrieved from cache.");
        }
        return nodeArray;
    }

    /// find coding scheme specific Value Sets

    public JSONArray getRootValueSets(String scheme, String version) {
        return getRootValueSets(scheme, version, true);
    }

    public JSONArray getRootValueSets(String scheme, String version, boolean fromCache) {

        List list = null;// new ArrayList();
        String key = scheme + "$" + version + "$valueset" + "$root";
        JSONArray nodesArray = null;

        if (scheme == null)
            scheme = Constants.CODING_SCHEME_NAME;
        String retval = DataUtils.getCodingSchemeName(scheme);
        if (retval != null) {
            scheme = retval;
        }

        if (fromCache) {
            Element element = _cache.get(key);
            if (element != null) {
                nodesArray = (JSONArray) element.getValue();
            }
        }

        if (nodesArray == null) {
            _logger.debug("Not in cache -- calling ValueSetHierarchy.getRootValueSets " + scheme);
            try {
                HashMap hmap = DataUtils.getValueSetHierarchy().getRootValueSets(scheme);
                TreeItem root = (TreeItem) hmap.get("<Root>");
                nodesArray = new JSONArray();

				for (String association : root._assocToChildMap.keySet()) {

					 List<TreeItem> children = root._assocToChildMap.get(association);
					 for (TreeItem childItem : children) {

						 String code = childItem._code;
						 String name = childItem._text;

						 int childCount = 0;
						 if (childItem._expandable) childCount = 1;

						 //childItem._text = code + " (" + name + ")";

						 try {
							 JSONObject nodeObject = new JSONObject();
							 //nodeObject.put(ONTOLOGY_NODE_SCHEME, scheme);

							 //nodeObject.put(ONTOLOGY_NODE_ID, code);

							 nodeObject.put(ONTOLOGY_NODE_ID, scheme + "$" + code);

							 nodeObject.put(ONTOLOGY_NODE_NAME, name);
							 nodeObject.put(ONTOLOGY_NODE_CHILD_COUNT, childCount);
							 nodeObject.put(CHILDREN_NODES, new JSONArray());
							 nodesArray.put(nodeObject);

						 } catch (Exception ex) {
							 ex.printStackTrace();
						 }
					 }
                }

                //nodeArray = list2JSONArray(scheme, list);

                if (fromCache) {
                    Element element = new Element(key, nodesArray);
                    _cache.put(element);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            _logger.debug("Retrieved from cache.");
        }
        return nodesArray;
    }

    //build_cs_vs_tree
    public JSONArray getRootValueSets(boolean fromCache) {

        List list = null;// new ArrayList();
        String key = "cs_valuesetroots";
        JSONArray nodesArray = null;

        if (fromCache) {
            Element element = _cache.get(key);
            if (element != null) {
                nodesArray = (JSONArray) element.getValue();
            }
        }

        if (nodesArray == null) {
            try {
                HashMap hmap = DataUtils.getValueSetHierarchy().getRootValueSets();

                //ValueSetHierarchy.moveNCItToTop(hmap);

                TreeItem root = (TreeItem) hmap.get("<Root>");
                nodesArray = new JSONArray();

				for (String association : root._assocToChildMap.keySet()) {

					 List<TreeItem> children = root._assocToChildMap.get(association);
					 for (TreeItem childItem : children) {

						 String code = childItem._code;
						 String name = childItem._text;

						 int childCount = 0;
						 if (childItem._expandable) childCount = 1;

						 try {
							 JSONObject nodeObject = new JSONObject();
							 nodeObject.put(ONTOLOGY_NODE_ID, code);
							 nodeObject.put(ONTOLOGY_NODE_NAME, name);
							 nodeObject.put(ONTOLOGY_NODE_CHILD_COUNT, childCount);
							 nodeObject.put(CHILDREN_NODES, new JSONArray());
							 nodesArray.put(nodeObject);

						 } catch (Exception ex) {
							 ex.printStackTrace();
						 }
					 }
                }

                //nodeArray = list2JSONArray(scheme, list);

                if (fromCache) {
                    Element element = new Element(key, nodesArray);
                    _cache.put(element);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            _logger.debug("Retrieved from cache.");
        }
        return nodesArray;
    }

    // value set home page (Source view)

    public JSONArray build_src_vs_tree() {
		return getRootValueSets(true, true);
	}


    public JSONArray getRootValueSets(boolean fromCache, boolean bySource) {

        List list = null;// new ArrayList();
        String key = "src_valuesetroots";
        JSONArray nodesArray = null;

        if (fromCache) {
            Element element = _cache.get(key);
            if (element != null) {
                nodesArray = (JSONArray) element.getValue();
            }
        }

        if (nodesArray == null) {
            try {
                HashMap hmap = DataUtils.getValueSetHierarchy().build_src_vs_tree();

                TreeItem root = (TreeItem) hmap.get("<Root>");
                nodesArray = new JSONArray();

				for (String association : root._assocToChildMap.keySet()) {

					 List<TreeItem> children = root._assocToChildMap.get(association);
					 for (TreeItem childItem : children) {

						 String code = childItem._code;
						 String name = childItem._text;

                         if (childItem._expandable) {

							 int childCount = 1;
							 try {
								 JSONObject nodeObject = new JSONObject();
								 nodeObject.put(ONTOLOGY_NODE_ID, code);
								 nodeObject.put(ONTOLOGY_NODE_NAME, name);
								 nodeObject.put(ONTOLOGY_NODE_CHILD_COUNT, childCount);
								 nodeObject.put(CHILDREN_NODES, new JSONArray());
								 nodesArray.put(nodeObject);

							 } catch (Exception ex) {
								 ex.printStackTrace();
							 }

					     }


					 }
                }

                if (fromCache) {
                    Element element = new Element(key, nodesArray);
                    _cache.put(element);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            _logger.debug("Retrieved from cache.");
        }
        return nodesArray;
    }



    public JSONArray getRootConcepts(String scheme, String version) {
        return getRootConcepts(scheme, version, true);
    }

    public JSONArray getRootConcepts(String scheme, String version, boolean fromCache) {
        List list = null;// new ArrayList();
        String key = scheme + "$" + version + "$root";
        JSONArray nodeArray = null;

        if (scheme == null)
            scheme = Constants.CODING_SCHEME_NAME;
        String retval = DataUtils.getCodingSchemeName(scheme);
        if (retval != null) {
            scheme = retval;
            version = DataUtils.key2CodingSchemeVersion(scheme);
        }

        if (fromCache) {
            Element element = _cache.get(key);
            if (element != null) {
                // _logger.debug("getRootConcepts fromCache element != null returning list"
                // );
                nodeArray = (JSONArray) element.getValue();
            }
        }

        if (nodeArray == null) {
            _logger.debug("Not in cache -- calling getHierarchyRoots ");
            try {
                // list = new DataUtils().getHierarchyRoots(scheme, version,
                // null);
                LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
                list = new TreeUtils(lbSvc).getHierarchyRoots(scheme, version, null);
                nodeArray = list2JSONArray(scheme, list);

                if (fromCache) {
                    Element element = new Element(key, nodeArray);
                    _cache.put(element);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            _logger.debug("Retrieved from cache.");
        }
        return nodeArray;
    }



    private JSONArray list2JSONArray(String scheme, List list) {
        List newlist = new ArrayList();
        JSONArray nodesArray = null;
        try {
            if (list != null) {
                nodesArray = new JSONArray();
                for (int i = 0; i < list.size(); i++) {
                    Object obj = list.get(i);
                    String code = "";
                    String name = "";
                    if (obj instanceof ResolvedConceptReference) {
                        ResolvedConceptReference node =
                            (ResolvedConceptReference) obj;
                        code = node.getConceptCode();
                        try {
                            name = node.getEntityDescription().getContent();
                        } catch (Exception e) {
                            name = code;
                        }
                        if (name.compareTo("<Not assigned>") == 0)
                            name = code;
                        EntityDescription ed = new EntityDescription();
                        ed.setContent(name);
                        node.setEntityDescription(ed);
                        newlist.add(node);
                    } else if (obj instanceof Entity) {
                        Entity node = (Entity) obj;
                        code = node.getEntityCode();
                        try {
                            name = node.getEntityDescription().getContent();
                        } catch (Exception e) {
                            name = code;
                        }
                        if (name.compareTo("<Not assigned>") == 0)
                            name = code;
                        EntityDescription ed = new EntityDescription();
                        ed.setContent(name);
                        node.setEntityDescription(ed);
                        newlist.add(node);
                    }
                }
                SortUtils.quickSort(newlist);
				LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
		        TreeService treeService =
			        TreeServiceFactory.getInstance().getTreeService(lbSvc);

                for (int i = 0; i < newlist.size(); i++) {
                    Object obj = newlist.get(i);
                    ResolvedConceptReference node =
                        (ResolvedConceptReference) obj;

					String cs_name = node.getCodingSchemeName();
					String cs_version = node.getCodingSchemeVersion();
					String cs_ns = node.getCodeNamespace();
					String code = node.getConceptCode();
					String name = node.getEntityDescription().getContent();

/*
 			        boolean isExpandible = isExpandable(lbSvc, node.getCodingSchemeName(),
			                                         node.getCodingSchemeVersion(),
			                                         node.getConceptCode(),
			                                         node.getCodeNamespace());
*/
			        boolean isExpandible = isExpandable(treeService, cs_name, cs_version, code, cs_ns);
			        int childCount = 0;
                    if (isExpandible) {
						childCount = 1;
					}
/*


                    LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();

                    ArrayList sub_list =
                        new TreeUtils(lbSvc).getSubconceptNamesAndCodes(scheme, null, code);
                    if (sub_list.size() == 0)
                        childCount = 0;

*/

                    try {
                        JSONObject nodeObject = new JSONObject();
                        nodeObject.put(ONTOLOGY_NODE_ID, code);
                        nodeObject.put(ONTOLOGY_NODE_NAME, name);
                        nodeObject.put(ONTOLOGY_NODE_CHILD_COUNT, childCount);
                        nodeObject.put(CHILDREN_NODES, new JSONArray());
                        nodesArray.put(nodeObject);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return nodesArray;
    }



    /*
     * //HL7 fix private JSONArray list2JSONArray(List list) { JSONArray
     * nodesArray = null; try { if (list != null) { nodesArray = new
     * JSONArray(); for (int i=0; i<list.size(); i++) { Object obj =
     * list.get(i); String code = ""; String name = ""; if (obj instanceof
     * ResolvedConceptReference) { ResolvedConceptReference node =
     * (ResolvedConceptReference) obj; code = node.getConceptCode(); try { name
     * = node.getEntityDescription().getContent(); } catch (Exception e) { name
     * = code; } } else if (obj instanceof Concept) { Concept node = (Concept)
     * obj; code = node.getEntityCode(); try { name =
     * node.getEntityDescription().getContent(); } catch (Exception e) { name =
     * code; } }
     *
     * int j = i+1; _logger.debug("( " + j + ") code: " + code + " name: " +
     * name);
     *
     * if (name.compareTo("<Not assigned>") == 0) name = code;
     *
     * ResolvedConceptReference node = (ResolvedConceptReference) list.get(i);
     * int childCount = 1; try { JSONObject nodeObject = new JSONObject();
     * nodeObject.put(ONTOLOGY_NODE_ID, code);
     * nodeObject.put(ONTOLOGY_NODE_NAME, name);
     * nodeObject.put(ONTOLOGY_NODE_CHILD_COUNT, childCount);
     * nodeObject.put(CHILDREN_NODES, new JSONArray());
     * nodesArray.put(nodeObject);
     *
     * } catch (Exception ex) { ex.printStackTrace(); } } }
     *
     *
     * } catch (Exception ex) {
     *
     * } return nodesArray; }
     */


    public JSONArray expand_src_vs_tree(String node_id) {
		//JSONArray nodesArray = null;

        String key = "expand_src_vs_tree$" + node_id;
        JSONArray nodesArray = null;
        Element element = _cache.get(key);
        if (element != null) {
            nodesArray = (JSONArray) element.getValue();
        }

        if (nodesArray == null) {
            _logger.debug("Not in cache -- calling expand_src_vs_tree_exclude_src_nodes " + node_id);
            try {

				//HashMap hmap = ValueSetHierarchy.expand_src_vs_tree_exclude_src_nodes(node_id);
				HashMap hmap = DataUtils.getValueSetHierarchy().expand_src_vs_tree_exclude_src_nodes(node_id);

				nodesArray = hashMap2JSONArray(hmap);
                element = new Element(key, nodesArray);
                _cache.put(element);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            _logger.debug("Retrieved from cache.");
        }
        return nodesArray;
	}



    public JSONArray hashMap2JSONArray(HashMap hmap) {
        //JSONObject json = new JSONObject();
        JSONArray nodesArray = null;
        try {
            nodesArray = new JSONArray();
            Set keyset = hmap.keySet();
            Object[] objs = keyset.toArray();
            String code = (String) objs[0];

            TreeItem ti = (TreeItem) hmap.get(code);
            for (String association : ti._assocToChildMap.keySet()) {

                List<TreeItem> children = ti._assocToChildMap.get(association);
                // Collections.sort(children);
                for (TreeItem childItem : children) {
                    // printTree(childItem, focusCode, depth + 1);
                    JSONObject nodeObject = new JSONObject();
                    nodeObject.put(ONTOLOGY_NODE_ID, childItem._code);
                    nodeObject.put(ONTOLOGY_NODE_NS, childItem._ns);
                    nodeObject.put(ONTOLOGY_NODE_NAME, childItem._text);
                    int knt = 0;
                    if (childItem._expandable) {
                        knt = 1;
                    }
                    nodeObject.put(ONTOLOGY_NODE_CHILD_COUNT, knt);
                    nodesArray.put(nodeObject);
                }
            }
        } catch (Exception e) {
			e.printStackTrace();
        }
        return nodesArray;
    }

    public JSONArray getPathsToRoots(String scheme, String version, String code) {
        List list = null;// new ArrayList();
        String key = scheme + "$" + version + "$" + code + "$path";
        JSONArray nodeArray = null;
        Element element = _cache.get(key);
        if (element != null) {
            nodeArray = (JSONArray) element.getValue();
        }

        if (nodeArray == null) {
            _logger.debug("Not in cache -- calling getHierarchyRoots ");
            try {
                nodeArray = getPathsToRoots(scheme, version, code, true);
                element = new Element(key, nodeArray);
                _cache.put(element);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            _logger.debug("Retrieved from cache.");
        }
        return nodeArray;
    }

    public JSONArray getPathsToRoots(String ontology_display_name,
        String version, String node_id, boolean fromCache) {
        return getPathsToRoots(ontology_display_name, version, node_id,
            fromCache, -1);
    }

    public JSONArray getPathsToRoots(String ontology_display_name,
        String version, String node_id, boolean fromCache, int maxLevel) {
        if (ontology_display_name == null)
            ontology_display_name = Constants.CODING_SCHEME_NAME;

        String retval = DataUtils.getCodingSchemeName(ontology_display_name);
        if (retval != null) {
            ontology_display_name = retval;
            version = DataUtils.key2CodingSchemeVersion(ontology_display_name);
        }

        JSONArray rootsArray = null;
        if (maxLevel == -1) {
            rootsArray = getRootConcepts(ontology_display_name, version, false);
            try {
                LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
                TreeUtils util = new TreeUtils(lbSvc);
                HashMap hmap =
                    util.getTreePathData(ontology_display_name, version, null,
                        node_id);
                // _logger.debug("Calling util.getTreePathData2...");
                // HashMap hmap = util.getTreePathData2(ontology_display_name,
                // null, node_id, maxLevel);
                // util.printTree(hmap);

                Set keyset = hmap.keySet();
                Object[] objs = keyset.toArray();
                String code = (String) objs[0];
                TreeItem ti = (TreeItem) hmap.get(code); // TreeItem ti = new
                                                         // TreeItem("<Root>",
                                                         // "Root node");
                JSONArray nodesArray = getNodesArray(node_id, ti);
                replaceJSONObjects(rootsArray, nodesArray);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return rootsArray;
        } else {
            try {
				LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
                TreeUtils util = new TreeUtils(lbSvc);

                HashMap hmap =
                    util.getTreePathData(ontology_display_name, version, null,
                        node_id, maxLevel);
                // HashMap hmap = util.getTreePathData2(ontology_display_name,
                // null, node_id, maxLevel);
                // util.printTree(hmap);
                Object[] objs = hmap.keySet().toArray();
                String code = (String) objs[0];
                TreeItem ti = (TreeItem) hmap.get(code);
                List list = util.getTopNodes(ti);
                rootsArray = list2JSONArray(ontology_display_name, list);
                /*
                 * Set keyset = hmap.keySet(); Object[] objs = keyset.toArray();
                 * String code = (String) objs[0]; TreeItem ti = (TreeItem)
                 * hmap.get(code); //TreeItem ti = new TreeItem("<Root>",
                 * "Root node");
                 */
                // JSONArray nodesArray = getNodesArray(ti);
                JSONArray nodesArray = getNodesArray(node_id, ti);
                replaceJSONObjects(rootsArray, nodesArray);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return rootsArray;

        }
    }

    private void replaceJSONObject(JSONArray nodesArray, JSONObject obj) {
        String obj_id = null;
        try {
            obj_id = (String) obj.get(ONTOLOGY_NODE_ID);
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
        for (int i = 0; i < nodesArray.length(); i++) {
            String node_id = null;
            try {
                JSONObject node = nodesArray.getJSONObject(i);
                node_id = (String) node.get(ONTOLOGY_NODE_ID);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            if (obj_id.compareTo(node_id) == 0) {
                try {
                    nodesArray.put(i, obj);
                    break;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void replaceJSONObjects(JSONArray nodesArray, JSONArray nodesArray2) {
        for (int i = 0; i < nodesArray2.length(); i++) {
            try {
                JSONObject obj = nodesArray2.getJSONObject(i);
                replaceJSONObject(nodesArray, obj);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /*
     * private JSONArray getNodesArray(String node_id, TreeItem ti) { JSONArray
     * nodesArray = new JSONArray(); for (String association :
     * ti.assocToChildMap.keySet()) { List<TreeItem> children =
     * ti.assocToChildMap.get(association); SortUtils.quickSort(children); for
     * (int i=0; i<children.size(); i++) { TreeItem childItem = (TreeItem)
     * children.get(i); int knt = 0; if (childItem.expandable) { knt = 1; }
     * JSONObject nodeObject = new JSONObject(); try {
     * nodeObject.put(ONTOLOGY_NODE_ID, childItem.code);
     * nodeObject.put(ONTOLOGY_NODE_NAME, childItem.text);
     * nodeObject.put(ONTOLOGY_NODE_CHILD_COUNT, knt);
     * nodeObject.put(CHILDREN_NODES, getNodesArray(node_id, childItem));
     * nodesArray.put(nodeObject); } catch (Exception ex) {
     *
     * } } } return nodesArray; }
     */


    private int findFocusNodePosition(String node_id, List<TreeItem> children) {
        for (int i = 0; i < children.size(); i++) {
            TreeItem childItem = (TreeItem) children.get(i);
            if (node_id.compareTo(childItem._code) == 0)
                return i;
        }
        return -1;
    }

    // node_id: search_tree target (highlighted node)
    private JSONArray getNodesArray(String node_id, TreeItem ti) {
        JSONArray nodesArray = new JSONArray();
        for (String association : ti._assocToChildMap.keySet()) {
            List<TreeItem> children = ti._assocToChildMap.get(association);
            SortUtils.quickSort(children);

            int cut_off = 200;
            int m = cut_off / 2;
            int m2 = m / 2;

            if (children.size() <= cut_off) {
                for (int i = 0; i < children.size(); i++) {
                    TreeItem childItem = (TreeItem) children.get(i);
                    int knt = 0;
                    if (childItem._expandable) {
                        knt = 1;
                    }
                    JSONObject nodeObject = new JSONObject();
                    try {
                        nodeObject.put(ONTOLOGY_NODE_ID, childItem._code);
                        nodeObject.put(ONTOLOGY_NODE_NS, childItem._ns);
                        nodeObject.put(ONTOLOGY_NODE_NAME, childItem._text);
                        nodeObject.put(ONTOLOGY_NODE_CHILD_COUNT, knt);
                        nodeObject.put(CHILDREN_NODES, getNodesArray(node_id,
                            childItem));
                        nodesArray.put(nodeObject);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } else {
                int len = children.size();
                int min = 0;
                int max = len - 1;
                int pos = findFocusNodePosition(node_id, children);
                if (pos == -1) {
                    for (int i = 0; i < children.size(); i++) {
                        TreeItem childItem = (TreeItem) children.get(i);
                        int knt = 0;
                        if (childItem._expandable) {
                            knt = 1;
                        }
                        JSONObject nodeObject = new JSONObject();
                        try {
                            nodeObject.put(ONTOLOGY_NODE_ID, childItem._code);
                            nodeObject.put(ONTOLOGY_NODE_NS, childItem._ns);
                            nodeObject.put(ONTOLOGY_NODE_NAME, childItem._text);
                            nodeObject.put(ONTOLOGY_NODE_CHILD_COUNT, knt);
                            nodeObject.put(CHILDREN_NODES, getNodesArray(
                                node_id, childItem));
                            nodesArray.put(nodeObject);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                } else {
                    if (pos < m) {
                        min = 0;
                        max = m + 1;
                        if (max > len)
                            max = len;
                    } else {
                        if (pos - m2 > 0)
                            min = pos - m2;
                        if (pos + m2 < max)
                            max = pos + m2;
                    }

                    JSONObject nodeObject = null;
                    for (int i = min; i < max; i++) {
                        TreeItem childItem = (TreeItem) children.get(i);
                        int knt = 0;
                        if (childItem._expandable) {
                            knt = 1;
                        }
                        nodeObject = new JSONObject();
                        try {
                            nodeObject.put(ONTOLOGY_NODE_ID, childItem._code);
                            nodeObject.put(ONTOLOGY_NODE_NS, childItem._ns);
                            nodeObject.put(ONTOLOGY_NODE_NAME, childItem._text);
                            nodeObject.put(ONTOLOGY_NODE_CHILD_COUNT, knt);
                            nodeObject.put(CHILDREN_NODES, getNodesArray(
                                node_id, childItem));
                            nodesArray.put(nodeObject);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    nodeObject = new JSONObject();
                    try {
                        nodeObject.put(ONTOLOGY_NODE_ID, node_id);
                        nodeObject.put(ONTOLOGY_NODE_NAME,
                            "(Too many sibling nodes -- only " + m
                                + " of a total " + len + " are displayed.)");
                        nodeObject.put(ONTOLOGY_NODE_CHILD_COUNT, 0);
                        nodeObject.put(CHILDREN_NODES, new JSONArray());
                        nodesArray.put(nodeObject);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        return nodesArray;
    }




    private JSONArray toJSONArray(TreeItem ti) {
        JSONArray nodesArray = new JSONArray();
        for (String association : ti._assocToChildMap.keySet()) {
            List<TreeItem> children = ti._assocToChildMap.get(association);
			for (int i = 0; i < children.size(); i++) {
				TreeItem childItem = (TreeItem) children.get(i);
				int knt = 0;
				if (childItem._expandable) {
					knt = 1;
				}
				JSONObject nodeObject = new JSONObject();
				try {
					nodeObject.put(ONTOLOGY_NODE_ID, childItem._code);
					nodeObject.put(ONTOLOGY_NODE_NS, childItem._ns);
					nodeObject.put(ONTOLOGY_NODE_NAME, childItem._text);
					nodeObject.put(ONTOLOGY_NODE_CHILD_COUNT, knt);
					nodeObject.put(CHILDREN_NODES, toJSONArray(childItem));
					nodesArray.put(nodeObject);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return nodesArray;
	}

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Tree Extension Code:
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static String getSubConcepts(String codingScheme,
        CodingSchemeVersionOrTag versionOrTag, String code) {

	    String version = null;
	    if (versionOrTag != null && versionOrTag.getVersion() != null) version = versionOrTag.getVersion();


        if (!CacheController.getInstance().containsKey(getSubConceptKey(
            codingScheme, version, code))) {
            _logger.debug("SubConcepts Not Found In Cache.");
            TreeService treeService =
                TreeServiceFactory.getInstance().getTreeService(
                    RemoteServerUtil.createLexBIGService());

            LexEvsTreeNode node =
                TreeServiceFactory.getInstance().getTreeService(
                    RemoteServerUtil.createLexBIGService()).getSubConcepts(
                    codingScheme, versionOrTag, code);

            String json =
                treeService.getJsonConverter().buildChildrenNodes(node);

            _cache.put(new Element(getSubConceptKey(codingScheme, version, code), json));
            return json;
        }
        return (String) _cache.get(getSubConceptKey(codingScheme, version, code))
            .getObjectValue();
    }

/*
    public static String getTree(String codingScheme,
        CodingSchemeVersionOrTag versionOrTag, String code, String namespace) {
		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
		String json = new ViewInHierarchyUtils(lbSvc).getTree(codingScheme, versionOrTag.getVersion(), code, namespace);
		return json;
    }
*/

    public static String getTree(String codingScheme,
        CodingSchemeVersionOrTag versionOrTag, String code, String namespace) {
        if (!CacheController.getInstance()
            .containsKey(getTreeKey(codingScheme, code))) {
            _logger.debug("Tree Not Found In Cache.");

            TreeService treeService =
                TreeServiceFactory.getInstance().getTreeService(
                    RemoteServerUtil.createLexBIGService());

            LexEvsTree tree = null;
            if (StringUtils.isNullOrBlank(namespace)) {
				tree = treeService.getTree(codingScheme, versionOrTag, code);
			} else {
				tree = treeService.getTree(codingScheme, versionOrTag, code, namespace);
			}

            String json =
                treeService.getJsonConverter().buildJsonPathFromRootTree(
                    tree.getCurrentFocus());

            _cache.put(new Element(getTreeKey(tree, versionOrTag.getVersion()), json));
            return json;
        }
        return (String) _cache.get(getTreeKey(codingScheme, versionOrTag.getVersion(), code))
            .getObjectValue();
    }



    public static String getTree(String codingScheme,
        CodingSchemeVersionOrTag versionOrTag, String code) {
		return getTree(codingScheme, versionOrTag, code, null);
	}

    public void activeCacheTree(ResolvedConceptReference ref) {
        _logger.debug("Actively caching tree.");

        String codingScheme = ref.getCodingSchemeName();
        String codingSchemeVersion = ref.getCodingSchemeVersion();
        String code = ref.getCode();

        if (!CacheController.getInstance()
            .containsKey(getTreeKey(codingScheme, code))) {
            _logger.debug("Tree Not Found In Cache.");
            TreeService treeService =
                TreeServiceFactory.getInstance().getTreeService(
                    RemoteServerUtil.createLexBIGService());

            //LexEvsTree tree = treeService.getTree(codingScheme, null, code);
            CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
            if (codingSchemeVersion != null) {
				versionOrTag.setVersion(codingSchemeVersion);
			}
            LexEvsTree tree = treeService.getTree(codingScheme, versionOrTag, code);
            if (tree == null) return;
            String json =
                treeService.getJsonConverter().buildJsonPathFromRootTree(
                    tree.getCurrentFocus());

           // _cache.put(new Element(getTreeKey(tree), json));
           String treeKey = getTreeKey(tree, codingSchemeVersion);
           //if (treeKey == null) return;

           _cache.put(new Element(treeKey, json));
        }
    }
/*
    private static String getTreeKey(LexEvsTree tree) {
        return getTreeKey(tree.getCodingScheme(), tree.getCurrentFocus()
            .getCode());
    }
*/

    private static String getTreeKey(LexEvsTree tree, String version) {
        return getTreeKey(tree.getCodingScheme(), version, tree.getCurrentFocus()
            .getCode());
    }

    private static String getSubConceptKey(String codingScheme, String code) {
        return String.valueOf("SubConcept".hashCode() + codingScheme.hashCode()
            + code.hashCode());
    }

    private static String getSubConceptKey(String codingScheme, String version, String code) {
		if (version == null) return getSubConceptKey(codingScheme, code);
        return String.valueOf("SubConcept".hashCode() + codingScheme.hashCode()
            + version.hashCode()
            + code.hashCode());
    }

    private static String getTreeKey(String codingScheme, String code) {
        return String.valueOf("Tree".hashCode() + codingScheme.hashCode()
            + code.hashCode());
    }


    private static String getTreeKey(String codingScheme, String version, String code) {
        return String.valueOf("Tree".hashCode() + codingScheme.hashCode()
            + version.hashCode()
            + code.hashCode());
    }


    private JSONArray getNodesArray(TreeItem ti) {
        JSONArray nodesArray = new JSONArray();
        for (String association : ti._assocToChildMap.keySet()) {
            List<TreeItem> children = ti._assocToChildMap.get(association);
            SortUtils.quickSort(children);

			for (int i = 0; i < children.size(); i++) {
				TreeItem childItem = (TreeItem) children.get(i);
				int knt = 0;
				if (childItem._expandable) {
					knt = 1;
				}
				JSONObject nodeObject = new JSONObject();
				try {
					nodeObject.put(ONTOLOGY_NODE_ID, childItem._code);
					nodeObject.put(ONTOLOGY_NODE_NS, childItem._ns);
					nodeObject.put(ONTOLOGY_NODE_NAME, childItem._text);
					nodeObject.put(ONTOLOGY_NODE_CHILD_COUNT, knt);
					nodeObject.put(CHILDREN_NODES, getNodesArray(childItem));
					nodesArray.put(nodeObject);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
        return nodesArray;
    }



    public JSONArray getSourceValueSetTree (String scheme, String version, boolean fromCache) {
        List list = null;// new ArrayList();
        String key = "src_vs_tree";
        JSONArray nodeArray = null;

        // retval = DataUtils.getCodingSchemeName(scheme);
        /*
        if (retval != null) {
            scheme = retval;
            version = DataUtils.key2CodingSchemeVersion(scheme);
        }
        */

        if (fromCache) {
            Element element = _cache.get(key);
            if (element != null) {
                nodeArray = (JSONArray) element.getValue();
                _logger.debug("Retrieved source value set tree from cache.");
                return nodeArray;
            }
        }

        HashMap map = null;
		_logger.debug("Not in cache -- calling getSubValueSets ");

		//map = ValueSetHierarchy.getSourceValueSetTree(scheme, version);
		//map = ValueSetHierarchy.getSourceValueSetTree();
		map = DataUtils.getValueSetHierarchy().getSourceValueSetTree();

		TreeItem root = (TreeItem) map.get("<Root>");
		nodeArray = toJSONArray(root);

		if (nodeArray != null && fromCache) {
			try {
				Element element = new Element(key, nodeArray);
				_cache.put(element);
			} catch (Exception ex) {
                ex.printStackTrace();
			}
		}

        return nodeArray;
	}



    public JSONArray getCodingSchemeValueSetTree (String scheme, String version, boolean fromCache) {
        List list = null;// new ArrayList();
        String key = "cs_vs_tree";
        JSONArray nodeArray = null;
        String retval = DataUtils.getCodingSchemeName(scheme);
        if (retval != null) {
            scheme = retval;
            version = DataUtils.key2CodingSchemeVersion(scheme);
        }

        if (fromCache) {
            Element element = _cache.get(key);
            if (element != null) {
                nodeArray = (JSONArray) element.getValue();
                _logger.debug("Retrieved terminology value set tree from cache.");
                return nodeArray;
            }
        }

        HashMap map = null;
		_logger.debug("Not in cache -- calling getCodingSchemeValueSetTree ");

		//map = ValueSetHierarchy.getCodingSchemeValueSetTree(scheme, version);
		map = DataUtils.getValueSetHierarchy().getCodingSchemeValueSetTree(scheme, version);

		TreeItem root = (TreeItem) map.get("<Root>");
		nodeArray = toJSONArray(root);

		if (nodeArray != null && fromCache) {
			try {
				Element element = new Element(key, nodeArray);
				_cache.put(element);
			} catch (Exception ex) {
                ex.printStackTrace();
			}
		}

        return nodeArray;
	}

////////////////////////////////////////////////////////////////////////////////////////////

    public ResolvedConceptReferenceList getHierarchyRoots(
        String codingScheme, String version) {
		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
		return new TreeUtils(lbSvc).getHierarchyRoots(codingScheme, version);
	}


	public boolean isExpandable(TreeService treeService, String scheme, String version, String code, String namespace) {
		CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
		if (version != null) {
			versionOrTag.setVersion(version);
		}
		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
		LexEvsTreeNode node = treeService.getSubConcepts(scheme, versionOrTag, code, namespace);
        if (node != null && node.getExpandableStatus().toString().compareTo("IS_EXPANDABLE") == 0) return true;
        return false;
	}

    public String getRootJSONString(String codingScheme, String version) {
		String key = codingScheme + "$" + version + "$root";
		Element element = _cache.get(key);
		if (element != null) {
			return (String) element.getValue();
		}

		long ms = System.currentTimeMillis();
		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
		ResolvedConceptReferenceList rcrl = getHierarchyRoots(codingScheme, version);
            System.out.println("getHierarchyRoots run time (milliseconds): "
                + (System.currentTimeMillis() - ms));

		TreeService treeService =
			TreeServiceFactory.getInstance().getTreeService(lbSvc);

		ms = System.currentTimeMillis();
		TreeItem root = new TreeItem("Root", "<Root>");

        Vector w = new Vector();
        for (int i=0; i<rcrl.getResolvedConceptReferenceCount(); i++) {
			ResolvedConceptReference rcr = rcrl.getResolvedConceptReference(i);
			w.add(rcr);
		}

		w = SortUtils.quickSort(w);
        for (int i=0; i<w.size(); i++) {
			ResolvedConceptReference rcr = (ResolvedConceptReference) w.elementAt(i);
			String cs_name = rcr.getCodingSchemeName();
			String cs_version = rcr.getCodingSchemeVersion();
			String cs_ns = rcr.getCodeNamespace();
			String code = rcr.getConceptCode();
			String name = rcr.getEntityDescription().getContent();
			boolean is_expandable = isExpandable(treeService, cs_name, cs_version, code, cs_ns);
			TreeItem child = new TreeItem(code, name, cs_ns, null);
			child._expandable = is_expandable;
			root.addChild("has_child", child);
		}

            System.out.println("TreeItem run time (milliseconds): "
                + (System.currentTimeMillis() - ms));

		ms = System.currentTimeMillis();
		String json = JSON2TreeItem.treeItem2Json(root);

            System.out.println("treeItem2Json run time (milliseconds): "
                + (System.currentTimeMillis() - ms));

		//json = "{\"root_nodes\":" + json + "}";

		try {
			element = new Element(key, json);
			_cache.put(element);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return json;
	}

//    public static JSONObject TreeItem2JSONObject(TreeItem ti) {

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
		Element element = _cache.get(key);
		if (element != null) {
			JSONArray jsonArray = (JSONArray) element.getValue();
			return jsonArray;
		}

		long ms = System.currentTimeMillis();
		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();

        boolean useNamespace = false;
        if (ns == null) {
			ns = new ConceptDetails(lbSvc).getNamespaceByCode(codingScheme, version, code);
			if (ns != null) {
				useNamespace = true;
			}
		}

		TreeService treeService =
			TreeServiceFactory.getInstance().getTreeService(lbSvc);

		TreeItem root = new TreeItem("Root", "<Root>");
		if (code.compareTo("<Root>") == 0) {
			return null;
		}
        RelationshipUtils relUtils = new RelationshipUtils(lbSvc);

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
            boolean is_expandable = isExpandable(treeService, codingScheme, version, child_code, child_ns);
			TreeItem child = new TreeItem(child_code, child_name, child_ns, null);
			child._expandable = is_expandable;
			treeItem_list.add(child);
		}

		ms = System.currentTimeMillis();
		//String json = JSON2TreeItem.treeItem2Json(root);

		//treeItem_list = SortUtils.quickSort(treeItem_list);

		JSONArray nodeArray = treeItemList2JSONArray(treeItem_list);
        /*
		try {
			element = new Element(key, json);
			_cache.put(element);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		*/
		return nodeArray;
	}



    public String getSubconceptJSONString(String codingScheme, String version, String code, String ns) {

        String key = codingScheme + "$" + version + "$" + code + "$" + ns;
		Element element = _cache.get(key);
		if (element != null) {
			String json = (String) element.getValue();
			return json;
		}

		long ms = System.currentTimeMillis();
		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();

        boolean useNamespace = false;
        if (ns == null) {
			ns = new ConceptDetails(lbSvc).getNamespaceByCode(codingScheme, version, code);
			if (ns != null) {
				useNamespace = true;
			}
		}

		TreeService treeService =
			TreeServiceFactory.getInstance().getTreeService(lbSvc);

		TreeItem root = new TreeItem("Root", "<Root>");
		if (code.compareTo("<Root>") == 0) {
			return null;
		}
        RelationshipUtils relUtils = new RelationshipUtils(lbSvc);

        List options = relUtils.createOptionList(false, true, false, false, false, false);

        HashMap relMap = relUtils.getRelationshipHashMap(codingScheme, version, code, ns, useNamespace, options);
        List list = (ArrayList) relMap.get("type_subconcept");

        Vector w = new Vector();
        for (int i=0; i<list.size(); i++) {
			String t = (String) list.get(i);
			w.add(t);
		}
		w = SortUtils.quickSort(w);
        for (int i=0; i<w.size(); i++) {
		    String t = (String) w.elementAt(i);
			Vector u = StringUtils.parseData(t);
			String child_name = (String) u.elementAt(0);
			String child_code = (String) u.elementAt(1);
			String child_ns = (String) u.elementAt(2);
            boolean is_expandable = isExpandable(treeService, codingScheme, version, child_code, child_ns);
			TreeItem child = new TreeItem(child_code, child_name, child_ns, null);
			child._expandable = is_expandable;
			root.addChild("has_child", child);
		}

		ms = System.currentTimeMillis();
		String json = JSON2TreeItem.treeItem2Json(root);

		try {
			element = new Element(key, json);
			_cache.put(element);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
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
}
