package gov.nih.nci.evs.browser.utils;

import java.util.*;
import java.io.*;
import java.util.Map.Entry;

public class GraphUtils {
    public static int NODES_ONLY = 1;
    public static int EDGES_ONLY = 2;
    public static int NODES_AND_EDGES = 3;

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

	public static String generateGraphScript(HashSet nodes, HashMap edge_map, int option) {
		Vector node_label_vec = new Vector();
		Iterator it = nodes.iterator();
		while (it.hasNext()) {
			String node_label = (String) it.next();
			node_label_vec.add(node_label);
		}
		node_label_vec = SortUtils.quickSort(node_label_vec);
		int knt = 0;
		HashMap label2IdMap = new HashMap();
		for (int k=0; k<node_label_vec.size(); k++) {
			String node_label = (String) node_label_vec.elementAt(k);
			int j = k+1;
			String id = "" + j;
			label2IdMap.put(node_label, id);
		}

		StringBuffer buf = new StringBuffer();
        if (option == NODES_ONLY || option == NODES_AND_EDGES) {
			buf.append("var nodes = [").append("\n");
			for (int k=0; k<node_label_vec.size()-1; k++) {
				String node_label = (String) node_label_vec.elementAt(k);
				String id = (String) label2IdMap.get(node_label);
				buf.append("{id: " + id + ", label: '" + node_label + "'},").append("\n");
			}
			String node_label = (String) node_label_vec.elementAt(node_label_vec.size()-1);
			String id = (String) label2IdMap.get(node_label);
			buf.append("{id: " + id + ", label: '" + node_label + "'}").append("\n");
			buf.append("];").append("\n");
	    }

        if (option == EDGES_ONLY || option == NODES_AND_EDGES) {
			buf.append("var edges = [").append("\n");
			int count = 0;
			Iterator it2 = edge_map.keySet().iterator();
			while (it2.hasNext()) {
				String key = (String) it2.next();
				Vector w = (Vector) edge_map.get(key);
				count = count + w.size();
			}

			int m = 0;
			it2 = edge_map.keySet().iterator();
			while (it2.hasNext()) {
				String key = (String) it2.next();
				Vector u = gov.nih.nci.evs.browser.utils.StringUtils.parseData(key, "$");
				String source = (String) u.elementAt(0);
				String target = (String) u.elementAt(1);

				String from_id = (String) label2IdMap.get(source);
				String to_id = (String) label2IdMap.get(target);
				Vector w = (Vector) edge_map.get(key);
				for (int k=0; k<w.size(); k++) {
					String edge = (String) w.elementAt(k);
					m++;
					if (m < count) {
						buf.append("{from: " + from_id + ", to: " + to_id + ", arrows:'to', label: '" + edge + "'}, ").append("\n");
					} else {
						buf.append("{from: " + from_id + ", to: " + to_id + ", arrows:'to', label: '" + edge + "'}").append("\n");
					}
				}
			}
			buf.append("];").append("\n");
		}
		return buf.toString();
	}


    public static String generateGraphScript(Vector graphData, int option) {
		if (graphData == null) return null;
		HashSet nodes = new HashSet();
		HashMap edge_map = new HashMap();
        for (int i=0; i<graphData.size(); i++) {
			String t = (String) graphData.elementAt(i);
			Vector w = gov.nih.nci.evs.browser.utils.StringUtils.parseData(t);
			String source = (String) w.elementAt(0);
			String target = (String) w.elementAt(1);
			String relationship = (String) w.elementAt(2);
			if (!nodes.contains(source)) {
				nodes.add(source);
			}
			if (!nodes.contains(target)) {
				nodes.add(target);
			}
			String key = source + "$" + target;
			Vector v = (Vector) edge_map.get(key);
			if (v == null) {
				v = new Vector();
			}
			v.add(relationship);
			edge_map.put(key, v);
		}
		return generateGraphScript(nodes, edge_map, option);
    }

    public static void main(String [] args) {
		Vector graphData = readFile("graph.txt");
        String t = generateGraphScript(graphData, NODES_ONLY);
        System.out.println("\n" + t);

        t = generateGraphScript(graphData, EDGES_ONLY);
        System.out.println("\n" + t);

        t = generateGraphScript(graphData, NODES_AND_EDGES);
        System.out.println("\n" + t);

	}
}

