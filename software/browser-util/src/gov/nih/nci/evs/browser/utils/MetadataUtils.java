package gov.nih.nci.evs.browser.utils;

import gov.nih.nci.evs.browser.common.*;
import java.util.*;
import org.LexGrid.LexBIG.DataModel.Collections.*;
import org.LexGrid.LexBIG.DataModel.Core.*;
import org.LexGrid.LexBIG.LexBIGService.*;
import org.LexGrid.LexBIG.Utility.*;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.*;
import org.LexGrid.codingSchemes.*;
import org.LexGrid.LexBIG.Extensions.Generic.*;
import org.LexGrid.LexBIG.Exceptions.LBException;

import javax.faces.model.*;
import org.apache.log4j.*;

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
 * @author EVS Team
 * @version 1.0
 *
 *          Modification history Initial implementation kim.ong@ngc.com
 *
 */

public class MetadataUtils {
	private boolean debug = false;
    private static Logger _logger = Logger.getLogger(MetadataUtils.class);
    private static final String CODING_SCHEME_NAME_PROPERTY = "codingScheme";
    LexBIGService lbSvc = null;
    LexBIGServiceConvenienceMethods lbscm = null;

    private Vector getMetadataCodingSchemeNames(MetadataPropertyList mdpl) {
        // List<MetadataProperty> metaDataProperties = new
        // ArrayList<MetadataProperty>();
        Vector v = new Vector();
        Iterator<? extends MetadataProperty> metaItr = mdpl.iterateMetadataProperty();
        while (metaItr.hasNext()) {
            MetadataProperty property = (MetadataProperty) metaItr.next();
            if (property.getName().equals(CODING_SCHEME_NAME_PROPERTY)) {
                v.add(property.getValue());
            }
        }
        return v;
    }

	public MetadataUtils() {

	}

    public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public MetadataUtils(LexBIGService lbSvc) {
		this.lbSvc = lbSvc;
		/*
		lbscm =
			(LexBIGServiceConvenienceMethods) lbSvc
				.getGenericExtension("LexBIGServiceConvenienceMethods");
		lbscm.setLexBIGService(lbSvc);
		*/
	}

    /**
     * Gets all of the Metadata Properties from a given Coding Scheme.
     *
     * @param mdpl The whole set of Metadata Properties
     * @param codingScheme The Coding Scheme to restrict to
     * @return All of the Metadata Properties associated with the given Coding
     *         Scheme.
     */
    //private static List<MetadataProperty> getMetadataForCodingSchemes(
	private List<MetadataProperty> getMetadataForCodingSchemes(
        MetadataPropertyList mdpl, String codingScheme) {
        List<MetadataProperty> metaDataProperties =
            new ArrayList<MetadataProperty>();

        Iterator<? extends MetadataProperty> metaItr = mdpl.iterateMetadataProperty();
        while (metaItr.hasNext()) {
            MetadataProperty property = (MetadataProperty) metaItr.next();
            if (property.getName().equals(CODING_SCHEME_NAME_PROPERTY)
                && property.getValue().equals(codingScheme)) {
                metaDataProperties.add(property);
                while (metaItr.hasNext()) {
                    property = metaItr.next();
                    if (!property.getName().equals(CODING_SCHEME_NAME_PROPERTY)) {
                        metaDataProperties.add(property);
                    } else {
                        return metaDataProperties;
                    }
                }
            }
        }
        // if it hasn't found anything, throw an exception.
        throw new RuntimeException(
            "Error retrieving Metadata from Coding Scheme: " + codingScheme);
    }

    /*
     * public MetadataPropertyList getMetadataPropertyList(LexBIGService
     * lbSvc, String codingSchemeName, String version, String urn) {
     * AbsoluteCodingSchemeVersionReference acsvr = new
     * AbsoluteCodingSchemeVersionReference();
     *
     * LexBIGServiceMetadata smd = null; MetadataPropertyList mdpl = null; try {
     * smd = lbSvc.getServiceMetadata(); if (smd == null) return null; if (urn
     * != null) acsvr.setCodingSchemeURN(urn); if (version != null)
     * acsvr.setCodingSchemeVersion(version);
     *
     * try { smd = smd.restrictToCodingScheme(acsvr); } catch (Exception ex) {
     * ex.printStackTrace();
     * _logger.error("smd.restrictToCodingScheme(acsvr) failed???"); return
     * null; }
     *
     * try { mdpl = smd.resolve(); } catch (Exception ex) {
     * ex.printStackTrace(); _logger.error("smd.resolve() failed???"); return
     * null; }
     *
     * if (mdpl == null || mdpl.getMetadataPropertyCount() == 0) { acsvr = new
     * AbsoluteCodingSchemeVersionReference();
     * acsvr.setCodingSchemeURN(codingSchemeName);
     * acsvr.setCodingSchemeVersion(version); smd = lbSvc.getServiceMetadata();
     * smd = smd.restrictToCodingScheme(acsvr);
     *
     * try { mdpl = smd.resolve(); } catch (Exception ex) {
     * ex.printStackTrace(); return null; } }
     *
     * if (mdpl == null) return null; } catch (Exception ex) {
     * ex.printStackTrace(); }
     *
     * return mdpl; }
     */

    public Vector<String> getAvailableCodingSchemeVersions(String codingSchemeName) {
        Vector<String> v = new Vector<String>();
        try {
            CodingSchemeRenderingList lcsrl = lbSvc.getSupportedCodingSchemes();
            CodingSchemeRendering[] csrs = lcsrl.getCodingSchemeRendering();
            for (int i = 0; i < csrs.length; i++) {
                CodingSchemeRendering csr = (CodingSchemeRendering) csrs[i];
                CodingSchemeSummary css = csr.getCodingSchemeSummary();
                String formalName = css.getFormalName();
                String localName = css.getLocalName();
                if (formalName.compareTo(codingSchemeName) == 0
                    || localName.compareTo(codingSchemeName) == 0) {
                    v.add(css.getRepresentsVersion());
                }
            }
        } catch (Exception e) {
            return new Vector<String>();
        }
        return v;
    }

    public Vector getMetadataForCodingScheme(String codingSchemeName, String version,
        String propertyName) {
        if (version == null) {
			Vector versions =
				getAvailableCodingSchemeVersions(codingSchemeName);
			version = (String) versions.elementAt(0);
		}

        MetadataPropertyList mdpl =
            getMetadataPropertyList(codingSchemeName, version, null);
        return getMetadataForCodingScheme(mdpl, propertyName);
    }


    public Vector getMetadataForCodingScheme(MetadataPropertyList mdpl,
        String propertyName) {
        Vector v = new Vector();

        Vector codingSchemeNames = getMetadataCodingSchemeNames(mdpl);

        _logger.debug("getMetadataCodingSchemeNames returns "
            + codingSchemeNames.size());

        int knt = 0;
        for (int k = 0; k < codingSchemeNames.size(); k++) {
            String codingSchemeName = (String) codingSchemeNames.elementAt(k);

            String propertyValue =
                getEntityDescriptionForCodingScheme(mdpl, codingSchemeName,
                    propertyName);
            v.add(codingSchemeName + "|" + propertyValue);
        }
        v = SortUtils.quickSort(v);
        return v;
    }

    public String getEntityDescriptionForCodingScheme(
        MetadataPropertyList mdpl, String codingSchemeName, String propertyName) {
        try {
            List<MetadataProperty> properties =
                getMetadataForCodingScheme(mdpl, codingSchemeName);
            if (properties == null)
                return null;

            // Print out all of the Metadata Properties of the Coding Scheme.
            // The propery names ('prop.getName()') are going to be things like
            // 'formalName',
            // 'codingSchemeURI', 'representsVersion', etc... so you can pick
            // out which ones
            // you'd like to use.
            for (MetadataProperty prop : properties) {
                // _logger.debug("\tProperty Name: " + prop.getName() +
                // "\n\tProperty Value: " + prop.getValue());
                if (prop.getName().compareTo(propertyName) == 0) {
                    return prop.getValue();
                }
            }
        } catch (Exception ex) {
            // _logger.error("getEntityDescriptionForCodingScheme throws exception???? "
            // );
            _logger.error("WARNING: Unable to retrieve metadata for source "
                + codingSchemeName
                + " please consult your system administrator.");
            // ex.printStackTrace();
        }
        return null;

    }

    public Vector getMetadataNameValuePairs(String codingSchemeName,
        String version, String urn) {
        if (version == null) {
            try {
                CodingScheme cs =
                    lbSvc.resolveCodingScheme(codingSchemeName, null);
                version = cs.getRepresentsVersion();
            } catch (Exception ex) {

            }
        }

        MetadataPropertyList mdpl =
            getMetadataPropertyList(codingSchemeName, version, urn);
        return getMetadataNameValuePairs(mdpl);

    }

    public Vector getMetadataNameValuePairs(MetadataPropertyList mdpl, boolean sort) {
		if (debug) {
		System.out.println("====================== dump getMetadataNameValuePairs ======================");
		}

        if (mdpl == null)
            return null;
        Vector v = new Vector();
        Iterator<? extends MetadataProperty> metaItr = mdpl.iterateMetadataProperty();
        while (metaItr.hasNext()) {
            MetadataProperty property = (MetadataProperty) metaItr.next();
            String t = property.getName() + "|" + property.getValue();
			if (debug) {
				System.out.println(t);
			}
            v.add(t);
        }
        if (sort)
            return SortUtils.quickSort(v);
        return v;
    }

    public Vector getMetadataNameValuePairs(MetadataPropertyList mdpl) {
        return getMetadataNameValuePairs(mdpl, true);
    }

    public Vector getMetadataValues(Vector metadata, String propertyName) {
        if (metadata == null)
            return null;
        Vector w = new Vector();
        for (int i = 0; i < metadata.size(); i++) {
            String t = (String) metadata.elementAt(i);
            Vector u = StringUtils.parseData(t, "|");
            String name = (String) u.elementAt(0);
            if (name.compareTo(propertyName) == 0) {
                String value = (String) u.elementAt(1);
                w.add(value);
            }
        }
        return w;
    }

    public Vector getMetadataValues(String codingSchemeName,
        String version, String urn, String propertyName, boolean sort) {
        MetadataPropertyList mdpl =
            getMetadataPropertyList(codingSchemeName, version, urn);
        if (mdpl == null)
            return null;

        Vector metadata = getMetadataNameValuePairs(mdpl, sort);
        if (metadata == null)
            return null;

        return getMetadataValues(metadata, propertyName);
    }


    public String getMetadataValue(String codingSchemeName,
        String version, String urn, String propertyName) {

// Temporary partial workaround for MedDRASecurity OutOfMemoryError exception (LEXEVS JIRA 999):
/*
Exception in thread "main" java.lang.OutOfMemoryError: unable to create new nati
ve thread
        at java.lang.Thread.start0(Native Method)
        at java.lang.Thread.start(Thread.java:714)
        at java.util.Timer.<init>(Timer.java:160)
        at java.util.Timer.<init>(Timer.java:132)
        at gov.nih.nci.system.dao.security.MedDRASecurity.resetCache(Unknown Source)
*/

if (codingSchemeName.compareTo("MedDRA") == 0) {
	if (propertyName.compareTo("display_name") == 0) {
	    return codingSchemeName;
	} else if (propertyName.compareTo("full_name") == 0) {
		return "Medical Dictionary for Regulatory Activities Terminology";
	} else if (propertyName.compareTo("term_browser_version") == 0) {
		return version;
	}
}

        Vector v = getMetadataValues(codingSchemeName, version, urn, propertyName, true);
        if (v == null) {
            _logger
                .warn("getMetadataValue returns null??? " + codingSchemeName);
            return "";
        }
        int n = v.size();
        if (n <= 0) {
            _logger.warn("WARNING: getMetadataValue(\"" + propertyName + "\"): returns no value " + codingSchemeName + " (version: " + version + ")");
            _logger.warn("  * Note: This metadata might not be loaded.");
            return "";
        }
        if (v.size() == 1)
            return v.elementAt(0).toString();

        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < n; ++i) {
            if (i > 0)
                buffer.append("|");
            buffer.append(v.elementAt(i).toString());
        }
        return buffer.toString();
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
/*
    public MetadataPropertyList getMetadataPropertyList(
        String codingSchemeName, String version, String urn) {

        if (urn == null) {
			CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
			if (version != null) versionOrTag.setVersion(version);
			try {
				CodingScheme cs = getCodingScheme(codingSchemeName, versionOrTag);
				if (cs == null) {
					return null;
				}
				urn = cs.getCodingSchemeURI();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
        MetadataPropertyList mdpl = null;
        try {
            LexBIGServiceMetadata lbsm = lbSvc.getServiceMetadata();
            lbsm =
                lbsm.restrictToCodingScheme(Constructors
                    .createAbsoluteCodingSchemeVersionReference(urn, version));
            mdpl = lbsm.resolve();

            return mdpl;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return mdpl;
    }
*/

    public MetadataPropertyList getMetadataPropertyList(
        String codingSchemeName, String version, String urn) {

        if (urn == null) {
			CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
			if (version != null) versionOrTag.setVersion(version);
			try {
				CodingScheme cs = getCodingScheme(codingSchemeName, versionOrTag);
				if (cs == null) return null;

				urn = cs.getCodingSchemeURI();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

        //LexBIGServiceConvenienceMethods lbscm = null;
        MetadataPropertyList mdpl = null;
        try {
            LexBIGServiceMetadata lbsm = lbSvc.getServiceMetadata();
            lbsm =
                lbsm.restrictToCodingScheme(Constructors
                    .createAbsoluteCodingSchemeVersionReference(
                        //codingSchemeName, version));
                        urn, version));
            mdpl = lbsm.resolve();

            return mdpl;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return mdpl;
    }


    public NameAndValue createNameAndValue(String name, String value) {
        NameAndValue nv = new NameAndValue();
        nv.setName(name);
        nv.setContent(value);
        return nv;
    }

    public NameAndValue[] getMetadataProperties(CodingScheme cs) {
        String cs_urn = cs.getCodingSchemeURI();
        //String cs_urn = cs.getCodingSchemeURN();
        String version = cs.getRepresentsVersion();
        Vector<NameAndValue> v = new Vector<NameAndValue>();
        try {
            //LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
            LexBIGServiceMetadata svc = lbSvc.getServiceMetadata();
            AbsoluteCodingSchemeVersionReferenceList acsvrl =
                svc.listCodingSchemes();
            AbsoluteCodingSchemeVersionReference[] acdvra =
                acsvrl.getAbsoluteCodingSchemeVersionReference();
            for (int i = 0; i < acdvra.length; i++) {
                AbsoluteCodingSchemeVersionReference acsvr = acdvra[i];
                String urn = acsvr.getCodingSchemeURN();
                String ver = acsvr.getCodingSchemeVersion();
                if (urn.equals(cs_urn) && ver.equals(version)) {
                    // 100807 KLO
                    svc = svc.restrictToCodingScheme(acsvr);
                    MetadataPropertyList mdpl = svc.resolve();
                    MetadataProperty[] properties = mdpl.getMetadataProperty();
                    for (int j = 0; j < properties.length; j++) {
                        MetadataProperty property = properties[j];
                        NameAndValue nv =
                            createNameAndValue(property.getName(), property
                                .getValue());
                        v.add(nv);
                    }
                }
            }

            if (v.size() > 0) {
                NameAndValue[] nv_array = new NameAndValue[v.size()];
                for (int i = 0; i < v.size(); i++) {
                    NameAndValue nv = (NameAndValue) v.elementAt(i);
                    nv_array[i] = nv;
                }
                return nv_array;
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
        return new NameAndValue[0];

    }

    public HashMap getMappingDisplayHashMap(String codingSchemeName, String version) {
        String propertyName = "shortName";
        try {
			HashMap hmap = new HashMap();
			if (version == null) {
				Vector versions =
					getAvailableCodingSchemeVersions(codingSchemeName);
				version = (String) versions.elementAt(0);
			}

			MetadataPropertyList mdpl =
				getMetadataPropertyList(codingSchemeName, version, null);
			if (mdpl == null) {
				return null;
			}

            MetadataProperty[] properties = mdpl.getMetadataProperty();

            if (properties == null)
                return null;

            // Print out all of the Metadata Properties of the Coding Scheme.
            // The propery names ('prop.getName()') are going to be things like
            // 'formalName',
            // 'codingSchemeURI', 'representsVersion', etc... so you can pick
            // out which ones
            // you'd like to use.
            for (MetadataProperty prop : properties) {
                // _logger.debug("\tProperty Name: " + prop.getName() +
                // "\n\tProperty Value: " + prop.getValue());
                if (prop.getName().compareTo(propertyName) == 0) {
					Object[] context_array = prop.getContext();
					int context_array_size = context_array.length;
					String cs_name = context_array[context_array_size-1].toString();
					hmap.put(cs_name, prop.getValue());
                }
            }
            return hmap;
        } catch (Exception ex) {
            // _logger.error("getEntityDescriptionForCodingScheme throws exception???? "
            // );
            _logger.error("WARNING: Unable to retrieve metadata for source "
                + codingSchemeName
                + " please consult your system administrator.");
            // ex.printStackTrace();
        }
        return null;
    }

    public boolean tree_access_allowed(String scheme, String version) {
		try {
			CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
			if (version != null) {
				versionOrTag.setVersion(version);
			}

			CodingScheme cs = lbSvc.resolveCodingScheme(scheme, versionOrTag);
			NameAndValue[] nvList = getMetadataProperties(cs);
			for (int k = 0; k < nvList.length; k++) {
				NameAndValue nv = (NameAndValue) nvList[k];
				if (nv.getName().compareTo(gov.nih.nci.evs.browser.common.Constants.TREE_ACCESS_ALLOWED) == 0) {
				    if (nv.getContent().compareTo("false") == 0) {
						return false;
					}
				}
			}
		} catch (Exception ex) {
            ex.printStackTrace();
		}
        return true;
	}
}

