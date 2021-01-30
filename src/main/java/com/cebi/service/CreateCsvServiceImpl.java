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
	public byte[] downloadCsv(QueryData queryData, String bank) {
		return createCsvDao.downloadCsv(queryData, bank);
	}

	@Override
	public byte[] downloadCsvPipeSeperator(QueryData queryData, String bank) {
		return createCsvDao.downloadCsvPipeSeperator(queryData, bank);
	}

}
