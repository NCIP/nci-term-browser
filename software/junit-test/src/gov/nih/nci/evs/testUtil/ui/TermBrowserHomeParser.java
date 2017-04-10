package gov.nih.nci.evs.testUtil.ui;


import gov.nih.nci.evs.browser.bean.*;
import gov.nih.nci.evs.browser.utils.*;
import gov.nih.nci.evs.testUtil.*;
import java.io.*;
import java.net.*;
import java.util.*;


/**
 * <!-- LICENSE_TEXT_START -->
 * Copyright 2008-2015 NGIT. This software was developed in conjunction
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
 * Modification history:
 *     Initial implementation kim.ong@ngc.com
 *
 */


public class TermBrowserHomeParser {

   public String getOutputFileName(String input_url) {
      int n = input_url.lastIndexOf("/");
      String outputfile = null;
      if (n != -1)
      {
      	  outputfile = input_url.substring(n+1, input_url.length()) + ".txt";
	  }
	  return outputfile;
   }

   public Vector run(String input_url) {
      Vector w = new Vector();
      URL u;
      InputStream is = null;
      DataInputStream dis;
      String s;
      try {
         u = new URL(input_url);
         is = u.openStream();         // throws an IOException
         dis = new DataInputStream(new BufferedInputStream(is));
         while ((s = dis.readLine()) != null) {
			 s = s.trim();
			 if (s.length() > 0) {
            	w.add(s);
			 }
         }
      } catch (MalformedURLException mue) {
         mue.printStackTrace();
      } catch (IOException ioe) {
         ioe.printStackTrace();
      } finally {
         try {
            is.close();
         } catch (IOException ioe) {
            ioe.printStackTrace();
         }
      }
      return w;
   }

   public Vector getTerminologyDisplayLabelsUnsorted(String url) {
	   Vector w = new Vector();
	   Vector v = run(url);
	   for (int i=0; i<v.size(); i++) {
		   String line = (String) v.elementAt(i);
		   if (line.indexOf("home.jsf") != -1 ) {
			   int n1 = line.indexOf(">");
			   int n2 = line.lastIndexOf("<");
			   line = line.substring(n1+1, n2);
			   line = line.replaceAll("&nbsp;", " ");
			   w.add(line);
	       }
		   if (line.indexOf("vocabulary.jsf?") != -1 ) {
			   String line2 = (String) v.elementAt(i+1);
			   line2 = line2.replaceAll("&nbsp;", " ");
			   w.add(line2);
		   }
		   if (line.indexOf("img src") != -1 && line.indexOf("NCI Metathesaurus:&nbsp;") != -1) {
			   int m1 = line.lastIndexOf("NCI Metathesaurus:&nbsp;");
			   line = line.substring(m1, line.length()-4);
			   line = line.replaceAll("&nbsp;", " ");
			   w.add(line);
		   }
	   }
	   return w;
    }

   public Vector getTerminologyDisplayLabels(String url) {
	   Vector w = getTerminologyDisplayLabelsUnsorted(url);
	   w = new SortUtils().quickSort(w);
	   return w;
    }

    public static void main(String [ ] args)
    {
	   String url = "http://nciterms.nci.nih.gov/ncitbrowser";
	   if (args.length == 1) {
		   url = args[0];
	   }
    }
}

