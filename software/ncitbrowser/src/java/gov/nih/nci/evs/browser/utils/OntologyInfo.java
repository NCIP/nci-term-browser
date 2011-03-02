package gov.nih.nci.evs.browser.utils;

import java.util.*;

public class OntologyInfo {
    private String _displayNameVersion = "";
    private String _codingScheme = "";
    private int _sortCategory = 0;

    public OntologyInfo(String displayNameVersion, String codingScheme, String sortCategory) {
        try {
            if (sortCategory == null || sortCategory.trim().length() <= 0)
                throw new Exception("Sort Category not defined.");
            int category = Integer.parseInt(sortCategory);
            init(displayNameVersion, codingScheme, category);
        } catch (Exception e) {
            init(displayNameVersion, codingScheme, 0);
        }
    }

    public OntologyInfo(String displayNameVersion, String codingScheme, int sortCategory) {
        init(displayNameVersion, codingScheme, sortCategory);
    }

    private void init(String displayNameVersion, String codingScheme, int sortCategory) {
        _displayNameVersion = displayNameVersion;
        _codingScheme = codingScheme;
        _sortCategory = sortCategory;
    }

    public String getDisplayNameVersion() {
        return _displayNameVersion;
    }

    public String getCodingScheme() {
        return _codingScheme;
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
