package de.dema.pd3.controller;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LogPageController {

	@Autowired
	private CircularFifoQueue<String> logmessages; 

	@GetMapping("/log")
	public String logPage(Model model) {
		model.addAttribute("messages", logmessages);
		return "log";
	}

}
