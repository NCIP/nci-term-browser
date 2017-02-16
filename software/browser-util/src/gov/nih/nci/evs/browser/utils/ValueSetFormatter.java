package gov.nih.nci.evs.browser.utils;

import gov.nih.nci.evs.browser.bean.MappingData;
import gov.nih.nci.evs.browser.common.Constants;
import gov.nih.nci.evs.browser.properties.*;

import java.io.*;
import java.net.URI;
import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Map;
import javax.faces.model.*;
import org.LexGrid.LexBIG.DataModel.Collections.*;
import org.LexGrid.LexBIG.DataModel.Core.*;
import org.LexGrid.LexBIG.DataModel.Core.types.*;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.*;
import org.LexGrid.LexBIG.Exceptions.*;
import org.LexGrid.LexBIG.Extensions.Generic.*;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.Direction;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.Mapping.SearchContext;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.Mapping;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.MappingSortOption;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.MappingSortOptionName;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.QualifierSortOption;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.Extensions.Generic.SupplementExtension;
import org.LexGrid.LexBIG.History.*;
import org.LexGrid.LexBIG.LexBIGService.*;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.*;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.ActiveOption;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.*;
import org.LexGrid.LexBIG.Utility.Iterators.*;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.LexBIG.caCore.interfaces.LexEVSApplicationService;
import org.LexGrid.LexBIG.caCore.interfaces.LexEVSDistributed;
import org.LexGrid.LexBIG.gui.sortOptions.SortOptions;
import org.LexGrid.codingSchemes.*;
import org.LexGrid.commonTypes.*;
import org.LexGrid.commonTypes.Properties;
import org.LexGrid.commonTypes.Property;
import org.LexGrid.commonTypes.Source;
import org.LexGrid.commonTypes.types.PropertyTypes;
import org.LexGrid.concepts.*;
import org.LexGrid.naming.*;
import org.LexGrid.relations.AssociationPredicate;
import org.LexGrid.relations.Relations;
import org.LexGrid.valueSets.ValueSetDefinition;
import org.LexGrid.versions.*;
import org.apache.commons.codec.language.*;
import org.apache.commons.lang.*;
import org.apache.log4j.*;
import org.lexevs.property.PropertyExtension;
import org.lexgrid.resolvedvalueset.LexEVSResolvedValueSetService;
import org.lexgrid.resolvedvalueset.impl.LexEVSResolvedValueSetServiceImpl;
import org.lexgrid.valuesets.LexEVSValueSetDefinitionServices;
import static gov.nih.nci.evs.browser.common.Constants.*;


public class ValueSetFormatter {

// Variable declaration
	LexBIGService lbSvc = null;
	String serviceUrl = null;
	LexEVSValueSetDefinitionServices vsd_service = null;
	CodingSchemeDataUtils csdu = null;

	private String uri;
	private String name;
	private String defaultCodingScheme;
	private String version;
	private String supportedSource;

	int maxToReturn = 250;

// Default constructor
	public ValueSetFormatter() {

	}

// Constructor

    public ValueSetFormatter(LexBIGService lbSvc, LexEVSValueSetDefinitionServices vsd_service) {
		this.lbSvc = lbSvc;
		this.vsd_service = vsd_service;
        csdu = new CodingSchemeDataUtils(lbSvc);
	}

	public ValueSetFormatter(
		String uri,
		String name,
		String defaultCodingScheme,
		String version,
		String supportedSource) {

		this.uri = uri;
		this.name = name;
		this.defaultCodingScheme = defaultCodingScheme;
		this.version = version;
		this.supportedSource = supportedSource;
	}

// Set methods
	public void setUri(String uri) {
		this.uri = uri;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDefaultCodingScheme(String defaultCodingScheme) {
		this.defaultCodingScheme = defaultCodingScheme;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setSupportedSource(String supportedSource) {
		this.supportedSource = supportedSource;
	}


// Get methods
	public String getUri() {
		return this.uri;
	}

	public String getName() {
		return this.name;
	}

	public String getDefaultCodingScheme() {
		return this.defaultCodingScheme;
	}

	public String getVersion() {
		return this.version;
	}

	public String getSupportedSource() {
		return this.supportedSource;
	}

    public Vector resolveIterator(ResolvedConceptReferencesIterator iterator, int maxToReturn) {
        Vector v = new Vector();
        if (iterator == null) {
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
				    String t = rcr.getEntityDescription().getContent() + "|"+ rcr.getCode() + "|"+ rcr.getCodingSchemeName()
					+ "|" + rcr.getCodeNamespace();
                    v.add(t);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }

    public Vector run(String scheme, String version, String source, Vector codes, int maxToReturn) {
		Vector w = new Vector();
		long ms = System.currentTimeMillis();
		try {
			CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
			if (version != null) {
				csvt.setVersion(version);
			}
			String[] a = new String[codes.size()];
			for (int i=0; i<codes.size(); i++) {
				String t = (String) codes.elementAt(i);
				a[i] = t;
			}

			ConceptReferenceList crefs = ConvenienceMethods.createConceptReferenceList(a, scheme);
			CodedNodeSet cns = lbSvc.getCodingSchemeConcepts(scheme, csvt);

			if (cns == null) {
				return null;
			}

			org.LexGrid.commonTypes.Property[] properties = null;
			cns = cns.restrictToStatus(ActiveOption.ALL, null);
			cns = cns.restrictToCodes(crefs);

            SortOptionList sortCriteria = null;
            LocalNameList propertyNames = null;
            CodedNodeSet.PropertyType[] propertyTypes = new CodedNodeSet.PropertyType[2];
            propertyTypes[0] = CodedNodeSet.PropertyType.PRESENTATION;
            propertyTypes[1] = CodedNodeSet.PropertyType.DEFINITION;

            ResolvedConceptReferencesIterator iterator = null;
            try {
                iterator =
                    cns.resolve(sortCriteria, propertyNames, propertyTypes);
            } catch (Exception e) {
                e.printStackTrace();
            }



            while (iterator.hasNext()) {
                iterator = iterator.scroll(maxToReturn);
                ResolvedConceptReferenceList rcrl = iterator.getNext();
                ResolvedConceptReference[] rcra =
                    rcrl.getResolvedConceptReference();
                for (int lcv = 0; lcv< rcra.length; lcv++) {
                    ResolvedConceptReference ref = rcra[lcv];
                    Vector synonyms = new Vector();
					Entity node = ref.getEntity();
					String preferred_term = null;
					String nci_definition = null;
					String term_subsource = null;

					Presentation[] prsentations = node.getPresentation();
					for (int i = 0; i < prsentations.length; i++) {
						 Presentation presentation = prsentations[i];
						 String representationalForm = presentation.getRepresentationalForm();

						 String synonym = presentation.getValue().getContent();
						 if (!synonyms.contains(synonym)) {
							 synonyms.add(synonym);
						 }

						 PropertyQualifier[] qualifiers = presentation.getPropertyQualifier();
						 if (qualifiers != null) {
							for (int j = 0; j < qualifiers.length; j++) {
								PropertyQualifier q = qualifiers[j];
								String qualifier_name = q.getPropertyQualifierName();
								String qualifier_value = q.getValue().getContent();
								if (qualifier_name.compareTo("subsource-name") == 0) {
									term_subsource = qualifier_value;
								}
							}
						 }

						 Source[] sources = presentation.getSource();
						 String src = null;
						 for (int k=0; k<sources.length; k++) {
							  src =  sources[k].getContent().toString();
							  if (src.compareTo(source) == 0  && representationalForm.compareTo("PT") == 0) {
								  preferred_term = synonym;
							  } else {
								  if (term_subsource != null && term_subsource.compareTo(source) == 0  && representationalForm.compareTo("PT") == 0) {
									  preferred_term = synonym;
								  }
							  }
						 }
					}

					Definition[] definitions = node.getDefinition();
					for (int i = 0; i < definitions.length; i++) {
						 Definition definition = definitions[i];
						 String def_name = definition.getPropertyName();
						 Source[] sources = definition.getSource();
						 String src = null;
						 for (int k=0; k<sources.length; k++) {
							  src = sources[k].getContent().toString();
							  if ((def_name.compareTo("DEFINITION") == 0) || (src.compareTo("NCI") == 0)) {
								  nci_definition = definition.getValue().getContent().toString();
								  break;
							  }
					     }
					}
					if (preferred_term == null) {
						preferred_term = node.getEntityDescription().getContent();
					}
					synonyms = gov.nih.nci.evs.browser.utils.SortUtils.quickSort(synonyms);

					StringBuffer syn_buf = new StringBuffer();
					for (int n=0; n<synonyms.size(); n++) {
						String syn = (String) synonyms.elementAt(n);
						syn_buf.append(syn).append("$");
					}
					String syn_str = syn_buf.toString();
					syn_str = syn_str.substring(0, syn_str.length()-1);
					String line = node.getEntityCode() + "|" + preferred_term + "|"
					    + node.getEntityDescription().getContent()
					    + "|" + syn_str + "|"
					    + node.getEntityCodeNamespace()
					    + "|" + nci_definition;
					//System.out.println(line);
					w.add(line);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("Total ValueSetFormatter run time (ms): " + (System.currentTimeMillis() - ms));
        return w;
    }

/*
	public static void main(String[] args) {
		//String uri = args[0];
		String vsd_uri = "http://ndfrt:PE";
		vsd_uri = "http://evs.nci.nih.gov/valueset/C54453";
		vsd_uri = "http://evs.nci.nih.gov/valueset/C54451";
		vsd_uri = "http://evs.nci.nih.gov/valueset/C73339";
		String serviceUrl = "https://lexevsapi6-stage.nci.nih.gov/lexevsapi64";

		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
		LexEVSValueSetDefinitionServices vsd_service = RemoteServerUtil.getLexEVSValueSetDefinitionServices(serviceUrl);

		ValueSetDefinitionUtils vsds = new ValueSetDefinitionUtils(lbSvc, vsd_service);
		XMLNode xmlNode = vsds.createValueSetXMLNode(vsd_uri);
        System.out.println("vsd_uri: " + vsd_uri);
		String source = vsds.getValueSetSupportedSource(xmlNode);
        System.out.println("source: " + source);
        String scheme = vsds.getValueSetDefaultCodingScheme(xmlNode);
        System.out.println("defaultCodingScheme: " + scheme);

        ResolvedConceptReferencesIterator rcri = vsds.resolveValueSetCodingScheme(vsd_uri, null);
        Vector code_vec = new Vector();
        Vector v = vsds.resolveIterator(rcri, 100);
        for (int i=0; i<v.size(); i++) {
			int j = i+1;
			String t = (String) v.elementAt(i);
			System.out.println("(" + j + ") " + t);

			Vector u = gov.nih.nci.evs.browser.utils.StringUtils.parseData(t);
			String code = (String) u.elementAt(1);
			scheme = (String) u.elementAt(3);
			code_vec.add(code);
		}

        long ms = System.currentTimeMillis();
		String version = null;
		System.out.println("\n=========================================");
        try {
			Vector w = new ValueSetFormatter(lbSvc, vsd_service).run(scheme, version, source, code_vec, 250);
			for (int i=0; i<w.size(); i++) {
				int j = i+1;
				String t = (String) w.elementAt(i);
				System.out.println("(" + j + ") " + t);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("generateReport Total run time (ms): " + (System.currentTimeMillis() - ms));
	}
*/
}
