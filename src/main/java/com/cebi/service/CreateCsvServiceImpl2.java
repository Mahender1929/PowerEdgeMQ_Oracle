/*package com.cebi.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cebi.dao.CreateCsvDao;
import com.cebi.dao.StaticReportDaoImpl;
import com.cebi.entity.QueryData;
import com.cebi.utility.CebiConstant;

@Service
@Transactional
public class CreateCsvServiceImpl2 implements CreateCsvService {

	@Autowired
	CreateCsvDao createCsvDao;

	@Autowired
	private StaticReportDaoImpl staticReportDaoImpl;

	@Autowired
	private EventPublisherService eventPublisherService;

	@Override
	public byte[] downloadCsv(QueryData getTableData, String bank) {
		String variable = "";
		String countValue = staticReportDaoImpl.checkQueryType(getTableData.getTable());
		if ("0".equalsIgnoreCase(countValue)) {
			return createCsvDao.downloadCsv(getTableData, bank);
		} else {
			if (getTableData.getParameter() != null && getTableData.getParameter().length() > 0) {
				variable = getTableData.getParameter().substring(0, getTableData.getParameter().length() - 1);
				variable = variable.replace(",", "','");
				List<String> data = staticReportDaoImpl.getOriginalData(variable, getTableData.getTable());
				getTableData.setParameter(data.stream().filter(data1 -> data1.trim().length() > 1).map(i -> i.toString()).collect(Collectors.joining(",")));
			}
			List<Object[]> mandatoryFeildValues = staticReportDaoImpl.getMandatoryField(getTableData.getTable());
			int mandatoryFieldCount = mandatoryFeildValues.size();
			String mandatory = "";
			String rumnum = " ";
			if (getTableData.getQuery() != null && getTableData.getQuery().length() > 0) {
				mandatory = getTableData.getQuery();
				rumnum = CebiConstant.QRY_ROWNUM;
			}
			for (int i = 0; i < mandatoryFieldCount; i++) {
				Object[] mandatoryField = mandatoryFeildValues.get(i);
				if (!getTableData.getQuery().contains((String) mandatoryField[1])) {
					if (i != (mandatoryFieldCount - 1))
						mandatory = mandatory + (String) mandatoryField[0] + " AND ";
					else if (i == 0)
						mandatory = mandatory + (String) mandatoryField[0] + "  AND ";
					else
						mandatory = mandatory + (String) mandatoryField[0] + "  ";
				}
			}
			mandatory = mandatory + rumnum;
			getTableData.setQuery(mandatory);
			return createCsvDao.downloadCsv(getTableData, bank);
		}

	}

	@Override
	public byte[] downloadCsvPipeSeperator(QueryData getTableData, String bank) {
		String variable = "";
		String countValue = staticReportDaoImpl.checkQueryType(getTableData.getTable());
		if ("0".equalsIgnoreCase(countValue)) {
			return createCsvDao.downloadCsvpipe(getTableData, bank);
		} else {
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
				rumnum = CebiConstant.QRY_ROWNUM;
			}
			for (int i = 0; i < mandatoryFieldCount; i++) {
				Object[] mandatoryField = mandatoryFeildValues.get(i);
				if (!getTableData.getQuery().contains((String) mandatoryField[1])) {
					if (i != (mandatoryFieldCount - 1))
						mandatory = mandatory + (String) mandatoryField[0] + " AND ";
					else if (i == 0)
						mandatory = mandatory + (String) mandatoryField[0] + "  AND ";
					else
						mandatory = mandatory + (String) mandatoryField[0] + "  ";
				}
			}
			mandatory = mandatory + rumnum;
			getTableData.setQuery(mandatory);
			return createCsvDao.downloadCsvpipe(getTableData, bank);
		}

	}

	@Override
	public int csvDownloadQueue(QueryData queryData, String bank) {
		return 0;
	}

	@Override
	public int downloadCsv1(QueryData queryData, String bank) {
		return 0;
	}
}
*/