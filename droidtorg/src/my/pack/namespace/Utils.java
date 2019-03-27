package my.pack.namespace;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

public class Utils {
	private final static String decimalPattern = "#,#00.00";

	// экземпл€ры класса форматировани€ даты
	private final static SimpleDateFormat dateBefore = new SimpleDateFormat();
	private final static SimpleDateFormat dateAfter = new SimpleDateFormat();

	// экземпл€ры класса форматировани€ числа с плав. точкой
	private final static DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
	private final static DecimalFormat df = new DecimalFormat();

	public Utils() {
		// TODO Auto-generated constructor stub
	}

	public static void ShowMsg(Context context, CharSequence msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	public static void ShowMsg(Context context, int res) {
		Toast.makeText(context, res, Toast.LENGTH_SHORT).show();
	}

	public static void ShowLog(String TAG, String msg) {
		Log.d(TAG, msg);
	}

	public static BigDecimal formatNumber(double arg1, int scale) {
		BigDecimal bigd = BigDecimal.valueOf(arg1).setScale(scale,
				RoundingMode.HALF_UP);
		
		return bigd;
	}

	/**
	 * ќписание - форматирует число с плав. точкой
	 * @param d - число типа double
	 * @param sym - дес€тичный знак
	 * @return - возвращает строку
	 */
	public static String formatNumber(double d, String pattern) {
		// otherSymbols.setDecimalSeparator(sym);
		// df.setDecimalFormatSymbols(otherSymbols);
		df.setRoundingMode(RoundingMode.HALF_UP);
		df.applyPattern(pattern);

		/* на fly iq 275 выпадает Exception Syntax error
		 * df.applyLocalizedPattern(pattern); */
		
		return df.format(d);
	}

	public static String formatNumberLocal(double d, int scale) {
		NumberFormat formatter = NumberFormat.getNumberInstance();
		// кол-во знаков после зап€той
		formatter.setMaximumFractionDigits(3);
		// formatter.setMinimumIntegerDigits(6);
		return formatter.format(d);
	}

	private static String setFormatDate(String pattern, int dayOfMonth,
			int monthOfYear, int year) {
		Time dateFormat = new Time();
		dateFormat.set(dayOfMonth, monthOfYear, year);

		long dt = dateFormat.toMillis(true);
		String strFormatDate = (String) DateFormat.format(pattern, dt);
		return strFormatDate;
	}

	/**
	 * ќписание- определ€ет текущую дату
	 */
	protected static String getCurentDate(String pattern) {
		final Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int monthOfYear = cal.get(Calendar.MONTH);
		int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

		String strFormatDate = setFormatDate(pattern, dayOfMonth, monthOfYear,
				year);
		return strFormatDate;
	}

	/**
	 * ќписание - конвертирует дату представленную строкой strDate с маской
	 * patterBefore в дату с маской patterAfter.
	 * @param strDate - строка даты в виде patterBefore
	 * @param patterBefore - маска исходной строки strDate
	 * @param patternAfter - маска конечной строки
	 * @return - возвращает строку даты в виде patterAfter
	 * @throws ParseException
	 */
	public static String convertDate(String strDate, String patterBefore,
			String patternAfter) throws ParseException {
		dateBefore.applyPattern(patterBefore);
		dateAfter.applyPattern(patternAfter);
		String strDateFormat = dateAfter.format(dateBefore.parse(strDate));
		return strDateFormat;
	}
}
