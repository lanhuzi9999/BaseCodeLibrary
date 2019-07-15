package com.example.basecodelibrary.util;

import java.util.Date;

public class TimeUtil {
	/**将日期格式化为 yyyy-MM-dd hh:mm:ss*/
	public static String formatDate(long mill) {
	    return mill<1? String.valueOf(mill): String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS", mill);
	}

	/**将日期格式化为 yyyy-MM-dd hh:mm:ss*/
	public static String formatDate(Date d) {
	    return d==null? "":String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS", d);
	}
}
