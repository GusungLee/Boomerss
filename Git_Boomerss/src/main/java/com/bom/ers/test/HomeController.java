package com.bom.ers.test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
	
	@GetMapping("/test")
	public String test() {
		return "test";
	}
	
	
}
