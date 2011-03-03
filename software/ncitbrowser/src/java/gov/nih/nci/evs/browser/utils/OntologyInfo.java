package gov.nih.nci.evs.browser.utils;

import java.util.*;

public class OntologyInfo {
    private String _codingScheme = "";
    private String _displayName = "";
    private String _version = "";
    private String _displayNameVersion = "";
    private int _sortCategory = 0;

    public OntologyInfo(String codingScheme, String displayName, String version,
        String sortCategory) {
        try {
            if (sortCategory == null || sortCategory.trim().length() <= 0)
                throw new Exception("Sort Category not defined.");
            int category = Integer.parseInt(sortCategory);
            init(codingScheme, displayName, version, category);
        } catch (Exception e) {
            init(codingScheme, displayName, version, 0);
        }
    }

    public OntologyInfo(String codingScheme, String displayName, String version,
        int sortCategory) {
        init(codingScheme, displayName, version, sortCategory);
    }

    private void init(String codingScheme, String displayName, String version,
        int sortCategory) {
        _codingScheme = codingScheme;
        _displayName = displayName;
        _version = version;
        _displayNameVersion = displayName + "$" + version;
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
