package com.tm.broadband.pdf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.tm.broadband.model.CompanyDetail;
import com.tm.broadband.model.CustomerCallRecord;
import com.tm.broadband.model.CustomerInvoice;
import com.tm.broadband.model.CustomerInvoiceDetail;
import com.tm.broadband.model.CustomerOrder;
import com.tm.broadband.util.TMUtils;
import com.tm.broadband.util.itext.ITextFont;
import com.tm.broadband.util.itext.ITextUtils;

/** 
* Generates Invoice PDF
* 
* @author DONG CHEN
*/ 
public class InvoicePDFCreator extends ITextUtils {

	private CompanyDetail companyDetail;
	private CustomerOrder co;
	private CustomerInvoice currentCustomerInvoice;
    private CustomerInvoice lastCustomerInvoice;
    private CustomerInvoice lastCustomerInvoiceStatistic;

	private BaseColor titleBGColor = new BaseColor(92,184,92);
	private BaseColor totleChequeAmountBGColor = new BaseColor(110,110,110);
	
	private Integer globalBorderWidth = 0;
	private String pdf_resources_path = TMUtils.createPath("broadband" + File.separator + "pdf_resources" + File.separator);
	
	// BALANCE
	private Double totalBalance = 0d;
	// BALANCE BEFORE TAX
	private Double beforeTaxAmount = 0d;
	// TAX FROM BALANCE
	private Double taxAmount = 0d;
	
	private Double totalCreditBack = 0d;
	private Double totalPayableAmount = 0d;
	private Double totalFinalPayableAmount = 0d;

    private Double previousBalance = 0d;
    private Double currentFinalPayable = 0d;
	
    boolean isBusiness = false;
    
    private Map<String, List<CustomerCallRecord>> ccrsMap = new LinkedHashMap<String, List<CustomerCallRecord>>();
    private List<CustomerCallRecord> ccrPSTNStatistics = new ArrayList<CustomerCallRecord>();
    private List<CustomerCallRecord> ccrVoIPStatistics = new ArrayList<CustomerCallRecord>();
    private boolean isFirstPstn = true;
	
	public InvoicePDFCreator(){}
	
	public InvoicePDFCreator(CompanyDetail companyDetail
			,CustomerInvoice currentCustomerInvoice
			,CustomerOrder co
			,List<CustomerCallRecord> ccrs) {
		this.companyDetail = companyDetail;
		this.currentCustomerInvoice = currentCustomerInvoice;
		this.lastCustomerInvoice = currentCustomerInvoice.getLastCustomerInvoice();
	}

	public Map<String, Object> create() throws DocumentException, MalformedURLException, IOException{
		Map<String, Object> map = new HashMap<String, Object>();
		
        Document document = new Document(PageSize.A4);
        
		// Output PDF Path, e.g.: application_60089.pdf
		String outputFile = TMUtils.createPath(
				"broadband" 
				+ File.separator
				+ "customers" + File.separator + this.co.getId()
				+ File.separator + "invoice_" + this.getCurrentCustomerInvoice().getId() + ".pdf");
		
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputFile));
        document.open();
        
        /*
         *
         *  FIRST PAGE BEGIN
         * 
         */
		super.setGlobalBorderWidth(globalBorderWidth);
		
		// BASIC INFO
        document.add(createCustomerBasicInfo());

        
        // BEGIN CustomerInvoiceDetails
        List<CustomerInvoiceDetail> cids = this.getCurrentCustomerInvoice().getCustomerInvoiceDetails();
        for (CustomerInvoiceDetail cid : cids) {
        	
        	// BEGIN TotalPayableAmount
        	Double detailPrice = cid.getInvoice_detail_price();
        	Integer detailUnit = cid.getInvoice_detail_unit();
	
        	totalPayableAmount = detailPrice!=null && detailUnit!=null ? TMUtils.bigAdd(totalPayableAmount, TMUtils.bigMultiply(detailPrice, detailUnit)) :
			  detailPrice!=null && detailUnit==null ? TMUtils.bigAdd(totalPayableAmount, detailPrice) : TMUtils.bigAdd(totalPayableAmount, 0d);
	
        	// END TotalPayableAmount
        	Double detailDiscount = cid.getInvoice_detail_discount();
        	// BEGIN TotalCreditBack
        	totalCreditBack = detailDiscount!=null ? TMUtils.bigAdd(totalCreditBack, TMUtils.bigMultiply(detailDiscount, detailUnit)) : TMUtils.bigAdd(totalCreditBack, 0d);
        	// END TotalCreditBack
		}
        // END CustomerInvoiceDetails
        
        // If is business customer
        isBusiness = "business".toUpperCase().equals(this.getCo().getCustomer_type().toUpperCase());
        
        totalPayableAmount = isBusiness ? TMUtils.bigMultiply(totalPayableAmount, 1.15) : totalPayableAmount;
        
        // BEGIN ASSIGN FINAL VALUES
        this.totalFinalPayableAmount = TMUtils.bigSub(totalPayableAmount, totalCreditBack);
        // END ASSIGN FINAL VALUES
        

        totalBalance = TMUtils.bigSub(totalFinalPayableAmount, this.getCurrentCustomerInvoice().getAmount_paid());
        totalBalance = TMUtils.bigAdd(totalBalance, this.getLastCustomerInvoiceStatistic()!=null && this.getLastCustomerInvoiceStatistic().getBalance()!=null ? this.getLastCustomerInvoiceStatistic().getBalance() : 0d);

        if(totalBalance > 0){
        	// personal plan include GST
            this.beforeTaxAmount = TMUtils.bigDivide(totalBalance, 1.15);
            
            // include GST
            this.taxAmount = TMUtils.bigMultiply(beforeTaxAmount, 0.15);
        } else {
        	this.beforeTaxAmount = totalBalance;
        	this.taxAmount = totalBalance;
        }
        
        // RECENT TRANSACTION
        document.add(createRecentTransaction());
        
        // INVOICE SUMMARY
        document.add(createInvoiceSummary());

        // DUE NOTIFICATION
        if(this.getLastCustomerInvoice()!=null){
            document.add(createDueNotification());
        }

        
        /*
         * PAYMENT SLIP TABLE BEGIN
         */
        
        // CARTOON
		Image cartoon = null;
        File companyLogoFile = new File(pdf_resources_path + "common_company_logo.png");
		if(companyLogoFile.exists()){
			cartoon = Image.getInstance(pdf_resources_path + "common_company_logo.png");
        } else {
    		cartoon = Image.getInstance("pdf"+File.separator+"img"+File.separator+"company_logo.png");
        }
		cartoon.scaleAbsolute(100f, 46f);
		cartoon.setAbsolutePosition(30, 170);
		writer.getDirectContent().addImage(cartoon);
		
		{	// createPaymentSlip
	        PdfPTable paymentSlipTable = newTable().columns(5).totalWidth(535F).o();
	        Image img = Image.getInstance("pdf"+File.separator+"img"+File.separator+"scissor_separator.png");
	        img.setWidthPercentage(100);
	        addCol(paymentSlipTable, " ").colspan(5).image(img).o();
	        
	        // WHITE TITLE
	        addEmptyRow(paymentSlipTable, 4);
	        
	        addCol(paymentSlipTable, "Payment Slip").rowspan(4).font(ITextFont.arial_bold_10).paddingTo("t", 6F).o();
	        
	        // LIGHT GRAY TITLE
	        addCol(paymentSlipTable, " ").bgColor(new BaseColor(234,234,234)).borderColor(BaseColor.WHITE).border("r", 1F).o();
	        addCol(paymentSlipTable, " ").bgColor(new BaseColor(234,234,234)).borderColor(BaseColor.WHITE).border("r", 1F).o();
	        addCol(paymentSlipTable, " ").bgColor(new BaseColor(234,234,234)).o();
	        
	        // LIGHT GRAY CONTENT
	        addCol(paymentSlipTable, "Customer ID").font(ITextFont.arial_bold_8).bgColor(new BaseColor(234,234,234)).borderColor(BaseColor.WHITE).border("r", 1F).indent(14F).o();
	        addCol(paymentSlipTable, "Invoice Number").font(ITextFont.arial_bold_8).bgColor(new BaseColor(234,234,234)).borderColor(BaseColor.WHITE).border("r", 1F).indent(14F).o();
	        addCol(paymentSlipTable, "Due Date").font(ITextFont.arial_bold_8).bgColor(new BaseColor(234,234,234)).indent(14F).o();
	
	        boolean isFirst = this.getLastCustomerInvoice()==null;
	        
	        // LIGHT GRAY VALUE
	        addCol(paymentSlipTable, this.getCo().getCustomer_id().toString()).font(ITextFont.arial_normal_6).bgColor(new BaseColor(234,234,234)).borderColor(BaseColor.WHITE).border("r", 1F).paddingTo("t", 6F).indent(14F).o();
	        addCol(paymentSlipTable, this.getCurrentCustomerInvoice().getId().toString()).font(ITextFont.arial_normal_7).bgColor(new BaseColor(234,234,234)).borderColor(BaseColor.WHITE).border("r", 1F).paddingTo("t", 6F).indent(14F).o();
	        addCol(paymentSlipTable, isFirst ? "" : this.getCurrentCustomerInvoice().getDue_date() != null ? TMUtils.retrieveMonthAbbrWithDate(this.getCurrentCustomerInvoice().getDue_date()) : " ").font(ITextFont.arial_normal_7).bgColor(new BaseColor(234,234,234)).paddingTo("t", 6F).indent(14F).o();
	        addCol(paymentSlipTable, " ").bgColor(new BaseColor(234,234,234)).borderColor(BaseColor.WHITE).border("r", 1F).o();
	        addCol(paymentSlipTable, " ").bgColor(new BaseColor(234,234,234)).borderColor(BaseColor.WHITE).border("r", 1F).o();
	        addCol(paymentSlipTable, " ").bgColor(new BaseColor(234,234,234)).o();
	        
	        // SEPARATOR BEGIN
	        addEmptyCol(paymentSlipTable, 2F, 5);
	        // SEPARATOR END
	
			
	        // SECOND SECTION
	        addCol(paymentSlipTable, "Paying By Direct Credit").colspan(2).font(ITextFont.arial_normal_8).indent(4F).o();
	        addCol(paymentSlipTable, "Total amount due before").rowspan(2).font(ITextFont.arial_normal_white_8).bgColor(totleChequeAmountBGColor).paddingTo("t", 8F).indent(14F).o();
	        addCol(paymentSlipTable, this.getCurrentCustomerInvoice().getDue_date_str()).rowspan(2).font(ITextFont.arial_normal_white_8).bgColor(totleChequeAmountBGColor).paddingTo("t", 8F).indent(32F).o();
	        // input box begin
	        addCol(paymentSlipTable, "$ "+TMUtils.fillDecimalPeriod(String.valueOf(TMUtils.bigAdd(previousBalance, currentFinalPayable)))).rowspan(2).font(ITextFont.arial_normal_8).bgColor(BaseColor.WHITE).borderColor(totleChequeAmountBGColor).borderZoom(8F).alignH("r").o();
	        // input box end
	        addCol(paymentSlipTable, "Bank: "+this.getCompanyDetail().getBank_name()).colspan(2).font(ITextFont.arial_normal_8).indent(4F).o();
	        addCol(paymentSlipTable, "Name of Account: "+this.getCompanyDetail().getBank_account_name()).colspan(2).font(ITextFont.arial_normal_8).indent(4F).o();
	        addCol(paymentSlipTable, "Total amount due after").rowspan(2).font(ITextFont.arial_normal_white_8).bgColor(totleChequeAmountBGColor).paddingTo("t", 8F).indent(14F).o();
	        addCol(paymentSlipTable, this.getCurrentCustomerInvoice().getDue_date_str()).rowspan(2).font(ITextFont.arial_normal_white_8).bgColor(totleChequeAmountBGColor).paddingTo("t", 8F).indent(32F).o();
	        // input box begin
	        addCol(paymentSlipTable, "$ "+TMUtils.fillDecimalPeriod(String.valueOf(TMUtils.bigAdd(previousBalance, currentFinalPayable)))).rowspan(2).font(ITextFont.arial_normal_8).bgColor(BaseColor.WHITE).borderColor(totleChequeAmountBGColor).borderZoom(8F).alignH("r").o();
	        // input box end
	        addCol(paymentSlipTable, "Account Number: "+this.getCompanyDetail().getBank_account_number()).colspan(2).font(ITextFont.arial_normal_8).indent(4F).o();
	        
	        // THIRD SECTION
	        addEmptyCol(paymentSlipTable, 1F, 5);
	        
	        // SEPARATOR BEGIN
	        // SEPARATOR END
	        
	        addCol(paymentSlipTable, "Paying by cheques").colspan(3).font(ITextFont.arial_normal_white_8).bgColor(totleChequeAmountBGColor).paddingTo("t", 4F).indent(4F).o();
	        addCol(paymentSlipTable, "ENCLOSED AMOUNT").colspan(2).font(ITextFont.arial_bold_white_10).bgColor(totleChequeAmountBGColor).paddingTo("t", 4F).indent(40F).o();
	        addCol(paymentSlipTable, "Please make cheques payable to "+this.companyDetail.getName()+" and").colspan(3).font(ITextFont.arial_normal_white_8).bgColor(totleChequeAmountBGColor).indent(4F).o();
	        
			// BEGIN BOX
			addCol(paymentSlipTable, " ").colspan(2).rowspan(4).bgColor(BaseColor.WHITE).paddingTo("l", 42F).paddingTo("t", 6F).borderColor(totleChequeAmountBGColor).border("r", 20F).o();
			// END BOX
			
			addCol(paymentSlipTable, "write your Name and Phone Number on the back of your cheque.").colspan(3).font(ITextFont.arial_normal_white_8).bgColor(totleChequeAmountBGColor).indent(4F).o();
			addCol(paymentSlipTable, " ").colspan(3).bgColor(totleChequeAmountBGColor).o();
			addCol(paymentSlipTable, "Please post it with this payment slip to").colspan(3).font(ITextFont.arial_normal_white_8).bgColor(totleChequeAmountBGColor).indent(4F).o();
			addCol(paymentSlipTable, this.getCompanyDetail().getName()+" PO Box 41547, St Lukes, Auckland 1346, New Zealand").colspan(3).font(ITextFont.arial_normal_white_8).bgColor(totleChequeAmountBGColor).indent(4F).o();
			addCol(paymentSlipTable, "** PLEASE DO NOT SEND CASH").colspan(2).font(ITextFont.arial_normal_white_8).bgColor(totleChequeAmountBGColor).indent(40F).o();
			addEmptyCol(paymentSlipTable, 2F, 5);
	        // complete the table
	        paymentSlipTable.completeRow();
	        // write the table to an absolute position
	        PdfContentByte paymentSlipTableCanvas = writer.getDirectContent();
	        paymentSlipTable.writeSelectedRows(0, -1, (PageSize.A4.getWidth()-paymentSlipTable.getTotalWidth())/2, paymentSlipTable.getTotalHeight() + 28, paymentSlipTableCanvas);
	        /*
	         * PAYMENT SLIP TABLE END
	         */
        }

        // FIRST PAGE'S HEADER
		pageHeader(writer);
        /*
         *
         *  FIRST PAGE END
         * 
         */

        /*
         *
         *  SECOND PAGE BEGIN
         * 
         */
		// start new page
        document.newPage();
        
        // SECOND PAGE'S HEADER
		pageHeader(writer);

        // ADD TABLE 2 DOCUMENT
        document.add(createInvoiceDetails());
        /*
         *
         *  SECOND PAGE END
         * 
         */
        
        /*
         *
         *  THIRD PAGE BEGIN
         * 
         */
        
        // BEGIN CALLING RECORD
        if(ccrsMap.size() > 0){
     		// start new page
            document.newPage();
            // SECOND PAGE'S HEADER
    		pageHeader(writer);
            // ADD TABLE 2 DOCUMENT
    		for (String k : ccrsMap.keySet()) {
              document.add(createCallRecordDetails(k, ccrsMap.get(k)));
			}
        }
        // END CALLING RECORD
        

        // BEGIN CALLING STATISTIC
    	document.add(callingStatistic());
        // END CALLING STATISTIC
        
        /*
         *
         *  THIRD PAGE END
         * 
         */
		
		// CLOSE DOCUMENT
        document.close();
        
        map.put("amount_paid", this.getCurrentCustomerInvoice().getAmount_paid());
        map.put("final_amount_payable", this.totalFinalPayableAmount);
        map.put("amount_payable", this.totalPayableAmount);
        map.put("balance", TMUtils.bigSub(this.totalFinalPayableAmount, this.getCurrentCustomerInvoice().getAmount_paid()));
        map.put("filePath", outputFile);
		return map;
	}
	
	// BEGIN CUSTOMER BASIC INFO
	private PdfPTable createCustomerBasicInfo(){
        PdfPTable headerTable = new PdfPTable(1);
        headerTable.setWidthPercentage(102);
		// add common header
        addEmptyCol(headerTable, 140F, 1);
        
        String customerName = null;
//        String customerTitle = null;
        if("business".toUpperCase().equals(this.getCo().getCustomer_type().toUpperCase())){
        	customerName = this.getCo().getOrg_name();
//        	customerTitle = "BUSINESS";
        } else {
        	customerName = this.getCo().getTitle() != null ? this.getCo().getTitle().toUpperCase()+" " : "";
        	customerName += this.getCo().getFirst_name()+" "+this.getCo().getLast_name();
//        	customerTitle = "PERSONAL";
        }
//        addCol(headerTable, customerTitle + " USER").font(ITextFont.arial_bold_12).border(0).paddingTo("l", 50F).paddingTo("b", 10F).o();
        addCol(headerTable, customerName.trim()).font(ITextFont.arial_bold_9).paddingTo("l", 30F).paddingTo("b", 10F).border(0).o();
        String addressArr[] = this.getCo().getAddress().split(",");
        for (String address : addressArr) {
            addCol(headerTable, address.trim()).font(ITextFont.arial_bold_8).paddingTo("l", 30F).border(0).o();
		}
        addEmptyCol(headerTable, 40F, 1);
		return headerTable;
	}
	// END CUSTOMER BASIC INFO

    
    // current invoice
    // this invoice amount is consist of (current invoice total amount + last invoice balance - current customer invoice paid fees)
    // preventing subtraction inaccuracy
    
	// BEGIN RECENT TRANSACTION
	private PdfPTable createRecentTransaction(){
        /*
         * Recent Transactions Table begin
         */
        // non empty table start with 4 columns
		
		Integer colspan = 8;
		
        PdfPTable transactionTable = newTable().columns(colspan).widthPercentage(98F).o();
        addTitleBar(transactionTable, "Recent transactions", ITextFont.arial_bold_white_10, titleBGColor, titleBGColor, colspan, 4F);
        
        // TITLE BAR COLUMN
        addCol(transactionTable, "Date").colspan(2).font(ITextFont.arial_bold_9).paddingV(6F).indent(10F).o();
        addCol(transactionTable, "Description").colspan(colspan/2).font(ITextFont.arial_bold_9).paddingV(6F).o();
        addCol(transactionTable, "Amount").colspan(2).font(ITextFont.arial_bold_9).paddingV(6F).alignH("r").o();

        // account filtering of last invoice begin
        Double previousAmountPayable = this.getLastCustomerInvoice()!=null && this.getLastCustomerInvoice().getAmount_payable()!=null ? this.getLastCustomerInvoice().getAmount_payable() : 0.0;
        Double previousFinalAmountPayable = this.getLastCustomerInvoice()!=null && this.getLastCustomerInvoice().getFinal_payable_amount()!=null ? this.getLastCustomerInvoice().getFinal_payable_amount() : 0.0;
        Double previousAmountPaid = this.getLastCustomerInvoice()!=null && this.getLastCustomerInvoice().getAmount_paid()!=null ? this.getLastCustomerInvoice().getAmount_paid() : 0.0;
        
        // account filtering of last invoice end

    	Double amount_paid = this.getCurrentCustomerInvoice().getAmount_paid();
    	
        // if last invoice isn't null then go into <if statement>, otherwise only Opening Balance appears
        if(this.getLastCustomerInvoice()!=null){
        	
            // LAST INVOICE PAYABLE CASE
        	addCol(transactionTable, TMUtils.retrieveMonthAbbrWithDate(this.getLastCustomerInvoice().getCreate_date())+" & all unpaids").colspan(2).font(ITextFont.arial_normal_8).indent(10F).o();
        	addCol(transactionTable, "Previous Invoice Total").colspan(colspan/2).font(ITextFont.arial_normal_8).o();
        	addCol(transactionTable, "$ " + TMUtils.fillDecimalPeriod(String.valueOf(previousAmountPayable))).colspan(2).font(ITextFont.arial_normal_8).alignH("r").o();
            
            // CURRENT INVOICE DISCOUNT CASE
        	addCol(transactionTable, "").colspan(2).font(ITextFont.arial_normal_8).indent(10F).o();
        	addCol(transactionTable, "Total Credit Back").colspan(colspan/2).font(ITextFont.arial_normal_8).o();
        	addCol(transactionTable, "$ -" + TMUtils.fillDecimalPeriod(TMUtils.bigSub(previousAmountPayable, previousFinalAmountPayable))).colspan(2).font(ITextFont.arial_normal_8).indent(10F).alignH("r").o();
            
            // LAST INVOICE PAID CASE
        	addCol(transactionTable, TMUtils.retrieveMonthAbbrWithDate(this.getLastCustomerInvoice().getPaid_date())).colspan(2).font(ITextFont.arial_normal_8).indent(10F).o();
        	addCol(transactionTable, this.getLastCustomerInvoice().getPaid_type() != null ? this.getLastCustomerInvoice().getPaid_type() : "Amount Paid").colspan(colspan/2).font(ITextFont.arial_normal_8).o();
        	addCol(transactionTable, "$ -" + TMUtils.fillDecimalPeriod(String.valueOf(previousAmountPaid <=0 ? 0 : previousAmountPaid))).colspan(2).font(ITextFont.arial_normal_8).alignH("r").o();

            // LAST INVOICE SEPARATOR LINE
        	addEmptyCol(transactionTable, 7);
        	addCol(transactionTable, " ").border("b", 1F).o();

            // LAST INVOICE TOTAL AMOUNT
        	if(TMUtils.bigSub(previousFinalAmountPayable, previousAmountPaid) <= 0d){
            	addCol(transactionTable, "Opening Balance").colspan(6).font(ITextFont.arial_bold_10).alignH("r").o();
            	addCol(transactionTable, "$ "+ TMUtils.fillDecimalPeriod(String.valueOf(TMUtils.bigSub(previousFinalAmountPayable, previousAmountPaid)))).colspan(2).font(ITextFont.arial_bold_10).alignH("r").o();
        	} else {
            	addCol(transactionTable, "Opening Balance").colspan(6).font(ITextFont.arial_bold_red_10).alignH("r").o();
            	addCol(transactionTable, "$ "+ TMUtils.fillDecimalPeriod(String.valueOf(TMUtils.bigSub(previousFinalAmountPayable, previousAmountPaid)))).colspan(2).font(ITextFont.arial_bold_red_10).alignH("r").o();
        	}
        	addEmptyCol(transactionTable, 10F, colspan);
            
        } else {
        	
            // CURRENT INVOICE PAYABLE CASE
        	addCol(transactionTable, TMUtils.retrieveMonthAbbrWithDate(this.getCurrentCustomerInvoice().getCreate_date())).colspan(2).font(ITextFont.arial_normal_8).indent(10F).o();
        	addCol(transactionTable, "Current Invoice Total").colspan(colspan/2).font(ITextFont.arial_normal_8).o();
        	addCol(transactionTable, "$ " + TMUtils.fillDecimalPeriod(this.totalPayableAmount)).colspan(2).font(ITextFont.arial_normal_8).alignH("r").o();
            
            // CURRENT INVOICE DISCOUNT CASE
        	addCol(transactionTable, "").colspan(2).font(ITextFont.arial_normal_8).indent(10F).o();
        	addCol(transactionTable, "Total Credit Back").colspan(colspan/2).font(ITextFont.arial_normal_8).o();
        	addCol(transactionTable, "$ -" + TMUtils.fillDecimalPeriod(this.totalCreditBack)).colspan(2).font(ITextFont.arial_normal_8).indent(10F).alignH("r").o();
        	
            // CURRENT INVOICE PAID CASE
        	addCol(transactionTable, TMUtils.retrieveMonthAbbrWithDate(this.getCurrentCustomerInvoice().getPaid_date())).colspan(2).font(ITextFont.arial_normal_8).indent(10F).o();
        	addCol(transactionTable, this.getCurrentCustomerInvoice().getPaid_type() != null ? this.getCurrentCustomerInvoice().getPaid_type() : "Amount Paid").colspan(colspan/2).font(ITextFont.arial_normal_8).o();
        	addCol(transactionTable, "$ -" + TMUtils.fillDecimalPeriod(amount_paid<=0 ? 0 : amount_paid)).colspan(2).font(ITextFont.arial_normal_8).indent(10F).alignH("r").o();
            
            // CURRENT INVOICE SEPARATOR LINE
        	addEmptyCol(transactionTable, 7);
        	addCol(transactionTable, " ").border("b", 1F).o();
            addEmptyCol(transactionTable, 2F, colspan);
            
            // CURRENT INVOICE TOTAL AMOUNT
            if(totalCreditBack > this.getCurrentCustomerInvoice().getAmount_payable()) {
            	addCol(transactionTable, "Remaining Credit").colspan(6).font(ITextFont.arial_bold_green_10).alignH("r").o();
            	addCol(transactionTable, "$ "+ TMUtils.fillDecimalPeriod(String.valueOf(TMUtils.bigSub(this.totalFinalPayableAmount, this.getCurrentCustomerInvoice().getAmount_paid())))).colspan(2).font(ITextFont.arial_bold_green_10).alignH("r").o();
        	} else if(TMUtils.bigSub(this.getCurrentCustomerInvoice().getFinal_payable_amount(), amount_paid) <= 0d){
            	addCol(transactionTable, "Opening Balance").colspan(6).font(ITextFont.arial_bold_10).alignH("r").o();
            	addCol(transactionTable, "$ "+ TMUtils.fillDecimalPeriod(String.valueOf(TMUtils.bigSub(this.totalFinalPayableAmount, this.getCurrentCustomerInvoice().getAmount_paid())))).colspan(2).font(ITextFont.arial_bold_10).alignH("r").o();
        	} else {
            	addCol(transactionTable, "Opening Balance").colspan(6).font(ITextFont.arial_bold_red_10).alignH("r").o();
            	addCol(transactionTable, "$ "+ TMUtils.fillDecimalPeriod(String.valueOf(TMUtils.bigSub(this.totalFinalPayableAmount, this.getCurrentCustomerInvoice().getAmount_paid())))).colspan(2).font(ITextFont.arial_bold_red_10).alignH("r").o();
        	}
        	addEmptyCol(transactionTable, 10F, colspan);
            
        }
        
        return transactionTable;
	}
	// END RECENT TRANSACTION
	
	private PdfPTable createInvoiceSummary(){
		
		Integer colspan = 8;
		
        PdfPTable invoiceSummaryTable = newTable().columns(colspan).widthPercentage(98F).o();
        
        addTitleBar(invoiceSummaryTable, "Total Invoice Summary", ITextFont.arial_bold_white_10, titleBGColor, titleBGColor, colspan, 4F);

        // INVOICE SUMMARY FIRST ROW
        addCol(invoiceSummaryTable, "Net charges").colspan(6).font(ITextFont.arial_normal_8).paddingTo("t", 10F).indent(10F).o();
        
        addCol(invoiceSummaryTable, "$ " + TMUtils.fillDecimalPeriod(String.valueOf(beforeTaxAmount<=0 ? 0 : beforeTaxAmount))).colspan(2).font(ITextFont.arial_normal_8).paddingTo("t", 10F).alignH("r").o();

        // INVOICE SUMMARY SECOND ROW
        addCol(invoiceSummaryTable, "GST at 15%").colspan(6).font(ITextFont.arial_normal_8).indent(10F).o();
        addCol(invoiceSummaryTable, "$ " + TMUtils.fillDecimalPeriod(String.valueOf(taxAmount<=0 ? 0 : taxAmount))).colspan(2).font(ITextFont.arial_normal_8).alignH("r").o();

        // INVOICE SUMMARY THIRD ROW
        addCol(invoiceSummaryTable, "Total charges (please see Invoice Details page)").colspan(6).font(ITextFont.arial_normal_8).indent(10F).o();
        addCol(invoiceSummaryTable, "$ " + TMUtils.fillDecimalPeriod(String.valueOf(totalBalance<=0 ? 0 : totalBalance))).colspan(2).font(ITextFont.arial_normal_8).alignH("r").o();
        
        // Empty ROW
        addEmptyCol(invoiceSummaryTable, 30F, colspan);
        
        // INVOICE SUMMARY SEPARATOR ROW
    	addCol(invoiceSummaryTable, "Previous Balance").colspan(1).font(ITextFont.arial_bold_10).alignH("r").o();
    	addCol(invoiceSummaryTable, "").o();
    	addCol(invoiceSummaryTable, "Current Balance").colspan(1).font(ITextFont.arial_bold_10).alignH("r").o();
    	addCol(invoiceSummaryTable, "").o();
    	addCol(invoiceSummaryTable, "Current\n Paid").colspan(1).font(ITextFont.arial_bold_10).alignH("r").o();
    	addCol(invoiceSummaryTable, " ").colspan(2).o();
    	addCol(invoiceSummaryTable, " ").border("b", 1F).o();
        addEmptyCol(invoiceSummaryTable, 2F, colspan);

        // INVOICE SUMMARY INVOICE TOTAL DUE ROW
        boolean isFirst = this.getLastCustomerInvoice()==null;
		currentFinalPayable = totalCreditBack > this.getCurrentCustomerInvoice().getAmount_payable() ? 0 : totalBalance;
		
    	addCol(invoiceSummaryTable, TMUtils.fillDecimalPeriod(String.valueOf(this.getLastCustomerInvoiceStatistic()!=null && this.getLastCustomerInvoiceStatistic().getBalance()!=null ? this.getLastCustomerInvoiceStatistic().getBalance() : 0d))).colspan(1).font(ITextFont.arial_bold_10).alignH("r").o();
    	addCol(invoiceSummaryTable, "+").alignH("r").o();
    	addCol(invoiceSummaryTable, TMUtils.fillDecimalPeriod(String.valueOf(TMUtils.bigSub(this.totalPayableAmount, this.totalCreditBack)))).colspan(1).font(ITextFont.arial_bold_10).alignH("r").o();
    	addCol(invoiceSummaryTable, "-").alignH("r").o();
    	addCol(invoiceSummaryTable, TMUtils.fillDecimalPeriod(String.valueOf(this.getCurrentCustomerInvoice().getAmount_paid()))).colspan(1).font(ITextFont.arial_bold_10).alignH("r").o();
    	addCol(invoiceSummaryTable, "=").alignH("r").o();
    	addCol(invoiceSummaryTable, " ").o();
    	addCol(invoiceSummaryTable, "$ " + TMUtils.fillDecimalPeriod(currentFinalPayable)).colspan(1).font(ITextFont.arial_bold_10).alignH("r").o();

        addEmptyCol(invoiceSummaryTable, 4F, colspan);
    	addCol(invoiceSummaryTable, isFirst || TMUtils.bigAdd(previousBalance, currentFinalPayable) <= 0 ? "" : "Due On: "+TMUtils.retrieveMonthAbbrWithDate(this.getCurrentCustomerInvoice().getDue_date())).colspan(8).indent(10F).font(ITextFont.arial_normal_8).alignH("r").o();
        
        return invoiceSummaryTable;
	}
	
	private PdfPTable createDueNotification(){
		
		Integer colspan = 3;
        PdfPTable dueNotificationTable = newTable().columns(colspan).widthPercentage(98F).o();
        addCol(dueNotificationTable, "Overdue Penalty:").colspan(colspan).font(ITextFont.arial_bold_green_10).alignH("l").o();
        addCol(dueNotificationTable, "10% percent of your total amount every month.").colspan(colspan).font(ITextFont.arial_normal_green_8).indent(20F).alignH("l").o();
        addCol(dueNotificationTable, "(Overdue: 30 days after your invoice due date, overdue payment must be paid in 90 days after due date.)").colspan(colspan).font(ITextFont.arial_normal_green_8).indent(20F).alignH("l").o();
        
//		Integer innerColspan = 3;
//        PdfPTable dueNotificationinnerTable = newTable().columns(innerColspan).widthPercentage(100F).o();
//        addEmptyCol(dueNotificationinnerTable, 10F, innerColspan);
//    	addCol(dueNotificationinnerTable, "In 30 Days").colspan(innerColspan/3).font(ITextFont.arial_bold_green_20).alignH("c").o();
//    	addCol(dueNotificationinnerTable, "In 30-60 Days").colspan(innerColspan/3).border("l", 1F).font(ITextFont.arial_bold_green_20).alignH("c").o();
//    	addCol(dueNotificationinnerTable, "In 60-90 Days").colspan(innerColspan/3).border("l", 1F).font(ITextFont.arial_bold_green_20).alignH("c").o();
//        addEmptyCol(dueNotificationinnerTable, 8F, innerColspan);
//    	addCol(dueNotificationinnerTable, "0.0%").colspan(innerColspan/3).font(ITextFont.arial_normal_green_8).alignH("c").o();
//    	addCol(dueNotificationinnerTable, "0.0%").colspan(innerColspan/3).border("l", 1F).font(ITextFont.arial_normal_green_8).alignH("c").o();
//    	addCol(dueNotificationinnerTable, "0.0%").colspan(innerColspan/3).border("l", 1F).font(ITextFont.arial_normal_green_8).alignH("c").o();
//        addEmptyCol(dueNotificationinnerTable, 10F, innerColspan);
//    	addTableInCol(dueNotificationTable, dueNotificationinnerTable, colspan, titleBGColor, 1F);
//    	
//        addEmptyCol(dueNotificationTable, 20F, colspan);
        
        return dueNotificationTable;
	}
	
	private PdfPTable createInvoiceDetails(){
        PdfPTable invoiceDetailsTable = newTable().columns(10).widthPercentage(98F).o();
        addEmptyCol(invoiceDetailsTable, 160F, 10);
        // page's width percentage
        addCol(invoiceDetailsTable, "Current Invoice Details").colspan(10).font(ITextFont.arial_bold_white_10).bgColor(titleBGColor).paddingTo("b", 4F).alignH("c").o();
        addEmptyCol(invoiceDetailsTable, 10F, 10);
        
        // title
        addCol(invoiceDetailsTable, "Service / Product").colspan(4).font(ITextFont.arial_bold_8).border("b", 1F).fixedHeight(16F).o();
        addCol(invoiceDetailsTable, "Description").colspan(3).font(ITextFont.arial_bold_8).border("b", 1F).fixedHeight(16F).indent(32F).o();
        addCol(invoiceDetailsTable, "Debit").font(ITextFont.arial_bold_8).border("b", 1F).fixedHeight(16F).alignH("r").o();
        addCol(invoiceDetailsTable, "Qty").font(ITextFont.arial_bold_8).border("b", 1F).fixedHeight(16F).alignH("r").o();
        addCol(invoiceDetailsTable, "Subtotal").font(ITextFont.arial_bold_8).border("b", 1F).fixedHeight(16F).alignH("r").o();
        
        // PRODUCT(S) BEGIN
        addEmptyCol(invoiceDetailsTable, 14F, 10);
        // PRODUCT ITEM(S) BEGIN
        
        Double totalPrice = 0d;

        Boolean isDiscount = false;

        for (CustomerInvoiceDetail cid : this.getCurrentCustomerInvoice().getCustomerInvoiceDetails()) {
        	
        	// if both empty
        	Double subTotal = 0.0;
        	Double price = 0.0;
        	Integer unit = 0;
        	if(cid.getInvoice_detail_price() != null && cid.getInvoice_detail_unit() != null){
        		subTotal = TMUtils.bigMultiply(cid.getInvoice_detail_price(), cid.getInvoice_detail_unit());
        		price = cid.getInvoice_detail_price();
        		unit = cid.getInvoice_detail_unit();
        	}
        	// if price empty
        	if(cid.getInvoice_detail_price() == null && cid.getInvoice_detail_unit() != null){
        		// price == null then sub total = 0
//        		subTotal = 0.0;
//        		price = 0.0;
        		unit = cid.getInvoice_detail_unit();
        	}
        	// if unit empty
        	if(cid.getInvoice_detail_price() != null && cid.getInvoice_detail_unit() == null){
        		// price
        		subTotal = cid.getInvoice_detail_price();
        		price = cid.getInvoice_detail_price();
//        		unit = 0;
        	}
        	if(cid.getInvoice_detail_price() == null && cid.getInvoice_detail_unit() == null){
        		// if price & unit both empty
//        		subTotal = 0.0;
//        		price = 0.0;
//        		unit = 0;
        	}
        	if(cid.getInvoice_detail_discount() == null){
    			// plan name
            	addCol(invoiceDetailsTable, cid.getInvoice_detail_name()).colspan(4).font(ITextFont.arial_normal_7).indent(22F).o();
    			// term date
            	addCol(invoiceDetailsTable, cid.getInvoice_detail_desc()).colspan(3).font(ITextFont.arial_normal_7).o();
				// plan unit price
				addCol(invoiceDetailsTable, TMUtils.fillDecimalPeriod(String.valueOf(price))).font(ITextFont.arial_normal_7).alignH("r").o();
    			// unit
    			addCol(invoiceDetailsTable, unit.toString()).font(ITextFont.arial_normal_7).alignH("r").o();
				// sub total
				addCol(invoiceDetailsTable, TMUtils.fillDecimalPeriod(String.valueOf(subTotal))).font(ITextFont.arial_normal_7).alignH("r").o();
				totalPrice = TMUtils.bigAdd(totalPrice, isBusiness ? TMUtils.bigMultiply(subTotal, 1.15) : subTotal);
			} else {
				isDiscount = true;
			}
        }
        // PRODUCT(S) END
        
        // #####SEPARATOR BEGIN
        addCol(invoiceDetailsTable, " ").colspan(10).fixedHeight(8F).border("b", 1F).o();
        // #####SEPARATOR END
        
        // TOTAL BEGIN
        // #####EMTRY SPACE BEGIN
        addEmptyCol(invoiceDetailsTable, 10);
        // #####EMTRY SPACE END
        
        // FIRST ROW BEGIN
        addEmptyCol(invoiceDetailsTable, 14F, 7);
        addCol(invoiceDetailsTable, "Total before GST").colspan(2).font(ITextFont.arial_normal_8).o();
        
        Double totalBeforeGST = TMUtils.bigDivide(totalPrice, 1.15);
        // fill decimal, keep 2 decimals
        addCol(invoiceDetailsTable, TMUtils.fillDecimalPeriod(String.valueOf(totalBeforeGST))).font(ITextFont.arial_normal_8).alignH("r").o();
        // FIRST ROW END
        // SECOND ROW BEGIN
        addEmptyCol(invoiceDetailsTable, 14F, 7);
        addCol(invoiceDetailsTable, "GST at 15%").colspan(2).font(ITextFont.arial_normal_8).o();
        Double totalGST = TMUtils.bigMultiply(totalBeforeGST, 0.15);
        // fill decimal, keep 2 decimals
        addCol(invoiceDetailsTable, TMUtils.fillDecimalPeriod(String.valueOf(totalGST))).font(ITextFont.arial_normal_8).alignH("r").o();
        // SECOND ROW END
        
        // SEPARATOR BEGIN
        addEmptyCol(invoiceDetailsTable, 4F, 6);
        addCol(invoiceDetailsTable, " ").colspan(4).borderWidth("b", 1F).fixedHeight(4F).o();
        // SEPARATOR END
        
        // TOTAL AMOUNT BEGIN
        addEmptyCol(invoiceDetailsTable, 7);
        addCol(invoiceDetailsTable, "Invoice Total").colspan(2).font(ITextFont.arial_bold_8).o();
        addCol(invoiceDetailsTable, TMUtils.fillDecimalPeriod(String.valueOf(totalPrice))).font(ITextFont.arial_bold_8).alignH("r").o();
        // TOTAL AMOUNT END

        // If has discount, show credit calculation
        if(isDiscount){
            addEmptyCol(invoiceDetailsTable, 20F, 10);
            
            // CREDIT DETAILS
            addCol(invoiceDetailsTable, "Credit Calculation").colspan(10).font(ITextFont.arial_bold_white_10).bgColor(new BaseColor(200,200,234)).paddingTo("b", 4F).alignH("c").o();
            addEmptyCol(invoiceDetailsTable, 10F, 10);
            
            // title
            addCol(invoiceDetailsTable, "Service / Product").colspan(4).font(ITextFont.arial_bold_8).border("b", 1F).fixedHeight(16F).o();
            addCol(invoiceDetailsTable, "Description").colspan(3).font(ITextFont.arial_bold_8).border("b", 1F).fixedHeight(16F).indent(32F).o();
            addCol(invoiceDetailsTable, "Credit").font(ITextFont.arial_bold_8).border("b", 1F).fixedHeight(16F).alignH("r").o();
            addCol(invoiceDetailsTable, "Qty").font(ITextFont.arial_bold_8).border("b", 1F).fixedHeight(16F).alignH("r").o();
            addCol(invoiceDetailsTable, "Subtotal").font(ITextFont.arial_bold_8).border("b", 1F).fixedHeight(16F).alignH("r").o();
            
            // PRODUCT(S) BEGIN
            addEmptyCol(invoiceDetailsTable, 14F, 10);
            // PRODUCT ITEM(S) BEGIN
            
            Double totalPriceCopy = totalPrice;
            Double totalCreditCopy = 0d;
            
            for (CustomerInvoiceDetail cid : this.getCurrentCustomerInvoice().getCustomerInvoiceDetails()) {
            	
            	Double discount = 0.0;
            	Double subTotal = 0.0;
            	Integer unit = 0;

            	if(cid.getInvoice_detail_discount() != null && cid.getInvoice_detail_unit() != null){
            		subTotal = TMUtils.bigMultiply(cid.getInvoice_detail_discount(), cid.getInvoice_detail_unit());
            		discount = cid.getInvoice_detail_discount();
            		unit = cid.getInvoice_detail_unit();
            	}
            	if(cid.getInvoice_detail_discount()!=null){
        			// plan name
                	addCol(invoiceDetailsTable, cid.getInvoice_detail_name()).colspan(4).font(ITextFont.arial_normal_7).indent(22F).o();
        			// term date
                	addCol(invoiceDetailsTable, cid.getInvoice_detail_desc()).colspan(3).font(ITextFont.arial_normal_7).o();
    				// plan unit discount
    				addCol(invoiceDetailsTable, TMUtils.fillDecimalPeriod(String.valueOf(discount))).font(ITextFont.arial_normal_7).alignH("r").o();
        			// unit
        			addCol(invoiceDetailsTable, unit.toString()).font(ITextFont.arial_normal_7).alignH("r").o();

        			// sub total
        			addCol(invoiceDetailsTable, "-"+TMUtils.fillDecimalPeriod(String.valueOf(subTotal))).font(ITextFont.arial_normal_7).alignH("r").o();
        			totalPrice = TMUtils.bigSub(totalPrice, subTotal);
            	}
            	totalCreditCopy = TMUtils.bigAdd(totalCreditCopy, subTotal);
            }
            
            // #####SEPARATOR BEGIN
            addCol(invoiceDetailsTable, " ").colspan(10).fixedHeight(8F).border("b", 1F).o();
            // #####SEPARATOR END
            
            // TOTAL BEGIN
            // #####EMTRY SPACE BEGIN
            addEmptyCol(invoiceDetailsTable, 10);
            // #####EMTRY SPACE END
            
            // FIRST ROW BEGIN
            addEmptyCol(invoiceDetailsTable, 14F, 7);
            addCol(invoiceDetailsTable, "Total Invoice").colspan(2).font(ITextFont.arial_normal_8).o();
            
            // fill decimal, keep 2 decimals
            addCol(invoiceDetailsTable, TMUtils.fillDecimalPeriod(String.valueOf(totalPriceCopy))).font(ITextFont.arial_normal_8).alignH("r").o();
            // FIRST ROW END
            // SECOND ROW BEGIN
            addEmptyCol(invoiceDetailsTable, 14F, 7);
            addCol(invoiceDetailsTable, "Total Credit").colspan(2).font(ITextFont.arial_normal_8).o();
            // fill decimal, keep 2 decimals
            addCol(invoiceDetailsTable, TMUtils.fillDecimalPeriod("-"+String.valueOf(totalCreditCopy))).font(ITextFont.arial_normal_8).alignH("r").o();
            // SECOND ROW END
            
            // SEPARATOR BEGIN
            addEmptyCol(invoiceDetailsTable, 4F, 6);
            addCol(invoiceDetailsTable, " ").colspan(4).borderWidth("b", 1F).fixedHeight(4F).o();
            // SEPARATOR END
            
            // TOTAL AMOUNT BEGIN
            addEmptyCol(invoiceDetailsTable, 7);
            addCol(invoiceDetailsTable, "Total After Credit").colspan(2).font(ITextFont.arial_bold_8).o();
            addCol(invoiceDetailsTable, TMUtils.fillDecimalPeriod(String.valueOf(totalPrice))).font(ITextFont.arial_bold_8).alignH("r").o();
            // TOTAL AMOUNT END
        }
        
        // TOTAL END
        return invoiceDetailsTable;
	}
	
	private PdfPTable createCallRecordDetails(String pstn_number, List<CustomerCallRecord> ccrs){
        PdfPTable callRecordDetailsTable = newTable().columns(14).widthPercentage(98F).o();
        if(this.isFirstPstn){
            addEmptyCol(callRecordDetailsTable, 160F, 14);
            this.isFirstPstn = false;
        } else {
            addEmptyCol(callRecordDetailsTable, 20F, 14);
        }
        addCol(callRecordDetailsTable, "Calling details : " + pstn_number).colspan(14).font(ITextFont.arial_bold_12).paddingTo("b", 4F).o();
        addEmptyCol(callRecordDetailsTable, 10F, 14);
        addCol(callRecordDetailsTable, "Date").colspan(2).font(ITextFont.arial_bold_white_9).bgColor(titleBGColor).paddingTo("b", 5F).paddingTo("l", 4F).alignH("c").o();
        addCol(callRecordDetailsTable, "Phone Number").colspan(3).font(ITextFont.arial_bold_white_9).bgColor(titleBGColor).paddingTo("b", 4F).alignH("c").o();
        addCol(callRecordDetailsTable, "Destination").colspan(3).font(ITextFont.arial_bold_white_9).bgColor(titleBGColor).paddingTo("b", 4F).alignH("c").o();
        addCol(callRecordDetailsTable, "Call Type").colspan(2).font(ITextFont.arial_bold_white_9).bgColor(titleBGColor).paddingTo("b", 4F).alignH("c").o();
        addCol(callRecordDetailsTable, "Duration").colspan(2).font(ITextFont.arial_bold_white_9).bgColor(titleBGColor).paddingTo("b", 4F).alignH("r").o();
        addCol(callRecordDetailsTable, "Sub Total").colspan(2).font(ITextFont.arial_bold_white_9).bgColor(titleBGColor).paddingTo("b", 4F).alignH("r").o();
        addEmptyCol(callRecordDetailsTable, 6F, 14);
        
        Double totalCallFee = 0d;
        for (CustomerCallRecord ccr : ccrs) {
            addCol(callRecordDetailsTable, TMUtils.retrieveMonthAbbrWithDate(ccr.getCharge_date_time())).colspan(2).font(ITextFont.arial_normal_8).paddingTo("r", 4F).alignH("r").o();
            addCol(callRecordDetailsTable, ccr.getPhone_called()).colspan(3).font(ITextFont.arial_normal_8).paddingTo("b", 4F).alignH("c").o();
            addCol(callRecordDetailsTable, ccr.getBilling_description()).colspan(3).font(ITextFont.arial_normal_8).paddingTo("b", 4F).alignH("c").o();
            addCol(callRecordDetailsTable, ccr.getCallType()).colspan(2).font(ITextFont.arial_normal_8).paddingTo("b", 4F).alignH("c").o();
            addCol(callRecordDetailsTable, ccr.getFormated_duration()).colspan(2).font(ITextFont.arial_normal_8).paddingTo("b", 4F).alignH("r").o();
            addCol(callRecordDetailsTable, TMUtils.fillDecimalPeriod(String.valueOf(ccr.getAmount_incl()))).colspan(2).font(ITextFont.arial_normal_8).paddingTo("b", 4F).alignH("r").o();
            totalCallFee += ccr.getAmount_incl();
            
            boolean isFirstPSTN = true;
            
            if("pstn".equals(ccr.getOot_id())){

            	// If the loop held calling description match the loop retrieved calling detail then add duration & amount into the retrieved calling detail
            	for (CustomerCallRecord ccrStatistic : ccrPSTNStatistics) {
            		if(ccrStatistic.getBilling_description().equals(ccr.getBilling_description())){
            			
                    	Double duration = ccr.getDuration();
        				// If have reminder, then cut reminder and plus 1 minute, for example: 5.19 change to 6
        				if(TMUtils.isReminder(String.valueOf(duration))){
        					String durationStr = String.valueOf(duration);
        					duration =  Double.parseDouble(durationStr.substring(0, durationStr.indexOf(".")))+1d;
        				}
            			
            			ccrStatistic.setDuration(TMUtils.bigAdd(ccrStatistic.getDuration()==null ? 0 : ccrStatistic.getDuration(), duration));
            			ccrStatistic.setAmount_incl(TMUtils.bigAdd(ccrStatistic.getAmount_incl()==null ? 0 : ccrStatistic.getAmount_incl(), ccr.getAmount_incl()));
            		}
				}
            	
            	boolean isCount = true;

            	// If haven't found duplicated calling description then need to be add into the PSTN statistic list
            	for (CustomerCallRecord ccrStatistic : ccrPSTNStatistics) {
            		if(ccrStatistic.getBilling_description().equals(ccr.getBilling_description())){
            			
            			isCount = false;
            			
            		}
				}
            	
            	if(isCount){
            		
            		if(isFirstPSTN){
            			
                    	Double duration = ccr.getDuration();
        				// If have reminder, then cut reminder and plus 1 minute, for example: 5.19 change to 6
        				if(TMUtils.isReminder(String.valueOf(duration))){
        					String durationStr = String.valueOf(duration);
        					duration =  Double.parseDouble(durationStr.substring(0, durationStr.indexOf(".")))+1d;
        				}
            			
        				ccr.setDuration(duration);
        				isFirstPSTN = false;
            		}
            		
            		ccrPSTNStatistics.add(ccr);
            		
            	}
            	
            }
            
            boolean isFirstVoIP = true;
            
            if("voip".equals(ccr.getOot_id())){

            	// If the loop held calling description match the loop retrieved calling detail then add duration & amount into the retrieved calling detail
            	for (CustomerCallRecord ccrStatistic : ccrVoIPStatistics) {
            		if(ccrStatistic.getBilling_description().equals(ccr.getBilling_description())){
            			
                    	Double duration = ccr.getDuration();
        				// If have reminder, then cut reminder and plus 1 minute, for example: 5.19 change to 6
        				if(TMUtils.isReminder(String.valueOf(duration))){
        					String durationStr = String.valueOf(duration);
        					duration =  Double.parseDouble(durationStr.substring(0, durationStr.indexOf(".")))+1d;
        				}
            			
            			ccrStatistic.setDuration(TMUtils.bigAdd(ccrStatistic.getDuration()==null ? 0 : ccrStatistic.getDuration(), duration));
            			ccrStatistic.setAmount_incl(TMUtils.bigAdd(ccrStatistic.getAmount_incl()==null ? 0 : ccrStatistic.getAmount_incl(), ccr.getAmount_incl()));
            		}
				}
            	
            	boolean isCount = true;
            	
            	// If haven't found duplicated calling description then need to be add into the VoIP statistic list
            	for (CustomerCallRecord ccrStatistic : ccrVoIPStatistics) {
            		if(ccrStatistic.getBilling_description().equals(ccr.getBilling_description())){
            			
            			isCount = false;
            			
            		}
				}
            	
            	if(isCount){
            		
            		if(isFirstVoIP){
            			
                    	Double duration = ccr.getDuration();
        				// If have reminder, then cut reminder and plus 1 minute, for example: 5.19 change to 6
        				if(TMUtils.isReminder(String.valueOf(duration))){
        					String durationStr = String.valueOf(duration);
        					duration =  Double.parseDouble(durationStr.substring(0, durationStr.indexOf(".")))+1d;
        				}
            			
            			ccr.setDuration(duration);
            			isFirstVoIP = false;
            		}
                	
                	ccrVoIPStatistics.add(ccr);
            		
            	}
            	
            }
		}
        
        totalCallFee = isBusiness ? TMUtils.bigMultiply(totalCallFee, 1.15) : totalCallFee;
        
        
        addEmptyCol(callRecordDetailsTable, 20F, 14);
        
        // PRODUCT(S) END
        
        // #####SEPARATOR BEGIN
        addCol(callRecordDetailsTable, " ").colspan(14).fixedHeight(8F).border("b", 1F).o();
        // #####SEPARATOR END
        
        // TOTAL BEGIN
        // #####EMTRY SPACE BEGIN
        addEmptyCol(callRecordDetailsTable, 14);
        // #####EMTRY SPACE END
        
        // FIRST ROW BEGIN
        addEmptyCol(callRecordDetailsTable, 14F, 10);
        addCol(callRecordDetailsTable, "Total before GST").colspan(2).font(ITextFont.arial_normal_8).o();
        
        Double totalBeforeGST = totalCallFee/1.15;
        // fill decimal, keep 2 decimals
        addCol(callRecordDetailsTable, TMUtils.fillDecimalPeriod(String.valueOf(totalBeforeGST))).colspan(2).font(ITextFont.arial_normal_8).alignH("r").o();
        // FIRST ROW END
        // SECOND ROW BEGIN
        addEmptyCol(callRecordDetailsTable, 14F, 10);
        addCol(callRecordDetailsTable, "GST at 15%").colspan(2).font(ITextFont.arial_normal_8).o();
        Double totalGST = totalCallFee - totalCallFee/1.15;
        // fill decimal, keep 2 decimals
        addCol(callRecordDetailsTable, TMUtils.fillDecimalPeriod(String.valueOf(totalGST))).colspan(2).font(ITextFont.arial_normal_8).alignH("r").o();
        // SECOND ROW END
        
        // SEPARATOR BEGIN
        addEmptyCol(callRecordDetailsTable, 4F, 10);
        addCol(callRecordDetailsTable, " ").colspan(4).borderWidth("b", 1F).fixedHeight(4F).o();
        // SEPARATOR END
        
        // TOTAL AMOUNT BEGIN
        addEmptyCol(callRecordDetailsTable, 10);
        addCol(callRecordDetailsTable, "Calling Charge").colspan(2).font(ITextFont.arial_bold_8).o();
        addCol(callRecordDetailsTable, TMUtils.fillDecimalPeriod(String.valueOf(totalCallFee))).colspan(2).font(ITextFont.arial_bold_8).alignH("r").o();
        // TOTAL AMOUNT END
        
        
        return callRecordDetailsTable;
	}
	
	public PdfPTable callingStatistic(){
		
        PdfPTable callStatisticTable = newTable().columns(12).widthPercentage(98F).o();

        addEmptyCol(callStatisticTable, 12);

        if(this.getCcrPSTNStatistics().size() > 0){
        	
            addCol(callStatisticTable, "PSTN Calling Statistics").colspan(7).borderWidth("b", 1f).font(ITextFont.arial_bold_10).o();
            addEmptyCol(callStatisticTable, 5);
            addCol(callStatisticTable, "Description").colspan(3).font(ITextFont.arial_bold_10).o();
            addCol(callStatisticTable, "Duration").colspan(2).font(ITextFont.arial_bold_10).alignH("r").o();
            addCol(callStatisticTable, "Total Price").colspan(2).font(ITextFont.arial_bold_10).alignH("r").o();
            addCol(callStatisticTable, " ").colspan(5).o();

            for (CustomerCallRecord customerCallRecord : this.getCcrPSTNStatistics()) {
            	
//            	Double duration = customerCallRecord.getDuration();
//				// If have reminder, then cut reminder and plus 1 minute, for example: 5.19 change to 6
//				if(TMUtils.isReminder(String.valueOf(duration))){
//					String durationStr = String.valueOf(duration);
//					duration =  Double.parseDouble(durationStr.substring(0, durationStr.indexOf(".")))+1d;
//				}

                addCol(callStatisticTable, customerCallRecord.getBilling_description()).colspan(3).font(ITextFont.arial_normal_8).o();
                addCol(callStatisticTable, customerCallRecord.getDuration()<=0 ? "" : TMUtils.timeFormat.format(Double.parseDouble(String.valueOf(customerCallRecord.getDuration()))).replace(".", ":")).colspan(2).font(ITextFont.arial_normal_8).alignH("r").o();
                addCol(callStatisticTable, String.valueOf(isBusiness ? TMUtils.bigMultiply(customerCallRecord.getAmount_incl(), 1.15) : customerCallRecord.getAmount_incl())).colspan(2).font(ITextFont.arial_normal_8).alignH("r").o();
                addCol(callStatisticTable, " ").colspan(5).o();
    		}
            
            addEmptyCol(callStatisticTable, 12);
            
        }

        if(this.getCcrVoIPStatistics().size() > 0){
        	
            addCol(callStatisticTable, "VoIP Calling Statistics").colspan(7).borderWidth("b", 1f).font(ITextFont.arial_bold_10).o();
            addEmptyCol(callStatisticTable, 5);
            addCol(callStatisticTable, "Description").colspan(3).font(ITextFont.arial_bold_10).o();
            addCol(callStatisticTable, "Duration").colspan(2).font(ITextFont.arial_bold_10).alignH("r").o();
            addCol(callStatisticTable, "Total Price").colspan(2).font(ITextFont.arial_bold_10).alignH("r").o();
            addCol(callStatisticTable, " ").colspan(5).o();

            for (CustomerCallRecord customerCallRecord : this.getCcrVoIPStatistics()) {
            	
//            	Double duration = customerCallRecord.getDuration();
//				// If have reminder, then cut reminder and plus 1 minute, for example: 5.19 change to 6
//				if(TMUtils.isReminder(String.valueOf(duration))){
//					String durationStr = String.valueOf(duration);
//					duration =  Double.parseDouble(durationStr.substring(0, durationStr.indexOf(".")))+1d;
//				}

                addCol(callStatisticTable, customerCallRecord.getBilling_description()).colspan(3).font(ITextFont.arial_normal_8).o();
                addCol(callStatisticTable, customerCallRecord.getDuration()<=0 ? "" : TMUtils.timeFormat.format(Double.parseDouble(String.valueOf(customerCallRecord.getDuration()))).replace(".", ":")).colspan(2).font(ITextFont.arial_normal_8).alignH("r").o();
                addCol(callStatisticTable, String.valueOf(isBusiness ? TMUtils.bigMultiply(customerCallRecord.getAmount_incl(), 1.15) : customerCallRecord.getAmount_incl())).colspan(2).font(ITextFont.arial_normal_8).alignH("r").o();
                addCol(callStatisticTable, " ").colspan(5).o();
    		}
            
            addEmptyCol(callStatisticTable, 12);
        	
        }
        
        return callStatisticTable;
        
	}
	
	
	private void pageHeader(PdfWriter writer) throws MalformedURLException, IOException, DocumentException{
		Integer colspan = 15;
		PdfPTable headerTable = newTable().columns(colspan).totalWidth(510F).o();
		
        // logo & start
		Image logo = null;
        File invoiceLogoFile = new File(pdf_resources_path + "invoice_company_logo.png");
		if(invoiceLogoFile.exists()){
			logo = Image.getInstance(pdf_resources_path + "invoice_company_logo.png");
        } else {
    		logo = Image.getInstance("pdf"+File.separator+"img"+File.separator+"logo_top_final.png");
        }
		logo.scaleAbsolute(146f, 45f);
		logo.setAbsolutePosition(44, 760);
		writer.getDirectContent().addImage(logo);
		
		Phrase t1 = new Phrase("Statement / Tax Invoice", ITextFont.arial_normal_14);
		Phrase t2 = new Phrase("GST Registration Number: "+this.getCompanyDetail().getGst_registration_number(), ITextFont.lucida_sans_unicode_9);
		PdfContentByte canvas = writer.getDirectContentUnder();
		ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, t1, 44, 744, 0);
		ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, t2, 44, 730, 0);
		
		/*
		 * header table begin
		 */
        addEmptyCol(headerTable, 34F, colspan);
        addCol(headerTable, this.getCompanyDetail().getName()).colspan(colspan).font(ITextFont.arial_normal_8).alignH("r").o();
        addCol(headerTable, "PO Box 41547, St Lukes, Auckland 1346, New Zealand").colspan(colspan).font(ITextFont.arial_normal_8).alignH("r").o();
        addCol(headerTable, "Tel: "+this.getCompanyDetail().getTelephone()).colspan(colspan).font(ITextFont.arial_normal_8).alignH("r").o();
        addCol(headerTable, this.getCompanyDetail().getDomain()).colspan(colspan).font(ITextFont.arial_normal_8).alignH("r").o();
        addCol(headerTable, " ").colspan(colspan).borderColor("b", titleBGColor).borderWidth("b", 3F).o();
		/*
		 * header table end
		 */      
		/*
		 * invoice basics begin
		 */
        addEmptyCol(headerTable, 14F, colspan);
        addEmptyCol(headerTable, 4F, colspan-4);
        addCol(headerTable, "Customer Id: ").colspan(2).font(ITextFont.arial_bold_8).o();
        addCol(headerTable, this.getCo().getCustomer_id().toString()).colspan(2).font(ITextFont.arial_bold_8).alignH("r").o();
        addEmptyCol(headerTable, 4F, colspan-4);
        addCol(headerTable, "Invoice No: ").colspan(2).font(ITextFont.arial_bold_8).o();
        addCol(headerTable, this.getCurrentCustomerInvoice().getId().toString()).colspan(2).font(ITextFont.arial_bold_8).alignH("r").o();
        addEmptyCol(headerTable, 4F, colspan-4);
        addCol(headerTable, "Date: ").colspan(2).font(ITextFont.arial_bold_8).o();
        addCol(headerTable, TMUtils.retrieveMonthAbbrWithDate(this.getCurrentCustomerInvoice().getCreate_date())).colspan(2).font(ITextFont.arial_bold_8).alignH("r").o();
        addEmptyCol(headerTable, colspan);

		/*
		 * invoice basics end
		 */
        // complete the table
        headerTable.completeRow();
        // write the table to an absolute position
        PdfContentByte paymentSlipTableCanvas = writer.getDirectContent();
        headerTable.writeSelectedRows(0, -1, 41, 810, paymentSlipTableCanvas);
	}

	public CompanyDetail getCompanyDetail() {
		return companyDetail;
	}

	public void setCompanyDetail(CompanyDetail companyDetail) {
		this.companyDetail = companyDetail;
	}

	public CustomerInvoice getCurrentCustomerInvoice() {
		return currentCustomerInvoice;
	}

	public void setCurrentCustomerInvoice(CustomerInvoice currentCustomerInvoice) {
		this.setLastCustomerInvoice(currentCustomerInvoice.getLastCustomerInvoice());
		this.currentCustomerInvoice = currentCustomerInvoice;
	}

	public CustomerInvoice getLastCustomerInvoice() {
		return lastCustomerInvoice;
	}

	public void setLastCustomerInvoice(CustomerInvoice lastCustomerInvoice) {
		this.lastCustomerInvoice = lastCustomerInvoice;
	}

	public Map<String, List<CustomerCallRecord>> getCcrsMap() {
		return ccrsMap;
	}

	public void setCcrsMap(Map<String, List<CustomerCallRecord>> ccrsMap) {
		this.ccrsMap = ccrsMap;
	}

	public CustomerOrder getCo() {
		return co;
	}

	public void setCo(CustomerOrder co) {
		this.co = co;
	}

	public List<CustomerCallRecord> getCcrPSTNStatistics() {
		return ccrPSTNStatistics;
	}

	public void setCcrPSTNStatistics(List<CustomerCallRecord> ccrPSTNStatistics) {
		this.ccrPSTNStatistics = ccrPSTNStatistics;
	}

	public List<CustomerCallRecord> getCcrVoIPStatistics() {
		return ccrVoIPStatistics;
	}

	public void setCcrVoIPStatistics(List<CustomerCallRecord> ccrVoIPStatistics) {
		this.ccrVoIPStatistics = ccrVoIPStatistics;
	}

	public CustomerInvoice getLastCustomerInvoiceStatistic() {
		return lastCustomerInvoiceStatistic;
	}

	public void setLastCustomerInvoiceStatistic(
			CustomerInvoice lastCustomerInvoiceStatistic) {
		this.lastCustomerInvoiceStatistic = lastCustomerInvoiceStatistic;
	}

	
}