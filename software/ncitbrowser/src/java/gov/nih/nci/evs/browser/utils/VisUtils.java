package gov.nih.nci.evs.browser.utils;

import gov.nih.nci.evs.browser.utils.*;

import java.util.*;
import java.io.*;
import java.util.Map.Entry;
import gov.nih.nci.evs.browser.bean.*;

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


public class VisUtils {
    private static Logger _logger = Logger.getLogger(VisUtils.class);
    private LexBIGService lbSvc = null;
    private LexBIGServiceConvenienceMethods lbscm = null;

    public static int NODES_ONLY = 1;
    public static int EDGES_ONLY = 2;
    public static int NODES_AND_EDGES = 3;

	public VisUtils() {

	}

	public VisUtils(LexBIGService lbSvc) {
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


    public String getLabel(String name, String code) {
		name = encode(name);
		StringBuffer buf = new StringBuffer();
		buf.append(name + " (" + code + ")");
		return buf.toString();
	}

    public String getLabel(String line) {
        Vector u = gov.nih.nci.evs.browser.utils.StringUtils.parseData(line);
        String name = (String) u.elementAt(0);
        name = encode(name);
        String code = (String) u.elementAt(1);
        return getLabel(name, code);
	}

    public String getFieldValue(String line, int index) {
        Vector u = gov.nih.nci.evs.browser.utils.StringUtils.parseData(line);
        return (String) u.elementAt(index);
	}

	public String encode(String t) {
		if (t == null) return null;
		t = t.replaceAll("'", " ");
		return t;
	}


    public String generateDiGraph(String scheme, String version, String namespace, String code) {
		boolean useNamespace = false;
		if (namespace != null) useNamespace = true;
		Entity concept = new ConceptDetails(lbSvc).getConceptByCode(scheme, version, code, namespace, useNamespace);
		String name = "<NO DESCRIPTION>";
		if (concept.getEntityDescription() != null) {
			name = concept.getEntityDescription().getContent();
		}
		name = encode(name);

		if (gov.nih.nci.evs.browser.utils.StringUtils.isNullOrBlank(namespace)) {
			namespace = concept.getEntityCodeNamespace();
		}

		StringBuffer buf = new StringBuffer();
        buf.append("\ndigraph {").append("\n");
        buf.append("node [shape=oval fontsize=16]").append("\n");
        buf.append("edge [length=100, color=gray, fontcolor=black]").append("\n");

        String focused_node_label = "\"" + getLabel(name, code) + "\"" ;

        RelationshipUtils relUtils = new RelationshipUtils(lbSvc);
        HashMap relMap = relUtils.getRelationshipHashMap(scheme, version, code, namespace, useNamespace);

		String key = "type_superconcept";
		ArrayList list = (ArrayList) relMap.get(key);
		if (list != null) {
			for (int i=0; i<list.size(); i++) {
				String t = (String) list.get(i);
				String rel_node_label = "\"" + getLabel(t) + "\"" ; //getLabel(t);
				String rel_label = "is_a";
				buf.append(focused_node_label + " -> " + rel_node_label).append("\n");
				buf.append("[label=" + rel_label + "];").append("\n");
			}
		}

		key = "type_subconcept";
		list = (ArrayList) relMap.get(key);
		if (list != null) {
			for (int i=0; i<list.size(); i++) {
				String t = (String) list.get(i);
				String rel_node_label = "\"" + getLabel(t) + "\"" ; //getLabel(t);
				String rel_label = "inverse_is_a";
				buf.append(focused_node_label + " -> " + rel_node_label).append("\n");
				buf.append("[label=" + rel_label + "];").append("\n");
			}
		}

		key = "type_role";
		list = (ArrayList) relMap.get(key);
		if (list != null) {
			for (int i=0; i<list.size(); i++) {
				String t = (String) list.get(i);
				String rel_node_label =  "\"" + getLabel(getFieldValue(t, 1), getFieldValue(t, 2)) + "\"";
				String rel_label = getFieldValue(t, 0);
				buf.append(focused_node_label + " -> " + rel_node_label).append("\n");
				buf.append("[label=" + rel_label + "];").append("\n");
			}
		}

		key = "type_inverse_role";
		list = (ArrayList) relMap.get(key);
		if (list != null) {
			for (int i=0; i<list.size(); i++) {
				String t = (String) list.get(i);
				String rel_node_label =  "\"" + getLabel(getFieldValue(t, 1), getFieldValue(t, 2)) + "\"";
				String rel_label = getFieldValue(t, 0);
				buf.append(rel_node_label + " -> " + focused_node_label).append("\n");
				buf.append("[label=" + rel_label + "];").append("\n");
			}
		}

		key = "type_association";
		list = (ArrayList) relMap.get(key);
		if (list != null) {
			for (int i=0; i<list.size(); i++) {
				String t = (String) list.get(i);
				String rel_node_label =  "\"" + getLabel(getFieldValue(t, 1), getFieldValue(t, 2)) + "\"";
				String rel_label = getFieldValue(t, 0);
				buf.append(focused_node_label + " -> " + rel_node_label).append("\n");
				buf.append("[label=" + rel_label + "];").append("\n");
			}
		}

		key = "type_inverse_association";
		list = (ArrayList) relMap.get(key);
		if (list != null) {
			for (int i=0; i<list.size(); i++) {
				String t = (String) list.get(i);
				String rel_node_label =  "\"" + getLabel(getFieldValue(t, 1), getFieldValue(t, 2)) + "\"";
				String rel_label = getFieldValue(t, 0);
				buf.append(rel_node_label + " -> " + focused_node_label).append("\n");
				buf.append("[label=" + rel_label + "];").append("\n");
			}
		}


        buf.append(focused_node_label + " [").append("\n");
        buf.append("fontcolor=white,").append("\n");
        buf.append("color=red,").append("\n");
        buf.append("]").append("\n");
        buf.append("}").append("\n");
        return buf.toString();
	}


/*
<script type="text/javascript">
  var nodes = [
    {id: 1, label: 'Node 1', font: {strokeWidth: 3, strokeColor: 'white'}},
    {id: 2, label: 'Node 2'},
    {id: 3, label: 'Node 3'},
    {id: 4, label: 'Node 4'},
    {id: 5, label: 'Node 5'}
  ];

  // create an array with edges
  var edges = [
    {from: 1, to: 2, label: 'edgeLabel', font: {strokeWidth: 2, strokeColor : '#00ff00'}},
    {from: 1, to: 3, label: 'edgeLabel'},
    {from: 2, to: 4},
    {from: 2, to: 5}
  ];

  // create a network
  var container = document.getElementById('mynetwork');
  var data = {
    nodes: nodes,
    edges: edges
  };
  var options = {
    nodes : {
      shape: 'dot',
      size: 10
    }
  };
  var network = new vis.Network(container, data, options);
</script>
*/


    public String generateGraphScript(String scheme, String version, String namespace, String code) {
		return generateGraphScript(scheme, version, namespace, code, null);
	}


    public String generateGraphScript(String scheme, String version, String namespace, String code, String[] types) {
		return generateGraphScript(scheme, version, namespace, code, types, NODES_AND_EDGES, null);
	}


    public String generateGraphScript(String scheme, String version, String namespace, String code, String[] types, int option, HashMap hmap) {
		List typeList = null;
		if (types != null) {
			typeList = Arrays.asList(types);
		} else {
			typeList = new ArrayList();
			typeList.add("type_superconcept");
			typeList.add("type_subconcept");
			/*
			typeList.add("type_role");
			typeList.add("type_inverse_role");
			typeList.add("type_association");
			typeList.add("type_inverse_association");
			*/
		}


boolean useNamespace = true;
if (gov.nih.nci.evs.browser.utils.StringUtils.isNullOrBlank(namespace)) {
	useNamespace = false;
}

System.out.println("scheme: " + scheme);
System.out.println("version: " + version);
System.out.println("code: " + code);
System.out.println("namespace: " + namespace);
System.out.println("useNamespace: " + useNamespace);


		Entity concept = new ConceptDetails(lbSvc).getConceptByCode(scheme, version, code, namespace, useNamespace);
		String name = "<NO DESCRIPTION>";
		if (concept.getEntityDescription() != null) {
			name = concept.getEntityDescription().getContent();
		}
		name = encode(name);

		if (gov.nih.nci.evs.browser.utils.StringUtils.isNullOrBlank(namespace)) {
			namespace = concept.getEntityCodeNamespace();
		}
if (!gov.nih.nci.evs.browser.utils.StringUtils.isNullOrBlank(namespace)) {
	useNamespace = true;
}
        String focused_node_label = getLabel(name, code);

        HashMap relMap = null;
        if (relMap == null) {
			RelationshipUtils relUtils = new RelationshipUtils(lbSvc);
			relMap = relUtils.getRelationshipHashMap(scheme, version, code, namespace, useNamespace);
	    } else {
			relMap = hmap;
		}

        HashSet nodes = new HashSet();
        nodes.add(focused_node_label);

        ArrayList list = null;

		String key = null;

		key = "type_superconcept";
		if (typeList.contains(key)) {
			list = (ArrayList) relMap.get(key);
			if (list != null) {
				for (int i=0; i<list.size(); i++) {
					String t = (String) list.get(i);
					String rel_node_label = getLabel(t);
					if (!nodes.contains(rel_node_label)) {
						nodes.add(rel_node_label);
					}
				}
			}
	    }

		key = "type_subconcept";
		if (typeList.contains(key)) {
			list = (ArrayList) relMap.get(key);
			if (list != null) {
				for (int i=0; i<list.size(); i++) {
					String t = (String) list.get(i);
					String rel_node_label = getLabel(t);
					if (!nodes.contains(rel_node_label)) {
						nodes.add(rel_node_label);
					}
				}
			}
	    }

		key = "type_role";
		if (typeList.contains(key)) {
			list = (ArrayList) relMap.get(key);
			if (list != null) {
				for (int i=0; i<list.size(); i++) {
					String t = (String) list.get(i);
					String rel_node_label = getLabel(getFieldValue(t, 1), getFieldValue(t, 2));
					if (!nodes.contains(rel_node_label)) {
						nodes.add(rel_node_label);
					}
				}
			}
	    }

		key = "type_inverse_role";
		if (typeList.contains(key)) {
			list = (ArrayList) relMap.get(key);
			if (list != null) {
				for (int i=0; i<list.size(); i++) {
					String t = (String) list.get(i);
					String rel_node_label = getLabel(getFieldValue(t, 1), getFieldValue(t, 2));
					if (!nodes.contains(rel_node_label)) {
						nodes.add(rel_node_label);
					}
				}
			}
	    }

		key = "type_association";
		if (typeList.contains(key)) {
			list = (ArrayList) relMap.get(key);
			if (list != null) {
				for (int i=0; i<list.size(); i++) {
					String t = (String) list.get(i);
					String rel_node_label = getLabel(getFieldValue(t, 1), getFieldValue(t, 2));
					if (!nodes.contains(rel_node_label)) {
						nodes.add(rel_node_label);
					}
				}
			}
	    }

		key = "type_inverse_association";
		if (typeList.contains(key)) {
			list = (ArrayList) relMap.get(key);
			if (list != null) {
				for (int i=0; i<list.size(); i++) {
					String t = (String) list.get(i);
					String rel_node_label = getLabel(getFieldValue(t, 1), getFieldValue(t, 2));
					if (!nodes.contains(rel_node_label)) {
						nodes.add(rel_node_label);
					}
				}
			}
	    }

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

		if (option == NODES_AND_EDGES) {
			buf.append("var nodes = [").append("\n");
	    }

        if (option == NODES_ONLY || option == NODES_AND_EDGES) {
        if (option == NODES_ONLY) {
        	buf.append("[").append("\n");
	    }

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

        if (option == NODES_AND_EDGES) {
        	buf.append("var edges = [").append("\n");
	    }

        if (option == EDGES_ONLY || option == NODES_AND_EDGES) {
		if (option == EDGES_ONLY) {
        	buf.append("[").append("\n");
	    }
		key = "type_superconcept";
		if (typeList.contains(key)) {
			list = (ArrayList) relMap.get(key);
			if (list != null) {
				for (int i=0; i<list.size(); i++) {
					String t = (String) list.get(i);
					String rel_node_label = getLabel(t);
					String rel_label = "is_a";

					String from_id = (String) label2IdMap.get(focused_node_label);
					String to_id = (String) label2IdMap.get(rel_node_label);
					buf.append("{from: " + from_id + ", to: " + to_id + ", arrows:'to', label: '" + rel_label + "'}, ").append("\n");
				}
			}
	    }

		key = "type_subconcept";
		if (typeList.contains(key)) {
			list = (ArrayList) relMap.get(key);
			if (list != null) {
				for (int i=0; i<list.size(); i++) {
					String t = (String) list.get(i);
					String rel_node_label = getLabel(t);
					String rel_label = "is_a";

					String to_id = (String) label2IdMap.get(focused_node_label);
					String from_id = (String) label2IdMap.get(rel_node_label);
					buf.append("{from: " + from_id + ", to: " + to_id + ", arrows:'to', label: '" + rel_label + "'},").append("\n");
				}
			}
	    }

		key = "type_role";
		if (typeList.contains(key)) {
			list = (ArrayList) relMap.get(key);
			if (list != null) {
				for (int i=0; i<list.size(); i++) {
					String t = (String) list.get(i);
					String rel_node_label = getLabel(getFieldValue(t, 1), getFieldValue(t, 2));
					String rel_label = getFieldValue(t, 0);

					String from_id = (String) label2IdMap.get(focused_node_label);
					String to_id = (String) label2IdMap.get(rel_node_label);
					buf.append("{from: " + from_id + ", to: " + to_id + ", arrows:'to', label: '" + rel_label + "'},").append("\n");
				}
			}
	    }

		key = "type_inverse_role";
		if (typeList.contains(key)) {
			list = (ArrayList) relMap.get(key);
			if (list != null) {
				for (int i=0; i<list.size(); i++) {
					String t = (String) list.get(i);
					String rel_node_label = getLabel(getFieldValue(t, 1), getFieldValue(t, 2));
					String rel_label = getFieldValue(t, 0);

					String to_id = (String) label2IdMap.get(focused_node_label);
					String from_id = (String) label2IdMap.get(rel_node_label);
					buf.append("{from: " + from_id + ", to: " + to_id + ", arrows:'to', label: '" + rel_label + "'},").append("\n");
				}
			}
		}

		key = "type_association";
		if (typeList.contains(key)) {
			list = (ArrayList) relMap.get(key);
			if (list != null) {
				for (int i=0; i<list.size(); i++) {
					String t = (String) list.get(i);
					String rel_node_label = getLabel(getFieldValue(t, 1), getFieldValue(t, 2));
					String rel_label = getFieldValue(t, 0);

					String from_id = (String) label2IdMap.get(focused_node_label);
					String to_id = (String) label2IdMap.get(rel_node_label);
					buf.append("{from: " + from_id + ", to: " + to_id + ", arrows:'to', label: '" + rel_label + "'},").append("\n");
				}
			}
	    }

		key = "type_inverse_association";
		if (typeList.contains(key)) {
			list = (ArrayList) relMap.get(key);
			if (list != null) {
				for (int i=0; i<list.size(); i++) {
					String t = (String) list.get(i);
					String rel_node_label = getLabel(getFieldValue(t, 1), getFieldValue(t, 2));
					String rel_label = getFieldValue(t, 0);

					String to_id = (String) label2IdMap.get(focused_node_label);
					String from_id = (String) label2IdMap.get(rel_node_label);
					buf.append("{from: " + from_id + ", to: " + to_id + ", arrows:'to', label: '" + rel_label + "'},").append("\n");
				}
			}
		}
		buf.append("];");
	    }
        return buf.toString();
	}



    public static void main(String [] args) {

		//LexBIGService lbSvc = LexBIGServiceImpl.defaultInstance();
		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();

		VisUtils visUtils = new VisUtils(lbSvc);
/*
		String codingSchemeURN = "http://ncicb.nci.nih.gov/xml/owl/EVS/owl2lexevs.owl";
		codingSchemeURN = "owl2lexevs.owl";
		String codingSchemeVersion = "0.1.2";
		String code = "HappyPatientWalkingAround";
		String namespace = null;
		boolean useNamespace = false;
*/

		String codingSchemeURN = "NCI_Thesaurus";
		String codingSchemeVersion = "15.06e";
		String code = "C9118";//"Sarcoma (Code C9118)";
		String namespace = null;
		boolean useNamespace = false;
/*
        RelationshipUtils relUtils = new RelationshipUtils(lbSvc);
        HashMap relMap = relUtils.getRelationshipHashMap(codingSchemeURN, codingSchemeVersion, code, namespace, useNamespace);
        Iterator it = relMap.keySet().iterator();
        while (it.hasNext()) {
			String key = (String) it.next();
			ArrayList list = (ArrayList) relMap.get(key);
			System.out.println("\n" + key);
			if (list != null) {
				for (int i=0; i<list.size(); i++) {
					String t = (String) list.get(i);
					System.out.println("\t" + t);
				}
			}
		}
*/
		//String digraph = visUtils.generateDiGraph(codingSchemeURN, codingSchemeVersion, namespace, code);
		//System.out.println(digraph);

		String digraph = visUtils.generateGraphScript(codingSchemeURN, codingSchemeVersion, namespace, code);
		System.out.println(digraph);

	}
}

/*
digraph {
  node [shape=circle fontsize=16]
  edge [length=100, color=gray, fontcolor=black]

  A -> A[label=0.5];
  B -> B[label=1.2] -> C[label=0.7] -- A;
  B -> D;
  D -> {B; C}
  D -> E[label=0.2];
  F -> F;
  A [
    fontcolor=white,
    color=red,
  ]
}
*/