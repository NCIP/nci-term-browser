/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.bean;

import gov.nih.nci.evs.browser.utils.*;
import java.util.*;
import javax.faces.model.*;

import org.LexGrid.LexBIG.DataModel.Collections.*;
import org.LexGrid.LexBIG.DataModel.Core.*;
import org.LexGrid.LexBIG.LexBIGService.*;
import org.LexGrid.LexBIG.Utility.*;
import org.LexGrid.codingSchemes.*;
import org.LexGrid.naming.*;
import org.LexGrid.LexBIG.Impl.Extensions.GenericExtensions.*;
import org.apache.log4j.*;

/**
 * 
 */

public class OntologyBean {
    private static Logger _logger = Logger.getLogger(OntologyBean.class);
    private static List _rela_list = null;
    private static List _association_name_list = null;
    private static List _property_name_list = null;
    private static List _property_type_list = null;
    private static List _source_list = null;

    private static Vector _rela_vec = null;
    private static Vector _association_name_vec = null;
    private static Vector _property_name_vec = null;
    private static Vector _property_type_vec = null;
    private static Vector _source_vec = null;

    public static List getRELAList(String codingSchemeName) {
        if (_rela_list != null)
            return _rela_list;
        _rela_list = new ArrayList();
        if (_rela_vec == null) {
            _rela_vec = getRELAs(codingSchemeName, null);
        }
        _rela_list.add(new SelectItem("", ""));
        for (int k = 0; k < _rela_vec.size(); k++) {
            String value = (String) _rela_vec.elementAt(k);
            _rela_list.add(new SelectItem(value, value));
        }
        return _rela_list;
    }

    public static List getRELAList(String codingSchemeName, String version) {
        if (_rela_list != null)
            return _rela_list;
        _rela_list = new ArrayList();
        if (_rela_vec == null) {
            _rela_vec = getRELAs(codingSchemeName, version);
        }
        _rela_list.add(new SelectItem("", ""));
        for (int k = 0; k < _rela_vec.size(); k++) {
            String value = (String) _rela_vec.elementAt(k);
            _rela_list.add(new SelectItem(value, value));
        }
        return _rela_list;
    }


    public static Vector getAssociationNames(String codingSchemeName) {
        if (_association_name_vec != null) {
            return _association_name_vec;
        }
        CodingScheme cs = getCodingScheme(codingSchemeName, null);
        _association_name_vec = getSupportedAssociationNames(cs);

        return _association_name_vec;
    }


    public static Vector getAssociationNames(String codingSchemeName, String version) {
        if (_association_name_vec != null) {
            return _association_name_vec;
        }
        CodingScheme cs = getCodingScheme(codingSchemeName, version);
        _association_name_vec = getSupportedAssociationNames(cs);

        return _association_name_vec;
    }

    public static List getAssociationNameList(String codingSchemeName) {
		return getAssociationNameList(codingSchemeName, null);
	}


    public static List getAssociationNameList(String codingSchemeName, String version) {
        if (_association_name_list != null)
            return _association_name_list;
        _association_name_list = new ArrayList();
        CodingScheme cs = getCodingScheme(codingSchemeName, version);
        if (_association_name_vec == null) {
            _association_name_vec = getSupportedAssociationNames(cs);
        }
        for (int k = 0; k < _association_name_vec.size(); k++) {
            String value = (String) _association_name_vec.elementAt(k);
            _association_name_list.add(new SelectItem(value, value));
        }
        return _association_name_list;
    }

    public static List getPropertyNameList(String codingSchemeName) {
        if (_property_name_list != null)
            return _property_name_list;
        _property_name_list = new ArrayList();
        _property_name_list.add(new SelectItem("ALL", "ALL"));

        CodingScheme cs = getCodingScheme(codingSchemeName, null);
        Vector<String> properties = getSupportedPropertyNames(cs);
        for (int k = 0; k < properties.size(); k++) {
            String value = (String) properties.elementAt(k);
            _property_name_list.add(new SelectItem(value, value));
        }
        return _property_name_list;
    }

    public static List getPropertyNameList(String codingSchemeName, String version) {
        if (_property_name_list != null)
            return _property_name_list;
        _property_name_list = new ArrayList();
        _property_name_list.add(new SelectItem("ALL", "ALL"));

        CodingScheme cs = getCodingScheme(codingSchemeName, version);
        Vector<String> properties = getSupportedPropertyNames(cs);
        for (int k = 0; k < properties.size(); k++) {
            String value = (String) properties.elementAt(k);
            _property_name_list.add(new SelectItem(value, value));
        }
        return _property_name_list;
    }

    public static List getSourceList(String codingSchemeName) {
        if (_source_list != null)
            return _source_list;
        _source_list = new ArrayList();
        CodingScheme cs = getCodingScheme(codingSchemeName, null);
        _source_list.add(new SelectItem("ALL", "ALL"));

        Vector<String> sources = getSupportedSources(cs);
        for (int k = 0; k < sources.size(); k++) {
            String value = (String) sources.elementAt(k);
            _source_list.add(new SelectItem(value, value));
        }
        return _source_list;
    }

    public static List getSourceList(String codingSchemeName, String version) {
        if (_source_list != null)
            return _source_list;
        _source_list = new ArrayList();
        CodingScheme cs = getCodingScheme(codingSchemeName, version);
        _source_list.add(new SelectItem("ALL", "ALL"));

        Vector<String> sources = getSupportedSources(cs);
        for (int k = 0; k < sources.size(); k++) {
            String value = (String) sources.elementAt(k);
            _source_list.add(new SelectItem(value, value));
        }
        return _source_list;
    }

    public static List getPropertyTypeList(String codingSchemeName) {
		return getPropertyTypeList(codingSchemeName, null);
	}


    public static List getPropertyTypeList(String codingSchemeName, String version) {
        if (_property_type_list != null)
            return _property_type_list;
        _property_type_list = new ArrayList();
        _property_type_list.add(new SelectItem("ALL", "ALL"));

        Vector<String> propertytypes = getSupportedPropertyTypes();
        for (int k = 0; k < propertytypes.size(); k++) {
            String value = (String) propertytypes.elementAt(k);
            _property_type_list.add(new SelectItem(value, value));
        }
        return _property_type_list;
    }

    public static Vector getRELAs(String codingSchemeName) {
        if (_rela_vec != null)
            return _rela_vec;
        return getRELAs(codingSchemeName, null);
    }

    public static Vector getRELAs(String scheme, String version) {
        Vector v = new Vector();
        HashSet hset = new HashSet();
        LexBIGService lbs = RemoteServerUtil.createLexBIGService(false);
        LexBIGServiceMetadata lbsm = null;
        try {
            lbsm = lbs.getServiceMetadata();
            lbsm =
                lbsm
                    .restrictToCodingScheme(Constructors
                        .createAbsoluteCodingSchemeVersionReference(scheme,
                            version));

            MetadataPropertyList mdpl = lbsm.resolve();
            Set<String> relas = new HashSet<String>();
            int rela_count = 0;
            for (int i = 0; i < mdpl.getMetadataPropertyCount(); i++) {
                MetadataProperty prop = mdpl.getMetadataProperty(i);
                /*
                 * if(prop.getName().equals("dockey") &&
                 * prop.getValue().equals("RELA")){ i++; rela_count++; prop =
                 * mdpl.getMetadataProperty(i); if
                 * (!hset.contains(prop.getValue())) {
                 * relas.add(prop.getValue()); v.add(prop.getValue());
                 * hset.add(prop.getValue()); } }
                 */

                if (prop.getName().equals("dockey")
                    && prop.getValue().equals("RELA")) {
                    i++;
                    prop = mdpl.getMetadataProperty(i);

                    String potentialValue = prop.getValue();
                    i++;
                    prop = mdpl.getMetadataProperty(i);

                    String type = prop.getValue();
                    if (type.equals("expanded_form")
                        || type.equals("rela_inverse")) {
                        if (!hset.contains(potentialValue)) {
                            v.add(potentialValue);
                            hset.add(potentialValue);
                        }
                    }
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        hset.clear();
        return SortUtils.quickSort(v);
    }

    // /////////////////////
    // Convenience Methods
    // /////////////////////

    private static CodingScheme getCodingScheme(String codingScheme,
        String version) {
        CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
        if (version != null && version.compareTo("null") != 0)
            versionOrTag.setVersion(version);
        CodingScheme cs = null;
        try {
            LexBIGService lbSvc = new RemoteServerUtil().createLexBIGService();
            try {
                cs = lbSvc.resolveCodingScheme(codingScheme, versionOrTag);
            } catch (Exception ex2) {
                cs = null;
                System.out.println("ERROR: Unable to resolve coding scheme -- " + codingScheme + "(version: " + version + ")");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cs;
    }

    private static Vector getSupportedEntityType(CodingScheme cs) {
        if (cs == null)
            return null;
        Vector v = new Vector();
        try {
            SupportedEntityType[] types =
                cs.getMappings().getSupportedEntityType();
            for (int i = 0; i < types.length; i++) {
                v.add(types[i].getLocalId());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return SortUtils.quickSort(v);
    }

    private static Vector getSupportedPropertyQualifier(CodingScheme cs) {
        if (cs == null)
            return null;
        Vector v = new Vector();
        try {
            SupportedPropertyQualifier[] qualifiers =
                cs.getMappings().getSupportedPropertyQualifier();
            for (int i = 0; i < qualifiers.length; i++) {
                v.add(qualifiers[i].getLocalId());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return SortUtils.quickSort(v);
    }

    public static Vector getSupportedSources(String codingSchemeName) {
        return getSupportedSources(codingSchemeName, null);
    }

    public static Vector getSupportedSources(String codingSchemeName, String version) {
		System.out.println("OntologyBean getSupportedSources codingSchemeName:" + codingSchemeName);
		System.out.println("OntologyBean getSupportedSources version:" + version);

        CodingScheme cs = getCodingScheme(codingSchemeName, version);
        return getSupportedSources(cs);
    }

    private static Vector getSupportedSources(CodingScheme cs) {
        if (cs == null) {
			System.out.println("calling OntologyBean getSupportedSources cs == null ??? " );

            return null;
		}
        Vector v = new Vector();
        try {
            SupportedSource[] sources = cs.getMappings().getSupportedSource();
            for (int i = 0; i < sources.length; i++) {
                v.add(sources[i].getLocalId());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return SortUtils.quickSort(v);
    }

    private static Vector getSupportedPropertyTypes() {
        Vector v = new Vector();
        v.add("PRESENTATION");
        v.add("DEFINITION");
        v.add("COMMENT");
        v.add("GENERIC");
        return v;// SortUtils.quickSort(v);
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
        return SortUtils.quickSort(v);
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
        return SortUtils.quickSort(v);
    }

    public static Vector<String> getSupportedPropertyNames(
        String codingSchemeName) {
        CodingScheme cs = getCodingScheme(codingSchemeName, null);
        return getSupportedPropertyNames(cs);
    }

    public static Vector<String> getSupportedPropertyNames(
        String codingSchemeName, String version) {
        CodingScheme cs = getCodingScheme(codingSchemeName, version);
        return getSupportedPropertyNames(cs);
    }

    public static Vector<String> getSupportedAssociationQualifier(
        CodingScheme cs) {
        Vector<String> v = new Vector<String>();
        try {
            org.LexGrid.naming.SupportedAssociationQualifier[] supportedAssociationQualifiers =
                cs.getMappings().getSupportedAssociationQualifier();
            if (supportedAssociationQualifiers == null)
                return null;
            for (int i = 0; i < supportedAssociationQualifiers.length; i++) {
                SupportedAssociationQualifier q =
                    supportedAssociationQualifiers[i];
                v.add(q.getLocalId());
            }
        } catch (Exception e) {
            return null;
        }
        return SortUtils.quickSort(v);
    }

    public static Vector<String> getSupportedAssociationNames(
        String codingSchemeName) {
        CodingScheme cs = getCodingScheme(codingSchemeName, null);
        return getSupportedAssociationNames(cs);
    }
/*
    public static Vector<String> getSupportedAssociationNames(
        String codingSchemeName, String version) {
        CodingScheme cs = getCodingScheme(codingSchemeName, version);
        return getSupportedAssociationNames(cs);
    }
*/
    public static Vector<String> getSupportedAssociationNames(CodingScheme cs) {
        if (cs == null)
            return null;
        Vector<String> v = new Vector<String>();
        SupportedAssociation[] assos =
            cs.getMappings().getSupportedAssociation();
        for (int i = 0; i < assos.length; i++) {
            SupportedAssociation sa = (SupportedAssociation) assos[i];
            v.add(sa.getLocalId());
        }


        return SortUtils.quickSort(v);
    }

    public static Vector getAssociationCodesByNames(String codingScheme,
        String version, Vector associations) {
        LexBIGServiceConvenienceMethodsImpl lbscm = null;
        CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
        if (version != null)
            versionOrTag.setVersion(version);
        Vector w = new Vector();

        try {
            LexBIGService lbSvc = new RemoteServerUtil().createLexBIGService();
            lbscm =
                (LexBIGServiceConvenienceMethodsImpl) lbSvc
                    .getGenericExtension("LexBIGServiceConvenienceMethods");
            lbscm.setLexBIGService(lbSvc);

            for (int i = 0; i < associations.size(); i++) {
                String entityCode = (String) associations.elementAt(i);
                try {
                    String name =
                        lbscm.getAssociationNameFromAssociationCode(
                            codingScheme, versionOrTag, entityCode);
                    w.add(name);
                } catch (Exception e) {
                    w.add("<NOT ASSIGNED>");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return SortUtils.quickSort(w);
    }


    public static String convertAssociationName(String codingScheme,
        String version, String association) {
        LexBIGServiceConvenienceMethodsImpl lbscm = null;
        CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
        if (version != null)
            versionOrTag.setVersion(version);
        Vector w = new Vector();

        try {
            LexBIGService lbSvc = new RemoteServerUtil().createLexBIGService();
            lbscm =
                (LexBIGServiceConvenienceMethodsImpl) lbSvc
                    .getGenericExtension("LexBIGServiceConvenienceMethods");
            lbscm.setLexBIGService(lbSvc);

			try {
				String asscName =
					lbscm.getAssociationCodeFromAssociationName(
						codingScheme, versionOrTag, association);
				return asscName;
			} catch (Exception e) {
				e.printStackTrace();
			}

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return association;
    }



    public static Vector getSupportedAssociationNames(String codingSchemeName, String version) {
        _association_name_vec = new Vector();

        LexBIGServiceConvenienceMethodsImpl lbscm = null;
        try {
            LexBIGService lbSvc = new RemoteServerUtil().createLexBIGService();
            lbscm =
                (LexBIGServiceConvenienceMethodsImpl) lbSvc
                    .getGenericExtension("LexBIGServiceConvenienceMethods");
            lbscm.setLexBIGService(lbSvc);
			CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
			if (version != null)
				versionOrTag.setVersion(version);

			CodingScheme cs = getCodingScheme(codingSchemeName, version);
			if (cs == null) return null;
			SupportedAssociation[] assos =
				cs.getMappings().getSupportedAssociation();
			for (int i = 0; i < assos.length; i++) {
				SupportedAssociation sa = (SupportedAssociation) assos[i];
				String name =
					lbscm.getAssociationNameFromAssociationCode(
						codingSchemeName, versionOrTag, sa.getLocalId());

				_association_name_vec.add(name);

			}
			_association_name_vec = SortUtils.quickSort(_association_name_vec);
			return _association_name_vec;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
    }



    public static void dumpVector(String label, Vector v) {
        _logger.debug("\n" + label);
        if (v == null)
            return;
        for (int i = 0; i < v.size(); i++) {
            String t = (String) v.elementAt(i);
            int j = i + 1;
            _logger.debug("(" + j + "): " + t);
        }
    }





    public static void main(String[] args) throws Exception {
        String scheme = "NCI Metathesaurus";
        String version = null;

        Vector rela_vec = getRELAs(scheme, version);
        dumpVector("Supported RELAs", rela_vec);

        CodingScheme cs = getCodingScheme(scheme, version);
        Vector<String> properties = getSupportedPropertyNames(cs);
        dumpVector("Supported Properties", properties);

        Vector<String> associations = getSupportedAssociationNames(cs);
        dumpVector("Supported Association Names", associations);
    }
}
