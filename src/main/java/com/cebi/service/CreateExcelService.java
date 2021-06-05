package com.cebi.service;

import com.cebi.entity.QueryData;

public interface CreateExcelService {
	public byte[] downloadExcel(QueryData queryData,String bank, String merchantId);
}
