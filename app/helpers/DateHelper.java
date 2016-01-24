package helpers;

import play.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateHelper {

    private static SimpleDateFormat dateFormat;
    private static SimpleDateFormat chartDateFormat;
    private static SimpleDateFormat chartTitleDateFormat;
    private static SimpleDateFormat dtoDateFormat;
    private static SimpleDateFormat calendarDateFormat;

    private DateHelper() {

    }

    public static SimpleDateFormat getDateFormat() {
        if (dateFormat==null) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        }

        return dateFormat;
    }

    public static SimpleDateFormat getChartDateFormat() {
        if (chartDateFormat==null) {
            chartDateFormat = new SimpleDateFormat("HH:mm");
        }

        return chartDateFormat;
    }

    public static SimpleDateFormat getChartTitleDateFormat() {
        if (chartTitleDateFormat==null) {
            chartTitleDateFormat = new SimpleDateFormat("dd/MM/yy HH:mm");
        }

        return chartTitleDateFormat;
    }

    public static SimpleDateFormat getyDtoDateFormat() {
        if (dtoDateFormat == null) {
            dtoDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        }

        return dtoDateFormat;
    }

    public static SimpleDateFormat getCalendarDateFormat() {
        if (calendarDateFormat == null) {
            calendarDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        }

        return calendarDateFormat;
    }

    public static String formatChartDate(Date date) {
        return getChartDateFormat().format(date);
    }

    public static String formatChartTitleDate(Date date) {
        return getChartTitleDateFormat().format(date);
    }

    public static String formatDtoDate(Date date) {
        return getyDtoDateFormat().format(date);
    }

    public static String formatCalendarDate(Date date) {
        return getCalendarDateFormat().format(date);
    }

    public static String formatServiceDate(Date date) {
        return getDateFormat().format(date);
    }

    public static Date getDateStartDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getDateEndDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getCurrentHour() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

	public static Date getTodayStartDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

    public static Date getTodayEndDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date parseDate(String formattedDate) {
        try {
            return getDateFormat().parse(formattedDate);
        } catch (Exception e) {
            Logger.error(e, "Could not parse date");
        }

        return null;
    }

    public static boolean isBirthDayThisMonth(Date birthday) {
        int currentMonth = -1;
        int birthdayMonth = -1;

        Calendar birthdayCalendar = Calendar.getInstance();
        currentMonth = birthdayCalendar.get(Calendar.MONTH);
        birthdayCalendar.setTime(birthday);
        birthdayMonth = birthdayCalendar.get(Calendar.MONTH);
        return currentMonth == birthdayMonth && currentMonth!=-1;
    }

    public static boolean isBirthDayToday(Date birthday) {
        int currentDay = -1;
        int birthdayDay = -1;
        int daysInCurrentYear = -1;
        int daysInBirthdayYear = -1;

        Calendar birthdayCalendar = Calendar.getInstance();
        currentDay = birthdayCalendar.get(Calendar.DAY_OF_YEAR);
        daysInCurrentYear = birthdayCalendar.getActualMaximum(Calendar.DAY_OF_YEAR);

        birthdayCalendar.setTime(birthday);
        birthdayDay = birthdayCalendar.get(Calendar.DAY_OF_YEAR);
        daysInBirthdayYear = birthdayCalendar.getActualMaximum(Calendar.DAY_OF_YEAR);

        if (daysInCurrentYear > daysInBirthdayYear) {
            if (currentDay > 60) {
                return currentDay - 1 == birthdayDay && currentDay != -1;
            }
        } else if (daysInBirthdayYear > daysInCurrentYear) {
            if (currentDay > 60) {
                return currentDay == birthdayDay - 1 && currentDay != -1;
            }
        }

        return currentDay == birthdayDay && currentDay!=-1;
    }

    public static int calculateAge(Date birthDate)
    {
        int years = 0;
        int months = 0;
        int days = 0;
        //create calendar object for birth day
        Calendar birthDay = Calendar.getInstance();
        birthDay.setTimeInMillis(birthDate.getTime());
        //create calendar object for current day
        long currentTime = System.currentTimeMillis();
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(currentTime);
        //Get difference between years
        years = now.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
        int currMonth = now.get(Calendar.MONTH) + 1;
        int birthMonth = birthDay.get(Calendar.MONTH) + 1;
        //Get difference between months
        months = currMonth - birthMonth;
        //if month difference is in negative then reduce years by one and calculate the number of months.
        if (months < 0)
        {
            years--;
            months = 12 - birthMonth + currMonth;
            if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
                months--;
        } else if (months == 0 && now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
        {
            years--;
            months = 11;
        }
        //Calculate the days
        if (now.get(Calendar.DATE) > birthDay.get(Calendar.DATE))
            days = now.get(Calendar.DATE) - birthDay.get(Calendar.DATE);
        else if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
        {
            int today = now.get(Calendar.DAY_OF_MONTH);
            now.add(Calendar.MONTH, -1);
            days = now.getActualMaximum(Calendar.DAY_OF_MONTH) - birthDay.get(Calendar.DAY_OF_MONTH) + today;
        } else
        {
            days = 0;
            if (months == 12)
            {
                years++;
                months = 0;
            }
        }
        //Create new Age object
        return years;
    }
	
}
