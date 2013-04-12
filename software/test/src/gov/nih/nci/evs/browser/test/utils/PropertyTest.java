/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.test.utils;

import java.util.*;

import gov.nih.nci.evs.browser.properties.*;

public class PropertyTest {
    public static void main(String[] args) throws Exception {
        args = SetupEnv.getInstance().parse(args);
        NCItBrowserProperties.getInstance();
        String value =
            NCItBrowserProperties.getStandardFtpReportUrl();
        System.out.println("getStandardFtpReportUrl: " + value);

        Vector<StandardFtpReportInfo> list =
            NCItBrowserProperties.getStandardFtpReportInfoList();
        Iterator<StandardFtpReportInfo> iterator = list.iterator();
        System.out.println("getStandardFtpReportInfoList:");
        while (iterator.hasNext()) {
            StandardFtpReportInfo info = iterator.next();
            System.out.println("  * " + info);
        }
    }
}
