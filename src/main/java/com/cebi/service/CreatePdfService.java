package com.cebi.service;

import com.cebi.entity.QueryData;

public interface CreatePdfService {
	public byte[] downloadPdf(QueryData queryData,String bank,String merchantId);
}
