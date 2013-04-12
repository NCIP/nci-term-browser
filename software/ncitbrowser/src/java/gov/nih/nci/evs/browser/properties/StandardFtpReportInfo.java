/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.properties;

import gov.nih.nci.evs.browser.utils.*;

import java.util.*;

import org.apache.log4j.*;

/**
 * 
 */

/**
 * @author EVS Team
 * @version 1.0
 */

public class StandardFtpReportInfo {
    private static Logger _logger = Logger
        .getLogger(StandardFtpReportInfo.class);
    private String _name = "";
    private String _url = "";
    private Vector<Integer> _ncitColumns = new Vector<Integer>();

    public StandardFtpReportInfo(String name, String url,
        Vector<Integer> ncitColumns) {
        _name = name;
        _url = url;
        _ncitColumns = ncitColumns;
    }

    public String getName() {
        return _name;
    }

    public String getUrl() {
        return _url;
    }

    public Vector<Integer> getNcitColumns() {
        return _ncitColumns;
    }

    public String toString() {
        return "name=" + _name + ", url=" + _url + ", ncitColumns="
            + _ncitColumns.toString();
    }

    public static Vector<StandardFtpReportInfo> parse(String propertyName,
        int max) throws Exception {
        Vector<StandardFtpReportInfo> list =
            new Vector<StandardFtpReportInfo>();
        for (int i = 0; i < max; ++i) {
            String propertyValue =
                NCItBrowserProperties.getInstance().getProperty(
                    propertyName + "_" + i);
            StandardFtpReportInfo info = parse(propertyName, propertyValue);
            if (info == null)
                continue;
            list.add(info);
        }
        return list;
    }

    public static StandardFtpReportInfo parse(String propertyName,
        String propertyValue) {
        if (propertyValue == null)
            return null;
        String[] values = Utils.toStrings(propertyValue, ";", false);
        if (values.length == 1 && values[0].trim().startsWith("$"))
            return null;
        if (values.length < 3) {
            _logger.error("Error parsing property: " + propertyName);
            _logger
                .error("  * Missing some values.  Format: Name ; URL ; NCIt Columns");
            _logger.error("  * Current value: " + propertyValue);
            return null;
        }

        String name = values[0];
        String url = values[1];

        Vector<Integer> ncitColumns = new Vector<Integer>();
        String[] values2 = Utils.toStrings(values[2], " ", false);
        for (int i = 0; i < values2.length; ++i) {
            try {
                int col = Integer.parseInt(values2[i]);
                ncitColumns.add(col);
            } catch (Exception e) {
                _logger.error(e.getClass().getSimpleName() + ": "
                    + e.getMessage());
                _logger
                    .error("  * Could not parse NCIt columns from property value: "
                        + propertyValue);
            }
        }

        StandardFtpReportInfo report =
            new StandardFtpReportInfo(name, url, ncitColumns);
        return report;
    }

    public static StandardFtpReportInfo getByName(
        Vector<StandardFtpReportInfo> list, String name) {
        Iterator<StandardFtpReportInfo> iterator = list.iterator();
        while (iterator.hasNext()) {
            StandardFtpReportInfo info = iterator.next();
            if (info.getName().equals(name))
                return info;
        }
        return null;
    }
}
