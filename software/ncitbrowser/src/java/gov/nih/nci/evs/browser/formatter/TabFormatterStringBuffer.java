/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.formatter;

/**
 * 
 */

/**
 * @author EVS Team (David Yee)
 * @version 1.0
 */

public class TabFormatterStringBuffer extends TabFormatterBase {
    private StringBuffer _buffer = new StringBuffer();
    
    public TabFormatterStringBuffer(String filename) {
        super(filename);
    }

    public String write(String text) throws Exception {
        text = super.write(text);
        _buffer.append(text);
        return text;
    }
    
    public String toString() {
        return _buffer.toString();
    }
}
