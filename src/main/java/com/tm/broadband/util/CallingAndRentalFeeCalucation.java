package com.tm.broadband.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.tm.broadband.mapper.CallInternationalRateMapper;
import com.tm.broadband.mapper.CustomerCallRecordMapper;
import com.tm.broadband.mapper.CustomerCallingRecordCallplusMapper;
import com.tm.broadband.mapper.CustomerInvoiceMapper;
import com.tm.broadband.mapper.NZAreaCodeListMapper;
import com.tm.broadband.mapper.VOSVoIPCallRecordMapper;
import com.tm.broadband.mapper.VOSVoIPRateMapper;
import com.tm.broadband.model.CallInternationalRate;
import com.tm.broadband.model.CustomerCallRecord;
import com.tm.broadband.model.CustomerCallingRecordCallplus;
import com.tm.broadband.model.CustomerInvoice;
import com.tm.broadband.model.CustomerInvoiceDetail;
import com.tm.broadband.model.CustomerOrderDetail;
import com.tm.broadband.model.NZAreaCodeList;
import com.tm.broadband.model.VOSVoIPCallRecord;
import com.tm.broadband.model.VOSVoIPRate;
import com.tm.broadband.pdf.InvoicePDFCreator;
import com.tm.broadband.util.test.Console;

public class CallingAndRentalFeeCalucation {
	
	// BEGIN CustomerCallRecord OPERATION
	public static Double ccrRentalOperation(CustomerInvoice ci
			, Boolean isRegenerate, String pstn_number
			, List<CustomerInvoiceDetail> cids, Double totalAmountPayable
			, CustomerCallRecordMapper customerCallRecordMapper, CustomerInvoiceMapper ciMapper){
		
		Double totalAmountIncl = 0d;
		
		CustomerCallRecord ccrQuery = new CustomerCallRecord();
		
		if(isRegenerate){
			ccrQuery.getParams().put("where", "query_last_month_rental_records");
			ccrQuery.getParams().put("invoice_id", ci.getId());
		} else {
			ccrQuery.getParams().put("where", "query_unused_rental_records");
			ccrQuery.getParams().put("invoice_id", null);
		}
		ccrQuery.getParams().put("clear_service_id", TMUtils.formatPhoneNumber(pstn_number));
		List<CustomerCallRecord> ccrs = customerCallRecordMapper.selectCustomerCallRecord(ccrQuery);
		
		if(ccrs!=null && ccrs.size()>0){
			for (CustomerCallRecord ccr : ccrs) {
				
				CustomerInvoiceDetail cid = new CustomerInvoiceDetail();
				cid.setInvoice_detail_name(ccr.getBilling_description());
				cid.setInvoice_detail_desc(TMUtils.dateFormatYYYYMMDD(ccr.getDate_from())+" - "+TMUtils.dateFormatYYYYMMDD(ccr.getDate_to()));
				cid.setInvoice_detail_price(TMUtils.getRentalChargeFee(ccr.getDate_from(), "Smart Bundle package".equals(ccr.getBilling_description()) ? 18d : 6d));
				cid.setInvoice_detail_unit(1);
				
				cids.add(cid);
				
				totalAmountIncl = TMUtils.bigAdd(totalAmountIncl, cid.getInvoice_detail_price());
			}
			
			if(!isRegenerate){
				CustomerCallRecord ccr = new CustomerCallRecord();
				if(ci.getId()==null){
					ciMapper.insertCustomerInvoice(ci);
				}
				ccr.setInvoice_id(ci.getId());
				ccr.getParams().put("where", "last_rental_records");
				ccr.getParams().put("clear_service_id", pstn_number);
				ccr.getParams().put("invoice_id", null);
				customerCallRecordMapper.updateCustomerCallRecord(ccr);
			}
		}
		
		return TMUtils.bigAdd(totalAmountPayable, totalAmountIncl);
	}
	
	/**
	 * BEGIN Chorus, Callplus OPERATION
	 * @param ci
	 * @param isRegenerate
	 * @param pcms
	 * @param pstn_number
	 * @param cids
	 * @param invoicePDF
	 * @param totalPayableAmouont
	 * @param customerCallRecordMapper
	 * @param callInternationalRateMapper
	 * @param customerCallingRecordCallplusMapper
	 * @param ciMapper
	 * @param customerType
	 * @return
	 */
	public static Double ccrOperation(CustomerInvoice ci, Boolean isRegenerate
			, List<CustomerOrderDetail> pcms
			, String pstn_number, List<CustomerInvoiceDetail> cids
			, InvoicePDFCreator invoicePDF, Double totalPayableAmouont
			, CustomerCallRecordMapper customerCallRecordMapper
			, CallInternationalRateMapper callInternationalRateMapper
			, CustomerCallingRecordCallplusMapper customerCallingRecordCallplusMapper
			, CustomerInvoiceMapper ciMapper
			, String customerType){
		
		// Total calling record
		List<CustomerCallRecord> ccrs = new ArrayList<CustomerCallRecord>();
		// Total chargeable amount
		Double totalAmountIncl = 0d;
		// Total credit back
		Double totalCreditBack = 0d;
		
		String callType = "";
		
		// BEGIN Chorus Style Calling Record
		CustomerCallRecord ccrTemp = new CustomerCallRecord();
		ccrTemp.getParams().put("where", "query_last_call_records");
		ccrTemp.getParams().put("orderby", "ORDER BY charge_date_time ASC");
		if(isRegenerate){
			ccrTemp.getParams().put("invoice_id", ci.getId());
		} else {
			ccrTemp.getParams().put("invoice_id", null);
		}
		// PRODUCTION MODE CHANGE TO pstn_number
		ccrTemp.getParams().put("clear_service_id", //96272424
				TMUtils.formatPhoneNumber(pstn_number)
		);

		List<CustomerCallRecord> ccrsTemp = customerCallRecordMapper.selectCustomerCallRecord(ccrTemp);

		for (CustomerCallRecord customerCallRecord : ccrsTemp) {
			Console.log(customerCallRecord);
		}

		// ITERATIVELY ADD ALL CALL CHARGES
		for (CustomerCallRecord ccr : ccrsTemp) {

			// Residential local call free
			if("personal".equals(customerType.toLowerCase()) && "L".equals(ccr.getJuristiction())){
				continue;
			}
			
			// BEGIN Rate Operation
//			boolean is0900 = false; boolean isFax = false;
//			boolean isMobile = ccr.getBilling_description().toUpperCase().contains("MOBILE")
//					|| ccr.getBilling_description().toUpperCase().contains("OTH NETWRK");
//			boolean isNational = false; boolean isBusinessLocal = false;
			
			CallInternationalRate cir = new CallInternationalRate();
			switch (ccr.getJuristiction()) {
			case "O":	/* OTH NETWORK & Mobile */
				callType = "mobile";
				cir.getParams().put("area_prefix", "2");
				break;
			case "L":	/* Local (Auckland) */
				callType = "local";
				cir.getParams().put("area_prefix", "3");
				break;
			case "N":	/* National */
				callType = "national";
				cir.getParams().put("area_prefix", "3");
				break;
			case "I":	/* International */
				callType = "international";
				cir.getParams().put("area_prefix", ccr.getPhone_called().substring(0, ccr.getPhone_called().indexOf("-")));
				break;
			}
			List<CallInternationalRate> cirs = callInternationalRateMapper.selectCallInternationalRates(cir);
			boolean isOnRate = cirs!=null && cirs.size()>0 ? true : false;

			Double costPerMinute = 1d;
			if(isOnRate){ costPerMinute = cirs.get(0).getRate_cost(); }
			
			// DURATION/SECONDS
			Double duration = Double.parseDouble(TMUtils.fillDecimalTime(String.valueOf(TMUtils.bigDivide((double)ccr.getDuration(), 60d))));
			
			ccr.setCallType(TMUtils.strCapital(callType));
			
			if(//(is0900 || isFax || isNational || isBusinessLocal) || 
					isOnRate && duration>0){

				// If have reminder, then cut reminder and plus 1 minute, for example: 5.19 change to 6
				if(TMUtils.isReminder(String.valueOf(duration))){
					String durationStr = String.valueOf(duration);
					duration =  Double.parseDouble(durationStr.substring(0, durationStr.indexOf(".")))+1d;
				}

				// Process Present Calling Minutes
				totalCreditBack = processPresentCallingMinutes(pcms
						,cirs
						,duration
						,totalCreditBack
						,costPerMinute
						,callType);
				
				ccr.setAmount_incl(TMUtils.bigMultiply(duration, costPerMinute));
				totalAmountIncl = TMUtils.bigAdd(totalAmountIncl, TMUtils.bigMultiply(duration, costPerMinute));
				
			} else {
				totalAmountIncl = TMUtils.bigAdd(totalAmountIncl, ccr.getAmount_incl());
			}
			
			// END Rate Operation
			
			// FORMAT DURATION(second) TO TIME STYLE
			ccr.setFormated_duration(duration<=0 ? "" : TMUtils.timeFormat.format(Double.parseDouble(String.valueOf(duration))).replace(".", ":"));
			ccrs.add(ccr);
		}
		
		if(!isRegenerate){
			if(ccrs!=null && ccrs.size()>0){
				CustomerCallRecord ccrUpdate = new CustomerCallRecord();
				if(ci.getId()==null){
					ciMapper.insertCustomerInvoice(ci);
				}
				ccrUpdate.setInvoice_id(ci.getId());
				ccrUpdate.getParams().put("where", "last_call_records");
				ccrUpdate.getParams().put("clear_service_id", TMUtils.formatPhoneNumber(pstn_number));
				ccrUpdate.getParams().put("invoice_id", null);
				customerCallRecordMapper.updateCustomerCallRecord(ccrUpdate);
				ccrUpdate = null;
			}
		}
		
		// END Chorus Style Calling Record
		

		// BEGIN Callplus Style Calling Record
		CustomerCallingRecordCallplus ccrcQuery = new CustomerCallingRecordCallplus();
		ccrcQuery.getParams().put("where", "query_last_calling_records");
		ccrcQuery.getParams().put("orderby", "ORDER BY date ASC");
		ccrcQuery.getParams().put("original_number", TMUtils.formatPhoneNumber(pstn_number));
		if(isRegenerate){
			ccrcQuery.getParams().put("invoice_id", ci.getId());
		} else {
			ccrcQuery.getParams().put("invoice_id", null);
		}
		List<CustomerCallingRecordCallplus> ccrcs = customerCallingRecordCallplusMapper.selectCustomerCallingRecordCallplus(ccrcQuery);
		
		for (CustomerCallingRecordCallplus ccrc : ccrcs) {

			// Residential local call free
			if("personal".equals(customerType.toLowerCase()) && "L".equals(ccrc.getType())){
				continue;
			}
			
			CallInternationalRate cir = new CallInternationalRate();
			switch (ccrc.getType()) {
			case "M":	/* Mobile */
				callType = "mobile";
				cir.getParams().put("area_prefix", "2");
				break;
			case "MG":	/* Mobile Gsm */
				callType = "mobile";
				cir.getParams().put("area_prefix", "2");
				break;
			case "L":	/* Local (Auckland) */
				callType = "local";
				cir.getParams().put("area_prefix", "3");
				break;
			case "S":	/* Domestic */
				callType = "national";
				cir.getParams().put("area_prefix", "3");
				break;
			case "I":	/* International */
				callType = "international";
				String fix_mobile_country[] = "Bangladesh,Malaysia,Cambodia,Singapore,Canada,South Korea,China,Usa,Hong Kong,Vietnam,India".split(",");
				String fixMobileCountry = "";
				for (String country : fix_mobile_country) {
					if(ccrc.getDescription().contains(country)){
						fixMobileCountry = country;
					}
				}
				cir.getParams().put("area_name", "".equals(fixMobileCountry) ? ccrc.getDescription() : fixMobileCountry);
				break;
			}
			
			List<CallInternationalRate> cirs = callInternationalRateMapper.selectCallInternationalRates(cir);
			boolean isOnRate = cirs!=null && cirs.size()>0 ? true : false;

			Double costPerMinute = 1d;
//			if(is0900){} if(isFax){}
//			if(isMobile){ costPerMinute = 0.19d ; }
//			if(isNational){} if(isBusinessLocal){}
			if(isOnRate){ costPerMinute = cirs.get(0).getRate_cost(); }
			
			// duration = length / 60
			Double duration = Double.parseDouble(TMUtils.fillDecimalTime(String.valueOf(TMUtils.bigDivide((double)ccrc.getLength(), 60d))));
			
			CustomerCallRecord ccr = new CustomerCallRecord();
			ccr.setClear_service_id(ccrc.getOriginal_number());
			ccr.setCharge_date_time(ccrc.getDate());
			ccr.setPhone_called(ccrc.getDestination_number());
			ccr.setBilling_description(ccrc.getDescription());
			ccr.setCallType(TMUtils.strCapital(callType));
			ccr.setAmount_incl(ccrc.getCharged_fee());

			if(//(is0900 || isFax || isNational || isBusinessLocal) || 
					isOnRate){

				// If have reminder, then cut reminder and plus 1 minute, for example: 5.19 change to 6
				if(TMUtils.isReminder(String.valueOf(duration))){
					String durationStr = String.valueOf(duration);
					duration =  Double.parseDouble(durationStr.substring(0, durationStr.indexOf(".")))+1d;
				}
				
				// Process Present Calling Minutes
				totalCreditBack = processPresentCallingMinutes(pcms
						,cirs
						,duration
						,totalCreditBack
						,costPerMinute
						,callType);
				
				ccr.setAmount_incl(TMUtils.bigMultiply(duration, costPerMinute));
				totalAmountIncl = TMUtils.bigAdd(totalAmountIncl, TMUtils.bigMultiply(duration, costPerMinute));
				
			} else {
				totalAmountIncl = TMUtils.bigAdd(totalAmountIncl, ccr.getAmount_incl());
			}
			
			// END Rate Operation
			
			// FORMAT DURATION(second) TO TIME STYLE
			ccr.setFormated_duration(TMUtils.timeFormat.format(Double.parseDouble(String.valueOf(duration))).replace(".", ":"));
			ccrs.add(ccr);
			
		}
		
		if(!isRegenerate){
			if(ccrcs!=null && ccrcs.size()>0){
				CustomerCallingRecordCallplus ccrcUpdate = new CustomerCallingRecordCallplus();
				if(ci.getId()==null){
					ciMapper.insertCustomerInvoice(ci);
				}
				ccrcUpdate.setInvoice_id(ci.getId());
				ccrcUpdate.getParams().put("where", "last_calling_records");
				ccrcUpdate.getParams().put("original_number", TMUtils.formatPhoneNumber(pstn_number));
				ccrcUpdate.getParams().put("invoice_id", null);
				customerCallingRecordCallplusMapper.updateCustomerCallingRecordCallplus(ccrcUpdate);
				ccrcUpdate = null;
			}
		}
		
		// END Callplus Style Calling Record
		
		
		CustomerInvoiceDetail cid = new CustomerInvoiceDetail();
		cid.setInvoice_detail_name("Calling Charge : ( " + pstn_number + " )");
		cid.setInvoice_detail_price(TMUtils.bigSub(totalAmountIncl, totalCreditBack));
		cid.setInvoice_detail_desc("Total Charge: "+ totalAmountIncl + "\nCredit Back: "+ TMUtils.fillDecimalPeriod(totalCreditBack));
		cid.setInvoice_detail_unit(1);
		cid.setInvoice_detail_type("calling-charge");
		
		cids.add(cid);
		
		invoicePDF.getCrrsMap().put(pstn_number, ccrs);
		
		totalAmountIncl = TMUtils.bigSub(totalAmountIncl, totalCreditBack);
		
		ccrTemp = null;
		ccrsTemp = null;
		ccrcQuery = null;
		// ADD TOTAL CALL FEE INTO INVOICE
		return TMUtils.bigAdd(totalPayableAmouont, totalAmountIncl);
	}
	// END Chorus, Callplus OPERATION
	
	
	public static Double processPresentCallingMinutes(List<CustomerOrderDetail> pcms
			,List<CallInternationalRate> cirs
			,Double duration
			,Double totalCreditBack
			,Double costPerMinute
			,String callType){

		// BEGIN PresentCallingMinutes
		if(pcms!=null && pcms.size()>0){
			int index = 0;
			for (CustomerOrderDetail pcm : pcms) {
				String cirAreaName = cirs.get(0).getArea_name().toUpperCase();
				String pcmDetailDesc = pcm.getDetail_desc().toUpperCase();
				
				// Specific callType
				if(pcmDetailDesc.equals(callType.toUpperCase())){
					
					totalCreditBack = getCallingTotalCreditBack(pcms, pcm, duration, totalCreditBack, costPerMinute, index);
					index++;
					
				// Specific countries
				} else {
					
					String fix_mobile_country[] = "BANGLADESH,MALAYSIA,CAMBODIA,SINGAPORE,CANADA,SOUTH KOREA,CHINA,USA,HONG KONG,VIETNAM,INDIA".split(",");
					
					for (String country : fix_mobile_country) {
						if(cirAreaName.contains(country)){
							
							totalCreditBack = getCallingTotalCreditBack(pcms, pcm, duration, totalCreditBack, costPerMinute, index);
							index++;
							
						} else if(pcmDetailDesc.contains(cirs.get(0).getArea_prefix()) 
								&& pcmDetailDesc.contains(cirAreaName.split(" ")[0])
								&& (!cirAreaName.contains("MOBILE") && !cirAreaName.contains("MOB"))){
							totalCreditBack = getCallingTotalCreditBack(pcms, pcm, duration, totalCreditBack, costPerMinute, index);
							index++;
						}
					}
//					fix_mobile_country.contains(cirAreaName.split(" MOBILE")[0])
//					 ||fix_mobile_country.contains(cirAreaName.split(" MOBILE")
				}
				
			}
		}
		// END PresentCallingMinutes
		return totalCreditBack;
	}
	

	// BEGIN VOS VoIP OPERATION
	public static Double voipCallOperation(CustomerInvoice ci
			, Boolean isRegenerate
			, List<CustomerOrderDetail> pcmsVoIP
			, String voip_number
			, List<CustomerInvoiceDetail> cids
			, InvoicePDFCreator invoicePDF
			, Double totalPayableAmouont
			, NZAreaCodeListMapper nzAreaCodeListMapper
			, VOSVoIPRateMapper vosVoIPRateMapper
			, VOSVoIPCallRecordMapper vosVoIPCallRecordMapper
			, CustomerInvoiceMapper ciMapper
			, String customerType){

		List<NZAreaCodeList> nzAreaCodeList = nzAreaCodeListMapper.selectNZAreaCodeList(new NZAreaCodeList());
		
		String nineStartedLocalAreaCode = nzAreaCodeList.get(0).getNz_auckland_local_area_code();
		String[] nineStartedLocalAreaCodes = nineStartedLocalAreaCode.split(",");

		// Total calling record
		List<CustomerCallRecord> ccrs = new ArrayList<CustomerCallRecord>();
		// Total chargeable amount
		Double totalAmountIncl = 0d;
		// Total credit back
		Double totalCreditBack = 0d;
		
		String callType = "";
		
		// BEGIN VOS VoIP Calling Record
		VOSVoIPCallRecord vosVoIPCallRecordQuery = new VOSVoIPCallRecord();
		vosVoIPCallRecordQuery.getParams().put("where", "query_call_records");
		vosVoIPCallRecordQuery.getParams().put("orderby", "ORDER BY call_start ASC");
		if(isRegenerate){
			vosVoIPCallRecordQuery.getParams().put("invoice_id", ci.getId());
		} else {
			vosVoIPCallRecordQuery.getParams().put("invoice_id", null);
		}
		// PRODUCTION MODE CHANGE TO voip_number
		vosVoIPCallRecordQuery.getParams().put("ori_number", //96272424
				TMUtils.retrieveNonAreaCodeVoIPNumber(voip_number)
		);

		List<VOSVoIPCallRecord> vosVoIPCallRecordsQuery =  vosVoIPCallRecordMapper.selectVOSVoIPCallRecord(vosVoIPCallRecordQuery);
		
		
		// ITERATIVELY ADD ALL CALL CHARGES
		for (VOSVoIPCallRecord vosVoIPCallRecord : vosVoIPCallRecordsQuery) {
			
			String area_prefix = String.valueOf(vosVoIPCallRecord.getArea_prefix());

			
			boolean isLocal = false;
			
			// If not related to NATIONAL then it won't be Local neither
			if("NATIONAL".equals(vosVoIPCallRecord.getCall_type().toUpperCase())){
				
				for (String startedLocalAreaCode : nineStartedLocalAreaCodes) {
					
					String destNumberFinal = TMUtils.retrieveNonAreaCodeVoIPNumber(vosVoIPCallRecord.getDest_number());
					
					destNumberFinal = destNumberFinal.substring(1);
					
					isLocal = destNumberFinal.startsWith(startedLocalAreaCode);
					
					if(isLocal){
						break;
					}
					
				}
			}

			// Residential local call free
			if("personal".equals(customerType.toLowerCase()) && isLocal){
				continue;
			}
			
			VOSVoIPRate vosVoIPRateQuery = new VOSVoIPRate();
			vosVoIPRateQuery.getParams().put("area_prefix", area_prefix);
			
			List<VOSVoIPRate> vosVoIPRates = vosVoIPRateMapper.selectVOSVoIPRate(vosVoIPRateQuery);
			
			// If Area Code Doesn't Matched, Maybe The Code Isn't Existed On The Rates Table, So Left 3 Digit And Search Again
			if(vosVoIPRates==null || vosVoIPRates.size()<=0){

				vosVoIPRateQuery.getParams().put("area_prefix", area_prefix.substring(0, 3));
				vosVoIPRates = vosVoIPRateMapper.selectVOSVoIPRate(vosVoIPRateQuery);
				
			}
			// If Area Code Doesn't Matched, Maybe The Code Isn't Existed On The Rates Table, So Left 2 Digit And Search Again
			if(vosVoIPRates==null || vosVoIPRates.size()<=0){

				vosVoIPRateQuery.getParams().put("area_prefix", area_prefix.substring(0, 2));
				vosVoIPRates = vosVoIPRateMapper.selectVOSVoIPRate(vosVoIPRateQuery);
				
			}
			
			boolean isOnRate = vosVoIPRates!=null && vosVoIPRates.size()>0 ? true : false;

			Double costPerMinute = 1d;
			if(isOnRate){ costPerMinute = vosVoIPRates.get(0).getBilled_per_min(); }
			
			// DURATION/SECONDS
			Double duration = Double.parseDouble(TMUtils.fillDecimalTime(String.valueOf(TMUtils.bigDivide((double)TMUtils.retrieveVoIPChargePerThreeMinutes(vosVoIPCallRecord.getCall_length()), 60d))));

			CustomerCallRecord ccr = new CustomerCallRecord();
			ccr.setClear_service_id(vosVoIPCallRecord.getOri_number());
			ccr.setCharge_date_time(vosVoIPCallRecord.getCall_start());
			ccr.setPhone_called(vosVoIPCallRecord.getDest_number());
			
			callType = isLocal ? "Local" : vosVoIPCallRecord.getCall_type();
			ccr.setBilling_description(vosVoIPRates.get(0).getArea_name());
			ccr.setCallType(TMUtils.strCapital(callType));
			ccr.setAmount_incl(vosVoIPCallRecord.getCall_cost());
			ccr.setCallType(TMUtils.strCapital(callType));
			
			if(//(is0900 || isFax || isNational || isBusinessLocal) || 
					isOnRate && duration>0){

				// If have reminder, then cut reminder and plus 1 minute, for example: 5.19 change to 6
				if(TMUtils.isReminder(String.valueOf(duration))){
					String durationStr = String.valueOf(duration);
					duration =  Double.parseDouble(durationStr.substring(0, durationStr.indexOf(".")))+1d;
				}
				

				// Process Present Calling Minutes
				totalCreditBack = processPresentCallingMinutesVoIP(pcmsVoIP
						,vosVoIPRates
						,duration
						,totalCreditBack
						,costPerMinute
						,callType
						,isLocal);
				
				ccr.setAmount_incl(TMUtils.bigMultiply(duration, costPerMinute));
				totalAmountIncl = TMUtils.bigAdd(totalAmountIncl, TMUtils.bigMultiply(duration, costPerMinute));
				
			} else {
				totalAmountIncl = TMUtils.bigAdd(totalAmountIncl, ccr.getAmount_incl());
			}
			
			// END Rate Operation
			
			// FORMAT DURATION(second) TO TIME STYLE
			ccr.setFormated_duration(duration<=0 ? "" : TMUtils.timeFormat.format(Double.parseDouble(String.valueOf(duration))).replace(".", ":"));
			ccrs.add(ccr);
		}
		
		if(!isRegenerate){
			if(ccrs!=null && ccrs.size()>0){
				VOSVoIPCallRecord vosVoIPCallRecordUpdate = new VOSVoIPCallRecord();
				if(ci.getId()==null){
					ciMapper.insertCustomerInvoice(ci);
				}
				vosVoIPCallRecordUpdate.setInvoice_id(ci.getId());
				vosVoIPCallRecordUpdate.getParams().put("where", "last_call_records");
				vosVoIPCallRecordUpdate.getParams().put("ori_number", TMUtils.retrieveAreaCodeVoIPNumber(voip_number));
				vosVoIPCallRecordUpdate.getParams().put("invoice_id", null);
				vosVoIPCallRecordMapper.updateVOSVoIPCallRecord(vosVoIPCallRecordUpdate);
				vosVoIPCallRecordUpdate = null;
			}
		}
		
		CustomerInvoiceDetail cid = new CustomerInvoiceDetail();
		cid.setInvoice_detail_name("Calling Charge : ( " + voip_number + " )");
		cid.setInvoice_detail_price(TMUtils.bigSub(totalAmountIncl, totalCreditBack));
		cid.setInvoice_detail_desc("Total Charge: "+ totalAmountIncl + "\nCredit Back: "+ TMUtils.fillDecimalPeriod(totalCreditBack));
		cid.setInvoice_detail_unit(1);
		cid.setInvoice_detail_type("calling-charge");
		
		cids.add(cid);
		
		invoicePDF.getCrrsMap().put(voip_number, ccrs);
		
		totalAmountIncl = TMUtils.bigSub(totalAmountIncl, totalCreditBack);
		
		
		// ADD TOTAL CALL FEE INTO INVOICE
		return TMUtils.bigAdd(totalPayableAmouont, totalAmountIncl);
	}
	// END VOS VoIP OPERATION
	
	public static Double processPresentCallingMinutesVoIP(List<CustomerOrderDetail> pcms
			,List<VOSVoIPRate> vosVoIPRates
			,Double duration
			,Double totalCreditBack
			,Double costPerMinute
			,String callType
			,boolean isLocal){

		// BEGIN PresentCallingMinutes
		if(pcms!=null && pcms.size()>0){
			
			int index = 0;
			
			for (CustomerOrderDetail pcm : pcms) {
				
				String cirAreaName = vosVoIPRates.get(0).getArea_name().toUpperCase();
				String cirRateType = vosVoIPRates.get(0).getRate_type().toUpperCase();
				String pcmDetailDesc = pcm.getDetail_desc().toUpperCase();
				
				
				// If is Local
				if(
				   isLocal
				&& pcmDetailDesc.startsWith("LOCAL")){
					
					if(pcm.getDetail_calling_minute()>0){
						
						totalCreditBack = getCallingTotalCreditBack(pcms, pcm, duration, totalCreditBack, costPerMinute, index);
					
					}
					
				// If is National
				} else if(
				   pcmDetailDesc.startsWith("NATIONAL")
				&& "NATIONAL".equals(cirRateType)
				&& "NZ NATIONAL".equals(cirAreaName)){
					
					if(pcm.getDetail_calling_minute()>0){
						
						totalCreditBack = getCallingTotalCreditBack(pcms, pcm, duration, totalCreditBack, costPerMinute, index);
					
					}
					
				// If is NZ Mobile
				} else if(
				   pcmDetailDesc.startsWith("MOBILE")
				&& "NATIONAL".equals(cirRateType)
				&& "NZ MOBILE".equals(cirAreaName)){
					
					if(pcm.getDetail_calling_minute()>0){
						
						totalCreditBack = getCallingTotalCreditBack(pcms, pcm, duration, totalCreditBack, costPerMinute, index);
					
					}
				
				// If is Both Mobile+Fixed Line Countries
				} else if(
					pcmDetailDesc.contains("-")
			    || ("BANGLADESH".equals(pcmDetailDesc.split("-")[0]) && cirAreaName.contains("BANGLADESH"))
			    || ("MALAYSIA".equals(pcmDetailDesc.split("-")[0]) && cirAreaName.contains("MALAYSIA"))
			    || ("CAMBODIA".equals(pcmDetailDesc.split("-")[0]) && cirAreaName.contains("CAMBODIA"))
			    || ("SINGAPORE".equals(pcmDetailDesc.split("-")[0]) && cirAreaName.contains("SINGAPORE"))
			    || ("CANADA".equals(pcmDetailDesc.split("-")[0]) && cirAreaName.contains("CANADA"))
			    || ("KOREA".equals(pcmDetailDesc.split("-")[0]) && cirAreaName.contains("KOREA") && cirAreaName.contains("SOUTH"))
			    || ("CHINA".equals(pcmDetailDesc.split("-")[0]) && cirAreaName.contains("CHINA"))
			    || ("USA".equals(pcmDetailDesc.split("-")[0]) && cirAreaName.contains("USA") && !cirAreaName.contains("ALASKA"))
			    || ("HONG KONG".equals(pcmDetailDesc.split("-")[0]) && cirAreaName.contains("HONG KONG"))
			    || ("VIETNAM".equals(pcmDetailDesc.split("-")[0]) && cirAreaName.contains("VIETNAM"))
			    || ("INDIA".equals(pcmDetailDesc.split("-")[0]) && cirAreaName.contains("INDIA"))){
					
					if(pcm.getDetail_calling_minute()>0){
						
						totalCreditBack = getCallingTotalCreditBack(pcms, pcm, duration, totalCreditBack, costPerMinute, index);
					
					}
				
				// If is Fixed Line Countries
				} else if(
				   pcmDetailDesc.contains("-")
			    || ("ARGENTINA".equals(pcmDetailDesc.split("-")[0]) && cirAreaName.contains("ARGENTINA") && isNotMobileAndSpecial(cirAreaName))
			    || ("GERMANY".equals(pcmDetailDesc.split("-")[0]) && cirAreaName.contains("GERMANY") && isNotMobileAndSpecial(cirAreaName))
			    || ("LAOS".equals(pcmDetailDesc.split("-")[0]) && cirAreaName.contains("LAOS") && isNotMobileAndSpecial(cirAreaName))
			    || ("SOUTH AFRICA".equals(pcmDetailDesc.split("-")[0]) && cirAreaName.contains("SOUTH AFRICA") && isNotMobileAndSpecial(cirAreaName))
			    || ("AUSTRALIA".equals(pcmDetailDesc.split("-")[0]) && cirAreaName.contains("AUSTRALIA") && isNotMobileAndSpecial(cirAreaName))
			    || ("GREECE".equals(pcmDetailDesc.split("-")[0]) && cirAreaName.contains("GREECE") && isNotMobileAndSpecial(cirAreaName))
			    || ("NETHERLANDS".equals(pcmDetailDesc.split("-")[0]) && cirAreaName.contains("NETHERLANDS") && isNotMobileAndSpecial(cirAreaName))
			    || ("SPAIN".equals(pcmDetailDesc.split("-")[0]) && cirAreaName.contains("SPAIN") && isNotMobileAndSpecial(cirAreaName))
			    || ("BELGIUM".equals(pcmDetailDesc.split("-")[0]) && cirAreaName.contains("BELGIUM") && isNotMobileAndSpecial(cirAreaName))
			    || ("INDONESIA".equals(pcmDetailDesc.split("-")[0]) && cirAreaName.contains("INDONESIA") && isNotMobileAndSpecial(cirAreaName))
			    || ("NORWAY".equals(pcmDetailDesc.split("-")[0]) && cirAreaName.contains("NORWAY") && isNotMobileAndSpecial(cirAreaName))
			    || ("SWEDEN".equals(pcmDetailDesc.split("-")[0]) && cirAreaName.contains("SWEDEN") && isNotMobileAndSpecial(cirAreaName))
			    || ("BRAZIL".equals(pcmDetailDesc.split("-")[0]) && cirAreaName.contains("BRAZIL") && isNotMobileAndSpecial(cirAreaName))
			    || ("IRELAND".equals(pcmDetailDesc.split("-")[0]) && cirAreaName.contains("IRELAND") && isNotMobileAndSpecial(cirAreaName))
			    || ("PAKISTAN".equals(pcmDetailDesc.split("-")[0]) && cirAreaName.contains("PAKISTAN") && isNotMobileAndSpecial(cirAreaName))
			    || ("SWITZERLAND".equals(pcmDetailDesc.split("-")[0]) && cirAreaName.contains("SWITZERLAND") && isNotMobileAndSpecial(cirAreaName))
			    || ("CYPRUS".equals(pcmDetailDesc.split("-")[0]) && cirAreaName.contains("CYPRUS") && isNotMobileAndSpecial(cirAreaName))
			    || ("ISRAEL".equals(pcmDetailDesc.split("-")[0]) && cirAreaName.contains("ISRAEL") && isNotMobileAndSpecial(cirAreaName))
			    || ("POLAND".equals(pcmDetailDesc.split("-")[0]) && cirAreaName.contains("POLAND") && isNotMobileAndSpecial(cirAreaName))
			    || ("TAIWAN".equals(pcmDetailDesc.split("-")[0]) && cirAreaName.contains("TAIWAN") && isNotMobileAndSpecial(cirAreaName))
			    || ("CZECH".equals(pcmDetailDesc.split("-")[0]) && cirAreaName.contains("CZECH") && isNotMobileAndSpecial(cirAreaName))
			    || ("ITALY".equals(pcmDetailDesc.split("-")[0]) && cirAreaName.contains("ITALY") && isNotMobileAndSpecial(cirAreaName))
			    || ("PORTUGAL".equals(pcmDetailDesc.split("-")[0]) && cirAreaName.contains("PORTUGAL") && isNotMobileAndSpecial(cirAreaName))
			    || ("THAILAND".equals(pcmDetailDesc.split("-")[0]) && cirAreaName.contains("THAILAND") && isNotMobileAndSpecial(cirAreaName))
			    || ("DENMARK".equals(pcmDetailDesc.split("-")[0]) && cirAreaName.contains("DENMARK") && isNotMobileAndSpecial(cirAreaName))
			    || ("JAPAN".equals(pcmDetailDesc.split("-")[0]) && cirAreaName.contains("JAPAN") && isNotMobileAndSpecial(cirAreaName))
			    || ("RUSSIA".equals(pcmDetailDesc.split("-")[0]) && cirAreaName.contains("RUSSIA") && isNotMobileAndSpecial(cirAreaName))
			    || ("UNITED KINGDOM".equals(pcmDetailDesc.split("-")[0]) && cirAreaName.contains("UNITED KINGDOM") && isNotMobileAndSpecial(cirAreaName))
			    || ("FRANCE".equals(pcmDetailDesc.split("-")[0]) && cirAreaName.contains("FRANCE") && isNotMobileAndSpecial(cirAreaName))){
					
					if(pcm.getDetail_calling_minute()>0){
						
						totalCreditBack = getCallingTotalCreditBack(pcms, pcm, duration, totalCreditBack, costPerMinute, index);
					
					}
					
				// If is International
				} else if(
				   pcmDetailDesc.startsWith("INTERNATIONAL")
				&& "INTERNATIONAL".equals(cirRateType)){
					
					if(pcm.getDetail_calling_minute()>0){
						
						totalCreditBack = getCallingTotalCreditBack(pcms, pcm, duration, totalCreditBack, costPerMinute, index);
					
					}
					
				}
				
				// Increment For Left Present Calling Minutes
				index++;
				
			}
			
		}
		// END PresentCallingMinutes
		return totalCreditBack;
	}

	/**
	 * 
	 * @param pcms
	 * @param pcm
	 * @param duration
	 * @param totalCreditBack
	 * @param costPerMinute
	 * @param index
	 * @return
	 */
	public static Double getCallingTotalCreditBack(List<CustomerOrderDetail> pcms
			,CustomerOrderDetail pcm
			,Double duration
			,Double totalCreditBack
			,Double costPerMinute
			,Integer index){
		
		// If got sufficient given minutes
		if(pcm.getDetail_calling_minute()!=null && pcm.getDetail_calling_minute() > 0){
			// If Present Minutes greater than duration
			if(pcm.getDetail_calling_minute() >= duration){
				// Decrease Present Minutes
				pcm.setDetail_calling_minute(TMUtils.bigOperationTwoReminders(pcm.getDetail_calling_minute().doubleValue(), duration, "sub").intValue());
				// Replace old one to new one
				Collections.replaceAll(pcms, pcms.get(index), pcm);
				totalCreditBack = TMUtils.bigAdd(totalCreditBack, TMUtils.bigMultiply(duration, costPerMinute));
			} else {
				totalCreditBack = TMUtils.bigAdd(totalCreditBack, TMUtils.bigMultiply(pcm.getDetail_calling_minute().doubleValue(), costPerMinute));
				// Decrease Present Minutes
				pcm.setDetail_calling_minute(0);
				// Replace old one to new one
				Collections.replaceAll(pcms, pcms.get(index), pcm);
			}
		}
		return totalCreditBack;
	}
	
//	/**
//	 * 
//	 * @param pcms
//	 * @param pcm
//	 * @param duration
//	 * @param totalCreditBack
//	 * @param costPerMinute
//	 * @param index
//	 * @return
//	 */
//	public static Double getCallingTotalCreditBackVoIP(List<CustomerOrderDetail> pcms
//			,CustomerOrderDetail pcm
//			,Double duration
//			,Double totalCreditBack
//			,Double costPerMinute
//			,Integer index){
//		
//		
//		System.out.println("pcm.getDetail_name():"+pcm.getDetail_name());
//		System.out.println("pcm.getDetail_calling_minute(): "+pcm.getDetail_calling_minute());
//		
//		for (CustomerOrderDetail pcmRecent : pcms) {
//			System.out.println("pcmRecent.getDetail_name(): "+pcmRecent.getDetail_name());
//			System.out.println("pcmRecent.getDetail_calling_minute(): "+pcmRecent.getDetail_calling_minute());
//		}
//		
//		System.out.println("index: "+index);
//		System.out.println("duration: "+duration);
//		System.out.println("costPerMinute: "+costPerMinute);
//		
//		// If got sufficient given minutes
//		if(pcm.getDetail_calling_minute()!=null && pcm.getDetail_calling_minute() > 0){
//			// If Present Minutes greater than duration
//			if(pcm.getDetail_calling_minute() >= duration){
//				// Decrease Present Minutes
//				pcm.setDetail_calling_minute(TMUtils.bigOperationTwoReminders(pcm.getDetail_calling_minute().doubleValue(), duration, "sub").intValue());
//				// Replace old one to new one
//				Collections.replaceAll(pcms, pcms.get(index), pcm);
//				totalCreditBack = TMUtils.bigAdd(totalCreditBack, TMUtils.bigMultiply(duration, costPerMinute));
//			} else {
//				totalCreditBack = TMUtils.bigAdd(totalCreditBack, TMUtils.bigMultiply(pcm.getDetail_calling_minute().doubleValue(), costPerMinute));
//				// Decrease Present Minutes
//				pcm.setDetail_calling_minute(0);
//				// Replace old one to new one
//				Collections.replaceAll(pcms, pcms.get(index), pcm);
//			}
//		}
//		
//		System.out.println("totalCreditBack: "+totalCreditBack);
//		
//		
//		return totalCreditBack;
//	}
	
	public static Boolean isNotMobileAndSpecial(String cirAreaName){
		return !cirAreaName.contains("MOBILE") && !cirAreaName.contains("SPECIAL");
	}
}
