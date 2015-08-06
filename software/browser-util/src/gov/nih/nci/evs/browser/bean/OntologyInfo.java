package gov.nih.nci.evs.browser.bean;

import java.io.*;
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

public class OntologyInfo implements java.io.Serializable {
    private String _codingScheme = "";
    private String _displayName = "";
    private String _version = "";
    private String _label = "";
    private int _sortCategory = 0;

    private String _tag = "";
    private boolean _hasMultipleVersions = false;
    private boolean _expanded = false;
    private boolean _selected = false;
    private boolean _visible = false;

    public OntologyInfo(String codingScheme, String displayName, String version, String tag, String label, String sortCategory) {
        _codingScheme = codingScheme;
        _displayName = displayName;
        _version = version;
        _tag = tag;
        _label = label;
        if (sortCategory != null && sortCategory.trim().length() > 0) {
        	_sortCategory = Integer.parseInt(sortCategory);
		}
		_hasMultipleVersions = false;
		_expanded = false;
		_selected = false;
		if (_tag != null && _tag.compareTo("PRODUCTION") == 0) {
			_visible = true;
		}
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

    public String getLabel() {
        return _label;
    }

    public String getTag() {
        return _tag;
    }

    public boolean isProduction() {
        if (_tag != null && _tag.compareTo("PRODUCTION") == 0) return true;
        if (_version == null) return true;
        return false;
    }

    public boolean getHasMultipleVersions() {
        return _hasMultipleVersions;
    }

    public void setHasMultipleVersions(boolean hasMultipleVersions) {
        this._hasMultipleVersions = hasMultipleVersions;
    }

    public boolean getExpanded() {
        return _expanded;
    }

    public void setExpanded(boolean expanded) {
        this._expanded = expanded;
    }

    public boolean getVisible() {
        return _visible;
    }

    public void setVisible(boolean visible) {
        this._visible = visible;
    }

    public boolean getSelected() {
        return _selected;
    }

    public void setSelected(boolean selected) {
        this._selected = selected;
    }

    public int getSortCategory() {
        return _sortCategory;
    }

    public static class ComparatorImpl implements Serializable, Comparator<OntologyInfo> {
        public int compare(OntologyInfo info1, OntologyInfo info2) {
            int sortCategory1 = info1.getSortCategory();
            int sortCategory2 = info2.getSortCategory();
            if (sortCategory1 != sortCategory2)
                return sortCategory2 - sortCategory1;

            String value1 = info1.getDisplayName() + info1.getVersion();
            String value2 = info2.getDisplayName() + info2.getVersion();
            return value1.compareToIgnoreCase(value2);
        }
    }
}
