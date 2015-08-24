package gov.nih.nci.evs.testUtil;




/**
 * <!-- LICENSE_TEXT_START -->
 * Copyright 2008-2015 NGIT. This software was developed in conjunction
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
 * Modification history:
 *     Initial implementation kim.ong@ngc.com
 *
 */


public class Constants {
	//public static String NCI_THESAURUS = "NCI_Thesaurus";

    public static String FULL_NAME = "full_name";
    public static String DISPLAY_NAME = "display_name";
    public static String TERM_BROWSER_VERSION = "term_browser_version";

    public static final String TERMINOLOGY_TAB = "terminologies";
    public static final String VALUESET_TAB = "valuesets";
    public static final String MAPPING_TAB = "mappings";

    public static final String NAV_TAB_MAPPING = "tab_map";
    public static final String NAV_TAB_VALUESET = "tab_valuesets";
    public static final String NAV_TAB_TERMINOLOGY = "tab_terms";

    public static final String ALGORITHM_CONTAINS = "contains";
    public static final String ALGORITHM_EXACTMATCH = "exactMatch";
    public static final String ALGORITHM_STARTSWITH = "startsWith";
    public static final String ALGORITHM_LUCENE = "lucene";

    public static final String NAMES = "names";
    public static final String CODES = "codes";
    public static final String PROPERTIES = "properties";
    public static final String RELATIONSHIPS = "relationships";

    //public static final String NAME = "Name";
    //public static final String CODE = "Code";
    //public static final String PROPERTY = "Property";
    //public static final String RELATIONSHIP = "Relationship";

    //public static final String PRODUCTION = "PRODUCTION";

    //public static final String TREE_ACCESS_ALLOWED = "tree_access_allowed";
    //public static final String TERMINOLOGY_VALUE_SET = "Terminology Value Set";
    //public static final String TERMINOLOGY_VALUE_SET_NAME = "Terminology_Value_Set.owl";
    //public static final String TERMINOLOGY_VALUE_SET_ALT_NAME = "Terminology Value Set.owl";

    public static final String[] LICENSED_TERMINOLOGY = new String[]{"UMLS SemNet", "MedDRA", "SNOMED CT", "ICD-10"};

    public static final String[] ALGORITHMS = new String[]{ALGORITHM_CONTAINS,ALGORITHM_EXACTMATCH,ALGORITHM_STARTSWITH,ALGORITHM_LUCENE};
    public static final String[] TARGETS = new String[]{NAMES,CODES,PROPERTIES,RELATIONSHIPS};

    //public static final String TYPE_ROLE = "type_role";
    //public static final String TYPE_ASSOCIATION = "type_association";
    //public static final String TYPE_SUPERCONCEPT = "type_superconcept";
    //public static final String TYPE_SUBCONCEPT = "type_subconcept";
    //public static final String TYPE_INVERSE_ROLE = "type_inverse_role";
    //public static final String TYPE_INVERSE_ASSOCIATION = "type_inverse_association";


    public static final String[] SIMPLE_SEARCH_ALGORITHMS = new String[] {"exactMatch", "contains", "lucene"};
    public static final String[] SIMPLE_SEARCH_TARGETS = new String[] {"names", "codes"};

    public static final String[] ADVANCED_SEARCH_ALGORITHMS = new String[] {"contains", "exactMatch", "startsWith", "lucene"};
    public static final String[] ADVANCED_SEARCH_TARGETS = new String[] {"Code", "Name", "Property", "Relationship"};

    public static final String[] SIMPLE_SEARCH_FORM_ALGORITHMS = new String[] {"exactMatch", "contains", "startsWith"};
    public static final String[] SIMPLE_SEARCH_FORM_TARGETS = new String[] {"names", "codes", "properties", "relationships"};

    //public static final int SEARCH_BOTH_DIRECTION = 0;
    //public static final int SEARCH_SOURCE = 1;
    //public static final int SEARCH_TARGET = 2;

    public static final String THIS_PAGE_CANNOT_BE_DISPLAYED = "This page can&rsquo;t be displayed";

//Arrays.asList(yourArray).contains(yourValue)


    /**
     * Constructor
     */
    private Constants() {
        // Prevent class from being explicitly instantiated
    }

} // Class Constants
