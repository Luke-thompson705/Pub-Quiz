package com.example.luke_.pubquiz.utils;


public class Utils {
    public static String Ordinals(int number) {
        int hundred = number % 100;
        int tens = number % 10;
        if (hundred - tens == 10) {
            return "th";
        }
        switch (tens) {
            case 1:
                return String.valueOf(number) + "st";
            case 2:
                return String.valueOf(number) + "nd";
            case 3:
                return String.valueOf(number) + "rd";
            default:
                return String.valueOf(number) + "th";

            }
    }
}
