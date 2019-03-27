package my.pack.adapter;

import java.text.DecimalFormat;

import my.pack.namespace.Utils;
import my.pack.namespace.R;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

public class TovarAdapter extends SimpleCursorAdapter implements ViewBinder {
	public Context docAdapter;
	private final DecimalFormat nf = new DecimalFormat("#,##0.000");
	private int mItemIndex = -1;
	private Resources res;
	private Drawable selector;

	public TovarAdapter(Context context, int layout, Cursor c, String[] from,
			int[] to) {
		super(context, layout, c, from, to);
		// TODO Auto-generated constructor stub
		setViewBinder(this);
		docAdapter = context;
		res = docAdapter.getResources();

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
			return true;
			// break;
		case R.id.tv5Myitem:
			double count = cursor.getDouble(columnIndex);
			((TextView) view).setText(Utils.formatNumber(count, "#,##0.000"));
			return true;
			// break;

		default:
			break;
		}
		// Utils.ShowLog(this.getClass().getName(), "---- 1");
		return false;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// http://stackoverflow.com/questions/5972155/does-anyone-know-how-to-highlight-a-selected-item-in-a-android-listview
		View view = super.getView(position, convertView, parent);

		if (convertView != null) {

			if (position == mItemIndex) {

				// convertView.setSelected(true);
				// convertView.setPressed(true);

				// convertView.setFocusableInTouchMode(true);
				// convertView.setFocusableInTouchMode(true);
				// convertView.setBackgroundResource(R.drawable.test_list_item_selector);
				// convertView.setBackgroundColor(Color.parseColor("#FF9912"));

				// convertView.setBackgroundDrawable(res.getDrawable(R.drawable.test_list_item_selector));

				convertView.setBackgroundDrawable(res
						.getDrawable(R.drawable.post_cell_selected));
			} else {
				// convertView.setSelected(false);
				// convertView.setPressed(false);
				// convertView.setFocusableInTouchMode(false);
				// convertView.setSelected(true);
				// convertView.setPressed(true);
				// convertView.setBackgroundColor(Color.TRANSPARENT);
				// convertView.setSelected(false);
				// convertView.setPressed(false);
				// convertView.setBackgroundDrawable(res.getDrawable(R.drawable.post_cell_normal));
				// convertView.setSelected(true);
				// convertView.setPressed(true);
				convertView.setBackgroundDrawable(res
						.getDrawable(R.drawable.post_cell_normal));
			}

		} else {

		}
		return view;

		// return super.getView(position, convertView, parent);
	}

	public void setSelectItem(int index) {
		mItemIndex = index;
	}

	public int getSelectItem() {
		return mItemIndex;
	}

}
