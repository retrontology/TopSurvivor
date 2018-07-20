package com.retrontology.topsurvivor;

public class TimeConverter
{
  public static double day = 24000.0D;
  public static double hour = 1000.0D;
  public static double minute = 16.0D;
  public static double second = minute / 60.0D;
  public static long milliDays = 86400000L;
  
  public static String getString(int ticks)
  {
    String time = "";
    time = time = time + (int)Math.floor(ticks / day) + "d ";
    time = time = time + (int)Math.floor(ticks % day / hour) + "h ";
    time = time = time + (int)Math.floor(ticks % day % hour / minute) + "m ";
    time = time = time + (int)Math.floor(ticks % day % hour % minute / second) + "s";
    return time;
  }
  
  public static int getDays(int ticks)
  {
    return (int)Math.floor(ticks / day);
  }
  
  public static int getHours(int ticks)
  {
    return (int)Math.floor(ticks % day / hour);
  }
  
  public static int getMinutes(int ticks)
  {
    return (int)Math.floor(ticks % day % hour / minute);
  }
  
  public static int getSeconds(int ticks)
  {
    return (int)Math.floor(ticks % day % hour % minute / second);
  }
  
  public static int milliToDays(long milli)
  {
    return (int)(milli / milliDays);
  }
  
  public static long daysToMilli(int days)
  {
    return days * milliDays;
  }
}
