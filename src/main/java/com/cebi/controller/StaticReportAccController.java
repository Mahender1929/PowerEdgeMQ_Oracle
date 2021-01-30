package com.cebi.controller;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cebi.entity.Banks;
import com.cebi.entity.StaticReports;
import com.cebi.entity.TellerMaster;
import com.cebi.service.StaticReportAccService;
import com.cebi.utility.CebiConstant;
import com.cebi.utility.MappingConstant;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Controller
public class StaticReportAccController {

	@Autowired
	StaticReportAccService staticReportAccService;

	private static final Logger logger = Logger
			.getLogger(StaticReportAccController.class);

	LocalTime time = null;

	@RequestMapping(value = MappingConstant.STATICREPORTBYACC, method = RequestMethod.GET)
	public ModelAndView staticRepoPage(ModelAndView model,
			HttpServletRequest request) {
		logger.info("ENTER into staticRepoPage : " + LocalTime.now().toString());
		ModelAndView modelAndView = new ModelAndView();
		HttpSession session = request.getSession();
		TellerMaster tellerMaster = new TellerMaster();
		if (session.getAttribute("user") != null) {
			modelAndView.setViewName("staticReportByAcc");
		} else {
			modelAndView.setViewName(CebiConstant.LOGIN);
			modelAndView.addObject("loginForm", tellerMaster);
			modelAndView.addObject("SESSION_LOGOUT",
					CebiConstant.SESSION_LOGOUT);
		}
		logger.info("Exit From staticRepoPage :" + LocalTime.now().toString());
		return modelAndView;
	}

	@RequestMapping(value = MappingConstant.GETDATABYACC, method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> getAccDetails(
			@RequestBody StaticReports staticReports, HttpServletRequest request) {
		
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		logger.info("ENTER into getAccDetails() :" + LocalTime.now().toString());
		if (!staticReports.getAccNum().equals("")
				&& !staticReports.getToDate().equals("")
				&& !staticReports.getFrDate().equals("")) {
			long daysDiff = 0;
			HttpSession session = request.getSession();
			List<Object[]> master = (List<Object[]>) session.getAttribute("auditHistory");
			TellerMaster tellerMaster = new TellerMaster();
			for (Object[] object : master) {
			    tellerMaster.setTellerid(object[0].toString());
			    tellerMaster.setBranchid(Long.parseLong(object[2].toString()));
			    tellerMaster.setBankCode(object[3].toString());
			}
			String bankCode = (String) session
					.getAttribute(CebiConstant.BANK_CODE);
			try {
				daysDiff = getDateDiff(staticReports.getFrDate(),
						staticReports.getToDate());
			} catch (IOException e) {
				e.printStackTrace();
				logger.info(e.getMessage());
			}
			if (daysDiff <= 365 && daysDiff >= 0) {
				staticReports.setFrDate(dateConvert(staticReports.getFrDate(),
						"dd/MM/yyyy", "yyyyMMdd"));
				staticReports.setToDate((dateConvert(staticReports.getToDate(),
						"dd/MM/yyyy", "yyyyMMdd")));
				List<StaticReports> list = staticReportAccService
						.getDetailByAccountNo(staticReports, bankCode,tellerMaster);
				if (list.isEmpty()) {
					map.put("error", 2);
				} else {
					map.put("list", list);
				}
			} else {
				map.put("error", 1);
			}
			logger.info("Exit From getAccDetails() :"
					+ LocalTime.now().toString());
		} else {
			map.put("error", 3);
		}

		return map;
	}

	public static String dateConvert(String postDate, String ipformat,
			String opformat) {
		java.util.Date fromDate;
		String sysDate = "";
		if (postDate != null && postDate.trim().length() > 0) {
			try {
				SimpleDateFormat dateformat = new SimpleDateFormat(ipformat);
				SimpleDateFormat dateformat1 = new SimpleDateFormat(opformat);
				fromDate = dateformat.parse(postDate);
				sysDate = dateformat1.format(fromDate);
			} catch (ParseException e) {
				e.printStackTrace();
				logger.info(e.getMessage() + LocalTime.now().toString());
			}
		} else {
			sysDate = "00000000";
		}
		return sysDate;
	}

	@RequestMapping(value = MappingConstant.PRINTQUERYSCRIPTDATA, method = RequestMethod.POST)
	public ResponseEntity<byte[]> printReportByAcc(
			@ModelAttribute("MAFORMDATA") StaticReports staticReports,
			HttpServletRequest request) throws FileNotFoundException {
		logger.info("ENTER into printReportByAcc() : "
				+ LocalTime.now().toString());
		File file =null;
		HttpSession session = request.getSession();
		List<Object[]> master = (List<Object[]>) session.getAttribute("auditHistory");
		TellerMaster tellerMaster = new TellerMaster();
		for (Object[] object : master) {
		    tellerMaster.setTellerid(object[0].toString());
		    tellerMaster.setBranchid(Long.parseLong(object[2].toString()));
		    tellerMaster.setBankCode(object[3].toString());
		}
		String bankCode = (String) session.getAttribute(CebiConstant.BANK_CODE);
		List<Banks> banks = staticReportAccService.getBankNames(bankCode);
		String bankName = "";
		for (Banks bank : banks) {
			if (bank.getBankCode().equalsIgnoreCase(bankCode)) {
				bankName = bank.getBankName();
			}
		}
		staticReports.setFrDate(dateConvert(staticReports.getFrDate(),
				"dd/MM/yyyy", "yyyyMMdd"));
		staticReports.setToDate((dateConvert(staticReports.getToDate(),
				"dd/MM/yyyy", "yyyyMMdd")));
		ResponseEntity<byte[]> pdfResponse = null;
		List<StaticReports> list = staticReportAccService.getDetailByAccountNo(
				staticReports, bankCode,tellerMaster);
		if (!list.isEmpty()) {
			StaticReports sr = list.get(0);

			try {
				Document document = new Document(PageSize.A4);
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				PdfWriter writer = PdfWriter.getInstance(document,
						byteArrayOutputStream);
				document.open();
				Font normalTable = new Font(Font.FontFamily.COURIER, 14f,
						Font.NORMAL);
				Font normalH = new Font(Font.FontFamily.HELVETICA, 14f,
						Font.BOLD);
				Font normal = new Font(Font.FontFamily.HELVETICA, 13f,
						Font.NORMAL);
				Font bold = new Font(Font.FontFamily.COURIER, 12f, Font.BOLD);
				Integer banchno = (Integer) request.getSession().getAttribute(
						"branchNo");
				Integer tellerId = (Integer) request.getSession().getAttribute(
						"tellerNo");
				int hed[] = { 80 };
				 file = new File(sr.getAccNum() + ".pdf");
				 Image img = null;
					String jpgpath="/bancedge//images//bank_logo//"+bankCode+".jpg";
					String gifpath="/bancedge//images//bank_logo//"+bankCode+".gif";
					File giffilepath=new File(gifpath);
					File jpgfilepath=new File(jpgpath);
					if(jpgfilepath.exists() && !giffilepath.exists())
					{
						try {
							img = Image.getInstance(jpgpath);
							img.setAlignment(Image.LEFT);
							img.scaleToFit(200f, 100f);
							document.add(img);
						} catch (MalformedURLException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					if(giffilepath.exists() && !jpgfilepath.exists())
					{
						try {
							img = Image.getInstance(gifpath);
							img.setAlignment(Image.LEFT);
							img.scaleToFit(200f, 100f);
							document.add(img);
						} catch (MalformedURLException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				Font f = new Font(FontFamily.TIMES_ROMAN, 10.0f, Font.BOLD,
						BaseColor.BLACK);
				PdfPTable table = new PdfPTable(2);
				table.setWidthPercentage(100);
				table.addCell(getCell(sr.getCustName().trim(),
						PdfPCell.ALIGN_LEFT, 12));
				table.addCell(getCell(bankName.substring(5, bankName.length()),
						PdfPCell.ALIGN_RIGHT, 13));
				table.addCell(getCell(sr.getAddress(), PdfPCell.ALIGN_LEFT, 10));
				table.addCell(getCell(sr.getJournalNo(), PdfPCell.ALIGN_RIGHT,
						10));
				document.add(table);

				PdfPTable tbl = new PdfPTable(2);
				tbl.setWidthPercentage(100);
				tbl.addCell(getCell("Account No : " + sr.getAccNum(),
						PdfPCell.ALIGN_LEFT, 12));
				tbl.addCell(getCell("Customer No :" + sr.getCustNo(),
						PdfPCell.ALIGN_RIGHT, 12));
				tbl.addCell(getCell("Branch Code : " + sr.getTlrBranch(),
						PdfPCell.ALIGN_LEFT, 12));
				tbl.addCell(getCell("Mobile No : " + sr.getMobNO(),
						PdfPCell.ALIGN_RIGHT, 12));
				tbl.addCell(getCell("Date : " + sr.getCurrDate(),
						PdfPCell.ALIGN_LEFT, 12));
				tbl.addCell(getCell("Time : " + sr.getCurrTime(),
						PdfPCell.ALIGN_RIGHT, 12));
				tbl.addCell(getCell("Current Balance  : " + sr.getCurrBalace(),
						PdfPCell.ALIGN_LEFT, 12));
				document.add(tbl);
				PdfPTable tb2 = new PdfPTable(1);
				tb2.setWidthPercentage(100);
				staticReports.setFrDate(dateConvert(staticReports.getFrDate(),
						"yyyyMMdd", "dd/MM/yyyy"));
				staticReports.setToDate((dateConvert(staticReports.getToDate(),
						"yyyyMMdd", "dd/MM/yyyy")));
				tb2.addCell(getCell("Date From " + staticReports.getFrDate()
						+ " To " + staticReports.getToDate(),
						PdfPCell.ALIGN_CENTER, 12));
				document.add(tb2);
				document.add(new Paragraph(" "));
				PdfPTable pdfPTable = new PdfPTable(8);
				pdfPTable.setWidthPercentage(100);
				pdfPTable.setWidths(new int[] { 1, 3, 3, 7, 3, 4, 4, 4 });
				Font f2 = new Font(FontFamily.TIMES_ROMAN, 10.0f, Font.NORMAL,
						BaseColor.BLACK);
				int i = 0;
				// Create cells
				pdfPTable.addCell(new PdfPCell(new Paragraph("Sr.No", f)));
				pdfPTable.addCell(new PdfPCell(new Paragraph("Post Date", f)));
				pdfPTable.addCell(new PdfPCell(new Paragraph("Value Date", f)));
				pdfPTable.addCell(new PdfPCell(new Paragraph("Details", f)));
				pdfPTable.addCell(new PdfPCell(new Paragraph("Cheque No", f)));
				pdfPTable.addCell(new PdfPCell(new Paragraph("Debit", f)));
				pdfPTable.addCell(new PdfPCell(new Paragraph("Credit", f)));
				pdfPTable.addCell(new PdfPCell(new Paragraph("Balance", f)));
				for (StaticReports staticReports1 : list) {
					i++;
					String srno = Integer.toString(i);
					pdfPTable.addCell(new PdfPCell(new Paragraph(srno, f2)));
					pdfPTable.addCell(new PdfPCell(new Paragraph(dateConvert(
							staticReports1.getPostTime(), "yyyy-MM-dd",
							"dd/MM/yyyy"), f2)));
					pdfPTable.addCell(new PdfPCell(new Paragraph(dateConvert(
							staticReports1.getTxDate(), "yyyy-MM-dd",
							"dd/MM/yyyy"), f2)));
					pdfPTable.addCell(new PdfPCell(new Paragraph(staticReports1
							.getNarration(), f2)));
					pdfPTable.addCell(new PdfPCell(new Paragraph(staticReports1
							.getCheqNo(), f2)));
					pdfPTable.addCell(new PdfPCell(new Paragraph(staticReports1
							.getInstNo(), f2)));
					pdfPTable.addCell(new PdfPCell(new Paragraph(staticReports1
							.getTxAmount(), f2)));
					pdfPTable.addCell(new PdfPCell(new Paragraph(staticReports1
							.getBalance(), f2)));
				}
				document.add(pdfPTable);
				document.add(new Paragraph("."));				
				StaticReports srps = list.get(list.size()-1);
				PdfPTable totTbl = new PdfPTable(5);
				totTbl.setWidthPercentage(100);
				totTbl.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
				totTbl.setWidths(new int[] { 8, 9, 4, 4, 4});
				totTbl.addCell(new PdfPCell(new Paragraph("Dr Count", f)));
				totTbl.addCell(new PdfPCell(new Paragraph("Cr Count", f)));
				totTbl.addCell(new PdfPCell(new Paragraph("Total Debit", f)));
				totTbl.addCell(new PdfPCell(new Paragraph("Total Credit", f)));
				totTbl.addCell(new PdfPCell(new Paragraph("Closing Balance", f)));
				totTbl.addCell(new PdfPCell(new Paragraph(sr.getDrCount(), f)));
				totTbl.addCell(new PdfPCell(new Paragraph(sr.getCrCount(), f)));
				totTbl.addCell(new PdfPCell(new Paragraph(sr.getTotalDr(), f)));
				totTbl.addCell(new PdfPCell(new Paragraph(sr.getTotalCr(), f)));
				totTbl.addCell(new PdfPCell(new Paragraph(srps.getBalance(), f)));
				document.add(totTbl);
				document.close();
				byte[] pdfBytes = byteArrayOutputStream.toByteArray();
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType
						.parseMediaType("application/pdf"));
				String filename = sr.getAccNum() + ".pdf";
				headers.setContentDispositionFormData("filename", filename);
				pdfResponse = new ResponseEntity<byte[]>(pdfBytes, headers,
						HttpStatus.OK);
			} catch (DocumentException e) {
				e.printStackTrace();
				logger.info(e.getMessage() + LocalTime.now().toString());
			}finally{
				file.delete();
			}
		}
		logger.info("EXIT FROM printReportByAcc() : "
				+ LocalTime.now().toString());
		return pdfResponse;
	}

	public static PdfPCell getCell(String text, int alignment, int fontSize) {
		PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(
				FontFactory.TIMES_ROMAN, fontSize)));
		cell.setPadding(3);
		cell.setHorizontalAlignment(alignment);
		cell.setBorder(PdfPCell.NO_BORDER);
		return cell;
	}

	public static PdfPCell getCell1(String text, int alignment, int fontSize) {
		PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(
				FontFactory.TIMES_ROMAN, fontSize)));
		cell.setPadding(0);
		cell.setHorizontalAlignment(alignment);
		cell.setBorder(PdfPCell.NO_BORDER);
		return cell;
	}

	public long getDateDiff(String date1, String date2) throws IOException {
		final DateTimeFormatter formatter = DateTimeFormatter
				.ofPattern("dd/MM/yyyy");
		final LocalDate firstDate = LocalDate.parse(date1, formatter);
		final LocalDate secondDate = LocalDate.parse(date2, formatter);
		final long days = ChronoUnit.DAYS.between(firstDate, secondDate);
		return days;
	}
	@RequestMapping(value = MappingConstant.DEPOSITSTATEMENT, method = RequestMethod.GET)
	public ModelAndView depositstmt(ModelAndView model,
			HttpServletRequest request) {
		logger.info("ENTER into depositstmt : " + LocalTime.now().toString());
		ModelAndView modelAndView = new ModelAndView();
		HttpSession session = request.getSession();
		TellerMaster tellerMaster = new TellerMaster();
		if (session.getAttribute("user") != null) {
			modelAndView.setViewName("depositstmtReportByAcc");
		} else {
			modelAndView.setViewName(CebiConstant.LOGIN);
			modelAndView.addObject("loginForm", tellerMaster);
			modelAndView.addObject("SESSION_LOGOUT",
					CebiConstant.SESSION_LOGOUT);
		}
		logger.info("Exit From depositstmt :" + LocalTime.now().toString());
		return modelAndView;
	}
	@RequestMapping(value = MappingConstant.DEPOSITSTMTDETAILS, method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> getDepositStmtDetails(
			@RequestBody StaticReports staticReports, HttpServletRequest request) {
		
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		logger.info("ENTER into getDepositStmtDetails() :" + LocalTime.now().toString());
		if (!staticReports.getAccNum().equals("")
				&& !staticReports.getToDate().equals("")
				&& !staticReports.getFrDate().equals("")) {
			long daysDiff = 0;
			HttpSession session = request.getSession();
			List<Object[]> master = (List<Object[]>) session.getAttribute("auditHistory");
			TellerMaster tellerMaster = new TellerMaster();
			for (Object[] object : master) {
			    tellerMaster.setTellerid(object[0].toString());
			    tellerMaster.setBranchid(Long.parseLong(object[2].toString()));
			    tellerMaster.setBankCode(object[3].toString());
			}
			String bankCode = (String) session
					.getAttribute(CebiConstant.BANK_CODE);
			try {
				daysDiff = getDateDiff(staticReports.getFrDate(),
						staticReports.getToDate());
			} catch (IOException e) {
				e.printStackTrace();
				logger.info(e.getMessage());
			}
			if (daysDiff <= 365 && daysDiff >= 0) {
				staticReports.setFrDate(dateConvert(staticReports.getFrDate(),
						"dd/MM/yyyy", "yyyyMMdd"));
				staticReports.setToDate((dateConvert(staticReports.getToDate(),
						"dd/MM/yyyy", "yyyyMMdd")));
				List<StaticReports> list = staticReportAccService
						.getDepositStmtDetailyByAccountNo(staticReports, bankCode,tellerMaster);
				if (list.isEmpty()) {
					map.put("error", 2);
				} else {
					map.put("list", list);
				}
			} else {
				map.put("error", 1);
			}
			logger.info("Exit From getDepositStmtDetails() :"
					+ LocalTime.now().toString());
		} else {
			map.put("error", 3);
		}

		return map;
	}
	
	
	
	
	@RequestMapping(value = MappingConstant.PRINTQUERYDEPOSITSTMT, method = RequestMethod.POST)
	public ResponseEntity<byte[]> printDepositStmtByAcc(
			@ModelAttribute("MAFORMDATA") StaticReports staticReports,
			HttpServletRequest request) throws FileNotFoundException {
		logger.info("ENTER into printDepositStmtByAcc() : "
				+ LocalTime.now().toString());
		File file =null;
		HttpSession session = request.getSession();
		List<Object[]> master = (List<Object[]>) session.getAttribute("auditHistory");
		TellerMaster tellerMaster = new TellerMaster();
		for (Object[] object : master) {
		    tellerMaster.setTellerid(object[0].toString());
		    tellerMaster.setBranchid(Long.parseLong(object[2].toString()));
		    tellerMaster.setBankCode(object[3].toString());
		}
		String bankCode = (String) session.getAttribute(CebiConstant.BANK_CODE);
		List<Banks> banks = staticReportAccService.getBankNames(bankCode);
		String bankName = "";
		for (Banks bank : banks) {
			if (bank.getBankCode().equalsIgnoreCase(bankCode)) {
				bankName = bank.getBankName();
			}
		}
		staticReports.setFrDate(dateConvert(staticReports.getFrDate(),
				"dd/MM/yyyy", "yyyyMMdd"));
		staticReports.setToDate((dateConvert(staticReports.getToDate(),
				"dd/MM/yyyy", "yyyyMMdd")));
		ResponseEntity<byte[]> pdfResponse = null;
		List<StaticReports> list = staticReportAccService.getDepositStmtDetailyByAccountNo(
				staticReports, bankCode,tellerMaster);
		if (!list.isEmpty()) {
			StaticReports sr = list.get(0);

			try {
				Document document = new Document(PageSize.A4);
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				PdfWriter writer = PdfWriter.getInstance(document,
						byteArrayOutputStream);
				document.open();
				Font normalTable = new Font(Font.FontFamily.COURIER, 14f,
						Font.NORMAL);
				Font normalH = new Font(Font.FontFamily.HELVETICA, 14f,
						Font.BOLD);
				Font normal = new Font(Font.FontFamily.HELVETICA, 13f,
						Font.NORMAL);
				Font bold = new Font(Font.FontFamily.COURIER, 12f, Font.BOLD);
				Integer banchno = (Integer) request.getSession().getAttribute(
						"branchNo");
				Integer tellerId = (Integer) request.getSession().getAttribute(
						"tellerNo");
				int hed[] = { 80 };
				 file = new File(sr.getAccNum() + ".pdf");
					Image img = null;
					String jpgpath="/bancedge//images//bank_logo//"+bankCode+".jpg";
					String gifpath="/bancedge//images//bank_logo//"+bankCode+".gif";
					File giffilepath=new File(gifpath);
					File jpgfilepath=new File(jpgpath);
					if(jpgfilepath.exists() && !giffilepath.exists())
					{
						try {
							img = Image.getInstance(jpgpath);
							img.setAlignment(Image.LEFT);
							img.scaleToFit(200f, 100f);
							document.add(img);
						} catch (MalformedURLException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					if(giffilepath.exists() && !jpgfilepath.exists())
					{
						try {
							img = Image.getInstance(gifpath);
							img.setAlignment(Image.LEFT);
							img.scaleToFit(200f, 100f);
							document.add(img);
						} catch (MalformedURLException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				Font f = new Font(FontFamily.TIMES_ROMAN, 10.0f, Font.BOLD,
						BaseColor.BLACK);
				PdfPTable table = new PdfPTable(2);
				table.setWidthPercentage(100);
				table.addCell(getCell(sr.getCustName().trim(),
						PdfPCell.ALIGN_LEFT, 12));
				table.addCell(getCell(bankName.substring(5, bankName.length()),
						PdfPCell.ALIGN_RIGHT, 13));
				table.addCell(getCell(sr.getAddress(), PdfPCell.ALIGN_LEFT, 10));
				table.addCell(getCell(sr.getJournalNo(), PdfPCell.ALIGN_RIGHT,
						10));
				document.add(table);

				PdfPTable tbl = new PdfPTable(2);
				tbl.setWidthPercentage(100);
				tbl.addCell(getCell("Account No : " + sr.getAccNum(),
						PdfPCell.ALIGN_LEFT, 12));
				tbl.addCell(getCell("Customer No :" + sr.getCustNo(),
						PdfPCell.ALIGN_RIGHT, 12));
				tbl.addCell(getCell("Branch Code : " + sr.getTlrBranch(),
						PdfPCell.ALIGN_LEFT, 12));
				tbl.addCell(getCell("Mobile No : " + sr.getMobNO(),
						PdfPCell.ALIGN_RIGHT, 12));
				tbl.addCell(getCell("Date : " + sr.getCurrDate(),
						PdfPCell.ALIGN_LEFT, 12));
				tbl.addCell(getCell("Time : " + sr.getCurrTime(),
						PdfPCell.ALIGN_RIGHT, 12));
				tbl.addCell(getCell("Current Balance  : " + sr.getCurrBalace(),
						PdfPCell.ALIGN_LEFT, 12));
				document.add(tbl);
				PdfPTable tb2 = new PdfPTable(1);
				tb2.setWidthPercentage(100);
				staticReports.setFrDate(dateConvert(staticReports.getFrDate(),
						"yyyyMMdd", "dd/MM/yyyy"));
				staticReports.setToDate((dateConvert(staticReports.getToDate(),
						"yyyyMMdd", "dd/MM/yyyy")));
				tb2.addCell(getCell("Date From " + staticReports.getFrDate()
						+ " To " + staticReports.getToDate(),
						PdfPCell.ALIGN_CENTER, 12));
				document.add(tb2);
				document.add(new Paragraph(" "));
				PdfPTable pdfPTable = new PdfPTable(8);
				pdfPTable.setWidthPercentage(100);
				pdfPTable.setWidths(new int[] { 1, 3, 3, 7, 3, 4, 4, 4 });
				Font f2 = new Font(FontFamily.TIMES_ROMAN, 10.0f, Font.NORMAL,
						BaseColor.BLACK);
				int i = 0;
				// Create cells
				pdfPTable.addCell(new PdfPCell(new Paragraph("Sr.No", f)));
				pdfPTable.addCell(new PdfPCell(new Paragraph("Post Date", f)));
				pdfPTable.addCell(new PdfPCell(new Paragraph("Value Date", f)));
				pdfPTable.addCell(new PdfPCell(new Paragraph("Details", f)));
				pdfPTable.addCell(new PdfPCell(new Paragraph("Cheque No", f)));
				pdfPTable.addCell(new PdfPCell(new Paragraph("Debit", f)));
				pdfPTable.addCell(new PdfPCell(new Paragraph("Credit", f)));
				pdfPTable.addCell(new PdfPCell(new Paragraph("Balance", f)));
				for (StaticReports staticReports1 : list) {
					i++;
					String srno = Integer.toString(i);
					pdfPTable.addCell(new PdfPCell(new Paragraph(srno, f2)));
					pdfPTable.addCell(new PdfPCell(new Paragraph(dateConvert(
							staticReports1.getPostTime(), "yyyy-MM-dd",
							"dd/MM/yyyy"), f2)));
					pdfPTable.addCell(new PdfPCell(new Paragraph(dateConvert(
							staticReports1.getTxDate(), "yyyy-MM-dd",
							"dd/MM/yyyy"), f2)));
					pdfPTable.addCell(new PdfPCell(new Paragraph(staticReports1
							.getNarration(), f2)));
					pdfPTable.addCell(new PdfPCell(new Paragraph(staticReports1
							.getCheqNo(), f2)));
					pdfPTable.addCell(new PdfPCell(new Paragraph(staticReports1
							.getInstNo(), f2)));
					pdfPTable.addCell(new PdfPCell(new Paragraph(staticReports1
							.getTxAmount(), f2)));
					pdfPTable.addCell(new PdfPCell(new Paragraph(staticReports1
							.getBalance(), f2)));
				}
				document.add(pdfPTable);
				document.add(new Paragraph("."));				
				StaticReports srps = list.get(list.size()-1);
				PdfPTable totTbl = new PdfPTable(5);
				totTbl.setWidthPercentage(100);
				totTbl.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
				totTbl.setWidths(new int[] { 8, 9, 4, 4, 4});
				totTbl.addCell(new PdfPCell(new Paragraph("Dr Count", f)));
				totTbl.addCell(new PdfPCell(new Paragraph("Cr Count", f)));
				totTbl.addCell(new PdfPCell(new Paragraph("Total Debit", f)));
				totTbl.addCell(new PdfPCell(new Paragraph("Total Credit", f)));
				totTbl.addCell(new PdfPCell(new Paragraph("Closing Balance", f)));
				totTbl.addCell(new PdfPCell(new Paragraph(sr.getDrCount(), f)));
				totTbl.addCell(new PdfPCell(new Paragraph(sr.getCrCount(), f)));
				totTbl.addCell(new PdfPCell(new Paragraph(sr.getTotalDr(), f)));
				totTbl.addCell(new PdfPCell(new Paragraph(sr.getTotalCr(), f)));
				totTbl.addCell(new PdfPCell(new Paragraph(srps.getBalance(), f)));
				document.add(totTbl);
				document.close();
				byte[] pdfBytes = byteArrayOutputStream.toByteArray();
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType
						.parseMediaType("application/pdf"));
				String filename = sr.getAccNum() + ".pdf";
				headers.setContentDispositionFormData("filename", filename);
				pdfResponse = new ResponseEntity<byte[]>(pdfBytes, headers,
						HttpStatus.OK);
			} catch (DocumentException e) {
				e.printStackTrace();
				logger.info(e.getMessage() + LocalTime.now().toString());
			}finally{
				file.delete();
			}
		}
		logger.info("EXIT FROM printDepositStmtByAcc() : "
				+ LocalTime.now().toString());
		return pdfResponse;
	}
}
