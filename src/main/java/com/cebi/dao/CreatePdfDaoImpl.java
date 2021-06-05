package com.cebi.dao;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cebi.entity.QueryData;
import com.cebi.utility.CebiConstant;
import com.cebi.utility.ConnectionException;
import com.cebi.utility.PdfUtils;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

@Repository
@Transactional
public class CreatePdfDaoImpl extends PdfUtils implements CreatePdfDao {
	private static final Logger logger = Logger.getLogger(CreatePdfDaoImpl.class);

	@Autowired
	CebiConstant cebiConstant;
	@Autowired
	StaticReportDaoImpl staticReportDaoImpl;

	public byte[] downloadPdf(QueryData queryData, String bank, String merchantId) {

		byte[] bytes = null;
		PdfWriter pdfWriter = null;
		Session session = null;
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		Document document = new Document(PageSize.A4, 36, 36, 45, 72);
		session = cebiConstant.getCurrentSession(bank);
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		ResultSet resultSet = null;
		String parameter = "", columns = "", criteria = "";

		if ("INCT".equalsIgnoreCase(queryData.getTable())) {
			String variable = "";
			if (queryData.getParameter() != null && queryData.getParameter().length() > 0) {
				variable = queryData.getParameter().substring(0, queryData.getParameter().length() - 1);
				variable = variable.replace(",", "','");
				List<String> data = null;
				queryData.setParameter(data.stream().filter(data1 -> data1.trim().length() > 1).map(i -> i.toString()).collect(Collectors.joining(",")));
			}

			if (queryData.getQuery() != null && queryData.getQuery().length() > 0) {
				String mandatory = "";
				if (!queryData.getQuery().contains("INST_NO")) {
					mandatory = "  INST_NO ='003'  AND ";
				}
				if (!queryData.getQuery().contains("TRAN_TYPE")) {
					mandatory = "  TRAN_TYPE IN (01, 20, 80)   AND ";
				}
				queryData.setQuery(mandatory + queryData.getQuery());
			} else {
				String mandatory = "  INST_NO ='003'  AND    TRAN_TYPE IN (01, 20, 80) ";
				queryData.setQuery(mandatory);
			}
			parameter = queryData.getParameter().trim().length() > 0 ? queryData.getParameter() : "";
			criteria = queryData.getQuery().trim().length() > 0 ? queryData.getQuery() : "";
			columns = queryData.getColumnNames().trim().length() > 0 ? queryData.getColumnNames() : "";
		} else {
			parameter = queryData.getParameter().trim().length() > 0 ? queryData.getParameter() : "";
			criteria = queryData.getQuery().trim().length() > 0 ? queryData.getQuery() : "";
			columns = queryData.getColumnNames().trim().length() > 0 ? queryData.getColumnNames() : "";
		}
		String query = populateQuery(queryData, parameter, criteria, merchantId);
		logger.info("Inside createJasperReport()::Query :: " + query);
		try {
			connection = ((SessionImpl) session).connection();
			prepareStatement = (PreparedStatement) connection.prepareStatement(query);
			resultSet = prepareStatement.executeQuery();

			String lstparam = parameter.substring(0, (parameter.length() - 1));
			List<String> dbColumns = Arrays.asList(lstparam.split(","));
			List<String> columnLables = Arrays.asList(columns.split(","));

			pdfWriter = PdfWriter.getInstance(document, buffer);
			MyFooter event = new MyFooter(bank);
			pdfWriter.setPageEvent(event);
			document.open();
			document.add(addDateAndIdTable());
			document.add(Chunk.NEWLINE);
			PdfPTable headeTable = createDataTable(columnLables, dbColumns, resultSet, queryData.getTable());
			document.add(headeTable);
			document.close();
			bytes = buffer.toByteArray();

		} catch (Exception ex) {
			ex.getMessage();
		} catch (OutOfMemoryError error) {
			throw new ConnectionException("Failed to allocate Max memory...!");
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) { /* ignored */
				}
			}
			if (prepareStatement != null) {
				try {
					prepareStatement.close();
				} catch (SQLException e) { /* ignored */
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) { /* ignored */
				}
			}
		}
		return bytes;

	}
}

class MyFooter extends PdfPageEventHelper {
	Font ffont = new Font(Font.FontFamily.UNDEFINED, 5, Font.ITALIC);
	String bankName = null;

	public MyFooter(String bank) {
		this.bankName = bank;
	}

	@Override
	public void onEndPage(PdfWriter writer, Document document) {
		Phrase header = null;
		PdfContentByte cb = writer.getDirectContent();

		Image logo = null;

		try {
			ClassLoader classLoader = this.getClass().getClassLoader();
			if (bankName.equalsIgnoreCase(CebiConstant.CKGB)) {
				header = new Phrase("CKGB Kaveri Grameena Bank");
				logo = Image.getInstance(classLoader.getResource(CebiConstant.BANK_LOGO_IMAGE));
			} else if (bankName.equalsIgnoreCase(CebiConstant.GBCB)) {
				header = new Phrase("GBCB The Greater Bombay");
				logo = Image.getInstance(classLoader.getResource(CebiConstant.GBCB_LOGO_IMAGE));
			} else if (bankName.equalsIgnoreCase(CebiConstant.CHGB)) {
				header = new Phrase("Chhattisgarh Gramin Bank");
				logo = Image.getInstance(classLoader.getResource(CebiConstant.CHGB_LOGO_IMAGE));
			}
			if (logo != null) {
				logo.scaleToFit(100, 100);
				logo.setAlignment(Element.ALIGN_CENTER);
				logo.setAbsolutePosition(30, document.top() + 10);
			}
			writer.getDirectContent().addImage(logo);

		} catch (Exception e) {
			e.getMessage();
		}
		Phrase footer = new Phrase("Page " + writer.getPageNumber(), ffont);
		ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, header, (document.right() - document.left()) / 2 + document.leftMargin(), document.top() + 10, 0);
		ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, footer, (document.right() - document.left()) / 2 + document.leftMargin(), document.bottom() - 10, 0);

	}

}
