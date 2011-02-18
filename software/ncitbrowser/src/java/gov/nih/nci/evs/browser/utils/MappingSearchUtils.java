package gov.nih.nci.evs.browser.utils;

import java.util.*;
import java.sql.*;

import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.*;
import org.LexGrid.LexBIG.DataModel.Collections.*;
import org.LexGrid.LexBIG.DataModel.Core.*;
import org.LexGrid.LexBIG.Exceptions.*;
import org.LexGrid.LexBIG.Impl.*;
import org.LexGrid.LexBIG.LexBIGService.*;
import org.LexGrid.concepts.*;
import org.LexGrid.LexBIG.Utility.Iterators.*;
import org.LexGrid.codingSchemes.*;
import org.LexGrid.LexBIG.Utility.*;
import org.LexGrid.LexBIG.DataModel.Core.types.*;
import org.LexGrid.LexBIG.Extensions.Generic.*;
import org.LexGrid.naming.*;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.*;
import org.LexGrid.commonTypes.*;

import gov.nih.nci.evs.browser.properties.*;
import gov.nih.nci.evs.browser.common.*;

import org.apache.commons.codec.language.*;
import org.apache.log4j.*;
import org.LexGrid.relations.Relations;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.Mapping.SearchContext;

import org.LexGrid.LexBIG.Extensions.Generic.*;
import org.LexGrid.LexBIG.Extensions.Generic.SupplementExtension;

import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.Direction;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.MappingSortOption;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.MappingSortOptionName;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.QualifierSortOption;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.Mapping;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.SearchDesignationOption;


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

public class MappingSearchUtils {
    private static Logger _logger = Logger.getLogger(SearchUtils.class);

    public MappingSearchUtils() {

    }


    public static String getMappingRelationsContainerName(String scheme, String version) {
		CodingSchemeVersionOrTag versionOrTag =
			new CodingSchemeVersionOrTag();
		if (version != null) {
			versionOrTag.setVersion(version);
		}

		String relationsContainerName = null;
        try {
			LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
			CodingScheme cs = lbSvc.resolveCodingScheme(scheme, versionOrTag);
			if (cs == null) return null;

			java.util.Enumeration<? extends Relations> relations = cs.enumerateRelations();
			while (relations.hasMoreElements()) {
				Relations relation = (Relations) relations.nextElement();
				Boolean isMapping = relation.getIsMapping();
				System.out.println("isMapping: " + isMapping);
				if (isMapping != null && isMapping.equals(Boolean.TRUE)) {
 					relationsContainerName = relation.getContainerName();
					break;
				}
			}

			if (relationsContainerName == null) {
				System.out.println("WARNING: Mapping container not found in " + scheme);
				return null;
			} else {
				System.out.println("relationsContainerName " + relationsContainerName);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return relationsContainerName;
	}



    private CodedNodeSet.PropertyType[] getAllNonPresentationPropertyTypes() {
        CodedNodeSet.PropertyType[] propertyTypes =
            new CodedNodeSet.PropertyType[3];
        propertyTypes[0] = PropertyType.COMMENT;
        propertyTypes[1] = PropertyType.DEFINITION;
        propertyTypes[2] = PropertyType.GENERIC;
        return propertyTypes;
    }



    public ResolvedConceptReferencesIteratorWrapper searchByCode(
        String scheme, String version, String matchText,
        String matchAlgorithm, int maxToReturn) {
		Vector schemes = new Vector();
		schemes.add(scheme);
		Vector versions = new Vector();
		versions.add(version);
		return searchByCode(schemes, versions, matchText, matchAlgorithm, maxToReturn);
	}


    public ResolvedConceptReferencesIteratorWrapper searchByCode(
        Vector schemes, Vector versions, String matchText,
        String matchAlgorithm, int maxToReturn) {

System.out.println("==============================  MappingSearchUtils searchByCode");


		if (matchText == null || matchText.trim().length() == 0)
			return null;

		matchText = matchText.trim();
		_logger.debug("searchByCode ... " + matchText);

		if (matchAlgorithm.compareToIgnoreCase("contains") == 0)
		{
		   matchAlgorithm = new SearchUtils().findBestContainsAlgorithm(matchText);
		}

		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
		MappingExtension mappingExtension = null;
		try {
			mappingExtension = (MappingExtension)lbSvc.getGenericExtension("MappingExtension");
		} catch (Exception ex) {
            ex.printStackTrace();
            return null;
		}

        ResolvedConceptReferencesIterator itr = null;
        int lcv = 0;
        String scheme = null;
        String version = null;
        int numberRemaining = -1;
        while (itr == null && numberRemaining == -1 && lcv < schemes.size()) {
            scheme = (String) schemes.elementAt(lcv);
            version = (String) versions.elementAt(lcv);

System.out.println(scheme + " (version: " +  version);

			String containerName = getMappingRelationsContainerName(scheme, version);
System.out.println("\tcontainer name: " +  containerName);

			if (containerName != null) {
				try {
					Mapping mapping =
						mappingExtension.getMapping(scheme, null, containerName);

					if (mapping != null) {

						ConceptReferenceList codeList = new ConceptReferenceList();
						ConceptReference ref = new ConceptReference();
						ref.setConceptCode(matchText);
 						codeList.addConceptReference(ref);

                        mapping = mapping.restrictToCodes(codeList, SearchContext.SOURCE_OR_TARGET_CODES);
						itr = mapping.resolveMapping();
						if (itr != null) {
							try {
								numberRemaining = itr.numberRemaining();
								System.out.println("\tsearchByCode matches: " + numberRemaining);

							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
		    }
		    lcv++;
		}
		if (itr != null) {
			ResolvedConceptReferencesIteratorWrapper wrapper = new ResolvedConceptReferencesIteratorWrapper(itr);
			wrapper.setCodingSchemeName(scheme);
			wrapper.setCodingSchemeVersion(version);
			return wrapper;
		}
		return null;
    }



    public ResolvedConceptReferencesIteratorWrapper searchByName(
        String scheme, String version, String matchText,
        String matchAlgorithm, int maxToReturn) {
		Vector schemes = new Vector();
		schemes.add(scheme);
		Vector versions = new Vector();
		versions.add(version);
		return searchByName(schemes, versions, matchText, matchAlgorithm, maxToReturn);
	}


    public ResolvedConceptReferencesIteratorWrapper searchByName(
        Vector schemes, Vector versions, String matchText,
        String matchAlgorithm, int maxToReturn) {

		if (matchText == null || matchText.trim().length() == 0)
			return null;

		matchText = matchText.trim();
		_logger.debug("searchByName ... " + matchText);

		if (matchAlgorithm.compareToIgnoreCase("contains") == 0)
		{
		   matchAlgorithm = new SearchUtils().findBestContainsAlgorithm(matchText);
		}

		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
		MappingExtension mappingExtension = null;
		try {
			mappingExtension = (MappingExtension)lbSvc.getGenericExtension("MappingExtension");
		} catch (Exception ex) {
            ex.printStackTrace();
            return null;
		}

        ResolvedConceptReferencesIterator itr = null;
        int lcv = 0;
        String scheme = null;
        String version = null;
        int numberRemaining = -1;
        while (itr == null && numberRemaining == -1 && lcv < schemes.size()) {
            scheme = (String) schemes.elementAt(lcv);
            version = (String) versions.elementAt(lcv);
			String containerName = getMappingRelationsContainerName(scheme, version);
			if (containerName != null) {
				try {
					Mapping mapping =
						mappingExtension.getMapping(scheme, null, containerName);

					if (mapping != null) {
						mapping = mapping.restrictToMatchingDesignations(
									matchText, SearchDesignationOption.ALL, matchAlgorithm, null, SearchContext.SOURCE_OR_TARGET_CODES
);

							//Finally, resolve the Mapping.
						itr = mapping.resolveMapping();
						try {
							numberRemaining = itr.numberRemaining();
							System.out.println("Number of matches: " + numberRemaining);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
					//return null;
				}
		    }
		    lcv++;
		}
		if (itr != null) {
			ResolvedConceptReferencesIteratorWrapper wrapper = new ResolvedConceptReferencesIteratorWrapper(itr);
			wrapper.setCodingSchemeName(scheme);
			wrapper.setCodingSchemeVersion(version);
			return wrapper;
		}
		return null;
    }

    public ResolvedConceptReferencesIteratorWrapper searchByProperties(
        String scheme, String version, String matchText,
        String matchAlgorithm, int maxToReturn) {

System.out.println("searchByProperties scheme: " + scheme);
System.out.println("searchByProperties version: " + version);


		Vector schemes = new Vector();
		schemes.add(scheme);
		Vector versions = new Vector();
		versions.add(version);
		return searchByProperties(schemes, versions, matchText, matchAlgorithm, maxToReturn);
	}


    public ResolvedConceptReferencesIteratorWrapper searchByProperties(
        Vector schemes, Vector versions, String matchText,
        String matchAlgorithm, int maxToReturn) {

		if (matchText == null || matchText.trim().length() == 0)
			return null;

		matchText = matchText.trim();
		_logger.debug("searchByName ... " + matchText);

		if (matchAlgorithm.compareToIgnoreCase("contains") == 0)
		{
		   matchAlgorithm = new SearchUtils().findBestContainsAlgorithm(matchText);
		}

        CodedNodeSet.PropertyType[] propertyTypes = null;
        LocalNameList propertyNames = null;
        LocalNameList sourceList = null;
        propertyTypes = getAllNonPresentationPropertyTypes();

        LocalNameList contextList = null;
        NameAndValueList qualifierList = null;
        String language = null;
        // to be modified
        SearchContext searchContext = SearchContext.SOURCE_OR_TARGET_CODES
;

		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
		MappingExtension mappingExtension = null;
		try {
			mappingExtension = (MappingExtension)lbSvc.getGenericExtension("MappingExtension");
		} catch (Exception ex) {
            ex.printStackTrace();
            return null;
		}

        ResolvedConceptReferencesIterator itr = null;
        int lcv = 0;
        String scheme = null;
        String version = null;

        System.out.println("schemes.size(): " + schemes.size() + " lcv: " + lcv);
        int numberRemaining = -1;
        while (itr == null && numberRemaining == -1 && lcv < schemes.size()) {

            scheme = (String) schemes.elementAt(lcv);
            version = (String) versions.elementAt(lcv);

			String containerName = getMappingRelationsContainerName(scheme, version);
			if (containerName != null) {
				try {
					Mapping mapping =
						mappingExtension.getMapping(scheme, null, containerName);

					if (mapping != null) {
						mapping = mapping.restrictToMatchingProperties(
							   propertyNames,
							   propertyTypes,
							   sourceList,
							   contextList,
							   qualifierList,
							   matchAlgorithm,
							   language,
							   null,
							   searchContext);

							//Finally, resolve the Mapping.
						itr = mapping.resolveMapping();
						try {
							numberRemaining = itr.numberRemaining();
							System.out.println("Number of matches: " + numberRemaining);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
					//return null;
				}
		    }
		    lcv++;
		}
		if (itr != null) {
			ResolvedConceptReferencesIteratorWrapper wrapper = new ResolvedConceptReferencesIteratorWrapper(itr);
			wrapper.setCodingSchemeName(scheme);
			wrapper.setCodingSchemeVersion(version);
			return wrapper;
		}
		return null;
    }

    public LocalNameList getSupportedAssociationNames(LexBIGService lbSvc, String scheme,
        String version, String containerName) {
        CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
        if (version != null)
            csvt.setVersion(version);

        LocalNameList list = new LocalNameList();
        try {
            CodingScheme cs = lbSvc.resolveCodingScheme(scheme, csvt);
            Relations[] relations = cs.getRelations();
            for (int i = 0; i < relations.length; i++) {
                Relations relation = relations[i];

                _logger.debug("** getSupportedRoleNames containerName: "
                    + relation.getContainerName());

                if (relation.getContainerName().compareToIgnoreCase(containerName) == 0) {
                    org.LexGrid.relations.AssociationPredicate[] asso_array =
                        relation.getAssociationPredicate();
                    for (int j = 0; j < asso_array.length; j++) {
                        org.LexGrid.relations.AssociationPredicate association =
                            (org.LexGrid.relations.AssociationPredicate) asso_array[j];
                        // list.add(association.getAssociationName());
                        // KLO, 092209
                        //list.add(association.getForwardName());
                        list.addEntry(association.getAssociationName());
                    }
                }
            }
        } catch (Exception ex) {

        }
        return list;
    }


    public ResolvedConceptReferencesIteratorWrapper searchByRelationships(
        String scheme, String version, String matchText,
        String matchAlgorithm, int maxToReturn) {
		Vector schemes = new Vector();
		schemes.add(scheme);
		Vector versions = new Vector();
		versions.add(version);
		return searchByRelationships(schemes, versions, matchText, matchAlgorithm, maxToReturn);
	}


    public ResolvedConceptReferencesIteratorWrapper searchByRelationships(
        Vector schemes, Vector versions, String matchText,
        String matchAlgorithm, int maxToReturn) {

		if (matchText == null || matchText.trim().length() == 0)
			return null;

		matchText = matchText.trim();
		_logger.debug("searchByName ... " + matchText);

		if (matchAlgorithm.compareToIgnoreCase("contains") == 0)
		{
		   matchAlgorithm = new SearchUtils().findBestContainsAlgorithm(matchText);
		}

		SearchDesignationOption option = SearchDesignationOption.ALL;
		String language = null;


        CodedNodeSet.PropertyType[] propertyTypes = null;
        LocalNameList propertyNames = null;
        LocalNameList sourceList = null;
        propertyTypes = getAllNonPresentationPropertyTypes();

        LocalNameList contextList = null;
        NameAndValueList qualifierList = null;

		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
		MappingExtension mappingExtension = null;
		try {
			mappingExtension = (MappingExtension)lbSvc.getGenericExtension("MappingExtension");
		} catch (Exception ex) {
            ex.printStackTrace();
            return null;
		}

        ResolvedConceptReferencesIterator itr = null;
        int lcv = 0;
        String scheme = null;
        String version = null;

        System.out.println("schemes.size(): " + schemes.size() + " lcv: " + lcv);
        int numberRemaining = -1;
        while (itr == null && numberRemaining == -1 && lcv < schemes.size()) {

            scheme = (String) schemes.elementAt(lcv);
            version = (String) versions.elementAt(lcv);

			String containerName = getMappingRelationsContainerName(scheme, version);
			if (containerName != null) {

				LocalNameList relationshipList = getSupportedAssociationNames(lbSvc, scheme, version, containerName);

				try {
					Mapping mapping =
						mappingExtension.getMapping(scheme, null, containerName);

					if (mapping != null) {

					    mapping = mapping.restrictToRelationship(
							 matchText,
							 option,
							 matchAlgorithm,
							 language,
							 relationshipList);

							//Finally, resolve the Mapping.
						itr = mapping.resolveMapping();
						try {
							numberRemaining = itr.numberRemaining();
							System.out.println("Number of matches: " + numberRemaining);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
					//return null;
				}
		    }
		    lcv++;
		}
		if (itr != null) {
			ResolvedConceptReferencesIteratorWrapper wrapper = new ResolvedConceptReferencesIteratorWrapper(itr);
			wrapper.setCodingSchemeName(scheme);
			wrapper.setCodingSchemeVersion(version);
			return wrapper;
		}
		return null;
    }




     public static ResolvedConceptReferencesIterator getRestrictedMappingDataIterator(String scheme, String version,
        List<MappingSortOption> sortOptionList, ResolvedConceptReferencesIterator searchResultsIterator) {
        return getRestrictedMappingDataIterator(scheme, version,
        sortOptionList, searchResultsIterator, SearchContext.SOURCE_OR_TARGET_CODES);

    }



    public static ResolvedConceptReferencesIterator getRestrictedMappingDataIterator(String scheme, String version,
        List<MappingSortOption> sortOptionList, ResolvedConceptReferencesIterator searchResultsIterator, SearchContext context) {

System.out.println("(***********) getRestrictedMappingDataIterator ...");
if (searchResultsIterator == null) return null;

		try {
			int numRemaining = searchResultsIterator.numberRemaining();
			System.out.println("(***********) searchResultsIterator passing number of matches: " + numRemaining);
		} catch (Exception e) {
			System.out.println("searchResultsIterator.numberRemaining() throws exception???");
			return null;
		}


		CodingSchemeVersionOrTag versionOrTag =
			new CodingSchemeVersionOrTag();
		if (version != null) {
			versionOrTag.setVersion(version);
		}
		String relationsContainerName = null;

        LexBIGService distributed = RemoteServerUtil.createLexBIGService();
        try {
			CodingScheme cs = distributed.resolveCodingScheme(scheme, versionOrTag);
			if (cs == null) return null;

			java.util.Enumeration<? extends Relations> relations = cs.enumerateRelations();
			while (relations.hasMoreElements()) {
				Relations relation = (Relations) relations.nextElement();
				Boolean isMapping = relation.getIsMapping();
				System.out.println("isMapping: " + isMapping);
				if (isMapping != null && isMapping.equals(Boolean.TRUE)) {
 					relationsContainerName = relation.getContainerName();
					break;
				}
			}

			if (relationsContainerName == null) {
				System.out.println("WARNING: Mapping container not found in " + scheme);
				return null;
			} else {
				System.out.println("relationsContainerName " + relationsContainerName);
			}

			MappingExtension mappingExtension = (MappingExtension)
				distributed.getGenericExtension("MappingExtension");

		    Mapping mapping =
			    mappingExtension.getMapping(scheme, versionOrTag, relationsContainerName);

            //ConceptReferenceList codeList (to be derived based on ResolvedConceptReferencesIterator searchResultsIterator)
            ConceptReferenceList codeList = new ConceptReferenceList();

System.out.println("getRestrictedMappingDataIterator Step 5 while loop -- retrieving refs");

			if (searchResultsIterator != null) {
				int lcv = 0;
				while(searchResultsIterator.hasNext()){
					ResolvedConceptReference[] refs = searchResultsIterator.next(100).getResolvedConceptReference();
					for(ResolvedConceptReference ref : refs){
						lcv++;
						System.out.println("(" + lcv + ") " + ref.getEntityDescription().getContent() + "(" + ref.getCode() + ")");
						codeList.addConceptReference((ConceptReference) ref);
					}
				}
			} else {
				System.out.println("resolved_value_set.jsp ResolvedConceptReferencesIterator == NULL???");
			}

            mapping = mapping.restrictToCodes(codeList, context);
            ResolvedConceptReferencesIterator itr = mapping.resolveMapping(sortOptionList);
			return itr;

		} catch (Exception ex) {
			//ex.printStackTrace();
			System.out.println("getRestrictedMappingDataIterator throws exceptions???");

		}
		return null;
	}


}