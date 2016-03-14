package gov.nih.nci.evs.browser.utils;

import java.util.*;
import java.io.*;
import java.net.*;
import org.LexGrid.LexBIG.caCore.interfaces.*;
import org.LexGrid.LexBIG.LexBIGService.*;
import org.LexGrid.LexBIG.Impl.*;

import gov.nih.nci.system.client.*;
import gov.nih.nci.evs.security.*;

import org.apache.log4j.*;

import org.LexGrid.LexBIG.caCore.interfaces.LexEVSDistributed;
import org.lexgrid.valuesets.LexEVSValueSetDefinitionServices;


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
import org.LexGrid.valueSets.ValueSetDefinitionReference;
import org.LexGrid.commonTypes.EntityDescription;
import org.LexGrid.valueSets.CodingSchemeReference;

import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;

import org.LexGrid.LexBIG.DataModel.Collections.SortOptionList;
import org.LexGrid.LexBIG.Exceptions.LBException;

import org.lexgrid.valuesets.dto.ResolvedValueSetCodedNodeSet;


import org.LexGrid.LexBIG.DataModel.Core.AbsoluteCodingSchemeVersionReference;
import org.LexGrid.LexBIG.DataModel.Collections.AbsoluteCodingSchemeVersionReferenceList;


import org.LexGrid.LexBIG.DataModel.Core.ConceptReference;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.types.CodingSchemeVersionStatus;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;

import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;

import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;

import org.lexgrid.valuesets.dto.ResolvedValueSetCodedNodeSet;
import org.LexGrid.LexBIG.DataModel.Collections.AbsoluteCodingSchemeVersionReferenceList;
import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;

import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.PropertyType;
import org.LexGrid.LexBIG.DataModel.Collections.ConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.ConceptReference;

import org.lexgrid.resolvedvalueset.LexEVSResolvedValueSetService;
import org.lexgrid.resolvedvalueset.impl.LexEVSResolvedValueSetServiceImpl;
import org.lexgrid.valuesets.impl.LexEVSValueSetDefinitionServicesImpl;


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
 */


public class ValueSetSearchUtils
{
	private static Logger _logger = Logger.getLogger(ValueSetSearchUtils.class);

    public static final String SEARCH_BY_NAME_ONLY = "SEARCH_BY_NAME_ONLY";//1;
    public static final String SEARCH_BY_CODE_ONLY = "SEARCH_BY_CODE_ONLY";//2;
    public static final String SEARCH_BY_NAME_AND_CODE = "SEARCH_BY_NAME_AND_CODE";//3;

    private LexBIGService lbSvc = null;
    LexEVSResolvedValueSetService lrvs = null;

    private String serviceUrl = null;

    public ValueSetSearchUtils() {

	}

    public ValueSetSearchUtils(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

    public ValueSetSearchUtils(LexBIGService lbSvc) {
		this.lbSvc = lbSvc;
        this.lrvs = new LexEVSResolvedValueSetServiceImpl(lbSvc);
	}

    public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

    public void setLexBIGService(LexBIGService lbSvc) {
		this.lbSvc = lbSvc;
		this.lrvs = new LexEVSResolvedValueSetServiceImpl(lbSvc);
	}

	protected static void displayRef(int count, ResolvedConceptReference ref){
		if (ref.getEntityDescription() != null) {
		    System.out.println("(" + count + ") " + ref.getConceptCode() + " (" + ref.getEntityDescription().getContent()
		    + ") namespace: " + ref.getCodeNamespace() + ", coding scheme: " + ref.getCodingSchemeName() + ", version: " + ref.getCodingSchemeVersion());
		} else {
		    System.out.println("(" + count + ") " + ref.getConceptCode() + " (" + ""
		    + ") namespace: " + ref.getCodeNamespace() + ", coding scheme: " + ref.getCodingSchemeName() + ", version: " + ref.getCodingSchemeVersion());

		}
	}


    private String findBestContainsAlgorithm(String matchText) {
        if (matchText == null)
            return "nonLeadingWildcardLiteralSubString";
        matchText = matchText.trim();
        if (matchText.length() == 0)
            return "nonLeadingWildcardLiteralSubString"; // or null
        if (matchText.length() > 1)
            return "nonLeadingWildcardLiteralSubString";
        char ch = matchText.charAt(0);
        if (Character.isDigit(ch))
            return "literal";
        else if (Character.isLetter(ch))
            return "LuceneQuery";
        else
            return "literalContains";
    }


    public AbsoluteCodingSchemeVersionReferenceList getEntireAbsoluteCodingSchemeVersionReferenceList() {
        boolean includeInactive = false;
        AbsoluteCodingSchemeVersionReferenceList list = new AbsoluteCodingSchemeVersionReferenceList();
        try {
            // LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
            if (lbSvc == null) {
                _logger
                    .warn("WARNING: Unable to connect to instantiate LexBIGService ???");
                return null;
            }

            CodingSchemeRenderingList csrl = null;
            try {
                csrl = lbSvc.getSupportedCodingSchemes();

            } catch (LBInvocationException ex) {
                ex.printStackTrace();
                _logger.error("lbSvc.getSupportedCodingSchemes() FAILED..."
                    + ex.getCause());
                return null;
            }

            CodingSchemeRendering[] csrs = csrl.getCodingSchemeRendering();
            for (int i = 0; i < csrs.length; i++) {
                int j = i + 1;
                CodingSchemeRendering csr = csrs[i];
                CodingSchemeSummary css = csr.getCodingSchemeSummary();
                String formalname = css.getFormalName();
                Boolean isActive = null;
                /*
                if (csr == null) {
                    _logger.warn("\tcsr == null???");
                } else
                */

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
                    // nv_vec.add(value);
                    // csnv2codingSchemeNameMap.put(value, formalname);
                    // csnv2VersionMap.put(value, representsVersion);

                    // KLO 010810
                    CodingSchemeVersionOrTag vt =
                        new CodingSchemeVersionOrTag();
                    vt.setVersion(representsVersion);

                    try {
                        CodingScheme cs =
                            lbSvc.resolveCodingScheme(formalname, vt);

                        AbsoluteCodingSchemeVersionReference acsvr = new AbsoluteCodingSchemeVersionReference();
                        acsvr.setCodingSchemeURN(cs.getCodingSchemeURI());
                        acsvr.setCodingSchemeVersion(representsVersion);

                        list.addAbsoluteCodingSchemeVersionReference(acsvr);

                    } catch (Exception ex) {
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
            // return null;
        }
        return list;
    }

    public ResolvedConceptReferencesIterator searchByCode(
        String vsd_uri, String matchText, int maxToReturn) {

		if (matchText == null) return null;
        //String matchText0 = matchText;
        String matchAlgorithm0 = "exactMatch";
        //matchText0 = matchText0.trim();

        _logger.debug("searchByCode ..." + matchText);
        //long ms = System.currentTimeMillis(), delay = 0;
        long tnow = System.currentTimeMillis();
        long total_delay = 0;
        boolean debug_flag = false;

        boolean preprocess = true;
        //if (matchText == null || matchText.length() == 0) {
		if (matchText.length() == 0) {
            return null;
        }

        matchText = matchText.trim();
        CodedNodeSet cns = null;
        ResolvedConceptReferencesIterator iterator = null;
        try {
			LexEVSValueSetDefinitionServices vsd_service = getLexEVSValueSetDefinitionServices();
            if (vsd_service == null) {
                _logger.warn("vsd_service = null");
                return null;
            }
            java.lang.String valueSetDefinitionRevisionId = null;
            AbsoluteCodingSchemeVersionReferenceList csVersionList = null;

            String csVersionTag = null;

            ResolvedValueSetCodedNodeSet rvs_cns = null;
            try {
				rvs_cns = vsd_service.getCodedNodeSetForValueSetDefinition(new URI(vsd_uri),
					  valueSetDefinitionRevisionId, csVersionList, csVersionTag);
		    } catch (Exception ex) {
				ex.printStackTrace();
			}

            if (rvs_cns == null) {
                return null;
            }

            cns = rvs_cns.getCodedNodeSet();
            if (cns == null) {
                return null;
            }

            ConceptReferenceList codeList = new ConceptReferenceList();
            ConceptReference cr = new ConceptReference();
            cr.setConceptCode(matchText);
            codeList.addConceptReference(cr);

            cns = cns.restrictToCodes(codeList);

            SortOptionList sortOptions = null;
            LocalNameList filterOptions = null;
            LocalNameList propertyNames = null;
            boolean resolveObjects = false;
            CodedNodeSet.PropertyType[] propertyTypes = null;

            iterator = cns.resolve(sortOptions, filterOptions, propertyNames, propertyTypes, resolveObjects);

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

        total_delay = System.currentTimeMillis() - tnow;
        _logger.debug("Total search delay: (millisec.): " + total_delay);
        return iterator;

    }




    public ResolvedConceptReferencesIterator searchByName(
        String vsd_uri, String matchText, String matchAlgorithm, int maxToReturn) {

		if (matchText == null) return null;

        //String matchText0 = matchText;
        String matchAlgorithm0 = matchAlgorithm;
        //matchText0 = matchText0.trim();

        _logger.debug("searchByName ..." + matchText);

        //long ms = System.currentTimeMillis(), delay = 0;
        long tnow = System.currentTimeMillis();
        long total_delay = 0;
        boolean debug_flag = false;

        boolean preprocess = true;
        if (matchText.length() == 0) {
            return null;
        }

        matchText = matchText.trim();
        if (matchAlgorithm.compareToIgnoreCase("contains") == 0)
        {
            matchAlgorithm = findBestContainsAlgorithm(matchText);
        }

        CodedNodeSet cns = null;
        ResolvedConceptReferencesIterator iterator = null;
        try {
			LexEVSValueSetDefinitionServices vsd_service = getLexEVSValueSetDefinitionServices();
            if (vsd_service == null) {
                _logger.warn("vsd_service = null");
                return null;
            }
            java.lang.String valueSetDefinitionRevisionId = null;
            AbsoluteCodingSchemeVersionReferenceList csVersionList = null;
            /*
            Vector cs_ref_vec = DataUtils.getCodingSchemeReferencesInValueSetDefinition(vsd_uri, "PRODUCTION");
            if (cs_ref_vec != null) csVersionList = DataUtils.vector2CodingSchemeVersionReferenceList(cs_ref_vec);
            */

            String csVersionTag = null;

            ResolvedValueSetCodedNodeSet rvs_cns = null;
            try {
				rvs_cns = vsd_service.getCodedNodeSetForValueSetDefinition(new URI(vsd_uri),
                  valueSetDefinitionRevisionId, csVersionList, csVersionTag);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

            if (rvs_cns == null) {
                return null;
            }

            cns = rvs_cns.getCodedNodeSet();
            if (cns == null) {
                return null;
            }

            CodedNodeSet.SearchDesignationOption option = null;
            String language = null;
            cns = cns.restrictToAnonymous(CodedNodeSet.AnonymousOption.NON_ANONYMOUS_ONLY);
            cns = cns.restrictToMatchingDesignations(matchText, option, matchAlgorithm, language);
            SortOptionList sortOptions = null;
            LocalNameList filterOptions = null;
            LocalNameList propertyNames = null;
            CodedNodeSet.PropertyType[] propertyTypes = null;
            boolean resolveObjects = false;
            iterator = cns.resolve(sortOptions, filterOptions, propertyNames, propertyTypes, resolveObjects);

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

        total_delay = System.currentTimeMillis() - tnow;
        _logger.debug("Total search delay: (millisec.): " + total_delay);
        return iterator;

    }


    private CodedNodeSet.PropertyType[] getAllPropertyTypes() {
        CodedNodeSet.PropertyType[] propertyTypes =
            new CodedNodeSet.PropertyType[4];
        propertyTypes[0] = PropertyType.COMMENT;
        propertyTypes[1] = PropertyType.DEFINITION;
        propertyTypes[2] = PropertyType.GENERIC;
        propertyTypes[3] = PropertyType.PRESENTATION;
        return propertyTypes;
    }

    private CodedNodeSet.PropertyType[] getAllNonPresentationPropertyTypes() {
        CodedNodeSet.PropertyType[] propertyTypes =
            new CodedNodeSet.PropertyType[3];
        propertyTypes[0] = PropertyType.COMMENT;
        propertyTypes[1] = PropertyType.DEFINITION;
        propertyTypes[2] = PropertyType.GENERIC;
        return propertyTypes;
    }


    public ResolvedConceptReferencesIterator searchByProperties(
        String vsd_uri, String matchText, boolean excludeDesignation, String matchAlgorithm, int maxToReturn) {
		if (matchText == null) return null;

        //String matchText0 = matchText;
        String matchAlgorithm0 = matchAlgorithm;
        //matchText0 = matchText0.trim();

        _logger.debug("searchByProperties ..." + matchText);

        //long ms = System.currentTimeMillis(), delay = 0;
        long tnow = System.currentTimeMillis();
        long total_delay = 0;
        boolean debug_flag = false;

        boolean preprocess = true;
        if (matchText.length() == 0) {
            return null;
        }

        matchText = matchText.trim();
        if (matchAlgorithm.compareToIgnoreCase("contains") == 0)
        {
            matchAlgorithm = findBestContainsAlgorithm(matchText);
        }
        CodedNodeSet cns = null;
        ResolvedConceptReferencesIterator iterator = null;
        try {
			LexEVSValueSetDefinitionServices vsd_service = getLexEVSValueSetDefinitionServices();
            if (vsd_service == null) {
                _logger.warn("vsd_service = null");
                return null;
            }
            java.lang.String valueSetDefinitionRevisionId = null;
            AbsoluteCodingSchemeVersionReferenceList csVersionList = null;
            /*
            Vector cs_ref_vec = DataUtils.getCodingSchemeReferencesInValueSetDefinition(vsd_uri, "PRODUCTION");
            if (cs_ref_vec != null) csVersionList = DataUtils.vector2CodingSchemeVersionReferenceList(cs_ref_vec);
            */
            String csVersionTag = null;

            ResolvedValueSetCodedNodeSet rvs_cns = null;
            try {
				rvs_cns = vsd_service.getCodedNodeSetForValueSetDefinition(new URI(vsd_uri),
                  valueSetDefinitionRevisionId, csVersionList, csVersionTag);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

            //ResolvedValueSetCodedNodeSet rvs_cns = vsd_service.getCodedNodeSetForValueSetDefinition(new URI(vsd_uri),
            //      valueSetDefinitionRevisionId, csVersionList, csVersionTag);

            if (rvs_cns == null) {
                return null;
            }

            cns = rvs_cns.getCodedNodeSet();
            if (cns == null) {
                return null;
            }

            CodedNodeSet.SearchDesignationOption option = null;
            String language = null;
            cns = cns.restrictToAnonymous(CodedNodeSet.AnonymousOption.NON_ANONYMOUS_ONLY);
			LocalNameList propertyNames = new LocalNameList();
			CodedNodeSet.PropertyType[] propertyTypes = null;
			if (!excludeDesignation) {
				propertyTypes = getAllPropertyTypes();

			} else {
				propertyTypes = getAllNonPresentationPropertyTypes();
			}

			cns = cns.restrictToMatchingProperties(
						propertyNames, propertyTypes,
						matchText, matchAlgorithm, language);

            SortOptionList sortOptions = null;
            LocalNameList filterOptions = null;
            propertyNames = null;
            boolean resolveObjects = false;
            iterator = cns.resolve(sortOptions, filterOptions, propertyNames, propertyTypes, resolveObjects);

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

        total_delay = System.currentTimeMillis() - tnow;
        _logger.debug("Total search delay: (millisec.): " + total_delay);
        return iterator;

    }


    public Vector filterValueSetMetadata(Vector metadata_vec, String codingSchemeName) {
		if (codingSchemeName.compareTo("ALL") == 0) return metadata_vec;
		Vector w = new Vector();
        for (int i=0; i<metadata_vec.size(); i++) {
		    String vsd_str = (String) metadata_vec.elementAt(i);
		    Vector u = StringUtils.parseData(vsd_str);
		    String uri = (String) u.elementAt(1);
		    Vector cs_vec = getCodingSchemeURNsInValueSetDefinition(uri);

		    if (cs_vec.contains(codingSchemeName)) {
				w.add(vsd_str);
			}
		}
        return w;
	}


    public boolean containsConceptInCodingScheme(String vsd_uri, String codingSchemeName) {
		if (codingSchemeName.compareTo("ALL") == 0) return true;
	    Vector cs_vec = getCodingSchemeURNsInValueSetDefinition(vsd_uri);
        if (cs_vec.contains(codingSchemeName)) return true;
        return false;
	}


	public Vector getCodingSchemeVersionsByURN(String RVSCS_formalname) {
		//if (RVSCS_formalname == null) return null;
		//return DataUtils.getRVSCSVersionsByFormalName(RVSCS_formalname);
		return getResovedValueSetVersions(RVSCS_formalname);

	}


    // default to Name search
    public ResolvedConceptReferencesIterator searchResolvedValueSetCodingSchemes(String checked_vocabularies,
        String matchText, String matchAlgorithm) {
		return searchResolvedValueSetCodingSchemes(checked_vocabularies, matchText, SimpleSearchUtils.BY_NAME, matchAlgorithm);
	}

    public Vector getResovedValueSetVersions(String selected_vocabulary) {
		Vector v = new Vector();
		try {
			List<CodingScheme> schemes = lrvs.listAllResolvedValueSets();
			for (int i = 0; i < schemes.size(); i++) {
				CodingScheme cs = schemes.get(i);
				int j = i+1;
				String key = cs.getCodingSchemeURI();
				String cs_name = cs.getCodingSchemeName();
				String name = cs.getFormalName();
				String value = cs.getRepresentsVersion();

				if (key.compareTo(selected_vocabulary) == 0 || cs_name.compareTo(selected_vocabulary) == 0 || name.compareTo(selected_vocabulary) == 0) {
                    v.add(value);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return v;
    }

    public ResolvedConceptReferencesIterator searchResolvedValueSetCodingSchemes(String checked_vocabularies,
        String matchText, String searchOption, String matchAlgorithm) {
		if (searchOption.compareToIgnoreCase("names") == 0 || searchOption.compareToIgnoreCase("name") == 0) {
			return searchResolvedValueSetCodingSchemes(checked_vocabularies, matchText, SimpleSearchUtils.BY_NAME, matchAlgorithm);
		} else if (searchOption.compareToIgnoreCase("codes") == 0 || searchOption.compareToIgnoreCase("code") == 0) {
			return searchResolvedValueSetCodingSchemes(checked_vocabularies, matchText, SimpleSearchUtils.BY_CODE, matchAlgorithm);
		}
		return null;
	}


    public HashMap getRVSCSName2VersionHashMap() {
		HashMap hmap = new HashMap();
		try {
			  List<CodingScheme> schemes = lrvs.listAllResolvedValueSets();
			  for (int i = 0; i < schemes.size(); i++) {
					CodingScheme cs = schemes.get(i);
					String uri = cs.getCodingSchemeURI();
					String name = cs.getCodingSchemeName();
					hmap.put(uri, cs.getRepresentsVersion());
					hmap.put(name, cs.getRepresentsVersion());
			  }
		} catch (Exception ex) {
			  ex.printStackTrace();
		}
		return hmap;
	}



    public ResolvedConceptReferencesIterator searchResolvedValueSetCodingSchemes(String checked_vocabularies,
        String matchText, int searchOption, String matchAlgorithm) {
		ResolvedConceptReferencesIterator iterator = null;

        long ms = System.currentTimeMillis();

		if (checked_vocabularies == null) return null;
		Vector selected_vocabularies = StringUtils.parseData(checked_vocabularies, ",");
		HashMap hmap = getRVSCSName2VersionHashMap();

		// find versions
		Vector schemes = new Vector();
		Vector versions = new Vector();

		for (int i=0; i<selected_vocabularies.size(); i++) {
			int k = i+1;
			String selected_vocabulary = (String) selected_vocabularies.elementAt(i);
			//Vector u = getCodingSchemeVersionsByURN(selected_vocabulary);
			/*
			Vector u = getResovedValueSetVersions(selected_vocabulary);

			if (u != null && u.size() > 0) {
				String version = (String) u.elementAt(0);

				schemes.add(selected_vocabulary);
				versions.add(version);
		    } else {

				schemes.add(selected_vocabulary);
				versions.add(null);
			}
			*/
			schemes.add(selected_vocabulary);
			versions.add((String) hmap.get(selected_vocabulary));
		}
        if (searchOption == SimpleSearchUtils.BY_NAME) {
			if (SimpleSearchUtils.isSimpleSearchSupported(matchAlgorithm, SimpleSearchUtils.NAMES)) {
				try {
					iterator = new SimpleSearchUtils(lbSvc).search(schemes, versions, matchText, searchOption, matchAlgorithm);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else {
				String source = "ALL";
				boolean ranking = false;
				int maxToReturn = -1;
				ResolvedConceptReferencesIteratorWrapper wrapper = new SearchUtils(lbSvc).searchByNameOrCode(
						schemes, versions, matchText,
						source, matchAlgorithm, ranking, maxToReturn, SEARCH_BY_NAME_ONLY);
				if (wrapper != null) {
					 iterator = wrapper.getIterator();
				}

			}

		} else if (searchOption == SimpleSearchUtils.BY_CODE) {
			if (SimpleSearchUtils.isSimpleSearchSupported(matchAlgorithm, SimpleSearchUtils.CODES)) {
				try {
					iterator = new SimpleSearchUtils(lbSvc).search(schemes, versions, matchText, searchOption, matchAlgorithm);

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else {

				String source = "ALL";
				boolean ranking = false;
				int maxToReturn = -1;


					ResolvedConceptReferencesIteratorWrapper wrapper = new SearchUtils(lbSvc).searchByNameOrCode(
							schemes, versions, matchText,
							source, matchAlgorithm, ranking, maxToReturn, SEARCH_BY_CODE_ONLY);
					if (wrapper != null) {
						 iterator = wrapper.getIterator();
					}
			}
		}
		return iterator;
	}


	public LexEVSValueSetDefinitionServices getLexEVSValueSetDefinitionServices() {
		return getLexEVSValueSetDefinitionServices(serviceUrl);
	}


    public LexEVSValueSetDefinitionServices getLexEVSValueSetDefinitionServices(String serviceUrl) {
		if (serviceUrl == null || serviceUrl.compareTo("") == 0 || serviceUrl.compareToIgnoreCase("null") == 0) {
			return LexEVSValueSetDefinitionServicesImpl.defaultInstance();
		}
		try {
			LexEVSDistributed distributed =
				(LexEVSDistributed)
				ApplicationServiceProvider.getApplicationServiceFromUrl(serviceUrl, "EvsServiceInfo");

			LexEVSValueSetDefinitionServices vds = distributed.getLexEVSValueSetDefinitionServices();
			return vds;
		} catch (Exception e) {
            e.printStackTrace();
        }
        return null;
	}


   public Vector getCodingSchemeURNsInValueSetDefinition(String uri) {
	    Vector v = new Vector();
		try {
			java.net.URI valueSetDefinitionURI = new URI(uri);
			LexEVSValueSetDefinitionServices vsd_service = getLexEVSValueSetDefinitionServices();
			if (vsd_service == null) {
				return null;
			}

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
		return SortUtils.quickSort(v);
   }

   public java.util.List<java.lang.String> listValueSetDefinitionURIs() {
		try {
			LexEVSValueSetDefinitionServices vsd_service = getLexEVSValueSetDefinitionServices();
			if (vsd_service == null) {
				return null;
			}
			return vsd_service.listValueSetDefinitionURIs();

		} catch (Exception ex) {
			ex.printStackTrace();

		}
		return null;
    }

    public static void main(String [] args) {
		try {

			LexBIGService lbSvc = LexBIGServiceImpl.defaultInstance();
			ValueSetSearchUtils test = new ValueSetSearchUtils(lbSvc);

			String vsd_uri = "http://evs.nci.nih.gov/valueset/C54453";
			String matchText = "red";
			String matchAlgorithm = "exactMatch";
			int maxToReturn = 100;


			ResolvedConceptReferencesIterator iterator = test.searchByName(
			        vsd_uri, matchText, matchAlgorithm, maxToReturn);

            if (iterator != null) {
				try {
					int numberRemaining = iterator.numberRemaining();
					while (iterator.hasNext()) {
						ResolvedConceptReference rcr = (ResolvedConceptReference) iterator.next();
						if (rcr.getEntityDescription() != null) {
							System.out.println(rcr.getEntityDescription().getContent() + "(" + rcr.getConceptCode() + ")");
						} else {
							System.out.println("(" + rcr.getConceptCode() + ")");
						}
					}

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else {
				System.out.println("searchByName returns NULL???");
			}

            matchText = "C48326";
			iterator = test.searchByCode(
			        vsd_uri, matchText, maxToReturn);

            if (iterator != null) {
				try {
					int numberRemaining = iterator.numberRemaining();
					//System.out.println("Number of matches: " + numberRemaining);

					while (iterator.hasNext()) {
						ResolvedConceptReference rcr = (ResolvedConceptReference) iterator.next();
						if (rcr.getEntityDescription() != null) {
							System.out.println(rcr.getEntityDescription().getContent() + "(" + rcr.getConceptCode() + ")");
						} else {
							System.out.println("(" + rcr.getConceptCode() + ")");
						}
					}


				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else {
				System.out.println("searchByName returns NULL???");
			}

			String checked_vocabularies = vsd_uri;
			iterator = test.searchResolvedValueSetCodingSchemes(checked_vocabularies,
				matchText, 1, matchAlgorithm);

            if (iterator != null) {
				try {
					int numberRemaining = iterator.numberRemaining();
					System.out.println("Number of matches: " + numberRemaining);

					while (iterator.hasNext()) {
						ResolvedConceptReference rcr = (ResolvedConceptReference) iterator.next();
						if (rcr.getEntityDescription() != null) {
							System.out.println(rcr.getEntityDescription().getContent() + "(" + rcr.getConceptCode() + ")");
						} else {
							System.out.println("(" + rcr.getConceptCode() + ")");
						}
					}


				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else {
				System.out.println("searchByName returns NULL???");
			}

		} catch (Exception ex) {

		}

	}

}

