package gov.nih.nci.evs.browser.utils;

import gov.nih.nci.evs.browser.common.*;

import java.io.*;
import java.net.URI;
import java.text.*;
import java.util.*;
import java.sql.*;

import gov.nih.nci.system.client.ApplicationServiceProvider;
import org.LexGrid.LexBIG.DataModel.Collections.AbsoluteCodingSchemeVersionReferenceList;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.LexBIG.caCore.interfaces.LexEVSDistributed;
import org.LexGrid.naming.Mappings;
import org.LexGrid.naming.SupportedCodingScheme;
import org.LexGrid.util.PrintUtility;
import org.LexGrid.valueSets.DefinitionEntry;
import org.LexGrid.valueSets.EntityReference;
import org.LexGrid.valueSets.PropertyMatchValue;
import org.LexGrid.valueSets.PropertyReference;
import org.LexGrid.valueSets.ValueSetDefinition;
import org.LexGrid.valueSets.types.DefinitionOperator;
import org.lexgrid.valuesets.LexEVSValueSetDefinitionServices;
import org.lexgrid.valuesets.dto.ResolvedValueSetDefinition;

import org.LexGrid.LexBIG.DataModel.Collections.*;
import org.LexGrid.LexBIG.DataModel.Core.*;
import org.LexGrid.LexBIG.Exceptions.*;
import org.LexGrid.LexBIG.History.*;
import org.LexGrid.LexBIG.LexBIGService.*;
import org.LexGrid.LexBIG.Utility.*;
import org.LexGrid.concepts.*;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.*;
import org.LexGrid.LexBIG.Utility.Iterators.*;
import org.LexGrid.codingSchemes.*;
import org.LexGrid.commonTypes.*;
import org.LexGrid.relations.Relations;
import org.LexGrid.versions.*;
import org.LexGrid.naming.*;
import org.LexGrid.LexBIG.DataModel.Core.types.*;
import org.LexGrid.LexBIG.Extensions.Generic.*;


import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.Direction;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.MappingSortOption;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.MappingSortOptionName;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.QualifierSortOption;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.LexBIG.caCore.interfaces.LexEVSDistributed;

import org.LexGrid.LexBIG.Extensions.Generic.SupplementExtension;
import org.LexGrid.relations.AssociationPredicate;

import org.LexGrid.LexBIG.caCore.interfaces.LexEVSDistributed;
import org.lexgrid.valuesets.LexEVSValueSetDefinitionServices;

import org.LexGrid.valueSets.ValueSetDefinition;
import org.LexGrid.commonTypes.Source;


import org.apache.log4j.*;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.Mapping;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.Mapping.SearchContext;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.caCore.interfaces.LexEVSApplicationService;
import org.lexgrid.valuesets.impl.LexEVSValueSetDefinitionServicesImpl;

import org.json.*;

public class ValueSetHierarchy {
	private AbsoluteCodingSchemeVersionReferenceList _csVersionList = null;
    private Logger _logger = Logger.getLogger(ValueSetHierarchy.class);

    public final LocalNameList _noopList = Constructors.createLocalNameList("_noop_");
    public String SOURCE_SCHEME = Constants.TERMINOLOGY_VALUE_SET_NAME;//"Terminology_Value_Set.owl";
    public String SOURCE_VERSION = null;

    private HashSet _valueSetParticipationHashSet = null;
    private HashMap _source_hierarchy = null;
    private HashMap _source_subconcept_map = null;
    private HashMap _source_superconcept_map = null;
    private HashMap _valueSetDefinitionHierarchyHashMap = null;
    private Vector  _availableValueSetDefinitionSources = null;
    private Vector  _valueSetDefinitionHierarchyRoots = null;
    private HashMap _valueSetDefinitionSourceCode2Name_map = null;
    private HashMap _vsd_source_to_vsds_map = null;
    private HashMap _valueSetDefinitionURI2VSD_map = null;
    private HashMap _cs2vsdURIs_map = null;
    private HashMap _subValueSet_hmap = null;
    //private HashMap vsdURI2CodingSchemeURNs = null;

    private Set vocabularyNameSet = null;
    private HashMap localName2FormalNameHashMap = null;
    private HashMap codingSchemeName2URIHashMap = null;

    private LexBIGService lbSvc = null;
    private LexEVSValueSetDefinitionServices vsd_service = null;
    private HashMap _rootValueSets = null;

    private CodingSchemeDataUtils codingSchemeDataUtils = null;

    private TreeUtils treeUtils = null;

    private List valueSetDefinitionURIList = null;

    private static String INVERSE_IS_A = "inverse_is_a";

    private HashMap _vsUri2ParticipationCS_map = null;

    public ValueSetHierarchy(LexBIGService lbSvc,
                             LexEVSValueSetDefinitionServices vsd_service) {
		this.lbSvc = lbSvc;
		this.vsd_service = vsd_service;

		codingSchemeDataUtils = new CodingSchemeDataUtils(lbSvc);
		treeUtils = new TreeUtils(lbSvc);

		try {
			valueSetDefinitionURIList = this.vsd_service.listValueSetDefinitionURIs();
		} catch (Exception ex) {
            System.out.println("ERROR: vsd_service.listValueSetDefinitionURIs() failed.");
		}

        initialize();
    }


    public ValueSetHierarchy(LexBIGService lbSvc,
                             LexEVSValueSetDefinitionServices vsd_service,
                             HashMap localName2FormalNameHashMap,
                             HashMap codingSchemeName2URIHashMap) {
		this.lbSvc = lbSvc;
		this.vsd_service = vsd_service;

		codingSchemeDataUtils = new CodingSchemeDataUtils(lbSvc);
		treeUtils = new TreeUtils(lbSvc);

		try {
			valueSetDefinitionURIList = vsd_service.listValueSetDefinitionURIs();
		} catch (Exception ex) {
            System.out.println("ERROR: vsd_service.listValueSetDefinitionURIs() failed.");
		}

		this.localName2FormalNameHashMap = localName2FormalNameHashMap;
		if (localName2FormalNameHashMap != null) {
			this.vocabularyNameSet = localName2FormalNameHashMap.keySet();
		}
        this.codingSchemeName2URIHashMap = codingSchemeName2URIHashMap;
        initialize();
    }


    public void setCodingSchemeName2URIHashMap(HashMap codingSchemeName2URIHashMap) {
		this.codingSchemeName2URIHashMap = codingSchemeName2URIHashMap;
	}

    private void initialize() {
		long ms = System.currentTimeMillis();
		if (localName2FormalNameHashMap == null || codingSchemeName2URIHashMap == null) {
			initializeHashMaps();
		}
		//vsdURI2CodingSchemeURNs = getVsdURI2CodingSchemeURNs();
		_valueSetDefinitionSourceCode2Name_map = getCodeHashMap(SOURCE_SCHEME, SOURCE_VERSION);
        _valueSetDefinitionURI2VSD_map = createValueSetDefinitionURI2VSD_map();
	    _availableValueSetDefinitionSources = findAvailableValueSetDefinitionSources();
	    constructVsUri2ParticipationCS_map();
		_source_hierarchy = getValueSetSourceHierarchy(SOURCE_SCHEME, SOURCE_VERSION);
	    _vsd_source_to_vsds_map = createVSDSource2VSDsMap();
		_subValueSet_hmap = getSubValueSetsFromSourceCodingScheme();
		_rootValueSets = getRootValueSets();
		System.out.println("Total ValueSetHierarchy initalization run time (ms): " + (System.currentTimeMillis() - ms));
	}


    public HashMap getValueSetDefinitionSourceCode2Name_map() {
		return _valueSetDefinitionSourceCode2Name_map;
	}

    public HashMap getSource_hierarchy() {
		return _source_hierarchy;
	}

    public HashMap getVsd_source_to_vsds_map() {
		return _vsd_source_to_vsds_map;
	}

    public HashMap getSubValueSet_hmap() {
		return _subValueSet_hmap;
	}


	public String getFormalName(String name) {
		if (localName2FormalNameHashMap == null) {
			return null;
		}
		if (!localName2FormalNameHashMap.containsKey(name)) {
			return null;
		}
		return (String) localName2FormalNameHashMap.get(name);
	}

	public String getCodingSchemeURN(String formalName) {
		if (codingSchemeName2URIHashMap == null) {
			return null;
		}
		if (!codingSchemeName2URIHashMap.containsKey(formalName)) {
			return null;
		}
		return (String) codingSchemeName2URIHashMap.get(formalName);
	}


    public HashMap get_valueSetDefinitionSourceCode2Name_map() {
		return _valueSetDefinitionSourceCode2Name_map;
	}


    public String getValueSetDecription(String uri) {
		ValueSetDefinition vsd = (ValueSetDefinition) _valueSetDefinitionURI2VSD_map.get(uri);
		if (vsd == null) {
		    return null;
		}
		if (vsd.getEntityDescription() == null) {
			return null;
		}
		return vsd.getEntityDescription().getContent();
	}


    public HashSet get_valueSetParticipationHashSet() {
		return _valueSetParticipationHashSet;
	}


    public Vector getValueSetDecriptionSources(String uri) {
		Vector source_vec = new Vector();
		ValueSetDefinition vsd = (ValueSetDefinition) _valueSetDefinitionURI2VSD_map.get(uri);
		if (vsd == null) return null;
		java.util.Enumeration<? extends Source> sourceEnum = vsd.enumerateSource();

		while (sourceEnum.hasMoreElements()) {
			Source source = (Source) sourceEnum.nextElement();
			String src_str = source.getContent();
			source_vec.add(src_str);
		}

		return source_vec;
	}



    public String getSourceSchemeVersion() {
		return SOURCE_VERSION;
	}


//===========================================================================================================================
// Value Set Hierarchy
//===========================================================================================================================


    public ResolvedConceptReferenceList getValueSetHierarchyRoots() {
		return codingSchemeDataUtils.getHierarchyRoots("Terminology Value Set", null);
	}


    public HashMap getValueSetDefinitionURI2VSD_map() {
		return _valueSetDefinitionURI2VSD_map;
	}


    public HashMap createValueSetDefinitionURI2VSD_map() {
		_valueSetDefinitionURI2VSD_map = new HashMap();
		try {
			String valueSetDefinitionRevisionId = null;
			List list = valueSetDefinitionURIList;
			for (int i=0; i<list.size(); i++) {
				String uri = (String) list.get(i);
			    ValueSetDefinition vsd = vsd_service.getValueSetDefinition(new URI(uri), valueSetDefinitionRevisionId);
			    _valueSetDefinitionURI2VSD_map.put(uri, vsd);
		    }
		    return _valueSetDefinitionURI2VSD_map;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;

	}

/*
    public ValueSetDefinition findValueSetDefinitionByURI(String uri) {

        if (_valueSetDefinitionURI2VSD_map == null) {
			_valueSetDefinitionURI2VSD_map = new HashMap();
		}
		if (_valueSetDefinitionURI2VSD_map.containsKey(uri)) {
			return (ValueSetDefinition) _valueSetDefinitionURI2VSD_map.get(uri);
		}

		if (uri == null) return null;
	    if (uri.indexOf("|") != -1) {
			Vector u = StringUtils.parseData(uri);
			uri = (String) u.elementAt(1);
		}

		String valueSetDefinitionRevisionId = null;
		try {
			ValueSetDefinition vsd = vsd_service.getValueSetDefinition(new URI(uri), valueSetDefinitionRevisionId);
			if (vsd != null) {
				_valueSetDefinitionURI2VSD_map.put(uri, vsd);
				return vsd;
		    }
		} catch (Exception ex) {
			//ex.printStackTrace();
		}
		return null;
	}
*/
    public ValueSetDefinition findValueSetDefinitionByURI(String uri) {
		if (uri == null) return null;
		return (ValueSetDefinition) _valueSetDefinitionURI2VSD_map.get(uri);
	}

    public Vector getValueSetDefinitionsBySource(String source) {

		if (_availableValueSetDefinitionSources != null) {
			if (!_availableValueSetDefinitionSources.contains(source)) return null;
		}

		Vector v = new Vector();
        List list = valueSetDefinitionURIList;
        if (list == null) return null;
        for (int i=0; i<list.size(); i++) {
			String uri = (String) list.get(i);
			ValueSetDefinition vsd = findValueSetDefinitionByURI(uri);
			java.util.Enumeration<? extends Source> sourceEnum = vsd.enumerateSource();
            boolean found = false;
			while (sourceEnum.hasMoreElements()) {
				Source src = (Source) sourceEnum.nextElement();
				String src_str = src.getContent();
				if (src_str.compareTo(source) == 0) {
					v.add(vsd);
					break;
				}
			}
		}
		return v;
	}


    public Vector findAvailableValueSetDefinitionSources() {
		Vector v = new Vector();
		HashSet hset = new HashSet();
		List list = valueSetDefinitionURIList;
        if (list == null) return null;
        for (int i=0; i<list.size(); i++) {
			String uri = (String) list.get(i);
			ValueSetDefinition vsd = findValueSetDefinitionByURI(uri);
			java.util.Enumeration<? extends Source> sourceEnum = vsd.enumerateSource();

			while (sourceEnum.hasMoreElements()) {
				Source src = (Source) sourceEnum.nextElement();
				String src_str = src.getContent();
				if (!hset.contains(src_str)) {
					hset.add(src_str);
					v.add(src_str);
				}
			}
		}
		return v;
	}


    public Vector getAvailableValueSetDefinitionSources() {
		if (_availableValueSetDefinitionSources != null) return _availableValueSetDefinitionSources;
		_availableValueSetDefinitionSources = findAvailableValueSetDefinitionSources();
		return _availableValueSetDefinitionSources;

	}


    public String getValueSetDefinitionMetadata(ValueSetDefinition vsd) {
		if (vsd== null) return null;
		String name = "";
		String uri = "";
		String description = "";
		String domain = "";
		String src_str = "";
		StringBuffer buf = new StringBuffer();

		uri = vsd.getValueSetDefinitionURI();
		name = vsd.getValueSetDefinitionName();
		if (name == null || name.compareTo("") == 0) {
			name = "<NOT ASSIGNED>";
		}

		domain = vsd.getConceptDomain();
		if (domain == null || domain.compareTo("") == 0) {
			domain = "<NOT ASSIGNED>";
		}

		java.util.Enumeration<? extends Source> sourceEnum = vsd.enumerateSource();

		while (sourceEnum.hasMoreElements()) {
			Source src = (Source) sourceEnum.nextElement();
			//src_str = src_str + src.getContent() + ";";
			buf.append(src.getContent() + ";");
		}
		src_str = buf.toString();

		if (src_str.length() > 0) {
			src_str = src_str.substring(0, src_str.length()-1);
		}

		if (src_str == null || src_str.compareTo("") == 0) {
			src_str = "<NOT ASSIGNED>";
		}

		if (vsd.getEntityDescription() != null) {
			description = vsd.getEntityDescription().getContent();
			if (description == null || description.compareTo("") == 0) {
				description = "<NO DESCRIPTION>";
			}
		} else {
			description = "<NO DESCRIPTION>";
		}

		return name + "|" + uri + "|" + description + "|" + domain + "|" + src_str;
	}





    public HashMap getSubconcepts(String scheme, String version, String code) {
        return treeUtils.getSubconcepts(scheme, version, code);
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
                    JSONObject nodeObject = new JSONObject();
                    nodeObject.put(Constants.ONTOLOGY_NODE_ID, childItem._code);
                    nodeObject.put(Constants.ONTOLOGY_NODE_NS, childItem._ns);
                    nodeObject.put(Constants.ONTOLOGY_NODE_NAME, childItem._text);
                    int knt = 0;
                    if (childItem._expandable) {
                        knt = 1;
                    }
                    nodeObject.put(Constants.ONTOLOGY_NODE_CHILD_COUNT, knt);
                    nodesArray.put(nodeObject);
                }
            }
        } catch (Exception e) {

        }
        return nodesArray;
    }

    public String getNodeLabel(TreeItem ti) {
		if (ti == null) return null;
		if (ti._text.compareTo(ti._code) == 0) return ti._text;
		return ti._text + " (" + ti._code + ")";
	}


    public void printTree(HashMap hmap) {
        if (hmap == null) {
            return;
        }
        Object[] objs = hmap.keySet().toArray();
        String code = (String) objs[0];
        TreeItem ti = (TreeItem) hmap.get(code);
        printTree(ti, code, 0);
    }

    public void printTree(TreeItem ti, String focusCode, int depth) {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<depth; i++) {
			buf.append("\t");
		}
		String indent = buf.toString();
        for (String association : ti._assocToChildMap.keySet()) {
            List<TreeItem> children = ti._assocToChildMap.get(association);
            Collections.sort(children);
            for (TreeItem childItem : children) {
				System.out.println(indent + "[" + getNodeLabel(ti) + "] --> (" + association + ") --> [" + getNodeLabel(childItem) + "]");
                printTree(childItem, focusCode, depth + 1);
			}
        }
    }


    public List getChildrenNodes(TreeItem ti) {
        List list = new ArrayList();
        getChildrenNodes(ti, list, 0, 1);
        return list;
    }

    public void getChildrenNodes(TreeItem ti, List list, int currLevel, int maxLevel) {
        if (list == null)
            list = new ArrayList();
        if (currLevel > maxLevel)
            return;
        if (ti._assocToChildMap.keySet().size() > 0) {
            if (ti._text.compareTo("Root node") != 0) {
                ResolvedConceptReference rcr = new ResolvedConceptReference();
                rcr.setConceptCode(ti._code);
                EntityDescription entityDescription = new EntityDescription();
                entityDescription.setContent(ti._text);
                rcr.setEntityDescription(entityDescription);
                // _logger.debug("Root: " + ti.text);
                list.add(rcr);
            }
        }

        for (String association : ti._assocToChildMap.keySet()) {
            List<TreeItem> children = ti._assocToChildMap.get(association);
            Collections.sort(children);
            for (TreeItem childItem : children) {
                getChildrenNodes(childItem, list, currLevel + 1, maxLevel);
            }
        }
    }


    public List getSubconceptList(String scheme, String version, String code) {
		return treeUtils.getSubconceptNamesAndCodes(scheme, version, code);
	}

	public void populateChildrenNodes(String scheme, String version, TreeItem ti, HashSet visited_nodes) {
        if (visited_nodes.contains(ti._code)) {
			return;
		}

		visited_nodes.add(ti._code);
		List subconcepts = getSubconceptList(scheme, version, ti._code);
		ti._expandable = false;
		if (subconcepts.size() == 0) {
			return;
		}
		ti._expandable = true;
		List<TreeItem> children = new ArrayList();
		for (int lcv=0; lcv<subconcepts.size(); lcv++) {
			String nv = (String) subconcepts.get(lcv);
			Vector u = StringUtils.parseData(nv);
			String name = (String) u.elementAt(0);
			String code = (String) u.elementAt(1);
			TreeItem children_node = new TreeItem(code, name);
			populateChildrenNodes(scheme, version, children_node, visited_nodes);
			children.add(children_node);
		}
		new SortUtils().quickSort(children);
		ti.addAll(INVERSE_IS_A, children);
	}

    public boolean hasSubSourceInSourceHierarchy(String src) {
		//if (_source_subconcept_map == null) preprocessSourceHierarchyData();
		if (_source_subconcept_map.containsKey(src)) {
			return true;
		}
		return false;
	}

    public boolean hasValueSetsInSource(String src) {
		//if (_source_subconcept_map == null) preprocessSourceHierarchyData();
		if (_source_subconcept_map.containsKey(src)) {
			return true;
		}
		return false;
	}


    public void preprocessSourceHierarchyData() {
		//_source_hierarchy = getValueSetSourceHierarchy(SOURCE_SCHEME, SOURCE_VERSION);

		_source_subconcept_map = new HashMap();
		populateSubconceptHashMap(_source_hierarchy, _source_subconcept_map);//, root);

		_source_superconcept_map = new HashMap();
		populateSuperconceptHashMap(_source_hierarchy, _source_superconcept_map);//, root);

	}

	public HashMap getValueSetSourceHierarchy() {
		return getValueSetSourceHierarchy(SOURCE_SCHEME, SOURCE_VERSION);
	}



	public HashMap getValueSetSourceHierarchy(String scheme, String version) {
		if (_source_hierarchy != null) return _source_hierarchy;

		HashMap hmap = new HashMap();
		//_valueSetDefinitionSourceCode2Name_map = getCodeHashMap(scheme, version);

		// value set source coding scheme
		ResolvedConceptReferenceList roots = codingSchemeDataUtils.getHierarchyRoots(scheme, version);

		HashSet visited_nodes = new HashSet();

		TreeItem ti = new TreeItem("<Root>", "Root node");
		ti._expandable = false;
		if (roots != null) {
			for (int i=0; i<roots.getResolvedConceptReferenceCount(); i++) {
				ResolvedConceptReference rcr = roots.getResolvedConceptReference(i);
				TreeItem root_node = new TreeItem(rcr.getConceptCode(), rcr.getEntityDescription().getContent());

				populateChildrenNodes(scheme, version, root_node, visited_nodes);
				ti.addChild(INVERSE_IS_A, root_node);
				ti._expandable = true;
			}
		}
		hmap.put("<Root>", ti);

		_source_subconcept_map = new HashMap();
		populateSubconceptHashMap(hmap, _source_subconcept_map);//, root);

		_source_superconcept_map = new HashMap();
		populateSuperconceptHashMap(hmap, _source_superconcept_map);//, root);

		return hmap;
	}

    //hmap HashMap for quick access to child codes
    public void populateSubconceptHashMap(HashMap hmap, TreeItem ti) {

        for (String association : ti._assocToChildMap.keySet()) {
            List<TreeItem> children = ti._assocToChildMap.get(association);

            for (TreeItem childItem : children) {

				if(hmap.containsKey(ti._code)) {
					Vector v = (Vector) hmap.get(ti._code);
					if (!v.contains(childItem._code)) {
						v.add(childItem._code);
						hmap.put(ti._code, v);
					}
				} else {
					Vector v = new Vector();
					v.add(childItem._code);
					hmap.put(ti._code, v);
				}

				populateSubconceptHashMap(hmap, childItem);
		    }
		}
	}

    //hmap HashMap for quick access to parent codes
    public void populateSuperconceptHashMap(HashMap hmap, TreeItem ti) {

        for (String association : ti._assocToChildMap.keySet()) {
            List<TreeItem> children = ti._assocToChildMap.get(association);
            String superconcept_key = ti._code;
            for (TreeItem childItem : children) {
                String subconcept_key = childItem._code;
				if(hmap.containsKey(subconcept_key)) {
					Vector v = (Vector) hmap.get(subconcept_key);
					if (!v.contains(superconcept_key)) {
						v.add(superconcept_key);
						hmap.put(subconcept_key, v);
					}
				} else {
					Vector v = new Vector();
					v.add(superconcept_key);
					hmap.put(subconcept_key, v);
				}

				populateSuperconceptHashMap(hmap, childItem);
		    }
		}
	}


    public void populateSuperconceptHashMap(HashMap source_hierarchy, HashMap hmap) {
		TreeItem root = (TreeItem) source_hierarchy.get("<Root>");
        for (String association : root._assocToChildMap.keySet()) {
            List<TreeItem> children = root._assocToChildMap.get(association);
            for (TreeItem childItem : children) {
            	populateSuperconceptHashMap(hmap, childItem);
			}
		}
	}

    public void populateSubconceptHashMap(HashMap source_hierarchy, HashMap hmap) {
		TreeItem root = (TreeItem) source_hierarchy.get("<Root>");
        for (String association : root._assocToChildMap.keySet()) {
            List<TreeItem> children = root._assocToChildMap.get(association);
            for (TreeItem childItem : children) {
            	populateSubconceptHashMap(hmap, childItem);
			}
		}
	}




    public HashMap getCodeHashMap(String scheme, String version) {
        try {
            CodingSchemeVersionOrTag versionOrTag =
                new CodingSchemeVersionOrTag();
            versionOrTag.setVersion(version);

            CodedNodeSet cns = null;

            try {
                cns = lbSvc.getCodingSchemeConcepts(scheme, versionOrTag);
                if (cns == null) return null;
            } catch (Exception e1) {
                e1.printStackTrace();
                return null;
            }

            HashMap hmap = new HashMap();
            try {
                //LocalNameList propertyNames = null;
                SortOptionList sortOptions = null;
                LocalNameList filterOptions = null;
                boolean resolveObjects = false; // needs to be set to true
                int maxToReturn = -1;

                ResolvedConceptReferenceList rcrl =
                    cns.resolveToList(sortOptions, filterOptions,
                        null, null, resolveObjects,
                        maxToReturn);
                Vector v = new Vector();
                if (rcrl.getResolvedConceptReferenceCount() > 0) {
                    for (int i = 0; i < rcrl.getResolvedConceptReferenceCount(); i++) {
                        ResolvedConceptReference rcr =
                            rcrl.getResolvedConceptReference(i);
                        v.add(rcr.getConceptCode() + "|" + rcr.getEntityDescription().getContent());
                        hmap.put(rcr.getConceptCode(), rcr.getEntityDescription().getContent());
					}
				}
				return hmap;
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

   public Vector getCodingSchemeURNsInValueSetDefinition(String uri) {
	    Vector v = new Vector();
		try {
			java.net.URI valueSetDefinitionURI = new URI(uri);
	        ValueSetDefinition vsd = vsd_service.getValueSetDefinition(valueSetDefinitionURI, null);
	        Mappings mappings = vsd.getMappings();
            SupportedCodingScheme[] supportedCodingSchemes = mappings.getSupportedCodingScheme();
            for (int i=0; i<supportedCodingSchemes.length; i++) {
				SupportedCodingScheme supportedCodingScheme = supportedCodingSchemes[i];
				v.add(supportedCodingScheme.getUri());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			//System.out.println("Total run time (ms): " + (System.currentTimeMillis() - ms));
		}
		return new SortUtils().quickSort(v);
   }

   public Vector getCodingSchemeURNsInValueSetDefinition(LexEVSValueSetDefinitionServices vsd_service, String uri) {
	    Vector v = new Vector();
		try {
			java.net.URI valueSetDefinitionURI = new URI(uri);
	        ValueSetDefinition vsd = vsd_service.getValueSetDefinition(valueSetDefinitionURI, null);
	        Mappings mappings = vsd.getMappings();
            SupportedCodingScheme[] supportedCodingSchemes = mappings.getSupportedCodingScheme();
            for (int i=0; i<supportedCodingSchemes.length; i++) {
				SupportedCodingScheme supportedCodingScheme = supportedCodingSchemes[i];
				v.add(supportedCodingScheme.getUri());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			//System.out.println("Total run time (ms): " + (System.currentTimeMillis() - ms));
		}
		return new SortUtils().quickSort(v);
   }


    public HashMap getValueSetDefinitionNodesWithSource(String src) {
		//createVSDSource2VSDsMap();

		//String text = (String) _valueSetDefinitionSourceCode2Name_map.get(src);
		//TreeItem root = new TreeItem(src, src + " (" + text + ")");
		TreeItem root = new TreeItem(src, src);
		root._expandable = false;
		List <TreeItem> children = new ArrayList();
		Vector v = getValueSetDefinitionsWithSource(src);
		if (v != null) {
			for (int i=0; i<v.size(); i++) {
				ValueSetDefinition vsd = (ValueSetDefinition) v.elementAt(i);
				TreeItem ti = new TreeItem(vsd.getValueSetDefinitionURI(), vsd.getValueSetDefinitionName());
				ti._expandable = false;
				children.add(ti);
			}
			if (v.size() > 0) {
				root._expandable = true;
			}
	    }
	    new SortUtils().quickSort(children);
		root.addAll(INVERSE_IS_A, children);
		HashMap hmap = new HashMap();
		hmap.put("<Root>", root);
        return hmap;

	}


    public HashMap createVSDSource2VSDsMap() {

		HashMap vsd_source_to_vsds_map = new HashMap();
		//LexEVSValueSetDefinitionServices vsd_service = RemoteServerUtil.getLexEVSValueSetDefinitionServices();
		List list = valueSetDefinitionURIList;

		for (int i=0; i<list.size(); i++) {
			String uri = (String) list.get(i);
			ValueSetDefinition vsd = findValueSetDefinitionByURI(uri);

			String vsd_uri = vsd.getValueSetDefinitionURI();
			java.util.Enumeration<? extends Source> sourceEnum = vsd.enumerateSource();

			while (sourceEnum.hasMoreElements()) {
				Source source = (Source) sourceEnum.nextElement();
				String src_str = source.getContent();

				Vector vsd_vec = new Vector();
				if (vsd_source_to_vsds_map.containsKey(src_str)) {
					vsd_vec = (Vector) vsd_source_to_vsds_map.get(src_str);
				}
				boolean found = false;
				for (int j=0; j<vsd_vec.size(); j++) {
					ValueSetDefinition next_vsd = (ValueSetDefinition) vsd_vec.elementAt(j);
					if (next_vsd.getValueSetDefinitionURI().compareTo(vsd_uri) == 0) {
						found = true;
						break;
					}
				}
				if (!found) {
					vsd_vec.add(vsd);
				}
				vsd_source_to_vsds_map.put(src_str, vsd_vec);
			}
		}
		return vsd_source_to_vsds_map;

	}



	public boolean hasValueSetDefinitionsWithSource(String src) {
		if (_vsd_source_to_vsds_map.containsKey(src)) {
			return true;
		}
		return false;
	}


	public Vector getValueSetDefinitionsWithSource(String src) {
		return (Vector) _vsd_source_to_vsds_map.get(src);
	}






	public HashMap getRootValueSets(boolean bySource) {
		if (!bySource) return getRootValueSets();
		//HashMap source_hier = getValueSetSourceHierarchy();
        Vector source_vec = new Vector();
        HashSet source_set = new HashSet();

        HashMap source2VSD_map = new HashMap();
        HashMap uri2VSD_map = new HashMap();

		//Vector v = new Vector();
		//LexEVSValueSetDefinitionServices vsd_service = RemoteServerUtil.getLexEVSValueSetDefinitionServices();
        List list = valueSetDefinitionURIList;

        for (int i=0; i<list.size(); i++) {
			String uri = (String) list.get(i);
			ValueSetDefinition vsd = findValueSetDefinitionByURI(uri);
			uri2VSD_map.put(uri, vsd);
			java.util.Enumeration<? extends Source> sourceEnum = vsd.enumerateSource();
			while (sourceEnum.hasMoreElements()) {
				Source src = (Source) sourceEnum.nextElement();
				String src_str = src.getContent();
				if (!source_set.contains(src_str)) {
					source_set.add(src_str);
					source_vec.add(src_str);
				}

				if (source2VSD_map.containsKey(src_str)) {
					Vector vsd_v = (Vector) source2VSD_map.get(src_str);
					if (!vsd_v.contains(uri)) {
						vsd_v.add(uri);
					}
					source2VSD_map.put(src_str, vsd_v);
				} else {
					Vector vsd_v = new Vector();
					vsd_v.add(uri);
					source2VSD_map.put(src_str, vsd_v);
				}
			}
		}

		TreeItem root = new TreeItem("<Root>", "Root node");
        List <TreeItem> children = new ArrayList();
        //Vector root_source_vec = new Vector();
		for (int k=0; k<source_vec.size(); k++) {
			String src = (String) source_vec.elementAt(k);
			// check has children
			boolean has_children = false;
			if (_source_subconcept_map.containsKey(src)) {
				Vector sub_vec = (Vector) _source_subconcept_map.get(src);
				for (int j=0; j<source_vec.size(); j++) {
					String source = (String) source_vec.elementAt(j);
					if (source.compareTo(src) != 0 && sub_vec.contains(source)) {
						has_children = true;
						break;
					}
				}

			}


			boolean has_parent = false;
			if (_source_superconcept_map.containsKey(src)) {
				Vector super_vec = (Vector) _source_superconcept_map.get(src);
				for (int j=0; j<source_vec.size(); j++) {
					String source = (String) source_vec.elementAt(j);
					if (source.compareTo(src) != 0 && super_vec.contains(source)) {
						has_parent = true;
						break;
					}
				}
			}

			// orphan
			if (!has_children && !has_parent) {
				// find all VSDs has the source
				Vector vsd_v = (Vector) source2VSD_map.get(src);
				for(int i=0; i<vsd_v.size(); i++) {
					//ValueSetDefinition vsd = (ValueSetDefinition) vsd_v.elementAt(i);
					String vsd_uri = (String) vsd_v.elementAt(i);
					ValueSetDefinition vsd = (ValueSetDefinition) uri2VSD_map.get(vsd_uri);

					String name = vsd.getValueSetDefinitionName();
					if (name == null || name.compareTo("") == 0) {
						name = "<NOT ASSIGNED>";
					}

                    String text = (String) _valueSetDefinitionSourceCode2Name_map.get(src);
					//TreeItem ti = new TreeItem(src, src + " (" + text + ")");
					TreeItem ti = new TreeItem(src, src);
					ti._expandable = false;
					children.add(ti);
				}

			} else if (has_children && !has_parent) {
				Vector vsd_v = (Vector) source2VSD_map.get(src);
				for(int i=0; i<vsd_v.size(); i++) {
					//ValueSetDefinition vsd = (ValueSetDefinition) vsd_v.elementAt(i);
					String vsd_uri = (String) vsd_v.elementAt(i);
					ValueSetDefinition vsd = (ValueSetDefinition) uri2VSD_map.get(vsd_uri);

					String name = vsd.getValueSetDefinitionName();
					if (name == null || name.compareTo("") == 0) {
						name = "<NOT ASSIGNED>";
					}

                    String text = (String) _valueSetDefinitionSourceCode2Name_map.get(src);
					//TreeItem ti = new TreeItem(src, src + " (" + text + ")");
					TreeItem ti = new TreeItem(src, src);
					ti._expandable = true;
					children.add(ti);
				}
			}
		}
		root._expandable = false;
		new SortUtils().quickSort(children);
		root.addAll(INVERSE_IS_A, children);

		HashMap hmap = new HashMap();
		hmap.put("<Root>", root);
        return hmap;
	}



    public AbsoluteCodingSchemeVersionReferenceList getAbsoluteCodingSchemeVersionReferenceList() {
        if (_valueSetParticipationHashSet == null) {
			_valueSetParticipationHashSet = getValueSetParticipationHashSet();
		}

		if (_csVersionList != null) return _csVersionList;


		AbsoluteCodingSchemeVersionReferenceList csVersionList = new AbsoluteCodingSchemeVersionReferenceList();
        try {
            if (lbSvc == null) return null;
            CodingSchemeRenderingList csrl = null;
            try {
                csrl = lbSvc.getSupportedCodingSchemes();
                if (csrl == null) return null;
            } catch (LBInvocationException ex) {
                ex.printStackTrace();
                return csVersionList;
            }
            CodingSchemeRendering[] csrs = csrl.getCodingSchemeRendering();
            for (int i = 0; i < csrs.length; i++) {
                int j = i + 1;
                CodingSchemeRendering csr = csrs[i];

                CodingSchemeSummary css = csr.getCodingSchemeSummary();
                String formalname = css.getFormalName();

                Boolean isActive = null;

                if (csr.getRenderingDetail() == null) {
                } else if (csr.getRenderingDetail().getVersionStatus() == null) {
                } else {
                    isActive =
                        csr.getRenderingDetail().getVersionStatus().equals(
                            CodingSchemeVersionStatus.ACTIVE);
                }

                String representsVersion = css.getRepresentsVersion();
                boolean includeInactive = false;

                if ((includeInactive && isActive == null)
                    || (isActive != null && isActive.equals(Boolean.TRUE))
                    || (includeInactive && (isActive != null && isActive
                        .equals(Boolean.FALSE)))) {


                    CodingSchemeVersionOrTag vt =
                        new CodingSchemeVersionOrTag();
                    vt.setVersion(representsVersion);

                    try {
                        CodingScheme cs =
                            lbSvc.resolveCodingScheme(formalname, vt);

                        String cs_uri = cs.getCodingSchemeURI();
                        if (_valueSetParticipationHashSet.contains(cs_uri)) {
							AbsoluteCodingSchemeVersionReference csv_ref = new AbsoluteCodingSchemeVersionReference();
							csv_ref.setCodingSchemeURN(cs.getCodingSchemeURI());
							csv_ref.setCodingSchemeVersion(representsVersion);
							csVersionList.addAbsoluteCodingSchemeVersionReference(csv_ref);
						}
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // return null;
        }
        return csVersionList;
    }


    //build_cs_vs_tree
	public HashMap getRootValueSets() {
        if (_valueSetParticipationHashSet == null) {
            _valueSetParticipationHashSet = getValueSetParticipationHashSet();
		}
        Vector root_cs_vec = new Vector();
        Iterator it = _valueSetParticipationHashSet.iterator();
        while (it.hasNext()) {
			String cs = (String) it.next();
			String formalName = getFormalName(cs);
			root_cs_vec.add(formalName);
		}
		root_cs_vec = new SortUtils().quickSort(root_cs_vec);
		TreeItem root = new TreeItem("<Root>", "Root node");

        List <TreeItem> children = new ArrayList();
if (root_cs_vec != null) {

        root_cs_vec = new SortUtils().quickSort(root_cs_vec);
        Vector sorted_root_cs_vec = new Vector();
        for (int i=0; i<root_cs_vec.size(); i++) {
			String cs = (String) root_cs_vec.elementAt(i);

			if (cs != null && cs.compareTo("NCI Thesaurus") == 0) {
				sorted_root_cs_vec.add(cs);
				break;
			}
		}

        for (int i=0; i<root_cs_vec.size(); i++) {
			String cs = (String) root_cs_vec.elementAt(i);
			if (cs != null && cs.compareTo("NCI Thesaurus") != 0) {
				sorted_root_cs_vec.add(cs);
			}
		}

		root_cs_vec = sorted_root_cs_vec;

        for (int i=0; i<root_cs_vec.size(); i++) {
			String cs = (String) root_cs_vec.elementAt(i);
			String code = cs;
			TreeItem ti = new TreeItem(code, cs);
			ti._expandable = true;
			children.add(ti);
		}
        //new SortUtils().quickSort(children);
		root.addAll(INVERSE_IS_A, children);

		HashMap hmap = new HashMap();
		hmap.put("<Root>", root);

        return hmap;
}

        return null;
	}



	public HashSet getValueSetParticipationHashSet() {
		if (_valueSetParticipationHashSet != null) return _valueSetParticipationHashSet;
        HashSet valueSetParticipationHashSet = new HashSet();
		//if (_valueSetParticipationHashSet != null) return _valueSetParticipationHashSet;
		//LexEVSValueSetDefinitionServices vsd_service = RemoteServerUtil.getLexEVSValueSetDefinitionServices();
		try {
			List list = valueSetDefinitionURIList;
            int k = 0;
			for (int i=0; i<list.size(); i++) {
				String uri = (String) list.get(i);
				k = i+1;
				//System.out.println("(" + k + ") " + uri);
				Vector cs_vec = getCodingSchemeURNsInValueSetDefinition(uri);
				for (int j=0; j<cs_vec.size(); j++) {
					String cs = (String) cs_vec.elementAt(j);
					if (!valueSetParticipationHashSet.contains(cs)) {
						valueSetParticipationHashSet.add(cs);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return valueSetParticipationHashSet;
	}



	public boolean hasValueSet(String cs_name) {
	    if (_valueSetParticipationHashSet == null) {
	    	_valueSetParticipationHashSet = getValueSetParticipationHashSet();
		}

		String scheme = getFormalName(cs_name);
		scheme = getCodingSchemeURN(scheme);

		//KLO, 062213
        if (_valueSetParticipationHashSet == null) return false;
		boolean retval = _valueSetParticipationHashSet.contains(scheme);
		return retval;
    }



    public CodedNodeSet getNodeSet(String scheme, String version) {
		CodedNodeSet cns = null;
		CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
		if (version != null) versionOrTag.setVersion(version);
		try {
			cns = lbSvc.getCodingSchemeConcepts(scheme, versionOrTag);
			CodedNodeSet.AnonymousOption restrictToAnonymous = CodedNodeSet.AnonymousOption.NON_ANONYMOUS_ONLY;
			cns = cns.restrictToAnonymous(restrictToAnonymous);
	    } catch (Exception ex) {
			ex.printStackTrace();
		}
 	    return cns;
    }


    public Vector getCodesInNodeSet(CodedNodeSet cns) {
		if (cns == null) return null;
        Vector v = new Vector();

        SortOptionList sortOptions = null;
        LocalNameList filterOptions = null;
        LocalNameList propertyNames = null;
        CodedNodeSet.PropertyType[] propertyTypes = null;
        boolean resolveObjects = false;
        int maxToReturn = -1;
    	ResolvedConceptReferenceList rcrl = null;
    	try {
			rcrl = cns.resolveToList(sortOptions, filterOptions, propertyNames, propertyTypes,
				 resolveObjects, maxToReturn);
			for (int i=0; i<rcrl.getResolvedConceptReferenceCount(); i++) {
				ResolvedConceptReference rcr = rcrl.getResolvedConceptReference(i);
				v.add(rcr.getConceptCode());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return v;
	}


    private HashMap getSubValueSetsFromSourceCodingScheme() {
		HashMap subValueSet_hmap = new HashMap();

        CodedNodeSet cns = getNodeSet(SOURCE_SCHEME, SOURCE_VERSION);
        if (cns == null) return null;

        Vector codes = getCodesInNodeSet(cns);
        for (int i=0; i<codes.size(); i++) {
			String code = (String) codes.elementAt(i);
			try {
				HashMap code_hmap = treeUtils.getSubconcepts(SOURCE_SCHEME, SOURCE_VERSION, code);
				if (code_hmap != null) {
					subValueSet_hmap.put(code, code_hmap);
				}
			} catch (Exception e) {
                System.out.println("ERROR -- getSubValueSetsFromSourceCodingScheme throws exception.");
			}
		}
		return subValueSet_hmap;
    }


    public HashMap getSubValueSetsFromSourceCodingScheme(String code) {
		if (_subValueSet_hmap == null) {
			_subValueSet_hmap = getSubValueSetsFromSourceCodingScheme();
		}
		if (_subValueSet_hmap == null) {
			return null;
		}
		if (_subValueSet_hmap.containsKey(code)) {
			return ((HashMap) _subValueSet_hmap.get(code));
		}
		return null;
	}





    // code: parent vsd_uri
	public HashMap getSubValueSets(String vsd_uri) {
		//HashMap source_hier = getValueSetSourceHierarchy();
		//createVSDSource2VSDsMap();
		Vector sub_src_vec = new Vector();
		HashSet hset = new HashSet();

		ValueSetDefinition root_vsd = findValueSetDefinitionByURI(vsd_uri);
		TreeItem root = new TreeItem(root_vsd.getValueSetDefinitionURI(), root_vsd.getValueSetDefinitionName());

		java.util.Enumeration<? extends Source> sourceEnum = root_vsd.enumerateSource();

		while (sourceEnum.hasMoreElements()) {
			Source source = (Source) sourceEnum.nextElement();
			String src = source.getContent();

			if (_source_subconcept_map.containsKey(src)) {
				Vector sub_vec = (Vector) _source_subconcept_map.get(src);
				for (int j=0; j<sub_vec.size(); j++) {
					String sub_src = (String) sub_vec.elementAt(j);
					if (!hset.contains(sub_src)) {
						hset.add(sub_src);
						sub_src_vec.add(sub_src);
					}
				}

			}
		}


		List<TreeItem> children = new ArrayList();
		HashSet sub_vsd_uri_hset = new HashSet();
		for (int i=0; i<sub_src_vec.size(); i++) {
			String sub_str = (String) sub_src_vec.elementAt(i);
			if (_vsd_source_to_vsds_map.containsKey(sub_str)) {
				Vector sub_vsd_vec = (Vector) _vsd_source_to_vsds_map.get(sub_str);
				for (int k=0; k<sub_vsd_vec.size(); k++) {
					ValueSetDefinition sub_vsd = (ValueSetDefinition) sub_vsd_vec.elementAt(k);
					if (!sub_vsd_uri_hset.contains(sub_vsd.getValueSetDefinitionURI())) {
						sub_vsd_uri_hset.add(sub_vsd.getValueSetDefinitionURI());
						//create child node
						TreeItem childnode = new TreeItem(sub_vsd.getValueSetDefinitionURI(), sub_vsd.getValueSetDefinitionName());
						children.add(childnode);
					}
				}
			}
		}

		if (sub_vsd_uri_hset.size() > 0) {
			root._expandable = true;
		}
		new SortUtils().quickSort(children);
		root.addAll(INVERSE_IS_A, children);

		HashMap hmap = new HashMap();
		hmap.put("<Root>", root);
        return hmap;
	}


    public void assignTreeNodeExpandable(HashMap hmap) {
		Iterator it = hmap.entrySet().iterator();
		while (it.hasNext()) {
			Entry entry = (Entry) it.next();
			String key = (String) entry.getKey();
			TreeItem ti = (TreeItem) entry.getValue();

			for (String association : ti._assocToChildMap.keySet()) {
				List<TreeItem> children = ti._assocToChildMap.get(association);
				for (TreeItem childItem : children) {

					String code = childItem._code;
					String text = childItem._text;
					childItem._expandable = false;

					if (hasSubSourceInSourceHierarchy(code)) {
						childItem._expandable = true;
					} else if (hasValueSetDefinitionsWithSource(code)) {
						childItem._expandable = true;
					}

				}

			}
		}
	}

    public void assignValueSetNodeExpandable(HashMap hmap) {
		Iterator it = hmap.entrySet().iterator();
		while (it.hasNext()) {
			Entry entry = (Entry) it.next();
			String key = (String) entry.getKey();
			TreeItem ti = (TreeItem) entry.getValue();
			for (String association : ti._assocToChildMap.keySet()) {
				List<TreeItem> children = ti._assocToChildMap.get(association);
				for (TreeItem childItem : children) {
					String code = childItem._code;
					String text = childItem._text;

					childItem._expandable = false;
					Vector source_vec = getValueSetDecriptionSources(code);
					for (int i=0; i<source_vec.size(); i++) {
						String source = (String) source_vec.elementAt(i);
						if (hasSubSourceInSourceHierarchy(source)) {
							childItem._expandable = true;
							break;
						}
					}
				}
			}
		}
	}


///////////////////////////////////////////////////////////////////////////////////////////////////
// Standards (Source) View Tree
///////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean isTerminologyNode(String node_id) {
	   return false;
	}

    public boolean isValueSetSourceNode(String node_id) {
		/*
       if (_valueSetDefinitionSourceCode2Name_map == null) {
		   _valueSetDefinitionSourceCode2Name_map = getCodeHashMap(SOURCE_SCHEME, SOURCE_VERSION);
	   }
	   */
	   if (_valueSetDefinitionSourceCode2Name_map.containsKey(node_id)) return true;
	   return false;
	}

    public boolean isValueSetNode(String node_id) {
	   if (_valueSetDefinitionURI2VSD_map.containsKey(node_id)) return true;
	   return false;
	}

    public boolean isTerminologyNodeExpandable(String node_id) {
	   return false;
	}

    public boolean isValueSetSourceNodeExpandable(String node_id) {
	   return false;
	}

    public boolean isValueSetNodeExpandable(String node_id) {
	   return false;
	}


    public boolean isNodeExpandable(String node_id) {
	   if (isTerminologyNode(node_id)) {
	       if (containsValueSets(node_id)) return true;
	       return false;
       } else if (isValueSetSourceNode(node_id)) {
	       if (containsValueSets(node_id)) return true;
	       return false;
       } else if (isValueSetNode(node_id)) {

	       return false;
       }
       return false;
    }

    public boolean hasChildren(HashMap hmap) {
/*
		Iterator it = hmap.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			TreeItem ti = (TreeItem) hmap.get(key);
*/
		Iterator it = hmap.entrySet().iterator();
		while (it.hasNext()) {
			Entry entry = (Entry) it.next();
			String key = (String) entry.getKey();
			TreeItem ti = (TreeItem) entry.getValue();

			int k = 0;
			for (String association : ti._assocToChildMap.keySet()) {
				List<TreeItem> children = ti._assocToChildMap.get(association);
				if (children.size() > 0) return true;
			}
		}
		return false;
	}


    public boolean containsValueSets(String src_str) {
		Vector v = getVSDRootsBySource(src_str);
		if (v == null || v.size() == 0) return false;
        return true;
	}



	public HashMap build_src_vs_tree() {
        ResolvedConceptReferenceList rcrl = null;//TreeUtils.getHierarchyRoots(SOURCE_SCHEME, SOURCE_VERSION);
        int count = 0;
        try {
			rcrl = treeUtils.getHierarchyRoots(SOURCE_SCHEME, SOURCE_VERSION);
			if (rcrl == null) {
				//System.out.println("*** TreeUtils.getHierarchyRoots returns NULL???: ");
			} else {
				count = rcrl.getResolvedConceptReferenceCount();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

		TreeItem root = new TreeItem("<Root>", "Root node");
        List <TreeItem> children = new ArrayList();

        for (int i=0; i<count; i++) {
			ResolvedConceptReference rcr = rcrl.getResolvedConceptReference(i);
			String src = rcr.getConceptCode();
			int j = i+1;
			String text = rcr.getEntityDescription().getContent();
			TreeItem ti = new TreeItem(src, text);
			ti._expandable = containsValueSets(src);
			children.add(ti);
		}
		new SortUtils().quickSort(children);
		root.addAll(INVERSE_IS_A, children);

		HashMap hmap = new HashMap();
		hmap.put("<Root>", root);
        return hmap;
	}


	public HashMap build_src_vs_tree_exclude_src_nodes() {
        ResolvedConceptReferenceList rcrl = treeUtils.getHierarchyRoots(SOURCE_SCHEME, SOURCE_VERSION);
        if (rcrl == null) {
			return null;
		}

		TreeItem root = new TreeItem("<Root>", "Root node");
		HashSet hset = new HashSet();
        List <TreeItem> children = new ArrayList();

        for (int i=0; i<rcrl.getResolvedConceptReferenceCount(); i++) {
			ResolvedConceptReference rcr = rcrl.getResolvedConceptReference(i);
			String src = rcr.getConceptCode();

			Vector vsd_root_vec = getVSDRootsBySource(src);
			if (vsd_root_vec != null) {
				for (int j=0; j<vsd_root_vec.size(); j++) {
					ValueSetDefinition vsd = (ValueSetDefinition) vsd_root_vec.elementAt(j);
					if (!hset.contains(vsd.getValueSetDefinitionURI())) {
						hset.add(vsd.getValueSetDefinitionURI());
						TreeItem ti = new TreeItem(vsd.getValueSetDefinitionURI(), vsd.getValueSetDefinitionName());
                        Vector sub_vsd_vec = getVSDChildrenNodesBySource(vsd.getValueSetDefinitionURI());
                        if (sub_vsd_vec != null && sub_vsd_vec.size() > 0) {
							ti._expandable = true;
						}
						children.add(ti);
					}
				}
			}
		}
		new SortUtils().quickSort(children);
		root.addAll(INVERSE_IS_A, children);
		HashMap hmap = new HashMap();
		hmap.put("<Root>", root);
		hset.clear();
        return hmap;
	}


    public HashMap expand_src_vs_tree(String node_id) {
		HashMap hmap = null;
		if (isValueSetSourceNode(node_id)) {

			hmap = getValueSetDefinitionNodesWithSource(node_id);
			boolean has_children = hasChildren(hmap);

			if (has_children) {
				assignValueSetNodeExpandable(hmap);

				return hmap;
			}

			hmap = getSubValueSetsFromSourceCodingScheme(node_id);

			if (hmap != null) {
				assignTreeNodeExpandable(hmap);
				TreeUtils.relabelTreeNodes(hmap);
			}
			return hmap;

		} else if (isValueSetNode(node_id)) {

			Vector source_vec = getValueSetDecriptionSources(node_id);
			HashMap hmap0 = null;
			for (int i=0; i<source_vec.size();i++) {
				String src = (String) source_vec.elementAt(i);
				hmap = getSubValueSetsFromSourceCodingScheme(src);

				hmap = TreeUtils.combine(hmap0, hmap);
				hmap0 = hmap;
			}
			if (hmap != null) {
				assignTreeNodeExpandable(hmap);
				TreeUtils.relabelTreeNodes(hmap);
			}
		}
		return hmap;
	}



	public String getCodingSchemeName(String cs_code) {
		if (cs_code.indexOf("$") == -1) return cs_code;
		Vector v = StringUtils.parseData(cs_code, "$");
		return (String) v.elementAt(0);
	}

	public String getValueSetURI(String cs_code) {
		if (cs_code.indexOf("$") == -1) return cs_code;
		Vector v = StringUtils.parseData(cs_code, "$");
		return (String) v.elementAt(1);
	}



    public void initializeCS2vsdURIs_map() {
        //HashSet hset = getValueSetParticipationHashSet();
        if (_valueSetParticipationHashSet == null) {
            _valueSetParticipationHashSet = getValueSetParticipationHashSet();
	    }
	}


    public Vector getValueSetDefinitionURIsForCodingScheme(String codingSchemeName) {
		return (Vector) _cs2vsdURIs_map.get(codingSchemeName);

		/*
		if (_cs2vsdURIs_map == null) {
			_cs2vsdURIs_map = new HashMap();
		}

		if (_cs2vsdURIs_map.containsKey(codingSchemeName)) {
			return (Vector) _cs2vsdURIs_map.get(codingSchemeName);
		}

		//long ms = System.currentTimeMillis();
		Vector v = new Vector();
		String cs_uri = getCodingSchemeURN(codingSchemeName);

		//_valueSetDefinitionURI2VSD_map = createValueSetDefinitionURI2VSD_map();

		Iterator it = _valueSetDefinitionURI2VSD_map.keySet().iterator();
		while (it.hasNext()) {
			String vsd_uri = (String) it.next();
			Vector cs_vec = getCodingSchemeURNsInValueSetDefinition(vsd_uri);
			if (cs_vec != null) {
				if (cs_vec.contains(cs_uri)) {
					v.add(vsd_uri);
				}
			}
		}

		_cs2vsdURIs_map.put(codingSchemeName, v);
		return v;
		*/
	}



    public Vector getVSDRootsBySource(String src_str) {
		//createVSDSource2VSDsMap();
		//if (_source_subconcept_map == null) preprocessSourceHierarchyData();
		Vector vsd_vec = (Vector) _vsd_source_to_vsds_map.get(src_str);

		if (vsd_vec != null && vsd_vec.size() > 0) {
			return vsd_vec;

		} else {
			//get sub_sources
			Vector vsd_in_sub_src_vec = new Vector();
			if (!_source_subconcept_map.containsKey(src_str)) {
				return null;
			}
			Vector sub_vec = (Vector) _source_subconcept_map.get(src_str);
			if (sub_vec == null) {
				return null;
			}

			if (sub_vec.size() == 0) {
				return new Vector();
			}

			for (int i=0; i<sub_vec.size(); i++) {
				String sub = (String) sub_vec.elementAt(i);

  			    Vector vec_sub = getVSDRootsBySource(sub);
				if (vec_sub != null && vec_sub.size() > 0) {
					for (int j=0; j<vec_sub.size(); j++) {
						ValueSetDefinition vsd = (ValueSetDefinition) vec_sub.elementAt(j);
						vsd_in_sub_src_vec.add(vsd);
					}
				}
			}
			return new SortUtils().quickSort(vsd_in_sub_src_vec);
		}
	}


    public Vector getVSDChildrenNodesBySource(String vsd_uri) {
		//createVSDSource2VSDsMap();
		if (_source_subconcept_map == null) return null;

		// find srcs for vsd_uri
		Vector vsd_in_sub_src_vec = new Vector();
		Vector sources = getValueSetDecriptionSources(vsd_uri);
		if (sources == null || sources.size() == 0) return null;

        HashSet hset = new HashSet();
	    for (int i=0; i<sources.size(); i++) {
			String source = (String) sources.elementAt(i);

			if (_source_subconcept_map.containsKey(source)) {
				Vector sub_vec = (Vector) _source_subconcept_map.get(source);
				for (int j=0; j<sub_vec.size(); j++) {
					String sub_source = (String) sub_vec.elementAt(j);
			        Vector vsd_vec = getVSDRootsBySource(sub_source);

					if (vsd_vec != null && vsd_vec.size() > 0) {
						for (int k=0; k<vsd_vec.size(); k++) {
							ValueSetDefinition vsd = (ValueSetDefinition) vsd_vec.elementAt(k);
							if (!hset.contains(vsd.getValueSetDefinitionURI())) {
							   hset.add(vsd.getValueSetDefinitionURI());
							   vsd_in_sub_src_vec.add(vsd);
						    }
						}
					}
				}
			}
		}
		hset.clear();
		return new SortUtils().quickSort(vsd_in_sub_src_vec);
	}




    public HashMap expand_src_vs_tree_exclude_src_nodes(String vsduri) {

        String node_id = vsduri;

		HashMap hmap = new HashMap();
		HashSet hset = new HashSet();
		TreeItem root = new TreeItem("<Root>", "Root node");
		List <TreeItem> children = new ArrayList();

		if (isValueSetSourceNode(node_id)) {

			Vector vsd_root_vec = getVSDRootsBySource(node_id);
			if (vsd_root_vec != null) {
				for (int j=0; j<vsd_root_vec.size(); j++) {
					ValueSetDefinition vsd = (ValueSetDefinition) vsd_root_vec.elementAt(j);
					if (!hset.contains(vsd.getValueSetDefinitionURI())) {
						hset.add(vsd.getValueSetDefinitionURI());
						TreeItem ti = new TreeItem(vsd.getValueSetDefinitionURI(), vsd.getValueSetDefinitionName());
                        Vector sub_vsd_vec = getVSDChildrenNodesBySource(vsd.getValueSetDefinitionURI());
                        if (sub_vsd_vec != null && sub_vsd_vec.size() > 0) {
							ti._expandable = true;
						}
						children.add(ti);
					}
				}
			}

	    } else {

			Vector sub_vsd_vec = getVSDChildrenNodesBySource(vsduri);
			if (sub_vsd_vec != null && sub_vsd_vec.size() > 0) {
				for (int i=0; i<sub_vsd_vec.size(); i++) {
					ValueSetDefinition vsd = (ValueSetDefinition) sub_vsd_vec.elementAt(i);

					if (!hset.contains(vsd.getValueSetDefinitionURI())) {
						hset.add(vsd.getValueSetDefinitionURI());
						TreeItem child_node = new TreeItem(vsd.getValueSetDefinitionURI(), vsd.getValueSetDefinitionName());
						Vector next_level_sub_vsd_vec = getVSDChildrenNodesBySource(vsd.getValueSetDefinitionURI());
						if (next_level_sub_vsd_vec != null && next_level_sub_vsd_vec.size() > 0) {
							child_node._expandable = true;
						}
						children.add(child_node);
					}
				}
			}
		}
		new SortUtils().quickSort(children);
		root.addAll(INVERSE_IS_A, children);
		hmap.put("<Root>", root);
		hset.clear();
		return hmap;

    }



    // To be implemented

    public TreeItem getVSDChildNodeBySource(ValueSetDefinition vsd) {
		TreeItem ti = new TreeItem(vsd.getValueSetDefinitionURI(), vsd.getValueSetDefinitionName());
		List <TreeItem> children = new ArrayList();

		Vector sub_vsd_vec = getVSDChildrenNodesBySource(vsd.getValueSetDefinitionURI());
		if (sub_vsd_vec != null && sub_vsd_vec.size() > 0) {
			ti._expandable = true;

			for (int i=0; i<sub_vsd_vec.size(); i++) {
				ValueSetDefinition child_vsd = (ValueSetDefinition) sub_vsd_vec.elementAt(i);
				children.add(getVSDChildNodeBySource(child_vsd));
			}
		}
		return ti;
	}



    public TreeItem getSourceValueSetTreeBranch(ValueSetDefinition vsd) {
		TreeItem ti = new TreeItem(vsd.getValueSetDefinitionURI(), vsd.getValueSetDefinitionName());
		ti._expandable = false;
		List <TreeItem> children = new ArrayList();

		HashMap src_vs_tree_root_hmap = expand_src_vs_tree_exclude_src_nodes(vsd.getValueSetDefinitionURI());
		TreeItem src_vs_tree_root = (TreeItem) src_vs_tree_root_hmap.get("<Root>");
		for (String asso_name : src_vs_tree_root._assocToChildMap.keySet()) {
		    List<TreeItem> child_nodes = src_vs_tree_root._assocToChildMap.get(asso_name);
		    for (TreeItem child_item : child_nodes) {
				 ValueSetDefinition child_vsd = findValueSetDefinitionByURI(child_item._code);
				 TreeItem root = getSourceValueSetTreeBranch(child_vsd);
				 children.add(root);
				 ti._expandable = true;
		    }
		}
	    new SortUtils().quickSort(children);
	    ti.addAll(INVERSE_IS_A, children);
	    return ti;
	}




    public HashMap getSourceValueSetTree(String scheme, String version) {
		TreeItem super_root = new TreeItem("<Root>", "Root node");
		super_root._expandable = false;
		List <TreeItem> branch = new ArrayList();

        HashMap src_vs_tree_hmap = build_src_vs_tree();
        if (src_vs_tree_hmap == null) return null;
		TreeItem vs_roots = null;//(TreeItem) src_vs_tree_hmap.get("<Root>");

		try {
			vs_roots = (TreeItem) src_vs_tree_hmap.get("<Root>");
	    } catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
		for (String association : vs_roots._assocToChildMap.keySet()) {
			 List<TreeItem> children = vs_roots._assocToChildMap.get(association);
			 for (TreeItem childItem : children) {
				 String code = childItem._code;
				 String name = childItem._text;
				 TreeItem top_node = new TreeItem(childItem._code, childItem._text);

				 top_node._expandable = false;
				 List <TreeItem> top_branch = new ArrayList();

				 HashMap src_vs_tree_root_hmap = expand_src_vs_tree_exclude_src_nodes(code);
				 TreeItem src_vs_tree_root = (TreeItem) src_vs_tree_root_hmap.get("<Root>");
				 for (String asso_name : src_vs_tree_root._assocToChildMap.keySet()) {
					 List<TreeItem> child_nodes = src_vs_tree_root._assocToChildMap.get(asso_name);
					 for (TreeItem child_item : child_nodes) {
						 ValueSetDefinition child_vsd = findValueSetDefinitionByURI(child_item._code);
						 TreeItem root = getSourceValueSetTreeBranch(child_vsd);
						 top_branch.add(root);
						 top_node._expandable = true;
					 }
				 }

				 new SortUtils().quickSort(top_branch);
				 top_node.addAll(INVERSE_IS_A, top_branch);

				 branch.add(top_node);

				 super_root._expandable = true;

			 }
		}

		new SortUtils().quickSort(branch);
		super_root.addAll(INVERSE_IS_A, branch);
		HashMap hmap = new HashMap();
		hmap.put("<Root>", super_root);
		//hset.clear();
        return hmap;
	}




	public List sortCSVSDTree(List <TreeItem> branch) {
		if (branch == null) return null;
		if (branch.size() == 1) return null;

		List <TreeItem> new_branch = new ArrayList();

		//TreeItem ncit_node = null;
		for (int i=0; i<branch.size(); i++) {
			TreeItem ti = (TreeItem) branch.get(i);
			if (ti._text.compareTo("NCI Thesaurus") == 0) {
				//ncit_node = ti;
				new_branch.add(ti);
				break;
			}
		}

		for (int i=0; i<branch.size(); i++) {
			TreeItem ti = (TreeItem) branch.get(i);
			if (ti._text.compareTo("NCI Thesaurus") != 0) {
				new_branch.add(ti);
			}
		}

		return new_branch;
	}





    public HashMap getSourceValueSetTree() {
		return getSourceValueSetTree(null, null);
	}





    private void initializeHashMaps() {
		long ms = System.currentTimeMillis();

        localName2FormalNameHashMap = new HashMap();
        codingSchemeName2URIHashMap = new HashMap();

        Vector nv_vec = new Vector();
        boolean includeInactive = false;

        try {
            CodingSchemeRenderingList csrl = null;
            try {
                csrl = lbSvc.getSupportedCodingSchemes();
            } catch (LBInvocationException ex) {
                ex.printStackTrace();
                return;
            }
            CodingSchemeRendering[] csrs = csrl.getCodingSchemeRendering();
            for (int i = 0; i < csrs.length; i++) {
                int j = i + 1;
                CodingSchemeRendering csr = csrs[i];
                CodingSchemeSummary css = csr.getCodingSchemeSummary();
                String formalname = css.getFormalName();
                Boolean isActive = null;
                if (csr.getRenderingDetail() == null) {
                    _logger.warn("\tcsr.getRenderingDetail() == null");
                } else if (csr.getRenderingDetail().getVersionStatus() == null) {
                    _logger
                        .warn("\tcsr.getRenderingDetail().getVersionStatus() == null");
                } else {
                    isActive =
                        csr.getRenderingDetail().getVersionStatus().equals(
                            CodingSchemeVersionStatus.ACTIVE);
                }

                String representsVersion = css.getRepresentsVersion();
                _logger.debug("(" + j + ") " + formalname + "  version: "
                    + representsVersion);
                _logger.debug("\tActive? " + isActive);

                if ((includeInactive && isActive == null)
                    || (isActive != null && isActive.equals(Boolean.TRUE))
                    || (includeInactive && (isActive != null && isActive
                        .equals(Boolean.FALSE)))) {

                    CodingSchemeVersionOrTag vt =
                        new CodingSchemeVersionOrTag();
                    vt.setVersion(representsVersion);

                    try {
                        CodingScheme cs = lbSvc.resolveCodingScheme(formalname, vt);
                        codingSchemeName2URIHashMap.put(cs.getCodingSchemeName(), cs.getCodingSchemeURI());
                        codingSchemeName2URIHashMap.put(formalname, cs.getCodingSchemeURI());

                        String[] localnames = cs.getLocalName();
                        for (int m = 0; m < localnames.length; m++) {
                            String localname = localnames[m];
                            _logger.debug("\tlocal name: " + localname);
                            localName2FormalNameHashMap.put(localname,
                                formalname);
                        }
                        localName2FormalNameHashMap.put(cs.getCodingSchemeURI(), formalname);
                        localName2FormalNameHashMap.put(cs.getCodingSchemeName(), formalname);
                        localName2FormalNameHashMap.put(formalname, formalname);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("initializeHashMaps run time (ms): " + (System.currentTimeMillis() - ms));
    }



////////////////////////////////////////////////////////////////////////////////////
// Terminology View
////////////////////////////////////////////////////////////////////////////////////

	public HashMap getRootValueSets(String scheme) {
		String formalName = getFormalName(scheme);
		String codingSchemeURN = getCodingSchemeURN(formalName);
        Vector source_in_cs_vsd_vec = new Vector();
        HashMap source2VSD_map = new HashMap();
        HashMap uri2VSD_map = new HashMap();
        List list = valueSetDefinitionURIList;
        for (int i=0; i<list.size(); i++) {
			String uri = (String) list.get(i);
            Vector cs_vec = getCodingSchemeURNsInValueSetDefinition(uri);
            if (cs_vec.contains(codingSchemeURN)) {
				ValueSetDefinition vsd = findValueSetDefinitionByURI(uri);

				uri2VSD_map.put(uri, vsd);

				java.util.Enumeration<? extends Source> sourceEnum = vsd.enumerateSource();

				while (sourceEnum.hasMoreElements()) {
					Source src = (Source) sourceEnum.nextElement();
					String src_str = src.getContent();
					if (!source_in_cs_vsd_vec.contains(src_str)) {
						source_in_cs_vsd_vec.add(src_str);
					}
					if (source2VSD_map.containsKey(src_str)) {
						Vector vsd_v = (Vector) source2VSD_map.get(src_str);
						if (!vsd_v.contains(uri)) {
							vsd_v.add(uri);
						}
						source2VSD_map.put(src_str, vsd_v);
					} else {
						Vector vsd_v = new Vector();
						vsd_v.add(uri);
						source2VSD_map.put(src_str, vsd_v);
					}
				}
			}
		}

		TreeItem root = new TreeItem("<Root>", "Root node");
		HashSet hset = new HashSet();

        List <TreeItem> children = new ArrayList();
		for (int k=0; k<source_in_cs_vsd_vec.size(); k++) {
			String src = (String) source_in_cs_vsd_vec.elementAt(k);
			// check has children
			boolean has_children = false;
			if (_source_subconcept_map.containsKey(src)) {
				Vector sub_vec = (Vector) _source_subconcept_map.get(src);
				for (int j=0; j<source_in_cs_vsd_vec.size(); j++) {
					String source = (String) source_in_cs_vsd_vec.elementAt(j);
					if (source.compareTo(src) != 0 && sub_vec.contains(source)) {
						has_children = true;
						break;
					}
				}
			}

			boolean has_parent = false;
			if (_source_superconcept_map.containsKey(src)) {
				Vector super_vec = (Vector) _source_superconcept_map.get(src);
				for (int j=0; j<source_in_cs_vsd_vec.size(); j++) {
					String source = (String) source_in_cs_vsd_vec.elementAt(j);
					if (source.compareTo(src) != 0 && super_vec.contains(source)) {
						has_parent = true;
						break;
					}
				}
			}

			// orphan
			if (!has_children && !has_parent) {
				// find all VSDs has the source
				Vector vsd_v = (Vector) source2VSD_map.get(src);
				for(int i=0; i<vsd_v.size(); i++) {
					//ValueSetDefinition vsd = (ValueSetDefinition) vsd_v.elementAt(i);
					String vsd_uri = (String) vsd_v.elementAt(i);
					ValueSetDefinition vsd = (ValueSetDefinition) uri2VSD_map.get(vsd_uri);

					String name = vsd.getValueSetDefinitionName();
					if (name == null || name.compareTo("") == 0) {
						name = "<NOT ASSIGNED>";
					}

					String key = vsd.getValueSetDefinitionURI() + "$" + name;
					if (!hset.contains(key)) {
						hset.add(key);
						TreeItem ti = new TreeItem(vsd.getValueSetDefinitionURI(), name);
						ti._expandable = false;
						children.add(ti);
					}
				}

			} else if (has_children && !has_parent) {
				Vector vsd_v = (Vector) source2VSD_map.get(src);
				for(int i=0; i<vsd_v.size(); i++) {
					//ValueSetDefinition vsd = (ValueSetDefinition) vsd_v.elementAt(i);
					String vsd_uri = (String) vsd_v.elementAt(i);
					ValueSetDefinition vsd = (ValueSetDefinition) uri2VSD_map.get(vsd_uri);

					String name = vsd.getValueSetDefinitionName();
					if (name == null || name.compareTo("") == 0) {
						name = "<NOT ASSIGNED>";
					}
					String key = vsd.getValueSetDefinitionURI() + "$" + name;

                    if (!hset.contains(key)) {
						hset.add(key);
						TreeItem ti = new TreeItem(vsd.getValueSetDefinitionURI(), name);
						ti._expandable = true;
						children.add(ti);
				    }
				}
			}
		}

		root._expandable = false;
		new SortUtils().quickSort(children);

		try {
			root.addAll(INVERSE_IS_A, children);
		} catch (Exception ex) {
			System.out.println("ERROR: root.addAll failed -- possible duplicated child nodes.");
		}
		HashMap hmap = new HashMap();
		hmap.put("<Root>", root);
        return hmap;
	}


    public HashMap getCodingSchemeValueSetTree() {
		return getCodingSchemeValueSetTree(null, null);
	}

	public void printTreeItem(TreeItem ti) {
		System.out.println("Node: " + ti._text + "(" + ti._code + ")");
	}


    public HashMap getCodingSchemeValueSetTree(String scheme, String version) {
		List <TreeItem> branch = new ArrayList();
		TreeItem super_root = new TreeItem("<Root>", "Root node");
		HashMap hmap = getRootValueSets();  // same as getRootValuSetes(by_source = true)
		TreeItem root = (TreeItem) hmap.get("<Root>");

		for (String association : root._assocToChildMap.keySet()) {
			 List<TreeItem> children = root._assocToChildMap.get(association);
			 for (TreeItem childItem : children) {
				 String code = childItem._code;
				 String name = childItem._text;
				 TreeItem cs_vs_root = new TreeItem(code, name);
   				 cs_vs_root._expandable = false;
				 List <TreeItem> cs_vs_root_children = new ArrayList();
				 String node_id = code;
				 try {
				     HashMap cs_hmap = getRootValueSets(node_id);
				     TreeItem temp_root = (TreeItem) cs_hmap.get("<Root>");
				     for (String asso_name : temp_root._assocToChildMap.keySet()) {
					     List<TreeItem> cs_vs_children = temp_root._assocToChildMap.get(asso_name);
					     for (TreeItem child_item : cs_vs_children) {
						     String vs_code = child_item._code;
						     String vs_name = child_item._text;

							 TreeItem child = getCodingSchemeValueSetTreeBranch(node_id, vs_code, vs_name);
							 cs_vs_root_children.add(child);
							 cs_vs_root._expandable = true;
					     }
				     }
				 } catch (Exception ex) {
				   ex.printStackTrace();
				 }

				 new SortUtils().quickSort(cs_vs_root_children);
				 cs_vs_root.addAll(INVERSE_IS_A, cs_vs_root_children);

				 branch.add(cs_vs_root);
				 super_root._expandable = true;
			}
		}
		new SortUtils().quickSort(branch);
		// bubble NCI Thesaurus node to the top

		if (branch.size() > 1) {
			branch = sortCSVSDTree(branch);
		}

		if (super_root != null) {
			super_root.addAll(INVERSE_IS_A, branch);
		}

		HashMap map = new HashMap();
		map.put("<Root>", super_root);
        return map;
	}




    public TreeItem getCodingSchemeValueSetTreeBranch(String scheme, String code, String name) {
		TreeItem ti = new TreeItem(code, name);
		ti._expandable = false;
		List <TreeItem> children = new ArrayList();

		HashMap hmap = getSubValueSets(scheme, code);
		if (hmap != null) {
			TreeItem temp_root = (TreeItem) hmap.get(code);
            for (String association : temp_root._assocToChildMap.keySet()) {
                List<TreeItem> child_nodes = temp_root._assocToChildMap.get(association);
                for (TreeItem childItem : child_nodes) {
					TreeItem child = getCodingSchemeValueSetTreeBranch(scheme, childItem._code, childItem._text);
					children.add(child);
					ti._expandable = true;
				}
			}
		}
	    new SortUtils().quickSort(children);
	    ti.addAll(INVERSE_IS_A, children);
	    return ti;
	}

    public void constructVsUri2ParticipationCS_map() {
		_vsUri2ParticipationCS_map = new HashMap();
		_cs2vsdURIs_map = new HashMap();
        List list = valueSetDefinitionURIList;
        for (int i=0; i<list.size(); i++) {
			String uri = (String) list.get(i);
            Vector cs_vec = getCodingSchemeURNsInValueSetDefinition(uri);
            _vsUri2ParticipationCS_map.put(uri, cs_vec);

            for (int j=0; j<cs_vec.size(); j++) {
				String cs = (String) cs_vec.elementAt(j);
            	Vector vsdURI_vec = (Vector) _cs2vsdURIs_map.get(cs);
            	if (vsdURI_vec == null) {
					vsdURI_vec = new Vector();
				}
				if (!vsdURI_vec.contains(uri)) {
					vsdURI_vec.add(uri);
				}
				_cs2vsdURIs_map.put(cs, vsdURI_vec);
				String cs_uri = getCodingSchemeURN(cs);
				_cs2vsdURIs_map.put(cs_uri, vsdURI_vec);
				String formalName = getFormalName(cs);
				_cs2vsdURIs_map.put(formalName, vsdURI_vec);
			}
		}
	}




	public boolean get_restriction_to_cs_condition(String vs_uri, String codingSchemeURN) {
		if (vs_uri == null) return false;
		Vector cs_vec = (Vector) _vsUri2ParticipationCS_map.get(vs_uri);
		if (cs_vec == null) return false;
		return cs_vec.contains(codingSchemeURN);
	}


    // code: value set URI
	public HashMap getSubValueSets(String scheme, String code) {
		if (scheme == null) {
			// default to source hierarchy
			 HashMap hmap = getSubValueSetsFromSourceCodingScheme(code);
             TreeUtils.relabelTreeNodes(hmap);
			 return hmap;
		}

		Vector vsd_vec = getValueSetDefinitionURIsForCodingScheme(scheme);
		if (vsd_vec == null) {
			System.out.println("vsd_vec is NULL??? " + scheme);
			return null;
		}

		if (vsd_vec.size() == 0) {
			return null;
		}

        // find sub value sets based on value set source data:
		String codingSchemeURN = null;
		String formalName = getFormalName(scheme);
		codingSchemeURN = (String) getCodingSchemeURN(formalName);

		//HashMap source_hier = getValueSetSourceHierarchy();
        Vector source_in_cs_vsd_vec = new Vector();
        HashMap source2VSD_map = new HashMap();
        HashMap uri2VSD_map = new HashMap();
        //uri2VSD_map = getValueSetDefinitionURI2VSD_map();

		ValueSetDefinition root_vsd = null;
		Vector participating_vsd_vec = new Vector();

		// find participating VSDs based on scheme uri2VSD_map & source2VSD_map
        List list = valueSetDefinitionURIList;
        for (int i=0; i<list.size(); i++) {
			String uri = (String) list.get(i);
			boolean restriction_to_cs_condition = get_restriction_to_cs_condition(uri, codingSchemeURN);
            if (restriction_to_cs_condition) {
				ValueSetDefinition vsd = findValueSetDefinitionByURI(uri);
				//System.out.println("vsd: " + vsd.getValueSetDefinitionName());

				if (uri.compareTo(code) == 0) {
					root_vsd = vsd;
				}

				uri2VSD_map.put(uri, vsd);
				participating_vsd_vec.add(uri);

				java.util.Enumeration<? extends Source> sourceEnum = vsd.enumerateSource();
				while (sourceEnum.hasMoreElements()) {
					Source src = (Source) sourceEnum.nextElement();
					String src_str = src.getContent();
					if (!source_in_cs_vsd_vec.contains(src_str)) {
						source_in_cs_vsd_vec.add(src_str);
					}
					if (source2VSD_map.containsKey(src_str)) {
						Vector vsd_v = (Vector) source2VSD_map.get(src_str);
						if (!vsd_v.contains(uri)) {
							vsd_v.add(uri);
						}
						source2VSD_map.put(src_str, vsd_v);
					} else {
						Vector vsd_v = new Vector();
						vsd_v.add(uri);
						source2VSD_map.put(src_str, vsd_v);
					}
				}
			}
		}

		if (root_vsd == null) {
			return null;
		}

		TreeItem root = new TreeItem(code, root_vsd.getValueSetDefinitionName());

        List <TreeItem> children = new ArrayList();

		java.util.Enumeration<? extends Source> sourceEnum = root_vsd.enumerateSource();

        try {

			while (sourceEnum.hasMoreElements()) {
				Source src = (Source) sourceEnum.nextElement();
				String src_str = src.getContent();
				if (_source_subconcept_map.containsKey(src_str)) {
					Vector sub_vec = (Vector) _source_subconcept_map.get(src_str);
					for (int k=0; k<sub_vec.size(); k++) {
						String sub_src = (String) sub_vec.elementAt(k);

						Vector sub_vsd_vec = (Vector) source2VSD_map.get(sub_src);
						if (sub_vsd_vec != null) {
							for (int m=0; m<sub_vsd_vec.size(); m++) {
								String sub_vsd_uri = (String) sub_vsd_vec.elementAt(m);

								if (participating_vsd_vec.contains(sub_vsd_uri)) {

									ValueSetDefinition sub_vsd = (ValueSetDefinition) uri2VSD_map.get(sub_vsd_uri);
									String vsd_text = sub_vsd.getValueSetDefinitionName();

											java.util.Enumeration<? extends Source> sub_vsd_sourceEnum = sub_vsd.enumerateSource();
											while (sub_vsd_sourceEnum.hasMoreElements()) {
												Source sub_vsd_src = (Source) sub_vsd_sourceEnum.nextElement();
												String sub_vsd_src_str = sub_vsd_src.getContent();
											}


									if (vsd_vec.contains( sub_vsd_uri )) {
										TreeItem ti_sub = new TreeItem(sub_vsd_uri, vsd_text);
										ti_sub._expandable = false; // to be modified
										children.add(ti_sub);
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		root._expandable = false;
		if (children.size() > 0) {
			root._expandable = true;
		}

		new SortUtils().quickSort(children);

		root.addAll(INVERSE_IS_A, children);

		HashMap hmap = new HashMap();
		hmap.put(code, root);
        return hmap;
	}


/*
	public static void main(String[] args) throws Exception {
		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
		String url = "http://lexevsapi6.nci.nih.gov/lexevsapi63";
		LexEVSValueSetDefinitionServices vsd_service = RemoteServerUtil.getLexEVSValueSetDefinitionServices(url);
        ValueSetHierarchy vsu = new ValueSetHierarchy(lbSvc, vsd_service);


        String cs_url = "http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#";
	    HashMap root_hmap = vsu.getRootValueSets(cs_url);
	    System.out.println("\nTree:");
	    if (root_hmap != null) {
			System.out.println("vsu.getRootValueSets returns != null " + cs_url);
			vsu.printTree(root_hmap);
		} else {
			System.out.println("vsu.getRootValueSets returns null??? " + cs_url);
		}

        cs_url = "National Drug File - Reference Terminology";
	    root_hmap = vsu.getRootValueSets(cs_url);
	    System.out.println("\nTree:");
	    if (root_hmap != null) {
			System.out.println("vsu.getRootValueSets returns != null " + cs_url);
			vsu.printTree(root_hmap);
		} else {
			System.out.println("vsu.getRootValueSets returns null??? " + cs_url);
		}


		HashMap src_tree = vsu.getSourceValueSetTree();
		vsu.printTree(src_tree);

        HashMap cs_vs_roots = vsu.getRootValueSets();
        vsu.printTree(cs_vs_roots);

        String scheme = "National Drug File - Reference Terminology";
        String code = "National Drug File - Reference Terminology";
        String name = "National Drug File - Reference Terminology";

        TreeItem ndf = vsu.getCodingSchemeValueSetTreeBranch(scheme, code, name);
        vsu.printTree(ndf, null, 0);

        long ms = System.currentTimeMillis();
        HashMap cs_tree = vsu.getCodingSchemeValueSetTree();
        vsu.printTree(cs_tree);
        System.out.println("Total run time (ms): " + (System.currentTimeMillis() - ms));

	}
*/
}

