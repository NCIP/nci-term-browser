package gov.nih.nci.evs.browser.utils;

import java.util.*;

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

public class OntologyInfo {
    private String _codingScheme = "";
    private String _displayName = "";
    private String _version = "";
    private String _displayNameVersion = "";
    private String _label = "";
    private int _sortCategory = 0;

    public OntologyInfo(String codingScheme, String displayName, String version,
        String label, String sortCategory) {
        try {
            if (sortCategory == null || sortCategory.trim().length() <= 0)
                throw new Exception("Sort Category not defined.");
            int category = Integer.parseInt(sortCategory);
            init(codingScheme, displayName, version, label, category);
        } catch (Exception e) {
            init(codingScheme, displayName, version, label, 0);
        }
    }

    public OntologyInfo(String codingScheme, String displayName, String version,
        String label, int sortCategory) {
        init(codingScheme, displayName, version, label, sortCategory);
    }

    private void init(String codingScheme, String displayName, String version,
        String label, int sortCategory) {
        _codingScheme = codingScheme;
        _displayName = displayName;
        _version = version;
        _displayNameVersion = displayName + "$" + version;
        _label = label;
        _sortCategory = sortCategory;
    }

    public String getCodingScheme() {
        return _codingScheme;
    }

    public String getDisplayName() {
        return _displayName;
    }

    public String getVersion() {
        return _version;
    }

    public String getDisplayNameVersion() {
        return _displayNameVersion;
    }

    public String getLabel() {
        return _label;
    }

    public int getSortCategory() {
        return _sortCategory;
    }

    public static class ComparatorImpl implements Comparator<OntologyInfo> {
        public int compare(OntologyInfo value1, OntologyInfo value2) {
            int sortCategory1 = value1.getSortCategory();
            int sortCategory2 = value2.getSortCategory();
            if (sortCategory1 != sortCategory2)
                return sortCategory2 - sortCategory1;

            String displayName1 = value1.getDisplayNameVersion();
            String displayName2 = value2.getDisplayNameVersion();
            return displayName1.compareToIgnoreCase(displayName2);
        }
    }
}
