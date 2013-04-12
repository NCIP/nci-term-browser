/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.utils;

import gov.nih.nci.evs.browser.properties.*;

/**
 * 
 */

public class SortOption {
    public enum Type {
        FALSE, TRUE, ALL
    };

    private Type _type = Type.TRUE;
    private boolean _sort_by_pt_only = true;
    private boolean _apply_sort_score = true;

    public SortOption() {
    }

    public SortOption(boolean isRanking) {
        if (isRanking)
            setType(Type.ALL);
        else
            setType(Type.FALSE);
    }

    public SortOption(SortOption.Type type) {
        setType(type);
    }

    public boolean isSortByPtOnly() {
        return _sort_by_pt_only;
    }

    public boolean isApplySortScore() {
        return _apply_sort_score;
    }

    public Type getType() {
        return _type;
    }

    public void setType(SortOption.Type type) {
        _type = type;
        switch (type) {
        case FALSE:
            _sort_by_pt_only = true;
            _apply_sort_score = false;
            break;
        case ALL:
            _sort_by_pt_only = false;
            _apply_sort_score = true;
            break;
        default: // TRUE
            _sort_by_pt_only = true;
            _apply_sort_score = true;
            break;
        }
    }

    public void setType(String value) {
        if (value == null)
            return; // Use default values
        setType(Type.valueOf(value.toUpperCase()));
    }

    public void setTypeByPropertyFile() {
        try {
            String value =
                NCItBrowserProperties
                    .getProperty(NCItBrowserProperties.SORT_BY_SCORE);
            setType(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        return _type.name().toLowerCase() + " (" + "sort_by_pt_only: "
            + _sort_by_pt_only + ", apply_sort_score: " + _apply_sort_score + ")";
    }
}
