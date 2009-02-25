package gov.nih.nci.evs.browser.properties;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.Logger;


/**
 * Singleton for accessing Report Writer Properties.
 *
 * @author <a href="mailto:rajasimhah@mail.nih.gov">Harsha Rajasimha</a>
 *
 */
public class NCItBrowserProperties {

       //KLO
		public static final String EVS_SERVICE_URL = "EVS_SERVICE_URL";
		public static final String REPORT_DOWNLOAD_DIRECTORY = "REPORT_DOWNLOAD_DIRECTORY";
		public static final String MAXIMUM_LEVEL = "MAXIMUM_LEVEL";
		public static final String MAXIMUM_RETURN = "MAXIMUM_RETURN";

	    private static Logger log = Logger.getLogger(NCItBrowserProperties.class);

		private static NCItBrowserProperties NCItBrowserProperties;

	    private static Properties properties = new Properties();


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
					}
				}
			}
			return NCItBrowserProperties ;
		}


	    //public String getProperty(String key) throws Exception{
		public static String getProperty(String key) throws Exception{
	    	return properties.getProperty(key);
	    }


	    private static void loadProperties() throws Exception{
			String propertyFile = System.getProperty("gov.nih.nci.cacore.nciNCItBrowserProperties");

			log.info("NCItBrowserProperties FileLocation= "+ propertyFile);

			if(propertyFile != null && propertyFile.length() > 0){
				FileInputStream fis = new FileInputStream(new File(propertyFile));
				properties.load(fis);
			}
			else System.out.println("propertyFile is null");

			for(Iterator i = properties.keySet().iterator(); i.hasNext();){
				String key = (String)i.next();
				String value  = properties.getProperty(key);

				System.out.println("key: " + key);
				System.out.println("value: " + value);

	            log.debug("KEY: "+ key +"\t - "+value);
			}
		}
	}
