package gov.nih.nci.evs.browser.utils;

import java.io.*;
import java.util.*;
import java.net.*;

import java.util.*;
import java.io.*;
import java.util.Map.Entry;
import gov.nih.nci.evs.browser.bean.*;
import gov.nih.nci.evs.browser.common.*;
import gov.nih.nci.evs.browser.properties.*;

import org.LexGrid.concepts.*;
import org.LexGrid.LexBIG.Utility.Iterators.*;
import org.LexGrid.LexBIG.DataModel.Core.*;

import java.util.Enumeration;
import org.LexGrid.LexBIG.DataModel.Collections.AssociationList;
import org.LexGrid.LexBIG.DataModel.Collections.ConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.AssociatedConcept;
import org.LexGrid.LexBIG.DataModel.Core.Association;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.NameAndValue;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.ActiveOption;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.ConvenienceMethods;
import org.LexGrid.LexBIG.caCore.interfaces.LexEVSApplicationService;
import org.LexGrid.commonTypes.EntityDescription;
import org.LexGrid.commonTypes.Property;
import org.LexGrid.commonTypes.PropertyQualifier;
import org.LexGrid.commonTypes.Source;
import org.LexGrid.concepts.Comment;
import org.LexGrid.concepts.Definition;
import org.LexGrid.concepts.Entity;
import org.LexGrid.concepts.Presentation;

import org.apache.log4j.*;
import gov.nih.nci.evs.browser.common.Constants;
import org.LexGrid.codingSchemes.*;
import org.LexGrid.naming.SupportedProperty;
import org.LexGrid.LexBIG.DataModel.Collections.SortOptionList;
import org.apache.commons.lang.*;

import org.LexGrid.LexBIG.Extensions.Generic.LexBIGServiceConvenienceMethods;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.RenderingDetail;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.descriptors.RenderingDetailDescriptor;
import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeTagList;
import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.DataModel.Collections.NameAndValueList;
import org.LexGrid.LexBIG.DataModel.Core.NameAndValue;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.lexevs.property.PropertyExtension;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeGraph;


public class PropertyData
{
	private static boolean owl2_layout = false;
    private static Logger _logger = Logger.getLogger(PropertyData.class);
    private LexBIGService lbSvc = null;
    private ConceptDetails conceptDetails = null;
    private HistoryUtils histUtils = null;

// Variable declaration
	private Entity curr_concept;
	private String codingScheme;
	private String version;
	private String code;
	private String namespace;
	private Boolean isActive;
	private String concept_status;
	private Vector properties_to_display;
	private Vector properties_to_display_label;
	private Vector properties_to_display_url;
	private Vector properties_to_display_linktext;
	private Vector additionalproperties;
	private Vector external_source_codes;
	private Vector external_source_codes_label;
	private Vector external_source_codes_url;
	private Vector external_source_codes_linktext;
	private Vector descendantCodes;
	private HashMap propertyName2ValueHashMap;
	private HashMap propertyQualifierHashMap;
	private HashMap displayLabel2PropertyNameHashMap;


	private List displayItemList = null;

	private Vector displayed_properties = null;

	private HashMap defSourceMapping = null;

	private String vocabulary_format = null;
	private String is_defined = "No";

	boolean show_status = false;

	String dictionary = null;
	String formalName = null;

	//private static String _ncitUrl = null;
    private static String NCIT_URL = null;
        //_ncitUrl + "/ConceptReport.jsp?dictionary=NCI%20Thesaurus&";
    private Vector presentation_vec = null;
    private String concept_id = null;

    private HashMap relationshipHashMap = null;
    private String owl_role_quantifiers = null;
    RelationshipTabFormatter formatter = null;//new RelationshipTabFormatter(lbSvc);


// Constructor
	public PropertyData(LexBIGService lbSvc, String dictionary, String version) {
        this.lbSvc = lbSvc;
        formatter = new RelationshipTabFormatter(lbSvc);
        this.dictionary = dictionary;
        this.version = version;

        this.codingScheme = dictionary;

        this.conceptDetails = new ConceptDetails(lbSvc);
        this.histUtils = new HistoryUtils(lbSvc);

        this.additionalproperties = new Vector();
        this.displayed_properties = new Vector();
	}

	public void set_owl_role_quantifiers(String owl_role_quantifiers) {
		this.owl_role_quantifiers = owl_role_quantifiers;
	}

	public HistoryUtils getHistoryUtils() {
		return this.histUtils;
	}

    public ConceptDetails getConceptDetails() {
		return this.conceptDetails;
	}


// Set methods

	public void setRelationshipHashMap(HashMap relationshipHashMap) {
        this.relationshipHashMap = relationshipHashMap;
	}

    public void setNCIT_URL(String NCIT_URL) {
		this.NCIT_URL = NCIT_URL;
	}

    public void setFormalName(String formalName) {
		this.formalName = formalName;
	}

	public void setDisplayItemList(List displayItemList) {
		this.displayItemList = displayItemList;
	}

	public void setDefSourceMapping(HashMap defSourceMapping) {
		this.defSourceMapping = defSourceMapping;
	}

	public boolean isDefinition(String label) {
		String t = label.toLowerCase();
		if (label.indexOf("definition") != -1) {
			return true;
		}
		return false;
	}

	public boolean isDefinitionWithSource(String label) {
		String t = label.toLowerCase();
		if (label.indexOf("definition") != -1 && label.indexOf("|") != -1) {
			return true;
		}
		return false;
	}

	public Vector get_presentation_vec() {
		return presentation_vec;
	}

	public Vector getadditionalproperties() {
		return this.additionalproperties;
	}

	public HashMap getPropertyQualifierHashMap() {
		return this.propertyQualifierHashMap;
	}

	public HashMap addToHashMap(HashMap hmap, String key, String value) {
		if (hmap == null) {
			hmap = new HashMap();
		}
		Vector v = new Vector();
		if (hmap.containsKey(key)) {
			v = (Vector) hmap.get(key);
		}
		v.add(value);
		hmap.put(key, v);
		return hmap;
	}

	public void setCurr_concept(Entity curr_concept) {

		this.curr_concept = curr_concept;
		this.code = curr_concept.getEntityCode();
		this.namespace = curr_concept.getEntityCodeNamespace();
		this.isActive = curr_concept.isIsActive();
		this.additionalproperties = new Vector();
		this.additionalproperties.add("CONCEPT_NAME");
		this.additionalproperties.add("primitive");
		this.concept_status = conceptDetails.getConceptStatus(codingScheme, version, null, curr_concept.getEntityCode());
		this.displayLabel2PropertyNameHashMap = new HashMap();

		if (concept_status != null) {
		   concept_status = concept_status.replaceAll("_", " ");
		   if (concept_status.compareToIgnoreCase("active") == 0 || concept_status.compareToIgnoreCase("reviewed") == 0) concept_status = null;
		}
		this.propertyName2ValueHashMap = conceptDetails.getPropertyName2ValueHashMap(curr_concept);
		this.propertyQualifierHashMap = conceptDetails.getPropertyQualifierHashMap(curr_concept);
		Vector propertyvalues = new Vector();
		concept_id = curr_concept.getEntityCode();
		propertyvalues.add(concept_id);
		propertyName2ValueHashMap.put("Code", propertyvalues);

		properties_to_display = new Vector();
		properties_to_display_label = new Vector();
		properties_to_display_url = new Vector();
		properties_to_display_linktext = new Vector();

		presentation_vec = conceptDetails.getPresentationProperties(curr_concept);

		for (int i=0; i<presentation_vec.size(); i++) {
			//name$value$isPreferred
			String t = (String) presentation_vec.elementAt(i);
			Vector w = gov.nih.nci.evs.browser.utils.StringUtils.parseData(t, "$");
			String presentation_name = (String) w.elementAt(0);
			String presentation_value = (String) w.elementAt(1);

			String isPreferred = (String) w.elementAt(2);
			if (isPreferred.compareTo("true") == 0) {
				properties_to_display.add(presentation_name);
				String display_label = presentation_name.replaceAll("_", " ");
				properties_to_display_label.add(display_label);
displayLabel2PropertyNameHashMap = addToHashMap(displayLabel2PropertyNameHashMap, display_label, presentation_name);
				properties_to_display_url.add(null);
				properties_to_display_linktext.add(null);
		    }
		}
		for (int i=0; i<displayItemList.size(); i++) {
			DisplayItem displayItem = (DisplayItem) displayItemList.get(i);
			if (!displayItem.getIsExternalCode() && !properties_to_display.contains( displayItem.getPropertyName() )) {
				properties_to_display.add(displayItem.getPropertyName());
				properties_to_display_label.add(displayItem.getItemLabel());
				//displayLabel2PropertyNameHashMap.put(displayItem.getItemLabel(), displayItem.getPropertyName());
				properties_to_display_url.add(displayItem.getUrl());
				properties_to_display_linktext.add(displayItem.getHyperlinkText());
		    }
displayLabel2PropertyNameHashMap = addToHashMap(displayLabel2PropertyNameHashMap, displayItem.getItemLabel(), displayItem.getPropertyName());
		}
		this.external_source_codes = new Vector();
		this.external_source_codes_label = new Vector();
		this.external_source_codes_url = new Vector();
		this.external_source_codes_linktext = new Vector();

		for (int i=0; i<displayItemList.size(); i++) {
			DisplayItem displayItem = (DisplayItem) displayItemList.get(i);
			if (displayItem.getIsExternalCode()) {
				external_source_codes.add(displayItem.getPropertyName());
				external_source_codes_label.add(displayItem.getItemLabel());
				external_source_codes_url.add(displayItem.getUrl());
				external_source_codes_linktext.add(displayItem.getHyperlinkText());
			}
		}

		if (codingScheme.compareTo(Constants.NCIT_CS_NAME) == 0 || codingScheme.compareTo(Constants.NCI_METATHESAURUS) == 0) {
			descendantCodes = histUtils.getDescendantCodes(codingScheme, null, null, curr_concept.getEntityCode());
		}

		getTermsAndPropertiesData(curr_concept);
	}

	public HashMap getDisplayLabel2PropertyNameHashMap() {
		return this.displayLabel2PropertyNameHashMap;
	}


	public void dumpVector(String label, Vector v) {
		System.out.println(label);
		if (v == null) return;
		for (int i=0; i<v.size(); i++) {
			String t = (String) v.elementAt(i);
			System.out.println("\t" + t);
		}
	}


	public void dumpHashMap(String label, HashMap hmap) {
		System.out.println(label);
		if (hmap == null) return;
		Iterator iterator = hmap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry thisEntry = (Entry) iterator.next();
			String prop_name = (String) thisEntry.getKey();
			Vector value_vec = (Vector) thisEntry.getValue();
			for (int k=0; k<value_vec.size(); k++) {
				String value = (String) value_vec.elementAt(k);
				System.out.println(prop_name + " -> " + value);
			}
			System.out.println("\n");
		}

	}

	public void dumpData() {
		System.out.println("Concept: " + curr_concept.getEntityDescription().getContent());
		System.out.println("code: " + code);
		System.out.println("namespace: " + namespace);
		System.out.println("isActive: " + isActive);
		System.out.println("concept_status: " + concept_status);

		dumpVector("additionalproperties: ", additionalproperties);
		dumpHashMap("propertyName2ValueHashMap", propertyName2ValueHashMap);
        dumpVector("properties_to_display: ", properties_to_display);
        dumpVector("properties_to_display_label: ", properties_to_display_label);
        dumpVector("properties_to_display_url: ", properties_to_display_url);
        dumpVector("properties_to_display_linktext: ", properties_to_display_linktext);

		Vector presentation_vec = conceptDetails.getPresentationProperties(curr_concept);
		dumpVector("presentation_vec: ", presentation_vec);


		for (int i=0; i<presentation_vec.size(); i++) {
			//name$value$isPreferred
			String t = (String) presentation_vec.elementAt(i);
			Vector w = gov.nih.nci.evs.browser.utils.StringUtils.parseData(t, "$");
			String presentation_name = (String) w.elementAt(0);
			String presentation_value = (String) w.elementAt(1);
			String isPreferred = (String) w.elementAt(2);
			if (isPreferred.compareTo("true") == 0) {
				properties_to_display.add(presentation_name);
				properties_to_display_label.add(presentation_name.replaceAll("_", " "));
				properties_to_display_url.add(null);
				properties_to_display_linktext.add(null);
		    }
		}

		for (int i=0; i<displayItemList.size(); i++) {
			DisplayItem displayItem = (DisplayItem) displayItemList.get(i);
			if (!displayItem.getIsExternalCode() && !properties_to_display.contains( displayItem.getPropertyName() )) {
				properties_to_display.add(displayItem.getPropertyName());
				properties_to_display_label.add(displayItem.getItemLabel());
				properties_to_display_url.add(displayItem.getUrl());
				properties_to_display_linktext.add(displayItem.getHyperlinkText());
		    }
		}

		this.external_source_codes = new Vector();
		this.external_source_codes_label = new Vector();
		this.external_source_codes_url = new Vector();
		this.external_source_codes_linktext = new Vector();

		for (int i=0; i<displayItemList.size(); i++) {
			DisplayItem displayItem = (DisplayItem) displayItemList.get(i);
			if (displayItem.getIsExternalCode()) {
				external_source_codes.add(displayItem.getPropertyName());
				external_source_codes_label.add(displayItem.getItemLabel());
				external_source_codes_url.add(displayItem.getUrl());
				external_source_codes_linktext.add(displayItem.getHyperlinkText());
			}
		}

		if (codingScheme.compareTo(Constants.NCIT_CS_NAME) == 0 || codingScheme.compareTo(Constants.NCI_METATHESAURUS) == 0) {
			descendantCodes = histUtils.getDescendantCodes(codingScheme, null, null, curr_concept.getEntityCode());
		}

	}


	public void setCodingScheme(String codingScheme) {
		this.codingScheme = codingScheme;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public void setConcept_status(String concept_status) {
		this.concept_status = concept_status;
	}

	public void setProperties_to_display(Vector properties_to_display) {
		this.properties_to_display = properties_to_display;
	}

	public void setProperties_to_display_label(Vector properties_to_display_label) {
		this.properties_to_display_label = properties_to_display_label;
	}

	public void setProperties_to_display_url(Vector properties_to_display_url) {
		this.properties_to_display_url = properties_to_display_url;
	}

	public void setProperties_to_display_linktext(Vector properties_to_display_linktext) {
		this.properties_to_display_linktext = properties_to_display_linktext;
	}

	public void setAdditionalproperties(Vector additionalproperties) {
		this.additionalproperties = additionalproperties;
	}

	public void setExternal_source_codes(Vector external_source_codes) {
		this.external_source_codes = external_source_codes;
	}

	public void setExternal_source_codes_label(Vector external_source_codes_label) {
		this.external_source_codes_label = external_source_codes_label;
	}

	public void setExternal_source_codes_url(Vector external_source_codes_url) {
		this.external_source_codes_url = external_source_codes_url;
	}

	public void setExternal_source_codes_linktext(Vector external_source_codes_linktext) {
		this.external_source_codes_linktext = external_source_codes_linktext;
	}

	public void setDescendantCodes(Vector descendantCodes) {
		this.descendantCodes = descendantCodes;
	}

	public void setPropertyName2ValueHashMap(HashMap propertyName2ValueHashMap) {
		this.propertyName2ValueHashMap = propertyName2ValueHashMap;
	}


// Get methods
    public String get_concept_id() {
		return this.concept_id;
	}

	public Entity getCurr_concept() {
		return this.curr_concept;
	}

	public String getCodingScheme() {
		return this.codingScheme;
	}

	public String getVersion() {
		return this.version;
	}

	public String getCode() {
		return this.code;
	}

	public String getNamespace() {
		return this.namespace;
	}

	public Boolean getIsActive() {
		return this.isActive;
	}

	public String getConcept_status() {
		return this.concept_status;
	}

	public Vector getProperties_to_display() {
		return this.properties_to_display;
	}

	public Vector getProperties_to_display_label() {
		return this.properties_to_display_label;
	}

	public Vector getProperties_to_display_url() {
		return this.properties_to_display_url;
	}

	public Vector getProperties_to_display_linktext() {
		return this.properties_to_display_linktext;
	}

	public Vector getAdditionalproperties() {
		return this.additionalproperties;
	}

	public Vector getExternal_source_codes() {
		return this.external_source_codes;
	}

	public Vector getExternal_source_codes_label() {
		return this.external_source_codes_label;
	}

	public Vector getExternal_source_codes_url() {
		return this.external_source_codes_url;
	}

	public Vector getExternal_source_codes_linktext() {
		return this.external_source_codes_linktext;
	}

	public Vector getDescendantCodes() {
		return this.descendantCodes;
	}

	public HashMap getPropertyName2ValueHashMap() {
		return this.propertyName2ValueHashMap;
	}


	public static String getDisplayLink(HashMap<String, String> label2URL,
		HashMap<String, String> label2Linktext, String label, String value) {
		String url = (String) label2URL.get(label);
		if (url == null) return "";
		String linktext = (String) label2Linktext.get(label);
		String encoded_value = value;
		encoded_value = encoded_value.replaceAll(":", "%3A");
		String url_str = url + encoded_value;
		// Note: Escaped " character.
		String val =  "<a href=\"javascript:redirect_site('"
		+ url_str + "')\">(" + linktext + ")</a>";
		return val;
	}


	public Vector getOtherProperties() {
		if (curr_concept == null) return null;
		Vector other_properties = new Vector();
		boolean hasOtherProperties = false;
		Vector prop_name_value_vec = new Vector();
		Iterator iterator = propertyName2ValueHashMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry thisEntry = (Entry) iterator.next();
			String prop_name = (String) thisEntry.getKey();
			Vector value_vec = (Vector) thisEntry.getValue();
			for (int k=0; k<value_vec.size(); k++) {
				String value = (String) value_vec.elementAt(k);
				prop_name_value_vec.add(prop_name + "|" + value);
			}
		}
		for (int k=0; k<prop_name_value_vec.size(); k++) {
			String prop_name_value = (String) prop_name_value_vec.elementAt(k);
			Vector w = gov.nih.nci.evs.browser.utils.StringUtils.parseData( prop_name_value );
			String prop_name = (String) w.elementAt(0);
			if (!displayed_properties.contains(prop_name) && !additionalproperties.contains(prop_name)) {
                other_properties.add(prop_name_value);
			}
		}
		return other_properties;
	}


    public void getTermsAndPropertiesData(Entity curr_concept) {
        getConceptStatusData(curr_concept);
        getDescendantCodes(curr_concept);

	}

	public Vector getDescendantCodes(Entity curr_concept) {
	    Vector descendantCodes = histUtils.getDescendantCodes(dictionary, null, null, curr_concept.getEntityCode());
	    return descendantCodes;
	}


	public void getConceptStatusData(Entity curr_concept) {
		isActive = curr_concept.isIsActive();
		show_status = false;
		if (concept_status == null) {
			concept_status = conceptDetails.getConceptStatus(dictionary, version, null, curr_concept.getEntityCode());
			if (concept_status != null) {
			   concept_status = concept_status.replaceAll("_", " ");
			   if (concept_status.compareToIgnoreCase("active") == 0 || concept_status.compareToIgnoreCase("reviewed") == 0) concept_status = null;
			}
		}
		if (isActive != null ) {
			 if (!isActive.equals(Boolean.TRUE) && concept_status == null) {
				 concept_status = "Inactive";
			 }
		}
		if (concept_status != null) {
			if (concept_status.compareToIgnoreCase("Retired Concept")  == 0 || concept_status.compareToIgnoreCase("Obsolete Concept") == 0) {
				show_status = true;
			}
		}
	}

	public boolean get_show_status() {
		return show_status;
	}


	public void getLabelData(Entity curr_concept) {
		int other_src_alt_def_count = 0;
		Vector nci_def_label_value = new Vector();
		Vector non_nci_def_label_value = new Vector();
		Vector other_label_value = new Vector();
		boolean is_definition = false;
		Vector ncim_metathesaurus_cui_vec = new Vector();
		HashSet ncim_metathesaurus_cui_hset = new HashSet();

		String ncim_cui_propName = "NCI_META_CUI";
		String ncim_cui_propName_label = null;
		String ncim_cui_prop_url = null;
		String ncim_cui_prop_linktext = null;
		Vector ncim_cui_code_vec = new Vector();


		HashMap<String, String> label2URL = new HashMap<String, String>();
		HashMap<String, String> label2Linktext = new HashMap<String, String>();
		for (int m=0; m<properties_to_display.size(); m++) {
			String propName = (String) properties_to_display.elementAt(m);
			String prop_nm_label = (String) properties_to_display_label.elementAt(m);
			String prop_url = (String) properties_to_display_url.elementAt(m);
			String prop_linktext = (String) properties_to_display_linktext.elementAt(m);
			if (prop_url != null) {
				label2URL.put(prop_nm_label, prop_url);
				label2Linktext.put(prop_nm_label, prop_linktext);
			}
		}

		for (int i=0; i<properties_to_display.size(); i++) {
			String propName = (String) properties_to_display.elementAt(i);
			String propName_label = (String) properties_to_display_label.elementAt(i);

			if (propName_label.compareTo("NCI Thesaurus Code") == 0  && propName.compareTo("NCI_THESAURUS_CODE") != 0) {
				//String formalName = dataUtils.getFormalName(dictionary);
				if (formalName == null) {
					formalName = dictionary;
				}
				propName_label = formalName + " Code";
			}

			String propName_label2 = propName_label;
			String url = (String) properties_to_display_url.elementAt(i);
			String linktext = (String) properties_to_display_linktext.elementAt(i);

			if (propName.compareTo(ncim_cui_propName) == 0) {
				ncim_cui_propName_label = propName_label;
				ncim_cui_prop_url = url;
				ncim_cui_prop_linktext = linktext;
				Vector ncim_cui_code_vec_temp = conceptDetails.getPropertyValues(
				curr_concept, "GENERIC", propName);
				if (ncim_cui_code_vec_temp != null) {
					for (int lcv=0; lcv<ncim_cui_code_vec_temp.size(); lcv++) {
						String t = (String) ncim_cui_code_vec_temp.elementAt(lcv);
						ncim_cui_code_vec.add(t);
					}
				}
			}

			String qualifier = "";
			if (propName_label.indexOf("Synonyms") == -1) {
				displayed_properties.add(propName);
				Vector value_vec = (Vector) propertyName2ValueHashMap.get(propName);
				if (value_vec != null && value_vec.size() > 0) {
					int k = -1;
					for (int j=0; j<value_vec.size(); j++) {
						String value = (String) value_vec.elementAt(j);
						k++;
						if (propName.compareTo("NCI_META_CUI") == 0) {
							ncim_cui_code_vec.add(value);
						}
						if(propName_label.compareTo("Definition") == 0) {
							String value_pre = value;
							value = reformatPDQDefinition(value);
							String value_post = value;
							if (value_pre.compareTo(value_post) != 0 && !value_post.endsWith("PDQ")) {
								System.out.println("WARNING -- possible definition formatting issue with " + value_pre);
							}
						}

						String value_wo_qualifier = value;
						int n = value.indexOf("|");
						is_definition = false;
						if (n != -1 && (propName_label.indexOf("Definition") != -1 || propName_label.indexOf("DEFINITION") != -1
						    || propName_label.indexOf("definition") != -1)) {
						    is_definition = true;
						    Vector def_vec = gov.nih.nci.evs.browser.utils.StringUtils.parseData(value);
						    value_wo_qualifier = (String) def_vec.elementAt(0);
						    qualifier = "";
						    if (def_vec.size() > 1) {
						        qualifier = (String) def_vec.elementAt(1);
						    }
							if (defSourceMapping != null && defSourceMapping.containsKey(qualifier)) {
								String def_source_display_value = (String) defSourceMapping.get(qualifier);
								value = value_wo_qualifier + " (" + qualifier + ")";
								propName_label = def_source_display_value + " " + propName_label2;
							} else {
								if (qualifier.indexOf("PDQ") != -1) {
									//value = JSPUtils.reformatPDQDefinition(value);
								} else if (qualifier.compareTo("NCI") != 0) {
									value = value_wo_qualifier;
									propName_label = qualifier + " " + propName_label2;
								} else if (qualifier.compareTo("NCI") == 0 && propName.compareTo("ALT_DEFINITION") == 0) {
									value = value_wo_qualifier;
									if (other_src_alt_def_count > 0) {
										propName_label = qualifier + " " + propName_label2;
									} else {
										propName_label = propName_label2;
									}
								} else if (qualifier.compareTo("NCI") == 0 && propName.compareTo("ALT_DEFINITION") != 0) {
									value = value_wo_qualifier;
									propName_label = propName_label2;
								} else {
									value = value_wo_qualifier;
								}
							}
							if (qualifier.compareToIgnoreCase("NCI") == 0) {
								nci_def_label_value.add(propName_label2 + "|" + value);
							} else {
								non_nci_def_label_value.add(propName_label + "|" + value);
							}
						}

						if (propName_label.indexOf("textualPresentation") == -1) {
							if (!is_definition) {
								other_label_value.add(propName_label + "|" + value);
							}
						}
					}
				}
			}
		}

	}

    public Vector findOtherPropertyNames() {
		Vector prop_name_vec = new Vector();
		Iterator iterator = propertyName2ValueHashMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry thisEntry = (Entry) iterator.next();
			String prop_name = (String) thisEntry.getKey();
			if (!displayed_properties.contains(prop_name) && !additionalproperties.contains(prop_name)) {
				prop_name_vec.add(prop_name);
			}
		}
		return SortUtils.quickSort(prop_name_vec);
	}

	public void add_displayed_property(String property_name) {
		if (!displayed_properties.contains(property_name)) {
			displayed_properties.add(property_name);
		}
	}


///////////////////////////////////////////////////////////////////////////////////////////////
// other property table
///////////////////////////////////////////////////////////////////////////////////////////////

    public String generatePropertyTable(Entity concept, Vector property_names, String description) {
		UIUtils uiUtils = new UIUtils();
		uiUtils.set_owl_role_quantifiers(owl_role_quantifiers);
		return uiUtils.generatePropertyTable(concept, property_names, description, 2);
	}




///////////////////////////////////////////////////////////////////////////////////////////////
// relationship tables
///////////////////////////////////////////////////////////////////////////////////////////////

/*
    public static final String TYPE_ROLE = "type_role";
    public static final String TYPE_ASSOCIATION = "type_association";
    public static final String TYPE_SUPERCONCEPT = "type_superconcept";
    public static final String TYPE_SUBCONCEPT = "type_subconcept";
    public static final String TYPE_INVERSE_ROLE = "type_inverse_role";
    public static final String TYPE_INVERSE_ASSOCIATION = "type_inverse_association";
*/


    public static final String NCI_THESAURUS = "NCI Thesaurus";
    public static final String NCIT_CS_NAME = "NCI_Thesaurus";

    public String generateRelationshipTable(String codingScheme, String version, String code, String namespace, String rel_type) {
		return generateRelationshipTable(codingScheme, version, code, namespace, rel_type, false);
	}

	public boolean isNCIT(String codingScheme) {
		if (codingScheme == null) return false;
		if (codingScheme.compareTo(Constants.NCI_THESAURUS) == 0 ||
            codingScheme.compareTo(Constants.NCIT_CS_NAME) == 0) {
			return true;
		}
		return false;
	}


    public String generateRelationshipTable(String codingScheme, String version, String code, String namespace, String rel_type, boolean display_qualifiers) {
        boolean display_equiv_expression = false;
        String equivalanceClass = null;
        String retstr = null;
        if (isNCIT(codingScheme) && rel_type.compareTo(Constants.TYPE_ROLE) == 0) {
			try {
				equivalanceClass = new CodingSchemeDataUtils(lbSvc).getEquivalenceExpression(codingScheme, version, code);
				if (equivalanceClass != null) {
					display_equiv_expression = true;
				}
		    } catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		StringBuffer buf = new StringBuffer();
		if (display_equiv_expression) {
			String expression = new ExpressionParser(lbSvc).infixExpression2Text(codingScheme, version, equivalanceClass);
			expression = new ExpressionFormatter().reformat(expression);
			buf.append(expression);
		}
		if (isNCIT(codingScheme) && rel_type.compareTo(Constants.TYPE_ROLE) == 0) {
			ArrayList roles = null;
			String formattedTable = null;
			if (relationshipHashMap != null) {
				roles = (ArrayList) relationshipHashMap.get(Constants.TYPE_ROLE);
				formattedTable = formatter.formatOutboundRoleTable(roles);
			} else {
			    formattedTable = formatter.formatOutboundRoleTable(codingScheme, version, code, codingScheme);
			}
			buf.append("<p></p>");
			buf.append(formattedTable);
			buf.append("<p></p>");
			return buf.toString();
		} else if (isNCIT(codingScheme) && rel_type.compareTo(Constants.TYPE_INVERSE_ROLE) == 0) {
			ArrayList roles = null;
			String formattedTable = null;
			if (relationshipHashMap != null) {
				roles = (ArrayList) relationshipHashMap.get(Constants.TYPE_INVERSE_ROLE);
				formattedTable = formatter.formatInboundRoleTable(roles);
			} else {
			    formattedTable = formatter.formatInboundRoleTable(codingScheme, version, code, codingScheme);
			}
			buf.append("<p></p>");
			buf.append(formattedTable);
			buf.append("<p></p>");
			return buf.toString();
		}
		return generateRelationshipTable(codingScheme, version, code, namespace, rel_type, display_qualifiers, null);
	}

    public String generateRelationshipTable(String codingScheme, String version, String code, String namespace, String rel_type,
        boolean display_qualifiers, ArrayList list) {
        if (list == null) {
			HashMap hmap = null;
			if (relationshipHashMap != null) {
				hmap = relationshipHashMap;
			} else {
				hmap = new RelationshipUtils(lbSvc).getRelationshipHashMap(codingScheme, version, code, namespace, true);
			}
			list = (ArrayList) hmap.get(rel_type);
	    }

		UIUtils uiUtils = new UIUtils();
		uiUtils.set_owl_role_quantifiers(owl_role_quantifiers);

        String defaultLabel = null;
        boolean isEmpty = false;
        if (list == null || list.size() == 0) {
			isEmpty = true;
		}
        String description = uiUtils.getRelationshipTableLabel(defaultLabel, rel_type, isEmpty);
        if (isEmpty) return description;

		String firstColumnHeading = null;
		String secondColumnHeading = null;

		int	firstPercentColumnWidth = 40;
		int	secondPercentColumnWidth = 60;
		int qualifierColumn = 2;

		firstColumnHeading = "Relationship";
		secondColumnHeading = "Value (qualifiers indented underneath)";

		if (rel_type.startsWith("type_inverse") || rel_type.compareTo("type_subconcept") == 0) {
		    firstPercentColumnWidth = 60;
		    secondPercentColumnWidth = 40;
		    qualifierColumn = 1;

			secondColumnHeading = "Relationship";
			firstColumnHeading = "Value (qualifiers indented underneath)";
		}

		if (!display_qualifiers) {
			qualifierColumn = 0;
			if (rel_type.startsWith("type_inverse") || rel_type.compareTo("type_subconcept") == 0) {
				firstColumnHeading = "Value";
				secondColumnHeading = "Relationship";
			} else {
				secondColumnHeading = "Value";
				firstColumnHeading = "Relationship";
			}
		}

		HTMLTableSpec spec = null;
		try {
			spec = uiUtils.relationshipList2HTMLTableSpec(
				description,
				firstColumnHeading,
				secondColumnHeading,
				firstPercentColumnWidth,
				secondPercentColumnWidth,
				qualifierColumn,
				list);

			return uiUtils.generateHTMLTable(spec, codingScheme, version, rel_type);
		} catch (Exception ex) {
			System.out.println("Exception: UIUtils.relationshipList2HTMLTableSpec");
		}
		return "";
	}



///////////////////////////////////////////////////////////////////////////////////////////////
// format definition
///////////////////////////////////////////////////////////////////////////////////////////////

    public Vector<String> findHyperlinks(String t, String target) {
        Vector<String> v = new Vector<String>();
        boolean found = false;
        // "aspx?"
        String t1 = t;
        String t2 = t;
        String doubleQuote = "\"";
        String t6 = null;
        String replacedWith = null;
        String t5 = null;

        for (int i = 0; i < t.length() - target.length(); i++) {
            String substr = t.substring(i, i + target.length());
            if (substr.compareTo(target) == 0) {
                found = true;
                t1 = t.substring(0, i);
                int k1 = i;
                while (k1 < t.length() - 1) {
                    k1++;
                    String c = t.substring(k1, k1 + 1);
                    if (c.compareTo(doubleQuote) == 0) {
                        t1 = t.substring(0, k1);
                        break;
                    }
                }
                int k2 = i;
                while (k2 > 0) {
                    k2--;
                    String c = t.substring(k2, k2 + 1);
                    if (c.compareTo(doubleQuote) == 0) {
                        t2 = t.substring(0, k2);
                        break;
                    }
                }

                String t3 = t.substring(k2, k1 + 1);
                String t4 = t.substring(k2 + 1, k1);
                v.add(t4);
            }
        }
        return v;
    }

    public String replaceHyperlinks(String s, String target,
        String hyperlinktext) {
        Vector<String> v = findHyperlinks(s, target);
        //String t3 = "";

        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < v.size(); i++) {
            String str = (String) v.elementAt(i);
            String s1 = str;
            int n1 = s.indexOf(s1);
            String t1 = s.substring(0, n1);
            String t2 = s.substring(n1 + str.length(), s.length());
            s = t2;

            String link = null;

            int index = s.indexOf(hyperlinktext);
            if (index != -1) {
                String text = s.substring(0, index + hyperlinktext.length());
                s = s.substring(index + hyperlinktext.length(), s.length());
                text = text.trim();
                if (text.charAt(0) == '\"') {
                    text = text.substring(1, text.length());
                    text = text.trim();
                }
                if (hyperlinktext.compareTo("NCI Thesaurus") == 0) {
                    str = replaceBrowserURL(str);
                }
                link = createHyperLink(str, text);
            }

            String s2 = link;
            t2 = t1 + s2;
            //t3 = t3 + t2;
            buf.append(t2);
        }
        //t3 = t3 + s;
        buf.append(s);
        String t3 = buf.toString();

        t3 = t3.replaceAll("\"<a", "<a");
        t3 = t3.replaceAll("</a>\"", "</a>");

        return t3;
    }

    public String createHyperLink(String url, String text) {
        String s =
            "<a href=\"" + url + "\" target=\"_blank\" alt=\"" + text + "\">"
                + text + "</a>";
        return s;

    }

    public String reformatPDQDefinition(String s) {
        String target = "aspx?";
        String t = replaceHyperlinks(s, target, "clinical trials");
        s = t;
        target = "jsp?";
        t = replaceHyperlinks(s, target, "NCI Thesaurus");
        return t;
    }

    public String replaceBrowserURL(String url) {
        int n = url.indexOf("code=");
        if (n != -1) {
            String t = url.substring(n, url.length());
            return NCIT_URL + t;
        }
        return url;
    }


    public static boolean isConceptEntity(ResolvedConceptReference rcr) {
		if (rcr == null) return false;
        String[] entityTypes = rcr.getEntityType();
        if (entityTypes == null) return false;
        if (entityTypes.length == 0) return true;
        if (Arrays.asList(entityTypes).contains("concept")) return true;
        return false;
	}


    public String getHyperlink(String scheme, String version, ResolvedConceptReference rcr,
                               boolean encode, String otherParameters) {

        String name = rcr.getEntityDescription().getContent();
        if (isConceptEntity(rcr)) return name;

        String code = rcr.getConceptCode();
        String ns = rcr.getCodeNamespace();

		StringBuffer buf = new StringBuffer();
		buf.append("<a href=");
		buf.append("\"");

		buf.append("<%=request.getContextPath() %>/ConceptReport.jsp?")
			.append("dictionary=<%=" + scheme + "%>")
			.append("&version=<%=" + version + "%>")
			.append("&code=<%=" + code + "%>")
			.append("&ns=<%=" + ns + "%>");
		if (otherParameters != null) {
			buf.append("<%=" + otherParameters + "%>");
		}
		buf.append("\"");

		buf.append("><%=");
		buf.append(name);
		buf.append("%></a>");
		return buf.toString();
	}

	public Vector getPropertyQualifiers(String displayLabel, String propertyValue) {
		Vector propertyName_vec = null;
		if (displayLabel2PropertyNameHashMap.containsKey(displayLabel)) {
			propertyName_vec = (Vector) displayLabel2PropertyNameHashMap.get(displayLabel);
		} else {
			return null;
		}
        for (int i=0; i<propertyName_vec.size(); i++) {
			String propertyName = (String) propertyName_vec.elementAt(i);
			String key = propertyName + "$" + propertyValue;
			if (propertyQualifierHashMap.containsKey(key)) {
				Vector v = (Vector) propertyQualifierHashMap.get(key);
				return v;
			}
	    }
	    return null;
	}

	public String getPropertyQualifierString(String displayLabel, String propertyValue) {
		if (!owl2_layout) return null;
		Vector v = getPropertyQualifiers(displayLabel, propertyValue);
		if (v == null || v.size() == 0) return null;
		StringBuffer buf = new StringBuffer();
		String indent = "&nbsp;&nbsp;&nbsp;";
		buf.append("<table class=\"datatable_960\" border=\"0\" width=\"100%\">").append("\n");
		for (int i=0; i<v.size(); i++) {
			String nv = (String) v.elementAt(i);
			Vector u = gov.nih.nci.evs.browser.utils.StringUtils.parseData(nv, "=");
			String name = (String) u.elementAt(0);
			String value = (String) u.elementAt(1);
			buf.append("<tr>").append("\n");
			buf.append("	  <td class=\"dataCellText\" scope=\"row\">").append("\n");
			buf.append(indent + name + ": " + value).append("\n");
			buf.append("	  </td>").append("\n");
			buf.append("</tr>").append("\n");
		}
		buf.append("</table>");
		return buf.toString();
	}

/*
	public static void main(String[] args) {
		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
		String scheme = "NCI_Thesaurus";
		String version = "15.12d";
		PropertyData pd = new PropertyData(lbSvc, scheme, version);
		String code = "C17359"; // TP53 Gene (Code C17359)
		String namespace = scheme;
		String rel_type = Constants.TYPE_ROLE;
		String t = pd.generateRelationshipTable(scheme, version, code, namespace, rel_type);
        System.out.println(t);
	}
*/
}