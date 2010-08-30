package gov.nih.nci.evs.browser.bean;

import java.io.*;
import java.util.*;

public class MappingIteratorBean
{

// Variable declaration
	private Iterator iterator;
	private int numberRemaining;
	private int istart;
	private int iend;
	private int size;
	private int pageNumber;
	private int numberPages;
	private List list;

// Default constructor
	public MappingIteratorBean() {
	}

// Constructor
	public MappingIteratorBean(
		Iterator iterator,
		int numberRemaining,
		int istart,
		int iend,
		int size,
		int pageNumber,
		int numberPages,
		List list) {

		this.iterator = iterator;
		this.numberRemaining = numberRemaining;
		this.istart = istart;
		this.iend = iend;
		this.size = size;
		this.pageNumber = pageNumber;
		this.numberPages = numberPages;
		this.list = list;
	}

// Set methods
	public void setIterator(Iterator iterator) {
		this.iterator = iterator;
	}

	public void setNumberRemaining(int numberRemaining) {
		this.numberRemaining = numberRemaining;
	}

	public void setIstart(int istart) {
		this.istart = istart;
	}

	public void setIend(int iend) {
		this.iend = iend;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public void setNumberPages(int numberPages) {
		this.numberPages = numberPages;
	}

	public void setList(List list) {
		this.list = list;
	}


// Get methods
	public Iterator getIterator() {
		return this.iterator;
	}

	public int getNumberRemaining() {
		return this.numberRemaining;
	}

	public int getIstart() {
		return this.istart;
	}

	public int getIend() {
		return this.iend;
	}

	public int getSize() {
		return this.size;
	}

	public int getPageNumber() {
		return this.pageNumber;
	}

	public int getNumberPages() {
		return this.numberPages;
	}

	public List getList() {
		return this.list;
	}

}
