/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.properties;

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;

import org.apache.log4j.*;
import org.w3c.dom.*;
import org.xml.sax.*;

import gov.nih.nci.evs.browser.bean.*;

/**
 * 
 */

/**
 * @author EVS Team
 * @version 1.0
 * 
 *          Modification history Initial implementation kim.ong@ngc.com
 * 
 */

public class PropertyFileParser {
    private static Logger _logger = Logger.getLogger(PropertyFileParser.class);
    private List _displayItemList;
    private HashMap _configurableItemMap;

    private List _metadataElementList;
    private List _defSourceMappingList;
    private HashMap _defSourceMappingHashMap;

    private List _securityTokenList;
    private HashMap _securityTokenHashMap;

    private String _xmlfile;

    private Document _dom;

    public PropertyFileParser() {
        _displayItemList = new ArrayList();
        _metadataElementList = new ArrayList();
        _defSourceMappingList = new ArrayList();
        _defSourceMappingHashMap = new HashMap();
        _securityTokenList = new ArrayList();
        _securityTokenHashMap = new HashMap();
        _configurableItemMap = new HashMap();
    }

    public PropertyFileParser(String xmlfile) {
        _displayItemList = new ArrayList();
        _metadataElementList = new ArrayList();
        _defSourceMappingList = new ArrayList();
        _defSourceMappingHashMap = new HashMap();
        _securityTokenList = new ArrayList();
        _securityTokenHashMap = new HashMap();
        _configurableItemMap = new HashMap();
        _xmlfile = xmlfile;
    }

    public void run() {
        parseXmlFile(_xmlfile);
        parseDocument();
        // printData();
    }

    public List getDisplayItemList() {
        return _displayItemList;
    }

    public List getMetadataElementList() {
        return _metadataElementList;
    }

    public List getDefSourceMappingList() {
        return _defSourceMappingList;
    }

    public HashMap getDefSourceMappingHashMap() {
        return _defSourceMappingHashMap;
    }

    public List getSecurityTokenList() {
        return _securityTokenList;
    }

    public HashMap getSecurityTokenHashMap() {
        return _securityTokenHashMap;
    }

    public HashMap getConfigurableItemMap() {
        return _configurableItemMap;
    }

    private void parseXmlFile(String xmlfile) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            _dom = db.parse(xmlfile);
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void parseDocument() {
        Element docEle = _dom.getDocumentElement();
        NodeList list1 = docEle.getElementsByTagName("DisplayItem");
        if (list1 != null && list1.getLength() > 0) {
            for (int i = 0; i < list1.getLength(); i++) {
                Element el = (Element) list1.item(i);
                DisplayItem e = getDisplayItem(el);
                _displayItemList.add(e);
            }
        }

        NodeList list2 = docEle.getElementsByTagName("ConfigurableItem");
        if (list2 != null && list2.getLength() > 0) {
            for (int i = 0; i < list2.getLength(); i++) {
                Element el = (Element) list2.item(i);
                getConfigurableItem(el);
            }
        }

        NodeList list3 = docEle.getElementsByTagName("MetadataElement");
        if (list3 != null && list3.getLength() > 0) {
            for (int i = 0; i < list3.getLength(); i++) {
                Element el = (Element) list3.item(i);
                MetadataElement e = getMetadataElement(el);
                _metadataElementList.add(e);
            }
        }

        NodeList list4 = docEle.getElementsByTagName("DefSourceMapping");
        if (list4 != null && list4.getLength() > 0) {
            for (int i = 0; i < list4.getLength(); i++) {
                Element el = (Element) list4.item(i);
                DefSourceMapping e = getDefSourceMapping(el);
                _defSourceMappingList.add(e);
                _defSourceMappingHashMap.put(e.getName(), e.getValue());
            }
        }

        NodeList list5 = docEle.getElementsByTagName("SecurityTokenHolder");
        if (list5 != null && list5.getLength() > 0) {
            for (int i = 0; i < list5.getLength(); i++) {
                Element el = (Element) list5.item(i);
                SecurityTokenHolder e = getSecurityTokenHolder(el);

                if (e.getValue().indexOf("token") == -1) {
                    _securityTokenList.add(e);
                    _securityTokenHashMap.put(e.getName(), e.getValue());
                }
            }
        }
    }

    private DisplayItem getDisplayItem(Element displayItemElement) {
        String propertyName = getTextValue(displayItemElement, "propertyName");
        String itemLabel = getTextValue(displayItemElement, "itemLabel");
        String url = getTextValue(displayItemElement, "url");
        String hyperlinkText =
            getTextValue(displayItemElement, "hyperlinkText");
        boolean isExternalCode =
            getBooleanValue(displayItemElement, "isExternalCode");

        if (url.compareTo("null") == 0) {
            url = null;
        }
        if (hyperlinkText.compareTo("null") == 0) {
            hyperlinkText = null;
        }

        DisplayItem item =
            new DisplayItem(propertyName, itemLabel, url, hyperlinkText,
                isExternalCode);
        return item;
    }

    private MetadataElement getMetadataElement(Element metadataElement) {
        String name = getTextValue(metadataElement, "name");
        MetadataElement item = new MetadataElement(name);
        return item;
    }

    private DefSourceMapping getDefSourceMapping(Element defSourceMapping) {
        String name = getTextValue(defSourceMapping, "name");
        String value = getTextValue(defSourceMapping, "value");
        DefSourceMapping item = new DefSourceMapping(name, value);
        return item;
    }

    private SecurityTokenHolder getSecurityTokenHolder(
        Element securityTokenHolder) {
        String name = getTextValue(securityTokenHolder, "name");
        String value = getTextValue(securityTokenHolder, "value");
        SecurityTokenHolder item = new SecurityTokenHolder(name, value);
        return item;
    }

    private void getConfigurableItem(Element displayItemElement) {
        String key = getTextValue(displayItemElement, "key");
        String value = getTextValue(displayItemElement, "value");
        _configurableItemMap.put(key, value);
    }

    private String getTextValue(Element ele, String tagName) {
        String textVal = null;
        NodeList nl = ele.getElementsByTagName(tagName);
        if (nl != null && nl.getLength() > 0) {
            Element el = (Element) nl.item(0);
            textVal = el.getFirstChild().getNodeValue();
        }
        return textVal = textVal.trim();
    }

    private boolean getBooleanValue(Element ele, String tagName) {
        String textVal = getTextValue(ele, tagName);
        if (textVal.compareToIgnoreCase("true") == 0)
            return true;
        return false;
    }

    private void printData() {
        Iterator it = _displayItemList.iterator();
        while (it.hasNext()) {
            _logger.debug(it.next().toString());
        }
    }

    public static void main(String[] args) {
        PropertyFileParser parser = new PropertyFileParser();
        parser.run();
    }

}
