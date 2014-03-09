package com.tm.broadband.pdf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.tm.broadband.model.CompanyDetail;
import com.tm.broadband.model.Customer;
import com.tm.broadband.model.CustomerInvoice;
import com.tm.broadband.model.CustomerInvoiceDetail;
import com.tm.broadband.util.TMUtils;

/** 
* Generating Invoice PDF By Incoming Details
* 
* @author DON CHEN
*/ 
public class InvoicePDFCreator {
	
	private CustomerInvoice customerInvoice;
	private CompanyDetail companyDetail;
	private Customer customer;

	private Font arial_normal_6;
	private Font arial_normal_7;
	private Font arial_normal_8;
	private Font arial_normal_white_8;
	private Font arial_normal_10;
	private Font lucida_sans_unicode_9;
	private Font verdana_normal_8;
	private Font verdana_bold_7;
	private Font verdana_bold_8;
	private Font verdana_normal_white_8;
	private Font verdana_bold_10;
	private Font verdana_bold_white_10;
	private Font verdana_normal_14;
	private Font tahoma_normal_10;
	private Font tahoma_bold_10;
	private Font tahoma_bold_white_10;
	private BaseColor titleBGColor = new BaseColor(92,184,92);
	private BaseColor totleChequeAmountBGColor = new BaseColor(110,110,110);
	
	
	public InvoicePDFCreator(){
		try {
			BaseFont bf_arial_normal_6 = BaseFont.createFont("pdf"+File.separator+"font-family"+File.separator+"Arial.ttf",BaseFont.WINANSI, BaseFont.EMBEDDED);
			this.arial_normal_6 = new Font(bf_arial_normal_6, 6, Font.NORMAL);
			BaseFont bf_arial_normal_7 = BaseFont.createFont("pdf"+File.separator+"font-family"+File.separator+"Arial.ttf",BaseFont.WINANSI, BaseFont.EMBEDDED);
			this.arial_normal_7 = new Font(bf_arial_normal_7, 7, Font.NORMAL);
			BaseFont bf_arial = BaseFont.createFont("pdf"+File.separator+"font-family"+File.separator+"Arial.ttf",BaseFont.WINANSI, BaseFont.EMBEDDED);
			this.arial_normal_8 = new Font(bf_arial, 8, Font.NORMAL);
			BaseFont bf_arial_normal_white_8 = BaseFont.createFont("pdf"+File.separator+"font-family"+File.separator+"Arial.ttf",BaseFont.WINANSI, BaseFont.EMBEDDED);
			this.arial_normal_white_8 = new Font(bf_arial_normal_white_8, 8, Font.NORMAL, BaseColor.WHITE);
			BaseFont bf_arial_normal_10 = BaseFont.createFont("pdf"+File.separator+"font-family"+File.separator+"Arial.ttf",BaseFont.WINANSI, BaseFont.EMBEDDED);
			this.arial_normal_10 = new Font(bf_arial_normal_10, 10, Font.NORMAL);
			BaseFont bf_lucida_sans_unicode_9 = BaseFont.createFont("pdf"+File.separator+"font-family"+File.separator+"Lucida Sans Unicode.ttf",BaseFont.WINANSI, BaseFont.EMBEDDED);
			this.lucida_sans_unicode_9 = new Font(bf_lucida_sans_unicode_9, 9, Font.NORMAL);
			BaseFont bf_verdana_normal_8 = BaseFont.createFont("pdf"+File.separator+"font-family"+File.separator+"Verdana.ttf",BaseFont.WINANSI, BaseFont.EMBEDDED);
			this.verdana_normal_8 = new Font(bf_verdana_normal_8, 8, Font.NORMAL);
			BaseFont bf_verdana_7 = BaseFont.createFont("pdf"+File.separator+"font-family"+File.separator+"Verdana.ttf",BaseFont.WINANSI, BaseFont.EMBEDDED);
			this.verdana_bold_7 = new Font(bf_verdana_7, 7, Font.BOLD);
			BaseFont bf_verdana_8 = BaseFont.createFont("pdf"+File.separator+"font-family"+File.separator+"Verdana.ttf",BaseFont.WINANSI, BaseFont.EMBEDDED);
			this.verdana_bold_8 = new Font(bf_verdana_8, 8, Font.BOLD);
			BaseFont bf_verdana_normal_white_8 = BaseFont.createFont("pdf"+File.separator+"font-family"+File.separator+"Verdana.ttf",BaseFont.WINANSI, BaseFont.EMBEDDED);
			this.verdana_normal_white_8 = new Font(bf_verdana_normal_white_8, 8, Font.NORMAL, BaseColor.WHITE);
			BaseFont bf_verdana_bold_10 = BaseFont.createFont("pdf"+File.separator+"font-family"+File.separator+"Verdana.ttf",BaseFont.WINANSI, BaseFont.EMBEDDED);
			this.verdana_bold_10 = new Font(bf_verdana_bold_10, 10, Font.BOLD);
			BaseFont bf_verdana_bold_white_10 = BaseFont.createFont("pdf"+File.separator+"font-family"+File.separator+"Verdana.ttf",BaseFont.WINANSI, BaseFont.EMBEDDED);
			this.verdana_bold_white_10 = new Font(bf_verdana_bold_white_10, 10, Font.BOLD, BaseColor.WHITE);
			BaseFont bf_verdana_normal_14 = BaseFont.createFont("pdf"+File.separator+"font-family"+File.separator+"Verdana.ttf",BaseFont.WINANSI, BaseFont.EMBEDDED);
			this.verdana_normal_14 = new Font(bf_verdana_normal_14, 14, Font.NORMAL);
			BaseFont bf_tahoma_10 = BaseFont.createFont("pdf"+File.separator+"font-family"+File.separator+"tahoma.ttf",BaseFont.WINANSI, BaseFont.EMBEDDED);
			this.tahoma_normal_10 = new Font(bf_tahoma_10, 10, Font.NORMAL);
			BaseFont bf_tahoma_bold_10 = BaseFont.createFont("pdf"+File.separator+"font-family"+File.separator+"tahomabd.ttf",BaseFont.WINANSI, BaseFont.EMBEDDED);
			this.tahoma_bold_10 = new Font(bf_tahoma_bold_10, 10, Font.NORMAL);
			BaseFont bf_tahoma_bold_white_10 = BaseFont.createFont("pdf"+File.separator+"font-family"+File.separator+"tahomabd.ttf",BaseFont.WINANSI, BaseFont.EMBEDDED);
			this.tahoma_bold_white_10 = new Font(bf_tahoma_bold_white_10, 10, Font.NORMAL, BaseColor.WHITE);
		} catch (DocumentException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public void create(String outputFile) throws DocumentException, MalformedURLException, IOException{
        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputFile));
        document.open();
        
        /*
         *
         *  FIRST PAGE CONTENTS BEGIN
         * 
         */
        PdfPTable headerTable = new PdfPTable(1);
		PdfPCell cell = new PdfPCell(new Phrase(" "));
		// add common header
        cell.setPaddingLeft(50);
        cell.setBorder(0);
        headerTable.addCell(cell);
        headerTable.addCell(cell);
        headerTable.addCell(cell);
        headerTable.addCell(cell);
        headerTable.addCell(cell);
        headerTable.addCell(cell);
        headerTable.addCell(cell);
        headerTable.addCell(cell);
        headerTable.addCell(cell);
        headerTable.addCell(cell);
        Customer customer = this.getCustomer();
        cell.setPhrase(new Phrase(customer.getFirst_name()+" "+customer.getLast_name(), tahoma_normal_10));
        headerTable.addCell(cell);
        cell.setPhrase(new Phrase(customer.getAddress(), tahoma_normal_10));
        headerTable.addCell(cell);
        cell.setPhrase(new Phrase(" "));
        headerTable.addCell(cell);
        headerTable.addCell(cell);
        headerTable.addCell(cell);
        headerTable.addCell(cell);
        document.add(headerTable);
        
        /*
         * Recent Transactions Table begin
         */
        // non empty table start with 4 columns
        PdfPTable transactionTable = new PdfPTable(4);
        // page's width percentage
        transactionTable.setWidthPercentage(98);
        PdfPCell transactionTitleCell = newCell("Recent transactions", tahoma_bold_white_10, 0);
        transactionTitleCell.setPaddingBottom(4);
        transactionTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        transactionTitleCell.setBackgroundColor(titleBGColor);
        transactionTitleCell.setColspan(4);
        transactionTable.addCell(transactionTitleCell);
        
        // transaction columns
        PdfPCell transactionColumnsCell = newCell("Date", verdana_bold_7, 0);
        transactionColumnsCell.setPaddingTop(6);
        transactionColumnsCell.setIndent(10);
        transactionTable.addCell(transactionColumnsCell);
        transactionColumnsCell = newCell("Description", verdana_bold_7, 0);
        transactionColumnsCell.setPaddingTop(6);
        transactionColumnsCell.setColspan(2);
        transactionTable.addCell(transactionColumnsCell);
        transactionColumnsCell = newCell("Amount", verdana_bold_7, 0);
        transactionColumnsCell.setPaddingTop(6);
        transactionColumnsCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        transactionTable.addCell(transactionColumnsCell);
        
        // account filtering of last invoice begin
        CustomerInvoice currentCustomerInvoice = this.getCustomerInvoice();
        CustomerInvoice lastCustomerInvoice = this.getCustomerInvoice().getLastCustomerInvoice();
        Double lastAmountPayable = lastCustomerInvoice!=null && lastCustomerInvoice.getAmount_payable()!=null ? lastCustomerInvoice.getAmount_payable() : 0.0;
        Double lastAmountPaid = lastCustomerInvoice!=null && lastCustomerInvoice.getAmount_paid()!=null ? lastCustomerInvoice.getAmount_paid() : 0.0;
        Double lastBalance = lastCustomerInvoice!=null && lastCustomerInvoice.getBalance()!=null ? lastCustomerInvoice.getBalance() : 0.0;
//        if(lastAmountPayable==null){
//        	lastAmountPayable = 0.0;
//        }
//        if(lastAmountPaid==null){
//        	lastAmountPaid = 0.0;
//        }
//        if(lastBalance==null){
//        	lastBalance = 0.0;
//        }
        // account filtering of last invoice end
        
        // if last invoice isn't null then go into <if statement>, otherwise only Opening Balance appears
        if(lastCustomerInvoice!=null){
            // LAST INVOICE'S SITUATION
            transactionColumnsCell = newCell(lastCustomerInvoice.getCreate_date_str(), verdana_normal_8, 0);
            transactionColumnsCell.setIndent(10);
            transactionTable.addCell(transactionColumnsCell);
            transactionColumnsCell = newCell("Previous Invoice Total", verdana_normal_8, 0);
            transactionColumnsCell.setColspan(2);
            transactionTable.addCell(transactionColumnsCell);
            transactionColumnsCell = newCell("$ " + TMUtils.fillDecimal(String.valueOf(lastAmountPayable)), verdana_normal_8, 0);
            transactionColumnsCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            transactionTable.addCell(transactionColumnsCell);
            
            // CURRENT INVOICE'S SITUATION
            transactionColumnsCell = newCell(lastCustomerInvoice.getPaid_date_str(), verdana_normal_8, 0);
            transactionColumnsCell.setIndent(10);
            transactionTable.addCell(transactionColumnsCell);
            transactionColumnsCell = newCell(lastCustomerInvoice.getPaid_type(), verdana_normal_8, 0);
            transactionColumnsCell.setColspan(2);
            transactionTable.addCell(transactionColumnsCell);
            transactionColumnsCell = newCell("$ -" + TMUtils.fillDecimal(String.valueOf(lastAmountPaid)), verdana_normal_8, 0);
            transactionColumnsCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            transactionTable.addCell(transactionColumnsCell);
            
            // SEPARATOR ROW
            transactionColumnsCell = newCell("________________", verdana_normal_8, 0);
            transactionColumnsCell.setColspan(4);
            transactionColumnsCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            transactionTable.addCell(transactionColumnsCell);

            // TOTAL AMOUNT ROW
            transactionColumnsCell = newCell("Opening Balance", verdana_bold_10, 0);
            transactionColumnsCell.setColspan(3);
            transactionColumnsCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            transactionTable.addCell(transactionColumnsCell);
            transactionColumnsCell = newCell("$ "+ TMUtils.fillDecimal(String.valueOf(lastBalance)), verdana_bold_10, 0);
            transactionColumnsCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            transactionTable.addCell(transactionColumnsCell);
            PdfPCell lastCell = new PdfPCell(new Phrase(" "));
            lastCell.setBorder(0);
            lastCell.setColspan(4);
            transactionTable.addCell(lastCell);
            document.add(transactionTable);
        } else {
            // CURRENT INVOICE'S SITUATION
            transactionColumnsCell = newCell(currentCustomerInvoice.getCreate_date_str(), verdana_normal_8, 0);
            transactionColumnsCell.setIndent(10);
            transactionTable.addCell(transactionColumnsCell);
            transactionColumnsCell = newCell("Current Invoice Total", verdana_normal_8, 0);
            transactionColumnsCell.setColspan(2);
            transactionTable.addCell(transactionColumnsCell);
            transactionColumnsCell = newCell("$ " + TMUtils.fillDecimal(String.valueOf(currentCustomerInvoice.getAmount_payable())), verdana_normal_8, 0);
            transactionColumnsCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            transactionTable.addCell(transactionColumnsCell);
            
            // CURRENT INVOICE'S SITUATION
            transactionColumnsCell = newCell(currentCustomerInvoice.getPaid_date_str(), verdana_normal_8, 0);
            transactionColumnsCell.setIndent(10);
            transactionTable.addCell(transactionColumnsCell);
            transactionColumnsCell = newCell(currentCustomerInvoice.getPaid_type(), verdana_normal_8, 0);
            transactionColumnsCell.setColspan(2);
            transactionTable.addCell(transactionColumnsCell);
            transactionColumnsCell = newCell("$ -" + TMUtils.fillDecimal(String.valueOf(currentCustomerInvoice.getAmount_paid())), verdana_normal_8, 0);
            transactionColumnsCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            transactionTable.addCell(transactionColumnsCell);
            
            // SEPARATOR ROW
            transactionColumnsCell = newCell("________________", verdana_normal_8, 0);
            transactionColumnsCell.setColspan(4);
            transactionColumnsCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            transactionTable.addCell(transactionColumnsCell);

            // TOTAL AMOUNT ROW
            transactionColumnsCell = newCell("Opening Balance", verdana_bold_10, 0);
            transactionColumnsCell.setColspan(3);
            transactionColumnsCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            transactionTable.addCell(transactionColumnsCell);
            transactionColumnsCell = newCell("$ "+ TMUtils.fillDecimal(String.valueOf(currentCustomerInvoice.getBalance())), verdana_bold_10, 0);
            transactionColumnsCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            transactionTable.addCell(transactionColumnsCell);
            PdfPCell lastCell = new PdfPCell(new Phrase(" "));
            lastCell.setBorder(0);
            lastCell.setColspan(4);
            transactionTable.addCell(lastCell);
            document.add(transactionTable);
        }
        
        /*
         * THIS INVOICE SUMMARY TABLE BEGIN
         */
        PdfPTable invoiceSummaryTable = new PdfPTable(4);
        // page's width percentage
        invoiceSummaryTable.setWidthPercentage(98);
        PdfPCell invoiceSummaryTitleCell = newCell("This Invoice Summary", tahoma_bold_white_10, 0);
        invoiceSummaryTitleCell.setPaddingBottom(4);
        invoiceSummaryTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        invoiceSummaryTitleCell.setBackgroundColor(titleBGColor);
        invoiceSummaryTitleCell.setColspan(4);
        invoiceSummaryTable.addCell(invoiceSummaryTitleCell);
        
        // INVOICE SUMMARY COLUMNS
        PdfPCell invoiceSummaryColumnsCell = newCell("Net charges", arial_normal_8, 0);
        invoiceSummaryColumnsCell.setPaddingTop(10);
        invoiceSummaryColumnsCell.setIndent(10);
        invoiceSummaryColumnsCell.setColspan(3);
        invoiceSummaryTable.addCell(invoiceSummaryColumnsCell);
        // this invoice detail
        Double currentInvoiceDetailTotalPrice = 0.0;
        List<CustomerInvoiceDetail> listInvoiceDetails = currentCustomerInvoice.getCustomerInvoiceDetails();
        Iterator<CustomerInvoiceDetail> iterInvoiceDetails = listInvoiceDetails.iterator();
    	// get invoice detail(s) from invoice
        while(iterInvoiceDetails.hasNext()){
        	CustomerInvoiceDetail customerInvoiceDetail = iterInvoiceDetails.next();
        	// if both empty
        	Double subTotal = 0.0;
        	// if price and unit are not null
        	if(customerInvoiceDetail.getInvoice_detail_price() != null && customerInvoiceDetail.getInvoice_detail_unit() != null){
        		// price * unit
        		subTotal = (customerInvoiceDetail.getInvoice_detail_price()*customerInvoiceDetail.getInvoice_detail_unit());
        	}
        	// if price empty
        	if(customerInvoiceDetail.getInvoice_detail_price() == null && customerInvoiceDetail.getInvoice_detail_unit() != null){
        		// price == null then sub total = 0
        		subTotal = 0.0;
        	}
        	// if unit empty
        	if(customerInvoiceDetail.getInvoice_detail_price() != null && customerInvoiceDetail.getInvoice_detail_unit() == null){
        		// price * 1
        		subTotal = (customerInvoiceDetail.getInvoice_detail_price()*1);
        	}
        	currentInvoiceDetailTotalPrice+=subTotal;
        }
        // current invoice
        // this invoice amount is consist of (current invoice total amount + last invoice balance - current customer invoice paid fees)
        Double thisInvoicetotalAmount = currentInvoiceDetailTotalPrice + lastBalance - currentCustomerInvoice.getAmount_paid();
        Double afterTaxAmount = 0.0;
        Double taxAmount = 0.0;
        if(thisInvoicetotalAmount > 0){
        	// plus tax fee
            afterTaxAmount = thisInvoicetotalAmount/1.15;
            
            // tax fee
            taxAmount = thisInvoicetotalAmount-thisInvoicetotalAmount/1.15;
        } else {
        	afterTaxAmount = thisInvoicetotalAmount;
        	taxAmount = thisInvoicetotalAmount;
        }
        invoiceSummaryColumnsCell = newCell("$ " + TMUtils.fillDecimal(String.valueOf(afterTaxAmount)), verdana_normal_8, 0);
        invoiceSummaryColumnsCell.setPaddingTop(10);
        invoiceSummaryColumnsCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        invoiceSummaryTable.addCell(invoiceSummaryColumnsCell);
        
        // INVOICE SUMMARY FIRST ROW
        invoiceSummaryColumnsCell = newCell("GST at 15%", verdana_normal_8, 0);
        invoiceSummaryColumnsCell.setIndent(10);
        invoiceSummaryColumnsCell.setColspan(3);
        invoiceSummaryTable.addCell(invoiceSummaryColumnsCell);
        invoiceSummaryColumnsCell = newCell("$ " + TMUtils.fillDecimal(String.valueOf(taxAmount)), verdana_normal_8, 0);
        invoiceSummaryColumnsCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        invoiceSummaryTable.addCell(invoiceSummaryColumnsCell);
        
        // INVOICE SUMMARY SECOND ROW
        invoiceSummaryColumnsCell = newCell("Total charges (please see Invoice Details page)", verdana_normal_8, 0);
        invoiceSummaryColumnsCell.setIndent(10);
        invoiceSummaryColumnsCell.setColspan(3);
        invoiceSummaryTable.addCell(invoiceSummaryColumnsCell);
        // include tax fee
        invoiceSummaryColumnsCell = newCell("$ " + TMUtils.fillDecimal(String.valueOf(thisInvoicetotalAmount)), verdana_normal_8, 0);
        invoiceSummaryColumnsCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        invoiceSummaryTable.addCell(invoiceSummaryColumnsCell);
        invoiceSummaryColumnsCell = newCell(" ", arial_normal_8, 0);
        invoiceSummaryColumnsCell.setColspan(4);
        invoiceSummaryTable.addCell(invoiceSummaryColumnsCell);
        invoiceSummaryTable.addCell(invoiceSummaryColumnsCell);
        invoiceSummaryTable.addCell(invoiceSummaryColumnsCell);
        
        // INVOICE SUMMARY SEPARATOR ROW
        invoiceSummaryColumnsCell = newCell("________________", verdana_normal_8, 0);
        invoiceSummaryColumnsCell.setColspan(4);
        invoiceSummaryColumnsCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        invoiceSummaryTable.addCell(invoiceSummaryColumnsCell);

        // INVOICE SUMMARY INVOICE TOTAL DUE ROW
        invoiceSummaryColumnsCell = newCell("Invoice total due on", arial_normal_10, 0);
        invoiceSummaryColumnsCell.setColspan(2);
        invoiceSummaryColumnsCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        invoiceSummaryTable.addCell(invoiceSummaryColumnsCell);
        invoiceSummaryColumnsCell = newCell(currentCustomerInvoice.getDue_date_str(), verdana_bold_10, 0);
        invoiceSummaryColumnsCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        invoiceSummaryTable.addCell(invoiceSummaryColumnsCell);
        invoiceSummaryColumnsCell = newCell("$ " + TMUtils.fillDecimal(String.valueOf(thisInvoicetotalAmount)), verdana_bold_10, 0);
        invoiceSummaryColumnsCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        invoiceSummaryTable.addCell(invoiceSummaryColumnsCell);
        document.add(invoiceSummaryTable);
        /*
         * THIS INVOICE SUMMARY TABLE END
         */
        
        /*
         * PAYMENT SLIP TABLE BEGIN
         */
        PdfPTable paymentSlipTable = new PdfPTable(5);
        paymentSlipTable.setTotalWidth(535);
        PdfPCell paymentSlipCell = newCell(" ", arial_normal_10, 0);
        paymentSlipCell.setColspan(5);
        Image img = Image.getInstance("pdf"+File.separator+"img"+File.separator+"scissor_separator.png");
        img.setWidthPercentage(100);
        paymentSlipCell.setImage(img);
        paymentSlipTable.addCell(paymentSlipCell);

        // WHITE TITLE
        paymentSlipCell = newCell(this.getCompanyDetail().getName(), verdana_bold_8, 0);
        paymentSlipCell.setRowspan(4);
        paymentSlipTable.addCell(paymentSlipCell);
        paymentSlipCell = newCell("Payment Slip", verdana_bold_10, 0);
        paymentSlipCell.setPaddingTop(6);
        paymentSlipCell.setRowspan(4);
        paymentSlipTable.addCell(paymentSlipCell);
        
        // LIGHT GRAY TITLE
        paymentSlipCell = newCell(" ", verdana_bold_8, 0, new BaseColor(234,234,234));
        paymentSlipCell.setBorderColorRight(BaseColor.WHITE);
        paymentSlipCell.setBorderWidthRight(1);
        paymentSlipTable.addCell(paymentSlipCell);
        paymentSlipCell = newCell(" ", verdana_bold_8, 0, new BaseColor(234,234,234));
        paymentSlipCell.setBorderColorRight(BaseColor.WHITE);
        paymentSlipCell.setBorderWidthRight(1);
        paymentSlipTable.addCell(paymentSlipCell);
        paymentSlipCell = newCell(" ", verdana_bold_8, 0, new BaseColor(234,234,234));
        paymentSlipTable.addCell(paymentSlipCell);
        paymentSlipCell = newCell("Customer ID", verdana_bold_8, 0, new BaseColor(234,234,234));
        paymentSlipCell.setBorderColorRight(BaseColor.WHITE);
        paymentSlipCell.setBorderWidthRight(1);
        paymentSlipCell.setIndent(14);
        paymentSlipTable.addCell(paymentSlipCell);
        paymentSlipCell = newCell("Invoice Number", verdana_bold_8, 0, new BaseColor(234,234,234));
        paymentSlipCell.setBorderColorRight(BaseColor.WHITE);
        paymentSlipCell.setBorderWidthRight(1);
        paymentSlipCell.setIndent(14);
        paymentSlipTable.addCell(paymentSlipCell);
        paymentSlipCell = newCell("Due Date", verdana_bold_8, 0, new BaseColor(234,234,234));
        paymentSlipCell.setBorderColorRight(BaseColor.WHITE);
        paymentSlipCell.setIndent(14);
        paymentSlipTable.addCell(paymentSlipCell);

        // LIGHT GRAY VALUE
        paymentSlipCell = newCell(this.getCustomer().getId().toString(), arial_normal_6, 0, new BaseColor(234,234,234));
        paymentSlipCell.setBorderColorRight(BaseColor.WHITE);
        paymentSlipCell.setBorderWidthRight(1);
        paymentSlipCell.setIndent(14);
        paymentSlipCell.setPaddingTop(6);
        paymentSlipTable.addCell(paymentSlipCell);
        paymentSlipCell = newCell(currentCustomerInvoice.getId().toString(), arial_normal_7, 0, new BaseColor(234,234,234));
        paymentSlipCell.setBorderColorRight(BaseColor.WHITE);
        paymentSlipCell.setBorderWidthRight(1);
        paymentSlipCell.setIndent(14);
        paymentSlipCell.setPaddingTop(6);
        paymentSlipTable.addCell(paymentSlipCell);
        paymentSlipCell = newCell(currentCustomerInvoice.getDue_date_str(), arial_normal_7, 0, new BaseColor(234,234,234));
        paymentSlipCell.setIndent(14);
        paymentSlipCell.setPaddingTop(6);
        paymentSlipTable.addCell(paymentSlipCell);
        paymentSlipCell = newCell(" ", verdana_bold_8, 0, new BaseColor(234,234,234));
        paymentSlipCell.setBorderColorRight(BaseColor.WHITE);
        paymentSlipCell.setBorderWidthRight(1);
        paymentSlipTable.addCell(paymentSlipCell);
        paymentSlipCell = newCell(" ", verdana_bold_8, 0, new BaseColor(234,234,234));
        paymentSlipCell.setBorderColorRight(BaseColor.WHITE);
        paymentSlipCell.setBorderWidthRight(1);
        paymentSlipTable.addCell(paymentSlipCell);
        paymentSlipCell = newCell(" ", verdana_bold_8, 0, new BaseColor(234,234,234));
        paymentSlipTable.addCell(paymentSlipCell);
        paymentSlipCell.setColspan(0);
        
        // SEPARATOR BEGIN
        paymentSlipCell = newCell(" ", arial_normal_8, 0);
        paymentSlipCell.setFixedHeight(3);
        paymentSlipTable.addCell(paymentSlipCell);
        paymentSlipCell = newCell(" ", arial_normal_8, 0);
        paymentSlipCell.setFixedHeight(3);
        paymentSlipTable.addCell(paymentSlipCell);
        paymentSlipCell = newCell(" ", arial_normal_8, 0);
        paymentSlipCell.setFixedHeight(3);
        paymentSlipTable.addCell(paymentSlipCell);
        paymentSlipCell = newCell(" ", arial_normal_8, 0);
        paymentSlipCell.setFixedHeight(3);
        paymentSlipTable.addCell(paymentSlipCell);
        paymentSlipCell = newCell(" ", arial_normal_8, 0);
        paymentSlipCell.setFixedHeight(3);
        paymentSlipTable.addCell(paymentSlipCell);
        // SEPARATOR END

		
        // SECOND SECTION
        paymentSlipCell = newCell("Paying By Direct Credit", arial_normal_8, 0);
        paymentSlipCell.setIndent(4);
        paymentSlipCell.setColspan(2);
        paymentSlipTable.addCell(paymentSlipCell);
        paymentSlipCell = newCell("Total amount due before", arial_normal_white_8, 0, totleChequeAmountBGColor);
        paymentSlipCell.setPaddingTop(8);
        paymentSlipCell.setIndent(14);
        paymentSlipCell.setRowspan(2);
        paymentSlipTable.addCell(paymentSlipCell);
        paymentSlipCell = newCell(currentCustomerInvoice.getDue_date_str(), arial_normal_white_8, 0, totleChequeAmountBGColor);
        paymentSlipCell.setIndent(32);
        paymentSlipCell.setPaddingTop(8);
        paymentSlipCell.setRowspan(2);
        paymentSlipTable.addCell(paymentSlipCell);
        // input box begin
        paymentSlipCell = newCell(TMUtils.fillDecimal(String.valueOf(thisInvoicetotalAmount)), arial_normal_8, 0, BaseColor.WHITE);
        paymentSlipCell.setUseBorderPadding(true);
        paymentSlipCell.setBorderColor(totleChequeAmountBGColor);
        paymentSlipCell.setBorderWidthTop(8f);
        paymentSlipCell.setBorderWidthRight(8f);
        paymentSlipCell.setBorderWidthBottom(8f);
        paymentSlipCell.setBorderWidthLeft(8f);
        paymentSlipCell.setRowspan(2);
        paymentSlipCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        paymentSlipTable.addCell(paymentSlipCell);
        // input box end
        paymentSlipCell = newCell("Bank: "+this.getCompanyDetail().getBank_name(), arial_normal_8, 0);
        paymentSlipCell.setIndent(4);
        paymentSlipCell.setColspan(2);
        paymentSlipTable.addCell(paymentSlipCell);
        paymentSlipCell = newCell("Name of Account: "+this.getCompanyDetail().getBank_account_name(), arial_normal_8, 0);
        paymentSlipCell.setIndent(4);
        paymentSlipCell.setColspan(2);
        paymentSlipTable.addCell(paymentSlipCell);
        paymentSlipCell = newCell("Total amount due after", arial_normal_white_8, 0, totleChequeAmountBGColor);
        paymentSlipCell.setPaddingTop(8);
        paymentSlipCell.setIndent(14);
        paymentSlipCell.setRowspan(2);
        paymentSlipTable.addCell(paymentSlipCell);
        paymentSlipCell = newCell(currentCustomerInvoice.getDue_date_str(), arial_normal_white_8, 0, totleChequeAmountBGColor);
        paymentSlipCell.setIndent(32);
        paymentSlipCell.setPaddingTop(8);
        paymentSlipCell.setRowspan(2);
        paymentSlipCell.setBorderColor(totleChequeAmountBGColor);
        paymentSlipCell.setBorderWidthRight(2f);
        paymentSlipTable.addCell(paymentSlipCell);
        // input box begin
        paymentSlipCell = newCell(TMUtils.fillDecimal(String.valueOf(thisInvoicetotalAmount)), arial_normal_8, 0, BaseColor.WHITE);
        paymentSlipCell.setUseBorderPadding(true);
        paymentSlipCell.setBorderColor(totleChequeAmountBGColor);
        paymentSlipCell.setBorderWidthTop(8f);
        paymentSlipCell.setBorderWidthRight(8f);
        paymentSlipCell.setBorderWidthBottom(8f);
        paymentSlipCell.setBorderWidthLeft(8f);
        paymentSlipCell.setRowspan(2);
        paymentSlipCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        paymentSlipTable.addCell(paymentSlipCell);
        // input box end
        paymentSlipCell = newCell("Account Number: "+this.getCompanyDetail().getBank_account_number(), arial_normal_8, 0);
        paymentSlipCell.setIndent(4);
        paymentSlipCell.setColspan(2);
        paymentSlipTable.addCell(paymentSlipCell);
        
        // THIRD SECTION
        paymentSlipCell = newCell(" ", arial_normal_8, 0);
        paymentSlipCell.setColspan(5);
        paymentSlipCell.setFixedHeight(3);
        paymentSlipTable.addCell(paymentSlipCell);
        
        // SEPARATOR BEGIN
        paymentSlipCell = newCell(" ", arial_normal_8, 0, totleChequeAmountBGColor);
        paymentSlipCell.setFixedHeight(2);
        paymentSlipTable.addCell(paymentSlipCell);
        paymentSlipCell = newCell(" ", arial_normal_8, 0, totleChequeAmountBGColor);
        paymentSlipCell.setFixedHeight(2);
        paymentSlipTable.addCell(paymentSlipCell);
        paymentSlipCell = newCell(" ", arial_normal_8, 0, totleChequeAmountBGColor);
        paymentSlipCell.setFixedHeight(2);
        paymentSlipTable.addCell(paymentSlipCell);
        paymentSlipCell = newCell(" ", arial_normal_8, 0, totleChequeAmountBGColor);
        paymentSlipCell.setFixedHeight(2);
        paymentSlipTable.addCell(paymentSlipCell);
        paymentSlipCell = newCell(" ", arial_normal_8, 0, totleChequeAmountBGColor);
        paymentSlipCell.setFixedHeight(2);
        paymentSlipTable.addCell(paymentSlipCell);
        // SEPARATOR END
        
        // 
        paymentSlipCell = newCell("Paying by cheques", verdana_normal_white_8, 0, totleChequeAmountBGColor);
        paymentSlipCell.setIndent(4);
        paymentSlipCell.setColspan(3);
        paymentSlipTable.addCell(paymentSlipCell);
        paymentSlipCell = newCell("ENCLOSED AMOUNT", verdana_bold_white_10, 0, totleChequeAmountBGColor);
        paymentSlipCell.setPaddingTop(4);
        paymentSlipCell.setIndent(40);
        paymentSlipCell.setColspan(2);
        paymentSlipTable.addCell(paymentSlipCell);
        paymentSlipCell = newCell("Please make cheques payable to Total Mobile Solution Services Ltd and", verdana_normal_white_8, 0, totleChequeAmountBGColor);
        paymentSlipCell.setIndent(4);
        paymentSlipCell.setColspan(3);
        paymentSlipTable.addCell(paymentSlipCell);
		// boxes begin
		Image sBox = Image.getInstance("pdf"+File.separator+"img"+File.separator+"box_large_white.png");
		sBox.scaleAbsolute(155.25f, 35.25f);
		sBox.setAbsolutePosition(0, 0);
		writer.getDirectContent().addImage(sBox);
        paymentSlipCell = newCell(sBox, arial_normal_white_8, 0, totleChequeAmountBGColor);
        paymentSlipCell.setPaddingLeft(42);
        paymentSlipCell.setPaddingTop(6);
        paymentSlipCell.setRowspan(4);
        paymentSlipCell.setColspan(2);
        paymentSlipTable.addCell(paymentSlipCell);
		// boxes end
        paymentSlipCell = newCell("write your Name and Phone Number on the back of your cheque.", verdana_normal_white_8, 0, totleChequeAmountBGColor);
        paymentSlipCell.setIndent(4);
        paymentSlipCell.setColspan(3);
        paymentSlipTable.addCell(paymentSlipCell);
        paymentSlipCell = newCell(" ", arial_normal_8, 0, totleChequeAmountBGColor);
        paymentSlipCell.setColspan(3);
        paymentSlipTable.addCell(paymentSlipCell);
        paymentSlipCell = newCell("Please post it with this payment slip to", verdana_normal_white_8, 0, totleChequeAmountBGColor);
        paymentSlipCell.setIndent(4);
        paymentSlipCell.setColspan(3);
        paymentSlipTable.addCell(paymentSlipCell);
        paymentSlipCell = newCell(this.getCompanyDetail().getName()+" "+this.getCompanyDetail().getAddress(), verdana_normal_white_8, 0, totleChequeAmountBGColor);
        paymentSlipCell.setIndent(4);
        paymentSlipCell.setColspan(3);
        paymentSlipTable.addCell(paymentSlipCell);
        paymentSlipCell = newCell("** PLEASE DO NOT SEND CASH", verdana_normal_white_8, 0, totleChequeAmountBGColor);
        paymentSlipCell.setIndent(40);
        paymentSlipCell.setColspan(2);
        paymentSlipTable.addCell(paymentSlipCell);
        paymentSlipCell = newCell(" ", arial_normal_8, 0, totleChequeAmountBGColor);
        paymentSlipCell.setColspan(4);
        paymentSlipCell.setFixedHeight(2);
        paymentSlipTable.addCell(paymentSlipCell);
        paymentSlipCell = newCell(" ", arial_normal_8, 0, totleChequeAmountBGColor);
        paymentSlipCell.setColspan(1);
        paymentSlipCell.setFixedHeight(2);
        paymentSlipTable.addCell(paymentSlipCell);
        // complete the table
        paymentSlipTable.completeRow();
        // write the table to an absolute position
        PdfContentByte paymentSlipTableCanvas = writer.getDirectContent();
        paymentSlipTable.writeSelectedRows(0, -1, (PageSize.A4.getWidth()-paymentSlipTable.getTotalWidth())/2, paymentSlipTable.getTotalHeight() + 28, paymentSlipTableCanvas);
        /*
         * PAYMENT SLIP TABLE END
         */

        // FIRST PAGE'S HEADER
        headerTable = new PdfPTable(1);
		cell = new PdfPCell(new Phrase(" "));
		pageHeader(writer, headerTable, cell);
        /*
         *
         *  FIRST PAGE CONTENTS END
         * 
         */

        /*
         *
         *  SECOND PAGE CONTENTS BEGIN
         * 
         */
		// start new page
        document.newPage();

        PdfPTable invoiceDetailsTable = new PdfPTable(10);
        invoiceDetailsTable.setWidthPercentage(98);
        PdfPCell invoiceDetailsTitleCell = new PdfPCell(new Phrase(" "));
        invoiceDetailsTitleCell.setBorder(0);
        invoiceDetailsTitleCell.setColspan(10);
        invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
        invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
        invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
        invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
        invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
        invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
        invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
        invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
        invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
        invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
        // page's width percentage
        invoiceDetailsTitleCell = newCell("Invoice Details", tahoma_bold_white_10, 0);
        invoiceDetailsTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        invoiceDetailsTitleCell.setColspan(10);
        invoiceDetailsTitleCell.setBackgroundColor(this.titleBGColor);
        invoiceDetailsTitleCell.setPaddingBottom(4);
        invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
        invoiceDetailsTitleCell = newCell(" ", tahoma_bold_10, 0);
        invoiceDetailsTitleCell.setFixedHeight(20);
        invoiceDetailsTitleCell.setColspan(10);
        invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
        
        // title
        invoiceDetailsTitleCell = newCell("Service / Product", verdana_bold_7, 0);
        invoiceDetailsTitleCell.setIndent(14);
        invoiceDetailsTitleCell.setBorderWidthBottom(1);
        invoiceDetailsTitleCell.setFixedHeight(16);
        invoiceDetailsTitleCell.setColspan(4);
        invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
        invoiceDetailsTitleCell = newCell("Date", verdana_bold_7, 0);
        invoiceDetailsTitleCell.setIndent(32);
        invoiceDetailsTitleCell.setBorderWidthBottom(1);
        invoiceDetailsTitleCell.setFixedHeight(16);
        invoiceDetailsTitleCell.setColspan(2);
        invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
        invoiceDetailsTitleCell = newCell("Unit Price", verdana_bold_7, 0);
        invoiceDetailsTitleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        invoiceDetailsTitleCell.setBorderWidthBottom(1);
        invoiceDetailsTitleCell.setFixedHeight(16);
        invoiceDetailsTitleCell.setColspan(1);
        invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
        invoiceDetailsTitleCell = newCell("Discount", verdana_bold_7, 0);
        invoiceDetailsTitleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        invoiceDetailsTitleCell.setBorderWidthBottom(1);
        invoiceDetailsTitleCell.setFixedHeight(16);
        invoiceDetailsTitleCell.setColspan(1);
        invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
        invoiceDetailsTitleCell = newCell("Qty", verdana_bold_7, 0);
        invoiceDetailsTitleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        invoiceDetailsTitleCell.setBorderWidthBottom(1);
        invoiceDetailsTitleCell.setFixedHeight(16);
        invoiceDetailsTitleCell.setColspan(1);
        invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
        invoiceDetailsTitleCell = newCell("Subtotal", verdana_bold_7, 0);
        invoiceDetailsTitleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        invoiceDetailsTitleCell.setBorderWidthBottom(1);
        invoiceDetailsTitleCell.setFixedHeight(16);
        invoiceDetailsTitleCell.setColspan(1);
        invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
        
        // PRODUCT(S) BEGIN
        invoiceDetailsTitleCell = newCell(" ", verdana_bold_7, 0);
        invoiceDetailsTitleCell.setFixedHeight(14);
        invoiceDetailsTitleCell.setPaddingTop(4);
        invoiceDetailsTitleCell.setIndent(14);
        invoiceDetailsTitleCell.setColspan(10);
        invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
        // PRODUCT ITEM(S) BEGIN
        Double totalPrice = 0.0;

        listInvoiceDetails = currentCustomerInvoice.getCustomerInvoiceDetails();
        iterInvoiceDetails = listInvoiceDetails.iterator();
    	// get invoice detail(s) from invoice
        while(iterInvoiceDetails.hasNext()){
        	CustomerInvoiceDetail customerInvoiceDetail = iterInvoiceDetails.next();
        	// if both empty
        	Double subTotal = 0.0;
        	Double price = 0.0;
        	Integer unit = 0;
        	if(customerInvoiceDetail.getInvoice_detail_price() != null && customerInvoiceDetail.getInvoice_detail_unit() != null){
        		subTotal = (customerInvoiceDetail.getInvoice_detail_price()*customerInvoiceDetail.getInvoice_detail_unit());
        		price = customerInvoiceDetail.getInvoice_detail_price();
        		unit = customerInvoiceDetail.getInvoice_detail_unit();
        	}
        	// if price empty
        	if(customerInvoiceDetail.getInvoice_detail_price() == null && customerInvoiceDetail.getInvoice_detail_unit() != null){
        		// price == null then sub total = 0
        		subTotal = 0.0;
        		price = 0.0;
        		unit = 0;
        	}
        	// if unit empty
        	if(customerInvoiceDetail.getInvoice_detail_price() != null && customerInvoiceDetail.getInvoice_detail_unit() == null){
        		// price * 1
        		subTotal = (customerInvoiceDetail.getInvoice_detail_price()*1);
        		price = customerInvoiceDetail.getInvoice_detail_price();
        		unit = customerInvoiceDetail.getInvoice_detail_unit();
        	}
			// plan name
			invoiceDetailsTitleCell = newCell(customerInvoiceDetail.getInvoice_detail_name(), arial_normal_7, 0);
			invoiceDetailsTitleCell.setIndent(22);
			invoiceDetailsTitleCell.setColspan(4);
			invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
			// term date
			invoiceDetailsTitleCell = newCell(" ", arial_normal_7, 0);
			invoiceDetailsTitleCell.setColspan(2);
			invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
			// plan unit price
			invoiceDetailsTitleCell = newCell(TMUtils.fillDecimal(String.valueOf(price)), arial_normal_7, 0);
			invoiceDetailsTitleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
			invoiceDetailsTitleCell = newCell(" ", arial_normal_7, 0);
			invoiceDetailsTitleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
			// unit
			invoiceDetailsTitleCell = newCell(unit.toString(), arial_normal_7, 0);
			invoiceDetailsTitleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
			// sub total
			invoiceDetailsTitleCell = newCell(TMUtils.fillDecimal(String.valueOf(subTotal)), arial_normal_7, 0);
			invoiceDetailsTitleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
			totalPrice+=subTotal;
        }
        // PRODUCT(S) END
        
        // #####EMTRY SPACE BEGIN
//        invoiceDetailsTitleCell = newCell(" ", arial_normal_7, 0);
//        invoiceDetailsTitleCell.setColspan(10);
//        invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
        // #####EMTRY SPACE END
        
        // PRODUCT BEGIN
//        invoiceDetailsTitleCell = newCell("Kanny@adsl.world-net.co.nz", verdana_bold_7, 0);
//        invoiceDetailsTitleCell.setFixedHeight(14);
//        invoiceDetailsTitleCell.setPaddingTop(4);
//        invoiceDetailsTitleCell.setIndent(14);
//        invoiceDetailsTitleCell.setColspan(10);
//        invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
        // PRODUCT ITEMS BEGIN
        // PRODUCT ITEM BEGIN
//        invoiceDetailsTitleCell = newCell("FS/FS 100G + 100G Internet Monthly Fee", arial_normal_7, 0);
//        invoiceDetailsTitleCell.setIndent(22);
//        invoiceDetailsTitleCell.setColspan(4);
//        invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
//        invoiceDetailsTitleCell = newCell("2014-02-19  -  2014-03-18", arial_normal_7, 0);
//        invoiceDetailsTitleCell.setColspan(2);
//        invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
//        invoiceDetailsTitleCell = newCell("$33.91", arial_normal_7, 0);
//        invoiceDetailsTitleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
//        invoiceDetailsTitleCell = newCell(" ", arial_normal_7, 0);
//        invoiceDetailsTitleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
//        invoiceDetailsTitleCell = newCell("1", arial_normal_7, 0);
//        invoiceDetailsTitleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
//        invoiceDetailsTitleCell = newCell("$33.91", arial_normal_7, 0);
//        invoiceDetailsTitleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
        // PRODUCT ITEM END
        // PRODUCT ITEMS END
        // PRODUCT END
        /*
         * 
         * PRODUCTS END
         * 
         */
        
        // #####SEPARATOR BEGIN
        invoiceDetailsTitleCell = newCell(" ", arial_normal_7, 0);
        invoiceDetailsTitleCell.setFixedHeight(8);
        invoiceDetailsTitleCell.setBorderWidthBottom(1);
        invoiceDetailsTitleCell.setColspan(10);
        invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
        // #####SEPARATOR END
        
        // TOTAL BEGIN
        // #####EMTRY SPACE BEGIN
        invoiceDetailsTitleCell = newCell(" ", arial_normal_7, 0);
        invoiceDetailsTitleCell.setColspan(10);
        invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
        // #####EMTRY SPACE END
        
        // FIRST ROW BEGIN
        invoiceDetailsTitleCell = newCell(" ", arial_normal_7, 0);
        invoiceDetailsTitleCell.setFixedHeight(14);
        invoiceDetailsTitleCell.setColspan(7);
        invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
        invoiceDetailsTitleCell = newCell("Total before GST", arial_normal_8, 0);
        invoiceDetailsTitleCell.setColspan(2);
        invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
        Double totalBeforeGST = totalPrice/1.15;
        // fill decimal, keep 2 decimals
        invoiceDetailsTitleCell = newCell(TMUtils.fillDecimal(String.valueOf(totalBeforeGST)), arial_normal_8, 0);
        invoiceDetailsTitleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
        // FIRST ROW END
        // SECOND ROW BEGIN
        invoiceDetailsTitleCell = newCell(" ", arial_normal_7, 0);
        invoiceDetailsTitleCell.setFixedHeight(14);
        invoiceDetailsTitleCell.setColspan(7);
        invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
        invoiceDetailsTitleCell = newCell("GST at 15%", arial_normal_8, 0);
        invoiceDetailsTitleCell.setColspan(2);
        invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
        Double totalGST = totalPrice-totalPrice/1.15;
        // fill decimal, keep 2 decimals
        invoiceDetailsTitleCell = newCell(TMUtils.fillDecimal(String.valueOf(totalGST)), arial_normal_8, 0);
        invoiceDetailsTitleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
        // SECOND ROW END
        
        // SEPARATOR BEGIN
        invoiceDetailsTitleCell = newCell(" ", arial_normal_7, 0);
        invoiceDetailsTitleCell.setColspan(6);
        invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
        invoiceDetailsTitleCell = newCell(" ", arial_normal_7, 0);
        invoiceDetailsTitleCell.setColspan(4);
        invoiceDetailsTitleCell.setBorderWidthTop(1);
        invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
        // SEPARATOR END
        
        // TOTAL AMOUNT BEGIN
        invoiceDetailsTitleCell = newCell(" ", arial_normal_7, 0);
        invoiceDetailsTitleCell.setColspan(7);
        invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
        invoiceDetailsTitleCell = newCell("Invoice Total", verdana_bold_8, 0);
        invoiceDetailsTitleCell.setColspan(2);
        invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
        invoiceDetailsTitleCell = newCell(TMUtils.fillDecimal(String.valueOf(totalPrice)), verdana_bold_8, 0);
        invoiceDetailsTitleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        invoiceDetailsTable.addCell(invoiceDetailsTitleCell);
        // TOTAL AMOUNT END
        
        // TOTAL END
        
        
        
        // ADD TABLE 2 DOCUMENT
        document.add(invoiceDetailsTable);
        
        /*
         * 
         * 
         * 
         */
        
        
        // SECOND PAGE'S HEADER
        headerTable = new PdfPTable(1);
        cell = new PdfPCell(new Phrase(" "));
		pageHeader(writer, headerTable, cell);
        /*
         *
         *  SECOND PAGE CONTENTS END
         * 
         */

        
        /*
         *
         *  THIRD PAGE CONTENTS BEGIN
         * 
         */

        /*
         *
         *  THIRD PAGE CONTENTS END
         * 
         */
		

        // THIRD PAGE'S HEADER
//        document.newPage();
//        headerTable = new PdfPTable(1);
//		cell = new PdfPCell(new Phrase(" "));
//		pageHeader(writer, headerTable, cell);
//		
		// CLOSE DOCUMENT
        document.close();
        
		
        
	}
	
	private void pageHeader(PdfWriter writer, PdfPTable headerTable, PdfPCell cell) throws MalformedURLException, IOException, DocumentException{
		headerTable.setTotalWidth(510);
        // logo & start
		Image logo = Image.getInstance("pdf"+File.separator+"img"+File.separator+"logo_top_final.png");
		logo.scaleAbsolute(171f, 45f);
		logo.setAbsolutePosition(44, 760);
		writer.getDirectContent().addImage(logo);
		
		Phrase t1 = new Phrase("Statement / Tax Invoice", verdana_normal_14);
		Phrase t2 = new Phrase("GST Registration Number: "+this.getCompanyDetail().getGst_registration_number(), lucida_sans_unicode_9);
		PdfContentByte canvas = writer.getDirectContentUnder();
		ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, t1, 44, 744, 0);
		ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, t2, 44, 730, 0);
		
		// seperator
        LineSeparator UNDERLINE = new LineSeparator(3, 100, titleBGColor, Element.ALIGN_CENTER, -2);
        // adds a separator
        Phrase seperator = new Phrase();
        seperator.add(UNDERLINE);
		/*
		 * header table begin
		 */
		cell.setBorder(0);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        headerTable.addCell(cell);
        headerTable.addCell(cell);
        cell.setPhrase(new Phrase(this.getCompanyDetail().getName(), arial_normal_8));
        headerTable.addCell(cell);
        cell.setPhrase(new Phrase(this.getCompanyDetail().getAddress(), arial_normal_8));
        headerTable.addCell(cell);
        cell.setPhrase(new Phrase("Tel: "+this.getCompanyDetail().getTelephone()+"  /  Fax: "+this.getCompanyDetail().getFax(), arial_normal_8));
        headerTable.addCell(cell);
        cell.setPhrase(new Phrase(this.getCompanyDetail().getDomain(), arial_normal_8));
        headerTable.addCell(cell);
        cell = new PdfPCell(seperator);
		cell.setBorder(0);
        headerTable.addCell(cell);
		/*
		 * header table end
		 */      
		/*
		 * invoice basics begin
		 */
		cell = new PdfPCell(new Phrase(" "));
        cell.setBorder(0);
        Paragraph basicsP = new Paragraph();
        Phrase date = new Phrase("Date: ", verdana_bold_8);
        Phrase invoiceNo = new Phrase(" | Invoice No: ", verdana_bold_8);
        Phrase loginName = new Phrase(" | Customer Id: ", verdana_bold_8);
        Phrase dateField = new Phrase(TMUtils.dateFormatYYYYMMDD(this.customerInvoice.getCreate_date()), arial_normal_8);
        Phrase invoiceNoField = new Phrase(this.customerInvoice.getId().toString(), arial_normal_8);
        Phrase loginNameField = new Phrase(this.getCustomer().getId().toString(), arial_normal_8);
        // put paragraph into table cell
        basicsP.add(date);
        basicsP.add(dateField);
        basicsP.add(invoiceNo);
        basicsP.add(invoiceNoField);
        basicsP.add(loginName);
        basicsP.add(loginNameField);
        cell.setPaddingTop(10);
        cell.setPaddingLeft(4);
        cell.addElement(basicsP);
        headerTable.addCell(cell);
		/*
		 * invoice basics end
		 */
        // complete the table
        headerTable.completeRow();
        // write the table to an absolute position
        PdfContentByte paymentSlipTableCanvas = writer.getDirectContent();
        headerTable.writeSelectedRows(0, -1, 41, 810, paymentSlipTableCanvas);
	}
	
	
	private PdfPCell newCell(Image sBox, Font font, int border, BaseColor color) {
		PdfPCell pcell = new PdfPCell(new Phrase("", font));
		pcell.addElement(sBox);
		pcell.setBorder(border);
		pcell.setBackgroundColor(color);
		return pcell;
	}

	public PdfPCell newCell(String content, Font font, int border){
		PdfPCell pcell = new PdfPCell(new Phrase(content, font));
		pcell.setBorder(border);
		return pcell;
	}
	
	public PdfPCell newCell(String content, Font font, int border, BaseColor color){
		PdfPCell pcell = new PdfPCell(new Phrase(content, font));
		pcell.setBorder(border);
		pcell.setBackgroundColor(color);
		return pcell;
	}

	public CustomerInvoice getCustomerInvoice() {
		return customerInvoice;
	}

	public void setCustomerInvoice(CustomerInvoice customerInvoice) {
		this.customerInvoice = customerInvoice;
	}

	public CompanyDetail getCompanyDetail() {
		return companyDetail;
	}

	public void setCompanyDetail(CompanyDetail companyDetail) {
		this.companyDetail = companyDetail;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	
}