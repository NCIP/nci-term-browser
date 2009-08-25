package gov.nih.nci.evs.browser.utils;

import java.util.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.Arrays;

import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.SearchDesignationOption;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Collections.SortOptionList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.concepts.Concept;
import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.LexBIG.DataModel.Collections.ConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.ConceptReference;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeGraph;
import org.LexGrid.LexBIG.DataModel.Collections.NameAndValueList;
import org.LexGrid.LexBIG.DataModel.Core.NameAndValue;
import org.LexGrid.LexBIG.DataModel.Collections.AssociationList;
import org.LexGrid.LexBIG.DataModel.Core.AssociatedConcept;
import org.LexGrid.LexBIG.DataModel.Core.Association;
import org.LexGrid.LexBIG.DataModel.Collections.AssociatedConceptList;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.concepts.Presentation;
import org.LexGrid.LexBIG.Utility.ConvenienceMethods;
import org.LexGrid.LexBIG.DataModel.Core.types.CodingSchemeVersionStatus;
import org.LexGrid.LexBIG.Extensions.Generic.LexBIGServiceConvenienceMethods;
import org.LexGrid.naming.Mappings;
import org.LexGrid.naming.SupportedHierarchy;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.RenderingDetail;
import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeTagList;

import gov.nih.nci.evs.browser.properties.NCItBrowserProperties;
import static gov.nih.nci.evs.browser.common.Constants.*;
import org.LexGrid.naming.SupportedNamespace;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.LexGrid.commonTypes.EntityDescription;
//import org.LexGrid.LexBIG.DataModel.enums.SearchDesignationOption;
import org.apache.commons.codec.language.DoubleMetaphone;

import org.LexGrid.LexBIG.Utility.Constructors;

/**
 * <!-- LICENSE_TEXT_START -->
 * Copyright 2008,2009 NGIT. This software was developed in conjunction with the National Cancer Institute,
 * and so to the extent government employees are co-authors, any rights in such works shall be subject to Title 17 of the United States Code, section 105.
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the disclaimer of Article 3, below. Redistributions
 * in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 * 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 * "This product includes software developed by NGIT and the National Cancer Institute."
 * If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself,
 * wherever such third-party acknowledgments normally appear.
 * 3. The names "The National Cancer Institute", "NCI" and "NGIT" must not be used to endorse or promote products derived from this software.
 * 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize
 * the recipient to use any trademarks owned by either NCI or NGIT
 * 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE NATIONAL CANCER INSTITUTE,
 * NGIT, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */

/**
 * @author EVS Team
 * @version 1.0
 *
 * Modification history
 *     Initial implementation kim.ong@ngc.com
 *
 */

public class SearchUtils {
	private DoubleMetaphone doubleMetaphone = null;

	private int max_str_length = 1000;
	private int penalty_multiplier_1 = 1;
	private int penalty_multiplier_2 = 2;

	private boolean apply_sort_score = false;
	private boolean sort_by_pt_only = true;

	Connection con;
	Statement stmt;
	ResultSet rs;

	private static org.LexGrid.LexBIG.LexBIGService.LexBIGService lbSvc = null;
	public org.LexGrid.LexBIG.Utility.ConvenienceMethods lbConvMethods = null;
	public CodingSchemeRenderingList csrl = null;
	private static HashMap codingSchemeMap = null;
	private static HashMap csnv2codingSchemeNameMap = null;
	private static HashMap csnv2VersionMap = null;
	private static String url = null;

	public static int ALL = 0;
	public static int PREFERRED_ONLY = 1;
	public static int NON_PREFERRED_ONLY = 2;

	static int RESOLVE_SOURCE = 1;
	static int RESOLVE_TARGET = -1;
	static int RESTRICT_SOURCE = -1;
	static int RESTRICT_TARGET = 1;

	public static final int SEARCH_NAME_CODE = 1;
	public static final int SEARCH_DEFINITION = 2;

	public static final int SEARCH_PROPERTY_VALUE = 3;
	public static final int SEARCH_ROLE_VALUE = 6;
	public static final int SEARCH_ASSOCIATION_VALUE = 7;

	static final List<String> STOP_WORDS = Arrays.asList(new String[] { "a",
			"an", "and", "by", "for", "of", "on", "in", "nos", "the", "to",
			"with" });

	public SearchUtils() {
		initializeSortParameters();
	}

	public SearchUtils(String url) {
		SearchUtils.url = url;
		initializeSortParameters();
	}

	private void initializeSortParameters() {
		try {
			NCItBrowserProperties properties = NCItBrowserProperties
					.getInstance();
			String sort_str = properties
					.getProperty(NCItBrowserProperties.SORT_BY_SCORE);
			sort_by_pt_only = true;
			apply_sort_score = true;
			if (sort_str != null) {
				if (sort_str.compareTo("false") == 0) {
					apply_sort_score = false;
				} else if (sort_str.compareToIgnoreCase("all") == 0) {
					sort_by_pt_only = false;
				}
			}

		} catch (Exception ex) {

		}
	}

	private static void setCodingSchemeMap() {
		codingSchemeMap = new HashMap();
		csnv2codingSchemeNameMap = new HashMap();
		csnv2VersionMap = new HashMap();

		try {
			//RemoteServerUtil rsu = new RemoteServerUtil();
			//EVSApplicationService lbSvc = rsu.createLexBIGService();
			LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
			CodingSchemeRenderingList csrl = lbSvc.getSupportedCodingSchemes();
			if (csrl == null)
				System.out.println("csrl is NULL");

			CodingSchemeRendering[] csrs = csrl.getCodingSchemeRendering();
			for (int i = 0; i < csrs.length; i++) {
				CodingSchemeRendering csr = csrs[i];
				Boolean isActive = csr.getRenderingDetail().getVersionStatus()
						.equals(CodingSchemeVersionStatus.ACTIVE);
				if (isActive != null && isActive.equals(Boolean.TRUE)) {
					CodingSchemeSummary css = csr.getCodingSchemeSummary();
					String formalname = css.getFormalName();
					String representsVersion = css.getRepresentsVersion();
					CodingSchemeVersionOrTag vt = new CodingSchemeVersionOrTag();
					vt.setVersion(representsVersion);

					CodingScheme scheme = null;
					try {
						scheme = lbSvc.resolveCodingScheme(formalname, vt);
						if (scheme != null) {
							codingSchemeMap.put((Object) formalname,
									(Object) scheme);

							String value = formalname + " (version: "
									+ representsVersion + ")";
							System.out.println(value);

							csnv2codingSchemeNameMap.put(value, formalname);
							csnv2VersionMap.put(value, representsVersion);

						}

					} catch (Exception e) {
						String urn = css.getCodingSchemeURI();
						try {
							scheme = lbSvc.resolveCodingScheme(urn, vt);
							if (scheme != null) {
								codingSchemeMap.put((Object) formalname,
										(Object) scheme);

								String value = formalname + " (version: "
										+ representsVersion + ")";
								System.out.println(value);

								csnv2codingSchemeNameMap.put(value, formalname);
								csnv2VersionMap.put(value, representsVersion);

							}

						} catch (Exception ex) {

							String localname = css.getLocalName();
							try {
								scheme = lbSvc.resolveCodingScheme(localname,
										vt);
								if (scheme != null) {
									codingSchemeMap.put((Object) formalname,
											(Object) scheme);

									String value = formalname + " (version: "
											+ representsVersion + ")";
									System.out.println(value);

									csnv2codingSchemeNameMap.put(value,
											formalname);
									csnv2VersionMap.put(value,
											representsVersion);

								}
							} catch (Exception e2) {
								e2.printStackTrace();
							}
						}
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Vector getSuperconceptCodes(String scheme, String version,
			String code) { //throws LBException{
		long ms = System.currentTimeMillis();
		Vector v = new Vector();
		try {
			//EVSApplicationService lbSvc = new RemoteServerUtil().createLexBIGService(this.url);
			LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
			LexBIGServiceConvenienceMethods lbscm = (LexBIGServiceConvenienceMethods) lbSvc
					.getGenericExtension("LexBIGServiceConvenienceMethods");
			lbscm.setLexBIGService(lbSvc);
			CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
			csvt.setVersion(version);
			String desc = null;
			try {
				desc = lbscm.createCodeNodeSet(new String[] { code }, scheme,
						csvt).resolveToList(null, null, null, 1)
						.getResolvedConceptReference(0).getEntityDescription()
						.getContent();
			} catch (Exception e) {
				desc = "<not found>";
			}

			// Iterate through all hierarchies and levels ...
			String[] hierarchyIDs = lbscm.getHierarchyIDs(scheme, csvt);
			for (int k = 0; k < hierarchyIDs.length; k++) {
				String hierarchyID = hierarchyIDs[k];
				AssociationList associations = lbscm.getHierarchyLevelPrev(
						scheme, csvt, hierarchyID, code, false, null);
				for (int i = 0; i < associations.getAssociationCount(); i++) {
					Association assoc = associations.getAssociation(i);
					AssociatedConceptList concepts = assoc
							.getAssociatedConcepts();
					for (int j = 0; j < concepts.getAssociatedConceptCount(); j++) {
						AssociatedConcept concept = concepts
								.getAssociatedConcept(j);
						String nextCode = concept.getConceptCode();
						v.add(nextCode);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			System.out.println("Run time (ms): "
					+ (System.currentTimeMillis() - ms));
		}
		return v;
	}

	public Vector getSuperconceptCodes_Local(String scheme, String version,
			String code) { //throws LBException{
		long ms = System.currentTimeMillis();
		Vector v = new Vector();
		try {
			LexBIGService lbSvc = new LexBIGServiceImpl();
			LexBIGServiceConvenienceMethods lbscm = (LexBIGServiceConvenienceMethods) lbSvc
					.getGenericExtension("LexBIGServiceConvenienceMethods");
			lbscm.setLexBIGService(lbSvc);
			CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
			csvt.setVersion(version);
			String desc = null;
			try {
				desc = lbscm.createCodeNodeSet(new String[] { code }, scheme,
						csvt).resolveToList(null, null, null, 1)
						.getResolvedConceptReference(0).getEntityDescription()
						.getContent();
			} catch (Exception e) {
				desc = "<not found>";
			}

			// Iterate through all hierarchies and levels ...
			String[] hierarchyIDs = lbscm.getHierarchyIDs(scheme, csvt);
			for (int k = 0; k < hierarchyIDs.length; k++) {
				String hierarchyID = hierarchyIDs[k];
				AssociationList associations = lbscm.getHierarchyLevelPrev(
						scheme, csvt, hierarchyID, code, false, null);
				for (int i = 0; i < associations.getAssociationCount(); i++) {
					Association assoc = associations.getAssociation(i);
					AssociatedConceptList concepts = assoc
							.getAssociatedConcepts();
					for (int j = 0; j < concepts.getAssociatedConceptCount(); j++) {
						AssociatedConcept concept = concepts
								.getAssociatedConcept(j);
						String nextCode = concept.getConceptCode();
						v.add(nextCode);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			System.out.println("Run time (ms): "
					+ (System.currentTimeMillis() - ms));
		}
		return v;
	}

	public Vector getHierarchyAssociationId(String scheme, String version) {

		Vector association_vec = new Vector();
		try {
			LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();

			// Will handle secured ontologies later.
			CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
			versionOrTag.setVersion(version);
			CodingScheme cs = lbSvc.resolveCodingScheme(scheme, versionOrTag);
			Mappings mappings = cs.getMappings();
			SupportedHierarchy[] hierarchies = mappings.getSupportedHierarchy();
			java.lang.String[] ids = hierarchies[0].getAssociationNames();

			for (int i = 0; i < ids.length; i++) {
				if (!association_vec.contains(ids[i])) {
					association_vec.add(ids[i]);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return association_vec;
	}

	public static String getVocabularyVersionByTag(String codingSchemeName,
			String ltag) {
		if (codingSchemeName == null)
			return null;
		try {
			//EVSApplicationService lbSvc = new RemoteServerUtil().createLexBIGService();
			LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
			CodingSchemeRenderingList lcsrl = lbSvc.getSupportedCodingSchemes();
			CodingSchemeRendering[] csra = lcsrl.getCodingSchemeRendering();
			for (int i = 0; i < csra.length; i++) {
				CodingSchemeRendering csr = csra[i];
				CodingSchemeSummary css = csr.getCodingSchemeSummary();
				if (css.getFormalName().compareTo(codingSchemeName) == 0
						|| css.getLocalName().compareTo(codingSchemeName) == 0) {
					if (ltag == null)
						return css.getRepresentsVersion();
					RenderingDetail rd = csr.getRenderingDetail();
					CodingSchemeTagList cstl = rd.getVersionTags();
					java.lang.String[] tags = cstl.getTag();
					for (int j = 0; j < tags.length; j++) {
						String version_tag = (String) tags[j];
						if (version_tag.compareToIgnoreCase(ltag) == 0) {
							return css.getRepresentsVersion();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Version corresponding to tag " + ltag
				+ " is not found " + " in " + codingSchemeName);
		return null;
	}

	public static Vector<String> getVersionListData(String codingSchemeName) {

		Vector<String> v = new Vector();
		try {
			RemoteServerUtil rsu = new RemoteServerUtil();
			//EVSApplicationService lbSvc = rsu.createLexBIGService();
			LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
			CodingSchemeRenderingList csrl = lbSvc.getSupportedCodingSchemes();
			if (csrl == null)
				System.out.println("csrl is NULL");

			CodingSchemeRendering[] csrs = csrl.getCodingSchemeRendering();
			for (int i = 0; i < csrs.length; i++) {
				CodingSchemeRendering csr = csrs[i];
				Boolean isActive = csr.getRenderingDetail().getVersionStatus()
						.equals(CodingSchemeVersionStatus.ACTIVE);
				if (isActive != null && isActive.equals(Boolean.TRUE)) {
					CodingSchemeSummary css = csr.getCodingSchemeSummary();
					String formalname = css.getFormalName();
					if (formalname.compareTo(codingSchemeName) == 0) {
						String representsVersion = css.getRepresentsVersion();
						v.add(representsVersion);
					}
				}
			}
		} catch (Exception ex) {

		}
		return v;
	}

	protected static Association processForAnonomousNodes(Association assoc) {
		//clone Association except associatedConcepts
		Association temp = new Association();
		temp.setAssociatedData(assoc.getAssociatedData());
		temp.setAssociationName(assoc.getAssociationName());
		temp.setAssociationReference(assoc.getAssociationReference());
		temp.setDirectionalName(assoc.getDirectionalName());
		temp.setAssociatedConcepts(new AssociatedConceptList());

		for (int i = 0; i < assoc.getAssociatedConcepts()
				.getAssociatedConceptCount(); i++) {
			//Conditionals to deal with anonymous nodes and UMLS top nodes "V-X"
			//The first three allow UMLS traversal to top node.
			//The last two are specific to owl anonymous nodes which can act like false
			//top nodes.
			if (assoc.getAssociatedConcepts().getAssociatedConcept(i)
					.getReferencedEntry() != null
					&& assoc.getAssociatedConcepts().getAssociatedConcept(i)
							.getReferencedEntry().getIsAnonymous() != null
					&& assoc.getAssociatedConcepts().getAssociatedConcept(i)
							.getReferencedEntry().getIsAnonymous() != false
					&& !assoc.getAssociatedConcepts().getAssociatedConcept(i)
							.getConceptCode().equals("@")
					&& !assoc.getAssociatedConcepts().getAssociatedConcept(i)
							.getConceptCode().equals("@@")) {
				//do nothing
			} else {
				temp.getAssociatedConcepts().addAssociatedConcept(
						assoc.getAssociatedConcepts().getAssociatedConcept(i));
			}
		}
		return temp;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static NameAndValueList createNameAndValueList(String[] names,
			String[] values) {
		NameAndValueList nvList = new NameAndValueList();
		for (int i = 0; i < names.length; i++) {
			NameAndValue nv = new NameAndValue();
			nv.setName(names[i]);
			if (values != null) {
				nv.setContent(values[i]);
			}
			nvList.addNameAndValue(nv);
		}
		return nvList;
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

	protected static NameAndValueList createNameAndValueList(Vector names,
			Vector values) {
		if (names == null)
			return null;
		NameAndValueList nvList = new NameAndValueList();
		for (int i = 0; i < names.size(); i++) {
			String name = (String) names.elementAt(i);
			String value = (String) values.elementAt(i);
			NameAndValue nv = new NameAndValue();
			nv.setName(name);
			if (value != null) {
				nv.setContent(value);
			}
			nvList.addNameAndValue(nv);
		}
		return nvList;
	}

	protected ResolvedConceptReferencesIterator restrictToMatchingProperty(
			String codingSchemeName, String version, Vector property_vec,
			Vector source_vec, Vector qualifier_name_vec,
			Vector qualifier_value_vec, java.lang.String matchText,
			java.lang.String matchAlgorithm, java.lang.String language) {


System.out.println("restrictToMatchingProperty codingSchemeName " + codingSchemeName);
System.out.println("restrictToMatchingProperty matchText " + matchText);
System.out.println("restrictToMatchingProperty matchAlgorithm " + matchAlgorithm);


		LocalNameList propertyList = vector2LocalNameList(property_vec);
		CodedNodeSet.PropertyType[] propertyTypes = null;
		LocalNameList sourceList = vector2LocalNameList(source_vec);

		NameAndValueList qualifierList = createNameAndValueList(
				qualifier_name_vec, qualifier_value_vec);

		return restrictToMatchingProperty(codingSchemeName, version,
				propertyList, propertyTypes, sourceList, qualifierList,
				matchText, matchAlgorithm, language);

	}

	protected ResolvedConceptReferencesIterator restrictToMatchingProperty(
			String codingSchemeName, String version,

			LocalNameList propertyList,
			CodedNodeSet.PropertyType[] propertyTypes,
			LocalNameList sourceList, NameAndValueList qualifierList,

			java.lang.String matchText, java.lang.String matchAlgorithm,
			java.lang.String language) {

System.out.println("2 restrictToMatchingProperty codingSchemeName " + codingSchemeName);
System.out.println("2 restrictToMatchingProperty matchText " + matchText);
System.out.println("2 restrictToMatchingProperty matchAlgorithm " + matchAlgorithm);


		CodedNodeSet cns = null;
		ResolvedConceptReferencesIterator iterator = null;
		try {
			LexBIGService lbSvc = new RemoteServerUtil().createLexBIGService();
			CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
			if (version != null) versionOrTag.setVersion(version);

			if (lbSvc == null) {
				System.out.println("lbSvc = null");
				return null;
			}


System.out.println("2 restrictToMatchingProperty getCodingSchemeConcepts " + codingSchemeName);


			cns = lbSvc.getCodingSchemeConcepts(codingSchemeName, versionOrTag);
			if (cns == null) {
				System.out.println("cns = null");
				return null;
			} else {

System.out.println("2 restrictToMatchingProperty getCodingSchemeConcepts cns != null " + codingSchemeName);
			}

			LocalNameList contextList = null;
			try {
				cns = cns.restrictToMatchingProperties(propertyList,
						propertyTypes, sourceList, contextList, qualifierList,
						matchText, matchAlgorithm, language);
			} catch (Exception ex) {
System.out.println("2 restrictToMatchingProperty restrictToMatchingProperties matchText " + matchText);
System.out.println("2 restrictToMatchingProperty restrictToMatchingProperties matchAlgorithm " + matchAlgorithm);
System.out.println("2 restrictToMatchingProperty restrictToMatchingProperties returns NULL??? " + codingSchemeName);

				return null;
			}

			LocalNameList restrictToProperties = new LocalNameList();
			// KLO, 030509
			SortOptionList sortCriteria = null;
			//Constructors.createSortOptionList(new String[]{"matchToQuery"});

			try {
				// resolve nothing unless sort_by_pt_only is set to false
				boolean resolveConcepts = false;
				if (apply_sort_score && !sort_by_pt_only)
					resolveConcepts = true;
				try {

System.out.println("2 restrictToMatchingProperty calling cns.resolve " + codingSchemeName);


					iterator = cns.resolve(sortCriteria, null,
							restrictToProperties, null, resolveConcepts);
				} catch (Exception e) {
					System.out.println("ERROR: cns.resolve throws exceptions.");
				}

			} catch (Exception ex) {

System.out.println("2 restrictToMatchingProperty exception encountered " + codingSchemeName);

				//ex.printStackTrace();
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return iterator;
	}

	private boolean isNull(String s) {
		if (s == null)
			return true;
		s = s.trim();
		if (s.length() == 0)
			return true;
		if (s.compareTo("") == 0)
			return true;
		if (s.compareToIgnoreCase("null") == 0)
			return true;
		return false;
	}

	public static Concept getConceptByCode(String codingSchemeName,
			String vers, String ltag, String code) {
		try {
			LexBIGService lbSvc = new RemoteServerUtil().createLexBIGService();
			if (lbSvc == null) {
				System.out.println("lbSvc == null???");
				return null;
			}

			CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
			versionOrTag.setVersion(vers);

			ConceptReferenceList crefs = createConceptReferenceList(
					new String[] { code }, codingSchemeName);

			CodedNodeSet cns = null;

			try {
				cns = lbSvc.getCodingSchemeConcepts(codingSchemeName,
						versionOrTag);
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			cns = cns.restrictToCodes(crefs);
			ResolvedConceptReferenceList matches = cns.resolveToList(null,
					null, null, null, false, 1);

			if (matches == null) {
				System.out.println("Concep not found.");
				return null;
			}

			// Analyze the result ...
			if (matches.getResolvedConceptReferenceCount() > 0) {
				ResolvedConceptReference ref = (ResolvedConceptReference) matches
						.enumerateResolvedConceptReference().nextElement();

				org.LexGrid.concepts.Concept entry = new org.LexGrid.concepts.Concept();
				entry.setEntityCode(ref.getConceptCode());
				entry.setEntityDescription(ref.getEntityDescription());

				//Concept entry = ref.getReferencedEntry();
				return entry;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	public static ConceptReferenceList createConceptReferenceList(
			String[] codes, String codingSchemeName) {
		if (codes == null) {
			return null;
		}
		ConceptReferenceList list = new ConceptReferenceList();
		for (int i = 0; i < codes.length; i++) {
			ConceptReference cr = new ConceptReference();
			cr.setCodingSchemeName(codingSchemeName);
			cr.setConceptCode(codes[i]);
			list.addConceptReference(cr);
		}
		return list;
	}

	public Vector getParentCodes(String scheme, String version, String code) {
		//SearchUtils util = new SearchUtils();
		Vector hierarchicalAssoName_vec = getHierarchyAssociationId(scheme,
				version);
		if (hierarchicalAssoName_vec == null
				|| hierarchicalAssoName_vec.size() == 0) {
			return null;
		}
		String hierarchicalAssoName = (String) hierarchicalAssoName_vec
				.elementAt(0);
		Vector superconcept_vec = getAssociationSources(scheme, version, code,
				hierarchicalAssoName);
		if (superconcept_vec == null)
			return null;
		return superconcept_vec;

	}

	public ResolvedConceptReferenceList getNext(
			ResolvedConceptReferencesIterator iterator) {
		return iterator.getNext();
	}

	/**
	 * Dump_matches to output, for debug purposes
	 *
	 * @param iterator the iterator
	 * @param maxToReturn the max to return
	 */
	public static Vector resolveIterator(
			ResolvedConceptReferencesIterator iterator, int maxToReturn) {
		return resolveIterator(iterator, maxToReturn, null);
	}

	public static Vector resolveIterator(
			ResolvedConceptReferencesIterator iterator, int maxToReturn,
			String code) {
		Vector v = new Vector();
		if (iterator == null) {
			System.out.println("No match.");
			return v;
		}
		try {
			int iteration = 0;

			while (iterator.hasNext()) {
				iteration++;
				iterator = iterator.scroll(maxToReturn);
				ResolvedConceptReferenceList rcrl = iterator.getNext();
				ResolvedConceptReference[] rcra = rcrl
						.getResolvedConceptReference();
				for (int i = 0; i < rcra.length; i++) {
					ResolvedConceptReference rcr = rcra[i];
					//org.LexGrid.concepts.Concept ce = rcr.getReferencedEntry();
					org.LexGrid.concepts.Concept ce = new org.LexGrid.concepts.Concept();
					ce.setEntityCode(rcr.getConceptCode());
					ce.setEntityDescription(rcr.getEntityDescription());

					if (code == null) {
						v.add(ce);
					} else {
						if (ce.getEntityCode().compareTo(code) != 0)
							v.add(ce);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return v;
	}

	public ResolvedConceptReferencesIterator codedNodeGraph2CodedNodeSetIterator(
			CodedNodeGraph cng, ConceptReference graphFocus,
			boolean resolveForward, boolean resolveBackward,
			int resolveAssociationDepth, int maxToReturn) {
		CodedNodeSet cns = null;
		try {
			cns = cng.toNodeList(graphFocus, resolveForward, resolveBackward,
					resolveAssociationDepth, maxToReturn);

			if (cns == null) {
				System.out.println("cng.toNodeList returns null???");
				return null;
			}

			SortOptionList sortCriteria = null;
			//Constructors.createSortOptionList(new String[]{"matchToQuery", "code"});

			LocalNameList propertyNames = null;
			CodedNodeSet.PropertyType[] propertyTypes = null;
			ResolvedConceptReferencesIterator iterator = null;
			try {
				iterator = cns.resolve(sortCriteria, propertyNames,
						propertyTypes);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (iterator == null) {
				System.out.println("cns.resolve returns null???");
			}
			return iterator;

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public Vector getSuperconcepts(String scheme, String version, String code) {
		//String assocName = "hasSubtype";
		String hierarchicalAssoName = "hasSubtype";
		Vector hierarchicalAssoName_vec = getHierarchyAssociationId(scheme,
				version);
		if (hierarchicalAssoName_vec != null
				&& hierarchicalAssoName_vec.size() > 0) {
			hierarchicalAssoName = (String) hierarchicalAssoName_vec
					.elementAt(0);
		}
		return getAssociationSources(scheme, version, code,
				hierarchicalAssoName);
	}

	public Vector getAssociationSources(String scheme, String version,
			String code, String assocName) {
		CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
		if (version != null)
			csvt.setVersion(version);
		ResolvedConceptReferenceList matches = null;
		Vector v = new Vector();
		try {
			//EVSApplicationService lbSvc = new RemoteServerUtil().createLexBIGService();
			LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
			CodedNodeGraph cng = lbSvc.getNodeGraph(scheme, csvt, null);
			NameAndValueList nameAndValueList = createNameAndValueList(
					new String[] { assocName }, null);

			NameAndValueList nameAndValueList_qualifier = null;
			cng = cng.restrictToAssociations(nameAndValueList,
					nameAndValueList_qualifier);
			ConceptReference graphFocus = ConvenienceMethods
					.createConceptReference(code, scheme);

			boolean resolveForward = false;
			boolean resolveBackward = true;

			int resolveAssociationDepth = 1;
			int maxToReturn = 1000;

			ResolvedConceptReferencesIterator iterator = codedNodeGraph2CodedNodeSetIterator(
					cng, graphFocus, resolveForward, resolveBackward,
					resolveAssociationDepth, maxToReturn);

			v = resolveIterator(iterator, maxToReturn, code);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return v;
	}

	public Vector getSubconcepts(String scheme, String version, String code) {
		//String assocName = "hasSubtype";
		String hierarchicalAssoName = "hasSubtype";
		Vector hierarchicalAssoName_vec = getHierarchyAssociationId(scheme,
				version);
		if (hierarchicalAssoName_vec != null
				&& hierarchicalAssoName_vec.size() > 0) {
			hierarchicalAssoName = (String) hierarchicalAssoName_vec
					.elementAt(0);
		}
		return getAssociationTargets(scheme, version, code,
				hierarchicalAssoName);
	}

	public Vector getAssociationTargets(String scheme, String version,
			String code, String assocName) {
		CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
		if (version != null)
			csvt.setVersion(version);
		ResolvedConceptReferenceList matches = null;
		Vector v = new Vector();
		try {
			//EVSApplicationService lbSvc = new RemoteServerUtil().createLexBIGService();
			LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
			CodedNodeGraph cng = lbSvc.getNodeGraph(scheme, csvt, null);
			NameAndValueList nameAndValueList = createNameAndValueList(
					new String[] { assocName }, null);

			NameAndValueList nameAndValueList_qualifier = null;
			cng = cng.restrictToAssociations(nameAndValueList,
					nameAndValueList_qualifier);
			ConceptReference graphFocus = ConvenienceMethods
					.createConceptReference(code, scheme);

			boolean resolveForward = true;
			boolean resolveBackward = false;

			int resolveAssociationDepth = 1;
			int maxToReturn = 1000;

			ResolvedConceptReferencesIterator iterator = codedNodeGraph2CodedNodeSetIterator(
					cng, graphFocus, resolveForward, resolveBackward,
					resolveAssociationDepth, maxToReturn);

			v = resolveIterator(iterator, maxToReturn, code);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return v;
	}

	//search for "lycogen"
	public static String preprocessContains(String s) {
		if (s == null)
			return null;
		if (s.length() == 0)
			return null;
		s = s.trim();
		List<String> words = toWords(s, "\\s", false, false);
		String delim = ".*";
		StringBuffer searchPhrase = new StringBuffer();

		int k = -1;
		searchPhrase.append(delim);

		for (int i = 0; i < words.size(); i++) {
			String wd = (String) words.get(i);
			wd = wd.trim();
			if (wd.compareTo("") != 0) {
				searchPhrase.append(wd);
				if (words.size() > 1 && i < words.size() - 1) {
					searchPhrase.append("\\s");
				}
			}
		}
		searchPhrase.append(delim);
		String t = searchPhrase.toString();
		//System.out.println("preprocessContains " + t);
		return t;
	}

	private boolean isNumber(String s) {
		if (s.length() == 0)
			return false;
		for (int i = 0; i < s.length(); i++) {
			if (!Character.isDigit(s.charAt(i)))
				return false;
		}
		return true;
	}

/*
	public Vector<org.LexGrid.concepts.Concept> searchByName(String scheme, String version, String matchText, String matchAlgorithm, int maxToReturn) {
		String matchText0 = matchText;
		String matchAlgorithm0 = matchAlgorithm;
		matchText0 = matchText0.trim();


System.out.println("searchByName scheme: " + scheme);
System.out.println("searchByName matchText: " + matchText);
System.out.println("searchByName matchAlgorithm: " + matchAlgorithm);
System.out.println("searchByName maxToReturn: " + maxToReturn);


		boolean preprocess = true;
		if (matchText == null || matchText.length() == 0) {
			return new Vector();
		}

		matchText = matchText.trim();
		if (matchAlgorithm.compareToIgnoreCase("exactMatch") == 0) {
			//KLO 032409
			if (!isNumber(matchText)) {
				if (nonAlphabetic(matchText) || matchText.indexOf(".") != -1
						|| matchText.indexOf("/") != -1) {
					return searchByName(scheme, version, matchText, "RegExp",
							maxToReturn);
				}
			}
			if (containsSpecialChars(matchText)) {
				return searchByName(scheme, version, matchText, "RegExp",
						maxToReturn);
			}
		}

		else if (matchAlgorithm.compareToIgnoreCase("startsWith") == 0) {

		} else if (matchAlgorithm.compareToIgnoreCase("contains") == 0) //p11.1-q11.1  /100{WBC}
		{

            String delim = ".*";
			if (containsSpecialChars(matchText)) {

				matchText = delim + matchText + delim;
				matchAlgorithm = "RegExp";
				preprocess = false;
			} else if (matchText.indexOf(" ") != -1) {
				matchText = preprocessContains(matchText);
				matchAlgorithm = "RegExp";
				preprocess = false;
				//KLO 051209
			} else if (matchText.indexOf(" ") == -1) {
				matchText = delim + matchText + delim;
				matchAlgorithm = "RegExp";
				preprocess = false;
			}
		}
		if (matchAlgorithm.compareToIgnoreCase("RegExp") == 0 && preprocess) {
			matchText = preprocessRegExp(matchText);
		}

		System.out.println("(2) matchText: " + matchText);
		System.out.println("(2) matchAlgorithm: " + matchAlgorithm);

		LocalNameList propertyList = null;
		CodedNodeSet.PropertyType[] propertyTypes = new CodedNodeSet.PropertyType[1];
		propertyTypes[0] = CodedNodeSet.PropertyType.PRESENTATION;

		LocalNameList sourceList = null;
		NameAndValueList qualifierList = null;
		String language = null;
		ResolvedConceptReferencesIterator iterator = restrictToMatchingProperty(
				scheme, version,
				propertyList, propertyTypes, sourceList, qualifierList,
				matchText, matchAlgorithm, language);

		// KLO, 042909

System.out.println("Step 1: ");

		if (iterator == null) {
			System.out.println("Step 1 iterator == null: ");
		}

		if (iterator != null) {


			if (apply_sort_score) {
				long ms = System.currentTimeMillis();
				try {
					if (sort_by_pt_only) {
						iterator = sortByScore(matchText0, iterator,
								maxToReturn, true);
					} else {
						iterator = sortByScore(matchText0, iterator,
								maxToReturn);
					}
				} catch (Exception ex) {

				}
				System.out.println("Sorting delay ---- Run time (ms): "
						+ (System.currentTimeMillis() - ms));
			}

			if (iterator != null) {
				Vector v = resolveIterator(iterator, maxToReturn, null,
						sort_by_pt_only);
				if (v != null && v.size() > 0) {
					if (!apply_sort_score) {
						SortUtils.quickSort(v);
					}
					return v;
				}
				if (matchAlgorithm.compareToIgnoreCase("exactMatch") == 0) {
					Concept c = getConceptByCode(scheme, null, null, matchText0);
					if (c != null) {
						v = new Vector();
						v.add(c);
						return v;
					}
				}
			}
		}

System.out.println("Step 2: ");


		if (matchAlgorithm0.compareToIgnoreCase("exactMatch") == 0) {
			matchText0 = matchText0.trim();
			Concept c = getConceptByCode(scheme, null, null, matchText0);
			if (c != null) {
				Vector v = new Vector();
				v.add(c);
				return v;
			}
		} else if (matchAlgorithm0.compareTo("contains") == 0) { // /100{WBC} & search by code


System.out.println("Step 3: startsWith ");

			return searchByName(scheme, version, matchText0, "startsWith",
					maxToReturn);
		}

		else if (matchAlgorithm.compareTo("RegExp") != 0) {

System.out.println("Step 4: ");

			return searchByName(scheme, version, matchText0, "exactMatch",
					maxToReturn);
		}
		return new Vector();
	}
*/

///////////////////////////////////////////////////////////////////////////////////////////////////////////
// 5.1 implementation
///////////////////////////////////////////////////////////////////////////////////////////////////////////

    public ResolvedConceptReferencesIterator searchByName(String scheme, String version, String matchText, String matchAlgorithm, boolean ranking, int maxToReturn) {
		return searchByName(scheme, version, matchText, null, matchAlgorithm, ranking, maxToReturn);
	}


    public ResolvedConceptReferencesIterator searchByName(String scheme, String version, String matchText, String source, String matchAlgorithm, boolean ranking, int maxToReturn) {
		String matchText0 = matchText;
		String matchAlgorithm0 = matchAlgorithm;
		matchText0 = matchText0.trim();

		boolean preprocess = true;
        if (matchText == null || matchText.length() == 0)
        {
			return null;
		}

        matchText = matchText.trim();
        if (matchAlgorithm.compareToIgnoreCase("contains") == 0) //p11.1-q11.1  /100{WBC}
		{
			matchAlgorithm = "subString";
		}
		System.out.println("algorithm: " + matchAlgorithm);

        CodedNodeSet cns = null;
        ResolvedConceptReferencesIterator iterator = null;
        try {
            LexBIGService lbSvc = new RemoteServerUtil().createLexBIGService();

            if (lbSvc == null)
            {
                System.out.println("lbSvc = null");
                return null;
            }

            CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
            if (version != null) versionOrTag.setVersion(version);
            cns = lbSvc.getNodeSet(scheme, versionOrTag, null);

            if (cns == null)
            {
                System.out.println("cns = null");
                return null;
            }

            //LocalNameList contextList = null;
            try {
				cns = cns.restrictToMatchingDesignations(matchText, SearchDesignationOption.ALL, matchAlgorithm, null);
				cns = restrictToSource(cns, source);
            } catch (Exception ex) {
                return null;
            }

            LocalNameList restrictToProperties = new LocalNameList();
            boolean resolveConcepts = true;
            if (!ranking) resolveConcepts = false;

            SortOptionList sortCriteria = null;

            //if (sortOption.isApplySortScore() && !sortOption.isSortByPtOnly()) {
		    if (ranking){
				System.out.println("*** Sort by Lucene score...");
				sortCriteria = Constructors.createSortOptionList(new String[]{"matchToQuery"});

            } else {
                sortCriteria = Constructors.createSortOptionList(new String[] { "entityDescription" }); //code
                System.out.println("*** Sort alphabetically...");
                resolveConcepts = false;
			}
            try {
               try {
					long ms = System.currentTimeMillis(), delay = 0;
					System.out.println("*** calling cns.resolve..");
                    iterator = cns.resolve(sortCriteria, null, restrictToProperties, null, resolveConcepts);
					System.out.println("*** exit cns.resolve..");
					//Debug.println("cns.resolve delay ---- Run time (ms): " + (delay = System.currentTimeMillis() - ms) + " -- matchAlgorithm " + matchAlgorithm);
                    //DBG.debugDetails(delay, "cns.resolve", "searchByName, CodedNodeSet.resolve");

                }  catch (Exception e) {
                    System.out.println("ERROR: cns.resolve throws exceptions.");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

        if (iterator == null) {
			System.out.println("*** WARNING: iterator == null.." + scheme);
			iterator = matchConceptCode(scheme, version, matchText0, source, "LuceneQuery");
		} else {
			try {
				int size = iterator.numberRemaining();
				System.out.println("*** number of matches: " + size);
				if (size == 0) {
					iterator = matchConceptCode(scheme, version, matchText0, source, "LuceneQuery");
				}
			} catch (Exception e) {

			}
		}
        return iterator;
    }

    public ResolvedConceptReferencesIterator searchByName(Vector schemes, Vector versions, String matchText, String matchAlgorithm, boolean ranking, int maxToReturn) {
		return searchByName(schemes, versions, matchText, null, matchAlgorithm, ranking, maxToReturn);
	}


    public ResolvedConceptReferencesIterator searchByName(Vector schemes, Vector versions, String matchText, String source, String matchAlgorithm, boolean ranking, int maxToReturn) {
		String matchText0 = matchText;
		String matchAlgorithm0 = matchAlgorithm;
		matchText0 = matchText0.trim();

		boolean preprocess = true;
        if (matchText == null || matchText.length() == 0)
        {
			return null;
		}

        matchText = matchText.trim();
        if (matchAlgorithm.compareToIgnoreCase("contains") == 0) //p11.1-q11.1  /100{WBC}
		{
			matchAlgorithm = "subString";
		}
		System.out.println("algorithm: " + matchAlgorithm);

        CodedNodeSet cns = null;
        ResolvedConceptReferencesIterator iterator = null;

        String scheme = null;
        String version = null;


        try {
            LexBIGService lbSvc = new RemoteServerUtil().createLexBIGService();

            if (lbSvc == null)
            {
                System.out.println("lbSvc = null");
                return null;
            }

			Vector cns_vec = new Vector();
			for (int i=0; i<schemes.size(); i++) {
				cns = null;
				iterator = null;
				scheme = (String) schemes.elementAt(i);
				CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
				version = (String) versions.elementAt(i);
				if (version != null) versionOrTag.setVersion(version);

	System.out.println("scheme: " + scheme);
	System.out.println("version: " + version);

				try {
					if (lbSvc == null)
					{
						System.out.println("lbSvc = null");
						return null;
					}

					cns = lbSvc.getNodeSet(scheme, versionOrTag, null);

					if (cns != null)
					{
						try {
							cns = cns.restrictToMatchingDesignations(matchText, null, matchAlgorithm, null);
							cns = restrictToSource(cns, source);
						} catch (Exception ex) {
							//return null;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					//return null;
				}
				if (cns != null) {
	System.out.println("cns_vec.add(cns): " + scheme);
					cns_vec.add(cns);
				}
			}

			iterator = null;
			cns = union(cns_vec);
			if (cns == null) return null;

            LocalNameList restrictToProperties = new LocalNameList();
            boolean resolveConcepts = true;
            if (!ranking) resolveConcepts = false;

            SortOptionList sortCriteria = null;

            //if (sortOption.isApplySortScore() && !sortOption.isSortByPtOnly()) {
		    if (ranking){
				System.out.println("*** Sort by Lucene score...");
				sortCriteria = Constructors.createSortOptionList(new String[]{"matchToQuery"});

            } else {
                sortCriteria = Constructors.createSortOptionList(new String[] { "entityDescription" }); //code
                System.out.println("*** Sort alphabetically...");
                resolveConcepts = false;
			}
            try {
               try {
					long ms = System.currentTimeMillis(), delay = 0;
					System.out.println("*** calling cns.resolve");
                    iterator = cns.resolve(sortCriteria, null, restrictToProperties, null, resolveConcepts);
                    System.out.println("*** exit cns.resolve");
					//Debug.println("cns.resolve delay ---- Run time (ms): " + (delay = System.currentTimeMillis() - ms) + " -- matchAlgorithm " + matchAlgorithm);
                    //DBG.debugDetails(delay, "cns.resolve", "searchByName, CodedNodeSet.resolve");

                }  catch (Exception e) {
                    System.out.println("ERROR: cns.resolve throws exceptions.");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

        if (iterator == null) {
			iterator = matchConceptCode(scheme, version, matchText0, source, "LuceneQuery");
		} else {
			try {
				int size = iterator.numberRemaining();
				System.out.println("*** number of matches " + size);
				if (size == 0) {
					iterator = matchConceptCode(scheme, version, matchText0, source, "LuceneQuery");
				}
			} catch (Exception e) {

			}
		}
        return iterator;
    }


	public ResolvedConceptReferencesIterator matchConceptCode(String scheme, String version, String matchText, String source, String matchAlgorithm) {
		LexBIGService lbs = RemoteServerUtil.createLexBIGService();
		Vector v = new Vector();
		CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
		if (version != null) versionOrTag.setVersion(version);
		CodedNodeSet cns = null;
		ResolvedConceptReferencesIterator iterator = null;
		try {
			cns = lbs.getNodeSet(scheme, versionOrTag, null);
			if (source != null) cns = restrictToSource(cns, source);
			CodedNodeSet.PropertyType[] propertyTypes = null;
			LocalNameList sourceList = null;
			LocalNameList contextList = null;
			NameAndValueList qualifierList = null;
			cns = cns.restrictToMatchingProperties(ConvenienceMethods.createLocalNameList(new String[]{"conceptCode"}),
					  propertyTypes, sourceList, contextList,
					  qualifierList,matchText, matchAlgorithm, null);

            LocalNameList restrictToProperties = new LocalNameList();
            SortOptionList sortCriteria = null;
            try {
                boolean resolveConcepts = true;
                try {
					long ms = System.currentTimeMillis(), delay = 0;
                    iterator = cns.resolve(sortCriteria, null, restrictToProperties, null, resolveConcepts);

                    int size = iterator.numberRemaining();
                    System.out.println("cns.resolve size: " + size);

                }  catch (Exception e) {
                    System.out.println("ERROR: cns.resolve throws exceptions.");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }

		} catch (Exception ex) {
			System.out.println("WARNING: searchByCode throws exception.");
		}
        return iterator;
	}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*
	public void testSearchByName() {
		String scheme = CODING_SCHEME_NAME;
		String version = null;
		String matchText = "blood";
		String matchAlgorithm = "contains";
		int maxToReturn = 1000;

		long ms = System.currentTimeMillis();
		Vector<org.LexGrid.concepts.Concept> v = searchByName(scheme, version,
				matchText, matchAlgorithm, maxToReturn);
		System.out.println("Run time (ms): "
				+ (System.currentTimeMillis() - ms));
		if (v != null) {
			System.out.println("v.size() = " + v.size());
			for (int i = 0; i < v.size(); i++) {
				int j = i + 1;
				Concept ce = (Concept) v.elementAt(i);
				System.out.println("(" + j + ")" + " " + ce.getEntityCode() + " "
						+ ce.getEntityDescription().getContent());
			}
		}
	}
*/
	protected static List<String> toWords(String s, String delimitRegex,
			boolean removeStopWords, boolean removeDuplicates) {
		String[] words = s.split(delimitRegex);
		List<String> adjusted = new ArrayList<String>();
		for (int i = 0; i < words.length; i++) {
			String temp = words[i].toLowerCase();
			if (removeDuplicates && adjusted.contains(temp))
				continue;
			if (!removeStopWords || !STOP_WORDS.contains(temp))
				adjusted.add(temp);
		}
		return adjusted;
	}

	protected static String[] toWords(String s, boolean removeStopWords) {
		String[] words = s.split("\\s");
		List<String> adjusted = new ArrayList<String>();
		for (int i = 0; i < words.length; i++) {
			if (!removeStopWords || !STOP_WORDS.contains(words[i]))
				adjusted.add(words[i].toLowerCase());
		}
		return adjusted.toArray(new String[adjusted.size()]);
	}

	protected static List<String> toWords2(String s, String delimitRegex,
			boolean removeStopWords, boolean removeDuplicates) {
		s = s.trim();
		s = replaceSpecialCharsWithBlankChar(s);
		List<String> adjusted = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(s);
		while (st.hasMoreTokens()) {
			String temp = st.nextToken().toLowerCase();
			if (removeDuplicates && adjusted.contains(temp))
				continue;
			if (!removeStopWords || !STOP_WORDS.contains(temp)) {
				adjusted.add(temp);
			}
		}
		return adjusted;
	}

	public static String preprocessSearchString(String s) {
		if (s == null)
			return null;
		if (s.length() == 0)
			return null;
		StringBuffer searchPhrase = new StringBuffer();
		List<String> words = toWords(s, "\\s", true, false);
		int k = -1;
		for (int i = 0; i < words.size(); i++) {
			String wd = (String) words.get(i);
			wd = wd.trim();
			if (wd.compareTo("") != 0) {
				k++;
				if (k > 0) {
					searchPhrase.append(" ");
				}
				searchPhrase.append(wd);
			}
		}
		String t = searchPhrase.toString();
		return t;
	}

	public static boolean nonAlphabetic(String s) {
		if (s == null)
			return false;
		if (s.length() == 0)
			return true;
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			if (Character.isLetter(ch))
				return false;
		}
		return true;
	}

	/*
	 private static String replaceSpecialChars(String s){
	 //String escapedChars = "|!(){}[]^\"~*?:;-";
	 String escapedChars = "|!(){}[]^\"~*?;-_";
	 for (int i=0; i<escapedChars.length(); i++)
	 {
	 char c = escapedChars.charAt(i);
	 s = s.replace(c, ' ');
	 }
	 return s;
	 }
	 */

	private static String escapeSpecialChars(String s, String specChars) {
		String escapeChar = "\\";
		StringBuffer regex = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (specChars.indexOf(c) != -1) {
				regex.append(escapeChar);

			}
			regex.append(c);
		}
		return regex.toString();
	}

	private static String replaceSpecialChars(String s) {
		String escapedChars = "/";
		for (int i = 0; i < escapedChars.length(); i++) {
			char c = escapedChars.charAt(i);
			s = s.replace(c, '.');
		}
		return s;
	}

	public static String preprocessRegExp(String s) {
		s = replaceSpecialChars(s);
		//s = escapeSpecialChars(s, "()");
		s = escapeSpecialChars(s, "(){}\\,-[]");
		String prefix = s.toLowerCase();
		String[] words = toWords(prefix, false); // include stop words
		StringBuffer regex = new StringBuffer();
		regex.append('^');
		for (int i = 0; i < words.length; i++) {
			boolean lastWord = i == words.length - 1;
			String word = words[i];
			int word_length = word.length();
			if (word_length > 0) {
				regex.append('(');
				if (word.charAt(word.length() - 1) == '.') {
					regex.append(word.substring(0, word.length()));
					regex.append("\\w*");
				} else {
					regex.append(word);
				}
				regex.append("\\s").append(lastWord ? '*' : '+');
				regex.append(')');
			}
		}
		return regex.toString();
	}

	/************************
	 * Custom sort processing
	 ************************/
	/**
	 * Sorts the given concept references based on a scoring algorithm
	 * designed to provide a more natural ordering.  Scores are determined by
	 * comparing each reference against a provided search term.
	 * @param searchTerm The term used for comparison; single or multi-word.
	 * @param toSort The iterator containing references to sort.
	 * @param maxToReturn Sets upper limit for number of top-scored items returned.
	 * @return Iterator over sorted references.
	 * @throws LBException
	 */

	protected ResolvedConceptReferencesIterator sortByScore(String searchTerm,
			ResolvedConceptReferencesIterator toSort, int maxToReturn)
			throws LBException {
		// Determine the set of individual words to compare against.
		List<String> compareWords = toScoreWords(searchTerm);

		// Create a bucket to store results.
		Map<String, ScoredTerm> scoredResult = new TreeMap<String, ScoredTerm>();

		// Score all items ...
		while (toSort.hasNext()) {
			// Working in chunks of 100.
			ResolvedConceptReferenceList refs = toSort.next(100);
			for (int i = 0; i < refs.getResolvedConceptReferenceCount(); i++) {
				ResolvedConceptReference ref = refs
						.getResolvedConceptReference(i);

				String code = ref.getConceptCode();
				Concept ce = ref.getReferencedEntry();
				ce.setEntityCodeNamespace(ref.getCodingSchemeName());

				// Note: Preferred descriptions carry more weight,
				// but we process all terms to allow the score to improve
				// based on any contained presentation.
				Presentation[] allTermsForConcept = ce.getPresentation();
				for (Presentation p : allTermsForConcept) {
					//float score = score(p.getValue().getContent(), compareWords, p.isIsPreferred(), i);
					float score = score(p.getValue().getContent(), compareWords,
							p.isIsPreferred(), i);

					// Check for a previous match on this code for a different presentation.
					// If already present, keep the highest score.
					if (scoredResult.containsKey(code)) {
						ScoredTerm scoredTerm = (ScoredTerm) scoredResult
								.get(code);
						if (scoredTerm.score > score)
							continue;
					}
					scoredResult.put(code, new ScoredTerm(ref, score));
				}
			}
		}
		// Return an iterator that will sort the scored result.
		return new ScoredIterator(scoredResult.values(), maxToReturn);
	}

	protected ResolvedConceptReferencesIterator sortByScore(String searchTerm,
			ResolvedConceptReferencesIterator toSort, int maxToReturn,
			boolean descriptionOnly) throws LBException {

		if (!descriptionOnly)
			return sortByScore(searchTerm, toSort, maxToReturn);
		// Determine the set of individual words to compare against.
		List<String> compareWords = toScoreWords(searchTerm);

		// Create a bucket to store results.
		Map<String, ScoredTerm> scoredResult = new TreeMap<String, ScoredTerm>();

		// Score all items ...
		while (toSort.hasNext()) {
			// Working in chunks of 100.
			ResolvedConceptReferenceList refs = toSort.next(100);
			for (int i = 0; i < refs.getResolvedConceptReferenceCount(); i++) {
				ResolvedConceptReference ref = refs
						.getResolvedConceptReference(i);
				String code = ref.getConceptCode();
				String name = ref.getEntityDescription().getContent();
				float score = score(name, compareWords, true, i);
				scoredResult.put(code, new ScoredTerm(ref, score));
			}
		}
		// Return an iterator that will sort the scored result.
		return new ScoredIterator(scoredResult.values(), maxToReturn);
	}

    protected float score_contains(String text, String target) {
		String text_lower = text.toLowerCase();
		String target_lower = target.toLowerCase();
		int n = text_lower.indexOf(target_lower);
		if (n == -1) return -1 * max_str_length;
		int m1 = n;
		int m2 = text.length() - (n + target.length());
		int score = max_str_length - penalty_multiplier_2 * m2 - penalty_multiplier_1 * m1;
		return Math.max(0, score);
	}
/*
    protected ResolvedConceptReferencesIterator sortByScore(String searchTerm, ResolvedConceptReferencesIterator toSort, int maxToReturn) throws LBException {
        // Determine the set of individual words to compare against.
        List<String> compareWords = toScoreWords(searchTerm);

        // Create a bucket to store results.
        Map<String, ScoredTerm> scoredResult = new TreeMap<String, ScoredTerm>();

        // Score all items ...
        while (toSort.hasNext()) {
            // Working in chunks of 100.
            ResolvedConceptReferenceList refs = toSort.next(100);
            for (int i = 0; i < refs.getResolvedConceptReferenceCount(); i++) {
                ResolvedConceptReference ref = refs.getResolvedConceptReference(i);

                String code = ref.getConceptCode();
                Concept ce = ref.getReferencedEntry();

// This didn't help. ref.getCodingSchemeName() is "Thesaurus", not "NCI Thesaurus", or "NCI_Thesaurus"
// ce.setEntityCodeNamespace(ref.getCodingSchemeName());

                // Note: Preferred descriptions carry more weight,
                // but we process all terms to allow the score to improve
                // based on any contained presentation.
                Presentation[] allTermsForConcept = ce.getPresentation();
                for (Presentation p : allTermsForConcept) {
                    float score = score(p.getValue().getContent(), compareWords);

                    // Check for a previous match on this code for a different presentation.
                    // If already present, keep the highest score.
                    if (score > 0.) {
						if (scoredResult.containsKey(code)) {
							ScoredTerm scoredTerm = (ScoredTerm) scoredResult.get(code);
							if (scoredTerm.score > score)
								continue;
						}
						scoredResult.put(code, new ScoredTerm(ref, score));
				    }
                }
            }
        }
        // Return an iterator that will sort the scored result.
        return new ScoredIterator(scoredResult.values(), maxToReturn);
    }
*/
    protected ResolvedConceptReferencesIterator sortByScore(String searchTerm, ResolvedConceptReferencesIterator toSort,
                                                            int maxToReturn, String algorithm) throws LBException {
        // Determine the set of individual words to compare against.
        List<String> compareWords = toScoreWords(searchTerm);
        List<String> compareCodes = new ArrayList<String>();
		for (int k=0; k<compareWords.size(); k++) {
			String word = (String) compareWords.get(k);
			compareCodes.add(word);
		}
        String target = "";
		if (algorithm.compareTo("DoubleMetaphoneLuceneQuery") == 0) {
			compareCodes = new ArrayList<String>();
			for (int k=0; k<compareWords.size(); k++) {
				String word = (String) compareWords.get(k);
				String doubleMetaphonecode = doubleMetaphoneEncode(word);
				compareCodes.add(doubleMetaphonecode);
				//System.out.println("*** DoubleMetaphoneLuceneQuery word " + word + " code: " + doubleMetaphone.encode(word));
				target = target + doubleMetaphonecode;
				if (k < compareWords.size()-1) target = target + " ";
			}
		}
        // Create a bucket to store results.
        Map<String, ScoredTerm> scoredResult = new TreeMap<String, ScoredTerm>();
        // Score all items ...

        int knt = 0, nloops = 0;
        long msTotal = System.currentTimeMillis(), msTotal_toSortNext = 0, msTotal_sorted = 0;
        while (toSort.hasNext()) {
            ++nloops;
            // Working in chunks of 100.
            long ms = System.currentTimeMillis(), delay = 0;
            ResolvedConceptReferenceList refs = toSort.next(500); // slow why???
            ////Debug.println("Run time (ms): toSort.next() method call took " + (delay = System.currentTimeMillis() - ms) + " millisec.");
            //DBG.debugDetails("" + nloops + ") toSort.next(500): " + Utils.timeToString(delay) + " [ResolvedConceptReferencesIterator.next]");
            msTotal_toSortNext += delay;

            ms = System.currentTimeMillis();

            for (int i = 0; i < refs.getResolvedConceptReferenceCount(); i++) {
                ResolvedConceptReference ref = refs.getResolvedConceptReference(i);

                String code = ref.getConceptCode();
                Concept ce = ref.getReferencedEntry();
                ce.setEntityCodeNamespace(ref.getCodingSchemeName());

                // Note: Preferred descriptions carry more weight,
                // but we process all terms to allow the score to improve
                // based on any contained presentation.
                Presentation[] allTermsForConcept = ce.getPresentation();
                for (Presentation p : allTermsForConcept) {
					float score = (float) 0.0;
					/*
					if (algorithm.compareTo("DoubleMetaphoneLuceneQuery") != 0) {
                        score = score(p.getValue().getContent(), compareWords);
					} else {
						score = score(p.getValue().getContent(), compareWords, compareCodes, target, true);
					}
					*/
					if (algorithm.compareTo("contains") == 0 || algorithm.compareTo("RegExp") == 0) {
                        score = score_contains(p.getValue().getContent(), searchTerm);
                        //score = score(p.getValue().getContent(), compareWords);
					} else if (algorithm.compareTo("DoubleMetaphoneLuceneQuery") == 0){
						score = score(p.getValue().getContent(), compareWords, compareCodes, target, true);
					} else {
                        score = score(p.getValue().getContent(), compareWords);
					}

					if (score > 0.) {
						// Check for a previous match on this code for a different presentation.
						// If already present, keep the highest score.
						if (scoredResult.containsKey(code)) {
							ScoredTerm scoredTerm = (ScoredTerm) scoredResult.get(code);
							if (scoredTerm.score > score)
								continue;
						}
						scoredResult.put(code, new ScoredTerm(ref, score));
				    }
                }
            }
            int num_concepts = refs.getResolvedConceptReferenceCount();

            knt = knt + num_concepts;
            //if (knt > 1000) break;
            ////Debug.println("" + knt + " completed.  Run time (ms): Assigning scores to " + num_concepts + " concepts took " + (delay = System.currentTimeMillis() - ms) + " millisec.");
            //DBG.debugDetails("" + nloops + ") Sorted [" + knt + " concepts]: " + Utils.timeToString(delay));
            msTotal_sorted += delay;
        }
        //if (//DBG.isPerformanceTesting()) {
            //long duration = System.currentTimeMillis() - msTotal;
            //long avgDuration = duration/nloops;
            //DBG.debugDetails("* Summary of toSort/Sorted calls:");
            //DBG.debugDetails(duration, "Run Time", "sortByScore");
            //DBG.debugDetails("Iterations", nloops);
            //DBG.debugDetails(avgDuration, "Average", "sortByScore");
            //DBG.debugDetails(msTotal_toSortNext, "Total toSort.next time", "sortByScore");
            //DBG.debugDetails(msTotal_sorted, "Total sorted time", "sortByScore");
        //}
        // Return an iterator that will sort the scored result.
        return new ScoredIterator(scoredResult.values(), maxToReturn);
    }

/*
    protected ResolvedConceptReferencesIterator sortByScore(String searchTerm, ResolvedConceptReferencesIterator toSort, int maxToReturn, boolean descriptionOnly) throws LBException {
        if (!descriptionOnly) return sortByScore(searchTerm, toSort, maxToReturn);
        // Determine the set of individual words to compare against.
        List<String> compareWords = toScoreWords(searchTerm);

        // Create a bucket to store results.
        Map<String, ScoredTerm> scoredResult = new TreeMap<String, ScoredTerm>();

        // Score all items ...
        while (toSort.hasNext()) {
            // Working in chunks of 100.
            ResolvedConceptReferenceList refs = toSort.next(100);
            for (int i = 0; i < refs.getResolvedConceptReferenceCount(); i++) {
                ResolvedConceptReference ref = refs.getResolvedConceptReference(i);
                String code = ref.getConceptCode();
                String name = ref.getEntityDescription().getContent();
                float score = score(name, compareWords);
                if (score > 0.) {
                	scoredResult.put(code, new ScoredTerm(ref, score));
			    }
            }
        }
        // Return an iterator that will sort the scored result.
        return new ScoredIterator(scoredResult.values(), maxToReturn);
    }
*/

    protected ResolvedConceptReferencesIterator sortByScore(String searchTerm, ResolvedConceptReferencesIterator toSort, int maxToReturn,
                                                            boolean descriptionOnly, String algorithm) throws LBException {

        if (!descriptionOnly) {
			return sortByScore(searchTerm, toSort, maxToReturn);
		}
        // Determine the set of individual words to compare against.

        List<String> compareWords = toScoreWords(searchTerm);
        List<String> compareCodes = new ArrayList<String>(compareWords.size());
        String target = "";
        if (algorithm.compareTo("DoubleMetaphoneLuceneQuery") == 0) {
			for (int k=0; k<compareWords.size(); k++) {
				String word = (String) compareWords.get(k);
				String doubleMetaphonecode = doubleMetaphoneEncode(word);
				compareCodes.set(k, doubleMetaphonecode);
				target = target + doubleMetaphonecode;
				if (k < compareWords.size()-1) target = target + " ";
			}
		}

        // Create a bucket to store results.
        Map<String, ScoredTerm> scoredResult = new TreeMap<String, ScoredTerm>();

        // Score all items ...
        while (toSort.hasNext()) {
            // Working in chunks of 100.
            long ms = System.currentTimeMillis();

            ResolvedConceptReferenceList refs = toSort.next(500); // slow why???
            //System.out.println("Run time (ms): toSort.next() took " + (System.currentTimeMillis() - ms) + " millisec.");

            ms = System.currentTimeMillis();
            for (int i = 0; i < refs.getResolvedConceptReferenceCount(); i++) {
                ResolvedConceptReference ref = refs.getResolvedConceptReference(i);
                String code = ref.getConceptCode();
                String name = ref.getEntityDescription().getContent();
                float score = (float) 0.0;//score(name, compareWords, true, i);
                /*
				if (algorithm.compareTo("DoubleMetaphoneLuceneQuery") == 0) {
					score = score(name, compareWords, compareCodes, target, true);
				} else {
					score = score(name, compareWords);
				}
				*/

				if (algorithm.compareTo("DoubleMetaphoneLuceneQuery") == 0) {
					score = score(name, compareWords, compareCodes, target, true);
				} else if (algorithm.compareTo("contains") == 0 || algorithm.compareTo("RegExp") == 0) {
					score = score_contains(name, searchTerm);
				} else {
					score = score(name, compareWords);
				}

				if (score > 0.) {
                	scoredResult.put(code, new ScoredTerm(ref, score));
			    }
            }
            //System.out.println("Run time (ms): assigning scores took " + (System.currentTimeMillis() - ms) + " millisec.");
        }
        // Return an iterator that will sort the scored result.
        return new ScoredIterator(scoredResult.values(), maxToReturn);
    }



	/**
	 * Returns a score providing a relative comparison of the given
	 * text against a set of keywords.
	 * <p>
	 * Currently the score is evaluated as a simple percentage
	 * based on number of words in the first set that are also in the
	 * second, with minor adjustment for order (matching later
	 * words given slightly higher weight, anticipating often the
	 * subject of search will follow descriptive text).  Weight
	 * is also increased for shorter phrases (measured in # words)
	 * If the text is indicated to be preferred, the score is doubled
	 * to promote 'bubbling to the top'.
	 * <p>
	 * Ranking from the original search is available but not
	 * currently used in the heuristic (tends to throw-off desired
	 * alphabetic groupings later).
	 *
	 * @param text
	 * @param keywords
	 * @param isPreferred
	 * @param searchRank
	 * @return The score; a higher value indicates a stronger match.
	 */

	protected float score(String text, List<String> keywords,
			boolean isPreferred, float searchRank) {
		List<String> wordsToCompare = toScoreWords(text);
		float totalWords = wordsToCompare.size();
		float matchScore = 0;
		float position = 0;
		for (Iterator<String> words = wordsToCompare.listIterator(); words
				.hasNext(); position++) {
			String word = words.next();
			if (keywords.contains(word))
				matchScore += ((position / 10) + 1);
		}
		return Math.max(0, 100 + (matchScore / totalWords * 100)
				- (totalWords * 2));
		//Math.max(0, 100 + (matchScore / totalWords * 100) - (totalWords * 2))
		//* (isPreferred ? 2 : 1);
	}

    /* scoring method for DoubleMetaphoneLuceneQuery */
    protected float score(String text, List<String> keywords, List<String> keyword_codes, String target, boolean fuzzy_match) {
        List<String> wordsToCompare = toScoreWords(text);
        float totalWords = wordsToCompare.size();
        float matchScore = 0;
        float position = 0;
        String s = "";
        int k = 0;
        for (Iterator<String> words = wordsToCompare.listIterator(); words.hasNext(); position++) {
            String word = words.next();
            if (keywords.contains(word)) {
                //matchScore += ((position / 10) + 1);
                //matchScore = matchScore * 2;
                matchScore = matchScore + (float) 10. * ((position / 10) + 1);
                word = doubleMetaphoneEncode(word);

			} else if (fuzzy_match) {
				word = doubleMetaphoneEncode(word);
				if (keyword_codes.contains(word)) {
					matchScore += ((position / 10) + 1);
				}
			}
			s = s + word;
			if (k < wordsToCompare.size()-1) s = s + " ";
			k++;
        }

        if (s.indexOf(target) == -1) {
			return (float) 0.0;
		}
        float score = Math.max(0, 100 + (matchScore / totalWords * 100) - (totalWords * 2));
        return score;
            //Math.max(0, 100 + (matchScore / totalWords * 100) - (totalWords * 2))
                //* (isPreferred ? 2 : 1);
    }


	/**
	 * Return words from the given string to be used in scoring
	 * algorithms, in order of occurrence and ignoring duplicates,
	 * stop words, whitespace and common separators.
	 * @param s
	 * @return List
	 */

	protected List<String> toScoreWords(String s) {
		//return toWords(s, "[\\s,:+-;]", true, true);
		return toWords2(s, "[\\s,:+-;]", true, true);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private boolean containsSpecialChars(String s) {
		//String escapedChars = "/.|!(){}[]^\"~*?;-_";
		String escapedChars = "/.|!(){}[]^\"~?;-_";
		for (int i = 0; i < escapedChars.length(); i++) {
			char c = escapedChars.charAt(i);
			if (s.indexOf(c) != -1)
				return true;
		}
		return false;
	}

	private static String replaceSpecialCharsWithBlankChar(String s) {
		String escapedChars = "/.|!(){}[]^\"~*?;-_";
		for (int i = 0; i < escapedChars.length(); i++) {
			char c = escapedChars.charAt(i);
			s = s.replace(c, ' ');
		}
		s = s.trim();
		return s;
	}

    public static Vector resolveIterator(ResolvedConceptReferencesIterator iterator, int maxToReturn, String code, boolean sortLight)
    {
        Vector v = new Vector();
        if (iterator == null)
        {
            System.out.println("No match.");
            return v;
        }

        if (maxToReturn <= 0) maxToReturn = 100;
        try {
            int iteration = 0;
            while (iterator.hasNext())
            {
                iteration++;
                iterator = iterator.scroll(maxToReturn);
                ResolvedConceptReferenceList rcrl = iterator.getNext();
                if (rcrl != null) {
					ResolvedConceptReference[] rcra = rcrl.getResolvedConceptReference();
					if (rcra != null && rcra.length > 0) {
						for (int i=0; i<rcra.length; i++)
						{
							ResolvedConceptReference rcr = rcra[i];
							if (rcr != null) {
								if (sortLight) {
									org.LexGrid.concepts.Concept ce = new org.LexGrid.concepts.Concept();
									ce.setEntityCode(rcr.getConceptCode());
									ce.setEntityDescription(rcr.getEntityDescription());
									if (code == null)
									{
										v.add(ce);
									}
									else
									{
										if (ce.getEntityCode().compareTo(code) != 0) v.add(ce);
									}
								} else {
									Concept ce = rcr.getReferencedEntry();
									if (ce == null) {
										System.out.println("rcr.getReferencedEntry() returns null???");
									} else {
										if (code == null) v.add(ce);
										else if (ce.getEntityCode().compareTo(code) != 0) v.add(ce);
									}
								}
						    }
					   }
			       }
		       }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }

///////////////////////////////////////////////////////////////////
    public HashMap getNamespaceId2CodingSchemeFormalNameMapping()
	{
		HashMap hmap = new HashMap();
        LexBIGService lbSvc = new RemoteServerUtil().createLexBIGService();
		if (lbSvc == null)
		{
			System.out.println("setCodingSchemeMap..??????????????" );
			return null;
		}
        try {
            CodingSchemeRenderingList csrl = null;
            try {
				csrl = lbSvc.getSupportedCodingSchemes();
			} catch (LBInvocationException ex) {
				ex.printStackTrace();
				System.out.println("lbSvc.getSupportedCodingSchemes() FAILED..." + ex.getCause() );
				return null;
			}

			CodingSchemeRendering[] csrs = csrl.getCodingSchemeRendering();
			for (int i=0; i<csrs.length; i++)
			{
				CodingSchemeRendering csr = csrs[i];
            	Boolean isActive = csr.getRenderingDetail().getVersionStatus().equals(CodingSchemeVersionStatus.ACTIVE);
				//System.out.println("\nActive? " + isActive);

				if (isActive != null && isActive.equals(Boolean.TRUE))
				{
					CodingSchemeSummary css = csr.getCodingSchemeSummary();
					String formalname = css.getFormalName();

					 System.out.println("\tformalname: " + formalname);
					 EntityDescription ed = css.getCodingSchemeDescription();
					 System.out.println("\tcodingSchemeDescription: " + ed.getContent());
					 java.lang.String uri = css.getCodingSchemeURI();
					 System.out.println("\tURI: " + uri);
					 java.lang.String localName = css.getLocalName();
					 System.out.println("\tlocalName: " + localName);
					 java.lang.String version = css.getRepresentsVersion();
					 System.out.println("\tversion: " + version + "\n");
					 CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
					 CodingScheme cs = lbSvc.resolveCodingScheme(formalname, versionOrTag);
					 SupportedNamespace[] namespaces = cs.getMappings().getSupportedNamespace();
					 for (int j=0; j<namespaces.length; j++) {
                         SupportedNamespace ns =  namespaces[j];
                         java.lang.String ns_name = ns.getEquivalentCodingScheme();
                         java.lang.String ns_id = ns.getContent() ;
                         System.out.println("\tns_name: " + ns_name + " ns_id:" + ns_id);
                         if (ns_id != null && ns_id.compareTo("") != 0) {
							 hmap.put(ns_id, formalname);
						 }
					 }
			    }
			}
	    } catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return hmap;
	}

	public Vector searchByCode(String scheme, String version, String matchText, String source, String matchAlgorithm) {
		LexBIGService lbs = RemoteServerUtil.createLexBIGService();
		Vector v = new Vector();
		CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
		if (version != null) versionOrTag.setVersion(version);
		CodedNodeSet cns = null;
		try {
			cns = lbs.getNodeSet(scheme, versionOrTag, null);
			if (source != null) cns = restrictToSource(cns, source);
			CodedNodeSet.PropertyType[] propertyTypes = null;
			LocalNameList sourceList = null;
			LocalNameList contextList = null;
			NameAndValueList qualifierList = null;
			cns = cns.restrictToMatchingProperties(ConvenienceMethods.createLocalNameList(new String[]{"conceptCode"}),
					  propertyTypes, sourceList, contextList,
					  qualifierList,matchText, matchAlgorithm, null);

			ResolvedConceptReference[] list = null;
			try {
				list = cns.resolveToList(null, null, null, 500).getResolvedConceptReference();

				for(ResolvedConceptReference ref : list) {
					v.add(ref.getReferencedEntry());
				}
			} catch (Exception ex) {
				System.out.println("WARNING: searchByCode throws exception.");
			}
		} catch (Exception ex) {
			System.out.println("WARNING: searchByCode throws exception.");
		}
        return v;
	}

    private CodedNodeSet union(Vector<CodedNodeSet> cns_vec) {
		if (cns_vec == null) return null;
		if (cns_vec.size() == 0) return null;
		CodedNodeSet cns = cns_vec.elementAt(0);

		if (cns_vec.size() == 1) return cns;
		for (int i=1; i<cns_vec.size(); i++) {
			CodedNodeSet next_cns = cns_vec.elementAt(i);
			try {
				cns = cns.union(next_cns);
			} catch (Exception ex) {
				System.out.println("WARNING: cns.union throws exception.");
			}
		}
		return cns;
	}

/*
    public Vector<org.LexGrid.concepts.Concept> searchByName(Vector schemes, Vector versions, String matchText, String source, String matchAlgorithm, SortOption sortOption, int maxToReturn) {
		String matchText0 = matchText;
		String matchAlgorithm0 = matchAlgorithm;
		matchText0 = matchText0.trim();

System.out.println("matchAlgorithm: " + matchAlgorithm);
System.out.println("matchText0: " + matchText0);

		boolean preprocess = true;
        if (matchText == null || matchText.length() == 0)
        {
			return new Vector();
		}

        matchText = matchText.trim();
        if (matchAlgorithm.compareToIgnoreCase("contains") == 0) //p11.1-q11.1  /100{WBC}
		{
            String delim = ".*";
            if (containsSpecialChars(matchText)) {
				matchText = delim + matchText + delim;
				matchAlgorithm = "RegExp";
		    } else if (matchText.indexOf(" ") != -1) {
				// multiple tokens case:
				matchText = preprocessContains(matchText);
				matchAlgorithm = "RegExp";
			} else if (matchText.indexOf(" ") == -1) {
				// single token case:
				matchText = delim + matchText + delim;
				matchAlgorithm = "RegExp";
			}
		}

		LexBIGService lbSvc = new RemoteServerUtil().createLexBIGService();
		Vector cns_vec = new Vector();
        for (int i=0; i<schemes.size(); i++) {
			CodedNodeSet cns = null;
			ResolvedConceptReferencesIterator iterator = null;

			String scheme = (String) schemes.elementAt(i);
			CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
			String version = (String) versions.elementAt(i);
			if (version != null) versionOrTag.setVersion(version);

System.out.println("scheme: " + scheme);
System.out.println("version: " + version);

			try {
				if (lbSvc == null)
				{
					System.out.println("lbSvc = null");
					return null;
				}

				cns = lbSvc.getNodeSet(scheme, versionOrTag, null);

				if (cns != null)
				{
					try {
						//cns = cns.restrictToMatchingDesignations(matchText, SearchDesignationOption.ALL, matchAlgorithm, null);
						cns = cns.restrictToMatchingDesignations(matchText, null, matchAlgorithm, null);
						cns = restrictToSource(cns, source);
					} catch (Exception ex) {
						//return null;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				//return null;
			}
			if (cns != null) {
System.out.println("cns_vec.add(cns): " + scheme);

				cns_vec.add(cns);
			}
		}

		// find the union of all CNSs

        ResolvedConceptReferencesIterator iterator = null;
        CodedNodeSet cns = union(cns_vec);
        if (cns == null) {
System.out.println("union returns null??? " );

		} else {
System.out.println("union returns NOT NULL " );

			LocalNameList restrictToProperties = new LocalNameList();
			SortOptionList sortCriteria = null;
				//Constructors.createSortOptionList(new String[]{"matchToQuery"});


			try {
				// resolve nothing unless sort_by_pt_only is set to false
				boolean resolveConcepts = false;
				if (sortOption.isApplySortScore() && !sortOption.isSortByPtOnly()) resolveConcepts = true;

				System.out.println("resolveConcepts? " + resolveConcepts);

				try {
					long ms = System.currentTimeMillis(), delay = 0;
					iterator = cns.resolve(sortCriteria, null, restrictToProperties, null, resolveConcepts);
					////Debug.println("cns.resolve delay ---- Run time (ms): " + (delay = System.currentTimeMillis() - ms) + " -- matchAlgorithm " + matchAlgorithm);
					//DBG.debugDetails(delay, "cns.resolve", "searchByName, CodedNodeSet.resolve");

				}  catch (Exception e) {
					System.out.println("ERROR: cns.resolve throws exceptions.");
				}

			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}

            if (iterator == null) {
				System.out.println("cns.resolve returns null???");
			} else {
				System.out.println("cns.resolve returns OK");
			}

			System.out.println("sortOption: " + sortOption);
			System.out.println("apply_sort_score? " + sortOption.isApplySortScore());

			if (sortOption.isApplySortScore())
			{
				long ms = System.currentTimeMillis();
				try {
					if (sortOption.isSortByPtOnly()) {
						iterator = sortByScore(matchText0, iterator, maxToReturn, true, matchAlgorithm0);
					} else {
						iterator = sortByScore(matchText0, iterator, maxToReturn, matchAlgorithm0);
					}

				} catch (Exception ex) {

				}
				////Debug.println("sortByScore delay ---- Run time (ms): " + (System.currentTimeMillis() - ms));
			}

	    }

        Vector v = null;
        if (iterator != null) {
			//testing KLO
			//v = resolveIterator( iterator, maxToReturn, null, sort_by_pt_only);
			long ms = System.currentTimeMillis(), delay = 0;
			v = resolveIterator( iterator, maxToReturn, null, sortOption.isSortByPtOnly());
			////Debug.println("resolveIterator delay ---- Run time (ms): " + (delay = System.currentTimeMillis() - ms));
			//DBG.debugDetails(delay, "resolveIterator", "searchByName");
        }

        if (v == null || v.size() == 0) {
			System.out.println("** No match -- trying matching by code " );

			v = new Vector();
			for (int j=0; j<schemes.size(); j++) {
				String scheme = (String) schemes.elementAt(j);
				CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
				String version = (String) versions.elementAt(j);
				if (version != null) versionOrTag.setVersion(version);

				Vector w = searchByCode(scheme, version, matchText0, source, "LuceneQuery");
				if (w.size() > 0) {
					for (int k=0; k<w.size(); k++) {
						Concept con = (Concept) w.elementAt(k);
						v.add(con);
					}
				}
				if (v.size() > 0) return v;
				boolean searchInactive = true;
				// to be modified -- case insensitive source code match. Is this feasible???
				//Vector u = new SearchUtils().findConceptWithSourceCodeMatching(scheme, version,
				Vector u = findConceptWithSourceCodeMatching(scheme, version,
													  source, matchText0, maxToReturn, searchInactive);
				if (u != null) {
					for (int k=0; k<w.size(); k++) {
						Concept con = (Concept) u.elementAt(k);
						v.add(con);
					}
				}
				if (v.size() > 0) return v;
		    }
	    }

		if(!sortOption.isApplySortScore())
		{
			v = SortUtils.quickSort(v);
		}
        return v;
    }
*/
   public Vector findConceptWithSourceCodeMatching(String scheme, String version,
												   String sourceAbbr, String code,
												   int maxToReturn, boolean searchInactive)
   {
	    if (sourceAbbr == null || code == null) return new Vector();

		LexBIGService lbSvc = new RemoteServerUtil().createLexBIGService();
		CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
		versionOrTag.setVersion(version);

		if (lbSvc == null)
		{
			System.out.println("lbSvc = null");
			return null;
		}

	    LocalNameList contextList = null;
        NameAndValueList qualifierList = null;

 		Vector<String> v = null;

		if (code != null && code.compareTo("") != 0)
		{
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
        if (sourceAbbr.compareTo("*") == 0 || sourceAbbr.compareToIgnoreCase("ALL") == 0)
        {
			sourceLnL = null;
		}

        ResolvedConceptReferencesIterator matchIterator = null;
		SortOptionList sortCriteria = null;//Constructors.createSortOptionList(new String[]{"matchToQuery", "code"});
		try {
			CodedNodeSet cns = lbSvc.getCodingSchemeConcepts(scheme, null);
			if (cns == null)
			{
				System.out.println("lbSvc.getCodingSchemeConceptsd returns null");
				return null;
			}
			CodedNodeSet.PropertyType[] types = new CodedNodeSet.PropertyType[] {CodedNodeSet.PropertyType.PRESENTATION};
			cns = cns.restrictToProperties(propertyLnL, types, sourceLnL, contextList, qualifierList);

            if (cns != null) {
				boolean activeOnly = !searchInactive;
				cns = restrictToActiveStatus(cns, activeOnly);

				try {
					matchIterator = cns.resolve(sortCriteria, null,null);//ConvenienceMethods.createLocalNameList(getPropertyForCodingScheme(cs)),null);
				} catch (Exception ex) {

				}
				if (matchIterator != null) {
					v = resolveIterator(	matchIterator, maxToReturn);
					return v;
				}
		    }

		} catch (Exception e) {
			 //getLogger().error("ERROR: Exception in findConceptWithSourceCodeMatching.");
			 return null;
		}
		return null;
    }

   private CodedNodeSet restrictToActiveStatus(CodedNodeSet cns, boolean activeOnly)
   {
		 if (cns == null) return null;
		 if (!activeOnly) return cns; // no restriction, do nothing
		 try {
		 	 cns = cns.restrictToStatus(CodedNodeSet.ActiveOption.ACTIVE_ONLY, null);
		 	 return cns;
		 } catch (Exception e) {
			 e.printStackTrace();
			 return null;
		 }
   }


    public static CodedNodeSet restrictToSource(CodedNodeSet cns, String source) {
		if (cns == null) return cns;
		if (source == null || source.compareTo("*") == 0 || source.compareTo("") == 0 || source.compareTo("ALL") == 0) return cns;

		LocalNameList contextList = null;
		LocalNameList sourceLnL = null;
		NameAndValueList qualifierList = null;

		Vector<String> w2 = new Vector<String>();
		w2.add(source);
		sourceLnL = vector2LocalNameList(w2);
		LocalNameList propertyLnL = null;
		CodedNodeSet.PropertyType[] types = new CodedNodeSet.PropertyType[] {CodedNodeSet.PropertyType.PRESENTATION};
		try {
			cns = cns.restrictToProperties(propertyLnL, types, sourceLnL, contextList, qualifierList);
		} catch (Exception ex) {
			System.out.println("restrictToSource throws exceptions.");
			return null;
		}
		return cns;
	}

	private static final boolean isInteger(final String s)
	{
		for (int x = 0; x < s.length(); x++) {
			final char c = s.charAt(x);
			if (x == 0 && (c == '-')) continue;  // negative
			if ((c >= '0') && (c <= '9')) continue;  // 0 - 9
			return false; // invalid
		}
		return true; // valid
	}

	private static final boolean containsDigit(final String s)
	{
		for (int x = 0; x < s.length(); x++) {
			final char c = s.charAt(x);
			if (x == 0 && (c == '-')) continue;  // negative
			if ((c >= '0') && (c <= '9')) return true;
		}
		return false; // does not contain digit
	}

    protected String doubleMetaphoneEncode(String word) {
		if (word == null || word.length() == 0) return word;
		//if (isInteger(word)) return word;
		if (containsDigit(word)) return word;

		return doubleMetaphone.encode(word);
	}

    protected float score(String text, List<String> keywords) {
        List<String> wordsToCompare = toScoreWords(text);
        float totalWords = wordsToCompare.size();
        float matchScore = 0;
        float position = 0;
        for (Iterator<String> words = wordsToCompare.listIterator(); words.hasNext(); position++) {
            String word = words.next();
            if (keywords.contains(word))
                matchScore += ((position / 10) + 1);
        }
        return Math.max(0, 100 + (matchScore / totalWords * 100) - (totalWords * 2));
            //Math.max(0, 100 + (matchScore / totalWords * 100) - (totalWords * 2))
                //* (isPreferred ? 2 : 1);
    }

	public static void main(String[] args) {
		String url = "http://lexevsapi.nci.nih.gov/lexevsapi42";
		if (args.length == 1) {
			url = args[0];
			System.out.println(url);
		}

		SearchUtils test = new SearchUtils(url);
		//test.testSearchByName();
	}

}
