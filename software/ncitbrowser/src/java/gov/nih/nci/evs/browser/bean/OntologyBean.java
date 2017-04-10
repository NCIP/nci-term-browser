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

import org.LexGrid.relations.AssociationPredicate;
import org.LexGrid.relations.Relations;


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



   private OntologyBean(){

   }

   public static OntologyBean getInstance(){
         return OntologyBeanClassHolder.instance;
   }

   private static class OntologyBeanClassHolder{
         private static final OntologyBean instance = new OntologyBean();
   }

    public static List getRELAList(String codingSchemeName) {
		if (codingSchemeName == null) return null;
		/*
        if (_rela_list != null)
            return _rela_list;
            */
        List rela_list = new ArrayList();
        /*
        if (_rela_vec == null) {
            _rela_vec = getRELAs(codingSchemeName, null);
        }
        */
        Vector rela_vec = getRELAs(codingSchemeName, null);
        rela_list.add(new SelectItem("", ""));
        for (int k = 0; k < rela_vec.size(); k++) {
            String value = (String) rela_vec.elementAt(k);
            rela_list.add(new SelectItem(value, value));
        }
        return rela_list;
    }

    public static List getRELAList(String codingSchemeName, String version) {
		if (codingSchemeName == null) return null;
		/*
        if (_rela_list != null)
            return _rela_list;
            */
        List rela_list = new ArrayList();
        /*
        if (_rela_vec == null) {
            _rela_vec = getRELAs(codingSchemeName, version);
        }
        */
        Vector rela_vec = getRELAs(codingSchemeName, version);
        rela_list.add(new SelectItem("", ""));
        for (int k = 0; k < rela_vec.size(); k++) {
            String value = (String) rela_vec.elementAt(k);
            rela_list.add(new SelectItem(value, value));
        }
        return rela_list;
    }


    public static Vector getAssociationNames(String codingSchemeName) {
		if (codingSchemeName == null) return null;
		/*
        if (_association_name_vec != null) {
            return _association_name_vec;
        }
        */
        CodingScheme cs = getCodingScheme(codingSchemeName, null);
        Vector association_name_vec = getSupportedAssociationNames(cs);
        return association_name_vec;
    }


    public static Vector getAssociationNames(String codingSchemeName, String version) {
		if (codingSchemeName == null) return null;
		/*
        if (_association_name_vec != null) {
            return _association_name_vec;
        }
        */
        CodingScheme cs = getCodingScheme(codingSchemeName, version);
        Vector association_name_vec = getSupportedAssociationNames(cs);
        return association_name_vec;
    }

    public static List getAssociationNameList(String codingSchemeName) {
		if (codingSchemeName == null) return null;
		return getAssociationNameList(codingSchemeName, null);
	}


    public static List getAssociationNameList(String codingSchemeName, String version) {
		if (codingSchemeName == null) return null;
        if (_association_name_list != null)
            return _association_name_list;
        List association_name_list = new ArrayList();
        CodingScheme cs = getCodingScheme(codingSchemeName, version);
        if (_association_name_vec == null) {
            _association_name_vec = getSupportedAssociationNames(cs);
        }
        for (int k = 0; k < _association_name_vec.size(); k++) {
            String value = (String) _association_name_vec.elementAt(k);
            association_name_list.add(new SelectItem(value, value));
        }
        return association_name_list;
    }

    public static List getPropertyNameList(String codingSchemeName) {
		if (codingSchemeName == null) return null;
        if (_property_name_list != null)
            return _property_name_list;
        List property_name_list = new ArrayList();
        property_name_list.add(new SelectItem("ALL", "ALL"));

        CodingScheme cs = getCodingScheme(codingSchemeName, null);
        Vector<String> properties = getSupportedPropertyNames(cs);
        for (int k = 0; k < properties.size(); k++) {
            String value = (String) properties.elementAt(k);
            property_name_list.add(new SelectItem(value, value));
        }
        return property_name_list;
    }

    public static List getPropertyNameList(String codingSchemeName, String version) {
		if (codingSchemeName == null) return null;
        if (_property_name_list != null)
            return _property_name_list;
        List property_name_list = new ArrayList();
        property_name_list.add(new SelectItem("ALL", "ALL"));

        CodingScheme cs = getCodingScheme(codingSchemeName, version);
        Vector<String> properties = getSupportedPropertyNames(cs);
        for (int k = 0; k < properties.size(); k++) {
            String value = (String) properties.elementAt(k);
            property_name_list.add(new SelectItem(value, value));
        }
        return property_name_list;
    }

    public static List getSourceList(String codingSchemeName) {
		if (codingSchemeName == null) return null;
        if (_source_list != null)
            return _source_list;
        List source_list = new ArrayList();
        CodingScheme cs = getCodingScheme(codingSchemeName, null);
        source_list.add(new SelectItem("ALL", "ALL"));

        Vector<String> sources = getSupportedSources(cs);
        for (int k = 0; k < sources.size(); k++) {
            String value = (String) sources.elementAt(k);
            source_list.add(new SelectItem(value, value));
        }
        return source_list;
    }

    public static List getSourceList(String codingSchemeName, String version) {
		if (codingSchemeName == null) return null;
        if (_source_list != null)
            return _source_list;
        List source_list = new ArrayList();
        CodingScheme cs = getCodingScheme(codingSchemeName, version);
        source_list.add(new SelectItem("ALL", "ALL"));

        Vector<String> sources = getSupportedSources(cs);
        for (int k = 0; k < sources.size(); k++) {
            String value = (String) sources.elementAt(k);
            source_list.add(new SelectItem(value, value));
        }
        return source_list;
    }

    public static List getPropertyTypeList(String codingSchemeName) {
		if (codingSchemeName == null) return null;
		return getPropertyTypeList(codingSchemeName, null);
	}


    public static List getPropertyTypeList(String codingSchemeName, String version) {
		if (codingSchemeName == null) return null;
        if (_property_type_list != null)
            return _property_type_list;
        List property_type_list = new ArrayList();
        property_type_list.add(new SelectItem("ALL", "ALL"));

        Vector<String> propertytypes = getSupportedPropertyTypes();
        for (int k = 0; k < propertytypes.size(); k++) {
            String value = (String) propertytypes.elementAt(k);
            property_type_list.add(new SelectItem(value, value));
        }
        return property_type_list;
    }

    public static Vector getRELAs(String codingSchemeName) {
		if (codingSchemeName == null) return null;
        if (_rela_vec != null)
            return _rela_vec;
        return getRELAs(codingSchemeName, null);
    }

    public static Vector getRELAs(String scheme, String version) {
		if (scheme == null) return null;
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
            //Set<String> relas = new HashSet<String>();
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
        return new SortUtils().quickSort(v);
    }

    // /////////////////////
    // Convenience Methods
    // /////////////////////

    private static CodingScheme getCodingScheme(String codingScheme,
        String version) {
		if (codingScheme == null) return null;

        CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
        if (version != null && version.compareTo("null") != 0)
            versionOrTag.setVersion(version);
        CodingScheme cs = null;
        try {
            LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
            try {
                cs = lbSvc.resolveCodingScheme(codingScheme, versionOrTag);
            } catch (Exception ex2) {
                cs = null;
                //System.out.println("ERROR: Unable to resolve coding scheme -- " + codingScheme + "(version: " + version + ")");
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
        return new SortUtils().quickSort(v);
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
        return new SortUtils().quickSort(v);
    }

    public static Vector getSupportedSources(String codingSchemeName) {
		if (codingSchemeName == null) return null;

        return getSupportedSources(codingSchemeName, null);
    }

    public static Vector getSupportedSources(String codingSchemeName, String version) {
		if (codingSchemeName == null) return null;

        CodingScheme cs = getCodingScheme(codingSchemeName, version);
        return getSupportedSources(cs);
    }

    private static Vector getSupportedSources(CodingScheme cs) {
        if (cs == null) {
			//System.out.println("calling OntologyBean getSupportedSources cs == null ??? " );
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
        return new SortUtils().quickSort(v);
    }

    private static Vector getSupportedPropertyTypes() {
        Vector v = new Vector();
        v.add("PRESENTATION");
        v.add("DEFINITION");
        v.add("COMMENT");
        v.add("GENERIC");
        return v;// new SortUtils().quickSort(v);
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
        return new SortUtils().quickSort(v);
    }

/*
    public static Vector<String> getSupportedPropertyNames(CodingScheme cs) {
        if (cs == null)
            return null;
        Vector w = getSupportedProperties(cs);
        if (w == null)
            return null;

        Vector<String> v = new Vector<String>();
        for (int i = 0; i < w.size(); i++) {
            SupportedProperty sp = (SupportedProperty) w.elementAt(i);
            v.add(sp.getLocalId());
        }
        return new SortUtils().quickSort(v);
    }
*/
	public static boolean isAnnotationPropertyPCode(String t) {
		if (t == null) return false;
		if (t.length() <= 1) return false;
		if (!t.startsWith("P")) return false;
		for (int i=1; i<t.length(); i++) {
			char c = t.charAt(i);
			if (!Character.isDigit(c)) return false;
		}
		return true;
	}

    public static Vector<String> getSupportedPropertyNames(CodingScheme cs) {
        Vector w = getSupportedProperties(cs);
		if (w == null) return null;
        Vector<String> v = new Vector<String>();
		for (int i=0; i<w.size(); i++)
		{
		     SupportedProperty sp = (SupportedProperty) w.elementAt(i);
		     if (sp.getUri() != null && isAnnotationPropertyPCode(sp.getLocalId())) {
				 if (!sp.getUri().endsWith(sp.getLocalId())) {
					 v.add(sp.getLocalId());
				 }
			 } else {
				 v.add(sp.getLocalId());
			 }
		}
        return new SortUtils().quickSort(v);
	}


    public static Vector<String> getSupportedPropertyNames(
        String codingSchemeName) {
		if (codingSchemeName == null) return null;

        CodingScheme cs = getCodingScheme(codingSchemeName, null);
        return getSupportedPropertyNames(cs);
    }



    public static Vector<String> getSupportedPropertyNames(
        String codingSchemeName, String version) {
		if (codingSchemeName == null) return null;

        CodingScheme cs = getCodingScheme(codingSchemeName, version);
        return getSupportedPropertyNames(cs);
    }

    public static Vector<String> getSupportedAssociationQualifier(
        CodingScheme cs) {
		if (cs == null) return null;

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
        return new SortUtils().quickSort(v);
    }

    public static Vector<String> getSupportedAssociationNames(
        String codingSchemeName) {
		if (codingSchemeName == null) return null;

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


        return new SortUtils().quickSort(v);
    }

    public static Vector getAssociationCodesByNames(String codingScheme,
        String version, Vector associations) {
		if (codingScheme == null) return null;
        LexBIGServiceConvenienceMethodsImpl lbscm = null;
        CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
        if (version != null)
            versionOrTag.setVersion(version);
        Vector w = new Vector();

        try {
            LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
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
                    return w;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new SortUtils().quickSort(w);
    }


    public static String convertAssociationName(String codingScheme,
        String version, String association) {
		if (codingScheme == null) return null;

        LexBIGServiceConvenienceMethodsImpl lbscm = null;
        CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
        if (version != null)
            versionOrTag.setVersion(version);
        //Vector w = new Vector();

        try {
            LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
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
		if (codingSchemeName == null) return null;
        _association_name_vec = new Vector();

        LexBIGServiceConvenienceMethodsImpl lbscm = null;
        try {
            LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
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
				String name = sa.getLocalId();
				try {
					lbscm.getAssociationNameFromAssociationCode(
						codingSchemeName, versionOrTag, sa.getLocalId());
				} catch (Exception ex) {
                    _logger.debug("lbscm.getAssociationNameFromAssociationCode threw exception.");
				}
				_association_name_vec.add(name);

			}
			_association_name_vec = new SortUtils().quickSort(_association_name_vec);
			return _association_name_vec;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
    }


    public static Vector getSupportedAssociationNamesAndIDs(String codingSchemeName, String version) {
		if (codingSchemeName == null) return null;

        _association_name_vec = new Vector();

        LexBIGServiceConvenienceMethodsImpl lbscm = null;
        try {
            LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
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
				String id = sa.getLocalId();
				String name = sa.getContent();
				String t = name + "|" + id;
				if (name != null && name.compareTo("") != 0 && id != null && id.compareTo("") != 0) {
					_association_name_vec.add(name + "|" + id);
				}

			}
			_association_name_vec = new SortUtils().quickSort(_association_name_vec);
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


/*
    public static Vector getSupportedMappingAssociationNamesAndIDs(String codingSchemeName, String version) {
		if (codingSchemeName == null) return null;
        Vector association_name_vec = new Vector();

        CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
        if (version != null)
            csvt.setVersion(version);

		//List list = new ArrayList();
		try {
			LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
			CodingScheme cs = lbSvc.resolveCodingScheme(codingSchemeName, csvt);
			Relations[] relations = cs.getRelations();
			if (relations.length == 0) {
				return association_name_vec;
			}
			for (int i = 0; i < relations.length; i++) {
				Relations relation = relations[i];
				Boolean bool_obj = relation.isIsMapping();
				if (bool_obj == null || bool_obj.equals(Boolean.FALSE)) {
					return association_name_vec;
				} else {
                    AssociationPredicate[] asso_array = relation.getAssociationPredicate();
                    for (int j=0; j<asso_array.length; j++) {
						AssociationPredicate asso = asso_array[j];
						association_name_vec.add(asso.getAssociationName() + "|" + asso.getAssociationName());
					}

				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
        }
        return association_name_vec;
	}
*/

    public static Vector getSupportedMappingAssociationNamesAndIDs(String codingSchemeName, String version) {
		if (codingSchemeName == null) return null;
        Vector association_name_vec = new Vector();


        CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
        if (version != null) {
            csvt.setVersion(version);
		}
		try {
			LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
			CodingScheme cs = lbSvc.resolveCodingScheme(codingSchemeName, csvt);

			if (cs == null) {
				System.out.println("Coding scheme " + codingSchemeName + " not found.");
				return null;
			}
			Relations[] relations = cs.getRelations();
			if (relations.length == 0) {
				return association_name_vec;
			}
			for (int i = 0; i < relations.length; i++) {
				Relations relation = relations[i];
				/*
				Boolean bool_obj = relation.isIsMapping();
				System.out.println("is mapping: " + bool_obj);
				if (bool_obj == null || bool_obj.equals(Boolean.FALSE)) {
					return association_name_vec;
				} else {
                    AssociationPredicate[] asso_array = relation.getAssociationPredicate();
                    for (int j=0; j<asso_array.length; j++) {
						AssociationPredicate asso = asso_array[j];
						association_name_vec.add(asso.getAssociationName() + "|" + asso.getAssociationName());
					}
				}
				*/
				AssociationPredicate[] asso_array = relation.getAssociationPredicate();
				for (int j=0; j<asso_array.length; j++) {
					AssociationPredicate asso = asso_array[j];
				    association_name_vec.add(asso.getAssociationName() + "|" + asso.getAssociationName());
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
        }
        return association_name_vec;
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
