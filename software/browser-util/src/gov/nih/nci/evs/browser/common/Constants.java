package gov.nih.nci.evs.browser.common;

import java.util.List;
import java.util.Arrays;

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
 * Application constants class
 *
 * @author kim.ong@ngc.com
 */
public class Constants {
    public static final String PRODUCTION = "PRODUCTION";
    public static final String TELEPHONE = "240-276-5541 or Toll-Free: 1-888-478-4423";
    public static final String MAIL_TO = "ncicbiit@mail.nih.gov";
    public static final String NCICB_URL = "http://ncicb.nci.nih.gov/support";

    // Application version
    public static final int MAJOR_VER = 1;
    public static final int MINOR_VER = 0;
    public static final String CONFIG_FILE = "NCItBrowserProperties.xml";
    public static final String CODING_SCHEME_NAME = "NCI Thesaurus";
    public static final String NCI_THESAURUS = "NCI Thesaurus";
    public static final String NCIT_CS_NAME = "NCI_Thesaurus";
    public static final String NCI_METATHESAURUS = "NCI Metathesaurus";
    public static final String NCI_METATHESAURUS_CUI = "NCI Metathesaurus CUI";

    public static final String CONCEPT_STATUS = "Concept_Status";

    // Application constants
    public static final String NA = "N/A";
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final String EMPTY = "";

    public static final String ALL = "ALL";
    public static final String OBO = "OBO";

    // Application error constants
    public static final String INIT_PARAM_ERROR_PAGE = "errorPage";
    public static final String ERROR_MESSAGE = "systemMessage";
    public static final String ERROR_UNEXPECTED =
        "Warning: An unexpected processing error has occurred.";

    public static final int DEFAULT_PAGE_SIZE = 50;

    public static final String ERROR_NO_VOCABULARY_SELECTED =
        "Please select at least one terminology.";
    public static final String ERROR_NO_SEARCH_STRING_ENTERED =
        "Please enter a search string.";
    public static final String ERROR_NO_MATCH_FOUND = "No match found.";
    public static final String ERROR_NO_MATCH_FOUND_TRY_OTHER_ALGORITHMS =
        "No match found. Please try 'Begins With' or 'Contains' search instead.";

    public static final String ERROR_NO_MATCH_FOUND_CODE_IS_CASESENSITIVE =
        "No match found. Please note that code search is case sensitive.";

    public static final String ERROR_ENCOUNTERED_TRY_NARROW_QUERY =
        "Unable to perform search successfully. Please narrow your query.";
    public static final String ERROR_REQUIRE_MORE_SPECIFIC_QUERY_STRING =
        "Please provide a more specific search criteria.";

    public static final String EXACT_SEARCH_ALGORITHM = "exactMatch";// "literalSubString";//"subString";
    public static final String STARTWITH_SEARCH_ALGORITHM = "startsWith";// "literalSubString";//"subString";
    public static final String CONTAIN_SEARCH_ALGORITHM =
        "nonLeadingWildcardLiteralSubString";// "literalSubString";//"subString";
    public static final String LICENSE_STATEMENT = "license_statement";// "literalSubString";//"subString";

    public static final String DEFAULT_SEARCH_ALGORITHM = "contains";


    public static final int SEARCH_BOTH_DIRECTION = 0;
    public static final int SEARCH_SOURCE = 1;
    public static final int SEARCH_TARGET = 2;

    public static final String TREE_ACCESS_ALLOWED = "tree_access_allowed";

    public static final String TYPE_ROLE = "type_role";
    public static final String TYPE_ASSOCIATION = "type_association";
    public static final String TYPE_SUPERCONCEPT = "type_superconcept";
    public static final String TYPE_SUBCONCEPT = "type_subconcept";
    public static final String TYPE_INVERSE_ROLE = "type_inverse_role";
    public static final String TYPE_INVERSE_ASSOCIATION = "type_inverse_association";
    public static final String TYPE_LOGICAL_DEFINITION = "type_logical_definition";

    public static final int  STANDARD_VIEW = 1;
    public static final int  TERMINOLOGY_VIEW = 2;

    public static final String  TERMINOLOGY_VALUE_SET = "Terminology Value Set";
    public static final String  TERMINOLOGY_VALUE_SET_NAME = "Terminology_Value_Set.owl";
    public static final String  TERMINOLOGY_VALUE_SET_ALT_NAME = "Terminology Value Set.owl";

    public static final String[] TERMINOLOGY_VALUE_SET_NAMES = {"Terminology Value Set", "Terminology_Value_Set.owl", "Terminology Value Set.owl", "Terminology_Value_Set"};


    public static final long MILLISECONDS_PER_MINUTE = 60L * 1000;

    public static final String PLEASE_COMPLETE_DATA_ENTRIES = "Please complete data entries.";
    public static final String INVALID_EMAIL_ADDRESS = "Invalid email address.";


    public static final String CODE = "codes";
    public static final String NAME = "names";
    public static final String PROPERTY = "properties";
    public static final String RELATIONSHIP = "relationahips";

    //public static final String[] VALID_CAPTCHA_OTPIONS = new String[] {"default","audio"};
    public static final String DEFAULT_CAPTCHA_OTPION = "default";
    public static final String AUDIO_CAPTCHA_OTPION = "audio";

    public static final String INVALID_CAPTCHA_OTPION = "WARNING: Invalid CAPTCHA option.";

    public static final String UNIDENTIFIABLE_VOCABULARY = "WARNING: Unidentifiable vocabulary name.";


    public static final String ROLE_LABEL = "Role Relationships";
    public static final String ROLE_LABEL_2 = "pointing from the current concept to other concepts";
    public static final String ROLE_DESCRIPTION_LABEL = "(True for the current concept and its descendants, may be inherited from parent(s).)";

    public static final String ASSOCIATION_LABEL = "Associations";
    public static final String ASSOCIATION_LABEL_2 = "pointing from the current concept to other concepts";
    public static final String ASSOCIATION_DESCRIPTION_LABEL = "(True for the current concept.)";

    public static final String INVERSE_ROLE_LABEL = "Incoming Role Relationships";
    public static final String INVERSE_ROLE_LABEL_2 = "pointing from other concepts to the current concept";
    public static final String INVERSE_ROLE_DESCRIPTION_LABEL = "(True for the other concept and its descendants, may be inherited from its parent(s).)";

    public static final String INVERSE_ASSOCIATION_LABEL = "Incoming Associations";
    public static final String INVERSE_ASSOCIATION_LABEL_2 = "pointing from other concepts to the current concept";

    public static final String INVERSE_ASSOCIATION_DESCRIPTION_LABEL = "(True for the other concept's relationship to the current concept.)";

    public static final String NCBO_PURL = "http://purl.bioontology.org/ontology/";
    public static final String NCIT_NCBO_ID = "1032";
    public static final String NCIT = "NCIT";
    public static final String NCIT_NAMESPACE = "http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#";
    public static final String NCBO_WIDGET_QUERY_STRING = "http://bioportal.bioontology.org/widgets/visualization?ontology=";

    //public static final String DEFAULT_NCBO_WIDGET_INFO = "NCI_Thesaurus|1032|NCIT;";
    public static final String DEFAULT_NCBO_WIDGET_INFO = "NCI_Thesaurus|http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#|NCIT;";

    public static final String[] NAV_TYPE_VALUES = {"terminologies", "mappings", "valuesets"};

    public static final String VALUE_SET_REPORT_CONFIG = "gov.nih.nci.evs.browser.ValueSetReportConfig";

    public static final String VALUE_SET_URI_PREFIX = "http://evs.nci.nih.gov/valueset/";
    public static final String VALUE_SET_URI_PREFIX_OLD = "http://ncit";

    public static final String[] NCIT_OR_NCIM = {"NCI Thesaurus", "NCI_Thesaurus", "NCI Metathesaurus", "NCI_Metathesaurus"};
    public static final String[] NCIT_NAMES = {"NCI Thesaurus", "NCI_Thesaurus", "NCIT"};

    public static final String[] TRUE_OR_FALSE = {"true", "false"};

	public static String[] NCIM_CODE_PROPERTYIES = new String[] {"NCI_META_CUI", "UMLS_CUI"};

	public static final String NO_VALUE_SET_REPORT_AVAILABLE = "No value set report is available.";

    public static final String CDISC_SUBMISSION_VALUE = "CDISC Submission Value";

    public static String INDENT_HALF = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
    public static String INDENT = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";

	public static final String[] HAZARD_CHARS = {
		"$QUERY", "$WHERE", "()", "(EVAL", ".JPG HTTP",
		"/IFRAME ", "/SCRIPT", ";DECLARE", "</SPAN>", "</XML>",
		"<SCRIPT>", "<STYLE>", "=(", "=FUNCTION", "\"ALERT",
		"ALERT(", "A HREF=", "BASE HREF", "BGSOUND SRC", "BODY ONLOAD",
		//[NCITERM-733] Search using a string containing a '-' character may result in a misleading error page.
		//"BR SIZE", "CAST(", "DATA=", "DIV STYLE", "E-",
		"BR SIZE", "CAST(", "DATA=", "DIV STYLE",
		"ECHO(", "EXEC(", "EXPRESSION(", "FONT-FAMILY", "HREF=",
		"IFRAME ", "IFRAME SRC", "IMG DYNSRC", "IMG LOWSRC", "IMG SRC=",
		"IMG STYLE", "INPUT TYPE", "JAVASCRIPT:ALERT", "LINK REL", "META HTTP-EQUIV",
		"OBJECT TYPE", "ONMOUSEOVER", "SCRIPT/SRC", "SCRIPT/XSS ", "SCRIPT SRC=",
		"SION(EVAL", "SLEEP(", "SPAN DATASRC", "SRC=", "STYLE=",
		"VARCHAR(", "XML ID", "XSS:EXPRESSION", "XSS STYLE"
	};


    public static final String NO_VALUE_SET_LOADED_MSG = "No value set has been loaded.";
    public static final String ONTOLOGY_NODE_ID = "ontology_node_id";
    public static final String ONTOLOGY_NODE_NS = "ontology_node_ns";
    public static final String ONTOLOGY_NODE_NAME = "ontology_node_name";
    public static final String ONTOLOGY_NODE_PARENT_ASSOC = "ontology_node_parent_assoc";
    public static final String ONTOLOGY_NODE_CHILD_COUNT = "ontology_node_child_count";
    public static final String ONTOLOGY_NODE_DEFINITION = "ontology_node_definition";
    public static final String CHILDREN_NODES = "children_nodes";
    public static final String ASSOCIATION = "association";

    public static final String CODING_SCHEME_NOT_AVAILABLE = "Coding scheme is either not loaded or inactive. Please consult your system administrator.";
    public static final String NCIT_NOT_AVAILABLE = "NCI Thesaurus is either not loaded or inactive. Please consult your system administrator.";

    public static final String[] NON_CONCEPT_TO_CONCEPT_ASSOCIATION = new String[] {"domain", "range", "instance"};

    public static final String NO_ROOT_NODES_AVAILABLE = "No root nodes available";

    public static final String MODE_EXPAND = "1";
    public static final String MODE_COLLAPSE = "2";

	public static final String[] NCIT_ROLES = new String[] {
		"R100|Disease_Has_Associated_Anatomic_Site",
		"R101|Disease_Has_Primary_Anatomic_Site",
		"R102|Disease_Has_Metastatic_Anatomic_Site",
		"R103|Disease_Has_Normal_Tissue_Origin",
		"R104|Disease_Has_Normal_Cell_Origin",
		"R105|Disease_Has_Abnormal_Cell",
		"R106|Disease_Has_Molecular_Abnormality",
		"R107|Disease_Has_Cytogenetic_Abnormality",
		"R108|Disease_Has_Finding",
		"R110|Disease_Is_Grade",
		"R112|Disease_May_Have_Normal_Cell_Origin",
		"R113|Disease_May_Have_Abnormal_Cell",
		"R114|Disease_May_Have_Cytogenetic_Abnormality",
		"R115|Disease_May_Have_Finding",
		"R116|Disease_May_Have_Associated_Disease",
		"R122|Chemical_Or_Drug_Is_Metabolized_By_Enzyme",
		"R123|Chemotherapy_Regimen_Has_Component",
		"R124|Chemical_Or_Drug_Has_Mechanism_Of_Action",
		"R125|Chemical_Or_Drug_Has_Physiologic_Effect",
		"R126|Disease_Has_Associated_Disease",
		"R130|Gene_Is_Element_In_Pathway",
		"R131|Gene_Product_Is_Element_In_Pathway",
		"R132|Gene_Has_Abnormality",
		"R133|Gene_Product_Has_Abnormality",
		"R135|Disease_Excludes_Primary_Anatomic_Site",
		"R137|Disease_Excludes_Normal_Tissue_Origin",
		"R138|Disease_Excludes_Normal_Cell_Origin",
		"R139|Disease_Excludes_Abnormal_Cell",
		"R140|Disease_Excludes_Molecular_Abnormality",
		"R141|Disease_Excludes_Cytogenetic_Abnormality",
		"R142|Disease_Excludes_Finding",
		"R145|Gene_Has_Physical_Location",
		"R146|Chemical_Or_Drug_Affects_Gene_Product",
		"R150|Chemical_Or_Drug_Affects_Abnormal_Cell",
		"R153|Allele_Has_Abnormality",
		"R155|Allele_In_Chromosomal_Location",
		"R156|Allele_Absent_From_Wild-type_Chromosomal_Location",
		"R158|Allele_Plays_Altered_Role_In_Process",
		"R159|Allele_Has_Activity",
		"R160|Allele_Plays_Role_In_Metabolism_Of_Chemical_Or_Drug",
		"R163|Procedure_Has_Target_Anatomy",
		"R165|Procedure_Has_Imaged_Anatomy",
		"R166|Procedure_May_Have_Excised_Anatomy",
		"R167|Procedure_Has_Excised_Anatomy",
		"R168|Procedure_May_Have_Partially_Excised_Anatomy",
		"R169|Procedure_Has_Partially_Excised_Anatomy",
		"R170|Procedure_May_Have_Completely_Excised_Anatomy",
		"R171|Procedure_Has_Completely_Excised_Anatomy",
		"R172|Regimen_Has_Accepted_Use_For_Disease",
		"R173|Cytogenetic_Abnormality_Involves_Chromosome",
		"R174|Disease_Mapped_To_Chromosome",
		"R175|Gene_Involved_In_Pathogenesis_Of_Disease",
		"R176|Disease_Mapped_To_Gene",
		"R177|Molecular_Abnormality_Involves_Gene",
		"R178|Gene_Mutant_Encodes_Gene_Product_Sequence_Variation",
		"R179|Gene_Product_Sequence_Variation_Encoded_By_Gene_Mutant",
		"R23|EO_Disease_Has_Associated_Cell_Type",
		"R24|EO_Disease_Maps_To_Human_Disease",
		"R25|EO_Disease_Has_Associated_EO_Anatomy",
		"R26|EO_Disease_Has_Property_Or_Attribute",
		"R27|Conceptual_Part_Of",
		"R28|Biological_Process_Has_Initiator_Process",
		"R29|Biological_Process_Has_Result_Chemical_Or_Drug",
		"R30|Biological_Process_Has_Associated_Location",
		"R31|Biological_Process_Has_Result_Anatomy",
		"R32|Biological_Process_Has_Initiator_Chemical_Or_Drug",
		"R34|Biological_Process_Has_Result_Biological_Process",
		"R35|Biological_Process_Is_Part_Of_Process",
		"R36|Gene_Is_Biomarker_Type",
		"R37|Gene_Plays_Role_In_Process",
		"R38|Gene_Associated_With_Disease",
		"R39|Gene_Is_Biomarker_Of",
		"R40|Gene_In_Chromosomal_Location",
		"R41|Gene_Found_In_Organism",
		"R42|Gene_Product_Is_Biomarker_Type",
		"R44|Gene_Product_Has_Chemical_Classification",
		"R45|Gene_Product_Has_Organism_Source",
		"R46|Gene_Product_Has_Associated_Anatomy",
		"R47|Gene_Product_Is_Biomarker_Of",
		"R48|Gene_Product_Malfunction_Associated_With_Disease",
		"R49|Gene_Product_Expressed_In_Tissue",
		"R50|Gene_Product_Has_Structural_Domain_Or_Motif",
		"R51|Gene_Product_Is_Physical_Part_Of",
		"R52|Gene_Product_Has_Biochemical_Function",
		"R53|Gene_Product_Plays_Role_In_Biological_Process",
		"R54|Gene_Product_Encoded_By_Gene",
		"R66|Chemical_Or_Drug_Plays_Role_In_Biological_Process",
		"R72|Chemical_Or_Drug_Affects_Cell_Type_Or_Tissue",
		"R81|Anatomic_Structure_Has_Location",
		"R82|Anatomic_Structure_Is_Physical_Part_Of",
		"R88|Disease_Is_Stage",
		"R89|Disease_May_Have_Molecular_Abnormality"
		};

	public static final String[] NCIT_ROLE_DATA = new String[] {
		"R156|Allele_Absent_From_Wild-type_Chromosomal_Location|Role_Has_Range|Anatomic Structure, System, or Substance|Role_Has_Domain|Gene",
		"R153|Allele_Has_Abnormality|Role_Has_Range|Molecular Abnormality|Role_Has_Domain|Gene",
		"R159|Allele_Has_Activity|Role_Has_Range|Property or Attribute|Role_Has_Domain|Gene",
		"R155|Allele_In_Chromosomal_Location|Role_Has_Range|Anatomic Structure, System, or Substance|Role_Has_Domain|Gene",
		"R158|Allele_Plays_Altered_Role_In_Process|Role_Has_Range|Biological Process|Role_Has_Domain|Gene",
		"R160|Allele_Plays_Role_In_Metabolism_Of_Chemical_Or_Drug|Role_Has_Range|Drug, Food, Chemical or Biomedical Material|Role_Has_Domain|Gene",
		"R81|Anatomic_Structure_Has_Location|Role_Has_Range|Anatomic Structure, System, or Substance|Role_Has_Domain|Anatomic Structure, System, or Substance",
		"R82|Anatomic_Structure_Is_Physical_Part_Of|Role_Has_Range|Anatomic Structure, System, or Substance|Role_Has_Domain|Anatomic Structure, System, or Substance",
		"R30|Biological_Process_Has_Associated_Location|Role_Has_Range|Anatomic Structure, System, or Substance|Role_Has_Domain|Biological Process",
		"R32|Biological_Process_Has_Initiator_Chemical_Or_Drug|Role_Has_Range|Drug, Food, Chemical or Biomedical Material|Role_Has_Domain|Biological Process",
		"R28|Biological_Process_Has_Initiator_Process|Role_Has_Range|Biological Process|Role_Has_Domain|Biological Process",
		"R31|Biological_Process_Has_Result_Anatomy|Role_Has_Range|Anatomic Structure, System, or Substance|Role_Has_Domain|Biological Process",
		"R34|Biological_Process_Has_Result_Biological_Process|Role_Has_Range|Biological Process|Role_Has_Domain|Biological Process",
		"R29|Biological_Process_Has_Result_Chemical_Or_Drug|Role_Has_Range|Drug, Food, Chemical or Biomedical Material|Role_Has_Domain|Biological Process",
		"R35|Biological_Process_Is_Part_Of_Process|Role_Has_Range|Biological Process|Role_Has_Domain|Biological Process",
		"R150|Chemical_Or_Drug_Affects_Abnormal_Cell|Role_Has_Range|Abnormal Cell|Role_Has_Domain|Drug, Food, Chemical or Biomedical Material",
		"R72|Chemical_Or_Drug_Affects_Cell_Type_Or_Tissue|Role_Has_Range|Anatomic Structure, System, or Substance|Role_Has_Domain|Drug, Food, Chemical or Biomedical Material",
		"R146|Chemical_Or_Drug_Affects_Gene_Product|Role_Has_Range|Gene Product|Role_Has_Domain|Drug, Food, Chemical or Biomedical Material",
		"R124|Chemical_Or_Drug_Has_Mechanism_Of_Action|Role_Has_Range|Biological Process|Role_Has_Domain|Drug, Food, Chemical or Biomedical Material",
		"R125|Chemical_Or_Drug_Has_Physiologic_Effect|Role_Has_Range|Biological Process|Role_Has_Domain|Drug, Food, Chemical or Biomedical Material",
		"R122|Chemical_Or_Drug_Is_Metabolized_By_Enzyme|Role_Has_Range|Gene Product|Role_Has_Domain|Drug, Food, Chemical or Biomedical Material",
		"R66|Chemical_Or_Drug_Plays_Role_In_Biological_Process|Role_Has_Range|Biological Process|Role_Has_Domain|Drug, Food, Chemical or Biomedical Material",
		"R123|Chemotherapy_Regimen_Has_Component|Role_Has_Range|Drug, Food, Chemical or Biomedical Material|Role_Has_Domain|Chemotherapy Regimen or Agent Combination",
		"R27|Conceptual_Part_Of|Role_Has_Range|Conceptual Entity|Role_Has_Domain|Conceptual Entity",
		"R173|Cytogenetic_Abnormality_Involves_Chromosome|Role_Has_Range|Chromosome|Role_Has_Domain|Cytogenetic Abnormality",
		"R139|Disease_Excludes_Abnormal_Cell|Role_Has_Range|Abnormal Cell|Role_Has_Domain|Disease, Disorder or Finding",
		"R141|Disease_Excludes_Cytogenetic_Abnormality|Role_Has_Range|Molecular Abnormality|Role_Has_Domain|Disease, Disorder or Finding",
		"R142|Disease_Excludes_Finding|Role_Has_Range|Disease, Disorder or Finding|Role_Has_Domain|Disease, Disorder or Finding",
		"null|Disease_Excludes_Metastatic_Anatomic_Site|Role_Has_Range|Anatomic Structure, System, or Substance|Role_Has_Domain|Disease, Disorder or Finding",
		"R140|Disease_Excludes_Molecular_Abnormality|Role_Has_Range|Molecular Abnormality|Role_Has_Domain|Disease, Disorder or Finding",
		"R138|Disease_Excludes_Normal_Cell_Origin|Role_Has_Range|Anatomic Structure, System, or Substance|Role_Has_Domain|Disease, Disorder or Finding",
		"R137|Disease_Excludes_Normal_Tissue_Origin|Role_Has_Range|Anatomic Structure, System, or Substance|Role_Has_Domain|Disease, Disorder or Finding",
		"R135|Disease_Excludes_Primary_Anatomic_Site|Role_Has_Range|Anatomic Structure, System, or Substance|Role_Has_Domain|Disease, Disorder or Finding",
		"R105|Disease_Has_Abnormal_Cell|Role_Has_Range|Abnormal Cell|Role_Has_Domain|Disease, Disorder or Finding",
		"R100|Disease_Has_Associated_Anatomic_Site|Role_Has_Range|Anatomic Structure, System, or Substance|Role_Has_Domain|Disease, Disorder or Finding",
		"R126|Disease_Has_Associated_Disease|Role_Has_Range|Disease, Disorder or Finding|Role_Has_Domain|Disease, Disorder or Finding",
		"R107|Disease_Has_Cytogenetic_Abnormality|Role_Has_Range|Molecular Abnormality|Role_Has_Domain|Disease, Disorder or Finding",
		"R108|Disease_Has_Finding|Role_Has_Range|Disease, Disorder or Finding|Role_Has_Domain|Disease, Disorder or Finding",
		"R102|Disease_Has_Metastatic_Anatomic_Site|Role_Has_Range|Anatomic Structure, System, or Substance|Role_Has_Domain|Disease, Disorder or Finding",
		"R106|Disease_Has_Molecular_Abnormality|Role_Has_Range|Molecular Abnormality|Role_Has_Domain|Disease, Disorder or Finding",
		"R104|Disease_Has_Normal_Cell_Origin|Role_Has_Range|Anatomic Structure, System, or Substance|Role_Has_Domain|Disease, Disorder or Finding",
		"R103|Disease_Has_Normal_Tissue_Origin|Role_Has_Range|Anatomic Structure, System, or Substance|Role_Has_Domain|Disease, Disorder or Finding",
		"R101|Disease_Has_Primary_Anatomic_Site|Role_Has_Range|Anatomic Structure, System, or Substance|Role_Has_Domain|Disease, Disorder or Finding",
		"R110|Disease_Is_Grade|Role_Has_Range|Property or Attribute|Role_Has_Domain|Disease, Disorder or Finding",
		"R88|Disease_Is_Stage|Role_Has_Range|Property or Attribute|Role_Has_Domain|Disease, Disorder or Finding",
		"R174|Disease_Mapped_To_Chromosome|Role_Has_Range|Chromosome|Role_Has_Domain|Disease, Disorder or Finding",
		"R176|Disease_Mapped_To_Gene|Role_Has_Range|Gene|Role_Has_Domain|Disease, Disorder or Finding",
		"R113|Disease_May_Have_Abnormal_Cell|Role_Has_Range|Abnormal Cell|Role_Has_Domain|Disease, Disorder or Finding",
		"R116|Disease_May_Have_Associated_Disease|Role_Has_Range|Disease, Disorder or Finding|Role_Has_Domain|Disease, Disorder or Finding",
		"R114|Disease_May_Have_Cytogenetic_Abnormality|Role_Has_Range|Molecular Abnormality|Role_Has_Domain|Disease, Disorder or Finding",
		"R115|Disease_May_Have_Finding|Role_Has_Range|Disease, Disorder or Finding|Role_Has_Domain|Disease, Disorder or Finding",
		"R89|Disease_May_Have_Molecular_Abnormality|Role_Has_Range|Molecular Abnormality|Role_Has_Domain|Disease, Disorder or Finding",
		"R112|Disease_May_Have_Normal_Cell_Origin|Role_Has_Range|Anatomic Structure, System, or Substance|Role_Has_Domain|Disease, Disorder or Finding",
		"null|Disease_May_Have_Normal_Tissue_Origin|Role_Has_Range|Anatomic Structure, System, or Substance|Role_Has_Domain|Disease, Disorder or Finding",
		"R23|EO_Disease_Has_Associated_Cell_Type|Role_Has_Range|Experimental Organism Anatomical Concept|Role_Has_Domain|Experimental Organism Diagnosis",
		"R25|EO_Disease_Has_Associated_EO_Anatomy|Role_Has_Range|Experimental Organism Anatomical Concept|Role_Has_Domain|Experimental Organism Diagnosis",
		"R26|EO_Disease_Has_Property_Or_Attribute|Role_Has_Range|Property or Attribute|Role_Has_Domain|Experimental Organism Diagnosis",
		"R24|EO_Disease_Maps_To_Human_Disease|Role_Has_Range|Disease, Disorder or Finding|Role_Has_Domain|Experimental Organism Diagnosis",
		"R38|Gene_Associated_With_Disease|Role_Has_Range|Disease, Disorder or Finding|Role_Has_Domain|Gene",
		"R41|Gene_Found_In_Organism|Role_Has_Range|Organism|Role_Has_Domain|Gene",
		"R132|Gene_Has_Abnormality|Role_Has_Range|Molecular Abnormality|Role_Has_Domain|Gene",
		"R145|Gene_Has_Physical_Location|Role_Has_Range|Anatomic Structure, System, or Substance|Role_Has_Domain|Gene",
		"R40|Gene_In_Chromosomal_Location|Role_Has_Range|Anatomic Structure, System, or Substance|Role_Has_Domain|Gene",
		"R175|Gene_Involved_In_Pathogenesis_Of_Disease|Role_Has_Range|Gene|Role_Has_Domain|Disease or Disorder",
		"R36|Gene_Is_Biomarker_Type|Role_Has_Range|Diagnostic or Prognostic Factor|Role_Has_Domain|Gene",
		"R39|Gene_Is_Biomarker_Of|Role_Has_Range|Disease, Disorder or Finding|Role_Has_Domain|Gene",
		"R130|Gene_Is_Element_In_Pathway|Role_Has_Range|Biochemical Pathway|Role_Has_Domain|Gene",
		"R178|Gene_Mutant_Encodes_Gene_Product_Sequence_Variation|Role_Has_Range|Gene Product Sequence Variation|Role_Has_Domain|Gene Mutant",
		"R37|Gene_Plays_Role_In_Process|Role_Has_Range|Biological Process|Role_Has_Domain|Gene",
		"R54|Gene_Product_Encoded_By_Gene|Role_Has_Range|Gene|Role_Has_Domain|Gene Product",
		"R49|Gene_Product_Expressed_In_Tissue|Role_Has_Range|Anatomic Structure, System, or Substance|Role_Has_Domain|Gene Product",
		"R133|Gene_Product_Has_Abnormality|Role_Has_Range|Molecular Abnormality|Role_Has_Domain|Gene Product",
		"R46|Gene_Product_Has_Associated_Anatomy|Role_Has_Range|Anatomic Structure, System, or Substance|Role_Has_Domain|Gene Product",
		"R52|Gene_Product_Has_Biochemical_Function|Role_Has_Range|Gene Product|Role_Has_Domain|Gene Product",
		"R44|Gene_Product_Has_Chemical_Classification|Role_Has_Range|Gene Product|Role_Has_Domain|Gene Product",
		"R45|Gene_Product_Has_Organism_Source|Role_Has_Range|Organism|Role_Has_Domain|Gene Product",
		"R50|Gene_Product_Has_Structural_Domain_Or_Motif|Role_Has_Range|Anatomic Structure, System, or Substance|Role_Has_Domain|Gene Product",
		"R47|Gene_Product_Is_Biomarker_Of|Role_Has_Range|Disease, Disorder or Finding|Role_Has_Domain|Gene Product",
		"R42|Gene_Product_Is_Biomarker_Type|Role_Has_Range|Diagnostic or Prognostic Factor|Role_Has_Domain|Gene Product",
		"R131|Gene_Product_Is_Element_In_Pathway|Role_Has_Range|Biochemical Pathway|Role_Has_Domain|Gene Product",
		"R51|Gene_Product_Is_Physical_Part_Of|Role_Has_Range|Gene Product|Role_Has_Domain|Gene Product",
		"R48|Gene_Product_Malfunction_Associated_With_Disease|Role_Has_Range|Disease, Disorder or Finding|Role_Has_Domain|Gene Product",
		"R53|Gene_Product_Plays_Role_In_Biological_Process|Role_Has_Range|Biological Process|Role_Has_Domain|Gene Product",
		"R179|Gene_Product_Sequence_Variation_Encoded_By_Gene_Mutant|Role_Has_Range|Gene Mutant|Role_Has_Domain|Gene Product Sequence Variation",
		"null|Gene_Product_Variant_Of_Gene_Product|Role_Has_Range|Gene Product|Role_Has_Domain|Gene Product Sequence Variation",
		"R177|Molecular_Abnormality_Involves_Gene|Role_Has_Range|Gene|Role_Has_Domain|Molecular Abnormality",
		"R171|Procedure_Has_Completely_Excised_Anatomy|Role_Has_Range|Anatomic Structure, System, or Substance|Role_Has_Domain|Activity",
		"R167|Procedure_Has_Excised_Anatomy|Role_Has_Range|Anatomic Structure, System, or Substance|Role_Has_Domain|Activity",
		"R165|Procedure_Has_Imaged_Anatomy|Role_Has_Range|Anatomic Structure, System, or Substance|Role_Has_Domain|Activity",
		"R169|Procedure_Has_Partially_Excised_Anatomy|Role_Has_Range|Anatomic Structure, System, or Substance|Role_Has_Domain|Activity",
		"R163|Procedure_Has_Target_Anatomy|Role_Has_Range|Anatomic Structure, System, or Substance|Role_Has_Domain|Activity",
		"null|Procedure_Has_Target_Disease|Role_Has_Range|Disease, Disorder or Finding|Role_Has_Domain|Activity",
		"R170|Procedure_May_Have_Completely_Excised_Anatomy|Role_Has_Range|Anatomic Structure, System, or Substance|Role_Has_Domain|Activity",
		"R166|Procedure_May_Have_Excised_Anatomy|Role_Has_Range|Anatomic Structure, System, or Substance|Role_Has_Domain|Activity",
		"R168|Procedure_May_Have_Partially_Excised_Anatomy|Role_Has_Range|Anatomic Structure, System, or Substance|Role_Has_Domain|Activity",
		"null|Procedure_Uses_Manufactured_Object|Role_Has_Range|Manufactured Object|Role_Has_Domain|Activity",
		"R172|Regimen_Has_Accepted_Use_For_Disease|Role_Has_Range|Disease, Disorder or Finding|Role_Has_Domain|Chemotherapy Regimen or Agent Combination"
		};

    /**
     * Constructor
     */
    private Constants() {
        // Prevent class from being explicitly instantiated
    }

    public static String getCodingSchemeName() {
        return CODING_SCHEME_NAME.replaceAll(" ", "%20");
    }

} // Class Constants
