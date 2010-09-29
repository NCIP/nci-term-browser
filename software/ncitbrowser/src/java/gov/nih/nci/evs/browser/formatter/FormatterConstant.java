package gov.nih.nci.evs.browser.formatter;

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

    public static final int[] CDISC_SDTM_NCIT_COLUMNS = new int[] { 0, 1 };
    public static final int[] CDISC_SUBSET_NCIT_COLUMNS = new int[] { 1, 3 };
    public static final int[] CDRH_NCIT_COLUMNS = new int[] { 1, 3, 9 };
    public static final int[] FDA_SPL_NCIT_COLUMNS = new int[] { 1 };
    public static final int[] FDA_UNII_NCIT_COLUMNS = new int[] { 2 };
    public static final int[] ICS_SUBSET_NCIT_COLUMNS = new int[] { 1, 3 };
    public static final int[] SPL_NCIT_COLUMNS = new int[] { 1, 3 };
}
