/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.bean;

import java.io.*;
import java.util.HashSet;
import java.util.Vector;

import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.LexBIG.Exceptions.LBException;

//import gov.nih.nci.evs.browser.utils.RemoteServerUtil;
import gov.nih.nci.evs.browser.utils.*;
import gov.nih.nci.evs.browser.properties.NCItBrowserProperties;
import gov.nih.nci.evs.browser.common.Constants;


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

public class LicenseBean extends Object {

    HashSet licenseAgreementHashSet = null;

    public LicenseBean() {
        licenseAgreementHashSet = new HashSet();
    }

    public void addLicenseAgreement(String scheme) {
		System.out.println("LicenseBean addLicenseAgreement " +  scheme);
		licenseAgreementHashSet.add(scheme);
	}

    public boolean licenseAgreementAccepted(String scheme) {
        // option to not pop-up the license agreement page:
		String license_page_option = NCItBrowserProperties.getLicensePageOption();
		if (license_page_option.compareToIgnoreCase("true") != 0) return true;

		boolean retval = licenseAgreementHashSet.contains(scheme);
		return (retval);
	}

    public static boolean isLicensed(String codingSchemeName, String version) {
		//MedDRA, SNOMED CT, and UMLS Semantic Network.
        String license_display = null;

        license_display = getLicenseDisplay(codingSchemeName, "license_display");
        //if (license_display != null && (license_display.compareTo("show") == 0 || license_display.compareTo("accept") == 0)) return true;
        if (license_display != null && license_display.compareTo("accept") == 0) return true;

        return false;
    }


    public static String getLicenseDisplay(String codingSchemeName, String version) {
		//MedDRA, SNOMED CT, and UMLS Semantic Network.
        String license_display = null;
        return DataUtils.getMetadataValue(codingSchemeName, "license_display");
    }

	public static String resolveCodingSchemeCopyright(String codingSchemeName, String version) {
		LexBIGService lbs = RemoteServerUtil.createLexBIGService();
		CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
		if (version != null) versionOrTag.setVersion(version);
		String copyRightStmt = null;
		try {
			//[#24879] Update UMLS SN licence metadata
			copyRightStmt = DataUtils.getMetadataValue(codingSchemeName, "license_statement");
		} catch (Exception ex) {
		}
		return copyRightStmt;
	}

}