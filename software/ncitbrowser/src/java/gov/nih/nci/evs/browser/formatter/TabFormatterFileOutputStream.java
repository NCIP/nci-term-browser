/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.formatter;

import java.io.*;

/**
 * 
 */

/**
 * @author EVS Team (David Yee)
 * @version 1.0
 */

public class TabFormatterFileOutputStream extends TabFormatterBase 
{
	private FileOutputStream _out = null;

	public TabFormatterFileOutputStream(String filename) throws Exception {
		super(filename);
		_out = new FileOutputStream(filename);
	}

	public void close() throws Exception {
		_out.close();
	}

    public String write(String text) throws Exception {
        text = super.write(text);
        _out.write(text.getBytes());
        return text;
    }
}
