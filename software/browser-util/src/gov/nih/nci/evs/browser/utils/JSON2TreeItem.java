package gov.nih.nci.evs.browser.utils;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.json.simple.JSONValue;

import java.io.*;
import java.util.*;

//http://code.google.com/p/json-simple/wiki/DecodingExamples

public class JSON2TreeItem {

	public static final String ONTOLOGY_NODE_CHILD_COUNT = "ontology_node_child_count";

	/** The Constant ONTOLOGY_NODE_ID. */
	public static final String ONTOLOGY_NODE_ID = "ontology_node_id";

	/** The Constant ONTOLOGY_NODE_NS. */
	public static final String ONTOLOGY_NODE_NS = "ontology_node_ns";

	/** The Constant ONTOLOGY_NODE_NAME. */
	public static final String ONTOLOGY_NODE_NAME = "ontology_node_name";

	/** The Constant CHILDREN_NODES. */
	public static final String CHILDREN_NODES = "children_nodes";

	/** The Constant NODES. */
	public static final String NODES = "nodes";

	/** The Constant PAGE. */
	public static final String PAGE = "page";

	/** The MA x_ children. */
	private int MAX_CHILDREN = 5;

	/** The SUBCHILDRE n_ levels. */
	private int SUBCHILDREN_LEVELS = 1;

	/** The MOR e_ childre n_ indicator. */
	private static String MORE_CHILDREN_INDICATOR = "...";

	private static String assocText = "CHD";


    public static String VIH_JSON =
"[{\"children_nodes\":[{\"children_nodes\":[{\"children_nodes\":[{\"children_nodes\":[{\"children_nodes\":[],\"ontology_node_child_count\":1,\"ontology_node_id\":\"NPO_1607\",\"ontology_node_name\":\"targeting\"},{\"children_nodes\":[],\"ontology_node_child_count\":1,\"ontology_node_id\":\"NPO_564\",\"ontology_node_name\":\"antineoplastic activity\"},{\"children_nodes\":[],\"ontology_node_child_count\":1,\"ontology_node_id\":\"GO_0003674\",\"ontology_node_name\":\"molecular_function\"},{\"children_nodes\":[],\"ontology_node_child_count\":1,\"ontology_node_id\":\"GO_0003674\",\"ontology_node_name\":\"molecular function\"},{\"children_nodes\":[],\"ontology_node_child_count\":1,\"ontology_node_id\":\"NPO_1425\",\"ontology_node_name\":\"detection\"},{\"children_nodes\":[],\"page\":1,\"ontology_node_child_count\":0,\"ontology_node_id\":\"Process\",\"ontology_node_name\":\"...\"}],\"ontology_node_child_count\":1,\"ontology_node_id\":\"Process\",\"ontology_node_name\":\"process\"},{\"children_nodes\":[],\"ontology_node_child_count\":0,\"ontology_node_id\":\"ProcessAggregate\",\"ontology_node_name\":\"process_aggregate\"},{\"children_nodes\":[],\"ontology_node_child_count\":0,\"ontology_node_id\":\"ProcessualContext\",\"ontology_node_name\":\"processual_context\"},{\"children_nodes\":[],\"ontology_node_child_count\":0,\"ontology_node_id\":\"ProcessBoundary\",\"ontology_node_name\":\"process_boundary\"},{\"children_nodes\":[],\"ontology_node_child_count\":0,\"ontology_node_id\":\"FiatProcessPart\",\"ontology_node_name\":\"fiat_process_part\"},{\"children_nodes\":[],\"page\":1,\"ontology_node_child_count\":0,\"ontology_node_id\":\"ProcessualEntity\",\"ontology_node_name\":\"...\"}],\"ontology_node_child_count\":1,\"ontology_node_id\":\"ProcessualEntity\",\"ontology_node_name\":\"processual_entity\"},{\"children_nodes\":[],\"ontology_node_child_count\":1,\"ontology_node_id\":\"SpatiotemporalRegion\",\"ontology_node_name\":\"spatiotemporal_region\"},{\"children_nodes\":[],\"ontology_node_child_count\":1,\"ontology_node_id\":\"TemporalRegion\",\"ontology_node_name\":\"temporal_region\"}],\"ontology_node_child_count\":1,\"ontology_node_id\":\"Occurrent\",\"ontology_node_name\":\"occurrent\"},{\"children_nodes\":[],\"ontology_node_child_count\":1,\"ontology_node_id\":\"Continuant\",\"ontology_node_name\":\"continuant\"}],\"ontology_node_child_count\":1,\"ontology_node_id\":\"Entity\",\"ontology_node_name\":\"entity\"},{\"children_nodes\":[],\"ontology_node_child_count\":1,\"ontology_node_id\":\"DomainConcept\",\"ontology_node_name\":\"DomainConcept\"},{\"children_nodes\":[],\"ontology_node_child_count\":1,\"ontology_node_id\":\"DomainConcept\",\"ontology_node_name\":\"DomainConcept\"},{\"children_nodes\":[],\"ontology_node_child_count\":1,\"ontology_node_id\":\"DomainConcept\",\"ontology_node_name\":\"DomainConcept\"},{\"children_nodes\":[],\"ontology_node_child_count\":1,\"ontology_node_id\":\"ValuePartition\",\"ontology_node_name\":\"ValorDaParticao\"},{\"children_nodes\":[],\"page\":1,\"ontology_node_child_count\":0,\"ontology_node_id\":\"@@\",\"ontology_node_name\":\"...\"}]";


	public JSON2TreeItem() {

	}


	public static TreeItem JSONObject2TreeItem(JSONObject jsonObj) {
		JSONParser parser = new JSONParser();

		TreeItem ti = new TreeItem("", "");
		Iterator it = jsonObj.keySet().iterator();
        while (it.hasNext()) {
			String key = (String) it.next();
			Object obj = jsonObj.get(key);
			if (obj instanceof String || obj instanceof Double || obj instanceof Float || obj instanceof Number) {
				String value = JSONValue.toJSONString(jsonObj.get(key));
				if (value.startsWith("\"")) {
					value = value.substring(1, value.length());
				}
				if (value.endsWith("\"")) {
					value = value.substring(0, value.length()-1);
				}
				if (key.compareTo(ONTOLOGY_NODE_ID) == 0) {
					ti._code = value;

				} else if (key.compareTo(ONTOLOGY_NODE_NS) == 0) {
					ti._ns = value;

				} else if (key.compareTo(ONTOLOGY_NODE_NAME) == 0) {
					ti._text = value;
				} else if (key.compareTo(ONTOLOGY_NODE_CHILD_COUNT) == 0) {
					int count = Integer.parseInt(value);
					ti._expandable = false;
					if (count > 0) {
						ti._expandable = true;
					}
				} else if (key.compareTo(PAGE) == 0) {
				}
			} else if (key.compareTo(CHILDREN_NODES) == 0) {
				String value = JSONValue.toJSONString(jsonObj.get(key));
				if (value.compareTo("[]") != 0) {
					try {
						Object children_node_obj = parser.parse(value);
						JSONArray array=(JSONArray) children_node_obj;

						for (int i=0; i<array.size(); i++) {
							JSONObject object = (JSONObject) JSONValue.parse((array.get(i)).toString());
							TreeItem child_ti = JSONObject2TreeItem(object);

							ti.addChild(assocText, child_ti);
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
		return ti;
	}


    public static TreeItem json2TreeItem(String json_string) {
		TreeItem ti = new TreeItem("<Root>", "Root node");
		try {
			JSONParser parser=new JSONParser();
			String assocText = "has_child";
			Object obj = parser.parse(json_string);
			JSONArray array=(JSONArray)obj;

			for (int i=0; i<array.size(); i++) {
				JSONObject object = (JSONObject) JSONValue.parse((array.get(i)).toString());
				TreeItem child_ti = JSONObject2TreeItem(object);
				ti.addChild(assocText, child_ti);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return ti;
	}
}

