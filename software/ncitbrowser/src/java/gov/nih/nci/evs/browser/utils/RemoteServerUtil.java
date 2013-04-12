/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.utils;

import java.util.*;

import org.LexGrid.LexBIG.caCore.interfaces.*;
import org.LexGrid.LexBIG.LexBIGService.*;
import org.LexGrid.LexBIG.Impl.*;

import gov.nih.nci.evs.browser.properties.*;
import gov.nih.nci.system.client.*;
import gov.nih.nci.evs.security.*;
import gov.nih.nci.evs.browser.bean.*;
import org.apache.log4j.*;

import org.LexGrid.LexBIG.caCore.interfaces.LexEVSDistributed;
import org.lexgrid.valuesets.LexEVSValueSetDefinitionServices;
import org.lexgrid.valuesets.impl.LexEVSValueSetDefinitionServicesImpl;


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

public class RemoteServerUtil {
    private static Logger _logger = Logger.getLogger(RemoteServerUtil.class);
    private static boolean _debug = false;
    private static String _serviceInfo = "EvsServiceInfo";
    private static String _serviceURL = null;

    public RemoteServerUtil() {
        // Do nothing
    }

    public static LexBIGService createLexBIGService() {
        String url = "http://ncias-d488-v.nci.nih.gov:29080/lexevsapi60";

        url = "http://localhost:8080/lexevsapi60";

        NCItBrowserProperties properties = null;
        try {
            properties = NCItBrowserProperties.getInstance();
            url = properties.getProperty(NCItBrowserProperties.EVS_SERVICE_URL);
            return createLexBIGService(url);
        } catch (Exception ex) {
            // Do nothing
            // _logger.error("WARNING: NCItBrowserProperties loading error...");
            // _logger.error("\t-- trying to connect to " + url + " instead.");
            ex.printStackTrace();
        }
        return null;// createLexBIGService(url);
    }

    public static LexBIGService createLexBIGService(String serviceUrl) {
        try {
            NCItBrowserProperties properties = null;
            properties = NCItBrowserProperties.getInstance();

            if (serviceUrl == null || serviceUrl.compareTo("") == 0 || serviceUrl.compareToIgnoreCase("null") == 0) {
                String lg_config_file =
                    properties
                        .getProperty(NCItBrowserProperties.LG_CONFIG_FILE);
                System.setProperty(NCItBrowserProperties.LG_CONFIG_FILE,
                    lg_config_file);
                LexBIGService lbSvc = LexBIGServiceImpl.defaultInstance();
                return lbSvc;
            }
            if (_debug) {
                _logger.debug(Utils.SEPARATOR);
                _logger.debug("LexBIGService(remote): " + serviceUrl);
            }
            LexEVSApplicationService lexevsService =
                (LexEVSApplicationService) ApplicationServiceProvider
                    .getApplicationServiceFromUrl(serviceUrl, "EvsServiceInfo");
            lexevsService = registerAllSecurityTokens(lexevsService);
            return (LexBIGService) lexevsService;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // KLO 100709
    public static LexEVSApplicationService registerAllSecurityTokens(
        LexEVSApplicationService lexevsService) {
        List list = NCItBrowserProperties.getSecurityTokenList();
        if (list == null || list.size() == 0)
            return lexevsService;
        for (int i = 0; i < list.size(); i++) {
            SecurityTokenHolder holder = (SecurityTokenHolder) list.get(i);
            lexevsService =
                registerSecurityToken(lexevsService, holder.getName(), holder
                    .getValue());
        }
        return lexevsService;
    }

    // KLO 100709
    public static LexEVSApplicationService registerSecurityToken(
        LexEVSApplicationService lexevsService, String codingScheme,
        String token) {
        SecurityToken securityToken = new SecurityToken();
        securityToken.setAccessToken(token);
        Boolean retval = null;
        try {
            retval =
                lexevsService
                    .registerSecurityToken(codingScheme, securityToken);
            if (retval != null && retval.equals(Boolean.TRUE)) {
                // _logger.debug("Registration of SecurityToken was successful.");
            } else {
                _logger.error("WARNING: Registration of SecurityToken failed.");
            }
        } catch (Exception e) {
            _logger.error("WARNING: Registration of SecurityToken failed.");
        }
        return lexevsService;
    }

    // KLO 100709
    public static LexBIGService createLexBIGService(String serviceUrl,
        String codingScheme, String token) {
        SecurityToken securityToken = new SecurityToken();
        securityToken.setAccessToken(token);
        return createLexBIGService(serviceUrl, codingScheme, securityToken);
    }

    // KLO 100709
    public static LexBIGService createLexBIGService(String serviceUrl,
        String codingScheme, SecurityToken securityToken) {
        try {
            if (serviceUrl == null || serviceUrl.compareTo("") == 0 || serviceUrl.compareToIgnoreCase("null") == 0) {
                LexBIGService lbSvc = LexBIGServiceImpl.defaultInstance();
                return lbSvc;
            }

            LexEVSApplicationService lexevsService =
                (LexEVSApplicationService) ApplicationServiceProvider
                    .getApplicationServiceFromUrl(serviceUrl, "EvsServiceInfo");

            Boolean retval = false;
            retval =
                lexevsService
                    .registerSecurityToken(codingScheme, securityToken);

            if (retval.equals(Boolean.TRUE)) {
                // _logger.debug("Registration of SecurityToken was successful.");
            } else {
                _logger.error("WARNING: Registration of SecurityToken failed.");
            }

            _logger.debug("Connected to " + serviceUrl);
            return (LexBIGService) lexevsService;
        } catch (Exception e) {
            _logger.error("Unable to connected to " + serviceUrl);
            e.printStackTrace();
        }
        return null;
    }

    public static String getServiceURL() {
        return _serviceURL;
    }

    public static LexBIGService createLexBIGService(
        boolean registerSecurityTokens) {
        String url = "http://ncias-d177-v.nci.nih.gov:19480/lexevsapi51";
        url = "http://localhost:8080/lexevsapi60";

        NCItBrowserProperties properties = null;
        try {
            properties = NCItBrowserProperties.getInstance();
            url = properties.getProperty(NCItBrowserProperties.EVS_SERVICE_URL);
            return createLexBIGService(url, registerSecurityTokens);
        } catch (Exception ex) {
            // Do nothing
            // _logger.error("WARNING: NCItBrowserProperties loading error...");
            // _logger.error("\t-- trying to connect to " + url + " instead.");
            ex.printStackTrace();
        }
        return null;// createLexBIGService(url);
    }

    public static LexBIGService createLexBIGService(String serviceUrl,
        boolean registerSecurityTokens) {
        try {

            // To be evaluated:
            /*
             * NCItBrowserProperties properties = null; properties =
             * NCItBrowserProperties.getInstance();
             *
             * if (serviceUrl == null || serviceUrl.compareTo("") == 0) { String
             * lg_config_file =
             * properties.getProperty(NCItBrowserProperties.LG_CONFIG_FILE);
             * System
             * .setProperty(NCItBrowserProperties.LG_CONFIG_FILE,lg_config_file
             * ); LexBIGService lbSvc = LexBIGServiceImpl.defaultInstance(); return lbSvc; }
             * if (debug) { _logger.debug(Utils.SEPARATOR);
             * _logger.debug("LexBIGService(remote): " + serviceUrl); }
             */
            LexEVSApplicationService lexevsService =
                (LexEVSApplicationService) ApplicationServiceProvider
                    .getApplicationServiceFromUrl(serviceUrl, "EvsServiceInfo");
            if (registerSecurityTokens)
                lexevsService = registerAllSecurityTokens(lexevsService);
            return (LexBIGService) lexevsService;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    public static LexEVSDistributed getLexEVSDistributed() {
		String url = "http://ncias-d488-v.nci.nih.gov:29080/lexevsapi60";
		NCItBrowserProperties properties = null;
		try {
            properties = NCItBrowserProperties.getInstance();
            url = properties.getProperty(NCItBrowserProperties.EVS_SERVICE_URL);
            return getLexEVSDistributed(url);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
	}


    public static LexEVSDistributed getLexEVSDistributed(String serviceUrl) {
		try {
			LexEVSDistributed distributed =
				(LexEVSDistributed)
				ApplicationServiceProvider.getApplicationServiceFromUrl(serviceUrl, "EvsServiceInfo");

			return distributed;
		} catch (Exception e) {
            e.printStackTrace();
        }
        return null;
	}


    public static LexEVSValueSetDefinitionServices getLexEVSValueSetDefinitionServices() {

		NCItBrowserProperties properties = null;
		try {
            properties = NCItBrowserProperties.getInstance();
            String serviceUrl = properties.getProperty(NCItBrowserProperties.EVS_SERVICE_URL);
            if (serviceUrl == null || serviceUrl.compareTo("") == 0 || serviceUrl.compareToIgnoreCase("null") == 0) {
				return LexEVSValueSetDefinitionServicesImpl.defaultInstance();
			}
			LexEVSDistributed distributed = getLexEVSDistributed();
			LexEVSValueSetDefinitionServices vds = distributed.getLexEVSValueSetDefinitionServices();
			return vds;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
	}


    public static LexEVSValueSetDefinitionServices getLexEVSValueSetDefinitionServices(String serviceUrl) {
		try {
			LexEVSDistributed distributed =
				(LexEVSDistributed)
				ApplicationServiceProvider.getApplicationServiceFromUrl(serviceUrl, "EvsServiceInfo");

			LexEVSValueSetDefinitionServices vds = distributed.getLexEVSValueSetDefinitionServices();
			return vds;
		} catch (Exception e) {
            e.printStackTrace();
        }
        return null;
	}

}
