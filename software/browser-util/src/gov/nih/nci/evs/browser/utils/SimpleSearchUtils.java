package gov.nih.nci.evs.browser.utils;

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
 * SimpleSearchUtils (uses LexEVSAPI 6.1 SearchExtension)
 *
 * @author kimong
 *
 */

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

import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;

import org.lexgrid.resolvedvalueset.LexEVSResolvedValueSetService;
import org.lexgrid.resolvedvalueset.impl.LexEVSResolvedValueSetServiceImpl;
import org.lexgrid.valuesets.impl.LexEVSValueSetDefinitionServicesImpl;


public class SimpleSearchUtils {
	private static Logger _logger = Logger.getLogger(SimpleSearchUtils.class);

    public static final int BY_CODE = 1;
    public static final int BY_NAME = 2;

    public static final String EXACT_MATCH = "exactMatch";
    //public static final String STARTS_WITH = "startsWith";
    public static final String CONTAINS = "contains";
    public static final String LUCENE = "lucene";

    public static final String NAMES = "names";
    public static final String CODES = "codes";
    //public static final String PROPERTIES = "properties";
    //public static final String RELATIONSHIPS = "relationships";

    LexBIGService lbSvc = null;
    LexEVSResolvedValueSetService lrvs = null;

    public SimpleSearchUtils() {

	}

    public SimpleSearchUtils(LexBIGService lbSvc) {
        this.lbSvc = lbSvc;
        this.lrvs = new LexEVSResolvedValueSetServiceImpl(lbSvc);
	}

	public void setLexBIGService(LexBIGService lbSvc) {
		this.lbSvc = lbSvc;
		this.lrvs = new LexEVSResolvedValueSetServiceImpl(lbSvc);
	}

    public ResolvedConceptReferencesIterator search(
        Vector<String> schemes, Vector<String> versions, String matchText, String algorithm, String target) throws LBException {
        if (algorithm == null|| target == null) return null;

        if (algorithm.compareToIgnoreCase(EXACT_MATCH) == 0 && target.compareToIgnoreCase(CODES) == 0) {
			return search(schemes, versions, matchText, BY_CODE, "exactMatch");
        } else if (algorithm.compareToIgnoreCase(LUCENE) == 0 && target.compareToIgnoreCase(CODES) == 0) {
			return search(schemes, versions, matchText, BY_CODE, "exactMatch");


        } else if (algorithm.compareToIgnoreCase(EXACT_MATCH) == 0 && target.compareToIgnoreCase(NAMES) == 0) {
			return search(schemes, versions, matchText, BY_NAME, "exactMatch");


        } else if (algorithm.compareToIgnoreCase(LUCENE) == 0 && target.compareToIgnoreCase(NAMES) == 0) {
			return search(schemes, versions, matchText, BY_NAME, "lucene");
        } else if (algorithm.compareToIgnoreCase(CONTAINS) == 0 && target.compareToIgnoreCase(NAMES) == 0) {
			return search(schemes, versions, matchText, BY_NAME, "contains");
		}
		return null;
	}

	public static boolean isSimpleSearchSupported(String algorithm, String target) {
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


    public boolean isSearchExtensionAvaliable() {
		if (getSearchExtension() == null) return false;
		return true;
	}


    public SearchExtension getSearchExtension() {
		SearchExtension searchExtension = null;

		try {
			//LexBIGService lbSvc = new RemoteServerUtil().createLexBIGService();
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

    public boolean searchAllSources(String source) {
		if (source != null && source.compareTo("ALL") != 0) return false;
		return true;
	}


    public ResolvedConceptReferencesIterator search(
        String scheme, String version, String matchText, String target, String algorithm) throws LBException {
		if (scheme == null) return null;
		Vector<String> schemes = new Vector();
		Vector<String> versions = new Vector();
		schemes.add(scheme);
		versions.add(version);

		int searchOption = BY_NAME;
		String search_target = target;
		if (search_target != null) {
			search_target = search_target.toLowerCase();
			if (search_target.startsWith("code")) {
				searchOption = BY_CODE;
			}
		}
		return search(schemes, versions, matchText, searchOption, algorithm);
    }

    public ResolvedConceptReferencesIterator search(
        String scheme, String version, String matchText, int searchOption, String algorithm) throws LBException {
		if (scheme == null) return null;
		Vector<String> schemes = new Vector();
		Vector<String> versions = new Vector();
		schemes.add(scheme);
		versions.add(version);
		return search(schemes, versions, matchText, searchOption, algorithm);
    }


    private void printNumberOfMatches(ResolvedConceptReferencesIterator iterator) {
		if (iterator == null) {
			return;
		}
		try {
			int numRemaining = iterator.numberRemaining();
			System.out.println("Number of matches: " + numRemaining);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


    public ResolvedConceptReferencesIterator search(
        Vector<String> schemes, Vector<String> versions, String matchText, int searchOption, String algorithm) throws LBException {

	    if (schemes == null|| versions == null) return null;
	    if (schemes.size() != versions.size()) return null;
	    if (schemes.size() == 0) return null;
	    if (matchText == null) return null;
	    if (searchOption != BY_CODE && searchOption != BY_NAME) return null;
	    if (searchOption != BY_CODE && algorithm == null) return null;

		//LexBIGService lbSvc = new RemoteServerUtil().createLexBIGService();

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
				//System.out.println("scheme: " + scheme + " (version: " + version + ")");
				versionOrTag.setVersion(version);
				//[NCITERM-643] Name search failed on diacritics.
				ref.setVersionOrTag(versionOrTag);
		    }
			includes.add(ref);
		}

		ResolvedConceptReferencesIterator iterator = null;
		try {
			iterator = searchExtension.search(matchText, includes, convertToMatchAlgorithm(searchOption, algorithm));
			//iterator = searchExtension.search(matchText, includes, null, convertToMatchAlgorithm(searchOption, algorithm), false, true);
			//printNumberOfMatches(iterator);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return iterator;
	}


    protected static void displayRef(ResolvedConceptReference ref) {
		if (ref.getEntityDescription() != null) {
	        System.out.println(ref.getConceptCode() + ":" + ref.getEntityDescription().getContent());
		} else {
			System.out.println(ref.getConceptCode() + ":");
		}
    }




    public void dumpIterator(ResolvedConceptReferencesIterator itr) {
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


	public SearchExtension.MatchAlgorithm convertToMatchAlgorithm(int searchOption, String algorithm) {
		if (algorithm == null) return null;
	    if (searchOption != BY_CODE && searchOption != BY_NAME) return null;
	    if (searchOption == BY_NAME) {
			if (algorithm.compareTo("exactMatch") == 0) {
				return SearchExtension.MatchAlgorithm.PRESENTATION_EXACT;
			} else if (algorithm.compareTo("contains") == 0) {
				return SearchExtension.MatchAlgorithm.PRESENTATION_CONTAINS;
			} else if (algorithm.compareTo("lucene") == 0) {
				return SearchExtension.MatchAlgorithm.LUCENE;
			} else { // Note: there is no startsWith equivalence in the search extension algorithm.
				return SearchExtension.MatchAlgorithm.LUCENE;
			}
		} else if (algorithm.compareTo("exactMatch") == 0 && searchOption == BY_CODE) {
			return SearchExtension.MatchAlgorithm.CODE_EXACT;
		}
		return null;
	}

    public CodedNodeSet getNodeSet(LexBIGService lbSvc, String scheme, CodingSchemeVersionOrTag versionOrTag)
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

    public ResolvedConceptReferencesIterator searchAssociation(
        Vector<String> schemes, Vector<String> versions, String matchText, String source, String algorithm, boolean designationOnly, int searchOption,
        boolean search_source) throws LBException {

		CodedNodeSet cns = null;
		for (int i=0; i<schemes.size(); i++) {

			String scheme = (String) schemes.elementAt(i);
			String version = (String) versions.elementAt(i);

			CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
			if (version != null) {
				csvt.setVersion(version);
			}

			ResolvedConceptReferencesIterator iterator
			   = search(scheme, version, matchText, searchOption, algorithm);

			if (iterator != null) {
				try {
					int numRemaining = iterator.numberRemaining();
					System.out.println("" + numRemaining + " matches found in " + scheme);
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				//LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
				ConceptReferenceList crl = new ConceptReferenceList();

				try {
					while (iterator.hasNext()) {
						try {
							ResolvedConceptReference[] refs =
								iterator.next(100).getResolvedConceptReference();

							for (ResolvedConceptReference ref : refs) {

								    String code = ref.getCode();

									ResolvedConceptReferenceList matches = lbSvc.getNodeGraph(scheme, csvt, null).resolveAsList(
											ConvenienceMethods.createConceptReference(code, scheme), !search_source, search_source, 1, 1, new LocalNameList(), null,
											null, 1024);


									// Analyze the result ...
									if (matches.getResolvedConceptReferenceCount() > 0) {
										Enumeration<? extends ResolvedConceptReference> refEnum = matches.enumerateResolvedConceptReference();
										while (refEnum.hasMoreElements()) {
											ResolvedConceptReference rcr = refEnum.nextElement();

											if (rcr.getTargetOf() != null) {
												System.out.println("rcr.getTargetOf() != null");
											}
											if (rcr.getSourceOf() != null) {
												System.out.println("rcr.getSourceOf() != null");
											}

											AssociationList targetof = null;

											if (search_source) {
												targetof = rcr.getTargetOf();
											} else {
												targetof = rcr.getSourceOf();
											}

											if (targetof != null) {
												Association[] associations = targetof.getAssociation();

												if (associations != null) {
													for (int k = 0; k < associations.length; k++) {
														Association assoc = associations[k];
														AssociatedConcept[] acl = assoc.getAssociatedConcepts().getAssociatedConcept();

														for (int j = 0; j < acl.length; j++) {
															AssociatedConcept ac = acl[j];
															ConceptReference cr = new ConceptReference();
															cr.setCodingSchemeName(ac.getCodingSchemeName());
															cr.setCodeNamespace(ac.getCodeNamespace());
															cr.setConceptCode(ac.getConceptCode());
														    crl.addConceptReference(cr);
														}
													}
												} else {
													System.out.println("associations = null");
												}
											}
										}

									}
							}
						} catch (Exception ex) {
							ex.printStackTrace();
							break;
						}
					}

					if (cns == null) {

						if (crl.getConceptReferenceCount() > 0) {
							cns = getNodeSet(lbSvc, scheme, csvt);
							cns = cns.restrictToCodes(crl);
					    }

					} else {
						CodedNodeSet cns_next = getNodeSet(lbSvc, scheme, csvt);
						if (crl.getConceptReferenceCount() > 0) {
							cns_next = cns_next.restrictToCodes(crl);
							cns = cns.union(cns_next);
						}
					}

				} catch (Exception ex) {
					ex.printStackTrace();
				}
		    }

	    }

		SortOptionList sortOptions = null;
		LocalNameList filterOptions = null;
		LocalNameList propertyNames = null;
		CodedNodeSet.PropertyType[] propertyTypes = null;
		boolean resolveObjects = false;

		if (cns == null) {
			System.out.println("CNS is null???");
			return null;
		}

        ResolvedConceptReferencesIterator asso_iterator = cns.resolve(sortOptions, filterOptions, propertyNames, propertyTypes, resolveObjects);
        return asso_iterator;
    }



//FDA SPL Color Terminology|88f715e912f8afaecb7e670ed4dc8a60|FDA SPL Color Terminology|http://evs.nci.nih.gov/valueset/C54453

    public Vector getResovedValueSetVersions(String selected_vocabulary) {
		Vector v = new Vector();
		try {
			List<CodingScheme> schemes = lrvs.listAllResolvedValueSets();
			for (int i = 0; i < schemes.size(); i++) {
				CodingScheme cs = schemes.get(i);
				int j = i+1;
				String key = cs.getCodingSchemeURI();
				String cs_name = cs.getCodingSchemeName();
				String name = cs.getFormalName();
				String value = cs.getRepresentsVersion();

				if (key.compareTo(selected_vocabulary) == 0 || cs_name.compareTo(selected_vocabulary) == 0 || name.compareTo(selected_vocabulary) == 0) {
                    v.add(value);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return v;
    }

    public static void main(String [] args) {
		try {
			LexBIGService lbSvc = LexBIGServiceImpl.defaultInstance();
			SimpleSearchUtils test = new SimpleSearchUtils(lbSvc);

			String vsd_uri = "http://evs.nci.nih.gov/valueset/C54453";
			vsd_uri = "FDA SPL Color Terminology";

			String matchText = "red";
			String matchAlgorithm = "exactMatch";
			int searchOption = BY_NAME;

			Vector versions = test.getResovedValueSetVersions(vsd_uri);
			String version = null;
			for (int i=0; i<versions.size(); i++) {
				version = (String) versions.elementAt(i);
				//System.out.println("Version: " + version);
			}

			String checked_vocabularies = vsd_uri;
			ResolvedConceptReferencesIterator iterator = test.search(checked_vocabularies, null,
				matchText, searchOption, matchAlgorithm);


            if (iterator != null) {
				try {
					int numberRemaining = iterator.numberRemaining();
					//System.out.println("Number of matches: " + numberRemaining);

					while (iterator.hasNext()) {
						ResolvedConceptReference rcr = (ResolvedConceptReference) iterator.next();
						if (rcr.getEntityDescription() != null) {
							System.out.println(rcr.getEntityDescription().getContent() + "(" + rcr.getConceptCode() + ")");
						} else {
							System.out.println("(" + rcr.getConceptCode() + ")");
						}
					}


				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else {
				System.out.println("searchByName returns NULL???");
			}

			checked_vocabularies = "NCI_Thesaurus";
			version = null;
			iterator = test.search(checked_vocabularies, null,
				matchText, searchOption, matchAlgorithm);


            if (iterator != null) {
				try {
					int numberRemaining = iterator.numberRemaining();
					System.out.println("Number of matches: " + numberRemaining);

					while (iterator.hasNext()) {
						ResolvedConceptReference rcr = (ResolvedConceptReference) iterator.next();
						if (rcr.getEntityDescription() != null) {
							System.out.println(rcr.getEntityDescription().getContent() + "(" + rcr.getConceptCode() + ")");
						} else {
							System.out.println("(" + rcr.getConceptCode() + ")");
						}
					}

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else {
				System.out.println("searchByName returns NULL???");
			}


		} catch (Exception ex) {

		}
    }

}


