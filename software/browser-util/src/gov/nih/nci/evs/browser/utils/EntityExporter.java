package gov.nih.nci.evs.browser.utils;

import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.client.ApplicationServiceProvider;
import java.io.*;
import java.util.Enumeration;
import java.util.*;
import org.LexGrid.LexBIG.DataModel.Collections.AssociationList;
import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeTagList;
import org.LexGrid.LexBIG.DataModel.Collections.ConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.AssociatedConcept;
import org.LexGrid.LexBIG.DataModel.Core.Association;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.NameAndValue;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.RenderingDetail;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Extensions.Generic.LexBIGServiceConvenienceMethods;
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

import org.LexGrid.concepts.Concept;
import org.LexGrid.commonTypes.Text;
import org.LexGrid.commonTypes.Source;


public class EntityExporter {
    private LexBIGServiceConvenienceMethods lbscm = null;
    LexBIGService lbSvc = null;
    PrintWriter pw = null;
    String outputfile = null;

    public EntityExporter(LexBIGService lbSvc) {
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

    public void setOutputfile(String outputfile) {
		this.outputfile = outputfile;
	}


    void displayAndLogError(String s, Exception e) {
		pw.println(s);
		e.printStackTrace();
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
        if (ltag != null && ltag.compareToIgnoreCase("PRODUCTION") == 0 & knt == 1) {
            return version;
        }
        return null;
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


    /**
     * Process the provided code.
     *
     * @param code
     * @throws LBException
     */


     public void exportEntity(String scheme, String version, String code) throws LBException {
        try {
			if (version == null) {
				version = getVocabularyVersionByTag(scheme, "PRODUCTION");
				System.out.println("version: " + version);
			}

			this.outputfile = code + ".txt";
			PrintWriter pw = new PrintWriter(outputfile, "UTF-8");
			CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
			if (version != null) csvt.setVersion(version);
			printProps(pw, scheme, csvt, code);
			printFrom(pw,  scheme, csvt, code);
			printTo(pw, scheme, csvt, code);
	    } catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				pw.close();
				System.out.println("Output file " + outputfile + " generated.");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
    }


	public static Vector readFile(String filename)
	{
		Vector v = new Vector();
		try {
            FileReader a = new FileReader(filename);
            BufferedReader br = new BufferedReader(a);
            String line;
            line = br.readLine();
            while(line != null){
                v.add(line);
                line = br.readLine();
            }
            br.close();
		} catch (Exception ex) {
            ex.printStackTrace();
		}
		return v;
	}


    public void exportEntity(PrintWriter pw, String scheme, String version, String code) throws LBException {
        try {
			if (version == null) {
				version = getVocabularyVersionByTag(scheme, "PRODUCTION");
				System.out.println("version: " + version);
			}
			CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
			if (version != null) csvt.setVersion(version);
			printProps(pw, scheme, csvt, code);
			printFrom(pw,  scheme, csvt, code);
			printTo(pw, scheme, csvt, code);
	    } catch (Exception ex) {
			ex.printStackTrace();
		}
    }


    public void printProperties(PrintWriter pw, Entity node) {
        if (node == null) return;
		Presentation[] presentations = node.getPresentation();
		for (int i = 0; i < presentations.length; i++) {
			 Presentation presentation = presentations[i];
			 StringBuffer buf = new StringBuffer();
			 buf.append("Presentation|").append(presentation.getPropertyName())
					 .append("|").append(presentation.getValue().getContent());
			 PropertyQualifier[] qualifiers = presentation.getPropertyQualifier();
			 if (qualifiers.length > 0) {
				 buf.append("$");
				 for (int k=0; k<qualifiers.length; k++) {
					  PropertyQualifier qualifier = qualifiers[k];
					  buf.append(qualifier.getPropertyQualifierName() +"="+ qualifier.getValue().getContent());
					  if (k<qualifiers.length-1){
						  buf.append("|");
					  }
				 }
			 }
			 if (presentation instanceof Presentation) {
				 String pf = presentation.getRepresentationalForm();
				 if (pf != null) {
					 buf.append("$"+ "RepresentationalForm|" + pf);
				 }
			 }

			 Source[] sources = presentation.getSource();
			 if (sources.length > 0) {
				 for (int k=0; k<sources.length; k++) {
					  Source source = sources[k];
					  buf.append("$" + "Source|"+source.getContent());
					  if (k<sources.length-1){
						  buf.append("|");
					  }
				 }
			 }
			 pw.println(buf.toString());
		}


		Definition[] definitions = node.getDefinition();
		for (int i = 0; i < definitions.length; i++) {
			 Definition definition = definitions[i];
			 StringBuffer buf = new StringBuffer();
			 buf.append("Definition|").append(definition.getPropertyName())
					 .append("|").append(definition.getValue().getContent());
			 PropertyQualifier[] qualifiers = definition.getPropertyQualifier();
			 if (qualifiers.length > 0) {
				 buf.append("$");
				 for (int k=0; k<qualifiers.length; k++) {
					  PropertyQualifier qualifier = qualifiers[k];
					  buf.append(qualifier.getPropertyQualifierName() +"="+ qualifier.getValue().getContent());
					  if (k<qualifiers.length-1){
						  buf.append("|");
					  }
				 }
			 }
			 Source[] sources = definition.getSource();
			 if (sources.length > 0) {
				 for (int k=0; k<sources.length; k++) {
					  Source source = sources[k];
					  buf.append("$" + "Source|"+source.getContent());
					  if (k<sources.length-1){
						  buf.append("|");
					  }
				 }
			 }
			 pw.println(buf.toString());
		}

		Comment[] comments = node.getComment();
		for (int i = 0; i < comments.length; i++) {
			 Comment comment = comments[i];
			 StringBuffer buf = new StringBuffer();
			 buf.append("Comment|").append(comment.getPropertyName())
					 .append("|").append(comment.getValue().getContent());
			 PropertyQualifier[] qualifiers = comment.getPropertyQualifier();
			 if (qualifiers.length > 0) {
				 buf.append("$");
				 for (int k=0; k<qualifiers.length; k++) {
					  PropertyQualifier qualifier = qualifiers[k];
					  buf.append(qualifier.getPropertyQualifierName() +"="+ qualifier.getValue().getContent());
					  if (k<qualifiers.length-1){
						  buf.append("|");
					  }
				 }
			 }
			 Source[] sources = comment.getSource();
			 if (sources.length > 0) {
				 for (int k=0; k<sources.length; k++) {
					  Source source = sources[k];
					  buf.append("$" + "Source|"+source.getContent());
					  if (k<sources.length-1){
						  buf.append("|");
					  }
				 }
			 }
			 pw.println(buf.toString());
		}

		Property[] properties = node.getProperty();
		for (int i = 0; i < properties.length; i++) {
			 Property property = properties[i];
			 StringBuffer buf = new StringBuffer();
			 buf.append("Property|").append(property.getPropertyName())
					 .append("|").append(property.getValue().getContent());
			 PropertyQualifier[] qualifiers = property.getPropertyQualifier();
			 if (qualifiers.length > 0) {
				 buf.append("$");
				 for (int k=0; k<qualifiers.length; k++) {
					  PropertyQualifier qualifier = qualifiers[k];
					  buf.append(qualifier.getPropertyQualifierName() +"="+ qualifier.getValue().getContent());
					  if (k<qualifiers.length-1){
						  buf.append("|");
					  }
				 }
			 }
			 Source[] sources = property.getSource();
			 if (sources.length > 0) {
				 for (int k=0; k<sources.length; k++) {
					  Source source = sources[k];
					  buf.append("$" + "Source|"+source.getContent());
					  if (k<sources.length-1){
						  buf.append("|");
					  }
				 }
			 }
			 pw.println(buf.toString());
		}
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
    protected boolean printProps(PrintWriter pw, String scheme, CodingSchemeVersionOrTag csvt, String code)
            throws LBException {
        // Perform the query ...
        pw.println("Coding scheme|" + scheme);
        pw.println("Version|" + csvt.getVersion());
        List<String> namespaces = getDistinctNamespacesOfCode(scheme, csvt.getVersion(), code);
        String namespace = namespaces.get(0);
        pw.println("Namespace|" + namespace);
        pw.println("code|" + code);

        ConceptReferenceList crefs = ConvenienceMethods.createConceptReferenceList(new String[] { code }, scheme);
        CodedNodeSet cns = lbSvc.getCodingSchemeConcepts(scheme, csvt);

		if (cns == null) {
			pw.println("CNS == NULL???");
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
            printProperties(pw, node);
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
    protected void printFrom(PrintWriter pw, String scheme, CodingSchemeVersionOrTag csvt, String code)
            throws LBException {
        pw.println("Pointed at by ...");

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
						pw.println("\t" + assoc.getAssociationName());

						AssociatedConcept[] acl = assoc.getAssociatedConcepts().getAssociatedConcept();
						for (int j = 0; j < acl.length; j++) {
							AssociatedConcept ac = acl[j];
							String rela = replaceAssociationNameByRela(ac, assoc.getAssociationName());
							EntityDescription ed = ac.getEntityDescription();
							pw.println("\t\t" + ac.getConceptCode() + "/"
									+ (ed == null ? "**No Description**" : ed.getContent()) + " --> (" + rela + ") --> " + code);
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
    protected void printTo(PrintWriter pw, String scheme, CodingSchemeVersionOrTag csvt, String code)
            throws LBException {
        pw.println("Points to ...");

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
                    pw.println("\t" + assoc.getAssociationName());

                    AssociatedConcept[] acl = assoc.getAssociatedConcepts().getAssociatedConcept();
                    for (int j = 0; j < acl.length; j++) {
                        AssociatedConcept ac = acl[j];
                        String rela = replaceAssociationNameByRela(ac, assoc.getAssociationName());

                        EntityDescription ed = ac.getEntityDescription();
                        pw.println("\t\t" + code + " --> (" + rela + ") --> " + ac.getConceptCode() + "/"
                                + (ed == null ? "**No Description**" : ed.getContent()));
                    }
                }
            }
        }
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

//////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void dumpProperty(Property property) {
		System.out.println("\n" + property.getPropertyType());
		System.out.println(property.getPropertyName() + ": " + property.getValue().getContent());

		PropertyQualifier[] qualifiers = property.getPropertyQualifier();
		if (qualifiers != null) {
			System.out.println("Property Qualifiers: " );
			for (int i=0; i<qualifiers.length; i++) {
				PropertyQualifier qualifier = qualifiers[i];
				System.out.println("\t" + qualifier.getPropertyQualifierName() + ": " + qualifier.getValue().getContent());
			}
		}
		Source[] sources = property.getSource();

		if (sources != null) {
			System.out.println("Sources: " );
			for (int i=0; i<sources.length; i++) {
				Source source = sources[i];
				System.out.println("\t" + source.getContent());
			}
	    }
	    if (property instanceof Presentation) {
			Presentation presentation = (Presentation) property;
			if (presentation.getRepresentationalForm() != null) {
				System.out.println("RepresentationalForm: " + presentation.getRepresentationalForm());
		    }
		}
	}

    public Property createProperty(String t) {
		Vector u = StringUtils.parseData(t, "$");
		String t0 = (String) u.elementAt(0);
		u = StringUtils.parseData(t0);
		String type = (String) u.elementAt(0);
		Property property = new Property();
		type = type.toUpperCase();
		property.setPropertyType(type);
		String property_name = (String) u.elementAt(1);
		property.setPropertyName(property_name);
		Text text = new Text();
		String property_value = (String) u.elementAt(2);
		text.setContent(property_value);
		property.setValue(text);
		if (t.indexOf("=") != -1) {
		    Vector w = StringUtils.parseData(t, "$");
		    String qualifier_str = (String) w.elementAt(1);
		    w = StringUtils.parseData(qualifier_str);
		    PropertyQualifier[] qualifiers = new PropertyQualifier[w.size()];
		    for (int i=0; i<w.size(); i++) {
				String s = (String) w.elementAt(i);
				Vector w2 = StringUtils.parseData(s, "=");
				String prop_qual_type = null;//???
				String prop_qual_name = (String) w2.elementAt(0);
				String prop_qual_value = (String) w2.elementAt(1);

                PropertyQualifier qualifier = new PropertyQualifier();
				text = new Text();
				text.setContent(prop_qual_value);
				qualifier.setPropertyQualifierName(prop_qual_name);
				qualifier.setPropertyQualifierType(prop_qual_type);
				qualifier.setValue(text);
				qualifiers[i] = qualifier;
			}
			property.setPropertyQualifier(qualifiers);
		}
		if (t.indexOf("$Source") != -1) {
		    int n = t.lastIndexOf("|");
			String src = t.substring(n+1, t.length());
			Source[] sources = new Source[1];
			Source source = new Source();//src, null, null, true);
			source.setContent(src);
			sources[0] = source;
			property.setSource(sources);
		}
		return property;
	}

    public Definition createDefinitionProperty(String t) {
		Vector u = StringUtils.parseData(t, "$");
		String t0 = (String) u.elementAt(0);
		u = StringUtils.parseData(t0);
		String type = (String) u.elementAt(0);
		Definition property = new Definition();
		type = type.toUpperCase();
		property.setPropertyType(type);
		String property_name = (String) u.elementAt(1);
		property.setPropertyName(property_name);
		Text text = new Text();
		String property_value = (String) u.elementAt(2);
		text.setContent(property_value);
		property.setValue(text);
		if (t.indexOf("=") != -1) {
		    Vector w = StringUtils.parseData(t, "$");
		    String qualifier_str = (String) w.elementAt(1);
		    w = StringUtils.parseData(qualifier_str);
		    PropertyQualifier[] qualifiers = new PropertyQualifier[w.size()];
		    for (int i=0; i<w.size(); i++) {
				String s = (String) w.elementAt(i);
				Vector w2 = StringUtils.parseData(s, "=");
				String prop_qual_type = null;//???
				String prop_qual_name = (String) w2.elementAt(0);
				String prop_qual_value = (String) w2.elementAt(1);
                PropertyQualifier qualifier = new PropertyQualifier();
				text = new Text();
				text.setContent(prop_qual_value);
				qualifier.setPropertyQualifierName(prop_qual_name);
				qualifier.setPropertyQualifierType(prop_qual_type);
				qualifier.setValue(text);
				qualifiers[i] = qualifier;
			}
			property.setPropertyQualifier(qualifiers);
		}
		if (t.indexOf("$Source") != -1) {
		    int n = t.lastIndexOf("|");
			String src = t.substring(n+1, t.length());
			Source[] sources = new Source[1];
			Source source = new Source();//src, null, null, true);
			source.setContent(src);
			sources[0] = source;
			property.setSource(sources);
		}
		return property;
	}

    public Comment createCommentProperty(String t) {
		Vector u = StringUtils.parseData(t, "$");
		String t0 = (String) u.elementAt(0);
		u = StringUtils.parseData(t0);
		String type = (String) u.elementAt(0);
		Comment property = new Comment();
		type = type.toUpperCase();
		property.setPropertyType(type);
		String property_name = (String) u.elementAt(1);
		property.setPropertyName(property_name);
		Text text = new Text();
		String property_value = (String) u.elementAt(2);
		text.setContent(property_value);
		property.setValue(text);
		if (t.indexOf("=") != -1) {
		    Vector w = StringUtils.parseData(t, "$");
		    String qualifier_str = (String) w.elementAt(1);
		    w = StringUtils.parseData(qualifier_str);
		    PropertyQualifier[] qualifiers = new PropertyQualifier[w.size()];
		    for (int i=0; i<w.size(); i++) {
				String s = (String) w.elementAt(i);
				Vector w2 = StringUtils.parseData(s, "=");
				String prop_qual_type = null;//???
				String prop_qual_name = (String) w2.elementAt(0);
				String prop_qual_value = (String) w2.elementAt(1);

                PropertyQualifier qualifier = new PropertyQualifier();
				text = new Text();
				text.setContent(prop_qual_value);
				qualifier.setPropertyQualifierName(prop_qual_name);
				qualifier.setPropertyQualifierType(prop_qual_type);
				qualifier.setValue(text);
				qualifiers[i] = qualifier;
			}
			property.setPropertyQualifier(qualifiers);
		}
		if (t.indexOf("$Source") != -1) {
		    int n = t.lastIndexOf("|");
			String src = t.substring(n+1, t.length());
			Source[] sources = new Source[1];
			Source source = new Source();//src, null, null, true);
			source.setContent(src);
			sources[0] = source;
			property.setSource(sources);
		}
		return property;
	}


    public Presentation createPresentationProperty(String t) {
		Vector u = StringUtils.parseData(t, "$");
		String t0 = (String) u.elementAt(0);
		u = StringUtils.parseData(t0);
		Presentation property = new Presentation();
		property.setPropertyType("PRESENTATION");
		String property_name = (String) u.elementAt(1);
		property.setPropertyName(property_name);
		Text text = new Text();
		String property_value = (String) u.elementAt(2);
		text.setContent(property_value);
		property.setValue(text);
		if (t.indexOf("$RepresentationalForm") != -1) {
			Vector w = StringUtils.parseData(t, "$");
			String term_type_str = (String) w.elementAt(1);
			if (t.indexOf("=") != -1) {
				term_type_str = (String) w.elementAt(2);
			}
			w = StringUtils.parseData(term_type_str);
			String term_type = (String) w.elementAt(1);
			property.setRepresentationalForm(term_type);
		}
		if (t.indexOf("=") != -1) {
		    Vector w = StringUtils.parseData(t, "$");
		    String qualifier_str = (String) w.elementAt(1);
		    w = StringUtils.parseData(qualifier_str);
		    PropertyQualifier[] qualifiers = new PropertyQualifier[w.size()];
		    for (int i=0; i<w.size(); i++) {
				String s = (String) w.elementAt(i);
				Vector w2 = StringUtils.parseData(s, "=");
				String prop_qual_type = null;//???
				String prop_qual_name = (String) w2.elementAt(0);
				String prop_qual_value = (String) w2.elementAt(1);

                PropertyQualifier qualifier = new PropertyQualifier();
				text = new Text();
				text.setContent(prop_qual_value);
				qualifier.setPropertyQualifierName(prop_qual_name);
				qualifier.setPropertyQualifierType(prop_qual_type);
				qualifier.setValue(text);
				qualifiers[i] = qualifier;
			}
			property.setPropertyQualifier(qualifiers);
		}
		if (t.indexOf("$Source") != -1) {
		    int n = t.lastIndexOf("|");
			String src = t.substring(n+1, t.length());
			Source[] sources = new Source[1];
			Source source = new Source();//src, null, null, true);
			source.setContent(src);
			sources[0] = source;
			property.setSource(sources);
		}
		return property;
	}

	public Concept load(String filename) {
		Vector v = readFile(filename);
		return load(v);
	}

	public Concept load(Vector v) {
        Concept concept = new Concept();

        Vector<Presentation> v_pres = new Vector();
        Vector<Definition> v_def = new Vector();
        Vector<Comment> v_com = new Vector();
        Vector<Property> v_prop = new Vector();

        String label = null;
        for (int i=0; i<v.size(); i++) {
			String t = (String) v.elementAt(i);
			if (t.startsWith("Presentation|label")) {
				Vector u = StringUtils.parseData(t);
				label = (String) u.elementAt(2);
			}
		}
		EntityDescription entityDescription = new EntityDescription();
		entityDescription.setContent(label);
		concept.setEntityDescription(entityDescription);

        PropertyQualifier propertyQualifier = null;
        for (int i=0; i<v.size(); i++) {
			String t = (String) v.elementAt(i);
			Vector u = StringUtils.parseData(t);
			if (u != null && u.size() > 2) {
				String s0 = (String) u.elementAt(0);
				String s1 = (String) u.elementAt(1);
				if (s0.compareTo("Coding scheme") == 0) {
					//concept.setCodingScheme(s1);
				} else if (s0.compareTo("Version") == 0) {
					//concept.setCodingSchemeVersion(s1);
				} else if (s0.compareTo("Namespace") == 0) {
					concept.setEntityCodeNamespace(s1);
				} else if (s0.compareTo("code") == 0) {
					concept.setEntityCode(s1);
				} else if (s0.compareTo("Presentation") == 0) {
					Presentation presentation = (Presentation) createPresentationProperty(t);
					dumpProperty(presentation);
					v_pres.add(presentation);
				} else if (s0.compareTo("Definition") == 0) {
					Definition property = createDefinitionProperty(t);
					dumpProperty(property);
					v_def.add(property);
				} else if (s0.compareTo("Comment") == 0) {
					Comment property = createCommentProperty(t);
					dumpProperty(property);
					v_com.add(property);
				} else if (s0.compareTo("Property") == 0) {
					Property property = createProperty(t);
					dumpProperty(property);
					v_prop.add(property);
				}
			}
		}
		Presentation[] presentations = v_pres.toArray(new Presentation[v_pres.size()]);
		Definition[] definitions = v_def.toArray(new Definition[v_def.size()]);
		Comment[] comments = v_com.toArray(new Comment[v_com.size()]);
		Property[] properties = v_prop.toArray(new Property[v_prop.size()]);

		concept.setPresentation(presentations);
		if (presentations != null) {
			//concept.setPresentationCount(presentations.length);
		}
		concept.setDefinition(definitions);
		if (definitions != null) {
			//concept.setDefinitionCount(definitions.length);
		}

		concept.setComment(comments);
		if (comments != null) {
			//concept.setCommentCount(comments.length);
		}

		concept.setProperty(properties);
		if (properties != null) {
			//concept.setPropertyCount(properties.length);
		}
        return concept;
	}

}
