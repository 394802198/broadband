package com.tm.broadband.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.tm.broadband.model.CustomerCallRecord;

public class CallingRecordUltility {
	
	private static Integer statementDateIndex 			= 0;
	private static Integer recordTypeIndex 				= 5;
	private static Integer clearServiceIdIndex 			= 8;
	private static Integer chargeDateIndex				= 12;
	private static Integer chargeTimeIndex				= 13;
	private static Integer durationIndex				= 15;
	private static Integer ootIdIndex					= 17;
	private static Integer billingDescriptionIndex		= 19;
	private static Integer amountExclIndex				= 24;
	private static Integer amountInclIndex				= 25;
	private static Integer phoneCallIndex				= 29;
	
	// Get calling records
	public static List<CustomerCallRecord> ccrs(String csvPath){
		
		List<CustomerCallRecord> ccrs = new ArrayList<CustomerCallRecord>();

		try {
			File csv = new File(csvPath); // CSV File
			BufferedReader br = new BufferedReader(new FileReader(csv));
			String line = "";
			boolean header = true;
			while ((line = br.readLine()) != null) {
				if(header){ header=false; continue; }
				ccrs.add(setCustomerCallRecord(line));
			}
			br.close();

		} catch (Exception e) { e.printStackTrace(); }
		return ccrs;
	}
	
	// Get statemnent date
	public static Date statementDate(String csvPath){
		Date statementDate = null;

		try {
			File csv = new File(csvPath); // CSV File
			BufferedReader br = new BufferedReader(new FileReader(csv));
			br.readLine();
			String arr[] = br.readLine().split(",");
			if(arr.length >= statementDateIndex){
				String statementDateStr = arr[statementDateIndex];
				if(!"".equals(statementDateStr.trim())){
					statementDate = TMUtils.parseDate1YYYYMMDD(statementDateStr);
				}
			}
			br.close();

		} catch (Exception e) { e.printStackTrace(); }
		
		return statementDate;
	}
	
	// Iterating calling record
	private static CustomerCallRecord setCustomerCallRecord(String line){
		CustomerCallRecord ccr = new CustomerCallRecord();
		String arr[] = line.split(",");
		
		// Date
		if(arr.length > statementDateIndex){
			String statementDateStr = arr[statementDateIndex];
			if(!"".equals(statementDateStr.trim())){
				ccr.setStatement_date(TMUtils.parseDate1YYYYMMDD(statementDateStr));
			}
			
			// String
			if(arr.length > recordTypeIndex){
				ccr.setRecord_type(arr[recordTypeIndex]);
				
				// String
				if(arr.length > clearServiceIdIndex){
		        	String number = arr[clearServiceIdIndex];
		        	if(number.length() > 0 && "T1".equals(arr[recordTypeIndex])){
		        		number = number.substring(0, number.length()-1);
		        	}
					ccr.setClear_service_id(number);
					
					// Datetime
					if(arr.length > chargeDateIndex && arr.length >= chargeTimeIndex){
						String temp = arr[chargeDateIndex]+" "+arr[chargeTimeIndex];
						if(!"".equals(temp.trim())){
							ccr.setCharge_date_time(TMUtils.parseDate3YYYYMMDD(temp));
						}
						
						// Integer
						if(arr.length > durationIndex){
							if(arr[durationIndex] != null && !"".equals(arr[durationIndex].trim())){
								ccr.setDuration(Integer.parseInt(arr[durationIndex]));
							}
							
							// String
							if(arr.length > ootIdIndex){
								ccr.setOot_id(arr[ootIdIndex]);
								
								// String
								if(arr.length > billingDescriptionIndex){
									ccr.setBilling_description(arr[billingDescriptionIndex]);
									
									// Double
									if(arr.length > amountExclIndex){
										if(arr[amountExclIndex] != null && !"".equals(arr[amountExclIndex].trim())){
											ccr.setAmount_excl(Double.parseDouble(arr[amountExclIndex]));
										}
										
										// Double
										if(arr.length > amountInclIndex){
											if(arr[amountInclIndex] != null && !"".equals(arr[amountInclIndex].trim())){
												ccr.setAmount_incl(Double.parseDouble(arr[amountInclIndex]));
											}
											
											// String
											if(arr.length > phoneCallIndex){
												ccr.setPhone_called(arr[phoneCallIndex]);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return ccr;
	}
}
