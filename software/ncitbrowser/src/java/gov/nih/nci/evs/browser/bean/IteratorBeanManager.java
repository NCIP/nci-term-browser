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
        String key = matchText.trim();
        key = key + "|" + searchTarget + "|" + matchAlgorithm;
        for (int i = 0; i < schemes.size(); i++) {
            String scheme = (String) schemes.elementAt(i);
            key = key + "|" + scheme;
        }
        key = key + "|" + maxReturn_str;
        int randomNumber = _random.nextInt();
        String randomNumber_str = Integer.toString(randomNumber);
        key = key + "|" + randomNumber_str;
        return key;
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