package com.cebi.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.internal.SessionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cebi.entity.ApplicationLabel;
import com.cebi.entity.ColumnNames;
import com.cebi.entity.RequiredField;
import com.cebi.entity.TableMetaData;
import com.cebi.utility.CebiConstant;

@Repository
@Transactional
public class AdminTableMetaDataDaoImpl implements AdminTableMetaDataDao {

	private static final Logger logger = Logger.getLogger(AdminTableMetaDataDaoImpl.class);


	@Autowired
	ApplicationLabelDao applicationLabelDao;

	@Autowired
	CebiConstant cebiConstant;

	@Autowired
	AdminReportDao adminReportDao;

	@Autowired
	StaticReportDaoImpl staticReportDaoImpl;

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public List<TableMetaData> retrieveDbTables(String bank, String merchantId) {
		List<TableMetaData> tableNames = new ArrayList<TableMetaData>();
		Connection connection = null;
		ResultSet resultSet = null;
		TableMetaData tableMetaData = null;
		PreparedStatement prepareStatement = null;
		Session session = cebiConstant.getCurrentSession(bank);
		connection = ((SessionImpl) session).connection();
		List<ApplicationLabel> labels = applicationLabelDao.retrieveAllLabels();
		try {
			prepareStatement = connection.prepareStatement("select view_name from user_views");
			resultSet = prepareStatement.executeQuery();
			tableMetaData = new TableMetaData();
			while (resultSet.next()) {
				tableMetaData = new TableMetaData();
				String viewName = resultSet.getString("view_name");
				if (!viewName.equalsIgnoreCase("NPA_CUSTOMERS")) {
					if (merchantVadilate(merchantId, viewName, connection)) {
						tableMetaData.setTableName(viewName.trim());
						setTableLabel(labels, viewName.trim(), tableMetaData);
						tableNames.add(tableMetaData);
						/*
						 * tableMetaData.setTableName(resultSet.getString(
						 * "view_name").trim());
						 * setTableLabel(labels,resultSet.getString("view_name")
						 * .trim(),tableMetaData);
						 * tableNames.add(tableMetaData);
						 */
					}
				}
			}
		} catch (SQLException e) {
			logger.info("Exception in retrieveDbTables() Method:: " + e.getMessage());
			e.getMessage();
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					/* ignored */}
			}
			if (prepareStatement != null) {
				try {
					prepareStatement.close();
				} catch (SQLException e) {
					/* ignored */}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					/* ignored */}
			}
		}
		if (tableNames.size() > 0) {
			tableNames.get(0).setAppLabels(labels);
		}
		return tableNames;
	}

	private boolean merchantVadilate(String merchantId, String viewName, Connection connection) {
		String sql = "select count(*) as count from " + viewName + " where merchant_id='" + merchantId + "'";
		PreparedStatement prepareStatement = null;
		ResultSet rs = null;
		try {
			prepareStatement = connection.prepareStatement(sql);
			rs = prepareStatement.executeQuery();
			while (rs.next()) {
				if (rs.getInt("count") > 0) {
					return true;
				}
			}
		} catch (SQLException e) {
			logger.info(e.getMessage());
		}
		return false;
	}

	@Override
	public Map<String, String> retrieveDbTable(String bank) {
		Map<String, String> map = new HashMap<>();
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement prepareStatement = null;
		Session session = cebiConstant.getCurrentSession(bank);
		connection = ((SessionImpl) session).connection();
		try {
			prepareStatement = connection.prepareStatement("select view_name from user_views");
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				String viewName = resultSet.getString("view_name");
				map.put(resultSet.getString("view_name"), resultSet.getString("view_name"));
				System.out.println("view name==" + resultSet.getString("view_name"));
			}
			int size = 0;
			if (resultSet != null) {
				resultSet.last(); // moves cursor to the last row
				size = resultSet.getRow(); // get row id
			}
			System.out.println("Result Set Size===" + size);
		} catch (SQLException e) {
			logger.info("Exception in retrieveDbTables() Method:: " + e.getMessage());
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
		return map;
	}

	private void setTableLabel(List<ApplicationLabel> labels, String string, TableMetaData tableMetaData) {
		if (addLabels(labels, string) != null) {
			tableMetaData.setName(addLabels(labels, string));
		} else {
			tableMetaData.setName(string);
		}

	}

	public String addLabels(List<ApplicationLabel> labels, String tableName) {
		String label = null;
		for (ApplicationLabel lbl : labels) {
			if (lbl.getLabelCode().equalsIgnoreCase(tableName)) {
				label = lbl.getAppLabel();
				break;
			}
		}
		return label;

	}

	@Override
	public List<ColumnNames> retrieveTableColumns(String table, String bank) {
		ColumnNames columnName = null;
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		List<ColumnNames> names = new ArrayList<>();
		String checkInMysql = staticReportDaoImpl.checkQueryType(table);
		if ("0".equalsIgnoreCase(checkInMysql)) {
			String TBL_GET_CLM_QRY = "SELECT COLUMN_NAME,DATA_TYPE FROM ALL_TAB_COLUMNS WHERE  table_name = " + "'"
					+ table.toUpperCase() + "' order by COLUMN_NAME";
			Session session = cebiConstant.getCurrentSession(bank);
			try {
				connection = ((SessionImpl) session).connection();
				prepareStatement = connection.prepareStatement(TBL_GET_CLM_QRY);
				resultSet = prepareStatement.executeQuery();
				while (resultSet.next()) {
					if (!resultSet.getString("COLUMN_NAME").equalsIgnoreCase("VAR_AREA")
							&& !resultSet.getString("COLUMN_NAME").equalsIgnoreCase("OD_VISA_AREA")) {
						columnName = new ColumnNames();
						columnName.setDataType(resultSet.getString("DATA_TYPE"));
						columnName.setName(resultSet.getString("COLUMN_NAME"));
						names.add(columnName);
					}
				}
			} catch (Exception e) {
				logger.info("Exception in retrieveTableColumns() Method :: " + e.getMessage());
				e.printStackTrace();
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
		} else {
			String TBL_GET_CLM_QRY = "SELECT columnname,datatype  FROM statictablemetadata WHERE  tablename=:tablename ORDER BY columnname";
			try {
				List<Object[]> object = sessionFactory.getCurrentSession().createSQLQuery(TBL_GET_CLM_QRY)
						.setParameter("tablename", table).list();
				names = object.parallelStream().map(data -> new ColumnNames((String) data[0], (String) data[1]))
						.collect(Collectors.toList());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		populateRequiredFiels(table, names);
		populateMandatoryFiels(table, names);
		names.get(0).setAppLabels(applicationLabelDao.retrieveAllLabels());
		return names;
	}

	protected void populateRequiredFiels(String table, List<ColumnNames> names) {
		RequiredField fields = adminReportDao.populateFields(table);
		logger.info("populateRequiredFiels====" + fields);
		if (fields.getFiled() != null) {
			for (String str : fields.getFiled().split(",")) {
				for (ColumnNames columnName : names) {
					if (columnName.getName().equalsIgnoreCase(str)) {
						columnName.setRequired("Y");
						break;
					}
				}
			}
		}
	}

	protected void populateMandatoryFiels(String table, List<ColumnNames> names) {
		logger.info("table name=====" + table.toString());
		logger.info("names=====" + names.toString());
		RequiredField fields = staticReportDaoImpl.populateManFields(table);
		logger.info("populateMandatoryFiels====" + fields);
		if (fields.getFiled() != null) {
			for (String str : fields.getFiled().split(",")) {
				for (ColumnNames columnName : names) {
					if (columnName.getName().equalsIgnoreCase(str)) {
						columnName.setRequired("M");
						break;
					}
				}
			}
		}
	}
}
