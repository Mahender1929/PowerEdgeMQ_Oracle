package com.cebi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cebi.dao.CreateCsvDao;
import com.cebi.entity.QueryData;

@Service
public class CreateCsvServiceImpl implements CreateCsvService {

	@Autowired
	CreateCsvDao createCsvDao;

	@Override
	public byte[] downloadCsv(QueryData queryData, String bank, String merchantId) {
		return createCsvDao.downloadCsv(queryData, bank, merchantId);
	}

	@Override
	public byte[] downloadCsvPipeSeperator(QueryData queryData, String bank, String merchantId) {
		return createCsvDao.downloadCsvPipeSeperator(queryData, bank, merchantId);
	}

}
