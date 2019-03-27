package my.pack.mask;

import java.text.DecimalFormatSymbols;
import java.util.regex.Pattern;

import android.text.InputFilter;
import android.text.Spanned;

public class MaskEditText implements InputFilter {

	public CharSequence filter(CharSequence source, int start, int end,
			Spanned dest, int dstart, int dend) {
		// TODO Auto-generated method stub

		// String checkedText = dest.toString() + source.toString();

		String checkedText = dest.subSequence(0, dstart).toString()
				+ source.subSequence(start, end)
				+ dest.subSequence(dend, dest.length()).toString();

		String pattern = getPattern();

//		Log.d("Mask", "" + source + " _ " + start + " _ " + end + " _ " + dest
//				+ " _ " + dstart + " _ " + dend + "");

		if (!Pattern.matches(pattern, checkedText)) {
			return "";
		}
		if (dend > 10) {
			return "";
		}
		return null;
	}

	private String getPattern() {
		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		String ds = String.valueOf(dfs.getDecimalSeparator());
		String pattern = "[0-9]+([" + ds + "]{1}||[" + ds + "]{1}[0-9]{1,3})?";
		return pattern;
	}

}
