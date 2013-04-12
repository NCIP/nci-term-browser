/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.utils;

import java.util.*;
import javax.servlet.http.*;
import org.apache.log4j.*;
import gov.nih.nci.evs.browser.bean.*;

/**
 * 
 */

/**
 * @author EVS Team
 * @version 1.0
 */

public class LicenseUtils {
    private static Logger _logger = Logger.getLogger(LicenseUtils.class);

    private static LicenseBean getLicenseBean(HttpServletRequest request) {
        LicenseBean licenseBean =
            (LicenseBean) request.getSession().getAttribute("licenseBean");
        if (licenseBean == null) {
            licenseBean = new LicenseBean();
            request.getSession().setAttribute("licenseBean", licenseBean);
        }
        return licenseBean;
    }

    public static LexEVSUtils.CSchemes getUnacceptedLicenses(
        HttpServletRequest request, List<String> ontologiesToSearchOn) {
        LicenseBean licenseBean = getLicenseBean(request);
        LexEVSUtils.CSchemes cSchemes = new LexEVSUtils.CSchemes();
        for (int i = 0; i < ontologiesToSearchOn.size(); i++) {
            String key = (String) ontologiesToSearchOn.get(i);
            String scheme = DataUtils.key2CodingSchemeName(key);
            String version = DataUtils.key2CodingSchemeVersion(key);
            boolean isLicensed = LicenseBean.isLicensed(scheme, version);
            boolean accepted = licenseBean.licenseAgreementAccepted(scheme);
            if (isLicensed && !accepted)
                cSchemes.add(scheme, version);
        }
        return cSchemes;
    }

    public static void acceptLicense(HttpServletRequest request, String scheme) {
        LicenseBean licenseBean = getLicenseBean(request);
        licenseBean.addLicenseAgreement(scheme);
    }

    public static void acceptLicenses(HttpServletRequest request,
        LexEVSUtils.CSchemes schemes) {
        LicenseBean licenseBean = getLicenseBean(request);
        Iterator<LexEVSUtils.CScheme> iterator = schemes.iterator();
        while (iterator.hasNext()) {
            LexEVSUtils.CScheme cScheme = iterator.next();
            licenseBean.addLicenseAgreement(cScheme.getCodingScheme());
        }
    }

    public static boolean isLicensedAndNotAccepted(HttpServletRequest request,
        String scheme, String version) {
        boolean isLicensed = LicenseBean.isLicensed(scheme, version);
        if (! isLicensed)
            return false;
        
        LicenseBean licenseBean =
            (LicenseBean) request.getSession().getAttribute("licenseBean");
        if (licenseBean == null) {
            licenseBean = new LicenseBean();
            request.getSession().setAttribute("licenseBean", licenseBean);
        }
        boolean accepted = licenseBean.licenseAgreementAccepted(scheme);
        boolean value = isLicensed && !accepted;
        return value;
    }

    public static void clearAllLicenses(HttpServletRequest request) {
        _logger.debug(Utils.SEPARATOR);
        _logger.debug("Clear all licenses");
        LicenseBean licenseBean = getLicenseBean(request);
        licenseBean.clearAllLicenseAgreements();
    }

    public static class WebPageHelper {
        private LexEVSUtils.CSchemes _schemes = null;

        public WebPageHelper(LexEVSUtils.CSchemes schemes) {
            _schemes = schemes;
        }

        public WebPageHelper(String scheme, String version) {
            _schemes = new LexEVSUtils.CSchemes();
            _schemes.add(new LexEVSUtils.CScheme(scheme, version));
        }

        public String getReviewAndAcceptMessage() {
            StringBuffer buffer = new StringBuffer();
            buffer.append("To access <b>");
            Vector<String> list = _schemes.getCodingSchemes();
            list = Utils.unique(list);

            int n = list.size();
            for (int i = 0; i < n; ++i) {
                String scheme = list.get(i);
                if (n > 1 && i == (n - 1))
                    buffer.append(" and ");
                else if (i > 0)
                    buffer.append(", ");
                buffer.append(DataUtils
                    .getMetadataValue(scheme, "display_name"));
            }
            buffer.append("</b>, please review and accept the ");
            buffer.append("copyright/license statement below:");
            return buffer.toString();
        }

        public String getLicenseMessages(int maxChars) {
            StringBuffer buffer = new StringBuffer();
            HashSet<String> hset = new HashSet<String>();

            int n = _schemes.size();
            for (int i = 0; i < n; ++i) {
                LexEVSUtils.CScheme cScheme = _schemes.get(i);
                String scheme = cScheme.getCodingScheme();
                String version = cScheme.getVersion();
                if (hset.contains(scheme))
                    continue;
                if (i > 0)
                    buffer.append("\n");
                if (n > 1) {
                    String name = cScheme.getDisplayName();
                    String separator =
                        Utils.fill("== " + name + " License: ", '=', maxChars);
                    buffer.append(separator + "\n");
                }
                buffer.append(LicenseBean.resolveCodingSchemeCopyright(scheme,
                    version).trim());
                buffer.append("\n");
                hset.add(cScheme.getCodingScheme());
            }
            return buffer.toString();
        }

        public String getButtonMessage() {
            return "If and only if you agree to these terms and conditions,"
                + " click the Accept button to proceed.";
        }
    }
}
