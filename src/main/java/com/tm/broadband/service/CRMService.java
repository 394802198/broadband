package com.tm.broadband.service;


import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itextpdf.text.DocumentException;
import com.tm.broadband.email.ApplicationEmail;
import com.tm.broadband.mapper.CallInternationalRateMapper;
import com.tm.broadband.mapper.CompanyDetailMapper;
import com.tm.broadband.mapper.ContactUsMapper;
import com.tm.broadband.mapper.CustomerCallRecordMapper;
import com.tm.broadband.mapper.CustomerCallingRecordCallplusMapper;
import com.tm.broadband.mapper.CustomerCreditMapper;
import com.tm.broadband.mapper.CustomerDDPayMapper;
import com.tm.broadband.mapper.CustomerInvoiceDetailMapper;
import com.tm.broadband.mapper.CustomerInvoiceMapper;
import com.tm.broadband.mapper.CustomerMapper;
import com.tm.broadband.mapper.CustomerOrderBroadbandASIDMapper;
import com.tm.broadband.mapper.CustomerOrderChorusAddonMapper;
import com.tm.broadband.mapper.CustomerOrderDetailDeleteRecordMapper;
import com.tm.broadband.mapper.CustomerOrderDetailMapper;
import com.tm.broadband.mapper.CustomerOrderDetailRecoverableListMapper;
import com.tm.broadband.mapper.CustomerOrderMapper;
import com.tm.broadband.mapper.CustomerOrderNZCallingRatesSettingMapper;
import com.tm.broadband.mapper.CustomerOrderOnsiteDetailMapper;
import com.tm.broadband.mapper.CustomerOrderOnsiteMapper;
import com.tm.broadband.mapper.CustomerOrderProvisionChecklistMapper;
import com.tm.broadband.mapper.CustomerServiceRecordMapper;
import com.tm.broadband.mapper.CustomerTransactionMapper;
import com.tm.broadband.mapper.EarlyTerminationChargeMapper;
import com.tm.broadband.mapper.EarlyTerminationChargeParameterMapper;
import com.tm.broadband.mapper.InviteRatesMapper;
import com.tm.broadband.mapper.ManualDefrayLogMapper;
import com.tm.broadband.mapper.ManualManipulationRecordMapper;
import com.tm.broadband.mapper.NZAreaCodeListMapper;
import com.tm.broadband.mapper.NotificationMapper;
import com.tm.broadband.mapper.ProvisionLogMapper;
import com.tm.broadband.mapper.TerminationRefundMapper;
import com.tm.broadband.mapper.TicketCommentMapper;
import com.tm.broadband.mapper.TicketMapper;
import com.tm.broadband.mapper.UserMapper;
import com.tm.broadband.mapper.VOSVoIPCallRecordMapper;
import com.tm.broadband.mapper.VOSVoIPRateMapper;
import com.tm.broadband.mapper.VoucherMapper;
import com.tm.broadband.model.CompanyDetail;
import com.tm.broadband.model.ContactUs;
import com.tm.broadband.model.Customer;
import com.tm.broadband.model.CustomerBillingLog;
import com.tm.broadband.model.CustomerCredit;
import com.tm.broadband.model.CustomerDDPay;
import com.tm.broadband.model.CustomerInvoice;
import com.tm.broadband.model.CustomerInvoiceDetail;
import com.tm.broadband.model.CustomerOrder;
import com.tm.broadband.model.CustomerOrderBroadbandASID;
import com.tm.broadband.model.CustomerOrderChorusAddon;
import com.tm.broadband.model.CustomerOrderDetail;
import com.tm.broadband.model.CustomerOrderDetailDeleteRecord;
import com.tm.broadband.model.CustomerOrderDetailRecoverableList;
import com.tm.broadband.model.CustomerOrderNZCallingRatesSetting;
import com.tm.broadband.model.CustomerOrderOnsite;
import com.tm.broadband.model.CustomerOrderOnsiteDetail;
import com.tm.broadband.model.CustomerOrderProvisionChecklist;
import com.tm.broadband.model.CustomerServiceRecord;
import com.tm.broadband.model.CustomerTransaction;
import com.tm.broadband.model.EarlyTerminationCharge;
import com.tm.broadband.model.EarlyTerminationChargeParameter;
import com.tm.broadband.model.Hardware;
import com.tm.broadband.model.InviteRates;
import com.tm.broadband.model.JSONBean;
import com.tm.broadband.model.ManualDefrayLog;
import com.tm.broadband.model.ManualManipulationRecord;
import com.tm.broadband.model.Notification;
import com.tm.broadband.model.Page;
import com.tm.broadband.model.Plan;
import com.tm.broadband.model.ProvisionLog;
import com.tm.broadband.model.TerminationRefund;
import com.tm.broadband.model.Ticket;
import com.tm.broadband.model.TicketComment;
import com.tm.broadband.model.User;
import com.tm.broadband.model.Voucher;
import com.tm.broadband.model.VoucherBannedList;
import com.tm.broadband.pdf.EarlyTerminationChargePDFCreator;
import com.tm.broadband.pdf.InvoicePDFCreator;
import com.tm.broadband.pdf.OrderingPDFCreator;
import com.tm.broadband.pdf.ReceiptPDFCreator;
import com.tm.broadband.pdf.TerminationRefundPDFCreator;
import com.tm.broadband.util.Calculation4PlanTermInvoice;
import com.tm.broadband.util.CallingAndRentalFeeCalucation;
import com.tm.broadband.util.MailRetriever;
//import com.tm.broadband.util.Post2Xero;
import com.tm.broadband.util.TMUtils;

/**
 * CRM service
 * 
 * @author Cook1fan, Don Chen
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
	private ContactUsMapper contactUsMapper;
	private ManualDefrayLogMapper manualDefrayLogMapper;
	private CustomerCallRecordMapper customerCallRecordMapper;
	private CallInternationalRateMapper callInternationalRateMapper;
	private EarlyTerminationChargeMapper earlyTerminationChargeMapper;
	private EarlyTerminationChargeParameterMapper earlyTerminationChargeParameterMapper;
	private TerminationRefundMapper terminationRefundMapper;
	private CustomerServiceRecordMapper customerServiceRecordMapper;
	private VoucherMapper voucherMapper;
	private CustomerCallingRecordCallplusMapper customerCallingRecordCallplusMapper;
	private TicketMapper ticketMapper;
	private TicketCommentMapper ticketCommentMapper;
	private ManualManipulationRecordMapper manualManipulationRecordMapper;
	private VOSVoIPCallRecordMapper vosVoIPCallRecordMapper;
	private VOSVoIPRateMapper vosVoIPRateMapper;
	private InviteRatesMapper inviteRatesMapper;
	private UserMapper userMapper;
	private NZAreaCodeListMapper nzAreaCodeListMapper;
	private CustomerOrderDetailDeleteRecordMapper customerOrderDetailDeleteRecordMapper;
	private CustomerOrderDetailRecoverableListMapper customerOrderDetailRecoverableListMapper;
	private CustomerCreditMapper customerCreditMapper;
	private CustomerDDPayMapper customerDDPayMapper;
	private CustomerOrderProvisionChecklistMapper customerOrderProvisionChecklistMapper;
	private CustomerOrderOnsiteMapper customerOrderOnsiteMapper;
	private CustomerOrderOnsiteDetailMapper customerOrderOnsiteDetailMapper;
	private CustomerOrderChorusAddonMapper customerOrderChorusAddonMapper;
	private CustomerOrderBroadbandASIDMapper customerOrderBroadbandASIDMapper;
	private CustomerOrderNZCallingRatesSettingMapper customerOrderNZCallingRatesSettingMapper;
	
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
			ContactUsMapper contactUsMapper,
			ManualDefrayLogMapper manualDefrayLogMapper,
			CustomerCallRecordMapper customerCallRecordMapper,
			CallInternationalRateMapper callInternationalRateMapper,
			EarlyTerminationChargeMapper earlyTerminationChargeMapper,
			EarlyTerminationChargeParameterMapper earlyTerminationChargeParameterMapper,
			TerminationRefundMapper terminationRefundMapper,
			CustomerServiceRecordMapper customerServiceRecordMapper,
			VoucherMapper voucherMapper,
			CustomerCallingRecordCallplusMapper customerCallingRecordCallplusMapper,
			TicketMapper ticketMapper,
			TicketCommentMapper ticketCommentMapper,
			ManualManipulationRecordMapper manualManipulationRecordMapper,
			VOSVoIPCallRecordMapper vosVoIPCallRecordMapper,
			VOSVoIPRateMapper vosVoIPRateMapper,
			InviteRatesMapper inviteRatesMapper,
			UserMapper userMapper,
			NZAreaCodeListMapper nzAreaCodeListMapper,
			CustomerOrderDetailDeleteRecordMapper customerOrderDetailDeleteRecordMapper,
			CustomerOrderDetailRecoverableListMapper customerOrderDetailRecoverableListMapper,
			CustomerCreditMapper customerCreditMapper,
			CustomerDDPayMapper customerDDPayMapper,
			CustomerOrderProvisionChecklistMapper customerOrderProvisionChecklistMapper,
			CustomerOrderOnsiteMapper customerOrderOnsiteMapper,
			CustomerOrderOnsiteDetailMapper customerOrderOnsiteDetailMapper,
			CustomerOrderChorusAddonMapper customerOrderChorusAddonMapper,
			CustomerOrderBroadbandASIDMapper customerOrderBroadbandASIDMapper,
			CustomerOrderNZCallingRatesSettingMapper customerOrderNZCallingRatesSettingMapper){
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
		this.contactUsMapper = contactUsMapper;
		this.manualDefrayLogMapper = manualDefrayLogMapper;
		this.customerCallRecordMapper = customerCallRecordMapper;
		this.callInternationalRateMapper = callInternationalRateMapper;
		this.earlyTerminationChargeMapper = earlyTerminationChargeMapper;
		this.earlyTerminationChargeParameterMapper = earlyTerminationChargeParameterMapper;
		this.terminationRefundMapper = terminationRefundMapper;
		this.customerServiceRecordMapper = customerServiceRecordMapper;
		this.voucherMapper = voucherMapper;
		this.customerCallingRecordCallplusMapper = customerCallingRecordCallplusMapper;
		this.ticketMapper = ticketMapper;
		this.ticketCommentMapper = ticketCommentMapper;
		this.manualManipulationRecordMapper = manualManipulationRecordMapper;
		this.vosVoIPCallRecordMapper = vosVoIPCallRecordMapper;
		this.vosVoIPRateMapper = vosVoIPRateMapper;
		this.inviteRatesMapper = inviteRatesMapper;
		this.userMapper = userMapper;
		this.nzAreaCodeListMapper = nzAreaCodeListMapper;
		this.customerOrderDetailDeleteRecordMapper = customerOrderDetailDeleteRecordMapper;
		this.customerOrderDetailRecoverableListMapper = customerOrderDetailRecoverableListMapper;
		this.customerCreditMapper = customerCreditMapper;
		this.customerDDPayMapper = customerDDPayMapper;
		this.customerOrderProvisionChecklistMapper = customerOrderProvisionChecklistMapper;
		this.customerOrderOnsiteMapper = customerOrderOnsiteMapper;
		this.customerOrderOnsiteDetailMapper = customerOrderOnsiteDetailMapper;
		this.customerOrderChorusAddonMapper = customerOrderChorusAddonMapper;
		this.customerOrderBroadbandASIDMapper = customerOrderBroadbandASIDMapper;
		this.customerOrderNZCallingRatesSettingMapper = customerOrderNZCallingRatesSettingMapper;
	}
	
	
	public void doOrderConfirm(Customer customer, Plan plan) {
		
		customer.getCustomerOrder().setCustomerOrderDetails(new ArrayList<CustomerOrderDetail>());
		customer.getCustomerOrder().setOrder_create_date(new Date());
		//customer.getCustomerOrder().setOrder_status("paid");
		customer.getCustomerOrder().setOrder_type(plan.getPlan_group().replace("plan", "order"));

		CustomerOrderDetail cod_plan = new CustomerOrderDetail();
		cod_plan.setDetail_name(plan.getPlan_name());
		//cod_plan.setDetail_desc(plan.getPlan_desc());
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
			
			customer.getCustomerOrder().setOrder_total_price(plan.getPlan_price());
			
//			CustomerOrderDetail cod_topup = new CustomerOrderDetail();
//			cod_topup.setDetail_name("Broadband Top-Up");
//			cod_topup.setDetail_price(plan.getTopup().getTopup_fee());
//			cod_topup.setDetail_type("topup");
//			//cod_topup.setDetail_is_next_pay(0);
//			//cod_topup.setDetail_expired(new Date());
//			cod_topup.setDetail_unit(1);
//			
//			customer.getCustomerOrder().getCustomerOrderDetails().add(cod_topup);
			
		} else if ("plan-no-term".equals(plan.getPlan_group())) {
			
			customer.getCustomerOrder().setOrder_total_price(plan.getPlan_price() * plan.getPlan_prepay_months());
			System.out.println("Order_total_price: " + customer.getCustomerOrder().getOrder_total_price());
			
			//cod_plan.setDetail_is_next_pay(1);
			
		} else if ("plan-term".equals(plan.getPlan_group())) {
			
			if ("personal".equals(plan.getPlan_class())) {
				customer.getCustomerOrder().setOrder_status("paid");
			} else if ("business".equals(plan.getPlan_class())) {
				customer.getCustomerOrder().setOrder_status("pending");
			}
			
			customer.getCustomerOrder().setOrder_total_price(plan.getPlan_price() * plan.getPlan_prepay_months());
			
			CustomerOrderDetail cod_hd = new CustomerOrderDetail();
			if ("ADSL".equals(plan.getPlan_type())) {
				cod_hd.setDetail_name("TP - LINK 150Mbps Wireless N ADSL2+ Modem Router(Free)");
			} else if ("VDSL".equals(plan.getPlan_type())) {
				cod_hd.setDetail_name("TP - LINK 150Mbps Wireless N VDSL2+ Modem Router(Free)");
			} else if ("UFB".equals(plan.getPlan_type())) {
				cod_hd.setDetail_name("UFB Modem Router(Free)");
			}
			
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
	public void saveCustomerOrder(Customer customer, CustomerOrder customerOrder, List<CustomerTransaction> cts) {
		
		customer.setRegister_date(new Date(System.currentTimeMillis()));
		customer.setActive_date(new Date(System.currentTimeMillis()));
		
		if (!customer.getNewOrder()) {
			this.customerMapper.insertCustomer(customer);
		}
			
		customerOrder.setCustomer_id(customer.getId());
		
		if("business".equals(customerOrder.getCustomer_type())){
			customerOrder.setIs_ddpay(true);
		}
		
		System.out.println("customerOrder.getSale_id(): " + customerOrder.getSale_id());
		this.customerOrderMapper.insertCustomerOrder(customerOrder);
		
		for (CustomerOrderDetail cod : customerOrder.getCustomerOrderDetails()) {
			cod.setOrder_id(customerOrder.getId());
			this.customerOrderDetailMapper.insertCustomerOrderDetail(cod);
		}
		
		if (cts != null) {
			for (CustomerTransaction ct: cts) {
				ct.setCustomer_id(customer.getId());
				ct.setOrder_id(customer.getCustomerOrder().getId());
				ct.setTransaction_date(new Date(System.currentTimeMillis()));
				this.customerTransactionMapper.insertCustomerTransaction(ct);
			}
		}
		
		if (customer.getVouchers() != null) {
			for (Voucher vQuery: customer.getVouchers()) {
				vQuery.setStatus("used");
				vQuery.setCustomer_id(customer.getId());
				vQuery.setOrder_id(customerOrder.getId());
				vQuery.getParams().put("serial_number", vQuery.getSerial_number());
				vQuery.getParams().put("card_number", vQuery.getCard_number());
				this.voucherMapper.updateVoucher(vQuery);
			}
		}
		
	}
	
	@Transactional
	public void customerTopup(Customer customer, CustomerTransaction customerTransaction) {
		this.customerMapper.updateCustomer(customer);
		this.customerTransactionMapper.insertCustomerTransaction(customerTransaction);
	}
	
	@Transactional
	public void customerOrderingForm(Customer customer, CustomerOrder order, CustomerTransaction customerTransaction) {
		this.customerMapper.updateCustomer(customer);
		this.customerOrderMapper.updateCustomerOrder(order);
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
		List<Customer> cs = this.queryCustomers(customer);
		return cs!=null && cs.size()>0 ? cs.get(0) : null;
	}

	@Transactional
	public List<Customer> queryCustomers(Customer customer) {
		return this.customerMapper.selectCustomers(customer);
	}
	
	@Transactional
	public Customer queryCustomerByIdWithCustomerOrder(int id) {
		Customer cQuery = new Customer();
		cQuery.getParams().put("id", id);
		Customer c = this.queryCustomer(cQuery);
		CustomerOrder coQuery = new CustomerOrder();
		coQuery.getParams().put("customer_id", id);
		List<CustomerOrder> cos = this.customerOrderMapper.selectCustomerOrders(coQuery);
		
		// Retrieve 
		for (int i=0; i<cos.size(); i++) {
			CustomerOrderDetail codQuery = new CustomerOrderDetail();
			codQuery.getParams().put("order_id", cos.get(i).getId());
			List<CustomerOrderDetail> cods = this.customerOrderDetailMapper.selectCustomerOrderDetails(codQuery);
			cos.get(i).setCustomerOrderDetails(cods);
		}
		
		c.setCustomerOrders(cos);
		return c;
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
	public Customer queryCustomerWhenLogin(Customer c) {
		Customer customer = this.queryCustomer(c);
		/*if ("business".equals(customer.getCustomer_type())) {
			customer.setOrganization(this.organizationMapper.selectOrganizationByCustomerId(customer.getId()));
		}*/
		return customer;
	}
	
	@Transactional
	public CustomerOrder queryCustomerOrder(CustomerOrder customerOrder) {
		List<CustomerOrder> cos = this.queryCustomerOrders(customerOrder);
		return cos!=null && cos.size()>0 ? cos.get(0) : null;
	}
	
	@Transactional
	public List<CustomerOrder> queryCustomerOrders(CustomerOrder customerOrder) {
		return this.customerOrderMapper.selectCustomerOrders(customerOrder);
	}
	
	@Transactional
	public Page<CustomerOrder> queryCustomerOrdersByPage(Page<CustomerOrder> page) {
		page.setTotalRecord(this.customerOrderMapper.selectCustomerOrdersSum(page));
		page.setResults(this.customerOrderMapper.selectCustomerOrdersByPage(page));
		return page;
	}
	
	@Transactional
	public int queryCustomerOrdersSumByPage(Page<CustomerOrder> page) {
		return this.customerOrderMapper.selectCustomerOrdersSum(page);
	}
	
	public void editCustomerOrderSVCVLanRFSDateEmpty(CustomerOrder customerOrder){
		this.customerOrderMapper.updateCustomerOrderSVCVLanRFSDateEmpty(customerOrder);
	}
	
	public void editCustomerOrderServiceGivingNextInvoiceEmpty(CustomerOrder customerOrder){
		this.customerOrderMapper.updateCustomerOrderServiceGivingNextInvoiceEmpty(customerOrder);
	}
	
	public void editCustomerOrderBroadbandASIDEmpty(CustomerOrder customerOrder){
		this.customerOrderMapper.updateCustomerOrderBroadbandASIDEmpty(customerOrder);
	}

	@Transactional
	public Page<CustomerInvoice> queryCustomerInvoicesByPage(Page<CustomerInvoice> page) {
		page.setTotalRecord(this.ciMapper.selectCustomerInvoicesSum(page));
		page.setResults(this.ciMapper.selectCustomerInvoicesByPage(page));
		return page;
	}
	
	@Transactional
	public int queryCustomerInvoicesSumByPage(Page<CustomerInvoice> page) {
		return this.ciMapper.selectCustomerInvoicesSum(page);
	}

	public List<CustomerInvoiceDetail> queryCustomerInvoiceDetailsByCustomerInvoiceId(int id) {
		return this.ciDetailMapper.selectCustomerInvoiceDetailsByCustomerInvoiceId(id);
	}

	@Transactional
	public void editCustomerTransaction(CustomerTransaction ct){
		this.customerTransactionMapper.updateCustomerTransaction(ct);
	}
	
	@Transactional
	public Page<CustomerTransaction> queryCustomerTransactionsByPage(Page<CustomerTransaction> page) {
		page.setTotalRecord(this.customerTransactionMapper.selectCustomerTransactionsSum(page));
		page.setResults(this.customerTransactionMapper.selectCustomerTransactionsByPage(page));
		return page;
	}
	
	@Transactional
	public CustomerTransaction queryCustomerTransaction(CustomerTransaction ct) {
		List<CustomerTransaction> cts = queryCustomerTransactions(ct);
		return cts!=null && cts.size()>0 ? cts.get(0) : null;
	}
	
	@Transactional
	public List<CustomerTransaction> queryCustomerTransactions(CustomerTransaction ct) {
		return this.customerTransactionMapper.selectCustomerTransactions(ct);
	}
	
	@Transactional
	public List<CustomerTransaction> queryCustomerTransactionsByCustomerId(int id) {
		return this.customerTransactionMapper.selectCustomerTransactionsByCustomerId(id);
	}
	
	@Transactional
	public int queryCustomerTransactionsSumByPage(Page<CustomerTransaction> page) {
		return this.customerTransactionMapper.selectCustomerTransactionsSum(page);
	}

	@Transactional
	public void editCustomer(Customer customer) {
		this.customerMapper.updateCustomer(customer);
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
		
	}

//	@Transactional
//	public void createCustomer(Customer customer) {
//		this.customerMapper.insertCustomer(customer);
//		if ("business".equals(customer.getCustomer_type())) {
//			customer.getOrganization().setCustomer_id(customer.getId());
//			this.organizationMapper.insertOrganization(customer.getOrganization());
//		}
//	}

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
	public void createCustomerInvoice(CustomerInvoice ci){
		this.ciMapper.insertCustomerInvoice(ci);
	}
	
	public CustomerInvoice queryCustomerInvoice(CustomerInvoice ci) {
		List<CustomerInvoice> cis = this.ciMapper.selectCustomerInvoices(ci);
		return cis != null && cis.size() > 0 ? cis.get(0) : null;
	}
	
	public List<CustomerInvoice> queryCustomerInvoices(CustomerInvoice ci) {
		return this.ciMapper.selectCustomerInvoices(ci);
	}
	
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
	
	@Transactional
	public void removeCustomerInvoiceByIdWithDetail(int id){
		this.ciMapper.deleteCustomerInvoiceById(id);
		this.ciDetailMapper.deleteCustomerInvoiceDetailByInvoiceId(id);
	}
	/*
	 * end customer invoice
	 * */
	
	/*
	 * customer order detail delete record
	 * */
	public void createCustomerOrderDetailDeleteRecord(CustomerOrderDetailDeleteRecord coddr){
		this.customerOrderDetailDeleteRecordMapper.insertCustomerOrderDetailDeleteRecord(coddr);
	}
	
	public CustomerOrderDetailDeleteRecord queryCustomerOrderDetailDeleteRecord(CustomerOrderDetailDeleteRecord coddr) {
		List<CustomerOrderDetailDeleteRecord> coddrs = this.customerOrderDetailDeleteRecordMapper.selectCustomerOrderDetailDeleteRecord(coddr);
		return coddrs!=null && coddrs.size()>0 ? coddrs.get(0) : null;
	}
	
	public List<CustomerOrderDetailDeleteRecord> queryCustomerOrderDetailDeleteRecords(CustomerOrderDetailDeleteRecord coddr) {
		return this.customerOrderDetailDeleteRecordMapper.selectCustomerOrderDetailDeleteRecord(coddr);
	}
	
	@Transactional
	public Page<CustomerOrderDetailDeleteRecord> queryCustomerOrderDetailDeleteRecordsByPage(Page<CustomerOrderDetailDeleteRecord> page){
		page.setTotalRecord(this.customerOrderDetailDeleteRecordMapper.selectCustomerOrderDetailDeleteRecordsSum(page));
		page.setResults(this.customerOrderDetailDeleteRecordMapper.selectCustomerOrderDetailDeleteRecordsByPage(page));
		return page;
	}
	
	public int queryCustomerOrderDetailDeleteRecordsSumByPage(Page<CustomerOrderDetailDeleteRecord> page){
		return this.customerOrderDetailDeleteRecordMapper.selectCustomerOrderDetailDeleteRecordsSum(page);
	}
	
	@Transactional
	public void editCustomerOrderDetailDeleteRecord(CustomerOrderDetailDeleteRecord coddr){
		this.customerOrderDetailDeleteRecordMapper.updateCustomerOrderDetailDeleteRecord(coddr);
	}
	
	@Transactional
	public void removeCustomerOrderDetailDeleteRecordByDetailId(int id){
		this.customerOrderDetailDeleteRecordMapper.deleteCustomerOrderDetailDeleteRecordByDetailId(id);
	}
	/*
	 * end customer order detail delete record
	 * */
	
	
	/*
	 * customer order detail recoverable list
	 * */
	
	public void createCustomerOrderDetailRecoverableList(CustomerOrderDetailRecoverableList codrl){
		this.customerOrderDetailRecoverableListMapper.insertCustomerOrderDetailRecoverableList(codrl);
	}
	
	public CustomerOrderDetailRecoverableList queryCustomerOrderDetailRecoverableList(CustomerOrderDetailRecoverableList codrl) {
		List<CustomerOrderDetailRecoverableList> codrls = this.customerOrderDetailRecoverableListMapper.selectCustomerOrderDetailRecoverableList(codrl);
		return codrls!=null && codrls.size()>0 ? codrls.get(0) : null;
	}
	
	public List<CustomerOrderDetailRecoverableList> queryCustomerOrderDetailRecoverableLists(CustomerOrderDetailRecoverableList codrl) {
		return this.customerOrderDetailRecoverableListMapper.selectCustomerOrderDetailRecoverableList(codrl);
	}
	
	@Transactional
	public Page<CustomerOrderDetailRecoverableList> queryCustomerOrderDetailRecoverableListsByPage(Page<CustomerOrderDetailRecoverableList> page){
		page.setTotalRecord(this.customerOrderDetailRecoverableListMapper.selectCustomerOrderDetailRecoverableListsSum(page));
		page.setResults(this.customerOrderDetailRecoverableListMapper.selectCustomerOrderDetailRecoverableListsByPage(page));
		return page;
	}
	
	public int queryCustomerOrderDetailRecoverableListsSumByPage(Page<CustomerOrderDetailRecoverableList> page){
		return this.customerOrderDetailRecoverableListMapper.selectCustomerOrderDetailRecoverableListsSum(page);
	}
	
	@Transactional
	public void editCustomerOrderDetailRecoverableList(CustomerOrderDetailRecoverableList codrl){
		this.customerOrderDetailRecoverableListMapper.updateCustomerOrderDetailRecoverableList(codrl);
	}
	
	@Transactional
	public void removeCustomerOrderDetailRecoverableListByDetailId(int id){
		this.customerOrderDetailRecoverableListMapper.deleteCustomerOrderDetailRecoverableListByDetailId(id);
	}
	
	/*
	 * end customer order detail recoverable list
	 * */
	
	
	/**
	 * BEGIN Ticket
	 */
	@Transactional
	public List<Ticket> queryTicket(Ticket t){
		return this.ticketMapper.selectTicket(t);
	}
	
	@Transactional
	public void createTicket(Ticket t){
		this.ticketMapper.insertTicket(t);
	}
	
	@Transactional
	public void editTicket(Ticket t){
		this.ticketMapper.updateTicket(t);
	}
	
	@Transactional
	public Page<Ticket> queryTicketsByPage(Page<Ticket> page){
		page.setTotalRecord(this.ticketMapper.selectTicketsSum(page));
		page.setResults(this.ticketMapper.selectTicketsByPage(page));
		return page;
	}
	
	@Transactional
	public void removeTicketById(int id){
		this.ticketMapper.deleteTicketById(id);
	}
	
	@Transactional
	public int queryTicketsBySum(Page<Ticket> page){
		return this.ticketMapper.selectTicketsSum(page);
	}

	/**
	 * END Ticket
	 */
	
	/**
	 * BEGIN TicketComment
	 */
	@Transactional
	public List<TicketComment> queryTicketComment(TicketComment tc){
		return this.ticketCommentMapper.selectTicketComment(tc);
	}
	
	@Transactional
	public void createTicketComment(TicketComment tc){
		this.ticketCommentMapper.insertTicketComment(tc);
	}
	
	@Transactional
	public void editTicketComment(TicketComment tc){
		this.ticketCommentMapper.updateTicketComment(tc);
	}
	
	@Transactional
	public Page<TicketComment> queryTicketCommentsByPage(Page<TicketComment> page){
		page.setTotalRecord(this.ticketCommentMapper.selectTicketCommentsSum(page));
		page.setResults(this.ticketCommentMapper.selectTicketCommentsByPage(page));
		return page;
	}
	
	@Transactional
	public void removeTicketCommentById(int id){
		this.ticketCommentMapper.deleteTicketCommentById(id);
	}
	
	@Transactional
	public void removeTicketCommentByTicketId(int id){
		this.ticketCommentMapper.deleteTicketCommentByTicketId(id);
	}
	
	@Transactional
	public int queryTicketCommentsBySum(Page<TicketComment> page){
		return this.ticketCommentMapper.selectTicketCommentsSum(page);
	}

	/**
	 * END TicketComment
	 */
	
	/**
	 * BEGIN createOrderingForm
	 */
	public String createOrderingFormPDFByDetails(Customer c){
		// call OrderPDFCreator
		OrderingPDFCreator oPDFCreator = new OrderingPDFCreator();
		oPDFCreator.setCo(c.getCustomerOrder());
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			resultMap = oPDFCreator.create();
		} catch (DocumentException | IOException e) {
			e.printStackTrace();
		}
		CustomerOrder co = new CustomerOrder();
		co.getParams().put("id", c.getCustomerOrder().getId());
		co.setOrdering_form_pdf_path((String) resultMap.get("path"));
		co.setOrder_total_price((Double) resultMap.get("totalPrice"));
		this.customerOrderMapper.updateCustomerOrder(co);
		
		return (String) resultMap.get("path");
	}
	/**
	 * END createOrderingForm
	 */

	/**
	 * BEGIN createReceipt
	 */
	public String createReceiptPDFByDetails(CustomerOrder co){
		// call OrderPDFCreator
		ReceiptPDFCreator rPDFCreator = new ReceiptPDFCreator();
		rPDFCreator.setCo(co);

		String pdfPath = "";
		try {
			pdfPath = rPDFCreator.create();
		} catch (DocumentException | IOException e) {
			e.printStackTrace();
		}
		CustomerOrder coUpdate = new CustomerOrder();
		coUpdate.getParams().put("id", co.getId());
		coUpdate.setReceipt_pdf_path(pdfPath);
		this.customerOrderMapper.updateCustomerOrder(coUpdate);
		
		return pdfPath;
	}
	/**
	 * END createReceipt
	 */
	
	// manually generating invoice PDF by id
	@Transactional
	public void createInvoicePDFByInvoiceID(int invoiceId, boolean isOverduePenalty){
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
		CustomerOrder coQuery = new CustomerOrder();
		coQuery.getParams().put("id", ci.getOrder_id());
		CustomerOrder co = this.queryCustomerOrder(coQuery);
		// get necessary models end
		
		// If is overdue penalty
		if(isOverduePenalty){
			CustomerInvoiceDetail cid = new CustomerInvoiceDetail();
			cid.setInvoice_id(ci.getId());
			cid.setInvoice_detail_name("Overdue Penalty");
			cid.setInvoice_detail_desc("10% of Total Payable Amount");
			cid.setInvoice_detail_unit(1);
			cid.setInvoice_detail_price(TMUtils.bigMultiply(ci.getFinal_payable_amount(), 0.1d));
			this.ciDetailMapper.insertCustomerInvoiceDetail(cid);
			ci.getCustomerInvoiceDetails().add(cid);
		}
		
		// initialize invoice's important informations
		InvoicePDFCreator invoicePDF = new InvoicePDFCreator();
		invoicePDF.setCompanyDetail(companyDetail);
		invoicePDF.setCo(co);
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
		ci.setAmount_paid((Double) map.get("amount_paid"));
		ci.setAmount_payable((Double) map.get("amount_payable"));
		ci.setFinal_payable_amount((Double) map.get("final_amount_payable"));
		ci.setBalance((Double) map.get("balance"));
		ci.setInvoice_type("general-invoice");
		ci.getParams().put("id", ci.getId());
		
		this.ciMapper.updateCustomerInvoice(ci);
		// invoice PDF generator manipulation end
	}
	
	@Transactional
	public void createInvoicePDF(CustomerOrder customerOrder
			,Notification notificationEmail
			,Notification notificationSMS) {

		CustomerOrder coQuery = new CustomerOrder();
		coQuery.getParams().put("id", customerOrder.getId());
		coQuery = this.queryCustomerOrder(coQuery);
		createInvoicePDFBoth(coQuery
				,new Notification(notificationEmail.getTitle(), notificationEmail.getContent())
				,new Notification(notificationEmail.getTitle(), notificationEmail.getContent())
				, false, false);	// false : first invoice
	}

	@Transactional 
	public List<Map<String, Object>> createNextInvoice(CustomerOrder co) throws ParseException{
		Notification notificationEmail = this.notificationMapper.selectNotificationBySort("invoice", "email");
		Notification notificationSMS = this.notificationMapper.selectNotificationBySort("invoice", "sms");
		List<CustomerOrder> customerOrders = this.queryCustomerOrders(co);

        List<Map<String, Object>> resultMaps = new ArrayList<Map<String, Object>>();

		for (CustomerOrder customerOrder : customerOrders) {
			//System.out.println(customerOrder.getId() + ":" + customerOrder.getNext_invoice_create_date());
			Map<String, Object> resultMap = createInvoicePDFBoth(customerOrder
					,new Notification(notificationEmail.getTitle(), notificationEmail.getContent())
					,new Notification(notificationSMS.getTitle(), notificationSMS.getContent())
					, true, false);	// true : next invoice
			resultMaps.add(resultMap);
		}
		
		// BEGIN TOPUP NOTIFICATION
//		CustomerOrder topupCustomerOrder = new CustomerOrder();
//		topupCustomerOrder.getParams().put("where", "query_topup");
//		topupCustomerOrder.getParams().put("order_status", "using");
//		topupCustomerOrder.getParams().put("order_type_topup", "order-topup");
//		Calendar cal = Calendar.getInstance();
//
//        // using new SimpleDateFormat("yyyy-MM-dd").parse("2014-06-13") under testing environment
//		// using new Date() under production environment
//		cal.setTime(new Date());
//		cal.add(Calendar.DATE, 1);
//		topupCustomerOrder.getParams().put("order_due_backward_one", cal.getTime());
//		cal.add(Calendar.DATE, 1);
//		topupCustomerOrder.getParams().put("order_due_backward_two", cal.getTime());
//		List<CustomerOrder> topupCustomerOrders = this.customerOrderMapper.selectCustomerOrdersBySome(topupCustomerOrder);
//		
//		
//		Notification topupNotificationEmail = this.notificationMapper.selectNotificationBySort("topup-notification", "email");
//		Notification topupNotificationSMS = this.notificationMapper.selectNotificationBySort("topup-notification", "sms");
//		CompanyDetail companyDetail = this.companyDetailMapper.selectCompanyDetail();
//		
//		for (CustomerOrder customerOrder : topupCustomerOrders) {
//			Customer c = customerOrder.getCustomer();
//			
//			// Prevent template pollution
//			Notification topupNotificationEmailFinal = new Notification(topupNotificationEmail.getTitle(), topupNotificationEmail.getContent());
//			Notification topupNotificationSMSFinal = new Notification(topupNotificationSMS.getTitle(), topupNotificationSMS.getContent());
//
//			// call mail at value retriever
//			Organization org = this.organizationMapper.selectOrganizationByCustomerId(c.getId());
//			c.setOrganization(org);
//			MailRetriever.mailAtValueRetriever(topupNotificationEmailFinal, c, customerOrder, companyDetail);
//			ApplicationEmail applicationEmail = new ApplicationEmail();
//			applicationEmail.setAddressee(c.getEmail());
//			applicationEmail.setSubject(topupNotificationEmailFinal.getTitle());
//			applicationEmail.setContent(topupNotificationEmailFinal.getContent());
//			// binding attachment name & path to email
//			this.mailerService.sendMailByAsynchronousMode(applicationEmail);
//
//			// get sms register template from db
//			MailRetriever.mailAtValueRetriever(topupNotificationSMSFinal, c, customerOrder, companyDetail);
//			// send sms to customer's mobile phone
//			this.smserService.sendSMSByAsynchronousMode(c.getCellphone(), topupNotificationSMSFinal.getContent());
//
//			topupNotificationEmailFinal = null;
//			topupNotificationSMSFinal = null;
//			c = null;
//			org = null;
//		}
//		topupNotificationEmail = null;
//		topupNotificationSMS = null;
		// END TOPUP NOTIFICATION
		
		return resultMaps;
		
	}
	
	@Transactional 
	public void sendTermPlanInvoicePDF() {
		
		Notification notificationEmail = this.notificationMapper.selectNotificationBySort("invoice", "email");
		Notification notificationSMS = this.notificationMapper.selectNotificationBySort("invoice", "sms");
		
		CompanyDetail companyDetail = this.companyDetailMapper.selectCompanyDetail();
		
		CustomerOrder coTemp = new CustomerOrder();
		coTemp.getParams().put("where", "query_term");
		coTemp.getParams().put("status", "using");
		coTemp.getParams().put("order_type", "order-term");
		coTemp.getParams().put("is_ddpay", true);
		List<CustomerOrder> cos = this.customerOrderMapper.selectCustomerOrders(coTemp);
		
		for (CustomerOrder co : cos) {
			
			// Prevent template pollution
			Notification notificationEmailFinal = new Notification(notificationEmail.getTitle(), notificationEmail.getContent());
			Notification notificationSMSFinal = new Notification(notificationSMS.getTitle(), notificationSMS.getContent());
			
			Customer cQuery = new Customer();
			cQuery.getParams().put("id", co.getCustomer_id());
			cQuery = this.queryCustomer(cQuery);
			
			CustomerInvoice ci = new CustomerInvoice();
			ci.getParams().put("where", "by_max_id_with_invoice_type");
			ci.getParams().put("customer_id", cQuery.getId());
			ci.getParams().put("order_id", co.getId());
			ci.getParams().put("invoice_type", "general-invoice");
			ci = this.ciMapper.selectCustomerInvoice(ci);


			MailRetriever.mailAtValueRetriever(notificationEmailFinal, cQuery, co, ci, companyDetail);
			ApplicationEmail applicationEmail = new ApplicationEmail();
			applicationEmail.setAddressee(co.getEmail()!=null && !"".equals(co.getEmail()) ? co.getEmail() : cQuery.getEmail());
			applicationEmail.setSubject(notificationEmailFinal.getTitle());
			applicationEmail.setContent(notificationEmailFinal.getContent());
			// binding attachment name & path to email
			applicationEmail.setAttachName("invoice_" + ci.getId() + ".pdf");
			applicationEmail.setAttachPath(ci.getInvoice_pdf_path());
			this.mailerService.sendMailByAsynchronousMode(applicationEmail);
			
			if("personal".equals(co.getCustomer_type())){
				// get sms register template from db
				MailRetriever.mailAtValueRetriever(notificationSMSFinal, cQuery, co, ci, companyDetail);
				// send sms to customer's mobile phone
				this.smserService.sendSMSByAsynchronousMode(co.getMobile()!=null && !"".equals(co.getMobile()) ? co.getMobile() : cQuery.getCellphone(), notificationSMSFinal.getContent());
			}
			
			notificationEmailFinal = null;
			notificationSMSFinal = null;
			ci = null;

		}
		notificationEmail = null;
		notificationSMS = null;
	}
	
	@Transactional 
	public void createNextCallingInvoicePDF() {
		
		Notification notificationEmail = this.notificationMapper.selectNotificationBySort("calling-invoice", "email");
		Notification notificationSMS = this.notificationMapper.selectNotificationBySort("calling-invoice", "sms");
		
		CompanyDetail companyDetail = this.companyDetailMapper.selectCompanyDetail();
		
		CustomerOrder coTemp = new CustomerOrder();
		coTemp.getParams().put("where", "query_callings");
		coTemp.getParams().put("status", "using");
		List<CustomerOrder> cos = this.customerOrderMapper.selectCustomerOrders(coTemp);
		
		if(cos!=null && cos.size()>0){

			List<Map<String, Object>> resultMaps = new ArrayList<Map<String, Object>>();
			
			for (CustomerOrder co : cos) {
				
				Notification notificationEmailFinal = new Notification(notificationEmail.getTitle(), notificationEmail.getContent());
				Notification notificationSMSFinal = new Notification(notificationSMS.getTitle(), notificationSMS.getContent());
				
				List<CustomerInvoiceDetail> cids = new ArrayList<CustomerInvoiceDetail>();

				List<CustomerOrderDetail> pcmsPSTN = new ArrayList<CustomerOrderDetail>();
				List<CustomerOrderDetail> pcmsVoIP = new ArrayList<CustomerOrderDetail>();
				CustomerOrderDetail codQuery = new CustomerOrderDetail();
				codQuery.getParams().put("order_id", co.getId());
				List<CustomerOrderDetail> cods = this.queryCustomerOrderDetails(codQuery);

				List<String> pstn_numbers = new ArrayList<String>();
				List<String> voip_numbers = new ArrayList<String>();

				Double totalCreditBack = 0d;
				Double totalAmountPayable = 0d;
				
				for (CustomerOrderDetail cod : cods) {
					
					CustomerInvoiceDetail cid = new CustomerInvoiceDetail();

					if("pstn".equals(cod.getDetail_type()) || "voip".equals(cod.getDetail_type())){
						
						cid.setInvoice_detail_name(cod.getDetail_name());
						cid.setInvoice_detail_unit(cod.getDetail_unit());
						cid.setInvoice_detail_type(cod.getDetail_type());
						cid.setInvoice_detail_price(cod.getDetail_price()!=null ? cod.getDetail_price() : 0d);
						cids.add(cid);
						
					}

					if("pstn".equals(cod.getDetail_type())){
						
						if((cod.getPstn_number()!=null && !"".equals(cod.getPstn_number().trim()))){
							pstn_numbers.add(cod.getPstn_number());
						}
						
						cid = null;
						continue;
						
					} else if("voip".equals(cod.getDetail_type())){
						
						if((cod.getPstn_number()!=null && !"".equals(cod.getPstn_number().trim()))){
							voip_numbers.add(cod.getPstn_number());
						}
						
						cid = null;
						continue;
						
					} else if(cod.getDetail_type()!=null && ("present-calling-minutes".equals(cod.getDetail_type()) || "super-free-calling".equals(cod.getDetail_type()))){
						cid.setInvoice_detail_name(cod.getDetail_name());
						cid.setInvoice_detail_price(cod.getDetail_price()!=null ? cod.getDetail_price() : 0d);
						cid.setInvoice_detail_desc(cod.getDetail_calling_minute()+" Minutes");
						cid.setInvoice_detail_unit(cod.getDetail_unit());
						cid.setInvoice_detail_type(cod.getDetail_type());
						cids.add(cid);
						
						if(cod.getDetail_desc().endsWith("voip")){
							
							pcmsVoIP.add(cod);
							
						} else {
							
							pcmsPSTN.add(cod);
							
						}
						
						totalAmountPayable = TMUtils.bigAdd(totalAmountPayable, cod.getDetail_price()!=null ? cod.getDetail_price() : 0d);
						
						cid = null;
						continue;
					
					// Termed & No Termed
					} else if("discount".equals(cod.getDetail_type()) && cod.getDetail_expired() != null && cod.getDetail_expired().getTime() >= System.currentTimeMillis()){
						
						cid.setInvoice_detail_name(cod.getDetail_name());
						cid.setInvoice_detail_discount(cod.getDetail_price());
						cid.setInvoice_detail_unit(cod.getDetail_unit());
						cid.setInvoice_detail_type(cod.getDetail_type());

						// totalCreditBack add ( discount price times discount unit )
						totalCreditBack = TMUtils.bigAdd(totalCreditBack, TMUtils.bigMultiply(cod.getDetail_price(), cod.getDetail_unit()));
						cids.add(cid);
						
						cid = null;
						continue;
						
					// Static IP
					} else if(cod.getDetail_type()!=null && "static-ip".equals(cod.getDetail_type())){
						
						cid.setInvoice_detail_name(cod.getDetail_name());
						cid.setInvoice_detail_price(cod.getDetail_price()!=null ? cod.getDetail_price() : 0d);
						cid.setInvoice_detail_unit(cod.getDetail_unit());
						cid.setInvoice_detail_type(cod.getDetail_type());
						cids.add(cid);
						
						totalAmountPayable = TMUtils.bigAdd(totalAmountPayable, cod.getDetail_price()!=null ? cod.getDetail_price() : 0d);
						
						cid = null;
						continue;
						
					// Else if unexpired then add order detail(s) into invoice detail(s)
					}
				}
				
				InvoicePDFCreator invoicePDF = new InvoicePDFCreator();
				CustomerInvoice ci = new CustomerInvoice();
				
				ci.setOrder_id(co.getId());
				
				if(pstn_numbers.size() > 0){
					
					for (String pstn_number : pstn_numbers) {
						
						totalAmountPayable = CallingAndRentalFeeCalucation.ccrRentalOperation(ci, false, pstn_number, cids, totalAmountPayable, customerCallRecordMapper, this.ciMapper, customerOrderChorusAddonMapper);
						
						totalAmountPayable = CallingAndRentalFeeCalucation.ccrOperation(ci, false, pcmsPSTN, pstn_number, cids, invoicePDF, totalAmountPayable, this.customerCallRecordMapper, this.callInternationalRateMapper, this.customerCallingRecordCallplusMapper, this.ciMapper, co.getCustomer_type(), this.customerOrderNZCallingRatesSettingMapper, co.getId());
						
					}
				}
				
				if(voip_numbers.size() > 0){
					
					for (String voip_number : voip_numbers) {
						totalAmountPayable = CallingAndRentalFeeCalucation.voipCallOperation(ci, false, pcmsVoIP, voip_number, cids, invoicePDF, totalAmountPayable, nzAreaCodeListMapper, vosVoIPRateMapper, vosVoIPCallRecordMapper, ciMapper, co.getCustomer_type(), this.customerOrderNZCallingRatesSettingMapper, co.getId());
						
					}
				}
				
				if(totalAmountPayable>0){
					
					if(ci.getId()==null){
						this.ciMapper.insertCustomerInvoice(ci);
					}
					
					ci.setCustomer_id(co.getCustomer_id());
					ci.setOrder_id(co.getId());
					ci.setCreate_date(new Date());
					Calendar calDueDate = Calendar.getInstance();
					calDueDate.add(Calendar.DATE, 6);
					ci.setDue_date(calDueDate.getTime());
					ci.setStatus("unpaid");
					ci.setMemo("calling-only");
					
					ci.setAmount_paid(0d);
					ci.setAmount_payable(totalAmountPayable);
					ci.setFinal_payable_amount(TMUtils.bigSub(totalAmountPayable, totalCreditBack));
					ci.setBalance(ci.getFinal_payable_amount());
					ci.setCustomerInvoiceDetails(cids);
					for (CustomerInvoiceDetail cid : cids) {
						cid.setInvoice_id(ci.getId());
						this.ciDetailMapper.insertCustomerInvoiceDetail(cid);
					}

					invoicePDF.setCompanyDetail(companyDetail);
					invoicePDF.setCo(co);
					invoicePDF.setCurrentCustomerInvoice(ci);
					
					String filePath = null;
					try {
						filePath = (String) invoicePDF.create().get("filePath");
						
					} catch (DocumentException | IOException e) {
						e.printStackTrace();
					}
					ci.setInvoice_pdf_path(filePath);
					ci.setInvoice_type("calling-invoice");
					ci.getParams().put("id", ci.getId());
					this.ciMapper.updateCustomerInvoice(ci);

					
					Customer cQuery = new Customer();
					cQuery.getParams().put("id", co.getCustomer_id());
					cQuery = this.queryCustomer(cQuery);
					
					
					MailRetriever.mailAtValueRetriever(notificationEmailFinal, cQuery,  co, ci, companyDetail);
					ApplicationEmail applicationEmail = new ApplicationEmail();
					applicationEmail.setAddressee(co.getEmail()!=null && !"".equals(co.getEmail()) ? co.getEmail() : cQuery.getEmail());
					applicationEmail.setSubject(notificationEmailFinal.getTitle());
					applicationEmail.setContent(notificationEmailFinal.getContent());
					// binding attachment name & path to email
					applicationEmail.setAttachName("invoice_" + ci.getId() + ".pdf");
					applicationEmail.setAttachPath(filePath);
					this.mailerService.sendMailByAsynchronousMode(applicationEmail);

					// get sms register template from db
					MailRetriever.mailAtValueRetriever(notificationSMSFinal, cQuery,  co, ci, companyDetail);
					// send sms to customer's mobile phone
					this.smserService.sendSMSByAsynchronousMode(co.getMobile()!=null && !"".equals(co.getMobile()) ? co.getMobile() : cQuery.getCellphone(), notificationSMSFinal.getContent());
					
					
					
					Map<String, Object> resultMap = new HashMap<String, Object>();
					resultMap.put("customer", cQuery);
					resultMap.put("customerInvoice", ci);
					resultMap.put("customerOrder", co);
					if(co.getIs_ddpay()==null || !co.getIs_ddpay()){
						resultMaps.add(resultMap);
					}
					
				}
				
			}
			
//			if(resultMaps.size()>0){
//				
//				Post2Xero.postMultiInvoices(resultMaps, "Monthly Calling Charge");
//			}
			
		}
		
	}

	@Transactional
	public void createEarlyTerminationInvoice(Integer order_id
			, Date terminatedDate
			, Integer executor_id) throws ParseException{
		CustomerOrder coQuery = new CustomerOrder();
		coQuery.getParams().put("id", order_id);
		coQuery = this.queryCustomerOrder(coQuery);
		Map<String, Object> map = TMUtils.earlyTerminationDatesCalculation(coQuery.getOrder_using_start(), terminatedDate);
		
		EarlyTerminationCharge etc = new EarlyTerminationCharge();
		etc.setCustomer_id(coQuery.getCustomer_id());
		etc.setOrder_id(order_id);
		etc.setCreate_date(new Date());
		etc.setService_given_date(coQuery.getOrder_using_start());
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
			invoicePDFPath = new EarlyTerminationChargePDFCreator(cd, coQuery, etc).create();
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

		CustomerOrder coQuery = new CustomerOrder();
		coQuery.getParams().put("id", order_id);
		coQuery = this.queryCustomerOrder(coQuery);
		
		Map<String, Object> map = TMUtils.terminationRefundCalculations(terminatedDate, monthlyCharge);
		
		TerminationRefund tr = new TerminationRefund();
		tr.setCustomer_id(coQuery.getCustomer_id());
		tr.setOrder_id(order_id);
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
			refundPDFPath = new TerminationRefundPDFCreator(cd, coQuery, tr, u).create();
		} catch (DocumentException | IOException e) {
			e.printStackTrace();
		}
		tr.getParams().put("id", tr.getId());
		tr.setRefund_pdf_path(refundPDFPath);
		this.terminationRefundMapper.updateTerminationRefund(tr);
	}

	@Transactional
	public void customerUpdateInvoiceUnpaid2Overdue() throws ParseException{
		CustomerInvoice ciQuery = new CustomerInvoice();
		ciQuery.getParams().put("where", "by_unpaid_due_date_less_than_today");
		List<CustomerInvoice> cis = this.ciMapper.selectCustomerInvoices(ciQuery);
		
		if(cis!=null && cis.size()>0){
			for (CustomerInvoice ci : cis) {
				CustomerInvoice finalCi = new CustomerInvoice();
				finalCi.getParams().put("id", ci.getId());
				finalCi.setStatus("overdue");
				this.ciMapper.updateCustomerInvoice(finalCi);
				finalCi = null;
			}
		}
		
		ciQuery = null;
		cis = null;
	}

	@Transactional
	public void customerUpdateInvoiceOverdue2BadDebit() throws ParseException{
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
		cal.add(Calendar.MONTH, -3);
		cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
		Date dueMonthMax = cal.getTime();
		
		CustomerInvoice ciQuery = new CustomerInvoice();
		ciQuery.getParams().put("where", "by_overdue_over_three_month");
		ciQuery.getParams().put("dueMonthMax", dueMonthMax);
		List<CustomerInvoice> cis = this.ciMapper.selectCustomerInvoices(ciQuery);
		
		if(cis!=null && cis.size()>0){
			for (CustomerInvoice ci : cis) {
				CustomerInvoice finalCi = new CustomerInvoice();
				finalCi.getParams().put("id", ci.getId());
				finalCi.setStatus("bad-debit");
				this.ciMapper.updateCustomerInvoice(finalCi);
				finalCi = null;
			}
		}
		
		// RELEASE MEMORY SPACE
		cal = null;
		dueMonthMax = null;
		ciQuery = null;
		cis = null;
	}

	@Transactional
	public void createInvoiceOverduePenalty() throws ParseException{
		CustomerInvoice ciTemp = new CustomerInvoice();
		Calendar beginCal = Calendar.getInstance();
		beginCal.set(Calendar.MONTH, beginCal.get(Calendar.MONTH)-3);
		beginCal.set(Calendar.DAY_OF_MONTH, 1);
		beginCal.set(Calendar.HOUR_OF_DAY, 0);
		beginCal.set(Calendar.MINUTE, 0);
		beginCal.set(Calendar.SECOND, 0);
		Calendar endCal = Calendar.getInstance();
		endCal.set(Calendar.DAY_OF_MONTH, 1);
		endCal.set(Calendar.HOUR_OF_DAY, 0);
		endCal.set(Calendar.MINUTE, 0);
		endCal.set(Calendar.SECOND, 0);
		endCal.add(Calendar.DAY_OF_MONTH, -1);
		ciTemp.getParams().put("where", "by_overdue_penalty");
		ciTemp.getParams().put("customer_type", "personal");
		ciTemp.getParams().put("status", "unpaid");
		ciTemp.getParams().put("begin_date", beginCal.getTime());
		ciTemp.getParams().put("end_date", endCal.getTime());
		List<CustomerInvoice> cis = this.ciMapper.selectCustomerInvoices(ciTemp);
		for (CustomerInvoice ci : cis) {
			createInvoicePDFByInvoiceID(ci.getId(), true);
		}
	}

	@Transactional
	public void createTopupPlanInvoice() throws ParseException{
		
		// Production environment should edit as shown below
		// status = using
		// order_type = order-topup
		// next_invoice_create_date = new Date()
		CustomerOrder coQuery = new CustomerOrder();
		coQuery.getParams().put("order_status", "using");
		coQuery.getParams().put("order_type", "order-topup");
		coQuery.getParams().put("next_invoice_create_date", TMUtils.parseDateYYYYMMDD("2014-10-13"));
		List<CustomerOrder> cos = this.customerOrderMapper.selectCustomerOrders(coQuery);
		
		// If found any order(s)
		if(cos.size() > 0){
			
			List<Map<String, Object>> resultMaps = new ArrayList<Map<String, Object>>();
			
			Notification notificationEmail = this.notificationMapper.selectNotificationBySort("invoice", "email");
			Notification notificationSMS = this.notificationMapper.selectNotificationBySort("invoice", "sms");
			
			boolean isRegenerateInvoice = false;
			boolean is_Next_Invoice = true;
			
			for (CustomerOrder co : cos) {
				
				Map<String, Object> resultMap = createTopupPlanInvoiceByOrder(co
						, new Notification(notificationEmail.getTitle(), notificationEmail.getContent())
						, new Notification(notificationSMS.getTitle(), notificationSMS.getContent())
						, isRegenerateInvoice
						, is_Next_Invoice);
				
				if(co.getIs_ddpay()==null || !co.getIs_ddpay()){
					resultMaps.add(resultMap);
				}
				
			}
			
//			if(resultMaps.size()>0){
//				Post2Xero.postMultiInvoices(resultMaps, "Monthly Broadband Charge");
//			}
			
		}
	}
	
	@Transactional
	public Map<String, Object> createTopupPlanInvoiceByOrder(CustomerOrder co,
			Notification notificationEmailFinal,
			Notification notificationSMSFinal,
			boolean isRegenerateInvoice,
			boolean is_Next_Invoice) throws ParseException{
		
		Customer cQuery = new Customer();
		cQuery.getParams().put("id", co.getCustomer_id());
		Customer c = this.queryCustomer(cQuery);
		
		CustomerOrderDetail codQuery = new CustomerOrderDetail();
		codQuery.getParams().put("order_id", co.getId());
		List<CustomerOrderDetail> cods = this.queryCustomerOrderDetails(codQuery);
		co.setCustomerOrderDetails(cods);
		
		// Current invoice
		CustomerInvoice ci = new CustomerInvoice();
//		System.out.println("isRegenerateInvoice: " + isRegenerateInvoice);
		
		// Previous invoice's preparation
		CustomerInvoice ciPreQuery = new CustomerInvoice();
		ciPreQuery.getParams().put("customer_id", c.getId());
		ciPreQuery.getParams().put("order_id", co.getId());
		// Previous invoice
		CustomerInvoice cpi = null;
		
		if(isRegenerateInvoice){
			CustomerInvoice ciCurQuery = new CustomerInvoice();
			ciCurQuery.getParams().put("where", "by_max_id");
			ciCurQuery.getParams().put("customer_id", c.getId());
			ciCurQuery.getParams().put("order_id", co.getId());
			ci = ciMapper.selectCustomerInvoice(ciCurQuery);
			if(ci != null && !"paid".equals(ci.getStatus())){
				ciPreQuery.getParams().put("where", "by_second_id");
				cpi = ciMapper.selectCustomerInvoice(ciPreQuery);
			} else if(ci != null) {
				ciPreQuery.getParams().put("where", "by_max_id");
				cpi = ciMapper.selectCustomerInvoice(ciPreQuery);
				ci = new CustomerInvoice();
				ci.setCustomer_id(c.getId());
				ci.setOrder_id(co.getId());
				ci.setCreate_date(new Date());
				ci.setDue_date(TMUtils.getInvoiceDueDate(ci.getCreate_date(), 2));
//				System.out.println("due_date_ci_!null: "+ci.getDue_date());
				ciMapper.insertCustomerInvoice(ci);
			} else if(ci == null){
				ci = new CustomerInvoice();
				ci.setCustomer_id(c.getId());
				ci.setOrder_id(co.getId());
				ci.setCreate_date(new Date());
				ci.setDue_date(TMUtils.getInvoiceDueDate(ci.getCreate_date(), 2));
//				System.out.println("due_date_ci_null: "+ci.getDue_date());
				ciMapper.insertCustomerInvoice(ci);
			}
		} else {
			// If Not Regenerate then set next invoice create date
			Date invoiceCreateDay = !is_Next_Invoice 
					? (co.getOrder_using_start() != null 
							? co.getOrder_using_start() 
							: new Date()) 
					: co.getNext_invoice_create_date();
			ciPreQuery.getParams().put("where", "by_max_id");
			cpi = ciMapper.selectCustomerInvoice(ciPreQuery);
			ci = new CustomerInvoice();
			ci.setCustomer_id(c.getId());
			ci.setOrder_id(co.getId());
			ci.setCreate_date(invoiceCreateDay);
			ci.setDue_date(TMUtils.getInvoiceDueDate(ci.getCreate_date(), 2));
//			System.out.println("!regenerate: "+ci.getDue_date());
			ciMapper.insertCustomerInvoice(ci);
			// Set id for invoice's update
		}
		
		// Delete all invoice's details
		if(ci!=null && ci.getId()!=null){
			this.ciDetailMapper.deleteCustomerInvoiceDetailByInvoiceId(ci.getId());
		}
		ci.setAmount_paid(ci.getAmount_paid()!=null ? ci.getAmount_paid() : 0d);
		ci.setAmount_payable(ci.getAmount_payable()!=null ? ci.getAmount_payable() : 0d);
		ci.setBalance(ci.getBalance()!=null ? ci.getBalance() : 0d);
		ci.getParams().put("id", ci.getId());	// For update invoice use

		boolean isFirst = //true;
				cpi == null ? true : false;

		// Calculate invoice's payable amount
		Double totalAmountPayable = 0d;
		Double totalCreditBack = 0d;
		
		List<CustomerInvoiceDetail> cids = new ArrayList<CustomerInvoiceDetail>();

		List<String> pstn_numbers = new ArrayList<String>();
		List<String> voip_numbers = new ArrayList<String>();
		List<CustomerOrderDetail> pcmsVoIP = new ArrayList<CustomerOrderDetail>();
		List<CustomerOrderDetail> pcmsPSTN = new ArrayList<CustomerOrderDetail>();

		InvoicePDFCreator invoicePDF = new InvoicePDFCreator();
		
		for (CustomerOrderDetail cod : co.getCustomerOrderDetails()) {
			CustomerInvoiceDetail cid = new CustomerInvoiceDetail();

			if(cod.getDetail_type()!=null && "pstn".equals(cod.getDetail_type())){
				
				if((cod.getPstn_number()!=null && !"".equals(cod.getPstn_number().trim()))){
					pstn_numbers.add(cod.getPstn_number());
				}
				
			}

			if(cod.getDetail_type()!=null && "voip".equals(cod.getDetail_type())){
				
				if((cod.getPstn_number()!=null && !"".equals(cod.getPstn_number().trim()))){
					voip_numbers.add(cod.getPstn_number());
				}
				
			}

			if(!isFirst
			&& (cod.getDetail_type()!=null && ("pstn".equals(cod.getDetail_type()) || "voip".equals(cod.getDetail_type())))){
				
				cid.setInvoice_detail_name(cod.getDetail_name());
				cid.setInvoice_detail_unit(cod.getDetail_unit());
				cid.setInvoice_detail_type(cod.getDetail_type());
				cid.setInvoice_detail_price(cod.getDetail_price()!=null ? cod.getDetail_price() : 0d);
				cids.add(cid);

				totalAmountPayable = TMUtils.bigAdd(totalAmountPayable, cod.getDetail_price()!=null ? cod.getDetail_price() : 0d);
				
			}
			
			if(cod.getDetail_type()!=null
			&& (("present-calling-minutes".equals(cod.getDetail_type()) || "super-free-calling".equals(cod.getDetail_type())))){
				cid.setInvoice_detail_name(cod.getDetail_name());
				cid.setInvoice_detail_price(cod.getDetail_price()!=null ? cod.getDetail_price() : 0d);
				cid.setInvoice_detail_desc(cod.getDetail_calling_minute()+" Minutes");
				cid.setInvoice_detail_unit(1);
				cid.setInvoice_detail_type(cod.getDetail_type());
				cids.add(cid);
				
				if(cod.getDetail_desc().endsWith("voip")){
					
					pcmsVoIP.add(cod);
					
				} else {
					
					pcmsPSTN.add(cod);
					
				}
				
				totalAmountPayable = TMUtils.bigAdd(totalAmountPayable, cod.getDetail_price()!=null ? cod.getDetail_price() : 0d);
				
				continue;
			}

			// Static IP
			if(cod.getDetail_type()!=null && "static-ip".equals(cod.getDetail_type())){
				
				cid.setInvoice_detail_name(cod.getDetail_name());
				cid.setInvoice_detail_price(cod.getDetail_price()!=null ? cod.getDetail_price() : 0d);
				cid.setInvoice_detail_unit(cod.getDetail_unit());
				cid.setInvoice_detail_type(cod.getDetail_type());
				cids.add(cid);
				
				totalAmountPayable = TMUtils.bigAdd(totalAmountPayable, cod.getDetail_price()!=null ? cod.getDetail_price() : 0d);
				
			}
			
			// Fax
			if(cod.getDetail_type()!=null && "fax".equals(cod.getDetail_type())){

				cid.setInvoice_detail_name(cod.getDetail_name()+" ( "+cod.getPstn_number()+" )");
				cid.setInvoice_detail_price(cod.getDetail_price()!=null ? cod.getDetail_price() : 0d);
				cid.setInvoice_detail_unit(cod.getDetail_unit());
				cid.setInvoice_detail_type(cod.getDetail_type());
				cids.add(cid);
				
				totalAmountPayable = TMUtils.bigAdd(totalAmountPayable, cod.getDetail_price()!=null ? cod.getDetail_price() : 0d);
				
			}
			
			if(isFirst){
				
				if(cod.getDetail_type()!=null && "plan-topup".equals(cod.getDetail_type())){
					
					cid.setInvoice_detail_name(cod.getDetail_name());
					Calendar cal = Calendar.getInstance(Locale.CHINA);
					cal.setTime(co.getOrder_using_start());
					cal.add(Calendar.WEEK_OF_MONTH, 1);
//					System.out.println("cal.add(Calendar.WEEK_OF_MONTH, 1): "+TMUtils.dateFormatYYYYMMDD(cal.getTime()));
					cal.add(Calendar.DAY_OF_WEEK, -1);
//					System.out.println("cal.add(Calendar.DAY_OF_WEEK, -1): "+TMUtils.dateFormatYYYYMMDD(cal.getTime()));
					Date startFrom = co.getOrder_using_start();
					Date endTo = cal.getTime();
					cid.setInvoice_detail_desc(TMUtils.retrieveMonthAbbrWithDate(startFrom)+" - "+TMUtils.retrieveMonthAbbrWithDate(endTo));
					cid.setInvoice_detail_price(cod.getDetail_price());
					cid.setInvoice_detail_unit(cod.getDetail_unit()!=null ? cod.getDetail_unit() : 1);
					cid.setInvoice_detail_type(cod.getDetail_type());
					
					cids.add(cid);
					
					Double subTotalPrice = TMUtils.bigMultiply(cod.getDetail_price(), cod.getDetail_unit()!=null ? cod.getDetail_unit() : 1);
					
					totalAmountPayable = TMUtils.bigAdd(totalAmountPayable, subTotalPrice);
					
				} else {
					if(cod.getDetail_type()!=null && "discount".equals(cod.getDetail_type()) && cod.getDetail_expired()!=null && cod.getDetail_expired().getTime() >= System.currentTimeMillis()){
						
						cid.setInvoice_detail_name(cod.getDetail_name());
						cid.setInvoice_detail_discount(cod.getDetail_price());
						cid.setInvoice_detail_unit(cod.getDetail_unit());
						cid.setInvoice_detail_type(cod.getDetail_type());
						
						cids.add(cid);
						
						Double subTotalCredit = TMUtils.bigMultiply(cod.getDetail_price(), cod.getDetail_unit());
						
						totalCreditBack = TMUtils.bigAdd(totalCreditBack, subTotalCredit);
						
					} else {

						cid.setInvoice_detail_name(cod.getDetail_name());
						cid.setInvoice_detail_price(cod.getDetail_price());
						cid.setInvoice_detail_unit(cod.getDetail_unit()!=null ? cod.getDetail_unit() : 1);
						cid.setInvoice_detail_type(cod.getDetail_type());
						
						cids.add(cid);
						
						Double subTotalPrice = TMUtils.bigMultiply(cod.getDetail_price(), cod.getDetail_unit()!=null ? cod.getDetail_unit() : 1);
						
						totalAmountPayable = TMUtils.bigAdd(totalAmountPayable, subTotalPrice);
						
					}
				}
				
			} else {
				
				// Add previous invoice's details into
				ci.setLast_invoice_id(cpi.getId());
				ci.setLastCustomerInvoice(cpi);
				
				if(cod.getDetail_type()!=null && "plan-topup".equals(cod.getDetail_type())){
					
					cid.setInvoice_detail_name(cod.getDetail_name());
					Calendar cal = Calendar.getInstance(Locale.CHINA);
					Date startFrom = co.getNext_invoice_create_date();
					if(is_Next_Invoice){
						cal.setTime(co.getNext_invoice_create_date());
						cal.add(Calendar.DAY_OF_WEEK, 3);
						startFrom = cal.getTime();
					} else {
						cal.setTime(co.getNext_invoice_create_date());
						cal.add(Calendar.WEEK_OF_MONTH, -1);
						cal.add(Calendar.DAY_OF_WEEK, 3);
						startFrom = cal.getTime();
					}
					cal.add(Calendar.WEEK_OF_MONTH, 1);
					cal.add(Calendar.DAY_OF_WEEK, -1);
					Date endTo = cal.getTime();
					cid.setInvoice_detail_desc(TMUtils.retrieveMonthAbbrWithDate(startFrom)+" - "+TMUtils.retrieveMonthAbbrWithDate(endTo));
					cid.setInvoice_detail_price(cod.getDetail_price());
					if(isFirst){
						cid.setInvoice_detail_unit(cod.getDetail_unit()!=null ? cod.getDetail_unit() : 1);
					} else {
						cid.setInvoice_detail_unit(1);
					}
					cid.setInvoice_detail_type(cod.getDetail_type());
					
					cids.add(cid);
					
					Double subTotalPrice = TMUtils.bigMultiply(cod.getDetail_price(), cod.getDetail_unit()!=null ? cod.getDetail_unit() : 1);
					
					totalAmountPayable = TMUtils.bigAdd(totalAmountPayable, subTotalPrice);
					
				} else {
					if(cod.getDetail_type()!=null && "discount".equals(cod.getDetail_type()) && cod.getDetail_expired()!=null && cod.getDetail_expired().getTime() >= System.currentTimeMillis()){
						
						cid.setInvoice_detail_name(cod.getDetail_name());
						cid.setInvoice_detail_discount(cod.getDetail_price());
						cid.setInvoice_detail_unit(cod.getDetail_unit());
						cid.setInvoice_detail_type(cod.getDetail_type());
						
						cids.add(cid);
						
						Double subTotalCredit = TMUtils.bigMultiply(cod.getDetail_price(), cod.getDetail_unit());
						
						totalCreditBack = TMUtils.bigAdd(totalCreditBack, subTotalCredit);
						
					} else if(cod.getDetail_expired()!=null && cod.getDetail_expired().getTime() >= System.currentTimeMillis()) {

						cid.setInvoice_detail_name(cod.getDetail_name());
						cid.setInvoice_detail_price(cod.getDetail_price());
						cid.setInvoice_detail_unit(cod.getDetail_unit()!=null ? cod.getDetail_unit() : 1);
						cid.setInvoice_detail_type(cod.getDetail_type());
						
						cids.add(cid);
						
						Double subTotalPrice = TMUtils.bigMultiply(cod.getDetail_price(), cod.getDetail_unit()!=null ? cod.getDetail_unit() : 1);
						
						totalAmountPayable = TMUtils.bigAdd(totalAmountPayable, subTotalPrice);
						
					}
				}
			}
			
			if(cod.getDetail_type()!=null && "plan-topup".equals(cod.getDetail_type()) && !isRegenerateInvoice){
				
				int nextInvoiceWeek = 1;
				int nextInvoiceDay = -2;
				Calendar calNextInvoiceDay = Calendar.getInstance();
				calNextInvoiceDay.setTime(co.getNext_invoice_create_date_flag());
				calNextInvoiceDay.add(Calendar.WEEK_OF_MONTH, nextInvoiceWeek);
				// update customer order's next invoice create day flag begin
				co.setNext_invoice_create_date_flag(calNextInvoiceDay.getTime());
				// update customer order's next invoice create day flag end

				calNextInvoiceDay.add(Calendar.DAY_OF_WEEK, nextInvoiceDay);
				// update customer order's next invoice create day begin
				co.setNext_invoice_create_date(calNextInvoiceDay.getTime());
				// update customer order's next invoice create day end
				
				co.getParams().put("id", co.getId());
				
				this.customerOrderMapper.updateCustomerOrder(co);
				
			}
		}

		if(!isFirst){
			// If previous invoice's balance less than zero
			if(cpi.getBalance()<0){
				ci.setAmount_paid(TMUtils.bigAdd(ci.getAmount_paid(), Math.abs(cpi.getBalance())));
				
				cpi.setBalance(0d);
				cpi.setAmount_paid(cpi.getFinal_payable_amount());
				cpi.getParams().put("id", cpi.getId());
				this.ciMapper.updateCustomerInvoice(cpi);
				
				ci.setStatus("not_pay_off");
			
			} else {
				if(!isRegenerateInvoice){
					ci.setStatus("unpaid");
				}
			}
		} else {
			ci.setStatus("unpaid");
		}

		// store company detail begin
		CompanyDetail companyDetail = companyDetailMapper.selectCompanyDetail();
		// store company detail end
		
		invoicePDF.setCompanyDetail(companyDetail);
		
		ci.setOrder_id(co.getId());

		
		if(pstn_numbers.size() > 0){
			
			for (String pstn_number : pstn_numbers) {
				
				totalAmountPayable = CallingAndRentalFeeCalucation.ccrRentalOperation(ci, isRegenerateInvoice, pstn_number, cids, totalAmountPayable, customerCallRecordMapper, this.ciMapper, customerOrderChorusAddonMapper);
				
				totalAmountPayable = CallingAndRentalFeeCalucation.ccrOperation(ci, isRegenerateInvoice, pcmsPSTN, pstn_number, cids, invoicePDF, totalAmountPayable, this.customerCallRecordMapper, this.callInternationalRateMapper, this.customerCallingRecordCallplusMapper, this.ciMapper, co.getCustomer_type(), this.customerOrderNZCallingRatesSettingMapper, co.getId());
				
			}
		}
		
		if(voip_numbers.size() > 0){
			
			for (String voip_number : voip_numbers) {
				totalAmountPayable = CallingAndRentalFeeCalucation.voipCallOperation(ci, isRegenerateInvoice, pcmsVoIP, voip_number, cids, invoicePDF, totalAmountPayable, nzAreaCodeListMapper, vosVoIPRateMapper, vosVoIPCallRecordMapper, ciMapper, co.getCustomer_type(), this.customerOrderNZCallingRatesSettingMapper, co.getId());
				
			}
		}
		
		// truncate unnecessary reminders, left only two reminders, e.g. 1.0001 change to 1.00
		totalCreditBack = Double.parseDouble(TMUtils.fillDecimalPeriod(totalCreditBack));
//		totalAmountPayable = isBusiness ? TMUtils.bigMultiply(totalAmountPayable, 1.15) : totalAmountPayable;
		
		// Set current invoice's payable amount
		ci.setAmount_payable(totalAmountPayable);
		ci.setFinal_payable_amount(TMUtils.bigSub(totalAmountPayable, totalCreditBack));
		ci.setBalance(TMUtils.bigOperationTwoReminders(ci.getFinal_payable_amount(), ci.getAmount_paid(), "sub"));

		// Iteratively inserting invoice detail(s) into tm_invoice_detail table
		for (CustomerInvoiceDetail cid : cids) {
			cid.setInvoice_id(ci.getId());
			ciDetailMapper.insertCustomerInvoiceDetail(cid);
		}
		
		// Set invoice details into 
		ci.setCustomerInvoiceDetails(cids);

		invoicePDF.setCurrentCustomerInvoice(ci);
		invoicePDF.setCo(co);
		invoicePDF.setLastCustomerInvoice(cpi);

		// set file path
		Map<String, Object> map = null;
		try {
			map = invoicePDF.create();
			// generate invoice PDF
			ci.setInvoice_pdf_path((String) map.get("filePath"));
		} catch (DocumentException | IOException e) {
			e.printStackTrace();
		}
		ci.setInvoice_type("general-invoice");
		ciMapper.updateCustomerInvoice(ci);

		// Deleting repeated invoices
		ciMapper.deleteCustomerInvoiceByRepeat();

		if(!isRegenerateInvoice){
			
			// call mail at value retriever
			MailRetriever.mailAtValueRetriever(notificationEmailFinal, c,  co, ci, companyDetail);
			ApplicationEmail applicationEmail = new ApplicationEmail();
			applicationEmail.setAddressee(c.getEmail());
			applicationEmail.setSubject(notificationEmailFinal.getTitle());
			applicationEmail.setContent(notificationEmailFinal.getContent());
			// binding attachment name & path to email
			applicationEmail.setAttachName("invoice_" + ci.getId() + ".pdf");
			applicationEmail.setAttachPath((String) map.get("filePath"));
			this.mailerService.sendMailByAsynchronousMode(applicationEmail);

			// get sms register template from db
			MailRetriever.mailAtValueRetriever(notificationSMSFinal, c, co, ci, companyDetail);
			// send sms to customer's mobile phone
			this.smserService.sendSMSByAsynchronousMode(c.getCellphone(), notificationSMSFinal.getContent());
		}
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("customer", c);
		resultMap.put("customerInvoice", ci);
		resultMap.put("customerOrder", co);
		return resultMap;
		
	}

	@Transactional
	public void createDDPayInvoiceManualManipulationRecord(ManualManipulationRecord mmr, User user) throws ParseException{

		try {
			this.createTermPlanInvoice();
		} catch (ParseException e) { e.printStackTrace(); }
		
		mmr.setAdmin_id(user.getId());
		mmr.setAdmin_name(user.getUser_name());
		mmr.setManipulation_time(new Date());
		mmr.setManipulation_name("Manually Generate DDPay Invoices");
		this.manualManipulationRecordMapper.insertManualManipulationRecord(mmr);
	}

	@Transactional
	public void createNonDDPayInvoiceManualManipulationRecord(Date date, ManualManipulationRecord mmr, User user) throws ParseException{
		
		try {
			createNonDDPayPlanInvoice(date);
		} catch (ParseException e) { e.printStackTrace(); }
		
		// RECORDING MANIPULATOR'S DETAILS
		mmr.setAdmin_id(user.getId());
		mmr.setAdmin_name(user.getUser_name());
		mmr.setManipulation_time(new Date());
		mmr.setManipulation_name("Manually Generate Non DDPay Invoices");
		this.manualManipulationRecordMapper.insertManualManipulationRecord(mmr);
	}

	@Transactional
	public void createNonDDPayPlanInvoice(Date date) throws ParseException{

        CustomerOrder customerOrder = new CustomerOrder();
        // only if the order is in using status
        customerOrder.getParams().put("where", "query_no_term");
        customerOrder.getParams().put("order_status", "using");
        
        // using new SimpleDateFormat("yyyy-MM-dd").parse("2014-06-10") under testing environment
		// using new Date() under production environment
        customerOrder.getParams().put("next_invoice_create_date", date);
        customerOrder.getParams().put("order_type", "order-no-term");
        customerOrder.getParams().put("is_ddpay", false);
        customerOrder.getParams().put("order_term_type", "order-term");
        
        // call Service Method
		List<Map<String, Object>> resultMaps = createNextInvoice(customerOrder);
//		if(resultMaps.size()>0){
//			Post2Xero.postMultiInvoices(resultMaps, "Monthly Broadband Charge");
//		}
	}

	@Transactional
	public void createTermPlanInvoice() throws ParseException{
		
		// Production environment should edit as shown below
		// where = query_term
		// status = using
		// order_type = order-term
		// is_ddpay = false
		CustomerOrder coTemp = new CustomerOrder();
		coTemp.getParams().put("where", "query_term");
		coTemp.getParams().put("status", "using");
		coTemp.getParams().put("order_type", "order-term");
		coTemp.getParams().put("is_ddpay", true);
		List<CustomerOrder> cos = this.customerOrderMapper.selectCustomerOrders(coTemp);
		
		// If found any order(s)
		if(cos.size() > 0){
			
			List<Map<String, Object>> resultMaps = new ArrayList<Map<String, Object>>();
			
			boolean isRegenerateInvoice = false;
			boolean is_Next_Invoice = true;
			
			for (CustomerOrder co : cos) {
				
				if("business".equals(co.getCustomer_type())){
					
					Calendar lastMonthMaxDate = Calendar.getInstance();
					lastMonthMaxDate.add(Calendar.MONTH, -1);
					lastMonthMaxDate.set(Calendar.DATE, lastMonthMaxDate.getActualMaximum(Calendar.DATE));
					
					long serviceGiving = co.getOrder_using_start().getTime();
					long lastMonthMaxDateLong = lastMonthMaxDate.getTimeInMillis();
					
					// If Business Service Given Greater Than Last Month's Max Date then Move Forward To Next Order.
					if(serviceGiving > lastMonthMaxDateLong){
						continue;
					}
					
					
					
				}	
				
				Map<String, Object> resultMap = createTermPlanInvoiceByOrder(co, isRegenerateInvoice, is_Next_Invoice);
				if(co.getIs_ddpay()==null || !co.getIs_ddpay()){
					resultMaps.add(resultMap);
				}
				
			}
			
//			if(resultMaps.size()>0){
//				Post2Xero.postMultiInvoices(resultMaps, "Monthly Broadband Charge");
//			}
			
		}
	}

	@Transactional
	public Map<String, Object> createTermPlanInvoiceByOrder(CustomerOrder co
			,boolean isRegenerateInvoice, boolean is_Next_Invoice) throws ParseException{
		
		// BEGIN initial order details
		CustomerOrderDetail codQuery = new CustomerOrderDetail();
		codQuery.getParams().put("order_id", co.getId());
		List<CustomerOrderDetail> cods = this.queryCustomerOrderDetails(codQuery);
		co.setCustomerOrderDetails(cods);
		
		// BEGIN get usable beans
		// Customer
		Customer cQuery = new Customer();
		cQuery.getParams().put("id", co.getCustomer_id());
		Customer c = this.queryCustomer(cQuery);
		boolean isBusiness = "business".toUpperCase().equals(co.getCustomer_type().toUpperCase());
		
		// Current invoice
		CustomerInvoice ci = new CustomerInvoice();
//		System.out.println("isRegenerateInvoice: " + isRegenerateInvoice);
		
		// Previous invoice's preparation
		CustomerInvoice ciPreQuery = new CustomerInvoice();
		ciPreQuery.getParams().put("customer_id", c.getId());
		ciPreQuery.getParams().put("order_id", co.getId());
		// Previous invoice
		CustomerInvoice cpi = null;
		
		if(isRegenerateInvoice){
			CustomerInvoice ciCurQuery = new CustomerInvoice();
			ciCurQuery.getParams().put("where", "by_max_id");
			ciCurQuery.getParams().put("customer_id", c.getId());
			ciCurQuery.getParams().put("order_id", co.getId());
			ci = ciMapper.selectCustomerInvoice(ciCurQuery);
			if(ci != null && !"paid".equals(ci.getStatus())){
				ciPreQuery.getParams().put("where", "by_second_id");
				cpi = ciMapper.selectCustomerInvoice(ciPreQuery);
			} else if(ci != null) {
				ciPreQuery.getParams().put("where", "by_max_id");
				cpi = ciMapper.selectCustomerInvoice(ciPreQuery);
				ci = new CustomerInvoice();
				ci.setCustomer_id(c.getId());
				ci.setOrder_id(co.getId());
				ci.setCreate_date(new Date());
				ci.setDue_date(TMUtils.getInvoiceDueDate(ci.getCreate_date(), 6));
//				System.out.println("due_date_ci_!null: "+ci.getDue_date());
				ciMapper.insertCustomerInvoice(ci);
			} else if(ci == null){
				ci = new CustomerInvoice();
				ci.setCustomer_id(c.getId());
				ci.setOrder_id(co.getId());
				ci.setCreate_date(new Date());
				ci.setDue_date(TMUtils.getInvoiceDueDate(ci.getCreate_date(), 6));
//				System.out.println("due_date_ci_null: "+ci.getDue_date());
				ciMapper.insertCustomerInvoice(ci);
			}
		} else {
			// If Not Regenerate then set next invoice create date
			Date invoiceCreateDay = !is_Next_Invoice 
					? (co.getOrder_using_start() != null 
							? co.getOrder_using_start() 
							: new Date()) 
					: co.getNext_invoice_create_date();
			ciPreQuery.getParams().put("where", "by_max_id");
			cpi = ciMapper.selectCustomerInvoice(ciPreQuery);
			ci = new CustomerInvoice();
			ci.setCustomer_id(c.getId());
			ci.setOrder_id(co.getId());
			ci.setCreate_date(invoiceCreateDay);
			ci.setDue_date(TMUtils.getInvoiceDueDate(invoiceCreateDay, 6));
//			System.out.println("!regenerate: "+ci.getDue_date());
			ciMapper.insertCustomerInvoice(ci);
			// Set id for invoice's update
		}
		if(ci!=null && ci.getId()!=null){
			this.ciDetailMapper.deleteCustomerInvoiceDetailByInvoiceId(ci.getId());
		}
		ci.setAmount_paid(ci.getAmount_paid()!=null ? ci.getAmount_paid() : 0d);
		ci.setAmount_payable(ci.getAmount_payable()!=null ? ci.getAmount_payable() : 0d);
		ci.setBalance(ci.getBalance()!=null ? ci.getBalance() : 0d);
		ci.getParams().put("id", ci.getId());	// For update invoice use
		
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
		Double totalAmountPayable = 0d;
		Double totalCreditBack = 0d;

		List<String> pstn_numbers = new ArrayList<String>();
		List<String> voip_numbers = new ArrayList<String>();
		List<CustomerOrderDetail> pcmsPSTN = new ArrayList<CustomerOrderDetail>();
		List<CustomerOrderDetail> pcmsVoIP = new ArrayList<CustomerOrderDetail>();

		InvoicePDFCreator invoicePDF = new InvoicePDFCreator();
		
		for (CustomerOrderDetail cod : co.getCustomerOrderDetails()) {
			CustomerInvoiceDetail cid = new CustomerInvoiceDetail();


			if("pstn".equals(cod.getDetail_type())){
				
				if((cod.getPstn_number()!=null && !"".equals(cod.getPstn_number().trim()))){
					pstn_numbers.add(cod.getPstn_number());
				}
				
			}

			if("voip".equals(cod.getDetail_type())){
				
				if((cod.getPstn_number()!=null && !"".equals(cod.getPstn_number().trim()))){
					voip_numbers.add(cod.getPstn_number());
				}
				
			}

			if(!isFirst
			&& ("pstn".equals(cod.getDetail_type()) || "voip".equals(cod.getDetail_type()))){
				
				cid.setInvoice_detail_name(cod.getDetail_name());
				cid.setInvoice_detail_unit(cod.getDetail_unit());
				cid.setInvoice_detail_type(cod.getDetail_type());
				cid.setInvoice_detail_price(cod.getDetail_price()!=null ? cod.getDetail_price() : 0d);
				cids.add(cid);
				
				totalAmountPayable = TMUtils.bigAdd(totalAmountPayable, cod.getDetail_price());
				
			}
			
			
			if("termination-credit".equals(cod.getDetail_type())){
				cid.setInvoice_detail_name(cod.getDetail_name());
				cid.setInvoice_detail_unit(cod.getDetail_unit());
				cid.setInvoice_detail_discount(cod.getDetail_price());
				cid.setInvoice_detail_desc(cod.getDetail_desc());
				cid.setInvoice_detail_type(cod.getDetail_type());
				cids.add(cid);
				
				totalCreditBack = TMUtils.bigAdd(totalCreditBack, TMUtils.bigMultiply(cod.getDetail_price()!=null ? cod.getDetail_price() : 0d, cod.getDetail_unit()!=null ? cod.getDetail_unit() : 1));

				continue;
				
			} else if("early-termination-debit".equals(cod.getDetail_type())){
				cid.setInvoice_detail_name(cod.getDetail_name());
				cid.setInvoice_detail_unit(cod.getDetail_unit());
				cid.setInvoice_detail_price(cod.getDetail_price());
				cid.setInvoice_detail_desc(cod.getDetail_desc());
				cid.setInvoice_detail_type(cod.getDetail_type());
				cids.add(cid);

				totalAmountPayable = TMUtils.bigAdd(totalAmountPayable, cod.getDetail_price()!=null ? cod.getDetail_price() : 0d);

				continue;
				
			} else if(cod.getDetail_type()!=null && ("present-calling-minutes".equals(cod.getDetail_type()) || "super-free-calling".equals(cod.getDetail_type()))){
				
				cid.setInvoice_detail_name(cod.getDetail_name());
				cid.setInvoice_detail_price(cod.getDetail_price()!=null ? cod.getDetail_price() : 0d);
				cid.setInvoice_detail_desc(cod.getDetail_calling_minute()+" Minutes");
				cid.setInvoice_detail_unit(1);
				cid.setInvoice_detail_type(cod.getDetail_type());
				cids.add(cid);
				
				if(cod.getDetail_desc().endsWith("voip")){
					
					pcmsVoIP.add(cod);
					
				} else {
					
					pcmsPSTN.add(cod);
					
				}
				
				totalAmountPayable = TMUtils.bigAdd(totalAmountPayable, cod.getDetail_price()!=null ? cod.getDetail_price() : 0d);
				
				continue;
			
			// Static IP
			} else if(cod.getDetail_type()!=null && "static-ip".equals(cod.getDetail_type())){
				
				cid.setInvoice_detail_name(cod.getDetail_name());
				cid.setInvoice_detail_price(cod.getDetail_price()!=null ? cod.getDetail_price() : 0d);
				cid.setInvoice_detail_unit(cod.getDetail_unit());
				cid.setInvoice_detail_type(cod.getDetail_type());
				cids.add(cid);
				
				totalAmountPayable = TMUtils.bigAdd(totalAmountPayable, cod.getDetail_price()!=null ? cod.getDetail_price() : 0d);

			// Fax
			} else if(cod.getDetail_type()!=null && "fax".equals(cod.getDetail_type())){

				cid.setInvoice_detail_name(cod.getDetail_name()+" ( "+cod.getPstn_number()+" )");
				cid.setInvoice_detail_price(cod.getDetail_price()!=null ? cod.getDetail_price() : 0d);
				cid.setInvoice_detail_unit(cod.getDetail_unit());
				cid.setInvoice_detail_type(cod.getDetail_type());
				cids.add(cid);
				
				totalAmountPayable = TMUtils.bigAdd(totalAmountPayable, cod.getDetail_price()!=null ? cod.getDetail_price() : 0d);
				
			}
			
//			System.out.println("isFirst: " + isFirst);
			// If first invoice then add all order details into invoice details
//			System.out.println("----------------------------");
//			System.out.println("isFirst: "+isFirst);
//			System.out.println("----------------------------");
			if(isFirst){
				
				if(cod.getDetail_type()!=null && "plan-term".equals(cod.getDetail_type())){
//					System.out.println("isNotFirst");
//					System.out.println();
					
					if(!isRegenerateInvoice || (isRegenerateInvoice && !"paid".equals(ci.getStatus())) || (isRegenerateInvoice && "paid".equals(cpi != null ? cpi.getStatus() : "paid") && ! (cpi != null ? TMUtils.isSameMonth(cpi.getCreate_date(), new Date()) : false))){

						// Add service given to served month's last day
						Calendar cal = Calendar.getInstance(Locale.CHINA);
						if(isFirst){
							cal.setTime(co.getOrder_using_start());
							cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));
							cid.setInvoice_detail_desc(TMUtils.retrieveMonthAbbrWithDate(co.getOrder_using_start())+" - "+TMUtils.retrieveMonthAbbrWithDate(cal.getTime()));
						}
						
						// Get served month's term plan's remaining charges 
						// Production environment should edit as shown below
						// {new Date()} change to {co.getOrder_using_start()}
						Map<String, Object> resultMap = Calculation4PlanTermInvoice.servedMonthDetails(//new Date()
								co.getOrder_using_start()
								, cod.getDetail_price());
						cid.setInvoice_detail_name(cod.getDetail_name()+" ("+resultMap.get("remainingDays")+" day(s) counting from service start date to end of month)");
						cid.setInvoice_detail_unit(cod.getDetail_unit());
						cid.setInvoice_detail_price((Double)resultMap.get("totalPrice"));
						cid.setInvoice_detail_type(cod.getDetail_type());
						
						// Add remaining price into payable amount
						totalAmountPayable = TMUtils.bigAdd(totalAmountPayable, (Double)resultMap.get("totalPrice"));
						cids.add(cid);
						
						// If is not served in current month, then add plan-term type detail into invoice detail
						if (!isServedCurrentMonth) {
							
							// Reconstruct invoice's detail
							cid = new CustomerInvoiceDetail();
							cid.setInvoice_detail_name(cod.getDetail_name());
							cid.setInvoice_detail_unit(cod.getDetail_unit());
							cid.setInvoice_detail_price(cod.getDetail_price());
							cid.setInvoice_detail_type(cod.getDetail_type());
							
							// Add first day to last day
							cal = Calendar.getInstance(Locale.CHINA);
							cal.set(Calendar.DAY_OF_MONTH, 1);
							Date firstDay = cal.getTime();
							cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));
							Date lastDay = cal.getTime();
							cid.setInvoice_detail_desc(TMUtils.retrieveMonthAbbrWithDate(firstDay)+" - "+TMUtils.retrieveMonthAbbrWithDate(lastDay));
							
							// Add monthly price into payable amount
							totalAmountPayable = TMUtils.bigAdd(totalAmountPayable, cod.getDetail_price());
							cids.add(cid);
						}
					}
					
//					System.out.println("cod.getDetail_type(): "+cod.getDetail_type());
//					System.out.println("\"plan-term\".equals(cod.getDetail_type()): "+"plan-term".equals(cod.getDetail_type()));
//					System.out.println("!isRegenerateInvoice: "+!isRegenerateInvoice);
					
					if (cod.getDetail_type()!=null && "plan-term".equals(cod.getDetail_type()) && !isRegenerateInvoice && (co.getIs_ddpay()!=null && co.getIs_ddpay()==true)) {
						
						// if is next invoice then plus one month else plus unit month(s)
						int nextInvoiceMonth = 1;
						int nextInvoiceDay = -7;
						Calendar calNextInvoiceDay = Calendar.getInstance();
						
//						System.out.println("--------------------------------------");
//						System.out.println("calNextInvoiceDay: "+calNextInvoiceDay);
//						System.out.println("--------------------------------------");
						calNextInvoiceDay.set(Calendar.DAY_OF_MONTH, 14);
						
//						System.out.println("--------------------------------------");
//						System.out.println("calNextInvoiceDay: "+calNextInvoiceDay);
//						System.out.println("--------------------------------------");
						calNextInvoiceDay.add(Calendar.MONTH, nextInvoiceMonth);
						
//						System.out.println("--------------------------------------");
//						System.out.println("calNextInvoiceDay: "+calNextInvoiceDay);
//						System.out.println("--------------------------------------");
						// update customer order's next invoice create day flag begin
						co.setNext_invoice_create_date_flag(calNextInvoiceDay.getTime());
						// update customer order's next invoice create day flag end

						calNextInvoiceDay.add(Calendar.DAY_OF_MONTH, nextInvoiceDay);
						
//						System.out.println("--------------------------------------");
//						System.out.println("calNextInvoiceDay: "+calNextInvoiceDay);
//						System.out.println("--------------------------------------");
						// update customer order's next invoice create day begin
						co.setNext_invoice_create_date(calNextInvoiceDay.getTime());
						// update customer order's next invoice create day end
						
						co.getParams().put("id", co.getId());
						
						this.customerOrderMapper.updateCustomerOrder(co);
						
					}
					
					continue;

				// Else if discount and unexpired then do add discount
				} else if(cod.getDetail_type()!=null && "discount".equals(cod.getDetail_type()) && cod.getDetail_expired() != null && cod.getDetail_expired().getTime() >= System.currentTimeMillis()){
					
					cid.setInvoice_detail_name(cod.getDetail_name());
					cid.setInvoice_detail_discount(cod.getDetail_price());
					cid.setInvoice_detail_unit(cod.getDetail_unit());
					cid.setInvoice_detail_type(cod.getDetail_type());
					// totalCreditBack add ( discount price times discount unit )
					totalCreditBack = TMUtils.bigAdd(totalCreditBack, TMUtils.bigMultiply(cod.getDetail_price(), cod.getDetail_unit()));
					cids.add(cid);
					
					continue;
					
				// Else add all non plan-term, discount, termination-credit, early-termination-debit type details into invoice details
				} else if(cod.getDetail_type()!=null && !"discount".equals(cod.getDetail_type())
						  && !"termination-credit".equals(cod.getDetail_type())
						  && !"early-termination-debit".equals(cod.getDetail_type())
						  && !"present-calling-minutes".equals(cod.getDetail_type())
						  && !"super-free-calling".equals(cod.getDetail_type())) {

					cid.setInvoice_detail_name(cod.getDetail_name());
					cid.setInvoice_detail_price(cod.getDetail_price());
					cid.setInvoice_detail_unit(cod.getDetail_unit());
					cid.setInvoice_detail_type(cod.getDetail_type());
					
					// Payable amount plus ( detail price times detail unit )
					totalAmountPayable = TMUtils.bigAdd(totalAmountPayable, TMUtils.bigMultiply(cod.getDetail_price(), cod.getDetail_unit()));
					cids.add(cid);
					
					continue;
				}
				
			// Else not first invoice, add unexpired order detail(s) into invoice detail(s)
			} else {
				
				// Add previous invoice's details into
				ci.setLast_invoice_id(cpi.getId());
				ci.setLastCustomerInvoice(cpi);
				
				Double pciPayable = 0d;
				Double pciFinalPayable = 0d;
				Double pciPaid = 0d;
				Double pciBalance = 0d;
				
				CustomerInvoice pciQuery = new CustomerInvoice();
				pciQuery.getParams().put("order_id", co.getId());
				pciQuery.getParams().put("status_in_unpaid_overdue_not_pay_off", "true");
				pciQuery.getParams().put("id_not_in", ci.getId());
				List<CustomerInvoice> pcisQuery = this.queryCustomerInvoices(pciQuery);
				for (CustomerInvoice pci : pcisQuery) {
					System.out.println("Payable: "+pci.getAmount_payable()+", Final Payable: "+pci.getFinal_payable_amount()+", Paid: "+pci.getAmount_paid()+", Balance: "+pci.getBalance());
					pciPayable = TMUtils.bigAdd(pciPayable, pci.getAmount_payable()!=null ? pci.getAmount_payable() : 0d);
					pciFinalPayable = TMUtils.bigAdd(pciFinalPayable, pci.getFinal_payable_amount()!=null ? pci.getFinal_payable_amount() : 0d);
					pciPaid = TMUtils.bigAdd(pciPaid, pci.getAmount_paid()!=null ? pci.getAmount_paid() : 0d);
					pciBalance = TMUtils.bigAdd(pciBalance, pci.getBalance()!=null ? pci.getBalance() : 0d);
				}
				CustomerInvoice cpiStatistic = new CustomerInvoice();
				invoicePDF.setLastCustomerInvoiceStatistic(cpiStatistic);
				cpiStatistic.setAmount_payable(pciPayable);
				cpiStatistic.setFinal_payable_amount(pciFinalPayable);
				cpiStatistic.setAmount_paid(pciPaid);
				cpiStatistic.setBalance(pciBalance);
				

				// If plan-term type, then add detail into invoice detail
				if("plan-term".equals(cod.getDetail_type())){
//					System.out.println("!isRegenerateInvoice: "+!isRegenerateInvoice);
//					System.out.println("isRegenerateInvoice && \"paid\".equals(cpi.getStatus()) && TMUtils.isSameMonth(cpi.getCreate_date(), new Date()): "+(isRegenerateInvoice && "paid".equals(cpi.getStatus()) && TMUtils.isSameMonth(cpi.getCreate_date(), new Date())));
					if(!isRegenerateInvoice || (isRegenerateInvoice && !"paid".equals(ci.getStatus())) || (isRegenerateInvoice && "paid".equals(cpi != null ? cpi.getStatus() : "paid") && ! (cpi != null ? TMUtils.isSameMonth(cpi.getCreate_date(), new Date()) : false))){

						// If contains calling-only then this is a calling record invoice
						if(ci.getMemo()!=null && ci.getMemo().contains("calling-only")){
							continue;
						}
						
						Calendar cal = Calendar.getInstance();
						Date startFrom = null;
						Date endTo = null;
						
						// If is old order than use old logic, service from 1th to last day of the month.
						if((co.getOrder_serial()!=null && co.getOrder_serial().contains("old"))
						  || co.getCustomer_type().equals("business") || (co.getIs_ddpay()!=null && co.getIs_ddpay()==true)){
							// Add first day as begin date and last day as end date
							cal.set(Calendar.DAY_OF_MONTH, 1);
							startFrom = cal.getTime();
							cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));
							endTo = cal.getTime();
						} else {
							// If second or greater invoices then assign begin date to next_invoice_create_date_flag
							if(is_Next_Invoice){
								cal.setTime(co.getNext_invoice_create_date_flag());
							// Else first invoice then assign begin date to next_invoice_create_date_flag - 1
							} else {
								cal.setTime(co.getNext_invoice_create_date_flag());
								cal.add(Calendar.MONTH, -1);
							}
							// Assign calculated date to begin date
							startFrom = cal.getTime();
							// If unit not null then add unit month(s), else add 1 month
							cal.add(Calendar.MONTH, 1);
							// Minus 1 day
							cal.add(Calendar.DAY_OF_MONTH, -1);
							endTo = cal.getTime();
						}
						
						
						cid.setInvoice_detail_desc(TMUtils.retrieveMonthAbbrWithDate(startFrom)+" - "+TMUtils.retrieveMonthAbbrWithDate(endTo));
						
						cid.setInvoice_detail_name(cod.getDetail_name());
						cid.setInvoice_detail_unit(cod.getDetail_unit());
						cid.setInvoice_detail_price(cod.getDetail_price());
						cid.setInvoice_detail_type(cod.getDetail_type());
						
						// Add monthly price into payable amount
						totalAmountPayable = TMUtils.bigAdd(totalAmountPayable, cod.getDetail_price());
						cids.add(cid);
					}
					
					// If is not regenerate invoice then update next invoice create data & flag
					if (!isRegenerateInvoice) {
						
						// if is next invoice then plus one month else plus unit month(s)
						int nextInvoiceMonth = 1;
						int nextInvoiceDay = -7;
						Calendar calNextInvoiceDay = Calendar.getInstance();
						calNextInvoiceDay.setTime(co.getNext_invoice_create_date_flag());
						calNextInvoiceDay.add(Calendar.MONTH, nextInvoiceMonth);
						// update customer order's next invoice create day flag begin
						co.setNext_invoice_create_date_flag(calNextInvoiceDay.getTime());
						// update customer order's next invoice create day flag end

						calNextInvoiceDay.add(Calendar.DAY_OF_MONTH, nextInvoiceDay);
						// update customer order's next invoice create day begin
						co.setNext_invoice_create_date(calNextInvoiceDay.getTime());
						// update customer order's next invoice create day end
						
						co.getParams().put("id", co.getId());
						
						this.customerOrderMapper.updateCustomerOrder(co);
					}

				// Else if discount and unexpired then do add discount
				} else if(cod.getDetail_type()!=null && "discount".equals(cod.getDetail_type()) && cod.getDetail_expired() != null && cod.getDetail_expired().getTime() >= System.currentTimeMillis()){
					
					cid.setInvoice_detail_name(cod.getDetail_name());
					cid.setInvoice_detail_discount(cod.getDetail_price());
					cid.setInvoice_detail_unit(cod.getDetail_unit());
					cid.setInvoice_detail_type(cod.getDetail_type());

					// totalCreditBack add ( discount price times discount unit )
					totalCreditBack = TMUtils.bigAdd(totalCreditBack, TMUtils.bigMultiply(cod.getDetail_price(), cod.getDetail_unit()));
					cids.add(cid);

				// Else if unexpired then add order detail(s) into invoice detail(s)
				} else if(cod.getDetail_expired() != null && cod.getDetail_expired().getTime() >= System.currentTimeMillis()) {

					cid.setInvoice_detail_name(cod.getDetail_name());
					cid.setInvoice_detail_price(cod.getDetail_price());
					cid.setInvoice_detail_unit(cod.getDetail_unit());
					cid.setInvoice_detail_type(cod.getDetail_type());

					// Payable amount plus ( detail price times detail unit )
					totalAmountPayable = TMUtils.bigAdd(totalAmountPayable, TMUtils.bigMultiply(cod.getDetail_price(), cod.getDetail_unit()));
					cids.add(cid);
				}
				
			}
		}

		// Automatically assign previous invoice's prepayment to current invoice if have
		if(!isFirst){
			// If previous invoice's balance less than zero
			if(cpi.getBalance()<0){
				
				CustomerInvoice cpiUpdate = new CustomerInvoice();
				
				// previous final payable < 0 && previous paid == 0 ? (totalCreditBack = totalCreditBack + abs(previous balance), previous final payable = 0d), prepayment is credit
				if(cpi.getFinal_payable_amount() < 0 && cpi.getAmount_paid()==0){
					totalCreditBack = TMUtils.bigAdd(totalCreditBack, Math.abs(cpi.getBalance()));
					
					cpiUpdate.setFinal_payable_amount(0d);
					
				// (previous paid > previous final payable) && (abs(previous balance) == previous paid - previous final payable) && previous final payable > 0 ? (current paid = current paid + abs(previous balance), previous paid = previous final payable), prepayment is paid
				} else if(cpi.getAmount_paid() > cpi.getFinal_payable_amount()
				  && Math.abs(cpi.getBalance()) == TMUtils.bigSub(cpi.getAmount_paid(), cpi.getFinal_payable_amount())
				  && cpi.getFinal_payable_amount() > 0){
					ci.setAmount_paid(TMUtils.bigAdd(ci.getAmount_paid(), Math.abs(cpi.getBalance())));

					cpiUpdate.setAmount_paid(cpi.getFinal_payable_amount());
					
				// (previous paid > previous final payable) && (abs(previous balance) > previous paid - previous final payable) ? (current paid = current paid + (abs(previous balance) - abs(previous final payable)), totalCreditBack = (totalCreditBack + (abs(previous balance) - previous paid))), prepayment is credit&paid 
				} else if(cpi.getAmount_paid() > cpi.getFinal_payable_amount()
				 && Math.abs(cpi.getBalance()) >= TMUtils.bigSub(cpi.getAmount_paid(), cpi.getFinal_payable_amount())){
					// ciPaid = ciPaid + (abs(cpiBalance) - abs(cpiFinalPayable))
					ci.setAmount_paid(TMUtils.bigAdd(ci.getAmount_paid(), TMUtils.bigSub(Math.abs(cpi.getBalance()), Math.abs(cpi.getFinal_payable_amount()))));
					// totalCreditBack = totalCreditBack + (abs(cpiBalance) - (cpiPaid - cpiFinalPayable))
					totalCreditBack = TMUtils.bigAdd(totalCreditBack, TMUtils.bigSub(Math.abs(cpi.getBalance()), cpi.getAmount_paid()));
					
					cpiUpdate.setAmount_paid(0d);
					cpiUpdate.setFinal_payable_amount(0d);
					
				}
				
				cpiUpdate.setBalance(0d);
				cpiUpdate.getParams().put("id", cpi.getId());
				cpiUpdate.setStatus("paid");
				this.ciMapper.updateCustomerInvoice(cpiUpdate);
				
			}
		}

		// store company detail begin
		CompanyDetail companyDetail = companyDetailMapper.selectCompanyDetail();
		// store company detail end
		
		invoicePDF.setCompanyDetail(companyDetail);

	

		invoicePDF.setCo(co);

		ci.setOrder_id(co.getId());

		
		if(pstn_numbers.size() > 0){
			
			for (String pstn_number : pstn_numbers) {
				
				totalAmountPayable = CallingAndRentalFeeCalucation.ccrRentalOperation(ci, isRegenerateInvoice, pstn_number, cids, totalAmountPayable, customerCallRecordMapper, this.ciMapper, this.customerOrderChorusAddonMapper);
				
				totalAmountPayable = CallingAndRentalFeeCalucation.ccrOperation(ci, isRegenerateInvoice, pcmsPSTN, pstn_number, cids, invoicePDF, totalAmountPayable, this.customerCallRecordMapper, this.callInternationalRateMapper, this.customerCallingRecordCallplusMapper, this.ciMapper, co.getCustomer_type(), this.customerOrderNZCallingRatesSettingMapper, co.getId());
				
			}
		}

		if(voip_numbers.size() > 0){
			
			for (String voip_number : voip_numbers) {
				
				totalAmountPayable = CallingAndRentalFeeCalucation.voipCallOperation(ci, isRegenerateInvoice, pcmsVoIP, voip_number, cids, invoicePDF, totalAmountPayable, nzAreaCodeListMapper, vosVoIPRateMapper, vosVoIPCallRecordMapper, ciMapper, co.getCustomer_type(), this.customerOrderNZCallingRatesSettingMapper, co.getId());
				
			}
		}
		
		// truncate unnecessary reminders, left only two reminders, e.g. 1.0001 change to 1.00
		totalCreditBack = Double.parseDouble(TMUtils.fillDecimalPeriod(totalCreditBack));
		totalAmountPayable = isBusiness ? TMUtils.bigMultiply(totalAmountPayable, 1.15) : totalAmountPayable;
		
		// If previous balance greater than 0 and customer_type equals to business
//		if((cpi!=null && cpi.getBalance()>0) && "business".equals(co.getCustomer_type())){
//			totalAmountPayable = TMUtils.bigAdd(totalAmountPayable, cpi.getBalance());
//		}
		
//		System.out.println("Pre ci: ");
//		System.out.println("payable: "+ci.getAmount_payable()+", balance: "+ci.getBalance());
//		System.out.println();
		
		// Set current invoice's payable amount
		ci.setAmount_payable(totalAmountPayable);
		ci.setFinal_payable_amount(TMUtils.bigSub(totalAmountPayable, totalCreditBack));
		ci.setAmount_paid(ci.getAmount_paid() == null ? 0d : ci.getAmount_paid());
		ci.setBalance(TMUtils.bigOperationTwoReminders(ci.getFinal_payable_amount(), ci.getAmount_paid(), "sub"));
		
//		System.out.println("Post ci: ");
//		System.out.println("payable: "+ci.getAmount_payable()+", balance: "+ci.getBalance());
		
		// If balance greater than 0
		if(ci.getBalance()>0){
			// If customer account credit greater than 0, then use account credit to defray invoice balance 
			if(c.getBalance()!=null && c.getBalance()>0){
				
				Customer cUpdate = new Customer();
				CustomerTransaction ct = new CustomerTransaction();
				
				// If customer account credit greater than balance
				if(c.getBalance() >= ci.getBalance()){
					
					ct.setAmount(ci.getBalance());
					ct.setAmount_settlement(ci.getBalance());
					ct.setCard_name("account-credit");
					ct.setCustomer_id(c.getId());
					ct.setOrder_id(co.getId());
					ct.setInvoice_id(ci.getId());
					ct.setTransaction_date(new Date());
					ct.setCurrency_input("NZD");
					
					cUpdate.setBalance(TMUtils.bigSub(c.getBalance(), ci.getBalance()));
					ci.setAmount_paid(ci.getFinal_payable_amount());
					ci.setBalance(0d);
				// Else customer account credit less than balance
				} else {
					
					ct.setAmount(c.getBalance());
					ct.setAmount_settlement(c.getBalance());
					ct.setCard_name("account-credit");
					ct.setCustomer_id(c.getId());
					ct.setOrder_id(co.getId());
					ct.setInvoice_id(ci.getId());
					ct.setTransaction_date(new Date());
					ct.setCurrency_input("NZD");
					
					ci.setBalance(TMUtils.bigSub(ci.getBalance(), c.getBalance()));
					ci.setAmount_paid(TMUtils.bigAdd(ci.getAmount_paid(), c.getBalance()));
					cUpdate.setBalance(0d);
				}
				cUpdate.getParams().put("id", c.getId());
				this.customerMapper.updateCustomer(cUpdate);
				this.customerTransactionMapper.insertCustomerTransaction(ct);
			}
		}
		
		if(ci.getBalance()<=0){
			ci.setStatus("paid");
			
		// Else paid some fee
		} else if(ci.getAmount_paid()!=null && ci.getAmount_paid()>0){
			ci.setStatus("not_pay_off");
		} else {
			ci.setStatus("unpaid");
		}
		
		// Set invoice details into 
		ci.setCustomerInvoiceDetails(cids);

		// Iteratively inserting invoice detail(s) into tm_invoice_detail table
		for (CustomerInvoiceDetail cid : cids) {
			cid.setInvoice_id(ci.getId());
			ciDetailMapper.insertCustomerInvoiceDetail(cid);
		}

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
		ci.setInvoice_type("general-invoice");
		ciMapper.updateCustomerInvoice(ci);

		// Deleting repeated invoices
//		ciMapper.deleteCustomerInvoiceByRepeat();
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("customer", c);
		resultMap.put("customerInvoice", ci);
		resultMap.put("customerOrder", co);
		
		return resultMap;
		
	}
	
	
	@Transactional
	public Map<String, Object> createInvoicePDFBoth(CustomerOrder customerOrder
			, Notification notificationEmailFinal
			, Notification notificationSMSFinal, Boolean is_Next_Invoice, boolean isRegenerate){
		// store company detail begin
		CompanyDetail companyDetail = this.companyDetailMapper.selectCompanyDetail();
		// store company detail end
		
		CustomerOrderDetail codQuery = new CustomerOrderDetail();
		codQuery.getParams().put("order_id", customerOrder.getId());
		List<CustomerOrderDetail> cods = this.queryCustomerOrderDetails(codQuery);
		customerOrder.setCustomerOrderDetails(cods);
		
		Customer cQuery = new Customer();
		cQuery.getParams().put("id", customerOrder.getCustomer_id());
		Customer customer = this.queryCustomer(cQuery);

		boolean isBusiness = "business".toUpperCase().equals(customerOrder.getCustomer_type().toUpperCase());
		
		// current invoice model
		CustomerInvoice ci = new CustomerInvoice();
		CustomerInvoice ciCurQuery = new CustomerInvoice();
		ciCurQuery.getParams().put("customer_id", customer.getId());
		ciCurQuery.getParams().put("order_id", customerOrder.getId());
		// previous invoice model
		CustomerInvoice cpi = new CustomerInvoice();
		CustomerInvoice ciPreQuery = new CustomerInvoice();
		ciPreQuery.getParams().put("customer_id", customer.getId());
		ciPreQuery.getParams().put("order_id", customerOrder.getId());
		
		boolean isMostRecentInvoicePaid = false;
		
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
			ci.setDue_date(TMUtils.getInvoiceDueDate(invoiceCreateDay, 6));
			ci.setStatus("unpaid");
			// If Not Regenerate then get most recent invoice as previous invoice
			ciPreQuery.getParams().put("where", "by_max_id");
			cpi = this.ciMapper.selectCustomerInvoice(ciPreQuery);
			this.ciMapper.insertCustomerInvoice(ci);
		} else {
			// If Regenerate then get most recent invoice as current invoice
			ciCurQuery.getParams().put("where", "by_max_id");
			ci = this.ciMapper.selectCustomerInvoice(ciCurQuery);
			// If Regenerate then get before than most recent invoice as previous invoice
			if(ci!=null && !"paid".equals(ci.getStatus())){
				ciPreQuery.getParams().put("where", "by_second_id");
				cpi = this.ciMapper.selectCustomerInvoice(ciPreQuery);
			} else if(ci!=null) {
				ciPreQuery.getParams().put("where", "by_max_id");
				cpi = this.ciMapper.selectCustomerInvoice(ciPreQuery);
				isMostRecentInvoicePaid = true;
				ci = new CustomerInvoice();
				ci.setCustomer_id(customer.getId());
				ci.setOrder_id(customerOrder.getId());
				ci.setCreate_date(ci.getCreate_date()!=null ? ci.getCreate_date() : new Date());
				ci.setDue_date(ci.getDue_date()!=null ? ci.getDue_date() : TMUtils.getInvoiceDueDate(ci.getCreate_date(), 6));
				this.ciMapper.insertCustomerInvoice(ci);
			} else if(ci==null) {
				ci = new CustomerInvoice();
				ci.setCustomer_id(customer.getId());
				ci.setOrder_id(customerOrder.getId());
				ci.setCreate_date(ci.getCreate_date()!=null ? ci.getCreate_date() : new Date());
				ci.setDue_date(ci.getDue_date()!=null ? ci.getDue_date() : TMUtils.getInvoiceDueDate(ci.getCreate_date(), 6));
				this.ciMapper.insertCustomerInvoice(ci);
			}
		}
		if(ci!=null && ci.getId()!=null){
			this.ciDetailMapper.deleteCustomerInvoiceDetailByInvoiceId(ci.getId());
		}
		// Customer previous invoice
		if (cpi != null && !ci.getId().equals(cpi.getId())) {
			ci.setLast_invoice_id(cpi.getId());
			
			CustomerInvoice preCIQuery = new CustomerInvoice();
			preCIQuery.getParams().put("order_id", customerOrder.getId());
			List<CustomerInvoice> preCIsQuery = this.ciMapper.selectCustomerInvoices(preCIQuery);
			
			Double preCIPayable = 0d;
			Double preCIPaid = 0d;
			Double preCIBalance = 0d;
			for (CustomerInvoice preCITemp : preCIsQuery)
			{
				preCIPayable = TMUtils.bigAdd(preCIPayable, preCITemp.getAmount_payable()!=null ? preCITemp.getAmount_payable() : 0d);
				preCIPaid = TMUtils.bigAdd(preCIPaid, preCITemp.getAmount_paid()!=null ? preCITemp.getAmount_paid() : 0d);
				preCIBalance = TMUtils.bigAdd(preCIBalance, preCITemp.getBalance()!=null ? preCITemp.getBalance() : 0d);
			}
			
			cpi.setAmount_payable(preCIPayable);
			cpi.setAmount_paid(preCIPaid);
			cpi.setBalance(preCIBalance);
			
			ci.setLastCustomerInvoice(cpi);
		} else {
			cpi = null;
		}
		boolean isFirst = cpi==null ? true : false;
//		System.out.println("isFirst: "+isFirst);
		
		// detail holder begin
		List<CustomerInvoiceDetail> cids = new ArrayList<CustomerInvoiceDetail>();
		// detail holder end
		List<CustomerOrderDetail> customerOrderDetails = customerOrder.getCustomerOrderDetails();

		Double totalAmountPayable = 0d;
		Double totalCreditBack = 0d;
		
		List<String> pstn_numbers = new ArrayList<String>();
		List<String> voip_numbers = new ArrayList<String>();
		List<CustomerOrderDetail> pcmsPSTN = new ArrayList<CustomerOrderDetail>();
		List<CustomerOrderDetail> pcmsVoIP = new ArrayList<CustomerOrderDetail>();
		InvoicePDFCreator invoicePDF = new InvoicePDFCreator();
		
		for (CustomerOrderDetail cod: customerOrderDetails) {

			if(cod.getDetail_type()!=null && "pstn".equals(cod.getDetail_type())){
				
				if((cod.getPstn_number()!=null && !"".equals(cod.getPstn_number().trim()))){
					pstn_numbers.add(cod.getPstn_number());
				}
				
			}

			if(cod.getDetail_type()!=null && "voip".equals(cod.getDetail_type())){
				
				if((cod.getPstn_number()!=null && !"".equals(cod.getPstn_number().trim()))){
					voip_numbers.add(cod.getPstn_number());
				}
				
			}
			
			CustomerInvoiceDetail cid = new CustomerInvoiceDetail();
			cid.setInvoice_id(ci.getId());
			cid.setInvoice_detail_name(cod.getDetail_name());
			cid.setInvoice_detail_unit(cod.getDetail_unit() == null ? 1 : cod.getDetail_unit());
			cid.setInvoice_detail_type(cod.getDetail_type());

			if(!isFirst
			&& cod.getDetail_type()!=null && ("pstn".equals(cod.getDetail_type()) || "voip".equals(cod.getDetail_type()))){

				cid.setInvoice_detail_name(cod.getDetail_name());
				cid.setInvoice_detail_unit(cod.getDetail_unit());
				cid.setInvoice_detail_type(cod.getDetail_type());
				cid.setInvoice_detail_price(cod.getDetail_price()!=null ? cod.getDetail_price() : 0d);
				cids.add(cid);

				totalAmountPayable = TMUtils.bigAdd(totalAmountPayable, cod.getDetail_price()!=null ? cod.getDetail_price() : 0d);
				
			}

			// if detail type equals discount and detail expire date greater
			// equal than today's date then go into if statement
			if ((cod.getDetail_type()!=null && "discount".equals(cod.getDetail_type()))
					&& (cod.getDetail_expired()!=null && cod.getDetail_expired().getTime() >= System.currentTimeMillis())) {
				
				cid.setInvoice_detail_discount(cod.getDetail_price());
				cid.setInvoice_detail_type(cod.getDetail_type());
				cid.setInvoice_detail_unit(cod.getDetail_unit());
				// decrease amountPayable
				totalCreditBack = TMUtils.bigAdd(totalCreditBack, TMUtils.bigMultiply(cid.getInvoice_detail_discount()!=null ? cid.getInvoice_detail_discount() : 0d, cid.getInvoice_detail_unit()!=null ? cid.getInvoice_detail_unit() : 1));
				// add invoice detail to list
				cids.add(cid);
				// else if detail type is discount then this discount is expired
				// and will not be add to the invoice detail list
			} else if (cod.getDetail_type()!=null && !"discount".equals(cod.getDetail_type())) {
				if(cod.getDetail_type()!=null && "termination-credit".equals(cod.getDetail_type())){
					cid.setInvoice_detail_name(cod.getDetail_name());
					cid.setInvoice_detail_unit(cod.getDetail_unit());
					cid.setInvoice_detail_discount(cod.getDetail_price());
					cid.setInvoice_detail_desc(cod.getDetail_desc());
					cid.setInvoice_detail_type(cod.getDetail_type());
					cids.add(cid);
					totalCreditBack = TMUtils.bigAdd(totalCreditBack, TMUtils.bigMultiply(cod.getDetail_price(), cod.getDetail_unit()));
				} else if(cod.getDetail_type()!=null && ("present-calling-minutes".equals(cod.getDetail_type()) || "super-free-calling".equals(cod.getDetail_type()))){
					cid.setInvoice_detail_name(cod.getDetail_name());
					cid.setInvoice_detail_price(cod.getDetail_price()!=null ? cod.getDetail_price() : 0d);
					cid.setInvoice_detail_desc(cod.getDetail_calling_minute()+" Minutes");
					cid.setInvoice_detail_unit(cod.getDetail_unit());
					cid.setInvoice_detail_type(cod.getDetail_type());
					cids.add(cid);
					
					if(cod.getDetail_desc()!=null && cod.getDetail_desc().endsWith("voip")){
						
						pcmsVoIP.add(cod);
						
					} else {
						
						pcmsPSTN.add(cod);
						
					}
					
					totalAmountPayable = TMUtils.bigAdd(totalAmountPayable, cod.getDetail_price()!=null ? cod.getDetail_price() : 0d);
				
				// Static IP
				} else if(cod.getDetail_type()!=null && "static-ip".equals(cod.getDetail_type())){
					cid.setInvoice_detail_name(cod.getDetail_name());
					cid.setInvoice_detail_price(cod.getDetail_price()!=null ? cod.getDetail_price() : 0d);
					cid.setInvoice_detail_unit(cod.getDetail_unit());
					cid.setInvoice_detail_type(cod.getDetail_type());
					cids.add(cid);
					
					totalAmountPayable = TMUtils.bigAdd(totalAmountPayable, cod.getDetail_price()!=null ? cod.getDetail_price() : 0d);
				
				// Fax
				} else if(cod.getDetail_type()!=null && "fax".equals(cod.getDetail_type())){
					cid.setInvoice_detail_name(cod.getDetail_name()+" ( "+cod.getPstn_number()+" )");
					cid.setInvoice_detail_price(cod.getDetail_price()!=null ? cod.getDetail_price() : 0d);
					cid.setInvoice_detail_unit(cod.getDetail_unit());
					cid.setInvoice_detail_type(cod.getDetail_type());
					cids.add(cid);
					
					totalAmountPayable = TMUtils.bigAdd(totalAmountPayable, cod.getDetail_price()!=null ? cod.getDetail_price() : 0d);
				
				// Termed & No Termed
				} else if(!isMostRecentInvoicePaid && cod.getDetail_type()!=null && cod.getDetail_type().contains("plan-") && !"plan-topup".equals(cod.getDetail_type())){

					// If contains calling-only then this is a calling record invoice
					if(ci.getMemo()!=null && ci.getMemo().contains("calling-only")){
						continue;
					}
					
					Calendar cal = Calendar.getInstance();
					Date endTo = null;
					Date startFrom = null;
					if(isFirst){
						cal.setTime(customerOrder.getOrder_using_start());
						cal.add(Calendar.MONTH, cod.getDetail_unit());
						cal.add(Calendar.DAY_OF_MONTH, -1);
						startFrom = customerOrder.getOrder_using_start();
						endTo = cal.getTime();
						
					} else {
						
						// If is old order than use old logic, service from 1th to last day of the month.
						if((customerOrder.getOrder_serial()!=null && customerOrder.getOrder_serial().contains("old"))
						  || "business".equals(customerOrder.getCustomer_type())){
							// Add first day to last day
							cal.set(Calendar.DAY_OF_MONTH, 1);
							startFrom = cal.getTime();
							cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));
							endTo = cal.getTime();
						} else {
							if(is_Next_Invoice){
								cal.setTime(customerOrder.getNext_invoice_create_date_flag());
							} else {
								cal.setTime(customerOrder.getNext_invoice_create_date_flag());
								cal.add(Calendar.MONTH, -1);
							}
							startFrom = cal.getTime();
							cal.add(Calendar.MONTH,  1);
							cal.add(Calendar.DAY_OF_MONTH, -1);
							endTo = cal.getTime();
						}
					}
					
					cid.setInvoice_detail_desc(TMUtils.retrieveMonthAbbrWithDate(startFrom)+" - "+TMUtils.retrieveMonthAbbrWithDate(endTo));
					
					// if is first invoice and unit isn't null then assigned from unit, otherwise assign to 1
					cid.setInvoice_detail_unit(cod.getDetail_unit() != null && !is_Next_Invoice && isFirst ? cod.getDetail_unit() : 1);
					cid.setInvoice_detail_price(cod.getDetail_price() == null ? 0d : cod.getDetail_price());
					cid.setInvoice_detail_type(cod.getDetail_type());
					// increase amountPayable
					totalAmountPayable =  cid.getInvoice_detail_price() != null ? TMUtils.bigAdd(totalAmountPayable, TMUtils.bigMultiply(cid.getInvoice_detail_price(), cid.getInvoice_detail_unit())) : 0;
					// add invoice detail to list
					
					cids.add(cid);
				
				// Topup Plan
//				} else if(!isMostRecentInvoicePaid && cod.getDetail_type()!=null && cod.getDetail_type().contains("plan-") && "plan-topup".equals(cod.getDetail_type())){
//					
//					// if is first invoice and unit isn't null then assigned from unit, otherwise assign to 1
//					cid.setInvoice_detail_unit(cod.getDetail_unit() != null && !is_Next_Invoice && isFirst ? cod.getDetail_unit() : 1);
//					cid.setInvoice_detail_price(cod.getDetail_price() == null ? 0d : cod.getDetail_price());
//					// increase amountPayable
//					totalAmountPayable =  cid.getInvoice_detail_price() != null ? TMUtils.bigAdd(totalAmountPayable, TMUtils.bigMultiply(cid.getInvoice_detail_price(), cid.getInvoice_detail_unit())) : 0;
//					// add invoice detail to list
//					cids.add(cid);
				} else if(!isMostRecentInvoicePaid && !is_Next_Invoice && isFirst){
					// if is first invoice and unit isn't null then assigned from unit, otherwise assign to 1
					cid.setInvoice_detail_unit(cod.getDetail_unit() != null && !is_Next_Invoice && isFirst ? cod.getDetail_unit() : 1);
					cid.setInvoice_detail_price(cod.getDetail_price() == null ? 0d : cod.getDetail_price());
					cid.setInvoice_detail_type(cod.getDetail_type());
					// increase amountPayable
					totalAmountPayable =  cid.getInvoice_detail_price() != null ? TMUtils.bigAdd(totalAmountPayable, TMUtils.bigMultiply(cid.getInvoice_detail_price(), cid.getInvoice_detail_unit())) : 0;
					// add invoice detail to list
					cids.add(cid);
				} else if((cod.getDetail_expired()!=null && cod.getDetail_expired().getTime() >= System.currentTimeMillis())
						|| cod.getDetail_type().contains("plan-")){
					// if is first invoice and unit isn't null then assigned from unit, otherwise assign to 1
					if(!isFirst){
						cid.setInvoice_detail_unit(1);
					} else {
						cid.setInvoice_detail_unit(cod.getDetail_unit() != null ? cod.getDetail_unit() : 1);
					}
					cid.setInvoice_detail_price(cod.getDetail_price() == null ? 0d : cod.getDetail_price());
					cid.setInvoice_detail_type(cod.getDetail_type());
					// increase amountPayable
					totalAmountPayable =  cid.getInvoice_detail_price() != null ? TMUtils.bigAdd(totalAmountPayable, TMUtils.bigMultiply(cid.getInvoice_detail_price(), cid.getInvoice_detail_unit())) : 0;
					// add invoice detail to list
					cids.add(cid);
				} else if(isFirst) {
					cid.setInvoice_detail_name(cod.getDetail_name());
					cid.setInvoice_detail_price(cod.getDetail_price()!=null ? cod.getDetail_price() : 0d);
					cid.setInvoice_detail_unit(cod.getDetail_unit());
					cid.setInvoice_detail_type(cod.getDetail_type());
					cids.add(cid);
				}
				
				if (cod.getDetail_type()!=null && cod.getDetail_type().contains("plan-") 
						 && !isRegenerate) {
//					System.out.println("is_Next_Invoice: "+is_Next_Invoice);
					// if is next invoice then plus one month else plus unit month(s)
					int nextInvoiceMonth = is_Next_Invoice ? 1 : cod.getDetail_unit();
					int nextInvoiceDay = -7;
					Calendar calNextInvoiceDay = Calendar.getInstance();
					calNextInvoiceDay.setTime(!is_Next_Invoice 
								? (customerOrder.getOrder_using_start() != null 
								? customerOrder.getOrder_using_start() 
								: new Date()) 
						: customerOrder.getNext_invoice_create_date_flag());
					calNextInvoiceDay.add(Calendar.MONTH, nextInvoiceMonth);
					// update customer order's next invoice create day flag begin
					customerOrder.setNext_invoice_create_date_flag(calNextInvoiceDay.getTime());
					// update customer order's next invoice create day flag end

					calNextInvoiceDay.add(Calendar.DAY_OF_MONTH, nextInvoiceDay);
					// update customer order's next invoice create day begin
					customerOrder.setNext_invoice_create_date(calNextInvoiceDay.getTime());
					// update customer order's next invoice create day end
					
					customerOrder.getParams().put("id", customerOrder.getId());
					
					this.customerOrderMapper.updateCustomerOrder(customerOrder);
				}
			}
		}

		// Automatically assign previous invoice's prepayment to current invoice if have
		if(!isFirst){
			// If previous invoice's balance less than zero
			if(cpi.getBalance()!=null && cpi.getBalance()<0){
				
				CustomerInvoice cpiUpdate = new CustomerInvoice();
				
				cpi.setFinal_payable_amount(cpi.getFinal_payable_amount()!=null ? cpi.getFinal_payable_amount() : 0d);
				cpi.setAmount_paid(cpi.getAmount_paid()!=null ? cpi.getAmount_paid() : 0d);
				cpi.setBalance(cpi.getBalance()!=null ? cpi.getBalance() : 0d);
				ci.setAmount_paid(ci.getAmount_paid()!=null ? ci.getAmount_paid() : 0d);
				
				// previous final payable < 0 && previous paid == 0 ? (totalCreditBack = totalCreditBack + abs(previous balance), previous final payable = 0d), prepayment is credit
				if(cpi.getFinal_payable_amount() < 0 && cpi.getAmount_paid()==0){
					totalCreditBack = TMUtils.bigAdd(totalCreditBack, Math.abs(cpi.getBalance()));
					
					cpiUpdate.setFinal_payable_amount(0d);
					
				// (previous paid > previous final payable) && (abs(previous balance) == previous paid - previous final payable) && previous final payable > 0 ? (current paid = current paid + abs(previous balance), previous paid = previous final payable), prepayment is paid
				} else if(cpi.getAmount_paid() > cpi.getFinal_payable_amount()
				  && Math.abs(cpi.getBalance()) == TMUtils.bigSub(cpi.getAmount_paid(), cpi.getFinal_payable_amount())
				  && cpi.getFinal_payable_amount() > 0){
					ci.setAmount_paid(TMUtils.bigAdd(ci.getAmount_paid(), Math.abs(cpi.getBalance())));

					cpiUpdate.setAmount_paid(cpi.getFinal_payable_amount());
					
				// (previous paid > previous final payable) && (abs(previous balance) > previous paid - previous final payable) ? (current paid = current paid + (abs(previous balance) - abs(previous final payable)), totalCreditBack = (totalCreditBack + (abs(previous balance) - previous paid))), prepayment is credit&paid 
				} else if(cpi.getAmount_paid() > cpi.getFinal_payable_amount()
				 && Math.abs(cpi.getBalance()) >= TMUtils.bigSub(cpi.getAmount_paid(), cpi.getFinal_payable_amount())){
					// ciPaid = ciPaid + (abs(cpiBalance) - abs(cpiFinalPayable))
					ci.setAmount_paid(TMUtils.bigAdd(ci.getAmount_paid(), TMUtils.bigSub(Math.abs(cpi.getBalance()), Math.abs(cpi.getFinal_payable_amount()))));
					// totalCreditBack = totalCreditBack + (abs(cpiBalance) - (cpiPaid - cpiFinalPayable))
					totalCreditBack = TMUtils.bigAdd(totalCreditBack, TMUtils.bigSub(Math.abs(cpi.getBalance()), cpi.getAmount_paid()));
					
					cpiUpdate.setAmount_paid(0d);
					cpiUpdate.setFinal_payable_amount(0d);
					
				}
				
				cpiUpdate.setBalance(0d);
				cpiUpdate.getParams().put("id", cpi.getId());
				cpiUpdate.setStatus("paid");
				this.ciMapper.updateCustomerInvoice(cpiUpdate);
				
			}
		}
		

		invoicePDF.setCompanyDetail(companyDetail);
		invoicePDF.setCo(customerOrder);
		invoicePDF.setCurrentCustomerInvoice(ci);
		
		ci.setOrder_id(customerOrder.getId());
		
		
		if(pstn_numbers.size() > 0){
			
			for (String pstn_number : pstn_numbers) {
				
				totalAmountPayable = CallingAndRentalFeeCalucation.ccrRentalOperation(ci, isRegenerate, pstn_number, cids, totalAmountPayable, customerCallRecordMapper, this.ciMapper, customerOrderChorusAddonMapper);
				
				totalAmountPayable = CallingAndRentalFeeCalucation.ccrOperation(ci, isRegenerate, pcmsPSTN, pstn_number, cids, invoicePDF, totalAmountPayable, this.customerCallRecordMapper, this.callInternationalRateMapper, this.customerCallingRecordCallplusMapper, this.ciMapper, customerOrder.getCustomer_type(), this.customerOrderNZCallingRatesSettingMapper, customerOrder.getId());
				
			}
		}
		
		if(voip_numbers.size() > 0){
			
			for (String voip_number : voip_numbers) {
				totalAmountPayable = CallingAndRentalFeeCalucation.voipCallOperation(ci, isRegenerate, pcmsVoIP, voip_number, cids, invoicePDF, totalAmountPayable, nzAreaCodeListMapper, vosVoIPRateMapper, vosVoIPCallRecordMapper, ciMapper, customerOrder.getCustomer_type(), this.customerOrderNZCallingRatesSettingMapper, customerOrder.getId());
				
			}
		}
		
		totalCreditBack = Double.parseDouble(TMUtils.fillDecimalPeriod(totalCreditBack));
		totalAmountPayable = isBusiness ? TMUtils.bigMultiply(totalAmountPayable, 1.15) : totalAmountPayable;
		
		// If previous balance greater than 0 and customer_type equals to business
//		if(cpi!=null && cpi.getBalance()!=null && cpi.getBalance()>0 && (customerOrder.getCustomer_type()!=null && "business".equals(customerOrder.getCustomer_type()))){
//			totalAmountPayable = TMUtils.bigAdd(totalAmountPayable, cpi.getBalance());
//		}

		ci.setAmount_payable(totalAmountPayable);
		ci.setFinal_payable_amount(TMUtils.bigSub(totalAmountPayable, totalCreditBack));
		ci.setAmount_paid(ci.getAmount_paid() == null ? 0d : ci.getAmount_paid());
		// balance = final payable - paid
		ci.setBalance(TMUtils.bigSub(ci.getFinal_payable_amount(), ci.getAmount_paid()));
		
		// If balance greater than 0
		if(ci.getBalance()>0){
			// If customer account credit greater than 0, then use account credit to defray invoice balance 
			if(customer.getBalance()!=null && customer.getBalance()>0){
				
				Customer cUpdate = new Customer();
				CustomerTransaction ct = new CustomerTransaction();
				
				// If customer account credit greater than balance
				if(customer.getBalance() >= ci.getBalance()){
					
					ct.setAmount(customer.getBalance());
					ct.setAmount_settlement(customer.getBalance());
					ct.setCard_name("account-credit");
					ct.setCustomer_id(customer.getId());
					ct.setOrder_id(customerOrder.getId());
					ct.setInvoice_id(ci.getId());
					ct.setTransaction_date(new Date());
					ct.setCurrency_input("NZD");
					
					cUpdate.setBalance(TMUtils.bigSub(customer.getBalance(), ci.getBalance()));
					ci.setAmount_paid(ci.getFinal_payable_amount());
					ci.setBalance(0d);
				// Else customer account credit less than balance
				} else {
					
					ct.setAmount(customer.getBalance());
					ct.setAmount_settlement(customer.getBalance());
					ct.setCard_name("account-credit");
					ct.setCustomer_id(customer.getId());
					ct.setOrder_id(customerOrder.getId());
					ct.setInvoice_id(ci.getId());
					ct.setTransaction_date(new Date());
					ct.setCurrency_input("NZD");
					
					ci.setBalance(TMUtils.bigSub(ci.getBalance(), customer.getBalance()));
					ci.setAmount_paid(TMUtils.bigAdd(ci.getAmount_paid(), customer.getBalance()));
					cUpdate.setBalance(0d);
				}
				cUpdate.getParams().put("id", customer.getId());
				this.customerMapper.updateCustomer(cUpdate);
				this.customerTransactionMapper.insertCustomerTransaction(ct);
			}
		}
		
		if(ci.getBalance()<=0){
			ci.setStatus("paid");
			
		// Else paid some fee
		} else if(ci.getAmount_paid()!=null && ci.getAmount_paid()>0){
			ci.setStatus("not_pay_off");
		} else {
			ci.setStatus("unpaid");
		}
		
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

		ci.setInvoice_type("general-invoice");
		ci.getParams().put("id", ci.getId());
		this.ciMapper.updateCustomerInvoice(ci);

		// Deleting repeated invoices
//		this.ciMapper.deleteCustomerInvoiceByRepeat();

		if(!isRegenerate){
			
			// call mail at value retriever

			MailRetriever.mailAtValueRetriever(notificationEmailFinal, customer,  customerOrder, ci, companyDetail);
			ApplicationEmail applicationEmail = new ApplicationEmail();
			applicationEmail.setAddressee(customerOrder.getEmail()!=null && !"".equals(customerOrder.getEmail()) ? customerOrder.getEmail() : customer.getEmail());
			applicationEmail.setSubject(notificationEmailFinal.getTitle());
			applicationEmail.setContent(notificationEmailFinal.getContent());
			// binding attachment name & path to email
			applicationEmail.setAttachName("invoice_" + ci.getId() + ".pdf");
			applicationEmail.setAttachPath((String) map.get("filePath"));
			this.mailerService.sendMailByAsynchronousMode(applicationEmail);

			// get sms register template from db
			MailRetriever.mailAtValueRetriever(notificationSMSFinal, customer, customerOrder, ci, companyDetail);
			// send sms to customer's mobile phone
			this.smserService.sendSMSByAsynchronousMode(customerOrder.getMobile()!=null && !"".equals(customerOrder.getMobile()) ? customerOrder.getMobile() : customer.getCellphone(), notificationSMSFinal.getContent());
		}
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("customer", customer);
		resultMap.put("customerInvoice", ci);
		resultMap.put("customerOrder", customerOrder);
		
		return resultMap;
	}
	
	/**
	 * BEGIN CustomerOrderDetail
	 */
	
	public CustomerOrderDetail queryCustomerOrderDetail(CustomerOrderDetail cod){
		List<CustomerOrderDetail> cods = this.customerOrderDetailMapper.selectCustomerOrderDetails(cod);
		return cods!=null && cods.size()>0 ? cods.get(0) : null;
	}
	
	public List<CustomerOrderDetail> queryCustomerOrderDetails(CustomerOrderDetail cod){
		return this.customerOrderDetailMapper.selectCustomerOrderDetails(cod);
	}
	
	@Transactional
	public Page<CustomerOrderDetail> queryCustomerOrderDetailsByPage(Page<CustomerOrderDetail> page) {
		page.setTotalRecord(this.customerOrderDetailMapper.selectCustomerOrderDetailsSum(page));
		page.setResults(this.customerOrderDetailMapper.selectCustomerOrderDetailsByPage(page));
		return page;
	}
	
	@Transactional
	public int queryCustomerOrderDetailsBySum(Page<CustomerOrderDetail> page) {
		return this.customerOrderDetailMapper.selectCustomerOrderDetailsSum(page);
	}
	
	public String queryCustomerOrderDetailGroupByOrderId(int order_id) {
		CustomerOrderDetail codQuery = new CustomerOrderDetail();
		codQuery.getParams().put("order_id", order_id);
		List<CustomerOrderDetail> customerOrderDetails = this.queryCustomerOrderDetails(codQuery);
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
	
	@Transactional
	public void removeCustomerTransactionById(int id){
		this.customerTransactionMapper.deleteCustomerTransactionById(id);
	}
	
	/**
	 * END CustomerTransaction
	 */
	

	
	
	
	/**
	 * END Organization
	 */


	@Transactional 
	public String queryCustomerPreviousProviderInvoiceFilePathById(int id){
		CustomerOrder coQuery = new CustomerOrder();
		coQuery.getParams().put("id", id);
		coQuery = this.queryCustomerOrder(coQuery);
		return coQuery.getPrevious_provider_invoice();
	}

	@Transactional 
	public String queryCustomerCreditFilePathById(int id){
		CustomerOrder coQuery = new CustomerOrder();
		coQuery.getParams().put("id", id);
		coQuery = this.queryCustomerOrder(coQuery);
		return coQuery.getCredit_pdf_path();
	}

	@Transactional 
	public String queryCustomerDDPayFormPathById(int id){
		CustomerOrder coQuery = new CustomerOrder();
		coQuery.getParams().put("id", id);
		coQuery = this.queryCustomerOrder(coQuery);
		return coQuery.getDdpay_pdf_path();
	}

	@Transactional 
	public String queryCustomerOrderingFormPathById(int id){
		CustomerOrder coQuery = new CustomerOrder();
		coQuery.getParams().put("id", id);
		coQuery = this.queryCustomerOrder(coQuery);
		return coQuery.getOrdering_form_pdf_path();
	}

	@Transactional 
	public String queryCustomerReceiptFormPathById(int id){
		CustomerOrder coQuery = new CustomerOrder();
		coQuery.getParams().put("id", id);
		coQuery = this.queryCustomerOrder(coQuery);
		return coQuery.getReceipt_pdf_path();
	}
	
	/**
	 * BEGIN CustomerServiceRecord
	 */
	@Transactional
	public List<CustomerServiceRecord> queryCustomerServiceRecord(CustomerServiceRecord csr){
		return this.customerServiceRecordMapper.selectCustomerServiceRecord(csr);
	}

	@Transactional
	public Page<CustomerServiceRecord> queryCustomerServiceRecordsByPage(Page<CustomerServiceRecord> page) {
		page.setTotalRecord(this.customerServiceRecordMapper.selectCustomerServiceRecordsSum(page));
		page.setResults(this.customerServiceRecordMapper.selectCustomerServiceRecordsByPage(page));
		return page;
	}

	@Transactional
	public CustomerServiceRecord queryCustomerServiceRecordById(int id){
		return this.customerServiceRecordMapper.selectCustomerServiceRecordById(id);
	}

	@Transactional
	public void createCustomerServiceRecord(CustomerServiceRecord csr){
		this.customerServiceRecordMapper.insertCustomerServiceRecord(csr);
	};

	@Transactional
	public void editCustomerServiceRecord(CustomerServiceRecord csr){
		this.customerServiceRecordMapper.updateCustomerServiceRecord(csr);
	};

	@Transactional
	public void removeCustomerServiceRecord(CustomerServiceRecord csr){
		this.customerServiceRecordMapper.deleteCustomerServiceRecord(csr);
	};
	/**
	 * END CustomerServiceRecord
	 */
	
	/**
	 * BEGIN CustomerServiceGivenPaid
	 */
	@Transactional
	public String serviceGivenPaid(Customer c, CustomerOrder co, CompanyDetail cd, User u){
		CustomerOrderDetail codQuery = new CustomerOrderDetail();
		codQuery.getParams().put("order_id", co.getId());
		List<CustomerOrderDetail> cods = this.queryCustomerOrderDetails(codQuery);
		List<CustomerInvoiceDetail> cids = new ArrayList<CustomerInvoiceDetail>();

		CustomerOrder coQuery = new CustomerOrder();
		coQuery.getParams().put("id", co.getId());
		coQuery = this.queryCustomerOrder(coQuery);
		// If is customer invite customer
		if(coQuery.getInviter_customer_id()!=null){
			Double customer_inviter_gained_commission = TMUtils.bigMultiply(TMUtils.bigDivide(coQuery.getInviter_rate(), 100d), coQuery.getOrder_total_price());
			Customer cQuery = new Customer();
			cQuery.getParams().put("id", coQuery.getInviter_customer_id());
			Customer cUpdate = this.queryCustomer(cQuery);
			cUpdate.setBalance(TMUtils.bigAdd(cUpdate.getBalance()!=null ? cUpdate.getBalance() : 0d, customer_inviter_gained_commission));
			cUpdate.getParams().put("id", cUpdate.getId());
			this.editCustomer(cUpdate);
			
			CustomerTransaction ctCreate = new CustomerTransaction();
			ctCreate.setCustomer_id(cUpdate.getId());
			ctCreate.setTransaction_date(new Date());
			ctCreate.setAmount(customer_inviter_gained_commission);
			ctCreate.setAmount_settlement(customer_inviter_gained_commission);
			ctCreate.setCurrency_input("Inviter Commission");
			ctCreate.setCard_name("Inviter Commission(Customer)");
			this.createCustomerTransaction(ctCreate);
		}
		// If is user invite customer
		if(coQuery.getInviter_user_id()!=null){
			Double user_inviter_gained_commission = TMUtils.bigMultiply(TMUtils.bigDivide(coQuery.getInviter_rate(), 100d), coQuery.getOrder_total_price());
			User uUpdate = this.userMapper.selectUserById(coQuery.getInviter_user_id());
			uUpdate.setInvite_commission(user_inviter_gained_commission);
			uUpdate.getParams().put("id", uUpdate.getId());
			this.userMapper.updateUser(uUpdate);
			
			CustomerTransaction ctCreate = new CustomerTransaction();
			ctCreate.setCustomer_id(uUpdate.getId());
			ctCreate.setTransaction_date(new Date());
			ctCreate.setAmount(user_inviter_gained_commission);
			ctCreate.setAmount_settlement(user_inviter_gained_commission);
			ctCreate.setCurrency_input("Inviter Commission");
			ctCreate.setCard_name("Inviter Commission(Customer)");
			this.createCustomerTransaction(ctCreate);
		}
		
		
		Double totalCreditBack = 0d;
		Double totalAmountPayable = 0d;
		
		for (CustomerOrderDetail cod : cods) {
			
			CustomerInvoiceDetail cid = new CustomerInvoiceDetail();
			cid.setInvoice_detail_name(cod.getDetail_name());
			cid.setInvoice_detail_unit(cod.getDetail_unit() == null ? 1 : cod.getDetail_unit());
			cid.setInvoice_detail_type(cod.getDetail_type());
			
			if ("discount".equals(cod.getDetail_type())
				&& (cod.getDetail_desc()==null || (cod.getDetail_desc()!=null && cod.getDetail_desc().contains("all-forms")))) {
				
				cid.setInvoice_detail_discount(cod.getDetail_price());
				// decrease amountPayable
				totalCreditBack = TMUtils.bigAdd(totalCreditBack, TMUtils.bigMultiply(cid.getInvoice_detail_discount(), cid.getInvoice_detail_unit()));
				// add invoice detail to list
				cids.add(cid);
				// else if detail type is discount then this discount is expired
				// and will not be add to the invoice detail list
			} else if ("debit".equals(cod.getDetail_type()) && cod.getDetail_expired().getTime() >= System.currentTimeMillis()) {
				
				cid.setInvoice_detail_price(cod.getDetail_price());
				// decrease amountPayable
				totalAmountPayable = TMUtils.bigSub(totalAmountPayable, TMUtils.bigMultiply(cid.getInvoice_detail_price(), cid.getInvoice_detail_unit()));
				// add invoice detail to list
				cids.add(cid);
				// else if detail type is discount then this discount is expired
				// and will not be add to the invoice detail list
			} else if (!"discount".equals(cod.getDetail_type())) {
				if(cod.getDetail_type()!=null && "termination-credit".equals(cod.getDetail_type())){
					cid.setInvoice_detail_name(cod.getDetail_name());
					cid.setInvoice_detail_unit(cod.getDetail_unit());
					cid.setInvoice_detail_discount(cod.getDetail_price());
					cid.setInvoice_detail_desc(cod.getDetail_desc());
					cids.add(cid);
					totalCreditBack = TMUtils.bigAdd(totalCreditBack, TMUtils.bigMultiply(cod.getDetail_price(), cod.getDetail_unit()));
				} else if(cod.getDetail_type()!=null && ("present-calling-minutes".equals(cod.getDetail_type()) || "super-free-calling".equals(cod.getDetail_type()))){
					cid.setInvoice_detail_unit(cod.getDetail_unit());
					cids.add(cid);
				} else {
					
					if("plan-term".equals(cod.getDetail_type()) || "plan-no-term".equals(cod.getDetail_type())){
						Calendar cal = Calendar.getInstance();
						Date startFrom = TMUtils.parseDateYYYYMMDD(co.getOrder_using_start_str());
						cal.setTime(TMUtils.parseDateYYYYMMDD(co.getOrder_using_start_str()));
						cal.add(Calendar.MONTH, cod.getDetail_unit());
						cal.add(Calendar.DAY_OF_MONTH, -1);
						Date endTo = cal.getTime();
						cid.setInvoice_detail_desc(TMUtils.dateFormatYYYYMMDD(startFrom)+" - "+TMUtils.dateFormatYYYYMMDD(endTo));
					} else if("plan-topup".equals(cod.getDetail_type())){
						Calendar cal = Calendar.getInstance();
						Date startFrom = TMUtils.parseDateYYYYMMDD(co.getOrder_using_start_str());
						cal.setTime(TMUtils.parseDateYYYYMMDD(co.getOrder_using_start_str()));
						cal.add(Calendar.WEEK_OF_MONTH, 1);
						cal.add(Calendar.DAY_OF_WEEK, -1);
						Date endTo = cal.getTime();
						cid.setInvoice_detail_desc(TMUtils.dateFormatYYYYMMDD(startFrom)+" - "+TMUtils.dateFormatYYYYMMDD(endTo));
					}
					
					// if is first invoice and unit isn't null then assigned from unit, otherwise assign to 1
					cid.setInvoice_detail_unit(cod.getDetail_unit() != null  ? cod.getDetail_unit() : 1);
					cid.setInvoice_detail_price(cod.getDetail_price() == null ? 0d : cod.getDetail_price());
					// increase amountPayable
					totalAmountPayable =  cid.getInvoice_detail_price() != null ? TMUtils.bigAdd(totalAmountPayable, TMUtils.bigMultiply(cid.getInvoice_detail_price(), cid.getInvoice_detail_unit())) : 0;
					// add invoice detail to list
					cids.add(cid);
				}
			}
		}
		
//		System.out.println("totalAmountPayable: "+totalAmountPayable);
//		System.out.println("totalCreditBack: "+totalCreditBack);
//		System.out.println("c.getBalance(): "+c.getBalance());
		
		totalCreditBack = Double.parseDouble(TMUtils.fillDecimalPeriod(totalCreditBack));
		totalAmountPayable = Double.parseDouble(TMUtils.fillDecimalPeriod(totalAmountPayable));
		
		// IF customer's account credit is less than final payable amount
		if(c.getBalance()==null || c.getBalance()<TMUtils.bigSub(totalAmountPayable, totalCreditBack)){
			return null;
		}
		
		CustomerInvoice ci = new CustomerInvoice();
		ci.setCustomer_id(c.getId());
		ci.setOrder_id(co.getId());
		ci.setCreate_date(TMUtils.parseDateYYYYMMDD(co.getOrder_using_start_str()));
		ci.setDue_date(TMUtils.getInvoiceDueDate(ci.getCreate_date(), 7));
		this.ciMapper.insertCustomerInvoice(ci);
		
		InvoicePDFCreator invoicePDF = new InvoicePDFCreator();
		invoicePDF.setCompanyDetail(cd);
		invoicePDF.setCo(coQuery);
		invoicePDF.setCurrentCustomerInvoice(ci);

		ci.setAmount_payable("personal".toUpperCase().equals(coQuery.getCustomer_type().toUpperCase()) ? totalAmountPayable : TMUtils.bigMultiply(totalAmountPayable, 1.15));
		ci.setFinal_payable_amount(TMUtils.bigSub(ci.getAmount_payable(), totalCreditBack));
		Double amount_paid = c.getBalance()>=ci.getFinal_payable_amount() ? ci.getFinal_payable_amount() : ci.getAmount_paid() == null ? 0d : ci.getAmount_paid();
		ci.setAmount_paid(amount_paid<=0 ? 0 : amount_paid);
		// balance = final payable - paid
		ci.setBalance(TMUtils.bigSub(ci.getFinal_payable_amount(), ci.getAmount_paid()));
		// Add cids into ci
		ci.setCustomerInvoiceDetails(cids);
		ci.setStatus(c.getBalance()>=ci.getFinal_payable_amount() ? "paid" : "unpaid");

		// If customer's account credit is greater than invoice's final payable amount, then paid off the invoice
		if(c.getBalance()>=ci.getFinal_payable_amount()){
			// BEGIN TRANSACTION ASSIGNMENT
			CustomerTransaction ct = new CustomerTransaction();
			// Assign invoice's paid amount to transaction's amount
			ct.setAmount(ci.getAmount_paid());
			ct.setAmount_settlement(ci.getAmount_paid());
			// Assign card_name as ddpay
			ct.setCard_name("account-credit");
			// Assign transaction's sort as type's return from order by order_id
			ct.setTransaction_sort(co.getOrder_type());
			// Assign customer_id, order_id, invoice_id to transaction's related
			// fields
			ct.setCustomer_id(c.getId());
			ct.setOrder_id(co.getId());
			ct.setInvoice_id(ci.getId());
			// Assign transaction's time as current time
			ct.setTransaction_date(new Date());
			ct.setCurrency_input("NZD");
			ct.setExecutor(u.getId());
			this.customerTransactionMapper.insertCustomerTransaction(ct);
		}

		// If customer's account credit is greater than invoice's final payable amount, then account credit = account credit - invoice's final payable amount
		c.setBalance(c.getBalance()>=ci.getFinal_payable_amount() ? TMUtils.bigSub(c.getBalance(), ci.getFinal_payable_amount()) : c.getBalance());
		
		// Add cids into db
		for (CustomerInvoiceDetail cid : cids) {
			cid.setInvoice_id(ci.getId());
			this.ciDetailMapper.insertCustomerInvoiceDetail(cid);
		}

		// Set file path
		String filePath = null;
		try {
			filePath = (String) invoicePDF.create().get("filePath");
			// generate invoice PDF
			ci.setInvoice_pdf_path(filePath);
		} catch (DocumentException | IOException e) {
			e.printStackTrace();
		}
		
		ci.getParams().put("id", ci.getId());
		this.ciMapper.updateCustomerInvoice(ci);
		c.getParams().put("id", c.getId());
		this.customerMapper.updateCustomer(c);
		
		
		return filePath;
	}
	
	// Check Pending Order
	@Transactional 
	public void checkPendingOrder(CustomerOrder coQuery) throws ParseException{
		Notification notificationEmail = this.notificationMapper.selectNotificationBySort("order-warning-notice", "email");
		Notification notificationSMS = this.notificationMapper.selectNotificationBySort("order-warning-notice", "sms");
		List<CustomerOrder> cos = this.queryCustomerOrders(coQuery);
		
		CompanyDetail cd = this.queryCompanyDetail();

		for (CustomerOrder co : cos) {
			CustomerOrder customerOrder = new CustomerOrder();
			customerOrder.setOrder_status("pending-warning");
			customerOrder.getParams().put("id", co.getId());
			this.editCustomerOrder(customerOrder);
			
			Customer cQuery = new Customer();
			cQuery.getParams().put("id", co.getCustomer_id());
			Customer c = this.queryCustomer(cQuery);

			// Prevent template pollution
			Notification notificationEmailFinal = new Notification(notificationEmail.getTitle(), notificationEmail.getContent());
			Notification notificationSMSFinal = new Notification(notificationSMS.getTitle(), notificationSMS.getContent());
			
			MailRetriever.mailAtValueRetriever(notificationEmailFinal, c, co, cd);
			ApplicationEmail applicationEmail = new ApplicationEmail();
			applicationEmail.setAddressee(co.getEmail()!=null && !"".equals(co.getEmail()) ? co.getEmail() : c.getEmail());
			applicationEmail.setSubject(notificationEmailFinal.getTitle());
			applicationEmail.setContent(notificationEmailFinal.getContent());
			// binding attachment name & path to email
			if(co.getOrdering_form_pdf_path()!=null && !"".equals(co.getOrdering_form_pdf_path())){
				applicationEmail.setAttachName("ordering_form_" + co.getId() + ".pdf");
				applicationEmail.setAttachPath(co.getOrdering_form_pdf_path());
			}
//			else {
//				CustomerInvoice ciQuery = new CustomerInvoice();
//				ciQuery.getParams().put("order_id", co.getId());
//				CustomerInvoice ci = this.queryCustomerInvoice(ciQuery);
//				applicationEmail.setAttachName("invoice_" + ci.getId() + ".pdf");
//				applicationEmail.setAttachPath(ci.getInvoice_pdf_path());
//			}
			this.mailerService.sendMailByAsynchronousMode(applicationEmail);

			// get sms register template from db
			MailRetriever.mailAtValueRetriever(notificationSMSFinal, c, co, cd);
			// send sms to customer's mobile phone
			this.smserService.sendSMSByAsynchronousMode(co.getMobile()!=null && !"".equals(co.getMobile()) ? co.getMobile() : c.getCellphone(), notificationSMSFinal.getContent());
		}
	}
	
	// Check Pending Warning Order
	@Transactional 
	public void checkPendingWarningOrder(CustomerOrder coQuery) throws ParseException{
		Notification notificationEmail = this.notificationMapper.selectNotificationBySort("order-void-notice", "email");
		Notification notificationSMS = this.notificationMapper.selectNotificationBySort("order-void-notice", "sms");
		List<CustomerOrder> cos = this.queryCustomerOrders(coQuery);
		
		CompanyDetail cd = this.queryCompanyDetail();

		for (CustomerOrder co : cos) {
			CustomerOrder customerOrder = new CustomerOrder();
			customerOrder.setOrder_status("void");
			customerOrder.getParams().put("id", co.getId());
			this.editCustomerOrder(customerOrder);

			Customer cQuery = new Customer();
			cQuery.getParams().put("id", co.getCustomer_id());
			Customer c = this.queryCustomer(cQuery);

			// Prevent template pollution
			Notification notificationEmailFinal = new Notification(notificationEmail.getTitle(), notificationEmail.getContent());
			Notification notificationSMSFinal = new Notification(notificationSMS.getTitle(), notificationSMS.getContent());
			
			MailRetriever.mailAtValueRetriever(notificationEmailFinal, c, co, cd);
			ApplicationEmail applicationEmail = new ApplicationEmail();
			applicationEmail.setAddressee(co.getEmail()!=null && !"".equals(co.getEmail()) ? co.getEmail() : c.getEmail());
			applicationEmail.setSubject(notificationEmailFinal.getTitle());
			applicationEmail.setContent(notificationEmailFinal.getContent());
			this.mailerService.sendMailByAsynchronousMode(applicationEmail);

			// get sms register template from db
			MailRetriever.mailAtValueRetriever(notificationSMSFinal, c, co, cd);
			// send sms to customer's mobile phone
			this.smserService.sendSMSByAsynchronousMode(co.getMobile()!=null && !"".equals(co.getEmail()) ? co.getEmail() : c.getCellphone(), notificationSMSFinal.getContent());
		}
	}
	
	// Check Pending Warning Order
	@Transactional 
	public void checkInvoiceNotification() throws ParseException{

		Calendar calThird = Calendar.getInstance();
		calThird.add(Calendar.DATE, -2);
		Calendar calFifth = Calendar.getInstance();
		calFifth.add(Calendar.DATE, -4);
		Calendar calEighth = Calendar.getInstance();
		calEighth.add(Calendar.DATE, -7);
		Calendar calNinth = Calendar.getInstance();
		calNinth.add(Calendar.DATE, -8);
		Calendar calTenth = Calendar.getInstance();
		calTenth.add(Calendar.DATE, -9);
		Date third = calThird.getTime();
		Date fifth = calFifth.getTime();
		Date eighth = calEighth.getTime();
		Date ninth = calNinth.getTime();
		Date tenth = calTenth.getTime();
		
		CustomerInvoice ciQuery = new CustomerInvoice();
		ciQuery.getParams().put("invoice_type", "general-invoice");
		ciQuery.getParams().put("where", "create_date2_status2_due_date_is_not_null");
		ciQuery.getParams().put("status", "unpaid");
		ciQuery.getParams().put("status2", "overdue");
		
		ciQuery.getParams().put("create_date", third);
		ciQuery.getParams().put("create_date2", fifth);
		List<CustomerInvoice> cisThirdFifth = this.queryCustomerInvoices(ciQuery);
		ciQuery.getParams().put("create_date", eighth);
		ciQuery.getParams().put("create_date2", ninth);
		List<CustomerInvoice> cisEighthNinth = this.queryCustomerInvoices(ciQuery);
		
		ciQuery.getParams().put("where", "create_date_status2_due_date_is_not_null");
		ciQuery.getParams().remove("create_date2");
		ciQuery.getParams().put("create_date", tenth);
		List<CustomerInvoice> cisTenth = this.queryCustomerInvoices(ciQuery);
		
		// If any of them isn't null and size>0
		if((cisThirdFifth!=null && cisThirdFifth.size()>0)
		|| (cisEighthNinth!=null && cisEighthNinth.size()>0)
		|| (cisTenth!=null && cisTenth.size()>0)){

			Notification emailThirdFifth = this.notificationMapper.selectNotificationBySort("invoice-notice-third-fifth-day", "email");
			Notification smsThirdFifth = this.notificationMapper.selectNotificationBySort("invoice-notice-third-fifth-day", "sms");

			Notification emailEighthNinth = this.notificationMapper.selectNotificationBySort("invoice-notice-eighth-ninth-day", "email");
			Notification smsEighthNinth = this.notificationMapper.selectNotificationBySort("invoice-notice-eighth-ninth-day", "sms");

			Notification emailTenth = this.notificationMapper.selectNotificationBySort("invoice-notice-tenth-day", "email");
			Notification smsTenth = this.notificationMapper.selectNotificationBySort("invoice-notice-tenth-day", "sms");
			
			CompanyDetail cd = this.queryCompanyDetail();
			
//			System.out.println("cisThirdFifth: ");
			for (CustomerInvoice ci : cisThirdFifth) {
				
				Customer cQuery = new Customer();
				cQuery.getParams().put("id", ci.getCustomer_id());
				Customer c = this.queryCustomer(cQuery);
				
				CustomerOrder coQuery = new CustomerOrder();
				coQuery.getParams().put("id", ci.getOrder_id());
				CustomerOrder co = this.queryCustomerOrder(coQuery);
				
				if("personal".equals(co.getCustomer_type())){

					// Prevent template pollution
					Notification emailThirdFifthFinal = new Notification(emailThirdFifth.getTitle(), emailThirdFifth.getContent());
					Notification smsThirdFifthFinal = new Notification(smsThirdFifth.getTitle(), smsThirdFifth.getContent());
					
					MailRetriever.mailAtValueRetriever(emailThirdFifthFinal, c, co, ci, cd);
					ApplicationEmail applicationEmail = new ApplicationEmail();
					applicationEmail.setAddressee(co.getEmail()!=null && !"".equals(co.getEmail()) ? co.getEmail() : c.getEmail());
					applicationEmail.setSubject(emailThirdFifthFinal.getTitle());
					applicationEmail.setContent(emailThirdFifthFinal.getContent());
					applicationEmail.setAttachName("invoice_" + ci.getId() + ".pdf");
					applicationEmail.setAttachPath(ci.getInvoice_pdf_path());
					this.mailerService.sendMailByAsynchronousMode(applicationEmail);

					// get sms register template from db
					MailRetriever.mailAtValueRetriever(smsThirdFifthFinal, c, co, ci, cd);
					// send sms to customer's mobile phone
					this.smserService.sendSMSByAsynchronousMode(co.getMobile()!=null && !"".equals(co.getMobile()) ? co.getMobile() : c.getCellphone(), smsThirdFifthFinal.getContent());
					
//					Console.log(ci);
				}
			}

//			System.out.println("cisEighthNinth: ");
			for (CustomerInvoice ci : cisEighthNinth) {
				
				Calendar calSuspend = Calendar.getInstance();
				calSuspend.setTime(ci.getCreate_date());
				calSuspend.add(Calendar.DATE, 9);
				ci.setSuspend_date_str(TMUtils.dateFormatYYYYMMDD(calSuspend.getTime()));
				
				Customer cQuery = new Customer();
				cQuery.getParams().put("id", ci.getCustomer_id());
				Customer c = this.queryCustomer(cQuery);
				
				CustomerOrder coQuery = new CustomerOrder();
				coQuery.getParams().put("id", ci.getOrder_id());
				CustomerOrder co = this.queryCustomerOrder(coQuery);
				
				if("personal".equals(co.getCustomer_type())){
	
					// Prevent template pollution
					Notification emailEighthNinthFinal = new Notification(emailEighthNinth.getTitle(), emailEighthNinth.getContent());
					Notification smsEighthNinthFinal = new Notification(smsEighthNinth.getTitle(), smsEighthNinth.getContent());
					
					MailRetriever.mailAtValueRetriever(emailEighthNinthFinal, c, co, ci, cd);
					ApplicationEmail applicationEmail = new ApplicationEmail();
					applicationEmail.setAddressee(co.getEmail()!=null && !"".equals(co.getEmail()) ? co.getEmail() : c.getEmail());
					applicationEmail.setSubject(emailEighthNinthFinal.getTitle());
					applicationEmail.setContent(emailEighthNinthFinal.getContent());
					applicationEmail.setAttachName("invoice_" + ci.getId() + ".pdf");
					applicationEmail.setAttachPath(ci.getInvoice_pdf_path());
					this.mailerService.sendMailByAsynchronousMode(applicationEmail);
	
					// get sms register template from db
					MailRetriever.mailAtValueRetriever(smsEighthNinthFinal, c, co, ci, cd);
					// send sms to customer's mobile phone
					this.smserService.sendSMSByAsynchronousMode(co.getMobile()!=null && !"".equals(co.getMobile()) ? co.getMobile() : c.getCellphone(), smsEighthNinthFinal.getContent());
					
				}
				
				
//				Console.log(ci);
			}

//			System.out.println("cisTenth: ");
			for (CustomerInvoice ci : cisTenth) {
				
				Calendar calDisconnect = Calendar.getInstance();
				calDisconnect.setTime(ci.getCreate_date());
				calDisconnect.add(Calendar.DATE, 11);
				ci.setDisconnected_date_str(TMUtils.dateFormatYYYYMMDD(calDisconnect.getTime()));

				Customer cQuery = new Customer();
				cQuery.getParams().put("id", ci.getCustomer_id());
				Customer c = this.queryCustomer(cQuery);
				
				CustomerOrder coQuery = new CustomerOrder();
				coQuery.getParams().put("id", ci.getOrder_id());
				CustomerOrder co = this.queryCustomerOrder(coQuery);
				
				if("personal".equals(co.getCustomer_type())){
	
					// Prevent template pollution
					Notification emailTenthFinal = new Notification(emailTenth.getTitle(), emailTenth.getContent());
					Notification smsTenthFinal = new Notification(smsTenth.getTitle(), smsTenth.getContent());
					
					MailRetriever.mailAtValueRetriever(emailTenthFinal, c, co, ci, cd);
					ApplicationEmail applicationEmail = new ApplicationEmail();
					applicationEmail.setAddressee(co.getEmail()!=null && !"".equals(co.getEmail()) ? co.getEmail() : c.getEmail());
					applicationEmail.setSubject(emailTenthFinal.getTitle());
					applicationEmail.setContent(emailTenthFinal.getContent());
					applicationEmail.setAttachName("invoice_" + ci.getId() + ".pdf");
					applicationEmail.setAttachPath(ci.getInvoice_pdf_path());
					this.mailerService.sendMailByAsynchronousMode(applicationEmail);
	
					// get sms register template from db
					MailRetriever.mailAtValueRetriever(smsTenthFinal, c, co, ci, cd);
					// send sms to customer's mobile phone
					this.smserService.sendSMSByAsynchronousMode(co.getMobile()!=null && !"".equals(co.getMobile()) ? co.getMobile() : c.getCellphone(), smsTenthFinal.getContent());
					
				}
				
//				Console.log(ci);
			}
			
		}
		
	}
	
	
	public void doPlansOrderConfirm(Customer customer) {
		
		Double plan_price = 0d;
		Double modem_price = 0d;
		Double service_price = 0d;
		Double discount_price = 0d;
		Double addons_price = 0d;
		
		CustomerOrder customerOrder = customer.getCustomerOrder();
		Plan plan = customerOrder.getPlan();
		
		customer.setSelect_plan_id(plan.getId());
		
		customerOrder.setCustomerOrderDetails(new ArrayList<CustomerOrderDetail>());
		customerOrder.setOrder_create_date(new Date());
		customerOrder.setOrder_type(plan.getPlan_group().replace("plan", "order"));
		customerOrder.setTerm_period(plan.getTerm_period());

		plan_price = plan.getPlan_price();
		CustomerOrderDetail cod_plan = new CustomerOrderDetail();
		cod_plan.setMonthly(true);
		cod_plan.setDetail_name(plan.getPlan_name());
		cod_plan.setDetail_price(plan.getPlan_price());
		cod_plan.setDetail_data_flow(plan.getData_flow());
		cod_plan.setDetail_plan_status(plan.getPlan_status());
		cod_plan.setDetail_plan_type(plan.getPlan_type());
		cod_plan.setDetail_plan_sort(plan.getPlan_sort());
		cod_plan.setDetail_plan_group(plan.getPlan_group());
		cod_plan.setDetail_plan_class(plan.getPlan_class());
		cod_plan.setDetail_plan_new_connection_fee(plan.getPlan_new_connection_fee());
		cod_plan.setDetail_term_period(plan.getTerm_period());
		cod_plan.setDetail_plan_memo(plan.getMemo());
		cod_plan.setDetail_unit(customerOrder.getPrepay_months());
		cod_plan.setDetail_type(plan.getPlan_group());
		
		customerOrder.getCustomerOrderDetails().add(cod_plan);
		
		if (customerOrder.getPrepay_months() == 1) {
			discount_price = 0d;
		} else if (customerOrder.getPrepay_months() == 3) {
			if ("VDSL".equals(customer.getSelect_plan_type()) && customerOrder.getSale_id() != null && customerOrder.getSale_id().intValue() == 20023) {
				discount_price = 0d;
			} else {
				discount_price = plan.getPlan_price() * 3 * 0.03;
			}
		} else if (customerOrder.getPrepay_months() == 6) {
			discount_price = plan.getPlan_price() * 6 * 0.07;
		} else if (customerOrder.getPrepay_months() == 12) {
			if ("VDSL".equals(customer.getSelect_plan_type()) && customerOrder.getSale_id() != null && customerOrder.getSale_id().intValue() == 10023) {
				discount_price = 0d;
			} else {
				discount_price = plan.getPlan_price() * 12 * 0.15;
			}
		}
		customerOrder.setDiscount_price(discount_price.intValue());
		
		if (discount_price > 0d) {
			CustomerOrderDetail cod_discount = new CustomerOrderDetail();
			cod_discount.setMonthly(false);
			if("plan".equals(customerOrder.getPlan_or_hardware_discount())){
				if (customerOrder.getPrepay_months() == 3) {
					cod_discount.setDetail_name("Prepay 3 Months Discount");
					cod_discount.setDetail_desc("3% off the total price of 3 months plan. all-forms");
				} else if (customerOrder.getPrepay_months() == 6) {
					cod_discount.setDetail_name("Prepay 6 Months Discount");
					cod_discount.setDetail_desc("7% off the total price of 6 months plan. all-forms");
				} else if (customerOrder.getPrepay_months() == 12) {
					cod_discount.setDetail_name("Prepay 12 Months Discount");
					cod_discount.setDetail_desc("15% off the total price of 12 months plan. all-forms");
				}
				cod_discount.setDetail_price(new Double(discount_price.intValue()));
				cod_discount.setDetail_type("discount");
				cod_discount.setDetail_unit(1);
				customerOrder.getCustomerOrderDetails().add(cod_discount);
			}
		}
		
		System.out.println("discount_price: " + discount_price.intValue());
		System.out.println("customerOrder.getContract(): " + customerOrder.getContract());
		customerOrder.setDiscount_price(discount_price.intValue());
		
		if ("transition".equals(customerOrder.getOrder_broadband_type())) {
			 
			service_price = plan.getTransition_fee();
			
			CustomerOrderDetail cod_trans = new CustomerOrderDetail();
			cod_trans.setMonthly(false);
			cod_trans.setDetail_name("Broadband Transition");
			cod_trans.setDetail_price(plan.getTransition_fee());
			cod_trans.setDetail_type("transition");
			cod_trans.setDetail_unit(1);
			
			customerOrder.getCustomerOrderDetails().add(cod_trans);
			
		}  else if ("new-connection".equals(customerOrder.getOrder_broadband_type())) {
			
			CustomerOrderDetail cod_conn = new CustomerOrderDetail();
			cod_conn.setMonthly(false);
			cod_conn.setDetail_name("Broadband New Connection");
			
			if ("personal".equals(customerOrder.getCustomer_type())) {
				
				if ("12 months contract".equals(customerOrder.getContract())) {
					 if (customerOrder.getPrepay_months() == 1 || customerOrder.getPrepay_months() == 3 || customerOrder.getPrepay_months() == 6) {
						 if (customerOrder.getSale_id() != null && customerOrder.getSale_id().intValue() == 20023) {
							 service_price = 0d;
							 cod_conn.setDetail_price(service_price);
						 } else {
							 service_price = 49d;
							 cod_conn.setDetail_price(service_price);
						 }
					 } else if (customerOrder.getPrepay_months() == 12){
						 service_price = 0d;
						 cod_conn.setDetail_price(service_price);
					 }
				} else if ("open term".equals(customerOrder.getContract())) {
					if (customerOrder.getPrepay_months() == 1) {
						service_price = plan.getPlan_new_connection_fee();
						cod_conn.setDetail_price(service_price);
					} else if (customerOrder.getPrepay_months() == 3 || customerOrder.getPrepay_months() == 6) {
						Double temp = (plan.getPlan_new_connection_fee()/12) * customerOrder.getPrepay_months();
						service_price = plan.getPlan_new_connection_fee() - temp.intValue();
						cod_conn.setDetail_price(new Double(service_price.intValue()));
					} else if (customerOrder.getPrepay_months() == 12) {
						service_price = 0d;
						cod_conn.setDetail_price(0d);
					}
				}
			} else if ("business".equals(customerOrder.getCustomer_type())) {
				service_price = 0d;
				cod_conn.setDetail_price(0d);
			}
			
			
			cod_conn.setDetail_type("new-connection");
			cod_conn.setDetail_unit(1);
			
			customerOrder.getCustomerOrderDetails().add(cod_conn);
			
		} else if ("jackpot".equals(customerOrder.getOrder_broadband_type())) {
			
			service_price = plan.getJackpot_fee();
			
			CustomerOrderDetail cod_jackpot = new CustomerOrderDetail();
			cod_jackpot.setMonthly(false);
			cod_jackpot.setDetail_name("Broadband New Connection & Phone Jack Installation");
			cod_jackpot.setDetail_price(plan.getJackpot_fee());
			cod_jackpot.setDetail_type("jackpot");
			cod_jackpot.setDetail_unit(1);
			
			customerOrder.getCustomerOrderDetails().add(cod_jackpot);
			
		} 
		
		// add pstn
		
		if (plan.getPstn_count() != null && plan.getPstn_count() > 0) {
			
			for (int i = 0; i < plan.getPstn_count(); i++) {
				
				CustomerOrderDetail cod_pstn = new CustomerOrderDetail();
				cod_pstn.setMonthly(false);
				if ("business".equals(customerOrder.getCustomer_type())) {
					cod_pstn.setDetail_name("BusinessLine");
				} else {
					cod_pstn.setDetail_name("HomeLine");
				}
				cod_pstn.setDetail_type("pstn");
				cod_pstn.setDetail_price(0d);
				cod_pstn.setDetail_unit(1);
				cod_pstn.setPstn_number(customerOrder.getTransition_porting_number());
				
				customerOrder.getCustomerOrderDetails().add(cod_pstn);
			}
		
		}
		
		// add voip
		
		if (plan.getVoip_count() != null && plan.getVoip_count() > 0) {
			
			for (int i = 0; i < plan.getVoip_count(); i++) {
				
				CustomerOrderDetail cod_voip = new CustomerOrderDetail();
				cod_voip.setMonthly(false);
				if ("business".equals(customerOrder.getCustomer_type())) {
					cod_voip.setDetail_name("VoIP Businessline");
				} else {
					cod_voip.setDetail_name("VoIP Homeline");
				}
				cod_voip.setDetail_type("voip");
				cod_voip.setDetail_price(0d);
				cod_voip.setDetail_unit(1);
				//cod_voip.setPstn_number(customerOrder.getTransition_porting_number());
				
				customerOrder.getCustomerOrderDetails().add(cod_voip);
			}
		
		}
		
		// add modem
		
		if (customerOrder.getHardwares() != null && customerOrder.getHardwares().size() > 0) {
			
			for (Hardware chd: customerOrder.getHardwares()) {
				
				if (chd != null) {
					
					CustomerOrderDetail cod_hd = new CustomerOrderDetail();
					cod_hd.setMonthly(false);
					cod_hd.setDetail_name(chd.getHardware_name());
					cod_hd.setDetail_price(chd.getHardware_price());
					
					if ("12 months contract".equals(customerOrder.getContract())) {
						if (customerOrder.getPrepay_months() == 1 || customerOrder.getPrepay_months() == 3 || customerOrder.getPrepay_months() == 6) {
							if (customerOrder.getSale_id() != null && customerOrder.getSale_id().intValue() == 20023 && "VDSL".equals(customer.getSelect_plan_type())) {
								modem_price = 0d;
								cod_hd.setDetail_price(modem_price);
							} else {
								if (chd.getId().intValue() == 3 || chd.getId().intValue() == 1) {
									modem_price = 0d;
									cod_hd.setDetail_price(modem_price);
								} else {
									Double temp = chd.getHardware_price()/2;
									modem_price = chd.getHardware_price() - temp.intValue();
									cod_hd.setDetail_price(new Double(modem_price.intValue()));
								}
							}
						} else if (customerOrder.getPrepay_months() == 12) {

							if("plan".equals(customerOrder.getPlan_or_hardware_discount())){
								modem_price = chd.getHardware_price();
								cod_hd.setDetail_price(modem_price);
							} else {
								modem_price = 0d;
								cod_hd.setDetail_price(modem_price);
							}
						}
					} else if ("open term".equals(customerOrder.getContract())) {
						
						System.out.println("--------------------------");
						System.out.println(customerOrder.getPlan_or_hardware_discount());
						System.out.println("--------------------------");

						if("plan".equals(customerOrder.getPlan_or_hardware_discount())){
							modem_price = chd.getHardware_price();
							cod_hd.setDetail_price(modem_price);
						} else {
							
							if (customerOrder.getPrepay_months() == 1) {
								modem_price = chd.getHardware_price();
								cod_hd.setDetail_price(modem_price);
							} else if (customerOrder.getPrepay_months() == 3 || customerOrder.getPrepay_months() == 6) {
								Double temp = (chd.getHardware_price()/12) * customerOrder.getPrepay_months();
								modem_price = chd.getHardware_price() - temp.intValue();
								cod_hd.setDetail_price(new Double(modem_price.intValue()));
							} else if (customerOrder.getPrepay_months() == 12) {
								modem_price = 0d;
								cod_hd.setDetail_price(0d);
							}
						}
						
					} else if ("24 months contract".equals(customerOrder.getContract())) {
						modem_price = 0d;
						cod_hd.setDetail_price(modem_price);
					}
					
					cod_hd.setDetail_unit(1);
					cod_hd.setIs_post(0);
					cod_hd.setDetail_type("hardware-router");
					
					customerOrder.setHardware_post(customerOrder.getHardware_post() != null ? customerOrder.getHardware_post() + 1 : 1);
					customerOrder.getCustomerOrderDetails().add(cod_hd);
				}
			
			}
		}
		
		// add-ons
		
		if ("VDSL".equals(customer.getSelect_plan_type())) {
			
			if (customerOrder.getSale_id() != null && customerOrder.getSale_id().intValue() == 10023) {
				
				CustomerOrderDetail cod_addons = new CustomerOrderDetail();
				cod_addons.setMonthly(true);
				cod_addons.setDetail_name("200 calling minutes of 40 countries");
				cod_addons.setDetail_type("present-calling-minutes");
				cod_addons.setDetail_desc("40 countries,voip");
				cod_addons.setDetail_calling_minute(200);
				cod_addons.setDetail_price(0d);
				cod_addons.setDetail_unit(1);
				
				customerOrder.getCustomerOrderDetails().add(cod_addons);
				
				CustomerOrderDetail cod_ipad = new CustomerOrderDetail();
				cod_ipad.setMonthly(false);
				cod_ipad.setDetail_name("Apple iPad Mini 16G");
				cod_ipad.setDetail_type("hardware");
				cod_ipad.setDetail_price(0d);
				cod_ipad.setDetail_unit(1);
				
				customerOrder.getCustomerOrderDetails().add(cod_ipad);
				
			} else if (customerOrder.getSale_id() != null && customerOrder.getSale_id().intValue() == 20023) {
				
				CustomerOrderDetail cod_addons = new CustomerOrderDetail();
				cod_addons.setMonthly(true);
				cod_addons.setDetail_name("100 calling minutes of 40 countries");
				cod_addons.setDetail_type("present-calling-minutes");
				cod_addons.setDetail_desc("40 countries,voip");
				cod_addons.setDetail_calling_minute(100);
				cod_addons.setDetail_price(0d);
				cod_addons.setDetail_unit(1);
				
				customerOrder.getCustomerOrderDetails().add(cod_addons);
				
				CustomerOrderDetail cod_hd = new CustomerOrderDetail();
				cod_hd.setMonthly(false);
				cod_hd.setDetail_name("1 TB Portable Hard Drive");
				cod_hd.setDetail_type("hardware");
				cod_hd.setDetail_price(0d);
				cod_hd.setDetail_unit(1);
				
				customerOrder.getCustomerOrderDetails().add(cod_hd);
				
			}
			
		}
		
		if (customerOrder.getAddons() != null && customerOrder.getAddons().size() > 0) {
			
			for (CustomerOrderDetail cod_addon: customerOrder.getAddons()) {
				cod_addon.setUser_id(customer.getCurrentOperateUserid());
				addons_price += cod_addon.getDetail_price();
				customerOrder.getCustomerOrderDetails().add(cod_addon);
			}
		}
		
		// promotion
		Double promotion_price = 0d;
		
		if (customer.getIr() != null) {
			
			if (customer.getIr().isIs_customer()) {
				customerOrder.setInviter_customer_id(Integer.parseInt(customer.getIr().getPromotion_code()));
				customerOrder.setInviter_rate(customer.getIr().getInviter_customer_rate());
				customerOrder.setInvitee_rate(customer.getIr().getInvitee_rate());
			} else if (customer.getIr().isIs_user()) {
				User userQuery = this.userMapper.selectUserById(Integer.parseInt(customer.getIr().getPromotion_code()));
				if("sales".equals(userQuery.getUser_role()) && "agent".equals(userQuery.getUser_role())){
					customerOrder.setSale_id(userQuery.getId());
				} else {
					customerOrder.setUser_id(userQuery.getId());
				}
				customerOrder.setInviter_user_id(Integer.parseInt(customer.getIr().getPromotion_code()));
				customerOrder.setInviter_rate(customer.getIr().getInviter_user_rate());
				customerOrder.setInvitee_rate(customer.getIr().getInvitee_rate());
			}			
			
			CustomerOrderDetail cod_promotion = new CustomerOrderDetail();
			cod_promotion.setMonthly(false);
			cod_promotion.setDetail_name("Promotion Discount");
			cod_promotion.setDetail_type("discount");
			cod_promotion.setDetail_desc(customer.getIr().getInvitee_rate().intValue() + "% off the total price. all-forms");
			
			Double total = 0d;
			if ("personal".equals(customerOrder.getCustomer_type())) {
				total = plan_price * customerOrder.getPrepay_months() + service_price.intValue() + modem_price.intValue() - discount_price.intValue() + addons_price.intValue();
			} else if ("business".equals(customerOrder.getCustomer_type())) {
				total = (plan_price * customerOrder.getPrepay_months() + service_price.intValue() + modem_price.intValue() + addons_price.intValue()) * 1.15 - discount_price.intValue();
			}
			
			promotion_price = total * customer.getIr().getInvitee_rate()/100;
			cod_promotion.setDetail_price(new Double(promotion_price.intValue()));
			cod_promotion.setDetail_unit(1);
			
			customerOrder.getCustomerOrderDetails().add(cod_promotion);
			
		}
		
		// monthly or one-off detail
		
		for (CustomerOrderDetail cod: customerOrder.getCustomerOrderDetails()) {
			if (cod.getMonthly()!=null && cod.getMonthly()) {
				customerOrder.getMonthly_cods().add(cod);
			} else {
				customerOrder.getOneoff_cods().add(cod);
			}
		}
		
		if ("personal".equals(customerOrder.getCustomer_type())) {
			customerOrder.setOrder_total_price(plan_price * customerOrder.getPrepay_months() + service_price.intValue() + modem_price.intValue() + addons_price.intValue() - discount_price.intValue() - promotion_price.intValue());
		} else if ("business".equals(customerOrder.getCustomer_type())) {
			customerOrder.setOrder_total_price((plan_price * customerOrder.getPrepay_months() + service_price.intValue() + modem_price.intValue() + addons_price.intValue()) * 1.15 - discount_price.intValue() - promotion_price.intValue());
		}
		
	}
	
	@Transactional
	public JSONBean<String> doTopupAccountCreditByVoucher(int customer_id
			,String pin_number
			,HttpServletRequest req
			,BillingService billingService){

		JSONBean<String> json = new JSONBean<String>();
		
		Voucher v = new Voucher();
		v.getParams().put("card_number", pin_number);
		v.getParams().put("status", "unused");
		List<Voucher> vs = billingService.queryVouchers(v);
		v = vs!=null && vs.size()>0 ? vs.get(0) : null;
		
		if(v==null){
			json.getErrorMap().put("alert-error", "We Haven't found this Voucher, or it had been used!");
			return json;
		}
		
		Double face_value = v.getFace_value();

		Customer cQuery = new Customer();
		cQuery.getParams().put("id", customer_id);
		Customer c = this.queryCustomer(cQuery);
		c.setBalance(TMUtils.bigAdd(c.getBalance()!=null ? c.getBalance() : 0d, face_value));
		c.getParams().put("id", customer_id);
		this.editCustomer(c);
		
		json.getSuccessMap().put("alert-success", "Voucher defrayed successfull!");
		v.setStatus("used");
		v.getParams().put("serial_number", v.getSerial_number());
		v.setCustomer_id(customer_id);
		String process_way = "# - "+v.getCard_number();
		billingService.editVoucher(v);

		User userSession = (User) req.getSession().getAttribute("userSession");
		CustomerTransaction ct = new CustomerTransaction();
		ct.setAmount(face_value);
		ct.setAmount_settlement(face_value);
		ct.setCard_name(process_way);
		ct.setCustomer_id(customer_id);
		ct.setTransaction_date(new Date());
		ct.setCurrency_input("Voucher");
		ct.setExecutor(userSession.getId());
		this.createCustomerTransaction(ct);

		return json;
	}
	
	public void sendCustomerSMSByCellphone(String cellphone, String content){

		this.smserService.sendSMSByAsynchronousMode(cellphone, content);
	}
	
	
	
	
	@Transactional
	public InviteRates applyPromotionCode(String code) {
	
		InviteRates ir = null;
		
		Customer cQ = new Customer();
		cQ.getParams().put("where", "query_exist_customer_by_id");
		cQ.getParams().put("id", code);
		
		int count = this.customerMapper.selectExistCustomer(cQ);
		if (count > 0) {
			ir = this.inviteRatesMapper.selectInviteRates(new InviteRates()).get(0);
			ir.setIs_customer(true);
			ir.setPromotion_code(code);
		} else {
			User uQ = new User();
			uQ.getParams().put("id", code);
			count = this.userMapper.selectExistUser(uQ);
			if (count > 0) {
				ir = this.inviteRatesMapper.selectInviteRates(new InviteRates()).get(0);
				ir.setIs_user(true);
				ir.setPromotion_code(code);
			}
		}
		
		return ir;
	}
	
	
	
	/**
	 * BEGIN CustomerCredit
	 */
	
	public List<CustomerCredit> queryCustomerCredits(CustomerCredit cc){
		return this.customerCreditMapper.selectCustomerCredits(cc);
	}
	
	public CustomerCredit queryCustomerCredit(CustomerCredit cc){
		List<CustomerCredit> ccs = this.queryCustomerCredits(cc);
		return ccs!=null && ccs.size()>0 ? ccs.get(0) : null;
	}
	
	public Page<CustomerCredit> queryCustomerCreditsByPage(Page<CustomerCredit> page){
		page.setTotalRecord(this.customerCreditMapper.selectCustomerCreditsSum(page));
		page.setResults(this.customerCreditMapper.selectCustomerCreditsByPage(page));
		return page;
	}

	@Transactional
	public void editCustomerCredit(CustomerCredit cc){
		this.customerCreditMapper.updateCustomerCredit(cc);
	}

	@Transactional
	public void removeCustomerCreditCardById(int id){
		this.customerCreditMapper.deleteCustomerCreditCardById(id);
	}

	@Transactional
	public void createCustomerCredit(CustomerCredit cc){
		this.customerCreditMapper.insertCustomerCredit(cc);
	}
	
	/**
	 * END CustomerCredit
	 */
	
	
	/**
	 * BEGIN CustomerDDPay
	 */
	
	public List<CustomerDDPay> queryCustomerDDPays(CustomerDDPay cddpay){
		return this.customerDDPayMapper.selectCustomerDDPay(cddpay);
	}
	
	public CustomerDDPay queryCustomerDDPay(CustomerDDPay cddpay){
		List<CustomerDDPay> cddpays = this.queryCustomerDDPays(cddpay);
		return cddpays!=null && cddpays.size()>0 ? cddpays.get(0) : null;
	}
	
	public Page<CustomerDDPay> queryCustomerDDPaysByPage(Page<CustomerDDPay> page){
		page.setTotalRecord(this.customerDDPayMapper.selectCustomerDDPaysSum(page));
		page.setResults(this.customerDDPayMapper.selectCustomerDDPaysByPage(page));
		return page;
	}

	@Transactional
	public void editCustomerDDPay(CustomerDDPay cddpay){
		this.customerDDPayMapper.updateCustomerDDPay(cddpay);
	}

	@Transactional
	public void removeCustomerDDPayById(int id){
		this.customerDDPayMapper.deleteCustomerDDPayById(id);
	}

	@Transactional
	public void createCustomerDDPay(CustomerDDPay cddpay){
		this.customerDDPayMapper.insertCustomerDDPay(cddpay);
	}
	
	/**
	 * END CustomerDDPay
	 */
	
	
	/**
	 * BEGIN CustomerOrderProvisionChecklist
	 */
	
	public List<CustomerOrderProvisionChecklist> queryCustomerOrderProvisionChecklists(CustomerOrderProvisionChecklist copc){
		return this.customerOrderProvisionChecklistMapper.selectCustomerOrderProvisionChecklist(copc);
	}
	
	public CustomerOrderProvisionChecklist queryCustomerOrderProvisionChecklist(CustomerOrderProvisionChecklist copc){
		List<CustomerOrderProvisionChecklist> copcs = this.queryCustomerOrderProvisionChecklists(copc);
		return copcs!=null && copcs.size()>0 ? copcs.get(0) : null;
	}
	
	public Page<CustomerOrderProvisionChecklist> queryCustomerOrderProvisionChecklistsByPage(Page<CustomerOrderProvisionChecklist> page){
		page.setTotalRecord(this.customerOrderProvisionChecklistMapper.selectCustomerOrderProvisionChecklistsSum(page));
		page.setResults(this.customerOrderProvisionChecklistMapper.selectCustomerOrderProvisionChecklistsByPage(page));
		return page;
	}

	@Transactional
	public void editCustomerOrderProvisionChecklist(CustomerOrderProvisionChecklist copc){
		this.customerOrderProvisionChecklistMapper.updateCustomerOrderProvisionChecklist(copc);
	}

	@Transactional
	public void removeCustomerOrderProvisionChecklistById(int id){
		this.customerOrderProvisionChecklistMapper.deleteCustomerOrderProvisionChecklistById(id);
	}

	@Transactional
	public void createCustomerOrderProvisionChecklist(CustomerOrderProvisionChecklist copc){
		this.customerOrderProvisionChecklistMapper.insertCustomerOrderProvisionChecklist(copc);
	}
	
	/**
	 * END CustomerOrderProvisionChecklist
	 */
	
	
	/**
	 * BEGIN CustomerOrderOnsite
	 */
	
	public List<CustomerOrderOnsite> queryCustomerOrderOnsites(CustomerOrderOnsite coo){
		return this.customerOrderOnsiteMapper.selectCustomerOrderOnsite(coo);
	}
	
	public CustomerOrderOnsite queryCustomerOrderOnsite(CustomerOrderOnsite coo){
		List<CustomerOrderOnsite> coos = this.queryCustomerOrderOnsites(coo);
		return coos!=null && coos.size()>0 ? coos.get(0) : null;
	}
	
	public Page<CustomerOrderOnsite> queryCustomerOrderOnsitesByPage(Page<CustomerOrderOnsite> page){
		page.setTotalRecord(this.customerOrderOnsiteMapper.selectCustomerOrderOnsitesSum(page));
		page.setResults(this.customerOrderOnsiteMapper.selectCustomerOrderOnsitesByPage(page));
		return page;
	}

	@Transactional
	public void editCustomerOrderOnsite(CustomerOrderOnsite coo){
		this.customerOrderOnsiteMapper.updateCustomerOrderOnsite(coo);
	}

	@Transactional
	public void removeCustomerOrderOnsiteById(int id){
		this.customerOrderOnsiteMapper.deleteCustomerOrderOnsiteById(id);
	}

	@Transactional
	public void createCustomerOrderOnsite(CustomerOrderOnsite coo){
		this.customerOrderOnsiteMapper.insertCustomerOrderOnsite(coo);
	}
	
	/**
	 * END CustomerOrderOnsite
	 */
	
	
	/**
	 * BEGIN CustomerOrderOnsiteDetail
	 */
	
	public List<CustomerOrderOnsiteDetail> queryCustomerOrderOnsiteDetails(CustomerOrderOnsiteDetail cood){
		return this.customerOrderOnsiteDetailMapper.selectCustomerOrderOnsiteDetail(cood);
	}
	
	public CustomerOrderOnsiteDetail queryCustomerOrderOnsiteDetail(CustomerOrderOnsiteDetail cood){
		List<CustomerOrderOnsiteDetail> coods = this.queryCustomerOrderOnsiteDetails(cood);
		return coods!=null && coods.size()>0 ? coods.get(0) : null;
	}
	
	public Page<CustomerOrderOnsiteDetail> queryCustomerOrderOnsiteDetailsByPage(Page<CustomerOrderOnsiteDetail> page){
		page.setTotalRecord(this.customerOrderOnsiteDetailMapper.selectCustomerOrderOnsiteDetailsSum(page));
		page.setResults(this.customerOrderOnsiteDetailMapper.selectCustomerOrderOnsiteDetailsByPage(page));
		return page;
	}

	@Transactional
	public void editCustomerOrderOnsiteDetail(CustomerOrderOnsiteDetail cood){
		this.customerOrderOnsiteDetailMapper.updateCustomerOrderOnsiteDetail(cood);
	}

	@Transactional
	public void removeCustomerOrderOnsiteDetailById(int id){
		this.customerOrderOnsiteDetailMapper.deleteCustomerOrderOnsiteDetailById(id);
	}

	@Transactional
	public void createCustomerOrderOnsiteDetail(CustomerOrderOnsiteDetail cood){
		this.customerOrderOnsiteDetailMapper.insertCustomerOrderOnsiteDetail(cood);
	}
	
	/**
	 * END CustomerOrderOnsiteDetail
	 */
	
	
	/**
	 * BEGIN Customer
	 */
	
	@Transactional
	public boolean customerMigrate(Integer customer_id,
			Integer migrate_customer_id,
			BillingService billingService){
		
		boolean isMigrate = false;
		
		Customer cMigrateQuery = new Customer();
		Customer cTargetQuery = new Customer();
		Customer cUpdate = new Customer();
		CustomerBillingLog cblUpdate =new CustomerBillingLog();
		CustomerCredit ccUpdate = new CustomerCredit();
		CustomerDDPay cddpayUpdate = new CustomerDDPay();
		CustomerInvoice ciUpdate = new CustomerInvoice();
		CustomerOrder coUpdate = new CustomerOrder();
		CustomerOrderOnsite cooUpdate = new CustomerOrderOnsite();
		CustomerServiceRecord csrUpdate = new CustomerServiceRecord();
		CustomerTransaction ctUpdate = new CustomerTransaction();
		EarlyTerminationCharge etcUpdate = new EarlyTerminationCharge();
		Ticket tUpdate = new Ticket();
		Voucher vUpdate = new Voucher();
		VoucherBannedList vblUpdate = new VoucherBannedList();
		TerminationRefund trUpdate = new TerminationRefund();
		
		cblUpdate.getParams().put("customer_id", migrate_customer_id);
		List<CustomerBillingLog> cbls = billingService.queryCustomerBillingLogs(cblUpdate);
		if(cbls!=null && cbls.size()>0){
			cblUpdate.setCustomer_id(customer_id);
			billingService.editCustomerBillingLog(cblUpdate);
			isMigrate = true;
		}
		ccUpdate.getParams().put("customer_id", migrate_customer_id);
		List<CustomerCredit> ccs = this.queryCustomerCredits(ccUpdate);
		if(ccs!=null && ccs.size()>0){
			ccUpdate.setCustomer_id(customer_id);
			this.editCustomerCredit(ccUpdate);
			isMigrate = true;
		}
		cddpayUpdate.getParams().put("customer_id", migrate_customer_id);
		List<CustomerDDPay> cddpays = this.queryCustomerDDPays(cddpayUpdate);
		if(cddpays!=null && cddpays.size()>0){
			cddpayUpdate.setCustomer_id(customer_id);
			this.editCustomerDDPay(cddpayUpdate);
			isMigrate = true;
		}
		ciUpdate.getParams().put("customer_id", migrate_customer_id);
		List<CustomerInvoice> cis = this.queryCustomerInvoices(ciUpdate);
		if(cis!=null && cis.size()>0){
			ciUpdate.setCustomer_id(customer_id);
			this.editCustomerInvoice(ciUpdate);
			isMigrate = true;
		}
		coUpdate.getParams().put("customer_id", migrate_customer_id);
		List<CustomerOrder> cos = this.queryCustomerOrders(coUpdate);
		if(cos!=null && cos.size()>0){
			coUpdate.setCustomer_id(customer_id);
			this.editCustomerOrder(coUpdate);
			isMigrate = true;
		}
		cooUpdate.getParams().put("customer_id", migrate_customer_id);
		List<CustomerOrderOnsite> coos = this.queryCustomerOrderOnsites(cooUpdate);
		if(coos!=null && coos.size()>0){
			cooUpdate.setCustomer_id(customer_id);
			this.editCustomerOrderOnsite(cooUpdate);
			isMigrate = true;
		}
		csrUpdate.getParams().put("customer_id", migrate_customer_id);
		List<CustomerServiceRecord> csrs = this.queryCustomerServiceRecord(csrUpdate);
		if(csrs!=null && csrs.size()>0){
			csrUpdate.setCustomer_id(customer_id);
			this.editCustomerServiceRecord(csrUpdate);
			isMigrate = true;
		}
		ctUpdate.getParams().put("customer_id", migrate_customer_id);
		List<CustomerTransaction> cts = this.queryCustomerTransactions(ctUpdate);
		if(cts!=null && cts.size()>0){
			ctUpdate.setCustomer_id(customer_id);
			this.editCustomerTransaction(ctUpdate);
			isMigrate = true;
		}
		etcUpdate.getParams().put("customer_id", migrate_customer_id);
		List<EarlyTerminationCharge> etcs = billingService.queryEarlyTerminationCharge(etcUpdate);
		if(etcs!=null && etcs.size()>0){
			etcUpdate.setCustomer_id(customer_id);
			billingService.editEarlyTerminationCharge(etcUpdate);
			isMigrate = true;
		}
		tUpdate.getParams().put("customer_id", migrate_customer_id);
		List<Ticket> ts = this.queryTicket(tUpdate);
		if(ts!=null && ts.size()>0){
			tUpdate.setCustomer_id(customer_id);
			this.editTicket(tUpdate);
			isMigrate = true;
		}
		vUpdate.getParams().put("customer_id", migrate_customer_id);
		List<Voucher> vs = billingService.queryVouchers(vUpdate);
		if(vs!=null && vs.size()>0){
			vUpdate.setCustomer_id(customer_id);
			billingService.editVoucher(vUpdate);
			isMigrate = true;
		}
		vblUpdate.getParams().put("customer_id", migrate_customer_id);
		List<VoucherBannedList> vbls = billingService.queryVoucherBannedList(vblUpdate);
		if(vbls!=null && vbls.size()>0){
			vblUpdate.setCustomer_id(customer_id);
			billingService.editVoucherBannedList(vblUpdate);
			isMigrate = true;
		}
		trUpdate.getParams().put("customer_id", migrate_customer_id);
		List<TerminationRefund> trs = billingService.queryTerminationRefund(trUpdate);
		if(trs!=null && trs.size()>0){
			trUpdate.setCustomer_id(customer_id);
			billingService.editTerminationRefund(trUpdate);
			isMigrate = true;
		}
		
		if(isMigrate){

			cMigrateQuery.getParams().put("id", migrate_customer_id);
			cMigrateQuery = this.queryCustomer(cMigrateQuery);
			cTargetQuery.getParams().put("id", customer_id);
			cTargetQuery = this.queryCustomer(cTargetQuery);
			
			cUpdate.getParams().put("id", customer_id);
			cUpdate.setBalance(TMUtils.bigAdd(cTargetQuery.getBalance()==null ? 0d : cTargetQuery.getBalance(), cMigrateQuery.getBalance()==null ? 0d : cMigrateQuery.getBalance()));
			this.editCustomer(cUpdate);
			
			cMigrateQuery.getParams().put("id", migrate_customer_id);
			cMigrateQuery.setBalance(0d);
			this.editCustomer(cMigrateQuery);
			
		}
		
		return isMigrate;
		
	}
	
	/**
	 * END Customer
	 */
	
	
	/**
	 * BEGIN CustomerOrderChorusAddon
	 */

	public void createCustomerOrderChorusAddon(CustomerOrderChorusAddon coca){
		this.customerOrderChorusAddonMapper.insertCustomerOrderChorusAddon(coca);
	}
	
	public CustomerOrderChorusAddon queryCustomerOrderChorusAddon(CustomerOrderChorusAddon coca) {
		List<CustomerOrderChorusAddon> cocas = this.customerOrderChorusAddonMapper.selectCustomerOrderChorusAddon(coca);
		return cocas!=null && cocas.size()>0 ? cocas.get(0) : null;
	}
	
	public List<CustomerOrderChorusAddon> queryCustomerOrderChorusAddons(CustomerOrderChorusAddon coca) {
		return this.customerOrderChorusAddonMapper.selectCustomerOrderChorusAddon(coca);
	}
	
	@Transactional
	public Page<CustomerOrderChorusAddon> queryCustomerOrderChorusAddonsByPage(Page<CustomerOrderChorusAddon> page){
		page.setTotalRecord(this.customerOrderChorusAddonMapper.selectCustomerOrderChorusAddonsSum(page));
		page.setResults(this.customerOrderChorusAddonMapper.selectCustomerOrderChorusAddonsByPage(page));
		return page;
	}
	
	public int queryCustomerOrderChorusAddonsSumByPage(Page<CustomerOrderChorusAddon> page){
		return this.customerOrderChorusAddonMapper.selectCustomerOrderChorusAddonsSum(page);
	}
	
	@Transactional
	public void editCustomerOrderChorusAddon(CustomerOrderChorusAddon coca){
		this.customerOrderChorusAddonMapper.updateCustomerOrderChorusAddon(coca);
	}
	
	@Transactional
	public void removeCustomerOrderChorusAddonById(int id){
		this.customerOrderChorusAddonMapper.deleteCustomerOrderChorusAddonById(id);
	}
	
	@Transactional
	public void removeCustomerOrderChorusAddonByOrderId(int order_id){
		this.customerOrderChorusAddonMapper.deleteCustomerOrderChorusAddonByOrderId(order_id);
	}
	
	/**
	 * END CustomerOrderChorusAddon
	 */
	
	
	/**
	 * BEGIN CustomerOrderNZCallingRatesSetting
	 */

	public void createCustomerOrderNZCallingRatesSetting(CustomerOrderNZCallingRatesSetting conzcrs){
		this.customerOrderNZCallingRatesSettingMapper.insertCustomerOrderNZCallingRatesSetting(conzcrs);
	}
	
	public CustomerOrderNZCallingRatesSetting queryCustomerOrderNZCallingRatesSetting(CustomerOrderNZCallingRatesSetting conzcrs) {
		List<CustomerOrderNZCallingRatesSetting> conzcrss = this.customerOrderNZCallingRatesSettingMapper.selectCustomerOrderNZCallingRatesSetting(conzcrs);
		return conzcrss!=null && conzcrss.size()>0 ? conzcrss.get(0) : null;
	}
	
	public List<CustomerOrderNZCallingRatesSetting> queryCustomerOrderNZCallingRatesSettings(CustomerOrderNZCallingRatesSetting conzcrs) {
		return this.customerOrderNZCallingRatesSettingMapper.selectCustomerOrderNZCallingRatesSetting(conzcrs);
	}
	
	@Transactional
	public Page<CustomerOrderNZCallingRatesSetting> queryCustomerOrderNZCallingRatesSettingsByPage(Page<CustomerOrderNZCallingRatesSetting> page){
		page.setTotalRecord(this.customerOrderNZCallingRatesSettingMapper.selectCustomerOrderNZCallingRatesSettingsSum(page));
		page.setResults(this.customerOrderNZCallingRatesSettingMapper.selectCustomerOrderNZCallingRatesSettingsByPage(page));
		return page;
	}
	
	public int queryCustomerOrderNZCallingRatesSettingsSumByPage(Page<CustomerOrderNZCallingRatesSetting> page){
		return this.customerOrderNZCallingRatesSettingMapper.selectCustomerOrderNZCallingRatesSettingsSum(page);
	}
	
	@Transactional
	public void editCustomerOrderNZCallingRatesSetting(CustomerOrderNZCallingRatesSetting conzcrs){
		this.customerOrderNZCallingRatesSettingMapper.updateCustomerOrderNZCallingRatesSetting(conzcrs);
	}
	
	@Transactional
	public void removeCustomerOrderNZCallingRatesSettingById(int id){
		this.customerOrderNZCallingRatesSettingMapper.deleteCustomerOrderNZCallingRatesSettingById(id);
	}
	
	@Transactional
	public void removeCustomerOrderNZCallingRatesSettingByOrderId(int order_id){
		this.customerOrderNZCallingRatesSettingMapper.deleteCustomerOrderNZCallingRatesSettingByOrderId(order_id);
	}
	
	/**
	 * END CustomerOrderNZCallingRatesSetting
	 */

	
	
	/**
	 * BEGIN CustomerOrderBroadbandASID
	 */
	

	public void createCustomerOrderBroadbandASID(CustomerOrderBroadbandASID cobasid){
		this.customerOrderBroadbandASIDMapper.insertCustomerOrderBroadbandASID(cobasid);
	}
	
	public CustomerOrderBroadbandASID queryCustomerOrderBroadbandASID(CustomerOrderBroadbandASID cobasid) {
		List<CustomerOrderBroadbandASID> cobasids = this.customerOrderBroadbandASIDMapper.selectCustomerOrderBroadbandASID(cobasid);
		return cobasids!=null && cobasids.size()>0 ? cobasids.get(0) : null;
	}
	
	public List<CustomerOrderBroadbandASID> queryCustomerOrderBroadbandASIDs(CustomerOrderBroadbandASID cobasid) {
		return this.customerOrderBroadbandASIDMapper.selectCustomerOrderBroadbandASID(cobasid);
	}
	
	@Transactional
	public Page<CustomerOrderBroadbandASID> queryCustomerOrderBroadbandASIDsByPage(Page<CustomerOrderBroadbandASID> page){
		page.setTotalRecord(this.customerOrderBroadbandASIDMapper.selectCustomerOrderBroadbandASIDsSum(page));
		page.setResults(this.customerOrderBroadbandASIDMapper.selectCustomerOrderBroadbandASIDsByPage(page));
		return page;
	}
	
	public int queryCustomerOrderBroadbandASIDsSumByPage(Page<CustomerOrderBroadbandASID> page){
		return this.customerOrderBroadbandASIDMapper.selectCustomerOrderBroadbandASIDsSum(page);
	}
	
	@Transactional
	public void editCustomerOrderBroadbandASID(CustomerOrderBroadbandASID cobasid){
		this.customerOrderBroadbandASIDMapper.updateCustomerOrderBroadbandASID(cobasid);
	}
	
	@Transactional
	public void removeCustomerOrderBroadbandASIDById(int id){
		this.customerOrderBroadbandASIDMapper.deleteCustomerOrderBroadbandASIDById(id);
	}
	
//	@Transactional
//	public void removeCustomerOrderBroadbandASIDByOrderId(int order_id){
//		this.customerOrderChorusAddonMapper.deleteCustomerOrderBroadbandASIDByOrderId(order_id);
//	}
	
	/**
	 * END CustomerOrderChorusAddon
	 */
	
	
}
