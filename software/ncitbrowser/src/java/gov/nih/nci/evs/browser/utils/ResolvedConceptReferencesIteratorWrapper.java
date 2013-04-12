/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.utils;

import org.LexGrid.LexBIG.Utility.Iterators.*;

/**
 * 
 */

/**
 * The Class ResolvedConceptReferencesIteratorWrapper. Decorates a
 * ResolvedConceptReferencesIterator to provide paging support for Associated
 * Concept-type searches. As the iterator advances, subconcepts are queried from
 * the decorated iterator on demand, rather than all at once. This elminates the
 * need to resolve large CodedNodeGraphs.
 */
public class ResolvedConceptReferencesIteratorWrapper {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4126716487618136771L;

    /** The lbs. */
    private ResolvedConceptReferencesIterator _iterator;

    /** The quick iterator. */
    private String _message = null;
    private String _codingSchemeName = null;
    private String _codingSchemeVersion = null;

    public ResolvedConceptReferencesIteratorWrapper(
        ResolvedConceptReferencesIterator iterator) {
        _iterator = iterator;
        _message = null;
    }

    public ResolvedConceptReferencesIteratorWrapper(
        ResolvedConceptReferencesIterator iterator, String message) {
        _iterator = iterator;
        _message = message;
    }

    public void setIterator(ResolvedConceptReferencesIterator iterator) {
        _iterator = iterator;
    }

    public ResolvedConceptReferencesIterator getIterator() {
        return _iterator;
    }

    public void setMessage(String message) {
        _message = message;
    }

    public String getMessage() {
        return _message;
    }

    public void setCodingSchemeName(String scheme) {
        _codingSchemeName = scheme;
    }

    public String getCodingSchemeName() {
        return _codingSchemeName;
    }

    public void setCodingSchemeVersion(String version) {
        _codingSchemeVersion = version;
    }

    public String getCodingSchemeVersion() {
        return _codingSchemeVersion;
    }
}
