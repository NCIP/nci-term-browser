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
 * The Class RelationshipTabFormatter.
 */

public class RelationshipTabFormatter {
    String TYPE_CATEGORY = "1";
    String TYPE_PARENT = "2";
    String TYPE_ROLE = "3";
    String TYPE_ROLE_GROUP = "4";

    static String indent_half = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
    static String indent = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";

    LexBIGService lbSvc = null;
    RelationshipUtils relUtils = null;

    public RelationshipTabFormatter() {

	}

    public RelationshipTabFormatter(LexBIGService lbSvc) {
		this.lbSvc = lbSvc;
		this.relUtils = new RelationshipUtils(lbSvc);
	}

    public String createHyperlink(String codingScheme, String namespace, String code, String name) {
        StringBuffer buf = new StringBuffer();
        buf.append("<a href=\"ncitbrowser/ConceptReport.jsp?dictionary=" + codingScheme);
        buf.append("&code=").append(code).append("&ns=" + namespace + "\">").append(name).append("</a>");
        return buf.toString();
	}

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
        StringBuffer buf = new StringBuffer();
        buf.append("<a href=\"ncitbrowser/ConceptReport.jsp?dictionary=NCI_Thesaurus");
        buf.append("&code=").append(code).append("&ns=NCI_Thesaurus\">").append(name).append("</a>");
		String hyperlink = buf.toString();
		if (role.compareTo("") == 0) {
			return indent + hyperlink;
		}
		return indent + role + indent + hyperlink;
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
        StringBuffer buf = new StringBuffer();
        buf.append("<a href=\"ncitbrowser/ConceptReport.jsp?dictionary=NCI_Thesaurus");
        buf.append("&code=").append(code).append("&ns=NCI_Thesaurus\">").append(name).append("</a>");
		String hyperlink = buf.toString();
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


	public String run(String expression) {
        StringBuffer buf = new StringBuffer();
        buf.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\">\n");
		Vector w = gov.nih.nci.evs.browser.utils.StringUtils.parseData(expression, "\n");
		for (int i=0; i<w.size(); i++) {
			String line = (String) w.elementAt(i);
			buf.append("<tr><td class=\"dataCellText\">\n");
			String s = createHyperlink(line);
			if (s.indexOf("\t") == -1) {
				buf.append(s);
			} else {
				s = s.trim();
				if (!s.startsWith(indent)) {
					buf.append(indent);
				}
				buf.append(s);
			}
			buf.append("\n");
			buf.append("</td></tr>\n");
		}
		buf.append("<table>\n");
		return buf.toString();
	}

	public String reformat(String expression) {
		return reformat(expression, false);
	}


	public String reformat(String expression, boolean skipParents) {
		boolean start = true;
		if (skipParents) {
			start = false;
		}
        StringBuffer buf = new StringBuffer();
        buf.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\">\n");
		Vector v = gov.nih.nci.evs.browser.utils.StringUtils.parseData(expression, "\n");
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
				buf.append("<tr>");
				buf.append("<td class=\"dataCellText\">\n");

				Vector w = formatLine(line);
				String type = (String) w.elementAt(0);

				if (type.compareTo(TYPE_ROLE_GROUP) == 0) {
					String s = (String) w.elementAt(1);
					if (start) buf.append(indent).append(s);
					role_group_start = true;

				} else if (type.compareTo(TYPE_CATEGORY) == 0) {
					String s = (String) w.elementAt(1);
					buf.append(s);
					if (s.compareTo("or") != 0) {
						role_group_start = false;
					}
				} else if (type.compareTo(TYPE_PARENT) == 0) {
					String s = (String) w.elementAt(1);
					buf.append(indent).append(s);
				} else if (type.compareTo(TYPE_ROLE) == 0) {
					String s = (String) w.elementAt(1);
					String t = (String) w.elementAt(2);
					if (role_group_start) {
						buf.append(indent);
					}
					buf.append(indent).append(s).append(indent).append(t);
				}
				buf.append("\n");
				buf.append("</td></tr>\n");
			}
		}
		buf.append("<table>\n");
		return buf.toString();
	}

    public HashMap getInboundRoleTable(String scheme_curr, String version_curr, String code_curr, String ns_curr) {
		boolean superconcept = false;
		boolean subconcept = false;
		boolean role = false;
		boolean inverse_role = true;
		boolean association = false;
		boolean inverse_association = false;

        List options = relUtils.createOptionList(
			                     superconcept,
                                 subconcept,
                                 role,
                                 inverse_role,
                                 association,
                                 inverse_association);

        HashMap hmap = relUtils.getRelationshipHashMap(scheme_curr, version_curr, code_curr, ns_curr, true, options);
        ArrayList roles = (ArrayList) hmap.get(Constants.TYPE_ROLE);
        return getOutboundRoleTable(roles);
	}


    public HashMap getOutboundRoleTable(String scheme_curr, String version_curr, String code_curr, String ns_curr) {
		boolean superconcept = false;
		boolean subconcept = false;
		boolean role = true;
		boolean inverse_role = false;
		boolean association = false;
		boolean inverse_association = false;

        List options = relUtils.createOptionList(
			                     superconcept,
                                 subconcept,
                                 role,
                                 inverse_role,
                                 association,
                                 inverse_association);

        HashMap hmap = relUtils.getRelationshipHashMap(scheme_curr, version_curr, code_curr, ns_curr, true, options);
        ArrayList roles = (ArrayList) hmap.get(Constants.TYPE_ROLE);
        return getOutboundRoleTable(roles);
	}

    public HashMap getOutboundRoleTable(ArrayList roles) {
		boolean restrictToConcept = true;
		ExpressionParser parser = new ExpressionParser();
        Vector v = new Vector();
        HashMap valueDomain2RoleDataMap = new HashMap();
        for (int i=0; i<roles.size(); i++) {
			String r = (String) roles.get(i);
			Vector u = gov.nih.nci.evs.browser.utils.StringUtils.parseData(r);
			String roleName = (String) u.elementAt(0);
			String valueDomain = parser.getValueDomain(roleName);
			Vector w = new Vector();
			if (valueDomain2RoleDataMap.containsKey(valueDomain)) {
				w = (Vector) valueDomain2RoleDataMap.get(valueDomain);
			}
			if (!w.contains(r)) {
				w.add(r);
			}
			valueDomain2RoleDataMap.put(valueDomain, w);
		}
        return valueDomain2RoleDataMap;
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

   	public String getRelationshipTableLabel(String type, boolean isEmpty) {
		String NONE = "<i>(none)</i>";
		StringBuffer buf = new StringBuffer();
		if (type.compareTo(Constants.TYPE_ROLE) == 0) {
			buf.append("<b>Role Relationships</b>&nbsp;asserted or inherited, pointing from the current concept to other concepts:");
			if (isEmpty) {
				buf.append(" ").append(NONE).append("\n");
			} else {
				buf.append("<br/>").append("\n");
				buf.append("<i>(True for the current concept.)</i>").append("\n");
			}
		} else if (type.compareTo(Constants.TYPE_LOGICAL_DEFINITION) == 0) {
			buf.append("<b>Logical Definition</b>,&nbsp;showing the parent concepts and direct role assertions that define this concept:");
			if (isEmpty) {
				buf.append(" ").append(NONE).append("\n");
			} else {
				buf.append("<br/>").append("\n");
			}
		} else if (type.compareTo(Constants.TYPE_SUPERCONCEPT) == 0) {
			buf.append("<b>Parent Concepts</b>:");
			if (isEmpty) {
				buf.append(" ").append(NONE).append("\n");
			} else {
				buf.append("<br/>").append("\n");
			}
		} else if (type.compareTo(Constants.TYPE_SUBCONCEPT) == 0) {
			buf.append("<b>Child Concepts</b>:");
			if (isEmpty) {
				buf.append(" ").append(NONE).append("\n");
			} else {
				buf.append("<br/>").append("\n");
			}
		} else if (type.compareTo(Constants.TYPE_INVERSE_ROLE) == 0) {
			buf.append("<b>Incoming Role Relationships</b>&nbsp;pointing from other concepts to the current concept:");
			if (isEmpty) {
				buf.append(" ").append(NONE).append("\n");
			}
		}
		return buf.toString();
	}

    public String formatOutboundRoleTable(ArrayList roles) {
		HashMap hmap = getOutboundRoleTable(roles);
		return formatOutboundRoleTable(hmap);
	}

    public String formatOutboundRoleTable(String scheme_curr, String version_curr, String code_curr, String ns_curr) {
		HashMap hmap = getOutboundRoleTable(scheme_curr, version_curr, code_curr, ns_curr);
		return formatOutboundRoleTable(hmap);
	}

    public String formatSingleColumnTable(String scheme, String type, ArrayList list) {
		StringBuffer buf = new StringBuffer();
		boolean isEmpty = false;
		if (list == null || list.size() == 0) {
			isEmpty = true;
		}
		String label = getRelationshipTableLabel(type, isEmpty);
        buf.append(label);
        if (isEmpty) {
			return buf.toString();
		}
		buf.append("<table class=\"datatable_960\" border=\"0\" width=\"100%\">");
		for (int i=0; i<list.size(); i++) {
			String line = (String) list.get(i);
			Vector u = gov.nih.nci.evs.browser.utils.StringUtils.parseData(line);
			String name = (String) u.elementAt(0);
			String code = (String) u.elementAt(1);
			String namespace = (String) u.elementAt(2);
			Boolean bool_obj = UIUtils.isEven(new Integer(i));
			if (bool_obj.equals(Boolean.TRUE)) {
				buf.append("<tr class=\"dataRowDark\">");
			} else {
				buf.append("<tr class=\"dataRowLight\">");
			}
			buf.append("<td class=\"dataCellText\">");
			String hyperlink = createHyperlink(scheme, namespace, code, name);
			buf.append(indent_half + hyperlink);
			buf.append("</td>");
			buf.append("</tr>");
		}
		buf.append("</table>");
        return buf.toString();
	}


    public String formatOutboundRoleTable(HashMap hmap) {
		StringBuffer buf = new StringBuffer();
        Vector key_vec = new Vector();
		Iterator it = hmap.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			key_vec.add(key);
		}
		boolean isEmpty = false;
		if (key_vec.size() == 0) {
			isEmpty = true;
		}
		String label = getRelationshipTableLabel(Constants.TYPE_ROLE, isEmpty);
        buf.append(label);
        if (isEmpty) {
			return buf.toString();
		}

        Vector columnHeadings = new Vector();
        columnHeadings.add("Relationship");
		columnHeadings.add("Value (qualifiers indented underneath)");
        Vector columnWidths = new Vector();
        columnWidths.add(new Integer(30));
        columnWidths.add(new Integer(70));

        String table = createTable(columnHeadings, columnWidths);
        buf.append(table);

		key_vec = gov.nih.nci.evs.browser.utils.SortUtils.quickSort(key_vec);
		for (int i=0; i<key_vec.size(); i++) {
			String key = (String) key_vec.elementAt(i);
			buf.append("<tr class=\"dataRowDark\">");
			buf.append("<td class=\"dataCellText\">");
			buf.append(indent_half + key);
			buf.append("</td><td>" + indent + "</td></tr>");
			Vector w = (Vector) hmap.get(key);
			w = gov.nih.nci.evs.browser.utils.SortUtils.quickSort(w);
			for (int k=0; k<w.size(); k++) {
				String s = (String) w.elementAt(k);
				Vector u = gov.nih.nci.evs.browser.utils.StringUtils.parseData(s);
				String roleName = (String) u.elementAt(0);
				String roleTargetName = (String) u.elementAt(1);
				String roleTargetCode = (String) u.elementAt(2);
				String codingScheme = (String) u.elementAt(3);
				String namespace = (String) u.elementAt(4);

				String hyperlink = createHyperlink(codingScheme, namespace, roleTargetCode, roleTargetName);

				buf.append("<tr class=\"dataRowLight\">");
				buf.append("<td class=\"dataCellText\" scope=\"row\" valign=\"top\">");
				buf.append(indent).append(roleName);
				buf.append("</td>");
				buf.append("<td class=\"dataCellText\" scope=\"row\" valign=\"top\">");
				buf.append(hyperlink);
				buf.append("</td>");
				buf.append("</tr>");
			}
		}
		buf.append("</table>");
		return buf.toString();
    }

    public String formatInboundRoleTable(String scheme_curr, String version_curr, String code_curr, String ns_curr) {
		HashMap hmap = getInboundRoleTable(scheme_curr, version_curr, code_curr, ns_curr);
		return formatInboundRoleTable(hmap);
	}

    public String formatInboundRoleTable(ArrayList roles) {
		HashMap hmap = getInboundRoleTable(roles);
		return formatInboundRoleTable(hmap);
	}

    public HashMap getInboundRoleTable(ArrayList roles) {
		boolean restrictToConcept = true;
		ExpressionParser parser = new ExpressionParser();
        Vector v = new Vector();
        HashMap domain2RoleDataMap = new HashMap();
        for (int i=0; i<roles.size(); i++) {
			String r = (String) roles.get(i);
			Vector u = gov.nih.nci.evs.browser.utils.StringUtils.parseData(r);
			String roleName = (String) u.elementAt(0);
			String domain = parser.getDomain(roleName);
			Vector w = new Vector();
			if (domain2RoleDataMap.containsKey(domain)) {
				w = (Vector) domain2RoleDataMap.get(domain);
			}
			if (!w.contains(r)) {
				w.add(r);
			}
			domain2RoleDataMap.put(domain, w);
		}
        return domain2RoleDataMap;
	}

    public String formatInboundRoleTable(HashMap hmap) {
		StringBuffer buf = new StringBuffer();
        Vector key_vec = new Vector();
		Iterator it = hmap.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			key_vec.add(key);
		}
		boolean isEmpty = false;
		if (key_vec.size() == 0) {
			isEmpty = true;
		}
		String label = getRelationshipTableLabel(Constants.TYPE_INVERSE_ROLE, isEmpty);
        buf.append(label);
        if (isEmpty) {
			return buf.toString();
		}

        Vector columnHeadings = new Vector();
        columnHeadings.add("Value (qualifiers indented underneath)");
        columnHeadings.add("Relationship");

        Vector columnWidths = new Vector();
        columnWidths.add(new Integer(70));
        columnWidths.add(new Integer(30));

        String table = createTable(columnHeadings, columnWidths);
        buf.append(table);

		key_vec = gov.nih.nci.evs.browser.utils.SortUtils.quickSort(key_vec);
		for (int i=0; i<key_vec.size(); i++) {
			String key = (String) key_vec.elementAt(i);
			buf.append("<tr class=\"dataRowDark\">");
			buf.append("<td class=\"dataCellText\">");
			buf.append(indent_half + key);
			buf.append("</td><td>" + indent + "</td></tr>");
			Vector w = (Vector) hmap.get(key);
			w = gov.nih.nci.evs.browser.utils.SortUtils.quickSort(w);
			for (int k=0; k<w.size(); k++) {
				String s = (String) w.elementAt(k);
				Vector u = gov.nih.nci.evs.browser.utils.StringUtils.parseData(s);
				String roleName = (String) u.elementAt(0);
				String roleTargetName = (String) u.elementAt(1);
				String roleTargetCode = (String) u.elementAt(2);
				String codingScheme = (String) u.elementAt(3);
				String namespace = (String) u.elementAt(4);
				String hyperlink = createHyperlink(codingScheme, namespace, roleTargetCode, roleTargetName);

				buf.append("<tr class=\"dataRowLight\">");
				buf.append("<td class=\"dataCellText\" scope=\"row\" valign=\"top\">");
				buf.append(indent).append(hyperlink);
                buf.append("</td>");
				buf.append("<td class=\"dataCellText\" scope=\"row\" valign=\"top\">");
				buf.append(roleName);
				buf.append("</td>");
				buf.append("</tr>");
			}
		}
		buf.append("</table>");
		return buf.toString();
	}

/*
   public static void main(String[] args) {
		long ms = System.currentTimeMillis();
		String scheme = null;
		scheme = "NCI_Thesaurus";
		//String code = "C5266";

		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
		String version = new CodingSchemeDataUtils(lbSvc).getVocabularyVersionByTag(scheme, "PRODUCTION");

		String ns_curr = scheme;
		RelationshipTabFormatter formatter = new RelationshipTabFormatter(lbSvc);
		String code = "C37193";
		String formattedTable = formatter.formatOutboundRoleTable(scheme, version, code, ns_curr);

		PrintWriter pw = null;
		String outputfile = "test_" + code + ".txt";
		try {
			pw = new PrintWriter(outputfile, "UTF-8");
			pw.println(formattedTable);
		} catch (Exception ex) {

		} finally {
			try {
				pw.close();
				System.out.println("Output file " + outputfile + " generated.");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		System.out.println("Total run time (ms): " + (System.currentTimeMillis() - ms));
   }
   */
}

