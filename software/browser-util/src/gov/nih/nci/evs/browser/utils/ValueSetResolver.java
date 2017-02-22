package gov.nih.nci.evs.browser.utils;

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



public class ValueSetResolver {
	LexBIGService lbSvc = null;
	LexEVSValueSetDefinitionServices vsd_service = null;
	ValueSetDefUtils valueSetDefUtils = null;
	HashMap vsdUri2NameMap = null;
	CodingSchemeDataUtils csdu = null;

	public ValueSetResolver() {

	}

	public ValueSetResolver(LexBIGService lbSvc, LexEVSValueSetDefinitionServices vsd_service) {
		this.lbSvc = lbSvc;
		valueSetDefUtils = new ValueSetDefUtils(lbSvc, vsd_service);
		vsdUri2NameMap = valueSetDefUtils.getVsdUri2NameMap();
        csdu = new CodingSchemeDataUtils(lbSvc);
	}

	public void run(String outputfile) {
		long ms = System.currentTimeMillis();
		PrintWriter pw = null;
		String version = null;
		boolean resolveObjects = false;
		Vector result_vec = new Vector();
		int i = 0;
		try {
			pw = new PrintWriter(outputfile, "UTF-8");
			Iterator it = vsdUri2NameMap.keySet().iterator();
			while (it.hasNext()) {
				String cs_uri = (String) it.next();
				String name = (String) vsdUri2NameMap.get(cs_uri);
				i++;
				System.out.println("(" + i + ") " + name + " (" + cs_uri + ")");
				ResolvedConceptReferencesIterator rcri = null;
				try {
					rcri = csdu.resolveCodingScheme(cs_uri, version, resolveObjects);
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				if (rcri == null) {
					//pw.println("\t" + "Unable to resolve " + name + " (" + cs_uri + ")");
					result_vec.add(name + "|" + cs_uri + "|Exception thrown at csdu.resolveCodingScheme.");
				} else {
					try {
						int numberRemaining = rcri.numberRemaining();
						result_vec.add(name + "|" + cs_uri + "|" + numberRemaining);
					} catch (Exception ex) {
						ex.printStackTrace();
						result_vec.add(name + "|" + cs_uri + "|Exception thrown at rcri.numberRemaining()");
					}
				}
			}
			result_vec = SortUtils.quickSort(result_vec);
			int j = 0;
			pw.println("\n\n");
			for (i=0; i<result_vec.size(); i++) {
				String t = (String) result_vec.elementAt(i);
				j++;
				pw.println("(" + j + ") " + t);
			}


		} catch (Exception ex) {

		} finally {
			try {
				pw.close();
				System.out.println("Output file " + outputfile + " generated.");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		System.out.println("Total run time (ms): " + (System.currentTimeMillis() - ms));
	}
/*
    public static void main(String[] args) {
		String outputfile = args[0];
		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
		LexEVSValueSetDefinitionServices vsd_service = RemoteServerUtil.getLexEVSValueSetDefinitionServices();
        ValueSetResolver resolver = new ValueSetResolver(lbSvc, vsd_service);
		long ms = System.currentTimeMillis();
		resolver.run(outputfile);
		System.out.println("ValueSetResolver total run time (ms): " + (System.currentTimeMillis() - ms));
    }
*/
}
