package com.example.renatojava.javasemester.util;

public class DateFormatter {



    public static String getDateTimeFormatted(String start){
        String fullDateTime = start.substring(8,10) + "-" + start.substring(5,7) + "-" + start.substring(0,4) + " " + start.substring(11,13) + ":" + start.substring(14,16);
        return fullDateTime;
    }

    public static String getDateFormatted(String start){
        return start.substring(8,10) + "-" + start.substring(5,7) + "-" + start.substring(0,4);
    }
}