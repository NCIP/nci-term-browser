package gov.nih.nci.evs.browser.utils;

import java.util.*;

public class HierarchyUtils {
	private static final String INDENT = "  ";
	
	public static TreeItem add(TreeItem parent, String code, String text) {
		TreeItem child = new TreeItem(code, text);
		parent.addChild("child", child);
		return child;
	}
	
	public static TreeItem init2() {
		TreeItem parent = new TreeItem("root", "Root");
		add(parent, "a1", "Analysis");
		addImplementation(add(parent, "a2", "Implementation"));
		add(parent, "a3", "Design");
		return parent;
	}
	
	public static void addImplementation(TreeItem parent) {
		add(parent, "b1", "PHP");
		addVisualCPP(add(parent, "b2", "Visual C++"));
	}

	public static void addVisualCPP(TreeItem parent) {
		add(parent, "c1", "Memory Leak problems");
	}

	public static TreeItem init() {
		TreeItem parent = new TreeItem("root", "Root");
		
		add(parent, "C12913", "Abnormal Cell");
		addActivity(add(parent, "C43431", "Activity"));
		add(parent, "C12219", "Anatomic Structure, System, or Substance");

		return parent;
	}
	
	public static void addActivity(TreeItem parent) {
		add(parent, "C25404", "Action");
		add(parent, "C49235", "Administrative Activity");
		addBehavior(add(parent, "C16326", "Behavior"));
		add(parent, "C16203", "Clinical or Research Activity");
		add(parent, "C51306", "Educational Activity");
		add(parent, "C17706", "Physical Activity");
		add(parent, "C16847", "Technique");
	}
	
	public static void addBehavior(TreeItem parent) {
		add(parent, "C94296", "Antisocial Behavior");
		add(parent, "C54264", "Avoidance");
		add(parent, "C93233", "Ceremony");
	}

	public static void println(String text) {
		System.out.println(text);
	}
	
	public static void debug(TreeItem top, String indent) {
		println(indent + " * " + top._text + " (" + top._code + ")");

		Map<String, List<TreeItem>> map = top._assocToChildMap;
		Set<String> keys = map.keySet();
		Iterator<String> iterator_key = keys.iterator();
		while (iterator_key.hasNext()) {
			String key = iterator_key.next();
			List<TreeItem> items = map.get(key);
			Iterator<TreeItem> iterator_treeItem = items.iterator();
			while (iterator_treeItem.hasNext()) {
				TreeItem item = iterator_treeItem.next(); 
				debug(item, indent + INDENT);
			}
		}
	}
	
	private static void append(StringBuffer buffer, String text) {
		buffer.append(text + "\n");
	}
	
	private static final int INDENT_PIXELS = 10;
	private static final String BASEPATH = "/ncitbrowser";
	private static final String ICON_LEAF = BASEPATH + "/images/yui/treeview/ln.gif";
	private static final String ICON_EXPAND = BASEPATH + "/images/yui/treeview/lp.gif";
	private static final String ICON_COLLAPSE = BASEPATH + "/images/yui/treeview/lm.gif";

	public static StringBuffer html(StringBuffer buffer, TreeItem top, String indent) {
		Map<String, List<TreeItem>> map = top._assocToChildMap;
		Set<String> keys = map.keySet();
		boolean isLeafNode = keys.isEmpty();

		append(buffer, indent + "<table border=0>");
		append(buffer, indent + "  <tr>");
		append(buffer, indent + "    <td width=\"" + INDENT_PIXELS + "\"></td>");
		
		append(buffer, indent + "    <td>");
		if (isLeafNode)
			append(buffer, indent + "      <img src=\"" + ICON_LEAF +  "\">" + top._text + "<div>");
		else
			append(buffer, indent + "      <a onclick=\"toggle(this)\"><img src=\"" + ICON_COLLAPSE + "\">" + top._text + "</a><div>");
//		append(buffer, indent + "      <div>");

		Iterator<String> iterator_key = keys.iterator();
		while (iterator_key.hasNext()) {
			String key = iterator_key.next();
			List<TreeItem> items = map.get(key);
			Iterator<TreeItem> iterator_treeItem = items.iterator();
			while (iterator_treeItem.hasNext()) {
				TreeItem item = iterator_treeItem.next(); 
				html(buffer, item, indent + "      " + INDENT);
			}
		}
		append(buffer, indent + "      </div>");
		append(buffer, indent + "    </td>");
		append(buffer, indent + "  </tr>");
		append(buffer, indent + "</table>");
		return buffer;
	}

	public static void main(String[] args) {
		TreeItem root = init();
		println(Utils.SEPARATOR);
		debug(root, "");

		println(Utils.SEPARATOR);
		StringBuffer buffer = new StringBuffer();
		html(buffer, root, "");
		println(buffer.toString());
	}
}
