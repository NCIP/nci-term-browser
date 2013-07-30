

/*
 * Copyright: (c) 2004-2010 Mayo Foundation for Medical Education and
 * Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
 * triple-shield Mayo logo are trademarks and service marks of MFMER.
 *
 * Except as contained in the copyright notice above, or as used to identify
 * MFMER as the author of this software, the trade names, trademarks, service
 * marks, or product names of the copyright holder shall not be used in
 * advertising, promotion or otherwise in connection with this software without
 * prior written authorization of the copyright holder.
 *
 * Licensed under the Eclipse Public License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.eclipse.org/legal/epl-v10.html
 *
 */
package gov.nih.nci.evs.browser.utils;

import java.util.*;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;


import org.apache.log4j.*;

import org.LexGrid.LexBIG.DataModel.Core.AbsoluteCodingSchemeVersionReference;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Extensions.Generic.CodingSchemeReference;
import org.LexGrid.LexBIG.Extensions.Generic.SearchExtension;
//import org.LexGrid.LexBIG.Impl.function.LexBIGServiceTestCase;

//import org.LexGrid.LexBIG.Impl.testUtility.ServiceHolder;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;

import org.LexGrid.LexBIG.DataModel.Collections.*;
import org.LexGrid.LexBIG.DataModel.Core.*;
import org.LexGrid.LexBIG.LexBIGService.*;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.*;
import org.LexGrid.LexBIG.Utility.*;
import org.LexGrid.concepts.*;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.*;
import org.LexGrid.LexBIG.Utility.Iterators.*;
import org.LexGrid.codingSchemes.*;
import org.apache.log4j.*;

import org.LexGrid.LexBIG.DataModel.Core.types.*;
import org.LexGrid.naming.*;
import org.LexGrid.LexBIG.Extensions.Generic.*;

import org.apache.commons.codec.language.*;
import org.LexGrid.LexBIG.Extensions.Generic.SearchExtension;


public class SimpleSearchUtils {
	private static Logger _logger = Logger.getLogger(SimpleSearchUtils.class);

    final static String testID = "SearchExtensionImplTest";

    public static final int BY_CODE = 1;
    public static final int BY_NAME = 2;

    public static final String EXACT_MATCH = "exactMatch";
    public static final String STARTS_WITH = "startsWith";
    public static final String CONTAINS = "contains";
    public static final String LUCENE = "lucene";

    public static final String NAMES = "names";
    public static final String CODES = "codes";
    public static final String PROPERTIES = "properties";
    public static final String RELATIONSHIPS = "relationships";

    private static String charactersToRemove = ",./\\`\"+*=@#$%^&?!";
    private static String charactersTreatedAsWhitespace = "-;(){}[]<>|";

    public SimpleSearchUtils() {

	}

    public ResolvedConceptReferencesIteratorWrapper search(
        Vector<String> schemes, Vector<String> versions, String matchText, String algorithm, String target) throws LBException {
        if (algorithm == null|| target == null) return null;

        if (algorithm.compareToIgnoreCase(EXACT_MATCH) == 0 && target.compareToIgnoreCase(CODES) == 0) {
			return search(schemes, versions, matchText, BY_CODE, "exactMatch");
        } else if (algorithm.compareToIgnoreCase(LUCENE) == 0 && target.compareToIgnoreCase(CODES) == 0) {
			return search(schemes, versions, matchText, BY_CODE, "exactMatch");
        } else if (algorithm.compareToIgnoreCase(LUCENE) == 0 && target.compareToIgnoreCase(NAMES) == 0) {
			return search(schemes, versions, matchText, BY_NAME, "lucene");
        } else if (algorithm.compareToIgnoreCase(CONTAINS) == 0 && target.compareToIgnoreCase(NAMES) == 0) {
			return search(schemes, versions, matchText, BY_NAME, "contains");
		}
		return null;
	}

    public String preprocessingString(String input) {
		if (input == null) return null;
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<input.length(); i++) {
			//char c = input.charAt(i);
			String ch = input.substring(i, i+1);
			if (charactersTreatedAsWhitespace.indexOf(ch) > 0) {
				buf.append(" ");
			} else if (charactersToRemove.indexOf(ch) == -1) {
				buf.append(ch);
			}
		}
		return buf.toString();

	}

	public static boolean isSimpleSearchSupported(String algorithm, String target) {
		//if (!isSearchExtensionAvaliable) return false;

		if (algorithm == null|| target == null) return false;

        if (algorithm.compareToIgnoreCase(EXACT_MATCH) == 0 && target.compareToIgnoreCase(CODES) == 0) {
			return true;
        } else if (algorithm.compareToIgnoreCase(EXACT_MATCH) == 0 && target.compareToIgnoreCase(NAMES) == 0) {
			return true;
        } else if (algorithm.compareToIgnoreCase(LUCENE) == 0 && target.compareToIgnoreCase(CODES) == 0) {
			return true;
        } else if (algorithm.compareToIgnoreCase(LUCENE) == 0 && target.compareToIgnoreCase(NAMES) == 0) {
			return true;
        } else if (algorithm.compareToIgnoreCase(CONTAINS) == 0 && target.compareToIgnoreCase(NAMES) == 0) {
			return true;
		}
		return false;
	}


    public static boolean isSearchExtensionAvaliable() {
		if (getSearchExtension() == null) return false;
		return true;
	}


    public static SearchExtension getSearchExtension() {
		SearchExtension searchExtension = null;

		try {
			LexBIGService lbSvc = new RemoteServerUtil().createLexBIGService();
			if (lbSvc == null) {
				_logger.warn("createLexBIGService returns NULL???");
				return null;
			}
			searchExtension = (SearchExtension) lbSvc.getGenericExtension("SearchExtension");
			return searchExtension;
		} catch (Exception e){
			_logger.warn("SearchExtension is not available.");
			return null;
		}
	}



    public static boolean searchAllSources(String source) {
		if (source != null && source.compareTo("ALL") != 0) return false;
		return true;
	}


    public ResolvedConceptReferencesIteratorWrapper search(
        String scheme, String version, String matchText, int searchOption, String algorithm) throws LBException {
		if (scheme == null) return null;
		Vector<String> schemes = new Vector();
		Vector<String> versions = new Vector();
		schemes.add(scheme);
		versions.add(version);
		return search(schemes, versions, matchText, searchOption, algorithm);
    }

    public ResolvedConceptReferencesIteratorWrapper search(
        Vector<String> schemes, Vector<String> versions, String matchText, int searchOption, String algorithm) throws LBException {
	    if (schemes == null|| versions == null) return null;
	    if (schemes.size() != versions.size()) return null;
	    if (schemes.size() == 0) return null;
	    if (matchText == null) return null;
	    if (searchOption != BY_CODE && searchOption != BY_NAME) return null;
	    if (searchOption != BY_CODE && algorithm == null) return null;

		LexBIGService lbSvc = new RemoteServerUtil().createLexBIGService();

		if (lbSvc == null) {
			return null;
		}

		SearchExtension searchExtension = null;
		try {
			searchExtension = (SearchExtension) lbSvc.getGenericExtension("SearchExtension");
		} catch (Exception e){
			_logger.warn("SearchExtension is not available.");
			return null;
		}

        Set<CodingSchemeReference> includes = new HashSet();

        for (int i=0; i<schemes.size(); i++) {
			String scheme = (String) schemes.elementAt(i);
			String version = (String) versions.elementAt(i);
			CodingSchemeReference ref = new CodingSchemeReference();
			ref.setCodingScheme(scheme);

			if (version != null) {
				CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
				versionOrTag.setVersion(version);
		    }
			includes.add(ref);
		}
		/*
		if (searchOption == BY_CODE) {
			matchText = "code:" + matchText;
		}
		*/

		ResolvedConceptReferencesIterator iterator = null;
		try {
			iterator = searchExtension.search(matchText, includes, converToMatchAlgorithm(searchOption, algorithm));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (iterator != null) {
			return new ResolvedConceptReferencesIteratorWrapper(iterator);
		}
		return null;
	}


    protected static void displayRef(ResolvedConceptReference ref) {
        System.out.println(ref.getConceptCode() + ":" + ref.getEntityDescription().getContent());
    }

    public static void dumpIterator(ResolvedConceptReferencesIterator itr) {
        try {
            while (itr.hasNext()) {
                try {
                    ResolvedConceptReference[] refs =
                        itr.next(100).getResolvedConceptReference();
                    for (ResolvedConceptReference ref : refs) {
                        displayRef(ref);
                    }
                } catch (Exception ex) {
                    break;
                }
            }
        } catch (Exception ex) {
			ex.printStackTrace();
        }
    }


	public static SearchExtension.MatchAlgorithm converToMatchAlgorithm(int searchOption, String algorithm) {
		if (algorithm == null) return null;
	    if (searchOption != BY_CODE && searchOption != BY_NAME) return null;
	    if (searchOption == BY_NAME) {
			if (algorithm.compareTo("exactMatch") == 0) {
				return SearchExtension.MatchAlgorithm.PRESENTATION_EXACT;
			} else if (algorithm.compareTo("contains") == 0) {
				return SearchExtension.MatchAlgorithm.PRESENTATION_CONTAINS;
			} else if (algorithm.compareTo("lucene") == 0) {
				return SearchExtension.MatchAlgorithm.LUCENE;
			}
		} else if (algorithm.compareTo("exactMatch") == 0 && searchOption == BY_CODE) {
			return SearchExtension.MatchAlgorithm.CODE_EXACT;
		}
		return null;
	}

	public static void main(String [ ] args) {
		boolean searchExtensionAvaliable = isSearchExtensionAvaliable();
		if (!searchExtensionAvaliable) {
			System.out.println("SearchExtension is not available.");
			System.exit(1);
		}

		SimpleSearchUtils test = new SimpleSearchUtils();
        try {
			Vector<String> schemes = new Vector();
			Vector<String> versions = new Vector();
			schemes.add("NCI_Thesaurus");
			//versions.add("12.05d");
			versions.add("13.03d");

			schemes.add("NCI Metathesaurus");
			versions.add("201105");

			String matchText = "cell aging";

			ResolvedConceptReferencesIteratorWrapper wrapper = test.search(schemes, versions, matchText, SimpleSearchUtils.BY_NAME, "contains");
			if (wrapper != null) {
				ResolvedConceptReferencesIterator iterator = wrapper.getIterator();
				if (iterator != null) {
					try {
						int numRemaining = iterator.numberRemaining();
						System.out.println("Number of matches: " + numRemaining);
						dumpIterator(iterator);

					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			} else {
				System.out.println("wrapper is NULL??? " + matchText);
			}


		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}

/*

	public static MatchAlgorithm converToMatchAlgorithm(String algorithm) {
		if (algorithm == null) return null;
		if (algorithm.compareTo("exactMatch") == 0) {
			return MatchAlgorithm.PRESENTATION_EXACT;
		} else if (algorithm.compareTo("contains") == 0) {
			return MatchAlgorithm.PRESENTATION_CONTAINS;
		} else if (algorithm.compareTo("lucene") == 0) {
			return MatchAlgorithm.LUCENE;
		}
		return null;

	public enum MatchAlgorithm {
		PRESENTATION_EXACT,
		PRESENTATION_CONTAINS,
		CODE_EXACT,
		LUCENE
	}

*/