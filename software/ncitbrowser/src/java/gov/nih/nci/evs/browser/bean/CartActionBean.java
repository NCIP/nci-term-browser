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
	private String _backurl = null;

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

	/**
	 * Compute a back to url that is not the cart page
	 * @return
	 */
	public String getBackurl() {
        HttpServletRequest request =
            (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();		
        String targetPage = request.getHeader("Referer"); 
    	targetPage = (targetPage == null) ? request.getContextPath() : targetPage;		
        if (!targetPage.contains("cart.jsf")) _backurl = targetPage; 
        return _backurl;
	}	
	
	/**
	 * Return the concept collection 
	 * @return
	 */
	public Collection<Concept> getConcepts() {
		if (_cart == null) _init();
		return _cart.values();
	}
	
	/**
	 * Initialize the cart container
	 */
	private void _init() {
		if (_cart == null) _cart = new HashMap<String, Concept>();
	}
	
    /**
     * Add concept to the Cart
     * @return
     * @throws Exception
     */
    public void addToCart() throws Exception {    	    	
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
        
        if (_cart == null) _init();
        Concept item = new Concept();
        item.setCode(code); 
        item.setDictionary(dictionary);
        item.setName(name);
        
        if (!_cart.containsKey(code))
        	_cart.put(code,item);        
    }
   
    /**
     * Remove concept(s) from the Cart
     * @return
     * @throws Exception
     */
    public void removeFromCart() {  
        if (_cart != null && _cart.size() > 0) {
            for (Iterator<Concept> i = getConcepts().iterator(); i.hasNext();) {
            	Concept item = (Concept)i.next();	
            	if (item.getSelected()) {
            		if (_cart.containsKey(item.code))
            			i.remove();
            	}	
            }        	
        }    	
    }
    
    /**
     * Subclass to hold contents of the cart
     * @author garciawa2
     */
    public class Concept {
    	private String code = null;
    	private String dictionary = null;
    	private String name = null;
    	private boolean selected = false;
    	
    	// Getters & setters
    	
    	public String getCode() {
    		return this.code;
    	}

    	public void setCode(String code) {
    		this.code = code;
    	}    	
    	
    	public String getDictionary() {
    		return this.dictionary;
    	}
    	
    	public void setDictionary(String dictionary) {
    		this.dictionary = dictionary;
    	}   
    	
    	public String getName() {
    		return this.name;
    	}

    	public void setName(String name) {
    		this.name = name;
    	}  

    	public void setSelected(Boolean value) {
    		this.selected = value;
    	}
    	
    	public Boolean getSelected() {
    		return this.selected;
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
                sb.append("\t   Checked = " + item.selected + "\n");
            }        	
        } else {
        	sb.append("Cart is empty.");       	
        }

        return sb.toString();
    }    
    
} // End of CartActionBean
