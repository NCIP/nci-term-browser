package gov.nih.nci.evs.browser.utils;
import java.io.*;
import java.util.*;

/**
 * <!-- LICENSE_TEXT_START -->
 * Copyright 2008-2016 NGIS. This software was developed in conjunction
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
 *      "This product includes software developed by NGIS and the National
 *      Cancer Institute."   If no such end-user documentation is to be
 *      included, this acknowledgment shall appear in the software itself,
 *      wherever such third-party acknowledgments normally appear.
 *   3. The names "The National Cancer Institute", "NCI" and "NGIS" must
 *      not be used to endorse or promote products derived from this software.
 *   4. This license does not authorize the incorporation of this software
 *      into any third party proprietary programs. This license does not
 *      authorize the recipient to use any trademarks owned by either NCI
 *      or NGIS
 *   5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED
 *      WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *      OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE
 *      DISCLAIMED. IN NO EVENT SHALL THE NATIONAL CANCER INSTITUTE,
 *      NGIS, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT,
 *      INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 *      BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *      LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *      CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *      LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 *      ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *      POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */

/**
 * @author EVS Team
 * @version 1.0
 *
 * Modification history:
 *     Initial implementation kim.ong@ngc.com
 *
 */


public class HierarchyHelper {
    private static String TYPE_ROOT = "TYPE_ROOT";
    private static String TYPE_LEAF = "TYPE_LEAF";

    private static boolean FORWARD = true;
    private static boolean BACKWARD = false;

    //private HashMap code2NameMap = null;
    private HashMap code2LabelMap = null;


    private Vector _PARENT_CHILDREN = null;
	private String _ROOTS = null;

	private Vector rel_vec = null;
	private Vector label_vec = null;

	private Vector roots = null;
	private Vector leaves = null;

	private HashMap _parent2childcodesMap = null;
	private HashMap _child2parentcodesMap = null;

	private int FORMAT_PARENT_CHILD = 1;
	private int FORMAT_CHILD_PARENT = 2;

	private int format = 0;
    private boolean show_code = true;

    public HierarchyHelper() {
	}

    public HierarchyHelper(Vector v) {
        this.rel_vec = v;
        this.format = FORMAT_PARENT_CHILD;
        initialize(v, format);
	}


    public HierarchyHelper(Vector v, int format) {
		long ms = System.currentTimeMillis();
        this.rel_vec = v;
        this.format = format;
        initialize(v, format);
	}

	public void set_show_code(boolean bool) {
		this.show_code = bool;
	}

	public HashMap get_parent2childcodesMap() {
		return _parent2childcodesMap;
	}

	public HashMap get_child2parentcodesMap() {
		return _child2parentcodesMap;
	}

	private HashMap getInverseHashMap(HashMap hmap) {
		HashMap inverse_hmap = new HashMap();
		Iterator it = hmap.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			Vector v = (Vector) hmap.get(key);
			for (int k=0; k<v.size(); k++) {
				String value = (String) v.elementAt(k);
				Vector w = new Vector();
				if (inverse_hmap.containsKey(value)) {
					w = (Vector) inverse_hmap.get(value);
				}
				if (!w.contains(key)) {
					w.add(key);
				}
				inverse_hmap.put(value, w);
			}
		}
		return inverse_hmap;
	}

	private void initialize(Vector v, int format) {
		System.out.println("\nInstantiating HierarchyHelper...");
		long ms0 = System.currentTimeMillis();
		long ms = System.currentTimeMillis();
        this._parent2childcodesMap = createparent2childcodesMap(v, format);
        this._child2parentcodesMap = getInverseHashMap(_parent2childcodesMap);
        //findRootAndLeafNodes();
		//System.out.println("createparent2childcodesMap run time (ms): " + (System.currentTimeMillis() - ms));
        //ms = System.currentTimeMillis();
        this.code2LabelMap = createCode2LabelMap(v, format);
		//System.out.println("createCode2LabelMap run time (ms): " + (System.currentTimeMillis() - ms));
		//System.out.println("Total initialization run time (ms): " + (System.currentTimeMillis() - ms0));
	}

	public Vector getRoots() {
		return this.roots;
	}

	public Vector getLeaves() {
		return this.leaves;
	}

	public Vector sortCodesByNames(Vector codes) {
		Vector names = new Vector();
		HashMap hmap = new HashMap();
		for (int i=0; i<codes.size(); i++) {
			String code = (String) codes.elementAt(i);
			String name = (String) code2LabelMap.get(code);
			names.add(name);
			hmap.put(name, code);
		}
		names = new SortUtils().quickSort(names);
		Vector u = new Vector();
		for (int i=0; i<names.size(); i++) {
			String name = (String) names.elementAt(i);
			String code = (String) hmap.get(name);
			u.add(code);
		}
	    return u;
	}

	public void findRootAndLeafNodes() {
        roots = findRoots(_parent2childcodesMap);
        leaves = findLeaves(_parent2childcodesMap);
        roots = sortCodesByNames(roots);
        leaves = sortCodesByNames(leaves);
	}

	public void load_parent_child_rel(String filename) {
		this.rel_vec = Utils.readFile(filename);
		System.out.println("parent_child_rel size: " + this.rel_vec.size());
	}

    public void set_rel_vec(Vector rel_vec) {
        this.rel_vec = rel_vec;
	}


    public void set_label_vec(Vector label_vec) {
        this.label_vec = label_vec;
        this.code2LabelMap = createCode2LabelMap(label_vec);
	}

	public HashMap createCode2LabelMap(Vector label_vec) {
		HashMap label_hmap = new HashMap();
		for (int i=0; i<label_vec.size(); i++) {
			String t = (String) label_vec.elementAt(i);
			Vector u = StringUtils.parseData(t);
			String code = (String) u.elementAt(0);
			String name = (String) u.elementAt(1);
			label_hmap.put(code, name);
		}
		return label_hmap;
	}

    public String getLabel(String code) {
	    if (this.code2LabelMap.containsKey(code)) {
			return (String) code2LabelMap.get(code);
		}
		return null;
	}


	public Vector findRoots(HashMap _parent2childcodesMap) {
		Vector parent_codes = new Vector();
		Vector child_codes = new Vector();

		Iterator it = _parent2childcodesMap.keySet().iterator();
		while (it.hasNext()) {
			String parent_code = (String) it.next();
			parent_codes.add(parent_code);
		}

		it = _parent2childcodesMap.keySet().iterator();
		while (it.hasNext()) {
			String parent_code = (String) it.next();
			Vector w = (Vector) _parent2childcodesMap.get(parent_code);
			for (int i=0; i<w.size(); i++) {
				String s = (String) w.elementAt(i);
				child_codes.add(s);
			}
		}
		Vector all_codes = new Vector();
		all_codes.addAll(parent_codes);
		all_codes.addAll(child_codes);
		Vector roots = new Vector();
		for (int i=0; i<all_codes.size(); i++) {
			String s = (String) all_codes.elementAt(i);
			if (parent_codes.contains(s) && !child_codes.contains(s)) {
				roots.add(s);
			}
		}
		return new SortUtils().quickSort(roots);
	}

	public Vector findLeaves(HashMap _parent2childcodesMap) {
		Vector parent_codes = new Vector();
		Vector child_codes = new Vector();

		Iterator it = _parent2childcodesMap.keySet().iterator();
		while (it.hasNext()) {
			String parent_code = (String) it.next();
			parent_codes.add(parent_code);
		}

		it = _parent2childcodesMap.keySet().iterator();
		while (it.hasNext()) {
			String parent_code = (String) it.next();
			Vector w = (Vector) _parent2childcodesMap.get(parent_code);
			for (int i=0; i<w.size(); i++) {
				String s = (String) w.elementAt(i);
				child_codes.add(s);
			}
		}
		Vector all_codes = new Vector();
		all_codes.addAll(parent_codes);
		all_codes.addAll(child_codes);
		Vector leaf_nodes = new Vector();
		for (int i=0; i<all_codes.size(); i++) {
			String s = (String) all_codes.elementAt(i);
			if (!parent_codes.contains(s) && child_codes.contains(s)) {
				leaf_nodes.add(s);
			}
		}
		return new SortUtils().quickSort(leaf_nodes);
	}



	private HashMap createparent2childcodesMap(Vector w) {
		return createparent2childcodesMap(w, FORMAT_PARENT_CHILD);
	}


	private HashMap createparent2childcodesMap(Vector w, int format) {
		HashMap parent2childcodesMap = new HashMap();
        for (int i=0; i<w.size(); i++) {
			String t = (String) w.elementAt(i);
			Vector u = StringUtils.parseData(t);
			String parent_code = null;
			String child_code = null;
			if (format == FORMAT_PARENT_CHILD) {
				if (u.size() == 2) {
					parent_code = (String) u.elementAt(0);
					child_code = (String) u.elementAt(1);
				} else {
					parent_code = (String) u.elementAt(1);
					child_code = (String) u.elementAt(3);
				}
		    } else {
				if (u.size() == 2) {
					parent_code = (String) u.elementAt(1);
					child_code = (String) u.elementAt(0);
				} else {
					parent_code = (String) u.elementAt(3);
					child_code = (String) u.elementAt(1);
				}
			}

			Vector v = new Vector();
			if (parent2childcodesMap.containsKey(parent_code)) {
				v = (Vector) parent2childcodesMap.get(parent_code);
			}
			if (!v.contains(child_code)) {
				v.add(child_code);
			}
			parent2childcodesMap.put(parent_code, v);
		}
		return parent2childcodesMap;
	}


///////////////////////////
	private HashMap createCode2LabelMap(Vector w, int format) {
		HashMap label_hmap = new HashMap();
        for (int i=0; i<w.size(); i++) {
			String t = (String) w.elementAt(i);
			Vector u = StringUtils.parseData(t);
			if (u.size() == 2) {
				String code = (String) u.elementAt(0);
				String label = (String) u.elementAt(1);
				label_hmap.put(code, label);

			} else {
				String parent_code = null;
				String child_code = null;
				String parent_label = null;
				String child_label = null;

				if (format == FORMAT_PARENT_CHILD) {
					parent_label = (String) u.elementAt(0);
					parent_code = (String) u.elementAt(1);
					child_label = (String) u.elementAt(2);
					child_code = (String) u.elementAt(3);
				} else {
					child_label = (String) u.elementAt(0);
					child_code = (String) u.elementAt(1);
					parent_label = (String) u.elementAt(2);
					parent_code = (String) u.elementAt(3);
				}
				label_hmap.put(parent_code, parent_label);
				label_hmap.put(child_code, child_label);
		    }
		}
		return label_hmap;
	}

////////////////////////////////////////////////////////////////////////////////////

	public Vector getSubclassCodes(String code) {
		if (!_parent2childcodesMap.containsKey(code)) return null;
		return (Vector) _parent2childcodesMap.get(code);
	}

	public Vector getSuperclassCodes(String code) {
		if (!_child2parentcodesMap.containsKey(code)) return null;
		return (Vector) _child2parentcodesMap.get(code);
	}

	public Vector mergeVector(Vector v, Vector u) {
		for (int i=0; i<u.size(); i++) {
			String t = (String) u.elementAt(i);
			if (!v.contains(t)) {
				v.add(t);
			}
		}
		return v;
	}

    public Vector getTransitiveClosure(String c) {
		if (c == null) return null;
		return getTransitiveClosure(new Vector(), c);
	}

	public Vector getTransitiveClosure(Vector v, String c) {
		Vector child_codes = getSubclassCodes(c);
		if (child_codes == null || child_codes.size() == 0) return v;
		for (int i=0; i<child_codes.size(); i++) {
			String child_code = (String) child_codes.elementAt(i);
			System.out.println("Parent " + c + " child: " + child_code);
			Vector w = getTransitiveClosure(v, child_code);
			if (w != null && w.size() > 0) {
				v = mergeVector(v, w);
			}
			if (!v.contains(child_code)) {
				v.add(child_code);
			}
		}
		return v;
	}

	public void printTree() {
		if (roots == null) {
			findRootAndLeafNodes();
		}
		for (int i=0; i<roots.size(); i++) {
			String root = (String) roots.elementAt(i);
			printTree(root, 0);
		}
	}

	public void printTree(String code, int level) {
		String indent = "";
		for (int i=0; i<level; i++) {
			indent = indent + "\t";
		}
		String label = getLabel(code);
		//System.out.println(indent + code);
		System.out.println(indent + label + " (" + code + ")");
		Vector child_codes = getSubclassCodes(code);
		if (child_codes != null && child_codes.size() > 0) {
			for (int i=0; i<child_codes.size(); i++) {
				String child_code = (String) child_codes.elementAt(i);
				printTree(child_code, level+1);
			}
		}
	}



	public void printTree(PrintWriter pw) {
		if (roots == null) {
			findRootAndLeafNodes();
		}

		Vector label_vec = new Vector();
		HashMap label2codeMap = new HashMap();
		for (int i=0; i<roots.size(); i++) {
			String root = (String) roots.elementAt(i);
			String label = getLabel(root);
			label2codeMap.put(label, root);
			label_vec.add(label);
		}
		label_vec = new SortUtils().quickSort(label_vec);
		for (int i=0; i<label_vec.size(); i++) {
			String label = (String) label_vec.elementAt(i);
			String code = (String) label2codeMap.get(label);
			printTree(pw, code, 0);
		}

	}

	public void printTree(PrintWriter pw, String code, int level) {
		String indent = "";
		for (int i=0; i<level; i++) {
			indent = indent + "\t";
		}
		String label = getLabel(code);
		if (show_code) {
			pw.println(indent + label + " (" + code + ")");
		} else {
			pw.println(indent + label);
		}

		Vector child_codes = getSubclassCodes(code);
        if (child_codes != null && child_codes.size() > 0) {
			Vector label_vec = new Vector();
			HashMap label2codeMap = new HashMap();
			for (int i=0; i<child_codes.size(); i++) {
				String root = (String) child_codes.elementAt(i);
				label = getLabel(root);
				label2codeMap.put(label, root);
				label_vec.add(label);
			}
			label_vec = new SortUtils().quickSort(label_vec);

			for (int i=0; i<label_vec.size(); i++) {
				label = (String) label_vec.elementAt(i);
				String child_code = (String) label2codeMap.get(label);
				printTree(pw, child_code, level+1);
			}
		}
	}

    public static void main(String[] args) {
		Vector v = Utils.readFile("tvs_rel.txt");
		HierarchyHelper test = new HierarchyHelper(v, 2);
		Vector roots = test.getRoots();
		StringUtils.dumpVector("roots", roots);
		Vector leaves = test.getLeaves();
		StringUtils.dumpVector("leaves", leaves);

		for (int i=0; i<roots.size(); i++) {
			String root = (String) roots.elementAt(i);
			Vector w = test.getTransitiveClosure(root);
			StringUtils.dumpVector("\n" + root, w);
		}
		test.printTree();
	}
}
