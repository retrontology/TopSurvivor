package com.retrontology.topsurvivor;

public class TimeConverter {

	public static double day = 24000;
	public static double hour = 1000;
	public static double minute = 1000/60;
	public static double second = minute/60;
	
	public static String getString(int ticks){
		String time = "";
		time = time += (int)Math.floor(ticks/day) + "d ";
		time = time += (int)Math.floor(ticks%day/hour) + "h ";
		time = time += (int)Math.floor(ticks%day%hour/minute) + "m ";
		time = time += (int)Math.floor(ticks%day%hour%minute/second) + "s";
		return time;
	}
	
	public static int getDays(int ticks){
		return (int) Math.floor(ticks/day);
	}
	
	public static int getHours(int ticks){
		return (int) Math.floor(ticks%day/hour);
	}
	
	public static int getMinutes(int ticks){
		return (int) Math.floor(ticks%day%hour/minute);
	}
	
	public static int getSeconds(int ticks){
		return (int) Math.floor(ticks%day%hour%minute/second);
	}
	
}
