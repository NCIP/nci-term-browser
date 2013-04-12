/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.properties;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import gov.nih.nci.evs.browser.bean.DisplayItem;
import gov.nih.nci.evs.browser.bean.MetadataElement;
import gov.nih.nci.evs.browser.bean.DefSourceMapping;
import gov.nih.nci.evs.browser.bean.SecurityTokenHolder;


/**
  * 
  */

/**
  * @author EVS Team
  * @version 1.0
  *
  * Modification history
  *     Initial implementation kim.ong@ngc.com
  *
 */

public class PropertyFileParser {

	List displayItemList;
	HashMap configurableItemMap;

	List metadataElementList;
	List defSourceMappingList;
	HashMap defSourceMappingHashMap;

	List securityTokenList;
	HashMap securityTokenHashMap;


	String xmlfile;

	Document dom;

	public PropertyFileParser(){
		displayItemList = new ArrayList();
		metadataElementList = new ArrayList();
		defSourceMappingList = new ArrayList();
		defSourceMappingHashMap = new HashMap();
		securityTokenList = new ArrayList();
		securityTokenHashMap = new HashMap();
		configurableItemMap = new HashMap();
	}

	public PropertyFileParser(String xmlfile){
		displayItemList = new ArrayList();
		metadataElementList = new ArrayList();
		defSourceMappingList = new ArrayList();
		defSourceMappingHashMap = new HashMap();
		securityTokenList = new ArrayList();
		securityTokenHashMap = new HashMap();
		configurableItemMap = new HashMap();
		this.xmlfile = xmlfile;
	}

	public void run() {
		parseXmlFile(this.xmlfile);
		parseDocument();
		//printData();
	}

	public List getDisplayItemList() {
		return this.displayItemList;
	}

	public List getMetadataElementList() {
		return this.metadataElementList;
	}

	public List getDefSourceMappingList() {
		return this.defSourceMappingList;
	}

	public HashMap getDefSourceMappingHashMap() {
		return defSourceMappingHashMap;
	}

	public List getSecurityTokenList() {
		return this.securityTokenList;
	}

	public HashMap getSecurityTokenHashMap() {
		return securityTokenHashMap;
	}

	public HashMap getConfigurableItemMap() {
		return this.configurableItemMap;
	}

	private void parseXmlFile(String xmlfile){
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
		    DocumentBuilder db = dbf.newDocumentBuilder();
			dom = db.parse(xmlfile);
		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}


	private void parseDocument(){
		Element docEle = dom.getDocumentElement();
		NodeList list1 = docEle.getElementsByTagName("DisplayItem");
		if(list1 != null && list1.getLength() > 0) {
			for(int i = 0 ; i < list1.getLength();i++) {
				Element el = (Element) list1.item(i);
				DisplayItem e = getDisplayItem(el);
				displayItemList.add(e);
			}
		}

		NodeList list2 = docEle.getElementsByTagName("ConfigurableItem");
		if(list2 != null && list2.getLength() > 0) {
			for(int i = 0 ; i < list2.getLength();i++) {
				Element el = (Element) list2.item(i);
				getConfigurableItem(el);
			}
		}

		NodeList list3 = docEle.getElementsByTagName("MetadataElement");
		if(list3 != null && list3.getLength() > 0) {
			for(int i = 0 ; i < list3.getLength();i++) {
				Element el = (Element) list3.item(i);
				MetadataElement e = getMetadataElement(el);
				metadataElementList.add(e);
			}
		}

		NodeList list4 = docEle.getElementsByTagName("DefSourceMapping");
		if(list4 != null && list4.getLength() > 0) {
			for(int i = 0 ; i < list4.getLength();i++) {
				Element el = (Element) list4.item(i);
				DefSourceMapping e = getDefSourceMapping(el);
				defSourceMappingList.add(e);
				defSourceMappingHashMap.put(e.getName(), e.getValue());
			}
		}

		NodeList list5 = docEle.getElementsByTagName("SecurityTokenHolder");
		if(list5 != null && list5.getLength() > 0) {
			for(int i = 0 ; i < list5.getLength();i++) {
				Element el = (Element) list5.item(i);
				SecurityTokenHolder e = getSecurityTokenHolder(el);

				if (e.getValue().indexOf("token") == -1) {
					securityTokenList.add(e);
					securityTokenHashMap.put(e.getName(), e.getValue());
			    }
			}
		}
	}


	private DisplayItem getDisplayItem(Element displayItemElement) {
	    String propertyName = getTextValue(displayItemElement,"propertyName");
		String itemLabel = getTextValue(displayItemElement,"itemLabel");
		String url = getTextValue(displayItemElement,"url");
		String hyperlinkText = getTextValue(displayItemElement,"hyperlinkText");
		boolean isExternalCode = getBooleanValue(displayItemElement,"isExternalCode");

		if (url.compareTo("null") == 0)
		{
			url = null;
		}
		if (hyperlinkText.compareTo("null") == 0)
		{
			hyperlinkText = null;
		}

		DisplayItem item = new DisplayItem(propertyName,itemLabel,url,hyperlinkText, isExternalCode);
		return item;
	}


	private MetadataElement getMetadataElement(Element metadataElement) {
	    String name = getTextValue(metadataElement,"name");
		MetadataElement item = new MetadataElement(name);
		return item;
	}

	private DefSourceMapping getDefSourceMapping(Element defSourceMapping) {
	    String name = getTextValue(defSourceMapping,"name");
	    String value = getTextValue(defSourceMapping,"value");
		DefSourceMapping item = new DefSourceMapping(name, value);
		return item;
	}

	private SecurityTokenHolder getSecurityTokenHolder(Element securityTokenHolder) {
	    String name = getTextValue(securityTokenHolder,"name");
	    String value = getTextValue(securityTokenHolder,"value");
		SecurityTokenHolder item = new SecurityTokenHolder(name, value);
		return item;
	}

	private void getConfigurableItem(Element displayItemElement) {
	    String key = getTextValue(displayItemElement,"key");
		String value = getTextValue(displayItemElement,"value");
		configurableItemMap.put(key, value);
	}


	private String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if(nl != null && nl.getLength() > 0) {
			Element el = (Element)nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}
		return textVal = textVal.trim();
	}

	private boolean getBooleanValue(Element ele, String tagName) {
		String textVal = getTextValue(ele, tagName);
		if (textVal.compareToIgnoreCase("true") == 0) return true;
		return false;
	}


	private void printData(){
		Iterator it = displayItemList.iterator();
		while(it.hasNext()) {
			System.out.println(it.next().toString());
		}
	}


	public static void main(String[] args){
		PropertyFileParser parser = new PropertyFileParser();
		parser.run();
	}

}
