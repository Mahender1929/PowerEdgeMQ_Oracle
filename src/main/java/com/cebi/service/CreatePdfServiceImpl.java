package com.cebi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cebi.dao.CreatePdfDao;
import com.cebi.entity.QueryData;

@Service
public class CreatePdfServiceImpl implements CreatePdfService {

	@Autowired
	CreatePdfDao createPdfDao;;

	public byte[] downloadPdf(QueryData queryData, String bank, String merchantId) {
		byte[] bytes = null;
		bytes = createPdfDao.downloadPdf(queryData,bank,merchantId);
		return bytes;
	}
}
