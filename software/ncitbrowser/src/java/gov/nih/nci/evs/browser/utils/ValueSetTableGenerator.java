package gov.nih.nci.evs.browser.utils;

import  gov.nih.nci.evs.browser.properties.*;
import  gov.nih.nci.evs.browser.bean.*;

import java.io.*;
import java.util.*;


public class ValueSetTableGenerator {

	 public static String URL = "http://nciterms.nci.nih.gov";
	 public static String SCHEME = "NCI Thesaurus";

     public ValueSetTableGenerator() {

	 }

     private boolean fileExists(String filename) {
		File f = new File(filename);
		if(f.exists() && !f.isDirectory()) {
			return true;
		}
		return false;
	 }

     public void generate(ValueSetConfig vsc) {
		  if (vsc == null) return;
		  PrintWriter out = null;

		  boolean test = false;

          String filename = vsc.getReportURI();
          String excelfile = filename;
          String htmlfile = filename;

          int n = vsc.getReportURI().lastIndexOf("/");
          if (n != -1) {
			  excelfile = filename.substring(n+1, filename.length());
		  }

          // To be modified: always download

		  if (!fileExists(excelfile)) {
			  FTPDownload.download(vsc.getReportURI());
		  }

          n = excelfile.lastIndexOf(".");
          if (n != -1) {
			  htmlfile = excelfile.substring(0, n) + ".html";
		  }
		  if (test) return;

          try {
			  out = new PrintWriter(htmlfile, "UTF-8");

			  out.println("<html>");
			  out.println("<head>");
			  out.println("<style>");
			  out.println("table {");
			  out.println("  table-layout: fixed;");
			  out.println("  width: 100%;");
			  out.println("  *margin-left: -100px;/*ie7*/");
			  out.println("}");
			  out.println("td, th {");
			  out.println("  vertical-align: top;");
			  out.println("  border-top: 1px solid #ccc;");
			  out.println("  padding:10px;");
			  out.println("  width:100px;");
			  out.println("}");
			  out.println("th {");
			  out.println("  position:absolute;");
			  out.println("  *position: relative; /*ie7*/");
			  out.println("  left:0;");
			  out.println("  width:100px;");
			  out.println("}");
			  out.println(".outer {position:relative}");
			  out.println(".inner {");
			  out.println("  overflow-x:scroll;");
			  out.println("  overflow-y:scroll;");
			  out.println("  width:900px;");
			  //out.println("  margin-left:100px;");
			  out.println("  margin-left:5px;");
			  out.println("  height:500px;");
			  out.println("}");
			  out.println("</style>");
			  out.println("</head>");
			  out.println("<body>");
			  out.println("");
			  out.println("<h3>" + vsc.getName() + "</h3>");
			  out.println("<hr></hr>");
			  out.println("");
			  out.println("<div class=\"outer\">");
			  out.println("  <div class=\"inner\">");
			  out.println("  ");
			  out.println("<!-- Insert table here -->");


			  Vector u = ValueSetDefinitionConfig.interpretExtractionRule(vsc.getExtractionRule());
			  int col = -1;
			  int sheet = -1;
			  String code = null;

			  if (u == null || (u.size() != 2 && u.size() != 3)) {
				  out.println("Data not found.");
			  } else {
				  sheet = Integer.parseInt((String) u.elementAt(0)) - 1;
				  if (u.size() == 2) {
					  code = (String) u.elementAt(1);

				  } else if (u.size() == 3) {
					  col = Integer.parseInt((String) u.elementAt(1)) - 1;
					  code = (String) u.elementAt(2);
				  }

				  int startIndex = ExcelUtil.getHSSFStartRow(excelfile, sheet, col, code);
				  //int endIndex = ExcelUtil.getHSSFEndRow(excelfile, sheet, col, code);

				  try {
					  String url = "http://nciterms.nci.nih.gov/ConceptReport.jsp?dictionary=NCI%20Thesaurus";
					  //HSSF2HTML generator = new HSSF2HTML(excelfile, sheet, startIndex, endIndex, url);
					  HSSFtoHTML generator = new HSSFtoHTML(excelfile, sheet, startIndex, col, code, url);
					  String table_content = generator.getHTML();
					  out.print(table_content);
				  } catch (Exception ex) {
					  ex.printStackTrace();
				  }
			  }

			  out.println("  ");
			  out.println(" </div>");
			  out.println("</div>");
			  out.println("");
			  out.println("</body>");
			  out.println("</html>");
			  out.println("  ");
		  } catch (Exception ex) {
			  ex.printStackTrace();
		  } finally {
			  try {
			      out.close();
			  } catch (Exception e) {
				  e.printStackTrace();
			  }
		  }
     }

     public static boolean isNCItCode(String str) {
		 if (str == null) return false;
		 String first_ch = str.substring(0, 1);
		 if (first_ch.compareTo("C") != 0) return false;
		 String substr = str.substring(1, str.length());
		 for (int i=0; i<substr.length(); i++) {
			 char c = substr.charAt(i);
			 if (!Character.isDigit(c)) return false;
		 }
         return true;
	 }

	 public static String getHyperLink(String url, String code) {
         StringBuffer buf = new StringBuffer();
         buf.append("<a href=\"");
         buf.append(url);
		 buf.append("&code=" + code);
		 buf.append("\"");
		 buf.append("<a>");
		 return buf.toString();
	 }


	 public static String getHyperLink(String url, String scheme, String version, String code) {
         StringBuffer buf = new StringBuffer();
         buf.append("<a href=\"");
         buf.append(url);
         buf.append("/ConceptReport.jsp?dictionary=");
         scheme = scheme.replaceAll(" ", "%20");
         buf.append(scheme);
         if (version != null) {
			 buf.append("&version=" + version);
		 }
		 buf.append("&code=" + code);
		 buf.append("\"");
		 buf.append("<a>");
		 return buf.toString();
	 }


	 public static String testHyperlink(String code) {
		 if (code == null) return code;
		 if (!isNCItCode(code)) {
			 return code;
		 }
		 return getHyperLink(URL, SCHEME, null, code);
	 }


	 public static void main(String [ ] args)
	 {
		  String uri = "http://ncit:C89972";
		  uri = "http://ncit:C78418";
		  uri = "http://ncit:C90259";
		  uri = "http://ncit:C54452";
		  uri = "http://ncit:C54456";
		  uri = "http://ncit:C65047";
		  uri = "http://ncit:C81223";
		  uri = "http://ncit:C54577";

	      ValueSetConfig vsc = ValueSetDefinitionConfig.getValueSetConfig(uri);
	      new ValueSetTableGenerator().generate(vsc);

	 }
}

