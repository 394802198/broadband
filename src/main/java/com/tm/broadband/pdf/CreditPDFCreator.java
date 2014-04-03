package com.tm.broadband.pdf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.tm.broadband.model.Customer;
import com.tm.broadband.model.CustomerCredit;
import com.tm.broadband.model.CustomerOrder;
import com.tm.broadband.util.ITextUtils;
import com.tm.broadband.util.TMUtils;

public class CreditPDFCreator extends ITextUtils {
	private Customer customer;
	private CustomerCredit cc;
	private CustomerOrder co;
	private Font arial_normal_8;
	private Font arial_normal_9;
	private Font arial_normal_10;
	private Font arial_bold_12;
	private Font arial_bold_20;
	private BaseColor titleBGColor = new BaseColor(237,237,237);
	private BaseColor titleBorderColor = new BaseColor(200,200,200);
	private BaseColor tableBorderColor = new BaseColor(159,159,159);
	private BaseColor tableRowLineColor = new BaseColor(237,237,237);

	public CreditPDFCreator() {
		try {
			BaseFont bf_arial_normal_8 = BaseFont.createFont("pdf"+File.separator+"font-family/Arial.ttf",BaseFont.WINANSI, BaseFont.EMBEDDED);
			this.arial_normal_8 = new Font(bf_arial_normal_8, 8, Font.NORMAL);
			BaseFont bf_arial_normal_9 = BaseFont.createFont("pdf"+File.separator+"font-family/Arial.ttf",BaseFont.WINANSI, BaseFont.EMBEDDED);
			this.arial_normal_9 = new Font(bf_arial_normal_9, 9, Font.NORMAL);
			BaseFont bf_arial_normal_10 = BaseFont.createFont("pdf"+File.separator+"font-family/Arial.ttf",BaseFont.WINANSI, BaseFont.EMBEDDED);
			this.arial_normal_10 = new Font(bf_arial_normal_10, 10, Font.NORMAL);
			BaseFont bf_arial_bold_12 = BaseFont.createFont("pdf"+File.separator+"font-family/arialbd.ttf",BaseFont.WINANSI, BaseFont.EMBEDDED);
			this.arial_bold_12 = new Font(bf_arial_bold_12, 12, Font.NORMAL);
			BaseFont bf_arial_bold_20 = BaseFont.createFont("pdf"+File.separator+"font-family/arialbd.ttf",BaseFont.WINANSI, BaseFont.EMBEDDED);
			this.arial_bold_20 = new Font(bf_arial_bold_20, 20, Font.NORMAL);
		} catch (DocumentException | IOException e) {
			e.printStackTrace();
		}
	}

	public CreditPDFCreator(Customer customer, CustomerCredit cc, CustomerOrder co) {
		this();
		this.setCustomer(customer);
		this.setCc(cc);
		this.setCo(co);
	}
	
	public void create() throws DocumentException, MalformedURLException, IOException{
		
        Document document = new Document(PageSize.A4);
		String outputFile = TMUtils.createPath("broadband" + File.separator
				+ "customers" + File.separator + this.customer.getId()
				+ File.separator + "credit_" + this.co.getId()
				+ ".pdf");
        
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputFile));
        
        // OPEN DOCUMENT
        document.open();
        
    	// company_logo
    	addImage(writer, "pdf"+File.separator+"img"+File.separator+"company_logo.png", 140F, 64F, 418F, 748F);
        
    	// PDF TITLE TABLE
    	document.add(createCreditPDFTitleTable());
        
    	// OFFICIAL USE ONLY TABLE
    	document.add(createOfficialUseOnlyTable());

    	// CONTACT DETAILS TABLE
    	document.add(createContactDetailsTable());
    	
    	// PAYMENT DETAILS TABLE
    	document.add(createPaymentDetailsTable());
        
    	// line_dash
    	addImage(writer, "pdf"+File.separator+"img"+File.separator+"line_dash.png", 535F, 2.5F, 29F, 364F);
    	
    	// CREDIT CARD AUTHORITY TABLE
    	document.add(createCreditCardAuthorityTable(writer));
    	
		// second page
        document.newPage();
    	
    	// TERM AND CONDITION TABLE
    	document.add(createTermAndConditionTable(writer));
        
		// CLOSE DOCUMENT
        document.close();
		// CLOSE WRITER
        writer.close();
	}
	
	public PdfPTable createCreditPDFTitleTable(){
		
        PdfPTable orderPDFTitleTable = new PdfPTable(10);
        orderPDFTitleTable.setWidthPercentage(102);
    	
    	// BEGIN CREDIT PDF TITLE PADDING TOP
        addEmptyRow(orderPDFTitleTable, 1F, 10);
    	// END CREDIT PDF TITLE PADDING TOP
        
        // BEGIN CREDIT PDF Title
        addPDFTitle(orderPDFTitleTable, "CREDIT CARD REQUEST", arial_bold_20, 4F, 0, null, 10);
        addPDFTitle(orderPDFTitleTable, "Please use a black ink, print in capital letters", arial_normal_10, 0F, 0, null, 10);
        addPDFTitle(orderPDFTitleTable, "and avoid contact with the edge of the box.", arial_normal_10, 22F, PdfPCell.BOTTOM, null, 10);
        // END CREDIT PDF Title
    	
    	// BEGIN CREDIT PDF TITLE PADDING BOTTOM
        addEmptyRow(orderPDFTitleTable, 10F, 10);
    	// END CREDIT PDF TITLE PADDING BOTTOM
        
		return orderPDFTitleTable;
	}
	
	public PdfPTable createOfficialUseOnlyTable(){
		
        PdfPTable officialUseOnlyTable = new PdfPTable(14);
        officialUseOnlyTable.setWidthPercentage(102);
		
        PdfPTable officialUseOnlyInnerTable = new PdfPTable(14);
        officialUseOnlyInnerTable.setWidthPercentage(100);
    	
    	// BEGIN OFFICIAL USE ONLY INNER TABLE PADDING TOP
        addEmptyRow(officialUseOnlyInnerTable, 6F, 14);
    	// END OFFICIAL USE ONLY INNER TABLE PADDING TOP
        
        // BEGIN PARAMETERS
        Float labelIndent = 4F;
        Float rowPaddingBottom = 8F;
        // END PARAMETERS
        
        // BEGIN OFFICIAL USE ONLY ROWS
        addCol(officialUseOnlyInnerTable, "Office use only", 14, labelIndent, arial_normal_10, 0F, rowPaddingBottom, null);
        addCol(officialUseOnlyInnerTable, "Customer Ref", 2, labelIndent, arial_normal_10, 0F, 0F, null);
        addColBottomBorder(officialUseOnlyInnerTable, " ", 5, 0F, null, 0F, 0F, null, null);
        addEmptyRow(officialUseOnlyInnerTable, 6F, 1);
        addCol(officialUseOnlyInnerTable, "Date", 1, labelIndent, arial_normal_10, 0F, 0F, null);
        addColBottomBorder(officialUseOnlyInnerTable, " ", 4, 0F, null, 0F, 0F, null, null);
        addEmptyRow(officialUseOnlyInnerTable, 6F, 1);
        // END OFFICIAL USE ONLY ROWS
    	
    	// BEGIN OFFICIAL USE ONLY INNER TABLE PADDING BOTTOM
        addEmptyRow(officialUseOnlyInnerTable, 6F, 14);
    	// END OFFICIAL USE ONLY INNER TABLE PADDING BOTTOM
        
        // BEGIN OFFICIAL USE ONLY TABLE IN COL
        addTableInCol(officialUseOnlyTable, officialUseOnlyInnerTable, 14, tableBorderColor);
        // END OFFICIAL USE ONLY TABLE IN COL
    	
    	// BEGIN OFFICIAL USE ONLY PADDING BOTTOM
        addEmptyRow(officialUseOnlyTable, 14F, 14);
    	// END OFFICIAL USE ONLY PADDING BOTTOM
        
		return officialUseOnlyTable;
	}
	
	public PdfPTable createContactDetailsTable(){
		
        PdfPTable contactDetailsTable = new PdfPTable(14);
        contactDetailsTable.setWidthPercentage(102);

        // BEGIN CONTACT DETAILS TITLE BAR
    	addTitleBar(contactDetailsTable, "CONTACT DETAILS", arial_bold_12, titleBGColor, titleBorderColor, 14, 4F);
        // END CONTACT DETAILS TITLE BAR
    	
    	// BEGIN CONTACT DETAILS TABLE PADDING BETWEEN TITLE AND ROW
        addEmptyRow(contactDetailsTable, 8F, 14);
    	// END CONTACT DETAILS TABLE PADDING BETWEEN TITLE AND ROW

		// BEGIN INNER TABLE
        PdfPTable contactDetailsInnerTable = new PdfPTable(14);
        contactDetailsInnerTable.setWidthPercentage(100);
        
        // BEGIN PARAMETERS
        Integer colspan = 14;
        Float labelIndent = 4F;
        Float rowPadding = 14F;
        // END PARAMETERS
    	
    	// BEGIN CONTACT DETAILS INNER TABLE PADDING TOP
        addEmptyRow(contactDetailsInnerTable, rowPadding, colspan);
    	// END CONTACT DETAILS INNER TABLE PADDING TOP

        // BEGIN CONTACT DETAILS ROWS
        addCol(contactDetailsInnerTable, "First Name", 2, labelIndent, arial_normal_10, 0F, 0F, null);
        addColBottomBorder(contactDetailsInnerTable, this.getCustomer().getFirst_name(), 4, labelIndent, arial_normal_10, 0F, 1F, null, null);
        addEmptyRow(contactDetailsInnerTable, 6F, 1);
        addCol(contactDetailsInnerTable, "Last Name", 2, labelIndent, arial_normal_10, 0F, 0F, null);
        addColBottomBorder(contactDetailsInnerTable, this.getCustomer().getLast_name(), 4, labelIndent, arial_normal_10, 0F, 1F, null, null);
        addEmptyRow(contactDetailsInnerTable, 6F, 1);
    	// BEGIN CONTACT DETAILS INNER TABLE PADDING BETWEEN ROWS
        addEmptyRow(contactDetailsInnerTable, rowPadding, colspan);
    	// END CONTACT DETAILS INNER TABLE PADDING BETWEEN ROWS
        addCol(contactDetailsInnerTable, "Full Address", 2, labelIndent, arial_normal_10, 0F, 0F, null);
        addColBottomBorder(contactDetailsInnerTable, this.getCustomer().getAddress(), 11, labelIndent, arial_normal_10, 0F, 1F, null, null);
        addEmptyRow(contactDetailsInnerTable, 6F, 1);
    	// BEGIN CONTACT DETAILS INNER TABLE PADDING BETWEEN ROWS
        addEmptyRow(contactDetailsInnerTable, rowPadding, colspan);
    	// END CONTACT DETAILS INNER TABLE PADDING BETWEEN ROWS
        addCol(contactDetailsInnerTable, "Phone", 2, labelIndent, arial_normal_10, 0F, 0F, null);
        addColBottomBorder(contactDetailsInnerTable, this.getCustomer().getPhone(), 4, labelIndent, arial_normal_10, 0F, 1F, null, null);
        addEmptyRow(contactDetailsInnerTable, 6F, 1);
        addCol(contactDetailsInnerTable, "Mobile", 2, labelIndent, arial_normal_10, 0F, 0F, null);
        addColBottomBorder(contactDetailsInnerTable, this.getCustomer().getCellphone(), 4, labelIndent, arial_normal_10, 0F, 1F, null, null);
        addEmptyRow(contactDetailsInnerTable, 6F, 1);
    	// BEGIN CONTACT DETAILS INNER TABLE PADDING BETWEEN ROWS
        addEmptyRow(contactDetailsInnerTable, rowPadding, colspan);
    	// END CONTACT DETAILS INNER TABLE PADDING BETWEEN ROWS
        addCol(contactDetailsInnerTable, "E-mail", 2, labelIndent, arial_normal_10, 0F, 0F, null);
        addColBottomBorder(contactDetailsInnerTable, this.getCustomer().getEmail(), 11, labelIndent, arial_normal_10, 0F, 1F, null, null);
        addEmptyRow(contactDetailsInnerTable, 6F, 1);
    	// BEGIN CONTACT DETAILS INNER TABLE PADDING BETWEEN ROWS
        addEmptyRow(contactDetailsInnerTable, rowPadding, colspan);
    	// END CONTACT DETAILS INNER TABLE PADDING BETWEEN ROWS
        // END CONTACT DETAILS ROWS
        
        
		// END INNER TABLE
        
        // BEGIN CONTACT DETAILS TABLE IN COL
    	addTableInCol(contactDetailsTable, contactDetailsInnerTable, 14, tableBorderColor);
        // END CONTACT DETAILS TABLE IN COL
    	
    	// BEGIN CONTACT DETAILS PADDING BOTTOM
        addEmptyRow(contactDetailsTable, 14F, 14);
    	// END CONTACT DETAILS PADDING BOTTOM
        
		return contactDetailsTable;
	}
	
	public PdfPTable createPaymentDetailsTable(){
		
        PdfPTable paymentDetailsTable = new PdfPTable(14);
        paymentDetailsTable.setWidthPercentage(102);

        // BEGIN PAYMENT DETAILS TITLE BAR
    	addTitleBar(paymentDetailsTable, "PAYMENT DETAILS", arial_bold_12, titleBGColor, titleBorderColor, 14, 4F);
        // END PAYMENT DETAILS TITLE BAR
    	
    	// BEGIN PAYMENT DETAILS TABLE PADDING BETWEEN TITLE AND ROW
        addEmptyRow(paymentDetailsTable, 8F, 14);
    	// END PAYMENT DETAILS TABLE PADDING BETWEEN TITLE AND ROW

		// BEGIN INNER TABLE
        PdfPTable paymentDetailsInnerTableFirst = new PdfPTable(14);
        paymentDetailsInnerTableFirst.setWidthPercentage(100);
        
        // BEGIN PARAMETERS
        Integer colspan = 14;
        Float labelIndent = 8F;
        Float rowPadding = 4F;
        // END PARAMETERS

        // BEGIN PAYMENT DETAILS ROWS
    	// BEGIN PAYMENT DETAILS INNER TABLE PADDING BETWEEN ROWS
        addEmptyRow(paymentDetailsInnerTableFirst, 8F, colspan);
    	// END PAYMENT DETAILS INNER TABLE PADDING BETWEEN ROWS
        addCol(paymentDetailsInnerTableFirst, "I authorise and request the registered initiator detailed below to debit payments from my nominated account, as specified", 14, labelIndent, arial_normal_9, 0F, 0F, null);
    	// BEGIN PAYMENT DETAILS INNER TABLE PADDING BETWEEN ROWS
        addEmptyRow(paymentDetailsInnerTableFirst, rowPadding, colspan);
    	// END PAYMENT DETAILS INNER TABLE PADDING BETWEEN ROWS
        addCol(paymentDetailsInnerTableFirst, "below, at intervals and amounts as directed by Telnet Telecommunication Ltd as per the Terms and Conditions of the Telnet", 14, labelIndent, arial_normal_9, 0F, 0F, null);
    	// BEGIN PAYMENT DETAILS INNER TABLE PADDING BETWEEN ROWS
        addEmptyRow(paymentDetailsInnerTableFirst, rowPadding, colspan);
    	// END PAYMENT DETAILS INNER TABLE PADDING BETWEEN ROWS
        addCol(paymentDetailsInnerTableFirst, "Telecommunication Ltd agreement and subsequent agreements.", 14, labelIndent, arial_normal_9, 0F, 0F, null);
    	// BEGIN PAYMENT DETAILS INNER TABLE PADDING BETWEEN ROWS
        addEmptyRow(paymentDetailsInnerTableFirst, 8F, colspan);
    	// END PAYMENT DETAILS INNER TABLE PADDING BETWEEN ROWS
        
		// END INNER TABLE
        
        // BEGIN PAYMENT DETAILS TABLE IN COL FIRST
    	addTableInCol(paymentDetailsTable, paymentDetailsInnerTableFirst, 14, tableBorderColor);
        // END PAYMENT DETAILS TABLE IN COL FIRST
    	
    	// BEGIN PAYMENT DETAILS BETWEEN INNER TABLE
        addEmptyRow(paymentDetailsTable, 6F, 14);
    	// END PAYMENT DETAILS BETWEEN INNER TABLE

		// BEGIN INNER TABLE
        PdfPTable paymentDetailsInnerTableSecond = new PdfPTable(14);
        paymentDetailsInnerTableSecond.setWidthPercentage(100);
    	
        // BEGIN PAYMENT DETAILS ROWS
    	// BEGIN PAYMENT DETAILS INNER TABLE PADDING BETWEEN ROWS
        addEmptyRow(paymentDetailsInnerTableSecond, 8F, colspan);
    	// END PAYMENT DETAILS INNER TABLE PADDING BETWEEN ROWS
        addCol(paymentDetailsInnerTableSecond, "When completed please FAX to 0800 2 29237 or Post the original to PO Box 41457, St Lukes, Auckland 1025", 14, labelIndent, arial_normal_9, 0F, 0F, null);
    	// BEGIN PAYMENT DETAILS INNER TABLE PADDING BETWEEN ROWS
        addEmptyRow(paymentDetailsInnerTableSecond, 8F, colspan);
    	// END PAYMENT DETAILS INNER TABLE PADDING BETWEEN ROWS
        // END PAYMENT DETAILS ROWS
        
        // BEGIN PAYMENT DETAILS TABLE IN COL SECOND
    	addTableInCol(paymentDetailsTable, paymentDetailsInnerTableSecond, 14, tableBorderColor);
        // END PAYMENT DETAILS TABLE IN COL SECOND
    	
    	// BEGIN PAYMENT DETAILS PADDING BOTTOM
        addEmptyRow(paymentDetailsTable, 44F, 14);
    	// END PAYMENT DETAILS PADDING BOTTOM
        
		return paymentDetailsTable;
	}
	
	public PdfPTable createCreditCardAuthorityTable(PdfWriter writer) throws MalformedURLException, DocumentException, IOException{
		
        PdfPTable creditCardAuthorityTable = new PdfPTable(14);
        creditCardAuthorityTable.setWidthPercentage(102);

        // BEGIN CREDIT CARD AUTHORITY TITLE BAR
    	addTitleBar(creditCardAuthorityTable, "CREDIT CARD AUTHORITY", arial_bold_12, titleBGColor, titleBorderColor, 14, 4F);
        // END CREDIT CARD AUTHORITY TITLE BAR
    	
    	// BEGIN CREDIT CARD AUTHORITY TABLE PADDING BETWEEN TITLE AND ROW
        addEmptyRow(creditCardAuthorityTable, 8F, 14);
    	// END CREDIT CARD AUTHORITY TABLE PADDING BETWEEN TITLE AND ROW
        
        // BEGIN PARAMETERS
        Integer colspan = 14;
        Float labelIndent = 8F;
        Float rowPadding = 14F;
        // END PARAMETERS

        // BEGIN CREDIT CARD AUTHORITY OUTER TABLE ROWS
        addCol(creditCardAuthorityTable, "I / We authorise Cyber Park Limited to collect the instalments by initiating a transaction, at the frequency indicated above, from", 14, labelIndent, arial_normal_9, 0F, 0F, null);
    	// BEGIN CREDIT CARD AUTHORITY OUTER TABLE PADDING BETWEEN ROWS
        addEmptyRow(creditCardAuthorityTable, 2F, colspan);
    	// END CREDIT CARD AUTHORITY OUTER TABLE PADDING BETWEEN ROWS
        addCol(creditCardAuthorityTable, "the Credit Card company indicated below.", 14, labelIndent, arial_normal_9, 0F, 0F, null);
    	// BEGIN CREDIT CARD AUTHORITY OUTER TABLE PADDING BETWEEN ROWS
        addEmptyRow(creditCardAuthorityTable, 8F, colspan);
    	// END CREDIT CARD AUTHORITY OUTER TABLE PADDING BETWEEN ROWS
        // BEGIN CREDIT CARD AUTHORITY OUTER TABLE ROWS

		// BEGIN INNER TABLE
        PdfPTable creditCardAuthorityInnerTable = new PdfPTable(14);
        creditCardAuthorityInnerTable.setWidthPercentage(100);

        // BEGIN CREDIT CARD AUTHORITY INNER TABLE ROWS
    	// BEGIN CREDIT CARD AUTHORITY INNER TABLE PADDING BETWEEN ROWS
        addEmptyRow(creditCardAuthorityInnerTable, 12F, colspan);
    	// END CREDIT CARD AUTHORITY INNER TABLE PADDING BETWEEN ROWS
        addImage(writer, "pdf"+File.separator+"img"+File.separator+"tick_braket.png", 14F, 14F, 41F, 252F);
        addCol(creditCardAuthorityInnerTable, "VISA", 3, 26F, arial_normal_10, 0F, 0F, null);
        addImage(writer, "pdf"+File.separator+"img"+File.separator+"tick_braket.png", 14F, 14F, 130F, 252F);
        addCol(creditCardAuthorityInnerTable, "MasterCard", 11, 2F, arial_normal_10, 0F, 0F, null);
        if(this.getCc().getCard_type().equalsIgnoreCase("VISA")){
            addImage(writer, "pdf"+File.separator+"img"+File.separator+"tick.png", 14F, 14F, 41F, 252F);
        } else if(this.getCc().getCard_type().equalsIgnoreCase("MASTERCARD")){
            addImage(writer, "pdf"+File.separator+"img"+File.separator+"tick.png", 14F, 14F, 130F, 252F);
        }
    	// BEGIN CREDIT CARD AUTHORITY INNER TABLE PADDING BETWEEN ROWS
        addEmptyRow(creditCardAuthorityInnerTable, rowPadding, colspan);
    	// END CREDIT CARD AUTHORITY INNER TABLE PADDING BETWEEN ROWS
        addCol(creditCardAuthorityInnerTable, "Card Holder Name", 3, labelIndent, arial_normal_10, 0F, 0F, null);
        addColBottomBorder(creditCardAuthorityInnerTable, this.getCc().getHolder_name(), 10, labelIndent, arial_normal_10, 0F, 0F, null, null);
        addEmptyRow(creditCardAuthorityInnerTable, rowPadding, 1);
    	// BEGIN CREDIT CARD AUTHORITY INNER TABLE PADDING BETWEEN ROWS
        addEmptyRow(creditCardAuthorityInnerTable, rowPadding, colspan);
    	// END CREDIT CARD AUTHORITY INNER TABLE PADDING BETWEEN ROWS
        addCol(creditCardAuthorityInnerTable, "Card Number", 3, labelIndent, arial_normal_10, 0F, 0F, null);
        addColBottomBorder(creditCardAuthorityInnerTable, this.getCc().getCard_number(), 10, labelIndent, arial_normal_10, 0F, 0F, null, null);
        addEmptyRow(creditCardAuthorityInnerTable, rowPadding, 1);
    	// BEGIN CREDIT CARD AUTHORITY INNER TABLE PADDING BETWEEN ROWS
        addEmptyRow(creditCardAuthorityInnerTable, rowPadding, colspan);
    	// END CREDIT CARD AUTHORITY INNER TABLE PADDING BETWEEN ROWS
        addCol(creditCardAuthorityInnerTable, "Security Code", 3, labelIndent, arial_normal_10, 0F, 0F, null);
        addColBottomBorder(creditCardAuthorityInnerTable, this.getCc().getSecurity_code(), 3, labelIndent, arial_normal_10, 0F, 0F, null, null);
        addEmptyRow(creditCardAuthorityInnerTable, rowPadding, 8);
    	// BEGIN CREDIT CARD AUTHORITY INNER TABLE PADDING BETWEEN ROWS
        addEmptyRow(creditCardAuthorityInnerTable, rowPadding, colspan);
    	// END CREDIT CARD AUTHORITY INNER TABLE PADDING BETWEEN ROWS
        addCol(creditCardAuthorityInnerTable, "Expiry Date", 3, labelIndent, arial_normal_10, 0F, 0F, null);
        addColBottomBorder(creditCardAuthorityInnerTable, this.getCc().getExpiry_date(), 3, labelIndent, arial_normal_10, 0F, 0F, null, null);
        addEmptyRow(creditCardAuthorityInnerTable, rowPadding, 8);
    	// BEGIN CREDIT CARD AUTHORITY INNER TABLE PADDING BETWEEN ROWS
        addEmptyRow(creditCardAuthorityInnerTable, rowPadding, colspan);
    	// END CREDIT CARD AUTHORITY INNER TABLE PADDING BETWEEN ROWS
        addCol(creditCardAuthorityInnerTable, "Signature", 3, labelIndent, arial_normal_10, 0F, 0F, null);
        addColBottomBorder(creditCardAuthorityInnerTable, " ", 3, labelIndent, arial_normal_10, 0F, 0F, null, null);
        addEmptyRow(creditCardAuthorityInnerTable, rowPadding, 8);
    	// BEGIN CREDIT CARD AUTHORITY INNER TABLE PADDING BETWEEN ROWS
        addEmptyRow(creditCardAuthorityInnerTable, rowPadding, colspan);
    	// END CREDIT CARD AUTHORITY INNER TABLE PADDING BETWEEN ROWS
        addCol(creditCardAuthorityInnerTable, "Date", 3, labelIndent, arial_normal_10, 0F, 0F, null);
        addColBottomBorder(creditCardAuthorityInnerTable, " ", 3, labelIndent, arial_normal_10, 0F, 0F, null, null);
        addEmptyRow(creditCardAuthorityInnerTable, rowPadding, 8);
    	// BEGIN CREDIT CARD AUTHORITY INNER TABLE PADDING BETWEEN ROW AND ROW LINE
        addEmptyRow(creditCardAuthorityInnerTable, 8F, colspan);
    	// END CREDIT CARD AUTHORITY INNER TABLE PADDING BETWEEN ROW AND ROW LINE
    	// BEGIN CREDIT CARD AUTHORITY INNER TABLE ROW LINE
        addLineRow(creditCardAuthorityInnerTable, colspan, tableRowLineColor, 1F);
    	// END CREDIT CARD AUTHORITY INNER TABLE ROW LINE
    	// BEGIN CREDIT CARD AUTHORITY INNER TABLE PADDING BETWEEN ROW AND ROW LINE
        addEmptyRow(creditCardAuthorityInnerTable, 4F, colspan);
    	// END CREDIT CARD AUTHORITY INNER TABLE PADDING BETWEEN ROW AND ROW LINE
        addEmptyRow(creditCardAuthorityInnerTable, 11F, 1);
        addCol(creditCardAuthorityInnerTable, "I / we acknowledge that Cyber Park Limited will appear as the business name on my credit card statement.", 13, labelIndent, arial_normal_8, 0F, 0F, null);
        // END CREDIT CARD AUTHORITY INNER TABLE ROWS
        
        // BEGIN CREDIT CARD AUTHORITY TABLE IN COL
    	addTableInCol(creditCardAuthorityTable, creditCardAuthorityInnerTable, 14, tableBorderColor);
        // END CREDIT CARD AUTHORITY TABLE IN COL
        
        
		return creditCardAuthorityTable;
	}
	
	public PdfPTable createTermAndConditionTable(PdfWriter writer) throws MalformedURLException, DocumentException, IOException{
		
        PdfPTable termAndConditionTable = new PdfPTable(14);
        termAndConditionTable.setWidthPercentage(102);
        
        // BEGIN PARAMETERS
        Integer colspan = 14;
        Float rowPadding = 14F;
        // END PARAMETERS

        // BEGIN TERM AND CONDITION TITLE BAR
    	addTitleBar(termAndConditionTable, "TERM AND CONDITION", arial_bold_12, titleBGColor, titleBorderColor, colspan, 10F);
        // END TERM AND CONDITION TITLE BAR

    	// BEGIN TERM AND CONDITION ROWS
    	// BEGIN TERM AND CONDITION PADDING BETWEEN ROWS
        addEmptyRow(termAndConditionTable, rowPadding, colspan);
    	// END TERM AND CONDITION PADDING BETWEEN ROWS
        addCol(termAndConditionTable, "Privacy Policy", 14, 0F, arial_normal_9, 0F, 0F, null);
    	// BEGIN TERM AND CONDITION PADDING BETWEEN ROWS
        addEmptyRow(termAndConditionTable, rowPadding, colspan);
    	// END TERM AND CONDITION PADDING BETWEEN ROWS
        addCol(termAndConditionTable, "Introduction", 14, 0F, arial_normal_9, 0F, 0F, null);
        addCol(termAndConditionTable, "Direct Payment Solutions Limited or its licensors (hereinafter referred to as DPS) are committed to protecting your privacy as an Internet user whenever you buy goods or services from "
 +"a Merchant which uses Payment Express. The Merchant will generally be using Payment Express when the cardholder is using a credit or debit card over the Internet, Phone, Fax, "
 +"Unattended or Integrated EFTPOS system. DPS recognizes its responsibility to keep confidential at all times any information which DPS acquires in connection with such a transaction, "
 +"whether directly from the Cardholders or Merchant. DPS protects personal information (at a minimum) to the Payment Card Industry Data Security Standards. Please note however; "
 +"DPS responsibility is limited to protection by DPS of information which DPS obtains. DPS itself cannot, of course, control the use or disclosure by your supplier of any information "
 +"which they obtain from you.", 14, 0F, arial_normal_8, 0F, 0F, null);
    	// BEGIN TERM AND CONDITION PADDING BETWEEN ROWS
        addEmptyRow(termAndConditionTable, rowPadding, colspan);
    	// END TERM AND CONDITION PADDING BETWEEN ROWS
        addCol(termAndConditionTable, "Collection of Information", 14, 0F, arial_normal_9, 0F, 0F, null);
        addCol(termAndConditionTable, "To enable DPS to provide secure payment facilities it will typically acquire information which may include the Cardholder's name, credit card number (with the expiry date) and "
 +"billing address.", 14, 0F, arial_normal_8, 0F, 0F, null);
    	// BEGIN TERM AND CONDITION PADDING BETWEEN ROWS
        addEmptyRow(termAndConditionTable, rowPadding, colspan);
    	// END TERM AND CONDITION PADDING BETWEEN ROWS
        addCol(termAndConditionTable, "Use and Disclosure of Information", 14, 0F, arial_normal_9, 0F, 0F, null);
        addCol(termAndConditionTable, "DPS uses the information to obtain authorization of the transaction from the Issuing bank of the credit card and DPS's own or the Merchant's bank and to process the payment. Some "
 +"details from the transaction (such as name, email and delivery address) may be made available to the Merchant or Acquirer through Payline - DPS web based transactions management "
 +"system, which allows Merchants to track transactions and process refunds.", 14, 0F, arial_normal_8, 0F, 0F, null);
    	// BEGIN TERM AND CONDITION PADDING BETWEEN ROWS
        addEmptyRow(termAndConditionTable, rowPadding, colspan);
    	// END TERM AND CONDITION PADDING BETWEEN ROWS
        addCol(termAndConditionTable, "Security", 14, 0F, arial_normal_9, 0F, 0F, null);
        addCol(termAndConditionTable, "DPS is committed to data security. DPS uses a variety of technologies and procedures to help protect personal information from unauthorized access, use or disclosure. For example, "
 +"DPS stores the data in computer servers with limited access that are located in controlled facilities secured by the latest in surveillance and security technology. When DPS transmits "
 +"sensitive information (such as a credit card numbers), DPS protects it through the use of encryption, such as the Secure Socket Layer (SSL) protocol. Credit card details stored onsite are "
 +"encrypted using 168bit 3DES encryption. DPS is a level 1 certified PCI-DSS compliant provider:", 14, 0F, arial_normal_8, 0F, 0F, null);
    	// BEGIN TERM AND CONDITION PADDING BETWEEN ROWS
        addEmptyRow(termAndConditionTable, rowPadding, colspan);
    	// END TERM AND CONDITION PADDING BETWEEN ROWS
        addCol(termAndConditionTable, "PCI DSS", 14, 0F, arial_normal_9, 0F, 0F, null);
        addCol(termAndConditionTable, "PCI DSS, the Payment Card Industry Data Security Standard is a set of security requirements relating to the protection of card holder data. The standard is governed by the PCI Security "
 +"Standards Council, an organisation put together by most of the major card schemes VISA, MasterCard, American Express, JCB and Discover. It's relevant for any entity that stores or "
 +"transmits sensitive card holder data, that being generally things like the PAN (card number), Card security code, track data, PIN block. The current version of the standard is "
 +"Version 1.2. Preceding PCI-DSS the card schemes had their own standards, the VISA Account Information Security (AIS) standard formed the basis to most of the PCI-DSS "
 +"requirements.", 14, 0F, arial_normal_8, 0F, 0F, null);
    	// BEGIN TERM AND CONDITION PADDING BETWEEN ROWS
        addEmptyRow(termAndConditionTable, 44F, colspan);
    	// END TERM AND CONDITION PADDING BETWEEN ROWS
        addCol(termAndConditionTable, "Account2Account Terms & Conditions", 14, 0F, arial_normal_9, 0F, 0F, null);
    	// BEGIN TERM AND CONDITION PADDING BETWEEN ROWS
        addEmptyRow(termAndConditionTable, rowPadding, colspan);
    	// END TERM AND CONDITION PADDING BETWEEN ROWS
        addCol(termAndConditionTable, "To the fullest extent permitted by applicable law:", 14, 0F, arial_normal_9, 0F, 0F, null);
        addCol(termAndConditionTable, "- DPS will not be liable to you or any other party for any loss (including indirect, consequential or special loss), damage, cost or expense, however caused  (including through "
 +"negligence), suffered or incurred by you or any other party arising out of, or in connection with, your use of Payment Express.", 14, 0F, arial_normal_8, 0F, 0F, null);
    	// BEGIN TERM AND CONDITION PADDING BETWEEN ROWS
        addEmptyRow(termAndConditionTable, rowPadding, colspan);
    	// END TERM AND CONDITION PADDING BETWEEN ROWS
        addCol(termAndConditionTable, "- DPS disclaims all warranties, conditions and representations, express or implied, in respect of your use of Payment Express, and in any event your sole and exclusive remedy for any "
 +"breach by DPS of an implied warranty, condition or representation is the re-supply of the Payment Express service.", 14, 0F, arial_normal_8, 0F, 0F, null);
    	// BEGIN TERM AND CONDITION PADDING BETWEEN ROWS
        addEmptyRow(termAndConditionTable, rowPadding, colspan);
    	// END TERM AND CONDITION PADDING BETWEEN ROWS
        addCol(termAndConditionTable, "Payments made by you using Payment Express will be subject to your financial institution’s terms and conditions. By using Payment Express you warrant and agree that you are the "
 +"owner of the account from which you are making a payment, you are authorised to make payments from that account, and any refunds agreed to be paid should be credited to the same "
 +"account from which the initial payment was made.", 14, 0F, arial_normal_8, 0F, 0F, null);
        
    	// line_dash
    	addImage(writer, "pdf"+File.separator+"img"+File.separator+"line_dash.png", 535F, 2.5F, 29F, 244F);
    	
		return termAndConditionTable;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public CustomerCredit getCc() {
		return cc;
	}

	public void setCc(CustomerCredit cc) {
		this.cc = cc;
	}

	public CustomerOrder getCo() {
		return co;
	}

	public void setCo(CustomerOrder co) {
		this.co = co;
	}

}