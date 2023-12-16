package application.tools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtilities {

    private static final SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.FRENCH);

    public static Date getDateFromString(String _dateString) {
        try {
            return inputFormat.parse(_dateString);
        } catch (Exception e) {
            return null;
        }
    }

    public static String transformDateFormat(String _dateFormat) {
        _dateFormat = _dateFormat.replace("JJ", "dd");
        _dateFormat = _dateFormat.replace("AAAA", "yyyy");
        _dateFormat = _dateFormat.replace("hh", "HH'h'");
        return _dateFormat;
    }
}
