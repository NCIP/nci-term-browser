/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.utils;

import java.util.*;
import javax.servlet.http.*;

/**
 * 
 */

/**
 * @author EVS Team
 * @version 1.0
 */

public class LexEVSUtils {
    public static class CScheme {
        private String _codingScheme;
        private String _version;

        public CScheme(String codingScheme, String version) {
            _codingScheme = codingScheme;
            _version = version;
        }

        public String getCodingScheme() {
            return _codingScheme;
        }

        public String getVersion() {
            return _version;
        }

        public String getDisplayName() {
            return DataUtils.getMetadataValue(_codingScheme, "display_name");
        }

        public String toString() {
            // Note: This method affects toCScheme method.
            String text = _codingScheme;
            text += " (" + _version + ")";
            return text;
        }

        public static CScheme toCScheme(String text) {
            int i = text.lastIndexOf(" (");
            String codingScheme = text.substring(0, i);
            String version = text.substring(i);
            version = version.replace("(", "");
            version = version.replace(")", "");
            version = version.trim();
            return new CScheme(codingScheme, version);
        }
    }

    public static class CSchemes extends Vector<CScheme> {
        private static final long serialVersionUID = 1L;
        private static final String DELIMITER = "|";

        public void add(String codingScheme, String version) {
            add(new CScheme(codingScheme, version));
        }

        public Vector<String> getCodingSchemes() {
            Vector<String> list = new Vector<String>();
            Iterator<CScheme> iterator = iterator();
            while (iterator.hasNext()) {
                CScheme item = iterator.next();
                list.add(item.getCodingScheme());
            }
            return list;
        }

        public Vector<String> getVersions() {
            Vector<String> list = new Vector<String>();
            Iterator<CScheme> iterator = iterator();
            while (iterator.hasNext()) {
                CScheme item = iterator.next();
                list.add(item.getVersion());
            }
            return list;
        }

        public String toString(String delimiter) {
            StringBuffer buffer = new StringBuffer();
            Iterator<CScheme> iterator = iterator();
            while (iterator.hasNext()) {
                CScheme item = iterator.next();
                if (buffer.length() > 0)
                    buffer.append(delimiter);
                buffer.append(item.toString());
            }
            return buffer.toString();
        }

        public String toString() {
            return toString(", ");
        }

        public String getDelimitedValues() {
            return toString(DELIMITER);
        }

        public static CSchemes toSchemes(String delimitedValues) {
            CSchemes cSchemes = new CSchemes();
            StringTokenizer tokenizer =
                new StringTokenizer(delimitedValues, DELIMITER);
            while (tokenizer.hasMoreElements()) {
                String token = tokenizer.nextToken();
                CScheme cScheme = CScheme.toCScheme(token);
                cSchemes.add(cScheme);
            }
            return cSchemes;
        }
    }

    public static CSchemes getCSchemes(HttpServletRequest request,
        List<String> ontologiesToSearchOn) {
        CSchemes cSchemes = new CSchemes();
        for (int i = 0; i < ontologiesToSearchOn.size(); i++) {
            String key = (String) ontologiesToSearchOn.get(i);
            String scheme = DataUtils.key2CodingSchemeName(key);
            String version = DataUtils.key2CodingSchemeVersion(key);
            cSchemes.add(scheme, version);
        }
        return cSchemes;
    }
}
