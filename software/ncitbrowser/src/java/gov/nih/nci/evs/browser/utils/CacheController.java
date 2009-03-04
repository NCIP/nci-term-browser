package gov.nih.nci.evs.browser.utils;

/**
  * <!-- LICENSE_TEXT_START -->
* Copyright 2008,2009 NGIT. This software was developed in conjunction with the National Cancer Institute,
* and so to the extent government employees are co-authors, any rights in such works shall be subject to Title 17 of the United States Code, section 105.
* Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
* 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the disclaimer of Article 3, below. Redistributions
* in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other
* materials provided with the distribution.
* 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
* "This product includes software developed by NGIT and the National Cancer Institute."
* If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself,
* wherever such third-party acknowledgments normally appear.
* 3. The names "The National Cancer Institute", "NCI" and "NGIT" must not be used to endorse or promote products derived from this software.
* 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize
* the recipient to use any trademarks owned by either NCI or NGIT
* 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
* MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE NATIONAL CANCER INSTITUTE,
* NGIT, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
* PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
* WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
  * <!-- LICENSE_TEXT_END -->
  */

/**
  * @author EVS Team
  * @version 1.0
  *
  * Modification history
  *     Initial implementation kim.ong@ngc.com
  *
 */

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import java.util.Vector;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;


import gov.nih.nci.evs.browser.utils.TreeUtils.TreeItem;
import gov.nih.nci.evs.browser.properties.NCItBrowserProperties;


import java.util.List;
import java.util.Collection;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.Vector;
import java.util.Set;
import java.util.Collections;
import java.util.Map;


import org.json.*;

import org.LexGrid.LexBIG.DataModel.Collections.AssociatedConceptList;
import org.LexGrid.LexBIG.DataModel.Collections.AssociationList;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.AssociatedConcept;
import org.LexGrid.LexBIG.DataModel.Core.Association;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Extensions.Generic.LexBIGServiceConvenienceMethods;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.commonTypes.EntityDescription;
import org.LexGrid.concepts.Concept;
import org.LexGrid.LexBIG.DataModel.Collections.ConceptReferenceList;

import org.LexGrid.LexBIG.DataModel.Core.ConceptReference;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.concepts.Presentation;


public class CacheController
{
    private static CacheController instance = null;
    private static Cache cache = null;
    private static CacheManager cacheManager = null;

    public CacheController(String cacheName) {
		cacheManager = getCacheManager();
        if (!cacheManager.cacheExists(cacheName)) {
            cacheManager.addCache(cacheName);
        }

        System.out.println("cache added");
        this.cache = cacheManager.getCache(cacheName);
    }

    public static CacheController getInstance()
    {
        synchronized (CacheController.class)
        {
            if (instance == null)
            {
                instance = new CacheController("treeCache");
            }
        }
        return instance;
    }

    private static CacheManager getCacheManager() {
        if (cacheManager != null) return cacheManager;
		try {
			NCItBrowserProperties properties = NCItBrowserProperties.getInstance();
			String ehcache_xml_pathname = properties.getProperty(NCItBrowserProperties.EHCACHE_XML_PATHNAME);
			cacheManager = new CacheManager(ehcache_xml_pathname);
			return cacheManager;

	    } catch (Exception ex) {
            ex.printStackTrace();
		}
        return null;
	}

    public String[] getCacheNames() {
		return getCacheManager().getCacheNames();
    }

    public void clear() {
        cache.removeAll();
        //cache.flush();
    }

    public boolean containsKey(Object key) {
        return cache.isKeyInCache(key);
    }

    public boolean containsValue(Object value) {
        return cache.isValueInCache(value);
    }

    public boolean isEmpty() {
        return cache.getSize() > 0;
    }


    public HashMap getSubconcepts(String scheme, String version, String code)
    {
		return getSubconcepts(scheme, version, code, true);
	}


    public HashMap getSubconcepts(String scheme, String version, String code, boolean fromCache)
    {
		HashMap map = null;
		String key = scheme + "$" + version + "$" + code;
        if (fromCache)
        {
            Element element = cache.get(key);
            if (element != null)
            {
           	    map = (HashMap) element.getValue();
			}
        }
        if (map == null)
        {
			System.out.println("Not in cache -- calling getSubconcepts " );
            map = new TreeUtils().getSubconcepts(scheme, version, code);
            try {
				Element element = new Element(key, map);
	            cache.put(element);
			} catch (Exception ex) {

			}
        }
        else
        {
			System.out.println("Retrieved from cache." );
		}
        return  map;
    }

    public List getRootConcepts(String scheme, String version)
    {
		return getRootConcepts(scheme, version, true);
	}


    public List getRootConcepts(String scheme, String version, boolean fromCache)
    {
		List list = null;//new ArrayList();
		String key = scheme + "$" + version + "$root";
        if (fromCache)
        {
            Element element = cache.get(key);
            if (element != null) {

				System.out.println("getRootConcepts fromCache element != null returning list" );
	            list = (List) element.getValue();
			}
        }
        if (list == null)
        {
			System.out.println("Not in cache -- calling getHierarchyRoots " );
            try {
				list = new DataUtils().getHierarchyRoots(scheme, version, null);
				Element element = new Element(key, list);
	            cache.put(element);
			} catch (Exception ex) {
                ex.printStackTrace();
			}
        }
        else
        {
			System.out.println("Retrieved from cache." );
		}
        return list;
    }
}

