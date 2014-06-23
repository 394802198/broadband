package com.tm.broadband.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tm.broadband.model.CustomerOrder;
import com.tm.broadband.model.DateUsage;
import com.tm.broadband.model.JSONBean;
import com.tm.broadband.model.NetworkUsage;
import com.tm.broadband.model.Page;
import com.tm.broadband.service.DataService;
import com.tm.broadband.util.TMUtils;

@RestController
public class DataRestController {
	
	private DataService dataService;

	@Autowired
	public DataRestController(DataService dataService) {
		this.dataService = dataService;
	}
	
	
	@RequestMapping("/broadband-user/data/calculator-usage/{calculator_date}")
	public JSONBean<NetworkUsage> calculatorUsage(
			@PathVariable("calculator_date") String calculator_date){
		
		String[] date = calculator_date.split("-");
		int year = Integer.parseInt(date[0]);
		int month = Integer.parseInt(date[1]);
		int last_month = Integer.parseInt(date[1]) - 1;
		
		JSONBean<NetworkUsage> json = new JSONBean<NetworkUsage>();
		
		NetworkUsage usage = new NetworkUsage();
		usage.getParams().put("year", year);
		usage.getParams().put("month", month);
		usage.getParams().put("last_month", last_month);
		
		dataService.calculatorUsage(usage);
		
		json.getSuccessMap().put("alert-success", year + "-" + last_month + ", " + year + "-" + month + " Network Usage have been update.");
		
		return json;
	}
	
	
	@RequestMapping("/broadband-user/data/customer/view/{pageNo}/{calculator_date}")
	public Page<CustomerOrder> doDataCustomerView(
			@PathVariable("pageNo") int pageNo,
			@PathVariable("calculator_date") String calculator_date){
		
		Page<CustomerOrder> page = new Page<CustomerOrder>();
		page.setPageNo(pageNo);
		page.setPageSize(30);
		//page.getParams().put("orderby", "order by plan_status desc, plan_type");
		
		this.dataService.queryDataCustomersByPage(page);
		
		String[] date = calculator_date.split("-");
		int year = Integer.parseInt(date[0]);
		int month = Integer.parseInt(date[1]);
		
		NetworkUsage usage = new NetworkUsage();
		usage.getParams().put("currentYear", year);
		usage.getParams().put("currentMonth", month);
		
		List<NetworkUsage> usages = this.dataService.queryCurrentMonthUsages(usage);
		
		List<CustomerOrder> cos = page.getResults();
		if (cos != null && usages != null) {
			String vlan = "", svlan = "", cvlan = "", type = "";
			for (CustomerOrder co: cos) {
				svlan = co.getSvlan() != null ? co.getSvlan().toLowerCase() : "";
				cvlan = co.getCvlan() != null ? co.getCvlan().toLowerCase() : "";
				type = co.getCod().getDetail_plan_type();
				if (!"".equals(svlan) && !"".equals(cvlan)) {
					if ("ADSL".equals(type)) {
						vlan = "d" + svlan + "/" + String.valueOf(Integer.parseInt(cvlan) + 1600);
					} else if ("VDSL".equals(type)){
						vlan = "d" + svlan + "/" + cvlan;
					} else if ("UFB".equals(type)) {
						vlan = "u" + svlan + "/" + cvlan;
					}
				}
				
				for (NetworkUsage u : usages) {
					if (u.getVlan() != null && vlan.equals(u.getVlan().toLowerCase())) {
						co.setUsage(u);
						break;
					}
				}
			}
		}
		
		return page;
		
	}
	
	
	@RequestMapping("/broadband-user/data/customer/usage/view/{svlan}/{cvlan}/{type}/{calculator_date}")
	public List<DateUsage> doCustomerUsageView(
			@PathVariable("svlan") String svlan,
			@PathVariable("cvlan") String cvlan,
			@PathVariable("type") String type,
			@PathVariable("calculator_date") String calculator_date){
		
		Calendar c = Calendar.getInstance();

		String[] date_array = calculator_date.split("-");
		int year = Integer.parseInt(date_array[0]);
		int month = Integer.parseInt(date_array[1]);

		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month - 1);
		
		int days = TMUtils.judgeDay(year, month);

		List<DateUsage> dateUsages = new ArrayList<DateUsage>();
		for (int i = 0; i < days; i++) {
			c.set(Calendar.DAY_OF_MONTH, i + 1);
			Date date = c.getTime();
			
			DateUsage dateUsage = new DateUsage();
			dateUsage.setDate(TMUtils.dateFormatYYYYMMDD(date));
			dateUsages.add(dateUsage);
		}
		
		NetworkUsage u = new NetworkUsage();
		String vlan = "";
		u.getParams().put("where", "query_currentMonth");
		if ("ADSL".equals(type)) {
			vlan = "d" + svlan + "/" + String.valueOf(Integer.parseInt(cvlan) + 1600);
		} else if ("VDSL".equals(type)){
			vlan = "d" + svlan + "/" + cvlan;
		} else if ("UFB".equals(type)) {
			vlan = "u" + svlan + "/" + cvlan;
		}
		u.getParams().put("vlan", vlan);
		u.getParams().put("currentYear", year);
		u.getParams().put("currentMonth", month);
		
		List<NetworkUsage> usages = this.dataService.queryUsages(u);

		if (usages != null && usages.size() > 0) {
			for (NetworkUsage usage: usages) {
				for (DateUsage dateUsage: dateUsages) {
					//System.out.println(TMUtils.dateFormatYYYYMMDD(usage.getAccounting_date()));
					if (dateUsage.getDate().equals(TMUtils.dateFormatYYYYMMDD(usage.getAccounting_date()))) {
						dateUsage.setUsage(usage);
						System.out.println(TMUtils.dateFormatYYYYMMDD(usage.getAccounting_date()));
						break;
					}
				}
			}
		}
		
		return dateUsages;
		
	}
	

}
