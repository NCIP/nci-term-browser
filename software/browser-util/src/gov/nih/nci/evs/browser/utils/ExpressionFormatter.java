package gov.nih.nci.evs.browser.utils;
import gov.nih.nci.evs.browser.common.*;
import java.io.*;
import java.text.*;
import java.util.*;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;

/**
 * @author EVS Team
 * @version 1.0
 *
 *      Modification history Initial implementation kim.ong@ngc.com
 *
 */

/**
 * The Class ExpressionFormatter.
 */

public class ExpressionFormatter {
//    static String Constants.INDENT_HALF = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
//    static String indent = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";

    String TYPE_CATEGORY = "1";
    String TYPE_PARENT = "2";
    String TYPE_ROLE = "3";
    String TYPE_ROLE_GROUP = "4";

    //LexBIGService lbSvc = null;
    static HashSet valueDomainSet = null;
    UIUtils uiUtils = null;

    static {
		valueDomainSet = new HashSet();
		String[] lines = Constants.NCIT_ROLE_DATA;
		for (int i=0; i<lines.length; i++) {
			String line = (String) lines[i];
            Vector u = StringUtils.parseData(line);
            String range = (String) u.elementAt(3);
            valueDomainSet.add(range);
		}
		valueDomainSet.add("Parent");
	}

    public ExpressionFormatter() {
        uiUtils = new UIUtils();
	}

//<a href="ncitbrowser/ConceptReport.jsp?dictionary=NCI_Thesaurus&version=17.01e&code=C39679&ns=NCI_Thesaurus">Hallmark Cell</a>

    public String createHyperlink(String line) {
		if (line.indexOf("\t") == -1) return line;
		if (!line.endsWith(")")) return line;
		int n = line.lastIndexOf("(");
		if (n == -1) return line;
		String code = line.substring(n+1, line.length()-1);
		String name = line.substring(0, n-1);
		name = name.trim();
		n = name.indexOf("\t");
		String role = "";
		if (n != -1) {
		    role = name.substring(0, n);
		    role = role.trim();
		}
		name = name.substring(n+1, name.length());
        name = name.trim();
        /*
        StringBuffer buf = new StringBuffer();
        buf.append("<a href=\"ncitbrowser/ConceptReport.jsp?dictionary=NCI_Thesaurus");
        buf.append("&code=").append(code).append("&ns=NCI_Thesaurus\">").append(name).append("</a>");
		String hyperlink = buf.toString();
		*/
        String hyperlink = uiUtils.getHyperlink(name, code);

		if (role.compareTo("") == 0) {
			return Constants.INDENT + hyperlink;
		}
		return Constants.INDENT + role + Constants.INDENT + hyperlink;
	}

    public Vector formatLine(String line) {
		Vector w = new Vector();
		if (line.indexOf("Role group") != -1) {
			line = line.trim();
			w.add(TYPE_ROLE_GROUP);
			w.add(line);
			return w;
		} else if (line.compareTo("\tor") == 0) {
			line = line.trim();
			w.add(TYPE_ROLE_GROUP);
			w.add(line);
			return w;
		}
		if (line.indexOf("\t") == -1) {
			w.add(TYPE_CATEGORY);
			w.add(line);
			return w;
		}
		if (!line.endsWith(")")) {
			w.add(TYPE_CATEGORY);
			w.add(line);
			return w;
		}
		int n = line.lastIndexOf("(");
		if (n == -1) {
			w.add(TYPE_CATEGORY);
			w.add(line);
			return w;
		}
		String code = line.substring(n+1, line.length()-1);
		String name = line.substring(0, n-1);
		name = name.trim();
		n = name.indexOf("\t");
		String role = "";
		if (n != -1) {
		    role = name.substring(0, n);
		    role = role.trim();
		}
		name = name.substring(n+1, name.length());
        name = name.trim();
        /*
        StringBuffer buf = new StringBuffer();
        buf.append("<a href=\"ncitbrowser/ConceptReport.jsp?dictionary=NCI_Thesaurus");
        buf.append("&code=").append(code).append("&ns=NCI_Thesaurus\">").append(name).append("</a>");
		String hyperlink = buf.toString();
		*/
		String hyperlink = uiUtils.getHyperlink(name, code);

		if (role.compareTo("") == 0) {
			w.add(TYPE_PARENT);
			w.add(hyperlink);
			return w;
		}
		w.add(TYPE_ROLE);
		w.add(role);
		w.add(hyperlink);
		return w;
	}


   	public String getRelationshipTableLabel(String type, boolean isEmpty) {
		String NONE = "<i>(none)</i>";
		StringBuffer buf = new StringBuffer();
		if (type.compareTo(Constants.TYPE_ROLE) == 0) {
			buf.append("<b>Role Relationships</b>,&nbsp;asserted or inherited, pointing from the current concept to other concepts:");
			if (isEmpty) {
				buf.append(" ").append(NONE).append("\n");
			} else {
				buf.append("<br/>").append("\n");
				buf.append("<i>(True for the current concept.)</i>").append("\n");
			}
/*
		} if (type.compareTo(Constants.TYPE_LOGICAL_DEFINITION) == 0) {
			buf.append("<b>Logical Definition</b>,&nbsp;showing the parent concepts and direct role assertions that define this concept:");
			if (isEmpty) {
				buf.append(" ").append(NONE).append("\n");
			} else {
				//buf.append("<br/>").append("\n");
			}
*/
		} else if (type.compareTo(Constants.TYPE_LOGICAL_DEFINITION) == 0) {
			buf.append("<b>Logical Definition</b>,&nbsp;showing the parent concepts and direct role assertions that define this concept:");
			if (isEmpty) {
				buf.append(" ").append(NONE).append("\n");
			} else {
				buf.append("<br/>").append("\n");
				buf.append("<i>(True for the current concept.)</i>").append("\n");
			}
		} else if (type.compareTo(Constants.TYPE_INVERSE_ROLE) == 0) {
			buf.append("<b>Incoming Role Relationships</b>,&nbsp;asserted or inherited, pointing from other concepts to the current concept:");
			if (isEmpty) {
				buf.append(" ").append(NONE).append("\n");
			}
		}
		return buf.toString();
	}

    private String createTable(Vector columnHeadings, Vector columnWidths) {
		StringBuffer buf = new StringBuffer();
		buf.append("<table class=\"datatable_960\" border=\"0\" width=\"100%\">");
		buf.append("<tr>");
		for (int i=0; i<columnHeadings.size(); i++) {
			String s = (String) columnHeadings.elementAt(i);
			buf.append("   <th class=\"dataCellText\" scope=\"col\" align=\"left\">" + s + "</th>");
		}
		buf.append("</tr>");
		for (int i=0; i<columnWidths.size(); i++) {
			Integer int_obj = (Integer) columnWidths.elementAt(i);
			buf.append("   <col width=\"" + int_obj.intValue() + "%\">");
		}
		return buf.toString();
	}

	public String reformat(String expression) {
        StringBuffer buf = new StringBuffer();
		String label = getRelationshipTableLabel(Constants.TYPE_LOGICAL_DEFINITION, false);
        buf.append(label);
        Vector columnHeadings = new Vector();
        columnHeadings.add("Relationship");
		columnHeadings.add("Value (qualifiers indented underneath)");
        Vector columnWidths = new Vector();
        /*
        columnWidths.add(new Integer(30));
        columnWidths.add(new Integer(70));
        */
        columnWidths.add(new Integer(40));
        columnWidths.add(new Integer(60));
        String table = createTable(columnHeadings, columnWidths);
        buf.append(table);
		Vector v = gov.nih.nci.evs.browser.utils.StringUtils.parseData(expression, "\n");
		boolean role_group_start = false;
		for (int i=0; i<v.size(); i++) {
			String line = (String) v.elementAt(i);
				String category = line;
				category = category.trim();

				if (valueDomainSet.contains(category) || category.compareTo("Parent") == 0) {
					buf.append("<tr class=\"dataRowDark\">");
					buf.append("<td class=\"dataCellText\">\n");
					buf.append(Constants.INDENT_HALF + category);
					buf.append("</td><td>" + Constants.INDENT + "</td></tr>");

				} else {
					buf.append("<tr class=\"dataRowLight\">");
				}

				Vector w = formatLine(line);
				String type = (String) w.elementAt(0);
				if (type.compareTo(TYPE_ROLE_GROUP) == 0) {
					String s = (String) w.elementAt(1);
					//if (start) {

						buf.append("<td class=\"dataCellText\" scope=\"row\" valign=\"top\">");
						buf.append(Constants.INDENT).append(s);
						buf.append("</td><td>" + Constants.INDENT + "</td></tr>");

					role_group_start = true;

				} else if (type.compareTo(TYPE_CATEGORY) == 0) {
					String s = (String) w.elementAt(1);
					if (s.compareTo("or") != 0) {
						role_group_start = false;
					}
				} else if (type.compareTo(TYPE_PARENT) == 0) {
					String s = (String) w.elementAt(1);

						buf.append("<td class=\"dataCellText\" scope=\"row\" valign=\"top\">");
						buf.append(Constants.INDENT).append(s);
						buf.append("</td><td>" + Constants.INDENT + "</td></tr>");

				} else if (type.compareTo(TYPE_ROLE) == 0) {
					String s = (String) w.elementAt(1);
					String t = (String) w.elementAt(2);

					if (role_group_start) {
						buf.append(Constants.INDENT);
					}

					buf.append("<td class=\"dataCellText\" scope=\"row\" valign=\"top\">");
					buf.append(Constants.INDENT).append(s);
					buf.append("</td><td>" + t + "</td></tr>");
				}
				buf.append("\n");
			//}
		}
		buf.append("</table>\n");
		return buf.toString();
	}


	/*
	public String reformat(String expression) {
		boolean start = true;
		if (skipParents) {
			start = false;
		}
        StringBuffer buf = new StringBuffer();
		String label = getRelationshipTableLabel(Constants.TYPE_LOGICAL_DEFINITION, false);
        buf.append(label);
        //buf.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\">\n");
        Vector columnHeadings = new Vector();
        columnHeadings.add("Relationship");
		columnHeadings.add("Value");
        Vector columnWidths = new Vector();
        columnWidths.add(new Integer(30));
        columnWidths.add(new Integer(70));

        String table = createTable(columnHeadings, columnWidths);
        buf.append(table);

		Vector v = gov.nih.nci.evs.browser.utils.StringUtils.parseData(expression, "\n");
		for (int i=0; i<v.size(); i++) {
			String t = (String) v.elementAt(i);
			System.out.println("(" + i + ")" + t);
		}
		boolean role_group_start = false;
		for (int i=0; i<v.size(); i++) {
			String line = (String) v.elementAt(i);
			if (skipParents && !start) {
				if (!line.startsWith("\t")) {
					if (line.compareTo("Parent") != 0) {
						start = true;
					}
				}
		    }

		    if (start) {
				String category = line;
				category = category.trim();
				if (line.startsWith("\t")) {
					category = category.substring(1, category.length());
				}
				if (valueDomainSet.contains(category)) {
					buf.append("<tr class=\"dataRowDark\">");
					buf.append("<td class=\"dataCellText\">\n");
					buf.append(Constants.INDENT_HALF + category);
					buf.append("</td><td>" + indent + "</td></tr>");

				} else {
					buf.append("<tr class=\"dataRowLight\">");
				}

				Vector w = formatLine(line);
				String type = (String) w.elementAt(0);
				if (type.compareTo(TYPE_ROLE_GROUP) == 0) {
					String s = (String) w.elementAt(1);
					if (start) {

						buf.append("<td class=\"dataCellText\" scope=\"row\" valign=\"top\">");
						buf.append(indent).append(s);
						buf.append("</td><td>" + indent + "</td></tr>");

					}
					role_group_start = true;

				} else if (type.compareTo(TYPE_CATEGORY) == 0) {
					String s = (String) w.elementAt(1);
					if (s.compareTo("or") != 0) {
						role_group_start = false;
					}
				} else if (type.compareTo(TYPE_PARENT) == 0) {
					String s = (String) w.elementAt(1);

						buf.append("<td class=\"dataCellText\" scope=\"row\" valign=\"top\">");
						buf.append(indent).append(s);
						buf.append("</td><td>" + indent + "</td></tr>");

				} else if (type.compareTo(TYPE_ROLE) == 0) {
					String s = (String) w.elementAt(1);
					String t = (String) w.elementAt(2);

					if (role_group_start) {
						buf.append(indent);
					}

					buf.append("<td class=\"dataCellText\" scope=\"row\" valign=\"top\">");
					buf.append(indent).append(s);
					buf.append("</td><td>" + t + "</td></tr>");
				}
				buf.append("\n");
			}
		}
		buf.append("</table>\n");
		return buf.toString();
	}
	*/
/*
    public static void main(String [] args) {
		long ms = System.currentTimeMillis();
		String scheme = null;
		scheme = "NCI_Thesaurus";
		String code = "C5266";
		code = "C37193";

		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
		String version = new CodingSchemeDataUtils(lbSvc).getVocabularyVersionByTag(scheme, "PRODUCTION");;

		ExpressionFormatter test = new ExpressionFormatter(lbSvc);
		//String expression = "((C3720 and (((R114 some C36437) and (R89 some C36711)) or ((R114 some C36435) and (R89 some C36707)) or ((R114 some C36590) and (R89 some C37216)) or ((R114 some C45440) and (R89 some C45443)) or ((R114 some C45439) and (R89 some C45442)) or ((R114 some C45441) and (R89 some C45444)) or ((R114 some C36436) and (R89 some C36708)) or ((R114 some C27711) and (R89 some C36706)) or ((R114 some C36591) and (R89 some C38348))) and (R105 some C39679) and (R106 some C81946) and (R106 some C37208) and (R104 some C39687) and (R176 some C38184) and (R176 some C101046) and (R176 some C99873) and (R176 some C101059) and (R176 some C99361) and (R176 some C99869) and (R176 some C101075) and (R176 some C101083) and (R176 some C101085) and (R113 some C39680) and (R113 some C37026) and (R113 some C37024) and (R115 some C39695) and (R115 some C36156) and (R115 some C50764)) or (R138 some C12486) or (R89 some C36447) or (R101 some C41165) or (R108 some C39715) or (R105 some C12922) or (R139 some C38640) or (R139 some C12660) or (R104 some C39567) or (R100 some C41165) or (R104 some C33061) or (R108 some C36113) or (R138 some C39300) or (R103 some C13049) or (R101 some C12746) or (R105 some C37017) or (R105 some C12917) or (R105 some C37060) or (R138 some C33930) or (R105 some C36725) or (R89 some C36448) or (R104 some C12535) or (R103 some C41168) or (R105 some C39566) or (R104 some C32725) or (R105 some C36988) or (R104 some C39568) or (R138 some C12549))";

		String equivalanceClass = null;
		try {
			equivalanceClass = new CodingSchemeDataUtils(lbSvc).getEquivalenceExpression(scheme, version, code);
			String expression = test.infixExpression2Text(scheme, version, equivalanceClass);
            //ExpressionFormatter test2 = new ExpressionFormatter(lbSvc);
			System.out.println(test.reformat(expression, true));
			System.out.println("\n\n(*) Total run time (ms): " + (System.currentTimeMillis() - ms));


		} catch (Exception ex) {
			ex.printStackTrace();
		}
   }
   */

}

// Issues:
// OWL formatted ontologies RoleID???
//<tr class="dataRowLight">

//<tr class="dataRowDark">

