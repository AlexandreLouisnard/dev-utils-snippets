import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DatesUtils {
	public enum MyDayOfTheWeek {
		MONDAY(0x1), TUESDAY(0x2), WEDNESDAY(0x4), THURSDAY(0x8), FRIDAY(0x10), SATURDAY(0x20), SUNDAY(0x40);

		private final byte code;

		MyDayOfTheWeek(int code) {
			this.code = (byte) code;
		}

		public byte getCode() {
			return code;
		}

		public static String getSymbol(int position, boolean value) {
			return value
					? (" " + DayOfWeek.of(position + 1).getDisplayName(TextStyle.NARROW, Locale.getDefault()) + " ")
					: " - ";

		}

	}

	public static Calendar timestampToCalendar(long timestamp) {
		Date d = new Date(timestamp);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		return calendar;
	}
}