package com.cebi.service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.internal.SessionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cebi.entity.ReportDownload;
import com.cebi.entity.ReportHistory;
import com.cebi.entity.ReportQueueData;
import com.cebi.utility.CebiConstant;


@Service
@Transactional
public class DownloadServiceImpl implements DownloadService {

	@Autowired
	CebiConstant cebiConstant;

	@Autowired
	SessionFactory sessionFactory;

	@Override
	public List<ReportDownload> getReportStatus(ReportDownload reportDownload) {
		String TBL_GET_STS_Q = "SELECT id,fileName,queuedataid,status,timeadded,timecomplete FROM reportqueuetable WHERE bank = ? and to_char(timeadded,'DD-MM-YYYY') = ?";
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		List<ReportDownload> names = new ArrayList<>();
		try {
			Session session = sessionFactory.openSession();
			connection = ((SessionImpl) session).connection();
			prepareStatement = connection.prepareStatement(TBL_GET_STS_Q);
			prepareStatement.setString(1, reportDownload.getBranCode());
			prepareStatement.setDate(2,Date.valueOf(reportDownload.getDate()));
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				if (resultSet != null) {
					reportDownload = new ReportDownload();
					reportDownload.setId(resultSet.getString("id"));
					if (resultSet.getString("fileName") == null) {
						reportDownload.setFileName("-----------");
					} else {
						reportDownload.setFileName(resultSet
								.getString("fileName"));
					}
					if (resultSet.getString("timecomplete") == null) {
						reportDownload.setEndTime("----------");
					} else {
						reportDownload.setEndTime(resultSet
								.getString("timecomplete"));
					}
					reportDownload.setStartTime(resultSet
							.getString("timeadded"));
					reportDownload.setStatus(resultSet.getString("status"));
					reportDownload.setQueId(resultSet.getString("queuedataid"));
					names.add(reportDownload);
				}
			}
		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) { /* ignored */
				}
			}
			if (prepareStatement != null) {
				try {
					prepareStatement.close();
				} catch (SQLException e) { /* ignored */
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) { /* ignored */
				}
			}
		}
		return names;
	}

	@Override
	public String getReportDelete(ReportDownload reportDownload) {
		Session session = sessionFactory.openSession();
		String msge = null;
		int foo = Integer.parseInt(reportDownload.getId());
		Object o = session.load(ReportQueueData.class, new Integer(foo));
		ReportQueueData reportQueueData = (ReportQueueData) o;
		Transaction tx = session.beginTransaction();
		if (reportDownload.getStatus().equalsIgnoreCase("INPROCESS")) {
			reportQueueData.setStatus("STOPPED");
			msge = "QueueID  :" + foo + " - Stop Sucessfully";
		} else {
			reportQueueData.setStatus("DELETED");
			msge = "QueueID  :" + foo + " - Deleted Sucessfully";
		}
		session.saveOrUpdate(reportQueueData);
		tx.commit();
		session.close();
		return msge;

	}

	@Override
	public List<ReportHistory> getReportHistory(ReportDownload reportDownload) {
		List<ReportHistory> histories = new ArrayList<ReportHistory>();
		List<ReportDownload> lst = getReportStatus(reportDownload);
		for (ReportDownload rd : lst) {
			if (rd.getStatus().equalsIgnoreCase("COMPLETED" )&& !rd.getEndTime().equals("----------") ) {
				ReportHistory history = new ReportHistory();
				history.settext(rd.getFileName());
				String strtTm =rd.getStartTime().replaceAll(reportDownload.getDate(),"").trim();
				strtTm = timeConversion(strtTm);
				history.setStartTime(strtTm);
                String endTm = rd.getEndTime().replaceAll(reportDownload.getDate(),"").trim();
                endTm = timeConversion(endTm);
                history.setEndTime(endTm);
				history.setMTime(rd.getQueId());
				histories.add(history);
			}
		}
		return histories;
	}

	public static String timeConversion(String _24HourTime ) {
	       try {       
	          /* String _24HourTime = "22:15";*/
	           SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
	           SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
	           java.util.Date _24HourDt = _24HourSDF.parse(_24HourTime);
	           _24HourTime= _12HourSDF.format(_24HourDt);
	       } catch (Exception e) {
	           e.printStackTrace();
	       }
		return _24HourTime;
	
	}
}
