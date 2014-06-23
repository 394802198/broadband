package com.tm.broadband.service;


import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itextpdf.text.DocumentException;
import com.tm.broadband.email.ApplicationEmail;
import com.tm.broadband.mapper.CallInternationalRateMapper;
import com.tm.broadband.mapper.CompanyDetailMapper;
import com.tm.broadband.mapper.ContactUsMapper;
import com.tm.broadband.mapper.CustomerCallRecordMapper;
import com.tm.broadband.mapper.CustomerInvoiceDetailMapper;
import com.tm.broadband.mapper.CustomerInvoiceMapper;
import com.tm.broadband.mapper.CustomerMapper;
import com.tm.broadband.mapper.CustomerOrderDetailMapper;
import com.tm.broadband.mapper.CustomerOrderMapper;
import com.tm.broadband.mapper.CustomerTransactionMapper;
import com.tm.broadband.mapper.EarlyTerminationChargeMapper;
import com.tm.broadband.mapper.EarlyTerminationChargeParameterMapper;
import com.tm.broadband.mapper.ManualDefrayLogMapper;
import com.tm.broadband.mapper.NotificationMapper;
import com.tm.broadband.mapper.OrganizationMapper;
import com.tm.broadband.mapper.ProvisionLogMapper;
import com.tm.broadband.mapper.TerminationRefundMapper;
import com.tm.broadband.model.CompanyDetail;
import com.tm.broadband.model.ContactUs;
import com.tm.broadband.model.Customer;
import com.tm.broadband.model.CustomerInvoice;
import com.tm.broadband.model.CustomerInvoiceDetail;
import com.tm.broadband.model.CustomerOrder;
import com.tm.broadband.model.CustomerOrderDetail;
import com.tm.broadband.model.CustomerTransaction;
import com.tm.broadband.model.EarlyTerminationCharge;
import com.tm.broadband.model.EarlyTerminationChargeParameter;
import com.tm.broadband.model.Hardware;
import com.tm.broadband.model.ManualDefrayLog;
import com.tm.broadband.model.Notification;
import com.tm.broadband.model.Organization;
import com.tm.broadband.model.Page;
import com.tm.broadband.model.Plan;
import com.tm.broadband.model.ProvisionLog;
import com.tm.broadband.model.TerminationRefund;
import com.tm.broadband.model.User;
import com.tm.broadband.pdf.EarlyTerminationChargePDFCreator;
import com.tm.broadband.pdf.InvoicePDFCreator;
import com.tm.broadband.pdf.TerminationRefundPDFCreator;
import com.tm.broadband.util.Calculation4PlanTermInvoice;
import com.tm.broadband.util.MailRetriever;
import com.tm.broadband.util.TMUtils;

/**
 * CRM Module service
 * 
 * @author Cook1fan
 * 
 */
@Service
public class CRMService {
	
	private CustomerMapper customerMapper;
	private CustomerOrderMapper customerOrderMapper;
	private CustomerOrderDetailMapper customerOrderDetailMapper;
	private CustomerInvoiceMapper ciMapper;
	private CustomerInvoiceDetailMapper ciDetailMapper;
	private CustomerTransactionMapper customerTransactionMapper;
	private ProvisionLogMapper provisionLogMapper;
	private CompanyDetailMapper companyDetailMapper;
	private NotificationMapper notificationMapper;
	private OrganizationMapper organizationMapper;
	private ContactUsMapper contactUsMapper;
	private ManualDefrayLogMapper manualDefrayLogMapper;
	private CustomerCallRecordMapper customerCallRecordMapper;
	private CallInternationalRateMapper callInternationalRateMapper;
	private EarlyTerminationChargeMapper earlyTerminationChargeMapper;
	private EarlyTerminationChargeParameterMapper earlyTerminationChargeParameterMapper;
	private TerminationRefundMapper terminationRefundMapper;
	
	// service
	private MailerService mailerService;
	private SmserService smserService;

	public CRMService() { }
	
	@Autowired
	public CRMService(CustomerMapper customerMapper,
			CustomerOrderMapper customerOrderMapper,
			CustomerOrderDetailMapper customerOrderDetailMapper,
			CustomerInvoiceMapper ciMapper,
			CustomerInvoiceDetailMapper ciDetailMapper,
			CustomerTransactionMapper customerTransactionMapper,
			ProvisionLogMapper provisionLogMapper,
			CompanyDetailMapper companyDetailMapper,
			MailerService mailerService,
			NotificationMapper notificationMapper,
			SmserService smserService,
			OrganizationMapper organizationMapper,
			ContactUsMapper contactUsMapper,
			ManualDefrayLogMapper manualDefrayLogMapper,
			CustomerCallRecordMapper customerCallRecordMapper,
			CallInternationalRateMapper callInternationalRateMapper,
			EarlyTerminationChargeMapper earlyTerminationChargeMapper,
			EarlyTerminationChargeParameterMapper earlyTerminationChargeParameterMapper,
			TerminationRefundMapper terminationRefundMapper){
		this.customerMapper = customerMapper;
		this.customerOrderMapper = customerOrderMapper;
		this.customerOrderDetailMapper = customerOrderDetailMapper;
		this.customerTransactionMapper = customerTransactionMapper;
		this.ciMapper = ciMapper;
		this.ciDetailMapper = ciDetailMapper;
		this.provisionLogMapper = provisionLogMapper;
		this.companyDetailMapper = companyDetailMapper;
		this.mailerService = mailerService;
		this.notificationMapper = notificationMapper;
		this.smserService = smserService;
		this.organizationMapper = organizationMapper;
		this.contactUsMapper = contactUsMapper;
		this.manualDefrayLogMapper = manualDefrayLogMapper;
		this.customerCallRecordMapper = customerCallRecordMapper;
		this.callInternationalRateMapper = callInternationalRateMapper;
		this.earlyTerminationChargeMapper = earlyTerminationChargeMapper;
		this.earlyTerminationChargeParameterMapper = earlyTerminationChargeParameterMapper;
		this.terminationRefundMapper = terminationRefundMapper;
	}
	
	
	public void doOrderConfirm(Customer customer, Plan plan) {
		
		customer.getCustomerOrder().setCustomerOrderDetails(new ArrayList<CustomerOrderDetail>());
		customer.getCustomerOrder().setOrder_create_date(new Date());
		customer.getCustomerOrder().setOrder_status("paid");
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
		
		customer.getCustomerOrder().getCustomerOrderDetails().add(cod_plan);
		
		if ("plan-topup".equals(plan.getPlan_group())) {
			
			//cod_plan.setDetail_is_next_pay(0);
			//cod_plan.setDetail_expired(new Date());
			
			customer.getCustomerOrder().setOrder_total_price(plan.getTopup().getTopup_fee());
			
			CustomerOrderDetail cod_topup = new CustomerOrderDetail();
			cod_topup.setDetail_name("Broadband Top-Up");
			cod_topup.setDetail_price(plan.getTopup().getTopup_fee());
			cod_topup.setDetail_type("topup");
			//cod_topup.setDetail_is_next_pay(0);
			//cod_topup.setDetail_expired(new Date());
			cod_topup.setDetail_unit(1);
			
			customer.getCustomerOrder().getCustomerOrderDetails().add(cod_topup);
			
		} else if ("plan-no-term".equals(plan.getPlan_group())) {
			
			customer.getCustomerOrder().setOrder_total_price(plan.getPlan_price() * plan.getPlan_prepay_months());
			System.out.println("Order_total_price: " + customer.getCustomerOrder().getOrder_total_price());
			
			//cod_plan.setDetail_is_next_pay(1);
			
		} else if ("plan-term".equals(plan.getPlan_group())) {
			
			customer.getCustomerOrder().setOrder_status("pending");
			
			customer.getCustomerOrder().setOrder_total_price(plan.getPlan_price() * plan.getPlan_prepay_months());
			
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
			customer.getCustomerOrder().setHardware_post(customer.getCustomerOrder().getHardware_post() == null ? 1 : customer.getCustomerOrder().getHardware_post() + 1);
			cod_hd.setDetail_type("hardware-router");
			
			customer.getCustomerOrder().getCustomerOrderDetails().add(cod_hd);
		}
		
		//**************************************************
		
		if ("transition".equals(customer.getCustomerOrder().getOrder_broadband_type())) {
			
			customer.getCustomerOrder().setOrder_total_price(customer.getCustomerOrder().getOrder_total_price() + plan.getTransition_fee());
			
			CustomerOrderDetail cod_trans = new CustomerOrderDetail();
			cod_trans.setDetail_name("Broadband Transition");
			cod_trans.setDetail_price(plan.getTransition_fee());
			if ("plan-term".equals(plan.getPlan_group())) {
				//cod_trans.setDetail_is_next_pay(1);
			} else {
				//cod_trans.setDetail_is_next_pay(0);
				//cod_trans.setDetail_expired(new Date());
			}
			cod_trans.setDetail_type("transition");
			cod_trans.setDetail_unit(1);
			
			customer.getCustomerOrder().getCustomerOrderDetails().add(cod_trans);
			
		}  else if ("new-connection".equals(customer.getCustomerOrder().getOrder_broadband_type())) {
			
			customer.getCustomerOrder().setOrder_total_price(customer.getCustomerOrder().getOrder_total_price() + plan.getPlan_new_connection_fee());
			
			CustomerOrderDetail cod_conn = new CustomerOrderDetail();
			cod_conn.setDetail_name("Broadband New Connection");
			cod_conn.setDetail_price(plan.getPlan_new_connection_fee());
			if ("plan-term".equals(plan.getPlan_group())) {
				//cod_conn.setDetail_is_next_pay(1);
			} else {
				//cod_conn.setDetail_is_next_pay(0);
				//cod_conn.setDetail_expired(new Date());
			}
			cod_conn.setDetail_type("new-connection");
			cod_conn.setDetail_unit(1);
			
			customer.getCustomerOrder().getCustomerOrderDetails().add(cod_conn);
			
		} else if ("jackpot".equals(customer.getCustomerOrder().getOrder_broadband_type())) {
			
			customer.getCustomerOrder().setOrder_total_price(customer.getCustomerOrder().getOrder_total_price() + plan.getJackpot_fee());
			
			CustomerOrderDetail cod_jackpot = new CustomerOrderDetail();
			cod_jackpot.setDetail_name("Broadband New Connection & Phone Jack Installation");
			cod_jackpot.setDetail_price(plan.getJackpot_fee());
			if ("plan-term".equals(plan.getPlan_group())) {
				//cod_jackpot.setDetail_is_next_pay(1);
			} else {
				//cod_jackpot.setDetail_is_next_pay(0);
				//cod_jackpot.setDetail_expired(new Date());
			}
			cod_jackpot.setDetail_type("jackpot");
			cod_jackpot.setDetail_unit(1);
			
			customer.getCustomerOrder().getCustomerOrderDetails().add(cod_jackpot);
		} 
		
		for (Hardware chd: customer.getCustomerOrder().getHardwares()) {
		
			CustomerOrderDetail cod_hd = new CustomerOrderDetail();
			cod_hd.setDetail_name(chd.getHardware_name());
			cod_hd.setDetail_price(chd.getHardware_price());
			if ("plan-term".equals(plan.getPlan_group())) {
				//cod_hd.setDetail_is_next_pay(1);
			} else {
				//cod_hd.setDetail_is_next_pay(0);
				//cod_hd.setDetail_expired(new Date());
			}
			cod_hd.setDetail_unit(1);
			cod_hd.setIs_post(0);
			cod_hd.setDetail_type("hardware-router");
			customer.getCustomerOrder().setHardware_post(customer.getCustomerOrder().getHardware_post() == null ? 1 : customer.getCustomerOrder().getHardware_post() + 1);
			customer.getCustomerOrder().getCustomerOrderDetails().add(cod_hd);
			customer.getCustomerOrder().setOrder_total_price(customer.getCustomerOrder().getOrder_total_price() + chd.getHardware_price());
		
		}
		
	}
	
	@Transactional
	public void registerCustomer(Customer customer, CustomerTransaction customerTransaction) {
		
		customer.setRegister_date(new Date());
		customer.setActive_date(new Date());
		
		this.customerMapper.insertCustomer(customer);
		//System.out.println("customer id: " + customer.getId());
		
		if ("business".equals(customer.getCustomer_type())) {
			customer.getOrganization().setCustomer_id(customer.getId());
			this.organizationMapper.insertOrganization(customer.getOrganization());
		}
		
		customer.getCustomerOrder().setCustomer_id(customer.getId());
		
		this.customerOrderMapper.insertCustomerOrder(customer.getCustomerOrder());
		//System.out.println("customer order id: " + customer.getCustomerOrder().getId());
		
		CustomerInvoice ci = new CustomerInvoice();
		ci.setCustomer_id(customer.getId());
		ci.setOrder_id(customer.getCustomerOrder().getId());
		ci.setCreate_date(new Date(System.currentTimeMillis()));
		ci.setAmount_payable(customer.getCustomerOrder().getOrder_total_price());
		ci.setAmount_paid(customerTransaction.getAmount());
		ci.setBalance(ci.getAmount_payable() - ci.getAmount_paid());
		ci.setStatus("paid");
		ci.setPaid_date(new Date(System.currentTimeMillis()));
		ci.setPaid_type(customerTransaction.getCard_name());
		
		this.ciMapper.insertCustomerInvoice(ci);
		customer.setCustomerInvoice(ci);
		
		for (CustomerOrderDetail cod : customer.getCustomerOrder().getCustomerOrderDetails()) {
			cod.setOrder_id(customer.getCustomerOrder().getId());
			this.customerOrderDetailMapper.insertCustomerOrderDetail(cod);
			CustomerInvoiceDetail cid = new CustomerInvoiceDetail();
			cid.setInvoice_id(ci.getId());
			cid.setInvoice_detail_name(cod.getDetail_name());
			cid.setInvoice_detail_desc(cod.getDetail_desc());
			cid.setInvoice_detail_price(cod.getDetail_price());
			cid.setInvoice_detail_unit(cod.getDetail_unit());
			this.ciDetailMapper.insertCustomerInvoiceDetail(cid);
		}
		
		customerTransaction.setCustomer_id(customer.getId());
		customerTransaction.setOrder_id(customer.getCustomerOrder().getId());
		customerTransaction.setInvoice_id(ci.getId());
		customerTransaction.setTransaction_date(new Date(System.currentTimeMillis()));
		
		this.customerTransactionMapper.insertCustomerTransaction(customerTransaction);
	}
	
	@Transactional
	public void saveCustomerOrder(Customer customer, CustomerOrder customerOrder) {
		
		customer.setRegister_date(new Date(System.currentTimeMillis()));
		customer.setActive_date(new Date(System.currentTimeMillis()));
		
		this.customerMapper.insertCustomer(customer);
		
		if ("business".equals(customer.getCustomer_type())) {
			customer.getOrganization().setCustomer_id(customer.getId());
			this.organizationMapper.insertOrganization(customer.getOrganization());
			customerOrder.setOrder_total_price(customerOrder.getOrder_total_price() * 1.15);
		}
		customerOrder.setCustomer_id(customer.getId());
		
		this.customerOrderMapper.insertCustomerOrder(customerOrder);
		
		for (CustomerOrderDetail cod : customerOrder.getCustomerOrderDetails()) {
			cod.setOrder_id(customerOrder.getId());
			this.customerOrderDetailMapper.insertCustomerOrderDetail(cod);
		}
	}
	
	@Transactional
	public void customerTopup(Customer customer, CustomerTransaction customerTransaction) {
		this.customerMapper.updateCustomer(customer);
		this.customerTransactionMapper.insertCustomerTransaction(customerTransaction);
	}
	
	@Transactional
	public void customerBalance(CustomerInvoice ci, CustomerTransaction customerTransaction) {
		this.ciMapper.updateCustomerInvoice(ci);
		this.customerTransactionMapper.insertCustomerTransaction(customerTransaction);
	}
	
	@Transactional
	public int queryExistCustomer(Customer customer) {
		return this.customerMapper.selectExistCustomer(customer);
	}

	@Transactional
	public Customer queryCustomer(Customer customer) {
		return this.customerMapper.selectCustomer(customer);
	}

	@Transactional
	public Customer queryCustomerById(int id) {
		return this.customerMapper.selectCustomerById(id);
	}
	
	@Transactional
	public Customer queryCustomerByIdWithCustomerOrder(int id) {
		return this.customerMapper.selectCustomerByIdWithCustomerOrder(id);
	}
	
	@Transactional
	public Page<Customer> queryCustomersByPage(Page<Customer> page) {
		page.setTotalRecord(this.customerMapper.selectCustomersSum(page));
		page.setResults(this.customerMapper.selectCustomersByPage(page));
		return page;
	}
	
	@Transactional
	public int queryCustomersSumByPage(Page<Customer> page) {
		return this.customerMapper.selectCustomersSum(page);
	}
	
	@Transactional
	public Customer queryCustomerWhenLogin(Customer customer) {
		return this.customerMapper.selectCustomer(customer);
	}
	
	@Transactional
	public List<CustomerOrder> queryCustomerOrdersByCustomerId(int customer_id) {
		return this.customerOrderMapper.selectCustomerOrdersByCustomerId(customer_id);
	}
	
	@Transactional
	public CustomerOrder queryCustomerOrder(CustomerOrder customerOrder) {
		return this.customerOrderMapper.selectCustomerOrder(customerOrder);
	}
	
	@Transactional
	public List<CustomerOrder> queryCustomerOrders(CustomerOrder customerOrder) {
		return this.customerOrderMapper.selectCustomerOrders(customerOrder);
	}

	@Transactional
	public Page<CustomerInvoice> queryCustomerInvoicesByPage(Page<CustomerInvoice> page) {
		page.setTotalRecord(this.ciMapper.selectCustomerInvoicesSum(page));
		page.setResults(this.ciMapper.selectCustomerInvoicesByPage(page));
		return page;
	}
	
	@Transactional
	public Page<CustomerTransaction> queryCustomerTransactionsByPage(Page<CustomerTransaction> page) {
		page.setTotalRecord(this.customerTransactionMapper.selectCustomerTransactionsSum(page));
		page.setResults(this.customerTransactionMapper.selectCustomerTransactionsByPage(page));
		return page;
	}
	
	@Transactional
	public List<CustomerTransaction> queryCustomerTransactionsByCustomerId(int id) {
		return this.customerTransactionMapper.selectCustomerTransactionsByCustomerId(id);
	}
	
	@Transactional
	public String queryCustomerOrderTypeById(int id) {
		return this.customerOrderMapper.selectCustomerOrderTypeById(id);
	}
	
	@Transactional
	public CustomerOrder queryCustomerOrderById(int id) {
		return this.customerOrderMapper.selectCustomerOrderById(id);
	}

	@Transactional
	public void editCustomer(Customer customer) {
		this.customerMapper.updateCustomer(customer);
		if ("business".equals(customer.getCustomer_type())) {
			customer.getOrganization().getParams().put("customer_id", customer.getId());
			this.organizationMapper.updateOrganization(customer.getOrganization());
		}
	}

	@Transactional
	public void removeCustomer(int id) {
		this.customerMapper.deleteCustomerById(id);
		
		// BEGIN delete order area
		CustomerOrder co = new CustomerOrder();
		co.getParams().put("customer_id", id);
		// get order id
		List<CustomerOrder> cos = this.customerOrderMapper.selectCustomerOrders(co);
		for (CustomerOrder customerOrder : cos) {
			// delete order related details
			this.customerOrderDetailMapper.deleteCustomerOrderDetailByOrderId(customerOrder.getId());
		}
		// delete order
		this.customerOrderMapper.deleteCustomerOrderByCustomerId(id);
		// END delete order area
		
		// BEGIN delete invoice area
		CustomerInvoice ci = new CustomerInvoice();
		ci.getParams().put("customer_id", id);
		// get invoice id
		List<CustomerInvoice> cis = this.ciMapper.selectCustomerInvoices(ci);
		for (CustomerInvoice ci2 : cis) {
			// delete invoice related details
			this.ciDetailMapper.deleteCustomerInvoiceDetailByInvoiceId(ci2.getId());
		}
		// delete invoice
		this.ciMapper.deleteCustomerInvoiceByCustomerId(id);
		// END delete invoice area
		
		// delete transaction
		this.customerTransactionMapper.deleteCustomerTransactionByCustomerId(id);
		
		// delete related organization
		this.organizationMapper.deleteOrganizationByCustomerId(id);
		
	}

	@Transactional
	public void createCustomer(Customer customer) {
		this.customerMapper.insertCustomer(customer);
		if ("business".equals(customer.getCustomer_type())) {
			customer.getOrganization().setCustomer_id(customer.getId());
			this.organizationMapper.insertOrganization(customer.getOrganization());
		}
	}

	@Transactional
	public void createContactUs(ContactUs contactUs) {
		this.contactUsMapper.insertContactUs(contactUs);
	}

	@Transactional
	public void createManualDefrayLog(ManualDefrayLog manualDefrayLog) {
		this.manualDefrayLogMapper.insertManualDefrayLog(manualDefrayLog);
	}

	@Transactional
	public void createCustomerOrderDetail(CustomerOrderDetail customerOrderDetail) {
		this.customerOrderDetailMapper.insertCustomerOrderDetail(customerOrderDetail);
	}

	@Transactional
	public void removeCustomerOrderDetailById(int id) {
		this.customerOrderDetailMapper.deleteCustomerOrderDetailById(id);
	}

	@Transactional
	public void editCustomerOrder(CustomerOrder customerOrder, ProvisionLog proLog) {
		// edit order
		this.customerOrderMapper.updateCustomerOrder(customerOrder);
		// insert provision
		this.provisionLogMapper.insertProvisionLog(proLog);
	}
	
	@Transactional
	public void editCustomerOrder(CustomerOrder customerOrder) {
		// edit order
		this.customerOrderMapper.updateCustomerOrder(customerOrder);
	}
	
	@Transactional
	public void editCustomerOrderDetail(
			CustomerOrderDetail cod) {
		// edit order detail
		this.customerOrderDetailMapper.updateCustomerOrderDetail(cod);
		
	}
	
	@Transactional
	public void editOrganization(Organization org){
		this.organizationMapper.updateOrganization(org);
	}

	@Transactional 
	public String queryCustomerInvoiceFilePathById(int id){
		return this.ciMapper.selectCustomerInvoiceFilePathById(id);
	}
	
	/*
	 * Notification BEGIN
	 */
	
	@Transactional
	public Notification queryNotificationBySort(String sort, String type){
		return this.notificationMapper.selectNotificationBySort(sort, type);
	}
	
	/*
	 * Notification END
	 */

	/*
	 * CompanyDetail BEGIN
	 */
	
	@Transactional
	public CompanyDetail queryCompanyDetail(){
		return this.companyDetailMapper.selectCompanyDetail();
	}
	
	/*
	 * CompanyDetail END
	 */
	
	
	/*
	 * customer invoice
	 * */
	public Double queryCustomerInvoicesBalanceByCid(int cid, String status) {
		return this.ciMapper.selectCustomerInvoicesBalanceByCidAndStatus(cid, status);
	}
	
	public CustomerInvoice queryCustomerInvoiceById(int id){
		return this.ciMapper.selectCustomerInvoiceById(id);
	}
	
	@Transactional
	public void editCustomerInvoice(CustomerInvoice ci){
		this.ciMapper.updateCustomerInvoice(ci);
	}
	/*
	 * end customer invoice
	 * */
	
	// manually generating invoice PDF by id
	@Transactional
	public void createInvoicePDFByInvoiceID(int invoiceId){
		// store company details begin
		CompanyDetail companyDetail = this.companyDetailMapper.selectCompanyDetail();
		// store company details end
		
		// invoice PDF generator manipulation begin
		// get necessary models begin
		// get last invoice, customer and current invoice it self at the same time by using left join
		CustomerInvoice ci = this.ciMapper.selectInvoiceWithLastInvoiceIdById(invoiceId);
		// get invoice details
		ci.setCustomerInvoiceDetails(this.ciDetailMapper.selectCustomerInvoiceDetailsByCustomerInvoiceId(invoiceId));
		// get customer details from invoice
		Customer customer = ci.getCustomer();
		// get necessary models end
		
		// initialize invoice's important informations
		InvoicePDFCreator invoicePDF = new InvoicePDFCreator();
		invoicePDF.setCompanyDetail(companyDetail);
		invoicePDF.setCustomer(customer);
		invoicePDF.setCurrentCustomerInvoice(ci);
		invoicePDF.setOrg(this.organizationMapper.selectOrganizationByCustomerId(customer.getId()));

		// set file path
		Map<String, Object> map = null;
		try {
			map = invoicePDF.create();
			// generate invoice PDF
			ci.setInvoice_pdf_path((String) map.get("filePath"));
		} catch (DocumentException | IOException e) {
			e.printStackTrace();
		}
		// add sql condition: id
//		ci.setAmount_paid((Double) map.get("amount_paid"));
//		ci.setAmount_payable((Double) map.get("amount_payable"));
//		ci.setBalance((Double) map.get("balance")); 
		ci.getParams().put("id", ci.getId());
		
		this.ciMapper.updateCustomerInvoice(ci);
		// invoice PDF generator manipulation end
	}
	
	@Transactional
	public void createInvoicePDF(CustomerOrder customerOrder
			,Notification notificationEmail
			,Notification notificationSMS) {

		customerOrder = this.customerOrderMapper.selectCustomerOrderById(customerOrder.getId());
		createInvoicePDFBoth(customerOrder
				,new Notification(notificationEmail.getTitle(), notificationEmail.getContent())
				,new Notification(notificationEmail.getTitle(), notificationEmail.getContent())
				, false, false);	// false : first invoice
	}

	@Transactional 
	public void createNextInvoice(CustomerOrder customerOrderParam) throws ParseException{
		Notification notificationEmail = this.notificationMapper.selectNotificationBySort("invoice", "email");
		Notification notificationSMS = this.notificationMapper.selectNotificationBySort("invoice", "sms");
		List<CustomerOrder> customerOrders = this.customerOrderMapper.selectCustomerOrdersBySome(customerOrderParam);

		for (CustomerOrder customerOrder : customerOrders) {
			//System.out.println(customerOrder.getId() + ":" + customerOrder.getNext_invoice_create_date());
			createInvoicePDFBoth(customerOrder
					,new Notification(notificationEmail.getTitle(), notificationEmail.getContent())
					,new Notification(notificationSMS.getTitle(), notificationSMS.getContent())
					, true, false);	// true : next invoice
		}
		
		// BEGIN TOPUP NOTIFICATION
		CustomerOrder topupCustomerOrder = new CustomerOrder();
		topupCustomerOrder.getParams().put("where", "query_topup");
		topupCustomerOrder.getParams().put("order_status", "using");
		topupCustomerOrder.getParams().put("order_type_topup", "order-topup");
		Calendar cal = Calendar.getInstance();

        // using new SimpleDateFormat("yyyy-MM-dd").parse("2014-06-13") under testing environment
		// using new Date() under production environment
		cal.setTime(new Date());
		cal.add(Calendar.DATE, 1);
		topupCustomerOrder.getParams().put("order_due_backward_one", cal.getTime());
		cal.add(Calendar.DATE, 1);
		topupCustomerOrder.getParams().put("order_due_backward_two", cal.getTime());
		List<CustomerOrder> topupCustomerOrders = this.customerOrderMapper.selectCustomerOrdersBySome(topupCustomerOrder);
		
		
		Notification topupNotificationEmailTemp = this.notificationMapper.selectNotificationBySort("topup-notification", "email");
		Notification topupNotificationSMSTemp = this.notificationMapper.selectNotificationBySort("topup-notification", "sms");
		
		for (CustomerOrder customerOrder : topupCustomerOrders) {
			CompanyDetail companyDetail = this.companyDetailMapper.selectCompanyDetail();
			Customer c = customerOrder.getCustomer();
			Notification topupNotificationEmail = new Notification(topupNotificationEmailTemp.getTitle(), topupNotificationEmailTemp.getContent());
			Notification topupNotificationSMS = new Notification(topupNotificationSMSTemp.getTitle(), topupNotificationSMSTemp.getContent());

			// call mail at value retriever
			MailRetriever.mailAtValueRetriever(topupNotificationEmail, c, customerOrder, companyDetail);
			ApplicationEmail applicationEmail = new ApplicationEmail();
			applicationEmail.setAddressee(c.getEmail());
			applicationEmail.setSubject(topupNotificationEmail.getTitle());
			applicationEmail.setContent(topupNotificationEmail.getContent());
			// binding attachment name & path to email
			this.mailerService.sendMailByAsynchronousMode(applicationEmail);

			// get sms register template from db
			MailRetriever.mailAtValueRetriever(topupNotificationSMS, c, customerOrder, companyDetail);
			// send sms to customer's mobile phone
			this.smserService.sendSMSByAsynchronousMode(c.getCellphone(), topupNotificationSMS.getContent());
		}
		// END TOPUP NOTIFICATION
		
	}
	
	@Transactional 
	public void sendTermPlanInvoicePDF() {
		
		Notification notificationEmailTemp = this.notificationMapper.selectNotificationBySort("invoice", "email");
		Notification notificationSMSTemp = this.notificationMapper.selectNotificationBySort("invoice", "sms");
		
		CompanyDetail companyDetail = this.companyDetailMapper.selectCompanyDetail();
		
		CustomerOrder coTemp = new CustomerOrder();
		coTemp.getParams().put("where", "query_term");
		coTemp.getParams().put("status", "using");
		coTemp.getParams().put("order_type", "order-term"); 
		List<CustomerOrder> cos = this.customerOrderMapper.selectCustomerOrders(coTemp);
		
		for (CustomerOrder co : cos) {
			
			Notification notificationEmail = new Notification(notificationEmailTemp.getTitle(), notificationEmailTemp.getContent());
			Notification notificationSMS = new Notification(notificationSMSTemp.getTitle(), notificationSMSTemp.getContent());
			
			CustomerInvoice ci = new CustomerInvoice();
			ci.getParams().put("where", "by_max_id");
			ci.getParams().put("customer_id", co.getCustomer().getId());
			ci.getParams().put("order_id", co.getId());
			ci = this.ciMapper.selectCustomerInvoice(ci);
			
			// call mail at value retriever
			MailRetriever.mailAtValueRetriever(notificationEmail, co.getCustomer(), co, ci, companyDetail);
			ApplicationEmail applicationEmail = new ApplicationEmail();
			applicationEmail.setAddressee(co.getCustomer().getEmail());
			applicationEmail.setSubject(notificationEmail.getTitle());
			applicationEmail.setContent(notificationEmail.getContent());
			// binding attachment name & path to email
			applicationEmail.setAttachName("invoice_" + ci.getId() + ".pdf");
			applicationEmail.setAttachPath(ci.getInvoice_pdf_path());
			this.mailerService.sendMailByAsynchronousMode(applicationEmail);

			// get sms register template from db
			MailRetriever.mailAtValueRetriever(notificationSMS, co.getCustomer(), co, ci, companyDetail);
			// send sms to customer's mobile phone
			this.smserService.sendSMSByAsynchronousMode(co.getCustomer().getCellphone(), notificationSMS.getContent());
		}
	}

	@Transactional
	public void createEarlyTerminationInvoice(Integer order_id
			, Date terminatedDate
			, Integer executor_id) throws ParseException{
		CustomerOrder co = this.customerOrderMapper.selectCustomerOrderById(order_id);
		Customer c = co.getCustomer();
		Map<String, Object> map = TMUtils.earlyTerminationDatesCalculation(co.getOrder_using_start(), terminatedDate);
		
		EarlyTerminationCharge etc = new EarlyTerminationCharge();
		etc.setCustomer_id(c.getId());
		etc.setOrder_id(co.getId());
		etc.setCreate_date(new Date());
		etc.setService_given_date(co.getOrder_using_start());
		etc.setTermination_date(terminatedDate);
		etc.setLegal_termination_date((Date) map.get("legal_termination_date"));
		etc.setDue_date(TMUtils.getInvoiceDueDate(new Date(), 10));
		EarlyTerminationChargeParameter earlyTerminationChargeParam = this.earlyTerminationChargeParameterMapper.selectEarlyTerminationChargeParameter();
		etc.setOverdue_extra_charge(earlyTerminationChargeParam != null ? earlyTerminationChargeParam.getOverdue_extra_charge() : 0d);
		etc.setCharge_amount((Double) map.get("charge_amount"));
		etc.setTotal_payable_amount((Double) map.get("charge_amount") + etc.getOverdue_extra_charge());
		etc.setMonths_between_begin_end((Integer) map.get("months_between_begin_end"));
		etc.setExecute_by(executor_id);
		etc.setStatus("unpaid");
		
		this.earlyTerminationChargeMapper.insertEarlyTerminationCharge(etc);
		
		String invoicePDFPath = "";
		try {
			CompanyDetail cd = this.companyDetailMapper.selectCompanyDetail();
			Organization org = c.getOrganization();
			invoicePDFPath = new EarlyTerminationChargePDFCreator(cd, c, org, etc).create();
		} catch (DocumentException | IOException e) {
			e.printStackTrace();
		}
		etc.getParams().put("id", etc.getId());
		etc.setInvoice_pdf_path(invoicePDFPath);
		this.earlyTerminationChargeMapper.updateEarlyTerminationCharge(etc);
	}

	@Transactional
	public void createTerminationRefundInvoice(Integer order_id
			, Date terminatedDate, User u, String accountNo, String accountName, Double monthlyCharge, String productName) throws ParseException{
		CustomerOrder co = this.customerOrderMapper.selectCustomerOrderById(order_id);
		Customer c = co.getCustomer();
		Map<String, Object> map = TMUtils.terminationRefundCalculations(terminatedDate, monthlyCharge);
		
		TerminationRefund tr = new TerminationRefund();
		tr.setCustomer_id(c.getId());
		tr.setOrder_id(co.getId());
		tr.setCreate_date(new Date());
		tr.setTermination_date(terminatedDate);
		tr.setProduct_name(productName);
		tr.setProduct_monthly_price(monthlyCharge);
		tr.setRefund_bank_account_number(accountNo);
		tr.setRefund_bank_account_name(accountName);
		tr.setRefund_amount((Double) map.get("refund_amount"));
		tr.setDays_between_end_last((Integer) map.get("remaining_days"));
		tr.setStatus("unpaid");
		tr.setExecute_by(u.getId());
		tr.setLast_date_of_month((Date) map.get("last_date_of_month"));
		
		
		this.terminationRefundMapper.insertTerminationRefund(tr);
		
		String refundPDFPath = "";
		try {
			CompanyDetail cd = this.companyDetailMapper.selectCompanyDetail();
			Organization org = c.getOrganization();
			refundPDFPath = new TerminationRefundPDFCreator(cd, c, org, tr, u).create();
		} catch (DocumentException | IOException e) {
			e.printStackTrace();
		}
		tr.getParams().put("id", tr.getId());
		tr.setRefund_pdf_path(refundPDFPath);
		this.terminationRefundMapper.updateTerminationRefund(tr);
	}

	@Transactional
	public void createTermPlanInvoice() throws ParseException{
		
		// Production environment should edit as shown below
		// where = query_term
		// status = using
		// order_type_term = order-term
		CustomerOrder coTemp = new CustomerOrder();
		coTemp.getParams().put("where", "query_term");
		coTemp.getParams().put("status", "using");
		coTemp.getParams().put("order_type", "order-term"); 
		List<CustomerOrder> cos = this.customerOrderMapper.selectCustomerOrders(coTemp);
		
		// If found any order(s)
		if(cos.size() > 0){
			
			boolean isRegenerateInvoice = false;
			
			for (CustomerOrder co : cos) {
				createTermPlanInvoiceByOrder(co
						, isRegenerateInvoice);
			}
		}
	}

	@Transactional
	public void createTermPlanInvoiceByOrder(CustomerOrder co
			,boolean isRegenerateInvoice) throws ParseException{
		
		// BEGIN get usable beans
		// Customer
		Customer c = co.getCustomer();
		// Current invoice
		CustomerInvoice ci = new CustomerInvoice();
		System.out.println("isRegenerateInvoice: " + isRegenerateInvoice);
		
		// Previous invoice's preparation
		CustomerInvoice ciPre = new CustomerInvoice();
		ciPre.getParams().put("customer_id", c.getId());
		ciPre.getParams().put("order_id", co.getId());
		// Previous invoice
		CustomerInvoice cpi = null;
		
		if(isRegenerateInvoice){
			CustomerInvoice ciCur = new CustomerInvoice();
			ciCur.getParams().put("where", "by_max_id");
			ciCur.getParams().put("customer_id", c.getId());
			ciCur.getParams().put("order_id", co.getId());
			ci = ciMapper.selectCustomerInvoice(ciCur);
			if(ci != null && !"paid".equals(ci.getStatus())){
				
//				totalPayableAmouont = ci.getAmount_payable();
				
//				// Delete most recent invoice's PDF
//				File file = new File(ci.getInvoice_pdf_path());
//				if(file.exists()){
//					file.delete();
//				}
				
				// Delete most recent invoice's details
//				ciDetailMapper.deleteCustomerInvoiceDetailByInvoiceId(ci.getId());
				ciPre.getParams().put("where", "by_second_id");
				cpi = ciMapper.selectCustomerInvoice(ciPre);
			} else if(ci != null) {
				ciPre.getParams().put("where", "by_max_id");
				cpi = ciMapper.selectCustomerInvoice(ciPre);
				ci = new CustomerInvoice();
				ci.setCustomer_id(c.getId());
				ci.setOrder_id(co.getId());
				ci.setCreate_date(new Date());
				ci.setDue_date(TMUtils.getInvoiceDueDate(ci.getCreate_date(), 10));
				ciMapper.insertCustomerInvoice(ci);
			} else if(ci == null){
				ci = new CustomerInvoice();
				ci.setCustomer_id(c.getId());
				ci.setOrder_id(co.getId());
				ci.setCreate_date(new Date());
				ci.setDue_date(TMUtils.getInvoiceDueDate(ci.getCreate_date(), 10));
				ciMapper.insertCustomerInvoice(ci);
			}
		} else {
			ciPre.getParams().put("where", "by_max_id");
			cpi = ciMapper.selectCustomerInvoice(ciPre);
			ci = new CustomerInvoice();
			ci.setCustomer_id(c.getId());
			ci.setOrder_id(co.getId());
			ci.setCreate_date(new Date());
			ci.setDue_date(TMUtils.getInvoiceDueDate(ci.getCreate_date(), 10));
			ciMapper.insertCustomerInvoice(ci);
			// Set id for invoice's update
		}
		
		ci.setAmount_paid(ci.getAmount_paid()!=null ? ci.getAmount_paid() : 0d);
		ci.setAmount_payable(ci.getAmount_payable()!=null ? ci.getAmount_payable() : 0d);
		ci.setBalance(ci.getBalance()!=null ? ci.getBalance() : 0d);
		ci.getParams().put("id", ci.getId());	// For update invoice use
		
//		// If is regenerate current invoice, then execute some cleaning jobs
//		if(isRegenerateInvoice && cpi != null){
//			
//			// Delete most recent invoice
////			ciMapper.deleteCustomerInvoiceById(cpi.getId());
//			
//		}
		
		// Current invoice detail
		List<CustomerInvoiceDetail> cids = new ArrayList<CustomerInvoiceDetail>();
		// END get usable beans

		// If current order don't have any invoice then true, else false
		// Production environment should edit as shown below
		// {true} change to {cpi == null ? true : false;}
		boolean isFirst = //true;
				cpi == null ? true : false;
		// If service giving is gave by this month then true, else false
		// Production environment should edit as shown below
		// {true} change to {below long statement}
		boolean isServedCurrentMonth = //false; 
				co.getOrder_using_start() != null ? TMUtils.isSameMonth(co.getOrder_using_start(), new Date()) : false;

		// Calculate invoice's payable amount
		Double totalPayableAmouont = 0d;
		Double totalCreditBack = 0d;
		
		String pstn_number = null;
		
		for (CustomerOrderDetail cod : co.getCustomerOrderDetails()) {
			CustomerInvoiceDetail cid = new CustomerInvoiceDetail();
			
			if("termination-credit".equals(cod.getDetail_type())){
				cid.setInvoice_detail_name(cod.getDetail_name());
				cid.setInvoice_detail_unit(cod.getDetail_unit());
				cid.setInvoice_detail_discount(cod.getDetail_price());
				cid.setInvoice_detail_desc(cod.getDetail_desc());
				cids.add(cid);
				
				totalCreditBack = TMUtils.bigAdd(totalCreditBack, TMUtils.bigMultiply(cod.getDetail_price(), cod.getDetail_unit()));
			}
			if("early-termination-debit".equals(cod.getDetail_type())){
				cid.setInvoice_detail_name(cod.getDetail_name());
				cid.setInvoice_detail_unit(cod.getDetail_unit());
				cid.setInvoice_detail_price(cod.getDetail_price());
				cid.setInvoice_detail_desc(cod.getDetail_desc());
				cids.add(cid);
				
				totalPayableAmouont = TMUtils.bigAdd(totalPayableAmouont, cod.getDetail_price());
			}
			
			if("pstn".equals(cod.getDetail_type())){
				
				if((cod.getPstn_number()!=null && !"".equals(cod.getPstn_number().trim()))
					&& ("".equals(pstn_number) || pstn_number==null)){
					pstn_number = cod.getPstn_number();
				}
				
			}
			System.out.println("isFirst: " + isFirst);
			// If first invoice then add all order details into invoice details
			if(isFirst){
				
				if("plan-term".equals(cod.getDetail_type())){
					
					if(!isRegenerateInvoice || (isRegenerateInvoice && "unpaid".equals(ci.getStatus())) || (isRegenerateInvoice && "paid".equals(cpi != null ? cpi.getStatus() : "paid") && ! (cpi != null ? TMUtils.isSameMonth(cpi.getCreate_date(), new Date()) : false))){
						
						// Get served month's term plan's remaining charges 
						// Production environment should edit as shown below
						// {new Date()} change to {co.getOrder_using_start()}
						Map<String, Object> resultMap = Calculation4PlanTermInvoice.servedMonthDetails(//new Date()
								co.getOrder_using_start()
								, cod.getDetail_price());
						cid.setInvoice_detail_name(cod.getDetail_name()+" ("+resultMap.get("remainingDays")+" day(s) counting from service start date to end of month)");
						cid.setInvoice_detail_unit(cod.getDetail_unit());
						cid.setInvoice_detail_price((Double)resultMap.get("totalPrice"));
						
						// Add remaining price into payable amount
						totalPayableAmouont = TMUtils.bigAdd(totalPayableAmouont, (Double)resultMap.get("totalPrice"));
						cids.add(cid);
						
						// If is not served in current month, then add plan-term type detail into invoice detail
						if (!isServedCurrentMonth) {
							
							// Reconstruct invoice's detail
							cid = new CustomerInvoiceDetail();
							cid.setInvoice_detail_name(cod.getDetail_name());
							cid.setInvoice_detail_unit(cod.getDetail_unit());
							cid.setInvoice_detail_price(cod.getDetail_price());
							
							// Add monthly price into payable amount
							totalPayableAmouont = TMUtils.bigAdd(totalPayableAmouont, cod.getDetail_price());
							cids.add(cid);
						}
					}

				// Else if discount and unexpired then do add discount
				} else if("discount".equals(cod.getDetail_type()) && cod.getDetail_expired() != null && cod.getDetail_expired().getTime() >= System.currentTimeMillis()){
					
					cid.setInvoice_detail_name(cod.getDetail_name());
					cid.setInvoice_detail_discount(cod.getDetail_price());
					cid.setInvoice_detail_unit(cod.getDetail_unit());
					// totalCreditBack add ( discount price times discount unit )
					totalCreditBack = TMUtils.bigAdd(totalCreditBack, TMUtils.bigMultiply(cod.getDetail_price(), cod.getDetail_unit()));
					cids.add(cid);
					
				// Else add all non plan-term, discount, termination-credit, early-termination-debit type details into invoice details
				} else if(!"discount".equals(cod.getDetail_type())
						  && !"termination-credit".equals(cod.getDetail_type())
						  && !"early-termination-debit".equals(cod.getDetail_type())
						  && !"present-calling-minutes".equals(cod.getDetail_type())) {

					cid.setInvoice_detail_name(cod.getDetail_name());
					cid.setInvoice_detail_price(cod.getDetail_price());
					cid.setInvoice_detail_unit(cod.getDetail_unit());
					
					// Payable amount plus ( detail price times detail unit )
					totalPayableAmouont = TMUtils.bigAdd(totalPayableAmouont, TMUtils.bigMultiply(cod.getDetail_price(), cod.getDetail_unit()));
					cids.add(cid);
				}
				
			// Else not first invoice, add unexpired order detail(s) into invoice detail(s)
			} else {
				
				// Add previous invoice's details into
				ci.setLast_invoice_id(cpi.getId());
				ci.setLastCustomerInvoice(cpi);

				// If plan-term type, then add detail into invoice detail
				if("plan-term".equals(cod.getDetail_type())){
					System.out.println("!isRegenerateInvoice: "+!isRegenerateInvoice);
					System.out.println("isRegenerateInvoice && \"paid\".equals(cpi.getStatus()) && TMUtils.isSameMonth(cpi.getCreate_date(), new Date()): "+(isRegenerateInvoice && "paid".equals(cpi.getStatus()) && TMUtils.isSameMonth(cpi.getCreate_date(), new Date())));
					if(!isRegenerateInvoice || (isRegenerateInvoice && "unpaid".equals(ci.getStatus())) || (isRegenerateInvoice && "paid".equals(cpi != null ? cpi.getStatus() : "paid") && ! (cpi != null ? TMUtils.isSameMonth(cpi.getCreate_date(), new Date()) : false))){
						
						cid.setInvoice_detail_name(cod.getDetail_name());
						cid.setInvoice_detail_unit(cod.getDetail_unit());
						cid.setInvoice_detail_price(cod.getDetail_price());
						
						// Add monthly price into payable amount
						totalPayableAmouont = TMUtils.bigAdd(totalPayableAmouont, cod.getDetail_price());
						cids.add(cid);
					}

				// Else if discount and unexpired then do add discount
				} else if("discount".equals(cod.getDetail_type()) && cod.getDetail_expired() != null && cod.getDetail_expired().getTime() >= System.currentTimeMillis()){
					
					cid.setInvoice_detail_name(cod.getDetail_name());
					cid.setInvoice_detail_discount(cod.getDetail_price());
					cid.setInvoice_detail_unit(cod.getDetail_unit());

					// totalCreditBack add ( discount price times discount unit )
					totalCreditBack = TMUtils.bigAdd(totalCreditBack, TMUtils.bigMultiply(cod.getDetail_price(), cod.getDetail_unit()));
					cids.add(cid);

				// Else if unexpired then add order detail(s) into invoice detail(s)
				} else if(cod.getDetail_expired() != null && cod.getDetail_expired().getTime() >= System.currentTimeMillis()) {

					cid.setInvoice_detail_name(cod.getDetail_name());
					cid.setInvoice_detail_price(cod.getDetail_price());
					cid.setInvoice_detail_unit(cod.getDetail_unit());

					// Payable amount plus ( detail price times detail unit )
					totalPayableAmouont = TMUtils.bigAdd(totalPayableAmouont, TMUtils.bigMultiply(cod.getDetail_price(), cod.getDetail_unit()));
					cids.add(cid);
				}
				
			}
		}
//		if(!isFirst){
//			// Add previous invoice's balance
//			totalPayableAmouont = TMUtils.bigAdd(totalPayableAmouont, cpi.getBalance());
//			if(!"paid".equals(cpi.getStatus())){
//				cpi.getParams().put("id", cpi.getId());
//				ciMapper.updateCustomerInvoice(cpi);
//			}
//		}
		

		// store company detail begin
		CompanyDetail companyDetail = companyDetailMapper.selectCompanyDetail();
		// store company detail end

		InvoicePDFCreator invoicePDF = new InvoicePDFCreator();
		invoicePDF.setCompanyDetail(companyDetail);
		invoicePDF.setCustomer(c);
		invoicePDF.setOrg(organizationMapper.selectOrganizationByCustomerId(c.getId()));
		
		
		// BEGIN IF HAVE PSTN_NUMBER
		if(!"".equals(pstn_number) && pstn_number!=null){
			
			if(!isRegenerateInvoice || (isRegenerateInvoice && "unpaid".equals(ci.getStatus())) || (isRegenerateInvoice && "paid".equals(cpi != null ? cpi.getStatus() : "paid") && ! (cpi != null ? TMUtils.isSameMonth(cpi.getCreate_date(), new Date()) : false))){
				
				totalPayableAmouont = TMUtils.ccrOperation(pstn_number, cids, invoicePDF, totalPayableAmouont, customerCallRecordMapper, callInternationalRateMapper);
			}
		}
		// END IF HAVE PSTN_NUMBER
		
		// truncate unnecessary reminders, left only two reminders, e.g. 1.0001 change to 1.00
		totalCreditBack = Double.parseDouble(TMUtils.fillDecimalPeriod(totalCreditBack));
		totalPayableAmouont = Double.parseDouble(TMUtils.fillDecimalPeriod(totalPayableAmouont));
		
		// Set current invoice's payable amount
		ci.setAmount_payable(totalPayableAmouont);
		ci.setFinal_payable_amount(TMUtils.bigSub(totalPayableAmouont, totalCreditBack));
		ci.setBalance(TMUtils.bigOperationTwoReminders(ci.getFinal_payable_amount(), ci.getAmount_paid(), "sub"));

		// Iteratively inserting invoice detail(s) into tm_invoice_detail table
		for (CustomerInvoiceDetail cid : cids) {
			cid.setInvoice_id(ci.getId());
			ciDetailMapper.insertCustomerInvoiceDetail(cid);
		}
		
		// Set invoice details into 
		ci.setCustomerInvoiceDetails(cids);

		invoicePDF.setCurrentCustomerInvoice(ci);

		// set file path
		Map<String, Object> map = null;
		try {
			map = invoicePDF.create();
			// generate invoice PDF
			ci.setInvoice_pdf_path((String) map.get("filePath"));
		} catch (DocumentException | IOException e) {
			e.printStackTrace();
		}
		// add sql condition: id
//		ci.setAmount_paid((Double) map.get("amount_paid"));
//		ci.setFinal_payable_amount((Double) map.get("final_amount_payable"));
//		ci.setAmount_payable((Double) map.get("amount_payable"));
//		ci.setBalance((Double) map.get("balance"));
		ci.setStatus("unpaid");
		ciMapper.updateCustomerInvoice(ci);

		// Deleting repeated invoices
		ciMapper.deleteCustomerInvoiceByRepeat();
		
	}
	
	
	@Transactional
	public void createInvoicePDFBoth(CustomerOrder customerOrder
			,Notification notificationEmail
			,Notification notificationSMS, Boolean is_Next_Invoice, boolean isRegenerate){
		// store company detail begin
		CompanyDetail companyDetail = this.companyDetailMapper.selectCompanyDetail();
		// store company detail end

		Customer customer = customerOrder.getCustomer();
		// current invoice model
		CustomerInvoice ci = new CustomerInvoice();
		ci.getParams().put("customer_id", customer.getId());
		ci.getParams().put("order_id", customerOrder.getId());
		// previous invoice model
		CustomerInvoice cpi = new CustomerInvoice();
		cpi.getParams().put("customer_id", customer.getId());
		cpi.getParams().put("order_id", customerOrder.getId());
		
		if(!isRegenerate){
			// If Not Regenerate then set next invoice create date
			Date invoiceCreateDay = !is_Next_Invoice 
					? (customerOrder.getOrder_using_start() != null 
							? customerOrder.getOrder_using_start() 
							: new Date()) 
					: customerOrder.getNext_invoice_create_date();
			// Next invoice create date
			ci.setCustomer_id(customer.getId());
			ci.setOrder_id(customerOrder.getId());
			ci.setCreate_date(invoiceCreateDay);
			ci.setDue_date(TMUtils.getInvoiceDueDate(invoiceCreateDay, 10));
			this.ciMapper.insertCustomerInvoice(ci);
			// If Not Regenerate then get most recent invoice as previous invoice
			cpi.getParams().put("where", "by_max_id");
		} else {
			// If Regenerate then get most recent invoice as current invoice
			ci.getParams().put("where", "by_max_id");
			ci = this.ciMapper.selectCustomerInvoice(ci);
			// If Regenerate then get before than most recent invoice as previous invoice
			if(!"unpaid".equals(ci.getStatus())){
				cpi.getParams().put("where", "by_max_id");
				this.ciMapper.insertCustomerInvoice(ci);
			} else {
				cpi.getParams().put("where", "by_second_id");
			}
		}
		// Customer previous invoice
		cpi = this.ciMapper.selectCustomerInvoice(cpi);
		if (cpi != null && !ci.getId().equals(cpi.getId())) {
			ci.setLast_invoice_id(cpi.getId());
			ci.setLastCustomerInvoice(cpi);
		} else {
			cpi = null;
		}
		boolean isFirst = cpi==null ? true : false;
		
		// detail holder begin
		List<CustomerInvoiceDetail> cids = new ArrayList<CustomerInvoiceDetail>();
		// detail holder end
		List<CustomerOrderDetail> customerOrderDetails = customerOrder.getCustomerOrderDetails();

		Double totalAmountPayable = 0d;
		Double totalCreditBack = 0d;
		
		String pstn_number = "";
		
		for (CustomerOrderDetail cod: customerOrderDetails) {

			if("pstn".equals(cod.getDetail_type())){
				
				if((cod.getPstn_number()!=null && !"".equals(cod.getPstn_number().trim()))
					&& ("".equals(pstn_number) || pstn_number==null)){
					pstn_number = cod.getPstn_number();
				}
				
			}
			
			CustomerInvoiceDetail cid = new CustomerInvoiceDetail();
			cid.setInvoice_id(ci.getId());
			cid.setInvoice_detail_name(cod.getDetail_name());
			cid.setInvoice_detail_unit(cod.getDetail_unit() == null ? 1 : cod.getDetail_unit());

			// if detail type equals discount and detail expire date greater
			// equal than today's date then go into if statement
			if ("discount".equals(cod.getDetail_type())
					&& cod.getDetail_expired().getTime() >= System.currentTimeMillis()) {
				
				cid.setInvoice_detail_discount(cod.getDetail_price());
				// decrease amountPayable
				totalCreditBack = TMUtils.bigAdd(totalCreditBack, TMUtils.bigMultiply(cid.getInvoice_detail_discount(), cid.getInvoice_detail_unit()));
				// add invoice detail to list
				cids.add(cid);
				// else if detail type is discount then this discount is expired
				// and will not be add to the invoice detail list
			} else if (!"discount".equals(cod.getDetail_type())) {
				if(cod.getDetail_type()!=null && cod.getDetail_type().contains("plan-")){
					// if is first invoice and unit isn't null then assigned from unit, otherwise assign to 1
					cid.setInvoice_detail_unit(cod.getDetail_unit() != null && !is_Next_Invoice && isFirst ? cod.getDetail_unit() : 1);
					cid.setInvoice_detail_price(cod.getDetail_price() == null ? 0d : cod.getDetail_price());
					// increase amountPayable
					totalAmountPayable =  cid.getInvoice_detail_price() != null ? TMUtils.bigAdd(totalAmountPayable, TMUtils.bigMultiply(cid.getInvoice_detail_price(), cid.getInvoice_detail_unit())) : 0;
					// add invoice detail to list
					cids.add(cid);
				} else if((!is_Next_Invoice && isFirst) || cod.getDetail_expired().getTime() >= System.currentTimeMillis()
						  && !"termination-credit".equals(cod.getDetail_type())){
					// if is first invoice and unit isn't null then assigned from unit, otherwise assign to 1
					cid.setInvoice_detail_unit(cod.getDetail_unit() != null && !is_Next_Invoice && isFirst ? cod.getDetail_unit() : 1);
					cid.setInvoice_detail_price(cod.getDetail_price() == null ? 0d : cod.getDetail_price());
					// increase amountPayable
					totalAmountPayable =  cid.getInvoice_detail_price() != null ? TMUtils.bigAdd(totalAmountPayable, TMUtils.bigMultiply(cid.getInvoice_detail_price(), cid.getInvoice_detail_unit())) : 0;
					// add invoice detail to list
					cids.add(cid);
				} else if("termination-credit".equals(cod.getDetail_type())){
					cid.setInvoice_detail_name(cod.getDetail_name());
					cid.setInvoice_detail_unit(cod.getDetail_unit());
					cid.setInvoice_detail_discount(cod.getDetail_price());
					cid.setInvoice_detail_desc(cod.getDetail_desc());
					cids.add(cid);
					totalCreditBack = TMUtils.bigAdd(totalCreditBack, TMUtils.bigMultiply(cod.getDetail_price(), cod.getDetail_unit()));
				}
				if (cod.getDetail_type()!=null && cod.getDetail_type().contains("plan-") 
						&& !"plan-topup".equals(cod.getDetail_type()) && !isRegenerate) {
					
					// if is next invoice then plus one month else plus unit month(s)
					int nextInvoiceMonth = is_Next_Invoice && !isFirst ? 1 : cod.getDetail_unit();
					int nextInvoiceDay = is_Next_Invoice && !isFirst ? 0 : -15;
					Calendar calNextInvoiceDay = Calendar.getInstance();
					calNextInvoiceDay.setTime(!is_Next_Invoice && !isFirst
								? (customerOrder.getOrder_using_start() != null 
								? customerOrder.getOrder_using_start() 
								: new Date()) 
						: customerOrder.getNext_invoice_create_date());
					calNextInvoiceDay.add(Calendar.MONTH, nextInvoiceMonth);
					calNextInvoiceDay.add(Calendar.DAY_OF_MONTH, nextInvoiceDay);

					// update customer order's next invoice create day begin
					customerOrder.setNext_invoice_create_date(calNextInvoiceDay.getTime());
					customerOrder.getParams().put("id", customerOrder.getId());
					
					this.customerOrderMapper.updateCustomerOrder(customerOrder);
					// update customer order's next invoice create day end
				}
			}
		}

		InvoicePDFCreator invoicePDF = new InvoicePDFCreator();
		invoicePDF.setCompanyDetail(companyDetail);
		invoicePDF.setCustomer(customer);
		invoicePDF.setOrg(this.organizationMapper.selectOrganizationByCustomerId(customer.getId()));
		invoicePDF.setCurrentCustomerInvoice(ci);
		
		// BEGIN IF HAVE PSTN_NUMBER
		if(!"".equals(pstn_number) && pstn_number!=null){
			totalAmountPayable = TMUtils.ccrOperation(pstn_number, cids, invoicePDF, totalAmountPayable, this.customerCallRecordMapper, this.callInternationalRateMapper);
		}
		// END IF HAVE PSTN_NUMBER

		ci.setAmount_payable(totalAmountPayable);
		ci.setFinal_payable_amount(TMUtils.bigSub(totalAmountPayable, totalCreditBack));
		ci.setStatus("unpaid");
		ci.setAmount_paid(ci.getAmount_paid() == null ? 0d : ci.getAmount_paid());
		// balance = final payable - paid
		ci.setBalance(TMUtils.bigSub(ci.getFinal_payable_amount(), ci.getAmount_paid()));
		// Add cids into ci
		ci.setCustomerInvoiceDetails(cids);
		
		// Add cids into db
		for (CustomerInvoiceDetail cid : cids) {
			this.ciDetailMapper.insertCustomerInvoiceDetail(cid);
		}

		// Set file path
		Map<String, Object> map = null;
		try {
			map = invoicePDF.create();
			// generate invoice PDF
			ci.setInvoice_pdf_path((String) map.get("filePath"));
		} catch (DocumentException | IOException e) {
			e.printStackTrace();
		}
		
		ci.getParams().put("id", ci.getId());
		this.ciMapper.updateCustomerInvoice(ci);

		// Deleting repeated invoices
		this.ciMapper.deleteCustomerInvoiceByRepeat();

		if(!isRegenerate){
//			// call mail at value retriever
//			MailRetriever.mailAtValueRetriever(notificationEmail, customer, customerOrder, ci, companyDetail);
//			ApplicationEmail applicationEmail = new ApplicationEmail();
//			applicationEmail.setAddressee(customer.getEmail());
//			applicationEmail.setSubject(notificationEmail.getTitle());
//			applicationEmail.setContent(notificationEmail.getContent());
//			// binding attachment name & path to email
//			applicationEmail.setAttachName("invoice_" + ci.getId() + ".pdf");
//			applicationEmail.setAttachPath((String) map.get("filePath"));
//			this.mailerService.sendMailByAsynchronousMode(applicationEmail);
//
//			// get sms register template from db
//			MailRetriever.mailAtValueRetriever(notificationSMS, customer, customerOrder, ci, companyDetail);
//			// send sms to customer's mobile phone
//			this.smserService.sendSMSByAsynchronousMode(customer.getCellphone(), notificationSMS.getContent());
		}
	}
	
	/**
	 * BEGIN CustomerOrderDetail
	 */
	
	public String queryCustomerOrderDetailGroupByOrderId(int order_id) {
		List<CustomerOrderDetail> customerOrderDetails = this.customerOrderDetailMapper.selectCustomerOrderDetailsByOrderId(order_id);
		for (CustomerOrderDetail customerOrderDetail : customerOrderDetails) {
			if(customerOrderDetail.getDetail_plan_group()!=null && customerOrderDetail.getDetail_plan_group().indexOf("plan-")>-1){
				return customerOrderDetail.getDetail_plan_group();
			}
		}
		return "";
	}
	
	/**
	 * END CustomerOrderDetail
	 */
	
	
	/**
	 * BEGIN CustomerTransaction
	 */
	
	@Transactional
	public void createCustomerTransaction(CustomerTransaction customerTransaction){
		this.customerTransactionMapper.insertCustomerTransaction(customerTransaction);
	}
	
	/**
	 * END CustomerTransaction
	 */
	
	
	/**
	 * BEGIN Organization
	 */
	
	@Transactional
	public Organization queryOrganizationByCustomerId(int customer_id){
		return this.organizationMapper.selectOrganizationByCustomerId(customer_id);
	}
	
	@Transactional
	public void createOrganization(Organization org){
		this.organizationMapper.insertOrganization(org);
	}
	
	/**
	 * END Organization
	 */

	@Transactional 
	public String queryCustomerPreviousProviderInvoiceFilePathById(int id){
		return this.customerOrderMapper.selectCustomerPreviousProviderInvoiceFilePathById(id);
	}

	@Transactional 
	public String queryCustomerCreditFilePathById(int id){
		return this.customerOrderMapper.selectCustomerCreditFilePathById(id);
	}

	@Transactional 
	public String queryCustomerDDPayFormPathById(int id){
		return this.customerOrderMapper.selectCustomerDDPayFormPathById(id);
	}
}
