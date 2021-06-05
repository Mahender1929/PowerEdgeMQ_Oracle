package com.cebi.dao;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cebi.entity.QueryData;
import com.cebi.utility.CebiConstant;
import com.cebi.utility.ConnectionException;
import com.cebi.utility.MappingConstant;
import com.cebi.utility.PdfUtils;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@Repository
@Transactional
public class CreateExcelDaoImpl extends PdfUtils implements CreateExcelDao {

	private static final Logger logger = Logger.getLogger(CreateExcelDaoImpl.class);

	@Autowired
	CebiConstant cebiConstant;
	@Autowired
	StaticReportDaoImpl staticReportDaoImpl;

	@Override
	public byte[] downloadExcel(QueryData queryData, String bank, String merchantId) {
		String parameter = "";
		String columns = "";
		String criteria = "";
		int colNum = 0;
		int rowcnt = 1;
		byte[] outArray = null;
		Session session = cebiConstant.getCurrentSession(bank);
		ResultSet resultSet = null;
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		
		//SXSSFWorkbook wb = new SXSSFWorkbook(); 
		//Sheet sheet = wb.createSheet();         


		HSSFCellStyle cellStyle = wb.createCellStyle();
		cellStyle.setWrapText(true);
		HSSFRow row = sheet.createRow(0);
		HSSFCell cell = row.createCell(0);

		HSSFFont font = wb.createFont();
		font.setBold(true);
		cellStyle.setFont(font);

		ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
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
		try {
			connection = ((SessionImpl) session).connection();
			prepareStatement = connection.prepareStatement(query);
			resultSet = prepareStatement.executeQuery();

			String lstparam = parameter.substring(0, (parameter.length() - 1));
			List<String> dbColumns = Arrays.asList(lstparam.split(","));
			List<String> columnLables = Arrays.asList(columns.split(","));
			if ("INCT".equalsIgnoreCase(queryData.getTable())) {
				ResultSetMetaData rsmd = resultSet.getMetaData();
				int columnCount = rsmd.getColumnCount();
				dbColumns=new ArrayList<String>();
				for (int i = 1; i <= columnCount; i++) {
					String labelName=rsmd.getColumnName(i);
					cell = row.createCell(colNum);
					cell.setCellStyle(cellStyle);
					cell = row.createCell(colNum);
					cell.setCellValue(labelName);
					++colNum;
					dbColumns.add(labelName);
				}
			} else
				for (String lbl : columnLables) {
					cell = row.createCell(colNum);
					cell.setCellStyle(cellStyle);
					cell = row.createCell(colNum);
					cell.setCellValue(lbl);
					++colNum;
				}
			while (resultSet.next()) {
				colNum = 0;
				row = sheet.createRow(rowcnt);
				for (String label : dbColumns) {
					label = label.contains("(") && label.contains(")") ? label.substring(label.indexOf('(') + 1, label.indexOf(')')) : label;
					cell = row.createCell(colNum);
					if (resultSet.getString(label) == null || resultSet.getString(label).isEmpty())
						cell.setCellValue("");
					else
						cell.setCellValue(resultSet.getString(label));
					++colNum;
				}
				++rowcnt;
			}
			wb.write(outByteStream);
			
		} catch (Exception e) {
			logger.info(e.getMessage());
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
		outArray = outByteStream.toByteArray();
		return outArray;
	}

}
