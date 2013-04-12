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
        "Please select at least one vocabulary.";
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
