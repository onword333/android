package my.pack.adapter;

import java.text.DecimalFormat;

import my.pack.namespace.DbField;
import my.pack.namespace.Utils;
import my.pack.namespace.R;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

public class ReportDebtsBuyersAdapter extends SimpleCursorAdapter implements
		ViewBinder {
	protected Context JourCx;
	private final DecimalFormat nf = new DecimalFormat("#,##0.000");

	public ReportDebtsBuyersAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		setViewBinder(this);
		JourCx = context;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		super.notifyDataSetChanged();
	}

	public boolean setViewValue(View arg0, Cursor arg1, int arg2) {
		String tv3 = "";
		switch (arg0.getId()) {
		case R.id.tv1:

			break;

		case R.id.tv2:

			break;

		case R.id.tv3:
			String typeDoc = arg1.getString(arg1.getColumnIndex(DbField.fVDoc));
			int numDoc = arg1.getInt(arg1.getColumnIndex(DbField._id));
			String dateDoc = arg1.getString(arg1.getColumnIndex(DbField.fDDoc));
			// float sumRashod = arg1.getFloat(arg1
			// .getColumnIndex(DbField.fRashod));
			// float sumPrihod = arg1.getFloat(arg1
			// .getColumnIndex(DbField.fPrihod));
			double sumRashod = Double.parseDouble(arg1.getString(arg1
					.getColumnIndex(DbField.fRashod)));
			double sumPrihod = Double.parseDouble(arg1.getString(arg1
					.getColumnIndex(DbField.fPrihod)));
			double docSum;

			if (sumRashod > 0.00) {
				docSum = sumRashod;
			} else {
				docSum = sumPrihod;
			}

			tv3 = typeDoc + " �" + numDoc + " �� " + dateDoc + " �����: "
					+ Utils.formatNumber(docSum, "#,##0.00") + "";
			((TextView) arg0).setText(tv3);
			Utils.ShowLog(
					"ReportDebits",
					"" + docSum + " --> "
							+ Utils.formatNumber(docSum, "#,##0.00"));
			return true;

		default:
			break;
		}

		return false;
	}

}
