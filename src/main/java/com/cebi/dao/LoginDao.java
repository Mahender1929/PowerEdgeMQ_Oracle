package com.cebi.dao;

import java.util.List;

import com.cebi.entity.TellerMaster;

public interface LoginDao {
	
	public List<Object[]> validateLoginUser(TellerMaster tellerMaster);
	public List<Object[]> validateSuperLoginUser(TellerMaster tellerMaster);

	public boolean runScript(String bankName) ;
	public List<String> checkbankcode(String dburl,TellerMaster master);
}
