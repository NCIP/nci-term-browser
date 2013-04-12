/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.bean;

import java.util.*;

import org.LexGrid.LexBIG.DataModel.Core.*;
import org.LexGrid.LexBIG.LexBIGService.*;
import org.apache.log4j.*;

import gov.nih.nci.evs.browser.utils.*;
import gov.nih.nci.evs.browser.properties.*;

/**
 * 
 */

/**
 * @author EVS Team
 * @version 1.0
 *
 *          Modification history Initial implementation kim.ong@ngc.com
 *
 */

public class LicenseBean extends Object {
    private static Logger _logger = Logger.getLogger(LicenseBean.class);
    private HashSet _licenseAgreementHashSet = null;

    public LicenseBean() {
        _licenseAgreementHashSet = new HashSet();
    }

    public void addLicenseAgreement(String scheme) {
        _logger.debug("LicenseBean addLicenseAgreement " + scheme);
        _licenseAgreementHashSet.add(scheme);
    }

    public void clearAllLicenseAgreements() {
        _licenseAgreementHashSet = new HashSet();
    }

    public boolean licenseAgreementAccepted(String scheme) {
        // option to not pop-up the license agreement page:
        String license_page_option =
            NCItBrowserProperties.getLicensePageOption();
        if (license_page_option.compareToIgnoreCase("true") != 0)
            return true;

        boolean retval = _licenseAgreementHashSet.contains(scheme);
        return (retval);
    }

    public static boolean isLicensed(String codingSchemeName, String version) {
        // MedDRA, SNOMED CT, and UMLS Semantic Network.
        String license_display = null;

        license_display =
            getLicenseDisplay(codingSchemeName, "license_display");
        // if (license_display != null && (license_display.compareTo("show") ==
        // 0 || license_display.compareTo("accept") == 0)) return true;
        if (license_display != null && license_display.compareTo("accept") == 0)
            return true;

        return false;
    }

    public static String getLicenseDisplay(String codingSchemeName,
        String version) {
        // MedDRA, SNOMED CT, and UMLS Semantic Network.
        String license_display = null;
        return DataUtils.getMetadataValue(codingSchemeName, "license_display");
    }

    public static String resolveCodingSchemeCopyright(String codingSchemeName,
        String version) {
        //LexBIGService lbs = RemoteServerUtil.createLexBIGService();
        CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
        if (version != null)
            versionOrTag.setVersion(version);
        String copyRightStmt = null;
        try {
            // [#24879] Update UMLS SN licence metadata
            copyRightStmt =
                DataUtils.getMetadataValue(codingSchemeName,
                    "license_statement");
        } catch (Exception ex) {
        }
        return copyRightStmt;
    }
}