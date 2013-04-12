/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.common;

/**
 * 
 */

/**
 * Application constants class
 *
 * @author garciawa2
 */
public class Constants {

    public static final String TELEPHONE = "240-276-5541 or Toll-Free: 1-888-478-4423";
    public static final String MAIL_TO = "ncicbiit@mail.nih.gov";
    public static final String NCICB_URL = "http://ncicb.nci.nih.gov/support";

    // Application version
    public static final int MAJOR_VER = 1;
    public static final int MINOR_VER = 0;
    public static final String CONFIG_FILE = "NCItBrowserProperties.xml";
    public static final String CODING_SCHEME_NAME = "NCI Thesaurus";
    public static final String NCI_THESAURUS = "NCI Thesaurus";
    public static final String NCI_METATHESAURUS = "NCI Metathesaurus";

    // Application constants
    public static final String NA = "N/A";
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final String EMPTY = "";

    public static final String ALL = "ALL";

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

    public static final String ERROR_ENCOUNTERED_TRY_NARROW_QUERY =
        "Unable to perform search successfully. Please narrow your query.";
    public static final String ERROR_REQUIRE_MORE_SPECIFIC_QUERY_STRING =
        "Please provide a more specific search criteria.";

    public static final String EXACT_SEARCH_ALGORITHM = "exactMatch";// "literalSubString";//"subString";
    public static final String STARTWITH_SEARCH_ALGORITHM = "startsWith";// "literalSubString";//"subString";
    public static final String CONTAIN_SEARCH_ALGORITHM =
        "nonLeadingWildcardLiteralSubString";// "literalSubString";//"subString";
    public static final String LICENSE_STATEMENT = "license_statement";// "literalSubString";//"subString";

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

    public static final int  STANDARD_VIEW = 1;
    public static final int  TERMINOLOGY_VIEW = 2;

    public static final String  TERMINOLOGY_VALUE_SET = "Terminology Value Set";
    public static final String  TERMINOLOGY_VALUE_SET_NAME = "Terminology_Value_Set.owl";

    public static final long MILLISECONDS_PER_MINUTE = 60L * 1000;

    public static final String PLEASE_COMPLETE_DATA_ENTRIES = "Please complete data entries.";
    public static final String INVALID_EMAIL_ADDRESS = "Invalid email address.";


    public static final String CODE = "codes";
    public static final String NAME = "names";
    public static final String PROPERTY = "properties";
    public static final String RELATIONSHIP = "relationahips";


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
