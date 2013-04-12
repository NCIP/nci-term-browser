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

import java.text.DecimalFormat;
import java.util.*;
import java.util.Map;
import java.util.Map.Entry;


import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

public class HierarchyHelper {
    // -------------------------------------------------------------------------
    private static Logger _logger = Logger.getLogger(HierarchyHelper.class);
    private static DecimalFormat _iFormatter = new java.text.DecimalFormat("0000");
    private static final String INDENT = "  ";
    private final int INDENT_PIXELS = 4;
    private String _basePath = "";
    private String _leafIcon = "";
    private String _expandIcon = "";
    private String _collapseIcon = "";
    private int _idCtr = 0;
    private int _debugCtr = 0;
    private static boolean _debug = false;  // DYEE_DEBUG (default: false)

    // -------------------------------------------------------------------------
    public HierarchyHelper(String basePath) {
        if (basePath == null)
            basePath = "";
        _basePath = basePath;
        _leafIcon = _basePath + "/images/yui/treeview/ln.gif";
        _expandIcon = _basePath + "/images/yui/treeview/lp.gif";
        _collapseIcon = _basePath + "/images/yui/treeview/lm.gif";
    }

    public HierarchyHelper() {
        this(null);
    }

    public String getLeafIcon() {
        return _leafIcon;
    }

    public String getExpandIcon() {
        return _expandIcon;
    }

    public String getCollapseIcon() {
        return _collapseIcon;
    }

    // -------------------------------------------------------------------------
	public TreeItem getSampleTree() {
		TreeItem parent = new TreeItem("root", "Root");

		add(parent, "C12913", "Abnormal Cell");
		addActivity(add(parent, "C43431", "Activity"));
		add(parent, "C12219", "Anatomic Structure, System, or Substance");

		return parent;
	}

	private void addActivity(TreeItem parent) {
		add(parent, "C25404", "Action");
		add(parent, "C49235", "Administrative Activity");
		addBehavior(add(parent, "C16326", "Behavior"));
		add(parent, "C16203", "Clinical or Research Activity");
		add(parent, "C51306", "Educational Activity");
		add(parent, "C17706", "Physical Activity");
		add(parent, "C16847", "Technique");
	}

	private void addBehavior(TreeItem parent) {
		add(parent, "C94296", "Antisocial Behavior");
		add(parent, "C54264", "Avoidance");
		add(parent, "C93233", "Ceremony");
	}

    // -------------------------------------------------------------------------
	public void debug(String text) {
        text = _iFormatter.format(_debugCtr) + ": " + text; ++_debugCtr;
        _logger.debug(text);
        //System.out.println(text);
	}


	public void debug(TreeItem top, String indent) {
        if (top._expandable)
            debug(indent + "+ " + top._text + " (" + top._code + ")");
        else debug(indent + "* " + top._text + " (" + top._code + ")");

		Map<String, List<TreeItem>> map = top._assocToChildMap;
		/*
		Set<String> keys = map.keySet();
		Iterator<String> iterator_key = keys.iterator();
		*/
		//Iterator<String> iterator_key = map.entrySet().iterator();
		Iterator iterator_key = map.entrySet().iterator();
		while (iterator_key.hasNext()) {
			Entry thisEntry = (Entry) iterator_key.next();
			String key = (String) thisEntry.getKey();
			List<TreeItem> items = (ArrayList) thisEntry.getValue();

			Iterator<TreeItem> iterator_treeItem = items.iterator();
			while (iterator_treeItem.hasNext()) {
				TreeItem item = iterator_treeItem.next();
				debug(item, indent + INDENT);
			}
		}
	}

    // -------------------------------------------------------------------------
    private TreeItem add(TreeItem parent, String code, String text) {
        TreeItem child = new TreeItem(code, text);
        parent.addChild("child", child);
        return child;
    }

    private void append(StringBuffer buffer, String text) {
        if (_debug)
            debug(text);
        buffer.append(text + "\n");
    }

    // -------------------------------------------------------------------------
	private StringBuffer getHtml(HttpServletRequest request, String dictionary,
			String version, StringBuffer buffer, TreeItem top, String indent,
			boolean skip) {
	    String code = top._code;
	    String name = top._text;
		Map<String, List<TreeItem>> map = top._assocToChildMap;
		Set<String> keys = map.keySet();

		if (! skip) {
    		boolean isLeafNode = ! top._expandable;

    		append(buffer, indent + "<table border=0>");
    		append(buffer, indent + "  <tr>");
    		append(buffer, indent + "    <td width=\"" + INDENT_PIXELS + "\"></td>");
    		append(buffer, indent + "    <td>");

    		if (isLeafNode) {
    		    if (! name.equals("...")) {
        			append(buffer, indent + "      "
        				+ "<img src=\"" + _leafIcon +  "\">"
        				+ "<a href=\"" + JSPUtils.getConceptUrl(request, dictionary, version, code) +
        				"\">" + name + "</a>"
        				+ "<div>");
    		    } else {
                    append(buffer, indent + "      "
                        + "<img src=\"" + _leafIcon +  "\">"
                        + "<a href=\"" + JSPUtils.getConceptUrl(request, dictionary, version, code) +
                        "\">" + name + "</a>"
                        + "<div>");
    		    }
    		} else {
    		    if (keys.size() > 0) {
        			append(buffer, indent + "      "
        				+ "<div onclick=\"toggle(this)\">"
        				+ "<img src=\"" + _collapseIcon + "\">"
        				+ "<a href=\"" + JSPUtils.getConceptUrl(request, dictionary, version, code) + "\">" + name + "</a></div>"
        				+ "<div>");
    		    } else {
    		        String id = "add_" + _idCtr++;
                    append(buffer, indent + "      <div id=\"" + id + "\" name=\"" + name + "\">");
                    append(buffer, indent + "        <img src=\"" + _expandIcon + "\" onClick=\"addContent('" + id  + "', " + "'" + code + "')\"/> "
                            + "<a href=\"" + JSPUtils.getConceptUrl(request, dictionary, version, code) + "\">" + name + "</a>");
                    append(buffer, indent + "      </div><div>");
    		    }
    		}
		}

		Iterator<String> iterator_key = keys.iterator();
		while (iterator_key.hasNext()) {
			String key = iterator_key.next();
			List<TreeItem> items = map.get(key);
			Iterator<TreeItem> iterator_treeItem = items.iterator();
			while (iterator_treeItem.hasNext()) {
				TreeItem item = iterator_treeItem.next();
				String indent2 = indent;
				if (! skip)
				    indent2 += "      " + INDENT;
				getHtml(request, dictionary, version, buffer, item, indent2, false);
			}
		}

		if (! skip) {
    		append(buffer, indent + "      </div>");
    		append(buffer, indent + "    </td>");
    		append(buffer, indent + "  </tr>");
    		append(buffer, indent + "</table>");
		}
		return buffer;
	}

	public String getHtml(HttpServletRequest request, String dictionary,
		String version, TreeItem top) {
        Utils.StopWatch stopWatch = new Utils.StopWatch();
	    _debugCtr = 0;
	    StringBuffer buffer = new StringBuffer();
	    getHtml(request, dictionary, version, buffer, top, "", true);
	    _logger.debug("getHtml: " + stopWatch.getResult());
	    return buffer.toString();
	}

    // -------------------------------------------------------------------------
	public static void main(String[] args) {
        String basePath = "/ncitbrowser";
        String dictionary = "NCI Thesaurus";
        String version = "11.09d";

        HierarchyHelper helper = new HierarchyHelper(basePath);
		TreeItem root = helper.getSampleTree();
		helper.debug(Utils.SEPARATOR);
		helper.debug(root, "");

//		helper.debug(Utils.SEPARATOR);
//		String tree = helper.getHtml(null, dictionary, version, root);
//		helper.debug(tree);
	}
}
