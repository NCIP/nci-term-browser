package gov.nih.nci.evs.browser.utils;

import gov.nih.nci.evs.browser.bean.*;

import java.io.*;
import java.net.URI;

import java.text.*;
import java.util.*;
import java.sql.*;
import javax.faces.model.*;
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

import gov.nih.nci.evs.browser.properties.*;
import static gov.nih.nci.evs.browser.common.Constants.*;

import gov.nih.nci.evs.browser.common.Constants;
import gov.nih.nci.evs.browser.bean.MappingData;

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
import org.lexevs.property.PropertyExtension;

import org.lexgrid.resolvedvalueset.LexEVSResolvedValueSetService;
import org.lexgrid.resolvedvalueset.impl.LexEVSResolvedValueSetServiceImpl;

import org.LexGrid.commonTypes.Property;
import org.LexGrid.commonTypes.Properties;

import org.apache.commons.lang.*;

import org.lexgrid.resolvedvalueset.impl.LexEVSResolvedValueSetServiceImpl;
import org.lexgrid.resolvedvalueset.LexEVSResolvedValueSetService;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.ActiveOption;

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
 *          Modification history Initial implementation kim.ong@ngc.com
 *
 */
public class DataUtils {
    private static Logger _logger = Logger.getLogger(DataUtils.class);
    public static Boolean VALUE_SET_TAB_AVAILABLE = null;
    public static Boolean NCI_THESAURUS_AVAILABLE = null;
    private static LocalNameList _noopList = new LocalNameList();
    private int _maxReturn = -1;//5000;
    private static ValueSetHierarchy valueSetHierarchy = null;
    private static List _ontologies = null;
    public CodingSchemeRenderingList _csrl = null;

    private static HashSet _codingSchemeHashSet = null;
    private static HashMap _csnv2codingSchemeNameMap = null;
    private static HashMap _csnv2VersionMap = null;

    private static boolean initializeValueSetHierarchy = true;
    private static boolean valueSetHierarchyInitialized = false;
    private static boolean hasNoValueSet = false;

    // ==================================================================================
    public static final int ALL = 0;
    public static final int PREFERRED_ONLY = 1;
    public static final int NON_PREFERRED_ONLY = 2;

    private static final int RESOLVE_SOURCE = 1;
    private static final int RESOLVE_TARGET = -1;
    private static final int RESTRICT_SOURCE = -1;
    private static final int RESTRICT_TARGET = 1;

    public static final int SEARCH_NAME_CODE = 1;
    public static final int SEARCH_DEFINITION = 2;

    public static final int SEARCH_PROPERTY_VALUE = 3;
    public static final int SEARCH_ROLE_VALUE = 6;
    public static final int SEARCH_ASSOCIATION_VALUE = 7;

    public String _ncicbContactURL = null;
    public String _terminologySubsetDownloadURL = null;
    public String _term_suggestion_application_url = null;
    public String _ncitBuildInfo = null;
    public String _ncitAppVersion = null;
    public String _ncitAppVersionDisplay = null;
    public String _ncitAnthillBuildTagBuilt = null;
    public String _evsServiceURL = null;
    public String _ncimURL = null;

    private static HashMap _namespace2CodingScheme = null;

    private static HashMap _formalName2LocalNameHashMap = null;
    private static HashMap _formalName2LocalNamesHashMap = null;
    private static HashMap _localName2FormalNameHashMap = null;
    private static Set _vocabularyNameSet = null;
    private static HashMap _formalName2MetadataHashMap = null;
    private static HashMap _displayName2FormalNameHashMap = null;

    private static HashMap _formalNameVersion2LocalNameHashMap = null;
    private static HashMap _localNameVersion2FormalNameVersionHashMap = null;
    private static HashMap _formalNameVersion2MetadataHashMap = null;
    private static HashMap _displayNameVersion2FormalNameVersionHashMap = null;
    private static HashMap _uri2CodingSchemeNameHashMap = null;
    private static HashMap _codingSchemeName2URIHashMap = null;

    private static Vector _nonConcept2ConceptAssociations = null;
    private static String _defaultOntologiesToSearchOnStr = null;

    private static HashSet _vocabulariesWithConceptStatusHashSet = null;
    private static HashSet _vocabulariesWithoutTreeAccessHashSet = null;

    private static HashMap _formalName2NCImSABHashMap = null;

    private static HashMap _isMappingHashMap = null;
    private static HashMap _mappingDisplayNameHashMap = null;

    private static HashMap _codingSchemeTagHashMap = null;

    private static final HashMap _valueSetDefinitionHierarchyHashMap = null;
    private static Vector  _availableValueSetDefinitionSources = null;
    private static Vector  _valueSetDefinitionHierarchyRoots = null;

    private static HashMap _codingScheme2MappingCodingSchemes = null;
    private static Vector _valueSetDefinitionMetadata = null;

    private static HashMap _formalName2VersionsHashMap = null;
    private static HashMap _versionReleaseDateHashMap = null;

    private static Vector _source_code_schemes = null;

    private static HashMap sourceValueSetTree = null;
    private static HashMap terminologyValueSetTree = null;

    private static StringBuffer sourceValueSetTreeStringBuffer = null;
    private static HashMap sourceValueSetCheckboxid2NodeIdMap = null;

    private static StringBuffer terminologyValueSetTreeStringBuffer = null;
    private static HashMap terminologyValueSetCheckboxid2NodeIdMap = null;

    private static HashMap _formalName2VirtualIdMap = null;

    private static HashMap _sourceValueSetTreeKey2TreeItemMap = null;

    private static HashMap _terminologyValueSetDescriptionHashMap = null;

    private static HashMap _listOfCodingSchemeVersionsUsedInResolutionHashMap = null;

    private static HashMap _RVSCSFormalName2VersionHashMap = null;

    private static HashMap _RVSCSURI2VersionHashMap = null;

    private static HashMap _VSDName2URIHashMap = null;
    private static HashMap _VSDURI2NameHashMap = null;

    private static HashMap _formalName2DisplayNameHashMap = null;

    private static HashMap _visualizationWidgetHashMap = null;

    private static String _api_key = null;

    public String _ncitURL = null;

    private static HashMap resovedValueSetHashMap = null;

    private static Vector _sortedOntologies = null;

    private static HashMap _productionVersionHashMap = null;

    /////////////////////////////
    private static HashMap _rootValueSets = null;


    public DataUtils() {

    }

    static {
		System.out.println("Initialization ...");
		long ms0 = System.currentTimeMillis();
		long ms = System.currentTimeMillis();

		VALUE_SET_TAB_AVAILABLE = isCodingSchemeAvailable(Constants.TERMINOLOGY_VALUE_SET_NAME);
		NCI_THESAURUS_AVAILABLE = isCodingSchemeAvailable(Constants.NCIT_CS_NAME);

        if (VALUE_SET_TAB_AVAILABLE != null && VALUE_SET_TAB_AVAILABLE.equals(Boolean.TRUE)) {
			resovedValueSetHashMap = getResolvedValueSetHashMap();
			System.out.println("getResolvedValueSetHashMap run time (ms): " + (System.currentTimeMillis() - ms));
			ms = System.currentTimeMillis();
		} else {
			hasNoValueSet = true;
		}
		setCodingSchemeMap();
		System.out.println("setCodingSchemeMap run time (ms): " + (System.currentTimeMillis() - ms));
		ms = System.currentTimeMillis();

		if (_valueSetDefinitionMetadata == null) {
			_valueSetDefinitionMetadata = getValueSetDefinitionMetadata();
        }
		System.out.println("getValueSetDefinitionMetadata run time (ms): " + (System.currentTimeMillis() - ms));
		ms = System.currentTimeMillis();
        if (_namespace2CodingScheme == null) {
            _namespace2CodingScheme = getNamespaceId2CodingSchemeFormalNameMapping();
		}
		System.out.println("getNamespaceId2CodingSchemeFormalNameMapping run time (ms): " + (System.currentTimeMillis() - ms));

		ms = System.currentTimeMillis();
		if (_defaultOntologiesToSearchOnStr == null) {
            _defaultOntologiesToSearchOnStr = getDefaultOntologiesToSearchOnStr();
		}

		System.out.println("getDefaultOntologiesToSearchOnStr run time (ms): " + (System.currentTimeMillis() - ms));

		updateListOfCodingSchemeVersionsUsedInResolutionHashMap();
		System.out.println("updateListOfCodingSchemeVersionsUsedInResolutionHashMap run time (ms): " + (System.currentTimeMillis() - ms));
		ms = System.currentTimeMillis();

		_sortedOntologies = getSortedOntologies();
		System.out.println("getSortedOntologies run time (ms): " + (System.currentTimeMillis() - ms));

		ms = System.currentTimeMillis();

		if (_visualizationWidgetHashMap == null) {
            _visualizationWidgetHashMap = getNCBOWidgetString();
		}

		System.out.println("getNCBOWidgetString run time (ms): " + (System.currentTimeMillis() - ms));
		ms = System.currentTimeMillis();

		_api_key = NCItBrowserProperties.getNCBO_API_KEY();
		System.out.println("Total DataUtils initialization run time (ms): " + (System.currentTimeMillis() - ms0));

	}

	public static Set getVocabularyNameSet() {
		return _vocabularyNameSet;
	}

    public static Vector getResovedValueSetVersions(String rvs_uri) {
		if (!hasValueSets()) return null;
		return (Vector) resovedValueSetHashMap.get(rvs_uri);
	}

	public static String getValueSetName(String rvs_uri) {
		if (!hasValueSets()) return null;
		if (!_VSDURI2NameHashMap.containsKey(rvs_uri)) return null;
		return (String) _VSDURI2NameHashMap.get(rvs_uri);
	}

    public static boolean hasValueSets() {
		return !hasNoValueSet;
	}

    public static ValueSetHierarchy getValueSetHierarchy() {
		return valueSetHierarchy;
	}

    public static HashMap getResolvedValueSetHashMap() {
		if (!hasValueSets()) return null;
		if (resovedValueSetHashMap != null) return resovedValueSetHashMap;
		HashMap hmap = new HashMap();
		_VSDURI2NameHashMap = new HashMap();
		try {
			long ms = System.currentTimeMillis();
			LexBIGService lbs = RemoteServerUtil.createLexBIGService();
			List<CodingScheme> choices = new ArrayList<CodingScheme>();
			LexEVSResolvedValueSetService lrvs = new LexEVSResolvedValueSetServiceImpl(lbs);
			List<CodingScheme> schemes = lrvs.listAllResolvedValueSets();
			for (int i = 0; i < schemes.size(); i++) {
				CodingScheme cs = schemes.get(i);
				int j = i+1;
				String key = cs.getCodingSchemeURI();
				String name = cs.getCodingSchemeName();
				_VSDURI2NameHashMap.put(key, name);

				String value = cs.getRepresentsVersion();
				Vector v = new Vector();
				if (hmap.containsKey(key)) {
					v = (Vector) hmap.get(key);
				}
				v.add(value);
				hmap.put(key, v);
			}
			System.out.println("Total run time (ms): " + (System.currentTimeMillis() - ms));

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return hmap;
    }

	public static void setAPIKey(String apikey) {
		_api_key = apikey;
	}

	public static String getAPIKey() {
		return _api_key;
	}

    public static List getOntologyList() {
        if (_ontologies == null)
            setCodingSchemeMap();
        return _ontologies;
    }

    private static void setTerminologyValueSetDescriptionHashMap() {
		if (_terminologyValueSetDescriptionHashMap == null) {
			String prod_version = getVocabularyVersionByTag(Constants.TERMINOLOGY_VALUE_SET_NAME, Constants.PRODUCTION);
			_terminologyValueSetDescriptionHashMap = getPropertyValues(Constants.TERMINOLOGY_VALUE_SET_NAME, prod_version, "GENERIC", "Description");
		}
	}

    public static String getTerminologyValueSetDescription(String node_id) {
		if (node_id == null) return null;
		if (_terminologyValueSetDescriptionHashMap == null) {
			return "DESCRIPTION NOT AVAILABLE";
		}
		String description = (String) _terminologyValueSetDescriptionHashMap.get(node_id);
		if (description == null) return "DESCRIPTION NOT AVAILABLE";
		return description;
	}

    public static StringBuffer getSourceValueSetTreeStringBuffer() {
		if (sourceValueSetTreeStringBuffer == null) {
			initializeValueSetHierarchy();
		}
		return sourceValueSetTreeStringBuffer;
	}

    public static StringBuffer getCodingSchemeValueSetTreeStringBuffer() {
		if (terminologyValueSetTreeStringBuffer == null) {
			initializeValueSetHierarchy();
		}
		return terminologyValueSetTreeStringBuffer;
	}

    public static HashMap getRVSCSURI2VersionHashMap() {
		return _RVSCSURI2VersionHashMap;
	}


    public static HashMap getDefaultFormalName2VirtualIdMap() {
		HashMap formalName2VirtualIdMap = new HashMap();
		formalName2VirtualIdMap.put(Constants.NCIT, Constants.NCIT_NCBO_ID);
		formalName2VirtualIdMap.put("NCIt", Constants.NCIT_NCBO_ID);
		formalName2VirtualIdMap.put(Constants.NCIT_CS_NAME, Constants.NCIT_NCBO_ID);
		formalName2VirtualIdMap.put(Constants.NCI_THESAURUS, Constants.NCIT_NCBO_ID);
	    return formalName2VirtualIdMap;
	}

    public static HashMap get_localName2FormalNameHashMap() {
		return _localName2FormalNameHashMap;
	}

    public static HashSet get_vocabulariesWithoutTreeAccessHashSet() {
		return _vocabulariesWithoutTreeAccessHashSet;
	}

    private static boolean isCodingSchemeSupported(String codingSchemeName) {
        if (_codingSchemeHashSet == null)
            setCodingSchemeMap();
        return _codingSchemeHashSet.contains(codingSchemeName);
    }

    public static HashMap get_codingSchemeName2URIHashMap() {
		return _codingSchemeName2URIHashMap;
	}

    private static void setMappingDisplayNameHashMap() {
 		_mappingDisplayNameHashMap = new HashMap();
 		Iterator it = _csnv2codingSchemeNameMap.keySet().iterator();
 		while (it.hasNext()) {
 			String value = (String) it.next();
 			String cs = (String) _csnv2codingSchemeNameMap.get(value);
 			String version = (String) _csnv2VersionMap.get(value);
 			LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
 			HashMap hmap = new MetadataUtils(lbSvc).getMappingDisplayHashMap(cs, version);
 			if (hmap != null) {
 				_mappingDisplayNameHashMap.put(cs, hmap);
 			}
 		}
	}

	public static void initializeCodingSchemeMap() {
		setCodingSchemeMap();
	}

	public static boolean isResolvedValueSetCodingScheme(CodingScheme cs) {
		if (resovedValueSetHashMap == null) {
			resovedValueSetHashMap = getResolvedValueSetHashMap();
		}
		if (resovedValueSetHashMap == null) return false;
		return resovedValueSetHashMap.containsKey(cs.getCodingSchemeURI());
	}

	public static AbsoluteCodingSchemeVersionReferenceList getListOfCodingSchemeVersionsUsedInResolution(String codingScheme) {
		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
		return new CodingSchemeDataUtils(lbSvc).getListOfCodingSchemeVersionsUsedInResolution(codingScheme);
	}

	public static Vector getRVSCSVersionsByFormalName(String RVSCS_formalname) {
		if (!_RVSCSFormalName2VersionHashMap.containsKey(RVSCS_formalname)) return null;
		return (Vector) _RVSCSFormalName2VersionHashMap.get(RVSCS_formalname);
    }

	public static Vector getRVSCSVersionsByURI(String RVSCS_URI) {
		if (!_RVSCSURI2VersionHashMap.containsKey(RVSCS_URI)) return null;
		return (Vector) _RVSCSURI2VersionHashMap.get(RVSCS_URI);
    }

    private static void setCodingSchemeMap() {
        _logger.debug("Initializing ...");
        _productionVersionHashMap = new HashMap();
        _source_code_schemes = new Vector();
        _codingSchemeHashSet = new HashSet();
        _ontologies = new ArrayList();
        _csnv2codingSchemeNameMap = new HashMap();
        _csnv2VersionMap = new HashMap();
        _formalName2LocalNameHashMap = new HashMap();
        _formalName2LocalNamesHashMap = new HashMap();
        _localName2FormalNameHashMap = new HashMap();
        _formalName2MetadataHashMap = new HashMap();
        _displayName2FormalNameHashMap = new HashMap();
        _vocabulariesWithConceptStatusHashSet = new HashSet();
        _vocabulariesWithoutTreeAccessHashSet = new HashSet();

        _formalNameVersion2LocalNameHashMap = new HashMap();
        _localNameVersion2FormalNameVersionHashMap = new HashMap();
        _formalNameVersion2MetadataHashMap = new HashMap();
        _displayNameVersion2FormalNameVersionHashMap = new HashMap();
        _uri2CodingSchemeNameHashMap = new HashMap();
        _codingSchemeName2URIHashMap = new HashMap();

        _isMappingHashMap = new HashMap();

        _codingSchemeTagHashMap = new HashMap();

        _formalName2VersionsHashMap = new HashMap();
        _versionReleaseDateHashMap = new HashMap();
        _listOfCodingSchemeVersionsUsedInResolutionHashMap = new HashMap();
        _RVSCSFormalName2VersionHashMap = new HashMap();
        _RVSCSURI2VersionHashMap = new HashMap();
        _formalName2DisplayNameHashMap = new HashMap();

        Vector nv_vec = new Vector();
        boolean includeInactive = false;

        try {
            LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
            if (lbSvc == null) {
                _logger
                    .warn("WARNING: Unable to connect to instantiate LexBIGService ???");
                return;
            }
            CodingSchemeRenderingList csrl = null;
            try {
                csrl = lbSvc.getSupportedCodingSchemes();
            } catch (LBInvocationException ex) {
                ex.printStackTrace();
                _logger.error("lbSvc.getSupportedCodingSchemes() FAILED..."
                    + ex.getCause());
                return;
            }
            CodingSchemeRendering[] csrs = csrl.getCodingSchemeRendering();
            for (int i = 0; i < csrs.length; i++) {
                int j = i + 1;
                CodingSchemeRendering csr = csrs[i];
                CodingSchemeSummary css = csr.getCodingSchemeSummary();
                String formalname = css.getFormalName();

                if (!_formalName2VersionsHashMap.containsKey(formalname)) {
					Vector version_vec = new Vector();
					_formalName2VersionsHashMap.put(formalname, version_vec);
				}

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


                Boolean bool_obj = versionHasTag(csr, Constants.PRODUCTION);
                if (bool_obj != null && bool_obj.booleanValue()) {
					_productionVersionHashMap.put(formalname, representsVersion);
				}

                if ((includeInactive && isActive == null)
                    || (isActive != null && isActive.equals(Boolean.TRUE))
                    || (includeInactive && (isActive != null && isActive
                        .equals(Boolean.FALSE)))) {
                    CodingSchemeVersionOrTag vt =
                        new CodingSchemeVersionOrTag();
                    vt.setVersion(representsVersion);

                    Vector ver_vec = (Vector) _formalName2VersionsHashMap.get(formalname);
                    if (ver_vec == null) {
						ver_vec = new Vector();
					}
					if (!ver_vec.contains(representsVersion)) {
						ver_vec.add(representsVersion);
						_formalName2VersionsHashMap.put(formalname, ver_vec);
					}
                    try {
                        CodingScheme cs = lbSvc.resolveCodingScheme(formalname, vt);
                        if (isResolvedValueSetCodingScheme(cs)) {
							String cs_uri = cs.getCodingSchemeURI();
							String cs_name = cs.getCodingSchemeName();
							String cs_version = cs.getRepresentsVersion();
							String cs_formalname = cs.getFormalName();
							Vector w = (Vector) _RVSCSFormalName2VersionHashMap.get(cs_formalname);
							if (w == null) {
								w = new Vector();
							}
							w.add(cs_version);
							_RVSCSFormalName2VersionHashMap.put(cs_formalname, w);

							Vector w2 = (Vector) _RVSCSURI2VersionHashMap.get(cs_uri);
							if (w2 == null) {
								w2 = new Vector();
							}
							w2.add(cs_version);
							_RVSCSURI2VersionHashMap.put(cs_uri, w);


							HashMap hmap = new HashMap();
							AbsoluteCodingSchemeVersionReferenceList acsvr = getListOfCodingSchemeVersionsUsedInResolution(cs_name);
							if (acsvr != null) {
								for(AbsoluteCodingSchemeVersionReference abrefs :acsvr.getAbsoluteCodingSchemeVersionReference()){
									hmap.put(abrefs.getCodingSchemeURN(),  abrefs.getCodingSchemeVersion());
								}
								_listOfCodingSchemeVersionsUsedInResolutionHashMap.put(cs_name, hmap);
							}
                        } else {
                        Vector prop_quals = getSupportedPropertyQualifier(cs);
                        if (prop_quals.contains("source-code")) {
							if (!_source_code_schemes.contains(formalname)) {
								_source_code_schemes.add(formalname);
							}
						}

                        _uri2CodingSchemeNameHashMap.put(cs.getCodingSchemeURI(), cs.getCodingSchemeName());
                        _uri2CodingSchemeNameHashMap.put(formalname, cs.getCodingSchemeName());

                        _codingSchemeName2URIHashMap.put(cs.getCodingSchemeName(), cs.getCodingSchemeURI());

                        boolean isMapping = isMapping(cs.getCodingSchemeName(), representsVersion);
                        //_isMappingHashMap.put(cs.getCodingSchemeName(), new Boolean(isMapping));

                        _isMappingHashMap.put(cs.getCodingSchemeName(), Boolean.valueOf(isMapping));

                        String[] localnames = cs.getLocalName();
                        for (int m = 0; m < localnames.length; m++) {
                            String localname = localnames[m];
                            _logger.debug("\tlocal name: " + localname);
                            _localName2FormalNameHashMap.put(localname,
                                formalname);
                        }
                        _localName2FormalNameHashMap.put(cs.getCodingSchemeURI(), formalname);
                        _localName2FormalNameHashMap.put(cs.getCodingSchemeName(), formalname);
                        NameAndValue[] nvList =
                            new MetadataUtils(lbSvc).getMetadataProperties(cs);
                        if (nvList == null || nvList.length <= 0) {
                            //_logger.warn("\t*******************************************************************");
                            _logger.warn("\t*** Warning: Metadata properties are possibly not loaded.       ***");
                            _logger.warn("\t*** MetadataUtils.getMetadataProperties(cs) returns empty list. ***");
                            //_logger.warn("\t*******************************************************************");
                        }
                        //if (cs != null && nvList != null) {
						if (nvList != null) {

                            String css_local_name = css.getLocalName();
                            boolean localname_exist = false;
                            for (int lcv = 0; lcv < localnames.length; lcv++) {
                                String local_nm = (String) localnames[lcv];
                                if (local_nm.compareTo(css_local_name) == 0) {
                                    localname_exist = true;
                                    break;
                                }
                            }
                            if (!localname_exist) {
                                _logger.debug("\tlocal name (*): "
                                    + css_local_name);
                            }
                            String value =
                                formalname + " (version: " + representsVersion
                                    + ")";
                            _logger.debug("\tformalname & verson: " + value);
                            Vector<String> propertyNames =
                                getPropertyNameListData(formalname,
                                    representsVersion);
                            if (propertyNames.contains("Concept_Status")) {
                                _vocabulariesWithConceptStatusHashSet
                                    .add(formalname);
                                _logger
                                    .debug("\tConcept_Status property supported.");
                            }

                            if (!_codingSchemeHashSet.contains(formalname)) {
                                _codingSchemeHashSet.add(formalname);
                            }

                            _formalName2LocalNameHashMap.put(formalname,
                                css_local_name);

                            Vector localname_vec = new Vector();
                            if (localnames != null) {
								localname_vec = new Vector(Arrays.asList(localnames));
							}
							localname_vec.add(css_local_name);
                            _formalName2LocalNamesHashMap.put(formalname, localname_vec);

                            _formalNameVersion2LocalNameHashMap.put(formalname + "$" + representsVersion,
                                 css_local_name);

                            _formalName2LocalNameHashMap.put(css_local_name,
                                css_local_name);

                            _localName2FormalNameHashMap.put(formalname,
                                formalname);

                            _localNameVersion2FormalNameVersionHashMap.put(css_local_name +"$" + representsVersion,
                                                                           formalname + "$" + representsVersion);

                            _localName2FormalNameHashMap.put(css_local_name,
                                formalname);

                            _codingSchemeName2URIHashMap.put(formalname, cs.getCodingSchemeURI());

                            Vector metadataProperties = new Vector();
                            for (int k = 0; k < nvList.length; k++) {
                                NameAndValue nv = (NameAndValue) nvList[k];
                                metadataProperties.add(nv.getName() + "|"
                                    + nv.getContent());

                                // to be modified
                                //_vocabulariesWithoutTreeAccessHashSet
                                if (nv.getName().compareTo(Constants.TREE_ACCESS_ALLOWED) == 0 && nv.getContent().compareTo("false") == 0) {
									_vocabulariesWithoutTreeAccessHashSet.add(formalname);
									_logger.debug("\t" + "Tree not accessible.");
								}

								if (nv.getName().compareTo("version_releaseDate") == 0) {
									_versionReleaseDateHashMap.put(formalname + "$" + representsVersion, nv.getContent());
								}
                            }
                            _logger.debug("\t" + nvList.length
                                + " MetadataProperties cached for "
                                + formalname);

                            _formalName2MetadataHashMap.put(formalname, metadataProperties);
                            _formalName2MetadataHashMap.put(cs.getCodingSchemeName(), metadataProperties);
                            _formalNameVersion2MetadataHashMap.put(formalname + "$" + representsVersion,
                                metadataProperties);
                            _formalNameVersion2MetadataHashMap.put(cs.getCodingSchemeName() + "$" + representsVersion,
                                metadataProperties);
                            String displayName =
                                getMetadataValue(formalname, "display_name");
                            _logger.debug("\tdisplay_name: " + displayName);
                            _displayName2FormalNameHashMap.put(displayName, formalname);
                            _formalName2DisplayNameHashMap.put(formalname, displayName);
                            _formalName2DisplayNameHashMap.put(cs.getCodingSchemeName(), displayName);
                            _displayNameVersion2FormalNameVersionHashMap.put(displayName + "$" + representsVersion,
                                formalname + "$" + representsVersion);
                            String term_browser_version = getMetadataValue(formalname, "term_browser_version");
                            _logger.debug("\tterm_browser_version: "
                                + term_browser_version);
                            nv_vec.add(value);
                            _logger.debug("\tformal name: " + formalname);
                            _csnv2codingSchemeNameMap.put(value, formalname);
                            _csnv2VersionMap.put(value, representsVersion);
                            _logger.debug("\trepresentsVersion: " + representsVersion);

                        } else {
                            _logger
                                .error("WARNING: MetadataUtils.getMetadataPropertyList returns null??? "
                                    + formalname);
                            _logger.error("\t\trepresentsVersion "
                                + representsVersion);
                        }
					    }
                    } catch (Exception ex) {
                        _logger
                            .error("\tWARNING: Unable to resolve coding scheme "
                                + formalname
                                + " possibly due to missing security token.");
                        _logger.error("\t\tAccess to " + formalname
                            + " denied.");
                        ex.printStackTrace();
                    }

                } else {
                    _logger.error("\tWARNING: setCodingSchemeMap discards "
                        + formalname);
                    _logger.error("\t\trepresentsVersion " + representsVersion);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (nv_vec.size() > 0) {
            nv_vec = SortUtils.quickSort(nv_vec);
            for (int k = 0; k < nv_vec.size(); k++) {
                String value = (String) nv_vec.elementAt(k);
                if (!value.startsWith(Constants.TERMINOLOGY_VALUE_SET) && !value.startsWith(Constants.TERMINOLOGY_VALUE_SET_NAME)) {
	                _ontologies.add(new SelectItem(value, value));
				}
            }
        }
        _formalName2NCImSABHashMap = createFormalName2NCImSABHashMap();
        if (!_localName2FormalNameHashMap.containsKey(Constants.NCIT)) {
			_localName2FormalNameHashMap.put(Constants.NCIT, Constants.NCIT_CS_NAME);
		}
        //dumpHashMap(_formalName2NCImSABHashMap);
        setMappingDisplayNameHashMap();
         _vocabularyNameSet = _localName2FormalNameHashMap.keySet();

        if (initializeValueSetHierarchy) {
            initializeValueSetHierarchy();
	    }
    }

    public static HashMap getFormalName2DisplayNameHashMap() {
		return _formalName2DisplayNameHashMap;
	}

    private static void initializeValueSetHierarchy() {
		if (!hasValueSets()) return;
		long ms = System.currentTimeMillis();
		if (valueSetHierarchyInitialized) return;
		_VSDName2URIHashMap = getVSDName2URIHashMap();
		_logger.debug("Initializing Value Set Metadata ...");
		Vector v = getValueSetDefinitionMetadata();
		if (v == null || v.size() == 0) {
			return;
		}
		_logger.debug("Done Initializing Value Set Metadata ...");
		_logger.debug("\tInitializing ValueSetHierarchy ...");
		_logger.debug("\tpreprocessSourceHierarchyData ...");

        LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
        LexEVSValueSetDefinitionServices vsd_service = RemoteServerUtil.getLexEVSValueSetDefinitionServices();

        valueSetHierarchy = new ValueSetHierarchy(lbSvc,
                                   vsd_service,
                                   _localName2FormalNameHashMap,
                                   _codingSchemeName2URIHashMap);

		valueSetHierarchy.preprocessSourceHierarchyData();

		valueSetHierarchy.getValueSetParticipationHashSet();
		valueSetHierarchy.createVSDSource2VSDsMap();
		valueSetHierarchy.initializeCS2vsdURIs_map();
		_logger.debug("\tDone initializing ValueSetHierarchy ...");

		valueSetHierarchyInitialized = true;
		sourceValueSetTree = valueSetHierarchy.getSourceValueSetTree(null, null);
		if (sourceValueSetTree == null) {
			_logger.debug("\t(*) sourceValueSetTree == null??? ...");
		} else {
			TreeItem root = (TreeItem) sourceValueSetTree.get("<Root>");
			sourceValueSetTreeStringBuffer = new StringBuffer();
//KLO 070914
			//new ValueSetCacheUtils().printTree(sourceValueSetTreeStringBuffer, root, Constants.STANDARD_VIEW, Boolean.TRUE);
            //SimpleTreeUtils stu = new SimpleTreeUtils();
            SimpleTreeUtils stu = new SimpleTreeUtils(_vocabularyNameSet);
            sourceValueSetTreeStringBuffer = stu.getValueSetTreeStringBuffer(sourceValueSetTree);
            //sourceValueSetCheckboxid2NodeIdMap = stu.getCheckboxid2NodeIdMap();
	    }

		terminologyValueSetTree = valueSetHierarchy.getCodingSchemeValueSetTree(null, null);
		TreeItem root = (TreeItem) terminologyValueSetTree.get("<Root>");
		terminologyValueSetTreeStringBuffer = new StringBuffer();
        //SimpleTreeUtils stu_2 = new SimpleTreeUtils();
        SimpleTreeUtils stu_2 = new SimpleTreeUtils(_vocabularyNameSet);

		terminologyValueSetTreeStringBuffer = stu_2.getValueSetTreeStringBuffer(terminologyValueSetTree);
		//terminologyValueSetCheckboxid2NodeIdMap = stu_2.getCheckboxid2NodeIdMap();

		createSourceValueSetTreeKey2TreeItemMap();
		setTerminologyValueSetDescriptionHashMap();

		_rootValueSets = valueSetHierarchy.getRootValueSets();

		System.out.println("initializeValueSetHierarchy run time (ms): " + (System.currentTimeMillis() - ms));
	}

//////////////////////////////////////////////////////////
    public static TreeItem getSourceValueSetTreeItem(String node_id) {
		String vsd_name = null;
		TreeItem ti = null;
		ValueSetDefinition vsd = findValueSetDefinitionByURI(node_id);
		if (vsd != null) {
			vsd_name = vsd.getValueSetDefinitionName();
			ti = (TreeItem) _sourceValueSetTreeKey2TreeItemMap.get(node_id + "$" + vsd_name);
		} else {
			Entity entity = getConceptByCode(Constants.TERMINOLOGY_VALUE_SET_NAME, null, node_id);
			if (entity == null) return null;
			vsd_name = entity.getEntityDescription().getContent();
			ti = (TreeItem) _sourceValueSetTreeKey2TreeItemMap.get(node_id + "$" + vsd_name);
		}
		return ti;
	}

    public static HashMap getSourceValueSetTreeKey2TreeItemMap() {
		if (_sourceValueSetTreeKey2TreeItemMap == null) {
			initializeValueSetHierarchy();
	    }
		return _sourceValueSetTreeKey2TreeItemMap;
	}

    private static void createSourceValueSetTreeKey2TreeItemMap() {
        if (_sourceValueSetTreeKey2TreeItemMap != null) {
            return;
        }
        HashMap hmap = getSourceValueSetTree();
        TreeItem ti = (TreeItem) hmap.get("<Root>");
        _sourceValueSetTreeKey2TreeItemMap = new HashMap();
        createSourceValueSetTreeKey2TreeItemMap(ti);
    }

    private static void createSourceValueSetTreeKey2TreeItemMap(TreeItem ti) {
        String key = ti._code + "$" + ti._text;
        _sourceValueSetTreeKey2TreeItemMap.put(key, ti);

		for (String association : ti._assocToChildMap.keySet()) {
			 List<TreeItem> children = ti._assocToChildMap.get(association);
			 for (TreeItem childItem : children) {
				 createSourceValueSetTreeKey2TreeItemMap(childItem);
			 }
		}
    }

    public static HashMap getSourceValueSetTree(String node_id) {
		HashMap hmap = new HashMap();
		TreeItem super_root = new TreeItem("<Root>", "Root node");
		TreeItem root_node = getSourceValueSetTreeItem(node_id);
		super_root.addChild("inverse_is_a", root_node);
		hmap.put("<Root>", super_root);
        return hmap;
	}

    public static HashMap getCodingSchemeValueSetTree(String vsd_uri) {
		if (terminologyValueSetTree == null) {
			initializeValueSetHierarchy();
		}
		return terminologyValueSetTree;
	}

//////////////////////////////////////////////////////////
    public static HashMap getSourceValueSetTree() {
		if (sourceValueSetTree == null) {
			initializeValueSetHierarchy();
		}
		return sourceValueSetTree;
	}

    public static HashMap getCodingSchemeValueSetTree() {
		if (terminologyValueSetTree == null) {
			initializeValueSetHierarchy();
		}
		return terminologyValueSetTree;
	}

    public static String getProductionVersion(String scheme) {
		if (scheme == null) return null;
		String formalName = getFormalName(scheme);
		Object value = _productionVersionHashMap.get(formalName);
		if (value == null) return null;
		return (String) value;
	}

    public static String getMetadataValue(String scheme, String propertyName) {
		//032014
		String formalName = getFormalName(scheme);
		Vector v = getMetadataValues(formalName, propertyName);
        //Vector v = getMetadataValues(scheme, propertyName);
        if (v == null || v.size() == 0)
            return null;
        return (String) v.elementAt(0);
    }

    public static Vector getMetadataValues(String scheme, String propertyName) {
        if (_formalName2MetadataHashMap == null) {
            setCodingSchemeMap();
        }
		String formalName = getFormalName(scheme);
        if (!_formalName2MetadataHashMap.containsKey(formalName)) {
            return null;
        }
        Vector metadata = (Vector) _formalName2MetadataHashMap.get(formalName);
        if (metadata == null || metadata.size() == 0) {
            return null;
        }
        Vector v = new MetadataUtils().getMetadataValues(metadata, propertyName);
        return v;
    }

    public static String getMetadataValue(String scheme, String version, String propertyName) {
        Vector v = null;
        if (version != null && ! version.equalsIgnoreCase("null"))
            v = getMetadataValues(scheme, version, propertyName);
        else v = getMetadataValues(scheme, propertyName);
        if (v == null || v.size() == 0)
            return null;
        return (String) v.elementAt(0);
    }

    public static Vector getMetadataValues(String scheme, String version, String propertyName) {
        if (_formalName2MetadataHashMap == null) {
            setCodingSchemeMap();
        }
        String formalName = getFormalName(scheme);
        if (!_formalNameVersion2MetadataHashMap.containsKey(formalName + "$" + version)) {
            return null;
        }
        Vector metadata = (Vector) _formalNameVersion2MetadataHashMap.get(formalName + "$" + version);
        if (metadata == null || metadata.size() == 0) {
            return null;
        }
        return new MetadataUtils().getMetadataValues(metadata, propertyName);
    }

    public static boolean isCodingSchemeLoaded(String scheme, String version) {
		if (_formalNameVersion2MetadataHashMap == null) {
			setCodingSchemeMap();
		}
		String formalName = getFormalName(scheme);
        boolean isLoaded = _formalNameVersion2MetadataHashMap.containsKey(formalName + "$" + version);
        return isLoaded;
    }

    public static String getLocalName(String key) {
        if (_formalName2LocalNameHashMap == null) {
            setCodingSchemeMap();
        }
        return (String) _formalName2LocalNameHashMap.get(key);
    }

    public static Vector getLocalNames(String key) {
        if (_formalName2LocalNamesHashMap == null) {
            setCodingSchemeMap();
        }
        return (Vector) _formalName2LocalNamesHashMap.get(key);
    }

    public static String getFormalName(String key) {
        if (key == null) {
            return null;
        }
        if (_localName2FormalNameHashMap == null) {
            setCodingSchemeMap();
        }
        if (!_localName2FormalNameHashMap.containsKey(key))
            return null;

        String value = (String) _localName2FormalNameHashMap.get(key);
        return value;
    }

    public static Vector<String> getSupportedAssociationNames(String key) {
        if (_csnv2codingSchemeNameMap == null) {
            setCodingSchemeMap();
            return getSupportedAssociationNames(key);
        }
        String codingSchemeName = (String) _csnv2codingSchemeNameMap.get(key);
        if (codingSchemeName == null)
            return null;
        String version = (String) _csnv2VersionMap.get(key);
        if (version == null)
            return null;
        return getSupportedAssociationNames(codingSchemeName, version);
    }

    public static Vector<String> getSupportedAssociationNames(String codingSchemeName, String version) {
		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
		return new CodingSchemeDataUtils(lbSvc).getSupportedAssociationNames(codingSchemeName, version);
    }

    public static Vector<String> getPropertyNameListData(String key) {
        if (_csnv2codingSchemeNameMap == null) {
            setCodingSchemeMap();
        }

        String codingSchemeName = (String) _csnv2codingSchemeNameMap.get(key);
        if (codingSchemeName == null) {
            return null;
        }
        String version = (String) _csnv2VersionMap.get(key);
        if (version == null) {
            return null;
        }
        return getPropertyNameListData(codingSchemeName, version);
    }

    public static Vector<String> getPropertyNameListData(
        String codingSchemeName, String version) {
        CodingSchemeVersionOrTag vt = new CodingSchemeVersionOrTag();
        if (version != null) {
            vt.setVersion(version);
        }
        CodingScheme scheme = null;
        try {
            LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();

            scheme = lbSvc.resolveCodingScheme(codingSchemeName, vt);
            if (scheme == null)
                return null;
            Vector<String> propertyNameListData = new Vector<String>();
            SupportedProperty[] properties =
                scheme.getMappings().getSupportedProperty();
            for (int i = 0; i < properties.length; i++) {
                SupportedProperty property = properties[i];
                propertyNameListData.add(property.getLocalId());
            }
            return propertyNameListData;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static Vector<String> getRepresentationalFormListData(String key) {
        String codingSchemeName = (String) _csnv2codingSchemeNameMap.get(key);
        if (codingSchemeName == null)
            return null;
        String version = (String) _csnv2VersionMap.get(key);
        if (version == null)
            return null;
        return getRepresentationalFormListData(codingSchemeName, version);
    }

/////////////////////////////////////////////////////////////////////////////////////////////////KLO
    public static Vector<String> getRepresentationalFormListData(
        String codingSchemeName, String version) {
		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
		return new CodingSchemeDataUtils(lbSvc).getRepresentationalFormListData(codingSchemeName, version);
    }

    public static Vector<String> getPropertyQualifierListData(String key) {
        String codingSchemeName = (String) _csnv2codingSchemeNameMap.get(key);
        if (codingSchemeName == null)
            return null;
        String version = (String) _csnv2VersionMap.get(key);
        if (version == null)
            return null;
        return getPropertyQualifierListData(codingSchemeName, version);
    }

    public static Vector<String> getPropertyQualifierListData(
        String codingSchemeName, String version) {
		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
		return new CodingSchemeDataUtils(lbSvc).getPropertyQualifierListData(codingSchemeName, version);
    }

    public static Vector<String> getSourceListData(String key) {
        if (_csnv2codingSchemeNameMap == null) {
            setCodingSchemeMap();
            return getSourceListData(key);
        }
        String codingSchemeName = (String) _csnv2codingSchemeNameMap.get(key);
        if (codingSchemeName == null)
            return null;
        String version = (String) _csnv2VersionMap.get(key);
        if (version == null)
            return null;
        return getSourceListData(codingSchemeName, version);
    }

    public static Vector<String> getSourceListData(String codingSchemeName, String version) {
		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
		return new CodingSchemeDataUtils(lbSvc).getSourceListData(codingSchemeName, version);
    }

    public static String int2String(Integer int_obj) {
		return gov.nih.nci.evs.browser.utils.StringUtils.int2String(int_obj);
    }

    // ==================================================================================================================================
    public static Entity getConceptByCode(String codingSchemeName, String vers, String code) {
        LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
        return new ConceptDetails(lbSvc).getConceptByCode(codingSchemeName, vers, code);
    }

    public static String getNamespaceByCode(String codingSchemeName, String vers, String code) {
        LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
        return new ConceptDetails(lbSvc).getNamespaceByCode(codingSchemeName, vers, code);
    }

    public static Entity getConceptByCode(String codingSchemeName,
        String vers, String ltag, String code) {
        LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
        return new SearchUtils(lbSvc).matchConceptByCode(codingSchemeName, vers, code,
            null, "LuceneQuery");
            //null, "exactMatch");
    }

    public static NameAndValueList createNameAndValueList(String[] names, String[] values) {
		return ConceptDetails.createNameAndValueList(names, values);
    }

    public ResolvedConceptReferenceList getNext(
        ResolvedConceptReferencesIterator iterator) {
        return iterator.getNext();
    }

    public Vector getAssociationSourceCodes(String scheme, String version,
        String code, String assocName) {
		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
		return new ConceptDetails(lbSvc).getAssociationSourceCodes(scheme, version, code, assocName);
    }

    public static ConceptReferenceList createConceptReferenceList(
        String[] codes, String codingSchemeName) {
        return new ConceptDetails().createConceptReferenceList(
            codes, codingSchemeName, null);
    }

    public static ConceptReferenceList createConceptReferenceList(Vector codes,
        String codingSchemeName) {
	    String[] array = (String[]) codes.toArray(new String[codes.size()]);
	    return createConceptReferenceList(array, codingSchemeName);
    }

    public Vector getSuperconceptCodes(String scheme, String version, String code) {
		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
		return new TreeUtils(lbSvc).getSuperconceptCodes(scheme, version, code);
    }

    public Vector getHierarchyAssociationId(String scheme, String version) {
		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
		return new TreeUtils(lbSvc).getHierarchyAssociationId(scheme, version);
    }

    public static String getVersion() {
        return getVersion(CODING_SCHEME_NAME);
    }

    public static String getVersion(String scheme) {
        String info = getReleaseDate(scheme);

        String version =
            getMetadataValue(scheme, "term_browser_version");

        if (version == null)
            version = getVocabularyVersionByTag(scheme, null);

        if (version != null && version.length() > 0)
            info += " (" + version + ")";
        return info;
    }

    public static String getReleaseDate() {
        return getReleaseDate(CODING_SCHEME_NAME);
    }

    public static String getReleaseDate(String coding_scheme_name) {
        try {
            LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
            DateFormat formatter = new SimpleDateFormat("MMMM d, yyyy");
            HistoryService hs = null;
            try {
                hs = lbSvc.getHistoryService(coding_scheme_name);
            } catch (Exception ex) {
                _logger.error("WARNING: HistoryService is not available for "
                    + coding_scheme_name);
            }
            if (hs != null) {
                SystemRelease release = hs.getLatestBaseline();
                java.util.Date date = release.getReleaseDate();
                return formatter.format(date);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getVocabularyVersionByTag(String codingSchemeName, String ltag) {
		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
		CodingSchemeDataUtils codingSchemeDataUtils = new CodingSchemeDataUtils(lbSvc);
		return codingSchemeDataUtils.getVocabularyVersionByTag(codingSchemeName, ltag);
    }


    public static Vector<String> getVersionListData(String codingSchemeName) {
		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
		CodingSchemeDataUtils codingSchemeDataUtils = new CodingSchemeDataUtils(lbSvc);
		return codingSchemeDataUtils.getVersionListData(codingSchemeName);
    }

    public static String getFileName(String pathname) {
        File file = new File(pathname);
        String filename = file.getName();
        return filename;
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static LocalNameList vector2LocalNameList(Vector<String> v) {
		return ConceptDetails.vector2LocalNameList(v);
    }

    public static NameAndValueList createNameAndValueList(Vector names, Vector values) {
		return ConceptDetails.createNameAndValueList(names, values);
    }

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static CodingScheme getCodingScheme(String codingScheme,
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

    public static Vector<SupportedProperty> getSupportedProperties(
        CodingScheme cs) {
		return new ConceptDetails().getSupportedProperties(cs);
    }

    public static Vector<String> getSupportedPropertyNames(CodingScheme cs) {
		return new ConceptDetails().getSupportedPropertyNames(cs);
    }

    public static Vector<String> getSupportedPropertyNames(String codingScheme, String version) {
		return new ConceptDetails().getSupportedPropertyNames(codingScheme, version);
    }

    public static Vector getPropertyNamesByType(Entity concept, String property_type) {
	    return new ConceptDetails().getPropertyNamesByType(concept, property_type);
    }

    public static String getPropertyQualfierValues(org.LexGrid.commonTypes.Property p) {
		return new ConceptDetails().getPropertyQualfierValues(p);
    }

    public static Vector getPropertyValues(Entity concept, String property_type, String property_name) {
		return new ConceptDetails().getPropertyValues(concept, property_type, property_name);
    }

    // =====================================================================================

    public List getSupportedRoleNames(LexBIGService lbSvc, String scheme, String version) {
		return new CodingSchemeDataUtils(lbSvc).getSupportedRoleNames(scheme, version);
    }

    public String getPreferredName(Entity c) {
		return new ConceptDetails().getPreferredName(c);
    }

    public LexBIGServiceConvenienceMethods createLexBIGServiceConvenienceMethods(
        LexBIGService lbSvc) {
        LexBIGServiceConvenienceMethods lbscm = null;
        try {
            lbscm =
                (LexBIGServiceConvenienceMethods) lbSvc
                    .getGenericExtension("LexBIGServiceConvenienceMethods");
            lbscm.setLexBIGService(lbSvc);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return lbscm;
    }

    private String replaceAssociationNameByRela(AssociatedConcept ac,
        String associationName) {
        if (ac.getAssociationQualifiers() == null)
            return associationName;
        if (ac.getAssociationQualifiers().getNameAndValue() == null)
            return associationName;

        for (NameAndValue qual : ac.getAssociationQualifiers()
            .getNameAndValue()) {
            String qualifier_name = qual.getName();
            String qualifier_value = qual.getContent();
            if (qualifier_name.compareToIgnoreCase("rela") == 0) {
                return qualifier_value; // replace associationName by Rela value
            }
        }
        return associationName;
    }

    public HashMap getRelationshipHashMap(String scheme, String version, String code) {
        boolean isMapping = isMapping(scheme, version);

        NameAndValueList navl = null;
        if (isMapping) navl = getMappingAssociationNames(scheme, version);

        LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
        LexBIGServiceConvenienceMethods lbscm =
            createLexBIGServiceConvenienceMethods(lbSvc);

        CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
        if (version != null)
            csvt.setVersion(version);

        ConceptReference cr = ConvenienceMethods.createConceptReference(code, scheme);
        String entityCodeNamespace = null;

		Entity concept = getConceptByCode(scheme, version, null, code);
		if (concept == null) {
			if (!isMapping) {
				return null;
		    }
		} else {
			entityCodeNamespace = concept.getEntityCodeNamespace();
		}

        if (entityCodeNamespace != null) {
			cr.setCodingSchemeName(entityCodeNamespace);
		}



        // Perform the query ...
        ResolvedConceptReferenceList matches = null;
        List list = getSupportedRoleNames(lbSvc, scheme, version);

        ArrayList roleList = new ArrayList();
        ArrayList associationList = new ArrayList();

        ArrayList inverse_roleList = new ArrayList();
        ArrayList inverse_associationList = new ArrayList();

        ArrayList superconceptList = new ArrayList();
        ArrayList subconceptList = new ArrayList();

        HashMap map = new HashMap();

        // Exclude hierarchical relationships:
        String[] associationsToNavigate =
            createTreeUtils().getAssociationsToNavigate(scheme, version);
        Vector w = new Vector();
        if (associationsToNavigate != null) {
            for (int k = 0; k < associationsToNavigate.length; k++) {
                w.add(associationsToNavigate[k]);
            }
        }

         // superconcepts:
		if (!isMapping) {
				HashMap hmap_super = createTreeUtils().getSuperconcepts(scheme, version, code);
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
				//Collections.sort(superconceptList);
				SortUtils.quickSort(superconceptList);

		}
        map.put(Constants.TYPE_SUPERCONCEPT, superconceptList);

        /*
         * HashMap hmap_sub = TreeUtils.getSubconcepts(scheme, version, code);
         * if (hmap_sub != null) { TreeItem ti = (TreeItem) hmap_sub.get(code);
         * if (ti != null) { for (String association :
         * ti.assocToChildMap.keySet()) { List<TreeItem> children =
         * ti.assocToChildMap.get(association); for (TreeItem childItem :
         * children) { subconceptList.add(childItem.text + "|" +
         * childItem.code); } } } }
         */

        // subconcepts:
		if (!isMapping) {
				subconceptList =
					createTreeUtils().getSubconceptNamesAndCodes(scheme, version, code);
				//KLO
				//Collections.sort(subconceptList);
				SortUtils.quickSort(subconceptList);

		}
        map.put(Constants.TYPE_SUBCONCEPT, subconceptList);

        // associations:
        try {
            CodedNodeGraph cng = lbSvc.getNodeGraph(scheme, csvt, null);

            if (isMapping) {
                 if (navl != null) {
					 cng = cng.restrictToAssociations(navl, null);
 				 }
			}

            matches = null;
            try {
                matches =
                    cng.resolveAsList(cr,
                            true, false, 0, 1, null, null, null, null, -1,
                            false);

            } catch (Exception e) {
                _logger
                    .error("ERROR: DataUtils getRelationshipHashMap cng.resolveAsList throws exceptions."
                        + code);
            }

            if (matches != null
                && matches.getResolvedConceptReferenceCount() > 0) {
                Enumeration<? extends ResolvedConceptReference> refEnum =
                    matches.enumerateResolvedConceptReference();

                while (refEnum.hasMoreElements()) {
                    ResolvedConceptReference ref = (ResolvedConceptReference) refEnum.nextElement();
                    AssociationList sourceof = ref.getSourceOf();
                    if (sourceof != null) {
                        Association[] associations = sourceof.getAssociation();
                        if (associations != null) {
                            for (int i = 0; i < associations.length; i++) {
                                Association assoc = associations[i];
                                String associationName = null;

                                try {
									associationName =
										lbscm
											.getAssociationNameFromAssociationCode(
												scheme, csvt, assoc
													.getAssociationName());
								} catch (Exception ex) {
									associationName = assoc.getAssociationName();
								}

								//associationName = assoc.getDirectionalName();

                                boolean isRole = false;
                                if (list.contains(associationName)) {
                                    isRole = true;
                                }

                                AssociatedConcept[] acl =
                                    assoc.getAssociatedConcepts()
                                        .getAssociatedConcept();

                                for (int j = 0; j < acl.length; j++) {
                                    AssociatedConcept ac = acl[j];

                                    String ac_csn = ac.getCodingSchemeName();

                                    // [#26283] Remove self-referential
                                    // relationships.
                                    boolean include = true;
                                    if (ac.getConceptCode().compareTo(code) == 0)
                                        include = false;

                                    if (include) {

                                        EntityDescription ed =
                                            ac.getEntityDescription();

                                        String name = "No Description";
                                        if (ed != null)
                                            name = ed.getContent();
                                        String pt = name;

                                        if (associationName
                                            .compareToIgnoreCase("equivalentClass") != 0
                                            && ac.getConceptCode().indexOf("@") == -1) {
                                            if (!w.contains(associationName)) {
                                                // String s = associationName +
                                                // "|" + pt + "|" +
                                                // ac.getConceptCode();
                                                String relaValue =
                                                    replaceAssociationNameByRela(
                                                        ac, associationName);

                                                String s =
                                                    relaValue + "|" + pt + "|"
                                                        + ac.getConceptCode() + "|"
                                                        + ac.getCodingSchemeName();

                                                StringBuffer sb = new StringBuffer();
                                                if (isMapping) {
													if (ac.getAssociationQualifiers() != null) {
														//String qualifiers = "";
														StringBuffer buf = new StringBuffer();
														for (NameAndValue qual : ac
																.getAssociationQualifiers()
																.getNameAndValue()) {
															String qualifier_name = qual.getName();
															String qualifier_value = qual.getContent();
															//qualifiers = qualifiers + (qualifier_name + ":" + qualifier_value) + "$";
															buf.append((qualifier_name + ":" + qualifier_value) + "$");

														}
														String qualifiers = buf.toString();

														//s = s + "|" + qualifiers;
														sb.append("|" + qualifiers);
													}
													//s = s + "|" + ac.getCodeNamespace();
													sb.append("|" + ac.getCodeNamespace());
												}
												s = s + sb.toString();


                                                if (isRole) {
                                                    // if
                                                    // (associationName.compareToIgnoreCase("hasSubtype")
                                                    // != 0) {
                                                    // _logger.debug("Adding role: "
                                                    // +
                                                    // s);
                                                    roleList.add(s);
                                                    // }
                                                } else {
                                                    // _logger.debug("Adding association: "
                                                    // + s);
                                                    associationList.add(s);

                                                }

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            cng = lbSvc.getNodeGraph(scheme, csvt, null);

            if (isMapping) {
                 if (navl != null) {
					 cng = cng.restrictToAssociations(navl, null);
 				 }
			}

            matches = null;
            try {
                matches =
                    cng.resolveAsList(cr,
                            false, true, 0, 1, null, null, null, null, -1,
                            false);
            } catch (Exception e) {
                _logger
                    .error("ERROR: DataUtils getRelationshipHashMap cng.resolveAsList throws exceptions."
                        + code);
            }

            if (matches != null
                && matches.getResolvedConceptReferenceCount() > 0) {
                Enumeration<? extends ResolvedConceptReference> refEnum =
                    matches.enumerateResolvedConceptReference();

                while (refEnum.hasMoreElements()) {
                    ResolvedConceptReference ref = refEnum.nextElement();

                    // inverse roles and associations
                    AssociationList targetof = ref.getTargetOf();
                    if (targetof != null) {
                        Association[] inv_associations =
                            targetof.getAssociation();
                        if (inv_associations != null) {
                            for (int i = 0; i < inv_associations.length; i++) {
                                Association assoc = inv_associations[i];
                                String associationName = null;
                                try {
                                    associationName = lbscm
                                        .getAssociationNameFromAssociationCode(
                                            scheme, csvt, assoc
                                                .getAssociationName());
							    } catch (Exception ex) {
									associationName = assoc.getAssociationName();
								}
                                boolean isRole = false;
                                if (list.contains(associationName)) {
                                    isRole = true;
                                }
                                AssociatedConcept[] acl =
                                    assoc.getAssociatedConcepts()
                                        .getAssociatedConcept();
                                for (int j = 0; j < acl.length; j++) {
                                    AssociatedConcept ac = acl[j];

                                    // [#26283] Remove self-referential
                                    // relationships.
                                    boolean include = true;
                                    if (ac.getConceptCode().compareTo(code) == 0)
                                        include = false;

                                    if (include) {

                                        EntityDescription ed =
                                            ac.getEntityDescription();

                                        String name = "No Description";
                                        if (ed != null)
                                            name = ed.getContent();

                                        String pt = name;

                                        // [#24749] inverse association names
                                        // are empty for domain and range
                                        if (associationName.compareTo("domain") == 0
                                            || associationName
                                                .compareTo("range") == 0) {

											try {
												pt =
													lbscm
														.getAssociationNameFromAssociationCode(
															scheme, csvt, ac
																.getConceptCode());
											} catch (Exception ex) {
												pt = ac.getConceptCode();
											}
                                        }

                                        // if
                                        // (associationName.compareToIgnoreCase("equivalentClass")
                                        // != 0) {
                                        if (associationName
                                            .compareToIgnoreCase("equivalentClass") != 0
                                            && ac.getConceptCode().indexOf("@") == -1) {

                                            if (!w.contains(associationName)) {
                                                // String s = associationName +
                                                // "|" + pt + "|" +
                                                // ac.getConceptCode();
                                                String relaValue =
                                                    replaceAssociationNameByRela(
                                                        ac, associationName);

                                                String s =
                                                    relaValue + "|" + pt + "|"
                                                         + ac.getConceptCode() + "|"
                                                         + ac.getCodingSchemeName();

                                                StringBuffer sb = new StringBuffer();


                                                if (isMapping) {
													if (ac.getAssociationQualifiers() != null) {
														//String qualifiers = "";
														StringBuffer buf = new StringBuffer();
														for (NameAndValue qual : ac
																.getAssociationQualifiers()
																.getNameAndValue()) {
															String qualifier_name = qual.getName();
															String qualifier_value = qual.getContent();
															//qualifiers = qualifiers + (qualifier_name + ":" + qualifier_value) + "$";
															buf.append((qualifier_name + ":" + qualifier_value) + "$");
														}
														String qualifiers = buf.toString();
														//s = s + "|" + qualifiers;
														sb.append("|" + qualifiers);
													}
													//s = s + "|" + ac.getCodeNamespace();
													sb.append("|" + ac.getCodeNamespace());
													s = s + sb.toString();
												}

                                                if (isRole) {
                                                    inverse_roleList.add(s);
                                                } else {
                                                    inverse_associationList
                                                        .add(s);
                                                }
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (roleList.size() > 0) {
                //Collections.sort(roleList);
                SortUtils.quickSort(roleList);

            }

            if (associationList.size() > 0) {
                //Collections.sort(associationList);
                SortUtils.quickSort(associationList);
            }

            map.put(Constants.TYPE_ROLE, roleList);
            map.put(Constants.TYPE_ASSOCIATION, associationList);

            if (inverse_roleList.size() > 0) {
                //Collections.sort(inverse_roleList);
                SortUtils.quickSort(inverse_roleList);
            }

            if (inverse_associationList.size() > 0) {
                //Collections.sort(inverse_associationList);
                SortUtils.quickSort(inverse_associationList);
            }

            map.put(Constants.TYPE_INVERSE_ROLE, inverse_roleList);
            map.put(Constants.TYPE_INVERSE_ASSOCIATION, inverse_associationList);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    public Vector getAssociationSources(String scheme, String version, String code, String assocName) {
        CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
        if (version != null)
            csvt.setVersion(version);
        ResolvedConceptReferenceList matches = null;
        Vector v = new Vector();
        try {
            LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
            CodedNodeGraph cng = lbSvc.getNodeGraph(scheme, csvt, null);
            NameAndValueList nameAndValueList =
                createNameAndValueList(new String[] { assocName }, null);

            NameAndValueList nameAndValueList_qualifier = null;
            cng =
                cng.restrictToAssociations(nameAndValueList,
                    nameAndValueList_qualifier);
            ConceptReference graphFocus =
                ConvenienceMethods.createConceptReference(code, scheme);

            boolean resolveForward = false;
            boolean resolveBackward = true;

            int resolveAssociationDepth = 1;
            int maxToReturn = -1;

            ResolvedConceptReferencesIterator iterator =
                codedNodeGraph2CodedNodeSetIterator(cng, graphFocus,
                    resolveForward, resolveBackward, resolveAssociationDepth,
                    maxToReturn);

            v = resolveIterator(iterator, maxToReturn, code);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return v;
    }

    public Vector getAssociationTargets(String scheme, String version, String code, String[] assocNames) {
        CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
        if (version != null)
            csvt.setVersion(version);
        ResolvedConceptReferenceList matches = null;
        Vector v = new Vector();
        try {
            LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
            CodedNodeGraph cng = lbSvc.getNodeGraph(scheme, csvt, null);
            NameAndValueList nameAndValueList =
                createNameAndValueList(assocNames, null);

            NameAndValueList nameAndValueList_qualifier = null;
            cng =
                cng.restrictToAssociations(nameAndValueList,
                    nameAndValueList_qualifier);
            ConceptReference graphFocus =
                ConvenienceMethods.createConceptReference(code, scheme);

            boolean resolveForward = true;
            boolean resolveBackward = false;

            int resolveAssociationDepth = 1;
            int maxToReturn = -1;

            ResolvedConceptReferencesIterator iterator =
                codedNodeGraph2CodedNodeSetIterator(cng, graphFocus,
                    resolveForward, resolveBackward, resolveAssociationDepth,
                    maxToReturn);

            v = resolveIterator(iterator, maxToReturn, code);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return v;
    }


    public Vector getAssociationTargets(String scheme, String version, String code, String assocName) {
        CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
        if (version != null)
            csvt.setVersion(version);
        ResolvedConceptReferenceList matches = null;
        Vector v = new Vector();
        try {
            LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
            CodedNodeGraph cng = lbSvc.getNodeGraph(scheme, csvt, null);
            NameAndValueList nameAndValueList =
                createNameAndValueList(new String[] { assocName }, null);

            NameAndValueList nameAndValueList_qualifier = null;
            cng =
                cng.restrictToAssociations(nameAndValueList,
                    nameAndValueList_qualifier);
            ConceptReference graphFocus =
                ConvenienceMethods.createConceptReference(code, scheme);

            boolean resolveForward = true;
            boolean resolveBackward = false;

            int resolveAssociationDepth = 1;
            int maxToReturn = -1;

            ResolvedConceptReferencesIterator iterator =
                codedNodeGraph2CodedNodeSetIterator(cng, graphFocus,
                    resolveForward, resolveBackward, resolveAssociationDepth,
                    maxToReturn);

            v = resolveIterator(iterator, maxToReturn, code);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return v;
    }

    public ResolvedConceptReferencesIterator codedNodeGraph2CodedNodeSetIterator(
        CodedNodeGraph cng, ConceptReference graphFocus,
        boolean resolveForward, boolean resolveBackward,
        int resolveAssociationDepth, int maxToReturn) {
		return new ConceptDetails().codedNodeGraph2CodedNodeSetIterator(
			cng, graphFocus,
			resolveForward, resolveBackward,
			resolveAssociationDepth, maxToReturn);
    }

    public Vector resolveIterator(ResolvedConceptReferencesIterator iterator,
        int maxToReturn) {
        return resolveIterator(iterator, maxToReturn, null);
    }

    public Vector resolveIterator(ResolvedConceptReferencesIterator iterator,
        int maxToReturn, String code) {
        Vector v = new Vector();
        if (iterator == null) {
            _logger.warn("No match.");
            return v;
        }
        try {
            int iteration = 0;
            while (iterator.hasNext()) {
                iteration++;
                iterator = iterator.scroll(maxToReturn);
                ResolvedConceptReferenceList rcrl = iterator.getNext();
                ResolvedConceptReference[] rcra =
                    rcrl.getResolvedConceptReference();
                for (int i = 0; i < rcra.length; i++) {
                    ResolvedConceptReference rcr = rcra[i];
                    org.LexGrid.concepts.Entity ce = rcr.getReferencedEntry();
                    if (code == null) {
                        v.add(ce);
                    } else {
                        if (ce.getEntityCode().compareTo(code) != 0)
                            v.add(ce);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }


    public static Vector<String> parseData(String line) {
		return gov.nih.nci.evs.browser.utils.StringUtils.parseData(line, "|");
    }

    public static Vector<String> parseData(String line, String tab) {
		return gov.nih.nci.evs.browser.utils.StringUtils.parseData(line, tab);
    }


    public static String getHyperlink(String url, String codingScheme,
        String code) {
        codingScheme = codingScheme.replace(" ", "%20");
        String link =
            url + "/ConceptReport.jsp?dictionary=" + codingScheme + "&code="
                + code;
        return link;
    }

    public static Vector getSynonyms(String scheme, String version, String tag, String code) {
        Entity concept = getConceptByCode(scheme, version, tag, code);
        return getSynonyms(scheme, concept);
    }

    //public static Vector getSynonyms(Entity concept) {
	//	return new ConceptDetails().getSynonyms(concept);
    //}

    public static Vector getSynonyms(String scheme, Entity concept) {
        if (concept == null)
            return null;
        Vector v = new Vector();
        Presentation[] properties = concept.getPresentation();
        int n = 0;
        boolean inclusion = true;
        for (int i = 0; i < properties.length; i++) {
            Presentation p = properties[i];
            // for NCI Thesaurus or Pre-NCI Thesaurus, show FULL_SYNs only
            if (scheme != null && scheme.indexOf(CODING_SCHEME_NAME) != -1) {
                inclusion = false;
                if (p.getPropertyName().compareTo("FULL_SYN") == 0) {
                    inclusion = true;
                }
            }
            if (inclusion) {
                String term_name = p.getValue().getContent();
                String term_type = "null";
                String term_source = "null";
                String term_source_code = "null";

                PropertyQualifier[] qualifiers = p.getPropertyQualifier();
                if (qualifiers != null) {
                    for (int j = 0; j < qualifiers.length; j++) {
                        PropertyQualifier q = qualifiers[j];
                        String qualifier_name = q.getPropertyQualifierName();
                        String qualifier_value = q.getValue().getContent();
                        if (qualifier_name.compareTo("source-code") == 0) {
                            term_source_code = qualifier_value;
                            break;
                        }
                    }
                }
                term_type = p.getRepresentationalForm();
                Source[] sources = p.getSource();
                if (sources != null && sources.length > 0) {
                    Source src = sources[0];
                    term_source = src.getContent();
                }
                v.add(term_name + "|" + term_type + "|" + term_source + "|"
                    + term_source_code);
            }
        }
        SortUtils.quickSort(v);
        return v;
    }



    // /////////////////////////////////////////////////////////////////////////////

    public static CodingScheme resolveCodingScheme(LexBIGService lbSvc,
        String formalname, CodingSchemeVersionOrTag versionOrTag) {
		try {
			return new CodingSchemeDataUtils(lbSvc).getCodingScheme(formalname, versionOrTag);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
    }

    public static HashMap getNamespaceId2CodingSchemeFormalNameMapping() {
        if (_namespace2CodingScheme != null) {
            return _namespace2CodingScheme;
        }
        HashMap hmap = new HashMap();
        LexBIGService lbSvc = new RemoteServerUtil().createLexBIGService();
        if (lbSvc == null) {
            _logger
                .error("ERROR: setCodingSchemeMap RemoteServerUtil().createLexBIGService() returns null.");
            return null;
        }
        CodingSchemeRenderingList csrl = null;
        CodingSchemeRendering[] csrs = null;
        try {
            csrl = lbSvc.getSupportedCodingSchemes();
            csrs = csrl.getCodingSchemeRendering();
        } catch (LBInvocationException ex) {
            _logger.error("lbSvc.getSupportedCodingSchemes() FAILED..."
                + ex.getCause());
            return null;
        }
        for (int i = 0; i < csrs.length; i++) {
            CodingSchemeRendering csr = csrs[i];
            //if (csr != null && csr.getRenderingDetail() != null) {
			if (csr != null) {
                Boolean isActive = null;
                if (csr.getRenderingDetail() == null) {
                    _logger.warn("\tcsr.getRenderingDetail() == null");
                } else if (csr.getRenderingDetail().getVersionStatus() == null) {
                    _logger
                        .warn("\tcsr.getRenderingDetail().getVersionStatus() == null");
                } else {
                    if (csr.getRenderingDetail() != null) {
						isActive =
							csr.getRenderingDetail().getVersionStatus().equals(
								CodingSchemeVersionStatus.ACTIVE);
					}
                }

                // if (isActive != null && isActive.equals(Boolean.TRUE))
                {
                    CodingSchemeSummary css = csr.getCodingSchemeSummary();
                    String formalname = css.getFormalName();
                    java.lang.String version = css.getRepresentsVersion();
                    CodingSchemeVersionOrTag versionOrTag =
                        new CodingSchemeVersionOrTag();
                    if (version != null)
                        versionOrTag.setVersion(version);

                    CodingScheme cs = null;
                    cs = resolveCodingScheme(lbSvc, formalname, versionOrTag);
                    if (cs != null) {
                        Mappings mapping = cs.getMappings();
                        if (mapping != null) {
                            SupportedNamespace[] namespaces =
                                mapping.getSupportedNamespace();
                            if (namespaces != null) {
                                for (int j = 0; j < namespaces.length; j++) {
                                    SupportedNamespace ns = namespaces[j];
                                    if (ns != null) {
                                        java.lang.String ns_name =
                                            ns.getEquivalentCodingScheme();
                                        java.lang.String ns_id =
                                            ns.getContent();
                                        // _logger.debug("\tns_name: " + ns_name
                                        // + " ns_id:" + ns_id);
                                        if (ns_id != null
                                            && ns_id.compareTo("") != 0) {
                                            hmap.put(ns_id, formalname);
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        _logger.warn("??? Unable to resolveCodingScheme "
                            + formalname);
                    }
                }
            }
        }
        return hmap;
    }

    public static String getCodingSchemeName(String key) {
        return key2CodingSchemeName(key);
    }

    public static String getCodingSchemeVersion(String key) {
        return key2CodingSchemeVersion(key);
    }

    public static String key2CodingSchemeName(String key) {
        if (key == null) {
            return null;
        }
        if (_csnv2codingSchemeNameMap == null) {
            _logger.debug("setCodingSchemeMap");
            setCodingSchemeMap();
        }
        if (key.indexOf("%20") != -1) {
            key = key.replaceAll("%20", " ");
        }
        if (_csnv2codingSchemeNameMap == null) {
            _logger.warn("csnv2codingSchemeNameMap == NULL???");
            return key;
        }
        String value = (String) _csnv2codingSchemeNameMap.get(key);
        if (value == null) {
            // _logger.debug("key2CodingSchemeName returns " + key);
            return key;
        }
        return value;
    }

    public static String key2CodingSchemeVersion(String key) {
        if (key == null) {
            return null;
        }
        if (_csnv2VersionMap == null)
            setCodingSchemeMap();
        if (key.indexOf("%20") != -1) {
            key = key.replaceAll("%20", " ");
        }

        if (_csnv2VersionMap == null) {
            _logger.warn("csnv2VersionMap == NULL???");
            return key;
        }

        String value = (String) _csnv2VersionMap.get(key);
        return value;
    }

    public static String replaceAll(String s, String t1, String t2) {
		// AppScan
		if (s == null) return s;
        s = s.replaceAll(t1, t2);
        return s;
    }

    // ////////////////////////////////////////////////////////////////
    public static Vector getStatusByConceptCodes(String scheme, String version,
        String ltag, Vector codes) {
        boolean conceptStatusSupported = false;
        if (_vocabulariesWithConceptStatusHashSet.contains(scheme))
            conceptStatusSupported = true;

        Vector w = new Vector();
        long ms = System.currentTimeMillis();
        for (int i = 0; i < codes.size(); i++) {
            if (conceptStatusSupported) {
                String code = (String) codes.elementAt(i);
                Entity c = getConceptByCode(scheme, version, ltag, code);
                if (c != null) {
                    w.add(c.getStatus());
                } else {
                    _logger
                        .warn("WARNING: getStatusByConceptCode returns null on "
                            + scheme + " code: " + code);
                    w.add(null);
                }
            } else {
                w.add(null);
            }
        }
        _logger.debug("getStatusByConceptCodes Run time (ms): "
            + (System.currentTimeMillis() - ms) + " number of concepts: "
            + codes.size());
        return w;
    }


    public static String getConceptStatus(String scheme, String version, String ltag, String code) {
        boolean conceptStatusSupported = false;
        if (_vocabulariesWithConceptStatusHashSet.contains(scheme))
            conceptStatusSupported = true;
        if (!conceptStatusSupported)
            return null;
        Entity c = getConceptWithProperty(scheme, version, code, Constants.CONCEPT_STATUS);
        String con_status = null;
        if (c != null) {
            Vector status_vec = getConceptPropertyValues(c, Constants.CONCEPT_STATUS);
            if (status_vec == null || status_vec.size() == 0) {
                con_status = c.getStatus();
            } else {
                con_status = gov.nih.nci.evs.browser.utils.StringUtils.convertToCommaSeparatedValue(status_vec);
            }
            return con_status;
        }
        return null;
    }

    public static Vector getConceptStatusByConceptCodes(String scheme,
        String version, String ltag, Vector codes) {
        boolean conceptStatusSupported = false;
        if (_vocabulariesWithConceptStatusHashSet.contains(scheme))
            conceptStatusSupported = true;

        Vector w = new Vector();
        long ms = System.currentTimeMillis();
        for (int i = 0; i < codes.size(); i++) {
            if (conceptStatusSupported) {
                String code = (String) codes.elementAt(i);
                Entity c =
                    getConceptWithProperty(scheme, version, code,
                        "Concept_Status");
                String con_status = null;
                if (c != null) {
                    Vector status_vec =
                        getConceptPropertyValues(c, "Concept_Status");
                    if (status_vec == null || status_vec.size() == 0) {
                        con_status = c.getStatus();
                    } else {
                        con_status =
                            gov.nih.nci.evs.browser.utils.StringUtils.convertToCommaSeparatedValue(status_vec);
                    }
                    w.add(con_status);
                } else {
                    w.add(null);
                }
            } else {
                w.add(null);
            }
        }
        _logger.debug("getConceptStatusByConceptCodes Run time (ms): "
            + (System.currentTimeMillis() - ms) + " number of concepts: "
            + codes.size());
        return w;
    }

    // for HL7 fix
    public static String searchFormalName(String s) {
        if (s == null)
            return null;

        if (_formalName2LocalNameHashMap == null) {
            setCodingSchemeMap();
        }

        if (_formalName2LocalNameHashMap.containsKey(s))
            return s;
        Iterator it = _formalName2LocalNameHashMap.keySet().iterator();
        while (it.hasNext()) {
            String t = (String) it.next();
            String t0 = t;
            t = t.trim();
            if (t.compareTo(s) == 0)
                return t0;
        }
        return s;
    }

    public static String getFormalNameByDisplayName(String s) {
        if (_displayName2FormalNameHashMap == null)
            setCodingSchemeMap();
        return (String) _displayName2FormalNameHashMap.get(s);
    }

    // [#25034] Remove hyperlink from instances on the Relationship tab. (KLO,
    // 121709)
    public static Vector getNonConcept2ConceptAssociation() {
        Vector nonConcept2ConceptAssociations = new Vector();
            nonConcept2ConceptAssociations.add("domain");
            nonConcept2ConceptAssociations.add("range");
            nonConcept2ConceptAssociations.add("instance");
        return nonConcept2ConceptAssociations;
	}

    public static boolean isNonConcept2ConceptAssociation(String associationName) {
        if (_nonConcept2ConceptAssociations == null) {
            _nonConcept2ConceptAssociations = getNonConcept2ConceptAssociation();
        }
        if (associationName == null)
            return false;
        return _nonConcept2ConceptAssociations.contains(associationName);
    }

    public HashMap getPropertyValuesForCodes(String scheme, String version,
        Vector codes, String propertyName) {
		LexBIGService lbSvc = new RemoteServerUtil().createLexBIGService();
		return new ConceptDetails(lbSvc).getPropertyValuesForCodes(scheme, version, codes, propertyName);
    }

    public static Entity getConceptWithProperty(String scheme, String version, String code, String propertyName) {
		LexBIGService lbSvc = new RemoteServerUtil().createLexBIGService();
		return new ConceptDetails(lbSvc).getConceptWithProperty(scheme, version, code, propertyName);
    }

    public static Vector getConceptPropertyValues(Entity c, String propertyName) {
		return new ConceptDetails().getConceptPropertyValues(c, propertyName);
    }

    public static Vector getPresentationProperties(Entity concept) {
		return new ConceptDetails().getPresentationProperties(concept);
    }

    private static Vector getSupportedSources(String codingScheme, String version) {
		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
		return new CodingSchemeDataUtils(lbSvc).getSupportedSources(codingScheme, version);
	}

	public static String getNCImSAB(String formalName) {
		if (_formalName2NCImSABHashMap.containsKey(formalName)) {
			String value = (String) _formalName2NCImSABHashMap.get(formalName);
			return value;
		}
		return null;
	}

	private static HashMap createFormalName2NCImSABHashMap() {
		HashMap hmap = new HashMap();
		Vector sab_vec = getSupportedSources(Constants.NCI_METATHESAURUS, null);
		if (sab_vec != null) {
			for (int i=0; i<sab_vec.size(); i++) {
				String sab = (String) sab_vec.elementAt(i);
				if (_localName2FormalNameHashMap.containsKey(sab)) {
					String value = (String) _localName2FormalNameHashMap.get(sab);
					hmap.put(value, sab);
				}
			}
		}
		return hmap;
	}

    private static void dumpHashMap(HashMap<String, String> hmap) {
		if (hmap == null) return;

		Set<Map.Entry<String, String>> set = hmap.entrySet();
		for (Map.Entry<String, String> me : set) {
		    System.out.print(me.getKey() + ": ");
		    System.out.println(me.getValue());
		}
    }

///////////////////////////////////////////////////////////////////////////////////////////////
//
// Mapping
//
///////////////////////////////////////////////////////////////////////////////////////////////

    public static boolean hasMapping(String codingScheme) {
		Vector v = getMappingCodingSchemes(codingScheme);
		if (v != null && v.size() > 0) return true;
		return false;
	}

	public static Vector getMappingCodingSchemes(String codingScheme) {
        if (_codingSchemeHashSet == null)
            setCodingSchemeMap();

        if (_codingScheme2MappingCodingSchemes == null) {
			_codingScheme2MappingCodingSchemes = new HashMap();
		}
        String formalName = getFormalName(codingScheme);

        if (_codingScheme2MappingCodingSchemes.containsKey(formalName)) {
			return (Vector) _codingScheme2MappingCodingSchemes.get(formalName);
		}
		Vector v = new Vector();
		List ontology_list = getOntologyList();
		LexBIGService lbSvc = new RemoteServerUtil().createLexBIGService();
        for (int i = 0; i < ontology_list.size(); i++) {
			SelectItem item = (SelectItem) ontology_list.get(i);
			String value = (String) item.getValue();
			String label = (String) item.getLabel();

			String scheme = key2CodingSchemeName(value);
			String version = key2CodingSchemeVersion(value);

			if (isMapping(scheme, version)) {
				try {

					CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
					if (version != null)
						csvt.setVersion(version);
					CodingScheme cs = lbSvc.resolveCodingScheme(scheme, csvt);
					Relations[] relations = cs.getRelations();
					for (int j = 0; j < relations.length; j++) {
						Relations relation = relations[j];
						Boolean bool_obj = relation.isIsMapping();
						if (bool_obj != null && bool_obj.equals(Boolean.TRUE)) {

                            if (!isNull(codingScheme)) {
                                String src_cs_name = relation.getSourceCodingScheme();
                                String target_cs_name = relation.getTargetCodingScheme();
                                if (src_cs_name != null && target_cs_name != null) {
									String src_cs_formal_name = getFormalName(src_cs_name);
									String target_cs_formal_name = getFormalName(target_cs_name);
									if (src_cs_formal_name != null && target_cs_formal_name != null) {
										if (codingScheme.compareTo(getFormalName(relation.getSourceCodingScheme())) == 0 ||
											codingScheme.compareTo(getFormalName(relation.getTargetCodingScheme())) == 0) {
											v.add(label);
											break;
										}
									}
							    }
						    }
						}
					}
				} catch (Exception ex) {
                    ex.printStackTrace();
				}
			}
		}
		_codingScheme2MappingCodingSchemes.put(formalName, v);
		return SortUtils.quickSort(v);
	}

    public static boolean isMapping(String scheme, String version) {
		if (_isMappingHashMap == null) {
			setCodingSchemeMap();
		}
		if (_isMappingHashMap.containsKey(scheme)) {
			Boolean is_mapping = (Boolean) _isMappingHashMap.get(scheme);
			return is_mapping.booleanValue();
		}

        CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
        if (version != null)
            csvt.setVersion(version);

		try {
			LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
			CodingSchemeDataUtils codingSchemeDataUtils = new CodingSchemeDataUtils(lbSvc);
            boolean isMappingCS = codingSchemeDataUtils.isMapping(scheme, version);
            Boolean bool_obj = Boolean.valueOf(isMappingCS);
			_isMappingHashMap.put(scheme, bool_obj);
			return isMappingCS;

		} catch (Exception ex) {
			_isMappingHashMap.put(scheme, Boolean.FALSE);
            return false;
        }
    }

	public static boolean isExtension(String codingScheme, String version) {
		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
		CodingSchemeDataUtils codingSchemeDataUtils = new CodingSchemeDataUtils(lbSvc);
		return codingSchemeDataUtils.isExtension(codingScheme, version);
	}

	public static List<MappingSortOption> createMappingSortOption(int sortBy) {
        LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
        return new MappingUtils(lbSvc).createMappingSortOption(sortBy);
	}

    public static ResolvedConceptReferencesIterator getMappingDataIterator(String scheme, String version) {
		return getMappingDataIterator(scheme, version, MappingData.COL_SOURCE_CODE);
	}

    public static ResolvedConceptReferencesIterator getMappingDataIterator(String scheme, String version, int sortBy) {
		List<MappingSortOption> sortOptionList = createMappingSortOption(sortBy);
		return getMappingDataIterator(scheme, version, sortOptionList);
	}

    public static ResolvedConceptReferencesIterator getMappingDataIterator(String scheme, String version, List<MappingSortOption> sortOptionList) {
        LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
        return new MappingUtils(lbSvc).getMappingDataIterator(scheme, version, sortOptionList);
	}

    public static NameAndValueList getMappingAssociationNames(String scheme, String version) {
        LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
        return new MappingUtils(lbSvc).getMappingAssociationNames(scheme, version);
    }

    public static Vector getMappingCodingSchemesEntityParticipatesIn(String code, String namespace) {
        LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
        return new MappingUtils(lbSvc).getMappingCodingSchemesEntityParticipatesIn(code, namespace);
	}

	public static String uri2CodingSchemeName(String uri) {
	    if (!_uri2CodingSchemeNameHashMap.containsKey(uri)) return null;
	    return (String) _uri2CodingSchemeNameHashMap.get(uri);
	}

	public static String codingSchemeName2URI(String name) {
	    if (!_codingSchemeName2URIHashMap.containsKey(name)) return null;
	    return (String) _codingSchemeName2URIHashMap.get(name);
	}

	public static Vector getConceptDomainNames() {
		LexBIGService lbSvc = new RemoteServerUtil().createLexBIGService();
		return new CodingSchemeDataUtils(lbSvc).getConceptDomainNames();
	}


	public static Vector getCodingSchemeFormalNames() {
        if (_codingSchemeHashSet == null)
            setCodingSchemeMap();

		Vector v = new Vector();
		Set keyset = _formalName2LocalNameHashMap.keySet();
		Iterator iterator = keyset.iterator();
		while (iterator.hasNext()) {
			String t = (String) iterator.next();
			v.add(t);
		}
		return SortUtils.quickSort(v);
	}


	public static Vector getValueSetURIs() {
		Vector v = new Vector();
		LexEVSValueSetDefinitionServices vsd_service = RemoteServerUtil.getLexEVSValueSetDefinitionServices();
		if (vsd_service == null) {
			System.out.println("Unable to instantiate LexEVSValueSetDefinitionServices???");
			return null;
		}

        List list = vsd_service.listValueSetDefinitionURIs();
        for (int i=0; i<list.size(); i++) {
			String t = (String) list.get(i);
			v.add(t);
		}
		return SortUtils.quickSort(v);
	}

    public static ValueSetDefinition findValueSetDefinitionByURI(String uri) {
		if (uri == null) return null;
	    if (uri.indexOf("|") != -1) {
			Vector u = parseData(uri);
			uri = (String) u.elementAt(1);
		}

		String valueSetDefinitionRevisionId = null;
		try {
			LexEVSValueSetDefinitionServices vsd_service = RemoteServerUtil.getLexEVSValueSetDefinitionServices();
			if (vsd_service == null) {
				System.out.println("Unable to instantiate LexEVSValueSetDefinitionServices???");
				return null;
			}
			ValueSetDefinition vsd = vsd_service.getValueSetDefinition(new URI(uri), valueSetDefinitionRevisionId);
			return vsd;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static Vector getValueSetNamesAndURIs() {
		Vector v = new Vector();
		LexEVSValueSetDefinitionServices vsd_service = RemoteServerUtil.getLexEVSValueSetDefinitionServices();
		if (vsd_service == null) {
			System.out.println("Unable to instantiate LexEVSValueSetDefinitionServices???");
			return null;
		}
        List list = vsd_service.listValueSetDefinitionURIs();
        for (int i=0; i<list.size(); i++) {
			String t = (String) list.get(i);
			ValueSetDefinition vsd = findValueSetDefinitionByURI(t);
			String name = vsd.getValueSetDefinitionName();
			if (name == null) {
				name = "<NOT ASSIGNED>";
			}

			v.add(name + "|" + t);
		}
		return SortUtils.quickSort(v);
	}

	public static String getVSDURIByName(String name) {
		if (_VSDName2URIHashMap.containsKey(name)) {
			return (String) _VSDName2URIHashMap.get(name);
		}
		return null;
	}

	public static HashMap getVSDName2URIHashMap() {
		HashMap vSDName2URIHashMap = new HashMap();
		LexEVSValueSetDefinitionServices vsd_service = RemoteServerUtil.getLexEVSValueSetDefinitionServices();
		if (vsd_service == null) {
			System.out.println("Unable to instantiate LexEVSValueSetDefinitionServices???");
			return null;
		}
        List list = vsd_service.listValueSetDefinitionURIs();
        for (int i=0; i<list.size(); i++) {
			String uri = (String) list.get(i);
			ValueSetDefinition vsd = findValueSetDefinitionByURI(uri);
			String name = vsd.getValueSetDefinitionName();
            vSDName2URIHashMap.put(name, uri);
		}
		return vSDName2URIHashMap;
	}


	public static String getValueSetDefinitionURIByName(String vsd_name) {
		//Vector v = new Vector();
		LexEVSValueSetDefinitionServices vsd_service = RemoteServerUtil.getLexEVSValueSetDefinitionServices();
		if (vsd_service == null) {
			System.out.println("Unable to instantiate LexEVSValueSetDefinitionServices???");
			return null;
		}
        List list = vsd_service.listValueSetDefinitionURIs();
        for (int i=0; i<list.size(); i++) {
			String uri = (String) list.get(i);
			ValueSetDefinition vsd = findValueSetDefinitionByURI(uri);
			String name = vsd.getValueSetDefinitionName();
			if (name != null && name.compareTo(vsd_name) == 0) {
				return uri;
			}
		}
		return null;
	}


    public static String valueSetDefiniionURI2Name(String vsd_uri) {
		String metadata = getValueSetDefinitionMetadata(vsd_uri);
		Vector v = parseData(metadata);
		return (String) v.elementAt(0);
	}


    public static HashMap getCodingSchemeURN2ValueSetMetadataHashMap(Vector vsd_vec) {
        HashMap hmap = new HashMap();

        for (int i=0; i<vsd_vec.size(); i++) {
		    String vsd_str = (String) vsd_vec.elementAt(i);
		    Vector u = parseData(vsd_str);
		    String name = (String) u.elementAt(0);
		    String uri = (String) u.elementAt(1);

		    String description = (String) u.elementAt(2);

		    Vector cs_vec = getCodingSchemeURNsInValueSetDefinition(uri);

            if (cs_vec != null) {

				for (int k=0; k<cs_vec.size(); k++) {
					String cs_urn = (String) cs_vec.elementAt(k);

					String cs_name = uri2CodingSchemeName(cs_urn);

					if (hmap.containsKey(cs_name)) {
						Vector v = (Vector) hmap.get(cs_name);
						if (!v.contains(vsd_str)) {
							v.add(vsd_str);
							hmap.put(cs_name, v);
						}
					} else {
						Vector v = new Vector();
						v.add(vsd_str);
						hmap.put(cs_name, v);
					}
				}
		    }
		}
		return hmap;
	}


    public static HashMap getSource2ValueSetMetadataHashMap(Vector vsd_vec) {
        HashMap hmap = new HashMap();

        for (int i=0; i<vsd_vec.size(); i++) {
		    String vsd_str = (String) vsd_vec.elementAt(i);

		    Vector u = parseData(vsd_str);
		    String name = (String) u.elementAt(0);
		    String uri = (String) u.elementAt(1);
		    String description = (String) u.elementAt(2);
		    String domain = (String) u.elementAt(3);
		    String src_str = (String) u.elementAt(4);
		    if (src_str == null || src_str.compareTo("<NOT ASSIGNED>") == 0) {
				String key = "<NOT ASSIGNED>";
				if (hmap.containsKey(key)) {
					Vector v = (Vector) hmap.get(key);
					if (!v.contains(vsd_str)) {
						v.add(vsd_str);
						hmap.put(key, v);
					}
				} else {
					Vector v = new Vector();
					v.add(vsd_str);
					hmap.put(key, v);
				}
			} else {
		    	Vector src_vec = parseData(src_str);
		    	for (int j=0; j<src_vec.size(); j++) {
					String src = (String) src_vec.elementAt(j);
					if (hmap.containsKey(src)) {
						Vector v = (Vector) hmap.get(src);
						if (!v.contains(vsd_str)) {
							v.add(vsd_str);
							hmap.put(src, v);
						}
					} else {
						Vector v = new Vector();
						v.add(vsd_str);
						hmap.put(src, v);
					}

			    }
			}
		}
		return hmap;
	}

//===========================================================================================================================
// Value Set Hierarchy
//===========================================================================================================================

    public TreeUtils createTreeUtils() {
        LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
        TreeUtils treeUtils = new TreeUtils(lbSvc);
		return treeUtils;
	}

    public static ResolvedConceptReferenceList getValueSetHierarchyRoots() {
        String scheme = Constants.TERMINOLOGY_VALUE_SET;//"Terminology Value Set";
        String version = getVocabularyVersionByTag(scheme, Constants.PRODUCTION);
        LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
        TreeUtils treeUtils = new TreeUtils(lbSvc);
        ResolvedConceptReferenceList rcrl = treeUtils.getHierarchyRoots(scheme, version);

        if (rcrl == null) {
			scheme = Constants.TERMINOLOGY_VALUE_SET_NAME;//"Terminology_Value_Set.owl";
			version = getVocabularyVersionByTag(scheme, Constants.PRODUCTION);

			rcrl = treeUtils.getHierarchyRoots(scheme, version);
		}
        return rcrl;
	}

    public static Vector getValueSetDefinitionsBySource(String source) {
		return valueSetHierarchy.getValueSetDefinitionsBySource(source);
	}


    public static Vector getAvailableValueSetDefinitionSources() {
		return valueSetHierarchy.getAvailableValueSetDefinitionSources();
	}

    public static void printValueSetDefinitionHierarchyNode(int level, String root) {
		Vector children = (Vector) _valueSetDefinitionHierarchyHashMap.get(root);
		if (children != null) {
			for (int j=0; j<children.size(); j++) {
				String child = (String) children.elementAt(j);
                printValueSetDefinitionHierarchyNode(level+1, child);
			}
		}
	}

//////////////////////////////////////////////////////////////////////////////////////////////////////

	public static Vector getValueSetDefinitionMetadata() {
		if (!hasValueSets()) return null;

		if (_valueSetDefinitionMetadata != null) return _valueSetDefinitionMetadata;
		Vector valueSetDefinitionMetadata = new Vector();
		LexEVSValueSetDefinitionServices vsd_service = RemoteServerUtil.getLexEVSValueSetDefinitionServices();
		if (vsd_service == null) {
			System.out.println("Unable to instantiate LexEVSValueSetDefinitionServices???");
			return null;
		}

		try {
			List list = vsd_service.listValueSetDefinitionURIs();
			if (list == null || list.size() == 0) return null;
			for (int i=0; i<list.size(); i++) {
				String uri = (String) list.get(i);
				ValueSetDefinition vsd = findValueSetDefinitionByURI(uri);
				String metadata = getValueSetDefinitionMetadata(vsd);
				valueSetDefinitionMetadata.add(metadata);
			}
			SortUtils.quickSort(valueSetDefinitionMetadata);
	    } catch (Exception ex) {
			ex.printStackTrace();
		}
		return valueSetDefinitionMetadata;
	}

	public static String getValueSetDefinitionMetadata(String vsd_uri) {
		ValueSetDefinition vsd = findValueSetDefinitionByURI(vsd_uri);
		if (vsd == null) return null;
		return getValueSetDefinitionMetadata(vsd);
	}

    public static String getValueSetDefinitionMetadata(ValueSetDefinition vsd) {
		if (vsd== null) return null;
		String name = "";
		String uri = "";
		String description = "";
		String domain = "";
		String src_str = "";

		//String supportedSourceStr = "";
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

		//[GF#31718] Sources on value set home pages displaying wrong value.
		Mappings mappings = vsd.getMappings();
        java.util.Enumeration<? extends SupportedSource> supportedSourceEnum = mappings.enumerateSupportedSource();

		while (supportedSourceEnum.hasMoreElements()) {
			SupportedSource src = (SupportedSource) supportedSourceEnum.nextElement();
			//supportedSourceStr = supportedSourceStr + src.getContent() + ";";
			buf.append(src.getContent() + ";");
		}
		String supportedSourceStr = buf.toString();

		if (supportedSourceStr.length() > 0) {
			supportedSourceStr = supportedSourceStr.substring(0, supportedSourceStr.length()-1);
		}
		if (supportedSourceStr == null || supportedSourceStr.compareTo("") == 0) {
			supportedSourceStr = "<NOT ASSIGNED>";
		}

		return name + "|" + uri + "|" + description + "|" + domain + "|" + src_str + "|" + supportedSourceStr;
	}

//////////////////////////////////////////////////////////////////////////////////////////////////////

    public static Vector getCodingSchemeVersionsByURN(String urn) {
		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
		return new CodingSchemeDataUtils(lbSvc).getCodingSchemeVersionsByURN(urn);
	}


    public static Vector getCodingSchemeReferencesInValueSetDefinition(String uri) {
		HashSet hset = new HashSet();
	    if (uri.indexOf("|") != -1) {
			Vector u = parseData(uri);
			uri = (String) u.elementAt(1);
		}

		try {
			Vector w = new Vector();
			Vector urn_vec = getCodingSchemeURNsInValueSetDefinition(uri);
			if (urn_vec != null) {
				for (int i=0; i<urn_vec.size(); i++) {
					String urn = (String) urn_vec.elementAt(i);
					Vector v = getCodingSchemeVersionsByURN(urn);
					if (v != null) {
						for (int j=0; j<v.size(); j++) {
							String version = (String) v.elementAt(j);
							String urn_version = urn + "|" + version;
							if (!hset.contains(urn_version)) {
								hset.add(urn_version);
							    w.add(urn_version);
						    }
						}
					}
				}
				w = SortUtils.quickSort(w);
				return w;
		    }
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

    public static AbsoluteCodingSchemeVersionReferenceList vector2CodingSchemeVersionReferenceList(Vector v) {
		if (v == null) return null;
		AbsoluteCodingSchemeVersionReferenceList list = new AbsoluteCodingSchemeVersionReferenceList();
		for (int i=0; i<v.size(); i++) {
			String s = (String) v.elementAt(i);
			Vector u = parseData(s);
			String uri = (String) u.elementAt(0);
			String version = (String) u.elementAt(1);
			AbsoluteCodingSchemeVersionReference vAbsoluteCodingSchemeVersionReference
			    = new AbsoluteCodingSchemeVersionReference();
			vAbsoluteCodingSchemeVersionReference.setCodingSchemeURN(uri);
			vAbsoluteCodingSchemeVersionReference.setCodingSchemeVersion(version);
			list.addAbsoluteCodingSchemeVersionReference(vAbsoluteCodingSchemeVersionReference);
		}
		return list;
	}

    public static Vector getCodingSchemeReferencesInValueSetDefinition(String uri, String tag) {
		try {
			Vector w = new Vector();
			Vector urn_vec = getCodingSchemeURNsInValueSetDefinition(uri);
			if (urn_vec != null) {
				for (int i=0; i<urn_vec.size(); i++) {
					String urn = (String) urn_vec.elementAt(i);
					Vector v = getCodingSchemeVersionsByURN(urn, tag);
					if (v != null) {
						for (int j=0; j<v.size(); j++) {
							String version = (String) v.elementAt(j);
							w.add(urn + "|" + version);
						}
					}
				}
				w = SortUtils.quickSort(w);
				return w;
		    }
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}



    public static Vector getCodingSchemeVersionsByURN(String urn, String tag) {
		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
		return new CodingSchemeDataUtils(lbSvc).getCodingSchemeVersionsByURN(urn, tag);
	}

    public static Vector getMatchedMetathesaurusCUIs(String scheme, String version,
        String ltag, String code) {
        Entity c = getConceptByCode(scheme, version, ltag, code);
        if (c != null) {
            Vector v = getConceptPropertyValues(c, "NCI_META_CUI");
            if (v == null || v.size() == 0) {
				return getConceptPropertyValues(c, "UMLS_CUI");
			}        }
        return null;
    }


    public static Vector getMatchedMetathesaurusCUIs(Entity c) {
        if (c != null) {
            Vector v = getConceptPropertyValues(c, "NCI_META_CUI");
            if (v == null || v.size() == 0) {
				return getConceptPropertyValues(c, "UMLS_CUI");
			}
        }
        return null;
    }

    public static String getMappingDisplayName(String scheme, String name) {
		HashMap hmap = (HashMap) _mappingDisplayNameHashMap.get(scheme);
		if (hmap == null) {
			return name;
		}
		Object obj = hmap.get(name);
		if (obj == null) {
			return name;
		}
		return obj.toString();
	}

    private static HashMap getPropertyValues(String scheme, String version, String propertyType, String propertyName) {
		LexBIGService lbSvc = new RemoteServerUtil().createLexBIGService();
		return new ConceptDetails(lbSvc).getPropertyValues(scheme, version, propertyType, propertyName);
	}


//  Key coding scheme name$version$code
//  Value: property value (delimeter: ;)
    public static HashMap getPropertyValuesInBatch(List list, String propertyName) {

        if (list == null) return null;
        HashMap hmap = new HashMap();
        if (list.size() == 0) return hmap;

        PropertyExtension extension = null;
        try {
            LexBIGService lbSvc = new RemoteServerUtil().createLexBIGService();

            if (lbSvc == null) {
                _logger.warn("lbSvc = null");
                return null;
            }
            extension = (PropertyExtension) lbSvc.getGenericExtension("property-extension");
            if (extension == null) {
                _logger.error("Error! PropertyExtension is null!");
                return null;
            }
		} catch (Exception ex) {
			return null;
		}


        //Vector cs_name_vec = new Vector();
        //Vector cs_version_vec = new Vector();
        HashSet hset = new HashSet();

        HashMap csnv2codesMap = new HashMap();

		for (int i=0; i<list.size(); i++) {
			ResolvedConceptReference rcr = (ResolvedConceptReference) list.get(i);
			String cs_name = rcr.getCodingSchemeName();

			boolean conceptStatusSupported = false;
			if (_vocabulariesWithConceptStatusHashSet.contains(getFormalName(cs_name))) {
				String version = rcr.getCodingSchemeVersion();
				String cs_name_and_version = cs_name + "$" + version;
				if (!hset.contains(cs_name_and_version)) {
					hset.add(cs_name_and_version);
					//cs_name_vec.add(cs_name);
					//cs_version_vec.add(version);
					ArrayList alist = new ArrayList();
					alist.add(rcr.getConceptCode());
					csnv2codesMap.put(cs_name_and_version, alist);

				} else {
					ArrayList alist = (ArrayList) csnv2codesMap.get(cs_name_and_version);
					alist.add(rcr.getConceptCode());
					csnv2codesMap.put(cs_name_and_version, alist);
				}

			}
		}

		//Iterator it = csnv2codesMap.keySet().iterator();
		Iterator it = csnv2codesMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry thisEntry = (Entry) it.next();
			//String key = (String) it.next();
			String key = (String) thisEntry.getKey();

			ArrayList alist = (ArrayList) csnv2codesMap.get(key);

			Vector u = parseData(key, "$");
			String scheme = (String) u.elementAt(0);
			String version = (String) u.elementAt(1);
			CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
			if (version != null) csvt.setVersion(version);
			String[] a = new String[alist.size()];
			for (int i=0; i<alist.size(); i++) {
				a[i] = (String) alist.get(i);
			}

            try {
				Map<String,String> propertyMap =
					extension.getProperty(scheme, csvt, propertyName, Arrays.asList(a));

				for (Entry<String, String> entry : propertyMap.entrySet()) {
					hmap.put(key + "$" + entry.getKey(), entry.getValue());
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}

        return hmap;
	}

    public static List<String> getDistinctNamespacesOfCode(
            String codingScheme,
            String version,
            String code) {
        LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
        return new ConceptDetails(lbSvc).getDistinctNamespacesOfCode(codingScheme, version, code);
	}

    public static String getVocabularyVersionTag(String codingSchemeName, String version) {
        if (codingSchemeName == null)
            return null;
        if (_codingSchemeTagHashMap != null) {
			String key = null;
			if (version == null) {
				key = codingSchemeName + "$null";
			} else {
				key = codingSchemeName + "$" + version;
			}
			if (_codingSchemeTagHashMap.containsKey(key)) {
				String tag = (String) _codingSchemeTagHashMap.get(key);
				return tag;
			}
		}
		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
		String tag_str = new CodingSchemeDataUtils(lbSvc).getVocabularyVersionTag(codingSchemeName, version);
		if (tag_str != null && tag_str.compareTo("<NOT AVAILABLE>") == 0) return tag_str;
		if (_codingSchemeTagHashMap == null) {
			_codingSchemeTagHashMap = new HashMap();
		}
		String key = null;
		key = codingSchemeName + "$" + version;
		_codingSchemeTagHashMap.put(key, tag_str);
		return tag_str;
    }


    public static boolean isNull(String value) {
		return gov.nih.nci.evs.browser.utils.StringUtils.isNull(value);
	}

    public static boolean isNullOrBlank(String value) {
		return gov.nih.nci.evs.browser.utils.StringUtils.isNullOrBlank(value);
	}

    public static boolean isNullOrEmpty(Vector v) {
		return gov.nih.nci.evs.browser.utils.StringUtils.isNullOrEmpty(v);
	}



	public static String getNavigationTabType(String dictionary, String version,
	        String vsd_uri, String nav_type) {
		if (nav_type != null)
		    return nav_type;
		if (vsd_uri != null)
			return "valuesets";
		if (dictionary != null) {
			boolean isMapping = isMapping(dictionary, version);
			return isMapping ? "mappings" : "terminologies";
		}
		return null;
	}

	public static boolean validateCodingSchemeVersion(String codingSchemeName, String version) {
		if (!_localName2FormalNameHashMap.containsKey(codingSchemeName)) return false;
	    String formalname = (String) _localName2FormalNameHashMap.get(codingSchemeName);
	    if (formalname == null) return false;

	    if (!_formalName2VersionsHashMap.containsKey(formalname)) return false;
	    Vector version_vec = (Vector) _formalName2VersionsHashMap.get(formalname);
	    if (version_vec == null) return false;
	    if (!version_vec.contains(version)) return false;
	    return true;
    }


	public static String getVersionReleaseDate(String codingSchemeName, String version) {
        if (_versionReleaseDateHashMap == null) {
            setCodingSchemeMap();
        }
		String key = getFormalName(codingSchemeName) + "$" + version;
		if (_versionReleaseDateHashMap.containsKey(key)) {
			return (String) _versionReleaseDateHashMap.get(key);
		}
		return null;
	}


    public static Vector getSupportedPropertyQualifier(CodingScheme cs) {
		Vector v = new Vector();
		if (cs != null) {
			SupportedPropertyQualifier[] qualifiers = cs.getMappings().getSupportedPropertyQualifier();
			for (int i=0; i<qualifiers.length; i++)
			{
				v.add(qualifiers[i].getLocalId());
			}
			return v;
	    }
	    return null;
	}

    public static boolean hasSourceCodeQualifier(String scheme) {
	    String formalName = getFormalName(scheme);
	    if (formalName == null) return false;
	    if ( _source_code_schemes.contains(formalName)) return true;
	    return false;
    }

    public static CodedNodeSet getNodeSet(LexBIGService lbSvc, String scheme, CodingSchemeVersionOrTag versionOrTag)
        throws Exception {
		return new ConceptDetails(lbSvc).getNodeSet(scheme, versionOrTag);
	}

	public static boolean isInteger( String input )	{
		return gov.nih.nci.evs.browser.utils.StringUtils.isInteger(input);
	}

    public static boolean isCaptchaOptionValid(String value) {
	   if (isNullOrBlank(value)) return true;
	   //return Arrays.asList(VALID_CAPTCHA_OTPIONS).contains(value);

       if (value.compareToIgnoreCase(Constants.DEFAULT_CAPTCHA_OTPION) == 0) return true;
       if (value.compareToIgnoreCase(Constants.AUDIO_CAPTCHA_OTPION) == 0) return true;
       return false;

    }

    public static HashMap getListOfCodingSchemeVersionsUsedInResolutionHashMap() {
		return _listOfCodingSchemeVersionsUsedInResolutionHashMap;
	}

    public static void dumpListOfCodingSchemeVersionsUsedInResolutionHashMap() {
		if (getListOfCodingSchemeVersionsUsedInResolutionHashMap() == null) {
			return;
		}

		Iterator it = _listOfCodingSchemeVersionsUsedInResolutionHashMap.keySet().iterator();
		while (it.hasNext()) {
			String cs_name = (String) it.next();
			System.out.println("\nKEY: " + cs_name);
			HashMap hmap = (HashMap) _listOfCodingSchemeVersionsUsedInResolutionHashMap.get(cs_name);
			//Iterator it2 = hmap.keySet().iterator();
			Iterator it2 = hmap.entrySet().iterator();
			while (it2.hasNext()) {
				Entry entry = (Entry) it2.next();
				String uri = (String) entry.getKey();
				String version = (String) entry.getValue();
			    //String uri = (String) it2.next();
				//String version = (String) hmap.get(uri);
				System.out.println("\t" + uri + " --> " + version);
			}
		}
	}


    private static void createListOfCodingSchemeVersionsUsedInResolutionHashMap() {
		if (_listOfCodingSchemeVersionsUsedInResolutionHashMap != null) {
			return;
		}
		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
		_listOfCodingSchemeVersionsUsedInResolutionHashMap = new CodingSchemeDataUtils(lbSvc).createListOfCodingSchemeVersionsUsedInResolutionHashMap();
    }

    private static void updateListOfCodingSchemeVersionsUsedInResolutionHashMap() {
		if (_listOfCodingSchemeVersionsUsedInResolutionHashMap == null) return;

		try {
			Iterator it = _listOfCodingSchemeVersionsUsedInResolutionHashMap.keySet().iterator();
			while (it.hasNext()) {
				String cs_name = (String) it.next();
				HashMap hmap = (HashMap) _listOfCodingSchemeVersionsUsedInResolutionHashMap.get(cs_name);
				if (hmap == null) hmap = new HashMap();
				Iterator it2 = hmap.entrySet().iterator();
				while (it2.hasNext()) {
					Entry entry = (Entry) it2.next();
					String uri = (String) entry.getKey();
					String version = (String) entry.getValue();
					String coding_scheme_name = (String) _uri2CodingSchemeNameHashMap.get(uri);
					hmap.put(coding_scheme_name, version);
				}
				_listOfCodingSchemeVersionsUsedInResolutionHashMap.put(cs_name, hmap);
			}
		} catch (Exception ex) {
			System.out.println("WARNING: updateListOfCodingSchemeVersionsUsedInResolutionHashMap throws exceptions.");
		}
	}

    public static String findVersionOfCodingSchemeUsedInValueSetResolution(String value_set_name, String coding_scheme_name) {
		if (_listOfCodingSchemeVersionsUsedInResolutionHashMap == null) return null;
		HashMap hmap = (HashMap) _listOfCodingSchemeVersionsUsedInResolutionHashMap.get(value_set_name);
		if (hmap == null) return null;
		return (String) hmap.get(coding_scheme_name);
	}

    public static Entity getConceptByCode(String codingSchemeName, String vers, String code, String ns, boolean use_ns) {
		LexBIGService lbSvc = new RemoteServerUtil().createLexBIGService();
		return new ConceptDetails(lbSvc).getConceptByCode(codingSchemeName, vers, code, ns, use_ns);
    }

    public static ConceptReferenceList createConceptReferenceList(
        String[] codes, String codingSchemeName, String ns) {
		return new ConceptDetails().createConceptReferenceList(codes, codingSchemeName, ns);
    }

	public static String getCSName(String vocabularyName) {
        if (_uri2CodingSchemeNameHashMap == null) setCodingSchemeMap();
		String formalname = getFormalName(vocabularyName);
		if (_uri2CodingSchemeNameHashMap.get(formalname) == null) return formalname;
		String t = (String) _uri2CodingSchemeNameHashMap.get(formalname);
		return t;
	}

	public static String getDisplayName(String vocabularyName) {
        if (_formalName2DisplayNameHashMap == null) setCodingSchemeMap();
		String formalname = getFormalName(vocabularyName);
		if (_formalName2DisplayNameHashMap.get(formalname) == null) return formalname;
		String t = (String) _formalName2DisplayNameHashMap.get(formalname);
		return t;
	}

//==========================================================================================


    public static HashMap parseNCBOWidgetString(String s) {
		HashMap hmap = new HashMap();
		Vector v = parseData(s, ";");
		for (int i=0; i<v.size(); i++) {
			String t = (String) v.elementAt(i);
			int n = t.indexOf("|");
			String key = t.substring(0, n);
			String value = t.substring(n+1, t.length());
			hmap.put(key, value);
		}
		return hmap;
	}

    public static boolean visualizationWidgetSupported(String dictionary) {
		String csName = getCSName(dictionary);
		if (csName == null) return false;
		if (_visualizationWidgetHashMap == null) return false;
		if (_visualizationWidgetHashMap.containsKey(csName)) return true;
		return false;
	}

	public static String getNCBOOntologyId(String dictionary) {
		boolean bool = visualizationWidgetSupported(dictionary);
		if (!bool) return null;
		String csName = getCSName(dictionary);
		String ncbo_widget_info_str = (String) _visualizationWidgetHashMap.get(csName);
		if (ncbo_widget_info_str == null) return null;
		Vector v = parseData(ncbo_widget_info_str);
		if (v == null) return null;
		return (String) v.elementAt(0);
	}

	public static String getNCBOOntologyAbbreviation(String dictionary) {
		boolean bool = visualizationWidgetSupported(dictionary);
		if (!bool) return null;
		String csName = getCSName(dictionary);
		String ncbo_widget_info_str = (String) _visualizationWidgetHashMap.get(csName);
		if (ncbo_widget_info_str == null) return null;
		Vector v = parseData(ncbo_widget_info_str);
		if (v == null) return null;
		return (String) v.elementAt(1);
	}

	public static String getNCBOOntologyNamespace(String dictionary) {
		boolean bool = visualizationWidgetSupported(dictionary);
		if (!bool) return null;
		String csName = getCSName(dictionary);
		String ncbo_widget_info_str = (String) _visualizationWidgetHashMap.get(csName);
		if (ncbo_widget_info_str == null) return null;
		Vector v = parseData(ncbo_widget_info_str);
		if (v == null) return null;
		return (String) v.elementAt(0);
	}



    public static String encodeTerm(String s) {
		return gov.nih.nci.evs.browser.utils.StringUtils.encodeTerm(s);
    }

    public static String encode_term(String s) {
		return gov.nih.nci.evs.browser.utils.StringUtils.encode_term(s);
    }

    public static Vector getCodingSchemeURNsInValueSetDefinition(String uri) {
	   return valueSetHierarchy.getCodingSchemeURNsInValueSetDefinition(uri);
    }

    public static Vector uri2CodingSchemeName(Vector uri_vec) {
	    if (uri_vec == null) return null;
	    Vector u = new Vector();
	    for (int i=0; i<uri_vec.size(); i++) {
			String uri = (String) uri_vec.elementAt(i);
			String name = (String) _VSDURI2NameHashMap.get(uri);
			u.add(name);
		}
		return u;
    }



////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static boolean isNCIT_OR_NCIM(String display_name) {
		if (display_name == null) return false;
		for (int i=0; i<Constants.NCIT_OR_NCIM.length; i++) {
			String name = (String) Constants.NCIT_OR_NCIM[i];
			if (display_name.compareToIgnoreCase(name) == 0) return true;
		}
		return false;
	}


    public static Vector getNCImCodes(String scheme, String version, String code) {
        Vector w = new Vector();
        CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
        if (version != null) {
			csvt.setVersion(version);
		}
		try {
			LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
			if (lbSvc == null) {
				_logger
					.warn("WARNING: Unable to connect to instantiate LexBIGService ???");
				return null;
			}

			ConceptReferenceList crefs = ConvenienceMethods.createConceptReferenceList(new String[] { code }, scheme);
			CodedNodeSet cns = lbSvc.getCodingSchemeConcepts(scheme, csvt);

			if (cns == null) {
				return null;
			}
			cns = cns.restrictToStatus(ActiveOption.ALL, null);
			cns = cns.restrictToCodes(crefs);
			ResolvedConceptReferenceList matches = cns.resolveToList(null, null, null, 1);
			if (matches.getResolvedConceptReferenceCount() > 0) {
				ResolvedConceptReference ref = (ResolvedConceptReference) matches.enumerateResolvedConceptReference()
						.nextElement();
				Entity node = ref.getEntity();
				return getNCImCodes(node);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return w;
    }


    public static Vector getNCImCodes(Entity node) {
		if (node == null) return null;
        Vector w = new Vector();
		Property[] props = node.getAllProperties();
		for (int i = 0; i < props.length; i++) {
			Property prop = props[i];
			 PropertyQualifier[] qualifiers = prop.getPropertyQualifier();
			 for (int k=0; k<qualifiers.length; k++) {
				  PropertyQualifier qualifier = qualifiers[k];
			 }
			 Source[] sources = prop.getSource();
			 for (int k=0; k<sources.length; k++) {
				  Source source = sources[k];
			 }
			 if (Arrays.asList(Constants.NCIM_CODE_PROPERTYIES).contains(prop.getPropertyName())) {
				 if (!w.contains(prop.getValue().getContent())) {
					w.add(prop.getValue().getContent());
				 }
			 }
		}
		return w;
    }


    public static HashMap getCodingSchemeValueSetSubTree(String scheme) {
		//if (terminologyValueSetTree == null) {
		//	terminologyValueSetTree = ValueSetHierarchy.getCodingSchemeValueSetTree(null, null);
		//}
		String formalname = getFormalName(scheme);
		HashMap tree = new HashMap();
		TreeItem ti = new TreeItem("<Root>", "Root node");
		ti._expandable = false;
		TreeItem root = (TreeItem) terminologyValueSetTree.get("<Root>");
        for (String association : root._assocToChildMap.keySet()) {
			List<TreeItem> child_nodes = root._assocToChildMap.get(association);
			for (TreeItem childItem : child_nodes) {
				if (childItem._text.compareTo(formalname) == 0) {
					ti.addChild(association, childItem);
					ti._expandable = true;
		            tree.put("<Root>", ti);
                    return tree;
				}
			}
		}
		return tree;
	}

	public static ResolvedConceptReferencesIterator resolveCodingScheme(String cs_uri, String version, boolean resolveObjects) {
		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
		CodingSchemeDataUtils codingSchemeDataUtils = new CodingSchemeDataUtils(lbSvc);
		return codingSchemeDataUtils.resolveCodingScheme(cs_uri, version, resolveObjects);
	}

    public static HashSet getVocabulariesWithConceptStatusHashSet() {
        return _vocabulariesWithConceptStatusHashSet;
    }

    public static HashMap getUri2CodingSchemeNameHashMap() {
		return _uri2CodingSchemeNameHashMap;
	}

	public static HashMap getLocalName2FormalNameHashMap() {
	    return _localName2FormalNameHashMap;
	}

	public static HashMap getFormalNameVersion2MetadataHashMap() {
        return _formalNameVersion2MetadataHashMap;
	}

	public static HashMap getFormalName2MetadataHashMap() {
        return _formalName2MetadataHashMap;
	}

	public static HashMap getVisualizationWidgetHashMap() {
        return _visualizationWidgetHashMap;
	}

    public static Boolean versionHasTag(CodingSchemeRendering csr, String ltag) {
		if (csr == null || ltag == null) return null;
		RenderingDetail rd = csr.getRenderingDetail();
		CodingSchemeTagList cstl = rd.getVersionTags();
		java.lang.String[] tags = cstl.getTag();

		if (tags == null) return Boolean.FALSE;
		if (tags.length > 0) {
			for (int j = 0; j < tags.length; j++) {
				String version_tag = (String) tags[j];
				if (version_tag != null && version_tag.compareToIgnoreCase(ltag) == 0) {
					return Boolean.TRUE;
				}
			}
		}
		return Boolean.FALSE;
	}

    public static String getMetadataValue(String codingSchemeName, String version, String urn, String propertyName) {
		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
		return new MetadataUtils(lbSvc).getMetadataValue(codingSchemeName, version, urn, propertyName);
    }

    public static Vector getMetadataNameValuePairs(String codingSchemeName, String version, String urn) {
		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
		return new MetadataUtils(lbSvc).getMetadataNameValuePairs(codingSchemeName, version, urn);
	}

    public static Boolean isCodingSchemeAvailable(String cs_name) {
		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
		return new CodingSchemeDataUtils(lbSvc).isCodingSchemeAvailable(cs_name);
    }

    public static boolean isNCIT(String scheme) {
        if (scheme == null) return true;
        return Arrays.asList(Constants.NCIT_NAMES).contains(scheme);
    }

    public static Boolean isNCITAvailable() {
		return NCI_THESAURUS_AVAILABLE;
	}





    public static String getDefaultOntologiesToSearchOnStr() {
		if (_defaultOntologiesToSearchOnStr != null) return _defaultOntologiesToSearchOnStr;
        if (_ontologies == null) setCodingSchemeMap();
        Vector display_name_vec = getSortedOntologies();
        StringBuffer buf = new StringBuffer();
        buf.append("|");
        for (int i = 0; i < display_name_vec.size(); i++) {
		    OntologyInfo info = (OntologyInfo) display_name_vec.elementAt(i);
		    if (info.getLabel().indexOf(Constants.NCIT_CS_NAME) != -1 || info.getLabel().indexOf(Constants.NCI_THESAURUS) != -1) {
			//if (isNCIT(info.getLabel())) {
		        if (!isNull(info.getTag()) && info.getTag().compareToIgnoreCase(Constants.PRODUCTION) == 0) {
                    buf.append(info.getLabel() + "|");
			    }
		    }
	    }
	    _defaultOntologiesToSearchOnStr = buf.toString();
	    return _defaultOntologiesToSearchOnStr;
    }

    public static Vector getSortedOntologies() {
	   if (_sortedOntologies != null) return _sortedOntologies;

	   Vector display_name_vec = new Vector();
	   List ontology_list = getOntologyList();
	   int num_vocabularies = ontology_list.size();

	   for (int i = 0; i < ontology_list.size(); i++) {
			SelectItem item = (SelectItem) ontology_list.get(i);
			String value = (String) item.getValue();
			String label = (String) item.getLabel();

			String scheme = key2CodingSchemeName(value);
			String short_scheme_name = uri2CodingSchemeName(scheme);

			String version = key2CodingSchemeVersion(value);
			String display_name = getMetadataValue(short_scheme_name, version, "display_name");
			if (isNull(display_name)) {
			   display_name = getLocalName(scheme);
			}
			String sort_category = getMetadataValue(scheme, version, "vocabulary_sort_category");
			if (sort_category == null) {
				sort_category = "0";
			}
			String tag = getVocabularyVersionTag(short_scheme_name, version);

	        //OntologyInfo info = new OntologyInfo(short_scheme_name, display_name, version, label, sort_category);
	        OntologyInfo info = new OntologyInfo(short_scheme_name, display_name, version, tag, label, sort_category);
			display_name_vec.add(info);
	   }

	   for (int i = 0; i < display_name_vec.size(); i++) {
		    OntologyInfo info = (OntologyInfo) display_name_vec.elementAt(i);
		    if (!isNull(info.getTag()) && info.getTag().compareToIgnoreCase(Constants.PRODUCTION) == 0) {
			    Vector w = getNonProductionOntologies(display_name_vec, info.getCodingScheme());
			    if (w.size() > 0) {
			        info.setHasMultipleVersions(true);
			    }
		    }
	   }
	   return display_name_vec;
    }


	public static Vector getNonProductionOntologies(Vector v, String scheme) {
		Vector u = new Vector();
		for (int i = 0; i < v.size(); i++) {
			OntologyInfo info = (OntologyInfo) v.elementAt(i);
			if (scheme.compareTo(info.getCodingScheme()) == 0) {
				if (isNull(info.getTag()) || info.getTag().compareToIgnoreCase(Constants.PRODUCTION) != 0) {
					u.add(info);
				}
			}
		}
		if (u.size() > 0) {
			Collections.sort(u, new OntologyInfo.ComparatorImpl());
		}
		return u;
	}

	public static Vector sortOntologyInfo(Vector v) {
		Vector u = new Vector();
        Collections.sort(v, new OntologyInfo.ComparatorImpl());
		for (int i = 0; i < v.size(); i++) {
			OntologyInfo info = (OntologyInfo) v.elementAt(i);
			if (!isNull(info.getTag()) && info.getTag().compareToIgnoreCase(Constants.PRODUCTION) == 0) {
				u.add(info);
			    if (info.getExpanded()) {
					Vector w = getNonProductionOntologies(v, info.getCodingScheme());
					for (int j=0; j<w.size(); j++) {
						OntologyInfo ontologyInfo = (OntologyInfo) w.elementAt(j);
						u.add(ontologyInfo);
					}
				}
			}
		}
		return u;
	}



//==============================================
//           NCItBrowserProperties
//==============================================


    public static HashMap getNCBOWidgetString() {
        String ncbo_widget_info = NCItBrowserProperties.getNCBO_WIDGET_INFO();
		if (isNull(ncbo_widget_info)) {
			//System.out.println("(*) ncbo_widget_info: " + ncbo_widget_info);
			//System.out.println("(*) computeNCBOWidgetString ... ");
			ncbo_widget_info = computeNCBOWidgetString();
		}
		//System.out.println("(*) ncbo_widget_info: " + ncbo_widget_info);
		return parseNCBOWidgetString(ncbo_widget_info);
	}


	public static String computeNCBOWidgetString() {
		StringBuffer buf = new StringBuffer();


//      _bioportalAcronym2NameHashMap = RESTClient.getBioportalAcronym2NameHashMap(_ncbo_api_key);

		HashMap map = NCItBrowserProperties.getBioportalAcronym2NameHashMap();

		if (map == null) {
			System.out.println("(*) getBioportalAcronym2NameHashMap returns null??? ");
			return null;
		}

        Set entrys = map.entrySet() ;
        Iterator iter = entrys.iterator() ;
        while(iter.hasNext()) {
            Map.Entry me = (Map.Entry)iter.next();
            String acronym = (String) me.getKey();
            //String name = (String) me.getValue();
            if (_localName2FormalNameHashMap.containsKey(acronym)) {
				System.out.println("(*) _localName2FormalNameHashMap containsKey " + acronym);
				String formalname = (String) _localName2FormalNameHashMap.get(acronym);
				String cs_name = (String) _uri2CodingSchemeNameHashMap.get(formalname);
				buf.append(cs_name + "|" + formalname + "|" + acronym + ";");
            }
        }
        String t = buf.toString();
        if (t.indexOf(Constants.NCI_THESAURUS) == -1) {
			t = t + Constants.DEFAULT_NCBO_WIDGET_INFO;//"NCI_Thesaurus|NCI_Thesaurus|NCIT;";
		}
        return t;
	}

	public static String createVisualizationWidgetURL(String abbreviation, String code) {
		String api_key = getAPIKey();
		String csName = getCSName(abbreviation);
		String purl = getNCBOOntologyNamespace(csName);
		if (purl == null) return null;

		if (purl.indexOf("/obo/") != -1) {
			code = code.replaceAll(":", "_");
		}
		return Constants.NCBO_WIDGET_QUERY_STRING + abbreviation + "&class="
			       + HTTPUtils.encode(purl + code)
			       + "&apikey=" + getAPIKey();
	}


    public static String getVisualizationWidgetURL(String dictionary, String code) {
		String abbreviation = getNCBOOntologyAbbreviation(dictionary);
		if (abbreviation == null) return null;
		return createVisualizationWidgetURL(abbreviation, code);
	}


    public String getNCICBContactURL() {
        if (_ncicbContactURL != null) {
            return _ncicbContactURL;
        }
        String default_url = "ncicb@pop.nci.nih.gov";
        NCItBrowserProperties properties = null;
        try {
            properties = NCItBrowserProperties.getInstance();
            _ncicbContactURL =
                properties.getProperty(NCItBrowserProperties.NCICB_CONTACT_URL);
            if (_ncicbContactURL == null) {
                _ncicbContactURL = default_url;
            }
        } catch (Exception ex) {

        }
        return _ncicbContactURL;
    }

    public String getTerminologySubsetDownloadURL() {
        NCItBrowserProperties properties = null;
        try {
            properties = NCItBrowserProperties.getInstance();
            _terminologySubsetDownloadURL =
                properties
                    .getProperty(NCItBrowserProperties.TERMINOLOGY_SUBSET_DOWNLOAD_URL);
        } catch (Exception ex) {

        }
        return _terminologySubsetDownloadURL;
    }

    public String getNCITBuildInfo() {
        if (_ncitBuildInfo != null) {
            return _ncitBuildInfo;
        }
        String default_info = "N/A";
        NCItBrowserProperties properties = null;
        try {
            properties = NCItBrowserProperties.getInstance();
            _ncitBuildInfo =
                properties.getProperty(NCItBrowserProperties.NCIT_BUILD_INFO);
            if (_ncitBuildInfo == null) {
                _ncitBuildInfo = default_info;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return _ncitBuildInfo;
    }

    public String getApplicationVersion() {
        if (_ncitAppVersion != null) {
            return _ncitAppVersion;
        }
        String default_info = "1.0";
        NCItBrowserProperties properties = null;
        try {
            properties = NCItBrowserProperties.getInstance();
            _ncitAppVersion =
                properties.getProperty(NCItBrowserProperties.NCIT_APP_VERSION);
            if (_ncitAppVersion == null) {
                _ncitAppVersion = default_info;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return _ncitAppVersion;
    }

    public String getApplicationVersionDisplay() {
        if (_ncitAppVersionDisplay != null)
            return _ncitAppVersionDisplay;
        try {
            NCItBrowserProperties properties = NCItBrowserProperties.getInstance();
            String value =
                properties.getProperty(NCItBrowserProperties.NCIT_APP_VERSION_DISPLAY);
            if (value == null)
                return _ncitAppVersionDisplay = "";
            String version = getApplicationVersion();
            value = value.replace("$application.version", version);
            return _ncitAppVersionDisplay = value;
        } catch (Exception ex) {
            ex.printStackTrace();
            return _ncitAppVersionDisplay = "";
        }
    }

    public String getNCITAnthillBuildTagBuilt() {
        if (_ncitAnthillBuildTagBuilt != null) {
            return _ncitAnthillBuildTagBuilt;
        }
        String default_info = "N/A";
        NCItBrowserProperties properties = null;
        try {
            properties = NCItBrowserProperties.getInstance();
            _ncitAnthillBuildTagBuilt =
                properties
                    .getProperty(NCItBrowserProperties.ANTHILL_BUILD_TAG_BUILT);
            if (_ncitAnthillBuildTagBuilt == null) {
                _ncitAnthillBuildTagBuilt = default_info;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return _ncitAnthillBuildTagBuilt;
    }

    public String getNCItURL() {
        if (_ncitURL != null) {
            return _ncitURL;
        }
        String default_info = "N/A";
        NCItBrowserProperties properties = null;
        try {
            properties = NCItBrowserProperties.getInstance();
            _ncitURL = properties.getProperty(NCItBrowserProperties.NCIT_URL);
            if (_ncitURL == null) {
                _ncitURL = default_info;
            }
        } catch (Exception ex) {

        }
        return _ncitURL;
    }

    public String getNCImURL() {
        if (_ncimURL != null) {
            return _ncimURL;
        }
        String default_info = "http://ncim.nci.nih.gov";
        NCItBrowserProperties properties = null;
        try {
            properties = NCItBrowserProperties.getInstance();
            _ncimURL = properties.getProperty(NCItBrowserProperties.NCIM_URL);
            if (_ncimURL == null) {
                _ncimURL = default_info;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return _ncimURL;
    }

    public String getEVSServiceURL() {
        if (_evsServiceURL != null) {
            return _evsServiceURL;
        }
        String default_info = "Local LexEVS";
        NCItBrowserProperties properties = null;
        try {
            properties = NCItBrowserProperties.getInstance();
            _evsServiceURL =
                properties.getProperty(NCItBrowserProperties.EVS_SERVICE_URL);
            if (_evsServiceURL == null) {
                _evsServiceURL = default_info;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return _evsServiceURL;
    }

    public String getTermSuggestionURL() {
        NCItBrowserProperties properties = null;
        try {
            properties = NCItBrowserProperties.getInstance();
            _term_suggestion_application_url =
                properties
                    .getProperty(NCItBrowserProperties.TERM_SUGGESTION_APPLICATION_URL);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return _term_suggestion_application_url;
    }

    public static void main(String[] args) {
        String scheme = "NCI Thesaurus";
        String version = null;
        // Breast Carcinoma (Code C4872)
        String code = "C4872";

        DataUtils test = new DataUtils();

        //HashMap hmap = test.getRelationshipHashMap(scheme, version, code);
        //test.dumpRelationshipHashMap(hmap);
    }

}

