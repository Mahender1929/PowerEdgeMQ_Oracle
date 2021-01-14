/*package com.cebi.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cebi.entity.TellerMaster;
import com.cebi.utility.Constants;

@Service
public class EventPublisherService {
	
	@Autowired
	AmqpTemplate template;

	public void publishEvent(com.cebi.rabbitqueue.Events event, Object messages,TellerMaster master) throws IOException, TimeoutException {
		Map<String, Object> message = new HashMap<String, Object>();
		message.put("event", event);
		message.put("message", messages);
		message.put("master", master);
		template.convertAndSend(Constants.queueName, message);
	}

}*/