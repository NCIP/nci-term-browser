/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.test;

import java.util.*;

import org.LexGrid.LexBIG.DataModel.Core.*;
import org.LexGrid.LexBIG.Utility.Iterators.*;
import org.apache.log4j.*;

import gov.nih.nci.evs.browser.test.utils.*;
import gov.nih.nci.evs.browser.utils.*;

public class SearchUtilsTest {
    private static Logger _logger = getLogger(); 

    private static Logger getLogger() {
        Logger.getLogger("gov.nih.nci.evs").setLevel(Level.DEBUG);
        Class<SearchUtilsTest> c = SearchUtilsTest.class;
        Logger.getLogger(c).setLevel(Level.DEBUG);
        return Logger.getLogger(c);
    }
    
    public void searchByName(String matchText) throws Exception {
        Vector<String> schemes = new Vector<String>();
        Vector<String> versions = new Vector<String>();
        schemes.add("NCI Thesaurus"); versions.add("10.11e");
        schemes.add("NCI Thesaurus"); versions.add("10.12c");
        schemes.add("NCI Thesaurus"); versions.add("11.01e");
        String source = "ALL";
        String matchAlgorithm = "exactMatch";
        boolean ranking = true;
        int maxToReturn = 100000;
        
        _logger.debug("------------------------------------------------------");
        _logger.debug("Method: SearchUtils().searchByName");
        _logger.debug("  * schemes: " + schemes);
        _logger.debug("  * versions: " + versions);
        _logger.debug("  * matchText: " + matchText);
        _logger.debug("  * source: " + source);
        _logger.debug("  * matchAlgorithm: " + matchAlgorithm);
        _logger.debug("  * ranking: " + ranking);
        _logger.debug("  * maxToReturn: " + maxToReturn);
        ResolvedConceptReferencesIteratorWrapper wrapper =
            new SearchUtils().searchByName(schemes, versions, matchText,
                source, matchAlgorithm, ranking, maxToReturn);
        ResolvedConceptReferencesIterator iterator = wrapper.getIterator();
        if (iterator == null)
            return;
        while (iterator.hasNext()) {
            ResolvedConceptReference rcr = iterator.next();
            String code = rcr.getCode();
            String desc = rcr.getEntityDescription().getContent();
            _logger.debug(code + ": " + desc);
        }
        _logger.debug("");
    }

    public static void main(String[] args) throws Exception {
        args = SetupEnv.getInstance().parse(args);
        new SearchUtilsTest().searchByName("Zinc Finger Protein GLI3");
        new SearchUtilsTest().searchByName("C17410");
    }
}
