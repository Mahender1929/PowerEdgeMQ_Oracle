package com.cebi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cebi.dao.CreateExcelDao;
import com.cebi.entity.QueryData;

@Service
public class CreateExcelServiceImpl implements CreateExcelService {

	@Autowired
	CreateExcelDao excelDao;

	@Override
	public byte[] downloadExcel(QueryData queryData, String bank, String merchantId) {
		return excelDao.downloadExcel(queryData,bank,merchantId);
	}

}
