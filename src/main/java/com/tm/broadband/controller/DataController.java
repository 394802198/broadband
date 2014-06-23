package com.tm.broadband.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tm.broadband.model.CustomerOrder;
import com.tm.broadband.service.DataService;
import com.tm.broadband.util.TMUtils;

@Controller
public class DataController {
	
	private DataService dataService;

	@Autowired
	public DataController(DataService dataService) {
		this.dataService = dataService;
	}
	
	@RequestMapping("/broadband-user/data/operatre")
	public String toDataOperatre(Model model){
		
		model.addAttribute("panelheading", "Calculator Data Flow");
		return "broadband-user/data/data-operatre";
	}
	
	@RequestMapping("/broadband-user/data/customer/view")
	public String toDataCustomerView(Model model){
		
		model.addAttribute("panelheading", "Data Customer View");
		model.addAttribute("current_date", TMUtils.dateFormatYYYYMMDD(new Date()));
		return "broadband-user/data/customer-view";
		
	} 
	
	@RequestMapping("/broadband-user/data/customer/usage/view/{order_id}")
	public String toCustomerUsage(Model model,
			@PathVariable("order_id") int order_id) {
		
		CustomerOrder co = new CustomerOrder();
		co.getParams().put("id", order_id);
		
		co = this.dataService.queryDataCustomer(co);
		model.addAttribute("co", co);
		model.addAttribute("current_date", TMUtils.dateFormatYYYYMMDD(new Date()).substring(0, 7));
		
		return "broadband-user/data/customer-usage-view";
	}
	
	
	

}
