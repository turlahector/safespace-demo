package com.safespace.controller;

import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CrossOriginController {

	
	@RequestMapping("/crossOrigin")
	public  ModelAndView  welcome(Map<String, Object> model) {
		ModelAndView c = new ModelAndView("crossOrigin");
		
		return c;
	}
	

}