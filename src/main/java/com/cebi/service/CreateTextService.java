package com.cebi.service;

import com.cebi.entity.QueryData;

public interface CreateTextService {

	public byte[] downloadText(QueryData queryData, String bank, String merchantId);

}
