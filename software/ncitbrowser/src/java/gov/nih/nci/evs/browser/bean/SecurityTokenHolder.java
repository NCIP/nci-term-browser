/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.bean;

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

public class SecurityTokenHolder {

	private String name;
	private String value;

	public SecurityTokenHolder(){

	}

	public SecurityTokenHolder(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return this.name;
	}

	public String getValue() {
		return this.value;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("SecurityTokenHolder ");
		sb.append("\n");
		sb.append("\tname: " + name);
		sb.append("\tvalue: " + value);
		sb.append("\n");
		sb.append("\n");

		return sb.toString();
	}
}
