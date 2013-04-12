/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.properties;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Properties;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;

import org.apache.log4j.Logger;

/**
  * 
  */

/**
  * @author EVS Team
  * @version 1.0
  *
  * Modification history
  *     Initial implementation kim.ong@ngc.com
  *
 */

public class NCItBrowserProperties {

        private static List displayItemList;
        private static List metadataElementList;
        private static List defSourceMappingList;
        private static HashMap defSourceMappingHashMap;
        private static List securityTokenList;
        private static HashMap securityTokenHashMap;
        private static HashMap configurableItemMap;

        // KLO
        public static final String DEBUG_ON = "DEBUG_ON";
        public static final String EVS_SERVICE_URL = "EVS_SERVICE_URL";
        public static final String LG_CONFIG_FILE = "LG_CONFIG_FILE";
        public static final String MAXIMUM_RETURN = "MAXIMUM_RETURN";
        public static final String EHCACHE_XML_PATHNAME = "EHCACHE_XML_PATHNAME";
        public static final String SORT_BY_SCORE = "SORT_BY_SCORE";
        public static final String MAIL_SMTP_SERVER = "MAIL_SMTP_SERVER";
        public static final String NCICB_CONTACT_URL = "NCICB_CONTACT_URL";
        public static final String MAXIMUM_TREE_LEVEL = "MAXIMUM_TREE_LEVEL";
        public static final String TERMINOLOGY_SUBSET_DOWNLOAD_URL= "TERMINOLOGY_SUBSET_DOWNLOAD_URL";
        public static final String NCIT_BUILD_INFO = "NCIT_BUILD_INFO";
        public static final String NCIT_APP_VERSION = "APPLICATION_VERSION";
        public static final String ANTHILL_BUILD_TAG_BUILT = "ANTHILL_BUILD_TAG_BUILT";
        public static final String NCIM_URL = "NCIM_URL";
        public static final String NCIT_URL = "NCIT_URL";
        public static final String TERM_SUGGESTION_APPLICATION_URL= "TERM_SUGGESTION_APPLICATION_URL";
        public static final String LICENSE_PAGE_OPTION= "LICENSE_PAGE_OPTION";

        public static final String PAGINATION_TIME_OUT= "PAGINATION_TIME_OUT";
        public static final String MINIMUM_SEARCH_STRING_LENGTH = "MINIMUM_SEARCH_STRING_LENGTH";
        public static final String SLIDING_WINDOW_HALF_WIDTH = "SLIDING_WINDOW_HALF_WIDTH";


        private static Logger log = Logger.getLogger(NCItBrowserProperties.class);
        private static NCItBrowserProperties NCItBrowserProperties = null;
        private static Properties properties = new Properties();

        public static boolean debugOn = false;
        private static int maxToReturn = 1000;
        private static int maxTreeLevel = 1000;
        private static String service_url = null;
        private static String lg_config_file = null;

        private static String sort_by_score = null;
        private static String mail_smtp_server = null;
        private static String ncicb_contact_url = null;
        private static String terminology_subset_download_url = null;
        private static String term_suggestion_application_url = null;

        private static String license_page_option = null;
        private static String ncim_url = null;
        private static String ncit_url = null;
        private static int    pagination_time_out = 4;
        private static int    minimum_search_string_length = 1;

        private static int    sliding_window_half_width = 5;

        /**
         * Private constructor for singleton pattern.
         */
        private NCItBrowserProperties() {}

        /**
         * Gets the single instance of NCItBrowserProperties.
         *
         * @return single instance of NCItBrowserProperties
         *
         * @throws Exception the exception
         */
        public static NCItBrowserProperties getInstance() throws Exception{
            if(NCItBrowserProperties == null) {
                synchronized(NCItBrowserProperties.class) {

                    if(NCItBrowserProperties == null) {
                        NCItBrowserProperties = new NCItBrowserProperties();
                        loadProperties();

                        debugOn = Boolean.parseBoolean(getProperty(DEBUG_ON));

                        String max_str = NCItBrowserProperties.getProperty(NCItBrowserProperties.MAXIMUM_RETURN);
                        maxToReturn = Integer.parseInt(max_str);

                        String max_tree_level_str = NCItBrowserProperties.getProperty(NCItBrowserProperties.MAXIMUM_TREE_LEVEL);
                        maxTreeLevel = Integer.parseInt(max_tree_level_str);

                        service_url = NCItBrowserProperties.getProperty(NCItBrowserProperties.EVS_SERVICE_URL);
                        //System.out.println("EVS_SERVICE_URL: " + service_url);

                        lg_config_file = NCItBrowserProperties.getProperty(NCItBrowserProperties.LG_CONFIG_FILE);
                        //System.out.println("LG_CONFIG_FILE: " + lg_config_file);

                        sort_by_score = NCItBrowserProperties.getProperty(NCItBrowserProperties.SORT_BY_SCORE);
                        ncicb_contact_url = NCItBrowserProperties.getProperty(NCItBrowserProperties.NCICB_CONTACT_URL);
                        mail_smtp_server = NCItBrowserProperties.getProperty(NCItBrowserProperties.MAIL_SMTP_SERVER);
                        terminology_subset_download_url = NCItBrowserProperties.getProperty(NCItBrowserProperties.TERMINOLOGY_SUBSET_DOWNLOAD_URL);
                        term_suggestion_application_url = NCItBrowserProperties.getProperty(NCItBrowserProperties.TERM_SUGGESTION_APPLICATION_URL);
                        license_page_option = NCItBrowserProperties.getProperty(NCItBrowserProperties.LICENSE_PAGE_OPTION);
                        ncim_url = NCItBrowserProperties.getProperty(NCItBrowserProperties.NCIM_URL);
                        ncit_url = NCItBrowserProperties.getProperty(NCItBrowserProperties.NCIT_URL);

                        String pagination_time_out_str = NCItBrowserProperties.getProperty(NCItBrowserProperties.PAGINATION_TIME_OUT);
                        if (pagination_time_out_str != null) {
                            pagination_time_out = Integer.parseInt(pagination_time_out_str);
                        }

                        String minimum_search_string_length_str = NCItBrowserProperties.getProperty(NCItBrowserProperties.MINIMUM_SEARCH_STRING_LENGTH);
                        if (minimum_search_string_length_str != null) {
                            int min_search_string_length = Integer.parseInt(minimum_search_string_length_str);
                            if (min_search_string_length > 1) {
                                minimum_search_string_length = min_search_string_length;
                            }
                        }
                        String sliding_window_half_width_str = NCItBrowserProperties.getProperty(NCItBrowserProperties.SLIDING_WINDOW_HALF_WIDTH);
                        if (sliding_window_half_width_str != null) {
                            int sliding_window_halfwidth = Integer.parseInt(sliding_window_half_width_str);
                            if (sliding_window_halfwidth > 1) {
                                sliding_window_half_width = sliding_window_halfwidth;
                            }
                        }
                   }
                }
            }

            return NCItBrowserProperties ;
        }


        //public String getProperty(String key) throws Exception{
        public static String getProperty(String key) throws Exception{
            //return properties.getProperty(key);
            String ret_str = (String) configurableItemMap.get(key);
            if (ret_str == null) return null;
            if (ret_str.compareToIgnoreCase("null") == 0) return null;
            return ret_str;
        }


        public static List getDisplayItemList() {
            return displayItemList;
        }

        public static List getMetadataElementList() {
            return metadataElementList;
        }

        public static List getDefSourceMappingList() {
            return defSourceMappingList;
        }

        public static HashMap getDefSourceMappingHashMap() {
            return defSourceMappingHashMap;
        }

        public static List getSecurityTokenList() {
            return securityTokenList;
        }

        public static HashMap getSecurityTokenHashMap() {
            return securityTokenHashMap;
        }

        private static void loadProperties() throws Exception {
            String propertyFile = System.getProperty("gov.nih.nci.evs.browser.NCItBrowserProperties");
            log.info("NCItBrowserProperties File Location= "+ propertyFile);
            PropertyFileParser parser = new PropertyFileParser(propertyFile);
            parser.run();

            displayItemList = parser.getDisplayItemList();
            metadataElementList = parser.getMetadataElementList();
            defSourceMappingList = parser.getDefSourceMappingList();
            defSourceMappingHashMap = parser.getDefSourceMappingHashMap();
            securityTokenList = parser.getSecurityTokenList();
            securityTokenHashMap = parser.getSecurityTokenHashMap();

            configurableItemMap = parser.getConfigurableItemMap();

        }

        public static String getLicensePageOption() {
            return license_page_option;
        }

        public static String getNCIM_URL() {
            return ncim_url;
        }

        public static String getNCIT_URL() {
            return ncit_url;
        }

        public static int getPaginationTimeOut() {
            return pagination_time_out;
        }

        public static int getMinimumSearchStringLength() {
            return minimum_search_string_length;
        }

        public static int getSlidingWindowHalfWidth() {
            return sliding_window_half_width;
        }

    }
