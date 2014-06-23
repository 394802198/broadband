package com.tm.broadband.controller;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itextpdf.text.DocumentException;
import com.tm.broadband.email.ApplicationEmail;
import com.tm.broadband.model.CompanyDetail;
import com.tm.broadband.model.Customer;
import com.tm.broadband.model.CustomerCredit;
import com.tm.broadband.model.CustomerOrder;
import com.tm.broadband.model.CustomerOrderDetail;
import com.tm.broadband.model.Hardware;
import com.tm.broadband.model.Notification;
import com.tm.broadband.model.Page;
import com.tm.broadband.model.Plan;
import com.tm.broadband.model.User;
import com.tm.broadband.pdf.CreditPDFCreator;
import com.tm.broadband.pdf.OrderPDFCreator;
import com.tm.broadband.service.CRMService;
import com.tm.broadband.service.MailerService;
import com.tm.broadband.service.PlanService;
import com.tm.broadband.service.SaleService;
import com.tm.broadband.service.SmserService;
import com.tm.broadband.service.SystemService;
import com.tm.broadband.util.MailRetriever;
import com.tm.broadband.util.TMUtils;
import com.tm.broadband.validator.mark.CustomerCreditValidatedMark;

@Controller
@SessionAttributes(value = {"orderCustomer", "orderCustomerOrderDetails", "orderPlan"})
public class SaleController {
	
	private PlanService planService;
	private CRMService crmService;
	private SaleService saleService;
	private MailerService mailerService;
	private SmserService smserService;
	private SystemService systemService;

	@Autowired
	public SaleController(PlanService planService, CRMService crmService, MailerService mailerService, SystemService systemService, SmserService smserService
			, SaleService saleService) {
		this.planService = planService;
		this.crmService = crmService;
		this.saleService = saleService;
		this.mailerService = mailerService;
		this.smserService = smserService;
		this.systemService = systemService;
	}
	
	@RequestMapping("/broadband-user/sale/online/ordering/plans/{class}")
	public String plans(Model model,
			@PathVariable("class") String classz) {
		
		model.addAttribute("classz", classz);
		
		Customer customer = new Customer();
		customer.getCustomerOrder().setOrder_broadband_type("transition");//new-connection
		model.addAttribute("orderCustomer", customer);
		
		List<Plan> plans = null;
		
		Plan plan = new Plan();
		plan.getParams().put("plan_group", "plan-term");
		plan.getParams().put("plan_class", classz);
		plan.getParams().put("plan_status", "selling");
		plan.getParams().put("orderby", "order by data_flow");
		
		plans = this.planService.queryPlans(plan);
		
		model.addAttribute("plans", plans);
		
		Hardware hardware = new Hardware();
		hardware.getParams().put("hardware_status", "selling");
		List<Hardware> hardwares = this.planService.queryHardwares(hardware);
		model.addAttribute("hardwares", hardwares);
		
		return "broadband-user/sale/online-ordering";
	}
	
	@RequestMapping("/broadband-user/sale/online/ordering/order/{id}")
	public String orderPlanTerm(Model model, @PathVariable("id") int id) {
		
		Plan plan = new Plan();
		plan.getParams().put("id", id);
		plan = this.planService.queryPlan(plan);
		model.addAttribute("orderPlan", plan);
		
		String url = "";
		if ("personal".equals(plan.getPlan_class())) {
			url = "broadband-user/sale/online-ordering-personal-info";
		} else if ("business".equals(plan.getPlan_class())) {
			url = "broadband-user/sale/online-ordering-business-info";
		}
		
		return url;
	}
	
	@RequestMapping(value = "/broadband-user/sale/online/ordering/order/confirm")
	public String orderConfirm(Model model,
			@ModelAttribute("orderPlan") Plan plan, 
			@ModelAttribute("orderCustomer") Customer customer, 
			@ModelAttribute("orderCustomerOrderDetails") List<CustomerOrderDetail> cods, 
			HttpServletRequest req,
			RedirectAttributes attr) {
		
		customer.getCustomerOrder().setCustomerOrderDetails(new ArrayList<CustomerOrderDetail>());
		customer.getCustomerOrder().getCustomerOrderDetails().addAll(cods);
		customer.getCustomerOrder().setOrder_create_date(new Date());
		customer.getCustomerOrder().setOrder_status("pending");
		customer.getCustomerOrder().setOrder_type(plan.getPlan_group().replace("plan", "order"));

		CustomerOrderDetail cod_plan = new CustomerOrderDetail();
		
		cod_plan.setDetail_name(plan.getPlan_name());
		cod_plan.setDetail_desc(plan.getPlan_desc());
		cod_plan.setDetail_price(plan.getPlan_price() == null ? 0d : plan.getPlan_price());
		cod_plan.setDetail_data_flow(plan.getData_flow());
		cod_plan.setDetail_plan_status(plan.getPlan_status());
		cod_plan.setDetail_plan_type(plan.getPlan_type());
		cod_plan.setDetail_plan_sort(plan.getPlan_sort());
		cod_plan.setDetail_plan_group(plan.getPlan_group());
		cod_plan.setDetail_plan_class(plan.getPlan_class());
		cod_plan.setDetail_plan_new_connection_fee(plan.getPlan_new_connection_fee());
		cod_plan.setDetail_term_period(plan.getTerm_period());
		customer.getCustomerOrder().setTerm_period(plan.getTerm_period());
		cod_plan.setDetail_plan_memo(plan.getMemo());
		cod_plan.setDetail_unit(plan.getPlan_prepay_months() == null ? 1 : plan.getPlan_prepay_months());
		cod_plan.setDetail_type(plan.getPlan_group());
		
		customer.getCustomerOrder().getCustomerOrderDetails().add(0, cod_plan);
		
		if ("plan-term".equals(plan.getPlan_group())) {
			
			customer.getCustomerOrder().setOrder_total_price(plan.getPlan_price() * plan.getPlan_prepay_months());
			
			if ("transition".equals(customer.getCustomerOrder().getOrder_broadband_type())) {
				
				customer.getCustomerOrder().setOrder_total_price(customer.getCustomerOrder().getOrder_total_price() + plan.getTransition_fee());
				
				CustomerOrderDetail cod_trans = new CustomerOrderDetail();
				cod_trans.setDetail_name("Broadband Transition");
				cod_trans.setDetail_price(plan.getTransition_fee());
				//cod_trans.setDetail_is_next_pay(1);
				cod_trans.setDetail_type("transition");
				cod_trans.setDetail_unit(1);
				
				customer.getCustomerOrder().getCustomerOrderDetails().add(cod_trans);
				
			} else if ("new-connection".equals(customer.getCustomerOrder().getOrder_broadband_type())) {
				
				customer.getCustomerOrder().setOrder_total_price(customer.getCustomerOrder().getOrder_total_price() + plan.getPlan_new_connection_fee());
				
				CustomerOrderDetail cod_conn = new CustomerOrderDetail();
				cod_conn.setDetail_name("Broadband New Connection");
				cod_conn.setDetail_price(plan.getPlan_new_connection_fee());
				//cod_conn.setDetail_is_next_pay(1);
				cod_conn.setDetail_type("new-connection");
				cod_conn.setDetail_unit(1);
				
				customer.getCustomerOrder().getCustomerOrderDetails().add(cod_conn);

			} else if ("jackpot".equals(customer.getCustomerOrder().getOrder_broadband_type())) {
				
				customer.getCustomerOrder().setOrder_total_price(customer.getCustomerOrder().getOrder_total_price() + plan.getJackpot_fee());
				
				CustomerOrderDetail cod_jackpot = new CustomerOrderDetail();
				cod_jackpot.setDetail_name("Broadband New Connection & Jackpot Installation");
				cod_jackpot.setDetail_price(plan.getJackpot_fee());
				//cod_jackpot.setDetail_is_next_pay(1);
				cod_jackpot.setDetail_type("jackpot");
				cod_jackpot.setDetail_unit(1);
				
				customer.getCustomerOrder().getCustomerOrderDetails().add(cod_jackpot);
				
			}
			
			// add plan free pstn
			for (int i = 0; i < plan.getPstn_count(); i++) {
				CustomerOrderDetail cod_pstn = new CustomerOrderDetail();
				if ("personal".equals(plan.getPlan_class())) {
					cod_pstn.setDetail_name("Home Phone Line");
				} else if ("business".equals(plan.getPlan_class())) {
					cod_pstn.setDetail_name("Business Phone Line");
				}
				cod_pstn.setDetail_price(0d);
				//cod_pstn.setDetail_is_next_pay(0);
				//cod_pstn.setDetail_expired(new Date());
				cod_pstn.setDetail_type("pstn");
				cod_pstn.setDetail_unit(1);
				cod_pstn.setPstn_number(customer.getCustomerOrder().getTransition_porting_number());
				customer.getCustomerOrder().getCustomerOrderDetails().add(cod_pstn);
			}
			
			CustomerOrderDetail cod_hd = new CustomerOrderDetail();
			cod_hd.setDetail_name("TP - LINK 150Mbps Wireless N ADSL2+ Modem Router(Free)");
			cod_hd.setDetail_price(0d);
			//cod_hd.setDetail_is_next_pay(0);
			//cod_hd.setDetail_expired(new Date());
			cod_hd.setDetail_unit(1);
			cod_hd.setIs_post(0);
			cod_hd.setDetail_type("hardware-router");
			
			customer.getCustomerOrder().getCustomerOrderDetails().add(cod_hd);
		}
		
		if (cods != null) {
			for (CustomerOrderDetail cod : customer.getCustomerOrder().getCustomerOrderDetails()) {
				if ("hardware-router".equals(cod.getDetail_type())) {
					//cod.setDetail_is_next_pay(0);
					cod.setIs_post(0);
					customer.getCustomerOrder().setHardware_post(customer.getCustomerOrder().getHardware_post() == null ? 1 : customer.getCustomerOrder().getHardware_post() + 1);
					customer.getCustomerOrder().setOrder_total_price(customer.getCustomerOrder().getOrder_total_price() + cod.getDetail_price());
				} else if ("pstn".equals(cod.getDetail_type()) 
						|| "voip".equals(cod.getDetail_type())){
					cod.setDetail_unit(1);
					//cod.setDetail_is_next_pay(1);
					customer.getCustomerOrder().setOrder_total_price(customer.getCustomerOrder().getOrder_total_price() + cod.getDetail_price());
				}
			}
		}
		
		return "broadband-user/sale/online-ordering-confirm";
	}
	
	
	@RequestMapping(value = "/broadband-user/sale/online/ordering/order/confirm/save", method = RequestMethod.POST)
	public String orderSave(Model model, 
			@ModelAttribute("orderPlan") Plan plan, 
			@ModelAttribute("orderCustomer") Customer customer, 
			@RequestParam("optional_request") String optional_request,
			RedirectAttributes attr, SessionStatus status, HttpServletRequest req) {
		
		customer.setPassword(TMUtils.generateRandomString(6));
		customer.setUser_name(customer.getLogin_name());
		customer.setStatus("active");
		customer.getCustomerOrder().setOrder_status("pending");
		customer.getCustomerOrder().setOrder_type(plan.getPlan_group().replace("plan", "order"));
		customer.getCustomerOrder().setSale_id(customer.getId());
		customer.getCustomerOrder().setSignature("unsigned");
		
		User user = (User) req.getSession().getAttribute("userSession");
		customer.getCustomerOrder().setSale_id(user.getId());
		customer.getCustomerOrder().setOptional_request(optional_request);
		
		this.crmService.saveCustomerOrder(customer, customer.getCustomerOrder());
		
		// BEGIN SET NECESSARY AND GENERATE ORDER PDF
		String orderPDFPath = null;
		try {
			orderPDFPath = new OrderPDFCreator(customer, customer.getCustomerOrder(), customer.getOrganization()).create();
		} catch (DocumentException | IOException e) {
			e.printStackTrace();
		}
		CustomerOrder co = new CustomerOrder();
		co.getParams().put("id", customer.getCustomerOrder().getId());
		co.setOrder_pdf_path(orderPDFPath);
		
		this.crmService.editCustomerOrder(co);
		// END SET NECESSARY INFO AND GENERATE ORDER PDF

		CompanyDetail companyDetail = this.crmService.queryCompanyDetail();
		Notification notification = this.systemService.queryNotificationBySort("online-ordering", "email");
		MailRetriever.mailAtValueRetriever(notification, customer, companyDetail); // call mail at value retriever
		ApplicationEmail applicationEmail = new ApplicationEmail();
		applicationEmail.setAddressee(customer.getEmail());
		applicationEmail.setSubject(notification.getTitle());
		applicationEmail.setContent(notification.getContent());
		this.mailerService.sendMailByAsynchronousMode(applicationEmail);
		notification = this.systemService.queryNotificationBySort("online-ordering", "sms"); // get sms register template from db
		MailRetriever.mailAtValueRetriever(notification, customer, companyDetail);
		this.smserService.sendSMSByAsynchronousMode(customer.getCellphone(), notification.getContent()); // send sms to customer's mobile phone
		
		status.setComplete();
		
		return "redirect:/broadband-user/sale/online/ordering/order/credit/" + customer.getId() + "/" + customer.getCustomerOrder().getId();
	}
	
	@RequestMapping(value = "/broadband-user/sales/online-ordering/redirect")
	public String toOnlineOrdering(Model model){
		return "redirect:/broadband-user/sale/online/ordering/plans/business";
	}
	
	@RequestMapping(value = "/broadband-user/sale/online/ordering/order/credit/{customer_id}/{order_id}")
	public String toCredit(Model model, @PathVariable("customer_id") Integer customer_id
			, @PathVariable("order_id") Integer order_id) {
		
		model.addAttribute("customer_id", customer_id);
		model.addAttribute("order_id", order_id);
		model.addAttribute("customerCredit", new CustomerCredit());

		// BEGIN CREDIT CARD YEAR
		List<String> years = new ArrayList<String>();
		for (int i = 14; i <= 99; i++) {
			if(i<10){
				years.add(String.format("%02d",i));
			} else {
				years.add(String.format("%d",i));
			}
		}
		// END CREDIT CARD YEAR

		// BEGIN CREDIT CARD MONTH
		List<String> months = new ArrayList<String>();
		for (int i = 1; i <= 12; i++) {
			if(i<10){
				months.add(String.format("%02d",i));
			} else {
				months.add(String.format("%d",i));
			}
		}
		// END CREDIT CARD MONTH

		model.addAttribute("months", months);
		model.addAttribute("years", years);
		return "broadband-user/sale/online-ordering-credit";
	}
	
	// DOWNLOAD ORDER PDF
	@RequestMapping(value = "/broadband-user/crm/customer/order/pdf/download/{order_id}")
    public ResponseEntity<byte[]> downloadOrderPDF(Model model
    		,@PathVariable(value = "order_id") int order_id) throws IOException {
		String filePath = this.saleService.queryCustomerOrderFilePathById(order_id);
		
		// get file path
        Path path = Paths.get(filePath);
        byte[] contents = null;
        // transfer file contents to bytes
        contents = Files.readAllBytes( path );
        
        HttpHeaders headers = new HttpHeaders();
        // set spring framework media type
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        // get file name with file's suffix
        String filename = URLEncoder.encode(filePath.substring(filePath.lastIndexOf(File.separator)+1, filePath.indexOf("."))+".pdf", "UTF-8");
        headers.setContentDispositionFormData( filename, filename );
        ResponseEntity<byte[]> response = new ResponseEntity<byte[]>( contents, headers, HttpStatus.OK );
        return response;
    }
	
	@RequestMapping(value = "/broadband-user/sale/online/ordering/order/credit/create", method = RequestMethod.POST)
	public String doCredit(Model model,
			@ModelAttribute("customerCredit") @Validated(CustomerCreditValidatedMark.class) CustomerCredit customerCredit
			,@RequestParam("customer_id") Integer customer_id
			,@RequestParam("order_id") Integer order_id
			,BindingResult result
			,RedirectAttributes attr) {
		
		customerCredit.setExpiry_date(
				"20"+customerCredit.getExpiry_year()
				+"-"+customerCredit.getExpiry_month());
		
		model.addAttribute("panelheading", "Customer Credit Card Information");
		model.addAttribute("action", "/broadband-user/sale/online/ordering/order/credit/create");

		if (result.hasErrors()) {
			return "broadband-user/sale/online-ordering-credit";
		}
		
		CustomerOrder co = new CustomerOrder();
		co.setId(order_id);
		this.saleService.createCustomerCredit(customerCredit);

		// BEGIN CREDIT PDF
		CreditPDFCreator cPDFCreator = new CreditPDFCreator();
		customerCredit.setCustomer(this.crmService.queryCustomerById(customer_id));
		cPDFCreator.setCc(customerCredit);
		cPDFCreator.setCo(co);
		cPDFCreator.setOrg(this.saleService.queryOrganizationByCustomerId(customer_id));
		String creditPDFPath = null;
		try {
			creditPDFPath = cPDFCreator.create();
		} catch (DocumentException | IOException e) {
			e.printStackTrace();
		}
		co.getParams().put("id", order_id);
		co.setCredit_pdf_path(creditPDFPath);
		this.crmService.editCustomerOrder(co);
		// END CREDIT PDF
		
		
		return "redirect:/broadband-user/sale/online-ordering-upload-result/"+customer_id+"/"+order_id;
	}

	@RequestMapping(value = "/broadband-user/sale/online-ordering-upload-result/{customer_id}/{order_id}")
	public String toOnlineOrderingResult(Model model,
			@PathVariable("customer_id") Integer customer_id,
			@PathVariable("order_id") Integer order_id){
		model.addAttribute("customer_id",customer_id);
		model.addAttribute("order_id",order_id);
		return "/broadband-user/sale/online-ordering-upload-result";
	}
	
	@RequestMapping(value = "/broadband-user/sale/online-ordering-result/{order_id}/{customer_id}")
	public String toCreditResult(Model model
			, @PathVariable("order_id") Integer order_id
			, @PathVariable("customer_id") Integer customer_id
			,HttpServletRequest req) {
		
		CustomerOrder co = new CustomerOrder();
		co.setId(order_id);
		model.addAttribute("customerOrder", co);

		User user = (User) req.getSession().getAttribute("userSession");
		
		return "redirect:/broadband-user/sale/online/ordering/view/"+1+"/"+user.getId();
	}
	
	@RequestMapping(value = "/broadband-user/sale/online/ordering/order/upload")
	public String uploadSignedPDF(Model model
			, @RequestParam("order_id") Integer order_id
			, @RequestParam("customer_id") Integer customer_id
			, @RequestParam("order_pdf_path") MultipartFile order_pdf_path
			, @RequestParam("credit_pdf_path") MultipartFile credit_pdf_path
			, HttpServletRequest req) {

		User user = (User) req.getSession().getAttribute("userSession");
		if(!order_pdf_path.isEmpty() && !credit_pdf_path.isEmpty()){
			String order_path = TMUtils.createPath("broadband" + File.separator
					+ "customers" + File.separator + customer_id
					+ File.separator + "application_" + order_id
					+ ".pdf");
			String credit_path = TMUtils.createPath("broadband" + File.separator
					+ "customers" + File.separator + customer_id
					+ File.separator + "credit_" + order_id
					+ ".pdf");
			try {
				order_pdf_path.transferTo(new File(order_path));
				credit_pdf_path.transferTo(new File(credit_path));
			} catch (IllegalStateException | IOException e) {
				e.printStackTrace();
			}
			
			CustomerOrder co = new CustomerOrder();
			co.setOrder_pdf_path(order_path);
			co.setCredit_pdf_path(credit_path);
			co.getParams().put("id", order_id);
			co.setSignature("signed");
			this.crmService.editCustomerOrder(co);
		}
		
		return "redirect:/broadband-user/sale/online/ordering/view/1/" + user.getId();
	}
	
	@RequestMapping(value = "/broadband-user/sale/online/ordering/order/upload-single")
	public String uploadSinglePDF(Model model
			, @RequestParam("sale_id") Integer sale_id
			, @RequestParam("order_id") Integer order_id
			, @RequestParam("customer_id") Integer customer_id
			, @RequestParam("order_pdf_path") MultipartFile order_pdf_path
			, @RequestParam("credit_pdf_path") MultipartFile credit_pdf_path
			,HttpServletRequest req) {

		
		if(!order_pdf_path.isEmpty()){
			String order_path = TMUtils.createPath("broadband" + File.separator
					+ "customers" + File.separator + customer_id
					+ File.separator + "application_" + order_id
					+ ".pdf");
			try {
				order_pdf_path.transferTo(new File(order_path));
			} catch (IllegalStateException | IOException e) {
				e.printStackTrace();
			}
			
			CustomerOrder co = new CustomerOrder();
			co.setOrder_pdf_path(order_path);
			co.getParams().put("id", order_id);
			co.setSignature("signed");
			this.crmService.editCustomerOrder(co);
		}
		if(!credit_pdf_path.isEmpty()){
			String credit_path = TMUtils.createPath("broadband" + File.separator
					+ "customers" + File.separator + customer_id
					+ File.separator + "credit_" + order_id
					+ ".pdf");
			try {
				credit_pdf_path.transferTo(new File(credit_path));
			} catch (IllegalStateException | IOException e) {
				e.printStackTrace();
			}
			
			CustomerOrder co = new CustomerOrder();
			co.setCredit_pdf_path(credit_path);
			co.getParams().put("id", order_id);
			co.setSignature("signed");
			this.crmService.editCustomerOrder(co);
		}
		
		return "redirect:/broadband-user/sale/online/ordering/view/1/" + sale_id;
	}
	
	@RequestMapping(value = "/broadband-user/sale/online/ordering/order/upload_previous_provider_invoice_pdf")
	public String uploadPreviousProviderInvoicePDF(Model model
			, @RequestParam("sale_id") Integer sale_id
			, @RequestParam("order_id") Integer order_id
			, @RequestParam("customer_id") Integer customer_id
			, @RequestParam("previous_provider_invoice_path") MultipartFile previous_provider_invoice_path
			,HttpServletRequest req) {

		
		if(!previous_provider_invoice_path.isEmpty()){
			String order_path = TMUtils.createPath("broadband" + File.separator
					+ "customers" + File.separator + customer_id
					+ File.separator + "previous_provider_invoice_" + order_id
					+ ".pdf");
			try {
				previous_provider_invoice_path.transferTo(new File(order_path));
			} catch (IllegalStateException | IOException e) {
				e.printStackTrace();
			}
			
			CustomerOrder co = new CustomerOrder();
			co.setPrevious_provider_invoice(order_path);
			co.getParams().put("id", order_id);
			this.crmService.editCustomerOrder(co);
		}
		
		return "redirect:/broadband-user/sale/online/ordering/view/1/" + sale_id;
	}
	
	@RequestMapping(value = "/broadband-user/sale/online/ordering/view/{pageNo}/{sale_id}")
	public String onlineOrderView(Model model
			, @PathVariable("pageNo") int pageNo
			, @PathVariable("sale_id") int sale_id
			,HttpServletRequest req) {

		model.addAttribute("bothActive", "active");

		Page<CustomerOrder> page = new Page<CustomerOrder>();
		page.setPageNo(pageNo);
		page.getParams().put("orderby", "order by order_create_date desc");
		page.getParams().put("where", "query_sale_id_not_null");

		Page<CustomerOrder> pageSignatureSum = new Page<CustomerOrder>();
		pageSignatureSum.getParams().put("orderby", "order by order_create_date desc");
		pageSignatureSum.getParams().put("where", "query_sale_id_not_null");

		User user = (User) req.getSession().getAttribute("userSession");
		if(user.getUser_role().equals("sales")){
			page.getParams().put("sale_id", user.getId());
			sale_id = user.getId();
			pageSignatureSum.getParams().put("sale_id", user.getId());
		} else if(sale_id!=0){
			page.getParams().put("sale_id", sale_id);
			pageSignatureSum.getParams().put("sale_id", sale_id);
		}
		
		this.saleService.queryOrdersByPage(page);
		List<User> salesUsers = this.saleService.queryUsersWhoseIdExistInOrder();
		
		model.addAttribute("page", page);
		model.addAttribute("salesUsers", salesUsers);
		model.addAttribute("sale_id", sale_id);

		// BEGIN QUERY SUM BY SIGNATURE
		this.saleService.queryOrdersSumByPage(pageSignatureSum);
		model.addAttribute("bothSum", pageSignatureSum.getTotalRecord());
		pageSignatureSum.getParams().put("signature", "signed");
		this.saleService.queryOrdersSumByPage(pageSignatureSum);
		model.addAttribute("signedSum", pageSignatureSum.getTotalRecord());
		pageSignatureSum.getParams().put("signature", "unsigned");
		this.saleService.queryOrdersSumByPage(pageSignatureSum);
		model.addAttribute("unsignedSum", pageSignatureSum.getTotalRecord());
		// END QUERY SUM BY SIGNATURE
		
		return "broadband-user/sale/online-order-view";
	}
	
	@RequestMapping(value = "/broadband-user/sale/online/ordering/view/by_sales_id")
	public String onlineOrderViewBySalesId(Model model
			,HttpServletRequest req
			,@RequestParam("sale_id") Integer sale_id) {

		model.addAttribute("bothActive", "active");

		Page<CustomerOrder> page = new Page<CustomerOrder>();
		page.setPageNo(1);
		page.getParams().put("orderby", "order by order_create_date desc");
		page.getParams().put("where", "query_sale_id_not_null");


		Page<CustomerOrder> pageSignatureSum = new Page<CustomerOrder>();
		pageSignatureSum.getParams().put("where", "query_sale_id_not_null");

		User user = (User) req.getSession().getAttribute("userSession");
		if(user.getUser_role().equals("sales")){
			page.getParams().put("sale_id", user.getId());
			sale_id = user.getId();
			pageSignatureSum.getParams().put("sale_id", user.getId());
		} else if(sale_id!=0){
			page.getParams().put("sale_id", sale_id);
			pageSignatureSum.getParams().put("sale_id", sale_id);
		}
		
		this.saleService.queryOrdersByPage(page);
		model.addAttribute("page", page);
		model.addAttribute("users", this.saleService.queryUsersWhoseIdExistInOrder());
		model.addAttribute("sale_id", sale_id);

		// BEGIN QUERY SUM BY SIGNATURE
		this.saleService.queryOrdersSumByPage(pageSignatureSum);
		model.addAttribute("bothSum", pageSignatureSum.getTotalRecord());
		pageSignatureSum.getParams().put("signature", "signed");
		this.saleService.queryOrdersSumByPage(pageSignatureSum);
		model.addAttribute("signedSum", pageSignatureSum.getTotalRecord());
		pageSignatureSum.getParams().put("signature", "unsigned");
		this.saleService.queryOrdersSumByPage(pageSignatureSum);
		model.addAttribute("unsignedSum", pageSignatureSum.getTotalRecord());
		// END QUERY SUM BY SIGNATURE
		
		return "broadband-user/sale/online-order-view";
	}
	
	@RequestMapping(value = "/broadband-user/sale/online/ordering/view/by/{sale_id}/{signature}")
	public String onlineOrderViewBySignature(Model model
			,HttpServletRequest req
			, @PathVariable("signature") String signature
			, @PathVariable("sale_id") int sale_id) {
		
		if(signature.equals("signed")){
			model.addAttribute("signedActive", "active");
		} else if(signature.equals("unsigned")){
			model.addAttribute("unsignedActive", "active");
		} else if(signature.equals("both")){
			model.addAttribute("bothActive", "active");
		}

		Page<CustomerOrder> page = new Page<CustomerOrder>();
		page.setPageNo(1);
		if(!signature.equals("both")){
			page.getParams().put("signature", signature);
		}
		page.getParams().put("orderby", "order by order_create_date desc");
		page.getParams().put("where", "query_sale_id_not_null");
		User user = (User) req.getSession().getAttribute("userSession");

		Page<CustomerOrder> pageSignatureSum = new Page<CustomerOrder>();
		pageSignatureSum.getParams().put("where", "query_sale_id_not_null");
		
		if(user.getUser_role().equals("sales")){
			page.getParams().put("sale_id", user.getId());
			sale_id = user.getId();
			pageSignatureSum.getParams().put("sale_id", user.getId());
		} else if(sale_id!=0){
			page.getParams().put("sale_id", sale_id);
			pageSignatureSum.getParams().put("sale_id", sale_id);
		}
		
		this.saleService.queryOrdersByPage(page);
		model.addAttribute("page", page);
		model.addAttribute("users", this.saleService.queryUsersWhoseIdExistInOrder());
		model.addAttribute("sale_id", sale_id);

		// BEGIN QUERY SUM BY SIGNATURE
		this.saleService.queryOrdersSumByPage(pageSignatureSum);
		model.addAttribute("bothSum", pageSignatureSum.getTotalRecord());
		pageSignatureSum.getParams().put("signature", "signed");
		this.saleService.queryOrdersSumByPage(pageSignatureSum);
		model.addAttribute("signedSum", pageSignatureSum.getTotalRecord());
		pageSignatureSum.getParams().put("signature", "unsigned");
		this.saleService.queryOrdersSumByPage(pageSignatureSum);
		model.addAttribute("unsignedSum", pageSignatureSum.getTotalRecord());
		// END QUERY SUM BY SIGNATURE
		
		return "broadband-user/sale/online-order-view";
	}
}
