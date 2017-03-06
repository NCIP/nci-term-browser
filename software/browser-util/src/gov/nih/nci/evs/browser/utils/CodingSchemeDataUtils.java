package gov.nih.nci.evs.browser.utils;

import gov.nih.nci.evs.browser.common.*;
import gov.nih.nci.evs.security.SecurityToken;
import gov.nih.nci.system.client.ApplicationServiceProvider;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.Map;
import org.LexGrid.LexBIG.DataModel.Collections.*;
import org.LexGrid.LexBIG.DataModel.Collections.AbsoluteCodingSchemeVersionReferenceList;
import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeTagList;
import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;
import org.LexGrid.LexBIG.DataModel.Collections.ModuleDescriptionList;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Collections.SortOptionList;
import org.LexGrid.LexBIG.DataModel.Core.*;
import org.LexGrid.LexBIG.DataModel.Core.AbsoluteCodingSchemeVersionReference;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.DataModel.Core.types.CodingSchemeVersionStatus;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.ModuleDescription;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.RenderingDetail;
import org.LexGrid.LexBIG.Exceptions.*;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Extensions.Generic.*;
import org.LexGrid.LexBIG.Extensions.Generic.LexBIGServiceConvenienceMethods.*;
import org.LexGrid.LexBIG.Extensions.Generic.SupplementExtension;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;
import org.LexGrid.LexBIG.LexBIGService.*;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.*;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.*;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.LexBIG.caCore.interfaces.LexEVSDistributed;
import org.LexGrid.codingSchemes.*;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.commonTypes.*;
import org.LexGrid.concepts.*;
import org.LexGrid.concepts.Entity;
import org.LexGrid.naming.*;
import org.LexGrid.relations.Relations;
import org.lexgrid.resolvedvalueset.LexEVSResolvedValueSetService;
import org.lexgrid.resolvedvalueset.impl.LexEVSResolvedValueSetServiceImpl;



/**
 * @author EVS Team
 * @version 1.0
 *
 *      Modification history Initial implementation kim.ong@ngc.com
 *
 */


/**
 * The Class CodingSchemeDataUtils.
 */


public class CodingSchemeDataUtils {
	static final String PRODUCTION = "PRODUCTION";
	private static LocalNameList _noopList = new LocalNameList();

    HashMap resovedValueSetHashMap = null;

	LexBIGService lbSvc = null;
	private LexBIGServiceConvenienceMethods lbscm = null;


	public CodingSchemeDataUtils(LexBIGService lbSvc) {
		this.lbSvc = lbSvc;
		try {
			this.lbscm = (LexBIGServiceConvenienceMethods) lbSvc.getGenericExtension("LexBIGServiceConvenienceMethods");
	    } catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	public void setLexBIGService(LexBIGService lbSvc) {
		this.lbSvc = lbSvc;
		try {
			this.lbscm = (LexBIGServiceConvenienceMethods) lbSvc.getGenericExtension("LexBIGServiceConvenienceMethods");
	    } catch (Exception ex) {
			ex.printStackTrace();
		}	}

    public void dumpVector(String label, Vector v) {
		System.out.println("\n" + label + ":");
		if (v == null) return;
		for (int i=0; i<v.size(); i++) {
			String t = (String) v.elementAt(i);
			int j=i+1;
			System.out.println("(" + j + ") " + t);
		}
	}

    public CodingScheme resolveCodingScheme(String codingScheme) {
		return resolveCodingScheme(codingScheme, null);
	}


    public CodingScheme resolveCodingScheme(String codingScheme, String version) {
		CodingScheme cs = null;
		CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
		if (version != null) {
			versionOrTag.setVersion(version);
		}
        try {
			cs = lbSvc.resolveCodingScheme(codingScheme, versionOrTag);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return cs;
    }

    public Vector getSupportedSources(String codingScheme, String version) {
		CodingScheme cs = resolveCodingScheme(codingScheme, version);
		return getSupportedSources(cs) ;
	}

    public Vector getSupportedSources(CodingScheme cs) {
		if (cs == null) return null;
		Vector v = new Vector();
        try {
			SupportedSource[] sources = cs.getMappings().getSupportedSource();
			for (int i=0; i<sources.length; i++)
			{
				v.add(sources[i].getLocalId());
			}
	    } catch (Exception ex) {
			ex.printStackTrace();
		}
		v = SortUtils.quickSort(v);
		return v;
	}

    public Vector<SupportedProperty> getSupportedProperties(CodingScheme cs) {
		if (cs == null) return null;
        Vector<SupportedProperty> v = new Vector<SupportedProperty>();
	    SupportedProperty[] properties = cs.getMappings().getSupportedProperty();
		for (int i=0; i<properties.length; i++)
		{
		     SupportedProperty sp = (SupportedProperty) properties[i];
		     v.add(sp);
		}
        return v;
	}

/*
    public Vector<String> getSupportedPropertyNames(CodingScheme cs) {
        Vector w = getSupportedProperties(cs);
		if (w == null) return null;
        Vector<String> v = new Vector<String>();
		for (int i=0; i<w.size(); i++)
		{
		     SupportedProperty sp = (SupportedProperty) w.elementAt(i);
		     if (sp == null)
		     {
				 System.out.println("SupportedProperty == null");
			 }
			 else if (sp.getLocalId() == null)
			 {
			     System.out.println("SupportedProperty.getLocalId() == null");
			 }

		     v.add(sp.getLocalId());
		}
        return v;
	}
*/

	public boolean isAnnotationPropertyPCode(String t) {
		if (t == null) return false;
		if (t.length() <= 1) return false;
		if (!t.startsWith("P")) return false;
		for (int i=1; i<t.length(); i++) {
			char c = t.charAt(i);
			if (!Character.isDigit(c)) return false;
		}
		return true;
	}

    public Vector<String> getSupportedPropertyNames(CodingScheme cs) {
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
        return SortUtils.quickSort(v);
	}


    public Vector<String> getSupportedPropertyData(CodingScheme cs) {
        Vector w = getSupportedProperties(cs);
		if (w == null) return null;
        Vector<String> v = new Vector<String>();
		for (int i=0; i<w.size(); i++)
		{
		     SupportedProperty sp = (SupportedProperty) w.elementAt(i);
		     v.add(sp.getUri() + "|" + sp.getLocalId() + "|" + sp.getContent() + "|" + sp.getPropertyType());
		}
        return SortUtils.quickSort(v);
	}

    public HashMap getPropertyName2TypeHashMap(CodingScheme cs) {
        Vector w = getSupportedProperties(cs);
		if (w == null) return null;
        HashMap hmap = new HashMap();
		for (int i=0; i<w.size(); i++)
		{
		     SupportedProperty sp = (SupportedProperty) w.elementAt(i);
		     if (sp.getUri() != null) {
				 if (!sp.getUri().endsWith(sp.getLocalId())) {
					 hmap.put(sp.getLocalId(), sp.getPropertyType().toString());
				 }
			 }
		}
        return hmap;
	}

    public Vector getSupportedPropertyQualifier(CodingScheme cs) {
		if (cs == null) return null;
		Vector v = new Vector();
        try {
			SupportedPropertyQualifier[] qualifiers = cs.getMappings().getSupportedPropertyQualifier();
			for (int i=0; i<qualifiers.length; i++)
			{
				v.add(qualifiers[i].getLocalId());
			}
	    } catch (Exception ex) {
			ex.printStackTrace();
		}
		return v;
	}

    public Vector<String> getSupportedAssociationNames(String codingSchemeName, String version) {
        CodingScheme cs = resolveCodingScheme(codingSchemeName, version);
        return getSupportedAssociationNames(cs);
	}


    public Vector<String> getSupportedAssociationNames(CodingScheme cs) {
		if (cs == null) return null;
        Vector<String> v = new Vector<String>();
	    SupportedAssociation[] assos = cs.getMappings().getSupportedAssociation();
		for (int i=0; i<assos.length; i++)
		{
		     SupportedAssociation sa = (SupportedAssociation) assos[i];
		     v.add(sa.getLocalId());
		}
        return v;
	}

	public Vector<String> getSupportedAssociationQualifier(CodingScheme cs) {
        Vector<String> v = new Vector<String>();
        try {
			org.LexGrid.naming.SupportedAssociationQualifier[] supportedAssociationQualifiers
			    = cs.getMappings().getSupportedAssociationQualifier();
			if (supportedAssociationQualifiers == null) return null;
			for (int i=0; i<supportedAssociationQualifiers.length; i++)
			{
				SupportedAssociationQualifier q = supportedAssociationQualifiers[i];
				v.add(q.getLocalId());
			}
			return v;
	    } catch (Exception e) {
			return null;
		}
	}

    public Vector getSupportedEntityType(CodingScheme cs) {
		if (cs == null) return null;
		Vector v = new Vector();
        try {
			SupportedEntityType[] types = cs.getMappings().getSupportedEntityType();
			for (int i=0; i<types.length; i++)
			{
				v.add(types[i].getLocalId());
			}

	    } catch (Exception ex) {
			ex.printStackTrace();
		}
		return v;
	}


	public boolean isResolvedValueSetCodingScheme(CodingScheme cs) {
		if (resovedValueSetHashMap == null) {
			resovedValueSetHashMap = getResolvedValueSetHashMap();
		}
		return resovedValueSetHashMap.containsKey(cs.getCodingSchemeURI());
	}

    public Vector getResolvedValueSetCodingSchemes() {
		Vector w = new Vector();
		try {
			//List<CodingScheme> choices = new ArrayList<CodingScheme>();
			LexEVSResolvedValueSetService lrvs = new LexEVSResolvedValueSetServiceImpl(lbSvc);

			if (lrvs == null) {
				System.out.println("WARNING: lrvs == null???");
				return null;
			}
			List<CodingScheme> schemes = lrvs.listAllResolvedValueSets();
			if (schemes == null) {
				System.out.println("WARNING: lrvs.listAllResolvedValueSets() returns null.");
				return null;
			}
			for (int i = 0; i < schemes.size(); i++) {
				CodingScheme cs = schemes.get(i);
                w.add(cs.getCodingSchemeName() + "|" + cs.getRepresentsVersion() + "|" + cs.getFormalName() + "|" + cs.getCodingSchemeURI());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return w;
    }

    public HashMap getResolvedValueSetHashMap() {
		if (resovedValueSetHashMap != null) return resovedValueSetHashMap;
		HashMap hmap = new HashMap();
		try {
			//List<CodingScheme> choices = new ArrayList<CodingScheme>();
			LexEVSResolvedValueSetService lrvs = new LexEVSResolvedValueSetServiceImpl(lbSvc);
			List<CodingScheme> schemes = lrvs.listAllResolvedValueSets();
			for (int i = 0; i < schemes.size(); i++) {
				CodingScheme cs = schemes.get(i);
				int j = i+1;
				String key = cs.getCodingSchemeURI();
				String name = cs.getCodingSchemeName();
				String value = cs.getRepresentsVersion();
				Vector v = new Vector();
				if (hmap.containsKey(key)) {
					v = (Vector) hmap.get(key);
				}
				v.add(value);
				hmap.put(key, v);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return hmap;
    }

    public boolean isMapping(String scheme, String version) {
        CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
        if (version != null)
            csvt.setVersion(version);

		try {
			MappingExtension mappingExtension = (MappingExtension)
				lbSvc.getGenericExtension("MappingExtension");

            boolean isMappingCS = mappingExtension.isMappingCodingScheme(scheme, csvt);
            Boolean bool_obj = Boolean.valueOf(isMappingCS);

			return isMappingCS;

		} catch (Exception ex) {
            return false;
        }
    }


    public Vector getCodingSchemes() {
		return getCodingSchemes(true);
	}


    public Vector getCodingSchemes(boolean excludeMappings) {
		Vector w = new Vector();
		resovedValueSetHashMap = getResolvedValueSetHashMap();
        boolean includeInactive = false;
        try {
             CodingSchemeRenderingList csrl = null;
             try {
                 csrl = lbSvc.getSupportedCodingSchemes();
             } catch (LBInvocationException ex) {
                 ex.printStackTrace();
                 return null;
             }
             CodingSchemeRendering[] csrs = csrl.getCodingSchemeRendering();
             for (int i = 0; i < csrs.length; i++) {
                 int j = i + 1;
                 CodingSchemeRendering csr = csrs[i];
                 CodingSchemeSummary css = csr.getCodingSchemeSummary();
                 String formalname = css.getFormalName();

                 Boolean isActive = null;

                 if (csr.getRenderingDetail() == null) {
                     //_logger.warn("\tcsr.getRenderingDetail() == null");
                 } else if (csr.getRenderingDetail().getVersionStatus() == null) {
                     //_logger
                     //    .warn("\tcsr.getRenderingDetail().getVersionStatus() == null");
                 } else {
                      isActive =
                         csr.getRenderingDetail().getVersionStatus().equals(
                             CodingSchemeVersionStatus.ACTIVE);
                 }

                 String representsVersion = css.getRepresentsVersion();
                 if ((includeInactive && isActive == null)
                     || (isActive != null && isActive.equals(Boolean.TRUE))
                     || (includeInactive && (isActive != null && isActive
                         .equals(Boolean.FALSE)))) {

                     CodingSchemeVersionOrTag vt =
                         new CodingSchemeVersionOrTag();
                     vt.setVersion(representsVersion);

                     try {
                         CodingScheme cs = lbSvc.resolveCodingScheme(formalname, vt);
                         if (excludeMappings) {
							 if (!isResolvedValueSetCodingScheme(cs) && !isMapping(cs.getCodingSchemeName(), representsVersion)) {
								  w.add(cs.getCodingSchemeName() + "|" + representsVersion + "|" + cs.getFormalName() + "|" + cs.getCodingSchemeURI());
							 }
					     } else {
							 if (!isResolvedValueSetCodingScheme(cs)) {
								  w.add(cs.getCodingSchemeName() + "|" + representsVersion + "|" + cs.getFormalName() + "|" + cs.getCodingSchemeURI());
							 }
						 }

                     } catch (Exception ex) {
                         ex.printStackTrace();
                     }
                  }
             }
         } catch (Exception e) {
             e.printStackTrace();
         }
 	     w = SortUtils.quickSort(w);
 	     return w;
    }

    public Vector getMappingCodingSchemes() {
		Vector w = new Vector();
		//resovedValueSetHashMap = getResolvedValueSetHashMap();
        boolean includeInactive = false;
        try {
             CodingSchemeRenderingList csrl = null;
             try {
                 csrl = lbSvc.getSupportedCodingSchemes();
             } catch (LBInvocationException ex) {
                 ex.printStackTrace();
                 return null;
             }
             CodingSchemeRendering[] csrs = csrl.getCodingSchemeRendering();
             for (int i = 0; i < csrs.length; i++) {
                 int j = i + 1;
                 CodingSchemeRendering csr = csrs[i];
                 CodingSchemeSummary css = csr.getCodingSchemeSummary();
                 String formalname = css.getFormalName();

                 Boolean isActive = null;

                 if (csr.getRenderingDetail() == null) {
                     //_logger.warn("\tcsr.getRenderingDetail() == null");
                 } else if (csr.getRenderingDetail().getVersionStatus() == null) {
                     //_logger
                     //    .warn("\tcsr.getRenderingDetail().getVersionStatus() == null");
                 } else {
                      isActive =
                         csr.getRenderingDetail().getVersionStatus().equals(
                             CodingSchemeVersionStatus.ACTIVE);
                 }

                 String representsVersion = css.getRepresentsVersion();
                 if ((includeInactive && isActive == null)
                     || (isActive != null && isActive.equals(Boolean.TRUE))
                     || (includeInactive && (isActive != null && isActive
                         .equals(Boolean.FALSE)))) {

                     CodingSchemeVersionOrTag vt =
                         new CodingSchemeVersionOrTag();
                     vt.setVersion(representsVersion);

                     try {
                         CodingScheme cs = lbSvc.resolveCodingScheme(formalname, vt);
						 if (isMapping(cs.getCodingSchemeName(), representsVersion)) {
						     w.add(cs.getCodingSchemeName() + "|" + representsVersion + "|" + cs.getFormalName() + "|" + cs.getCodingSchemeURI());
						 }

                     } catch (Exception ex) {
                         ex.printStackTrace();
                     }
                  }
             }
         } catch (Exception e) {
             e.printStackTrace();
         }
 	     w = SortUtils.quickSort(w);
 	     return w;
    }


    public String getVocabularyVersionByTag(String codingSchemeName, String ltag) {
		if (codingSchemeName == null) return null;
        String version = null;
        int knt = 0;
        try {
            CodingSchemeRenderingList lcsrl = lbSvc.getSupportedCodingSchemes();
            CodingSchemeRendering[] csra = lcsrl.getCodingSchemeRendering();
            for (int i = 0; i < csra.length; i++) {
                CodingSchemeRendering csr = csra[i];
                CodingSchemeSummary css = csr.getCodingSchemeSummary();
                if ((css.getFormalName() != null && css.getFormalName().compareTo(codingSchemeName) == 0)
                    || (css.getLocalName() != null && css.getLocalName().compareTo(codingSchemeName) == 0)
                    || (css.getCodingSchemeURI() != null && css.getCodingSchemeURI().compareTo(codingSchemeName) == 0)) {
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
        if (ltag != null && ltag.compareToIgnoreCase(PRODUCTION) == 0 & knt == 1) {
            return version;
        }
        return null;
    }

    public CodingScheme getCodingScheme(String codingScheme, String version) throws LBException {
        CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
        if (version != null) {
			versionOrTag.setVersion(version);
		}
		return getCodingScheme(codingScheme, versionOrTag);
	}

    public CodingScheme getCodingScheme(String codingScheme,
        CodingSchemeVersionOrTag versionOrTag) throws LBException {
        CodingScheme cs = null;
        try {
            cs = lbSvc.resolveCodingScheme(codingScheme, versionOrTag);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cs;
    }

    public String[] getHierarchyIDs(String codingScheme,
        CodingSchemeVersionOrTag versionOrTag) throws LBException {

        String[] hier = null;
        Set<String> ids = new HashSet<String>();
        SupportedHierarchy[] sh = null;
        try {
            sh = getSupportedHierarchies(codingScheme, versionOrTag);
            if (sh != null) {
                for (int i = 0; i < sh.length; i++) {
                    ids.add(sh[i].getLocalId());
                }
                hier = ids.toArray(new String[ids.size()]);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return hier;
    }


    public String getHierarchyID(String codingScheme, String version) {
        CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
        if (version != null)
            versionOrTag.setVersion(version);
        try {
            String[] ids = getHierarchyIDs(codingScheme, versionOrTag);
            if (ids.length > 0)
                return ids[0];
        } catch (Exception e) {

        }
        return null;
    }



    public ResolvedConceptReferenceList getHierarchyRoots(
        String codingScheme, String version) {
        CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
        if (version != null)
            versionOrTag.setVersion(version);
        try {
            LexBIGServiceConvenienceMethods lbscm =
                (LexBIGServiceConvenienceMethods) lbSvc
                    .getGenericExtension("LexBIGServiceConvenienceMethods");

            lbscm.setLexBIGService(lbSvc);
            String hierarchyID = getHierarchyID(codingScheme, version);

            ResolvedConceptReferenceList rcrl = lbscm.getHierarchyRoots(codingScheme, versionOrTag,
                hierarchyID);

            return rcrl;

        } catch (Exception ex) {
			ex.printStackTrace();
            return null;
        }
    }

    protected SupportedHierarchy[] getSupportedHierarchies(
        String codingScheme, CodingSchemeVersionOrTag versionOrTag)
            throws LBException {

        CodingScheme cs = null;
        try {
            cs = getCodingScheme(codingScheme, versionOrTag);
        } catch (Exception ex) {

        }
        if (cs == null) {
            throw new LBResourceUnavailableException(
                "Coding scheme not found -- " + codingScheme);
        }
        Mappings mappings = cs.getMappings();
        return mappings.getSupportedHierarchy();
    }

    public Vector getRoots(String codingScheme, String version) {
        ResolvedConceptReferenceList rcrl = getHierarchyRoots(
             codingScheme, version);

        if (rcrl == null) {
		    System.out.println("Unable to find roots for "  + codingScheme + " (version: " + version + ")");
		    return null;
		}

        Vector v = new Vector();
        for(int i=0; i<rcrl.getResolvedConceptReferenceCount(); i++) {
			ResolvedConceptReference rcr = rcrl.getResolvedConceptReference(i);
			v.add(rcr);
		}

		v = SortUtils.quickSort(v);
		return v;
    }

    public Vector getNamespaceNames(String codingScheme, String version) {
		Vector v = new Vector();
		CodingScheme cs = resolveCodingScheme(codingScheme, version);
		if (cs != null) {
			Mappings mapping = cs.getMappings();
			if (mapping != null) {
				SupportedNamespace[] namespaces =
					mapping.getSupportedNamespace();
				if (namespaces != null) {
					for (int j = 0; j < namespaces.length; j++) {
						SupportedNamespace ns = namespaces[j];
						if (ns != null) {
							java.lang.String ns_name =
								ns.getEquivalentCodingScheme();
							java.lang.String ns_id =
								ns.getContent();
							if (ns_id != null && ns_id.compareTo("") != 0) {
								v.add(ns_id);
							}
						}
					}
				}
			}
		} else {
			System.out.println("??? Unable to resolveCodingScheme "
				+ codingScheme);
		}
		return v;
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

	public ResolvedConceptReferencesIterator resolveCodingScheme(String cs_uri, String version, boolean resolveObjects) {
	    ResolvedConceptReferencesIterator itr = null;
 		try {
            if (lbSvc == null) {
                return itr;
            }
			CodingSchemeVersionOrTag vt = new CodingSchemeVersionOrTag();
			if (version != null) {
				vt.setVersion(version);
			}
			CodingScheme cs = lbSvc.resolveCodingScheme(cs_uri, vt);
			// [NCITERM-747] Value sets with same name, but different URIs should have values.
			//String cs_name = cs.getCodingSchemeName();
			String cs_name = cs.getCodingSchemeURI();
			CodedNodeSet cns = null;
			try {
				try {
					cns = getNodeSet(cs_name, vt);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (cns != null) {
					try {
						SortOptionList sortOptions = null;
						LocalNameList filterOptions = null;
						LocalNameList propertyNames = null;
						CodedNodeSet.PropertyType[] propertyTypes = null;
						itr = cns.resolve(sortOptions, filterOptions, propertyNames, propertyTypes, resolveObjects);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return itr;
	}

    public String getVocabularyVersionTag(String codingSchemeName, String version) {
        if (codingSchemeName == null)
            return null;
        try {
            CodingSchemeRenderingList lcsrl = lbSvc.getSupportedCodingSchemes();
            CodingSchemeRendering[] csra = lcsrl.getCodingSchemeRendering();
            for (int i = 0; i < csra.length; i++) {
                CodingSchemeRendering csr = csra[i];
                CodingSchemeSummary css = csr.getCodingSchemeSummary();

                if ((css.getFormalName() != null && css.getFormalName().compareTo(codingSchemeName) == 0)
                    || (css.getLocalName() != null && css.getLocalName().compareTo(codingSchemeName) == 0)
                    || (css.getCodingSchemeURI() != null && css.getCodingSchemeURI().compareTo(codingSchemeName) == 0)) {

					if (version == null) return Constants.PRODUCTION;

					String representsVersion = css.getRepresentsVersion();
                    if (representsVersion.compareTo(version) == 0) {
						RenderingDetail rd = csr.getRenderingDetail();
						CodingSchemeTagList cstl = rd.getVersionTags();
						String tag_str = "";
						java.lang.String[] tags = cstl.getTag();
						if (tags == null)
							return "NOT ASSIGNED";
						if (tags.length > 0) {
							tag_str = "";
							for (int j = 0; j < tags.length; j++) {
								String version_tag = (String) tags[j];
								if (j == 0) {
									tag_str = version_tag;
								} else if (j == tags.length-1) {
									tag_str = tag_str + version_tag;
								} else {
									tag_str = tag_str + version_tag + "|";
								}
							}
						} else {
							return "<NOT ASSIGNED>";
						}
						return tag_str;
					}
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "<NOT AVAILABLE>";
    }

    public Vector getCodingSchemeVersionsByURN(String urn) {
        try {
			Vector v = new Vector();
            CodingSchemeRenderingList csrl = null;
            try {
                csrl = lbSvc.getSupportedCodingSchemes();
            } catch (LBInvocationException ex) {
                ex.printStackTrace();
                return null;
            }
            CodingSchemeRendering[] csrs = csrl.getCodingSchemeRendering();
            for (int i = 0; i < csrs.length; i++) {
                int j = i + 1;
                CodingSchemeRendering csr = csrs[i];
                CodingSchemeSummary css = csr.getCodingSchemeSummary();
                Boolean isActive =
                        csr.getRenderingDetail().getVersionStatus().equals(
                            CodingSchemeVersionStatus.ACTIVE);

                if (isActive != null && isActive.equals(Boolean.TRUE)) {
                	String uri = css.getCodingSchemeURI();
                	if (uri != null && uri.compareTo(urn) == 0) {
						String representsVersion = css.getRepresentsVersion();
						v.add(representsVersion);
					}
				}
			}
			return v;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

    public Vector getCodingSchemeVersionsByURN(String urn, String tag) {
        try {
			Vector v = new Vector();
            CodingSchemeRenderingList csrl = null;
            try {
                csrl = lbSvc.getSupportedCodingSchemes();
            } catch (LBInvocationException ex) {
                ex.printStackTrace();
                return null;
            }
            CodingSchemeRendering[] csrs = csrl.getCodingSchemeRendering();
            for (int i = 0; i < csrs.length; i++) {
                int j = i + 1;
                CodingSchemeRendering csr = csrs[i];
                CodingSchemeSummary css = csr.getCodingSchemeSummary();
                Boolean isActive =
                        csr.getRenderingDetail().getVersionStatus().equals(
                            CodingSchemeVersionStatus.ACTIVE);

                if (isActive != null && isActive.equals(Boolean.TRUE)) {
                	String uri = css.getCodingSchemeURI();
                	if (uri.compareTo(urn) == 0) {
						String representsVersion = css.getRepresentsVersion();

						if (tag != null) {
							String cs_tag = getVocabularyVersionTag(uri, representsVersion);
							if (cs_tag != null && cs_tag.compareToIgnoreCase(tag) == 0) {
								v.add(representsVersion);
							}
						} else {
							v.add(representsVersion);
						}
					}
				}
			}
			return v;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public boolean isExtension(String codingScheme, String version) {
		CodingSchemeVersionOrTag tagOrVersion = new CodingSchemeVersionOrTag();
		if (version != null) tagOrVersion.setVersion(version);
		try {
			SupplementExtension supplementExtension =
				(SupplementExtension) lbSvc.getGenericExtension("SupplementExtension");

			return supplementExtension.isSupplement(codingScheme, tagOrVersion);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

    public Vector<String> getVersionListData(String codingSchemeName) {
        Vector<String> v = new Vector();
        try {
            CodingSchemeRenderingList csrl = lbSvc.getSupportedCodingSchemes();
            if (csrl == null) {
                return v;
			}
            CodingSchemeRendering[] csrs = csrl.getCodingSchemeRendering();
            for (int i = 0; i < csrs.length; i++) {
                CodingSchemeRendering csr = csrs[i];
                Boolean isActive =
                    csr.getRenderingDetail().getVersionStatus().equals(
                        CodingSchemeVersionStatus.ACTIVE);
                CodingSchemeSummary css = csr.getCodingSchemeSummary();
                String formalname = css.getFormalName();
                if (formalname.compareTo(codingSchemeName) == 0 || css.getCodingSchemeURI().compareTo(codingSchemeName) == 0
                    || css.getLocalName().compareTo(codingSchemeName) == 0) {
                    String representsVersion = css.getRepresentsVersion();
                    v.add(representsVersion);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return v;
    }

    public CodingSchemeVersionOrTag createCodingSchemeVersionOrTag(String version) {
		CodingSchemeVersionOrTag vt = new CodingSchemeVersionOrTag();
		if (version != null) {
			vt.setVersion(version);
	    }
	    return vt;
	}

    public List getSupportedRoleNames(String scheme, String version) {
        CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
        if (version != null) {
            csvt.setVersion(version);
		}

        List list = new ArrayList();
        try {
            CodingScheme cs = lbSvc.resolveCodingScheme(scheme, csvt);
            Relations[] relations = cs.getRelations();
            for (int i = 0; i < relations.length; i++) {
                Relations relation = relations[i];
                if (relation.getContainerName().compareToIgnoreCase("roles") == 0
                    || relation.getContainerName().compareToIgnoreCase(
                        "relations") == 0) {
                    org.LexGrid.relations.AssociationPredicate[] asso_array =
                        relation.getAssociationPredicate();
                    for (int j = 0; j < asso_array.length; j++) {
                        org.LexGrid.relations.AssociationPredicate association =
                            (org.LexGrid.relations.AssociationPredicate) asso_array[j];
                        list.add(association.getAssociationName());
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public HashMap createListOfCodingSchemeVersionsUsedInResolutionHashMap() {
		HashMap listOfCodingSchemeVersionsUsedInResolutionHashMap = new HashMap();
        try {
            CodingSchemeRenderingList csrl = null;
            try {
                csrl = lbSvc.getSupportedCodingSchemes();
            } catch (LBInvocationException ex) {
                ex.printStackTrace();
                return null;
            }

            CodingSchemeRendering[] csrs = csrl.getCodingSchemeRendering();
            for (int i = 0; i < csrs.length; i++) {
                int j = i + 1;
                CodingSchemeRendering csr = csrs[i];
                CodingSchemeSummary css = csr.getCodingSchemeSummary();
                String formalname = css.getFormalName();

                Boolean isActive = null;
                if (csr.getRenderingDetail() == null) {

                } else if (csr.getRenderingDetail().getVersionStatus() == null) {

                } else {
                    isActive =
                        csr.getRenderingDetail().getVersionStatus().equals(
                            CodingSchemeVersionStatus.ACTIVE);
                }
                String representsVersion = css.getRepresentsVersion();

				CodingSchemeVersionOrTag vt =
					new CodingSchemeVersionOrTag();
				vt.setVersion(representsVersion);
				try {
					CodingScheme cs = lbSvc.resolveCodingScheme(formalname, vt);
					if (isResolvedValueSetCodingScheme(cs)) {
						String cs_uri = cs.getCodingSchemeURI();
						String cs_name = cs.getCodingSchemeName();

						HashMap hmap = new HashMap();
						AbsoluteCodingSchemeVersionReferenceList acsvr = getListOfCodingSchemeVersionsUsedInResolution(cs_name);
						if (acsvr != null) {
							for(AbsoluteCodingSchemeVersionReference abrefs :acsvr.getAbsoluteCodingSchemeVersionReference()){
								hmap.put(abrefs.getCodingSchemeURN(),  abrefs.getCodingSchemeVersion());
							}
							listOfCodingSchemeVersionsUsedInResolutionHashMap.put(cs_name, hmap);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return listOfCodingSchemeVersionsUsedInResolutionHashMap;
    }

	public AbsoluteCodingSchemeVersionReferenceList getListOfCodingSchemeVersionsUsedInResolution(String codingScheme) {
        try {
            CodingScheme scheme = lbSvc.resolveCodingScheme(codingScheme, null);
            LexEVSResolvedValueSetService service = new LexEVSResolvedValueSetServiceImpl(lbSvc);
            if (service != null) {
				AbsoluteCodingSchemeVersionReferenceList acsvr = service.getListOfCodingSchemeVersionsUsedInResolution(scheme);
				return acsvr;
		    }

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public Vector getConceptDomainNames() {
		String scheme = "conceptDomainCodingScheme";
		String version = null;
        CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
		Vector conceptDomainName_vec = new Vector();
		try {
			LocalNameList entityTypes = new LocalNameList();
			entityTypes.addEntry("conceptDomain");

			CodedNodeSet cns = lbSvc.getNodeSet(scheme, csvt, entityTypes);

			SortOptionList sortOptions = null;
			LocalNameList filterOptions = null;
			LocalNameList propertyNames = null;
			CodedNodeSet.PropertyType[] propertyTypes = null;
			boolean resolveObjects = true;
			int maxToReturn = 1000;
            ResolvedConceptReferenceList rcrl = cns.resolveToList(sortOptions, filterOptions, propertyNames, propertyTypes, resolveObjects, maxToReturn);

            for (int i=0; i<rcrl.getResolvedConceptReferenceCount(); i++) {
				ResolvedConceptReference rcr = rcrl.getResolvedConceptReference(i);
				Entity entity = rcr.getReferencedEntry();
				if (entity.getEntityDescription() != null) {
					conceptDomainName_vec.add(entity.getEntityDescription().getContent());
				} else {
					conceptDomainName_vec.add("");
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		conceptDomainName_vec = SortUtils.quickSort(conceptDomainName_vec);
		return conceptDomainName_vec;
	}

    public Boolean isCodingSchemeAvailable(String cs_name) {
        try {
			CodingScheme cs = lbSvc.resolveCodingScheme(cs_name, null);
			if (cs != null) {
				return Boolean.TRUE;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return Boolean.FALSE;
    }

    public Vector<String> getPropertyQualifierListData(
        String codingSchemeName, String version) {
        CodingSchemeVersionOrTag vt = new CodingSchemeVersionOrTag();
        if (version != null) {
            vt.setVersion(version);
        }
        CodingScheme scheme = null;
        try {
            scheme = lbSvc.resolveCodingScheme(codingSchemeName, vt);
            if (scheme == null)
                return null;
            Vector<String> propertyQualifierListData = new Vector<String>();
            SupportedPropertyQualifier[] qualifiers =
                scheme.getMappings().getSupportedPropertyQualifier();
            for (int i = 0; i < qualifiers.length; i++) {
                SupportedPropertyQualifier qualifier = qualifiers[i];
                propertyQualifierListData.add(qualifier.getLocalId());
            }

            return propertyQualifierListData;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Vector<String> getRepresentationalFormListData(
        String codingSchemeName, String version) {
        CodingSchemeVersionOrTag vt = new CodingSchemeVersionOrTag();
        if (version != null) {
            vt.setVersion(version);
        }
        CodingScheme scheme = null;
        try {
            scheme = lbSvc.resolveCodingScheme(codingSchemeName, vt);
            if (scheme == null)
                return null;
            Vector<String> propertyNameListData = new Vector<String>();
            SupportedRepresentationalForm[] forms =
                scheme.getMappings().getSupportedRepresentationalForm();
            if (forms != null) {
                for (int i = 0; i < forms.length; i++) {
                    SupportedRepresentationalForm form = forms[i];
                    propertyNameListData.add(form.getLocalId());
                }
            }
            return propertyNameListData;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Vector<String> getSourceListData(String codingSchemeName, String version) {
        CodingSchemeVersionOrTag vt = new CodingSchemeVersionOrTag();
        if (version != null) {
            vt.setVersion(version);
        }
        CodingScheme scheme = null;
        try {
            scheme = lbSvc.resolveCodingScheme(codingSchemeName, vt);
            if (scheme == null)
                return null;
            Vector<String> sourceListData = new Vector<String>();
            SupportedSource[] sources =
                scheme.getMappings().getSupportedSource();
            for (int i = 0; i < sources.length; i++) {
                SupportedSource source = sources[i];
                sourceListData.add(source.getLocalId());
            }

            return sourceListData;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public boolean hasSourceCodeQualifier(String scheme) {
		return hasQualifier(scheme, "source-code");
	}


    public boolean hasQualifier(String scheme, String qualifier) {
        CodingScheme cs = resolveCodingScheme(scheme);
        if (cs == null) return false;

		Vector v = getSupportedPropertyQualifier(cs);
		if (v == null) return false;
		return v.contains(qualifier);
	}

    public String getMappingSourceAndTarget(String scheme, String version) {
		CodingScheme cs = resolveCodingScheme(scheme, version);
		if (cs == null) return null;
		Relations[] relations = cs.getRelations();
		if (relations == null) return null;
		Relations relation = cs.getRelations(0);

		Boolean bool_obj = relation.isIsMapping();
		if (bool_obj == null || bool_obj.equals(Boolean.FALSE)) {
			return null;
		}

		return relation.getSourceCodingScheme() + "|"
		      + relation.getSourceCodingSchemeVersion() + "|"
		      + relation.getTargetCodingScheme() + "|"
		      + relation.getTargetCodingSchemeVersion();
	}


	public HashMap updateCodeNamespace2VersionMap(HashMap hmap, String codeNamespace) {
		if (hmap == null) {
			hmap = new HashMap();
		}
		if (codeNamespace == null) {
		    return hmap;
		}

		if (hmap.containsKey(codeNamespace)) {
			return hmap;
		}

		String version = getVocabularyVersionByTag(codeNamespace, "PRODUCTION");
		hmap.put(codeNamespace, version);
		return hmap;
	}

    public boolean isBlank(String str) {
        if ((str == null) || str.matches("^\\s*$")) {
            return true;
        } else {
            return false;
        }
    }

    protected  String getDirectionalLabel(
        LexBIGServiceConvenienceMethods lbscm, String scheme,
        CodingSchemeVersionOrTag csvt, Association assoc, boolean navigatedFwd)
            throws LBException {

        String assocLabel =
            navigatedFwd ? lbscm.getAssociationForwardName(assoc
                .getAssociationName(), scheme, csvt) : lbscm
                .getAssociationReverseName(assoc.getAssociationName(), scheme,
                    csvt);
        // if (StringUtils.isBlank(assocLabel))
        if (isBlank(assocLabel))
            assocLabel =
                (navigatedFwd ? "" : "[Inverse]") + assoc.getAssociationName();
        return assocLabel;
    }

    public Vector getRoleData(String scheme, String version) {
		Vector w = new Vector();
        CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
        if (version != null) {
            versionOrTag.setVersion(version);
		}
		java.lang.String relationContainerName = null;
        NameAndValueList association = null;
        NameAndValueList associationQualifiers = null;
        Boolean restrictToAnonymous = Boolean.FALSE;

        String[] associationsToNavigate = new String[2];
        associationsToNavigate[0] = "Role_Has_Domain";
        associationsToNavigate[1] = "Role_Has_Range";
        boolean associationsNavigatedFwd = true;
        LocalNameList _noopList = new LocalNameList();

        try {
			CodedNodeGraph cng = lbSvc.getNodeGraph(scheme, versionOrTag, null);
			cng = cng.restrictToAnonymous(restrictToAnonymous);
			cng = cng.restrictToAssociations(Constructors
					.createNameAndValueList(associationsToNavigate), null);
			ConceptReference focus = null;
			ResolvedConceptReferenceList branch = null;
            try {
                branch =
                    cng.resolveAsList(focus,
                        associationsNavigatedFwd,
                        !associationsNavigatedFwd, -1, 1, _noopList, null,
                        null, null, -1, false);

            } catch (Exception e) {
				e.printStackTrace();
                return null;
            }
			System.out.println(branch.getResolvedConceptReferenceCount());
            for (Iterator<? extends ResolvedConceptReference> nodes =
                branch.iterateResolvedConceptReference(); nodes.hasNext();) {
                ResolvedConceptReference node = nodes.next();
                AssociationList childAssociationList = null;
                if (associationsNavigatedFwd) {
                    childAssociationList = node.getSourceOf();
                } else {
                    childAssociationList = node.getTargetOf();
                }

                StringBuffer buf = new StringBuffer();
                buf.append(node.getEntityDescription().getContent()).append("|");

                if (childAssociationList != null) {
                    for (Iterator<? extends Association> pathsToChildren =
                        childAssociationList.iterateAssociation(); pathsToChildren
                        .hasNext();) {
                        Association child = pathsToChildren.next();

                        String childNavText =
                            getDirectionalLabel(lbscm, scheme, versionOrTag, child,
                                associationsNavigatedFwd);
                        buf.append(childNavText).append("|");
                        AssociatedConceptList branchItemList =
                            child.getAssociatedConcepts();

                        List child_list = new ArrayList();
                        for (Iterator<? extends AssociatedConcept> branchNodes =
                            branchItemList.iterateAssociatedConcept(); branchNodes
                            .hasNext();) {
                            AssociatedConcept branchItemNode =
                                branchNodes.next();
                            child_list.add(branchItemNode);
                        }

                        SortUtils.quickSort(child_list);

                        for (int i = 0; i < child_list.size(); i++) {
                            AssociatedConcept branchItemNode =
                                (AssociatedConcept) child_list.get(i);
                            String branchItemCode =
                                branchItemNode.getConceptCode();
                            String branchItemText =  branchItemNode.getEntityDescription().getContent();
                            buf.append(branchItemText).append("|");
                        }
                        //System.out.println(role_data);
                    }
					String role_data = buf.toString();
					role_data = role_data.substring(0, role_data.length()-1);
                    w.add(role_data);
                }
            }

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return w;
	}

    public String getEquivalenceExpression(String scheme, String version, String code)
            throws LBException {
		String expression = null;
		CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
		if (version != null) {
			csvt.setVersion(version);
		}

        ResolvedConceptReferenceList matches = lbSvc.getNodeGraph(scheme, csvt, null).resolveAsList(
                ConvenienceMethods.createConceptReference(code, scheme), true, false, 1, 1, new LocalNameList(), null,
                null, -1);

        // Analyze the result ...
        if (matches.getResolvedConceptReferenceCount() > 0) {
            Enumeration<? extends ResolvedConceptReference> refEnum = matches.enumerateResolvedConceptReference();

            while (refEnum.hasMoreElements()) {
                ResolvedConceptReference ref = refEnum.nextElement();
                AssociationList sourceof = ref.getSourceOf();
                Association[] associations = sourceof.getAssociation();

                for (int i = 0; i < associations.length; i++) {
                    Association assoc = associations[i];
                    String assoName = assoc.getAssociationName();
                    if (assoName.compareTo("equivalentClass") == 0) {
						AssociatedConcept[] acl = assoc.getAssociatedConcepts().getAssociatedConcept();
						for (int j = 0; j < acl.length; j++) {
							AssociatedConcept ac = acl[j];
							String rela = replaceAssociationNameByRela(ac, assoc.getAssociationName());
							EntityDescription ed = ac.getEntityDescription();
							//expression = code + " --> (" + rela + ") --> " + ac.getConceptCode() + " " + ed.getContent();
							expression = ed.getContent();
                            break;
						}
					}
                }
            }
        }
        return expression;
    }

    private String replaceAssociationNameByRela(AssociatedConcept ac, String associationName) {
		if (ac.getAssociationQualifiers() == null) return associationName;
		if (ac.getAssociationQualifiers().getNameAndValue() == null) return associationName;

		for(NameAndValue qual : ac.getAssociationQualifiers().getNameAndValue()){
			String qualifier_name = qual.getName();
			String qualifier_value = qual.getContent();
			if (qualifier_name.compareToIgnoreCase("rela") == 0) {
				return qualifier_value; // replace associationName by Rela value
			}
		}
		return associationName;
	}


    public Vector resolveIterator(ResolvedConceptReferencesIterator iterator, int maxToReturn) {
        Vector v = new Vector();
        if (iterator == null) {
            return v;
        }
        try {
            int iteration = 0;
            while (iterator.hasNext()) {
                iteration++;
                iterator = iterator.scroll(maxToReturn);
                ResolvedConceptReferenceList rcrl = iterator.getNext();
                ResolvedConceptReference[] rcra =
                    rcrl.getResolvedConceptReference();
                for (int i = 0; i < rcra.length; i++) {
                    ResolvedConceptReference rcr = rcra[i];
				    String t = rcr.getEntityDescription().getContent() + "|"+ rcr.getCode() + "|"+ rcr.getCodingSchemeName()
					+ "|" + rcr.getCodeNamespace();
                    v.add(t);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }

    public Vector resolve(String scheme, String version) {
		if (version == null) {
			version = getVocabularyVersionByTag(scheme, Constants.PRODUCTION);
		}
		return resolve(scheme, version, 250);
	}

    public Vector resolve(String scheme, String version, int maxToReturn) {
		boolean resolveObjects = false;
		ResolvedConceptReferencesIterator iterator = resolveCodingScheme(scheme, version, resolveObjects);
        return resolveIterator(iterator, maxToReturn);
    }

    public Vector getCodesInCodingScheme(String scheme, String version) {
		Vector data_vec = resolve(scheme, version);
		Vector codes = new Vector();
		for (int i=0; i<data_vec.size(); i++) {
			String line = (String) data_vec.elementAt(i);
			Vector u = gov.nih.nci.evs.browser.utils.StringUtils.parseData(line);
			String code = (String) u.elementAt(1);
			codes.add(code);
		}
		return codes;
	}

	 public String getPropertyValues(Entity node) {
		//long ms = System.currentTimeMillis();
        if (node == null) return null;
        String line = "";
        String qualifier_name = null;
        String qualifier_value = null;
        line = line + node.getEntityCode() + "|";
        line = line + node.getEntityCodeNamespace() + "|";
		Presentation[] prsentations = node.getPresentation();
		for (int i = 0; i < prsentations.length; i++) {
			StringBuffer buf = new StringBuffer();
			Presentation presentation = prsentations[i];
			String name = presentation.getPropertyName();
			String value = presentation.getValue().getContent();
			String representationalForm = presentation.getRepresentationalForm();
            buf.append("name").append("$");
            buf.append(name).append("$");
            buf.append(value).append("$");
            buf.append("form=").append(representationalForm).append("$");
            PropertyQualifier[] qualifiers = presentation.getPropertyQualifier();
			if (qualifiers != null) {
				for (int j = 0; j < qualifiers.length; j++) {
					PropertyQualifier q = qualifiers[j];
					qualifier_name = q.getPropertyQualifierName();
					qualifier_value = q.getValue().getContent();
					buf.append(qualifier_name).append("=").append(qualifier_value).append("$");
				}
			}
			Source[] sources = presentation.getSource();
			String src = null;
			for (int k=0; k<sources.length; k++) {
				src =  sources[k].getContent().toString();
				buf.append("source").append("=").append(src).append("$");
		    }
		    String t = buf.toString();
		    if (t.length() > 0) {
				t = t.substring(0, t.length()-1);
			}
		    line = line + t + "|";
		}
		Definition[] definitions = node.getDefinition();
		for (int i = 0; i < definitions.length; i++) {
			Definition definition = definitions[i];
			StringBuffer buf = new StringBuffer();
			String name = definition.getPropertyName();
			String value = definition.getValue().getContent();
            buf.append("definition").append("$");
            buf.append(name).append("$");
            buf.append(value).append("$");
            PropertyQualifier[] qualifiers = definition.getPropertyQualifier();
			if (qualifiers != null) {
				for (int j = 0; j < qualifiers.length; j++) {
					PropertyQualifier q = qualifiers[j];
					qualifier_name = q.getPropertyQualifierName();
					qualifier_value = q.getValue().getContent();
					buf.append(qualifier_name).append("=").append(qualifier_value).append("$");
				}
			}
			Source[] sources = definition.getSource();
			String src = null;
			for (int k=0; k<sources.length; k++) {
				src =  sources[k].getContent().toString();
				buf.append("source").append("=").append(src).append("$");
		    }
		    String t = buf.toString();
		    if (t.length() > 0) {
				t = t.substring(0, t.length()-1);
			}
		    line = line + t + "|";
		}

		Comment[] comments = node.getComment();
		for (int i = 0; i < comments.length; i++) {
			Comment comment = comments[i];
			StringBuffer buf = new StringBuffer();
			String name = comment.getPropertyName();
			String value = comment.getValue().getContent();
            buf.append("comment").append("$");
            buf.append(name).append("$");
            buf.append(value).append("$");
            PropertyQualifier[] qualifiers = comment.getPropertyQualifier();
			if (qualifiers != null) {
				for (int j = 0; j < qualifiers.length; j++) {
					PropertyQualifier q = qualifiers[j];
					qualifier_name = q.getPropertyQualifierName();
					qualifier_value = q.getValue().getContent();
					buf.append(qualifier_name).append("=").append(qualifier_value).append("$");
				}
			}
			Source[] sources = comment.getSource();
			String src = null;
			for (int k=0; k<sources.length; k++) {
				src =  sources[k].getContent().toString();
				buf.append("source").append("=").append(src).append("$");
		    }
		    String t = buf.toString();
		    if (t.length() > 0) {
				t = t.substring(0, t.length()-1);
			}
		    line = line + t + "|";
		}


		Property[] properties = node.getProperty();
		for (int i = 0; i < properties.length; i++) {
			Property property = properties[i];
			StringBuffer buf = new StringBuffer();
			String prop_name = property.getPropertyName();
			String prop_value = property.getValue().getContent();
            buf.append("property").append("$");
            buf.append(prop_name).append("$");
            buf.append(prop_value).append("$");
            PropertyQualifier[] qualifiers = property.getPropertyQualifier();
			if (qualifiers != null) {
				for (int j = 0; j < qualifiers.length; j++) {
					PropertyQualifier q = qualifiers[j];
					qualifier_name = q.getPropertyQualifierName();
					qualifier_value = q.getValue().getContent();
					buf.append(qualifier_name).append("=").append(qualifier_value).append("$");
				}
			}
			Source[] sources = property.getSource();
			String src = null;
			for (int k=0; k<sources.length; k++) {
				src =  sources[k].getContent().toString();
				buf.append("source").append("=").append(src).append("$");
		    }
		    String t = buf.toString();
		    if (t.length() > 0) {
				t = t.substring(0, t.length()-1);
			}
		    line = line + t + "|";
		}
		if (line.length() > 0) {
        	line = line.substring(0, line.length()-1);
		}
		//System.out.println("Total getPropertyValues run time (ms): " + (System.currentTimeMillis() - ms));
		return line;
	 }


}
