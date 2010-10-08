package gov.nih.nci.evs.browser.test.utils;

import java.util.*;

import gov.nih.nci.evs.browser.properties.*;

public class PropertyTest {
    public static void main(String[] args) throws Exception {
        args = SetupEnv.getInstance().parse(args);
        String value =
            NCItBrowserProperties.getInstance().getStandardFtpReportUrl();
        System.out.println("getStandardFtpReportUrl: " + value);

        Vector<StandardFtpReportInfo> list =
            NCItBrowserProperties.getInstance().getStandardFtpReportInfoList();
        Iterator<StandardFtpReportInfo> iterator = list.iterator();
        System.out.println("getStandardFtpReportInfoList:");
        while (iterator.hasNext()) {
            StandardFtpReportInfo info = iterator.next();
            System.out.println("  * " + info);
        }
    }
}
