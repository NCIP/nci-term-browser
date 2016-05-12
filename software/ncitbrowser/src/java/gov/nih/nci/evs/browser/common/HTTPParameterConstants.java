package gov.nih.nci.evs.browser.common;

import java.util.*;

public class HTTPParameterConstants {

    public static final String[] HTTP_REQUEST_PARAMETER_NAMES = {
       "acceptedLicenses",
       "action",
       "adv_search_algorithm",
       "adv_search_source",
       "adv_search_type",
       "algorithm",
       "answer",
       "b",
       "captcha_option",
       "cart_code",
       "cart_dictionary",
       "cart_version",
       "checkedNodes",
       "checked_vocabularies",
       "code",
       "content_page",
       "content_title",
       "csn",
       "data_type",
       "dictionary",
       "dir",
       "direction",
       "display_app_logo",
       "emailaddress",
       "format",
       "from_download",
       "home",
       "id",
       "initial_search",
       "key",
       "m",
       "mappingSearch",
       "matchText",
       "message",
       "mode",
       "multiplematches",
       "n",
       "nav_type",
       "ncbo_id",
       "ns",
       "ontology_display_name",
       "ontology_list_str",
       "ontology_node_id",
       "ontology_node_ns",
       "ontology_version",
       "opt",
       "page_number",
       "partialCheckedNodes",
       "partial_checked_vocabularies",
       "prop",
       "referer",
       "refresh",
       "rel",
       "rel_search_association",
       "rel_search_direction",
       "rel_search_rela",
       "rela",
       "report",
       "resultsPerPage",
       "root_vsd_uri",
       "sab",
       "schema",
       "scheme",
       "scheme_and_version",
       "searchTarget",
       "searchTerm",
       "selected_vocabularies",
       "selectedOntology",
       "selectProperty",
       "selectPropertyType",
       "selectSearchOption",
       "selectValueSetSearchOption",
       "single_mapping_search",
       "sortBy",
       "source",
       "subject",
       "target",
       "text",
       "type",
       "uri",
       "value_set_home",
       "value_set_tab",
       "valueset",
       "valueset_search_algorithm",
       "version",
       "view",
       "vocabulary",
       "vsd_uri",
       "vse",
       "javax.faces.ViewState",
       "ontology_list",
       "ontology_source",
       "search_value_set",
       "searchTerm:search.x",
       "searchTerm:search.y",
       "searchTerm:multiple_search.x",
       "searchTerm:multiple_search.y",
       "advancedSearchForm",
       "advancedSearchForm:adv_search.x",
       "advancedSearchForm:adv_search.y",
       "valueSetSearchForm",
       "valueSetSearchForm:valueset_search",
       "valueSetSearchForm:valueset_search.x",
       "valueSetSearchForm:valueset_search.y",
       "value_set_home",
       "valueset_search_algorithm",
       "searchTerm:multi_search.x",
       "searchTerm:multi_search.y",
       "searchTerm:multipleSearch.x",
       "searchTerm:multipleSearch.y",
       "resolvedValueSetSearchForm"
    };

    public static final List HTTP_REQUEST_PARAMETER_NAME_LIST = Arrays.asList(HTTP_REQUEST_PARAMETER_NAMES);

	public static final String[] adv_search_algorithm_values = new String[] {"contains", "exactMatch", "lucene", "startsWith"};
	public static final String[] algorithm_values = new String[] {"contains", "exactMatch", "startsWith"};
	public static final String[] direction_values = new String[] {"source", "target"};
	public static final String[] searchTarget_values = new String[] {"codes", "names", "properties", "relationships"};
	public static final String[] selectSearchOption_values = new String[] {"Code", "Name", "Property", "Relationship"};
	public static final String[] selectValueSetSearchOption_values = new String[] {"Code", "CodingScheme", "Name", "Source"};
	public static final String[] valueset_search_algorithm_values = new String[] {"contains", "exactMatch", "startsWith"};

	public static List adv_search_algorithm_value_list = Arrays.asList(adv_search_algorithm_values);
	public static List algorithm_value_list = Arrays.asList(algorithm_values);
	public static List direction_value_list = Arrays.asList(direction_values);
	public static List searchTarget_value_list = Arrays.asList(searchTarget_values);
	public static List selectSearchOption_value_list = Arrays.asList(selectSearchOption_values);
	public static List selectValueSetSearchOption_value_list = Arrays.asList(selectValueSetSearchOption_values);
	public static List valueset_search_algorithm_value_list = Arrays.asList(valueset_search_algorithm_values);

    /**
     * Constructor
     */
    private HTTPParameterConstants() {
        // Prevent class from being explicitly instantiated
    }


} // Class HTTPParameterConstants
