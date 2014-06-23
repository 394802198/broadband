package com.tm.broadband.util;

import java.util.Calendar;
import java.util.Date;




public class Test {

	public static void main(String[] args) throws Exception {

		// String[] pwds = new String[] { "1", "5", "9", "8", "7", "6", "4",
		// "3", "2", "q",
		// "w", "e", "r", "t", "y", "u", "i", "o", "p", "a", "s", "d", "f", "g",
		// "h", "j",
		// "k", "l", "z", "x", "c", "v", "b", "n", "m", "0"};
		// Random random = new Random();
		// System.out.println(pwds[random.nextInt(36)]);
		// String finalResult =
		// BroadbandCapability.getCapabilityResultByAddress("奥克兰New North Road945");
		// System.out.println(finalResult.substring(finalResult.lastIndexOf(",")
		// + 1));
		//
		// System.out.println(ITextFont.arial_normal_6);

		// Customer c = new Customer();
		// c.setFirst_name("<script>asdasdasd");
		// c.setLast_name("<script>asdasdasd");
		// System.out.println(CheckScriptInjection.isScriptInjection(c));

		// Map<String, Object> resultMap =
		// Calculation4PlanTermInvoice.servedMonthDetails(new
		// SimpleDateFormat("yyyy-MM-dd").parse("2014-05-14"), 89d);
		// System.out.println(resultMap.get("totalPrice"));
		// System.out.println(resultMap.get("remainingDays"));
		// System.out.println(resultMap.get("dailyPrice"));
		//
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		// boolean isServedCurrentMonth =
		// sdf.format(sdf.parse("2014-05-19")).equals(sdf.format(new Date()));
		// System.out.println(isServedCurrentMonth);

		// Calendar cal = Calendar.getInstance(Locale.CHINA);
		// String yyyy_MM = new SimpleDateFormat("yyyy-MM-dd").format(new
		// Date()).substring(0, 7);
		// cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(yyyy_MM+"-20"));
		// System.out.println(cal.getTime());
		
		
//		List<CustomerCallRecord> ccrs = CallingRecordUltility.ccrs("C:/TMS_20140528_EB05.csv");
//		for (CustomerCallRecord ccr : ccrs) {
//			System.out.print(ccr.getStatement_date()+"\t");
//			System.out.print(ccr.getRecord_type()+"\t");
//			System.out.print(ccr.getClear_service_id()+"\t");
//			System.out.print(ccr.getCharge_date_time()+"\t");
//			System.out.print(ccr.getDuration()+"\t");
//			System.out.print(ccr.getOot_id()+"\t");
//			System.out.print(ccr.getBilling_description()+"\t");
//			System.out.print(ccr.getAmount_excl()+"\t");
//			System.out.print(ccr.getAmount_incl()+"\t");
//			System.out.print(ccr.getPhone_called());
//			System.out.println();
//		}
		
//		Calendar c = Calendar.getInstance(Locale.CHINA);
//		c.set(Calendar.MONDAY, c.get(Calendar.MONDAY)-1);
//		System.out.println(TMUtils.dateFormatYYYYMMDD(c.getTime()));
		
//		System.out.println(CallingRecordUltility.statementDate("C:/TMS_20140428_EB05.csv"));
//		
//		File file = new File("C:/writers.csv");
//		file.deleteOnExit();
		
//		String str = "09 - 0299%10*9";
//		System.out.println(TMUtils.formatPhoneNumber(str));
		
//		String str = "4501";
//		
//		str = TMUtils.fillDecimalColon(String.valueOf(Double.parseDouble(str) / 60));
//		System.out.println(str);

//		String sum = String.valueOf(Math.round((double)Double.valueOf("1.409")*100)/100.00);
//		DecimalFormat df = new DecimalFormat("00.00");
//		System.out.println(df.format(Double.parseDouble(sum)));
		
//		List<CallInternationalRate> cirs = CallInternationalRateUltility.cirs("E:\\项目\\ISP项目开发\\PDF模版\\账单电话拨打细目\\Calling Rate for CyberPark_05June2014.csv");
//		
//		for (CallInternationalRate cir : cirs) {
//			System.out.print(cir.getArea_prefix()+"\t");
//			System.out.print(cir.getRate_type()+"\t");
//			System.out.print(cir.getArea_name()+"\t");
//			System.out.println(cir.getRate_cost());
//		}
		
//		System.out.println("0098755123".replaceFirst("^0*", ""));
		
//		System.out.println("09-9786548".substring(0, "09-9786548".indexOf("-")));
		

//		// DURATION/SECONDS
//		BigDecimal bigDuration = new BigDecimal(120d);
//		
//		// 60 SECONDS/MINUTE
//		BigDecimal bigDuration60 = new BigDecimal(60d);
//		
//		// FINAL DURATION/TOTAL MINUTE
//		BigDecimal bigFinalDuration = new BigDecimal(bigDuration.divide(bigDuration60, 2, BigDecimal.ROUND_DOWN).doubleValue());
//		
//		boolean isReminder = bigFinalDuration.toString().indexOf(".") > 0;
//		
//		if(isReminder){
//			System.out.println(isReminder);
//			String bigFinalDurationStr = bigFinalDuration.toString();
//			bigFinalDuration = new BigDecimal(Integer.parseInt(bigFinalDurationStr.substring(0, bigFinalDurationStr.indexOf(".")))+1);
//			System.out.println(bigFinalDuration);
//		}
//		System.out.println(bigFinalDuration);
		
//		String sum = "2.69";
//		Integer sumInteger = Integer.parseInt(sum.substring(0, sum.indexOf(".")));
//		Integer sumReminder = Integer.parseInt(sum.substring(sum.indexOf(".")+1));
//		if(sumReminder > 59){
//			sumReminder = sumReminder - 60;
//			sumInteger ++;
//		}
//		System.out.println(sumInteger+"."+sumReminder);

//		String str = String.valueOf((double)61/60);
//		Integer reminderLength = str.substring(str.indexOf(".")+1).length();
//		String strTemp = str.substring(str.indexOf(".")+1, reminderLength>2 ?str.indexOf(".")+3 : str.indexOf(".")+2);
//		System.out.println(Integer.parseInt(strTemp)>0);
		
//		Calculation4PlanTermInvoice.servedMonthDetails(TMUtils.parseDateYYYYMMDD("2014-05-01"), 89d);
		

		//System.out.println(TMUtils.retrieveMonthAbbrWithDate(TMUtils.parseDateYYYYMMDD("2014-06-09")));
		
//		Calendar cal = Calendar.getInstance();
//		cal.setTime(new Date());
//		
//		System.out.println(cal.get(Calendar.MONTH));

//		System.out.println(TMUtils.retrieveMonthAbbrWithDate(TMUtils.parseDateYYYYMMDD("2014-06-09")));
		
		System.out.println("A".equals(String.valueOf("abc".toUpperCase().charAt(0))));
		
	}

}
