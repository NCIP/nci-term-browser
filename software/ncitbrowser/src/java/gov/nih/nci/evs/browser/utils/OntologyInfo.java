/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.utils;

import java.util.*;

public class OntologyInfo {
    private String _displayName = "";
    private int _sortCategory = 0;
    
    public OntologyInfo(String displayName, String sortCategory) {
        if (sortCategory == null || sortCategory.trim().length() <= 0) {
            System.out.println("Warning: vocabulary_sort_category metadata value not set:");
            System.out.println("Warning:   * Defaulting " + displayName + " to sort category 0.");
            init(displayName, 0);
            return;
        }

        try {
            int category = Integer.parseInt(sortCategory);
            System.out.println("FYI: * " + displayName + " set to sort category " + category);
            init(displayName, category);
        } catch (Exception e) {
            System.out.println("Error: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            System.out.println("Error:   * Defaulting " + displayName + " to sort category 0.");
            init(displayName, 0);
        }
    }

    public OntologyInfo(String displayName, int sortCategory) {
        init(displayName, sortCategory);
    }
    
    private void init(String displayName, int sortCategory) {
        _displayName = displayName;
        _sortCategory = sortCategory;
    }

    public String getDisplayName() {
        return _displayName;
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

            String displayName1 = value1.getDisplayName();
            String displayName2 = value2.getDisplayName();
            return displayName1.compareToIgnoreCase(displayName2);
        }
    }
}
