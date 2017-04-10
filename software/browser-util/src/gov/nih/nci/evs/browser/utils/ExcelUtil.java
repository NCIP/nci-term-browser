package gov.nih.nci.evs.browser.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.io.*;
import java.util.*;

import org.apache.commons.codec.binary.Base64;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.apache.poi.hssf.usermodel.HSSFPictureData;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFShape;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.util.CellRangeAddress;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

import java.io.*;


public class ExcelUtil {
    final private StringBuilder out = new StringBuilder(65536);

    private short colIndex;
    private int rowIndex, mergeStart, mergeEnd;

    final private static String XLSX_FORMAT = "xlsx";
    final private static String XLS_FORMAT = "xls";
    final private static String UNKNOWN_FORMAT = "unknown";


     public ExcelUtil() {

	 }

     public static String getHSSFHeader(String file, int sheet) {
		StringBuffer buf = new StringBuffer();
		try {
			FileInputStream fis = new FileInputStream(new File(file));
			//Get the workbook instance for XLS file
			HSSFWorkbook workbook = new HSSFWorkbook(fis);
			try {
				fis.close();
			} catch (Exception ex) {
                ex.printStackTrace();
			}

			//Get first sheet from the workbook
			HSSFSheet hSSFSheet = workbook.getSheetAt(sheet);
			HSSFRow row = hSSFSheet.getRow(0);

			int cells = row.getPhysicalNumberOfCells();
			for (int c = 0; c < cells; c++) {
				HSSFCell cell = row.getCell(c);
				String value = null;

				switch (cell.getCellType()) {

					case HSSFCell.CELL_TYPE_FORMULA:
						value = cell.getCellFormula();
						break;

					case HSSFCell.CELL_TYPE_NUMERIC:
						value = "" + cell.getNumericCellValue();
						break;

					case HSSFCell.CELL_TYPE_STRING:
						value = cell.getStringCellValue();
						break;

					default:
				}
				buf.append(value);
				if (c < cells-1) {
					buf.append("|");
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return buf.toString();
	 }


     public static int getHSSFStartRow(String file, int sheet, int col, String code) {
		try {
			FileInputStream fis = new FileInputStream(new File(file));
			//Get the workbook instance for XLS file
			HSSFWorkbook workbook = new HSSFWorkbook(fis);
			try {
				fis.close();
			} catch (Exception ex) {
                ex.printStackTrace();
			}

			//Get first sheet from the workbook
			HSSFSheet hSSFSheet = workbook.getSheetAt(sheet);

			if (col == -1) {
				return 1;
			}

			//Get iterator to all the rows in current sheet
			Iterator<Row> rowIterator = hSSFSheet.iterator();

			//Get iterator to all cells of current row
			int lcv = 0;
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				//if (row.getCell(0).getStringCellValue().compareTo(code) == 0 ||
				if (row.getCell(col).getStringCellValue().compareTo(code) == 0) {
					return lcv;
				}

				lcv++;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return -1;
	 }

     public static int getHSSFEndRow(String file, int sheet, int col, String code) {
		int num = -1;
		try {
			FileInputStream fis = new FileInputStream(new File(file));
			//Get the workbook instance for XLS file
			HSSFWorkbook workbook = new HSSFWorkbook(fis);
			try {
				fis.close();
			} catch (Exception ex) {
                ex.printStackTrace();
			}

			//Get first sheet from the workbook
			HSSFSheet hSSFSheet = workbook.getSheetAt(sheet);

			if (col == -1) {
				return hSSFSheet.getLastRowNum();
			}

			//Get iterator to all the rows in current sheet
			Iterator<Row> rowIterator = hSSFSheet.iterator();

			//Get iterator to all cells of current row
			int lcv = 0;

			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				//if (row.getCell(0).getStringCellValue().compareTo(code) == 0 ||
				if (row.getCell(col).getStringCellValue().compareTo(code) == 0) {
					num = lcv;
				}
				lcv++;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return num;
	}

	//String getHSSFHeader(String file, int sheet)
	public static void main(String [] args)
	{
		String header = getHSSFHeader("ADaM_Terminology.xls", 1);
		System.out.println(header);
	}

}
