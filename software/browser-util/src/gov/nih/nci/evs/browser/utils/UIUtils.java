package gov.nih.nci.evs.browser.utils;

import gov.nih.nci.evs.browser.bean.*;
import java.util.*;
import java.io.*;
import java.util.Map.Entry;

import org.LexGrid.concepts.*;
import org.LexGrid.LexBIG.Utility.Iterators.*;
import org.LexGrid.LexBIG.DataModel.Core.*;

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

import org.LexGrid.LexBIG.Extensions.Generic.LexBIGServiceConvenienceMethods;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.RenderingDetail;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.descriptors.RenderingDetailDescriptor;
import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeTagList;
import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.DataModel.Collections.NameAndValueList;
import org.LexGrid.LexBIG.DataModel.Core.NameAndValue;


public class UIUtils {
    private static Logger _logger = Logger.getLogger(UIUtils.class);
    private LexBIGService lbSvc = null;
    private LexBIGServiceConvenienceMethods lbscm = null;
    private String indent = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
	public UIUtils() {

	}

	public UIUtils(LexBIGService lbSvc) {
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

    public Vector getPropertiesByName(org.LexGrid.commonTypes.Property[] properties , String propertyName) {
		Vector v = new Vector();
        for (int i = 0; i < properties.length; i++) {
            Property p = (Property) properties[i];
            if (p.getPropertyName().compareTo(propertyName) == 0) {
            	v.add(p);
			}
        }
        return v;
    }

    public void generatePropertyTable(Entity concept) {
		String prop_PRESENTATION = generatePropertyTable(concept, "PRESENTATION");
		System.out.println(prop_PRESENTATION);

		String prop_DEFINITION = generatePropertyTable(concept, "DEFINITION");
		System.out.println(prop_DEFINITION);

		String prop_COMMENT = generatePropertyTable(concept, "COMMENT");
		System.out.println(prop_COMMENT);

		String prop_GENERIC = generatePropertyTable(concept, "GENERIC");
		System.out.println(prop_GENERIC);
	}


    public String generatePropertyTable(Entity concept, String property_type) {
		org.LexGrid.commonTypes.Property[] properties = null;

        if (property_type.compareToIgnoreCase("GENERIC") == 0) {
            properties = concept.getProperty();
        } else if (property_type.compareToIgnoreCase("PRESENTATION") == 0) {
            properties = concept.getPresentation();
        } else if (property_type.compareToIgnoreCase("COMMENT") == 0) {
            properties = concept.getComment();
        } else if (property_type.compareToIgnoreCase("DEFINITION") == 0) {
            properties = concept.getDefinition();
        }

		String description = property_type;
		String firstColumnHeading = "Name";
		String secondColumnHeading = "Value";
		int firstPercentColumnWidth = 20;
		int secondPercentColumnWidth = 80;
		int qualifierColumn = 2;

		Vector keyVec = new Vector();
		HashMap qualifierHashMap = new HashMap();

        for (int i = 0; i < properties.length; i++) {
            Property p = (Property) properties[i];
			String name = p.getPropertyName();
			String value = p.getValue().getContent();
			String n_v = name + "|" + value;
			Vector qualifier_vec = new Vector();
			PropertyQualifier[] qualifiers = p.getPropertyQualifier();
			for (int j = 0; j < qualifiers.length; j++) {
				PropertyQualifier q = qualifiers[j];
				String qualifier_name = q.getPropertyQualifierName();
				String qualifier_value = q.getValue().getContent();
				String t = qualifier_name + "|" + qualifier_value;
				qualifier_vec.add(t);
			}
			keyVec.add(n_v);
			qualifier_vec = SortUtils.quickSort(qualifier_vec);
			qualifierHashMap.put(n_v, qualifier_vec);
		}

		keyVec = SortUtils.quickSort(keyVec);

	    HTMLTableSpec spec = new HTMLTableSpec(
			 description,
			 firstColumnHeading,
			 secondColumnHeading,
			 firstPercentColumnWidth,
			 secondPercentColumnWidth,
			 qualifierColumn,
			 keyVec,
			 qualifierHashMap);

		return generateHTMLTable(spec);
	}

	public HTMLTableSpec relationshipList2HTMLTableSpec(
		String description,
		String firstColumnHeading,
		String secondColumnHeading,
		int firstPercentColumnWidth,
		int secondPercentColumnWidth,
		int qualifierColumn,
		ArrayList list) {

		Vector keyVec = new Vector();
		HashMap qualifierHashMap = new HashMap();

		if (list != null && list.size() > 0) {
		    for (int i=0; i<list.size(); i++) {
				String line = (String) list.get(i);
				Vector u = gov.nih.nci.evs.browser.utils.StringUtils.parseData(line);
				String name = (String) u.elementAt(0);
				String value = (String) u.elementAt(1);
				String code = null;
				String codingScheme = null;
				String namespace = null;
				String qualifiers = null;
				if (u.size() > 4) {
					code = (String) u.elementAt(2);
					codingScheme = (String) u.elementAt(3);
					namespace = (String) u.elementAt(4);
				}
				if (u.size() > 5) {
					qualifiers = (String) u.elementAt(5);
				}
				if (qualifiers != null) {
					String key = name + "|" + value + "|" + code + "|" + namespace;
					keyVec.add(key);
					Vector w = gov.nih.nci.evs.browser.utils.StringUtils.parseData(qualifiers, "$");
					Vector w2 = new Vector();
					for (int k=0; k<w.size(); k++) {
						String t = (String) w.elementAt(k);
						t = t.replaceAll(":", "|");
						w2.add(t);
					}
					qualifierHashMap.put(key, w2);
				} else {
					String key = name + "|" + value + "|" + code + "|" + namespace;
					keyVec.add(key);
					qualifierHashMap.put(key, new Vector());
				}
			}
	    }

	    return new HTMLTableSpec(
		    description,
		    firstColumnHeading,
		    secondColumnHeading,
		    firstPercentColumnWidth,
		    secondPercentColumnWidth,
		    qualifierColumn,
		    keyVec,
		    qualifierHashMap);

	}

    public String generateHTMLTable(HTMLTableSpec spec) {
		return generateHTMLTable(spec, null, null);
	}

	public boolean hasQualifiers(Vector qualifiers) {
		if (qualifiers == null || qualifiers.size() == 0) return false;
		for (int j = 0; j < qualifiers.size(); j++) {
			String q = (String) qualifiers.elementAt(j);
			if (q != null && q.length() > 0) return true;
		}
		return false;
	}

    public String generateHTMLTable(HTMLTableSpec spec, String codingScheme, String version) {
		return generateHTMLTable(spec, codingScheme, version, null);
	}


    public String generateHTMLTable(HTMLTableSpec spec, String codingScheme, String version, String rel_type) {
		StringBuffer buf = new StringBuffer();
		HashMap qualifierHashMap = spec.getQualifierHashMap();
		Vector nv_vec = spec.getKeyVec();
		if (nv_vec == null) {
			Iterator entries = qualifierHashMap.entrySet().iterator();
			while (entries.hasNext()) {
				Entry thisEntry = (Entry) entries.next();
				String nv = (String) thisEntry.getKey();
				nv_vec.add(nv);
			}
			nv_vec = SortUtils.quickSort(nv_vec);
		}
		String description = spec.getDescription();
		if (description != null) {
			buf.append(description).append("\n");
		}
		buf.append("<table class=\"datatable_960\" border=\"0\" width=\"100%\">").append("\n");

	    String firstColumnHeading = spec.getFirstColumnHeading();
	    String secondColumnHeading = spec.getSecondColumnHeading();
        if (firstColumnHeading != null && secondColumnHeading != null) {
			buf.append("<tr>").append("\n");
			buf.append("   <th class=\"dataCellText\" scope=\"col\" align=\"left\">" + firstColumnHeading  + "</th>").append("\n");
			buf.append("   <th class=\"dataCellText\" scope=\"col\" align=\"left\">" + secondColumnHeading + "</th>").append("\n");
			buf.append("</tr>").append("\n");
	    }
        int firstPercentColumnWidth = spec.getFirstPercentColumnWidth();
        int secondPercentColumnWidth = spec.getSecondPercentColumnWidth();

        if (firstPercentColumnWidth <= 0 || firstPercentColumnWidth <= 0) {
			buf.append("   <col width=\"50%\">").append("\n");
			buf.append("   <col width=\"50%\">").append("\n");
		} else {
			String w1 = Integer.toString(firstPercentColumnWidth);
			String w2 = Integer.toString(secondPercentColumnWidth);
			buf.append("   <col width=\"" + w1 + "%\">").append("\n");
			buf.append("   <col width=\"" + w2 + "%\">").append("\n");
	    }

	    int qualifierColumn = spec.getQualifierColumn();
		int n = 0;
        for (int i = 0; i < nv_vec.size(); i++) {
            String n_v = (String) nv_vec.elementAt(i);
            Vector w = gov.nih.nci.evs.browser.utils.StringUtils.parseData(n_v);
            String name = "";
            String value = "";

            if (w.size() > 0) {
            	name = (String) w.elementAt(0);
			}

			if (w.size() > 1) {
            	value = (String) w.elementAt(1);
			}

            String code = null;
            String namespace = null;

            if (w.size() > 2) {
				code = (String) w.elementAt(2);
			}
			if (w.size() > 3) {
				namespace = (String) w.elementAt(3);
			}
            Vector qualifiers = (Vector) qualifierHashMap.get(n_v);
            qualifiers = SortUtils.quickSort(qualifiers);

			if ((n++) % 2 == 0) {
				  buf.append("	<tr class=\"dataRowDark\">").append("\n");
			} else {
				  buf.append("	<tr class=\"dataRowLight\">").append("\n");
			}

			if (qualifierColumn == 0) {
                  if (rel_type == null || !rel_type.startsWith("type_inverse")) {
					  buf.append("<td class=\"dataCellText\" valign=\"top\">").append("\n");
					  buf.append("				 " + name).append("\n");
					  buf.append("</td>").append("\n");
					  if (code != null) {
						  value = getHyperlink(codingScheme, version, value, code, namespace);
					  }
					  buf.append("<td class=\"dataCellText\" scope=\"row\" valign=\"top\">" + value + "</td>").append("\n");
				  } else {
					  if (code != null) {
						  value = getHyperlink(codingScheme, version, value, code, namespace);
					  }
					  buf.append("<td class=\"dataCellText\" valign=\"top\">").append("\n");
					  buf.append("				 " + value).append("\n");
					  buf.append("</td>").append("\n");
					  buf.append("<td class=\"dataCellText\" scope=\"row\" valign=\"top\">" + name + "</td>").append("\n");
				  }

			} else if (qualifierColumn == 1) {
                if (hasQualifiers(qualifiers)) {

					if (code != null) {
						value = getHyperlink(codingScheme, version, value, code, namespace);
					}
					buf.append("	  <td class=\"dataCellText\" scope=\"row\" valign=\"top\">").append("\n");
					buf.append("		  <table>").append("\n");
					buf.append("			 <tr>");
					buf.append("<td class=\"dataCellText\">").append("\n");
					buf.append("				 " + value).append("\n");
					buf.append("			 </td></tr>").append("\n");
					for (int j = 0; j < qualifiers.size(); j++) {
						String q = (String) qualifiers.elementAt(j);
						Vector u = gov.nih.nci.evs.browser.utils.StringUtils.parseData(q);

						String qualifier_name = "";
						String qualifier_value = "";

						if (u.size() > 0) {
							qualifier_name = (String) u.elementAt(0);
						}
						if (u.size() > 1) {
							qualifier_value = (String) u.elementAt(1);
						}

						String t = qualifier_name + ":" + qualifier_value;
						if (t.length() > 1) {
							buf.append("			 <tr>").append("\n");
							buf.append("			 <td class=\"dataCellText\" >" + indent + t + "</td>").append("\n");
							buf.append("			 </tr>").append("\n");
					    }
					}

					buf.append("		  </table>").append("\n");
					buf.append("	  </td>").append("\n");
					buf.append("	  <td class=\"dataCellText\" scope=\"row\" valign=\"top\">" + name + "</td>").append("\n");
			    } else {
					if (code != null) {
						value = getHyperlink(codingScheme, version, value, code, namespace);
					}
					buf.append("	  <td class=\"dataCellText\" scope=\"row\" valign=\"top\">" + value + "</td>").append("\n");
					buf.append("	  <td class=\"dataCellText\" scope=\"row\" valign=\"top\">" + name + "</td>").append("\n");
				}

			} else if (qualifierColumn == 2) {

                if (hasQualifiers(qualifiers)) {
					if (code != null) {
						value = getHyperlink(codingScheme, version, value, code, namespace);
					}
					buf.append("	  <td class=\"dataCellText\" scope=\"row\" valign=\"top\">" + name + "</td>").append("\n");

					buf.append("	  <td class=\"dataCellText\" scope=\"row\" valign=\"top\">").append("\n");
					buf.append("		  <table>").append("\n");
					buf.append("			 <tr>");
					buf.append("<td class=\"dataCellText\">").append("\n");
					buf.append("				 " + value).append("\n");
					buf.append("			 </td></tr>").append("\n");
					for (int j = 0; j < qualifiers.size(); j++) {
						String q = (String) qualifiers.elementAt(j);
						Vector u = gov.nih.nci.evs.browser.utils.StringUtils.parseData(q);

						String qualifier_name = "";
						String qualifier_value = "";

						if (u.size() > 0) {
							qualifier_name = (String) u.elementAt(0);
						}
						if (u.size() > 1) {
							qualifier_value = (String) u.elementAt(1);
						}
						String t = qualifier_name + ":" + qualifier_value;
						if (t.length() > 1) {
							buf.append("			 <tr>").append("\n");
							buf.append("			 <td class=\"dataCellText\" >" + indent + t + "</td>").append("\n");
							buf.append("			 </tr>").append("\n");
					    }
					}
					buf.append("		  </table>").append("\n");
					buf.append("	  </td>").append("\n");

			    } else {
					if (code != null) {
						value = getHyperlink(codingScheme, version, value, code, namespace);
					}
					buf.append("	  <td class=\"dataCellText\" scope=\"row\" valign=\"top\">" + name + "</td>").append("\n");
					buf.append("	  <td class=\"dataCellText\" scope=\"row\" valign=\"top\">" + value + "</td>").append("\n");
				}
			}
			buf.append("	</tr>").append("\n");
		}
		buf.append("</table>").append("\n");
        return buf.toString();
	}

    public String getHyperlink(String codingScheme, String version, String name, String code, String ns) {
		if (Arrays.asList(Constants.NON_CONCEPT_TO_CONCEPT_ASSOCIATION).contains(name)) return name;

		StringBuffer buf = new StringBuffer();
		if (gov.nih.nci.evs.browser.utils.StringUtils.isNullOrBlank(ns)) {
			buf.append("<a href=\"/ncitbrowser/ConceptReport.jsp?dictionary=" + codingScheme + "&version=" + version + "&code=" + code + "\">").append("\n");
		} else {
			buf.append("<a href=\"/ncitbrowser/ConceptReport.jsp?dictionary=" + codingScheme + "&version=" + version + "&code=" + code + "&ns=" + ns + "\">").append("\n");
		}
		buf.append(name).append("\n");
		buf.append("</a>").append("\n");
		return buf.toString();
    }

	public String getRelationshipTableLabel(String defaultLabel, String type, boolean isEmpty) {
		String NONE = "<i>(none)</i>";
		StringBuffer buf = new StringBuffer();

		if (type.compareTo(Constants.TYPE_SUPERCONCEPT) == 0) {
			buf.append("<b>Parent Concepts:</b>");
			if (isEmpty) {
				buf.append(" ").append(NONE).append("\n");
			}
		} else if (type.compareTo(Constants.TYPE_SUBCONCEPT) == 0) {
			buf.append("<b>Child Concepts:</b>");
			if (isEmpty) {
				buf.append(" ").append(NONE).append("\n");
			}
		} else if (type.compareTo(Constants.TYPE_ROLE) == 0) {
			buf.append("<b>Role Relationships</b>&nbsp;pointing from the current concept to other concepts:");
			if (isEmpty) {
				buf.append(" ").append(NONE).append("\n");
			} else {
				buf.append("<br/>").append("\n");
				buf.append("<i>(True for the current concept.)</i>").append("\n");
			}

		} else if (type.compareTo(Constants.TYPE_ASSOCIATION) == 0) {
			buf.append("<b>Associations</b>&nbsp;pointing from the current concept to other concepts:");
			if (isEmpty) {
				buf.append(" ").append(NONE).append("\n");
			} else {
				buf.append("<br/>").append("\n");
				buf.append("<i>(True for the current concept.)</i>").append("\n");
			}
		} else if (type.compareTo(Constants.TYPE_INVERSE_ROLE) == 0) {
			buf.append("<b>Incoming Role Relationships</b>&nbsp;pointing from other concepts to the current concept:");
			if (isEmpty) {
				buf.append(" ").append(NONE).append("\n");
			}
		} else if (type.compareTo(Constants.TYPE_INVERSE_ASSOCIATION) == 0) {
			buf.append("<b>Incoming Associations</b>&nbsp;pointing from other concepts to the current concept:");
			if (isEmpty) {
				buf.append(" ").append(NONE).append("\n");
			}
		}
		String label = buf.toString();
		if (label.length() == 0) {
			label = defaultLabel;
		}
	    return label;
    }




    public String generatePropertyTable(Entity concept, Vector property_names, String description) {
		int qualifierColumn = 1;
        return generatePropertyTable(concept, property_names, description, qualifierColumn);
	}


    public String generatePropertyTable(Entity concept, Vector property_names, String description, int qualifierColumn) {
		if (property_names == null) return null;

		org.LexGrid.commonTypes.Property[] properties = null;
		properties = concept.getAllProperties();

		String firstColumnHeading = "Name";
		String secondColumnHeading = "Value";
		secondColumnHeading = "Value (qualifiers indented underneath)";

		int firstPercentColumnWidth = 20;
		int secondPercentColumnWidth = 80;

		Vector keyVec = new Vector();
		HashMap qualifierHashMap = new HashMap();

        for (int i = 0; i < properties.length; i++) {
            Property p = (Property) properties[i];
			String name = p.getPropertyName();
			if (property_names.contains(name)) {
				String value = p.getValue().getContent();
				String n_v = name + "|" + value;
				Vector qualifier_vec = new Vector();
				PropertyQualifier[] qualifiers = p.getPropertyQualifier();
				for (int j = 0; j < qualifiers.length; j++) {
					PropertyQualifier q = qualifiers[j];
					String qualifier_name = q.getPropertyQualifierName();
					String qualifier_value = q.getValue().getContent();
					String t = qualifier_name + "|" + qualifier_value;
					qualifier_vec.add(t);
				}
				keyVec.add(n_v);
				qualifier_vec = SortUtils.quickSort(qualifier_vec);
				qualifierHashMap.put(n_v, qualifier_vec);
			}
		}
		keyVec = SortUtils.quickSort(keyVec);

	    HTMLTableSpec spec = new HTMLTableSpec(
			 description,
			 firstColumnHeading,
			 secondColumnHeading,
			 firstPercentColumnWidth,
			 secondPercentColumnWidth,
			 qualifierColumn,
			 keyVec,
			 qualifierHashMap);

		//return generateHTMLTable(spec);
		return generatePropertyTable(spec, null, null);
	}


    public String generatePropertyTable(HTMLTableSpec spec, String codingScheme, String version) {
		StringBuffer buf = new StringBuffer();
		HashMap qualifierHashMap = spec.getQualifierHashMap();
		Vector nv_vec = spec.getKeyVec();
		if (nv_vec == null) {
			Iterator entries = qualifierHashMap.entrySet().iterator();
			while (entries.hasNext()) {
				Entry thisEntry = (Entry) entries.next();
				String nv = (String) thisEntry.getKey();
				nv_vec.add(nv);
			}
			nv_vec = SortUtils.quickSort(nv_vec);
		}
		String description = spec.getDescription();
		if (description != null) {
			buf.append(description).append("\n");
		}
		buf.append("<table class=\"datatable_960\" border=\"0\" width=\"100%\">").append("\n");

	    String firstColumnHeading = spec.getFirstColumnHeading();
	    String secondColumnHeading = spec.getSecondColumnHeading();
        if (firstColumnHeading != null && secondColumnHeading != null) {
			buf.append("<tr>").append("\n");
			buf.append("   <th class=\"dataCellText\" scope=\"col\" align=\"left\">" + firstColumnHeading  + "</th>").append("\n");
			buf.append("   <th class=\"dataCellText\" scope=\"col\" align=\"left\">" + secondColumnHeading + "</th>").append("\n");
			buf.append("</tr>").append("\n");
	    }
        int firstPercentColumnWidth = spec.getFirstPercentColumnWidth();
        int secondPercentColumnWidth = spec.getSecondPercentColumnWidth();

        if (firstPercentColumnWidth <= 0 || firstPercentColumnWidth <= 0) {
			buf.append("   <col width=\"50%\">").append("\n");
			buf.append("   <col width=\"50%\">").append("\n");
		} else {
			String w1 = Integer.toString(firstPercentColumnWidth);
			String w2 = Integer.toString(secondPercentColumnWidth);
			buf.append("   <col width=\"" + w1 + "%\">").append("\n");
			buf.append("   <col width=\"" + w2 + "%\">").append("\n");
	    }

	    int qualifierColumn = spec.getQualifierColumn();
		int n = 0;
        for (int i = 0; i < nv_vec.size(); i++) {
            String n_v = (String) nv_vec.elementAt(i);
            Vector w = gov.nih.nci.evs.browser.utils.StringUtils.parseData(n_v);
            String name = "";
            String value = "";

            if (w.size() > 0) {
            	name = (String) w.elementAt(0);
			}

			if (w.size() > 1) {
            	value = (String) w.elementAt(1);
			}

            String code = null;
            String namespace = null;

            if (w.size() > 2) {
				code = (String) w.elementAt(2);
			}
			if (w.size() > 3) {
				namespace = (String) w.elementAt(3);
			}
            Vector qualifiers = (Vector) qualifierHashMap.get(n_v);
            qualifiers = SortUtils.quickSort(qualifiers);

			if ((n++) % 2 == 0) {
				  buf.append("	<tr class=\"dataRowDark\">").append("\n");
			} else {
				  buf.append("	<tr class=\"dataRowLight\">").append("\n");
			}

            if (qualifierColumn == 1) {
                if (hasQualifiers(qualifiers)) {
					buf.append("	  <td class=\"dataCellText\" scope=\"row\">").append("\n");
					buf.append("		  <table>").append("\n");
					buf.append("			 <tr>");
					buf.append("<td class=\"dataCellText\" valign=\"top\">").append("\n");
					buf.append("				 " + name).append("\n");
					buf.append("			 </td></tr>").append("\n");

					for (int j = 0; j < qualifiers.size(); j++) {
						String q = (String) qualifiers.elementAt(j);
						Vector u = gov.nih.nci.evs.browser.utils.StringUtils.parseData(q);

						String qualifier_name = "";
						String qualifier_value = "";

						if (u.size() > 0) {
							qualifier_name = (String) u.elementAt(0);
						}
						if (u.size() > 1) {
							qualifier_value = (String) u.elementAt(1);
						}

						String t = qualifier_name + ":" + qualifier_value;
						if (t.length() > 1) {
							buf.append("			 <tr>").append("\n");
							buf.append("			 <td class=\"dataCellText\" >" + indent + t + "</td>").append("\n");
							buf.append("			 </tr>").append("\n");
					    }
					}

					buf.append("		  </table>").append("\n");
					buf.append("	  </td>").append("\n");
					if (code != null) {
						value = getHyperlink(codingScheme, version, value, code, namespace);
					}
					buf.append("	  <td class=\"dataCellText\" scope=\"row\">" + value + "</td>").append("\n");
			    } else {
					buf.append("	  <td class=\"dataCellText\" scope=\"row\">" + name + "</td>").append("\n");
					if (code != null) {
						value = getHyperlink(codingScheme, version, value, code, namespace);
					}
					buf.append("	  <td class=\"dataCellText\" scope=\"row\">" + value + "</td>").append("\n");
				}
			}

            if (qualifierColumn == 2) {
                if (hasQualifiers(qualifiers)) {
					buf.append("	  <td class=\"dataCellText\" scope=\"row\" valign=\"top\">" + name + "</td>").append("\n");
					if (code != null) {
						value = getHyperlink(codingScheme, version, value, code, namespace);
					}

					buf.append("	  <td class=\"dataCellText\" scope=\"row\">").append("\n");
					buf.append("		  <table>").append("\n");
					buf.append("			 <tr>");
					buf.append("<td class=\"dataCellText\">").append("\n");
					buf.append("				 " + value).append("\n");
					buf.append("			 </td></tr>").append("\n");

					for (int j = 0; j < qualifiers.size(); j++) {
						String q = (String) qualifiers.elementAt(j);
						Vector u = gov.nih.nci.evs.browser.utils.StringUtils.parseData(q);

						String qualifier_name = "";
						String qualifier_value = "";

						if (u.size() > 0) {
							qualifier_name = (String) u.elementAt(0);
						}
						if (u.size() > 1) {
							qualifier_value = (String) u.elementAt(1);
						}

						String t = qualifier_name + ":" + qualifier_value;
						if (t.length() > 1) {
							buf.append("			 <tr>").append("\n");
							buf.append("			 <td class=\"dataCellText\" >" + indent + t + "</td>").append("\n");
							buf.append("			 </tr>").append("\n");
					    }
					}

					buf.append("		  </table>").append("\n");
					buf.append("	  </td>").append("\n");

			    } else {
					buf.append("	  <td class=\"dataCellText\" scope=\"row\">" + name + "</td>").append("\n");
					if (code != null) {
						value = getHyperlink(codingScheme, version, value, code, namespace);
					}
					buf.append("	  <td class=\"dataCellText\" scope=\"row\">" + value + "</td>").append("\n");
				}
			}
			buf.append("	</tr>").append("\n");
		}
		buf.append("</table>").append("\n");
        return buf.toString();
	}



/*
    public static void main(String [] args) {
        boolean testLocal = true;
        LexBIGService lbSvc = null;
        if (testLocal) {
			lbSvc = LexBIGServiceImpl.defaultInstance();
		} else {
			lbSvc = RemoteServerUtil.createLexBIGService();
		}

		UIUtils uiUtils = new UIUtils(lbSvc);
		ConceptDetails conceptDetails = new ConceptDetails(lbSvc);
		String codingSchemeURN = "NCI_Thesaurus";
		String codingSchemeVersion = "15.10d";
		String code = "C16612";
		String namespace = "NCI_Thesaurus";
		boolean useNamespace = true;

        if (testLocal) {
			codingSchemeURN = "http://ncicb.nci.nih.gov/xml/owl/EVS/owl2lexevs.owl";
			//= "owl2lexevs.owl";
			codingSchemeVersion = "0.1.2";
			code = "HappyPatientWalkingAround";
			namespace = null;
			useNamespace = false;
	    }

		Entity concept = conceptDetails.getConceptByCode(codingSchemeURN, codingSchemeVersion, code, namespace, useNamespace);
		String property_type = "PRESENTATION";
		String t = uiUtils.generatePropertyTable(concept, property_type);
		System.out.println(t);

        RelationshipUtils relUtils = new RelationshipUtils(lbSvc);
        HashMap relMap = relUtils.getRelationshipHashMap(codingSchemeURN, codingSchemeVersion, code, namespace, useNamespace);
        Iterator it = relMap.keySet().iterator();
        while (it.hasNext()) {
			String key = (String) it.next();
			ArrayList list = (ArrayList) relMap.get(key);
			System.out.println("\n" + key);
			if (list != null) {
				for (int i=0; i<list.size(); i++) {
					t = (String) list.get(i);
					System.out.println("\t" + t);
				}
			}
		}

		String description = "Association";
		String firstColumnHeading = "Name";
		String secondColumnHeading = "Value";
		int firstPercentColumnWidth = 20;
		int secondPercentColumnWidth = 80;
		int qualifierColumn = 2;
		//qualifierColumn = 0;
		ArrayList list = (ArrayList) relMap.get("type_association");

	    HTMLTableSpec spec = uiUtils.relationshipList2HTMLTableSpec(
		    description,
		    firstColumnHeading,
		    secondColumnHeading,
		    firstPercentColumnWidth,
		    secondPercentColumnWidth,
		    qualifierColumn,
		    list);

		//String html_str = uiUtils.generateHTMLTable(spec, "NCI_Thesaurus", "12.05d");
		String html_str = uiUtils.generateHTMLTable(spec, codingSchemeURN, codingSchemeVersion);
		System.out.println(html_str);

	}
*/
}

