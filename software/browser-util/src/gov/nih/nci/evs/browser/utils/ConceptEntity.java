/*
 * Copyright: (c) 2004-2009 Mayo Foundation for Medical Education and
 * Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
 * triple-shield Mayo logo are trademarks and service marks of MFMER.
 *
 * Except as contained in the copyright notice above, or as used to identify
 * MFMER as the author of this software, the trade names, trademarks, service
 * marks, or product names of the copyright holder shall not be used in
 * advertising, promotion or otherwise in connection with this software without
 * prior written authorization of the copyright holder.
 *
 * Licensed under the Eclipse Public License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.eclipse.org/legal/epl-v10.html
 *
 */
//package org.LexGrid.LexBIG.example;


package gov.nih.nci.evs.browser.utils;

import java.io.*;
import java.net.URI;
import java.text.*;
import java.util.*;
import java.sql.*;

import gov.nih.nci.system.client.ApplicationServiceProvider;
import org.LexGrid.LexBIG.DataModel.Collections.AbsoluteCodingSchemeVersionReferenceList;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.LexBIG.caCore.interfaces.LexEVSDistributed;
import org.LexGrid.naming.Mappings;
import org.LexGrid.naming.SupportedCodingScheme;
import org.LexGrid.util.PrintUtility;
import org.LexGrid.valueSets.DefinitionEntry;
import org.LexGrid.valueSets.EntityReference;
import org.LexGrid.valueSets.PropertyMatchValue;
import org.LexGrid.valueSets.PropertyReference;
import org.LexGrid.valueSets.ValueSetDefinition;
import org.LexGrid.valueSets.types.DefinitionOperator;
import org.lexgrid.valuesets.LexEVSValueSetDefinitionServices;
import org.lexgrid.valuesets.dto.ResolvedValueSetDefinition;

import org.LexGrid.LexBIG.DataModel.Collections.*;
import org.LexGrid.LexBIG.DataModel.Core.*;
import org.LexGrid.LexBIG.Exceptions.*;
import org.LexGrid.LexBIG.History.*;
import org.LexGrid.LexBIG.LexBIGService.*;
import org.LexGrid.LexBIG.Utility.*;
import org.LexGrid.concepts.*;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.*;
import org.LexGrid.LexBIG.Utility.Iterators.*;
import org.LexGrid.codingSchemes.*;
import org.LexGrid.commonTypes.*;
import org.LexGrid.relations.Relations;
import org.LexGrid.versions.*;
import org.LexGrid.naming.*;
import org.LexGrid.LexBIG.DataModel.Core.types.*;
import org.LexGrid.LexBIG.Extensions.Generic.*;


import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.Direction;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.MappingSortOption;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.MappingSortOptionName;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.QualifierSortOption;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.LexBIG.caCore.interfaces.LexEVSDistributed;

import org.LexGrid.LexBIG.Extensions.Generic.SupplementExtension;
import org.LexGrid.relations.AssociationPredicate;

import org.LexGrid.LexBIG.caCore.interfaces.LexEVSDistributed;
import org.lexgrid.valuesets.LexEVSValueSetDefinitionServices;

import org.LexGrid.valueSets.ValueSetDefinition;
import org.LexGrid.commonTypes.Source;


import org.apache.log4j.*;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.Mapping;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.Mapping.SearchContext;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.caCore.interfaces.LexEVSApplicationService;
import org.lexgrid.valuesets.impl.LexEVSValueSetDefinitionServicesImpl;

import org.json.*;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.ActiveOption;


public class ConceptEntity {
    static String serviceUrl = "https://lexevsapi6.nci.nih.gov/lexevsapi64";
    String entityDescription = null;
    LexBIGService lbSvc = null;

    public ConceptEntity() {
        //super();
    }

    public ConceptEntity(LexBIGService lbSvc) {
        //super();
        this.lbSvc = lbSvc;
    }


    static void displayAndLogError(String s, Exception e) {
		System.out.println(s);
		e.printStackTrace();
	}

    /**
     * Process the provided code.
     *
     * @param code
     * @throws LBException
     */
    public void run(String scheme, String version, String code) throws LBException {
	    //LexBIGService lbSvc = createLexBIGService();
		CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
		if (version != null) csvt.setVersion(version);
		printProps(code, scheme, csvt);
		printFrom(code, scheme, csvt);
		printTo(code, scheme, csvt);
    }


    public void displayMessage(String s) {
	   System.out.println(s);
	}


    /**
     * Display properties for the given code.
     *
     * @param code
     * @param lbSvc
     * @param scheme
     * @param csvt
     * @return
     * @throws LBException
     */
    protected boolean printProps(String code, String scheme, CodingSchemeVersionOrTag csvt)
            throws LBException {
        // Perform the query ...

        System.out.println("======================================================================");
        System.out.println("Coding scheme: " + scheme);
        System.out.println("Coding scheme version: " + csvt.getVersion());
        System.out.println("code: " + code);
        System.out.println("======================================================================");

        ConceptReferenceList crefs = ConvenienceMethods.createConceptReferenceList(new String[] { code }, scheme);

        CodedNodeSet cns = lbSvc.getCodingSchemeConcepts(scheme, csvt);

if (cns == null) {
System.out.println("CNS == NULL???");
return false;
}


        cns = cns.restrictToStatus(ActiveOption.ALL, null);
        cns = cns.restrictToCodes(crefs);
        ResolvedConceptReferenceList matches = cns.resolveToList(null, null, null, 1);

        // Analyze the result ...
        if (matches.getResolvedConceptReferenceCount() > 0) {
            ResolvedConceptReference ref = (ResolvedConceptReference) matches.enumerateResolvedConceptReference()
                    .nextElement();

            Entity node = ref.getEntity();

            Presentation[] prsentations = node.getPresentation();
            for (int i = 0; i < prsentations.length; i++) {
                 Presentation presentation = prsentations[i];
                 displayMessage(new StringBuffer().append("\tPresentation name: ").append(presentation.getPropertyName())
                         .append(" text: ").append(presentation.getValue().getContent()).toString());

                 PropertyQualifier[] qualifiers = presentation.getPropertyQualifier();
                 for (int k=0; k<qualifiers.length; k++) {
                      PropertyQualifier qualifier = qualifiers[k];
						 displayMessage(new StringBuffer().append("\t\tQualifier name: ").append(qualifier.getPropertyQualifierName())
								 .append(" text: ").append(qualifier.getValue().getContent()).toString());
				 }

                 Source[] sources = presentation.getSource();
                 for (int k=0; k<sources.length; k++) {
                      Source source = sources[k];
						 displayMessage(new StringBuffer().append("\t\tSource: ").append(source.getContent()).toString());
				 }

            }
            System.out.println("\n");

            Definition[] definitions = node.getDefinition();
            for (int i = 0; i < definitions.length; i++) {
                Definition definition = definitions[i];
                displayMessage(new StringBuffer().append("\tDefinition name: ").append(definition.getPropertyName())
                        .append(" text: ").append(definition.getValue().getContent()).toString());
            }
            System.out.println("\n");

            Comment[] comments = node.getComment();
            for (int i = 0; i < comments.length; i++) {
                Comment comment = comments[i];
                displayMessage(new StringBuffer().append("\tComment name: ").append(comment.getPropertyName())
                        .append(" text: ").append(comment.getValue().getContent()).toString());
            }
            System.out.println("\n");

            Property[] props = node.getProperty();
            for (int i = 0; i < props.length; i++) {
                Property prop = props[i];
                displayMessage(new StringBuffer().append("\tProperty name: ").append(prop.getPropertyName())
                        .append(" text: ").append(prop.getValue().getContent()).toString());
            }
            System.out.println("\n");


            System.out.println("\n=========================================================================");

            props = node.getAllProperties();
            for (int i = 0; i < props.length; i++) {
                Property prop = props[i];
                displayMessage(new StringBuffer().append("\tProperty name: ").append(prop.getPropertyName())
                        .append(" text: ").append(prop.getValue().getContent()).toString());
            }
            System.out.println("\n");

        } else {
            displayMessage("No match found!");
            return false;
        }

        return true;
    }

    /**
     * Display relations to the given code from other concepts.
     *
     * @param code
     * @param lbSvc
     * @param scheme
     * @param csvt
     * @throws LBException
     */
    @SuppressWarnings("unchecked")
    public void printFrom(String code, String scheme, CodingSchemeVersionOrTag csvt)
            throws LBException {
        displayMessage("Pointed at by ...");

        // Perform the query ...
        ResolvedConceptReferenceList matches = lbSvc.getNodeGraph(scheme, csvt, null).resolveAsList(
                ConvenienceMethods.createConceptReference(code, scheme), false, true, 1, 1, new LocalNameList(), null,
                null, 1024);

        // Analyze the result ...
        if (matches.getResolvedConceptReferenceCount() > 0) {
            Enumeration<? extends ResolvedConceptReference> refEnum = matches.enumerateResolvedConceptReference();

            while (refEnum.hasMoreElements()) {
                ResolvedConceptReference ref = refEnum.nextElement();
                AssociationList targetof = ref.getTargetOf();

                if (targetof != null) {
					Association[] associations = targetof.getAssociation();

					for (int i = 0; i < associations.length; i++) {
						Association assoc = associations[i];
						displayMessage("\t" + assoc.getAssociationName());

						AssociatedConcept[] acl = assoc.getAssociatedConcepts().getAssociatedConcept();
						for (int j = 0; j < acl.length; j++) {
							AssociatedConcept ac = acl[j];
							String rela = replaceAssociationNameByRela(ac, assoc.getAssociationName());
							EntityDescription ed = ac.getEntityDescription();
							String concept_code = ac.getConceptCode();
							if (!concept_code.startsWith("@")) {
								displayMessage("\t\t" + ac.getConceptCode() + "/"
									+ (ed == null ? "**No Description**" : ed.getContent()) + " --> (" + rela + ") --> " + code);
						    }
						}
					}
			    }
            }
        }

    }

    public String code2Name(String scheme, String version, String code) {
		ConceptDetails cd = new ConceptDetails(lbSvc);
		String ns = cd.getNamespaceByCode(scheme, version, code);
        Entity entity = new ConceptDetails(lbSvc).getConceptByCode(scheme, version, code, ns, true);
        String name = entity.getEntityDescription().getContent();
        return name;
	}


    public void printFrom(PrintWriter pw, String scheme, String version, String code)
            throws LBException {
        String name = code2Name(scheme, version, code);

		CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
		if (version == null) {
			csvt.setVersion(version);
		}
        // Perform the query ...
        ResolvedConceptReferenceList matches = lbSvc.getNodeGraph(scheme, csvt, null).resolveAsList(
                ConvenienceMethods.createConceptReference(code, scheme), false, true, 1, 1, new LocalNameList(), null,
                null, -1);

        // Analyze the result ...
        if (matches != null && matches.getResolvedConceptReferenceCount() > 0) {
            Enumeration<? extends ResolvedConceptReference> refEnum = matches.enumerateResolvedConceptReference();

            while (refEnum.hasMoreElements()) {
                ResolvedConceptReference ref = refEnum.nextElement();
                AssociationList targetof = ref.getTargetOf();

                if (targetof != null) {
					Association[] associations = targetof.getAssociation();

					for (int i = 0; i < associations.length; i++) {
						Association assoc = associations[i];
						//displayMessage("\t" + assoc.getAssociationName());

						AssociatedConcept[] acl = assoc.getAssociatedConcepts().getAssociatedConcept();
						for (int j = 0; j < acl.length; j++) {
							AssociatedConcept ac = acl[j];
							String rela = replaceAssociationNameByRela(ac, assoc.getAssociationName());
							EntityDescription ed = ac.getEntityDescription();
							String concept_code = ac.getConceptCode();
							if (!concept_code.startsWith("@")) {
								pw.println(ac.getConceptCode() + "|"
									+ (ed == null ? "**No Description**" : ed.getContent()) + "|" + rela
									+ "|" + code + "|" + name);
						    }
						}
					}
			    }
            }
        }
    }


    /**
     * Display relations from the given code to other concepts.
     *
     * @param code
     * @param lbSvc
     * @param scheme
     * @param csvt
     * @throws LBException
     */
    @SuppressWarnings("unchecked")
    protected void printTo(String code, String scheme, CodingSchemeVersionOrTag csvt)
            throws LBException {
        displayMessage("Points to ...");

        // Perform the query ...
        ResolvedConceptReferenceList matches = lbSvc.getNodeGraph(scheme, csvt, null).resolveAsList(
                ConvenienceMethods.createConceptReference(code, scheme), true, false, 1, 1, new LocalNameList(), null,
                null, 1024);

        // Analyze the result ...
        if (matches.getResolvedConceptReferenceCount() > 0) {
            Enumeration<? extends ResolvedConceptReference> refEnum = matches.enumerateResolvedConceptReference();

            while (refEnum.hasMoreElements()) {
                ResolvedConceptReference ref = refEnum.nextElement();
                AssociationList sourceof = ref.getSourceOf();
                Association[] associations = sourceof.getAssociation();

                for (int i = 0; i < associations.length; i++) {
                    Association assoc = associations[i];
                    displayMessage("\t" + assoc.getAssociationName());

                    AssociatedConcept[] acl = assoc.getAssociatedConcepts().getAssociatedConcept();
                    for (int j = 0; j < acl.length; j++) {
                        AssociatedConcept ac = acl[j];
                        String rela = replaceAssociationNameByRela(ac, assoc.getAssociationName());

                        EntityDescription ed = ac.getEntityDescription();
                        displayMessage("\t\t" + code + " --> (" + rela + ") --> " + ac.getConceptCode() + "/"
                                + (ed == null ? "**No Description**" : ed.getContent()));
                    }
                }
            }
        }
    }



    public String replaceAssociationNameByRela(AssociatedConcept ac, String associationName) {
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



    public Vector getProps(String scheme, String version, String code, String propName) throws LBException {
        Vector w = new Vector();
        CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
        if (version != null) {
			csvt.setVersion(version);
		}
        ConceptReferenceList crefs = ConvenienceMethods.createConceptReferenceList(new String[] { code }, scheme);
        CodedNodeSet cns = lbSvc.getCodingSchemeConcepts(scheme, csvt);
        cns = cns.restrictToStatus(ActiveOption.ALL, null);
        cns = cns.restrictToCodes(crefs);
        ResolvedConceptReferenceList matches = cns.resolveToList(null, null, null, 1);
        // Analyze the result ...
        if (matches.getResolvedConceptReferenceCount() > 0) {
            ResolvedConceptReference ref = (ResolvedConceptReference) matches.enumerateResolvedConceptReference()
                    .nextElement();
            Entity node = ref.getEntity();
            Property[] props = node.getAllProperties();
            for (int i = 0; i < props.length; i++) {
                Property prop = props[i];
                if (prop.getPropertyName().compareTo(propName) == 0) {
					w.add(prop.getValue().getContent().toString());
				}
            }
        }
        return w;
    }


    public Vector getOutboundRelationships(String scheme, String version, String code, String name)
            throws LBException {
        Vector w = new Vector();
        CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
        if (version != null) {
			csvt.setVersion(version);
		}

        // Perform the query ...
        ResolvedConceptReferenceList matches = lbSvc.getNodeGraph(scheme, csvt, null).resolveAsList(
                ConvenienceMethods.createConceptReference(code, scheme), true, false, 1, 1, new LocalNameList(), null,
                null, 1024);

        // Analyze the result ...
        if (matches.getResolvedConceptReferenceCount() > 0) {
            Enumeration<? extends ResolvedConceptReference> refEnum = matches.enumerateResolvedConceptReference();

            while (refEnum.hasMoreElements()) {
                ResolvedConceptReference ref = refEnum.nextElement();
                AssociationList sourceof = ref.getSourceOf();
                Association[] associations = sourceof.getAssociation();

                for (int i = 0; i < associations.length; i++) {
                    Association assoc = associations[i];
                    //displayMessage("\t" + assoc.getAssociationName());
                    AssociatedConcept[] acl = assoc.getAssociatedConcepts().getAssociatedConcept();
                    for (int j = 0; j < acl.length; j++) {
                        AssociatedConcept ac = acl[j];
                        String rela = replaceAssociationNameByRela(ac, assoc.getAssociationName());

                        String asso_concept_name = ac.getEntityDescription().getContent();
                        String line = code + "|" + name + "|" + rela + "|outbound|"
                                      + asso_concept_name + "|" + ac.getConceptCode();
                        w.add(line);
                    }
                }
            }
        }
        return w;
    }

    public Vector getInboundRelationships(String scheme, String version, String code, String name)
            throws LBException {
        Vector w = new Vector();
        CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
        if (version != null) {
			csvt.setVersion(version);
		}

        // Perform the query ...
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
					Association[] associations = targetof.getAssociation();
					for (int i = 0; i < associations.length; i++) {
						Association assoc = associations[i];
						//displayMessage("\t" + assoc.getAssociationName());
						AssociatedConcept[] acl = assoc.getAssociatedConcepts().getAssociatedConcept();
						for (int j = 0; j < acl.length; j++) {
							AssociatedConcept ac = acl[j];
							String rela = replaceAssociationNameByRela(ac, assoc.getAssociationName());
							String asso_concept_name = ac.getEntityDescription().getContent();
							String line = code + "|" + name + "|" + rela + "|inbound|"
										  + asso_concept_name + "|" + ac.getConceptCode();
							w.add(line);
						}
					}
			    }
            }
        }
        return w;
    }
}

