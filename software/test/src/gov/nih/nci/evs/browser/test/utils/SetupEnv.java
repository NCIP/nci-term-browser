/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.test.utils;

import gov.nih.nci.evs.browser.properties.*;

import java.util.*;

public class SetupEnv {
    private static final String PROPERTY_FILE = 
        "gov.nih.nci.evs.browser.NCItBrowserProperties";
    private static String DEFAULT_PROPERTY_FILE =
        "c:/Users/yeed/apps/evs/ncit/conf/NCItBrowserProperties.xml";
    private static SetupEnv _instance = null;
    
    public static SetupEnv getInstance() {
        if (_instance == null)
            _instance = new SetupEnv();
        return _instance;
    }
    
    private SetupEnv() {
        setup(DEFAULT_PROPERTY_FILE);
    }
    
    public void setup(String propertyFile) {
        System.setProperty(PROPERTY_FILE, propertyFile);
        try {
            NCItBrowserProperties.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String[] parse(String[] args) {
        String prevArg = "";
        ArrayList<String> newArgs = new ArrayList<String>();
        for (int i = 0; i < args.length; ++i) {
            String arg = args[i];
            if (arg.equals("-propertyFile")) {
                prevArg = arg;
            } else if (prevArg.equals("-propertyFile")) {
                setup(arg);
                prevArg = "";
            } else {
                newArgs.add(arg);
            }
        }
        return newArgs.toArray(new String[newArgs.size()]);
    }
}
