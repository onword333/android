package my.pack.adapter;

import java.text.DecimalFormat;

import my.pack.namespace.Utils;
import my.pack.namespace.R;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

public class DocTableAdapter extends SimpleCursorAdapter implements ViewBinder {
	public Context docAdapter;
	private final DecimalFormat nf = new DecimalFormat("#,##0.00");

	public DocTableAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		// TODO Auto-generated constructor stub
		setViewBinder(this);
		docAdapter = context;
	}

	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		super.notifyDataSetChanged();
	}

	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
		// TODO Auto-generated method stub

		switch (view.getId()) {
		case R.id.tv4Myitem:
			double price = cursor.getDouble(columnIndex);
			((TextView) view).setText(Utils.formatNumber(price, "#,##0.000"));
			return false;
			// break;
		case R.id.tv5Myitem:
			double count = cursor.getDouble(columnIndex);
			((TextView) view).setText(Utils.formatNumber(count, "#,##0.00"));
			return true;
			// break;

		default:
			break;
		}

		return false;
	}
}
