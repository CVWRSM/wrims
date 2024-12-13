/*
 * Water Resource Integrated Modeling System (WRIMS) Copyright (c) 2024.
 *
 * WRIMS 2 is copyrighted by the State of California Department of Water Resources.
 * It is licensed under the Eclipse Public License, Version 1.0.
 * See Eclipse Public License for more details.
 */

package gov.ca.dwr.hecdssvue.dts;

import java.util.Date;

class TimeOperation {

    private TimeOperation() {
        throw new AssertionError("Utility class");
    }

    public static String dssTime(int year, int month, int day) {
        return dayName(day) + monthName(month) + year + " 0000";
    }

    public static String dayName(int day) {
        if (day < 10) {
            return "0" + day;
        } else {
            return "" + day;
        }
    }

    public static String monthName(int month) {
        if (month == 1) {
            return "JAN";
        } else if (month == 2) {
            return "FEB";
        } else if (month == 3) {
            return "MAR";
        } else if (month == 4) {
            return "APR";
        } else if (month == 5) {
            return "MAY";
        } else if (month == 6) {
            return "JUN";
        } else if (month == 7) {
            return "JUL";
        } else if (month == 8) {
            return "AUG";
        } else if (month == 9) {
            return "SEP";
        } else if (month == 10) {
            return "OCT";
        } else if (month == 11) {
            return "NOV";
        } else {
            return "DEC";
        }
    }

    public static boolean isLeapYear(int year) {
        if (year % 4 == 0) {
			if (year % 100 != 0) {
				return true;
			} else {
				return year % 400 == 0;
			}
        } else {
            return false;
        }
    }

    public static int numberOfDays(int month, int year) {
        int days;
        if (month == 1 || month == 3 || month == 5 || month == 7
            || month == 8 || month == 10 || month == 12) {
            days = 31;
        } else if (month == 4 || month == 6 || month == 9 || month == 11) {
            days = 30;
        } else {
            if (TimeOperation.isLeapYear(year)) {
                days = 29;
            } else {
                days = 28;
            }
        }
        return days;
    }

    public static Date addOneMonth(Date date) {
        int month = date.getMonth() + 1;
        int year = date.getYear();
        if (month > 11) {
            month = month - 12;
            year = year + 1;
        }
        int day = TimeOperation.numberOfDays(month + 1, year + 1900);
        Date newDate = new Date(year, month, day);
        return newDate;
    }

    public static Date addOneDay(Date date) {
        long newTime = date.getTime() + 24 * 60 * 60 * 1000L;
        Date newDate = new Date(newTime);
        return newDate;
    }
}
