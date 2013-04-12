/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.utils;

import gov.nih.nci.system.applicationservice.EVSApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;
//DYEE: import gov.nih.nci.evs.browser.properties.NCItBrowserProperties;
import java.util.Hashtable;
import java.util.Properties;

import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;
/**
  * 
  */
import org.apache.log4j.Logger;

/**
  * @author EVS Team
  * @version 1.0
  *
  * Modification history
  *     Initial implementation kim.ong@ngc.com
  *
 */

public class RemoteServerUtil {
    private static Logger _logger = Logger.getLogger(RemoteServerUtil.class);
	private static String _serviceInfo = "EvsServiceInfo";
	private Properties systemProperties = null;

    //public static EVSApplicationService  appService = null;
    //public static LexBIGService lbSvc;
    //private static String serviceUrl = null;

    public RemoteServerUtil() {

    }


	public static LexBIGService createLexBIGService()
    {
		// default URL (to be read from a property file)
		String url = "http://lexevsapi.nci.nih.gov/lexevsapi42";
        //url = null; //DYEE
        /* DYEE
		NCItBrowserProperties properties = null;
		try {
		    properties = NCItBrowserProperties.getInstance();
			url = properties.getProperty(NCItBrowserProperties.EVS_SERVICE_URL);
	    } catch (Exception ex) {

		}
		*/
		return createLexBIGService(url);
	}


	public static LexBIGService createLexBIGService(String serviceUrl)
    {
        try{
	        _logger.debug(Util.SEPARATOR);
			if (serviceUrl == null || serviceUrl.compareTo("") == 0)
			{
		        _logger.debug("LexBIGService(local): new LexBIGServiceImpl();");
		        _logger.debug("LG_CONFIG_FILE: " + System.getProperty("LG_CONFIG_FILE"));
				LexBIGService lbSvc = new LexBIGServiceImpl();
				return lbSvc;
			}
            _logger.debug("LexBIGService(remote): " + serviceUrl);
//            try {
//                throw new Exception("Not an exception.  Used to trace where this method was called.");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

		    EVSApplicationService appService = (EVSApplicationService) ApplicationServiceProvider.getApplicationServiceFromUrl(serviceUrl, _serviceInfo);
           return (LexBIGService) appService;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

}
