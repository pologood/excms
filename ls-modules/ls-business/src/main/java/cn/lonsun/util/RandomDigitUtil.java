package cn.lonsun.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class RandomDigitUtil {
	public static String getRandomDigit(){
	    StringBuffer randomDigit = new StringBuffer(); 
		Date date=new Date();  
	    SimpleDateFormat formatter=new SimpleDateFormat("yyyyMMddHHmmss");  
	    randomDigit.append(formatter.format(date));  
	    String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";     
	    Random random = new Random();     
	    for (int i = 0; i < 5; i++) {     
	        int number = random.nextInt(base.length());     
	        randomDigit.append(base.charAt(number));     
	    }  
		return randomDigit.toString();
	}
}

