package com.tm.broadband.controller;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.tm.broadband.model.Broadband;
import com.tm.broadband.model.Customer;
import com.tm.broadband.util.broadband.BroadbandCapability;

@RestController
@SessionAttributes(value = { "customer", "orderCustomer"})
public class BroadbandRestController {

	public BroadbandRestController() {
	}
	
	@RequestMapping("/address/check/{address}")
	public Broadband checkAddress(@PathVariable("address") String address,
			HttpSession session) {
		Customer customer = (Customer) session.getAttribute("customerReg");
		if (customer != null) {
			return returnBroadband(address, customer);
		}
		return null;
		
	}
	
	@RequestMapping("/sale/address/check/{address}")
	public Broadband checkAddressSale(@PathVariable("address") String address,
			@ModelAttribute("orderCustomer") Customer customer) {
		return returnBroadband(address, customer);
	}
	
	private Broadband returnBroadband(String address, Customer customer) {
		String message = "";
		try {
			message = BroadbandCapability.getCapabilityResultByAddress(address);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Broadband broadband = new Broadband();
		customer.setBroadband(broadband);
		broadband.setAddress(address);
		String services_available = "";
		if (message.contains("> 10")) {
			broadband.setAdsl_available(true);
			services_available += "ADSL";
		} 
		if (message.contains("> 20")) {
			broadband.setVdsl_available(true);
			services_available += "VDSL";
		} 
		if (message.contains("Business fibre available") || message.contains("Network capability:<\\/h4><ul><li>UFB fibre up to 100 Mbps")) {
			broadband.setUfb_available(true);
			services_available += "UFB";
		} 
		broadband.setScheduled(message.substring(message.lastIndexOf(",") + 1));
		broadband.setServices_available(services_available);
		customer.setAddress(address);
		
		customer.setServiceAvailable(false);
		
		if ("ADSL".equals(customer.getSelect_plan_type()) && broadband.isAdsl_available()) {
			customer.setServiceAvailable(true);
		}
		if ("VDSL".equals(customer.getSelect_plan_type()) && broadband.isVdsl_available()) {
			customer.setServiceAvailable(true);
		}
		if ("UFB".equals(customer.getSelect_plan_type()) && broadband.isUfb_available()) {
			customer.setServiceAvailable(true);
		}
		return broadband;
	}
	
	@RequestMapping(value = "/do/service")
	public void doServiceAvailable(HttpSession session) {

		Customer customer = (Customer) session.getAttribute("customerReg");
		
		if (customer != null) {
			customer.setServiceAvailable(true);
			System.out.println("do service...");
		}

	}

}
