package gov.nih.nci.evs.testUtil.ui;


import gov.nih.nci.evs.browser.bean.*;
import gov.nih.nci.evs.browser.utils.*;
import gov.nih.nci.evs.testUtil.*;
import java.io.*;
import java.net.*;
import java.util.*;


/**
 * <!-- LICENSE_TEXT_START -->
 * Copyright 2008-2015 NGIT. This software was developed in conjunction
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
 * @author EVS Team
 * @version 1.0
 *
 * Modification history:
 *     Initial implementation kim.ong@ngc.com
 *
 */


public class UITestGeneratorRunner {

	private static String PACKAGE_NAME = "package_name";
	private static String CLASS_NAME = "class_name";
	private static String REMOTE_WEB_DRIVER_URL = "remoteWebDriverURL";
	private static String BASE_URL = "baseUrl";
	private static String SERVICE_URL = "serviceUrl";
	private static String DELAY = "delay";

	private Properties properties = null;
    private String propertyFile = "resources/Test.properties";
    private CodeGeneratorConfiguration config = null;
    private UITestGenerator generator = null;

    public UITestGeneratorRunner() {
		initialize();
	}

    public UITestGeneratorRunner(String propertyFile) {
		this.propertyFile = propertyFile;
		initialize();
	}

	public void initialize() {
		properties = loadProperties();
	    String packageName = properties.getProperty(PACKAGE_NAME);
	    String className = properties.getProperty(CLASS_NAME);
	    String remoteWebDriverURL = properties.getProperty(REMOTE_WEB_DRIVER_URL);
	    String baseUrl = properties.getProperty(BASE_URL);
	    String serviceUrl = properties.getProperty(SERVICE_URL);
	    String delay_str = properties.getProperty(DELAY);
	    int delay = 1;
	    if (delay_str != null) {
	    	delay = Integer.parseInt(delay_str);
		}

	    config = new CodeGeneratorConfiguration();
	    config.setPackageName(packageName);
	    config.setClassName(className);
	    config.setRemoteWebDriverURL(remoteWebDriverURL);
	    config.setBaseUrl(baseUrl);
	    config.setServiceUrl(serviceUrl);
	    config.setDelay(delay);

	    System.out.println("packageName: " + packageName);
	    System.out.println("className: " + className);
	    System.out.println("remoteWebDriverURL: " + remoteWebDriverURL);
	    System.out.println("baseUrl: " + baseUrl);
	    System.out.println("serviceUrl: " + serviceUrl);
	    System.out.println("delay: " + delay);
	    generator = new UITestGenerator(config);
	}

	public void run() {
	    generator.run();
	}

	private Properties loadProperties() {
		try{
			properties = new Properties();
			if(propertyFile != null && propertyFile.length() > 0){
				FileInputStream fis = new FileInputStream(new File(propertyFile));
				properties.load(fis);
			}
			for(Iterator i = properties.keySet().iterator(); i.hasNext();){
				String key = (String)i.next();
				String value  = properties.getProperty(key);
			}
			return properties;
		} catch (Exception e){
			System.out.println("Error reading properties file");
			e.printStackTrace();
			return null;
		}
	}

    public static void main(String[] args) {
		if (args.length>0) {
			String propertyFile = args[0];
			UITestGeneratorRunner runner = new UITestGeneratorRunner(propertyFile);
			runner.run();
		} else {
			UITestGeneratorRunner runner = new UITestGeneratorRunner();
			runner.run();
		}
    }
}




