package com.cebi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cebi.dao.AdminTableMetaDataDao;
import com.cebi.dao.ApplicationLabelDao;
import com.cebi.dao.StaticReportDaoImpl;
import com.cebi.entity.ColumnNames;
import com.cebi.entity.TableMetaData;

@Service
@Transactional
public class AdminTableMetaDataServiceImpl implements AdminTableMetaDataService {

	@Autowired
	AdminTableMetaDataDao adminTableMetaDataDao;

	@Autowired
	ApplicationLabelDao applicationLabelDao;

	@Autowired
	StaticReportDaoImpl staticReportDaoImpl;

	@Override
	public List<TableMetaData> retrieveDbTables(String bank,String merchantId) {
		return adminTableMetaDataDao.retrieveDbTables(bank,merchantId);
	}

	@Override
	public List<ColumnNames> retrieveTableColumns(String table, String bank) {
		return adminTableMetaDataDao.retrieveTableColumns(table, bank);
	}

}
