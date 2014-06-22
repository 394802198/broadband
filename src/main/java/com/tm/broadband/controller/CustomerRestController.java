package com.tm.broadband.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.google.code.kaptcha.Constants;
import com.tm.broadband.email.ApplicationEmail;
import com.tm.broadband.model.CompanyDetail;
import com.tm.broadband.model.ContactUs;
import com.tm.broadband.model.Customer;
import com.tm.broadband.model.CustomerOrder;
import com.tm.broadband.model.DateUsage;
import com.tm.broadband.model.JSONBean;
import com.tm.broadband.model.NetworkUsage;
import com.tm.broadband.model.Notification;
import com.tm.broadband.service.CRMService;
import com.tm.broadband.service.DataService;
import com.tm.broadband.service.MailerService;
import com.tm.broadband.service.SmserService;
import com.tm.broadband.service.SystemService;
import com.tm.broadband.util.CheckScriptInjection;
import com.tm.broadband.util.MailRetriever;
import com.tm.broadband.util.TMUtils;
import com.tm.broadband.validator.mark.ChangePasswordValidatedMark;
import com.tm.broadband.validator.mark.ContactUsValidatedMark;
import com.tm.broadband.validator.mark.CustomerForgottenPasswordValidatedMark;
import com.tm.broadband.validator.mark.CustomerLoginValidatedMark;
import com.tm.broadband.validator.mark.CustomerOrganizationValidatedMark;
import com.tm.broadband.validator.mark.CustomerValidatedMark;

@RestController
@SessionAttributes(value = { "customer", "orderPlan"})
public class CustomerRestController {
	
	private CRMService crmService;
	private MailerService mailerService;
	private SmserService smserService;
	private SystemService systemService;
	private DataService dataService;

	@Autowired
	public CustomerRestController(CRMService crmService, MailerService mailerService, SystemService systemService, SmserService smserService, DataService dataService) {
		this.crmService = crmService;
		this.mailerService = mailerService;
		this.smserService = smserService;
		this.systemService = systemService;
		this.dataService = dataService;
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public JSONBean<Customer> login(
			@Validated(CustomerLoginValidatedMark.class) Customer customer, BindingResult result, 
			HttpServletRequest req) {
		
		JSONBean<Customer> json = new JSONBean<Customer>();
		json.setModel(customer);
		
		if (result.hasErrors()) {
			TMUtils.setJSONErrorMap(json, result);
			return json;
		}

		customer.getParams().put("where", "when_login");
		customer.getParams().put("cellphone", customer.getLogin_name());
		customer.getParams().put("email", customer.getLogin_name());
		customer.getParams().put("password", customer.getPassword());
		customer.getParams().put("status", "active");
		Customer customerSession = this.crmService.queryCustomerWhenLogin(customer);

		if (customerSession == null) {
			json.getErrorMap().put("alert-error", "Incorrect account or password");
			return json;
		}
		
		req.getSession().setAttribute("customerSession", customerSession);
		json.setUrl("/customer/home/redirect");

		return json;
	}
	
	@RequestMapping(value = "/forgotten-password", method = RequestMethod.POST)
	public JSONBean<Customer> forgottenPassword(
			@Validated(CustomerForgottenPasswordValidatedMark.class) Customer customer,
			BindingResult result,
			@RequestParam("code") String code,
			HttpServletRequest req) {
		
		JSONBean<Customer> json = new JSONBean<Customer>();
		json.setModel(customer);
		
		// If contains script> value then this is a script injection
		if(CheckScriptInjection.isScriptInjection(customer)){
			json.getErrorMap().put("alert-error", "Malicious actions are not allowed!");
			return json;
		}
		
		// if verification does not matched!
		if(!code.equalsIgnoreCase(req.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY).toString().trim())){
			json.getErrorMap().put("code", "verification code does not matched!");
			return json;
		}

		if (result.hasErrors()) {
			TMUtils.setJSONErrorMap(json, result);
			return json;
		}
		
		if("email".equals(customer.getType())){
			
			customer.getParams().put("where", "query_forgotten_password_email");
			customer.setEmail(customer.getLogin_name());
			customer.getParams().put("email", customer.getLogin_name());
			
		} else if("cellphone".equals(customer.getType())){
			
			customer.getParams().put("where", "query_forgotten_password_cellphone");
			customer.setCellphone(customer.getLogin_name());
			customer.getParams().put("cellphone", customer.getLogin_name());
			
		}
		customer.getParams().put("status", "active");
		Customer customerSession = this.crmService.queryCustomer(customer);

		if (customerSession == null) {
			
			json.getErrorMap().put("alert-error", "email".equals(customer.getType()) ? "The email address you've just given isn't existed!" : "The mobile number you've just given isn't existed!");
			
		} else {
			customer.setPassword(TMUtils.generateRandomString(6));
			this.crmService.editCustomer(customer);
			
			CompanyDetail companyDetail = this.crmService.queryCompanyDetail();
			Notification notification = this.systemService.queryNotificationBySort("forgotten-password", "email");
			
			String msg = "";

			if("email".equals(customer.getType())){
				msg = "We’ve sent an email to your email address containing a random login password. Please check your spam folder if the email doesn’t appear within a few minutes.";
				MailRetriever.mailAtValueRetriever(notification, customer, companyDetail); // call mail at value retriever
				ApplicationEmail applicationEmail = new ApplicationEmail();
				applicationEmail.setAddressee(customer.getEmail());
				applicationEmail.setSubject(notification.getTitle());
				applicationEmail.setContent(notification.getContent());
				this.mailerService.sendMailByAsynchronousMode(applicationEmail);
				customer.getParams().put("email", customer.getLogin_name());
			} else if("cellphone".equals(customer.getType())){
				msg = "We’ve sent an message to your cellphone containing a random login password. Please check your spam folder if the message doesn’t appear within a few minutes.";
				notification = this.systemService.queryNotificationBySort("forgotten-password", "sms"); // get sms register template from db
				MailRetriever.mailAtValueRetriever(notification, customer, companyDetail);
				this.smserService.sendSMSByAsynchronousMode(customer.getCellphone(), notification.getContent()); // send sms to customer's mobile phone
				customer.getParams().put("cellphone", customer.getLogin_name());
			}
			
			json.getSuccessMap().put("alert-success", msg);
			
		}
		return json;
	}
	
	@RequestMapping(value = "/customer/change-password", method = RequestMethod.POST)
	public JSONBean<Customer> changePassword(
			@Validated(ChangePasswordValidatedMark.class) Customer customer, BindingResult result, 
			HttpServletRequest req) {
		
		Customer customerSession = (Customer)req.getSession().getAttribute("customerSession");
		
		JSONBean<Customer> json = new JSONBean<Customer>();
		json.setModel(customer);
		
		// If contains script> value then this is a script injection
		if(CheckScriptInjection.isScriptInjection(customer)){
			json.getErrorMap().put("alert-error", "Malicious actions are not allowed!");
			return json;
		}

		if (result.hasErrors()) {
			TMUtils.setJSONErrorMap(json, result);
			return json;
		}
		
		if (!customer.getPassword().equals(customer.getCk_password())) {
			json.getErrorMap().put("password", "new password and confirm password is different");
			return json;
		}
		
		if (!customer.getOld_password().equals(customerSession.getPassword())) {
			json.getErrorMap().put("old_password", "old password is wrong");
			return json;
		}
 
		Customer tempC = new Customer();
		tempC.setPassword(customer.getPassword());
		tempC.getParams().put("id", customerSession.getId());
		this.crmService.editCustomer(tempC);
		customerSession.setPassword(customer.getPassword());
		
		req.getSession().setAttribute("customerSession", customerSession);
		json.setUrl("/customer/change-password/redirect");

		return json;
	}

	@RequestMapping(value = "/order/personal", method = RequestMethod.POST)
	public JSONBean<Customer> doOrderPersonal(Model model,
			@Validated(CustomerValidatedMark.class) @RequestBody Customer customer, BindingResult result, 
			HttpServletRequest req) {

		model.addAttribute("customer", customer);
		JSONBean<Customer> json = this.returnJsonCustomer(customer, result);
		json.setUrl("/order/personal/confirm");
		
		return json;
	}	
	
	@RequestMapping(value = "/order/business", method = RequestMethod.POST)
	public JSONBean<Customer> doOrderBusiness(Model model,
			@Validated(CustomerOrganizationValidatedMark.class) @RequestBody Customer customer, BindingResult result, 
			HttpServletRequest req) {

		model.addAttribute("customer", customer);
		JSONBean<Customer> json = this.returnJsonCustomer(customer, result);
		json.setUrl("/order/business/confirm");
		
		return json;
	}
	
	private JSONBean<Customer> returnJsonCustomer(Customer customer, BindingResult result) {
		
		JSONBean<Customer> json = new JSONBean<Customer>();
		json.setModel(customer);
		
		// If contains script> value then this is a script injection
		if(CheckScriptInjection.isScriptInjection(customer)){
			json.getErrorMap().put("alert-error", "Malicious actions are not allowed!");
			return json;
		}
		
		if (result.hasErrors()) {
			TMUtils.setJSONErrorMap(json, result);
			return json;
		}

		Customer cValid = new Customer();
		cValid.getParams().put("where", "query_exist_customer_by_mobile");
		cValid.getParams().put("cellphone", customer.getCellphone());
		int count = this.crmService.queryExistCustomer(cValid);

		if (count > 0) {
			json.getErrorMap().put("cellphone", "is already in use");
			return json;
		}
		
		cValid.getParams().put("where", "query_exist_customer_by_email");
		cValid.getParams().put("email", customer.getEmail());
		count = this.crmService.queryExistCustomer(cValid);

		if (count > 0) {
			json.getErrorMap().put("email", "is already in use");
			return json;
		}
		return json;
	}
	
	@RequestMapping(value = "/contact-us", method = RequestMethod.POST)
	public JSONBean<ContactUs> doContactUs(Model model,
			@Validated(ContactUsValidatedMark.class) ContactUs contactUs, BindingResult result, 
			HttpServletRequest req) {
		
		JSONBean<ContactUs> json = new JSONBean<ContactUs>();
		
		// If contains script> value then this is a script injection
		if (CheckScriptInjection.isScriptInjection(contactUs)) {
			json.getErrorMap().put("alert-error", "Malicious actions are not allowed!");
			return json;
		}
		
		// if verification does not matched!
		if (!contactUs.getCode().equalsIgnoreCase(req.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY).toString().trim())) {
			json.getErrorMap().put("code", "Verification code does not matched!");
			return json;
		}

		if (result.hasErrors()) {
			TMUtils.setJSONErrorMap(json, result);
			return json;
		}
		
		contactUs.setStatus("new");
		contactUs.setSubmit_date(new Date());
		this.crmService.createContactUs(contactUs);
		
		json.getSuccessMap().put("alert-success", "Your request has been submitted, we will respond you as fast as we can.");

		return json;
	}
	
	@RequestMapping("/customer/data/view/{calculator_date}")
	public List<DateUsage> doCustomerUsageView(HttpServletRequest req,
			@PathVariable("calculator_date") String calculator_date){
		
		Customer customer = (Customer) req.getSession().getAttribute("customerSession");
		CustomerOrder co = customer.getCustomerOrders().get(0);
		String svlan = co.getSvlan()
				, cvlan = co.getCvlan();
		
		Calendar c = Calendar.getInstance();
		
		String[] date_array = calculator_date.split("-");
		int year = Integer.parseInt(date_array[0]);
		int month = Integer.parseInt(date_array[1]);
		
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month-1);
		
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
		u.getParams().put("where", "query_currentMonth");
		u.getParams().put("vlan", svlan + cvlan);
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
