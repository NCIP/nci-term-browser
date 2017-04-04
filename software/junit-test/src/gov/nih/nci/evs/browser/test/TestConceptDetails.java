package gov.nih.nci.evs.browser.test;


import gov.nih.nci.evs.browser.common.*;
import gov.nih.nci.evs.browser.utils.*;
import gov.nih.nci.evs.security.SecurityToken;
import gov.nih.nci.evs.testUtil.*;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.client.ApplicationServiceProvider;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.Enumeration;
import java.util.Map.Entry;
import org.apache.commons.lang.*;
import org.apache.log4j.*;
import org.junit.*;
import org.LexGrid.codingSchemes.*;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.commonTypes.EntityDescription;
import org.LexGrid.commonTypes.Property;
import org.LexGrid.commonTypes.PropertyQualifier;
import org.LexGrid.commonTypes.Source;
import org.LexGrid.concepts.*;
import org.LexGrid.concepts.Comment;
import org.LexGrid.concepts.Definition;
import org.LexGrid.concepts.Entity;
import org.LexGrid.concepts.Presentation;
import org.LexGrid.LexBIG.caCore.interfaces.LexEVSApplicationService;
import org.LexGrid.LexBIG.caCore.interfaces.LexEVSDistributed;
import org.LexGrid.LexBIG.DataModel.Collections.AssociationList;
import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeTagList;
import org.LexGrid.LexBIG.DataModel.Collections.ConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;
import org.LexGrid.LexBIG.DataModel.Collections.ModuleDescriptionList;
import org.LexGrid.LexBIG.DataModel.Collections.NameAndValueList;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Collections.SortOptionList;
import org.LexGrid.LexBIG.DataModel.Core.*;
import org.LexGrid.LexBIG.DataModel.Core.AssociatedConcept;
import org.LexGrid.LexBIG.DataModel.Core.Association;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.ConceptReference;
import org.LexGrid.LexBIG.DataModel.Core.NameAndValue;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.descriptors.RenderingDetailDescriptor;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.ModuleDescription;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.RenderingDetail;
import org.LexGrid.LexBIG.Exceptions.*;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Extensions.Generic.*;
import org.LexGrid.LexBIG.Extensions.Generic.LexBIGServiceConvenienceMethods.*;
import org.LexGrid.LexBIG.Extensions.Generic.LexBIGServiceConvenienceMethods;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.Direction;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.MappingSortOption;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.MappingSortOptionName;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.QualifierSortOption;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.ActiveOption;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.ConvenienceMethods;
import org.LexGrid.LexBIG.Utility.Iterators.*;
import org.LexGrid.naming.*;
import org.LexGrid.naming.SupportedProperty;


/**
 * <!-- LICENSE_TEXT_START -->
 * Copyright 2008-2015 NGIT. This software was developed in conjunction
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
 * Modification history:
 *     Initial implementation kim.ong@ngc.com
 *
 */


public class TestConceptDetails extends ServiceTestCase {
    final static String testID = "TestConceptDetails";
	public String[] NCIM_CODE_PROPERTYIES = new String[] {"NCI_META_CUI", "UMLS_CUI"};

	LexBIGService lbSvc = null;
	CodingSchemeDataUtils codingSchemeDataUtils = null;
    private	Vector csVec = null;
	TreeUtils treeUtils = null;
	TestCaseGenerator testCaseGenerator = null;
	int NUMBER_OF_TEST_CASES = 3;

	LexBIGServiceConvenienceMethods lbscm = null;
	RelationshipUtils relationshipUtils = null;
	gov.nih.nci.evs.browser.utils.StringUtils stringUtils = null;
	ConceptDetails conceptDetails = null;

    //@Before
	public void setUp(){
		lbSvc = LexEVSServiceUtil.getLexBIGService();
		codingSchemeDataUtils = new CodingSchemeDataUtils(lbSvc);
		treeUtils = new TreeUtils(lbSvc);
		testCaseGenerator = new TestCaseGenerator(lbSvc);
		conceptDetails = new ConceptDetails(lbSvc);

		relationshipUtils = new RelationshipUtils(lbSvc);
		stringUtils = new gov.nih.nci.evs.browser.utils.StringUtils();
		try {
	        lbscm = (LexBIGServiceConvenienceMethods) lbSvc.getGenericExtension("LexBIGServiceConvenienceMethods");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setNumberOfTestCases(int number) {
		this.NUMBER_OF_TEST_CASES = number;
	}

    @Override
    protected String getTestID() {
        return testID;
    }

    @Test
    public void testConceptDetails() throws Exception {
		String codingScheme = gov.nih.nci.evs.browser.common.Constants.NCIT_CS_NAME;
		String version = codingSchemeDataUtils.getVocabularyVersionByTag(codingScheme, "PRODUCTION");
;
		int number = 1;
		int m = 0;

		long ms = System.currentTimeMillis();
	    ResolvedConceptReferenceList rcrl = testCaseGenerator.generateResolvedConceptReferences(codingScheme, version, number);
		System.out.println("generateResolvedConceptReferences run time (ms): " + (System.currentTimeMillis() - ms));
		ResolvedConceptReference rcr = rcrl.getResolvedConceptReference(0);

		String name = rcr.getEntityDescription().getContent();
		String code = rcr.getCode();

        List<String> list = getDistinctNamespacesOfCode(
             codingScheme,
             version,
             code);
        if (list.size() == 0) m++;

        String namespace = "";
        System.out.println(codingScheme + "(" + version + ")");
        System.out.println("\t" + name + " (" + code + ")");
        for (int i=0; i<list.size(); i++) {
			String ns = (String) list.get(i);
			System.out.println("\tnamespace: " + ns);
			namespace = ns;
		}

		boolean use_ns = true;
		Entity concept = getConceptByCode(codingScheme, version, code, namespace, use_ns);
		if (concept == null) {
			System.out.println("Concept " + code + " not found in " + codingScheme);
			m++;
		} else {
			Vector v = conceptDetails.getSynonyms(codingScheme, concept);
			if (v == null) {
				m++;
			} else {
				v = new SortUtils().quickSort(v);
				for (int j=0; j<v.size(); j++) {
					String s = (String) v.elementAt(j);
					System.out.println("\t" + s);
				}
			}
	    }

		assertTrue(m == 0);

    }

    public ConceptReferenceList createConceptReferenceList(
        String[] codes, String codingSchemeName, String ns) {
        if (codes == null) {
            return null;
        }
        ConceptReferenceList list = new ConceptReferenceList();
        for (int i = 0; i < codes.length; i++) {
            ConceptReference cr = new ConceptReference();
            cr.setCodingSchemeName(codingSchemeName);
            cr.setConceptCode(codes[i]);
            if (ns != null) {
				cr.setCodeNamespace(ns);
			}

            list.addConceptReference(cr);
        }
        return list;
    }

    public List<String> getDistinctNamespacesOfCode(
            String codingScheme,
            String version,
            String code) {

        try {
            CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
            csvt.setVersion(version);

            List<String> list = lbscm.getDistinctNamespacesOfCode(codingScheme, csvt, code);
            return list;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
        return null;
	}


    public boolean isMapping(String scheme, String version) {
        CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
        if (version != null)
            csvt.setVersion(version);

		try {
			LexBIGService distributed = lbSvc;
			MappingExtension mappingExtension = (MappingExtension)
				distributed.getGenericExtension("MappingExtension");

            boolean isMappingCS = mappingExtension.isMappingCodingScheme(scheme, csvt);
			return isMappingCS;

		} catch (Exception ex) {
            return false;
        }
    }

    public ConceptReferenceList createConceptReferenceList(
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

    public ConceptReferenceList createConceptReferenceList(Vector code_vec,
        String codingSchemeName) {
        if (code_vec == null) {
            return null;
        }
        ConceptReferenceList list = new ConceptReferenceList();
        for (int i = 0; i < code_vec.size(); i++) {
            String code = (String) code_vec.elementAt(i);
            ConceptReference cr = new ConceptReference();
            cr.setCodingSchemeName(codingSchemeName);
            cr.setConceptCode(code);
            list.addConceptReference(cr);
        }
        return list;
    }

    public CodedNodeSet getNodeSet(String scheme, CodingSchemeVersionOrTag versionOrTag) throws Exception {
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

    public Entity getConceptByCode(String codingSchemeName, String vers, String code, String ns, boolean use_ns) {
        try {
			if (code == null) {
				//System.out.println("Input error in DataUtils.getConceptByCode -- code is null.");
				return null;
			}
			if (code.indexOf("@") != -1) return null; // anonymous class
            CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
            if (vers != null) versionOrTag.setVersion(vers);

            ConceptReferenceList crefs = null;
            if (use_ns) {
                 crefs = createConceptReferenceList(new String[] { code }, codingSchemeName, ns);
			} else {
				 crefs = createConceptReferenceList(new String[] { code }, codingSchemeName);
			}

            CodedNodeSet cns = null;
            try {
				try {
					cns = getNodeSet(codingSchemeName, versionOrTag);

				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}

                if (cns == null) {
					return null;
				}

                cns = cns.restrictToCodes(crefs);
 				ResolvedConceptReferenceList matches = null;
				try {
					matches = cns.resolveToList(null, null, null, 1);
				} catch (Exception e) {
					e.printStackTrace();
				}

                if (matches == null) {
                    //System.out.println("Concept not found.");
                    return null;
                }
                int count = matches.getResolvedConceptReferenceCount();
                // Analyze the result ...
                if (count == 0)
                    return null;
                if (count > 0) {
                    try {
                        ResolvedConceptReference ref = (ResolvedConceptReference) matches
                                .enumerateResolvedConceptReference()
                                .nextElement();
                        Entity entry = ref.getReferencedEntry();
                        return entry;
                    } catch (Exception ex1) {
                        ex1.printStackTrace();
                        return null;
                    }
                }
            } catch (Exception e1) {
                e1.printStackTrace();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public Entity getConceptByCode(String codingSchemeName, String vers, String code, String ns) {
        return getConceptByCode(codingSchemeName, vers, code, ns, false);
	}

    public String encodeTerm(String s) {
		if (s == null) return null;
		//if (stringUtils.isAlphanumeric(s)) return s;

        StringBuilder buf = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
			if (Character.isLetterOrDigit(c)) {
                buf.append(c);
            } else {
                buf.append("&#").append((int) c).append(";");
            }
        }
        return buf.toString();
    }

    public Vector getPresentationProperties(Entity concept) {
        Vector v = new Vector();
        org.LexGrid.commonTypes.Property[] properties =
            concept.getPresentation();
        // name$value$isPreferred

        if (properties == null || properties.length == 0)
            return v;
        for (int i = 0; i < properties.length; i++) {
            Presentation p = (Presentation) properties[i];
            String name = p.getPropertyName();
            String value = p.getValue().getContent();
            String isPreferred = "false";
            if (p.getIsPreferred() != null) {
                isPreferred = p.getIsPreferred().toString();
			}
            String t = name + "$" + value + "$" + isPreferred;
            v.add(t);
        }
        return v;
    }

    public Entity getConceptWithProperty(String scheme, String version,
        String code, String propertyName) {
        try {

            CodingSchemeVersionOrTag versionOrTag =
                new CodingSchemeVersionOrTag();
            if (version != null) versionOrTag.setVersion(version);

            ConceptReferenceList crefs =
                createConceptReferenceList(new String[] { code }, scheme);
            CodedNodeSet cns = null;

            try {
                cns = lbSvc.getCodingSchemeConcepts(scheme, versionOrTag);
            } catch (Exception e1) {
                e1.printStackTrace();
                return null;
            }

            //cns = cns.restrictToCodes(crefs);

            try {
				cns = cns.restrictToCodes(crefs);

                LocalNameList propertyNames = new LocalNameList();
                if (propertyName != null) propertyNames.addEntry(propertyName);
                CodedNodeSet.PropertyType[] propertyTypes = null;

                //long ms = System.currentTimeMillis(), delay = 0;
                SortOptionList sortOptions = null;
                LocalNameList filterOptions = null;
                boolean resolveObjects = true; // needs to be set to true
                int maxToReturn = 1000;

                ResolvedConceptReferenceList rcrl =
                    cns.resolveToList(sortOptions, filterOptions,
                        propertyNames, propertyTypes, resolveObjects,
                        maxToReturn);

                //HashMap hmap = new HashMap();
                if (rcrl == null) {
                    ////_logger.warn("Concep not found.");
                    return null;
                }

                if (rcrl.getResolvedConceptReferenceCount() > 0) {
                    // ResolvedConceptReference[] list =
                    // rcrl.getResolvedConceptReference();
                    for (int i = 0; i < rcrl.getResolvedConceptReferenceCount(); i++) {
                        ResolvedConceptReference rcr =
                            rcrl.getResolvedConceptReference(i);
                        Entity c = rcr.getReferencedEntry();
                        return c;
                    }
                }

                return null;

            } catch (Exception e) {
                //_logger.error("Method: SearchUtil.getConceptWithProperty");
                //_logger.error("* ERROR: getConceptWithProperty throws exceptions.");
                //_logger.error("* " + e.getClass().getSimpleName() + ": "
                //    + e.getMessage());
                //e.printStackTrace();
            }
        } catch (Exception ex) {
                //_logger.error("Method: SearchUtil.getConceptWithProperty");
                //_logger.error("* ERROR: getConceptWithProperty throws exceptions.");
                //_logger.error("* " + ex.getClass().getSimpleName() + ": "
                //    + ex.getMessage());

        }
        return null;
    }


    public Vector getConceptPropertyValues(Entity c, String propertyName) {
        if (c == null)
            return null;
        Vector v = new Vector();
        Property[] properties = c.getProperty();

        for (int j = 0; j < properties.length; j++) {
            Property prop = properties[j];

            if (prop.getPropertyName().compareTo(propertyName) == 0) {
                v.add(prop.getValue().getContent());
            }
        }
        return v;
    }

    public String convertToCommaSeparatedValue(Vector v) {
        if (v == null)
            return null;
        String s = "";
        if (v.size() == 0)
            return s;

        StringBuffer buf = new StringBuffer();
        buf.append((String) v.elementAt(0));

        //s = (String) v.elementAt(0);
        for (int i = 1; i < v.size(); i++) {
            String next = (String) v.elementAt(i);
            //s = s + "; " + next;
            buf.append("; " + next);
        }
        return buf.toString();
        //return s;
    }

    public String getConceptStatus(String scheme, String version,
        String ltag, String code) {
        boolean conceptStatusSupported = false;
        /*
        if (DataUtils.getVocabulariesWithConceptStatusHashSet().contains(scheme))
            conceptStatusSupported = true;
        if (!conceptStatusSupported)
            return null;
        */

        Entity c = getConceptWithProperty(scheme, version, code, "Concept_Status");
        String con_status = null;
        if (c != null) {
            Vector status_vec = getConceptPropertyValues(c, "Concept_Status");
            if (status_vec == null || status_vec.size() == 0) {
                con_status = c.getStatus();
            } else {
                con_status = convertToCommaSeparatedValue(status_vec);
            }
            return con_status;
        }
        return null;
    }

    public Vector<SupportedProperty> getSupportedProperties(
        CodingScheme cs) {
        if (cs == null)
            return null;
        Vector<SupportedProperty> v = new Vector<SupportedProperty>();
        SupportedProperty[] properties =
            cs.getMappings().getSupportedProperty();
        for (int i = 0; i < properties.length; i++) {
            SupportedProperty sp = (SupportedProperty) properties[i];
            v.add(sp);
        }
        return v;
    }

    public Vector<String> getSupportedPropertyNames(CodingScheme cs) {
        Vector w = getSupportedProperties(cs);
        if (w == null)
            return null;

        Vector<String> v = new Vector<String>();
        for (int i = 0; i < w.size(); i++) {
            SupportedProperty sp = (SupportedProperty) w.elementAt(i);
            v.add(sp.getLocalId());
        }
        return v;
    }


    public Vector<String> getSupportedPropertyNames(String codingScheme,
        String version) {
        CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
        if (version != null)
            versionOrTag.setVersion(version);
        try {
            CodingScheme cs = getCodingScheme(codingScheme, versionOrTag);
            return getSupportedPropertyNames(cs);
        } catch (Exception ex) {
        }
        return null;
    }

    public Vector getPropertyValues(Entity concept,
        String property_type, String property_name) {

		if (concept	== null || property_type == null || property_name == null) return null;
        Vector v = new Vector();
        org.LexGrid.commonTypes.Property[] properties = null;

        boolean addQualifiers = false;
        if (property_type.compareToIgnoreCase("GENERIC") == 0) {
            properties = concept.getProperty();
            addQualifiers = true;
        } else if (property_type.compareToIgnoreCase("PRESENTATION") == 0) {
            properties = concept.getPresentation();
        } else if (property_type.compareToIgnoreCase("COMMENT") == 0) {
            properties = concept.getComment();
            addQualifiers = true;
        } else if (property_type.compareToIgnoreCase("DEFINITION") == 0) {
            properties = concept.getDefinition();
        }

        if (properties == null || properties.length == 0)
            return v;
        for (int i = 0; i < properties.length; i++) {
            Property p = (Property) properties[i];
            if (property_name.compareTo(p.getPropertyName()) == 0) {
                String t = p.getValue().getContent();

                // #27034
                if (addQualifiers) {
                    String qualifiers = getPropertyQualfierValues(p);
                    if (qualifiers.compareTo("") != 0) {
                        t = t + " (" + getPropertyQualfierValues(p) + ")";
                    }
                }

                Source[] sources = p.getSource();
                if (sources != null && sources.length > 0) {
                    Source src = sources[0];
                    t = t + "|" + src.getContent();
                }

                v.add(t);
            }
        }
        return v;
    }

    public String getPropertyQualfierValues(
        org.LexGrid.commonTypes.Property p) {

        StringBuffer buf = new StringBuffer();
        String s = "";

        PropertyQualifier[] qualifiers = p.getPropertyQualifier();
        if (qualifiers != null && qualifiers.length > 0) {
            for (int j = 0; j < qualifiers.length; j++) {
                PropertyQualifier q = qualifiers[j];
                String qualifier_name = q.getPropertyQualifierName();
                //KLO, 110910
                if (qualifier_name.compareTo("label") != 0) {
					String qualifier_value = q.getValue().getContent();

					//s = s + qualifier_name + ": " + qualifier_value;
					buf.append(qualifier_name + ": " + qualifier_value);
					if (j < qualifiers.length - 1)
						//s = s + "; ";
						buf.append("; ");
				}
            }
        }
        s = buf.toString();
        return s;
    }

    public CodingScheme getCodingScheme(String codingScheme,
        CodingSchemeVersionOrTag versionOrTag) throws LBException {

        CodingScheme cs = null;
        try {
            //LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
            cs = lbSvc.resolveCodingScheme(codingScheme, versionOrTag);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cs;
    }

    public Vector getNCImCodes(String scheme, String version, String code) {
        Vector w = new Vector();
        CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
        if (version != null) {
			csvt.setVersion(version);
		}
		try {

			ConceptReferenceList crefs = ConvenienceMethods.createConceptReferenceList(new String[] { code }, scheme);
			CodedNodeSet cns = lbSvc.getCodingSchemeConcepts(scheme, csvt);

			if (cns == null) {
				return null;
			}
			cns = cns.restrictToStatus(ActiveOption.ALL, null);
			cns = cns.restrictToCodes(crefs);
			ResolvedConceptReferenceList matches = cns.resolveToList(null, null, null, 1);
			if (matches.getResolvedConceptReferenceCount() > 0) {
				ResolvedConceptReference ref = (ResolvedConceptReference) matches.enumerateResolvedConceptReference()
						.nextElement();
				Entity node = ref.getEntity();
				return getNCImCodes(node);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return w;
    }

    public Vector getNCImCodes(Entity node) {
		if (node == null) return null;
        Vector w = new Vector();
		Property[] props = node.getAllProperties();
		for (int i = 0; i < props.length; i++) {
			 Property prop = props[i];
			 PropertyQualifier[] qualifiers = prop.getPropertyQualifier();
			 for (int k=0; k<qualifiers.length; k++) {
				  PropertyQualifier qualifier = qualifiers[k];
			 }
			 Source[] sources = prop.getSource();
			 for (int k=0; k<sources.length; k++) {
				  Source source = sources[k];
			 }
			 if (Arrays.asList(NCIM_CODE_PROPERTYIES).contains(prop.getPropertyName())) {
				 if (!w.contains(prop.getValue().getContent())) {
					w.add(prop.getValue().getContent());
				 }
			 }
		}
		return w;
    }

    public Vector getConceptStatusByConceptCodes(String scheme,
        String version, String ltag, Vector code_vec) {
        boolean conceptStatusSupported = false;
        //if (DataUtils.getVocabulariesWithConceptStatusHashSet().contains(scheme))
        //    conceptStatusSupported = true;

        Vector w = new Vector();
        long ms = System.currentTimeMillis();
        for (int i = 0; i < code_vec.size(); i++) {
            if (conceptStatusSupported) {
                String code = (String) code_vec.elementAt(i);
                Entity c =
                    getConceptWithProperty(scheme, version, code,
                        "Concept_Status");
                String con_status = null;
                if (c != null) {
                    Vector status_vec =
                        getConceptPropertyValues(c, "Concept_Status");
                    if (status_vec == null || status_vec.size() == 0) {
                        con_status = c.getStatus();
                    } else {
                        con_status =
                            convertToCommaSeparatedValue(status_vec);
                    }
                    w.add(con_status);
                } else {
                    w.add(null);
                }
            } else {
                w.add(null);
            }
        }
        //_logger.debug("getConceptStatusByConceptCodes Run time (ms): "
        //    + (System.currentTimeMillis() - ms) + " number of concepts: "
        //    + code_vec.size());
        return w;
    }


	public void main(String [] args) {
		try {
			TestConceptDetails test = new TestConceptDetails();
		} catch (Exception ex) {

		}
	}
}

