package com.tm.broadband.util;

import java.text.ParseException;


public class StevenTest {
	
	public static void main(String[] args) throws ParseException {
//		//CyberparkTopup30_5000_20140605.csv
//		String filePath = TMUtils.createPath("CyberparkTopup30_5000_20140605.csv");
//		List<Voucher> vs = VoucherRecordUtility.vs(filePath);
//		for (Voucher v : vs) {
//			System.out.print(v.getSerial_number()+"\t");
//			System.out.print(v.getCard_number()+"\t");
//			System.out.print(v.getFace_value()+"\t");
//			System.out.println(v.getComment());
//		}

//		Calendar beginCal = Calendar.getInstance();
//		beginCal.set(Calendar.MONTH, beginCal.get(Calendar.MONTH)-3);
//		beginCal.set(Calendar.DAY_OF_MONTH, 1);
//		beginCal.set(Calendar.HOUR_OF_DAY, 0);
//		beginCal.set(Calendar.MINUTE, 0);
//		beginCal.set(Calendar.SECOND, 0);
//		System.out.println(beginCal.getTime());
//
//		Calendar endCal = Calendar.getInstance();
//		endCal.set(Calendar.DAY_OF_MONTH, 1);
//		endCal.add(Calendar.DAY_OF_MONTH, -1);
//		endCal.set(Calendar.HOUR_OF_DAY, 0);
//		endCal.set(Calendar.MINUTE, 0);
//		endCal.set(Calendar.SECOND, 0);
//		System.out.println(endCal.getTime());

//		String filePath = TMUtils.createPath("CallPlus Calling Record June2014 90445601.csv");
//		List<CustomerCallingRecordCallplus> ccrs = CallingRecordUtility_CallPlus.ccrcs(filePath);
//		for (CustomerCallingRecordCallplus ccr : ccrs) {
//			System.out.println(ccr.getDate_str());
//		}
		
//		Calendar cal = Calendar.getInstance();
//		cal.setTime(new Date());
//		cal.add(Calendar.DAY_OF_MONTH, -7);
//		System.out.println(cal.getTime());
		// Second Encode
//		String secondEncode = TMUtils.generateRandomString(3)+DigestUtils.md5Hex("cyberpark123z")+TMUtils.generateRandomString(3);
//		System.out.println(secondEncode.length());
//		System.out.println(secondEncode);
//		// Second Decode
//		String secondDecode = secondEncode.substring(3, secondEncode.length()-3);
//		System.out.println(secondDecode);
		
		
		SimpleMapperCreator smc = new SimpleMapperCreator();
		smc.setAuthor("StevenChen");
		smc.setModel("TMSComboDetail");
		smc.setTable("tms_combo_detail");
		smc.initial();
		
//		Calendar cal = Calendar.getInstance(Locale.CHINA);
//		cal.setTime(new Date());
//		cal.add(Calendar.WEEK_OF_MONTH, 1);
//		cal.add(Calendar.DAY_OF_WEEK, -1);
//		System.out.println(TMUtils.dateFormatYYYYMMDD(cal.getTime()));
//		cal.add(Calendar.DAY_OF_WEEK, -2);
//		System.out.println(TMUtils.dateFormatYYYYMMDD(cal.getTime()));
		
	}
}
