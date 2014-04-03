package com.tm.broadband.pdf;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

import com.itextpdf.text.DocumentException;
import com.tm.broadband.model.Customer;
import com.tm.broadband.model.CustomerCredit;
import com.tm.broadband.model.CustomerOrder;
import com.tm.broadband.service.SmserService;

public class TestAll {
	

	@Autowired
	private static SmserService smserService;

	public static void main(String[] args) throws DocumentException, IOException {

		/*
		 * Test TMUtil.mailAtValueRetriever() method begin
		 */

//		 Notification noti = new Notification();
//		 noti.setTitle(" We are kind to notice that your new invoice #@<invoice_id> has just generated, to find out more details please check the attached PDF file in the attachment. Please keep an eye on the invoice’s due date. If you have any questions about the invoice, please don’t hastate to contact us: @<company_telephone> Kind regards, @<company_name>: @<company_domain>");
//		 noti.setContent(" We are kind to notice that your new invoice #@<invoice_id> has just generated, to find out more details please check the attached PDF file in the attachment. Please keep an eye on the invoice’s due date. If you have any questions about the invoice, please don’t hastate to contact us: @<company_telephone> Kind regards, @<company_name>: @<company_domain>");
//		 CompanyDetail company = new CompanyDetail();
//		 company.setTelephone("01212131212");
//		 company.setName("Total Mobile Solution");
//		 company.setDomain("www.cyberpark.co.nz");
//		 Customer cus = new Customer();
//		 cus.setLogin_name("Kanny");
//		 CustomerInvoice inv = new CustomerInvoice();
//		 inv.setId(70004);
//		 System.out.println("----------Before Begin----------");
//		 System.out.println(noti.getTitle());
//		 System.out.println(noti.getContent());
//		 System.out.println("----------Before End----------");
//		 TMUtils.mailAtValueRetriever(noti, cus, inv, company);
//		 System.out.println("----------After Begin----------");
//		 System.out.println(noti.getTitle());
//		 System.out.println(noti.getContent());
//		 System.out.println("----------After End----------");

		/*
		 * Test TMUtil.mailAtValueRetriever() method end
		 */
//		List<RegisterCustomer> registerCustomers = new ArrayList<RegisterCustomer>();
//		TMUtils.thisWeekDateForRegisterStatistic(registerCustomers);
//		// SimpleDateFormat("y年M月d日 E H时m分s秒",Locale.CHINA);
//
//		for (RegisterCustomer registerCustomer : registerCustomers) {
//			System.out.print(registerCustomer.getRegisterDate() + "\t");
//			System.out.println(registerCustomer.getRegisterCount());
//		}
		
//		Integer year = Calendar.YEAR;
//		Integer month = Calendar.MONTH;
//		
//		List<RegisterCustomer> registerCustomers = new ArrayList<RegisterCustomer>();
//		TMUtils.thisMonthDateForRegisterStatistic(year, month, registerCustomers);
//		for (RegisterCustomer registerCustomer : registerCustomers) {
//			System.out.print(registerCustomer.getRegisterMonthDate_str()+"\t");
//			System.out.println(registerCustomer.getRegisterCount());
//		}
//		
//		Calendar c = Calendar.getInstance(Locale.CHINA);
//		System.out.println(c.get(Calendar.YEAR));
//		CompanyDetail company = new CompanyDetail();
//		company.setName("Total Mobile Solution");
//		Customer customer = new Customer();
//		customer.setCellphone("02102387392");
//		customer.setFirst_name("Dong");
//		customer.setLast_name("Chen");
//		Notification notification = new Notification();
//		notification.setContent("Dear @<customer_first_name> @<customer_last_name>, This is a reminder that your next payment for @<company_name> Service is coming up. We have attached a copy of your invoice in PDF format and sent it to your email. Thank you for choosing @<company_name>!!");
//		TMUtils.mailAtValueRetriever(notification, customer, company);
//		smserService.sendSMSByAsynchronousMode(customer, notification);
		

//		// month * unit
//		int nextInvoiceMonth = 5;
//		int nextInvoiceDay = 0;
//		Calendar calNextInvoiceDay = Calendar.getInstance();
//		calNextInvoiceDay.setTime(new Date());
//		calNextInvoiceDay.add(Calendar.MONTH, nextInvoiceMonth);
//		calNextInvoiceDay.add(Calendar.DAY_OF_MONTH, nextInvoiceDay);
//		
//		System.out.println(TMUtils.dateFormatYYYYMMDD(calNextInvoiceDay.getTime()));
//		System.out.println(System.getProperty("user.dir")+File.separator);

		
		/**
		 * BEGIN TEST OrderPDFCreator
		 */
//		Customer c = new Customer();
//		Organization org = new Organization();
//		CustomerOrder co = new CustomerOrder();
//		CustomerOrderDetail cod = new CustomerOrderDetail();
//		List<CustomerOrderDetail> cods = new ArrayList<CustomerOrderDetail>();
//		
//		// CUSTOMER type
//		c.setCustomer_type("business");
//		// ORDER Broadband Type
//		// Necessary if broadband type is transition
//		co.setOrder_broadband_type("transition");
//		co.setTransition_provider_name("Telecom");
//		co.setTransition_account_holder_name("David Li");
//		co.setTransition_account_number("1234 4321 1234 4321");
//		co.setTransition_porting_number("9876 6789 9876 6789");
//		
//		// set customer
//		c.setId(60089);
//		c.setTitle("Mr");
//		c.setLogin_name("steven1989930");
//		c.setFirst_name("Dong");
//		c.setLast_name("Chen");
//		c.setEmail("davidli@gmail.com");
//		c.setCellphone("021 1234567");
//		c.setPhone("021 1234567");
//		c.setAddress("7 Skeates Ave, Mt roskill, Auckland");
//		c.setBirth(TMUtils.parseDateYYYYMMDD("1970-04-01"));
//		c.setDriver_licence("5a. DM670646     5b. 241");
//		c.setPassport("G4041765");
//		c.setCountry("New Zealand");
//		
//		// set org
//		org.setOrg_name("CyberPark");
//		org.setOrg_type("NZ Incoporated Company");
//		org.setOrg_trading_name("NZ Limited");
//		org.setOrg_register_no("NZ19876542");
//		org.setOrg_incoporate_date(new Date());
//		org.setHolder_name("Steve");
//		org.setHolder_job_title("Manager");
//		org.setHolder_phone("0210210213");
//		org.setHolder_email("Steve@gmail.com");
//
//		// set order detail
//		// SET PLAN DETAIL
//		cod.setDetail_name("ADSL Naked 150 GB Plan");
//		cod.setDetail_type("plan-term");
//		cod.setDetail_price(89.0d);
//		cod.setDetail_data_flow(100L);
//		cod.setDetail_term_period(24);
//		cod.setDetail_unit(3);
//		cods.add(cod);
//		// SET ADD ON DETAIL
//		cod = new CustomerOrderDetail();
//		cod.setDetail_name("Broadband New Connection");
//		cod.setDetail_type("new-connection");
//		cod.setDetail_price(99.0d);
//		cod.setDetail_unit(1);
//		cods.add(cod);
//		cod = new CustomerOrderDetail();
//		cod.setDetail_name("TP - LINK 150Mbps Wireless N ADSL2+ Modem Router");
//		cod.setDetail_type("hardware-router");
//		cod.setDetail_price(49.0d);
//		cod.setDetail_unit(2);
//		cods.add(cod);
//		
//		// set order
//		co.setId(60005);
//		co.setOrder_create_date(new Date());
//		co.setCustomerOrderDetails(cods);
//		
//		// call OrderPDFCreator
//		OrderPDFCreator oPDFCreator = new OrderPDFCreator();
//		oPDFCreator.setCustomer(c);
//		oPDFCreator.setOrg(org);
//		oPDFCreator.setCustomerOrder(co);
//		
//		// create order PDF
//		oPDFCreator.create();
		/**
		 * END TEST OrderPDFCreator
		 */

		/**
		 * BEGIN TEST CreditPDFCreator
		 */
		Customer c = new Customer();
		CustomerCredit cc = new CustomerCredit();
		cc.setCard_type("MASTERCARD");
		CustomerOrder co = new CustomerOrder();
		
		// set customer
		c.setId(60089);
		c.setFirst_name("Dong");
		c.setLast_name("Chen");
		c.setAddress("7 Skeates Ave, Mt roskill, Auckland 1041");
		c.setCellphone("021 1234567");
		c.setPhone("021 1234567");
		c.setEmail("davidli@gmail.com");
		
		co.setId(60005);
		
		// set customer credit
		cc.setHolder_name("COOK");
		cc.setCard_number("9999 8888 7777 6666");
		cc.setSecurity_code("214");
		cc.setExpiry_date("2016-09-08");
		
		// call OrderPDFCreator
		CreditPDFCreator cPDFCreator = new CreditPDFCreator();
		cPDFCreator.setCustomer(c);
		cPDFCreator.setCc(cc);
		cPDFCreator.setCo(co);
		
		// create order PDF
		cPDFCreator.create();
		/**
		 * END TEST CreditPDFCreator
		 */
		
	}
	

}
