/*package com.cebi.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.internal.SessionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cebi.entity.ApplicationLabel;
import com.cebi.entity.QueryData;
import com.cebi.entity.ReportQueueData;
import com.cebi.service.ApplicationLabelService;
import com.cebi.utility.CebiConstant;
import com.cebi.utility.ConnectionException;
import com.cebi.utility.PdfUtils;
import com.opencsv.CSVWriter;

@Repository
@Transactional
public class CreateCsvDaoImpl2 extends PdfUtils implements CreateCsvDao {

	private static final Logger logger = Logger.getLogger(PdfUtils.class);

	@Autowired
	CebiConstant cebiConstant;
	@Autowired
	StaticReportDaoImpl staticReportDaoImpl;
	@Autowired
	SessionFactory sessionFactory;
	@Autowired
	ApplicationLabelService applicationLabelService;

	@Override
	public byte[] downloadCsv(QueryData getTableData, String bank) {
		byte[] outArray = null;
		byte[] bytesArray = null;
		Session session = cebiConstant.getCurrentSession(bank);
		ResultSet resultSet = null;
		String parameter = "";
		String columns = "";
		String criteria = "";
		PreparedStatement prepareStatement = null;
		Connection connection = null;
		StringBuilder buffer = new StringBuilder();
		String query = null;
		Statement statement = null;
		List<ApplicationLabel> labels = null;

		File file = null;
		try {
			session = cebiConstant.getCurrentSession(bank);
			connection = ((SessionImpl) session).connection();
			parameter = getTableData.getParameter().trim().length() > 0 ? getTableData.getParameter() : "";
			criteria = getTableData.getQuery().trim().length() > 0 ? getTableData.getQuery() : "";
			labels = applicationLabelService.retrieveAllLabels();
			query = super.populateQuery(getTableData, parameter, criteria);
			System.out.println(query);
			prepareStatement = connection.prepareStatement(query);
			resultSet = prepareStatement.executeQuery();

			ResultSetMetaData rsmd = resultSet.getMetaData();
			int columnCount = rsmd.getColumnCount();
			List<String> colummnName = new ArrayList<String>();
			for (int i = 1; i <= columnCount; i++) {
				buffer.append(rsmd.getColumnName(i) + " , ");
				colummnName.add(rsmd.getColumnName(i));
			}
			for (String lbl : colummnName) {
				buffer.append(lbl + CebiConstant.COMMA);
			}
			file = new File("cebi.csv");
			CSVWriter writer = new CSVWriter(new FileWriter(file));
			writer.writeNext(columns.split(","));
			writer.writeAll(resultSet, false);
			writer.close();
			bytesArray = new byte[(int) file.length()];
			FileInputStream fis = new FileInputStream(file);
			fis.read(bytesArray); // read file into bytes[]
			fis.close();
		} catch (SQLException e) {
			logger.info(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (OutOfMemoryError error) {
			throw new ConnectionException("Failed to allocate Max memory...!");
		} finally {
			closeConnection(resultSet, connection, prepareStatement);
		}

		return file.toString().getBytes();
	}

	protected void closeConnection(ResultSet resultSet, Connection connection, PreparedStatement prepareStatement) {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException e) {  ignored 
			}
		}
		if (prepareStatement != null) {
			try {
				prepareStatement.close();
			} catch (SQLException e) {  ignored 
			}
		}
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {  ignored 
			}
		}
	}

	@Override
	public int addReportQueueData(ReportQueueData reportQueueData) {
		return (int) sessionFactory.getCurrentSession().save(reportQueueData);
	}

	@Override
	@Transactional
	public ReportQueueData getReportQueueData(int id) {
		logger.info(sessionFactory.getCurrentSession());
		return (ReportQueueData) sessionFactory.getCurrentSession().load(ReportQueueData.class, id);
		//return (ReportQueueData) sessionFactory.getCurrentSession().get(ReportQueueData.class, id);
	}

	@Override
	public byte[] downloadCsvpipe(QueryData getTableData, String bank) {
		byte[] outArray = null;
		byte[] bytesArray = null;
		Session session = cebiConstant.getCurrentSession(bank);
		ResultSet resultSet = null;
		String parameter = "";
		String columns = "";
		String criteria = "";
		PreparedStatement prepareStatement = null;
		Connection connection = null;
		StringBuilder buffer = new StringBuilder();
		String query = null;
		Statement statement = null;
		List<ApplicationLabel> labels = null;

		File file = null;
		try {
			session = cebiConstant.getCurrentSession(bank);
			connection = ((SessionImpl) session).connection();
			parameter = getTableData.getParameter().trim().length() > 0 ? getTableData.getParameter() : "";
			criteria = getTableData.getQuery().trim().length() > 0 ? getTableData.getQuery() : "";
			labels = applicationLabelService.retrieveAllLabels();
			query = super.populateQuery(getTableData, parameter, criteria);

			prepareStatement = connection.prepareStatement(query);
			resultSet = prepareStatement.executeQuery();

			ResultSetMetaData rsmd = resultSet.getMetaData();
			int columnCount = rsmd.getColumnCount();
			List<String> colummnName = new ArrayList<String>();
			for (int i = 1; i <= columnCount; i++) {
				buffer.append(rsmd.getColumnName(i) + " , ");
				colummnName.add(rsmd.getColumnName(i));
			}
			for (String lbl : colummnName) {
				buffer.append(lbl + CebiConstant.COMMA);
			}
			file = new File("cebi.csv");
			CSVWriter writer = new CSVWriter(new FileWriter(file), '|', CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
			writer.writeAll(resultSet, false);
			writer.close();
			bytesArray = new byte[(int) file.length()];
			FileInputStream fis = new FileInputStream(file);
			fis.read(bytesArray); // read file into bytes[]
			fis.close();
		} catch (SQLException e) {
			logger.info(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (OutOfMemoryError error) {
			throw new ConnectionException("Failed to allocate Max memory...!");
		} finally {
			closeConnection(resultSet, connection, prepareStatement);
		}

		return file.toString().getBytes();
	}
}
*/