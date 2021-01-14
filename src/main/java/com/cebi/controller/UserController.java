package com.cebi.controller;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/*import com.cebi.rabbitqueue.Events;
import com.cebi.service.EventPublisherService;*/

/*@Controller
@RequestMapping("/")*/
public class UserController {
	/*@Autowired
	EventPublisherService eventPublisherService;*/

	/*@RequestMapping("/rabbitMq")
	public ModelAndView welcome() throws IOException, TimeoutException {

		for (int i = 0; i <= 10; i++)
			eventPublisherService.publishEvent(Events.Event1, i);
		return new ModelAndView("index", "message", "Implemented");
	}*/
}