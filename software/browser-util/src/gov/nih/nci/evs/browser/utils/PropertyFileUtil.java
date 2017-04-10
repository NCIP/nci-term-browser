package gov.nih.nci.evs.browser.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.*;

import org.LexGrid.LexBIG.DataModel.Collections.NameAndValueList;
import org.LexGrid.LexBIG.DataModel.Core.NameAndValue;

public class PropertyFileUtil {

  public static void main(String[] args) {

	Properties prop = new Properties();
	InputStream input = null;
	String filename = "lbconfig.props";

	if (args.length>0) {
		filename = args[0];
	}
	NameAndValueList name_value_list = parse(filename);
  }


  public static NameAndValueList parse(String filename) {
	Properties prop = new Properties();
	InputStream input = null;

	NameAndValueList nvl = new NameAndValueList();

	try {
		input = new FileInputStream(filename);

		// load a properties file
		prop.load(input);
		Vector u = new Vector();

		Set<String> keys = prop.stringPropertyNames();
		Iterator it = keys.iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			String value = prop.getProperty(key);
			//System.out.println(key + ": " + value);
			u.add(key + ": " + value);

			NameAndValue nv = new NameAndValue();
			nv.setName(key);
			nv.setContent(value);
			nvl.addNameAndValue(nv);



		}
		u = new SortUtils().quickSort(u);
		for (int k=0; k<u.size(); k++) {
			String t = (String) u.elementAt(k);
			System.out.println(t);
		}


	} catch (IOException ex) {
		ex.printStackTrace();
	} finally {
		if (input != null) {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	return nvl;

  }
}
