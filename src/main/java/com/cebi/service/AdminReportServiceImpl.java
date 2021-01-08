package com.cebi.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cebi.controller.AdminReportController;
import com.cebi.dao.AdminReportDao;
import com.cebi.dao.StaticReportDaoImpl;
import com.cebi.entity.Banks;
import com.cebi.entity.QueryData;
import com.cebi.entity.ReportQueueData;
import com.cebi.entity.TableMetaData;
import com.cebi.entity.TellerMaster;
import com.cebi.entity.ViewInfo;
import com.cebi.rabbitqueue.Events;
import com.cebi.utility.ApplicationLabelCache;
import com.cebi.utility.CebiConstant;
import com.cebi.utility.Constants;

@Service
@Transactional
public class AdminReportServiceImpl implements AdminReportService {

    @Autowired
    AdminReportDao adminReportDao;

    @Autowired
    AdminTableMetaDataService adminTableMetaDataService;
   
    @Autowired
    private StaticReportDaoImpl staticReportDaoImpl;
    
    @Autowired 
    private EventPublisherService eventPublisherService;
   
    private static final Logger logger = Logger.getLogger(AdminReportController.class);

  
     public List<TableMetaData> getTableData(QueryData getTableData, String bank, TellerMaster master) throws Exception {
	 return (List<TableMetaData>)buildSqlQuery(getTableData, bank, master);
    }

    public List<TableMetaData> populateDbTables(String bank) {
	List<TableMetaData> tables = null;
	Map<String, List<TableMetaData>> cache = ApplicationLabelCache.getViewsInstance();
	if (cache.get("views") == null) {
	    tables = adminTableMetaDataService.retrieveDbTables(bank);
	    cache.put("views", tables);
	} else {
	    for (Map.Entry<String, List<TableMetaData>> entry : cache.entrySet()) {
		tables = entry.getValue();
	    }
	}
	return tables;
    }

    @Override
    public List<Banks> retreiveBankNames() {
	List<Banks> banks = null;
	banks = adminReportDao.retreiveBankNames();
	return banks;
    }

    @Override
    public Map<String, List<String>> populateBankDbDetails(List<Banks> banks) {
	List<String> list = null;
	Map<String, List<String>> bankDetails = ApplicationLabelCache.getBankDbDetailsInstance();
	if (banks != null && banks.size() > 0) {
	    for (Banks bank : banks) {
		list = new ArrayList<String>();
		list.add(bank.getDriverClass());
		list.add(bank.getDatabaseUrl());
		list.add(bank.getUsername());
		list.add(bank.getPassword());
		bankDetails.put(bank.getBankCode(), list);
	    }
	}
	return bankDetails;
    }

    @Override
    public Banks populateBankDbDetail(String bank) {
	return adminReportDao.retreiveDbConnection(bank);
    }

    @Override
    public List<ViewInfo> retreiveViewDetails(String bankcode) {
	String type = adminReportDao.populateBankType(bankcode);
	if (type != null)
	    return adminReportDao.retreiveViewDetails(type);
	else
	    return null;
    }

    @Override
    public Object buildSqlQuery(QueryData getTableData, String bank, TellerMaster tellerMaster) throws Exception {
	logger.info("----------------buildSqlQuery----------------- " + getTableData.getTable());
	//System.out.println("----------------buildSqlQuery----------------- " + getTableData.getTable());
	String variable = "";
	String countValue = staticReportDaoImpl.checkQueryType(getTableData.getTable());
	if ("0".equalsIgnoreCase(countValue)) {
	    logger.info("view Data Getting ");
	    	 return adminReportDao.populateDataTable(getTableData, bank, tellerMaster);
	} else {
	    logger.info("table Data Getting ");
	    if (getTableData.getParameter() != null && getTableData.getParameter().length() > 0) {
		variable = getTableData.getParameter().substring(0, getTableData.getParameter().length() - 1);
		variable = variable.replace(",", "','");
		List<String> data = staticReportDaoImpl.getOriginalData(variable, getTableData.getTable());
		getTableData.setParameter(data.stream().filter(data1 -> data1.trim().length() > 1).map(i -> i.toString()).collect(Collectors.joining(",")));
	    }
	    List<Object[]> mandatoryFeildValues = staticReportDaoImpl.getMandatoryField(getTableData.getTable());
	    int mandatoryFieldCount = mandatoryFeildValues.size();
	    String mandatory = "";
	    String rumnum = "  ";
	    if (getTableData.getQuery() != null && getTableData.getQuery().length() > 0) {
		mandatory = getTableData.getQuery();
	    } else {
		rumnum = CebiConstant.QRY_ROWNUM;
	    }

	    mandatory = replaceCriteriaField(mandatory, getTableData.getTable());

	    for (int i = 0; i < mandatoryFieldCount; i++) {
		Object[] mandatoryField = mandatoryFeildValues.get(i);
		if (!getTableData.getQuery().contains((String) mandatoryField[1])) {
		    if (i == 0)
			if (getTableData.getQuery() == null && getTableData.getQuery().length() == 0)
			    mandatory = mandatory + (String) mandatoryField[0] + "  AND ";
			else if (getTableData.getQuery() != null && getTableData.getQuery().trim().length() == 0 && mandatoryFieldCount == 1)
			    mandatory = mandatory + (String) mandatoryField[0] + "    ";
			else if (getTableData.getQuery() != null && getTableData.getQuery().trim().length() == 0)
			    mandatory = mandatory + (String) mandatoryField[0] + " AND   ";
			else if (mandatoryFieldCount == 1)
			    mandatory = mandatory + "   AND  " + (String) mandatoryField[0] + "   ";
			else
			    mandatory = mandatory + "   AND  " + (String) mandatoryField[0] + "  AND ";
		    else if (i != (mandatoryFieldCount - 1))
			mandatory = mandatory + (String) mandatoryField[0] + " AND ";
		    else
			mandatory = mandatory + (String) mandatoryField[0] + "  ";
		}
	    }

	    if (!"SIMPLE".equalsIgnoreCase(getTableData.getReporttype()))
		mandatory = mandatory + rumnum;
	    getTableData.setQuery(mandatory);
	    return adminReportDao.populateDataTable(getTableData, bank, tellerMaster);
	}
    }

    private String replaceCriteriaField(String mandatory, String table) {
	List<Object[]> obj = adminReportDao.replaceCriteriaField(table);
	for (Object[] data : obj) {
	    if ((String) data[1] != null && ((String) data[1]).trim().length() != 0)
		if (mandatory.contains((String) data[0])) {
		    mandatory = mandatory.replace((String) data[0], (String) data[1]);
		}
	}
	return mandatory;
    }

    private String chekrum(String mandatory) {
	if (mandatory.trim().endsWith("AND")) {
	    return "   ROWNUM < 100";
	} else {
	    return " AND  ROWNUM < 100 ";
	}
    }

    @Override
    public byte[] csvDownloadQueue(QueryData queryData, String bank, TellerMaster master) throws Exception {
	logger.info("Enter Into csvDownloadQueue Service");
	queryData.setReporttype(Constants.CSV);
	return (byte[]) buildSqlQuery(queryData, bank, master);
    }

    @Override
    public void updatereportStatus(int id, String inProcess) {
	adminReportDao.updatereportStatus(id, inProcess);
    }
    
	@Override
	public Map<String, Object> commonDownloadQueue(QueryData queryData, String bank, TellerMaster master) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ReportQueueData datQueueData = new ReportQueueData();
		datQueueData.setQueuedataid(queryData);
		datQueueData.setStatus("IN QUEUE");
		datQueueData.setTimeadded(new Date());
		datQueueData.setBank(bank.trim());
		int i = adminReportDao.addReportQueueData(datQueueData);
		String msg = " File is Added in Queue Succesfully with  ID :" + i;
		
		try {
			if (queryData.getReporttype().equalsIgnoreCase("csv")) {
				eventPublisherService.publishEvent(Events.CSV, i, master);
				msg = Events.CSV.toString()+ msg;
			} else if (queryData.getReporttype().equalsIgnoreCase("csvpipe")){
				eventPublisherService.publishEvent(Events.CSVPIPE, i, master);
				msg = Events.CSVPIPE.toString()+ msg;
			} else if (queryData.getReporttype().equalsIgnoreCase("xls")) {
				eventPublisherService.publishEvent(Events.EXCEL, i, master);
				msg = Events.EXCEL.toString()+ msg;
			} else if(queryData.getReporttype().equalsIgnoreCase("text")){
				eventPublisherService.publishEvent(Events.TEXT, i, master);
				msg = Events.TEXT.toString()+ msg;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		map.put("msg", msg);
		return map;
	}

    @Override
    public byte[] downloadExcel(QueryData queryData, String bank,  TellerMaster master) throws Exception {
	queryData.setReporttype(Constants.EXCEL);
	return (byte[]) buildSqlQuery(queryData, bank, master);
    }

    @Override
    public byte[] downloadCsvPipeSeperator(QueryData queryData, String bank, TellerMaster master) throws Exception {
	queryData.setReporttype(Constants.CSVPIPE);
	return (byte[])buildSqlQuery(queryData, bank, master);
    }
 

    @Override
    public byte[] downloadPdf(QueryData queryData, String bank,  TellerMaster master) throws Exception {
	queryData.setReporttype(Constants.TEXT);
	return (byte[]) buildSqlQuery(queryData, bank, master);
    }

	@Override
	public BigDecimal getReportQueueStatusCount(String bank, String date) {
		return adminReportDao.getReportQueueStatusCount(bank, date);
	}

	@Override
	public  LinkedHashMap<String, Integer> getTotalCount(String bankCode) {
		return adminReportDao.getTotalCount(bankCode);
	}



}
