/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.utils;

import java.io.*;
import java.util.*;

/**
 * 
 */

public class OntologyInfo {
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
        _label = label;
        _sortCategory = sortCategory;

        _tag = DataUtils.getVocabularyVersionTag(codingScheme, version);
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
