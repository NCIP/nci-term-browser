package gov.nih.nci.evs.browser.utils;

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


import java.io.*;
import java.net.URI;

import java.text.*;
import java.util.*;
import java.sql.*;
//import javax.faces.model.*;

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

//import org.LexGrid.LexBIG.Utility.ServiceUtility;
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
//import org.lexevs.property.PropertyExtension;
import org.json.*;



public class ValueSetHierarchy {
	public static AbsoluteCodingSchemeVersionReferenceList _csVersionList = null;

	public static HashSet _valueSetParticipationHashSet = null;

    public static String SOURCE_SCHEME = "Terminology Value Set";
    public static String SOURCE_VERSION = null;

	public static HashMap _source_hierarchy = null;
	public static HashMap _source_subconcept_map = null;
	public static HashMap _source_superconcept_map = null;


    static LocalNameList _noopList = Constructors.createLocalNameList("_noop_");
    private static Logger _logger = Logger.getLogger(ValueSetHierarchy.class);


    public static final String ONTOLOGY_NODE_ID = "ontology_node_id";

    public static final String ONTOLOGY_NODE_NAME = "ontology_node_name";
    public static final String ONTOLOGY_NODE_PARENT_ASSOC =
        "ontology_node_parent_assoc";
    public static final String ONTOLOGY_NODE_CHILD_COUNT =
        "ontology_node_child_count";
    public static final String ONTOLOGY_NODE_DEFINITION =
        "ontology_node_definition";
    public static final String CHILDREN_NODES = "children_nodes";

    public static HashMap _valueSetDefinitionHierarchyHashMap = null;
    public static Vector  _availableValueSetDefinitionSources = null;
    public static Vector  _valueSetDefinitionHierarchyRoots = null;

    //public static Vector  _valueSetDefinitionSourceListing = null;
    public static HashMap _valueSetDefinitionSourceCode2Name_map = null;

    public static HashMap _vsd_source_to_vsds_map = null;
    public static HashMap _valueSetDefinitionURI2VSD_map = null;

	//private static String URL = "http://bmidev4:19280/lexevsapi60";
	//private static String URL = "http://ncias-d488-v.nci.nih.gov:29080/lexevsapi60";
	//private static String URL = "http://ncias-q532-v.nci.nih.gov:29080/lexevsapi60";

	private static String URL = "http://localhost:8080/lexevsapi60";

    public static HashMap _cs2vsdURIs_map = null;
    public static HashMap _subValueSet_hmap = null;

    public ValueSetHierarchy() {
        SOURCE_VERSION = DataUtils.getVocabularyVersionByTag(SOURCE_SCHEME, "PRODUCTION");
        System.out.println("SOURCE_VERSION: " + SOURCE_VERSION);

        _valueSetDefinitionURI2VSD_map = getValueSetDefinitionURI2VSD_map();
    }

    public static String getValueSetDecription(String uri) {
		if (_valueSetDefinitionURI2VSD_map == null) {
			_valueSetDefinitionURI2VSD_map = getValueSetDefinitionURI2VSD_map();
		}
		ValueSetDefinition vsd = (ValueSetDefinition) _valueSetDefinitionURI2VSD_map.get(uri);
		if (vsd == null) {
		    return null;
		}
		if (vsd.getEntityDescription() == null) {
			return null;
		}
		return vsd.getEntityDescription().getContent();
	}

    public static Vector getValueSetDecriptionSources(String uri) {
		if (_valueSetDefinitionURI2VSD_map == null) {
			_valueSetDefinitionURI2VSD_map = getValueSetDefinitionURI2VSD_map();
		}
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



    public static String getSourceSchemeVersion() {
		if (SOURCE_VERSION == null) {
			SOURCE_VERSION = DataUtils.getVocabularyVersionByTag(SOURCE_SCHEME, "PRODUCTION");
		}
		return SOURCE_VERSION;
	}

    public static String getVocabularyVersionByTag(String codingSchemeName,
        String ltag) {

        if (codingSchemeName == null)
            return null;
        String version = null;
        int knt = 0;
        try {
            LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
            CodingSchemeRenderingList lcsrl = lbSvc.getSupportedCodingSchemes();
            CodingSchemeRendering[] csra = lcsrl.getCodingSchemeRendering();
            for (int i = 0; i < csra.length; i++) {
                CodingSchemeRendering csr = csra[i];
                CodingSchemeSummary css = csr.getCodingSchemeSummary();
                if (css.getFormalName().compareTo(codingSchemeName) == 0
                    || css.getLocalName().compareTo(codingSchemeName) == 0
                    || css.getCodingSchemeURI().compareTo(codingSchemeName) == 0) {
					version = css.getRepresentsVersion();
                    knt++;

                    if (ltag == null)
                        return version;
                    RenderingDetail rd = csr.getRenderingDetail();
                    CodingSchemeTagList cstl = rd.getVersionTags();
                    java.lang.String[] tags = cstl.getTag();
                    // KLO, 102409
                    if (tags == null)
                        return version;

                    if (tags != null && tags.length > 0) {
                        for (int j = 0; j < tags.length; j++) {
                            String version_tag = (String) tags[j];

                            if (version_tag != null && version_tag.compareToIgnoreCase(ltag) == 0) {
                                return version;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //_logger.warn("Version corresponding to tag " + ltag + " is not found "
        //    + " in " + codingSchemeName);
        if (ltag != null && ltag.compareToIgnoreCase("PRODUCTION") == 0
            & knt == 1) {
            //_logger.warn("\tUse " + version + " as default.");
            return version;
        }
        return null;
    }
    public static String[] getHierarchyIDs(String codingScheme,
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

                // Cache and return the new value ...
                hier = ids.toArray(new String[ids.size()]);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return hier;
    }

    protected static SupportedHierarchy[] getSupportedHierarchies(
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

    protected static CodingScheme getCodingScheme(String codingScheme,
        CodingSchemeVersionOrTag versionOrTag) throws LBException {
        CodingScheme cs = null;
        try {
            LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
            cs = lbSvc.resolveCodingScheme(codingScheme, versionOrTag);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cs;
    }


    public static String getHierarchyID(String codingScheme, String version) {
        CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
        if (version != null)
            versionOrTag.setVersion(version);
        try {
            String[] ids = getHierarchyIDs(codingScheme, versionOrTag);
            if (ids.length > 0)
                return ids[0];
        } catch (Exception e) {

        }
        return null;
    }


    public static ResolvedConceptReferenceList getHierarchyRoots(
        String codingScheme, String version) {
        CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
        if (version != null)
            versionOrTag.setVersion(version);
        try {
            LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
            LexBIGServiceConvenienceMethods lbscm =
                (LexBIGServiceConvenienceMethods) lbSvc
                    .getGenericExtension("LexBIGServiceConvenienceMethods");
            lbscm.setLexBIGService(lbSvc);
            String hierarchyID = getHierarchyID(codingScheme, version);
            return lbscm.getHierarchyRoots(codingScheme, versionOrTag,
                hierarchyID);
        } catch (Exception e) {
            return null;
        }
    }


//===========================================================================================================================
// Value Set Hierarchy
//===========================================================================================================================


    public static ResolvedConceptReferenceList getValueSetHierarchyRoots() {
        String scheme = "Terminology Value Set";
        String version = getVocabularyVersionByTag(scheme, "PRODUCTION");
        return getHierarchyRoots(scheme, version);
	}

	public static void geValueSetHierarchy(HashMap hmap, Vector v) {

    }

	public static void geValueSetHierarchy() {
        HashMap hmap = new HashMap();
        ResolvedConceptReferenceList roots = getValueSetHierarchyRoots();
        Vector v = new Vector();
        for (int i=0; i<roots.getResolvedConceptReferenceCount(); i++) {
			ResolvedConceptReference rcr = roots.getResolvedConceptReference(i);
			v.add(rcr);
		}
        geValueSetHierarchy(hmap, v);
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

    public static HashMap getValueSetDefinitionURI2VSD_map() {
		if (_valueSetDefinitionURI2VSD_map != null) {
			return _valueSetDefinitionURI2VSD_map;
		}
		_valueSetDefinitionURI2VSD_map = new HashMap();

		try {
			LexEVSValueSetDefinitionServices vsd_service = RemoteServerUtil.getLexEVSValueSetDefinitionServices();
			String valueSetDefinitionRevisionId = null;
			List list = vsd_service.listValueSetDefinitionURIs();
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


    public static ValueSetDefinition findValueSetDefinitionByURI(String uri) {

        if (_valueSetDefinitionURI2VSD_map == null) {
			_valueSetDefinitionURI2VSD_map = new HashMap();
		}
		if (_valueSetDefinitionURI2VSD_map.containsKey(uri)) {
			return (ValueSetDefinition) _valueSetDefinitionURI2VSD_map.get(uri);
		}

		if (uri == null) return null;
	    if (uri.indexOf("|") != -1) {
			Vector u = parseData(uri);
			uri = (String) u.elementAt(1);
		}

		String valueSetDefinitionRevisionId = null;
		try {
			LexEVSValueSetDefinitionServices vsd_service = RemoteServerUtil.getLexEVSValueSetDefinitionServices();
			ValueSetDefinition vsd = vsd_service.getValueSetDefinition(new URI(uri), valueSetDefinitionRevisionId);
			_valueSetDefinitionURI2VSD_map.put(uri, vsd);
			return vsd;
		} catch (Exception ex) {
			//ex.printStackTrace();
		}
		return null;
	}

    public static Vector getValueSetDefinitionsBySource(String source) {
		if (_availableValueSetDefinitionSources != null) {
			if (!_availableValueSetDefinitionSources.contains(source)) return null;
		}
		Vector v = new Vector();
		LexEVSValueSetDefinitionServices vsd_service = RemoteServerUtil.getLexEVSValueSetDefinitionServices();
        List list = vsd_service.listValueSetDefinitionURIs();
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



    public static Vector getAvailableValueSetDefinitionSources() {
		if (_availableValueSetDefinitionSources != null) return _availableValueSetDefinitionSources;
		_availableValueSetDefinitionSources = new Vector();
		HashSet hset = new HashSet();
		LexEVSValueSetDefinitionServices vsd_service = RemoteServerUtil.getLexEVSValueSetDefinitionServices();
        List list = vsd_service.listValueSetDefinitionURIs();
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
					_availableValueSetDefinitionSources.add(src_str);
				}
			}
		}
		return _availableValueSetDefinitionSources;
	}


    public static String getValueSetDefinitionMetadata(ValueSetDefinition vsd) {
		if (vsd== null) return null;
		String name = "";
		String uri = "";
		String description = "";
		String domain = "";
		String src_str = "";

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
			src_str = src_str + src.getContent() + ";";
		}
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
        return new TreeUtils().getSubconcepts(scheme, version, code);
    }


    public static JSONArray HashMap2JSONArray(HashMap hmap) {
        JSONObject json = new JSONObject();
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

        }
        return nodesArray;
    }


    public static void printTree(HashMap hmap) {
        if (hmap == null) {
            System.out.println("ERROR printTree -- hmap is null.");
            return;
        }
        Object[] objs = hmap.keySet().toArray();
        String code = (String) objs[0];
        TreeItem ti = (TreeItem) hmap.get(code);
        printTree(ti, code, 0);
    }

    public static void printTree(TreeItem ti, String focusCode, int depth) {
        StringBuffer indent = new StringBuffer();
        for (int i = 0; i < depth * 2; i++)
            indent.append("| ");

        StringBuffer codeAndText =
            new StringBuffer(indent).append(
                focusCode.equals(ti._code) ? ">>>>" : "").append(ti._code)
                .append(':').append(
                    ti._text.length() > 64 ? ti._text.substring(0, 62) + "..."
                        : ti._text).append(ti._expandable ? " [+]" : "");
        System.out.println(codeAndText.toString());

        indent.append("| ");
        for (String association : ti._assocToChildMap.keySet()) {
            System.out.println(indent.toString() + association);
            List<TreeItem> children = ti._assocToChildMap.get(association);
            Collections.sort(children);
            for (TreeItem childItem : children) {
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

//=========================================================================================

    public static HashMap getAssociatedConcepts(String scheme, String version,
        String code, String assocName, boolean direction) {
        try {
            LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
            LexBIGServiceConvenienceMethods lbscm =
                (LexBIGServiceConvenienceMethods) lbSvc
                    .getGenericExtension("LexBIGServiceConvenienceMethods");
            lbscm.setLexBIGService(lbSvc);
            return getAssociatedConcepts(lbSvc, lbscm, scheme, version, code,
                assocName, direction);
        } catch (Exception ex) {
            return null;
        }
    }


    protected static Association processForAnonomousNodes(Association assoc) {
        if (assoc == null)
            return null;
        // clone Association except associatedConcepts
        Association temp = new Association();
        temp.setAssociatedData(assoc.getAssociatedData());
        temp.setAssociationName(assoc.getAssociationName());
        temp.setAssociationReference(assoc.getAssociationReference());
        temp.setDirectionalName(assoc.getDirectionalName());
        temp.setAssociatedConcepts(new AssociatedConceptList());

        for (int i = 0; i < assoc.getAssociatedConcepts()
            .getAssociatedConceptCount(); i++) {
            // Conditionals to deal with anonymous nodes and UMLS top nodes
            // "V-X"
            // The first three allow UMLS traversal to top node.
            // The last two are specific to owl anonymous nodes which can act
            // like false
            // top nodes.

            /*
             * if(assoc.getAssociatedConcepts().getAssociatedConcept(i).
             * getReferencedEntry() != null) {
             * _logger.debug(assoc.getAssociatedConcepts
             * ().getAssociatedConcept(i)
             * .getReferencedEntry().getEntityDescription().getContent() +
             * " === IsAnonymous? " +
             * assoc.getAssociatedConcepts().getAssociatedConcept(i)
             * .getReferencedEntry().getIsAnonymous()); } else {_logger.debug(
             * "assoc.getAssociatedConcepts().getAssociatedConcept(i).getReferencedEntry() == null"
             * ); }
             */
            /*
             * if (assoc.getAssociatedConcepts().getAssociatedConcept(i)
             * .getReferencedEntry() != null &&
             * assoc.getAssociatedConcepts().getAssociatedConcept(i)
             * .getReferencedEntry().getIsAnonymous() != false) { // do nothing
             * (NCI Thesaurus) }
             */
            if (assoc.getAssociatedConcepts().getAssociatedConcept(i)
                .getReferencedEntry() != null
                && assoc.getAssociatedConcepts().getAssociatedConcept(i)
                    .getReferencedEntry().getIsAnonymous() != null
                && assoc.getAssociatedConcepts().getAssociatedConcept(i)
                    .getReferencedEntry().getIsAnonymous() != false
                && !assoc.getAssociatedConcepts().getAssociatedConcept(i)
                    .getConceptCode().equals("@")
                && !assoc.getAssociatedConcepts().getAssociatedConcept(i)
                    .getConceptCode().equals("@@")) {
                // do nothing
            } else {
                temp.getAssociatedConcepts().addAssociatedConcept(
                    assoc.getAssociatedConcepts().getAssociatedConcept(i));
            }
        }
        return temp;
    }


    /**
     * Returns the entity description for the given resolved concept reference.
     */
    protected static String getCodeDescription(ResolvedConceptReference ref)
            throws LBException {
        EntityDescription desc = ref.getEntityDescription();
        if (desc != null)
            return desc.getContent();
        return "<Not assigned>";
    }

    protected static String getCodeDescription(LexBIGService lbsvc,
        String scheme, CodingSchemeVersionOrTag csvt, String code)
            throws LBException {

        CodedNodeSet cns = lbsvc.getCodingSchemeConcepts(scheme, csvt);
        cns =
            cns.restrictToCodes(Constructors.createConceptReferenceList(code,
                scheme));
        ResolvedConceptReferenceList rcrl = null;
        try {
            rcrl = cns.resolveToList(null, _noopList, null, 1);
        } catch (Exception ex) {
            _logger
                .error("WARNING: TreeUtils getCodeDescription cns.resolveToList throws exceptions");
            return "null";
        }

        if (rcrl != null && rcrl.getResolvedConceptReferenceCount() > 0) {
            EntityDescription desc =
                rcrl.getResolvedConceptReference(0).getEntityDescription();
            if (desc != null)
                return desc.getContent();
        }
        return "<Not assigned>";
    }


    public static HashMap getAssociatedConcepts(LexBIGService lbSvc,
        LexBIGServiceConvenienceMethods lbscm, String scheme, String version,
        String code, String assocName, boolean direction) {
        HashMap hmap = new HashMap();
        TreeItem ti = null;
        long ms = System.currentTimeMillis();

        Set<String> codesToExclude = Collections.EMPTY_SET;

        CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
        if (version != null)
            csvt.setVersion(version);
        ResolvedConceptReferenceList matches = null;
        Vector v = new Vector();
        try {
			Entity concept = getConceptByCode(scheme, version, null, code);
			String entityCodeNamespace = concept.getEntityCodeNamespace();
			ConceptReference focus = ConvenienceMethods.createConceptReference(code, scheme);
			focus.setCodingSchemeName(entityCodeNamespace);
            String name = concept.getEntityDescription().getContent();//getCodeDescription(lbSvc, scheme, csvt, code);

            //String name = getCodeDescription(lbSvc, scheme, csvt, code);
            ti = new TreeItem(code, name);
            ti._expandable = false;

            CodedNodeGraph cng = lbSvc.getNodeGraph(scheme, csvt, null);
            //ConceptReference focus =
            //    Constructors.createConceptReference(code, scheme);
            cng =
                cng.restrictToAssociations(Constructors
                    .createNameAndValueList(assocName), null);
            boolean associationsNavigatedFwd = direction;

            // To remove anonymous classes (KLO, 091009), the
            // resolveCodedEntryDepth parameter cannot be set to -1.
            // Alternative -- use code to determine whether the class is
            // anonymous

            ResolvedConceptReferenceList branch = null;
            try {
                branch =
                    cng.resolveAsList(focus,
                        associationsNavigatedFwd,
                        // !associationsNavigatedFwd, 1, 2, noopList_, null,
                        // null, null, -1, false);
                        !associationsNavigatedFwd, -1, 2, _noopList, null,
                        null, null, -1, false);

            } catch (Exception e) {
                _logger
                    .error("TreeUtils getAssociatedConcepts throws exceptions.");
                return null;
            }

            for (Iterator<? extends ResolvedConceptReference> nodes =
                branch.iterateResolvedConceptReference(); nodes.hasNext();) {
                ResolvedConceptReference node = nodes.next();
                AssociationList childAssociationList = null;

                // AssociationList childAssociationList =
                // associationsNavigatedFwd ? node.getSourceOf():
                // node.getTargetOf();

                if (associationsNavigatedFwd) {
                    childAssociationList = node.getSourceOf();

                } else {
                    childAssociationList = node.getTargetOf();
                }

                if (childAssociationList != null) {
                    // Process each association defining children ...
                    for (Iterator<? extends Association> pathsToChildren =
                        childAssociationList.iterateAssociation(); pathsToChildren
                        .hasNext();) {
                        Association child = pathsToChildren.next();
                        // KLO 091009 remove anonymous nodes

                        child = processForAnonomousNodes(child);

                        String childNavText =
                            getDirectionalLabel(lbscm, scheme, csvt, child,
                                associationsNavigatedFwd);

                        // Each association may have multiple children ...
                        AssociatedConceptList branchItemList =
                            child.getAssociatedConcepts();

                        /*
                         * for (Iterator<AssociatedConcept> branchNodes =
                         * branchItemList.iterateAssociatedConcept();
                         * branchNodes .hasNext();) { AssociatedConcept
                         * branchItemNode = branchNodes.next();
                         */

                        List child_list = new ArrayList();
                        for (Iterator<? extends AssociatedConcept> branchNodes =
                            branchItemList.iterateAssociatedConcept(); branchNodes
                            .hasNext();) {
                            AssociatedConcept branchItemNode =
                                branchNodes.next();
                            child_list.add(branchItemNode);
                        }

                        SortUtils.quickSort(child_list);

                        for (int i = 0; i < child_list.size(); i++) {
                            AssociatedConcept branchItemNode =
                                (AssociatedConcept) child_list.get(i);
                            String branchItemCode =
                                branchItemNode.getConceptCode();

                            if (!branchItemCode.startsWith("@")) {
                                // Add here if not in the list of excluded
                                // codes.
                                // This is also where we look to see if another
                                // level
                                // was indicated to be available. If so, mark
                                // the
                                // entry with a '+' to indicate it can be
                                // expanded.
                                if (!codesToExclude.contains(branchItemCode)) {
                                    TreeItem childItem =
                                        new TreeItem(branchItemCode,
                                            getCodeDescription(branchItemNode));

                                    ti._expandable = true;
                                    AssociationList grandchildBranch =
                                        associationsNavigatedFwd ? branchItemNode
                                            .getSourceOf()
                                            : branchItemNode.getTargetOf();
                                    if (grandchildBranch != null)
                                        childItem._expandable = true;

                                    ti.addChild(childNavText, childItem);
                                }
                            }
                        }
                    }
                } else {
                    _logger.warn("WARNING: childAssociationList == null.");
                }
            }
            hmap.put(code, ti);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        _logger.debug("Run time (milliseconds) getSubconcepts: "
            + (System.currentTimeMillis() - ms) + " to resolve ");
        return hmap;
    }

    public static HashMap getAssociatedConcepts(String scheme, String version,
        String code, String[] assocNames, boolean direction) {
        try {
            LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
            LexBIGServiceConvenienceMethods lbscm =
                (LexBIGServiceConvenienceMethods) lbSvc
                    .getGenericExtension("LexBIGServiceConvenienceMethods");
            lbscm.setLexBIGService(lbSvc);
            return getAssociatedConcepts(lbSvc, lbscm, scheme, version, code,
                assocNames, direction);
        } catch (Exception ex) {
            return null;
        }
    }

    public static HashMap getAssociatedConcepts(LexBIGService lbSvc,
        LexBIGServiceConvenienceMethods lbscm, String scheme, String version,
        String code, String[] assocNames, boolean direction) {
        HashMap hmap = new HashMap();
        TreeItem ti = null;
        long ms = System.currentTimeMillis();

        Set<String> codesToExclude = Collections.EMPTY_SET;

        CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
        if (version != null)
            csvt.setVersion(version);
        ResolvedConceptReferenceList matches = null;
        Vector v = new Vector();
        try {

			Entity concept = getConceptByCode(scheme, version, null, code);
			String entityCodeNamespace = concept.getEntityCodeNamespace();
			ConceptReference focus = ConvenienceMethods.createConceptReference(code, scheme);
			focus.setCodingSchemeName(entityCodeNamespace);
            String name = concept.getEntityDescription().getContent();//getCodeDescription(lbSvc, scheme, csvt, code);

            //String name = getCodeDescription(lbSvc, scheme, csvt, code);
            ti = new TreeItem(code, name);
            ti._expandable = false;

            CodedNodeGraph cng = lbSvc.getNodeGraph(scheme, csvt, null);
            //ConceptReference focus =
            //    Constructors.createConceptReference(code, scheme);
            cng =
                cng.restrictToAssociations(Constructors
                    .createNameAndValueList(assocNames), null);
            boolean associationsNavigatedFwd = direction;

            // To remove anonymous classes (KLO, 091009), the
            // resolveCodedEntryDepth parameter cannot be set to -1.
            // Alternative -- use code to determine whether the class is
            // anonymous

            ResolvedConceptReferenceList branch = null;
            try {
                branch =
                    cng.resolveAsList(focus,
                        associationsNavigatedFwd,
                        // !associationsNavigatedFwd, 1, 2, noopList_, null,
                        // null, null, -1, false);
                        !associationsNavigatedFwd, -1, 2, _noopList, null,
                        null, null, -1, false);

            } catch (Exception e) {
                _logger
                    .error("TreeUtils getAssociatedConcepts throws exceptions.");
                return null;
            }

            for (Iterator<? extends ResolvedConceptReference> nodes =
                branch.iterateResolvedConceptReference(); nodes.hasNext();) {
                ResolvedConceptReference node = nodes.next();
                AssociationList childAssociationList = null;

                // AssociationList childAssociationList =
                // associationsNavigatedFwd ? node.getSourceOf():
                // node.getTargetOf();

                if (associationsNavigatedFwd) {
                    childAssociationList = node.getSourceOf();

                } else {
                    childAssociationList = node.getTargetOf();
                }

                if (childAssociationList != null) {
                    // Process each association defining children ...
                    for (Iterator<? extends Association> pathsToChildren =
                        childAssociationList.iterateAssociation(); pathsToChildren
                        .hasNext();) {
                        Association child = pathsToChildren.next();
                        // KLO 091009 remove anonymous nodes

                        child = processForAnonomousNodes(child);

                        String childNavText =
                            getDirectionalLabel(lbscm, scheme, csvt, child,
                                associationsNavigatedFwd);

                        // Each association may have multiple children ...
                        AssociatedConceptList branchItemList =
                            child.getAssociatedConcepts();

                        /*
                         * for (Iterator<AssociatedConcept> branchNodes =
                         * branchItemList.iterateAssociatedConcept();
                         * branchNodes .hasNext();) { AssociatedConcept
                         * branchItemNode = branchNodes.next();
                         */

                        List child_list = new ArrayList();
                        for (Iterator<? extends AssociatedConcept> branchNodes =
                            branchItemList.iterateAssociatedConcept(); branchNodes
                            .hasNext();) {
                            AssociatedConcept branchItemNode =
                                branchNodes.next();
                            child_list.add(branchItemNode);
                        }

                        SortUtils.quickSort(child_list);

                        for (int i = 0; i < child_list.size(); i++) {
                            AssociatedConcept branchItemNode =
                                (AssociatedConcept) child_list.get(i);
                            String branchItemCode =
                                branchItemNode.getConceptCode();

                            if (!branchItemCode.startsWith("@")) {
                                // Add here if not in the list of excluded
                                // codes.
                                // This is also where we look to see if another
                                // level
                                // was indicated to be available. If so, mark
                                // the
                                // entry with a '+' to indicate it can be
                                // expanded.
                                if (!codesToExclude.contains(branchItemCode)) {
                                    TreeItem childItem =
                                        new TreeItem(branchItemCode,
                                            getCodeDescription(branchItemNode));

                                    ti._expandable = true;
                                    AssociationList grandchildBranch =
                                        associationsNavigatedFwd ? branchItemNode
                                            .getSourceOf()
                                            : branchItemNode.getTargetOf();
                                    if (grandchildBranch != null)
                                        childItem._expandable = true;

                                    ti.addChild(childNavText, childItem);
                                }
                            }
                        }
                    }
                } else {
                    _logger.warn("WARNING: childAssociationList == null.");
                }
            }
            hmap.put(code, ti);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        _logger.debug("Run time (milliseconds) getSubconcepts: "
            + (System.currentTimeMillis() - ms) + " to resolve ");
        return hmap;
    }


    public static ConceptReferenceList createConceptReferenceList(
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

    public static boolean isBlank(String str) {
        if ((str == null) || str.matches("^\\s*$")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns the label to display for the given association and directional
     * indicator.
     */
    protected static String getDirectionalLabel(
        LexBIGServiceConvenienceMethods lbscm, String scheme,
        CodingSchemeVersionOrTag csvt, Association assoc, boolean navigatedFwd)
            throws LBException {

        String assocLabel =
            navigatedFwd ? lbscm.getAssociationForwardName(assoc
                .getAssociationName(), scheme, csvt) : lbscm
                .getAssociationReverseName(assoc.getAssociationName(), scheme,
                    csvt);
        // if (StringUtils.isBlank(assocLabel))
        if (isBlank(assocLabel))
            assocLabel =
                (navigatedFwd ? "" : "[Inverse]") + assoc.getAssociationName();
        return assocLabel;
    }


    public static Entity getConceptByCode(String codingSchemeName,
        String vers, String ltag, String code) {
        try {
            LexBIGService lbSvc = new RemoteServerUtil().createLexBIGService();
            if (lbSvc == null) {
                _logger.warn("lbSvc == null???");
                return null;
            }

            CodingSchemeVersionOrTag versionOrTag =
                new CodingSchemeVersionOrTag();
            versionOrTag.setVersion(vers);

            ConceptReferenceList crefs =
                createConceptReferenceList(new String[] { code },
                    codingSchemeName);

            CodedNodeSet cns = null;

            try {
                cns =
                    lbSvc.getCodingSchemeConcepts(codingSchemeName,
                        versionOrTag);
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            cns = cns.restrictToCodes(crefs);
            ResolvedConceptReferenceList matches =
                cns.resolveToList(null, null, null, 1);

            if (matches == null) {
                _logger.warn("Concept not found.");
                return null;
            }

            // Analyze the result ...
            if (matches.getResolvedConceptReferenceCount() > 0) {
                ResolvedConceptReference ref =
                    (ResolvedConceptReference) matches
                        .enumerateResolvedConceptReference().nextElement();

                Entity entry = ref.getReferencedEntry();
                return entry;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static NameAndValueList createNameAndValueList(String[] names,
        String[] values) {
        NameAndValueList nvList = new NameAndValueList();
        for (int i = 0; i < names.length; i++) {
            NameAndValue nv = new NameAndValue();
            nv.setName(names[i]);
            if (values != null) {
                nv.setContent(values[i]);
            }
            nvList.addNameAndValue(nv);
        }
        return nvList;
    }

    public List getSuperconceptList(String scheme, String version, String code) {
        ArrayList superconceptList = new ArrayList();
		HashMap hmap_super = TreeUtils.getSuperconcepts(scheme, version, code);
		if (hmap_super != null) {
			TreeItem ti = (TreeItem) hmap_super.get(code);
			if (ti != null) {
				for (String association : ti._assocToChildMap.keySet()) {
					List<TreeItem> children =
						ti._assocToChildMap.get(association);
					for (TreeItem childItem : children) {
						superconceptList.add(childItem._text + "|"
							+ childItem._code);
					}
				}
			}
		}
		Collections.sort(superconceptList);
		return superconceptList;
	}

    public static List getSubconceptList(String scheme, String version, String code) {
		return TreeUtils.getSubconceptNamesAndCodes(scheme, version, code);
	}

	public static void populateChildrenNodes(String scheme, String version, TreeItem ti, HashSet visited_nodes) {
        if (visited_nodes.contains(ti._code)) {
			//System.out.println("Loop detected -- code: " + ti._code);
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
			Vector u = parseData(nv);
			String name = (String) u.elementAt(0);
			String code = (String) u.elementAt(1);

			TreeItem children_node = new TreeItem(code, name);
			populateChildrenNodes(scheme, version, children_node, visited_nodes);
			children.add(children_node);
		}
		//ti.addAll("[inverse_is_a]", children);

		SortUtils.quickSort(children);
		ti.addAll("[inverse_is_a]", children);
	}

    public static boolean hasSubSourceInSourceHierarchy(String src) {
		if (_source_subconcept_map == null) preprocessSourceHierarchyData();
		if (_source_subconcept_map.containsKey(src)) {
			return true;
		}
		return false;
	}

    public static boolean hasValueSetsInSource(String src) {
		if (_source_subconcept_map == null) preprocessSourceHierarchyData();
		if (_source_subconcept_map.containsKey(src)) {
			return true;
		}
		return false;
	}


    public static void preprocessSourceHierarchyData() {
		_source_hierarchy = getValueSetSourceHierarchy(SOURCE_SCHEME, SOURCE_VERSION);

		_source_subconcept_map = new HashMap();
		populateSubconceptHashMap(_source_hierarchy, _source_subconcept_map);//, root);

		_source_superconcept_map = new HashMap();
		populateSuperconceptHashMap(_source_hierarchy, _source_superconcept_map);//, root);

	}

	public static HashMap getValueSetSourceHierarchy() {
		return getValueSetSourceHierarchy(SOURCE_SCHEME, SOURCE_VERSION);
	}



	public static HashMap getValueSetSourceHierarchy(String scheme, String version) {
		if (_source_hierarchy != null) return _source_hierarchy;

		_source_hierarchy = new HashMap();
		//_valueSetDefinitionSourceListing = getCodeList(SOURCE_SCHEME, SOURCE_VERSION);
		_valueSetDefinitionSourceCode2Name_map = getCodeHashMap(scheme, version);

		// value set source coding scheme
		ResolvedConceptReferenceList roots = getHierarchyRoots(scheme, version);

		HashSet visited_nodes = new HashSet();

		TreeItem ti = new TreeItem("<Root>", "Root node");
		ti._expandable = false;
		if (roots != null) {
			for (int i=0; i<roots.getResolvedConceptReferenceCount(); i++) {
				ResolvedConceptReference rcr = roots.getResolvedConceptReference(i);
				//System.out.println("\tRoot: " + rcr.getEntityDescription().getContent() + " (" + rcr.getConceptCode() + ")");

				TreeItem root_node = new TreeItem(rcr.getConceptCode(), rcr.getEntityDescription().getContent());

				populateChildrenNodes(scheme, version, root_node, visited_nodes);
				ti.addChild("inverse_is_a", root_node);
				ti._expandable = true;
			}
		}
		_source_hierarchy.put("<Root>", ti);

		_source_subconcept_map = new HashMap();
		populateSubconceptHashMap(_source_hierarchy, _source_subconcept_map);//, root);

		_source_superconcept_map = new HashMap();
		populateSuperconceptHashMap(_source_hierarchy, _source_superconcept_map);//, root);


		return _source_hierarchy;
	}

    public static void populateSubconceptHashMap(HashMap hmap, TreeItem ti) {

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

    public static void populateSuperconceptHashMap(HashMap hmap, TreeItem ti) {

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


    public static void populateSuperconceptHashMap(HashMap source_hierarchy, HashMap hmap) {
		TreeItem root = (TreeItem) source_hierarchy.get("<Root>");
        for (String association : root._assocToChildMap.keySet()) {
            List<TreeItem> children = root._assocToChildMap.get(association);
            for (TreeItem childItem : children) {
            	populateSuperconceptHashMap(hmap, childItem);
			}
		}
	}

    public static void populateSubconceptHashMap(HashMap source_hierarchy, HashMap hmap) {
		TreeItem root = (TreeItem) source_hierarchy.get("<Root>");
        for (String association : root._assocToChildMap.keySet()) {
            List<TreeItem> children = root._assocToChildMap.get(association);
            for (TreeItem childItem : children) {
            	populateSubconceptHashMap(hmap, childItem);
			}
		}
	}




    public static HashMap getCodeHashMap(String scheme, String version) {
        try {
            LexBIGService lbSvc = new RemoteServerUtil().createLexBIGService();

            if (lbSvc == null) {
                return null;
            }

            CodingSchemeVersionOrTag versionOrTag =
                new CodingSchemeVersionOrTag();
            versionOrTag.setVersion(version);

            CodedNodeSet cns = null;

            try {
                cns = lbSvc.getCodingSchemeConcepts(scheme, versionOrTag);
            } catch (Exception e1) {
                e1.printStackTrace();
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



    public static Vector getCodingSchemeURNsInValueSetDefinition(String uri) {
		try {
			java.net.URI valueSetDefinitionURI = new URI(uri);
			Vector v = new Vector();
			try {
				LexEVSValueSetDefinitionServices vsd_service = RemoteServerUtil.getLexEVSValueSetDefinitionServices();
				AbsoluteCodingSchemeVersionReferenceList codingSchemes =
					vsd_service.getCodingSchemesInValueSetDefinition(valueSetDefinitionURI);

                if (codingSchemes != null) {
					//output is all of the mapping ontologies that this code participates in.
					for(AbsoluteCodingSchemeVersionReference ref : codingSchemes.getAbsoluteCodingSchemeVersionReference()){
						v.add(ref.getCodingSchemeURN());
					}
					return SortUtils.quickSort(v);
			    } else {
					System.out.println("WARNING: DataUtils.getCodingSchemeURNsInValueSetDefinition returns null (URI: "
					   + uri + ").");
				}

			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("WARNING: DataUtils.getCodingSchemeURNsInValueSetDefinition throws exceptions.");
			}
			return v;
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("WARNING: DataUtils.getCodingSchemeURNsInValueSetDefinition throws exceptions.");
		}
		return null;
	}


    public static HashMap getValueSetDefinitionNodesWithSource(String src) {
		createVSDSource2VSDsMap();

		String text = (String) _valueSetDefinitionSourceCode2Name_map.get(src);
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
	    SortUtils.quickSort(children);
		root.addAll("[inverse_is_a]", children);
		HashMap hmap = new HashMap();
		hmap.put("<Root>", root);
        return hmap;

	}



    public static void createVSDSource2VSDsMap() {
		if (_vsd_source_to_vsds_map != null) return;

		_vsd_source_to_vsds_map = new HashMap();

		LexEVSValueSetDefinitionServices vsd_service = RemoteServerUtil.getLexEVSValueSetDefinitionServices();
		List list = vsd_service.listValueSetDefinitionURIs();
		for (int i=0; i<list.size(); i++) {
			String uri = (String) list.get(i);
			ValueSetDefinition vsd = findValueSetDefinitionByURI(uri);

			String vsd_uri = vsd.getValueSetDefinitionURI();
			java.util.Enumeration<? extends Source> sourceEnum = vsd.enumerateSource();

			while (sourceEnum.hasMoreElements()) {
				Source source = (Source) sourceEnum.nextElement();
				String src_str = source.getContent();

				Vector vsd_vec = new Vector();
				if (_vsd_source_to_vsds_map.containsKey(src_str)) {
					vsd_vec = (Vector) _vsd_source_to_vsds_map.get(src_str);
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
				_vsd_source_to_vsds_map.put(src_str, vsd_vec);
			}
		}

		Iterator it = _vsd_source_to_vsds_map.keySet().iterator();
		while (it.hasNext()) {
			String src_str = (String) it.next();
			Vector vsd_vec = (Vector) _vsd_source_to_vsds_map.get(src_str);
			for (int i=0; i<vsd_vec.size(); i++) {
				ValueSetDefinition vsd = (ValueSetDefinition) vsd_vec.elementAt(i);
				System.out.println("\t" + vsd.getValueSetDefinitionName());
			}
		}

	}


	public static boolean hasValueSetDefinitionsWithSource(String src) {
		if (_vsd_source_to_vsds_map == null) {
			createVSDSource2VSDsMap();
		}
		if (_vsd_source_to_vsds_map.containsKey(src)) {
			return true;
		}
		return false;
	}


	public static Vector getValueSetDefinitionsWithSource(String src) {
		if (_vsd_source_to_vsds_map == null) {
			createVSDSource2VSDsMap();
		}
		return (Vector) _vsd_source_to_vsds_map.get(src);
	}




	public static HashMap getRootValueSets(String scheme) {

System.out.println("ValueSetHierarchy getRootValueSets scheme " + scheme);


		if (DataUtils._codingSchemeName2URIHashMap == null) DataUtils.initializeCodingSchemeMap();
		String formalName = DataUtils.getFormalName(scheme);
		String codingSchemeURN = (String) DataUtils._codingSchemeName2URIHashMap.get(formalName);

		HashMap source_hier = getValueSetSourceHierarchy();
        Vector source_in_cs_vsd_vec = new Vector();
        HashMap source2VSD_map = new HashMap();
        HashMap uri2VSD_map = new HashMap();

		//Vector v = new Vector();
		LexEVSValueSetDefinitionServices vsd_service = RemoteServerUtil.getLexEVSValueSetDefinitionServices();
        List list = vsd_service.listValueSetDefinitionURIs();
        for (int i=0; i<list.size(); i++) {
			String uri = (String) list.get(i);
			//
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

				//v.add(uri);
			}
		}

		TreeItem root = new TreeItem("<Root>", "Root node");

        List <TreeItem> children = new ArrayList();
        Vector root_source_vec = new Vector();
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

					TreeItem ti = new TreeItem(vsd.getValueSetDefinitionURI(), name);
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

					TreeItem ti = new TreeItem(vsd.getValueSetDefinitionURI(), name);


System.out.println("Adding VSD " + vsd.getValueSetDefinitionURI() + " (" + name + ")");

					ti._expandable = true;
					children.add(ti);
				}
			}

		}

		root._expandable = false;
		SortUtils.quickSort(children);
		root.addAll("[inverse_is_a]", children);

		HashMap hmap = new HashMap();
		hmap.put("<Root>", root);
        return hmap;
	}


	public static HashMap getRootValueSets(boolean bySource) {
		if (!bySource) return getRootValueSets();


System.out.println("calling getRootValueSets " + bySource);


		HashMap source_hier = getValueSetSourceHierarchy();
        Vector source_vec = new Vector();
        HashSet source_set = new HashSet();

        HashMap source2VSD_map = new HashMap();
        HashMap uri2VSD_map = new HashMap();

		//Vector v = new Vector();
		LexEVSValueSetDefinitionServices vsd_service = RemoteServerUtil.getLexEVSValueSetDefinitionServices();
        List list = vsd_service.listValueSetDefinitionURIs();
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
        Vector root_source_vec = new Vector();
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
		SortUtils.quickSort(children);
		root.addAll("[inverse_is_a]", children);

		HashMap hmap = new HashMap();
		hmap.put("<Root>", root);
        return hmap;
	}


    public static AbsoluteCodingSchemeVersionReferenceList getAbsoluteCodingSchemeVersionReferenceList() {
        if (_valueSetParticipationHashSet == null) {
			getValueSetParticipationHashSet();
		}
        if (_csVersionList != null) return _csVersionList;
		_csVersionList = new AbsoluteCodingSchemeVersionReferenceList();
        try {
            LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
            if (lbSvc == null) return null;
            CodingSchemeRenderingList csrl = null;
            try {
                csrl = lbSvc.getSupportedCodingSchemes();
            } catch (LBInvocationException ex) {
                ex.printStackTrace();
                System.out.println("lbSvc.getSupportedCodingSchemes() FAILED..."
                    + ex.getCause());
                return _csVersionList;
            }
            CodingSchemeRendering[] csrs = csrl.getCodingSchemeRendering();
            for (int i = 0; i < csrs.length; i++) {
                int j = i + 1;
                CodingSchemeRendering csr = csrs[i];
                CodingSchemeSummary css = csr.getCodingSchemeSummary();
                String formalname = css.getFormalName();

                Boolean isActive = null;
                if (csr == null) {
                    System.out.println("\tcsr == null???");
                } else if (csr.getRenderingDetail() == null) {
                    System.out.println("\tcsr.getRenderingDetail() == null");
                } else if (csr.getRenderingDetail().getVersionStatus() == null) {
                    System.out.println("\tcsr.getRenderingDetail().getVersionStatus() == null");
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

							System.out.println(cs.getCodingSchemeURI() + " (version: " + representsVersion + ")");

							_csVersionList.addAbsoluteCodingSchemeVersionReference(csv_ref);
						}
                    } catch (Exception ex) {

                    }
                }
            }
        } catch (Exception e) {
            // e.printStackTrace();
            // return null;
        }
        return _csVersionList;
    }


    //build_cs_vs_tree
	public static HashMap getRootValueSets() {
		if (DataUtils._codingSchemeName2URIHashMap == null) DataUtils.initializeCodingSchemeMap();

        if (_valueSetParticipationHashSet == null) {
            //return null;
            getValueSetParticipationHashSet();
		}

        Vector root_cs_vec = new Vector();
        Iterator it = _valueSetParticipationHashSet.iterator();
        while (it.hasNext()) {
			String cs = (String) it.next();
			String formalName = DataUtils.getFormalName(cs);
			root_cs_vec.add(formalName);
		}
		root_cs_vec = SortUtils.quickSort(root_cs_vec);
		TreeItem root = new TreeItem("<Root>", "Root node");
        List <TreeItem> children = new ArrayList();

        SortUtils.quickSort(root_cs_vec);
        Vector sorted_root_cs_vec = new Vector();
        for (int i=0; i<root_cs_vec.size(); i++) {
			String cs = (String) root_cs_vec.elementAt(i);
			if (cs.compareTo("NCI Thesaurus") == 0) {
				sorted_root_cs_vec.add(cs);
				break;
			}
		}

        for (int i=0; i<root_cs_vec.size(); i++) {
			String cs = (String) root_cs_vec.elementAt(i);
			if (cs.compareTo("NCI Thesaurus") != 0) {
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

        //SortUtils.quickSort(children);
		root.addAll("[has_value_sets]", children);

		HashMap hmap = new HashMap();
		hmap.put("<Root>", root);

        return hmap;
	}



	public static HashSet getValueSetParticipationHashSet() {

		if (_valueSetParticipationHashSet != null) return _valueSetParticipationHashSet;
		_valueSetParticipationHashSet = new HashSet();
		LexEVSValueSetDefinitionServices vsd_service = RemoteServerUtil.getLexEVSValueSetDefinitionServices();
		List list = vsd_service.listValueSetDefinitionURIs();
		for (int i=0; i<list.size(); i++) {
			String uri = (String) list.get(i);
			Vector cs_vec = getCodingSchemeURNsInValueSetDefinition(uri);
			for (int j=0; j<cs_vec.size(); j++) {
				String cs = (String) cs_vec.elementAt(j);
				if (!_valueSetParticipationHashSet.contains(cs)) {
					_valueSetParticipationHashSet.add(cs);
				}
			}
		}
		return _valueSetParticipationHashSet;
	}



	public static boolean hasValueSet(String cs_name) {
		String scheme = DataUtils.getFormalName(cs_name);
		scheme = DataUtils.codingSchemeName2URI(scheme);

		if (_valueSetParticipationHashSet == null) {
            _valueSetParticipationHashSet = new HashSet();
			LexEVSValueSetDefinitionServices vsd_service = RemoteServerUtil.getLexEVSValueSetDefinitionServices();
			List list = vsd_service.listValueSetDefinitionURIs();
			for (int i=0; i<list.size(); i++) {
				String uri = (String) list.get(i);
				Vector cs_vec = getCodingSchemeURNsInValueSetDefinition(uri);
				for (int j=0; j<cs_vec.size(); j++) {
					String cs = (String) cs_vec.elementAt(j);
					if (!_valueSetParticipationHashSet.contains(cs)) {
						_valueSetParticipationHashSet.add(cs);
					}
				}
		    }
		}

		boolean retval = _valueSetParticipationHashSet.contains(scheme);
		return retval;
    }



    public static HashMap getSubValueSetsFromSourceCodingScheme(String code) {
		if (_subValueSet_hmap == null) {
			_subValueSet_hmap = new HashMap();
		}
		if (_subValueSet_hmap.containsKey(code)) {
			return ((HashMap) _subValueSet_hmap.get(code));
		}
    	HashMap hmap = new TreeUtils().getSubconcepts(SOURCE_SCHEME, SOURCE_VERSION, code);
    	if (hmap != null) {
			_subValueSet_hmap.put(code, hmap);
		}
		return hmap;
	}

    // code: value set URI
	public static HashMap getSubValueSets(String scheme, String code) {

		if (scheme == null) {
			// default to source hierarchy
			 HashMap hmap = getSubValueSetsFromSourceCodingScheme(code);
             TreeUtils.relabelTreeNodes(hmap);
			 return hmap;
		}

		Vector vsd_vec = getValueSetDefinitionURIsForCodingScheme(scheme);
		if (vsd_vec == null) return null;
		if (vsd_vec.size() == 0) return null;

        // find sub value sets based on value set source data:
		String codingSchemeURN = null;
		String formalName = DataUtils.getFormalName(scheme);

		codingSchemeURN = (String) DataUtils._codingSchemeName2URIHashMap.get(formalName);

		HashMap source_hier = getValueSetSourceHierarchy();
        Vector source_in_cs_vsd_vec = new Vector();
        HashMap source2VSD_map = new HashMap();
        HashMap uri2VSD_map = new HashMap();
		ValueSetDefinition root_vsd = null;
		Vector participating_vsd_vec = new Vector();

		// find participating VSDs based on scheme uri2VSD_map & source2VSD_map

		LexEVSValueSetDefinitionServices vsd_service = RemoteServerUtil.getLexEVSValueSetDefinitionServices();
        List list = vsd_service.listValueSetDefinitionURIs();
        for (int i=0; i<list.size(); i++) {
			String uri = (String) list.get(i);
			//
            Vector cs_vec = getCodingSchemeURNsInValueSetDefinition(uri);
            boolean restriction_to_cs_condition = true;
            if (codingSchemeURN != null) {
				if (!cs_vec.contains(codingSchemeURN)) {
					restriction_to_cs_condition = false;
				}
			}
            if (restriction_to_cs_condition) {
				ValueSetDefinition vsd = findValueSetDefinitionByURI(uri);
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
			System.out.println("Error: VSD " + code + " not found.");
			return null;
		} else {
			System.out.println("fining subs for VSD: " + code);
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
		if (root == null) {
			System.out.println("root == null???");
			return null;
		}
		root._expandable = false;
		if (children.size() > 0) {
			root._expandable = true;
		}

		SortUtils.quickSort(children);

		root.addAll("[inverse_is_a]", children);

		HashMap hmap = new HashMap();
		hmap.put(code, root);
        return hmap;
	}


    // code: parent vsd_uri
	public static HashMap getSubValueSets(String vsd_uri) {
		HashMap source_hier = getValueSetSourceHierarchy();
		createVSDSource2VSDsMap();
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
		SortUtils.quickSort(children);
		root.addAll("[inverse_is_a]", children);

		HashMap hmap = new HashMap();
		hmap.put("<Root>", root);
        return hmap;
	}


    public static void assignTreeNodeExpandable(HashMap hmap) {
		Iterator it = hmap.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();

			TreeItem ti = (TreeItem) hmap.get(key);
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

    public static void assignValueSetNodeExpandable(HashMap hmap) {
		Iterator it = hmap.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			TreeItem ti = (TreeItem) hmap.get(key);
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

    public static boolean isTerminologyNode(String node_id) {
	   return false;
	}

    public static boolean isValueSetSourceNode(String node_id) {
       if (_valueSetDefinitionSourceCode2Name_map == null) {
		   _valueSetDefinitionSourceCode2Name_map = getCodeHashMap(SOURCE_SCHEME, SOURCE_VERSION);
	   }
	   if (_valueSetDefinitionSourceCode2Name_map.containsKey(node_id)) return true;
	   return false;
	}

    public static boolean isValueSetNode(String node_id) {
	   if (_valueSetDefinitionURI2VSD_map.containsKey(node_id)) return true;
	   return false;
	}

    public static boolean isTerminologyNodeExpandable(String node_id) {
	   return false;
	}

    public static boolean isValueSetSourceNodeExpandable(String node_id) {
	   //HashMap = new TreeUtils().getSubconcepts(ValueSetHierarchy.SOURCE_SCHEME, ValueSetHierarchy.SOURCE_VERSION, node_id);

	   return false;
	}

    public static boolean isValueSetNodeExpandable(String node_id) {
	   return false;
	}

/*
	public static boolean containsValueSets(String node_id) {
	   return false;
	}
*/
    public static boolean isNodeExpandable(String node_id) {
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

    public static boolean hasChildren(HashMap hmap) {
		Iterator it = hmap.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			TreeItem ti = (TreeItem) hmap.get(key);
			int k = 0;
			for (String association : ti._assocToChildMap.keySet()) {
				List<TreeItem> children = ti._assocToChildMap.get(association);
				if (children.size() > 0) return true;
			}
		}
		return false;
	}


    public static boolean containsValueSets(String src_str) {
		Vector v = getVSDRootsBySource(src_str);
		if (v == null || v.size() == 0) return false;
        return true;
	}



	public static HashMap build_src_vs_tree() {
        ResolvedConceptReferenceList rcrl = TreeUtils.getHierarchyRoots(SOURCE_SCHEME, SOURCE_VERSION);

		TreeItem root = new TreeItem("<Root>", "Root node");
        List <TreeItem> children = new ArrayList();
        for (int i=0; i<rcrl.getResolvedConceptReferenceCount(); i++) {
			ResolvedConceptReference rcr = rcrl.getResolvedConceptReference(i);
			String src = rcr.getConceptCode();

			String text = rcr.getEntityDescription().getContent();
			//TreeItem ti = new TreeItem(src, src + " (" + text + ")");

			//TreeItem ti = new TreeItem(src, src);
			//KLO 091411
			TreeItem ti = new TreeItem(src, text);

			ti._expandable = containsValueSets(src);
			children.add(ti);
		}
		SortUtils.quickSort(children);
		root.addAll("[inverse_is_a]", children);

		HashMap hmap = new HashMap();
		hmap.put("<Root>", root);
        return hmap;
	}


	public static HashMap build_src_vs_tree_exclude_src_nodes() {
        ResolvedConceptReferenceList rcrl = TreeUtils.getHierarchyRoots(SOURCE_SCHEME, SOURCE_VERSION);
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
		SortUtils.quickSort(children);
		root.addAll("[inverse_is_a]", children);
		HashMap hmap = new HashMap();
		hmap.put("<Root>", root);
		hset.clear();
        return hmap;
	}


    public static HashMap expand_src_vs_tree(String node_id) {
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

/*
    public static HashMap expand_src_vs_tree_exclude_src_nodes(String vsduri) {

		HashMap hmap = new HashMap();
		HashSet hset = new HashSet();
        TreeItem root = new TreeItem("<Root>", "Root node");
        List <TreeItem> children = new ArrayList();
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
		SortUtils.quickSort(children);
		root.addAll("[inverse_is_a]", children);
		hmap.put("<Root>", root);
		hset.clear();

        return hmap;
    }
*/

	public static String getCodingSchemeName(String cs_code) {
		if (cs_code.indexOf("$") == -1) return cs_code;
		Vector v = DataUtils.parseData(cs_code, "$");
		return (String) v.elementAt(0);
	}

	public static String getValueSetURI(String cs_code) {
		if (cs_code.indexOf("$") == -1) return cs_code;
		Vector v = DataUtils.parseData(cs_code, "$");
		return (String) v.elementAt(1);
	}



    public static void initializeCS2vsdURIs_map() {
        HashSet hset = getValueSetParticipationHashSet();
        Iterator it = hset.iterator();
        while (it.hasNext()) {
			String cs_uri = (String) it.next();
			String cs_name = DataUtils.getFormalName(cs_uri);
			Vector v = getValueSetDefinitionURIsForCodingScheme(cs_name);
			if (v != null) {
				System.out.println(cs_name + " -- number of VSDs: " + v.size());
			}
		}
	}


    public static Vector getValueSetDefinitionURIsForCodingScheme(String codingSchemeName) {
		if (_cs2vsdURIs_map == null) {
			_cs2vsdURIs_map = new HashMap();
		}
		if (_cs2vsdURIs_map.containsKey(codingSchemeName)) {
			return (Vector) _cs2vsdURIs_map.get(codingSchemeName);
		}

		long ms = System.currentTimeMillis();
		Vector v = new Vector();
		String cs_uri = DataUtils.codingSchemeName2URI(codingSchemeName);
		_valueSetDefinitionURI2VSD_map = getValueSetDefinitionURI2VSD_map();
		Iterator it = _valueSetDefinitionURI2VSD_map.keySet().iterator();
		while (it.hasNext()) {
			String vsd_uri = (String) it.next();
			Vector cs_vec = DataUtils.getCodingSchemeURNsInValueSetDefinition(vsd_uri);
			if (cs_vec != null) {
				if (cs_vec.contains(cs_uri)) {
					v.add(vsd_uri);
				}
			}
		}

		_cs2vsdURIs_map.put(codingSchemeName, v);
		System.out.println("getValueSetDefinitionURIsForCodingScheme " + codingSchemeName);
		System.out.println("Run time (ms): " + (System.currentTimeMillis() - ms));
		return v;
	}



    public static Vector getVSDRootsBySource(String src_str) {
		createVSDSource2VSDsMap();
		if (_source_subconcept_map == null) preprocessSourceHierarchyData();
		Vector vsd_vec = (Vector) _vsd_source_to_vsds_map.get(src_str);
		if (vsd_vec != null && vsd_vec.size() > 0) {
			return vsd_vec;
		} else {
			//get sub_sources
			Vector vsd_in_sub_src_vec = new Vector();
			if (!_source_subconcept_map.containsKey(src_str)) return null;
			Vector sub_vec = (Vector) _source_subconcept_map.get(src_str);
			if (sub_vec == null) return null;
			if (sub_vec.size() == 0) return new Vector();
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
			return SortUtils.quickSort(vsd_in_sub_src_vec);
		}
	}


    public static Vector getVSDChildrenNodesBySource(String vsd_uri) {
		createVSDSource2VSDsMap();
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
		return SortUtils.quickSort(vsd_in_sub_src_vec);
	}




    public static HashMap expand_src_vs_tree_exclude_src_nodes(String vsduri) {

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
		SortUtils.quickSort(children);
		root.addAll("[inverse_is_a]", children);
		hmap.put("<Root>", root);
		hset.clear();
		return hmap;

    }



    // To be implemented

    public static TreeItem getVSDChildNodeBySource(ValueSetDefinition vsd) {
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




    public static TreeItem getSourceValueSetTreeBranch(ValueSetDefinition vsd) {
		TreeItem ti = new TreeItem(vsd.getValueSetDefinitionURI(), vsd.getValueSetDefinitionName());
		ti._expandable = false;
		List <TreeItem> children = new ArrayList();
		Vector sub_vsd_vec = getVSDChildrenNodesBySource(vsd.getValueSetDefinitionURI());
		if (sub_vsd_vec != null && sub_vsd_vec.size() > 0) {
			//ti._expandable = true;
			for (int j=0; j<sub_vsd_vec.size(); j++) {
				ValueSetDefinition child_vsd = (ValueSetDefinition) sub_vsd_vec.elementAt(j);
				TreeItem child = getSourceValueSetTreeBranch(child_vsd);
				children.add(child);
				ti._expandable = true;
			}
		}
	    SortUtils.quickSort(children);
	    ti.addAll("[inverse_is_a]", children);
	    return ti;
	}



    public static HashMap getSourceValueSetTree(String scheme, String version) {
        ResolvedConceptReferenceList rcrl = TreeUtils.getHierarchyRoots(SOURCE_SCHEME, SOURCE_VERSION);
        if (rcrl == null) {
			return null;
		}

		TreeItem super_root = new TreeItem("<Root>", "Root node");

		List <TreeItem> branch = new ArrayList();
		//HashSet hset = new HashSet();

        //List <TreeItem> children = new ArrayList();
        for (int i=0; i<rcrl.getResolvedConceptReferenceCount(); i++) {
			ResolvedConceptReference rcr = rcrl.getResolvedConceptReference(i);
			String src = rcr.getConceptCode();

			Vector vsd_root_vec = getVSDRootsBySource(src);
			if (vsd_root_vec != null) {
				for (int j=0; j<vsd_root_vec.size(); j++) {
					ValueSetDefinition root_vsd = (ValueSetDefinition) vsd_root_vec.elementAt(j);
					TreeItem root = new TreeItem(root_vsd.getValueSetDefinitionURI(), root_vsd.getValueSetDefinitionName());
					root._expandable = false;

					List <TreeItem> children = new ArrayList();
					Vector sub_vsd_vec = getVSDChildrenNodesBySource(root_vsd.getValueSetDefinitionURI());
					if (sub_vsd_vec != null && sub_vsd_vec.size() > 0) {
						for (int k=0; k<sub_vsd_vec.size(); k++) {
							ValueSetDefinition child_vsd = (ValueSetDefinition) sub_vsd_vec.elementAt(k);
							TreeItem child = getSourceValueSetTreeBranch(child_vsd);
							children.add(child);
							root._expandable = true;
						}
					}
					SortUtils.quickSort(children);
					root.addAll("[inverse_is_a]", children);
	                branch.add(root);
	                super_root._expandable = true;
				}
			}
		}
		SortUtils.quickSort(branch);
		super_root.addAll("[inverse_is_a]", branch);
		HashMap hmap = new HashMap();
		hmap.put("<Root>", super_root);
		//hset.clear();
        return hmap;
	}




/*
        List list = null;// new ArrayList();
        String key = scheme + "$" + version + "$valueset" + "$root";
        JSONArray nodesArray = null;


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
                HashMap hmap = ValueSetHierarchy.getRootValueSets(scheme);
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
*/



    public static HashMap getCodingSchemeValueSetTree(String scheme, String version) {


System.out.println("getCodingSchemeValueSetTree: scheme " + scheme);
System.out.println("getCodingSchemeValueSetTree: version " + version);



		List <TreeItem> branch = new ArrayList();

		TreeItem super_root = new TreeItem("<Root>", "Root node");


System.out.println("calling getRootValueSets: scheme " + scheme);


		HashMap hmap = getRootValueSets(scheme);
		TreeItem root = (TreeItem) hmap.get("<Root>");
		JSONArray nodesArray = new JSONArray();

		for (String association : root._assocToChildMap.keySet()) {
			 List<TreeItem> children = super_root._assocToChildMap.get(association);
			 for (TreeItem childItem : children) {
				 String code = childItem._code;
				 String name = childItem._text;
				 TreeItem cs_vs_root = new TreeItem(code, name);

System.out.println("CS VS tree root code: " + code);
System.out.println("CS VS tree root name: " + name);

   				 cs_vs_root._expandable = true;
				 List <TreeItem> cs_vs_root_children = new ArrayList();

				 /*

				 Vector sub_vsd_vec = getVSDChildrenNodesBySource(root_vsd.getValueSetDefinitionURI());
				 if (sub_vsd_vec != null && sub_vsd_vec.size() > 0) {
					for (int k=0; k<sub_vsd_vec.size(); k++) {
						ValueSetDefinition child_vsd = (ValueSetDefinition) sub_vsd_vec.elementAt(k);
						TreeItem child = getSourceValueSetTreeBranch(child_vsd);
						children.add(child);
						root._expandable = true;
					}
				 }
				 */
				 SortUtils.quickSort(cs_vs_root_children);
				 cs_vs_root.addAll("[inverse_is_a]", cs_vs_root_children);

				 branch.add(cs_vs_root);
				 super_root._expandable = true;
			}
		}
		SortUtils.quickSort(branch);
		super_root.addAll("[inverse_is_a]", branch);
		hmap = new HashMap();
		hmap.put("<Root>", super_root);
		//hset.clear();
        return hmap;
	}



///////////////////////////////////////////////////////////////////////////////////////////////////

	public static void main(String[] args) throws Exception {
		ValueSetHierarchy test = new ValueSetHierarchy();
        String cs_url = "http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#";
	    HashMap root_hmap = ValueSetHierarchy.getRootValueSets(cs_url);
	    if (root_hmap != null) {
			printTree(root_hmap);
		}
	}

}

