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

public class DisplayItem {

	private String propertyName;
	private String itemLabel;
	private String url;
	private String hyperlinkText;
	private boolean isExternalCode;

	public DisplayItem(){

	}

	public DisplayItem(String propertyName, String itemLabel, String url, String hyperlinkText) {
		this.propertyName = propertyName;
		this.itemLabel = itemLabel;
		this.url  = url;
		this.hyperlinkText = hyperlinkText;
		this.isExternalCode = false;
	}


	public DisplayItem(String propertyName, String itemLabel, String url, String hyperlinkText, boolean isExternalCode) {
		this.propertyName = propertyName;
		this.itemLabel = itemLabel;
		this.url  = url;
		this.hyperlinkText = hyperlinkText;
		this.isExternalCode = isExternalCode;
	}

	public String getPropertyName() {
		return this.propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getItemLabel() {
		return this.itemLabel;
	}

	public void setItemLabel(String itemLabel) {
		this.itemLabel = itemLabel;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getHyperlinkText() {
		return this.hyperlinkText;
	}

	public void setHyperlinkText(String hyperlinkText) {
		this.hyperlinkText = hyperlinkText;
	}

	public boolean getIsExternalCode() {
		return this.isExternalCode;
	}

	public void setIsExternalCode(String isExternalCode) {
		this.hyperlinkText = isExternalCode;
	}


	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("DisplayItem ");
		sb.append("\n");
		sb.append("\tpropertyName: " + getPropertyName());
		sb.append("\n");
		sb.append("\titemLabel: " + getItemLabel());
		sb.append("\n");
		sb.append("\turl: " + getUrl());
		sb.append("\n");
		sb.append("\thyperlinkText: " + getHyperlinkText());
		sb.append("\n");
		sb.append("\tisExternalCode: " + getIsExternalCode());
		sb.append("\n");

		return sb.toString();
	}
}
