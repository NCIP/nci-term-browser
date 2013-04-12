/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.formatter;

/**
 * 
 */

/**
 * @author EVS Team (David Yee)
 * @version 1.0
 */

public interface FormatterConstant {
//    public static final String DOWNLOAD_DIR = AppProperties.getInstance()
//        .getProperty(AppProperties.REPORT_DOWNLOAD_DIRECTORY);

    public static final String DOWNLOAD_DIR =
        "c:/apps/evs/ncireportwriter-webapp/downloads/";
    public static final String CDISC_SDTM_FILE = DOWNLOAD_DIR
        + "/CDISC_SDTM_Terminology__10.06e.txt";
    public static final String CDISC_SUBSET_FILE = DOWNLOAD_DIR
        + "/CDISC_Subset_REPORT__10.06e.txt";
    public static final String CDRH_SUBSET_FILE = DOWNLOAD_DIR
        + "/CDRH_Subset_REPORT__10.06e.txt";
    public static final String FDA_SPL_FILE = DOWNLOAD_DIR
        + "/FDA-SPL_Country_Code_REPORT__10.06e.txt";
    public static final String FDA_UNII_FILE = DOWNLOAD_DIR
        + "/FDA-UNII_Subset_REPORT__10.06e.txt";
    public static final String ICS_SUBSET_FILE = DOWNLOAD_DIR
        + "/Individual_Case_Safety_(ICS)_Subset_REPORT__10.06e.txt";
    public static final String SPL_FILE = DOWNLOAD_DIR
        + "/Structured_Product_Labeling_(SPL)_REPORT__10.06e.txt";

    public static final String FTP_URL = "http://evs.nci.nih.gov/ftp1";
    public static final String CDISC_SDTM_REPORT_URL = FTP_URL
        + "/CDISC/SDTM/SDTM%20Terminology.txt";
    public static final String CDRH_REPORT_URL = FTP_URL
        + "/FDA/CDRH/FDA-CDRH_NCIt_Subsets.txt";
    public static final String FDA_SPL_REPORT_URL = FTP_URL
        + "/FDA/SPL/FDA-SPL_Country_Codes.txt";
    public static final String FDA_UNII_REPORT_URL = FTP_URL
        + "/FDA/UNII/FDA-UNII_NCIt_Subsets.txt";

/*
    public static final int[] CDISC_SDTM_NCIT_COLUMNS = new int[] { 0, 1 };
    public static final int[] CDISC_SUBSET_NCIT_COLUMNS = new int[] { 1, 3 };
    public static final int[] CDRH_NCIT_COLUMNS = new int[] { 1, 3, 9 };
    public static final int[] FDA_SPL_NCIT_COLUMNS = new int[] { 1 };
    public static final int[] FDA_UNII_NCIT_COLUMNS = new int[] { 2 };
    public static final int[] ICS_SUBSET_NCIT_COLUMNS = new int[] { 1, 3 };
    public static final int[] SPL_NCIT_COLUMNS = new int[] { 1, 3 };
*/

}
