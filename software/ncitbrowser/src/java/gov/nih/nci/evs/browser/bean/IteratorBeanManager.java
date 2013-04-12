/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.bean;

import java.util.*;

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

public class IteratorBeanManager extends Object {
    private HashMap _iteratorBeanHashMap = null;
    private Random _random = null;

    public IteratorBeanManager() {
        _iteratorBeanHashMap = new HashMap();
        _random = new Random();
    }



    public String createIteratorKey(Vector schemes, String matchText,
        String searchTarget, String matchAlgorithm, int maxReturn) {
        String maxReturn_str = Integer.toString(maxReturn);
        StringBuffer buf = new StringBuffer();
        buf.append(matchText.trim());
        buf.append("|" + searchTarget + "|" + matchAlgorithm);
        for (int i = 0; i < schemes.size(); i++) {
            String scheme = (String) schemes.elementAt(i);
            buf.append("|" + scheme);
        }
        buf.append("|" + maxReturn_str);
        int randomNumber = _random.nextInt();
        String randomNumber_str = Integer.toString(randomNumber);
        buf.append("|" + randomNumber_str);
        return buf.toString();
    }



    public String createIteratorKey(Vector schemes, Vector versions, String matchText,
        String searchTarget, String matchAlgorithm, int maxReturn) {
        //String maxReturn_str = Integer.toString(maxReturn);

        String key = "";
        matchText = matchText.trim();

// [#32150] NCI Thesaurus searches with a different criteria but same search string do not return proper results
//        int hashcode = matchText.hashCode()
//           + searchTarget.hashCode();

        int hashcode = matchText.hashCode()
           + searchTarget.hashCode() + matchAlgorithm.hashCode();


        for (int i = 0; i < schemes.size(); i++) {
            String scheme = (String) schemes.elementAt(i);
            String version = (String) versions.elementAt(i);
            hashcode = hashcode + scheme.hashCode();
            if (version != null) {
				hashcode = hashcode + version.hashCode();
			}
        }
        hashcode = hashcode + maxReturn;
        if (hashcode < 0) {
			hashcode = hashcode * (-1);
			key = key + "n";
		}
		return key + hashcode;
    }

    public static String createIteratorKey(String ontologiesToSearchOnStr, String matchText,
        String searchTarget, String matchAlgorithm) {

        String key = "";
        matchText = matchText.trim();
        int hashcode = matchText.hashCode()
            + ontologiesToSearchOnStr.hashCode()
            + searchTarget.hashCode()
            + matchAlgorithm.hashCode();

        if (hashcode < 0) {
			hashcode = hashcode * (-1);
			key = key + "n";
		}
		return key + hashcode;
    }



    public boolean addIteratorBean(IteratorBean bean) {
        String key = bean.getKey();
        if (_iteratorBeanHashMap.containsKey(key))
            return false;
        _iteratorBeanHashMap.put(key, bean);
        return true;
    }

    public IteratorBean getIteratorBean(String key) {
        if (key == null)
            return null;
        if (!containsIteratorBean(key))
            return null;
        return (IteratorBean) _iteratorBeanHashMap.get(key);
    }

    public boolean containsIteratorBean(String key) {
        if (key == null)
            return false;
        return _iteratorBeanHashMap.containsKey(key);
    }

    public Vector getKeys() {
        if (_iteratorBeanHashMap == null)
            return null;
        Vector key_vec = new Vector();
        Iterator iterator = _iteratorBeanHashMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            IteratorBean bean = (IteratorBean) _iteratorBeanHashMap.get(key);
            key_vec.add(bean.getKey());
        }
        return key_vec;
    }

}