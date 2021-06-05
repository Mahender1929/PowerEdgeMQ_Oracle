package com.cebi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cebi.dao.CreateTextDao;
import com.cebi.entity.QueryData;

@Service
public class CreateTextServiceImpl implements CreateTextService {

	@Autowired
	CreateTextDao textDao;
		
	@Override
	public byte[] downloadText(QueryData queryData, String bank, String merchantId) {
		return textDao.downloadText(queryData,bank,merchantId);
	}

}
