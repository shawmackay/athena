/*
 * Created by IntelliJ IDEA.
 * User: calum
 * Date: 25-Jun-02
 * Time: 11:48:46
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.jini.projects.athena.command.types.transforms;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.jini.projects.athena.command.types.Transformer;

/**
 * Translation type for mapping a Java date to
 * MainFrame date format. Only uses the year, month and day portion of the date.
 */
public class MFrameDate_type implements Transformer {
    java.util.Date inDate;

    public MFrameDate_type(java.util.Date inDate) {
        this.inDate = inDate;
    }

    public MFrameDate_type() {
    }

    /**
     * Sets the source date.
     * @throws java.lang.ClassCastException in the event of wrong data types being passed
     */
    public void set(Object obj) throws ClassCastException {
        inDate = (Date) obj;
    }

    /**
     * Translates the source date and returns a capsil format date in it's place
     * i.e CYYMMDD
     *      */
    public Object get() throws org.jini.projects.athena.exception.ValidationException {
        Calendar cal = new GregorianCalendar();
        cal.setTime(inDate);
        String month = (cal.get(Calendar.MONTH) + 1 < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : String.valueOf(cal.get(Calendar.MONTH) + 1);
        String day = (cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        return String.valueOf(cal.get(Calendar.YEAR) - 1800) +
                month +
                day;
    }

    

   
}
