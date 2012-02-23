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
  * <!-- LICENSE_TEXT_START -->
* Copyright 2008,2009 NGIT. This software was developed in conjunction with the National Cancer Institute,
* and so to the extent government employees are co-authors, any rights in such works shall be subject to Title 17 of the United States Code, section 105.
* Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
* 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the disclaimer of Article 3, below. Redistributions
* in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other
* materials provided with the distribution.
* 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
* "This product includes software developed by NGIT and the National Cancer Institute."
* If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself,
* wherever such third-party acknowledgments normally appear.
* 3. The names "The National Cancer Institute", "NCI" and "NGIT" must not be used to endorse or promote products derived from this software.
* 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize
* the recipient to use any trademarks owned by either NCI or NGIT
* 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
* MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE NATIONAL CANCER INSTITUTE,
* NGIT, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
* PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
* WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
  * <!-- LICENSE_TEXT_END -->
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
        public static final String NCIM_URL = "NCIM_URL";
        public static final String TERM_SUGGESTION_APPLICATION_URL= "TERM_SUGGESTION_APPLICATION_URL";

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


        public List getDisplayItemList() {
            return this.displayItemList;
        }


        private static void loadProperties() throws Exception {
            String propertyFile = System.getProperty("gov.nih.nci.evs.browser.NCItBrowserProperties");

            log.info("NCItBrowserProperties File Location= "+ propertyFile);

            PropertyFileParser parser = new PropertyFileParser(propertyFile);
            parser.run();

            displayItemList = parser.getDisplayItemList();
            configurableItemMap = parser.getConfigurableItemMap();
        }
    }
