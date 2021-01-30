package com.cebi.service;

import com.cebi.entity.QueryData;

public interface CreateCsvService {
	public byte[] downloadCsv(QueryData queryData,String bank);
	public byte[] downloadCsvPipeSeperator(QueryData queryData, String bank);
	/*int csvDownloadQueue(QueryData queryData, String bank);
	public int downloadCsv1(QueryData queryData, String bank);*/
}
