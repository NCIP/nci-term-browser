package gov.nih.nci.evs.browser.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.io.*;
import java.util.*;

import java.io.InputStream;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

import java.net.URLConnection;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import gov.nih.nci.evs.browser.properties.*;


public class RESTClient{
    public static final String ID = "id";
    public static final String ONTOLOGY_ID = "ontologyId";
    public static final String VERSION_NUMBER = "versionNumber";

    public static final int KEY_TO_VALUE_HASHMAP = 1;
    public static final int VALUE_TO_KEY_HASHMAP = 2;

    public static String getNodeTypeString (Node node) {
		short type = node.getNodeType();

        if (type == Node.ATTRIBUTE_NODE) {
			return "ATTRIBUTE_NODE";
		} else if (type == Node.CDATA_SECTION_NODE) {
			return "CDATA_SECTION_NODE";
		} else if (type == Node.COMMENT_NODE) {
			return "COMMENT_NODE";
		} else if (type == Node.DOCUMENT_FRAGMENT_NODE) {
			return "DOCUMENT_FRAGMENT_NODE";
		} else if (type == Node.DOCUMENT_FRAGMENT_NODE) {
			return "DOCUMENT_FRAGMENT_NODE";
		} else if (type == Node.DOCUMENT_NODE) {
			return "DOCUMENT_NODE";
		} else if (type == Node.DOCUMENT_POSITION_CONTAINED_BY) {
			return "DOCUMENT_POSITION_CONTAINED_BY";
		} else if (type == Node.DOCUMENT_POSITION_CONTAINS) {
			return "DOCUMENT_POSITION_CONTAINS";
		} else if (type == Node.DOCUMENT_POSITION_DISCONNECTED) {
			return "DOCUMENT_POSITION_DISCONNECTED";
		} else if (type == Node.DOCUMENT_POSITION_FOLLOWING) {
			return "DOCUMENT_POSITION_FOLLOWING";
		} else if (type == Node.DOCUMENT_POSITION_IMPLEMENTATION_SPECIFIC) {
			return "DOCUMENT_POSITION_IMPLEMENTATION_SPECIFIC";
		} else if (type == Node.DOCUMENT_POSITION_PRECEDING) {
			return "DOCUMENT_POSITION_PRECEDING";
		} else if (type == Node.DOCUMENT_TYPE_NODE) {
			return "DOCUMENT_TYPE_NODE";
		} else if (type == Node.ELEMENT_NODE) {
			return "ELEMENT_NODE";
		} else if (type == Node.ENTITY_NODE) {
			return "ENTITY_NODE";
		} else if (type == Node.ENTITY_REFERENCE_NODE) {
			return "ENTITY_REFERENCE_NODE";
		} else if (type == Node.NOTATION_NODE) {
			return "NOTATION_NODE";
		} else if (type == Node.PROCESSING_INSTRUCTION_NODE) {
			return "PROCESSING_INSTRUCTION_NODE";
		} else if (type == Node.TEXT_NODE) {
			return "TEXT_NODE";
		}
		return "UNKNOWN";
	}


    public static String extractValue(String line, String tag) {
		String open_tag = "<" + tag + ">";
		String close_tag = "</" + tag + ">";
		return extractValue(line, open_tag, close_tag);
	}



    public static String extractValue(String line, String open_tag, String close_tag)
    {
		boolean debug = false;
		if (line == null) return null;
		line = line.trim();

		if (debug) {
			System.out.println(line);
			System.out.println(open_tag);
			System.out.println(close_tag);
		}

		int n = line.indexOf(open_tag);
		if (debug) {
			System.out.println("n: " + n);
		}

		if (n == -1) return null;
		line = line.substring(n + open_tag.length(), line.length());
		if (debug) {
			System.out.println(line);
		}


		n = line.indexOf(close_tag);

		if (n == -1) return null;
		line = line.substring(0, n);
		line = line.trim();
		if (debug) {
			System.out.println("return: " + line);
		}
		return line;
	}



	private static String getValue(String tag, Element element) {
		if (tag == null || element == null) return null;

		if (element.getElementsByTagName(tag) == null) return null;
		if (element.getElementsByTagName(tag).item(0) == null) return null;


		NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
		if (nodes == null) return null;
		Node node = (Node) nodes.item(0);

		return node.getNodeValue();
	}


  public void dumpNamedNodeMap(NamedNodeMap map) {
	  if (map == null) return;
	  int len = map.getLength();

	  for (int i = 0; i < len; i++) {
		  Node node = map.item(i);
   	      System.out.println(node.getNodeName() + ": " + node.getNodeValue() + " (" + getNodeTypeString(node) + ")");
	  }
  }

  public void getChildNodes(Node node) {
	  NodeList child_nodes = node.getChildNodes();
	  if (child_nodes == null || child_nodes.getLength() == 0) {
		  System.out.println(node.getNodeName() + " has no child node.");
		  return;
	  }

	  for (int i = 0; i < child_nodes.getLength(); i++) {
		  Node child_node = child_nodes.item(i);
          if (child_node.getNodeType() == Node.ELEMENT_NODE) {
	           String value = getValue(child_node.getNodeName(), (Element) node);
	           value = value.trim();
	           if (value == null || value.length() == 0) {
				   value = getValue("core:value", (Element) node);
			   }
	           System.out.println(child_node.getNodeName() + ": " + value + " (" + getNodeTypeString(child_node) + ")");
		  }
	  }
  }


    protected BufferedReader getBufferReader(String filename) throws Exception {
        File file = new File(filename);
        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);
        BufferedReader br = new BufferedReader(new InputStreamReader(bis, "UTF8"));
        return br;
    }



  public static Vector retrieve_REST_content(String https_url) {
      if(https_url == null) return null;
      URL url = null;
      String id = null;
      String curr_id = null;
      String versionNumber = null;
      Vector v = new Vector();

      try {
          URLConnection con = new URL(https_url).openConnection();
		  BufferedReader reader =
			  new BufferedReader(
				  //new InputStreamReader(con.getInputStream()));
				  new InputStreamReader(con.getInputStream(), "UTF8"));

		  String sCurrentLine = null;
		  while ((sCurrentLine = reader.readLine()) != null) {
			  v.add(sCurrentLine);
		  }
          reader.close();

      } catch (IOException e) {
		  e.printStackTrace();
      }
      return v;
  }


  public static HashMap parse_REST_content(Vector v, String key_label, Vector value_label_vec) {
	  if (v == null || v.size() == 0) return null;
	  if (value_label_vec == null || value_label_vec.size() == 0) return null;

      HashMap hmap = new HashMap();
      String key = null;
      String curr_key = null;
      String value = null;
      Vector curr_value_vec = new Vector();
      for (int i=0; i<v.size(); i++) {
		  String sCurrentLine = (String) v.elementAt(i);
		  key = extractValue(sCurrentLine, key_label);
		  if (key != null) {
  			  if (curr_value_vec.size() > 0) {
				  String s = concatValues(curr_value_vec);
				  if (s != null) {
					  hmap.put(curr_key, s);
				  }
				  curr_value_vec = new Vector();
			  }
			  curr_key = key;
		  }

		  for (int k=0; k<value_label_vec.size(); k++) {
			  String value_label = (String) value_label_vec.elementAt(k);
		  	  value = extractValue(sCurrentLine, value_label);
			  if (value != null) {
				  curr_value_vec.add(value);
			  }
	      }
	  }

      if (curr_key != null) {
		  if (curr_value_vec.size() > 0) {
			  String s = concatValues(curr_value_vec);
			  if (s != null) {
				  hmap.put(curr_key, s);
			  }
			  //curr_value_vec = new Vector();
		  }
	  }

	  return hmap;
  }




  public static void export_REST_content(String filename, Vector v) {
	  PrintWriter pw = null;
	  try {
			File file = new File(filename);
			if (!file.exists()) {
				boolean retval = file.createNewFile();
				if (!retval) return;
			}
			pw = new PrintWriter(file, "UTF-8");
		    for (int i=0; i<v.size(); i++) {
			    String sCurrentLine = (String) v.elementAt(i);
			    pw.println(sCurrentLine);
			}

	  } catch (IOException e) {
		e.printStackTrace();
	  } finally {
		try {
			pw.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	  }
 }



    private static void dump_HashMap(HashMap<String, String> hmap) {
		if (hmap == null) return;

		Set<Map.Entry<String, String>> set = hmap.entrySet();
		for (Map.Entry<String, String> me: set) {
		    System.out.print(me.getKey() + ": ");
		    System.out.println(me.getValue());
		}
    }


    private static void dumpHashMap(HashMap<String, String> hmap) {
		if (hmap == null) return;

		Vector key_vec = new Vector();
		HashSet hset = new HashSet();

		Set<Map.Entry<String, String>> set = hmap.entrySet();
		for (Map.Entry<String, String> me: set) {
			String key = me.getKey();
			if (!hset.contains(key)) {
				hset.add(key);
				key_vec.add(key);
			}
		}

		key_vec = SortUtils.quickSort(key_vec);
		for (int i=0; i<key_vec.size(); i++) {
			String key = (String) key_vec.elementAt(i);
		    System.out.print(key + ": " + hmap.get(key) + "\n");
		}
    }


  public static HashMap parse_REST_content(Vector v, String key_label, Vector value_label_vec, int option) {
	  if (v == null || v.size() == 0) return null;
	  if (value_label_vec == null || value_label_vec.size() == 0) return null;

      HashMap hmap = new HashMap();
      String key = null;
      String curr_key = null;
      String value = null;
      Vector curr_value_vec = new Vector();
      for (int i=0; i<v.size(); i++) {
		  String sCurrentLine = (String) v.elementAt(i);
		  key = extractValue(sCurrentLine, key_label);
		  if (key != null) {
  			  if (curr_value_vec.size() > 0) {
				  String s = concatValues(curr_value_vec);
				  if (s != null) {
					  if (hmap.get(curr_key) == null) {
						  hmap.put(curr_key, s);
					  } else {
						  String t = (String) hmap.get(curr_key);
						  hmap.put(curr_key, t + "|" + s);
					  }
				  }
				  curr_value_vec = new Vector();
			  }
			  curr_key = key;
		  }

		  for (int k=0; k<value_label_vec.size(); k++) {
			  String value_label = (String) value_label_vec.elementAt(k);
		  	  value = extractValue(sCurrentLine, value_label);
			  if (value != null) {
				  curr_value_vec.add(value);
			  }
	      }
	  }

      if (curr_key != null) {
		  if (curr_value_vec.size() > 0) {
			  String s = concatValues(curr_value_vec);
			  if (s != null) {
				  hmap.put(curr_key, s);
			  }
		  }
	  }


      if (option == KEY_TO_VALUE_HASHMAP) {
		  return hmap;
	  }

	  return inverseHashMap(hmap);
  }


  public static Vector getKeys(HashMap hmap) {
	  return getKeys(hmap, null);
  }


  public static Vector getKeys(HashMap hmap, String target_value) {
	  Vector keys = new Vector();
	  Set<Map.Entry<String, String>> set = hmap.entrySet();
	  for (Map.Entry<String, String> me: set) {
   		  String key = me.getKey();
		  String value = me.getValue();
		  if (target_value == null) {
			  keys.add(key);
		  } else if (value.compareTo(target_value) == 0) {
			  keys.add(key);
		  }
	  }
	  keys = SortUtils.quickSort(keys);
	  return keys;
  }


  public static Vector getValues(HashMap hmap) {
	  return getValues(hmap, null);
  }


  public static Vector getValues(HashMap hmap, String target_key) {
	  Vector values = new Vector();
	  HashSet hset = new HashSet();
	  Set<Map.Entry<String, String>> set = hmap.entrySet();
	  for (Map.Entry<String, String> me: set) {
		  String key = me.getKey();
		  String value = me.getValue();
		  if (!hset.contains(value)) {
			  if (target_key == null) {
				  hset.add(value);
				  values.add(value);
			  } else {
				  if (key.compareTo(target_key) == 0) {
					  hset.add(value);
					  values.add(value);
				  }
			  }
		  }
	  }
	  values = SortUtils.quickSort(values);
	  return values;
  }




  public static HashMap inverseHashMap(HashMap hmap) {
	  if (hmap == null) return null;
	  HashMap inverseMap = new HashMap();
	  Set<Map.Entry<String, String>> set = hmap.entrySet();
	  HashSet value_hset = new HashSet();
	  Vector value_vec = new Vector();

	  for (Map.Entry<String, String> me: set) {
   		  String key = me.getKey();
		  String value = me.getValue();
		  if (!value_hset.contains(value)) {
			  value_hset.add(value);
			  value_vec.add(value);
		  }
	  }

	  value_vec = SortUtils.quickSort(value_vec);

	  for (int i=0; i<value_vec.size(); i++) {
		  String value = (String) value_vec.elementAt(i);
		  Vector keys = getKeys(hmap, value);
		  String t = concatValues(keys);
		  inverseMap.put(value, t);
	  }
	  return inverseMap;
  }


  public static HashMap parse_REST_content(Vector v, String key_label, String value_label) {
	  Vector w = new Vector();
	  w.add(value_label);
	  return parse_REST_content(v, key_label, w, KEY_TO_VALUE_HASHMAP);
  }

  public static HashMap parse_REST_content(Vector v, String key_label, String value_label, int option) {
	  if (v == null || v.size() == 0) return null;
	  Vector w = new Vector();
	  w.add(value_label);
	  return parse_REST_content(v, key_label, w, option);
  }

  public static String concatValues(Vector v) {

	  if (v == null || v.size() == 0) {
		  return null;
	  }
	  v = SortUtils.quickSort(v);

	  StringBuffer buf = new StringBuffer();
	  buf.append((String) v.elementAt(0));
	  if (v.size() == 1) return buf.toString();
	  for (int i=1; i<v.size(); i++) {
		  String t = (String) v.elementAt(i);
		  buf.append("|" + t);
	  }
	  return buf.toString();
  }


  private String getAPIKey() {
	  String api_key = NCItBrowserProperties.getNCBO_API_KEY();
	  return api_key;
  }

  public HashMap getVirtualId2NamesMap() {
	  return getVirtualId2NamesMap(getAPIKey());
  }


  public HashMap getVirtualId2NamesMap(String api_key) {
	     if (api_key == null) return null;
		 String url = "http://rest.bioontology.org/bioportal/ontologies?apikey=";
		 url = url + api_key;
		 Vector v = new RESTClient().retrieve_REST_content(url);
		 Vector name_vec = new Vector();
		 name_vec.add("abbreviation");
		 name_vec.add("displayLabel");
         HashMap virtual_id_2_name_hmap = parse_REST_content(v, RESTClient.ONTOLOGY_ID, name_vec);
         return virtual_id_2_name_hmap;
  }


  public HashMap getVersion2IdMap(String virtual_id) {
	  return getVersion2IdMap(virtual_id, getAPIKey());
  }


  public HashMap getVersion2IdMap(String virtual_id, String api_key) {
		 String url = "http://rest.bioontology.org/bioportal/ontologies/versions/" + virtual_id + "?apikey=";
		 url = url + api_key;
		 HashMap hmap = null;
		 Vector v = retrieve_REST_content(url);
		 if (v != null && v.size() > 0) {
			 hmap = parse_REST_content(v, RESTClient.ID, RESTClient.VERSION_NUMBER);
			 if (hmap != null) {
			 	hmap = inverseHashMap(hmap);
			 }
	     }
		 return hmap;
  }

  public static void dumpVector(Vector v) {
	  if (v == null) return;
	  for (int i=0; i<v.size(); i++) {
		  int k = i+1;
		  String t = (String) v.elementAt(i);
		  System.out.println("(" + k + ") " + t);
	  }
  }




	  public static void main(String[] args)
	  {
		 String API_KEY = "?";//"YourAPIKey";  //Login to BioPortal (http://bioportal.bioontology.org/login) to get your API key
         // get distinct virtual ontology ids:
		 String getLatestOntologiesUrl = "http://rest.bioontology.org/bioportal/ontologies?apikey=";
		 String http_url_0 = getLatestOntologiesUrl+API_KEY;
		 Vector v = new RESTClient().retrieve_REST_content(http_url_0);
		 for (int i=0; i<v.size(); i++) {
			 String t = (String) v.elementAt(i);
			 System.out.println(t);
		 }

		 String filename = "test_nci.out";
		 export_REST_content(filename, v);

		 Vector name_vec = new Vector();
		 name_vec.add("abbreviation");
		 name_vec.add("displayLabel");
         HashMap virtual_id_2_name_hmap = parse_REST_content(v, RESTClient.ONTOLOGY_ID, name_vec);
         dumpHashMap(virtual_id_2_name_hmap);

		 dumpHashMap(inverseHashMap(virtual_id_2_name_hmap));
         System.out.println("\n");


		 HashMap hmap = parse_REST_content(v, RESTClient.ID, RESTClient.ONTOLOGY_ID);
		 System.out.println("\n\n" + RESTClient.ID + " to " + RESTClient.ONTOLOGY_ID + " mapping:");
		 dumpHashMap(hmap);

		 Vector virtual_ids = getValues(hmap);
		 for (int i=0; i<virtual_ids.size(); i++) {
			 String t = (String) virtual_ids.elementAt(i);
			 int k = i+1;
			 System.out.println("(" + k + ") " + t);
		 }

         String ontologyId = "1032";
		 getLatestOntologiesUrl = "http://rest.bioontology.org/bioportal/ontologies/versions/" + ontologyId + "?apikey=";
		 http_url_0 = getLatestOntologiesUrl+API_KEY;
		 v = new RESTClient().retrieve_REST_content(http_url_0);
		 hmap = parse_REST_content(v, RESTClient.ID, RESTClient.ONTOLOGY_ID);
		 dumpHashMap(hmap);

		 dumpHashMap(inverseHashMap(hmap));
		 getLatestOntologiesUrl = "http://rest.bioontology.org/bioportal/ontologies/versions/1032?apikey=";

		 //Call Search REST URL and Parse results
		 http_url_0 = getLatestOntologiesUrl+API_KEY;
		 v = new RESTClient().retrieve_REST_content(http_url_0);
		 for (int i=0; i<v.size(); i++) {
			 String t = (String) v.elementAt(i);
			 System.out.println(t);
		 }

		 filename = "ncit_versions.out";
		 export_REST_content(filename, v);
		 hmap = parse_REST_content(v, RESTClient.ID, RESTClient.VERSION_NUMBER);
		 dumpHashMap(inverseHashMap(hmap));
         System.out.println("\n\n");
         HashMap map1 = new RESTClient().getVirtualId2NamesMap(API_KEY);
         dumpHashMap(map1);

         Vector virtualId_vec = new RESTClient().getKeys(map1, null);
         dumpVector(virtualId_vec);

         Vector ncit_names = getValues(map1, "1032");
         dumpVector(ncit_names);
	  }
}

