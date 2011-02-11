package org.jini.projects.athena.service;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;

/**
 * Class that formats the <code>LogRecord</code> for output to the
 * the standard out and file output.<br>
 */
public class LogFormatter extends Formatter {
    private String lineSep = System.getProperty("line.separator");
    private String format = "{0} [{1,date,dd/MM/yy} {1,time,HH:mm:ss}] ({2}) {3}";

    public String format(java.util.logging.LogRecord logRecord) {
        int startIndex = logRecord.getSourceClassName().lastIndexOf(".") + 1;
        int endIndex = logRecord.getSourceClassName().length();
        String source = logRecord.getSourceClassName().substring(startIndex, endIndex);
        StringBuffer level = new StringBuffer(logRecord.getLevel().getName());
        while ("warning".length() > level.length()) {
            level.append(" ");
        }

        Object[] args = new Object[]{level.toString(),
                                     new Date(logRecord.getMillis()),
                                     source,
                                     logRecord.getMessage()};

        StringBuffer out = new StringBuffer(MessageFormat.format(format, args));

        if (logRecord.getThrown() != null) {
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                logRecord.getThrown().printStackTrace(pw);
                out.append(lineSep);
                out.append("\t");
                out.append(sw.toString());
            } catch (Exception exc) {
                //Ignore
            }
        }

        out.append(lineSep);
        return out.toString();
    }

}
