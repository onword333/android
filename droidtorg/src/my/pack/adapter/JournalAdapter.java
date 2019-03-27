package my.pack.adapter;

import java.text.DecimalFormat;

import my.pack.namespace.Utils;
import my.pack.namespace.R;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

public class JournalAdapter extends SimpleCursorAdapter implements ViewBinder {
	protected Context JourCx;
	private final DecimalFormat nf = new DecimalFormat("#,##0.000");

	public JournalAdapter(Context context, int layout, Cursor c, String[] from,
			int[] to) {
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
		// TODO Auto-generated method stub
		ImageView img = (ImageView) ((LinearLayout)arg0.getParent().getParent()).findViewById(R.id.imageView1);

		switch (arg0.getId()) {
		case R.id.tv1:
			int ind = arg1.getColumnIndex("fOut");
			if (arg1.getString(ind).equalsIgnoreCase("X")) {
				/* ((TextView)
				 * arg0).setTextColor(JourCx.getResources().getColor(
				 * R.color.white)); */
				// ((LinearLayout) arg0.getParent().getParent())
				// .setBackgroundColor(JourCx.getResources().getColor(
				// R.color.test));
				
				img.setImageDrawable(JourCx.getResources().getDrawable(
						R.drawable.doc_send_7));
			} else {
				/* ((TextView)
				 * arg0).setTextColor(JourCx.getResources().getColor(
				 * R.color.Chocolate1)); */

				// ((LinearLayout) arg0.getParent().getParent())
				// .setBackgroundColor(JourCx.getResources().getColor(
				// R.color.grey612));

				img.setImageDrawable(JourCx.getResources().getDrawable(
						R.drawable.doc_no_send_7));

				// ((LinearLayout) arg0.getParent()).setBackgroundColor(JourCx
				// .getResources().getColor(R.color.grey51));
			}
			// Log.d("ADAPTER", "" + ((TextView) arg0).getText() + "  out");
			// ((TextView) arg0).setText(arg1.getString(arg2));
			// return true;
			break;

		case R.id.tv7:
			double sum = arg1.getDouble(arg2);

			// Utils.ShowLog("JourAdapter", sum + "");
			// ((TextView) arg0).setText(" - fdsf");
			((TextView) arg0).setText(Utils.formatNumber(sum, "#,##0.00"));
			return true;
			// break;

		case R.id.tv5:

			// Log.d("ADAPTER", "" + ((TextView) arg0).getText() + "  out");
			// ((TextView) arg0).setText(arg1.getString(arg2));
			// return true;
			break;

		default:
			break;
		}
		// Log.d("ADAPTER", "" + ((TextView) arg0).getText() + "  out");
		return false;
	}

}
