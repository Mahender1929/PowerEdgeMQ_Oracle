/*package com.cebi.controller;

import java.util.Map;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cebi.dao.CreateCsvDao;
import com.cebi.entity.ReportQueueData;
import com.cebi.entity.TellerMaster;
import com.cebi.service.AdminReportService;
import com.cebi.service.CreateCsvService;
import com.cebi.utility.CebiConstant;
import com.cebi.utility.Constants;

@EnableRabbit
@Component
public class RabbitMqMessageListener {

	  @Autowired 
	  CreateCsvService createCsvService;
	  
	  @Autowired 
	  AdminReportService adminReportService;
	  
	  @Autowired 
	  CreateCsvDao createCsvDao;

	@RabbitListener(queues = Constants.queueName)
	public void processQueue(Map<String, Object> message) {
		try {
			String reportType = message.get("event").toString();
			int id = (int) message.get("message");
			TellerMaster master = (TellerMaster)message.get("master");
			adminReportService.updatereportStatus(id,CebiConstant.IN_PROCESS);
			ReportQueueData reportQueueData = createCsvDao.getReportQueueData(id);
            reportQueueData.getQueuedataid().setReporttype(reportType);
			reportQueueData.getQueuedataid().setReportDataId(id);
			adminReportService.buildSqlQuery(reportQueueData.getQueuedataid(), reportQueueData.getBank(), master);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}*/