package com.cebi.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.internal.SessionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cebi.entity.AuditHistory;
import com.cebi.entity.Banks;
import com.cebi.entity.TellerMaster;
import com.cebi.entity.ViewInfo;
import com.cebi.service.AdminReportService;
import com.cebi.utility.CebiConstant;

@Repository
public class LoginUserDaoImpl implements LoginDao {

	private static final Logger logger = Logger.getLogger(LoginUserDaoImpl.class);

	@Autowired
	SessionFactory sessionFactory;

	@Autowired
	CebiConstant cebiConstant;
	
	@Autowired
	AdminTableMetaDataDao adminTableMetaDataDao;
	
	@Autowired
	AdminReportService adminReportService;

	@SuppressWarnings("unchecked")
	@Transactional()
	public List<Object[]> validateLoginUser(TellerMaster tellerMaster) {
		logger.info("inside validatLoginUser start time::" + System.currentTimeMillis());
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("SELECT T.tellerid,T.ip,T.branchid,T.bankCode,T.ccdp,T.tellertype,T.merchantLogin,T.merchantId FROM TellerMaster T WHERE tellerid=:tellerid AND pwd=:pwd");
		query.setParameter("tellerid", tellerMaster.getTellerid());
		query.setParameter("pwd", tellerMaster.getPwd());
		List<Object[]> results = (List<Object[]>) query.list();
		logger.info("inside validatLoginUser end time::" + System.currentTimeMillis());
		return results;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Object[]> validateSuperLoginUser(TellerMaster tellerMaster) {
		logger.info("inside validateSuperLoginUser start time::" + System.currentTimeMillis());
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("SELECT T.tellerid,T.ip,T.branchid,T.bankCode,T.ccdp,T.tellertype,T.merchantLogin,T.merchantId FROM TellerMaster T WHERE tellerid=:tellerid AND pwd=:pwd AND bankCode=:bankcode");
		query.setParameter("tellerid", tellerMaster.getTellerid());
		query.setParameter("pwd", tellerMaster.getPwd());
		query.setParameter("bankcode", tellerMaster.getBankCode());
		
		List<Object[]> results = (List<Object[]>) query.list();
		logger.info("inside validateSuperLoginUser end time::" + System.currentTimeMillis());
		System.out.println(results.size());
		return results;
	}

	@Transactional
	public boolean runScript(String bankName) {
		Banks db = adminReportService.populateBankDbDetail(bankName);
		Session session = cebiConstant.getCurrentSession(bankName, db);
		if (session != null) {
			List<ViewInfo> views = null;
			views = populateViews(db); //To display local views from database(MySQL)
			//List<String> dbviews=adminTableMetaDataDao.retrieveDbViews(bankName,merchantId);
			Map<String, String> dbmap = adminTableMetaDataDao.retrieveDbTable(bankName);
			Map<String, String> excludeExceptions = populateExceptions();
			Connection connection = ((SessionImpl) session).connection();
			for (ViewInfo info : views) {
				String view = info.getView();
				String viewname = view.substring(view.indexOf("VIEW") + 5, view.indexOf("AS SELECT") - 1);
				try {
					if (!dbmap.containsKey(viewname.trim())) {
						PreparedStatement prepareStatement = connection.prepareStatement(view);
						prepareStatement.executeUpdate();
					}

				} catch (SQLException e) {
					logger.error(e.getMessage());
				}
			}
			return true;
		} else {
			return false;
		}
	}

	@Transactional
	protected List<ViewInfo> populateViews(Banks db) {
		Session localSession = sessionFactory.getCurrentSession();
		List<ViewInfo> viewInfos = new ArrayList<ViewInfo>();
		ViewInfo viewInfo = null;
		Query query = null;
		if (db != null && db.getBanktype().equalsIgnoreCase("NB")) {
			query = localSession.createQuery("SELECT id,viewName,view,type,status,access FROM ViewInfo WHERE access NOT IN (2) AND STATUS NOT IN('deactive')");
		} else {
			query = localSession.createQuery("SELECT id,viewName,view,type,status,access FROM ViewInfo WHERE access NOT IN (0) AND STATUS NOT IN('deactive')");
		}

		List<Object[]> objectList = (List<Object[]>) query.list();

		for (Object[] object : objectList) {
			viewInfo = new ViewInfo();
			viewInfo.setId((Integer) object[0]);
			viewInfo.setViewName(object[1].toString());
			viewInfo.setView(object[2].toString());
			viewInfo.setType(object[3].toString());
			viewInfo.setStatus(object[4].toString());
			viewInfos.add(viewInfo);
		}
		return viewInfos;
	}

	protected Map<String,String> populateExceptions(){
		Map<String,String> exceptions = new HashMap<String, String>();
		exceptions.put(CebiConstant.NAME_ALREADY_USED_ERR, CebiConstant.NAME_ALREADY_USED_ERR);
		exceptions.put(CebiConstant.SERVICE_REQUESTED_CONNECT_ERR, CebiConstant.SERVICE_REQUESTED_CONNECT_ERR);
		exceptions.put(CebiConstant.Communication_Channel, CebiConstant.Communication_Channel);
		exceptions.put( CebiConstant.VIEW_DOESNOT_EXIST, CebiConstant.VIEW_DOESNOT_EXIST);
		return exceptions;
	}

	@Override
	@Transactional
	public List<String> checkbankcode(String database_url,TellerMaster master) {
		
		List<String> banks = new ArrayList<>();
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("SELECT bankCode FROM Banks  WHERE databaseUrl = :database_url");
		String q="SELECT bankCode FROM Banks  WHERE databaseUrl = :database_url";
		query.setParameter("database_url", database_url);
		banks = (List<String>) query.list();
		populateAuditHistory("LOGIN", master, q);
		return banks;
	}
	
	 protected void populateAuditHistory(String table, TellerMaster master, String query) {
	    	logger.info("populateAuditHistory");
	    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		AuditHistory audit = new AuditHistory();
		audit.setTellerId(Integer.parseInt(master.getTellerid()));
		//audit.setBranchId(master.getBranchid());
		audit.setBankCode(master.getBankCode());
		audit.setQuery(query);
		audit.setTable(table);
		audit.setAudDate(simpleDateFormat.format(new Date()));
		audit.setDburl(master.getIp());
		sessionFactory.getCurrentSession().save(audit);
		logger.info("populateAuditHistory exit");
	    }
}



/*@Transactional
public boolean runScript(String bankName) {
	Banks db = adminReportService.populateBankDbDetail(bankName);
	Session session = cebiConstant.getCurrentSession(bankName, db);
	List<String> views = new ArrayList<String>();
	if (db != null && db.getBanktype().equalsIgnoreCase("NB")){
		views.add(CebiConstant.CREATE_GLINF_VIEW_NB);
		views.add(CebiConstant.CREATE_CUSTOMER_VIEW_NB);
	}else{
		views.add(CebiConstant.CREATE_CUSTOMER_VIEW);
		views.add(CebiConstant.CREATE_GLINF_VIEW);
	}
	
	

	views.add(CebiConstant.CREATE_BGLTRNS_VIEW);
	views.add(CebiConstant.CREATE_DEMAND_DEPOSIT_VIEW);
	views.add(CebiConstant.CREATE_TIME_DEPOSIT_VIEW);
	views.add(CebiConstant.CREATE_CASHCREDIT_OVERDRAFT_VIEW);
	views.add(CebiConstant.CREATE_LOANS_VIEW);
	views.add(CebiConstant.CREATE_NPA_CUSTOMERS_VIEW);
	views.add(CebiConstant.CREATE_ONE_NPA_ALL_VIEW);
	views.add(CebiConstant.CREATE_LOC_MAS_VIEW);
	
	
	views.add(CebiConstant.CREATE_ATMCARD_VIEW);
	views.add(CebiConstant.CREATE_IMPS_TRANS_VIEW);
	views.add(CebiConstant.CREATE_RTGS_TRANS_VIEW);
	views.add(CebiConstant.CREATE_LOAN_PARAM_VIEW);
	views.add(CebiConstant.CREATE_CCOD_PARAM_VIEW);
	views.add(CebiConstant.CREATE_SBCA_PARAM_VIEW);
	views.add(CebiConstant.CREATE_FD_PARAM_VIEW);
	views.add(CebiConstant.CREATE_DEPOSIT_TRNS_VIEW);
	views.add(CebiConstant.CREATE_LOAN_TRANS_VIEW);
	views.add(CebiConstant.CREATE_UIDL_MASTER_VIEW);
	views.add(CebiConstant.CREATE_TAX_MASTER_VIEW);
	views.add(CebiConstant.CREATE_CUID_MASTER_VIEW);
	views.add(CebiConstant.CREATE_MCAD_MASTER_VIEW);
	views.add(CebiConstant.CREATE_SI_MASTER_VIEW);
	views.add(CebiConstant.CREATE_COLM_MASTER_VIEW);
	
	Map<String, String> dbmap = adminTableMetaDataDao.retrieveDbTable(bankName);
	Map<String,String> excludeExceptions=populateExceptions();
	Connection connection = ((SessionImpl) session).connection();
	for (String view : views) {
		String viewname = view.substring(view.indexOf("VIEW") + 5, view.indexOf("AS SELECT") - 1);
		try {
			if (!dbmap.containsKey(viewname.trim())) {
				PreparedStatement prepareStatement = connection.prepareStatement(view);
				prepareStatement.executeUpdate();
			}

		} catch (SQLException e) {
			logger.error(e.getMessage());
			if (!e.getMessage().trim().equals(CebiConstant.NAME_ALREADY_USED_ERR) && !e.getMessage().trim().equals(CebiConstant.SERVICE_REQUESTED_CONNECT_ERR)) {
				throw new ConnectionException(e.getMessage());
			}
		}
		catch (SQLException e) {
			logger.error(e.getMessage());
			if (!excludeExceptions.containsKey(e.getMessage().trim())) {
				throw new ConnectionException(e.getMessage());
			}
		}
	}
	return true;
}*/
