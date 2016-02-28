package gov.nih.nci.evs.browser.utils;

import java.util.*;
import java.io.*;
import java.util.Map.Entry;
import gov.nih.nci.evs.browser.bean.*;

import org.LexGrid.concepts.*;
import org.LexGrid.LexBIG.Utility.Iterators.*;
import org.LexGrid.LexBIG.DataModel.Core.*;

import java.util.Enumeration;
import org.LexGrid.LexBIG.DataModel.Collections.AssociationList;
import org.LexGrid.LexBIG.DataModel.Collections.ConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.AssociatedConcept;
import org.LexGrid.LexBIG.DataModel.Core.Association;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.NameAndValue;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.ActiveOption;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.ConvenienceMethods;
import org.LexGrid.LexBIG.caCore.interfaces.LexEVSApplicationService;
import org.LexGrid.commonTypes.EntityDescription;
import org.LexGrid.commonTypes.Property;
import org.LexGrid.commonTypes.PropertyQualifier;
import org.LexGrid.commonTypes.Source;
import org.LexGrid.concepts.Comment;
import org.LexGrid.concepts.Definition;
import org.LexGrid.concepts.Entity;
import org.LexGrid.concepts.Presentation;

import org.apache.log4j.*;
import gov.nih.nci.evs.browser.common.Constants;
import org.LexGrid.codingSchemes.*;
import org.LexGrid.naming.SupportedProperty;
import org.LexGrid.LexBIG.DataModel.Collections.SortOptionList;
import org.apache.commons.lang.*;

import org.LexGrid.LexBIG.Extensions.Generic.LexBIGServiceConvenienceMethods;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.RenderingDetail;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.descriptors.RenderingDetailDescriptor;
import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeTagList;
import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.DataModel.Collections.NameAndValueList;
import org.LexGrid.LexBIG.DataModel.Core.NameAndValue;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.lexevs.property.PropertyExtension;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeGraph;


public class ConceptDetails {
	private int _maxReturn = -1;

    private static Logger _logger = Logger.getLogger(ConceptDetails.class);
    private LexBIGService lbSvc = null;
    private LexBIGServiceConvenienceMethods lbscm = null;

    public ConceptDetails() {

	}

	public ConceptDetails(LexBIGService lbSvc) {
        this.lbSvc = lbSvc;
        try {
            lbscm =
                (LexBIGServiceConvenienceMethods) lbSvc
                    .getGenericExtension("LexBIGServiceConvenienceMethods");
            lbscm.setLexBIGService(lbSvc);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
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

    public CodedNodeSet getNodeSet(String scheme, String version) throws Exception {
		CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
		if (version != null) versionOrTag.setVersion(version);
		return getNodeSet(scheme, versionOrTag);
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


    public Entity getConceptByCode(String codingSchemeName, String vers, String code) {

        try {
			if (code == null) {
				return null;
			}
			if (code.indexOf("@") != -1) return null; // anonymous class
            CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
            if (vers != null) versionOrTag.setVersion(vers);

            ConceptReferenceList crefs = createConceptReferenceList(
                    new String[] { code }, codingSchemeName);

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

    public String encodeTerm(String s) {
		if (s == null) return null;
		if (gov.nih.nci.evs.browser.utils.StringUtils.isAlphanumeric(s)) return s;

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

    public Vector getPresentationProperties(String codingSchemeName, String vers, String code) {
		Entity concept = getConceptByCode(codingSchemeName, vers, code);
		if (concept == null) return null;
		return getPresentationProperties(concept);
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
        return SortUtils.quickSort(v);
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
                    _logger.warn("Concep not found.");
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
                _logger.error("Method: SearchUtil.getConceptWithProperty");
                _logger.error("* ERROR: getConceptWithProperty throws exceptions.");
                _logger.error("* " + e.getClass().getSimpleName() + ": "
                    + e.getMessage());
                //e.printStackTrace();
            }
        } catch (Exception ex) {
                _logger.error("Method: SearchUtil.getConceptWithProperty");
                _logger.error("* ERROR: getConceptWithProperty throws exceptions.");
                _logger.error("* " + ex.getClass().getSimpleName() + ": "
                    + ex.getMessage());

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

    public String getConceptStatus(String scheme, String version,
        String ltag, String code) {
        Entity c =
            getConceptWithProperty(scheme, version, code, "Concept_Status");
        String con_status = null;
        if (c != null) {
            Vector status_vec = getConceptPropertyValues(c, "Concept_Status");
            if (status_vec == null || status_vec.size() == 0) {
                con_status = c.getStatus();
            } else {
                con_status = gov.nih.nci.evs.browser.utils.StringUtils.convertToCommaSeparatedValue(status_vec);
            }
            return con_status;
        }
        return null;
    }

    public Vector<String> getSupportedPropertyNames(String codingScheme,
        String version) {
        try {
            CodingScheme cs = new CodingSchemeDataUtils(lbSvc).resolveCodingScheme(codingScheme, version);
            return getSupportedPropertyNames(cs);
        } catch (Exception ex) {
        }
        return null;
    }

    public Vector getPropertyValues(Entity concept, String property_type, String property_name) {
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

    public String getPropertyQualfierValues(org.LexGrid.commonTypes.Property p) {
        StringBuffer buf = new StringBuffer();
        String s = "";
        PropertyQualifier[] qualifiers = p.getPropertyQualifier();
        if (qualifiers != null && qualifiers.length > 0) {
            for (int j = 0; j < qualifiers.length; j++) {
                PropertyQualifier q = qualifiers[j];
                String qualifier_name = q.getPropertyQualifierName();
                if (qualifier_name.compareTo("label") != 0) {
					String qualifier_value = q.getValue().getContent();
					buf.append(qualifier_name + ": " + qualifier_value);
					if (j < qualifiers.length - 1) {
						buf.append("; ");
					}
				}
            }
        }
        s = buf.toString();
        return s;
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
			 if (Arrays.asList(Constants.NCIM_CODE_PROPERTYIES).contains(prop.getPropertyName())) {
				 if (!w.contains(prop.getValue().getContent())) {
					w.add(prop.getValue().getContent());
				 }
			 }
		}
		return w;
    }

    public Vector getPropertyNamesByType(Entity concept,
        String property_type) {
        Vector v = new Vector();
        org.LexGrid.commonTypes.Property[] properties = null;

        if (property_type.compareToIgnoreCase("GENERIC") == 0) {
            properties = concept.getProperty();
        } else if (property_type.compareToIgnoreCase("PRESENTATION") == 0) {
            properties = concept.getPresentation();
        } else if (property_type.compareToIgnoreCase("COMMENT") == 0) {
            properties = concept.getComment();
        } else if (property_type.compareToIgnoreCase("DEFINITION") == 0) {
            properties = concept.getDefinition();
        }
        if (properties == null || properties.length == 0)
            return v;
        for (int i = 0; i < properties.length; i++) {
            Property p = (Property) properties[i];
            // v.add(p.getText().getContent());
            v.add(p.getPropertyName());
        }
        return v;
    }

    public Vector getConceptStatusByConceptCodes(String scheme,
        String version, String ltag, Vector code_vec) {
        boolean conceptStatusSupported = false;

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
                            gov.nih.nci.evs.browser.utils.StringUtils.convertToCommaSeparatedValue(status_vec);
                    }
                    w.add(con_status);
                } else {
                    w.add(null);
                }
            } else {
                w.add(null);
            }
        }
        _logger.debug("getConceptStatusByConceptCodes Run time (ms): "
            + (System.currentTimeMillis() - ms) + " number of concepts: "
            + code_vec.size());
        return w;
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

    public String getVocabularyVersionByTag(String codingSchemeName,
        String ltag) {

		if (codingSchemeName == null) {
			codingSchemeName = "NCI Thesaurus";
		}

        String version = null;
        int knt = 0;
        try {
            CodingSchemeRenderingList lcsrl = lbSvc.getSupportedCodingSchemes();
            CodingSchemeRendering[] csra = lcsrl.getCodingSchemeRendering();
            for (int i = 0; i < csra.length; i++) {
                CodingSchemeRendering csr = csra[i];
                CodingSchemeSummary css = csr.getCodingSchemeSummary();
                if (css.getFormalName().compareTo(codingSchemeName) == 0
                    || css.getLocalName().compareTo(codingSchemeName) == 0
                    || css.getCodingSchemeURI().compareTo(codingSchemeName) == 0) {
					version = css.getRepresentsVersion();
                    knt++;

                    if (ltag == null)
                        return version;
                    RenderingDetail rd = csr.getRenderingDetail();
                    CodingSchemeTagList cstl = rd.getVersionTags();
                    java.lang.String[] tags = cstl.getTag();
                    if (tags == null)
                        return version;
					if (tags.length > 0) {
                        for (int j = 0; j < tags.length; j++) {
                            String version_tag = (String) tags[j];

                            if (version_tag != null && version_tag.compareToIgnoreCase(ltag) == 0) {
                                return version;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
       if (ltag != null && ltag.compareToIgnoreCase(Constants.PRODUCTION) == 0
            & knt == 1) {
            return version;
        }
        return null;
    }

    public String getNamespaceByCode(String codingSchemeName, String vers, String code) {
        try {
			if (code == null) {
				return null;
			}
			if (code.indexOf("@") != -1) return null; // anonymous class
            CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
            if (vers != null) versionOrTag.setVersion(vers);

            ConceptReferenceList crefs = createConceptReferenceList(
                    new String[] { code }, codingSchemeName);

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
					System.out.println("DataUtils getNamespaceByCode --  no match.");

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

                        return ref.getCodeNamespace();
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

    public static NameAndValueList createNameAndValueList(String[] names, String[] values) {
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

    public static NameAndValueList createNameAndValueList(Vector names, Vector values) {
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

    public String getPreferredName(Entity c) {
        Presentation[] presentations = c.getPresentation();
        for (int i = 0; i < presentations.length; i++) {
            Presentation p = presentations[i];
            if (p.getPropertyName().compareTo("Preferred_Name") == 0) {
                return p.getValue().getContent();
            }
        }
        return null;
    }

    public HashMap getPropertyValues(String scheme, String version, String propertyType, String propertyName) {
		HashMap hmap = new HashMap();
		CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
		if (version != null) versionOrTag.setVersion(version);
		try {
			CodedNodeSet cns = getNodeSet(scheme, versionOrTag);
			SortOptionList sortOptions = null;
			LocalNameList filterOptions = null;
			LocalNameList propertyNames = Constructors.createLocalNameList(propertyName);
			CodedNodeSet.PropertyType[] propertyTypes = null;
			boolean resolveObjects = true;

			ResolvedConceptReferencesIterator iterator = cns.resolve(sortOptions, filterOptions, propertyNames,
				propertyTypes, resolveObjects);
			while (iterator != null && iterator.hasNext()) {
				ResolvedConceptReference rcr = iterator.next();
				Entity concept = rcr.getEntity();
    			Vector v = getPropertyValues(concept, propertyType, propertyName);
    			if (v != null) {
					if (v.size() > 0) {
						String key = concept.getEntityCode();
						String value = (String) v.elementAt(0);
						hmap.put(key, value);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return hmap;
	}

    public Vector getAssociationSourceCodes(String scheme, String version,
        String code, String assocName) {
        CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
        if (version != null)
            csvt.setVersion(version);
        ResolvedConceptReferenceList matches = null;
        Vector v = new Vector();
        try {
            CodedNodeGraph cng = lbSvc.getNodeGraph(scheme, csvt, null);
            Boolean restrictToAnonymous = Boolean.FALSE;
            cng = cng.restrictToAnonymous(restrictToAnonymous);
            NameAndValueList nameAndValueList =
                createNameAndValueList(new String[] { assocName }, null);

            NameAndValueList nameAndValueList_qualifier = null;
            cng =
                cng.restrictToAssociations(nameAndValueList,
                    nameAndValueList_qualifier);

            matches =
                cng.resolveAsList(ConvenienceMethods.createConceptReference(
                    code, scheme), false, true, 1, 1, new LocalNameList(),
                    null, null, _maxReturn);

            if (matches.getResolvedConceptReferenceCount() > 0) {
                java.util.Enumeration<? extends ResolvedConceptReference> refEnum = matches.enumerateResolvedConceptReference();
                while (refEnum.hasMoreElements()) {
                    ResolvedConceptReference ref = (ResolvedConceptReference) refEnum.nextElement();

                    AssociationList targetof = ref.getTargetOf();
                    Association[] associations = targetof.getAssociation();

                    for (int i = 0; i < associations.length; i++) {
                        Association assoc = associations[i];
                        AssociatedConcept[] acl =
                            assoc.getAssociatedConcepts()
                                .getAssociatedConcept();
                        for (int j = 0; j < acl.length; j++) {
                            AssociatedConcept ac = acl[j];
                            v.add(ac.getReferencedEntry().getEntityCode());
                        }
                    }
                }
                SortUtils.quickSort(v);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return v;
    }

    public Vector getSynonyms(String scheme, String version, String tag, String code) {
        Entity concept = getConceptByCode(scheme, version, code);
        return getSynonyms(scheme, concept);
    }

    public Vector getSynonyms(String scheme, Entity concept) {
        if (concept == null)
            return null;
        Vector v = new Vector();
        Presentation[] properties = concept.getPresentation();
        int n = 0;
        boolean inclusion = true;
        for (int i = 0; i < properties.length; i++) {
            Presentation p = properties[i];
            // for NCI Thesaurus or Pre-NCI Thesaurus, show FULL_SYNs only
            if (scheme != null && (scheme.indexOf("NCI_Thesaurus") != -1 || scheme.indexOf("NCI Thesaurus") != -1)) {
                inclusion = false;
                if (p.getPropertyName().compareTo("FULL_SYN") == 0) {
                    inclusion = true;
                }
            }
            if (inclusion) {
                String term_name = p.getValue().getContent();
                String term_type = "null";
                String term_source = "null";
                String term_source_code = "null";
                String term_subsource = "null";

                PropertyQualifier[] qualifiers = p.getPropertyQualifier();
                if (qualifiers != null) {
                    for (int j = 0; j < qualifiers.length; j++) {
                        PropertyQualifier q = qualifiers[j];
                        String qualifier_name = q.getPropertyQualifierName();
                        String qualifier_value = q.getValue().getContent();
                        if (qualifier_name.compareTo("source-code") == 0) {
                            term_source_code = qualifier_value;
                        }
                        if (qualifier_name.compareTo("subsource-name") == 0) {
                            term_subsource = qualifier_value;
                        }
                    }
                }
                term_type = p.getRepresentationalForm();
                Source[] sources = p.getSource();
                if (sources != null && sources.length > 0) {
                    Source src = sources[0];
                    term_source = src.getContent();
                }
                v.add(term_name + "|" + term_type + "|" + term_source + "|"
                    + term_source_code + "|" +  term_subsource);
            }
        }
        SortUtils.quickSort(v);
        return v;
    }




    public HashMap getPropertyValuesForCodes(String scheme, String version,
        Vector codes, String propertyName) {
        try {
            CodingSchemeVersionOrTag versionOrTag =
                new CodingSchemeVersionOrTag();
            versionOrTag.setVersion(version);

            ConceptReferenceList crefs =
                createConceptReferenceList(codes, scheme);

            CodedNodeSet cns = null;

            try {
                cns = lbSvc.getCodingSchemeConcepts(scheme, versionOrTag);
                if (cns == null) return null;
            } catch (Exception e1) {
                e1.printStackTrace();
                return null;
            }
            try {
				cns = cns.restrictToCodes(crefs);
                LocalNameList propertyNames = new LocalNameList();
                propertyNames.addEntry(propertyName);
                CodedNodeSet.PropertyType[] propertyTypes = null;

                //long ms = System.currentTimeMillis(), delay = 0;
                SortOptionList sortOptions = null;
                LocalNameList filterOptions = null;
                boolean resolveObjects = true; // needs to be set to true
                int maxToReturn = -1;

                ResolvedConceptReferenceList rcrl =
                    cns.resolveToList(sortOptions, filterOptions,
                        propertyNames, propertyTypes, resolveObjects,
                        maxToReturn);

                // _logger.debug("resolveToList done");
                HashMap hmap = new HashMap();

                if (rcrl == null) {
                    _logger.debug("Concept not found.");
                    return null;
                }

                if (rcrl.getResolvedConceptReferenceCount() > 0) {
                    // ResolvedConceptReference[] list =
                    // rcrl.getResolvedConceptReference();
                    for (int i = 0; i < rcrl.getResolvedConceptReferenceCount(); i++) {
                        ResolvedConceptReference rcr =
                            rcrl.getResolvedConceptReference(i);
                        // _logger.debug("(*) " + rcr.getCode());
                        Entity c = rcr.getReferencedEntry();
                        if (c == null) {
                            _logger.debug("Concept is null.");
                        } else {
                            _logger
                                .debug(c.getEntityDescription().getContent());
                            Property[] properties = c.getProperty();
                            //String values = "";
                            StringBuffer buf = new StringBuffer();
                            for (int j = 0; j < properties.length; j++) {
                                Property prop = properties[j];
                                //values = values + prop.getValue().getContent();
                                buf.append(prop.getValue().getContent());

                                if (j < properties.length - 1) {
                                    //values = values + "; ";
                                    buf.append("; ");
                                }
                            }
                            String values = buf.toString();
                            hmap.put(rcr.getCode(), values);
                        }
                    }
                }
                return hmap;

            } catch (Exception e) {
                _logger.error("Method: SearchUtil.searchByProperties");
                _logger.error("* ERROR: cns.resolve throws exceptions.");
                _logger.error("* " + e.getClass().getSimpleName() + ": "
                    + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public ResolvedConceptReferencesIterator codedNodeGraph2CodedNodeSetIterator(
        CodedNodeGraph cng, ConceptReference graphFocus,
        boolean resolveForward, boolean resolveBackward,
        int resolveAssociationDepth, int maxToReturn) {
        CodedNodeSet cns = null;
        try {
            cns =
                cng.toNodeList(graphFocus, resolveForward, resolveBackward,
                    resolveAssociationDepth, maxToReturn);

            if (cns == null) {
                _logger.warn("cng.toNodeList returns null???");
                return null;
            }

            SortOptionList sortCriteria = null;
            LocalNameList propertyNames = null;
            CodedNodeSet.PropertyType[] propertyTypes = null;
            ResolvedConceptReferencesIterator iterator = null;
            try {
                iterator =
                    cns.resolve(sortCriteria, propertyNames, propertyTypes);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (iterator == null) {
                _logger.warn("cns.resolve returns null???");
            }
            return iterator;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public HashMap getPropertyName2ValueHashMap(Entity curr_concept) {
		if (curr_concept == null) return null;
		Vector propertytypes = new Vector();
		propertytypes.add("PRESENTATION");
		propertytypes.add("DEFINITION");
		propertytypes.add("GENERIC");
		propertytypes.add("COMMENT");
		HashSet hset = new HashSet();
		HashMap hmap = new HashMap();
		Vector propertyvalues = null;
		for (int i=0; i<propertytypes.size(); i++) {
		    String propertytype = (String) propertytypes.elementAt(i);
		    Vector propertynames = getPropertyNamesByType(
			    curr_concept, propertytype);

		    for (int j=0; j<propertynames.size(); j++) {
			    String propertyname = (String) propertynames.elementAt(j);
			    if (!hset.contains(propertyname)) {
			        hset.add(propertyname);
			        propertyvalues = getPropertyValues(curr_concept, propertytype, propertyname);
			        if (propertyvalues != null) {
				        hmap.put(propertyname, propertyvalues);
					}
			    }
		    }
		}
		return hmap;
	}

    public HashMap getPropertyQualifierHashMap(Entity node) {
		HashMap hmap = new HashMap();
		Presentation[] presentations = node.getPresentation();
		for (int i = 0; i < presentations.length; i++) {
			 Presentation presentation = presentations[i];
			 String key = presentation.getPropertyName() + "$" + presentation.getValue().getContent();
			 PropertyQualifier[] qualifiers = presentation.getPropertyQualifier();
			 for (int k=0; k<qualifiers.length; k++) {
				  PropertyQualifier qualifier = qualifiers[k];
				  String value = qualifier.getPropertyQualifierName() + "=" + qualifier.getValue().getContent();
				  Vector v = new Vector();
				  if (hmap.containsKey(key)) {
					   v = (Vector) hmap.get(key);
				  }
				  v.add(value);
				  hmap.put(key, v);
			 }
     	}

		Definition[] definitions = node.getDefinition();
		for (int i = 0; i < definitions.length; i++) {
			 Definition definition = definitions[i];
			 String key = definition.getPropertyName() + "$" + definition.getValue().getContent();
			 PropertyQualifier[] qualifiers = definition.getPropertyQualifier();
			 for (int k=0; k<qualifiers.length; k++) {
				  PropertyQualifier qualifier = qualifiers[k];
				  String value = qualifier.getPropertyQualifierName() + "=" + qualifier.getValue().getContent();
				  Vector v = new Vector();
				  if (hmap.containsKey(key)) {
					   v = (Vector) hmap.get(key);
				  }
				  v.add(value);
				  hmap.put(key, v);
			 }
		}

		Comment[] comments = node.getComment();
		for (int i = 0; i < comments.length; i++) {
			 Comment comment = comments[i];
			 String key = comment.getPropertyName() + "$" + comment.getValue().getContent();
			 PropertyQualifier[] qualifiers = comment.getPropertyQualifier();
			 for (int k=0; k<qualifiers.length; k++) {
				  PropertyQualifier qualifier = qualifiers[k];
				  String value = qualifier.getPropertyQualifierName() + "=" + qualifier.getValue().getContent();
				  Vector v = new Vector();
				  if (hmap.containsKey(key)) {
					   v = (Vector) hmap.get(key);
				  }
				  v.add(value);
				  hmap.put(key, v);
			 }
		}

		Property[] properties = node.getProperty();
		for (int i = 0; i < properties.length; i++) {
			 Property property = properties[i];
			 String key = property.getPropertyName() + "$" + property.getValue().getContent();
			 PropertyQualifier[] qualifiers = property.getPropertyQualifier();
			 for (int k=0; k<qualifiers.length; k++) {
				  PropertyQualifier qualifier = qualifiers[k];
				  String value = qualifier.getPropertyQualifierName() + "=" + qualifier.getValue().getContent();
				  Vector v = new Vector();
				  if (hmap.containsKey(key)) {
					   v = (Vector) hmap.get(key);
				  }
				  v.add(value);
				  hmap.put(key, v);
			 }
		}
        return hmap;
    }

    public Vector getRelationshipSource(String scheme, String version, String code) {
		return getRelationshipTarget(scheme, version, code, true);
	}

    public Vector getRelationshipSource(String scheme, String version, String code, boolean namedClassOnly) {
		Vector v = new Vector();
		CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
		if (version != null) {
			csvt.setVersion(version);
		}
        // Perform the query ...
        try {
			ResolvedConceptReferenceList matches = lbSvc.getNodeGraph(scheme, csvt, null).resolveAsList(
					ConvenienceMethods.createConceptReference(code, scheme), false, true, 1, 1, new LocalNameList(), null,
					null, -1);

			// Analyze the result ...
			if (matches.getResolvedConceptReferenceCount() > 0) {
				Enumeration<? extends ResolvedConceptReference> refEnum = matches.enumerateResolvedConceptReference();

				while (refEnum.hasMoreElements()) {
					ResolvedConceptReference ref = refEnum.nextElement();
					AssociationList targetof = ref.getTargetOf();
					if (targetof != null) {
						if (targetof != null) {
							Association[] associations = targetof.getAssociation();
							if (associations != null && associations.length > 0) {
								for (int i = 0; i < associations.length; i++) {
									Association assoc = associations[i];
									AssociatedConcept[] acl = assoc.getAssociatedConcepts().getAssociatedConcept();
									for (int j = 0; j < acl.length; j++) {
										AssociatedConcept ac = acl[j];
										if (namedClassOnly) {
											if (!ac.getConceptCode().startsWith("@")) {
												String rela = assoc.getAssociationName();
												EntityDescription ed = ac.getEntityDescription();
												v.add(ed.getContent() + "$" + ac.getConceptCode() + "$" + rela);
											}
										} else {
											String rela = assoc.getAssociationName();
											EntityDescription ed = ac.getEntityDescription();
											v.add(ed.getContent() + "$" + ac.getConceptCode() + "$" + rela);
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
        return v;
    }

    public Vector getRelationshipTarget(String scheme, String version, String code) {
		return getRelationshipTarget(scheme, version, code, true);
	}


    public Vector getRelationshipTarget(String scheme, String version, String code, boolean namedClassOnly) {
		Vector v = new Vector();
		CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
		if (version != null) {
			csvt.setVersion(version);
		}
        // Perform the query ...
        try {
			ResolvedConceptReferenceList matches = lbSvc.getNodeGraph(scheme, csvt, null).resolveAsList(
					ConvenienceMethods.createConceptReference(code, scheme), true, false, 1, 1, new LocalNameList(), null,
					null, -1);

			// Analyze the result ...
			if (matches.getResolvedConceptReferenceCount() > 0) {
				Enumeration<? extends ResolvedConceptReference> refEnum = matches.enumerateResolvedConceptReference();

				while (refEnum.hasMoreElements()) {
					ResolvedConceptReference ref = refEnum.nextElement();
					AssociationList sourceof = ref.getSourceOf();

					if (sourceof != null) {
						Association[] associations = sourceof.getAssociation();

						if (associations != null && associations.length > 0) {
							for (int i = 0; i < associations.length; i++) {
								Association assoc = associations[i];
								//displayMessage(pw, "\t" + assoc.getAssociationName());

								AssociatedConcept[] acl = assoc.getAssociatedConcepts().getAssociatedConcept();
								for (int j = 0; j < acl.length; j++) {
									AssociatedConcept ac = acl[j];
									if (namedClassOnly) {
										if (!ac.getConceptCode().startsWith("@")) {
											String rela = assoc.getAssociationName();
											EntityDescription ed = ac.getEntityDescription();
											v.add(ed.getContent() + "$" + ac.getConceptCode() + "$" + rela);
										}
								    } else {
										String rela = assoc.getAssociationName();
										EntityDescription ed = ac.getEntityDescription();
										v.add(ed.getContent() + "$" + ac.getConceptCode() + "$" + rela);
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return v;
    }


}
