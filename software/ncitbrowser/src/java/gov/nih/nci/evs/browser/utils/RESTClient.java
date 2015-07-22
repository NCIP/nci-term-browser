package gov.nih.nci.evs.browser.utils;

import java.net.*;
import java.io.*;
import java.util.*;

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

/**
 * @author EVS Team
 * @version 1.0
 *
 *      Modification history Initial implementation kim.ong@ngc.com
 *
 */

public class RESTClient{
    private static final String NCBO_BASE_URL = "http://data.bioontology.org/";
    private static final String TAG_ONTOLOGY = "ontology";
    private static final String TAG_ACRONYM = "acronym";
    private static final String TAG_NAME = "name";

    public static Vector retrieve_REST_content(String apikey) {
		return retrieve_REST_content(NCBO_BASE_URL, apikey);
	}

    public static Vector retrieve_REST_content(String uri, String apikey) {
		uri = uri + "ontologies?format=xml&apikey=" + apikey;
        Vector v = new Vector();
        URL url;
        HttpURLConnection connection = null;
        try {
           url = new URL(uri);
           connection = (HttpURLConnection) url.openConnection();
           connection.setRequestProperty("Accept", "text/xml");
           if (connection.getResponseCode() != 200) {
               throw new RuntimeException("Failed : The HTTP error code is : "
                       + connection.getResponseCode());
           }
           BufferedReader br = new BufferedReader(new InputStreamReader(
                   (connection.getInputStream())));
           String output;
           while ((output = br.readLine()) != null) {
               v.add(output);
           }
        } catch (MalformedURLException e) {
           e.printStackTrace();
        } catch (ProtocolException e) {
           e.printStackTrace();
        } catch (IOException e) {
           e.printStackTrace();
        } finally {
           if(connection != null){
               connection.disconnect();
           }
        }
        return v;
    }

    public static String extractValue(String line, String tag) {
		String open_tag = "<" + tag + ">";
		String close_tag = "</" + tag + ">";
		return extractValue(line, open_tag, close_tag);
	}

    public static String extractValue(String line, String open_tag, String close_tag)
    {
		if (line == null) return null;
		line = line.trim();
		int n = line.indexOf(open_tag);
		if (n == -1) return null;
		line = line.substring(n + open_tag.length(), line.length());
		n = line.indexOf(close_tag);

		if (n == -1) return null;
		line = line.substring(0, n);
		line = line.trim();
		return line;
	}

	public static HashMap getBioportalAcronym2NameHashMap(String apikey) {
		Vector v = retrieve_REST_content(apikey);
		return getBioportalAcronym2NameHashMap(v);
	}


	public static HashMap getBioportalAcronym2NameHashMap(Vector v) {
		if (v == null) return null;
		HashMap hmap = new HashMap();
        String acronym = null;
        String name = null;

		for (int i=0; i<v.size(); i++) {
			 String t = (String) v.elementAt(i);
			 String s = extractValue(t, TAG_ACRONYM);
			 if (s != null) {
				 acronym = s;
			 }
			 name = extractValue(t, TAG_NAME);
			 if (name != null) {
				 if (hmap.containsKey(acronym)) {
					 System.out.println("WARNING: Acronym already exists -- " + acronym);
				 } else {
				 	 hmap.put(acronym, name);
			     }
			 }
		}
		return hmap;
	}

	public static void dumpHashMap(HashMap map) {
		if (map == null) return;
        Set entrys = map.entrySet() ;
        Vector v = new Vector();
        Iterator iter = entrys.iterator();
        while(iter.hasNext()) {
            Map.Entry me = (Map.Entry)iter.next();
            v.add(me.getKey() + "|" + me.getValue());
        }
        v = SortUtils.quickSort(v);
        for (int i=0; i<v.size(); i++) {
			String t = (String) v.elementAt(i);
			int j = i+1;
			System.out.println("(" + j + ") " + t);
		}
	}

	public static void main(String[] args)
	{
		 String API_KEY = "";
         Vector v = retrieve_REST_content(API_KEY);
         HashMap map = getBioportalAcronym2NameHashMap(v);
         dumpHashMap(map);
	}
}
