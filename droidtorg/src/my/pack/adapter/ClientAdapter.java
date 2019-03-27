package my.pack.adapter;




import java.text.DecimalFormat;

import my.pack.namespace.R;
import my.pack.namespace.Utils;

//import android.R;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
//import android.R;

public class ClientAdapter extends SimpleCursorAdapter implements ViewBinder {
	public Context contextMyAdapter;
	private final DecimalFormat nf = new DecimalFormat("#,##0.00");

	public ClientAdapter(Context context, int layout, Cursor c, String[] from,
			int[] to) {
		super(context, layout, c, from, to);
		// TODO Auto-generated constructor stub

		setViewBinder(this);
		contextMyAdapter = context;

	}

	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
		// TODO Auto-generated method stub

		double sum = cursor.getDouble(columnIndex);
		((TextView) view).setText(Utils.formatNumber(sum, "#,##0.00"));
		switch (view.getId()) {
		case R.id.tvCounterAgent_sumOur:
			

			return true;

		case R.id.tvCounterAgent_sumThem:

			return true;
		}
		return false;

		// switch (view.getId()) {
		//
		//
		// case R.id.tvCounterAgent_sumThem:
		// ((TextView) view).setText(nf.format(sum));
		//
		// // double sum = cursor.getDouble(columnIndex);
		// // double s = 0;
		// // if (sum >= s) {
		// // ((TextView) view).setTextColor(contextMyAdapter.getResources()
		// // .getColor(R.color.green));
		// // } else {
		// // ((TextView) view).setTextColor(contextMyAdapter.getResources()
		// // .getColor(R.color.red));
		// }
		// // double x = new BigDecimal(d).setScale(2,
		// // RoundingMode.HALF_UP).doubleValue();
		// // ((TextView) view).setText(String.format("%17.3f", d));
		//
		// //((TextView) view).setText(nf.format(sum));
		//
		// return true;
		// }
		// }
		// return false;
	}

}
