package gov.nih.nci.evs.browser.utils;

import java.util.*;
import javax.servlet.http.*;

/**
 * <!-- LICENSE_TEXT_START -->
 * Copyright 2008,2009 NGIT. This software was developed in conjunction
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
 */

public class LexEVSUtils {
    public static class CScheme {
        private String _codingScheme;
        private String _version;
        private String _displayName;

        public CScheme(String codingScheme, String version) {
            _codingScheme = codingScheme;
            _version = version;
            _displayName = null;
        }

        public String getCodingScheme() {
            return _codingScheme;
        }

        public String getVersion() {
            return _version;
        }

        public String getDisplayName() {
			if (_displayName == null) {
            	_displayName = DataUtils.getMetadataValue(_codingScheme, "display_name");
			}
			return _displayName;
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
