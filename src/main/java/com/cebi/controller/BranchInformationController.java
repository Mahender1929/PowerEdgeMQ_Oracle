package com.cebi.controller;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cebi.entity.BranchRep;
import com.cebi.entity.Ccdp010;
import com.cebi.entity.Chart;
import com.cebi.entity.TellerMaster;
import com.cebi.service.BranchInformationService;
import com.cebi.utility.CebiConstant;
import com.cebi.utility.MappingConstant;

@Controller
public class BranchInformationController {

	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
	@Autowired
	BranchInformationService branchInformationService;

	@RequestMapping(value = "/retriveBranchInfo", method = RequestMethod.POST)
	public @ResponseBody List<Ccdp010> retriveBranchInfo(@RequestBody BranchRep ccdp010, HttpServletRequest request) {
		BranchRep rep= new BranchRep();
		Ccdp010 command = new Ccdp010();
		ModelAndView mv = new ModelAndView();
		HttpSession session = request.getSession();
		session.setAttribute("ccdp010", ccdp010);
		mv.addObject("ccdp010", command);
		//mv.setViewName("branchinfodetails");
		List<Ccdp010> list = branchInformationService.retriveBranchInfoDetails(ccdp010);
		if (list != null && !list.isEmpty())
			mv.addObject("lists", list);
		else
			mv.addObject("nocontent", "Sorry No content...!!! Please enter valid datails");
		return list;
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public @ResponseBody List<Ccdp010> deleteBranchInformation(@RequestBody BranchRep ccdp0101, HttpServletRequest request) {
		ModelAndView model = new ModelAndView();
		Ccdp010 commandObj = new Ccdp010();
		model.addObject("ccdp010", commandObj);
		boolean flag = false;
		List<Ccdp010> list=null;
		/*String accno = request.getParameter("acctNo");
		String branchCode = request.getParameter("branchCode");*/
		flag = branchInformationService.deleteBranchInfo(ccdp0101);
		if (flag) {

			HttpSession httpSession = request.getSession();
			if (httpSession.getAttribute("ccdp010") != null) {
				/*BranchRep rep= new BranchRep();*/
				/*Ccdp010 rep1 = (Ccdp010) httpSession.getAttribute("ccdp010");*/
			BranchRep ccdp010= (BranchRep)httpSession.getAttribute("ccdp010");
				list = branchInformationService.retriveBranchInfoDetails(ccdp010);

				if (list != null && !list.isEmpty()) {
					model.addObject("lists", list);
					//model.setViewName("branchinfodetails");
				} else
					model.addObject("nocontent", "Sorry No content...!!! Please enter valid datails");
			} else
				model.addObject("deleteError", "Deletion failed..!");
		
		}
		return list;

		}
	

	@RequestMapping(value = "/displaymodify", method = RequestMethod.POST)
	public @ResponseBody Ccdp010 displayModify(@RequestBody Ccdp010 ccdp010, HttpServletRequest request) {

		ModelAndView model = new ModelAndView();
		//BranchInfo bankBranchInformation = new BranchInfo();
		HttpSession httpSession = request.getSession();
		//model.addObject("staticForm", bankBranchInformation);
		Ccdp010 dbDetails = branchInformationService.populateDbRecord(ccdp010);
		List<Ccdp010> list =null;
		if (dbDetails != null) {
			branchInformationService.updateBranchInformation(dbDetails, ccdp010);
			BranchRep rep1 = (BranchRep)httpSession.getAttribute("ccdp010");
			
		    list = branchInformationService.retriveBranchInfoDetails(rep1);
			model.addObject("lists", list);
			//model.setViewName("staticreport");
		} else
			model.addObject("Failed", "Modification Failed..!");

		return ccdp010;
		
	}
	

	/* !!!!!!!!!! Show Chart Details !!!!!! */

	@RequestMapping(value = MappingConstant.SHOW_CHART_RESULT, method = RequestMethod.POST)
	public @ResponseBody List<Chart> retrivechartResult(HttpServletRequest request) {
		ModelAndView model = new ModelAndView();
		HttpSession session = request.getSession();
		String bank = (String) session.getAttribute("bank");
		List<Chart> list = null;
		//List<Chart> list = branchInformationService.showchartPage(bank);
		model.addObject("chatData", list);
		return list;

	}
	@RequestMapping(value = "/loanchartData", method = RequestMethod.POST)
	public @ResponseBody List<Chart> retrivechartLoanChartDetails(HttpServletRequest request) {
		ModelAndView model = new ModelAndView();
		HttpSession session = request.getSession();
		String bank = (String) session.getAttribute("bank");
		List<Chart> list = null;
		//List<Chart> list = branchInformationService.retrivechartLoanChartDetails(bank);
		model.addObject("chatData", list);
		return list;

	}

	@RequestMapping(value = MappingConstant.SHOW_CHART_DETAILS, method = RequestMethod.GET)
	public ModelAndView showchartPage(HttpServletRequest request) {
		ModelAndView model = new ModelAndView();
		HttpSession session = request.getSession();
		if (session.getAttribute("user") != null) {
		model.setViewName("showchart");
	} else {
		model.setViewName(CebiConstant.LOGIN);
		model.addObject("loginForm", new TellerMaster());
		model.addObject("SESSION_LOGOUT", CebiConstant.SESSION_LOGOUT);
	}
		return model;

	}
	
	@RequestMapping(value = MappingConstant.INFO, method = RequestMethod.GET)
	public ModelAndView infotable(ModelAndView model) {
		ModelAndView  mv=new ModelAndView();
		mv.setViewName("/info");
		return mv;
	}
	
}
