package gov.nih.nci.evs.browser.utils;

import gov.nih.nci.evs.browser.bean.*;
import gov.nih.nci.evs.browser.common.*;

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
import org.LexGrid.naming.Mappings.*;
import org.LexGrid.naming.SupportedSource;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class ValueSetFormatter {

// Variable declaration
	public static final String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

	LexBIGService lbSvc = null;
	String serviceUrl = null;
	LexEVSValueSetDefinitionServices vsd_service = null;
	CodingSchemeDataUtils csdu = null;
	public static int MAX_RETURN = 250;

	private String uri;
	private String name;
	private String defaultCodingScheme;
	private String version;
	private String supportedSource;

	private static int maxToReturn = 250;
	private static String TYPE_PRESENTATION = "PRESENTATION";
	private static String TYPE_DEFINITION = "DEFINITION";
	private static String TYPE_COMMENT = "COMMENT";
	private static String TYPE_PROPERTY = "PROPERTY";

	private static String NCIT_CONCEPT_CODE = "NCIt Concept Code";
	//private static String SOURCE_PREFERRED_TERM = "Source Preferred Term";
	private static String SOURCE_PREFERRED_TERM = "Source Name";
	private static String SOURCE_PREFERRED_TERM_SOURCE_CODE = "Source Preferred Term Source Code";
	private static String SOURCE_SYNONYMS = "Source Synonyms";
	private static String SOURCE_SYNONYM_SOURCE_CODE= "Source Synonym Source Code";
	private static String SOURCE_DEFINITION = "Source Definition";
	private static String SOURCE_SUBSET_CODE = "Source Subset Code";
	private static String SOURCE_SUBSET_NAME = "Source Subset Name";

	private static String NCIT_PREFERRED_TERM = "NCIt Preferred Term";
	private static String NCIT_SYNONYMS = "NCIt Synonyms";
	private static String NCIT_DEFINITION = "NCIt Definition";

	private static String MALIGNANCY_STATUS = "Malignancy Status";
	private static String NCI_METATHESAURUS_CUI = "NCI Metathesaurus CUI";
	private static String UMLS_CUI = "UMLS CUI";

	private static String[] TYPES = {NCIT_CONCEPT_CODE,
	                                 SOURCE_PREFERRED_TERM,
	                                 SOURCE_PREFERRED_TERM_SOURCE_CODE,
	                                 SOURCE_SYNONYMS,
	                                 SOURCE_SYNONYM_SOURCE_CODE,
	                                 SOURCE_DEFINITION,
	                                 SOURCE_SUBSET_CODE,
	                                 SOURCE_SUBSET_NAME,
	                                 NCIT_PREFERRED_TERM,
	                                 NCIT_SYNONYMS,
	                                 NCIT_DEFINITION,
	                                 MALIGNANCY_STATUS,
	                                 NCI_METATHESAURUS_CUI,
	                                 UMLS_CUI
	                                 };
    UIUtils uiUtils = null;

    static HashMap vsHeading2VarHashMap = null;

	private String NCITCODE = "ncitCode";
	private String SOURCEPREFERREDTERM = "sourcePreferredTerm";  //*
	private String NCITPREFERREDTERM = "ncitPreferredTerm"; //*
	private String NCITSYNONYMS = "ncitSynonyms";
	private String SOURCESYNONYMS = "sourceSynonyms"; //*
	private String NCITDEFINITIONS = "ncitDefinitions"; //*
	private String SOURCEDEFINITIONS = "sourceDefinitions";

	private	ValueSetMetadataUtils vsmdu = new ValueSetMetadataUtils(vsd_service);

    static {
		vsHeading2VarHashMap = new HashMap();
		vsHeading2VarHashMap.put("NCIt Concept Code", "ncitCode");
		vsHeading2VarHashMap.put("Source Name", "sourcePreferredTerm");
		vsHeading2VarHashMap.put("NCIt Preferred Term", "ncitPreferredTerm");
		vsHeading2VarHashMap.put("NCIt Synonyms", "ncitSynonyms");
		vsHeading2VarHashMap.put("Source Synonyms", "sourceSynonyms");
		vsHeading2VarHashMap.put("NCIt Definition", "ncitDefinitions");
		vsHeading2VarHashMap.put("Source Definition", "sourceDefinitions");
	}


// Default constructor
	public ValueSetFormatter() {
        uiUtils = new UIUtils();
	}

// Constructor

    public ValueSetFormatter(LexBIGService lbSvc, LexEVSValueSetDefinitionServices vsd_service) {
		this.lbSvc = lbSvc;
		this.vsd_service = vsd_service;
        csdu = new CodingSchemeDataUtils(lbSvc);
	    vsmdu = new ValueSetMetadataUtils(vsd_service);
        uiUtils = new UIUtils();
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

    public static Vector getDefaultFields(boolean withSource) {
		Vector fields = new Vector();
		fields.add(NCIT_CONCEPT_CODE);
		if (withSource) {
			fields.add(SOURCE_PREFERRED_TERM);
		}
		fields.add(NCIT_PREFERRED_TERM);
		fields.add(NCIT_SYNONYMS);
		if (withSource) {
			fields.add(SOURCE_DEFINITION);
	    }
		fields.add(NCIT_DEFINITION);
		return fields;
	}

    public static Vector getDefaultFields() {
		return getDefaultFields(true);
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

    private LocalNameList createLocalNameList(Vector propertyNames) {
		if (propertyNames == null) return null;
		LocalNameList propertyList = new LocalNameList();
		for (int i=0; i<propertyNames.size(); i++) {
			String propertyName = (String) propertyNames.elementAt(i);
			propertyList.addEntry(propertyName);
		}
		return propertyList;
	}

    public CodedNodeSet restrictToProperties(CodedNodeSet cns, Vector propertyNames) {
		LocalNameList propertyList = createLocalNameList(propertyNames);
		try {
			cns = cns.restrictToProperties(propertyList, null);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return cns;
	}

    public LocalNameList getPropertyNameLocalNameList(Vector types) {
		 Vector property_names = new Vector();
		 if (types == null || types.size() == 0) return null;
		 for (int i=0; i<types.size(); i++) {
			 String type = (String) types.elementAt(i);
			 if (type.endsWith("Name") || type.endsWith("Term") || type.endsWith("Code")) {
				 if (!property_names.contains("FULL_SYN")) {
					 property_names.add("FULL_SYN");
				 }
			 } else if (type.endsWith("Definition")) {
				 if (!property_names.contains("DEFINITION")) {
					 property_names.add("DEFINITION");
				 }
				 if (!property_names.contains("ALT_DEFINITION")) {
					 property_names.add("ALT_DEFINITION");
				 }
			 } else {
				 if (type.compareTo(MALIGNANCY_STATUS) == 0) {
					 if (!property_names.contains("Malignancy_Status")) {
						 property_names.add("Malignancy_Status");
					 }
				 }
				 if (type.compareTo(MALIGNANCY_STATUS) == 0) {
					 if (!property_names.contains("Malignancy_Status")) {
						 property_names.add("Malignancy_Status");
					 }
				 } else if (type.compareTo(NCI_METATHESAURUS_CUI) == 0) {
					 if (!property_names.contains("NCI_META_CUI")) {
						 property_names.add("NCI_META_CUI");
					 }
				 } else if (type.compareTo(UMLS_CUI) == 0) {
					 if (!property_names.contains("UMLS_CUI")) {
						 property_names.add("UMLS_CUI");
					 }
				 }
			 }
		 }
		 return createLocalNameList(property_names);
	 }

     public Vector resolve(String scheme, String version, String source, Vector fields, Vector codes, int maxToReturn) {
		CodingSchemeDataUtils csdu = new CodingSchemeDataUtils(lbSvc);
		Vector w = new Vector();
		long ms = System.currentTimeMillis();
		try {
			CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
			if (version == null) {
				version = csdu.getVocabularyVersionByTag(scheme, Constants.PRODUCTION);
			}
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
            CodedNodeSet.PropertyType[] propertyTypes = null;
            LocalNameList propertyNames = getPropertyNameLocalNameList(fields);

            /*
            CodedNodeSet.PropertyType[] propertyTypes = new CodedNodeSet.PropertyType[3];
            propertyTypes[0] = CodedNodeSet.PropertyType.PRESENTATION;
            propertyTypes[1] = CodedNodeSet.PropertyType.DEFINITION;
            propertyTypes[2] = CodedNodeSet.PropertyType.GENERIC;
            */

            ResolvedConceptReferencesIterator iterator = null;
            try {
                iterator = cns.resolve(sortCriteria, propertyNames, propertyTypes);
                try {
					int numRemaining = iterator.numberRemaining();
				} catch (Exception e) {
					e.printStackTrace();
				}

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
                    Entity node = ref.getEntity();
                    String line = csdu.getPropertyValues(node);
					w.add(line);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
        return w;
    }

    public HashMap lineSegment2HashMap(String t) {
		HashMap hmap = new HashMap();
		Vector w = gov.nih.nci.evs.browser.utils.StringUtils.parseData(t, "$");
		String prop_type = (String) w.elementAt(0);
		hmap.put("prop_type", prop_type);
		String prop_name = (String) w.elementAt(1);
		hmap.put("prop_name", prop_name);
		String prop_value = (String) w.elementAt(2);
		hmap.put("prop_value", prop_value);
		for (int i=3; i<w.size(); i++) {
			String s = (String) w.elementAt(i);
			Vector nv = gov.nih.nci.evs.browser.utils.StringUtils.parseData(s, "=");
			String nm = (String) nv.elementAt(0);
			String val = (String) nv.elementAt(1);
			hmap.put(nm, val);
		}
		return hmap;
	}

    public String parseProperty(String line, String type, String source) {
		if (line == null) return null;
		Vector u = gov.nih.nci.evs.browser.utils.StringUtils.parseData(line);
		if (type.compareTo(NCIT_CONCEPT_CODE) == 0) {
			String code = (String) u.elementAt(0);
			return code;

		} else if (type.compareTo(SOURCE_PREFERRED_TERM) == 0) {
            for (int i=0; i<u.size(); i++) {
				String t = (String) u.elementAt(i);
				if (t.startsWith("name")) {
					HashMap hmap = lineSegment2HashMap(t);
					String form = (String) hmap.get("form");
					String src = (String) hmap.get("source");
					if (form != null && form.compareTo("PT") == 0 && src != null && src.compareTo(source) == 0) {
						String term_name = (String) hmap.get("prop_value");
						return term_name;
					}
				}
			}

		} else if (type.compareTo(SOURCE_PREFERRED_TERM_SOURCE_CODE) == 0) {
			StringBuffer buf = new StringBuffer();
            for (int i=0; i<u.size(); i++) {
				String t = (String) u.elementAt(i);
				if (t.startsWith("name")) {
					HashMap hmap = lineSegment2HashMap(t);
					String form = (String) hmap.get("form");
					String src = (String) hmap.get("source");
					if (form != null && form.compareTo("PT") == 0 && src != null && src.compareTo(source) == 0) {
						String source_code = (String) hmap.get("source_code");
						buf.append(source_code).append("$");
					}
				}
			}
			String s = buf.toString();
			if (s.length() > 0) {
				s = s.substring(0, s.length()-1);
			}
			return s;

		} else if (type.compareTo(SOURCE_SYNONYMS) == 0) {
			StringBuffer buf = new StringBuffer();
            for (int i=0; i<u.size(); i++) {
				String t = (String) u.elementAt(i);
				if (t.startsWith("name")) {
					HashMap hmap = lineSegment2HashMap(t);
					String form = (String) hmap.get("form");
					String src = (String) hmap.get("source");
					if (form != null && form.compareTo("PT") != 0 && src != null && src.compareTo(source) == 0) {
						String term_name = (String) hmap.get("prop_value");
						buf.append(term_name).append("$");
					}
				}
			}
			String s = buf.toString();
			if (s.length() > 0) {
				s = s.substring(0, s.length()-1);
			}
			return s;
		} else if (type.compareTo(SOURCE_SYNONYM_SOURCE_CODE) == 0) {
			StringBuffer buf = new StringBuffer();
            for (int i=0; i<u.size(); i++) {
				String t = (String) u.elementAt(i);
				if (t.startsWith("name")) {
					HashMap hmap = lineSegment2HashMap(t);
					String form = (String) hmap.get("form");
					String src = (String) hmap.get("source");
					if (form != null && form.compareTo("PT") != 0 && src != null && src.compareTo(source) == 0) {
						String source_code = (String) hmap.get("source-code");
						buf.append(source_code).append("$");
					}
				}
			}
			String s = buf.toString();
			if (s.length() > 0) {
				s = s.substring(0, s.length()-1);
			}
			return s;
		} else if (type.compareTo(SOURCE_DEFINITION) == 0) {
			StringBuffer buf = new StringBuffer();
            for (int i=0; i<u.size(); i++) {
				String t = (String) u.elementAt(i);
				if (t.startsWith("definition")) {
					HashMap hmap = lineSegment2HashMap(t);
					String src = (String) hmap.get("source");
					if (src.compareTo(source) == 0) {
						String def = (String) hmap.get("prop_value");
						buf.append(def).append("$");
					}
				}
			}
			String s = buf.toString();
			if (s.length() > 0) {
				s = s.substring(0, s.length()-1);
			}
			return s;
			/*
		} else if (type.compareTo(SOURCE_SUBSET_CODE) == 0) {

		} else if (type.compareTo(SOURCE_SUBSET_NAME) == 0) {
            */
		} else if (type.compareTo(NCIT_PREFERRED_TERM) == 0) {
            for (int i=0; i<u.size(); i++) {
				String t = (String) u.elementAt(i);
				if (t.startsWith("name")) {
					HashMap hmap = lineSegment2HashMap(t);
					String form = (String) hmap.get("form");
					String src = (String) hmap.get("source");
					if (form != null && form.compareTo("PT") == 0 && src != null && src.compareTo("NCI") == 0) {
						String term_name = (String) hmap.get("prop_value");
						return term_name;
					}
				}
			}
		} else if (type.compareTo(NCIT_SYNONYMS) == 0) {
			Vector syn_vec = new Vector();
            for (int i=0; i<u.size(); i++) {
				String t = (String) u.elementAt(i);
				if (t.startsWith("name")) {
					HashMap hmap = lineSegment2HashMap(t);
					String form = (String) hmap.get("form");
					String src = (String) hmap.get("source");
					if (form != null && form.compareTo("PT") != 0 && src != null && src.compareTo("NCI") == 0) {
						String term_name = (String) hmap.get("prop_value");
						syn_vec.add(term_name);
					}
				}
			}
			HashMap hmap = new HashMap();
			Vector keys = new Vector();
			Vector values = new Vector();
			for (int i=0; i<syn_vec.size(); i++) {
				String syn = (String) syn_vec.elementAt(i);
				String syn_lower_case = syn.toLowerCase();
				keys.add(syn_lower_case);
				hmap.put(syn_lower_case, syn);
			}
			keys = gov.nih.nci.evs.browser.utils.SortUtils.quickSort(keys);
			for (int i=0; i<keys.size(); i++) {
				String key = (String) keys.elementAt(i);
				String value = (String) hmap.get(key);
				values.add(value);
			}
			StringBuffer buf = new StringBuffer();
            for (int i=0; i<values.size(); i++) {
				String value = (String) values.elementAt(i);
				buf.append(value).append("$");
			}
			String s = buf.toString();
			if (s.length() > 0) {
				s = s.substring(0, s.length()-1);
			}

/*
			StringBuffer buf = new StringBuffer();
            for (int i=0; i<u.size(); i++) {
				String t = (String) u.elementAt(i);
				if (t.startsWith("name")) {
					HashMap hmap = lineSegment2HashMap(t);
					String form = (String) hmap.get("form");
					String src = (String) hmap.get("source");
					if (form != null && form.compareTo("PT") != 0 && src != null && src.compareTo("NCI") == 0) {
						String term_name = (String) hmap.get("prop_value");
						buf.append(term_name).append("$");
					}
				}
			}
			String s = buf.toString();
			if (s.length() > 0) {
				s = s.substring(0, s.length()-1);
			}
*/
			return s;
		} else if (type.compareTo(NCIT_DEFINITION) == 0) {
			StringBuffer buf = new StringBuffer();
            for (int i=0; i<u.size(); i++) {
				String t = (String) u.elementAt(i);
				if (t.startsWith("definition")) {
					HashMap hmap = lineSegment2HashMap(t);
					String src = (String) hmap.get("source");
					if (src != null && src.compareTo("NCI") == 0) {
						String def = (String) hmap.get("prop_value");
						buf.append(def).append("$");
					}
				}
			}
			String s = buf.toString();
			if (s.length() > 0) {
				s = s.substring(0, s.length()-1);
			}
			return s;

		} else if (type.compareTo(MALIGNANCY_STATUS) == 0) { //Neoplastic_Status
            for (int i=0; i<u.size(); i++) {
				String t = (String) u.elementAt(i);
				if (t.startsWith("property")) {
					Vector w = gov.nih.nci.evs.browser.utils.StringUtils.parseData(t, "$");
					String prop_name = (String) w.elementAt(1);
					if (prop_name.compareTo("Neoplastic_Status") == 0) {
						String prop_value = (String) w.elementAt(2);
						return prop_value;
					}
				}
			}
		} else if (type.compareTo(NCI_METATHESAURUS_CUI) == 0 || type.compareTo("NCI_META_CUI") == 0) {
            for (int i=0; i<u.size(); i++) {
				String t = (String) u.elementAt(i);
				if (t.startsWith("property")) {
					Vector w = gov.nih.nci.evs.browser.utils.StringUtils.parseData(t, "$");
					String prop_name = (String) w.elementAt(1);
					if (prop_name.compareTo("NCI_META_CUI") == 0) {
						String prop_value = (String) w.elementAt(2);
						return prop_value;
					}
				}
			}

		} else if (type.compareTo(UMLS_CUI) == 0 || type.compareTo("UMLS_CUI") == 0) {
            for (int i=0; i<u.size(); i++) {
				String t = (String) u.elementAt(i);
				if (t.startsWith("property")) {
					Vector w = gov.nih.nci.evs.browser.utils.StringUtils.parseData(t, "$");
					String prop_name = (String) w.elementAt(1);
					if (prop_name.compareTo("UMLS_CUI") == 0) {
						String prop_value = (String) w.elementAt(2);
					    return prop_value;
					}
				}
			}
		} else {
            for (int i=0; i<u.size(); i++) {
				String t = (String) u.elementAt(i);
				if (t.startsWith("property")) {
					Vector w = gov.nih.nci.evs.browser.utils.StringUtils.parseData(t, "$");
					String prop_name = (String) w.elementAt(1);
					if (prop_name.compareTo(type) == 0) {
						String prop_value = (String) w.elementAt(2);
						return prop_value;
					}
				}
			}
		}
		return null;
	}

    public String line2Table(String line, String source) {
		HashMap hmap = line2ValueHashMap(line, source);
		return valueHashMap2Table(hmap);
	}

    public HashMap line2ValueHashMap(String line, String source) {
		HashMap hmap = new HashMap();
		for (int k=0; k<TYPES.length; k++) {
			String type = TYPES[k];
			String value = parseProperty(line, type, source);
			hmap.put(new Integer(k), value);
		}
		return hmap;
	}

	public String valueHashMap2Table(HashMap hmap) {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<TYPES.length; i++) {
			Integer int_obj = new Integer(i);
			String type = TYPES[i];
			String values = (String) hmap.get(int_obj);
			if (values != null  && values.length()>0) {
				buf.append("<table>");
				buf.append("<tr class=\"textbody\">");
				buf.append("    <td>" + type + "</td>");
				Vector u = gov.nih.nci.evs.browser.utils.StringUtils.parseData(values);
				buf.append("<td>");
				buf.append("<table>");
				for (int k=0; k<u.size(); k++) {
					String value = (String) u.elementAt(k);
					buf.append("<tr class=\"textbody\">");
					buf.append("<td valign=\"top\">" + value + "</td>");
					buf.append("</tr>");
			    }
				buf.append("</table>");
				buf.append("</td>");
				buf.append("</tr>");
				buf.append("</table>");
		    }
		}
		return buf.toString();
	}

    public String formatLine(Vector fields, HashMap fieldValueHmap, boolean isEven) {
		String key = null;
		String value = "";
        StringBuffer buf = new StringBuffer();
        if (isEven) {
			buf.append("<tr class=\"dataRowDark\">");
		} else {
			buf.append("<tr class=\"dataRowLight\">");
		}

        for (int i=0; i<fields.size(); i++) {
			buf.append("<td class=\"dataCellText\" scope=\"row\">");
			key = (String) fields.elementAt(i);
			value = (String) fieldValueHmap.get(key);
			if (value == null || value.compareTo("null") == 0) {
				value = "";
			}
			Vector w = gov.nih.nci.evs.browser.utils.StringUtils.parseData(value, "$");
			if (w.size() == 1) {
				String hyperlink = value;
				if (key.compareTo(NCIT_CONCEPT_CODE) == 0) {
					String code = value;
					hyperlink = getHyperlink(code);
				}
				buf.append(hyperlink);
		    } else {
				/*
				buf.append("<table>");
				for (int n=0; n<w.size(); n++) {
					String s = (String) w.elementAt(n);
					buf.append("<tr class=\"textbody\"><td>" + s + "</tr></td>");
				}
				buf.append("</table>");
				*/

				String table = UIUtils.createTable(w);
				/*
				buf.append("<table>");
				for (int n=0; n<w.size(); n++) {
					String s = (String) w.elementAt(n);
					buf.append("<tr class=\"textbody\"><td>" + s + "</tr></td>");
				}
				buf.append("</table>");
				*/
				buf.append(table);
			}
			buf.append("</td>");
		}
		buf.append("</tr>");
		return buf.toString();
	}

    public String formatLine(Vector fields, HashMap fieldValueHmap) {
		String key = null;
		String value = "";
        StringBuffer buf = new StringBuffer();
        for (int i=0; i<fields.size(); i++) {
			key = (String) fields.elementAt(i);
			value = (String) fieldValueHmap.get(key);
			if (value == null || value.compareTo("null") == 0) {
				value = "";
			}
			Vector w = gov.nih.nci.evs.browser.utils.StringUtils.parseData(value, "$");
			if (w.size() == 1) {
				buf.append(value).append("|");
		    } else {
				StringBuffer value_buf = new StringBuffer();
				for (int n=0; n<w.size(); n++) {
					String s = (String) w.elementAt(n);
					value_buf.append(s);
					if (n <w.size()-1) {
						value_buf.append("$");
					}
				}
				String values = value_buf.toString();
                buf.append(values).append("|");
			}
		}
		String retstr = buf.toString();
		if (retstr.endsWith("|")) {
			retstr = retstr.substring(0, retstr.length()-1);
		}
		return retstr;
	}

    public String formatLine(String line, String source) {
	    int n = line.indexOf("|");
	    String code = line.substring(0, n);
	    StringBuffer buf = new StringBuffer();
		buf.append("<table>");
		buf.append("<tr class=\"textbody\">");
		buf.append("<td>");
		buf.append(code);
		buf.append("</td>");
		buf.append("<td>");
		String table = line2Table(line, source);
		buf.append(table);
		buf.append("</td>");
		buf.append("</tr>");
		buf.append("</table>");
		return buf.toString();
	}

	public String getHyperlink(String code) {
		//return "<a href=\"https://nciterms.nci.nih.gov/ncitbrowser/ConceptReport.jsp?dictionary=NCI_Thesaurus&code=" + code + "&ns=NCI_Thesaurus\">" + code + "</a>";
		return uiUtils.getHyperlink(code, code);
    }

	public String generate(String vsd_uri, String version, String source, Vector fields, Vector codes) {
		return generate(vsd_uri, version, source, fields, codes, MAX_RETURN);
	}

	public String generate(String vsd_uri, String version, String source, Vector fields, Vector codes, int maxReturn) {
		if (codes == null) {
		    codes = csdu.getCodesInCodingScheme(vsd_uri, null);
		}
        StringBuffer buf = new StringBuffer();
		Vector w = resolve(vsd_uri, version, source, fields, codes, maxReturn);
		HashMap fieldValueHmap = new HashMap();
		buf.append("<table width=\"900\">");
		for (int k=0; k<fields.size(); k++) {
			String field = (String) fields.elementAt(k);
			buf.append("<th class=\"textbody\" align=\"left\">").append("\n");
			if (source != null) {
				if (field.startsWith("Source ")) {
					field= field.replace("Source", source);
				}
			}
			buf.append(field).append("\n");
			buf.append("</th>").append("\n");
		}
		for (int i=0; i<w.size(); i++) {
			int j = i+1;
			String line = (String) w.elementAt(i);
			for (int k=0; k<fields.size(); k++) {
				String type = (String) fields.elementAt(k);
				String value = parseProperty(line, type, source);
				fieldValueHmap.put(type, value);
			}
			boolean isEven = UIUtils.isEven(i);
			String formatted_line = formatLine(fields, fieldValueHmap, isEven);
			buf.append(formatted_line).append("\n");
		}
		buf.append("</table>");
		return buf.toString();
	}

	public String generate(String vsd_uri, String version, String source, Vector fields, int maxReturn) {
		Vector codes = null;
		return generate(vsd_uri, version, source, fields, codes, maxReturn);
	}

	public String generate(String vsd_uri, String version, String source, Vector fields) {
		Vector codes = null;
		return generate(vsd_uri, version, source, fields, codes, MAX_RETURN);
	}

	public String generate(String vsd_uri, String version, Vector fields) {
        String source = getValueSetSupportedSource(vsd_uri);
        Vector codes = null;
		return generate(vsd_uri, version, source, fields, codes, MAX_RETURN);
	}

	public String generate(String vsd_uri, String version, Vector fields, int maxReturn) {
        String source = getValueSetSupportedSource(vsd_uri);
        Vector codes = null;
		return generate(vsd_uri, version, source, fields, codes, maxReturn);
	}

////////////////////////////////////////////////////////////////////////////////////////////////////
	public Vector export(String vsd_uri, String version, Vector fields) {
		Vector codes = null;
		String source = getValueSetSupportedSource(vsd_uri);
		return export(vsd_uri, version, source, fields, codes);
	}

	public Vector export(String vsd_uri, String version, String source, Vector fields) {
		Vector codes = null;
		return export(vsd_uri, version, source, fields, codes);
	}

	public Vector export(String vsd_uri, String version, String source, Vector fields, Vector codes) {
		if (codes == null) {
		    codes = csdu.getCodesInCodingScheme(vsd_uri, null);
		}
		HashMap fieldValueHmap = new HashMap();
        Vector retvec = new Vector();
		Vector w = resolve(vsd_uri, version, source, fields, codes, codes.size());
		StringBuffer buf = new StringBuffer();
		for (int k=0; k<fields.size(); k++) {
			String field = (String) fields.elementAt(k);
			buf.append(field);
			if (k <fields.size()-1) {
				buf.append("|");
			}
		}
		retvec.add(buf.toString());
		for (int i=0; i<w.size(); i++) {
			String line = (String) w.elementAt(i);
			for (int k=0; k<fields.size(); k++) {
				String type = (String) fields.elementAt(k);
				String value = parseProperty(line, type, source);
				fieldValueHmap.put(type, value);
			}
			String formatted_line = formatLine(fields, fieldValueHmap);
            retvec.add(formatted_line);
		}
		return retvec;
	}

    public ValueSetDefinition findValueSetDefinitionByURI(String uri) {
		if (uri == null) return null;
		String valueSetDefinitionRevisionId = null;
		try {
			ValueSetDefinition vsd = vsd_service.getValueSetDefinition(new URI(uri), valueSetDefinitionRevisionId);
			return vsd;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

    public String getValueSetSupportedSource(String uri) {
		ValueSetDefinition vsd = findValueSetDefinitionByURI(uri);
		Mappings mappings = vsd.getMappings();
		SupportedSource[] supporetedSources = mappings.getSupportedSource();
		for (int i=0; i<supporetedSources.length; i++) {
			SupportedSource supportedSource = supporetedSources[i];
			return supportedSource.getContent();
		}
        return null;
	}

//=================================================================================================================
    public String getVarName(String colName) {
		if (!vsHeading2VarHashMap.containsKey(colName)) {
			return null;
		}
		return (String) vsHeading2VarHashMap.get(colName);
	}

	public Vector getVars(String line) {
		Vector w = new Vector();
		Vector u = gov.nih.nci.evs.browser.utils.StringUtils.parseData(line);
		for (int i=0; i<u.size(); i++) {
			String heading = (String) u.elementAt(i);
			String varName = getVarName(heading);
			w.add(varName);
		}
		return w;
	}

	public ArrayList delimited2List(String delimitedStr) {
		if (delimitedStr == null) return null;
		ArrayList a = new ArrayList();
		Vector u = gov.nih.nci.evs.browser.utils.StringUtils.parseData(delimitedStr, '$');
		u = removeDuplicateValues(u);
		u = SortUtils.quickSort(u);
		for (int i=0; i<u.size(); i++) {
			String t = (String) u.elementAt(i);
			a.add(t);
		}
		return a;
	}

	public Vector removeDuplicateValues(Vector v) {
		Vector w = new Vector();
		HashSet hset = new HashSet();
		for (int i=0; i<v.size(); i++) {
			String t = (String) v.elementAt(i);
			if (!hset.contains(t)) {
				hset.add(t);
				w.add(t);
			}
		}
		return w;
	}

    public ValueSet instantiateValueSet(String vsd_uri, String version, Vector fields) {
		Vector vs_data = export(vsd_uri, version, fields);
		ValueSet vs = new ValueSet();
		ArrayList concepts =new ArrayList();
		String heading_line = (String) vs_data.elementAt(0);
		Vector var_vec = getVars(heading_line);
		for (int i=1; i<vs_data.size(); i++) {
			String line = (String) vs_data.elementAt(i);
			Vector u = gov.nih.nci.evs.browser.utils.StringUtils.parseData(line, '|');
			gov.nih.nci.evs.browser.bean.Concept c = new gov.nih.nci.evs.browser.bean.Concept();
			for (int j=0; j<var_vec.size(); j++) {
				String var = (String) var_vec.elementAt(j);
				String value = (String) u.elementAt(j);
                if (var.compareTo(NCITCODE) == 0) {
					c.setNcitCode(value);
                } else if (var.compareTo(SOURCEPREFERREDTERM) == 0) {
					c.setSourcePreferredTerm(value);
                } else if (var.compareTo(NCITPREFERREDTERM) == 0) {
					c.setNcitPreferredTerm(value);
				} else {
					ArrayList a = delimited2List(value);
					if (var.compareTo(NCITSYNONYMS) == 0) {
						c.setNcitSynonyms(a);
					} else if (var.compareTo(SOURCESYNONYMS) == 0) {
						c.setSourceSynonyms(a);
					} else if (var.compareTo(NCITDEFINITIONS) == 0) {
						c.setNcitDefinitions(a);
					} else if (var.compareTo(SOURCEDEFINITIONS) == 0) {
						c.setSourceDefinitions(a);
					}
				}
			}
			concepts.add(c);
		}
		vs.setConcepts(concepts);
        return vs;
    }


	public String object2XMLStream(ValueSet vs) {
		XStream xstream_xml = new XStream(new DomDriver());
		String xml = XML_DECLARATION + "\n" + xstream_xml.toXML(vs);
		return xml;
	}


    public String get_rvs_tbl(String vsd_uri) {
		return get_rvs_tbl(vsd_uri, null);
	}

    public String get_rvs_tbl(String vsd_uri, Vector codes) {
		String rvs_tbl = null;
		String supported_source = vsmdu.getValueSetSupportedSource(vsd_uri);
		CodingSchemeDataUtils csdu = new CodingSchemeDataUtils(lbSvc);
		String metadata = vsmdu.getValueSetDefinitionMetadata(vsd_uri);
		Vector u = gov.nih.nci.evs.browser.utils.StringUtils.parseData(metadata);
		String defaultCodingScheme = (String) u.elementAt(6);
		boolean non_ncit_source = true;
		if (supported_source == null || supported_source.compareTo("null") == 0 || supported_source.compareTo("NCI") == 0) {
			non_ncit_source = false;
		}
		if (codes == null) {
			codes = new Vector();
			ResolvedConceptReferencesIterator rcri = null;
			boolean resolveObjects = false;
			try {
				rcri = csdu.resolveCodingScheme(vsd_uri, null, resolveObjects);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (rcri != null) {
				try {
					int numberRemaining = rcri.numberRemaining();
					while (rcri.hasNext()) {
						rcri = rcri.scroll(maxToReturn);
						ResolvedConceptReferenceList rcrl = rcri.getNext();
						ResolvedConceptReference[] rcra =
							rcrl.getResolvedConceptReference();
						for (int i = 0; i < rcra.length; i++) {
							ResolvedConceptReference rcr = rcra[i];
							codes.add(rcr.getCode());
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		ValueSetFormatter formatter = new ValueSetFormatter(lbSvc, vsd_service);
		Vector fields = formatter.getDefaultFields(non_ncit_source);
		rvs_tbl = formatter.generate(defaultCodingScheme, null, supported_source, fields, codes, codes.size());
		return rvs_tbl;
	}

}
