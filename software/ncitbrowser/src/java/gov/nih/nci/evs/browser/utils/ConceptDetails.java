package gov.nih.nci.evs.browser.utils;

import java.util.*;
import java.io.*;
import java.util.Map.Entry;
import gov.nih.nci.evs.browser.bean.*;
import gov.nih.nci.evs.browser.properties.*;
import gov.nih.nci.evs.browser.utils.*;
import org.LexGrid.concepts.*;
import org.LexGrid.LexBIG.Utility.Iterators.*;
import org.LexGrid.LexBIG.DataModel.Core.*;


import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.client.ApplicationServiceProvider;
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

public class ConceptDetails {
    private static Logger _logger = Logger.getLogger(ConceptDetails.class);
    public String _ncimURL = null;
    public String _ncitURL = null;


	public ConceptDetails() {

	}

    public String getNCItURL() {
        if (_ncitURL != null) {
            return _ncitURL;
        }
        String default_info = "N/A";
        NCItBrowserProperties properties = null;
        try {
            properties = NCItBrowserProperties.getInstance();
            _ncitURL = properties.getProperty(NCItBrowserProperties.NCIT_URL);
            if (_ncitURL == null) {
                _ncitURL = default_info;
            }
        } catch (Exception ex) {

        }
        return _ncitURL;
    }

    public String getNCImURL() {
        if (_ncimURL != null) {
            return _ncimURL;
        }
        String default_info = "http://ncim.nci.nih.gov";
        NCItBrowserProperties properties = null;
        try {
            properties = NCItBrowserProperties.getInstance();
            _ncimURL = properties.getProperty(NCItBrowserProperties.NCIM_URL);
            if (_ncimURL == null) {
                _ncimURL = default_info;
            }
        } catch (Exception ex) {

        }
        return _ncimURL;
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

    public static ConceptReferenceList createConceptReferenceList(Vector codes,
        String codingSchemeName) {
        if (codes == null) {
            return null;
        }
        ConceptReferenceList list = new ConceptReferenceList();
        for (int i = 0; i < codes.size(); i++) {
            String code = (String) codes.elementAt(i);
            ConceptReference cr = new ConceptReference();
            cr.setCodingSchemeName(codingSchemeName);
            cr.setConceptCode(code);
            list.addConceptReference(cr);
        }
        return list;
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


    public Entity getConceptByCode(String codingSchemeName, String vers, String code) {

        try {
			if (code == null) {
				return null;
			}
			if (code.indexOf("@") != -1) return null; // anonymous class

            LexBIGService lbSvc = new RemoteServerUtil().createLexBIGService();
            if (lbSvc == null) {
               return null;
            }
            CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
            if (vers != null) versionOrTag.setVersion(vers);

            ConceptReferenceList crefs = createConceptReferenceList(
                    new String[] { code }, codingSchemeName);

            CodedNodeSet cns = null;
            try {
				try {
					cns = getNodeSet(lbSvc, codingSchemeName, versionOrTag);

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

    public static String encodeTerm(String s) {
		if (s == null) return null;
		if (StringUtils.isAlphanumeric(s)) return s;

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

    public static Vector getPresentationProperties(Entity concept) {
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


    public static Entity getConceptWithProperty(String scheme, String version,
        String code, String propertyName) {
        try {
            LexBIGService lbSvc = new RemoteServerUtil().createLexBIGService();

            if (lbSvc == null) {
                _logger.warn("lbSvc = null");
                return null;
            }

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


    public static Vector getConceptPropertyValues(Entity c, String propertyName) {
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

    public static String getConceptStatus(String scheme, String version,
        String ltag, String code) {
        boolean conceptStatusSupported = false;
        if (DataUtils.getVocabulariesWithConceptStatusHashSet().contains(scheme))
            conceptStatusSupported = true;
        if (!conceptStatusSupported)
            return null;

        Entity c =
            getConceptWithProperty(scheme, version, code, "Concept_Status");
        String con_status = null;
        if (c != null) {
            Vector status_vec = getConceptPropertyValues(c, "Concept_Status");
            if (status_vec == null || status_vec.size() == 0) {
                con_status = c.getStatus();
            } else {
                con_status = DataUtils.convertToCommaSeparatedValue(status_vec);
            }
            return con_status;
        }
        return null;
    }

    public Vector<String> getSupportedPropertyNames(String codingScheme,
        String version) {
        CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
        if (version != null)
            versionOrTag.setVersion(version);
        try {
            CodingScheme cs = DataUtils.getCodingScheme(codingScheme, versionOrTag);
            return getSupportedPropertyNames(cs);
        } catch (Exception ex) {
        }
        return null;
    }

    public static Vector getPropertyValues(Entity concept,
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

    public static String getPropertyQualfierValues(
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


    public static Vector<String> parseData(String line) {
		if (line == null) return null;
        String tab = "|";
        return parseData(line, tab);
    }

    public static Vector<String> parseData(String line, String tab) {
		if (line == null) return null;
        Vector data_vec = new Vector();
        StringTokenizer st = new StringTokenizer(line, tab);
        while (st.hasMoreTokens()) {
            String value = st.nextToken();
            if (value.compareTo("null") == 0)
                value = " ";
            data_vec.add(value);
        }
        return data_vec;
    }

    public Vector getNCImCodes(String scheme, String version, String code) {
        Vector w = new Vector();
        CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
        if (version != null) {
			csvt.setVersion(version);
		}
		try {
			LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
			if (lbSvc == null) {
				_logger
					.warn("WARNING: Unable to connect to instantiate LexBIGService ???");
				return null;
			}

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

    public static String getMetadataValue(String scheme, String propertyName) {
		//032014
		String formalName = getFormalName(scheme);
		Vector v = getMetadataValues(formalName, propertyName);

        //Vector v = getMetadataValues(scheme, propertyName);
        if (v == null || v.size() == 0)
            return null;
        return (String) v.elementAt(0);
    }

    public static Vector getMetadataValues(String scheme, String propertyName) {
        //if (_formalName2MetadataHashMap == null) {
        //    setCodingSchemeMap();
        //}

		String formalName = getFormalName(scheme);
        if (!DataUtils.getFormalName2MetadataHashMap().containsKey(formalName)) {
            return null;
        }
        Vector metadata = (Vector) DataUtils.getFormalName2MetadataHashMap().get(formalName);
        if (metadata == null || metadata.size() == 0) {
            return null;
        }
        Vector v = MetadataUtils.getMetadataValues(metadata, propertyName);
        return v;
    }



    public static String getMetadataValue(String scheme, String version, String propertyName) {
        Vector v;
        if (version != null && ! version.equalsIgnoreCase("null"))
            v = getMetadataValues(scheme, version, propertyName);
        else v = getMetadataValues(scheme, propertyName);
        if (v == null || v.size() == 0)
            return null;
        return (String) v.elementAt(0);
    }

    public static Vector getMetadataValues(String scheme, String version, String propertyName) {
        //if (_formalName2MetadataHashMap == null) {
        //    setCodingSchemeMap();
        //}

        String formalName = getFormalName(scheme);

        if (!DataUtils.getFormalNameVersion2MetadataHashMap().containsKey(formalName + "$" + version)) {
            return null;
        }
        Vector metadata = (Vector) DataUtils.getFormalNameVersion2MetadataHashMap().get(formalName + "$" + version);
        if (metadata == null || metadata.size() == 0) {
            return null;
        }
        Vector v = MetadataUtils.getMetadataValues(metadata, propertyName);
        return v;
    }

    public static boolean isCodingSchemeLoaded(String scheme, String version) {
		//if (_formalNameVersion2MetadataHashMap == null) {
		//	setCodingSchemeMap();
		//}

		String formalName = getFormalName(scheme);
        boolean isLoaded = DataUtils.getFormalNameVersion2MetadataHashMap().containsKey(formalName + "$" + version);
        return isLoaded;
    }


    public static Vector getNCImCodes(Entity node) {
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


    public static String getFormalName(String key) {
        if (key == null) {
            return null;
        }
        //if (DataUtil._localName2FormalNameHashMap == null) {
        //    setCodingSchemeMap();
        //}
        if (!DataUtils.getLocalName2FormalNameHashMap().containsKey(key))
            return null;

        String value = (String) DataUtils.getLocalName2FormalNameHashMap().get(key);
//        Utils.debugHashMap("DataUtils.getFormalName: " + key,
//        	_localName2FormalNameHashMap, "value: " + value);
        return value;
    }

	public static String getCSName(String vocabularyName) {
//        if (_uri2CodingSchemeNameHashMap == null) setCodingSchemeMap();
		String formalname = getFormalName(vocabularyName);
		if (DataUtils.getUri2CodingSchemeNameHashMap().get(formalname) == null) return formalname;
		String t = (String) DataUtils.getUri2CodingSchemeNameHashMap().get(formalname);
		return t;
	}

	public String getDisplayName(String vocabularyName) {
        //if (_formalName2DisplayNameHashMap == null) setCodingSchemeMap();
		String formalname = getFormalName(vocabularyName);
		if (DataUtils.getFormalName2DisplayNameHashMap().get(formalname) == null) return formalname;
		String t = (String) DataUtils.getFormalName2DisplayNameHashMap().get(formalname);
		return t;
	}

    public static Vector getPropertyNamesByType(Entity concept,
        String property_type) {
        Vector v = new Vector();
        org.LexGrid.commonTypes.Property[] properties = null;

        if (property_type.compareToIgnoreCase("GENERIC") == 0) {
            properties = concept.getProperty();
        } else if (property_type.compareToIgnoreCase("PRESENTATION") == 0) {
            properties = concept.getPresentation();
            // } else if (property_type.compareToIgnoreCase("INSTRUCTION") == 0)
            // {
            // properties = concept.getInstruction();
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
        String version, String ltag, Vector codes) {
        boolean conceptStatusSupported = false;
        if (DataUtils.getVocabulariesWithConceptStatusHashSet().contains(scheme))
            conceptStatusSupported = true;

        Vector w = new Vector();
        long ms = System.currentTimeMillis();
        for (int i = 0; i < codes.size(); i++) {
            if (conceptStatusSupported) {
                String code = (String) codes.elementAt(i);
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
                            DataUtils.convertToCommaSeparatedValue(status_vec);
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
            + codes.size());
        return w;
    }

    public static Vector<String> getSupportedPropertyNames(CodingScheme cs) {
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


    protected static CodingScheme getCodingScheme(String codingScheme,
        CodingSchemeVersionOrTag versionOrTag) throws LBException {

        CodingScheme cs = null;
        try {
            LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
            cs = lbSvc.resolveCodingScheme(codingScheme, versionOrTag);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cs;
    }

    public static Vector<SupportedProperty> getSupportedProperties(
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
/*
    public String getNCImURL() {
        if (_ncimURL != null) {
            return _ncimURL;
        }
        String default_info = "http://ncim.nci.nih.gov";
        NCItBrowserProperties properties = null;
        try {
            properties = NCItBrowserProperties.getInstance();
            _ncimURL = properties.getProperty(NCItBrowserProperties.NCIM_URL);
            if (_ncimURL == null) {
                _ncimURL = default_info;
            }
        } catch (Exception ex) {

        }
        return _ncimURL;
    }

    public String getEVSServiceURL() {
        if (_evsServiceURL != null) {
            return _evsServiceURL;
        }
        String default_info = "Local LexEVS";
        NCItBrowserProperties properties = null;
        try {
            properties = NCItBrowserProperties.getInstance();
            _evsServiceURL =
                properties.getProperty(NCItBrowserProperties.EVS_SERVICE_URL);
            if (_evsServiceURL == null) {
                _evsServiceURL = default_info;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return _evsServiceURL;
    }

    public String getTermSuggestionURL() {
        NCItBrowserProperties properties = null;
        try {
            properties = NCItBrowserProperties.getInstance();
            _term_suggestion_application_url =
                properties
                    .getProperty(NCItBrowserProperties.TERM_SUGGESTION_APPLICATION_URL);
        } catch (Exception ex) {

        }
        return _term_suggestion_application_url;
    }
*/
}
