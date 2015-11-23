package gov.nih.nci.evs.browser.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.Map;
import java.util.ListIterator;

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

import java.io.*;
import java.util.*;


/**
 * Use Apache POI to read an Excel (.xls) file and output an HTML table per
 * sheet.
 *
 *
 */
public class ResolvedValueSetIteratorHolder {
    //final private StringBuilder out = new StringBuilder(65536);
    private StringBuffer out = new StringBuffer();
    private SimpleDateFormat sdf;
    private HSSFWorkbook book;
    private HSSFPalette palette;
    private FormulaEvaluator evaluator;
    private short colIndex;
    private int rowIndex, mergeStart, mergeEnd;
    private String URL = null;

    private List resolvedValueSetList = null;
    private ListIterator resolvedValueSetIterator = null;
    private Map<Integer, Map<Short, List<HSSFPictureData>>> pix = new HashMap<Integer, Map<Short, List<HSSFPictureData>>>();

    private Vector rvs_content_vec = null;
    private static String YELLOW = "#FFFF00";

    public ResolvedValueSetIteratorHolder() {

	}

    public ResolvedValueSetIteratorHolder(final String filename, int sheet, int startIndex, int endIndex) throws IOException {
		resolvedValueSetList = new ArrayList();

		InputStream in = getInputStream(filename);
        sdf = new SimpleDateFormat("dd/MM/yyyy");
        if (in == null) {
            book = null;
            palette = null;
            evaluator = null;
            return;
        }
        book = new HSSFWorkbook(in);
        try {
			in.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
        palette = book.getCustomPalette();
        evaluator = book.getCreationHelper().createFormulaEvaluator();
        table(book.getSheetAt(sheet), startIndex, endIndex);
        //rvs_content_vec = getTableContent(sheet, startIndex, endIndex);
        rvs_content_vec = extractRawDataFromTableContent();

   }



    public ResolvedValueSetIteratorHolder(final String filename, int sheet, int startIndex, int col, String code, String url, boolean cdisc) throws IOException {
		resolvedValueSetList = new ArrayList();

		InputStream in = getInputStream(filename);
        sdf = new SimpleDateFormat("dd/MM/yyyy");
        if (in == null) {
            book = null;
            palette = null;
            evaluator = null;
            return;
        }
        this.URL = url;
        book = new HSSFWorkbook(in);
        try {
			in.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
        palette = book.getCustomPalette();
        evaluator = book.getCreationHelper().createFormulaEvaluator();
        table(book.getSheetAt(sheet), startIndex, col, code, cdisc);
        //rvs_content_vec = getTableContent(sheet, startIndex, endIndex);
        rvs_content_vec = extractRawDataFromTableContent();
   }



    public ResolvedValueSetIteratorHolder(final InputStream in) throws IOException {
		resolvedValueSetList = new ArrayList();
		sdf = new SimpleDateFormat("dd/MM/yyyy");
        if (in == null) {
            book = null;
            palette = null;
            evaluator = null;
            return;
        }
        book = new HSSFWorkbook(in);
        try {
			in.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

        palette = book.getCustomPalette();
        evaluator = book.getCreationHelper().createFormulaEvaluator();
        for (int i = 0; i < book.getNumberOfSheets(); ++i) {
            table(book.getSheetAt(i));
        }
    }

    public ResolvedValueSetIteratorHolder(final String filename, int sheet, int startIndex, int endIndex, String url) throws IOException {
		resolvedValueSetList = new ArrayList();

		InputStream in = getInputStream(filename);
        sdf = new SimpleDateFormat("dd/MM/yyyy");
        if (in == null) {
            book = null;
            palette = null;
            evaluator = null;
            return;
        }
        this.URL = url;
        book = new HSSFWorkbook(in);
        try {
			in.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
        palette = book.getCustomPalette();
        evaluator = book.getCreationHelper().createFormulaEvaluator();
        table(book.getSheetAt(sheet), startIndex, endIndex);
    }


    public ResolvedValueSetIteratorHolder(final InputStream in, String url) throws IOException {
        sdf = new SimpleDateFormat("dd/MM/yyyy");
        if (in == null) {
            book = null;
            palette = null;
            evaluator = null;
            return;
        }
        this.URL = url;
        book = new HSSFWorkbook(in);
        try {
			in.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

        palette = book.getCustomPalette();
        evaluator = book.getCreationHelper().createFormulaEvaluator();
        for (int i = 0; i < book.getNumberOfSheets(); ++i) {
            table(book.getSheetAt(i));
        }
    }


    private void table(final HSSFSheet sheet) {
        if (sheet == null) {
            return;
        }
        if (sheet.getDrawingPatriarch() != null) {
            final List<HSSFShape> shapes = sheet.getDrawingPatriarch()
                    .getChildren();
            for (int i = 0; i < shapes.size(); ++i) {
                if (shapes.get(i) instanceof HSSFPicture) {
                    try {
                        // Gain access to private field anchor.
                        final HSSFShape pic = shapes.get(i);
                        final Field f = HSSFShape.class
                                .getDeclaredField("anchor");
                        f.setAccessible(true);
                        final HSSFClientAnchor anchor = (HSSFClientAnchor) f
                                .get(pic);
                        // Store picture cell row, column and picture data.
                        if (!pix.containsKey(anchor.getRow1())) {
                            pix.put(anchor.getRow1(),
                                    new HashMap<Short, List<HSSFPictureData>>());
                        }
                        if (!pix.get(anchor.getRow1()).containsKey(
                                anchor.getCol1())) {
                            pix.get(anchor.getRow1()).put(anchor.getCol1(),
                                    new ArrayList<HSSFPictureData>());
                        }
                        pix.get(anchor.getRow1())
                                .get(anchor.getCol1())
                                .add(book.getAllPictures().get(
                                        ((HSSFPicture) pic).getPictureIndex()));
                    } catch (final Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        out.append("<table cellspacing='0' style='border-spacing:0; border-collapse:collapse;'>\n");
        for (rowIndex = 0; rowIndex < sheet.getPhysicalNumberOfRows(); ++rowIndex) {
            tr(sheet.getRow(rowIndex));
        }
        out.append("</table>\n");
    }


    public static String getOpenTableTag(String table_name) {
		//return "<table cellspacing='0' style='border-spacing:0; border-collapse:collapse;'>";
        //return "<table class=\"datatable_960\" summary=\"Data Table\" cellpadding=\"3\" cellspacing=\"0\" border=\"0\" width=\"100%\">";
        return "<table id=\"" + table_name + "\" width=\"900\" class=\"mt\">";
	}


    public static String getCloseTableTag() {
		return "</table>";
	}


    private void table(final HSSFSheet sheet, int startIndex, int endIndex) {
        if (sheet == null) {
            return;
        }
        if (sheet.getDrawingPatriarch() != null) {
            final List<HSSFShape> shapes = sheet.getDrawingPatriarch()
                    .getChildren();
            for (int i = 0; i < shapes.size(); ++i) {
                if (shapes.get(i) instanceof HSSFPicture) {
                    try {
                        // Gain access to private field anchor.
                        final HSSFShape pic = shapes.get(i);
                        final Field f = HSSFShape.class
                                .getDeclaredField("anchor");
                        f.setAccessible(true);
                        final HSSFClientAnchor anchor = (HSSFClientAnchor) f
                                .get(pic);
                        // Store picture cell row, column and picture data.
                        if (!pix.containsKey(anchor.getRow1())) {
                            pix.put(anchor.getRow1(),
                                    new HashMap<Short, List<HSSFPictureData>>());
                        }
                        if (!pix.get(anchor.getRow1()).containsKey(
                                anchor.getCol1())) {
                            pix.get(anchor.getRow1()).put(anchor.getCol1(),
                                    new ArrayList<HSSFPictureData>());
                        }
                        pix.get(anchor.getRow1())
                                .get(anchor.getCol1())
                                .add(book.getAllPictures().get(
                                        ((HSSFPicture) pic).getPictureIndex()));
                    } catch (final Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        out.append("<table id=\"" + "rvs_table" + "\" width=\"900\" class=\"mt\">\n");
        tr(sheet.getRow(0));
        StringBuffer buf = new StringBuffer();
        tr(sheet.getRow(0), buf);
        String t = buf.toString();
        resolvedValueSetList.add(t);

        for (int i=startIndex; i<=endIndex; i++) {
            tr(sheet.getRow(i));
			buf = new StringBuffer();
			tr(sheet.getRow(i), buf);
			t = buf.toString();
			resolvedValueSetList.add(t);

        }
        out.append("</table>\n");

        resolvedValueSetIterator = resolvedValueSetList.listIterator();
    }


    private void table(final HSSFSheet sheet, int startIndex, int col, String code, boolean cdisc) {
        resolvedValueSetList = new ArrayList();

        if (sheet == null) {
            return;
        }
        if (sheet.getDrawingPatriarch() != null) {
            final List<HSSFShape> shapes = sheet.getDrawingPatriarch()
                    .getChildren();
            for (int i = 0; i < shapes.size(); ++i) {
                if (shapes.get(i) instanceof HSSFPicture) {
                    try {
                        // Gain access to private field anchor.
                        final HSSFShape pic = shapes.get(i);
                        final Field f = HSSFShape.class
                                .getDeclaredField("anchor");
                        f.setAccessible(true);
                        final HSSFClientAnchor anchor = (HSSFClientAnchor) f
                                .get(pic);
                        // Store picture cell row, column and picture data.
                        if (!pix.containsKey(anchor.getRow1())) {
                            pix.put(anchor.getRow1(),
                                    new HashMap<Short, List<HSSFPictureData>>());
                        }
                        if (!pix.get(anchor.getRow1()).containsKey(
                                anchor.getCol1())) {
                            pix.get(anchor.getRow1()).put(anchor.getCol1(),
                                    new ArrayList<HSSFPictureData>());
                        }
                        pix.get(anchor.getRow1())
                                .get(anchor.getCol1())
                                .add(book.getAllPictures().get(
                                        ((HSSFPicture) pic).getPictureIndex()));
                    } catch (final Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        out.append("<table id=\"" + "rvs_table" + "\" width=\"900\" class=\"mt\">\n");
        tr(sheet.getRow(0));
        StringBuffer buf = new StringBuffer();
        tr(sheet.getRow(0), buf);
        String t = buf.toString();
        resolvedValueSetList.add(t);
        int rows = sheet.getPhysicalNumberOfRows();

        for (int i=startIndex; i<=rows; i++) {
			HSSFRow row = sheet.getRow(i);
			if (row != null) {
				if (col != -1) {
					HSSFCell cell = row.getCell(col);
					if (cell != null) {
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
						if ((cdisc && i==startIndex) ||
						    (value != null && value.compareTo(code) == 0)) {
							tr(row);
							buf = new StringBuffer();
							tr(row, buf);
							t = buf.toString();
							resolvedValueSetList.add(t);
						}
					}
			    } else {
					tr(row);
					buf = new StringBuffer();
					tr(row, buf);
					t = buf.toString();
					resolvedValueSetList.add(t);
				}
		    }
        }
        out.append("</table>\n");
        resolvedValueSetIterator = resolvedValueSetList.listIterator();
    }


    public ListIterator getResolvedValueSetIterator() {
		return resolvedValueSetIterator;
	}

    public List getResolvedValueSetList() {
		return resolvedValueSetList;
	}


    /**
     * (Each Excel sheet row becomes an HTML table row) Generates an HTML table
     * row which has the same height as the Excel row.
     *
     * @param row
     *            The Excel row.
     */
    private void tr(final HSSFRow row) {
        if (row == null) {
            return;
        }
        out.append("<tr ");
        // Find merged cells in current row.
        for (int i = 0; i < row.getSheet().getNumMergedRegions(); ++i) {
            final CellRangeAddress merge = row.getSheet().getMergedRegion(i);
            if (rowIndex >= merge.getFirstRow()
                    && rowIndex <= merge.getLastRow()) {
                mergeStart = merge.getFirstColumn();
                mergeEnd = merge.getLastColumn();
                break;
            }
        }
        out.append("style='");
        if (row.getHeight() != -1) {
            out.append("height: ")
                    .append(Math.round(row.getHeight() / 20.0 * 1.33333))
                    .append("px; ");
        }
        out.append("'>\n");
        for (colIndex = 0; colIndex < row.getLastCellNum(); ++colIndex) {
            td(row.getCell(colIndex));
        }
        out.append("</tr>\n");
    }


    private void tr(final HSSFRow row, StringBuffer buf) {
        if (row == null) {
            return;
        }
        buf.append("<tr ");
        // Find merged cells in current row.
        for (int i = 0; i < row.getSheet().getNumMergedRegions(); ++i) {
            final CellRangeAddress merge = row.getSheet().getMergedRegion(i);
            if (rowIndex >= merge.getFirstRow()
                    && rowIndex <= merge.getLastRow()) {
                mergeStart = merge.getFirstColumn();
                mergeEnd = merge.getLastColumn();
                break;
            }
        }
        buf.append("style='");
        if (row.getHeight() != -1) {
            buf.append("height: ")
                    .append(Math.round(row.getHeight() / 20.0 * 1.33333))
                    .append("px; ");

        }
        buf.append("'>");
        for (colIndex = 0; colIndex < row.getLastCellNum(); ++colIndex) {
            td(row.getCell(colIndex));
            td(row.getCell(colIndex), buf);
        }
        buf.append("</tr>");
    }




     public boolean isCode(String str) {
		 if (str == null) return false;
		 if (str.length() == 0) return false;
		 char c = str.charAt(0);
		 //String first_ch = str.substring(0, 1);
		 String first_ch = "" + c;
		 if (first_ch.compareTo("C") != 0) return false;
		 String substr = str.substring(1, str.length());
		 for (int i=0; i<substr.length(); i++) {
			 c = substr.charAt(i);
			 if (!Character.isDigit(c)) return false;
		 }
         return true;
	 }

	 public String getHyperLink(String code) {
         StringBuffer buf = new StringBuffer();
         buf.append("<a href=\"");
         buf.append(URL);
		 buf.append("&code=" + code);
		 buf.append("\"");
		 buf.append(">");
		 buf.append(code);
		 buf.append("</a>");
		 return buf.toString();
	 }

    /**
     * (Each Excel sheet cell becomes an HTML table cell) Generates an HTML
     * table cell which has the same font styles, alignments, colours and
     * borders as the Excel cell.
     *
     * @param cell
     *            The Excel cell.
     */




    private void td(final HSSFCell cell) {
        int colspan = 1;
        if (colIndex == mergeStart) {
            // First cell in the merging region - set colspan.
            colspan = mergeEnd - mergeStart + 1;
        } else if (colIndex == mergeEnd) {
            // Last cell in the merging region - no more skipped cells.
            mergeStart = -1;
            mergeEnd = -1;
            return;
        } else if (mergeStart != -1 && mergeEnd != -1 && colIndex > mergeStart
                && colIndex < mergeEnd) {
            // Within the merging region - skip the cell.
            return;
        }
        out.append("<td ");
        if (colspan > 1) {
            out.append("colspan='").append(colspan).append("' ");
        }
        if (cell == null) {
            out.append("/>\n");
            return;
        }
        out.append("style='");
        final HSSFCellStyle style = cell.getCellStyle();
        // Text alignment
        switch (style.getAlignment()) {
        case CellStyle.ALIGN_LEFT:
            out.append("text-align: left; ");
            break;
        case CellStyle.ALIGN_RIGHT:
            out.append("text-align: right; ");
            break;
        case CellStyle.ALIGN_CENTER:
            out.append("text-align: center; ");
            break;
        default:
            break;
        }
        // Font style, size and weight
        final HSSFFont font = style.getFont(book);
        if (font.getBoldweight() == HSSFFont.BOLDWEIGHT_BOLD) {
            out.append("font-weight: bold; ");
        }
        if (font.getItalic()) {
            out.append("font-style: italic; ");
        }
        if (font.getUnderline() != HSSFFont.U_NONE) {
            out.append("text-decoration: underline; ");
        }
        out.append("font-size: ")
                .append(Math.floor(font.getFontHeightInPoints() * 0.8))
                .append("pt; ");
        // Cell background and font colours
        final short[] backRGB = style.getFillForegroundColorColor()
                .getTriplet();
        final HSSFColor foreColor = palette.getColor(font.getColor());
        if (foreColor != null) {
            final short[] foreRGB = foreColor.getTriplet();
            if (foreRGB[0] != 0 || foreRGB[1] != 0 || foreRGB[2] != 0) {
                out.append("color: rgb(").append(foreRGB[0]).append(',')
                        .append(foreRGB[1]).append(',').append(foreRGB[2])
                        .append(");");
            }
        }
        if (backRGB[0] != 0 || backRGB[1] != 0 || backRGB[2] != 0) {
            out.append("background-color: rgb(").append(backRGB[0]).append(',')
                    .append(backRGB[1]).append(',').append(backRGB[2])
                    .append(");");
        }
        // Border
        if (style.getBorderTop() != HSSFCellStyle.BORDER_NONE) {
            out.append("border-top-style: solid; ");
        }
        if (style.getBorderRight() != HSSFCellStyle.BORDER_NONE) {
            out.append("border-right-style: solid; ");
        }
        if (style.getBorderBottom() != HSSFCellStyle.BORDER_NONE) {
            out.append("border-bottom-style: solid; ");
        }
        if (style.getBorderLeft() != HSSFCellStyle.BORDER_NONE) {
            out.append("border-left-style: solid; ");
        }
        out.append("'>");
        String val = "";
        try {
            switch (cell.getCellType()) {
            case HSSFCell.CELL_TYPE_STRING:
                val = cell.getStringCellValue();
                break;
            case HSSFCell.CELL_TYPE_NUMERIC:
                // POI does not distinguish between integer and double, thus:
                final double original = cell.getNumericCellValue(),
                rounded = Math.round(original);
                if (Math.abs(rounded - original) < 0.00000000000000001) {
                    val = String.valueOf((int) rounded);
                } else {
                    val = String.valueOf(original);
                }
                break;
            case HSSFCell.CELL_TYPE_FORMULA:
                final CellValue cv = evaluator.evaluate(cell);
                switch (cv.getCellType()) {
                case Cell.CELL_TYPE_BOOLEAN:
                    out.append(cv.getBooleanValue());
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    out.append(cv.getNumberValue());
                    break;
                case Cell.CELL_TYPE_STRING:
                    out.append(cv.getStringValue());
                    break;
                case Cell.CELL_TYPE_BLANK:
                    break;
                case Cell.CELL_TYPE_ERROR:
                    break;
                default:
                    break;
                }
                break;
            default:
                // Neither string or number? Could be a date.
                try {
                    val = sdf.format(cell.getDateCellValue());
                } catch (final Exception e1) {
                }
            }
        } catch (final Exception e) {
            val = e.getMessage();
        }
        if ("null".equals(val)) {
            val = "";
        }
        if (pix.containsKey(rowIndex)) {
            if (pix.get(rowIndex).containsKey(colIndex)) {
                for (final HSSFPictureData pic : pix.get(rowIndex)
                        .get(colIndex)) {
                    out.append("<img alt='Image in Excel sheet' src='data:");
                    out.append(pic.getMimeType());
                    out.append(";base64,");
                    try {
                        out.append(new String(
                                Base64.encodeBase64(pic.getData()), "US-ASCII"));
                    } catch (final UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                    out.append("'/>");
                }
            }
        }


		if (isCode(val) && this.URL != null) {
			val = getHyperLink(val);
		}


        out.append(val);
        out.append("</td>\n");
    }


    private void td(final HSSFCell cell, StringBuffer buf) {
        int colspan = 1;
        if (colIndex == mergeStart) {
            // First cell in the merging region - set colspan.
            colspan = mergeEnd - mergeStart + 1;
        } else if (colIndex == mergeEnd) {
            // Last cell in the merging region - no more skipped cells.
            mergeStart = -1;
            mergeEnd = -1;
            return;
        } else if (mergeStart != -1 && mergeEnd != -1 && colIndex > mergeStart
                && colIndex < mergeEnd) {
            // Within the merging region - skip the cell.
            return;
        }
        buf.append("<td ");
        if (colspan > 1) {
            buf.append("colspan='").append(colspan).append("' ");
        }
        if (cell == null) {
            buf.append("/>");
            return;
        }
        buf.append("style='");
        final HSSFCellStyle style = cell.getCellStyle();
        // Text alignment
        switch (style.getAlignment()) {
        case CellStyle.ALIGN_LEFT:
            buf.append("text-align: left; ");
            break;
        case CellStyle.ALIGN_RIGHT:
            buf.append("text-align: right; ");
            break;
        case CellStyle.ALIGN_CENTER:
            buf.append("text-align: center; ");
            break;
        default:
            break;
        }
        // Font style, size and weight
        final HSSFFont font = style.getFont(book);
        if (font.getBoldweight() == HSSFFont.BOLDWEIGHT_BOLD) {
            buf.append("font-weight: bold; ");
        }
        if (font.getItalic()) {
            buf.append("font-style: italic; ");
        }
        if (font.getUnderline() != HSSFFont.U_NONE) {
            buf.append("text-decoration: underline; ");
        }
        buf.append("font-size: ")
                .append(Math.floor(font.getFontHeightInPoints() * 0.8))
                .append("pt; ");

        // Cell background and font colours
        final short[] backRGB = style.getFillForegroundColorColor()
                .getTriplet();
        final HSSFColor foreColor = palette.getColor(font.getColor());
        if (foreColor != null) {
            final short[] foreRGB = foreColor.getTriplet();
            if (foreRGB[0] != 0 || foreRGB[1] != 0 || foreRGB[2] != 0) {
                buf.append("color: rgb(").append(foreRGB[0]).append(',')
                        .append(foreRGB[1]).append(',').append(foreRGB[2])
                        .append(");");

            }
        }
        if (backRGB[0] != 0 || backRGB[1] != 0 || backRGB[2] != 0) {
            buf.append("background-color: rgb(").append(backRGB[0]).append(',')
                    .append(backRGB[1]).append(',').append(backRGB[2])
                    .append(");");

        }
        // Border
        if (style.getBorderTop() != HSSFCellStyle.BORDER_NONE) {
            buf.append("border-top-style: solid; ");
        }
        if (style.getBorderRight() != HSSFCellStyle.BORDER_NONE) {
            buf.append("border-right-style: solid; ");
        }
        if (style.getBorderBottom() != HSSFCellStyle.BORDER_NONE) {
            buf.append("border-bottom-style: solid; ");
        }
        if (style.getBorderLeft() != HSSFCellStyle.BORDER_NONE) {
            buf.append("border-left-style: solid; ");
        }
        buf.append("'>");
        String val = "";
        try {
            switch (cell.getCellType()) {
            case HSSFCell.CELL_TYPE_STRING:
                val = cell.getStringCellValue();
                break;
            case HSSFCell.CELL_TYPE_NUMERIC:
                // POI does not distinguish between integer and double, thus:
                final double original = cell.getNumericCellValue(),
                rounded = Math.round(original);
                if (Math.abs(rounded - original) < 0.00000000000000001) {
                    val = String.valueOf((int) rounded);
                } else {
                    val = String.valueOf(original);
                }
                break;
            case HSSFCell.CELL_TYPE_FORMULA:
                final CellValue cv = evaluator.evaluate(cell);
                switch (cv.getCellType()) {
                case Cell.CELL_TYPE_BOOLEAN:
                    buf.append(cv.getBooleanValue());
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    buf.append(cv.getNumberValue());
                    break;
                case Cell.CELL_TYPE_STRING:
                    buf.append(cv.getStringValue());
                    break;
                case Cell.CELL_TYPE_BLANK:
                    break;
                case Cell.CELL_TYPE_ERROR:
                    break;
                default:
                    break;
                }
                break;
            default:
                // Neither string or number? Could be a date.
                try {
                    val = sdf.format(cell.getDateCellValue());
                } catch (final Exception e1) {
                }
            }
        } catch (final Exception e) {
            val = e.getMessage();
        }
        if ("null".equals(val)) {
            val = "";
        }
        if (pix.containsKey(rowIndex)) {
            if (pix.get(rowIndex).containsKey(colIndex)) {
                for (final HSSFPictureData pic : pix.get(rowIndex)
                        .get(colIndex)) {
                    buf.append("<img alt='Image in Excel sheet' src='data:");
                    buf.append(pic.getMimeType());
                    buf.append(";base64,");


                    try {
                        buf.append(new String(
                                Base64.encodeBase64(pic.getData()), "US-ASCII"));


                    } catch (final UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                    buf.append("'/>");
                }
            }
        }


		if (isCode(val) && this.URL != null) {
			val = getHyperLink(val);
		}
        buf.append(val);
        buf.append("</td>");

    }



    public InputStream getInputStream(String filename) {
		try {
			return new FileInputStream(new File(filename));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}


    public String getHTML() {
        return out.toString();
    }


    public Vector getTableContent() {
       return rvs_content_vec;
    }


    public Vector getTableContent(int sheet_number, int startIndex, int endIndex) {

	    HSSFSheet sheet = book.getSheetAt(sheet_number);
		Vector v = new Vector();
        getRowData(v, sheet.getRow(0));
        for (int i=startIndex; i<=endIndex; i++) {
            getRowData(v, sheet.getRow(i));
        }
        return v;
	}

    private void getRowData(Vector v, final HSSFRow row) {
        if (row == null) {
            return;
        }

        for (int i = 0; i < row.getSheet().getNumMergedRegions(); ++i) {
            final CellRangeAddress merge = row.getSheet().getMergedRegion(i);
            if (rowIndex >= merge.getFirstRow()
                    && rowIndex <= merge.getLastRow()) {
                mergeStart = merge.getFirstColumn();
                mergeEnd = merge.getLastColumn();
                break;
            }
        }

        StringBuffer buf = new StringBuffer();
        for (colIndex = 0; colIndex < row.getLastCellNum(); ++colIndex) {
			if (colIndex > 0) {
				buf.append("|");
			}
            String val = getCellData(row.getCell(colIndex));
            if (val != null) {
				buf.append(val);
			}
        }
        v.add(buf.toString());
    }

    private String getCellData(final HSSFCell cell) {
		if (cell == null) return null;
        int colspan = 1;
        if (colIndex == mergeStart) {
            // First cell in the merging region - set colspan.
            colspan = mergeEnd - mergeStart + 1;
        } else if (colIndex == mergeEnd) {
            // Last cell in the merging region - no more skipped cells.
            mergeStart = -1;
            mergeEnd = -1;
            return null;
        } else if (mergeStart != -1 && mergeEnd != -1 && colIndex > mergeStart
                && colIndex < mergeEnd) {
            // Within the merging region - skip the cell.
            return null;
        }
        StringBuffer buf = new StringBuffer();
        String val = "";
        try {
            switch (cell.getCellType()) {
            case HSSFCell.CELL_TYPE_STRING:
                val = cell.getStringCellValue();
                break;
            case HSSFCell.CELL_TYPE_NUMERIC:
                // POI does not distinguish between integer and double, thus:
                final double original = cell.getNumericCellValue(),
                rounded = Math.round(original);
                if (Math.abs(rounded - original) < 0.00000000000000001) {
                    val = String.valueOf((int) rounded);
                } else {
                    val = String.valueOf(original);
                }
                break;
            case HSSFCell.CELL_TYPE_FORMULA:
                final CellValue cv = evaluator.evaluate(cell);
                switch (cv.getCellType()) {
                case Cell.CELL_TYPE_BOOLEAN:
                    out.append(cv.getBooleanValue());
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    out.append(cv.getNumberValue());
                    break;
                case Cell.CELL_TYPE_STRING:
                    out.append(cv.getStringValue());
                    break;
                case Cell.CELL_TYPE_BLANK:
                    break;
                case Cell.CELL_TYPE_ERROR:
                    break;
                default:
                    break;
                }
                break;
            default:
                // Neither string or number? Could be a date.
                try {
                    val = sdf.format(cell.getDateCellValue());
                } catch (final Exception e1) {
                }
            }
        } catch (final Exception e) {
            val = e.getMessage();
        }
        if ("null".equals(val)) {
            val = "";
        }
        return val;
    }


    public Vector tableContent2CSV(Vector tableContent) {

		if (tableContent == null) return null;
		Vector v = new Vector();

		String headings = (String) tableContent.elementAt(0);
		Vector w = StringUtils.parseData(headings);
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<w.size(); i++) {
			String t = (String) w.elementAt(i);
			if (i > 0) {
				buf.append(",");
			}
			buf.append("\"");
			buf.append(t);
			buf.append("\"");
		}
		v.add(buf.toString());
		if (tableContent.size() == 1) return v;
		for (int i=1; i<tableContent.size(); i++) {
			String t = (String) tableContent.elementAt(i);
			w = StringUtils.parseData(t);
			buf = new StringBuffer();
			for (int k=0; k<w.size(); k++) {
				String s = (String) w.elementAt(k);
				if (k > 0) {
					buf.append(",");
				}
				buf.append("\"");
				buf.append(s);
				buf.append("\"");
			}
			v.add(buf.toString());
		}
		return v;
	}

	public Vector extractRawDataFromTableContent() {
		if (resolvedValueSetList == null) {
			return null;
		}
		Vector w = new Vector();

		for (int i=0; i<resolvedValueSetList.size(); i++) {
            String line = (String) resolvedValueSetList.get(i);
			line = line.replaceAll("</a>", "");
			line = line.replaceAll("</td>", "|");
			line = line.replaceAll("</th>", "|");
			Vector v = StringUtils.parseData(line);

			StringBuffer buf = new StringBuffer();
			for (int j=0; j<v.size(); j++) {
				String t = (String) v.elementAt(j);
				int n = t.lastIndexOf(">");
				if (n != -1) {
					t = t.substring(n+1, t.length());
					if (j > 0) {
						buf.append("|");
					}
					buf.append(t);
			    }
			}
			w.add(buf.toString());
		}
		return w;
	}


    public static String removeHyperlinks(String t) {
		if (t == null) return t;
		int n = t.indexOf("<a href=");
		while (n != -1) {
			String t1 = t.substring(0, n);
			String t2 = t.substring(n, t.length());
			int m = t2.indexOf(">");
			String t3 = "";
			if (m != -1) {
			    t3 = t2.substring(m+1, t2.length());
				m = t3.indexOf("</a>");
				if (m != -1) {
					String t4 = t3.substring(0, m);
					t3 = t4 + t3.substring(m+4, t3.length());
				}
			}
			t = t1 + t3;
			n = t.indexOf("<a href=");
		}
		return t;
	}


    public static String addRowBackgroundColor(String tr) {
		return addRowBackgroundColor(YELLOW, tr);
	}


    public static String addRowBackgroundColor(String colorCode, String tr) {
		if (tr == null) return null;

		if (colorCode == null) {
			colorCode = YELLOW;
		}
		int n = tr.indexOf("<tr>");
		if (n != -1) {
			tr = tr.replace("<tr>", "<tr " + "bgcolor=" + "\"" + colorCode + "\">");
			return tr;
		}

		n = tr.indexOf("<tr ");
		if (n != -1) {
			tr = tr.replace("<tr ", "<tr " + "bgcolor=" + "\"" + colorCode + "\" ");
			return tr;
		}
		return tr;
	}

	public static void main(String [ ] args)
	{
		String filename = args[0];
		System.out.println(filename);
		try {
        	System.out.println(new ResolvedValueSetIteratorHolder(new FileInputStream(new File(filename))).getHTML());
    	} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}

