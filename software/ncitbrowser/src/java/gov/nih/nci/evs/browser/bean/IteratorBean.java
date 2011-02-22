package gov.nih.nci.evs.browser.bean;

import java.io.*;
import java.util.*;

import org.LexGrid.LexBIG.Utility.Iterators.*;
import org.LexGrid.LexBIG.DataModel.Core.*;
import org.apache.log4j.*;

import gov.nih.nci.evs.browser.common.*;
import gov.nih.nci.evs.browser.properties.*;

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
 *
 *          Modification history Initial implementation kim.ong@ngc.com
 *
 */

public class IteratorBean extends Object {
    private static Logger _logger = Logger.getLogger(IteratorBean.class);
    private static int DEFAULT_MAX_RETURN = 100;
    private ResolvedConceptReferencesIterator _iterator = null;
    private int _size = 0;
    private List _list = null;

    private int _pageNumber;
    private int _pageSize;
    private int _startIndex;
    private int _endIndex;
    private int _numberOfPages;

    private int _lastResolved;
    private int _maxReturn = 100;
    private String _message = null;

    private String _matchText = null;

    private String _key = null;
    private boolean _timeout = false;

    public IteratorBean(ResolvedConceptReferencesIterator iterator) {
        _iterator = iterator;
        _maxReturn = DEFAULT_MAX_RETURN;
        initialize();
    }

    public IteratorBean(ResolvedConceptReferencesIterator iterator,
        int maxReturn) {
        _iterator = iterator;
        _maxReturn = maxReturn;
        initialize();
    }

    public int getNumberOfPages() {
        return _numberOfPages;
    }

    public void setIterator(ResolvedConceptReferencesIterator iterator) {
        _iterator = iterator;
        _maxReturn = DEFAULT_MAX_RETURN;
        initialize();
    }

    public ResolvedConceptReferencesIterator getIterator() {
        return _iterator;
    }

    public boolean getTimeout() {
        return _timeout;
    }

    public void initialize() {
        try {
            if (_iterator == null) {
                _size = 0;
            } else {
                _size = _iterator.numberRemaining();
            }
            _pageNumber = 1;
            /*
            _list = new ArrayList(_size);
            for (int i = 0; i < _size; i++) {
                _list.add(null);
            }
            */
            _list = new ArrayList();

            _pageSize = Constants.DEFAULT_PAGE_SIZE;
            _numberOfPages = _size / _pageSize;
            if (_pageSize * _numberOfPages < _size) {
                _numberOfPages = _numberOfPages + 1;
            }
            _lastResolved = -1;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int getMumberOfPages() {
        return _numberOfPages;
    }

    public int getSize() {
        return _size;
    }

    public void setPageSize(int pageSize) {
        _pageSize = pageSize;
    }

    public int getPageSize() {
        return _pageSize;
    }

    public int getLastResolved() {
        return _lastResolved;
    }

    public int getStartIndex(int pageNumber) {
        _startIndex = (pageNumber - 1) * _pageSize;
        if (_startIndex < 0)
            _startIndex = 0;
        return _startIndex;
    }

    public int getEndIndex(int pageNumber) {
        _endIndex = pageNumber * _pageSize - 1;
        if (_endIndex > (_size - 1))
            _endIndex = _size - 1;
        return _endIndex;
    }

    public List getData(int pageNumber) {
        int idx1 = getStartIndex(pageNumber);
        int idx2 = getEndIndex(pageNumber);
        return getData(idx1, idx2);
    }

    public List getData(int idx1, int idx2) {
        _logger.debug("Retrieving data (from: " + idx1 + " to: " + idx2 + ")");
        long ms = System.currentTimeMillis();
        long dt = 0;
        long total_delay = 0;
        _timeout = false;
        try {
            while (_iterator != null && _iterator.hasNext()
                && _lastResolved < idx2) {
                ResolvedConceptReference[] refs =
                    _iterator.next(_maxReturn).getResolvedConceptReference();
                for (ResolvedConceptReference ref : refs) {
                    // displayRef(ref);
                    _lastResolved++;
                   // _list.set(_lastResolved, ref);

                   _list.add(ref);

                }
                dt = System.currentTimeMillis() - ms;
                ms = System.currentTimeMillis();
                total_delay = total_delay + dt;
                if (total_delay > NCItBrowserProperties.getPaginationTimeOut() * 60 * 1000) {
                    _timeout = true;
                    _logger.debug("Time out at: " + _lastResolved);
                    break;
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        /*
         * for (int i=idx1; i<=idx2; i++) { ResolvedConceptReference rcr =
         * (ResolvedConceptReference) list.get(i); rcr_list.add(rcr); if (i
         * > lastResolved) break; }
         */

        Vector temp_vec = new Vector();
        for (int i = idx1; i <= idx2; i++) {
            ResolvedConceptReference rcr =
                (ResolvedConceptReference) _list.get(i);
            temp_vec.add(rcr);
            if (i > _lastResolved)
                break;
        }
        List rcr_list = new ArrayList();
        /*
        for (int i = 0; i < temp_vec.size(); i++) {
            rcr_list.add(null);
        }

        for (int i = 0; i < temp_vec.size(); i++) {
            ResolvedConceptReference rcr =
                (ResolvedConceptReference) temp_vec.elementAt(i);
            rcr_list.set(i, rcr);
        }
        */
        for (int i = 0; i < temp_vec.size(); i++) {
            ResolvedConceptReference rcr =
                (ResolvedConceptReference) temp_vec.elementAt(i);
            rcr_list.add(rcr);
        }

        _logger.debug("getData Run time (ms): "
            + (System.currentTimeMillis() - ms));
        return rcr_list;
    }

    protected void displayRef(ResolvedConceptReference ref) {
        _logger.debug(ref.getConceptCode() + ":"
            + ref.getEntityDescription().getContent());
    }

    protected void displayRef(int k, ResolvedConceptReference ref) {
        _logger.debug("(" + k + ") " + ref.getCodingSchemeName() + " "
            + ref.getConceptCode() + ":"
            + ref.getEntityDescription().getContent());
    }

    protected void displayRef(OutputStreamWriter osWriter, int k,
        ResolvedConceptReference ref) {
        try {
            osWriter.write("(" + k + ") " + ref.getConceptCode() + ":"
                + ref.getEntityDescription().getContent() + "\n");
        } catch (Exception ex) {

        }
    }

    public void dumpData(List list) {
        if (list == null) {
            _logger.warn("WARNING: dumpData list = null???");
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            ResolvedConceptReference rcr =
                (ResolvedConceptReference) list.get(i);
            int j = i + 1;
            displayRef(j, rcr);
        }
    }

    public void dumpData(OutputStreamWriter osWriter, List list) {
        if (list == null) {
            _logger.warn("WARNING: dumpData list = null???");
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            ResolvedConceptReference rcr =
                (ResolvedConceptReference) list.get(i);
            int j = i + 1;
            displayRef(osWriter, j, rcr);
        }
    }

    public void setKey(String key) {
        _key = key;
    }

    public String getKey() {
        return _key;
    }

    public void setMessage(String message) {
        _message = message;
    }

    public String getMessage() {
        return _message;
    }

    public void setMatchText(String matchText) {
        _matchText = matchText;
    }

    public String getMatchText() {
        return _matchText;
    }
}