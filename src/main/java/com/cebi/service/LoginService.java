package com.cebi.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.cebi.entity.Banks;
import com.cebi.entity.TellerMaster;

public interface LoginService {
	public List<Object[]> validateLoginUser(TellerMaster tellerMaster);
	public List<Object[]> validateSuperLoginUser(TellerMaster tellerMaster);
	public boolean runScript(String bankName);
	public List<String> checkbankcode(String dburl,TellerMaster tellermaster);
}
