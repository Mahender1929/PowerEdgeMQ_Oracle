package com.cebi.dao;

import com.cebi.entity.QueryData;

public interface CreateExcelDao {
	public byte[] downloadExcel(QueryData queryData,String bank,String merchantId);
}
