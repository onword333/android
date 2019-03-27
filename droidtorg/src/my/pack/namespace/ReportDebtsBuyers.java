package my.pack.namespace;

import my.pack.adapter.ReportDebtsBuyersAdapter;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.TextView;

public class ReportDebtsBuyers extends Main {
	private String TAG = this.getClass().getName();
	private Context cx;
	private TextView tvPeriod;
	private EditText etClient;
	private Button btExecute;
	private ListView lv;
	protected db DB;
	protected Cursor cur;
	protected ReportDebtsBuyersAdapter ad;

	private String startDate;
	private String endDate;

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itSearch:
			InputMethodManager imm = (InputMethodManager) getSystemService(cx.INPUT_METHOD_SERVICE);
			imm.showSoftInput(lv, InputMethodManager.SHOW_FORCED);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.counteragent, menu);
		return super.onCreateOptionsMenu(menu);
	}

	private void setStrSql(db DB, String like) {

		String[] columns = new String[7];
		columns[0] = "a." + DbField._id + " AS " + DbField._id;
		columns[1] = "b." + DbField.fName + " AS " + "fName";
		columns[2] = "c." + DbField.fName + " AS " + "fName2";

		columns[3] = "strftime('%Y-%m-%d', " + DbField.fDDoc + ") AS "
				+ DbField.fDDoc;
		/* // объединение колонок columns[3] = DbField.fDDoc + " || " +
		 * DbField.fVDoc + " AS " + DbField.fDDoc; */
		columns[4] = DbField.fVDoc;
		columns[5] = DbField.fPrihod;
		columns[6] = DbField.fRashod;

		String tbl = DbField.tblDebitorka + " a";
		tbl += " LEFT JOIN " + DbField.tblPartner + " b";

		tbl += " ON a." + DbField.fkPartner + " = b." + DbField._id;
		tbl += " LEFT JOIN " + DbField.tblMag + " c";
		tbl += " ON a." + DbField.fkMagazin + " = c." + DbField._id;

		String where = "b." + DbField.fName + " LIKE ?";
		String[] where_arg = new String[] { "%" + like.toUpperCase() + "%" };
		String order_by = DbField.fName + ", " + DbField.fDDoc;
		DB.setField(tbl, columns, where, where_arg, null, null, order_by);
	}

	private void setOnclickButton(Button bt) {
		bt.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.bt_debts_buyers_execute:

					DB.open();
					setStrSql(DB, "");

					cur = DB.doSelect();

					String[] from = { DbField.fName, DbField.fName + "2",
							DbField.fDDoc };
					int[] to = { R.id.tv1, R.id.tv2, R.id.tv3 };

					ad = new ReportDebtsBuyersAdapter(cx,
							R.layout.reports_debts_buers_item, cur, from, to);
					lv.setAdapter(ad);
					DB.close();
					break;

				default:
					break;
				}
			}
		});
	}

	private void initWidget() {
		cx = this.getApplicationContext();
		tvPeriod = (TextView) findViewById(R.id.report_tv_period);
		etClient = (EditText) findViewById(R.id.et_reports_debts_buyers);
		btExecute = (Button) findViewById(R.id.bt_debts_buyers_execute);
		lv = (ListView) findViewById(R.id.lv_reports_debts_buyers);
		setOnclickButton(btExecute);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reports_debts_buers);

		initWidget();

		DB = new db(cx);

		startDate = Utils.getCurentDate("yyyy-MM-dd");
		endDate = startDate;
		tvPeriod.setText("Дата: " + startDate + " по " + endDate + "");
		DB.open();
		setStrSql(DB, " ");

		cur = DB.doSelect();

		String[] from = { DbField.fName, DbField.fName + "2", DbField.fDDoc };
		int[] to = { R.id.tv1, R.id.tv2, R.id.tv3 };

		ad = new ReportDebtsBuyersAdapter(cx,
				R.layout.reports_debts_buers_item, cur, from, to);

		ad.setFilterQueryProvider(new FilterQueryProvider() {
			public Cursor runQuery(CharSequence constraint) {

				if (!constraint.toString().equalsIgnoreCase("")) {
					setStrSql(DB, constraint.toString());
					cur = DB.fetchCountriesByName(constraint.toString());
				}
				// return null;
				return cur;
			}
		});

		lv.setAdapter(ad);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		DB.close();
	}
}
