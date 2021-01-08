package com.cebi.dao;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cebi.entity.AuditHistory;
import com.cebi.entity.ColumnNames;
import com.cebi.entity.RequiredField;
import com.cebi.entity.TellerMaster;
import com.cebi.utility.CebiConstant;

@Repository
public class StaticReportDaoImpl {

	@Autowired
	private SessionFactory sessionFactory;
	@Autowired
	CebiConstant cebiConstant;
	@Autowired
	AdminReportDao adminReportDao;
	@Autowired
	ApplicationLabelDao applicationLabelDao;

	public List<String> getAllTables() {
		return sessionFactory.getCurrentSession().createSQLQuery("SELECT distinct tablename  FROM statictablemetadata").list();
	}

	public List<ColumnNames> getColumnByTable(String table) {

		String TBL_GET_CLM_QRY = "SELECT columnname,datatype  FROM statictablemetadata WHERE tablename=:tablename ORDER BY columnname";
		ColumnNames columnName = null;
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		List<ColumnNames> names = new ArrayList<>();
		try {
			List<Object[]> object = sessionFactory.getCurrentSession().createSQLQuery(TBL_GET_CLM_QRY).setParameter("tablename", table).list();

			names = object.parallelStream().map(data -> new ColumnNames((String) data[0], (String) data[1])).collect(Collectors.toList());
			populateRequiredFiels(table, names);
			names.get(0).setAppLabels(applicationLabelDao.retrieveAllLabels());
		} catch (Exception e) {
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
		return names;
	}

	protected void populateRequiredFiels(String table, List<ColumnNames> names) {
		RequiredField fields = adminReportDao.populateFields(table);
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

	public List<String> getOriginalData(String data, String tableName) {
		return sessionFactory.getCurrentSession().createSQLQuery(" SELECT columnlabel FROM statictablemetadata WHERE tableName= '" + tableName + "' and columnname IN ('" + data + "')").list();
	}

	public List<Object[]> getOriginalDatamap(String data) {
		return sessionFactory.getCurrentSession().createSQLQuery(" SELECT  columnname,columnlabel FROM statictablemetadata ").list();
	}

	public String checkQueryType(String table) {
		return (String) sessionFactory.getCurrentSession().createSQLQuery("SELECT COUNT(1) FROM statictablemetadata WHERE tablename='" + table + "'").uniqueResult().toString();

	}

	public List<Object[]> getMandatoryField(String table) {
		return (List<Object[]>) sessionFactory.getCurrentSession().createSQLQuery("SELECT requiredFilledValue,mandatoryColumnName  FROM statictablemetadata WHERE  isRequired = 'M'and tablename='" + table + "'").list();
	}

	public List<String> getLocalViewFromDb() {
		System.out.println("SELECT DISTINCT tablename FROM statictablemetadata");
		return (List<String>) sessionFactory.getCurrentSession().createSQLQuery("SELECT DISTINCT tablename FROM statictablemetadata ").list();
	}

	public RequiredField populateManFields(String table) {
		String str = (String) sessionFactory.getCurrentSession().createSQLQuery("SELECT columnname FROM statictablemetadata WHERE isRequired = 'M' and tablename = '"+table+"'" ).list().stream().collect(Collectors.joining(","));
		RequiredField requiredField = new RequiredField();
		requiredField.setFiled(str);
		return requiredField;
	}
	
	@Transactional
	 public void populateAuditHistory(String table, TellerMaster master, String query) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
			AuditHistory audit = new AuditHistory();
			audit.setTellerId(Integer.parseInt(master.getTellerid()));
			audit.setBranchId(master.getBranchid());
			audit.setBankCode(master.getBankCode());
			audit.setQuery(query);
			audit.setTable(table);
			audit.setAudDate(simpleDateFormat.format(new Date()));
			sessionFactory.getCurrentSession().save(audit);
		    }
}
