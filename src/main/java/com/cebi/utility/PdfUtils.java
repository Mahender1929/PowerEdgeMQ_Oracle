package com.cebi.utility;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;

import com.cebi.dao.AdminReportDao;
import com.cebi.dao.AdminReportDaoImpl;
import com.cebi.dao.ApplicationLabelDao;
import com.cebi.entity.ApplicationLabel;
import com.cebi.entity.Banks;
import com.cebi.entity.QueryData;
import com.cebi.service.GenerateCheckDigitService;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

public class PdfUtils {
	public static final Font smallHeadersFont = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.BOLD);
	public static final Font smallHeadersResultFont = new Font(Font.FontFamily.TIMES_ROMAN, 6, Font.NORMAL);
	public static final Font headerBold = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
	public static final Font tableheaderBold = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL);

	@Autowired
	GenerateCheckDigitService generateCheckDigitService;
	
	@Autowired
	ApplicationLabelDao applicationLabelDao;
	
	@Autowired
	AdminReportDao adminReportDao;

	public String populateQuery(QueryData table, String parameter, String criteria, String merchantId) {
		String sql = "Select ";
		String parameterS = "  ";
		if (parameter.trim().length() > 0) {
			 parameter = parameter.trim();
			    if (parameter.endsWith(","))
				parameterS += parameter.substring(0, (parameter.length() - 1)) + " ";
			    else
				parameterS += parameter.substring(0, (parameter.length())) + " ";
		}else {
		    parameterS += "  * ";
		}
		if (criteria.trim().length() > 0) {
			sql = sql + parameterS + CebiConstant.QRY_FROM + table.getTable() + CebiConstant.QRY_WHERE;
			sql +=  criteria  +  " AND MERCHANTID ="+ merchantId;
			//sql += CebiConstant.QRY_ROWNUM;
		} else {
			//sql = sql + parameterS + " from " + table.getTable() + CebiConstant.WHERE_ROWNUM;
		    sql = sql + parameterS + CebiConstant.QRY_FROM + table.getTable() + CebiConstant.QRY_WHERE +  "MERCHANTID = "+ merchantId;
		}
		if (table.getGroupby() != null && table.getGroupby().trim().length() > 0) {
			String groups = table.getGroupby().substring(0, (table.getGroupby().length() - 1));
			sql = sql + "GROUP BY " + groups;
		}
		return sql;
	}

	public PdfPCell getTableHeadingCell(String text, int alignment, int size) {
		PdfPCell cell = new PdfPCell(new Phrase(text, smallHeadersFont));
		cell.setFixedHeight(20f);
		cell.setHorizontalAlignment(alignment);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorderColor(BaseColor.LIGHT_GRAY);
		cell.setBorder(PdfPCell.NO_BORDER);
		cell.setColspan(size);
		return cell;
	}

	// Add cells for Result Data
	public PdfPCell getResultCell(String text, int alignment) {
		PdfPCell cell = new PdfPCell(new Phrase(text, smallHeadersResultFont));
		cell.setPaddingLeft(3);
		cell.setFixedHeight(12f);
		cell.setHorizontalAlignment(alignment);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorderColor(BaseColor.LIGHT_GRAY);
		return cell;
	}

	public PdfPCell getEmptyCell() {
		PdfPCell cell = new PdfPCell();
		cell.setFixedHeight(15f);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorder(PdfPCell.NO_BORDER);
		return cell;
	}

	public PdfPCell getCell(String text, int alignment) {
		PdfPCell cell = new PdfPCell(new Phrase(text, smallHeadersFont));
		cell.setFixedHeight(15f);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setHorizontalAlignment(alignment);
		cell.setBorder(PdfPCell.NO_BORDER);
		cell.setBorderColor(BaseColor.LIGHT_GRAY);
		return cell;
	}

	// Add image cell
	public PdfPCell getImageCell(Image logo) {
		PdfPCell logoLeftCell = new PdfPCell();
		logoLeftCell.setFixedHeight(15f);
		logoLeftCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		logoLeftCell.setBorder(Rectangle.NO_BORDER);
		logoLeftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		logo.setAlignment(Element.ALIGN_LEFT);
		logoLeftCell.addElement(logo);
		return logoLeftCell;
	}

	public Paragraph addHeading() {
		Paragraph para = new Paragraph("Report Heading", smallHeadersFont);
		para.setAlignment(Element.ALIGN_CENTER);
		return para;

	}

	public PdfPTable addDateAndIdTable() throws Exception {
		PdfPTable table = new PdfPTable(2);
		Random random = new Random(System.currentTimeMillis());
		int branchId = random.nextInt(12) + 1;
		int reportId = random.nextInt(15) + 1;
		ClassLoader classLoader = new AdminReportDaoImpl().getClass().getClassLoader();
		Image logo = Image.getInstance(classLoader.getResource(CebiConstant.BANK_LOGO_IMAGE));
		logo.scalePercent(100);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
		table.setWidthPercentage(100);
		table.addCell(getEmptyCell());
		table.addCell(getCell("Branch Id : " + branchId, PdfPCell.ALIGN_RIGHT));
		table.addCell(getCell("Date : " + simpleDateFormat.format(new Date()), PdfPCell.ALIGN_LEFT));
		table.addCell(getCell("Report Id : " + reportId, +PdfPCell.ALIGN_RIGHT));
		return table;

	}

	public PdfPTable createDataTable(List<String> columnLables, List<String> dbColumns, ResultSet resultSet, String tablename) throws SQLException, DocumentException {
		PdfPTable table = null;
		if ("INCT".equalsIgnoreCase(tablename)) {
			ResultSetMetaData rsmd = resultSet.getMetaData();
			int columnCount = rsmd.getColumnCount();
			table = new PdfPTable(columnCount);
			table.setWidthPercentage(100);
			table.setWidths(columnsWidths(columnCount));
			table.setHeaderRows(2);
			table.addCell(getTableHeadingCell("Report Heading", PdfPCell.ALIGN_CENTER, columnCount));
			PdfPCell cell = new PdfPCell();
			dbColumns = new ArrayList<String>();
			for (int i = 1; i <= columnCount; i++) {
				String columnlabel = rsmd.getColumnName(i);
				cell = new PdfPCell(new Phrase(columnlabel, headerBold));
				cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				cell.setPaddingLeft(3);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setBorderColor(BaseColor.LIGHT_GRAY);
				cell.setFixedHeight(20f);
				table.addCell(cell);
				dbColumns.add(columnlabel);
			}
		} else {
			table = new PdfPTable(columnLables.size());
			table.setWidthPercentage(100);
			table.setWidths(columnsWidths(columnLables.size()));
			table.setHeaderRows(2);
			table.addCell(getTableHeadingCell("Report Heading", PdfPCell.ALIGN_CENTER, columnLables.size()));
			PdfPCell cell = new PdfPCell();
			for (String lbl : columnLables) {

				cell = new PdfPCell(new Phrase(lbl, headerBold));
				cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				cell.setPaddingLeft(3);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setBorderColor(BaseColor.LIGHT_GRAY);
				cell.setFixedHeight(20f);
				table.addCell(cell);
			}
		}
		while (resultSet.next()) {
			for (String label : dbColumns) {
				label = label.contains("(") && label.contains(")") ? label.substring(label.indexOf('(') + 1, label.indexOf(')')) : label;
				if (resultSet.getString(label).equals(null) || resultSet.getString(label).isEmpty())
					table.addCell(getResultCell("", PdfPCell.ALIGN_LEFT));
				else
					table.addCell(getResultCell(resultSet.getString(label), PdfPCell.ALIGN_LEFT));
			}
		}
		return table;
	}

	public static int[] columnsWidths(int size) {
		int[] widths = new int[size];
		for (int i = 0; i < size; i++) {
			widths[i] = 30;
		}
		return widths;
	}

	protected PdfPCell getCell(String text) {
		PdfPCell cell = new PdfPCell();
		cell = new PdfPCell(new Phrase(text, smallHeadersResultFont));
		cell.setPaddingLeft(3);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorderColor(BaseColor.LIGHT_GRAY);
		cell.setFixedHeight(20f);
		return cell;
	}

	protected PdfPCell getStateCell(String text) {
		PdfPCell cell = new PdfPCell();
		cell = new PdfPCell(new Phrase(text, tableheaderBold));
		cell.setPaddingLeft(3);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorderColor(BaseColor.LIGHT_GRAY);
		cell.setFixedHeight(20f);
		return cell;
	}

	protected PdfPCell getStateCellSix(String text) {
		PdfPCell cell = new PdfPCell();
		cell = new PdfPCell(new Phrase(text, headerBold));
		cell.setPaddingLeft(3);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorderColor(BaseColor.LIGHT_GRAY);
		cell.setFixedHeight(20f);
		cell.setColspan(2);
		return cell;
	}

	protected PdfPCell getStateCellForSix(String text) {
		PdfPCell cell = new PdfPCell();
		cell = new PdfPCell(new Phrase(text, headerBold));
		cell.setPaddingLeft(3);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorderColor(BaseColor.LIGHT_GRAY);
		cell.setFixedHeight(20f);
		cell.setColspan(2);
		return cell;
	}

	protected PdfPCell getTableHeaderCell(String text, int a) {
		PdfPCell cell = new PdfPCell();
		cell = new PdfPCell(new Phrase(text, tableheaderBold));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorderColor(BaseColor.BLACK);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		cell.setFixedHeight(20f);
		cell.setColspan(a);
		return cell;
	}

	protected PdfPCell addTableHeader(String text, int a, int noBorder) {
		PdfPCell cell = new PdfPCell();
		cell = new PdfPCell(new Phrase(text, tableheaderBold));
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_LEFT);
		cell.setBorderColor(BaseColor.LIGHT_GRAY);
		cell.setFixedHeight(20f);
		cell.setColspan(a);
		cell.setBorder(noBorder);
		return cell;
	}

	/*
	 * public PdfPCell getResultCell(String text, int alignment) { PdfPCell cell
	 * = new PdfPCell(new Phrase(text, smallHeadersResultFont));
	 * cell.setPaddingLeft(3); cell.setFixedHeight(12f);
	 * cell.setHorizontalAlignment(alignment);
	 * cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
	 * cell.setBorderColor(BaseColor.LIGHT_GRAY); return cell; }
	 */
	public String getBankName(String bankcd) {
		Banks bankdata = adminReportDao.retreiveDbConnection(bankcd);
		bankdata.getBankName();
	 	return bankdata.getBankName().trim();
	}
	public String getTableNames(String table) {
		 List<ApplicationLabel> lables=applicationLabelDao.retrieveAllLabels();
		 String tableName=null;
		if (!table.isEmpty()) {
			for (ApplicationLabel lable : lables) {
				if (lable.getLabelCode().trim().equalsIgnoreCase(table.trim())) {
					tableName=lable.getAppLabel();
					break;
				}
			}
		}
		return tableName;
	}
	
}
