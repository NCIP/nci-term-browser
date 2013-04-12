/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package org.LexGrid.LexBIG.util;

import gov.nih.nci.system.applicationservice.EVSApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;

import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;

/**
 * 
 */

/**
 * @author EVS Team
 * @version 1.0
 * 
 * Modification history Initial implementation kim.ong@ngc.com
 * 
 */

public class RemoteServerUtil2 {
    private static String _serviceInfo = "EvsServiceInfo";
    public static final String URL = "http://lexevsapi.nci.nih.gov/lexevsapi42";
    public static final String URL_DEV = "http://lexevsapi-dev.nci.nih.gov/lexevsapi42";
    public static String _serviceUrl = URL;

    /**
     * Establish a remote LexBIG connection.
     */
    public static EVSApplicationService createLexBIGService() {
        EVSApplicationService lbSvc = null;

        try {
            System.out.println("Service URL: " + _serviceUrl);
            System.out.flush();
            lbSvc = (EVSApplicationService) ApplicationServiceProvider
                    .getApplicationServiceFromUrl(_serviceUrl, _serviceInfo);
            return lbSvc;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Establish a remote LexBIG connection.
     * 
     */
    public static EVSApplicationService createLexBIGService(String url) {
        if (url == null) {
            LexBIGService lbs = null;
            try {
                lbs = new LexBIGServiceImpl();
                return (EVSApplicationService) lbs;
            } catch (Exception ex) {
                return null;
            }
        }

        EVSApplicationService lbSvc = null;
        try {
            lbSvc = (EVSApplicationService) ApplicationServiceProvider
                    .getApplicationServiceFromUrl(url, _serviceInfo);

            return lbSvc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
