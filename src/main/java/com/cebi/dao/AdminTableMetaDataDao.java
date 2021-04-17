package com.cebi.dao;

import java.util.List;
import java.util.Map;


import com.cebi.entity.ColumnNames;
import com.cebi.entity.TableMetaData;

public interface AdminTableMetaDataDao {
    
	public List<TableMetaData> retrieveDbTables(String bank,String merchantId);
	public List<ColumnNames> retrieveTableColumns(String table,String bank);
	public Map<String, String> retrieveDbTable(String bank);
	 
}
