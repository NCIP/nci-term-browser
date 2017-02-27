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
 * The Class ExpressionParser.
 */

public class ExpressionParser {
	public static String EQUIVALENT_CLASS = "equivalentClass";

	private static int SEGMENT_SIZE = 5000;
	private HashMap operand_map = null;
	static HashMap code2NameMap = null;
	static HashMap name2CodeMap = null;
	public static HashMap roleToValueDomainMap = null;
	public static HashMap roleToDomainMap = null;

	HashMap conceptcode2NameMap = null;

	String codingScheme = null;
	String version = null;

	public int MAX_BRACKET = 5;

	public static String INPUT_EXPRESSION = "input_expression";
	public static String OUTPUT_EXPRESSION = "output_expression";
    public static String PARENT = "Parent";

    private CodeSearchUtils csu = null;
    LexBIGService lbSvc = null;

    public ExpressionParser() {

	}

    public ExpressionParser(LexBIGService lbSvc) {
        this.lbSvc = lbSvc;
        this.csu = new CodeSearchUtils(lbSvc);
	}

	private void setconceptcode2NameMap(HashMap hmap) {
		this.conceptcode2NameMap = hmap;
	}

//  to be modified for arbitrary owl formatted ontology
	static {
        code2NameMap = new HashMap();
        name2CodeMap = new HashMap();
        String[] roles = gov.nih.nci.evs.browser.common.Constants.NCIT_ROLES;
        for (int i=0; i<roles.length; i++) {
			String line = (String) roles[i];
			Vector u = StringUtils.parseData(line);
			String code = (String) u.elementAt(0);
			String name = (String) u.elementAt(1);
			code2NameMap.put(code, name);
			name2CodeMap.put(name, code);
		}
		String[] role_data = gov.nih.nci.evs.browser.common.Constants.NCIT_ROLE_DATA;
        roleToValueDomainMap = new HashMap();
        for (int i=0; i<role_data.length; i++) {
			String line = (String) role_data[i];
			Vector u = StringUtils.parseData(line);
			String role_name = (String) u.elementAt(1);
			String role_range = (String) u.elementAt(3);
			roleToValueDomainMap.put(role_name, role_range);
		}

//		"R156|Allele_Absent_From_Wild-type_Chromosomal_Location|Role_Has_Range|Anatomic Structure, System, or Substance|Role_Has_Domain|Gene",

        roleToDomainMap = new HashMap();
        for (int i=0; i<role_data.length; i++) {
			String line = (String) role_data[i];
			Vector u = StringUtils.parseData(line);
			String role_name = (String) u.elementAt(1);
			String role_domain = (String) u.elementAt(5);
			roleToDomainMap.put(role_name, role_domain);
		}
 	}

    public HashMap getOperandMap() {
		return operand_map;
	}

    public static Vector<String> parseData(String line) {
		if (line == null) return null;
        String tab = "|";
        return parseData(line, tab);
    }

    public static Vector<String> parseData(String line, String tab) {
		if (line == null) return null;
        Vector data_vec = new Vector();
        StringTokenizer st = new StringTokenizer(line, tab);
        while (st.hasMoreTokens()) {
            String value = st.nextToken();
            if (value.compareTo("null") == 0)
                value = "";
            data_vec.add(value);
        }
        return data_vec;
    }

	public void dumpVector(Vector v) {
		dumpVector("\n", v);
	}

	public void dumpVector(String label, Vector v) {
		System.out.println("\n" + label);
		for (int i=0; i<v.size(); i++) {
			int j = i+1;
			String t = (String) v.elementAt(i);
			System.out.println("(" + j + ") " + t);
		}
	}

	public String createBracket(int m, boolean open) {
	   StringBuffer bracket_buf = new StringBuffer();
		for (int n=0; n<m; n++) {
			if (open) {
				bracket_buf.append("(");
			} else {
				bracket_buf.append(")");
			}
		}
		return bracket_buf.toString();
	}

	public String restriction2Text(String restriction_id, String restriction) {
		if (restriction == null || restriction.length() == 0) return restriction;
		if (restriction.startsWith("(")) {
			restriction = restriction.substring(1, restriction.length());
		}
		if (restriction.endsWith(")")) {
			restriction = restriction.substring(0, restriction.length()-1);
		}
		Vector u = parseData(restriction, " ");
        String role_name = null;
		if (u.size() == 3) {
			String role_id = (String) u.elementAt(0);
			role_name = (String) code2NameMap.get(role_id);
			String code = (String) u.elementAt(2);
			//to be modified:
			String name = code;
			String valueDomain = (String) roleToValueDomainMap.get(role_name);
			//System.out.println(valueDomain);
			return role_name + "\t" + name;
		}
		return restriction;
	}

	public String getValueDomain(String role_name) {
		String valueDomain = (String) roleToValueDomainMap.get(role_name);
		return valueDomain;
	}


	public String getDomain(String role_name) {
		String domain = (String) roleToDomainMap.get(role_name);
		return domain;
	}

    public String findRoleGroup(String expression) {
		if (expression == null || expression.length() == 0) return null;

		for (int k=0; k<MAX_BRACKET-1; k++) {
			int m = MAX_BRACKET-k;

			String open_bracket = createBracket(m, true);
			String close_bracket = createBracket(m, false);
			Vector u = parseData(expression, " ");

			for (int i=0; i<u.size(); i++) {
				String token = (String) u.elementAt(i);
				if (token.startsWith(open_bracket)) {
					StringBuffer buf = new StringBuffer();
					buf.append(token);
					for (int j=i+1; j<u.size(); j++) {
						token = (String) u.elementAt(j);
						if (!token.endsWith(close_bracket)) {
							buf.append(" ").append(token);
						} else {
							buf.append(" ").append(token);
							break;
						}
					}
					return buf.toString();
				}
			}
		}
		return null;
	}

	public HashMap parseEquivClassExpression(HashMap hmap, String input_expression, String expression, int knt) {
		String role_group = findRoleGroup(expression);
		if (role_group != null) {
			knt++;
			String role_group_code = "RG_" + knt;
			hmap.put(role_group_code, role_group);
			String new_expression = expression.replace(role_group, role_group_code);
            return parseEquivClassExpression(hmap, input_expression, new_expression, knt);
		} else {
			hmap.put(OUTPUT_EXPRESSION, expression);
			return hmap;
		}
	}


	public HashMap parseEquivClassExpression(String expression) {
        HashMap hmap = new HashMap();
        hmap.put(INPUT_EXPRESSION, expression);
        return parseEquivClassExpression(hmap, expression, expression, 0);
	}


	public boolean isRestriction(String expression) {
		if (expression == null) return false;
		if (expression.indexOf(" ") == -1) return false;
		Vector u = parseData(expression, " ");
		if (u.size() != 3) return false;
		String some = (String) u.elementAt(1);
		if (some.compareTo("some") != 0) return false;
		return true;
	}


	public String translateRestriction(String expression) {
		if (expression.startsWith("(")) {
			expression = expression.substring(1, expression.length());
		}
		if (expression.endsWith(")")) {
			expression = expression.substring(0, expression.length()-1);
		}
		if (!isRestriction(expression)) return null;
		Vector u = parseData(expression, " ");
		String role_id = (String) u.elementAt(0);
		String role_target_code = (String) u.elementAt(2);

		String role_target_name = (String) conceptcode2NameMap.get(role_target_code);
		String role_name = (String) code2NameMap.get(role_id);
        return role_name + "|" + role_target_name + "|" + role_target_code;
	}

	public HashMap parseRestriction(HashMap hmap, String expression, int knt) {
		if (hmap == null) {
			hmap = new HashMap();
		}
		if (expression == null || expression.length() == 0) return hmap;
		Vector u = parseData(expression, " ");
		if (u.contains("some")) {
			for (int i=0; i<u.size(); i++) {
				String t = (String) u.elementAt(i);
				if (t.compareTo("some") == 0) {
					String role_name = (String) u.elementAt(i-1);
					String role_target = (String) u.elementAt(i+1);
					String s = role_name + " some " + role_target;

					for (int k3=0; k3<MAX_BRACKET-1; k3++) {
						int m = MAX_BRACKET-k3;
						String open_bracket = createBracket(m, true);
						String close_bracket = createBracket(m, false);
						if (s.startsWith(open_bracket)) {
							s = s.replace(open_bracket, "(");
						}
						if (s.endsWith(close_bracket)) {
							s = s.replace(close_bracket, ")");
						}
					}

					knt++;
					String role_id = "R_" + knt;
					String new_line = expression.replace(s, role_id);
					//System.out.println(new_line);
					hmap.put(role_id, s);
					return parseRestriction(hmap, new_line, knt);
				}
			}
		} else {
			hmap.put(OUTPUT_EXPRESSION, expression);
		}
		return hmap;
	}

    public HashMap getEquivalentClassHashMap(String line) {
        HashMap hmap = parseEquivClassExpression(line);
        String expression = (String) hmap.get(OUTPUT_EXPRESSION);
        hmap = parseRestriction(hmap, expression, 0);
        return hmap;
	}

	public HashMap parseRestriction(String expression) {
		if (expression == null) return null;
		HashMap hmap = new HashMap();
		hmap.put(INPUT_EXPRESSION, expression);
		return parseRestriction(hmap, expression, 0);
	}

	public HashMap translateEquivalentClassHashMap(String scheme, String version,
	                                               String expression, HashMap hmap) {
		String[] codes = searchCodesInExpression(expression);
		HashMap conceptcode2NameMap = csu.codes2Names(scheme, version, codes);
		setconceptcode2NameMap(conceptcode2NameMap);

		HashMap map = new HashMap();
		Vector v = new Vector();
		Vector rg_vec = new Vector();
		Iterator it = hmap.keySet().iterator();
		while (it.hasNext()) {
			String role = (String) it.next();
			String value = (String) hmap.get(role);
			if (role.startsWith("P_")) {
				String role_name = PARENT;

				String role_target_code = value;
				//to be modified
				//String role_target_name = value;//getEntityDescription(codingScheme, version, value);
				String role_target_name = (String) conceptcode2NameMap.get(role_target_code);

				String value_domain = PARENT;
				Vector u = new Vector();
				if (map.get(value_domain) != null) {
					u = (Vector) map.get(value_domain);
				}
				if (!u.contains(role_target_name + "(" + role_target_code + ")")) {
					u.add(role_target_name + " (" + role_target_code + ")");
					u = gov.nih.nci.evs.browser.utils.SortUtils.quickSort(u);
					map.put(value_domain, u);
				}

			} else if (role.startsWith("R_")) {
				String t = translateRestriction(value);
				v.add(t);
				Vector w = parseData(t);
				String role_name = (String) w.elementAt(0);
				String role_target_name = (String) w.elementAt(1);
				String role_target_code = (String) w.elementAt(2);
				if (roleToValueDomainMap == null) {
					System.out.println("(***) roleToValueDomainMap == null???: ");
				}
				String value_domain = (String) roleToValueDomainMap.get(role_name);
				Vector u = new Vector();
				if (map.get(value_domain) != null) {
					u = (Vector) map.get(value_domain);
				}
				if (!u.contains(role_name + "\t" + role_target_name + "(" + role_target_code + ")")) {
					u.add(role_name + "\t" + role_target_name + " (" + role_target_code + ")");
					u = gov.nih.nci.evs.browser.utils.SortUtils.quickSort(u);
					map.put(value_domain, u);
				}
			} else if (role.startsWith("RG_")) {
				if (value.indexOf(" or ") != -1) {
					String str = outputRoleGroup(value);
					Vector dv_vec = findRoleGroupDomainValue(value);
					String dv = (String) dv_vec.elementAt(0);
					Vector u = new Vector();
					if (map.get(dv) != null) {
						u = (Vector) map.get(dv);
					}
					if (!u.contains(str)) {
						u.add(str);
					}
					map.put(dv, u);
				} else {
					Vector w = outputRestrictionCollection(value);
					for (int lcv=0; lcv<w.size(); lcv++) {
						String restriction = (String) w.elementAt(lcv);
						Vector w2 = parseData(restriction);
						String role_name = (String) w2.elementAt(0);
						String role_target_name = (String) w2.elementAt(1);
						String role_target_code = (String) w2.elementAt(2);
						String str = role_name + "\t" + role_target_name + " (" + role_target_code + ")";
						String dv = (String) roleToValueDomainMap.get(role_name);
						Vector u = new Vector();
						if (map.get(dv) != null) {
							u = (Vector) map.get(dv);
						}
						if (!u.contains(str)) {
							u.add(str);
						}
						map.put(dv, u);
					}
				}
			}
		}
		return map;
	}


	public Vector findRoleGroupDomainValue(String role_gp_str) {
		Vector role_gp = parseData(role_gp_str, " ");
		Vector v = new Vector();
		for (int i=0; i<role_gp.size(); i++) {
			String s = (String) role_gp.elementAt(i);
			s = removeBrackets(s);
			v.add(s);
		}
		Vector w = new Vector();
		for (int i=0; i<v.size(); i++) {
			String s = (String) v.elementAt(i);
			if (s.compareTo("some") == 0) {
				String role_id = (String) v.elementAt(i-1);
				String role_target_code = (String) v.elementAt(i+1);
				String value = role_id + " some " + role_target_code;
				String t = translateRestriction(value);
				Vector w1 = parseData(t);
				String role_name = (String) w1.elementAt(0);
				String domain_value = (String) roleToValueDomainMap.get(role_name);
				if (!w.contains(domain_value)) {
					w.add(domain_value);
				}
			}
		}
		return w;
	}

	public String outputRoleGroup(String role_gp_str) {
		Vector role_gp = parseData(role_gp_str, " ");
		Vector v = new Vector();
		for (int i=0; i<role_gp.size(); i++) {
			String s = (String) role_gp.elementAt(i);
			s = removeBrackets(s);
			v.add(s);
		}

		StringBuffer buf = new StringBuffer();
		buf.append("Role group").append("\n");
		for (int i=0; i<v.size(); i++) {
			String s = (String) v.elementAt(i);
			if (s.compareTo("some") == 0) {
				String role_id = (String) v.elementAt(i-1);
				String role_target_code = (String) v.elementAt(i+1);
				String value = role_id + " some " + role_target_code;
				String t = translateRestriction(value);
				Vector w = parseData(t);
				String role_name = (String) w.elementAt(0);
				String role_target_name = (String) w.elementAt(1);
				role_target_code = (String) w.elementAt(2);
				String str = role_name + "\t" + role_target_name + " (" + role_target_code + ")";
				buf.append("\t\t").append(str).append("\n");
			} else if (s.compareTo("or") == 0) {
				buf.append("\tor").append("\n");
			}
		}
		return buf.toString();
	}

	public Vector outputRestrictionCollection(String role_gp_str) {
		Vector restriction_vec = new Vector();
		Vector role_gp = parseData(role_gp_str, " ");
		Vector v = new Vector();
		for (int i=0; i<role_gp.size(); i++) {
			String s = (String) role_gp.elementAt(i);
			s = removeBrackets(s);
			v.add(s);
		}
		for (int i=0; i<v.size(); i++) {
			String s = (String) v.elementAt(i);
			if (s.compareTo("some") == 0) {
				String role_id = (String) v.elementAt(i-1);
				String role_target_code = (String) v.elementAt(i+1);
				String value = role_id + " some " + role_target_code;
				String t = translateRestriction(value);
				restriction_vec.add(t);
			}
		}
		return restriction_vec;
	}


    public String removeBrackets(String line) {
	    if (line == null) return null;
	    StringBuffer buf = new StringBuffer();
	    for (int i=0; i<line.length(); i++) {
			char c = line.charAt(i);
			if (c != '(' && c != ')') {
				buf.append(c);
			}
		}
		return buf.toString();
	}


	public Vector getParentClasses(String line) {
		Vector w = new Vector();
		if (line == null) return w;
		Vector v = new Vector();
		int n = line.indexOf("(");
		if (n != -1) {
			String s = line.substring(0, n);
			v = parseData(s, " ");
			for (int i=0; i<v.size(); i++) {
				String t = (String) v.elementAt(i);
				if (t.compareTo("and") != 0) {
					w.add(t);
				}
			}
		}
		return w;
	}

//////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String infixExpression2Text(String scheme, String version, String expression) {
		long ms = System.currentTimeMillis();
		try {
			Vector parent_vec = getParentClasses(expression);
			HashMap hmap = getEquivalentClassHashMap(expression);
			int lcv = 0;
			for (int k=0; k<parent_vec.size(); k++) {
				String parent = (String) parent_vec.elementAt(k);
				lcv++;
				String parent_id = "P_" + lcv;
				hmap.put(parent_id, parent);
			}
			HashMap map = translateEquivalentClassHashMap(scheme, version, expression, hmap);
			String s = writeEquivalentClass(map);
			System.out.println("Time elapsed run time (ms): " + (System.currentTimeMillis() - ms));
			return s;

		} catch (Exception ex) {
			ex.printStackTrace();

		}
		return null;
	}

	public String writeEquivalentClass(HashMap map) {
		StringBuffer buf = new StringBuffer();
		try {
			Vector key_vec = new Vector();
			Iterator it2 = map.keySet().iterator();
			while (it2.hasNext()) {
				String value_domain = (String) it2.next();
				if (!key_vec.contains(value_domain)) {
					key_vec.add(value_domain);
				}
			}
			key_vec = gov.nih.nci.evs.browser.utils.SortUtils.quickSort(key_vec);
			for (int k=0; k<key_vec.size(); k++) {
				String value_domain = (String) key_vec.elementAt(k);
				if (value_domain.compareTo(PARENT) == 0) {
					Vector role_data_vec = (Vector) map.get(value_domain);
					buf.append(value_domain).append("\n");
					for (int k3=0; k3<role_data_vec.size(); k3++) {
						String role_data = (String) role_data_vec.elementAt(k3);
						buf.append("\t" + role_data).append("\n");
					}
					buf.append("\n");
			    }
			}

			for (int k=0; k<key_vec.size(); k++) {
				String value_domain = (String) key_vec.elementAt(k);
				if (value_domain.compareTo(PARENT) != 0) {
					Vector role_data_vec = (Vector) map.get(value_domain);
					buf.append(value_domain).append("\n");
					for (int k3=0; k3<role_data_vec.size(); k3++) {
						String role_data = (String) role_data_vec.elementAt(k3);
						buf.append("\t" + role_data).append("\n");
					}
					buf.append("\n");
			    }
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return buf.toString();
	}

    public String[] searchCodesInExpression(String expression) {
		Vector v = new Vector();
        try {
			expression = removeBrackets(expression);
			expression = expression.replaceAll("and", "");
			expression = expression.replaceAll("or", "");
			expression = expression.replaceAll("some", "");
			expression = expression.replaceAll("all", "");
			Vector u = gov.nih.nci.evs.browser.utils.StringUtils.parseData(expression, " ");
			for (int i=0; i<u.size(); i++) {
				String t = (String) u.elementAt(i);
				t = t.trim();
				if (!code2NameMap.containsKey(t) && !v.contains(t)) {
					v.add(t);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();

		}
		String[] codes = new String[v.size()];
		for (int i=0; i<v.size(); i++) {
			String t = (String) v.elementAt(i);
			codes[i] = t;
		}
        return codes;
	}

    /*
    public static void main(String [] args) {
		long ms = System.currentTimeMillis();
		String codingSchemeURN = null;
		codingSchemeURN = "NCI_Thesaurus";
		String code = "C5266";
		String version = null;
		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
		ExpressionParser test = new ExpressionParser(lbSvc);
		String expression = "((C3720 and (((R114 some C36437) and (R89 some C36711)) or ((R114 some C36435) and (R89 some C36707)) or ((R114 some C36590) and (R89 some C37216)) or ((R114 some C45440) and (R89 some C45443)) or ((R114 some C45439) and (R89 some C45442)) or ((R114 some C45441) and (R89 some C45444)) or ((R114 some C36436) and (R89 some C36708)) or ((R114 some C27711) and (R89 some C36706)) or ((R114 some C36591) and (R89 some C38348))) and (R105 some C39679) and (R106 some C81946) and (R106 some C37208) and (R104 some C39687) and (R176 some C38184) and (R176 some C101046) and (R176 some C99873) and (R176 some C101059) and (R176 some C99361) and (R176 some C99869) and (R176 some C101075) and (R176 some C101083) and (R176 some C101085) and (R113 some C39680) and (R113 some C37026) and (R113 some C37024) and (R115 some C39695) and (R115 some C36156) and (R115 some C50764)) or (R138 some C12486) or (R89 some C36447) or (R101 some C41165) or (R108 some C39715) or (R105 some C12922) or (R139 some C38640) or (R139 some C12660) or (R104 some C39567) or (R100 some C41165) or (R104 some C33061) or (R108 some C36113) or (R138 some C39300) or (R103 some C13049) or (R101 some C12746) or (R105 some C37017) or (R105 some C12917) or (R105 some C37060) or (R138 some C33930) or (R105 some C36725) or (R89 some C36448) or (R104 some C12535) or (R103 some C41168) or (R105 some C39566) or (R104 some C32725) or (R105 some C36988) or (R104 some C39568) or (R138 some C12549))";
		String t = test.infixExpression2Text(codingSchemeURN, version, expression);
		System.out.println("Concept code: " + code);
		System.out.println(t);
		System.out.println("Total run time (ms): " + (System.currentTimeMillis() - ms));
   }
   */
}

