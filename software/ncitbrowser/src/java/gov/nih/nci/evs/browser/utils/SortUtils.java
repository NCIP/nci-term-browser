/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.Collections;

import java.util.Comparator;

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


public class SortUtils {

    public static int SORT_BY_NAME = 1;
    public static int SORT_BY_CODE = 2;


	 /**
	 * Performs quick sort of a List by name.
	 *
	 * @param list an instance of List
	 */
    public static void quickSort(List list)
    {
	    quickSort(list, SORT_BY_NAME);
    }

	 /**
	 * Performs quick sort of a List by a specified sort option.
	 *
	 * @param list an instance of List
	 * @param sort_option, an integer; 1, if sort by name; 2: if sort by code
	 */
    public static void quickSort(List list, int sort_option)
    {
       if (list == null) return;
       if (list.size() <= 1) return;
       try {
		   Collections.sort(list, new SortComparator(sort_option));
       } catch (Exception ex) {
		   ex.printStackTrace();
	   }
    }

	 /**
	 * Performs quick sort of a Vector by a specified sort option.
	 *
	 * @param v an instance of Vector
	 * @param sort_option, an integer; 1, if sort by name; 2: if sort by code
	 */


   public static Vector quickSort(Vector v, int sort_option)
   {
       if (v == null) return v;
       if (v.size() <= 1) return v;
       try {
		   Collections.sort((List) v, new SortComparator(sort_option));
		   return v;
       } catch (Exception ex) {
		   ex.printStackTrace();
	   }
       return null;
   }



	 /**
	 * Performs quick sort of a Vector by name.
	 *
	 * @param v an instance of Vector
	 */

   public static Vector quickSort(Vector v)
   {
	   return quickSort(v, SORT_BY_NAME);
   }

   @SuppressWarnings("unchecked")
   public static Enumeration<?> sort(Enumeration<?> enumeration) {
       if (enumeration == null)
           return enumeration;
       
       List keyList = Collections.list(enumeration);
       Collections.sort(keyList);
       enumeration = Collections.enumeration(keyList);
       return enumeration;
   }
}

