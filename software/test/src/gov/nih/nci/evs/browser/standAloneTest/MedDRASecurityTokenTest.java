package gov.nih.nci.evs.browser.standAloneTest;

import gov.nih.nci.evs.browser.test.utils.*;
import gov.nih.nci.evs.security.SecurityToken;
import gov.nih.nci.system.client.*;

import org.LexGrid.LexBIG.DataModel.Core.*;
import org.LexGrid.LexBIG.LexBIGService.*;
import org.LexGrid.LexBIG.caCore.interfaces.*;
import org.apache.log4j.*;

public class MedDRASecurityTokenTest {
    private static Logger _logger = getLogger();

    private static Logger getLogger() {
        Logger.getLogger("gov.nih.nci.evs").setLevel(Level.DEBUG);
        Class<MedDRASecurityTokenTest> c = MedDRASecurityTokenTest.class;
        Logger.getLogger(c).setLevel(Level.DEBUG);
        return Logger.getLogger(c);
    }

    public CodedNodeSet getCNSTest(String lexevsURL, String scheme,
        String version) throws Exception {
        _logger.debug("------------------------------------------------------");
        _logger.debug("lexevsURL: " + lexevsURL);
        _logger.debug("scheme: " + scheme);
        _logger.debug("version: " + version);
        LexBIGService lbs =
            MyRemoteServerUtil.createLexBIGService(lexevsURL, scheme, version);
        CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
        if (version != null)
            versionOrTag.setVersion(version);
        CodedNodeSet cns = lbs.getCodingSchemeConcepts(scheme, versionOrTag);
        _logger.debug("cns: " + cns);
        _logger.debug("");
        return cns;
    }

    public void test() throws Exception {
        String lexevsURL = "http://ncias-d499-v.nci.nih.gov:29080/lexevsapi60";
        String scheme =
            "MedDRA (Medical Dictionary for Regulatory Activities Terminology)";
        String version = "12.0";

        getCNSTest(lexevsURL, scheme, version);
        try {
            scheme = "MedDRA";
            getCNSTest(lexevsURL, scheme, version);
        } catch (Exception e) {
            _logger.error("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        new MedDRASecurityTokenTest().test();
    }

    public static class MyRemoteServerUtil {
        private static Logger _logger = Logger
            .getLogger(MyRemoteServerUtil.class);

        public static LexBIGService createLexBIGService(String serviceUrl,
            String scheme, String version) throws Exception {
            LexEVSApplicationService lexevsService =
                (LexEVSApplicationService) ApplicationServiceProvider
                    .getApplicationServiceFromUrl(serviceUrl, "EvsServiceInfo");
            lexevsService =
                registerSecurityToken(lexevsService, scheme, "10382");
            return (LexBIGService) lexevsService;
        }

        public static LexEVSApplicationService registerSecurityToken(
            LexEVSApplicationService lexevsService, String codingScheme,
            String token) {
            SecurityToken securityToken = new SecurityToken();
            securityToken.setAccessToken(token);
            Boolean retval = null;
            try {
                retval =
                    lexevsService.registerSecurityToken(codingScheme,
                        securityToken);
                if (retval != null && retval.equals(Boolean.TRUE)) {
                    // _logger.debug("Registration of SecurityToken was successful.");
                } else {
                    _logger
                        .error("WARNING: Registration of SecurityToken failed.");
                }
            } catch (Exception e) {
                _logger.error("WARNING: Registration of SecurityToken failed.");
            }
            return lexevsService;
        }
    }
}
