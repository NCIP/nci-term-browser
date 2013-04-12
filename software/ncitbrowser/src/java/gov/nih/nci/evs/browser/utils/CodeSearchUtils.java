/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.utils;

import java.util.*;

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


/**
 * 
 */

/**
 * @author EVS Team
 * @version 1.0
 *
 *          Modification history Initial implementation kim.ong@ngc.com
 *
 */
 public class CodeSearchUtils {
    private static Logger _logger = Logger.getLogger(CodeSearchUtils.class);

    public CodeSearchUtils() {

    }

    public static CodedNodeSet getNodeSet(LexBIGService lbSvc, String scheme, CodingSchemeVersionOrTag versionOrTag)
        throws Exception {
		CodedNodeSet cns = null;
		try {
			cns = lbSvc.getCodingSchemeConcepts(scheme, versionOrTag);
			CodedNodeSet.AnonymousOption restrictToAnonymous = CodedNodeSet.AnonymousOption.NON_ANONYMOUS_ONLY;
			cns = cns.restrictToAnonymous(restrictToAnonymous);
	    } catch (Exception ex) {
			ex.printStackTrace();
		}
		return cns;
	}

    public String findBestContainsAlgorithm(String matchText) {
        if (matchText == null)
            return "nonLeadingWildcardLiteralSubString";
        matchText = matchText.trim();
        if (matchText.length() == 0)
            return "nonLeadingWildcardLiteralSubString"; // or null
        if (matchText.length() > 1)
            return "nonLeadingWildcardLiteralSubString";
        char ch = matchText.charAt(0);
        if (Character.isDigit(ch))
            return "literal";
        else if (Character.isLetter(ch))
            return "LuceneQuery";
        else
            return "literalContains";
    }

    public static CodedNodeSet restrictToSource(CodedNodeSet cns, String source) {
        if (cns == null)
            return cns;
        if (source == null || source.compareTo("*") == 0
            || source.compareTo("") == 0 || source.compareTo("ALL") == 0)
            return cns;

        LocalNameList contextList = null;
        LocalNameList sourceLnL = null;
        NameAndValueList qualifierList = null;

        Vector<String> w2 = new Vector<String>();
        w2.add(source);
        sourceLnL = vector2LocalNameList(w2);
        LocalNameList propertyLnL = null;
        CodedNodeSet.PropertyType[] types =
            new PropertyType[] { PropertyType.PRESENTATION };
        try {
            cns =
                cns.restrictToProperties(propertyLnL, types, sourceLnL,
                    contextList, qualifierList);
        } catch (Exception ex) {
            _logger.error("restrictToSource throws exceptions.");
            return null;
        }
        return cns;
    }

    private CodedNodeSet restrictToActiveStatus(CodedNodeSet cns,
        boolean activeOnly) {
        if (cns == null)
            return null;
        if (!activeOnly)
            return cns; // no restriction, do nothing
        try {
            cns =
                cns.restrictToStatus(CodedNodeSet.ActiveOption.ACTIVE_ONLY,
                    null);
            return cns;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static LocalNameList vector2LocalNameList(Vector<String> v) {
        if (v == null)
            return null;
        LocalNameList list = new LocalNameList();
        for (int i = 0; i < v.size(); i++) {
            String vEntry = (String) v.elementAt(i);
            list.addEntry(vEntry);
        }
        return list;
    }


    public CodedNodeSet getCodedNodeSetContainingSourceCode(
        String scheme, String version, String sourceAbbr, String code,
        int maxToReturn, boolean searchInactive) {
        if (sourceAbbr == null || code == null)
            return null;

        LexBIGService lbSvc = new RemoteServerUtil().createLexBIGService();
        CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
        versionOrTag.setVersion(version);

        if (lbSvc == null) {
            _logger.warn("lbSvc = null");
            return null;
        }

        LocalNameList contextList = null;
        NameAndValueList qualifierList = null;

        Vector<String> v = null;
		if (code.compareTo("") != 0) {
            qualifierList = new NameAndValueList();
            NameAndValue nv = new NameAndValue();
            nv.setName("source-code");
            nv.setContent(code);
            qualifierList.addNameAndValue(nv);
        }

        LocalNameList propertyLnL = null;
        // sourceLnL
        Vector<String> w2 = new Vector<String>();
        w2.add(sourceAbbr);
        LocalNameList sourceLnL = vector2LocalNameList(w2);
        if (sourceAbbr.compareTo("*") == 0
            || sourceAbbr.compareToIgnoreCase("ALL") == 0) {
            sourceLnL = null;
        }

        SortOptionList sortCriteria = null;// Constructors.createSortOptionList(new
                                           // String[]{"matchToQuery", "code"});
        CodedNodeSet cns = null;
        try {
            cns = getNodeSet(lbSvc, scheme, versionOrTag);
            if (cns == null) {
                _logger.warn("lbSvc.getCodingSchemeConceptsd returns null");
                return null;
            }
            CodedNodeSet.PropertyType[] types =
                new PropertyType[] { PropertyType.PRESENTATION };
            cns =
                cns.restrictToProperties(propertyLnL, types, sourceLnL,
                    contextList, qualifierList);

            if (cns != null) {
                boolean activeOnly = !searchInactive;
                cns = restrictToActiveStatus(cns, activeOnly);
            }

        } catch (Exception e) {
            // getLogger().error("ERROR: Exception in findConceptWithSourceCodeMatching.");
            return null;
        }
        return cns;
    }




    public ResolvedConceptReferencesIterator findConceptWithSourceCodeMatching(
        String scheme, String version, String sourceAbbr, String code,
        int maxToReturn, boolean searchInactive) {
        if (sourceAbbr == null || code == null)
            return null;
        ResolvedConceptReferencesIterator matchIterator = null;

        LexBIGService lbSvc = new RemoteServerUtil().createLexBIGService();
        CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
        versionOrTag.setVersion(version);

        if (lbSvc == null) {
            _logger.warn("lbSvc = null");
            return null;
        }

        LocalNameList contextList = null;
        NameAndValueList qualifierList = null;

        Vector<String> v = null;

        //if (code != null && code.compareTo("") != 0) {
		if (code.compareTo("") != 0) {
            qualifierList = new NameAndValueList();
            NameAndValue nv = new NameAndValue();
            nv.setName("source-code");
            nv.setContent(code);
            qualifierList.addNameAndValue(nv);
        }

        LocalNameList propertyLnL = null;
        // sourceLnL
        Vector<String> w2 = new Vector<String>();
        w2.add(sourceAbbr);
        LocalNameList sourceLnL = vector2LocalNameList(w2);
        if (sourceAbbr.compareTo("*") == 0
            || sourceAbbr.compareToIgnoreCase("ALL") == 0) {
            sourceLnL = null;
        }

        SortOptionList sortCriteria = null;// Constructors.createSortOptionList(new
                                           // String[]{"matchToQuery", "code"});
        try {
            CodedNodeSet cns = lbSvc.getCodingSchemeConcepts(scheme, null);
            if (cns == null) {
                _logger.warn("lbSvc.getCodingSchemeConceptsd returns null");
                return null;
            }
            CodedNodeSet.PropertyType[] types =
                new PropertyType[] { PropertyType.PRESENTATION };
            cns =
                cns.restrictToProperties(propertyLnL, types, sourceLnL,
                    contextList, qualifierList);

            if (cns != null) {
                boolean activeOnly = !searchInactive;
                cns = restrictToActiveStatus(cns, activeOnly);

                try {
                    matchIterator = cns.resolve(sortCriteria, null, null);// ConvenienceMethods.createLocalNameList(getPropertyForCodingScheme(cs)),null);
                } catch (Exception ex) {

                }
            }

        } catch (Exception e) {
            // getLogger().error("ERROR: Exception in findConceptWithSourceCodeMatching.");
            return null;
        }
        return matchIterator;
    }

     public ResolvedConceptReferencesIteratorWrapper searchByCode(String scheme,
         String version, String matchText, String source, String matchAlgorithm,
         boolean ranking, int maxToReturn) {

         ResolvedConceptReferencesIterator iterator = null;
         iterator =
             matchConceptCode(scheme, version, matchText, source, "LuceneQuery");
         try {
             int size = iterator.numberRemaining();
             System.out.println("matchConceptCode returns: " + size);
             if (size == 0) {
                 iterator =
                     findConceptWithSourceCodeMatching(scheme, version, source,
                         matchText, maxToReturn, true);
             }

             return new ResolvedConceptReferencesIteratorWrapper(iterator);
         } catch (Exception ex) {
             ex.printStackTrace();
         }
         return null;
    }

    public CodedNodeSet getCodedNodeSetContainingCode(String scheme,
        String version, String matchText, String source, boolean searchInactive) {
        LexBIGService lbs = RemoteServerUtil.createLexBIGService();
        if (lbs == null) return null;
        ResolvedConceptReferencesIterator iterator = null;
        CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
        if (version != null)
            versionOrTag.setVersion(version);
        CodedNodeSet cns = null;
        String matchAlgorithm = "LuceneQuery";
        try {
            //cns = lbs.getNodeSet(scheme, versionOrTag, null);
            cns = getNodeSet(lbs, scheme, versionOrTag);
            if (cns == null) return null;
            if (source != null && source.compareTo("ALL") != 0) {
                cns = restrictToSource(cns, source);
            }
            CodedNodeSet.PropertyType[] propertyTypes = null;
            LocalNameList sourceList = null;
            LocalNameList contextList = null;
            NameAndValueList qualifierList = null;
            cns =
                cns.restrictToMatchingProperties(ConvenienceMethods
                    .createLocalNameList(new String[] { "conceptCode" }),
                    propertyTypes, sourceList, contextList, qualifierList,
                    matchText, matchAlgorithm, null);
            if (cns != null) {
                boolean activeOnly = !searchInactive;
                cns = restrictToActiveStatus(cns, activeOnly);
            }
        } catch (Exception ex) {
            _logger.error("WARNING: searchByCode throws exception.");
        }
        return cns;
	}


    public ResolvedConceptReferencesIterator matchConceptCode(String scheme,
        String version, String matchText, String source, String matchAlgorithm) {
        ResolvedConceptReferencesIterator iterator = null;

        CodedNodeSet cns = getCodedNodeSetContainingCode(scheme,
             version, matchText, source, false);

		LocalNameList restrictToProperties = new LocalNameList();
		SortOptionList sortCriteria = null;
		try {
			boolean resolveConcepts = false;
			try {
				long ms = System.currentTimeMillis(), delay = 0;
				iterator =
					cns.resolve(sortCriteria, null, restrictToProperties,
						null, resolveConcepts);

				int size = iterator.numberRemaining();
				_logger.debug("cns.resolve size: " + size);

			} catch (Exception e) {
				_logger.error("ERROR: cns.resolve throws exceptions.");
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
        return iterator;
    }

    public ResolvedConceptReferencesIteratorWrapper searchByCode(
        Vector<String> schemes, Vector<String> versions, String matchText,
        String source, String matchAlgorithm, boolean ranking, int maxToReturn) {
		return searchByCode(
			schemes,  versions, matchText,
			source, matchAlgorithm, ranking, maxToReturn, false);
	}

	public CodedNodeSet union(CodedNodeSet cns, CodedNodeSet cns_2) {
		if (cns == null && cns_2 == null) return null;
		if (cns != null && cns_2 == null) return cns;
		if (cns == null && cns_2 != null) return cns_2;
		try {
			cns = cns.union(cns_2);
			return cns;
	    } catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}


    public ResolvedConceptReferencesIteratorWrapper searchByCode(
        Vector<String> schemes, Vector<String> versions, String matchText,
        String source, String matchAlgorithm, boolean ranking, int maxToReturn, boolean activeOnly) {

		if (matchText == null || matchText.trim().length() == 0) return null;
		System.out.println("matchText: " + matchText);

		LocalNameList contextList = null;
		NameAndValueList qualifierList = null;
		LocalNameList propertyLnL = null;
		SortOptionList sortCriteria = null;
		LocalNameList sourceLnL = null;
		LocalNameList sourceList = null;
		LocalNameList restrictToProperties = new LocalNameList();
		CodedNodeSet.PropertyType[] propertyTypes = null;
		boolean resolveConcepts = false;
		CodedNodeSet.PropertyType[] types = new PropertyType[] { PropertyType.PRESENTATION };
        Vector<CodedNodeSet> cns_vec = new Vector<CodedNodeSet>();

        try {
            matchText = matchText.trim();
            String code = matchText;
            if (matchAlgorithm.compareToIgnoreCase("contains") == 0) {
                matchAlgorithm = findBestContainsAlgorithm(matchText);
            }

            LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
            for (int i = 0; i < schemes.size(); i++) {
                String scheme = (String) schemes.elementAt(i);
                String version = (String) versions.elementAt(i);
                System.out.println("scheme: " + scheme);
				CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
				if (version != null)
					versionOrTag.setVersion(version);

                boolean searchInactive = !activeOnly;
                // Match concept code:
				CodedNodeSet cns = getCodedNodeSetContainingCode(scheme,
					version, matchText, source, searchInactive);
				CodedNodeSet cns_2 = getCodedNodeSetContainingSourceCode(
					scheme, version, source, code, maxToReturn, searchInactive);
                cns = union(cns, cns_2);
				if (cns != null) {
					cns_vec.add(cns);
				}
			}
			ResolvedConceptReferencesIterator iterator =
				new QuickUnionIterator(cns_vec, sortCriteria, null,
					restrictToProperties, null, resolveConcepts);
			if (iterator != null) {
				return new ResolvedConceptReferencesIteratorWrapper(iterator);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

}