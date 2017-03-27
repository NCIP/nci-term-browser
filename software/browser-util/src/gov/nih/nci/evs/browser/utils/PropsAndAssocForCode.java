package gov.nih.nci.evs.browser.utils;

import java.io.*;
import java.util.Enumeration;

import org.LexGrid.commonTypes.PropertyQualifier;
import org.LexGrid.commonTypes.Source;

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
import org.LexGrid.concepts.Entity;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;

import org.LexGrid.concepts.Comment;
import org.LexGrid.concepts.Definition;
import org.LexGrid.concepts.Presentation;

import org.LexGrid.LexBIG.DataModel.Core.NameAndValue;

import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.client.ApplicationServiceProvider;
import org.LexGrid.LexBIG.caCore.interfaces.LexEVSApplicationService;

import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;


public class PropsAndAssocForCode {

	private String serviceUrl = null;
	LexBIGService lbSvc = null;

	public PropsAndAssocForCode() {
		lbSvc = getLexBIGService();
	}

	public PropsAndAssocForCode(LexBIGService lbSvc) {
		this.lbSvc = lbSvc;
	}

    public LexBIGService getLexBIGService() {
		LexBIGServiceImpl lbSvc = null;
		try {
			lbSvc = LexBIGServiceImpl.defaultInstance();

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return lbSvc;
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
    public void run(PrintWriter pw, String scheme, String version, String code) throws LBException {
		CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
		if (version != null) csvt.setVersion(version);
		printProps(pw, code, lbSvc, scheme, csvt);
		printFrom(pw, code, lbSvc, scheme, csvt);
		printTo(pw, code, lbSvc, scheme, csvt);
    }



    public void run(PrintWriter pw, String scheme, String version, String code, String entityType) throws LBException {
		CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
		if (version != null) csvt.setVersion(version);
		printProps(pw, code, lbSvc, scheme, csvt, entityType);
		printFrom(pw, code, lbSvc, scheme, csvt);
		printTo(pw, code, lbSvc, scheme, csvt);
    }




    void displayMessage(String s) {
		displayMessage(null, s);
	}

    void displayMessage(PrintWriter pw, String s) {
	    if (pw == null) {
			System.out.println(s);
		} else {
			pw.println(s);
		}
	}

    public CodedNodeSet getCodedNodeSet(String scheme, CodingSchemeVersionOrTag csvt, String entityType) {
		LocalNameList lnl = new LocalNameList();
		lnl.addEntry(entityType);
		CodedNodeSet cns = null;
		try {
			cns = lbSvc.getNodeSet(scheme, csvt, lnl);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return cns;
    }

    public CodedNodeSet getCodedNodeSet(String scheme, CodingSchemeVersionOrTag csvt) {
		return getCodedNodeSet(scheme, csvt, "concept");
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

    public boolean printProps(PrintWriter pw, String code, LexBIGService lbSvc, String scheme, CodingSchemeVersionOrTag csvt)
            throws LBException {
		return printProps(pw, code, lbSvc, scheme, csvt, "concept");
	}


    public boolean printProps(PrintWriter pw, String code, LexBIGService lbSvc, String scheme, CodingSchemeVersionOrTag csvt, String entityType)
            throws LBException {
        ConceptReferenceList crefs = ConvenienceMethods.createConceptReferenceList(new String[] { code }, scheme);

        //CodedNodeSet cns = lbSvc.getCodingSchemeConcepts(scheme, csvt);

        CodedNodeSet cns = getCodedNodeSet(scheme, csvt, entityType);
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

                 String representationalForm = presentation.getRepresentationalForm();
                 StringBuffer buf = new StringBuffer();
                 buf.append("\tPresentation name: " + presentation.getPropertyName());
                 buf.append(" text: " + presentation.getValue().getContent());
                 buf.append(" form: " + representationalForm);
                 displayMessage(pw, buf.toString());


                 PropertyQualifier[] qualifiers = presentation.getPropertyQualifier();
                 for (int k=0; k<qualifiers.length; k++) {
                      PropertyQualifier qualifier = qualifiers[k];
                      buf = new StringBuffer();
                      buf.append("\t\tQualifier name: " + qualifier.getPropertyQualifierName());
                      buf.append(" text: " + qualifier.getValue().getContent());
                      displayMessage(pw, buf.toString());
				 }

                 Source[] sources = presentation.getSource();
                 for (int k=0; k<sources.length; k++) {
                      Source source = sources[k];
						 displayMessage(pw, new StringBuffer().append("\t\tSource: ").append(source.getContent()).toString());
				 }

            }
            System.out.println("\n");

            Definition[] definitions = node.getDefinition();
            for (int i = 0; i < definitions.length; i++) {
                Definition definition = definitions[i];
                displayMessage(pw, new StringBuffer().append("\tDefinition name: ").append(definition.getPropertyName())
                        .append(" text: ").append(definition.getValue().getContent()).toString());

                 Source[] sources = definition.getSource();
                 for (int j=0; j<sources.length; j++) {
					 Source src = sources[j];
					 displayMessage(pw, new StringBuffer().append("\t\tSource: ").append(src.getContent()).toString());
				 }

				 PropertyQualifier[] qualifiers = definition.getPropertyQualifier();
				 if (qualifiers != null && qualifiers.length > 0) {
					 for (int j = 0; j < qualifiers.length; j++) {
						 PropertyQualifier q = qualifiers[j];
						 String qualifier_name = q.getPropertyQualifierName();
						 String qualifier_value = q.getValue().getContent();
                         displayMessage(pw, new StringBuffer().append("\t\tQualifier - ").append(qualifier_name + ": ").append(qualifier_value).toString());
					 }
				 }
            }

            System.out.println("\n");

            Comment[] comments = node.getComment();
            for (int i = 0; i < comments.length; i++) {
                Comment comment = comments[i];
                displayMessage(pw, new StringBuffer().append("\tComment name: ").append(comment.getPropertyName())
                        .append(" text: ").append(comment.getValue().getContent()).toString());

 				 PropertyQualifier[] qualifiers = comment.getPropertyQualifier();
				 if (qualifiers != null && qualifiers.length > 0) {
					 for (int j = 0; j < qualifiers.length; j++) {
						 PropertyQualifier q = qualifiers[j];
						 String qualifier_name = q.getPropertyQualifierName();
						 String qualifier_value = q.getValue().getContent();
                         displayMessage(pw, new StringBuffer().append("\t\tQualifier - ").append(qualifier_name + ": ").append(qualifier_value).toString());
					 }
				 }


            }
            System.out.println("\n");

            Property[] props = node.getProperty();
            for (int i = 0; i < props.length; i++) {
                Property prop = props[i];
                displayMessage(pw, new StringBuffer().append("\tProperty name: ").append(prop.getPropertyName())
                        .append(" text: ").append(prop.getValue().getContent()).toString());

  				 PropertyQualifier[] qualifiers = prop.getPropertyQualifier();
				 if (qualifiers != null && qualifiers.length > 0) {
					 for (int j = 0; j < qualifiers.length; j++) {
						 PropertyQualifier q = qualifiers[j];
						 String qualifier_name = q.getPropertyQualifierName();
						 String qualifier_value = q.getValue().getContent();
                         displayMessage(pw, new StringBuffer().append("\t\tQualifier - ").append(qualifier_name + ": ").append(qualifier_value).toString());
					 }
				 }


            }
            System.out.println("\n");


            System.out.println("\n=========================================================================");

            props = node.getAllProperties();
            for (int i = 0; i < props.length; i++) {
                Property prop = props[i];
                displayMessage(pw, new StringBuffer().append("\tProperty name: ").append(prop.getPropertyName())
                        .append(" text: ").append(prop.getValue().getContent()).toString());

   				 PropertyQualifier[] qualifiers = prop.getPropertyQualifier();
				 if (qualifiers != null && qualifiers.length > 0) {
					 for (int j = 0; j < qualifiers.length; j++) {
						 PropertyQualifier q = qualifiers[j];
						 String qualifier_name = q.getPropertyQualifierName();
						 String qualifier_value = q.getValue().getContent();
                         displayMessage(pw, new StringBuffer().append("\t\tQualifier - ").append(qualifier_name + ": ").append(qualifier_value).toString());
					 }
				 }



            }
            System.out.println("\n");

        } else {
            displayMessage(pw, "No match found!");
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
    protected void printFrom(PrintWriter pw, String code, LexBIGService lbSvc, String scheme, CodingSchemeVersionOrTag csvt)
            throws LBException {
        displayMessage(pw, "\n\tPointed at by ...");

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
					if (targetof != null) {
						Association[] associations = targetof.getAssociation();
						if (associations != null && associations.length > 0) {
							for (int i = 0; i < associations.length; i++) {
								Association assoc = associations[i];
								//displayMessage(pw, "\t" + assoc.getAssociationName());

								AssociatedConcept[] acl = assoc.getAssociatedConcepts().getAssociatedConcept();
								for (int j = 0; j < acl.length; j++) {
									AssociatedConcept ac = acl[j];

									String rela = replaceAssociationNameByRela(ac, assoc.getAssociationName());
									EntityDescription ed = ac.getEntityDescription();
									displayMessage(pw, "\t\t" + ac.getConceptCode() + "/"
											+ (ed == null ? "**No Description**" : ed.getContent()) + " --> (" + rela + ") --> " + code);
									if (ac.getAssociationQualifiers() != null && ac.getAssociationQualifiers().getNameAndValue() != null) {
												for(NameAndValue nv: ac.getAssociationQualifiers().getNameAndValue()){
										displayMessage(pw, "\t\t\tAssoc Qualifier - " + nv.getName() + ": " + nv.getContent());
										displayMessage(pw, "\n");
												}

									}
								}
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
    protected void printTo(PrintWriter pw, String code, LexBIGService lbSvc, String scheme, CodingSchemeVersionOrTag csvt)
            throws LBException {
        displayMessage(pw, "\n\tPoints to ...");

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

                if (sourceof != null) {
					Association[] associations = sourceof.getAssociation();

					if (associations != null && associations.length > 0) {
						for (int i = 0; i < associations.length; i++) {
							Association assoc = associations[i];
							//displayMessage(pw, "\t" + assoc.getAssociationName());

							AssociatedConcept[] acl = assoc.getAssociatedConcepts().getAssociatedConcept();
							for (int j = 0; j < acl.length; j++) {
								AssociatedConcept ac = acl[j];

								String rela = replaceAssociationNameByRela(ac, assoc.getAssociationName());

								EntityDescription ed = ac.getEntityDescription();
								displayMessage(pw, "\t\t" + code + " --> (" + rela + ") --> " + ac.getConceptCode() + "/"
										+ (ed == null ? "**No Description**" : ed.getContent()));
								if (ac.getAssociationQualifiers() != null && ac.getAssociationQualifiers().getNameAndValue() != null) {
											for(NameAndValue nv: ac.getAssociationQualifiers().getNameAndValue()){
									displayMessage(pw, "\t\t\tAssoc Qualifier - " + nv.getName() + ": " + nv.getContent());
									displayMessage(pw, "\n");
											}

								}
							}
						}
					}
			    }
            }
        }
    }



    private String replaceAssociationNameByRela(AssociatedConcept ac, String associationName) {
		return associationName;
	}

    public static void main(String[] args) {
		String scheme = "owl2lexevs2.owl";
		String version = "0.1.2";
		String code = "in_organism";
		String entityType = "association";
		code = "http://purl.obolibrary.org/obo/CL_0000000";

		scheme = "PDQ";
		version = null;//"2013_0131";
        entityType = "concept";
		code = "CDR0000459776";


        PrintWriter pw = null;
        try {
            new PropsAndAssocForCode().run(null, scheme, version, code, entityType);
        } catch (Exception e) {
            displayAndLogError("REQUEST FAILED !!!", e);
        }
    }
}

