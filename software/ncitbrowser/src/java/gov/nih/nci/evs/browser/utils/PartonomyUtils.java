package gov.nih.nci.evs.browser.utils;

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


public class PartonomyUtils {
    private static Logger _logger = Logger.getLogger(PartonomyUtils.class);
    private LexBIGService lbSvc = null;
    private LexBIGServiceConvenienceMethods lbscm = null;

	private static String[] PART_OF = new String[] {
		                                    "part_of",
		                                    "componentOf",
		                                    "Anatomic_Structure_Is_Physical_Part_Of",
		                                    "Gene_Product_Is_Physical_Part_Of",
		                                    "Part_Of"};


	private static String[] HAS_PART = new String[] {
		                                    "has_part",
		                                    "has_organism_part",
		                                    "has_part_modified",
											"has_component_part",
											"Has_Part"};


	private static List<String> PART_OF_LIST = Arrays.asList(PART_OF);
	private static List<String> HAS_PART_LIST = Arrays.asList(HAS_PART);

	RelationshipUtils relationshipUtils = null;

	public PartonomyUtils(LexBIGService lbSvc) {
        this.lbSvc = lbSvc;
        this.relationshipUtils = new RelationshipUtils(lbSvc);
        try {
            this.lbscm =
                (LexBIGServiceConvenienceMethods) lbSvc
                    .getGenericExtension("LexBIGServiceConvenienceMethods");
            lbscm.setLexBIGService(lbSvc);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public List getPartOfData(HashMap relMap) {
		if (relMap == null) return null;
		List list_all = new ArrayList();
	    List list = relationshipUtils.getRelationshipData(relMap, "type_role", PART_OF_LIST);
	    if (list != null && list.size() > 0) {
			list_all.addAll(list);
		}
	    list = relationshipUtils.getRelationshipData(relMap, "type_association", PART_OF_LIST);
	    if (list != null && list.size() > 0) {
			list_all.addAll(list);
		}

	    list = relationshipUtils.getRelationshipData(relMap, "inverse_type_role", HAS_PART_LIST);
	    if (list != null && list.size() > 0) {
			list_all.addAll(list);
		}
	    list = relationshipUtils.getRelationshipData(relMap, "inverse_type_association", HAS_PART_LIST);
	    if (list != null && list.size() > 0) {
			list_all.addAll(list);
		}
		return list_all;
	}


	public List getHasPartData(HashMap relMap) {
		if (relMap == null) return null;
		List list_all = new ArrayList();
	    List list = relationshipUtils.getRelationshipData(relMap, "type_inverse_role", PART_OF_LIST);
	    if (list != null && list.size() > 0) {
			list_all.addAll(list);
		}
	    list = relationshipUtils.getRelationshipData(relMap, "type_inverse_association", PART_OF_LIST);
	    if (list != null && list.size() > 0) {
			list_all.addAll(list);
		}
	    list = relationshipUtils.getRelationshipData(relMap, "type_role", HAS_PART_LIST);
	    if (list != null && list.size() > 0) {
			list_all.addAll(list);
		}
	    list = relationshipUtils.getRelationshipData(relMap, "type_association", HAS_PART_LIST);
	    if (list != null && list.size() > 0) {
			list_all.addAll(list);
		}

		return list_all;
	}

	public boolean hasPartOfRelationships(HashMap relMap) {
		if (relMap == null) return false;
		List list = getHasPartData(relMap);
		if (list != null && list.size()>0) return true;
		list = getPartOfData(relMap);
		if (list != null && list.size()>0) return true;
		return false;
	}


	public static void main(String [] args) {
		try {
			LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
//Hand (Code C32712)
			String codingSchemeURN = "NCI_Thesaurus";
			String codingSchemeVersion = "15.07d";
			String code = "C32712";
			String namespace = null;
			boolean useNamespace = false;

			RelationshipUtils relUtils = new RelationshipUtils(lbSvc);
			HashMap relMap = relUtils.getRelationshipHashMap(codingSchemeURN, codingSchemeVersion, code, namespace, useNamespace);
			Iterator it = relMap.keySet().iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				ArrayList list = (ArrayList) relMap.get(key);
				System.out.println("\n" + key);
				if (list != null) {
					for (int i=0; i<list.size(); i++) {
						String t = (String) list.get(i);
						System.out.println("\t" + t);
					}
				}
			}
/*
			System.out.println("\n=========================part_of_list========================================");
	        List part_of_list = relUtils.getPartonomyData(relMap, PartonomyUtils.HAS_PART_LIST);
	        for (int i=0; i<part_of_list.size(); i++) {
				String t = (String) part_of_list.get(i);
				System.out.println(t);
			}
*/

            PartonomyUtils partUtils = new PartonomyUtils(lbSvc);
			System.out.println("\n==========================part_of_list========================================");
	        List part_of_list = partUtils.getPartOfData(relMap);
	        for (int i=0; i<part_of_list.size(); i++) {
				String t = (String) part_of_list.get(i);
				System.out.println(t);
			}

			System.out.println("\n==========================has_part_list========================================");
	        List has_part_list = partUtils.getHasPartData(relMap);
	        for (int i=0; i<has_part_list.size(); i++) {
				String t = (String) has_part_list.get(i);
				System.out.println(t);
			}


/*
            List options = relUtils.createOptionList(true, true, true, false, false, false);
			relMap = relUtils.getRelationshipHashMap(codingSchemeURN, codingSchemeVersion, code, namespace, useNamespace, options);
			it = relMap.keySet().iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				ArrayList list = (ArrayList) relMap.get(key);
				System.out.println("\n" + key);
				if (list != null) {
					for (int i=0; i<list.size(); i++) {
						String t = (String) list.get(i);
						System.out.println("\t" + t);
					}
				}
			}
*/



		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
