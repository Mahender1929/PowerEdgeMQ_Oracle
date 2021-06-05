package com.cebi.dao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.cebi.entity.Banks;
import com.cebi.entity.QueryData;
import com.cebi.entity.ReportQueueData;
import com.cebi.entity.RequiredField;
import com.cebi.entity.TableMetaData;
import com.cebi.entity.TellerMaster;
import com.cebi.entity.ViewInfo;

public interface AdminReportDao {

	public Object populateDataTable(QueryData getTableData, String bank, TellerMaster master) throws Exception;
	public String populateQuery(QueryData table, String param, String criteria, String merchantid);
	public RequiredField populateFields(String table);
	public void saveFavouriteQuery(QueryData data);
	public List<QueryData> retrieveFavouriteList(String data);
	public List<QueryData> getReportDetails(int id);
	public List<Banks> retreiveBankNames();
	public Banks retreiveDbConnection(String bank);

	/* public Map<String, List<BranchInformation>> populateBranchInformation(); */
	public List<ViewInfo> retreiveViewDetails(String type);
	public String populateBankType(String bankcode);
	int addReportQueueData(ReportQueueData reportQueueData);
	ReportQueueData getReportQueueData(int id);
	void updateReportQueueData(ReportQueueData reportQueueData);
	public void updatereportStatus(int id, String inProcess);
	public List<Object[]> replaceCriteriaField(String table);
	public BigDecimal getReportQueueStatusCount(String bank, String date);
	public  LinkedHashMap<String, Integer> getTotalCount(String bankCode);
	public int deleteFavRec(int id);

}
