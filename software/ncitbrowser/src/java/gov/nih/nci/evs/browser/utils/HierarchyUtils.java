package gov.nih.nci.evs.browser.utils;

/**
 * <!-- LICENSE_TEXT_START -->
 * Copyright 2008,2009 NGIT. This software was developed in conjunction
 * with the National Cancer Institute, and so to the extent government
 * employees are co-authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the disclaimer of Article 3,
 *      below. Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions and the following
 *      disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *   2. The end-user documentation included with the redistribution,
 *      if any, must include the following acknowledgment:
 *      "This product includes software developed by NGIT and the National
 *      Cancer Institute."   If no such end-user documentation is to be
 *      included, this acknowledgment shall appear in the software itself,
 *      wherever such third-party acknowledgments normally appear.
 *   3. The names "The National Cancer Institute", "NCI" and "NGIT" must
 *      not be used to endorse or promote products derived from this software.
 *   4. This license does not authorize the incorporation of this software
 *      into any third party proprietary programs. This license does not
 *      authorize the recipient to use any trademarks owned by either NCI
 *      or NGIT
 *   5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED
 *      WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *      OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE
 *      DISCLAIMED. IN NO EVENT SHALL THE NATIONAL CANCER INSTITUTE,
 *      NGIT, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT,
 *      INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 *      BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *      LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *      CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *      LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 *      ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *      POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */

import java.util.*;

import javax.servlet.http.HttpServletRequest;

public class HierarchyUtils {
	private static final String INDENT = "  ";
	
	private static TreeItem add(TreeItem parent, String code, String text) {
		TreeItem child = new TreeItem(code, text);
		parent.addChild("child", child);
		return child;
	}
	
	public static TreeItem getSampleTree() {
		TreeItem parent = new TreeItem("root", "Root");
		
		add(parent, "C12913", "Abnormal Cell");
		addActivity(add(parent, "C43431", "Activity"));
		add(parent, "C12219", "Anatomic Structure, System, or Substance");

		return parent;
	}
	
	private static void addActivity(TreeItem parent) {
		add(parent, "C25404", "Action");
		add(parent, "C49235", "Administrative Activity");
		addBehavior(add(parent, "C16326", "Behavior"));
		add(parent, "C16203", "Clinical or Research Activity");
		add(parent, "C51306", "Educational Activity");
		add(parent, "C17706", "Physical Activity");
		add(parent, "C16847", "Technique");
	}
	
	private static void addBehavior(TreeItem parent) {
		add(parent, "C94296", "Antisocial Behavior");
		add(parent, "C54264", "Avoidance");
		add(parent, "C93233", "Ceremony");
	}

	private static void println(String text) {
		System.out.println(text);
	}
	
	public static void debug(TreeItem top, String indent) {
        if (top._expandable)
            println(indent + "+ " + top._text + " (" + top._code + ")");
        else println(indent + "* " + top._text + " (" + top._code + ")");

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
	
	private static final int INDENT_PIXELS = 4;
	private static String BASEPATH = null;
	private static String ICON_LEAF = null;
	private static String ICON_EXPAND = null;
	private static String ICON_COLLAPSE = null;
	
	public static String getLeafIcon(String basePath) {
		return basePath + "/images/yui/treeview/ln.gif";
	}

	public static String getExpandIcon(String basePath) {
		return basePath + "/images/yui/treeview/lp.gif";
	}

	public static String getCollapseIcon(String basePath) {
		return basePath + "/images/yui/treeview/lm.gif";
	}
	
	private static void init(String basePath) {
		if (BASEPATH != null)
			return;
		
		BASEPATH = basePath;
		ICON_LEAF = getLeafIcon(basePath);
		ICON_EXPAND = getExpandIcon(basePath);
		ICON_COLLAPSE = getCollapseIcon(basePath);
	}

	public static StringBuffer html(HttpServletRequest request, String dictionary, 
			String version, StringBuffer buffer, TreeItem top, String indent, 
			boolean skip) {
		Map<String, List<TreeItem>> map = top._assocToChildMap;
		Set<String> keys = map.keySet();
		
		if (! skip) {
    		boolean isLeafNode = keys.isEmpty();
    
    		append(buffer, indent + "<table border=0>");
    		append(buffer, indent + "  <tr>");
    		append(buffer, indent + "    <td width=\"" + INDENT_PIXELS + "\"></td>");
    		
    		append(buffer, indent + "    <td>");
//    		if (isLeafNode)
//    			append(buffer, indent + "      " 
//    				+ "<img src=\"" + ICON_LEAF +  "\">" + top._text
//    				+ "<div>");
//    		else
//    			append(buffer, indent + "      " 
//    				+ "<a onclick=\"toggle(this)\"><img src=\"" + ICON_COLLAPSE + "\">" + top._text + "</a>" 
//    				+ "<div>");

    		if (isLeafNode)
    			append(buffer, indent + "      " 
    				+ "<img src=\"" + ICON_LEAF +  "\">"
    				+ "<a href=\"" + JSPUtils.getConceptUrl(request, dictionary, version, top._code) +
    				"\">" + top._text + "</a>" 
    				+ "<div>");
    		else
    			append(buffer, indent + "      " 
    				+ "<div onclick=\"toggle(this)\">" 
    				+ "<img src=\"" + ICON_COLLAPSE + "\">" 
    				+ "<a href=\"" + JSPUtils.getConceptUrl(request, dictionary, version, top._code) + "\">" + top._text + "</a></div>"
    				+ "<div>");
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
				//html(buffer, item, indent + "      " + INDENT, false);
                html(request, dictionary, version, buffer, item, indent2, false);
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
	
	public static StringBuffer getHtml(HttpServletRequest request, String dictionary, 
		String version, String basePath, StringBuffer buffer, TreeItem top) {
		init(basePath);
	    return html(request, dictionary, version, buffer, top, "", true);
	}

	public static void main(String[] args) {
		TreeItem root = getSampleTree();
		println(Utils.SEPARATOR);
		debug(root, "");

		String basePath = "/ncitbrowser";
		String dictionary = "NCI Thesaurus";
		String version = "11.09d";
		println(Utils.SEPARATOR);
		StringBuffer buffer = new StringBuffer();
		getHtml(null, dictionary, version, basePath, buffer, root);
		println(buffer.toString());
	}
}
