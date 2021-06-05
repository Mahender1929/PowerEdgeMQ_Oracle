package com.cebi.dao;

import com.cebi.entity.QueryData;

public interface CreatePdfDao {
	public byte[] downloadPdf(QueryData queryData,String bank,String merchantId);
}
