/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

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

import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.PropertyType;

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

import static gov.nih.nci.evs.browser.common.Constants.*;
import gov.nih.nci.evs.browser.bean.*;


/**
 * 
 */

/**
 * @author EVS Team
 * @version 1.0
 *
 *          Modification history Initial implementation kim.ong@ngc.com
 *
 */

public class MappingSearchUtils {
    private static Logger _logger = Logger.getLogger(MappingSearchUtils.class);

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
				if (isMapping != null && isMapping.equals(Boolean.TRUE)) {
 					relationsContainerName = relation.getContainerName();
					break;
				}
			}

			if (relationsContainerName == null) {
				//System.out.println("WARNING: Mapping container not found in " + scheme);
				return null;
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
		return searchByCode(schemes, versions, matchText, matchAlgorithm, SearchContext.SOURCE_OR_TARGET_CODES, maxToReturn);
	}


    public ResolvedConceptReferencesIteratorWrapper searchByCode(
        Vector schemes, Vector versions, String matchText,
        String matchAlgorithm, int maxToReturn) {



        return searchByCode(
         schemes, versions, matchText,
         matchAlgorithm, SearchContext.SOURCE_OR_TARGET_CODES, maxToReturn);
    }


    public ResolvedConceptReferencesIteratorWrapper searchByCode(
        String scheme, String version, String matchText,
        String matchAlgorithm, SearchContext searchContext, int maxToReturn) {
		Vector schemes = new Vector();
		schemes.add(scheme);
		Vector versions = new Vector();
		versions.add(version);
		return searchByCode(schemes, versions, matchText, matchAlgorithm, searchContext, maxToReturn);
	}


    public ResolvedConceptReferencesIteratorWrapper searchByCode(
        Vector schemes, Vector versions, String matchText,
        String matchAlgorithm, SearchContext searchContext, int maxToReturn) {
		if (matchText == null || matchText.trim().length() == 0)
			return null;

		matchText = matchText.trim();
		_logger.debug("searchByCode ... " + matchText);

/*
		if (matchAlgorithm.compareToIgnoreCase("contains") == 0)
		{
		   matchAlgorithm = new SearchUtils().findBestContainsAlgorithm(matchText);
		}
*/
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

		CodingSchemeVersionOrTag versionOrTag =
			new CodingSchemeVersionOrTag();
		if (version != null) {
			versionOrTag.setVersion(version);
		}

					Mapping mapping =
						mappingExtension.getMapping(scheme, versionOrTag, containerName);

					if (mapping != null) {

						ConceptReferenceList codeList = new ConceptReferenceList();
						ConceptReference ref = new ConceptReference();
						ref.setConceptCode(matchText);
 						codeList.addConceptReference(ref);
                        mapping = mapping.restrictToCodes(codeList, searchContext);
						itr = mapping.resolveMapping();
						if (itr != null) {
							try {
								numberRemaining = itr.numberRemaining();
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

		CodingSchemeVersionOrTag versionOrTag =
			new CodingSchemeVersionOrTag();
		if (version != null) {
			versionOrTag.setVersion(version);
		}
					Mapping mapping =
						mappingExtension.getMapping(scheme, versionOrTag, containerName);

					if (mapping != null) {
						mapping = mapping.restrictToMatchingDesignations(
									matchText, SearchDesignationOption.ALL, matchAlgorithm, null, SearchContext.SOURCE_OR_TARGET_CODES
);

							//Finally, resolve the Mapping.
						itr = mapping.resolveMapping();
						try {
							numberRemaining = itr.numberRemaining();
							//System.out.println("Number of matches: " + numberRemaining);
						} catch (Exception ex) {
							ex.printStackTrace();
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

    public ResolvedConceptReferencesIteratorWrapper searchByProperties(
        String scheme, String version, String matchText,
        String matchAlgorithm, int maxToReturn) {

		Vector schemes = new Vector();
		schemes.add(scheme);
		Vector versions = new Vector();
		versions.add(version);
		return searchByProperties(schemes, versions, matchText, matchAlgorithm, maxToReturn);
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
        //SearchContext searchContext = SearchContext.SOURCE_OR_TARGET_CODES
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

        int numberRemaining = -1;
        while (itr == null && numberRemaining == -1 && lcv < schemes.size()) {

            scheme = (String) schemes.elementAt(lcv);
            version = (String) versions.elementAt(lcv);

			String containerName = getMappingRelationsContainerName(scheme, version);
			if (containerName != null) {
				try {

		CodingSchemeVersionOrTag versionOrTag =
			new CodingSchemeVersionOrTag();
		if (version != null) {
			versionOrTag.setVersion(version);
		}

					Mapping mapping =
						mappingExtension.getMapping(scheme, versionOrTag, containerName);

					if (mapping != null) {

					mapping = mapping.restrictToMatchingProperties(
		                                propertyNames,
										propertyTypes,
										sourceList,
										contextList,
										qualifierList,
										matchText,
										matchAlgorithm,
										null,
										SearchContext.SOURCE_OR_TARGET_CODES);


							//Finally, resolve the Mapping.
						itr = mapping.resolveMapping();
						try {
							numberRemaining = itr.numberRemaining();
							//System.out.println("Number of matches: " + numberRemaining);
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


        //CodedNodeSet.PropertyType[] propertyTypes = null;
        LocalNameList propertyNames = null;
        LocalNameList sourceList = null;
        //propertyTypes = getAllNonPresentationPropertyTypes();

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

        int numberRemaining = -1;
        while (itr == null && numberRemaining == -1 && lcv < schemes.size()) {

            scheme = (String) schemes.elementAt(lcv);
            version = (String) versions.elementAt(lcv);

			String containerName = getMappingRelationsContainerName(scheme, version);
			if (containerName != null) {

				LocalNameList relationshipList = getSupportedAssociationNames(lbSvc, scheme, version, containerName);

				try {

		CodingSchemeVersionOrTag versionOrTag =
			new CodingSchemeVersionOrTag();
		if (version != null) {
			versionOrTag.setVersion(version);
		}
					Mapping mapping =
						mappingExtension.getMapping(scheme, versionOrTag, containerName);

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
							//System.out.println("Number of matches: " + numberRemaining);
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

if (searchResultsIterator == null) return null;
/*
		try {
			int numRemaining = searchResultsIterator.numberRemaining();
			System.out.println("searchResultsIterator passing number of matches: " + numRemaining);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
*/

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
				if (isMapping != null && isMapping.equals(Boolean.TRUE)) {
 					relationsContainerName = relation.getContainerName();
					break;
				}
			}

			if (relationsContainerName == null) {
				//System.out.println("WARNING: Mapping container not found in " + scheme);
				return null;
			}

			MappingExtension mappingExtension = (MappingExtension)
				distributed.getGenericExtension("MappingExtension");

		    Mapping mapping =
			    mappingExtension.getMapping(scheme, versionOrTag, relationsContainerName);

            //ConceptReferenceList codeList (to be derived based on ResolvedConceptReferencesIterator searchResultsIterator)
            ConceptReferenceList codeList = new ConceptReferenceList();



				int lcv = 0;
				while(searchResultsIterator.hasNext()){
					ResolvedConceptReference[] refs = searchResultsIterator.next(100).getResolvedConceptReference();
					for(ResolvedConceptReference ref : refs){
						lcv++;
						//System.out.println("(" + lcv + ") " + ref.getEntityDescription().getContent() + "(" + ref.getCode() + ")");
						codeList.addConceptReference((ConceptReference) ref);
					}
				}

            mapping = mapping.restrictToCodes(codeList, context);
            ResolvedConceptReferencesIterator itr = mapping.resolveMapping(sortOptionList);
			return itr;

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

    public List getMappingRelationship(
        String scheme, String version, String code, int direction) {

		SearchContext searchContext = SearchContext.SOURCE_OR_TARGET_CODES;
		if (direction == 1) searchContext = SearchContext.SOURCE_CODES;
        else if (direction == -1) searchContext = SearchContext.TARGET_CODES;

        ResolvedConceptReferencesIteratorWrapper wrapper = searchByCode(
         scheme, version, code, "exactMatch", searchContext, -1);

        if (wrapper == null) return null;
        ResolvedConceptReferencesIterator iterator = wrapper.getIterator();
        if (iterator == null) return null;

		int numberRemaining = 0;
		try {
			numberRemaining = iterator.numberRemaining();
			if (numberRemaining == 0) {
                return null;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

/*
		MappingIteratorBean mappingIteratorBean = new MappingIteratorBean(
		iterator,
		numberRemaining, // number remaining
		0,    // istart
		50,   // iend,
		//numberRemaining, // size,
		0,    // pageNumber,
		1);   // numberPages

		mappingIteratorBean.initialize(
		iterator,
		numberRemaining, // number remaining
		0,    // istart
		50,   // iend,
		numberRemaining, // size,
		0,    // pageNumber,
		1);   // numberPages
*/
        //mappingIteratorBean.initialize();


 		MappingIteratorBean mappingIteratorBean = new MappingIteratorBean(iterator);
		List list = mappingIteratorBean.getData(0, numberRemaining); // implement getAll
		if (mappingIteratorBean.getSize() != numberRemaining) {
			list = mappingIteratorBean.getData(0, mappingIteratorBean.getSize() );
		}
		return list;
    }

/*
    public static String TYPE_ROLE = "type_role";
    public static String TYPE_ASSOCIATION = "type_association";
    public static String TYPE_SUPERCONCEPT = "type_superconcept";
    public static String TYPE_SUBCONCEPT = "type_subconcept";
    public static String TYPE_INVERSE_ROLE = "type_inverse_role";
    public static String TYPE_INVERSE_ASSOCIATION = "type_inverse_association";

*/

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


    public HashMap getMappingRelationshipHashMap(String scheme, String version, String code) {
        HashMap hmap = new HashMap();
		HashMap map1 = getMappingRelationshipHashMap(scheme, version, code, 1);
        ArrayList list = (ArrayList) map1.get(TYPE_ASSOCIATION);
        if (list != null) {
			hmap.put(TYPE_ASSOCIATION, list);
		}
		HashMap map2 = getMappingRelationshipHashMap(scheme, version, code, -1);
        list = (ArrayList) map2.get(TYPE_INVERSE_ASSOCIATION);
        if (list != null) {
			hmap.put(TYPE_INVERSE_ASSOCIATION, list);
		}
		return hmap;
	}


    public HashMap getMappingRelationshipHashMap(
        String scheme, String version, String code, int direction) {

		SearchContext searchContext = SearchContext.SOURCE_OR_TARGET_CODES;
		if (direction == 1) searchContext = SearchContext.SOURCE_CODES;
        else if (direction == -1) searchContext = SearchContext.TARGET_CODES;
        LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
        LexBIGServiceConvenienceMethods lbscm =
            new DataUtils().createLexBIGServiceConvenienceMethods(lbSvc);

        CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
        if (version != null)
            csvt.setVersion(version);


        ResolvedConceptReferencesIteratorWrapper wrapper = searchByCode(
         scheme, version, code, "exactMatch", searchContext, -1);

        if (wrapper == null) return null;
        ResolvedConceptReferencesIterator iterator = wrapper.getIterator();
        if (iterator == null) return null;

        HashMap hmap = new HashMap();
        ArrayList list = new ArrayList();
        try {
			while (iterator.hasNext()) {
				ResolvedConceptReference ref = (ResolvedConceptReference) iterator.next();

				AssociationList asso_of = ref.getSourceOf();
				//KLO, 030811
				if (direction == -1) asso_of = ref.getTargetOf();

				if (asso_of != null) {
					Association[] associations =
						asso_of.getAssociation();

					if (associations != null) {

						for (int i = 0; i < associations.length; i++) {
							Association assoc = associations[i];
							String associationName = null;
							try {
								associationName = lbscm
									.getAssociationNameFromAssociationCode(
										scheme, csvt, assoc
											.getAssociationName());
							} catch (Exception ex) {
								associationName = assoc.getAssociationName();
							}

							AssociatedConcept[] acl =
								assoc.getAssociatedConcepts()
									.getAssociatedConcept();

							for (int j = 0; j < acl.length; j++) {
								AssociatedConcept ac = acl[j];

								EntityDescription ed =
									ac.getEntityDescription();

								String name = "No Description";
								if (ed != null)
									name = ed.getContent();
								String pt = name;

								if (associationName
									.compareToIgnoreCase("equivalentClass") != 0
									&& ac.getConceptCode().indexOf("@") == -1) {

/*
									String relaValue =
										replaceAssociationNameByRela(
											ac, associationName);
*/
String relaValue = associationName;

									String s =
										relaValue + "|" + pt + "|"
											 + ac.getConceptCode() + "|"
											 + ac.getCodingSchemeName();

                                    if (direction == -1) {

										EntityDescription ref_ed =
											ref.getEntityDescription();

										String ref_name = "No Description";
										if (ref_ed != null)
											ref_name = ref_ed.getContent();


										s = relaValue + "|" + ref_name + "|"
											 + ref.getCode() + "|"
											 + ref.getCodingSchemeName();

									}

									StringBuffer buf = new StringBuffer();
									buf.append(s);

									if (ac.getAssociationQualifiers() != null) {
										String qualifiers = "";
										for (NameAndValue qual : ac
												.getAssociationQualifiers()
												.getNameAndValue()) {
											String qualifier_name = qual.getName();
											String qualifier_value = qual.getContent();
											qualifiers = qualifiers + (qualifier_name + ":" + qualifier_value) + "$";
										}
										//s = s + "|" + qualifiers;
										buf.append("|" + qualifiers);
									}


                                    if (direction == -1) {
									    //s = s + "|" + ref.getCodeNamespace();
									    buf.append("|" + ref.getCodeNamespace());
									} else {
										//s = s + "|" + ac.getCodeNamespace();
										buf.append("|" + ac.getCodeNamespace());
									}
									s = buf.toString();
									list.add(s);

								}
							}
						}
					}
				}
			}
			if (list.size() > 0) {
				Collections.sort(list);
			}

			if (direction == 1) {
				hmap.put(TYPE_ASSOCIATION, list);
			} else {
				hmap.put(TYPE_INVERSE_ASSOCIATION, list);
			}
	    } catch (Exception ex) {
			ex.printStackTrace();
		}
		return hmap;

	}

    public ResolvedConceptReferencesIteratorWrapper searchByNameOrCode(
        String scheme, String version, String matchText,
        String matchAlgorithm, int maxToReturn, String searchTarget) {
		if (scheme == null) return null;

		Vector schemes = new Vector();
		schemes.add(scheme);
		Vector versions = new Vector();
		versions.add(version);
		if (searchTarget == SearchUtils.SEARCH_BY_CODE_ONLY) {
	        return searchByCode(
         			schemes, versions, matchText,
         			matchAlgorithm, SearchContext.SOURCE_OR_TARGET_CODES, maxToReturn);
		} else {
			return searchByName(schemes, versions, matchText, matchAlgorithm, maxToReturn);
		}
    }

    public ResolvedConceptReferencesIteratorWrapper searchByNameOrCode(
        Vector schemes, Vector versions, String matchText,
        String matchAlgorithm, int maxToReturn, String searchTarget) {
		if (searchTarget == SearchUtils.SEARCH_BY_CODE_ONLY) {
 	        return searchByCode(
         			schemes, versions, matchText,
         			matchAlgorithm, SearchContext.SOURCE_OR_TARGET_CODES, maxToReturn);
		} else {
			return searchByName(schemes, versions, matchText, matchAlgorithm, maxToReturn);
		}
    }
}