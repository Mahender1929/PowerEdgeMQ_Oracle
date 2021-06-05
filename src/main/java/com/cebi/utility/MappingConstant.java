package com.cebi.utility;

public class MappingConstant {
	public static final String DEFAULT_PAGE = "/";
	public static final String LANDING_PAGE = "/landing";
	public static final String LOGOUT_PAGE = "/logout";
	public static final String TABLES = "/tables";
	public static final String REPORT_PAGE = "/report";
	public static final String RETRIVE_COLUMNS = "/retriveColumns";
	public static final String GET_TABLEDATA = "/getTableData";
	public static final String GET_TABLEDATAINCT = "/getTableDatainct";
	public static final String DOWNLOAD_TEXT = "/createText";
	public static final String DOWNLOAD_PDF = "/createPdf";
	public static final String DOWNLOAD_EXCEL = "/createExcel";
	public static final String DOWNLOAD_CSV = "/createCsv";
	public static final String DOWNLOAD_CSV_PIPE = "/createPipeCsv";
	public static final String DOWNLOAD_QUEUE = "/createQueue";
	public static final String DOWNLOAD_REPORT_PAGE = "/downloadreportPage";
	public static final String REPORTHISTORY = "/reporthistory";
	public static final String FAVOURITE = "/favourite";
	public static final String SHOW_FAVOURITE_LIST = "/showFavouriteList";
	public static final String GET_REPORT_DETAILS = "/getReportDetails";
	public static final String SHOW_CHART_DETAILS = "/showchartDetails";
	public static final String SHOW_CHART_RESULT = "/showchartResult";
	public static final String LANDING_DEFAULT_PAGE = "/landingDefaultPage";
	//public static final String BANK_REPORT_LOCATION= "/bancedge/PowerEdgeMQ/";
	public static final String BANK_REPORT_LOCATION= "D:/Mahender/CEBI_MQ_Repots/";
	public static final String STATIC_REPORT = "/staticreport";
	public static final String SA_5 = "/sa-5";
	public static final String HELP = "/help";
	public static final String VIEWDETAILS = "/viewDeatils";
	public static final String INFO = "1";
	public static final String STATIC_REPORT_PAGE = "/staticreportPage";
	public static final String PREDEFINE_REPORT_PAGE = "/predefinereports";	
	public static final String SAVE_REPORT = "/predefinerepo";	
	public static final String DELETE_SAVED_QRY = "/deletesaveqry";	
	public static final String GET_TABLE_DATA = "/getdatatable";
	
	public static final String DOWNLOAD_PR_CSV = "/prcsvdownload";
	public static final String DOWNLOAD_PR_TXT = "/prtxtdownload";
	public static final String DOWNLOAD_PR_XLS = "/prxlsdownload";
	
	public static final String STATICREPORTBYACC = "/getReportByAcc";
	public static final String DEPOSITSTATEMENT	="/getDepositstmt";
	public static final String GETDATABYACC = "/getReportDataByAcc";
	public static final String DEPOSITSTMTDETAILS = "/getDepositStmtDetails";
	public static final String QUERYSCRIPT ="SELECT rec_no, TO_NUMBER ( LPAD( ACCT_NO || CHECKDIGIT.GENERATE (ACCT_NO), 17, ' ' ) ) AS ACCT_NO, CASE WHEN TRAN_TYPE IN (01, 80) AND BASECONV.CONVDATA (VAR_AREA, 33, 8, 3) < 0 THEN 'DR ' WHEN TRAN_TYPE IN (01, 80) AND BASECONV.CONVDATA (VAR_AREA, 33, 8, 3) > 0 THEN 'CR' ELSE 'NONFIN' END AS TRNDRCR, CASE WHEN TRAN_TYPE IN (01, 80) THEN BASECONV.CONVDATA (VAR_AREA, 17, 4) ELSE 0 END AS TRNCODE, TRIM( (SELECT RPAD(TRIM(' ' FROM TX_DESC), 18, ' ') FROM ED2P WHERE INST_NO = '003' AND TRAN_CODE = LPAD( BASECONV.CONVDATA (VAR_AREA, 17, 4), 5, '0' )) || ' ' || (SELECT SUBSTR(VAR_AREA, 13, 49) FROM INCT@RRBINCT B WHERE N.ACCT_NO = B.ACCT_NO AND B.JRNL_NO = N.JRNL_NO AND B.TRAN_TYPE = '20' AND B.INST_NO = '003' AND N.REC_NO = (B.REC_NO + 1)) ) NARRATION, CASE WHEN TRAN_TYPE IN (01, 80) THEN BASECONV.CONVDATA (VAR_AREA, 33, 8, 3) ELSE 0 END AS TRNAMT, CASE WHEN TRAN_TYPE IN (01, 80) THEN BASECONV.CONVDATA (VAR_AREA, 41, 8, 3) ELSE 0 END AS BALANCE, TO_DATE ( TO_CHAR ( TO_DATE ('31-DEC-1899', 'DD-MM-YYYY') + TRAN_DATE, 'DD/MM/YYYY' ), 'DD/MM/YYYY' ) TRAN_DATE, TO_DATE ( TO_CHAR ( TO_DATE ('31-DEC-1899', 'DD-MM-YYYY') + POST_DATE, 'DD/MM/YYYY' ), 'DD/MM/YYYY' ) POST_DATE, CASE WHEN BASECONV.CONVDATA (VAR_AREA, 17, 4) IN ('51071', '51072', '51073') THEN BASECONV.CONVDATA (VAR_AREA, 29, 4) ELSE 0 END AS CHQNO, A.BRANCH_NO, A.CURR_BAL, SUBSTR( CUSTOMER_NO || CHECKDIGIT.GENERATE(SUBSTR(CUSTOMER_NO, 4, 16)), 1, 17 ) CUSTOMER_NO, RPAD( ( RTRIM( ( RTRIM(C.NAME1, ' ') || ' ' || RTRIM(C.MID_NAME, ' ') || ' ' || RTRIM(C.NAME2, ' ') ), ' ' ) ), 50, ' ' ) AS CUSTOMER_NAME, (RPAD( RTRIM(C.ADD1, ' ') || ' ' || RTRIM(C.ADD2, ' '), 40, ' ' ) || ' '|| RPAD( RTRIM(C.ADD3, ' ') || ' ' || RTRIM(C.ADD4, ' '), 40, ' ' )) AS ADD1, PHONE_NO_BUS, TELEX_NO AS MOB_NO, TRIM((SELECT RTRIM(BR_NAME)|| ' ' || RTRIM(ADDRESS_1)|| ' ' || RTRIM(ADDRESS_2)|| ' ' || RTRIM(ADDRESS_3)|| ' ' || RTRIM(POST_CODE)|| ' ' || PHONE_NO FROM BRHM WHERE KEY_1 = '00300000000000'||A.BRANCH_NO ))BRANCH_DETAILS FROM INVM A, INCT@RRBINCT N, CUSVAA C WHERE N.INST_NO = '003' AND C.INST_NO = '003' AND A.KEY_1 = N.INST_NO || N.ACCT_NO AND A.CUSTOMER_NO = C.CUST_NO AND N.ACCT_NO = 'XXXXXX' AND TRAN_DATE BETWEEN (to_char(to_date(ZZZZZZ,'YYYYMMDD'),'J')-2415020) AND (to_char(to_date(YYYYYY,'YYYYMMDD'),'J')-2415020) AND TRAN_TYPE IN (01, 80) AND C.DELI = 0 ORDER BY rec_no DESC";
	public static final String PRINTQUERYSCRIPTDATA = "/printdatabyacc";
	public static final String DEPOSITSTMTQUERYSCRIPT ="SELECT rec_no, TO_NUMBER ( LPAD( ACCT_NO || CHECKDIGIT.GENERATE (ACCT_NO), 17, ' ' ) ) AS ACCT_NO, CASE WHEN TRAN_TYPE IN (01, 80) AND BASECONV.CONVDATA (VAR_AREA, 33, 8, 3) < 0 THEN 'DR ' WHEN TRAN_TYPE IN (01, 80) AND BASECONV.CONVDATA (VAR_AREA, 33, 8, 3) > 0 THEN 'CR' ELSE 'NONFIN' END AS TRNDRCR, CASE WHEN TRAN_TYPE IN (01, 80) THEN BASECONV.CONVDATA (VAR_AREA, 17, 4) ELSE 0 END AS TRNCODE, TRIM( (SELECT RPAD(TRIM(' ' FROM TX_DESC), 18, ' ') FROM ED2P WHERE INST_NO = '003' AND TRAN_CODE = LPAD( BASECONV.CONVDATA (VAR_AREA, 17, 4), 5, '0' )) || ' ' || (SELECT SUBSTR(VAR_AREA, 13, 49) FROM INCT B WHERE N.ACCT_NO = B.ACCT_NO AND B.JRNL_NO = N.JRNL_NO AND B.TRAN_TYPE = '20' AND B.INST_NO = '003' AND N.REC_NO = (B.REC_NO + 1)) ) NARRATION, CASE WHEN TRAN_TYPE IN (01, 80) THEN BASECONV.CONVDATA (VAR_AREA, 33, 8, 3) ELSE 0 END AS TRNAMT, CASE WHEN TRAN_TYPE IN (01, 80) THEN BASECONV.CONVDATA (VAR_AREA, 41, 8, 3) ELSE 0 END AS BALANCE, TO_DATE ( TO_CHAR ( TO_DATE ('31-DEC-1899', 'DD-MM-YYYY') + TRAN_DATE, 'DD/MM/YYYY' ), 'DD/MM/YYYY' ) TRAN_DATE, TO_DATE ( TO_CHAR ( TO_DATE ('31-DEC-1899', 'DD-MM-YYYY') + POST_DATE, 'DD/MM/YYYY' ), 'DD/MM/YYYY' ) POST_DATE, CASE WHEN BASECONV.CONVDATA (VAR_AREA, 17, 4) IN ('51071', '51072', '51073') THEN BASECONV.CONVDATA (VAR_AREA, 29, 4) ELSE 0 END AS CHQNO, A.BRANCH_NO, A.CURR_BAL, SUBSTR( CUSTOMER_NO || CHECKDIGIT.GENERATE(SUBSTR(CUSTOMER_NO, 4, 16)), 1, 17 ) CUSTOMER_NO, RPAD( ( RTRIM( ( RTRIM(C.NAME1, ' ') || ' ' || RTRIM(C.MID_NAME, ' ') || ' ' || RTRIM(C.NAME2, ' ') ), ' ' ) ), 50, ' ' ) AS CUSTOMER_NAME, (RPAD( RTRIM(C.ADD1, ' ') || ' ' || RTRIM(C.ADD2, ' '), 40, ' ' ) || ' '|| RPAD( RTRIM(C.ADD3, ' ') || ' ' || RTRIM(C.ADD4, ' '), 40, ' ' )) AS ADD1, PHONE_NO_BUS, TELEX_NO AS MOB_NO, TRIM((SELECT RTRIM(BR_NAME)|| ' ' || RTRIM(ADDRESS_1)|| ' ' || RTRIM(ADDRESS_2)|| ' ' || RTRIM(ADDRESS_3)|| ' ' || RTRIM(POST_CODE)|| ' ' || PHONE_NO FROM BRHM WHERE KEY_1 = '00300000000000'||A.BRANCH_NO ))BRANCH_DETAILS FROM INVM A, INCT N, CUSVAA C WHERE N.INST_NO = '003' AND C.INST_NO = '003' AND A.KEY_1 = N.INST_NO || N.ACCT_NO AND A.CUSTOMER_NO = C.CUST_NO AND N.ACCT_NO = 'XXXXXX' AND TRAN_DATE BETWEEN (to_char(to_date(ZZZZZZ,'YYYYMMDD'),'J')-2415020) AND (to_char(to_date(YYYYYY,'YYYYMMDD'),'J')-2415020) AND TRAN_TYPE IN (01, 80) order by  rec_no desc";
	public static final String PRINTQUERYDEPOSITSTMT = "/printdepositStmnt";

}
