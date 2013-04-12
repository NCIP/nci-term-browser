/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.utils;

/**
  * 
  */

/**
  * @author EVS Team
  * @version 1.0
  *
  * Modification history
  *     Initial implementation kim.ong@ngc.com
  *
 */

//import gov.nih.nci.evs.browser.common.Constants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.LexGrid.LexBIG.DataModel.Collections.MetadataPropertyList;
import org.LexGrid.LexBIG.DataModel.Core.AbsoluteCodingSchemeVersionReference;
import org.LexGrid.LexBIG.DataModel.Core.MetadataProperty;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;
import org.LexGrid.LexBIG.LexBIGService.LexBIGServiceMetadata;

import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;

import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.concepts.Concept;
import org.LexGrid.LexBIG.DataModel.Collections.ConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.ConceptReference;

import org.LexGrid.LexBIG.Utility.Constructors;

import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;

//v5.0
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.client.ApplicationServiceProvider;
import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.LexGrid.LexBIG.caCore.interfaces.LexEVSApplicationService;
import org.LexGrid.LexBIG.caCore.interfaces.LexEVSDataService;
import org.LexGrid.LexBIG.caCore.interfaces.LexEVSDistributed;
import org.LexGrid.LexBIG.caCore.interfaces.LexEVSService;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.Extensions.Generic.LexBIGServiceConvenienceMethods;

import org.LexGrid.LexBIG.DataModel.Collections.NameAndValueList;
import org.LexGrid.LexBIG.DataModel.Core.NameAndValue;

import javax.faces.model.SelectItem;
import org.LexGrid.LexBIG.DataModel.Collections.AbsoluteCodingSchemeVersionReferenceList;


public class MetadataUtils {

	private static final String CODING_SCHEME_NAME_PROPERTY = "codingScheme";

	private static Vector getMetadataCodingSchemeNames(MetadataPropertyList mdpl){
		//List<MetadataProperty> metaDataProperties = new ArrayList<MetadataProperty>();
        Vector v = new Vector();
		Iterator<MetadataProperty> metaItr = mdpl.iterateMetadataProperty();
		while(metaItr.hasNext()){
			MetadataProperty property = metaItr.next();
			if (property.getName().equals(CODING_SCHEME_NAME_PROPERTY)) {
	            v.add(property.getValue());
			}
		}
		return v;
	}

	/**
	 * Gets all of the Metadata Properties from a given Coding Scheme.
	 *
	 * @param mdpl The whole set of Metadata Properties
	 * @param codingScheme The Coding Scheme to restrict to
	 * @return All of the Metadata Properties associated with the given Coding Scheme.
	 */
	private static List<MetadataProperty> getMetadataForCodingSchemes(MetadataPropertyList mdpl, String codingScheme){
		List<MetadataProperty> metaDataProperties = new ArrayList<MetadataProperty>();

		Iterator<MetadataProperty> metaItr = mdpl.iterateMetadataProperty();
		while(metaItr.hasNext()){
			MetadataProperty property = metaItr.next();
			if (property.getName().equals(CODING_SCHEME_NAME_PROPERTY) && property.getValue().equals(codingScheme)) {
				metaDataProperties.add(property);
				while(metaItr.hasNext()){
					property = metaItr.next();
					if(!property.getName().equals(CODING_SCHEME_NAME_PROPERTY)){
						metaDataProperties.add(property);
					} else {
						return metaDataProperties;
					}
				}
			}
		}
		//if it hasn't found anything, throw an exception.
		throw new RuntimeException("Error retrieving Metadata from Coding Scheme: " + codingScheme);
	}

/*
    public static MetadataPropertyList getMetadataPropertyList(LexBIGService lbSvc, String codingSchemeName, String version, String urn) {
		AbsoluteCodingSchemeVersionReference acsvr = new AbsoluteCodingSchemeVersionReference();

        LexBIGServiceMetadata smd = null;
        MetadataPropertyList mdpl = null;
        try {
			smd = lbSvc.getServiceMetadata();
			if (smd == null) return null;
			if (urn != null) acsvr.setCodingSchemeURN(urn);
			if (version != null) acsvr.setCodingSchemeVersion(version);

			try {
				smd = smd.restrictToCodingScheme(acsvr);
			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("smd.restrictToCodingScheme(acsvr) failed???");
				return null;
			}

			try {
				mdpl = smd.resolve();
			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("smd.resolve() failed???");
				return null;
			}

			if (mdpl == null || mdpl.getMetadataPropertyCount() == 0) {
				acsvr = new AbsoluteCodingSchemeVersionReference();
				acsvr.setCodingSchemeURN(codingSchemeName);
				acsvr.setCodingSchemeVersion(version);
				smd = lbSvc.getServiceMetadata();
				smd = smd.restrictToCodingScheme(acsvr);

				try {
					mdpl = smd.resolve();
				} catch (Exception ex) {
					ex.printStackTrace();
					return null;
				}
			}

			if (mdpl == null) return null;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return mdpl;
	}
*/

 	public static Vector<String> getAvailableCodingSchemeVersions(LexBIGService lbSvc, String codingSchemeName)
	{
		Vector<String> v = new Vector<String>();
		try {
			CodingSchemeRenderingList lcsrl = lbSvc.getSupportedCodingSchemes();
			CodingSchemeRendering[] csrs = lcsrl.getCodingSchemeRendering();
			for (int i=0; i<csrs.length; i++)
			{
				CodingSchemeRendering csr = (CodingSchemeRendering) csrs[i];
				CodingSchemeSummary css = csr.getCodingSchemeSummary();
				String formalName = css.getFormalName();
				String localName = css.getLocalName();
				if (formalName.compareTo(codingSchemeName) == 0 || localName.compareTo(codingSchemeName) == 0)
				{
					v.add(css.getRepresentsVersion());
				}
			}
	    } catch (Exception e) {
			return new Vector<String>();
		}
		return v;
	}

    public static Vector getMetadataForCodingScheme(String codingSchemeName, String propertyName) {
		//String codingSchemeName = Constants.CODING_SCHEME_NAME;
		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();

		String version = null;
		Vector versions = getAvailableCodingSchemeVersions(lbSvc, codingSchemeName);
		version = (String) versions.elementAt(0);

		//String urn = "urn:oid:2.16.840.1.113883.3.26.1.2";//urn:oid:2.16.840.1.113883.3.26.1.2
		MetadataPropertyList mdpl = getMetadataPropertyList(lbSvc, codingSchemeName, version, null);
		return getMetadataForCodingScheme(mdpl, propertyName);
	}


    public static Vector getMetadataForCodingScheme(MetadataPropertyList mdpl, String propertyName) {
		Vector v = new Vector();
		Vector codingSchemeNames = getMetadataCodingSchemeNames(mdpl);

		System.out.println("getMetadataCodingSchemeNames returns " + codingSchemeNames.size() );

		int knt = 0;
		for (int k=0; k<codingSchemeNames.size(); k++) {
			String codingSchemeName = (String) codingSchemeNames.elementAt(k);

			String propertyValue = getEntityDescriptionForCodingScheme(mdpl, codingSchemeName, propertyName);
			v.add(codingSchemeName + "|" + propertyValue);
	    }
	    v = SortUtils.quickSort(v);
		return v;
	}


    public static String getEntityDescriptionForCodingScheme(MetadataPropertyList mdpl, String codingSchemeName, String propertyName) {
		try {
			List<MetadataProperty> properties = getMetadataForCodingScheme(mdpl, codingSchemeName);
			if (properties == null) return null;

			//Print out all of the Metadata Properties of the Coding Scheme.
			//The propery names ('prop.getName()') are going to be things like 'formalName',
			//'codingSchemeURI', 'representsVersion', etc... so you can pick out which ones
			//you'd like to use.
			for(MetadataProperty prop : properties){
				//System.out.println("\tProperty Name: " + prop.getName() + "\n\tProperty Value: " + prop.getValue());
				if (prop.getName().compareTo(propertyName) == 0) {
					return prop.getValue();
				}
			}
		} catch (Exception ex) {
			//System.out.println("getEntityDescriptionForCodingScheme throws exception???? " );
			System.out.println("WARNING: Unable to retrieve metadata for source " + codingSchemeName + " please consult your system administrator." );
			//ex.printStackTrace();
		}
		return null;

	}

    public static Vector getMetadataNameValuePairs(String codingSchemeName,
        String version, String urn) {
		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();

		if (version == null) {
			try {
				CodingScheme cs = lbSvc.resolveCodingScheme(codingSchemeName, null);
				version = cs.getRepresentsVersion();
			} catch (Exception ex) {

			}
		}

		MetadataPropertyList mdpl = getMetadataPropertyList(lbSvc, codingSchemeName, version, urn);
		return getMetadataNameValuePairs(mdpl);

	}

	public static Vector getMetadataNameValuePairs(MetadataPropertyList mdpl, boolean sort){
		if (mdpl == null) return null;
		Vector v = new Vector();
		Iterator<MetadataProperty> metaItr = mdpl.iterateMetadataProperty();
		while(metaItr.hasNext()){
			MetadataProperty property = metaItr.next();
			String t = property.getName() + "|" + property.getValue();
            v.add(t);
		}
		if (sort)
		    return SortUtils.quickSort(v);
		return v;
	}

	public static Vector getMetadataNameValuePairs(MetadataPropertyList mdpl){
	    return getMetadataNameValuePairs(mdpl, true);
	}
	
	public static Vector getMetadataValues(Vector metadata, String propertyName){
		if (metadata == null) return null;
		Vector w = new Vector();
		for (int i=0; i<metadata.size(); i++) {
			String t = (String) metadata.elementAt(i);
			Vector u = DataUtils.parseData(t, "|");
			String name = (String) u.elementAt(0);
			if (name.compareTo(propertyName) == 0) {
				String value = (String) u.elementAt(1);
				w.add(value);
			}
		}
		return w;
	}

	public static Vector getMetadataValues(String codingSchemeName, String version,
	    String urn, String propertyName, boolean sort){
		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
		MetadataPropertyList mdpl = getMetadataPropertyList(lbSvc, codingSchemeName, version, urn);
		if (mdpl == null) return null;

		Vector metadata = getMetadataNameValuePairs(mdpl, sort);
		if (metadata == null) return null;

		return getMetadataValues(metadata, propertyName);
	}

    public static Vector getMetadataValues(String codingSchemeName, String version,
        String urn, String propertyName){
        return getMetadataValues(codingSchemeName, version,
            urn, propertyName, true);
    }

    public static String getMetadataValue(String codingSchemeName, String version,
        String urn, String propertyName) {
        Vector v = getMetadataValues(codingSchemeName, version, urn, propertyName);
        if (v == null) {
			System.out.println("getMetadataValue returns null??? " + codingSchemeName);
            return "";
		}
        int n = v.size();
        if (n <= 0)
            return "";
        if (v.size() == 1)
            return v.elementAt(0).toString();

        StringBuffer buffer = new StringBuffer();
        for (int i=0; i<n; ++i) {
            if (i > 0)
                buffer.append(" | ");
            buffer.append(v.elementAt(i).toString());
        }
        return buffer.toString();
    }

    public Vector getSupportedVocabularyMetadataValues(String propertyName) {
		Vector v = new Vector();
		List ontology_list = DataUtils.getOntologyList();
		for (int i = 0; i < ontology_list.size(); i++) {
			  SelectItem item = (SelectItem) ontology_list.get(i);
			  String value = (String) item.getValue();

			  String label = (String) item.getLabel();
			  String scheme = DataUtils.key2CodingSchemeName(value);
			  String version = DataUtils.key2CodingSchemeVersion(value);
			  String name = DataUtils.getMetadataValue(scheme, "display_name");

			  String urn = null;

			  Vector w = getMetadataValues(scheme, version, urn, propertyName);
			  if (w == null || w.size() == 0) {
				  v.add(name + "|" + propertyName + " not available");
			  } else {
				  String t = (String) w.elementAt(0);
				  v.add(name + " (version: " + version + ")" + "|" + t);
			  }
	    }
	    return v;
	}



    public static MetadataPropertyList getMetadataPropertyList(LexBIGService lbSvc, String codingSchemeName, String version, String urn) {
		LexBIGServiceConvenienceMethods lbscm = null;
		MetadataPropertyList mdpl = null;
		try {
			lbscm = (LexBIGServiceConvenienceMethods) lbSvc
					.getGenericExtension("LexBIGServiceConvenienceMethods");
			lbscm.setLexBIGService(lbSvc);

			LexBIGServiceMetadata lbsm = lbSvc.getServiceMetadata();
			lbsm = lbsm.restrictToCodingScheme(Constructors.createAbsoluteCodingSchemeVersionReference(codingSchemeName, version));
			mdpl = lbsm.resolve();

			return mdpl;
	    } catch (Exception ex) {
			ex.printStackTrace();
		}
		return mdpl;
    }


	public static NameAndValue createNameAndValue(String name, String value)
	{
        NameAndValue nv = new NameAndValue();
		nv.setName(name);
		nv.setContent(value);
		return nv;
	}

	public static NameAndValue[] getMetadataProperties(CodingScheme cs)
	{
        String formalName = cs.getFormalName();
        String version = cs.getRepresentsVersion();
		Vector<NameAndValue> v = new Vector<NameAndValue>();
		try {
			LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
			LexBIGServiceMetadata svc = lbSvc.getServiceMetadata();
			AbsoluteCodingSchemeVersionReferenceList acsvrl = svc.listCodingSchemes();
			AbsoluteCodingSchemeVersionReference[] acdvra =	acsvrl.getAbsoluteCodingSchemeVersionReference();
			for (int i=0; i<acdvra.length; i++)
			{
				AbsoluteCodingSchemeVersionReference acsvr = acdvra[i];
				String urn = acsvr.getCodingSchemeURN();
				String ver = acsvr.getCodingSchemeVersion();
				if (urn.equals(formalName) && ver.equals(version))
				{
					//100807 KLO
					svc = svc.restrictToCodingScheme(acsvr);
					MetadataPropertyList mdpl = svc.resolve();
					MetadataProperty[] properties = mdpl.getMetadataProperty();
					for (int j=0; j<properties.length; j++)
					{
						MetadataProperty property = properties[j];
						NameAndValue nv = createNameAndValue(property.getName(), property.getValue());
						v.add(nv);
					}
				}
			}

			if (v.size() > 0)
			{
				NameAndValue[] nv_array = new NameAndValue[v.size()];
				for (int i=0; i<v.size(); i++)
				{
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



	/**
	 * Simple example to demonstrate extracting a specific Coding Scheme's Metadata.
	 *
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		MetadataUtils test = new MetadataUtils();
		String serviceUrl = "http://ncias-d177-v.nci.nih.gov:19280/lexevsapi50";
		//serviceUrl = "http://cbvapp-d1007.nci.nih.gov:19080/lexevsapi50";
		serviceUrl = "http://lexevsapi-qa.nci.nih.gov/lexevsapi50";
		//serviceUrl = "http://lexevsapi-dev.nci.nih.gov/lexevsapi50";

		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService(serviceUrl);
		//LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();

		if (lbSvc == null) {
			System.out.println("Unable to connect to " + serviceUrl);
			System.exit(1);
		} else {
			System.out.println("Connected to " + serviceUrl);
		}
/*
		String codingSchemeName = "NCI MetaThesaurus";
		String urn = "urn:oid:2.16.840.1.113883.3.26.1.2";//urn:oid:2.16.840.1.113883.3.26.1.2
		String version = "200812";
		urn = null;
*/
		String codingSchemeName = "Zebrafish";
		String urn = "http://ncicb.nci.nih.gov/xmlns/zebrafish.owl#";
		String version = "1.2";

        Vector v = test.getMetadataNameValuePairs(codingSchemeName, version, null);
        if (v == null || v.size() == 0) {
			System.out.println("Metadata not found.");
		}
		else {
			for (int i=0; i<v.size(); i++) {
				String t = (String) v.elementAt(i);
				System.out.println(t);
			}
	    }

		codingSchemeName = "Medical Dictionary for Regulatory Activities Terminology (MedDRA), 10.1";
		version = "10.1";
		urn = null;

        //Vector v = test.getMetadataNameValuePairs(codingSchemeName, version, urn);
        v = test.getMetadataNameValuePairs(codingSchemeName, version, null);
        if (v == null || v.size() == 0) {
			System.out.println("Metadata not found.");
		}
		else {
			for (int i=0; i<v.size(); i++) {
				String t = (String) v.elementAt(i);
				System.out.println(t);
			}
	    }

		//MetadataPropertyList mdpl = test.getMetadataPropertyList(lbSvc, codingSchemeName, version, urn);

/*
        String propertyName = "entityDescription";
        test.getMetadataForCodingSchemes(mdpl, propertyName);

        propertyName = "formalName";
        test.getMetadataForCodingSchemes(mdpl, propertyName);
 */
    }

}











