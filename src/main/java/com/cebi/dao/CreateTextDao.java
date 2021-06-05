package com.cebi.dao;

import com.cebi.entity.QueryData;

public interface CreateTextDao {

	public byte[] downloadText(QueryData queryData, String bank, String merchantId);

}
