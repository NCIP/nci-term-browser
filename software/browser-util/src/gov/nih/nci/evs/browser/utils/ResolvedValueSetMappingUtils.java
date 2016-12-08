package gov.nih.nci.evs.browser.utils;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.*;
import java.util.*;
import java.text.*;

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Collections.ModuleDescriptionList;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.ModuleDescription;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.Exceptions.LBException;

import gov.nih.nci.evs.security.SecurityToken;
import gov.nih.nci.system.client.ApplicationServiceProvider;

import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.caCore.interfaces.LexEVSDistributed;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.Extensions.Generic.LexBIGServiceConvenienceMethods.*;
import org.LexGrid.LexBIG.Extensions.Generic.*;
import org.LexGrid.naming.*;
import org.LexGrid.LexBIG.Exceptions.*;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;

import org.lexevs.tree.json.JsonConverter;
import org.lexevs.tree.json.JsonConverterFactory;
import org.lexevs.tree.model.LexEvsTree;
import org.lexevs.tree.model.LexEvsTreeNode;
import org.lexevs.tree.model.LexEvsTreeNode.ExpandableStatus;
import org.lexevs.tree.service.TreeService;
import org.lexevs.tree.service.TreeServiceFactory;

import org.lexevs.tree.dao.iterator.ChildTreeNodeIterator;

import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeTagList;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.RenderingDetail;

import gov.nih.nci.evs.browser.common.*;

import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;
import org.LexGrid.LexBIG.DataModel.Collections.NameAndValueList;
import org.LexGrid.LexBIG.DataModel.Collections.SortOptionList;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;

import org.LexGrid.concepts.Entity;
import org.LexGrid.commonTypes.Property;
import org.LexGrid.concepts.Presentation;
import org.LexGrid.concepts.Definition;
import org.LexGrid.commonTypes.Source;
import org.LexGrid.commonTypes.PropertyQualifier;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONValue;

/**
 * @author EVS Team
 * @version 1.0
 *
 *      Modification history Initial implementation kim.ong@ngc.com
 *
 */


/**
 * The Class ResolvedValueSetMappingUtils.
 */


public class ResolvedValueSetMappingUtils {
	LexBIGService lbSvc = null;
	ConceptDetails conceptDetails = null;
	MetathesaurusUtils metathesaurusUtils = null;
	EntityExporter entityExporter = null;
	CodingSchemeDataUtils codingSchemeDataUtils = null;
	Vector valueSetCodes = null;


	public ResolvedValueSetMappingUtils() {

	}


	public ResolvedValueSetMappingUtils(LexBIGService lbSvc) {
		this.lbSvc = lbSvc;
		conceptDetails = new ConceptDetails(lbSvc);
		metathesaurusUtils = new MetathesaurusUtils(lbSvc);
		entityExporter = new EntityExporter(lbSvc);
		codingSchemeDataUtils = new CodingSchemeDataUtils(lbSvc);
	}

//"C3173","Accelerated Phase Chronic Myelogenous Leukemia, BCR-ABL1 Positive","Accelerated Phase CML","A phase of chronic myelogenous leukemia characterized by one or more of the following: 1) Myeloblasts accounting for 10-19% of the peripheral blood white cells or of the nucleated cells in the bone marrow, 2) peripheral blood basophils at least 20%, 3) persistent thrombocytopenia that is unrelated to therapy, 4) persistent thrombocytosis despite adequate therapy, 5) increasing white blood cell count and increasing spleen size unresponsive to therapy, and/or evidence of clonal evolution. (WHO, 2001)","Malignant"

    public String getCode(String csvLine) {
		int n = csvLine.indexOf("\",");
		String code = csvLine.substring(1, n);
		return code;
	}

    public String getCodeAndName(String csvLine) {
		int n = csvLine.indexOf("\",");
		String code = csvLine.substring(1, n);
		csvLine = csvLine.substring(n+2, csvLine.length());
		n = csvLine.indexOf("\",");
		String name = csvLine.substring(1, n);
		return code + "|" + name;
	}



    public void run(PrintWriter pw, Vector codes) {
		String scheme = "NCI_Thesaurus";
		String version = codingSchemeDataUtils.getVocabularyVersionByTag(scheme, "PRODUCTION");
		String ncim_scheme = "NCI Metathesaurus";
		String ncim_version = codingSchemeDataUtils.getVocabularyVersionByTag(ncim_scheme, "PRODUCTION");
		String ltag = null;
		long ms = System.currentTimeMillis();
		//for (int i=0; i<10; i++) {
		for (int i=0; i<codes.size(); i++) {
			String line = (String) codes.elementAt(i);
			Vector u = StringUtils.parseData(line);
			String code = (String) u.elementAt(0);
			String name = (String) u.elementAt(1);
			int j = i+1;
			pw.println("(" + j + ") " + name + " (" + code + ")");
            Vector cuis = metathesaurusUtils.getMatchedMetathesaurusCUIs(scheme, version, ltag, code);
            //for (int k=0; k<cuis.size(); k++) {
		    if (cuis != null && cuis.size() > 0) {
				for (int k=0; k<cuis.size(); k++) {
					String cui = (String) cuis.elementAt(k);
					Entity ncim_entity = conceptDetails.getConceptByCode(ncim_scheme, ncim_version, cui);
					String description = ncim_entity.getEntityDescription().getContent();
					pw.println("CUI: " + cui + " Name: " + description);
					try {
						entityExporter.printProperties(pw, ncim_entity);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
		    }
		    pw.println("\n");
	    }
	    System.out.println("Total run time (ms): " + (System.currentTimeMillis() - ms));
    }

    public Vector getMatchedMetathesaurusCUIs(Entity c) {
        if (c != null) {
            Vector v = conceptDetails.getConceptPropertyValues(c, "NCI_META_CUI");
			if (v == null || v.size() == 0) {
				return conceptDetails.getConceptPropertyValues(c, "UMLS_CUI");
			}
        }
        return null;
    }


/////////////////////////////////////////////////////////////////////////////////////////
    public Vector loadValueSetData(String csvFile) {
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		Vector v = new Vector();
		valueSetCodes = new Vector();
		try {
			br = new BufferedReader(new FileReader(csvFile));
			line = br.readLine();
			int k = 0;
			while ((line = br.readLine()) != null) {
                String code = getCode(line);
				String codeName = getCodeAndName(line);
				valueSetCodes.add(code);
				v.add(codeName);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return v;
	}

	public static void dumpVector(String label, Vector v) {
		System.out.println(label);
		for (int i=0; i<v.size(); i++) {
			String t = (String) v.elementAt(i);
			System.out.println(t);
		}
	}


    public void run(String value_set_ascii_file, String outputfile) {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(outputfile, "UTF-8");
			Vector valueSetCodes = loadValueSetData(value_set_ascii_file);
			run(pw, valueSetCodes);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				pw.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}