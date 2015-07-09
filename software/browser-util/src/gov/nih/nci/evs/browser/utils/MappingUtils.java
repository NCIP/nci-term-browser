package gov.nih.nci.evs.browser.utils;

import gov.nih.nci.evs.browser.bean.*;

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

import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.relations.AssociationPredicate;


/**
 * @author EVS Team
 * @version 1.0
 *
 *      Modification history Initial implementation kim.ong@ngc.com
 *
 */


/**
 * The Class MappingUtils.
 */


public class MappingUtils {
    private static Logger _logger = Logger.getLogger(MappingUtils.class);

	private String serviceUrl = null;
    private LexBIGService lbSvc = null;


    public MappingUtils(LexBIGService lbSvc) {
        this.lbSvc = lbSvc;
    }

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}


	public List<MappingSortOption> createMappingSortOption(int sortBy) {
        List<MappingSortOption> list = new ArrayList<MappingSortOption>();
        MappingSortOption option = null;
        QualifierSortOption qualifierOption = null;
        switch (sortBy) {
            case 1:
				option = new MappingSortOption(MappingSortOptionName.SOURCE_CODE, Direction.ASC);
				list.add(option);
				option = new MappingSortOption(MappingSortOptionName.SOURCE_ENTITY_DESCRIPTION, Direction.ASC);
                list.add(option);
                qualifierOption = new QualifierSortOption(Direction.ASC, "rel");
                list.add(qualifierOption);
                qualifierOption = new QualifierSortOption(Direction.DESC, "score");
                list.add(qualifierOption);
				option = new MappingSortOption(MappingSortOptionName.TARGET_CODE, Direction.ASC);
				list.add(option);
				option = new MappingSortOption(MappingSortOptionName.TARGET_ENTITY_DESCRIPTION, Direction.ASC);
                list.add(option);
                break;

            case 2:
				option = new MappingSortOption(MappingSortOptionName.SOURCE_ENTITY_DESCRIPTION, Direction.ASC);
                list.add(option);
				option = new MappingSortOption(MappingSortOptionName.SOURCE_CODE, Direction.ASC);
				list.add(option);
                qualifierOption = new QualifierSortOption(Direction.ASC, "rel");
                list.add(qualifierOption);
                qualifierOption = new QualifierSortOption(Direction.DESC, "score");
                list.add(qualifierOption);
				option = new MappingSortOption(MappingSortOptionName.TARGET_CODE, Direction.ASC);
				list.add(option);
				option = new MappingSortOption(MappingSortOptionName.TARGET_ENTITY_DESCRIPTION, Direction.ASC);
                list.add(option);
                break;

            // to be modified
            case 3:
				option = new MappingSortOption(MappingSortOptionName.SOURCE_ENTITY_DESCRIPTION, Direction.ASC);
                list.add(option);
				option = new MappingSortOption(MappingSortOptionName.SOURCE_CODE, Direction.ASC);
				list.add(option);
                qualifierOption = new QualifierSortOption(Direction.ASC, "rel");
                list.add(qualifierOption);
                qualifierOption = new QualifierSortOption(Direction.DESC, "score");
                list.add(qualifierOption);
				option = new MappingSortOption(MappingSortOptionName.TARGET_CODE, Direction.ASC);
				list.add(option);
				option = new MappingSortOption(MappingSortOptionName.TARGET_ENTITY_DESCRIPTION, Direction.ASC);
                list.add(option);
                break;

            case 4:
                qualifierOption = new QualifierSortOption(Direction.ASC, "rel");
                list.add(qualifierOption);
                qualifierOption = new QualifierSortOption(Direction.DESC, "score");
                list.add(qualifierOption);
				option = new MappingSortOption(MappingSortOptionName.SOURCE_CODE, Direction.ASC);
				list.add(option);
				option = new MappingSortOption(MappingSortOptionName.SOURCE_ENTITY_DESCRIPTION, Direction.ASC);
                list.add(option);
				option = new MappingSortOption(MappingSortOptionName.TARGET_CODE, Direction.ASC);
				list.add(option);
				option = new MappingSortOption(MappingSortOptionName.TARGET_ENTITY_DESCRIPTION, Direction.ASC);
                list.add(option);
                break;

            case 5:
                qualifierOption = new QualifierSortOption(Direction.DESC, "score");
                list.add(qualifierOption);
				option = new MappingSortOption(MappingSortOptionName.SOURCE_CODE, Direction.ASC);
				list.add(option);
				option = new MappingSortOption(MappingSortOptionName.SOURCE_ENTITY_DESCRIPTION, Direction.ASC);
                list.add(option);
                qualifierOption = new QualifierSortOption(Direction.ASC, "rel");
                list.add(qualifierOption);
				option = new MappingSortOption(MappingSortOptionName.TARGET_CODE, Direction.ASC);
				list.add(option);
				option = new MappingSortOption(MappingSortOptionName.TARGET_ENTITY_DESCRIPTION, Direction.ASC);
                list.add(option);
                break;

            case 6:
				option = new MappingSortOption(MappingSortOptionName.TARGET_CODE, Direction.ASC);
				list.add(option);
				option = new MappingSortOption(MappingSortOptionName.TARGET_ENTITY_DESCRIPTION, Direction.ASC);
                list.add(option);
				option = new MappingSortOption(MappingSortOptionName.SOURCE_CODE, Direction.ASC);
				list.add(option);
				option = new MappingSortOption(MappingSortOptionName.SOURCE_ENTITY_DESCRIPTION, Direction.ASC);
                list.add(option);
                qualifierOption = new QualifierSortOption(Direction.ASC, "rel");
                list.add(qualifierOption);
                qualifierOption = new QualifierSortOption(Direction.DESC, "score");
                list.add(qualifierOption);
                break;

            case 7:
				option = new MappingSortOption(MappingSortOptionName.TARGET_ENTITY_DESCRIPTION, Direction.ASC);
                list.add(option);
 				option = new MappingSortOption(MappingSortOptionName.TARGET_CODE, Direction.ASC);
				list.add(option);
				option = new MappingSortOption(MappingSortOptionName.SOURCE_CODE, Direction.ASC);
				list.add(option);
				option = new MappingSortOption(MappingSortOptionName.SOURCE_ENTITY_DESCRIPTION, Direction.ASC);
                list.add(option);
                qualifierOption = new QualifierSortOption(Direction.ASC, "rel");
                list.add(qualifierOption);
                qualifierOption = new QualifierSortOption(Direction.DESC, "score");
                list.add(qualifierOption);
               break;

            // to be modified
            case 8:
 				//option = new MappingSortOption(MappingSortOptionName.TARGET_NAMESPACE, Direction.ASC);
                //list.add(option);
				option = new MappingSortOption(MappingSortOptionName.TARGET_ENTITY_DESCRIPTION, Direction.ASC);
                list.add(option);
 				option = new MappingSortOption(MappingSortOptionName.TARGET_CODE, Direction.ASC);
				list.add(option);
				option = new MappingSortOption(MappingSortOptionName.SOURCE_CODE, Direction.ASC);
				list.add(option);
				option = new MappingSortOption(MappingSortOptionName.SOURCE_ENTITY_DESCRIPTION, Direction.ASC);
                list.add(option);
                qualifierOption = new QualifierSortOption(Direction.ASC, "rel");
                list.add(qualifierOption);
                qualifierOption = new QualifierSortOption(Direction.DESC, "score");
                list.add(qualifierOption);
               break;

            default:
               return createMappingSortOption(1);
		}
		return list;
	}


    public ResolvedConceptReferencesIterator getMappingDataIterator(String scheme, String version) {
		return getMappingDataIterator(scheme, version, MappingData.COL_SOURCE_CODE);
	}

    public ResolvedConceptReferencesIterator getMappingDataIterator(String scheme, String version, int sortBy) {
		List<MappingSortOption> sortOptionList = createMappingSortOption(sortBy);
		return getMappingDataIterator(scheme, version, sortOptionList);
	}

    public ResolvedConceptReferencesIterator getMappingDataIterator(String scheme, String version, List<MappingSortOption> sortOptionList) {
		CodingSchemeVersionOrTag versionOrTag =
			new CodingSchemeVersionOrTag();
		if (version != null) {
			versionOrTag.setVersion(version);
		}
		String relationsContainerName = null;
        try {

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
				return null;
			}

			MappingExtension mappingExtension = (MappingExtension)
				lbSvc.getGenericExtension("MappingExtension");

			ResolvedConceptReferencesIterator itr = mappingExtension.resolveMapping(
					scheme,
					versionOrTag,
					relationsContainerName,
					sortOptionList);

			return itr;

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

    public NameAndValueList getMappingAssociationNames(String scheme, String version) {
        CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
        if (version != null) {
            csvt.setVersion(version);
		}
		NameAndValueList navList = new NameAndValueList();
		try {
			CodingScheme cs = lbSvc.resolveCodingScheme(scheme, csvt);
			Relations[] relations = cs.getRelations();
			for (int i = 0; i < relations.length; i++) {
				Relations relation = relations[i];
                Boolean isMapping = relation.isIsMapping();
                if (isMapping != null && isMapping.equals(Boolean.TRUE)) {
					AssociationPredicate[] associationPredicates = relation.getAssociationPredicate();
					for (int j=0; j<associationPredicates.length; j++) {
						AssociationPredicate associationPredicate = associationPredicates[j];
						String name = associationPredicate.getAssociationName();
						NameAndValue vNameAndValue = new NameAndValue();
						vNameAndValue.setName(name);
						navList.addNameAndValue(vNameAndValue);
					}
					return navList;
				} else {
					return null;
				}
			}
		} catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    public Vector getMappingCodingSchemesEntityParticipatesIn(String code, String namespace) {
        Vector v = new Vector();
        try {
			MappingExtension mappingExtension =
				(MappingExtension) lbSvc.getGenericExtension("MappingExtension");

			AbsoluteCodingSchemeVersionReferenceList mappingSchemes =
				mappingExtension.getMappingCodingSchemesEntityParticipatesIn(code, namespace);

			//output is all of the mapping ontologies that this code participates in.
			for(AbsoluteCodingSchemeVersionReference ref : mappingSchemes.getAbsoluteCodingSchemeVersionReference()){
				v.add(ref.getCodingSchemeURN() + "|" + ref.getCodingSchemeVersion());
			}

		} catch (Exception ex) {
            ex.printStackTrace();
        }
		return v;
	}


}


