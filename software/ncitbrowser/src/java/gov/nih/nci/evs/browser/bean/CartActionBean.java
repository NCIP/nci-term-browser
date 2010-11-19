package gov.nih.nci.evs.browser.bean;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.LexGrid.concepts.Entity;

/**
 * Action bean for cart operations
 * 
 * @author garciawa2
 */
public class CartActionBean {

	// Local class variables
	
	private String _codename = null;
	private HashMap<String, Concept> _cart = null;

	// Getters & Setters

	/**
	 * Set name of parameter to use to acquire the code parameter
	 * 
	 * (This is a temporary fix to be used only until NCIt code is cleaned up and
	 *  made to use JSF MVC methodology.)
	 *  
	 * @param codename
	 */
	public void setCodename(String codename) {
		this._codename = codename;
	}
	
	/**
	 * Return number of items in cart
	 * @return
	 */
	public int getCount() {
		if (_cart == null) return 0;
		return _cart.size();
	}
	
	public Collection<Concept> getConcepts() {
		return _cart.values();
	}
	
    /**
     * Add concept to the Cart
     * @return
     * @throws Exception
     */
    public String addToCart() throws Exception {    	    	

    	String code = null;
    	String dictionary = null;  
    	String name = null;
    	
    	// Get concept information from the Entity item passed in
        HttpServletRequest request =
            (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();        
        Entity curr_concept = null;
        curr_concept = (Entity) request.getSession().getAttribute(_codename);
        code = curr_concept.getEntityCode();        
        dictionary = curr_concept.getEntityCodeNamespace();
        name = curr_concept.getEntityDescription().getContent();
        
        // Add concept to cart
        
        if (_cart == null) _cart = new HashMap<String, Concept>();
        Concept item = new Concept();
        item.setCode(code); 
        item.setDictionary(dictionary);
        item.setName(name);
        
        if (!_cart.containsKey(code))
        	_cart.put(code,item);
        
        System.out.println(toString());		// Debug

        return null;
    }
   
    /**
     * Subclass to hold contents of the cart
     * @author garciawa2
     */
    public class Concept {
    	private String code = null;
    	private String dictionary = null;
    	private String name = null;
    	
    	// Getters & setters
    	
    	public String getCode() {
    		return code;
    	}

    	public void setCode(String code) {
    		this.code = code;
    	}    	
    	
    	public String getDictionary() {
    		return dictionary;
    	}
    	
    	public void setDictionary(String dictionary) {
    		this.dictionary = dictionary;
    	}   
    	
    	public String getName() {
    		return name;
    	}

    	public void setName(String name) {
    		this.name = name;
    	}     	
    } // End of Concept

    //**
    //* Utility methods
    //**
    
    /**
     * Dump containts of cart object 
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Listing cart contents...\n");
        
        if (_cart != null && _cart.size() > 0) {
        	sb.append("\tCart:\n");
            for (Iterator<Concept> i = getConcepts().iterator(); i.hasNext();) {
            	Concept item = (Concept)i.next();
                sb.append("\t      Code = " + item.code + "\n");
                sb.append("\tDictionary = " + item.dictionary + "\n");   
                sb.append("\t      Name = " + item.name + "\n");
            }        	
        } else {
        	sb.append("Cart is empty.n");       	
        }

        return sb.toString();
    }    
    
} // End of CartActionBean
