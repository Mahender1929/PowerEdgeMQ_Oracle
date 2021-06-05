package com.cebi.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cebi.entity.AppMessages;
import com.cebi.entity.ApplicationLabel;
import com.cebi.entity.ColumnNames;
import com.cebi.entity.QueryData;
import com.cebi.entity.ReportQueueData;
import com.cebi.entity.TableMetaData;
import com.cebi.utility.Block;
import com.cebi.utility.Board;
import com.cebi.utility.CebiConstant;
import com.cebi.utility.MappingConstant;
import com.cebi.utility.Table;
import com.cebi.utility.PdfUtils;

@Repository
@Transactional
public class CreateTextDaoImpl extends PdfUtils implements CreateTextDao {

	private static final Logger logger = Logger.getLogger(CreateExcelDaoImpl.class);

	@Autowired
	CebiConstant cebiConstant;

	@Override
	public byte[] downloadText(QueryData queryData, String bank, String merchantId) {
		logger.info("inside downloadText()..............!!!!!");
		String parameter = "";
		String criteria = "";
		String columns = ""; 
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		Session session = cebiConstant.getCurrentSession(bank);
		Statement statement = null;
		ResultSet resultSet = null;
		String query = null;
		byte[] output =  null;
		byte[] bytesArray = null;
		StringBuilder buff = new StringBuilder();
		
		parameter = queryData.getParameter().trim().length() > 0 ? queryData.getParameter() : "";
		criteria = queryData.getQuery().trim().length() > 0 ? queryData.getQuery() : "";
		columns = queryData.getColumnNames().trim().length() > 0 ? queryData.getColumnNames() : "";
		query = populateQuery(queryData, parameter, criteria, merchantId);
		logger.info("query  --- >  " + query);
		/*
		 * if (criteria != null && !criteria.isEmpty()) {
		 * validateTableCriteria(criteria, getTableData, tableMetaData,
		 * appMessages); }
		 */
		try {
			connection = ((SessionImpl) session).connection();
			prepareStatement = connection.prepareStatement(query);
			resultSet = prepareStatement.executeQuery();
			//statement = (Statement) connection.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			//statement.setFetchSize(5000);
			//resultSet = statement.executeQuery(query);
			StringBuilder buffer = new StringBuilder();
			ResultSetMetaData rsmd = resultSet.getMetaData();
			int columnCount = rsmd.getColumnCount();
			List<String> colummnName = new ArrayList<String>();
			List<Integer> colTitleSize = new ArrayList<>();
			for (int i = 1; i <= columnCount; i++) {
				colummnName.add(rsmd.getColumnName(i));
				colTitleSize.add(rsmd.getColumnName(i).length());
			}

			final List<List<String>> rowList = new LinkedList<List<String>>();
			int i = 0;
			List<Integer> arrySize = new ArrayList<>();

			while (resultSet.next()) {
				final List<String> columnList = new LinkedList<String>();
				rowList.add(columnList);

				for (int column = 1; column <= columnCount; ++column) {
					final Object value = resultSet.getObject(column);
					columnList.add(String.valueOf(value).trim());
				}
				i++;
			}
		Map<String, List<Integer>> sizeMap = new HashMap<>();

		for (int column = 0; column < columnCount; ++column) {
			List<Integer> len = new ArrayList<Integer>();
			for (int row = 0; row < rowList.size(); ++row) {
				List<String> al = rowList.get(row);
				String size = al.get(column).trim();
				len.add(size.length());
			}
			sizeMap.put(colummnName.get(column), len);
		}

		for (int column = 0; column < columnCount; ++column) {
			List<Integer> sz = sizeMap.get(colummnName.get(column));
			int maxSizeCol = Collections.max(sz);
			arrySize.add(maxSizeCol);
		}

		List<Integer> colWd1 = new ArrayList<>();
		
		for (int column = 0; column < columnCount; ++column) {
			if (arrySize.get(column) < colTitleSize.get(column)) {
				colWd1.add(colTitleSize.get(column) + 1);
			} else {
				colWd1.add(arrySize.get(column) + 1);
			}

		}
		Board board = new Board(1600);
		Table table = new Table(board, 1600, colummnName, rowList);
		table.setGridMode(Table.GRID_COLUMN);
		List<Integer> colAl = new ArrayList<>();
		for (int column = 1; column <= columnCount; ++column) {
			colAl.add(Block.DATA_TOP_LEFT);
		}
		
		table.setColWidthsList(colWd1);
		table.setColAlignsList(colAl);
		Block tableBlock = table.tableToBlocks();
		board.setInitialBlock(tableBlock);
		board.build();
		String tableString = board.getPreview();
		String tabStr = tableString.replaceAll("null", "----");
		String[] lines = tabStr.split("\\s*\\r?\\n\\s*");
		List al = Arrays.asList(lines);

		
		for (int s = 0; s < al.size(); s++) {
			buff.append(al.get(s));
			buff.append("\r\n");
		}
		File file = new File("cebi.csv");
		FileInputStream fis = new FileInputStream(file);
		bytesArray = new byte[(int) file.length()];
		fis.read(bytesArray); // read file into bytes[]
		fis.close();
		
		} catch (SQLException e) {
			logger.info(e.getMessage());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	 finally {
		closeConnection(resultSet,connection,prepareStatement);
	}
		output = String.valueOf(buff).getBytes();
		return output;

	}
	protected void closeConnection(ResultSet resultSet, Connection connection,
			PreparedStatement prepareStatement) {
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

}
/*
 * Date enddate = new Date(); ReportQueueData reportQueueData =
 * getReportQueueData(getTableData.getReportDataId());
 * reportQueueData.setFileName(filename);
 * reportQueueData.setTimecomplete(enddate);
 * reportQueueData.setTimetake(enddate.getTime() - date1.getTime() + "");
 * reportQueueData.setStatus(CebiConstant.COMPLETED);
 * reportQueueData.setTotalCount(i + "");
 * updateReportQueueData(reportQueueData);
 * populateAuditHistory(getTableData.getTable(), master1, query);
 * createZipFile(textFileLoc);
 */
//List<Integer> colWidthsList = Arrays.asList(20, 14, 13,
		// 14, 14);
		// List<Integer> colAlignList =
		// Arrays.asList(Block.DATA_CENTER, Block.DATA_CENTER,
		// Block.DATA_CENTER, Block.DATA_CENTER, Block.DATA_CENTER);

/* SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy"); 
 Date date = new Date(); 
String filename = formatter.format(date) + "_" + getTableNames(queryData.getTable()) + "_"
		+ queryData.getReportDataId() + ".txt";
String textFileLoc = MappingConstant.BANK_REPORT_LOCATION + bank + "/" + filename;
 
try (FileOutputStream fos = new FileOutputStream(filename)) {
	fos.write(output);
} catch (IOException ioe) {
	ioe.printStackTrace();
}
*/
/*ColumnNames field;
List<ColumnNames> names = new ArrayList<>();
List<AppMessages> appMessages = new ArrayList<>();
List<TableMetaData> data = new ArrayList<>();
TableMetaData tableMetaData = new TableMetaData();
List<ApplicationLabel> labels = null;
int lgth = 0;
List<String> stringList = new ArrayList<>();*/