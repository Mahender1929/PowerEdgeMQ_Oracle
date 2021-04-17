package com.cebi.service;

import java.util.List;

import com.cebi.entity.ColumnNames;
import com.cebi.entity.TableMetaData;

public interface AdminTableMetaDataService {
	public List<TableMetaData> retrieveDbTables(String bank, String merchantId);
	public List<ColumnNames> retrieveTableColumns(String table, String bank);
}
