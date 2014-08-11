package gov.nih.nci.evs.browser.utils;

import java.io.*;
import java.net.*;
import org.apache.commons.io.FilenameUtils;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FTPDownload {

   public static void downloadText(String uri) {
      URL u;
      InputStream is = null;
      DataInputStream dis;
      String s;
      PrintWriter pw = null;
      int n = uri.lastIndexOf("/");
      String outputfile = null;
      if (n != -1) {
		  outputfile = uri.substring(n+1, uri.length());
	  }
      System.out.println(outputfile);
      if (outputfile == null) return;
      try {
         pw = new PrintWriter(outputfile, "UTF-8");
         u = new URL(uri);
         is = u.openStream();
         dis = new DataInputStream(new BufferedInputStream(is));
         while ((s = dis.readLine()) != null) {
            pw.println(s);
         }

      } catch (MalformedURLException mue) {
         mue.printStackTrace();
         System.exit(1);

      } catch (IOException ioe) {
         ioe.printStackTrace();
         System.exit(1);

      } finally {
         try {
            is.close();
            pw.close();
            System.out.println("Output file " + outputfile + " generated.");
         } catch (IOException ioe) {
            ioe.printStackTrace();
         }
      }
   }


   public static void downloadExcel(String uri) {
      URL u;
      InputStream is = null;
      int n = uri.lastIndexOf("/");
      String outputfile = null;
      if (n != -1) {
		  outputfile = uri.substring(n+1, uri.length());
	  }
      System.out.println(outputfile);
      if (outputfile == null) return;
      try {
          u = new URL(uri);
          is = u.openStream();
		  byte[] buffer = new byte[8 * 1024];
		  try {
			  OutputStream output = new FileOutputStream(outputfile);
			  try {
				  int bytesRead;
				  while ((bytesRead = is.read(buffer)) != -1) {
				      output.write(buffer, 0, bytesRead);
				  }
			  } finally {
				  output.close();
			  }
		  } finally {
			  is.close();
		  }
	  } catch (Exception ex) {
		  ex.printStackTrace();
	  } finally {
		  System.out.println("Output file " + outputfile + " generated.");
	  }
   }


   public static void downloadText(String uri, String outputfile) {
      URL u;
      InputStream is = null;
      DataInputStream dis;
      String s;
      PrintWriter pw = null;

      if (outputfile == null) return;
      try {
         pw = new PrintWriter(outputfile, "UTF-8");
         u = new URL(uri);
         is = u.openStream();
         dis = new DataInputStream(new BufferedInputStream(is));
         while ((s = dis.readLine()) != null) {
            pw.println(s);
         }

      } catch (MalformedURLException mue) {
         mue.printStackTrace();
         System.exit(1);

      } catch (IOException ioe) {
         ioe.printStackTrace();
         System.exit(1);

      } finally {
         try {
            is.close();
            pw.close();
            System.out.println("Output file " + outputfile + " generated.");
         } catch (IOException ioe) {
            ioe.printStackTrace();
         }
      }
   }


   public static void downloadExcel(String uri, String outputfile) {
      URL u;
      InputStream is = null;
      if (outputfile == null) return;
      try {
          u = new URL(uri);
          is = u.openStream();
		  byte[] buffer = new byte[8 * 1024];
		  try {
			  OutputStream output = new FileOutputStream(outputfile);
			  try {
				  int bytesRead;
				  while ((bytesRead = is.read(buffer)) != -1) {
				      output.write(buffer, 0, bytesRead);
				  }
			  } finally {
				  output.close();
			  }
		  } finally {
			  is.close();
		  }
	  } catch (Exception ex) {
		  ex.printStackTrace();
	  } finally {
		  System.out.println("Output file " + outputfile + " generated.");
	  }
   }


   public static void download(String uri) {
	  String ext = FilenameUtils.getExtension(uri);
	  if (ext.compareTo("txt") == 0) {
		  downloadText(uri);
	  } else {
		  downloadExcel(uri);
	  }

   }

   public static void download(String uri, String outputfile) {
	  String ext = FilenameUtils.getExtension(uri);
	  if (ext.compareTo("txt") == 0) {
		  downloadText(uri, outputfile);
	  } else {
		  downloadExcel(uri, outputfile);
	  }

   }


   public static void main (String[] args) {
	  String uri = "http://evs.nci.nih.gov/ftp1/FDA/CDRH/FDA-CDRH_NCIt_Subsets.txt";
	  uri = "ftp://ftp1.nci.nih.gov/pub/cacore/EVS/CDISC/SDTM/SDTM Terminology.xls";
	  if (args.length == 1) {
		  uri = args[0];
	  }
	  download(uri);
   }
}
