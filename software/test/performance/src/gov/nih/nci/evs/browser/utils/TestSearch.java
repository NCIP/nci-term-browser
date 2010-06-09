package gov.nih.nci.evs.browser.utils;

import java.io.*;
import java.util.*;

//import gov.nih.nci.evs.reportwriter.bean.StandardReportTemplate;
import gov.nih.nci.system.applicationservice.EVSApplicationService;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.HashSet;
import java.util.Arrays;

import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Collections.SortOptionList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.PropertyType;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.SearchDesignationOption;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.LBConstants.MatchAlgorithms;
import org.LexGrid.concepts.Concept;

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;

import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;
import org.LexGrid.LexBIG.DataModel.Collections.ModuleDescriptionList;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.ModuleDescription;


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
import org.LexGrid.concepts.Definition;
import org.LexGrid.concepts.Comment;
import org.LexGrid.concepts.Instruction;
import org.LexGrid.concepts.Presentation;

//import org.LexGrid.relations.Relations;

import org.apache.log4j.Logger;

import org.LexGrid.LexBIG.Exceptions.LBResourceUnavailableException;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;

import org.LexGrid.LexBIG.Utility.ConvenienceMethods;
import org.LexGrid.commonTypes.EntityDescription;

import org.LexGrid.concepts.ConceptProperty;
import org.LexGrid.commonTypes.Property;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;

import org.LexGrid.LexBIG.DataModel.Collections.AssociationList;
import org.LexGrid.LexBIG.DataModel.Collections.ConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.AssociatedConcept;
import org.LexGrid.LexBIG.DataModel.Core.Association;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.ActiveOption;
import org.LexGrid.LexBIG.Utility.ConvenienceMethods;
import org.LexGrid.commonTypes.EntityDescription;
import org.LexGrid.commonTypes.Property;
import org.LexGrid.concepts.Concept;
import org.LexGrid.concepts.ConceptProperty;

import org.LexGrid.relations.Relations;

import org.LexGrid.commonTypes.PropertyQualifier;

import org.LexGrid.commonTypes.Source;
import org.LexGrid.naming.SupportedSource;

import org.LexGrid.naming.SupportedPropertyQualifier;
import org.LexGrid.LexBIG.DataModel.Core.types.CodingSchemeVersionStatus;

import org.LexGrid.naming.SupportedAssociation;
import org.LexGrid.naming.SupportedAssociationQualifier;

import org.LexGrid.naming.SupportedProperty;
import org.LexGrid.naming.SupportedPropertyQualifier;
import org.LexGrid.naming.SupportedRepresentationalForm;
import org.LexGrid.naming.SupportedSource;

import org.LexGrid.LexBIG.DataModel.Collections.AssociatedConceptList;
import org.LexGrid.LexBIG.DataModel.Collections.AssociationList;
import org.LexGrid.LexBIG.DataModel.Core.AssociatedConcept;
import org.LexGrid.LexBIG.DataModel.Core.Association;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Extensions.Generic.LexBIGServiceConvenienceMethods;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.codingSchemes.Mappings;
import org.LexGrid.naming.SupportedHierarchy;

//import gov.nih.nci.evs.reportwriter.bean.*;

import org.LexGrid.LexBIG.DataModel.InterfaceElements.RenderingDetail;
import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeTagList;

//import gov.nih.nci.evs.reportwriter.properties.ReportWriterProperties;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;

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

public class TestSearch {
    private static Logger _logger = Logger.getLogger(TestSearch.class);
    int maxReturn = 5000;
	Connection con;
	Statement stmt;
	ResultSet rs;

	private List supportedStandardReportList = new ArrayList();

	private static List standardReportTemplateList = null;
	private static List adminTaskList = null;
    private static List userTaskList = null;

    private static List propertyTypeList = null;

	private static List _ontologies = null;

	//private static org.LexGrid.LexBIG.LexBIGService.LexBIGService lbSvc = null;
	public org.LexGrid.LexBIG.Utility.ConvenienceMethods lbConvMethods = null;
    public CodingSchemeRenderingList csrl = null;
    private Vector supportedCodingSchemes = null;
    private static HashMap codingSchemeMap = null;
    private Vector codingSchemes = null;

    private static HashMap csnv2codingSchemeNameMap = null;
    private static HashMap csnv2VersionMap = null;

    private static List directionList = null;

    private static String url = null;

    //==================================================================================
    // For customized query use

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

	static final List<String> STOP_WORDS = Arrays.asList(new String[] {
		"a", "an", "and", "by", "for", "of", "on", "in", "nos", "the", "to", "with"});

    //==================================================================================


    public TestSearch()
    {
        //setCodingSchemeMap();
	}

    public TestSearch(String url)
    {
        //setCodingSchemeMap();
        this.url = url;
        _logger.debug("server URL: " + url);
	}


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

 	public static NameAndValueList createNameAndValueList(String[] names, String[] values)
 	{
 		NameAndValueList nvList = new NameAndValueList();
 		for (int i=0; i<names.length; i++)
 		{
 			NameAndValue nv = new NameAndValue();
 			nv.setName(names[i]);
 			if (values != null)
 			{
 				nv.setContent(values[i]);
 			}
 			nvList.addNameAndValue(nv);
 		}
 		return nvList;
 	}

	public static LocalNameList vector2LocalNameList(Vector<String> v)
	{
	    if (v == null) return null;
	    LocalNameList list = new LocalNameList();
		for (int i=0; i<v.size(); i++)
		{
		    String vEntry = (String) v.elementAt(i);
	        list.addEntry(vEntry);
		}
        return list;
	}

 	protected static NameAndValueList createNameAndValueList(Vector names, Vector values)
 	{
		if (names == null) return null;
 		NameAndValueList nvList = new NameAndValueList();
 		for (int i=0; i<names.size(); i++)
 		{
			String name = (String) names.elementAt(i);
			String value = (String) values.elementAt(i);
			NameAndValue nv = new NameAndValue();
 			nv.setName(name);
 			if (value != null)
 			{
 				nv.setContent(value);
 			}
 			nvList.addNameAndValue(nv);
 		}
 		return nvList;
 	}


    public static ResolvedConceptReferencesIterator restrictToMatchingProperty(
											 String codingSchemeName,
											 String version,
		                                     Vector property_vec,
                                             Vector source_vec,
                                             Vector qualifier_name_vec,
                                             Vector qualifier_value_vec,
											 java.lang.String matchText,
											 java.lang.String matchAlgorithm,
											 java.lang.String language)
    {

		LocalNameList propertyList = vector2LocalNameList(property_vec);
		CodedNodeSet.PropertyType[] propertyTypes = null;
		LocalNameList sourceList = vector2LocalNameList(source_vec);

		NameAndValueList qualifierList = createNameAndValueList(qualifier_name_vec, qualifier_value_vec);

	    return restrictToMatchingProperty(codingSchemeName,
	                                   version,
	                                   propertyList,
	                                   propertyTypes,
	                                   sourceList,
	                                   qualifierList,
									   matchText,
									   matchAlgorithm,
									   language);


    }


	public static ResolvedConceptReferencesIterator restrictToMatchingProperty(
		                                        String codingSchemeName,
	                                            String version,

	                                            LocalNameList propertyList,
                                                CodedNodeSet.PropertyType[] propertyTypes,
                                                LocalNameList sourceList,
                                                NameAndValueList qualifierList,

 											    java.lang.String matchText,
											    java.lang.String matchAlgorithm,
											    java.lang.String language)
	{
	    CodedNodeSet cns = null;
         ResolvedConceptReferencesIterator iterator = null;
         try {
            LexBIGService lbSvc = new RemoteServerUtil().createLexBIGService(url);
            //LexBIGService lbSvc = new LexBIGServiceImpl();

			CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
			versionOrTag.setVersion(version);

			if (lbSvc == null)
			{
				_logger.warn("lbSvc = null");
				return null;
			}

			cns = lbSvc.getCodingSchemeConcepts(codingSchemeName, versionOrTag);
			if (cns == null)
			{
				_logger.warn("cns = null");
				return null;
			}

			LocalNameList contextList = null;
            cns = cns.restrictToMatchingProperties(propertyList,
                                           propertyTypes,
                                           sourceList,
                                           contextList,
                                           qualifierList,
                                           matchText,
                                           matchAlgorithm,
                                           language
                                           );

			LocalNameList restrictToProperties = new LocalNameList();
		    SortOptionList sortCriteria =
			    Constructors.createSortOptionList(new String[]{"matchToQuery"});

            try {
                iterator = cns.resolve(sortCriteria, null, restrictToProperties, null);
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}

	    } catch (Exception e) {
			e.printStackTrace();
			return null;
	    }
		return iterator;
	}



	public static ConceptReferenceList createConceptReferenceList(String[] codes, String codingSchemeName)
	{
		if (codes == null)
		{
			return null;
		}
		ConceptReferenceList list = new ConceptReferenceList();
		for (int i = 0; i < codes.length; i++)
		{
			ConceptReference cr = new ConceptReference();
			cr.setCodingScheme(codingSchemeName);
			cr.setConceptCode(codes[i]);
			list.addConceptReference(cr);
		}
		return list;
	}

	public static ResolvedConceptReferenceList getNext(ResolvedConceptReferencesIterator iterator)
	{
		return iterator.getNext();
	}

	/**
	* Dump_matches to output, for debug purposes
	*
	* @param iterator the iterator
	* @param maxToReturn the max to return
	*/
	public static Vector  resolveIterator(ResolvedConceptReferencesIterator iterator, int maxToReturn)
	{
		return resolveIterator(iterator, maxToReturn, null);
	}


	public static Vector resolveIterator(ResolvedConceptReferencesIterator iterator, int maxToReturn, String code)
	{
		Vector v = new Vector();
		if (iterator == null)
		{
			_logger.debug("No match.");
			return v;
		}
		try {
			int iteration = 0;
			while (iterator.hasNext())
			{
				iteration++;
				iterator = iterator.scroll(maxToReturn);
				ResolvedConceptReferenceList rcrl = iterator.getNext();
				ResolvedConceptReference[] rcra = rcrl.getResolvedConceptReference();
				for (int i=0; i<rcra.length; i++)
				{
					ResolvedConceptReference rcr = rcra[i];
					org.LexGrid.concepts.Concept ce = rcr.getReferencedEntry();
					//_logger.debug("Iteration " + iteration + " " + ce.getId() + " " + ce.getEntityDescription().getContent());
					if (code == null)
					{
						v.add(ce);
					}
					else
					{
						if (ce.getId().compareTo(code) != 0) v.add(ce);

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return v;
	}

	public static Vector<org.LexGrid.concepts.Concept> searchByName(String scheme, String version, String matchText, String matchAlgorithm, int maxToReturn) {
        if (matchText == null) return null;

		if (matchAlgorithm.compareToIgnoreCase("startsWith") == 0)
		{
			matchText = "^" + matchText;
			matchAlgorithm = "regExp";
		}
		else if (matchAlgorithm.compareToIgnoreCase("contains") == 0)
		{
			if (matchText.indexOf(" ") != -1) // contains multiple words
			{
				matchText = ".*" + matchText + ".*";
				matchAlgorithm = "regExp";
		    }
		}

//KLO
        LocalNameList propertyList = null;

        CodedNodeSet.PropertyType[] propertyTypes = new CodedNodeSet.PropertyType[1];

        propertyTypes[0] = CodedNodeSet.PropertyType.PRESENTATION;

        LocalNameList sourceList = null;
        NameAndValueList qualifierList = null;
        String language = null;

	    ResolvedConceptReferencesIterator iterator = restrictToMatchingProperty(
						scheme,
						version,

						propertyList,
						propertyTypes,
						sourceList,
						qualifierList,

						matchText,
						matchAlgorithm,
						language);

		return resolveIterator(	iterator, maxToReturn);

	}

	public void testSearchByName(String scheme, String version, String matchText, String matchAlgorithm) {

		int maxToReturn = 10000;

_logger.debug("************* scheme " + scheme);
_logger.debug("************* version " + version);
_logger.debug("************* matchText " + matchText);
_logger.debug("************* matchAlgorithm " + matchAlgorithm);

        long ms = System.currentTimeMillis();
		Vector<org.LexGrid.concepts.Concept> v = searchByName(scheme, version, matchText, matchAlgorithm, maxToReturn);

		_logger.debug("Run time (ms): " + (System.currentTimeMillis() - ms));
		if (v != null)
		{
			_logger.debug("v.size() = " + v.size());
			for (int i=0; i<v.size(); i++)
			{
				int j = i + 1;
				Concept ce = (Concept) v.elementAt(i);
				_logger.debug("(" + j + ")" + " " + ce.getId() + " " + ce.getEntityDescription().getContent());
			}
		}


	}



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public List ResolvedConceptReferenceList2List(ResolvedConceptReferenceList rcrl)
    {
		ArrayList list = new ArrayList();
		for (int i=0; i<rcrl.getResolvedConceptReferenceCount(); i++)
		{
			ResolvedConceptReference rcr = rcrl.getResolvedConceptReference(i);
			list.add(rcr);
		}
		return list;
	}





	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



	public static void main(String[] args)
	{
		 String url = "http://lexevsapi-dev.nci.nih.gov/lexevsapi42";
		 url = "http://lexevsapi.nci.nih.gov/lexevsapi42";

		 String matchText = "blood";
		 matchText = "mononuclear";
		 String matchAlgorithm = "contains";
		 if (args.length >= 1)
		 {
			 matchText = args[0];
			 _logger.debug(matchText);
		 }
		 if (args.length >= 2)
		 {
			 matchAlgorithm = args[1];
			 _logger.debug(matchAlgorithm);
		 }

		 //url = null;
         TestSearch test = new TestSearch(url);
         String scheme = "NCI Thesaurus";
         String version = null;//"08.06d";

         _logger.debug("Calling LexBIG -- please wait...");
		 long ms = System.currentTimeMillis();

		 Vector matchText_vec = new Vector();
		 Vector matchAlgorithm_vec = new Vector();

         //matchText_vec.add("blood");
         //matchText_vec.add("blood cell");
         //matchText_vec.add("ratio");
         //matchText_vec.add("bladder");

         //matchText_vec.add("aging");

         //DYEE: matchText_vec.add("cell");
		 matchText_vec.add("Biological_Process_Has_Associated_Location"); //DYEE

         //matchAlgorithm_vec.add("contains");
         //DYEE: matchAlgorithm_vec.add("contains");
         //matchAlgorithm_vec.add("startsWith");

         matchAlgorithm_vec.add("exactMatch"); //DYEE
		 matchAlgorithm_vec.add("startsWith"); //DYEE
		 matchAlgorithm_vec.add("contains"); //DYEE

         for (int i=0; i<matchText_vec.size(); i++)
         {
			 matchText = (String) matchText_vec.elementAt(i);

_logger.debug("matchText: " + matchText);

			 matchAlgorithm = (String) matchAlgorithm_vec.elementAt(i);
			 test.testSearchByName(scheme, version, matchText, matchAlgorithm);
		 }
		 _logger.debug("\n--------------------------------------------------------------------------------------");

    }

}


/*

NCI MetaThesaurus (version: 200808)
Pre NCI Thesaurus (version: 08.11d_pre)
Zebrafish (version: 1.2)
UMLS Semantic Network (version: 3.2)
The MGED Ontology (version: 1.3.1)
NCI Thesaurus (version: 08.11d)
HL7 (version: V3 R2.20)
Gene Ontology (version: November2008)
Logical Observation Identifier Names and Codes (version: 222)



=================================================================
Calling LexBIG -- please wait...
******** Current Concept: NRAS1 (code: C16889)

***** scheme: NCI Thesaurus

***** hierarchyID: is_a

***** code: C16889
    hasSubtype->C17061:Oncogene RAS
        hasSubtype->C18340:Oncogenes, G-Proteins
            hasSubtype->C16936:Cancer-Promoting Gene
                hasSubtype->C19540:Cancer Gene
                    hasSubtype->C16612:Gene
     C16612
sub: 32
         C19540
sub: 13
             C16936
sub: 5
                 C18340
sub: 2
                     C17061
sub: 3
Run time (ms): 6541





*/
