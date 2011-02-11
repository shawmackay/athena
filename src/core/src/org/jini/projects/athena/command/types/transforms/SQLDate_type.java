package org.jini.projects.athena.command.types.transforms;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.jini.projects.athena.command.types.Transformer;

/**
 * Translation type for mapping a Java date to
 * String in sql date format. Only uses the year, month and day portion of the date.
 * Tested on Oracle only
 */
public class SQLDate_type implements Transformer {
    boolean nulldate = false;
    java.util.Date inDate;

    public SQLDate_type(java.util.Date inDate) {
        this.inDate = inDate;
    }

    public SQLDate_type() {

    }

    /**
     * Sets the source date.
     * @throws java.lang.ClassCastException in the event of wrong data types being passed
     */
    public void set(Object obj) throws ClassCastException {
        System.out.println("Casting now (" + obj.getClass().getName() + ")");
        if (obj instanceof String) {
            String x = (String) obj;
            if (x.equals("")) {
                nulldate = true;
            } else {
                try {
                    SimpleDateFormat format =
                            new SimpleDateFormat("dd-MMM-yyyy");
                    inDate = format.parse(x);
                } catch (Exception ex) {
                }
            }
        }
        if (obj instanceof Date) {
            inDate = (Date) obj;
        }
        System.out.println("Date Handled");
    }

    /**
     * Translates the source date and returns a string in oracle date format
     *      */
    public Object get() throws org.jini.projects.athena.exception.ValidationException {
        if (nulldate) return "NULL";
        Calendar cal = new GregorianCalendar();
        String[] months =
                new String[]{
                    "JAN",
                    "FEB",
                    "MAR",
                    "APR",
                    "MAY",
                    "JUN",
                    "JUL",
                    "AUG",
                    "SEP",
                    "OCT",
                    "NOV",
                    "DEC"};
        cal.setTime(inDate);
        int month = cal.get(Calendar.MONTH);
        //  String month = (cal.get(Calendar.MONTH)+1<10) ? "0" + (cal.get(Calendar.MONTH)+1) : String.valueOf(cal.get(Calendar.MONTH)+1);
        String day =
                (cal.get(Calendar.DAY_OF_MONTH) < 10)
                ? "0" + cal.get(Calendar.DAY_OF_MONTH)
                : String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        System.out.println(
                "'"
                + day
                + "-"
                + month
                + "-"
                + String.valueOf(cal.get(Calendar.YEAR)) + "'");
        return "'"
                + day
                + "-"
                + months[month]
                + "-"
                + String.valueOf(cal.get(Calendar.YEAR))
                + "'";
    }

    public static void main(String[] args) {
        try {
            SQLDate_type caps = new SQLDate_type();
            caps.set(new Date());
            System.out.println("caps.get(): " + caps.get());
        } catch (ClassCastException e) {
            System.out.println("ClassCast");
        } catch (org.jini.projects.athena.exception.ValidationException e) {
            System.out.println("Err: " + e.getMessage());
            e.printStackTrace();
        }

    }

    
}
