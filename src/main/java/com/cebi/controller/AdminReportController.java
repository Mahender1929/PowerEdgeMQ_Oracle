package com.cebi.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.spec.AlgorithmParameterSpec;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cebi.dao.AdminReportDao;
import com.cebi.entity.Banks;
import com.cebi.entity.ColumnNames;
import com.cebi.entity.QueryData;
import com.cebi.entity.TableMetaData;
import com.cebi.entity.TellerMaster;
import com.cebi.entity.ViewInfo;
import com.cebi.service.AdminReportService;
import com.cebi.service.AdminTableMetaDataService;
import com.cebi.service.ApplicationLabelService;
import com.cebi.service.CreateCsvService;
import com.cebi.service.CreateExcelService;
import com.cebi.service.CreatePdfService;
import com.cebi.service.LoginService;
import com.cebi.utility.ApplicationLabelCache;
import com.cebi.utility.CebiConstant;
import com.cebi.utility.ConnectionException;
import com.cebi.utility.MappingConstant;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.pdf.codec.Base64;

@Controller
public class AdminReportController {

    private static final Logger logger = Logger.getLogger(AdminReportController.class);

    @Autowired
    AdminReportService adminReportService;

    @Autowired
    LoginService loginService;

    @Autowired
    CreatePdfService createPdfService;

    @Autowired
    CreateExcelService createExcelService;

    @Autowired
    AdminTableMetaDataService adminTableMetaDataService;

    @Autowired
    ApplicationLabelService applicationLabelService;

    @Autowired
    CreateCsvService createCsvService;
    
    @Autowired
    AdminReportDao adminReportDao;

    @GetMapping(value = MappingConstant.DEFAULT_PAGE)
    public ModelAndView loginPage(ModelAndView model, HttpServletRequest request) {
	HttpSession httpSession = request.getSession();
	httpSession.setMaxInactiveInterval(10*60); // Session Time to logout 
	if (httpSession.getAttribute("user") != null) {
	    httpSession.invalidate();
	}
	List<Banks> banks = null;
	TellerMaster tellerMaster = new TellerMaster();
	model.addObject("loginForm", tellerMaster);
	model.setViewName(CebiConstant.LOGIN);

	banks = adminReportService.retreiveBankNames();
	if (banks != null && !banks.isEmpty())
	    model.addObject("banks", banks);
	return model;
    }
    
    //Landing Page
    @RequestMapping(value = MappingConstant.LANDING_PAGE, method = RequestMethod.POST)
    public String callLandingPage(@ModelAttribute("loginForm") TellerMaster tellerMaster, Model model, HttpServletRequest request) throws Exception {
	String page = null;
	boolean ipAddress = false;
	List<Object[]> master = null;
	HttpSession session = request.getSession();
	String afterDecrypt = decrypt(tellerMaster.getPwd());
	byte[] bytes = Hex.decodeHex(afterDecrypt.toCharArray());
	tellerMaster.setPwd(new String(bytes, "UTF-8"));
	Map<String, List<TableMetaData>> map = ApplicationLabelCache.getViewsInstance();
	if (tellerMaster.getTellerid() != null && tellerMaster.getPwd() != null) {
	    if (tellerMaster.getBankName().equalsIgnoreCase("Please Select Bank Name")) {
		master = loginService.validateLoginUser(tellerMaster);
	    } else {
		tellerMaster.setBankCode(tellerMaster.getBankName().substring(0, 4));
		CebiConstant.BANKNAME = tellerMaster.getBankName();
		master = loginService.validateSuperLoginUser(tellerMaster);
	    }
	    session.setAttribute("auditHistory", master);
	    if (master.isEmpty()) {
		model.addAttribute("INVALID_USER", CebiConstant.INVALID_USER);
		page = CebiConstant.LOGIN;
	    } else {
		String remoteAddr = request.getRemoteAddr();
		if (remoteAddr.equals("0:0:0:0:0:0:0:1") || "127.0.0.1".equalsIgnoreCase(remoteAddr)) {
		    InetAddress localip = null;
		    try {
			localip = java.net.InetAddress.getLocalHost();
			remoteAddr = localip.getHostAddress();
			logger.info("remoteAddr=="+remoteAddr);
		    } catch (UnknownHostException e) {
			logger.info(e.getMessage());
		    }
		}

		remoteAddr = remoteAddr.substring(0, remoteAddr.lastIndexOf('.'));
		for (Object[] obj : master) {
		    String branchIp = (String) obj[1];
		    String bankCode = (String) obj[3];
		    String ccdp = (String) obj[4];
		    long tellertype=0;
		    if((Long) obj[5]==null)
		    {
		    	tellertype=1;
		    }
		    else
		    {
		    	 tellertype=(Long) obj[5];
		    }
		    tellerMaster.setBankCode(bankCode);
		    tellerMaster.setCcdp(ccdp);
		    tellerMaster.setTellertype(tellertype);
		    if (branchIp.contains(".")) {
				branchIp = branchIp.substring(0, branchIp.lastIndexOf('.'));
				if (branchIp.equalsIgnoreCase(remoteAddr)) {
			   		logger.info(remoteAddr);
					ipAddress = true;
					break;
				}
			}else {
			session.setAttribute("bank", tellerMaster.getBankCode());
			session.setAttribute("user", tellerMaster.getTellerid());
			session.setAttribute(CebiConstant.BANK_CODE, tellerMaster.getBankCode());
			session.setAttribute("isCCdp", tellerMaster.isCcdp());
			session.setAttribute("tellertype", tellerMaster.getTellertype());
			page = "landing";
		    }
		}
				if (ipAddress) {
					boolean flag;
					try {
						flag = loginService.runScript(tellerMaster.getBankCode());

						if (!flag) {
							model.addAttribute("LOGIN_ERROR", "Bank Region ORACLE Server Issue...!!!");
							page = CebiConstant.LOGIN;
						} else {
							Banks banks = adminReportService.populateBankDbDetail(tellerMaster.getBankCode());
							tellerMaster.setIp(banks.getDatabaseUrl());
							session.setAttribute("dburl", banks.getDatabaseUrl());
							session.setAttribute("bank", banks.getBankCode());
							session.setAttribute("user", tellerMaster.getTellerid());
							session.setAttribute(CebiConstant.BANK_CODE, tellerMaster.getBankCode());
							session.setAttribute("isCCdp", tellerMaster.isCcdp());
							session.setAttribute("bnkname", tellerMaster.getBankName());
							session.setAttribute("tellertype", tellerMaster.getTellertype());
							List<TableMetaData> tables = adminTableMetaDataService
									.retrieveDbTables(tellerMaster.getBankCode());
							if (!tables.isEmpty()) {
								map.put("views", tables);
								page = "landing";
								/*String bankcode = tellerMaster.getBankCode();
								Map<String, Integer> downloadHistory = DownloadHistory(bankcode);
								downloadHistory.entrySet().stream()
										.forEach(e -> model.addAttribute(e.getKey(), e.getValue()));*/
							} else {
								model.addAttribute("NO_PRVLG", "Please check privilages..!");
								page = "/";
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						model.addAttribute("NO_PRVLG", e.getMessage());
						page = CebiConstant.LOGIN;
					}
				} else {
					model.addAttribute("LOGIN_ERROR", CebiConstant.IP_ADDRESS);
					page = CebiConstant.LOGIN;
				}
	    }
	    applicationLabelService.retrieveAllLabels();

	} else {
	    page = CebiConstant.LOGIN;
	}
	return page;
    }
    
    //Download History Code
/*      public Map<String,Integer>  DownloadHistory(String bankcode) {
	  return adminReportService.getTotalCount(bankcode);
    }
 */   
    public String decrypt(final String encrypted) throws Exception {
    	 
    	 try {
    	 SecretKey key = new SecretKeySpec(Base64.decode("u/Gu5posvwDsXUnV5Zaq4g=="), "AES");
    	 AlgorithmParameterSpec iv = new IvParameterSpec(Base64.decode("5D9r9ZVzEYYgha93/aUK2w=="));
    	 byte[] decodeBase64 = Base64.decode(encrypted);
    	 Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    	 cipher.init(Cipher.DECRYPT_MODE, key, iv);
    	 return new String(cipher.doFinal(decodeBase64), "UTF-8");
    	 } catch (Exception e) {
    	 e.printStackTrace();
    	 throw new RuntimeException("This should not happen in production.", e);
    	 }
    	} 

    @ExceptionHandler(ConnectionException.class)
    public ModelAndView connectionException(ConnectionException errorMessage) {
	ModelAndView responseObject1 = new ModelAndView("login");
	responseObject1.addObject("loginForm", new TellerMaster());
	responseObject1.addObject(CebiConstant.ERROR, errorMessage.getMessage());
	return responseObject1;
    }

    @RequestMapping(value = MappingConstant.TABLES, method = RequestMethod.GET)
    public @ResponseBody List<TableMetaData> createTableMetaData(Model model, HttpServletRequest request) {
	HttpSession session = request.getSession();
	List<TableMetaData> tables = null;
	String bank = (String) session.getAttribute("bank");
		tables = adminReportService.populateDbTables(bank);
	return tables;
    }

    @RequestMapping(value = MappingConstant.REPORT_PAGE, method = RequestMethod.GET)
    public ModelAndView callReportPage(HttpServletRequest request) {
	List<TableMetaData> tables = null;
	ModelAndView modelAndView = new ModelAndView();
	HttpSession session = request.getSession();
	TellerMaster tellerMaster = new TellerMaster();
	if (session.getAttribute("user") != null) {
	    String bank = (String) session.getAttribute("bank");
	    tables = adminReportService.populateDbTables(bank);
	    if (!tables.isEmpty()) {
		modelAndView.addObject("tables", tables);
		modelAndView.setViewName("report");
	    }
	} else {
	   // modelAndView.setViewName(CebiConstant.LOGIN);
	    modelAndView.setViewName("logout");
	    modelAndView.addObject("loginForm", tellerMaster);
	    modelAndView.addObject("SESSION_LOGOUT", CebiConstant.SESSION_LOGOUT);
	}
	return modelAndView;
    }
    
    @RequestMapping(value = MappingConstant.RETRIVE_COLUMNS, method = RequestMethod.POST)
    public @ResponseBody List<ColumnNames> retriveColumnMetaData(@RequestBody String table, HttpServletRequest request) {
	HttpSession session = request.getSession();
	String bank = (String) session.getAttribute("bank");
	List<ColumnNames> cnm=adminTableMetaDataService.retrieveTableColumns(table, bank);
	//return adminTableMetaDataService.retrieveTableColumns(table, bank);
	return cnm;
    }

    @RequestMapping(value = MappingConstant.GET_TABLEDATA, method = RequestMethod.POST)
    public @ResponseBody List<TableMetaData> getTableData(@RequestBody QueryData getTableData, HttpServletRequest request) throws Exception {
	HttpSession session = request.getSession();
	String bank = (String) session.getAttribute("bank");
	@SuppressWarnings("unchecked")
	List<Object[]> master = (List<Object[]>) session.getAttribute("auditHistory");
	String dburlstr=(String) session.getAttribute("dburl");
	logger.info(dburlstr);
	TellerMaster tellerMaster = new TellerMaster();
	for (Object[] object : master) {
	    tellerMaster.setTellerid(object[0].toString());
	    tellerMaster.setBranchid(Long.parseLong(object[2].toString()));
	    //tellerMaster.setBranchid(Integer.parseInt(object[2].toString()));
	    tellerMaster.setBankCode(object[3].toString());
	    break;
	}
	tellerMaster.setIp(dburlstr);
	return adminReportService.getTableData(getTableData, bank, tellerMaster);
    }

    @RequestMapping(value = MappingConstant.DOWNLOAD_PDF, method = RequestMethod.POST)
    public ResponseEntity<byte[]> exportDataToPdf(HttpServletRequest request, HttpServletResponse response) throws Exception {
	byte[] pdfBytes = null;
	ResponseEntity<byte[]> pdfResponse = null;
	ObjectMapper mapper = new ObjectMapper();
	String param = request.getParameter("pdfJson");
	QueryData queryData = mapper.readValue(param, QueryData.class);
	HttpSession session = request.getSession();
	String bank = (String) session.getAttribute("bank");
	pdfBytes = createExcelService.downloadExcel(queryData, bank);
	HttpHeaders headers = new HttpHeaders();
	response.setContentType("application/ms-excel");
	headers.setContentDispositionFormData(CebiConstant.INLINE, "cebi.xls");
	pdfResponse = new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
	return pdfResponse;
}
	/*TellerMaster master =populatemasterData(request);
	try{
		pdfBytes = adminReportService.downloadPdf(queryData, bank,master);
		HttpHeaders headers = new HttpHeaders();
		response.setContentType("application/txt");
		headers.setContentDispositionFormData(CebiConstant.INLINE, "cebi.txt");
		pdfResponse = new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
	}catch(Exception e)
	{
		logger.info(e.getMessage());
		response.sendRedirect(request.getContextPath()+"/rptpage?"+"error=1&errmsg=" + e.getMessage());
	}
	return pdfResponse;
    }*/

    @RequestMapping(value = MappingConstant.DOWNLOAD_EXCEL, method = RequestMethod.POST)
    public ResponseEntity<byte[]> exportDataToExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {
	byte[] pdfBytes = null;
	ResponseEntity<byte[]> pdfResponse = null;
	ObjectMapper mapper = new ObjectMapper();
	String param = request.getParameter("excelJson");
	QueryData queryData = mapper.readValue(param, QueryData.class);
	HttpSession session = request.getSession();
	String bank = (String) session.getAttribute("bank");
	pdfBytes = createExcelService.downloadExcel(queryData, bank);
	HttpHeaders headers = new HttpHeaders();
	response.setContentType("application/ms-excel");
	headers.setContentDispositionFormData(CebiConstant.INLINE, "cebi.xls");
	pdfResponse = new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
	return pdfResponse;
}
	/*TellerMaster master =populatemasterData(request);
	try{
		pdfBytes = adminReportService.downloadExcel(queryData, bank,master);
		HttpHeaders headers = new HttpHeaders();
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		headers.setContentDispositionFormData(CebiConstant.INLINE, "cebi.xlsx");
		headers.setContentLength(pdfBytes.length);
		pdfResponse = new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
	}catch(Exception e)
	{
		logger.info(e.getMessage());
		response.sendRedirect(request.getContextPath()+"/rptpage?"+"error=1&errmsg=" + e.getMessage());
	}
	
	return pdfResponse;
    }
    */

    @RequestMapping(value = MappingConstant.DOWNLOAD_CSV, method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<byte[]> exportDataToCsv(HttpServletRequest request, HttpServletResponse response) throws Exception {
	logger.info("Enter into exportDataToCsv Method");
	byte[] pdfBytes = null;
	ResponseEntity<byte[]> pdfResponse = null;
	ObjectMapper mapper = new ObjectMapper();
	String param = request.getParameter("csvJson");
	HttpSession session = request.getSession();
	QueryData queryData = mapper.readValue(param, QueryData.class);
	String bank = (String) session.getAttribute("bank");
	pdfBytes = createCsvService.downloadCsv(queryData, bank);
	HttpHeaders headers = new HttpHeaders();
	response.setContentType("text/csv");
	headers.setContentDispositionFormData(CebiConstant.INLINE, "cebi.csv");
	pdfResponse = new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
	return pdfResponse;
}
	/*TellerMaster master =populatemasterData(request);
	try{
		pdfBytes = adminReportService.csvDownloadQueue(queryData, bank,master);
		HttpHeaders headers = new HttpHeaders();
		response.setContentType("text/csv");
		headers.setContentDispositionFormData(CebiConstant.INLINE, "cebi.csv");
		pdfResponse = new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
	}catch(Exception e)
	{
		logger.info(e.getMessage());
		response.sendRedirect(request.getContextPath()+"/rptpage?"+"error=1&errmsg=" + e.getMessage());
	}
	
	return pdfResponse;
    }
*/
    @RequestMapping(value = MappingConstant.DOWNLOAD_CSV_PIPE, method = RequestMethod.POST)
    public ResponseEntity<byte[]> downloadCsvPipeSeperator(HttpServletRequest request, HttpServletResponse response) throws IOException {
	byte[] pdfBytes = null;
	ResponseEntity<byte[]> pdfResponse = null;
	ObjectMapper mapper = new ObjectMapper();
	String param = request.getParameter("csvJson");
	QueryData queryData = mapper.readValue(param, QueryData.class);
	HttpSession session = request.getSession();
	String bank = (String) session.getAttribute("bank");
	pdfBytes = createCsvService.downloadCsvPipeSeperator(queryData, bank);
	HttpHeaders headers = new HttpHeaders();
	response.setContentType("text/csv");
	headers.setContentDispositionFormData(CebiConstant.INLINE, "cebi.csv");
	pdfResponse = new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
	return pdfResponse;
}
	/*TellerMaster master =populatemasterData(request);
	try{
		pdfBytes =  (byte[]) adminReportService.downloadCsvPipeSeperator(queryData, bank,master);
		HttpHeaders headers = new HttpHeaders();
		response.setContentType("text/csv");
		headers.setContentDispositionFormData(CebiConstant.INLINE, "cebi.csv");
		pdfResponse = new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
	}
	catch(Exception e)
	{
		logger.info(e.getMessage());
		response.sendRedirect(request.getContextPath()+"/rptpage?"+"error=1&errmsg=" + e.getMessage());
	}
		return pdfResponse;
    }
    */
    @RequestMapping(value = MappingConstant.DOWNLOAD_QUEUE, method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> createCommonDownloadQueue(@RequestBody QueryData queryData, HttpServletRequest request,HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();
		String bank = (String) session.getAttribute("bank");
		TellerMaster master = new TellerMaster();
		master.setBankCode(bank);
		master.setIp(session.getAttribute("dburl").toString());
		master.setTellerid(session.getAttribute("user").toString());
		Map<String, Object> downloadQueueResponse =null;
		try {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			String date = dtf.format(LocalDateTime.now());
			BigDecimal count = adminReportService.getReportQueueStatusCount(bank, date);
			if(count.intValue()<4){
		     /*downloadQueueResponse = adminReportService.commonDownloadQueue(queryData,bank,master);*/
			}else{
				downloadQueueResponse = new HashMap<String, Object>();
				downloadQueueResponse.put("msg", queryData.getReporttype().toUpperCase() + " Another File is INPROCESS....!!!!!!!");
		    }
		} catch (Exception e) {
			logger.info(e.getMessage());
			response.sendRedirect(request.getContextPath()+"/rptpage?"+"error=1&errmsg=" + e.getMessage());
		}
		return downloadQueueResponse;
	}

    @RequestMapping(value = MappingConstant.FAVOURITE, method = RequestMethod.POST)
    public @ResponseBody Map<String, String> savefavouriteQuery(@RequestBody QueryData data, HttpServletRequest request, HttpServletResponse response) {
	Map<String, String> map = new HashMap<>();
	HttpSession session = request.getSession();
	String bankCode = (String) session.getAttribute(CebiConstant.BANK_CODE);
	ModelAndView model = new ModelAndView();
	data.setBankCode(bankCode);
	adminReportDao.saveFavouriteQuery(data);
	map.put("saved", "Added to favourite list..");
	model.addObject("saved", "Added to favourite list..");
	return map;

    }

    @RequestMapping(value = MappingConstant.SHOW_FAVOURITE_LIST, method = RequestMethod.GET)
    public ModelAndView savefavouriteQuery(HttpServletRequest request,@ModelAttribute("deleteSuccess") String deleteSuccess,@ModelAttribute("deleteFailed") String deleteFailed,Model mdl) {
	ModelAndView model = new ModelAndView();
	model.setViewName("favouritelist");
	HttpSession session = request.getSession();
	String bankCode = (String) session.getAttribute(CebiConstant.BANK_CODE);
	List<QueryData> list = adminReportDao.retrieveFavouriteList(bankCode);
	if (list != null && !list.isEmpty()){
		mdl.addAttribute("deleteSuccess",deleteSuccess);
	    mdl.addAttribute("deleteFailed", deleteFailed);
	    model.addObject("favouriteLists", list);
	}else{
	    model.addObject("nocontent", "No Content..!");
	}
	return model;

    }

    @RequestMapping(value = MappingConstant.GET_REPORT_DETAILS, method = RequestMethod.POST)
    public ModelAndView getReportDetails(HttpServletRequest request) {
	ModelAndView model = new ModelAndView();
	model.setViewName("report");
	int id = Integer.parseInt(request.getParameter("id"));
	List<QueryData> list = adminReportDao.getReportDetails(id);
	if (list != null && !list.isEmpty())
	    model.addObject("favouriteLists", list.get(0));
	else
	    model.addObject("nocontent", "No Content..!");
	return model;

    }

	@RequestMapping(value = MappingConstant.LANDING_DEFAULT_PAGE, method = RequestMethod.GET)
	public String landingPage(Model model, HttpServletRequest request) {
		HttpSession session = request.getSession();
		String bankcode = (String) session.getAttribute(CebiConstant.BANK_CODE);
		if (bankcode != null) {
			/*Map<String, Integer> downloadHistory = DownloadHistory(bankcode);
			downloadHistory.entrySet().stream().forEach(e -> model.addAttribute(e.getKey(), e.getValue()));
			*/return "landing";
		} else {
			return "logout";
		}
	}
    

    @RequestMapping(value = MappingConstant.HELP, method = RequestMethod.GET)
    public ModelAndView faqQuestions(ModelAndView model) {
	ModelAndView mv = new ModelAndView();
	mv.setViewName("/help");
	return mv;
    }

    @RequestMapping(value = MappingConstant.VIEWDETAILS, method = RequestMethod.POST)
    public @ResponseBody List<ViewInfo> retriveViewDetails(HttpServletRequest request) {
	HttpSession session = request.getSession();
	List<ViewInfo> viewInfos = new ArrayList<>();
	String bankCode = (String) session.getAttribute(CebiConstant.BANK_CODE);
	viewInfos = (List<ViewInfo>) adminReportService.retreiveViewDetails(bankCode);
	return viewInfos;
    }

    
    public TellerMaster  populatemasterData(HttpServletRequest session){ 
	List<Object[]> master = (List<Object[]>) session.getSession().getAttribute("auditHistory");
	TellerMaster tellerMaster = new TellerMaster();
	for (Object[] object : master) {
	    tellerMaster.setTellerid(object[0].toString());
	    tellerMaster.setBranchid(Long.parseLong(object[2].toString()));
	    tellerMaster.setBankCode(object[3].toString());
	    break;
	}
	return tellerMaster;
    }

    @RequestMapping(value = "/rptpage", method = RequestMethod.GET)
    public ModelAndView reportPage(HttpServletRequest request) {
	ModelAndView modelAndView = new ModelAndView();
	String errorcode=request.getParameter("error");
	String errormsg=request.getParameter("errmsg");
	if(errormsg!="" && errorcode.equals("1"))
	{
		modelAndView.addObject("errormsg", errormsg);
		 modelAndView.setViewName("report"); 
	}
	return modelAndView;
    }
    
    @RequestMapping(value="chkbankcode.json",method=RequestMethod.POST)
    public @ResponseBody Map<String,String> chkbankcode(HttpServletRequest request)
    {
    	Map<String, String> map=new HashMap();
    	TellerMaster master=new TellerMaster();
    	HttpSession s=request.getSession(false);
    	logger.info("entered into chkbankcode");
    	String dburl=(String) s.getAttribute("dburl");
    	master.setTellerid((String)s.getAttribute("user"));
    	master.setBankCode((String)s.getAttribute("bankCode"));
    	master.setIp(dburl);
    	List<String> banks=loginService.checkbankcode(dburl,master);
    	String bankcode=(String)banks.get(0);
    	map.put("list", bankcode);
    	logger.info("exit from chkbankcode");
    	return map;
    }
    
    @RequestMapping(value = "/deleteFav", method=RequestMethod.GET)
    public String deleteFavourite( @RequestParam(value = "id", required = false) int id,Model model,RedirectAttributes redirectAttrs) {
    int deleteFavRec = adminReportDao.deleteFavRec(id);
    if(deleteFavRec==1){
    	System.out.println("deleted successfully");
    	redirectAttrs.addFlashAttribute("deleteSuccess", "Record deleted successfully");
    }
    else {
    	System.out.println("delete failed");
    	redirectAttrs.addFlashAttribute("deleteFailed", "Record deleted Failed");

    }
  
    return "redirect:/showFavouriteList";
    }
    
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ModelAndView logut(ModelAndView model) {
	ModelAndView mv = new ModelAndView();
	mv.setViewName("logout");
	return mv;
    }
    
}
/*if (branchIp.equalsIgnoreCase(remoteAddr)) {
logger.info(remoteAddr);
ipAddress = true;
break;
}*/